package org.ofbiz.report.providentFund;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import java.awt.geom.Arc2D.Double;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;

public class ProvidentFundReportHelper{
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

       ArrayList reportRows = new ArrayList();
	  
       List<GenericValue> dataList = null;
	   GenericValue employee = null;
	    
	   try {
		   dataList = delegator.findByAnd("PersonPayrollInfo", UtilMisc.toMap("isActive", "Y"));
	      } catch (GenericEntityException e) {
	        e.printStackTrace();
	   }
       
       ArrayList leaveHistoryArrayList = new ArrayList();
	    for (GenericValue rowData : dataList) {
	        HashMap rowMap = new HashMap();
	    	String employeeId =rowData.get("employeeId").toString();
	    	String partyId=rowData.get("partyId").toString();
	    	String selfInvestment="0";
	    	String companyInvestment="0";
	    	String totalPFAmount="0";
	    	String emplayeeName=getAnEmployeeName(delegator, partyId);
	    	
	    	rowMap.put("employeeId", employeeId);
	    	rowMap.put("employeeName", emplayeeName);
	    	
	    	List<GenericValue> emplPresentSalaryItemList = null;
	  	    
	  	    try {
	  		 emplPresentSalaryItemList = delegator.findByAnd("EmplPresentSalary", UtilMisc.toMap("partyId",partyId,"payrollItemTypeId","EMPL_PF_INV"));
	  	      } catch (GenericEntityException e) {
	  	        e.printStackTrace();
	  	    }
		    for (GenericValue salInfoRowData : emplPresentSalaryItemList) {
		    	selfInvestment=salInfoRowData.get("amount").toString();
		    }
		    
	  	    try {
	  		 emplPresentSalaryItemList = delegator.findByAnd("EmplPresentSalary", UtilMisc.toMap("partyId",partyId,"payrollItemTypeId","COMP_CPF_INV"));
	  	      } catch (GenericEntityException e) {
	  	        e.printStackTrace();
	  	    }
		    for (GenericValue salInfoRowData : emplPresentSalaryItemList) {
		    	companyInvestment=salInfoRowData.get("amount").toString();
		    }
	    	rowMap.put("selfInvestment", selfInvestment);
	    	rowMap.put("companyInvestment", companyInvestment);
	    	
	    	
	    	double totalAmount=0;
	    	List<GenericValue> emplSelfPFPaymentList = null;
	  	    
	  	    try {
	  	    	emplSelfPFPaymentList = delegator.findByAnd("EmplFinalSalaryOpt", UtilMisc.toMap("partyId",partyId,"payrollItemTypeId","EMPL_PF_INV"));
	  	      } catch (GenericEntityException e) {
	  	        e.printStackTrace();
	  	    }
		    for (GenericValue salInfoRowData : emplSelfPFPaymentList) {
		    	selfInvestment=salInfoRowData.get("amount").toString();
		    	totalAmount=totalAmount+java.lang.Double.parseDouble(selfInvestment);
		    }
		    List<GenericValue> emplCompPFPaymentList = null;
	  	    
	  	    try {
	  	    	emplCompPFPaymentList = delegator.findByAnd("EmplFinalSalaryOpt", UtilMisc.toMap("partyId",partyId,"payrollItemTypeId","COMP_CPF_INV"));
	  	      } catch (GenericEntityException e) {
	  	        e.printStackTrace();
	  	    }
		    for (GenericValue salInfoRowData : emplCompPFPaymentList) {
		    	selfInvestment=salInfoRowData.get("amount").toString();
		    	totalAmount=totalAmount+java.lang.Double.parseDouble(selfInvestment);
		    }
	    	rowMap.put("totalAmount",String.valueOf(totalAmount));
	    	reportRows.add(rowMap);  	    	
	    }
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
     
}