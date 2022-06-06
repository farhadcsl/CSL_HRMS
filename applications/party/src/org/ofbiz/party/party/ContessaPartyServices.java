package org.ofbiz.party.party;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

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

/**
 * Services for Party/Person/Group maintenance
 */
public class ContessaPartyServices {

    public static final String module = ContessaPartyServices.class.getName();
    public static final String resource = "PartyUiLabels";
    public static final String resourceError = "PartyErrorUiLabels";

    
/**
 * added new method for Contessa spcific party find based on findParty method from party service
 * @param dctx
 * @param context
 * @return result
 */
    public static Map<String, Object> updatePersonAddressAndTelecom(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        
        String address1 = (String) context.get("address1");
        String address2 = (String) context.get("address2");
        String address3 = (String) context.get("address3");
        String city = (String) context.get("city");
        String city1 = (String) context.get("city1");
        String city2 = (String) context.get("city2");
        String country1 = (String) context.get("country1");
        String country2 = (String) context.get("country2");
        String postalCode = (String) context.get("postalCode");
        String infoString = (String) context.get("infoString");
        String contactNumber = (String) context.get("contactNumber");
        String parmanentAddress = (String) context.get("parmanentAddress");
        
        /* Add Division District Thana*/
        
        String countryGeoId = (String) context.get("countryGeoId");
        String divisionGeoId = (String) context.get("divisionGeoId");
        String districtGeoId = (String) context.get("districtGeoId");
        String thanaGeoId = (String) context.get("thanaGeoId");
        
        String partyId = getPartyId(context);
        if (UtilValidate.isEmpty(partyId)) {
            return ServiceUtil.returnError(UtilProperties.getMessage(ServiceUtil.resource, 
                    "serviceUtil.party_id_missing", locale));
        }

        GenericValue person = null;
        GenericValue party = null;

        try {
            person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
            party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "person.update.read_failure", new Object[] { e.getMessage() }, locale));
        }

        if (person == null || party == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "person.update.not_found", locale));
        }

        // update status by separate service
        String oldStatusId = party.getString("statusId");
        if (party.get("statusId") == null) { // old records
            party.set("statusId", "PARTY_ENABLED");
        }

        person.setNonPKFields(context);
        party.setNonPKFields(context);

        party.set("statusId", oldStatusId);

        try {
            person.store();
            party.store();
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "person.update.write_failure", new Object[] { e.getMessage() }, locale));
        }

        if (UtilValidate.isNotEmpty(context.get("statusId")) && !context.get("statusId").equals(oldStatusId)) {
            try {
                dispatcher.runSync("setPartyStatus", UtilMisc.toMap("partyId", partyId, "statusId", context.get("statusId"), "userLogin", context.get("userLogin")));
            } catch (GenericServiceException e) {
                Debug.logWarning(e.getMessage(), module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                        "person.update.write_failure", new Object[] { e.getMessage() }, locale));
            }
        }
        //update the postal address and telecom number
        List<GenericValue> listUpdatablePartyContactMech = null;
        GenericValue updatablePartyContactMech = null; 
        String updatableContactMechTypeId = "POSTAL_ADDRESS"; 
        
        //get the value of updatable contactMech
        List<EntityExpr> exprs = FastList.newInstance();
        exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        exprs.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, updatableContactMechTypeId));
        try {
       	 listUpdatablePartyContactMech = delegator.findList("PartyAndPostalAddress", EntityCondition.makeCondition(exprs, EntityOperator.AND), null,null, null, false);//
       	 //("PartyAndPostalAddress", UtilMisc.toMap("contactMechId", mainContactMechId));
       	 
       	 //update the all 4 value into postal address table
       		 /*if(UtilValidate.isNotEmpty(contactMechId)){
       			 updatablePartyContactMech.set("contactMechId", contactMechId);
       		 }*/
       	 if (UtilValidate.isNotEmpty(listUpdatablePartyContactMech)){
       	 	GenericValue backupPartyContactMech = EntityUtil.getFirst(listUpdatablePartyContactMech);
       	 	if (UtilValidate.isEmpty(backupPartyContactMech)){
          		 return ServiceUtil.returnError("Changed Present Address is not found");
          		
          		 
       	 	}
       	 	updatablePartyContactMech = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", backupPartyContactMech.get("contactMechId")));
       		 if(UtilValidate.isNotEmpty(address1)){
       			 updatablePartyContactMech.set("address1", address1);
       		 }
       		 if(UtilValidate.isNotEmpty(city)){
       			 updatablePartyContactMech.set("city", city);
       		 }
       		 if(UtilValidate.isNotEmpty(city)){
       			 updatablePartyContactMech.set("postalCode", postalCode);
       		 }
       		 
       		if(UtilValidate.isNotEmpty(countryGeoId)){
      			 updatablePartyContactMech.set("countryGeoId", countryGeoId);
      		 }
       		if(UtilValidate.isNotEmpty(divisionGeoId)){
      			 updatablePartyContactMech.set("divisionGeoId", divisionGeoId);
      		 }
       		if(UtilValidate.isNotEmpty(districtGeoId)){
      			 updatablePartyContactMech.set("districtGeoId", districtGeoId);
      		 }
       		if(UtilValidate.isNotEmpty(thanaGeoId)){
      			 updatablePartyContactMech.set("thanaGeoId", thanaGeoId);
      		 }
       		 /*if(UtilValidate.isNotEmpty(mobileNo)){
       			 updatablePartyContactMech.set("mobileNo", mobileNo);
       		 }*/
       		 
       		 updatablePartyContactMech.store();
       	 }
       	 else{
       	String contactMechId = delegator.getNextSeqId("ContactMech");
       		Timestamp formDate = UtilDateTime.nowTimestamp();
       	  Map<String, Object> PostalContactMechIn = FastMap.newInstance();
        
       		PostalContactMechIn.put("contactMechId", contactMechId);
       		PostalContactMechIn.put("contactMechTypeId", "POSTAL_ADDRESS");
       		try {
      			GenericValue createPostalContactValue = delegator
      					.makeValue("ContactMech",
      							UtilMisc.toMap(PostalContactMechIn));
      			createPostalContactValue.create();
              } catch (Exception e) {

              	e.printStackTrace();
              }
       		Map<String, Object> PostalPartyContactMechIn = FastMap.newInstance();
       		
       		PostalPartyContactMechIn.put("partyId", partyId);
       		PostalPartyContactMechIn.put("contactMechId", contactMechId);
       		PostalPartyContactMechIn.put("fromDate", formDate);
       		try {
      			GenericValue createPostalPartyContactValue = delegator
      					.makeValue("PartyContactMech",
      							UtilMisc.toMap(PostalPartyContactMechIn));
      			createPostalPartyContactValue.create();
              } catch (Exception e) {

              	e.printStackTrace();
              }
       		Map<String, Object> PostalAddressIn = FastMap.newInstance();
       		PostalAddressIn.put("contactMechId", contactMechId);
       		PostalAddressIn.put("address1", address1);
       		PostalAddressIn.put("city", city);
       		PostalAddressIn.put("postalCode", postalCode);
       		PostalAddressIn.put("countryGeoId", countryGeoId);
       		
       		try {
      			GenericValue createPostalAddresssValue = delegator
      					.makeValue("PostalAddress",
      							UtilMisc.toMap(PostalAddressIn));
      			createPostalAddresssValue.create();
              } catch (Exception e) {

              	e.printStackTrace();
              }
          
       		 
       	 }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
            return ServiceUtil.returnError("Changed Present Address could not be Updated");
        }
        
        //update parmanent address
        try {
			List<GenericValue>  parmanentAddressList = delegator.findByAnd("PartyAndPostalAddress", UtilMisc.toMap("partyId",partyId,"contactMechTypeId","PERMANENT_ADDRESS"));
			if(UtilValidate.isNotEmpty(parmanentAddressList)){
			GenericValue parmanentAddressValue = EntityUtil.getFirst(parmanentAddressList);
			if (UtilValidate.isEmpty(parmanentAddressValue)){
         		 return ServiceUtil.returnError("Changed Parmanent Address is not found");
      	 	}
			GenericValue updatableParmanentAddress = delegator.findByPrimaryKey("PostalAddress",UtilMisc.toMap("contactMechId",parmanentAddressValue.get("contactMechId")));
			
			if(UtilValidate.isNotEmpty(address3)){
				updatableParmanentAddress.set("address1", address3);
			}
			if(UtilValidate.isNotEmpty(city2)){
				updatableParmanentAddress.set("city", city2);
      		 }
			if(UtilValidate.isNotEmpty(country2)){
				updatableParmanentAddress.set("countryGeoId", country2);
      		 }
			updatableParmanentAddress.store();
			}
			else {
				
			       	String contactMechId = delegator.getNextSeqId("ContactMech");
			       		Timestamp formDate = UtilDateTime.nowTimestamp();
			       	  Map<String, Object> PostalContactMechIn = FastMap.newInstance();
			        
			       		PostalContactMechIn.put("contactMechId", contactMechId);
			       		PostalContactMechIn.put("contactMechTypeId", "PERMANENT_ADDRESS");
			       		try {
			      			GenericValue createPostalContactValue = delegator
			      					.makeValue("ContactMech",
			      							UtilMisc.toMap(PostalContactMechIn));
			      			createPostalContactValue.create();
			              } catch (Exception e) {

			              	e.printStackTrace();
			              }
			       		Map<String, Object> PostalPartyContactMechIn = FastMap.newInstance();
			       		
			       		PostalPartyContactMechIn.put("partyId", partyId);
			       		PostalPartyContactMechIn.put("contactMechId", contactMechId);
			       		PostalPartyContactMechIn.put("fromDate", formDate);
			       		try {
			      			GenericValue createPostalPartyContactValue = delegator
			      					.makeValue("PartyContactMech",
			      							UtilMisc.toMap(PostalPartyContactMechIn));
			      			createPostalPartyContactValue.create();
			              } catch (Exception e) {

			              	e.printStackTrace();
			              }
			       		Map<String, Object> PostalAddressIn = FastMap.newInstance();
			       		PostalAddressIn.put("contactMechId", contactMechId);
			       		PostalAddressIn.put("address1", address3);
			       		PostalAddressIn.put("city", city2);
			       		PostalAddressIn.put("postalCode", postalCode);
			       		PostalAddressIn.put("countryGeoId", country2);
			       		
			       		try {
			      			GenericValue createPostalAddresssValue = delegator
			      					.makeValue("PostalAddress",
			      							UtilMisc.toMap(PostalAddressIn));
			      			createPostalAddresssValue.create();
			              } catch (Exception e) {

			              	e.printStackTrace();
			              }
			          
			       		 
			       	 

			}
		} catch (GenericEntityException e1) {
			 return ServiceUtil.returnError("Changed Parmanent Address could not be Updated");
		}
        //update Mailing Address
        
        /*try {
			
        	List<GenericValue>  mailingAddressList = delegator.findByAnd("PartyAndPostalAddress", UtilMisc.toMap("partyId",partyId,"contactMechTypeId","MAILING_ADDRESS"));
			if (UtilValidate.isNotEmpty(mailingAddressList)){
        	GenericValue mailingAddressValue = EntityUtil.getFirst(mailingAddressList);
			GenericValue updatableMailingAddress = delegator.findByPrimaryKey("PostalAddress",UtilMisc.toMap("contactMechId",mailingAddressValue.get("contactMechId")));
			
			if(UtilValidate.isNotEmpty(address2)){
				updatableMailingAddress.set("address1", address2);
			}
			if(UtilValidate.isNotEmpty(city1)){
				updatableMailingAddress.set("city", city1);
      		 }
			if(UtilValidate.isNotEmpty(country1)){
				updatableMailingAddress.set("countryGeoId", country1);
      		 }
			updatableMailingAddress.store();
			}
		} catch (GenericEntityException e1) {
			 return ServiceUtil.returnError("Changed Parmanent Address could not be Updated");
		}*/
        
        //update info string
        
        try {
 			List<GenericValue> listPartyContactMechs = delegator.findList("PartyContactMech", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),null,null,null,false);
 			String contactMechIdToCheck = "";
 			GenericValue contactMech = null;
 			for(GenericValue val : listPartyContactMechs){
 				contactMechIdToCheck = (String)val.get("contactMechId");
 				List<EntityExpr> exprt = FastList.newInstance();
                 exprt.add(EntityCondition.makeCondition("contactMechId", EntityOperator.EQUALS, contactMechIdToCheck));
                 exprt.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "EMAIL_ADDRESS"));
 				
 				List<GenericValue> listContactMechs = delegator.findList("ContactMech", EntityCondition.makeCondition(exprt, EntityOperator.AND),null,null,null,false);
 				
 				if (UtilValidate.isNotEmpty(listContactMechs)) {
					contactMech = EntityUtil.getFirst(listContactMechs);
				}
 			}
 			
 			if (UtilValidate.isNotEmpty(contactMech)){
 				contactMech.set("infoString", infoString);
 				contactMech.store();
        	 	}
 			
 			
 		} catch (GenericEntityException e) {
 			 return ServiceUtil.returnError("sendFrom emailAddress is not found");
 		}
        List<GenericValue> listUpdatableTelecomPartyContactMech = null;
        GenericValue updatableTelecomPartyContactMech = null; 
        String updatableTelecomContactMechTypeId = "TELECOM_NUMBER"; 
        
        //get the value of updatable contactMech
        List<EntityExpr> expr1 = FastList.newInstance();
        expr1.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        /*expr1.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, updatableTelecomContactMechTypeId));*/
        try {
       	 listUpdatableTelecomPartyContactMech = delegator.findList("PartyAndTelecomNumber", EntityCondition.makeCondition(expr1, EntityOperator.AND), null, UtilMisc.toList("fromDate"), null, false);//
       	 //("PartyAndPostalAddress", UtilMisc.toMap("contactMechId", mainContactMechId));
       	 
       	 //update the all 4 value into postal address table
       		 /*if(UtilValidate.isNotEmpty(contactMechId)){
       			 updatablePartyContactMech.set("contactMechId", contactMechId);
       		 }*/
       	 if (UtilValidate.isNotEmpty(listUpdatableTelecomPartyContactMech)){
       	 	GenericValue backupPartyContactMech = EntityUtil.getFirst(listUpdatableTelecomPartyContactMech);
       	 	if (UtilValidate.isEmpty(backupPartyContactMech)){
          		 return ServiceUtil.returnError("Changed Telecom Number is not found");
          		
          		 
       	 	}
       	 	updatableTelecomPartyContactMech = delegator.findByPrimaryKey("TelecomNumber", UtilMisc.toMap("contactMechId", backupPartyContactMech.get("contactMechId")));
       		 
       		 if(UtilValidate.isNotEmpty(contactNumber)){
       			 updatableTelecomPartyContactMech.set("contactNumber", contactNumber);
       		 }
       		 
       		 updatableTelecomPartyContactMech.store();
       	 }
       	 else {

       		String contactMechId = delegator.getNextSeqId("ContactMech");
       		Timestamp formDate = UtilDateTime.nowTimestamp();
       	  Map<String, Object> TelecomContactMechIn = FastMap.newInstance();
        
       		TelecomContactMechIn.put("contactMechId", contactMechId);
       		TelecomContactMechIn.put("contactMechTypeId", "TELECOM_NUMBER");
       		try {
      			GenericValue createTelecomContactValue = delegator
      					.makeValue("ContactMech",
      							UtilMisc.toMap(TelecomContactMechIn));
      			createTelecomContactValue.create();
              } catch (Exception e) {

              	e.printStackTrace();
              }
       		Map<String, Object> TelecomPartyContactMechIn = FastMap.newInstance();
       		
       		TelecomPartyContactMechIn.put("partyId", partyId);
       		TelecomPartyContactMechIn.put("contactMechId", contactMechId);
       		TelecomPartyContactMechIn.put("fromDate", formDate);
       		try {
      			GenericValue createTelecomPartyContactValue = delegator
      					.makeValue("PartyContactMech",
      							UtilMisc.toMap(TelecomPartyContactMechIn));
      			createTelecomPartyContactValue.create();
              } catch (Exception e) {

              	e.printStackTrace();
              }
       		Map<String, Object> TelecomAddressIn = FastMap.newInstance();
       		TelecomAddressIn.put("contactMechId", contactMechId);
       		TelecomAddressIn.put("contactNumber", contactNumber);
       	
       		
       		try {
      			GenericValue createTelecomAddresssValue = delegator
      					.makeValue("TelecomNumber",
      							UtilMisc.toMap(TelecomAddressIn));
      			createTelecomAddresssValue.create();
              } catch (Exception e) {

              	e.printStackTrace();
              }
          
       		 

       		 
       	 }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
            return ServiceUtil.returnError("Changed Telecom Number could not be Updated");
        }
        
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        result.put(ModelService.SUCCESS_MESSAGE, 
                UtilProperties.getMessage(resourceError, "person.update.success", locale));
        result.put("partyId",partyId);
        return result;
    }
    
    
    public static Map<String, Object> findContessaParty(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        //GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        
        String extInfo = (String) context.get("extInfo");

        // get the role types
        try {
            List<GenericValue> roleTypes = delegator.findList("RoleType", null, null, UtilMisc.toList("description"), null, false);
            result.put("roleTypes", roleTypes);
        } catch (GenericEntityException e) {
            String errMsg = "Error looking up RoleTypes: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "PartyLookupRoleTypeError",
                    UtilMisc.toMap("errMessage", e.toString()), locale));
        }

        // current role type
        String roleTypeId;
        try {
            roleTypeId = (String) context.get("roleTypeId");
            if (UtilValidate.isNotEmpty(roleTypeId)) {
                GenericValue currentRole = delegator.findByPrimaryKeyCache("RoleType", UtilMisc.toMap("roleTypeId", roleTypeId));
                result.put("currentRole", currentRole);
            }
        } catch (GenericEntityException e) {
            String errMsg = "Error looking up current RoleType: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "PartyLookupRoleTypeError",
                    UtilMisc.toMap("errMessage", e.toString()), locale));
        }

        //get party types
        try {
            List<GenericValue> partyTypes = delegator.findList("PartyType", null, null, UtilMisc.toList("description"), null, false);
            result.put("partyTypes", partyTypes);
        } catch (GenericEntityException e) {
            String errMsg = "Error looking up PartyTypes: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "PartyLookupPartyTypeError",
                    UtilMisc.toMap("errMessage", e.toString()), locale));
        }

        // current party type
        String partyTypeId;
        try {
            partyTypeId = (String) context.get("partyTypeId");
            if (UtilValidate.isNotEmpty(partyTypeId)) {
                GenericValue currentPartyType = delegator.findByPrimaryKeyCache("PartyType", UtilMisc.toMap("partyTypeId", partyTypeId));
                result.put("currentPartyType", currentPartyType);
            }
        } catch (GenericEntityException e) {
            String errMsg = "Error looking up current PartyType: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "PartyLookupPartyTypeError",
                    UtilMisc.toMap("errMessage", e.toString()), locale));
        }

        // current state
        String stateProvinceGeoId;
        try {
            stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
            if (UtilValidate.isNotEmpty(stateProvinceGeoId)) {
                GenericValue currentStateGeo = delegator.findByPrimaryKeyCache("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId));
                result.put("currentStateGeo", currentStateGeo);
            }
        } catch (GenericEntityException e) {
            String errMsg = "Error looking up current stateProvinceGeo: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "PartyLookupStateProvinceGeoError",
                    UtilMisc.toMap("errMessage", e.toString()), locale));
        }

        // set the page parameters
        int viewIndex = 0;
        try {
            viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
        } catch (Exception e) {
            viewIndex = 0;
        }
        result.put("viewIndex", Integer.valueOf(viewIndex));

        int viewSize = 20;
        try {
            viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
        } catch (Exception e) {
            viewSize = 20;
        }
        result.put("viewSize", Integer.valueOf(viewSize));

        // get the lookup flag
        String lookupFlag = (String) context.get("lookupFlag");

        // blank param list
        String paramList = "";

        List<GenericValue> partyList = null;
        int partyListSize = 0;
        int lowIndex = 0;
        int highIndex = 0;

        if ("Y".equals(lookupFlag)) {
            String showAll = (context.get("showAll") != null ? (String) context.get("showAll") : "N");
            paramList = paramList + "&lookupFlag=" + lookupFlag + "&showAll=" + showAll + "&extInfo=" + extInfo;

            // create the dynamic view entity
            //String viewName = "";
           // DynamicViewEntity dynamicView = new DynamicViewEntity();
            
            // default view settings
           // viewName = "Party";
           /* dynamicView.addMemberEntity("PT", "Party");
           
            dynamicView.addAlias("PT", "partyId");
            dynamicView.addAlias("PT", "statusId");
            dynamicView.addAlias("PT", "partyTypeId");
            dynamicView.addAlias("PT", "createdDate");
            dynamicView.addAlias("PT", "lastModifiedDate");
            dynamicView.addRelation("one-nofk", "", "PartyType", ModelKeyMap.makeKeyMapList("partyTypeId"));
            dynamicView.addRelation("many", "", "UserLogin", ModelKeyMap.makeKeyMapList("partyId"));*/

            // define the main condition & expression list
            List<EntityCondition> andExprs = FastList.newInstance();
            EntityCondition mainCond = null;

            List<String> orderBy = FastList.newInstance();
            Set<String> fieldsToSelect = FastSet.newInstance();
           // List<String> fieldsToSelect = FastList.newInstance();
            // fields we need to select; will be used to set distinct
            fieldsToSelect.add("partyId");
            fieldsToSelect.add("statusId");
            fieldsToSelect.add("partyTypeId");
            fieldsToSelect.add("createdDate");
            fieldsToSelect.add("lastModifiedDate");

            // filter on parties that have relationship with logged in user
           
            /*String partyRelationshipTypeId = (String) context.get("partyRelationshipTypeId");
            if (UtilValidate.isNotEmpty(partyRelationshipTypeId)) {
                // add relation to view
                dynamicView.addMemberEntity("PRSHP", "PartyRelationship");
                dynamicView.addAlias("PRSHP", "partyIdTo");
                dynamicView.addAlias("PRSHP", "partyIdFrom");
                dynamicView.addAlias("PRSHP", "partyRelationshipTypeId");
                dynamicView.addViewLink("PT", "PRSHP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId", "partyIdTo"));
                List<String> ownerPartyIds = UtilGenerics.cast(context.get("ownerPartyIds"));
                EntityCondition relationshipCond = null;
                if (UtilValidate.isEmpty(ownerPartyIds)) {
                    String partyIdFrom = userLogin.getString("partyId");
                    paramList = paramList + "&partyIdFrom=" + partyIdFrom;
                    relationshipCond = EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyIdFrom"), EntityOperator.EQUALS, EntityFunction.UPPER(partyIdFrom));
                } else {
                    relationshipCond = EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, ownerPartyIds);
                }
                dynamicView.addAlias("PRSHP", "partyIdFrom");
                // add the expr
                andExprs.add(EntityCondition.makeCondition(
                        relationshipCond, EntityOperator.AND,
                        EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyRelationshipTypeId"), EntityOperator.EQUALS, EntityFunction.UPPER(partyRelationshipTypeId))));
                fieldsToSelect.add("partyIdTo");
            }*/
            

            // get the params
            String partyId = (String) context.get("partyId");
            String statusId = (String) context.get("statusId");
            String userLoginId = (String) context.get("userLoginId");
            String firstName = (String) context.get("firstName");
            String lastName = (String) context.get("lastName");
            String groupName = (String) context.get("groupName");
            String defaultOrganizationPartyId = (String) context.get("defaultOrganizationPartyId");

            if (!"Y".equals(showAll)) {
            	
            	// filter on parties that have relationship with logged in user
               
                if (UtilValidate.isNotEmpty(defaultOrganizationPartyId)){
                	paramList = paramList + "&partyIdFrom=" + defaultOrganizationPartyId;
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyIdFrom"), EntityOperator.EQUALS, EntityFunction.UPPER(defaultOrganizationPartyId)));
                    fieldsToSelect.add("partyIdFrom");
                	
                }
             
                // check for a partyId
                if (UtilValidate.isNotEmpty(partyId)) {
                    paramList = paramList + "&partyId=" + partyId;
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"), EntityOperator.LIKE, EntityFunction.UPPER("%"+partyId+"%")));
                }

                // now the statusId - send ANY for all statuses; leave null for just enabled; or pass a specific status
                if (statusId != null) {
                    paramList = paramList + "&statusId=" + statusId;
                    if (!"ANY".equalsIgnoreCase(statusId)) {
                        andExprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
                    }
                } else {
                    // NOTE: _must_ explicitly allow null as it is not included in a not equal in many databases... odd but true
                    andExprs.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED")));
                    //andExprs.add( EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"));
                }
                // check for partyTypeId
                if (partyTypeId != null && !"ANY".equals(partyTypeId)) {
                    paramList = paramList + "&partyTypeId=" + partyTypeId;
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyTypeId"), EntityOperator.LIKE, EntityFunction.UPPER("%"+partyTypeId+"%")));
                }

                // ----
                // UserLogin Fields
                // ----

                // filter on user login
                if (UtilValidate.isNotEmpty(userLoginId)) {
                    paramList = paramList + "&userLoginId=" + userLoginId;


                    // add the expr
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("userLoginId"), EntityOperator.LIKE, EntityFunction.UPPER("%"+userLoginId+"%")));

                    fieldsToSelect.add("userLoginId");
                }else{
                	
                    fieldsToSelect.add("userLoginId");
                }

                // ----
                // PartyGroup Fields
                // ----

                // filter on groupName
               /* if (UtilValidate.isNotEmpty(groupName)) {
                    paramList = paramList + "&groupName=" + groupName;

                    // modify the dynamic view
                    dynamicView.addMemberEntity("PG", "PartyGroup");
                    dynamicView.addAlias("PG", "groupName");
                    dynamicView.addViewLink("PT", "PG", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));

                    // add the expr
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("groupName"), EntityOperator.LIKE, EntityFunction.UPPER("%"+groupName+"%")));

                    fieldsToSelect.add("groupName");
                }*/

                // ----
                // Person Fields
                // ----

                // modify the dynamic view
                if (UtilValidate.isNotEmpty(firstName) || UtilValidate.isNotEmpty(lastName)) {
                    fieldsToSelect.add("firstName");
                    fieldsToSelect.add("lastName");
                   
                    orderBy.add("lastName");
                    orderBy.add("firstName");
                }
                //only to get mobile no add in a view
               
                fieldsToSelect.add("mobileno");
               
            
                // filter on firstName
                if (UtilValidate.isNotEmpty(firstName)) {
                    paramList = paramList + "&firstName=" + firstName;
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"), EntityOperator.LIKE, EntityFunction.UPPER("%"+firstName+"%")));
                }

                // filter on lastName
                if (UtilValidate.isNotEmpty(lastName)) {
                    paramList = paramList + "&lastName=" + lastName;
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastName"), EntityOperator.LIKE, EntityFunction.UPPER("%"+lastName+"%")));
                }

                // ----
                // RoleType Fields
                // ----

                // filter on role member
                if (roleTypeId != null && !"ANY".equals(roleTypeId)) {
                    paramList = paramList + "&roleTypeId=" + roleTypeId;
                    // add the expr
                    andExprs.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));

                    fieldsToSelect.add("roleTypeId");
                }

                // ----
                // InventoryItem Fields
                // ----

                

                // ----
                // PostalAddress fields
                // ----
                if ("P".equals(extInfo)) {
                   

                    // filter on address1
                    String address1 = (String) context.get("address1");
                    if (UtilValidate.isNotEmpty(address1)) {
                        paramList = paramList + "&address1=" + address1;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("address1"), EntityOperator.LIKE, EntityFunction.UPPER("%" + address1 + "%")));
                    }

                    // filter on address2
                    String address2 = (String) context.get("address2");
                    if (UtilValidate.isNotEmpty(address2)) {
                        paramList = paramList + "&address2=" + address2;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("address2"), EntityOperator.LIKE, EntityFunction.UPPER("%" + address2 + "%")));
                    }

                    // filter on city
                    String city = (String) context.get("city");
                    if (UtilValidate.isNotEmpty(city)) {
                        paramList = paramList + "&city=" + city;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("city"), EntityOperator.LIKE, EntityFunction.UPPER("%" + city + "%")));
                    }

                    // filter on state geo
                    if (stateProvinceGeoId != null && !"ANY".equals(stateProvinceGeoId)) {
                        paramList = paramList + "&stateProvinceGeoId=" + stateProvinceGeoId;
                        andExprs.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, stateProvinceGeoId));
                    }

                    // filter on postal code
                    String postalCode = (String) context.get("postalCode");
                    if (UtilValidate.isNotEmpty(postalCode)) {
                        paramList = paramList + "&postalCode=" + postalCode;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("postalCode"), EntityOperator.LIKE, EntityFunction.UPPER("%" + postalCode + "%")));
                    }

                    fieldsToSelect.add("postalCode");
                    fieldsToSelect.add("city");
                    fieldsToSelect.add("stateProvinceGeoId");
                }

                // ----
                // Generic CM Fields
                // ----
                if ("O".equals(extInfo)) {
                    

                    // filter on infoString
                    String infoString = (String) context.get("infoString");
                    if (UtilValidate.isNotEmpty(infoString)) {
                        paramList = paramList + "&infoString=" + infoString;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("infoString"), EntityOperator.LIKE, EntityFunction.UPPER("%"+infoString+"%")));
                        fieldsToSelect.add("infoString");
                    }

                }

                // ----
                // TelecomNumber Fields
                // ----
                if ("T".equals(extInfo)) {
                   

                    // filter on countryCode
                    String countryCode = (String) context.get("countryCode");
                    if (UtilValidate.isNotEmpty(countryCode)) {
                        paramList = paramList + "&countryCode=" + countryCode;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("countryCode"), EntityOperator.EQUALS, EntityFunction.UPPER(countryCode)));
                    }

                    // filter on areaCode
                    String areaCode = (String) context.get("areaCode");
                    if (UtilValidate.isNotEmpty(areaCode)) {
                        paramList = paramList + "&areaCode=" + areaCode;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("areaCode"), EntityOperator.EQUALS, EntityFunction.UPPER(areaCode)));
                    }

                    // filter on contact number
                    String contactNumber = (String) context.get("contactNumber");
                    if (UtilValidate.isNotEmpty(contactNumber)) {
                        paramList = paramList + "&contactNumber=" + contactNumber;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("contactNumber"), EntityOperator.EQUALS, EntityFunction.UPPER(contactNumber)));
                    }

                    fieldsToSelect.add("contactNumber");
                    fieldsToSelect.add("areaCode");
                }
                //added the organization parameters and selection check
                
                if ("C".equals(extInfo)) {
                	// filter on division
                    String division = (String) context.get("division");
                  
                    if (UtilValidate.isNotEmpty(division)) {
                    	paramList = paramList + "&positionPartyId=" + division;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("positionPartyId"), EntityOperator.EQUALS, EntityFunction.UPPER(division)));
                    }
                 // filter on department
                    String department = (String) context.get("department");
                  
                    if (UtilValidate.isNotEmpty(department)) {
                    	paramList = paramList + "&positionPartyId=" + department;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("positionPartyId"), EntityOperator.EQUALS, EntityFunction.UPPER(department)));
                    }
                 // filter on section
                    String section = (String) context.get("section");
                  
                    if (UtilValidate.isNotEmpty(section)) {
                    	paramList = paramList + "&positionPartyId=" + section;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("positionPartyId"), EntityOperator.EQUALS, EntityFunction.UPPER(section)));
                    }
                    
                 // filter on section
                    String line = (String) context.get("line");
                  
                    if (UtilValidate.isNotEmpty(line)) {
                    	paramList = paramList + "&positionPartyId=" + line;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("positionPartyId"), EntityOperator.EQUALS, EntityFunction.UPPER(line)));
                    }
                    
                    fieldsToSelect.add("positionPartyId");
                }

                // ---- End of Dynamic View Creation

                // build the main condition
                if (andExprs.size() > 0) mainCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
            }

            Debug.logInfo("In findParty mainCond=" + mainCond, module);

            String sortField = (String) context.get("sortField");
            if(UtilValidate.isNotEmpty(sortField)){
                orderBy.add(sortField);
            }
            
            // do the lookup
            if (mainCond != null || "Y".equals(showAll)) {
                try {
                    // get the indexes for the partial list
                    lowIndex = viewIndex * viewSize + 1;
                    highIndex = (viewIndex + 1) * viewSize;

                    // set distinct on so we only get one row per order
                    EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, highIndex, true);
                    EntityListIterator pli =null;
                    // using list iterator
                    if ("P".equals(extInfo)){         	
                    	pli = delegator.find("PartyAndSearchAndPostalAddress", mainCond, null, fieldsToSelect, orderBy, findOpts);
                    }else if("O".equals(extInfo)){
                    	pli = delegator.find("PartyAndSearchAndAndOthers", mainCond, null, fieldsToSelect, orderBy, findOpts);      	
                    }else if("T".equals(extInfo)){
                    	pli = delegator.find("PartyAndSearchAndTelecomAddress", mainCond, null, fieldsToSelect, orderBy, findOpts);
                    }else if("C".equals(extInfo)){
                    	pli = delegator.find("PartyAndPersonAndFulfillment", mainCond, null, fieldsToSelect, orderBy, findOpts);
                    }else{
                    	 pli = delegator.find("PartyAndPersonAndUserLoginAndEmploymentAndPartyRole", mainCond, null, fieldsToSelect, orderBy, findOpts);
                    }
                    
                    	
                    
                   // ("PartyAndPersonAndUserLoginAndEmploymentAndPartyRole", mainCond,null,fieldsToSelect, orderBy, findOpts, false);
                    		
                   // EntityListIterator pli = delegator.findListIteratorByCondition(dynamicView, mainCond, null, fieldsToSelect, orderBy, findOpts);
                    	
                 // get the partial list for this page
                    partyList = pli.getPartialList(lowIndex, viewSize);

                    // attempt to get the full size
                    partyListSize = pli.getResultsSizeAfterPartialList();
                    if (highIndex > partyListSize) {
                        highIndex = partyListSize;
                    }

                    // close the list iterator
                    pli.close();
                } catch (GenericEntityException e) {
                    String errMsg = "Failure in party find operation, rolling back transaction: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                            "PartyLookupPartyError",
                            UtilMisc.toMap("errMessage", e.toString()), locale));
                }
            } else {
                partyListSize = 0;
            }
        }

        if (partyList == null) partyList = FastList.newInstance();
        result.put("partyList", partyList);
        result.put("partyListSize", Integer.valueOf(partyListSize));
        result.put("paramList", paramList);
        result.put("highIndex", Integer.valueOf(highIndex));
        result.put("lowIndex", Integer.valueOf(lowIndex));

        return result;
    }
    public static String getPartyId(Map<String, ? extends Object> context) {
        String partyId = (String) context.get("partyId");
        if (UtilValidate.isEmpty(partyId)) {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            if (userLogin != null) {
                partyId = userLogin.getString("partyId");
            }
        }
        return partyId;
    }
    
    
    
    public static Map<String, Object> updatePersonAddressAndTelecom2(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        
        String address1 = (String) context.get("address1");
        String city = (String) context.get("city");
        String postalCode = (String) context.get("postalCode");
        String infoString = (String) context.get("infoString");
        String contactNumber = (String) context.get("contactNumber");
        String BGMEAId = (String) context.get("BGMEAId");
        String parmanentAddress = (String) context.get("parmanentAddress");
        
        String partyId = getPartyId(context);
        if (UtilValidate.isEmpty(partyId)) {
            return ServiceUtil.returnError(UtilProperties.getMessage(ServiceUtil.resource, 
                    "serviceUtil.party_id_missing", locale));
        }

        GenericValue person = null;
        GenericValue party = null;

        try {
            person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
            party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "person.update.read_failure", new Object[] { e.getMessage() }, locale));
        }

        if (person == null || party == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "person.update.not_found", locale));
        }

        // update status by separate service
        String oldStatusId = party.getString("statusId");
        if (party.get("statusId") == null) { // old records
            party.set("statusId", "PARTY_ENABLED");
        }

        person.setNonPKFields(context);
        party.setNonPKFields(context);

        party.set("statusId", oldStatusId);

        try {
            person.store();
            party.store();
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "person.update.write_failure", new Object[] { e.getMessage() }, locale));
        }

        if (UtilValidate.isNotEmpty(context.get("statusId")) && !context.get("statusId").equals(oldStatusId)) {
            try {
                dispatcher.runSync("setPartyStatus", UtilMisc.toMap("partyId", partyId, "statusId", context.get("statusId"), "userLogin", context.get("userLogin")));
            } catch (GenericServiceException e) {
                Debug.logWarning(e.getMessage(), module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                        "person.update.write_failure", new Object[] { e.getMessage() }, locale));
            }
        }
        //update the postal address and telecom number
        List<GenericValue> listUpdatablePartyContactMech = null;
        GenericValue updatablePartyContactMech = null; 
        String updatableContactMechTypeId = "POSTAL_ADDRESS"; 
        
        //get the value of updatable contactMech
        List<EntityExpr> exprs = FastList.newInstance();
        exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        exprs.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, updatableContactMechTypeId));
        try {
       	 listUpdatablePartyContactMech = delegator.findList("PartyAndPostalAddress", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, UtilMisc.toList("fromDate"), null, false);//
       	 //("PartyAndPostalAddress", UtilMisc.toMap("contactMechId", mainContactMechId));
       	 
       	 //update the all 4 value into postal address table
       		 /*if(UtilValidate.isNotEmpty(contactMechId)){
       			 updatablePartyContactMech.set("contactMechId", contactMechId);
       		 }*/
       	 	GenericValue backupPartyContactMech = EntityUtil.getFirst(listUpdatablePartyContactMech);
       	 	if (UtilValidate.isEmpty(backupPartyContactMech)){
          		 return ServiceUtil.returnError("Changed Present Address is not found");
          		
          		 
       	 	}
       	 	updatablePartyContactMech = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", backupPartyContactMech.get("contactMechId")));
       		 if(UtilValidate.isNotEmpty(address1)){
       			 updatablePartyContactMech.set("address1", address1);
       		 }
       		 if(UtilValidate.isNotEmpty(city)){
       			 updatablePartyContactMech.set("city", city);
       		 }
       		 if(UtilValidate.isNotEmpty(city)){
       			 updatablePartyContactMech.set("postalCode", postalCode);
       		 }
       		 /*if(UtilValidate.isNotEmpty(mobileNo)){
       			 updatablePartyContactMech.set("mobileNo", mobileNo);
       		 }*/
       		 
       		 updatablePartyContactMech.store();
       	 
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
            return ServiceUtil.returnError("Changed Present Address could not be Updated");
        }
        //update parmanent address
       /* try {
			List<GenericValue>  parmanentAddressList = delegator.findByAnd("PartyAndPostalAddress", UtilMisc.toMap("partyId",partyId,"contactMechTypeId","PERMANENT_ADDRESS"));
			GenericValue parmanentAddressValue = EntityUtil.getFirst(parmanentAddressList);
			if (UtilValidate.isEmpty(parmanentAddressValue)){
         		 return ServiceUtil.returnError("Changed Parmanent Address is not found");
      	 	}
			GenericValue updatableParmanentAddress = delegator.findByPrimaryKey("PostalAddress",UtilMisc.toMap("contactMechId",parmanentAddressValue.get("contactMechId")));
			
			if(UtilValidate.isNotEmpty(parmanentAddress)){
				updatableParmanentAddress.set("address1", parmanentAddress);
			}
			updatableParmanentAddress.store();
		} catch (GenericEntityException e1) {
			 return ServiceUtil.returnError("Changed Parmanent Address could not be Updated");
		}
        */
        //update info string
        
        try {
 			List<GenericValue> listPartyContactMechs = delegator.findList("PartyContactMech", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),null,null,null,false);
 			String contactMechIdToCheck = "";
 			GenericValue contactMech = null;
 			for(GenericValue val : listPartyContactMechs){
 				contactMechIdToCheck = (String)val.get("contactMechId");
 				List<EntityExpr> exprt = FastList.newInstance();
                 exprt.add(EntityCondition.makeCondition("contactMechId", EntityOperator.EQUALS, contactMechIdToCheck));
                 exprt.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "EMAIL_ADDRESS"));
 				
 				List<GenericValue> listContactMechs = delegator.findList("ContactMech", EntityCondition.makeCondition(exprt, EntityOperator.AND),null,null,null,false);
 				
 				if (UtilValidate.isNotEmpty(listContactMechs)) {
					contactMech = EntityUtil.getFirst(listContactMechs);
				}
 			}
 			
 			if (UtilValidate.isNotEmpty(contactMech)){
 				contactMech.set("infoString", infoString);
 				contactMech.store();
        	 	}
 			
 			
 		} catch (GenericEntityException e) {
 			 return ServiceUtil.returnError("sendFrom emailAddress is not found");
 		}
        List<GenericValue> listUpdatableTelecomPartyContactMech = null;
        GenericValue updatableTelecomPartyContactMech = null; 
        String updatableTelecomContactMechTypeId = "TELECOM_NUMBER"; 
        
        //get the value of updatable contactMech
        List<EntityExpr> expr1 = FastList.newInstance();
        expr1.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        /*expr1.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, updatableTelecomContactMechTypeId));*/
       /* try {
       	 listUpdatableTelecomPartyContactMech = delegator.findList("PartyAndTelecomNumber", EntityCondition.makeCondition(expr1, EntityOperator.AND), null, UtilMisc.toList("fromDate"), null, false);//
       	 //("PartyAndPostalAddress", UtilMisc.toMap("contactMechId", mainContactMechId));
       	 
       	 //update the all 4 value into postal address table
       		 if(UtilValidate.isNotEmpty(contactMechId)){
       			 updatablePartyContactMech.set("contactMechId", contactMechId);
       		 }
       	 	GenericValue backupPartyContactMech = EntityUtil.getFirst(listUpdatableTelecomPartyContactMech);
       	 	if (UtilValidate.isEmpty(backupPartyContactMech)){
          		 return ServiceUtil.returnError("Changed Telecom Number is not found");
          		
          		 
       	 	}
       	 	updatableTelecomPartyContactMech = delegator.findByPrimaryKey("TelecomNumber", UtilMisc.toMap("contactMechId", backupPartyContactMech.get("contactMechId")));
       		 
       		 if(UtilValidate.isNotEmpty(contactNumber)){
       			 updatableTelecomPartyContactMech.set("contactNumber", contactNumber);
       		 }
       		 
       		 updatableTelecomPartyContactMech.store();
       	 
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
            return ServiceUtil.returnError("Changed Telecom Number could not be Updated");
        }*/
       //update partyIdentification for bgmea id
        List<GenericValue> partyIdetifications = null;
        String partyIdentificationTypeId = "BGMEAId";
        GenericValue partyIdetification = null;
        try {
        	partyIdetifications = delegator.findByAnd("PartyIdentification", UtilMisc.toMap("partyId", partyId,"partyIdentificationTypeId",partyIdentificationTypeId));
        	if(UtilValidate.isNotEmpty(BGMEAId)){
        		partyIdetification = EntityUtil.getFirst(partyIdetifications);
        		partyIdetification.set("idValue", BGMEAId);
        		partyIdetification.store();
        	}
        	
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        result.put(ModelService.SUCCESS_MESSAGE, 
                UtilProperties.getMessage(resourceError, "person.update.success", locale));
        result.put("partyId",partyId);
        return result;
    }
   
}
