package org.ofbiz.report.TaxReport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.report.TaxReport.TaxService;

public class TaxEvent {

	public static String generateReport(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String, Object> ctx = UtilHttp.getParameterMap(request);
        JRDataSource jrDataSource = createReportDataSource(delegator, ctx);
        HashMap jrParameters = new HashMap();
        
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
		String employeeId =(String) ctx.get("employeeId");
	       
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
	        
		
		 String fiscalYear = (String) ctx.get("fiscalYear");
		 List<GenericValue> employeeTaxList = null;
		    GenericValue employee = null;
		    
		    try {
		    	employeeTaxList = delegator.findByAnd("EmplFinalSalaryOpt", UtilMisc.toMap("partyId",partyId,"fiscalYear",fiscalYear,"payrollItemTypeId","TAX"));
		      } catch (GenericEntityException e) {
		        e.printStackTrace();
		    }
		    ArrayList reportRows = new ArrayList();
		    HashMap rowMap = new HashMap();
		    assert employeeTaxList != null;

	    	String employeeName = "";
	        String amount = "";
	        
		    for (GenericValue employeeValue : employeeTaxList) {
		        rowMap = new HashMap();
		        String employeePartyId = null;
		        if (UtilValidate.isNotEmpty(employeeValue)) {
		            employeePartyId = employeeValue.getString("partyId");
		        }
		        String monthNumber=employeeValue.getString("monthNumber");
		        String monthName=TaxService.getMonthName(monthNumber);
		         employeeName = TaxService.getAnEmployeeName(delegator, employeePartyId);
		         amount = TaxService.getTaxAmount(delegator, employeePartyId);

			   rowMap.put("employeeId", employeeId);
			   rowMap.put("employeeName", employeeName);
			   rowMap.put("year", fiscalYear);
		       rowMap.put("amount", amount);
		       rowMap.put("monthNumber",monthName);
		       reportRows.add(rowMap);
		    }
       return reportRows;
   }
}
