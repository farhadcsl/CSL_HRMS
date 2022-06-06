import java.util.List;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

Logger logger=Logger.getLogger("Groovy");
List<Map<String,String>> retdesignationPositionList=FastList.newInstance();
DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
List<GenericValue> employeeAttendancePreferenceList = delegator.findByAnd("EmployeeAttendancePreference", UtilMisc.toMap("orgPartyId",orgPartyId.trim()));
for(GenericValue employeeAttendancePreference:employeeAttendancePreferenceList){
	Map<String,String> employeePreferenceInfo=FastMap.newInstance();
	employeePreferenceInfo.put("employeeId",employeeAttendancePreference.get("employeeId").toString());
	employeePreferenceInfo.put("employeePreferenceId",employeeAttendancePreference.get("employeePreferenceId").toString());
	employeePreferenceInfo.put("empPrefRevision",employeeAttendancePreference.get("empPrefRevision").toString());
	employeePreferenceInfo.put("partyId",employeeAttendancePreference.get("partyId").toString());
	try{
		List<GenericValue> emplPositionFulfillment=delegator.findByAnd("EmplPositionFulfillment",UtilMisc.toMap("partyId", employeeAttendancePreference.get("partyId").toString()));
		GenericValue person=delegator.findOne("Person",UtilMisc.toMap("partyId", employeeAttendancePreference.get("partyId").toString()),false);
		GenericValue emplPosition=delegator.findOne("EmplPosition",UtilMisc.toMap("emplPositionId", emplPositionFulfillment[0].get("emplPositionId").toString()),false);
		GenericValue emplPositionType=delegator.findOne("EmplPositionType",UtilMisc.toMap("emplPositionTypeId", emplPosition.get("emplPositionTypeId").toString()),false);
		String emplName=null;
		if(person.get("firstName")!=null){
			emplName=person.get("firstName").toString();
		}
		if(person.get("middleName")!=null){
			emplName=emplName+person.get("middleName").toString();
		}
		if(person.get("lastName")!=null){
			emplName=emplName+person.get("lastName").toString();
		}
		employeePreferenceInfo.put("emplName",emplName);
		employeePreferenceInfo.put("emplPositionName",emplPositionType.get("description").toString());
	}catch(Exception ex){
		logger.debug("Exception in GetEmployeePreference.groovy file--------->"+ex);
	}
	if(employeeAttendancePreference.get("fromDate")!=null){
		employeePreferenceInfo.put("fromDate",df.format(employeeAttendancePreference.get("fromDate")));
	}else{
		employeePreferenceInfo.put("fromDate","Not Set");
	}
	if(employeeAttendancePreference.get("thruDate")!=null){
		employeePreferenceInfo.put("thruDate",df.format(employeeAttendancePreference.get("thruDate")));
	}else{
		employeePreferenceInfo.put("thruDate","Not Set");
	}
	if(employeeAttendancePreference.get("oTallowances")){
		employeePreferenceInfo.put("oTallowances",employeeAttendancePreference.get("oTallowances").toString());
	}else{
		employeePreferenceInfo.put("oTallowances","Not Set");
	}
	if(employeeAttendancePreference.get("attendanceBonusAllowances")!=null){
		employeePreferenceInfo.put("attendanceBonusAllowances",employeeAttendancePreference.get("attendanceBonusAllowances").toString());
	}else{
		employeePreferenceInfo.put("attendanceBonusAllowances","Not Set");
	}
	if(employeeAttendancePreference.get("extraOTallowances")!=null){
		employeePreferenceInfo.put("extraOTallowances",employeeAttendancePreference.get("extraOTallowances").toString());
	}else{
		employeePreferenceInfo.put("extraOTallowances","Not Set");
	}
	retdesignationPositionList.add(employeePreferenceInfo);
}
context.empAttenPreferenceList=retdesignationPositionList;