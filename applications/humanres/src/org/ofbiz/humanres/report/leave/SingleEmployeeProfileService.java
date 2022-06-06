package org.ofbiz.humanres.report.leave;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SingleEmployeeProfileService {
	
	public static final String module = SingleEmployeeProfileService.class.getName();


    public static String getAnEmployeeName(Delegator delegator, String partyId) {
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
    
    public static String getAnEmployeeId(Delegator delegator, String partyId) {
        List<GenericValue> partyEmplId = null;
        try {
        	partyEmplId = delegator.findByAnd("PartyAndPersonAndFulfillment", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String userId = "";
       
        if (UtilValidate.isNotEmpty(partyEmplId)) {
            if (UtilValidate.isNotEmpty(partyEmplId.get(0).get("userLoginId"))) {
            	userId = partyEmplId.get(0).get("userLoginId").toString();
            }
        }
        
      
        return userId;
    }

    public static String getAnEmployeeMobile(Delegator delegator, String partyId) {
        List<GenericValue> partyMobile = null;
        try {
        	partyMobile = delegator.findByAnd("PartyAndTelecomNumber", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String Mobile = "";
      
        if (UtilValidate.isNotEmpty(partyMobile)) {
            if (UtilValidate.isNotEmpty(partyMobile.get(0).get("contactNumber"))) {
            	Mobile = partyMobile.get(0).get("contactNumber").toString();
            }
        }
       return Mobile;
    }
    public static String getAnEmployeeDesignation(Delegator delegator, String partyId) {
        List<GenericValue> partyDesignation = null;
        List<GenericValue> partyDesignation1 = null;
        List<GenericValue> partyDesignation2 = null;
        try {
        	partyDesignation = delegator.findByAnd("EmplPositionFulfillment", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String Designation = "";
      
        if (UtilValidate.isNotEmpty(partyDesignation)) {
            if (UtilValidate.isNotEmpty(partyDesignation.get(0).get("emplPositionId"))) {
            	Designation = partyDesignation.get(0).get("emplPositionId").toString();
            }
        }
        
        try {
        	partyDesignation1 = delegator.findByAnd("EmplPosition", UtilMisc.toMap("emplPositionId", Designation));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String Designation1 = "";
      
        if (UtilValidate.isNotEmpty(partyDesignation1)) {
            if (UtilValidate.isNotEmpty(partyDesignation1.get(0).get("emplPositionTypeId"))) {
            	Designation1 = partyDesignation1.get(0).get("emplPositionTypeId").toString();
            }
        }
        
        try {
        	partyDesignation2 = delegator.findByAnd("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", Designation1));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String Designation2 = "";
      
        if (UtilValidate.isNotEmpty(partyDesignation2)) {
            if (UtilValidate.isNotEmpty(partyDesignation2.get(0).get("description"))) {
            	Designation2 = partyDesignation2.get(0).get("description").toString();
            }
        }
        return Designation2;
    }
	
	
}