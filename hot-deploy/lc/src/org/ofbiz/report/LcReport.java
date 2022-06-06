package org.ofbiz.report;
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

public class LcReport {
	
	public static String generateReport(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String, Object> ctx = UtilHttp.getParameterMap(request);
        JRDataSource jrDataSource = createReportDataSource(delegator, ctx);
        String reportsDirPath = request.getRealPath("/lc/reports/");

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
	
	 private static Collection initializeMapCollection(Delegator delegator, Map<String, Object> ctx) {
  
		   String fromDateStr=(String) ctx.get("fromDate");
		   String thruDateStr=(String) ctx.get("thruDate");
		   DateTime fromDateDT=new DateTime(fromDateStr);
		   DateTime thruDateDT=new DateTime(thruDateStr);
		   Timestamp fromDateTS = new Timestamp(fromDateDT.getMillis());
		   Timestamp thruDateTS = new Timestamp(thruDateDT.getMillis());
		 
		   
		   ArrayList reportRows = new ArrayList();
		   HashMap rowMap = new HashMap();  
		   rowMap.put("fromDate", fromDateStr);
		   rowMap.put("thruDate", thruDateStr);
			List criteria = new LinkedList();
			criteria.add(fromDateTS); 
			criteria.add(thruDateTS);
			
		   List<EntityExpr> exprs = FastList.newInstance(); 
		   /* exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));*/
		    exprs.add(EntityCondition.makeCondition("date", EntityOperator.BETWEEN,criteria));
		    double totalcost=0;
		List<GenericValue> employeeAttendanceList = null;
		
		try {
			employeeAttendanceList = delegator.findList("LcBank", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		
		
		 for (GenericValue employeeAttendanceInfo : employeeAttendanceList) {
			 String lccost = "";
			 if (null != employeeAttendanceInfo.get("cost")) {
				 lccost = employeeAttendanceInfo.get("cost").toString();   
				 totalcost=totalcost+java.lang.Double.parseDouble(lccost);
			 }
			 rowMap.put("totalcost", totalcost);
			
		    }
		   
		  double pitotalamount=0;
		   ArrayList employeeAttendanceArrayList = new ArrayList();
		    for (GenericValue employeeAttendanceInfo : employeeAttendanceList) {
		    	HashMap subRowMap = new HashMap();
		
		String lcNo = "";
		String piId = "";
		String cost = "";
		String lcExpiryDate = "";
		String lastShipmentDate = "";
		String shipmentType = "";
		String issuingBank = "";
		String actualShipmentDate = "";
		String eta = "";
		String shiperName = "";
		String insuranceCoverNote = "";
		String dateAdded = "";
		 if (null != employeeAttendanceInfo.get("lcNo")) {
			 lcNo = employeeAttendanceInfo.get("lcNo").toString();       	
		 }
		 if (null != employeeAttendanceInfo.get("piId")) {
			 piId = employeeAttendanceInfo.get("piId").toString();       	
		 }
		 if (null != employeeAttendanceInfo.get("cost")) {
			 cost = employeeAttendanceInfo.get("cost").toString();       	
		 }
		 if (null != employeeAttendanceInfo.get("lcExpiryDate")) {
			 lcExpiryDate = employeeAttendanceInfo.get("lcExpiryDate").toString();       	
		 }
		 if (null != employeeAttendanceInfo.get("shipmentType")) {
			 shipmentType = employeeAttendanceInfo.get("shipmentType").toString();       	
		 }
		 if (null != employeeAttendanceInfo.get("issuingBank")) {
			 issuingBank = employeeAttendanceInfo.get("issuingBank").toString();
		 }
		 if (null != employeeAttendanceInfo.get("actualShipmentDate")) {
			 actualShipmentDate = employeeAttendanceInfo.get("actualShipmentDate").toString();       	
		 }
		 if (null != employeeAttendanceInfo.get("eta")) {
			 eta = employeeAttendanceInfo.get("eta").toString();       	
		 }
		 if (null != employeeAttendanceInfo.get("shiperName")) {
			 shiperName = employeeAttendanceInfo.get("shiperName").toString();       	
		 }

		 List<GenericValue> partyList = null;
	        try {
	        	partyList = delegator.findByAnd("LcPi",
	                    UtilMisc.toMap("piId", piId));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String piNo = null;
	        if (UtilValidate.isNotEmpty(partyList)) {
	            if (UtilValidate.isNotEmpty(partyList.get(0).get("piNo"))) {
	            	piNo = partyList.get(0).get("piNo").toString();
	            }
	        }
	            String piamount = null;
		        if (UtilValidate.isNotEmpty(partyList)) {
		            if (UtilValidate.isNotEmpty(partyList.get(0).get("amount"))) {
		            	piamount = partyList.get(0).get("amount").toString();
		            }
		        }
		            String goodsDescription = null;
			        if (UtilValidate.isNotEmpty(partyList)) {
			            if (UtilValidate.isNotEmpty(partyList.get(0).get("goodsDescription"))) {
			            	goodsDescription = partyList.get(0).get("goodsDescription").toString();
			            }
	        }
			        String beneficiary = null;
			        if (UtilValidate.isNotEmpty(partyList)) {
			            if (UtilValidate.isNotEmpty(partyList.get(0).get("beneficiary"))) {
			            	beneficiary = partyList.get(0).get("beneficiary").toString();
			            }
	        }
		pitotalamount=pitotalamount+java.lang.Double.parseDouble(piamount);
		subRowMap.put("lcNo", lcNo);
		subRowMap.put("piNo", piNo);
		subRowMap.put("cost", cost);
		subRowMap.put("lcExpiryDate", lcExpiryDate);
		subRowMap.put("shipmentType", shipmentType);
		subRowMap.put("issuingBank", issuingBank);
		subRowMap.put("actualShipmentDate", actualShipmentDate);
		subRowMap.put("eta", eta);
		subRowMap.put("shiperName", shiperName);
		subRowMap.put("goodsDescription", goodsDescription);
		subRowMap.put("piamount", piamount);
		subRowMap.put("beneficiary", beneficiary);
		    	employeeAttendanceArrayList.add(subRowMap);  	    	
		    }
	       rowMap.put("pitotalamount", pitotalamount);
		   rowMap.put("individualemployeeAttendanceList", employeeAttendanceArrayList);
		 
		   
		   reportRows.add(rowMap);
		   
		   
		   return reportRows;
	 }
       
}
