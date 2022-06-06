
import javolution.util.FastMap;
import org.ofbiz.humanres.attendance.AttendanceUtils;

// <-----------------------------  (Todays AttendanceInfo ByPosition) ..........Start -------------->
String orgPartyIdVal = orgPartyId;
String emplPositionId = emplPositionId;
Map<String,String> todayAttInfoByP=FastMap.newInstance();
todayAttInfoByP = AttendanceUtils.getTodaysAttendanceInfoByPosition(delegator,orgPartyIdVal,emplPositionId);
context.absentEmployees = todayAttInfoByP.get("absentEmployees") ;
context.presentEmployees = todayAttInfoByP.get("presentEmployees") ;
context.leaveEmployees = todayAttInfoByP.get("leaveEmployees") ;