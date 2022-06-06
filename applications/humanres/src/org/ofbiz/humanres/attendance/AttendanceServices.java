package org.ofbiz.humanres.attendance;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;

import com.sun.org.apache.bcel.internal.generic.Select;

public class AttendanceServices {

	 
	   public static String SetEmployeeAttendanceComments(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
			String partyId = request.getParameter("partyId");
			String dateStr = request.getParameter("dateOfAttendance");
			String employeeComments = request.getParameter("employeeComments");			
			Timestamp dateOfAttendance = Timestamp.valueOf(dateStr);
		
			Map<String, Object> AttendanceCommentsInSetupIn = FastMap.newInstance();
			AttendanceCommentsInSetupIn.put("partyId", partyId);
			AttendanceCommentsInSetupIn.put("dateOfAttendance", dateOfAttendance);
			AttendanceCommentsInSetupIn.put("employeeComments", employeeComments);
			
		   try {
				GenericValue AttendanceCommentsInSetup = delegator
					.makeValue("DailyAttendance",
							UtilMisc.toMap(AttendanceCommentsInSetupIn));
					AttendanceCommentsInSetup.store(); 
				
				
		    } catch (Exception e) { }
		  

	        request.setAttribute("_EVENT_MESSAGE_", "Attendance Comments Successfully Added");
	        return "success";
	    }
	    
	    public static String SendAttendanceProcessToSuperior(HttpServletRequest request, HttpServletResponse response) {
	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
			String partyId = request.getParameter("partyId");
			String employeeId = request.getParameter("employeeId");
			String dateStr = request.getParameter("dateOfAttendance");
			String employeeComments = request.getParameter("employeeComments");			
			Timestamp dateOfAttendance = Timestamp.valueOf(dateStr);
			GenericValue personInfo = null; 
			String approverId="";
			try {
				personInfo = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));

		    	if (null != personInfo.get("attendanceSuperiorId")) {
		    		approverId=personInfo.get("attendanceSuperiorId").toString();
		        }
		    	else{
		    		List<EntityExpr> exprs = FastList.newInstance(); 
		    	    exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		    	    exprs.add(EntityCondition.makeCondition("partyTypeId", EntityOperator.EQUALS, "PERSON"));
		    	    exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "EMPL_POS_ACTIVE"));
		    	    exprs.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, employeeId));
		    	    exprs.add(EntityCondition.makeCondition("employeePartyId", EntityOperator.EQUALS, partyId));
		    	    exprs.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "EMPLOYEE"));
		    		List<GenericValue> employeeList = null;
		    		String employeeBusinessUnitId="";
		    		try {
		    			employeeList = delegator.findList("PartyAndPersonAndFulfillment", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
		    		} catch (GenericEntityException e) {
		    			e.printStackTrace();
		    		}

		    		for (GenericValue employee : employeeList){		    			
	    		    	 if (null != employee.get("positionPartyId")) {
	    		    		 employeeBusinessUnitId = employee.get("positionPartyId").toString();       	
	    		         }		    		    	 
	    			}
		    		if(employeeBusinessUnitId ==""){
		    			request.setAttribute("_ERROR_MESSAGE_", "Employee BusinessUnit Not Find");
		    			return "Error";
		    		}
		    		else{
		    			GenericValue businessUnitTopInfo = null; 
		    			try {
		    				businessUnitTopInfo = delegator.findByPrimaryKey("BusinessUnitTopAssign", UtilMisc.toMap("partyId", employeeBusinessUnitId));
		    		    	if (null != businessUnitTopInfo) {
		    		    		approverId=businessUnitTopInfo.get("employeePartyId").toString();

			    		    	if (null != personInfo.get("employeePartyId")) {
					    			request.setAttribute("_ERROR_MESSAGE_", "Not Find Superior");
					    			return "Error";
			    		        }
		    		        }
		    		    	else{
				    			request.setAttribute("_ERROR_MESSAGE_", "Employee BusinessUnit Not Find");
				    			return "Error";
		    		        }
		    			}
		    			catch(Exception e){}
		    		}
		    		
		    	}
			}
			 catch(Exception e){}
			Map<String, Object> AttendanceCommentsInSetupIn = FastMap.newInstance();
			AttendanceCommentsInSetupIn.put("partyId", partyId);
			AttendanceCommentsInSetupIn.put("dateOfAttendance", dateOfAttendance);
			AttendanceCommentsInSetupIn.put("employeeComments", employeeComments);
			AttendanceCommentsInSetupIn.put("supervisorPartyId", approverId);
			AttendanceCommentsInSetupIn.put("processStatus", "Forward To Reporting");			
		   try {
				GenericValue AttendanceCommentsInSetup = delegator
					.makeValue("DailyAttendance",
							UtilMisc.toMap(AttendanceCommentsInSetupIn));
					AttendanceCommentsInSetup.store(); 				
				
		    } catch (Exception e) { }
		    request.setAttribute("_EVENT_MESSAGE_", "Attendance Report Successfully Send to your Reporting Boss");
	        return "success";
	    }
	    

	    public static String ApprovedAttendanceBySupervisor(HttpServletRequest request, HttpServletResponse response) {
	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
			String partyId = request.getParameter("partyId");
			String dateStr = request.getParameter("dateOfAttendance");
			String employeeComments = request.getParameter("employeeComments");			
			Timestamp dateOfAttendance = Timestamp.valueOf(dateStr);
			
			Map<String, Object> AttendanceCommentsInSetupIn = FastMap.newInstance();
			AttendanceCommentsInSetupIn.put("partyId", partyId);
			AttendanceCommentsInSetupIn.put("dateOfAttendance", dateOfAttendance);
			AttendanceCommentsInSetupIn.put("processStatus", "Approved by supervisor");			
		   try {
				GenericValue AttendanceCommentsInSetup = delegator
					.makeValue("DailyAttendance",
							UtilMisc.toMap(AttendanceCommentsInSetupIn));
					AttendanceCommentsInSetup.store(); 				
				
		    } catch (Exception e) { }
		    request.setAttribute("_EVENT_MESSAGE_", "Successfully approved by supervisor");
	        return "success";
	    }

	    public static String SetSupervisorCommentsForAttendance(HttpServletRequest request, HttpServletResponse response) {
	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
			String partyId = request.getParameter("partyId");
			String dateStr = request.getParameter("dateOfAttendance");
			String attendanceStatus = request.getParameter("attendanceStatus");	
			String supervisorComments = request.getParameter("supervisorComments");	
			String processStatus = request.getParameter("processStatus");			
			Timestamp dateOfAttendance = Timestamp.valueOf(dateStr);
			
			Map<String, Object> AttendanceCommentsInSetupIn = FastMap.newInstance();
			AttendanceCommentsInSetupIn.put("partyId", partyId);
			AttendanceCommentsInSetupIn.put("dateOfAttendance", dateOfAttendance);
			AttendanceCommentsInSetupIn.put("attendanceStatus", attendanceStatus);
			AttendanceCommentsInSetupIn.put("supervisorComments", supervisorComments);
			if(processStatus!="" ){
				AttendanceCommentsInSetupIn.put("processStatus", processStatus);
			}
		   try {
				GenericValue AttendanceCommentsInSetup = delegator
					.makeValue("DailyAttendance",
							UtilMisc.toMap(AttendanceCommentsInSetupIn));
					AttendanceCommentsInSetup.store(); 				
				
		    } catch (Exception e) { }
		    request.setAttribute("_EVENT_MESSAGE_", "Successfully added supervisor comments");
	        return "success";
	    }
}