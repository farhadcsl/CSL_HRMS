package org.ofbiz.humanres.report.attendance;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import org.apache.poi.ss.formula.functions.Countif;
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
import org.ofbiz.entity.util.EntityUtil;
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

public class EmployeeAttendance {
	
	public static String generateReport(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String, Object> ctx = UtilHttp.getParameterMap(request);
        JRDataSource jrDataSource = createReportDataSource(delegator, ctx);
        String reportsDirPath = request.getRealPath("/humanres/reports/attendance/");
        String departmentId = request.getParameter("positionPartyId");
        HashMap jrParameters = new HashMap();
        

        jrParameters.put("reportsDirPath", reportsDirPath);

        request.setAttribute("jrDataSource", jrDataSource);
        request.setAttribute("jrParameters", jrParameters);
        return "success";
     }
	 public static JRDataSource createReportDataSource(Delegator delegator, Map<String, Object> ctx) throws Exception {
	        JRMapCollectionDataSource dataSource;
	        Collection reportRows = initializeMapCollection(delegator, ctx);
	        dataSource = new JRMapCollectionDataSource(reportRows);
	        return dataSource;
	 }
	
	 public static String getAdepartmentName(Delegator delegator, String departmentId) {
	        List<GenericValue> partyEmailAdress = null;
	        try {
	        	partyEmailAdress = delegator.findByAnd("PartyGroup", UtilMisc.toMap("partyId", departmentId));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String groupName = "";
	       
	        if (UtilValidate.isNotEmpty(partyEmailAdress)) {
	            if (UtilValidate.isNotEmpty(partyEmailAdress.get(0).get("groupName"))) {
	            	groupName = partyEmailAdress.get(0).get("groupName").toString();
	            }
	        }
	        
	      
	        return groupName;
	    }  
	 private static Collection initializeMapCollection(Delegator delegator, Map<String, Object> ctx) throws Exception {
		   Timestamp currentDatetime = new Timestamp(System.currentTimeMillis()); 
		   String fromDateStr=(String) ctx.get("fromDate");
		   String thruDateStr=(String) ctx.get("thruDate");
		   DateTime fromDateDT=new DateTime(fromDateStr);
		   DateTime thruDateDT=new DateTime(thruDateStr);
		   String department=(String) ctx.get("positionPartyId");
		   String numberOfDays=(String) ctx.get("numberOfDays");
		   Timestamp fromDateTS = new Timestamp(fromDateDT.getMillis());
		   Timestamp thruDateTS = new Timestamp(thruDateDT.getMillis());
		   String departmentName = getAdepartmentName(delegator,department);
		   
		   
		   ArrayList reportRows = new ArrayList();
		   HashMap rowMap = new HashMap();     
		   rowMap.put("fromDate", fromDateStr);
		   rowMap.put("thruDate", thruDateStr);
		   rowMap.put("totalWorkingDay", numberOfDays);
		   
		   List<GenericValue> DailyAttendanceList = null;
	     
		   
		   
		   List<GenericValue> employeeAttendanceList = null;
		    GenericValue employee = null;
		    
		    try {
		    	if(!department.equals("")){	
		    		employeeAttendanceList = delegator.findByAnd("PartyAndPersonAndFulfillment", UtilMisc.toMap("positionPartyId",department));
		    	}
		    	else{
		    		departmentName="All Department";
		    		employeeAttendanceList = delegator.findByAnd("PartyAndUserLoginAndPerson", UtilMisc.toMap());
		    	}
		      } catch (GenericEntityException e) {
		        e.printStackTrace();
		    }

	       ArrayList employeeAttendanceArrayList = new ArrayList();
		    for (GenericValue employeeAttendanceInfo : employeeAttendanceList) {
		    	HashMap subRowMap = new HashMap();
		    	String partyId = employeeAttendanceInfo.get("partyId").toString();
		    	String firstName = employeeAttendanceInfo.get("firstName").toString();
		    	String middleName = "";
		    	String lastName = employeeAttendanceInfo.get("lastName").toString();
		    	String userLoginId = employeeAttendanceInfo.get("userLoginId").toString();
		    	
		    	List criteria = new LinkedList();
		    	criteria.add(fromDateTS); 
		    	criteria.add(thruDateTS);
		    	
		    	List<EntityExpr> exprs = FastList.newInstance(); 
			    exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			    exprs.add(EntityCondition.makeCondition("dateOfAttendance", EntityOperator.BETWEEN,criteria));
				
		    	try {
		    		DailyAttendanceList = delegator.findList("DailyAttendance", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			    int lateInCount=0;
			    int absenceCount=0;
			    int normalCount=0;
			    for (GenericValue DailyAttendanceGroupValue : DailyAttendanceList) {
			    	String attendanceResult = DailyAttendanceGroupValue.get("attendanceResult").toString();
			    	if(attendanceResult.equals("Late In")){
			    		lateInCount=lateInCount+1;
				    }
			    	if(attendanceResult.equals("Absence")){
			    	   absenceCount=absenceCount+1;
				    }
			    	if(attendanceResult.equals("Normal")){
			    		normalCount=normalCount+1;
				    }
			    }
		    	subRowMap.put("name", firstName+" "+middleName+" "+lastName+" ("+userLoginId+")");
		    	subRowMap.put("lateIn", lateInCount);
		    	subRowMap.put("absence", absenceCount);
		    	subRowMap.put("normal", normalCount);
		    	employeeAttendanceArrayList.add(subRowMap);  	    	
		    }
		   rowMap.put("employeeAttendanceList", employeeAttendanceArrayList);
		   rowMap.put("department", departmentName);
		   reportRows.add(rowMap);
		   
		   
		   return reportRows;
	 }
       
}
