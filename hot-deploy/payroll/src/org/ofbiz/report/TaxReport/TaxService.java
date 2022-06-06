package org.ofbiz.report.TaxReport;

import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.report.TaxReport.TaxService;

public class TaxService {

	public static final String module = TaxService.class.getName();


    public static String getAnEmployeeName(Delegator delegator, String partyId) {
        List<GenericValue> partyName = null;
        try {
        	partyName = delegator.findByAnd("Person", UtilMisc.toMap("partyId", partyId));
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
    

    public static String getTaxAmount(Delegator delegator, String partyId) {
        List<GenericValue> partyamount = null;
        try {
        	partyamount = delegator.findByAnd("EmplFinalSalaryOpt", UtilMisc.toMap("payrollItemTypeId", "TAX"));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String amount = "";
       
        if (UtilValidate.isNotEmpty(partyamount)) {
            if (UtilValidate.isNotEmpty(partyamount.get(0).get("amount"))) {
            	amount = partyamount.get(0).get("amount").toString();
            }
        }
        
      
        return amount;
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
