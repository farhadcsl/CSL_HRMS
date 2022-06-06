package org.ofbiz.humanres.organization;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;

public class IncrementRuleAssignment {


    public static String assingIncrementPerson(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        String salaryStepSeqId = request.getParameter("salaryStepSeqId");
        String emplId = request.getParameter("emplId");
        String emplPositionId = request.getParameter("emplPositionId");
        String payGradeId = request.getParameter("payGradeId");
        //to use partyIdFrom
       // String companyId = request.getParameter("orgPartyId");
        String companyId = request.getParameter("companyId");
        String doRealize = request.getParameter("doRealize");


        if (UtilValidate.isEmpty(emplId)) {
            request.setAttribute("_ERROR_MESSAGE_", "emplId can not be empty");
            return "error";
        }

        String partyId = IncrementRuleAssignment.getPartyIdfromEmplId(delegator, emplId);

        if (UtilValidate.isEmpty(partyId)) {
            request.setAttribute("_ERROR_MESSAGE_", "emplId not found");
            return "error";
        }
        if (UtilValidate.isEmpty(salaryStepSeqId)) {
            request.setAttribute("_ERROR_MESSAGE_", "salaryStepSeqId not found");
            return "error";
        }
        String fromDateStr = request.getParameter("effectiveDate");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(fromDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Timestamp fromDate = new java.sql.Timestamp(parsedDate.getTime());

        Map<String, Object> incrementPersonMap = FastMap.newInstance();

        incrementPersonMap.put("personIncrementId", delegator.getNextSeqId("PersonIncrement"));
        incrementPersonMap.put("partyId", partyId);
        incrementPersonMap.put("salaryStepSeqId", salaryStepSeqId);
        incrementPersonMap.put("fromDate", fromDate);

        if (doRealize.equals("Y")) {
            incrementPersonMap.put("arrearsAdjustFlag", "Y");
            incrementPersonMap.put("isCurrentSalary", "Y");
        } else {
            incrementPersonMap.put("arrearsAdjustFlag", "N");
            incrementPersonMap.put("isCurrentSalary", "N");
        }


        //to do: if already exist now on only create a new one

        GenericValue personIncrement = null;
        try {

            personIncrement = delegator.makeValue("PersonIncrement", UtilMisc.toMap(incrementPersonMap));
            personIncrement.create();
        } catch (GenericEntityException e) {
            request.setAttribute("_ERROR_MESSAGE_", "Can not Assigned Successfully");
            return "error";
        }
        //realization in partyBenefit and payroll preference
        //first get value from the increment benefit and update partyBenefit
        if (doRealize.equals("Y")) {
            String returnValue = IncrementRuleAssignment.updatePartyBenefit(delegator, companyId, partyId, salaryStepSeqId);
            //secondly get value from the increment deduction and update the payroll preferanc
            if ("succes".equals(returnValue)) {
                returnValue = IncrementRuleAssignment.updatePayRollPrefarance(delegator, companyId, partyId, salaryStepSeqId);
            }

            if ("succes".equals(returnValue)) {

                request.setAttribute("emplPositionId", emplPositionId);
                request.setAttribute("payGradeId", payGradeId);
                request.setAttribute("salaryStepSeqId", salaryStepSeqId);
                request.setAttribute("_EVENT_MESSAGE_", "Person Assigned Successfully");
                return "success";
            } else {
                request.setAttribute("_ERROR_MESSAGE_", "can not update benefit and deduction");
                return returnValue;
            }
        } else {
            request.setAttribute("emplPositionId", emplPositionId);
            request.setAttribute("payGradeId", payGradeId);
            request.setAttribute("salaryStepSeqId", salaryStepSeqId);
            request.setAttribute("_EVENT_MESSAGE_", "Person Assigned Successfully");
            return "success";
        }
    }

    /**
     * @param request
     * @param response
     * @return
     */
    public static String incrementBenefitRuleAssignment(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();


        String payrollItemId = request.getParameter("payrollItemId");
        String ruleId = request.getParameter("ruleId");
        String emplPositionId = request.getParameter("emplPositionId");
        String userLoginId = request.getParameter("userLoginId");
        String salaryStepSeqId = request.getParameter("salaryStepSeqId");
        String defaultOrganizationPartyId = request.getParameter("defaultOrganizationPartyId");
        String payGradeId = request.getParameter("payGradeId");


        if (UtilValidate.isEmpty(payrollItemId)) {
            request.setAttribute("_ERROR_MESSAGE_", "Payroll Item can not be empty");
            return "error";
        }

        if (UtilValidate.isEmpty(ruleId)) {
            request.setAttribute("_ERROR_MESSAGE_", "RuleId can not be empty");
            return "error";
        }
        if (UtilValidate.isEmpty(emplPositionId)) {
        }
        if (UtilValidate.isEmpty(salaryStepSeqId)) {
            request.setAttribute("_ERROR_MESSAGE_", "salaryStepSeqId can not be found");
            return "error";
        }
        if (UtilValidate.isEmpty(userLoginId)) {
        }


        //create and update pay grade benefit
        String benefitResult = IncrementRuleAssignment.createAndUpateIncrementBenefit(delegator, dispatcher, salaryStepSeqId, ruleId, defaultOrganizationPartyId, payrollItemId, userLoginId);
        if ("error".equalsIgnoreCase(benefitResult)) {
            request.setAttribute("_ERROR_MESSAGE_", "could not assign succesfully");
            return "error";
        }
        request.setAttribute("emplPositionId", emplPositionId);
        request.setAttribute("payGradeId", payGradeId);
        request.setAttribute("salaryStepSeqId", salaryStepSeqId);
        request.setAttribute("_EVENT_MESSAGE_", "Benefit Assigned Successfully");
        return "success";
    }

    /**
     * @param request
     * @param response
     * @return success/failure
     */

    public static String incrementDeductionRuleAssignment(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();

        String emplPositionId = request.getParameter("emplPositionId");
        String payrollItemId = request.getParameter("payrollItemId");
        String ruleId = request.getParameter("ruleId");
        String salaryStepSeqId = request.getParameter("salaryStepSeqId");
        String defaultOrganizationPartyId = request.getParameter("defaultOrganizationPartyId");
        String payGradeId = request.getParameter("payGradeId");
        //check all the value
        if (UtilValidate.isEmpty(payrollItemId)) {
            request.setAttribute("_ERROR_MESSAGE_", "Payroll Item can not be empty");
            return "error";
        }

        if (UtilValidate.isEmpty(ruleId)) {
            request.setAttribute("_ERROR_MESSAGE_", "RuleId can not be empty");
            return "error";
        }
        if (UtilValidate.isEmpty(emplPositionId)) {
        }
        if (UtilValidate.isEmpty(salaryStepSeqId)) {
            request.setAttribute("_ERROR_MESSAGE_", "salaryStepSeqId can not be found");
            return "error";
        }
        //create and update pay grade deduction
        String deductionResult = IncrementRuleAssignment.createAndUpateIncrementDuduction(delegator, dispatcher, salaryStepSeqId, ruleId, payrollItemId);
        if ("error".equalsIgnoreCase(deductionResult)) {
            request.setAttribute("_ERROR_MESSAGE_", "could not assign succesfully");
            return "error";
        }
        request.setAttribute("emplPositionId", emplPositionId);
        request.setAttribute("payGradeId", payGradeId);
        request.setAttribute("salaryStepSeqId", salaryStepSeqId);
        request.setAttribute("_EVENT_MESSAGE_", "Deduction Assigned Successfully");
        return "success";
    }

    /**
     * if benefit is selected form front end then
     * we use that method to update or create a new
     * party Benefit for the person
     *
     * @param delegator
     * @param dispatcher
     * @param partyId
     * @param ruleId
     * @return succes or error
     */
    public static String createAndUpateIncrementBenefit(Delegator delegator, LocalDispatcher dispatcher, String salaryStepSeqId, String ruleId, String defaultOrganizationPartyId, String payrollItemId, String userLogin) {

        List<GenericValue> incrementBenefits = null;
        try {
            incrementBenefits = delegator.findByAnd("IncrementBenefit", UtilMisc.toMap("salaryStepSeqId", salaryStepSeqId, "benefitTypeId", payrollItemId));
        } catch (GenericEntityException e) {

            return "error";
        }
        GenericValue incrementBenefit = EntityUtil.getFirst(incrementBenefits);
        //update the value
        if (UtilValidate.isNotEmpty(incrementBenefit)) {
            try {
                String cost = IncrementRuleAssignment.calcultateCostForBenefit(delegator, dispatcher, salaryStepSeqId, ruleId);
                incrementBenefit.set("ruleId", ruleId);
                incrementBenefit.set("cost", new BigDecimal(cost));

//                incrementBenefit.store();
                delegator.store(incrementBenefit);
            } catch (GenericEntityException e) {
                return "error";
            }

        } else {
            //if not available then create benefit


            String pay = defaultOrganizationPartyId;
           
             /*
              * todo: benefit and cost need to fixed
			  */
            String benefitTypeId = payrollItemId;
            String cost = IncrementRuleAssignment.calcultateCostForBenefit(delegator, dispatcher, salaryStepSeqId, ruleId);
            //String fromDate = new Date().toString();
            Timestamp fromDate = new Timestamp(System.currentTimeMillis());
            // create the adjustment transaction
            Map<String, Object> incrementBenefitMap = FastMap.newInstance();

            incrementBenefitMap.put("incrementBenefitId", delegator.getNextSeqId("IncrementBenefit"));
            incrementBenefitMap.put("benefitTypeId", benefitTypeId);
            incrementBenefitMap.put("fromDate", fromDate);
            incrementBenefitMap.put("salaryStepSeqId", salaryStepSeqId);
            incrementBenefitMap.put("cost", new BigDecimal(cost));
            incrementBenefitMap.put("ruleId", ruleId);


            try {
                incrementBenefit = delegator.makeValue("IncrementBenefit", UtilMisc.toMap(incrementBenefitMap));
                incrementBenefit.create();
            } catch (GenericEntityException e) {

                return "error";
            }
        }
        return "success";
    }

    /**
     * if duduction is selected form front end then
     * we use that method to update or create a new
     * payroll preference for the person
     *
     * @param delegator
     * @param dispatcher
     * @param partyId
     * @param ruleId
     * @return succes or error
     */
    public static String createAndUpateIncrementDuduction(Delegator delegator, LocalDispatcher dispatcher, String salaryStepSeqId, String ruleId, String payrollItemId) {

        List<GenericValue> incrementDeductions = null;
        String roleTypeId = "EMPLOYEE";
        try {
            incrementDeductions = delegator.findByAnd("IncrementDeduction", UtilMisc.toMap("salaryStepSeqId", salaryStepSeqId, "deductionTypeId", payrollItemId));
        } catch (GenericEntityException e) {

            e.printStackTrace();
        }
        GenericValue incrementDeduction = EntityUtil.getFirst(incrementDeductions);
        //update the value
        if (UtilValidate.isNotEmpty(incrementDeduction)) {
            //update the value
            try {
                String cost = IncrementRuleAssignment.calcultateForIncrementDeduction(delegator, dispatcher, salaryStepSeqId, ruleId);
                incrementDeduction.set("ruleId", ruleId);
                incrementDeduction.set("cost", new BigDecimal(cost));

                delegator.store(incrementDeduction);
            } catch (GenericEntityException e) {
                return "error";
            }

        } else {
            //if not available then create deduction

            
           
             /*
              * todo: benefit and cost need to fixed
			  */
            String deductionTypeId = payrollItemId;
            String cost = IncrementRuleAssignment.calcultateCostForBenefit(delegator, dispatcher, salaryStepSeqId, ruleId);
            //String fromDate = new Date().toString();
            Timestamp fromDate = new Timestamp(System.currentTimeMillis());
            // create the adjustment transaction
            Map<String, Object> incrementDeductionMap = FastMap.newInstance();
            incrementDeductionMap.put("incrementDeductionId", delegator.getNextSeqId("IncrementDeduction"));
            incrementDeductionMap.put("deductionTypeId", deductionTypeId);
            incrementDeductionMap.put("fromDate", fromDate);
            incrementDeductionMap.put("salaryStepSeqId", salaryStepSeqId);

            incrementDeductionMap.put("cost", new BigDecimal(cost));
            incrementDeductionMap.put("ruleId", ruleId);
            //partyBenefitMap.put("userLogin", "admin");

            try {
                incrementDeduction = delegator.makeValue("IncrementDeduction", UtilMisc.toMap(incrementDeductionMap));
                incrementDeduction.create();
            } catch (GenericEntityException e) {

                return "error";
            }
        }
        return "success";
    }

    /**
     * @param delegator
     * @param dispatcher
     * @param partyId
     * @param ruleId
     * @return
     */
    public static String calcultateCostForBenefit(Delegator delegator, LocalDispatcher dispatcher, String salaryStepSeqId, String ruleId) {

        List<GenericValue> payrollItemRules = null;
        try {
            payrollItemRules = delegator.findByAnd("PayrollItemRule", UtilMisc.toMap("ruleId", ruleId));
        } catch (GenericEntityException e) {

            return "ruleId is not set properly";
        }
        GenericValue payrollItemRule = EntityUtil.getFirst(payrollItemRules);

        String ruleOperator = (String) payrollItemRule.get("ruleOperator");
        BigDecimal value = (BigDecimal) payrollItemRule.get("value");
        BigDecimal resultCost = null;
        String parentPayrollItem = (String) payrollItemRule.get("parentPayrollItem");
        //now we have parent payroll item and use this with party id we have to check the cost from party benefit table

        List<GenericValue> incrementBenefits = null;
        //get the amount to calculate
        BigDecimal parentCost = null;
        try {
            incrementBenefits = delegator.findByAnd("IncrementBenefit", UtilMisc.toMap("salaryStepSeqId", salaryStepSeqId, "benefitTypeId", parentPayrollItem));
        } catch (GenericEntityException e) {

            return "party benefit not found";
        }
        GenericValue incrementBenefit = EntityUtil.getFirst(incrementBenefits);
        if (UtilValidate.isEmpty(incrementBenefit)) {

            resultCost = value;

        } else {
            parentCost = (BigDecimal) incrementBenefit.get("cost");

            if ("plus".equalsIgnoreCase(ruleOperator)) {
                resultCost = parentCost.add(value);
            } else if ("min".equalsIgnoreCase(ruleOperator)) {

                resultCost = parentCost.subtract(value);
            } else if ("div".equalsIgnoreCase(ruleOperator)) {
                resultCost = parentCost.divide(value);
            } else if ("multi".equalsIgnoreCase(ruleOperator)) {
                resultCost = parentCost.multiply(value);
            } else if ("per".equalsIgnoreCase(ruleOperator)) {
                resultCost = percentage(parentCost, value);
            } else if ("equ".equalsIgnoreCase(ruleOperator)) {
                resultCost = value;
            } else {
                return "in rule id invalid operator inserted";
            }
        }

        return "" + resultCost;

    }

    /**
     * @param delegator
     * @param dispatcher
     * @param partyId
     * @param ruleId
     * @return
     */
    public static String calcultateForIncrementDeduction(Delegator delegator, LocalDispatcher dispatcher, String salaryStepSeqId, String ruleId) {

        List<GenericValue> payrollItemRules = null;
        try {
            payrollItemRules = delegator.findByAnd("PayrollItemRule", UtilMisc.toMap("ruleId", ruleId));
        } catch (GenericEntityException e) {

            return "ruleId is not set properly";
        }
        GenericValue payrollItemRule = EntityUtil.getFirst(payrollItemRules);

        String ruleOperator = (String) payrollItemRule.get("ruleOperator");
        BigDecimal value = (BigDecimal) payrollItemRule.get("value");
//		 String value = (String) payrollItemRule.get("value");
        String parentPayrollItem = (String) payrollItemRule.get("parentPayrollItem");
        //now we have parent payroll item and use this with party id we have to check the cost from party benefit table

        List<GenericValue> incrementDeductions = null;
        try {
            incrementDeductions = delegator.findByAnd("IncrementDeduction", UtilMisc.toMap("salaryStepSeqId", salaryStepSeqId, "deductionTypeId", parentPayrollItem));
        } catch (GenericEntityException e) {

            return "party deduction not found";
        }
        BigDecimal resultCost = null;
        GenericValue incrementDeduction = EntityUtil.getFirst(incrementDeductions);
        if (UtilValidate.isEmpty(incrementDeduction)) {
            resultCost = value;
        } else {
            //get the amount to calculate
            BigDecimal parentCost = (BigDecimal) incrementDeduction.get("cost");

            if ("plus".equalsIgnoreCase(ruleOperator)) {
                resultCost = parentCost.add(value);
            } else if ("min".equalsIgnoreCase(ruleOperator)) {

                resultCost = parentCost.subtract(value);
            } else if ("div".equalsIgnoreCase(ruleOperator)) {
                resultCost = parentCost.divide(value);
            } else if ("multi".equalsIgnoreCase(ruleOperator)) {
                resultCost = parentCost.multiply(value);
            } else if ("per".equalsIgnoreCase(ruleOperator)) {
                resultCost = percentage(parentCost, value);
            } else if ("equ".equalsIgnoreCase(ruleOperator)) {
                resultCost = value;
            }

        }
        return "" + resultCost;
    }

    public static BigDecimal percentage(BigDecimal base, BigDecimal pct) {
        return base.multiply(pct).divide(new BigDecimal(100));
    }


    public static String getPartyIdfromEmplId(Delegator delegator, String emplId) {


        // check emplId
        if (UtilValidate.isEmpty(emplId)) {
            return "emplId is not found";
        }
        List<GenericValue> userLogins = null;
        try {
            userLogins = delegator.findByAnd("UserLogin", UtilMisc.toMap("userLoginId", emplId));
        } catch (GenericEntityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        GenericValue userlogin = EntityUtil.getFirst(userLogins);

        String partyId = userlogin.getString("partyId");
        if (partyId == null) {
            return "partyId not found";
        }

        return partyId;

    }

    public static String getPartyRoleTypeFromCompany(Delegator delegator, String companyId) {

        boolean flag = true;
        String roleTypeIdFrom = "INTERNAL_ORGANIZATIO";
        //get role type id from PartyRole by companyId
        List<GenericValue> partyRoles = null;
        try {
            partyRoles = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", companyId));
        } catch (GenericEntityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //iterate and check whether its INTERNAL_ORGANIZATIO or not
        for (GenericValue val : partyRoles) {
            String roleTypeId = (String) val.get("roleTypeId");
            // the role is not INTERNAL_ORGANIZATIO
            if (roleTypeIdFrom.equalsIgnoreCase(roleTypeId)) {
                flag = false;
            }
        }
        // the role is not INTERNAL_ORGANIZATIO
        if (flag) {
            GenericValue partyRole = EntityUtil.getFirst(partyRoles);
            roleTypeIdFrom = (String) partyRole.get("roleTypeId");
        }
        return roleTypeIdFrom;
    }

    /**
     * @param delegator
     * @param companyId
     * @param partyId
     * @param salaryStepSeqId
     * @return
     */
    public static String updatePartyBenefit(Delegator delegator, String companyId, String partyId, String salaryStepSeqId) {
        Timestamp fromDate = new Timestamp(System.currentTimeMillis());
        try {
            List<GenericValue> incrementBenefits = delegator.findByAnd("IncrementBenefit", UtilMisc.toMap("salaryStepSeqId", salaryStepSeqId));


            for (GenericValue value : incrementBenefits) {
                String incrementBenefitTypeId = (String) value.get("benefitTypeId");
                BigDecimal incrementCost = (BigDecimal) value.get("cost");
                String incrementRuleId = (String) value.get("ruleId");

                //check that type is already exist or not
                List<GenericValue> partyBenefits = delegator.findByAnd("PartyBenefit", UtilMisc.toMap("partyIdTo", partyId, "benefitTypeId", incrementBenefitTypeId));
                GenericValue partyBenefit = EntityUtil.getFirst(partyBenefits);

                //get the roletype id , default value "INTERNAL_ORGANIZATIO"
               //String roleTypeIdFrom = IncrementRuleAssignment.getPartyRoleTypeFromCompany(delegator, companyId);
                String roleTypeIdFrom = "INTERNAL_ORGANIZATIO";
                if (UtilValidate.isNotEmpty(partyBenefit)) {
                    //update the cost and rule id
                    partyBenefit.set("cost", incrementCost);
                    partyBenefit.set("ruleId", incrementRuleId);
                    partyBenefit.set("fromDate", fromDate);
                    //partyBenefit.set("roleTypeIdFrom", roleTypeIdFrom);
                    //partyBenefit.set("partyIdFrom", companyId);
                    delegator.store(partyBenefit);

                } else {
                    String periodTypeId = "FISCAL_YEAR";

                    String roleTypeIdTo = "EMPLOYEE";


                    Map<String, Object> partyBenefitMap = FastMap.newInstance();
                    partyBenefitMap.put("periodTypeId", periodTypeId);
                    partyBenefitMap.put("roleTypeIdFrom", roleTypeIdFrom);
                    partyBenefitMap.put("roleTypeIdTo", roleTypeIdTo);
                    partyBenefitMap.put("partyIdFrom", companyId);
                    partyBenefitMap.put("fromDate", fromDate);
                    partyBenefitMap.put("partyIdTo", partyId);
                    partyBenefitMap.put("benefitTypeId", incrementBenefitTypeId);
                    partyBenefitMap.put("cost", incrementCost);
                    partyBenefitMap.put("ruleId", incrementRuleId);

                    try {
                        partyBenefit = delegator.makeValue("PartyBenefit", UtilMisc.toMap(partyBenefitMap));
                        partyBenefit.create();
                    } catch (GenericEntityException e) {

                        return "error";
                    }
                }
            }

        } catch (GenericEntityException e) {

            return "error";
        }
        return "succes";
    }

    public static String updatePayRollPrefarance(Delegator delegator, String companyId, String partyId, String salaryStepSeqId) {

        Timestamp fromDate = new Timestamp(System.currentTimeMillis());
        try {
            List<GenericValue> incrementDeductions = delegator.findByAnd("IncrementDeduction", UtilMisc.toMap("salaryStepSeqId", salaryStepSeqId));


            for (GenericValue value : incrementDeductions) {
                String incrementDeductionTypeId = (String) value.get("deductionTypeId");
                BigDecimal incrementCost = (BigDecimal) value.get("cost");
                String incrementRuleId = (String) value.get("ruleId");

                //check that type is already exist or not
                List<GenericValue> payrollPreferences = delegator.findByAnd("PayrollPreference", UtilMisc.toMap("partyId", partyId, "deductionTypeId", incrementDeductionTypeId));
                GenericValue payrollPreference = EntityUtil.getFirst(payrollPreferences);


                if (UtilValidate.isNotEmpty(payrollPreference)) {
                    //update the cost and rule id
                    payrollPreference.set("flatAmount", incrementCost);
                    payrollPreference.set("ruleId", incrementRuleId);
                    payrollPreference.set("fromDate", fromDate);

//                    payrollPreference.set("partyIdFrom", companyId);
                    delegator.store(payrollPreference);

                } else {
                    String periodTypeId = "FISCAL_YEAR";
                    String paymentMethodTypeId = "CASH";
                    String roleTypeId = "EMPLOYEE";

                    Map<String, Object> payrollPrefMap = FastMap.newInstance();
                    payrollPrefMap.put("payrollPreferenceSeqId", delegator.getNextSeqId("PayrollPreference"));
                    payrollPrefMap.put("periodTypeId", periodTypeId);
                    payrollPrefMap.put("roleTypeId", roleTypeId);
                    payrollPrefMap.put("paymentMethodTypeId", paymentMethodTypeId);
                    payrollPrefMap.put("deductionTypeId", incrementDeductionTypeId);
                    payrollPrefMap.put("flatAmount", incrementCost);
                    payrollPrefMap.put("partyId", partyId);
                    payrollPrefMap.put("fromDate", fromDate);
                    payrollPrefMap.put("ruleId", incrementRuleId);

                    try {
                        payrollPreference = delegator.makeValue("PayrollPreference", UtilMisc.toMap(payrollPrefMap));
                        payrollPreference.create();
                    } catch (GenericEntityException e) {

                        return "error";
                    }
                }
            }

        } catch (GenericEntityException e) {

            return "error";
        }
        return "succes";
    }
}
