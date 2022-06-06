package org.ofbiz.bonus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;


public class BonusProcessForAllEmpl {
	 public static String createPartyBonusSetupForAllEmpl(HttpServletRequest request, HttpServletResponse response) 
	 {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
	        List<EntityExpr> exprsForPartyList = FastList.newInstance();
	        exprsForPartyList.add(EntityCondition.makeCondition("employmentType", EntityOperator.NOT_EQUAL, null));        
			List<GenericValue> employeeList = null;
			try {
				employeeList = delegator.findList("Employment", EntityCondition.makeCondition(exprsForPartyList, EntityOperator.AND), null, null, null, false);
			} catch (GenericEntityException e) {
				e.printStackTrace();
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

			 //------------ Find Bonus Type --------------------------//
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
			for (GenericValue employeeInfo : employeeList){
				String partyId=employeeInfo.get("partyIdTo").toString();
				String employeeID="";
		        List<GenericValue> partyList = null;
		        try {
		        	partyList = delegator.findByAnd("UserLogin",
		                    UtilMisc.toMap("partyId", partyId));
		        } catch (GenericEntityException e) {
		            e.printStackTrace();
		        }
		        if (UtilValidate.isNotEmpty(partyList)) {
		            if (UtilValidate.isNotEmpty(partyList.get(0).get("userLoginId"))) {
		            	employeeID = partyList.get(0).get("userLoginId").toString();
		            }
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
				
				//---------------------Bonus Amount Calculation--------------------------//

				if(!basicSalaryStr.equals("")){
			        String amountStr="";
					BigDecimal amount=new BigDecimal(0);
					if(bonusCalculationMode.equals("Percentage")){		
						float floatAmount= Float.parseFloat(bonusAmountStr);	
						float floatBasicAmount= Float.parseFloat(basicSalaryStr);				
						float floatEpsAmount =  floatBasicAmount * floatAmount / 100;
						amount=BigDecimal.valueOf(floatEpsAmount);
					}
					else{
						float floatOptValue= Float.parseFloat(bonusAmountStr);	
						amount=BigDecimal.valueOf(floatOptValue);	
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
			     	    partyBonusHisIn.put("bonusYear", year);
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
				}
				
			}
	        request.setAttribute("_EVENT_MESSAGE_", "Party Bonus Setup Successfully");
	        return "success";
	    }
}