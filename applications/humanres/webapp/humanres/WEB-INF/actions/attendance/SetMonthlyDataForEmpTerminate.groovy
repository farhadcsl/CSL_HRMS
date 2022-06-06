import javolution.util.FastMap;
import org.ofbiz.humanres.attendance.AttendanceReportUtils;



// Start Total terminate Employee this year and monthly basis
Map<String,String> terminateListMonthlyBasis = FastMap.newInstance();

terminateListMonthlyBasis = AttendanceReportUtils.getMonthlyDataForTerminate(delegator,orgPartyId);
context.terminateInJanuary = (String) terminateListMonthlyBasis.get("January");
context.terminateInFebruary =(String) terminateListMonthlyBasis.get("February");
context.terminateInMarch = (String)terminateListMonthlyBasis.get("March");
context.terminateInApril=(String) terminateListMonthlyBasis.get("April");
context.terminateInMay = (String)terminateListMonthlyBasis.get("May");
context.terminateInJune =(String) terminateListMonthlyBasis.get("June");
context.terminateInJuly =(String) terminateListMonthlyBasis.get("July");
context.terminateInAugust = (String)terminateListMonthlyBasis.get("August");
context.terminateInSeptember =(String) terminateListMonthlyBasis.get("September");
context.terminateInOctober =(String) terminateListMonthlyBasis.get("October");
context.terminateInNovember = (String)terminateListMonthlyBasis.get("November");
context.terminateInDecember =(String) terminateListMonthlyBasis.get("December");

