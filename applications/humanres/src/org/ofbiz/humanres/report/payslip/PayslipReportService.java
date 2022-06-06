package org.ofbiz.humanres.report.payslip;
import javolution.util.FastList;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PayslipReportService {
	
	public static List<GenericValue> getAnEmployeeBenefitFromDatabase(Delegator delegator, String partyId, String monthNumber) {
        List<GenericValue> payrollBenefit = null;
        try {
        	payrollBenefit = delegator.findByAnd("EmplFinalSalaryOpt", UtilMisc.toMap("partyId", partyId, "monthNumber", monthNumber,"calculationType","B"));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return payrollBenefit;
    }
	
	public static List<GenericValue> getAnEmployeeDeductionsFromDatabase(Delegator delegator, String partyId, String monthNumber) {
        List<GenericValue> payrollBenefit = null;
        try {
        	payrollBenefit = delegator.findByAnd("EmplFinalSalaryOpt", UtilMisc.toMap("partyId", partyId, "monthNumber", monthNumber,"calculationType","D"));
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
            if (UtilValidate.isNotEmpty(payrollItemList.get(0).get("payrollItemName"))) {
            	payrollItemTypeName = payrollItemList.get(0).get("payrollItemName").toString();
            }
        }
        
        return payrollItemTypeName;
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
}