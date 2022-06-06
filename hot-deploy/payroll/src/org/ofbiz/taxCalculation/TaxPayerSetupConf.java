package org.ofbiz.taxCalculation;

import java.math.BigDecimal;
import java.sql.Timestamp;
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


public class TaxPayerSetupConf {


	public static String updateTaxPayerSetup(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        String taxPayerTypeId = "";
        if (null != request.getParameter("taxPayerTypeId")) {
        	taxPayerTypeId = request.getParameter("taxPayerTypeId");
        }

        if (taxPayerTypeId.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Tax Payer Id can not be empty");
            return "error";
        }
		String partyId = request.getParameter("partyId");
		String employeeId = request.getParameter("employeeId");
		String payGradeId = request.getParameter("payGradeId");
		String salaryLevelId = request.getParameter("salaryLevelId");
		String salaryTemplateId = request.getParameter("salaryTemplateId");
		String basicSalaryStepId = request.getParameter("basicSalaryStepId");
		String comments = request.getParameter("comments");
		String taxConditionId = delegator.getNextSeqId("TaxCondition");
		
		Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
		///---------------------------- PersonPayrollInfo Setup-----------------------//   
	    GenericValue personPayrollInfo = null;
		try { 
			personPayrollInfo = delegator.findByPrimaryKey("PersonPayrollInfo", UtilMisc.toMap("partyId", partyId));
			
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
    	Map<String, Object> PersonPayrollInfoIn = FastMap.newInstance();
    	PersonPayrollInfoIn.put("partyId", partyId);
    	PersonPayrollInfoIn.put("employeeId", employeeId);
    	PersonPayrollInfoIn.put("payGradeId", payGradeId);
    	PersonPayrollInfoIn.put("salaryLevelId", salaryLevelId);
    	PersonPayrollInfoIn.put("salaryTemplateId", salaryTemplateId);
    	PersonPayrollInfoIn.put("basicSalaryStepId", basicSalaryStepId);
    	PersonPayrollInfoIn.put("taxPayerTypeId", taxPayerTypeId);
    	PersonPayrollInfoIn.put("comments", comments);
    	PersonPayrollInfoIn.put("isActive", "Y");
    	PersonPayrollInfoIn.put("dateAdded", currentDatetime);
	    try {
			GenericValue PersonPayrollInfoSetup = delegator
				.makeValue("PersonPayrollInfo",
						UtilMisc.toMap(PersonPayrollInfoIn));
			if(personPayrollInfo == null){
				PersonPayrollInfoSetup.create();
			}
			else{
				taxPayerTypeId=personPayrollInfo.getString(taxPayerTypeId);
				PersonPayrollInfoSetup.store();
			}
			// ---------------------- History Data Added ---------------------------- //
	    	Map<String, Object> PersonPayrollInfoHisIn = FastMap.newInstance();
	    	PersonPayrollInfoHisIn.put("partyId", partyId);
	    	PersonPayrollInfoHisIn.put("employeeId", employeeId);
	    	PersonPayrollInfoHisIn.put("payGradeId", payGradeId);
	    	PersonPayrollInfoHisIn.put("salaryLevelId", salaryLevelId);
	    	PersonPayrollInfoHisIn.put("salaryTemplateId", salaryLevelId);
	    	PersonPayrollInfoHisIn.put("basicSalaryStepId", taxPayerTypeId);
	    	PersonPayrollInfoHisIn.put("taxPayerTypeId", taxPayerTypeId);
	    	PersonPayrollInfoHisIn.put("comments", comments);
	    	PersonPayrollInfoHisIn.put("isActive", "Y");
	    	PersonPayrollInfoHisIn.put("dateAdded", currentDatetime);
	    	 try {
	 			GenericValue PersonPayrollInfoHisSetup = delegator
	 				.makeValue("PersonPayrollInfoHis",
	 						UtilMisc.toMap(PersonPayrollInfoHisIn));
	 			PersonPayrollInfoHisSetup.create();
	    	 }
	    	 catch(Exception e){}
	    } catch (Exception e) { }

		
        request.setAttribute("_EVENT_MESSAGE_", "Tax Payer Successfully Configure");
        return "success";
    }

}