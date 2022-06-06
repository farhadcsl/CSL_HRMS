package org.ofbiz.humanres.kpi;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.xmlrpc.metadata.Util;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
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
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanres.KpiEvents;
import org.ofbiz.humanres.dataimport.HrDataImportServices;
import org.ofbiz.humanres.kpi.KpiEmailServices;
import org.ofbiz.party.communication.CommunicationEventServices;
import org.ofbiz.service.DispatchContext;
//import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericAbstractDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transaction;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

public class MonthlyKpiEvents {
	public static final String module = CommunicationEventServices.class.getName();
	public static final String kpiCounter = "2";

	public static Map<String, Object> updateMonthlyKpi(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		String partyId = (String) context.get("partyId");
		String mMonth = (String) context.get("mMonth");
		String mYear = (String) context.get("mYear");
		String monthYearID = mMonth + mYear + partyId;
		String mAchievementStatus = "";
		Float mAchievementResult = 0f;
		Float mTotalAchvResult = 0f;
		Float mOverallAchvResult = 0f;
		int kpiCount = 0;
		int goalsKpiCount = 0;
		String mOverallAchvStatus = "";
		Map<String, Object> resultMap = null;
		String goalsKpiId, mGoalsKpiId;
		String unit;
		String unitRemarks;
		String mTarget;
		String mAchievement;
		GenericValue empKpiValues = null;
		String employeeId, managerId, department, mmGoalsKpiId = null;
		String mgrApprStatus = "KPI_REVIEW_REQ";
		try {
			List<EntityCondition> findEmployeeKpi = FastList.newInstance();

			findEmployeeKpi.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			List<GenericValue> empGoalsKpi = delegator.findList("EmployeeGoalsAndKpi",
					EntityCondition.makeCondition(findEmployeeKpi, EntityOperator.AND), null, null, null, false);

			if (empGoalsKpi != null) {
				for (int i = 0; i < empGoalsKpi.size(); i++) {
					empKpiValues = empGoalsKpi.get(i);
					managerId = (String) empKpiValues.get("managerId");
					goalsKpiCount++;
				}
			}

			findEmployeeKpi.add(EntityCondition.makeCondition("mMonth", EntityOperator.EQUALS, mMonth));
			findEmployeeKpi.add(EntityCondition.makeCondition("mYear", EntityOperator.EQUALS, mYear));
			List<GenericValue> empKpiMonthly = delegator.findList("EmployeeGoalsAndKpiMonthly",
					EntityCondition.makeCondition(findEmployeeKpi, EntityOperator.AND), null, null, null, false);

			if (empKpiMonthly != null) {
				for (int i = 0; i < empKpiMonthly.size(); i++) {
					empKpiValues = empKpiMonthly.get(i);
					mGoalsKpiId = (String) empKpiValues.get("mGoalsKpiId");
					goalsKpiId = (String) empKpiValues.get("goalsKpiId");
					unit = (String) empKpiValues.get("unit");
					unitRemarks = (String) empKpiValues.get("unitRemarks");
					mTarget = (String) empKpiValues.get("mTarget");
					mAchievement = (String) empKpiValues.get("mAchievement");

					mAchievementResult = KpiEvents.calculateIndividualKpi(unitRemarks, mTarget, mAchievement);
					mAchievementResult = (float) Math.round(mAchievementResult * 100) / 100;
					mAchievementStatus = KpiEvents.findKpiStatus(mAchievementResult);
					mTotalAchvResult = mTotalAchvResult + mAchievementResult;
					Map<String, Object> MonthlyKpiIn = FastMap.newInstance();
					MonthlyKpiIn.put("mGoalsKpiId", mGoalsKpiId);
					MonthlyKpiIn.put("goalsKpiId", goalsKpiId);
					MonthlyKpiIn.put("mAchievement", mAchievement);
					MonthlyKpiIn.put("mAchievementResult", mAchievementResult.toString());
					MonthlyKpiIn.put("mAchievementStatus", mAchievementStatus);
					GenericValue MonthlyKpiUpdate = delegator.makeValue("EmployeeGoalsAndKpiMonthly", MonthlyKpiIn);
					MonthlyKpiUpdate.store();
					kpiCount++;
				}
			}

			employeeId = (String) empKpiValues.get("employeeId");
			managerId = (String) empKpiValues.get("managerId");
			department = (String) empKpiValues.get("department");
			mmGoalsKpiId = (String) empKpiValues.get("mGoalsKpiId");

			Map<String, Object> MonthlyOverallResultIn = FastMap.newInstance();
			mOverallAchvResult = mTotalAchvResult / kpiCount;
			mOverallAchvResult = (float) Math.round(mOverallAchvResult * 100) / 100;
			mOverallAchvStatus = KpiEvents.findKpiStatus(mOverallAchvResult);
			MonthlyOverallResultIn.put("monthYearID", monthYearID);
			MonthlyOverallResultIn.put("mGoalsKpiId", mmGoalsKpiId);
			MonthlyOverallResultIn.put("partyId", partyId);
			MonthlyOverallResultIn.put("employeeId", employeeId);
			MonthlyOverallResultIn.put("managerId", managerId);
			MonthlyOverallResultIn.put("department", department);
			MonthlyOverallResultIn.put("mOverallAchvResult", mOverallAchvResult.toString());
			MonthlyOverallResultIn.put("mOverallAchvStatus", mOverallAchvStatus);
			MonthlyOverallResultIn.put("mgrApprStatus", mgrApprStatus);
			MonthlyOverallResultIn.put("mgrRemarks", "mgrRemarks");
			MonthlyOverallResultIn.put("kpiType", "Personal");
			MonthlyOverallResultIn.put("mMonth", mMonth);
			MonthlyOverallResultIn.put("mYear", mYear);

			List<GenericValue> empOvAllKpiMon = delegator.findList("EmployeeGAKOvrlStatMonthly",
					EntityCondition.makeCondition(findEmployeeKpi, EntityOperator.AND), null, null, null, false);
			if (empOvAllKpiMon.isEmpty() || empOvAllKpiMon.equals("null")) {
				MonthlyOverallResultIn.put("kpiCounter", kpiCounter);
			}

			GenericValue MonthlyOverallResultADD = delegator.makeValue("EmployeeGAKOvrlStatMonthly",
					MonthlyOverallResultIn);
			delegator.createOrStore(MonthlyOverallResultADD);

			// String managerPartyId = new
			// HrDataImportServices().finupdateKPIYearly(dctx,
			// context);dPartyIdByEmployeeId(delegator,
			// managerId);

			// KpiEvents.sendEmailNotification(dctx, context, managerId,
			// mgrApprStatus);

			resultMap = ServiceUtil.returnSuccess("Achievement Status Updated Successfully!");

			updateKPIYearly(dctx, context);
			KpiEvents.updateKpi(dctx, context);

			return resultMap;

		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ServiceUtil.returnError("Achievement Status failed to update!");
			return resultMap;
		}
	}

	public static String getOverallMonthlyAchievement(Delegator delegator, String partyId) {
		Float mAchvResult = 0f;
		String smAchvResult = "";
		Float mTotalAchvResult = 0f;
		Float mOverallAchvResult = 0f;
		int kpiCount = 0;
		try {
			List<EntityCondition> findEmployeeKpi = FastList.newInstance();

			findEmployeeKpi.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			findEmployeeKpi.add(EntityCondition.makeCondition("mYear", EntityOperator.EQUALS, "2019"));
			List<GenericValue> empGoalsKpi = delegator.findList("EmployeeGAKOvrlStatMonthly",
					EntityCondition.makeCondition(findEmployeeKpi, EntityOperator.AND), null, null, null, false);
			GenericValue empKpiValues = null;

			if (empGoalsKpi != null) {
				for (int i = 0; i < empGoalsKpi.size(); i++) {
					empKpiValues = empGoalsKpi.get(i);
					smAchvResult = (String) empKpiValues.get("mOverallAchvResult");
					mAchvResult = Float.valueOf(smAchvResult);
					mTotalAchvResult += mAchvResult;
					kpiCount++;
				}
			}
			mOverallAchvResult = mTotalAchvResult / kpiCount;
			mOverallAchvResult = (float) Math.round(mOverallAchvResult * 100) / 100;
			return mOverallAchvResult.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
			
		}
	}

	public static String[] getoverallCompanyKPI(Delegator delegator) {
		Float companyResult = 0f;
		String scompanyResult = "";
		Float totalCompanyResult = 0f;
		Float overallCompanyResult = 0f;
		String sOverallCompanyResult = "";
		String overallCompanyStatus = "";
		int kpiCount = 0;
		String[] result = new String[2];
		try {
			List<EntityCondition> findCompanyKpi = FastList.newInstance();

			List<GenericValue> companyKpi = delegator.findList("DepartmentWiseKpiStatus", null, null, null, null,
					false);
			GenericValue companyKpiValues = null;

			if (companyKpi != null) {
				for (int i = 0; i < companyKpi.size(); i++) {
					companyKpiValues = companyKpi.get(i);
					scompanyResult = (String) companyKpiValues.get("overallDeptResult");
					companyResult = Float.valueOf(scompanyResult);
					totalCompanyResult += companyResult;
					kpiCount++;
				}
			}
			overallCompanyResult = totalCompanyResult / kpiCount;
			// overallCompanyResult = (float)
			// Math.round(overallCompanyResult*100)/100;
			overallCompanyResult = (float) Math.ceil(overallCompanyResult);
			sOverallCompanyResult = String.valueOf(overallCompanyResult);
			overallCompanyStatus = KpiEvents.findKpiStatus(overallCompanyResult);
			result[0] = sOverallCompanyResult;
			result[1] = overallCompanyStatus;
			return result;
		} catch (Exception e) {
			return result;
		}
	}

	// Modified by Abdullah Al Parve for update yearly achievement
	public static Map updateKPIYearly(DispatchContext dctx, Map context) {
		Delegator delegator = dctx.getDelegator();
		String partyId = (String) context.get("partyId");
		String mYear = (String) context.get("mYear");
		Map resultMap = null;
		DecimalFormat df = new DecimalFormat("#");
		df.setMaximumFractionDigits(0);

		GenericValue empKpiValues = null;
		GenericValue empKpiMonValues = null;
		try {
			List<EntityCondition> findEmployeeKpi = FastList.newInstance();

			findEmployeeKpi.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			findEmployeeKpi.add(EntityCondition.makeCondition("year", EntityOperator.EQUALS, mYear));
			List<GenericValue> empKpi = delegator.findList("EmployeeGoalsAndKpi",
					EntityCondition.makeCondition(findEmployeeKpi, EntityOperator.AND), null, null, null, false);

			String achievementStatus = "";
			Float achievementResult = 0f;
			int kpiCount = 0;
			String goalsKpiId = null;
			String achievement = null;
			String unitRemarks = null;
			String target = null;
			String param = null;
			List<GenericValue> empKpiMontly = null;

			if (empKpi != null) {
				for (int i = 0; i < empKpi.size(); i++) {
					empKpiValues = empKpi.get(i);
					goalsKpiId = (String) empKpiValues.get("goalsKpiId");
					achievement = (String) empKpiValues.get("achievement");
					param = (String) empKpiValues.get("param");
					unitRemarks = (String) empKpiValues.get("unitRemarks");
					target = (String) empKpiValues.get("target");

					findEmployeeKpi = FastList.newInstance();
					findEmployeeKpi.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
					findEmployeeKpi.add(EntityCondition.makeCondition("goalsKpiId", EntityOperator.EQUALS, goalsKpiId));
					findEmployeeKpi.add(EntityCondition.makeCondition("mYear", EntityOperator.EQUALS, mYear));

					empKpiMontly = delegator.findList("EmployeeGoalsAndKpiMonthly",
							EntityCondition.makeCondition(findEmployeeKpi, EntityOperator.AND), null, null, null,
							false);

					// Modified By Parves to calculate 12Same KPI value
					// 30/09/2018
					int mKpiCunnter = 0;

					double yAchievementDouble = 0;
					float yAchievementResult = 0f;
					String yAchievement = null;

					if (empKpiMontly != null) {

						for (int j = 0; j < empKpiMontly.size(); j++) {
							empKpiMonValues = empKpiMontly.get(j);
							yAchievement = (String) empKpiMonValues.get("mAchievement");
							double parsedValue = Double.parseDouble(yAchievement);
							// Modified By Parves to calculate 12Same KPI value
							// 30/09/2018
							yAchievementDouble += parsedValue;

							if (param.equals("12Same") && parsedValue > 0) {
								mKpiCunnter++;
							}

						}

					}

					Map<String, Object> EmployeeGolasKPI = FastMap.newInstance();
					EmployeeGolasKPI.put("goalsKpiId", goalsKpiId);

					if (param.equals("12Same")) {
						if (mKpiCunnter == 0) {
							yAchievementDouble = 0;
							EmployeeGolasKPI.put("achievement", df.format(yAchievementDouble));
						} else {
							yAchievementDouble = yAchievementDouble / mKpiCunnter;
							EmployeeGolasKPI.put("achievement", df.format(yAchievementDouble));
						}
					} else {
						EmployeeGolasKPI.put("achievement", df.format(yAchievementDouble));
					}

					yAchievementResult = KpiEvents.calculateIndividualKpi(unitRemarks, target,
							Double.toString(yAchievementDouble));
					EmployeeGolasKPI.put("achievementStatus",
							KpiEvents.findKpiStatus(Float.valueOf(yAchievementResult)));

					GenericValue EmployeeKpiUpdate = delegator.makeValue("EmployeeGoalsAndKpi", EmployeeGolasKPI);
					EmployeeKpiUpdate.store();
					findEmployeeKpi = null;
					mKpiCunnter = 0;
					kpiCount++;

				}
			}

			resultMap = ServiceUtil.returnSuccess("Yearly Achievement Updated Successfully!");
			return resultMap;

		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ServiceUtil.returnError("Yearly Achievement failed to update!");
			return resultMap;
		}
	}

	// Created by Abdullah Al Parve for Department wise Data Monthly
	public static Map<String, Object> genreateDeptWiseDataMonthly(DispatchContext dctx, Map<String, Object> context) {

		Delegator delegator = dctx.getDelegator();
		Map resultMap = null;
		String department = (String) context.get("department");
		String year = (String) context.get("year");
		List<EntityCondition> findDeptWiseKPI = null;
		GenericValue empKpiMonthlyValues = null;
		List<GenericValue> empKpiMon = null;
		GenericValue genEmpKpiMon = null;
		DecimalFormat df = new DecimalFormat("#");
		df.setMaximumFractionDigits(0);
		try {
			findDeptWiseKPI = FastList.newInstance();
			findDeptWiseKPI.add(EntityCondition.makeCondition("department", EntityOperator.EQUALS, department));
			findDeptWiseKPI.add(EntityCondition.makeCondition("mYear", EntityOperator.EQUALS, year));
			List<GenericValue> empKpiMonthly = delegator.findList("EmployeeGAKOvrlStatMonthly",
					EntityCondition.makeCondition(findDeptWiseKPI, EntityOperator.AND), null, null, null, false);
			if (empKpiMonthly != null && !empKpiMonthly.isEmpty()) {
				for (int i = 0; i < empKpiMonthly.size(); i++) {
					empKpiMonthlyValues = empKpiMonthly.get(i);
					String month = empKpiMonthlyValues.getString("mMonth");
					double mAchievementDouble = 0;
					int monKpiCounter = 0;
					if (findDeptWiseKPI != null) {
						findDeptWiseKPI = FastList.newInstance();
						findDeptWiseKPI
								.add(EntityCondition.makeCondition("department", EntityOperator.EQUALS, department));
						findDeptWiseKPI.add(EntityCondition.makeCondition("mYear", EntityOperator.EQUALS, year));
						findDeptWiseKPI.add(EntityCondition.makeCondition("mMonth", EntityOperator.EQUALS, month));
					}

					empKpiMon = delegator.findList("EmployeeGAKOvrlStatMonthly",
							EntityCondition.makeCondition(findDeptWiseKPI, EntityOperator.AND), null, null, null,
							false);
					for (int j = 0; j < empKpiMon.size(); j++) {
						genEmpKpiMon = empKpiMon.get(j);
						if (genEmpKpiMon.getString("mOverallAchvResult") != null
								&& genEmpKpiMon.getString("mOverallAchvResult") != "") {
							mAchievementDouble += Double.parseDouble(genEmpKpiMon.getString("mOverallAchvResult"));
							monKpiCounter++;
						}
					}
					String monthYearDepartment = year + month+department;
					String ovlMonDeptResult = df.format(mAchievementDouble / monKpiCounter);
					String ovlMonDeptStatus = KpiEvents.findKpiStatus(Float.valueOf(ovlMonDeptResult));
					Map<String, Object> deptWiseKpiMonStaMake = FastMap.newInstance();
					deptWiseKpiMonStaMake.put("monthYearDepartment", monthYearDepartment);
					deptWiseKpiMonStaMake.put("department", department);
					deptWiseKpiMonStaMake.put("ovlMonDeptResult", ovlMonDeptResult);
					deptWiseKpiMonStaMake.put("ovlMonDeptStatus", ovlMonDeptStatus);
					deptWiseKpiMonStaMake.put("month", month);
					deptWiseKpiMonStaMake.put("year", year);
					GenericValue deptWiseKpiMon = delegator.makeValue("DepartmentWiseKpiMontlyStatus",
							deptWiseKpiMonStaMake);
					delegator.createOrStore(deptWiseKpiMon);
				}

			}
			String[] requirments = { department, year };
			resultMap = ServiceUtil.returnSuccess("Depatment wise report genration Successfully!");
			KpiEvents.deptWisekpI(dctx, context);
		
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ServiceUtil.returnError("Depatment wise report genration Failed!!!");
			return resultMap;
		}
	}

	public static Map<String, Object> genCompWiseReport(DispatchContext dctx, Map context) {
		Delegator delegator = dctx.getDelegator();
		String year = (String) context.get("year");
		String company = (String) context.get("company");
		Map resultMap = null;
		List<EntityCondition> kpiSelection = null;
		List<GenericValue> compWiseKpiAll = null;
		List<GenericValue> compWiseKpiMonthly = null;
		String month = null;
		DecimalFormat df = new DecimalFormat("#");
		df.setMaximumFractionDigits(0);
		
		try{
			kpiSelection = FastList.newInstance();
			kpiSelection.add(EntityCondition.makeCondition("year", EntityOperator.EQUALS, year));
			compWiseKpiAll = delegator.findList("DepartmentWiseKpiMontlyStatus",
					EntityCondition.makeCondition(kpiSelection, EntityOperator.AND), null, null, null, false);
			for (GenericValue departmentGAK : compWiseKpiAll) {
				
				month = (String) departmentGAK.get("month");
				double ovlMonDeptResult = 0;
				int monKpiCounter = 0;
				
				if(kpiSelection != null){
					kpiSelection = FastList.newInstance();
					kpiSelection.add(EntityCondition.makeCondition("year", EntityOperator.EQUALS, year));
					kpiSelection.add(EntityCondition.makeCondition("month", EntityOperator.EQUALS, month));
					compWiseKpiMonthly = delegator.findList("DepartmentWiseKpiMontlyStatus",
							EntityCondition.makeCondition(kpiSelection, EntityOperator.AND), null, null, null, false);
					for (GenericValue companyGAKMonthly : compWiseKpiMonthly) {
						if (companyGAKMonthly.get("ovlMonDeptResult") != null
								&& companyGAKMonthly.get("ovlMonDeptResult") != "") {
							String indiDepResult = (String)companyGAKMonthly.get("ovlMonDeptResult");
							ovlMonDeptResult += Double.parseDouble(indiDepResult);
							monKpiCounter++;
						}
					}

					String ovlMonComResult = df.format(ovlMonDeptResult / monKpiCounter);
					String ovlMonDeptStatus = KpiEvents.findKpiStatus(Float.valueOf(ovlMonComResult));
					Map<String, Object> comWiseMonKpiStatus = FastMap.newInstance();
					String monthYearId = year+month;
					comWiseMonKpiStatus.put("monthYearId", monthYearId);
					comWiseMonKpiStatus.put("companyName", "Computer Services Ltd.");
					comWiseMonKpiStatus.put("ovlMonDeptResult", ovlMonComResult);
					comWiseMonKpiStatus.put("ovlMonDeptStatus", ovlMonDeptStatus);
					comWiseMonKpiStatus.put("month", month);
					comWiseMonKpiStatus.put("year", year);
					GenericValue DeptOverallResultADD = delegator.makeValue("CompanyWiseKpiMontlyStat", comWiseMonKpiStatus);
					delegator.createOrStore(DeptOverallResultADD);
				}
			}
			kpiSelection = FastList.newInstance();
			kpiSelection.add(EntityCondition.makeCondition("year", EntityOperator.EQUALS, year));
			compWiseKpiMonthly = delegator.findList("CompanyWiseKpiMontlyStat",
					EntityCondition.makeCondition(kpiSelection, EntityOperator.AND), null, null, null, false);
			double ovlMonCompResult = 0;
			int compKpiCounter = 0;
			for (GenericValue companyGAK : compWiseKpiMonthly) {
				if (companyGAK.get("ovlMonDeptResult") != null
						&& companyGAK.get("ovlMonDeptResult") != "") {
					String indiDepResult = (String)companyGAK.get("ovlMonDeptResult");
					ovlMonCompResult += Double.parseDouble(indiDepResult);
					compKpiCounter++;
				}
			}
			String ovlAnualComResult = df.format(ovlMonCompResult / compKpiCounter);
			String ovlAnnualComStatus = KpiEvents.findKpiStatus(Float.valueOf(ovlAnualComResult));
			Map<String, Object> comWiseAnnualKpiStatus = FastMap.newInstance();
			comWiseAnnualKpiStatus.put("year",year);
			comWiseAnnualKpiStatus.put("companyName","Computer Services Ltd.");
			comWiseAnnualKpiStatus.put("ovlAnnualResult",ovlAnualComResult);
			comWiseAnnualKpiStatus.put("ovlAnnualStatus",ovlAnnualComStatus);
			GenericValue CompOverallResultADD = delegator.makeValue("CompanyWiseKpiAnnualStat", comWiseAnnualKpiStatus);
			delegator.createOrStore(CompOverallResultADD);
			
			resultMap = ServiceUtil.returnSuccess("Company wise report genration Successfully!");
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ServiceUtil.returnError("Company wise report genration Failed!!!");
			return resultMap;
		}
	}
}
