package org.ofbiz.humanres.attendance;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

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
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanres.HumanResEvents;
import org.ofbiz.humanres.excelexport.EmployeeExcelExportService;
import org.ofbiz.humanres.excelexport.ExcelUtils;
//import org.ofbiz.humanres.recruitment.RecruitmentServices;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.contessa.Attendance;
import org.ofbiz.webapp.contessa.SalaryWorker;
import org.ofbiz.webapp.contessa.Utils;
import org.opentaps.common.util.UtilCommon;

import com.healthmarketscience.jackcess.impl.scsu.Debug;

/**
 * @author zzz
 * @author zzz
 * 
 */
public class AttendanceReports {
	public static final String module = AttendanceReports.class.getName();
	private static Logger logger = Logger.getLogger("AttendanceReports");
	private static final DateFormat TWELVE_TF = new SimpleDateFormat("hh:mma");
	private static final DateFormat DF = new SimpleDateFormat("dd/mm/yyyy");
	private static final DateFormat TWENTY_FOUR_TF = new SimpleDateFormat(
			"HH:mm");

	private static List<Map<String,String>> ListMonthlyAttendanceSummary = FastList.newInstance();
	private static List<Map<String,Object>> listExtraOvertimePaymentSheetPDF = FastList.newInstance();
	
	
	public static Map<String, Object> findSectionWiseStrenth(
			DispatchContext dctx, Map<String, ? extends Object> context) {

		Map<String, Object> result = ServiceUtil.returnSuccess();

		Delegator delegator = dctx.getDelegator();
		String fromDate = (String) context.get("fromDate");
		String orgPartyId = (String)context.get("orgPartyId");
		String unitParyId=orgPartyId;
		java.sql.Date fromTime = null;
		DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

		List<Map<String, String>> sectionWiseStrenthList = FastList
				.newInstance();

		Map<String, String> totalInfo = FastMap.newInstance();

		// filter on division
		String division = (String) context.get("division");
		if (UtilValidate.isNotEmpty(division)) {
			unitParyId = division;
		}

		// filter on department
		String department = (String) context.get("department");
		if (UtilValidate.isNotEmpty(department)) {
			unitParyId = department;

		}

		// filter on section
		String section = (String) context.get("section");
		if (UtilValidate.isNotEmpty(section)) {
			unitParyId = section;
		}
		// filter on line
		String line = (String) context.get("line");
		if (UtilValidate.isNotEmpty(line)) {
			unitParyId = line;
		}

		if (fromDate != null && !fromDate.isEmpty()) {

			try {
				fromTime = Date.valueOf(fromDate);

			} catch (Exception e) {

			}

			int totalBudgets = 0;
			int totalEmployees = 0;
			int needs = 0;
			int overs = 0;
			int presents = 0;
			int absents = 0;
			int leaves=0;
			double totalAbsentPercentage = 0;
			double totalLeavePercentage = 0;

			if (fromTime != null) {
				List<GenericValue> unitTree = FastList.newInstance();
				unitTree = AttendanceUtils.getUnitTreeByUnitPartyId(delegator,unitTree, unitParyId);

				GenericValue rootParty = AttendanceUtils.getPartyRelationShipEntityByPartyId(delegator,unitParyId);
				unitTree.add(rootParty);

				int sl = 1;

				for (GenericValue unit : unitTree) {

					int totalEmployee = 0;
					int totalBudget = 0;
					int childBudget=0;
					int need = 0;
					int over = 0;
					int present = 0;
					int absent = 0;
					float absentPercentage = 0;
					int leaveEmployees=0;
					float leavePercentage=0;

					String partyIdTo = unit.getString("partyIdTo");
					String departmentName = AttendanceUtils.getDepartmentName(delegator, partyIdTo);

					// For Total Employee
					List<GenericValue> employeeList = FastList.newInstance();
					employeeList = AttendanceUtils.getUnitTotalActiveEmployee(delegator,employeeList, partyIdTo);
					totalEmployee = employeeList.size();

					// For Total Budget
					totalBudget = AttendanceUtils.getTotalBudgetByPartyId(delegator, totalBudget, partyIdTo);
					childBudget = AttendanceUtils.getTotalChildBudgetByPartyId(delegator, childBudget, partyIdTo);
					
					totalBudget=totalBudget-childBudget;
					need = totalBudget - totalEmployee;

					// For calculate Need And Over
					if (need < 0) {
						over = Math.abs(need);
						need = 0;
					}

					// For calculate Absence and Present Employees
					Map<String, String> employeeAttendanceInfo = AttendanceReportUtils
							.getEmployeeAbsenceInformationByDate(delegator, employeeList, fromTime, orgPartyId);
					present = Integer.valueOf(employeeAttendanceInfo
							.get("presentEmployees"));
					absent = Integer.valueOf(employeeAttendanceInfo
							.get("absentEmployees"));
					leaveEmployees= Integer.valueOf(employeeAttendanceInfo
							.get("leaveEmployees"));

					if (totalEmployee > 0) {
						absentPercentage = (float) (absent * 100)
								/ totalEmployee;
						leavePercentage = (float) (leaveEmployees * 100)
								/ totalEmployee;
					} else {
						absentPercentage = 0;
						leavePercentage=0;
					}

					Map<String, String> sectionWiseStrenth = FastMap
							.newInstance();

					sectionWiseStrenth.put("SL", sl + "");
					sectionWiseStrenth.put("Department", departmentName);
					sectionWiseStrenth.put("Required", totalBudget + "");
					sectionWiseStrenth.put("Existing", totalEmployee + "");
					sectionWiseStrenth.put("Need", need + "");
					sectionWiseStrenth.put("Over", over + "");
					sectionWiseStrenth.put("Present", present + "");
					sectionWiseStrenth.put("Absent", absent + "");
					sectionWiseStrenth.put("absentPersentance",
							String.format("%.2f", absentPercentage) + "%");
					sectionWiseStrenth.put("Leave", leaveEmployees + "");
					sectionWiseStrenth.put("LeavePersentance",
							String.format("%.2f", leavePercentage) + "%");

					sectionWiseStrenthList.add(sectionWiseStrenth);

					sl++;
					// For total calculation
					totalBudgets = totalBudgets + totalBudget;
					totalEmployees = totalEmployees + totalEmployee;
					needs = needs + need;
					overs = overs + over;
					presents = presents + present;
					absents = absents + absent;
					leaves=leaves+leaveEmployees;
				}

			}
			if(totalEmployees>0){
				totalAbsentPercentage = (absents*100)/totalEmployees;
				totalLeavePercentage = (leaves*100)/totalEmployees;
			}

			totalInfo.put("totalBudgets", totalBudgets + "");
			totalInfo.put("totalEmployees", totalEmployees + "");
			totalInfo.put("needs", needs + "");
			totalInfo.put("overs", overs + "");
			totalInfo.put("presents", presents + "");
			totalInfo.put("absents", absents + "");
			totalInfo.put("leaves", leaves + "");
			totalInfo.put("totalAbsentPercentage", String.format("%.2f", totalAbsentPercentage) + "%");
			totalInfo.put("totalLeavePercentage", String.format("%.2f", totalLeavePercentage) + "%");
			
		}

		result.put("sectionWiseStrenthList", sectionWiseStrenthList);
		result.put("fromDate", fromDate);
		result.put("totalInfo", totalInfo);
		if (UtilValidate.isNotEmpty(fromTime)) {
			result.put("fromDate", fmt.format(fromTime));
		} else {
			result.put("fromDate", fromDate);

		}

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Section Wise Strength List");
		return result;
	}

	public static Map<String, Object> findTiffinBillSummary(
			DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		Delegator delegator = dctx.getDelegator();

		String fromDate = (String) context.get("fromDate");
		String toDate = (String) context.get("toDate");
		String orgPartyId = (String) context.get("orgPartyId");
		String selectedTiffin = (String) context.get("selectedTiffin");

		String currentDate = DF.format(new java.util.Date(System
				.currentTimeMillis()));

		String tiffinStartTime = "";
		String tiffinEndTime = "";
		int totalEmployee = 0;
		int totalAmount = 0;
		
/*		Map<String, String> tiffinScheduleMapForOrganization = AttendanceUtils
				.getTiffinScheduleByOrganizationPartyId(delegator, orgPartyId);*/

		List<Map<String, String>> departmentEmployeeInfo = FastList
				.newInstance();

		if (!UtilValidate.isEmpty(fromDate) && !UtilValidate.isEmpty(toDate) &&!selectedTiffin.isEmpty()) {

			java.sql.Date fromTime = null;
			java.sql.Date toTime = null;
				try {
					fromTime = Date.valueOf(fromDate);
					toTime = Date.valueOf(toDate);
				} catch (Exception e) {
					fromTime = new Date(System.currentTimeMillis());
				}


			List<GenericValue> unitTree = FastList.newInstance();
			unitTree = AttendanceUtils.getUnitTreeByUnitPartyId(delegator,
					unitTree, orgPartyId);

			GenericValue rootParty = AttendanceUtils
					.getPartyRelationShipEntityByPartyId(delegator, orgPartyId);
			unitTree.add(rootParty);

			for (GenericValue unit : unitTree) {

				int noEmployee = 0;
				int deptAmount = 0;

				String partyIdTo = unit.getString("partyIdTo");
				List<GenericValue> unitLevelPositions = AttendanceUtils
						.getPositionsByParyId(delegator, partyIdTo);

				String departmentName = AttendanceUtils.getDepartmentName(
						delegator, partyIdTo);

				boolean unitflag = false;
				if (unitLevelPositions.size() > 0) {

					Timestamp dayStart = new Timestamp(
							AttendanceUtils.getStartTime(new Date(fromTime
									.getTime())));
					Timestamp dayEnd = new Timestamp(
							AttendanceUtils.getEndTime(new Date(toTime
									.getTime())));

					while (dayStart.getTime() <= dayEnd.getTime()) {

						java.sql.Date date = new Date(dayStart.getTime());

						Timestamp dayStartTime = new Timestamp(AttendanceUtils.getStartTime(date));
						Timestamp dayEndTime = new Timestamp(AttendanceUtils.getEndTime(date));

						for (GenericValue unitLevelPosition : unitLevelPositions) {
							String emplPositionId = unitLevelPosition
									.getString("emplPositionId");

/*							String description = unitLevelPosition
									.getString("description");*/
							List<GenericValue> employeeList = AttendanceUtils
									.getTotalEmployeeByPositionId(delegator,
											emplPositionId);

							// Filter Employee by active status and card
							employeeList = AttendanceUtils
									.filterEmployeeWithCardAndStatus(delegator,
											employeeList);

							for (GenericValue employee : employeeList) {

								List<GenericValue> employeeLogs = new ArrayList<GenericValue>();

								String employeePartyId = employee
										.getString("partyId");
								try{
									String firstTiffinStartTime=null;
									String firstTiffinEndTime=null;
									String firstTiffinRate=null;
									String secondTiffinStartTime=null;
									String secondTiffinEndTime=null;
									String secondTiffinRate=null;
									String nightStartTime=null;
									String nightEndTime=null;
									String nightRate=null;
									GenericValue employeePreferance=UtilAttendancePreference.getEmployeePreference(employeePartyId, delegator, date, orgPartyId);						
									if(employeePreferance!=null && employeePreferance.size()>0){
										firstTiffinStartTime=(String)employeePreferance.get("firstTiffinStartTime");
										firstTiffinEndTime=(String)employeePreferance.get("firstTiffinEndTime");
										firstTiffinRate=(String)employeePreferance.get("firstTiffinRate");
										secondTiffinStartTime=(String)employeePreferance.get("secondTiffinStartTime");
										secondTiffinEndTime=(String)employeePreferance.get("secondTiffinEndTime");
										secondTiffinRate=(String)employeePreferance.get("secondTiffinRate");
										nightStartTime=(String)employeePreferance.get("nightStartTime");
										nightEndTime=(String)employeePreferance.get("nightEndTime");
										nightRate=(String)employeePreferance.get("nightRate");
									}
									if(UtilValidate.isEmpty(employeePreferance)){
									GenericValue designationPreferance=UtilAttendancePreference.getDesignationPreference(employeePartyId, delegator, date, orgPartyId);						
									if(designationPreferance!=null && designationPreferance.size()>0){
										firstTiffinStartTime=(String)designationPreferance.get("firstTiffinStartTime");
										firstTiffinEndTime=(String)designationPreferance.get("firstTiffinEndTime");
										firstTiffinRate=(String)designationPreferance.get("firstTiffinRate");
										secondTiffinStartTime=(String)designationPreferance.get("secondTiffinStartTime");
										secondTiffinEndTime=(String)designationPreferance.get("secondTiffinEndTime");
										secondTiffinRate=(String)designationPreferance.get("secondTiffinRate");
										nightStartTime=(String)designationPreferance.get("nightStartTime");
										nightEndTime=(String)designationPreferance.get("nightEndTime");
										nightRate=(String)designationPreferance.get("nightRate");
										}
									}
								String entryTime = AttendanceUtils
										.getEntryTime(employeePartyId,
												delegator, date, orgPartyId);
								logger.debug("entryTime-------->"+entryTime+"  date---->"+date);
/*								Timestamp entryTimestamp = AttendanceUtils
										.getStringTiffinTimeToTimStamp(
												entryTime, date);*/
								Timestamp entryTimestamp = new Timestamp(
										AttendanceUtils.getStartTime(date));
								logger.debug("entryTimestamp-------->"+entryTimestamp);
								
								Timestamp tiffinStartTimestamp = null;
								Timestamp tiffinEndTimestamp = null;

								if (!firstTiffinStartTime.isEmpty()) {
									tiffinStartTimestamp = AttendanceUtils
											.getStringTiffinTimeToTimStamp(
													firstTiffinStartTime, date);
								}
								if (!firstTiffinEndTime.isEmpty()) {
									tiffinEndTimestamp = AttendanceUtils
											.getStringTiffinTimeToTimStamp(
													firstTiffinEndTime, date);
									dayEndTime = tiffinEndTimestamp;
								}

								// For Day Beginning Employee Log
								if (entryTimestamp != null) {
									dayStartTime = entryTimestamp;
									dayStartTime = AttendanceUtils
											.addHourToTimestamp(dayStartTime,
													-1);
								}
								// For Day End Employee Log
								dayEndTime = AttendanceUtils
										.addHourToTimestamp(dayEndTime, 1);
								List<EntityExpr> exprs = FastList.newInstance();
								List<String> orderBy = FastList.newInstance();
								orderBy.add("logtimeStamp ASC");
								exprs.add(EntityCondition.makeCondition(
										"logtimeStamp",
										EntityOperator.GREATER_THAN_EQUAL_TO,
										dayStartTime));
								exprs.add(EntityCondition.makeCondition(
										"logtimeStamp",
										EntityOperator.LESS_THAN_EQUAL_TO,
										dayEndTime));
								exprs.add(EntityCondition.makeCondition(
										"partyId", EntityOperator.EQUALS,
										employeePartyId));
								try {
									employeeLogs = delegator.findList(
											"EmployeeLog",
											EntityCondition.makeCondition(
													exprs, EntityOperator.AND),
											null, orderBy, null, true);

								} catch (GenericEntityException e) {
									logger.debug("Got Exception To get Employee Log In findJobCard Function---->"
											+ e);
									e.printStackTrace();
								}

								if (employeeLogs.size() > 1) {

									GenericValue employeeLogExit = employeeLogs
											.get(employeeLogs.size() - 1);

									Timestamp exitTimestamp = (Timestamp) employeeLogExit
											.get("logtimeStamp");

									GenericValue employeeLogEntrry = employeeLogs
											.get(0);
									Timestamp entryimestamp = (Timestamp) employeeLogEntrry
											.get("logtimeStamp");

									boolean flag = false;

									if (tiffinStartTimestamp != null
											&& exitTimestamp.getTime() > tiffinStartTimestamp
													.getTime()) {
										flag = true;

									}

									if (flag) {

										unitflag = true;

										int tiffinRate = 0;

										if (!UtilValidate.isEmpty(firstTiffinRate)) {
											tiffinRate = Integer.valueOf(firstTiffinRate);
										}

										if (!UtilValidate.isEmpty(secondTiffinRate)) {
											tiffinRate = Integer.valueOf(secondTiffinRate);
										}

										if (!UtilValidate.isEmpty(nightRate)) {
											tiffinRate = Integer.valueOf(nightRate);
										}

										deptAmount = deptAmount + tiffinRate;

										noEmployee++;

									}

								}
								
							}catch(Exception e){
								
							}//end Try Catch

							}

						}

						dayStart = UtilDateTime.addDaysToTimestamp(dayStart, 1);
					}
				}
				if (unitflag) {

					totalEmployee = totalEmployee + noEmployee;
					totalAmount = totalAmount + deptAmount;

					Map<String, String> employeeInfo = FastMap.newInstance();

					employeeInfo.put("sl", noEmployee + "");
					employeeInfo.put("departmentName", departmentName);
					employeeInfo.put("deptAmount", deptAmount + "");

					departmentEmployeeInfo.add(employeeInfo);

				}

			}

		}

		result.put("tiffinBillSummaryInfoList", departmentEmployeeInfo);
		if(!UtilValidate.isEmpty(fromDate)){
			result.put("fromDate", fromDate);
		}else{
			result.put("fromDate", fromDate);
		}
		result.put("toDate", toDate);
		result.put("tiffinStartTime", tiffinStartTime);
		result.put("tiffinEndTime", tiffinEndTime);
		result.put("currentDate", currentDate);
		result.put("totalEmployee", totalEmployee + "");
		result.put("totalAmount", totalAmount + "");
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE,
				"Employee Attendance Missing List");
		return result;
	}

	// Start Tiffin bill report
	public static Map<String, Object> findTiffinBillsReport(
			DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		Delegator delegator = dctx.getDelegator();
		
		String fromDate = (String) context.get("fromDate");
		String orgPartyId = (String) context.get("orgPartyId");
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		Locale locale = (Locale) context.get("locale");
		Timestamp tt=new Timestamp(System.currentTimeMillis());
		String currentDate = DF.format(new java.util.Date(System
				.currentTimeMillis()));
		int totalNumberOfFirstTiffin=0;
		int totalNumberOfSecondTiffin=0;
		int totalNumberOfNightTiffin=0;
		double totalAmount=0;

		Map<String, String> tiffinScheduleMapForOrganization = AttendanceUtils
				.getTiffinScheduleByOrganizationPartyId(delegator, orgPartyId);
		List<Map<String, List<Map<String, String>>>> departmentEmployeeInfoList = FastList
				.newInstance();
		java.sql.Date selectedDate = null;

		try {
			selectedDate = Date.valueOf(fromDate);
		} catch (Exception e) {

		}
		if (selectedDate != null) {

			Timestamp dayStart = new Timestamp(
					AttendanceUtils.getStartTime(selectedDate));
			Timestamp dayEnd = new Timestamp(
					AttendanceUtils.getEndTime(selectedDate));

			// filter on division
			String division = (String) context.get("division");
			if (UtilValidate.isNotEmpty(division)) {
				orgPartyId = division;
			}

			// filter on department
			String department = (String) context.get("department");

			if (UtilValidate.isNotEmpty(department)) {
				orgPartyId = department;

			}

			// filter on section
			String section = (String) context.get("section");

			if (UtilValidate.isNotEmpty(section)) {
				orgPartyId = section;
			}
			// filter on line
			String line = (String) context.get("line");

			if (UtilValidate.isNotEmpty(line)) {
				orgPartyId = line;
			}

			List<GenericValue> unitTree = FastList.newInstance();
			unitTree = AttendanceUtils.getUnitTreeByUnitPartyId(delegator,
					unitTree, orgPartyId);

			GenericValue rootParty = AttendanceUtils
					.getPartyRelationShipEntityByPartyId(delegator, orgPartyId);
			unitTree.add(rootParty);

			int sl = 1;

			for (GenericValue unit : unitTree) {
				double deptAmount = 0;
				boolean unitflag = false;

				String partyIdTo = unit.getString("partyIdTo");
				String departmentName = AttendanceUtils.getDepartmentName(
						delegator, partyIdTo);

				List<GenericValue> unitLevelPositions = AttendanceUtils
						.getPositionsByParyId(delegator, partyIdTo);

				List<Map<String, String>> employeeInfoList = FastList
						.newInstance();

				for (GenericValue unitLevelPosition : unitLevelPositions) {
					String emplPositionId = unitLevelPosition
							.getString("emplPositionId");

					String description = unitLevelPosition
							.getString("description");
					List<GenericValue> employeeList = AttendanceUtils
							.getTotalEmployeeByPositionId(delegator,
									emplPositionId);

					// Filter Employee by active status and card
					employeeList = AttendanceUtils
							.filterEmployeeWithCardAndStatus(delegator,
									employeeList);
					

					for (GenericValue employee : employeeList) {
						List<GenericValue> employeeLogs = new ArrayList<GenericValue>();
						try{
						String employeePartyId = employee.getString("partyId");
						String firstTiffinStartTime=null;
						String firstTiffinEndTime=null;
						String firstTiffinRate=null;
						String secondTiffinStartTime=null;
						String secondTiffinEndTime=null;
						String secondTiffinRate=null;
						String nightStartTime=null;
						String nightEndTime=null;
						String nightRate=null;
						
						GenericValue employeePreferance=UtilAttendancePreference.getEmployeePreference(employeePartyId, delegator, selectedDate, orgPartyId);						
						if(employeePreferance!=null && employeePreferance.size()>0){
							firstTiffinStartTime=(String)employeePreferance.get("firstTiffinStartTime");
							firstTiffinEndTime=(String)employeePreferance.get("firstTiffinEndTime");
							firstTiffinRate=(String)employeePreferance.get("firstTiffinRate");
							secondTiffinStartTime=(String)employeePreferance.get("secondTiffinStartTime");
							secondTiffinEndTime=(String)employeePreferance.get("secondTiffinEndTime");
							secondTiffinRate=(String)employeePreferance.get("secondTiffinRate");
							nightStartTime=(String)employeePreferance.get("nightStartTime");
							nightEndTime=(String)employeePreferance.get("nightEndTime");
							nightRate=(String)employeePreferance.get("nightRate");
						}
						
						if(UtilValidate.isEmpty(employeePreferance)){
						GenericValue designationPreferance=UtilAttendancePreference.getDesignationPreference(employeePartyId, delegator, selectedDate, orgPartyId);						
						if(designationPreferance!=null && designationPreferance.size()>0){
							firstTiffinStartTime=(String)designationPreferance.get("firstTiffinStartTime");
							firstTiffinEndTime=(String)designationPreferance.get("firstTiffinEndTime");
							firstTiffinRate=(String)designationPreferance.get("firstTiffinRate");
							secondTiffinStartTime=(String)designationPreferance.get("secondTiffinStartTime");
							secondTiffinEndTime=(String)designationPreferance.get("secondTiffinEndTime");
							secondTiffinRate=(String)designationPreferance.get("secondTiffinRate");
							nightStartTime=(String)designationPreferance.get("nightStartTime");
							nightEndTime=(String)designationPreferance.get("nightEndTime");
							nightRate=(String)designationPreferance.get("nightRate");
							}
						}
						String entryTime = AttendanceUtils.getEntryTime(
								employeePartyId, delegator, selectedDate,
								orgPartyId);
						Timestamp entryTimestamp = AttendanceUtils
								.getStringTimeToTimStamp(entryTime,
										selectedDate);
						dayEnd = new Timestamp(AttendanceUtils.getEndTime(selectedDate));	
						// Get TiffinTime Schedule from
						// EmployeePreference,RosterPreference and DayPreference
					/*	Map<String, String> tiffinScheduleMapForEmp = AttendanceUtils
								.getTiffinScheduleByEmpPartyIdFromEmpPreference(
										delegator, employeePartyId);
						Map<String, String> tiffinScheduleMapForRoster = AttendanceUtils
								.getTiffinScheduleByEmpPartyIdFromEmpPreference(
										delegator, employeePartyId);
						Map<String, String> tiffinScheduleMap = AttendanceUtils
								.filterTiffinScheduleMap(
										tiffinScheduleMapForOrganization,
										tiffinScheduleMapForSelectedDate,
										tiffinScheduleMapForEmp,
										tiffinScheduleMapForRoster);

						firstTiffinStartTime = tiffinScheduleMap
								.get("firstTiffinStartTime");
						firstTiffinEndTime = tiffinScheduleMap
								.get("firstTiffinEndTime");
						secondTiffinStartTime = tiffinScheduleMap
								.get("secondTiffinStartTime");
						secondTiffinEndTime = tiffinScheduleMap
								.get("secondTiffinEndTime");
						nightStartTime = tiffinScheduleMap
								.get("nightStartTime");
						nightEndTime = tiffinScheduleMap.get("nightEndTime");*/

						Timestamp firstTiffinStartTimestamp = null;
						Timestamp firstTiffinEndTimestamp = null;
						Timestamp secondTiffinStartTimestamp = null;
						Timestamp secondTiffinEndTimestamp = null;
						Timestamp nightStartTimestamp = null;
						Timestamp nightEndTimestamp = null;

						if (firstTiffinStartTime!=null) {
							firstTiffinStartTimestamp = AttendanceUtils
									.getStringTiffinTimeToTimStamp(
											firstTiffinStartTime, selectedDate);
						}
						if (firstTiffinEndTime!=null) {
							firstTiffinEndTimestamp = AttendanceUtils
									.getStringTiffinTimeToTimStamp(
											firstTiffinEndTime, selectedDate);
						}
						if (secondTiffinStartTime!=null) {
							secondTiffinStartTimestamp = AttendanceUtils
									.getStringTiffinTimeToTimStamp(
											secondTiffinStartTime, selectedDate);
						}
						if (secondTiffinEndTime!=null) {
							secondTiffinEndTimestamp = AttendanceUtils
									.getStringTiffinTimeToTimStamp(
											secondTiffinEndTime, selectedDate);
						}
						if (nightStartTime!=null) {
							nightStartTimestamp = AttendanceUtils
									.getStringTiffinTimeToTimStamp(
											nightStartTime, selectedDate);
						}
						if (nightEndTime!=null) {
							nightEndTimestamp = AttendanceUtils
									.getStringTiffinTimeToTimStamp(
											nightEndTime, selectedDate);
							dayEnd = nightEndTimestamp;
						}

						// For Day Beginning Employee Log
						if (entryTimestamp != null) {
							dayStart = entryTimestamp;
							dayStart = AttendanceUtils.addHourToTimestamp(
									dayStart, -1);
						}
/*						dayEnd = AttendanceUtils.addHourToTimestamp(dayEnd, -1);*/

						List<EntityExpr> exprs = FastList.newInstance();
						List<String> orderBy = FastList.newInstance();
						orderBy.add("logtimeStamp ASC");
						exprs.add(EntityCondition.makeCondition("logtimeStamp",
								EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
						exprs.add(EntityCondition.makeCondition("logtimeStamp",
								EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
						exprs.add(EntityCondition.makeCondition("partyId",
								EntityOperator.EQUALS, employeePartyId));
						try {
							employeeLogs = delegator.findList("EmployeeLog",
									EntityCondition.makeCondition(exprs,
											EntityOperator.AND), null, orderBy,
									null, true);

						} catch (GenericEntityException e) {
							logger.debug("Got Exception To get Employee Log In findJobCard Function---->"
									+ e);
							e.printStackTrace();
						}
						String in = "0";
						String out = "0";
						if (employeeLogs.size() >= 1) {

							GenericValue employeeLogExit = employeeLogs
									.get(employeeLogs.size() - 1);

							Timestamp exitTimestamp = (Timestamp) employeeLogExit
									.get("logtimeStamp");
							out = TWENTY_FOUR_TF.format(exitTimestamp);
							GenericValue employeeLogEntrry = employeeLogs
									.get(0);
							Timestamp entryimestamp = (Timestamp) employeeLogEntrry
									.get("logtimeStamp");
							in = TWENTY_FOUR_TF.format(entryimestamp);

							boolean firstTiffinFlag = false;
							boolean secondTiffinFlag = false;
							boolean nightTiffinFlag = false;
							if (firstTiffinStartTimestamp != null && exitTimestamp.getTime() > firstTiffinStartTimestamp.getTime()) {
								firstTiffinFlag = true;
							}
							if (secondTiffinStartTimestamp != null && exitTimestamp.getTime() > secondTiffinStartTimestamp.getTime()) {
								secondTiffinFlag = true;
							}
							if (nightStartTimestamp != null && exitTimestamp.getTime() > nightStartTimestamp.getTime()) {
								nightTiffinFlag = true;
							}
							if (firstTiffinFlag || secondTiffinFlag || nightTiffinFlag) {

								unitflag = true;

								// Get TiffinRate by emplPositionId
/*								Map<String, String> tiffinAndNightRare = AttendanceUtils
										.getTiffinAndNighitRateByEmpPositionId(
												delegator, emplPositionId);*/

								double firstTiffinRateInDouble = 0;
								if (firstTiffinRate!=null && firstTiffinRate.length()>0 && (firstTiffinFlag||secondTiffinFlag||nightTiffinFlag))  {
									firstTiffinRateInDouble = Double.parseDouble(firstTiffinRate);
									totalNumberOfFirstTiffin++;
								}
								double secondTiffinRateInDouble = 0;
								if (secondTiffinRate!=null && secondTiffinRate.length()>0 && (secondTiffinFlag||nightTiffinFlag))  {
									secondTiffinRateInDouble = Double.parseDouble(secondTiffinRate);
									totalNumberOfSecondTiffin++;
								}
								double nightRateInDouble = 0;
								if (nightRate!=null && nightRate.length()>0 && nightTiffinFlag)  {
									nightRateInDouble = Double.parseDouble(nightRate);
									totalNumberOfNightTiffin++;
								}
/*								int secondTiffinRate = 0;
								if (firstTiffinRate!=null && firstTiffinRate.length()>0) {
									secondTiffinRate = Integer
											.valueOf(tiffinAndNightRare
													.get("secondTiffinRate"));
								}
								int nightRate = 0;
								if (!tiffinAndNightRare.get("nightRate")
										.isEmpty()) {
									nightRate = Integer
											.valueOf(tiffinAndNightRare
													.get("nightRate"));
								}*/
								double amount = firstTiffinRateInDouble + secondTiffinRateInDouble+ nightRateInDouble;
								totalAmount=totalAmount+amount;
								deptAmount = deptAmount + amount;
								String employeeId = AttendanceUtils
										.getUserLoginIDFromPartyID(delegator,
												employeePartyId);
								String employeeName = AttendanceUtils
										.getEmployeeNameByPartyId(delegator,
												employeePartyId);

								Map<String, String> employeeInfo = FastMap
										.newInstance();
								employeeInfo.put("sl", sl + "");
								employeeInfo.put("employeeId", employeeId);
								employeeInfo.put("employeeName", employeeName);
								employeeInfo.put("in", in);
								employeeInfo.put("out", out);
								employeeInfo.put("firstTiffinRate",
										firstTiffinRate + "");
								employeeInfo.put("secondTiffinRate",
										secondTiffinRate + "");
								employeeInfo.put("nightRate", nightRate + "");
								employeeInfo.put("description", description);
								employeeInfo.put("departmentName",
										departmentName);
								employeeInfo.put("amount", amount + "");
								if(firstTiffinFlag || secondTiffinFlag || nightTiffinFlag){
									employeeInfo.put("hasFirstTiffin","Yes");
									if(secondTiffinFlag || nightTiffinFlag){
										employeeInfo.put("hasSecondTiffin","Yes");	
										if(nightTiffinFlag){
											employeeInfo.put("hasNightTiffin","Yes");
										}else{
											employeeInfo.put("hasNightTiffin","No");
										}
									}else{
										employeeInfo.put("hasSecondTiffin","No");
										employeeInfo.put("hasNightTiffin","No");
									}
									
								}else{
									employeeInfo.put("hasFirstTiffin","No");
									employeeInfo.put("hasSecondTiffin","No");
									employeeInfo.put("hasNightTiffin","No");
								}
								employeeInfoList.add(employeeInfo);

								sl++;
							}

						}
						
					  }catch(Exception e){
						  logger.debug("Exception in Tiffin Bill Calculation---->"+e);
					  }//end try catch

					}

				}
				if (unitflag) {

					Map<String, String> employeeInfo = FastMap.newInstance();
					employeeInfo.put("departmentName", departmentName);
					employeeInfo.put("deptAmount", deptAmount + "");
					employeeInfoList.add(employeeInfo);

					Map<String, List<Map<String, String>>> departmentEmployeeInfo = FastMap
							.newInstance();
					departmentEmployeeInfo.put("employeeInfoList",
							employeeInfoList);

					departmentEmployeeInfoList.add(departmentEmployeeInfo);

				}

			}

		}
		Map<String,Object> summeryDailyTiffinBillReport=FastMap.newInstance();
		summeryDailyTiffinBillReport.put("totalNumberOfFirstTiffin", totalNumberOfFirstTiffin);
		summeryDailyTiffinBillReport.put("totalNumberOfSecondTiffin", totalNumberOfSecondTiffin);
		summeryDailyTiffinBillReport.put("totalNumberOfNightTiffin", totalNumberOfNightTiffin);
		summeryDailyTiffinBillReport.put("totalAmount", totalAmount);
		List<Map<String,Object>> summeryDailyTiffinBillReportList=FastList.newInstance();
		summeryDailyTiffinBillReportList.add(summeryDailyTiffinBillReport);
		result.put("summeryDailyTiffinBillReportList", summeryDailyTiffinBillReportList);
		result.put("departmentEmployeeInfoList", departmentEmployeeInfoList);
		result.put("fromDate", fromDate);
		result.put("currentDate", currentDate);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE,
				"Employee Attendance Missing List");
		return result;
	}

	// End Tiffin bill report


	// Start New Empl Status Report
	public static Map<String, Object> findNewEmplStatusReport(
			DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		Delegator delegator = dctx.getDelegator();
		List<GenericValue> newEmplList = new ArrayList<GenericValue>();
		List<Map<String, String>> newEmplListInMap = FastList.newInstance();

		String fromDate = (String) context.get("fromDate");
		String toDate = (String) context.get("toDate");

		String orgPartyId = (String) context.get("orgPartyId");
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		Locale locale = (Locale) context.get("locale");

		java.sql.Date fromTime = null;
		java.sql.Date toTime = null;

		if ((fromDate != null && !fromDate.isEmpty())
				&& (toDate != null && !toDate.isEmpty())) {

			fromTime = Date.valueOf(fromDate);
			toTime = Date.valueOf(toDate);

			Timestamp dayStart = AttendanceUtils
					.getModifiedDayStart(new Timestamp(fromTime.getTime()));
			Timestamp dayEnd = AttendanceUtils.getModifiedDayEnd(new Timestamp(
					toTime.getTime()));

			logger.debug("FROM TIME:  sss " + dayStart + " TO TIME :  "
					+ dayEnd);

			List<EntityExpr> exprs = FastList.newInstance();
			List<String> orderBy = FastList.newInstance();
			orderBy.add("partyId ASC");
			exprs.add(EntityCondition.makeCondition("fromDate",
					EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
			exprs.add(EntityCondition.makeCondition("fromDate",
					EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			exprs.add(EntityCondition.makeCondition("partyIdFrom",
					EntityOperator.EQUALS, orgPartyId));
			try {
				// delegator.findList(entityName, entityCondition,
				// fieldsToSelect, orderBy, findOptions, useCache)
				newEmplList = delegator.findList("EmploymentAndPerson",
						EntityCondition
								.makeCondition(exprs, EntityOperator.AND),
						null, orderBy, null, true);
				long sl = 1;
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				for (GenericValue newValue : newEmplList) {
					Map<String, String> map = FastMap.newInstance();
					String partyId = newValue.get("partyId").toString();
					String emplId = AttendanceUtils.getUserLoginIDFromPartyID(
							delegator, partyId);
					String cardId = AttendanceUtils.getCardIdFromPartyId(
							delegator, partyId);
					List<String> deptNameInList = FastList.newInstance();
					deptNameInList = AttendanceUtils.getPartyDepartment(
							delegator, partyId, deptNameInList);
					String deptName = AttendanceUtils
							.makeDepartmentName(deptNameInList);
					String name = null;
					if (newValue.get("firstName") != null) {
						name = newValue.get("firstName").toString();
					}
					if (newValue.get("middleName") != null) {
						name = name + " "
								+ newValue.get("middleName").toString();
					}
					if (newValue.get("lastName") != null) {
						name = name + " " + newValue.get("lastName").toString();
					}
					String designation = AttendanceUtils.getPartyDesignation(
							delegator, partyId);
					if (newValue.get("fromDate") != null) {
						map.put("fromDate",
								sdf.format(newValue.get("fromDate")));
					} else {
						map.put("fromDate", "Not Available");
					}
					map.put("sl", String.valueOf(sl));
					map.put("name", name);
					map.put("cardId", cardId);
					map.put("emplId", emplId);
					map.put("partyId", partyId);
					map.put("designation", designation);
					map.put("deptName", deptName);
					sl++;
					newEmplListInMap.add(map);
				}
			} catch (Exception ex) {
				logger.debug("Exception to get empl list--->" + ex);
			}
		}
		logger.debug("newEmplList--->" + newEmplList.size());
		result.put("newEmplList", newEmplListInMap);
		result.put("selectedDate", fromDate);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE,
				"Employee Attendance Missing List");
		return result;
	}

	public static String exportNewEmplStatusReportToExcel(
			HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");
		List<Map<String, String>> newEmplList = (List<Map<String, String>>) request
				.getSession().getAttribute("newEmplList");

		if (newEmplList != null && !newEmplList.isEmpty()) {
			long currentTime = System.currentTimeMillis();
			String fileName = "NewEmployeeStatus" + String.valueOf(currentTime)
					+ ".xls";

			// create path for excel file
			String excelFilePath = UtilCommon.getAbsoluteFilePath(request,
					fileName);

			// prepare for excel
			List<String> columnHeaders = new ArrayList<String>();

			columnHeaders.add(0, "SL");
			columnHeaders.add(1, "EmplId");
			columnHeaders.add(2, "Name");
			columnHeaders.add(3, "Department");
			columnHeaders.add(4, "Designation");
			columnHeaders.add(5, "JoiningDate");
			columnHeaders.add(6, "RFID");
			columnHeaders.add(7, "Remarks");

			List<Map<String, String>> reportList = EmployeeExcelExportService
					.getNewEmplStatusReportInfo(delegator, newEmplList);
			List<List<String>> data = EmployeeExcelExportService
					.prepareExcelDataForReport(reportList, columnHeaders);

			try {
				ExcelUtils.saveToExcel2(excelFilePath, "newEmplList", data);
			} catch (IOException e) {
				logger.debug("Exception to write exportNewEmplStatusReportToExcel--->"
						+ e);
				e.printStackTrace();
			}

			return ExcelUtils.downloadExcel(fileName, request, response);
		} else {

			return "Data Empty";
		}
	}

	// End New Empl Status Report
	public static Map<String, Object> findDailyMonthlyMigration(
			DispatchContext dctx, Map<String, ? extends Object> context) {

		Map<String, Object> result = ServiceUtil.returnSuccess();

		// DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		Delegator delegator = dctx.getDelegator();

		String fromDate = (String) context.get("fromDate");
		String toDate = (String) context.get("toDate");
		String orgPartyId = (String) context.get("orgPartyId");
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		Locale locale = (Locale) context.get("locale");
		int size=0;
		List<Map<String, String>> dailyMonthlyMigrationList = FastList
				.newInstance();
		DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
		java.sql.Date fromTime = null;
		java.sql.Date toTime = null;

		// filter on division
		String division = (String) context.get("division");
		if (UtilValidate.isNotEmpty(division)) {
			orgPartyId = division;
		}

		// filter on department
		String department = (String) context.get("department");

		if (UtilValidate.isNotEmpty(department)) {
			orgPartyId = department;

		}

		// filter on section
		String section = (String) context.get("section");

		if (UtilValidate.isNotEmpty(section)) {
			orgPartyId = section;
		}
		// filter on line
		String line = (String) context.get("line");

		if (UtilValidate.isNotEmpty(line)) {
			orgPartyId = line;
		}

		List<GenericValue> partyList = FastList.newInstance();
		float totalResigned = 0;
		float dayInterval = 0;
		float averageResigned = 0;
		if (fromDate != null && !fromDate.isEmpty() && toDate != null
				&& !toDate.isEmpty()) {

			if (fromDate != null && !fromDate.isEmpty()) {
				try {
					fromTime = Date.valueOf(fromDate);
					toTime = Date.valueOf(toDate);
				} catch (Exception e) {
					fromTime = new Date(System.currentTimeMillis());
				}
			} else {
				fromTime = new Date(System.currentTimeMillis());
			}

			Timestamp dayStart = new Timestamp(
					AttendanceUtils.getStartTime(new Date(fromTime.getTime())));
			Timestamp dayEnd = new Timestamp(
					AttendanceUtils.getEndTime(new Date(toTime.getTime())));
			dayInterval = UtilDateTime.getIntervalInDays(dayStart, dayEnd);

			partyList = AttendanceUtils.getTotalEmployee(delegator, partyList,
					orgPartyId);

			partyList=AttendanceUtils.filterValidEmployeeListByDate(delegator, partyList, dayStart);
			// Get All Resigned Employees through fromTime to toTime
			List<GenericValue> inactiveList = AttendanceUtils
					.filterInActiveEmployeesByDate(delegator, partyList,
							dayStart, dayEnd);
			// Get total active employee at from date.
			/*partyList = AttendanceUtils.filterActiveEmployeesByDate(
					delegator,
					partyList,
					dayStart,
					new Timestamp(AttendanceUtils.getEndTime(new Date(dayStart
							.getTime()))));*/

			while (dayStart.getTime() <= dayEnd.getTime()) {

				Timestamp timeStart = new Timestamp(
						AttendanceUtils.getStartTime(new Date(dayStart
								.getTime())));
				Timestamp timeEnd = new Timestamp(
						AttendanceUtils.getEndTime(new Date(dayStart.getTime())));

				int value = AttendanceUtils.getInactiveEmployeeByDate(
						delegator, inactiveList, timeStart, timeEnd);
				totalResigned = totalResigned + value;
				Map<String, String> dateVersusResignedQuantity = FastMap
						.newInstance();
				String date = AttendanceUtils.FormatDate(new Date(timeStart
						.getTime()));
				dateVersusResignedQuantity.put("date", date);
				dateVersusResignedQuantity.put("value", value + "");
				dailyMonthlyMigrationList.add(dateVersusResignedQuantity);

				dayStart = UtilDateTime.addDaysToTimestamp(dayStart, 1);
			}

		}
		averageResigned = (Float) totalResigned / dayInterval;

		result.put("dailyMonthlyMigrationList", dailyMonthlyMigrationList);
		result.put("unitName",
				AttendanceUtils.getDepartmentName(delegator, orgPartyId));
		result.put("totalEmployee", String.valueOf(partyList.size()));
		result.put("totalResigned", String.valueOf((int) totalResigned));
		if(partyList.size()>0 && totalResigned>0){
			result.put("ResignedPercent", String.format("%.2f",(totalResigned * 100)/partyList.size()));
		}
		result.put("selectedDate", fromDate);
		result.put("selectedToDate", toDate);
		if (UtilValidate.isNotEmpty(fromTime)) {
			result.put("selectedDate", fmt.format(fromTime));
		} else {
			result.put("selectedDate", fromDate);

		}
		if (UtilValidate.isNotEmpty(fromTime)) {
			result.put("selectedToDate", fmt.format(toTime));
		} else {
			result.put("selectedToDate", toDate);

		}
		result.put("averageResigned", String.format("%.2f", averageResigned)
				+ "");
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE,
				"Employee Attendance Missing List");
		return result;
	}

	public static Map<String, Object> findManpowerPositionReport(
			DispatchContext dctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		Delegator delegator = dctx.getDelegator();

		String orgPartyId = (String) context.get("orgPartyId");

		return getDataForManpowerPosition(orgPartyId, delegator);

	}

	public static Map<String, Object> getDataForManpowerPosition(
			String orgPartyId, Delegator delegator)
			throws GenericEntityException {

		Map<String, Object> result = ServiceUtil.returnSuccess();
		int sl = 1;
		long currentTime = System.currentTimeMillis();
		Date currentDate = new Date(currentTime);
		DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
		logger.debug("orgPartyId-------->" + orgPartyId);

		List<Map<String, String>> manpowerList = FastList.newInstance();
		List<Map<String, String>> finalRowList = FastList.newInstance();
		List<GenericValue> empPosition = new ArrayList<GenericValue>();
		List<GenericValue> empPositionFullfilment = new ArrayList<GenericValue>();

		List<EntityExpr> exprsList = FastList.newInstance();
		exprsList.add(EntityCondition.makeCondition("partyId",
				EntityOperator.EQUALS, orgPartyId));

		/*
		 * empPosition = delegator.findList("EmplPosition",
		 * EntityCondition.makeCondition(exprsList, EntityOperator.AND), null,
		 * null, null, false);
		 */
		empPosition = AttendanceUtils.getTotalPositions(delegator, empPosition,
				orgPartyId);
		Map<String, Integer> GradeMale = new HashMap<String, Integer>();
		Map<String, Integer> GradeFemale = new HashMap<String, Integer>();
		Map<String, Integer> GradeUnspecified = new HashMap<String, Integer>();

		for (GenericValue empposition : empPosition) {

			String payGrade = empposition.getString("payGradeId");
			String empPositionId = empposition.getString("emplPositionId");

			exprsList.clear();
			exprsList.add(EntityCondition.makeCondition("emplPositionId",
					EntityOperator.EQUALS, empPositionId));

			if (payGrade != null) {

				empPositionFullfilment = delegator.findList(
						"EmplPositionFulfillment", EntityCondition
								.makeCondition(exprsList, EntityOperator.AND),
						null, null, null, false);
				int male = 0, female = 0, unspecified = 0;
				for (GenericValue fullfilment : empPositionFullfilment) {

					String employeeId = fullfilment.getString("partyId");
					GenericValue Person = delegator.findByPrimaryKey("Person",
							UtilMisc.toMap("partyId", employeeId.trim()));
					String gender = Person.getString("gender");
					if (gender != null) {
						if (gender.equals("M")) {
							male++;
						} else if (gender.equals("F")) {
							female++;
						}
					} else {
						unspecified++;
					}
				}

				if (GradeMale.containsKey(payGrade)) {
					GradeMale.put(payGrade, GradeMale.get(payGrade) + male);
				} else {
					GradeMale.put(payGrade, male);
				}
				if (GradeFemale.containsKey(payGrade)) {
					GradeFemale.put(payGrade, GradeFemale.get(payGrade)
							+ female);
				} else {
					GradeFemale.put(payGrade, female);
				}
				if (GradeUnspecified.containsKey(payGrade)) {
					GradeUnspecified.put(payGrade,
							GradeUnspecified.get(payGrade) + unspecified);
				} else {
					GradeUnspecified.put(payGrade, unspecified);
				}
			}
		}

		long totalMale = 0, totalFemale = 0, totalUnspecified = 0, grandTotal = 0;
		for (String s : GradeMale.keySet()) {

			Map<String, String> GradeList = FastMap.newInstance();

			GradeList.put("SL", String.valueOf(sl++));
			GradeList.put("Grade", s);
			GradeList.put("Male", GradeMale.get(s).toString());
			GradeList.put("Female", GradeFemale.get(s).toString());
			GradeList.put("Unspecified", GradeUnspecified.get(s).toString());
			totalMale += GradeMale.get(s);
			totalFemale += GradeFemale.get(s);
			totalUnspecified += GradeUnspecified.get(s);
			grandTotal += (GradeMale.get(s) + GradeFemale.get(s) + GradeUnspecified
					.get(s));

			GradeList.put(
					"Total",
					String.valueOf(GradeMale.get(s) + GradeFemale.get(s)
							+ GradeUnspecified.get(s)));

			logger.debug(s + "    Male: " + GradeMale.get(s) + " Female: "
					+ GradeFemale.get(s));
			manpowerList.add(GradeList);
		}

		Map<String, String> GrandGradeRow = FastMap.newInstance();

		GrandGradeRow.put("TotalMale", String.valueOf(totalMale));
		GrandGradeRow.put("TotalFemale", String.valueOf(totalFemale));
		GrandGradeRow.put("TotalUnspecified", String.valueOf(totalUnspecified));
		GrandGradeRow.put("GrandTotal", String.valueOf(grandTotal));

		finalRowList.add(GrandGradeRow);

		result.put("manpowerList", manpowerList);
		result.put("finalRowList", finalRowList);
		result.put("selectedDate", fmt.format(currentDate));
		return result;

	}

	public static String exportManpowerGradeToExcel(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException {

		String orgPartyId = (String) request.getParameter("orgPartyId");
		// Map<String,Object> result = FastMap.newInstance();

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request
				.getAttribute("dispatcher");
		DispatchContext context = dispatcher.getDispatchContext();

		/*
		 * Map<String,String> context=FastMap.newInstance();
		 * 
		 * context.put("orgPartyId", orgPartyId); try {
		 * result=dispatcher.runSync("findManpowerPositionReport", context); }
		 * catch (GenericServiceException e1) { // TODO Auto-generated catch
		 * block e1.printStackTrace(); }
		 */

		// List<Map<String, String>> manpowerList = (List<Map<String, String>>)
		// request.getSession().getAttribute("manpowerList");
		// DispatchContext dtx = (DispatchContext)
		// request.getRequestDispatcher("dctx");
		// GenericDelegator delegator = (GenericDelegator)
		// DelegatorFactory.getDelegator("default");

		logger.debug("ORGPARTYID " + orgPartyId);
		Map<String, Object> result = getDataForManpowerPosition(orgPartyId,
				delegator);

		List<Map<String, String>> manpowerList = FastList.newInstance();
		manpowerList = (List<Map<String, String>>) result.get("manpowerList");

		if (manpowerList != null && !manpowerList.isEmpty()) {
			long currentTime = System.currentTimeMillis();
			String fileName = "ManpowerGradeSummery"
					+ String.valueOf(currentTime) + ".xls";

			// create path for excel file
			String excelFilePath = UtilCommon.getAbsoluteFilePath(request,
					fileName);

			// prepare for excel
			List<String> columnHeaders = new ArrayList<String>();

			columnHeaders.add(0, "SL");
			columnHeaders.add(1, "Grade");
			columnHeaders.add(2, "Male");
			columnHeaders.add(3, "Female");
			columnHeaders.add(4, "Unspecified");
			columnHeaders.add(5, "Total");

			List<List<String>> data = EmployeeExcelExportService
					.prepareExcelDataForEmployeeAbsenceReport(manpowerList,
							columnHeaders);

			List<String> Row = new ArrayList<String>();
			long GrandTotal = 0;
			long totalMale = 0;
			long totalFemale = 0;
			long totalUnspecified = 0;
			for (Map<String, String> manPower : manpowerList) {
				GrandTotal += Integer.parseInt(manPower.get("Total"));
				totalMale += Integer.parseInt(manPower.get("Male"));
				totalFemale += Integer.parseInt(manPower.get("Female"));
				totalUnspecified += Integer.parseInt(manPower
						.get("Unspecified"));
			}
			/* logger.debug("GrandTotal"+GrandTotal); */
			Row.add(0, "");
			Row.add(1, "Grand-Total = ");
			Row.add(2, String.valueOf(totalMale));
			Row.add(3, String.valueOf(totalFemale));
			Row.add(4, String.valueOf(totalUnspecified));
			Row.add(5, String.valueOf(GrandTotal));

			data.add(Row);

			try {

				ExcelUtils.saveToExcel(excelFilePath, "ManpowerGradeSummery",
						data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return ExcelUtils.downloadExcel(fileName, request, response);
		} else {

			return "Data Empty";
		}
	}

	// Start Resigned Employee Report
	public static Map<String, Object> findResignedEmployeeReport(
			DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		Delegator delegator = dctx.getDelegator();
		List<GenericValue> resignedEmplList = new ArrayList<GenericValue>();
		List<Map<String, String>> resignedEmplListInMap = FastList
				.newInstance();
		String fromDate = (String) context.get("fromDate");
		String toDate = (String) context.get("toDate");
		String orgPartyId = (String) context.get("orgPartyId");
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		Locale locale = (Locale) context.get("locale");
		java.sql.Date fromDateTime = null;
		java.sql.Date toDateTime = null;
		DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

		String terminationReasonId = (String) context
				.get("terminationReasonId");
		String terminationTypeId = (String) context.get("terminationTypeId");

		/*
		 * logger.debug("terminationReasonId: "+terminationReasonId+
		 * " \nterminationTypeId: "+terminationTypeId);
		 */

		if (fromDate != null && !fromDate.isEmpty() && toDate != null
				&& !toDate.isEmpty()) {

			fromDateTime = Date.valueOf(fromDate);
			toDateTime = Date.valueOf(toDate);
			Timestamp dayStart = AttendanceUtils
					.getModifiedDayStart(new Timestamp(fromDateTime.getTime()));
			Timestamp dayEnd = AttendanceUtils.getModifiedDayEnd(new Timestamp(
					toDateTime.getTime()));

			List<EntityExpr> exprs = FastList.newInstance();
			List<String> orderBy = FastList.newInstance();
			orderBy.add("partyId ASC");
			exprs.add(EntityCondition.makeCondition("thruDate",
					EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
			exprs.add(EntityCondition.makeCondition("thruDate",
					EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			exprs.add(EntityCondition.makeCondition("partyIdFrom",
					EntityOperator.EQUALS, orgPartyId));
			try {
				// delegator.findList(entityName, entityCondition,
				// fieldsToSelect, orderBy, findOptions, useCache)
				resignedEmplList = delegator.findList("EmploymentAndPerson",
						EntityCondition
								.makeCondition(exprs, EntityOperator.AND),
						null, orderBy, null, true);
				long sl = 1;
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				for (GenericValue newValue : resignedEmplList) {
					Map<String, String> map = FastMap.newInstance();

					String terminationReasonIdE = (String) newValue
							.get("terminationReasonId");
					String terminationTypeIdE = (String) newValue
							.get("terminationTypeId");

					if ((!terminationReasonId.equals(terminationReasonIdE))
							&& (!terminationTypeId.equals(terminationTypeIdE))) {
						continue;
					}
					String partyId = newValue.get("partyId").toString();
					String emplId = AttendanceUtils.getUserLoginIDFromPartyID(
							delegator, partyId);
					String cardId = AttendanceUtils.getCardIdFromPartyId(
							delegator, partyId);
					List<String> deptNameInList = FastList.newInstance();
					deptNameInList = AttendanceUtils.getPartyDepartment(
							delegator, partyId, deptNameInList);
					logger.debug("deptNameInList---->" + deptNameInList);
					String deptName = AttendanceUtils
							.makeDepartmentName(deptNameInList);
					String name = null;
					if (newValue.get("firstName") != null) {
						name = newValue.get("firstName").toString();
					}
					if (newValue.get("middleName") != null) {
						name = name + " "
								+ newValue.get("middleName").toString();
					}
					if (newValue.get("lastName") != null) {
						name = name + " " + newValue.get("lastName").toString();
					}
					String designation = AttendanceUtils.getPartyDesignation(
							delegator, partyId);
					if (newValue.get("fromDate") != null) {
						map.put("fromDate",
								fmt.format(newValue.get("fromDate")));
					} else {
						map.put("fromDate", "Not Available");
					}
					if (newValue.get("thruDate") != null) {
						map.put("thruDate",
								fmt.format(newValue.get("thruDate")));
					} else {
						map.put("thruDate", "Not Available");
					}
					map.put("sl", String.valueOf(sl));
					map.put("name", name);
					map.put("cardId", cardId);
					map.put("emplId", emplId);
					map.put("partyId", partyId);
					map.put("designation", designation);
					map.put("deptName", deptName);
					map.put("terminationReasonId", terminationReasonIdE);
					map.put("terminationTypeId", terminationTypeIdE);
					sl++;
					resignedEmplListInMap.add(map);

				}
			} catch (Exception ex) {
				logger.debug("Exception to get empl list--->" + ex);
			}
		}
		logger.debug("resignedEmplList Size--->" + resignedEmplList.size());
		result.put("resignedEmplList", resignedEmplListInMap);
		/*
		 * result.put("fromDate", fromDate); result.put("toDate", toDate);
		 */
		if (UtilValidate.isNotEmpty(fromDateTime)) {
			result.put("fromDate", fmt.format(fromDateTime));
		} else {
			result.put("fromDate", fromDate);

		}
		if (UtilValidate.isNotEmpty(toDateTime)) {
			result.put("toDate", fmt.format(toDateTime));
		} else {
			result.put("toDate", toDate);

		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE,
				"Employee Attendance Missing List");
		return result;
	}

	public static String exportResignedEmployeeReportToExcel(
			HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");
		List<Map<String, String>> resignedEmplList = (List<Map<String, String>>) request
				.getSession().getAttribute("resignedEmplList");

		if (resignedEmplList != null && !resignedEmplList.isEmpty()) {
			long currentTime = System.currentTimeMillis();
			String fileName = "resignedEmplList" + String.valueOf(currentTime)
					+ ".xls";

			// create path for excel file
			String excelFilePath = UtilCommon.getAbsoluteFilePath(request,
					fileName);

			// prepare for excel
			List<String> columnHeaders = new ArrayList<String>();

			columnHeaders.add(0, "SL");
			columnHeaders.add(1, "EmplId");
			columnHeaders.add(2, "Name");
			columnHeaders.add(3, "Department");
			columnHeaders.add(4, "Designation");
			columnHeaders.add(5, "JoiningDate");
			columnHeaders.add(6, "ResignedDate");
			columnHeaders.add(7, "RFID");
			columnHeaders.add(8, "Remarks");

			List<Map<String, String>> reportList = EmployeeExcelExportService
					.getResignedEmployeeReportInfo(delegator, resignedEmplList);
			List<List<String>> data = EmployeeExcelExportService
					.prepareExcelDataForReport(reportList, columnHeaders);

			try {
				ExcelUtils
						.saveToExcel2(excelFilePath, "resignedEmplList", data);
			} catch (IOException e) {
				logger.debug("Exception to write exportNewEmplStatusReportToExcel--->"
						+ e);
				e.printStackTrace();
			}

			return ExcelUtils.downloadExcel(fileName, request, response);
		} else {

			return "Data Empty";
		}
	}

	// End New Empl Status Report

	// OTHolidayAndCostReport
	public static Map<String, Object> findOTHolidayAndCostReport(
			DispatchContext dctx, Map<String, ? extends Object> context) {

		Map<String, Object> result = ServiceUtil.returnSuccess();

		Delegator delegator = dctx.getDelegator();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
		String selectMonthYear = (String) context.get("selectMonthYear");
		
		String orgPartyId = (String) context.get("orgPartyId");
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		Locale locale = (Locale) context.get("locale");

		List<Map<String, String>> oTHolidayAndCostReports = FastList
				.newInstance();

		long subTotalHours = 0;
		long subTotalManPower = 0;
		float subTotalCosts = 0;

		if (selectMonthYear != null && !selectMonthYear.isEmpty()) {

			String extraOTallowances = "";
			String extraOTStartTime = "";
			String extraOTEndTime = "";

			java.sql.Date fromTime = AttendanceUtils
					.getsqltDate(selectMonthYear);

			Calendar curretCalender = Calendar.getInstance();
			String currentMonth = new SimpleDateFormat("MMMM")
					.format(curretCalender.getTime());
			String currentYear = new SimpleDateFormat("yyyy")
					.format(curretCalender.getTime());
			String selectedMonth = new SimpleDateFormat("MMMM")
					.format(fromTime);
			String selectedYear = new SimpleDateFormat("yyyy").format(fromTime);
			String currentDay = new SimpleDateFormat("dd")
					.format(curretCalender.getTime());

			// Find last date of month from calendar
			Calendar cal = Calendar.getInstance();
			cal.setTime(fromTime);
			if (currentMonth.equalsIgnoreCase(selectedMonth)
					&& currentYear.equalsIgnoreCase(selectedYear)) {
				cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(currentDay));
			} else {
				cal.set(Calendar.DAY_OF_MONTH,
						cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			}

			java.sql.Date toTime = new java.sql.Date(cal.getTime().getTime());

			Timestamp dayStart = new Timestamp(
					AttendanceUtils.getStartTime(new Date(fromTime.getTime())));
			Timestamp dayEnd = new Timestamp(
					AttendanceUtils.getEndTime(new Date(toTime.getTime())));

			List<Map<String, String>> partyList = FastList.newInstance();

			partyList = AttendanceUtils.getEmployeesWithBasicSalary(delegator,
					partyList, orgPartyId);

			// Get all holidays
			List<Timestamp> holidays = new ArrayList<Timestamp>();
			holidays = AttendanceUtils.getHolidays(orgPartyId, dayStart,
					dayEnd, delegator);

			while (dayStart.getTime() <= dayEnd.getTime()) {
				
				java.sql.Date preDate = new java.sql.Date(dayStart.getTime());

			
				GenericValue dayPreference=null;
				try {
					dayPreference = UtilAttendancePreference.getDayPreference(delegator, preDate, orgPartyId);
				} catch (GenericEntityException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// Check Day Preference
				if (dayPreference != null) {
					if (dayPreference.get("extraOTallowances") != null) {
						extraOTallowances = (String) dayPreference
								.get("extraOTallowances");
					}
					if (dayPreference.get("extraOTStartTime") != null) {
						extraOTStartTime = (String) dayPreference
								.get("extraOTStartTime");
					}
					if (dayPreference.get("extraOTEndTime") != null) {
						extraOTEndTime = (String) dayPreference
								.get("extraOTEndTime");
					}
				}
				boolean isHoliyDay = false;
				List<String> sections = FastList.newInstance();
				isHoliyDay = AttendanceUtils.checkHoliday(holidays, dayStart);

				long eotHours = 0;
				long manPower = 0;
				float totalCost = 0;

				for (Map<String, String> party : partyList) {

					String partyId = party.get("partyId");
					String basicSalary = party.get("basicSalary");

					Timestamp dayStartTime = new Timestamp(
							AttendanceUtils.getStartTime(new Date(dayStart
									.getTime())));
					Timestamp dayEndTime = new Timestamp(
							AttendanceUtils.getEndTime(new Date(dayStart
									.getTime())));

					boolean activeStatus = AttendanceUtils
							.checkEmployeeStatusByDate(delegator, partyId,
									dayStartTime, dayEndTime);
					if (activeStatus) {
						continue;
					}

					// Check Employee and Roster Preference
					if (dayPreference == null) {

						try {
							extraOTallowances = UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, preDate, orgPartyId, UtilPreferenceProperties.EXTRAOTALLOWANCES);
							extraOTStartTime = UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, preDate, orgPartyId, UtilPreferenceProperties.EXTRAOTSTARTTIME);
							extraOTEndTime = UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, preDate, orgPartyId, UtilPreferenceProperties.EXTRAOTENDTIME);

						} catch (GenericEntityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					if (extraOTallowances.equalsIgnoreCase("Y")) {

						long extraOTStartTimeInDate = AttendanceUtils
								.getStrngTimeInDate(extraOTStartTime, new Date(
										dayStart.getTime()));
						long extraOTEndTimeInDate = AttendanceUtils
								.getStrngTimeInDate(extraOTEndTime, new Date(
										dayStart.getTime()));

						List<EntityExpr> exprs = FastList.newInstance();
						List<String> orderBy = FastList.newInstance();
						orderBy.add("logtimeStamp ASC");

						Set<String> fieldsToSelect = FastSet.newInstance();
						fieldsToSelect.add("logtimeStamp");

						exprs.add(EntityCondition.makeCondition("logtimeStamp",
								EntityOperator.GREATER_THAN_EQUAL_TO,
								new Timestamp(dayStartTime.getTime())));
						exprs.add(EntityCondition.makeCondition("logtimeStamp",
								EntityOperator.LESS_THAN_EQUAL_TO,
								new Timestamp(dayEndTime.getTime())));
						exprs.add(EntityCondition.makeCondition("partyId",
								EntityOperator.EQUALS, partyId));
							
						List<GenericValue> employeeLogs = new ArrayList<GenericValue>();

						try {
							// delegator.findList(entityName, entityCondition,
							// fieldsToSelect, orderBy, findOptions, useCache)
							employeeLogs = delegator.findList("EmployeeLog",
									EntityCondition.makeCondition(exprs,
											EntityOperator.AND),
									fieldsToSelect, orderBy, null, true);
						} catch (GenericEntityException e) {
							e.printStackTrace();
						}

						if (employeeLogs.size() > 1) {

							GenericValue employeeLogEntrry = employeeLogs
									.get(0);
							Timestamp entrytime = (Timestamp) employeeLogEntrry
									.get("logtimeStamp");

							GenericValue employeeLog = employeeLogs
									.get(employeeLogs.size() - 1);
							Timestamp exitTime = (Timestamp) employeeLog
									.get("logtimeStamp");

							// For working day
							if (exitTime.getTime() > extraOTStartTimeInDate
									&& !isHoliyDay) {
								long eotHour = (exitTime.getTime() - extraOTStartTimeInDate) / 3600000;
								long eotMinute = ((exitTime.getTime() - extraOTStartTimeInDate) % 3600000) / 60000;
								float cost = 0;
								if (eotMinute >= 50) {
									eotHour = eotHour + 1;
								}
								if (eotHour > 0) {
									List<String> departmentNameList = FastList
											.newInstance();
									departmentNameList = AttendanceUtils
											.getPartyDepartment(delegator,
													partyId, departmentNameList);
									String department = AttendanceUtils
											.getDepartmentName(departmentNameList);
									if (!AttendanceUtils
											.checkDepartmentExistance(sections,
													department)) {
										sections.add(department);
									}
									manPower = manPower + 1;
									eotHours = eotHours + eotHour;
									if (basicSalary != null
											&& !basicSalary.isEmpty()) {
										float eotRate = Float
												.valueOf(basicSalary) / 104;
										cost = eotRate * eotHour;
										totalCost = totalCost + cost;
									}

								}
							}// For Holiy day
							else if (isHoliyDay) {
								long eotHour = (exitTime.getTime() - entrytime
										.getTime()) / 3600000;
								long eotMinute = ((exitTime.getTime() - entrytime
										.getTime()) % 3600000) / 60000;
								float cost = 0;
								if (eotMinute >= 50) {
									eotHour = eotHour + 1;
								}
								if (eotHour > 0) {
									List<String> departmentNameList = FastList
											.newInstance();
									departmentNameList = AttendanceUtils
											.getPartyDepartment(delegator,
													partyId, departmentNameList);
									String department = AttendanceUtils
											.getDepartmentName(departmentNameList);
									if (!AttendanceUtils
											.checkDepartmentExistance(sections,
													department)) {
										sections.add(department);
									}
									manPower = manPower + 1;
									eotHours = eotHours + eotHour;
									if (basicSalary != null
											&& !basicSalary.isEmpty()) {
										float eotRate = Float
												.valueOf(basicSalary) / 104;
										cost = eotRate * eotHour;
										totalCost = totalCost + cost;
									}
								}
							}
						}

					}
				}

				String section = AttendanceUtils.makeSectionName(sections);

				Map<String, String> oTHolidayAndCostReport = FastMap
						.newInstance();
				oTHolidayAndCostReport.put("date", dateFormat.format(dayStart));
				oTHolidayAndCostReport.put("hour", eotHours + "");
				oTHolidayAndCostReport.put("manPower", manPower + "");
				oTHolidayAndCostReport.put("section", section);
				oTHolidayAndCostReport.put("totalCost", Math.round(totalCost)
						+ "");

				oTHolidayAndCostReports.add(oTHolidayAndCostReport);

				dayStart = UtilDateTime.addDaysToTimestamp(dayStart, 1);
				subTotalHours = subTotalHours + eotHours;
				subTotalManPower = subTotalManPower + manPower;
				subTotalCosts = subTotalCosts + totalCost;
			}

		}
		result.put("oTHolidayAndCostReports", oTHolidayAndCostReports);
		result.put("subTotalHours", subTotalHours + "");
		result.put("subTotalManPower", Math.round(subTotalManPower) + "");
		result.put("subTotalCosts", Math.round(subTotalCosts) + "");
		result.put("selectMonthYear", selectMonthYear);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE,
				"Employee Attendance Missing List");
		return result;
	}

	
	/*public static Map<String, Object> findLeavesummeryProbition(
			DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		Delegator delegator = dctx.getDelegator();

		String fromDate = (String) context.get("fromDate");
		String toDate = (String) context.get("toDate");
		String orgPartyId = (String) context.get("orgPartyId");
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		Locale locale = (Locale) context.get("locale");

		List<Map<String, String>> probitionaryLeaveList = FastList
				.newInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
     	SimpleDateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd");

		int sl = 0;
		String Name = "";
		String Designation = "";
		String Department = "";
		java.sql.Date fromTime = null;
		java.sql.Date toTime = null;
		java.sql.Date cofirmDate = null;
		java.sql.Date leaveFromDateTime = null;
		java.sql.Date leavethruDateTime = null;

		logger.debug("orgPartyId-------->" + orgPartyId);

		if (fromDate != null && !fromDate.isEmpty() && toDate != null
				&& !toDate.isEmpty()) {

			if (fromDate != null && !fromDate.isEmpty() && toDate != null
					&& !toDate.isEmpty()) {

				fromTime = Date.valueOf(fromDate);
				toTime = Date.valueOf(toDate);
			}

			Timestamp dayStart = UtilDateTime.getDayStart(new Timestamp(
					fromTime.getTime()));
			Timestamp dayEnd = UtilDateTime.getDayEnd(new Timestamp(toTime
					.getTime()));

			List<GenericValue> leaveSummaryInfo = new ArrayList<GenericValue>();
			List<EntityExpr> exprs = FastList.newInstance();
			List<String> orderBy = FastList.newInstance();
			orderBy.add("fromDate ASC");
			exprs.add(EntityCondition.makeCondition("fromDate",
					EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
			exprs.add(EntityCondition.makeCondition("thruDate",
					EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			exprs.add(EntityCondition.makeCondition("partyIdFrom",
					EntityOperator.EQUALS, orgPartyId));
			exprs.add(EntityCondition.makeCondition("leaveStatus",
					EntityOperator.EQUALS, "LEAVE_APPROVED"));

			try {

				leaveSummaryInfo = delegator.findList(
						"EmpLeaveAndOrganization", EntityCondition
								.makeCondition(exprs, EntityOperator.AND),
						null, orderBy, null, true);

				if (leaveSummaryInfo.size() > 0) {

					for (GenericValue leave : leaveSummaryInfo) {
						Map<String, String> leaveInfo = FastMap.newInstance();
						String partyId = leave.getString("partyId");
						//String leaveFormDate = leave.getString("fromDate");
						//String leaveThruDate = leave.getString("thruDate");						
						Timestamp leaveFormDate = leave.getTimestamp("fromDate");
						Timestamp leaveThruDate = leave.getTimestamp("thruDate");
						
						String confirmationDate= RecruitmentServices.getEmplConfirmationDate( delegator, partyId, orgPartyId);
						cofirmDate = Date.valueOf(confirmationDate);
						//leaveFromDateTime= dfmt.format(leaveFormDate1);
					    leaveFromDateTime = Date.valueOf(dfmt.format(leaveFormDate));
						//leavethruDateTime = Date.valueOf(leaveThruDate.trim());
						
						List<GenericValue> employementflag = delegator.findByAnd("EmplPositionFulfillment", UtilMisc.toMap("partyId",partyId));
						String isProbationaryFlag = EntityUtil.getFirst(employementflag).getString("isProbationaryFlag");
						if(UtilValidate.isEmpty(isProbationaryFlag) && (cofirmDate.getTime()<=leaveFromDateTime.getTime())){
							continue;
						}
						
						sl++;
						
						List<GenericValue> userList = delegator.findByAnd(
								"UserLogin",
								UtilMisc.toMap("partyId", partyId.trim()));
						GenericValue user = EntityUtil.getFirst(userList);
						String userLoginId = user.get("userLoginId").toString();
						leaveInfo.put("PIN", userLoginId);

						GenericValue person = delegator.findByPrimaryKey(
								"Person",
								UtilMisc.toMap("partyId", partyId.trim()));

						if (!UtilValidate.isEmpty(person)) {
							Name = AttendanceUtils
									.avoidNullPointerException((String) person
											.get("firstName"))
									+ " "
									+ AttendanceUtils
											.avoidNullPointerException((String) person
													.get("middleName"))
									+ " "
									+ AttendanceUtils
											.avoidNullPointerException((String) person
													.get("lastName"));
							leaveInfo.put("EmployeeName", Name);

						}
						leaveInfo.put("SL", Integer.toString(sl));
						Designation = AttendanceUtils.getPartyDesignation(
								delegator, partyId);
						leaveInfo.put("Designation", Designation);
						List<String> departmentNameList = FastList
								.newInstance();
						departmentNameList = AttendanceUtils
								.getPartyDepartment(delegator, partyId,
										departmentNameList);
						Department = AttendanceUtils
								.getDepartmentName(departmentNameList);
						leaveInfo.put("Department", Department);
						List<GenericValue> employement = delegator.findByAnd(
								"Employment",
								UtilMisc.toMap("partyIdTo", partyId.trim()));

						Timestamp joinDate = EntityUtil.getFirst(employement).getTimestamp("fromDate");
						leaveInfo.put("JoinDate", dateFormat.format(joinDate));
						leaveInfo.put("LeaveEnjoyedDate",
								dateFormat.format(leaveFormDate)+" to"+"\n"+dateFormat.format(leaveThruDate));


						GenericValue leaveTypes = delegator.findByPrimaryKey(
								"EmplLeaveType",
								UtilMisc.toMap("leaveTypeId",
										leave.getString("leaveTypeId").trim()));
						String leaveTypeName = leaveTypes
								.getString("description");
						leaveInfo.put("LeaveType", leaveTypeName);
						leaveInfo.put("Remarks", "");
						

						probitionaryLeaveList.add(leaveInfo);

					}

				}

			} catch (GenericEntityException e) {
				e.printStackTrace();
			}

		}

		result.put("probitionLeaveList", probitionaryLeaveList);
		if (UtilValidate.isNotEmpty(fromTime)) {
			result.put("selectedDate", dateFormat.format(fromTime));
		} else {
			result.put("selectedDate", fromDate);

		}
		if (UtilValidate.isNotEmpty(toTime)) {
			result.put("selectedToDate", dateFormat.format(toTime));
		} else {
			result.put("selectedToDate", toDate);

		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE,
				"Employee Leave Summery Report");

		return result;
	}*/
	
	
	

	/*public static String exportLeavesummeryProbitionToExcel(
			HttpServletRequest request, HttpServletResponse response) {

		@SuppressWarnings("unchecked")
		List<Map<String, String>> probitionLeaveSList = (List<Map<String, String>>) request
				.getSession().getAttribute("probitionLeaveList");
		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");
		if (probitionLeaveSList != null && !probitionLeaveSList.isEmpty()) {
			long currentTime = System.currentTimeMillis();
			String fileName = "probitionLeaveSummeryReport_"
					+ String.valueOf(currentTime) + ".xls";

			// create path for excel file
			String excelFilePath = UtilCommon.getAbsoluteFilePath(request,
					fileName);

			// prepare for excel
			List<String> columnHeaders = new ArrayList<String>();
			columnHeaders.add(0, "SL");
			columnHeaders.add(1, "PIN");
			columnHeaders.add(2, "EmployeeName");
			columnHeaders.add(3, "Designation");
			columnHeaders.add(4, "Department");
			columnHeaders.add(5, "JoinDate");
			columnHeaders.add(6, "LeaveEnjoyedDate");
			columnHeaders.add(7, "LeaveType");
			columnHeaders.add(8, "Remarks");
			List<Map<String, String>> reportList = EmployeeExcelExportService
					.getprobitionLeaveReportInfo(delegator, probitionLeaveSList);
			List<List<String>> data = EmployeeExcelExportService
					.prepareExcelDataForLeaveProbitionReport(reportList,
							columnHeaders);

			try {
				ExcelUtils.saveToExcel(excelFilePath,
						"probitionLeaveSummeryList", data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return ExcelUtils.downloadExcel(fileName, request, response);
		} else {

			return "Data Empty";
		}
	}*/


	public static Map<String, Object> findLeavesummeryDesignation(
			DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		Delegator delegator = dctx.getDelegator();

		String fromDate = (String) context.get("fromDate");
		String toDate = (String) context.get("toDate");
		String orgPartyId = (String) context.get("orgPartyId");
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		Locale locale = (Locale) context.get("locale");

		List<Map<String, String>> designationWiseLeaveSummary = FastList
				.newInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		int sl = 0;
		String Name = "";
		String Designation = "";
		String Department = "";
		java.sql.Date fromTime = null;
		java.sql.Date toTime = null;

		logger.debug("orgPartyId-------->" + orgPartyId);

		if (fromDate != null && !fromDate.isEmpty() && toDate != null
				&& !toDate.isEmpty()) {

			if (fromDate != null && !fromDate.isEmpty() && toDate != null
					&& !toDate.isEmpty()) {

				fromTime = Date.valueOf(fromDate);
				toTime = Date.valueOf(toDate);
			}

			Timestamp dayStart = UtilDateTime.getDayStart(new Timestamp(
					fromTime.getTime()));
			Timestamp dayEnd = UtilDateTime.getDayEnd(new Timestamp(toTime
					.getTime()));

			List<GenericValue> leaveSummaryInfo = new ArrayList<GenericValue>();
			List<EntityExpr> exprs = FastList.newInstance();
			List<String> orderBy = FastList.newInstance();
			orderBy.add("fromDate ASC");
			exprs.add(EntityCondition.makeCondition("fromDate",
					EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
			exprs.add(EntityCondition.makeCondition("thruDate",
					EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			exprs.add(EntityCondition.makeCondition("partyIdFrom",
					EntityOperator.EQUALS, orgPartyId));
			exprs.add(EntityCondition.makeCondition("leaveStatus",
					EntityOperator.EQUALS, "LEAVE_APPROVED"));

			try {

				leaveSummaryInfo = delegator.findList(
						"EmpLeaveAndOrganization", EntityCondition
								.makeCondition(exprs, EntityOperator.AND),
						null, orderBy, null, true);

				if (leaveSummaryInfo.size() > 0) {

					for (GenericValue leave : leaveSummaryInfo) {
						sl++;
						Map<String, String> leaveInfo = FastMap.newInstance();
						String partyId = leave.getString("partyId");
						List<GenericValue> userList = delegator.findByAnd(
								"UserLogin",
								UtilMisc.toMap("partyId", partyId.trim()));
						GenericValue user = EntityUtil.getFirst(userList);
						String userLoginId = user.get("userLoginId").toString();
						leaveInfo.put("PIN", userLoginId);

						GenericValue person = delegator.findByPrimaryKey(
								"Person",
								UtilMisc.toMap("partyId", partyId.trim()));

						if (!UtilValidate.isEmpty(person)) {
							Name = AttendanceUtils
									.avoidNullPointerException((String) person
											.get("firstName"))
									+ " "
									+ AttendanceUtils
											.avoidNullPointerException((String) person
													.get("middleName"))
									+ " "
									+ AttendanceUtils
											.avoidNullPointerException((String) person
													.get("lastName"));
							leaveInfo.put("EmployeeName", Name);

						}
						leaveInfo.put("SL", Integer.toString(sl));
						Designation = AttendanceUtils.getPartyDesignation(
								delegator, partyId);
						leaveInfo.put("Designation", Designation);
						List<String> departmentNameList = FastList
								.newInstance();
						departmentNameList = AttendanceUtils
								.getPartyDepartment(delegator, partyId,
										departmentNameList);
						Department = AttendanceUtils
								.getDepartmentName(departmentNameList);
						leaveInfo.put("Department", Department);

						GenericValue leaveTypes = delegator.findByPrimaryKey(
								"EmplLeaveType",
								UtilMisc.toMap("leaveTypeId",
										leave.getString("leaveTypeId").trim()));
						String leaveTypeName = leaveTypes
								.getString("description");
						String maxAvailableLeave = leaveTypes
								.getString("maxAvailableLeave");
						leaveInfo.put("LeaveType", leaveTypeName);
						leaveInfo.put("MaxAvailableLeave", maxAvailableLeave);
						String enjoyedDay = getDifferenceBetweenTwoDate(
								dayStart, dayEnd);
						leaveInfo.put("Enjoyed", enjoyedDay);
						int Balance = Integer.parseInt(maxAvailableLeave)
								- Integer.parseInt(enjoyedDay);
						String balance = "" + Balance;
						leaveInfo.put("Balance", balance);

						designationWiseLeaveSummary.add(leaveInfo);

					}

				}

			} catch (GenericEntityException e) {
				e.printStackTrace();
			}

		}

		result.put("designationWiseLeaveList", designationWiseLeaveSummary);
		if (UtilValidate.isNotEmpty(fromTime)) {
			result.put("selectedDate", dateFormat.format(fromTime));
		} else {
			result.put("selectedDate", fromDate);

		}
		if (UtilValidate.isNotEmpty(fromTime)) {
			result.put("selectedToDate", dateFormat.format(toTime));
		} else {
			result.put("selectedToDate", toDate);

		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE,
				"Employee Leave Summery Report");

		return result;
	}



	public static String getDifferenceBetweenTwoDate(Timestamp startTime,
			Timestamp stopTime) {
		Date startDate = new Date(startTime.getTime());
		Date endDate = new Date(stopTime.getTime());
		int difInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
		return "" + difInDays;
	}

	public static String exportLeavesummeryDesignationToExcel(
			HttpServletRequest request, HttpServletResponse response) {

		@SuppressWarnings("unchecked")
		List<Map<String, String>> probitionLeaveSList = (List<Map<String, String>>) request
				.getSession().getAttribute("designationWiseLeaveList");
		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");
		if (probitionLeaveSList != null && !probitionLeaveSList.isEmpty()) {
			long currentTime = System.currentTimeMillis();
			String fileName = "DesignationWiseLeaveReport_"
					+ String.valueOf(currentTime) + ".xls";

			// create path for excel file
			String excelFilePath = UtilCommon.getAbsoluteFilePath(request,
					fileName);

			// prepare for excel
			List<String> columnHeaders = new ArrayList<String>();
			columnHeaders.add(0, "Designation");
			columnHeaders.add(1, "SL");
			columnHeaders.add(2, "PIN");
			columnHeaders.add(3, "EmployeeName");
			columnHeaders.add(4, "Department");
			columnHeaders.add(5, "LeaveType");
			columnHeaders.add(6, "MaxAvailableLeave");
			columnHeaders.add(7, "Enjoyed");
			columnHeaders.add(8, "Balance");

			List<Map<String, String>> reportList = EmployeeExcelExportService
					.getDesignationLeaveReportInfo(delegator,
							probitionLeaveSList);
			List<List<String>> data = EmployeeExcelExportService
					.prepareExcelDataForLeaveDesignationReport(reportList,
							columnHeaders);

			try {
				ExcelUtils.saveToExcel(excelFilePath,
						"designationWiseLeaveList", data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return ExcelUtils.downloadExcel(fileName, request, response);
		} else {

			return "Data Empty";
		}
	}

	// Start Parvez Code -->
	public static Map<String, Object> findLostCardInfo(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		Delegator delegator = dctx.getDelegator();

		String fromDate = (String) context.get("fromDate");
		String toDate = (String) context.get("toDate");
		String orgPartyId = (String) context.get("orgPartyId");
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		Locale locale = (Locale) context.get("locale");

		List<Map<String, String>> cardLostList = FastList.newInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		int sl = 0;
		String PIN = "";
		String Name = "";
		String Designation = "";
		String Department = "";
		String CardNo = "";
		String IssuedDate = "";
		String LostDate = "";
		java.sql.Date fromTime = null;
		java.sql.Date toTime = null;

		logger.debug("orgPartyId-------->" + orgPartyId);

		if (fromDate != null && !fromDate.isEmpty() && toDate != null
				&& !toDate.isEmpty()) {

			if (fromDate != null && !fromDate.isEmpty() && toDate != null
					&& !toDate.isEmpty()) {

				fromTime = Date.valueOf(fromDate);
				toTime = Date.valueOf(toDate);

			}

			Timestamp dayStart = UtilDateTime.getDayStart(new Timestamp(
					fromTime.getTime()));
			Timestamp dayEnd = UtilDateTime.getDayEnd(new Timestamp(toTime
					.getTime()));

			List<GenericValue> cardLostInfo = new ArrayList<GenericValue>();
			List<EntityExpr> exprs = FastList.newInstance();
			List<String> orderBy = FastList.newInstance();
			orderBy.add("lostDate ASC");
			exprs.add(EntityCondition.makeCondition("lostDate",
					EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
			exprs.add(EntityCondition.makeCondition("lostDate",
					EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			exprs.add(EntityCondition.makeCondition("partyIdFrom",
					EntityOperator.EQUALS, orgPartyId));
			exprs.add(EntityCondition.makeCondition(EntityCondition
					.makeCondition("logisticsItemTypeId",
							EntityOperator.EQUALS, "ID_CARD"),
					EntityOperator.OR, EntityCondition.makeCondition(
							"logisticsItemTypeId", EntityOperator.EQUALS,
							"PROXIMITY_CARD")));

			try {

				cardLostInfo = delegator.findList(
						"LogisticsItemPartyRelationshipView", EntityCondition
								.makeCondition(exprs, EntityOperator.AND),
						null, orderBy, null, true);

				if (cardLostInfo.size() > 0) {

					for (GenericValue lostCard : cardLostInfo) {
						sl++;
						Map<String, String> lostInfo = FastMap.newInstance();
						// String partyId =lostCard.get("issuedTo").toString();
						String userLoginId = lostCard.get("issuedTo")
								.toString();

						// get employeeId from EmploymentAndPersonAndUserLogin
						// entity

						List<GenericValue> userList = delegator.findByAnd(
								"UserLogin",
								UtilMisc.toMap("userLoginId", userLoginId));
						GenericValue user = EntityUtil.getFirst(userList);

						String partyId = user.get("partyId").toString();

						lostInfo.put("PIN", userLoginId);

						GenericValue person = delegator.findByPrimaryKey(
								"Person",
								UtilMisc.toMap("partyId", partyId.trim()));

						if (!UtilValidate.isEmpty(person)) {
							Name = AttendanceUtils
									.avoidNullPointerException((String) person
											.get("firstName"))
									+ " "
									+ AttendanceUtils
											.avoidNullPointerException((String) person
													.get("middleName"))
									+ " "
									+ AttendanceUtils
											.avoidNullPointerException((String) person
													.get("lastName"));
							lostInfo.put("Name", Name);

						}
						lostInfo.put("sl", Integer.toString(sl));
						Designation = AttendanceUtils.getPartyDesignation(
								delegator, partyId);
						lostInfo.put("Designation", Designation);
						List<String> departmentNameList = FastList
								.newInstance();
						departmentNameList = AttendanceUtils
								.getPartyDepartment(delegator, partyId,
										departmentNameList);
						Department = AttendanceUtils
								.getDepartmentName(departmentNameList);
						lostInfo.put("Department", Department);
						lostInfo.put("CardType",
								lostCard.getString("logisticsItemTypeId"));
						lostInfo.put("CardNo", lostCard.getString("cardId"));
						IssuedDate = dateFormat.format(lostCard
								.get("issuedDate"));
						lostInfo.put("IssuedDate", IssuedDate);
						LostDate = dateFormat.format(lostCard.get("lostDate"));
						lostInfo.put("LostDate", LostDate);
						lostInfo.put("reIssueReason",
								lostCard.getString("reIssueReason"));
						if (UtilValidate.isNotEmpty(lostCard
								.getString("chargeAmount"))) {
							lostInfo.put(
									"chargeAmount",
									lostCard.getString("chargeAmount").concat(
											" tk"));
						}
						lostInfo.put("SelectedDate",
								"From : " + dateFormat.format(fromTime)
										+ " To : " + dateFormat.format(toTime));

						cardLostList.add(lostInfo);

					}

				}

			} catch (GenericEntityException e) {
				e.printStackTrace();
			}

		}

		result.put("cardLostList", cardLostList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Lost Card  Info List");

		return result;
	}

	public static String exportLostCardInfo(HttpServletRequest request,
			HttpServletResponse response) {

		List<Map<String, String>> cardLostList = (List<Map<String, String>>) request
				.getSession().getAttribute("cardLostList");

		if (cardLostList != null && !cardLostList.isEmpty()) {
			long currentTime = System.currentTimeMillis();

			String fileName = "LostCardReport_"
					+ String.valueOf(cardLostList.get(0).get("SelectedDate"))
					+ ".xls";

			// create path for excel file
			String excelFilePath = UtilCommon.getAbsoluteFilePath(request,
					fileName);

			// prepare for excel
			List<String> columnHeaders = new ArrayList<String>();

			columnHeaders.add(0, "PIN");
			columnHeaders.add(1, "Name");
			columnHeaders.add(2, "Designation");
			columnHeaders.add(3, "Department");
			columnHeaders.add(4, "CardNo");
			columnHeaders.add(5, "IssuedDate");
			columnHeaders.add(6, "LostDate");

			List<List<String>> data = EmployeeExcelExportService
					.prepareExcelDataForReport(cardLostList, columnHeaders);

			try {
				ExcelUtils.saveToExcel(excelFilePath, "employeeLostCardList",
						data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return ExcelUtils.downloadExcel(fileName, request, response);
		} else {

			return "Data Empty";
		}
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

	// End Parvez Code

	// Tomal start coding ...

	/*public static Map<String, Object> findLeaveSummeryReportForPermanent(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy"); 
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
		Delegator delegator = dctx.getDelegator();
		
		String fromDate = (String) context.get("fromDate");
		String toDate = (String) context.get("toDate");
		
		String orgPartyId = (String) context.get("orgPartyId");
		
		int sl = 1;
		
	    logger.debug("orgPartyId-------->"+orgPartyId);
		
	    java.sql.Date fromTime = null;
		java.sql.Date toTime = null;
		
		List<Map<String, String>> ListLeaveSummeryReportPermanent=FastList.newInstance();
		List<GenericValue> partyList=FastList.newInstance();
		if(fromDate!=null&&!fromDate.isEmpty()&&toDate!=null&&!toDate.isEmpty()){
				fromTime=Date.valueOf(fromDate);
				toTime=Date.valueOf(toDate);
				partyList=AttendanceUtils.getTotalEmployee(delegator,partyList,orgPartyId);
		}
		
		List<Map<String, String> > LeaveList = FastList.newInstance();
		
		int count=0;
		for(GenericValue party:partyList){
			
			count++;
			String employeeId=party.getString("partyId");
//			String employeeId="1000628";
			logger.debug("EmploYeeeIDDDD: "+employeeId+" Counter: "+count);
			List<EntityExpr> exprsList = FastList.newInstance();
			exprsList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,employeeId.trim()));
			exprsList.add(EntityCondition.makeCondition("leaveStatus",EntityOperator.EQUALS,"LEAVE_APPROVED"));
			
			List<GenericValue> EmplLeave = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsList,EntityOperator.AND),null, null, null, false);
			if(EmplLeave!=null && EmplLeave.size()>0){
			
			getting permanent employee 
			List<GenericValue> permanentEmpl = FastList.newInstance();
			permanentEmpl = delegator.findByAnd("EmplPositionFulfillment", UtilMisc.toMap("partyId", employeeId.trim()));
			
			if(permanentEmpl!=null && permanentEmpl.size()>0){
				String isProbitionary = permanentEmpl.get(0).getString("isProbationaryFlag");
				
				if(isProbitionary==null||isProbitionary.equals("N")){
					
					GenericValue person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId",employeeId.trim()));
					
					String employeeName = "";
					String firstName =person.getString("firstName");
					String middleName =person.getString("middleName");
					String lastName =person.getString("lastName");
					
					 getting employee fullname
					if(firstName!=null){
						employeeName+=(firstName+" ");
					}
					if(middleName!=null){
						employeeName+=(middleName+" ");
					}
					if(lastName!=null){
						employeeName+=lastName;
					}
					
					getting employee designation
					String designation=	AttendanceUtils.getPartyDesignation(delegator,employeeId);
					
					List<String> departmentNameList=FastList.newInstance();
					departmentNameList=AttendanceUtils.getPartyDepartment(delegator,employeeId,departmentNameList);
					
					 getting employee department
					String department = AttendanceUtils.getDepartmentName(departmentNameList);
					
					List<GenericValue> employment = delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo", employeeId.trim()));
					
					getting join date from employment..
					Timestamp joinDate=null;
					if(employment.size()>0){
						joinDate = employment.get(0).getTimestamp("fromDate");
					}
					List<EntityExpr> exprsList = FastList.newInstance();
					exprsList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,employeeId.trim()));
					exprsList.add(EntityCondition.makeCondition("leaveStatus",EntityOperator.EQUALS,"LEAVE_APPROVED"));
					
					List<GenericValue> EmplLeave = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsList,EntityOperator.AND),null, null, null, false);
					
					for(GenericValue emplLeave:EmplLeave ){
						
						String leaveStatus = emplLeave.getString("leaveStatus");				
						GenericValue statusL = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId", leaveStatus));
						
						String isApproved = statusL.getString("statusCode");
						if(leaveStatus==null) leaveStatus="";
						
							
							String leaveTypeId = emplLeave.getString("leaveTypeId");
							
							getting Remarks from EmplLeave table
							String Remarks = emplLeave.getString("remarks");
							
							getting leave start date & end date
							Timestamp leaveStart = emplLeave.getTimestamp("fromDate");
							Timestamp leaveEnd = emplLeave.getTimestamp("thruDate");
							
							java.sql.Date sTime=null;
							java.sql.Date eTime=null;
							
							sTime = Date.valueOf(fromDate);
							eTime = Date.valueOf(toDate);
							
							long isTime = AttendanceUtils.getStartTime(sTime);
							long ieTime = AttendanceUtils.getEndTime(eTime);
							
							long tsTime = leaveStart.getTime();
							long teTime = leaveEnd.getTime();
							
							logger.debug("Screen S Timse "+isTime+" Screen E Time "+ieTime);
							logger.debug("Table S Time "+tsTime+" Table E Time "+teTime);
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
								}else{
									LeaveEnjoyedDate = df.format(tsTime)+"\n"+df.format(teTime);
								}
							}
							//second
							else if(isTime>=tsTime && ieTime<=teTime){
								if(df.format(isTime).toString().equals(df.format(ieTime).toString())){
									LeaveEnjoyedDate = df.format(isTime);
								}else{
									if(df.format(isTime).toString() == df.format(ieTime).toString()){
										LeaveEnjoyedDate = df.format(isTime);
									}else{
										LeaveEnjoyedDate = df.format(isTime)+"\n"+df.format(ieTime);
									}
								}
							}
							//third
							else if(isTime<=tsTime && tsTime<=ieTime && ieTime<=teTime){
								if(df.format(tsTime).toString().equals(df.format(ieTime).toString())){
									LeaveEnjoyedDate = df.format(tsTime);
								}else{
									LeaveEnjoyedDate = df.format(tsTime)+"\n"+df.format(ieTime);
								}
							}
							//fourth
							else if(isTime>=tsTime && isTime<=teTime && teTime<=ieTime){
								if(df.format(isTime).toString().equals(df.format(teTime).toString())){
									LeaveEnjoyedDate = df.format(isTime);
								}else{
									LeaveEnjoyedDate = df.format(isTime)+"\n"+df.format(teTime);
								}
								
							
							}else{
								
								flag=false;
								logger.debug("In Start date: "+df.format(isTime)+"In End date: "+df.format(ieTime));
								logger.debug("Table Start date: "+df.format(tsTime)+"Table End date: "+df.format(teTime));
								
								logger.debug("In Start time: "+String.valueOf(isTime)+"In End time: "+String.valueOf(ieTime));
								logger.debug("Table Start time: "+String.valueOf(tsTime)+"Table End time: "+String.valueOf(teTime));
							}
							// each day 86400000
							
							if(flag){
							
								getting leave type
								GenericValue leaveType = delegator.findByPrimaryKey("EmplLeaveType", UtilMisc.toMap("leaveTypeId", leaveTypeId));
								String leaveTypeDes = leaveType.getString("description");
								
								Map<String,String> leavePermanentEmployee = FastMap.newInstance();
								leavePermanentEmployee.put("SL", String.valueOf(sl++));
								leavePermanentEmployee.put("PIN", employeeId);
								leavePermanentEmployee.put("EmployeeName", employeeName);
								leavePermanentEmployee.put("Designation", designation);
								leavePermanentEmployee.put("Department", department);
								leavePermanentEmployee.put("JoinDate", df.format(joinDate));
								leavePermanentEmployee.put("LeaveEnjoyedDate",LeaveEnjoyedDate );
								leavePermanentEmployee.put("LeaveType", leaveTypeDes);
								leavePermanentEmployee.put("Remarks", Remarks);
								
								ListLeaveSummeryReportPermanent.add(leavePermanentEmployee);
							}
					}
					
				}
			}
			
		}
		}
		
	    result.put("ListLeaveSummeryReportPermanent", ListLeaveSummeryReportPermanent);
		result.put("selectedDateFrom", fromDate);
		result.put("selectedDateTo", toDate);
		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Employee Late List");
		return result;
	}*/

	/*public static String exportLeaveSummeryReportPermanentToExcel(
			HttpServletRequest request, HttpServletResponse response) {

		List<Map<String, String>> ListLeaveSummeryReportPermanent = (List<Map<String, String>>) request
				.getSession().getAttribute("ListLeaveSummeryReportPermanent");

		if (ListLeaveSummeryReportPermanent != null
				&& !ListLeaveSummeryReportPermanent.isEmpty()) {
			long currentTime = System.currentTimeMillis();
			String fileName = "LeaveSummeryReport(Permanent)"
					+ String.valueOf(currentTime) + ".xls";

			// create path for excel file
			String excelFilePath = UtilCommon.getAbsoluteFilePath(request,
					fileName);

			// prepare for excel
			List<String> columnHeaders = new ArrayList<String>();

			columnHeaders.add(0, "SL");
			columnHeaders.add(1, "PIN");
			columnHeaders.add(2, "EmployeeName");
			columnHeaders.add(3, "Designation");
			columnHeaders.add(4, "Department");
			columnHeaders.add(5, "JoinDate");
			columnHeaders.add(6, "LeaveEnjoyedDate");
			columnHeaders.add(7, "LeaveType");
			columnHeaders.add(8, "Remarks");

			List<List<String>> data = EmployeeExcelExportService
					.prepareExcelDataForEmployeeAbsenceReport(
							ListLeaveSummeryReportPermanent, columnHeaders);

			try {

				ExcelUtils.saveToExcel(excelFilePath,
						"ListLeaveSummeryReportPermanent", data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return ExcelUtils.downloadExcel(fileName, request, response);
		} else {

			return "Data Empty";
		}
	}*/
	
	// Tomal start coding  Monthly Summery...
	
	public static Map<String, Object> findMonthlySummeryForLateAbsentLeaveHalfday(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy"); 
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
		Delegator delegator = dctx.getDelegator();
		
		String fromDate = (String) context.get("fromDate");
		String toDate = (String) context.get("toDate");
		String orgPartyId = (String) context.get("orgPartyId");
		
		int sl = 1;
		/*logger.debug("orgPartyId-------->"+orgPartyId);*/
		java.sql.Date fromTime = null;
		java.sql.Date toTime = null;
		Timestamp startTime = null;
		Timestamp endTime = null;
		List<Map<String, String>> MonthlySummeryForLateAbsentLeaveHalfdayList=FastList.newInstance();
		List<GenericValue> partyList=FastList.newInstance();
		if(fromDate!=null&&!fromDate.isEmpty()&&toDate!=null&&!toDate.isEmpty()){
				fromTime=Date.valueOf(fromDate);
				toTime=Date.valueOf(toDate);
				partyList=AttendanceUtils.getTotalEmployee(delegator,partyList,orgPartyId);
				startTime = new Timestamp(fromTime.getTime());
				endTime = new Timestamp(toTime.getTime());
		}
		
		List<Map<String, String> > LeaveList = FastList.newInstance();
		
		for(GenericValue party:partyList){
			double totalLeave = 0;
			long totalFulDayAbsent = 0,totalHalfDayAbsent = 0;
			long totalLate = 0,totalEarlyOut = 0,totalPunchMissing = 0;
			String partyId=party.getString("partyId");
			GenericValue person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId",partyId.trim()));
			
			String employeeName = "";
			if(UtilValidate.isNotEmpty(person)){
				String firstName =person.getString("firstName");
				String middleName =person.getString("middleName");
				String lastName =person.getString("lastName");
				
//				 getting employee fullname
				if(firstName!=null){ employeeName+=(firstName+" ");}
				if(middleName!=null){ employeeName+=(middleName+" "); }
				if(lastName!=null){ employeeName+=lastName; }
			}
			
//			getting join date from employment..
			List<GenericValue> employment = delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo", partyId.trim()));
			Timestamp joinDate=null;
			String dateOfJoin = "";
			if(UtilValidate.isNotEmpty(employment)){
				joinDate = employment.get(0).getTimestamp("fromDate");
				dateOfJoin = df.format(joinDate);
			}
//			getting employee designation
			String designation =	AttendanceUtils.getPartyDesignation(delegator,partyId);
			if(UtilValidate.isEmpty(designation)){
				designation = "";
			}
//			getting employee department
			List<String> departmentNameList=FastList.newInstance();
			departmentNameList=AttendanceUtils.getPartyDepartment(delegator,partyId,departmentNameList);
			String department = AttendanceUtils.getDepartmentName(departmentNameList);
			if(UtilValidate.isEmpty(department)){
				department = "";
			}				
			
//			find total Late,EarlyOut,PunchMissing fromDate to toDate 
			Map<String,Long> TotalLateEarlyoutPunchMissing = FastMap.newInstance();
			TotalLateEarlyoutPunchMissing = getTotalLateEarlyoutPunchMissing(delegator,orgPartyId,partyId,fromDate,toDate);
			totalLate = TotalLateEarlyoutPunchMissing.get("totalLate");
			totalEarlyOut = TotalLateEarlyoutPunchMissing.get("totalEarlyout");
			totalPunchMissing = TotalLateEarlyoutPunchMissing.get("totalPunchMissing");
			
//			find total leave fromDate to toDate 
			totalLeave = getTotalFullDayLeave(delegator,orgPartyId,partyId,fromDate,toDate);
			int halfdayCount = getTotalHalfDayLeave(delegator,orgPartyId,partyId,startTime,endTime);
			totalLeave += (double) halfdayCount/2;
//			find Absent fromDate to toDate 
			Map<String, Long> totalAbsent = getTotalAbsent(delegator, orgPartyId, partyId, startTime, endTime);
			totalFulDayAbsent = totalAbsent.get("totalFulDayAbsent");
			totalHalfDayAbsent = totalAbsent.get("totalHalfDayAbsent");
			
			String pinNo = AttendanceUtils.getUserLoginIDFromPartyID(delegator, partyId);
			if(UtilValidate.isNotEmpty(pinNo)){
				Map<String,String> monthlyTotalData = FastMap.newInstance();
				monthlyTotalData.put("SL", String.valueOf(sl++));
				monthlyTotalData.put("PIN",pinNo);
				monthlyTotalData.put("EmployeeName", employeeName);
				monthlyTotalData.put("JoinDate", dateOfJoin);
				monthlyTotalData.put("Designation", designation);
				monthlyTotalData.put("Department", department);
				monthlyTotalData.put("Late", String.valueOf(totalLate));
				monthlyTotalData.put("EarlyOut", String.valueOf(totalEarlyOut));
				/*monthlyTotalData.put("Late", "Late");
				monthlyTotalData.put("EarlyOut", "Earlyout");*/
				monthlyTotalData.put("PunchMissing", String.valueOf(totalPunchMissing));
				monthlyTotalData.put("Absent", String.valueOf(totalFulDayAbsent));
				monthlyTotalData.put("Leave", String.valueOf(totalLeave));
				monthlyTotalData.put("HalfDayAbsent", String.valueOf(totalHalfDayAbsent));
				monthlyTotalData.put("Remarks","");
				
				MonthlySummeryForLateAbsentLeaveHalfdayList.add(monthlyTotalData);
			}
		}
		
		ArrayList<Map> sortedMonthlySummeryForLateAbsentLeaveHalfdayList = AttendanceUtils.getSortedList(MonthlySummeryForLateAbsentLeaveHalfdayList);
	    int serial =1;
	    for(int i=0;i<sortedMonthlySummeryForLateAbsentLeaveHalfdayList.size();i++){
	    	Map<String,String> sortedMap = sortedMonthlySummeryForLateAbsentLeaveHalfdayList.get(i);
	    	sortedMap.put("SL", String.valueOf(serial++));
	    	sortedMonthlySummeryForLateAbsentLeaveHalfdayList.set(i, sortedMap);
	    }
	    result.put("MonthlySummeryForLateAbsentLeaveHalfdayList", sortedMonthlySummeryForLateAbsentLeaveHalfdayList);
		result.put("selectedDateFrom", fromDate);
		result.put("selectedDateTo", toDate);
		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Employee Late List");
		return result;
	}
	
	/**
	 * @Description Total Late / Earlyout / Punch Missing  Finding
	 * @param Delegator delegator, String orgPartyId, String partyId, String fromDate, String toDate
	 * @return Map<String,Long>
	 * @author tomal mahdi
	 * 
	**/
	public static Map<String,Long> getTotalLateEarlyoutPunchMissing(Delegator delegator, String orgPartyId,String partyId,String fromDate, String toDate){
		
		long totalLate = 0,totalEarlyout = 0,totalPunchMissing = 0;
		java.sql.Date fromDateToSqlDate = null;
		java.sql.Date toDateToSqlDate = null;
		Map<String,Long> totalLateEarly = FastMap.newInstance();
		
		if(fromDate!=null&&!fromDate.isEmpty()&&toDate!=null&&!toDate.isEmpty()){
			fromDateToSqlDate=Date.valueOf(fromDate);
			toDateToSqlDate=Date.valueOf(toDate);
		}
		
		long stime=0;
		long etime=0;
		
		stime=AttendanceUtils.getStartTime(fromDateToSqlDate);
		etime=AttendanceUtils.getEndTime(toDateToSqlDate);
		
		while(stime<=etime){
			
			Timestamp 	startTimestamp=null;
			Timestamp	endTimestamp=null;
			startTimestamp=AttendanceUtils.getModifiedDayStart(new Timestamp(stime));
			endTimestamp=AttendanceUtils.getModifiedDayEnd(new Timestamp(stime));
			
			List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
			String entryTime = "";
			String exitTime = "";
			String lateEntryTime = "";
//			Map<String,Object> preference=UtilAttendancePreference.getPreference(partyId, delegator, new Date(stime), orgPartyId);
			try {
				/*entryTime=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, new Date(stime), orgPartyId, UtilPreferenceProperties.ENTRYTIME);*/
				exitTime=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, new Date(stime), orgPartyId, UtilPreferenceProperties.EXITTIME);
				/*logger.debug("EXXIT_TIME: "+exitTime+" entryTime: "+entryTime+"  End!");*/
				lateEntryTime=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, new Date(stime), orgPartyId, UtilPreferenceProperties.LATEENTRYTIME);
			} catch (GenericEntityException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			long entryTimeInDate=AttendanceUtils.getLateEntryTimeInDate(entryTime,new Date(stime));
			
			List<EntityExpr> exprs = FastList.newInstance();
			List<String> orderBy = FastList.newInstance();
			orderBy.add("logtimeStamp ASC");
			
			//for late count
			exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO, startTimestamp));
			exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.LESS_THAN_EQUAL_TO, endTimestamp));
			exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			
			try {
				employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!UtilValidate.isEmpty(employeeLogs)){
				
				//for late entry
				long lateEntryTimeInDate=AttendanceUtils.getLateEntryTimeInDate(lateEntryTime,new Date(stime));
				GenericValue employeeLog=employeeLogs.get(0);
				Timestamp	time=(Timestamp)employeeLog.get("logtimeStamp");
				if(AttendanceUtils.compareLateLogtime(lateEntryTimeInDate,time.getTime())){
					totalLate++;
				}
				
				//for early out
				long earlyoutTimeInDate = AttendanceUtils.getLateEntryTimeInDate(exitTime, new Date(stime)); 
				/*logger.debug("EarlyOutTimeInDate: "+earlyoutTimeInDate+"time: "+time+" End!");*/
				employeeLog=employeeLogs.get(employeeLogs.size()-1);
				time=(Timestamp)employeeLog.get("logtimeStamp");
				
				//check early out before exit time.
				if(time.getTime()>earlyoutTimeInDate){
					totalEarlyout++;
				}
				if(employeeLogs.size()==1){
					totalPunchMissing++;
				}
				
			}
			stime+=86400000;
			// each day 86400000
		}
		
		totalLateEarly.put("totalLate", totalLate);
		totalLateEarly.put("totalEarlyout", totalEarlyout);
		totalLateEarly.put("totalPunchMissing", totalPunchMissing);
		return totalLateEarly;
	}
	
	
	/**
	 * @see Total Leave 
	 * @param Delegator delegator, String orgPartyId, String partyId, String fromDate, String toDate
	 * @return Double totalLeave
	 * @author tomal mahdi
	 * 
	**/
	public static double getTotalFullDayLeave(Delegator delegator,String orgPartyId,String partyId,String fromDate,String toDate){
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
						java.sql.Date sTime=null;
						java.sql.Date eTime=null;
						
						sTime = Date.valueOf(fromDate);
						eTime = Date.valueOf(toDate);
						
						long isTime = AttendanceUtils.getStartTime(sTime);
						long ieTime = AttendanceUtils.getEndTime(eTime);
						
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
						
						totalLeave+=leaveCount;
				}
		}
		
		return totalLeave;
	}
	/** 
	 * @param Delegator delegator, String orgPartyId,String partyId, Timestamp startTime, Timestamp endTime
	 * @return Map<String,Double> ("totalFulDayAbsent","totalHalfDayAbsent") 
	 * @author tomal mahdi
	 * @see this method is for getting total absent fromDate to toDate.
	**/
	public static Map<String,Long> getTotalAbsent(Delegator delegator,String orgPartyId,String partyId,Timestamp startTime,Timestamp endTime){
			
			
			long totalFulDayAbsent = 0;
			long totalHalfDayAbsent= 0;
			
			Map<String,Long> absent = FastMap.newInstance(); 
			absent.put("totalFulDayAbsent", totalFulDayAbsent);
			absent.put("totalHalfDayAbsent", totalHalfDayAbsent);
			List<EntityExpr> exprsList = FastList.newInstance();
			exprsList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId.trim()));
			exprsList.add(EntityCondition.makeCondition("leaveStatus",EntityOperator.EQUALS,"LEAVE_APPROVED"));
			List<GenericValue> employeeLeaves=new ArrayList<GenericValue>();
			try{
				employeeLeaves = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsList, EntityOperator.AND), null, null, null, false);
			    }catch(Exception e){
			    	logger.debug("Exception to get leave in findDailyAbsenceReport function---->"+e);
			 }
			while(startTime.getTime()<=endTime.getTime()){
				Timestamp dayStart=AttendanceUtils.getModifiedDayStart(startTime);
				Timestamp dayEnd=AttendanceUtils.getModifiedDayEnd(startTime);
				
				List<Timestamp> holidays=AttendanceUtils.getHolidays(orgPartyId, dayStart, dayEnd, delegator);
//				check the day is holiday or not?
				if(UtilValidate.isEmpty(holidays)){
					boolean checkLeaveStatus=AttendanceUtils.checkEmpLeave(employeeLeaves,startTime.getTime());
//					check is he in leave or not?
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
						
							if(!UtilValidate.isEmpty(employeeLogs)){
								totalFulDayAbsent++;
							}
						
						} catch (GenericEntityException e) {
							e.printStackTrace();
						}
						
						
					}else{
						if(UtilValidate.isNotEmpty(employeeLeaves)){
							String leaveDuration = employeeLeaves.get(0).getString("leaveDuration");
							if(UtilValidate.isEmpty(leaveDuration)){
								leaveDuration = "";
							}
							if(!leaveDuration.equals("Full_Day")){
								totalHalfDayAbsent++;
							}
						}
					}
				}
				startTime = UtilDateTime.addDaysToTimestamp(startTime, 1);
		}
		absent.put("totalFulDayAbsent", totalFulDayAbsent);
		absent.put("totalHalfDayAbsent",totalHalfDayAbsent);
		return absent;
	}
	
	// Tomal End coding  Monthly Summery...

	public static Map<String, Object> getMonthlyDataForRecruitment(
			Delegator delegator, String orgPartyId)
			throws GenericEntityException {

		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<GenericValue> Employment = delegator.findByAnd("Employment",
				UtilMisc.toMap("partyIdFrom", orgPartyId.trim()));
		int[] months = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		/* logger.debug("Employement Size "+Employment.size()); */

		/* long thisYear = System.currentTimeMillis(); */
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		/*
		 * int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
		 * logger.debug("Current Year: "+currentYear+" Month: "+currentMonth);
		 */

		Calendar joinDateTime = Calendar.getInstance();
		int monthIndex = 0;
		for (GenericValue employment : Employment) {
			/* getting join month from employment.. */
			Timestamp joinDate = null;
			if (employment.size() > 0) {
				joinDate = employment.getTimestamp("fromDate");
				long calTime = joinDate.getTime();
				joinDateTime.setTimeInMillis(calTime);
				monthIndex = joinDateTime.get(Calendar.MONTH);
				int year = joinDateTime.get(Calendar.YEAR);
				if (currentYear == year) {
					months[monthIndex] += 1;
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
		/*
		 * logger.debug("MONTHSSSSSSSS "+months[0]+" "+months[1]+" "+months[2]+" "
		 * +months[3]+" "+months[4]+" "+months[5]+" "+months[6]+" oRGpARTYiD "+
		 * orgPartyId);
		 */

		return result;
	}
	/**
	 * @author tomal madhi
	 */
	public static Map<String, Object> findEarlyoutReport(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy"); 
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
		Delegator delegator = dctx.getDelegator();
		
		String fromDate = (String) context.get("fromDate");
		String toDate = (String) context.get("toDate");
		String orgPartyId = (String) context.get("orgPartyId");
		
		int sl = 1;
		/*logger.debug("orgPartyId-------->"+orgPartyId);*/
		java.sql.Date fromTime = null;
		java.sql.Date toTime = null;
		Timestamp startTime = null;
		Timestamp endTime = null;
		List<Map<String, String>> EarlyoutList=FastList.newInstance();
		List<GenericValue> partyList=FastList.newInstance();
		if(fromDate!=null&&!fromDate.isEmpty()&&toDate!=null&&!toDate.isEmpty()){
				fromTime=Date.valueOf(fromDate);
				toTime=Date.valueOf(toDate);
				partyList=AttendanceUtils.getTotalActiveEmployee(delegator,partyList,orgPartyId);
				startTime = new Timestamp(fromTime.getTime());
				endTime = new Timestamp(toTime.getTime());
		}
		
		List<Map<String, String> > LeaveList = FastList.newInstance();
		
		for(GenericValue party:partyList){
			long totalEarlyOut = 0;
			String partyId=party.getString("partyId");
			GenericValue person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId",partyId.trim()));
			
			String employeeName = "";
			if(UtilValidate.isNotEmpty(person)){
				String firstName =person.getString("firstName");
				String middleName =person.getString("middleName");
				String lastName =person.getString("lastName");
				
//				 getting employee fullname
				if(firstName!=null){ employeeName+=(firstName+" ");}
				if(middleName!=null){ employeeName+=(middleName+" "); }
				if(lastName!=null){ employeeName+=lastName; }
			}
			
//			getting join date from employment..
			List<GenericValue> employment = delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo", partyId.trim()));
			Timestamp joinDate=null;
			String dateOfJoin = "";
			if(UtilValidate.isNotEmpty(employment)){
				joinDate = employment.get(0).getTimestamp("fromDate");
				dateOfJoin = df.format(joinDate);
			}
//			getting employee designation
			String designation =	AttendanceUtils.getPartyDesignation(delegator,partyId);
			if(UtilValidate.isEmpty(designation)){
				designation = "";
			}
//			getting employee department
			List<String> departmentNameList=FastList.newInstance();
			departmentNameList=AttendanceUtils.getPartyDepartment(delegator,partyId,departmentNameList);
			String department = AttendanceUtils.getDepartmentName(departmentNameList);
			if(UtilValidate.isEmpty(department)){
				department = "";
			}				
			
//			find total Late,EarlyOut,PunchMissing fromDate to toDate 
			Map<String,Long> TotalLateEarlyoutPunchMissing = FastMap.newInstance();
			TotalLateEarlyoutPunchMissing = getTotalLateEarlyoutPunchMissing(delegator,orgPartyId,partyId,fromDate,toDate);
			totalEarlyOut = TotalLateEarlyoutPunchMissing.get("totalEarlyout");
			
			Map<String,String> monthlyTotalData = FastMap.newInstance();
			monthlyTotalData.put("SL", String.valueOf(sl++));
			monthlyTotalData.put("PIN", AttendanceUtils.getUserLoginIDFromPartyID(delegator, partyId));
			monthlyTotalData.put("EmployeeName", employeeName);
			monthlyTotalData.put("JoinDate", dateOfJoin);
			monthlyTotalData.put("Designation", designation);
			monthlyTotalData.put("Department", department);
			monthlyTotalData.put("EarlyOut", String.valueOf(totalEarlyOut));
			monthlyTotalData.put("Remarks","");
			
			EarlyoutList.add(monthlyTotalData);
		}
		
		ArrayList<Map> sortedEarlyoutList = AttendanceUtils.getSortedList(EarlyoutList);
	    int serial =1;
	    for(int i=0;i<sortedEarlyoutList.size();i++){
	    	Map<String,String> sortedMap = sortedEarlyoutList.get(i);
	    	sortedMap.put("SL", String.valueOf(serial++));
	    	sortedEarlyoutList.set(i, sortedMap);
	    }
	    result.put("EarlyoutList", sortedEarlyoutList);
		result.put("selectedDateFrom", fromDate);
		result.put("selectedDateTo", toDate);
		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Employee Late List");
		return result;
	}
	/**
	 * @description findMonthlyAttendanceSummary
	 * @author tomal mahdi
	 * @throws ParseException 
	 */
	public static Map<String, Object> findMonthlyAttendanceSummary(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException, ParseException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		List<Map<String, String> > monthlyAttendanceSummery=FastList.newInstance();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy"); 
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
		Delegator delegator = dctx.getDelegator();
		
		String fromDate = (String) context.get("fromDate");
		String toDate = (String) context.get("toDate");
		
		String orgPartyId = (String) context.get("orgPartyId");
		String unitPartyId=orgPartyId;
		int sl = 1;
		
		// filter on division
	    String division = (String) context.get("division");
		if(UtilValidate.isNotEmpty(division)){
				unitPartyId=division;
	    }
		// filter on department
		String department = (String) context.get("department");
		if(UtilValidate.isNotEmpty(department)){
	    		unitPartyId=department;   
	    }
	    // filter on section
	    String section = (String) context.get("section");
	    if(UtilValidate.isNotEmpty(section)){
        	unitPartyId=section; 
        }
	    // filter on line
        String line = (String) context.get("line");
        if (UtilValidate.isNotEmpty(line)) {
        	unitPartyId=line;  
        }
        /*logger.debug("orgPartyId-------->"+orgPartyId);*/
        
        java.sql.Date fromDateToSqlDate = null;
		java.sql.Date toDateToSqlDate = null;
		long stime=0;
		long etime=0;
		Timestamp fromTime =null;
		Timestamp toTime =null;
		if(fromDate!=null&&!fromDate.isEmpty()){
			fromDateToSqlDate=Date.valueOf(fromDate);
			fromTime = new Timestamp(fromDateToSqlDate.getTime()); 
			if(toDate!=null && toDate.length()>0){
				toDateToSqlDate=Date.valueOf(toDate);
				toTime = new Timestamp(toDateToSqlDate.getTime());
			}else{
				toDateToSqlDate=Date.valueOf(fromDate);
				toTime = new Timestamp(toDateToSqlDate.getTime());
			}
		}
		double totalLeave = 0,totalFulldayAbsent = 0,HalfdayAbsent = 0;
		long totalHoliday = 0;
		List<GenericValue> partyList=FastList.newInstance();
		partyList=AttendanceUtils.getTotalActiveEmployee(delegator, partyList, unitPartyId);
		List<GenericValue> emplLeave = FastList.newInstance();
		if(UtilValidate.isNotEmpty(fromTime) && UtilValidate.isNotEmpty(toTime)){
			for(GenericValue party:partyList){
				Map<String,String> data = FastMap.newInstance();
				String partyId = party.getString("partyId");
				String employeeId = AttendanceUtils.getUserLoginIDFromPartyID(delegator, partyId);
				String cardId=	AttendanceUtils.getCardIdFromPartyId(delegator,partyId);
				
				GenericValue person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId",partyId.trim()));
				String employeeName = "";
				if(UtilValidate.isNotEmpty(person)){
					String firstName =person.getString("firstName");
					String middleName =person.getString("middleName");
					String lastName =person.getString("lastName");
					
	//				 getting employee fullname
					if(firstName!=null){ employeeName+=(firstName+" ");}
					if(middleName!=null){ employeeName+=(middleName+" "); }
					if(lastName!=null){ employeeName+=lastName; }
				}
				
	//			getting join date from employment..
				List<GenericValue> employment = delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo", partyId.trim()));
				
				String employmentType = "";
				Timestamp joinDate=null;
				String dateOfJoin = "";
				if(UtilValidate.isNotEmpty(employment)){
					joinDate = employment.get(0).getTimestamp("fromDate");
					dateOfJoin = df.format(joinDate);
					employmentType = employment.get(0).getString("employmentType");
					
				}
//				getting employee designation
				String designation = AttendanceUtils.getPartyDesignation(delegator,partyId);
				if(UtilValidate.isEmpty(designation)){
					designation = "";
				}
//				getting payGrade for employee...
				String emplPostionId = AttendanceUtils.getPositionIdByPatyId(delegator, partyId);
				GenericValue emplPos = delegator.findByPrimaryKey("EmplPosition",UtilMisc.toMap("emplPositionId",emplPostionId));
				String payGradeId =""; 
				if(UtilValidate.isNotEmpty(emplPos)){
					payGradeId = emplPos.getString("payGradeId");
					if(UtilValidate.isNotEmpty(payGradeId)){
						GenericValue payGrade = delegator.findByPrimaryKey("PayGrade",UtilMisc.toMap("payGradeId",payGradeId));
						payGradeId = payGrade.getString("payGradeName");
					}else{
						payGradeId ="";
					}
				}
				// for getting holidays...
				List<Timestamp> holidays=AttendanceUtils.getHolidays(orgPartyId, fromTime, toTime, delegator);
				totalHoliday = holidays.size();
				
				//get total days... 
				stime=AttendanceUtils.getStartTime(fromDateToSqlDate);
				etime=AttendanceUtils.getEndTime(toDateToSqlDate);
				long totalDays = (etime-stime)/86400000;
				totalDays+=1;
				long totalWorkDays = totalDays-totalHoliday;
				
				// for getting total absent full day & half day...	
				Map<String,Long> TotalAbsent = getTotalAbsent(delegator,orgPartyId,partyId,fromTime,toTime);
				long fulldayAbsent = TotalAbsent.get("totalFulDayAbsent");
				long halfdayAb = TotalAbsent.get("totalHalfDayAbsent");
				HalfdayAbsent = (double) halfdayAb/2;
				
				// for getting employee leave halfday & fullday...
				totalLeave = getTotalFullDayLeave(delegator,orgPartyId,partyId,fromDate,toDate);
				int halfdayCount = getTotalHalfDayLeave(delegator, orgPartyId, partyId, fromTime, toTime);
				totalLeave += (double) halfdayCount/2;
				
//				getEmployeeOt
				List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
				int totalOTHours = 0;
				/*while(stime<etime){
					Timestamp startTimestamp=AttendanceUtils.getModifiedDayStart(new Timestamp(stime));
					Timestamp endTimestamp=AttendanceUtils.getModifiedDayEnd(new Timestamp(stime));
					
					List<EntityExpr> exprs = FastList.newInstance();
					List<String> orderBy = FastList.newInstance();
					orderBy.add("logtimeStamp ASC");
					
					exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO, startTimestamp));
					exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.LESS_THAN_EQUAL_TO, endTimestamp));
					exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
					logger.debug("Condition for late entry------>"+exprs);
					employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);
					
					if(UtilValidate.isNotEmpty(employeeLogs)){
						GenericValue emplLog = employeeLogs.get(employeeLogs.size()-1);
						Timestamp logtimeStamp = emplLog.getTimestamp("logtimeStamp");
						int otHours = AttendanceUtils.getEmployeeOt(delegator, orgPartyId, partyId, new Date(stime), logtimeStamp);
						totalOTHours += otHours;
					}
					stime+=86400000;
					
				}	*/	
				double eotHours = 0;
				while(stime<etime){
					Timestamp startTimestamp=AttendanceUtils.getModifiedDayStart(new Timestamp(stime));
					Timestamp endTimestamp=AttendanceUtils.getModifiedDayEnd(new Timestamp(stime));
					
					Map<String,Double> EotAmounts = AttendanceUtils.findEOTByEmplPartyId(delegator,partyId,startTimestamp,
													endTimestamp,orgPartyId);
					if(UtilValidate.isNotEmpty(EotAmounts)){
						eotHours += EotAmounts.get("eotHours");
					}
					stime+=86400000;
				}
				
				totalOTHours = (int)eotHours;
				Map<String,Object> holidayOverTimeMap = Attendance.findHolidayOvertime(delegator,partyId,employmentType,fromDateToSqlDate,toDateToSqlDate,orgPartyId);
				
				String totalHoursForHoliday = "0";
				if(UtilValidate.isNotEmpty(holidayOverTimeMap.get("totalHours"))){
					data.put("totalHolidayHours",  holidayOverTimeMap.get("totalHours").toString());
					totalOTHours += Integer.parseInt(holidayOverTimeMap.get("totalHours").toString());
				}else{
					data.put("totalHolidayHours",  "0");
				}
				
				data.put("pin", employeeId);
				data.put("cardId", cardId);
				data.put("employeeName", employeeName);
				data.put("designation", designation);
				data.put("grade", payGradeId);
				data.put("dateOfJoin", dateOfJoin);
				data.put("daysOfMonth",String.valueOf(totalDays));
				data.put("workDays", String.valueOf(totalWorkDays));
				data.put("totalLeave", String.valueOf(totalLeave));
				data.put("absent", String.valueOf(fulldayAbsent));
				data.put("holidays", String.valueOf(totalHoliday));
				data.put("halfDayAbsent", String.valueOf(HalfdayAbsent));
				data.put("eotHours", String.valueOf(eotHours));
				data.put("totalOT", String.valueOf(totalOTHours));
				monthlyAttendanceSummery.add(data);
			}
		}
//		stime+=86400000;
		ListMonthlyAttendanceSummary = monthlyAttendanceSummery;
		result.put("ListMonthlyAttendanceSummary", monthlyAttendanceSummery);
		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Employee Late List");
		return result;
	}
	/**
	 * @description findMonthlyAttendanceSummaryListForPdf
	 * @author tomal mahdi
	 */
	public static Map<String, Object> findMonthlyAttendanceSummaryListForPdf(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		result.put("ListMonthlyAttendanceSummary", ListMonthlyAttendanceSummary);
		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Employee Late List");
		return result;
	}
	
	// Tomal End coding...
	
	/** 
	 * @param 
	 * @return 
	 * @author zzz
	 * @throws GenericEntityException 
	 * @throws ParseException 
	 * @see.
	**/
	
	public static Map<String, Object> findHolidayBenefit(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException, ParseException {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		Delegator delegator = dctx.getDelegator();

		String orgPartyId = (String) context.get("orgPartyId");
		String parentPartyId = orgPartyId;
		String fromDate = (String) context.get("fromDate");
		String toDate = (String) context.get("toDate");
		String empType = (String) context.get("empType");
		java.sql.Date fromTime=null;
		java.sql.Date toTime=null;		
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		Locale locale = (Locale) context.get("locale");
        java.util.Date now = new java.util.Date();
    	List<GenericValue> partyList=FastList.newInstance();

		List<Map<String, String>> holidayBenifitsList = FastList.newInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		String PIN = "";
		String Name = "";
		String Designation = "";
		String Department = "";
		String payGradeId = "";
		

	  	// filter on division
	    String division = (String) context.get("division");
		if (UtilValidate.isNotEmpty(division)) {
			parentPartyId=division;
	        }
		
		// filter on department
		String department = (String) context.get("department");
		      
	    if (UtilValidate.isNotEmpty(department)) {
	    	parentPartyId=department;
		   
	        }
	    
	    // filter on section
	    String section = (String) context.get("section");
	      
	    if (UtilValidate.isNotEmpty(section)) {
	    	parentPartyId=section; 
	    }
	    // filter on line
	    String line = (String) context.get("line");
	    
	    if (UtilValidate.isNotEmpty(line)) {
	    	parentPartyId=line;  
	    }

		if (fromDate != null && !fromDate.isEmpty() && toDate != null
				&& !toDate.isEmpty()) {
			
			if(fromDate!=null && !fromDate.isEmpty() && toDate!=null && !toDate.isEmpty()){
				try{
					fromTime=Date.valueOf(fromDate);
					toTime=Date.valueOf(toDate);
					
				}catch(Exception e){
					fromTime=new Date(System.currentTimeMillis());
				}
			}else{
				fromTime=new Date(System.currentTimeMillis());
			}
			Timestamp dayStart=AttendanceUtils.getModifiedDayStart(new Timestamp(fromTime.getTime()));
			
			partyList=AttendancePreferences.getTotalEmployeeByPositionAndEmpType(delegator,partyList,parentPartyId,empType);
			
			for(GenericValue party:partyList){
				
				Map<String, String> employee = FastMap.newInstance();

				String partyId=party.getString("partyId");				
				
				Map<String,Object> holidayOverTimeMap= Attendance.findHolidayOvertime(delegator,partyId,empType,fromTime,toTime,orgPartyId);
				
				if(empType.equalsIgnoreCase("STAFF")){
					
					if(!holidayOverTimeMap.get("holidayOTAmount").toString().equals("0")){
						String emplPositionId = AttendanceUtils.getPositionIdByPatyId(delegator, partyId);

						PIN=AttendanceUtils.getUserLoginIDFromPartyID(delegator, partyId);
						employee.put("PIN", PIN);
						Name=AttendanceUtils.getPartyName(delegator, partyId);
						employee.put("Name", Name);				
						
						List<String> departmentNameList=FastList.newInstance();
						departmentNameList=AttendanceUtils.getPartyDepartment(delegator,partyId,departmentNameList);               
						employee.put("Department", AttendanceUtils.getDepartmentName(departmentNameList));									
						
						List<GenericValue>	emplPositionAndEmplPositionTypes = delegator.findByAnd("EmplPositionAndEmplPositionType",UtilMisc.toMap("emplPositionId", emplPositionId));
						GenericValue emplPositionAndEmplPositionType=EntityUtil.getFirst(emplPositionAndEmplPositionTypes);
						
						if (!UtilValidate.isEmpty(emplPositionAndEmplPositionType)){
							 payGradeId=emplPositionAndEmplPositionType.getString("payGradeId");
							 Designation=emplPositionAndEmplPositionType.getString("description");
							 employee.put("Designation", Designation);
							 employee.put("payGrade", payGradeId);
						}
						
						employee.put("numberOfDaysHolidayOTHappen",  holidayOverTimeMap.get("numberOfDaysHolidayOTHappen").toString());
						
						if(UtilValidate.isNotEmpty(holidayOverTimeMap.get("holidayRate"))){
							employee.put("holidayRate",  holidayOverTimeMap.get("holidayRate").toString());
						}else{
							employee.put("holidayRate",  "0");
						}
						employee.put("netAmount",  holidayOverTimeMap.get("holidayOTAmount").toString());
						
						holidayBenifitsList.add(employee);

					}									
				
				}else if(empType.equalsIgnoreCase("WORKERS")){
									
						if(!holidayOverTimeMap.get("holidayOTAmount").toString().equals("0")){
							
							String emplPositionId = AttendanceUtils.getPositionIdByPatyId(delegator, partyId);

							PIN=AttendanceUtils.getUserLoginIDFromPartyID(delegator, partyId);
							employee.put("PIN", PIN);
							Name=AttendanceUtils.getPartyName(delegator, partyId);
							employee.put("Name", Name);				
							
							List<String> departmentNameList=FastList.newInstance();
							departmentNameList=AttendanceUtils.getPartyDepartment(delegator,partyId,departmentNameList);               
							employee.put("Department", AttendanceUtils.getDepartmentName(departmentNameList));									
							
							List<GenericValue>	emplPositionAndEmplPositionTypes = delegator.findByAnd("EmplPositionAndEmplPositionType",UtilMisc.toMap("emplPositionId", emplPositionId));
							GenericValue emplPositionAndEmplPositionType=EntityUtil.getFirst(emplPositionAndEmplPositionTypes);
							
							if (!UtilValidate.isEmpty(emplPositionAndEmplPositionType)){
								 payGradeId=emplPositionAndEmplPositionType.getString("payGradeId");
								 Designation=emplPositionAndEmplPositionType.getString("description");
								 employee.put("Designation", Designation);
								 employee.put("payGrade", payGradeId);
							}
							
							if(UtilValidate.isNotEmpty(holidayOverTimeMap.get("basicSalary"))){
								employee.put("basicSalary",  holidayOverTimeMap.get("basicSalary").toString());
							}else{
								employee.put("basicSalary",  "0");
							}
							if(UtilValidate.isNotEmpty(holidayOverTimeMap.get("perHrsTake"))){
								employee.put("perHrsTake",  holidayOverTimeMap.get("perHrsTake").toString());
							}else{
								employee.put("perHrsTake",  "0");
							}
							if(UtilValidate.isNotEmpty(holidayOverTimeMap.get("totalHours"))){
								employee.put("totalHours",  holidayOverTimeMap.get("totalHours").toString());
							}else{
								employee.put("totalHours",  "0");
							}
							if(UtilValidate.isNotEmpty(holidayOverTimeMap.get("holidayOTAmount"))){
								employee.put("holidayOTAmount",  holidayOverTimeMap.get("holidayOTAmount").toString());
							}else{
								employee.put("holidayOTAmount",  "0");						
							}
							holidayBenifitsList.add(employee);
						}

					}
			}
		}

		ArrayList<Map> sortedholidayBenifitsList = AttendanceUtils.getSortedList(holidayBenifitsList);
		
		result.put("holidayBenifitsList", sortedholidayBenifitsList);
		result.put("fromToDate", "From : " + dateFormat.format(fromTime)
				+ " To : " + dateFormat.format(toTime));
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Employee Personal Info");

		return result;
	}
	
	/** 
	 * @param 
	 * @return 
	 * @author zzz
	 * @throws GenericEntityException 
	 * @throws ParseException 
	 * @see.
	**/
	
	public static Map<String, Object> findHolidayOtLedger(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException, ParseException {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		Delegator delegator = dctx.getDelegator();

		String orgPartyId = (String) context.get("orgPartyId");
		String parentPartyId = orgPartyId;
		String fromDate = (String) context.get("fromDate");
		String toDate = (String) context.get("toDate");
		String empType = (String) context.get("empType");
		java.sql.Date fromTime=null;
		java.sql.Date toTime=null;		
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		Locale locale = (Locale) context.get("locale");
        java.util.Date now = new java.util.Date();
    	List<GenericValue> partyList=FastList.newInstance();

		List<Map<String, Object>> holidayOTLedgerList = FastList.newInstance();
		List<Timestamp> holidays=new ArrayList<Timestamp>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		String PIN = "";
		String Name = "";
		String Designation = "";
		String Department = "";
		String payGradeId = "";
		
	  	// filter on division
	    String division = (String) context.get("division");
		if (UtilValidate.isNotEmpty(division)) {
			parentPartyId=division;
	        }
		
		// filter on department
		String department = (String) context.get("department");
		      
	    if (UtilValidate.isNotEmpty(department)) {
	    	parentPartyId=department;
		   
	        }
	    
	    // filter on section
	    String section = (String) context.get("section");
	      
	    if (UtilValidate.isNotEmpty(section)) {
	    	parentPartyId=section; 
	    }
	    // filter on line
	    String line = (String) context.get("line");
	    
	    if (UtilValidate.isNotEmpty(line)) {
	    	parentPartyId=line;  
	    }

		if (fromDate != null && !fromDate.isEmpty() && toDate != null
				&& !toDate.isEmpty()) {
			
			if(fromDate!=null && !fromDate.isEmpty() && toDate!=null && !toDate.isEmpty()){
				try{
					fromTime=Date.valueOf(fromDate);
					toTime=Date.valueOf(toDate);
					holidays=Utils.getHolidays(orgPartyId, new Timestamp(fromTime.getTime()), new Timestamp(toTime.getTime()), delegator);
					
					
				}catch(Exception e){
					fromTime=new Date(System.currentTimeMillis());
				}
			}else{
				fromTime=new Date(System.currentTimeMillis());
			}
			Timestamp dayStart=AttendanceUtils.getModifiedDayStart(new Timestamp(fromTime.getTime()));
			
			partyList=AttendancePreferences.getTotalEmployeeByPositionAndEmpType(delegator,partyList,parentPartyId,empType);
			Collections.sort(partyList);
			for(GenericValue party:partyList){
				Map<String, Object> employee = FastMap.newInstance();
				List<String> holidayWorkKey = new ArrayList<String>();
				List<String> holidayWorkData = new ArrayList<String>();

				String partyId=party.getString("partyId");
												
				if(empType.equalsIgnoreCase("STAFF")){
					
					
						String emplPositionId = AttendanceUtils.getPositionIdByPatyId(delegator, partyId);

						PIN=AttendanceUtils.getUserLoginIDFromPartyID(delegator, partyId);
						employee.put("PIN", PIN);
						Name=AttendanceUtils.getPartyName(delegator, partyId);
						employee.put("Name", Name);				
						
						List<String> departmentNameList=FastList.newInstance();
						departmentNameList=AttendanceUtils.getPartyDepartment(delegator,partyId,departmentNameList);               
						employee.put("Department", AttendanceUtils.getDepartmentName(departmentNameList));									
						
						List<GenericValue>	emplPositionAndEmplPositionTypes = delegator.findByAnd("EmplPositionAndEmplPositionType",UtilMisc.toMap("emplPositionId", emplPositionId));
						GenericValue emplPositionAndEmplPositionType=EntityUtil.getFirst(emplPositionAndEmplPositionTypes);
						
						if (!UtilValidate.isEmpty(emplPositionAndEmplPositionType)){
							 Designation=emplPositionAndEmplPositionType.getString("description");
							 employee.put("Designation", Designation);
						}						
						
						holidayOTLedgerList.add(employee);
				
				}else if(empType.equalsIgnoreCase("WORKERS")){
						long holidayWorkHours=0;
						long totalHolidayWorkHours=0;
						Collections.sort(holidays);
						for(Timestamp holiday:holidays){
						Map<String, Object> holidayWork = FastMap.newInstance();
						Date day=new Date(holiday.getTime());						
						Calendar cal = Calendar.getInstance();  
						cal.setTime(day);  
						int dayNumber = cal.get(Calendar.DAY_OF_MONTH);						
						long workingHour=Attendance.getWorkingHour(delegator, partyId, orgPartyId, day);
						holidayWorkHours = holidayWorkHours+workingHour;
						holidayWork.put(String.valueOf(dayNumber), String.valueOf(workingHour));
						
						holidayWorkKey.add(String.valueOf(dayNumber));
						holidayWorkData.add(String.valueOf(workingHour));

				    	}
					
					if(holidayWorkHours>0){
				            employee.put("holidayWorkKey", holidayWorkKey);
				            employee.put("holidayWorkData", holidayWorkData);
				            employee.put("totalHolidayWorkHours", String.valueOf(holidayWorkHours));

							String emplPositionId = AttendanceUtils.getPositionIdByPatyId(delegator, partyId);

							PIN=AttendanceUtils.getUserLoginIDFromPartyID(delegator, partyId);
							employee.put("PIN", PIN);
							Name=AttendanceUtils.getPartyName(delegator, partyId);
							employee.put("Name", Name);				
							
							List<String> departmentNameList=FastList.newInstance();
							departmentNameList=AttendanceUtils.getPartyDepartment(delegator,partyId,departmentNameList);               
							employee.put("Department", AttendanceUtils.getDepartmentName(departmentNameList));									
							
							List<GenericValue>	emplPositionAndEmplPositionTypes = delegator.findByAnd("EmplPositionAndEmplPositionType",UtilMisc.toMap("emplPositionId", emplPositionId));
							GenericValue emplPositionAndEmplPositionType=EntityUtil.getFirst(emplPositionAndEmplPositionTypes);
							
							if (!UtilValidate.isEmpty(emplPositionAndEmplPositionType)){
								 Designation=emplPositionAndEmplPositionType.getString("description");
								 employee.put("Designation", Designation);
							}
							
							String cardId = AttendanceUtils.getCardIdFromPartyId(delegator, partyId);
							employee.put("CardId", cardId);									

							holidayOTLedgerList.add(employee);
						
					}
					}

			}
		}

		result.put("holidayOTLedgerList", holidayOTLedgerList);
		result.put("fromToDate", "From : " + dateFormat.format(fromTime)
				+ " To : " + dateFormat.format(toTime));
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Employee Holiday Work Ledger");

		return result;
	}
	
	/** 
	 * @param 
	 * @return 
	 * @author zzz
	 * @throws GenericEntityException 
	 * @throws ParseException 
	 * @see.
	**/
	
	public static Map<String, Object> findHolidayWorkSummery(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException, ParseException {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		Delegator delegator = dctx.getDelegator();

		String orgPartyId = (String) context.get("orgPartyId");
		String unitParyId=orgPartyId;
		String fromDate = (String) context.get("fromDate");
		String toDate = (String) context.get("toDate");
		String empType = (String) context.get("empType");
		java.sql.Date fromTime=null;
		java.sql.Date toTime=null;		
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		Locale locale = (Locale) context.get("locale");
        java.util.Date now = new java.util.Date();
    	List<GenericValue> partyList=FastList.newInstance();
		List<GenericValue> unitTree = FastList.newInstance();

		List<Map<String, String>> holidayWorkSummeryList = FastList.newInstance();
		double grandTotalHrs=0.0;
		double grandTotalTk=0.0;

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DecimalFormat df = new DecimalFormat("#,#,##,##0.00");		

	  	// filter on division
	    String division = (String) context.get("division");
		if (UtilValidate.isNotEmpty(division)) {
			unitParyId=division;
	        }
		
		// filter on department
		String department = (String) context.get("department");
		      
	    if (UtilValidate.isNotEmpty(department)) {
	    	unitParyId=department;
		   
	        }
	    
	    // filter on section
	    String section = (String) context.get("section");
	      
	    if (UtilValidate.isNotEmpty(section)) {
	    	unitParyId=section; 
	    }
	    // filter on line
	    String line = (String) context.get("line");
	    
	    if (UtilValidate.isNotEmpty(line)) {
	    	unitParyId=line;  
	    }

		if (fromDate != null && !fromDate.isEmpty() && toDate != null
				&& !toDate.isEmpty()) {
			
			if(fromDate!=null && !fromDate.isEmpty() && toDate!=null && !toDate.isEmpty()){
				try{
					fromTime=Date.valueOf(fromDate);
					toTime=Date.valueOf(toDate);
					
					unitTree = AttendanceUtils.getUnitTreeByUnitPartyId(delegator,unitTree, unitParyId);

					GenericValue rootParty = AttendanceUtils.getPartyRelationShipEntityByPartyId(delegator,unitParyId);
					unitTree.add(rootParty);
					
					
				}catch(Exception e){
					fromTime=new Date(System.currentTimeMillis());
				}
			}else{
				fromTime=new Date(System.currentTimeMillis());
			}
	
			for (GenericValue unit : unitTree) {

				Map<String, String> partyUnit = FastMap.newInstance();
				double basic=0.0;
				double totalPerHrsTk=0.0;
				double totalHrs=0.0;
				double totalTk=0.0;

				String partyIdTo = unit.getString("partyIdTo");
				String unitName = AttendanceUtils.getDepartmentName(delegator, partyIdTo);

				List<GenericValue> employeeList = FastList.newInstance();				
				employeeList = AttendanceUtils.getEmployeesByParantIdAndEmplType(delegator,employeeList,partyIdTo,empType);
								
				for(GenericValue party: employeeList){
					String partyId=party.getString("partyId");
					 double basicSalary=0.0;
					 double perHrsTaka=0.0;
					 double totalHours=0.0;
					 double holidayOTAmount=0.0;

					
					Map<String,Object> holidayOverTimeSummary= Attendance.findHolidayOvertime(delegator,partyId,empType,fromTime,toTime,orgPartyId);
					if(!holidayOverTimeSummary.get("holidayOTAmount").toString().equals("0")){
						
						if(UtilValidate.isNotEmpty(holidayOverTimeSummary.get("basicSalary"))){
							basicSalary=Double.parseDouble(holidayOverTimeSummary.get("basicSalary").toString());

						}else{
							basicSalary=0.0;
						}
						if(UtilValidate.isNotEmpty(holidayOverTimeSummary.get("perHrsTake"))){
							perHrsTaka = Double.parseDouble(holidayOverTimeSummary.get("perHrsTake").toString());
						}else{
							perHrsTaka = 0.0;
						}
						if(UtilValidate.isNotEmpty(holidayOverTimeSummary.get("totalHours"))){
							totalHours=Double.parseDouble(holidayOverTimeSummary.get("totalHours").toString());
						}else{
							totalHours=0.0;
						}
						if(UtilValidate.isNotEmpty(holidayOverTimeSummary.get("holidayOTAmount"))){
							holidayOTAmount=Double.parseDouble(holidayOverTimeSummary.get("holidayOTAmount").toString());
						}else{
							holidayOTAmount=0.0;
						}
											
					}
					
					basic=basic+basicSalary;
					totalPerHrsTk=totalPerHrsTk+perHrsTaka;
					totalHrs=totalHrs+totalHours;
					totalTk=totalTk+holidayOTAmount;
					
				}
				if(!(totalTk==0.0 || totalTk==0)){
					partyUnit.put("Department", unitName);
					partyUnit.put("basic", df.format(basic));
					partyUnit.put("totalPerHrsTk", df.format(totalPerHrsTk));
					partyUnit.put("totalHrs", df.format(totalHrs));
					partyUnit.put("totalTk", df.format(totalTk));
					holidayWorkSummeryList.add(partyUnit);
					
					grandTotalHrs=grandTotalHrs+totalHrs;
					grandTotalTk=grandTotalTk+totalTk;

				}

			}
			
		}

		ArrayList<Map> sortedholidayWorkSummeryList = AttendanceUtils.getSortedList(holidayWorkSummeryList);
		
		result.put("holidayWorkSummeryList", sortedholidayWorkSummeryList);
		result.put("fromToDate", "From : " + dateFormat.format(fromTime)
				+ " To : " + dateFormat.format(toTime));
		result.put("grandTotalHrs", df.format(grandTotalHrs));
		result.put("grandTotalTk", df.format(grandTotalTk));
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Employee Personal Info");

		return result;
	}
	
	
	/**
	 * @description find Staff ,Worker And total Bonus
	 * @author zzz
	 * @param mounth , orgPartyId
	 * @throws ParseException 
	 */
	
	public static Map<String, Object> findAttendanceBonus(DispatchContext dctx,
			Map<String, ? extends Object> context) throws ParseException {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		String selectMonthYear = (String) context.get("selectMonthYear");
		String orgPartyId = (String) context.get("orgPartyId");		
		Delegator delegator = dctx.getDelegator();
        DecimalFormat df = new DecimalFormat("#,##,###.00");		
		double totalStaffBonus=0.0;
		double totalWorkerBonus=0.0;
		double totalBonus=0.0;
    	List<GenericValue> staffList=FastList.newInstance();
    	List<GenericValue> workerList=FastList.newInstance();
			
			if(selectMonthYear!=null&&!selectMonthYear.isEmpty()){

				staffList=AttendancePreferences.getTotalEmployeeByPositionAndEmpType(delegator,staffList,orgPartyId,"STAFF");
				workerList=AttendancePreferences.getTotalEmployeeByPositionAndEmpType(delegator,workerList,orgPartyId,"WORKERS");

			   for(GenericValue staff:staffList){
				   
				   String partyId=staff.getString("partyId");
				   String bonus="";
				   bonus =Attendance.findMonthlyAttendanceBonus(delegator,partyId,selectMonthYear,orgPartyId);
				   totalStaffBonus=totalStaffBonus+Double.parseDouble(bonus);
		      	}
			   for(GenericValue worker:workerList){
				   
				   String partyId=worker.getString("partyId");
				   String bonus="";
				   bonus =Attendance.findMonthlyAttendanceBonus(delegator,partyId,selectMonthYear,orgPartyId);
				   totalWorkerBonus=totalWorkerBonus+Double.parseDouble(bonus);

		      	}
			   totalBonus=totalStaffBonus+totalWorkerBonus;
			} 

	    result.put("totalStaffBonus", df.format(totalStaffBonus)+" TK");
	    result.put("totalWorkerBonus", df.format(totalWorkerBonus)+" TK");
	    result.put("totalBonus", df.format(totalBonus)+" TK");
		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Staff and Worker Attendance Bonus ");

		return result;
	}
	
	/**
	 * @description find Worker And total over Time Amount
	 * @author zzz
	 * @param mounth , orgPartyId
	 * @throws ParseException 
	 */
	
	public static Map<String, Object> findOvertimeStatement(DispatchContext dctx,
			Map<String, ? extends Object> context) throws ParseException {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		String selectMonthYear = (String) context.get("selectMonthYear");
		int monthNumber=0;
		int year=0;
		//String monthYear[] = selectMonthYear.split(" ");
		//monthNumber= AttendanceUtils.getMonthNumberFromName(monthYear[0]);
		java.sql.Date monthYearInDate=null;		
		monthYearInDate=AttendanceUtils.getsqltDate(selectMonthYear);
		Calendar cal = Calendar.getInstance();
        cal.setTime(monthYearInDate);
        monthNumber =cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
		//year= Integer.parseInt(monthYear[1]);
		String orgPartyId = (String) context.get("orgPartyId");		
		Delegator delegator = dctx.getDelegator();
        DecimalFormat df = new DecimalFormat("#,##,###.00");		
		double totalWorkerOverTime=0.0;
		double TotaloverTimeAmount = 0.0;
    	List<GenericValue> workerList=FastList.newInstance();
			
			if(selectMonthYear!=null&&!selectMonthYear.isEmpty()){

				workerList=AttendancePreferences.getTotalEmployeeByPositionAndEmpType(delegator,workerList,orgPartyId,"WORKERS");

			   for(GenericValue worker:workerList){
				   
				   String partyId=worker.getString("partyId");
				//   String bonus="0.0";
				  // bonus =Attendance.findMonthlyAttendanceBonus(delegator,partyId,selectMonthYear,orgPartyId);
				  
				   
		       Map overTimeAmountMap = SalaryWorker.getJobCardInfo(delegator, orgPartyId, partyId, String.valueOf(monthNumber), String.valueOf(year));
		       BigDecimal overTimeAmountBigDecimal = null;
		        if (UtilValidate.isNotEmpty(overTimeAmountMap.get("totalotAmount"))) {
		              overTimeAmountBigDecimal = new BigDecimal(overTimeAmountMap.get("totalotAmount").toString());
		              overTimeAmountBigDecimal = overTimeAmountBigDecimal.setScale(2, RoundingMode.HALF_UP);
		           
				     TotaloverTimeAmount=TotaloverTimeAmount+overTimeAmountBigDecimal.doubleValue(); 

		        }
		        

		      	}
			} 

	    result.put("totalWorkerOverTimeAmount", df.format(TotaloverTimeAmount)+" TK");
		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Worker Over Time Amount ");

		return result;
	}
	
	/** 
	 * @param 
	 * @return 
	 * @author zzz
	 * @throws GenericEntityException 
	 * @throws ParseException 
	 * @see.
	**/
	
	public static Map<String, Object> findExtraOvertimeSummary(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException, ParseException {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		Delegator delegator = dctx.getDelegator();

		String orgPartyId = (String) context.get("orgPartyId");
		String unitParyId=orgPartyId;
		String fromDate = (String) context.get("fromDate");
		String toDate = (String) context.get("toDate");
		String empType = (String) context.get("empType");
		java.sql.Date fromTime=null;
		java.sql.Date toTime=null;	
		long stime=0;
		long etime=0;
		long tempStartTime=0;
		long tempEndTime=0;

		TimeZone timeZone = (TimeZone) context.get("timeZone");
		Locale locale = (Locale) context.get("locale");
        java.util.Date now = new java.util.Date();
    	List<GenericValue> partyList=FastList.newInstance();
		List<GenericValue> unitTree = FastList.newInstance();

		List<Map<String, String>> extraOtSummeryList = FastList.newInstance();
		double grandTotalHrs=0.0;
		double grandTotalTk=0.0;
		

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DecimalFormat df = new DecimalFormat("#,#,##,##0.00");		

	  	// filter on division
	    String division = (String) context.get("division");
		if (UtilValidate.isNotEmpty(division)) {
			unitParyId=division;
	        }
		
		// filter on department
		String department = (String) context.get("department");
		      
	    if (UtilValidate.isNotEmpty(department)) {
	    	unitParyId=department;
		   
	        }
	    
	    // filter on section
	    String section = (String) context.get("section");
	      
	    if (UtilValidate.isNotEmpty(section)) {
	    	unitParyId=section; 
	    }
	    // filter on line
	    String line = (String) context.get("line");
	    
	    if (UtilValidate.isNotEmpty(line)) {
	    	unitParyId=line;  
	    }

		if (fromDate != null && !fromDate.isEmpty() && toDate != null
				&& !toDate.isEmpty()) {
			
			if(fromDate!=null && !fromDate.isEmpty() && toDate!=null && !toDate.isEmpty()){
				try{
					fromTime=Date.valueOf(fromDate);
					toTime=Date.valueOf(toDate);
					
					tempStartTime=AttendanceUtils.getStartTime(fromTime);
					tempEndTime=AttendanceUtils.getEndTime(toTime);
					
					unitTree = AttendanceUtils.getUnitTreeByUnitPartyId(delegator,unitTree, unitParyId);

					GenericValue rootParty = AttendanceUtils.getPartyRelationShipEntityByPartyId(delegator,unitParyId);
					unitTree.add(rootParty);
					
					
				}catch(Exception e){
					fromTime=new Date(System.currentTimeMillis());
				}
			}else{
				fromTime=new Date(System.currentTimeMillis());
			}
	
			for (GenericValue unit : unitTree) {

				Map<String, String> partyUnit = FastMap.newInstance();
				double basic=0.0;
				double totalPerHrsTk=0.0;
				double totalHrs=0.0;
				double totalTk=0.0;

				String partyIdTo = unit.getString("partyIdTo");
				String unitName = AttendanceUtils.getDepartmentName(delegator, partyIdTo);

				List<GenericValue> employeeList = FastList.newInstance();				
				employeeList = AttendanceUtils.getEmployeesByParantIdAndEmplType(delegator,employeeList,partyIdTo,empType);
								
				for(GenericValue party: employeeList){
					String partyId=party.getString("partyId");
					 double basicSalary=0.0;
					 double perHrsTaka=0.0;
					 double totalHours=0.0;
					 double holidayOTAmount=0.0;	
					 
					stime=tempStartTime;
					etime=tempEndTime;
					 
					String basicString=AttendanceUtils.getBasicSalaryByPartyId(delegator, partyId);
					if(basicString!=null && !basicString.isEmpty()){
						basicSalary=Double.parseDouble(basicString);
					}	
					double totalEmpTK = 0;
					
					double empAmount=0;
					double eotHours = 0;
					double hoursPerTaka = 0;	
					
					while(stime<etime){
						Timestamp startTimestamp=AttendanceUtils.getModifiedDayStart(new Timestamp(stime));
						Timestamp endTimestamp=AttendanceUtils.getModifiedDayEnd(new Timestamp(stime));
						
						Calendar cal = Calendar.getInstance();  
						cal.setTime(new Date(startTimestamp.getTime()));  
						int dayIn = cal.get(Calendar.DAY_OF_MONTH);
						
						Map<String,Double> EotAmounts = AttendanceUtils.findEOTByEmplPartyId(delegator,partyId,startTimestamp,
														endTimestamp,orgPartyId);
						
						empAmount = EotAmounts.get("eotAmount");
						eotHours += EotAmounts.get("eotHours");
						
						if(empAmount>0){
							totalEmpTK +=empAmount;
						}
						stime+=86400000;
					}
					
					if(eotHours>0) hoursPerTaka = totalEmpTK/eotHours;
					
					totalEmpTK = Math.round(totalEmpTK);
					
					basic=basic+basicSalary;
					totalPerHrsTk=totalPerHrsTk+hoursPerTaka;
					totalHrs=totalHrs+eotHours;
					totalTk=totalTk+totalEmpTK;
					
				}
				if(!(totalTk==0.0 || totalTk==0)){
					partyUnit.put("Department", unitName);
					partyUnit.put("basic", df.format(basic));
					partyUnit.put("totalPerHrsTk", df.format(totalPerHrsTk));
					partyUnit.put("totalHrs", df.format(totalHrs));
					partyUnit.put("totalTk", df.format(totalTk));
					extraOtSummeryList.add(partyUnit);
					
					grandTotalHrs=grandTotalHrs+totalHrs;
					grandTotalTk=grandTotalTk+totalTk;

				}

			}
			
		}

		ArrayList<Map> sortedExtraOtSummeryList = AttendanceUtils.getSortedList(extraOtSummeryList);
		
		result.put("extraOtSummeryList", sortedExtraOtSummeryList);
		result.put("fromToDate", "From : " + dateFormat.format(fromTime)
				+ " To : " + dateFormat.format(toTime));
		result.put("grandTotalHrs", df.format(grandTotalHrs));
		result.put("grandTotalTk", df.format(grandTotalTk));
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Statement Of Extra OT Summery");

		return result;
	}
	
	
	
	/** 
	 * @param 
	 * @return 
	 * @author zzz
	 * @throws GenericEntityException 
	 * @throws ParseException 
	 * @see.
	**/
	
	public static Map<String, Object> findSalaryManpower(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException, ParseException {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		Delegator delegator = dctx.getDelegator();

		String orgPartyId = (String) context.get("orgPartyId");
		String unitParyId=orgPartyId;
		String selectMonthYear = (String) context.get("selectMonthYear");
		java.sql.Date monthYearInDate=null;		
		monthYearInDate=AttendanceUtils.getsqltDate(selectMonthYear);

		TimeZone timeZone = (TimeZone) context.get("timeZone");
		Locale locale = (Locale) context.get("locale");
		List<GenericValue> unitTree = FastList.newInstance();

		List<Map<String, String>> salaryManpowerList = FastList.newInstance();
		int totalStaff=0;
		int totalWorker=0;
		int totalManager=0;
		int totalAll=0;

	    if(selectMonthYear!=null&&!selectMonthYear.isEmpty()){
			
				try{
					
					unitTree = AttendanceUtils.getUnitTreeByUnitPartyId(delegator,unitTree, unitParyId);
					GenericValue rootParty = AttendanceUtils.getPartyRelationShipEntityByPartyId(delegator,unitParyId);
					unitTree.add(rootParty);					
					
				}catch(Exception e){
				}
			
	
			for (GenericValue unit : unitTree) {

				Map<String, String> partyUnit = FastMap.newInstance();

				String partyIdTo = unit.getString("partyIdTo");
				String unitName = AttendanceUtils.getDepartmentName(delegator, partyIdTo);

				List<GenericValue> staffList = FastList.newInstance();
				List<GenericValue> workerList = FastList.newInstance();
				List<GenericValue> managerList = FastList.newInstance();

				staffList=AttendanceUtils.getStaffByParantIdAndEmplType(delegator,staffList,partyIdTo,"STAFF");				
				staffList=AttendanceUtils.filterValidEmployeeListByDate(delegator, staffList, new Timestamp(monthYearInDate.getTime()));
				
				workerList=AttendanceUtils.getWorkersByParantIdAndEmplType(delegator,workerList,partyIdTo,"WORKERS");
				workerList=AttendanceUtils.filterValidEmployeeListByDate(delegator, workerList, new Timestamp(monthYearInDate.getTime()));
				
				managerList=AttendanceUtils.getManagersByParantId(delegator,managerList,partyIdTo);
				managerList=AttendanceUtils.filterValidEmployeeListByDate(delegator, managerList, new Timestamp(monthYearInDate.getTime()));
				
				totalStaff=totalStaff+staffList.size();
				totalWorker=totalWorker+workerList.size();
				totalManager=totalManager+managerList.size();
				
				int total=staffList.size()+workerList.size()+managerList.size();
				totalAll=totalAll+total;
				
				if(total>0){
				partyUnit.put("worker", String.valueOf(workerList.size()));
				partyUnit.put("staff", String.valueOf(staffList.size()));
				partyUnit.put("manager", String.valueOf(managerList.size()));
				partyUnit.put("total", String.valueOf(total));
				partyUnit.put("Department", unitName);
				salaryManpowerList.add(partyUnit);
				}
			}

			
		}

		ArrayList<Map> sortedsalaryManpowerList = AttendanceUtils.getSortedList(salaryManpowerList);
		
		result.put("salaryManpowerList", salaryManpowerList);
		result.put("totalStaff", String.valueOf(totalStaff));
		result.put("totalWorker", String.valueOf(totalWorker));
		result.put("totalManager", String.valueOf(totalManager));
		result.put("totalAll", String.valueOf(totalAll));
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Salary Manpower");

		return result;
	}
	/** 
	 * @param 
	 * @return 
	 * @author zzz
	 * @throws GenericEntityException 
	 * @throws ParseException 
	 * @see.
	**/
	public static Map<String, Object> findSalaryOtSummery(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException, ParseException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		String orgPartyId = (String) context.get("orgPartyId");
		String unitParyId=orgPartyId;
		String selectMonthYear = (String) context.get("selectMonthYear");
		java.sql.Date monthYearInDate=null;		
		monthYearInDate=AttendanceUtils.getsqltDate(selectMonthYear);
		String empType = (String) context.get("empType");
		int monthNumber=0;
		int fiscalYear=0;
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		Locale locale = (Locale) context.get("locale");
		List<GenericValue> unitTree = FastList.newInstance();
		List<Map<String, String>> salaryOtSummeryList = FastList.newInstance();
        DecimalFormat df = new DecimalFormat("#,#,##,##0.00");		

	  	// filter on division
	    String division = (String) context.get("division");
		if (UtilValidate.isNotEmpty(division)) {
			unitParyId=division;
	        }		
		// filter on department
		String department = (String) context.get("department");
		      
	    if (UtilValidate.isNotEmpty(department)) {
	    	unitParyId=department;		   
	        }	    
	    // filter on section
	    String section = (String) context.get("section");	      
	    if (UtilValidate.isNotEmpty(section)) {
	    	unitParyId=section; 
	    }
	    // filter on line
	    String line = (String) context.get("line");
	    
	    if (UtilValidate.isNotEmpty(line)) {
	    	unitParyId=line;  
	    }
		if (selectMonthYear != null && !selectMonthYear.isEmpty()) {			
				try{					
					Calendar cal = Calendar.getInstance();  
					cal.setTime(new Date(monthYearInDate.getTime()));  
					monthNumber = cal.get(Calendar.MONTH);
				    fiscalYear = cal.get(Calendar.YEAR);
					unitTree = AttendanceUtils.getUnitTreeByUnitPartyId(delegator,unitTree, unitParyId);
					GenericValue rootParty = AttendanceUtils.getPartyRelationShipEntityByPartyId(delegator,unitParyId);
					unitTree.add(rootParty);										
				}catch(Exception e){
				}			
			for (GenericValue unit : unitTree) {
				Map<String, String> partyUnit = FastMap.newInstance();
				String partyIdTo = unit.getString("partyIdTo");
				String unitName = AttendanceUtils.getDepartmentNameInBangla(delegator, partyIdTo);
				if (unitName==null){
					unitName="নাম সেট হয়নি "; 
				}
				double  totalbasic=0;
				double leaveWithoutPayAmount=0;
				double absentDedAmount=0;
				double totalLoanAmount=0;
				double totalstampDed=0;
				double totalCutAmount=0;
				double totalRecAmount=0;
				double totalOtAmount=0;
				double totalAttenBonus=0;
				double totalAmount=0;
				
				List<GenericValue> employeeList = FastList.newInstance();				
				employeeList = AttendanceUtils.getEmployeesByParantIdAndEmplType(delegator,employeeList,partyIdTo,empType);							
				for(GenericValue party: employeeList){
					String partyId=party.getString("partyId");					
					String basicSalary =AttendanceUtils.getBasicSalaryByPartyId(delegator, partyId);
					if(UtilValidate.isNotEmpty(basicSalary)){
		  				totalbasic=totalbasic+Double.parseDouble(basicSalary);
		  			}
					
					List<GenericValue> emplSalaryBenefits = FastList.newInstance();
		  			emplSalaryBenefits = delegator.findByAnd("EmplSalaryBenefit", UtilMisc.toMap("employeePartyId",partyId,
		  										"monthNumber",String.valueOf(monthNumber),"fiscalYear",String.valueOf(fiscalYear)));

		  			if(!emplSalaryBenefits.isEmpty()){
			  			GenericValue emplSalaryBenefit= EntityUtil.getFirst(emplSalaryBenefits); 
			  			if(UtilValidate.isNotEmpty(emplSalaryBenefit.get("leaveWithoutPayDed"))){
			  				leaveWithoutPayAmount=leaveWithoutPayAmount+emplSalaryBenefit.getDouble("leaveWithoutPayDed");
			  			}
			  			if(UtilValidate.isNotEmpty(emplSalaryBenefit.get("absentDed"))){
			  				absentDedAmount = absentDedAmount+emplSalaryBenefit.getDouble("absentDed");
			  			}
			  			if(UtilValidate.isNotEmpty(emplSalaryBenefit.get("stampDed"))){
			  				totalstampDed = totalstampDed+emplSalaryBenefit.getDouble("stampDed");
			  			}
			  			if(UtilValidate.isNotEmpty(emplSalaryBenefit.get("otAmount"))){
			  				totalOtAmount = totalOtAmount+emplSalaryBenefit.getDouble("otAmount");
			  			}
			  			if(UtilValidate.isNotEmpty(emplSalaryBenefit.get("attendanceBonus"))){
			  				totalAttenBonus = totalAttenBonus+emplSalaryBenefit.getDouble("attendanceBonus");
			  			}
		  			}		  			
		  			List<GenericValue> invoiceItemType = FastList.newInstance();
		  		   // getting property value
					String employeeLoanGlAccountId = UtilProperties.getPropertyValue("contessa.properties","employeeLoanGlAccountId");
					// payroll loan
					invoiceItemType = delegator.findByAnd("InvoiceItemType", UtilMisc.toMap("defaultGlAccountId",employeeLoanGlAccountId));					
					String invItemTypeLoan = "";
					// get loan amount by partyId 
					if(UtilValidate.isNotEmpty(invoiceItemType)){
						invItemTypeLoan = invoiceItemType.get(0).getString("invoiceItemTypeId");
						if(UtilValidate.isNotEmpty(invItemTypeLoan)){
						List<GenericValue> loans = FastList.newInstance(); 
						loans = delegator.findByAnd("InvoiceAndInvoiceItem", UtilMisc.toMap("partyIdFrom", partyId,"invoiceItemTypeId",invItemTypeLoan,"statusId","INVOICE_READY","month",String.valueOf(monthNumber),"fiscalYear",String.valueOf(fiscalYear)));
						if(!loans.isEmpty()){
						 GenericValue loan = EntityUtil.getFirst(loans);
						 if(UtilValidate.isNotEmpty(loan.get("amount"))){
							 totalLoanAmount=totalLoanAmount + loan.getDouble("amount");
						    }
						   }
					      }						
					    }							
				}
				
				    totalCutAmount= leaveWithoutPayAmount+absentDedAmount+totalLoanAmount+totalstampDed;
				    totalRecAmount = totalbasic- totalCutAmount;
				    totalAmount=totalRecAmount+totalOtAmount+totalAttenBonus;
				    
					partyUnit.put("Department", unitName);
					partyUnit.put("numberOfemployees",HumanResEvents.convertNumberToBangla(String.valueOf(employeeList.size())));
					partyUnit.put("totalbasic", HumanResEvents.convertNumberToBangla(df.format(totalbasic)));
					partyUnit.put("leaveWithoutPayAmount",HumanResEvents.convertNumberToBangla(df.format(leaveWithoutPayAmount)));
					partyUnit.put("absentDedAmount",HumanResEvents.convertNumberToBangla(df.format(absentDedAmount)));
					partyUnit.put("totalLoanAmount",HumanResEvents.convertNumberToBangla(df.format(totalLoanAmount)));
					partyUnit.put("totalstampDed",HumanResEvents.convertNumberToBangla(df.format(totalstampDed)));
					partyUnit.put("totalCutAmount",HumanResEvents.convertNumberToBangla(df.format(totalCutAmount)));
					partyUnit.put("totalRecAmount",HumanResEvents.convertNumberToBangla(df.format(totalRecAmount)));
					partyUnit.put("totalOtAmount",HumanResEvents.convertNumberToBangla(df.format(totalOtAmount)));
					partyUnit.put("totalAttenBonus",HumanResEvents.convertNumberToBangla(df.format(totalAttenBonus)));
					partyUnit.put("totalAmount", HumanResEvents.convertNumberToBangla(df.format(totalAmount)));
					partyUnit.put("totalPayableAmount",HumanResEvents.convertNumberToBangla(df.format(Math.round(totalAmount))));

					if(employeeList.size()>0 && totalbasic>0){
						salaryOtSummeryList.add(partyUnit);						
					}		

			}
			
		}
		ArrayList<Map> sortedsalaryOtSummeryList = AttendanceUtils.getSortedList(salaryOtSummeryList);		
		result.put("salaryOtSummeryList", sortedsalaryOtSummeryList);
		result.put("monthYearBanglaFormat", SalaryWorker.getMonthBanglaName(String.valueOf(monthNumber))+", "+
		HumanResEvents.convertNumberToBangla(String.valueOf(fiscalYear)));
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Salary and OT Summery");

		return result;
	}
	/** 
	 * @param 
	 * @return 
	 * @author zzz
	 * @throws GenericEntityException 
	 * @throws ParseException 
	 * @see.
	**/
	public static Map<String, Object> findRecruitAbsenteeismMigration(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		Delegator delegator = dctx.getDelegator();

		String fromDate = (String) context.get("fromDate");
		String toDate = (String) context.get("toDate");
		String orgPartyId = (String) context.get("orgPartyId");
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		Locale locale = (Locale) context.get("locale");
		double migrationPercent = 0.0;
		int absentManPower=0;
		int days=0;
		double absentPercent=0;

		Map<String, String> recruitAbsentMigraData = FastMap.newInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat dateFt = new SimpleDateFormat("yyyy-MM-dd");

		java.sql.Date fromTime = null;
		java.sql.Date toTime = null;
		List<GenericValue> partyList=FastList.newInstance();
		List<Timestamp> holidays=new ArrayList<Timestamp>();

		logger.debug("orgPartyId-------->" + orgPartyId);

		if (fromDate != null && !fromDate.isEmpty() && toDate != null
				&& !toDate.isEmpty()) {

			if (fromDate != null && !fromDate.isEmpty() && toDate != null
					&& !toDate.isEmpty()) {

				fromTime = Date.valueOf(fromDate);
				toTime = Date.valueOf(toDate);
				Timestamp dayStart = UtilDateTime.getDayStart(new Timestamp(
						fromTime.getTime()));
				Timestamp dayEnd = UtilDateTime.getDayEnd(new Timestamp(toTime
						.getTime()));
				holidays=AttendanceUtils.getHolidays(orgPartyId, new Timestamp(fromTime.getTime()), new Timestamp(toTime.getTime()), delegator);
				Map<String, String> data = FastMap.newInstance();
                partyList=AttendancePreferences.getTotalEmployee(delegator,partyList,orgPartyId);
                List<GenericValue> inactiveList = AttendanceUtils
    					.filterInActiveEmployeesByDate(delegator, partyList,
    							dayStart, dayEnd);
		        partyList=AttendanceUtils.filterValidEmployeeListByDate(delegator, partyList, dayStart);
		        recruitAbsentMigraData.put("firstDayManPowers", String.valueOf(partyList.size()));
		        recruitAbsentMigraData.put("resignedManPowers", String.valueOf(inactiveList.size()));
		        if(partyList.size()>0){
			        migrationPercent= (float)((float)100*inactiveList.size())/partyList.size();
		        }
		        recruitAbsentMigraData.put("MigrationPercent", String.format("%.2f",migrationPercent));
		        
				Calendar start = Calendar.getInstance();
		        start.setTime(fromTime);
		        Calendar end = Calendar.getInstance();
		        end.setTime(toTime);
		        
		        //
		        
	        for (java.util.Date date =  start.getTime(); !start.after(end); start.add(Calendar.DATE, 1), date =  start.getTime()) 
			    {
		          if(holidays.contains(date)){  
			         continue;
			       }	
		          for(GenericValue party : partyList){
		        	  String partyId = party.getString("partyId");

		        	   List<EntityCondition> exprsAnd = FastList.newInstance();
					   Set<String> fieldsToSelect = UtilMisc.toSet("logtimeStamp");
					   List<String> orderBy = FastList.newInstance();
					   orderBy.add("logtimeStamp ASC");
					   String searchDate=dateFt.format(date);
					   
					   exprsAnd.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			           exprsAnd.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("logtimeStamp"), EntityOperator.LIKE, EntityFunction.UPPER(searchDate + "%")));
			            
			           List<GenericValue> empLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprsAnd, EntityOperator.AND), fieldsToSelect,orderBy , null, true);

			           if(empLogs.size()<=0)
			          	   {
			        	    absentManPower++;
			  		        recruitAbsentMigraData.put("absentManPower", String.valueOf(absentManPower));			          	        	   
			          	   }else{
			          	        	   
			          	   }  
		          }
	               days++;
				}
		        
		        recruitAbsentMigraData.put("TotalManPowerInDays", String.valueOf(partyList.size()*days));
		        if(partyList.size()>0){
			        absentPercent= (float)((float)100*absentManPower)/(Integer.parseInt(recruitAbsentMigraData.get("TotalManPowerInDays").toString()));
			        recruitAbsentMigraData.put("absentPercent", String.format("%.2f", absentPercent));
		        }
		        
		        List<EntityExpr> exprs = FastList.newInstance();
				exprs.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO,dayStart));
				exprs.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
				exprs.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, orgPartyId));
		        
				List<GenericValue> employements= delegator.findList("Employment", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, true);
				recruitAbsentMigraData.put("RecruitedManPower", String.valueOf(employements.size()));
				if(employements.size()>0){
					float recruitedPercent = (float)((float)employements.size()*100)/Integer.parseInt(recruitAbsentMigraData.get("firstDayManPowers"));
				recruitAbsentMigraData.put("RecruitedPercent", String.format("%.2f",recruitedPercent));
				}
				
				
		        //

			}

		}

		result.put("recruitAbsentMigraData", recruitAbsentMigraData);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Recruit Absent Migration");

		return result;
	}
	/**
	 * @description findExtraOvertimePaymentSheet
	 * @author tomal mahdi
	 * @throws ParseException 
	 */
	public static Map<String, Object> findExtraOvertimePaymentSheet(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException, ParseException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		List<Map<String, Object> > listExtraOvertimePaymentSheet=FastList.newInstance();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy"); 
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
		Delegator delegator = dctx.getDelegator();
		
		String fromDate = (String) context.get("fromDate");
		String toDate = (String) context.get("toDate");
		
		String orgPartyId = (String) context.get("orgPartyId");
		String unitPartyId=orgPartyId;
		// filter on division
	    String division = (String) context.get("division");
		if(UtilValidate.isNotEmpty(division)){
				unitPartyId=division;
	    }
		// filter on department
		String department = (String) context.get("department");
		if(UtilValidate.isNotEmpty(department)){
	    		unitPartyId=department;   
	    }
	    // filter on section
	    String section = (String) context.get("section");
	    if(UtilValidate.isNotEmpty(section)){
        	unitPartyId=section; 
        }
	    // filter on line
        String line = (String) context.get("line");
        if (UtilValidate.isNotEmpty(line)) {
        	unitPartyId=line;  
        }
        /*logger.debug("orgPartyId-------->"+orgPartyId);*/
        
        java.sql.Date fromDateToSqlDate = null;
		java.sql.Date toDateToSqlDate = null;
		long stime=0;
		long etime=0;
		Timestamp fromTime =null;
		Timestamp toTime =null;
		if(fromDate!=null&&!fromDate.isEmpty()){
			fromDateToSqlDate=Date.valueOf(fromDate);
			fromTime = new Timestamp(fromDateToSqlDate.getTime()); 
			if(toDate!=null && toDate.length()>0){
				toDateToSqlDate=Date.valueOf(toDate);
				toTime = new Timestamp(toDateToSqlDate.getTime());
			}else{
				toDateToSqlDate=Date.valueOf(fromDate);
				toTime = new Timestamp(toDateToSqlDate.getTime());
			}
		}
		double totalLeave = 0,totalFulldayAbsent = 0,HalfdayAbsent = 0;
		long totalHoliday = 0;
		List<GenericValue> partyList=FastList.newInstance();
		partyList=AttendanceUtils.getTotalActiveEmployee(delegator, partyList, unitPartyId);
		List<GenericValue> emplLeave = FastList.newInstance();
		if(UtilValidate.isNotEmpty(fromTime) && UtilValidate.isNotEmpty(toTime)){
			for(GenericValue party:partyList){
				
				String partyId = party.getString("partyId");
				String employeeId = AttendanceUtils.getUserLoginIDFromPartyID(delegator, partyId);
				
				GenericValue person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId",partyId.trim()));
				
				String employeeName = "";
				if(UtilValidate.isNotEmpty(person)){
					String firstName =person.getString("firstName");
					String middleName =person.getString("middleName");
					String lastName =person.getString("lastName");
					
//					getting employee fullname
					if(firstName!=null){ employeeName+=(firstName+" ");}
					if(middleName!=null){ employeeName+=(middleName+" "); }
					if(lastName!=null){ employeeName+=lastName; }
				}
//				getting employee designation
				String designation = AttendanceUtils.getPartyDesignation(delegator,partyId);
				if(UtilValidate.isEmpty(designation)){
					designation = "";
				}
//				getting payGrade for employee...
				String emplPostionId = AttendanceUtils.getPositionIdByPatyId(delegator, partyId);
				GenericValue emplPos = delegator.findByPrimaryKey("EmplPosition",UtilMisc.toMap("emplPositionId",emplPostionId));
				String payGradeId =""; 
				if(UtilValidate.isNotEmpty(emplPos)){
					payGradeId = emplPos.getString("payGradeId");
					if(UtilValidate.isNotEmpty(payGradeId)){
						GenericValue payGrade = delegator.findByPrimaryKey("PayGrade",UtilMisc.toMap("payGradeId",payGradeId));
						payGradeId = payGrade.getString("payGradeName");
					}else{
						payGradeId ="";
					}
				}
//				getting employee department
				List<String> departmentNameList=FastList.newInstance();
				departmentNameList=AttendanceUtils.getPartyDepartment(delegator,partyId,departmentNameList);
				String dept = AttendanceUtils.getDepartmentName(departmentNameList);
				if(UtilValidate.isEmpty(dept)){
					department = "";
				}
				
				String basicSalary =AttendanceUtils.getBasicSalaryByPartyId(delegator, partyId);
				
				stime=AttendanceUtils.getStartTime(fromDateToSqlDate);
				etime=AttendanceUtils.getEndTime(toDateToSqlDate);
				
				String[] ledger = new String[33];
				int cnt;
				// ledger initialization
				for(cnt=1;cnt<=31;cnt++){
					ledger[cnt]="";
				}
				
				double totalTK = 0;
				
				double amount=0;
				double eotHours = 0;
				double hoursPerTaka = 0;
				while(stime<etime){
					Timestamp startTimestamp=AttendanceUtils.getModifiedDayStart(new Timestamp(stime));
					Timestamp endTimestamp=AttendanceUtils.getModifiedDayEnd(new Timestamp(stime));
					
					Calendar cal = Calendar.getInstance();  
					cal.setTime(new Date(startTimestamp.getTime()));  
					int dayIn = cal.get(Calendar.DAY_OF_MONTH);
					
					Map<String,Double> EotAmounts = AttendanceUtils.findEOTByEmplPartyId(delegator,partyId,startTimestamp,
													endTimestamp,orgPartyId);
					
					amount = EotAmounts.get("eotAmount");
					eotHours += EotAmounts.get("eotHours");
					
					if(amount>0){
						totalTK +=amount;
						ledger[dayIn] = String.valueOf(EotAmounts.get("eotHours"));
					}
					stime+=86400000;
				}
				if(eotHours>0) hoursPerTaka = totalTK/eotHours;
				
				totalTK = Math.round(totalTK);
				if(totalTK>0){
					Map<String,Object> data = FastMap.newInstance();
					data.put("pin", employeeId);
					data.put("employeeName", employeeName);
					data.put("designation", designation);
					data.put("department", dept);
					data.put("grade", payGradeId);
					data.put("basicSalary", basicSalary);
					data.put("perHourTaka", String.format("%.2f",hoursPerTaka));
					data.put("totalHours",String.format("%.2f",eotHours));
					data.put("totalTk", String.format("%.2f",totalTK));
					data.put("ledger", ledger);
					
						
					listExtraOvertimePaymentSheet.add(data);
				}
			}
		}      
//		stime+=86400000; for each day...
		
		ArrayList<Map<String,Object>> sortedExtraOvertimePaymentSheetList = AttendanceUtils.getSortedList1(listExtraOvertimePaymentSheet);
		// for pdf...
		listExtraOvertimePaymentSheetPDF = sortedExtraOvertimePaymentSheetList;
		result.put("ListExtraOvertimePaymentSheet", sortedExtraOvertimePaymentSheetList);
		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Successful");
		return result;
	}
	
	/**
	 * @description findExtraOvertimePaymentSheet  PDF
	 * @author tomal mahdi
	 */
	public static Map<String, Object> findExtraOvertimePaymentSheetForPdf(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		result.put("ListExtraOvertimePaymentSheet", listExtraOvertimePaymentSheetPDF);
		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Successful");
		return result;
	}
	
	/**
	 * @description findExtraOvertimeStatement
	 * @author tomal mahdi
	 * @throws ParseException 
	 */
	public static Map<String, Object> findExtraOvertimeStatement(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException, ParseException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		List<Map<String, Object> > listExtraOvertimeStatement = FastList.newInstance();
		String selectMonthYear = (String) context.get("selectMonthYear");
		int monthNumber=0;
		int year = 0;
		java.sql.Date monthYearInDate=null;		
		monthYearInDate=AttendanceUtils.getsqltDate(selectMonthYear);
		
		if(UtilValidate.isNotEmpty(monthYearInDate)){
			// set first date in calendar...
			Calendar cal = Calendar.getInstance();
	        cal.setTime(monthYearInDate);
	        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
			
	        
	        DateFormat df = new SimpleDateFormat("dd/MM/yyyy"); 
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
			Delegator delegator = dctx.getDelegator();
			
			String orgPartyId = (String) context.get("orgPartyId");
			// filter on division
		    String division = (String) context.get("division");
			if(UtilValidate.isNotEmpty(division)){
					orgPartyId=division;
		    }
			// filter on department
			String department = (String) context.get("department");
			if(UtilValidate.isNotEmpty(department)){
					orgPartyId=department;   
		    }
		    // filter on section
		    String section = (String) context.get("section");
		    if(UtilValidate.isNotEmpty(section)){
		    		orgPartyId=section; 
	        }
		    // filter on line
	        String line = (String) context.get("line");
	        if (UtilValidate.isNotEmpty(line)){
	        		orgPartyId=line;  
	        }
	        logger.debug("orgPartyId-------->"+orgPartyId);
	        
	        java.sql.Date fromDateToSqlDate = new java.sql.Date(cal.getTimeInMillis());
	        
	        // last day of month...
	        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),cal.getActualMaximum(Calendar.DAY_OF_MONTH));
	        java.sql.Date toDateToSqlDate = new java.sql.Date(cal.getTimeInMillis());
		
		
			List<GenericValue> partyList=FastList.newInstance();
			partyList=AttendanceUtils.getTotalActiveEmployee(delegator, partyList, orgPartyId);
			
			double totalStaffAllowance = 0;
			double totalWorkerAllowance = 0;
			
			List<EntityExpr> exprs = FastList.newInstance();
//			List<String> orderBy = FastList.newInstance();
//			orderBy.add("logtimeStamp ASC");
			
			exprs.add(EntityCondition.makeCondition("eotAmount", EntityOperator.GREATER_THAN,new java.math.BigDecimal("0")));
			logger.debug("Condition for late entry------>"+exprs);
			
			double totalEOTAmount = 0;
			for(GenericValue party:partyList){
				String partyId = party.getString("partyId");	
				
				exprs.add(EntityCondition.makeCondition("employeePartyId", EntityOperator.EQUALS,partyId));
				exprs.remove(exprs.size()-1);
				List<GenericValue> employment = delegator.findByAnd("Employment",UtilMisc.toMap("partyIdTo",partyId));
				
				List<GenericValue> emplExtraSalaryBenefit = delegator.findList("EmplExtraSalaryBenefit", EntityCondition.makeCondition(exprs,EntityOperator.AND),null,null,null,true);
				if(UtilValidate.isNotEmpty(emplExtraSalaryBenefit)){
					java.math.BigDecimal eotAmount = emplExtraSalaryBenefit.get(0).getBigDecimal("eotAmount");
					if(UtilValidate.isNotEmpty(eotAmount)){
						totalEOTAmount+=eotAmount.doubleValue();
					}	
					if(UtilValidate.isNotEmpty(employment)){
						String emplType  =employment.get(0).getString("employmentType");
						if(UtilValidate.isNotEmpty(emplType) && emplType.equals("STAFF")){
							java.math.BigDecimal holidayOT = emplExtraSalaryBenefit.get(0).getBigDecimal("holidayOt");
							totalStaffAllowance += holidayOT.doubleValue();
						}
						if(UtilValidate.isNotEmpty(emplType) && emplType.equals("WORKERS")){
							java.math.BigDecimal holidayOT = emplExtraSalaryBenefit.get(0).getBigDecimal("holidayOt");
							totalWorkerAllowance += holidayOT.doubleValue();
						}
					}
				}
			}
//			String totalTakaWithFormat = String.format("%.2f",totalEOTAmount);
			Map<String,Object> data = FastMap.newInstance();
			data.put("totalTaka",totalEOTAmount);
			data.put("totalStaffAllowance",totalStaffAllowance);
			data.put("totalWorkerAllowance",totalWorkerAllowance);
			data.put("total",totalEOTAmount+totalStaffAllowance+totalWorkerAllowance);
			listExtraOvertimeStatement.add(data);
			}
		result.put("ListExtraOvertimeStatement", listExtraOvertimeStatement);
		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Successful");
		return result;
	}
}
