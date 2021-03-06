/*
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
 */

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;

if (context.noConditionFind == null) {
    context.noConditionFind = parameters.noConditionFind;
}
if (context.noConditionFind == null) {
    context.noConditionFind = UtilProperties.getPropertyValue("widget", "widget.defaultNoConditionFind");
}
if (context.filterByDate == null) {
    context.filterByDate = parameters.filterByDate;
}
prepareResult = dispatcher.runSync("prepareFind", [entityName : context.entityName,
                                                   orderBy : context.orderBy,
                                                   inputFields : parameters,
                                                   filterByDate : context.filterByDate,
                                                   filterByDateValue : context.filterByDateValue,
                                                   userLogin : context.userLogin] );

exprList = [EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
            EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, context.companyName), 
            EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, null)];
CondList = EntityCondition.makeCondition(exprList, EntityOperator.AND);

exprList2 = [EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null),
			 EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, context.companyName)];
CondList2 = EntityCondition.makeCondition(exprList2, EntityOperator.AND);

statusPartyDisable = EntityCondition.makeCondition([CondList2, CondList], EntityOperator.OR);
entityConditionList = null;
if (prepareResult.entityConditionList != null) {
    ConditionList = [ prepareResult.entityConditionList, statusPartyDisable ];
    entityConditionList = EntityCondition.makeCondition(ConditionList);
} else if (context.noConditionFind == "Y") {
    entityConditionList = statusPartyDisable;
}

executeResult = dispatcher.runSync("executeFind", [entityName : context.entityName,
                                                   orderByList : prepareResult.orderByList,
                                                   entityConditionList : entityConditionList,
                                                   noConditionFind :context.noConditionFind
                                                   ] );
if (executeResult.listIt == null) {
    Debug.log("No list found for query string + [" + prepareResult.queryString + "]");
}
context.listIt = executeResult.listIt;
context.queryString = prepareResult.queryString;
context.queryStringMap = prepareResult.queryStringMap;
