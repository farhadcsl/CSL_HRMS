/**
 * 
 */
package org.ofbiz.humanres.attendance;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.ServiceUtil;

/**
 * @author zzz
 * @author zzz
 *
 */
public class AttendanceReportUtils {
	
/*
* Get Today Employee Recruitment Information
* */	
public static Map<String,String> getTodayRecruitmentInformation(Delegator delegator,String orgPartyId ){
		Map<String,String> todayRecruitmentInformation=FastMap.newInstance();
		List<GenericValue> appliedEmployee=FastList.newInstance();
		List<GenericValue> recruitmentEmployee=FastList.newInstance();
		
		Date today=new Date(System.currentTimeMillis());
		long sTime= AttendanceUtils.getStartTime(today);
		long eTime= AttendanceUtils.getEndTime(today);
		
		
		List<EntityExpr> exprsApplied = FastList.newInstance();
		exprsApplied.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "APPLICANT_CREATED"));
		exprsApplied.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, orgPartyId));
        exprsApplied.add(EntityCondition.makeCondition("applicationDate", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(sTime)));
        exprsApplied.add(EntityCondition.makeCondition("applicationDate", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(eTime)));
        

        List<EntityExpr> exprsApproved= FastList.newInstance();
        exprsApproved.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "CONFIRMATION"));
        exprsApproved.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, orgPartyId));
        exprsApproved.add(EntityCondition.makeCondition("applicationDate", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(sTime)));
        exprsApproved.add(EntityCondition.makeCondition("applicationDate", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(eTime)));
        
		
		try {
			appliedEmployee = delegator.findList("EmploymentAppAndOrganization", EntityCondition.makeCondition(exprsApplied, EntityOperator.AND), null, null, null, false);
			recruitmentEmployee=delegator.findList("EmploymentAppAndOrganization", EntityCondition.makeCondition(exprsApproved, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			return null;
		}
		todayRecruitmentInformation.put("appliedEmployee", String.valueOf(appliedEmployee.size()));
		todayRecruitmentInformation.put("recruitmentEmployee", String.valueOf(recruitmentEmployee.size()));
		
		return todayRecruitmentInformation;
	}
	
/*
* Get Today Employee Job Posting Information
* */		
public static Map<String,String> getTodayJobPostingInformation(Delegator delegator,String orgPartyId ){
		
		Map<String,String> todayJobPostingInformation=FastMap.newInstance();
		List<GenericValue> jobPosting=FastList.newInstance();
		
		Date today=new Date(System.currentTimeMillis());
		long sTime= AttendanceUtils.getStartTime(today);
		long eTime= AttendanceUtils.getEndTime(today);
		
		
		List<EntityExpr> exprsApplied = FastList.newInstance();
		exprsApplied.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, orgPartyId));
        exprsApplied.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(sTime)));
        exprsApplied.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(eTime)));  
        
		
		try {
			jobPosting = delegator.findList("JobPostingAndOrganization", EntityCondition.makeCondition(exprsApplied, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			return null;
		}
		todayJobPostingInformation.put("jobPosting", String.valueOf(jobPosting.size()));
		
		return todayJobPostingInformation;
	}
	
/*
* Get Today Employee Job Requisition Information
* */	
public static Map<String,String> getTodayJobRequisitionInformation(Delegator delegator,String orgPartyId ){
		Map<String,String> todayJobRequisitionInformation=FastMap.newInstance();
		List<GenericValue> appliedJobRequisition=FastList.newInstance();
		List<GenericValue> approvedJobRequisition=FastList.newInstance();
		
		Date today=new Date(System.currentTimeMillis());
		long sTime= AttendanceUtils.getStartTime(today);
		long eTime= AttendanceUtils.getEndTime(today);
		
		
		List<EntityExpr> exprsApplied = FastList.newInstance();
		exprsApplied.add(EntityCondition.makeCondition("requisitionStatus", EntityOperator.EQUALS, "JOB_REQ_CREATED"));
		exprsApplied.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, orgPartyId));
        exprsApplied.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(sTime)));
        exprsApplied.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(eTime)));
        

        List<EntityExpr> exprsApproved= FastList.newInstance();
        exprsApproved.add(EntityCondition.makeCondition("requisitionStatus", EntityOperator.EQUALS, "JOB_REQ_APPROVED"));
        exprsApproved.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, orgPartyId));
        exprsApproved.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(sTime)));
        exprsApproved.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(eTime)));
        
		
		try {
			appliedJobRequisition = delegator.findList("JobRequisitionAndOrganization", EntityCondition.makeCondition(exprsApplied, EntityOperator.AND), null, null, null, false);
			approvedJobRequisition=delegator.findList("JobRequisitionAndOrganization", EntityCondition.makeCondition(exprsApproved, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			return null;
		}
		todayJobRequisitionInformation.put("appliedJobRequisition", String.valueOf(appliedJobRequisition.size()));
		todayJobRequisitionInformation.put("approvedJobRequisition", String.valueOf(approvedJobRequisition.size()));
		
		return todayJobRequisitionInformation;
	}
	


/*
* Get Today Employee Leave Information
* */	
public static Map<String,String> getTodayLeaveInformation(Delegator delegator,String orgPartyId ){
		Map<String,String> todayLeaveInformation=FastMap.newInstance();
		List<GenericValue> appliedEmpLeave=FastList.newInstance();
		List<GenericValue> approvedEmpLeave=FastList.newInstance();
		
		Date today=new Date(System.currentTimeMillis());
		long sTime= AttendanceUtils.getStartTime(today);
		long eTime= AttendanceUtils.getEndTime(today);
		
		
		List<EntityExpr> exprsApplied = FastList.newInstance();
		exprsApplied.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_CREATED"));
		exprsApplied.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, orgPartyId));
        exprsApplied.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(sTime)));
        exprsApplied.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(eTime)));
        

        List<EntityExpr> exprsApproved= FastList.newInstance();
        exprsApproved.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
        exprsApproved.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, orgPartyId));
        exprsApproved.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(sTime)));
        exprsApproved.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(eTime)));
        
		
		try {
			appliedEmpLeave = delegator.findList("EmpLeaveAndOrganization", EntityCondition.makeCondition(exprsApplied, EntityOperator.AND), null, null, null, false);
			approvedEmpLeave=delegator.findList("EmpLeaveAndOrganization", EntityCondition.makeCondition(exprsApproved, EntityOperator.AND), null, null, null, false);
			
		} catch (GenericEntityException e) {
			return null;
		}
		todayLeaveInformation.put("appliedLeave", String.valueOf(appliedEmpLeave.size()));
		todayLeaveInformation.put("approveLeave", String.valueOf(approvedEmpLeave));
		
		return todayLeaveInformation;
	}


/*
* Get Today Employee Absence Information
* */	
public static Map<String, String> getTodaysAttendanceAbsentEmployees(Delegator delegator,String	orgPartyId){
		
		int absentEmployees=0;
		int presentEmployees=0;
		
		Map<String,String> attendanceNotifications=FastMap.newInstance();

		String maximumStayTime="";
		
		GenericValue orgPreference=	AttendanceUtils.getOrgPreference(delegator,orgPartyId);
		if(orgPreference!=null){
			
			if(orgPreference.get("maximumStayTime")!=null){
				 maximumStayTime=(String)orgPreference.get("maximumStayTime");
			}
			 
		}

		Date toDay=new Date(System.currentTimeMillis());
	
		List<GenericValue> partyRelationships=FastList.newInstance();
		List<Timestamp> holidays=new ArrayList<Timestamp>();
	
		partyRelationships = AttendanceUtils.getTotalEmployee(delegator, partyRelationships, orgPartyId);

		
		for(GenericValue partyRelationship:partyRelationships){
			
				List<GenericValue> employeeLeaves=new ArrayList<GenericValue>();
				
				String employeeId=(String)partyRelationship.get("partyId");
				
				
		        List<EntityExpr> exprsList = FastList.newInstance();
		        exprsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
		        exprsList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
				
				try {
					employeeLeaves = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsList, EntityOperator.AND), null, null, null, false);
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
				
				GenericValue empOrRostPreference=	AttendanceUtils.getEmpORRosterPreference(delegator,orgPartyId, employeeId);
				
				if(empOrRostPreference!=null){
				
					if(empOrRostPreference.get("maximumStayTime")!=null){
						 maximumStayTime=(String)empOrRostPreference.get("maximumStayTime");
					}
					
				}
			
				
				List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
				
				if(!AttendanceUtils.checkHoliday(holidays, new Timestamp(toDay.getTime()))&&!AttendanceUtils.checkEmpLeave(employeeLeaves,toDay.getTime())){
					
					GenericValue dayPreference=	AttendanceUtils.getDayPreference(delegator, toDay, orgPartyId);
					
					if(dayPreference!=null){
					
						if(dayPreference.get("maximumStayTime")!=null){
							 maximumStayTime=(String)dayPreference.get("maximumStayTime");
						}
						
					}
					
					long maximumStayTimeInDate=AttendanceUtils.getLateEntryTimeInDate(maximumStayTime,toDay);
					long startTime=AttendanceUtils.getStartTime(toDay);
					
					List<EntityExpr> exprs = FastList.newInstance();
					List<String> orderBy = FastList.newInstance();
					orderBy.add("logtimeStamp ASC");
					exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(startTime)));
					exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(maximumStayTimeInDate)));
					exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
					
					try {
						//delegator.findList(entityName, entityCondition, fieldsToSelect, orderBy, findOptions, useCache)
						employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);
						if(employeeLogs.size()<=0){
							absentEmployees++;
						}else{
							presentEmployees++;
						}
					
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
					
				}
			}
		
		attendanceNotifications.put("absentEmployees",String.valueOf(absentEmployees));
		attendanceNotifications.put("presentEmployees",String.valueOf(presentEmployees));

		return attendanceNotifications;
	}
/*
 * Get Today Employee Late Information
 * */	
public static Map<String, String> getTodaysAttendanceLateEmployees(Delegator delegator,String	orgPartyId){
		
		int lateEmployees=0;
		
		Map<String,String> attendanceNotifications=FastMap.newInstance();

		String maximumStayTime="";
		String lateEntryTime="";
		GenericValue orgPreference=	AttendanceUtils.getOrgPreference(delegator,orgPartyId);
		if(orgPreference!=null){
			
			if(orgPreference.get("maximumStayTime")!=null){
				 maximumStayTime=(String)orgPreference.get("maximumStayTime");
			}
			if(orgPreference.get("lateEntryTime")!=null){
				lateEntryTime=(String)orgPreference.get("lateEntryTime");
			}
			 
		}

		Date toDay=new Date(System.currentTimeMillis());
	
		List<GenericValue> partyRelationships=FastList.newInstance();
		List<Timestamp> holidays=new ArrayList<Timestamp>();
	
		partyRelationships = AttendanceUtils.getTotalEmployee(delegator, partyRelationships, orgPartyId);


		for(GenericValue partyRelationship:partyRelationships){
		
			List<GenericValue> employeeLeaves=new ArrayList<GenericValue>();
			
			String employeeId=(String)partyRelationship.get("partyId");
			
			
		    List<EntityExpr> exprsList = FastList.newInstance();
		    exprsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
		    exprsList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
			
			try {
				employeeLeaves = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsList, EntityOperator.AND), null, null, null, false);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			
			
			GenericValue empOrRostPreference=	AttendanceUtils.getEmpORRosterPreference(delegator,orgPartyId, employeeId);
			
			if(empOrRostPreference!=null){
			
				if(empOrRostPreference.get("maximumStayTime")!=null){
					 maximumStayTime=(String)empOrRostPreference.get("maximumStayTime");
				}
				if(orgPreference.get("lateEntryTime")!=null){
					lateEntryTime=(String)orgPreference.get("lateEntryTime");
				}
				
			}
		
			
			List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
			
			if(!AttendanceUtils.checkHoliday(holidays, new Timestamp(toDay.getTime()))&&!AttendanceUtils.checkEmpLeave(employeeLeaves,toDay.getTime())){
				
				GenericValue dayPreference=	AttendanceUtils.getDayPreference(delegator, toDay, orgPartyId);
				
				if(dayPreference!=null){
				
					if(dayPreference.get("maximumStayTime")!=null){
						 maximumStayTime=(String)dayPreference.get("maximumStayTime");
					}
					if(orgPreference.get("lateEntryTime")!=null){
						lateEntryTime=(String)orgPreference.get("lateEntryTime");
					}
					
				}
				
				long maximumStayTimeInDate=AttendanceUtils.getLateEntryTimeInDate(maximumStayTime,toDay);
				long lateEntryTimeInDate=AttendanceUtils.getLateEntryTimeInDate(lateEntryTime,toDay);
				long startTime=AttendanceUtils.getStartTime(toDay);
				
				List<EntityExpr> exprs = FastList.newInstance();
				List<String> orderBy = FastList.newInstance();
				orderBy.add("logtimeStamp ASC");
				exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(startTime)));
				exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(maximumStayTimeInDate)));
				exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
				
				try {
					//delegator.findList(entityName, entityCondition, fieldsToSelect, orderBy, findOptions, useCache)
					employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);
					if(employeeLogs.size()>0){
						GenericValue empLog=employeeLogs.get(0);
						Timestamp logTime=empLog.getTimestamp("logtimeStamp");
						if(logTime.getTime()>=lateEntryTimeInDate){
							lateEmployees++;
						}
					}
				
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
				
			}
		}
		
		attendanceNotifications.put("lateEmployees",String.valueOf(lateEmployees));
		
		return attendanceNotifications;
	}
	

public static List<Map<String, String>> getMonthlyLeaveInformation(Delegator delegator,String orgPartyId ){
	List<Map<String, String>> monthFreqList = FastList.newInstance();
		
		List<GenericValue> approvedEmpLeave=FastList.newInstance();
		List<GenericValue> approvedEmpLeaveMnth=FastList.newInstance();
	
		
		
		List<EntityExpr> exprsApproved= FastList.newInstance();
		exprsApproved.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
		exprsApproved.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, orgPartyId));
	
	
	try {
		approvedEmpLeave = delegator.findList("EmpLeaveAndOrganization", EntityCondition.makeCondition(exprsApproved, EntityOperator.AND), null, null, null, false);
		
		//get the current date and year
		int currentYear = UtilDateTime.getYear(UtilDateTime.nowTimestamp(), TimeZone.getDefault(), Locale.getDefault());
		
		
	    int monthe = 0;
		while (monthe < 12){
			Map<String,String> monthlyLeaveInformation=FastMap.newInstance();
			//get monthe start date
			Calendar calendarStart=Calendar.getInstance();
		    	calendarStart.set(Calendar.YEAR,currentYear);
		    	calendarStart.set(Calendar.MONTH,monthe);
		    	calendarStart.set(Calendar.DAY_OF_MONTH,1);
		    Timestamp yearBeginVal = new Timestamp(calendarStart.getTimeInMillis());
		    Timestamp monthBegin = UtilDateTime.adjustTimestamp(yearBeginVal, Calendar.MONTH, 0);
		    //get the monthe end date
			 Calendar calendarEnd=Calendar.getInstance();
			    calendarEnd.set(Calendar.YEAR,currentYear);
			    calendarEnd.set(Calendar.MONTH,monthe);
			    calendarEnd.set(Calendar.DAY_OF_MONTH,31);
			 Timestamp yearEndVal = new Timestamp(calendarEnd.getTimeInMillis());
			 Timestamp monthEnd = UtilDateTime.adjustTimestamp(yearEndVal, Calendar.MONTH, 0);
			 
			 //get the monthly leave date
			 List<EntityExpr> exprsApprovedMonth= FastList.newInstance();
				exprsApprovedMonth.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, monthBegin));
				exprsApprovedMonth.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, monthEnd));
			approvedEmpLeaveMnth = delegator.findList("EmpLeaveAndOrganization", EntityCondition.makeCondition(exprsApprovedMonth, EntityOperator.AND), null, null, null, false);
			int totalLeave = 0;
			for(GenericValue val : approvedEmpLeaveMnth){
				Timestamp startTime = (Timestamp)val.get("fromDate");
				Timestamp stopTime = (Timestamp)val.get("thruDate");
				int leaveNo = getDifferenceBetweenTwoDate(startTime, stopTime);
				//count the leave
				totalLeave = leaveNo + totalLeave;
			}
			
			
			
			monthlyLeaveInformation.put("monthNo", ""+monthe);
			monthlyLeaveInformation.put("approveLeaveMonthlyList", ""+totalLeave);
			monthFreqList.add(monthlyLeaveInformation);
			
			monthe++;
			monthBegin = UtilDateTime.adjustTimestamp(monthBegin, Calendar.MONTH, 1);
			monthEnd = UtilDateTime.adjustTimestamp(monthEnd, Calendar.MONTH, 1);
		}
	
	
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return monthFreqList;

}
public static int getDifferenceBetweenTwoDate(Timestamp startTime,Timestamp stopTime){
	Date startDate = new Date(startTime.getTime());
	Date	endDate = new Date(stopTime.getTime());
	 int difInDays = (int)((endDate.getTime() - startDate.getTime())/(1000*60*60*24))+1;
	 return difInDays;
}


public static Map<String, Object> getMonthlyDataForTerminate(Delegator delegator,String orgPartyId) throws GenericEntityException {
	
	Map<String, Object> result = ServiceUtil.returnSuccess();
	List<GenericValue> Employment = delegator.findByAnd("Employment", UtilMisc.toMap("partyIdFrom", orgPartyId.trim()));
	int[] months = {0,0,0,0,0,0,0,0,0,0,0,0,0};
	
	int currentYear = Calendar.getInstance().get(Calendar.YEAR);
	Calendar terminateDateTime = Calendar.getInstance();
	int monthIndex=0;
	for(GenericValue employment: Employment){
		/*getting terminate month from employment..*/
		Timestamp terminateDate=null;
		if(employment.size()>0){
			terminateDate = employment.getTimestamp("thruDate");
			if(UtilValidate.isNotEmpty(terminateDate)){
			long calTime = terminateDate.getTime();
			terminateDateTime.setTimeInMillis(calTime);
			monthIndex = terminateDateTime.get(Calendar.MONTH);
			int year = terminateDateTime.get(Calendar.YEAR);
			if(currentYear==year){
				months[monthIndex]+=1;
			}
			}
		}
	}
	result.put("January", String.valueOf(months[0]));
	result.put("February", String.valueOf(months[1]));
	result.put("March", String.valueOf(months[2]));
	result.put("April", String.valueOf(months[3]));
	result.put("May", String.valueOf(months[4]));
	result.put("June", String.valueOf(months[5]));
	result.put("July", String.valueOf(months[6]));
	result.put("August", String.valueOf(months[7]));
	result.put("September", String.valueOf(months[8]));
	result.put("October", String.valueOf(months[9]));
	result.put("November", String.valueOf(months[10]));
	result.put("December", String.valueOf(months[11]));
		
	return result;	
}


/*
* Get Today Employee Absence Information
* */	
public static Map<String, String> getTodaysAttendanceInfo(Delegator delegator,String	orgPartyId){
		
		int absentEmployees=0;
		int presentEmployees=0;
		int lateEmployees=0;
		
		Map<String,String> attendanceNotifications=FastMap.newInstance();

		String maximumStayTime="";
		String lateEntryTime="";
		
		GenericValue orgPreference=	AttendanceUtils.getOrgPreference(delegator,orgPartyId);
		if(orgPreference!=null){
			
			if(orgPreference.get("maximumStayTime")!=null){
				 maximumStayTime=(String)orgPreference.get("maximumStayTime");
			}
			if(orgPreference.get("lateEntryTime")!=null){
				lateEntryTime=(String)orgPreference.get("lateEntryTime");
			}
			 
		}

		Date toDay=new Date(System.currentTimeMillis());
	
		List<GenericValue> partyRelationships=FastList.newInstance();
		List<Timestamp> holidays=new ArrayList<Timestamp>();
	
		partyRelationships = AttendanceUtils.getTotalActiveEmployee(delegator, partyRelationships, orgPartyId);

		
		for(GenericValue partyRelationship:partyRelationships){
			
				List<GenericValue> employeeLeaves=new ArrayList<GenericValue>();
				
				String employeeId=(String)partyRelationship.get("partyId");
				
				
		        List<EntityExpr> exprsList = FastList.newInstance();
		        exprsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
		        exprsList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
				
				try {
					employeeLeaves = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsList, EntityOperator.AND), null, null, null, false);
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
				
				GenericValue empOrRostPreference=	AttendanceUtils.getEmpORRosterPreference(delegator,orgPartyId, employeeId);
				
				if(empOrRostPreference!=null){
				
					if(empOrRostPreference.get("maximumStayTime")!=null){
						 maximumStayTime=(String)empOrRostPreference.get("maximumStayTime");
					}
					if(orgPreference.get("lateEntryTime")!=null){
						lateEntryTime=(String)orgPreference.get("lateEntryTime");
					}
					
				}
			
				
				List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
				
				if(!AttendanceUtils.checkHoliday(holidays, new Timestamp(toDay.getTime()))&&!AttendanceUtils.checkEmpLeave(employeeLeaves,toDay.getTime())){
					
					GenericValue dayPreference=	AttendanceUtils.getDayPreference(delegator, toDay, orgPartyId);
					
					if(dayPreference!=null){
					
						if(dayPreference.get("maximumStayTime")!=null){
							 maximumStayTime=(String)dayPreference.get("maximumStayTime");
						}
						if(orgPreference.get("lateEntryTime")!=null){
							lateEntryTime=(String)orgPreference.get("lateEntryTime");
						}
						
					}
					
					long maximumStayTimeInDate=AttendanceUtils.getLateEntryTimeInDate(maximumStayTime,toDay);
					long lateEntryTimeInDate=AttendanceUtils.getLateEntryTimeInDate(lateEntryTime,toDay);
					long startTime=AttendanceUtils.getStartTime(toDay);
					
					List<EntityExpr> exprs = FastList.newInstance();
					List<String> orderBy = FastList.newInstance();
					orderBy.add("logtimeStamp ASC");
					exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(startTime)));
					exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(maximumStayTimeInDate)));
					exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
					
					try {
						//delegator.findList(entityName, entityCondition, fieldsToSelect, orderBy, findOptions, useCache)
						employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);
						if(employeeLogs.size()<=0){
							absentEmployees++;
							
						}else{
							presentEmployees++;							
							GenericValue empLog=employeeLogs.get(0);
							Timestamp logTime=empLog.getTimestamp("logtimeStamp");
							if(logTime.getTime()>=lateEntryTimeInDate){
								lateEmployees++;
							}
						}
					
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
					
				}
			}
		
		attendanceNotifications.put("absentEmployees",String.valueOf(absentEmployees));
		attendanceNotifications.put("presentEmployees",String.valueOf(presentEmployees));
		attendanceNotifications.put("lateEmployees",String.valueOf(lateEmployees));

		return attendanceNotifications;
	}

/**
* Gets the random color.
*
* @return the random color
*/
public static String getRandomColor() {
     String[] letters = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
     String color = "#";
     for (int i = 0; i < 6; i++ ) {
        color += letters[(int) Math.round(Math.random() * 15)];
     }
     return color;
}

/**
* Get Today Employee Absence Information
* @author zzz
* */	
public static Map<String, String> getEmployeeAbsenceInformationByDate(Delegator delegator,List<GenericValue> employeeList,Date date,String orgPartyId){
		
		int absentEmployees=0;
		int presentEmployees=0;
		int leaveEmployees=0;
		
		Map<String,String> attendanceNotifications=FastMap.newInstance();

		Date toDay=date;

		
		for(GenericValue employee:employeeList){
			
				List<GenericValue> employeeLeaves=new ArrayList<GenericValue>();
				
				String partyId=(String)employee.get("partyId");
				
		        List<EntityExpr> exprsList = FastList.newInstance();
		        exprsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		        exprsList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
				
				try {
					employeeLeaves = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsList, EntityOperator.AND), null, null, null, false);
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
				
				
				if(!AttendanceUtils.checkEmpLeave(employeeLeaves,toDay.getTime())){
					
					Timestamp dayStart=AttendanceUtils.getModifiedDayStart( new Timestamp(toDay.getTime()));
					Timestamp dayEnd=AttendanceUtils.getModifiedDayEnd( new Timestamp(toDay.getTime()));
					
					List<Timestamp> holidays=AttendanceUtils.getHolidays(orgPartyId, dayStart, dayEnd, delegator);
					
					if(holidays==null||holidays.size()<=0){
						
						List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
					
						List<EntityExpr> exprs = FastList.newInstance();
						List<String> orderBy = FastList.newInstance();
						orderBy.add("logtimeStamp ASC");
						exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
						exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
						exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
						
						try {
							//delegator.findList(entityName, entityCondition, fieldsToSelect, orderBy, findOptions, useCache)
							employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);
							if(employeeLogs.size()<=0){
								absentEmployees++;
							}else{
								presentEmployees++;
							}
						
						} catch (GenericEntityException e) {
							e.printStackTrace();
						}
					
					}
					
				}else{
					if(UtilValidate.isNotEmpty(employeeLeaves)&&employeeLeaves.size()>0){
						leaveEmployees++;
						String leaveDuration = employeeLeaves.get(0).getString("leaveDuration");
						if(leaveDuration.equals("half_Day_Evening") || leaveDuration.equals("half_Day_Morning")){

							
							Timestamp dayStart=AttendanceUtils.getModifiedDayStart( new Timestamp(toDay.getTime()));
							Timestamp dayEnd=AttendanceUtils.getModifiedDayEnd( new Timestamp(toDay.getTime()));
							
							List<Timestamp> holidays=AttendanceUtils.getHolidays(orgPartyId, dayStart, dayEnd, delegator);
							
							if(holidays==null||holidays.size()<=0){
								
								List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
							
								List<EntityExpr> exprs = FastList.newInstance();
								List<String> orderBy = FastList.newInstance();
								orderBy.add("logtimeStamp ASC");
								exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
								exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
								exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
								
								try {
									//delegator.findList(entityName, entityCondition, fieldsToSelect, orderBy, findOptions, useCache)
									employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);
									if(employeeLogs.size()<=0){
										absentEmployees++;
									}else{
										presentEmployees++;
									}
								
								} catch (GenericEntityException e) {
									e.printStackTrace();
								}
							
							}
						}
					}
				}
			}
		
		attendanceNotifications.put("absentEmployees",String.valueOf(absentEmployees));
		attendanceNotifications.put("presentEmployees",String.valueOf(presentEmployees));
		attendanceNotifications.put("leaveEmployees",String.valueOf(leaveEmployees));

		return attendanceNotifications;
	}

}
