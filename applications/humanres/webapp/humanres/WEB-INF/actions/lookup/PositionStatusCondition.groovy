import org.ofbiz.entity.condition.*;
 
exprList = [EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"), 
			EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, context.companyName),
			EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS,context.emplPositionId),
            EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, null)];
condList = EntityCondition.makeCondition(exprList, EntityOperator.AND);

exprList2 = [EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null),
			 EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS,context.emplPositionId),
			 EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, context.companyName)];
CondList2 = EntityCondition.makeCondition(exprList2, EntityOperator.AND);
context.andCondition = EntityCondition.makeCondition([condList, CondList2], EntityOperator.OR);
 
