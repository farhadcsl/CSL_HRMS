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

package org.ofbiz.party.party;

import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelEntity;

/**
 * PartyHelper
 */
public class PartyHelper {

    public static final String module = PartyHelper.class.getName();

    private PartyHelper() {}

    public static String getPartyName(GenericValue partyObject) {
        return getPartyName(partyObject, false);
    }
    public static String getPartyConsName(GenericValue partyObject) {
        return getPartyConsName(partyObject, false);
    }

    public static String getPartyName(Delegator delegator, String partyId, boolean lastNameFirst) {
        GenericValue partyObject = null;
        try {
            partyObject = delegator.findByPrimaryKey("PartyNameView", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error finding PartyNameView in getPartyName", module);
        }
        if (partyObject == null) {
            return partyId;
        } else {
            return formatPartyNameObject(partyObject, lastNameFirst);
        }
    }
    
    public static String getPartyConsName(Delegator delegator, String partyId, boolean lastNameFirst) {
       /* GenericValue partyObject = null;
        try {
            partyObject = delegator.findByPrimaryKey("PartyNameView", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error finding PartyNameView in getPartyName", module);
        }
        if (partyObject == null) {
            return partyId;
        } else {
            return formatPartyNameObject(partyObject, lastNameFirst);
        }*/
    	List<GenericValue> getTem = null;
        try {
        	getTem = delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String employmentType = null;
        if (UtilValidate.isNotEmpty(getTem)) {
            if (UtilValidate.isNotEmpty(getTem.get(0).get("employmentType"))) {
            	employmentType = getTem.get(0).get("employmentType").toString();
            }
        }
        
        StringBuilder result = new StringBuilder();
        result.append(employmentType);
        return result.toString();
    }
    public static String getPartyConsName (GenericValue partyObject, boolean lastNameFirst){
    	
    	
    	 if (partyObject == null) {
             return "";
         }
         if ("PartyGroup".equals(partyObject.getEntityName()) || "Person".equals(partyObject.getEntityName())) {
             return formatPartyNameObject(partyObject, lastNameFirst);
         } else {
             String partyId = null;
             try {
                 partyId = partyObject.getString("partyId");
             } catch (IllegalArgumentException e) {
                 Debug.logError(e, "Party object does not contain a party ID", module);
             }

             if (partyId == null) {
                 Debug.logWarning("No party ID found; cannot get name based on entity: " + partyObject.getEntityName(), module);
                 return "";
             } else {
                 return getPartyConsName(partyObject.getDelegator(), partyId, lastNameFirst);
             }
         }
    	 /*List<GenericValue> getTem = null;
	        try {
	        	getTem = delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo", partyId));
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        String employmentType = null;
	        if (UtilValidate.isNotEmpty(getTem)) {
	            if (UtilValidate.isNotEmpty(getTem.get(0).get("employmentType"))) {
	            	employmentType = getTem.get(0).get("employmentType").toString();
	            }
	        }
	        return employmentType;*/
    }

    public static String getPartyName(GenericValue partyObject, boolean lastNameFirst) {
        if (partyObject == null) {
            return "";
        }
        if ("PartyGroup".equals(partyObject.getEntityName()) || "Person".equals(partyObject.getEntityName())) {
            return formatPartyNameObject(partyObject, lastNameFirst);
        } else {
            String partyId = null;
            try {
                partyId = partyObject.getString("partyId");
            } catch (IllegalArgumentException e) {
                Debug.logError(e, "Party object does not contain a party ID", module);
            }

            if (partyId == null) {
                Debug.logWarning("No party ID found; cannot get name based on entity: " + partyObject.getEntityName(), module);
                return "";
            } else {
                return getPartyName(partyObject.getDelegator(), partyId, lastNameFirst);
            }
        }
    }

    public static String formatPartyNameObject(GenericValue partyValue, boolean lastNameFirst) {
        if (partyValue == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        ModelEntity modelEntity = partyValue.getModelEntity();
        if (modelEntity.isField("firstName") && modelEntity.isField("middleName") && modelEntity.isField("lastName")) {
            if (lastNameFirst) {
                if (UtilFormatOut.checkNull(partyValue.getString("firstName")) != null) {
                    result.append(UtilFormatOut.checkNull(partyValue.getString("firstName")));
                    if (partyValue.getString("middleName") != null) {
                        result.append(" ");
                    }
                result.append(UtilFormatOut.checkNull(partyValue.getString("middleName")));
                    if (partyValue.getString("lastName") != null) {
                        result.append(" ");
                    }
                }
                result.append(UtilFormatOut.checkNull(partyValue.getString("lastName")));
            } else {
                result.append(UtilFormatOut.ifNotEmpty(partyValue.getString("firstName"), "", " "));
                result.append(UtilFormatOut.ifNotEmpty(partyValue.getString("middleName"), "", " "));
                result.append(UtilFormatOut.checkNull(partyValue.getString("lastName")));
            }
        }
        if (modelEntity.isField("groupName") && partyValue.get("groupName") != null) {
            result.append(partyValue.getString("groupName"));
        }
        return result.toString();
    }
}
