package org.ofbiz.humanres;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanres.PartyQual;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class PartyQual {
	
	public static final String module = PartyQual.class.getName();

	public static Map<String, Object> createQualification(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		Delegator delegator = dctx.getDelegator();

		String employeeId = (String) context.get("employeeId");
		String partyQualTypeId = (String) context.get("partyQualTypeId");
		String qualificationDesc = (String) context.get("qualificationDesc");
		Long passingYear = (Long) context.get("passingYear");
		String title = (String) context.get("title");
		String statusId = (String) context.get("statusId");
	
		String countryGeoId = (String) context.get("countryGeoId");
		String remarks = (String) context.get("remarks");
		String partyId = "";
		
		Timestamp fromDate = new Timestamp(System.currentTimeMillis());
		try {

			
			

			/* Check null Employee Selection */
			
			List<GenericValue> PartyAndPersonAndUserLoginAndEmployments = delegator.findByAnd("PartyAndPersonAndUserLoginAndEmployment",
							UtilMisc.toMap("userLoginId", employeeId));

			GenericValue partyAndPersonAndUserLoginAndEmployment = EntityUtil
					.getFirst(PartyAndPersonAndUserLoginAndEmployments);
			if (UtilValidate.isEmpty(partyAndPersonAndUserLoginAndEmployment)) {
				String errMsg = "Fail to Register, Employee Missing";
				Debug.logInfo(errMsg, module);
				Map<String, Object> results = ServiceUtil.returnSuccess();

				results.put(ModelService.ERROR_MESSAGE, errMsg);

				return results;
			}

			/* End Check null Employee Selection */

				partyId = (String) partyAndPersonAndUserLoginAndEmployment
						.get("partyId");
			GenericValue newValue = delegator.makeValue("PartyQual");

			newValue.set("partyId", partyId.trim());
			newValue.set("employeeId", employeeId);
			newValue.set("partyQualTypeId", partyQualTypeId);
			newValue.set("qualificationDesc", qualificationDesc);
			newValue.set("passingYear", passingYear);
			newValue.set("title", title);
			newValue.set("fromDate", fromDate);
			newValue.set("statusId", statusId);
			newValue.set("verifStatusId", "PQV_NOT_VERIFIED");
			newValue.set("countryGeoId", countryGeoId);
			newValue.set("remarks", remarks);
			delegator.create(newValue);

			result.put(ModelService.RESPONSE_MESSAGE,
					ModelService.RESPOND_SUCCESS);
			result.put(ModelService.SUCCESS_MESSAGE, "Qualification added Successfully");

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	/* This Method works for inserting new Professional qualification 
	 * Written Muntashir
	 * */
	
	public static Map<String, Object> createProfQual(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		Delegator delegator = dctx.getDelegator();
		String courseTitle = (String) context.get("courseTitle");
		String instituteName = (String) context.get("instituteName");
		Date courseCompletionDate = (Date) context.get("courseCompletionDate");
		String courseDuration = (String) context.get("courseDuration");
		String statusId = (String) context.get("statusId");
		String countryGeoId = (String) context.get("countryGeoId");
		String remarks = (String) context.get("remarks");
		String partyId = (String) context.get("partyId");
		String title = (String) context.get("title");
		Timestamp fromDate = new Timestamp(System.currentTimeMillis());
		
		try {

			List<GenericValue> PartyAndPersonAndUserLoginAndEmployments = delegator.findByAnd("PartyAndPersonAndUserLoginAndEmployment",
							UtilMisc.toMap("partyId", partyId));

			GenericValue partyAndPersonAndUserLoginAndEmployment = EntityUtil
					.getFirst(PartyAndPersonAndUserLoginAndEmployments);
			if (UtilValidate.isEmpty(partyAndPersonAndUserLoginAndEmployment)) {
				String errMsg = "Fail to Register, Employee Missing";
				Debug.logInfo(errMsg, module);
				Map<String, Object> results = ServiceUtil.returnSuccess();

				results.put(ModelService.ERROR_MESSAGE, errMsg);

				return results;
			}
			String employeeId = (String) partyAndPersonAndUserLoginAndEmployment
					.get("userLoginId");
		GenericValue newValue = delegator.makeValue("ProfQual");
		
		newValue.set("partyId", partyId.trim());
		newValue.set("courseTitle", courseTitle);
		newValue.set("instituteName", instituteName);
		newValue.set("courseCompletionDate", courseCompletionDate);
		newValue.set("courseDuration", courseDuration);
		newValue.set("fromDate", fromDate);
		newValue.set("statusId", statusId);
		newValue.set("verifStatusId", "PQV_NOT_VERIFIED");
		newValue.set("countryGeoId", countryGeoId);
		newValue.set("remarks", remarks);
		newValue.set("title", title);
		newValue.set("employeeId", employeeId);
		delegator.create(newValue);

		result.put(ModelService.RESPONSE_MESSAGE,
				ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Professional Qualification added Successfully");
		
		} catch (GenericEntityException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
		}
		result.put("partyId", partyId);
	return result;
	
	}
	
	
	
	//------------------- Previous Employment History----------------------------------//

	public static Map<String, Object> createPreviousEmploymentHistory(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		Delegator delegator = dctx.getDelegator();
		String partyId = (String) context.get("partyId");
		String orgName = (String) context.get("orgName");
		String address = (String) context.get("address");
		String description = (String) context.get("description");
		String position = (String) context.get("position");
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String contactNO = (String) context.get("contactNO");
		
		try {

			List<GenericValue> PartyAndPersonAndUserLoginAndEmployments = delegator.findByAnd("PartyAndPersonAndUserLoginAndEmployment",
							UtilMisc.toMap("partyId", partyId));

			GenericValue partyAndPersonAndUserLoginAndEmployment = EntityUtil
					.getFirst(PartyAndPersonAndUserLoginAndEmployments);
			if (UtilValidate.isEmpty(partyAndPersonAndUserLoginAndEmployment)) {
				String errMsg = "Fail to Register, Employee Missing";
				Debug.logInfo(errMsg, module);
				Map<String, Object> results = ServiceUtil.returnSuccess();

				results.put(ModelService.ERROR_MESSAGE, errMsg);

				return results;
			}
			String employeeId = (String) partyAndPersonAndUserLoginAndEmployment
					.get("userLoginId");
		GenericValue newValue = delegator.makeValue("PrevEmployment");
		
		newValue.set("partyId", partyId.trim());
		newValue.set("orgName", orgName);
		newValue.set("address", address);
		newValue.set("description", description);
		newValue.set("position", position);
		newValue.set("fromDate", fromDate);
		newValue.set("thruDate", thruDate);
		newValue.set("statusId", "PQV_NOT_VERIFIED");
		newValue.set("verifPartyId", "");
		newValue.set("contactNO", contactNO);
		//newValue.set("employeeId", employeeId);
		delegator.create(newValue);

		result.put(ModelService.RESPONSE_MESSAGE,
				ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Previous Employment History added Successfully");
		
		} catch (GenericEntityException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
		}
		result.put("partyId", partyId);
	return result;
	
	}
}
