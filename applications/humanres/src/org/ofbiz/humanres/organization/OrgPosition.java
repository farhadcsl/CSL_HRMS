package org.ofbiz.humanres.organization;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;



import java.math.BigDecimal;

/**
 * Created for add position under organization tab
 *
 */
public class OrgPosition {
	private static Logger logger=Logger.getLogger("OrgPosition");
	public static String addOrgPosition(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");

		String newEmplPositionTypeDescription = request.getParameter("emplPositionTypeId");
		String parentEmplPositionId = request.getParameter("emplPositionId");
		String partyId = request.getParameter("orgPartyId");
		String newEmplPositionId = null;
		//String parentEmplPostionId = null;
		String newEmplPositionTypeId = null;
		//first create a new position
		if (UtilValidate.isNotEmpty(newEmplPositionTypeDescription)) {
			List<GenericValue>  emplPosiotnTypeList = null;
			//get position type id from description
			try {
				emplPosiotnTypeList = delegator.findByAnd("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", newEmplPositionTypeDescription));
			} catch (GenericEntityException e1) {
				
				e1.printStackTrace();
			}
			GenericValue emplPosiotnType = EntityUtil.getFirst(emplPosiotnTypeList);
			newEmplPositionTypeId = (String) emplPosiotnType.get("emplPositionTypeId");
			if(UtilValidate.isEmpty(newEmplPositionTypeId)){
				request.setAttribute("_ERROR_MESSAGE_", "parent empl position type not found");
	            return "error";
			}
			//check already the positon is exist or not
			try {
				List<GenericValue> emplPostions = delegator.findByAnd("EmplPosition", UtilMisc.toMap("partyId",partyId,"emplPositionTypeId",newEmplPositionTypeId));
				 GenericValue pos = EntityUtil.getFirst(emplPostions);
				 if(UtilValidate.isNotEmpty(pos)){
					 request.setAttribute("_ERROR_MESSAGE_", "This position is already exist in this group");
			            return "error";
				 }
			} catch (GenericEntityException e1) {
				
				e1.printStackTrace();
			}
			
			//create position
			 Map<String, Object> emplPositionMap = FastMap.newInstance();
			 newEmplPositionId = delegator.getNextSeqId("EmplPosition");
			 emplPositionMap.put("emplPositionId", newEmplPositionId);
			 emplPositionMap.put("emplPositionTypeId", newEmplPositionTypeId);
			 emplPositionMap.put("partyId", partyId);
			 emplPositionMap.put("statusId", "EMPL_POS_ACTIVE");
			 emplPositionMap.put("salaryFlag", "Y");
			 emplPositionMap.put("exemptFlag", "Y");
			 emplPositionMap.put("fulltimeFlag", "Y");
			 emplPositionMap.put("temporaryFlag", "Y");
			 
			GenericValue emplPosition = null;
			
					// save value
					try {
						emplPosition = delegator.makeValue("EmplPosition", UtilMisc.toMap(emplPositionMap));
						delegator.create(emplPosition);
					} catch (GenericEntityException e) {
						return "error";
					}
            
        }else{
        	request.setAttribute("_ERROR_MESSAGE_", "new emplPosition can not be empty");
            return "error";
        }
		
		
		//if parent null then with new position update the parents
		if (UtilValidate.isEmpty(parentEmplPositionId)) {
			
			
			Timestamp fromDate = new Timestamp(System.currentTimeMillis());	
					//then assing in EmplPositionReportingStruct
			 Map<String, Object> emplPositionReportingStructMap = FastMap.newInstance();
			 emplPositionReportingStructMap.put("emplPositionIdReportingTo",newEmplPositionId);
			 emplPositionReportingStructMap.put("emplPositionIdManagedBy",newEmplPositionId);
			 emplPositionReportingStructMap.put("fromDate",fromDate);
			 
			 GenericValue emplPositionReportingStruct = null;
				
				// save value
				try {
					emplPositionReportingStruct = delegator.makeValue("EmplPositionReportingStruct", UtilMisc.toMap(emplPositionReportingStructMap));
					delegator.create(emplPositionReportingStruct);
				} catch (GenericEntityException e) {
					return "error";
				}
		}else{
			//parent is not empty then create the reporting parent
			List<GenericValue> emplPositionTypeList = null;

			/*try {
				emplPositionTypeList = delegator.findByAnd("EmplPositionAndEmplPositionType", UtilMisc.toMap("emplPositionId", parentEmplPositionId));
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}

			GenericValue id = EntityUtil.getFirst(emplPositionTypeList);

			parentEmplPostionId = id.get("emplPositionId").toString();*/
			
			if(UtilValidate.isEmpty(parentEmplPositionId)){
				request.setAttribute("_ERROR_MESSAGE_", "please insert a valid parent for new empl position");
	            return "error";
			}
			
			Timestamp fromDate = new Timestamp(System.currentTimeMillis());	
			//then assing in EmplPositionReportingStruct
				Map<String, Object> emplPositionReportingStructMap = FastMap.newInstance();
			emplPositionReportingStructMap.put("emplPositionIdReportingTo",parentEmplPositionId);
			emplPositionReportingStructMap.put("emplPositionIdManagedBy",newEmplPositionId);
			emplPositionReportingStructMap.put("fromDate",fromDate);
	 
			GenericValue emplPositionReportingStruct = null;
		
			// save value
			try {
				emplPositionReportingStruct = delegator.makeValue("EmplPositionReportingStruct", UtilMisc.toMap(emplPositionReportingStructMap));
				delegator.create(emplPositionReportingStruct);
			} catch (GenericEntityException e) {
				return "error";
			}
		}

		
		

		request.setAttribute("_EVENT_MESSAGE_", "Suceecssfully Created Position");
		return "success";
	}
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String assingFulfillmentPerson(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
      
        String emplId = request.getParameter("emplId");
        String emplPositionId = request.getParameter("emplPositionId");
       
        
        
        if (UtilValidate.isEmpty(emplId)) {
            request.setAttribute("_ERROR_MESSAGE_", "emplId can not be empty");
            request.setAttribute("emplPositionId", emplPositionId);
            return "error";
        }
        
        String partyId = IncrementRuleAssignment.getPartyIdfromEmplId(delegator, emplId);
       
        if (UtilValidate.isEmpty(partyId)) {
            request.setAttribute("_ERROR_MESSAGE_", "emplId not found");
            request.setAttribute("emplPositionId", emplPositionId);
            return "error";
        }
        Timestamp fromDate = new Timestamp(System.currentTimeMillis());
     // create the adjustment transaction
        Map<String, Object> emplPositionFulfillmentMap = FastMap.newInstance();
       
        
        emplPositionFulfillmentMap.put("partyId", partyId);
        emplPositionFulfillmentMap.put("emplPositionId", emplPositionId);
        emplPositionFulfillmentMap.put("fromDate", fromDate);
        List<GenericValue> emplPositionFulfillments = null;
        GenericValue personFulfillment = null;
        try {
       //check if already exist then update else create a new fulfillment
        	emplPositionFulfillments = delegator.findByAnd("EmplPositionFulfillment", UtilMisc.toMap("partyId", partyId));
        	personFulfillment = EntityUtil.getFirst(emplPositionFulfillments);
        	if(UtilValidate.isNotEmpty(personFulfillment)){
        		personFulfillment.remove();
        	}
        	personFulfillment = delegator.makeValue("EmplPositionFulfillment", UtilMisc.toMap(emplPositionFulfillmentMap));
    		personFulfillment.create();
        	
        } catch (GenericEntityException e) {
        	request.setAttribute("emplPositionId", emplPositionId);
            return "error";
        }
        
       
        request.setAttribute("emplPositionId", emplPositionId);
		request.setAttribute("_EVENT_MESSAGE_", "Person Assigned Successfully");
        return "success";
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String moveEmplForPosition(HttpServletRequest request, HttpServletResponse response) {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
      
       
       
        String orgPartyId = request.getParameter("orgPartyId");
        String emplPositionId = request.getParameter("emplPositionId");
        String destinationPartyId = request.getParameter("partyId");
        
        if (UtilValidate.isEmpty(destinationPartyId)) {
            request.setAttribute("_ERROR_MESSAGE_", "destination organization could not found");
            return "error";
        }
        if (UtilValidate.isEmpty(orgPartyId)) {
            request.setAttribute("_ERROR_MESSAGE_", "company could not found");
            return "error";
        }
        if (UtilValidate.isEmpty(emplPositionId)) {
            request.setAttribute("_ERROR_MESSAGE_", "source position is empty");
            return "error";
        }
        List<GenericValue> emplPositions = null;
        GenericValue emplPosition = null;
        try {
        	emplPosition = delegator.findByPrimaryKey("EmplPosition", UtilMisc.toMap("emplPositionId", emplPositionId));
        	
        	if(UtilValidate.isEmpty(emplPosition)){
        		 request.setAttribute("_ERROR_MESSAGE_", "employee position is not created");
                 return "error";
        	}
        	 Timestamp fromDate = new Timestamp(System.currentTimeMillis());
        	emplPosition.set("partyId", destinationPartyId.trim());
        	emplPosition.set("actualFromDate", fromDate);
        	emplPosition.store();
        	} catch (Exception e) {
			 return "error";
		}
        
       
        
       
        request.setAttribute("emplPositionId", emplPositionId);
		request.setAttribute("_EVENT_MESSAGE_", "move the position Successfully");
        return "success";
	}
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String mergedEmplPositionFromTypes(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String emplPositionTypeId = request.getParameter("emplPositionTypeId");
		GenericValue emplPositionType = null;
		List<GenericValue> emplPositionTypesList = null;
		List<GenericValue> emplPositionList = null;
		//String minPositionTypeId = "";
		
        try {
        	emplPositionType = delegator.findByPrimaryKey("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId));
        	
        	emplPositionTypesList = delegator.findByAnd("EmplPositionType", UtilMisc.toMap("description",emplPositionType.get("description")));
        	
        	//get the min postion id
        	/*ArrayList<Integer> arrayList = new ArrayList<Integer>();
        	for (GenericValue positionType : emplPositionTypesList) {
        		arrayList.add(Integer.parseInt(positionType.get("emplPositionTypeId").toString()));
			}
        	minPositionTypeId = Collections.min(arrayList).toString();
        	
        	 GenericValue firstValue = EntityUtil.getFirst(emplPositionTypesList);
        	 
        	 minPositionTypeId = firstValue.getString("emplPositionTypeId");
        	 */
        	for (GenericValue positionType : emplPositionTypesList) {
        		emplPositionList = delegator.findByAnd("EmplPosition", UtilMisc.toMap("emplPositionTypeId",positionType.get("emplPositionTypeId")));
        		GenericValue emplPostion = EntityUtil.getFirst(emplPositionList);
        		for(GenericValue emplPos : emplPositionList){
        			
        			//set the value and update the emplPostion
            		if(UtilValidate.isNotEmpty(emplPos)){
            			emplPos.set("emplPositionTypeId", emplPositionTypeId);
            			emplPos.store();
            		}
        		}
        		//removed the postionType
    			if(!emplPositionTypeId.equals(positionType.get("emplPositionTypeId"))){
    				positionType.remove();
    			}
        		
			}
        	
        	
        	
        	
        }catch(Exception e){
        	request.setAttribute("errorMessage", "merged error");
        	return "error";
        	
        }
		request.setAttribute("emplPositionId", "");
		request.setAttribute("_EVENT_MESSAGE_", "merged Successfully");
        return "success";
	}
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String addContessaReportingStruct(HttpServletRequest request, HttpServletResponse response) {	
		Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        String emplPositionId = request.getParameter("emplPositionId");
        String parentEmplPositionId = request.getParameter("parentEmplPositionId");
        
        if(UtilValidate.isEmpty(parentEmplPositionId)){
			request.setAttribute("_ERROR_MESSAGE_", "parent empl position not found");
			 request.setAttribute("emplPositionId", emplPositionId);
            return "error";
		}
        if(UtilValidate.isEmpty(emplPositionId)){
			request.setAttribute("_ERROR_MESSAGE_", "Empl position not found");
			 request.setAttribute("emplPositionId", emplPositionId);
            return "error";
		}
        
        Timestamp fromDate = new Timestamp(System.currentTimeMillis());	
		//then assing in EmplPositionReportingStruct
			Map<String, Object> emplPositionReportingStructMap = FastMap.newInstance();
		emplPositionReportingStructMap.put("emplPositionIdReportingTo",parentEmplPositionId);
		emplPositionReportingStructMap.put("emplPositionIdManagedBy",emplPositionId);
		emplPositionReportingStructMap.put("fromDate",fromDate);
 
		GenericValue emplPositionReportingStruct = null;
	
		// save value
		try {
			emplPositionReportingStruct = delegator.makeValue("EmplPositionReportingStruct", UtilMisc.toMap(emplPositionReportingStructMap));
			delegator.create(emplPositionReportingStruct);
		} catch (GenericEntityException e) {
			request.setAttribute("_ERROR_MESSAGE_", "Reporting Struct already exists");
			request.setAttribute("emplPositionId", emplPositionId);
			return "error";
		}
        
        request.setAttribute("emplPositionId", emplPositionId);
		request.setAttribute("_EVENT_MESSAGE_", "Add parent position Successfully");
        return "success";
	}
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String addContessaPositionResponsibility(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        String emplPositionId = request.getParameter("emplPositionId");
        String responsibilityTypeId = request.getParameter("responsibilityTypeId");
        
        if(UtilValidate.isEmpty(responsibilityTypeId)){
			request.setAttribute("_ERROR_MESSAGE_", "responsibility Type not found");
			 request.setAttribute("emplPositionId", emplPositionId);
            return "error";
		}
        if(UtilValidate.isEmpty(emplPositionId)){
			request.setAttribute("_ERROR_MESSAGE_", "Empl position not found");
			 request.setAttribute("emplPositionId", emplPositionId);
            return "error";
		}
        
        Timestamp fromDate = new Timestamp(System.currentTimeMillis());	
		//then assing in EmplPositionReportingStruct
			Map<String, Object> emplPositionResponsibilityMap = FastMap.newInstance();
			emplPositionResponsibilityMap.put("responsibilityTypeId",responsibilityTypeId);
			emplPositionResponsibilityMap.put("emplPositionId",emplPositionId);
			emplPositionResponsibilityMap.put("fromDate",fromDate);
 
		GenericValue emplPositionResponsibility = null;
	
		// save value
		try {
			emplPositionResponsibility = delegator.makeValue("EmplPositionResponsibility", UtilMisc.toMap(emplPositionResponsibilityMap));
			delegator.create(emplPositionResponsibility);
		} catch (GenericEntityException e) {
			request.setAttribute("_ERROR_MESSAGE_", "empl Position Responsibility already exists");
			request.setAttribute("emplPositionId", emplPositionId);
			return "error";
		}
        
		request.setAttribute("emplPositionId", emplPositionId);
		request.setAttribute("_EVENT_MESSAGE_", "Empl Position Responsibility added Successfully");
        return "success";
	}
	
	
	public String getBudgetAmount (Delegator delegator,Map<String, ? extends Object> context){
		String partyId = (String) context.get("partyId");
		String emplPositionId = (String) context.get("emplPositionId");
		String amount="";
		
		try {
			GenericValue employeePosition = delegator.findByPrimaryKey(
					"EmplPosition", UtilMisc.toMap("emplPositionId",emplPositionId.trim()));
			
			if(UtilValidate.isNotEmpty(employeePosition)){
				String budgetIId=employeePosition.getString("budgetId");
				String budgetSeqId=employeePosition.getString("budgetItemSeqId");
				List<GenericValue>  budgetItemList = null;
				budgetItemList = delegator.findByAnd("BudgetItem", UtilMisc.toMap("budgetId", budgetIId,"budgetItemSeqId", budgetSeqId,"budgetItemTypeId", "REQUIREMENT_BUDGET"));
				GenericValue budgetItem = EntityUtil.getFirst(budgetItemList);
				amount=budgetItem.getString("amount");
			}
			
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return amount;
		
	}
	
	public static Map<String, Object> updatePositionBudget(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		String partyId = (String) context.get("partyId");
		String emplPositionId = (String) context.get("emplPositionId");
		String amount = (String) context.get("budgetId");
		GenericValue longInUserInfo=(GenericValue) context.get("");
		Delegator delegator = dctx.getDelegator();
	
		try {
			GenericValue employeePosition = delegator.findByPrimaryKey(
					"EmplPosition", UtilMisc.toMap("emplPositionId",emplPositionId.trim()));
			
			
			if(UtilValidate.isNotEmpty(employeePosition)){
				String budgetIId=employeePosition.getString("budgetId");
				String budgetSeqId=employeePosition.getString("budgetItemSeqId");
				
				if (UtilValidate.isNotEmpty(budgetIId)) {
					List<GenericValue>  budgetItemList = null;
					try {
						budgetItemList = delegator.findByAnd("BudgetItem", UtilMisc.toMap("budgetId", budgetIId,"budgetItemSeqId", budgetSeqId,"budgetItemTypeId", "REQUIREMENT_BUDGET"));
						GenericValue budgetItem = EntityUtil.getFirst(budgetItemList);
						budgetItem.set("amount", new BigDecimal(amount));
						delegator.store(budgetItem);
					} catch (GenericEntityException e1) {
						
						e1.printStackTrace();
					}
			    }else{
			    				    	 
			    	  try{
			    	    GenericValue genValueBudget = delegator.makeValue("Budget");
						String budgetId = delegator.getNextSeqId("Budget");												
						genValueBudget.set("budgetId", budgetId.trim());
						genValueBudget.set("budgetTypeId", "REQUIREMENT_BUDGET");						
						delegator.create(genValueBudget);
						String budgetItemSeqId=null;
						try{
						GenericValue genValueBudgetItem = delegator.makeValue("BudgetItem");
						budgetItemSeqId = budgetId;												
						genValueBudgetItem.set("budgetItemSeqId", budgetItemSeqId);
						genValueBudgetItem.set("budgetId", budgetId.trim());
						genValueBudgetItem.set("budgetItemTypeId", "REQUIREMENT_BUDGET");	
						genValueBudgetItem.set("amount", new BigDecimal(amount));	
						delegator.create(genValueBudgetItem);
						}catch(Exception ex){
							logger.debug("Exception to create in BudgetItem--->"+ex);
						}
						employeePosition.set("budgetId", budgetId.trim());
						if(budgetItemSeqId!=null){
						employeePosition.set("budgetItemSeqId", budgetItemSeqId.trim());
						}
						delegator.store(employeePosition);
						
			    	  }catch(Exception e){
			    		  logger.debug("Exception to create and store--->"+e);
			    	  }
			    }
				  		
    		}
				
		} catch (GenericEntityException e) {
			result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
			result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
			return result;
		}
		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Position Budget Update Successfully");
		ArrayList<String> mailRecipientPartyIdList=new ArrayList<String>();
		mailRecipientPartyIdList.add("hradmin");
		mailRecipientPartyIdList.add("admin");
		for(String recipientPartyId:mailRecipientPartyIdList){
			try {
				List<GenericValue> partyAndContactGenericList = delegator.findByAnd("PartyAndContactMech", UtilMisc.toMap("partyId",recipientPartyId,"paContactMechId","EMAIL_ADDRESS"));
				
				if(UtilValidate.isNotEmpty(partyAndContactGenericList)){
					for(GenericValue partyAndContactGeneric:partyAndContactGenericList){
						if(partyAndContactGeneric.getString("infoString")!=null){
							String defaultMailAgent = UtilProperties.getPropertyValue("general.properties", "defaultMailAgent");
							//RecruitmentNotificationEvents.createCommEvent(dctx, context,defaultMailAgent, partyAndContactGeneric.getString("infoString"), "Position Budget Update Successfully content", "Position Budget Update Successfully subject","Position Budget Increase/Decrease");
						}
					}
				}
				
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return result;
	}
		
	
}
