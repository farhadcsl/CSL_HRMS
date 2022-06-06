package org.ofbiz.configuration;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.salaryprocess.NewSalaryServices;
import org.ofbiz.salaryprocess.SalaryUtils;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

//Created By Tutul
public class SalarySetup {

    public static final String module = SalarySetup.class.getName();
	//-------------------------------------------------------------------------------------------------------------------
    public static Map<String, Object> findSalaryTemplate(DispatchContext dctx, Map<String, ? extends Object> context) {
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    Delegator delegator = dctx.getDelegator();
	    List<EntityExpr> exprs = FastList.newInstance();
	    exprs.add(EntityCondition.makeCondition("salaryTemplateId", EntityOperator.NOT_EQUAL, ""));        
		List<GenericValue> salaryTemplateList = null;
		try {
			salaryTemplateList = delegator.findList("SalaryTemplate", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		List<Map> cfMap = FastList.newInstance();
		for (Map aParsed : salaryTemplateList){
			Map record = FastMap.newInstance();
			record.putAll(aParsed);
			cfMap.add(record);
		}
	    result.put("salaryTemplateList", cfMap);
	    return result;
	} 
    public static Map<String, Object> loadBasicSalaryStep(DispatchContext dctx, Map<String, ? extends Object> context) {
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    Delegator delegator = dctx.getDelegator();
	    String salaryTemplateId = (String) context.get("salaryTemplateId");	    
	    List<EntityExpr> exprsForGradeLevel = FastList.newInstance();
	    exprsForGradeLevel.add(EntityCondition.makeCondition("salaryTemplateId", EntityOperator.EQUALS, salaryTemplateId));
		List<GenericValue> SalTempBasSalList = null;
		try { 
			SalTempBasSalList = delegator.findList("SalTempBasSalAssign", EntityCondition.makeCondition(exprsForGradeLevel, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
	    int count=0;
        List exprsForBasicSalaryStep = FastList.newInstance();
		for (Map aParsed : SalTempBasSalList){
        	String basicSalaryStepId = SalTempBasSalList.get(count).get("basicSalaryStepId").toString();
        	exprsForBasicSalaryStep.add(EntityCondition.makeCondition("basicSalaryStepId",basicSalaryStepId));
		    count++;
		}
		if(exprsForBasicSalaryStep.isEmpty()){
			exprsForBasicSalaryStep.add(EntityCondition.makeCondition("basicSalaryStepId",""));
		}
		List<GenericValue> basicSalaryStepList = null;
		try {
			basicSalaryStepList = delegator.findList("BasicSalaryStep", EntityCondition.makeCondition(exprsForBasicSalaryStep, EntityOperator.OR), null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		List<Map> cfMap = FastList.newInstance();
		for (Map aParsed : basicSalaryStepList){
			Map record = FastMap.newInstance();
			record.putAll(aParsed);
			cfMap.add(record);
		}
	    result.put("basicSalaryStepList", cfMap);
	    return result;
	} 
    public static String saveEmployeeSalarySetup(HttpServletRequest request, HttpServletResponse response) {
    	
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        String employeeID = "";
        if (null != request.getParameter("employeeId")) {
        	employeeID = request.getParameter("employeeId");
			request.setAttribute("employeeId", employeeID);
        	
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
        
	    String salaryTemplateId = "";	 
	    String basicSalaryStepId = "";
        if (null != request.getParameter("salaryTemplateId")) {
        	salaryTemplateId =request.getParameter("salaryTemplateId");
        	
        }

        if (null != request.getParameter("basicSalaryStepId")) {
        	basicSalaryStepId = request.getParameter("basicSalaryStepId");        	
        }
	    
	    
	    
	    //------------------- Salary Level Information--------------------//
	    List<GenericValue>payrollItems =null;
	    List<GenericValue> invoicesItem = null;
        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
		try{		
			invoicesItem=delegator.findByAnd("InvoiceItemType", UtilMisc.toMap("defaultGlAccountId", "502100"));
		}catch (GenericEntityException e){
			e.printStackTrace();
			
		}
		 String invoiceItemTypeId = null;
	        if (UtilValidate.isNotEmpty(invoicesItem)) {
	            if (UtilValidate.isNotEmpty(invoicesItem.get(0).get("invoiceItemTypeId"))) {
	            	invoiceItemTypeId = invoicesItem.get(0).get("invoiceItemTypeId").toString();
	            }
	        }
	        try{		
	        	payrollItems=delegator.findByAnd("PayrollItem", UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId));
			}catch (GenericEntityException e){
				e.printStackTrace();
				
			}
	        String payrollItemTypeId = null;
	        if (UtilValidate.isNotEmpty(payrollItems)) {
	            if (UtilValidate.isNotEmpty(payrollItems.get(0).get("payrollItemTypeId"))) {
	            	payrollItemTypeId = payrollItems.get(0).get("payrollItemTypeId").toString();
	            }
	        }
	        
	    List<EntityExpr> exprsForSalaryLevel = FastList.newInstance();
	    exprsForSalaryLevel.add(EntityCondition.makeCondition("basicSalaryStepId", EntityOperator.EQUALS, basicSalaryStepId));
		List<GenericValue> salaryLevelList = null;
		try{		
			salaryLevelList=delegator.findByAnd("BasicSalaryStep", UtilMisc.toMap("basicSalaryStepId", basicSalaryStepId));
		}catch (GenericEntityException e){
			e.printStackTrace();
			
		}
		BigDecimal levelAmount= (BigDecimal)salaryLevelList.get(0).get("amount");
		String optValueforBasic = levelAmount.toString();
		
		///---------------------------- BasicSalary Setup-----------------------//
    	Map<String, Object> EmployeePresentBasicSalarySetupIn = FastMap.newInstance();
    	EmployeePresentBasicSalarySetupIn.put("partyId", partyId);
    	EmployeePresentBasicSalarySetupIn.put("companyId", "");
    	EmployeePresentBasicSalarySetupIn.put("payrollItemTypeId", payrollItemTypeId);
    	EmployeePresentBasicSalarySetupIn.put("calculationType", "B");
    	EmployeePresentBasicSalarySetupIn.put("calculationMode", "Fixed");
    	EmployeePresentBasicSalarySetupIn.put("optValue", optValueforBasic);
    	EmployeePresentBasicSalarySetupIn.put("amount", levelAmount);
    	EmployeePresentBasicSalarySetupIn.put("comments", "");
    	EmployeePresentBasicSalarySetupIn.put("fromDate", currentDatetime);
    	EmployeePresentBasicSalarySetupIn.put("emplStatusId", "EMPL_POS_ACTIVE");
	    try {
			GenericValue employeeBasicSalarySetup = delegator
				.makeValue("EmplPresentSalary",
						UtilMisc.toMap(EmployeePresentBasicSalarySetupIn));
			employeeBasicSalarySetup.create();
	    } catch (Exception e) { 
			request.setAttribute("partyId", partyId);
			request.setAttribute("_ERROR_MESSAGE_", "Already Has Salary Setup For This Employee");
			return "Already Has Salary Setup For This Employee";
	    }

	   /* try {
	    	Map<String, Object> EmplPayrollHistorIn = FastMap.newInstance();
	    	EmplPayrollHistorIn.put("partyId", partyId);
	    	//EmplPayrollHistorIn.put("companyId", "");
	    	EmplPayrollHistorIn.put("payrollItemTypeId", payrollItemTypeId);
	    	EmplPayrollHistorIn.put("calculationType", "B");
	    	EmplPayrollHistorIn.put("calculationMode", "Fixed");
	    	EmplPayrollHistorIn.put("optValue", optValueforBasic);
	    	EmplPayrollHistorIn.put("amount", levelAmount);
	    	EmplPayrollHistorIn.put("comments", "");
	    	EmplPayrollHistorIn.put("fromDate", currentDatetime);
	    	EmplPayrollHistorIn.put("emplStatusId", "EMPL_POS_ACTIVE");
			GenericValue emplPayrollHistorInSetup = delegator.makeValue("EmplPayrollHistory",UtilMisc.toMap(EmplPayrollHistorIn));
			emplPayrollHistorInSetup.create();
	    } catch (Exception e) { }*/
	    
	    List<EntityExpr> exprsForGradeItem = FastList.newInstance();
	    exprsForGradeItem.add(EntityCondition.makeCondition("salaryTemplateId", EntityOperator.EQUALS, salaryTemplateId));
	    exprsForGradeItem.add(EntityCondition.makeCondition("isActive", EntityOperator.EQUALS, "Y"));
		List<GenericValue> gradeItemList = null;
		try { 
			gradeItemList = delegator.findList("SalaryTemplateDetails", EntityCondition.makeCondition(exprsForGradeItem, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		int count=0;
		for (GenericValue aParsed : gradeItemList){
			//String epssalaryTemplateId = gradeItemList.get(count).get("salaryTemplateId").toString();
			String epsPayrollItemTypeId = gradeItemList.get(count).get("payrollItemTypeId").toString();
			String epsCalculationType = gradeItemList.get(count).get("calculationType").toString();
			String epsCalculationMode = gradeItemList.get(count).get("calculationMode").toString();
			String epsOptValue = gradeItemList.get(count).get("optValue").toString();
			String epsComments = "";
	        if (null != aParsed.get("comments")  ) {
	        	epsComments = aParsed.get("comments").toString();
	        }
			BigDecimal epsAmount= (BigDecimal)gradeItemList.get(count).get("amount");
			if(epsCalculationMode.equals("Percentage")){
				float floatLevelAmount=levelAmount.floatValue();
				float floatOptValue= Float.parseFloat(epsOptValue);				
				float floatEpsAmount =  floatLevelAmount * floatOptValue / 100;
				epsAmount=BigDecimal.valueOf(floatEpsAmount);
			}
			else{
				float floatOptValue= Float.parseFloat(epsOptValue);	
				epsAmount=BigDecimal.valueOf(floatOptValue);	
			}
			//----------------- EmplPresendSalary table data entry ---------------------//
        	Map<String, Object> EmployeePresentGradeItemIn = FastMap.newInstance();
        	EmployeePresentGradeItemIn.put("partyId", partyId);
        	EmployeePresentGradeItemIn.put("companyId", "");
        	EmployeePresentGradeItemIn.put("payrollItemTypeId", epsPayrollItemTypeId);
        	EmployeePresentGradeItemIn.put("calculationType", epsCalculationType);
        	EmployeePresentGradeItemIn.put("calculationMode", epsCalculationMode);
        	EmployeePresentGradeItemIn.put("optValue", epsOptValue);
        	EmployeePresentGradeItemIn.put("amount", epsAmount);
        	EmployeePresentGradeItemIn.put("comments", epsComments);
        	EmployeePresentGradeItemIn.put("fromDate", currentDatetime);
        	EmployeePresentGradeItemIn.put("emplStatusId", "EMPL_POS_ACTIVE");
    	    try {
    			GenericValue employeeSalaryItemSetup = delegator
    				.makeValue("EmplPresentSalary",
    						UtilMisc.toMap(EmployeePresentGradeItemIn));
    			employeeSalaryItemSetup.create();
    	    } catch (Exception e) { 
    	    }
    	    
    	      
    	/*  //----------------- EmplPayrollHistory table data entry ---------------------//
        	Map<String, Object> EmplPayrollHistoryGradeItemIn = FastMap.newInstance();
        	EmplPayrollHistoryGradeItemIn.put("partyId", partyId);
        	//EmplPayrollHistoryGradeItemIn.put("companyId", "");
        	EmplPayrollHistoryGradeItemIn.put("payrollItemTypeId", epsPayrollItemTypeId);
        	EmplPayrollHistoryGradeItemIn.put("calculationType", epsCalculationType);
        	EmplPayrollHistoryGradeItemIn.put("calculationMode", epsCalculationMode);
        	EmplPayrollHistoryGradeItemIn.put("optValue", epsOptValue);
        	EmplPayrollHistoryGradeItemIn.put("amount", epsAmount);
        	EmplPayrollHistoryGradeItemIn.put("comments", epsComments);
        	EmplPayrollHistoryGradeItemIn.put("fromDate", currentDatetime);
    	    try {
    			GenericValue employeePayrollHistorySalaryItemSetup = delegator
    				.makeValue("EmplPayrollHistory",
    						UtilMisc.toMap(EmplPayrollHistoryGradeItemIn));
    			employeePayrollHistorySalaryItemSetup.create();
    	    } catch (Exception e) { }*/
		    count++;
		}
		
		///---------------------------- PersonPayrollInfo Setup-----------------------//   
		String taxPayerTypeId="9001";
	    GenericValue personPayrollInfo = null;
		try { 
			personPayrollInfo = delegator.findByPrimaryKey("PersonPayrollInfo", UtilMisc.toMap("partyId", partyId));
			
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
    	Map<String, Object> PersonPayrollInfoIn = FastMap.newInstance();
    	PersonPayrollInfoIn.put("partyId", partyId);
    	PersonPayrollInfoIn.put("employeeId", employeeID);
    	PersonPayrollInfoIn.put("salaryTemplateId", salaryTemplateId);
    	PersonPayrollInfoIn.put("basicSalaryStepId", basicSalaryStepId);
    	PersonPayrollInfoIn.put("taxPayerTypeId", taxPayerTypeId);
    	PersonPayrollInfoIn.put("comments", "");
    	PersonPayrollInfoIn.put("isActive", "Y");
    	PersonPayrollInfoIn.put("dateAdded", currentDatetime);
	    try {
			GenericValue PersonPayrollInfoSetup = delegator
				.makeValue("PersonPayrollInfo",
						UtilMisc.toMap(PersonPayrollInfoIn));
			if(personPayrollInfo == null){
				PersonPayrollInfoSetup.create();
			}
			else{
				taxPayerTypeId=personPayrollInfo.getString(taxPayerTypeId);
		    	PersonPayrollInfoIn.put("taxPayerTypeId", taxPayerTypeId);
				PersonPayrollInfoSetup.store();
			}
			// ---------------------- History Data Added ---------------------------- //
	    	Map<String, Object> PersonPayrollInfoHisIn = FastMap.newInstance();
	    	PersonPayrollInfoHisIn.put("partyId", partyId);
	    	PersonPayrollInfoHisIn.put("employeeId", employeeID);
	    	PersonPayrollInfoHisIn.put("salaryTemplateId", salaryTemplateId);
	    	PersonPayrollInfoHisIn.put("basicSalaryStepId", basicSalaryStepId);
	    	PersonPayrollInfoHisIn.put("taxPayerTypeId", taxPayerTypeId);
	    	PersonPayrollInfoHisIn.put("comments", "");
	    	PersonPayrollInfoHisIn.put("isActive", "Y");
	    	PersonPayrollInfoHisIn.put("dateAdded", currentDatetime);
	    	 try {
	 			GenericValue PersonPayrollInfoHisSetup = delegator
	 				.makeValue("PersonPayrollInfoHis",
	 						UtilMisc.toMap(PersonPayrollInfoHisIn));
	 			PersonPayrollInfoHisSetup.create();
	    	 }
	    	 catch(Exception e){}
	    } catch (Exception e) { }

		
		
	    request.setAttribute("partyId", partyId);
	    request.setAttribute("_EVENT_MESSAGE_", "Salary Added Successfully");
		
	    return "Salary Added Successfully";
	}    

    public static String findPartyIdByEmployeeId(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();

        String employeeID = "";
        if (null != request.getParameter("employeeId")) {
        	employeeID = request.getParameter("employeeId");
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
        return "success";
    }

    public static String updateEmpPresSalItem(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();

        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
        String partyId = "";
        if (null != request.getParameter("partyId")) {
        	partyId = request.getParameter("partyId");
        }

		String epsPayrollItemTypeId = request.getParameter("payrollItemTypeId");
		String epsCalculationType =request.getParameter("calculationType");
		String epsCalculationMode = request.getParameter("calculationMode");
		String epsOptValue = request.getParameter("optValue");
		String epsComments = request.getParameter("comments");
		BigDecimal epsAmount=null;
		BigDecimal levelAmount=null;
        if(epsPayrollItemTypeId.equals("BASIC")){
			float floatOptValue= Float.parseFloat(epsOptValue);	
			epsAmount=BigDecimal.valueOf(floatOptValue);	
			levelAmount =epsAmount;
        }
        else{
		    //------------------- BASIC Salary Find--------------------//
		    List<EntityExpr> exprsForBasicSalary = FastList.newInstance();
		    exprsForBasicSalary.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)); 
		    exprsForBasicSalary.add(EntityCondition.makeCondition("payrollItemTypeId", EntityOperator.EQUALS, "BASIC"));
			List<GenericValue> basicSalarylList = null;
			try {
				basicSalarylList = delegator.findList("EmplPresentSalary", EntityCondition.makeCondition(exprsForBasicSalary, EntityOperator.AND), null, null, null, false);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			levelAmount= (BigDecimal)basicSalarylList.get(0).get("amount");
			String optValueforBasic = levelAmount.toString();
			
			if(epsCalculationMode.equals("Percentage")){
				float floatLevelAmount=levelAmount.floatValue();
				float floatOptValue= Float.parseFloat(epsOptValue);				
				float floatEpsAmount =  floatLevelAmount * floatOptValue / 100;
				epsAmount=BigDecimal.valueOf(floatEpsAmount);
			}
			else{
				float floatOptValue= Float.parseFloat(epsOptValue);	
				epsAmount=BigDecimal.valueOf(floatOptValue);	
			}
        }
		//----------------- EmplPresendSalary table data entry ---------------------//
    	Map<String, Object> EmployeePresentGradeItemIn = FastMap.newInstance();
    	EmployeePresentGradeItemIn.put("partyId", partyId);
    	EmployeePresentGradeItemIn.put("companyId", "");
    	EmployeePresentGradeItemIn.put("payrollItemTypeId", epsPayrollItemTypeId);
    	EmployeePresentGradeItemIn.put("calculationType", epsCalculationType);
    	EmployeePresentGradeItemIn.put("calculationMode", epsCalculationMode);
    	EmployeePresentGradeItemIn.put("optValue", epsOptValue);
    	EmployeePresentGradeItemIn.put("amount", epsAmount);
    	EmployeePresentGradeItemIn.put("comments", epsComments);
    	EmployeePresentGradeItemIn.put("fromDate", currentDatetime);
    	EmployeePresentGradeItemIn.put("emplStatusId", "EMPL_POS_ACTIVE");
	    try {
			GenericValue employeeSalaryItemSetup = delegator
				.makeValue("EmplPresentSalary",
						UtilMisc.toMap(EmployeePresentGradeItemIn));
			employeeSalaryItemSetup.store();
	    } catch (Exception e) { }
	    
       if(epsPayrollItemTypeId.equals("BASIC")){
    	    List<EntityExpr> exprsForEmplPreSentSal = FastList.newInstance();
    	    exprsForEmplPreSentSal.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)); 
    	    exprsForEmplPreSentSal.add(EntityCondition.makeCondition("calculationMode", EntityOperator.EQUALS, "Percentage")); 
	   		List<GenericValue> emplPresentSallList = null;
	   		try {
	   			emplPresentSallList = delegator.findList("EmplPresentSalary", EntityCondition.makeCondition(exprsForEmplPreSentSal, EntityOperator.AND), null, null, null, false);
	   		} catch (GenericEntityException e) {
	   			e.printStackTrace();
	   		}

			int count=0;
			for (Map aParsed : emplPresentSallList){
				String epsItemPayrollItemTypeId = emplPresentSallList.get(count).get("payrollItemTypeId").toString();
				String epsItemCalculationType = emplPresentSallList.get(count).get("calculationType").toString();
				String epsItemCalculationMode = emplPresentSallList.get(count).get("calculationMode").toString();
				String epsItemOptValue = emplPresentSallList.get(count).get("optValue").toString();
				String epsItemComments = emplPresentSallList.get(count).get("comments").toString();
				BigDecimal epsItemAmount= (BigDecimal)emplPresentSallList.get(count).get("amount");
				if(epsItemCalculationMode.equals("Percentage")){
					float floatLevelAmount=levelAmount.floatValue();
					float floatOptValue= Float.parseFloat(epsItemOptValue);				
					float floatEpsAmount =  floatLevelAmount * floatOptValue / 100;
					epsItemAmount=BigDecimal.valueOf(floatEpsAmount);
				}
				else{
					float floatOptValue= Float.parseFloat(epsItemOptValue);	
					epsItemAmount=BigDecimal.valueOf(floatOptValue);	
				}
				//----------------- EmplPresendSalary table data entry ---------------------//
	        	Map<String, Object> EmployeePresentSalaryItemIn = FastMap.newInstance();
	        	EmployeePresentSalaryItemIn.put("partyId", partyId);
	        	EmployeePresentSalaryItemIn.put("companyId", "");
	        	EmployeePresentSalaryItemIn.put("payrollItemTypeId", epsItemPayrollItemTypeId);
	        	EmployeePresentSalaryItemIn.put("calculationType", epsItemCalculationType);
	        	EmployeePresentSalaryItemIn.put("calculationMode", epsItemCalculationMode);
	        	EmployeePresentSalaryItemIn.put("optValue", epsItemOptValue);
	        	EmployeePresentSalaryItemIn.put("amount", epsItemAmount);
	        	EmployeePresentSalaryItemIn.put("comments", epsComments);
	        	EmployeePresentSalaryItemIn.put("fromDate", currentDatetime);
	        	EmployeePresentSalaryItemIn.put("emplStatusId", "EMPL_POS_ACTIVE");
	    	    try {
	    			GenericValue employeeSalaryItemSetup = delegator
	    				.makeValue("EmplPresentSalary",
	    						UtilMisc.toMap(EmployeePresentSalaryItemIn));
	    			employeeSalaryItemSetup.store();
	    	    } catch (Exception e) { }
	    	    
	    	  //----------------- EmplPayrollHistory table data entry ---------------------//
	        	/*Map<String, Object> EmplPayrollHistoryGradeItemIn = FastMap.newInstance();
	        	EmplPayrollHistoryGradeItemIn.put("partyId", partyId);
	        	//EmplPayrollHistoryGradeItemIn.put("companyId", "");
	        	EmplPayrollHistoryGradeItemIn.put("payrollItemTypeId", epsPayrollItemTypeId);
	        	EmplPayrollHistoryGradeItemIn.put("calculationType", epsCalculationType);
	        	EmplPayrollHistoryGradeItemIn.put("calculationMode", epsCalculationMode);
	        	EmplPayrollHistoryGradeItemIn.put("optValue", epsOptValue);
	        	EmplPayrollHistoryGradeItemIn.put("amount", epsAmount);
	        	EmplPayrollHistoryGradeItemIn.put("comments", epsComments);
	        	EmplPayrollHistoryGradeItemIn.put("fromDate", currentDatetime);
	    	    try {
	    			GenericValue employeePayrollHistorySalaryItemSetup = delegator
	    				.makeValue("EmplPayrollHistory",
	    						UtilMisc.toMap(EmplPayrollHistoryGradeItemIn));
	    			employeePayrollHistorySalaryItemSetup.create();
	    	    } catch (Exception e) { }*/
			    count++;
			}
       }
	    
	    
	    
        request.setAttribute("partyId", partyId);
        return "success";
    }

    public static String CreateSalaryAdjustment(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();

        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());

        String employeeID = "";
        if (null != request.getParameter("employeeId")) {
        	employeeID = request.getParameter("employeeId");
        }
        else{
	         request.setAttribute("_ERROR_MESSAGE_", "Please Select Employee For Adjustment");
	         return "error";
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
		String epsPayrollItemTypeId = request.getParameter("payrollItemTypeId");
        if (employeeID.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Employee can not be empty");
            return "error";
        }
        
		String epsCalculationType = "";
        if (null != request.getParameter("calculationType")) {
        	epsCalculationType = request.getParameter("calculationType");
        }
        if (epsCalculationType.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Please select calculation type");
            return "error";
        }
		String epsCalculationMode = "";
        if (null != request.getParameter("calculationMode")) {
        	epsCalculationMode = request.getParameter("calculationMode");
        }
        if (epsCalculationMode.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Please select calculation mode");
            return "error";
        }
		String epsOptValue = "";
        if (null != request.getParameter("optValue")) {
        	epsOptValue = request.getParameter("optValue");
        }
        if (epsOptValue.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Please select value");
            return "error";
        }
		String epsComments = request.getParameter("comments");
		BigDecimal epsAmount=null;
		BigDecimal levelAmount=null;
        if(epsPayrollItemTypeId.equals("BASIC")){
			float floatOptValue= Float.parseFloat(epsOptValue);	
			epsAmount=BigDecimal.valueOf(floatOptValue);	
			levelAmount =epsAmount;
        }
        else{
		    //------------------- BASIC Salary Find--------------------//
		    List<EntityExpr> exprsForBasicSalary = FastList.newInstance();
		    exprsForBasicSalary.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)); 
		    exprsForBasicSalary.add(EntityCondition.makeCondition("payrollItemTypeId", EntityOperator.EQUALS, "BASIC"));
			List<GenericValue> basicSalarylList = null;
			try {
				basicSalarylList = delegator.findList("EmplPresentSalary", EntityCondition.makeCondition(exprsForBasicSalary, EntityOperator.AND), null, null, null, false);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			levelAmount= (BigDecimal)basicSalarylList.get(0).get("amount");
			String optValueforBasic = levelAmount.toString();
			
			if(epsCalculationMode.equals("Percentage")){
				float floatLevelAmount=levelAmount.floatValue();
				float floatOptValue= Float.parseFloat(epsOptValue);				
				float floatEpsAmount =  floatLevelAmount * floatOptValue / 100;
				epsAmount=BigDecimal.valueOf(floatEpsAmount);
			}
			else{
				float floatOptValue= Float.parseFloat(epsOptValue);	
				epsAmount=BigDecimal.valueOf(floatOptValue);	
			}
        }

		//----------------- EmplPresendSalary table data entry ---------------------//
    	Map<String, Object> EmployeePresentGradeItemIn = FastMap.newInstance();
    	EmployeePresentGradeItemIn.put("partyId", partyId);
    	EmployeePresentGradeItemIn.put("companyId", "");
    	EmployeePresentGradeItemIn.put("payrollItemTypeId", epsPayrollItemTypeId);
    	EmployeePresentGradeItemIn.put("calculationType", epsCalculationType);
    	EmployeePresentGradeItemIn.put("calculationMode", epsCalculationMode);
    	EmployeePresentGradeItemIn.put("optValue", epsOptValue);
    	EmployeePresentGradeItemIn.put("amount", epsAmount);
    	EmployeePresentGradeItemIn.put("comments", epsComments);
    	EmployeePresentGradeItemIn.put("fromDate", currentDatetime);
    	EmployeePresentGradeItemIn.put("emplStatusId", "EMPL_POS_ACTIVE");
	    try {
			GenericValue employeeSalaryItemSetup = delegator
				.makeValue("EmplPresentSalary",
						UtilMisc.toMap(EmployeePresentGradeItemIn));
			//------------------Check Payroll Item Existence-----------------//
			Map<String, Object> findPayrollIteminPersentSalaryMap = UtilMisc.<String, Object>toMap("partyId", partyId, "payrollItemTypeId", epsPayrollItemTypeId);
            GenericValue checkPayrollIteminPersentSalary=delegator.findByPrimaryKey("EmplPresentSalary", findPayrollIteminPersentSalaryMap);
            if (checkPayrollIteminPersentSalary == null) {
    			employeeSalaryItemSetup.create();
    		    request.setAttribute("_EVENT_MESSAGE_", "Added payroll item "+epsPayrollItemTypeId+" = "+epsAmount+" for employee "+employeeID +" successfully");    			
            }
            else{
    			employeeSalaryItemSetup.store();
    		    request.setAttribute("_EVENT_MESSAGE_", "Update payroll item "+epsPayrollItemTypeId+" = "+epsAmount+" for employee "+employeeID+" successfully"); 
            }			
	    } catch (Exception e) { }

        request.setAttribute("partyId", partyId);
        return "success";
    }
}