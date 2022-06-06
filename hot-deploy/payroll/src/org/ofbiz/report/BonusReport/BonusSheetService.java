package org.ofbiz.report.BonusReport;


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

public class BonusSheetService {
	
	public static final String module = BonusSheetService.class.getName();


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
        	partyEmplId = delegator.findByAnd("PartyBonusStatus", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String userId = "";
       
        if (UtilValidate.isNotEmpty(partyEmplId)) {
            if (UtilValidate.isNotEmpty(partyEmplId.get(0).get("employeeId"))) {
            	userId = partyEmplId.get(0).get("employeeId").toString();
            }
        }
        
      
        return userId;
    }
    public static String getABonusName(Delegator delegator, String bonusId) {
        List<GenericValue> partybonusName = null;
        try {
        	partybonusName = delegator.findByAnd("BonusType", UtilMisc.toMap("bonusId", bonusId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String bonusName = "";
       
        if (UtilValidate.isNotEmpty(partybonusName)) {
            if (UtilValidate.isNotEmpty(partybonusName.get(0).get("bonusName"))) {
            	bonusName = partybonusName.get(0).get("bonusName").toString();
            }
        }
        return bonusName;
    }
   
    public static String getAnBonusAmount(Delegator delegator, String partyId) {
        List<GenericValue> partyamount = null;
        try {
        	partyamount = delegator.findByAnd("PartyBonusStatus", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String amount = "";
       
        if (UtilValidate.isNotEmpty(partyamount)) {
            if (UtilValidate.isNotEmpty(partyamount.get(0).get("amount"))) {
            	amount = partyamount.get(0).get("amount").toString();
            }
        }
        
      
        return amount;
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
	
	public static String getMonthName(String bonusMonth) {
	     String monthName = "";
	     if (bonusMonth.equals("0")) monthName = "January";
	     if (bonusMonth.equals("1")) monthName = "February";
	     if (bonusMonth.equals("2")) monthName = "March";
	     if (bonusMonth.equals("3")) monthName = "April";
	     if (bonusMonth.equals("4")) monthName = "May";
	     if (bonusMonth.equals("5")) monthName = "June";
	     if (bonusMonth.equals("6")) monthName = "July";
	     if (bonusMonth.equals("7")) monthName = "August";
	     if (bonusMonth.equals("8")) monthName = "September";
	     if (bonusMonth.equals("9")) monthName = "October";
	     if (bonusMonth.equals("10")) monthName = "November";
	     if (bonusMonth.equals("11")) monthName = "December";

	     return monthName;
	 }
	
	
	public static String getAnDepartment(Delegator delegator,String partyId){
		String departmentName="";
		String sectionId="";
		String partyUnitId="";
		String partyTypeId="";
		String partyUnitName="";
		String roleTypeIdFrom="";
		 List<GenericValue> party = null;
			try {

				party = delegator.findByAnd("EmplPositionAndFulfillment", UtilMisc.toMap("employeePartyId", partyId));
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			GenericValue partys = EntityUtil.getFirst(party);
			if(UtilValidate.isNotEmpty(partys)){
				partyUnitId=partys.getString("partyId");
				
			}
			List<GenericValue> partyRel = null;
			try {
				partyRel = delegator.findByAnd("PartyRelationshipAndPartyDetail", UtilMisc.toMap("partyId", partyUnitId));
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*GenericValue partyRels = EntityUtil.getFirst(partyRel);
			if(UtilValidate.isNotEmpty(partyRels)){
				partyTypeId=partyRels.getString("partyTypeId");
				
			}
			if (partyTypeId.equalsIgnoreCase("PARTY_SECTION")){
				sectionName=partyRels.getString("toGroupName");
			}else {
				sectionName=partyRels.getString("fromGroupName");
			}*/
			
			
			GenericValue partyRels = EntityUtil.getFirst(partyRel);
			if(UtilValidate.isNotEmpty(partyRels)){
				partyTypeId=partyRels.getString("partyTypeId");
				
			}
			if (partyTypeId.equalsIgnoreCase("PARTY_DEPARTMENT")){
				departmentName=partyRels.getString("toGroupName");
			} else if (partyTypeId.equalsIgnoreCase("PARTY_SECTION")) {
				departmentName=partyRels.getString("fromGroupName");
			}
			else if (partyTypeId.equalsIgnoreCase("PARTY_UNIT")){
				sectionId=partyRels.getString("partyIdFrom");
				List<GenericValue> partyRell = null;
				try {
					partyRell = delegator.findByAnd("PartyRelationshipAndPartyDetail", UtilMisc.toMap("partyId", sectionId));
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				GenericValue partyRells = EntityUtil.getFirst(partyRell);
				if(UtilValidate.isNotEmpty(partyRells)){
					departmentName=partyRells.getString("fromGroupName");
					
				}
			}
		return departmentName;
	}
	  
   
    
	
}