package org.ofbiz.salaryprocess;

import javolution.util.FastMap;
import javolution.util.FastList;

import org.apache.log4j.Logger;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;

public class NewSalaryServices {
    public static String module = NewSalaryServices.class.getName();
    private static Logger logger = Logger.getLogger("NewSalaryServices");

    public static boolean isAnEmployee() {
        return false;
    }
    
    public static boolean checkViewExist(Delegator delegator, String organizationId, String month, String year) {

        List condition1 = FastList.newInstance();
        condition1.add(EntityCondition.makeCondition("companyId", EntityOperator.EQUALS, organizationId));
        condition1.add(EntityCondition.makeCondition("monthNumber", EntityOperator.EQUALS, month));
        condition1.add(EntityCondition.makeCondition("fiscalYear", EntityOperator.EQUALS, year));
        EntityCondition cond1 = EntityCondition.makeCondition(condition1, EntityOperator.AND);

        List<GenericValue> salaryStatus = null;
        try {
            salaryStatus = delegator.findList("EligibleEmployee", cond1, null, null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        if (UtilValidate.isEmpty(salaryStatus)) {
            return false;
        } else {
            return true;
        }
    }
    
    /*
     * This Help to insert data on EligibleEmployee:MonthlyTotalSalary:EmplTempSalaryOpt
     * 
     * 
     */
    public static List<Map<String, String>> getElligiblePayeeListMap(Delegator delegator, String organizationId, String processingMonth, String year, String bonusCheck, String arrearCheck) {
    	
        Date monthStartDate = null;
        EntityFindOptions opts = new EntityFindOptions();
//opts.setFetchSize(100);
      List<String> orderBy = UtilMisc.toList("partyIdFrom");//?
      monthStartDate = SalaryUtils.getPayrollMonth(processingMonth).getTime();
      EntityExpr employementTableCondition = EntityCondition.makeCondition(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, new Timestamp(monthStartDate.getTime()))), EntityOperator.AND, EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, organizationId));

      EntityConditionList<EntityExpr> conditionList = EntityCondition.makeCondition(UtilMisc.toList(employementTableCondition));

      List<GenericValue> eligiblePayeeList = null;
      List<Map<String, String>> eligiblePayeeListMap = new ArrayList<Map<String, String>>();
      /*
       * 
       * 
       * find the eligible employee
       * 
       * 
       * */
      try {
          eligiblePayeeList = delegator.findList("EmploymentAndPerson", conditionList, null, orderBy, opts, true);
          for (GenericValue value : eligiblePayeeList) {
              if (!SalaryUtils.isPayStructureAvailable(delegator, value.get("partyId").toString())) {
                  eligiblePayeeList.remove(value);
              }
          }
      } catch (GenericEntityException e) {
          e.printStackTrace();
      }
      int listSize =eligiblePayeeList.size();
      String totalEmpl = Integer.toString(listSize);
      /*
       * 
       * insert data into monthly total salary table except total amount ---this required to insert data EligibleEmployee
       * 
       * */
      String processIdgen = delegator.getNextSeqId("MonthlyTotalSalary");
      Map<String, Object> totalsalaryForEachMonth = FastMap.newInstance();
      totalsalaryForEachMonth.put("processId", processIdgen);
      totalsalaryForEachMonth.put("companyId", organizationId);
      totalsalaryForEachMonth.put("monthNumber", processingMonth);
      totalsalaryForEachMonth.put("fiscalYear", year);   
      try {
   		GenericValue totalsalaryopt = delegator
   				.makeValue("MonthlyTotalSalary",
   						UtilMisc.toMap(totalsalaryForEachMonth));
   		totalsalaryopt.create();
       } catch (Exception e) {

       	e.printStackTrace();
       }
      Map<String, Object> contessaSalaryProcessEachMonth = FastMap.newInstance();
      contessaSalaryProcessEachMonth.put("processId", processIdgen);
      contessaSalaryProcessEachMonth.put("companyId", organizationId);
      contessaSalaryProcessEachMonth.put("monthNumber", processingMonth);
      contessaSalaryProcessEachMonth.put("fiscalYear", year);
      contessaSalaryProcessEachMonth.put("isBonus", bonusCheck);
      contessaSalaryProcessEachMonth.put("isArrear", arrearCheck);
      try {
     		GenericValue contessaProsalaryopt = delegator
     				.makeValue("ContessaSalaryPro",
     						UtilMisc.toMap(contessaSalaryProcessEachMonth));
     		contessaProsalaryopt.create();
         } catch (Exception e) {

         	e.printStackTrace();
         }
      /*==================== End The Process======================*/
      double totalNetPay = 0;
      for (GenericValue value : eligiblePayeeList) {
          Map<String, String> row = new HashMap<String, String>();
          //get party id from value
          String employeeId = value.getString("partyId").toString();
          String orgId = value.getString("partyIdFrom").toString();
          
          List<GenericValue> benefitsAndded = null;
          try {
              benefitsAndded = delegator.findByAnd("EmplPresentSalary", UtilMisc.toMap("partyId", employeeId));
          } catch (GenericEntityException e) {
              e.printStackTrace();
          }
          
          Timestamp stamp = new Timestamp(System.currentTimeMillis());
          double emplSalaryTotal = 0;
          double emplDeductionTotal = 0;
          double emplNetPay = 0;
          
          
          for (GenericValue benefit : benefitsAndded) {
        	  Map<String, Object> individualPayrollBenDedItem = FastMap.newInstance();
              String benefitType = benefit.get("calculationType").toString();
              

                  
                  if (benefitType.equalsIgnoreCase("B")){
                	  emplSalaryTotal += Double.parseDouble(benefit.get("amount").toString());
                  }else{
                	  emplDeductionTotal += Double.parseDouble(benefit.get("amount").toString()); 
                  }
                  individualPayrollBenDedItem.put("partyId", employeeId);
                  individualPayrollBenDedItem.put("payrollItemTypeId", benefit.get("payrollItemTypeId").toString());
                  individualPayrollBenDedItem.put("calculationType", benefitType);
                  //individualPayrollBenDedItem.put("calculationMode", benefit.get("calculationMode").toString());
      	        	//individualPayrollBenDedItem.put("optValue", benefit.get("optValue").toString());
                  
                  individualPayrollBenDedItem.put("amount", benefit.get("amount"));
                  individualPayrollBenDedItem.put("fromDate", stamp);
                  individualPayrollBenDedItem.put("processId", processIdgen);
                  individualPayrollBenDedItem.put("monthNumber", processingMonth);
                  individualPayrollBenDedItem.put("fiscalYear", year);
             /*Insert Data on EmplTempSalaryOpt Salary Table
              * 
              * 
              * */
              try {
          		GenericValue emplTemsalaryopt = delegator
          				.makeValue("EmplTempSalaryOpt",
          						UtilMisc.toMap(individualPayrollBenDedItem));
          		emplTemsalaryopt.create();
              } catch (Exception e) {

              	e.printStackTrace();
              }
                  
             
          }
         /* Start bonus process*/
          if (bonusCheck.equalsIgnoreCase("Y")){
        	  String bonusAmount = "";
        	  List<GenericValue> bonusAndded = null;
              try {
            	  bonusAndded = delegator.findByAnd("PartyBonusStatus", UtilMisc.toMap("partyId", employeeId, "isActive","Y", "bonusMonth", processingMonth,"bonusYear", year));
              } catch (GenericEntityException e) {
                  e.printStackTrace();
              }
          if  (!bonusAndded.isEmpty()){ 
              String calculationType = null;
  	        if (UtilValidate.isNotEmpty(bonusAndded)) {
  	            if (UtilValidate.isNotEmpty(bonusAndded.get(0).get("amount"))) {
  	            	bonusAmount = bonusAndded.get(0).get("amount").toString();
  	            }
  	        }
  	        
  	      String bonusId = null;
	        if (UtilValidate.isNotEmpty(bonusAndded)) {
	            if (UtilValidate.isNotEmpty(bonusAndded.get(0).get("bonusId"))) {
	            	bonusId = bonusAndded.get(0).get("bonusId").toString();
	            }
	        }
	        Timestamp dateAdded =(Timestamp) bonusAndded.get(0).get("dateAdded");
	        
  	      emplSalaryTotal += Double.parseDouble(bonusAmount);
  	      Map<String, Object> individualPayrollBonusItem = FastMap.newInstance();
  	    individualPayrollBonusItem.put("partyId", employeeId);
  	    individualPayrollBonusItem.put("calculationType", "B");
  	    individualPayrollBonusItem.put("amount",  new BigDecimal(bonusAmount));
  	    individualPayrollBonusItem.put("fromDate", stamp);
  	    individualPayrollBonusItem.put("processId", processIdgen);
    	individualPayrollBonusItem.put("monthNumber", processingMonth);
    	individualPayrollBonusItem.put("fiscalYear", year);
    	List<GenericValue> bonusAnddedId = null;
        try {
        	bonusAnddedId = delegator.findByAnd("BonusType", UtilMisc.toMap("bonusId", bonusId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String bonusPayrollItem = null;
        if (UtilValidate.isNotEmpty(bonusAnddedId)) {
            if (UtilValidate.isNotEmpty(bonusAnddedId.get(0).get("payrollItemTypeId"))) {
            	bonusPayrollItem = bonusAnddedId.get(0).get("payrollItemTypeId").toString();
            }
        }
        individualPayrollBonusItem.put("payrollItemTypeId", bonusPayrollItem);
        
        
        try {
      		GenericValue emplTemsalaryoptforBonus = delegator
      				.makeValue("EmplTempSalaryOpt",
      						UtilMisc.toMap(individualPayrollBonusItem));
      		emplTemsalaryoptforBonus.create();
          } catch (Exception e) {

          	e.printStackTrace();
          }
        
        
        GenericValue updateEemplBonus = null; 
        try {
        	updateEemplBonus = delegator.findByPrimaryKey("PartyBonusStatus", UtilMisc.toMap("bonusId", bonusId,"partyId", employeeId, "dateAdded", dateAdded));
        	updateEemplBonus.set("isActive", "N");
        	updateEemplBonus.set("isProcess", "Y");
        	updateEemplBonus.set("isPayroll", "Y");
        	updateEemplBonus.store();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
          }
  	
          }
        /*end bonus Process */ 
          
          /*Employee Arrear Total*/
          if (arrearCheck.equalsIgnoreCase("Y")){
        	  
        	  List<GenericValue> arrearAndded = null;
              try {
            	  arrearAndded = delegator.findByAnd("ArrearFinal", UtilMisc.toMap("partyId", employeeId, "isActive","Y", "monthNumber", processingMonth));
              } catch (GenericEntityException e) {
                  e.printStackTrace();
              }
             String arrearSal="0";
             String arrearDed = "0";
             String arrearId = "";
              if  (!arrearAndded.isEmpty()){ 
                 if (UtilValidate.isNotEmpty(arrearAndded)) {
      	            if (UtilValidate.isNotEmpty(arrearAndded.get(0).get("arrearBenefit"))) {
      	            	arrearSal = arrearAndded.get(0).get("arrearBenefit").toString();
      	            }
      	        }
                 if (UtilValidate.isNotEmpty(arrearAndded)) {
       	            if (UtilValidate.isNotEmpty(arrearAndded.get(0).get("arrearDeduct"))) {
       	            	arrearDed = arrearAndded.get(0).get("arrearDeduct").toString();
       	            }
       	        }
                 if (UtilValidate.isNotEmpty(arrearAndded)) {
       	            if (UtilValidate.isNotEmpty(arrearAndded.get(0).get("arrearId"))) {
       	            	arrearId = arrearAndded.get(0).get("arrearId").toString();
       	            }
       	        }
                 emplSalaryTotal += Double.parseDouble(arrearSal); 
                 emplDeductionTotal += Double.parseDouble(arrearDed);
                 Map<String, Object> individualPayrollArrearSalary = FastMap.newInstance();
                 individualPayrollArrearSalary.put("partyId", employeeId);
                 individualPayrollArrearSalary.put("calculationType", "B");
                 individualPayrollArrearSalary.put("amount",  new BigDecimal(arrearSal));
                 individualPayrollArrearSalary.put("fromDate", stamp);
                 individualPayrollArrearSalary.put("processId", processIdgen);
                 individualPayrollArrearSalary.put("monthNumber", processingMonth);
                 individualPayrollArrearSalary.put("fiscalYear", year);
                 individualPayrollArrearSalary.put("payrollItemTypeId", "ARREAR");
                 // Add arrear Benefits amount to TempSalary
                 try {
               		GenericValue emplTemsalaryoptforArrearSal = delegator
               				.makeValue("EmplTempSalaryOpt",
               						UtilMisc.toMap(individualPayrollArrearSalary));
               		emplTemsalaryoptforArrearSal.create();
                   } catch (Exception e) {

                   	e.printStackTrace();
                   }
                 Map<String, Object> individualPayrollArrearDD = FastMap.newInstance();
                 individualPayrollArrearDD.put("partyId", employeeId);
                 individualPayrollArrearDD.put("calculationType", "D");
                 individualPayrollArrearDD.put("amount",  new BigDecimal(arrearDed));
                 individualPayrollArrearDD.put("fromDate", stamp);
                 individualPayrollArrearDD.put("processId", processIdgen);
                 individualPayrollArrearDD.put("monthNumber", processingMonth);
                 individualPayrollArrearDD.put("fiscalYear", year);
                 individualPayrollArrearDD.put("payrollItemTypeId", "ARREAR_DD");
              // Add arrear Benefits amount to TempSalary
                 try {
               		GenericValue emplTemsalaryoptforArreardd = delegator
               				.makeValue("EmplTempSalaryOpt",
               						UtilMisc.toMap(individualPayrollArrearDD));
               		emplTemsalaryoptforArreardd.create();
                   } catch (Exception e) {

                   	e.printStackTrace();
                   }
                 /*Update Arrear List*/
                 GenericValue updateEemplArrear = null; 
                 try {
                 	updateEemplArrear = delegator.findByPrimaryKey("ArrearFinal", UtilMisc.toMap("arrearId", arrearId,"partyId", employeeId));
                 	updateEemplArrear.set("isActive", "N");
                 	
                 	updateEemplArrear.store();
                 } catch (GenericEntityException e) {
                     e.printStackTrace();
                 }
              }
          }
          /*Arrear flow End*/
          emplNetPay = emplSalaryTotal - emplDeductionTotal;
          
         totalNetPay += emplNetPay;
         
         String eligibleEmplId = delegator.getNextSeqId("EligibleEmployee");
         BigDecimal c = new BigDecimal(emplNetPay);
         Map<String, Object> individualTotalsalaryForEachEmployee = FastMap.newInstance();
         individualTotalsalaryForEachEmployee.put("processId", processIdgen);
         individualTotalsalaryForEachEmployee.put("eligibleEmployeeId", eligibleEmplId);
         individualTotalsalaryForEachEmployee.put("partyId", employeeId);
         individualTotalsalaryForEachEmployee.put("companyId", orgId);
         individualTotalsalaryForEachEmployee.put("totalSalary", c);
         individualTotalsalaryForEachEmployee.put("monthNumber", processingMonth);
         individualTotalsalaryForEachEmployee.put("fiscalYear", year);
         individualTotalsalaryForEachEmployee.put("isSalary", "Y");
         individualTotalsalaryForEachEmployee.put("isActive", "Y");
         /*Insert Data on EligibleEmployee Salary Table
          * 
          * 
          * */
         try {
       		GenericValue empleligiblesalaryopt = delegator
       				.makeValue("EligibleEmployee",
       						UtilMisc.toMap(individualTotalsalaryForEachEmployee));
       		empleligiblesalaryopt.create();
           } catch (Exception e) {

           	e.printStackTrace();
           }
         emplSalaryTotal = 0;
         emplDeductionTotal = 0;
         emplNetPay =0;
      }
      GenericValue monthlySalary = null; 
      BigDecimal b = new BigDecimal(totalNetPay);
     
      totalsalaryForEachMonth.put("totalSalary", b); 
      /*
       * 
       * Update Data on MonthlyTotalSalary Salary Table by processId
       * */
      try{
    	  monthlySalary = delegator.findByPrimaryKey("MonthlyTotalSalary", UtilMisc.toMap("processId", processIdgen));
    	  
    	  monthlySalary.set("totalSalary", b);
    	  monthlySalary.set("totalEmployee", totalEmpl);
    	  monthlySalary.store();
      }catch (Exception e) {

       	e.printStackTrace();
       }
      
      Map<String, String> row = new HashMap<String, String>();
      row.put("totalNetPay", String.valueOf(totalNetPay));
      row.put("totalNetPay", totalEmpl);
      row.put("processId", processIdgen);
      eligiblePayeeListMap.add(row);
      
      
      
      
        return eligiblePayeeListMap;
    }
    
    
}