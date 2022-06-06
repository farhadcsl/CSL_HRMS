package org.ofbiz.webapp.contessa;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.apache.log4j.Logger;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

/**
 * Date: 9/15/14
 * Time: 5:20 PM
 */
public class SalaryWorker {
	private static Logger logger=Logger.getLogger("SalaryWorker");
	private static final String defaultGlAccountId1=UtilProperties.getPropertyValue("contessa.properties", "basicSalary.defaultGlAccountId1");
	private static final String defaultGlAccountId2=UtilProperties.getPropertyValue("contessa.properties", "basicSalary.defaultGlAccountId2");
    public static BigDecimal getMonthlyPayrollBenefitsOnly(Delegator delegator, String monthNumber, String year, String companyId, boolean isBonus) {
        BigDecimal payrollBenefit = BigDecimal.ZERO;

        EntityFindOptions findOptions = new EntityFindOptions();
        findOptions.setDistinct(true);

        List<EntityCondition> orConditionList = FastList.newInstance();
        List<EntityCondition> conditionList = FastList.newInstance();
        conditionList.add(EntityCondition.makeCondition("invoiceTypeId", "PAYROL_INVOICE"));
        conditionList.add(EntityCondition.makeCondition("partyId", companyId));
        conditionList.add(EntityCondition.makeCondition("month", monthNumber));
        conditionList.add(EntityCondition.makeCondition("fiscalYear", year));

        orConditionList.add(EntityCondition.makeCondition("statusId", "INVOICE_READY"));
        orConditionList.add(EntityCondition.makeCondition("statusId", "INVOICE_PAID"));

        EntityCondition orCond = EntityCondition.makeCondition(conditionList, EntityOperator.OR);


        EntityCondition andCond = EntityCondition.makeCondition(conditionList);
        EntityCondition cond = EntityCondition.makeCondition(orCond, EntityOperator.AND, andCond);
        List<GenericValue> employeeList = null;

        try {
            employeeList = delegator.findList("Invoice", cond, null, UtilMisc.toList("partyIdFrom"), findOptions, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }


        for (GenericValue employeeValue : employeeList) {
            String employeePartyId = null;
            if (UtilValidate.isNotEmpty(employeeValue)) {
                employeePartyId = employeeValue.getString("partyIdFrom");
            }
            List<GenericValue> singleEmployeeBenefitList = getAnEmployeeBenefitInvoiceFromDatabase(delegator, employeePartyId, monthNumber, year);

            for (GenericValue employeeBenefit : singleEmployeeBenefitList) {


                payrollBenefit = payrollBenefit.add(new BigDecimal(employeeBenefit.getString("amount")));


            }


        }
        return payrollBenefit;
    }


    public static List<GenericValue> getAnEmployeeBenefitInvoiceFromDatabase(Delegator delegator, String partyId, String monthNumber, String fiscalYear) {
        List<GenericValue> invoice = null;
        try {
            invoice = delegator.findByAnd("InvoiceAndInvoiceItem", UtilMisc.toMap("partyIdFrom", partyId, "month", monthNumber, "fiscalYear", fiscalYear, "statusId", "INVOICE_READY"));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return invoice;
    }



	public static BigDecimal getMonthlyAbsentByPartyId(Delegator delegator,String orgPartyId,String month,String year,String partyId){
		BigDecimal deductionAmount=null;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR,Integer.parseInt(year));
		cal.set(Calendar.MONTH, Integer.parseInt(month));
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	    Timestamp startDateOfMonth = new Timestamp(cal.getTimeInMillis());
	    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
	    int daysOfMonth=cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	    Timestamp lastDateOfMonth=new Timestamp(cal.getTimeInMillis());
	    Map<String,Double> absentMap=Attendance.getTotalAbsent(delegator, orgPartyId, partyId, startDateOfMonth, lastDateOfMonth);
	    double fullAbsent=absentMap.get("totalFulDayAbsent");
	    double halfDayAbsent=absentMap.get("totalHalfDayAbsent");
        String basicSalaryString = Attendance.getBasicSalaryByPartyId(delegator, partyId);
        double basicSalary = 0.0;
        if (UtilValidate.isNotEmpty(basicSalaryString)) {
            basicSalary = Double.parseDouble(basicSalaryString);
        }
        deductionAmount = deductionAmount.valueOf((basicSalary/daysOfMonth)*(fullAbsent+(halfDayAbsent/2)));
		return deductionAmount;

	}
	public static Map<String,Object> getOverTimeAmountByPartyId(Delegator delegator,String orgPartyId,String month,String year,String partyId) {
		Map<String,Object> retMap=FastMap.newInstance();
		BigDecimal getOverTimeAmount=null;
		String otCalculationOperand=null;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR,Integer.parseInt(year));
		cal.set(Calendar.MONTH, Integer.parseInt(month));
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	    Timestamp startDateOfMonth = new Timestamp(cal.getTimeInMillis());
	    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
	    int daysOfMonth=cal.getActualMaximum(Calendar.DAY_OF_MONTH);

	    Timestamp lastDateOfMonth=new Timestamp(cal.getTimeInMillis());
//		double basicSalary=Double.parseDouble(Attendance.getBasicSalaryByPartyId(delegator, partyId));
        double basicSalary=0;
        String basicSalaryString = Attendance.getBasicSalaryByPartyId(delegator, partyId);
        if (UtilValidate.isNotEmpty(basicSalaryString)) {
            basicSalary = Double.parseDouble(basicSalaryString);
        }

		try {
			otCalculationOperand=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, new Date(System.currentTimeMillis()), orgPartyId, UtilPreferenceProperties.OTCALCULATIONOPERAND);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double otHour=Attendance.getOverTimeAmount(delegator, startDateOfMonth, lastDateOfMonth, orgPartyId, partyId);
        double rate = 0;
        if (basicSalary != 0 && UtilValidate.isNotEmpty(otCalculationOperand)) {
            rate=basicSalary/Double.parseDouble(otCalculationOperand);
            BigDecimal bd = new BigDecimal(rate).setScale(2, RoundingMode.HALF_UP);
            rate = bd.doubleValue();
        }
		if(otCalculationOperand!=null && otCalculationOperand.length()>0){
            getOverTimeAmount= getOverTimeAmount.valueOf(rate * otHour).setScale(2, RoundingMode.HALF_UP);
		}
		retMap.put("ratePerHour", rate);
		retMap.put("totalHour", otHour);
		retMap.put("totalHourAmount", getOverTimeAmount);
		return retMap;
	}

	public static BigDecimal countLeaveByMonthAndLeaveType(Delegator delegator, String companyId, String partyId, String leaveTypeId, String month, String year){


		if (UtilValidate.isEmpty(companyId)){
			List<GenericValue> employees = null;
			try {
				employees = delegator.findList("EmploymentAndPerson", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null, null, null, true);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			companyId = employees!=null?(employees.size()>0?employees.get(0).get("partyIdFrom").toString():""):"";
		}


		java.sql.Timestamp leaveStartDate = UtilDateTime.toTimestamp((Integer.parseInt(month)+1)+"", "1", year, "0", "0", "0");
		java.sql.Timestamp leaveEndDate = UtilDateTime.toTimestamp((Integer.parseInt(month)+2)+"", "1", year, "0", "0", "0");

		 List<EntityExpr> exprs = FastList.newInstance();
		 exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		 exprs.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
		 exprs.add(EntityCondition.makeCondition("leaveTypeId", EntityOperator.EQUALS, leaveTypeId));
		 exprs.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, leaveStartDate));
		 exprs.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN, leaveEndDate));

		List<GenericValue> employeeLeaves = null;
		try {
			employeeLeaves = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}

		double finalApprovedDay = 0;

		for (GenericValue oGenericValue: employeeLeaves){
			int noOfDaysApproved = 0;
			Timestamp stopDate = (Timestamp)oGenericValue.get("thruDate");
			Timestamp startDate = (Timestamp)oGenericValue.get("fromDate");
			String leaveDuration = (String)oGenericValue.get("leaveDuration");

			noOfDaysApproved = UtilDateTime.getIntervalInDays(startDate,stopDate);

			int holidays = 0;
			if (!leaveTypeId.equals(LeaveType.LEAVE_TYPE_EARN_LEAVE)){ // holiday will count except earn leave as per babylon policy
				holidays = getHolidayCount(delegator, companyId, startDate, stopDate);
			}


			if(!leaveDuration.equals(LeaveType.LEAVE_DURATION_FULL_DAY_LEAVE)){
				finalApprovedDay = finalApprovedDay + .5;
			}else{
				finalApprovedDay = finalApprovedDay + noOfDaysApproved + 1 - holidays;
			}

		}


		return new BigDecimal(finalApprovedDay);

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


	public static BigDecimal getDeductionAmountForUnpaidLeave(Delegator delegator, String companyId, String partyId, String month, String year){

		BigDecimal deductionAmount = new BigDecimal(0);


		BigDecimal unpaidLeave = countLeaveByMonthAndLeaveType(delegator, companyId, partyId, LeaveType.LEAVE_TYPE_LEAVE_WITHOUT_PAY, month, year);


		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR,Integer.parseInt(year));
		cal.set(Calendar.MONTH, Integer.parseInt(month));

		int daysOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		double basicSalary = 0;
		try{
			basicSalary = Double.parseDouble(Attendance.getBasicSalaryByPartyId(delegator, partyId));
		}catch(Exception e){}


		deductionAmount = new BigDecimal((basicSalary/daysOfMonth) * unpaidLeave.doubleValue());

		return deductionAmount;

	}
    public static String convertDateToBangla(String doubleValue){
        String retval="";
        if(doubleValue!=null){
            for(int i=0;i<doubleValue.length();i++){
                for(int j=0;j<10;j++){
                    Character chaa=doubleValue.charAt(i);

                    if(chaa.toString().equals("0")){
                        retval=retval+"০";
                        break;
                    }else if(chaa.toString().equals("1")){
                        retval=retval+"১";
                        break;
                    }else if(chaa.toString().equals("2")){
                        retval=retval+"২";
                        break;
                    }else if(chaa.toString().equals("3")){
                        retval=retval+"৩";
                        break;
                    }else if(chaa.toString().equals("4")){
                        retval=retval+"৪";
                        break;
                    }else if(chaa.toString().equals("5")){
                        retval=retval+"৫";
                        break;
                    }else if(chaa.toString().equals("6")){
                        retval=retval+"৬";
                        break;
                    }else if(chaa.toString().equals("7")){
                        retval=retval+"৭";
                        break;
                    }else if(chaa.toString().equals("8")){
                        retval=retval+"৮";
                        break;
                    }else if(chaa.toString().equals("9")){
                        retval=retval+"৯";
                        break;
                    }else if(chaa.toString().equals("/")){
                        retval=retval+"/";
                        break;
                    }
                }
            }
        }
        return retval;
    }

    public static String getMonthBanglaName(String monthNumber) {
        String monthName = "";
        if (monthNumber.equals("0")) monthName = "জানুয়ারী";
        if (monthNumber.equals("1")) monthName = "ফেব্রুয়ারী";
        if (monthNumber.equals("2")) monthName = "মার্চ";
        if (monthNumber.equals("3")) monthName = "এপ্রিল";
        if (monthNumber.equals("4")) monthName = "মে";
        if (monthNumber.equals("5")) monthName = "জুন";
        if (monthNumber.equals("6")) monthName = "জুলাই";
        if (monthNumber.equals("7")) monthName = "আগস্ট";
        if (monthNumber.equals("8")) monthName = "সেপ্টেম্বর";
        if (monthNumber.equals("9")) monthName = "অক্টোবর";
        if (monthNumber.equals("10")) monthName = "নভেম্বর";
        if (monthNumber.equals("11")) monthName = "ডিসেম্বর";

        return monthName;
    }
    public static String getMonthEnglishName(String monthNumber) {
        String monthName = "";
        if (monthNumber.equals("0")) monthName = "January";
        if (monthNumber.equals("1")) monthName = "February";
        if (monthNumber.equals("2")) monthName = "March";
        if (monthNumber.equals("3")) monthName = "April";
        if (monthNumber.equals("4")) monthName = "May";
        if (monthNumber.equals("5")) monthName = "June";
        if (monthNumber.equals("6")) monthName = "July";
        if (monthNumber.equals("7")) monthName = "August";
        if (monthNumber.equals("8")) monthName = "September";
        if (monthNumber.equals("9")) monthName = "October";
        if (monthNumber.equals("10")) monthName = "November";
        if (monthNumber.equals("11")) monthName = "December";

        return monthName;
    }

    public static Map<String, Object> getJobCardInfo(Delegator delegator,String orgPartyId,String partyId,String monthNumber,String year){
		double summeryWorkDay=0;
		double summeryAbsentDay=0;
		double summeryLeaveDay=0;
		int summeryHoliDay=0;
		int summeryWeeklyDay=0;
		int summeryFestivalDay=0;
		int summeryOtherHoliDay=0;
		int summeryTotalHalfDay=0;
		int summeryTotalHalfDayAbsent=0;
		int summeryTotalOtInHour=0;
		int summeryOfHalfDayLeave=0;
		BigDecimal getOverTimeAmount=null;
		String otCalculationOperand=null;
		BigDecimal deductionAmount=null;

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR,Integer.parseInt(year));
		cal.set(Calendar.MONTH, Integer.parseInt(monthNumber));
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	    Timestamp startDateOfMonth = new Timestamp(cal.getTimeInMillis());
	    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
	    int daysOfMonth=cal.getActualMaximum(Calendar.DAY_OF_MONTH);

	    Timestamp lastDateOfMonth=new Timestamp(cal.getTimeInMillis());
		Map<String, Object> result = ServiceUtil.returnSuccess();
		java.sql.Date fromTime=new Date(startDateOfMonth.getTime());
		java.sql.Date toTime=new Date(lastDateOfMonth.getTime());
		List<Map> employeeLogList=FastList.newInstance();
		List<Map> summeryReport=FastList.newInstance();
		Map<String,Object> employmentInfo =FastMap.newInstance();
		Map<String, Object> summeryReportMap=	FastMap.newInstance();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat dfWithDash = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm ");
		int daysCountBeforeJoining=0;
		int daysCountAfterResignation=0;
		if(startDateOfMonth!=null&&lastDateOfMonth!=null){
		List<GenericValue> employeeLeaves=new ArrayList<GenericValue>();
		
		
		  String LateEntryTime="";
		  String attendanceBonusAllowances="";
		  String lateEntryDaysForAttenBonusDisable="";
		  String lateEntryDaysForAttenBonusDeduction="";
		  String montlyBonusAmount="0";
		  String bonusDeductionAmount="";
		  String leaveAbsentForBonusDisable="";
		  String attenBonusDeductionAmountForhalfDayleave="";
		
		try{
			attendanceBonusAllowances=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, toTime, orgPartyId, UtilPreferenceProperties.ATTENDANCEBONUSALLOWANCES);
		    lateEntryDaysForAttenBonusDisable=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, toTime, orgPartyId, UtilPreferenceProperties.LATEENTRYDAYSFORATTENBONUSDISABLE);
			lateEntryDaysForAttenBonusDeduction=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, toTime, orgPartyId, UtilPreferenceProperties.LATEENTRYDAYSFORATTENBONUSDEDUCTION);
		    montlyBonusAmount=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, toTime, orgPartyId, UtilPreferenceProperties.MONTLYBONUSAMOUNT);
		    bonusDeductionAmount=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, toTime, orgPartyId, UtilPreferenceProperties.BONUSDEDUCTIONAMOUNT);
		    leaveAbsentForBonusDisable=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, toTime, orgPartyId, UtilPreferenceProperties.LEAVEORABSENTFORBONUSDISABLE);
		    attenBonusDeductionAmountForhalfDayleave=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, toTime, orgPartyId, UtilPreferenceProperties.HALFDAYLEAVEFORATTBONUSDeduction);
			
		}catch(Exception e){
			
		}
		
		
		
		boolean getAttendanceBonus=true;
		int totalNumberOfLateEntry=0;
		try{
		List<Map<String,Object>> holidays=FastList.newInstance();
		holidays=Utils.getHolidaysWithType(orgPartyId, startDateOfMonth, lastDateOfMonth, delegator);
		long stime=Utils.getStartTime(fromTime);
		long etime=Utils.getEndTime(toTime);
		
		
		
		List<GenericValue> employmentList=FastList.newInstance();
		java.sql.Timestamp joiningDate=null;
		java.sql.Timestamp resignationDate=null;
		try{
			employmentList=delegator.findByAnd("Employment", "partyIdTo",partyId);
			GenericValue employment=EntityUtil.getFirst(employmentList);
			joiningDate=(Timestamp)employment.get("fromDate");
			resignationDate=(Timestamp)employment.get("thruDate");
		}catch(Exception e){
			logger.debug("Exception--------------->"+e);
		}		
		while(stime<=etime){
				List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
				Timestamp dayStart=new Timestamp(stime);
				Object checkHoliday=Utils.checkHolidayFromHolidayMap(holidays,dayStart);
				boolean joiningFlag=true;
				boolean resignationFlag=true;
				if(joiningDate!=null){
					if(stime>=joiningDate.getTime()){
						joiningFlag=true;
					}else{
						joiningFlag=false;
						daysCountBeforeJoining++;
						if(UtilValidate.isEmpty(checkHoliday)){
							getAttendanceBonus=false;
						}
					}
				}
				if(resignationDate!=null){
					if(stime<resignationDate.getTime()){
						resignationFlag=true;
					}else{
						resignationFlag=false;
						daysCountAfterResignation++;
					}
				}
				if(joiningFlag && resignationFlag){																	
				long dayEndInLong=Utils.getEndTime(new java.sql.Date(stime));
				Timestamp dayEnd=new Timestamp(dayEndInLong);
			    
				
				if(checkHoliday==null){
				List<EntityExpr> exprsList = FastList.newInstance();
			    exprsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			    exprsList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
			    double numberOfLeaveDays=Utils.getTotalLeave(delegator, orgPartyId, partyId, dfWithDash.format(dayStart), dfWithDash.format(dayStart));	
				if(numberOfLeaveDays<1){
				List<EntityExpr> exprs = FastList.newInstance();
				List<String> orderBy = FastList.newInstance();
				orderBy.add("logtimeStamp ASC");
				exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO,dayStart));
				exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
				exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				try {
					employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);

				} catch (GenericEntityException e) {
					logger.debug("Got Exception To get Employee Log In findJobCard Function---->"+e);
					e.printStackTrace();
				}
				//String exitTime=AttendanceUtils.getExitTime(partyId, delegator, new Date(stime), orgPartyId);
				if(employeeLogs.size()>=1){
					Timestamp	outtime=null;
					Timestamp	entrytime=null;
					if(employeeLogs.size()==1){
						String entryTime=Utils.getEntryTime(partyId, delegator, new Date(stime), orgPartyId);
						long entryTimeInLong=Utils.getStrngTimeInDate(entryTime, new Date(stime));
						Timestamp officeEntryTimeInTimeStamp=new Timestamp(entryTimeInLong);
						GenericValue employeeLogEntrry=employeeLogs.get(0);
						entrytime=(Timestamp)employeeLogEntrry.get("logtimeStamp");
						float officeStayTime=UtilDateTime.getHourInterval(officeEntryTimeInTimeStamp, entrytime);
						if(officeStayTime<0){
							officeStayTime=officeStayTime*-1;
						}
						if(officeStayTime>4){
							entrytime=null;
							outtime=(Timestamp)employeeLogEntrry.get("logtimeStamp");
						}
					}else if(employeeLogs.size()>1){
					GenericValue outemployeeLog=employeeLogs.get(employeeLogs.size()-1);
					outtime=(Timestamp)outemployeeLog.get("logtimeStamp");
					GenericValue employeeLogEntrry=employeeLogs.get(0);
					entrytime=(Timestamp)employeeLogEntrry.get("logtimeStamp");
					}
					float officeStayTime=6F;		// If entry time or out time is missing that employee is count as present,so we initialize this as 6h
					if(entrytime!=null && outtime!=null){
						officeStayTime=UtilDateTime.getHourInterval(entrytime, outtime);
					}
					if(officeStayTime>=6){
					summeryWorkDay++;
					Integer otHour=0;
					if(outtime!=null){
						otHour=Utils.getEmployeeOt(delegator, orgPartyId, partyId, new Date(stime), outtime);
					}if(otHour>=0){
					summeryTotalOtInHour=summeryTotalOtInHour+otHour;
					}
					}else if(officeStayTime>=4 && employeeLogs.size()>1){
						summeryTotalHalfDay++;
						summeryWorkDay=summeryWorkDay+0.5;
						if(numberOfLeaveDays==0.5){
							summeryLeaveDay=summeryLeaveDay+.5;
							summeryOfHalfDayLeave++;
						}else{
							summeryAbsentDay=summeryAbsentDay+.5;
							summeryTotalHalfDayAbsent++;
						}
						Integer otHour=0;
						if(outtime!=null){
							otHour=Utils.getEmployeeOt(delegator, orgPartyId, partyId, new Date(stime), outtime);
						}
						if(otHour>=0){
						summeryTotalOtInHour=summeryTotalOtInHour+otHour;
						}
					}else if(employeeLogs.size()==1){
						Integer otHour=0;
						if(outtime!=null){
							otHour=Utils.getEmployeeOt(delegator, orgPartyId, partyId, new Date(stime), outtime);
						}
						if(otHour>=0){
						summeryTotalOtInHour=summeryTotalOtInHour+otHour;
						}
					}else{
					summeryAbsentDay++;
					}
					if(attendanceBonusAllowances.equals("Y")){
						java.sql.Date date1=new Date(stime); 
						try {
							LateEntryTime = UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date1, orgPartyId, UtilPreferenceProperties.LATEENTRYTIME);
						} catch (GenericEntityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						long  lateEntryTimeInDate= Utils.getLateEntryTimeInDate(LateEntryTime, date1);
						if(entrytime!=null){
						if(entrytime.getTime()>lateEntryTimeInDate){
							totalNumberOfLateEntry++;
							}
						}
					}
				}else if(numberOfLeaveDays==0.5){
					summeryAbsentDay=summeryAbsentDay+.5;
					summeryLeaveDay=summeryLeaveDay+.5;
				}else{
					summeryAbsentDay++;
				}
				}else{
					summeryLeaveDay++;
				}
		}else{
			summeryHoliDay++;
			String holidayType=(String)checkHoliday;
			if(holidayType.equals("OFFICE_HOLIDAY")){
				summeryWeeklyDay++;
			}else if(holidayType.equals("PUBLIC_HOLIDAY")){
				summeryFestivalDay++;
					}else if(holidayType.equals("OTHERS_HOLIDAY")){
						summeryOtherHoliDay++;
					}
				}
			}
				stime=stime+86400000;
			}
		}catch(Exception e){
			logger.debug("Exception in findJobCard--->"+e);
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
		  	  	int halfdayLeaveDeductionAmount = Integer.parseInt(attenBonusDeductionAmountForhalfDayleave); 		  
/*		  		  if(lateEntry<daysForBonusDeduction){
		  	    	  bonus=montlyBonusAmount; 
		  	      }
		  	      else if(lateEntry<daysForBonusDisable){
		  	    	  bonus=Integer.toString(bonusAmount-deductionAmount);
		  	      }else{
		  	    	  bonus="0";  
		  	      }*/		  		  
		  		  String bonus;
		  		  if(totalNumberOfLateEntry>=daysForBonusDisable || summeryAbsentDay>=absentDaysForBonusDisable ||!getAttendanceBonus ||summeryLeaveDay>=absentDaysForBonusDisable){
		  	    	  bonus="0"; 
		  	      }
		  	      else if(summeryTotalHalfDayAbsent==1 || summeryOfHalfDayLeave==1){
		  	    	  bonus=Integer.toString(bonusAmount-halfdayLeaveDeductionAmount);
		  	      }else{
		  	    	  bonus=montlyBonusAmount;
		  	      }
		  		summeryReportMap.put("attendanceBonusAmount",bonus);
		  		summeryReportMap.put("totalNumberOfLateEntry", totalNumberOfLateEntry);
		  		summeryReportMap.put("summeryOfHalfDayLeave", summeryOfHalfDayLeave);
		  		  		  		  
		  	}catch(Exception e){

		  	}
		 }else{
			 summeryReportMap.put("attendanceBonusAmount","0"); 		
	  		 summeryReportMap.put("totalNumberOfLateEntry", totalNumberOfLateEntry);
	  		 summeryReportMap.put("summeryOfHalfDayLeave", summeryOfHalfDayLeave);
		 }
		
		
		
		
		}
		double basicSalary=0;
        String basicSalaryString = Attendance.getBasicSalaryByPartyId(delegator, partyId);
        if (UtilValidate.isNotEmpty(basicSalaryString)) {
            basicSalary = Double.parseDouble(basicSalaryString);
        }
        try {
			otCalculationOperand=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, new Date(System.currentTimeMillis()), orgPartyId, UtilPreferenceProperties.OTCALCULATIONOPERAND);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        double rate = 0;
        if (basicSalary != 0 && UtilValidate.isNotEmpty(otCalculationOperand)) {
            rate=basicSalary/Double.parseDouble(otCalculationOperand);
            BigDecimal bd = new BigDecimal(rate).setScale(2, RoundingMode.HALF_UP);
            rate = bd.doubleValue();
        }
        if(otCalculationOperand!=null && otCalculationOperand.length()>0){
            getOverTimeAmount= getOverTimeAmount.valueOf(rate * summeryTotalOtInHour).setScale(2, RoundingMode.HALF_UP);

		}
        deductionAmount = deductionAmount.valueOf((basicSalary/daysOfMonth)*summeryAbsentDay);
        if(daysCountBeforeJoining>0 ||daysCountAfterResignation>0){
        	BigDecimal grossSalary=SalaryWorker.getMonthlyGrossSalary(delegator, partyId);
        	BigDecimal addToDeductionAmount=null;
        	
        	addToDeductionAmount=addToDeductionAmount.valueOf((grossSalary.doubleValue()/daysOfMonth)*(daysCountBeforeJoining+daysCountAfterResignation));
        	deductionAmount=deductionAmount.add(addToDeductionAmount);
        }
		deductionAmount = deductionAmount.setScale(2, RoundingMode.HALF_UP);

		summeryReportMap.put("otRatePerHour", rate);
		summeryReportMap.put("totalotInHour", String.valueOf(summeryTotalOtInHour));
		summeryReportMap.put("totalotAmount", getOverTimeAmount);

		summeryReportMap.put("totalWorkDay", String.valueOf(summeryWorkDay));
		summeryReportMap.put("totalAbsentDay", String.valueOf(summeryAbsentDay-summeryTotalHalfDayAbsent));
		summeryReportMap.put("totalHalfDayAbsent", summeryTotalHalfDayAbsent);
		summeryReportMap.put("deductionAmountForAbsent", deductionAmount);

		summeryReportMap.put("totalLeaveDay", String.valueOf(summeryLeaveDay));

		summeryReportMap.put("totalMonthlyHoliDay", String.valueOf(summeryHoliDay));
		summeryReportMap.put("totalWeeklyHoliDay", String.valueOf(summeryWeeklyDay));
		summeryReportMap.put("totalFestivalHoliDay", String.valueOf(summeryFestivalDay));
		summeryReportMap.put("totalOtherHoliDay", String.valueOf(summeryOtherHoliDay));
		return summeryReportMap;
	}
    
    public static Map<String,Object> getAuditJobCardInfo(Delegator delegator,String orgPartyId,String partyId,String monthNumber,String year){
    	double summeryWorkDay=0;
		double summeryAbsentDay=0;
		double summeryLeaveDay=0;
		int summeryHoliDay=0;
		int summeryWeeklyDay=0;
		int summeryFestivalDay=0;
		int summeryOtherHoliDay=0;
		int summeryTotalHalfDay=0;
		int summeryTotalHalfDayAbsent=0;
		int summeryTotalOtInHour=0;
		int summeryOfHalfDayLeave=0;
		BigDecimal getOverTimeAmount=null;
		String otCalculationOperand=null;
		BigDecimal deductionAmount=null;

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR,Integer.parseInt(year));
		cal.set(Calendar.MONTH, Integer.parseInt(monthNumber));
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	    Timestamp startDateOfMonth = new Timestamp(cal.getTimeInMillis());
	    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
	    int daysOfMonth=cal.getActualMaximum(Calendar.DAY_OF_MONTH);

	    Timestamp lastDateOfMonth=new Timestamp(cal.getTimeInMillis());
		Map<String, Object> result = ServiceUtil.returnSuccess();
		java.sql.Date fromTime=new Date(startDateOfMonth.getTime());
		java.sql.Date toTime=new Date(lastDateOfMonth.getTime());
		List<Map> employeeLogList=FastList.newInstance();
		List<Map> summeryReport=FastList.newInstance();
		Map<String,Object> employmentInfo =FastMap.newInstance();
		Map<String, Object> summeryReportMap=	FastMap.newInstance();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat dfWithDash = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm ");
		int daysCountBeforeJoining=0;
		int daysCountAfterResignation=0;
		if(startDateOfMonth!=null&&lastDateOfMonth!=null){
		List<GenericValue> employeeLeaves=new ArrayList<GenericValue>();
		
		
		  String LateEntryTime="";
		  String attendanceBonusAllowances="";
		  String lateEntryDaysForAttenBonusDisable="";
		  String lateEntryDaysForAttenBonusDeduction="";
		  String montlyBonusAmount="0";
		  String bonusDeductionAmount="";
		  String leaveAbsentForBonusDisable="";
		  String attenBonusDeductionAmountForhalfDayleave="";
		
		try{
			attendanceBonusAllowances=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, toTime, orgPartyId, UtilPreferenceProperties.ATTENDANCEBONUSALLOWANCES);
		    lateEntryDaysForAttenBonusDisable=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, toTime, orgPartyId, UtilPreferenceProperties.LATEENTRYDAYSFORATTENBONUSDISABLE);
			lateEntryDaysForAttenBonusDeduction=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, toTime, orgPartyId, UtilPreferenceProperties.LATEENTRYDAYSFORATTENBONUSDEDUCTION);
		    montlyBonusAmount=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, toTime, orgPartyId, UtilPreferenceProperties.MONTLYBONUSAMOUNT);
		    bonusDeductionAmount=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, toTime, orgPartyId, UtilPreferenceProperties.BONUSDEDUCTIONAMOUNT);
		    leaveAbsentForBonusDisable=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, toTime, orgPartyId, UtilPreferenceProperties.LEAVEORABSENTFORBONUSDISABLE);
		    attenBonusDeductionAmountForhalfDayleave=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, toTime, orgPartyId, UtilPreferenceProperties.HALFDAYLEAVEFORATTBONUSDeduction);
			
		}catch(Exception e){
			
		}
		
		
		
		boolean getAttendanceBonus=true;
		int totalNumberOfLateEntry=0;
		try{
		List<Map<String,Object>> holidays=FastList.newInstance();
		holidays=Utils.getHolidaysWithType(orgPartyId, startDateOfMonth, lastDateOfMonth, delegator);
		long stime=Utils.getStartTime(fromTime);
		long etime=Utils.getEndTime(toTime);
		
		
		
		List<GenericValue> employmentList=FastList.newInstance();
		java.sql.Timestamp joiningDate=null;
		java.sql.Timestamp resignationDate=null;
		try{
			employmentList=delegator.findByAnd("Employment", "partyIdTo",partyId);
			GenericValue employment=EntityUtil.getFirst(employmentList);
			joiningDate=(Timestamp)employment.get("fromDate");
			resignationDate=(Timestamp)employment.get("thruDate");
		}catch(Exception e){
			logger.debug("Exception--------------->"+e);
		}		
		while(stime<=etime){
				List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
				Timestamp dayStart=new Timestamp(stime);
				Object checkHoliday=Utils.checkHolidayFromHolidayMap(holidays,dayStart);
				boolean joiningFlag=true;
				boolean resignationFlag=true;
				if(joiningDate!=null){
					if(stime>=joiningDate.getTime()){
						joiningFlag=true;
					}else{
						joiningFlag=false;
						daysCountBeforeJoining++;
						if(UtilValidate.isEmpty(checkHoliday)){
							getAttendanceBonus=false;
						}
					}
				}
				if(resignationDate!=null){
					if(stime<resignationDate.getTime()){
						resignationFlag=true;
					}else{
						resignationFlag=false;
						daysCountAfterResignation++;
					}
				}
				if(joiningFlag && resignationFlag){																	
				long dayEndInLong=Utils.getEndTime(new java.sql.Date(stime));
				Timestamp dayEnd=new Timestamp(dayEndInLong);
			    
				
				if(checkHoliday==null){
				List<EntityExpr> exprsList = FastList.newInstance();
			    exprsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			    exprsList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
			    double numberOfLeaveDays=Utils.getTotalLeave(delegator, orgPartyId, partyId, dfWithDash.format(dayStart), dfWithDash.format(dayStart));	
				if(numberOfLeaveDays<1){
				List<EntityExpr> exprs = FastList.newInstance();
				List<String> orderBy = FastList.newInstance();
				orderBy.add("logtimeStamp ASC");
				exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO,dayStart));
				exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
				exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				try {
					employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);

				} catch (GenericEntityException e) {
					logger.debug("Got Exception To get Employee Log In findJobCard Function---->"+e);
					e.printStackTrace();
				}
				//String exitTime=AttendanceUtils.getExitTime(partyId, delegator, new Date(stime), orgPartyId);
				if(employeeLogs.size()>=1){
					Timestamp	outtime=null;
					Timestamp	entrytime=null;
					if(employeeLogs.size()==1){
						String entryTime=Utils.getEntryTime(partyId, delegator, new Date(stime), orgPartyId);
						long entryTimeInLong=Utils.getStrngTimeInDate(entryTime, new Date(stime));
						Timestamp officeEntryTimeInTimeStamp=new Timestamp(entryTimeInLong);
						GenericValue employeeLogEntrry=employeeLogs.get(0);
						entrytime=(Timestamp)employeeLogEntrry.get("logtimeStamp");
						float officeStayTime=UtilDateTime.getHourInterval(officeEntryTimeInTimeStamp, entrytime);
						if(officeStayTime<0){
							officeStayTime=officeStayTime*-1;
						}
						if(officeStayTime>4){
							entrytime=null;
							outtime=(Timestamp)employeeLogEntrry.get("logtimeStamp");
						}
					}else if(employeeLogs.size()>1){
					GenericValue outemployeeLog=employeeLogs.get(employeeLogs.size()-1);
					outtime=(Timestamp)outemployeeLog.get("logtimeStamp");
					GenericValue employeeLogEntrry=employeeLogs.get(0);
					entrytime=(Timestamp)employeeLogEntrry.get("logtimeStamp");
					}
					float officeStayTime=6F;		// If entry time or out time is missing that employee is count as present,so we initialize this as 6h
					if(entrytime!=null && outtime!=null){
						officeStayTime=UtilDateTime.getHourInterval(entrytime, outtime);
					}
					if(officeStayTime>=6){
					summeryWorkDay++;
					Integer otHour=0;
					if(outtime!=null){
						otHour=Utils.getEmployeeAuditOt(delegator, orgPartyId, partyId, new Date(stime), outtime);
						logger.debug("Date------>"+new Date(stime)+"    outtime---->"+outtime+"    otHour---->"+otHour);
					}if(otHour>=0){
					summeryTotalOtInHour=summeryTotalOtInHour+otHour;
					}
					}else if(officeStayTime>=4 && employeeLogs.size()>1){
						summeryTotalHalfDay++;
						summeryWorkDay=summeryWorkDay+0.5;
						if(numberOfLeaveDays==0.5){
							summeryLeaveDay=summeryLeaveDay+.5;
							summeryOfHalfDayLeave++;
						}else{
							summeryAbsentDay=summeryAbsentDay+.5;
							summeryTotalHalfDayAbsent++;
						}
						Integer otHour=0;
						if(outtime!=null){
							otHour=Utils.getEmployeeAuditOt(delegator, orgPartyId, partyId, new Date(stime), outtime);
							logger.debug("Date------>"+new Date(stime)+"    outtime---->"+outtime+"    otHour---->"+otHour);
						}
						if(otHour>=0){
						summeryTotalOtInHour=summeryTotalOtInHour+otHour;
						}
					}else if(employeeLogs.size()==1){
						Integer otHour=0;
						if(outtime!=null){
							otHour=Utils.getEmployeeAuditOt(delegator, orgPartyId, partyId, new Date(stime), outtime);
							logger.debug("Date------>"+new Date(stime)+"    outtime---->"+outtime+"    otHour---->"+otHour);
						}
						if(otHour>=0){
						summeryTotalOtInHour=summeryTotalOtInHour+otHour;
						}
					}else{
					summeryAbsentDay++;
					}
					if(attendanceBonusAllowances.equals("Y")){
						java.sql.Date date1=new Date(stime); 
						try {
							LateEntryTime = UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, date1, orgPartyId, UtilPreferenceProperties.LATEENTRYTIME);
						} catch (GenericEntityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						long  lateEntryTimeInDate= Utils.getLateEntryTimeInDate(LateEntryTime, date1);
						if(entrytime!=null){
						if(entrytime.getTime()>lateEntryTimeInDate){
							totalNumberOfLateEntry++;
							}
						}
					}
				}else if(numberOfLeaveDays==0.5){
					summeryAbsentDay=summeryAbsentDay+.5;
					summeryLeaveDay=summeryLeaveDay+.5;
				}else{
					summeryAbsentDay++;
				}
				}else{
					summeryLeaveDay++;
				}
		}else{
			summeryHoliDay++;
			String holidayType=(String)checkHoliday;
			if(holidayType.equals("OFFICE_HOLIDAY")){
				summeryWeeklyDay++;
			}else if(holidayType.equals("PUBLIC_HOLIDAY")){
				summeryFestivalDay++;
					}else if(holidayType.equals("OTHERS_HOLIDAY")){
						summeryOtherHoliDay++;
					}
				}
			}
				stime=stime+86400000;
			}
		}catch(Exception e){
			logger.debug("Exception in findJobCard--->"+e);
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
		  	  	int halfdayLeaveDeductionAmount = Integer.parseInt(attenBonusDeductionAmountForhalfDayleave); 		  
/*		  		  if(lateEntry<daysForBonusDeduction){
		  	    	  bonus=montlyBonusAmount; 
		  	      }
		  	      else if(lateEntry<daysForBonusDisable){
		  	    	  bonus=Integer.toString(bonusAmount-deductionAmount);
		  	      }else{
		  	    	  bonus="0";  
		  	      }*/		  		  
		  		  String bonus;
		  		  if(totalNumberOfLateEntry>=daysForBonusDisable || summeryAbsentDay>=absentDaysForBonusDisable ||!getAttendanceBonus ||summeryLeaveDay>=absentDaysForBonusDisable){
		  	    	  bonus="0"; 
		  	      }
		  	      else if(summeryTotalHalfDayAbsent==1 || summeryOfHalfDayLeave==1){
		  	    	  bonus=Integer.toString(bonusAmount-halfdayLeaveDeductionAmount);
		  	      }else{
		  	    	  bonus=montlyBonusAmount;
		  	      }
		  		summeryReportMap.put("attendanceBonusAmount",bonus);
		  		summeryReportMap.put("totalNumberOfLateEntry", totalNumberOfLateEntry);
		  		summeryReportMap.put("summeryOfHalfDayLeave", summeryOfHalfDayLeave);
		  		  		  		  
		  	}catch(Exception e){

		  	}
		 }else{
			 summeryReportMap.put("attendanceBonusAmount","0"); 		
	  		 summeryReportMap.put("totalNumberOfLateEntry", totalNumberOfLateEntry);
	  		 summeryReportMap.put("summeryOfHalfDayLeave", summeryOfHalfDayLeave);
		 }
		
		
		
		
		}
		double basicSalary=0;
        String basicSalaryString = Attendance.getBasicSalaryByPartyId(delegator, partyId);
        if (UtilValidate.isNotEmpty(basicSalaryString)) {
            basicSalary = Double.parseDouble(basicSalaryString);
        }
        try {
			otCalculationOperand=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, new Date(System.currentTimeMillis()), orgPartyId, UtilPreferenceProperties.OTCALCULATIONOPERAND);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        double rate = 0;
        if (basicSalary != 0 && UtilValidate.isNotEmpty(otCalculationOperand)) {
            rate=basicSalary/Double.parseDouble(otCalculationOperand);
            BigDecimal bd = new BigDecimal(rate).setScale(2, RoundingMode.HALF_UP);
            rate = bd.doubleValue();
        }
        if(otCalculationOperand!=null && otCalculationOperand.length()>0){
            getOverTimeAmount= getOverTimeAmount.valueOf(rate * summeryTotalOtInHour).setScale(2, RoundingMode.HALF_UP);

		}
        deductionAmount = deductionAmount.valueOf((basicSalary/daysOfMonth)*summeryAbsentDay);
        if(daysCountBeforeJoining>0 ||daysCountAfterResignation>0){
        	BigDecimal grossSalary=SalaryWorker.getMonthlyGrossSalary(delegator, partyId);
        	BigDecimal addToDeductionAmount=null;
        	
        	addToDeductionAmount=addToDeductionAmount.valueOf((grossSalary.doubleValue()/daysOfMonth)*(daysCountBeforeJoining+daysCountAfterResignation));
        	deductionAmount=deductionAmount.add(addToDeductionAmount);
        }
		deductionAmount = deductionAmount.setScale(2, RoundingMode.HALF_UP);

		summeryReportMap.put("otRatePerHour", rate);
		summeryReportMap.put("totalotInHour", String.valueOf(summeryTotalOtInHour));
		summeryReportMap.put("totalotAmount", getOverTimeAmount);

		summeryReportMap.put("totalWorkDay", String.valueOf(summeryWorkDay));
		summeryReportMap.put("totalAbsentDay", String.valueOf(summeryAbsentDay-summeryTotalHalfDayAbsent));
		summeryReportMap.put("totalHalfDayAbsent", summeryTotalHalfDayAbsent);
		summeryReportMap.put("deductionAmountForAbsent", deductionAmount);

		summeryReportMap.put("totalLeaveDay", String.valueOf(summeryLeaveDay));

		summeryReportMap.put("totalMonthlyHoliDay", String.valueOf(summeryHoliDay));
		summeryReportMap.put("totalWeeklyHoliDay", String.valueOf(summeryWeeklyDay));
		summeryReportMap.put("totalFestivalHoliDay", String.valueOf(summeryFestivalDay));
		summeryReportMap.put("totalOtherHoliDay", String.valueOf(summeryOtherHoliDay));
		return summeryReportMap;
    }
    
    public static BigDecimal getMonthlyGrossSalary(Delegator delegator,String partyId){
    	BigDecimal grossSalary=null;
    	List<GenericValue> employeeBenefitItems=FastList.newInstance();
    	double salaryTotal=0;
    	try{
    		employeeBenefitItems=delegator.findByAnd("EmployeeBenefitItems", "partyIdTo",partyId,"category","M");
    	for (GenericValue benefit : employeeBenefitItems) {
			String defaultGlAccountId=benefit.get("defaultGlAccountId").toString();
			double costing=Double.parseDouble(benefit.get("cost").toString());
			if(!defaultGlAccountId.equals(GlProperties.ATTENDANCE_BONUS)){
            salaryTotal += costing;
            }       
    	 }
    	}catch(Exception e){
    		logger.debug("Exception to get gross salary----->"+e);
    	}
    	grossSalary=BigDecimal.valueOf(salaryTotal);
    	return grossSalary;
    }
    
}