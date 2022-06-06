
import javolution.util.FastMap;
import org.ofbiz.humanres.attendance.AttendanceUtils;

// <-----------------------------  (total leave on Dashboard) ..........Start -------------->
String orgPartyIdVal = orgPartyId;
Map<String,String> totalLeaveEmpl=FastMap.newInstance();
totalLeaveEmpl = AttendanceUtils.getTodayLeaveInformationForDashBoard(delegator,orgPartyIdVal);
context.totalLeave =totalLeaveEmpl.get("approveLeave") ;