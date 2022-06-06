package org.ofbiz.humanres.report.profile;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.cache.UtilCache;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.humanres.KpiEvents;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.control.ContextFilter;
import org.ofbiz.webapp.view.ViewHandlerException;

//import com.thoughtworks.selenium.condition.DefaultConditionRunner.Monitor;

import javolution.util.FastList;
import javolution.util.FastMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AllEmployeeSheetDetails {

	public static String generateReport(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> ctx = UtilHttp.getParameterMap(request);
		JRDataSource jrDataSource = createReportDataSource(delegator, ctx);

		/* String partyId = request.getParameter("partyId"); */

		HashMap jrParameters = new HashMap();

		/* jrParameters.put("employeeName", "1234"); */

		request.setAttribute("jrDataSource", jrDataSource);
		request.setAttribute("jrParameters", jrParameters);
		return "success";
	}

	public static JRDataSource createReportDataSource(Delegator delegator, Map<String, Object> ctx) {
		JRMapCollectionDataSource dataSource;
		Collection reportRows = initializeMapCollection(delegator, ctx);
		dataSource = new JRMapCollectionDataSource(reportRows);
		return dataSource;
	}

	private static Collection initializeMapCollection(Delegator delegator, Map<String, Object> ctx) {

		List<GenericValue> employeeList = null;
		GenericValue employee = null;

		List<EntityExpr> exprs = FastList.newInstance();
		exprs.add(EntityCondition.makeCondition("employmentType", EntityOperator.NOT_EQUAL, null));
		try {
			employeeList = delegator.findList("EmploymentAndPerson",
					EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		/* String partyId = (String) ctx.get("partyId"); */
		ArrayList reportRows = new ArrayList();
		HashMap rowMap = new HashMap();

		for (GenericValue employeeValue : employeeList) {

			rowMap = new HashMap();
			String employeePartyId = null;
			if (UtilValidate.isNotEmpty(employeeValue)) {
				employeePartyId = employeeValue.getString("partyId");
			}

			String employeeName = SingleEmployeeProfileService.getAnEmployeeName(delegator, employeePartyId);
			String employeeId = SingleEmployeeProfileService.getAnEmployeeId(delegator, employeePartyId);
			String mobileno = SingleEmployeeProfileService.getAnEmployeeMobile(delegator, employeePartyId);
			String designation = SingleEmployeeProfileService.getAnEmployeeDesignation(delegator, employeePartyId);
			String bloodgroup = SingleEmployeeProfileService.getAnBloodGroup(delegator, employeePartyId);
			String department = SingleEmployeeProfileService.getAnDepartment(delegator, employeePartyId);
			String fatherName = SingleEmployeeProfileService.getAnFatherName(delegator, employeePartyId);
			String birthdate = SingleEmployeeProfileService.getAnDateofBirth(delegator, employeePartyId);
			String email = SingleEmployeeProfileService.getAnEmailAdress(delegator, employeePartyId);
			rowMap.put("employeeId", employeeId);
			rowMap.put("employeeName", employeeName);
			rowMap.put("mobileno", mobileno);
			rowMap.put("designation", designation);
			rowMap.put("bloodgroup", bloodgroup);
			rowMap.put("department", department);
			rowMap.put("fatherName", fatherName);
			rowMap.put("birthdate", birthdate);
			rowMap.put("email", email);
			reportRows.add(rowMap);
		}
		return reportRows;
	}

	public static String generateReportByParves(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> ctx = UtilHttp.getParameterMap(request);
		JRDataSource jrDataSource = createReportDataSourceByParves(delegator, ctx);

		/* String partyId = request.getParameter("partyId"); */

		HashMap jrParameters = new HashMap();

		/* jrParameters.put("employeeName", "1234"); */

		request.setAttribute("jrDataSource", jrDataSource);
		request.setAttribute("jrParameters", jrParameters);
		return "success";
	}

	public static JRDataSource createReportDataSourceByParves(Delegator delegator, Map<String, Object> ctx) {
		JRMapCollectionDataSource dataSource;
		Collection reportRows = initializeMapCollectionByParves(delegator, ctx);
		dataSource = new JRMapCollectionDataSource(reportRows);
		return dataSource;
	}

	private static Collection initializeMapCollectionByParves(Delegator delegator, Map<String, Object> ctx) {

		List<GenericValue> employeeList = null;
		GenericValue employee = null;

		List<EntityExpr> exprs = FastList.newInstance();
		exprs.add(EntityCondition.makeCondition("employmentType", EntityOperator.NOT_EQUAL, null));
		try {
			employeeList = delegator.findList("EmploymentAndPerson",
					EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		/* String partyId = (String) ctx.get("partyId"); */
		ArrayList reportRows = new ArrayList();
		HashMap rowMap = new HashMap();

		for (GenericValue employeeValue : employeeList) {

			rowMap = new HashMap();
			String employeePartyId = null;
			if (UtilValidate.isNotEmpty(employeeValue)) {
				employeePartyId = employeeValue.getString("partyId");
			}

			String employeeName = SingleEmployeeProfileService.getAnEmployeeName(delegator, employeePartyId);
			String employeeId = SingleEmployeeProfileService.getAnEmployeeId(delegator, employeePartyId);
			String mobileno = SingleEmployeeProfileService.getAnEmployeeMobile(delegator, employeePartyId);
			String designation = SingleEmployeeProfileService.getAnEmployeeDesignation(delegator, employeePartyId);
			String bloodgroup = SingleEmployeeProfileService.getAnBloodGroup(delegator, employeePartyId);
			String department = SingleEmployeeProfileService.getAnDepartment(delegator, employeePartyId);
			String fatherName = SingleEmployeeProfileService.getAnFatherName(delegator, employeePartyId);
			String birthdate = SingleEmployeeProfileService.getAnDateofBirth(delegator, employeePartyId);
			String email = SingleEmployeeProfileService.getAnEmailAdress(delegator, employeePartyId);
			rowMap.put("employeeId", employeeId);
			rowMap.put("employeeName", employeeName);
			rowMap.put("empMobile", mobileno);
			rowMap.put("empDesig", designation);
			rowMap.put("empBlood", bloodgroup);
			rowMap.put("empDepart", department);
			rowMap.put("empFathName", fatherName);
			rowMap.put("empDob", birthdate);
			rowMap.put("empEmail", email);
			reportRows.add(rowMap);
		}
		return reportRows;
	}

	public static String genMonthlyIndiReport(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		String employeeId = "";
		String mMonth = "";
		String mYear = "";

		if (null != request.getParameter("userLoginId")) {
			employeeId = request.getParameter("userLoginId");
		}
		if (null != request.getParameter("month")) {
			mMonth = request.getParameter("month");
		}
		if (null != request.getParameter("year")) {
			mYear = request.getParameter("year");
		}
		String[] requrments = { employeeId, mMonth, mYear };
		JRDataSource jrDataSource = createDataSourceMonIndividual(delegator, context, requrments);
		HashMap jrParameters = getKPIMonIndiParamData(delegator, requrments);
		request.setAttribute("jrDataSource", jrDataSource);
		request.setAttribute("jrParameters", jrParameters);
		return "success";
	}

	public static JRDataSource createDataSourceMonIndividual(Delegator delegator, Map<String, Object> ctx,
			String[] requrments) {
		JRMapCollectionDataSource dataSource;
		Collection reportRows = initMapCollKpiMonIndi(delegator, ctx, requrments);
		dataSource = new JRMapCollectionDataSource(reportRows);
		return dataSource;
	}

	private static Collection initMapCollKpiMonIndi(Delegator delegator, Map<String, Object> ctx, String[] requrments) {

		List<GenericValue> employeeData = null;
		GenericValue employee = null;
		String employeePartyId = null;
		List<EntityCondition> findPartyId = null;
		String employeeId = null;
		String mMonth = null;
		String mYear = null;
		if (requrments != null) {
			employeeId = requrments[0];
			mMonth = requrments[1];
			mYear = requrments[2];
		}
		// GenericValue employeeGAK = null;
		ArrayList reportRows = new ArrayList();

		try {
			findPartyId = FastList.newInstance();
			findPartyId.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, employeeId));
			employeeData = delegator.findList("UserLogin",
					EntityCondition.makeCondition(findPartyId, EntityOperator.AND), null, null, null, false);
			if (employeeData != null) {
				employee = employeeData.get(0);
				employeePartyId = (String) employee.get("partyId");
			}

			HashMap rowMap = new HashMap();
			findPartyId = FastList.newInstance();
			findPartyId.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeePartyId));
			findPartyId.add(EntityCondition.makeCondition("mMonth", EntityOperator.EQUALS, mMonth));
			findPartyId.add(EntityCondition.makeCondition("mYear", EntityOperator.EQUALS, mYear));
			List<GenericValue> empKpiMonthly = delegator.findList("EmployeeGoalsAndKpiMonthly",
					EntityCondition.makeCondition(findPartyId, EntityOperator.AND), null, null, null, false);

			for (GenericValue employeeGAK : empKpiMonthly) {
				// employeeGAK = empKpiMonthly.get(i);
				rowMap = new HashMap();
				String goals = (String) employeeGAK.get("goal");
				String kpi = (String) employeeGAK.get("kpi");
				String target = (String) employeeGAK.get("mTarget");
				String achievement = (String) employeeGAK.get("mAchievement");
				String status = (String) employeeGAK.get("mAchievementStatus");
				rowMap.put("goals", goals);
				rowMap.put("kpi", kpi);
				rowMap.put("target", target);
				rowMap.put("achievement", achievement);
				rowMap.put("status", status);
				reportRows.add(rowMap);
			}

			return reportRows;

		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return reportRows;
	}

	private static HashMap getKPIMonIndiParamData(Delegator delegator, String[] arguments) {

		HashMap parameters = new HashMap();
		String employeeId = null;
		String mMonth = null;
		String mYear = null;
		String employeeName = null;
		String department = null;
		if (arguments != null) {
			employeeId = arguments[0];
			mMonth = arguments[1];
			mYear = arguments[2];
		}
		parameters.put("mMonth", mMonth);
		parameters.put("mYear", mYear);
		parameters.put("empId", employeeId);
		List<EntityCondition> findPartyId = null;
		List<GenericValue> employeeData = null;
		GenericValue employee = null;
		String employeePartyId = null;
		String ovrMonResult = null;
		String ovrMonStatus = null;
		try {
			findPartyId = FastList.newInstance();
			findPartyId.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, employeeId));
			employeeData = delegator.findList("UserLogin",
					EntityCondition.makeCondition(findPartyId, EntityOperator.AND), null, null, null, false);
			if (employeeData != null) {
				employee = employeeData.get(0);
				employeePartyId = (String) employee.get("partyId");
			}

			employeeName = SingleEmployeeProfileService.getAnEmployeeName(delegator, employeePartyId);
			department = SingleEmployeeProfileService.getAnDepartment(delegator, employeePartyId);

			findPartyId = FastList.newInstance();
			findPartyId.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeePartyId));
			findPartyId.add(EntityCondition.makeCondition("mMonth", EntityOperator.EQUALS, mMonth));
			findPartyId.add(EntityCondition.makeCondition("mYear", EntityOperator.EQUALS, mYear));
			employeeData = delegator.findList("EmployeeGAKOvrlStatMonthly",
					EntityCondition.makeCondition(findPartyId, EntityOperator.AND), null, null, null, false);
			if (employeeData != null && !employeeData.isEmpty()) {
				employee = employeeData.get(0);
				ovrMonResult = (String) employee.get("mOverallAchvResult");
				ovrMonStatus = (String) employee.get("mOverallAchvStatus");
			}

		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		parameters.put("empName", employeeName);
		parameters.put("empDept", department);
		parameters.put("ovlResult", ovrMonResult);
		parameters.put("ovlStatus", ovrMonStatus);
		return parameters;

	}

	public static String genYearlyIndiReport(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		String employeeId = "";
		String mYear = "";

		if (null != request.getParameter("userLoginId")) {
			employeeId = request.getParameter("userLoginId");
		}
		if (null != request.getParameter("year")) {
			mYear = request.getParameter("year");
		}
		String[] requrments = { employeeId, mYear };
		JRDataSource jrDataSource = createDataSourceYearIndividual(delegator, context, requrments);
		HashMap jrParameters = getKPIYearIndiParamData(delegator, requrments);
		request.setAttribute("jrDataSource", jrDataSource);
		request.setAttribute("jrParameters", jrParameters);
		return "success";
	}

	public static JRDataSource createDataSourceYearIndividual(Delegator delegator, Map<String, Object> ctx,
			String[] requrments) {
		JRMapCollectionDataSource dataSource;
		Collection reportRows = initMapCollKpiYearIndi(delegator, ctx, requrments);
		dataSource = new JRMapCollectionDataSource(reportRows);
		return dataSource;
	}

	private static Collection initMapCollKpiYearIndi(Delegator delegator, Map<String, Object> ctx,
			String[] requrments) {
		List<GenericValue> employeeData = null;
		GenericValue employee = null;
		String employeePartyId = null;
		List<EntityCondition> findPartyId = null;
		String employeeId = null;
		String mYear = null;
		if (requrments != null) {
			employeeId = requrments[0];
			mYear = requrments[1];
		}
		ArrayList reportRows = new ArrayList();
		try {
			findPartyId = FastList.newInstance();
			findPartyId.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, employeeId));
			employeeData = delegator.findList("UserLogin",
					EntityCondition.makeCondition(findPartyId, EntityOperator.AND), null, null, null, false);
			if (employeeData != null) {
				employee = employeeData.get(0);
				employeePartyId = (String) employee.get("partyId");
			}

			HashMap rowMap = new HashMap();
			findPartyId = FastList.newInstance();
			findPartyId.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeePartyId));
			findPartyId.add(EntityCondition.makeCondition("year", EntityOperator.EQUALS, mYear));
			List<GenericValue> empKpiMonthly = delegator.findList("EmployeeGoalsAndKpi",
					EntityCondition.makeCondition(findPartyId, EntityOperator.AND), null, null, null, false);

			for (GenericValue employeeGAK : empKpiMonthly) {
				// employeeGAK = empKpiMonthly.get(i);
				rowMap = new HashMap();
				String goals = (String) employeeGAK.get("goal");
				String kpi = (String) employeeGAK.get("kpi");
				String target = (String) employeeGAK.get("target");
				String achievement = (String) employeeGAK.get("achievement");
				String status = (String) employeeGAK.get("achievementStatus");
				rowMap.put("goals", goals);
				rowMap.put("kpi", kpi);
				rowMap.put("target", target);
				rowMap.put("achievement", achievement);
				rowMap.put("status", status);
				reportRows.add(rowMap);
			}

			return reportRows;

		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return reportRows;
	}

	private static HashMap getKPIYearIndiParamData(Delegator delegator, String[] arguments) {

		HashMap parameters = new HashMap();
		String employeeId = null;
		String mYear = null;
		String employeeName = null;
		String department = null;
		if (arguments != null) {
			employeeId = arguments[0];
			mYear = arguments[1];
		}
		parameters.put("year", mYear);
		parameters.put("empId", employeeId);
		List<EntityCondition> findPartyId = null;
		List<GenericValue> employeeData = null;
		GenericValue employee = null;
		String employeePartyId = null;
		String ovrResult = null;
		String ovrStatus = null;
		try {
			findPartyId = FastList.newInstance();
			findPartyId.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, employeeId));
			employeeData = delegator.findList("UserLogin",
					EntityCondition.makeCondition(findPartyId, EntityOperator.AND), null, null, null, false);
			if (employeeData != null) {
				employee = employeeData.get(0);
				employeePartyId = (String) employee.get("partyId");
			}

			employeeName = SingleEmployeeProfileService.getAnEmployeeName(delegator, employeePartyId);
			department = SingleEmployeeProfileService.getAnDepartment(delegator, employeePartyId);

			findPartyId = FastList.newInstance();
			findPartyId.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeePartyId));
			findPartyId.add(EntityCondition.makeCondition("year", EntityOperator.EQUALS, mYear));
			employeeData = delegator.findList("EmployeeGoalsAndKpiOverallStatus",
					EntityCondition.makeCondition(findPartyId, EntityOperator.AND), null, null, null, false);
			if (employeeData != null && !employeeData.isEmpty()) {
				employee = employeeData.get(0);
				ovrResult = (String) employee.get("overallAchvResult");
				ovrStatus = (String) employee.get("overallAchvStatus");
			}

		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		parameters.put("empName", employeeName);
		parameters.put("empDept", department);
		parameters.put("ovlResult", ovrResult);
		parameters.put("ovlStatus", ovrStatus);
		return parameters;

	}

	public static String generateDeptWiseMonthlyReport(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		String department = "";
		String year = "";
		String month = "";
		if (null != request.getParameter("department")) {
			department = request.getParameter("department");
		}
		if (null != request.getParameter("year")) {
			year = request.getParameter("year");
		}
		if (null != request.getParameter("month")) {
			month = request.getParameter("month");
		}
		String[] requrments = { department, year, month };
		JRDataSource jrDataSource = createDataSourceDeptMonthly(delegator, context, requrments);
		HashMap jrParameters = getKPIDeptMonthlyParameter(delegator, requrments);
		request.setAttribute("jrDataSource", jrDataSource);
		request.setAttribute("jrParameters", jrParameters);
		return "success";
	}

	public static JRDataSource createDataSourceDeptMonthly(Delegator delegator, Map<String, Object> ctx,
			String[] requrments) {
		JRMapCollectionDataSource dataSource;
		Collection reportRows = initMapCollKpiDeptMonthly(delegator, ctx, requrments);
		dataSource = new JRMapCollectionDataSource(reportRows);
		return dataSource;
	}

	private static Collection initMapCollKpiDeptMonthly(Delegator delegator, Map<String, Object> ctx,
			String[] requrments) {
		List<GenericValue> deptDataMonthWise = null;
		String department = null;
		String year = null;
		String month = null;
		List<EntityCondition> departmentSelection = null;
		if (requrments != null) {
			department = requrments[0];
			year = requrments[1];
			month = requrments[2];
		}
		ArrayList reportRows = new ArrayList();
		try {
			departmentSelection = FastList.newInstance();
			departmentSelection.add(EntityCondition.makeCondition("department", EntityOperator.EQUALS, department));
			departmentSelection.add(EntityCondition.makeCondition("mYear", EntityOperator.EQUALS, year));
			departmentSelection.add(EntityCondition.makeCondition("mMonth", EntityOperator.EQUALS, month));

			HashMap rowMap = null;

			deptDataMonthWise = delegator.findList("EmployeeGAKOvrlStatMonthly",
					EntityCondition.makeCondition(departmentSelection, EntityOperator.AND), null, null, null, false);

			for (GenericValue departmentGAK : deptDataMonthWise) {
				rowMap = new HashMap();
				String partyId = (String) departmentGAK.get("partyId");
				String employeeName = SingleEmployeeProfileService.getAnEmployeeName(delegator, partyId);
				String employeeId = (String) departmentGAK.get("employeeId");
				String empResult = (String) departmentGAK.get("mOverallAchvResult");
				String empStatus = (String) departmentGAK.get("mOverallAchvStatus");

				rowMap.put("empId", employeeId);
				rowMap.put("empName", employeeName);
				rowMap.put("empStatus", empStatus);
				rowMap.put("empResult", empResult);
				reportRows.add(rowMap);
			}

			return reportRows;

		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return reportRows;
	}

	private static HashMap getKPIDeptMonthlyParameter(Delegator delegator, String[] arguments) {

		HashMap parameters = new HashMap();
		String department = null;
		String year = null;
		String month = null;
		List<EntityCondition> departmentSelection = null;
		List<GenericValue> deptWiseData = null;
		if (arguments != null) {
			department = arguments[0];
			year = arguments[1];
			month = arguments[2];
		}
		parameters.put("year", year);
		parameters.put("month", month);
		parameters.put("dept", department);

		String ovlResult = null;
		String ovlStatus = null;
		try {
			departmentSelection = FastList.newInstance();
			departmentSelection.add(EntityCondition.makeCondition("department", EntityOperator.EQUALS, department));
			departmentSelection.add(EntityCondition.makeCondition("year", EntityOperator.EQUALS, year));
			departmentSelection.add(EntityCondition.makeCondition("month", EntityOperator.EQUALS, month));

			deptWiseData = delegator.findList("DepartmentWiseKpiMontlyStatus",
					EntityCondition.makeCondition(departmentSelection, EntityOperator.AND), null, null, null, false);
			for (GenericValue departmentGAK : deptWiseData) {
				ovlResult = (String) departmentGAK.get("ovlMonDeptResult");
				ovlStatus = (String) departmentGAK.get("ovlMonDeptStatus");
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		parameters.put("ovlResult", ovlResult);
		parameters.put("ovlStatus", ovlStatus);
		return parameters;

	}

	public static String generateDeptWiseAnnualReport(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		String department = "";
		String year = "";
		if (null != request.getParameter("department")) {
			department = request.getParameter("department");
		}
		if (null != request.getParameter("year")) {
			year = request.getParameter("year");
		}
		String[] requrments = { department, year };
		JRDataSource jrDataSource = createDataSourceDeptAnnual(delegator, context, requrments);
		HashMap jrParameters = getKPIDeptAnnualParameter(delegator, requrments);
		request.setAttribute("jrDataSource", jrDataSource);
		request.setAttribute("jrParameters", jrParameters);
		return "success";
	}

	public static JRDataSource createDataSourceDeptAnnual(Delegator delegator, Map<String, Object> ctx,
			String[] requrments) {
		JRMapCollectionDataSource dataSource;
		Collection reportRows = initMapCollKpiDeptAnnual(delegator, ctx, requrments);
		dataSource = new JRMapCollectionDataSource(reportRows);
		return dataSource;
	}

	private static Collection initMapCollKpiDeptAnnual(Delegator delegator, Map<String, Object> ctx,
			String[] requrments) {
		List<GenericValue> deptDataAnnual = null;
		String department = null;
		String year = null;
		List<EntityCondition> departmentSelection = null;
		if (requrments != null) {
			department = requrments[0];
			year = requrments[1];
		}
		ArrayList reportRows = new ArrayList();
		try {
			departmentSelection = FastList.newInstance();
			departmentSelection.add(EntityCondition.makeCondition("department", EntityOperator.EQUALS, department));
			departmentSelection.add(EntityCondition.makeCondition("year", EntityOperator.EQUALS, year));
			HashMap rowMap = null;
			deptDataAnnual = delegator.findList("EmployeeGoalsAndKpiOverallStatus",
					EntityCondition.makeCondition(departmentSelection, EntityOperator.AND), null, null, null, false);

			for (GenericValue departmentGAK : deptDataAnnual) {
				rowMap = new HashMap();
				String partyId = (String) departmentGAK.get("partyId");
				String employeeName = SingleEmployeeProfileService.getAnEmployeeName(delegator, partyId);
				String employeeId = (String) departmentGAK.get("employeeId");
				String empResult = (String) departmentGAK.get("overallAchvResult");
				String empStatus = (String) departmentGAK.get("overallAchvStatus");

				rowMap.put("empId", employeeId);
				rowMap.put("empName", employeeName);
				rowMap.put("empStatus", empStatus);
				rowMap.put("empResult", empResult);
				reportRows.add(rowMap);
			}

			return reportRows;

		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return reportRows;
	}

	private static HashMap getKPIDeptAnnualParameter(Delegator delegator, String[] arguments) {

		HashMap parameters = new HashMap();
		String department = null;
		String year = null;
		List<EntityCondition> departmentSelection = null;
		List<GenericValue> deptWiseData = null;
		if (arguments != null) {
			department = arguments[0];
			year = arguments[1];
		}
		parameters.put("year", year);
		parameters.put("dept", department);

		String ovlResult = null;
		String ovlStatus = null;
		try {
			departmentSelection = FastList.newInstance();
			departmentSelection.add(EntityCondition.makeCondition("department", EntityOperator.EQUALS, department));
			departmentSelection.add(EntityCondition.makeCondition("year", EntityOperator.EQUALS, year));

			deptWiseData = delegator.findList("DepartmentWiseKpiStatus",
					EntityCondition.makeCondition(departmentSelection, EntityOperator.AND), null, null, null, false);
			for (GenericValue departmentGAK : deptWiseData) {
				ovlResult = (String) departmentGAK.get("overallDeptResult");
				ovlStatus = (String) departmentGAK.get("overallDeptStatus");
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		parameters.put("ovlResult", ovlResult);
		parameters.put("ovlStatus", ovlStatus);
		return parameters;

	}
	
	public static String genCompWiseAnnualReport(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		String year = "";
		if (null != request.getParameter("year")) {
			year = request.getParameter("year");
		}
		
		JRDataSource jrDataSource = createDataSourceCompAnnual(delegator, context, request);
		HashMap jrParameters = getKPICompAnnualParameter(delegator, request);
		request.setAttribute("jrDataSource", jrDataSource);
		request.setAttribute("jrParameters", jrParameters);
		return "success";
	}
	public static JRDataSource createDataSourceCompAnnual(Delegator delegator, Map ctx, HttpServletRequest request) {
		JRMapCollectionDataSource dataSource;
		Collection reportRows = initMapCollKpiCompAnnual(delegator, ctx, request);
		dataSource = new JRMapCollectionDataSource(reportRows);
		return dataSource;
	}
	
	private static Collection initMapCollKpiCompAnnual(Delegator delegator, Map<String, Object> ctx, HttpServletRequest request) {
		List<GenericValue> compDataAnnual = null;
		String year = "";
		if (null != request.getParameter("year")) {
			year = request.getParameter("year");
		}
		List<EntityCondition> companySelection = null;
		ArrayList reportRows = new ArrayList();
		try {
			companySelection = FastList.newInstance();
			companySelection.add(EntityCondition.makeCondition("year", EntityOperator.EQUALS, year));
			HashMap rowMap = null;
			compDataAnnual = delegator.findList("CompanyWiseKpiMontlyStat",
					EntityCondition.makeCondition(companySelection, EntityOperator.AND), null, null, null, false);
			for (GenericValue companyGAK : compDataAnnual) {
				rowMap = new HashMap();
				String month = (String) companyGAK.get("month");
				String ovlMonDeptResult = (String) companyGAK.get("ovlMonDeptResult");
				String ovlMonDeptStatus = (String) companyGAK.get("ovlMonDeptStatus");
				rowMap.put("compMonth", month);
				rowMap.put("compResult", ovlMonDeptResult);
				rowMap.put("compStatus", ovlMonDeptStatus);
				reportRows.add(rowMap);
			}
			return reportRows;
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return reportRows;
	}
	private static HashMap getKPICompAnnualParameter(Delegator delegator, HttpServletRequest request) {

		HashMap parameters = new HashMap();
		String department = null;
		String year = null;
		List<EntityCondition> departmentSelection = null;
		List<GenericValue> deptWiseData = null;
		if (null != request.getParameter("year")) {
			year = request.getParameter("year");
		}
		parameters.put("year", year);
		String ovlResult = null;
		String ovlStatus = null;
		try {
			departmentSelection = FastList.newInstance();
			departmentSelection.add(EntityCondition.makeCondition("year", EntityOperator.EQUALS, year));

			deptWiseData = delegator.findList("CompanyWiseKpiAnnualStat",
					EntityCondition.makeCondition(departmentSelection, EntityOperator.AND), null, null, null, false);
			for (GenericValue departmentGAK : deptWiseData) {
				ovlResult = (String) departmentGAK.get("ovlAnnualResult");
				ovlStatus = (String) departmentGAK.get("ovlAnnualStatus");
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		parameters.put("ovlResult", ovlResult);
		parameters.put("ovlStatus", ovlStatus);
		return parameters;

	}
	
	public static String genCompWiseMonthlyReport(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getParameterMap(request);
		JRDataSource jrDataSource = createDataSourceCompMonthly(delegator, context, request);
		HashMap jrParameters = getKPICompMonthlyParameter(delegator, request);
		request.setAttribute("jrDataSource", jrDataSource);
		request.setAttribute("jrParameters", jrParameters);
		return "success";
	}
	
	public static JRDataSource createDataSourceCompMonthly(Delegator delegator, Map ctx, HttpServletRequest request) {
		JRMapCollectionDataSource dataSource;
		Collection reportRows = initMapCollKpiCompMonthly(delegator, ctx, request);
		dataSource = new JRMapCollectionDataSource(reportRows);
		return dataSource;
	}
	
	private static Collection initMapCollKpiCompMonthly(Delegator delegator, Map<String, Object> ctx, HttpServletRequest request) {
		List<GenericValue> compDataMonthly = null;
		String year = "";
		String month = "";
		if (null != request.getParameter("year")) {
			year = request.getParameter("year");
		}
		if (null != request.getParameter("month")) {
			month = request.getParameter("month");
		}
		List<EntityCondition> companySelection = null;
		ArrayList reportRows = new ArrayList();
		try {
			companySelection = FastList.newInstance();
			companySelection.add(EntityCondition.makeCondition("year", EntityOperator.EQUALS, year));
			companySelection.add(EntityCondition.makeCondition("month", EntityOperator.EQUALS, month));
			HashMap rowMap = null;
			compDataMonthly = delegator.findList("DepartmentWiseKpiMontlyStatus",
					EntityCondition.makeCondition(companySelection, EntityOperator.AND), null, null, null, false);
			for (GenericValue companyGAK : compDataMonthly) {
				rowMap = new HashMap();
				String ovlMonDeptResult = (String) companyGAK.get("ovlMonDeptResult");
				String ovlMonDeptStatus = (String) companyGAK.get("ovlMonDeptStatus");
				String department = (String) companyGAK.get("department");
				rowMap.put("compDept", department);
				rowMap.put("compResult", ovlMonDeptResult);
				rowMap.put("compStatus", ovlMonDeptStatus);
				reportRows.add(rowMap);
			}
			return reportRows;
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return reportRows;
	}
	
	private static HashMap getKPICompMonthlyParameter(Delegator delegator, HttpServletRequest request) {
		HashMap parameters = new HashMap();
		String department = null;
		List<EntityCondition> departmentSelection = null;
		List<GenericValue> deptWiseData = null;
		String year = "";
		String month = "";
		if (null != request.getParameter("year")) {
			year = request.getParameter("year");
		}
		if (null != request.getParameter("month")) {
			month = request.getParameter("month");
		}
		parameters.put("year", year);
		parameters.put("month", month);
		String ovlResult = null;
		String ovlStatus = null;
		try {
			departmentSelection = FastList.newInstance();
			departmentSelection.add(EntityCondition.makeCondition("year", EntityOperator.EQUALS, year));

			deptWiseData = delegator.findList("CompanyWiseKpiMontlyStat",
					EntityCondition.makeCondition(departmentSelection, EntityOperator.AND), null, null, null, false);
			for (GenericValue departmentGAK : deptWiseData) {
				ovlResult = (String) departmentGAK.get("ovlMonDeptResult");
				ovlStatus = (String) departmentGAK.get("ovlMonDeptStatus");
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		parameters.put("ovlResult", ovlResult);
		parameters.put("ovlStatus", ovlStatus);
		return parameters;

	}
	
	
	

	
}