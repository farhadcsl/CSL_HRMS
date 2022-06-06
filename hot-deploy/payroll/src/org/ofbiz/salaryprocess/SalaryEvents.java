package org.ofbiz.salaryprocess;

import javolution.util.FastList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class SalaryEvents {
	 public static String viewMonthlyYearlySalaryList(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        String fiscalYear= "";
	        String monthNumber = "";
	        if (null != request.getParameter("monthNumber")) {
	            monthNumber = request.getParameter("monthNumber");
	        }
	        if (monthNumber.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Month can not be.......empty");
	            return "error";
	        }
	        if (null != request.getParameter("fiscalYear")) {
	        	fiscalYear = request.getParameter("fiscalYear");
	        }
	        if (fiscalYear.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "year can not be empty");
	            return "error";
	        }
	       
	       
	  
	      List<GenericValue> eligibleEmployeeList = null;
	        try {
	        	eligibleEmployeeList = delegator.findByAnd("EligibleEmployee",
	                    UtilMisc.toMap("monthNumber", monthNumber,"fiscalYear", fiscalYear, "isActive", "N" ));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String processId = "";
	        if (UtilValidate.isNotEmpty(eligibleEmployeeList)) {
	            if (UtilValidate.isNotEmpty(eligibleEmployeeList.get(0).get("processId"))) {
	            	processId = eligibleEmployeeList.get(0).get("processId").toString();
	            }
	        }
	       
            request.setAttribute("fiscalYear", fiscalYear);
            request.setAttribute("monthNumber", monthNumber );
            request.setAttribute("processId", processId );
           
	        return "success";
	 }
	
	 public static String salaryProcessStatusChange(HttpServletRequest request, HttpServletResponse response) {     
	
	     Delegator delegator = (Delegator) request.getAttribute("delegator");
	     LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	     DispatchContext context = dispatcher.getDispatchContext();
	     String fiscalYear= "";
	     String monthNumber = "";
	     if (null != request.getParameter("monthNumber")) {
	         monthNumber = request.getParameter("monthNumber");
	     }
	     if (monthNumber.equals("")) {
	         request.setAttribute("_ERROR_MESSAGE_", "Month can not be.......empty");
	         return "error";
	     }
	     if (null != request.getParameter("fiscalYear")) {
	    	 fiscalYear = request.getParameter("fiscalYear");
	     }
	     if (fiscalYear.equals("")) {
	         request.setAttribute("_ERROR_MESSAGE_", "Year can not be.......empty");
	         return "error";
	     }

	
	     String isActive = request.getParameter("isActive");
	     String companyId = request.getParameter("companyId");
	     String partyId = request.getParameter("partyId");
	     
	     String processId = request.getParameter("processId");
	     
	     SalaryProcessModification(request,partyId,companyId,processId,monthNumber,fiscalYear,isActive);     
	     return "Success";
	 }
	
	 public static String salaryProcessStatusChangeForAll(HttpServletRequest request, HttpServletResponse response) {     
	
	     Delegator delegator = (Delegator) request.getAttribute("delegator");
	     LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	     DispatchContext context = dispatcher.getDispatchContext();
	     String fiscalYear= "";
	     String monthNumber = "";
	    if (null != request.getParameter("monthNumber")) {
	         monthNumber = request.getParameter("monthNumber");
	     }
	     if (monthNumber.equals("")) {
	         request.setAttribute("_ERROR_MESSAGE_", "Month can not be.......empty");
	         return "error";
	     }
	     if (null != request.getParameter("fiscalYear")) {
	    	 fiscalYear = request.getParameter("fiscalYear");
	     }
	     if (fiscalYear.equals("")) {
	         request.setAttribute("_ERROR_MESSAGE_", "Year can not be.......empty");
	         return "error";
	     }
		List<EntityExpr> exprsForEmployeeList = FastList.newInstance();
		exprsForEmployeeList.add(EntityCondition.makeCondition("monthNumber", EntityOperator.EQUALS,monthNumber));  
		exprsForEmployeeList.add(EntityCondition.makeCondition("fiscalYear", EntityOperator.EQUALS,fiscalYear)); 
		exprsForEmployeeList.add(EntityCondition.makeCondition("isActive", EntityOperator.EQUALS,"N"));        
		
		List<GenericValue> eligibleEmployeeList = null;
		try {
			eligibleEmployeeList = delegator.findList("EligibleEmployee", EntityCondition.makeCondition(exprsForEmployeeList, EntityOperator.AND), null, null, null, false);

			for (GenericValue rowData : eligibleEmployeeList){
				String partyId = rowData.get("partyId").toString();
				String companyId = rowData.get("companyId").toString();
				String processId = rowData.get("processId").toString();
			    try {
				     SalaryProcessModification(request,partyId,companyId,processId,monthNumber,fiscalYear,"N");  
			    }catch (Exception e) { 
		            request.setAttribute("_ERROR_MESSAGE_", "Error occer for the employee "+partyId);
		            return "Error";
			    }

			}
		}
		catch(Exception e){}
		
	     String companyId = request.getParameter("companyId");	     
	     String partyId = request.getParameter("partyId");
		 String isActive = request.getParameter("isActive");	     
	     String processId = request.getParameter("processId");	     
	     SalaryProcessModification(request,partyId,companyId,processId,monthNumber,fiscalYear,isActive);     
	     return "Success";
	 }
	 
	 public static String SalaryProcessModification( HttpServletRequest request,String partyId,String companyId,String processId,String monthNumber,String fiscalYear,String isActive){
	
	     Delegator delegator = (Delegator) request.getAttribute("delegator");
	
	     try{
			 if (isActive == null){
				 isActive = "Y";
			 }
			 if (isActive.equalsIgnoreCase("N")){
				 isActive = "Y";
			 }
		     List<GenericValue> salaryList = null;
		     GenericValue employee = null;
		     try {
		     	salaryList = delegator.findByAnd("EligibleEmployee", UtilMisc.toMap("companyId", companyId, "monthNumber", monthNumber, "fiscalYear", fiscalYear, "processId", processId, "partyId", partyId));
		         employee = EntityUtil.getFirst(salaryList);
		
		     } catch (GenericEntityException e) {
		         e.printStackTrace();
		     }
		     employee.set("isActive", isActive);
		     try {
		         delegator.store(employee);
		     } catch (GenericEntityException e) {
		         e.printStackTrace();
		     }
	     }
	     catch(Exception e){}
	     return "Success"; 
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
	        String isActive = "";
	        if (null != request.getParameter("isActive")) {
	            monthNumber = request.getParameter("isActive");
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
	        String partyId = "";
	        if (UtilValidate.isNotEmpty(partyList)) {
	            if (UtilValidate.isNotEmpty(partyList.get(0).get("partyId"))) {
	            	partyId = partyList.get(0).get("partyId").toString();
	            }
	        }
	       
	        request.setAttribute("partyId", partyId);
	        request.setAttribute("processId", processId);
	        request.setAttribute("monthNumber", monthNumber);
	        request.setAttribute("isActive", "N");
		 return "success";
	 }
	
}