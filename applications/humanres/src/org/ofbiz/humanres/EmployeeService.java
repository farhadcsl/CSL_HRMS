package org.ofbiz.humanres;
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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

//Created By muntashir Islam
public class EmployeeService {

    public static final String module = EmployeeService.class.getName();
    /* 
     * employee emergency contact update and create
     * Muntashir Islam
     */
    public static Map<String, Object> addUpdateEmergencyContact(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Map<String, Object> result = FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        
        String partyId = (String) context.get("partyId");
        String emergencyContactName = (String) context.get("emergencyContactName");
        String emergencyContactAddress = (String) context.get("emergencyContactAddress");
        String emergencyContactNumber = (String) context.get("emergencyContactNumber");
        String emergencyContactRelation = (String) context.get("emergencyContactRelation");
        String emergencyContactNational = (String) context.get("emergencyContactNational");
        String emergencyContactOther = (String) context.get("emergencyContactOther");
        
        String nomineeInfoName = (String) context.get("nomineeInfoName");
        String nomineeInfoAddress = (String) context.get("nomineeInfoAddress");
        String nomineeInfoNumber = (String) context.get("nomineeInfoNumber");
        String nomineeInfoRelation = (String) context.get("nomineeInfoRelation");
        String nomineeInfoNational = (String) context.get("nomineeInfoNational");
        String nomineeInfoOther = (String) context.get("nomineeInfoOther");
        List<GenericValue> emergencyContact = null;

        try {
        	emergencyContact = delegator.findByAnd("EmergencyAndNomineeInfo", UtilMisc.toMap("partyId", partyId));

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        GenericValue emergencyContactandNominee = null; 
        if (UtilValidate.isNotEmpty(emergencyContact)){
        	 try {
        		 emergencyContactandNominee = delegator.findByPrimaryKey("EmergencyAndNomineeInfo", UtilMisc.toMap("partyId", partyId));
        		 if(UtilValidate.isNotEmpty(emergencyContactName)){
        			 emergencyContactandNominee.set("emergencyContactName", emergencyContactName);
           		 }
           		 if(UtilValidate.isNotEmpty(emergencyContactAddress)){
           			emergencyContactandNominee.set("emergencyContactAddress", emergencyContactAddress);
           		 }
           		 if(UtilValidate.isNotEmpty(emergencyContactNumber)){
           			emergencyContactandNominee.set("emergencyContactNumber", emergencyContactNumber);
           		 }
           		 
           		if(UtilValidate.isNotEmpty(emergencyContactRelation)){
           			emergencyContactandNominee.set("emergencyContactRelation", emergencyContactRelation);
          		 }
           		if(UtilValidate.isNotEmpty(emergencyContactNational)){
           			emergencyContactandNominee.set("emergencyContactNational", emergencyContactNational);
          		 }
           		if(UtilValidate.isNotEmpty(emergencyContactOther)){
           			emergencyContactandNominee.set("emergencyContactOther", emergencyContactOther);
          		 }
           		if(UtilValidate.isNotEmpty(nomineeInfoName)){
       			 emergencyContactandNominee.set("nomineeInfoName", nomineeInfoName);
          		 }
          		 if(UtilValidate.isNotEmpty(nomineeInfoAddress)){
          			emergencyContactandNominee.set("nomineeInfoAddress", nomineeInfoAddress);
          		 }
          		 if(UtilValidate.isNotEmpty(nomineeInfoNumber)){
          			emergencyContactandNominee.set("nomineeInfoNumber", nomineeInfoNumber);
          		 }
          		 
          		if(UtilValidate.isNotEmpty(nomineeInfoRelation)){
          			emergencyContactandNominee.set("nomineeInfoRelation", nomineeInfoRelation);
         		 }
          		if(UtilValidate.isNotEmpty(nomineeInfoNational)){
          			emergencyContactandNominee.set("nomineeInfoNational", nomineeInfoNational);
         		 }
          		if(UtilValidate.isNotEmpty(nomineeInfoOther)){
          			emergencyContactandNominee.set("nomineeInfoOther", nomineeInfoOther);
         		 }
          		emergencyContactandNominee.store();
           		
        	 } catch (GenericEntityException e) {
                 e.printStackTrace();
             }
        } else {
        	Map<String, Object> emergencyAndNomineeAddressIn = FastMap.newInstance();
        	emergencyAndNomineeAddressIn.put("partyId", partyId);
        	emergencyAndNomineeAddressIn.put("emergencyContactName", emergencyContactName);
        	emergencyAndNomineeAddressIn.put("emergencyContactAddress", emergencyContactAddress);
        	emergencyAndNomineeAddressIn.put("emergencyContactNumber", emergencyContactNumber);
        	emergencyAndNomineeAddressIn.put("emergencyContactRelation", emergencyContactRelation);
        	emergencyAndNomineeAddressIn.put("emergencyContactNational", emergencyContactNational);
        	emergencyAndNomineeAddressIn.put("emergencyContactOther", emergencyContactOther);
        	emergencyAndNomineeAddressIn.put("nomineeInfoName", nomineeInfoName);
        	emergencyAndNomineeAddressIn.put("nomineeInfoAddress", nomineeInfoAddress);
        	emergencyAndNomineeAddressIn.put("nomineeInfoNumber", nomineeInfoNumber);
        	emergencyAndNomineeAddressIn.put("nomineeInfoRelation", nomineeInfoRelation);
        	emergencyAndNomineeAddressIn.put("nomineeInfoNational", nomineeInfoNational);
        	emergencyAndNomineeAddressIn.put("nomineeInfoOther", nomineeInfoOther);
        	try {
      			GenericValue createEmergencyAddresssValue = delegator
      					.makeValue("EmergencyAndNomineeInfo",
      							UtilMisc.toMap(emergencyAndNomineeAddressIn));
      			createEmergencyAddresssValue.create();
              } catch (Exception e) {

              	e.printStackTrace();
              }
        }
    
        result.put("partyId",partyId);
        return result;
    
    }
    /* 
     * employee Resposibility create add partyId wise create
     * Muntashir Islam
     */
    public static Map<String, Object> addEmplResponsibility(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Map<String, Object> result = FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        
        String partyId = (String) context.get("partyId");
        String responsibilityTypeId = (String) context.get("responsibilityTypeId");
        Timestamp fromDate = (Timestamp) context.get("fromDate");
        Timestamp thruDate = (Timestamp) context.get("thruDate");
        String comments = (String) context.get("comments");
       
        List<GenericValue> EmplPositionRes = null;

        try {
        	EmplPositionRes = delegator.findByAnd("EmplPositionAndFulfillment", UtilMisc.toMap("employeePartyId", partyId));

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
     
        String empPositionId = null;
		if (EmplPositionRes.size() != 0) {
			empPositionId = EmplPositionRes.get(0).get("emplPositionId").toString();
		}
		Map<String, Object> EmplResponsibilityIn = FastMap.newInstance();
		EmplResponsibilityIn.put("partyId", partyId);
		EmplResponsibilityIn.put("emplPositionId", empPositionId);
		EmplResponsibilityIn.put("responsibilityTypeId", responsibilityTypeId);
		EmplResponsibilityIn.put("fromDate", fromDate);
	
		EmplResponsibilityIn.put("thruDate", thruDate);
		EmplResponsibilityIn.put("comments", comments);
    	
    	try {
  			GenericValue createPartyResposibilityValue = delegator
  					.makeValue("ContessaEmplResponsibility",
  							UtilMisc.toMap(EmplResponsibilityIn));
  			createPartyResposibilityValue.create();
          } catch (Exception e) {
          	e.printStackTrace();
          }
        
        result.put("partyId",partyId);
        return result;
    }
    public static  Map<String, Object> saveProfileCustomField(DispatchContext dctx,
			Map<String, ? extends Object> context){
	    Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		String customFieldIdCheck="Update data";
		String customFieldId=(String) context.get("customFieldId");
	    String partyId = (String) context.get("partyId");
	    String fieldName = (String) context.get("fieldName");
	    String value = (String) context.get("fieldValue");
		//String customFieldId=delegator.getNextSeqId("customFieldId");
		if(customFieldId=="" || customFieldId ==null){
			customFieldId = delegator.getNextSeqId("customFieldId");
			customFieldIdCheck="New data";
		}

	    try {
		    if(customFieldIdCheck=="New data"){
		    	Map<String, Object> CustomFieldIn = FastMap.newInstance();
			    CustomFieldIn.put("customFieldId", customFieldId);
			    CustomFieldIn.put("partyId", partyId);
			    CustomFieldIn.put("fieldName", fieldName);
			    CustomFieldIn.put("fieldValue", value);
			    CustomFieldIn.put("tableName", "Person");
			    
				GenericValue customFiled = delegator
						.makeValue("CustomField",
								UtilMisc.toMap(CustomFieldIn));
				customFiled.create();
		    }
		    else
		    {
		        GenericValue CustomField = null;
		        CustomField = delegator.findByPrimaryKey("CustomField", UtilMisc.toMap("customFieldId",customFieldId,"partyId",partyId));
       		 
			   
			    CustomField.set("fieldName", fieldName);
			    CustomField.set("fieldValue", value);
		
	
				GenericValue customFiled = delegator
						.makeValue("CustomField",
								UtilMisc.toMap(CustomField));
				customFiled.store();
		    }
	    } 
	    catch (Exception e) {
	
	    	e.printStackTrace();
	    }
	   
	 /*
	    try {
			GenericValue customFiled = delegator
					.makeValue("CustomField",
							UtilMisc.toMap(CustomFieldIn));
			//customFiled.create();
			if(customFieldIdCheck=="Update data"){
				customFiled.store();
			}
			else
			{
				customFiled.create();
			}
	    } catch (Exception e) {
	
	    	e.printStackTrace();
	    }
	   */
	   String successMessage = "Successfully Created Custom Field";
	   result.put("customFieldId", customFieldId);
       /*List<GenericValue> EmplPositionRes = null;

       try {
       	EmplPositionRes = delegator.findByAnd("EmplPositionAndFulfillment", UtilMisc.toMap("employeePartyId", partyId));

       } catch (GenericEntityException e) {
           e.printStackTrace();
       }*/
       return result; 
    }


	//-------------------------------------------------------------------------------------------------------------------
	public static Map<String, Object> findProfileCustomField(DispatchContext dctx, Map<String, ? extends Object> context) {
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    Delegator delegator = dctx.getDelegator();
	    String partyId = (String) context.get("partyId");
	    List<EntityExpr> exprs = FastList.newInstance();
		exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	    List<String> orderBy = UtilMisc.toList("partyId", "customFieldId");
		List<GenericValue> customFieldList = null;
		try {
			customFieldList = delegator.findList("CustomField", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		List<Map> cfMap = FastList.newInstance();
		for (Map aParsed : customFieldList){
			Map record = FastMap.newInstance();
			record.putAll(aParsed);
			cfMap.add(record);
		}
	    result.put("customFieldList", cfMap);
	    return result;
	}    
	

	public static String updateEmployeeBasicInfo(HttpServletRequest request, HttpServletResponse response) {
	    
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
		String partyId = request.getParameter("partyId");
		String contactMechTypeId = request.getParameter("contactMechTypeId");
		String fieldName = request.getParameter("fieldName");
		String tableName = request.getParameter("tableName");
		String fieldCurrentValue = request.getParameter("fieldCurrentValue");
		String fieldUpdatedValue = request.getParameter("fieldUpdatedValue");
        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
		String requisitionId = delegator.getNextSeqId("BasicInfoUpdateRequ");
		

		///---------------------------- Requisition Data Update-----------------------//
    	Map<String, Object> RequisitionInfoIn = FastMap.newInstance();
    	RequisitionInfoIn.put("requisitionId", requisitionId);
    	RequisitionInfoIn.put("partyId", partyId);
    	RequisitionInfoIn.put("contactMechTypeId", contactMechTypeId);
    	RequisitionInfoIn.put("tableName", tableName);
    	RequisitionInfoIn.put("fieldName", fieldName);
    	RequisitionInfoIn.put("fieldCurrentValue", fieldCurrentValue);
    	RequisitionInfoIn.put("fieldUpdatedValue", fieldUpdatedValue);
    	RequisitionInfoIn.put("comments", "");
    	RequisitionInfoIn.put("dateAdded", currentDatetime);
    	RequisitionInfoIn.put("status", "Request Send");
	    try {
			GenericValue EmployeeBasicInfoSetup = delegator
				.makeValue("BasicInfoUpdateRequ",
						UtilMisc.toMap(RequisitionInfoIn));
			EmployeeBasicInfoSetup.create();
	        return "Success";
	    } catch (Exception e) { }
        return "Error";
	}  

	public static String confirmProfileBasicInfoChangeRequisition(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
		String requisitionId = request.getParameter("requisitionId");
		String comments = request.getParameter("comments");
        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
		GenericValue requisitionInfo = null;
		try { 
			requisitionInfo = delegator.findByPrimaryKey("BasicInfoUpdateRequ", UtilMisc.toMap("requisitionId", requisitionId));

			String partyId=requisitionInfo.get("partyId").toString();
			String contactMechTypeId=requisitionInfo.get("contactMechTypeId").toString();
			String tableName=requisitionInfo.get("tableName").toString();
			String fieldName=requisitionInfo.get("fieldName").toString();
			String fieldCurrentValue=requisitionInfo.get("fieldCurrentValue").toString();
			String fieldUpdatedValue=requisitionInfo.get("fieldUpdatedValue").toString();
			try {
				if(tableName.equalsIgnoreCase("PartyAndPerson")){
					Map<String, Object> EmplBasicInfoIn = FastMap.newInstance();
					EmplBasicInfoIn.put("partyId", partyId);
					EmplBasicInfoIn.put(fieldName, fieldUpdatedValue);
			    	try {
						GenericValue BasicInfoUpdateSetup = delegator
							.makeValue(tableName,
									UtilMisc.toMap(EmplBasicInfoIn));
						BasicInfoUpdateSetup.store();
				    } catch (Exception e) { }
				}
				else if(tableName.equalsIgnoreCase("PartyAndContactMech")){					
			    	List<EntityExpr> exprEmplBasicInfoIn = FastList.newInstance();
			    	exprEmplBasicInfoIn.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)); 
			    	exprEmplBasicInfoIn.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, contactMechTypeId));        
					List<GenericValue> listEmplBasicInfoIn = null;					
					try {
						listEmplBasicInfoIn = delegator.findList("PartyAndContactMech", EntityCondition.makeCondition(exprEmplBasicInfoIn, EntityOperator.AND), null, null, null, false);
						for (Map aParsed : listEmplBasicInfoIn){	
							aParsed.put("partyId", partyId);
							aParsed.put(fieldName, fieldUpdatedValue);
							GenericValue BasicInfoUpdateSetup = delegator
								.makeValue(tableName,
										UtilMisc.toMap(aParsed));
							BasicInfoUpdateSetup.store();
						}
					} 
					catch (GenericEntityException e) {
						e.printStackTrace();
					}
				}
				else if(tableName.equalsIgnoreCase("PartyAndPostalAddress")){					
			    	List<EntityExpr> exprEmplBasicInfoIn = FastList.newInstance();
			    	exprEmplBasicInfoIn.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)); 
			    	exprEmplBasicInfoIn.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, contactMechTypeId));        
					List<GenericValue> listEmplBasicInfoIn = null;					
					try {
						listEmplBasicInfoIn = delegator.findList("PartyAndPostalAddress", EntityCondition.makeCondition(exprEmplBasicInfoIn, EntityOperator.AND), null, null, null, false);
						for (Map aParsed : listEmplBasicInfoIn){	
							aParsed.put("partyId", partyId);
							aParsed.put(fieldName, fieldUpdatedValue);
							GenericValue BasicInfoUpdateSetup = delegator
								.makeValue(tableName,
										UtilMisc.toMap(aParsed));
							BasicInfoUpdateSetup.store();
						}
					} 
					catch (GenericEntityException e) {
						e.printStackTrace();
					}
				}
				else if(tableName.equalsIgnoreCase("PartyAndTelecomNumber")){
					
			    	List<EntityExpr> exprEmplBasicInfoIn = FastList.newInstance();
			    	exprEmplBasicInfoIn.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)); 
			    	List<GenericValue> listEmplBasicInfoIn = null;		
					
					try {
						listEmplBasicInfoIn = delegator.findList("PartyAndTelecomNumber", EntityCondition.makeCondition(exprEmplBasicInfoIn, EntityOperator.AND), null, null, null, false);
						for (Map aParsed : listEmplBasicInfoIn){	
							aParsed.put("partyId", partyId);
							aParsed.put(fieldName, fieldUpdatedValue);
							GenericValue BasicInfoUpdateSetup = delegator
								.makeValue(tableName,
										UtilMisc.toMap(aParsed));
							BasicInfoUpdateSetup.store();
						}
					} 
					catch (GenericEntityException e) {
						e.printStackTrace();
					}
				}
				else if(tableName.equalsIgnoreCase("CustomField")){

					Map<String, Object> EmplBasicInfoIn = FastMap.newInstance();
					EmplBasicInfoIn.put("customFieldId", contactMechTypeId);
					EmplBasicInfoIn.put("partyId", partyId);
					EmplBasicInfoIn.put("fieldValue", fieldUpdatedValue);
			    	try {
						GenericValue BasicInfoUpdateSetup = delegator
							.makeValue(tableName,
									UtilMisc.toMap(EmplBasicInfoIn));
						BasicInfoUpdateSetup.store();
				    } catch (Exception e) { }
				}
			}
			catch(Exception e){}
			
			///---------------------------- Requisition Confirm-----------------------//
	    	Map<String, Object> RequisitionInfoIn = FastMap.newInstance();
	    	RequisitionInfoIn.put("requisitionId", requisitionId);
	    	RequisitionInfoIn.put("partyId", partyId);
	    	RequisitionInfoIn.put("contactMechTypeId", contactMechTypeId);
	    	RequisitionInfoIn.put("tableName", tableName);
	    	RequisitionInfoIn.put("fieldName", fieldName);
	    	RequisitionInfoIn.put("fieldCurrentValue", fieldCurrentValue);
	    	RequisitionInfoIn.put("fieldUpdatedValue", fieldUpdatedValue);
	    	RequisitionInfoIn.put("comments", comments);
	    	RequisitionInfoIn.put("dateAdded", currentDatetime);
	    	RequisitionInfoIn.put("status", "Request Confirm");
		    try {
				GenericValue RequisitionInfoSetup = delegator
					.makeValue("BasicInfoUpdateRequ",
							UtilMisc.toMap(RequisitionInfoIn));
				RequisitionInfoSetup.store();

				/*String notificationId = delegator.getNextSeqId("BasicInfoUpdateNotification");
				String notificationTitle="Profile Information Updated";
				String notificationComments="Profile "+fieldName+ " field data updated from "+fieldCurrentValue +" to "+fieldUpdatedValue;
				///---------------------------- Notification Send-----------------------//
		    	Map<String, Object> NotificationInfoIn = FastMap.newInstance();
		    	NotificationInfoIn.put("notificationId", notificationId);
		    	NotificationInfoIn.put("requisitionId", requisitionId);
		    	NotificationInfoIn.put("partyId", partyId);
		    	NotificationInfoIn.put("notificationTitle", notificationTitle);
		    	NotificationInfoIn.put("comments", notificationComments);
		    	NotificationInfoIn.put("dateAdded", currentDatetime);
		    	NotificationInfoIn.put("status", "Confirm");
			    try {
					GenericValue EmployeeBasicInfoSetup = delegator
						.makeValue("BasicInfoUpdateNotification",
								UtilMisc.toMap(NotificationInfoIn));
					EmployeeBasicInfoSetup.create();
			        return "Success";
			    } catch (Exception e) { }*/
		    } catch (Exception e) { }

		}
		catch(Exception e){}
        return "Error";
	}
	public static String denyProfileBasicInfoChangeRequisition(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
		String requisitionId = request.getParameter("requisitionId");
		String comments = request.getParameter("comments");
        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
		GenericValue requisitionInfo = null;
		try { 
			requisitionInfo = delegator.findByPrimaryKey("BasicInfoUpdateRequ", UtilMisc.toMap("requisitionId", requisitionId));

			String partyId=requisitionInfo.get("partyId").toString();
			String contactMechTypeId=requisitionInfo.get("contactMechTypeId").toString();
			String tableName=requisitionInfo.get("tableName").toString();
			String fieldName=requisitionInfo.get("fieldName").toString();
			String fieldCurrentValue=requisitionInfo.get("fieldCurrentValue").toString();
			String fieldUpdatedValue=requisitionInfo.get("fieldUpdatedValue").toString();
		
			///---------------------------- Requisition Confirm-----------------------//
	    	Map<String, Object> RequisitionInfoIn = FastMap.newInstance();
	    	RequisitionInfoIn.put("requisitionId", requisitionId);
	    	RequisitionInfoIn.put("partyId", partyId);
	    	RequisitionInfoIn.put("contactMechTypeId", contactMechTypeId);
	    	RequisitionInfoIn.put("tableName", tableName);
	    	RequisitionInfoIn.put("fieldName", fieldName);
	    	RequisitionInfoIn.put("fieldCurrentValue", fieldCurrentValue);
	    	RequisitionInfoIn.put("fieldUpdatedValue", fieldUpdatedValue);
	    	RequisitionInfoIn.put("comments", comments);
	    	RequisitionInfoIn.put("dateAdded", currentDatetime);
	    	RequisitionInfoIn.put("status", "Request Rejected");
		    try {
				GenericValue RequisitionInfoSetup = delegator
					.makeValue("BasicInfoUpdateRequ",
							UtilMisc.toMap(RequisitionInfoIn));
				RequisitionInfoSetup.store();
		    } catch (Exception e) { }


			/*String notificationId = delegator.getNextSeqId("BasicInfoUpdateNotification");
			String notificationTitle="Profile Information Updated Requisition Deny";
			String notificationComments=comments;
			///---------------------------- Notification Send-----------------------//
	    	Map<String, Object> NotificationInfoIn = FastMap.newInstance();
	    	NotificationInfoIn.put("notificationId", notificationId);
	    	NotificationInfoIn.put("requisitionId", requisitionId);
	    	NotificationInfoIn.put("partyId", partyId);
	    	NotificationInfoIn.put("notificationTitle", notificationTitle);
	    	NotificationInfoIn.put("comments", notificationComments);
	    	NotificationInfoIn.put("dateAdded", currentDatetime);
	    	NotificationInfoIn.put("status", "Confirm");
		    try {
				GenericValue EmployeeBasicInfoSetup = delegator
					.makeValue("BasicInfoUpdateNotification",
							UtilMisc.toMap(NotificationInfoIn));
				EmployeeBasicInfoSetup.create();
		        return "Success";
		    } catch (Exception e) { }*/
		    
		}
		catch(Exception e){}
        return "Error";
	}


	public static String confirmAllProfileChangeRequiest(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();

	    List<EntityExpr> exprs = FastList.newInstance();
	    exprs.add(EntityCondition.makeCondition("status", EntityOperator.EQUALS, "Request Send")); 
        List<GenericValue> profileChangeNotificationList = null;
		try {
			profileChangeNotificationList = delegator.findList("BasicInfoUpdateRequ", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		int count=0;
		for (GenericValue emplProfileChangeReq : profileChangeNotificationList){

			//String requisitionId = request.getParameter("requisitionId");
			String requisitionId = profileChangeNotificationList.get(count).get("requisitionId").toString();
			String comments = request.getParameter("comments");
			count++;
	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());

			GenericValue requisitionInfo = null;
			try { 
				requisitionInfo = delegator.findByPrimaryKey("BasicInfoUpdateRequ", UtilMisc.toMap("requisitionId", requisitionId));

				String partyId=requisitionInfo.get("partyId").toString();
				String contactMechTypeId=requisitionInfo.get("contactMechTypeId").toString();
				String tableName=requisitionInfo.get("tableName").toString();
				String fieldName=requisitionInfo.get("fieldName").toString();
				String fieldCurrentValue=requisitionInfo.get("fieldCurrentValue").toString();
				String fieldUpdatedValue=requisitionInfo.get("fieldUpdatedValue").toString();
				try {
					if(tableName.equalsIgnoreCase("PartyAndPerson")){
						Map<String, Object> EmplBasicInfoIn = FastMap.newInstance();
						EmplBasicInfoIn.put("partyId", partyId);
						EmplBasicInfoIn.put(fieldName, fieldUpdatedValue);
				    	try {
							GenericValue BasicInfoUpdateSetup = delegator
								.makeValue(tableName,
										UtilMisc.toMap(EmplBasicInfoIn));
							BasicInfoUpdateSetup.store();
					    } catch (Exception e) { }
					}
					else if(tableName.equalsIgnoreCase("PartyAndContactMech")){					
				    	List<EntityExpr> exprEmplBasicInfoIn = FastList.newInstance();
				    	exprEmplBasicInfoIn.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)); 
				    	exprEmplBasicInfoIn.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, contactMechTypeId));        
						List<GenericValue> listEmplBasicInfoIn = null;					
						try {
							listEmplBasicInfoIn = delegator.findList("PartyAndContactMech", EntityCondition.makeCondition(exprEmplBasicInfoIn, EntityOperator.AND), null, null, null, false);
							for (Map aParsed : listEmplBasicInfoIn){	
								aParsed.put("partyId", partyId);
								aParsed.put(fieldName, fieldUpdatedValue);
								GenericValue BasicInfoUpdateSetup = delegator
									.makeValue(tableName,
											UtilMisc.toMap(aParsed));
								BasicInfoUpdateSetup.store();
							}
						} 
						catch (GenericEntityException e) {
							e.printStackTrace();
						}
					}
					else if(tableName.equalsIgnoreCase("PartyAndPostalAddress")){					
				    	List<EntityExpr> exprEmplBasicInfoIn = FastList.newInstance();
				    	exprEmplBasicInfoIn.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)); 
				    	exprEmplBasicInfoIn.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, contactMechTypeId));        
						List<GenericValue> listEmplBasicInfoIn = null;					
						try {
							listEmplBasicInfoIn = delegator.findList("PartyAndPostalAddress", EntityCondition.makeCondition(exprEmplBasicInfoIn, EntityOperator.AND), null, null, null, false);
							for (Map aParsed : listEmplBasicInfoIn){	
								aParsed.put("partyId", partyId);
								aParsed.put(fieldName, fieldUpdatedValue);
								GenericValue BasicInfoUpdateSetup = delegator
									.makeValue(tableName,
											UtilMisc.toMap(aParsed));
								BasicInfoUpdateSetup.store();
							}
						} 
						catch (GenericEntityException e) {
							e.printStackTrace();
						}
					}
					else if(tableName.equalsIgnoreCase("PartyAndTelecomNumber")){
						
				    	List<EntityExpr> exprEmplBasicInfoIn = FastList.newInstance();
				    	exprEmplBasicInfoIn.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)); 
				    	List<GenericValue> listEmplBasicInfoIn = null;		
						
						try {
							listEmplBasicInfoIn = delegator.findList("PartyAndTelecomNumber", EntityCondition.makeCondition(exprEmplBasicInfoIn, EntityOperator.AND), null, null, null, false);
							for (Map aParsed : listEmplBasicInfoIn){	
								aParsed.put("partyId", partyId);
								aParsed.put(fieldName, fieldUpdatedValue);
								GenericValue BasicInfoUpdateSetup = delegator
									.makeValue(tableName,
											UtilMisc.toMap(aParsed));
								BasicInfoUpdateSetup.store();
							}
						} 
						catch (GenericEntityException e) {
							e.printStackTrace();
						}
					}
					else if(tableName.equalsIgnoreCase("CustomField")){

						Map<String, Object> EmplBasicInfoIn = FastMap.newInstance();
						EmplBasicInfoIn.put("customFieldId", contactMechTypeId);
						EmplBasicInfoIn.put("partyId", partyId);
						EmplBasicInfoIn.put("fieldValue", fieldUpdatedValue);
				    	try {
							GenericValue BasicInfoUpdateSetup = delegator
								.makeValue(tableName,
										UtilMisc.toMap(EmplBasicInfoIn));
							BasicInfoUpdateSetup.store();
					    } catch (Exception e) { }
					}
				}
				catch(Exception e){}
				
				///---------------------------- Requisition Confirm-----------------------//
		    	Map<String, Object> RequisitionInfoIn = FastMap.newInstance();
		    	RequisitionInfoIn.put("requisitionId", requisitionId);
		    	RequisitionInfoIn.put("partyId", partyId);
		    	RequisitionInfoIn.put("contactMechTypeId", contactMechTypeId);
		    	RequisitionInfoIn.put("tableName", tableName);
		    	RequisitionInfoIn.put("fieldName", fieldName);
		    	RequisitionInfoIn.put("fieldCurrentValue", fieldCurrentValue);
		    	RequisitionInfoIn.put("fieldUpdatedValue", fieldUpdatedValue);
		    	RequisitionInfoIn.put("comments", comments);
		    	RequisitionInfoIn.put("dateAdded", currentDatetime);
		    	RequisitionInfoIn.put("status", "Request Confirm");
			    try {
					GenericValue RequisitionInfoSetup = delegator
						.makeValue("BasicInfoUpdateRequ",
								UtilMisc.toMap(RequisitionInfoIn));
					RequisitionInfoSetup.store();

					/*String notificationId = delegator.getNextSeqId("BasicInfoUpdateNotification");
					String notificationTitle="Profile Information Updated";
					String notificationComments="Profile "+fieldName+ " field data updated from "+fieldCurrentValue +" to "+fieldUpdatedValue;
					///---------------------------- Notification Send-----------------------//
			    	Map<String, Object> NotificationInfoIn = FastMap.newInstance();
			    	NotificationInfoIn.put("notificationId", notificationId);
			    	NotificationInfoIn.put("requisitionId", requisitionId);
			    	NotificationInfoIn.put("partyId", partyId);
			    	NotificationInfoIn.put("notificationTitle", notificationTitle);
			    	NotificationInfoIn.put("comments", notificationComments);
			    	NotificationInfoIn.put("dateAdded", currentDatetime);
			    	NotificationInfoIn.put("status", "Confirm");
				    try {
						GenericValue EmployeeBasicInfoSetup = delegator
							.makeValue("BasicInfoUpdateNotification",
									UtilMisc.toMap(NotificationInfoIn));
						EmployeeBasicInfoSetup.create();
				        return "Success";
				    } catch (Exception e) { }*/
			    } catch (Exception e) { }

			}
			catch(Exception e){}
		}
        
        
        
        return "Error";
	}
	
	
	
	
	public static Map<String, Object> ApprovalNotificationRemainder(DispatchContext dctx, Map<String, ? extends Object> context) {
		
	    Map<String, Object> result = ServiceUtil.returnSuccess();
	    Delegator delegator = dctx.getDelegator();
		String partyId = (String) context.get("partyId");
		List<GenericValue> remainderList = null;
	    List<EntityExpr> exprs = FastList.newInstance();
		exprs.add(EntityCondition.makeCondition("approverPartyId", EntityOperator.EQUALS, partyId));
		exprs.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_CREATED"));
		try {
			remainderList = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		List<Map> cfMap = FastList.newInstance();
		for (Map aParsed : remainderList){
			Map record = FastMap.newInstance();
			record.putAll(aParsed);
			cfMap.add(record);
		}
	    result.put("remainderList", cfMap);
        return result;
	}
	  public static String getAnEmployeeNameByPartyId(Delegator delegator, String partyId) {
	        List<GenericValue> partyName = null;
	        try {
	        	partyName = delegator.findByAnd("PartyAndPersonAndFulfillment", UtilMisc.toMap("partyId", partyId));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String firstName = "";
	        String lastName= "";
	        if (UtilValidate.isNotEmpty(partyName)) {
	            if (UtilValidate.isNotEmpty(partyName.get(0).get("firstName"))) {
	            	firstName = partyName.get(0).get("firstName").toString();
	            }
	        }
	        if (UtilValidate.isNotEmpty(partyName)) {
	            if (UtilValidate.isNotEmpty(partyName.get(0).get("lastName"))) {
	            	lastName = partyName.get(0).get("lastName").toString();
	            }
	        }
	        String Name = firstName +" " +lastName;
	        return Name;
	    }
	  
	  

	   public static String EmplPositionChange(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();

	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());		
			String partyId = request.getParameter("partyId");
			String currentPosition = request.getParameter("currentPosition");
			String employeeCurrentPositionInfo = request.getParameter("employeeCurrentPositionInfo");
			String employeeJoiningDate = request.getParameter("employeeJoiningDate");
			String emplPositionId = request.getParameter("emplPositionId");
			
	        
			List<EntityExpr> exprs = FastList.newInstance(); 
			exprs.add(EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS, currentPosition));
			exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			List<GenericValue> emplPositionFulfillmentList = null;
			try {
				emplPositionFulfillmentList = delegator.findList("EmplPositionFulfillment", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			String fDate="";
			String tDate="";
			Timestamp fromDate=null; 
			for (GenericValue emplPosition : emplPositionFulfillmentList){	
				fDate = emplPosition.get("fromDate").toString();  
				fromDate = (Timestamp)emplPosition.get("fromDate");
			}

		    //Add New Employee Position
			Map<String, Object> emplCurrentChangePositionIn = FastMap.newInstance();
			emplCurrentChangePositionIn.put("emplPositionId", emplPositionId);
			emplCurrentChangePositionIn.put("partyId", partyId);
			emplCurrentChangePositionIn.put("fromDate", currentDatetime);
			emplCurrentChangePositionIn.put("comments", "Employee position change into "+currentPosition+" to "+emplPositionId);
		    try {
				GenericValue emplCurrentChangePositionInSetup = delegator
					.makeValue("EmplPositionFulfillment",
							UtilMisc.toMap(emplCurrentChangePositionIn));
				emplCurrentChangePositionInSetup.create(); 
				
		    } catch (Exception e) { }
		    
			//Store Current Employee Position in History Table
			Map<String, Object> emplCurrentPositionBackupIn = FastMap.newInstance();
			emplCurrentPositionBackupIn.put("emplPositionId", currentPosition);
			emplCurrentPositionBackupIn.put("partyId", partyId);
			emplCurrentPositionBackupIn.put("fromDate", fromDate);
			emplCurrentPositionBackupIn.put("comments", "Employee position change into "+currentPosition+" to "+emplPositionId);
		    try {
				GenericValue emplCurrentPositionBackupInSetup = delegator
					.makeValue("EmplPositionFulfillmentHis",
							UtilMisc.toMap(emplCurrentPositionBackupIn));
				emplCurrentPositionBackupInSetup.create(); 
				
		    } catch (Exception e) { }
		    
		    
		    //Delete Employee Existing  Position
			Map<String, Object> emplCurrentPositionIn = FastMap.newInstance();
			emplCurrentPositionIn.put("emplPositionId", currentPosition);
			emplCurrentPositionIn.put("partyId", partyId);
			emplCurrentPositionIn.put("fromDate", fromDate);
		    try {
				GenericValue emplCurrentPositionInSetup = delegator
					.makeValue("EmplPositionFulfillment",
							UtilMisc.toMap(emplCurrentPositionIn));
					emplCurrentPositionInSetup.remove(); 
				
		    } catch (Exception e) { }


		    request.setAttribute("partyId", partyId);
	        request.setAttribute("_EVENT_MESSAGE_", "Change Employee Position Successfully");
	        return "success";
	    }
	  
	  
}