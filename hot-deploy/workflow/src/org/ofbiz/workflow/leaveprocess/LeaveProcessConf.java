package org.ofbiz.workflow.leaveprocess;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

//Created By Tutul
public class LeaveProcessConf {

    public static final String module = LeaveProcessConf.class.getName();
	//-------------------------------------------------------------------------------------------------------------------

    public static String createLeaveProcessStructureSetup(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        /*String processStrId = "";
        if (null != request.getParameter("processStrId")) {
        	processStrId = request.getParameter("processStrId");
        }

        if (processStrId.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Leave Structure Id can not be empty");
            return "error";
        }*/

	    String processStrId = delegator.getNextSeqId("ProcessStructure");
        String processName = "";
        if (null != request.getParameter("processName")) {
        	processName = request.getParameter("processName");
        }

        if (processName.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Leave Structure Name can not be empty");
            return "error";
        }

		String description = request.getParameter("description");
		
		
		

        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
		Map<String, Object> processStructureIn = FastMap.newInstance();
		processStructureIn.put("processStrId", processStrId);
		processStructureIn.put("processName", processName);
		processStructureIn.put("description", description);
		processStructureIn.put("processType", "Leave");
		processStructureIn.put("isActive", "Y");
		processStructureIn.put("fromDate", currentDatetime);
	    try {
			GenericValue processStructureInSetup = delegator
				.makeValue("ProcessStructure",
						UtilMisc.toMap(processStructureIn));
			processStructureInSetup.create();   

			
	    } catch (Exception e) { }

        request.setAttribute("_EVENT_MESSAGE_", "Leave Structure Successfully Created");
        return "success";
    }
    public static String updateLeaveProcessStructureSetup(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        String processStrId = "";
        if (null != request.getParameter("processStrId")) {
        	processStrId = request.getParameter("processStrId");
        }

        if (processStrId.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Leave Structure Id can not be empty");
            return "error";
        }
        String processName = "";
        if (null != request.getParameter("processName")) {
        	processName = request.getParameter("processName");
        }

        if (processName.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Leave Structure Name can not be empty");
            return "error";
        }

		String description = request.getParameter("description");
		
		
		

        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
		Map<String, Object> processStructureIn = FastMap.newInstance();
		processStructureIn.put("processStrId", processStrId);
		processStructureIn.put("processType", "Leave");
		processStructureIn.put("processName", processName);
		processStructureIn.put("description", description);
		processStructureIn.put("isActive", "Y");
		processStructureIn.put("fromDate", currentDatetime);
	    try {
			GenericValue processStructureInSetup = delegator
				.makeValue("ProcessStructure",
						UtilMisc.toMap(processStructureIn));
			processStructureInSetup.store();   

			
	    } catch (Exception e) { }

        request.setAttribute("_EVENT_MESSAGE_", "Leave Structure Successfully Updated");
        return "success";
    }
    public static String createLeaveProcessStepSetup(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        String processStrId = "";
        if (null != request.getParameter("processStrId")) {
        	processStrId = request.getParameter("processStrId");
        }

        if (processStrId.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Leave Structure Id can not be empty");
            return "error";
        }
        String stepOrder = "";
        if (null != request.getParameter("stepOrder")) {
        	stepOrder = request.getParameter("stepOrder");
        }

        if (stepOrder.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Step Order can not be empty");
            return "error";
        }

	    String stepId = delegator.getNextSeqId("ProcessSteps");
        String employeeID = "";
        if (null != request.getParameter("authEmployeeId")) {
        	employeeID = request.getParameter("authEmployeeId");
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
            else{
                request.setAttribute("_ERROR_MESSAGE_", "Employee not exist");
                return "error";
            }
        }
       
		String description = request.getParameter("description");
		
		

        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
		Map<String, Object> processStepIn = FastMap.newInstance();
		processStepIn.put("processStrId", processStrId);
		processStepIn.put("stepId", stepId);
		processStepIn.put("stepOrder", stepOrder);
		processStepIn.put("authPartyId", partyId);
		processStepIn.put("authEmployeeId", employeeID);
		processStepIn.put("description", description);
		processStepIn.put("isActive", "Y");
		processStepIn.put("dateAdded", currentDatetime);
		try {
			GenericValue processStepInSetup = delegator.
					makeValue("ProcessSteps", UtilMisc.toMap(processStepIn));				
			processStepInSetup.create();  	
			

		    String notificationId = delegator.getNextSeqId("ProcessStepNotifications");

			Map<String, Object> processStepNotificationsIn = FastMap.newInstance();
			processStepNotificationsIn.put("processStrId", processStrId);
			processStepNotificationsIn.put("stepId", stepId);
			processStepNotificationsIn.put("notificationId", notificationId);
			processStepNotificationsIn.put("stepFilter", "LEAVE_WAIT_FOR_APPR");
			processStepNotificationsIn.put("notifiedPerson", employeeID);
			processStepNotificationsIn.put("notifiedParty", partyId);
			processStepNotificationsIn.put("description", "Step Data Entry");
			processStepNotificationsIn.put("isActive", "Y");
			processStepNotificationsIn.put("dateAdded", currentDatetime);
		    try {
				GenericValue processStepNotificationsInSetup = delegator
					.makeValue("ProcessStepNotifications",
							UtilMisc.toMap(processStepNotificationsIn));
				processStepNotificationsInSetup.create(); 
			} 
			catch (Exception e) { }			
		} 
		catch (Exception e) { }
        request.setAttribute("_EVENT_MESSAGE_", "Leave Process Step Successfully Created");
        return "success";
    }

    public static String updateLeaveProcessStepSetup(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        String processStrId = "";
        if (null != request.getParameter("processStrId")) {
        	processStrId = request.getParameter("processStrId");
        }

        if (processStrId.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Leave Structure Id can not be empty");
            return "error";
        }
        String stepOrder = "";
        if (null != request.getParameter("stepOrder")) {
        	stepOrder = request.getParameter("stepOrder");
        }

        if (stepOrder.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Step Order can not be empty");
            return "error";
        }

        String employeeID = "";
        if (null != request.getParameter("authEmployeeId")) {
        	employeeID = request.getParameter("authEmployeeId");
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
            else{
                request.setAttribute("_ERROR_MESSAGE_", "Employee not exist");
                return "error";
            }
        }

		String description = request.getParameter("description");
		String stepId = request.getParameter("stepId");
		String isActive = request.getParameter("isActive");
		String dateAddedStr = request.getParameter("dateAdded");
        Timestamp dateAdded = Timestamp.valueOf(dateAddedStr);
		
		Map<String, Object> processStepIn = FastMap.newInstance();
		processStepIn.put("processStrId", processStrId);
		processStepIn.put("stepId", stepId);
		processStepIn.put("stepOrder", stepOrder);
		processStepIn.put("authPartyId", partyId);
		processStepIn.put("authEmployeeId", employeeID);
		processStepIn.put("description", description);
		processStepIn.put("isActive", isActive);
		processStepIn.put("dateAdded", dateAdded);
		try {
			GenericValue processStepInSetup = delegator.
					makeValue("ProcessSteps", UtilMisc.toMap(processStepIn));				
			processStepInSetup.store();  				
		} 
		catch (Exception e) { }
        request.setAttribute("_EVENT_MESSAGE_", "Leave Process Step Successfully Created");
        return "success";
    }
    
    
    

    public static String createPartyProcess(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        
        String processStrId = "";
        if (null != request.getParameter("processStrId")) {
        	processStrId = request.getParameter("processStrId");
        }

        if (processStrId.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Leave Structure Id can not be empty");
            return "error";
        }

        String processType = "";
        if (null != request.getParameter("processTypeId")) {
        	processType = request.getParameter("processTypeId");
        }

        if (processType.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Process Type can not be empty");
            return "error";
        }
        String employeeId = "";
        if (null != request.getParameter("employeeId")) {
        	employeeId = request.getParameter("employeeId");
        }

        if (employeeId.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Employee can not be empty");
            return "error";
        }
        List<GenericValue> partyList = null;
        try {
        	partyList = delegator.findByAnd("UserLogin",
                    UtilMisc.toMap("userLoginId", employeeId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String partyId = null;
        if (UtilValidate.isNotEmpty(partyList)) {
            if (UtilValidate.isNotEmpty(partyList.get(0).get("partyId"))) {
            	partyId = partyList.get(0).get("partyId").toString();
            }
            else{
                request.setAttribute("_ERROR_MESSAGE_", "Employee not exist");
                return "error";
            }
        }

		String comments = request.getParameter("comments");

		/*	Check Previous Process Status	*/

 	    List<EntityExpr> exprsPartyPreProcessStatus = FastList.newInstance();
 	    exprsPartyPreProcessStatus.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)); 
 	    exprsPartyPreProcessStatus.add(EntityCondition.makeCondition("isActive", EntityOperator.EQUALS, "Y"));        
 	    exprsPartyPreProcessStatus.add(EntityCondition.makeCondition("processTypeId", EntityOperator.EQUALS, processType));        
		List<GenericValue> partyPreProcessStatus = null;		
		
		try {
			partyPreProcessStatus = delegator.findList("ProcessParty", EntityCondition.makeCondition(exprsPartyPreProcessStatus, EntityOperator.AND), null, null, null, false);
			for (Map aParsed : partyPreProcessStatus){	

                request.setAttribute("_ERROR_MESSAGE_", "Sorry!!! Previous Process Is Active ");
                return "error";
			}
		} 
		catch (GenericEntityException e) {
			e.printStackTrace();
		}

		/*	======================================	*/
		
		
		
		
        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
		Map<String, Object> partyProcessIn = FastMap.newInstance();
		partyProcessIn.put("processStrId", processStrId);
		partyProcessIn.put("processTypeId",processType);
		partyProcessIn.put("employeeId", employeeId);
		partyProcessIn.put("partyId", partyId);
		partyProcessIn.put("comments", comments);
		partyProcessIn.put("isActive", "Y");
		partyProcessIn.put("dateAdded", currentDatetime);
		
	    try {
			GenericValue partyProcessInSetup = delegator
				.makeValue("ProcessParty",
						UtilMisc.toMap(partyProcessIn));
			partyProcessInSetup.create();   

			
	    } catch (Exception e) { }

        request.setAttribute("_EVENT_MESSAGE_", "Party Process Successfully Assign");
        return "success";
    }
    
    

    public static String statusChangePartyProcess(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        String processStrId = "";
        if (null != request.getParameter("processStrId")) {
        	processStrId = request.getParameter("processStrId");
        }

        if (processStrId.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Leave Structure Id can not be empty");
            return "error";
        }
        String employeeId = "";
        if (null != request.getParameter("employeeId")) {
        	employeeId = request.getParameter("employeeId");
        }

        if (employeeId.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Employee can not be empty");
            return "error";
        }
        List<GenericValue> partyList = null;
        try {
        	partyList = delegator.findByAnd("UserLogin",
                    UtilMisc.toMap("userLoginId", employeeId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String partyId = null;
        if (UtilValidate.isNotEmpty(partyList)) {
            if (UtilValidate.isNotEmpty(partyList.get(0).get("partyId"))) {
            	partyId = partyList.get(0).get("partyId").toString();
            }
            else{
                request.setAttribute("_ERROR_MESSAGE_", "Employee not exist");
                return "error";
            }
        }

		String comments = request.getParameter("comments");
		String isActive = request.getParameter("isActive");

        if (isActive.equals("Y")) {
        	isActive="N";
        }
        else{
        	isActive="Y";
        }
		String dateAddedStr = request.getParameter("dateAdded");
        Timestamp dateAdded = Timestamp.valueOf(dateAddedStr);
        
		Map<String, Object> partyProcessIn = FastMap.newInstance();
		partyProcessIn.put("processStrId", processStrId);
		partyProcessIn.put("processTypeId", "LEAVE");
		partyProcessIn.put("employeeId", employeeId);
		partyProcessIn.put("partyId", partyId);
		partyProcessIn.put("comments", comments);
		partyProcessIn.put("isActive", isActive);
		partyProcessIn.put("dateAdded", dateAdded);
	    try {
			GenericValue partyProcessInSetup = delegator
				.makeValue("ProcessParty",
						UtilMisc.toMap(partyProcessIn));
			partyProcessInSetup.store();   

			
	    } catch (Exception e) { }

        request.setAttribute("_EVENT_MESSAGE_", "Party Process Successfully Updated");
        return "success";
    }
    
    
    
    

    public static String createLeaveNotificationMap(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        String processStrId = "";
        if (null != request.getParameter("processStrId")) {
        	processStrId = request.getParameter("processStrId");
        }

        if (processStrId.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Leave Structure Id can not be empty");
            return "error";
        }
        String stepId = "";
        if (null != request.getParameter("stepId")) {
        	stepId = request.getParameter("stepId");
        }

        if (stepId.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Process Step Id can not be empty");
            return "error";
        }
        
        
        String notifiedPerson = "";
        if (null != request.getParameter("notifiedPerson")) {
        	notifiedPerson = request.getParameter("notifiedPerson");
        }

        if (notifiedPerson.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Notified Employee Id can not be empty");
            return "error";
        }

        List<GenericValue> partyList = null;
        try {
        	partyList = delegator.findByAnd("UserLogin",
                    UtilMisc.toMap("userLoginId", notifiedPerson));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String notifiedParty = null;
        if (UtilValidate.isNotEmpty(partyList)) {
            if (UtilValidate.isNotEmpty(partyList.get(0).get("partyId"))) {
            	notifiedParty = partyList.get(0).get("partyId").toString();
            }
            else{
                request.setAttribute("_ERROR_MESSAGE_", "Employee not exist");
                return "error";
            }
        }
		String stepFilter = request.getParameter("stepFilter");
		String description = request.getParameter("description");
	    String notificationId = delegator.getNextSeqId("ProcessStepNotifications");
		
		
		

        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
		Map<String, Object> processStepNotificationsIn = FastMap.newInstance();
		processStepNotificationsIn.put("processStrId", processStrId);
		processStepNotificationsIn.put("stepId", stepId);
		processStepNotificationsIn.put("notificationId", notificationId);
		processStepNotificationsIn.put("stepFilter", stepFilter);
		processStepNotificationsIn.put("notifiedPerson", notifiedPerson);
		processStepNotificationsIn.put("notifiedParty", notifiedParty);
		processStepNotificationsIn.put("description", description);
		processStepNotificationsIn.put("isActive", "Y");
		processStepNotificationsIn.put("dateAdded", currentDatetime);
	    try {
			GenericValue processStepNotificationsInSetup = delegator
				.makeValue("ProcessStepNotifications",
						UtilMisc.toMap(processStepNotificationsIn));
			processStepNotificationsInSetup.create();   

			
	    } catch (Exception e) { }

        request.setAttribute("_EVENT_MESSAGE_", "Leave Process Notification Successfully Created");
        return "success";
    }
}