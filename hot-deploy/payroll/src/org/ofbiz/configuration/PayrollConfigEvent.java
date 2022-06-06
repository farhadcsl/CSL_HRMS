package org.ofbiz.configuration;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Date: 12/20/13
 * Time: 1:27 PM
 */
public class PayrollConfigEvent {
	 public static String createInvoiceItemType(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();


	        String invoiceItemTypeId = "";
	        String parentTypeId = "";
	        String invoiceCategory = "";
	        String glAccountId = "";
	        String creditGlAccountId = "";
	        String description = "";

	        if (null != request.getParameter("invoiceItemTypeId")) {
	            invoiceItemTypeId = request.getParameter("invoiceItemTypeId");
	        }
	        if (null != request.getParameter("parentTypeId")) {
	            parentTypeId = request.getParameter("parentTypeId");
	        }
	        if (null != request.getParameter("category")) {
	            invoiceCategory = request.getParameter("category");
	        }
	        if (null != request.getParameter("defaultGlAccountId")) {
	            glAccountId = request.getParameter("defaultGlAccountId");
	        }
	        if (null != request.getParameter("defaultCreditGlAccountId")) {
	            creditGlAccountId = request.getParameter("defaultCreditGlAccountId");
	        }
	        if (null != request.getParameter("description")) {
	            description = request.getParameter("description");
	        }

	        Map invoiceItemType = UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId, "parentTypeId", parentTypeId, "defaultGlAccountId", glAccountId, "category", invoiceCategory, "description", description);
	        GenericValue InvoiceItemType = delegator.makeValue("InvoiceItemType", invoiceItemType);
	        try {
	            delegator.createOrStore(InvoiceItemType);
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }


	        HttpSession session = request.getSession();
	        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
	        String partyId = userLogin.get("partyId").toString();
	        List<GenericValue> companyParty = null;

	        try {
	            companyParty = delegator.findByAnd("EmploymentAndPerson",
	                    UtilMisc.toMap("partyIdTo", partyId));

	        } catch (Exception e) {

	        }

	        String companyId = null;
	        if (companyParty.size() != 0) {
	            companyId = companyParty.get(0).getString("partyIdFrom");
	        }

	        Map creditInvoiceItemType = UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId, "glAccountId", creditGlAccountId, "organizationPartyId", companyId);
	        GenericValue CreditInvoiceItemType = delegator.makeValue("InvoiceItemTypeGlAccount", creditInvoiceItemType);
	        try {
	            delegator.createOrStore(CreditInvoiceItemType);
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }

	        request.setAttribute("_EVENT_MESSAGE_", "Invoice Item Type Added Successfully");
	        return "success";
	    }
	
}