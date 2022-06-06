package org.ofbiz.humanres.recruitment;

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

import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
//import org.apache.poi.hslf.util.SystemTimeUtils;
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
import org.ofbiz.humanres.HumanResEvents;
import org.ofbiz.minilang.method.entityops.FindByAnd;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.humanres.attendance.AttendanceUtils;
//import org.opentaps.common.util.UtilCommon;
//import org.opentaps.foundation.entity.EntityException;
import org.ofbiz.humanres.HrUtils;

import com.ibm.icu.util.StringTokenizer;

public class RecruitmentServices {
	
	public static final String module = RecruitmentServices.class.getName();
	public static final String resource = "HRRecruitUiLabels";
	private static Logger logger = Logger.getLogger("RecruitmentServices");
	public static Map<String, Object> approveOrRejectRequisition(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();

		String requisitionAction = (String) context.get("requisitionAction");
		String jobRequisitionId = (String) context.get("jobRequisitionId");

		String successMessage = "success";
		String errorMessage = "";
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
	    java.sql.Date approvedDate = new java.sql.Date(stamp.getTime());
		 GenericValue updatableRequisition = null; 
		    try{
		    	updatableRequisition = delegator.findByPrimaryKey("ContessaJobRequisition", UtilMisc.toMap("jobRequisitionId", jobRequisitionId));
		    	if(UtilValidate.isNotEmpty(approvedDate)){
		    		updatableRequisition.set("approvalDate", approvedDate);
		  		 }
		    	updatableRequisition.set("requisitionStatus", requisitionAction );
		    	updatableRequisition.store();
		    } catch (GenericEntityException e) {
		        Debug.logWarning(e, module);
		        return ServiceUtil.returnError("Changed Requisition Status approval flow could not be Updated");
		    }
		
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, successMessage);

		return result;
	}
	public static String getConfirmationDate(Delegator delegator,
			String OrgPartyId, String partyId, String emplPositionId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String confirmationDate = "";
		Timestamp joinDate = null;
		Timestamp thruDate = null;
		List<GenericValue> employment = FastList.newInstance();
		try {
			employment = delegator.findByAnd("Employment", UtilMisc.toMap(
					"partyIdTo", partyId, "partyIdFrom", OrgPartyId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!UtilValidate.isEmpty(employment)) {
			GenericValue empl = EntityUtil.getFirst(employment);
			joinDate = empl.getTimestamp("fromDate");
			thruDate = empl.getTimestamp("thruDate");
			Timestamp confirmDate = empl.getTimestamp("confirmationDate");
			long currentTime = System.currentTimeMillis();
			// long thruTime = thruDate.getTime();

			Calendar joiningDateTime = Calendar.getInstance();
			// first check is there have confirm date or not
			if (!UtilValidate.isEmpty(confirmDate)) {
				confirmationDate = sdf.format(confirmDate);
				return confirmationDate;
			}
			// then check join date & calculate from preference to get
			// confirmation date
			if (!UtilValidate.isEmpty(joinDate)
					&& UtilValidate.isEmpty(thruDate)) {
				int numberOfMonth = AttendanceUtils
						.getNumberOfMonthToConfirmation(partyId, delegator,
								OrgPartyId, joinDate);
				joiningDateTime.setTime(joinDate);
				joiningDateTime.add(Calendar.MONTH, numberOfMonth);

				Date confDate = joiningDateTime.getTime();
				confirmationDate = sdf.format(confDate);
				return confirmationDate;
			}
		}
		return "";
	}
	
/*
 * Create New Employee Method
 * Written Muntashir
 */
	
	public static Map<String, Object> createApplicant(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Timestamp now = UtilDateTime.nowTimestamp();
		List<GenericValue> toBeStored = FastList.newInstance();

		Locale locale = (Locale) context.get("locale");
		String partyId = (String) context.get("partyId");
		if (partyId == null)
			partyId = "";

		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String preferredCurrencyUomId = (String) context.get("preferredCurrencyUomId");
		String jobRequisitionId = (String) context.get("jobRequisitionId");

		String userLoginId = (String) context.get("userLoginId");

		String externalId = (String) context.get("externalId");

		String nationalid = (String) context.get("nationalid");

		String bloodgroup = (String) context.get("bloodgroup");

		String statusId = (String) context.get("statusId");

		String infoString = (String) context.get("infoString");

		String firstName = (String) context.get("firstName");

		String lastName = (String) context.get("lastName");

		String fatherName = (String) context.get("fatherName");

		String spouseName = (String) context.get("spouseName");

		String mothersMaidenName = (String) context.get("mothersMaidenName");

		String religion = (String) context.get("religion");

		//String birthDate = (String) context.get("birthDate");

		String birthCertificateNo = (String) context.get("birthCertificateNo");

		String gender = (String) context.get("gender");
		String maritalStatus = (String) context.get("maritalStatus");

		String address1 = (String) context.get("address1");

		String contactNumber = (String) context.get("contactNumber");

		String address2 = (String) context.get("address2");

		String city1 = (String) context.get("city1");

		String country1 = (String) context.get("country1");
		
		
		String address3 = (String) context.get("address3");

		String city2 = (String) context.get("city2");

		String country2 = (String) context.get("country2");

		String passportNumber = (String) context.get("passportNumber");

		String drivingLicenseNo = (String) context.get("drivingLicenseNo");
		String employeeId = (String) context.get("employeeId");

		String referenceEmployeeId = (String) context.get("referenceEmployeeId");

		String referenceDetails = (String) context.get("referenceDetails");
		List<GenericValue> jobReqStatus=null;
		
		try {
			jobReqStatus = delegator.findByAnd("ContessaJobRequisition", UtilMisc.toMap("jobRequisitionId", jobRequisitionId));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		String requisitionStatus="";
		if (jobReqStatus.size() != 0) {
			requisitionStatus = jobReqStatus.get(0).get("requisitionStatus").toString();
		}
		if (requisitionStatus.equalsIgnoreCase("JOB_REQ_APPROVED")){
			
		

		String referencePartyId = null;
		if (referenceEmployeeId != null) {
			referencePartyId = HrUtils.getPartyIDFromUserLoginID(
					delegator, referenceEmployeeId);
		}

		// ///////// Generate Party ID and store in Party table ///

		if (UtilValidate.isEmpty(partyId)) {
			try {
				partyId = delegator.getNextSeqId("Party");

			} catch (IllegalArgumentException e) {
				return ServiceUtil.returnError(UtilProperties.getMessage(
						resource, "party.id_generation_failure", locale));
			}
		}

		Map<String, Object> newPartyMap = UtilMisc.toMap("partyId", partyId,
				"createdDate", now, "lastModifiedDate", now);
		if (!UtilValidate.isEmpty(preferredCurrencyUomId)) {
			newPartyMap.put("preferredCurrencyUomId", preferredCurrencyUomId);
		}
		if (!UtilValidate.isEmpty(externalId)) {
			newPartyMap.put("externalId", externalId);
		}
		if (userLogin != null) {
			newPartyMap.put("createdByUserLogin", userLogin.get("userLoginId"));
			newPartyMap.put("lastModifiedByUserLogin",
					userLogin.get("userLoginId"));
		}
		GenericValue party = delegator.makeValue("Party", newPartyMap);

		party.setNonPKFields(context);

		toBeStored.add(party);

		// ////////// Entry in Person table /////////

		Map<String, String> newPersonMap = UtilMisc.toMap("partyId", partyId);
		GenericValue person = delegator.makeValue("Person", newPersonMap);

		person.setNonPKFields(context);
		/* person.set("height", height); */

		toBeStored.add(person);

		// ////////// Entry in PartyRole table /////////

		Map<String, String> newPartyRole = UtilMisc.toMap("partyId", partyId,
				"roleTypeId", "EMPL_APPLICANT");
		GenericValue partyRole = delegator.makeValue("PartyRole", newPartyRole);

		toBeStored.add(partyRole);

		// ////////// Entry in PartyRelationShip table /////////

		String partyIdFrom = "";
		String emplPositionId = "";
		String jobPostingId = "";
		//String jobRequisitionId = "";
		String employmentAppSourceTypeId = "";

		try {

			

			GenericValue JobReqGenericValue = delegator.findByPrimaryKey(
					"ContessaJobRequisition",
					UtilMisc.toMap("jobRequisitionId", jobRequisitionId));
			if (JobReqGenericValue != null)
				emplPositionId = JobReqGenericValue.get("emplPositionId")
						.toString();

			GenericValue emplPostionGenericValue = delegator.findByPrimaryKey(
					"EmplPosition",
					UtilMisc.toMap("emplPositionId", emplPositionId));
			if (emplPostionGenericValue != null) {
				partyIdFrom = emplPostionGenericValue.get("partyId").toString();

				// Get the parent party (CSL)
				partyIdFrom = getParentParty(delegator, partyIdFrom);
			}

		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}

		Map<String, Object> newPartyRelationship = UtilMisc.toMap(
				"partyIdFrom", partyIdFrom, "roleTypeIdFrom",
				"INTERNAL_ORGANIZATIO", "partyIdTo", partyId, "roleTypeIdTo",
				"EMPL_APPLICANT", "partyRelationshipTypeId", "EMPL_APPLICANT",
				"fromDate", now);
		GenericValue partyRelationship = delegator.makeValue(
				"PartyRelationship", newPartyRelationship);

		toBeStored.add(partyRelationship);

		// ////////// Entry in PartyIdentification table for Nationalid /////////

		Map<String, Object> newPartyIdentification = UtilMisc.toMap("partyId",
				partyId, "partyIdentificationTypeId", "Nationalid", "idValue",
				(Object) nationalid);
		GenericValue partyIdentification = delegator.makeValue(
				"PartyIdentification", newPartyIdentification);
		toBeStored.add(partyIdentification);

		// //////////Entry in EmploymentApp table for Applicant /////////
		String applicationId = delegator.getNextSeqId("EmploymentApp");
		Map<String, Object> newPartyApplication = UtilMisc.toMap(
				"applicationId", applicationId, "emplPositionId",
				emplPositionId, "statusId", "APPLICANT_CREATED",
				"employmentAppSourceTypeId", employmentAppSourceTypeId,
				"applyingPartyId", partyId, "applicationDate", now,
				"approverPartyId", userLogin.get("userLoginId").toString(),
				"jobRequisitionId", jobRequisitionId, "jobPostingId",
				jobPostingId, "referredByPartyId", referencePartyId,
				"referenceDetails", referenceDetails);

		GenericValue partyEmploymentApp = delegator.makeValue("EmploymentApp",
				newPartyApplication);
		partyIdentification.setNonPKFields(context);
		toBeStored.add(partyEmploymentApp);
		
		try {
			delegator.storeAll(toBeStored);
		} catch (GenericEntityException e) {
			Debug.logWarning(e.getMessage(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource,
					"Party.create.db_error",
					new Object[] { e.getMessage() }, locale));
		}
		// //////////Entry for Telecom Number /////////
		// re-initiate toBeStored variable
		toBeStored = FastList.newInstance();
		
		// ContactMech
		String teleContactMechId = delegator.getNextSeqId("ContactMech");
		Map<String, Object> newTeleContactMech = UtilMisc.toMap(
				"contactMechId", (Object) teleContactMechId,
				"contactMechTypeId", "TELECOM_NUMBER");
		GenericValue teleContactMech = delegator.makeValue("ContactMech",
				newTeleContactMech);
		toBeStored.add(teleContactMech);

		// PartyContactMech
		Map<String, Object> newTelePartyContactMech = UtilMisc.toMap("partyId",
				partyId, "contactMechId", (Object) teleContactMechId,
				"fromDate", now);
		GenericValue partyTeleContactMech = delegator.makeValue(
				"PartyContactMech", newTelePartyContactMech);
		toBeStored.add(partyTeleContactMech);

		// PartyContactMechPurpose
		Map<String, Object> newPartyTeleContactMechPurpose = UtilMisc.toMap(
				"partyId", partyId, "contactMechId",
				(Object) teleContactMechId, "contactMechPurposeTypeId",
				"PHONE_MOBILE", "fromDate", now);
		GenericValue partyTeleContactMechPurpose = delegator.makeValue(
				"PartyContactMechPurpose", newPartyTeleContactMechPurpose);
		toBeStored.add(partyTeleContactMechPurpose);

		// TelecomNumber
		Map<String, Object> newTelecomNumber = UtilMisc.toMap("contactMechId",
				(Object) teleContactMechId);
		GenericValue partyTelecomNumber = delegator.makeValue("TelecomNumber",
				newTelecomNumber);
		partyTelecomNumber.setNonPKFields(context);
		partyTelecomNumber.set("askForName", firstName);
		toBeStored.add(partyTelecomNumber);

		try {
			delegator.storeAll(toBeStored);
		} catch (GenericEntityException e) {
			Debug.logWarning(e.getMessage(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource,
					"TelecomNumber.create.db_error",
					new Object[] { e.getMessage() }, locale));
		}

		// //////////Entry for Email Address /////////
		// re-initiate toBeStored variable
		toBeStored = FastList.newInstance();

		// ContactMech with email address
		String emailContactMechId = delegator.getNextSeqId("ContactMech");
		Map<String, Object> newEmailContactMech = UtilMisc.toMap(
				"contactMechId", (Object) emailContactMechId,
				"contactMechTypeId", "EMAIL_ADDRESS");
		GenericValue emailContactMech = delegator.makeValue("ContactMech",
				newEmailContactMech);
		emailContactMech.setNonPKFields(context);
		toBeStored.add(emailContactMech);

		// PartyContactMech
		Map<String, Object> newEmailPartyContactMech = UtilMisc.toMap(
				"partyId", partyId, "contactMechId",
				(Object) emailContactMechId, "fromDate", now);
		GenericValue partyEmailContactMech = delegator.makeValue(
				"PartyContactMech", newEmailPartyContactMech);
		toBeStored.add(partyEmailContactMech);

		// PartyContactMechPurpose
		Map<String, Object> newPartyEmailContactMechPurpose = UtilMisc.toMap(
				"partyId", partyId, "contactMechId",
				(Object) emailContactMechId, "contactMechPurposeTypeId",
				"PRIMARY_EMAIL", "fromDate", now);
		GenericValue partyEmailContactMechPurpose = delegator.makeValue(
				"PartyContactMechPurpose", newPartyEmailContactMechPurpose);
		toBeStored.add(partyEmailContactMechPurpose);

		try {
			delegator.storeAll(toBeStored);
		} catch (GenericEntityException e) {
			Debug.logWarning(e.getMessage(), module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource,
					"EmailAddress.create.db_error",
					new Object[] { e.getMessage() }, locale));
		}

		// ////////// Entry in POSTAL ADDRESS table for present address/////////
				
				String contactMechIdPostal = delegator.getNextSeqId("ContactMech");
		   		Timestamp formDate = UtilDateTime.nowTimestamp();
		   	  Map<String, Object> PostalContactMechIn = FastMap.newInstance();
		    
		   		PostalContactMechIn.put("contactMechId", contactMechIdPostal);
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
		   		PostalPartyContactMechIn.put("contactMechId", contactMechIdPostal);
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
		   		PostalAddressIn.put("contactMechId", contactMechIdPostal);
		   		PostalAddressIn.put("address1", address1);
		   		
		   		try {
		  			GenericValue createPostalAddresssValue = delegator
		  					.makeValue("PostalAddress",
		  							UtilMisc.toMap(PostalAddressIn));
		  			createPostalAddresssValue.create();
		          } catch (Exception e) {

		          	e.printStackTrace();
		          }
///////////////////////////////////////////////mailing Address/////////////////////////////////////////
// ////////// Entry in POSTAL ADDRESS table for present address/////////
				
				String contactMechIdmailing = delegator.getNextSeqId("ContactMech");
		   		Timestamp formDateMailing = UtilDateTime.nowTimestamp();
		   	  Map<String, Object> MailingPostalContactMechIn = FastMap.newInstance();
		    
		   	MailingPostalContactMechIn.put("contactMechId", contactMechIdmailing);
		   	MailingPostalContactMechIn.put("contactMechTypeId", "MAILING_ADDRESS");
		   		try {
		  			GenericValue createPostalMailinfContactValue = delegator
		  					.makeValue("ContactMech",
		  							UtilMisc.toMap(MailingPostalContactMechIn));
		  			createPostalMailinfContactValue.create();
		          } catch (Exception e) {

		          	e.printStackTrace();
		          }
		   		Map<String, Object> MailingPostalPartyContactMechIn = FastMap.newInstance();
		   		
		   		MailingPostalPartyContactMechIn.put("partyId", partyId);
		   		MailingPostalPartyContactMechIn.put("contactMechId", contactMechIdmailing);
		   		MailingPostalPartyContactMechIn.put("fromDate", formDateMailing);
		   		try {
		  			GenericValue createMailingPostalPartyContactValue = delegator
		  					.makeValue("PartyContactMech",
		  							UtilMisc.toMap(MailingPostalPartyContactMechIn));
		  			createMailingPostalPartyContactValue.create();
		          } catch (Exception e) {

		          	e.printStackTrace();
		          }
		   		Map<String, Object> MailingPostalAddressIn = FastMap.newInstance();
		   		MailingPostalAddressIn.put("contactMechId", contactMechIdmailing);
		   		MailingPostalAddressIn.put("address1", address2);
		   		MailingPostalAddressIn.put("city", city1);
		   		MailingPostalAddressIn.put("countryGeoId", country1);
		   		try {
		  			GenericValue createPostalMailingAddresssValue = delegator
		  					.makeValue("PostalAddress",
		  							UtilMisc.toMap(MailingPostalAddressIn));
		  			createPostalMailingAddresssValue.create();
		          } catch (Exception e) {

		          	e.printStackTrace();
		          }

				// ////////// Entry in POSTAL ADDRESS table for permanent
				// address/////////
				

		   	 	String contactMechIdparmanent= delegator.getNextSeqId("ContactMech");
		   		Timestamp formDateparmanent = UtilDateTime.nowTimestamp();
		   	  Map<String, Object> PostalContactMechInparmanent = FastMap.newInstance();
		    
		   		PostalContactMechInparmanent.put("contactMechId", contactMechIdparmanent);
		   		PostalContactMechInparmanent.put("contactMechTypeId", "PERMANENT_ADDRESS");
		   		try {
		  			GenericValue createPostalParmanentContactValue = delegator
		  					.makeValue("ContactMech",
		  							UtilMisc.toMap(PostalContactMechInparmanent));
		  			createPostalParmanentContactValue.create();
		          } catch (Exception e) {

		          	e.printStackTrace();
		          }
		   		Map<String, Object> PostalPartyContactMechInparmanent = FastMap.newInstance();
		   		
		   		PostalPartyContactMechInparmanent.put("partyId", partyId);
		   		PostalPartyContactMechInparmanent.put("contactMechId", contactMechIdparmanent);
		   		PostalPartyContactMechInparmanent.put("fromDate", formDateparmanent);
		   		try {
		  			GenericValue createPostalPartyContactValueParmanent = delegator
		  					.makeValue("PartyContactMech",
		  							UtilMisc.toMap(PostalPartyContactMechInparmanent));
		  			createPostalPartyContactValueParmanent.create();
		          } catch (Exception e) {

		          	e.printStackTrace();
		          }
		   		Map<String, Object> PostalAddressInPermanent = FastMap.newInstance();
		   		PostalAddressInPermanent.put("contactMechId", contactMechIdparmanent);
		   		PostalAddressInPermanent.put("address1", address3);
		   		PostalAddressInPermanent.put("city", city2);
		   	
		   		PostalAddressInPermanent.put("countryGeoId", country2);
		   		
		   		try {
		  			GenericValue createPostalAddresssValuePermanent = delegator
		  					.makeValue("PostalAddress",
		  							UtilMisc.toMap(PostalAddressInPermanent));
		  			createPostalAddresssValuePermanent.create();
		          } catch (Exception e) {

		          	e.printStackTrace();
		          }
		      
				
		}
		else {
			return ServiceUtil
					.returnError("Job Requisition Not Approved");
		}

		// ///////////////////////////////////

		result.put("partyId", partyId);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE,
				"Applicant information saved successfully.");

		return result;
	}
	
	
	public static Map<String, Object> updateApplicant(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");

		String preferredCurrencyUomId = (String) context.get("preferredCurrencyUomId");
		String partyId = (String) context.get("partyId");

		String userLoginId = (String) context.get("userLoginId");

		String externalId = (String) context.get("externalId");

		String nationalid = (String) context.get("nationalid");

		String bloodgroup = (String) context.get("bloodgroup");

		String statusId = (String) context.get("statusId");

		String infoString = (String) context.get("infoString");

		String firstName = (String) context.get("firstName");

		String lastName = (String) context.get("lastName");

		String fatherName = (String) context.get("fatherName");

		String spouseName = (String) context.get("spouseName");

		String mothersMaidenName = (String) context.get("mothersMaidenName");

		String religion = (String) context.get("religion");

		//String birthDate = (String) context.get("birthDate");

		String birthCertificateNo = (String) context.get("birthCertificateNo");

		String gender = (String) context.get("gender");
		String maritalStatus = (String) context.get("maritalStatus");

		String address1 = (String) context.get("address1");

		String contactNumber = (String) context.get("contactNumber");

		String address2 = (String) context.get("address2");

		String city1 = (String) context.get("city1");

		String country1 = (String) context.get("country1");
		
		
		String address3 = (String) context.get("address3");

		String city2 = (String) context.get("city2");

		String country2 = (String) context.get("country2");

		String passportNumber = (String) context.get("passportNumber");

		String drivingLicenseNo = (String) context.get("drivingLicenseNo");
		String employeeId = (String) context.get("employeeId");

		String referenceEmployeeId = (String) context.get("referenceEmployeeId");

		String referenceDetails = (String) context.get("referenceDetails");
	   
	     

	
		// start height formate (5'6" formate to 5.6 formate for store)
		/*
		 * String[] heightArray = height.split("'"); String
		 * height1=heightArray[0]; char ch = '"'; String formate = ch+"";
		 * String[] heightSec = heightArray[1].split(formate); String height2 =
		 * heightSec[0]; height = height1+"."+height2;
		 */
		// end height formate (5'6" formate to 5.6 formate for store)

		if (UtilValidate.isEmpty(partyId)) {
			return ServiceUtil.returnError(UtilProperties.getMessage(
					ServiceUtil.resource, "serviceUtil.party_id_missing",
					locale));
		}

		GenericValue person = null;
		GenericValue party = null;

		try {
			person = delegator.findByPrimaryKey("Person",
					UtilMisc.toMap("partyId", partyId));
			party = delegator.findByPrimaryKey("Party",
					UtilMisc.toMap("partyId", partyId));
		} catch (GenericEntityException e) {
			Debug.logWarning(e, module);
			return ServiceUtil.returnError("Party, Person not found.");
		}

		if (person == null || party == null) {
			return ServiceUtil.returnError("Applicant update failed");
		}

		// update status by separate service
		String oldStatusId = party.getString("statusId");
		if (party.get("statusId") == null) { // old records
			party.set("statusId", "PARTY_ENABLED");
		}

		person.setNonPKFields(context);
		/* person.set("height", height); */

		party.setNonPKFields(context);

		party.set("statusId", oldStatusId);

		try {
			person.store();
			party.store();
		} catch (GenericEntityException e) {
			Debug.logWarning(e.getMessage(), module);
			return ServiceUtil.returnError("Applicant update error.");
		}

		if (UtilValidate.isNotEmpty(context.get("statusId"))
				&& !context.get("statusId").equals(oldStatusId)) {
			try {
				dispatcher.runSync(
						"setPartyStatus",
						UtilMisc.toMap("partyId", partyId, "statusId",
								context.get("statusId"), "userLogin",
								context.get("userLogin")));
			} catch (GenericServiceException e) {
				Debug.logWarning(e.getMessage(), module);
				return ServiceUtil.returnError("Applicant update error.");
			}
		}

		// update the postal address and telecom number
		List<GenericValue> listUpdatablePartyContactMech = null;
		GenericValue updatablePartyContactMech = null;
		String updatableContactMechTypeId = "POSTAL_ADDRESS";

		// get the value of updatable contactMech
		List<EntityExpr> exprs = FastList.newInstance();
		exprs.add(EntityCondition.makeCondition("partyId",
				EntityOperator.EQUALS, partyId));
		exprs.add(EntityCondition.makeCondition("contactMechTypeId",
				EntityOperator.EQUALS, updatableContactMechTypeId));
		try {

			listUpdatablePartyContactMech = delegator.findList(
					"PartyAndPostalAddress",
					EntityCondition.makeCondition(exprs, EntityOperator.AND),
					null, UtilMisc.toList("fromDate"), null, false);//

			GenericValue backupPartyContactMech = EntityUtil
					.getFirst(listUpdatablePartyContactMech);
			if (UtilValidate.isEmpty(backupPartyContactMech)) {
				return ServiceUtil
						.returnError("Changed Present Address is not found");

			}
			updatablePartyContactMech = delegator.findByPrimaryKey(
					"PostalAddress",
					UtilMisc.toMap("contactMechId",
							backupPartyContactMech.get("contactMechId")));
			if (UtilValidate.isNotEmpty(address1)) {
				updatablePartyContactMech.set("address1", address1);
			}
		

			updatablePartyContactMech.store();

		} catch (GenericEntityException e) {
			Debug.logWarning(e, module);
			return ServiceUtil
					.returnError("Changed Present Address could not be Updated");
		}

		// //// update permanent address
		String updatablePermanentContactMechTypeId = "PERMANENT_ADDRESS";

		// get the value of updatable contactMech
		List<EntityExpr> exprsPermanent = FastList.newInstance();
		exprsPermanent.add(EntityCondition.makeCondition("partyId",
				EntityOperator.EQUALS, partyId));
		exprsPermanent.add(EntityCondition.makeCondition("contactMechTypeId",
				EntityOperator.EQUALS, updatablePermanentContactMechTypeId));
		try {

			List<GenericValue> listUpdatablePartyPermanentAddressContactMech = delegator
					.findList("PartyAndPostalAddress", EntityCondition
							.makeCondition(exprsPermanent, EntityOperator.AND),
							null, UtilMisc.toList("fromDate"), null, false);//

			GenericValue backupPartyContactMech = EntityUtil
					.getFirst(listUpdatablePartyPermanentAddressContactMech);
			if (UtilValidate.isEmpty(backupPartyContactMech)) {
				return ServiceUtil
						.returnError("Changed Permanent Address is not found");
			}

			GenericValue updatablePartyPermanentAddressContactMech = delegator
					.findByPrimaryKey("PostalAddress", UtilMisc.toMap(
							"contactMechId",
							backupPartyContactMech.get("contactMechId")));
			if (UtilValidate.isNotEmpty(address3)) {
				updatablePartyPermanentAddressContactMech.set("address1",
						address3);
			}
			if (UtilValidate.isNotEmpty(city2)) {
				updatablePartyPermanentAddressContactMech.set("city", city2);
			}
			if (UtilValidate.isNotEmpty(country2)) {
				updatablePartyPermanentAddressContactMech.set("countryGeoId", country2);
			}
			updatablePartyPermanentAddressContactMech.store();

		} catch (GenericEntityException e) {
			Debug.logWarning(e, module);
			return ServiceUtil
					.returnError("Changed Permanent Address could not be Updated");
		}
//mailing address Updatable Field
		
		String updatableMailingContactMechTypeId = "MAILING_ADDRESS";

		// get the value of updatable contactMech
		List<EntityExpr> exprsMailing= FastList.newInstance();
		exprsMailing.add(EntityCondition.makeCondition("partyId",
				EntityOperator.EQUALS, partyId));
		exprsMailing.add(EntityCondition.makeCondition("contactMechTypeId",
				EntityOperator.EQUALS, updatableMailingContactMechTypeId));
		try {

			List<GenericValue> listUpdatablePartyMailingAddressContactMech = delegator
					.findList("PartyAndPostalAddress", EntityCondition
							.makeCondition(exprsMailing, EntityOperator.AND),
							null, UtilMisc.toList("fromDate"), null, false);//
			if(UtilValidate.isNotEmpty(listUpdatablePartyMailingAddressContactMech)){
			GenericValue backupPartyContactMech = EntityUtil
					.getFirst(listUpdatablePartyMailingAddressContactMech);
			if (UtilValidate.isEmpty(backupPartyContactMech)) {
				return ServiceUtil
						.returnError("Changed Permanent Address is not found");
			}

			GenericValue updatablePartyMailingAddressContactMech = delegator
					.findByPrimaryKey("PostalAddress", UtilMisc.toMap(
							"contactMechId",
							backupPartyContactMech.get("contactMechId")));
			
			if (UtilValidate.isNotEmpty(address3)) {
				updatablePartyMailingAddressContactMech.set("address1",
						address2);
			}
			if (UtilValidate.isNotEmpty(city1)) {
				updatablePartyMailingAddressContactMech.set("city", city1);
			}
			if (UtilValidate.isNotEmpty(country2)) {
				updatablePartyMailingAddressContactMech.set("countryGeoId", country1);
			}
			updatablePartyMailingAddressContactMech.store();
			}
		} catch (GenericEntityException e) {
			Debug.logWarning(e, module);
			return ServiceUtil
					.returnError("Changed Permanent Address could not be Updated");
		}
		// update Email

		try {
			List<GenericValue> listPartyContactMechs = delegator.findList(
					"PartyContactMech", EntityCondition.makeCondition(
							"partyId", EntityOperator.EQUALS, partyId), null,
					null, null, false);
			String contactMechIdToCheck = "";
			GenericValue contactMech = null;
			for (GenericValue val : listPartyContactMechs) {
				contactMechIdToCheck = (String) val.get("contactMechId");
				List<EntityExpr> exprt = FastList.newInstance();
				exprt.add(EntityCondition.makeCondition("contactMechId",
						EntityOperator.EQUALS, contactMechIdToCheck));
				exprt.add(EntityCondition.makeCondition("contactMechTypeId",
						EntityOperator.EQUALS, "EMAIL_ADDRESS"));

				List<GenericValue> listContactMechs = delegator.findList(
						"ContactMech", EntityCondition.makeCondition(exprt,
								EntityOperator.AND), null, null, null, false);

				if (UtilValidate.isNotEmpty(listContactMechs)) {
					contactMech = EntityUtil.getFirst(listContactMechs);
				}
			}

			if (UtilValidate.isNotEmpty(contactMech)) {
				contactMech.set("infoString", infoString);
				contactMech.store();
			}

		} catch (GenericEntityException e) {
			return ServiceUtil
					.returnError("sendFrom emailAddress is not found");
		}
		List<GenericValue> listUpdatableTelecomPartyContactMech = null;
		GenericValue updatableTelecomPartyContactMech = null;

		// get the value of updatable contactMech
		List<EntityExpr> expr1 = FastList.newInstance();
		expr1.add(EntityCondition.makeCondition("partyId",
				EntityOperator.EQUALS, partyId));
		/*
		 * expr1.add(EntityCondition.makeCondition("contactMechTypeId",
		 * EntityOperator.EQUALS, updatableTelecomContactMechTypeId));
		 */
		try {
			listUpdatableTelecomPartyContactMech = delegator.findList(
					"PartyAndTelecomNumber",
					EntityCondition.makeCondition(expr1, EntityOperator.AND),
					null, UtilMisc.toList("fromDate"), null, false);//

			GenericValue backupPartyContactMech = EntityUtil
					.getFirst(listUpdatableTelecomPartyContactMech);
			if (UtilValidate.isEmpty(backupPartyContactMech)) {
				return ServiceUtil
						.returnError("Changed Telecom Number is not found");

			}
			updatableTelecomPartyContactMech = delegator.findByPrimaryKey(
					"TelecomNumber",
					UtilMisc.toMap("contactMechId",
							backupPartyContactMech.get("contactMechId")));

			if (UtilValidate.isNotEmpty(contactNumber)) {
				updatableTelecomPartyContactMech.set("contactNumber",
						contactNumber);
			}

			updatableTelecomPartyContactMech.store();

		} catch (GenericEntityException e) {
			Debug.logWarning(e, module);
			return ServiceUtil
					.returnError("Changed Telecom Number could not be Updated");
		}
		
		List<GenericValue> partyIdetifications = null;
		String partyIdentificationTypeId = "Nationalid";
		GenericValue partyIdetification = null;
		try {
			partyIdetifications = delegator.findByAnd("PartyIdentification",
					UtilMisc.toMap("partyId", partyId,
							"partyIdentificationTypeId",
							partyIdentificationTypeId));
			if (UtilValidate.isNotEmpty(nationalid)) {
				partyIdetification = EntityUtil.getFirst(partyIdetifications);
				partyIdetification.set("idValue", nationalid);
				partyIdetification.store();
			}
			

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		

		

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage(
				resource, "Person Updated Successfully ", locale));
		result.put("partyId", partyId);
		return result;
	}

	public static String getParentParty(Delegator delegator, String partyId)
			throws GenericEntityException {

		String parentPartyId = "";

		boolean loopFlag = true;

		while (loopFlag) {

			Map<String, String> parent1 = getParent(delegator, partyId);

			partyId = parent1.get("partyIdFrom");

			String str2 = "INTERNAL_ORGANIZATIO";
			String parentRole = parent1.get("roleType").toString();

			if (parentRole.equals(str2) || parentRole.equals("_NA_")) {
				loopFlag = false;
				parentPartyId = partyId;
				break;
			}
		}

		return parentPartyId;

	}
	
	public static Map<String, String> getParent(Delegator delegator,
			String partyId) throws GenericEntityException {

		// parent
		List<GenericValue> parentPartyRel = delegator.findByAnd(
				"PartyRelationship", UtilMisc.toMap("partyIdTo", partyId,
						"partyRelationshipTypeId", "GROUP_ROLLUP"));

		String parentPartyFrom = parentPartyRel.get(0).get("partyIdFrom")
				.toString();
		String parentPartyTo = parentPartyRel.get(0).get("partyIdTo")
				.toString();

		GenericValue parent = delegator.findByPrimaryKey("PartyGroup",
				UtilMisc.toMap("partyId", parentPartyFrom));

		Map<String, String> parentMap = FastMap.newInstance();
		parentMap.put("partyIdTo", parent.get("partyId").toString());

		if (parentPartyFrom.equals("Company")) {
			parentMap.put("partyIdFrom", parentPartyTo);
		} else {
			parentMap.put("partyIdFrom", parentPartyFrom);
		}

		parentMap.put("groupName", parent.getString("groupName"));
		parentMap.put("roleType", parentPartyRel.get(0).get("roleTypeIdFrom")
				.toString());

		return parentMap;
	}
	
	public static Map<String, Object> updateApplicantJoiningDate(
			DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		String partyId = (String) context.get("partyId");
		java.sql.Timestamp joiningDate = (Timestamp) context.get("joiningDate");
		String onProbation = null;
		if (context.get("onProbation") != null) {
			onProbation = context.get("onProbation").toString();
		}
		result.put("partyId", partyId);
		String successMessage = "";
		try {

			List<GenericValue> applications = delegator
					.findByAnd("EmploymentApp",
							UtilMisc.toMap("applyingPartyId", partyId));

			if (UtilValidate.isNotEmpty(joiningDate)) {
				GenericValue application = EntityUtil.getFirst(applications);

				application.set("joiningDate", joiningDate);
				if (onProbation != null) {
					application.set("isProbationaryFlag", "probationary");
				} else {
					application.set("isProbationaryFlag", "");
				}
				application.set("statusId", "WAIT_APPROVAL");
				application.store();
			}
			successMessage = "Action Performed successfully";
		} catch (GenericEntityException e) {
			Debug.logWarning(e, module);
			return ServiceUtil
					.returnError("Error occured during update applicant Joining Date");
		}

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, successMessage);

		return result;
	}
	
	
	
	public static Map<String, Object> updateStatus(
			DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		String applyingPartyId = (String) context.get("applyingPartyId");
		String statusId = (String) context.get("requisitionAction");
		String jobRequisitionId = null;
		if (context.get("jobRequisitionId") != null) {
			jobRequisitionId = context.get("jobRequisitionId").toString();
		}
		result.put("applyingPartyId", applyingPartyId);
		
		java.sql.Date currentdate = new java.sql.Date(
				System.currentTimeMillis());
		String successMessage = "";
		try {

			List<GenericValue> applications = delegator
					.findByAnd("EmploymentApp",
							UtilMisc.toMap("applyingPartyId", applyingPartyId));

			if (statusId.equalsIgnoreCase("WAIT_CONFIRMATION")) {
				GenericValue application = EntityUtil.getFirst(applications);

				
				application.set("statusId", statusId);
				application.store();
			}
			else if (statusId.equalsIgnoreCase("APPLICANT_REJECT")) {
				GenericValue application = EntityUtil.getFirst(applications);
				application.set("discardFlag", "Y");
				application
						.set("discardDate", new Timestamp(currentdate.getTime()));
				application.set("statusId", statusId);
				application.store();
			}
			
			successMessage = "Action Performed successfully";
		} catch (GenericEntityException e) {
			Debug.logWarning(e, module);
			return ServiceUtil
					.returnError("Error occured during update applicant Joining Date");
		}
		

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, successMessage);

		return result;
	}
	 public static Map<String, Object> updateApplicantStatus(
				DispatchContext dctx, Map<String, ? extends Object> context) {
			Map<String, Object> result = ServiceUtil.returnSuccess();
			Delegator delegator = dctx.getDelegator();
			String partyId = (String) context.get("partyId");
			String statusId = (String) context.get("statusId");
			String userLoginId = (String) context.get("userLoginId");
			String issuedBy = (String) context.get("issuedBy");
			String cardId = (String) context.get("cardId");
			String employmentType = (String) context.get("employmentType");
			// java.sql.Timestamp joiningDate = (Timestamp)
			// context.get("joiningDate");
			
			String onProbation = null;
			String applicantPosId = "";

			if (checkEmployeeId(delegator, userLoginId)) {
				return ServiceUtil
						.returnError("Error occured during converting applicant to employee,Employee Id already taken");
			}

			result.put("partyId", partyId);

			/*List<Object> applicantStatuses = getStatuses(delegator,
					"EMPL_JOB_APP_STATUS");

			// Get the sequence of current status.
			@SuppressWarnings("unchecked")
			Map<String, Integer> statusSeqMap = (Map<String, Integer>) applicantStatuses
					.get(1);
			Integer statusSequence = statusSeqMap.get(statusId);

			// Get the next status
			Integer updatedStatusSequence = new Integer(
					statusSequence.intValue() + 1);

			@SuppressWarnings("unchecked")
			Map<Integer, String> seqStatusMap = (Map<Integer, String>) applicantStatuses
					.get(2);
			String updatedStatusId = seqStatusMap.get(updatedStatusSequence);

			if (updatedStatusId == null || updatedStatusId.equals("")) {
				return ServiceUtil
						.returnError("There have no next stage to forward.");
			}*/

			String successMessage = "";

			try {

				List<GenericValue> applications = delegator
						.findByAnd("EmploymentApp",
								UtilMisc.toMap("applyingPartyId", partyId));
				GenericValue application = EntityUtil.getFirst(applications);
				applicantPosId = application.getString("emplPositionId");
				java.sql.Timestamp joiningDate = (Timestamp) application
						.get("joiningDate");

				if (UtilValidate.isNotEmpty(application.get("isProbationaryFlag"))) {
					onProbation = application.get("isProbationaryFlag").toString();
				}

		if (UtilValidate.isNotEmpty(statusId)) {

					
					application.set("statusId", statusId);
					application.store();
				}

				//if ("CONFIRMATION".equals(updatedStatusId)) {*/
					// convert applicant to employee

					boolean success = ConvertApplicantToEmployee(delegator,
							partyId, userLoginId, joiningDate, onProbation,
							employmentType);
					
					if (!success) {
						return ServiceUtil
								.returnError("Error occured during converting applicant to employee");
					}
				//}
				/*if (UtilValidate.isNotEmpty(cardId)) {

					GenericValue newValue = delegator.makeValue("LogisticsItem");
					String issueId = delegator.getNextSeqId("LogisticsSeq");
					java.sql.Timestamp issuedDate = null;
					issuedDate = new java.sql.Timestamp(System.currentTimeMillis());

					newValue.set("issueId", issueId.trim());
					newValue.set("logisticsItemTypeId", "PROXIMITY_CARD");
					newValue.set("issuedTo", userLoginId.trim());
					newValue.set("issuedBy", issuedBy.trim());
					newValue.set("cardId", cardId.trim());
					newValue.set("issuedDate", issuedDate);
					newValue.set("isActive", "Y");

					delegator.create(newValue);

					List<GenericValue> persons = delegator.findByAnd("Person",
							UtilMisc.toMap("partyId", partyId));
					GenericValue person = EntityUtil.getFirst(persons);
					person.set("cardId", cardId.trim());
					person.store();

				}*/

				successMessage = "Applicant status changes successfully";

			} catch (GenericEntityException e) {
				Debug.logWarning(e, module);
				return ServiceUtil
						.returnError("Error occured during update applicant status");
			}
			
				

			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			result.put(ModelService.SUCCESS_MESSAGE, successMessage);

			return result;
		}
	 public static boolean ConvertApplicantToEmployee(Delegator delegator,
				String partyId, String userLoginId, Timestamp joiningDate,
				String onProbation, String employmentType)
				throws GenericEntityException {

			List<GenericValue> toBeStored = FastList.newInstance();

			java.sql.Timestamp now = new java.sql.Timestamp(
					new java.util.Date().getTime());

			// update party status
			try {
				GenericValue party = delegator.findByPrimaryKey("Party",
						UtilMisc.toMap("partyId", partyId));
				party.set("statusId", "EMPL_POS_ACTIVE");
				toBeStored.add(party);
			} catch (Exception e) {
				logger.debug("Exception to set status Id--->" + e);
			}

			
			String companyId = "";
			String emplPositionId = "";
			String payGradeId = "";

			try {
				List<GenericValue> applications = delegator
						.findByAnd("EmploymentApp",
								UtilMisc.toMap("applyingPartyId", partyId));
				GenericValue applicant = EntityUtil.getFirst(applications);
				emplPositionId = applicant.get("emplPositionId").toString();


				GenericValue emplPosition = delegator.findByPrimaryKey(
						"EmplPosition",
						UtilMisc.toMap("emplPositionId", emplPositionId));
				companyId = getParentParty(delegator, emplPosition.get("partyId")
						.toString());
				payGradeId = emplPosition.get("payGradeId") != null ? emplPosition
						.get("payGradeId").toString() : "";

			} catch (GenericEntityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Enter data into UserLogin
			if (UtilValidate.isEmpty(userLoginId)) {
				userLoginId = delegator.getNextSeqId("UserLogin");
			}

			String currentPassword = "{SHA}d23079bbca9105f78abff1d7f66e833231739923"; // hradmin

			GenericValue userLogin = delegator.makeValue("UserLogin");

			userLogin.set("userLoginId", userLoginId);
			userLogin.set("currentPassword", currentPassword);
			userLogin.set("partyId", partyId);

			toBeStored.add(userLogin);

			// Enter data into UserLoginSecurityGroup
			GenericValue userLoginSecurityGroup = delegator
					.makeValue("UserLoginSecurityGroup");

			userLoginSecurityGroup.set("userLoginId", userLoginId);
			userLoginSecurityGroup.set("groupId", "HUMANRES_EMPLOYEE");
			userLoginSecurityGroup.set("fromDate", now);

			toBeStored.add(userLoginSecurityGroup);

			// Enter data into UserPreference
			GenericValue userPreference = delegator.makeValue("UserPreference");

			userPreference.set("userLoginId", userLoginId);
			userPreference.set("userPrefTypeId", "ORGANIZATION_PARTY");
			userPreference.set("userPrefGroupTypeId", "GLOBAL_PREFERENCES");
			userPreference.set("userPrefValue", companyId);
			toBeStored.add(userPreference);

			// Enter data into EmplPositionFulfillment
			GenericValue emplPositionFulfillment = delegator
					.makeValue("EmplPositionFulfillment");

			emplPositionFulfillment.set("emplPositionId", emplPositionId);
			emplPositionFulfillment.set("partyId", partyId);
			if (onProbation != null) {
				emplPositionFulfillment.set("isProbationaryFlag", "probationary");
			} else {
				emplPositionFulfillment.set("isProbationaryFlag", "");
			}
			emplPositionFulfillment.set("fromDate", now);
			toBeStored.add(emplPositionFulfillment);

			// make role to employee
			Map<String, String> newPartyRole = UtilMisc.toMap("partyId", partyId,
					"roleTypeId", "EMPLOYEE");
			GenericValue partyRole = delegator.makeValue("PartyRole", newPartyRole);
			toBeStored.add(partyRole);

			// update relation with company.
			GenericValue employment = delegator.makeValue("Employment");

			employment.set("roleTypeIdFrom", "INTERNAL_ORGANIZATIO");
			employment.set("roleTypeIdTo", "EMPLOYEE");
			employment.set("partyIdTo", partyId);
			employment.set("partyIdFrom", companyId);
			employment.set("fromDate", joiningDate);
			employment.set("employmentType", employmentType);

			toBeStored.add(employment);

			try {
				delegator.storeAll(toBeStored);
			} catch (GenericEntityException e) {
				Debug.logWarning(e.getMessage(), module);
				return false;
			}

			// / salary related data insertion
			/*try {
				if (!payGradeId.equals("")) {
					assignEmployeeSalary(delegator, partyId, companyId, payGradeId);
				}
			} catch (Exception e) {
				Debug.logWarning(e.getMessage(), module);
			}*/
			// end salary related data insertion

			// Update User Login Id
			Map<String, String> sequenceValueItemCondition = UtilMisc.toMap(
					"seqName", "UserLogin");
			GenericValue sequenceValueItem = delegator.findByPrimaryKey(
					"SequenceValueItem", sequenceValueItemCondition);
			long userLoginInSequenceValueItem = 0;
			try {
				userLoginInSequenceValueItem = (Long) sequenceValueItem
						.get("seqId");
				if (userLoginId != null && userLogin.size() > 0) {
					long userLoginIdInLong = Long.parseLong(userLoginId);
					if (userLoginIdInLong > userLoginInSequenceValueItem) {
						sequenceValueItem.set("seqId", userLoginIdInLong);
						delegator.store(sequenceValueItem);
					}

				}
			} catch (Exception ex) {
				logger.debug("userLoginInSequenceValueItem can not process due to--------->"
						+ ex);
			}
			return true;
		}
	 
	 public static boolean checkEmployeeId(Delegator delegator,
				String userLoginId) {
			GenericValue userLogin = null;
			try {
				userLogin = delegator.findByPrimaryKey("UserLogin",
						UtilMisc.toMap("userLoginId", userLoginId));
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (userLogin != null && !UtilValidate.isEmpty(userLogin)) {
				return true;
			}
			return false;
		}
	 
	 public static List<Object> getStatuses(Delegator delegator,
				String statusType) {

			List<Object> list = FastList.newInstance();

			List<GenericValue> statuses = FastList.newInstance();
			try {
				statuses = delegator.findList("StatusItem", EntityCondition
						.makeCondition("statusTypeId", EntityOperator.EQUALS,
								statusType), null, null, null, false);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			list.add(statuses); // put the status list in the first (0) position

			Map<String, Integer> statusSeqMap = FastMap.newInstance();
			for (GenericValue genericValue : statuses) {
				statusSeqMap.put(genericValue.get("statusId").toString(),
						new Integer(genericValue.get("sequenceId").toString()));
			}
			list.add(statusSeqMap);// put the status sequence map in the 2nd (1)
									// position

			Map<Integer, String> seqStatusMap = FastMap.newInstance();
			for (GenericValue genericValue : statuses) {
				seqStatusMap.put(new Integer(genericValue.get("sequenceId")
						.toString()), genericValue.get("statusId").toString());
			}
			list.add(seqStatusMap);// put the sequence status map in the 3rd (2)
									// position

			return list;

		}
	 
	 public static Map<String, Object> processDiscardApplicant(
				DispatchContext dctx, Map<String, ? extends Object> context) {
			Map<String, Object> result = ServiceUtil.returnSuccess();
			Delegator delegator = dctx.getDelegator();
			String partyId = (String) context.get("partyId");
			String discardFlag = (String) context.get("discardFlag");
			String discardReason = (String) context.get("discardReason");

			result.put("partyId", partyId);

			String successMessage = "";
			java.sql.Date currentdate = new java.sql.Date(
					System.currentTimeMillis());
			

			try {

				List<GenericValue> applications = delegator
						.findByAnd("EmploymentApp",
								UtilMisc.toMap("applyingPartyId", partyId));
				GenericValue application = EntityUtil.getFirst(applications);
				application.set("reasonForDicardApplicant", discardReason);
				application.set("discardFlag", discardFlag);
				application
						.set("discardDate", new Timestamp(currentdate.getTime()));
				application.store();

				successMessage = "Applicant is dicarded successfully";

			} catch (GenericEntityException e) {
				Debug.logWarning(e, module);
				return ServiceUtil
						.returnError("Error occured during discard applicant");
			}
			List<GenericValue> statusUpdate = null;
	   		String statusId = "APPLICANT_REJECT";
	   		GenericValue dicardApplicant = null;
	   		try {
	   			statusUpdate = delegator.findByAnd("EmploymentApp",
	   					UtilMisc.toMap("applyingPartyId", partyId));
	   			
	   			dicardApplicant = EntityUtil.getFirst(statusUpdate);
	   			dicardApplicant.set("statusId", statusId);
	   			dicardApplicant.store();
	   			
	   			

	   		} catch (Exception e) {
	   			// TODO Auto-generated catch block
	   			e.printStackTrace();
	   		}

			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			result.put(ModelService.SUCCESS_MESSAGE, "successMessage");

			return result;
	
}
	 
	 
	 public static Map<String, Object> changeJoingDate(DispatchContext dctx,
				Map<String, ? extends Object> context) {
			Map<String, Object> result = ServiceUtil.returnSuccess();
			Delegator delegator = dctx.getDelegator();
			String partyIdTo = (String) context.get("partyIdTo");
			java.sql.Timestamp changeDate = (java.sql.Timestamp) context
					.get("changeDate");
			/*logger.debug("Change Date " + changeDate + " PartyId: " + partyIdTo);*/
			Timestamp fromDateEmployement = null;

			try {
				List<GenericValue> isEmpl = delegator.findByAnd("Employment",
						UtilMisc.toMap("partyIdTo", partyIdTo));
				if (UtilValidate.isNotEmpty(isEmpl)) {
					GenericValue empl = EntityUtil.getFirst(isEmpl);

					/* insert a new row in Employment Service util . return error */
					fromDateEmployement = empl.getTimestamp("fromDate");
					empl.set("fromDate", changeDate);
					delegator.create(empl);
					
					List<GenericValue> agreementEmploymentAppl = delegator
							.findByAnd("AgreementEmploymentAppl",
									UtilMisc.toMap("fromDate", fromDateEmployement));
					if (UtilValidate.isNotEmpty(agreementEmploymentAppl)) {
						/* logger.debug("AgreementEmploymentAppl  Table"); */
						GenericValue agreementEmplAppl = EntityUtil
								.getFirst(agreementEmploymentAppl);

						delegator.removeValue(agreementEmplAppl);
						agreementEmplAppl.set("fromDate", changeDate);
						delegator.create(agreementEmplAppl);
					}

					List<GenericValue> payHistory = delegator.findByAnd(
							"PayHistory",
							UtilMisc.toMap("fromDate", fromDateEmployement));
					/* logger.debug("PayHistory  Table: "+payHistory.size()); */
					if (UtilValidate.isNotEmpty(payHistory)) {
						GenericValue payHis = EntityUtil.getFirst(payHistory);
						logger.debug("PayHistory  Table");

						delegator.removeValue(payHis);
						payHis.set("fromDate", changeDate);
						delegator.create(payHis);
						logger.debug("PayHistory  Table Successfully updated...s");
					}

					List<GenericValue> unemploymentClaim = delegator.findByAnd(
							"UnemploymentClaim",
							UtilMisc.toMap("fromDate", fromDateEmployement));
					if (UtilValidate.isNotEmpty(unemploymentClaim)) {
						GenericValue unEmplClaim = EntityUtil
								.getFirst(unemploymentClaim);

						delegator.removeValue(unEmplClaim);
						unEmplClaim.set("fromDate", changeDate);
						delegator.create(unEmplClaim);
					}
				}

				isEmpl = delegator.findByAnd("Employment", UtilMisc.toMap(
						"partyIdTo", partyIdTo, "fromDate", fromDateEmployement));
				if (UtilValidate.isNotEmpty(isEmpl)) {
					GenericValue RemEmpl = EntityUtil.getFirst(isEmpl);
					delegator.removeValue(RemEmpl);
				}
				
				/*also Update join date to EmploymentApp table if there have data */
				List<GenericValue> employmentApp = delegator.findByAnd("EmploymentApp",
						UtilMisc.toMap("applyingPartyId", partyIdTo));
				if (UtilValidate.isNotEmpty(employmentApp)) {
					GenericValue emplApp = EntityUtil.getFirst(employmentApp);

					/* insert a new row in Employment Service util . return error */
					fromDateEmployement = emplApp.getTimestamp("joiningDate");
					emplApp.set("joiningDate", changeDate);
					delegator.store(emplApp);
				}
				

			} catch (Exception e) {
				logger.debug("Exception in Update Employment Table --->" + e);
				e.printStackTrace();
				String errMsg = "Employee Join Date Updated Failed.";
				Debug.logInfo(errMsg, module);

				Map<String, Object> results = ServiceUtil.returnSuccess();

				results.put(ModelService.ERROR_MESSAGE, errMsg);

				return results;
			}
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			result.put(ModelService.SUCCESS_MESSAGE,
					"Employee Join Date Updated Successfully.");
			return result;
		}
}
