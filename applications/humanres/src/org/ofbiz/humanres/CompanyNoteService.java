/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.humanres;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONObject;

import org.apache.axiom.om.ds.InputStreamDataSource.Data;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.util.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class CompanyNoteService {
    public static final String module = CompanyNoteService.class.getName();
    public static Map<String, Object> findCompanyNoteView(DispatchContext dctx, Map<String, ? extends Object> context)
    {    	  
    	Map<String, Object> result = ServiceUtil.returnSuccess();
 	    Delegator delegator = dctx.getDelegator();
 	    List<String> sortList=UtilMisc.toList("dateAdded");
	    List<EntityExpr> exprs = FastList.newInstance(); 
	    exprs.add(EntityCondition.makeCondition("activeStatus", EntityOperator.EQUALS, "active"));
		List<GenericValue> noteList = null;
		try {
			noteList = delegator.findList("CompanyNote", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, sortList, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}

		ArrayList companyNoteList = new ArrayList();
		for (GenericValue orgNotice : noteList){
			try{
				String companyNoteId = orgNotice.get("companyNoteId").toString();
		    	String title = "";
		    	String description = "";
		    	String dateAdded = "";

		    	 if (null != orgNotice.get("title")) {
		    		 title = orgNotice.get("title").toString();       	
		         }
		    	 
		    	 if (null != orgNotice.get("description")) {
		    		 description = orgNotice.get("description").toString();       	
		         }
		    	 
		    	 if (null != orgNotice.get("dateAdded")) {
		    		 dateAdded = orgNotice.get("dateAdded").toString();
		    		 dateAdded=dateAdded.substring(0,10);
		         }
	
			    HashMap rowMap = new HashMap();
				rowMap.put("companyNoteId", companyNoteId);
				rowMap.put("title", title);
				rowMap.put("description", description);
				rowMap.put("dateAdded", dateAdded);
				companyNoteList.add(rowMap);
			}
			catch(Exception e){}
		}
	    result.put("companyNoteList", companyNoteList);
  	   	return result;
    	
    }

    
   public static String statusChangeCompanyNote(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();
        

		String companyNoteId = request.getParameter("companyNoteId");
		String title = request.getParameter("title");
		String description = request.getParameter("description");
		/*String dateAdded = request.getParameter("dateAdded");*/
		String activeStatus = request.getParameter("activeStatus");
		

        if (activeStatus.equals("active")) {
        	activeStatus="inActive";
        }
        else{
        	activeStatus="active";
        }
		String dateAddedStr = request.getParameter("dateAdded");
        Timestamp dateAdded = Timestamp.valueOf(dateAddedStr);
        
		Map<String, Object> companyNoteIn = FastMap.newInstance();
		companyNoteIn.put("companyNoteId", companyNoteId);
		companyNoteIn.put("title", "title");
		companyNoteIn.put("description", description);
		companyNoteIn.put("dateAdded", dateAdded);
		companyNoteIn.put("activeStatus", activeStatus);
	    try {
			GenericValue companyNoteInSetup = delegator
				.makeValue("CompanyNote",
						UtilMisc.toMap(companyNoteIn));
			companyNoteInSetup.store();   

			
	    } catch (Exception e) { }

        request.setAttribute("_EVENT_MESSAGE_", "Notice Status Successfully Updated");
        return "success";
    }
}
