package org.ofbiz.humanres.contessaTrainingDevelopment;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;

import java.math.BigDecimal;
import java.sql.Timestamp;


public class TrainingDevelopmentConfig {	
     public static String createTrainingExpenseType(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        
	      
	      String expenseId = delegator.getNextSeqId("TrainingExpenseType");
	      String description = request.getParameter("description");
	      String expenseName=request.getParameter("expenseName");
	      if (null != request.getParameter("expenseName")) {
	    	  expenseName = request.getParameter("expenseName");
	        }
	        if (expenseName.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Training Expense Type Name can not be empty");
	            return "error";
	        }
	      	     
			Map<String, Object> trainingExpenseType = FastMap.newInstance();
			trainingExpenseType.put("expenseId", expenseId);
			trainingExpenseType.put("expenseName", expenseName);
			trainingExpenseType.put("description", description);
   	    try {
   			GenericValue trainingExpenseTypeSetup = delegator
   				.makeValue("TrainingExpenseType",
   						UtilMisc.toMap(trainingExpenseType));
   			trainingExpenseTypeSetup.create();  
   		    request.setAttribute("_EVENT_MESSAGE_", "Performance Appraisal Type Create Successfully");
   	    } catch (Exception e) { }

        return "success";
    }
     
	 public static String createTrainingOffer(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
	      
	      String trainingOfferId = delegator.getNextSeqId("TrainingOffer");
	      String description = request.getParameter("description");
	      String comments = request.getParameter("comments");
	      
	      
	      String durationTime=request.getParameter("durationTime");
	      if (null != request.getParameter("durationTime")) {
	    	  durationTime = request.getParameter("durationTime");
	        }
	        if (durationTime.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Training duration Time can not be empty");
	            return "error";
	        }
	      String trainingTitle=request.getParameter("trainingTitle");
		      if (null != request.getParameter("trainingTitle")) {
		    	  trainingTitle = request.getParameter("trainingTitle");
		        }
		        if (trainingTitle.equals("")) {
		            request.setAttribute("_ERROR_MESSAGE_", "Training Title can not be empty");
		            return "error";
		        }
		  String trainingPlace=request.getParameter("trainingPlace");
			      if (null != request.getParameter("trainingPlace")) {
			    	  trainingPlace = request.getParameter("trainingPlace");
			        }
			        if (trainingPlace.equals("")) {
			            request.setAttribute("_ERROR_MESSAGE_", "Training Place can not be empty");
			            return "error";
			        }
	      	     
			Map<String, Object> trainingOfferType = FastMap.newInstance();
			trainingOfferType.put("trainingOfferId", trainingOfferId);
			trainingOfferType.put("trainingTitle", trainingTitle);
			trainingOfferType.put("trainingPlace", trainingPlace);
			trainingOfferType.put("durationTime", durationTime);
			trainingOfferType.put("isActive", "Y");
			trainingOfferType.put("dateAdded", currentDatetime);
			trainingOfferType.put("description", description);
			trainingOfferType.put("comments", comments);
			
   	    try {
   			GenericValue trainingOfferSetup = delegator
   				.makeValue("TrainingOffer",
   						UtilMisc.toMap(trainingOfferType));
   			trainingOfferSetup.create();  
   		    request.setAttribute("_EVENT_MESSAGE_", "Training Offer Create Successfully");
   	    } catch (Exception e) { }

        return "success";
    }

	 
	 
	 
	 
	 

	 public static String createPartyTrainingRequest(HttpServletRequest request, HttpServletResponse response) {

		 Delegator delegator = (Delegator) request.getAttribute("delegator");
		 LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		 DispatchContext context = dispatcher.getDispatchContext();
		 Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());

		  String trainingOfferId="";
		  if (null != request.getParameter("trainingOfferId")) {
			  trainingOfferId = request.getParameter("trainingOfferId");
		  }
		  if (trainingOfferId.equals("")) {
			  request.setAttribute("_ERROR_MESSAGE_", "Training Offer Id can not be empty");
			  return "error";
		  }
		  String employeeID = "";
			if (null != request.getParameter("employeeId")) {
				employeeID = request.getParameter("employeeId");				
			}

			  if (employeeID.equals("")) {
				  request.setAttribute("_ERROR_MESSAGE_", "Employee Id can not be empty");
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

		  String description="";
		  if (null != request.getParameter("description")) {
			  description = request.getParameter("description");
		  }
		  String requestId = delegator.getNextSeqId("PartyTrainingRequest");
		  Map<String, Object> trainingOfferType = FastMap.newInstance();
		  trainingOfferType.put("requestId", requestId);
		  trainingOfferType.put("partyId", partyId);
		  trainingOfferType.put("trainingOfferId", trainingOfferId);
		  trainingOfferType.put("isActive", "Y");
		  trainingOfferType.put("dateAdded", currentDatetime);
		  trainingOfferType.put("description", description);
			
	   	    try {
	   			GenericValue trainingOfferSetup = delegator
	   				.makeValue("PartyTrainingRequest",
	   						UtilMisc.toMap(trainingOfferType));
	   			trainingOfferSetup.create();  
	   		    request.setAttribute("_EVENT_MESSAGE_", "Training Request Create Successfully");
	   	    } catch (Exception e) { }

        return "success";
    }
	 
	 
	 
	 

	 public static String createPartyTrainingRequestDetails(HttpServletRequest request, HttpServletResponse response) {

		 Delegator delegator = (Delegator) request.getAttribute("delegator");
		 LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		 DispatchContext context = dispatcher.getDispatchContext();
		 Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());

		  String requestId="";
		  String trainingOfferId="";
		  String partyId="";
		  if (null != request.getParameter("requestId")) {
			  requestId = request.getParameter("requestId");
		  }
		  if (requestId.equals("")) {
			  request.setAttribute("_ERROR_MESSAGE_", "Request Id can not be empty");
			  return "error";
		  }
		  String expenseId = "";
		  if (null != request.getParameter("expenseId")) {
			  expenseId = request.getParameter("expenseId");				
		  }
		  if (expenseId.equals("")) {
			  request.setAttribute("_ERROR_MESSAGE_", "Expense can not be empty");
			  return "error";
		  }
		  String amountStr = "";
		  BigDecimal amount=new BigDecimal(0);
		  
		  if (null != request.getParameter("amount")) {
			  amountStr = request.getParameter("amount");
			  try
			  {
				  amount = new BigDecimal(amountStr);
			  }
			  catch(Exception e){
				  request.setAttribute("_ERROR_MESSAGE_", "Amount must be number");
				  return "error";				  
			  }			  
		  }
		  if (amountStr.equals("")) {
			  request.setAttribute("_ERROR_MESSAGE_", "Amount can not be empty");
			  return "error";
		  }
		  String description="";
		  if (null != request.getParameter("description")) {
			  description = request.getParameter("description");
		  }
		  String comments="";
		  if (null != request.getParameter("comments")) {
			  comments = request.getParameter("comments");
		  }
		  String requestDetailsId = delegator.getNextSeqId("PartyTrainingRequestDetails");
		  Map<String, Object> trainingOfferType = FastMap.newInstance();
		  trainingOfferType.put("requestDetailsId", requestDetailsId);
		  trainingOfferType.put("requestId", requestId);
		  trainingOfferType.put("expenseId", expenseId);
		  trainingOfferType.put("amount", amount);
		  trainingOfferType.put("isActive", "Y");
		  trainingOfferType.put("dateAdded", currentDatetime);
		  trainingOfferType.put("description", description);
		  trainingOfferType.put("comments", comments);
			
			try {
				GenericValue trainingOfferSetup = delegator
					.makeValue("PartyTrainingRequestDetails",
							UtilMisc.toMap(trainingOfferType));
				trainingOfferSetup.create();  
			    request.setAttribute("_EVENT_MESSAGE_", "Training Request Details Create Successfully");
			    trainingOfferId = request.getParameter("trainingOfferId");
			    partyId = request.getParameter("partyId");	
			} catch (Exception e) { }

		    request.setAttribute("requestId", requestId);
		    request.setAttribute("trainingOfferId", trainingOfferId);
		    request.setAttribute("partyId", partyId);
        return "success";
    }

	 public static String updatePartyTrainingRequestDetails(HttpServletRequest request, HttpServletResponse response) {

		 Delegator delegator = (Delegator) request.getAttribute("delegator");
		 LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		 DispatchContext context = dispatcher.getDispatchContext();
		 Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());

		  String requestId="";
		  String trainingOfferId="";
		  String partyId="";
		  if (null != request.getParameter("requestId")) {
			  requestId = request.getParameter("requestId");
		  }
		  if (requestId.equals("")) {
			  request.setAttribute("_ERROR_MESSAGE_", "Request Id can not be empty");
			  return "error";
		  }
		  String requestDetailsId = "";
		  if (null != request.getParameter("requestDetailsId")) {
			  requestDetailsId = request.getParameter("requestDetailsId");				
		  }
		  if (requestDetailsId.equals("")) {
			  request.setAttribute("_ERROR_MESSAGE_", "RequestDetailsId can not be empty");
			  return "error";
		  }
		  String expenseId = "";
		  if (null != request.getParameter("expenseId")) {
			  expenseId = request.getParameter("expenseId");				
		  }
		  if (expenseId.equals("")) {
			  request.setAttribute("_ERROR_MESSAGE_", "Expense can not be empty");
			  return "error";
		  }
		  String amountStr = "";
		  BigDecimal amount=new BigDecimal(0);
		  
		  if (null != request.getParameter("amount")) {
			  amountStr = request.getParameter("amount");
			  try
			  {
				  amount = new BigDecimal(amountStr);
			  }
			  catch(Exception e){
				  request.setAttribute("_ERROR_MESSAGE_", "Amount must be number");
				  return "error";				  
			  }			  
		  }
		  if (amountStr.equals("")) {
			  request.setAttribute("_ERROR_MESSAGE_", "Amount can not be empty");
			  return "error";
		  }
		  String description="";
		  if (null != request.getParameter("description")) {
			  description = request.getParameter("description");
		  }
		  String comments="";
		  if (null != request.getParameter("comments")) {
			  comments = request.getParameter("comments");
		  }
		  Map<String, Object> trainingOfferType = FastMap.newInstance();
		  trainingOfferType.put("requestDetailsId", requestDetailsId);
		  trainingOfferType.put("requestId", requestId);
		  trainingOfferType.put("expenseId", expenseId);
		  trainingOfferType.put("amount", amount);
		  trainingOfferType.put("isActive", "Y");
		  trainingOfferType.put("dateAdded", currentDatetime);
		  trainingOfferType.put("description", description);
		  trainingOfferType.put("comments", comments);
			
			try {
				GenericValue trainingOfferSetup = delegator
					.makeValue("PartyTrainingRequestDetails",
							UtilMisc.toMap(trainingOfferType));
				trainingOfferSetup.store();  
			    request.setAttribute("_EVENT_MESSAGE_", "Training Request Details Create Successfully");
			    trainingOfferId = request.getParameter("trainingOfferId");
			    partyId = request.getParameter("partyId");	
			} catch (Exception e) { }

		    request.setAttribute("requestId", requestId);
		    request.setAttribute("trainingOfferId", trainingOfferId);
		    request.setAttribute("partyId", partyId);
        return "success";
    }
}

	
