package org.ofbiz.report.salarySheet;

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
import java.math.BigDecimal;

public class SalarySheetEvent {



    public static String generateReport(HttpServletRequest request, HttpServletResponse response) {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	Collection parsed = UtilHttp.parseMultiFormData(UtilHttp.getParameterMap(request));
        Map info = (Map) parsed.toArray()[0];

        String companyId = info.get("companyId").toString();
        String monthNumber = info.get("month").toString();
        String year = info.get("fiscalYear").toString();
        Map<String, Object> ctx = UtilHttp.getParameterMap(request);
        JRDataSource jrDataSource = createReportDataSource(delegator, ctx,companyId,monthNumber,year);
        HashMap jrParameters = new HashMap();
        
        request.setAttribute("jrDataSource", jrDataSource);
        request.setAttribute("jrParameters", jrParameters);
        return "success";
    }

    public static JRDataSource createReportDataSource(Delegator delegator, Map<String, Object> ctx , String companyId,String monthNumber, String year) {
        JRMapCollectionDataSource dataSource;
        Collection reportRows = initializeMapCollection(delegator, ctx,companyId,monthNumber,year);
        dataSource = new JRMapCollectionDataSource(reportRows);
        return dataSource;
    }


    public static Collection initializeMapCollection(Delegator delegator, Map<String, Object> ctx, String companyId,String monthNumber, String year) {
		   
	    List partyIds = FastList.newInstance();
	        
	    List<GenericValue> eligibleList = null;
	    GenericValue employee = null;
	    try {
	    	eligibleList = delegator.findByAnd("EmployeeSalaryFinal", UtilMisc.toMap("monthNumber", monthNumber, "fiscalYear", "2015"));
	      } catch (GenericEntityException e) {
	        e.printStackTrace();
	    }
	    
	    
	    ArrayList reportRows = new ArrayList();
	    HashMap rowMap = new HashMap();
	    String monthNameYear = null;
	    assert eligibleList != null;

	    for (GenericValue employeeValue : eligibleList) {
	    	BigDecimal basic = BigDecimal.ZERO;
	        BigDecimal HouseRent = BigDecimal.ZERO;
	        BigDecimal medical = BigDecimal.ZERO;
	        BigDecimal mobile = BigDecimal.ZERO;
	        BigDecimal OtherAllow = BigDecimal.ZERO;
	        BigDecimal totalGross = BigDecimal.ZERO;
	        BigDecimal otherDeduction = BigDecimal.ZERO;
	        BigDecimal grossben = BigDecimal.ZERO;
	        BigDecimal grossDed = BigDecimal.ZERO;
	        BigDecimal CompanyCon = BigDecimal.ZERO;
	        BigDecimal employCon = BigDecimal.ZERO;
	        BigDecimal totalpay = BigDecimal.ZERO;
	        BigDecimal emplTax = BigDecimal.ZERO;
	    	rowMap = new HashMap();
	        String employeePartyId = null;
	        if (UtilValidate.isNotEmpty(employeeValue)) {
	            employeePartyId = employeeValue.getString("partyId");
	        }
	        List<GenericValue> singleEmployeeBenefitList = SalarySheetReportHelper.getAnEmployeeBenefitFromDatabase(delegator, employeePartyId, monthNumber, year);
	        String employeeName = SalarySheetReportHelper.getAnEmployeeName(delegator, employeePartyId);
	        String employeeId = SalarySheetReportHelper.getAnEmployeeId(delegator, employeePartyId);
	        rowMap.put("empId", employeeId);
	        rowMap.put("name", employeeName);
	        for (GenericValue employeeBenefit : singleEmployeeBenefitList) {
	        	String payrollItemTypeId = employeeBenefit.getString("payrollItemTypeId");
	        	String payrollType = SalarySheetReportHelper.getpayrollType(delegator,payrollItemTypeId);
	            if (payrollType.equals("basic")) {
	            	grossben = grossben.add((BigDecimal) employeeBenefit.get("amount"));
	                basic = (BigDecimal) employeeBenefit.get("amount");
	            }
	            if (payrollType.equals("houseRent")) {
	            	grossben = grossben.add((BigDecimal) employeeBenefit.get("amount"));
	                HouseRent = (BigDecimal) employeeBenefit.get("amount");
	            }
	            if (payrollType.equals("medical")) {
	            	grossben = grossben.add((BigDecimal) employeeBenefit.get("amount"));
	                medical = (BigDecimal) employeeBenefit.get("amount");
	            }
	            if (payrollType.equals("mobile")) {
	            	grossben = grossben.add((BigDecimal) employeeBenefit.get("amount"));
	                mobile = (BigDecimal) employeeBenefit.get("amount");
	            }
	            if (payrollType.equals("otherAllowance")) {
	            	grossben = grossben.add((BigDecimal) employeeBenefit.get("amount"));
	            	OtherAllow = (BigDecimal) employeeBenefit.get("amount");
	            }
	            if (payrollType.equals("CompanyCon")) {	            	
	            	CompanyCon = (BigDecimal) employeeBenefit.get("amount");
	            }	            
	        }
	        rowMap.put("basic", basic);
	        rowMap.put("houseRent", HouseRent);
	        rowMap.put("medical", medical);
	        rowMap.put("mobile", mobile);
	        rowMap.put("otherAllow", OtherAllow);
	        rowMap.put("gross", grossben);
	        rowMap.put("CompanyCon", CompanyCon);
	        List<GenericValue> singleEmployeeDeductionList = SalarySheetReportHelper.getAnEmployeeDeductionsFromDatabase(delegator, employeePartyId, monthNumber, year);
	        for (GenericValue employeeDeduction : singleEmployeeDeductionList) {
	        	String payrollItemTypeId = employeeDeduction.getString("payrollItemTypeId");
	        	String payrollType = SalarySheetReportHelper.getpayrollType(delegator,payrollItemTypeId);
	            if (payrollType.equals("otherded")) {
	            	grossDed = grossDed.add((BigDecimal) employeeDeduction.get("amount"));
	            	otherDeduction = (BigDecimal) employeeDeduction.get("amount");
	            } 
	            if (payrollType.equals("employCon")) {
	            	grossDed = grossDed.add((BigDecimal) employeeDeduction.get("amount"));
	            	employCon = (BigDecimal) employeeDeduction.get("amount");
	            } 
	            if (payrollType.equals("emplTax")) {
	            	//grossDed = grossDed.add((BigDecimal) employeeDeduction.get("amount"));
	            	emplTax = (BigDecimal) employeeDeduction.get("amount");
	            } 
	        }
	    }
    	return reportRows;
    }   
    
    
    
}


