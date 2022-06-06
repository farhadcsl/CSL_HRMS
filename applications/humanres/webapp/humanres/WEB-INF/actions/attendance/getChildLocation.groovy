import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Timestamp;
import java.util.*;

import javolution.util.FastMap;
import javolution.util.FastList;
import javolution.util.FastList.*;
import org.ofbiz.entity.GenericValue;

import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;


import java.util.List;
import java.util.ArrayList;

if(currentLocationId!="company"&&currentLocationId!=null){

	currentLocation=delegator.findByPrimaryKey("Location", [locationId : currentLocationId]);
	
	if(currentLocation.locationTypeId=="Employee"){
		time=currentLocation.lastUpdatedStamp;
		if(compareLogtime(time)){
			context.currentEmployeeStatus=currentLocation.currentInOutStatus;
		}
		
	}

	if(currentLocation.locationTypeId=="Device"){

		locations=getchild(currentLocationId);

		currentLocationInfoMap=getMap(locations);

		context.registeredEmployee=currentLocationInfoMap.registeredEmployee;
		context.inEmployee=currentLocationInfoMap.inEmployee;
		context.outEmployee=currentLocationInfoMap.outEmployee;
		context.atPresents=currentLocationInfoMap.atPresents;
	}
	else{

		List<GenericValue> emloyeelist=new ArrayList<GenericValue>();

		locations1=getchild(currentLocation.locationId);

		iterator1 = locations1.iterator()
		while (iterator1.hasNext()) {

			location1=iterator1.next();

			//for Gate
			if(location1.locationTypeId=="Device"){
				employees1=getchild(location1.locationId);
				for(GenericValue emp1:employees1){
					emloyeelist.add(emp1);
				}
			}
			else{
				employees1=getchild(location1.locationId);
				for(GenericValue emp1:employees1){
					//for Floor
					if(emp1.locationTypeId=="Device"){
						employees2=getchild(emp1.locationId);
						for(GenericValue emp2:employees2){
							emloyeelist.add(emp2);
						}
					}else{

						employees2=getchild(emp1.locationId);
						for(GenericValue emp2:employees2){
							//for Building
							if(emp2.locationTypeId=="Device"){
								employees3=getchild(emp2.locationId);
								for(GenericValue emp3:employees3){
									emloyeelist.add(emp3);
								}
							}
						}
					}
				}
			}
		}
		currentLocationInfoMap=getMap(emloyeelist);

		context.registeredEmployee=currentLocationInfoMap.registeredEmployee;
		context.inEmployee=currentLocationInfoMap.inEmployee;
		context.outEmployee=currentLocationInfoMap.outEmployee;
		context.atPresents=currentLocationInfoMap.atPresents;
	}
}else{

	locations=delegator.findByAnd("Location", [partyId : orgId.partyId]);

	currentLocationInfoMap=getMap(locations);

	context.registeredEmployee=currentLocationInfoMap.registeredEmployee;
	context.inEmployee=currentLocationInfoMap.inEmployee;
	context.outEmployee=currentLocationInfoMap.outEmployee;
	context.atPresents=currentLocationInfoMap.atPresents;
}


def getchild(locationId){
	locations=delegator.findByAnd("Location", [parentLocationId : locationId]);
	return locations;
}

def getMap(locations){

	currentLocationInfoMap= FastMap.newInstance();

	int registeredEmployee=0;
	int inEmployee=0;
	int outEmployee=0;
	int atPresents=0;

	iterator = locations.iterator()
	while (iterator.hasNext()) {

		location=iterator.next();


		if(location.statusId=="EMPL_REGISTERED" && location.partyId==orgId.partyId){

			registeredEmployee++;

			

			userid=getUserId(location.locationId);

			emlogs=getEmpLogs(userid);


			for(GenericValue emlog:emlogs){
				
				time=emlog.logtimeStamp;
				
				if(emlog.eventType=="IN"){

					if(compareLogtime(time)){
						inEmployee++;
					}
				}
				else if(emlog.eventType=="OUT"){
					if(compareLogtime(time)){
						outEmployee++;
					}
				}

			}


		}
	}
	
	atPresents=inEmployee-outEmployee;

	currentLocationInfoMap.put("registeredEmployee", registeredEmployee);
	currentLocationInfoMap.put("inEmployee", inEmployee);
	currentLocationInfoMap.put("outEmployee", outEmployee);
	currentLocationInfoMap.put("atPresents", atPresents);

	return currentLocationInfoMap;
}

def compareLogtime(time){

	Date date= new Date()

	Calendar calendarStart = Calendar.getInstance();
	calendarStart.setTime(date);
	calendarStart.set(Calendar.HOUR_OF_DAY, 0);
	calendarStart.set(Calendar.MINUTE, 0);
	calendarStart.set(Calendar.SECOND, 0);
	calendarStart.set(Calendar.MILLISECOND, 0);


	Calendar calendarEnd = Calendar.getInstance();
	calendarEnd.setTime(date);
	calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
	calendarEnd.set(Calendar.MINUTE, 59);
	calendarEnd.set(Calendar.SECOND, 59);
	calendarEnd.set(Calendar.MILLISECOND, 999);


	return (time.before((calendarEnd.getTime())) && time.after((calendarStart.getTime())));
}

def getUserId(locationId){
	employeeRegistrations=delegator.findByAnd("EmployeeRegistration", [locationId :locationId]);
	employeeRegistration=	EntityUtil.getFirst(employeeRegistrations);
	if (!UtilValidate.isEmpty(employeeRegistration)){
		return employeeRegistration.userLoginId;
	}
	return null;
}
def getEmpLogs(userid){


	employeeLogs=delegator.findByAnd("EmployeeLog", [userLoginId :userid]);
	
	return employeeLogs;
}
