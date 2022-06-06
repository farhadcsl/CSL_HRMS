 import javolution.util.FastMap
import javolution.util.FastList.*

import org.ofbiz.base.util.*
import org.ofbiz.entity.*
import org.ofbiz.entity.util.EntityUtil
import org.ofbiz.product.catalog.*
import org.ofbiz.product.category.*


loopFlag = true;




if(orgPartyId!="Company"){
	
	loopFlag = true;
	
	while(loopFlag){
	
		def parent1 = getParent(orgPartyId);
	
		orgPartyId = parent1.partyId;
		def str2 = "INTERNAL_ORGANIZATIO";
		if(parent1.roleType == str2 || parent1.roleType=="_NA_" ){
			loopFlag = false;
			context.orgId = parent1;
			context.selection="Company";
			break
		}
	}

}
else{
	context.selection="Group";
	context.orgId = "Group";
}

/*def getParent(partyId){
	//parent
	parentPartyRel = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationship", [partyIdTo : partyId, partyRelationshipTypeId : "GROUP_ROLLUP"]));
	parent = delegator.findByPrimaryKey("PartyGroup", [partyId : parentPartyRel[0].partyIdFrom]);
	parentRole = delegator.findByAnd("PartyRole", [partyId : parent.partyId]);
	parentMap = FastMap.newInstance();
	parentMap.put("partyId", parent.partyId);
	parentMap.put("groupName", parent.getString("groupName"));
	parentMap.put("roleType", parentRole [0].getString("roleTypeId"));


	return parentMap;
}*/

/*	currentSelectedCompany = delegator.findByPrimaryKey("PartyGroup", [partyId : orgPartyId]);
 
 currentSelectedCompanyRole = delegator.findByAnd("PartyRole", [partyId : orgPartyId]);

 if(currentSelectedCompanyRole[0].roleTypeId!="INTERNAL_ORGANIZATIO"){
	 loopFlag = true;
	 
		 while(loopFlag){

		 parent1 = getParent(orgPartyId);
		 str2 = "INTERNAL_ORGANIZATIO";
		 if(parent1.roleType == str2 ){
			 loopFlag = false;
			 context.orgId = parent1;
			 context.selection="Company";
		 }
		 orgPartyId=parent1.partyId;
		 
	 }
 }else{
	 context.orgId = currentSelectedCompany;
	 context.selection="Company";
 }*/



def getParent(partyId){
	//parent
	parentPartyRel = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationship", [partyIdTo : partyId, partyRelationshipTypeId : "GROUP_ROLLUP"]));
	parent = delegator.findByPrimaryKey("PartyGroup", [partyId : parentPartyRel[0].partyIdFrom]);
	//current=delegator.findByPrimaryKey("PartyGroup", [partyId : parentPartyRel[0].partyIdTo]);
	parentMap = FastMap.newInstance();
	//parentMap.put("partyIdTo", parent.partyId);
	
	if( parentPartyRel[0].partyIdFrom=="Company"){
		parentMap.put("partyId", parentPartyRel[0].partyIdTo);
	}else{
		parentMap.put("partyId", parentPartyRel[0].partyIdFrom);
	}

	parentMap.put("groupName", parent.getString("groupName"));
	parentMap.put("roleType", parentPartyRel[0].getString("roleTypeIdFrom"));


	return parentMap;
}