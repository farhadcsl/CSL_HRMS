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


breadcrumbList.reverse(true);
context.breadcrumbList=breadcrumbList;

unitbreadcrumbList =  FastList.newInstance();

//SelectedUnit
if(orgUnitId != partyId){
	selectedUnit = delegator.findByPrimaryKey("PartyGroup", [partyId : orgUnitId]);
	if (!UtilValidate.isEmpty(selectedUnit)) {
			selectedUnitMap = FastMap.newInstance();
			selectedUnitMap.put("partyId", selectedUnit.partyId);
			selectedUnitMap.put("groupName", selectedUnit.getString("groupName"));
			selectedUnitMap.put("seqNo", unitbreadcrumbList.size());
			unitbreadcrumbList.add(unitbreadcrumbList.size(),selectedUnitMap);
			partyRelationshipUnit = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationship", [partyIdTo : selectedUnit.partyId, partyRelationshipTypeId : "GROUP_ROLLUP"]));
	
			if(partyRelationshipUnit[0].partyIdFrom!="Company"){// if Selected is in low level of Company
				getParentUnit(partyRelationshipUnit[0].partyIdFrom);
			}
		}
}

def getParentUnit(partyId){
	//parent
		parentPartyRel = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationship", [partyIdTo : partyId, partyRelationshipTypeId : "GROUP_ROLLUP"]));
		parent = delegator.findByPrimaryKey("PartyGroup", [partyId : parentPartyRel[0].partyIdTo]);
	
		
		if(parentPartyRel[0].partyIdFrom!="Company"){
			parentUnitMap = FastMap.newInstance();
			parentUnitMap.put("partyId", parent.partyId);
			parentUnitMap.put("groupName", parent.getString("groupName"));
			parentUnitMap.put("seqNo", unitbreadcrumbList.size());
			unitbreadcrumbList.add(unitbreadcrumbList.size(),parentUnitMap);
			getParentUnit(parentPartyRel[0].partyIdFrom);
		}
}

unitbreadcrumbList.reverse(true);
context.unitbreadcrumbList=unitbreadcrumbList;
