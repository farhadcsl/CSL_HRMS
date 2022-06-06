package org.ofbiz.salaryprocess;

import javolution.util.FastList;
import javolution.util.FastSet;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilValidate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.*;

public class SalaryUtils {
 public static String getFiscalYear(Delegator delegator, String partyIdFrom) {
        List condition1 = FastList.newInstance();
        condition1.add(EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "N"));
        condition1.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, partyIdFrom));
        EntityCondition cond1 = EntityCondition.makeCondition(condition1, EntityOperator.AND);

        List<GenericValue> data1 = null;
        try {
            data1 = delegator.findList("CustomTimePeriod", cond1, null, null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        GenericValue fiscalYear = null;
        if (data1.size() != 0) {
            fiscalYear = data1.get(0);
            Date date = (Date) fiscalYear.get("fromDate");
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Dhaka"));
            int year = calendar.get(Calendar.YEAR);

            return String.valueOf(year);
        }

        return "error";
    }
 
 public static String getCompanyName(Delegator delegator, String organizationId) {
     GenericValue organization = null;
     try {
         organization = delegator.findByPrimaryKey("PartyAndGroup", UtilMisc.toMap("partyId", organizationId));
     } catch (GenericEntityException e) {
         e.printStackTrace();
     }
     return organization.get("groupName").toString();
 }
 public static boolean checkPendingSalaryProcessExist(Delegator delegator, String organizationId, String month, String year) {

     List condition1 = FastList.newInstance();
     condition1.add(EntityCondition.makeCondition("companyId", EntityOperator.EQUALS, organizationId));
     condition1.add(EntityCondition.makeCondition("monthNumber", EntityOperator.EQUALS, month));
     condition1.add(EntityCondition.makeCondition("fiscalYear", EntityOperator.EQUALS, year));
     EntityCondition cond1 = EntityCondition.makeCondition(condition1, EntityOperator.AND);

     List<GenericValue> salaryStatus = null;
     try {
         salaryStatus = delegator.findList("MonthlyTotalSalary", cond1, null, null, null, false);
     } catch (GenericEntityException e) {
         e.printStackTrace();
     }
     if (UtilValidate.isEmpty(salaryStatus)) {
         return false;
     } else {
         return true;
     }
 }

 
 public static String getMonthName(String monthNumber) {
     String monthName = "";
     if (monthNumber.equals("0")) monthName = "January";
     if (monthNumber.equals("1")) monthName = "February";
     if (monthNumber.equals("2")) monthName = "March";
     if (monthNumber.equals("3")) monthName = "April";
     if (monthNumber.equals("4")) monthName = "May";
     if (monthNumber.equals("5")) monthName = "June";
     if (monthNumber.equals("6")) monthName = "July";
     if (monthNumber.equals("7")) monthName = "August";
     if (monthNumber.equals("8")) monthName = "September";
     if (monthNumber.equals("9")) monthName = "October";
     if (monthNumber.equals("10")) monthName = "November";
     if (monthNumber.equals("11")) monthName = "December";

     return monthName;
 }
 
 public static Calendar getPayrollMonth(String processingMonth) {
     Calendar payrollMonth = Calendar.getInstance();
     payrollMonth.set(Calendar.MONTH, Integer.parseInt(processingMonth));
     payrollMonth.set(Calendar.DAY_OF_MONTH, 1);
     payrollMonth.set(Calendar.HOUR_OF_DAY, 1);
     return payrollMonth;
 }
 
 
 public static boolean isPayStructureAvailable(Delegator delegator, String employeeId) {
     List<GenericValue> salarys = null;
     try {
         salarys = delegator.findByAnd("EmplPresentSalary", UtilMisc.toMap("partyId", employeeId));
     } catch (GenericEntityException e) {
         e.printStackTrace();
     }
     return (salarys.isEmpty()) ? false : true;
 }
 
 public static List<GenericValue> getTotalActiveEmployee(Delegator delegator,List<GenericValue> totalActiveEmployee,String partyId,String employmentType){
		List<GenericValue> children=getChildren(delegator, partyId);
		
		List<GenericValue> positions=getPositionsByParyId(delegator, partyId);
		
		for(GenericValue child:children){

			totalActiveEmployee=getTotalActiveEmployee(delegator,totalActiveEmployee,child.getString("partyIdTo"),employmentType);
		}
		
		if(positions!=null){
			for(GenericValue position:positions){
				totalActiveEmployee.addAll(getTotalActiveEmployeeByPositionId(delegator, position.getString("emplPositionId"),employmentType));
			}
		}
		return totalActiveEmployee;
		
	}
 
 public static 	List<GenericValue> getChildren(Delegator delegator, String partyId){
		
		List<GenericValue> children=FastList.newInstance();
		
		Set<String> fieldsToSelect = FastSet.newInstance();
		fieldsToSelect.add("partyIdTo");
		   	
		try {
				List<EntityExpr> exprs = FastList.newInstance();
		        exprs.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
		        exprs.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP"));
		        children = delegator.findList("PartyRelationship", EntityCondition.makeCondition(exprs, EntityOperator.AND), fieldsToSelect, null, null, false);
		   
		
			}catch (GenericEntityException e) {
				return children;
			}
		
		return children;
	}
 
 public static 	List<GenericValue> getPositionsByParyId(Delegator delegator, String partyId){
		
		List<GenericValue> positions=FastList.newInstance();
		
		try {
			positions= delegator.findByAnd("EmplPositionAndEmplPositionType", UtilMisc.toMap("partyId", partyId, "statusId", "EMPL_POS_ACTIVE"));
		}catch (GenericEntityException e) {
			return positions;
		}
		return positions;
	}
 
 
 public static 	List<GenericValue> getTotalActiveEmployeeByPositionId(Delegator delegator, String positionId,String employmentType){
		List<GenericValue> emplPositionFulfillmentList=FastList.newInstance();

		try {
			List<EntityExpr> exprs = FastList.newInstance();
	        exprs.add(EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS, positionId));
	        exprs.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "EMPLOYEE"));
	        exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "EMPL_POS_ACTIVE"));
	        exprs.add(EntityCondition.makeCondition("partyTypeId", EntityOperator.EQUALS, "PERSON"));
	        
			 emplPositionFulfillmentList = delegator.findList("PartyAndPersonAndFulfillment", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
			
			 for(GenericValue emplPositionFulfillment:emplPositionFulfillmentList){
				 String partyId=emplPositionFulfillment.getString("partyId");
				 if(UtilValidate.isNotEmpty(partyId)){
					 GenericValue employment=EntityUtil.getFirst(delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo", partyId)));
					 if(UtilValidate.isNotEmpty(employment)){
							String	employmentTypeCheck= employment.getString("employmentType");
							
							if(employmentTypeCheck!=null){
								
								if(employmentType.equalsIgnoreCase("Manager")){
									String	isManager= employment.getString("isManager");
									if(isManager==null||!isManager.equalsIgnoreCase("Y")){
										emplPositionFulfillmentList.remove(emplPositionFulfillment);	
									}
								}else if(employmentType.equalsIgnoreCase("FinAccount")){
									GenericValue finAccount=EntityUtil.getFirst(delegator.findByAnd("FinAccount", UtilMisc.toMap("ownerPartyId", partyId)));
									if(UtilValidate.isEmpty(finAccount)){
										emplPositionFulfillmentList.remove(emplPositionFulfillment);	
									}
								}
								else if(!employmentTypeCheck.equalsIgnoreCase(employmentType)){
									emplPositionFulfillmentList.remove(emplPositionFulfillment);	
								}
								
							}else{
								emplPositionFulfillmentList.remove(emplPositionFulfillment);	
							}
								
					 }
				 }
			 }
			 
			 return emplPositionFulfillmentList;

		} catch (GenericEntityException e) {
			return emplPositionFulfillmentList;
		}
	}
 
 
}