package org.ofbiz.webapp.contessa;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;


public class Utils {
	
	public static final String officeHoliday = "OFFICE_HOLIDAY";
	public static final String publicHoliday = "PUBLIC_HOLIDAY";
	public static final String othersHoliday = "OTHERS_HOLIDAY";
	
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
	public static long getEndTime(java.sql.Date date){
		
		Calendar calendarEnd = Calendar.getInstance();
		calendarEnd.setTime(date);
		calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
		calendarEnd.set(Calendar.MINUTE, 59);
		calendarEnd.set(Calendar.SECOND, 59);
		calendarEnd.set(Calendar.MILLISECOND, 999);
		
		return calendarEnd.getTime().getTime();
	}
	/**
	 * @author zzz
	 * This method is used for calculating bonus from 1 month Attendance log (for single employee)
	 * @return bonus
	 */

	public static String getMonthlyBonus(Date date,List<Long>monthlogs, int totalAbsent, String partyId,String orgPartyId,Delegator delegator,int totalHalfDayLeave) throws ParseException{

		  String bonus = "0";
		  int totalAbsentDays = totalAbsent;
		  int lateEntry = totalAbsent;
		  int lateDates=0;
		  int intimeDates=0;
		  String LateEntryTime="";
		  String attendanceBonusAllowances="";
		  String lateEntryDaysForAttenBonusDisable="";
		  String lateEntryDaysForAttenBonusDeduction="";
		  String montlyBonusAmount="";
		  String bonusDeductionAmount="";
		  String leaveAbsentForBonusDisable="";
		  String attenBonusDeductionAmountForhalfDayleave="";

			try {
			    attendanceBonusAllowances=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date, orgPartyId, UtilPreferenceProperties.ATTENDANCEBONUSALLOWANCES);
			    lateEntryDaysForAttenBonusDisable=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date, orgPartyId, UtilPreferenceProperties.LATEENTRYDAYSFORATTENBONUSDISABLE);
				lateEntryDaysForAttenBonusDeduction=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date, orgPartyId, UtilPreferenceProperties.LATEENTRYDAYSFORATTENBONUSDEDUCTION);
			    montlyBonusAmount=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date, orgPartyId, UtilPreferenceProperties.MONTLYBONUSAMOUNT);
			    bonusDeductionAmount=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date, orgPartyId, UtilPreferenceProperties.BONUSDEDUCTIONAMOUNT);
			    leaveAbsentForBonusDisable=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date, orgPartyId, UtilPreferenceProperties.LEAVEORABSENTFORBONUSDISABLE);
			    attenBonusDeductionAmountForhalfDayleave=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date, orgPartyId, UtilPreferenceProperties.HALFDAYLEAVEFORATTBONUSDeduction);
			    
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
			  
			  if(UtilValidate.isEmpty(lateEntryDaysForAttenBonusDisable)){
				  lateEntryDaysForAttenBonusDisable="32";
			  	  }
			  if(UtilValidate.isEmpty(lateEntryDaysForAttenBonusDeduction)){
			  		lateEntryDaysForAttenBonusDeduction="32";
			  	  }
			  if(UtilValidate.isEmpty(leaveAbsentForBonusDisable)){
				  leaveAbsentForBonusDisable="32";
			  	  }
		  	 if(UtilValidate.isEmpty(montlyBonusAmount)){
		  		montlyBonusAmount="0";
			  	  }
		  	if(UtilValidate.isEmpty(bonusDeductionAmount)){
		  		bonusDeductionAmount="0";
		  	  }
		  	if(UtilValidate.isEmpty(attenBonusDeductionAmountForhalfDayleave)){
		  		attenBonusDeductionAmountForhalfDayleave="0";
		  	  }
			try{
		  		int daysForBonusDisable  = Integer.parseInt(lateEntryDaysForAttenBonusDisable);
		  	  	int daysForBonusDeduction = Integer.parseInt(lateEntryDaysForAttenBonusDeduction);
		  	  	int absentDaysForBonusDisable = Integer.parseInt(leaveAbsentForBonusDisable);
		  	  	if(daysForBonusDeduction>daysForBonusDisable){
		  	  	 daysForBonusDeduction=daysForBonusDisable;
		  	  	}
		  	  	int bonusAmount = Integer.parseInt(montlyBonusAmount);
		  	  	int deductionAmount = Integer.parseInt(bonusDeductionAmount);
		  	  	int halfdayLeaveDeductionAmount = Integer.parseInt(attenBonusDeductionAmountForhalfDayleave); 		  
/*		  		  if(lateEntry<daysForBonusDeduction){
		  	    	  bonus=montlyBonusAmount; 
		  	      }
		  	      else if(lateEntry<daysForBonusDisable){
		  	    	  bonus=Integer.toString(bonusAmount-deductionAmount);
		  	      }else{
		  	    	  bonus="0";  
		  	      }*/		  		  
		  		  
		  		  if(lateEntry>=daysForBonusDisable || totalAbsentDays>=absentDaysForBonusDisable){
		  	    	  bonus="0"; 
		  	      }
		  	      else if(lateEntry>=daysForBonusDeduction){
		  	    	  bonus=Integer.toString(bonusAmount-deductionAmount);
		  	    	  if(totalHalfDayLeave>0){
			  	    	  bonus=Integer.toString(bonusAmount-(halfdayLeaveDeductionAmount*totalHalfDayLeave));  
		  	    	  }
		  	      }else{
		  	    	  bonus=montlyBonusAmount;  
		  	    	  if(totalHalfDayLeave>0){
			  	    	  bonus=Integer.toString(bonusAmount-(halfdayLeaveDeductionAmount*totalHalfDayLeave));  
		  	    	  }
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
	 */
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
	 * @author zzz
	 * Get Employee Position By Employee partyId
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
	 * @author zzz
	 * 
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
	/**
	 * @author zzz
	 * Get Holidays
	 * */
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
	
	/**
	 * @author zzz
	 * @param delegator  
	 * @param orgPartyId
	 * @param partyId 
	 * @param holidayType
	 * @param year The year String; 2013,2014
	 * @param month  The month String; 0 for jan,1 for feb, .. 11 for dec.
	 * @return number of holiday within the specified month and year.
	 */
	public static int getMonthlyPublicHolidays(Delegator delegator,String month,String year,String orgPartyId,String holidayType){ 
		List<GenericValue> publicHolidays  = new ArrayList<GenericValue>();
		java.sql.Date monthFirstDate=null;
		String selectMonthYear="";		
		month = new DateFormatSymbols().getMonths()[(Integer.parseInt(month))];
		selectMonthYear=month+" "+year;
		if(selectMonthYear!=null&&!selectMonthYear.isEmpty()){
			//First date of month
			monthFirstDate=Utils.getsqltDate(selectMonthYear);

			Calendar cal = Calendar.getInstance();
            cal.setTime(monthFirstDate);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));        
            java.sql.Date monthLastDate=  new java.sql.Date(cal.getTime().getTime());
            
            Timestamp dayStart=new Timestamp(monthFirstDate.getTime());
			Timestamp dayEnd=new Timestamp(monthLastDate.getTime());
            
            List<EntityExpr> exprs = FastList.newInstance();
			List<String> orderBy = FastList.newInstance();
			orderBy.add("estimatedStartDate ASC");
			
			exprs.add(EntityCondition.makeCondition("estimatedStartDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayStart));
			exprs.add(EntityCondition.makeCondition("estimatedStartDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			exprs.add(EntityCondition.makeCondition("companyId", EntityOperator.EQUALS, orgPartyId));
			exprs.add(EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, holidayType));

			  try {
				publicHolidays = delegator.findList("WorkEffort", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);
			  } catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}         
			 
		}
		return publicHolidays.size();	
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
                        EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, "PUBLIC_HOLIDAY"),
                        EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, "OTHERS_HOLIDAY")
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
	public static long getStartTime(java.sql.Date date){
		
		
		Calendar calendarStart = Calendar.getInstance();
		calendarStart.setTime(date);
		calendarStart.set(Calendar.HOUR_OF_DAY, 0);
		calendarStart.set(Calendar.MINUTE, 0);
		calendarStart.set(Calendar.SECOND, 0);
		calendarStart.set(Calendar.MILLISECOND, 0);		
		return calendarStart.getTime().getTime();
	}
	public static double getTotalLeave(Delegator delegator,String orgPartyId,String partyId,String fromDate,String toDate){
		double totalLeave = 0;
		
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		List<EntityExpr> exprsList = FastList.newInstance();
		exprsList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId.trim()));
		exprsList.add(EntityCondition.makeCondition("leaveStatus",EntityOperator.EQUALS,"LEAVE_APPROVED"));
		
		List<GenericValue> EmplLeave = FastList.newInstance();
		try {
			EmplLeave = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsList,EntityOperator.AND),null, null, null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(UtilValidate.isNotEmpty(EmplLeave)){
		
			for(GenericValue emplLeave:EmplLeave ){
						
						/*getting leave start date & end date*/
						Timestamp leaveStart = emplLeave.getTimestamp("fromDate");
						Timestamp leaveEnd = emplLeave.getTimestamp("thruDate");
						String leaveDuration = emplLeave.getString("leaveDuration");
						if(UtilValidate.isEmpty(leaveDuration)){
							leaveDuration = "";
						}
						
						double leaveCount = 0;
						if(!leaveDuration.equals("Full_Day")){
							leaveCount = 0.5;
						}else{
								
								java.sql.Date sTime=null;
								java.sql.Date eTime=null;
								
								sTime = Date.valueOf(fromDate);
								eTime = Date.valueOf(toDate);
								
								long isTime = Utils.getStartTime(sTime);
								long ieTime = Utils.getEndTime(eTime);
								
								long tsTime = leaveStart.getTime();
								long teTime = leaveEnd.getTime();
								
								/*logger.debug("Screen S Timse "+isTime+" Screen E Time "+ieTime);
								logger.debug("Table S Time "+tsTime+" Table E Time "+teTime);*/
								String LeaveEnjoyedDate = "";
								boolean flag = true;
								if(isTime>ieTime){
									isTime+=ieTime;
									ieTime = isTime-ieTime;
									isTime = isTime-ieTime;
								}
								//first table start date & end date between input start & end date
								if(isTime<=tsTime && ieTime>=teTime){
									
									if(df.format(tsTime).toString().equals(df.format(teTime).toString())){
										LeaveEnjoyedDate = df.format(tsTime);
										leaveCount = 1;
									}else{
										LeaveEnjoyedDate = df.format(tsTime)+"\n"+df.format(teTime);
										leaveCount = (int) ((teTime - tsTime)/86400000);
									}
								}
								//second
								else if(isTime>=tsTime && ieTime<=teTime){
									if(df.format(isTime).toString().equals(df.format(ieTime).toString())){
										LeaveEnjoyedDate = df.format(isTime);
										leaveCount = 1;
									}else{
										if(df.format(isTime).toString() == df.format(ieTime).toString()){
											LeaveEnjoyedDate = df.format(isTime);
											leaveCount = 1;
										}else{
											LeaveEnjoyedDate = df.format(isTime)+"\n"+df.format(ieTime);
											leaveCount = (int) ((ieTime - isTime)/86400000);
										}
									}
								}
								//third
								else if(isTime<=tsTime && tsTime<=ieTime && ieTime<=teTime){
									if(df.format(tsTime).toString().equals(df.format(ieTime).toString())){
										LeaveEnjoyedDate = df.format(tsTime);
										leaveCount = 1;
									}else{
										LeaveEnjoyedDate = df.format(tsTime)+"\n"+df.format(ieTime);
										leaveCount = (int) ((ieTime - tsTime)/86400000);
									}
								}
								//fourth
								else if(isTime>=tsTime && isTime<=teTime && teTime<=ieTime){
									if(df.format(isTime).toString().equals(df.format(teTime).toString())){
										LeaveEnjoyedDate = df.format(isTime);
										leaveCount = 1;
									}else{
										LeaveEnjoyedDate = df.format(isTime)+"\n"+df.format(teTime);
										leaveCount = (int) ((teTime - isTime)/86400000);
									}
								}
								// each day 86400000	
						}
						totalLeave+=leaveCount;
				}
		}
		
		return totalLeave;
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

	
	public static int getEmployeeOt(Delegator delegator,String orgPartyId,String partyId,Date date,Timestamp exitTime) throws GenericEntityException{
		int otHour=0;
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
		return otHour;
	}
	
	
	public static int getEmployeeAuditOt(Delegator delegator,String orgPartyId,String partyId,Date date,Timestamp exitTime) throws GenericEntityException{
		int otHour=0;
		String oTStartTime=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date, orgPartyId, UtilPreferenceProperties.OTSTARTTIME);
		String oTEndTime=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date, orgPartyId, UtilPreferenceProperties.OTENDTIME);
		//String hasOTAllounce=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date, orgPartyId, UtilPreferenceProperties.OTALLOWANCES);
		//if(hasOTAllounce.equals("Y")){
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
		//}
		return otHour;
	}
}
