package org.ofbiz.humanres.report.payslip;


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
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.webapp.control.ContextFilter;
import org.ofbiz.webapp.view.ViewHandlerException;

import javolution.util.FastList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.humanres.report.profile.SingleEmployeeProfileService;
import org.ofbiz.humanres.report.payslip.PayslipReportService;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class PayslipReportEvent {
	
	public static String generateReport(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String, Object> ctx = UtilHttp.getParameterMap(request);
        JRDataSource jrDataSource = createReportDataSource(delegator, ctx);

      String partyId = request.getParameter("partyId");
      String monthNumber = request.getParameter("monthNumber");
      String year = request.getParameter("year");

        HashMap jrParameters = new HashMap();
        
        //jrParameters.put("employeeName", "1234");
      

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

      
       String partyId = (String) ctx.get("partyId");
       String monthNumber = (String) ctx.get("monthNumber");
       String year = (String) ctx.get("year");
       ArrayList reportRows = new ArrayList();
       HashMap rowMap = new HashMap();
       String employeeName = "";
       String employeeId = "";
       String mobileno = "";
       String bloodgroup = "";
       String department = "";
       String birthdate = "";
       String email = "";
       String address = "";
       String designation = "";
       // benefit import
      
       
      
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
           
          
           employeeName = SingleEmployeeProfileService.getAnEmployeeName(delegator, partyId);
           employeeId = SingleEmployeeProfileService.getAnEmployeeId(delegator, partyId);
           mobileno = SingleEmployeeProfileService.getAnEmployeeMobile(delegator, partyId);
           bloodgroup = SingleEmployeeProfileService.getAnBloodGroup(delegator, partyId);
           department = SingleEmployeeProfileService.getAnDepartment(delegator, partyId);
           birthdate = SingleEmployeeProfileService.getAnDateofBirth(delegator, partyId);
           email = SingleEmployeeProfileService.getAnEmailAdress(delegator, partyId);
           address = SingleEmployeeProfileService.getAnAdress(delegator, partyId);
           designation = SingleEmployeeProfileService.getAnEmployeeDesignation(delegator, partyId);
           
           rowMap.put("employeeId", employeeId);
           rowMap.put("employeeName", employeeName);
           rowMap.put("mobileno", mobileno);
           rowMap.put("designation", designation);
           rowMap.put("bloodgroup", bloodgroup);
           rowMap.put("department", department);
           rowMap.put("birthdate", birthdate);
           rowMap.put("email", email);
           rowMap.put("adress", address);
           rowMap.put("month", PayslipReportService.getMonthName(monthNumber));
           rowMap.put("year", year);
           List<GenericValue> singleEmployeeBenefitList = PayslipReportService.getAnEmployeeBenefitFromDatabase(delegator, partyId, monthNumber);
           for (GenericValue employeeBenefit : singleEmployeeBenefitList) {
           	String payrollItemTypeId = employeeBenefit.getString("payrollItemTypeId");
           	String payrollType = PayslipReportService.getpayrollType(delegator,payrollItemTypeId);
               if (payrollType.equals("basic")) {
               	
                   basic = (BigDecimal) employeeBenefit.get("amount");
                   grossben = grossben.add(basic);
               }
               if (payrollType.equals("houseRent")) {
               	
                   HouseRent = (BigDecimal) employeeBenefit.get("amount");
                   grossben = grossben.add(HouseRent);
               }
               if (payrollType.equals("medical")) {
               	
                   medical = (BigDecimal) employeeBenefit.get("amount");
                   grossben = grossben.add(medical);
               }
               if (payrollType.equals("mobile")) {
               	
                   mobile = (BigDecimal) employeeBenefit.get("amount");
                   grossben = grossben.add(mobile);
               }
               if (payrollType.equals("otherAllowance")) {
               	
               	OtherAllow = (BigDecimal) employeeBenefit.get("amount");
               	grossben = grossben.add(OtherAllow);
               }
               if (payrollType.equals("CompanyCon")) {
               	
               	CompanyCon = (BigDecimal) employeeBenefit.get("amount");
               	grossben = grossben.add(CompanyCon);
               }
               
           }
           rowMap.put("basic", basic);
           rowMap.put("houseRent", HouseRent);
           rowMap.put("medical", medical);
           rowMap.put("mobile", mobile);
           rowMap.put("otherAllow", OtherAllow);
           rowMap.put("gross", grossben);
           rowMap.put("CompanyCon", CompanyCon);
           List<GenericValue> singleEmployeeDeductionList = PayslipReportService.getAnEmployeeDeductionsFromDatabase(delegator, partyId, monthNumber);
           for (GenericValue employeeDeduction : singleEmployeeDeductionList) {
           	String payrollItemTypeId = employeeDeduction.getString("payrollItemTypeId");
           	String payrollType = PayslipReportService.getpayrollType(delegator,payrollItemTypeId);
               if (payrollType.equals("otherded")) {
               	
               	otherDeduction = (BigDecimal) employeeDeduction.get("amount");
               	grossDed = grossDed.add(otherDeduction);
               } 
               if (payrollType.equals("employCon")) {
               	
               	employCon = (BigDecimal) employeeDeduction.get("amount");
               	grossDed = grossDed.add(employCon);
               } 
               if (payrollType.equals("emplTax")) {
               	//grossDed = grossDed.add((BigDecimal) employeeDeduction.get("amount"));
               	emplTax = (BigDecimal) employeeDeduction.get("amount");
               	grossDed = grossDed.add(emplTax);
               } 
       }

           
         
           rowMap.put("emplTax", emplTax);
           rowMap.put("otherDeduct", otherDeduction);
           rowMap.put("deductionTotal", grossDed);
           totalpay = grossben.subtract(grossDed);
          
         
           rowMap.put("netPay", totalpay);
           reportRows.add(rowMap);
       
           
          
       
       	
       	return reportRows;
       }
       
}