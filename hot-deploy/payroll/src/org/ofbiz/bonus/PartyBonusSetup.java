package org.ofbiz.bonus;

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
import org.ofbiz.base.util.UtilHttp;
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


import java.sql.*;

public class PartyBonusSetup {
	 public static String createPartyBonusSetup(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
	        
	        

	        String employeeID = "";
	        if (null != request.getParameter("employeeId")) {
	        	employeeID = request.getParameter("employeeId");
	        }
	        if (employeeID.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Employee can not be empty");
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
	        String bonusId="";
	        String month ="";
	        String year="";
	        if (null != request.getParameter("bonusId")) {
	        	bonusId = request.getParameter("bonusId");
	        }

	        if (bonusId.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Bonus can not be empty");
	            return "error";
	        }
	      //------------ Find Employee Basic Salary--------------------------//
	        String basicSalaryStr="";
			BigDecimal basicSalary=new BigDecimal(0);
	 	    List<EntityExpr> exprsForBasic = FastList.newInstance();
	 	    exprsForBasic.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)); 
	 	    exprsForBasic.add(EntityCondition.makeCondition("payrollItemTypeId", EntityOperator.EQUALS, "BASIC"));        
			List<GenericValue> presentSalaryListForBasic = null;		
			
			try {
				presentSalaryListForBasic = delegator.findList("EmplPresentSalary", EntityCondition.makeCondition(exprsForBasic, EntityOperator.AND), null, null, null, false);
				for (Map aParsed : presentSalaryListForBasic){	
					basicSalaryStr=presentSalaryListForBasic.get(0).get("amount").toString();
					basicSalary=new BigDecimal(basicSalaryStr);
				}
			} 
			catch (GenericEntityException e) {
				e.printStackTrace();
			}
			//-----------------------------------------------------------------------//
			
			 //------------ Find Employee Basic Salary--------------------------//
	        String bonusAmountStr="";
	        String bonusCalculationMode="";
			BigDecimal bonusAmount=new BigDecimal(0);
	 	    List<EntityExpr> exprsForBonus = FastList.newInstance();
	 	   exprsForBonus.add(EntityCondition.makeCondition("bonusId", EntityOperator.EQUALS, bonusId)); 
	 	    List<GenericValue> bonusInfo = null;		
			
			try {
				bonusInfo = delegator.findList("BonusType", EntityCondition.makeCondition(exprsForBonus, EntityOperator.AND), null, null, null, false);
				for (Map aParsed : bonusInfo){	
					bonusAmountStr=bonusInfo.get(0).get("bonusValue").toString();
					bonusAmount=new BigDecimal(bonusAmountStr);
					bonusCalculationMode=bonusInfo.get(0).get("calculationMode").toString();
				}
			} 
			catch (GenericEntityException e) {
				e.printStackTrace();
			}
			//-----------------------------------------------------------------------//
			//---------------------Bonus Amount Calculation--------------------------//

	        String amountStr="";
			BigDecimal amount=new BigDecimal(0);
			if(bonusCalculationMode.equals("Percentage")){

		        if (basicSalaryStr.equals("")) {
		            request.setAttribute("_ERROR_MESSAGE_", "Employee basic salary can not be set");
		            return "error";
		        }
				float floatAmount= Float.parseFloat(bonusAmountStr);	
				float floatBasicAmount= Float.parseFloat(basicSalaryStr);				
				float floatEpsAmount =  floatBasicAmount * floatAmount / 100;
				amount=BigDecimal.valueOf(floatEpsAmount);
			}
			else{
				float floatOptValue= Float.parseFloat(bonusAmountStr);	
				amount=BigDecimal.valueOf(floatOptValue);	
			}
			
			//--------------------------------------------------------------------------//
			
	        if (null != request.getParameter("monthNumber")) {
	        	month = request.getParameter("monthNumber");
	        }
	        if (month.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Month can not be empty");
	            return "error";
	        }
	        if (null != request.getParameter("bonusYear")) {
	        	year = request.getParameter("bonusYear");
	        }
	        if (year.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Year can not be empty");
	            return "error";
	        }
			BigDecimal amountDec=new BigDecimal(0);
			BigDecimal selfProFundPercentFloat=new BigDecimal(0);
			try{
				amountDec=new BigDecimal(amountStr);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			String comments = request.getParameter("comments");
			Map<String, Object> partyBonusIn = FastMap.newInstance();
			partyBonusIn.put("partyId", partyId);
    	    partyBonusIn.put("employeeId", employeeID);
			partyBonusIn.put("bonusId", bonusId);
			partyBonusIn.put("bonusMonth", month);
			partyBonusIn.put("bonusYear", year);
			partyBonusIn.put("amount", amount);
			partyBonusIn.put("comments", comments);
			partyBonusIn.put("isActive", "Y");
			partyBonusIn.put("dateAdded", currentDatetime);
    	    try {
    			GenericValue partyBonusInSetup = delegator
    				.makeValue("PartyBonusStatus",
    						UtilMisc.toMap(partyBonusIn));
    			partyBonusInSetup.create();   
    			

        	    Map<String, Object> partyBonusHisIn = FastMap.newInstance();
        	    partyBonusHisIn.put("partyId", partyId);
        	    partyBonusHisIn.put("employeeId", employeeID);
        	    partyBonusHisIn.put("bonusId", bonusId);
        	    partyBonusHisIn.put("bonusMonth", month);
        	    partyBonusHisIn.put("year", year);
        	    partyBonusHisIn.put("amount", amount);
        	    partyBonusHisIn.put("comments", comments);
        	    partyBonusHisIn.put("isActive", "Y");
        	    partyBonusHisIn.put("dateAdded", currentDatetime);
        	    try {
        			GenericValue partyBonusHisInSetup = delegator
        				.makeValue("PartyBonusStatusHis",
        						UtilMisc.toMap(partyBonusHisIn));
        			partyBonusHisInSetup.create();    			
        			
        	    } catch (Exception e) { }
    			
    	    } catch (Exception e) { }

	        request.setAttribute("_EVENT_MESSAGE_", "Party Bonus Setup Successfully");
	        return "success";
	    }
 
	 public static String updatePartyBonusSetup(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
	        
	        
	        String partyId = null;
	        if (null != request.getParameter("partyId")) {
	        	partyId = request.getParameter("partyId");
	        }
	        String bonusId="";
	        String month ="";
	        String year="";
	        String isActive="";
	        if (null != request.getParameter("bonusId")) {
	        	bonusId = request.getParameter("bonusId");
	        }

	        if (bonusId.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Bonus can not be empty");
	            return "error";
	        }
	      //------------ Find Employee Basic Salary--------------------------//
	        String basicSalaryStr="";
			BigDecimal basicSalary=new BigDecimal(0);
	 	    List<EntityExpr> exprsForBasic = FastList.newInstance();
	 	    exprsForBasic.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)); 
	 	    exprsForBasic.add(EntityCondition.makeCondition("payrollItemTypeId", EntityOperator.EQUALS, "BASIC"));        
			List<GenericValue> presentSalaryListForBasic = null;		
			
			try {
				presentSalaryListForBasic = delegator.findList("EmplPresentSalary", EntityCondition.makeCondition(exprsForBasic, EntityOperator.AND), null, null, null, false);
				for (Map aParsed : presentSalaryListForBasic){	
					basicSalaryStr=presentSalaryListForBasic.get(0).get("amount").toString();
					basicSalary=new BigDecimal(basicSalaryStr);
				}
			} 
			catch (GenericEntityException e) {
				e.printStackTrace();
			}
			//-----------------------------------------------------------------------//
			
			 //------------ Find Employee Basic Salary--------------------------//
	        String bonusAmountStr="";
	        String bonusCalculationMode="";
			BigDecimal bonusAmount=new BigDecimal(0);
	 	    List<EntityExpr> exprsForBonus = FastList.newInstance();
	 	   exprsForBonus.add(EntityCondition.makeCondition("bonusId", EntityOperator.EQUALS, bonusId)); 
	 	    List<GenericValue> bonusInfo = null;		
			
			try {
				bonusInfo = delegator.findList("BonusType", EntityCondition.makeCondition(exprsForBonus, EntityOperator.AND), null, null, null, false);
				for (Map aParsed : bonusInfo){	
					bonusAmountStr=bonusInfo.get(0).get("bonusValue").toString();
					bonusAmount=new BigDecimal(bonusAmountStr);
					bonusCalculationMode=bonusInfo.get(0).get("calculationMode").toString();
				}
			} 
			catch (GenericEntityException e) {
				e.printStackTrace();
			}
			//-----------------------------------------------------------------------//
			//---------------------Bonus Amount Calculation--------------------------//

	        String amountStr="";
			BigDecimal amount=new BigDecimal(0);
			if(bonusCalculationMode.equals("Percentage")){

		        if (basicSalaryStr.equals("")) {
		            request.setAttribute("_ERROR_MESSAGE_", "Employee basic salary can not be set");
		            return "error";
		        }
				float floatAmount= Float.parseFloat(bonusAmountStr);	
				float floatBasicAmount= Float.parseFloat(basicSalaryStr);				
				float floatEpsAmount =  floatBasicAmount * floatAmount / 100;
				amount=BigDecimal.valueOf(floatEpsAmount);
			}
			else{
				float floatOptValue= Float.parseFloat(bonusAmountStr);	
				amount=BigDecimal.valueOf(floatOptValue);	
			}
			
			//--------------------------------------------------------------------------//
			
	        if (null != request.getParameter("monthNumber")) {
	        	month = request.getParameter("monthNumber");
	        }
	        if (month.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Month can not be empty");
	            return "error";
	        }
	        if (null != request.getParameter("bonusYear")) {
	        	year = request.getParameter("bonusYear");
	        }
	        if (year.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Year can not be empty");
	            return "error";
	        }
	        if (null != request.getParameter("isActive")) {
	        	isActive = request.getParameter("isActive");
	        }
	        if (isActive.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "IsActive can not be empty");
	            return "error";
	        }
	        String dateAddedStr = request.getParameter("dateAdded");
	        Timestamp dateAdded = Timestamp.valueOf(dateAddedStr);
			BigDecimal amountDec=new BigDecimal(0);
			BigDecimal selfProFundPercentFloat=new BigDecimal(0);
			try{
				amountDec=new BigDecimal(amountStr);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			String comments = request.getParameter("comments");
			String employeeId= request.getParameter("employeeId");
			
			Map<String, Object> partyBonusIn = FastMap.newInstance();
			partyBonusIn.put("partyId", partyId);
    	    partyBonusIn.put("employeeId", employeeId);
			partyBonusIn.put("bonusId", bonusId);
			partyBonusIn.put("bonusMonth", month);
			partyBonusIn.put("bonusYear", year);
			partyBonusIn.put("amount", amount);
			partyBonusIn.put("comments", comments);
			partyBonusIn.put("isActive", isActive);
			partyBonusIn.put("dateAdded", dateAdded);
    	    try {
    			GenericValue partyBonusInSetup = delegator
    				.makeValue("PartyBonusStatus",
    						UtilMisc.toMap(partyBonusIn));
    			partyBonusInSetup.store();    			

        	    Map<String, Object> partyBonusHisIn = FastMap.newInstance();
        	    partyBonusHisIn.put("partyId", partyId);
        	    partyBonusHisIn.put("employeeId", employeeId);
        	    partyBonusHisIn.put("bonusId", bonusId);
        	    partyBonusHisIn.put("bonusMonth", month);
        	    partyBonusHisIn.put("bonusYear", year);
        	    partyBonusHisIn.put("amount", amount);
        	    partyBonusHisIn.put("comments", comments);
        	    partyBonusHisIn.put("isActive", isActive);
        	    partyBonusHisIn.put("dateAdded", currentDatetime);
        	    try {
        			GenericValue partyBonusHisInSetup = delegator
        				.makeValue("PartyBonusStatusHis",
        						UtilMisc.toMap(partyBonusHisIn));
        			partyBonusHisInSetup.create();    			
        			
        	    } catch (Exception e) { }
    	    } catch (Exception e) { }

			
	        request.setAttribute("_EVENT_MESSAGE_", "Party Bonus Setup Successfully");
	        return "success";
	    }

	 public static String processPartyBonus(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
	        
	        
	        String partyId = null;
	        if (null != request.getParameter("partyId")) {
	        	partyId = request.getParameter("partyId");
	        }
	        String bonusId="";
	        String month ="";
	        String year="";
	        String isActive="";
	        if (null != request.getParameter("bonusId")) {
	        	bonusId = request.getParameter("bonusId");
	        }

	        if (bonusId.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Bonus can not be empty");
	            return "error";
	        }
	      //------------ Find Employee Basic Salary--------------------------//
	        String basicSalaryStr="";
			BigDecimal basicSalary=new BigDecimal(0);
	 	    List<EntityExpr> exprsForBasic = FastList.newInstance();
	 	    exprsForBasic.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)); 
	 	    exprsForBasic.add(EntityCondition.makeCondition("payrollItemTypeId", EntityOperator.EQUALS, "BASIC"));        
			List<GenericValue> presentSalaryListForBasic = null;		
			
			try {
				presentSalaryListForBasic = delegator.findList("EmplPresentSalary", EntityCondition.makeCondition(exprsForBasic, EntityOperator.AND), null, null, null, false);
				for (Map aParsed : presentSalaryListForBasic){	
					basicSalaryStr=presentSalaryListForBasic.get(0).get("amount").toString();
					basicSalary=new BigDecimal(basicSalaryStr);
				}
			} 
			catch (GenericEntityException e) {
				e.printStackTrace();
			}
			//-----------------------------------------------------------------------//
			
			 //------------ Find Employee Basic Salary--------------------------//
	        String bonusAmountStr="";
	        String bonusCalculationMode="";
			BigDecimal bonusAmount=new BigDecimal(0);
	 	    List<EntityExpr> exprsForBonus = FastList.newInstance();
	 	   exprsForBonus.add(EntityCondition.makeCondition("bonusId", EntityOperator.EQUALS, bonusId)); 
	 	    List<GenericValue> bonusInfo = null;		
			
			try {
				bonusInfo = delegator.findList("BonusType", EntityCondition.makeCondition(exprsForBonus, EntityOperator.AND), null, null, null, false);
				for (Map aParsed : bonusInfo){	
					bonusAmountStr=bonusInfo.get(0).get("bonusValue").toString();
					bonusAmount=new BigDecimal(bonusAmountStr);
					bonusCalculationMode=bonusInfo.get(0).get("calculationMode").toString();
				}
			} 
			catch (GenericEntityException e) {
				e.printStackTrace();
			}
			//-----------------------------------------------------------------------//
			//---------------------Bonus Amount Calculation--------------------------//

	        String amountStr="";
			BigDecimal amount=new BigDecimal(0);
			if(bonusCalculationMode.equals("Percentage")){

		        if (basicSalaryStr.equals("")) {
		            request.setAttribute("_ERROR_MESSAGE_", "Employee basic salary can not be set");
		            return "error";
		        }
				float floatAmount= Float.parseFloat(bonusAmountStr);	
				float floatBasicAmount= Float.parseFloat(basicSalaryStr);				
				float floatEpsAmount =  floatBasicAmount * floatAmount / 100;
				amount=BigDecimal.valueOf(floatEpsAmount);
			}
			else{
				float floatOptValue= Float.parseFloat(bonusAmountStr);	
				amount=BigDecimal.valueOf(floatOptValue);	
			}
			
			//--------------------------------------------------------------------------//
			
	        if (null != request.getParameter("bonusMonth")) {
	        	month = request.getParameter("bonusMonth");
	        }
	        if (month.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Month can not be empty");
	            return "error";
	        }
	        if (null != request.getParameter("bonusYear")) {
	        	year = request.getParameter("bonusYear");
	        }
	        if (year.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Year can not be empty");
	            return "error";
	        }
	        if (null != request.getParameter("isActive")) {
	        	isActive = request.getParameter("isActive");
	        }
	        if (isActive.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "IsActive can not be empty");
	            return "error";
	        }
	        String dateAddedStr = request.getParameter("dateAdded");
	        Timestamp dateAdded = Timestamp.valueOf(dateAddedStr);
			BigDecimal amountDec=new BigDecimal(0);
			BigDecimal selfProFundPercentFloat=new BigDecimal(0);
			try{
				amountDec=new BigDecimal(amountStr);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			String comments = request.getParameter("comments");
			String employeeId= request.getParameter("employeeId");
			
			Map<String, Object> partyBonusInfoIn = FastMap.newInstance();
			partyBonusInfoIn.put("partyId", partyId);
			partyBonusInfoIn.put("employeeId", employeeId);
			partyBonusInfoIn.put("bonusId", bonusId);
			partyBonusInfoIn.put("bonusMonth", month);
			partyBonusInfoIn.put("bonusYear", year);
			partyBonusInfoIn.put("amount", amount);
			partyBonusInfoIn.put("comments", comments);
			partyBonusInfoIn.put("isActive", isActive);
			partyBonusInfoIn.put("dateAdded", dateAdded);
    	    try {
    			GenericValue partyBonusInfoInSetup = delegator
    				.makeValue("PartyBonusPayInfo",
    						UtilMisc.toMap(partyBonusInfoIn));
    			partyBonusInfoInSetup.create();    			

    			Map<String, Object> partyBonusIn = FastMap.newInstance();
    			partyBonusIn.put("partyId", partyId);
        	    partyBonusIn.put("employeeId", employeeId);
    			partyBonusIn.put("bonusId", bonusId);
    			partyBonusIn.put("bonusYear", month);
    			partyBonusIn.put("bonusYear", year);
    			partyBonusIn.put("amount", amount);
    			partyBonusIn.put("comments", comments);
    			partyBonusIn.put("isActive", "N");
    			partyBonusIn.put("isProcess", "Y");
    			partyBonusIn.put("dateAdded", dateAdded);
        	    try {
        			GenericValue partyBonusInSetup = delegator
        				.makeValue("PartyBonusStatus",
        						UtilMisc.toMap(partyBonusIn));
        			partyBonusInSetup.store();    			
        			
        	    } catch (Exception e) { } 
        			
        	    Map<String, Object> partyBonusHisIn = FastMap.newInstance();
        	    partyBonusHisIn.put("partyId", partyId);
        	    partyBonusHisIn.put("employeeId", employeeId);
        	    partyBonusHisIn.put("bonusId", bonusId);
        	    partyBonusHisIn.put("bonusMonth", month);
        	    partyBonusHisIn.put("bonusYear", year);
        	    partyBonusHisIn.put("amount", amount);
        	    partyBonusHisIn.put("comments", comments);
        	    partyBonusHisIn.put("isActive", isActive);
        	    partyBonusHisIn.put("dateAdded", currentDatetime);
        	    try {
        			GenericValue partyBonusHisInSetup = delegator
        				.makeValue("PartyBonusStatusHis",
        						UtilMisc.toMap(partyBonusHisIn));
        			partyBonusHisInSetup.create();    			
        			
        	    } catch (Exception e) { }
    	    } catch (Exception e) { }

			
	        request.setAttribute("_EVENT_MESSAGE_", "Party Bonus Setup Successfully");
	        return "success";
	    }
 
	 
}