import org.ofbiz.entity.condition.*

/**
 * Filter partyId of a entity using context.companyId, context.companyId may return BGL 
 */
exprList = [EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, context.parentTypeId)];
condList = EntityCondition.makeCondition(exprList, EntityOperator.AND);

context.andCondition = EntityCondition.makeCondition([condList], EntityOperator.AND);
