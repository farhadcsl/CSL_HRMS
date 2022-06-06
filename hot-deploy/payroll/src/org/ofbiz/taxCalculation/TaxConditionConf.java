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

import com.sun.org.apache.xerces.internal.impl.dv.xs.DecimalDV;

public class TaxConditionConf {

	public static String createTaxCondition(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        String taxCalculationTypeId = "";
        if (null != request.getParameter("taxCalculationTypeId")) {
        	taxCalculationTypeId = request.getParameter("taxCalculationTypeId");
        }

        if (taxCalculationTypeId.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Tax Colculation Type Id can not be empty");
            return "error";
        }
        String stepOrderStr = "";
        BigDecimal stepOrder = new BigDecimal(0);
        BigDecimal fromAmount = new BigDecimal(0);
        BigDecimal toAmount = new BigDecimal(0);
        BigDecimal amount = new BigDecimal(0);
        BigDecimal deductionPercentage = new BigDecimal(0);
        if (null != request.getParameter("stepOrder")) {
        	stepOrderStr = request.getParameter("stepOrder");
        	try{
        	stepOrder = new BigDecimal(stepOrderStr.replaceAll(",", ""));
        	}
        	catch(Exception e){
                request.setAttribute("_ERROR_MESSAGE_", "Step Order must be a number");
                return "error";
        	}
        }

        if (stepOrderStr.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Step Order can not be empty");
            return "error";
        }
        String fromAmountStr = "";
        if (null != request.getParameter("fromAmount")) {
        	fromAmountStr = request.getParameter("fromAmount");
        	try{
        		fromAmount = new BigDecimal(fromAmountStr.replaceAll(",", ""));
        	}
        	catch(Exception e){
                request.setAttribute("_ERROR_MESSAGE_", "From Amount must be a number");
                return "error";
        	}
        }
        String toAmountStr = "";
        if (null != request.getParameter("toAmount")) {
        	toAmountStr = request.getParameter("toAmount");
        	try{
        		toAmount = new BigDecimal(toAmountStr.replaceAll(",", ""));
        		amount=toAmount.subtract(fromAmount);
        	}
        	catch(Exception e){
                request.setAttribute("_ERROR_MESSAGE_", "To Amount must be a number");
                return "error";
        	}
        }
        String deductionPercentageStr = "";
        if (null != request.getParameter("deductionPercentage")) {
        	deductionPercentageStr = request.getParameter("deductionPercentage");
        	try{
        		deductionPercentage = new BigDecimal(deductionPercentageStr.replaceAll("%", ""));
        	}
        	catch(Exception e){
                request.setAttribute("_ERROR_MESSAGE_", "Percentage must be a number");
                return "error";
        	}
        }

        if (deductionPercentageStr.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Percentage can not be empty");
            return "error";
        }
		String comments = request.getParameter("comments");
		String taxConditionId = delegator.getNextSeqId("TaxCondition");
		
		Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
		Map<String, Object> taxConditionIn = FastMap.newInstance();
		taxConditionIn.put("taxConditionId", taxConditionId);
		taxConditionIn.put("taxCalculationTypeId", taxCalculationTypeId);
		taxConditionIn.put("stepOrder", stepOrder);
		taxConditionIn.put("fromAmount", fromAmount);
		taxConditionIn.put("toAmount", toAmount);
		taxConditionIn.put("amount", amount);
		taxConditionIn.put("deductionPercentage", deductionPercentage);
		taxConditionIn.put("comments", comments);
		taxConditionIn.put("isActive", "Y");
		taxConditionIn.put("dateAdded", currentDatetime);
	    try {
			GenericValue taxConditionInSetup = delegator
				.makeValue("TaxCondition",
						UtilMisc.toMap(taxConditionIn));
			taxConditionInSetup.create();   

			
	    } catch (Exception e) { }

        request.setAttribute("_EVENT_MESSAGE_", "Tax Condition Successfully Created");
        return "success";
    }


	public static String updateTaxCondition(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        String taxCalculationTypeId = "";
        if (null != request.getParameter("taxCalculationTypeId")) {
        	taxCalculationTypeId = request.getParameter("taxCalculationTypeId");
        }

        if (taxCalculationTypeId.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Tax Colculation Type Id can not be empty");
            return "error";
        }
        String stepOrderStr = "";
        BigDecimal stepOrder = new BigDecimal(0);
        BigDecimal fromAmount = new BigDecimal(0);
        BigDecimal toAmount = new BigDecimal(0);
        BigDecimal amount = new BigDecimal(0);
        BigDecimal deductionPercentage = new BigDecimal(0);
        if (null != request.getParameter("stepOrder")) {
        	stepOrderStr = request.getParameter("stepOrder");
        	try{
        	stepOrder = new BigDecimal(stepOrderStr.replaceAll(",", ""));
        	}
        	catch(Exception e){
                request.setAttribute("_ERROR_MESSAGE_", "Step Order must be a number");
                return "error";
        	}
        }

        if (stepOrderStr.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Step Order can not be empty");
            return "error";
        }
        String fromAmountStr = "";
        if (null != request.getParameter("fromAmount")) {
        	fromAmountStr = request.getParameter("fromAmount");
        	try{
        		fromAmount = new BigDecimal(fromAmountStr.replaceAll(",", ""));
        	}
        	catch(Exception e){
                request.setAttribute("_ERROR_MESSAGE_", "From Amount must be a number");
                return "error";
        	}
        }
        String toAmountStr = "";
        if (null != request.getParameter("toAmount")) {
        	toAmountStr = request.getParameter("toAmount");
        	try{
        		toAmount = new BigDecimal(toAmountStr.replaceAll(",", ""));
        		amount=toAmount.subtract(fromAmount);
        	}
        	catch(Exception e){
                request.setAttribute("_ERROR_MESSAGE_", "To Amount must be a number");
                return "error";
        	}
        }
        String deductionPercentageStr = "";
        if (null != request.getParameter("deductionPercentage")) {
        	deductionPercentageStr = request.getParameter("deductionPercentage");
        	try{
        		deductionPercentage = new BigDecimal(deductionPercentageStr.replaceAll("%", ""));
        	}
        	catch(Exception e){
                request.setAttribute("_ERROR_MESSAGE_", "Percentage must be a number");
                return "error";
        	}
        }

        if (deductionPercentageStr.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Percentage can not be empty");
            return "error";
        }
		String comments = request.getParameter("comments");
		String taxConditionId = request.getParameter("taxConditionId");
		
		Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
		Map<String, Object> taxConditionIn = FastMap.newInstance();
		taxConditionIn.put("taxConditionId", taxConditionId);
		taxConditionIn.put("taxCalculationTypeId", taxCalculationTypeId);
		taxConditionIn.put("stepOrder", stepOrder);
		taxConditionIn.put("fromAmount", fromAmount);
		taxConditionIn.put("toAmount", toAmount);
		taxConditionIn.put("amount", amount);
		taxConditionIn.put("deductionPercentage", deductionPercentage);
		taxConditionIn.put("comments", comments);
		taxConditionIn.put("isActive", "Y");
		taxConditionIn.put("dateAdded", currentDatetime);
	    try {
			GenericValue taxConditionInSetup = delegator
				.makeValue("TaxCondition",
						UtilMisc.toMap(taxConditionIn));
			taxConditionInSetup.store();   

			
	    } catch (Exception e) { }

        request.setAttribute("_EVENT_MESSAGE_", "Tax Condition Successfully Created");
        return "success";
    }

}