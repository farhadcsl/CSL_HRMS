package org.ofbiz.humanres;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.fortuna.ical4j.model.DateTime;

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
import org.ofbiz.humanres.dataimport.HrDataImportServices;
import org.ofbiz.humanres.kpi.KpiEmailServices;
import org.ofbiz.party.communication.CommunicationEventServices;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericAbstractDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
//import org.python.modules.re;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transaction;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

public class KpiEvents {
	public static final String module = CommunicationEventServices.class.getName();
	private static final String ZERO_VALUE = "0";

	public static Map updateKpi(DispatchContext dctx, Map context) {
		Delegator delegator = dctx.getDelegator();
		String partyId = (String) context.get("partyId");
		String mYear = (String) context.get("mYear");
		String achievementStatus = "";
		Float achievementResult = 0f;
		Float totalAchvResult = 0f;
		Float overallAchvResult = 0f;
		int kpiCount = 0;
		String overallAchvStatus = "";
		Map resultMap = null;
		String goalsKpiId;
		String unit;
		String unitRemarks;
		String target;
		String achievement;
		String mgrApprStatus = "";
		GenericValue empKpiValues = null;
		String employeeId, managerId, department;
		try {
			List<EntityCondition> findEmployeeKpi = FastList.newInstance();

			findEmployeeKpi.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			findEmployeeKpi.add(EntityCondition.makeCondition("year", EntityOperator.EQUALS, mYear));
			List<GenericValue> empKpi = delegator.findList("EmployeeGoalsAndKpi",
					EntityCondition.makeCondition(findEmployeeKpi, EntityOperator.AND), null, null, null, false);

			if (empKpi != null) {
				for (int i = 0; i < empKpi.size(); i++) {
					empKpiValues = empKpi.get(i);
					goalsKpiId = (String) empKpiValues.get("goalsKpiId");
					unit = (String) empKpiValues.get("unit");
					unitRemarks = (String) empKpiValues.get("unitRemarks");
					target = (String) empKpiValues.get("target");
					achievement = (String) empKpiValues.get("achievement");

					// kpiresult = calculatekpiresult(unit, target,
					// achievement);
					achievementResult = calculateIndividualKpi(unitRemarks, target, achievement);
					achievementResult = (float) Math.round(achievementResult * 100) / 100;
					achievementStatus = findKpiStatus(achievementResult);
					// edied for overall achievement Result
					totalAchvResult = totalAchvResult + achievementResult;
					// edied for overall achievement Result
					Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
					Map<String, Object> EmployeeKpiIn = FastMap.newInstance();
					EmployeeKpiIn.put("goalsKpiId", goalsKpiId);
					EmployeeKpiIn.put("achievement", achievement);
					EmployeeKpiIn.put("achievementResult", achievementResult.toString());
					EmployeeKpiIn.put("achievementStatus", achievementStatus);
					GenericValue EmployeeKpiUpdate = delegator.makeValue("EmployeeGoalsAndKpi", EmployeeKpiIn);
					// delegator.createOrStore(EmployeeKpiUpdate);
					EmployeeKpiUpdate.store();
					kpiCount++;
				}
			}

			// new edited march 27
			employeeId = (String) empKpiValues.get("employeeId");
			managerId = (String) empKpiValues.get("managerId");
			department = (String) empKpiValues.get("department");
			// new edited march 27

			// new edited march 23
			// String overallgoalsKpiId =
			// delegator.getNextSeqId("EmployeeGoalsAndKpiOverallStatus");
			Map<String, Object> EmployeeOverallResultIn = FastMap.newInstance();
			overallAchvResult = totalAchvResult / kpiCount;
			overallAchvResult = (float) Math.round(overallAchvResult * 100) / 100;
			overallAchvStatus = findKpiStatus(overallAchvResult);
			// EmployeeOverallResultIn.put("overallgoalsKpiId",
			// overallgoalsKpiId);
			EmployeeOverallResultIn.put("partyYear", partyId+mYear);
			EmployeeOverallResultIn.put("partyId", partyId);
			EmployeeOverallResultIn.put("employeeId", employeeId);
			EmployeeOverallResultIn.put("managerId", managerId);
			EmployeeOverallResultIn.put("department", department);
			EmployeeOverallResultIn.put("overallAchvResult", overallAchvResult.toString());
			EmployeeOverallResultIn.put("overallAchvStatus", overallAchvStatus);
			EmployeeOverallResultIn.put("mgrApprStatus", mgrApprStatus);
			EmployeeOverallResultIn.put("mgrRemarks", "-");
			EmployeeOverallResultIn.put("kpiType", "Personal");
			EmployeeOverallResultIn.put("year", mYear);

			GenericValue EmployeeOverallResultADD = delegator.makeValue("EmployeeGoalsAndKpiOverallStatus",
					EmployeeOverallResultIn);
			delegator.createOrStore(EmployeeOverallResultADD);
			// new edited march 23

			String managerPartyId = new HrDataImportServices().findPartyIdByEmployeeId(delegator, managerId);

			// String managerEmailId = getEmailByParty(delegator,
			// managerPartyId);
			// sendEmailWhenKPIChange( dctx, managerEmailId, mgrApprStatus);

			resultMap = ServiceUtil.returnSuccess("Achievement Status Updated Successfully!");
			return resultMap;

		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ServiceUtil.returnError("Achievement Status failed to update!");
			return resultMap;
		}
	}

	public static Map updateKPIByManagerService(DispatchContext dctx, Map context) {
		Delegator delegator = dctx.getDelegator();
		String partyId = (String) context.get("partyId");
		String achievementStatus = "";
		Float achievementResult = 0f;
		Float totalAchvResult = 0f;
		Float overallAchvResult = 0f;
		int kpiCount = 0;
		String overallAchvStatus = "";
		Map resultMap = null;
		String goalsKpiId;
		String unit;
		String unitRemarks;
		String target;
		String achievement;
		String year = null;
		String mgrApprStatus = "Reviewed by Manager";

		GenericValue empKpiValues = null;
		String employeeId, managerId, department;
		try {
			List<EntityCondition> findEmployeeKpi = FastList.newInstance();

			findEmployeeKpi.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			List<GenericValue> empKpi = delegator.findList("EmployeeGoalsAndKpi",
					EntityCondition.makeCondition(findEmployeeKpi, EntityOperator.AND), null, null, null, false);

			if (empKpi != null) {
				for (int i = 0; i < empKpi.size(); i++) {
					empKpiValues = empKpi.get(i);
					goalsKpiId = (String) empKpiValues.get("goalsKpiId");
					unit = (String) empKpiValues.get("unit");
					unitRemarks = (String) empKpiValues.get("unitRemarks");
					target = (String) empKpiValues.get("target");
					achievement = (String) empKpiValues.get("achievement");
					year = (String) empKpiValues.get("year");

					// kpiresult = calculatekpiresult(unit, target,
					// achievement);
					achievementResult = calculateIndividualKpi(unitRemarks, target, achievement);
					achievementResult = (float) Math.round(achievementResult * 100) / 100;
					achievementStatus = findKpiStatus(achievementResult);
					// edied for overall achievement Result
					totalAchvResult = totalAchvResult + achievementResult;
					// edied for overall achievement Result
					Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
					Map<String, Object> EmployeeKpiIn = FastMap.newInstance();
					EmployeeKpiIn.put("goalsKpiId", goalsKpiId);
					EmployeeKpiIn.put("achievement", achievement);
					EmployeeKpiIn.put("achievementResult", achievementResult.toString());
					EmployeeKpiIn.put("achievementStatus", achievementStatus);
					GenericValue EmployeeKpiUpdate = delegator.makeValue("EmployeeGoalsAndKpi", EmployeeKpiIn);
					// delegator.createOrStore(EmployeeKpiUpdate);
					EmployeeKpiUpdate.store();
					kpiCount++;
				}
			}

			// new edited march 27
			employeeId = (String) empKpiValues.get("employeeId");
			managerId = (String) empKpiValues.get("managerId");
			department = (String) empKpiValues.get("department");
			// new edited march 27

			// new edited march 23
			// String overallgoalsKpiId =
			// delegator.getNextSeqId("EmployeeGoalsAndKpiOverallStatus");
			Map<String, Object> EmployeeOverallResultIn = FastMap.newInstance();
			overallAchvResult = totalAchvResult / kpiCount;
			overallAchvResult = (float) Math.round(overallAchvResult * 100) / 100;
			overallAchvStatus = findKpiStatus(overallAchvResult);
			// EmployeeOverallResultIn.put("overallgoalsKpiId",
			// overallgoalsKpiId);
			EmployeeOverallResultIn.put("partyId", partyId);
			EmployeeOverallResultIn.put("employeeId", employeeId);
			EmployeeOverallResultIn.put("managerId", managerId);
			EmployeeOverallResultIn.put("department", department);
			EmployeeOverallResultIn.put("overallAchvResult", overallAchvResult.toString());
			EmployeeOverallResultIn.put("overallAchvStatus", overallAchvStatus);
			EmployeeOverallResultIn.put("mgrApprStatus", mgrApprStatus);
			EmployeeOverallResultIn.put("mgrRemarks", "mgrRemarks");
			EmployeeOverallResultIn.put("kpiType", "Personal");
			EmployeeOverallResultIn.put("year", year);

			GenericValue EmployeeOverallResultADD = delegator.makeValue("EmployeeGoalsAndKpiOverallStatus",
					EmployeeOverallResultIn);
			delegator.createOrStore(EmployeeOverallResultADD);
			// new edited march 23

			// String employeePartyId = getEmailByParty(delegator, partyId);
			// sendEmailWhenKPIChange( dctx, employeePartyId, mgrApprStatus);

			resultMap = ServiceUtil.returnSuccess("Achievement Status Updated Successfully!");
			return resultMap;

		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ServiceUtil.returnError("Achievement Status failed to update!");
			return resultMap;
		}
	}

	public static float calculatekpiresult(String unit, String target, String achievement) {
		Float result = 0f;
		String tempUnit = unit.toLowerCase();
		String kpiUnit = null;
		if (tempUnit.length() < 4) {
			kpiUnit = tempUnit.substring(0);
		} else {
			kpiUnit = tempUnit.substring(0, 3);
		}
		Float calcA = Float.parseFloat(target);
		Float calcB = Float.parseFloat(achievement);
		if ((kpiUnit.equals("day")) || (kpiUnit.equals("mon")) || (kpiUnit.equals("hou"))
				|| (kpiUnit.equals("bus")) && (calcA > calcB)) {
			result = (calcA / calcB) * 100;
		}

		else {
			result = (calcB / calcA) * 100;
		}


		return result;
	}

	public static float calculateIndividualKpi(String unitRemarks, String target, String achievement) {
		Float result = 0f;
		String kpiUnit = unitRemarks.toLowerCase();
		Float calcA = Float.parseFloat(target);
		Float calcB = Float.parseFloat(achievement);

		// Modification by Parves and Farhad

		if (!kpiUnit.isEmpty()) {

			if (kpiUnit.equals("greater than or equal")) {
				if (calcA == 0) {
					result = (float) 120;
				} else if (calcB == 0) {
					result = (float) 0;
				} else {
					result = (calcB / calcA) * 100;
				}

			} else if (kpiUnit.equals("less than or equal")) {
				if (calcA == 0) {
					result = (float) 120;
				} else if (calcB == 0) {
					result = (float) 120;
				} else {
					result = (calcA / calcB) * 100;
				}
			} else if (kpiUnit.equals("nbd, less than or equal")) {
				if (calcA == 0) {
					result = (float) 120;
				} else if (calcB == 0) {
					result = (float) 120;
				} else {
					result = (calcA / calcB) * 100;
				}
			}

		}

		if (result >= 120) {
			result = (float) 120;
		}

		return result;
		// End Modification
	}

	public static String findKpiStatus(Float achievementResult) {
		String achievementStatus = "";
		if (achievementResult >= 95) {
			achievementStatus = "Outstanding";
		} else if (achievementResult >= 80 && achievementResult < 95)
			achievementStatus = "Excellent";
		else if (achievementResult >= 50 && achievementResult < 80)
			achievementStatus = "Average";
		else if (achievementResult >= 40 && achievementResult < 50)
			achievementStatus = "poor";
		else
			achievementStatus = "Need to improve";
		return achievementStatus;
	}

	// This method is for department wise kpi
	// Created by Md. Farhad Hossain

	public static Map deptWisekpI(DispatchContext dctx, Map context) {
		Delegator delegator = dctx.getDelegator();
		String department = (String) context.get("department");
		String year = (String) context.get("year");
		String achievementStatus = "";
		Float deptResult = 0f;
		Float totaldeptResult = 0f;
		Float overallDeptResult = 0f;
		int deptCount = 0;
		String overallDeptStatus = "";
		Map resultMap = null;
		GenericValue deptKpiValues = null;
		String employeeId, managerId;
		String deptToStore = " ";

		try {
			List<EntityCondition> findDeptWiseKpi = FastList.newInstance();

			findDeptWiseKpi.add(EntityCondition.makeCondition("department", EntityOperator.EQUALS, department));
			List<GenericValue> deptWiseKpi = delegator.findList("EmployeeGoalsAndKpiOverallStatus",
					EntityCondition.makeCondition(findDeptWiseKpi, EntityOperator.AND), null, null, null, false);

			if (deptWiseKpi != null) {
				for (int i = 0; i < deptWiseKpi.size(); i++) {
					deptKpiValues = deptWiseKpi.get(i);
					String collectiondeptResult = (String) deptKpiValues.get("overallAchvResult");
					deptResult = Float.parseFloat(collectiondeptResult);
					totaldeptResult = totaldeptResult + deptResult;
					deptCount++;
				}

				deptToStore = (String) deptKpiValues.get("department");
			}

			Map<String, Object> DeptOverallResultIn = FastMap.newInstance();

			overallDeptResult = totaldeptResult / deptCount;
			overallDeptResult = (float) Math.round(overallDeptResult * 100) / 100;
			overallDeptStatus = findKpiStatus(overallDeptResult);
			DeptOverallResultIn.put("departmentYear", department+year);
			DeptOverallResultIn.put("department", deptToStore);
			DeptOverallResultIn.put("overallDeptResult", overallDeptResult.toString());
			DeptOverallResultIn.put("overallDeptStatus", overallDeptStatus);
			DeptOverallResultIn.put("year", year);
			DeptOverallResultIn.put("month", "month");
			DeptOverallResultIn.put("hodStatus", "hodStatus");
			DeptOverallResultIn.put("remarks", "remarks");

			try {
				GenericValue DeptOverallResultADD = delegator.makeValue("DepartmentWiseKpiStatus", DeptOverallResultIn);
				delegator.createOrStore(DeptOverallResultADD);

			} catch (Exception e) {
				e.printStackTrace();
			}

			resultMap = ServiceUtil.returnSuccess("Department Data Generated Successfully");
			return resultMap;

		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ServiceUtil.returnError("Department Data failed to update!");
			return resultMap;
		}
	}

	public static String sendEmailWhenKPIChange(DispatchContext ctx, String email, String mailBody) {
		// Get the local Service dispatcher from the context
		// Note: Dont forget to import the org.ofbiz.service.* classes
		LocalDispatcher dispatcher = (LocalDispatcher) ctx.getDispatcher();
		String errMsg = null;
		// The following are hardcoded as an example only
		// Your program would set these up from the context or from
		// database lookups

		/* Map sendMailContext = new HashMap(); */
		Map<String, Object> sendMailContext = FastMap.newInstance();
		/* Map<String, Object> sendMailParams = FastMap.newInstance(); */
		sendMailContext.put("sendTo", email);
		sendMailContext.put("sendCc", "");
		sendMailContext.put("sendBcc", "");
		sendMailContext.put("sendFrom", "admin.erp@computerservicesltd.com");
		sendMailContext.put("subject", "KPI Review Notification");
		sendMailContext.put("body", mailBody + "\n\n\nComputer Services Ltd.");
		try {
			// Call the sendMail Service and pass the sendMailContext
			// Map object.
			// Set timeout to 360 and wrap with a new transaction
			Map<String, Object> result = null;

			result = dispatcher.runSync("sendMail", sendMailContext);
			// Note easy way to get errors when they are returned
			// from another Service

			// if (ModelService.RESPOND_ERROR.equals((String)
			// result.get(ModelService.RESPONSE_MESSAGE))) {
			// Map<String, Object> messageMap = UtilMisc.toMap("errorMessage",
			// result.get(ModelService.ERROR_MESSAGE));
			// errMsg = "Problem sending this email";
			// ctx.setAttribute("_ERROR_MESSAGE_", errMsg);
			// return "error";
			// }
		} catch (GenericServiceException e) {
			// For Events error messages are passed back
			Debug.logWarning(e, "", module);
			// as Request Attributes
			errMsg = "Problem with the send Service call...!";
			// request.setAttribute("_ERROR_MESSAGE_", errMsg);
			return "error";
		}
		return "success";
	}

	// Get Email Address using Party Id
	public static String getEmailByParty(Delegator delegator, String partyId) {

		String email = "";

		try {
			List<GenericValue> listPartyContactMechs = delegator.findList("PartyContactMech",
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null, null, null, false);
			String contactMechIdToCheck = "";
			GenericValue contactMech = null;
			for (GenericValue val : listPartyContactMechs) {
				contactMechIdToCheck = (String) val.get("contactMechId");
				List<EntityExpr> exprs = FastList.newInstance();
				exprs.add(EntityCondition.makeCondition("contactMechId", EntityOperator.EQUALS, contactMechIdToCheck));
				exprs.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "EMAIL_ADDRESS"));

				List<GenericValue> listContactMechs = delegator.findList("ContactMech",
						EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
				if (listContactMechs.size() > 0) {
					contactMech = listContactMechs.get(0);
				}

			}

			if (contactMech != null)
				email = (String) contactMech.get("infoString");

			if (email == null)
				email = "";

		} catch (GenericEntityException e) {
		}

		return email;

	}

	// Modified by Parves

	public static Map generateMonthlyGoalsAndKpi(DispatchContext dctx, Map context) {
		Delegator delegator = dctx.getDelegator();
		String partyId = (String) context.get("partyId");
		String targedYear = (String) context.get("year");
		int kpiCount = 0;
		Map resultMap = null;
		String goalsKpiId;

		GenericValue empKpiValues = null;
		String mTarget = "";
		String mMonth = "";
		Timestamp mKpiDate = null;
		//String mYear = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
		String mYear = null;
		int targetCounter = 0;
		boolean checkFlag = true;
		String goal, kpi, unit, unitRemarks, param;
		double increasedTarget = 0;
		String[] monthWiseTarget = null;

		try {
			List<EntityCondition> findEmployeeKpi = FastList.newInstance();

			findEmployeeKpi.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			findEmployeeKpi.add(EntityCondition.makeCondition("year", EntityOperator.EQUALS, targedYear));
			List<GenericValue> empKpi = delegator.findList("EmployeeGoalsAndKpi",
					EntityCondition.makeCondition(findEmployeeKpi, EntityOperator.AND), null, null, null, false);
			
			
			//updated 27/01/2019 By Abdullah Al Parves to restrict condition
			if (empKpi.isEmpty() || empKpi.equals("null")) {
				resultMap = ServiceUtil.returnError("No available KPI for "+targedYear+" year to generate!!!");
				return resultMap;
			}else{
				findEmployeeKpi = FastList.newInstance();
				findEmployeeKpi.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				findEmployeeKpi.add(EntityCondition.makeCondition("mYear", EntityOperator.EQUALS, targedYear));
				List<GenericValue> empKpiMonthly = delegator.findList("EmployeeGoalsAndKpiMonthly",
						EntityCondition.makeCondition(findEmployeeKpi, EntityOperator.AND), null, null, null, false);
				
				if (empKpiMonthly.isEmpty() || empKpiMonthly.equals("null")) {

					if (empKpi != null) {
						String target = null, department = null, employeeId = null, managerId = null;
						for (int i = 0; i < empKpi.size(); i++) {
							empKpiValues = empKpi.get(i);
							goalsKpiId = (String) empKpiValues.get("goalsKpiId");
							department = (String) empKpiValues.get("department");
							employeeId = (String) empKpiValues.get("employeeId");
							managerId = (String) empKpiValues.get("managerId");
							target = (String) empKpiValues.get("target");
							// testTarget=(String) empKpiValues.get("target");
							goal = (String) empKpiValues.get("goal");
							kpi = (String) empKpiValues.get("kpi");
							unit = (String) empKpiValues.get("unit");
							unitRemarks = (String) empKpiValues.get("unitRemarks");
							param = (String) empKpiValues.get("param");
							monthWiseTarget = giveMonthWiseTarget(target, param);
							mYear = (String) empKpiValues.get("year");

							for (int j = 0; j < 12; j++) {
								Map<String, Object> EmployeeKpiIn = FastMap.newInstance();
								String mGoalsKpiId = delegator.getNextSeqId("EmployeeGoalsAndKpiMonthly");
								EmployeeKpiIn.put("mGoalsKpiId", mGoalsKpiId);
								EmployeeKpiIn.put("partyId", partyId);
								EmployeeKpiIn.put("goalsKpiId", goalsKpiId);
								EmployeeKpiIn.put("employeeId", employeeId);
								EmployeeKpiIn.put("managerId", managerId);
								EmployeeKpiIn.put("department", department);
								EmployeeKpiIn.put("goal", goal);
								EmployeeKpiIn.put("kpi", kpi);
								EmployeeKpiIn.put("unit", unit);
								EmployeeKpiIn.put("unitRemarks", unitRemarks);
								mMonth = theMonth(j);
								EmployeeKpiIn.put("mMonth", mMonth);

								EmployeeKpiIn.put("mTarget", monthWiseTarget[j]);
								EmployeeKpiIn.put("mAchievement", "0");
								EmployeeKpiIn.put("mAchievementStatus", "-");

								mKpiDate = getMKpiDate(j);

								EmployeeKpiIn.put("mKpiDate", mKpiDate);
								EmployeeKpiIn.put("mMgrRemarks", "-");
								EmployeeKpiIn.put("mMgrApprStatus", "-");
								EmployeeKpiIn.put("mYear", mYear);

								GenericValue EmployeeKpiMonthlyInsert = delegator.makeValue("EmployeeGoalsAndKpiMonthly",
										EmployeeKpiIn);
								EmployeeKpiMonthlyInsert.create();
							}
							kpiCount++;
						}
					}
				}else{
					resultMap = ServiceUtil.returnError("Monthly Goals and KPI already Ganarated!!!");
					return resultMap;	
				}
			}
			//updated 27/01/2019
			resultMap = ServiceUtil.returnSuccess("Monthly Goals and KPI generated Successfully!");
			return resultMap;

		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ServiceUtil.returnError("Monthly Goals and KPI failed to Ganarate!");
			return resultMap;
		}
	}

	private static Timestamp getMKpiDate(int monthValue) {

		String tempMonth = "0" + monthValue;
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = Integer.parseInt(tempMonth);
		int day = 01;

		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		Timestamp tstamp = new Timestamp(cal.getTimeInMillis());
		return tstamp;
	}

	private static String getMTargetRoundValue(String target, int numberOfDivision) {
		String mTarget = "";
		if (mTarget != null) {
			double tempTarget = Math.floor(Double.parseDouble(target) / numberOfDivision);
			String formatedTarget = String.format("%.0f", tempTarget);
			mTarget = String.valueOf(formatedTarget);
		}
		return mTarget;
	}

	protected static String addValueWithTarget(String target, double addTarget) {
		String mTarget = "";

		double tempTarget = Double.parseDouble(target) + addTarget;
		String formatedTarget = String.format("%.0f", tempTarget);
		mTarget = String.valueOf(formatedTarget);

		return mTarget;
	}

	public static String theMonth(int month) {
		String[] monthNames = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
				"October", "November", "December" };
		return monthNames[month];
	}

	// Modified by Parves

//	public static Map<String, Object> sendEmailNotification(DispatchContext ctx,
//			Map<String, ? extends Object> context) {
//		Map<String, Object> resultMap = null;
//		Delegator delegator = ctx.getDelegator();
//		String approverLoginID = (String) context.get("managerId");
//		String employeePartyId = (String) context.get("partyId");
//		String action = (String) context.get("mgrApprStatus");
//		String year = (String) context.get("mYear");
//		String month = (String) context.get("mMonth");
//
//		String approverPartyID = new HrDataImportServices().findPartyIdByEmployeeId(delegator, approverLoginID);
//
//		resultMap = KpiEmailServices.sendKpiEmail(ctx, context, approverPartyID, employeePartyId, action);
//		return resultMap;
//	}
	
	public static Map sendEmailNotification(DispatchContext dctx,
			Map context) {
		Map resultMap = null;
		GenericValue empKpiOvAllValues = null;
		Delegator delegator = dctx.getDelegator();
		String approverLoginID = (String) context.get("managerId");
		String employeePartyId = (String) context.get("partyId");
		String action = (String) context.get("mgrApprStatus");
		String mYear = (String) context.get("mYear");
		String mMonth = (String) context.get("mMonth");
		
		String monthYearID = "";
		String kpiCounter = "";
		
		try {
			
			List<EntityCondition> findEmployeeKpi = FastList.newInstance();

			findEmployeeKpi.add(EntityCondition.makeCondition("partyId",
					EntityOperator.EQUALS, employeePartyId));

			findEmployeeKpi.add(EntityCondition.makeCondition("mMonth",
					EntityOperator.EQUALS, mMonth));
			findEmployeeKpi.add(EntityCondition.makeCondition("mYear",
					EntityOperator.EQUALS, mYear));
			
			List<GenericValue> empKpiMonthly = delegator.findList(
					"EmployeeGAKOvrlStatMonthly",
					EntityCondition.makeCondition(findEmployeeKpi,
							EntityOperator.AND), null, null, null, false);
			
			Map<String, Object> MonthlyKpiInOvAll = FastMap.newInstance();
			
			if(empKpiMonthly !=null){
				empKpiOvAllValues = empKpiMonthly.get(0);
				monthYearID = (String) empKpiOvAllValues.get("monthYearID");
				kpiCounter = (String) empKpiOvAllValues.get("kpiCounter");
			}
			
			MonthlyKpiInOvAll.put("monthYearID", monthYearID);
			MonthlyKpiInOvAll.put("partyId", employeePartyId);
			MonthlyKpiInOvAll.put("mMonth", mMonth);
			MonthlyKpiInOvAll.put("mYear", mYear);
			MonthlyKpiInOvAll.put("kpiCounter", String.valueOf(Integer.parseInt(kpiCounter)-1));
			GenericValue MonthlyOverallResultADD = delegator.makeValue(
					"EmployeeGAKOvrlStatMonthly", MonthlyKpiInOvAll);
			delegator.createOrStore(MonthlyOverallResultADD);

			
		} catch (Exception e) {
			e.printStackTrace();
		}

		String approverPartyID = new HrDataImportServices().findPartyIdByEmployeeId(delegator, approverLoginID);

		resultMap = KpiEmailServices.sendKpiEmail(dctx, context, approverPartyID, employeePartyId, action);
		return resultMap;
	}

	public static Map updateMonthlyTarget(DispatchContext dctx, Map context) {
		Delegator delegator = dctx.getDelegator();
		String partyId = (String) context.get("partyId");
		String achievementStatus = "";
		Float achievementResult = 0f;
		Float totalAchvResult = 0f;
		Float overallAchvResult = 0f;
		int kpiCount = 0;
		String overallAchvStatus = "";
		Map resultMap = null;
		String goalsKpiId;
		String unit;
		String unitRemarks;
		String target = (String) context.get("mTarget");
		String achievement;
		String mgrApprStatus = "Need to be Reviewed By Manager";
		GenericValue empKpiValues = null;
		String employeeId, managerId, department;
		try {
			List<EntityCondition> findEmployeeKpi = FastList.newInstance();

			findEmployeeKpi.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			List<GenericValue> empKpi = delegator.findList("EmployeeGoalsAndKpiMonthly",
					EntityCondition.makeCondition(findEmployeeKpi, EntityOperator.AND), null, null, null, false);

			if (empKpi != null) {
				for (int i = 0; i < empKpi.size(); i++) {
					empKpiValues = empKpi.get(i);
					goalsKpiId = (String) empKpiValues.get("goalsKpiId");
					unit = (String) empKpiValues.get("unit");
					unitRemarks = (String) empKpiValues.get("unitRemarks");
					target = (String) empKpiValues.get("target");
					achievement = (String) empKpiValues.get("achievement");

					// kpiresult = calculatekpiresult(unit, target,
					// achievement);
					achievementResult = calculateIndividualKpi(unitRemarks, target, achievement);
					achievementStatus = findKpiStatus(achievementResult);
					// edied for overall achievement Result
					totalAchvResult = totalAchvResult + achievementResult;
					// edied for overall achievement Result
					Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
					Map<String, Object> EmployeeKpiIn = FastMap.newInstance();
					EmployeeKpiIn.put("goalsKpiId", goalsKpiId);
					EmployeeKpiIn.put("achievement", achievement);
					EmployeeKpiIn.put("achievementResult", achievementResult.toString());
					EmployeeKpiIn.put("achievementStatus", achievementStatus);
					GenericValue EmployeeKpiUpdate = delegator.makeValue("EmployeeGoalsAndKpiMonthly", EmployeeKpiIn);
					// delegator.createOrStore(EmployeeKpiUpdate);
					EmployeeKpiUpdate.store();
					kpiCount++;
				}
			}

			// new edited march 27EmployeeGoalsAndKpi
			employeeId = (String) empKpiValues.get("employeeId");
			managerId = (String) empKpiValues.get("managerId");
			department = (String) empKpiValues.get("department");
			// new edited march 27

			// new edited march 23
			// String overallgoalsKpiId =
			// delegator.getNextSeqId("EmployeeGoalsAndKpiOverallStatus");
			Map<String, Object> EmployeeOverallResultIn = FastMap.newInstance();
			overallAchvResult = totalAchvResult / kpiCount;
			overallAchvStatus = findKpiStatus(overallAchvResult);
			// EmployeeOverallResultIn.put("overallgoalsKpiId",
			// overallgoalsKpiId);
			EmployeeOverallResultIn.put("partyId", partyId);
			EmployeeOverallResultIn.put("employeeId", employeeId);
			EmployeeOverallResultIn.put("managerId", managerId);
			EmployeeOverallResultIn.put("department", department);
			EmployeeOverallResultIn.put("overallAchvResult", overallAchvResult.toString());
			EmployeeOverallResultIn.put("overallAchvStatus", overallAchvStatus);
			EmployeeOverallResultIn.put("mgrApprStatus", mgrApprStatus);
			EmployeeOverallResultIn.put("mgrRemarks", "mgrRemarks");
			EmployeeOverallResultIn.put("kpiType", "Personal");

			GenericValue EmployeeOverallResultADD = delegator.makeValue("EmployeeGoalsAndKpiOverallStatus",
					EmployeeOverallResultIn);
			delegator.createOrStore(EmployeeOverallResultADD);
			// new edited march 23

			String managerPartyId = new HrDataImportServices().findPartyIdByEmployeeId(delegator, managerId);

			// String managerEmailId = getEmailByParty(delegator,
			// managerPartyId);
			// sendEmailWhenKPIChange( dctx, managerEmailId, mgrApprStatus);

			resultMap = ServiceUtil.returnSuccess("Achievement Status Updated Successfully!");
			return resultMap;

		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ServiceUtil.returnError("Achievement Status failed to update!");
			return resultMap;
		}
	}

	public static String[] giveMonthWiseTarget(String kpItarget, String param) {

		String[] monthValue = new String[12];
		DecimalFormat df = new DecimalFormat("#");
		df.setMaximumFractionDigits(0);
		if (param.equals("12Div")) {
			double roundValue = Math.floor(Double.parseDouble(kpItarget) / 12);

			// System.out.println();
			// System.out.println(roundValue);

			for (int i = 0; i < monthValue.length; i++) {
				monthValue[i] = df.format(roundValue);
			}

			double remaider = Double.parseDouble(kpItarget) % 12;
			System.out.println(remaider);
			if (remaider > 0) {
				for (int i = 0; i < remaider; i++) {
					double temp = Double.parseDouble(monthValue[i]) + 1;
					monthValue[i] = df.format(temp);
				}
			}
		} else if (param.equals("2MarJun")) {
			double roundValue = Math.floor(Double.parseDouble(kpItarget) / 2);
			for (int i = 0; i < monthValue.length; i++) {
				// monthValue[i] = Double.toString(roundValue);
				if (i == 2) {
					monthValue[i] = df.format(roundValue);
				} else if (i == 5) {
					monthValue[i] = df.format(roundValue);
				} else {
					monthValue[i] = "0";
				}
			}

			double remaider = Double.parseDouble(kpItarget) % 2;
			System.out.println(remaider);
			if (remaider > 0) {
				for (int i = 0; i < remaider; i++) {
					double temp = Double.parseDouble(monthValue[2]) + 1;
					monthValue[2] = df.format(temp);
				}
			}
		} else if (param.equals("1Mar")) {
			double roundValue = Double.parseDouble(kpItarget);
			for (int i = 0; i < monthValue.length; i++) {
				if (i == 2) {
					monthValue[i] = df.format(roundValue);
				} else {
					monthValue[i] = "0";
				}
			}
		} else if (param.equals("1Sep")) {
			double roundValue = Double.parseDouble(kpItarget);
			for (int i = 0; i < monthValue.length; i++) {
				if (i == 8) {
					monthValue[i] = df.format(roundValue);
				} else {
					monthValue[i] = "0";
				}
			}
		} else if (param.equals("12Same")) {
			double roundValue = Double.parseDouble(kpItarget);
			for (int i = 0; i < monthValue.length; i++) {
				monthValue[i] = df.format(roundValue);
			}
		} else {
			for (int i = 0; i < monthValue.length; i++) {
				monthValue[i] = "0";
			}
		}

		return monthValue;
	}
	
	public static String changeKpiCounter(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        String employeeId = "";
        String kpiCounter="";
        String mMonth="";
        String mYear="";
        if (null != request.getParameter("userLoginId")) {
        	employeeId = request.getParameter("userLoginId");
        }
        if (null != request.getParameter("kpiCounter")) {
        	kpiCounter = request.getParameter("kpiCounter");
        }
        if (null != request.getParameter("mMonth")) {
        	mMonth = request.getParameter("mMonth");
        }
        if (null != request.getParameter("mYear")) {
        	mYear = request.getParameter("mYear");
        }
        List<GenericValue> employeeKpiOvl = null;
        GenericValue empKpiValues = null;
        String monthYearId= "";
        Map<String, Object> monthlyOvlKpi = FastMap.newInstance();
        
        try {
        	List<EntityCondition> findEmployeeKpiOvl = FastList.newInstance();
        	findEmployeeKpiOvl.add(EntityCondition.makeCondition("employeeId", EntityOperator.EQUALS, employeeId));
        	findEmployeeKpiOvl.add(EntityCondition.makeCondition("mMonth", EntityOperator.EQUALS, mMonth));
        	findEmployeeKpiOvl.add(EntityCondition.makeCondition("mYear", EntityOperator.EQUALS, mYear));
            
			
        	employeeKpiOvl = delegator.findList("EmployeeGAKOvrlStatMonthly",
					EntityCondition.makeCondition(findEmployeeKpiOvl, EntityOperator.AND), null, null, null,
					false);
			
			if (employeeKpiOvl != null) {
					empKpiValues = employeeKpiOvl.get(0);
					monthYearId = (String) empKpiValues.get("monthYearID");
			}
			
			monthlyOvlKpi.put("monthYearID", monthYearId);
			monthlyOvlKpi.put("kpiCounter", kpiCounter);
			
			GenericValue monthlyKpiCounterUpdate = delegator.makeValue("EmployeeGAKOvrlStatMonthly",
					monthlyOvlKpi);
			delegator.createOrStore(monthlyKpiCounterUpdate);


        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return "success";
 }
	
	//Modified by Abdullah Al Parves for change Employee department
	public static Map changeEmplDept(DispatchContext dctx, Map context) {
		Delegator delegator = dctx.getDelegator();
		//String employeeId = (String) context.get("userLoginId");
		String partyId = (String) context.get("partyId");
		String department = (String) context.get("department");

        List<GenericValue> employeeKpiList = null;
        Map resultMap = null;
        
        try {
        	List<EntityCondition> selectionList = FastList.newInstance();
        	selectionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        	
        	//EmployeeGoalsAndKpi department change for specific employee
			List<GenericValue> emplGoalsAndKpiData = delegator.findList("EmployeeGoalsAndKpi",
					EntityCondition.makeCondition(selectionList, EntityOperator.AND), null, null, null, false);
			GenericValue empKpiValues = null;
			if (emplGoalsAndKpiData != null && !emplGoalsAndKpiData.isEmpty()) {						
				String goalsKpiId = null;
				for (int i = 0; i < emplGoalsAndKpiData.size(); i++) {
					empKpiValues = emplGoalsAndKpiData.get(i);
					goalsKpiId = (String) empKpiValues.get("goalsKpiId");
					
					Map<String, Object> emplGoalsKpi = FastMap.newInstance();
					emplGoalsKpi.put("goalsKpiId", goalsKpiId);
					emplGoalsKpi.put("department", department);
					
					GenericValue employeeG_A_KUpdate = delegator.makeValue("EmployeeGoalsAndKpi", emplGoalsKpi);
					delegator.createOrStore(employeeG_A_KUpdate);
				 }
			}
			//EmployeeGoalsAndKpiOverallStatus department change for specific employee
			List<GenericValue> emplGoalsAndKpiOvlStatData = delegator.findList("EmployeeGoalsAndKpiOverallStatus",
					EntityCondition.makeCondition(selectionList, EntityOperator.AND), null, null, null, false);
			empKpiValues = null;
			if (emplGoalsAndKpiOvlStatData != null && !emplGoalsAndKpiOvlStatData.isEmpty()) {						

				for (int i = 0; i < emplGoalsAndKpiOvlStatData.size(); i++) {
					empKpiValues = emplGoalsAndKpiOvlStatData.get(i);
					Map<String, Object> emplGoalsAndKpiOvlStat = FastMap.newInstance();
					emplGoalsAndKpiOvlStat.put("partyId", partyId);
					emplGoalsAndKpiOvlStat.put("department", department);
					GenericValue employeeG_A_KOvlStatUpdate = delegator.makeValue("EmployeeGoalsAndKpiOverallStatus", emplGoalsAndKpiOvlStat);
					delegator.createOrStore(employeeG_A_KOvlStatUpdate);
				 }
			}
			//EmployeeGoalsAndKpiMonthly department change for specific employee
			List<GenericValue> emplG_A_K_MonthlyData = delegator.findList("EmployeeGoalsAndKpiMonthly",
					EntityCondition.makeCondition(selectionList, EntityOperator.AND), null, null, null, false);
			empKpiValues = null;
			if (emplG_A_K_MonthlyData != null && !emplG_A_K_MonthlyData.isEmpty()) {						
				String mGoalsKpiId = null;
				for (int i = 0; i < emplG_A_K_MonthlyData.size(); i++) {
					empKpiValues = emplG_A_K_MonthlyData.get(i);
					mGoalsKpiId = (String) empKpiValues.get("mGoalsKpiId");
					
					Map<String, Object> emplG_A_KMonthly = FastMap.newInstance();
					emplG_A_KMonthly.put("mGoalsKpiId", mGoalsKpiId);
					emplG_A_KMonthly.put("department", department);
					GenericValue employeeG_A_KMonthlyUpdate = delegator.makeValue("EmployeeGoalsAndKpiMonthly", emplG_A_KMonthly);
					delegator.createOrStore(employeeG_A_KMonthlyUpdate);
				 }
			}
			//EmployeeGAKOvrlStatMonthly department change for specific employee
			List<GenericValue> emplG_A_K_MonStatData = delegator.findList("EmployeeGAKOvrlStatMonthly",
					EntityCondition.makeCondition(selectionList, EntityOperator.AND), null, null, null, false);
			empKpiValues = null;
			if (emplG_A_K_MonStatData != null && !emplG_A_K_MonStatData.isEmpty()) {						
				String monthYearID = null;
				for (int i = 0; i < emplG_A_K_MonStatData.size(); i++) {
					empKpiValues = emplG_A_K_MonStatData.get(i);
					monthYearID = (String) empKpiValues.get("monthYearID");
					
					Map<String, Object> emplG_A_KMonStat = FastMap.newInstance();
					emplG_A_KMonStat.put("monthYearID", monthYearID);
					emplG_A_KMonStat.put("department", department);
					GenericValue employeeG_A_KMonStatUpdate = delegator.makeValue("EmployeeGAKOvrlStatMonthly", emplG_A_KMonStat);
					delegator.createOrStore(employeeG_A_KMonStatUpdate);
				 }
			}
			resultMap = ServiceUtil.returnSuccess("Department change Successfully!");
			return resultMap;
        } catch (GenericEntityException e) {
            e.printStackTrace();
            resultMap = ServiceUtil.returnError("Department change operation failed!");
			return resultMap;
        }
        
	}

	
	
	// Modified by Abdullah Al Parve for Change Employee Manager in KPI Module
	public static Map setKpiManagerService(DispatchContext dctx, Map context) {
			Delegator delegator = dctx.getDelegator();
			String partyId = (String) context.get("partyId");
			String managerId = null;
			String managerPartyId = null;
			Map resultMap = null;
			
			
			try {
				List<EntityCondition> changeManager = FastList.newInstance();
				changeManager.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				
				//Get Manager Party Id from Person Table
				List<GenericValue> empPersonData = delegator.findList("Person",
						EntityCondition.makeCondition(changeManager, EntityOperator.AND), null, null, null, false);
				if(empPersonData!=null){
					managerPartyId = (String)empPersonData.get(0).get("attendanceSuperiorId");
				}
				
				//Get Manager Id from UserLogin Table
				List<EntityCondition> getMgLoginId = FastList.newInstance();
				getMgLoginId.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, managerPartyId));
				
				List<GenericValue> empUserLoginData = delegator.findList("UserLogin",
						EntityCondition.makeCondition(getMgLoginId, EntityOperator.AND), null, null, null, false);
				if(empUserLoginData!=null){
					managerId = (String)empUserLoginData.get(0).get("userLoginId");
				}
				
				
				//EmployeeGoalsAndKpi manager change
				List<GenericValue> emplGoalsAndKpiData = delegator.findList("EmployeeGoalsAndKpi",
						EntityCondition.makeCondition(changeManager, EntityOperator.AND), null, null, null, false);
				GenericValue empKpiValues = null;
				if (emplGoalsAndKpiData != null && !emplGoalsAndKpiData.isEmpty()) {						
					String goalsKpiId = null;
					for (int i = 0; i < emplGoalsAndKpiData.size(); i++) {
						empKpiValues = emplGoalsAndKpiData.get(i);
						goalsKpiId = (String) empKpiValues.get("goalsKpiId");
						
						Map<String, Object> emplGoalsKpi = FastMap.newInstance();
						emplGoalsKpi.put("goalsKpiId", goalsKpiId);
						emplGoalsKpi.put("partyId", partyId);
						emplGoalsKpi.put("managerId", managerId);
						GenericValue EmployeeKpiUpdate = delegator.makeValue("EmployeeGoalsAndKpi", emplGoalsKpi);
						EmployeeKpiUpdate.store();
					 }
				}
				
				//EmployeeGoalsAndKpiOverallStatus manager change
				List<GenericValue> emplGAKOvlStsData = delegator.findList("EmployeeGoalsAndKpiOverallStatus",
						EntityCondition.makeCondition(changeManager, EntityOperator.AND), null, null, null, false);

				GenericValue emplGAKOvlStsValues = null;
				if (emplGAKOvlStsData != null && !emplGAKOvlStsData.isEmpty()) {						
					String partyYear = null;
					for (int i = 0; i < emplGAKOvlStsData.size(); i++) {
						emplGAKOvlStsValues = emplGAKOvlStsData.get(i);
						partyYear = (String) emplGAKOvlStsValues.get("partyYear");
						
						Map<String, Object> emplGAKOvlSts = FastMap.newInstance();
						emplGAKOvlSts.put("partyYear", partyYear);
						emplGAKOvlSts.put("managerId", managerId);
						GenericValue emplGAKOvlStsUpdate = delegator.makeValue("EmployeeGoalsAndKpiOverallStatus", emplGAKOvlSts);
						emplGAKOvlStsUpdate.store();
					 }
				
				}
				
				//EmployeeGoalsAndKpiMonthly manager change
				List<GenericValue> emplGAKMonthlyData = delegator.findList("EmployeeGoalsAndKpiMonthly",
						EntityCondition.makeCondition(changeManager, EntityOperator.AND), null, null, null, false);
				GenericValue empKpiMonthlyValues = null;
				if (emplGAKMonthlyData != null && !emplGAKMonthlyData.isEmpty()) {						
					String mGoalsKpiId = null;
					for (int i = 0; i < emplGAKMonthlyData.size(); i++) {
						empKpiMonthlyValues = emplGAKMonthlyData.get(i);
						mGoalsKpiId = (String) empKpiMonthlyValues.get("mGoalsKpiId");
						
						Map<String, Object> emplGAKMonthlyEntry = FastMap.newInstance();
						emplGAKMonthlyEntry.put("mGoalsKpiId", mGoalsKpiId);
						emplGAKMonthlyEntry.put("managerId", managerId);
						GenericValue emplGAKMonthlyUpdate = delegator.makeValue("EmployeeGoalsAndKpiMonthly", emplGAKMonthlyEntry);
						emplGAKMonthlyUpdate.store();
					 }
				}
				
				//EmployeeGAKOvrlStatMonthly manager change
				List<GenericValue> emplGAKOvlStsMonData = delegator.findList("EmployeeGAKOvrlStatMonthly",
						EntityCondition.makeCondition(changeManager, EntityOperator.AND), null, null, null, false);
				GenericValue emplGAKOvlStsMonValues = null;
				if (emplGAKOvlStsMonData != null && !emplGAKOvlStsMonData.isEmpty()) {						
					String monthYearID = null;
					for (int i = 0; i < emplGAKOvlStsMonData.size(); i++) {
						emplGAKOvlStsMonValues = emplGAKOvlStsMonData.get(i);
						monthYearID = (String) emplGAKOvlStsMonValues.get("monthYearID");
						
						Map<String, Object> emplGAKOvlStsMonEntry = FastMap.newInstance();
						emplGAKOvlStsMonEntry.put("monthYearID", monthYearID);
						emplGAKOvlStsMonEntry.put("managerId", managerId);
						GenericValue emplGAKOvlStsMonUpdate = delegator.makeValue("EmployeeGAKOvrlStatMonthly", emplGAKOvlStsMonEntry);
						emplGAKOvlStsMonUpdate.store();
					 }
				}

				resultMap = ServiceUtil.returnSuccess("Manager change Successfully!");
				return resultMap;

			} catch (Exception e) {
				e.printStackTrace();
				resultMap = ServiceUtil.returnError("Manager change operation failed!");
				return resultMap;
			}
	}

}
