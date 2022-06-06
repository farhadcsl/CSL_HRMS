import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.humanres.attendance.AttendanceUtils;



List<Map<String,String>> retdesignationPositionList=FastList.newInstance();
DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
List<GenericValue> designationPositionList = delegator.findByAnd("DesignPrefEmplposition", UtilMisc.toMap("designationPreferenceId",designationPreferenceId.trim()));
for(GenericValue designation:designationPositionList){
	Map<String,String> designationInfo=FastMap.newInstance();
	designationInfo.put("designationPreferenceId",designation.get("designationPreferenceId").toString());
	designationInfo.put("designEmplposRevesion",designation.get("designEmplposRevesion").toString());
	designationInfo.put("emplPositionId",designation.get("emplPositionId").toString());
	try{
		GenericValue emplPosition=delegator.findOne("EmplPosition",UtilMisc.toMap("emplPositionId", designation.get("emplPositionId").toString()),false);
		if(emplPosition!=null){
			String deptPartyId=(String)emplPosition.get("partyId");
			List<String> deptList=FastList.newInstance();
			deptList=AttendanceUtils.getDepartmentName(delegator,deptPartyId,deptList);
			String deptName=AttendanceUtils.makeDepartmentName(deptList);
			designationInfo.put("deptName",deptName);
		}
		GenericValue emplPositionType=delegator.findOne("EmplPositionType",UtilMisc.toMap("emplPositionTypeId", emplPosition.get("emplPositionTypeId").toString()),false);
		designationInfo.put("emplPositionName",emplPositionType.get("description").toString());
	}catch(Exception ex){
	
	}
	if(designation.get("fromDate")!=null){
		designationInfo.put("fromDate",df.format(designation.get("fromDate")));
	}else{
		designationInfo.put("fromDate","Not Found");
	}
	if(designation.get("thruDate")!=null){
		designationInfo.put("thruDate",df.format(designation.get("thruDate")));
	}else{
		designationInfo.put("thruDate","Not Set");
	}
	retdesignationPositionList.add(designationInfo);
}
context.designationPostionList=retdesignationPositionList;