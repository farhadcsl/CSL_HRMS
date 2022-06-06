package org.ofbiz.humanres;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.apache.log4j.Logger;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.service.LocalDispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
public class HrUtils {
public static String getUnit(Delegator delegator,String partyId){
	String unitName="";
	String unitId="";
	
	 List<GenericValue> party = null;
		try {
			party = delegator.findByAnd("EmplPositionAndFulfillment", UtilMisc.toMap("employeePartyId", partyId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GenericValue partys = EntityUtil.getFirst(party);
		if(UtilValidate.isNotEmpty(partys)){
			unitId=partys.getString("partyId");
			
		}
		List<GenericValue> partyRel = null;
		try {
			partyRel = delegator.findByAnd("PartyRelationshipAndPartyDetail", UtilMisc.toMap("partyId", unitId,"partyTypeId", "PARTY_UNIT"));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GenericValue partyRels = EntityUtil.getFirst(partyRel);
		if(UtilValidate.isNotEmpty(partyRels)){
			unitName=partyRels.getString("toGroupName");
			
		}
		
	return unitName;
}
public static String getSection(Delegator delegator,String partyId){
	String sectionName="";
	String sectionId="";
	String partyUnitId="";
	String partyTypeId="";
	String partyUnitName="";
	
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
		GenericValue partyRels = EntityUtil.getFirst(partyRel);
		if(UtilValidate.isNotEmpty(partyRels)){
			partyTypeId=partyRels.getString("partyTypeId");
			
		}
		if (partyTypeId.equalsIgnoreCase("PARTY_SECTION")){
			sectionName=partyRels.getString("toGroupName");
		}else {
			sectionName=partyRels.getString("fromGroupName");
		}
		
	
	return sectionName;
}
public static String getDepartment(Delegator delegator,String partyId){
	String departmentName="";
	String departmentId="";
	String partyUnitId="";
	String partyTypeId="";
	String partyUnitName="";
	String sectionId ="";
	
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
		GenericValue partyRels = EntityUtil.getFirst(partyRel);
		if(UtilValidate.isNotEmpty(partyRels)){
			partyTypeId=partyRels.getString("partyTypeId");
			
		}
		if (partyTypeId.equalsIgnoreCase("PARTY_DEPARTMENT")){
			departmentName=partyRels.getString("toGroupName");
		} else if (partyTypeId.equalsIgnoreCase("PARTY_SECTION")) {
			departmentName=partyRels.getString("fromGroupName");
		}else {
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

public static String getDivision(Delegator delegator,String partyId){
	String divisionName="";
	String departmentId="";
	String partyUnitId="";
	String partyTypeId="";
	String partyUnitName="";
	String sectionId ="";
	
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
	GenericValue partyRels = EntityUtil.getFirst(partyRel);
	
	if (partyTypeId.equalsIgnoreCase("PARTY_DIVISION")){
		divisionName=partyRels.getString("toGroupName");
	} else if (partyTypeId.equalsIgnoreCase("PARTY_DEPARTMENT")) {
		divisionName=partyRels.getString("fromGroupName");
	}else if (partyTypeId.equalsIgnoreCase("PARTY_SECTION")){
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
			divisionName=partyRells.getString("fromGroupName");
			
		}
	}else {
		sectionId=partyRels.getString("partyIdFrom");
		List<GenericValue> partyRelll = null;
		try {
			partyRelll = delegator.findByAnd("PartyRelationshipAndPartyDetail", UtilMisc.toMap("partyId", sectionId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GenericValue partyRellls = EntityUtil.getFirst(partyRelll);
		if(UtilValidate.isNotEmpty(partyRellls)){
			departmentId=partyRellls.getString("partyIdFrom");
			
		}
		List<GenericValue> partyRelV = null;
		try {
			partyRelV = delegator.findByAnd("PartyRelationshipAndPartyDetail", UtilMisc.toMap("partyId", departmentId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GenericValue partyRelVs = EntityUtil.getFirst(partyRelV);
		if(UtilValidate.isNotEmpty(partyRelVs)){
			divisionName=partyRelVs.getString("fromGroupName");
			
		}
	}
	
	return divisionName;
}
public static String getPositionIdByPatyId(Delegator delegator,String partyId){
	
	 try {
			List<GenericValue>	emplPositionFulfillments = delegator.findByAnd("EmplPositionFulfillment",UtilMisc.toMap("partyId", partyId));
			GenericValue emplPositionFulfillment=EntityUtil.getFirst(emplPositionFulfillments);
			if (!UtilValidate.isEmpty(emplPositionFulfillment)){
				
				if(emplPositionFulfillment.get("emplPositionId")!=null){
					List<GenericValue>	emplPositionAndEmplPositionType = delegator.findByAnd("EmplPositionAndEmplPositionType",UtilMisc.toMap("emplPositionId", emplPositionFulfillment.get("emplPositionId")));
					GenericValue emplPositionAndEmplPositionTypes=EntityUtil.getFirst(emplPositionAndEmplPositionType);
					return emplPositionAndEmplPositionTypes.getString("description");
				}
				
			}
		} catch (GenericEntityException e) {
			return null;
		}
		
		return null;
	}


public static Map<String, Object> getPartyIdfromEmplBack(DispatchContext ctx, Map<String, ? extends Object> context) {
	 Delegator delegator = ctx.getDelegator();
       Locale locale = (Locale) context.get("locale");

       String emplId = (String) context.get("emplId");
     //check emplId
        if (UtilValidate.isEmpty(emplId)) {
            return ServiceUtil.returnError("emplId is not found");
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
   if (partyId == null){
   	return ServiceUtil.returnError("partyId not found");
   }
    Map<String, Object> results = ServiceUtil.returnSuccess();
    results.put("partyId", partyId);
	 return results;
	 
}
public static String getPartyIDFromUserLoginID(Delegator delegator,String userLoginId){
	List<GenericValue> userLogins=FastList.newInstance();
	try {
		userLogins = delegator.findByAnd("UserLogin",UtilMisc.toMap("userLoginId", userLoginId));
		GenericValue	userLogin=	EntityUtil.getFirst(userLogins);
		if (!UtilValidate.isEmpty(userLogin)){
			return String.valueOf(userLogin.get("partyId"));
		}
	} catch (GenericEntityException e) {
		return null;
	}
	return null;
}
public static long getStartTime(java.sql.Date date){
	
	
	Calendar calendarStart = Calendar.getInstance();
	calendarStart.setTime(date);
	calendarStart.set(Calendar.HOUR_OF_DAY, 0);
	calendarStart.set(Calendar.MINUTE, 0);
	calendarStart.set(Calendar.SECOND, 0);
	calendarStart.set(Calendar.MILLISECOND, 0);
	
	return calendarStart.getTime().getTime();
}
public static long getEndTime(java.sql.Date date){
	
	Calendar calendarEnd = Calendar.getInstance();
	calendarEnd.setTime(date);
	calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
	calendarEnd.set(Calendar.MINUTE, 59);
	calendarEnd.set(Calendar.SECOND, 59);
	calendarEnd.set(Calendar.MILLISECOND, 999);
	
	return calendarEnd.getTime().getTime();
}
public static String getDepartmentNameList(List<String> departmentList){
	if(departmentList.size()>0){
		return departmentList.get(0);
	}
	else{
		return "";
	}
}
public static List<String> getDepartmentName(Delegator delegator,String partyId,List<String> department){
	
	try {
		GenericValue party = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", partyId));

		if(!UtilValidate.isEmpty(party)){
		
			department.add(party.getString("groupName"));
			
			List<GenericValue>	partyRelationships = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationship",  UtilMisc.toMap("partyIdTo", party.getString("partyId"),"partyRelationshipTypeId","GROUP_ROLLUP")));
			GenericValue partyRelationship=EntityUtil.getFirst(partyRelationships);
			if(!UtilValidate.isEmpty(partyRelationship)&&!partyRelationship.getString("partyIdFrom").equalsIgnoreCase("Company")){
			
				department=getDepartmentName(delegator,partyRelationship.getString("partyIdFrom"), department);
			}
	}
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		return department;
	}
	return department;
}

public static String previousEmploymentParty(HttpServletRequest request, HttpServletResponse response) {
	 Delegator delegator = (Delegator) request.getAttribute("delegator");
       LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
       DispatchContext context = dispatcher.getDispatchContext();
       String employeeId = "";
       String statusId = "";
       String noConditionFind = "";
       if (null != request.getParameter("employeeId")) {
    	   employeeId = request.getParameter("employeeId");
       }
  
       if (null != request.getParameter("statusId")) {
    	   statusId = request.getParameter("statusId");
       }
       if (null != request.getParameter("noConditionFind")) {
    	   noConditionFind = request.getParameter("noConditionFind");
       }
      
       List<GenericValue> partyList = null;
       try {
       	partyList = delegator.findByAnd("UserLogin",
                   UtilMisc.toMap("userLoginId", employeeId));
       } catch (GenericEntityException e) {
           e.printStackTrace();
       }
       String partyId = null;
       if (UtilValidate.isNotEmpty(partyList)) {
           if (UtilValidate.isNotEmpty(partyList.get(0).get("partyId"))) {
           	partyId = partyList.get(0).get("partyId").toString();
           }
       }
      
       request.setAttribute("partyId", partyId);
       request.setAttribute("statusId", statusId);
       request.setAttribute("noConditionFind", noConditionFind);
	 return "success";
}
}