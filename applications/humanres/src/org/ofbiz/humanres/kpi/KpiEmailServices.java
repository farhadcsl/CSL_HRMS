package org.ofbiz.humanres.kpi;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class KpiEmailServices {
	public static final String module = KpiEmailServices.class.getName();
	static String defaultMailAgent = UtilProperties.getPropertyValue(
			"general.properties", "defaultMailAgent");

	public static Map<String, Object> sendKpiEmail(DispatchContext ctx,
			Map<String, ? extends Object> context, String approverPartyId,
			String employeePartyId, String kpiAction) {

		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		String sendFrom = "";
		String sendToApproved = "";
		String footerMessage = "This is a system generated email, if you think it was sent incorrectly, please contact your System Administrator.";
		// Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		String fromTitle = ""; // person or employee
		String fromMailType = "";
		String fromMailDescription = "";
		String fromAction = "";

		String toTitle = ""; // hradmin or any other approver
		String toMailType = "";
		String toMailDescription = "";
		String toAction = "";

		String successMessage = "";

		boolean emailSendToEmployee = false;
		boolean emailSendToApprover = false;

		if (kpiAction.equals("KPI_REVIEW_REQ")) {

			emailSendToEmployee = true;
			emailSendToApprover = true;

			fromTitle = "CSL KPI Updated Notification";
			fromMailType = "KPI Updated";
			fromMailDescription = "Your KPI has send for Manager Review";
			fromAction = "Wait for review and you will get the notificaton soon";

			toTitle = "CSL KPI Updated Notification";
			toMailType = "KPI Updated";
			toMailDescription = "KPI Updated and requested to Review";

			successMessage = "KPI sends to Manager for Review Successfully";

		} else if (kpiAction.equals("KPI_REVIEWED")) {

			emailSendToEmployee = true;
			emailSendToApprover = false;

			fromTitle = "CSL KPI Review Notification";
			fromMailType = "KPI Reviewed";
			fromMailDescription = "Applied KPI is Reviewed";
			fromAction = "Please check your KPI in your profile";

			toTitle = "CSL KPI Review Notification";
			toMailType = "KPI Reviewed";
			toMailDescription = "Applied KPI Reviewed";
			toAction = "Please check your KPI in your profile";

			successMessage = "Applied KPI is Reviewed";
		}

		/*
		 * if (UtilValidate.isEmpty(partyId)) { return
		 * ServiceUtil.returnError(UtilProperties
		 * .getMessage(ServiceUtil.resource,
		 * "serviceUtil.party_id_missing",locale)); }
		 */

		// get the sendFrom value using partyId
		sendFrom = getEmailByParty(delegator, employeePartyId);
		if (sendFrom.trim().equals(""))
			successMessage = successMessage
					.concat(", Employee Email is not found");

		// get the sendTo value using approverPartyId
		sendToApproved = getEmailByParty(delegator, approverPartyId);
		if (sendToApproved.trim().equals(""))
			successMessage = successMessage
					.concat(", Approver Email is not found");

		// send email to human admin
		Map<String, Object> emailCtx = FastMap.newInstance();

		// set the body parameters
		Map<String, Object> bodyParameters = FastMap.newInstance();

		// send mail to approver
		bodyParameters.put("title", toTitle);
		bodyParameters.put("mailType", toMailType);
		bodyParameters.put("mailDescription", toMailDescription);
		bodyParameters.put("action", toAction);
		bodyParameters.put("priority", "High");
		bodyParameters.put("footerMsg", footerMessage);

		emailCtx.put("sendTo", sendToApproved);
		emailCtx.put("sendFrom", sendFrom);
		emailCtx.put("subject", "KPI Review Notification");
		emailCtx.put("userLogin", userLogin);
		emailCtx.put("bodyParameters", bodyParameters);

		if (emailSendToApprover && !sendToApproved.equals("")) {
			try {
				sendContessaMail(ctx, emailCtx, bodyParameters);

			} catch (Exception e) {
				// return ServiceUtil.returnError("send email failed");
				successMessage = successMessage.concat(", Send Email failed");
			}
		}

		// send mail to requested person
		bodyParameters.put("title", fromTitle);
		bodyParameters.put("mailType", fromMailType);
		bodyParameters.put("mailDescription", fromMailDescription);
		bodyParameters.put("action", fromAction);
		bodyParameters.put("priority", "High");
		bodyParameters.put("footerMsg", footerMessage);

		emailCtx.put("sendTo", sendFrom);
		emailCtx.put("sendFrom", defaultMailAgent);
		emailCtx.put("subject", "KPI Sends for Manager Review");
		emailCtx.put("userLogin", userLogin);
		emailCtx.put("bodyParameters", bodyParameters);

		if (emailSendToEmployee && !sendFrom.equals("")) {
			try {
				sendContessaMail(ctx, emailCtx, bodyParameters);

			} catch (Exception e) {
				// return ServiceUtil.returnError("send email failed");
				successMessage = successMessage.concat(", Send Email failed");

			}
		}

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, successMessage);
		/*
		 * result.put(ModelService.ERROR_MESSAGE,
		 * "Leave will not proceed due to some error.");
		 */

		return result;

	}

	public static String getEmailByParty(Delegator delegator, String partyId) {

		String email = "";

		try {
			List<GenericValue> listPartyContactMechs = delegator.findList(
					"PartyContactMech", EntityCondition.makeCondition(
							"partyId", EntityOperator.EQUALS, partyId), null,
					null, null, false);
			String contactMechIdToCheck = "";
			GenericValue contactMech = null;
			for (GenericValue val : listPartyContactMechs) {
				contactMechIdToCheck = (String) val.get("contactMechId");
				List<EntityExpr> exprs = FastList.newInstance();
				exprs.add(EntityCondition.makeCondition("contactMechId",
						EntityOperator.EQUALS, contactMechIdToCheck));
				exprs.add(EntityCondition.makeCondition("contactMechTypeId",
						EntityOperator.EQUALS, "EMAIL_ADDRESS"));

				List<GenericValue> listContactMechs = delegator.findList(
						"ContactMech", EntityCondition.makeCondition(exprs,
								EntityOperator.AND), null, null, null, false);
				if (listContactMechs.size() > 0) {
					contactMech = listContactMechs.get(0);
				}

			}

			if (contactMech != null)
				email = (String) contactMech.get("infoString");

			if (email == null)
				email = "";

		} catch (GenericEntityException e) {
		}

		return email;

	}

	public static Map<String, Object> sendContessaMail(DispatchContext ctx,
			Map<String, ?> serviceContext, Map<String, ?> bodyParam) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		// get the general template
		GenericValue emailTemplateSetting = null;
		try {
			emailTemplateSetting = delegator.findOne("EmailTemplateSetting",
					true, "emailTemplateSettingId", "GEN_EMAIL_TEMPLATE");/**/
		} catch (GenericEntityException e1) {
			Debug.logError(e1, module);
		}

		// set the body parameters
		Map<String, Object> parameters = FastMap.newInstance();

		parameters.put("title", bodyParam.get("title"));
		parameters.put("mailType", bodyParam.get("mailType"));
		parameters.put("mailDescription", bodyParam.get("mailDescription"));
		parameters.put("action", bodyParam.get("action"));
		parameters.put("priority", bodyParam.get("priority"));
		parameters.put("actionUrl", bodyParam.get("actionUrl"));
		parameters.put("footerMsg", bodyParam.get("footerMsg"));

		// set the email context
		Map<String, Object> emailCtx = FastMap.newInstance();
		emailCtx.put("sendTo", serviceContext.get("sendTo"));
		emailCtx.put("sendFrom", serviceContext.get("sendFrom"));
		emailCtx.put("sendCc", serviceContext.get("sendCc"));
		emailCtx.put("emailTemplateSettingId",
				emailTemplateSetting.getString("emailTemplateSettingId"));
		emailCtx.put("userLogin", serviceContext.get("userLogin"));
		emailCtx.put("bodyParameters", parameters);

		// send off the email async so we will retry on failed attempts
		// SC 20060405: Changed to runSync because runAsync kept getting an
		// error:
		// Problem serializing service attributes (Cannot serialize object of
		// class java.util.PropertyResourceBundle)
		try {
			dispatcher.runAsync("sendMailFromTemplateSetting", emailCtx);
		} catch (GenericServiceException e) {
			Debug.logError(e, "Problem sending mail", module);
			// this is fatal; we will rollback and try again later
			return ServiceUtil.returnError("send email failed");
		}

		return result;
	}


	public static Map<String, Object> sendMailByOfbizMailServer(
			DispatchContext ctx, Map<String, ?> context,
			Map<String, ?> bodyParam) {

		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> emailCtx = FastMap.newInstance();

		// get the general template

		GenericValue emailTemplateSetting = null;

		try {
			emailTemplateSetting = delegator.findOne("EmailTemplateSetting",
					true, "emailTemplateSettingId", "GEN_EMAIL_TEMPLATE");/**/
		} catch (GenericEntityException e1) {
			Debug.logError(e1, module);
		}

		Map<String, Object> parameters = FastMap.newInstance();

		parameters.put("title", bodyParam.get("title"));
		parameters.put("mailType", bodyParam.get("mailType"));
		parameters.put("mailDescription", bodyParam.get("mailDescription"));
		parameters.put("action", bodyParam.get("action"));
		parameters.put("priority", bodyParam.get("priority"));
		parameters.put("actionUrl", bodyParam.get("actionUrl"));
		parameters.put("footerMsg", bodyParam.get("footerMsg"));
		parameters.put("fullName", bodyParam.get("fullName"));
		parameters.put("remarks", bodyParam.get("remarks"));
		// set the email context
		emailCtx.put("sendTo", context.get("sendTo"));
		emailCtx.put("emailTemplateSettingId",
				emailTemplateSetting.getString("emailTemplateSettingId"));
		emailCtx.put("userLogin", context.get("userLogin"));
		emailCtx.put("bodyParameters", parameters);
		Map<String, Object> tmpResult = null;

		try {
			dispatcher.runAsync("sendMailFromTemplateSetting", emailCtx);
		} catch (GenericServiceException e) {
			Debug.logError(e, "Problem sending mail", module);
			// this is fatal; we will rollback and try again later
			return ServiceUtil.returnError("send email failed");
		}

		return result;
	}

	public static String getFullNameByPartyId(Delegator delegator,
			String partyId) {
		GenericValue partyPersonnalInfo = null;
		try {
			partyPersonnalInfo = delegator.findByPrimaryKey("PartyAndPerson",
					UtilMisc.toMap("partyId", partyId));

			String firstName = "";
			String middleName = "";
			String lastName = "";
			try {
				if (UtilValidate.isNotEmpty(partyPersonnalInfo.get("firstName")
						.toString())) {
					firstName = partyPersonnalInfo.get("firstName").toString();
				}
			} catch (Exception e) {
			}
			try {
				if (UtilValidate.isNotEmpty(partyPersonnalInfo.get("firstName")
						.toString())) {
					middleName = partyPersonnalInfo.get("middleName")
							.toString();
				}
			} catch (Exception e) {
			}
			try {
				if (UtilValidate.isNotEmpty(partyPersonnalInfo.get("lastName")
						.toString())) {
					lastName = partyPersonnalInfo.get("lastName").toString();
				}
			} catch (Exception e) {
			}
			String partyName = firstName + " " + middleName + " " + lastName;
			return partyName;
		} catch (Exception e) {
			return "error";
		}
	}

}