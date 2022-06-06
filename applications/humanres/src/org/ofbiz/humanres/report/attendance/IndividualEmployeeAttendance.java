package org.ofbiz.humanres.report.attendance;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import org.joda.time.DateTime;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.cache.UtilCache;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;

public class IndividualEmployeeAttendance {
	
	public static String generateReport(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String, Object> ctx = UtilHttp.getParameterMap(request);
        JRDataSource jrDataSource = createReportDataSource(delegator, ctx);
        String reportsDirPath = request.getRealPath("/humanres/reports/attendance/");

        HashMap jrParameters = new HashMap();
        

        jrParameters.put("reportsDirPath", reportsDirPath);

        request.setAttribute("jrDataSource", jrDataSource);
        request.setAttribute("jrParameters", jrParameters);
        return "success";
     }
	 public static JRDataSource createReportDataSource(Delegator delegator, Map<String, Object> ctx) {
	        JRMapCollectionDataSource dataSource;
	        Collection reportRows = initializeMapCollection(delegator, ctx);
	        dataSource = new JRMapCollectionDataSource(reportRows);
	        return dataSource;
	 }
	 public static String getpartyId(Delegator delegator, String userLoginId) {
	        GenericValue partyInfo = null;
	        try {
	        	partyInfo =delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String partyId = "";
	        if (UtilValidate.isNotEmpty(partyInfo)) {
	            if (UtilValidate.isNotEmpty(partyInfo.get("partyId"))) {
	            	partyId = partyInfo.get("partyId").toString();
	            }
	        }
	       
	        return partyId;
	    }
	 private static Collection initializeMapCollection(Delegator delegator, Map<String, Object> ctx) {
  
		   String fromDateStr=(String) ctx.get("fromDate");
		   String thruDateStr=(String) ctx.get("thruDate");
		   String userLoginId = (String) ctx.get("userLoginId");
		   DateTime fromDateDT=new DateTime(fromDateStr);
		   DateTime thruDateDT=new DateTime(thruDateStr);
		   String numberOfDays=(String) ctx.get("numberOfDays");
		   Timestamp fromDateTS = new Timestamp(fromDateDT.getMillis());
		   Timestamp thruDateTS = new Timestamp(thruDateDT.getMillis());
		   
		   
		   ArrayList reportRows = new ArrayList();
		   HashMap rowMap = new HashMap();  
		   String partyId = getpartyId(delegator,userLoginId);
		   String employeeName = SingleEmployeeProfileService.getAnEmployeeName(delegator, partyId);
		   rowMap.put("fromDate", fromDateStr);
		   rowMap.put("thruDate", thruDateStr);
		   rowMap.put("employeeName", employeeName);
		   rowMap.put("totalWorkingDay", numberOfDays);
			List criteria = new LinkedList();
			criteria.add(fromDateTS); 
			criteria.add(thruDateTS);
			
		   List<EntityExpr> exprs = FastList.newInstance(); 
		    exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
		    exprs.add(EntityCondition.makeCondition("dateOfAttendance", EntityOperator.BETWEEN,criteria));
		
		List<GenericValue> employeeAttendanceList = null;
		try {
			employeeAttendanceList = delegator.findList("DailyAttendance", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		   ArrayList employeeAttendanceArrayList = new ArrayList();
		    for (GenericValue employeeAttendanceInfo : employeeAttendanceList) {
		    	HashMap subRowMap = new HashMap();
		    	String firstInTime = "";
		String lastOutTime = "";
		String employeeComments = "";
		String dateOfAttendance = "";
		String supervisorComments = "";
		String attendanceStatus = "";
		String processStatus = "";
		Double activeHour = 0.0;
		String employeeId = "";
		 if (null != employeeAttendanceInfo.get("firstInTime")) {
		 firstInTime = employeeAttendanceInfo.get("firstInTime").toString();       	
		 }
		 if (null != employeeAttendanceInfo.get("lastOutTime")) {
		 lastOutTime = employeeAttendanceInfo.get("lastOutTime").toString();       	
		 }
		 if (null != employeeAttendanceInfo.get("employeeComments")) {
		 employeeComments = employeeAttendanceInfo.get("employeeComments").toString();       	
		 }
		 if (null != employeeAttendanceInfo.get("processStatus")) {
		 processStatus = employeeAttendanceInfo.get("processStatus").toString();       	
		 }
		 if (null != employeeAttendanceInfo.get("supervisorComments")) {
		 supervisorComments = employeeAttendanceInfo.get("supervisorComments").toString();       	
		 }
		 if (null != employeeAttendanceInfo.get("dateOfAttendance")) {
		 dateOfAttendance = employeeAttendanceInfo.get("dateOfAttendance").toString();
			 dateOfAttendance=dateOfAttendance.substring(0,11);
		 }
		 if (null != employeeAttendanceInfo.get("attendanceStatus")) {
		 attendanceStatus = employeeAttendanceInfo.get("attendanceStatus").toString();       	
		 }
		 if (null != employeeAttendanceInfo.get("processStatus")) {
		 processStatus = employeeAttendanceInfo.get("processStatus").toString();       	
		 }
		 if (null != employeeAttendanceInfo.get("employeeId")) {
		 employeeId = employeeAttendanceInfo.get("employeeId").toString();       	
		 }
		 
		 if (firstInTime.isEmpty() == false  && lastOutTime.isEmpty() == false){
			 String [] inTimeSplit = firstInTime.split(":");
			 String [] outTimeSplit = lastOutTime.split(":");
			 if(inTimeSplit !=null && lastOutTime !=null){
			    int inTimeHour = Integer.parseInt(inTimeSplit[0]);
			    int inTimeMins = Integer.parseInt(inTimeSplit[1]);
			    int inTimeHoursInMins = inTimeHour * 60;
			    double inTime = (inTimeHoursInMins + inTimeMins);
			    int outTimeHour = Integer.parseInt(outTimeSplit[0]);
			    int outTimeMins = Integer.parseInt(outTimeSplit[1]);
			    int outTimeHoursInMins = outTimeHour * 60;
			    double outTime = (outTimeHoursInMins + outTimeMins);
			 activeHour =    (outTime - inTime)/60;
			 }
		 }
		 if (firstInTime == null || lastOutTime == null){
			 activeHour =    0.0;
		 }
		
		subRowMap.put("dateOfAttendance", dateOfAttendance);
		subRowMap.put("firstInTime", firstInTime);
		subRowMap.put("lastOutTime", lastOutTime);
		subRowMap.put("activeHour", activeHour);
		subRowMap.put("attendanceStatus", attendanceStatus);
		subRowMap.put("employeeComments", employeeComments);
		subRowMap.put("supervisorComments", supervisorComments);
		subRowMap.put("processStatus", processStatus);
		subRowMap.put("employeeId", employeeId);
		    	employeeAttendanceArrayList.add(subRowMap);  	    	
		    }
		
		   rowMap.put("individualemployeeAttendanceList", employeeAttendanceArrayList);
		   
		   
		   reportRows.add(rowMap);
		   
		   
		   return reportRows;
	 }
       
}
