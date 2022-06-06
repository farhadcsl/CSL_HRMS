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

attenbreadcrumbList =  FastList.newInstance();

if(currentLocationId!=null&&currentLocationId!="company"){
	loopFlag = true;

	currentLocation=delegator.findByPrimaryKey("Location", [locationId : currentLocationId]);
	if (!UtilValidate.isEmpty(currentLocation)) {
		if(currentLocation.partyId==orgId.partyId){
			attenbreadcrumbList.add(attenbreadcrumbList.size(),getMap(currentLocation));
		}else{
			context.isCompany=true;
			context.selection="Company";
		}

		while(loopFlag){

			parent1 = getParent(currentLocationId);

			if(parent1==null) {
				loopFlag = false;
			}else{
				if(currentLocation.partyId==orgId.partyId){
					attenbreadcrumbList.add(attenbreadcrumbList.size(),getMap(parent1));
				}else{
					context.isCompany=true;
					context.selection="Company";
				}
				currentLocationId = parent1.locationId;
			}
		}
	}
}

def getParent(locationId){
	//parent
	selectedLocation=	delegator.findByPrimaryKey("Location", [locationId : locationId]);
	parentLocation = delegator.findByPrimaryKey("Location", [locationId : selectedLocation.parentLocationId]);
	return parentLocation;
}

def getMap(location){

	locationMap= FastMap.newInstance();

	locationMap.put("partyId", location.locationId);
	locationMap.put("groupName", location.locationName);
	locationMap.put("seqNo", attenbreadcrumbList.size());
	return locationMap;
}

attenbreadcrumbList.reverse(true);
context.attenbreadcrumbList=attenbreadcrumbList;
