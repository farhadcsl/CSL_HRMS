package org.ofbiz.humanres.report.leave;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.cache.UtilCache;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.humanres.report.profile.SingleEmployeeProfileService;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.webapp.control.ContextFilter;
import org.ofbiz.webapp.view.ViewHandlerException;

import javolution.util.FastList;
import javolution.util.FastMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;

public class LeaveReportHelper {
	
	public static String generateReport(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String, Object> ctx = UtilHttp.getParameterMap(request);
        String reportsDirPath = request.getRealPath("/humanres/reports/");
        String employeeID = "";
        if (null != request.getParameter("employeeId")) {
        	employeeID = request.getParameter("employeeId");
        }

        if (employeeID.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Employee can not be empty");
            return "error";
        }
        List<GenericValue> partyList = null;
        try {
        	partyList = delegator.findByAnd("UserLogin",
                    UtilMisc.toMap("userLoginId", employeeID));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String partyId = null;
        if (UtilValidate.isNotEmpty(partyList)) {
            if (UtilValidate.isNotEmpty(partyList.get(0).get("partyId"))) {
            	partyId = partyList.get(0).get("partyId").toString();
            }
        }
        JRDataSource jrDataSource = createReportDataSource(delegator, ctx,partyId);

        HashMap jrParameters = new HashMap();
        jrParameters.put("reportsDirPath", reportsDirPath);
      

        request.setAttribute("jrDataSource", jrDataSource);
        request.setAttribute("jrParameters", jrParameters);
        return "success";
    }
	public static JRDataSource createReportDataSource(Delegator delegator, Map<String, Object> ctx,String partyId) {
        JRMapCollectionDataSource dataSource;
        Collection reportRows = initializeMapCollection(delegator, ctx,partyId);
        dataSource = new JRMapCollectionDataSource(reportRows);
        return dataSource;
    }
	 
	 private static Collection initializeMapCollection(Delegator delegator, Map<String, Object> ctx,String partyId) {

      
       ArrayList reportRows = new ArrayList();
       HashMap rowMap = new HashMap();
	    
       String employeeName = SingleEmployeeProfileService.getAnEmployeeName(delegator, partyId);
       String employeeId = SingleEmployeeProfileService.getAnEmployeeId(delegator, partyId);
       String mobileno = SingleEmployeeProfileService.getAnEmployeeMobile(delegator, partyId);
       String designation = SingleEmployeeProfileService.getAnEmployeeDesignation(delegator, partyId);
       String emplLeaveReasonTypeId =null;
       rowMap.put("employeeId", employeeId);
       rowMap.put("employeeName", employeeName);
       rowMap.put("mobileno", mobileno);
       rowMap.put("designation", designation);
       List<GenericValue> leaveHistoryList = null;
	   GenericValue employee = null;
	    
	   try {
		   leaveHistoryList = delegator.findByAnd("EmplLeave", UtilMisc.toMap("partyId", partyId));
	      } catch (GenericEntityException e) {
	        e.printStackTrace();
	   }
       
       ArrayList leaveHistoryArrayList = new ArrayList();
	    for (GenericValue employeeLeaveInfo : leaveHistoryList) {
	    	HashMap subRowMap = new HashMap();
	    	String numberOfDays ="";
	    	String leaveDuration = employeeLeaveInfo.get("leaveDuration").toString();
	    	/*if(employeeLeaveInfo.get("emplLeaveReasonTypeId")!=null){
	    		emplLeaveReasonTypeId =  employeeLeaveInfo.get("emplLeaveReasonTypeId").toString();
	    	}else{
	    		 emplLeaveReasonTypeId = null;
	    	}*/
	    	if(employeeLeaveInfo.get("leaveTypeId")!=null){
	    		emplLeaveReasonTypeId =  employeeLeaveInfo.get("leaveTypeId").toString();
	    		if(emplLeaveReasonTypeId.equalsIgnoreCase("1000")){emplLeaveReasonTypeId = "Casual Leave";}
	    		if(emplLeaveReasonTypeId.equalsIgnoreCase("1001")){emplLeaveReasonTypeId = "Annual Leave";}
	    		if(emplLeaveReasonTypeId.equalsIgnoreCase("1002")){emplLeaveReasonTypeId = "Sick Leave";}
	    		if(emplLeaveReasonTypeId.equalsIgnoreCase("1003")){emplLeaveReasonTypeId = "Leave without pay";}
	    		if(emplLeaveReasonTypeId.equalsIgnoreCase("1004")){emplLeaveReasonTypeId = "Special Leave";}
	    	}else{
	    		 emplLeaveReasonTypeId = null;
	    	}
	    		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
	    		String fromDate = formatter.format(employeeLeaveInfo.get("fromDate"));
	    		String thruDate = formatter.format(employeeLeaveInfo.get("thruDate"));

	    	
	    	//String fromDate = employeeLeaveInfo.get("fromDate").toString();
	    	//String thruDate = employeeLeaveInfo.get("thruDate").toString();
	        if (UtilValidate.isNotEmpty(employeeLeaveInfo.get("numberOfDays"))) {
	    		numberOfDays = employeeLeaveInfo.get("numberOfDays").toString();
	        }
	    	String leaveStatus = employeeLeaveInfo.get("leaveStatus").toString();
	    	
	    	subRowMap.put("leaveDuration", leaveDuration);
	    	subRowMap.put("emplLeaveReasonTypeId", emplLeaveReasonTypeId);
	    	subRowMap.put("fromDate", fromDate);
	    	subRowMap.put("thruDate", thruDate);
	    	subRowMap.put("numberOfDays", numberOfDays);
	    	subRowMap.put("leaveStatus", leaveStatus);
	    	leaveHistoryArrayList.add(subRowMap);  	    	
	    }

	    rowMap.put("leaveHistoryList", leaveHistoryArrayList);
	    reportRows.add(rowMap);
       return reportRows;
   }
}