package org.ofbiz.salaryprocess;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.xmlrpc.metadata.Util;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transaction;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class UpdateSalaryItems {
	

	public static String updateBenefit(HttpServletRequest request, HttpServletResponse response) {
		 Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        String processId = request.getParameter("processId");
	        String monthNumber = request.getParameter("monthNumber");
	        String partyId = request.getParameter("partyId");
	        String year = request.getParameter("year");
	        String payrollItemTypeId = request.getParameter("payrollItemTypeId");
	        String NewAmount = request.getParameter("NewAmount");
	        
	        List<GenericValue> getTem = null;
	        try {
	        	getTem = delegator.findByAnd("EmplTempSalaryOpt", UtilMisc.toMap("processId", processId,"partyId", partyId,"payrollItemTypeId",payrollItemTypeId, "calculationType", "B"));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        BigDecimal currentAmount = (BigDecimal) getTem.get(0).get("amount");
	        BigDecimal finalNewAmount= new BigDecimal(NewAmount);
	       
		    GenericValue updateOpt = null; 
	        try {
	        	updateOpt = delegator.findByPrimaryKey("EmplTempSalaryOpt", UtilMisc.toMap("processId", processId,"partyId", partyId,"payrollItemTypeId",payrollItemTypeId));
	        	updateOpt.set("amount", finalNewAmount);
	        	updateOpt.store();
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String calculationType = null;
	        if (UtilValidate.isNotEmpty(getTem)) {
	            if (UtilValidate.isNotEmpty(getTem.get(0).get("calculationType"))) {
	            	calculationType = getTem.get(0).get("calculationType").toString();
	            }
	        }
	        List<GenericValue> benefitsaddWithEligibleEmpl = null;
	        try {
	        	benefitsaddWithEligibleEmpl = delegator.findByAnd("EligibleEmployee", UtilMisc.toMap("partyId", partyId,"processId",processId));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        BigDecimal EligibleEmplAmount = (BigDecimal) benefitsaddWithEligibleEmpl.get(0).get("totalSalary");
	        BigDecimal MinusEligibleEmplAmount = EligibleEmplAmount.subtract(currentAmount);
	        BigDecimal FinalEligibleEmplAmount = MinusEligibleEmplAmount.add(finalNewAmount);
	       
	        GenericValue updateEempltotalSalary = null; 
	        try {
	        	updateEempltotalSalary = delegator.findByPrimaryKey("EligibleEmployee", UtilMisc.toMap("processId", processId,"partyId", partyId));
	        	updateEempltotalSalary.set("totalSalary", FinalEligibleEmplAmount);
	        	updateEempltotalSalary.store();
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        /*=============sTART fROM hERE===================*/
	        request.setAttribute("processId", processId);
            request.setAttribute("partyId",partyId);
            request.setAttribute("monthNumber", monthNumber);
            request.setAttribute("year", year);
		return "success";
	}
	public static String updateDeductions(HttpServletRequest request, HttpServletResponse response) {
		 Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();

	        String processId = request.getParameter("processId");
	        String monthNumber = request.getParameter("monthNumber");
	        String partyId = request.getParameter("partyId");
	        String year = request.getParameter("year");
	        String payrollItemTypeId = request.getParameter("payrollItemTypeId");
	        String NewAmount = request.getParameter("NewAmount");
	        List<GenericValue> getTem = null;
	        try {
	        	getTem = delegator.findByAnd("EmplTempSalaryOpt", UtilMisc.toMap("processId", processId,"partyId", partyId,"payrollItemTypeId",payrollItemTypeId, "calculationType", "D"));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        BigDecimal currentAmount = (BigDecimal) getTem.get(0).get("amount");
	        BigDecimal finalNewAmount= new BigDecimal(NewAmount);
	    GenericValue updateOpt = null; 
        try {
        	updateOpt = delegator.findByPrimaryKey("EmplTempSalaryOpt", UtilMisc.toMap("processId", processId,"partyId", partyId,"payrollItemTypeId",payrollItemTypeId));
        	updateOpt.set("amount", finalNewAmount);
        	updateOpt.store();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        
        List<GenericValue> benefitsaddWithEligibleEmpl = null;
        try {
        	benefitsaddWithEligibleEmpl = delegator.findByAnd("EligibleEmployee", UtilMisc.toMap("partyId", partyId,"processId",processId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        BigDecimal EligibleEmplAmount = (BigDecimal) benefitsaddWithEligibleEmpl.get(0).get("totalSalary");
        BigDecimal finalEmplSalary= EligibleEmplAmount.add(currentAmount);
        BigDecimal OverallFinalEmplSalary= finalEmplSalary.subtract(finalNewAmount);
        GenericValue updateEempltotalSalary = null; 
        try {
        	updateEempltotalSalary = delegator.findByPrimaryKey("EligibleEmployee", UtilMisc.toMap("processId", processId,"partyId", partyId));
        	updateEempltotalSalary.set("totalSalary", OverallFinalEmplSalary);
        	updateEempltotalSalary.store();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        
        request.setAttribute("processId", processId);
        request.setAttribute("partyId",partyId);
        request.setAttribute("monthNumber", monthNumber);
        request.setAttribute("year", year);
        
return "success";	
}
	public static String deleteBenefitItems(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
		String processId = request.getParameter("processId");
        String monthNumber = request.getParameter("monthNumber");
        String partyId = request.getParameter("partyId");
        String year = request.getParameter("year");
        String payrollItemTypeId = request.getParameter("payrollItemTypeId");
        List<GenericValue> getTem = null;
        try {
        	getTem = delegator.findByAnd("EmplTempSalaryOpt", UtilMisc.toMap("processId", processId,"partyId", partyId,"payrollItemTypeId",payrollItemTypeId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String calculationType = null;
        if (UtilValidate.isNotEmpty(getTem)) {
            if (UtilValidate.isNotEmpty(getTem.get(0).get("calculationType"))) {
            	calculationType = getTem.get(0).get("calculationType").toString();
            }
        }
        BigDecimal currentAmount = (BigDecimal) getTem.get(0).get("amount");
        GenericValue updateOpt = null; 
        try {
        	updateOpt = delegator.findByPrimaryKey("EmplTempSalaryOpt", UtilMisc.toMap("processId", processId,"partyId", partyId,"payrollItemTypeId",payrollItemTypeId));
        	
        	updateOpt.remove();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        List<GenericValue> benefitsaddWithEligibleEmpl = null;
        try {
        	benefitsaddWithEligibleEmpl = delegator.findByAnd("EligibleEmployee", UtilMisc.toMap("partyId", partyId,"processId",processId, "monthNumber",monthNumber));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        if (calculationType.equalsIgnoreCase("B")){
        BigDecimal EligibleEmplAmount = (BigDecimal) benefitsaddWithEligibleEmpl.get(0).get("totalSalary");
        BigDecimal MinusEligibleEmplAmount = EligibleEmplAmount.subtract(currentAmount);
        GenericValue updateEempltotalSalary = null; 
        try {
        	updateEempltotalSalary = delegator.findByPrimaryKey("EligibleEmployee", UtilMisc.toMap("processId", processId,"partyId", partyId));
        	updateEempltotalSalary.set("totalSalary", MinusEligibleEmplAmount);
        	updateEempltotalSalary.store();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        } else {
        	 BigDecimal EligibleEmplAmount = (BigDecimal) benefitsaddWithEligibleEmpl.get(0).get("totalSalary");
             BigDecimal AddEligibleEmplAmount = EligibleEmplAmount.add(currentAmount);
             GenericValue updateEempltotalSalary = null; 
             try {
             	updateEempltotalSalary = delegator.findByPrimaryKey("EligibleEmployee", UtilMisc.toMap("processId", processId,"partyId", partyId));
             	updateEempltotalSalary.set("totalSalary", AddEligibleEmplAmount);
             	updateEempltotalSalary.store();
             } catch (GenericEntityException e) {
                 e.printStackTrace();
             } 
             
        }
        request.setAttribute("processId", processId);
        request.setAttribute("partyId",partyId);
        request.setAttribute("monthNumber", monthNumber);
        request.setAttribute("year", year);
        
		return "success";
	}
	public static String addNewSalaryBenefits(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
		String processId = request.getParameter("processId");
        String monthNumber = request.getParameter("monthNumber");
        String partyId = request.getParameter("partyId");
        String reqPartyId = "";
        if (null != request.getParameter("reqPartyId")) {
        	reqPartyId = request.getParameter("reqPartyId");
        }
        String year = request.getParameter("year");
        String updateamount = request.getParameter("updateamount");
        String payrollItemTypeId = request.getParameter("payrollItemTypeId");
        String comment = request.getParameter("comment");
        BigDecimal newAmount = new BigDecimal(updateamount);
        
        
        List<GenericValue> partyList = null;
        try {
        	partyList = delegator.findByAnd("UserLogin",
                    UtilMisc.toMap("userLoginId", reqPartyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String reqFinPartyId = null;
        if (UtilValidate.isNotEmpty(partyList)) {
            if (UtilValidate.isNotEmpty(partyList.get(0).get("partyId"))) {
            	reqFinPartyId = partyList.get(0).get("partyId").toString();
            }
        }
        List<GenericValue> payrollstatus = null;
        try {
        	payrollstatus = delegator.findByAnd("PayrollItem", UtilMisc.toMap("payrollItemTypeId", payrollItemTypeId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String invoiceItemTypeId = null;
        if (UtilValidate.isNotEmpty(payrollstatus)) {
            if (UtilValidate.isNotEmpty(payrollstatus.get(0).get("invoiceItemTypeId"))) {
            	invoiceItemTypeId = payrollstatus.get(0).get("invoiceItemTypeId").toString();
            }
        }
        List<GenericValue> invoiceItem = null;
        try {
        	invoiceItem = delegator.findByAnd("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String invoiceParentTypeId = null;
        if (UtilValidate.isNotEmpty(invoiceItem)) {
            if (UtilValidate.isNotEmpty(invoiceItem.get(0).get("parentTypeId"))) {
            	invoiceParentTypeId = invoiceItem.get(0).get("parentTypeId").toString();
            }
        }
        if (invoiceParentTypeId.equalsIgnoreCase("PAYROL_DD_FROM_GROSS")){
        	request.setAttribute("_ERROR_MESSAGE_", "This Item is A diduction type Not Earnings");
            return "error";
        }
        
        List<GenericValue> getTem = null;
        try {
        	getTem = delegator.findByAnd("EmplTempSalaryOpt", UtilMisc.toMap("processId", processId,"partyId", partyId,"monthNumber",monthNumber,"payrollItemTypeId",payrollItemTypeId ));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        if (!getTem.isEmpty()){
        	request.setAttribute("_ERROR_MESSAGE_", "This Item Already Exists for this Employee");
            return "error";
        }
        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        Map<String, Object> individualPayrollBenDedItem = FastMap.newInstance();
        individualPayrollBenDedItem.put("partyId", partyId);
        individualPayrollBenDedItem.put("payrollItemTypeId", payrollItemTypeId);
        individualPayrollBenDedItem.put("calculationType", "B");
        individualPayrollBenDedItem.put("amount", newAmount);
        individualPayrollBenDedItem.put("fromDate", stamp);
        individualPayrollBenDedItem.put("processId", processId);
        individualPayrollBenDedItem.put("monthNumber", monthNumber);
        individualPayrollBenDedItem.put("fiscalYear", year);
        individualPayrollBenDedItem.put("comments", comment);
        try {
      		GenericValue emplTemsalaryopt = delegator
      				.makeValue("EmplTempSalaryOpt",
      						UtilMisc.toMap(individualPayrollBenDedItem));
      		emplTemsalaryopt.create();
          } catch (Exception e) {

          	e.printStackTrace();
          }
              
        List<GenericValue> benefitsaddWithEligibleEmpl = null;
        try {
        	benefitsaddWithEligibleEmpl = delegator.findByAnd("EligibleEmployee", UtilMisc.toMap("partyId", partyId,"processId",processId, "monthNumber",monthNumber));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        BigDecimal EligibleEmplAmount = (BigDecimal) benefitsaddWithEligibleEmpl.get(0).get("totalSalary");
      BigDecimal totalEligibleEmplAmount = EligibleEmplAmount.add(newAmount);
      GenericValue updateEempltotalSalary = null; 
      try {
      	updateEempltotalSalary = delegator.findByPrimaryKey("EligibleEmployee", UtilMisc.toMap("processId", processId,"partyId", partyId));
      	updateEempltotalSalary.set("totalSalary", totalEligibleEmplAmount);
      	updateEempltotalSalary.store();
      } catch (GenericEntityException e) {
          e.printStackTrace();
      }
      
      Map<String, Object> salaryItemTem = FastMap.newInstance();
      salaryItemTem.put("partyId", partyId);
      salaryItemTem.put("payrollItemTypeId", payrollItemTypeId);
      salaryItemTem.put("calculationType", "B");
      salaryItemTem.put("amount", newAmount);
      salaryItemTem.put("fromDate", stamp);
      salaryItemTem.put("processId", processId);
      salaryItemTem.put("monthNumber", monthNumber);
      salaryItemTem.put("fiscalYear", year);
      salaryItemTem.put("comment", comment);
      salaryItemTem.put("reqPartyId", reqFinPartyId);
     
      try {
    		GenericValue emplsalaryItemTem = delegator
    				.makeValue("SalaryItemTem",
    						UtilMisc.toMap(salaryItemTem));
    		emplsalaryItemTem.create();
        } catch (Exception e) {

        	e.printStackTrace();
        }
      request.setAttribute("processId", processId);
      request.setAttribute("partyId",partyId);
      request.setAttribute("monthNumber", monthNumber);
      request.setAttribute("year", year);
     return "success";   
	}
	
	
	
	public static String addNewDDSalaryItems(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
		String processId = request.getParameter("processId");
        String monthNumber = request.getParameter("monthNumber");
        String partyId = request.getParameter("partyId");
        String reqPartyId = "";
        if (null != request.getParameter("reqPartyId")) {
        	reqPartyId = request.getParameter("reqPartyId");
        }
        String year = request.getParameter("year");
        String updateamount = request.getParameter("updateamount");
        String payrollItemTypeId = request.getParameter("payrollItemTypeId");
        String comment = request.getParameter("comment");
        BigDecimal newAmount = new BigDecimal(updateamount);
        
        
        List<GenericValue> partyList = null;
        try {
        	partyList = delegator.findByAnd("UserLogin",
                    UtilMisc.toMap("userLoginId", reqPartyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String reqFinPartyId = null;
        if (UtilValidate.isNotEmpty(partyList)) {
            if (UtilValidate.isNotEmpty(partyList.get(0).get("partyId"))) {
            	reqFinPartyId = partyList.get(0).get("partyId").toString();
            }
        }
        List<GenericValue> payrollstatus = null;
        try {
        	payrollstatus = delegator.findByAnd("PayrollItem", UtilMisc.toMap("payrollItemTypeId", payrollItemTypeId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String invoiceItemTypeId = null;
        if (UtilValidate.isNotEmpty(payrollstatus)) {
            if (UtilValidate.isNotEmpty(payrollstatus.get(0).get("invoiceItemTypeId"))) {
            	invoiceItemTypeId = payrollstatus.get(0).get("invoiceItemTypeId").toString();
            }
        }
        List<GenericValue> invoiceItem = null;
        try {
        	invoiceItem = delegator.findByAnd("InvoiceItemType", UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String invoiceParentTypeId = null;
        if (UtilValidate.isNotEmpty(invoiceItem)) {
            if (UtilValidate.isNotEmpty(invoiceItem.get(0).get("parentTypeId"))) {
            	invoiceParentTypeId = invoiceItem.get(0).get("parentTypeId").toString();
            }
        }
        if (invoiceParentTypeId.equalsIgnoreCase("PAYROL_EARN_HOURS")){
        	request.setAttribute("_ERROR_MESSAGE_", "This Item is A Benefit type Not Deductions");
            return "error";
        }
        
        List<GenericValue> getTem = null;
        try {
        	getTem = delegator.findByAnd("EmplTempSalaryOpt", UtilMisc.toMap("processId", processId,"partyId", partyId,"monthNumber",monthNumber,"payrollItemTypeId",payrollItemTypeId ));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        if (!getTem.isEmpty()){
        	request.setAttribute("_ERROR_MESSAGE_", "This Item Already Exists for this Employee");
            return "error";
        }
        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        Map<String, Object> individualPayrollDedItem = FastMap.newInstance();
        individualPayrollDedItem.put("partyId", partyId);
        individualPayrollDedItem.put("payrollItemTypeId", payrollItemTypeId);
        individualPayrollDedItem.put("calculationType", "D");
        individualPayrollDedItem.put("amount", newAmount);
        individualPayrollDedItem.put("fromDate", stamp);
        individualPayrollDedItem.put("processId", processId);
        individualPayrollDedItem.put("monthNumber", monthNumber);
        individualPayrollDedItem.put("fiscalYear", year);
        individualPayrollDedItem.put("comments", comment);
        try {
      		GenericValue emplTemsalaryopt = delegator
      				.makeValue("EmplTempSalaryOpt",
      						UtilMisc.toMap(individualPayrollDedItem));
      		emplTemsalaryopt.create();
          } catch (Exception e) {

          	e.printStackTrace();
          }
              
        List<GenericValue> benefitsaddWithEligibleEmpl = null;
        try {
        	benefitsaddWithEligibleEmpl = delegator.findByAnd("EligibleEmployee", UtilMisc.toMap("partyId", partyId,"processId",processId, "monthNumber",monthNumber));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        BigDecimal EligibleEmplAmount = (BigDecimal) benefitsaddWithEligibleEmpl.get(0).get("totalSalary");
      BigDecimal totalEligibleEmplAmount = EligibleEmplAmount.subtract(newAmount);
      GenericValue updateEempltotalSalary = null; 
      try {
      	updateEempltotalSalary = delegator.findByPrimaryKey("EligibleEmployee", UtilMisc.toMap("processId", processId,"partyId", partyId));
      	updateEempltotalSalary.set("totalSalary", totalEligibleEmplAmount);
      	updateEempltotalSalary.store();
      } catch (GenericEntityException e) {
          e.printStackTrace();
      }
      
      Map<String, Object> salaryItemTem = FastMap.newInstance();
      salaryItemTem.put("partyId", partyId);
      salaryItemTem.put("payrollItemTypeId", payrollItemTypeId);
      salaryItemTem.put("calculationType", "D");
      salaryItemTem.put("amount", newAmount);
      salaryItemTem.put("fromDate", stamp);
      salaryItemTem.put("processId", processId);
      salaryItemTem.put("monthNumber", monthNumber);
      salaryItemTem.put("fiscalYear", year);
      salaryItemTem.put("comment", comment);
      salaryItemTem.put("reqPartyId", reqFinPartyId);
     
      try {
    		GenericValue emplsalaryItemTem = delegator
    				.makeValue("SalaryItemTem",
    						UtilMisc.toMap(salaryItemTem));
    		emplsalaryItemTem.create();
        } catch (Exception e) {

        	e.printStackTrace();
        }
      request.setAttribute("processId", processId);
      request.setAttribute("partyId",partyId);
      request.setAttribute("monthNumber", monthNumber);
      request.setAttribute("year", year);
     return "success";   
	}
	
}