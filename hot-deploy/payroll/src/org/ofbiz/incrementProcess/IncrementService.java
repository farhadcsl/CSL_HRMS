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

public class IncrementService {
	
	public static String individualEmplsalaryUpdateWithArrear(Delegator delegator, String partyId, BigDecimal basicFinal, String monthNumber) {
	      
	       List<EntityExpr> exprsForsalaryItemPick = FastList.newInstance();
			 exprsForsalaryItemPick.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			// exprsForsalaryItemPick.add(EntityCondition.makeCondition("payrollItemTypeId", EntityOperator.NOT_EQUAL, "BASIC"));
			 exprsForsalaryItemPick.add(EntityCondition.makeCondition("payrollItemTypeId", EntityOperator.NOT_EQUAL, "EMPL_PF_INV"));
			 exprsForsalaryItemPick.add(EntityCondition.makeCondition("payrollItemTypeId", EntityOperator.NOT_EQUAL, "COMP_PF_INV"));
			 exprsForsalaryItemPick.add(EntityCondition.makeCondition("payrollItemTypeId", EntityOperator.NOT_EQUAL, "COMP_CPF_INV"));
			 exprsForsalaryItemPick.add(EntityCondition.makeCondition("payrollItemTypeId", EntityOperator.NOT_EQUAL, "TAX"));
			List<GenericValue> currentSalaryList= null;
			try { 
				currentSalaryList = delegator.findList("EmplPresentSalary", EntityCondition.makeCondition(exprsForsalaryItemPick, EntityOperator.AND), null, null, null, false);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
			Map<String, Object> arrearIn = FastMap.newInstance();
			 String arrearId = delegator.getNextSeqId("ArrearStatus");
			arrearIn.put("partyId", partyId);
			arrearIn.put("arrearId", arrearId);
			arrearIn.put("arrearFlag", "Y");
			arrearIn.put("listActive", "Y");
			arrearIn.put("fromDate", currentDatetime);
			arrearIn.put("monthNumber", monthNumber);
			try {
	 			GenericValue PersonArresrIn = delegator
	 				.makeValue("ArrearStatus",
	 						UtilMisc.toMap(arrearIn));
	 			PersonArresrIn.create();
	    	 }
			 catch(Exception e){}
			String strdivisor = "100";
			BigDecimal divisor = new BigDecimal(strdivisor);
			
			for (GenericValue value:currentSalaryList){
				String payrollItemTypeId = value.getString("payrollItemTypeId");
				String calculationTypes = value.getString("calculationType");
				String calculationModes = value.getString("calculationMode");
				String optValue = value.getString("optValue");
				BigDecimal calcValu = new BigDecimal(optValue);
				BigDecimal mainamount = (BigDecimal)value.get("amount");
				BigDecimal onAmount = (BigDecimal)value.get("amount");
				if (payrollItemTypeId.equalsIgnoreCase("BASIC")){
					mainamount = basicFinal;
					optValue = basicFinal.toString();
				}
				if (calculationModes.equalsIgnoreCase("Percentage")){
					mainamount = basicFinal.multiply(calcValu.divide(divisor));
				}
				Map<String, Object> arrearDataIn = FastMap.newInstance();
				arrearDataIn.put("payrollItemTypeId", payrollItemTypeId);
				arrearDataIn.put("partyId", partyId);
				arrearDataIn.put("calculationType", calculationTypes);
				arrearDataIn.put("calculationMode", calculationModes);
				arrearDataIn.put("optValue", optValue);
				arrearDataIn.put("amount", mainamount);
				arrearDataIn.put("onAmount", onAmount);
				arrearDataIn.put("arrearId", arrearId);
				arrearDataIn.put("fromDate", currentDatetime);
				
				
				try {
		 			GenericValue PersonArresrDataIn = delegator
		 				.makeValue("ArrearDetail",
		 						UtilMisc.toMap(arrearDataIn));
		 			PersonArresrDataIn.create();
		    	 }
				 catch(Exception e){}
			}
	        return "success";
	    }
	public static String individualEmplsalaryUpdateWithoutArrear(Delegator delegator, String partyId, BigDecimal basicFinal) {
	       String payrollItemTypeName = "";
	       List<EntityExpr> exprsForsalaryItemPick = FastList.newInstance();
			 exprsForsalaryItemPick.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			// exprsForsalaryItemPick.add(EntityCondition.makeCondition("payrollItemTypeId", EntityOperator.NOT_EQUAL, "BASIC"));
			 exprsForsalaryItemPick.add(EntityCondition.makeCondition("payrollItemTypeId", EntityOperator.NOT_EQUAL, "EMPL_PF_INV"));
			 exprsForsalaryItemPick.add(EntityCondition.makeCondition("payrollItemTypeId", EntityOperator.NOT_EQUAL, "COMP_PF_INV"));
			 exprsForsalaryItemPick.add(EntityCondition.makeCondition("payrollItemTypeId", EntityOperator.NOT_EQUAL, "COMP_CPF_INV"));
			 exprsForsalaryItemPick.add(EntityCondition.makeCondition("payrollItemTypeId", EntityOperator.NOT_EQUAL, "TAX"));
			List<GenericValue> currentSalaryList= null;
			try { 
				currentSalaryList = delegator.findList("EmplPresentSalary", EntityCondition.makeCondition(exprsForsalaryItemPick, EntityOperator.AND), null, null, null, false);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			String strdivisor = "100";
			BigDecimal divisor = new BigDecimal(strdivisor);
			Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
			for (GenericValue value:currentSalaryList){
				String payrollItemTypeId = value.getString("payrollItemTypeId");
				String calculationTypes = value.getString("calculationType");
				String calculationModes = value.getString("calculationMode");
				String optValue = value.getString("optValue");
				BigDecimal calcValu = new BigDecimal(optValue);
				BigDecimal mainamount = (BigDecimal)value.get("amount");
				if (payrollItemTypeId.equalsIgnoreCase("BASIC")){
					mainamount = basicFinal;
					optValue = basicFinal.toString();
				}
				if (calculationModes.equalsIgnoreCase("Percentage")){
					mainamount = basicFinal.multiply(calcValu.divide(divisor));
				}
				
				GenericValue updateEemplSalary = null; 
		        try {
		        	updateEemplSalary = delegator.findByPrimaryKey("EmplPresentSalary", UtilMisc.toMap("partyId", partyId,"payrollItemTypeId",payrollItemTypeId));
		        	updateEemplSalary.set("calculationType", calculationTypes);
		        	updateEemplSalary.set("calculationMode", calculationModes);
		        	updateEemplSalary.set("optValue", optValue);
		        	updateEemplSalary.set("calculationType", calculationTypes);
		        	updateEemplSalary.set("calculationMode", calculationModes);
		        	updateEemplSalary.set("amount", mainamount);
		        	updateEemplSalary.set("fromDate", currentDatetime);
		        	
		        	updateEemplSalary.store();
		        } catch (GenericEntityException e) {
		            e.printStackTrace();
		        }
			}
			
	        return "Success";
	    }
	
	

	public static String presentSalaryArrearAdjustment(Delegator delegator, String partyId,String arrearId) {
	       String payrollItemTypeName = "";
	       List<EntityExpr> exprsForsalaryItemPick = FastList.newInstance();
			exprsForsalaryItemPick.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			exprsForsalaryItemPick.add(EntityCondition.makeCondition("arrearId", EntityOperator.EQUALS, arrearId));
			List<GenericValue> arrearSalaryList= null;
			try { 
				arrearSalaryList = delegator.findList("ArrearDetail", EntityCondition.makeCondition(exprsForsalaryItemPick, EntityOperator.AND), null, null, null, false);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());

			
			
			
			
			
			// --------------- Update Salary Version ------------------------- //
			List<GenericValue> salaryVersion= null;
			try {
				salaryVersion = delegator.findByAnd("EmplSalaryVersion", UtilMisc.toMap("partyId", partyId,"tillActive","Y"));

				for (GenericValue value:salaryVersion){
					
		    	    
		    	    String versionId= value.get("versionId").toString();
		    	    GenericValue updateSalaryVersion = null; 
			        try {
			        	updateSalaryVersion = delegator.findByPrimaryKey("EmplSalaryVersion", UtilMisc.toMap("partyId", partyId,"versionId",versionId));
			        	updateSalaryVersion.set("tillActive", "N");     	
			        	updateSalaryVersion.store();
			        } catch (GenericEntityException e) {
			            e.printStackTrace();
			        }
			        
			        //--------ArrearStatus Flag Update----------------//
		    	    GenericValue updateArrearStatus = null; 
			        try {
			        	updateArrearStatus = delegator.findByPrimaryKey("ArrearStatus", UtilMisc.toMap("partyId", partyId,"arrearId",arrearId));
			        	updateArrearStatus.set("arrearFlag", "N");     	
			        	updateArrearStatus.store();
			        } catch (GenericEntityException e) {
			            e.printStackTrace();
			        }
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			
			// ----------Store Present SalaryData Into Version Table ------------//
			List<GenericValue> versionDataPrepareList= null;
			try {
				versionDataPrepareList = delegator.findByAnd("EmplPresentSalary", UtilMisc.toMap("partyId", partyId));
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			
			// ------------------------Update EmplPresentSalary Data ----------------//
			for (GenericValue value:arrearSalaryList){
				String payrollItemTypeId = value.getString("payrollItemTypeId");
				String calculationTypes = value.getString("calculationType");
				String calculationModes = value.getString("calculationMode");
				String optValue = value.getString("optValue");
				BigDecimal calcValu = new BigDecimal(optValue);
				BigDecimal mainamount = (BigDecimal)value.get("amount");
				
				GenericValue updateEemplSalary = null; 
		        try {
		        	updateEemplSalary = delegator.findByPrimaryKey("EmplPresentSalary", UtilMisc.toMap("partyId", partyId,"payrollItemTypeId",payrollItemTypeId));
		        	updateEemplSalary.set("calculationType", calculationTypes);
		        	updateEemplSalary.set("calculationMode", calculationModes);
		        	updateEemplSalary.set("optValue", optValue);
		        	updateEemplSalary.set("amount", mainamount);
		        	updateEemplSalary.set("fromDate", currentDatetime);		        	
		        	updateEemplSalary.store();
		        } catch (GenericEntityException e) {
		            e.printStackTrace();
		        }
			}
			
	        return "Success";
	    }
	
}
