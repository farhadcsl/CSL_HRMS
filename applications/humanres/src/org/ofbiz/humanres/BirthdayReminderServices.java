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
import org.ofbiz.service.ServiceUtil;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.ofbiz.humanres.report.profile.SingleEmployeeProfileService;

public class BirthdayReminderServices {
    public static final String module = BirthdayReminderServices.class.getName();
    public static final String resourceError = "ProductErrorUiLabels";
    public static Map<String, Object> findBirthdayPerson(DispatchContext dctx, Map<String, ? extends Object> context)
    {    	  
    	 Map<String, Object> result = ServiceUtil.returnSuccess();
 	    Delegator delegator = dctx.getDelegator();
    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
  	   	//get current date time with Date()
  	   	Date date = new Date();
  	   	String today= dateFormat.format(date);
  	   	today=today.substring(5);
	    List<EntityExpr> exprs = FastList.newInstance();
	    exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "EMPL_POS_ACTIVE"));        
		List<GenericValue> activeEmployeeList = null;
		try {
			activeEmployeeList = delegator.findList("PartyAndPerson", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}

		ArrayList birthdayEmployeeList = new ArrayList();
		for (GenericValue employee : activeEmployeeList){
			try{
				String employeeBirthday=employee.get("birthDate").toString();
				employeeBirthday=employeeBirthday.substring(5);
				if(employeeBirthday.equals(today))
				{
			    	String firstName = "";
			    	String middleName = "";
			    	String lastName = "";
			    	String partyId = employee.get("partyId").toString();   ;
			    	String birthDate = "";
			    	String profileImageUrl="";	
			    	
			    	 if (null != employee.get("firstName")) {
					    	firstName = employee.get("firstName").toString();       	
			         }
			    	try{

				    	 if (null != employee.get("firstName")) {
						    	firstName = employee.get("firstName").toString();       	
				         }
				    	 if (null != employee.get("middleName")) {
				    		 middleName = employee.get("middleName").toString();       	
				         }
				    	 if (null != employee.get("lastName")) {
				    		 lastName = employee.get("lastName").toString();       	
				         }
				    	 if (null != employee.get("firstName")) {
						    	firstName = employee.get("firstName").toString();       	
				         }
				    	 if (null != employee.get("birthDate")) {
				    		 birthDate = employee.get("birthDate").toString();       	
				         }
				    	profileImageUrl=getAnPicture(delegator, partyId);
			    	}
			    	catch(Exception  e){}

				    HashMap rowMap = new HashMap();
					rowMap.put("firstName", firstName);
					rowMap.put("middleName", middleName);
					rowMap.put("lastName", lastName);
					rowMap.put("firstName", firstName);
					rowMap.put("partyId", partyId);
					rowMap.put("birthDate", birthDate);
					rowMap.put("profileImageUrl", profileImageUrl);
					birthdayEmployeeList.add(rowMap);
				}
			}
			catch(Exception e){}
		}
	    result.put("birthdayEmployeeList", birthdayEmployeeList);
  	   	return result;
    	
    }

    public static String getAnPicture(Delegator delegator, String partyId) {
        List<GenericValue> partyPicture = null;
        try {
        	partyPicture = delegator.findByAnd("PartyContentDetail", UtilMisc.toMap("partyId", partyId,"partyContentTypeId","INTERNAL","mimeTypeId","image/jpeg","contentTypeId","DOCUMENT"));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String pictureName = "";
        String dataResourceId="";
        String pictureUrl="";

    	int contentCount=partyPicture.size();
        if (UtilValidate.isNotEmpty(partyPicture)) {
            if (UtilValidate.isNotEmpty(partyPicture.get(contentCount-1).get("contentName"))) {
            	pictureName = partyPicture.get(contentCount-1).get("contentName").toString();
            	dataResourceId = partyPicture.get(contentCount-1).get("dataResourceId").toString();
            }
        }
        pictureUrl="https://localhost/humanres/control/img/"+pictureName+"?imgId="+dataResourceId;
        /*GenericValue photoResourceInfo = null;
  		try { 
  			photoResourceInfo = delegator.findByPrimaryKey("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId));
  			
  			pictureUrl=photoResourceInfo.get("objectInfo").toString();
  		} catch (GenericEntityException e) {
  			e.printStackTrace();
  		}*/
      
        return pictureUrl;
    }
}
