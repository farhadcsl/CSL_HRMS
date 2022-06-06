import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.*;

import javolution.util.FastMap;
import javolution.util.FastList;
import javolution.util.FastList.*;

import org.ofbiz.entity.*;

import freemarker.core.ReturnInstruction.Return;

import java.awt.GraphicsConfiguration.DefaultBufferCapabilities;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.util.List;

breadcrumbList =  FastList.newInstance();

//Selected
selected = delegator.findByPrimaryKey("PartyGroup", [partyId : partyId]);
selectedMap = FastMap.newInstance();
selectedMap.put("partyId", selected.partyId);
selectedMap.put("groupName", selected.getString("groupName"));
selectedMap.put("seqNo", breadcrumbList.size());
breadcrumbList.add(breadcrumbList.size(),selectedMap);
partyRelationship = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationship", [partyIdTo : selected.partyId, partyRelationshipTypeId : "GROUP_ROLLUP"]));

if(partyRelationship[0].partyIdFrom!="Company"){// if Selected is in low level of Company
	getParent(partyRelationship[0].partyIdFrom);
}else{
	//Group if Selected is Company
	group = delegator.findByPrimaryKey("PartyGroup", [partyId : partyRelationship[0].partyIdFrom]);
	groupMap = FastMap.newInstance();
	groupMap.put("partyId", group.partyId);
	groupMap.put("groupName", group.getString("groupName"));
	groupMap.put("seqNo", breadcrumbList.size());
	breadcrumbList.add(breadcrumbList.size(),groupMap);
}

def getParent(partyId){
	//parent
		parentPartyRel = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationship", [partyIdTo : partyId, partyRelationshipTypeId : "GROUP_ROLLUP"]));
		parent = delegator.findByPrimaryKey("PartyGroup", [partyId : parentPartyRel[0].partyIdTo]);
		parentMap = FastMap.newInstance();
		parentMap.put("partyId", parent.partyId);
		parentMap.put("groupName", parent.getString("groupName"));
		parentMap.put("seqNo", breadcrumbList.size());
		breadcrumbList.add(breadcrumbList.size(),parentMap);
		
		if(parentPartyRel[0].partyIdFrom!="Company"){
			getParent(parentPartyRel[0].partyIdFrom);
		}else{
			//Group
			group = delegator.findByPrimaryKey("PartyGroup", [partyId : parentPartyRel[0].partyIdFrom]);
			groupMap = FastMap.newInstance();
			groupMap.put("partyId", group.partyId);
			groupMap.put("groupName", group.getString("groupName"));
			groupMap.put("seqNo", breadcrumbList.size());
			breadcrumbList.add(breadcrumbList.size(),groupMap);			
		}
}
breadcrumbList.reverse(true);
context.breadcrumbList=breadcrumbList;