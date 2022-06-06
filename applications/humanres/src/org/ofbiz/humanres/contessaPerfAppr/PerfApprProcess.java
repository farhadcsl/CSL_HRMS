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
//import org.python.core.exceptions;

public class PerfApprProcess {	
	
	public static String createInitiatePerformanceAppriasal(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
        
      
		String processId = delegator.getNextSeqId("PerfApprProcess");
		String partyId = ""; 
        if (null != request.getParameter("partyId")) {
        	partyId = request.getParameter("partyId");
        }

        if (partyId.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Employee can not be empty");
            return "error";
        }
		String perfApprTypeId="";
        if (null != request.getParameter("perfApprTypeId")) {
        	perfApprTypeId = request.getParameter("perfApprTypeId");
        }
        if (partyId.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Performance Appraisal Type can not be empty");
            return "error";
        }
		String title = ""; 
        if (null != request.getParameter("title")) {
        	title = request.getParameter("title");
        }
        if (title.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Title can not be empty");
            return "error";
        }

		String startDateStr = ""; 
        Timestamp startDate = new Timestamp(System.currentTimeMillis());		
        if (null != request.getParameter("startDate")) {
        	startDateStr = request.getParameter("startDate");
        	try{
        		startDate = Timestamp.valueOf(startDateStr);
        	}
        	catch(Exception e){
                request.setAttribute("_ERROR_MESSAGE_", "Start Date Data Format Not Correct");
        	}
        }
        if (startDate.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Start Date can not be empty");
            return "error";
        }
		String endDateStr = ""; 
        Timestamp endDate = new Timestamp(System.currentTimeMillis());		
        if (null != request.getParameter("endDate")) {
        	endDateStr = request.getParameter("endDate");
        	try{
        		endDate = Timestamp.valueOf(endDateStr);
        	}
        	catch(Exception e){
                request.setAttribute("_ERROR_MESSAGE_", "End Date Data Format Not Correct");
        	}
        }
        if (endDate.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "End Date can not be empty");
            return "error";
        }
        
        
		String description=request.getParameter("description");
      	     
		Map<String, Object> perfType = FastMap.newInstance();
		perfType.put("processId", processId);
		perfType.put("partyId", partyId);
		perfType.put("perfApprTypeId", perfApprTypeId);
		perfType.put("startDate", startDate);
		perfType.put("endDate", endDate);
		perfType.put("title", title);
		perfType.put("description", description);
		perfType.put("statusId", "Create");
		perfType.put("isActive", "Y");
		perfType.put("dateAdded", currentDatetime);
	    try {
			GenericValue perfTypeSetup = delegator
				.makeValue("PerfApprProcess",
						UtilMisc.toMap(perfType));
			perfTypeSetup.create();  
		    request.setAttribute("_EVENT_MESSAGE_", "Performance Appraisal Create Successfully");
	    } catch (Exception e) { 
            request.setAttribute("_ERROR_MESSAGE_", "Performance Appraisal Not Added");
	    }

        request.setAttribute("processId", processId);
        request.setAttribute("partyId", partyId);
        request.setAttribute("perfApprTypeId", perfApprTypeId);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("title", title);
        request.setAttribute("description", description);
        request.setAttribute("statusId", "Create");
        request.setAttribute("isActive", "");
        request.setAttribute("dateAdded", currentDatetime);
        return "success";
    }
	
	public static String createPerfApprBusinessKPI(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
        
      
		String dataId = delegator.getNextSeqId("PerfApprBusinessKpi");
		String processId = ""; 
        if (null != request.getParameter("processId")) {
        	processId = request.getParameter("processId");
        }

        if (processId.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Process Id can not be empty");
            return "error";
        }
		String objectiveDesc="";
        if (null != request.getParameter("objectiveDesc")) {
        	objectiveDesc = request.getParameter("objectiveDesc");
        }
        if (objectiveDesc.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Objective Description can not be empty");
            return "error";
        }
		String perfIndicator = ""; 
        if (null != request.getParameter("perfIndicator")) {
        	perfIndicator = request.getParameter("perfIndicator");
        }
        if (perfIndicator.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Performance Indicator can not be empty");
            return "error";
        }
		String ratingId = ""; 
        if (null != request.getParameter("ratingId")) {
        	ratingId = request.getParameter("ratingId");
        }
        if (ratingId.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Rating can not be empty");
            return "error";
        }
		String objectiveTypeId = ""; 
        if (null != request.getParameter("objectiveTypeId")) {
        	objectiveTypeId = request.getParameter("objectiveTypeId");
        }
        if (objectiveTypeId.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Objective Type can not be empty");
            return "error";
        }
		String achivementPercentageStr = ""; 
		Double achivementPercentage=0.0;
        if (null != request.getParameter("achivementPercentage")) {
        	achivementPercentageStr = request.getParameter("achivementPercentage");
        	try{
        		achivementPercentage=Double.valueOf(achivementPercentageStr);
        	}
        	catch(Exception e){
                request.setAttribute("_ERROR_MESSAGE_", "Achivment Percentage Must Be A Number Value");
        	}
        }
        if (achivementPercentageStr.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Achivment Percentage can not be empty");
            return "error";
        }

		String weightStr = ""; 
		Double weight=0.0;
        if (null != request.getParameter("weight")) {
        	weightStr = request.getParameter("weight");
        	try{
        		weight=Double.valueOf(weightStr);
        	}
        	catch(Exception e){
                request.setAttribute("_ERROR_MESSAGE_", "Achivment Percentage Must Be A Number Value");
        	}
        }
        if (weightStr.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Achivment Percentage can not be empty");
            return "error";
        }
        
		String comments=request.getParameter("comments");
		Map<String, Object> perfType = FastMap.newInstance();
		perfType.put("processId", processId);
		perfType.put("dataId", dataId);
		perfType.put("objectiveDesc", objectiveDesc);
		perfType.put("perfIndicator", perfIndicator);
		perfType.put("ratingId", ratingId);
		perfType.put("weight", weight);
		perfType.put("objectiveTypeId", objectiveTypeId);
		perfType.put("achivementPercentage", achivementPercentage);
		perfType.put("comments", comments);
		perfType.put("dateAdded", currentDatetime);
	    try {
			GenericValue perfTypeSetup = delegator
				.makeValue("PerfApprBusinessKpi",
						UtilMisc.toMap(perfType));
			perfTypeSetup.create();  
		    request.setAttribute("_EVENT_MESSAGE_", "Performance Appraisal Business KPI Set Successfully");
	    } catch (Exception e) { 
            request.setAttribute("_ERROR_MESSAGE_", "Performance Appraisal Business KPI Not Added");
	    }
        request.setAttribute("processId", processId);
        request.setAttribute("dataId", dataId);
        request.setAttribute("objectiveDesc", objectiveDesc);
        request.setAttribute("perfIndicator", perfIndicator);
        request.setAttribute("objectiveTypeId", objectiveTypeId);
        request.setAttribute("ratingId", ratingId);
        request.setAttribute("weight", weight);
        request.setAttribute("achivementPercentage", achivementPercentage);
        request.setAttribute("comments", comments);
        request.setAttribute("dateAdded", currentDatetime);
        GenericValue perfApprProcessInfo = null;
		try { 
			perfApprProcessInfo=delegator.findByPrimaryKey("PerfApprProcess", UtilMisc.toMap("processId", processId));
			String perfApprTypeId=perfApprProcessInfo.get("perfApprTypeId").toString();
			String startDate=perfApprProcessInfo.get("startDate").toString();
			String endDate=perfApprProcessInfo.get("endDate").toString();
			String title=perfApprProcessInfo.get("title").toString();
			String description=perfApprProcessInfo.get("description").toString();
			String partyId=perfApprProcessInfo.get("partyId").toString();
			request.setAttribute("perfApprTypeId", perfApprTypeId);
	        request.setAttribute("startDate", startDate);
	        request.setAttribute("endDate", endDate);
	        request.setAttribute("title", title);
	        request.setAttribute("description", description);
		}
		catch(Exception e){
			
		}
		return "success";
    }
	
	public static String SendForProcessPerfAppraisal(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());

		String firstApprovar ="";	
		String secondApprover ="";
		String thirdApprover ="";
        
        
        String processId=request.getParameter("processId").toString();
        GenericValue perfApprProcessInfo = null; 
        try {
        	perfApprProcessInfo = delegator.findByPrimaryKey("PerfApprProcess", UtilMisc.toMap("processId", processId));

			String partyId =perfApprProcessInfo.get("partyId").toString();	
			String perfApprTypeId =perfApprProcessInfo.get("perfApprTypeId").toString();
			String title =perfApprProcessInfo.get("title").toString();	
			String description =perfApprProcessInfo.get("description").toString();
			
			//--------------------------Party Performance Appraisal Workflow Step Person Find-----------------//
	        GenericValue partyPerfApprWorkflowInfo=null;
	        try {
	        	partyPerfApprWorkflowInfo = delegator.findByPrimaryKey("PartyPerfApprWorkflow", UtilMisc.toMap("partyId", partyId));
	        	if (partyPerfApprWorkflowInfo.isEmpty()){
		            request.setAttribute("_ERROR_MESSAGE_", "Employee Does Not Have Any Performance Appraisal Workflow Process !!!");
		            return "error";
	        	}
				firstApprovar =partyPerfApprWorkflowInfo.get("firstLevelApprover").toString();	
				secondApprover =perfApprProcessInfo.get("secondLevelApprover").toString();
				thirdApprover =perfApprProcessInfo.get("thirdLevelApprover").toString();
	        }
	        catch(Exception e){}
	        
			String startDateStr = ""; 
	        Timestamp startDate = new Timestamp(System.currentTimeMillis());		
	        if (null != perfApprProcessInfo.get("startDate").toString()) {
	        	startDateStr = perfApprProcessInfo.get("startDate").toString();
	        	try{
	        		startDate = Timestamp.valueOf(startDateStr);
	        	}
	        	catch(Exception e){
	                request.setAttribute("_ERROR_MESSAGE_", "Start Date Data Format Not Correct");
	        	}
	        }
	        if (startDate.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Start Date can not be empty");
	            return "error";
	        }
			String endDateStr = ""; 
	        Timestamp endDate = new Timestamp(System.currentTimeMillis());		
	        if (null != perfApprProcessInfo.get("endDate").toString()) {
	        	endDateStr = perfApprProcessInfo.get("endDate").toString();
	        	try{
	        		endDate = Timestamp.valueOf(endDateStr);
	        	}
	        	catch(Exception e){
	                request.setAttribute("_ERROR_MESSAGE_", "End Date Data Format Not Correct");
	        	}
	        }
	        if (endDate.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "End Date can not be empty");
	            return "error";
	        }

		    String workflowProcessIdgen = delegator.getNextSeqId("WorkflowProcess");
			Map<String, Object> perfType = FastMap.newInstance();
			perfType.put("processId", processId);
			perfType.put("partyId", partyId);
			perfType.put("perfApprTypeId", perfApprTypeId);
			perfType.put("startDate", startDate);
			perfType.put("endDate", endDate);
			perfType.put("title", title);
			perfType.put("description", description);
			perfType.put("statusId", "Send For Process");
			perfType.put("isActive", "Y");
			perfType.put("dateAdded", currentDatetime);
			perfType.put("stepPartyId", firstApprovar);
			perfType.put("firstApprovarId", firstApprovar);
			perfType.put("secondApprovarId", secondApprover);
			perfType.put("thirdApprovarId", thirdApprover);
			perfType.put("firstApprovarStatus", "0");
			perfType.put("partyStatus", "0");
			perfType.put("secondApprovarStatus", "0");
			perfType.put("thirdApprovarStatus", "0");
			/*perfType.put("firstApprovarComments", "");
			perfType.put("partyComments", "");
			perfType.put("secondApprovarComments", "");
			perfType.put("thirdApprovarComments", "");*/
			perfType.put("workflowProcessId", workflowProcessIdgen);
		    try {
				GenericValue perfTypeSetup = delegator
					.makeValue("PerfApprProcess",
							UtilMisc.toMap(perfType));
				perfTypeSetup.store();  
			    request.setAttribute("_EVENT_MESSAGE_", "Performance Appraisal Send Successfully");
		    } catch (Exception e) { 
	            request.setAttribute("_ERROR_MESSAGE_", "Performance Appraisal Not Send");
		    }
		    
		    /*List<GenericValue> processPartyList = null;
		     try {
		    	 processPartyList = delegator.findByAnd("ProcessParty", UtilMisc.toMap("partyId", partyId, "processTypeId","PerformanceAppraisal", "isActive","Y"));
		     } catch (GenericEntityException e) {
		         e.printStackTrace();
		     }
		     if (processPartyList.isEmpty()){
		    	 request.setAttribute("partyId", partyId);
		    	 request.setAttribute("_ERROR_MESSAGE_", "Sorry! You dont have leave workflow. Contact With your Admin ");
		         return "Error";
		     }
		     String processStrId = null;
		     if (UtilValidate.isNotEmpty(processPartyList)) {
		         if (UtilValidate.isNotEmpty(processPartyList.get(0).get("processStrId"))) {
		         	processStrId = processPartyList.get(0).get("processStrId").toString();
		         }
		     }
		     List<GenericValue> processStepList = null;
		     try {
		    	 processStepList = delegator.findByAnd("ProcessSteps", UtilMisc.toMap("processStrId", processStrId, "stepOrder","1"));
		     } catch (GenericEntityException e) {
		         e.printStackTrace();
		     }
		     if (processStepList.isEmpty()){
		    	 request.setAttribute("partyId", partyId);
		    	 request.setAttribute("_EVENT_MESSAGE_", "Sorry! You dont have leave workflow steps. Contact With your Admin ");
		         return "Error";
		     }
		     Map<String, Object> processForLeave = FastMap.newInstance();
		     processForLeave.put("processId", workflowProcessIdgen);
		     processForLeave.put("processStrId", processStrId);
		     processForLeave.put("ownerPartyId", partyId);
		      
		     try {
		  		GenericValue workflowProcess = delegator
		  				.makeValue("WorkflowProcess",
		  						UtilMisc.toMap(processForLeave));
		  		workflowProcess.create();
		      } catch (Exception e) {
		
		      	e.printStackTrace();
		      }


			    
		     String authPartyId = null;
		     String processStepId = null;
		     if (UtilValidate.isNotEmpty(processStepList)) {
		         if (UtilValidate.isNotEmpty(processStepList.get(0).get("authPartyId"))) {
		        	 authPartyId = processStepList.get(0).get("authPartyId").toString();
		         }
		         if (UtilValidate.isNotEmpty(processStepList.get(0).get("stepId"))) {
		        	 processStepId = processStepList.get(0).get("stepId").toString();
		         }
		     }
		     //----------------Data Insert in ProcessStepData Table-----------------//
		     Timestamp stamp = new Timestamp(System.currentTimeMillis());
		     Map<String, Object> emplPrefApprProcessData = FastMap.newInstance();
		    
		     emplPrefApprProcessData.put("processStrId", processStrId);
		     emplPrefApprProcessData.put("processStepId", processStepId);
		     emplPrefApprProcessData.put("processId", workflowProcessIdgen);
		     emplPrefApprProcessData.put("authorizePartyId", authPartyId);
		     emplPrefApprProcessData.put("dateAdded", stamp);
		     emplPrefApprProcessData.put("statusId", "PA_WAIT_FOR_APPR");
		     try {
	    		GenericValue processStepData = delegator
	    				.makeValue("ProcessStepData",
	    						UtilMisc.toMap(emplPrefApprProcessData));
	    		processStepData.create();
	         } catch (Exception e) {
		        	e.printStackTrace();
		     }*/
	        request.setAttribute("processId", processId);
        }
        catch(Exception e){}
        return "success";
    }

	public static String RemovePerfAppraisal(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
        String processId=request.getParameter("processId").toString();
        GenericValue perfApprProcessInfo = null; 
        try {
        	perfApprProcessInfo = delegator.findByPrimaryKey("PerfApprProcess", UtilMisc.toMap("processId", processId));

			String partyId =perfApprProcessInfo.get("partyId").toString();	
			String perfApprTypeId =perfApprProcessInfo.get("perfApprTypeId").toString();
			String title =perfApprProcessInfo.get("title").toString();	
			String description =perfApprProcessInfo.get("description").toString();
			
			String startDateStr = ""; 
	        Timestamp startDate = new Timestamp(System.currentTimeMillis());		
	        if (null != perfApprProcessInfo.get("startDate").toString()) {
	        	startDateStr = perfApprProcessInfo.get("startDate").toString();
	        	try{
	        		startDate = Timestamp.valueOf(startDateStr);
	        	}
	        	catch(Exception e){
	                request.setAttribute("_ERROR_MESSAGE_", "Start Date Data Format Not Correct");
	        	}
	        }
	        if (startDate.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Start Date can not be empty");
	            return "error";
	        }
			String endDateStr = ""; 
	        Timestamp endDate = new Timestamp(System.currentTimeMillis());		
	        if (null != perfApprProcessInfo.get("endDate").toString()) {
	        	endDateStr = perfApprProcessInfo.get("endDate").toString();
	        	try{
	        		endDate = Timestamp.valueOf(endDateStr);
	        	}
	        	catch(Exception e){
	                request.setAttribute("_ERROR_MESSAGE_", "End Date Data Format Not Correct");
	        	}
	        }
	        if (endDate.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "End Date can not be empty");
	            return "error";
	        }
			   
			Map<String, Object> perfType = FastMap.newInstance();
			perfType.put("processId", processId);
			perfType.put("partyId", partyId);
			perfType.put("perfApprTypeId", perfApprTypeId);
			perfType.put("startDate", startDate);
			perfType.put("endDate", endDate);
			perfType.put("title", title);
			perfType.put("description", description);
			perfType.put("statusId", "Removed");
			perfType.put("isActive", "N");
			perfType.put("dateAdded", currentDatetime);
		    try {
				GenericValue perfTypeSetup = delegator
					.makeValue("PerfApprProcess",
							UtilMisc.toMap(perfType));
				perfTypeSetup.store();  
			    request.setAttribute("_EVENT_MESSAGE_", "Performance Appraisal Send Successfully");
		    } catch (Exception e) { 
	            request.setAttribute("_ERROR_MESSAGE_", "Performance Appraisal Not Send");
		    }

	        request.setAttribute("processId", processId);
        }
        catch(Exception e){}
        return "success";
    }
	
	public static String createPerfApprBehaviorKpi(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
        
      
		String dataId = delegator.getNextSeqId("PerfApprBusinessKpi");
		String processId = ""; 
        if (null != request.getParameter("processId")) {
        	processId = request.getParameter("processId");
        }

        if (processId.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Process Id can not be empty");
            return "error";
        }
		String kpiItemDescription="";
        if (null != request.getParameter("kpiItemDescription")) {
        	kpiItemDescription = request.getParameter("kpiItemDescription");
        }
        if (kpiItemDescription.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "kpiItem Description can not be empty");
            return "error";
        }
		String perfIndicator = ""; 
        if (null != request.getParameter("perfIndicator")) {
        	perfIndicator = request.getParameter("perfIndicator");
        }
        if (perfIndicator.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Performance Indicator can not be empty");
            return "error";
        }
		String ratingId = ""; 
        if (null != request.getParameter("ratingId")) {
        	ratingId = request.getParameter("ratingId");
        }
        if (ratingId.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Rating can not be empty");
            return "error";
        }
		String kpiItemId = ""; 
        if (null != request.getParameter("kpiItemId")) {
        	kpiItemId = request.getParameter("kpiItemId");
        }
        if (kpiItemId.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "kpiItem Id can not be empty");
            return "error";
        }
		
		String weightStr = ""; 
		Double weight=0.0;
        if (null != request.getParameter("weight")) {
        	weightStr = request.getParameter("weight");
        	try{
        		weight=Double.valueOf(weightStr);
        	}
        	catch(Exception e){
                request.setAttribute("_ERROR_MESSAGE_", "weight Must Be A Number Value");
        	}
        }
        if (weightStr.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "weight can not be empty");
            return "error";
        }
        
		String comments=request.getParameter("comments");
		Map<String, Object> perfType = FastMap.newInstance();
		perfType.put("processId", processId);
		perfType.put("dataId", dataId);
		perfType.put("kpiItemDescription", kpiItemDescription);
		perfType.put("perfIndicator", perfIndicator);
		perfType.put("ratingId", ratingId);
		perfType.put("weight", weight);
		perfType.put("isActive", "Y");
		perfType.put("kpiItemId", kpiItemId);
		perfType.put("comments", comments);
		perfType.put("dateAdded", currentDatetime);
	    try {
			GenericValue perfTypeSetup = delegator
				.makeValue("PerfApprBehaviourKpi",
						UtilMisc.toMap(perfType));
			perfTypeSetup.create();  
		    request.setAttribute("_EVENT_MESSAGE_", "Performance Appraisal Behavioral KPI Set Successfully");
	    } catch (Exception e) { 
            request.setAttribute("_ERROR_MESSAGE_", "Performance Appraisal Behavioral KPI Not Added");
	    }
        request.setAttribute("processId", processId);
        request.setAttribute("dataId", dataId);
        request.setAttribute("kpiItemDescription", kpiItemDescription);
        request.setAttribute("perfIndicator", perfIndicator);
        request.setAttribute("kpiItemId", kpiItemId);
        request.setAttribute("ratingId", ratingId);
        request.setAttribute("weight", weight);
        request.setAttribute("comments", comments);
        request.setAttribute("dateAdded", currentDatetime);
        GenericValue perfApprProcessInfo = null;
		try { 
			perfApprProcessInfo=delegator.findByPrimaryKey("PerfApprProcess", UtilMisc.toMap("processId", processId));
			String perfApprTypeId=perfApprProcessInfo.get("perfApprTypeId").toString();
			String startDate=perfApprProcessInfo.get("startDate").toString();
			String endDate=perfApprProcessInfo.get("endDate").toString();
			String title=perfApprProcessInfo.get("title").toString();
			String description=perfApprProcessInfo.get("description").toString();
			String partyId=perfApprProcessInfo.get("partyId").toString();
			request.setAttribute("perfApprTypeId", perfApprTypeId);
	        request.setAttribute("startDate", startDate);
	        request.setAttribute("endDate", endDate);
	        request.setAttribute("title", title);
	        request.setAttribute("description", description);
		}
		catch(Exception e){
			
		}
		return "success";
    }
	
	public static String createDevelopmentPlan(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
	        
	      
			String dataId = delegator.getNextSeqId("PerfApprBusinessKpi");
			String processId = ""; 
	        if (null != request.getParameter("processId")) {
	        	processId = request.getParameter("processId");
	        }

	        if (processId.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Process Id can not be empty");
	            return "error";
	        }
			String objective="";
	        if (null != request.getParameter("objective")) {
	        	objective = request.getParameter("objective");
	        }
	        if (objective.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "kpiItem Description can not be empty");
	            return "error";
	        }
			String byWhen = ""; 
	        if (null != request.getParameter("byWhen")) {
	        	byWhen = request.getParameter("byWhen");
	        }
	        if (byWhen.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "by When can not be empty");
	            return "error";
	        }
			String ratingId = ""; 
	        if (null != request.getParameter("ratingId")) {
	        	ratingId = request.getParameter("ratingId");
	        }
	        if (ratingId.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Rating can not be empty");
	            return "error";
	        }
			String developmentRequirementId = ""; 
	        if (null != request.getParameter("developmentRequirementId")) {
	        	developmentRequirementId = request.getParameter("developmentRequirementId");
	        }
	        if (developmentRequirementId.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "kpiItem Id can not be empty");
	            return "error";
	        }
			
	        String statusId=request.getParameter("statusId");
			String comments=request.getParameter("comments");
			Map<String, Object> perfType = FastMap.newInstance();
			perfType.put("processId", processId);
			perfType.put("dataId", dataId);
			perfType.put("objective", objective);
			perfType.put("byWhen", byWhen);
			perfType.put("ratingId", ratingId);
			perfType.put("statusId", statusId);
			perfType.put("isActive", "Y");
			perfType.put("developmentRequirementId", developmentRequirementId);
			perfType.put("comments", comments);
			perfType.put("dateAdded", currentDatetime);
		    try {
				GenericValue perfTypeSetup = delegator
					.makeValue("PerfApprDevelopmentPlan",
							UtilMisc.toMap(perfType));
				perfTypeSetup.create();  
			    request.setAttribute("_EVENT_MESSAGE_", "Performance Appraisal Development Plan  Set Successfully");
		    } catch (Exception e) { 
	            request.setAttribute("_ERROR_MESSAGE_", "Performance Appraisal Development Plan  Not Added");
		    }
	        request.setAttribute("processId", processId);
	        request.setAttribute("dataId", dataId);
	        request.setAttribute("objective", objective);
	        request.setAttribute("byWhen", byWhen);
	        request.setAttribute("developmentRequirementId", developmentRequirementId);
	        request.setAttribute("ratingId", ratingId);
	        request.setAttribute("statusId", statusId);
	        request.setAttribute("comments", comments);
	        request.setAttribute("dateAdded", currentDatetime);
	        GenericValue perfApprProcessInfo = null;
			try { 
				perfApprProcessInfo=delegator.findByPrimaryKey("PerfApprProcess", UtilMisc.toMap("processId", processId));
				String perfApprTypeId=perfApprProcessInfo.get("perfApprTypeId").toString();
				String startDate=perfApprProcessInfo.get("startDate").toString();
				String endDate=perfApprProcessInfo.get("endDate").toString();
				String title=perfApprProcessInfo.get("title").toString();
				String description=perfApprProcessInfo.get("description").toString();
				String partyId=perfApprProcessInfo.get("partyId").toString();
				request.setAttribute("perfApprTypeId", perfApprTypeId);
		        request.setAttribute("startDate", startDate);
		        request.setAttribute("endDate", endDate);
		        request.setAttribute("title", title);
		        request.setAttribute("description", description);
			}
			catch(Exception e){
				
			}
			return "success";
	    }


	public static String ApprovedPerformanceApproval(HttpServletRequest request, HttpServletResponse response) {

		 Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
	        String processId=request.getParameter("processId").toString();
	        String appraisarComments = "";
	        if (null != request.getParameter("appraisarComments")) {
	        	appraisarComments = request.getParameter("appraisarComments");
	        }
	        if (appraisarComments.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Appraisar Comments can not be empty");
	            return "error";
	        }
			String approverStep="";
	        GenericValue perfApprProcessInfo = null; 
	        try {
	        	perfApprProcessInfo = delegator.findByPrimaryKey("PerfApprProcess", UtilMisc.toMap("processId", processId));

				String partyId =perfApprProcessInfo.get("partyId").toString();	
				String stepPartyId =perfApprProcessInfo.get("stepPartyId").toString();			
				String perfApprTypeId =perfApprProcessInfo.get("perfApprTypeId").toString();
				String title =perfApprProcessInfo.get("title").toString();	
				String description =perfApprProcessInfo.get("description").toString();
				String firstApprovarId =perfApprProcessInfo.get("firstApprovarId").toString();
				String secondApprovarId =perfApprProcessInfo.get("secondApprovarId").toString();
				String thirdApprovarId =perfApprProcessInfo.get("thirdApprovarId").toString();
				String firstApprovarStatus =perfApprProcessInfo.get("firstApprovarStatus").toString();
				String partyStatus =perfApprProcessInfo.get("partyStatus").toString();
				String secondApprovarStatus =perfApprProcessInfo.get("secondApprovarStatus").toString();
				String thirdApprovarStatus =perfApprProcessInfo.get("thirdApprovarStatus").toString();
				String firstApprovarComments ="";
				String partyComments ="";
				String secondApprovarComments = "";
				String thirdApprovarComments ="";
				String nextApproverId="";
				try
				{
					firstApprovarComments =perfApprProcessInfo.get("firstApprovarComments").toString();
					partyComments =perfApprProcessInfo.get("partyComments").toString();
					secondApprovarComments = perfApprProcessInfo.get("secondApprovarComments").toString();
					thirdApprovarComments =perfApprProcessInfo.get("thirdApprovarComments").toString();
				}
				catch(Exception e){}
				GenericValue partyPrefApprInfo = null; 	        
				try{
					partyPrefApprInfo = delegator.findByPrimaryKey("PartyPerfApprWorkflow", UtilMisc.toMap("partyId", partyId));
					String firstApprover=partyPrefApprInfo.get("firstLevelApprover").toString();	
					String secondApprover=partyPrefApprInfo.get("secondLevelApprover").toString();	
					String thirdApprover=partyPrefApprInfo.get("thirdLevelApprover").toString();
					if(stepPartyId.equals(firstApprover))
					{
						firstApprovarComments=appraisarComments;
						approverStep="Approved By First Approver";
						firstApprovarStatus="1";
						nextApproverId=partyId;
					}
					else{
						if(stepPartyId.equals(secondApprover))
						{
							secondApprovarComments=appraisarComments;
							approverStep="Approved By Second Approver";
							secondApprovarStatus="1";
							nextApproverId=thirdApprover;
						}
						else{
							if(stepPartyId.equals(thirdApprover))
							{
								thirdApprovarComments=appraisarComments;
								approverStep="Approved";
								thirdApprovarStatus="1";
							}
							else{
								partyComments=appraisarComments;
								approverStep="Approved By Self";
								partyStatus="1";
								nextApproverId=secondApprover;
							}
						}
					}
				}
				catch(Exception e){}
				
				
				Map<String, Object> perfType = FastMap.newInstance();
				perfType.put("processId", processId);
				perfType.put("statusId", approverStep);
				/*perfType.put("partyId", partyId);
				perfType.put("perfApprTypeId", perfApprTypeId);
				perfType.put("startDate", startDate);
				perfType.put("endDate", endDate);
				perfType.put("title", title);
				perfType.put("description", description);
				perfType.put("isActive", "Y");
				perfType.put("dateAdded", currentDatetime);*/
				perfType.put("stepPartyId", nextApproverId);
				if(approverStep.equals("Approved By First Approver")){
					perfType.put("firstApprovarComments", firstApprovarComments);
					perfType.put("firstApprovarStatus", firstApprovarStatus);
				}
				if(approverStep.equals("Approved By Self")){
					perfType.put("partyComments", partyComments);
					perfType.put("partyStatus", partyStatus);
				}
				if(approverStep.equals("Approved By Second Approver")){
					perfType.put("secondApprovarComments", secondApprovarComments);
					perfType.put("secondApprovarStatus", secondApprovarStatus);
				}

				if(approverStep.equals("Approved")){
					perfType.put("thirdApprovarComments", thirdApprovarComments);
					perfType.put("thirdApprovarStatus", thirdApprovarStatus);
				}
			    try {
					GenericValue perfTypeSetup = delegator
						.makeValue("PerfApprProcess",
								UtilMisc.toMap(perfType));
					perfTypeSetup.store();  
				    request.setAttribute("_EVENT_MESSAGE_", "Performance Appraisal Send Successfully");
			    } catch (Exception e) { 
		            request.setAttribute("_ERROR_MESSAGE_", "Performance Appraisal Not Send");
			    }

		        request.setAttribute("processId", processId);
	        }
	        catch(Exception e){}
	        
			return "success";
    }
	public static String RejectPreformanceApproval(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
        String processId=request.getParameter("processId").toString();
        //String appraisarComments=request.getParameter("appraisarCommentsParm").toString();
        String appraisarComments = "";
        if (null != request.getParameter("appraisarCommentsParm")) {
        	appraisarComments = request.getParameter("appraisarCommentsParm");
        }
        if (appraisarComments.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Appraisar Comments can not be empty");
            return "error";
        }
		String approverStep="";
        GenericValue perfApprProcessInfo = null; 
        try {
        	perfApprProcessInfo = delegator.findByPrimaryKey("PerfApprProcess", UtilMisc.toMap("processId", processId));
			String partyId =perfApprProcessInfo.get("partyId").toString();	
			String stepPartyId =perfApprProcessInfo.get("stepPartyId").toString();			
			String perfApprTypeId =perfApprProcessInfo.get("perfApprTypeId").toString();
			String title =perfApprProcessInfo.get("title").toString();	
			String description =perfApprProcessInfo.get("description").toString();
			String firstApprovarId =perfApprProcessInfo.get("firstApprovarId").toString();
			String secondApprovarId =perfApprProcessInfo.get("secondApprovarId").toString();
			String thirdApprovarId =perfApprProcessInfo.get("thirdApprovarId").toString();
			String firstApprovarStatus =perfApprProcessInfo.get("firstApprovarStatus").toString();
			String partyStatus =perfApprProcessInfo.get("partyStatus").toString();
			String secondApprovarStatus =perfApprProcessInfo.get("secondApprovarStatus").toString();
			String thirdApprovarStatus =perfApprProcessInfo.get("thirdApprovarStatus").toString();
			String firstApprovarComments ="";
			String partyComments ="";
			String secondApprovarComments = "";
			String thirdApprovarComments ="";
			try
			{
				firstApprovarComments =perfApprProcessInfo.get("firstApprovarComments").toString();
				partyComments =perfApprProcessInfo.get("partyComments").toString();
				secondApprovarComments = perfApprProcessInfo.get("secondApprovarComments").toString();
				thirdApprovarComments =perfApprProcessInfo.get("thirdApprovarComments").toString();
			}
			catch(Exception e){}
			GenericValue partyPrefApprInfo = null; 	        
			try{
				partyPrefApprInfo = delegator.findByPrimaryKey("PartyPerfApprWorkflow", UtilMisc.toMap("partyId", partyId));
				String firstApprover=partyPrefApprInfo.get("firstLevelApprover").toString();	
				if(stepPartyId.equals(firstApprover))
				{
					firstApprovarComments=appraisarComments;
					approverStep="Reject From First Level Approver";
				}
				else{
					String secondApprover=partyPrefApprInfo.get("secondLevelApprover").toString();	
					if(stepPartyId.equals(secondApprover))
					{
						secondApprovarComments=appraisarComments;
						approverStep="Reject From Second Level Approver";
					}
					else{
						String thirdApprover=partyPrefApprInfo.get("thirdLevelApprover").toString();	
						if(stepPartyId.equals(thirdApprover))
						{
							thirdApprovarComments=appraisarComments;
							approverStep="Reject From Third Level Approver";
						}
						else{
							partyComments=appraisarComments;
							approverStep="Reject From Self";
						}
					}
				}
			}
			catch(Exception e){}
			String startDateStr = ""; 
	        Timestamp startDate = new Timestamp(System.currentTimeMillis());		
	        if (null != perfApprProcessInfo.get("startDate").toString()) {
	        	startDateStr = perfApprProcessInfo.get("startDate").toString();
	        	try{
	        		startDate = Timestamp.valueOf(startDateStr);
	        	}
	        	catch(Exception e){
	                request.setAttribute("_ERROR_MESSAGE_", "Start Date Data Format Not Correct");
	        	}
	        }
	        if (startDate.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Start Date can not be empty");
	            return "error";
	        }
			String endDateStr = ""; 
	        Timestamp endDate = new Timestamp(System.currentTimeMillis());		
	        if (null != perfApprProcessInfo.get("endDate").toString()) {
	        	endDateStr = perfApprProcessInfo.get("endDate").toString();
	        	try{
	        		endDate = Timestamp.valueOf(endDateStr);
	        	}
	        	catch(Exception e){
	                request.setAttribute("_ERROR_MESSAGE_", "End Date Data Format Not Correct");
	        	}
	        }
	        if (endDate.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "End Date can not be empty");
	            return "error";
	        }			   
			Map<String, Object> perfType = FastMap.newInstance();
			perfType.put("processId", processId);
			perfType.put("partyId", partyId);
			perfType.put("perfApprTypeId", perfApprTypeId);
			perfType.put("startDate", startDate);
			perfType.put("endDate", endDate);
			perfType.put("title", title);
			perfType.put("description", description);
			perfType.put("statusId", approverStep);
			perfType.put("isActive", "N");
			perfType.put("dateAdded", currentDatetime);
			perfType.put("firstApprovarComments", firstApprovarComments);
			perfType.put("partyComments", partyComments);
			perfType.put("secondApprovarComments", secondApprovarComments);
			perfType.put("thirdApprovarComments", thirdApprovarComments);
		    try {
				GenericValue perfTypeSetup = delegator
					.makeValue("PerfApprProcess",
							UtilMisc.toMap(perfType));
				perfTypeSetup.store();  
			    request.setAttribute("_EVENT_MESSAGE_", "Performance Appraisal Send Successfully");
		    } catch (Exception e) { 
	            request.setAttribute("_ERROR_MESSAGE_", "Performance Appraisal Not Send");
		    }

	        request.setAttribute("processId", processId);
        }
        catch(Exception e){}        
		return "success";
    }
}