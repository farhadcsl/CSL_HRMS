package org.ofbiz.humanres.report.profile;
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

public class EmployeeReport {
	
	public static String generateReport(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String, Object> ctx = UtilHttp.getParameterMap(request);
        
        String employeeID = request.getParameter("employeeId");
        
        //Empty value Check       
        if (employeeID.equals("")) {
            request.setAttribute("_ERROR_MESSAGE_", "Employee Id can not be empty");
            return "error";
         }
        //Error Value Check
        List<GenericValue> partyList = null;
        try {
        	partyList = delegator.findByAnd("UserLogin",
                    UtilMisc.toMap("userLoginId", employeeID));
        } catch (Exception e) {
        	
        }
        String partyId1 = null;
       try {
    	   partyId1 = partyList.get(0).get("userLoginId").toString();

           if (!employeeID.equals(partyId1))  {               
               return "error";
            }
       } catch (Exception e) {
		request.setAttribute("_ERROR_MESSAGE_", "You Input Wrong Employee Id, Please Input Correct Employee Id Or Name");
        return "error";}
       
        
        
     
        
        JRDataSource jrDataSource = createReportDataSource(delegator, ctx);

        String reportsDirPath = request.getRealPath("/humanres/reports/profile/");
        String partyId = request.getParameter("partyId");

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
		 
		
		  String employeeID = "";
	        if (null != ctx.get("employeeId")) {
	        	employeeID = (String) ctx.get("employeeId");
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
       
       ArrayList reportRows = new ArrayList();
       HashMap rowMap = new HashMap();
       ArrayList reportRowss = new ArrayList();
       List<GenericValue> educationBackgroundList = null;
	    GenericValue employee = null;
	    
	    try {
	    	educationBackgroundList = delegator.findByAnd("PartyQual", UtilMisc.toMap("partyId", partyId));
	      } catch (GenericEntityException e) {
	        e.printStackTrace();
	    }
	    //assert educationBackgroundList != null;
	   
	  
       String employeeName = SingleEmployeeProfileService.getAnEmployeeName(delegator, partyId);
       String employeeId = SingleEmployeeProfileService.getAnEmployeeId(delegator, partyId);
       String mobileno = SingleEmployeeProfileService.getAnEmployeeMobile(delegator, partyId);
       String designation = SingleEmployeeProfileService.getAnEmployeeDesignation(delegator, partyId);
       String bloodgroup = SingleEmployeeProfileService.getAnBloodGroup(delegator, partyId);
       String department = SingleEmployeeProfileService.getAnDepartment(delegator, partyId);
       String fatherName = SingleEmployeeProfileService.getAnFatherName(delegator, partyId);
       String birthdate = SingleEmployeeProfileService.getAnDateofBirth(delegator, partyId);
       String email = SingleEmployeeProfileService.getAnEmailAdress(delegator, partyId);
       String adress = SingleEmployeeProfileService.getAnAdress(delegator, partyId);
       String picture = SingleEmployeeProfileService.getAnPicture(delegator, partyId);
       if(picture==""){
           picture ="/home/tutul/project/new/contessawork/runtime/uploads/1445748598835/10104.jpeg";    	   
       }
       String emergencyContactname = SingleEmployeeProfileService.getAnEmergencyContactName(delegator, partyId);
       String emergencyContactAddress = SingleEmployeeProfileService.getAnemergencyContactAddress(delegator, partyId);
       String emergencyContactNumber = SingleEmployeeProfileService.getAnemergencyContactNumber(delegator, partyId);
       String emergencyContactRelation = SingleEmployeeProfileService.getAnemergencyContactRelation(delegator, partyId);
       String nomineeName = SingleEmployeeProfileService.getAnomineeName(delegator, partyId);
       String nomineeAdress = SingleEmployeeProfileService.getAnomineeAddress(delegator, partyId);
       String nomineeMobileNo = SingleEmployeeProfileService.getAnomineeInfoNumber(delegator, partyId);
       String nomineeRelation = SingleEmployeeProfileService.getAnomineeRelation(delegator, partyId);
      
       rowMap.put("employeeId", employeeId);
       rowMap.put("employeeName", employeeName);
       rowMap.put("mobileno", mobileno);
       rowMap.put("designation", designation);
       rowMap.put("bloodgroup", bloodgroup);
       rowMap.put("department", department);
       rowMap.put("fatherName", fatherName);
       rowMap.put("birthdate", birthdate);
       rowMap.put("email", email);
       rowMap.put("adress", adress);
       rowMap.put("picture", picture);
       rowMap.put("emergencyContactname", emergencyContactname);
       rowMap.put("emergencyContactAddress", emergencyContactAddress);
       rowMap.put("emergencyContactNumber", emergencyContactNumber);
       rowMap.put("emergencyContactRelation", emergencyContactRelation);
       rowMap.put("nomineeName", nomineeName);
       rowMap.put("nomineeAdress", nomineeAdress);
       rowMap.put("nomineeMobileNo", nomineeMobileNo);
       rowMap.put("nomineeRelation", nomineeRelation);
      
       ArrayList employeeEducationList = new ArrayList();
       ArrayList reportTempData = new ArrayList();
	    for (GenericValue employeeEducationValue : educationBackgroundList) {
	    	Map mapData = FastMap.newInstance();
	    	String examTitle = employeeEducationValue.get("partyQualTypeId").toString();
	    	String Institute =  employeeEducationValue.get("qualificationDesc").toString();
	    	String passingYear = employeeEducationValue.get("passingYear").toString();
	    	mapData.put("examTitle", examTitle);
	    	mapData.put("Institute", Institute);
	    	mapData.put("passingYear", passingYear);
	    	employeeEducationList.add(mapData);  	    	
	    }

	    rowMap.put("employeeEducationList", employeeEducationList);

	       List<GenericValue> previouhistoryList = null;
	       try {
		    	previouhistoryList = delegator.findByAnd("PrevEmployment", UtilMisc.toMap("partyId", partyId));
		      } catch (GenericEntityException e) {
		        e.printStackTrace();
		    }
		    ArrayList employeePreviousHistoryList = new ArrayList();
			    for (GenericValue employeeHistoryValue : previouhistoryList) {
			    	Map mapData = FastMap.newInstance();
			    	String orgName = employeeHistoryValue.get("orgName").toString();
			    	String position =  employeeHistoryValue.get("position").toString();
			    	String fromDate = employeeHistoryValue.get("fromDate").toString();
			    	String thruDate = employeeHistoryValue.get("thruDate").toString();
			    	mapData.put("orgName", orgName);
			    	mapData.put("position", position);
			    	mapData.put("fromDate", fromDate);
			    	mapData.put("thruDate", thruDate);
			    	employeePreviousHistoryList.add(mapData);  	    	
			    }

			    rowMap.put("employeePreviousHistoryList", employeePreviousHistoryList);
			    
			    List<GenericValue> professionalcertificateList = null;
			    
			    try {
			    	professionalcertificateList = delegator.findByAnd("ProfQual", UtilMisc.toMap("partyId", partyId));
			      } catch (GenericEntityException e) {
			        e.printStackTrace();
			    }
			    ArrayList employeeProfessionalCertificateList = new ArrayList();
				    for (GenericValue  professionalcertificateValue : professionalcertificateList) {
				    	Map mapData = FastMap.newInstance();
				    	String courseTitle = professionalcertificateValue.get("courseTitle").toString();
				    	String instituteName =  professionalcertificateValue.get("instituteName").toString();
				    	String courseDuration = professionalcertificateValue.get("courseDuration").toString();
				    	String courseCompletionDate = professionalcertificateValue.get("courseCompletionDate").toString();
				    	 
				    	String title = "";
				        String result = "";
				        if (UtilValidate.isNotEmpty(professionalcertificateValue.get("title"))) {
				            	result = professionalcertificateValue.get("title").toString();
				        }				    	
				    	mapData.put("courseTitle", courseTitle);
				    	mapData.put("instituteName", instituteName);
				    	mapData.put("courseDuration", courseDuration);
				    	mapData.put("courseCompletionDate", courseCompletionDate);
				    	mapData.put("result", result);
				    	employeeProfessionalCertificateList.add(mapData);  	    	
				    }

				    rowMap.put("employeeProfessionalCertificateList", employeeProfessionalCertificateList);
	                reportRows.add(rowMap);
       
       return reportRows;
   }
}