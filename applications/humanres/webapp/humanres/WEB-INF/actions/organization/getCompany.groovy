import javolution.util.FastMap
import javolution.util.FastList.*

import org.ofbiz.base.util.*
import org.ofbiz.entity.*
import org.ofbiz.entity.util.EntityUtil
import org.ofbiz.product.catalog.*
import org.ofbiz.product.category.*

loopFlag = true;

while(loopFlag){

	def parent1 = getParent(partyId);

	partyId = parent1.partyIdFrom;
	def str2 = "INTERNAL_ORGANIZATIO";
	if(parent1.roleType == str2 || parent1.roleType=="_NA_" ){
		loopFlag = false;
		context.companyOfChild = parent1;
		break
	}
}

def getParent(partyId){
	//parent
	parentPartyRel = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationship", [partyIdTo : partyId, partyRelationshipTypeId : "GROUP_ROLLUP"]));
	parent = delegator.findByPrimaryKey("PartyGroup", [partyId : parentPartyRel[0].partyIdFrom]);
	parentMap = FastMap.newInstance();
	parentMap.put("partyIdTo", parent.partyId);
	
	if( parentPartyRel[0].partyIdFrom=="Company"){
		parentMap.put("partyIdFrom", parentPartyRel[0].partyIdTo);
	}else{
		parentMap.put("partyIdFrom", parentPartyRel[0].partyIdFrom);
	}

	parentMap.put("groupName", parent.getString("groupName"));
	parentMap.put("roleType", parentPartyRel[0].getString("roleTypeIdFrom"));


	return parentMap;
}
