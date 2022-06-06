/**
 * 
 */
package org.ofbiz.humanres.attendance;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.apache.log4j.Logger;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.contessa.UtilAttendancePreference;
import org.ofbiz.webapp.contessa.UtilPreferenceProperties;
import org.ofbiz.webapp.contessa.Utils;
import org.ofbiz.entity.condition.EntityJoinOperator;

/**

 *
 */
public class AttendanceUtils {
	private static Logger logger=Logger.getLogger("AttendanceUtils");
	private static final String defaultGlAccountId1=UtilProperties.getPropertyValue("contessa.properties", "basicSalary.defaultGlAccountId1");
	private static final String defaultGlAccountId2=UtilProperties.getPropertyValue("contessa.properties", "basicSalary.defaultGlAccountId2"); 

	/**
	* This method is use to get monthYear and convert sql date.
	* @param month year String: February 2014
	* @return sql Date
	* @author  zzz
	* */
	public static Date getsqltDate(String s){
		java.sql.Date dte=null;
		try{
			String str = s;
			SimpleDateFormat formatter = new SimpleDateFormat("MMM yy");
			java.util.Date dt = formatter.parse(str);
			dte=new java.sql.Date(dt.getTime());
		}catch(Exception e){
			e.printStackTrace();
		}

		return dte;
		}
	
	public static boolean checkEmpLeave(List<GenericValue> empLeaves,long time){
		boolean flag=false;
		for(GenericValue leave:empLeaves){
			long fromtime=((Timestamp)leave.get("fromDate")).getTime();
			long thrutime=((Timestamp)leave.get("thruDate")).getTime();
			flag=(fromtime<=time&&thrutime>=time);
			if(flag){
				break;
			}
		}
		return flag;
	}
	
	public static String getLeaveReasonDesc(List<GenericValue> empLeaves,long time,Delegator delegator) throws GenericEntityException{
		String leaveType=null;
		for(GenericValue leave:empLeaves){
			long fromtime=((Timestamp)leave.get("fromDate")).getTime();
			long thrutime=((Timestamp)leave.get("thruDate")).getTime();
			boolean flag=(fromtime<=time&&thrutime>=time);
			if(flag){
				String emplLeaveReasonTypeId=(String) leave.get("emplLeaveReasonTypeId");	
				GenericValue leaveReasonType=delegator.findByPrimaryKey("EmplLeaveReasonType",UtilMisc.toMap("emplLeaveReasonTypeId", emplLeaveReasonTypeId));
				if(leaveReasonType!=null){
				leaveType=(String)leaveReasonType.get("description");
				}
				break;
			}
		}
		return leaveType;
	}
	public static int getEmployeeOt(Delegator delegator,String orgPartyId,String partyId,Date date,Timestamp exitTime) throws GenericEntityException{
		int otHour=0;
		logger.debug("Date-->"+new Timestamp(date.getTime()));
		String oTStartTime=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date, orgPartyId, UtilPreferenceProperties.OTSTARTTIME);
		String oTEndTime=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date, orgPartyId, UtilPreferenceProperties.OTENDTIME);
		String hasOTAllounce=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date, orgPartyId, UtilPreferenceProperties.OTALLOWANCES);
		if(hasOTAllounce.equals("Y")){
		long otTime=0;
		long oTStartTimeInDate=convertStringTimeToLong(oTStartTime,date);
		long oTEndTimeInDate=convertStringTimeToLong(oTEndTime, date);
		long exitTimeInLong=exitTime.getTime();
/*		if(exitTimeInLong>oTStartTimeInDate && oTStartTimeInDate<oTEndTimeInDate){
			otTime=exitTimeInLong-oTStartTimeInDate;
		}else if(exitTimeInLong>oTEndTimeInDate){
			otTime=oTStartTimeInDate-oTStartTimeInDate;
		}*/
		if(exitTimeInLong>oTEndTimeInDate){
			exitTimeInLong=oTEndTimeInDate;
			//otTime=exitTimeInLong-oTStartTimeInDate;
		}
		otTime=exitTimeInLong-oTStartTimeInDate;
		HashMap<String, Integer> otTimeInHashMap=UtilDateTime.covertLongToHourMinuteSecond(otTime);
			otHour=otTimeInHashMap.get("hour");
		int otMinute=otTimeInHashMap.get("minute");
		if(otMinute>50){
			otHour=otHour+1;
		}
		}
		logger.debug("otHour-->"+otHour);
		return otHour;
	}
	/**
	* This method is use to get year start and end in milliseconds.
	* @param year String
	* @return Map<String,Long> of given year start and end.
	* @author zzz
	* */	
	public static Map<String,Long> getStartAndEndOfYear(String year){
		Map<String,Long> startAndEndOfYear=new HashMap<String,Long>();
		
		
		Calendar startYear = Calendar.getInstance();
		startYear.set(Calendar.YEAR,Integer.parseInt(year));
		startYear.set(Calendar.MONTH, Calendar.JANUARY);
		startYear.set(Calendar.DAY_OF_MONTH, 1);
		startYear.set(Calendar.HOUR_OF_DAY, 0);
		startYear.set(Calendar.MINUTE, 0);
		startYear.set(Calendar.SECOND, 0);
		startYear.set(Calendar.MILLISECOND, 0);
		
		Calendar endYear = Calendar.getInstance();
		endYear.set(Calendar.YEAR,Integer.parseInt(year));
		endYear.set(Calendar.MONTH, Calendar.DECEMBER);
		endYear.set(Calendar.DAY_OF_MONTH, 31);
		endYear.set(Calendar.HOUR_OF_DAY, 23);
		endYear.set(Calendar.MINUTE, 59);
		endYear.set(Calendar.SECOND, 59);
		endYear.set(Calendar.MILLISECOND, 999);
		
		startAndEndOfYear.put("startyear", startYear.getTimeInMillis());
		startAndEndOfYear.put("endyear", endYear.getTimeInMillis());
	
		
		return startAndEndOfYear;
	}
	
	/**
	* This method is use to avoid null pointer exception
	* @param Object
	* @return String value
	* @author zzz
	* */
	public static String avoidNullPointerException(Object value){
			return  (value != null ? (String)value : "");
		}
	
	/**
	* Get Time String of given java.sql.Date in dd/MM/yyyy hh:mm a format
	* @param time java.sql.Date value
	* @return String value
	* @author zzz
	* */
	public static String FormatDate(Date time) {
	
			Date date = new Date(time.getTime());
			
			SimpleDateFormat oSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			
			String returnDate = oSimpleDateFormat.format(date);
			
			return returnDate;
		
		}
	/**
	* Get Time String of given Timestamp in dd/MM/yyyy hh:mm a format
	* @param date java.sql.Timestamp value
	* @return String value
	* @author zzz
	* */	
	public static String getTimeFormate(java.sql.Timestamp logtimeStamp){
		     
		     Date date = new Date(logtimeStamp.getTime());
			
			SimpleDateFormat oSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			
			String returnDate = oSimpleDateFormat.format(date);
			
			return returnDate;
		}

	/**
	 * Get day Start Time in long value
	 * @param date java.sql.Date value
	 * @return Long value
	 * @author zzz
	 * */
	public static long getStartTime(java.sql.Date date){
	
	
			Calendar calendarStart = Calendar.getInstance();
			calendarStart.setTime(date);
			calendarStart.set(Calendar.HOUR_OF_DAY, 0);
			calendarStart.set(Calendar.MINUTE, 0);
			calendarStart.set(Calendar.SECOND, 0);
			calendarStart.set(Calendar.MILLISECOND, 0);
			
			return calendarStart.getTime().getTime();
		}
	
	/**
	 * Get day End Time in long value
	 * @param date java.sql.Date value
	 * @return Long value
	 * @author zzz
	 * */
	public static long getEndTime(java.sql.Date date){
	
			Calendar calendarEnd = Calendar.getInstance();
			calendarEnd.setTime(date);
			calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
			calendarEnd.set(Calendar.MINUTE, 59);
			calendarEnd.set(Calendar.SECOND, 59);
			calendarEnd.set(Calendar.MILLISECOND, 999);
			
			return calendarEnd.getTime().getTime();
		}
	public static boolean compareLogtime(java.util.Date date,java.util.Date time){


		Calendar calendarStart = Calendar.getInstance();
		calendarStart.setTime(date);
		calendarStart.set(Calendar.HOUR_OF_DAY, 0);
		calendarStart.set(Calendar.MINUTE, 0);
		calendarStart.set(Calendar.SECOND, 0);
		calendarStart.set(Calendar.MILLISECOND, 0);


		Calendar calendarEnd = Calendar.getInstance();
		calendarEnd.setTime(date);
		calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
		calendarEnd.set(Calendar.MINUTE, 59);
		calendarEnd.set(Calendar.SECOND, 59);
		calendarEnd.set(Calendar.MILLISECOND, 999);


		return (time.before((calendarEnd.getTime())) && time.after((calendarStart.getTime())));
	}
	/**
	 * @author zzz
	 * @param delegator
	 * @param totalActiveEmployee
	 * @param partyId
	 * @return
	 */
	public static List<GenericValue> getUnitTotalActiveEmployee(Delegator delegator,List<GenericValue> totalActiveEmployee,String partyId){
		
		List<GenericValue> positions=getPositionsByParyId(delegator, partyId);

		if(positions!=null){
			for(GenericValue position:positions){
				totalActiveEmployee.addAll(getTotalActiveEmployeeByPositionId(delegator, position.getString("emplPositionId")));
			}
		}
		return totalActiveEmployee;
		
	}

	public static boolean compareLateLogtime(long lateEntryTimeInDate,long logtime){
		return logtime>=lateEntryTimeInDate;
	}
	public static boolean compareOTtime(long otStartTime,long otEndTime,long logtime){
		return (logtime<=otEndTime&&logtime>=otStartTime);
	}
	public static boolean compareMaxStayLogtime(java.util.Date extraOTEndTimeInDate,java.util.Date maximumStayTimeInDate,java.util.Date time){


		Calendar calendarStart = Calendar.getInstance();
		calendarStart.setTime(extraOTEndTimeInDate);


		Calendar calendarEnd = Calendar.getInstance();
		calendarEnd.setTime(maximumStayTimeInDate);


		return (time.before((calendarEnd.getTime())) && time.after((calendarStart.getTime())));
	}
	
	/** 
	 * @author AnearParvez
	 * */
	public static long convertStringTimeToLong(String timeTobeConverted,Date date){

		StringTokenizer str = new StringTokenizer(timeTobeConverted, ":");
		int hour = 0;
		int minutes = 0;
		String minute = "";
		int i = 0;

		while (str.hasMoreElements()) {
			if (i == 0) {
				hour = Integer.parseInt(str.nextElement().toString());
			} else {
				minute = str.nextElement().toString();
			}
			i++;
		}
		if (minute.endsWith("am")) {
			minutes = Integer.parseInt(minute.substring(0, 2));
			if (hour == 12)
				hour = 0;
		} else if (minute.endsWith("pm")) {
			minutes = Integer.parseInt(minute.substring(0, 2));
			if (12 > hour)
				hour = hour + 12;
		}
    
		

		Calendar calendarStart = Calendar.getInstance();
		calendarStart.setTime(date);
		calendarStart.set(Calendar.HOUR_OF_DAY, hour);

		calendarStart.set(Calendar.MINUTE, minutes);
		
		return calendarStart.getTime().getTime();
	}
	
	
	public static long getLateEntryTimeInDate(String lateEntryTime,Date date){

		StringTokenizer str = new StringTokenizer(lateEntryTime, ":");
		int hour = 0;
		int minutes = 0;
		String minute = "";
		int i = 0;

		while (str.hasMoreElements()) {
			if (i == 0) {
				hour = Integer.parseInt(str.nextElement().toString());
			} else {
				minute = str.nextElement().toString();
			}
			i++;
		}
		if (minute.endsWith("am")) {
			minutes = Integer.parseInt(minute.substring(0, 2));
			if (hour == 12)
				hour = 0;
		} else if (minute.endsWith("pm")) {
			minutes = Integer.parseInt(minute.substring(0, 2));
			if (12 > hour)
				hour = hour + 12;
		}
    
		

		Calendar calendarStart = Calendar.getInstance();
		calendarStart.setTime(date);
		calendarStart.set(Calendar.HOUR_OF_DAY, hour);

		calendarStart.set(Calendar.MINUTE, minutes);
		
		return calendarStart.getTime().getTime();
	}
	/**
	 * 
	 * @author AnearParvez
	 * */
	public static long getStrngTimeInDate(String lateEntryTime,Date date){

		StringTokenizer str = new StringTokenizer(lateEntryTime, ":");
		int hour = 0;
		int minutes = 0;
		String minute = "";
		int i = 0;

		while (str.hasMoreElements()) {
			if (i == 0) {
				hour = Integer.parseInt(str.nextElement().toString());
			} else {
				minute = str.nextElement().toString();
			}
			i++;
		}
		if (minute.endsWith("am")) {
			minutes = Integer.parseInt(minute.substring(0, 2));
			if (hour == 12)
				hour = 0;
		} else if (minute.endsWith("pm")) {
			minutes = Integer.parseInt(minute.substring(0, 2));
			if (12 > hour)
				hour = hour + 12;
		}
    
		

		Calendar calendarStart = Calendar.getInstance();
		calendarStart.setTime(date);
		calendarStart.set(Calendar.HOUR_OF_DAY, hour);

		calendarStart.set(Calendar.MINUTE, minutes);
		
		return calendarStart.getTime().getTime();
	}
	
	/**
	 * This method is used to get Tiffin Time in Long
	 * 
	 * @author zzz
	 * */
	public static long getStringTiffinTimeToLong(String tiffinTime,java.sql.Date date) {
		Calendar calendarDate = Calendar.getInstance();
		calendarDate.setTime(date);
		String timeTobeConverted = tiffinTime;
		StringTokenizer str = new StringTokenizer(timeTobeConverted, ":");
		int hour = 0;
		int minutes = 0;
		String minute = "";
		int i = 0;

		while (str.hasMoreElements()) {
			if (i == 0) {
				hour = Integer.parseInt(str.nextElement().toString());
			} else {
				minute = str.nextElement().toString();
			}
			i++;
		}
		if (minute.endsWith("am")) {
			minutes = Integer.parseInt(minute.substring(0, 2));
			if (hour == 12) {
				hour = 0;
				calendarDate.add(Calendar.DATE, 1);
			}
			if (hour <= 6) {
				calendarDate.add(Calendar.DATE, 1);
			}
		} else if (minute.endsWith("pm")) {
			minutes = Integer.parseInt(minute.substring(0, 2));
			if (12 > hour)
				hour = hour + 12;
		}

		calendarDate.set(Calendar.HOUR_OF_DAY, hour);
		calendarDate.set(Calendar.MINUTE, minutes);
		
		return calendarDate.getTimeInMillis();
	}
	
	/**
	 * This method is used to get Tiffin Time in TimeStamp
	 * 
	 * @author zzz And Updated By Rajib
	 * */
	public static Timestamp getStringTiffinTimeToTimStamp(String tiffinTime,java.sql.Date date) {
		Calendar calendarDate = Calendar.getInstance();
		calendarDate.setTime(date);
		String timeTobeConverted = tiffinTime;
		StringTokenizer str = new StringTokenizer(timeTobeConverted, ":");
		int hour = 0;
		int minutes = 0;
		String minute = "";
		int i = 0;

		while (str.hasMoreElements()) {
			if (i == 0) {
				hour = Integer.parseInt(str.nextElement().toString());
			} else {
				minute = str.nextElement().toString();
			}
			i++;
		}
		if (minute.endsWith("am")) {
			minutes = Integer.parseInt(minute.substring(0, 2));
			calendarDate.add(Calendar.DATE, 1);
/*			if (hour == 12) {
				hour = 0;
				calendarDate.add(Calendar.DATE, 1);
			}
			if (hour <= 6) {
				calendarDate.add(Calendar.DATE, 1);
			}*/
		} else if (minute.endsWith("pm")) {
			minutes = Integer.parseInt(minute.substring(0, 2));
			if (12 > hour)
				hour = hour + 12;
		}

		calendarDate.set(Calendar.HOUR_OF_DAY, hour);
		calendarDate.set(Calendar.MINUTE, minutes);
		
		return new Timestamp(calendarDate.getTimeInMillis());
	}
	
	public static Timestamp getStringTimeToTimStamp(String time,java.sql.Date date) {
		Calendar calendarDate = Calendar.getInstance();
		calendarDate.setTime(date);
		String timeTobeConverted = time;
		StringTokenizer str = new StringTokenizer(timeTobeConverted, ":");
		int hour = 0;
		int minutes = 0;
		String minute = "";
		int i = 0;

		while (str.hasMoreElements()) {
			if (i == 0) {
				hour = Integer.parseInt(str.nextElement().toString());
			} else {
				minute = str.nextElement().toString();
			}
			i++;
		}
		if (minute.endsWith("am")) {
			minutes = Integer.parseInt(minute.substring(0, 2));
		} else if (minute.endsWith("pm")) {
			minutes = Integer.parseInt(minute.substring(0, 2));
			if (12 > hour)
				hour = hour + 12;
		}

		calendarDate.set(Calendar.HOUR_OF_DAY, hour);
		calendarDate.set(Calendar.MINUTE, minutes);
		
		return new Timestamp(calendarDate.getTimeInMillis());
	}

	/**
	 * Add hour to Timestamp
	 * 
	 * @author zzz
	 * */
	public static Timestamp addHourToTimestamp(Timestamp time, int hour) {
		Calendar date = Calendar.getInstance();
		date.setTime(time);
		date.add(Calendar.HOUR_OF_DAY, hour);
		return new Timestamp(date.getTimeInMillis());
	}

	public static boolean checkHoliday(List<Timestamp> holidays,Timestamp date){
		
		return holidays.contains(date);
	}
	public static Object checkHolidayFromHolidayMap(List<Map<String,Object>> holidays,Timestamp date){
		for(Map<String,Object> holidayMap:holidays){
			Timestamp holidayDate=(Timestamp)holidayMap.get("date");
			if(holidayDate.equals(date)){
				return holidayMap.get("holidayType");
			}
		}
		return null;
	}
	
	public static boolean checkDepartmentExistance(List<String> departments,String department){
		
		return departments.contains(department);
	}
	
	public static List<Timestamp> getHolidays(String orgPartyId,Timestamp startDate,Timestamp endDate,Delegator delegator){
		
		List<Timestamp> holidays=new ArrayList<Timestamp>();	
		EntityCondition codition= EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
                EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
                        EntityCondition.makeCondition("estimatedStartDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate),
                        EntityCondition.makeCondition("estimatedStartDate", EntityOperator.LESS_THAN_EQUAL_TO, endDate),
                        EntityCondition.makeCondition("companyId", EntityOperator.EQUALS, orgPartyId)
                ), EntityJoinOperator.AND),
                EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
                        EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, "OFFICE_HOLIDAY"),
                        EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, "PUBLIC_HOLIDAY")
                ), EntityJoinOperator.OR)
        ), EntityJoinOperator.AND);

		List<GenericValue> holidayList  = new ArrayList<GenericValue>();
		try {
			//delegator.findList(entityName, entityCondition, fieldsToSelect, orderBy, findOptions, useCache)
			holidayList = delegator.findList("WorkEffort", codition, null, null, null, true);		
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		for(GenericValue value:holidayList){
			Timestamp holiday=	(Timestamp)value.get("estimatedStartDate");
			holidays.add(holiday);
		}
		return holidays;
		
	}
	
	public static List<Map<String,Object>> getHolidaysWithType(String orgPartyId,Timestamp startDate,Timestamp endDate,Delegator delegator){		
		List<Map<String,Object>> holidays=new ArrayList<Map<String,Object>>();	
		EntityCondition codition= EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
                EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
                        EntityCondition.makeCondition("estimatedStartDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate),
                        EntityCondition.makeCondition("estimatedStartDate", EntityOperator.LESS_THAN_EQUAL_TO, endDate),
                        EntityCondition.makeCondition("companyId", EntityOperator.EQUALS, orgPartyId)
                ), EntityJoinOperator.AND),
                EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
                        EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, "OFFICE_HOLIDAY"),
                        EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, "PUBLIC_HOLIDAY")
                ), EntityJoinOperator.OR)
        ), EntityJoinOperator.AND);

		List<GenericValue> holidayList  = new ArrayList<GenericValue>();
		try {
			//delegator.findList(entityName, entityCondition, fieldsToSelect, orderBy, findOptions, useCache)
			holidayList = delegator.findList("WorkEffort", codition, null, null, null, true);		
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		for(GenericValue value:holidayList){
			Map<String,Object> holidayMap=FastMap.newInstance();
			Timestamp holiday=	(Timestamp)value.get("estimatedStartDate");
			holidayMap.put("date", holiday);
			holidayMap.put("holidayType", value.get("workEffortTypeId"));
			holidays.add(holidayMap);
		}
		return holidays;
		
	}
	/*public static boolean checkDepartmentExistance(List<String> departments,String department){
		
		return departments.contains(department);
	}
	
	public static List<Timestamp> getHolidays(String orgPartyId,Timestamp startDate,Timestamp endDate,Delegator delegator){
		
		List<Timestamp> holidays=new ArrayList<Timestamp>();
		List<EntityExpr> exprs = FastList.newInstance();
		exprs.add(EntityCondition.makeCondition("estimatedStartDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate));
		exprs.add(EntityCondition.makeCondition("estimatedStartDate", EntityOperator.LESS_THAN_EQUAL_TO, endDate));
		exprs.add(EntityCondition.makeCondition("companyId", EntityOperator.EQUALS, orgPartyId));

		List<GenericValue> holidayList  = new ArrayList<GenericValue>();
		try {
			//delegator.findList(entityName, entityCondition, fieldsToSelect, orderBy, findOptions, useCache)
			holidayList = delegator.findList("WorkEffort", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, true);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		for(GenericValue value:holidayList){
			Timestamp holiday=	(Timestamp)value.get("estimatedStartDate");
			holidays.add(holiday);
		}
		return holidays;
		
	}*/
	
public static Date getMonthAndYearInDate(String monthYear){
		
		Map<String,Integer> monthlist=new HashMap<String,Integer>();
		monthlist.put("January", 0);
		monthlist.put("February", 1);
		monthlist.put("March", 2);
		monthlist.put("April", 3);
		monthlist.put("May", 4);
		monthlist.put("June", 5);
		monthlist.put("July", 6);
		monthlist.put("August", 7);
		monthlist.put("September", 8);
		monthlist.put("October", 9);
		monthlist.put("November", 10);
		monthlist.put("December", 11);
		

		StringTokenizer str = new StringTokenizer(monthYear, " ");
	
		int month=0;		
		int year = 0;
		int i = 0;
		while (str.hasMoreElements()) {
			if (i == 0) {
				month = Integer.parseInt(str.nextElement().toString());
			} else {
				year = Integer.parseInt(str.nextElement().toString());
			}
			i++;
		}

		
		Calendar calendarStart = Calendar.getInstance();
		calendarStart.set(year, month, 0);
		//calendarStart.setTime(date);
		calendarStart.set(Calendar.MINUTE,0);
		calendarStart.set(Calendar.SECOND, 0);
		calendarStart.set(Calendar.MILLISECOND, 0);
		
		return new Date(calendarStart.getTime().getTime());
	}
	


public static GenericValue getEmpORRosterPreference(Delegator delegator,String orgPartyId,String employeeId){
	
	GenericValue preference=null;
	
	try {
		List<GenericValue> employeeAttendancePreferences = delegator.findByAnd(
				"EmployeeAttendancePreference", UtilMisc.toMap("employeeId",employeeId.trim(),"orgPartyId",orgPartyId));
		GenericValue employeeAttendancePreference = EntityUtil
				.getFirst(employeeAttendancePreferences);
		if(employeeAttendancePreferences!=null){
			preference=employeeAttendancePreference;
			return preference;
		}else{
			
			List<GenericValue> rosterIdcheck = delegator.findByAnd(
					"RosterEmployee", UtilMisc.toMap("employeeId",employeeId));
			GenericValue rosterEmpId = EntityUtil
					.getFirst(rosterIdcheck);
			if (!UtilValidate.isEmpty(rosterEmpId) && rosterEmpId.get("rosterPreferenceId")!=null) {
				String rosterPreferenceId=(String)rosterEmpId.get("rosterPreferenceId");
				GenericValue rosterAttendancePreference = delegator.findByPrimaryKey(
						"AttendanceRosterPreference", UtilMisc.toMap("rosterPreferenceId",rosterPreferenceId.trim()));
				if(rosterAttendancePreference!=null){
					preference=rosterAttendancePreference;
					return preference;
				}
			
			}
		}
	
	} catch (GenericEntityException e1) {
		// TODO Auto-generated catch block
		return preference;
	}
	return preference;
}

public static GenericValue getOrgPreference(Delegator delegator,String orgPartyId){
	
	try {
		List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
				"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
		GenericValue orgAttendancePreference = EntityUtil
				.getFirst(orgAttendancePreferences);
		if (!UtilValidate.isEmpty(orgAttendancePreference)) {
			return orgAttendancePreference;
		}
	
	
	} catch (GenericEntityException e1) {
		// TODO Auto-generated catch block
		return null;
	}
	return null;
}

public static GenericValue getDayPreference(Delegator delegator,Date date,String orgPartyId){
	
		try {
			List<GenericValue> dayAttendancePreferences = delegator.findByAnd(
					"DayAttendancePreference", UtilMisc.toMap("dateId",date,"orgPartyId",orgPartyId));
			GenericValue dayAttendancePreference = EntityUtil
					.getFirst(dayAttendancePreferences);
		
			if (!UtilValidate.isEmpty(dayAttendancePreference)) {
				return dayAttendancePreference;
			}
		
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			return null;
		}
		return null;	
	}

public static String getLateEntryTime(String employeeId,Delegator delegator,Date date,String orgPartyId){
	String lateEntryTime="";
	try {
		List<GenericValue> dayAttendancePreferences = delegator.findByAnd(
				"DayAttendancePreference", UtilMisc.toMap("dateId",date));
		GenericValue dayAttendancePreference = EntityUtil
				.getFirst(dayAttendancePreferences);
		if (!UtilValidate.isEmpty(dayAttendancePreference) && dayAttendancePreference.get("lateEntryTime")!=null) {
			lateEntryTime=(String)dayAttendancePreference.get("lateEntryTime");
		}
		else{
			List<GenericValue> employeeAttendancePreferences = delegator.findByAnd(
					"EmployeeAttendancePreference", UtilMisc.toMap("employeeId",employeeId.trim()));
			GenericValue employeeAttendancePreference = EntityUtil
					.getFirst(employeeAttendancePreferences);
			if (!UtilValidate.isEmpty(employeeAttendancePreference) && employeeAttendancePreference.get("lateEntryTime")!=null) {
				lateEntryTime=(String)employeeAttendancePreference.get("lateEntryTime");
			}else{
				List<GenericValue> designationPreferences=null; 
				String emplPositionId= getPositionIdByPatyId(delegator,employeeId);
				designationPreferences=delegator.findByAnd("DesignationPreference",UtilMisc.toMap("emplPositionId", emplPositionId));
				
				List<GenericValue> rosterIdcheck = delegator.findByAnd(
						"RosterEmployee", UtilMisc.toMap("employeeId",employeeId));
				GenericValue rosterEmpId = EntityUtil.getFirst(rosterIdcheck);
				
				if(UtilValidate.isNotEmpty(designationPreferences) && UtilValidate.isNotEmpty(EntityUtil.getFirst(designationPreferences).get("lateEntryTime"))){
					 GenericValue designationPreference=EntityUtil.getFirst(designationPreferences);
					 lateEntryTime= designationPreference.getString("lateEntryTime");				
				 }else if (!UtilValidate.isEmpty(rosterEmpId) && rosterEmpId.get("rosterPreferenceId")!=null) {
					String rosterPreferenceId=(String)rosterEmpId.get("rosterPreferenceId");
					GenericValue rosterAttendancePreference = delegator.findByPrimaryKey(
							"AttendanceRosterPreference", UtilMisc.toMap("rosterPreferenceId",rosterPreferenceId.trim()));
					if(rosterAttendancePreference.get("lateEntryTime")!=null){
						lateEntryTime=(String)rosterAttendancePreference.get("lateEntryTime");
					}
					else{
						List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
								"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
						GenericValue orgAttendancePreference = EntityUtil
								.getFirst(orgAttendancePreferences);
						if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("lateEntryTime")!=null) {
							lateEntryTime=(String) orgAttendancePreference.get("lateEntryTime");
						}
						
					}
				}else{
					List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
							"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
					GenericValue orgAttendancePreference = EntityUtil
							.getFirst(orgAttendancePreferences);
					if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("lateEntryTime")!=null) {
						lateEntryTime=(String) orgAttendancePreference.get("lateEntryTime");
					}
				}
			}
		}
	} catch (GenericEntityException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
	return lateEntryTime;
}


public static String getOtStartTime(String partyId,Delegator delegator,Date date,String orgPartyId){
	String exitTime="";
	try {
		List<GenericValue> dayAttendancePreferences = delegator.findByAnd(
				"DayAttendancePreference", UtilMisc.toMap("dateId",date));
		GenericValue dayAttendancePreference = EntityUtil
				.getFirst(dayAttendancePreferences);
		if (!UtilValidate.isEmpty(dayAttendancePreference) && dayAttendancePreference.get("oTStartTime")!=null) {
			exitTime=(String)dayAttendancePreference.get("oTStartTime");
		}
		else{
			List<GenericValue> employeeAttendancePreferences = delegator.findByAnd(
					"EmployeeAttendancePreference", UtilMisc.toMap("employeeId",partyId.trim()));
			GenericValue employeeAttendancePreference = EntityUtil
					.getFirst(employeeAttendancePreferences);
			if (!UtilValidate.isEmpty(employeeAttendancePreference) && employeeAttendancePreference.get("oTStartTime")!=null) {
				exitTime=(String)employeeAttendancePreference.get("oTStartTime");
			}else{
				List<GenericValue> rosterIdcheck = delegator.findByAnd(
						"RosterEmployee", UtilMisc.toMap("employeeId",partyId));
				GenericValue rosterEmpId = EntityUtil
						.getFirst(rosterIdcheck);
				if (!UtilValidate.isEmpty(rosterEmpId) && rosterEmpId.get("oTStartTime")!=null) {
					String rosterPreferenceId=(String)rosterEmpId.get("oTStartTime");
					GenericValue rosterAttendancePreference = delegator.findByPrimaryKey(
							"AttendanceRosterPreference", UtilMisc.toMap("rosterPreferenceId",rosterPreferenceId.trim()));
					if(rosterAttendancePreference.get("oTStartTime")!=null){
						exitTime=(String)rosterAttendancePreference.get("oTStartTime");
					}
					else{
						List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
								"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
						GenericValue orgAttendancePreference = EntityUtil
								.getFirst(orgAttendancePreferences);
						if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("oTStartTime")!=null) {
							exitTime=(String) orgAttendancePreference.get("oTStartTime");
						}
						
					}
				}else{
					List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
							"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
					GenericValue orgAttendancePreference = EntityUtil
							.getFirst(orgAttendancePreferences);
					if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("oTStartTime")!=null) {
						exitTime=(String) orgAttendancePreference.get("oTStartTime");
					}
				}
			}
		}
	} catch (GenericEntityException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
	return exitTime;
}

public static String getOtEndTime(String partyId,Delegator delegator,Date date,String orgPartyId){
	String exitTime="";
	try {
		List<GenericValue> dayAttendancePreferences = delegator.findByAnd(
				"DayAttendancePreference", UtilMisc.toMap("dateId",date));
		GenericValue dayAttendancePreference = EntityUtil
				.getFirst(dayAttendancePreferences);
		if (!UtilValidate.isEmpty(dayAttendancePreference) && dayAttendancePreference.get("oTEndTime")!=null) {
			exitTime=(String)dayAttendancePreference.get("oTEndTime");
		}
		else{
			List<GenericValue> employeeAttendancePreferences = delegator.findByAnd(
					"EmployeeAttendancePreference", UtilMisc.toMap("employeeId",partyId.trim()));
			GenericValue employeeAttendancePreference = EntityUtil
					.getFirst(employeeAttendancePreferences);
			if (!UtilValidate.isEmpty(employeeAttendancePreference) && employeeAttendancePreference.get("oTEndTime")!=null) {
				exitTime=(String)employeeAttendancePreference.get("oTEndTime");
			}else{
				List<GenericValue> rosterIdcheck = delegator.findByAnd(
						"RosterEmployee", UtilMisc.toMap("employeeId",partyId));
				GenericValue rosterEmpId = EntityUtil
						.getFirst(rosterIdcheck);
				if (!UtilValidate.isEmpty(rosterEmpId) && rosterEmpId.get("rosterPreferenceId")!=null) {
					String rosterPreferenceId=(String)rosterEmpId.get("rosterPreferenceId");
					GenericValue rosterAttendancePreference = delegator.findByPrimaryKey(
							"AttendanceRosterPreference", UtilMisc.toMap("rosterPreferenceId",rosterPreferenceId.trim()));
					if(rosterAttendancePreference.get("oTEndTime")!=null){
						exitTime=(String)rosterAttendancePreference.get("oTEndTime");
					}
					else{
						List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
								"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
						GenericValue orgAttendancePreference = EntityUtil
								.getFirst(orgAttendancePreferences);
						if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("oTEndTime")!=null) {
							exitTime=(String) orgAttendancePreference.get("oTEndTime");
						}
						
					}
				}else{
					List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
							"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
					GenericValue orgAttendancePreference = EntityUtil
							.getFirst(orgAttendancePreferences);
					if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("oTEndTime")!=null) {
						exitTime=(String) orgAttendancePreference.get("oTEndTime");
					}
				}
			}
		}
	} catch (GenericEntityException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
	return exitTime;
}
/**
 * @author zzz
 * @param partyId
 * @param delegator
 * @param date
 * @param orgPartyId
 * @return
 */
public static String getExitTime(String partyId,Delegator delegator,Date date,String orgPartyId){
	String exitTime="";
	try {
		List<GenericValue> dayAttendancePreferences = delegator.findByAnd(
				"DayAttendancePreference", UtilMisc.toMap("dateId",date));
		GenericValue dayAttendancePreference = EntityUtil
				.getFirst(dayAttendancePreferences);
		if (!UtilValidate.isEmpty(dayAttendancePreference) && dayAttendancePreference.get("exitTime")!=null) {
			exitTime=(String)dayAttendancePreference.get("exitTime");
		}else{
			List<GenericValue> employeeAttendancePreferences = delegator.findByAnd(
					"EmployeeAttendancePreference", UtilMisc.toMap("employeeId",partyId.trim()));
			GenericValue employeeAttendancePreference = EntityUtil
					.getFirst(employeeAttendancePreferences);
			if (!UtilValidate.isEmpty(employeeAttendancePreference) && employeeAttendancePreference.get("exitTime")!=null) {
				exitTime=(String)employeeAttendancePreference.get("exitTime");
			}else{
				List<GenericValue> rosterIdcheck = delegator.findByAnd(
						"RosterEmployee", UtilMisc.toMap("employeeId",partyId));
				GenericValue rosterEmpId = EntityUtil
						.getFirst(rosterIdcheck);
				if (!UtilValidate.isEmpty(rosterEmpId) && rosterEmpId.get("rosterPreferenceId")!=null) {
					String rosterPreferenceId=(String)rosterEmpId.get("rosterPreferenceId");
					GenericValue rosterAttendancePreference = delegator.findByPrimaryKey(
							"AttendanceRosterPreference", UtilMisc.toMap("rosterPreferenceId",rosterPreferenceId.trim()));
					if(rosterAttendancePreference.get("exitTime")!=null){
						exitTime=(String)rosterAttendancePreference.get("exitTime");
					}
					else{
						List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
								"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
						GenericValue orgAttendancePreference = EntityUtil
								.getFirst(orgAttendancePreferences);
						if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("exitTime")!=null) {
							exitTime=(String) orgAttendancePreference.get("exitTime");
						}
						
					}
				}else{
					List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
							"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
					GenericValue orgAttendancePreference = EntityUtil
							.getFirst(orgAttendancePreferences);
					if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("exitTime")!=null) {
						exitTime=(String) orgAttendancePreference.get("exitTime");
					}
				}
			}
		}
	} catch (GenericEntityException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
	return exitTime;
}





public static String getEntryTime(String partyId,Delegator delegator,Date date,String orgPartyId){
	String entryTime="";
	try {
		List<GenericValue> dayAttendancePreferences = delegator.findByAnd(
				"DayAttendancePreference", UtilMisc.toMap("dateId",date));
		GenericValue dayAttendancePreference = EntityUtil
				.getFirst(dayAttendancePreferences);
		if (!UtilValidate.isEmpty(dayAttendancePreference) && dayAttendancePreference.get("entryTime")!=null) {
			entryTime=(String)dayAttendancePreference.get("entryTime");
		}
		else{
			List<GenericValue> employeeAttendancePreferences = delegator.findByAnd(
					"EmployeeAttendancePreference", UtilMisc.toMap("employeeId",partyId.trim()));
			GenericValue employeeAttendancePreference = EntityUtil
					.getFirst(employeeAttendancePreferences);
			if (!UtilValidate.isEmpty(employeeAttendancePreference) && employeeAttendancePreference.get("entryTime")!=null) {
				entryTime=(String)employeeAttendancePreference.get("entryTime");
			}else{
				List<GenericValue> rosterIdcheck = delegator.findByAnd(
						"RosterEmployee", UtilMisc.toMap("employeeId",partyId));
				GenericValue rosterEmpId = EntityUtil
						.getFirst(rosterIdcheck);
				if (!UtilValidate.isEmpty(rosterEmpId) && rosterEmpId.get("rosterPreferenceId")!=null) {
					String rosterPreferenceId=(String)rosterEmpId.get("rosterPreferenceId");
					GenericValue rosterAttendancePreference = delegator.findByPrimaryKey(
							"AttendanceRosterPreference", UtilMisc.toMap("rosterPreferenceId",rosterPreferenceId.trim()));
					if(rosterAttendancePreference.get("entryTime")!=null){
						entryTime=(String)rosterAttendancePreference.get("entryTime");
					}
					else{
						List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
								"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
						GenericValue orgAttendancePreference = EntityUtil
								.getFirst(orgAttendancePreferences);
						if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("entryTime")!=null) {
							entryTime=(String) orgAttendancePreference.get("entryTime");
						}
						
					}
				}else{
					List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
							"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
					GenericValue orgAttendancePreference = EntityUtil
							.getFirst(orgAttendancePreferences);
					if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("entryTime")!=null) {
						entryTime=(String) orgAttendancePreference.get("entryTime");
					}
				}
			}
		}
	} catch (GenericEntityException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
	return entryTime;
}

public static String getlateEntryDaysForAttenBonusDisable(String employeeId,Delegator delegator,String orgPartyId){
	String lateEntryDaysForAttenBonusDisable="";
	try {
		
			List<GenericValue> employeeAttendancePreferences = delegator.findByAnd(
					"EmployeeAttendancePreference", UtilMisc.toMap("employeeId",employeeId.trim()));
			GenericValue employeeAttendancePreference = EntityUtil
					.getFirst(employeeAttendancePreferences);
			if (!UtilValidate.isEmpty(employeeAttendancePreference) && employeeAttendancePreference.get("lateEntryDaysForAttenBonusDisable")!=null) {
				lateEntryDaysForAttenBonusDisable=(String)employeeAttendancePreference.get("lateEntryDaysForAttenBonusDisable");
			}else{
				List<GenericValue> designationPreferences=null; 
				String emplPositionId= getPositionIdByPatyId(delegator,employeeId);
				designationPreferences=delegator.findByAnd("DesignationPreference",UtilMisc.toMap("emplPositionId", emplPositionId));
				
				List<GenericValue> rosterIdcheck = delegator.findByAnd(
						"RosterEmployee", UtilMisc.toMap("employeeId",employeeId));
				GenericValue rosterEmpId = EntityUtil.getFirst(rosterIdcheck);
				if(UtilValidate.isNotEmpty(designationPreferences) && UtilValidate.isNotEmpty(EntityUtil.getFirst(designationPreferences).get("lateEntryDaysForAttenBonusDisable"))){
					 GenericValue designationPreference=EntityUtil.getFirst(designationPreferences);					
					 lateEntryDaysForAttenBonusDisable= designationPreference.getString("lateEntryDaysForAttenBonusDisable");
						
				 }	else if (!UtilValidate.isEmpty(rosterEmpId) && rosterEmpId.get("rosterPreferenceId")!=null) {
					String rosterPreferenceId=(String)rosterEmpId.get("rosterPreferenceId");
					GenericValue rosterAttendancePreference = delegator.findByPrimaryKey(
							"AttendanceRosterPreference", UtilMisc.toMap("rosterPreferenceId",rosterPreferenceId.trim()));
					if(rosterAttendancePreference.get("lateEntryDaysForAttenBonusDisable")!=null){
						lateEntryDaysForAttenBonusDisable=(String)rosterAttendancePreference.get("lateEntryDaysForAttenBonusDisable");
					}
					else{
						List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
								"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
						GenericValue orgAttendancePreference = EntityUtil
								.getFirst(orgAttendancePreferences);
						if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("lateEntryDaysForAttenBonusDisable")!=null) {
							lateEntryDaysForAttenBonusDisable=(String) orgAttendancePreference.get("lateEntryDaysForAttenBonusDisable");
						}
						
					}
				}else{
					List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
							"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
					GenericValue orgAttendancePreference = EntityUtil
							.getFirst(orgAttendancePreferences);
					if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("lateEntryDaysForAttenBonusDisable")!=null) {
						lateEntryDaysForAttenBonusDisable=(String) orgAttendancePreference.get("lateEntryDaysForAttenBonusDisable");
					}
				}
			}
		
	} catch (GenericEntityException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
	return lateEntryDaysForAttenBonusDisable;
}

public static String getlateEntryDaysForAttenBonusDeduction(String employeeId,Delegator delegator,String orgPartyId){
	String lateEntryDaysForAttenBonusDeduction="";
	try {
		
			List<GenericValue> employeeAttendancePreferences = delegator.findByAnd(
					"EmployeeAttendancePreference", UtilMisc.toMap("employeeId",employeeId.trim()));
			GenericValue employeeAttendancePreference = EntityUtil
					.getFirst(employeeAttendancePreferences);
			if (!UtilValidate.isEmpty(employeeAttendancePreference) && employeeAttendancePreference.get("lateEntryDaysForAttenBonusDeduction")!=null) {
				lateEntryDaysForAttenBonusDeduction=(String)employeeAttendancePreference.get("lateEntryDaysForAttenBonusDeduction");
			}else{
				List<GenericValue> designationPreferences=null; 
				String emplPositionId= getPositionIdByPatyId(delegator,employeeId);
				designationPreferences=delegator.findByAnd("DesignationPreference",UtilMisc.toMap("emplPositionId", emplPositionId));
				List<GenericValue> rosterIdcheck = delegator.findByAnd(
						"RosterEmployee", UtilMisc.toMap("employeeId",employeeId));
				GenericValue rosterEmpId = EntityUtil.getFirst(rosterIdcheck);
				if(UtilValidate.isNotEmpty(designationPreferences) && UtilValidate.isNotEmpty(EntityUtil.getFirst(designationPreferences).get("lateEntryDaysForAttenBonusDeduction"))){
					 GenericValue designationPreference=EntityUtil.getFirst(designationPreferences);					
					 lateEntryDaysForAttenBonusDeduction= designationPreference.getString("lateEntryDaysForAttenBonusDeduction");						
				 }
				else if (!UtilValidate.isEmpty(rosterEmpId) && rosterEmpId.get("rosterPreferenceId")!=null) {
					String rosterPreferenceId=(String)rosterEmpId.get("rosterPreferenceId");
					GenericValue rosterAttendancePreference = delegator.findByPrimaryKey(
							"AttendanceRosterPreference", UtilMisc.toMap("rosterPreferenceId",rosterPreferenceId.trim()));
					if(rosterAttendancePreference.get("lateEntryDaysForAttenBonusDeduction")!=null){
						lateEntryDaysForAttenBonusDeduction=(String)rosterAttendancePreference.get("lateEntryDaysForAttenBonusDeduction");
					}
					else{
						List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
								"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
						GenericValue orgAttendancePreference = EntityUtil
								.getFirst(orgAttendancePreferences);
						if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("lateEntryDaysForAttenBonusDeduction")!=null) {
							lateEntryDaysForAttenBonusDeduction=(String) orgAttendancePreference.get("lateEntryDaysForAttenBonusDeduction");
						}
						
					}
				}else{
					List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
							"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
					GenericValue orgAttendancePreference = EntityUtil
							.getFirst(orgAttendancePreferences);
					if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("lateEntryDaysForAttenBonusDeduction")!=null) {
						lateEntryDaysForAttenBonusDeduction=(String) orgAttendancePreference.get("lateEntryDaysForAttenBonusDeduction");
					}
				}
			}
	
	} catch (GenericEntityException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
	return lateEntryDaysForAttenBonusDeduction;
}

//test



public static String getyearlyLateEntryDaysForAttenBonusDisable(String employeeId,Delegator delegator,String orgPartyId){
	String yearlyLateEntryDaysForAttenBonusDisable="";
	try {
		
			List<GenericValue> employeeAttendancePreferences = delegator.findByAnd(
					"EmployeeAttendancePreference", UtilMisc.toMap("employeeId",employeeId.trim()));
			GenericValue employeeAttendancePreference = EntityUtil
					.getFirst(employeeAttendancePreferences);
			if (!UtilValidate.isEmpty(employeeAttendancePreference) && employeeAttendancePreference.get("yearlyLateEntryDaysForAttenBonusDisable")!=null) {
				yearlyLateEntryDaysForAttenBonusDisable=(String)employeeAttendancePreference.get("yearlyLateEntryDaysForAttenBonusDisable");
			}else{
				List<GenericValue> rosterIdcheck = delegator.findByAnd(
						"RosterEmployee", UtilMisc.toMap("employeeId",employeeId));
				GenericValue rosterEmpId = EntityUtil
						.getFirst(rosterIdcheck);
				if (!UtilValidate.isEmpty(rosterEmpId) && rosterEmpId.get("rosterPreferenceId")!=null) {
					String rosterPreferenceId=(String)rosterEmpId.get("rosterPreferenceId");
					GenericValue rosterAttendancePreference = delegator.findByPrimaryKey(
							"AttendanceRosterPreference", UtilMisc.toMap("rosterPreferenceId",rosterPreferenceId.trim()));
					if(rosterAttendancePreference.get("yearlyLateEntryDaysForAttenBonusDisable")!=null){
						yearlyLateEntryDaysForAttenBonusDisable=(String)rosterAttendancePreference.get("yearlyLateEntryDaysForAttenBonusDisable");
					}
					else{
						List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
								"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
						GenericValue orgAttendancePreference = EntityUtil
								.getFirst(orgAttendancePreferences);
						if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("yearlyLateEntryDaysForAttenBonusDisable")!=null) {
							yearlyLateEntryDaysForAttenBonusDisable=(String) orgAttendancePreference.get("yearlyLateEntryDaysForAttenBonusDisable");
						}
						
					}
				}else{
					List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
							"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
					GenericValue orgAttendancePreference = EntityUtil
							.getFirst(orgAttendancePreferences);
					if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("yearlyLateEntryDaysForAttenBonusDisable")!=null) {
						yearlyLateEntryDaysForAttenBonusDisable=(String) orgAttendancePreference.get("yearlyLateEntryDaysForAttenBonusDisable");
					}
				}
			}
		
	} catch (GenericEntityException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
	return yearlyLateEntryDaysForAttenBonusDisable;
}

public static String getyearlylateEntryDaysForAttenBonusDeduction(String employeeId,Delegator delegator,String orgPartyId){
	String yearlylateEntryDaysForAttenBonusDeduction="";
	try {
		
			List<GenericValue> employeeAttendancePreferences = delegator.findByAnd(
					"EmployeeAttendancePreference", UtilMisc.toMap("employeeId",employeeId.trim()));
			GenericValue employeeAttendancePreference = EntityUtil
					.getFirst(employeeAttendancePreferences);
			if (!UtilValidate.isEmpty(employeeAttendancePreference) && employeeAttendancePreference.get("yearlylateEntryDaysForAttenBonusDeduction")!=null) {
				yearlylateEntryDaysForAttenBonusDeduction=(String)employeeAttendancePreference.get("yearlylateEntryDaysForAttenBonusDeduction");
			}else{
				List<GenericValue> rosterIdcheck = delegator.findByAnd(
						"RosterEmployee", UtilMisc.toMap("employeeId",employeeId));
				GenericValue rosterEmpId = EntityUtil
						.getFirst(rosterIdcheck);
				if (!UtilValidate.isEmpty(rosterEmpId) && rosterEmpId.get("rosterPreferenceId")!=null) {
					String rosterPreferenceId=(String)rosterEmpId.get("rosterPreferenceId");
					GenericValue rosterAttendancePreference = delegator.findByPrimaryKey(
							"AttendanceRosterPreference", UtilMisc.toMap("rosterPreferenceId",rosterPreferenceId.trim()));
					if(rosterAttendancePreference.get("yearlylateEntryDaysForAttenBonusDeduction")!=null){
						yearlylateEntryDaysForAttenBonusDeduction=(String)rosterAttendancePreference.get("yearlylateEntryDaysForAttenBonusDeduction");
					}
					else{
						List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
								"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
						GenericValue orgAttendancePreference = EntityUtil
								.getFirst(orgAttendancePreferences);
						if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("yearlylateEntryDaysForAttenBonusDeduction")!=null) {
							yearlylateEntryDaysForAttenBonusDeduction=(String) orgAttendancePreference.get("yearlylateEntryDaysForAttenBonusDeduction");
						}
						
					}
				}else{
					List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
							"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
					GenericValue orgAttendancePreference = EntityUtil
							.getFirst(orgAttendancePreferences);
					if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("yearlylateEntryDaysForAttenBonusDeduction")!=null) {
						yearlylateEntryDaysForAttenBonusDeduction=(String) orgAttendancePreference.get("yearlylateEntryDaysForAttenBonusDeduction");
					}
				}
			}
	
	} catch (GenericEntityException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
	return yearlylateEntryDaysForAttenBonusDeduction;
}



//test


public static String getmontlyBonusAmount(String employeeId,Delegator delegator,String orgPartyId){
	String montlyBonusAmount="";
	try {
	
			List<GenericValue> employeeAttendancePreferences = delegator.findByAnd(
					"EmployeeAttendancePreference", UtilMisc.toMap("employeeId",employeeId.trim()));
			GenericValue employeeAttendancePreference = EntityUtil
					.getFirst(employeeAttendancePreferences);
			if (!UtilValidate.isEmpty(employeeAttendancePreference) && employeeAttendancePreference.get("montlyBonusAmount")!=null) {
				montlyBonusAmount=(String)employeeAttendancePreference.get("montlyBonusAmount");
			}else{
				List<GenericValue> designationPreferences=null; 
				String emplPositionId= getPositionIdByPatyId(delegator,employeeId);
				designationPreferences=delegator.findByAnd("DesignationPreference",UtilMisc.toMap("emplPositionId", emplPositionId));
				List<GenericValue> rosterIdcheck = delegator.findByAnd(
						"RosterEmployee", UtilMisc.toMap("employeeId",employeeId));
				GenericValue rosterEmpId = EntityUtil
						.getFirst(rosterIdcheck);
				

                 if(UtilValidate.isNotEmpty(designationPreferences) && UtilValidate.isNotEmpty(EntityUtil.getFirst(designationPreferences).get("montlyBonusAmount")) ){
					 GenericValue designationPreference=EntityUtil.getFirst(designationPreferences);
					 montlyBonusAmount= designationPreference.getString("montlyBonusAmount");					
				  }	else if (!UtilValidate.isEmpty(rosterEmpId) && rosterEmpId.get("rosterPreferenceId")!=null) {
					String rosterPreferenceId=(String)rosterEmpId.get("rosterPreferenceId");
					GenericValue rosterAttendancePreference = delegator.findByPrimaryKey(
							"AttendanceRosterPreference", UtilMisc.toMap("rosterPreferenceId",rosterPreferenceId.trim()));
					if(rosterAttendancePreference.get("montlyBonusAmount")!=null){
						montlyBonusAmount=(String)rosterAttendancePreference.get("montlyBonusAmount");
					}
					else{
						List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
								"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
						GenericValue orgAttendancePreference = EntityUtil
								.getFirst(orgAttendancePreferences);
						if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("montlyBonusAmount")!=null) {
							montlyBonusAmount=(String) orgAttendancePreference.get("montlyBonusAmount");
						}
						
					}
				}else{
					List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
							"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
					GenericValue orgAttendancePreference = EntityUtil
							.getFirst(orgAttendancePreferences);
					if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("montlyBonusAmount")!=null) {
						montlyBonusAmount=(String) orgAttendancePreference.get("montlyBonusAmount");
					}
				}
			}
	} catch (GenericEntityException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
	return montlyBonusAmount;
}


public static String getyearlyBonusAmount(String employeeId,Delegator delegator,String orgPartyId){
	String yearlyBonusAmount="";
	try {
	
			List<GenericValue> employeeAttendancePreferences = delegator.findByAnd(
					"EmployeeAttendancePreference", UtilMisc.toMap("employeeId",employeeId.trim()));
			GenericValue employeeAttendancePreference = EntityUtil
					.getFirst(employeeAttendancePreferences);
			if (!UtilValidate.isEmpty(employeeAttendancePreference) && employeeAttendancePreference.get("yearlyBonusAmount")!=null) {
				yearlyBonusAmount=(String)employeeAttendancePreference.get("yearlyBonusAmount");
			}else{
				List<GenericValue> rosterIdcheck = delegator.findByAnd(
						"RosterEmployee", UtilMisc.toMap("employeeId",employeeId));
				GenericValue rosterEmpId = EntityUtil
						.getFirst(rosterIdcheck);
				if (!UtilValidate.isEmpty(rosterEmpId) && rosterEmpId.get("rosterPreferenceId")!=null) {
					String rosterPreferenceId=(String)rosterEmpId.get("rosterPreferenceId");
					GenericValue rosterAttendancePreference = delegator.findByPrimaryKey(
							"AttendanceRosterPreference", UtilMisc.toMap("rosterPreferenceId",rosterPreferenceId.trim()));
					if(rosterAttendancePreference.get("yearlyBonusAmount")!=null){
						yearlyBonusAmount=(String)rosterAttendancePreference.get("yearlyBonusAmount");
					}
					else{
						List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
								"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
						GenericValue orgAttendancePreference = EntityUtil
								.getFirst(orgAttendancePreferences);
						if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("yearlyBonusAmount")!=null) {
							yearlyBonusAmount=(String) orgAttendancePreference.get("yearlyBonusAmount");
						}
						
					}
				}else{
					List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
							"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
					GenericValue orgAttendancePreference = EntityUtil
							.getFirst(orgAttendancePreferences);
					if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("yearlyBonusAmount")!=null) {
						yearlyBonusAmount=(String) orgAttendancePreference.get("yearlyBonusAmount");
					}
				}
			}
	} catch (GenericEntityException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
	return yearlyBonusAmount;
}


public static String getbonusDeductionAmount(String employeeId,Delegator delegator,String orgPartyId){
	String bonusDeductionAmount="";
	try {
		
			List<GenericValue> employeeAttendancePreferences = delegator.findByAnd(
					"EmployeeAttendancePreference", UtilMisc.toMap("employeeId",employeeId.trim()));
			GenericValue employeeAttendancePreference = EntityUtil
					.getFirst(employeeAttendancePreferences);
			if (!UtilValidate.isEmpty(employeeAttendancePreference) && employeeAttendancePreference.get("bonusDeductionAmount")!=null) {
				bonusDeductionAmount=(String)employeeAttendancePreference.get("bonusDeductionAmount");
			}else{
				List<GenericValue> designationPreferences=null; 
				String emplPositionId= getPositionIdByPatyId(delegator,employeeId);
				designationPreferences=delegator.findByAnd("DesignationPreference",UtilMisc.toMap("emplPositionId", emplPositionId));
				List<GenericValue> rosterIdcheck = delegator.findByAnd(
						"RosterEmployee", UtilMisc.toMap("employeeId",employeeId));
				GenericValue rosterEmpId = EntityUtil
						.getFirst(rosterIdcheck);
				
				if(UtilValidate.isNotEmpty(designationPreferences) && UtilValidate.isNotEmpty(EntityUtil.getFirst(designationPreferences).get("bonusDeductionAmount"))){
					 GenericValue designationPreference=EntityUtil.getFirst(designationPreferences);					 
					 bonusDeductionAmount= designationPreference.getString("bonusDeductionAmount");						 			 
				 }else	if (!UtilValidate.isEmpty(rosterEmpId) && rosterEmpId.get("rosterPreferenceId")!=null) {
					String rosterPreferenceId=(String)rosterEmpId.get("rosterPreferenceId");
					GenericValue rosterAttendancePreference = delegator.findByPrimaryKey(
							"AttendanceRosterPreference", UtilMisc.toMap("rosterPreferenceId",rosterPreferenceId.trim()));
					if(rosterAttendancePreference.get("bonusDeductionAmount")!=null){
						bonusDeductionAmount=(String)rosterAttendancePreference.get("bonusDeductionAmount");
					}
					else{
						List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
								"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
						GenericValue orgAttendancePreference = EntityUtil
								.getFirst(orgAttendancePreferences);
						if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("bonusDeductionAmount")!=null) {
							bonusDeductionAmount=(String) orgAttendancePreference.get("bonusDeductionAmount");
						}
						
					}
				}else{
					List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
							"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
					GenericValue orgAttendancePreference = EntityUtil
							.getFirst(orgAttendancePreferences);
					if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("bonusDeductionAmount")!=null) {
						bonusDeductionAmount=(String) orgAttendancePreference.get("bonusDeductionAmount");
					}
				}
			}
	} catch (GenericEntityException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
	return bonusDeductionAmount;
}




//check the attendance bonus allowance for specified employee

public static String getAttendanceBonusAllowances(String employeeId,Delegator delegator,String orgPartyId){
	String attendanceBonusAllowances="";
	try {
		
			List<GenericValue> employeeAttendancePreferences = delegator.findByAnd(
					"EmployeeAttendancePreference", UtilMisc.toMap("employeeId",employeeId.trim()));
			GenericValue employeeAttendancePreference = EntityUtil
					.getFirst(employeeAttendancePreferences);
			if (!UtilValidate.isEmpty(employeeAttendancePreference) && employeeAttendancePreference.get("attendanceBonusAllowances")!=null) {
				attendanceBonusAllowances=(String)employeeAttendancePreference.get("attendanceBonusAllowances");
			}else{
				List<GenericValue> designationPreferences=null; 
				String emplPositionId= getPositionIdByPatyId(delegator,employeeId);
				designationPreferences=delegator.findByAnd("DesignationPreference",UtilMisc.toMap("emplPositionId", emplPositionId));
				List<GenericValue> rosterIdcheck = delegator.findByAnd(
						"RosterEmployee", UtilMisc.toMap("employeeId",employeeId));
				GenericValue rosterEmpId = EntityUtil.getFirst(rosterIdcheck);
				
				if(UtilValidate.isNotEmpty(designationPreferences) && UtilValidate.isNotEmpty(EntityUtil.getFirst(designationPreferences).get("attendanceBonusAllowances"))){
					 GenericValue designationPreference=EntityUtil.getFirst(designationPreferences);
					 attendanceBonusAllowances= designationPreference.getString("attendanceBonusAllowances");					
				 }else if (!UtilValidate.isEmpty(rosterEmpId) && rosterEmpId.get("rosterPreferenceId")!=null) {
					String rosterPreferenceId=(String)rosterEmpId.get("rosterPreferenceId");
					GenericValue rosterAttendancePreference = delegator.findByPrimaryKey(
							"AttendanceRosterPreference", UtilMisc.toMap("rosterPreferenceId",rosterPreferenceId.trim()));
					if(rosterAttendancePreference.get("attendanceBonusAllowances")!=null){
						attendanceBonusAllowances=(String)rosterAttendancePreference.get("attendanceBonusAllowances");
					}
					else{
						List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
								"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
						GenericValue orgAttendancePreference = EntityUtil
								.getFirst(orgAttendancePreferences);
						if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("attendanceBonusAllowances")!=null) {
							attendanceBonusAllowances=(String) orgAttendancePreference.get("attendanceBonusAllowances");
						}
						
					}
				}else{
					List<GenericValue> orgAttendancePreferences = delegator.findByAnd(
							"OrgAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
					GenericValue orgAttendancePreference = EntityUtil
							.getFirst(orgAttendancePreferences);
					if (!UtilValidate.isEmpty(orgAttendancePreference) && orgAttendancePreference.get("attendanceBonusAllowances")!=null) {
						attendanceBonusAllowances=(String) orgAttendancePreference.get("attendanceBonusAllowances");
					}
				}
			}
	} catch (GenericEntityException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
	return attendanceBonusAllowances;
}


/**
 * @author zzz
 * This method is used for calculating bonus from 1 month Attendance log
 * @return
 */

public static List<String> getEmployeeBonus(List<Long>monthlogs, int lateEntry, String employeeId,String orgPartyId,Delegator delegator,Map<String, Object> result) throws ParseException{

	  String bonus = "0";
	  int lateDates=0;
	  int intimeDates=0;
	  List<String> resultList = new ArrayList<String>();
	  
	  String attendanceBonusAllowances=getAttendanceBonusAllowances(employeeId,delegator,orgPartyId);
	  String lateEntryDaysForAttenBonusDisable=getlateEntryDaysForAttenBonusDisable(employeeId,delegator,orgPartyId);
	  String montlyBonusAmount=getmontlyBonusAmount(employeeId,delegator,orgPartyId);
  	  String bonusDeductionAmount=getbonusDeductionAmount(employeeId,delegator,orgPartyId);
	  String lateEntryDaysForAttenBonusDeduction=getlateEntryDaysForAttenBonusDeduction(employeeId,delegator,orgPartyId);
	  
	  for(long oneDay : monthlogs){
      
      	Date logTimeInDate = new Date(oneDay);  	
      	
    	Calendar logcalendar = Calendar.getInstance();
    	logcalendar.setTime(logTimeInDate);
    	logcalendar.set(Calendar.SECOND, 0);
    	logcalendar.set(Calendar.MILLISECOND, 0);
      	
    	String LateEntryTime = getLateEntryTime(employeeId,delegator,logTimeInDate,orgPartyId);
    		
    	long  lateEntryTimeInDate= getLateEntryTimeInDate(LateEntryTime, logTimeInDate);
    	
    	//Calendar settingcalendar = Calendar.getInstance();
    	//settingcalendar.setTime(lateEntryTimeInDate);
    	//settingcalendar.set(Calendar.SECOND, 0);
    	//settingcalendar.set(Calendar.MILLISECOND, 0);
    	
    	 if(logcalendar.getTimeInMillis()>lateEntryTimeInDate){
    		lateEntry++;
    		lateDates++;
    		
    	 }else{
    		 intimeDates++; 
    	 }
      	
      }
	  
	  
	  if(attendanceBonusAllowances.equals("Y")){
		  
		  if(lateEntryDaysForAttenBonusDisable.isEmpty()||lateEntryDaysForAttenBonusDisable.equals("")){
			  lateEntryDaysForAttenBonusDisable="32";
		  	  }
		  if(lateEntryDaysForAttenBonusDeduction.isEmpty()||lateEntryDaysForAttenBonusDeduction.equals("")){
		  		lateEntryDaysForAttenBonusDeduction="32";
		  	  }
	  	 if(montlyBonusAmount.isEmpty()||montlyBonusAmount.equals("")){
	  		montlyBonusAmount="0";
		  	  }
	  	if(bonusDeductionAmount.isEmpty()||bonusDeductionAmount.equals("")){
	  		bonusDeductionAmount="0";
	  	  }
		try{
	  		int daysForBonusDisable  = Integer.parseInt(lateEntryDaysForAttenBonusDisable);
	  	  	int daysForBonusDeduction = Integer.parseInt(lateEntryDaysForAttenBonusDeduction);
	  	  	if(daysForBonusDeduction>daysForBonusDisable){
	  	  	daysForBonusDeduction=daysForBonusDisable;
	  	  	}
	  	  	int bonusAmount = Integer.parseInt(montlyBonusAmount);
	  	  	int deductionAmount = Integer.parseInt(bonusDeductionAmount);
	  	  	  		  
	  		  if(lateEntry<daysForBonusDeduction){
	  	    	  bonus=montlyBonusAmount; 
	  	      }
	  	      else if(lateEntry<daysForBonusDisable){
	  	    	  bonus=Integer.toString(bonusAmount-deductionAmount);
	  	      }else{
	  	    	  bonus="0";  
	  	      }
	  	}catch(Exception e){

			result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
			result.put(ModelService.ERROR_MESSAGE, "At least One Preference Settings needed");
	  	}
		  		   
	  }else{
		  bonus="0";  
	  }
	  	
	resultList.add(bonus);
	resultList.add(Integer.toString(lateDates));
	resultList.add(Integer.toString(intimeDates));
	  
	return resultList;
}

/**
 * @author zzz
 * This method is used for calculating bonus from 1 month Attendance log (for single employee)
 * @return bonus
 */

public static String getMonthlyBonus(Date date,List<Long>monthlogs, int lateEntry, String partyId,String orgPartyId,Delegator delegator) throws ParseException{

	  String bonus = "0";
	  int lateDates=0;
	  int intimeDates=0;
	  String LateEntryTime="";
	  String attendanceBonusAllowances="";
	  String lateEntryDaysForAttenBonusDisable="";
	  String lateEntryDaysForAttenBonusDeduction="";
	  String montlyBonusAmount="";
	  String bonusDeductionAmount="";
		try {
		    attendanceBonusAllowances=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date, orgPartyId, UtilPreferenceProperties.ATTENDANCEBONUSALLOWANCES);
		    lateEntryDaysForAttenBonusDisable=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date, orgPartyId, UtilPreferenceProperties.LATEENTRYDAYSFORATTENBONUSDISABLE);
			lateEntryDaysForAttenBonusDeduction=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date, orgPartyId, UtilPreferenceProperties.LATEENTRYDAYSFORATTENBONUSDEDUCTION);
		    montlyBonusAmount=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date, orgPartyId, UtilPreferenceProperties.MONTLYBONUSAMOUNT);
		    bonusDeductionAmount=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date, orgPartyId, UtilPreferenceProperties.BONUSDEDUCTIONAMOUNT);

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  for(long oneDay : monthlogs){
      
      	Date logTimeInDate = new Date(oneDay); 
		try {
			LateEntryTime = UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, logTimeInDate, orgPartyId, UtilPreferenceProperties.LATEENTRYTIME);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      	
    	Calendar logcalendar = Calendar.getInstance();
    	logcalendar.setTime(logTimeInDate);
    	logcalendar.set(Calendar.SECOND, 0);
    	logcalendar.set(Calendar.MILLISECOND, 0);
	
	
    	long  lateEntryTimeInDate= getLateEntryTimeInDate(LateEntryTime, logTimeInDate);
    	
    	 if(logcalendar.getTimeInMillis()>lateEntryTimeInDate){
    		lateEntry++;
    		lateDates++;
    		
    	 }else{
    		 intimeDates++; 
    	 }
      	
      }
	  	  
	  if(attendanceBonusAllowances.equals("Y")){
		  
		  if(lateEntryDaysForAttenBonusDisable.isEmpty()||lateEntryDaysForAttenBonusDisable.equals("")){
			  lateEntryDaysForAttenBonusDisable="32";
		  	  }
		  if(lateEntryDaysForAttenBonusDeduction.isEmpty()||lateEntryDaysForAttenBonusDeduction.equals("")){
		  		lateEntryDaysForAttenBonusDeduction="32";
		  	  }
	  	 if(montlyBonusAmount.isEmpty()||montlyBonusAmount.equals("")){
	  		montlyBonusAmount="0";
		  	  }
	  	if(bonusDeductionAmount.isEmpty()||bonusDeductionAmount.equals("")){
	  		bonusDeductionAmount="0";
	  	  }
		try{
	  		int daysForBonusDisable  = Integer.parseInt(lateEntryDaysForAttenBonusDisable);
	  	  	int daysForBonusDeduction = Integer.parseInt(lateEntryDaysForAttenBonusDeduction);
	  	  	if(daysForBonusDeduction>daysForBonusDisable){
	  	  	daysForBonusDeduction=daysForBonusDisable;
	  	  	}
	  	  	int bonusAmount = Integer.parseInt(montlyBonusAmount);
	  	  	int deductionAmount = Integer.parseInt(bonusDeductionAmount);
	  	  	  		  
	  		  if(lateEntry<daysForBonusDeduction){
	  	    	  bonus=montlyBonusAmount; 
	  	      }
	  	      else if(lateEntry<daysForBonusDisable){
	  	    	  bonus=Integer.toString(bonusAmount-deductionAmount);
	  	      }else{
	  	    	  bonus="0";  
	  	      }
	  	}catch(Exception e){

	  	}
		  		   
	  }else{
		  bonus="0";  
	  }
	  
	return bonus;
}
/**
 * @author zzz
 * @param delegator
 * @param locationId
 * @return
 */
public static String getDeviceFirstTimeStatus(Delegator delegator, String locationId){
	List<GenericValue> deviceRegistrations=null;
	String action="";
	try {
		deviceRegistrations = delegator.findByAnd("DeviceRegistration",UtilMisc.toMap("locationId", locationId));
		GenericValue	deviceRegistration=	EntityUtil.getFirst(deviceRegistrations);
		if (!UtilValidate.isEmpty(deviceRegistration)){
			action= String.valueOf(deviceRegistration.get("firsTimeRegister"));
		}
	} catch (GenericEntityException e) {
		e.printStackTrace();
	}
return action;
}
/**
 * @author zzz
 * @param delegator
 * @param userLoginId
 * @return
 */
public static String getPartyIDFromUserLoginID(Delegator delegator,String userLoginId){
	List<GenericValue> userLogins=FastList.newInstance();
	try {
		userLogins = delegator.findByAnd("UserLogin",UtilMisc.toMap("userLoginId", userLoginId));
		GenericValue	userLogin=	EntityUtil.getFirst(userLogins);
		if (!UtilValidate.isEmpty(userLogin)){
			return String.valueOf(userLogin.get("partyId"));
		}
	} catch (GenericEntityException e) {
		return null;
	}
	return null;
}
/**
 * @author zzz
 * @param delegator
 * @param partyId
 * @return
 */
public static String getUserLoginIDFromPartyID(Delegator delegator,String partyId){
	List<GenericValue> userLogins=FastList.newInstance();
	try {
		userLogins = delegator.findByAnd("UserLogin",UtilMisc.toMap("partyId", partyId));
		GenericValue	userLogin=	EntityUtil.getFirst(userLogins);
		if (!UtilValidate.isEmpty(userLogin)){
			return String.valueOf(userLogin.get("userLoginId"));
		}
	} catch (GenericEntityException e) {
		return null;
	}
	return null;
}
/***
 * @author zzz
 * @param delegator
 * @param partyId
 * @return
 */
public static List<Map<String,String>> getPositions(Delegator delegator, String partyId){
	List<Map<String,String>> positionids=FastList.newInstance();
	List<GenericValue> positions=FastList.newInstance();
	try {
	
		positions= delegator.findList("EmplPositionAndEmplPositionType", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),null,null,null,false);
	if(positions!=null&&positions.size()>0){
		for(GenericValue position:positions){
			String positionid=position.getString("emplPositionId");
			int total=	getTotalEmployeeByPosition(delegator,positionid);
			String description=position.getString("description");
			Map<String,String> positionMap=FastMap.newInstance();
			positionMap.put("description", description+"("+String.valueOf(total)+")");
			positionMap.put("totalEmployee", String.valueOf(total));
			positionids.add(positionMap);
		}
		
	}else{
		return positionids;
	}
	}catch (GenericEntityException e) {
		return positionids;
	}
	return positionids;
}
/**
 * @author zzz
 * @param delegator
 * @param positionId
 * @return
 */
public static int getTotalEmployeeByPosition(Delegator delegator, String positionId){
	
	try {
		List<GenericValue> emplPositionFulfillmentList = delegator.findList("EmplPositionFulfillment", EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS, positionId), null, null, null, false);
		return emplPositionFulfillmentList.size();

	} catch (GenericEntityException e) {
		return 0;
	}
}
/**
 * @author zzz
 * @param stamp
 * @return
 */
public static Timestamp getModifiedDayStart(Timestamp stamp) {
	Calendar calendarStart = Calendar.getInstance();
	calendarStart.setTime(stamp);
	calendarStart.set(Calendar.HOUR_OF_DAY, 0);
	calendarStart.set(Calendar.MINUTE, 0);
	calendarStart.set(Calendar.SECOND, 0);
	calendarStart.set(Calendar.MILLISECOND, 0);
    Timestamp retStamp = new Timestamp(calendarStart.getTime().getTime());
    retStamp.setNanos(0);
    return retStamp;
}
/**
 * @author zzz
 * @param stamp
 * @return
 */
public static Timestamp getModifiedDayEnd(Timestamp stamp) {
	Calendar calendarEnd = Calendar.getInstance();
	calendarEnd.setTime(stamp);
	calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
	calendarEnd.set(Calendar.MINUTE, 59);
	calendarEnd.set(Calendar.SECOND, 59);
	calendarEnd.set(Calendar.MILLISECOND, 999);    
    Timestamp retStamp = new Timestamp(calendarEnd.getTime().getTime());
    retStamp.setNanos(0);
    return retStamp;
}

/**
 * @author zzz
 * @param delegator
 * @param totalPosition
 * @param partyId
 * @return
 */
public static List<GenericValue> getTotalPositions(Delegator delegator,List<GenericValue> totalPosition,String partyId){
	List<GenericValue> children=getChildren(delegator, partyId);
	
	List<GenericValue> positions=getPositionsForPayGradeByParyId(delegator, partyId);
	
	for(GenericValue child:children){
		totalPosition=getTotalPositions(delegator,totalPosition,child.getString("partyIdTo"));
	}
	
	if(positions!=null){
		totalPosition.addAll(positions);
	}
	return totalPosition;
	
}


/**
 * @author zzz
 * @param delegator
 * @param totalEmployee
 * @param partyId
 * @return
 */
public static List<GenericValue> getTotalEmployee(Delegator delegator,List<GenericValue> totalEmployee,String partyId){
	List<GenericValue> children=getChildren(delegator, partyId);
	
	List<GenericValue> positions=getPositionsByParyId(delegator, partyId);
	
	for(GenericValue child:children){

		totalEmployee=getTotalEmployee(delegator,totalEmployee,child.getString("partyIdTo"));
	}
	
	if(positions!=null){
		for(GenericValue position:positions){
			totalEmployee.addAll(getTotalEmployeeByPositionId(delegator, position.getString("emplPositionId")));
		}
	}
	return totalEmployee;
	
}


/**
 * @author zzz
 * @param delegator
 * @param totalActiveEmployee
 * @param partyId
 * @return
 */
public static List<GenericValue> getTotalActiveEmployee(Delegator delegator,List<GenericValue> totalActiveEmployee,String partyId){
	List<GenericValue> children=getChildren(delegator, partyId);
	
	List<GenericValue> positions=getPositionsByParyId(delegator, partyId);
	
	for(GenericValue child:children){

		totalActiveEmployee=getTotalActiveEmployee(delegator,totalActiveEmployee,child.getString("partyIdTo"));
	}
	
	if(positions!=null){
		for(GenericValue position:positions){
			totalActiveEmployee.addAll(getTotalActiveEmployeeByPositionId(delegator, position.getString("emplPositionId")));
		}
	}
	return totalActiveEmployee;
	
}

/**
 * @author zzz zzz
 * Get Total Active Employee By PositionId
 * */
public static 	List<GenericValue> getTotalActiveEmployeeByPositionId(Delegator delegator, String positionId,String employmentType){
	List<GenericValue> emplPositionFulfillmentList=FastList.newInstance();

	try {
		List<EntityExpr> exprs = FastList.newInstance();
        exprs.add(EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS, positionId));
        exprs.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "EMPLOYEE"));
        exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "EMPL_POS_ACTIVE"));
        exprs.add(EntityCondition.makeCondition("partyTypeId", EntityOperator.EQUALS, "PERSON"));
        
		 emplPositionFulfillmentList = delegator.findList("PartyAndPersonAndFulfillment", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
		
		 for(GenericValue emplPositionFulfillment:emplPositionFulfillmentList){
			 String partyId=emplPositionFulfillment.getString("partyId");
			 if(UtilValidate.isNotEmpty(partyId)){
				 GenericValue employment=EntityUtil.getFirst(delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo", partyId)));
				 if(UtilValidate.isNotEmpty(employment)){
						String	employmentTypeCheck= employment.getString("employmentType");
						
						if(employmentTypeCheck!=null){
							
							if(employmentType.equalsIgnoreCase("Manager")){
								String	isManager= employment.getString("isManager");
								if(isManager==null||!isManager.equalsIgnoreCase("Y")){
									emplPositionFulfillmentList.remove(emplPositionFulfillment);	
								}
							}else if(employmentType.equalsIgnoreCase("FinAccount")){
								GenericValue finAccount=EntityUtil.getFirst(delegator.findByAnd("FinAccount", UtilMisc.toMap("ownerPartyId", partyId)));
								if(UtilValidate.isEmpty(finAccount)){
									emplPositionFulfillmentList.remove(emplPositionFulfillment);	
								}
							}
							else if(!employmentTypeCheck.equalsIgnoreCase(employmentType)){
								emplPositionFulfillmentList.remove(emplPositionFulfillment);	
							}
							
						}else{
							emplPositionFulfillmentList.remove(emplPositionFulfillment);	
						}
							
				 }
			 }
		 }
		 
		 return emplPositionFulfillmentList;

	} catch (GenericEntityException e) {
		return emplPositionFulfillmentList;
	}
}


/**
 * @author zzz zzz
 * @param delegator
 * @param totalActiveEmployee
 * @param partyId
 * @return
 */
public static List<GenericValue> getTotalActiveEmployee(Delegator delegator,List<GenericValue> totalActiveEmployee,String partyId,String employmentType){
	List<GenericValue> children=getChildren(delegator, partyId);
	
	List<GenericValue> positions=getPositionsByParyId(delegator, partyId);
	
	for(GenericValue child:children){

		totalActiveEmployee=getTotalActiveEmployee(delegator,totalActiveEmployee,child.getString("partyIdTo"),employmentType);
	}
	
	if(positions!=null){
		for(GenericValue position:positions){
			totalActiveEmployee.addAll(getTotalActiveEmployeeByPositionId(delegator, position.getString("emplPositionId"),employmentType));
		}
	}
	return totalActiveEmployee;
	
}

/**
 * @author zzz
 * @param delegator
 * @param totalEmployee
 * @param partyId
 * @return
 */
public static List<Map<String,String>> getEmployeesWithBasicSalary(Delegator delegator, List<Map<String,String>> totalEmployee,String partyIdTo){
	List<GenericValue> children=getChildren(delegator, partyIdTo);
	
	List<GenericValue> positions=getPositionsByParty(delegator, partyIdTo);
	
	for(GenericValue child:children){

		totalEmployee=getEmployeesWithBasicSalary(delegator,totalEmployee,child.getString("partyIdTo"));
	}
	
	if(positions!=null){
		for(GenericValue position:positions){
			List<GenericValue> emplPositionFulfillments=getEmployeesByPosition(delegator, position.getString("emplPositionId"));
			for(GenericValue emplPositionFulfillment:emplPositionFulfillments){
				String partyId=emplPositionFulfillment.getString("partyId");
				Map<String,String> employeeBasicSalarryInfo=FastMap.newInstance();
				employeeBasicSalarryInfo.put("partyId", partyId);
				String basicSalary=getBasicSalaryByPartyId(delegator,partyId);
				employeeBasicSalarryInfo.put("basicSalary", basicSalary);
				totalEmployee.add(employeeBasicSalarryInfo);
			}
		}
	}
	return totalEmployee;
	
}
/**
 * @author zzz
 * @param delegator
 * @param partyId
 * @return
 */
public static 	List<GenericValue> getChildren(Delegator delegator, String partyId){
	
	List<GenericValue> children=FastList.newInstance();
	
	Set<String> fieldsToSelect = FastSet.newInstance();
	fieldsToSelect.add("partyIdTo");
	   	
	try {
			List<EntityExpr> exprs = FastList.newInstance();
	        exprs.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
	        exprs.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP"));
	        children = delegator.findList("PartyRelationship", EntityCondition.makeCondition(exprs, EntityOperator.AND), fieldsToSelect, null, null, false);
	   
	
		}catch (GenericEntityException e) {
			return children;
		}
	
	return children;
}

/**
 * @author zzz
 * Get Unit Level Positions By Unit PartyId
 * */
public static 	List<GenericValue> getPositionsByParty(Delegator delegator, String partyId){
	
	List<GenericValue> positions=FastList.newInstance();
	
	Set<String> fieldsToSelect = FastSet.newInstance();
	fieldsToSelect.add("emplPositionId");
	
	try {
		
		List<EntityExpr> exprs = FastList.newInstance();
        exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "EMPL_POS_ACTIVE"));
        positions = delegator.findList("EmplPositionAndEmplPositionType", EntityCondition.makeCondition(exprs, EntityOperator.AND), fieldsToSelect, null, null, false);
   
	}catch (GenericEntityException e) {
		return positions;
	}
	return positions;
}


/**
 * @author zzz
 * Get Unit Level Positions By Unit PartyId
 * */
public static 	List<GenericValue> getPositionsForPayGradeByParyId(Delegator delegator, String partyId){
	
	List<GenericValue> positions=FastList.newInstance();
	
	try {
		positions= delegator.findByAnd("EmplPosition", UtilMisc.toMap("partyId", partyId, "statusId", "EMPL_POS_ACTIVE"));
	}catch (GenericEntityException e) {
		return positions;
	}
	return positions;
}


/**
 * @author zzz
 * Get Unit Level Positions By Unit PartyId
 * */
public static 	List<GenericValue> getPositionsByParyId(Delegator delegator, String partyId){
	
	List<GenericValue> positions=FastList.newInstance();
	
	try {
		positions= delegator.findByAnd("EmplPositionAndEmplPositionType", UtilMisc.toMap("partyId", partyId, "statusId", "EMPL_POS_ACTIVE"));
	}catch (GenericEntityException e) {
		return positions;
	}
	return positions;
}

/**
 * @author zzz
 * Get Total Employee By PositionId
 * */
public static 	List<GenericValue> getEmployeesByPosition(Delegator delegator, String positionId){
	List<GenericValue> emplPositionFulfillmentList=FastList.newInstance();
	
	Set<String> fieldsToSelect = FastSet.newInstance();
	fieldsToSelect.add("partyId");

	try {
		
		 emplPositionFulfillmentList = delegator.findList("EmplPositionFulfillment", EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS, positionId), fieldsToSelect, null, null, false);

		return emplPositionFulfillmentList;

	} catch (GenericEntityException e) {
		return null;
	}
}


/**
 * @author zzz
 * Get Total Employee By PositionId
 * */
public static 	List<GenericValue> getTotalEmployeeByPositionId(Delegator delegator, String positionId){
	List<GenericValue> emplPositionFulfillmentList=FastList.newInstance();
	Set<String> fieldsToSelect = FastSet.newInstance();
	fieldsToSelect.add("partyId");
	
	try {
		  emplPositionFulfillmentList = delegator.findList("EmplPositionFulfillment", EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS, positionId), fieldsToSelect, null, null, false);

		 return emplPositionFulfillmentList;

	} catch (GenericEntityException e) {
		return null;
	}
}


/**
 * @author zzz
 * Get Total Active Employee By PositionId
 * */
public static 	List<GenericValue> getTotalActiveEmployeeByPositionId(Delegator delegator, String positionId){
	List<GenericValue> emplPositionFulfillmentList=FastList.newInstance();

	try {
		List<EntityExpr> exprs = FastList.newInstance();
        exprs.add(EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS, positionId));
        exprs.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "EMPLOYEE"));
        exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "EMPL_POS_ACTIVE"));
        exprs.add(EntityCondition.makeCondition("partyTypeId", EntityOperator.EQUALS, "PERSON"));
        
		 emplPositionFulfillmentList = delegator.findList("PartyAndPersonAndFulfillment", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
		
		 return emplPositionFulfillmentList;

	} catch (GenericEntityException e) {
		return emplPositionFulfillmentList;
	}
}

/**
 * @author zzz
 * Get Total InActive Employee By PositionId
 * */
public static 	List<GenericValue> getTotalInActiveEmployeeByPositionId(Delegator delegator, String positionId){
	List<GenericValue> emplPositionFulfillmentList=FastList.newInstance();

	try {
		List<EntityExpr> exprs = FastList.newInstance();
        exprs.add(EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS, positionId));
        exprs.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "EMPLOYEE"));
        exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "EMPL_POS_INACTIVE"));
        exprs.add(EntityCondition.makeCondition("partyTypeId", EntityOperator.EQUALS, "PERSON"));
        
		 emplPositionFulfillmentList = delegator.findList("PartyAndPersonAndFulfillment", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
		
		 return emplPositionFulfillmentList;

	} catch (GenericEntityException e) {
		return null;
	}
}


public static String getDepartmentName(Delegator delegator,String partyId){
	String departmentName="";
	try {
		GenericValue party = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", partyId));

		if(!UtilValidate.isEmpty(party)){
		departmentName=party.getString("groupName");
		
	}
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return departmentName;
}


public static String getDepartmentNameInBangla(Delegator delegator,String partyId){
	String departmentName="";
	try {
		GenericValue party = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", partyId));

		if(!UtilValidate.isEmpty(party)){
		departmentName=party.getString("groupNameLocal");
		
	}
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return departmentName;
}


public static boolean checkEmployeeStatus(Delegator delegator,String partyId){
	
	try {
		List<GenericValue> partyList = delegator.findByAnd("PartyAndPersonAndUserLoginAndEmploymentAndPartyRole", UtilMisc.toMap("partyId", partyId,"statusId","EMPL_POS_ACTIVE","roleTypeId","EMPLOYEE"));
		GenericValue party=EntityUtil.getFirst(partyList);
		if(!UtilValidate.isEmpty(party)){
			return true;
			}
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return false;
	
}
/**
 * This Method Filter Employee List by Card and Active Status
 * @author zzz
 * */
public static List<GenericValue> filterEmployeeWithCardAndStatus(Delegator delegator,List<GenericValue> partyList){
	for(GenericValue party:partyList){
		String partyId=party.getString("partyId");
		if(!AttendanceUtils.checkCardExistanceByPartyId(delegator, partyId)||AttendanceUtils.checkEmployeeActive(delegator, partyId)){
			partyList.remove(party);
		}
	}
	return partyList;
}
/**
 * This Method Filter Employee List by Active Status
 * @author zzz
 * */
public static List<GenericValue> filterInactiveEmployee(Delegator delegator,List<GenericValue> partyList){
	for(GenericValue party:partyList){
		String partyId=party.getString("partyId");
		if(!checkEmployeeStatus(delegator,partyId)){
			partyList.remove(party);	
		}
	}
	return partyList;
}
/**
 * This Method Filter Employee List by Active Status and Date
 * @author zzz
 * */
public static List<GenericValue> filterActiveEmployeesByDate(Delegator delegator,List<GenericValue> partyList,Timestamp startDate,Timestamp endDate){
	for(GenericValue party:partyList){
		String partyId=party.getString("partyId");
		if(!checkEmployeeStatus(delegator,partyId)&&checkResignedDate(delegator,partyId,startDate,endDate)){
			partyList.remove(party);	
		}
	}
	return partyList;
}
/**
 * 
 * */
public static boolean checkResignedDate(Delegator delegator,String partyId,Timestamp startDate,Timestamp endDate){

	GenericValue party=null;
	try {
		party = EntityUtil.getFirst(delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo", partyId)));
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	if(party!=null){
		Timestamp thruDate=party.getTimestamp("thruDate");
	
		if(thruDate!=null){
			return startDate.getTime()<=thruDate.getTime()&&thruDate.getTime()<=endDate.getTime();
		}
	}
	return false;
}
/**
 * 
 * */
public static List<GenericValue> filterInActiveEmployeesByDate(Delegator delegator,List<GenericValue> partyList,Timestamp startDate,Timestamp endDate){
	List<GenericValue> inactiveEmployeeList=FastList.newInstance();
	for(GenericValue party:partyList){
		String partyId=party.getString("partyId");
		if(!checkEmployeeStatus(delegator,partyId)&&checkResignedDate(delegator,partyId,startDate,endDate)){
			inactiveEmployeeList.add(party);
	
			
		}
	}
	return inactiveEmployeeList;
}

/**
 * 
 * @param delegator
 * @param partyId
 * @param startDate
 * @param endDate
 * @return
 */
public static boolean checkEmployeeStatusByDate(Delegator delegator,String partyId,Timestamp startDate,Timestamp endDate){
	
		try {
			GenericValue employment = EntityUtil.getFirst(delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo", partyId)));
			
			if(employment!=null&&UtilValidate.isNotEmpty(employment)){
			
				Timestamp thruDate=employment.getTimestamp("thruDate");
				if(thruDate!=null){
				
					if( startDate.getTime()<=thruDate.getTime()&&thruDate.getTime()<=endDate.getTime()){
						return true;	
					}
				}
			}
			
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	return false;
}

/**
 * 
 * @param delegator
 * @param partyList
 * @param startDate
 * @param endDate
 * @return
 */
public static int getInactiveEmployeeByDate(Delegator delegator,List<GenericValue> partyList,Timestamp startDate,Timestamp endDate){
	int count=0;
	for(GenericValue party:partyList){
		String partyId=party.getString("partyId");	
			GenericValue employment=null;
		try {
			employment = EntityUtil.getFirst(delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo", partyId)));
			
			if(employment!=null){
			
				Timestamp thruDate=employment.getTimestamp("thruDate");
				if(thruDate!=null){
				
					if( startDate.getTime()<=thruDate.getTime()&&thruDate.getTime()<=endDate.getTime()){
						count++;	
					}
				}
			}
			
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	return count;
}


/*
 * Get Employee Designation By Employee partyId
 * */
public static String getPartyDesignation(Delegator delegator,String partyId){
	
	if(partyId!=null&&!partyId.isEmpty()){
	
	 try {
		 	
			List<GenericValue>	emplPositionFulfillments = delegator.findByAnd("EmplPositionFulfillment",UtilMisc.toMap("partyId", partyId));
			GenericValue emplPositionFulfillment=EntityUtil.getFirst(emplPositionFulfillments);
			if (!UtilValidate.isEmpty(emplPositionFulfillment)){
				
				String emplPositionId=emplPositionFulfillment.getString("emplPositionId");
				
				if(emplPositionId!=null&&!emplPositionId.isEmpty()){
					
					List<GenericValue>	emplPositionAndEmplPositionTypes = delegator.findByAnd("EmplPositionAndEmplPositionType",UtilMisc.toMap("emplPositionId", emplPositionId));
					GenericValue emplPositionAndEmplPositionType=EntityUtil.getFirst(emplPositionAndEmplPositionTypes);
					
					if (!UtilValidate.isEmpty(emplPositionAndEmplPositionType)){
						String description=emplPositionAndEmplPositionType.getString("description");
						if(description!=null&&!description.isEmpty()){
							return description;
						}
						
					}
				
				}	
				
			}
		} catch (GenericEntityException e) {
			return "";
		}
	 }
		
		return "";
	}

/**
 * Get Employee Position By Employee partyId
 * @author zzz
 * */
public static String getPositionIdByPatyId(Delegator delegator,String partyId){
	
	 try {
			List<GenericValue>	emplPositionFulfillments = delegator.findByAnd("EmplPositionFulfillment",UtilMisc.toMap("partyId", partyId));
			GenericValue emplPositionFulfillment=EntityUtil.getFirst(emplPositionFulfillments);
			if (!UtilValidate.isEmpty(emplPositionFulfillment)){
				if(emplPositionFulfillment.get("emplPositionId")!=null){
					return emplPositionFulfillment.getString("emplPositionId");
				}
				
			}
		} catch (GenericEntityException e) {
			return null;
		}
		
		return null;
	}

/**
 * Get Employee PayGrade By Employee partyId
 * @author zzz
 * */
public static String getEmplPayGradeByPositionId(Delegator delegator,String emplPositionId){
	String payGradeId="";
	
	 try {
		 
			List<GenericValue>	emplPositionAndEmplPositionTypes = delegator.findByAnd("EmplPositionAndEmplPositionType",UtilMisc.toMap("emplPositionId", emplPositionId));
			GenericValue emplPositionAndEmplPositionType=EntityUtil.getFirst(emplPositionAndEmplPositionTypes);
			
			if (!UtilValidate.isEmpty(emplPositionAndEmplPositionType)){
				 payGradeId=emplPositionAndEmplPositionType.getString("payGradeId");
				
			}
	
		} catch (GenericEntityException e) {
			return payGradeId;
		}
		
		return payGradeId;
	}


/*
 * Get Employee Designation By PositionId partyId
 * @author zzz
 * */
public static String getDesignationByPositionId(Delegator delegator,String emplPositionId){
	
	 try {
			
			if (!UtilValidate.isEmpty(emplPositionId)){
				List<GenericValue>	emplPositionAndEmplPositionTypes = delegator.findByAnd("EmplPositionAndEmplPositionType",UtilMisc.toMap("emplPositionId",emplPositionId.trim()));
				GenericValue emplPositionAndEmplPositionType=EntityUtil.getFirst(emplPositionAndEmplPositionTypes);
				if (!UtilValidate.isEmpty(emplPositionAndEmplPositionType)){
					return emplPositionAndEmplPositionType.getString("description");
				}
			}
		} catch (GenericEntityException e) {
			return null;
		}
		
		return null;
	}

/*
 * Get Employee Name By partyId 
 * @author zzz
 * */
public static String getPartyName(Delegator delegator,String partyId){
	
	 try {
			
			if (!UtilValidate.isEmpty(partyId)){
				GenericValue person = delegator.findByPrimaryKey(
						"Person", UtilMisc.toMap("partyId",partyId.trim()));

				if (!UtilValidate.isEmpty(person)){
					String name=AttendanceUtils.avoidNullPointerException((String)person.get("firstName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("middleName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("lastName"));
					return name;
				}
			}
		} catch (GenericEntityException e) {
			return null;
		}
		
		return null;
	}
/*
 * 
 */
public static List<String> getDepartmentName(Delegator delegator,String partyId,List<String> department){
	
	try {
		GenericValue party = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", partyId));

		if(!UtilValidate.isEmpty(party)){
		
			department.add(party.getString("groupName"));
			
			List<GenericValue>	partyRelationships = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationship",  UtilMisc.toMap("partyIdTo", party.getString("partyId"),"partyRelationshipTypeId","GROUP_ROLLUP")));
			GenericValue partyRelationship=EntityUtil.getFirst(partyRelationships);
			if(!UtilValidate.isEmpty(partyRelationship)&&!partyRelationship.getString("partyIdFrom").equalsIgnoreCase("Company")){
			
				department=getDepartmentName(delegator,partyRelationship.getString("partyIdFrom"), department);
			}
	}
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		return department;
	}
	return department;
}





public static List<String> getDepartmentNameInBangla(Delegator delegator,String partyId,List<String> department){
	
	try {
		GenericValue party = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", partyId));

		if(!UtilValidate.isEmpty(party)){
		
			department.add(party.getString("groupNameLocal"));
			
			List<GenericValue>	partyRelationships = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationship",  UtilMisc.toMap("partyIdTo", party.getString("partyId"),"partyRelationshipTypeId","GROUP_ROLLUP")));
			GenericValue partyRelationship=EntityUtil.getFirst(partyRelationships);
			if(!UtilValidate.isEmpty(partyRelationship)&&!partyRelationship.getString("partyIdFrom").equalsIgnoreCase("Company")){
			
				department=getDepartmentNameInBangla(delegator,partyRelationship.getString("partyIdFrom"), department);
			}
	}
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		return department;
	}
	return department;
}

/*
 * 
 * */
public static List<String> getPartyDepartment(Delegator delegator,String partyId,List<String> department){
	
	
	 try {
			List<GenericValue>	emplPositionFulfillments = delegator.findByAnd("EmplPositionFulfillment",UtilMisc.toMap("partyId", partyId));
			GenericValue emplPositionFulfillment=EntityUtil.getFirst(emplPositionFulfillments);
			if (!UtilValidate.isEmpty(emplPositionFulfillment)){
				List<GenericValue>	emplPositionAndEmplPositionTypes = delegator.findByAnd("EmplPositionAndEmplPositionType",UtilMisc.toMap("emplPositionId", emplPositionFulfillment.getString("emplPositionId")));
				GenericValue emplPositionAndEmplPositionType=EntityUtil.getFirst(emplPositionAndEmplPositionTypes);
				if (!UtilValidate.isEmpty(emplPositionAndEmplPositionType)){
					return  getDepartmentName(delegator,emplPositionAndEmplPositionType.getString("partyId"),department);
				}
			}
		} catch (GenericEntityException e) {
			return department;
		}
		
		return department;
}
/*
 * 
 * */
public static String getDepartmentName(List<String> departmentList){
	if(departmentList.size()>0){
		return departmentList.get(0);
	}
	else{
		return "";
	}
}
/*
 * 
 * */
public static String getGroupName(Delegator delegator,String partyId){
	try{
		GenericValue group = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
		if(UtilValidate.isNotEmpty(group)){
			String groupName = group.getString("groupName");
			return groupName;
		}
	
	}catch(GenericEntityException e){
		return null;
	}
	return null;
}
/*
 * 
 * */
public static String makeDepartmentName(List<String> departmentList){
	Collections.reverse(departmentList);
	if(departmentList.size()==1){
		return departmentList.get(0);	
	}
	StringBuffer sb=new StringBuffer();
	int count=0;
	for(String department:departmentList){
		if(count>1){
			sb.append("-->");
		}
		if(count>0){
			sb.append(department);
		}
		count++;
	}
	return sb.toString();
 }
/*
 * */
public static String makeSectionName(List<String> departmentList){
	Collections.reverse(departmentList);
	if(departmentList.size()==1){
		return departmentList.get(0);
	}
	StringBuffer sb=new StringBuffer();
	int count=0;
	for(String department:departmentList){
		if(count>1){
			sb.append(", ");
		}
		if(count>0){
			sb.append(department);
		}
		count++;
	}
	return sb.toString();
 }


public static String makeString(List<String> departmentList){
	
	if(departmentList.size()==1){
		return departmentList.get(0);	
	}
	StringBuffer sb=new StringBuffer();
	int count=0;
	for(String department:departmentList){
		if(count>=1){
			sb.append("-->");
		}
		if(count>=0){
			sb.append(department);
		}
		count++;
	}
	return sb.toString();
 }
/**
 * Get Basic Salary By PartyId
 * @author zzz
 * */
public static String getBasicSalaryByPartyId(Delegator delegator,String partyId){
	String basicSalary="";
	String benefitTypeIdForBasicSalary=getBenefitTypeIdForBasicSary(delegator);
	List<GenericValue> partyBenefits=null;
	if(benefitTypeIdForBasicSalary!=null&&!benefitTypeIdForBasicSalary.isEmpty()){
		try {
			partyBenefits=delegator.findByAnd("PartyBenefit", UtilMisc.toMap("partyIdTo", partyId, "benefitTypeId", benefitTypeIdForBasicSalary));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	if(partyBenefits!=null){
		GenericValue partyBenefit=EntityUtil.getFirst(partyBenefits);
		if(partyBenefit!=null&&partyBenefit.get("cost")!=null){
			basicSalary=partyBenefit.getString("cost");
		}
	}
	
	return basicSalary;
}

/**
 * Get BenefitTypeId for Basic Salary
 * @author zzz
 * */
public static String getBenefitTypeIdForBasicSary(Delegator delegator){
	String benefitTypeId="";

	String invoiceItemTypeId=getInvoiceItemTypeIdForBasicSalary(delegator,defaultGlAccountId1);
	if(invoiceItemTypeId!=null&&!invoiceItemTypeId.isEmpty()){
		benefitTypeId=getBenefitTypeIdForInvoiceItemTypeId(delegator,invoiceItemTypeId);
	}
	
	return benefitTypeId;
}
/**
 * Get InvoiceItemTypeId for Basic Salary
 * @author zzz
 * */
public static String getInvoiceItemTypeIdForBasicSalary(Delegator delegator,String defaultGlAccountId){
	String invoiceItemTypeId="";
	List<GenericValue> invoiceItemTypes=null;
	try {
		invoiceItemTypes=delegator.findByAnd("InvoiceItemType", UtilMisc.toMap("defaultGlAccountId", defaultGlAccountId,"parentTypeId","PAYROL_EARN_HOURS"));
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	if(invoiceItemTypes!=null){
		GenericValue invoiceItemType=EntityUtil.getFirst(invoiceItemTypes);
		if(invoiceItemType!=null&&invoiceItemType.get("invoiceItemTypeId")!=null){
			invoiceItemTypeId=invoiceItemType.getString("invoiceItemTypeId");
		}
	}
	return invoiceItemTypeId;
}
/**
 * Get BenefitTypeId for InvoiceItemTypeId
 * @author zzz
 * */
public static String getBenefitTypeIdForInvoiceItemTypeId(Delegator delegator,String invoiceItemTypeId){
	String benefitTypeId="";
	List<GenericValue> benefitTypes=null;
	try {
		benefitTypes=delegator.findByAnd("BenefitType",UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId));
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	if(benefitTypes!=null){
		GenericValue benefitType=EntityUtil.getFirst(benefitTypes);
		if(benefitType!=null&&benefitType.get("benefitTypeId")!=null){
			benefitTypeId=benefitType.getString("benefitTypeId");
		}
	}
	return benefitTypeId;
}
/**
 * Get TotalEmployeeVersusPosition
 * @author zzz
 * */
public static List<Map<String,String>> getTotalEmployeeVersusPosition(Delegator delegator,List<Map<String,String>> employeeByPositions,String partyId){
	
	List<GenericValue> children=getChildren(delegator, partyId);
	
	List<GenericValue> positions=getPositionsByParyId(delegator, partyId);
	
	for(GenericValue child:children){

		employeeByPositions=getTotalEmployeeVersusPosition(delegator,employeeByPositions,child.getString("partyIdTo"));
	}
	
	if(positions!=null){
		for(GenericValue position:positions){
			Map<String,String> employeeByPosition=FastMap.newInstance();
			String description="";
			if(position.get("description")!=null){
				 description=position.getString("description");
			}
			employeeByPosition.put(description, getTotalEmployeeByPositionId(delegator, position.getString("emplPositionId")).size()+"");
			employeeByPositions.add(employeeByPosition);
		}
	}
	return employeeByPositions;
	
}
/**
 * Get Unit Tree By Unit PartyId
 * @author zzz
 * */
public static List<GenericValue> getUnitTreeByUnitPartyId(Delegator delegator,List<GenericValue> unitTree,String unitPartyId){
	List<GenericValue> children=getChildren(delegator, unitPartyId);
	for(GenericValue child:children){
		unitTree.add(child);
		unitTree=getUnitTreeByUnitPartyId(delegator,unitTree,child.getString("partyIdTo"));
	}
	return unitTree;
}
/**
 * Get PartyRelationShip Entity By PartyId
 * @author zzz
 * */
public static GenericValue getPartyRelationShipEntityByPartyId(Delegator delegator,String partyId){
	List<GenericValue> partyRelationShipList=null;
	try {
		List<EntityExpr> exprs = FastList.newInstance();
        exprs.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
        exprs.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP"));
        partyRelationShipList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
   
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	if(partyRelationShipList!=null){
		return EntityUtil.getFirst(partyRelationShipList);
	}
	return null;
}
/**
 * Check Card Existance By PartyId
 * @author zzz
 * */
public static boolean checkCardExistanceByPartyId(Delegator delegator,String partyId){
	
	try {
		
		GenericValue	person = delegator.findByPrimaryKey("Person",UtilMisc.toMap("partyId", partyId));
		
		if (!UtilValidate.isEmpty(person)){
			 if(person.getString("cardId")!=null&&!person.getString("cardId").isEmpty()){
				 return true; 
			 }
		}
	} catch (GenericEntityException e) {
		return false;
	}
	
	return false;

}
/**
 * 
 * */
public static boolean checkEmployeeActive(Delegator delegator,String partyId){

	GenericValue party=null;
	try {
		party = EntityUtil.getFirst(delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo", partyId)));
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	if(party!=null){
		Timestamp thruDate=party.getTimestamp("thruDate");
	
		if(thruDate!=null){
			return true;
		}
	}
	return false;
}

/**
* Get Employee Name By userLoginId.
* @param Delegator
* @param Employee userLoginId in String.
* @return EmployeeName in String.
* @author zzz
* */
public static String getEmployeeNameByUserLoginId(Delegator delegator,String userLoginId){
	String employeeName="";
	String partyId=getPartyIDFromUserLoginID(delegator, userLoginId);
	List<GenericValue> persons=null;
	try {
		persons=delegator.findByAnd("Person", UtilMisc.toMap("partyId", partyId));
		if(persons!=null){
			GenericValue person=EntityUtil.getFirst(persons);
			if(person!=null){
				String firstName="";
				String lastName="";
				String middleName="";
				if(person.get("firstName")!=null){
					firstName=person.getString("firstName");
				}
				if(person.get("middleName")!=null){
					middleName=person.getString("middleName");
				}
				if(person.get("lastName")!=null){
					lastName=person.getString("lastName");
				}
				
				employeeName=firstName+""+middleName+""+lastName;
			}
		}
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return employeeName;
}

/**
* Get EmployeeName By PartyId.
* @param Delegator, Employee PartyId in String.
* @return EmployeeName in String.
* @author zzz
* */
public static String getEmployeeNameByPartyId(Delegator delegator,String partyId){
	String employeeName="";
	List<GenericValue> persons=null;
	try {
		persons=delegator.findByAnd("Person", UtilMisc.toMap("partyId", partyId));
		if(persons!=null){
			GenericValue person=EntityUtil.getFirst(persons);
			if(person!=null){
				String firstName="";
				String lastName="";
				String middleName="";
				if(person.get("firstName")!=null){
					firstName=person.getString("firstName");
				}
				if(person.get("middleName")!=null){
					middleName=person.getString("middleName");
				}
				if(person.get("lastName")!=null){
					lastName=person.getString("lastName");
				}
				
				employeeName=firstName+""+middleName+""+lastName;
			}
		}
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return employeeName;
}
/**
 *This method use to get firstTiffinRate,secondTiffinRate and nightRate in Map<String,String> By Employee PositionId.
 *@author zzz
 *@return firstTiffinRate,secondTiffinRate and nightRate in Map<String,String>.
 */
 public static Map<String,String> getTiffinAndNighitRateByEmpPositionId(Delegator delegator,String emplPositionId){
	 String firstTiffinRate="";
	 String secondTiffinRate="";
	 String nightRate="";
	 Map<String,String> tiffinAndNightRateMap=FastMap.newInstance();
	 List<GenericValue> designationPreferences=null;
	 try {
		 designationPreferences=delegator.findByAnd("DesignationPreference",UtilMisc.toMap("emplPositionId", emplPositionId));
		 if(designationPreferences!=null){
			 GenericValue designationPreference=EntityUtil.getFirst(designationPreferences);
			 if(UtilValidate.isNotEmpty(designationPreference)){
				 if(designationPreference.get("firstTiffinRate")!=null){
					 firstTiffinRate= designationPreference.getString("firstTiffinRate");
				 }
				 if(designationPreference.get("secondTiffinRate")!=null){
					 secondTiffinRate= designationPreference.getString("secondTiffinRate");
				 }
				 if(designationPreference.get("nightRate")!=null){
					 nightRate= designationPreference.getString("nightRate");
				 }
			 }
		 }
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 tiffinAndNightRateMap.put("firstTiffinRate", firstTiffinRate);
	 tiffinAndNightRateMap.put("secondTiffinRate", secondTiffinRate);
	 tiffinAndNightRateMap.put("nightRate", nightRate);
	 
	 return tiffinAndNightRateMap;
 }
 /**
  * This method use to get  firstTiffinStartTime,firstTiffinEndTime,secondTiffinStartTime,
  * secondTiffinEndTime,nightStartTime,nightEndTime in Map<String,String> by Organization PartyId
  * @author zzz
  * 
  */
 public static Map<String,String> getTiffinScheduleByOrganizationPartyId(Delegator delegator,String orgPartyId){
	String firstTiffinStartTime="";
	String firstTiffinEndTime="";
	String	secondTiffinStartTime="";
	String secondTiffinEndTime="";
	String nightStartTime="";
	String nightEndTime="";
	Map<String,String> tiffinScheduleMap=FastMap.newInstance();
	
	List<GenericValue> orgPreferences=null;
	try {
		orgPreferences=delegator.findByAnd("OrgAttendancePreference", UtilMisc.toMap("orgPartyId", orgPartyId));
		if(orgPreferences!=null){
			GenericValue orgPreference=EntityUtil.getFirst(orgPreferences);
			if(UtilValidate.isNotEmpty(orgPreference)){
				 if(orgPreference.get("firstTiffinStartTime")!=null){
					 firstTiffinStartTime= orgPreference.getString("firstTiffinStartTime");
				 }
				 if(orgPreference.get("firstTiffinEndTime")!=null){
					 firstTiffinEndTime= orgPreference.getString("firstTiffinEndTime");
				 }
				 if(orgPreference.get("secondTiffinStartTime")!=null){
					 secondTiffinStartTime= orgPreference.getString("secondTiffinStartTime");
				 }
				 if(orgPreference.get("secondTiffinEndTime")!=null){
					 secondTiffinEndTime= orgPreference.getString("secondTiffinEndTime");
				 }
				 if(orgPreference.get("nightStartTime")!=null){
					 nightStartTime= orgPreference.getString("nightStartTime");
				 }
				 if(orgPreference.get("nightEndTime")!=null){
					 nightEndTime= orgPreference.getString("nightEndTime");
				 }
			}
		}
	
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	tiffinScheduleMap.put("firstTiffinStartTime", firstTiffinStartTime);
	tiffinScheduleMap.put("firstTiffinEndTime", firstTiffinEndTime);
	tiffinScheduleMap.put("secondTiffinStartTime", secondTiffinStartTime);
	tiffinScheduleMap.put("secondTiffinEndTime", secondTiffinEndTime);
	tiffinScheduleMap.put("nightStartTime", nightStartTime);
	tiffinScheduleMap.put("nightEndTime", nightEndTime);
	
	return tiffinScheduleMap; 
 }
 
 /**
  * This method use to get  firstTiffinStartTime,firstTiffinEndTime,secondTiffinStartTime,
  * secondTiffinEndTime,nightStartTime,nightEndTime in Map<String,String> by Employee PartyId
  * @author zzz
  * 
  */
 public static Map<String,String> getTiffinScheduleByEmpPartyIdFromEmpPreference(Delegator delegator,String partyId){
	String firstTiffinStartTime="";
	String firstTiffinEndTime="";
	String	secondTiffinStartTime="";
	String secondTiffinEndTime="";
	String nightStartTime="";
	String nightEndTime="";
	Map<String,String> tiffinScheduleMap=FastMap.newInstance();
	
	List<GenericValue> empPreferences=null;
	try {
		empPreferences=delegator.findByAnd("EmployeeAttendancePreference", UtilMisc.toMap("employeeId", partyId));
		if(empPreferences!=null){
			GenericValue empPreference=EntityUtil.getFirst(empPreferences);
			if(UtilValidate.isNotEmpty(empPreference)){
				 if(empPreference.get("firstTiffinStartTime")!=null){
					 firstTiffinStartTime= empPreference.getString("firstTiffinStartTime");
				 }
				 if(empPreference.get("firstTiffinEndTime")!=null){
					 firstTiffinEndTime= empPreference.getString("firstTiffinEndTime");
				 }
				 if(empPreference.get("secondTiffinStartTime")!=null){
					 secondTiffinStartTime= empPreference.getString("secondTiffinStartTime");
				 }
				 if(empPreference.get("secondTiffinEndTime")!=null){
					 secondTiffinEndTime= empPreference.getString("secondTiffinEndTime");
				 }
				 if(empPreference.get("nightStartTime")!=null){
					 nightStartTime= empPreference.getString("nightStartTime");
				 }
				 if(empPreference.get("nightEndTime")!=null){
					 nightEndTime= empPreference.getString("nightEndTime");
				 }
			}
		}
	
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	tiffinScheduleMap.put("firstTiffinStartTime", firstTiffinStartTime);
	tiffinScheduleMap.put("firstTiffinEndTime", firstTiffinEndTime);
	tiffinScheduleMap.put("secondTiffinStartTime", secondTiffinStartTime);
	tiffinScheduleMap.put("secondTiffinEndTime", secondTiffinEndTime);
	tiffinScheduleMap.put("nightStartTime", nightStartTime);
	tiffinScheduleMap.put("nightEndTime", nightEndTime);
	
	return tiffinScheduleMap; 
 }
 
 /**
  * This method use to get  firstTiffinStartTime,firstTiffinEndTime,secondTiffinStartTime,
  * secondTiffinEndTime,nightStartTime,nightEndTime in Map<String,String> by Employee PartyId
  * @author zzz
  * 
  */
 public static Map<String,String> getTiffinScheduleByEmpPartyIdFromRosterPreference(Delegator delegator,String partyId){
	String firstTiffinStartTime="";
	String firstTiffinEndTime="";
	String	secondTiffinStartTime="";
	String secondTiffinEndTime="";
	String nightStartTime="";
	String nightEndTime="";
	Map<String,String> tiffinScheduleMap=FastMap.newInstance();
	
	List<GenericValue> rosterEmployees=null;
	
	List<GenericValue> empRosterPreferences=null;
	try {
		
		rosterEmployees=delegator.findByAnd("RosterEmployee",UtilMisc.toMap("RosterEmployee", UtilMisc.toMap("employeeId", partyId)));
		
		GenericValue rosterEmployee=EntityUtil.getFirst(rosterEmployees);
		
		if(UtilValidate.isNotEmpty(rosterEmployee)){
			String rosterPreferenceId=rosterEmployee.getString("rosterEmployee");
			
			empRosterPreferences=delegator.findByAnd("AttendanceRosterPreference", UtilMisc.toMap("rosterPreferenceId", rosterPreferenceId));
			if(empRosterPreferences!=null){
				GenericValue empRosterPreference=EntityUtil.getFirst(empRosterPreferences);
				if(UtilValidate.isNotEmpty(empRosterPreference)){
					 if(empRosterPreference.get("firstTiffinStartTime")!=null){
						 firstTiffinStartTime= empRosterPreference.getString("firstTiffinStartTime");
					 }
					 if(empRosterPreference.get("firstTiffinEndTime")!=null){
						 firstTiffinEndTime= empRosterPreference.getString("firstTiffinEndTime");
					 }
					 if(empRosterPreference.get("secondTiffinStartTime")!=null){
						 secondTiffinStartTime= empRosterPreference.getString("secondTiffinStartTime");
					 }
					 if(empRosterPreference.get("secondTiffinEndTime")!=null){
						 secondTiffinEndTime= empRosterPreference.getString("secondTiffinEndTime");
					 }
					 if(empRosterPreference.get("nightStartTime")!=null){
						 nightStartTime= empRosterPreference.getString("nightStartTime");
					 }
					 if(empRosterPreference.get("nightEndTime")!=null){
						 nightEndTime= empRosterPreference.getString("nightEndTime");
					 }
				}
			}
		}
		
	
	
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	tiffinScheduleMap.put("firstTiffinStartTime", firstTiffinStartTime);
	tiffinScheduleMap.put("firstTiffinEndTime", firstTiffinEndTime);
	tiffinScheduleMap.put("secondTiffinStartTime", secondTiffinStartTime);
	tiffinScheduleMap.put("secondTiffinEndTime", secondTiffinEndTime);
	tiffinScheduleMap.put("nightStartTime", nightStartTime);
	tiffinScheduleMap.put("nightEndTime", nightEndTime);
	
	return tiffinScheduleMap; 
 }
 
 /**
  * This method use to get  firstTiffinStartTime,firstTiffinEndTime,secondTiffinStartTime,
  * secondTiffinEndTime,nightStartTime,nightEndTime in Map<String,String> by day
  * @author zzz
  * 
  */
 public static Map<String,String> getTiffinScheduleByEmpPartyIdFromDayPreference(Delegator delegator,java.sql.Date day,String orgPartyId){
	String firstTiffinStartTime="";
	String firstTiffinEndTime="";
	String	secondTiffinStartTime="";
	String secondTiffinEndTime="";
	String nightStartTime="";
	String nightEndTime="";
	Map<String,String> tiffinScheduleMap=FastMap.newInstance();
	
	GenericValue dayPreference=getDayPreference(delegator, day, orgPartyId);

		if(dayPreference!=null){
			
			if(UtilValidate.isNotEmpty(dayPreference)){
				 if(dayPreference.get("firstTiffinStartTime")!=null){
					 firstTiffinStartTime= dayPreference.getString("firstTiffinStartTime");
				 }
				 if(dayPreference.get("firstTiffinEndTime")!=null){
					 firstTiffinEndTime= dayPreference.getString("firstTiffinEndTime");
				 }
				 if(dayPreference.get("secondTiffinStartTime")!=null){
					 secondTiffinStartTime= dayPreference.getString("secondTiffinStartTime");
				 }
				 if(dayPreference.get("secondTiffinEndTime")!=null){
					 secondTiffinEndTime= dayPreference.getString("secondTiffinEndTime");
				 }
				 if(dayPreference.get("nightStartTime")!=null){
					 nightStartTime= dayPreference.getString("nightStartTime");
				 }
				 if(dayPreference.get("nightEndTime")!=null){
					 nightEndTime= dayPreference.getString("nightEndTime");
				 }
			}
		}
	
	tiffinScheduleMap.put("firstTiffinStartTime", firstTiffinStartTime);
	tiffinScheduleMap.put("firstTiffinEndTime", firstTiffinEndTime);
	tiffinScheduleMap.put("secondTiffinStartTime", secondTiffinStartTime);
	tiffinScheduleMap.put("secondTiffinEndTime", secondTiffinEndTime);
	tiffinScheduleMap.put("nightStartTime", nightStartTime);
	tiffinScheduleMap.put("nightEndTime", nightEndTime);
	
	return tiffinScheduleMap; 
 }
 /**
  * This Method Filter TiffinSchedule Map According to priority.
  **/
 public static Map<String,String> filterTiffinScheduleMap(Map<String,String> tiffinScheduleMapForOrganization,Map<String,String> tiffinScheduleMapForSelectedDate,
		 Map<String,String> tiffinScheduleMapForEmp,Map<String,String> tiffinScheduleMapForRoster){
		
	 	Map<String,String> tiffinScheduleMap=FastMap.newInstance(); 
	 	
	 	String firstTiffinStartTime="";
		String firstTiffinEndTime="";
		String secondTiffinStartTime="";
		String secondTiffinEndTime="";
		String nightStartTime="";
		String nightEndTime=""; 
		
		
		//Filter By Organization
		if(!tiffinScheduleMapForOrganization.get("firstTiffinStartTime").isEmpty()){
			firstTiffinStartTime=tiffinScheduleMapForOrganization.get("firstTiffinStartTime");	
		}
		if(!tiffinScheduleMapForOrganization.get("firstTiffinEndTime").isEmpty()){
			firstTiffinEndTime=tiffinScheduleMapForOrganization.get("firstTiffinEndTime");	
		}
		if(!tiffinScheduleMapForOrganization.get("secondTiffinStartTime").isEmpty()){
			secondTiffinStartTime=tiffinScheduleMapForOrganization.get("secondTiffinStartTime");	
		}
		if(!tiffinScheduleMapForOrganization.get("secondTiffinEndTime").isEmpty()){
			secondTiffinEndTime=tiffinScheduleMapForOrganization.get("secondTiffinEndTime");	
		}
		if(!tiffinScheduleMapForOrganization.get("nightStartTime").isEmpty()){
			nightStartTime=tiffinScheduleMapForOrganization.get("nightStartTime");	
		}
		if(!tiffinScheduleMapForOrganization.get("nightEndTime").isEmpty()){
			nightEndTime=tiffinScheduleMapForOrganization.get("nightEndTime");	
		}
		
		//Filter By Roster
		if(!tiffinScheduleMapForRoster.get("firstTiffinStartTime").isEmpty()){
			firstTiffinStartTime=tiffinScheduleMapForRoster.get("firstTiffinStartTime");	
		}
		if(!tiffinScheduleMapForRoster.get("firstTiffinEndTime").isEmpty()){
			firstTiffinEndTime=tiffinScheduleMapForRoster.get("firstTiffinEndTime");	
		}
		if(!tiffinScheduleMapForRoster.get("secondTiffinStartTime").isEmpty()){
			secondTiffinStartTime=tiffinScheduleMapForRoster.get("secondTiffinStartTime");	
		}
		if(!tiffinScheduleMapForRoster.get("secondTiffinEndTime").isEmpty()){
			secondTiffinEndTime=tiffinScheduleMapForRoster.get("secondTiffinEndTime");	
		}
		if(!tiffinScheduleMapForRoster.get("nightStartTime").isEmpty()){
			nightStartTime=tiffinScheduleMapForRoster.get("nightStartTime");	
		}
		if(!tiffinScheduleMapForRoster.get("nightEndTime").isEmpty()){
			nightEndTime=tiffinScheduleMapForRoster.get("nightEndTime");	
		}
		
		//Filter By Employee
		if(!tiffinScheduleMapForEmp.get("firstTiffinStartTime").isEmpty()){
			firstTiffinStartTime=tiffinScheduleMapForEmp.get("firstTiffinStartTime");	
		}
		if(!tiffinScheduleMapForEmp.get("firstTiffinEndTime").isEmpty()){
			firstTiffinEndTime=tiffinScheduleMapForEmp.get("firstTiffinEndTime");	
		}
		if(!tiffinScheduleMapForEmp.get("secondTiffinStartTime").isEmpty()){
			secondTiffinStartTime=tiffinScheduleMapForEmp.get("secondTiffinStartTime");	
		}
		if(!tiffinScheduleMapForEmp.get("secondTiffinEndTime").isEmpty()){
			secondTiffinEndTime=tiffinScheduleMapForEmp.get("secondTiffinEndTime");	
		}
		if(!tiffinScheduleMapForEmp.get("nightStartTime").isEmpty()){
			nightStartTime=tiffinScheduleMapForEmp.get("nightStartTime");	
		}
		if(!tiffinScheduleMapForEmp.get("nightEndTime").isEmpty()){
			nightEndTime=tiffinScheduleMapForEmp.get("nightEndTime");	
		}
		
		
		//Filter By Date
		if(!tiffinScheduleMapForSelectedDate.get("firstTiffinStartTime").isEmpty()){
			firstTiffinStartTime=tiffinScheduleMapForSelectedDate.get("firstTiffinStartTime");	
		}
		if(!tiffinScheduleMapForSelectedDate.get("firstTiffinEndTime").isEmpty()){
			firstTiffinEndTime=tiffinScheduleMapForSelectedDate.get("firstTiffinEndTime");	
		}
		if(!tiffinScheduleMapForSelectedDate.get("secondTiffinStartTime").isEmpty()){
			secondTiffinStartTime=tiffinScheduleMapForSelectedDate.get("secondTiffinStartTime");	
		}
		if(!tiffinScheduleMapForSelectedDate.get("secondTiffinEndTime").isEmpty()){
			secondTiffinEndTime=tiffinScheduleMapForSelectedDate.get("secondTiffinEndTime");	
		}
		if(!tiffinScheduleMapForSelectedDate.get("nightStartTime").isEmpty()){
			nightStartTime=tiffinScheduleMapForSelectedDate.get("nightStartTime");	
		}
		if(!tiffinScheduleMapForSelectedDate.get("nightEndTime").isEmpty()){
			nightEndTime=tiffinScheduleMapForSelectedDate.get("nightEndTime");	
		}
		
		
		tiffinScheduleMap.put("firstTiffinStartTime", firstTiffinStartTime);
		tiffinScheduleMap.put("firstTiffinEndTime", firstTiffinEndTime);
		tiffinScheduleMap.put("secondTiffinStartTime", secondTiffinStartTime);
		tiffinScheduleMap.put("secondTiffinEndTime", secondTiffinEndTime);
		tiffinScheduleMap.put("nightStartTime", nightStartTime);
		tiffinScheduleMap.put("nightEndTime", nightEndTime);
		
		return tiffinScheduleMap;
 }
 
 /**
  * Get Employee CardId from Employee PartyId
  * @author zzz
  * */
public static String getCardIdFromPartyId(Delegator delegator,String partyId){
		
		try {
			
			GenericValue	person = delegator.findByPrimaryKey("Person",UtilMisc.toMap("partyId", partyId));
			
			if (!UtilValidate.isEmpty(person)){
				return person.getString("cardId");
			}
		} catch (GenericEntityException e) {
			return null;
		}
		
		return null;
	}



/*SUNIPA countTotalEmployeeByPositionId*/


public static 	int countTotalNeedOverByPosition(Delegator delegator, String positionId){
	int needOver=0;
	int budget=getTotalBudgetEmployeeByPosition( delegator,  positionId);
	int reqruited=countTotalEmployeeByPositionId( delegator,  positionId);
	needOver=budget-reqruited;
	return needOver;	
}

public static int getTotalBudgetEmployeeByPosition(Delegator delegator, String positionId){
	
	try {
		List<GenericValue> employeePositions = delegator.findList("EmplPosition", EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS, positionId), null, null, null, false);
		for(GenericValue employeePosition:employeePositions){
			List<GenericValue> budgetItems = delegator.findList("BudgetItem", EntityCondition.makeCondition("budgetId", EntityOperator.EQUALS, employeePosition.get("budgetId")), null, null, null, false);
			for(GenericValue budgetItem:budgetItems){
				if(budgetItem.get("amount")!=null){
					try{
						BigDecimal bigValue=(BigDecimal)budgetItem.get("amount");
						return bigValue.intValue();
					}catch(Exception ex){
						logger.debug("Exception to parse in getTotalBudgetEmployeeByPosition---->"+ex);
						return 0;
					}
				}
				return 0;
			}
		}

	} catch (GenericEntityException e) {
		logger.debug("Exception to getTotalBudgetEmployeeByPosition---->"+e);
		return 0;
	}
	return 0;
}
/*count Total Employee By PositionId*/
public static 	int countTotalEmployeeByPositionId(Delegator delegator, String positionId){
	List<GenericValue> emplPositionFulfillmentList=FastList.newInstance();
		try {
			emplPositionFulfillmentList = delegator.findList("EmplPositionFulfillment", EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS, positionId), null, null, null, false);
			return emplPositionFulfillmentList.size();
			} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
/*SUNIPA get Total Employee By Org*/

public static int getTotalEmployeeByOrg(Delegator delegator,String orgPartyId){
		List<GenericValue> employeelist=FastList.newInstance();
		employeelist=getTotalEmployee(delegator, employeelist, orgPartyId);
		/*employeelist = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom",orgPartyId.trim()));*/
		return employeelist.size();
}


/*
* Get Today Employee Absence ,Present ,Leave Information ByPosition
* */	
public static Map<String, String> getTodaysAttendanceInfoByPosition(Delegator delegator,String	orgPartyId,String	emplPositionId){
		
		int absentEmployees=0;
		int presentEmployees=0;
		int lateEmployees=0;
		int leaveEmployees=0;
		
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
	
		List<GenericValue> emplPositionFulfillmentList=FastList.newInstance();
		List<Timestamp> holidays=new ArrayList<Timestamp>();
	
	//	partyRelationships = AttendanceUtils.getTotalEmployee(delegator, partyRelationships, orgPartyId);
		 try {
			emplPositionFulfillmentList = delegator.findList("EmplPositionFulfillment", EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS, emplPositionId), null, null, null, false);
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		for(GenericValue partyRelationship:emplPositionFulfillmentList){
			
				List<GenericValue> employeeLeaves=new ArrayList<GenericValue>();
				
				String employeeId=(String)partyRelationship.get("partyId");
				
				
		        List<EntityExpr> exprsList = FastList.newInstance();
		        exprsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
		        exprsList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
				
				try {
					employeeLeaves = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsList, EntityOperator.AND), null, null, null, false);
					
					for (GenericValue employeeLeave : employeeLeaves) {
						Timestamp leavefromDate=employeeLeave.getTimestamp("fromDate");
						Timestamp leavethruDate=employeeLeave.getTimestamp("thruDate");
						 //Get today leave
						if(toDay.compareTo(leavefromDate) >= 0 && toDay.compareTo(leavethruDate) <= 0){
							 leaveEmployees++;
						 }
					}
					
				
						 
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
		attendanceNotifications.put("leaveEmployees",String.valueOf(leaveEmployees));

		return attendanceNotifications;
	}



/*
* Get Today Employee Leave Information for HR dashBoard
* */	
public static Map<String,String> getTodayLeaveInformationForDashBoard(Delegator delegator,String orgPartyId ){
		Map<String,String> todayLeaveInformation=FastMap.newInstance();
		
		List<GenericValue> approvedEmpLeave=FastList.newInstance();
		int leaveEmployees=0;
		Date today=new Date(System.currentTimeMillis());
		String employeeId="";
		List<GenericValue> partyList=FastList.newInstance();
		partyList=AttendanceUtils.getTotalEmployee(delegator,partyList,orgPartyId);
			 
		try {
					 
		for(GenericValue party:partyList){
		 employeeId=party.getString("partyId");
		
        List<EntityExpr> exprsApproved= FastList.newInstance();
        exprsApproved.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
        exprsApproved.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
       
       
        approvedEmpLeave=delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsApproved, EntityOperator.AND), null, null, null, false);
			
			for (GenericValue employeeLeave : approvedEmpLeave) {
				Timestamp leavefromDate=employeeLeave.getTimestamp("fromDate");
				Timestamp leavethruDate=employeeLeave.getTimestamp("thruDate");
				 //Get today leave
				if(today.compareTo(leavefromDate) >= 0 && today.compareTo(leavethruDate) <= 0){
					leaveEmployees+=1;
				 }
				/*if(  today.equals(leavefromDate)||today.before(leavethruDate)){
					 leaveEmployees+=1;
				 }*/
				
			}
        }
		} catch (GenericEntityException e) {
			return null;
		}
		
	
		todayLeaveInformation.put("approveLeave", String.valueOf(leaveEmployees));
		
		return todayLeaveInformation;
	}



/**
 * This Method Give Budget Employee By Position Id
 * 
 * @author zzz
 **/
public static int getBudgetEmployeeByPosition(Delegator delegator,
		String emplPositionId) {

	try {
		
		List<GenericValue> employeePositions = delegator.findList("EmplPosition", EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS,emplPositionId), null, null, null, false);
		for (GenericValue employeePosition : employeePositions) {
			List<GenericValue> budgetItems = delegator.findList(
					"BudgetItem", EntityCondition.makeCondition("budgetId",
							EntityOperator.EQUALS,
							employeePosition.get("budgetId")), null, null,
					null, false);
			for (GenericValue budgetItem : budgetItems) {
				if (budgetItem.get("amount") != null) {
					try {
						BigDecimal bigValue = (BigDecimal) budgetItem.get("amount");
						return bigValue.intValue();
					} catch (Exception ex) {
						logger.debug("Exception to parse in getTotalBudgetEmployeeByPosition---->"
								+ ex);
						return 0;
					}
				}
				return 0;
			}
		}

	} catch (GenericEntityException e) {
		logger.debug("Exception to getTotalBudgetEmployeeByPosition---->"
				+ e);
		return 0;
	}
	return 0;
}

/**
 * This Method Give Total Budget By Unit(Department,Devision, etc) Party Id.
 * 
 * @author zzz
 * */
public static int getTotalBudgetByPartyId(Delegator delegator,int totalBudget, String partyId) {
	List<GenericValue> children = getChildren(delegator, partyId);

	List<GenericValue> positions = getPositionsByParyId(delegator, partyId);

	for (GenericValue child : children) {

		totalBudget = getTotalBudgetByPartyId(delegator, totalBudget,child.getString("partyIdTo"));
	}

	if (positions != null) {
		for (GenericValue position : positions) {
			totalBudget += getBudgetEmployeeByPosition(delegator,position.getString("emplPositionId"));
		}
	}
	return totalBudget;

}

/**
 * This Method Give Total Budget By Unit(Department,Devision, etc) Party Id.
 * 
 * @author zzz
 * */
public static int getTotalChildBudgetByPartyId(Delegator delegator,int totalBudget, String partyId) {
		List<GenericValue> children = getChildren(delegator, partyId);

		for (GenericValue child : children) {
			String partyIdTo=child.getString("partyIdTo");
			List<GenericValue> positions = getPositionsByParyId(delegator, partyIdTo);
		
			if (positions != null) {
				for (GenericValue position : positions) {
					totalBudget += getBudgetEmployeeByPosition(delegator,position.getString("emplPositionId"));
				}
			
			totalBudget = getTotalChildBudgetByPartyId(delegator, totalBudget,partyIdTo);
		}
	}
	return totalBudget;

}


public static String getOTAllowenceForEmployee(Delegator delegator,String orgId,String employeeId) {
	String otAllowene="";
	List<GenericValue> orgAttendancePreference  = FastList.newInstance();
	List<GenericValue> employeePreference=FastList.newInstance();
	try {
		
		orgAttendancePreference = delegator. findByAnd("OrgAttendancePreference",UtilMisc.toMap("orgPartyId", orgId,"oTallowances", "Y"));
		
		String userLoginId=AttendanceUtils. getUserLoginIDFromPartyID(delegator, employeeId);
		employeePreference=delegator.findByAnd("EmployeeAttendancePreference",UtilMisc.toMap("employeeId", userLoginId));
		if(UtilValidate.isNotEmpty(orgAttendancePreference)&&UtilValidate.isNotEmpty(employeePreference)){
			 otAllowene="YES";
			 }
		else 
			{ otAllowene="No";}
		
		
		List<GenericValue> rosterIdcheck = delegator.findByAnd("RosterEmployee", UtilMisc.toMap("employeeId",employeeId));
		GenericValue rosterEmpId = EntityUtil.getFirst(rosterIdcheck);
		if (!UtilValidate.isEmpty(rosterEmpId)){
		String	rosterPreferenceId=rosterEmpId.getString("rosterPreferenceId");
		
		if (UtilValidate.isNotEmpty(rosterPreferenceId)  && UtilValidate.isNotEmpty(orgAttendancePreference)) {
			
			List<GenericValue> rosterAttendancePreference = delegator.findByAnd("AttendanceRosterPreference"
					, UtilMisc.toMap("rosterPreferenceId",rosterPreferenceId.trim()
							,"oTallowances","Y"));
			if(UtilValidate.isNotEmpty(rosterAttendancePreference)){
			
				 otAllowene="YES";
			}
			
			else  otAllowene="NO";
			}
		}
		
		} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
	return otAllowene;

}

//This method is correct for getting OT allowence 
public static String getOTAllowenceForEmp(Delegator delegator,String orgId,String employeeId) {
	String otAllowene="";
	List<GenericValue> orgAttendancePreferences  = FastList.newInstance();
	List<GenericValue> employeePreferences=FastList.newInstance();
	try {
		
		GenericValue rosterEmpId=null;
		GenericValue employeePreference = null;
		GenericValue orgAttendancePreference =null;
		String userLoginId=AttendanceUtils. getUserLoginIDFromPartyID(delegator, employeeId);
		
		orgAttendancePreferences = delegator. findByAnd("OrgAttendancePreference",UtilMisc.toMap("orgPartyId", orgId,"oTallowances", "Y"));
	   if(UtilValidate.isNotEmpty(orgAttendancePreferences)){
		   orgAttendancePreference = EntityUtil.getFirst(orgAttendancePreferences);
	   }
			
		employeePreferences=delegator.findByAnd("EmployeeAttendancePreference",UtilMisc.toMap("employeeId", employeeId));
		if(UtilValidate.isNotEmpty(employeePreferences)){
			employeePreference = EntityUtil.getFirst(employeePreferences);
		   }
	    		
		List<GenericValue> rosterIdcheck = delegator.findByAnd("RosterEmployee", UtilMisc.toMap("employeeId",employeeId));
		if(UtilValidate.isNotEmpty(rosterIdcheck)){
			rosterEmpId = EntityUtil.getFirst(rosterIdcheck);
		   }		 
		
		if(UtilValidate.isNotEmpty(employeePreference) && employeePreference.get("oTallowances")!=null){
			 otAllowene=employeePreference.getString("oTallowances");
			 }else if (!UtilValidate.isEmpty(rosterEmpId)){
				 String	rosterPreferenceId=rosterEmpId.getString("rosterPreferenceId");
					
				 if (UtilValidate.isNotEmpty(rosterPreferenceId)) {
						
						List<GenericValue> rosterAttendancePreference = delegator.findByAnd("AttendanceRosterPreference"
								, UtilMisc.toMap("rosterPreferenceId",rosterPreferenceId.trim()));
						GenericValue rosterAttendancePre = EntityUtil.getFirst(rosterAttendancePreference);
						if(UtilValidate.isNotEmpty(rosterAttendancePre) && rosterAttendancePre.get("oTallowances")!=null){
						
							otAllowene=rosterAttendancePre.getString("oTallowances");
						}
						}
					}else if(UtilValidate.isNotEmpty(orgAttendancePreference) && orgAttendancePreference.get("oTallowances")!=null){
				       otAllowene=orgAttendancePreference.getString("oTallowances");
			 }else{
				 otAllowene="N";
			 }
		
		} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
	return otAllowene;

}


/**
 * @author zzz
 * @param partyId
 * @param delegator
 * @param joinDate
 * @param orgPartyId
 * @param selectedProperty
 * @return numberOfMonth to confirmation
 * @throws GenericEntityException 
 */

public static int getNumberOfMonthToConfirmation(String partyId,Delegator delegator,String orgPartyId,Timestamp joinDate){
	int numberOfMonth=0;
	String confirmationAfter = "";
	java.sql.Date preDate = new java.sql.Date(joinDate.getTime());
	try {
		confirmationAfter = UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, preDate, orgPartyId, UtilPreferenceProperties.CONFIRMATIONAFTER);
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

   if(UtilValidate.isNotEmpty(confirmationAfter)){         		
    numberOfMonth =Integer.parseInt(confirmationAfter);		        		 
   }
	
	return numberOfMonth;
}

public static Timestamp getModifiedDayEndWithTime(Timestamp stamp,String toTime) {
		Calendar calendarEnd = Calendar.getInstance();
		calendarEnd.setTime(stamp);
		String[] toTimeArray=toTime.split(":");
		if(toTimeArray.length>0){
			calendarEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(toTimeArray[0]));
		}else{
			calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
		}
		if(toTimeArray.length>1){
			calendarEnd.set(Calendar.MINUTE, Integer.parseInt(toTimeArray[1]));
		}else{
		calendarEnd.set(Calendar.MINUTE, 59);
		}
		if(toTimeArray.length>2){
			calendarEnd.set(Calendar.SECOND, Integer.parseInt(toTimeArray[2]));
		}else{
			calendarEnd.set(Calendar.SECOND, 59);
		}
		if(toTimeArray.length>3){
			calendarEnd.set(Calendar.MILLISECOND, Integer.parseInt(toTimeArray[3]));
		}else{
			calendarEnd.set(Calendar.MILLISECOND, 999);    
		}
	    Timestamp retStamp = new Timestamp(calendarEnd.getTime().getTime());
	    retStamp.setNanos(0);
	    return retStamp;
	}

public static Timestamp getModifiedDayStartWithTime(Timestamp stamp,String fromTime) {
		Calendar calendarEnd = Calendar.getInstance();
		calendarEnd.setTime(stamp);
		String[] fromTimeArray=fromTime.split(":");
		if(fromTimeArray.length>0){
			calendarEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(fromTimeArray[0]));
		}else{
			calendarEnd.set(Calendar.HOUR_OF_DAY, 0);
		}
		if(fromTimeArray.length>1){
			calendarEnd.set(Calendar.MINUTE, Integer.parseInt(fromTimeArray[1]));
		}else{
			calendarEnd.set(Calendar.MINUTE, 0);
		}
		if(fromTimeArray.length>2){
			calendarEnd.set(Calendar.SECOND, Integer.parseInt(fromTimeArray[2]));
		}else{
			calendarEnd.set(Calendar.SECOND, 0);
		}
		if(fromTimeArray.length>3){
			calendarEnd.set(Calendar.MILLISECOND, Integer.parseInt(fromTimeArray[3]));
		}else{
			calendarEnd.set(Calendar.MILLISECOND, 0);    
		}
	    Timestamp retStamp = new Timestamp(calendarEnd.getTime().getTime());
	    retStamp.setNanos(0);
	    return retStamp;
	}
/*
 * 
 */
public static boolean filterValidEmployeeByDate(Delegator delegator,String partyId,Timestamp startDate){

	GenericValue party=null;
	try {
		party = EntityUtil.getFirst(delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo", partyId)));
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	if(party!=null){
		Timestamp joiningDate=party.getTimestamp("fromDate");
		if(joiningDate.getTime()<=startDate.getTime()){				
			Timestamp thruDate=party.getTimestamp("thruDate");	
			if(thruDate!=null){
				return thruDate.getTime()>startDate.getTime();
			}else{
				return true;
			}
		}
	}
	return false;
}
/*
 * Find out valid employee of a specific date
 * 
 */
public static List<GenericValue> filterValidEmployeeListByDate(Delegator delegator,List<GenericValue> partyList,Timestamp startDate){
	for(GenericValue party:partyList){
		String partyId=party.getString("partyId");
		if(!filterValidEmployeeByDate(delegator,partyId,startDate)){
			partyList.remove(party);	
		}
	}
	return partyList;
}


public static Timestamp getModifiedMonthStart(Timestamp stamp, int daysLater) {
    Calendar tempCal = Calendar.getInstance();
    tempCal.setTime(stamp);
    tempCal.set(tempCal.get(Calendar.YEAR), tempCal.get(Calendar.MONTH), 1, 0, 0, 0);
    tempCal.add(Calendar.DAY_OF_MONTH, daysLater);
    Timestamp retStamp = new Timestamp(tempCal.getTimeInMillis());
    retStamp.setNanos(0);
    return retStamp;
}

/**
 * @author zzz
 * @param delegator  
 * @param orgPartyId
 * @param partyId 
 * @param selectMonthYear  String; January 2014
 * @return bonus within the specified month and year.
 * */

public static  String findMonthlyAttendanceBonus(Delegator delegator,String partyId,String selectMonthYear,String orgPartyId) 
		throws ParseException {
	   String bonus="";

	java.sql.Date monthYearInDate=null;		
	
	try {
		
		if(selectMonthYear!=null&&!selectMonthYear.isEmpty()){
			
			//First date of month
			monthYearInDate=AttendanceUtils.getsqltDate(selectMonthYear);

			Calendar cal = Calendar.getInstance();
            cal.setTime(monthYearInDate);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));        
            java.sql.Date monthLastDate=  new java.sql.Date(cal.getTime().getTime());
            
            
            //Get holidays of selected month
        	List<Timestamp> holidays=new ArrayList<Timestamp>();
			holidays=	AttendanceUtils.getHolidays(orgPartyId, new Timestamp(monthYearInDate.getTime()), new Timestamp(monthLastDate.getTime()), delegator);
					 
			 Map<String, String> employee=	FastMap.newInstance();
			 int totalAbsence =0;
			 int totalholidays=0;

			 List<Long>monthlogs=FastList.newInstance();				
			 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				
	         Calendar start = Calendar.getInstance();
	         start.setTime(monthYearInDate);
	         Calendar end = Calendar.getInstance();
	         end.setTime(monthLastDate);

			// find log from month 1st date to last date
			 for (java.util.Date date =  start.getTime(); !start.after(end); start.add(Calendar.DATE, 1), date =  start.getTime()) 
		      {
		           Calendar cal1 = Calendar.getInstance();
		           cal1.setTime(date);
		           if(holidays.contains(cal1.getTime())){
		        	   totalholidays++;
		           continue;
		       }	          

			   List<EntityCondition> exprsAnd = FastList.newInstance();
			   Set<String> fieldsToSelect = UtilMisc.toSet("logtimeStamp");
			   List<String> orderBy = FastList.newInstance();
			   orderBy.add("logtimeStamp ASC");
			   String searchDate=dateFormat.format(cal1.getTime());
			    
	           exprsAnd.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	           exprsAnd.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("logtimeStamp"), EntityOperator.LIKE, EntityFunction.UPPER(searchDate + "%")));
	            
	           List<GenericValue> empLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprsAnd, EntityOperator.AND), fieldsToSelect,orderBy , null, true);
	           if(!empLogs.isEmpty())
	           {
	            	monthlogs.add(empLogs.get(0).getTimestamp("logtimeStamp").getTime());      	
	           }else{
	            	totalAbsence++;
	            }    
	            
			}
			//Get selected month employee bonus from total absence and late entry 	 					  
			  bonus= getMonthlyBonus(monthYearInDate,monthlogs,totalAbsence,partyId,orgPartyId,delegator);
		

		}} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	return bonus;
}

/**
 * @author zzz
 * @param delegator  
 * @param orgPartyId
 * @param partyId 
 * @param year   The year String; 2013,2014
 * @param month   The month String; 0 for jan,1 for feb, .. 11 for dec.
 * @return bonus within the specified month and year.
 */
public static String getMonthlyAttendanceBonusForEmployee(Delegator delegator,String month,String year,String paryId,String orgPartyId){ 
	String bonus="";
	String selectMonthYear="";
	month = new DateFormatSymbols().getMonths()[(Integer.parseInt(month))];
	selectMonthYear=month+" "+year;
	
	  try {
		bonus =findMonthlyAttendanceBonus(delegator,paryId,selectMonthYear,orgPartyId);
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
	return bonus;	
}

/**
 * @Description This method is to sort List of Map according to (departmentName then employeeId) 
 * @author tomal mahdi
 * @param List<Map<String,String>> 
 * @return ArrayList<Map>
 **/
public static ArrayList<Map> getSortedList(List<Map<String,String>> sortedlist) {
	 
	ArrayList mylist = new ArrayList(sortedlist);
	Collections.sort(mylist,new ListOfMapComparator());
	return mylist;
}
/**
 * @Description This method is to sort List of Map according to (departmentName then employeeId) 
 * @author tomal mahdi
 * @param List<Map<String,Object>> 
 * @return ArrayList<Map<String,String>
 **/
public static ArrayList<Map<String,Object>> getSortedList1(List<Map<String,Object>> sortedlist) {
	 
	ArrayList mylist = new ArrayList(sortedlist);
	Collections.sort(mylist,new ListComparator());
	return mylist;
}


/**
 * @author zzz
 * @Description This method is to find organizationPreferencdId at current date
 * @param Delegator,orgPartyId
 * @return String organizationPreferencdId
 * @throws GenericEntityException 
 */
public static String getOrgPreferenceId(String orgPartyId,Delegator delegator) throws GenericEntityException {
	java.sql.Date currentTime=new Date(System.currentTimeMillis());

	String orgPreId=null;
	Timestamp now=new Timestamp(currentTime.getTime());
	GenericValue orgAttendancePreference=null;
	EntityCondition orgPrefCodition=EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
            EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, currentTime),
            EntityCondition.makeCondition("orgPartyId", EntityOperator.EQUALS, orgPartyId)
    ), EntityJoinOperator.AND);

	List<GenericValue> orgAttendancePreferences=delegator.findList("OrgAttendancePreference", orgPrefCodition, null, null, null, false);
	if(UtilValidate.isNotEmpty(orgAttendancePreferences)){
		orgAttendancePreference = orgAttendancePreferences.get(orgAttendancePreferences.size()-1);
		orgPreId=orgAttendancePreference.getString("organizationPreferencdId");
	}
	
    return orgPreId;
}

/**
 * @author zzz
 * @Description This method get employee Organization,Division,Department,Section,Line on a map
 * @param Delegator,PartyId
 * @return Map<Strin String> organizationPreferencdId
 * @throws GenericEntityException 
 */
public static Map<String, String> getEmployeeParents(String partyId,Delegator delegator) throws GenericEntityException {
	Map<String, String> empParent = FastMap.newInstance();
    String organization="";
    String division="";
    String department="";
    String section="";
    String line="";
    int j=0;
	List<String> parentList=getPartyDepartment(delegator, partyId, new ArrayList<String>());
    if(UtilValidate.isNotEmpty(parentList)){
   	 
   	Collections.reverse(parentList);
		for(String parent: parentList){
			if(j==0){
				organization=parent;
				empParent.put("organization", organization);
			} 
			else if(j==1){
				division=parent;
				empParent.put("division", division);
 				
			}
			else if(j==2){
				department=parent;
				empParent.put("department", department);
			}
			else if(j==3){
				section=parent;
				empParent.put("section", section);
			}
			else if(j==4){
				line=parent;
				empParent.put("line", line);
			}
			j++;
		}
    } 
	
    return empParent;
}


/**
 * @author zzz
 * @param delegator
 * @param partyId
 * @return
 */
public static String getEmploymentType(Delegator delegator,String partyId){
	String employmentType="";
	try {
		GenericValue employment=EntityUtil.getFirst(delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo", partyId)));
		employmentType=employment.getString("employmentType");
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return employmentType;
}

/**
 * @see This method find employee by position & employeementType
 * @author zzz
 * @param delegator
 * @param positionId,employmentType
 * @return employee list
 */
public static 	List<GenericValue> getEmployeeByPositionAndEmploymentType(Delegator delegator, String positionId, String employmentType){
	List<GenericValue> emplPositionFulfillmentList=FastList.newInstance();
	Set<String> fieldsToSelect = FastSet.newInstance();
	fieldsToSelect.add("partyId");

	try {
		 emplPositionFulfillmentList = delegator.findList("EmplPosFulfillmentAndEmployment",EntityCondition.makeCondition(
				 EntityCondition.makeCondition("emplPositionId",EntityOperator.EQUALS, positionId),
			     EntityOperator.AND, 
			     EntityCondition.makeCondition("employmentType", EntityOperator.EQUALS,employmentType)),
			     fieldsToSelect, null, null, false);			
		return emplPositionFulfillmentList;

	} catch (GenericEntityException e) {
		return null;
	}
}


/**
 * @see This method find employee by position & employeementType
 * @author zzz
 * @param delegator
 * @param positionId,employmentType
 * @return employee list
 */
public static 	List<GenericValue> getWorkersByPosition(Delegator delegator, String positionId){
	List<GenericValue> emplPositionFulfillmentList=FastList.newInstance();
	Set<String> fieldsToSelect = FastSet.newInstance();
	fieldsToSelect.add("partyId");

	try {
		 emplPositionFulfillmentList = delegator.findList("EmplPosFulfillmentAndEmployment",EntityCondition.makeCondition(
				 EntityCondition.makeCondition("emplPositionId",EntityOperator.EQUALS, positionId),
			     EntityOperator.AND, 
			     EntityCondition.makeCondition("employmentType", EntityOperator.EQUALS,"WORKERS")),
			     fieldsToSelect, null, null, false);			
		return emplPositionFulfillmentList;

	} catch (GenericEntityException e) {
		return null;
	}
}

/**
 * @see This method find staff by position & employeementType
 * @author zzz
 * @param delegator
 * @param positionId,employmentType
 * @return employee list
 */
public static 	List<GenericValue> getStaffByPosition(Delegator delegator, String positionId){
	List<GenericValue> emplPositionFulfillmentList=FastList.newInstance();
	Set<String> fieldsToSelect = FastSet.newInstance();
	fieldsToSelect.add("partyId");

	try {
		 emplPositionFulfillmentList = delegator.findList("EmplPosFulfillmentAndEmployment",EntityCondition.makeCondition(
			 EntityCondition.makeCondition("emplPositionId",EntityOperator.EQUALS, positionId),
			 EntityOperator.AND, EntityCondition.makeCondition(EntityCondition.makeCondition("employmentType", EntityOperator.EQUALS,"STAFF"),
			 EntityOperator.AND,EntityCondition.makeCondition("isManager", EntityOperator.NOT_EQUAL,"Y"))
			  ), fieldsToSelect, null, null, false);			
		return emplPositionFulfillmentList;

	} catch (GenericEntityException e) {
		return null;
	}
}
/**
 * @see This method find manager by position & employeementType
 * @author zzz
 * @param delegator
 * @param positionId,employmentType
 * @return employee list
 */
public static 	List<GenericValue> getManagersByPosition(Delegator delegator, String positionId){
	List<GenericValue> emplPositionFulfillmentList=FastList.newInstance();
	Set<String> fieldsToSelect = FastSet.newInstance();
	fieldsToSelect.add("partyId");

	try {
		 emplPositionFulfillmentList = delegator.findList("EmplPosFulfillmentAndEmployment",EntityCondition.makeCondition(
			 EntityCondition.makeCondition("emplPositionId",EntityOperator.EQUALS, positionId),
			 EntityOperator.AND, EntityCondition.makeCondition(EntityCondition.makeCondition("employmentType", EntityOperator.EQUALS,"STAFF"),
			 EntityOperator.AND,EntityCondition.makeCondition("isManager", EntityOperator.EQUALS,"Y"))
			  ), fieldsToSelect, null, null, false);			
		return emplPositionFulfillmentList;

	} catch (GenericEntityException e) {
		return null;
	}
}
/**
 * @author Tomal mahdi
 * @param delegator
 * @param orgPartyId
 * @param partyId
 * @param fromDate, toDate
 * @return bonus within the specified fromDate to toDate
 * */

public static  Map<String,Double> findEOTByEmplPartyId(Delegator delegator,String partyId,Timestamp fromDate,Timestamp toDate,
																								String orgPartyId)
		throws ParseException {
		
		double eotAmount=0.0;
		String otCalculateOperand ="0";
		Map<String,Double> EOTamount = FastMap.newInstance();
		
		EOTamount.put("eotAmount", 0.0);
        EOTamount.put("hoursPerTaka", 0.0);
        EOTamount.put("eotHours", 0.0);
        
		
		if(UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(toDate)){

		java.sql.Date monthStart = new Date(fromDate.getTime());
	    java.sql.Date monthEnd=  new Date(toDate.getTime());

	    int eotHours=0;
	    //Get holidays of selected month
		List<Timestamp> holidays=new ArrayList<Timestamp>();
		holidays=Utils.getHolidays(orgPartyId, new Timestamp(monthStart.getTime()), new Timestamp(monthEnd.getTime()), delegator);

		try {
			 otCalculateOperand = UtilAttendancePreference.getPreferencePropertyValue(
					partyId,
					delegator,
					monthEnd,
					orgPartyId,
					UtilPreferenceProperties.OTCALCULATIONOPERAND);
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		 	// find log from month 1st date to last date
			while (monthStart.getTime() <= monthEnd.getTime()) {
			    long eotTime=0;
				if (holidays.contains(monthStart)){
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(monthStart);
					calendar.add(Calendar.DATE, 1);
					monthStart = new Date(calendar.getTime().getTime());
					continue;
				}

				try {

					String eoTStartTime = UtilAttendancePreference.getPreferencePropertyValue(
							partyId,
							delegator,
							monthStart,
							orgPartyId,
							UtilPreferenceProperties.EXTRAOTSTARTTIME);
					String eoTEndTime = UtilAttendancePreference.getPreferencePropertyValue(
							partyId, delegator,
							monthStart, orgPartyId,
							UtilPreferenceProperties.EXTRAOTENDTIME);
					String hasEOTAllounce = UtilAttendancePreference.getPreferencePropertyValue(
							partyId,
							delegator,
							monthStart,
							orgPartyId,
							UtilPreferenceProperties.EXTRAOTALLOWANCES);


					List<GenericValue> employeeLogs = null;

					Timestamp dayStart = Utils.getModifiedDayStart(new Timestamp(monthStart.getTime()));
					if (hasEOTAllounce.equalsIgnoreCase("Y")) {
					if(!UtilValidate.isEmpty(eoTEndTime)){
					Timestamp eoTEndTimeInDate = Utils.getStringTiffinTimeToTimStamp(eoTEndTime,monthStart);

					//Timestamp dayEnd =Utils.addHourToTimestamp(eoTEndTimeInDate, 1);
					Timestamp dayEnd =eoTEndTimeInDate;

					List<EntityExpr> exprs = FastList.newInstance();
					List<String> orderBy = FastList.newInstance();
					orderBy.add("logtimeStamp ASC");
					exprs.add(EntityCondition.makeCondition("logtimeStamp",EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
					exprs.add(EntityCondition.makeCondition("logtimeStamp",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
					exprs.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS, partyId));

					employeeLogs = delegator.findList("EmployeeLog",EntityCondition.makeCondition(exprs,EntityOperator.AND), null, orderBy, null,false);

					if (employeeLogs != null && employeeLogs.size() > 1) {
							Timestamp eoTStartTimeInDate = Utils.getStringTiffinTimeToTimStamp(eoTStartTime, monthStart);

							GenericValue outemployeeLog = employeeLogs.get(employeeLogs.size() - 1);
							Timestamp exitTime = (Timestamp) outemployeeLog.get("logtimeStamp");

							if (exitTime.getTime() > eoTStartTimeInDate.getTime()) {
								eotTime = exitTime.getTime() - eoTStartTimeInDate.getTime();
							}
							HashMap<String, Integer> otTimeInHashMap = UtilDateTime.covertLongToHourMinuteSecond(eotTime);
							int eotHour = otTimeInHashMap.get("hour");
							int eotMinute = otTimeInHashMap.get("minute");
							if (eotMinute > 50) {
								eotHour = eotHour + 1;
							}
							eotHours=eotHours+eotHour;


						}
					}
				}
			}catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(monthStart);
			calendar.add(Calendar.DATE, 1);
			monthStart = new Date(calendar.getTime().getTime());
		}
		double eotH = eotHours; 
		if(eotHours>0){
			String basicSalary=	getBasicSalaryByPartyId(delegator, partyId);
            if (UtilValidate.isNotEmpty(basicSalary)&&UtilValidate.isNotEmpty(otCalculateOperand)) {
                eotAmount=(Double)(Double.valueOf(basicSalary)/Double.valueOf(otCalculateOperand))*eotHours;
                EOTamount.put("eotAmount", eotAmount);
                if(UtilValidate.isNotEmpty(otCalculateOperand)){
                	EOTamount.put("hoursPerTaka", Double.parseDouble(basicSalary)/Double.parseDouble(otCalculateOperand));	
                }
                EOTamount.put("eotHours", eotH);
                
            }else{
            	if(UtilValidate.isNotEmpty(otCalculateOperand) && UtilValidate.isNotEmpty(basicSalary)){
                	EOTamount.put("hoursPerTaka", Double.parseDouble(basicSalary)/Double.parseDouble(otCalculateOperand));	
                }
            }
        }

	}return EOTamount;
}

/**
 * @author zzz
 * @param delegator
 * @param partyId (parantPartyId)
 * @return PositionList
 * */

public static 	List<GenericValue> getPositionsByParentId(Delegator delegator, String partyId){
	
	List<GenericValue> positions=FastList.newInstance();
	Set<String> fieldsToSelect = FastSet.newInstance();
	fieldsToSelect.add("emplPositionId");
	
	try {
		positions= delegator.findList("EmplPosition", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),fieldsToSelect,null,null,false);
	}catch (GenericEntityException e) {
		return positions;
	}
	return positions;
}

/** 
 * @param 
 * @return 
 * @author zzz
 * @throws GenericEntityException 
 * 
**/
public static List<GenericValue> getEmployeesByParantIdAndEmplType(Delegator delegator,List<GenericValue> totalEmployee,String partyId,String employmentType){
	
	List<GenericValue> positions=getPositionsByParentId(delegator, partyId);
	
	if(positions!=null){
		for(GenericValue position:positions){
			totalEmployee.addAll(AttendanceUtils.getEmployeeByPositionAndEmploymentType(delegator, position.getString("emplPositionId"),employmentType));
		}
	}
	return totalEmployee;
	
}
/** 
 * @param 
 * @return 
 * @author zzz
 * @throws GenericEntityException 
 * 
**/
public static List<GenericValue> getStaffByParantIdAndEmplType(Delegator delegator,List<GenericValue> totalEmployee,String partyId,String employmentType){
	
	List<GenericValue> positions=getPositionsByParentId(delegator, partyId);
	
	if(positions!=null){
		for(GenericValue position:positions){
			totalEmployee.addAll(AttendanceUtils.getStaffByPosition(delegator, position.getString("emplPositionId")));
		}
	}
	return totalEmployee;
	
}
/** 
 * @param 
 * @return 
 * @author zzz
 * @throws GenericEntityException 
 * 
**/
public static List<GenericValue> getWorkersByParantIdAndEmplType(Delegator delegator,List<GenericValue> totalEmployee,String partyId,String employmentType){
	
	List<GenericValue> positions=getPositionsByParentId(delegator, partyId);
	
	if(positions!=null){
		for(GenericValue position:positions){
			totalEmployee.addAll(AttendanceUtils.getWorkersByPosition(delegator, position.getString("emplPositionId")));
		}
	}
	return totalEmployee;
	
}
/** 
 * @param 
 * @return 
 * @author zzz
 * @throws GenericEntityException 
 * 
**/
public static List<GenericValue> getManagersByParantId(Delegator delegator,List<GenericValue> totalEmployee,String partyId){
	
	List<GenericValue> positions=getPositionsByParentId(delegator, partyId);
	
	if(positions!=null){
		for(GenericValue position:positions){
			totalEmployee.addAll(AttendanceUtils.getManagersByPosition(delegator, position.getString("emplPositionId")));
		}
	}
	return totalEmployee;
	
}

	/**
	 * @description get formatted Date
	 * @author tomalmahdi
	 * 
	 * */
	public static String getFormatedDate(String fromDate,String formate){
		if(UtilValidate.isEmpty(fromDate)) return "";
		java.sql.Date fromTime = null;
		fromTime = java.sql.Date.valueOf(fromDate);
		DateFormat df = new SimpleDateFormat(formate);
		String selectedDate = df.format(fromTime);
		return selectedDate;
	}

	/**
	 * @param String inputTime, Integer intervalTime, String condition
	 * @example 5:00pm,10,up 
	 * @author tomalmahdi
	 * */
	public static String getDuration(String inputTime, Integer intervalTime, String condition){
		inputTime = inputTime.toLowerCase();
		condition = condition.toLowerCase();
		Scanner in = new Scanner(inputTime).useDelimiter("[^0-9]+");
		int hour = in.nextInt();
		int minute = in.nextInt();
		String intValue = inputTime.replaceAll("[^a-z]", "");
		if(intValue.equals("am") && hour==12 && condition!="down"){
			hour = 0;
		}
		if(intValue.equals("pm") && hour!=12){
			hour+=12;
		}
		int totalMinute = hour*60+minute;
		int firstInterval=0,lastinterval=0;
		if(condition.equals("up")){
			firstInterval =totalMinute;
			lastinterval = totalMinute+intervalTime;
		}else{
			firstInterval =totalMinute-intervalTime;
			lastinterval = totalMinute;
		}
		String retString ="",dayIdentifier="";
		int r = (int) (Math.random() * (lastinterval - firstInterval)) + firstInterval;
		int calculatedHour =(int)(r/60);
		int calculatedMinute = r%60;
		if(calculatedHour==12){
			retString = String.valueOf(calculatedHour);
			dayIdentifier = "pm";
		}else if(calculatedHour>12){
			calculatedHour = calculatedHour%12;
			retString = String.valueOf(calculatedHour);
			dayIdentifier = "pm";
		}else{
			retString = String.valueOf(calculatedHour);
			dayIdentifier = "am";
		}
		retString +=":";
		if(calculatedMinute<10){
			retString +="0";
		}retString +=String.valueOf(calculatedMinute);
		retString+=dayIdentifier;
	
		/*for(int i=0;i<10;i++){
			int r = (int) (Math.random() * (20 - 10)) + 10;
			System.out.println(r);
		}*/
	
		return retString;
	}
	
	/**
	 * Returns a psuedo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *@author zzz
	 * @param min Minimim value
	 * @param max Maximim value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static int randInt(int min, int max) {
	    // Usually this can be a field rather than a method variable
	    Random rand = new Random();
	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	
	/**
	 * Returns a psuedo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *@author shuravi
	 * @param min Minimim value
	 * @param max Maximim value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static String getListSize(List<GenericValue> listName) {
	    return String.valueOf(listName.size());
	}
	

}