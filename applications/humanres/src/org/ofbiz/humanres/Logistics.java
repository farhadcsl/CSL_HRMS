
package org.ofbiz.humanres;
/*
import java.util.Map;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
*/

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.regex.PatternSyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;


//import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
//import org.ofbiz.humanres.HumanResEvents;

import org.ofbiz.minilang.method.entityops.FindByAnd;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.GenericDispatcher;

import javax.servlet.http.HttpSession;


public class Logistics {

	
	public static final String module = Logistics.class.getName();
	
	public static  Map<String, Object> createLogisticsRequisition(DispatchContext dctx,
			Map<String, ? extends Object> context){
    Map<String, Object> result = ServiceUtil.returnSuccess();
	Delegator delegator = dctx.getDelegator();
	String issueId = delegator.getNextSeqId("LogisticsItem");
    String logisticsItemTypeId = (String) context.get("logisticsItemTypeId");
    String partyId = (String) context.get("partyId");
    Date requisitionDate = (Date) context.get("requisitionDate");
    String description = (String) context.get("description");
    //int quantity = 0;
    BigDecimal quantity = (BigDecimal) context.get("quantity");
    
    String remarks = (String) context.get("remarks");
   
    Map<String, Object> LogisticsRequsitionIn = FastMap.newInstance();
    
    LogisticsRequsitionIn.put("issueId", issueId);
    LogisticsRequsitionIn.put("logisticsItemTypeId", logisticsItemTypeId);
    LogisticsRequsitionIn.put("partyId", partyId);
    LogisticsRequsitionIn.put("quantity", quantity);
    LogisticsRequsitionIn.put("requisitionDate", requisitionDate);
    LogisticsRequsitionIn.put("statusId", "REQ_PEND");
    LogisticsRequsitionIn.put("description", description);
    
 
    try {
		GenericValue logisticsReq = delegator
				.makeValue("LogisticsItem",
						UtilMisc.toMap(LogisticsRequsitionIn));
		logisticsReq.create();
    } catch (Exception e) {

    	e.printStackTrace();
    }
   
   String successMessage = "Successfully Created Logistics Requisition.";
   result.put("partyId", partyId);
    return result; 
}
	
	
	public static  Map<String, Object> approveLogisticsRequisition(DispatchContext dctx,
			Map<String, ? extends Object> context){
    Map<String, Object> result = ServiceUtil.returnSuccess();
	Delegator delegator = dctx.getDelegator();
	
	String issueId =  (String) context.get("issueId");
    String partyId = (String) context.get("partyId");
    Timestamp stamp = new Timestamp(System.currentTimeMillis());
    java.sql.Date approvedDate = new java.sql.Date(stamp.getTime());
    GenericValue updatablePartylogistics = null; 
    try{
    	updatablePartylogistics = delegator.findByPrimaryKey("LogisticsItem", UtilMisc.toMap("issueId", issueId));
    	if(UtilValidate.isNotEmpty(approvedDate)){
    		updatablePartylogistics.set("approvedDate", approvedDate);
  		 }
    	updatablePartylogistics.set("statusId", "REQ_APPR");
    	updatablePartylogistics.store();
    } catch (GenericEntityException e) {
        Debug.logWarning(e, module);
        return ServiceUtil.returnError("Changed Logistics approval flow could not be Updated");
    }
    
   
    result.put(ModelService.SUCCESS_MESSAGE, "Logistics Requisition Approved.");
 
   result.put("partyId", partyId);
    return result; 
}
	public static  Map<String, Object> cancelLogisticsRequisition(DispatchContext dctx,
			Map<String, ? extends Object> context){
    Map<String, Object> result = ServiceUtil.returnSuccess();
	Delegator delegator = dctx.getDelegator();
	
	String issueId =  (String) context.get("issueId");
    String partyId = (String) context.get("partyId");
    Timestamp stamp = new Timestamp(System.currentTimeMillis());
    java.sql.Date cancelDate = new java.sql.Date(stamp.getTime());
    GenericValue updatablePartylogistics = null; 
    try{
    	updatablePartylogistics = delegator.findByPrimaryKey("LogisticsItem", UtilMisc.toMap("issueId", issueId));
    	if(UtilValidate.isNotEmpty(cancelDate)){
    		updatablePartylogistics.set("cancelDate", cancelDate);
  		 }
    	updatablePartylogistics.set("statusId", "REQ_CANCEL");
    	updatablePartylogistics.store();
    } catch (GenericEntityException e) {
        Debug.logWarning(e, module);
        return ServiceUtil.returnError("Changed Logistics approval flow could not be Updated");
    }
    
   
    result.put(ModelService.SUCCESS_MESSAGE, "Logistics Requisition Cancelled.");
 
   result.put("partyId", partyId);
    return result; 
}
	
	public static  Map<String, Object> issueLogisticsRequisition(DispatchContext dctx,
			Map<String, ? extends Object> context){
    Map<String, Object> result = ServiceUtil.returnSuccess();
	Delegator delegator = dctx.getDelegator();
	
	String issueId =  (String) context.get("issueId");
    String partyId = (String) context.get("partyId");
   
    Date issuedDate = (Date) context.get("issuedDate");
    String remarks = (String) context.get("remarks");
    BigDecimal quantity = (BigDecimal) context.get("issueQuantity");
    GenericValue updatablePartylogistics = null; 
    try{
    	updatablePartylogistics = delegator.findByPrimaryKey("LogisticsItem", UtilMisc.toMap("issueId", issueId));
    	if(UtilValidate.isNotEmpty(issuedDate)){
    		updatablePartylogistics.set("issuedDate", issuedDate);
  		 }
    	updatablePartylogistics.set("statusId", "REQ_ASSIG");
    	updatablePartylogistics.set("isActive", "Y");
    	updatablePartylogistics.set("issueQuantity", quantity);
   
    	updatablePartylogistics.set("remarks", remarks);
    	updatablePartylogistics.store();
    } catch (GenericEntityException e) {
        Debug.logWarning(e, module);
        return ServiceUtil.returnError("Changed Logistics approval flow could not be Updated");
    }
    
   
    result.put(ModelService.SUCCESS_MESSAGE, "Logistics Assigned.");
 
   result.put("partyId", partyId);
    return result; 
}
}