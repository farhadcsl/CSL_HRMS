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

		List<GenericValue> employeeLogs=getEmpLogs();
		if(employeeLogs.size()>0){
		GenericValue employeeLog = employeeLogs.get(employeeLogs.size()-1);
		time=employeeLog.logtimeStamp;
		
		if(compareLogtime(time)){
			emp=getEmpName(employeeLog.userLoginId);
			context.currentDeviceEmpStatus=employeeLog.eventType;
			context.currentDeviceEmpId=employeeLog.userLoginId;
			context.currentDeviceEmpName=emp.firstName+" "+emp.lastName;
			
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

def getEmpLogs(){
	
		employeeLogs=delegator.findList("EmployeeLog", null, null, null, null, true);
		
		return employeeLogs;
	}

def getEmpName(userLoginId){
	
		empInfos=delegator.findByAnd("Person", [partyId :userLoginId]);
		empInfo=EntityUtil.getFirst(empInfos);
	
	return empInfo;
}
