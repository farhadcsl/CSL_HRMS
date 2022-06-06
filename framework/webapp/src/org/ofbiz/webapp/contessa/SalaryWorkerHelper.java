package org.ofbiz.webapp.contessa;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;

/**
 * Date: 9/15/14
 * Time: 5:20 PM
 */
public class SalaryWorkerHelper {
    private static Logger logger = Logger.getLogger("SalaryWorkerHelper");
    private static final String defaultGlAccountId1 = UtilProperties.getPropertyValue("contessa.properties", "basicSalary.defaultGlAccountId1");
    private static final String defaultGlAccountId2 = UtilProperties.getPropertyValue("contessa.properties", "basicSalary.defaultGlAccountId2");

    /**
     * @param employeeList
     * @param organizationId
     * @param monthNumber
     * @param year
     * @return
     * @author zzz
     */
    public static BigDecimal getTotalMonthlyOvertime(Delegator delegator, List<GenericValue> employeeList, String organizationId, String monthNumber, String year) {
        BigDecimal totalOTAmount = new BigDecimal("0");
        for (GenericValue employee : employeeList) {
            String partyId = employee.getString("partyId");
            Map<String, Object> overTimeInfo = SalaryWorker.getOverTimeAmountByPartyId(delegator, organizationId, monthNumber, year, partyId);
            BigDecimal otAmount = (BigDecimal) overTimeInfo.get("totalHourAmount");
            totalOTAmount = totalOTAmount.add(otAmount);
        }
        return totalOTAmount;
    }

    /**
     * @param delegator
     * @param employeeList
     * @param organizationId
     * @param monthNumber
     * @param year
     * @return
     * @author zzz
     */
    public static BigDecimal getTotalMonthlyExtraOvertime(Delegator delegator, List<GenericValue> employeeList, String organizationId, String monthNumber, String year) {
        BigDecimal totalEOTAmount = new BigDecimal("0");
        for (GenericValue employee : employeeList) {
            String partyId = employee.getString("partyId");
            Map<String, Object> eotInfoMap = Attendance.getMonthlyEOTAmountByEmplPartyId(delegator, monthNumber, year, partyId, organizationId);
            BigDecimal eotAmount = (BigDecimal) eotInfoMap.get("eotAmount");
            eotAmount = eotAmount.setScale(2, RoundingMode.HALF_UP);
            totalEOTAmount = totalEOTAmount.add(eotAmount);
        }
        return totalEOTAmount;
    }

    /**
     * @param delegator
     * @param employeeList
     * @param organizationId
     * @param monthNumber
     * @param year
     * @return
     * @author zzz
     */
    public static BigDecimal getTotalMonthlyAttendanceBonus(Delegator delegator, List<GenericValue> employeeList, String organizationId, String monthNumber, String year) {
        BigDecimal totalAttendanceBonus = new BigDecimal("0");
        for (GenericValue employee : employeeList) {
            String partyId = employee.getString("partyId");
            String attendenceBonus = Attendance.getMonthlyAttendanceBonusForEmployee(delegator, monthNumber, year, partyId, organizationId);
            BigDecimal attendanceBonus = new BigDecimal(attendenceBonus);
            totalAttendanceBonus = totalAttendanceBonus.add(attendanceBonus);
        }
        return totalAttendanceBonus;
    }


    /**
     * @param delegator
     * @param companyId
     * @param partyId
     * @param month
     * @param year
     * @return
     * @author zzz
     */
    public static BigDecimal getDeductionAmountForUnpaidLeave(Delegator delegator, String companyId, String partyId, String month, String year) {

        BigDecimal deductionAmount = new BigDecimal(0);


        BigDecimal unpaidLeave = countLeaveByMonthAndLeaveType(delegator, companyId, partyId, LeaveType.LEAVE_TYPE_LEAVE_WITHOUT_PAY, month, year);


        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(year));
        cal.set(Calendar.MONTH, Integer.parseInt(month));

        int daysOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        double basicSalary = 0;
        try {
            basicSalary = Double.parseDouble(Attendance.getBasicSalaryByPartyId(delegator, partyId));
        } catch (Exception e) {
        }


        deductionAmount = new BigDecimal((basicSalary / daysOfMonth) * unpaidLeave.doubleValue());

        return deductionAmount.setScale(2, RoundingMode.HALF_UP);

    }

    /**
     * @param delegator
     * @param companyId
     * @param partyId
     * @param leaveTypeId
     * @param month
     * @param year
     * @return
     * @author zzz
     */
    public static BigDecimal countLeaveByMonthAndLeaveType(Delegator delegator, String companyId, String partyId, String leaveTypeId, String month, String year) {


        if (UtilValidate.isEmpty(companyId)) {
            List<GenericValue> employees = null;
            try {
                employees = delegator.findList("EmploymentAndPerson", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null, null, null, true);
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
            companyId = employees != null ? (employees.size() > 0 ? employees.get(0).get("partyIdFrom").toString() : "") : "";
        }


        java.sql.Timestamp leaveStartDate = UtilDateTime.toTimestamp((Integer.parseInt(month) + 1) + "", "1", year, "0", "0", "0");
        java.sql.Timestamp leaveEndDate = UtilDateTime.toTimestamp((Integer.parseInt(month) + 2) + "", "1", year, "0", "0", "0");

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

        for (GenericValue oGenericValue : employeeLeaves) {
            int noOfDaysApproved = 0;
            Timestamp stopDate = (Timestamp) oGenericValue.get("thruDate");
            Timestamp startDate = (Timestamp) oGenericValue.get("fromDate");
            String leaveDuration = (String) oGenericValue.get("leaveDuration");

            noOfDaysApproved = UtilDateTime.getIntervalInDays(startDate, stopDate);

            int holidays = 0;
            if (!leaveTypeId.equals(LeaveType.LEAVE_TYPE_EARN_LEAVE)) { // holiday will count except earn leave as per babylon policy
                holidays = getHolidayCount(delegator, companyId, startDate, stopDate);
            }


            if (!leaveDuration.equals(LeaveType.LEAVE_DURATION_FULL_DAY_LEAVE)) {
                finalApprovedDay = finalApprovedDay + .5;
            } else {
                finalApprovedDay = finalApprovedDay + noOfDaysApproved + 1 - holidays;
            }

        }


        return new BigDecimal(finalApprovedDay);

    }


    /**
     * @param delegator
     * @param companyId
     * @param startDate
     * @param endDate
     * @return
     * @author zzz
     */
    public static int getHolidayCount(Delegator delegator, String companyId, Timestamp startDate, Timestamp endDate) {


        List<EntityExpr> exprs = FastList.newInstance();
        exprs.add(EntityCondition.makeCondition("estimatedStartDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate));
        exprs.add(EntityCondition.makeCondition("estimatedStartDate", EntityOperator.LESS_THAN_EQUAL_TO, endDate));
        exprs.add(EntityCondition.makeCondition("companyId", EntityOperator.EQUALS, companyId));

        List<GenericValue> holidayList = new ArrayList<GenericValue>();
        try {
            //delegator.findList(entityName, entityCondition, fieldsToSelect, orderBy, findOptions, useCache)
            holidayList = delegator.findList("WorkEffort", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        return holidayList.size();
    }

    /**
     * @param delegator
     * @param employeeList
     * @param organizationId
     * @param monthNumber
     * @param year
     * @return
     * @author zzz
     */
    public static Map<String, Object> getTotalMonthlyExtraBenefit(Delegator delegator, List<GenericValue> employeeList, String organizationId, String monthNumber, String year) {
        BigDecimal monthlyOTAmount = new BigDecimal("0");
        BigDecimal monthlyEOTAmount = new BigDecimal("0");
        BigDecimal monthlyAttendanceBonus = new BigDecimal("0");
        BigDecimal monthlyHolidayOTAmount = new BigDecimal("0");
        BigDecimal monthlydeductionAmountForUnpaidLeave = new BigDecimal("0");
        BigDecimal monthlydeductionAmountForAbsent = new BigDecimal("0");

        for (GenericValue employee : employeeList) {
            //partyId is employeePartyId
            String partyId = employee.getString("partyIdFrom");
            //For OT
            BigDecimal overTimeAmountBigDecimal = BigDecimal.ZERO;
            BigDecimal otRate = BigDecimal.ZERO;
            BigDecimal otHour = BigDecimal.ZERO;
            Map jobCardInfo = SalaryWorker.getJobCardInfo(delegator, organizationId, partyId, monthNumber, year);
            if (UtilValidate.isNotEmpty(jobCardInfo.get("totalotAmount"))) {
                otRate = new BigDecimal(jobCardInfo.get("otRatePerHour").toString());
                otHour = new BigDecimal(jobCardInfo.get("totalotInHour").toString());
                overTimeAmountBigDecimal = new BigDecimal(jobCardInfo.get("totalotAmount").toString());
                monthlyOTAmount = monthlyOTAmount.add(overTimeAmountBigDecimal);
            }

            //For DeductionAmountForAbsent
            BigDecimal deductionAmountForAbsent = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            if (UtilValidate.isNotEmpty(jobCardInfo.get("deductionAmountForAbsent"))) {
                deductionAmountForAbsent = new BigDecimal(jobCardInfo.get("deductionAmountForAbsent").toString());
                monthlydeductionAmountForAbsent = monthlydeductionAmountForAbsent.add(deductionAmountForAbsent);
            }

            //For EOT
            BigDecimal eotHour = BigDecimal.ZERO;
            BigDecimal eotRate = BigDecimal.ZERO;
            BigDecimal eotAmount = BigDecimal.ZERO;
            Map<String, Object> eotInfoMap = Attendance.getMonthlyEOTAmountByEmplPartyId(delegator, monthNumber, year, partyId, organizationId);
            if (UtilValidate.isNotEmpty(eotInfoMap)) {
                if (UtilValidate.isNotEmpty(eotInfoMap.get("eotAmount"))) {
                    eotAmount = new BigDecimal(eotInfoMap.get("eotAmount").toString());
                }
                if (UtilValidate.isNotEmpty(eotInfoMap.get("eotRate"))) {
                    eotRate = new BigDecimal(eotInfoMap.get("eotRate").toString());
                }
                if (UtilValidate.isNotEmpty(eotInfoMap.get("eotHours"))) {
                    eotHour = new BigDecimal(eotInfoMap.get("eotHours").toString());
                }
            }
            eotAmount = eotAmount.setScale(2, RoundingMode.HALF_UP);
            if (UtilValidate.isNotEmpty(eotAmount)) {
                monthlyEOTAmount = monthlyEOTAmount.add(eotAmount);
            }
            //For Attendance Bonus
            //String attendenceBonus = Attendance.getMonthlyAttendanceBonusForEmployee(delegator, monthNumber, year, partyId, organizationId);
            String attendenceBonusStr = (String) jobCardInfo.get("attendanceBonusAmount");
            BigDecimal attendanceBonus = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            if (UtilValidate.isNotEmpty(attendenceBonusStr)) {
                attendanceBonus = new BigDecimal(attendenceBonusStr);
                monthlyAttendanceBonus = monthlyAttendanceBonus.add(attendanceBonus);
            }

            //For Monthly Holiday Overtime
            BigDecimal holidayOTAmount = BigDecimal.ZERO;
            BigDecimal holidayOtRate = BigDecimal.ZERO;
            BigDecimal holidayOtHour = BigDecimal.ZERO;
            Map<String, Object> holidayOTMap = Attendance.getMonthlyHolidayOvertime(delegator, monthNumber, year, partyId, organizationId);
            if (UtilValidate.isNotEmpty(holidayOTMap)) {
                if (UtilValidate.isNotEmpty(holidayOTMap.get("holidayOTAmount"))) {
                    holidayOTAmount = new BigDecimal(holidayOTMap.get("holidayOTAmount").toString());
                }
                if (UtilValidate.isNotEmpty(holidayOTMap.get("perHrsTake"))) {
                    holidayOtRate = new BigDecimal(holidayOTMap.get("perHrsTake").toString());
                }
                if (UtilValidate.isNotEmpty(holidayOTMap.get("totalHours"))) {
                    holidayOtHour = new BigDecimal(holidayOTMap.get("totalHours").toString());
                }
            }

            if (UtilValidate.isNotEmpty(holidayOTMap)) {
                monthlyHolidayOTAmount = monthlyHolidayOTAmount.add(holidayOTAmount);
            }

            //DeductionAmountForUnpaidLeave
            BigDecimal deductionAmountForUnpaidLeave = getDeductionAmountForUnpaidLeave(delegator, organizationId, partyId, monthNumber, year);
            if (UtilValidate.isNotEmpty(deductionAmountForUnpaidLeave)) {
                monthlydeductionAmountForUnpaidLeave = monthlydeductionAmountForUnpaidLeave.add(deductionAmountForUnpaidLeave);
            }


            BigDecimal totalMonthDay = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            BigDecimal workDay = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            BigDecimal absentDay = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            BigDecimal totalHoliday = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            BigDecimal halfDayAbsent = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            BigDecimal monthlyHoliday = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            BigDecimal weeklyHoliday = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            BigDecimal festivalHoliday = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            BigDecimal otherLeave = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            BigDecimal casualLeave = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            BigDecimal sickLeave = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            BigDecimal earnLeave = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            BigDecimal leaveWithoutPay = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            BigDecimal maternityLeave = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            BigDecimal specialLeave = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);


            if (UtilValidate.isNotEmpty(jobCardInfo.get("totalWorkDay"))) {
                workDay = new BigDecimal(jobCardInfo.get("totalWorkDay").toString());
            }
            if (UtilValidate.isNotEmpty(jobCardInfo.get("totalAbsentDay"))) {
                absentDay = new BigDecimal(jobCardInfo.get("totalAbsentDay").toString());
            }
            if (UtilValidate.isNotEmpty(jobCardInfo.get("totalHalfDayAbsent"))) {
                halfDayAbsent = new BigDecimal(jobCardInfo.get("totalHalfDayAbsent").toString());
            }
            if (UtilValidate.isNotEmpty(jobCardInfo.get("totalWeeklyHoliDay"))) {
                weeklyHoliday = new BigDecimal(jobCardInfo.get("totalWeeklyHoliDay").toString());
            }
            if (UtilValidate.isNotEmpty(jobCardInfo.get("totalFestivalHoliDay"))) {
                festivalHoliday = new BigDecimal(jobCardInfo.get("totalFestivalHoliDay").toString());
            }
            if (UtilValidate.isNotEmpty(jobCardInfo.get("totalOtherHoliDay"))) {
                otherLeave = new BigDecimal(jobCardInfo.get("totalOtherHoliDay").toString());
            }
            if (UtilValidate.isNotEmpty(jobCardInfo.get("totalMonthlyHoliDay"))) {
                monthlyHoliday = new BigDecimal(jobCardInfo.get("totalMonthlyHoliDay").toString());
            }


            maternityLeave = SalaryWorker.countLeaveByMonthAndLeaveType(delegator, organizationId, partyId, LeaveType.LEAVE_TYPE_MATERNITY_LEAVE, monthNumber, year);

            specialLeave = SalaryWorker.countLeaveByMonthAndLeaveType(delegator, organizationId, partyId, LeaveType.LEAVE_TYPE_SPECIAL_LEAVE, monthNumber, year);

            sickLeave = SalaryWorker.countLeaveByMonthAndLeaveType(delegator, organizationId, partyId, LeaveType.LEAVE_TYPE_SICK_LEAVE, monthNumber, year);

            leaveWithoutPay = SalaryWorker.countLeaveByMonthAndLeaveType(delegator, organizationId, partyId, LeaveType.LEAVE_TYPE_LEAVE_WITHOUT_PAY, monthNumber, year);

            earnLeave = SalaryWorker.countLeaveByMonthAndLeaveType(delegator, organizationId, partyId, LeaveType.LEAVE_TYPE_EARN_LEAVE, monthNumber, year);

            casualLeave = SalaryWorker.countLeaveByMonthAndLeaveType(delegator, organizationId, partyId, LeaveType.LEAVE_TYPE_CASUAL_LEAVE, monthNumber, year);

            totalHoliday = maternityLeave.add(specialLeave).add(sickLeave).add(leaveWithoutPay).add(earnLeave).add(casualLeave);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.valueOf(year), Integer.valueOf(monthNumber), 1);
            totalMonthDay = new BigDecimal(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

            Map<String, Object> transaction = FastMap.newInstance();
            transaction.put("emplMonthlyAccountId", delegator.getNextSeqId("EmplMonthlyHolidayLeave"));
            transaction.put("companyId", organizationId);
            transaction.put("employeePartyId", partyId);
            transaction.put("monthNumber", monthNumber);
            transaction.put("fiscalYear", year);
            transaction.put("totalMonthDay", totalMonthDay);
            transaction.put("workDay", workDay);
            transaction.put("absentDay", absentDay);
            transaction.put("totalHoliday", totalHoliday);
            transaction.put("halfDayAbsent", halfDayAbsent);
            transaction.put("monthlyHoliday", monthlyHoliday);
            transaction.put("weeklyHoliday", weeklyHoliday);
            transaction.put("festivalHoliday", festivalHoliday);
            transaction.put("otherLeave", otherLeave);
            transaction.put("casualLeave", casualLeave);
            transaction.put("sickLeave", sickLeave);
            transaction.put("earnLeave", earnLeave);
            transaction.put("leaveWithoutPay", leaveWithoutPay);
            transaction.put("maternityLeave", maternityLeave);
            transaction.put("specialLeave", specialLeave);

            GenericValue EmplMonthlyHolidayLeave = delegator.makeValue("EmplMonthlyHolidayLeave", transaction);
            try {
                delegator.create(EmplMonthlyHolidayLeave);
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }

            transaction = FastMap.newInstance();
            transaction.put("emplSalaryBenefitId", delegator.getNextSeqId("EmplSalaryBenefit"));
            transaction.put("companyId", organizationId);
            transaction.put("employeePartyId", partyId);
            transaction.put("monthNumber", monthNumber);
            transaction.put("fiscalYear", year);
            transaction.put("otRate", otRate);
            transaction.put("otHour", otHour);
            transaction.put("otAmount", overTimeAmountBigDecimal);
            transaction.put("attendanceBonus", attendanceBonus);
            transaction.put("leaveWithoutPayDed", deductionAmountForUnpaidLeave);
            transaction.put("absentDed", deductionAmountForAbsent);

            GenericValue EmplSalaryBenefit = delegator.makeValue("EmplSalaryBenefit", transaction);
            try {
                delegator.create(EmplSalaryBenefit);
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
            transaction = FastMap.newInstance();
            transaction.put("emplExtraSalaryBenefitId", delegator.getNextSeqId("EmplExtraSalaryBenefit"));
            transaction.put("companyId", organizationId);
            transaction.put("employeePartyId", partyId);
            transaction.put("monthNumber", monthNumber);
            transaction.put("fiscalYear", year);
            transaction.put("eotAmount", eotAmount);
            transaction.put("eotHour", eotHour);
            transaction.put("eotRate", eotRate);
            transaction.put("holidayOt", holidayOTAmount);
            transaction.put("holidayOtRate", holidayOtRate);
            transaction.put("holidayOtHour", holidayOtHour);

            GenericValue EmplExtraSalaryBenefit = delegator.makeValue("EmplExtraSalaryBenefit", transaction);
            try {
                delegator.create(EmplExtraSalaryBenefit);
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }

        }

        Map<String, Object> monthlyExtraBenefit = FastMap.newInstance();
        monthlyExtraBenefit.put("monthlyOTAmount", monthlyOTAmount);
        monthlyExtraBenefit.put("monthlyEOTAmount", monthlyEOTAmount);
        monthlyExtraBenefit.put("monthlyAttendanceBonus", monthlyAttendanceBonus);
        monthlyExtraBenefit.put("monthlyHolidayOTAmount", monthlyHolidayOTAmount);
        monthlyExtraBenefit.put("monthlydeductionAmountForUnpaidLeave", monthlydeductionAmountForUnpaidLeave);
        monthlyExtraBenefit.put("monthlydeductionAmountForAbsent", monthlydeductionAmountForAbsent);

        Map<String, Object> transaction = FastMap.newInstance();
        transaction.put("monthlySalaryBenefitId", delegator.getNextSeqId("MonthlySalaryBenefit"));
        transaction.put("companyId", organizationId);
        transaction.put("monthNumber", monthNumber);
        transaction.put("fiscalYear", year);
        transaction.put("otAmount", monthlyOTAmount);
        transaction.put("attendanceBonus", monthlyAttendanceBonus);
        transaction.put("leaveWithoutPayDed", monthlydeductionAmountForUnpaidLeave);
        transaction.put("absentDed", monthlydeductionAmountForAbsent);
        transaction.put("eotAmount", monthlyEOTAmount);
        transaction.put("holidayOt", monthlyHolidayOTAmount);

        GenericValue MonthlySalaryBenefit = delegator.makeValue("MonthlySalaryBenefit", transaction);
        try {
            delegator.create(MonthlySalaryBenefit);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        return monthlyExtraBenefit;
    }
}