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
package org.ofbiz.humanres.organization;

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


public class DocumentPermission {
   
    
   public static String setDocumentPermission(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        DispatchContext context = dispatcher.getDispatchContext();

        Timestamp currentDatetime = new Timestamp(System.currentTimeMillis());		
		String partyId = request.getParameter("partyId");
		String contentId = request.getParameter("contentId");
		String partyContentTypeId = request.getParameter("partyContentTypeId");
		String dataResourceId = request.getParameter("dataResourceId");
		String contentName = request.getParameter("contentName");
		String mimeTypeId = request.getParameter("mimeTypeId");
		String permissionType ="";
        if (null != request.getParameter("permissionType")) {
        	permissionType = request.getParameter("permissionType");
        }
        
		GenericValue document = null;
		try { 
			document = delegator.findByPrimaryKey("DocumentPermission", UtilMisc.toMap("contentId", contentId));
			
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		Map<String, Object> documentPermissionIn = FastMap.newInstance();
		documentPermissionIn.put("partyId", partyId);
		documentPermissionIn.put("contentId", contentId);
		documentPermissionIn.put("partyContentTypeId", partyContentTypeId);
		documentPermissionIn.put("dataResourceId", dataResourceId);
		documentPermissionIn.put("contentName", contentName);
		documentPermissionIn.put("mimeTypeId", mimeTypeId);
		documentPermissionIn.put("permissionType", permissionType);
		documentPermissionIn.put("fromDate", currentDatetime);
	    try {
			GenericValue documentPermissionInSetup = delegator
				.makeValue("DocumentPermission",
						UtilMisc.toMap(documentPermissionIn));
			if(document==null){
				documentPermissionInSetup.create(); 
			}
			else{
				documentPermissionInSetup.store();  
			}
			
	    } catch (Exception e) { }

        request.setAttribute("_EVENT_MESSAGE_", "Document Permission Successfully Added");
        return "success";
    }
}
