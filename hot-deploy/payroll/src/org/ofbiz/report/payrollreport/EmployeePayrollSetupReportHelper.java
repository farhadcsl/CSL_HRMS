package org.ofbiz.report.payrollreport;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

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

public class EmployeePayrollSetupReportHelper{
	public static String generateReport(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String, Object> ctx = UtilHttp.getParameterMap(request);
        String reportsDirPath = request.getRealPath("/reports/payrollreport/");
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
       GenericValue personPayrollInfo = null;
	   GenericValue employee = null;
	    
	   try {
		   personPayrollInfo = delegator.findByPrimaryKey("PersonPayrollInfo", UtilMisc.toMap("partyId", partyId));
	      } catch (GenericEntityException e) {
	        e.printStackTrace();
	   }
   		String employeeName=getAnEmployeeName(delegator, partyId);
  		String employeeId = personPayrollInfo.get("employeeId").toString();
  		String salaryTemplateId = personPayrollInfo.get("salaryTemplateId").toString();
    	String salaryTemplateName=getSalaryTemplateNameById(delegator, salaryTemplateId);
   		String basicSalaryStepId = personPayrollInfo.get("basicSalaryStepId").toString();
   		String basicSalaryStepName=getBasicSalaryNameById(delegator, basicSalaryStepId);
   		String taxPayerTypeId = personPayrollInfo.get("taxPayerTypeId").toString();
    	String taxPayerTypeName=getTaxCalculationTypeTitleById(delegator, taxPayerTypeId);

	    rowMap.put("employeeId", employeeId);
	    rowMap.put("employeeName", employeeName);
	    rowMap.put("salaryTemplateName", salaryTemplateName);
	    rowMap.put("basicSalaryStepName", basicSalaryStepName);
	    rowMap.put("taxPayerTypeName", taxPayerTypeName);
       
       
       
       
       
       
       
       List<GenericValue> emplPresentSalList = null;
	    
	   try {
		   emplPresentSalList = delegator.findByAnd("EmplPresentSalary", UtilMisc.toMap("partyId", partyId));
	      } catch (GenericEntityException e) {
	        e.printStackTrace();
	   }
	   double totalAmount=0;
       ArrayList emplPresentSalArrayList = new ArrayList();
	    for (GenericValue rowData : emplPresentSalList) {
	    	HashMap subRowMap = new HashMap();
	    	String payrollItemTypeId = rowData.get("payrollItemTypeId").toString();
	    	String payrollItemName=getPayrollItemNameById(delegator, payrollItemTypeId);
	    	String calculationType = rowData.get("calculationType").toString();
	    	String calculationMode = rowData.get("calculationMode").toString();
	    	String amount = rowData.get("amount").toString();
	    	if(calculationType.equals("B")){
	    		calculationType="Benifit";
	    		totalAmount=totalAmount + java.lang.Double.parseDouble(amount);
	    	}
	    	else{
	    		calculationType="Deduction";
	    		totalAmount=totalAmount - java.lang.Double.parseDouble(amount);
	    		amount="-"+amount;
	    	}
	    	subRowMap.put("payrollItemName", payrollItemName);
	    	subRowMap.put("calculationType", calculationType);
	    	subRowMap.put("calculationMode", calculationMode);
	    	subRowMap.put("amount", amount);
	    	emplPresentSalArrayList.add(subRowMap);  	    	
	    }

	    rowMap.put("totalAmount", String.valueOf(totalAmount));
	    rowMap.put("emplPresentSalList", emplPresentSalArrayList);
	    reportRows.add(rowMap);
       return reportRows;
     }
	 public static String getAnEmployeeName(Delegator delegator, String partyId) {
        List<GenericValue> partyName = null;
        try {
        	partyName = delegator.findByAnd("PartyAndPersonAndFulfillment", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String firstName = "";
        String lastName= "";
        if (UtilValidate.isNotEmpty(partyName)) {
            if (UtilValidate.isNotEmpty(partyName.get(0).get("firstName"))) {
            	firstName = partyName.get(0).get("firstName").toString();
            }
        }
        if (UtilValidate.isNotEmpty(partyName)) {
            if (UtilValidate.isNotEmpty(partyName.get(0).get("lastName"))) {
            	lastName = partyName.get(0).get("lastName").toString();
            }
        }
        String Name = firstName +" " +lastName;
        return Name;
    }
	 public static String getSalaryTemplateNameById(Delegator delegator, String salaryTemplateId) {
	        GenericValue salaryTemplateInfo = null;
	        String salaryTemplateName = "";
	        try {
	        	salaryTemplateInfo = delegator.findByPrimaryKey("SalaryTemplate", UtilMisc.toMap("salaryTemplateId", salaryTemplateId));
	            salaryTemplateName = salaryTemplateInfo.get("salaryTemplateName").toString();
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }        
	        return salaryTemplateName;
	    }
	 public static String getBasicSalaryNameById(Delegator delegator, String id) {
	        GenericValue basicSalaryStepInfo = null;
	        String basicSalaryStepName = "";
	        try {
	        	basicSalaryStepInfo = delegator.findByPrimaryKey("BasicSalaryStep", UtilMisc.toMap("basicSalaryStepId", id));
	        	basicSalaryStepName = basicSalaryStepInfo.get("basicSalaryStepName").toString();
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }        
	        return basicSalaryStepName;
	    }
	 public static String getTaxCalculationTypeTitleById(Delegator delegator, String id) {
	        GenericValue taxCalculationTypeInfo = null;
	        String taxCalculationTypeTitle = "";
	        try {
	        	taxCalculationTypeInfo = delegator.findByPrimaryKey("TaxCalculationType", UtilMisc.toMap("taxCalculationTypeId", id));
	        	taxCalculationTypeTitle = taxCalculationTypeInfo.get("taxCalculationTypeTitle").toString();
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }        
	        return taxCalculationTypeTitle;
	    }
	 public static String getPayrollItemNameById(Delegator delegator, String id) {
	        GenericValue payrollItemInfo = null;
	        String payrollItemName = "";
	        try {
	        	payrollItemInfo = delegator.findByPrimaryKey("PayrollItem", UtilMisc.toMap("payrollItemTypeId", id));
	        	payrollItemName = payrollItemInfo.get("payrollItemName").toString();
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }        
	        return payrollItemName;
	    }
  
}
