package org.ofbiz.taxCalculation;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.fop.layoutmgr.PaddingElement;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;

import java.util.Calendar;

public class MonthlyTaxCalculate {

	public static String setAllPartyMonthlyTaxableAmount(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<EntityExpr> exprsForEmployeeList = FastList.newInstance();
		exprsForEmployeeList.add(EntityCondition.makeCondition("isActive", EntityOperator.EQUALS,"Y"));        
		
		List<GenericValue> payrollEmployeeList = null;
		try {
			payrollEmployeeList = delegator.findList("PersonPayrollInfo", EntityCondition.makeCondition(exprsForEmployeeList, EntityOperator.AND), null, null, null, false);

			for (GenericValue rowData : payrollEmployeeList){
				String partyId = rowData.get("partyId").toString();
				String employeeId = rowData.get("employeeId").toString();
				String payGradeId = rowData.get("payGradeId").toString();
				String salaryLevelId = rowData.get("salaryLevelId").toString();
				String salaryTemplateId = rowData.get("salaryTemplateId").toString();
				String basicSalaryStepId = rowData.get("basicSalaryStepId").toString();
				String taxPayerTypeId = rowData.get("taxPayerTypeId").toString();
				String comments = rowData.get("comments").toString();
			    try {
			    	setPartyMonthlyTaxableAmount(delegator,partyId,employeeId,payGradeId,salaryLevelId,salaryTemplateId,basicSalaryStepId,taxPayerTypeId,comments,"Y");
			    }catch (Exception e) { 
		            request.setAttribute("_ERROR_MESSAGE_", "Error occer for the employee "+employeeId);
		            return "Error";
			    }
			}
		}
		catch(Exception e){}
		return "Success";
	}
	public static String setIndivisualPartyMonthlyTaxableAmount(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
		String partyId = request.getParameter("partyId");
		String employeeId = request.getParameter("employeeId");
		String payGradeId = request.getParameter("payGradeId");
		String salaryLevelId = request.getParameter("salaryLevelId");
		String salaryTemplateId = request.getParameter("salaryTemplateId").toString();
		String basicSalaryStepId = request.getParameter("basicSalaryStepId").toString();
		String taxPayerTypeId = request.getParameter("taxPayerTypeId");
		String comments = request.getParameter("comments");
		
	    try {
	    	setPartyMonthlyTaxableAmount(delegator,partyId,employeeId,payGradeId,salaryLevelId,salaryTemplateId,basicSalaryStepId,taxPayerTypeId,comments,"Y");
	    }catch (Exception e) { 
            request.setAttribute("_ERROR_MESSAGE_", "Error!!!!!!");
            return "Error";            
	    }
        request.setAttribute("_EVENT_MESSAGE_", "Tax Payer Successfully Configure");
        return "Success";
    }

	public static int getPandingMonth () {
		int pandingMonth=1;
	    Calendar now = Calendar.getInstance();
		pandingMonth=now.get(Calendar.MONTH) + 1;
		if(pandingMonth>6){
			pandingMonth=18-pandingMonth;
		}
		else{
			pandingMonth=6-pandingMonth;
		}
		
		return pandingMonth;
	}
	public static BigDecimal FindPartyBasicSalary (Delegator delegator,String partyId) {
		//Delegator delegator = (Delegator) request.getAttribute("delegator");
		
		BigDecimal epsAmount=null;
		BigDecimal levelAmount=null;
		try { 
		    List<EntityExpr> exprsForBasicSalary = FastList.newInstance();
		    exprsForBasicSalary.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)); 
		    exprsForBasicSalary.add(EntityCondition.makeCondition("payrollItemTypeId", EntityOperator.EQUALS, "BASIC"));
			List<GenericValue> basicSalarylList = null;
			try {
				basicSalarylList = delegator.findList("EmplPresentSalary", EntityCondition.makeCondition(exprsForBasicSalary, EntityOperator.AND), null, null, null, false);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			levelAmount= (BigDecimal)basicSalarylList.get(0).get("amount");
			String optValueforBasic = levelAmount.toString();
			return levelAmount;
		}
		catch (Exception e) {
            return levelAmount;
		}
	}
	
	public static BigDecimal FindYearlyTotalTaxableIncome (Delegator delegator,String partyId,int pandingMonth) {
        int totalMonthInYear=12;
        BigDecimal totalTaxableIncome=new BigDecimal(0);
		BigDecimal partyBasicSalary=FindPartyBasicSalary(delegator,partyId);
		
		List<EntityExpr> exprsForPartySalary = FastList.newInstance();
		exprsForPartySalary.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		List<GenericValue> partySalaryBreakdownList = null;
		try { 
			partySalaryBreakdownList = delegator.findList("EmplPresentSalary", EntityCondition.makeCondition(exprsForPartySalary, EntityOperator.AND), null, null, null, false);
	
			for (GenericValue rowData : partySalaryBreakdownList){
				String payrollItemId=rowData.get("payrollItemTypeId").toString();			
			    GenericValue payrollItemInfo = null;
				try { 
					payrollItemInfo = delegator.findByPrimaryKey("PayrollItem", UtilMisc.toMap("payrollItemTypeId", payrollItemId));
					
					if(UtilValidate.isNotEmpty(payrollItemInfo.get("isCalculatable"))){
						String itemIsTaxable=payrollItemInfo.get("isCalculatable").toString();				
						if(itemIsTaxable.equalsIgnoreCase("T")){
							BigDecimal itemAmount=rowData.getBigDecimal("amount");
							BigDecimal itemAmountForPandingMonth=itemAmount.multiply(BigDecimal.valueOf(pandingMonth));
							if(UtilValidate.isNotEmpty(payrollItemInfo.get("isCondition")) && (payrollItemInfo.get("isCondition").toString()=="Y")){
								String itemIsExamption=payrollItemInfo.get("isCondition").toString();
								String itemIsExamptionType="";
								if(UtilValidate.isNotEmpty(payrollItemInfo.get("examptionType"))){
									itemIsExamptionType=payrollItemInfo.get("examptionType").toString();
								}
								BigDecimal examptionAmount=BigDecimal.valueOf(0);
								if(UtilValidate.isNotEmpty(payrollItemInfo.get("examptionAmount"))){
									examptionAmount=payrollItemInfo.getBigDecimal("examptionAmount");
								}
								BigDecimal examptionPrecentage=BigDecimal.valueOf(0);
								if(UtilValidate.isNotEmpty(payrollItemInfo.get("examptionPrecentage"))){
									examptionPrecentage=payrollItemInfo.getBigDecimal("examptionPrecentage");
								}
								double examptionAmountForPandingMonthDouble=(examptionAmount.doubleValue() * pandingMonth)/totalMonthInYear;
								BigDecimal examptionAmountForPandingMonth=BigDecimal.valueOf(examptionAmountForPandingMonthDouble);
								if(itemIsExamption.equalsIgnoreCase("T")){
									if(itemIsExamptionType.equalsIgnoreCase("Fiexd")){
										totalTaxableIncome=totalTaxableIncome.add(itemAmountForPandingMonth).subtract(examptionAmountForPandingMonth);
									}
									if(itemIsExamptionType.equalsIgnoreCase("Low")){
										BigDecimal itemYearlyExamptionAmount=itemAmount.multiply(BigDecimal.valueOf(pandingMonth)).multiply(examptionPrecentage).divide(BigDecimal.valueOf(100));
										if(itemYearlyExamptionAmount.floatValue() < examptionAmountForPandingMonth.floatValue()){
											BigDecimal lowExamptionAmount=itemAmount.multiply(BigDecimal.valueOf(pandingMonth)).subtract(itemYearlyExamptionAmount);
											totalTaxableIncome=totalTaxableIncome.add(lowExamptionAmount);
										}
										else{
											BigDecimal lowExamptionAmount=itemAmount.multiply(BigDecimal.valueOf(pandingMonth)).subtract(examptionAmountForPandingMonth);
											totalTaxableIncome=totalTaxableIncome.add(lowExamptionAmount);
										}
									}
								}
							}
							else{
								totalTaxableIncome=totalTaxableIncome.add(itemAmountForPandingMonth);
							}
						}
					}
				} catch (GenericEntityException e) {
		            //request.setAttribute("_ERROR_MESSAGE_", "Payroll Item "+payrollItemId+" Not Find ");
		            return totalTaxableIncome;
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}

		return totalTaxableIncome;
	}
	public static String getTaxPayerType(Delegator delegator,String partyId){
		String taxPayerTypeId=null;
		GenericValue partyPayrollInfo = null;
		try{
			partyPayrollInfo=delegator.findByPrimaryKey("PersonPayrollInfo", UtilMisc.toMap("partyId", partyId));
			if(UtilValidate.isNotEmpty(partyPayrollInfo.get("taxPayerTypeId"))){
				taxPayerTypeId=partyPayrollInfo.get("taxPayerTypeId").toString();			
			}
			
		}
		catch(Exception e){}
		return taxPayerTypeId;
	}
	public static BigDecimal FindMonthlyTaxableAmount (Delegator delegator,String partyId,int pandingMonth,BigDecimal totalTaxableAmount) {
        BigDecimal remainingTaxableAmount=totalTaxableAmount;
		BigDecimal monthlyTaxableAmount=new BigDecimal(0);
		BigDecimal totalPayableTaxAmount=new BigDecimal(0);
		String taxPayerTypeId=getTaxPayerType(delegator,partyId);
		
        List<String> orderBy = UtilMisc.toList("stepOrder");
		List<EntityExpr> exprsForTaxSlab = FastList.newInstance();
		exprsForTaxSlab.add(EntityCondition.makeCondition("taxCalculationTypeId", EntityOperator.EQUALS,taxPayerTypeId));        
		List<GenericValue> partyTaxSlabList = null;
		try {
			partyTaxSlabList = delegator.findList("TaxCondition", EntityCondition.makeCondition(exprsForTaxSlab, EntityOperator.AND), null, orderBy, null, false);
			
			for (GenericValue rowData : partyTaxSlabList){
				if(remainingTaxableAmount.floatValue() > 0){
					BigDecimal slabAmount=rowData.getBigDecimal("amount");
					BigDecimal slabPercentage=rowData.getBigDecimal("deductionPercentage");
					BigDecimal taxDeductionAmount=new BigDecimal(0);
					BigDecimal slabToAmount=rowData.getBigDecimal("toAmount");
					BigDecimal slabAmountForPandingMonth=BigDecimal.valueOf(pandingMonth).divide(BigDecimal.valueOf(12),2,BigDecimal.ROUND_HALF_UP);
					slabAmountForPandingMonth=slabAmount.multiply(slabAmountForPandingMonth);
					if(slabToAmount.floatValue()==0.00f){
						taxDeductionAmount=remainingTaxableAmount.multiply(slabPercentage).divide(BigDecimal.valueOf(100));
						totalPayableTaxAmount=totalPayableTaxAmount.add(taxDeductionAmount);
						remainingTaxableAmount=new BigDecimal(0);
					}
					else if(slabAmountForPandingMonth.floatValue() < remainingTaxableAmount.floatValue()){
						taxDeductionAmount=slabAmountForPandingMonth.multiply(slabPercentage).divide(BigDecimal.valueOf(100));
						totalPayableTaxAmount=totalPayableTaxAmount.add(taxDeductionAmount);
						remainingTaxableAmount=remainingTaxableAmount.subtract(slabAmountForPandingMonth);
					}
					else{
						taxDeductionAmount=remainingTaxableAmount.multiply(slabPercentage).divide(BigDecimal.valueOf(100));
						totalPayableTaxAmount=totalPayableTaxAmount.add(taxDeductionAmount);
						remainingTaxableAmount=new BigDecimal(0);
					}	
				}
			}
			double totalPayableTaxAmountDouble=totalPayableTaxAmount.doubleValue();
			double monthlyTaxableAmountDouble=totalPayableTaxAmountDouble / pandingMonth;
			monthlyTaxableAmount=BigDecimal.valueOf(monthlyTaxableAmountDouble);
		} catch (Exception e) {
		}
		return monthlyTaxableAmount;
	}
	
	public static String setPartyMonthlyTaxableAmount(Delegator delegator,String partyId,String employeeId,String payGradeId,String salaryLevelId,String salaryTemplateId, String basicSalaryStepId, String taxPayerTypeId,String comments,String isTaxApply) {

		int pandingMonth=getPandingMonth();
		BigDecimal totalTaxableIncome=FindYearlyTotalTaxableIncome(delegator, partyId,pandingMonth);
		BigDecimal monthlyTaxableAmount=FindMonthlyTaxableAmount(delegator,partyId,pandingMonth,totalTaxableIncome);
		//-----------------Add Taxable Amount in EmplPresentSalary--------------------//
		if(isTaxApply.equals("Y")){
			setTaxInPresentSalary(delegator,partyId,monthlyTaxableAmount);
		}
		Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
    	Map<String, Object> PersonPayrollInfoIn = FastMap.newInstance();
    	PersonPayrollInfoIn.put("partyId", partyId);
    	PersonPayrollInfoIn.put("employeeId", employeeId);
    	PersonPayrollInfoIn.put("payGradeId", payGradeId);
    	PersonPayrollInfoIn.put("salaryLevelId", salaryLevelId);
    	PersonPayrollInfoIn.put("salaryTemplateId", salaryTemplateId);
    	PersonPayrollInfoIn.put("basicSalaryStepId", basicSalaryStepId);
    	PersonPayrollInfoIn.put("taxPayerTypeId", taxPayerTypeId);
    	PersonPayrollInfoIn.put("monthlyTaxAmount", monthlyTaxableAmount);
    	PersonPayrollInfoIn.put("isTaxApply", isTaxApply);
    	PersonPayrollInfoIn.put("comments", comments);
    	PersonPayrollInfoIn.put("isActive", "Y");
    	PersonPayrollInfoIn.put("dateAdded", currentDatetime);
	    try {
			GenericValue PersonPayrollInfoSetup = delegator
				.makeValue("PersonPayrollInfo",
						UtilMisc.toMap(PersonPayrollInfoIn));
			PersonPayrollInfoSetup.store();
			// ---------------------- History Data Added ---------------------------- //
	    	Map<String, Object> PersonPayrollInfoHisIn = FastMap.newInstance();
	    	PersonPayrollInfoHisIn.put("partyId", partyId);
	    	PersonPayrollInfoHisIn.put("employeeId", employeeId);
	    	PersonPayrollInfoHisIn.put("payGradeId", payGradeId);
	    	PersonPayrollInfoHisIn.put("salaryLevelId", salaryLevelId);
	    	PersonPayrollInfoHisIn.put("salaryTemplateId", salaryTemplateId);
	    	PersonPayrollInfoHisIn.put("basicSalaryStepId", basicSalaryStepId);
	    	PersonPayrollInfoHisIn.put("taxPayerTypeId", taxPayerTypeId);
	    	PersonPayrollInfoHisIn.put("monthlyTaxAmount", monthlyTaxableAmount);
	    	PersonPayrollInfoHisIn.put("isTaxApply", isTaxApply);
	    	PersonPayrollInfoHisIn.put("comments", "Monthly Tax Data Update");
	    	PersonPayrollInfoHisIn.put("isActive", "Y");
	    	PersonPayrollInfoHisIn.put("dateAdded", currentDatetime);
	    	try {
	 			GenericValue PersonPayrollInfoHisSetup = delegator
	 				.makeValue("PersonPayrollInfoHis",
	 						UtilMisc.toMap(PersonPayrollInfoHisIn));
	 			PersonPayrollInfoHisSetup.create();
	    	}catch(Exception e){
	             return "Error";
	        }
	    }catch (Exception e) { 
            return "Error";            
	    }
        return monthlyTaxableAmount.toString();
	}
	public static String setTaxInPresentSalary(Delegator delegator,String partyId,BigDecimal monthlyTaxAmount) {
	
        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
		try {
			GenericValue personEmplPresentSalaryInfo = null;
			try { 
				personEmplPresentSalaryInfo = delegator.findByPrimaryKey("EmplPresentSalary", UtilMisc.toMap("partyId", partyId,"payrollItemTypeId","TAX"));
				
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			///---------------------------- BasicSalary Setup-----------------------//
	    	Map<String, Object> EmployeePresentBasicSalarySetupIn = FastMap.newInstance();
	    	EmployeePresentBasicSalarySetupIn.put("partyId", partyId);
	    	EmployeePresentBasicSalarySetupIn.put("companyId", "");
	    	EmployeePresentBasicSalarySetupIn.put("payrollItemTypeId", "TAX");
	    	EmployeePresentBasicSalarySetupIn.put("calculationType", "D");
	    	EmployeePresentBasicSalarySetupIn.put("calculationMode", "Fixed");
	    	EmployeePresentBasicSalarySetupIn.put("optValue", monthlyTaxAmount.toString());
	    	EmployeePresentBasicSalarySetupIn.put("amount", monthlyTaxAmount);
	    	EmployeePresentBasicSalarySetupIn.put("comments", "");
	    	EmployeePresentBasicSalarySetupIn.put("fromDate", currentDatetime);
	    	EmployeePresentBasicSalarySetupIn.put("emplStatusId", "EMPL_POS_ACTIVE");
		    try {
				GenericValue employeeBasicSalarySetup = delegator
					.makeValue("EmplPresentSalary",
							UtilMisc.toMap(EmployeePresentBasicSalarySetupIn));
				if(personEmplPresentSalaryInfo == null){
					employeeBasicSalarySetup.create();
				}
				else{
					employeeBasicSalarySetup.store();
				}
		    	Map<String, Object> EmplPayrollHistorIn = FastMap.newInstance();
		    	EmplPayrollHistorIn.put("partyId", partyId);
		    	//EmplPayrollHistorIn.put("companyId", "");
		    	EmplPayrollHistorIn.put("payrollItemTypeId", "TAX");
		    	EmplPayrollHistorIn.put("calculationType", "D");
		    	EmplPayrollHistorIn.put("calculationMode", "Fixed");
		    	EmplPayrollHistorIn.put("optValue", monthlyTaxAmount.toString());
		    	EmplPayrollHistorIn.put("amount", monthlyTaxAmount);
		    	EmplPayrollHistorIn.put("comments", "");
		    	EmplPayrollHistorIn.put("fromDate", currentDatetime);
		    	EmplPayrollHistorIn.put("emplStatusId", "EMPL_POS_ACTIVE");
				GenericValue emplPayrollHistorInSetup = delegator
						.makeValue("EmplPayrollHistory",
								UtilMisc.toMap(EmplPayrollHistorIn));
				emplPayrollHistorInSetup.create();

		    } catch (Exception e) { }

		}
		catch(Exception e){}
		return "Success";
	}

	public static String AllEmployeeTaxPayrollIntregration(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<EntityExpr> exprsForEmployeeList = FastList.newInstance();
		exprsForEmployeeList.add(EntityCondition.makeCondition("isActive", EntityOperator.EQUALS,"Y"));        
		
		List<GenericValue> payrollEmployeeList = null;
		try {
			payrollEmployeeList = delegator.findList("PersonPayrollInfo", EntityCondition.makeCondition(exprsForEmployeeList, EntityOperator.AND), null, null, null, false);

			for (GenericValue rowData : payrollEmployeeList){
				String partyId = rowData.get("partyId").toString();
				String employeeId = rowData.get("employeeId").toString();
				BigDecimal monthlyTaxAmount = rowData.getBigDecimal("monthlyTaxAmount");
				String isTaxApply = rowData.get("isTaxApply").toString();
				String payGradeId = rowData.get("payGradeId").toString();
				String salaryLevelId = rowData.get("salaryLevelId").toString();
				String salaryTemplateId = rowData.get("salaryTemplateId").toString();
				String basicSalaryStepId = rowData.get("basicSalaryStepId").toString();
				String taxPayerTypeId = rowData.get("taxPayerTypeId").toString();
				String comments = rowData.get("comments").toString();
				
			    try {
			    	TaxItemPayrollIntregration(delegator,partyId,monthlyTaxAmount,"Y",employeeId,payGradeId,salaryLevelId,salaryTemplateId,basicSalaryStepId,taxPayerTypeId,comments);
			    }catch (Exception e) { 
		            request.setAttribute("_ERROR_MESSAGE_", "Error occer for the employee "+employeeId);
		            return "Error";
			    }
			}
		}
		catch(Exception e){}
		return "Success";
	}
	public static String IndivisualTaxPayrollIntregration(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
		String partyId = request.getParameter("partyId");
		String monthlyTaxAmountStr = request.getParameter("monthlyTaxAmount");
		String isTaxApply = request.getParameter("isTaxApply");
		String employeeId = request.getParameter("employeeId");
		String payGradeId = request.getParameter("payGradeId");
		String salaryLevelId = request.getParameter("salaryLevelId");
		String salaryTemplateId = request.getParameter("salaryTemplateId").toString();
		String basicSalaryStepId = request.getParameter("basicSalaryStepId").toString();
		String taxPayerTypeId = request.getParameter("taxPayerTypeId");
		String comments = request.getParameter("comments");
		try
		{
		BigDecimal monthlyTaxAmount=new BigDecimal(monthlyTaxAmountStr);

    	TaxItemPayrollIntregration(delegator,partyId,monthlyTaxAmount,isTaxApply,employeeId,payGradeId,salaryLevelId,salaryTemplateId,basicSalaryStepId,taxPayerTypeId,comments);
		}
		catch(Exception e){}
        request.setAttribute("_EVENT_MESSAGE_", "Successfully Updated");
        return "Success";
    }
	public static String TaxItemPayrollIntregration(Delegator delegator,String partyId,BigDecimal monthlyTaxAmount,String isTaxApply,String employeeId,String payGradeId,String salaryLevelId,String salaryTemplateId , String basicSalaryStepId ,String taxPayerTypeId,String comments){
		if(isTaxApply.equals("Y")){
			DisableTaxItemFromPayroll(delegator,partyId,employeeId,payGradeId,salaryLevelId,salaryTemplateId, basicSalaryStepId,taxPayerTypeId,comments,monthlyTaxAmount);
		}
		else {
			EnableTaxItemFromPayroll(delegator,partyId,employeeId,payGradeId,salaryLevelId,salaryTemplateId,basicSalaryStepId,taxPayerTypeId,comments,monthlyTaxAmount);			
		}
		return "Success";
	}
	public static String EnableTaxItemFromPayroll(Delegator delegator,String partyId,String employeeId,String payGradeId,String salaryLevelId,String salaryTemplateId , String basicSalaryStepId,String taxPayerTypeId,String comments,BigDecimal monthlyTaxAmount) {
		try{
			setPartyMonthlyTaxableAmount(delegator,partyId,employeeId,payGradeId,salaryLevelId,salaryTemplateId, basicSalaryStepId,taxPayerTypeId,comments,"Y");
		}
		catch(Exception e){}
		return "Success";
	}
	public static String DisableTaxItemFromPayroll(Delegator delegator,String partyId,String employeeId,String payGradeId,String salaryLevelId,String salaryTemplateId , String basicSalaryStepId,String taxPayerTypeId,String comments,BigDecimal monthlyTaxAmount) {
		try {
			GenericValue personEmplPresentSalaryInfo = null;
			try { 
				personEmplPresentSalaryInfo = delegator.findByPrimaryKey("EmplPresentSalary", UtilMisc.toMap("partyId", partyId,"payrollItemTypeId","TAX"));
				personEmplPresentSalaryInfo.remove();


				Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
		    	Map<String, Object> PersonPayrollInfoIn = FastMap.newInstance();
		    	PersonPayrollInfoIn.put("partyId", partyId);
		    	PersonPayrollInfoIn.put("employeeId", employeeId);
		    	PersonPayrollInfoIn.put("payGradeId", payGradeId);
		    	PersonPayrollInfoIn.put("salaryLevelId", salaryLevelId);
		    	PersonPayrollInfoIn.put("salaryTemplateId", salaryTemplateId);
		    	PersonPayrollInfoIn.put("basicSalaryStepId", basicSalaryStepId);
		    	PersonPayrollInfoIn.put("taxPayerTypeId", taxPayerTypeId);
		    	PersonPayrollInfoIn.put("monthlyTaxAmount", monthlyTaxAmount);
		    	PersonPayrollInfoIn.put("isTaxApply", "N");
		    	PersonPayrollInfoIn.put("comments", comments);
		    	PersonPayrollInfoIn.put("isActive", "Y");
		    	PersonPayrollInfoIn.put("dateAdded", currentDatetime);
			    try {
					GenericValue PersonPayrollInfoSetup = delegator
						.makeValue("PersonPayrollInfo",
								UtilMisc.toMap(PersonPayrollInfoIn));
					PersonPayrollInfoSetup.store();
					// ---------------------- History Data Added ---------------------------- //
			    	Map<String, Object> PersonPayrollInfoHisIn = FastMap.newInstance();
			    	PersonPayrollInfoHisIn.put("partyId", partyId);
			    	PersonPayrollInfoHisIn.put("employeeId", employeeId);
			    	PersonPayrollInfoHisIn.put("payGradeId", payGradeId);
			    	PersonPayrollInfoHisIn.put("salaryLevelId", salaryLevelId);
			    	PersonPayrollInfoHisIn.put("taxPayerTypeId", taxPayerTypeId);
			    	PersonPayrollInfoHisIn.put("salaryTemplateId", salaryTemplateId);
			    	PersonPayrollInfoHisIn.put("basicSalaryStepId", basicSalaryStepId);
			    	PersonPayrollInfoHisIn.put("monthlyTaxAmount", monthlyTaxAmount);
			    	PersonPayrollInfoHisIn.put("isTaxApply", "N");
			    	PersonPayrollInfoHisIn.put("comments", "Monthly Tax Data Update");
			    	PersonPayrollInfoHisIn.put("isActive", "Y");
			    	PersonPayrollInfoHisIn.put("dateAdded", currentDatetime);
			    	try {
			 			GenericValue PersonPayrollInfoHisSetup = delegator
			 				.makeValue("PersonPayrollInfoHis",
			 						UtilMisc.toMap(PersonPayrollInfoHisIn));
			 			PersonPayrollInfoHisSetup.create();
			    	}catch(Exception e){
			             return "Error";
			        }
			    }catch (Exception e) { 
		            return "Error";            
			    }
				
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			
		}
		catch(Exception e){}
		return "Success";
	}
}


