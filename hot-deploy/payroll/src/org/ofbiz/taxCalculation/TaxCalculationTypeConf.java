package org.ofbiz.taxCalculation;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;

public class TaxCalculationTypeConf {
	 public static String createTaxCalculationTypeSetup(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
	        
	      
	      String taxCalculationTypeId = delegator.getNextSeqId("taxCalculationTypeId");
	      String comments = request.getParameter("comments");
	      String taxCalculationTypeTitle=request.getParameter("taxCalculationTypeTitle");
	      if (null != request.getParameter("taxCalculationTypeTitle")) {
	    	  taxCalculationTypeTitle = request.getParameter("taxCalculationTypeTitle");
	        }
	        if (taxCalculationTypeTitle.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Tax payer group name can not be empty");
	            return "error";
	        }
	      	     
			Map<String, Object> taxCalculationType = FastMap.newInstance();
			taxCalculationType.put("taxCalculationTypeId", taxCalculationTypeId);
			taxCalculationType.put("taxCalculationTypeTitle", taxCalculationTypeTitle);
			taxCalculationType.put("isActive", "Y");
			taxCalculationType.put("dateAdded", currentDatetime);
			taxCalculationType.put("comments", comments);
    	    try {
    			GenericValue taxCalculationTypeSetup = delegator
    				.makeValue("TaxCalculationType",
    						UtilMisc.toMap(taxCalculationType));
    			taxCalculationTypeSetup.create();  
    		    request.setAttribute("_EVENT_MESSAGE_", "Tax payer group create successfully");
    	    } catch (Exception e) { }

	        return "success";
	    }
	 
	 public static String updateTaxCalculationTypeSetup(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
	        
	      
	      String taxCalculationTypeId = delegator.getNextSeqId("taxCalculationTypeId");
	      String comments = request.getParameter("comments");
	      String taxCalculationTypeTitle=request.getParameter("taxCalculationTypeTitle");
	      if (null != request.getParameter("taxCalculationTypeTitle")) {
	    	  taxCalculationTypeTitle = request.getParameter("taxCalculationTypeTitle");
	        }
	        if (taxCalculationTypeTitle.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Tax payer group name can not be empty");
	            return "error";
	        }
	      	     
			Map<String, Object> taxCalculationType = FastMap.newInstance();
			taxCalculationType.put("taxCalculationTypeId", taxCalculationTypeId);
			taxCalculationType.put("taxCalculationTypeTitle", taxCalculationTypeTitle);
			taxCalculationType.put("isActive", "Y");
			taxCalculationType.put("dateAdded", currentDatetime);
			taxCalculationType.put("comments", comments);
 	    try {
 			GenericValue taxCalculationTypeSetup = delegator
 				.makeValue("TaxCalculationType",
 						UtilMisc.toMap(taxCalculationType));
 			taxCalculationTypeSetup.store();  
 		    request.setAttribute("_EVENT_MESSAGE_", "Tax payer group updated successfully");
 	    } catch (Exception e) { }

	        return "success";
	    }

	  
}