package org.ofbiz.humanres;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.calendar.TemporalExpressions.DayOfWeekRange;


public class LeaveEvents {
	public static Map<String, Object> createContessaWeekHoliday(DispatchContext dctx, Map<String, ? extends Object> context){
		String dayName = "";
		
	Map<String, Object> result = FastMap.newInstance();
	 Delegator delegator = dctx.getDelegator();
	Timestamp now = UtilDateTime.nowTimestamp();
	dayName = (String) context.get("dayName");
	String	companyId = (String) context.get("companyId");
	GenericValue userLogin = (GenericValue) context.get("userLogin");
	String userLoginId = userLogin.get("userLoginId").toString();
	
		
		//String dayOfWeekString = "test";
		
		
		//dayName = "SUNDAY";
		int dayNo =  Integer.parseInt(dayName);
		String years = getFiscalYear(delegator, companyId);
		if (years.isEmpty()){
	    	 result.put("_ERROR_MESSAGE_", "Sorry! You Have to set Fiscal Year First.");
	         return result;
	     }
		int year = Integer.parseInt(years);
	
	
		Calendar cal = new GregorianCalendar(year, Calendar.JANUARY, 1);
		for (int i = 0, inc = 1; i < 366 && cal.get(Calendar.YEAR) == year; i+=inc) {
		    if (cal.get(Calendar.DAY_OF_WEEK) == dayNo) {
		        // this is a sunday
		    	Timestamp time = new Timestamp(cal.getTimeInMillis());
		    	 Map<String, Object> weeklyHolidayData = FastMap.newInstance();
		    	 String processIdgen = delegator.getNextSeqId("WorkEffort"); 
		    	 weeklyHolidayData.put("workEffortId", processIdgen);
		    	 weeklyHolidayData.put("workEffortTypeId", "OFFICE_HOLIDAY");
		    	 weeklyHolidayData.put("currentStatusId", "CAL_TENTATIVE");
		    	 weeklyHolidayData.put("lastStatusUpdate", now);
		    	 weeklyHolidayData.put("scopeEnumId", "WES_PUBLIC");
		    	 weeklyHolidayData.put("workEffortName", "Week Holiday");
		    	 
		    	 weeklyHolidayData.put("estimatedStartDate", time );
		    	 weeklyHolidayData.put("revisionNumber", Long.parseLong("1"));
		    	 weeklyHolidayData.put("createdDate", now);
		    	 weeklyHolidayData.put("createdByUserLogin",userLoginId);
		    	 weeklyHolidayData.put("lastModifiedDate", now);
		    	 weeklyHolidayData.put("lastModifiedByUserLogin", userLoginId);
		    	 
		    	 weeklyHolidayData.put("companyId", companyId);
		    	 
		         try {
		        		GenericValue workeffortdata = delegator
		        				.makeValue("WorkEffort",
		        						UtilMisc.toMap(weeklyHolidayData));
		        		workeffortdata.create();
		            } catch (Exception e) {
	
		            	e.printStackTrace();
		            }
	    		//System.out.println(dayOfWeekString + " " + i + ": " + time);
		        cal.add(Calendar.DAY_OF_MONTH, 7); 
		        inc = 7;
		    } else {
		        cal.add(Calendar.DAY_OF_MONTH, 1);
		        Timestamp time = new Timestamp(cal.getTimeInMillis());
		    	 Map<String, Object> weeklyHolidayData = FastMap.newInstance();
		    	 String processIdgen = delegator.getNextSeqId("WorkEffort"); 
		    	 weeklyHolidayData.put("workEffortId", processIdgen);
		    	 weeklyHolidayData.put("workEffortTypeId", "OFFICE_HOLIDAY");
		    	 weeklyHolidayData.put("currentStatusId", "CAL_TENTATIVE");
		    	 weeklyHolidayData.put("lastStatusUpdate", now);
		    	 weeklyHolidayData.put("workEffortName", "Week Holiday");
		    	 weeklyHolidayData.put("scopeEnumId", "WES_PUBLIC");
		    	 weeklyHolidayData.put("estimatedStartDate", time );
		    	 weeklyHolidayData.put("revisionNumber", Long.parseLong("1"));
		    	 weeklyHolidayData.put("createdDate", now);
		    	 weeklyHolidayData.put("createdByUserLogin",userLoginId);
		    	 weeklyHolidayData.put("lastModifiedDate", now);
		    	 weeklyHolidayData.put("lastModifiedByUserLogin", userLoginId);
		    	 weeklyHolidayData.put("companyId", companyId);
		         try {
		        		GenericValue workeffortdata = delegator
		        				.makeValue("WorkEffort",
		        						UtilMisc.toMap(weeklyHolidayData));
		        		workeffortdata.create();
		            } catch (Exception e) {
	
		            	e.printStackTrace();
		            }
		    }
		}
		result.put("dayName", dayName);
		return result;
	}
	public static String employeeLeaveSummaryView(HttpServletRequest request,HttpServletResponse response) {
	
		String partyId = "";
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
	
		if (null != request.getParameter("partyId")) {
			partyId = request.getParameter("partyId");
		}
		
		
		List<GenericValue> leaveTypeList = null;
	
		try {
			leaveTypeList = delegator.findList("EmplLeaveType", null, null,
					null, null, true);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<GenericValue> employees = null;
		try {
			employees = delegator.findList("EmploymentAndPerson", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null, null, null, true);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		String companyId = employees!=null?(employees.size()>0?employees.get(0).get("partyIdFrom").toString():""):"";
		
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		EntityListIterator emplLeavIterator = null;
		EntityListIterator emplLeavIterator2 = null;
		for (GenericValue genericValue : leaveTypeList) {
			
			Map<String, String> row = new HashMap<String, String>();
			row.put("description", genericValue.get("description").toString());
			int totalAvailableLeave = Integer.parseInt(genericValue.get("maxAvailableLeave").toString());
			
			String leaveTypeId = genericValue.get("leaveTypeId").toString();
			
			//// Get last year balance of employee //////////
			
			GenericValue currentFiscalYear = getCurrentFiscalYear(delegator);	
	
			String currentFiscalYearId = "";
			if (currentFiscalYear != null){
				currentFiscalYearId = currentFiscalYear.get("customTimePeriodId").toString();
				int prevLeaveBalance = countLeaveBalanceByLeaveType(delegator,partyId,currentFiscalYearId, leaveTypeId);
				totalAvailableLeave = totalAvailableLeave + prevLeaveBalance;
			}
			
			////////////////////////////////////////////////
			//Add Total Leave Amount in the list
			row.put("maxAvailableLeave", totalAvailableLeave+"");
			
			try {
	
				//Created Iterator
				emplLeavIterator = delegator.find("EmplLeave", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "leaveTypeId", leaveTypeId, "customTimePeriodId", currentFiscalYearId, "leaveStatus","LEAVE_CREATED"), EntityOperator.AND), null, null, null, null);
						
				double finalCreatedDay = 0;
				GenericValue runtimeData = null;
				while((runtimeData=emplLeavIterator.next())!= null){
					int noOfDaysCreated = 0;
					Timestamp stopDate = (Timestamp)runtimeData.get("thruDate");
					Timestamp startDate = (Timestamp)runtimeData.get("fromDate");
					String leaveDuration = runtimeData.getString("leaveDuration");
					
					List<GenericValue> emplLeavesApproved = delegator.findList("EmplLeave", EntityCondition.makeCondition(UtilMisc
							.toMap("partyId", partyId, "leaveTypeId", leaveTypeId, "leaveStatus","LEAVE_APPROVED", "fromDate", startDate, "thruDate", stopDate), EntityOperator.AND), null, null, null, true);
					
					if (emplLeavesApproved.size()==0){ // we need to avoid to count if this leave is already in approval list.
						int holidays = 0;
						
						if (!leaveTypeId.equals("1001")){ 
							holidays = getHolidayCount(delegator, companyId, startDate, stopDate);
						}
	
						
						noOfDaysCreated = getDifferenceBetweenTwoDate(startDate,stopDate);
						if(!leaveDuration.equals("Full_Day")){
							finalCreatedDay = finalCreatedDay + .5;
						}else{
							finalCreatedDay = finalCreatedDay + noOfDaysCreated + 1 - holidays;
						}
					}
				}
				emplLeavIterator.close();
				
				//Approved Iterator
				emplLeavIterator2 = delegator.find("EmplLeave", EntityCondition.makeCondition(UtilMisc
						.toMap("partyId", partyId, "leaveTypeId", leaveTypeId, "customTimePeriodId", currentFiscalYearId, "leaveStatus","LEAVE_APPROVED"), EntityOperator.AND), null, null, null, null);
						
				
				double finalApprovedDay = 0;
				GenericValue runtimeData2 = null;
				while ((runtimeData2=emplLeavIterator2.next())!= null){
					int noOfDaysApproved = 0;
					Timestamp stopDate2 = (Timestamp)runtimeData2.get("thruDate");
					Timestamp startDate2 = (Timestamp)runtimeData2.get("fromDate");
					String leaveDuration2 = runtimeData2.getString("leaveDuration");
					
					noOfDaysApproved = getDifferenceBetweenTwoDate(startDate2,stopDate2);//Find NumberOfDay for Indivisual Leave.
					
					//int holidays = getHolidayCount(delegator, companyId, startDate2, stopDate2);
					
					int holidays = 0;
					
					if (!leaveTypeId.equals("1001")){ 
						holidays = getHolidayCount(delegator, companyId, startDate2, stopDate2);
					}
	
	
					if(!leaveDuration2.equals("Full_Day")){
						finalApprovedDay = finalApprovedDay + .5;
					}else{
						finalApprovedDay = finalApprovedDay + noOfDaysApproved + 1 - holidays;
					}
				}
				emplLeavIterator2.close();
				
				row.put("approved", String.valueOf(finalApprovedDay));//Add Applied/Pending in the list 
				row.put("applied", String.valueOf(finalCreatedDay));//Add Approved in the list
				row.put("left", String.valueOf(totalAvailableLeave - finalApprovedDay));//Add Balance Leave in the list
				result.add(row);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}
	
		/**
		 * Get Two column from EmplLeaveTYpe table: LeaveTypeId, MaxAvaialbleLe
		 * 
		 * variable List resultleaveType = doQuery("EmplLeaveType",
		 * "Select leaveTypeId,MaxAvailableLeave from table where employeeId="
		 * +id);
		 * 
		 * 
		 * loop (resultleaveType Get total aproved leave from EmployeeLeave
		 * pseudo code
		 * 
		 * variable List resultleaveType = doQuery("EmplLeaveType",
		 * "Select leaveTypeId,MaxAvailableLeave from table where employeeId="
		 * +id);
		 * 
		 */
		
		
		request.setAttribute("result", result);
	
		return "success";
	}
	public static GenericValue getCurrentFiscalYear(Delegator delegator){
		
		 List<EntityExpr> exprs = FastList.newInstance();
		 exprs.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, "FISCAL_YEAR"));
		 exprs.add(EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "N"));
	
		List<GenericValue> currentFiscalYear = FastList.newInstance();
		try {
			currentFiscalYear = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		
		GenericValue oGenericValue = (currentFiscalYear!=null && currentFiscalYear.size()>0)?(GenericValue)currentFiscalYear.get(0):null;
		
		return oGenericValue;
	}
	public static int countLeaveBalanceByLeaveType(Delegator delegator, String partyId, String fiScalYearId, String leaveTypeId){
			
			int leaveBalance = 0;
			
			 List<EntityExpr> exprs = FastList.newInstance();
			 exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			 exprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, fiScalYearId));
			 exprs.add(EntityCondition.makeCondition("leaveTypeId", EntityOperator.EQUALS, leaveTypeId));
				
			List<GenericValue> employeeLeaves = null;
			try {
				employeeLeaves = delegator.findList("EmplLeaveBalance", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			
			if (employeeLeaves != null && employeeLeaves.size()>0){
				
				GenericValue oGenericValue = employeeLeaves.get(0);
				
				leaveBalance = Integer.parseInt(oGenericValue.get("remainingLeave").toString());
			}
			
			return leaveBalance;
			
		}
	public static int getHolidayCount(Delegator delegator, String companyId, Timestamp startDate, Timestamp endDate){
			
			
			List<EntityExpr> exprs = FastList.newInstance();
			exprs.add(EntityCondition.makeCondition("estimatedStartDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate));
			exprs.add(EntityCondition.makeCondition("estimatedStartDate", EntityOperator.LESS_THAN_EQUAL_TO, endDate));
			exprs.add(EntityCondition.makeCondition("companyId", EntityOperator.EQUALS, companyId));
	
			List<GenericValue> holidayList  = new ArrayList<GenericValue>();
			try {
				//delegator.findList(entityName, entityCondition, fieldsToSelect, orderBy, findOptions, useCache)
				holidayList = delegator.findList("WorkEffort", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, true);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			
			return holidayList.size();
		}
		
	public static int getDifferenceBetweenTwoDate(Timestamp startTime,Timestamp stopTime){
		
		Date startDate = new Date(startTime.getTime());
		Date endDate = new Date(stopTime.getTime());
		int difInDays = (int)((endDate.getTime() - startDate.getTime())/(1000*60*60*24));
		return difInDays;
	}
	
	public static Map<String, Object> findEmplLeave(DispatchContext dctx, Map<String, ? extends Object> context) {
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    Delegator delegator = dctx.getDelegator();
	    String approverPartyId = (String) context.get("approverPartyId");
	    
	    List<EntityExpr> exprs = FastList.newInstance();
		exprs.add(EntityCondition.makeCondition("approverPartyId", EntityOperator.EQUALS, approverPartyId));
		//exprs.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.NOT_EQUAL, "LEAVE_APPROVED"));
		
	    List<String> orderBy = UtilMisc.toList("partyId", "leaveTypeId", "fromDate", "lastUpdatedStamp");
	
		List<GenericValue> employeeLeaves = null;
		try {
			employeeLeaves = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		
	
		// Get leave status from item status
	    List<String> orderByLeaveStatus = UtilMisc.toList("sequenceId");
	
		List<GenericValue> employeeLeaveStatus = FastList.newInstance();
		try {
			employeeLeaveStatus = delegator.findList("StatusItem", EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, "LEAVE_STATUS"), null, orderByLeaveStatus, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		
		Map<String, Integer> leaveStatusMap = FastMap.newInstance();
		for (GenericValue genericValue : employeeLeaveStatus) {
			leaveStatusMap.put(genericValue.get("statusId").toString(), new Integer(genericValue.get("sequenceId").toString()));
		}
		
		/// filter to get only latest leave /////
		
		Map<String, GenericValue> row = FastMap.newInstance();
		for (GenericValue genericValue : employeeLeaves) {
			
			String key = genericValue.get("partyId").toString() + ":" + genericValue.get("leaveTypeId").toString() + ":" + genericValue.get("fromDate").toString();
			
			
			// We need to put the status which has highest sequence number.
			if (row.get(key) != null){
				
				GenericValue oGenericValue = row.get(key);
				
				Integer genericValueStatusSequence = leaveStatusMap.get(genericValue.get("leaveStatus").toString());
				Integer oGenericValueStatusSequence = leaveStatusMap.get(oGenericValue.get("leaveStatus").toString());
				
				if (genericValueStatusSequence.intValue() > oGenericValueStatusSequence.intValue()){
					row.put(key, genericValue);
				}
				
			} else{
				row.put(key, genericValue);
			}
			
		}
		
		
		// Remove approval leaves from the list for hradmin user.
		List<GenericValue> employeeLeavesNew = FastList.newInstance();
		
		for(Entry<String, GenericValue> entry : row.entrySet()) {
			
			GenericValue oGenericValue = entry.getValue();
			
			String leaveStatus = (String) oGenericValue.get("leaveStatus");
			
			//String key = oGenericValue.get("partyId").toString() + ":" + oGenericValue.get("leaveTypeId").toString() + ":" + oGenericValue.get("fromDate").toString();
			if (!leaveStatus.equals("LEAVE_APPROVED")){
			 
	        List<EntityExpr> exprsApprover = FastList.newInstance();
	        exprsApprover.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, oGenericValue.get("partyId").toString()));
	        exprsApprover.add(EntityCondition.makeCondition("leaveTypeId", EntityOperator.EQUALS, oGenericValue.get("leaveTypeId").toString()));
	        //exprsApprover.add(EntityCondition.makeCondition("leaveStatus",EntityOperator.EQUALS, "LEAVE_APPROVED"));
	        exprsApprover.add(EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS,  oGenericValue.get("fromDate")));
	        exprsApprover.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, oGenericValue.get("thruDate")));
			
	        // we are checking here if this leave is already approved by other hradmin, then we need not to show this leave for this hradmin
			List<GenericValue> emplLeavesApproved = null;
			
			try{
				emplLeavesApproved = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsApprover, EntityOperator.AND), null, null, null, false);
			}catch (GenericEntityException e) {}
			
			boolean addToApprover = true;
			for (GenericValue dbGenericValue: emplLeavesApproved){
				
				Integer dbGenericValueStatusSequence = leaveStatusMap.get(dbGenericValue.get("leaveStatus").toString());
				Integer oGenericValueStatusSequence = leaveStatusMap.get(oGenericValue.get("leaveStatus").toString());
				
				if (oGenericValueStatusSequence.intValue()<dbGenericValueStatusSequence.intValue()){
					addToApprover = false;
				}
			}
			
			if (emplLeavesApproved == null || emplLeavesApproved.size()==0 || addToApprover){ // we need to avoid to count if this leave is already in approval list.
				employeeLeavesNew.add(oGenericValue);
			}
			
		}
			
			/*if (!leaveStatus.equals("LEAVE_APPROVED")){
				employeeLeavesNew.add(oGenericValue);
			}*/
		
		}
		
		
		////////////////////////////////
		
		
		
	    result.put("employeeLeaveList", employeeLeavesNew);
	
	    return result;
	}
	public static List<GenericValue> getActionList(Delegator delegator, String status){
		
		 List<EntityExpr> exprs = FastList.newInstance();
		 exprs.add(EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, "LEAVE_STATUS"));
		 exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("LEAVE_WAIT_FOR_RECOM", "LEAVE_WAIT_FOR_APPR", "LEAVE_CREATED")));
			
		 if (status.equals("LEAVE_CREATED")){
			 exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("LEAVE_RECOMMENDED", "LEAVE_APPROVED")));			 
		 } else if (status.equals("LEAVE_WAIT_FOR_RECOM")){
			 exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("LEAVE_REVIEWED", "LEAVE_APPROVED")));			 
		} else if (status.equals("LEAVE_WAIT_FOR_APPR")){
		     exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("LEAVE_REVIEWED", "LEAVE_RECOMMENDED")));			 
		} else {
			return null;
		}
		 
		List<GenericValue> employeeLeaveStatus = FastList.newInstance();
		try {
			employeeLeaveStatus = delegator.findList("StatusItem", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		
		return employeeLeaveStatus;
	}
	public static String getLeaveActionDefaultValue(String leaveStatus){
		
		if (leaveStatus.equals("LEAVE_REVIEWED")){ return "LEAVE_WAIT_FOR_RECOM";}
		 else if (leaveStatus.equals("LEAVE_RECOMMENDED")){ return "LEAVE_WAIT_FOR_APPR";}
		 else return "";
	}
	
	public static boolean showApprover(String status){
		
		if (status.equals("LEAVE_REVIEWED") || status.equals("LEAVE_RECOMMENDED")) 
			return true;
		
		return false;
	}
	public static String getLeaveStatusDescription(Delegator delegator, String status){
		
		 List<EntityExpr> exprs = FastList.newInstance();
		 exprs.add(EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, "LEAVE_STATUS"));
		 exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, status));
			
		 
		List<GenericValue> employeeLeaveStatus = FastList.newInstance();
		try {
			employeeLeaveStatus = delegator.findList("StatusItem", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		
		
		if (employeeLeaveStatus.size()>0){
			return ((GenericValue)employeeLeaveStatus.get(0)).get("description").toString();
		}else
			return "";
		
	}
	
	public static Map<String, Object> updateEmplLeaveStatus(DispatchContext dctx, Map<String, ? extends Object> context) {
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    Delegator delegator = dctx.getDelegator();
	   
	    String approverPartyId = (String) context.get("approverPartyId");
	    Timestamp fromDate = (Timestamp) context.get("fromDate");
	    Timestamp thruDate = (Timestamp) context.get("thruDate");
	    String partyId = (String) context.get("partyId");
	    String leaveTypeId = (String) context.get("leaveTypeId");
	    String description = (String) context.get("description");
	    String remarks = (String) context.get("remarks");
	    String leaveAction = (String) context.get("leaveAction");
	    String emplLeaveReasonTypeId = (String) context.get("emplLeaveReasonTypeId");
	    String customTimePeriodId = (String) context.get("customTimePeriodId");
	    String leaveDuration = (String) context.get("leaveDuration");
	    String processId = (String) context.get("processId");
	    String stepOrder = (String) context.get("stepOrder");
	    String processIdParm=null;
	    String processStrIdParm=null;
	    String processStepIdParm=null;
	    String processNextStepIdParm=null;
	    
		try {
			
			boolean sendEmail = false;
			
			if (leaveAction.equals("LEAVE_APPROVED")){
				List<GenericValue> processStepDataList = null;
			     try {
			    	 processStepDataList = delegator.findByAnd("ProcessStepData", UtilMisc.toMap("authorizePartyId", approverPartyId, "processId", processId));
			     } catch (GenericEntityException e) {
			         e.printStackTrace();
			     }
			     String processStrId = null;
			     String processStepId = null;
			     if (UtilValidate.isNotEmpty(processStepDataList)) {
			         if (UtilValidate.isNotEmpty(processStepDataList.get(0).get("processStrId"))) {
			         	processStrId = processStepDataList.get(0).get("processStrId").toString();
			        	processStepId = processStepDataList.get(0).get("processStepId").toString();
			        	processStepIdParm=processStepId;
			        	processStrIdParm=processStepId;
			         }
			     }
			     Timestamp stamp = new Timestamp(System.currentTimeMillis());
			     GenericValue updateProcessData = null;
			     try {
			    	updateProcessData = delegator.findByPrimaryKey("ProcessStepData", UtilMisc.toMap("processId", processId, "processStrId", processStrId, "processStepId", processStepId, "authorizePartyId", approverPartyId));
			    	updateProcessData.set("statusId", leaveAction);		 
			    	updateProcessData.set("comments", remarks);	
			    	updateProcessData.set("dateAdded", stamp);	
			    	updateProcessData.store();
			     } catch (GenericEntityException e) {
						e.printStackTrace();
			     }			     
			     List<GenericValue> processStepsList = null;
			     try {
			    	 processStepsList = delegator.findByAnd("ProcessSteps", UtilMisc.toMap("stepId", processStepId, "processStrId", processStrId));
			     } catch (GenericEntityException e) {
			         e.printStackTrace();
			     }
			     String processStepOrder = null;
			     if (UtilValidate.isNotEmpty(processStepsList)) {
			         if (UtilValidate.isNotEmpty(processStepsList.get(0).get("stepOrder"))) {
			        	 processStepOrder = processStepsList.get(0).get("stepOrder").toString();
			         }
			     }
			     int stepno =  Integer.parseInt(processStepOrder);
			     int nextStepNo = stepno + 1;
			     String nextStepOrder = Integer.toString(nextStepNo);
			     List<GenericValue> processStepsNextList = null;
			     try {
			    	 processStepsNextList = delegator.findByAnd("ProcessSteps", UtilMisc.toMap("stepOrder", nextStepOrder, "processStrId", processStrId));
			     } catch (GenericEntityException e) {
			         e.printStackTrace();
			     }
			     
			     if (!processStepsNextList.isEmpty()){
			    	 String processAuthPartyId = null;
				     String nextProcessStepId = null;
				     if (UtilValidate.isNotEmpty(processStepsNextList)) {
				         if (UtilValidate.isNotEmpty(processStepsNextList.get(0).get("authPartyId"))) {
				        	 processAuthPartyId = processStepsNextList.get(0).get("authPartyId").toString();
				         }
				         if (UtilValidate.isNotEmpty(processStepsNextList.get(0).get("stepId"))) {
				        	 nextProcessStepId = processStepsNextList.get(0).get("stepId").toString();
				        	 processNextStepIdParm=nextProcessStepId;
				         }
				     }
			    	 GenericValue updatablePartyLeave = null; 
				     try{
				    	updatablePartyLeave = delegator.findByPrimaryKey("EmplLeave", UtilMisc.toMap("partyId", partyId,"leaveTypeId", leaveTypeId, "fromDate", fromDate));
				    	
				    	updatablePartyLeave.set("approverPartyId", processAuthPartyId);
				    	
				    	updatablePartyLeave.store();
				     } catch (GenericEntityException e) {
						e.printStackTrace();
					 }
				     Map<String, Object> newprocessStepData = FastMap.newInstance();
				     newprocessStepData.put("processStrId", processStrId);
				     newprocessStepData.put("processStepId", nextProcessStepId);
				     newprocessStepData.put("processId", processId);
				     newprocessStepData.put("authorizePartyId", processAuthPartyId);  
				     newprocessStepData.put("dateAdded", stamp); 
				     newprocessStepData.put("statusId", "LEAVE_WAIT_FOR_APPR"); 
				     try {
				  		GenericValue nextProcessStepDataLeave = delegator
				  				.makeValue("ProcessStepData",
				  						UtilMisc.toMap(newprocessStepData));
				  		nextProcessStepDataLeave.create();
				      } catch (Exception e) {

				      	e.printStackTrace();
				      } 
				     GenericValue userLogin = (GenericValue) context.get("userLogin");
				     List<GenericValue> notifiedUserList = null;
				     try {
					       notifiedUserList = delegator.findByAnd("ProcessStepNotifications", UtilMisc.toMap("stepId", processStepId ,"stepFilter",leaveAction));
					   } catch (GenericEntityException e) {
					       e.printStackTrace();
					   }
				  if (!notifiedUserList.isEmpty()){
				    for (GenericValue value : notifiedUserList) {
				     Map<String, Object> values = FastMap.newInstance();
				     values.put("toMailPartyId", value.get("notifiedParty"));
				     values.put("partyId", partyId);
				     values.put("approverPartyId", approverPartyId);
				     values.put("leaveAction", "LEAVE_STA_FOR_ALL");
				     values.put("userLogin", userLogin);
				     EmailServices.leavePartyNotificationMailSending(dctx, values);
				    }
				  }
				    Map<String, Object> values = FastMap.newInstance();
				    values.put("approverPartyId", approverPartyId);
				    values.put("partyId", partyId);
				     values.put("toMailPartyId", partyId);
				     values.put("leaveAction", "LEAVE_APPROVED_STEP");
				     values.put("userLogin", userLogin);
				     EmailServices.leavePartyNotificationMailSending(dctx, values);
				     Map<String, Object> values2 = FastMap.newInstance();
				     values2.put("toMailPartyId", processAuthPartyId);
				     values2.put("partyId", partyId);
				     values2.put("leaveAction", "LEAVE_WAIT_FOR_APPR");
				     values2.put("userLogin", userLogin);
				     EmailServices.leavePartyNotificationMailSending(dctx, values2);
			     }
			     else {
			    	GenericValue updatablePartyLeave = null; 
				    try{
				    	updatablePartyLeave = delegator.findByPrimaryKey("EmplLeave", UtilMisc.toMap("partyId", partyId,"leaveTypeId", leaveTypeId, "fromDate", fromDate));
				    	
				    	updatablePartyLeave.set("leaveStatus", leaveAction); // Need to work on here to change leave status for attendance Module Farhad
				    	updatablePartyLeave.store(); 
				    	
				    	Date startDate = new Date(fromDate.getTime());
					    Date endDate = new Date(thruDate.getTime());
					    List<Date> listOfDates = getDaysBetweenDates(startDate, endDate);
				    	GenericValue employee = null;
			    		List<GenericValue> employeeList = null;
			    		String employeeId = null;
			    		try {
			    			//employee = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", partyId));
			    			employeeList = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyId));
			    			employeeId = employeeList.get(0).get("userLoginId").toString();
						} catch (GenericEntityException e) {
							e.printStackTrace();
						}
				    	String noDays = updatablePartyLeave.get("numberOfDays").toString();
				    	for(int i = 0; i<listOfDates.size();i++){
				    		Date date1 = listOfDates.get(i);
				    		Timestamp leaveDate=new Timestamp(date1.getTime());  
				    		
				    		Map<String, Object> DailyAttendanceIn = FastMap.newInstance();
				    		DailyAttendanceIn.put("partyId", partyId);
							DailyAttendanceIn.put("dateOfAttendance", leaveDate);
							DailyAttendanceIn.put("employeeId", employeeId);
							DailyAttendanceIn.put("firstInTime", "");
							DailyAttendanceIn.put("lastOutTime", "");
							DailyAttendanceIn.put("attendanceResult", "Leave Approved");
							DailyAttendanceIn.put("attendanceStatus", "Leave Approved");
							DailyAttendanceIn.put("employeeComments", "");
							DailyAttendanceIn.put("processStatus", "Leave Approved");
							try {
								GenericValue DailyAttendanceInSetup = delegator.makeValue("DailyAttendance",
										UtilMisc.toMap(DailyAttendanceIn));
								delegator.createOrStore(DailyAttendanceInSetup);
							} catch (Exception e) {
								e.printStackTrace();
							}
				    	}
				    	
				    } catch (GenericEntityException e) {
						e.printStackTrace();
					}
				    GenericValue userLogin = (GenericValue) context.get("userLogin");
				     List<GenericValue> notifiedUserList = null;
				     try {
					       notifiedUserList = delegator.findByAnd("ProcessStepNotifications", UtilMisc.toMap("stepId", processStepId ,"stepFilter",leaveAction));
					   } catch (GenericEntityException e) {
					       e.printStackTrace();
					   }
				 if (!notifiedUserList.isEmpty()){
				    for (GenericValue value : notifiedUserList) {
				     Map<String, Object> values = FastMap.newInstance();
				     values.put("toMailPartyId", value.get("notifiedParty"));
				     values.put("approverPartyId", approverPartyId);
				     values.put("partyId", partyId);
				     values.put("leaveAction", "LEAVE_NEXT_FOR_ALL");
				     values.put("userLogin", userLogin);
				     EmailServices.leavePartyNotificationMailSending(dctx, values);
				    }
				 }
				    Map<String, Object> values = FastMap.newInstance();
				    values.put("approverPartyId", approverPartyId);
				    values.put("partyId", partyId);
				     values.put("toMailPartyId", partyId);
				     values.put("leaveAction", "LEAVE_APPROVED_FINAL");
				     values.put("userLogin", userLogin);
				     EmailServices.leavePartyNotificationMailSending(dctx, values);
				     
				     
			     }
			     sendEmail = true;
			} 
			else if(leaveAction.equals("LEAVE_REJECTED")){
			 
				List<EntityExpr> exprs = FastList.newInstance();
				//exprs.add(EntityCondition.makeCondition("approverPartyId", EntityOperator.EQUALS, approverPartyId));
			    exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			    exprs.add(EntityCondition.makeCondition("leaveTypeId", EntityOperator.EQUALS, leaveTypeId));
			    exprs.add(EntityCondition.makeCondition("emplLeaveReasonTypeId", EntityOperator.EQUALS, emplLeaveReasonTypeId));
			    exprs.add(EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS, fromDate));
			    exprs.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, thruDate));
				GenericValue emplLeaveInfo=delegator.findByPrimaryKey("EmplLeave", UtilMisc.toMap("partyId", partyId,"leaveTypeId", leaveTypeId, "fromDate", fromDate));
				delegator.removeByCondition("EmplLeave", EntityCondition.makeCondition(exprs, EntityOperator.AND), true);
	
		   		//--------------Employee Leave History Data Added-----------------//
			    Date date = new Date();
			    Timestamp todayDate = new Timestamp(date.getTime());
		        Map<String, Object> emplLeaveHis = FastMap.newInstance();
		        emplLeaveHis.put("partyId", partyId);
		        emplLeaveHis.put("leaveTypeId", leaveTypeId);
		        emplLeaveHis.put("leaveDuration", leaveDuration);
		        emplLeaveHis.put("emplLeaveReasonTypeId", emplLeaveReasonTypeId);
		        emplLeaveHis.put("fromDate", fromDate);
		        emplLeaveHis.put("thruDate", thruDate);
		        emplLeaveHis.put("approverPartyId", emplLeaveInfo.get(customTimePeriodId));
		        emplLeaveHis.put("leaveStatus", "Rejected");
		        emplLeaveHis.put("customTimePeriodId",emplLeaveInfo.get(customTimePeriodId));
		        emplLeaveHis.put("processId",emplLeaveInfo.get(processId));
		        emplLeaveHis.put("stepOrder",emplLeaveInfo.get(stepOrder));
		        emplLeaveHis.put("dateAdded", todayDate);
		        try {
		      		GenericValue emplLeaveHisAdd = delegator
		      				.makeValue("EmplLeaveHis",
		      						UtilMisc.toMap(emplLeaveHis));
		      		emplLeaveHisAdd.create();
		   	 	} 
		        catch (Exception e) { e.printStackTrace();}
		        //----------------------------------------------------------------------//
				
				 List<GenericValue> processStepDataList = null;
			     try {
			    	 processStepDataList = delegator.findByAnd("ProcessStepData", UtilMisc.toMap("authorizePartyId", approverPartyId, "processId", processId));
			     } catch (GenericEntityException e) {
			         e.printStackTrace();
			     }
			     String processStrId = null;
			     if (UtilValidate.isNotEmpty(processStepDataList)) {
			         if (UtilValidate.isNotEmpty(processStepDataList.get(0).get("processStrId"))) {
			         	processStrId = processStepDataList.get(0).get("processStrId").toString();
			         }
			     }
			     String processStepId = null;
			     if (UtilValidate.isNotEmpty(processStepDataList)) {
			         if (UtilValidate.isNotEmpty(processStepDataList.get(0).get("processStepId"))) {
			        	 processStepId = processStepDataList.get(0).get("processStepId").toString();
			        	 processStepIdParm=processStepId;
			         }
			     }
			     Timestamp stamp = new Timestamp(System.currentTimeMillis());
			     GenericValue updateProcessData = null;
			     try {
			    	 updateProcessData = delegator.findByPrimaryKey("ProcessStepData", UtilMisc.toMap("processId", processId, "processStrId", processStrId, "processStepId", processStepId, "authorizePartyId", approverPartyId));
			    			
			    	 updateProcessData.set("statusId", leaveAction);		 
			    	 updateProcessData.set("comments", remarks);	
			    	 updateProcessData.set("dateAdded", stamp);	
			    	 updateProcessData.store();
			     } catch (GenericEntityException e) {
						e.printStackTrace();
				 }
			     GenericValue userLogin = (GenericValue) context.get("userLogin");
			     List<GenericValue> notifiedUserList = null;
			     try {
				       notifiedUserList = delegator.findByAnd("ProcessStepNotifications", UtilMisc.toMap("stepId", processStepId ,"stepFilter",leaveAction));
				   } catch (GenericEntityException e) {
				       e.printStackTrace();
				   }
			 if (!notifiedUserList.isEmpty()){
			    for (GenericValue value : notifiedUserList) {
			     Map<String, Object> values = FastMap.newInstance();
			     values.put("toMailPartyId", value.get("notifiedParty"));
			     values.put("partyId", partyId);
			     values.put("approverPartyId", approverPartyId);
			     values.put("leaveAction", "LEAVE_STA_REJECTED");
			     values.put("userLogin", userLogin);
			     EmailServices.leavePartyNotificationMailSending(dctx, values);
			    }
			   }
			    Map<String, Object> values3 = FastMap.newInstance();
			    values3.put("partyId", partyId);
			    values3.put("approverPartyId", approverPartyId);
			    values3.put("leaveAction", "LEAVE_REJECTED");
			    values3.put("userLogin", userLogin);
			    EmailServices.leavePartyNotificationMailSending(dctx, values3);
			}
			/*GenericValue userLogin = (GenericValue) context.get("userLogin");
			Map<String, Object> values = FastMap.newInstance();
	        values.put("processId", processId);
	        values.put("processStepId", processStepIdParm);
	        values.put("nextProcessStepId", processNextStepIdParm);
	        values.put("approverPartyId", approverPartyId);
	        values.put("partyId", partyId);
	        values.put("leaveAction", leaveAction);
	        values.put("userLogin", userLogin);
	        values.put("remarks", remarks);
			if (sendEmail){
				//result = EmailServices.sendLeaveEmail(dctx, context, approverPartyId, partyId, leaveAction);
				result = EmailServices.leavePartyNotificationMailSending(dctx, values);
				EmailServices.leavePartyNotificationMailSending(dctx, values);
			}*/
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("partyId", partyId);;
	    return result;
	}
	public static Map<String, Object> createEmplLeave(DispatchContext dctx, Map<String, ? extends Object> context) {
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
	    Delegator delegator = dctx.getDelegator();

	    Timestamp fromDate = (Timestamp) context.get("fromDate");
	    String numberOfDays = (String) context.get("numberOfDays");
	    String emplLeaveNoOfLeave = (String) context.get("emplLeaveNoOfLeave");
	    Timestamp thruDate = (Timestamp) context.get("thruDate");
	    String partyId = (String) context.get("partyId");
	    String leaveTypeId = (String) context.get("leaveTypeId");
	    String description = (String) context.get("description");
	    String remarks = (String) context.get("remarks");
	    String leaveStatus = (String) context.get("leaveStatus");
	    String emplLeaveReasonTypeId = (String) context.get("emplLeaveReasonTypeId");
	    String companyId = "CSL";
	    String leaveDuration = (String) context.get("leaveDuration");
	    String year;
	    
	     
	     year = getFiscalYear(delegator, companyId);
	     if (year.equals("error")) {
	    	 result.put("_ERROR_MESSAGE_", "There have no current fiscal year. Please ask admin to add fiscal year");
	         return result;
	     }
	     List<GenericValue> EmplLeaveList = null;
	     try {
	    	 EmplLeaveList = delegator.findByAnd("EmplLeave", UtilMisc.toMap("partyId", partyId, "fromDate",fromDate,"leaveTypeId",leaveTypeId));
	     } catch (GenericEntityException e) {
	         e.printStackTrace();
	     }
	     if (!EmplLeaveList.isEmpty()){
	    	
	    	 result.put("leaveTypeId", leaveTypeId);
	    	 result.put("partyId", partyId);
	    	 result.put("leaveStatus", leaveStatus);
	    	 result.put("_ERROR_MESSAGE_", "Sorry! You Have Already Applied For This Date.");
	         return result;
	     }
	     
	     List<GenericValue> processPartyList = null;
	     try {
	    	 processPartyList = delegator.findByAnd("ProcessParty", UtilMisc.toMap("partyId", partyId, "processTypeId","LEAVE", "isActive","Y"));
	     } catch (GenericEntityException e) {
	         e.printStackTrace();
	     }
	     if (processPartyList.isEmpty()){
	    	 result.put("leaveTypeId", leaveTypeId);
	    	 result.put("partyId", partyId);
	    	 result.put("leaveStatus", leaveStatus);
	    	 result.put("_ERROR_MESSAGE_", "Sorry! You dont have leave workflow. Contact With your Admin ");
	         return result;
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
	    	 result.put("leaveTypeId", leaveTypeId);
	    	 result.put("partyId", partyId);
	    	 result.put("leaveStatus", leaveStatus);
	    	 result.put("_ERROR_MESSAGE_", "Sorry! You dont have leave workflow steps. Contact With your Admin ");
	         return result;
	     }
	     String processIdgen = delegator.getNextSeqId("WorkflowProcess");
	     Map<String, Object> processForLeave = FastMap.newInstance();
	     processForLeave.put("processId", processIdgen);
	     processForLeave.put("processStrId", processStrId);
	     processForLeave.put("ownerPartyId", partyId);
	      
	     try {
	  		GenericValue processLeave = delegator
	  				.makeValue("WorkflowProcess",
	  						UtilMisc.toMap(processForLeave));
	  		processLeave.create();
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
	     List<GenericValue> customTimePeriodIdList = null;
	     try {
	    	 customTimePeriodIdList = delegator.findByAnd("CustomTimePeriod", UtilMisc.toMap("periodTypeId", "FISCAL_YEAR", "isClosed","N"));
	     } catch (GenericEntityException e) {
	         e.printStackTrace();
	     }
	     String timePeriodId = null;
	     if (UtilValidate.isNotEmpty(customTimePeriodIdList)) {
	         if (UtilValidate.isNotEmpty(customTimePeriodIdList.get(0).get("customTimePeriodId"))) {
	        	 timePeriodId = customTimePeriodIdList.get(0).get("customTimePeriodId").toString();
	         }
	     }
	     Map<String, Object> emplLeave = FastMap.newInstance();
	     emplLeave.put("partyId", partyId);
	     emplLeave.put("leaveTypeId", leaveTypeId);
	     emplLeave.put("leaveDuration", leaveDuration);
	     emplLeave.put("emplLeaveReasonTypeId", emplLeaveReasonTypeId);
	     emplLeave.put("fromDate", fromDate);
	     emplLeave.put("thruDate", thruDate);
	     emplLeave.put("numberOfDays", numberOfDays);
	     emplLeave.put("approverPartyId", authPartyId);
	     emplLeave.put("leaveStatus", leaveStatus);
	     emplLeave.put("customTimePeriodId",timePeriodId);
	     emplLeave.put("description",description);
	     emplLeave.put("processId",processIdgen);
	     emplLeave.put("stepOrder","1");
	     try {
	   		GenericValue emplLeaveAdd = delegator
	   				.makeValue("EmplLeave",
	   						UtilMisc.toMap(emplLeave));
	   		emplLeaveAdd.create();
	   		
	   		//--------------Employee Leave History Data Added-----------------//
	   	    Date date = new Date();
	   	    Timestamp todayDate = new Timestamp(date.getTime());
	        Map<String, Object> emplLeaveHis = FastMap.newInstance();
	        emplLeaveHis.put("partyId", partyId);
	        emplLeaveHis.put("leaveTypeId", leaveTypeId);
	        emplLeaveHis.put("leaveDuration", leaveDuration);
	        emplLeaveHis.put("emplLeaveReasonTypeId", emplLeaveReasonTypeId);
	        emplLeaveHis.put("fromDate", fromDate);
	        emplLeaveHis.put("thruDate", thruDate);
	        emplLeaveHis.put("approverPartyId", authPartyId);
	        emplLeaveHis.put("leaveStatus", leaveStatus);
	        emplLeaveHis.put("customTimePeriodId",timePeriodId);
	        emplLeaveHis.put("processId",processIdgen);
	        emplLeaveHis.put("stepOrder","1");
	        emplLeaveHis.put("dateAdded", todayDate);
	        try {
	      		GenericValue emplLeaveHisAdd = delegator
	      				.makeValue("EmplLeaveHis",
	      						UtilMisc.toMap(emplLeaveHis));
	      		emplLeaveHisAdd.create();
	   	 	} 
	        catch (Exception e) { e.printStackTrace();}
		 } 
	     catch (Exception e) { e.printStackTrace();}
	     
	     
	     //----------------Data Insert in ProcessStepData Table-----------------//
	     Timestamp stamp = new Timestamp(System.currentTimeMillis());
	     Map<String, Object> emplLeaveProcessData = FastMap.newInstance();
	    
	     emplLeaveProcessData.put("processStrId", processStrId);
	     emplLeaveProcessData.put("processStepId", processStepId);
	     emplLeaveProcessData.put("processId", processIdgen);
	     emplLeaveProcessData.put("authorizePartyId", authPartyId);
	     emplLeaveProcessData.put("dateAdded", stamp);
	     emplLeaveProcessData.put("statusId", "LEAVE_WAIT_FOR_APPR");
	     try {
    		GenericValue processStepData = delegator
    				.makeValue("ProcessStepData",
    						UtilMisc.toMap(emplLeaveProcessData));
    		processStepData.create();
         } catch (Exception e) {
	        	e.printStackTrace();
	     }
	     //------------------------------------------------------------------------//
	     try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Map<String, Object> values = FastMap.newInstance();
	        /*values.put("processId", processIdgen);
	        values.put("processStepId", processStepId);
	        values.put("nextProcessStepId", processStepId);*/
	        values.put("toMailPartyId", authPartyId);
	        values.put("partyId", partyId);
	        values.put("leaveAction", "LEAVE_WAIT_FOR_APPR");
	        values.put("userLogin", userLogin);
	        
			result = EmailServices.leavePartyNotificationMailSending(dctx, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	     result.put("partyId", partyId);
	     result.put("leaveTypeId", leaveTypeId);
	//     result.put("fromDate", fromDate);
	     result.put("leaveStatus", leaveStatus);
	     result.put("_EVENT_MESSAGE_", "Successfully created ");
		return result;
	}
	
	public static String getFiscalYear(Delegator delegator, String partyIdFrom) {
	    List condition1 = FastList.newInstance();
	    condition1.add(EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "N"));
	    condition1.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, partyIdFrom));
	    EntityCondition cond1 = EntityCondition.makeCondition(condition1, EntityOperator.AND);
	
	    List<GenericValue> data1 = null;
	    try {
	        data1 = delegator.findList("CustomTimePeriod", cond1, null, null, null, false);
	    } catch (GenericEntityException e) {
	        e.printStackTrace();
	    }
	
	    GenericValue fiscalYear = null;
	    if (data1.size() != 0) {
	        fiscalYear = data1.get(0);
	        Date date = (Date) fiscalYear.get("fromDate");
	        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Dhaka"));
	        int year = calendar.get(Calendar.YEAR);
	
	        return String.valueOf(year);
	    }
	
	    return "error";
	}

	public static String getFullNameByPartyId(Delegator delegator, String partyId) {
		GenericValue partyPersonnalInfo=null;
		try {
		   partyPersonnalInfo = delegator.findByPrimaryKey("PartyAndPerson", UtilMisc.toMap("partyId", partyId));

		   String firstName="";
		   String middleName="";
		   String lastName="";
		   try{
	         if (UtilValidate.isNotEmpty(partyPersonnalInfo.get("firstName").toString())) {
	        	 firstName=partyPersonnalInfo.get("firstName").toString();
	         }	
		   }catch (Exception e) {}
		   try{
	         if (UtilValidate.isNotEmpty(partyPersonnalInfo.get("firstName").toString())) {
				   middleName=partyPersonnalInfo.get("middleName").toString();
	         }
		   }catch (Exception e) {}
		   try{
	         if (UtilValidate.isNotEmpty(partyPersonnalInfo.get("lastName").toString())) {
				   lastName=partyPersonnalInfo.get("lastName").toString();
	         }	
		   }catch (Exception e) {}
		   String partyName=firstName+" "+ middleName+" "+lastName;
	    return partyName;
		}catch(Exception e){return "error";}
	}
	
	public static List<Date> getDaysBetweenDates(Date startdate, Date enddate)
	{
	    List<Date> dates = new ArrayList<Date>();
	    Calendar calendar = new GregorianCalendar();
	    calendar.setTime(startdate);

	    while (calendar.getTime().before(enddate))
	    {
	        Date result = calendar.getTime();
	        dates.add(result);
	        calendar.add(Calendar.DATE, 1);
	    }
	    dates.add(enddate);
	    return dates;
	}

}