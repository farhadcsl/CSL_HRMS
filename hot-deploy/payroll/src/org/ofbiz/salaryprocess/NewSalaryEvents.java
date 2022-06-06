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

public class NewSalaryEvents {
	 public static String newSalaryPrepareList(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();

	        String monthNumber = "";
	        String companyId = "";
	        String description = "";
	        String year;
	        String bonusCheck ="";
	        String arrearCheck ="";
	        if (null != request.getParameter("monthNumber")) {
	            monthNumber = request.getParameter("monthNumber");
	        }
	        if (null != request.getParameter("partyIdFrom")) {
	            companyId = request.getParameter("partyIdFrom");
	        }
	        if (null != request.getParameter("bonusCheck")) {
	        	bonusCheck = request.getParameter("bonusCheck");
	        }
	        if (null != request.getParameter("arrearCheck")) {
	        	arrearCheck = request.getParameter("arrearCheck");
	        }
	        if (null != request.getParameter("monthDescription")) {
	            description = request.getParameter("monthDescription");
	            description = description.replace('+', ' ');
	        }
	        if (monthNumber.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Month can not be empty");
	            return "error";
	        }
	        year = SalaryUtils.getFiscalYear(delegator, companyId);
	        if (year.equals("error")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Fiscal year not set for this company");
	            return "error";
	        }
	        
	        String companyName = SalaryUtils.getCompanyName(delegator, companyId);
	        
	        boolean check = SalaryUtils.checkPendingSalaryProcessExist(delegator, companyId, monthNumber, year);
	        if (check) {
	            request.setAttribute("_ERROR_MESSAGE_", "Salary Process already exist for this month.");
	            return "error";
	        }
	        boolean viewExist = NewSalaryServices.checkViewExist(delegator, companyId, monthNumber, year);
	        //try to use this if loop or delete later
	        if (viewExist) {
	            List<GenericValue> eligiblePayeeGenericValue = null;
	            List<String> orderBy = UtilMisc.toList("employeeId");


	            List condition1 = FastList.newInstance();
	            condition1.add(EntityCondition.makeCondition("companyId", EntityOperator.EQUALS, companyId));
	            condition1.add(EntityCondition.makeCondition("monthNumber", EntityOperator.EQUALS, monthNumber));
	            condition1.add(EntityCondition.makeCondition("fiscalYear", EntityOperator.EQUALS, year));
	            EntityCondition cond1 = EntityCondition.makeCondition(condition1, EntityOperator.AND);

	            try {
	                eligiblePayeeGenericValue = delegator.findList("EligibleEmployee", cond1, null, orderBy, null, false);
	            } catch (GenericEntityException e) {
	                e.printStackTrace();
	            }
	            String processId = EntityUtil.getFirst(eligiblePayeeGenericValue).getString("processId");
	            GenericValue existingProcess = null; 
	            try{
	            	existingProcess = delegator.findByPrimaryKey("MonthlyTotalSalary", UtilMisc.toMap("processId", processId));
	            } catch (GenericEntityException e) {
	                e.printStackTrace();
	            }
	            String newTotalSalary = existingProcess.getString("totalSalary");
	            List<Map> eligiblePayeeListMap = FastList.newInstance();
	            for (Map aParsed : eligiblePayeeGenericValue) {
	                Map record = FastMap.newInstance();
	                record.putAll(aParsed);
	                if (record.get("isSalary").equals("Y")) {
	                    record.put("isSalary", "Y");
	                } else {
	                    record.put("isSalary", "N");
	                }
	                eligiblePayeeListMap.add(record);
	            }

	            request.setAttribute("eligiblePayeeListMap", eligiblePayeeListMap);
	            request.setAttribute("eligiblePayeeListSize", eligiblePayeeGenericValue.size());
	            request.setAttribute("monthName", SalaryUtils.getMonthName(EntityUtil.getFirst(eligiblePayeeGenericValue).getString("monthNumber")));
	            request.setAttribute("company", companyName);
	            //request.setAttribute("description", EntityUtil.getFirst(eligiblePayeeGenericValue).getString("description"));
	            request.setAttribute("year", year);
	            request.setAttribute("newTotalSalaryAmount", newTotalSalary);


	        }else {
	        	 List<Map<String, String>> eligiblePayeeListMap = NewSalaryServices.getElligiblePayeeListMap(delegator, companyId, monthNumber, year, bonusCheck, arrearCheck);
			}
	        
	        return "success";
	    }
	 
	 public static String viewMonthlySalaryList(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        String year;
	        String monthNumber = "";
	        if (null != request.getParameter("monthNumber")) {
	            monthNumber = request.getParameter("monthNumber");
	        }
	        if (monthNumber.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Month can not be empty");
	            return "error";
	        }
	        String companyId = "CSL";
	        String companyName = SalaryUtils.getCompanyName(delegator, companyId);
	        year = SalaryUtils.getFiscalYear(delegator, companyId);
/*

*
*
*Please check for process ID later
*
*
*/
	        boolean check = SalaryUtils.checkPendingSalaryProcessExist(delegator, companyId, monthNumber, year);
	        if (!check) {
	            request.setAttribute("_ERROR_MESSAGE_", "Salary Process Not exist for this month. Please Inititate");
	            return "error";
	        }
	        
	        List<GenericValue> monthlySalary = null;
	        try {
	        	monthlySalary = delegator.findByAnd("MonthlyTotalSalary",
	                    UtilMisc.toMap("monthNumber", monthNumber,"fiscalYear", year ));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        // Extra
	        List<GenericValue> eligibleEmployeeList = null;
	        try {
	        	eligibleEmployeeList = delegator.findByAnd("EligibleEmployee",
	                    UtilMisc.toMap("monthNumber", monthNumber,"fiscalYear", year, "isActive", "Y" ));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        int totalListEmplint = eligibleEmployeeList.size();
	        double MonthNetPay = 0;
	        for (GenericValue totalSalaryEmplList : eligibleEmployeeList) {
	        	MonthNetPay += Double.parseDouble(totalSalaryEmplList.get("totalSalary").toString());
	        }
	        BigDecimal finalMonthlyNetPay = new BigDecimal(MonthNetPay);
	        //================end=================================
	        String processId = null;
	        if (UtilValidate.isNotEmpty(monthlySalary)) {
	            if (UtilValidate.isNotEmpty(monthlySalary.get(0).get("processId"))) {
	            	processId = monthlySalary.get(0).get("processId").toString();
	            }
	        }
	        if (UtilValidate.isNotEmpty(monthlySalary)) {
	            if (UtilValidate.isNotEmpty(monthlySalary.get(0).get("totalEmployee"))) {
	            	String totalEmployee = monthlySalary.get(0).get("totalEmployee").toString();
	            }
	        }
	        if (UtilValidate.isNotEmpty(monthlySalary)) {
	            if (UtilValidate.isNotEmpty(monthlySalary.get(0).get("totalSalary"))) {
	            String totalSalary = monthlySalary.get(0).get("totalSalary").toString();
	            }
	        }
	       
	        request.setAttribute("processId", processId);
            request.setAttribute("year", year);
            request.setAttribute("monthName", SalaryUtils.getMonthName(monthNumber) );
            request.setAttribute("finalMonthlyNetPay", finalMonthlyNetPay);
            request.setAttribute("totalListEmplint", totalListEmplint);
            /*request.setAttribute("totalEmployee", totalEmployee);*/
	        return "success";
	 }
	 
	 public static String findFromMonthlySalaryList(HttpServletRequest request, HttpServletResponse response) {
		 Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        String employeeID = "";
	        if (null != request.getParameter("employeeId")) {
	        	employeeID = request.getParameter("employeeId");
	        }
	        String monthNumber = "";
	        if (null != request.getParameter("monthNumber")) {
	            monthNumber = request.getParameter("monthNumber");
	        }
	        
	        String processId = "";
	        if (null != request.getParameter("processId")) {
	        	processId = request.getParameter("processId");
	        }
	        List<GenericValue> partyList = null;
	        try {
	        	partyList = delegator.findByAnd("UserLogin",
	                    UtilMisc.toMap("userLoginId", employeeID));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String partyId = null;
	        if (UtilValidate.isNotEmpty(partyList)) {
	            if (UtilValidate.isNotEmpty(partyList.get(0).get("partyId"))) {
	            	partyId = partyList.get(0).get("partyId").toString();
	            }
	        }
	       
	        request.setAttribute("partyId", partyId);
	        request.setAttribute("processId", processId);
		 return "success";
	 }
	 
 public static Map<String, Object> updateSalaryList(DispatchContext dctx, Map<String, ? extends Object> context) {
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        Delegator delegator = dctx.getDelegator();
	        Map<String, Object> result = ServiceUtil.returnSuccess();

	       String isSalary = (String) context.get("isSalary");
	        String companyId = (String) context.get("companyId");
	        String monthNumber = (String) context.get("monthNumber");
	        String partyId = (String) context.get("partyId");
	        
	        String processId = (String) context.get("processId");
		 if (isSalary == null){
			 isSalary = "N";
		 }
		 if (isSalary.equalsIgnoreCase("N")){
			 isSalary = "N";
		 }
	        List<GenericValue> salaryList = null;
	        GenericValue employee = null;
	        try {
	        	salaryList = delegator.findByAnd("EligibleEmployee", UtilMisc.toMap("companyId", companyId, "monthNumber", monthNumber,"processId",processId, "partyId", partyId));
	            employee = EntityUtil.getFirst(salaryList);

	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        employee.set("isSalary", isSalary);
	        try {
	            delegator.store(employee);
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        
	        return ServiceUtil.returnSuccess();
	    }
 public static String removeFromSalaryList(HttpServletRequest request, HttpServletResponse response) {
	 Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        String partyId = "";
        if (null != request.getParameter("partyId")) {
        	partyId = request.getParameter("partyId");
        }
        String monthNumber = "";
        if (null != request.getParameter("monthNumber")) {
            monthNumber = request.getParameter("monthNumber");
        }
        
        String processId = "";
        if (null != request.getParameter("processId")) {
        	processId = request.getParameter("processId");
        }
        String year = "";
        if (null != request.getParameter("year")) {
        	year = request.getParameter("year");
        }
        String companyId = "";
        if (null != request.getParameter("companyId")) {
        	companyId = request.getParameter("companyId");
        }
        List<GenericValue> eligibleList = null;
        GenericValue employee = null;
        try {
        	eligibleList = delegator.findByAnd("EligibleEmployee", UtilMisc.toMap("companyId", companyId, "monthNumber", monthNumber,"processId",processId, "partyId", partyId));
            employee = EntityUtil.getFirst(eligibleList);

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        
        
        employee.set("isActive", "N");
        try {
            delegator.store(employee);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
       
        request.setAttribute("monthNumber", monthNumber);
        request.setAttribute("processId", processId);
	 return "success";
 }
 
 
 
 public static Map<String, Object> findActiveEmployee(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		String orgPartyId = (String) context.get("orgPartyId");
		String month = (String) context.get("month");
		String year = (String) context.get("fiscalYear");
		String unitParyId=orgPartyId;
		Map<String, String> totalInfo = FastMap.newInstance();
		String employmentType= (String) context.get("employmentType");
		// filter on division
		String division = (String) context.get("division");
		if (UtilValidate.isNotEmpty(division)) {
			unitParyId = division;
		}

		// filter on department
		String department = (String) context.get("department");
		if (UtilValidate.isNotEmpty(department)) {
			unitParyId = department;

		}

		// filter on section
		String section = (String) context.get("section");
		if (UtilValidate.isNotEmpty(section)) {
			unitParyId = section;
		}
		// filter on line
		String line = (String) context.get("line");
		if (UtilValidate.isNotEmpty(line)) {
			unitParyId = line;
		}
		
		List<GenericValue> employeeList = FastList.newInstance();
		
		

		 try {
			 employeeList = delegator.findByAnd("EmployeeSalaryFinal", UtilMisc.toMap("monthNumber",month ,"fiscalYear","2015"));
	           
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
		 
 
		result.put("absenceList", employeeList);	
		
		return result;
	}
 
 
	
}