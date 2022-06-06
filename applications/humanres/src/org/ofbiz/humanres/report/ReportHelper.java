package org.ofbiz.humanres.report;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;

public class ReportHelper {
	
	public static String generatePayrollItemListReport(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String, Object> ctx = UtilHttp.getParameterMap(request);
        
        JRDataSource jrDataSource = createPayrollItemReportDataSource(delegator, ctx);

        HashMap jrParameters = new HashMap();
        
        //jrParameters.put("employeeName", "1234");
      

        request.setAttribute("jrDataSource", jrDataSource);
        request.setAttribute("jrParameters", jrParameters);
        return "success";
    }
	public static JRDataSource createPayrollItemReportDataSource(Delegator delegator, Map<String, Object> ctx) {
        JRMapCollectionDataSource dataSource;
        Collection reportRows = initializeMapCollection(delegator, ctx);
        dataSource = new JRMapCollectionDataSource(reportRows);
        return dataSource;
    }
	 
	 private static Collection initializeMapCollection(Delegator delegator, Map<String, Object> ctx) {

      
       ArrayList reportRows = new ArrayList();
       List<GenericValue> payrollItemList = null;
	   GenericValue employee = null;
	    
	   try {
		   payrollItemList = delegator.findByAnd("PayrollItem", UtilMisc.toMap("isActive", "Y"));
	      } catch (GenericEntityException e) {
	        e.printStackTrace();
	   }
       
       ArrayList leaveHistoryArrayList = new ArrayList();
	    for (GenericValue employeeLeaveInfo : payrollItemList) {
	    	String payrollItemName = employeeLeaveInfo.get("payrollItemName").toString();
	    	String invoiceItemTypeId = employeeLeaveInfo.get("invoiceItemTypeId").toString();

	        HashMap rowMap = new HashMap();
	    	rowMap.put("payrollItemName", payrollItemName);
	    	rowMap.put("invoiceItemTypeId", invoiceItemTypeId);
		    reportRows.add(rowMap);
	    }

       return reportRows;
   }
}