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
