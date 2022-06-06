package org.ofbiz.humanres.contessaPerfAppr;

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

public class PerfApprConfig {	
     public static String createPerfType(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
	        
	      
	      String perfApprTypeId = delegator.getNextSeqId("PerfApprType");
	      String description = request.getParameter("description");
	      String perfApprTypeName=request.getParameter("perfApprTypeName");
	      if (null != request.getParameter("perfApprTypeName")) {
	    	  perfApprTypeName = request.getParameter("perfApprTypeName");
	        }
	        if (perfApprTypeName.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "performance Appriasal Type Name can not be empty");
	            return "error";
	        }
	      	     
			Map<String, Object> perfType = FastMap.newInstance();
			perfType.put("perfApprTypeId", perfApprTypeId);
			perfType.put("perfApprTypeName", perfApprTypeName);
			perfType.put("isActive", "Y");
			perfType.put("dateAdded", currentDatetime);
			perfType.put("description", description);
   	    try {
   			GenericValue perfTypeSetup = delegator
   				.makeValue("PerfApprType",
   						UtilMisc.toMap(perfType));
   			perfTypeSetup.create();  
   		    request.setAttribute("_EVENT_MESSAGE_", "Performance Appraisal Type Create Successfully");
   	    } catch (Exception e) { }

	        return "success";
	    }

	 public static String createPerfObjectiveType(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
	        
	      
	      String objectiveTypeId = delegator.getNextSeqId("ObjectiveType");
	      String description = request.getParameter("description");
	      String objectiveTypeName=request.getParameter("objectiveTypeName");
	      if (null != request.getParameter("objectiveTypeName")) {
	    	  objectiveTypeName = request.getParameter("objectiveTypeName");
	        }
	        if (objectiveTypeName.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Performance Appraisal ObjectiveType Name can not be empty");
	            return "error";
	        }
	      	     
			Map<String, Object> perfType = FastMap.newInstance();
			perfType.put("objectiveTypeId", objectiveTypeId);
			perfType.put("objectiveTypeName", objectiveTypeName);
			perfType.put("isActive", "Y");
			perfType.put("dateAdded", currentDatetime);
			perfType.put("description", description);
    	    try {
    			GenericValue perfTypeSetup = delegator
    				.makeValue("ObjectiveType",
    						UtilMisc.toMap(perfType));
    			perfTypeSetup.create();  
    		    request.setAttribute("_EVENT_MESSAGE_", "Performance Appraisal Objective Type Create Successfully");
    	    } catch (Exception e) { }

	        return "success";
	    }

	 public static String createPerfKpiType(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
	        
	      
	      String perfApprKpiId = delegator.getNextSeqId("PerfApprKpi");
	      String description = request.getParameter("description");
	      String perfApprKpiName=request.getParameter("perfApprKpiName");
	      if (null != request.getParameter("perfApprKpiName")) {
	    	  perfApprKpiName = request.getParameter("perfApprKpiName");
	        }
	        if (perfApprKpiName.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Performance Appraisal KPI Name can not be empty");
	            return "error";
	        }
	      	     
			Map<String, Object> perfKpiType = FastMap.newInstance();
			perfKpiType.put("perfApprKpiId", perfApprKpiId);
			perfKpiType.put("perfApprKpiName", perfApprKpiName);
			perfKpiType.put("isActive", "Y");
			perfKpiType.put("dateAdded", currentDatetime);
			perfKpiType.put("description", description);
    	    try {
    			GenericValue perfKpiTypeSetup = delegator
    				.makeValue("PerfApprKpi",
    						UtilMisc.toMap(perfKpiType));
    			perfKpiTypeSetup.create();  
    		    request.setAttribute("_EVENT_MESSAGE_", "Performance Appraisal KPI Create Successfully");
    	    } catch (Exception e) { }

	        return "success";
	    }

	 public static String createPerfDevReqType(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
	        
	      
	      String perfApprDevReqId = delegator.getNextSeqId("PerfApprDevReq");
	      String description = request.getParameter("description");
	      String perfApprDevReqName=request.getParameter("perfApprDevReqName");
	      if (null != request.getParameter("perfApprDevReqName")) {
	    	  perfApprDevReqName = request.getParameter("perfApprDevReqName");
	        }
	        if (perfApprDevReqName.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Performance Appraisal Development Requirement Name can not be empty");
	            return "error";
	        }
	      	     
			Map<String, Object> perfDevReqType = FastMap.newInstance();
			perfDevReqType.put("perfApprDevReqId", perfApprDevReqId);
			perfDevReqType.put("perfApprDevReqName", perfApprDevReqName);
			perfDevReqType.put("isActive", "Y");
			perfDevReqType.put("dateAdded", currentDatetime);
			perfDevReqType.put("description", description);
    	    try {
    			GenericValue perfDevReqTypeSetup = delegator
    				.makeValue("PerfApprDevReq",
    						UtilMisc.toMap(perfDevReqType));
    			perfDevReqTypeSetup.create();  
    		    request.setAttribute("_EVENT_MESSAGE_", "Performance Appraisal Development Requirement Create Successfully");
    	    } catch (Exception e) { }

	        return "success";
	    }

	 public static String createPerfReviewType(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
	        
	      
	      String perfReviewTypeId = delegator.getNextSeqId("PerfReviewType");
	      String comments = request.getParameter("comments");
	      String behaviorIndicators = request.getParameter("behaviorIndicators");
	      String perfReviewTypeName=request.getParameter("perfReviewTypeName");
	      if (null != request.getParameter("perfReviewTypeName")) {
	    	  perfReviewTypeName = request.getParameter("perfReviewTypeName");
	        }
	        if (perfReviewTypeName.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Performance Appraisal Review Name can not be empty");
	            return "error";
	        }
	      	     
			Map<String, Object> perfReviewType = FastMap.newInstance();
			perfReviewType.put("perfReviewTypeId", perfReviewTypeId);
			perfReviewType.put("perfReviewTypeName", perfReviewTypeName);
			perfReviewType.put("isActive", "Y");
			perfReviewType.put("dateAdded", currentDatetime);
			perfReviewType.put("comments", comments);
			perfReviewType.put("behaviorIndicators", behaviorIndicators);
    	    try {
    			GenericValue perfReviewTypeSetup = delegator
    				.makeValue("PerfReviewType",
    						UtilMisc.toMap(perfReviewType));
    			perfReviewTypeSetup.create();  
    		    request.setAttribute("_EVENT_MESSAGE_", "Performance Appraisal Review Type Create Successfully");
    	    } catch (Exception e) { }

	        return "success";
	    }

	 public static String createPerfRatingType(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
	      
	        
	      String perfApRatingTypeId = delegator.getNextSeqId("PerfApRatingType");
	      String comments = request.getParameter("comments");
	      String perfApRatingTypeName=request.getParameter("perfApRatingTypeName");
	      String perfApRatingScore=request.getParameter("perfApRatingScore");
	      if (null != request.getParameter("perfApRatingTypeName")) {
	    	  perfApRatingTypeName = request.getParameter("perfApRatingTypeName");
	        }
	        if (perfApRatingTypeName.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "performance Appraisal Rating Name can not be empty");
	            return "error";
	        }
	        
	      if (null != request.getParameter("perfApRatingScore")) {
	    	  perfApRatingScore = request.getParameter("perfApRatingScore");
	          }
	          if (perfApRatingScore.equals("")) {
	              request.setAttribute("_ERROR_MESSAGE_", "Performance Appraisal Rating Score can not be empty");
	              return "error";
	          }  
	      	     
			Map<String, Object> perfRatingType = FastMap.newInstance();
			perfRatingType.put("perfApRatingTypeId", perfApRatingTypeId);
			perfRatingType.put("perfApRatingTypeName", perfApRatingTypeName);
			perfRatingType.put("perfApRatingScore", perfApRatingScore);
			perfRatingType.put("isActive", "Y");
			perfRatingType.put("dateAdded", currentDatetime);
			perfRatingType.put("comments", comments);
		    try {
				GenericValue perfRatingTypeSetup = delegator
					.makeValue("PerfApRatingType",
							UtilMisc.toMap(perfRatingType));
				perfRatingTypeSetup.create();  
			    request.setAttribute("_EVENT_MESSAGE_", "Performance Appraisal Rating Type Create Successfully");
		    } catch (Exception e) { }

	        return "success";
	    }

	 public static String createPerfApprApproverAssign(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
	        
	        
	        String employeeID = "";
	        if (null != request.getParameter("employeeId")) {
	        	employeeID = request.getParameter("employeeId");
	        }
	        if (employeeID.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Employee can not be empty");
	            return "error";
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
	        
			String firstLevelApprover = ""; 
	        if (null != request.getParameter("firstLevelApprover")) {
	        	firstLevelApprover = request.getParameter("firstLevelApprover");
	        }
	        if (firstLevelApprover.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "First Level Approver Field can not be empty");
	            return "error";
	        }
	        try {
	        	partyList = delegator.findByAnd("UserLogin",
	                    UtilMisc.toMap("userLoginId", firstLevelApprover));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String firstLevelApproverId = null;
	        if (UtilValidate.isNotEmpty(partyList)) {
	            if (UtilValidate.isNotEmpty(partyList.get(0).get("partyId"))) {
	            	firstLevelApproverId = partyList.get(0).get("partyId").toString();
	            }
	        }
	        
	        
			String secondLevelApprover="";
	        if (null != request.getParameter("secondLevelApprover")) {
	        	secondLevelApprover = request.getParameter("secondLevelApprover");
	        }
	        if (secondLevelApprover.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Second Level Approver Field can not be empty");
	            return "error";
	        }
	        try {
	        	partyList = delegator.findByAnd("UserLogin",
	                    UtilMisc.toMap("userLoginId", secondLevelApprover));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String secondLevelApproverId = null;
	        if (UtilValidate.isNotEmpty(partyList)) {
	            if (UtilValidate.isNotEmpty(partyList.get(0).get("partyId"))) {
	            	secondLevelApproverId = partyList.get(0).get("partyId").toString();
	            }
	        }
	        
	        
	        

			String thirdLevelApprover="";
	        if (null != request.getParameter("thirdLevelApprover")) {
	        	thirdLevelApprover = request.getParameter("thirdLevelApprover");
	        }
	        try {
	        	partyList = delegator.findByAnd("UserLogin",
	                    UtilMisc.toMap("userLoginId", thirdLevelApprover));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String thirdLevelApproverId = null;
	        if (UtilValidate.isNotEmpty(partyList)) {
	            if (UtilValidate.isNotEmpty(partyList.get(0).get("partyId"))) {
	            	thirdLevelApproverId = partyList.get(0).get("partyId").toString();
	            }
	        }
		
	        
	        
	        
			String description=request.getParameter("description");
			Map<String, Object> perfApproverType = FastMap.newInstance();
			perfApproverType.put("partyId", partyId);
			perfApproverType.put("employeeId", employeeID);
			perfApproverType.put("firstLevelApprover", firstLevelApproverId);
			perfApproverType.put("secondLevelApprover", secondLevelApproverId);
			perfApproverType.put("thirdLevelApprover", thirdLevelApproverId);
			perfApproverType.put("isActive", "Y");
			perfApproverType.put("description", description);
			perfApproverType.put("dateAdded", currentDatetime);
		    try {
				GenericValue perfApproverTypeSetup = delegator
					.makeValue("PartyPerfApprWorkflow",
							UtilMisc.toMap(perfApproverType));
				perfApproverTypeSetup.create();  
			    request.setAttribute("_EVENT_MESSAGE_", "Performance Appraisal Approver Assign Set Successfully");
		    } catch (Exception e) { 
	            request.setAttribute("_ERROR_MESSAGE_", "Performance Appraisal Approver Assign Can'nt Set");
		    }
	    
			return "success";
	    }

	
}