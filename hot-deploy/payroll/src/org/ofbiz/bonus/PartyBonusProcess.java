package org.ofbiz.bonus;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.accountsprocess.AccountSalaryEvents;;


public class PartyBonusProcess {
	 public static String AllEmployeesBonusprocess(HttpServletRequest request, HttpServletResponse response) 
	 {
		  HttpSession session = request.getSession();
	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
	        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	        
	        String partyId = null;
	     
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
	 	    List<EntityExpr> exprsForBonusStatus = FastList.newInstance();
	 	    exprsForBonusStatus.add(EntityCondition.makeCondition("bonusId", EntityOperator.EQUALS, bonusId)); 
	 	    exprsForBonusStatus.add(EntityCondition.makeCondition("bonusMonth", EntityOperator.EQUALS, month));        
	 	    exprsForBonusStatus.add(EntityCondition.makeCondition("bonusYear", EntityOperator.EQUALS, year));        
	 	    exprsForBonusStatus.add(EntityCondition.makeCondition("isActive", EntityOperator.EQUALS, "Y"));        
			List<GenericValue> listForBonusStatus = null;		
			
			try {
				listForBonusStatus = delegator.findList("PartyBonusStatus", EntityCondition.makeCondition(exprsForBonusStatus, EntityOperator.AND), null, null, null, false);
				for (GenericValue aParsed : listForBonusStatus){	
					partyId = aParsed.getString("partyId");
					bonusId = aParsed.getString("bonusId");
					Map<String, Object> partyBonusIn = FastMap.newInstance();
		 			partyBonusIn.put("partyId", partyId);
		 			partyBonusIn.put("bonusId", bonusId);
		 			partyBonusIn.put("dateAdded", aParsed.get("dateAdded"));
		 			partyBonusIn.put("isActive", "N");
		 			partyBonusIn.put("isProcess", "Y");
		 			try {
		     			GenericValue partyBonusInSetup = delegator
		     				.makeValue("PartyBonusStatus",
		     						UtilMisc.toMap(partyBonusIn));
		     			partyBonusInSetup.store();    
		     	    } catch (Exception e) { 
			            request.setAttribute("_ERROR_MESSAGE_", "Error!!!");
			            return "error";
		     	    } 
		     	   
		     		AccountSalaryEvents.indBonusProcessAcctg(delegator,context, userLogin, partyId, bonusId, month, year, aParsed.getBigDecimal("amount"));
				}
			} 
			catch (GenericEntityException e) {
				e.printStackTrace();
	            request.setAttribute("_ERROR_MESSAGE_", "Error!!!");
	            return "error";

			}
			
	        request.setAttribute("_EVENT_MESSAGE_", "Bonus Procced Successfully");
	        return "success";
	    }
}