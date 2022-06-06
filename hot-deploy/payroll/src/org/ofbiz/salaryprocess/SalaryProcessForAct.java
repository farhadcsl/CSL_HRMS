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

public class SalaryProcessForAct {
	public static String finalProcessSalary(HttpServletRequest request, HttpServletResponse response) {
		 Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();

        String monthNumber = "";
        String companyId = "";
        String description = "";
        String year="";
        String processId = "";
        String calculationType ="";
    	String calculationMode ="";
    	String optValue ="";
    	String comments="";
        if (null != request.getParameter("monthNumber")) {
            monthNumber = request.getParameter("monthNumber");
        }
        if (null != request.getParameter("companyId")) {
            companyId = request.getParameter("companyId");
        }
        if (null != request.getParameter("fiscalYear")) {
        	year = request.getParameter("fiscalYear");
        }
        if (null != request.getParameter("processId")) {
        	processId = request.getParameter("processId");
        }
        if (null != request.getParameter("monthDescription")) {
            description = request.getParameter("monthDescription");
            description = description.replace('+', ' ');
        }
        if (processId.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "processId can not be empty");
            return "error";
        }
        if (monthNumber.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Month can not be empty");
            return "error";
        }
        Timestamp fromDate = new Timestamp(System.currentTimeMillis());
        List<GenericValue> salaryList = null;
        GenericValue employee = null;
        try {
        	salaryList = delegator.findByAnd("EligibleEmployee", UtilMisc.toMap("companyId", companyId, "monthNumber", monthNumber,"processId",processId,"isSalary","Y", "isActive","Y"));
            

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        if(salaryList.isEmpty()){
        	request.setAttribute("_ERROR_MESSAGE_", "No Employee found to process Salary");
            return "error";
        }
        for (GenericValue eligibleForSal : salaryList) {
        	String partyId = eligibleForSal.get("partyId").toString();
        	String versionId = "";
        	if (null != eligibleForSal.get("versionId")) {
        		versionId = request.getParameter("versionId");
            }
        	
        List<GenericValue> emplTemSalary = null;
        try {
        	emplTemSalary = delegator.findByAnd("EmplTempSalaryOpt",
                    UtilMisc.toMap("monthNumber", monthNumber,"fiscalYear", year, "processId",processId,"partyId",partyId ));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        
        for (GenericValue value : emplTemSalary) {
        	Map<String, Object> individualPayrollItemfinal = FastMap.newInstance();
        	
            String payrollItemTypeId = value.get("payrollItemTypeId").toString();
            if (null != value.get("calculationType")) {
           calculationType = value.get("calculationType").toString();
            }
            
            if (null != value.get("calculationMode")) {
            calculationMode = value.get("calculationMode").toString();
            }
            
            if (null != value.get("optValue")) {
            	optValue = value.get("optValue").toString();
            }
            if (null != value.get("comments")) {
            comments = value.get("comments").toString();
            }
            BigDecimal amount = new BigDecimal(value.get("amount").toString());
           
            
            if (UtilValidate.isNotEmpty(partyId)) {
            	 individualPayrollItemfinal.put("partyId", partyId);
	        }
            if (UtilValidate.isNotEmpty(payrollItemTypeId)) {
           	 individualPayrollItemfinal.put("payrollItemTypeId", payrollItemTypeId);
	        }
            if (UtilValidate.isNotEmpty(calculationType)) {
              	 individualPayrollItemfinal.put("calculationType", calculationType);
   	        }
            if (UtilValidate.isNotEmpty(calculationMode)) {
             	 individualPayrollItemfinal.put("calculationMode", calculationMode);
  	        }
            if (UtilValidate.isNotEmpty(optValue)) {
            	 individualPayrollItemfinal.put("optValue", optValue);
 	        }
            
           	
           	 if (UtilValidate.isNotEmpty(comments)) {
            	 individualPayrollItemfinal.put("comments", comments);
 	        }
           	if (UtilValidate.isNotEmpty(comments)) {
           	 individualPayrollItemfinal.put("comments", comments);
	        }
           	if (UtilValidate.isNotEmpty(versionId)) {
              	 individualPayrollItemfinal.put("versionId", versionId);
   	        }
           	
           	individualPayrollItemfinal.put("amount", amount);
           	individualPayrollItemfinal.put("fromDate", fromDate);
           	individualPayrollItemfinal.put("processId", processId);
       
           	individualPayrollItemfinal.put("monthNumber", monthNumber);
            individualPayrollItemfinal.put("fiscalYear", year);
            individualPayrollItemfinal.put("isAccounting", "N");
            try {
          		GenericValue emplFinalSalaryOpt = delegator
          				.makeValue("EmplFinalSalaryOpt",
          						UtilMisc.toMap(individualPayrollItemfinal));
          		emplFinalSalaryOpt.create();
              } catch (Exception e) {

              	e.printStackTrace();
              }
            GenericValue updateTemOpt = null; 
            try {
            	updateTemOpt = delegator.findByPrimaryKey("EmplTempSalaryOpt", UtilMisc.toMap("processId", processId,"partyId", partyId,"payrollItemTypeId",payrollItemTypeId));
            	
            	updateTemOpt.remove();
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
            
        }
        
        List<GenericValue> salaryListss = null;
        GenericValue employeess = null;
        try {
        	salaryListss = delegator.findByAnd("EligibleEmployee", UtilMisc.toMap("companyId", companyId, "monthNumber", monthNumber,"processId",processId,"isSalary","Y", "isActive","Y"));
            

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    	Map<String, Object> individualPayrolltotal = FastMap.newInstance();
    	individualPayrolltotal.put("totalSalary", (BigDecimal) salaryListss.get(0).get("totalSalary"));
    	individualPayrolltotal.put("fromDate", fromDate);
    	individualPayrolltotal.put("partyId", partyId);
    	individualPayrolltotal.put("processId", salaryListss.get(0).get("processId").toString());
    	individualPayrolltotal.put("monthNumber", monthNumber);
    	individualPayrolltotal.put("fiscalYear", year);
    	individualPayrolltotal.put("companyId", companyId);
        individualPayrolltotal.put("isActive", "Y");
        individualPayrolltotal.put("isSalary", "Y");
        try {
      		GenericValue emplSalaryList = delegator
      				.makeValue("EmployeeSalaryFinal",
      						UtilMisc.toMap(individualPayrolltotal));
      		emplSalaryList.create();
          } catch (Exception e) {

          	e.printStackTrace();
          }
        
        
        
        GenericValue updateEligibleEmpl = null; 
        try {
        	updateEligibleEmpl = delegator.findByPrimaryKey("EligibleEmployee", UtilMisc.toMap("processId", processId,"partyId", partyId));
        	
        	updateEligibleEmpl.remove();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
       }
        
	return "success";	
	}
}