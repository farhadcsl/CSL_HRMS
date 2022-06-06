package org.ofbiz.report.BonusReport;
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
import org.ofbiz.report.BonusReport.BonusSheetService;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.webapp.control.ContextFilter;
import org.ofbiz.webapp.view.ViewHandlerException;

import javolution.util.FastList;

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

public class BonusSheetEvent {
	
	public static String generateReport(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String, Object> ctx = UtilHttp.getParameterMap(request);
        JRDataSource jrDataSource = createReportDataSource(delegator, ctx);
        HashMap jrParameters = new HashMap();
        String reportsDirPath = request.getRealPath("/reports/bonusReport/");
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
	 
	 private static Collection initializeMapCollection(Delegator delegator, Map<String, Object> ctx) {
		 String bonusId = (String) ctx.get("bonusId");
		 String bonusMonth = (String) ctx.get("bonusMonth");
		 List<GenericValue> employeeList = null;
		    GenericValue employee = null;
		    
		    try {
		    	employeeList = delegator.findByAnd("PartyBonusStatus", UtilMisc.toMap("bonusId",bonusId,"bonusMonth",bonusMonth));
		      } catch (GenericEntityException e) {
		        e.printStackTrace();
		    }
		    ArrayList reportRows = new ArrayList();
		    HashMap rowMap = new HashMap();
		    assert employeeList != null;

	    	String employeeName = "";
	    	String employeeId = "";
	    	String designation = "";
	    	String department = "";
	    	String bonusName = BonusSheetService.getABonusName(delegator, bonusId);
	        String amount = "";
	        String monthName=BonusSheetService.getMonthName(bonusMonth);
	        double totalAmount=0;
		    ArrayList employeeBonusList = new ArrayList();
		    for (GenericValue employeeValue : employeeList) {
		    	HashMap subRowMap = new HashMap();
		        String employeePartyId = null;
		        if (UtilValidate.isNotEmpty(employeeValue)) {
		            employeePartyId = employeeValue.getString("partyId");
			         employeeName = BonusSheetService.getAnEmployeeName(delegator, employeePartyId);
			         employeeId = BonusSheetService.getAnEmployeeId(delegator, employeePartyId);
			         designation = BonusSheetService.getAnEmployeeDesignation(delegator, employeePartyId);
			         department = BonusSheetService.getAnDepartment(delegator, employeePartyId);
			         amount = employeeValue.getString("amount");
			         subRowMap.put("employeeId", employeeId);
			         subRowMap.put("employeeName", employeeName);
			         subRowMap.put("designation", designation);
			         subRowMap.put("department", department);
			         subRowMap.put("amount", amount);
			         employeeBonusList.add(subRowMap);
			         totalAmount=totalAmount+java.lang.Double.parseDouble(amount);

		        }
		    }
		       rowMap.put("totalAmount", totalAmount);
		       rowMap.put("bonusName", bonusName);
		       rowMap.put("bonusMonth",monthName );
		       rowMap.put("employeeBonusList", employeeBonusList);
		       reportRows.add(rowMap);
       return reportRows;
   }
}