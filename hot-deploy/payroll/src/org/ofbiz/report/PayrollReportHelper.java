package org.ofbiz.report;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import java.math.BigDecimal;
import java.util.List;

public class PayrollReportHelper{
	public static List<GenericValue> getAnEmployeeBenefitFromDatabase(Delegator delegator, String partyId, String monthNumber, String fiscalYear) {
        List<GenericValue> payrollBenefit = null;
        try {
        	payrollBenefit = delegator.findByAnd("EmplFinalSalaryOpt", UtilMisc.toMap("partyId", partyId, "monthNumber", monthNumber, "fiscalYear", fiscalYear,"calculationType","B"));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return payrollBenefit;
    }
	
	public static List<GenericValue> getAnEmployeeDeductionsFromDatabase(Delegator delegator, String partyId, String monthNumber, String fiscalYear) {
        List<GenericValue> payrollBenefit = null;
        try {
        	payrollBenefit = delegator.findByAnd("EmplFinalSalaryOpt", UtilMisc.toMap("partyId", partyId, "monthNumber", monthNumber, "fiscalYear", fiscalYear,"calculationType","D"));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return payrollBenefit;
    }
	public static String getApayrollItemName(Delegator delegator, String payrollItemTypeId) {
       String payrollItemTypeName = "";
       List<GenericValue> payrollItemList = null;
        try {
        	payrollItemList = delegator.findByAnd("PayrollItem", UtilMisc.toMap("payrollItemTypeId", payrollItemTypeId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        if (UtilValidate.isNotEmpty(payrollItemList)) {
            if (UtilValidate.isNotEmpty(payrollItemList.get(0).get("name"))) {
            	payrollItemTypeName = payrollItemList.get(0).get("name").toString();
            }
        }
        
        return payrollItemTypeName;
    }
	public static String getAnEmployeeName(Delegator delegator, String partyId) {
        List<GenericValue> partyName = null;
        try {
        	partyName = delegator.findByAnd("PartyAndPersonAndFulfillment", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String firstName = "";
        String lastName= "";
        if (UtilValidate.isNotEmpty(partyName)) {
            if (UtilValidate.isNotEmpty(partyName.get(0).get("firstName"))) {
            	firstName = partyName.get(0).get("firstName").toString();
            }
        }
        if (UtilValidate.isNotEmpty(partyName)) {
            if (UtilValidate.isNotEmpty(partyName.get(0).get("lastName"))) {
            	lastName = partyName.get(0).get("lastName").toString();
            }
        }
        String Name = firstName +" " +lastName;
        return Name;
    }
	
	public static String getAnEmployeeId(Delegator delegator, String partyId) {
        List<GenericValue> partyEmplId = null;
        try {
        	partyEmplId = delegator.findByAnd("PartyAndPersonAndFulfillment", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String userId = "";
       
        if (UtilValidate.isNotEmpty(partyEmplId)) {
            if (UtilValidate.isNotEmpty(partyEmplId.get(0).get("userLoginId"))) {
            	userId = partyEmplId.get(0).get("userLoginId").toString();
            }
        }
        
      
        return userId;
    }
	
	public static String getpayrollType(Delegator delegator, String payrollItemId) {
		 List<GenericValue> payrollstatus = null;
	        try {
	        	payrollstatus = delegator.findByAnd("PayrollItem", UtilMisc.toMap("payrollItemTypeId", payrollItemId));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String invoiceItemTypeId = null;
	        if (UtilValidate.isNotEmpty(payrollstatus)) {
	            if (UtilValidate.isNotEmpty(payrollstatus.get(0).get("invoiceItemTypeId"))) {
	            	invoiceItemTypeId = payrollstatus.get(0).get("invoiceItemTypeId").toString();
	            }
	        }
	        List<GenericValue> payrollGlAccId = null;
	        try {
	        	payrollGlAccId = delegator.findByAnd("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String glAccountId = null;
	        if (UtilValidate.isNotEmpty(payrollGlAccId)) {
	            if (UtilValidate.isNotEmpty(payrollGlAccId.get(0).get("defaultGlAccountId"))) {
	            	glAccountId = payrollGlAccId.get(0).get("defaultGlAccountId").toString();
	            }
	        }
	        if (glAccountId.equals("502100")) {
	            return "basic";
	        }
	        if (glAccountId.equals("502300")) {
	            return "houseRent";
	        }
	        
	        if (glAccountId.equals("502400")) {
	            return "medical";
	        }
	        if (glAccountId.equals("502700")) {
	            return "mobile";
	        }
	        if (glAccountId.equals("502900")) {
	            return "otherAllowance";
	        }
	        if (glAccountId.equals("505700")) {
	            return "otherded";
	        }
	        if (glAccountId.equals("506200")) {
	            return "CompanyCon";
	        }
	        if (glAccountId.equals("505100")) {
	            return "employCon";
	        }
	        if (glAccountId.equals("505500")) {
	            return "emplTax";
	        }
	        
	        return "";
    }
}