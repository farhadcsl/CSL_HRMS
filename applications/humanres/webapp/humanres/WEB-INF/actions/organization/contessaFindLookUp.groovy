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

exprList = [EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED")
            , EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, null)];
CondList = EntityCondition.makeCondition(exprList, EntityOperator.AND);
CondList1 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null);
statusPartyDisable = EntityCondition.makeCondition([CondList1, CondList], EntityOperator.OR);
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
