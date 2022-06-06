package org.ofbiz.webapp.contessa;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.metadata.Util;
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
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

public class Attendance {
	private static final String defaultGlAccountId1=UtilProperties.getPropertyValue("contessa.properties", "basicSalary.defaultGlAccountId1");
	private static final String defaultGlAccountId2=UtilProperties.getPropertyValue("contessa.properties", "basicSalary.defaultGlAccountId2");
	private static Logger logger=Logger.getLogger("Attendance");
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
public static String getBenefitTypeIdForBasicSary(Delegator delegator){
	String benefitTypeId="";

	String invoiceItemTypeId=getInvoiceItemTypeIdForBasicSalary(delegator,defaultGlAccountId1);
	if(invoiceItemTypeId!=null&&!invoiceItemTypeId.isEmpty()){
		benefitTypeId=getBenefitTypeIdForInvoiceItemTypeId(delegator,invoiceItemTypeId);
	}

	return benefitTypeId;
}
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
 * @author zzz
 * @param delegator
 * @param orgPartyId
 * @param paryId
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
 * @author zzz
 * @param delegator
 * @param orgPartyId
 * @param paryId
 * @param year   The year String; 2013,2014
 * @param month   The month String; 0 for jan,1 for feb, .. 11 for dec.
 * @return bonus within the specified month and year.
 */
public static Map<String,Object> getMonthlyEOTAmountByEmplPartyId(Delegator delegator,String month,String year,String paryId,String orgPartyId){
	Map<String,Object> retValue=FastMap.newInstance();
	BigDecimal eotAmount=new BigDecimal("0");
	String selectMonthYear="";
	month = new DateFormatSymbols().getMonths()[(Integer.parseInt(month))];
	selectMonthYear=month+" "+year;

	  try {
		  retValue =findMonthlyEOTAmountByEmplPartyId(delegator,paryId,selectMonthYear,orgPartyId);
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	//return eotAmount.setScale(2,RoundingMode.HALF_UP);
	 return retValue;
}

/**
 * @author zzz
 * @param delegator
 * @param month
 * @param year
 * @param paryId
 * @param orgPartyId
 * @return
 */
public static  Map<String,Object> getMonthlyHolidayOvertime(Delegator delegator,String month,String year,String paryId, String orgPartyId){
	//BigDecimal monthlyHolidayOTAmount=new BigDecimal("0");
	Map<String,Object> holidayOverTimeMap=FastMap.newInstance();
	String selectMonthYear="";
	month = new DateFormatSymbols().getMonths()[(Integer.parseInt(month))];
	selectMonthYear=month+" "+year;

	  try {
		  holidayOverTimeMap=findMonthlyHolidayOvertime(delegator,paryId,selectMonthYear,orgPartyId);
		  //monthlyHolidayOTAmount =(BigDecimal)holidayOverTimeMap.get("holidayOTAmount");
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return holidayOverTimeMap;
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
 * @author zzz
 * @param delegator
 * @param partyId
 * @param selectMonthYear
 * @param orgPartyId
 * @return
 * @throws ParseException
 */
public static  Map<String, Object> findMonthlyHolidayOvertime(Delegator delegator,String partyId,String selectMonthYear,String orgPartyId)
		throws ParseException {

		BigDecimal holidayOTAmount=new BigDecimal("0");
		Map<String,Object> holidayOverTimeMap=FastMap.newInstance();
		String otCalculateOperand ="0";
		String employmentType=getEmploymentType(delegator, partyId);
		String basicSalary=getBasicSalaryByPartyId(delegator, partyId);
		GenericValue employeePref=null;
		GenericValue designPref = null;
		double numberOfDaysHolidayOTHappen=0;
        double otRate=0;
        long totalWorkingHour=0;
		if(employmentType!=null&&!employmentType.isEmpty()){

			java.sql.Date monthStart = null;
			if(selectMonthYear!=null&&!selectMonthYear.isEmpty()){

			//First date of month
			monthStart=Utils.getsqltDate(selectMonthYear);

			Calendar cal = Calendar.getInstance();
		    cal.setTime(monthStart);
		    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		    java.sql.Date monthEnd=  new java.sql.Date(cal.getTime().getTime());

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

			if(employmentType.equalsIgnoreCase("STAFF")){
				for(Timestamp holiday:holidays){
					Date day=new Date(holiday.getTime());

					try {

						GenericValue dayPref=UtilAttendancePreference.getDayPreference(delegator, day, orgPartyId);
						if(dayPref!=null&&UtilValidate.isNotEmpty(dayPref)){
							continue;
						}

						designPref = UtilAttendancePreference.getDesignationPreference(partyId, delegator, day, orgPartyId);
						 if (UtilValidate.isNotEmpty(designPref)){
						String firstHalfHolidayRate=designPref.getString("firstHalfHolidayRate");
						String secondHalfHolidayRate=designPref.getString("secondHalfHolidayRate");

						if(firstHalfHolidayRate!=null&&secondHalfHolidayRate!=null){

							long workingHour=getWorkingHour(delegator, partyId, orgPartyId, day);

							if(workingHour>=8){
								holidayOTAmount=holidayOTAmount.add(new BigDecimal(secondHalfHolidayRate));
								numberOfDaysHolidayOTHappen++;
							}else if(workingHour>=3){
								holidayOTAmount=holidayOTAmount.add(new BigDecimal(firstHalfHolidayRate));
								numberOfDaysHolidayOTHappen=numberOfDaysHolidayOTHappen+0.5;
							}			

						}
						 }
					} catch (GenericEntityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}else if(employmentType.equalsIgnoreCase("WORKERS")){

				for(Timestamp holiday:holidays){
					Date day=new Date(holiday.getTime());
					try {
						employeePref=UtilAttendancePreference.getEmployeePreference(partyId, delegator, day, orgPartyId);
						 if (UtilValidate.isNotEmpty(employeePref)){
								GenericValue dayPref=UtilAttendancePreference.getDayPreference(delegator, day, orgPartyId);
								if(dayPref!=null&&UtilValidate.isNotEmpty(dayPref)){
									continue;
								}
						String firstHalfHolidayRate=employeePref.getString("firstHalfHolidayRate");
						String secondHalfHolidayRate=employeePref.getString("secondHalfHolidayRate");

						if(firstHalfHolidayRate!=null&&secondHalfHolidayRate!=null){

							long workingHour=getWorkingHour(delegator, partyId, orgPartyId, day);
							if(workingHour>=8){
								holidayOTAmount=holidayOTAmount.add(new BigDecimal(secondHalfHolidayRate));
								numberOfDaysHolidayOTHappen++;
							}
							else if(workingHour>=3){
								holidayOTAmount=holidayOTAmount.add(new BigDecimal(firstHalfHolidayRate));
								numberOfDaysHolidayOTHappen=numberOfDaysHolidayOTHappen+0.5;
							}

						}
						 }else{

							long workingHour=getWorkingHour(delegator, partyId, orgPartyId, day);
                             if (UtilValidate.isNotEmpty(otCalculateOperand)&& UtilValidate.isNotEmpty(basicSalary)) {
                                 otRate=new Double(basicSalary)/new Double(otCalculateOperand);
                                 BigDecimal bd = new BigDecimal(otRate).setScale(2, RoundingMode.HALF_UP);
                                 otRate = bd.doubleValue();
                             }
							double otAmount=otRate*workingHour;
							holidayOTAmount=holidayOTAmount.add(new BigDecimal(otAmount)).setScale(0,RoundingMode.HALF_UP);
							totalWorkingHour=totalWorkingHour+workingHour;
						}

					} catch (GenericEntityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			}


		}else{
			return holidayOverTimeMap;
		}

		holidayOverTimeMap.put("employmentType", employmentType);
		 if (UtilValidate.isNotEmpty(designPref)){
			 String firstHalfHolidayRate=designPref.getString("firstHalfHolidayRate");
			 String secondHalfHolidayRate=designPref.getString("secondHalfHolidayRate");
			 try{
				 holidayOverTimeMap.put("holidayRate", Integer.parseInt(firstHalfHolidayRate)+Integer.parseInt(secondHalfHolidayRate));
			 }catch(Exception e){
				 holidayOverTimeMap.put("holidayRate", 0); 
			 }
		 }
		 if (UtilValidate.isNotEmpty(employeePref)){
			 String firstHalfHolidayRate=employeePref.getString("firstHalfHolidayRate");
			 String secondHalfHolidayRate=employeePref.getString("secondHalfHolidayRate");
			 try{
				 holidayOverTimeMap.put("holidayRate", Integer.parseInt(firstHalfHolidayRate)+Integer.parseInt(secondHalfHolidayRate));
			 }catch(Exception e){
				 holidayOverTimeMap.put("holidayRate", 0); 
			 }
		 }
		 holidayOverTimeMap.put("numberOfDaysHolidayOTHappen", numberOfDaysHolidayOTHappen);
		 holidayOverTimeMap.put("basicSalary", basicSalary);
		 holidayOverTimeMap.put("perHrsTake",otRate);
		 holidayOverTimeMap.put("totalHours", totalWorkingHour);
		 holidayOverTimeMap.put("holidayOTAmount", holidayOTAmount);
	return holidayOverTimeMap;
}

/**
 * @author zzz
 * @param delegator
 * @param dayPref
 * @param day
 * @param partyId
 * @return
 * @throws GenericEntityException
 */
public static long getWorkingHour(Delegator delegator,GenericValue dayPref,Date day,String partyId) throws GenericEntityException{
	String exitTime = dayPref.getString("exitTime");
	String oTStartTime=dayPref.getString("oTStartTime");
	Timestamp dayStart = Utils.getModifiedDayStart(new Timestamp(day.getTime()));

	Timestamp exitTimeInDate = Utils.getStringTiffinTimeToTimStamp(exitTime,day);
	//Timestamp dayEnd =Utils.addHourToTimestamp(eoTEndTimeInDate, 1);
	Timestamp dayEnd =exitTimeInDate;
	List<GenericValue> employeeLogs = null;
	List<EntityExpr> exprs = FastList.newInstance();
	List<String> orderBy = FastList.newInstance();
	orderBy.add("logtimeStamp ASC");
	exprs.add(EntityCondition.makeCondition("logtimeStamp",EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
	exprs.add(EntityCondition.makeCondition("logtimeStamp",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
	exprs.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS, partyId));
	employeeLogs = delegator.findList("EmployeeLog",EntityCondition.makeCondition(exprs,EntityOperator.AND), null, orderBy, null,true);


	if (employeeLogs != null && employeeLogs.size() > 1) {
		GenericValue inLog = employeeLogs.get(0);
		Timestamp entryLog = (Timestamp) inLog.get("logtimeStamp");

		GenericValue outLog = employeeLogs.get(employeeLogs.size() - 1);
		Timestamp exitLog = (Timestamp) outLog.get("logtimeStamp");
		long workingHour= UtilDateTime.getHourInterval(entryLog, exitLog);
		return workingHour;

	}
	return 0;
}

/**
 * @author zzz
 * @param delegator
 * @param partyId
 * @param orgPartyId
 * @param day
 * @return
 * @throws GenericEntityException
 */
public static long getWorkingHour(Delegator delegator,String partyId,String orgPartyId,Date day) throws GenericEntityException{
	String exitTime = UtilAttendancePreference.getPreferencePropertyValue(
			partyId,
			delegator,
			day,
			orgPartyId,
			UtilPreferenceProperties.EXITTIME);
	String eoTEndTime = UtilAttendancePreference.getPreferencePropertyValue(
			partyId, delegator,
			day, orgPartyId,
			UtilPreferenceProperties.EXTRAOTENDTIME);
	
	String lunchStartTime = UtilAttendancePreference.getPreferencePropertyValue(
			partyId, delegator,
			day, orgPartyId,
			UtilPreferenceProperties.LUNCHSTARTTIME);
	
	String lunchEndTime = UtilAttendancePreference.getPreferencePropertyValue(
			partyId, delegator,
			day, orgPartyId,
			UtilPreferenceProperties.LUNCHENDTIME);
	String lunchduration = UtilAttendancePreference.getPreferencePropertyValue(
			partyId, delegator,
			day, orgPartyId,
			UtilPreferenceProperties.LUNCHDURATION);
	
	GenericValue dayPref=UtilAttendancePreference.getDayPreference(delegator, day, orgPartyId);
	Timestamp dayStart = Utils.getModifiedDayStart(new Timestamp(day.getTime()));
	Timestamp dayEnd = Utils.getModifiedDayEnd(new Timestamp(day.getTime()));
	Timestamp OtStartTimeInDate=null;
	Timestamp OtEndTimeInDate=null;
	//Timestamp dayEnd =Utils.addHourToTimestamp(eoTEndTimeInDate, 1);
	if(UtilValidate.isNotEmpty(dayPref)){
		String OtStartTime=(String)dayPref.get("oTStartTime");
		OtStartTimeInDate = Utils.getStringTiffinTimeToTimStamp(OtStartTime,day);
		String OtEndTime=(String)dayPref.get("oTEndTime");
		OtEndTimeInDate = Utils.getStringTiffinTimeToTimStamp(OtEndTime,day);
	}

	//Timestamp dayEnd =Utils.addHourToTimestamp(eoTEndTimeInDate, 1);
	if(eoTEndTime!=null){
		Timestamp eoTEndTimeInDate = Utils.getStringTiffinTimeToTimStamp(eoTEndTime,day);
		dayEnd =eoTEndTimeInDate;
	}

	List<GenericValue> employeeLogs = null;
	List<EntityExpr> exprs = FastList.newInstance();
	List<String> orderBy = FastList.newInstance();
	orderBy.add("logtimeStamp ASC");
	exprs.add(EntityCondition.makeCondition("logtimeStamp",EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
	exprs.add(EntityCondition.makeCondition("logtimeStamp",EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
	exprs.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS, partyId));
	employeeLogs = delegator.findList("EmployeeLog",EntityCondition.makeCondition(exprs,EntityOperator.AND), null, orderBy, null,true);
	if (employeeLogs != null && employeeLogs.size() > 1) {
		GenericValue inLog = employeeLogs.get(0);
		Timestamp entryLog = (Timestamp) inLog.get("logtimeStamp");

		GenericValue outLog = employeeLogs.get(employeeLogs.size() - 1);
		Timestamp exitLog = (Timestamp) outLog.get("logtimeStamp");
		
		long workingHour= 0;

		if(UtilValidate.isNotEmpty(dayPref)){
			long exitTimeInLong=exitLog.getTime();
			long oTEndTimeInDate=OtEndTimeInDate.getTime();
			if(exitLog.getTime()>oTEndTimeInDate){
				exitTimeInLong=oTEndTimeInDate;
				//otTime=exitTimeInLong-oTStartTimeInDate;
			}
			long otTime=exitTimeInLong-OtStartTimeInDate.getTime();
			HashMap<String, Integer> otTimeInHashMap=UtilDateTime.covertLongToHourMinuteSecond(otTime);
				workingHour=otTimeInHashMap.get("hour");
			int otMinute=otTimeInHashMap.get("minute");
			if(otMinute>50){
				workingHour=workingHour+1;
			}
		}else{
		workingHour=UtilDateTime.getHourInterval(entryLog, exitLog);
		if(UtilValidate.isNotEmpty(lunchStartTime) && UtilValidate.isNotEmpty(lunchEndTime)){
			Timestamp lunchTimeInDate = Utils.getStringTiffinTimeToTimStamp(lunchStartTime,day);
			Timestamp lunchEndTimeInDate = Utils.getStringTiffinTimeToTimStamp(lunchEndTime,day);
			if(lunchTimeInDate.getTime()>entryLog.getTime() && exitLog.getTime()>lunchEndTimeInDate.getTime()){
				if(UtilValidate.isNotEmpty(lunchduration)){
				workingHour=workingHour-Integer.parseInt(lunchduration);
				}else{
					long lunchDuration=lunchEndTimeInDate.getTime()-lunchTimeInDate.getTime();
					int lunchDurationInHour=(int)lunchDuration/(3600*1000);
					workingHour=workingHour-lunchDurationInHour;
				}
			}
		}
		}
		if(workingHour<0){
			workingHour=0;
		}
		return workingHour;

	}
	return 0;

}

/**
 * @author zzz
 * @param delegator
 * @param orgPartyId
 * @param partyId
 * @param selectMonthYear  String; January 2014
 * @return bonus within the specified month and year.
 * */

public static  Map<String,Object> findMonthlyEOTAmountByEmplPartyId(Delegator delegator,String partyId,String selectMonthYear,String orgPartyId)
		throws ParseException {
		Map<String,Object> retValue=FastMap.newInstance();
		double eotAmount=0.0;
		String otCalculateOperand ="0";
		java.sql.Date monthStart = null;

		if(selectMonthYear!=null&&!selectMonthYear.isEmpty()){

		//First date of month
		monthStart=Utils.getsqltDate(selectMonthYear);

		Calendar cal = Calendar.getInstance();
	    cal.setTime(monthStart);
	    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
	    java.sql.Date monthEnd=  new java.sql.Date(cal.getTime().getTime());
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
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(monthStart);
				calendar.add(Calendar.DATE, 1);
				monthStart = new Date(calendar.getTime().getTime());

			}
			BigDecimal eotRate=BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
			if(eotHours>0){
				String basicSalary=	getBasicSalaryByPartyId(delegator, partyId);
                if (UtilValidate.isNotEmpty(basicSalary)&&UtilValidate.isNotEmpty(otCalculateOperand)) {
                	eotRate=BigDecimal.valueOf((Double)(Double.valueOf(basicSalary)/Double.valueOf(otCalculateOperand)));
                    eotAmount=eotRate.doubleValue()*eotHours;
                }
            }
			retValue.put("eotHours", eotHours);
			retValue.put("eotRate", eotRate);
			retValue.put("eotAmount", new BigDecimal(Math.round(eotAmount)));

		}
		return retValue;
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
	   int totalHalfDayLeave=0;

	java.sql.Date monthYearInDate=null;

	try {

		if(selectMonthYear!=null&&!selectMonthYear.isEmpty()){

			//First date of month
			monthYearInDate=Utils.getsqltDate(selectMonthYear);
			Calendar cal = Calendar.getInstance();
            cal.setTime(monthYearInDate);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            java.sql.Date monthLastDate=  new java.sql.Date(cal.getTime().getTime());


            //Get holidays of selected month
        	List<Timestamp> holidays=new ArrayList<Timestamp>();
			holidays=Utils.getHolidays(orgPartyId, new Timestamp(monthYearInDate.getTime()), new Timestamp(monthLastDate.getTime()), delegator);

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
			 
			 totalHalfDayLeave=getTotalHalfDayLeave(delegator,orgPartyId,partyId, new Timestamp(monthYearInDate.getTime()), new Timestamp(monthLastDate.getTime()));
			//Get selected month employee bonus from total absence and late entry
			 bonus= Utils.getMonthlyBonus(monthYearInDate,monthlogs,totalAbsence,partyId,orgPartyId,delegator,totalHalfDayLeave);


		}} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	return bonus;
}

/**
 * @param Delegator delegator, String orgPartyId, String partyId, String fromDate, String toDate
 * @return int total numver of half day Leave
 * @author zzz
 * 
**/
public static int getTotalHalfDayLeave(Delegator delegator,String orgPartyId,String partyId,Timestamp dayStart,Timestamp dayEnd){
		
	DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
	List<EntityExpr> exprsList = FastList.newInstance();
	List<GenericValue> HalfDayLeaves = FastList.newInstance();
	
	if(dayStart!=null&&dayEnd!=null){
		
		exprsList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId.trim()));
		exprsList.add(EntityCondition.makeCondition("leaveStatus",EntityOperator.EQUALS,"LEAVE_APPROVED"));
		exprsList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayStart));
		exprsList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));			
		exprsList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("leaveDuration",EntityOperator.EQUALS, "half_Day_Morning"),
				      EntityOperator.OR, EntityCondition.makeCondition("leaveDuration", EntityOperator.EQUALS,"half_Day_Evening")));
				
	
	try {
		HalfDayLeaves = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsList,EntityOperator.AND),null, null, null, false);
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
	}
	
	return HalfDayLeaves.size();
}

		public static Map<String,Double> getTotalAbsent(Delegator delegator,String orgPartyId,String partyId,Timestamp startTime,Timestamp endTime){


			double totalFulDayAbsent = 0.0;
			double totalHalfDayAbsent= 0.0;

			Map<String,Double> absent = FastMap.newInstance();
			absent.put("totalFulDayAbsent", totalFulDayAbsent);
			absent.put("totalHalfDayAbsent", totalHalfDayAbsent);
			List<EntityExpr> exprsList = FastList.newInstance();
			exprsList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId.trim()));
			exprsList.add(EntityCondition.makeCondition("leaveStatus",EntityOperator.EQUALS,"LEAVE_APPROVED"));
			List<GenericValue> employeeLeaves=new ArrayList<GenericValue>();
			try{
				employeeLeaves = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsList, EntityOperator.AND), null, null, null, false);
			    }catch(Exception e){
			    	//logger.debug("Exception to get leave in findDailyAbsenceReport function---->"+e);
			 }
			while(startTime.getTime()<=endTime.getTime()){
				startTime = UtilDateTime.addDaysToTimestamp(startTime, 1);

				Timestamp dayStart=Utils.getModifiedDayStart(startTime);
				Timestamp dayEnd=Utils.getModifiedDayEnd(startTime);

				List<Timestamp> holidays=Attendance.getHolidays(orgPartyId, dayStart, dayEnd, delegator);
		//		check the day is holiday or not?
				if(holidays==null||holidays.size()<=0){
					boolean checkLeaveStatus=Attendance.checkEmpLeave(employeeLeaves,startTime.getTime());
		//			check is he in leave or not?
					if(!checkLeaveStatus){
						List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
						List<EntityExpr> exprs = FastList.newInstance();
						List<String> orderBy = FastList.newInstance();
						orderBy.add("logtimeStamp ASC");
						exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
						exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
						exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));

						try {
							employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);

							if(employeeLogs.size()<=0){
								totalFulDayAbsent+=1;
							}

						} catch (GenericEntityException e) {
							e.printStackTrace();
						}


					}else{
						if(UtilValidate.isNotEmpty(employeeLeaves)){
							String leaveDuration = employeeLeaves.get(0).getString("leaveDuration");
                            if (UtilValidate.isNotEmpty(leaveDuration)) {
                                if (leaveDuration.equals("half_Day_Evening") || leaveDuration.equals("half_Day_Morning")) {
                                    totalHalfDayAbsent += 0.5;
                                }
                            }
                        }
					}
				}
		}
		absent.put("totalFulDayAbsent", totalFulDayAbsent);
		absent.put("totalHalfDayAbsent",totalHalfDayAbsent);
		return absent;
		}


		public static int getEmployeeOt(Delegator delegator,String orgPartyId,String partyId,Date date,Timestamp exitTime) throws GenericEntityException{
			int otHour=0;
			String oTStartTime=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date, orgPartyId, UtilPreferenceProperties.OTSTARTTIME);
			String oTEndTime=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date, orgPartyId, UtilPreferenceProperties.OTENDTIME);
			String hasOTAllounce=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date, orgPartyId, UtilPreferenceProperties.OTALLOWANCES);
			if(hasOTAllounce.equals("Y")){
			long otTime=0;
			long oTStartTimeInDate=Utils.convertStringTimeToLong(oTStartTime,date);
			long oTEndTimeInDate=Utils.convertStringTimeToLong(oTEndTime, date);
			long exitTimeInLong=exitTime.getTime();
			if(exitTimeInLong>oTStartTimeInDate && oTStartTimeInDate<oTEndTimeInDate){
				otTime=exitTimeInLong-oTStartTimeInDate;
			}else if(exitTimeInLong>oTEndTimeInDate){
				otTime=oTStartTimeInDate-oTStartTimeInDate;
			}
			HashMap<String, Integer> otTimeInHashMap=UtilDateTime.covertLongToHourMinuteSecond(otTime);
				otHour=otTimeInHashMap.get("hour");
			int otMinute=otTimeInHashMap.get("minute");
			if(otMinute>50){
				otHour=otHour+1;
			}
			}
			return otHour;
		}
		public static double getOverTimeAmount(Delegator delegator,Timestamp fromDate,Timestamp toDate,String orgPartyId,String partyId){
			double retValue=0.0;
			Timestamp fromTime=Utils.getModifiedDayStart(fromDate);
			Timestamp toTime=Utils.getModifiedDayStart(toDate);
			long stime=fromTime.getTime();
			long etime=toTime.getTime();
			double otHour=0;
			while(stime<=etime){
					Map<String, String> employeeLog=	FastMap.newInstance();
					List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();

					Timestamp dayStart=new Timestamp(stime);
					long dayEndInLong=Utils.getEndTime(new java.sql.Date(stime));
					Timestamp dayEnd=new Timestamp(dayEndInLong);
					List<EntityExpr> exprs = FastList.newInstance();
					List<String> orderBy = FastList.newInstance();
					orderBy.add("logtimeStamp ASC");
					exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO,dayStart));
					exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
					exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
					try {
						employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);

					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
					if(employeeLogs.size()>=1){
						Timestamp	outtime=null;
						Timestamp	entrytime=null;
						if(employeeLogs.size()>1){
						GenericValue outemployeeLog=employeeLogs.get(employeeLogs.size()-1);
						outtime=(Timestamp)outemployeeLog.get("logtimeStamp");
						GenericValue employeeLogEntrry=employeeLogs.get(0);
						entrytime=(Timestamp)employeeLogEntrry.get("logtimeStamp");
						}
						if(outtime!=null){
							try {
								otHour=otHour+Attendance.getEmployeeOt(delegator, orgPartyId, partyId, new Date(stime), outtime);
							} catch (GenericEntityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					stime=stime+86400000;
			}
			return otHour;
		}
		
		
		public static  Map<String, Object> findHolidayOvertime(Delegator delegator,String partyId,String employmentType,java.sql.Date fromDate,java.sql.Date toDate,String orgPartyId)
				throws ParseException {
				long startTime=System.currentTimeMillis();
				BigDecimal holidayOTAmount=new BigDecimal("0");
				Map<String,Object> holidayOverTimeMap=FastMap.newInstance();
				String otCalculateOperand ="0";
				String basicSalary=getBasicSalaryByPartyId(delegator, partyId);
				GenericValue employeePref=null;
				GenericValue designPref = null;
				double numberOfDaysHolidayOTHappen=0;
		        double otRate=0;
		        long totalWorkingHour=0;
				if(employmentType!=null&&!employmentType.isEmpty()){

					if(fromDate!=null){


				    //Get holidays of selected month
					List<Timestamp> holidays=new ArrayList<Timestamp>();
					holidays=Utils.getHolidays(orgPartyId, new Timestamp(fromDate.getTime()), new Timestamp(toDate.getTime()), delegator);
					try {
						 otCalculateOperand = UtilAttendancePreference.getPreferencePropertyValue(
								partyId,
								delegator,
								toDate,
								orgPartyId,
								UtilPreferenceProperties.OTCALCULATIONOPERAND);
					} catch (GenericEntityException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					if(employmentType.equalsIgnoreCase("STAFF")){
						for(Timestamp holiday:holidays){
							Date day=new Date(holiday.getTime());

							try {

								GenericValue dayPref=UtilAttendancePreference.getDayPreference(delegator, day, orgPartyId);
								if(dayPref!=null&&UtilValidate.isNotEmpty(dayPref)){
									continue;
								}

								designPref = UtilAttendancePreference.getDesignationPreference(partyId, delegator, day, orgPartyId);
								 if (UtilValidate.isNotEmpty(designPref)){
								String firstHalfHolidayRate=designPref.getString("firstHalfHolidayRate");
								String secondHalfHolidayRate=designPref.getString("secondHalfHolidayRate");

								if(firstHalfHolidayRate!=null&&secondHalfHolidayRate!=null){

									long workingHour=getWorkingHour(delegator, partyId, orgPartyId, day);

									if(workingHour>=8){
										holidayOTAmount=holidayOTAmount.add(new BigDecimal(secondHalfHolidayRate)).add(new BigDecimal(firstHalfHolidayRate));
										numberOfDaysHolidayOTHappen++;
									}else if(workingHour>=3){
										holidayOTAmount=holidayOTAmount.add(new BigDecimal(firstHalfHolidayRate));
										numberOfDaysHolidayOTHappen=numberOfDaysHolidayOTHappen+0.5;
									}

								}
								 }
							} catch (GenericEntityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

					}else if(employmentType.equalsIgnoreCase("WORKERS")){

						for(Timestamp holiday:holidays){
							Date day=new Date(holiday.getTime());
							try {
								employeePref=UtilAttendancePreference.getEmployeePreference(partyId, delegator, day, orgPartyId);
								 if (UtilValidate.isNotEmpty(employeePref)){
										GenericValue dayPref=UtilAttendancePreference.getDayPreference(delegator, day, orgPartyId);
										if(dayPref!=null&&UtilValidate.isNotEmpty(dayPref)){
											continue;
										}
								String firstHalfHolidayRate=employeePref.getString("firstHalfHolidayRate");
								String secondHalfHolidayRate=employeePref.getString("secondHalfHolidayRate");

								if(firstHalfHolidayRate!=null&&secondHalfHolidayRate!=null){

									long workingHour=getWorkingHour(delegator, partyId, orgPartyId, day);
									if(workingHour>=8){
										holidayOTAmount=holidayOTAmount.add(new BigDecimal(secondHalfHolidayRate));
										numberOfDaysHolidayOTHappen++;
									}
									else if(workingHour>=3){
										holidayOTAmount=holidayOTAmount.add(new BigDecimal(firstHalfHolidayRate));
										numberOfDaysHolidayOTHappen=numberOfDaysHolidayOTHappen+0.5;
									}

								}
								 }else{

									long workingHour=getWorkingHour(delegator, partyId, orgPartyId, day);
		                             if (UtilValidate.isNotEmpty(otCalculateOperand)&& UtilValidate.isNotEmpty(basicSalary)) {
		                                 otRate=new Double(basicSalary)/new Double(otCalculateOperand);
		                                 BigDecimal bd = new BigDecimal(otRate).setScale(2, RoundingMode.HALF_UP);
		                                 otRate = bd.doubleValue();
		                             }
									double otAmount=otRate*workingHour;
									holidayOTAmount=holidayOTAmount.add(new BigDecimal(otAmount)).setScale(0,RoundingMode.HALF_UP);
									totalWorkingHour=totalWorkingHour+workingHour;
								}

							} catch (GenericEntityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					}


				}else{
					return holidayOverTimeMap;
				}

				holidayOverTimeMap.put("employmentType", employmentType);
				 if (UtilValidate.isNotEmpty(designPref)){
					 String firstHalfHolidayRate=designPref.getString("firstHalfHolidayRate");
					 String secondHalfHolidayRate=designPref.getString("secondHalfHolidayRate");
					 int firstHolyRate=0;
					 int secondHoliyRate=0;
					if( UtilValidate.isNotEmpty(firstHalfHolidayRate)|| firstHalfHolidayRate!=null){
						 firstHolyRate = Integer.parseInt(firstHalfHolidayRate);
					 }
					if( UtilValidate.isNotEmpty(secondHalfHolidayRate) || secondHalfHolidayRate!=null){
						 secondHoliyRate = Integer.parseInt(secondHalfHolidayRate);
					 }
					 try{
						 holidayOverTimeMap.put("holidayRate", secondHoliyRate+firstHolyRate);
					 }catch(Exception e){
						 holidayOverTimeMap.put("holidayRate", 0); 
					 }
				 }
				 if (UtilValidate.isNotEmpty(employeePref)){
					 String firstHalfHolidayRate=employeePref.getString("firstHalfHolidayRate");
					 String secondHalfHolidayRate=employeePref.getString("secondHalfHolidayRate");
					 int firstHolyRate=0;
					 int secondHoliyRate=0;
					if( UtilValidate.isNotEmpty(firstHalfHolidayRate)|| firstHalfHolidayRate!=null){
						 firstHolyRate = Integer.parseInt(firstHalfHolidayRate);
					 }
					if(UtilValidate.isNotEmpty(secondHalfHolidayRate) || secondHalfHolidayRate!=null){
						 secondHoliyRate = Integer.parseInt(secondHalfHolidayRate);
					 }
					 try{
						 holidayOverTimeMap.put("holidayRate", firstHolyRate + secondHoliyRate);
					 }catch(Exception e){
						 holidayOverTimeMap.put("holidayRate", 0); 
					 }
				 }
				 holidayOverTimeMap.put("numberOfDaysHolidayOTHappen", numberOfDaysHolidayOTHappen);
				 holidayOverTimeMap.put("basicSalary", basicSalary);
				 holidayOverTimeMap.put("perHrsTake",otRate);
				 holidayOverTimeMap.put("totalHours", totalWorkingHour);
				 holidayOverTimeMap.put("holidayOTAmount", holidayOTAmount);
				 long endTime=System.currentTimeMillis();
				 logger.debug("Need Total Time------->"+(endTime-startTime)/1000);
			return holidayOverTimeMap;
		}


}
