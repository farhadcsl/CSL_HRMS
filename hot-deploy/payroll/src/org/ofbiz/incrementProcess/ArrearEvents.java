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

public class ArrearEvents{
	 public static String findfromArrearSalaryList(HttpServletRequest request, HttpServletResponse response) {
		 Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        String employeeID = "";
	       
	        String fiscalYear="";
	        
	        if (null != request.getParameter("employeeId")) {
	        	employeeID = request.getParameter("employeeId");
	        }
	        String monthNumber = "";
	        if (null != request.getParameter("monthNo")) {
	        	monthNumber = request.getParameter("monthNo");
	        }
	        
	       
	        /*if (null != request.getParameter("fiscalYear")) {
	        	fiscalYear = request.getParameter("fiscalYear");
	        }*/
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
	      /*  request.setAttribute("fiscalYear", fiscalYear);*/
	        request.setAttribute("monthNo", monthNumber);
		 return "success";
	 }
	 
	 public static Map<String, Object> updateCheckArrerList(DispatchContext dctx, Map<String, ? extends Object> context) {
	        LocalDispatcher dispatcher = dctx.getDispatcher();
	        Delegator delegator = dctx.getDelegator();
	        Map<String, Object> result = ServiceUtil.returnSuccess();

	        String listActive = (String) context.get("listActive");
	        String monthNo = (String) context.get("monthNo");
	       
	        String partyId = (String) context.get("partyId");
	        
	        
		 if (listActive == null){
			 listActive = "N";
		 }
		 if (listActive.equalsIgnoreCase("N")){
			 listActive = "N";
		 }
	        List<GenericValue> arrearList = null;
	        GenericValue employee = null;
	        try {
	        	arrearList = delegator.findByAnd("ArrearStatus", UtilMisc.toMap("partyId", partyId));
	            employee = EntityUtil.getFirst(arrearList);

	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        employee.set("listActive", listActive);
	        try {
	            delegator.store(employee);
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        result.put("monthNo", monthNo);
	    
	        return result;
	        
	    }
	 public static String processArrear(HttpServletRequest request, HttpServletResponse response) {
		 Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        String monthNumber = "";
	        if (null != request.getParameter("monthNo")) {
	        	monthNumber = request.getParameter("monthNo");
	        }
	       
	        List<GenericValue> partyArrearList = null;
	        try {
	        	partyArrearList = delegator.findByAnd("ArrearStatus",
	                    UtilMisc.toMap("arrearFlag", "Y", "listActive", "Y"));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	       for (GenericValue arrear:partyArrearList){
	    	   double finalBenefitAmount = 0.0;
	    	   double finalDeductionAmount = 0.0;
		        double finalupdatedBenAmount = 0.0;
		        double finalcurrentBenAmount = 0.0;
		        double finalupdatedDedAmount = 0.0;
		        double finalcurrentDedAmount = 0.0;
	    	   String arrearId = arrear.getString("arrearId").toString();
	    	   String arrearPartyId = arrear.getString("partyId").toString();
	    	   /*BigDecimal currentAmmount;
	    	   BigDecimal updatedAmount;*/
	    	   
	    	   List<GenericValue> partyArrearDetailList = null;
		        try {
		        	partyArrearDetailList = delegator.findByAnd("ArrearDetail",
		                    UtilMisc.toMap("partyId", arrearPartyId, "arrearId", arrearId,"calculationType","B"));
		        } catch (GenericEntityException e) {
		            e.printStackTrace();
		        }
		        for (GenericValue arrearDetail:partyArrearDetailList){
		        	
		        	BigDecimal currentAmount =  arrearDetail.getBigDecimal("onAmount");
		        	BigDecimal updatedAmount =  arrearDetail.getBigDecimal("amount");
		        	double dcurrenAmmount = currentAmount.doubleValue();
		        	double dupdatedAmount = updatedAmount.doubleValue();
		        	
		        	finalupdatedBenAmount +=dupdatedAmount-dcurrenAmmount;
		        }
		        //finalupdatedBenAmount = finalupdatedBenAmount-finalcurrentBenAmount;
		        finalupdatedBenAmount = Math.abs(finalupdatedBenAmount);
		        Double monthArr = (Double.parseDouble(monthNumber)) - (Double.parseDouble(arrear.getString("monthNumber")) );
		        finalBenefitAmount = finalupdatedBenAmount * monthArr;
		        List<GenericValue> partyArrearDedDetailList = null;
		        try {
		        	partyArrearDedDetailList = delegator.findByAnd("ArrearDetail",
		                    UtilMisc.toMap("partyId", arrearPartyId, "arrearId", arrearId,"calculationType","D"));
		        } catch (GenericEntityException e) {
		            e.printStackTrace();
		        } 
		        for (GenericValue arrearDedDetail:partyArrearDedDetailList){
		        	BigDecimal currentAmount =  arrearDedDetail.getBigDecimal("onAmount");
		        	BigDecimal updatedAmount =  arrearDedDetail.getBigDecimal("amount");
		        	double dcurrenAmmount = currentAmount.doubleValue();
		        	double dupdatedAmount = updatedAmount.doubleValue();
		        	
			        finalupdatedDedAmount += dupdatedAmount - dcurrenAmmount;
		        }
		        //finalupdatedDedAmount = finalupdatedDedAmount - finalcurrentDedAmount;
		        finalupdatedDedAmount = Math.abs(finalupdatedDedAmount);
		        finalDeductionAmount = finalupdatedDedAmount * monthArr;

		        
		        // =====================ArrearFinal Data Store========================//
		        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
		        Map<String, Object> arrerFinalIn = FastMap.newInstance();
				arrerFinalIn.put("partyId", arrearPartyId);
				arrerFinalIn.put("arrearId", arrearId);
				arrerFinalIn.put("isActive", "Y");
				arrerFinalIn.put("fromDate", currentDatetime);
				arrerFinalIn.put("monthNumber", monthNumber);
				arrerFinalIn.put("arrearBenefit",BigDecimal.valueOf(finalBenefitAmount) );
				arrerFinalIn.put("arrearDeduct",BigDecimal.valueOf(finalDeductionAmount) );
	    	    try {
	    			GenericValue arrerFinalInSetup = delegator
	    				.makeValue("ArrearFinal",
	    						UtilMisc.toMap(arrerFinalIn));
	    			arrerFinalInSetup.create();
	    	    } catch (Exception e) { }
	    	    IncrementService.presentSalaryArrearAdjustment(delegator,arrearPartyId,arrearId);
	       }
	       
	       return "success";
	 }
	
	
}