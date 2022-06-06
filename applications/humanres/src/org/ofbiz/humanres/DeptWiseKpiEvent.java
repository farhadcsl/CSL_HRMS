package org.ofbiz.humanres;

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
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
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
import java.text.ParseException;
import java.util.*;

public class DeptWiseKpiEvent {

	public static Map deptWisekpI(DispatchContext dctx, Map context) {
		Delegator delegator = dctx.getDelegator();
		String department = (String) context.get("department");
		String achievementStatus = "";
		Float deptResult = 0f;
		Float totaldeptResult = 0f;
		Float overallDeptResult = 0f;
		int deptCount = 0;
		String overallDeptStatus = "";
		Map resultMap = null;
		GenericValue deptKpiValues = null;
		String employeeId, managerId;
		try {
			List<EntityCondition> findDeptWiseKpi = FastList.newInstance();

			findDeptWiseKpi.add(EntityCondition.makeCondition("department",
					EntityOperator.EQUALS, department));
			List<GenericValue> deptWiseKpi = delegator.findList(
					"EmployeeGoalsAndKpiOverallStatus", EntityCondition.makeCondition(
							findDeptWiseKpi, EntityOperator.AND), null, null,
					null, false);

			if (deptWiseKpi != null) {
				for (int i = 0; i < deptWiseKpi.size(); i++) {
					deptKpiValues = deptWiseKpi.get(i);
					deptResult = (Float) deptKpiValues.get("overallAchvResult");
					totaldeptResult = totaldeptResult + deptResult;
					deptCount++ ;
				}	
			}
			

			Map<String, Object> DeptOverallResultIn = FastMap.newInstance();
			overallDeptResult = totaldeptResult/deptCount;
			overallDeptStatus = findKpiStatus(overallDeptResult);
			DeptOverallResultIn.put("department", department);
			DeptOverallResultIn.put("overallDeptResult", overallDeptResult.toString());
			DeptOverallResultIn.put("overallDeptStatus", overallDeptStatus);
			DeptOverallResultIn.put("Year", "2018");
			DeptOverallResultIn.put("month", "month");
			DeptOverallResultIn.put("hodStatus", "hodStatus");
			DeptOverallResultIn.put("remarks", "remarks");
			
			
			GenericValue DeptOverallResultADD = delegator.makeValue(
					"DepartmentWiseKpiStatus", DeptOverallResultIn);
			delegator.createOrStore(DeptOverallResultADD);
			// new edited march 23
			
			resultMap = ServiceUtil
					.returnSuccess("Achievement Status Updated Successfully!");
			return resultMap;

		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ServiceUtil.returnError("Achievement Status failed to update!");
			return resultMap;
		}
	}

	public static float calculatekpiresult(String unit, String target,
			String achievement) {
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
		if ((kpiUnit.equals("day")) || (kpiUnit.equals("mon"))
				|| (kpiUnit.equals("hou")) || (kpiUnit.equals("bus")) && (calcA > calcB)) {
			result = (calcA / calcB) * 100;
		}

		else {
			result = (calcB / calcA) * 100;
		}
		
//		Modification by Parves and Farhad
		
		
//		if(!kpiUnit.isEmpty()){
//			if(kpiUnit.equals("")){
//				
//			}
//			
//		}
		
//		End Modification
		
		
		
		return result;
	}
	
	public static float calculateIndividualKpi(String unitRemarks, String target,
			String achievement) {
		Float result = 0f;
		String kpiUnit = unitRemarks.toLowerCase();
		Float calcA = Float.parseFloat(target);
		Float calcB = Float.parseFloat(achievement);
		
//		Modification by Parves and Farhad
		
		if(!kpiUnit.isEmpty()){
			if(kpiUnit.equals("greater than or equal")){
				result = (calcB / calcA) * 100;
			}else if(kpiUnit.equals("less than or equal")){
				result = (calcA / calcB) * 100;
			}else if(kpiUnit.equals("nbd, less than or equal")){
				result = (calcA / calcB) * 100;
			}
			
		}
		
		return result;
//		End Modification
	}

	public static String findKpiStatus(Float achievementResult) {
		String achievementStatus = "";
		if (achievementResult >= 95) {
			achievementStatus = "Outstanding";
		} else if (achievementResult >= 80 && achievementResult <= 94)
			achievementStatus = "Excellent";
		else if (achievementResult >= 50 && achievementResult <= 79)
			achievementStatus = "Average";
		else if (achievementResult >= 40 && achievementResult <= 49)
			achievementStatus = "poor";
		else
			achievementStatus = "Need to improve";
		return achievementStatus;
	}

}
