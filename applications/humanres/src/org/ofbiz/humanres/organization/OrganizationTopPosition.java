package org.ofbiz.humanres.organization;

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

public class OrganizationTopPosition {
	 public static String createOrganizationHead(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();
	        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());
	        
	        

	      String employeeID = "";
	        String orgPartyId = request.getParameter("partyId");
	        if (null != request.getParameter("employeePartyId")) {
	        	employeeID = request.getParameter("employeePartyId");
	        }
	       if (employeeID.equals("")) {
	            request.setAttribute("_ERROR_MESSAGE_", "Employee can not be empty");
	            return "error";
	        }

	        List<GenericValue> usderLoginList = null;
	        try {
	        	usderLoginList = delegator.findByAnd("UserLogin",
	                    UtilMisc.toMap("userLoginId", employeeID));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String partyId = null;
	        if (UtilValidate.isNotEmpty(usderLoginList)) {
	            if (UtilValidate.isNotEmpty(usderLoginList.get(0).get("partyId"))) {
	            	partyId = usderLoginList.get(0).get("partyId").toString();
	            }
	        }
	       GenericValue orgTopPartyId = null;
			try { 
				orgTopPartyId = delegator.findByPrimaryKey("BusinessUnitTopAssign", UtilMisc.toMap("partyId", orgPartyId));
				
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			Map<String, Object> departmentHeadIn = FastMap.newInstance();
			departmentHeadIn.put("partyId", orgPartyId);
			departmentHeadIn.put("employeePartyId", partyId);
			departmentHeadIn.put("fromDate", currentDatetime);
    	    try {
    			GenericValue departmentHeadInSetup = delegator
    				.makeValue("BusinessUnitTopAssign",
    						UtilMisc.toMap(departmentHeadIn));
    			if (orgTopPartyId==null) {
    				departmentHeadInSetup.create();
    				}
    			
    			else {
    	    			departmentHeadInSetup.store();
				}
    			
    	    } catch (Exception e) { }
    	    
		    request.setAttribute("_EVENT_MESSAGE_", "Department Head Assign Successfully");
	        return "success";
	    }
	 
}