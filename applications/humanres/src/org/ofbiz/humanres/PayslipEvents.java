package org.ofbiz.humanres;

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
public class PayslipEvents{
	 public static String viewpayslip(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        String monthNumber = "";
	        String fiscalYear="";
	        String partyId= "";
	        if (null != request.getParameter("monthNumber")) {
	            monthNumber = request.getParameter("monthNumber");
	        }
	        if (null != request.getParameter("fiscalYear")) {
	        	fiscalYear = request.getParameter("fiscalYear");
	        }
	        if (null != request.getParameter("partyId")) {
	        	partyId = request.getParameter("partyId");
	        }
	        List<GenericValue> employeeStatus = null;
	        
	        try {
	        	employeeStatus = delegator.findByAnd("EmployeeSalaryFinal", UtilMisc.toMap("partyId", partyId, "monthNumber", monthNumber,"fiscalYear",fiscalYear));
	            

	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        if (employeeStatus.isEmpty()){
	        	request.setAttribute("_ERROR_MESSAGE_", "Salary for this month not process yet, Please contact with your admin");
	            return "error";
	        } 
	 
	       request.setAttribute("year", fiscalYear); 
	       request.setAttribute("monthNumber", monthNumber); 
	       request.setAttribute("partyId", partyId); 
	 return "success";
	 }

	 
	 
	 public static String KpiSearchJava(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        String mMonth = "";
	        String mYear="";
	        String partyId= "";
	        if (null != request.getParameter("mMonth")) {
	        	mMonth = request.getParameter("mMonth");
	        }
	        if (null != request.getParameter("mYear")) {
	        	mYear = request.getParameter("mYear");
	        }
	        if (null != request.getParameter("partyId")) {
	        	partyId = request.getParameter("partyId");
	        }
	        List<GenericValue> employeeStatus = null;
	        
	        try {
	        	employeeStatus = delegator.findByAnd("EmployeeGoalsAndKpiMonthly", UtilMisc.toMap("partyId", partyId, "mMonth", mMonth,"mYear",mYear));

	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        if (employeeStatus.isEmpty()){
	        	request.setAttribute("_ERROR_MESSAGE_", "There is no data");
	            return "error";
	        } 
	 
	       request.setAttribute("mYear", mYear); 
	       request.setAttribute("mMonth", mMonth); 
	       request.setAttribute("partyId", partyId); 
	 return "success";
	 }
	 
	 public static String yearlyKpiSearch(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        String year="";
	        String partyId= "";
	        if (null != request.getParameter("year")) {
	        	year = request.getParameter("year");
	        }
	        if (null != request.getParameter("partyId")) {
	        	partyId = request.getParameter("partyId");
	        }
	        List<GenericValue> employeeYearlyStatus = null;
	        
	        try {
	        	employeeYearlyStatus = delegator.findByAnd("EmployeeGoalsAndKpi", UtilMisc.toMap("partyId", partyId,"year",year));
	            

	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        if (employeeYearlyStatus.isEmpty()){
	        	request.setAttribute("_ERROR_MESSAGE_", "There is no data");
	            return "error";
	        } 
	 
	       request.setAttribute("year", year); 
	       request.setAttribute("partyId", partyId); 
	 return "success";
	 }
	
}
