package org.ofbiz.incrementProcess;

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
import org.ofbiz.salaryprocess.SalaryUtils;
import org.ofbiz.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transaction;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class IncrementAllEvents {
	public static Map<String, Object> updateIncrementList(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map<String, Object> result = ServiceUtil.returnSuccess();

        String incrementFlag = (String) context.get("incrementFlag");
        String incrementAmount = (String) context.get("incrementAmount");
        String calculationMode = (String) context.get("calculationMode");
        String partyId = (String) context.get("partyId");
        
        
	 if (incrementFlag == null){
		 incrementFlag = "N";
	 }
	 if (incrementFlag.equalsIgnoreCase("N")){
		 incrementFlag = "N";
	 }
        List<GenericValue> incrementList = null;
        GenericValue employee = null;
        try {
        	incrementList = delegator.findByAnd("PersonPayrollInfo", UtilMisc.toMap("partyId", partyId));
            employee = EntityUtil.getFirst(incrementList);

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        employee.set("incrementFlag", incrementFlag);
        try {
            delegator.store(employee);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        result.put("incrementAmount", incrementAmount);
        result.put("calculationMode", calculationMode);
        return result;
        
    }
	
	 public static String findfromIncrementSalaryList(HttpServletRequest request, HttpServletResponse response) {
		 Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        String employeeID = "";
	        String incrementAmount="";
	        String calculationMode="";
	        
	        if (null != request.getParameter("employeeId")) {
	        	employeeID = request.getParameter("employeeId");
	        }
	        String monthNumber = "";
	        if (null != request.getParameter("incrementAmount")) {
	        	incrementAmount = request.getParameter("incrementAmount");
	        }
	        
	       
	        if (null != request.getParameter("calculationMode")) {
	        	calculationMode = request.getParameter("calculationMode");
	        }
	        List<GenericValue> partyList = null;
	        try {
	        	partyList = delegator.findByAnd("UserLogin",
	                    UtilMisc.toMap("userLoginId", employeeID));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String partyId = null;
	        if (UtilValidate.isNotEmpty(partyList)) {
	            if (UtilValidate.isNotEmpty(partyList.get(0).get("partyId"))) {
	            	partyId = partyList.get(0).get("partyId").toString();
	            }
	        }
	       
	        request.setAttribute("partyId", partyId);
	        request.setAttribute("calculationMode", calculationMode);
	        request.setAttribute("incrementAmount", incrementAmount);
		 return "success";
	 }
	 
	 public static String overallIncConfig(HttpServletRequest request, HttpServletResponse response) {
		 	Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	      
	        String calculationMode = "";
	        String activeCheck = "";
	        String incrementAmount = "";
	        String monthNumber = "";
	        
	        if (null != request.getParameter("calculationMode")) {
	        	calculationMode = request.getParameter("calculationMode");
	        }
	        if (null != request.getParameter("activeCheck")) {
	        	activeCheck = request.getParameter("activeCheck");
	        }
	        if (null != request.getParameter("incrementAmount")) {
	        	incrementAmount = request.getParameter("incrementAmount");
	        }
	        if (null != request.getParameter("monthNumber")) {
	        	monthNumber = request.getParameter("monthNumber");
	        }
	        List<GenericValue> currentPayrollInfo= null;
			try {
				currentPayrollInfo = delegator.findByAnd("PersonPayrollInfo", UtilMisc.toMap("incrementFlag", "Y"));
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			for (GenericValue payrollList : currentPayrollInfo){
				  String partyId = payrollList.getString("partyId").toString(); 
				  
				  /*Arrear Exist already check*/
				  List<GenericValue> currentArrearExist= null;
					try {
						currentArrearExist = delegator.findByAnd("ArrearStatus", UtilMisc.toMap("partyId", partyId,"arrearFlag","Y"));
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
					if (!currentArrearExist.isEmpty()){
						continue;
						/*request.setAttribute("_ERROR_MESSAGE_", "One increment is already pending for this employee");*/
					}
					
					/*End*/
	        BigDecimal incAmount = new BigDecimal(incrementAmount);
	        List<GenericValue> currentSalaryBasic= null;
			try {
				currentSalaryBasic = delegator.findByAnd("EmplPresentSalary", UtilMisc.toMap("partyId", partyId,"payrollItemTypeId","BASIC"));
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			String strdivisor = "100";
			BigDecimal divisor = new BigDecimal(strdivisor);
			BigDecimal basicFinal;
			BigDecimal levelAmount= (BigDecimal)currentSalaryBasic.get(0).get("amount");
			if(calculationMode.equals("Percentage")){
				basicFinal = levelAmount.add(levelAmount.multiply(incAmount.divide(divisor)));	
			}
			else{
				basicFinal = levelAmount.add(incAmount)	;
			}
			List<GenericValue> salaryVersion= null;
			try {
				salaryVersion = delegator.findByAnd("EmplSalaryVersion", UtilMisc.toMap("partyId", partyId));
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			int versionId =0;
			if (salaryVersion.isEmpty()){
				versionId = 1;
			}
			else{
				int vId = salaryVersion.size();
				versionId = vId +1;
			}
			Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
			// enter into version tabel
			Map<String, Object> versionIn = FastMap.newInstance();
			if (!activeCheck.equalsIgnoreCase("Y")){
				versionIn.put("tillActive", "Y");
			}
			versionIn.put("partyId", partyId);
			versionIn.put("versionId", String.valueOf(versionId));
			versionIn.put("fromDate", currentDatetime);
			try {
	 			GenericValue PersonVersionIn = delegator
	 				.makeValue("EmplSalaryVersion",
	 						UtilMisc.toMap(versionIn));
	 			PersonVersionIn.create();
	    	 }
	    	 catch(Exception e){}
			//enter to track increment
			Map<String, Object> incrementIn = FastMap.newInstance();
			incrementIn.put("partyId", partyId);
			incrementIn.put("calculationMode", calculationMode);
			incrementIn.put("incrementAmount", incrementAmount);
			incrementIn.put("fromDate", currentDatetime);
			incrementIn.put("monthNumber", monthNumber);
			try {
	 			GenericValue PersonincrementIn = delegator
	 				.makeValue("EmplSalIncrement",
	 						UtilMisc.toMap(incrementIn));
	 			PersonincrementIn.create();
	    	 }
	    	 catch(Exception e){}
			
			List<GenericValue> versionDataPrepareList= null;
			try {
				versionDataPrepareList = delegator.findByAnd("EmplPresentSalary", UtilMisc.toMap("partyId", partyId));
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			for (GenericValue value:versionDataPrepareList){
				Map<String, Object> versionDataIn = FastMap.newInstance();
				String payrollItemTypeId = value.getString("payrollItemTypeId");
				versionDataIn.put("payrollItemTypeId", payrollItemTypeId);
				versionDataIn.put("partyId", partyId);
				String calculationTypes = value.getString("calculationType");
				versionDataIn.put("calculationType", calculationTypes);
				String calculationModes = value.getString("calculationMode");
				versionDataIn.put("calculationMode", calculationModes);
				String optValue = value.getString("optValue");
				versionDataIn.put("optValue", optValue);
				BigDecimal mainamount = (BigDecimal)value.get("amount");
				versionDataIn.put("amount", mainamount);
				Timestamp fromDatepre = (Timestamp)value.get("fromDate");
				versionDataIn.put("fromDate", fromDatepre);
				versionDataIn.put("versionId",  String.valueOf(versionId));
				try {
		 			GenericValue PersonVersionDataIn = delegator
		 				.makeValue("EmplPayrollHistory",
		 						UtilMisc.toMap(versionDataIn));
		 			PersonVersionDataIn.create();
		    	 }
		    	 catch(Exception e){}
			}
			if (!activeCheck.equalsIgnoreCase("Y")){
				IncrementService.individualEmplsalaryUpdateWithArrear(delegator, partyId, basicFinal, monthNumber);
			}else {
				IncrementService.individualEmplsalaryUpdateWithoutArrear(delegator, partyId, basicFinal);
			}
			
			}
	        
	        
		return "success";
		} 

}