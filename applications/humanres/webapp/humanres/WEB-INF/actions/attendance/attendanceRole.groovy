import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.EntityUtil;

if(currentLocationId!="company"&&currentLocationId!=null){

	currentLocation=delegator.findByPrimaryKey("Location", [locationId : currentLocationId]);
	
	if (!UtilValidate.isEmpty(currentLocation) && orgId!="Group") {
		context.locationName=currentLocation.locationName;
		if(currentLocation.locationTypeId=="Building" && currentLocation.partyId==orgId.partyId){
			context.isBuilding=true;
			context.selection="BUILDING";
		}else if(currentLocation.locationTypeId=="Floor" && currentLocation.partyId==orgId.partyId){
			context.isFloor=true;
			context.selection="Floor";
		}else if(currentLocation.locationTypeId=="Gate" && currentLocation.partyId==orgId.partyId){
			context.isGate=true;
			context.selection="Gate";
		}
		else if(currentLocation.locationTypeId=="Device" && currentLocation.partyId==orgId.partyId){
			context.isDevice=true;
			context.selection="Device";

			deviceRegistrations=	delegator.findByAnd("DeviceRegistration", [locationId : currentLocationId]);
			deviceRegistration=	EntityUtil
					.getFirst(deviceRegistrations);
			if (!UtilValidate.isEmpty(deviceRegistration)) {
				context.terminalSN=deviceRegistration.terminalSN;
			}
		}
		else if(currentLocation.locationTypeId=="Employee" && currentLocation.partyId==orgId.partyId){
		
			context.isEmployee=true;
			context.selection="Employee";
			employeeRegistrations=	delegator.findByAnd("EmployeeRegistration", [locationId : currentLocationId]);
			employeeRegistration=	EntityUtil
					.getFirst(employeeRegistrations);
			if (!UtilValidate.isEmpty(employeeRegistration)) {
				context.userId=employeeRegistration.userLoginId;	
			}
		}
		else{
			context.isCompany=true;
			context.selection="Company";
		}
	}
}else{
	context.isCompany=true;
	context.selection="Company";
}
