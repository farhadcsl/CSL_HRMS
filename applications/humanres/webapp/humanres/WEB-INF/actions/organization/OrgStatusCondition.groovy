import org.ofbiz.entity.condition.*;
 
exprList = [EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"), 
            EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, null)];
condList = EntityCondition.makeCondition(exprList, EntityOperator.AND);
context.andCondition = EntityCondition.makeCondition([condList, EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)], EntityOperator.OR);
 

 
