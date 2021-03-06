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

public class ManagerMonthlyKpiEvents {
	public static final String module = CommunicationEventServices.class
			.getName();

	public static Map<String, Object> updateManagerMonthlyKpi(DispatchContext dctx,
			Map<String, Object> context) {
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
		String mgrApprStatus = "KPI_REVIEWED";
		try {
			List<EntityCondition> findEmployeeKpi = FastList.newInstance();

			findEmployeeKpi.add(EntityCondition.makeCondition("partyId",
					EntityOperator.EQUALS, partyId));
			findEmployeeKpi.add(EntityCondition.makeCondition("year",
					EntityOperator.EQUALS, mYear));
			List<GenericValue> empGoalsKpi = delegator.findList(
					"EmployeeGoalsAndKpi", EntityCondition.makeCondition(
							findEmployeeKpi, EntityOperator.AND), null, null,
					null, false);

			if (empGoalsKpi != null) {
				for (int i = 0; i < empGoalsKpi.size(); i++) {
					empKpiValues = empGoalsKpi.get(i);
					managerId = (String) empKpiValues.get("managerId");
					goalsKpiCount++;
				}
			}
			findEmployeeKpi = FastList.newInstance();
			findEmployeeKpi.add(EntityCondition.makeCondition("partyId",
					EntityOperator.EQUALS, partyId));
			findEmployeeKpi.add(EntityCondition.makeCondition("mMonth",
					EntityOperator.EQUALS, mMonth));
			findEmployeeKpi.add(EntityCondition.makeCondition("mYear",
					EntityOperator.EQUALS, mYear));
			List<GenericValue> empKpiMonthly = delegator.findList(
					"EmployeeGoalsAndKpiMonthly",
					EntityCondition.makeCondition(findEmployeeKpi,
							EntityOperator.AND), null, null, null, false);

			if (empKpiMonthly != null) {
				for (int i = 0; i < empKpiMonthly.size(); i++) {
					empKpiValues = empKpiMonthly.get(i);
					mGoalsKpiId = (String) empKpiValues.get("mGoalsKpiId");
					goalsKpiId = (String) empKpiValues.get("goalsKpiId");
					unit = (String) empKpiValues.get("unit");
					unitRemarks = (String) empKpiValues.get("unitRemarks");
					mTarget = (String) empKpiValues.get("mTarget");
					mAchievement = (String) empKpiValues.get("mAchievement");

					mAchievementResult = KpiEvents.calculateIndividualKpi(
							unitRemarks, mTarget, mAchievement);
					mAchievementResult = (float) Math.round(mAchievementResult*100)/100;
					mAchievementStatus = KpiEvents
							.findKpiStatus(mAchievementResult);
					mTotalAchvResult = mTotalAchvResult + mAchievementResult;
					Map<String, Object> MonthlyKpiIn = FastMap.newInstance();
					MonthlyKpiIn.put("mGoalsKpiId", mGoalsKpiId);
					MonthlyKpiIn.put("goalsKpiId", goalsKpiId);
					MonthlyKpiIn.put("mAchievement", mAchievement);
					MonthlyKpiIn.put("mAchievementResult",
							mAchievementResult.toString());
					MonthlyKpiIn.put("mAchievementStatus", mAchievementStatus);
					GenericValue MonthlyKpiUpdate = delegator.makeValue(
							"EmployeeGoalsAndKpiMonthly", MonthlyKpiIn);
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
			mOverallAchvResult = (float) Math.round(mOverallAchvResult*100)/100;
			mOverallAchvStatus = KpiEvents.findKpiStatus(mOverallAchvResult);
			MonthlyOverallResultIn.put("monthYearID", monthYearID);
			MonthlyOverallResultIn.put("mGoalsKpiId", mmGoalsKpiId);
			MonthlyOverallResultIn.put("partyId", partyId);
			MonthlyOverallResultIn.put("employeeId", employeeId);
			MonthlyOverallResultIn.put("managerId", managerId);
			MonthlyOverallResultIn.put("department", department);
			MonthlyOverallResultIn.put("mOverallAchvResult",
					mOverallAchvResult.toString());
			MonthlyOverallResultIn
					.put("mOverallAchvStatus", mOverallAchvStatus);
			MonthlyOverallResultIn.put("mgrApprStatus", mgrApprStatus);
			MonthlyOverallResultIn.put("mgrRemarks", "mgrRemarks");
			MonthlyOverallResultIn.put("kpiType", "Personal");
			MonthlyOverallResultIn.put("mMonth", mMonth);
			MonthlyOverallResultIn.put("mYear", mYear);

			GenericValue MonthlyOverallResultADD = delegator.makeValue(
					"EmployeeGAKOvrlStatMonthly", MonthlyOverallResultIn);
			delegator.createOrStore(MonthlyOverallResultADD);

			// String managerPartyId = new
			// HrDataImportServices().finupdateKPIYearly(dctx, context);dPartyIdByEmployeeId(delegator,
			// managerId);

			// KpiEvents.sendEmailNotification(dctx, context, managerId,
			// mgrApprStatus);

			resultMap = ServiceUtil
					.returnSuccess("Achievement Status Updated Successfully!");

			// MonthlyKpiEvents.updateMonthlyKpi(dctx, context);
			//KpiEvents.updateKPIByManagerService(dctx, context);
			MonthlyKpiEvents.updateKPIYearly(dctx, context);
			KpiEvents.updateKpi(dctx, context);

			
			String approverPartyID = new HrDataImportServices().findPartyIdByEmployeeId(delegator, managerId);   
			String employeePartyId = partyId;
			String action = mgrApprStatus;
			KpiEmailServices.sendKpiEmail(dctx, context, approverPartyID, employeePartyId, action);

			return resultMap;

		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ServiceUtil
					.returnError("Achievement Status failed to update!");
			return resultMap;
		}
	}

}
