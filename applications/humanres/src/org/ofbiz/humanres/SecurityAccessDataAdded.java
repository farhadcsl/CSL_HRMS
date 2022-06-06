package org.ofbiz.humanres;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;


public class SecurityAccessDataAdded {

	public static String setGroupPermission(HttpServletRequest request, HttpServletResponse response) {

	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	        DispatchContext context = dispatcher.getDispatchContext();

	        List<GenericValue> UserLoginSecurityGroupList = null;
		       try {
		    	   UserLoginSecurityGroupList = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap());
			      } catch (GenericEntityException e) {
			        e.printStackTrace();
			    }
			    
				    for (GenericValue UserLoginSecurityGroupValue : UserLoginSecurityGroupList) {
				    	String userLogin = UserLoginSecurityGroupValue.get("userLoginId").toString();
				    	String groupId =  UserLoginSecurityGroupValue.get("groupId").toString();
				    	String dateStr = UserLoginSecurityGroupValue.get("fromDate").toString();
				    	Timestamp fromDate=Timestamp.valueOf(dateStr);
				    	
				    	       if(groupId.equals("HUMANRES_EMPLOYEE")){
								// Enter data into UserLoginSecurityGroup
					 			GenericValue userLoginSecurityGroup = delegator
					 					.makeValue("UserLoginSecurityGroup");

					 			userLoginSecurityGroup.set("userLoginId", userLogin);
					 			userLoginSecurityGroup.set("groupId", "HUMANRES_EXEC");
					 			userLoginSecurityGroup.set("fromDate", fromDate);
							    try {
									GenericValue UserLoginSecurityGroupSetup = delegator
										.makeValue("UserLoginSecurityGroup",
												UtilMisc.toMap(userLoginSecurityGroup));
									UserLoginSecurityGroupSetup.create(); 
							    } catch (Exception e) { }
							}
				    	       
				    }
				    
					request.setAttribute("_EVENT_MESSAGE_", "Successfully Added");
				    return "success";	
	}	
	

}