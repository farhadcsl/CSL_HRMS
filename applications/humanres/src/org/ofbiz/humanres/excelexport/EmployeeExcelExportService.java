package org.ofbiz.humanres.excelexport;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanres.ProfileWorker;
import org.ofbiz.humanres.attendance.AttendanceUtils;

/**
 * 
 * @zzz
 * 
 */
public class EmployeeExcelExportService {
	public static final String module = EmployeeExcelExportService.class.getName();
	
	
	public static List<List<String>> prepareExcelDataForEmployeesDetails(GenericDelegator delegator,List partyList,String orgPartyId){
		List<Map<String, String>> employeesInfo = new ArrayList<Map<String,String>>();
		List<List<String>> allDataForExcelSheet = new ArrayList<List<String>>();
		List<String> oneRowData = new ArrayList<String>();
		List<String> columnHeaders = new ArrayList<String>();
		employeesInfo = getEmployeesInfo(delegator,partyList,orgPartyId);
		columnHeaders = getColumnHeadersForEmployeeDetails(delegator,employeesInfo);
		setExcelHeader(columnHeaders);
		Map<String,String> oneEmployeerowData = null;
		int i=0;
		for(Map<String,String> oneEmployeeInfo : employeesInfo){
			oneEmployeerowData = new HashMap<String, String>();
			oneEmployeerowData = getOneRowDataForEmployeeDetails(delegator, oneEmployeeInfo);
			oneRowData = getOneRowForEmployee(delegator, oneEmployeerowData,columnHeaders);
			allDataForExcelSheet.add(i, oneRowData);			
			i++;	
		}
		
		
		
		return allDataForExcelSheet;
	}
	
	
	public static List<List<String>> prepareExcelDataForMonthlyBonus(GenericDelegator delegator,List<Map<String, String>> employeeBonusList){
		List<Map<String, String>> employeesBonusInfo = new ArrayList<Map<String,String>>();
		List<List<String>> allDataForExcelSheet = new ArrayList<List<String>>();
		List<String> oneRowData = new ArrayList<String>();
		List<String> columnHeaders = new ArrayList<String>();
		
		employeesBonusInfo = getBonusInfo(delegator,employeeBonusList);
		columnHeaders = getColumnHeadersForBonusDetails(delegator,employeesBonusInfo);
		setExcelHeader(columnHeaders);
		Map<String,String> oneEmployeeBonusrowData = null;
		int i=0;
		for(Map<String,String> oneEmployeeBonusInfo : employeesBonusInfo){
			
			oneEmployeeBonusrowData = new HashMap<String, String>();			
			oneEmployeeBonusrowData = getOneRowDataForEmpBonusDetails(delegator, oneEmployeeBonusInfo);			
			oneRowData = getOneRowForEmployee(delegator, oneEmployeeBonusrowData,columnHeaders);
			allDataForExcelSheet.add(i, oneRowData);			
			i++;	
		}
		
		
		
		return allDataForExcelSheet;
	}
	
	
	public static List<List<String>> prepareExcelDataForEmployeeAbsenceReport(List<Map<String, String>> partyList,List<String> columnHeaders){
		List<List<String>> allDataForExcelSheet = new ArrayList<List<String>>();
		List<String> oneRowData = new ArrayList<String>();
		
		setExcelHeader(columnHeaders);
	
		int i=0;
		for(Map<String,String> oneEmployeeInfo : partyList){
		
			oneRowData = getOneRowForEmployee(oneEmployeeInfo,columnHeaders);
			allDataForExcelSheet.add(i, oneRowData);			
			i++;	
		}
		
		return allDataForExcelSheet;
	}
	
	public static List<List<String>> prepareExcelDataForProbitionLeaveSummeryReport(List<Map<String, String>> partyList,List<String> columnHeaders){
		List<List<String>> allDataForExcelSheet = new ArrayList<List<String>>();
		List<String> oneRowData = new ArrayList<String>();
		
		setExcelHeader(columnHeaders);
	
		int i=0;
		for(Map<String,String> oneEmployeeInfo : partyList){
		
			oneRowData = getOneRowForEmployee(oneEmployeeInfo,columnHeaders);
			allDataForExcelSheet.add(i, oneRowData);			
			i++;	
		}
		
		return allDataForExcelSheet;
	}

	public static List<List<String>> prepareExcelDataFor2DaysReport(List<Map<String, String>> partyList,List<String> columnHeaders){
		List<List<String>> allDataForExcelSheet = new ArrayList<List<String>>();
		List<String> oneRowData = new ArrayList<String>();
		
		setExcelHeader(columnHeaders);
	
		int i=0;
		for(Map<String,String> oneEmployeeInfo : partyList){
		
			oneRowData = getOneRowForEmployee(oneEmployeeInfo,columnHeaders);
			allDataForExcelSheet.add(i, oneRowData);			
			i++;	
		}
		
		return allDataForExcelSheet;
	}
	
	public static List<List<String>> prepareExcelDataForReport(List<Map<String, String>> partyList,List<String> columnHeaders){
		List<List<String>> allDataForExcelSheet = new ArrayList<List<String>>();
		List<String> oneRowData = new ArrayList<String>();
		
		setExcelHeader(columnHeaders);
	
		int i=0;
		for(Map<String,String> oneEmployeeInfo : partyList){
		
			oneRowData = getOneRowForEmployee(oneEmployeeInfo,columnHeaders);
			allDataForExcelSheet.add(i, oneRowData);			
			i++;	
		}
		
		return allDataForExcelSheet;
	}
	
	public static List<List<String>> prepareExcelDataForSectionWiseReport(List<Map<String, String>> partyList,List<String> columnHeaders){
		List<List<String>> allDataForExcelSheet = new ArrayList<List<String>>();
		List<String> oneRowData = new ArrayList<String>();
		
		setExcelHeader(columnHeaders);
	
		int i=0;
		for(Map<String,String> oneEmployeeInfo : partyList){
		
			oneRowData = getOneRowForEmployee(oneEmployeeInfo,columnHeaders);
			allDataForExcelSheet.add(i, oneRowData);			
			i++;	
		}
		
		return allDataForExcelSheet;
	}
	

	/**
	 * The method getTwoDaysinfo to get employee information
	 * @param delegator
	 * @param companyId
	 * @param processingMonth
	 * @param bonus
	 * @return
	 */
	
	@SuppressWarnings("null")
	public static List<Map<String, String>> getAttandanceReportInfo(Delegator delegator,List<Map<String, String>> twodaysReportList){
		List<Map<String, String>> reportList = new ArrayList<Map<String, String>>();
        Map<String,String> reportDetailsMap = null; 
        
        String SelectedDate= twodaysReportList.get(0).get("SelectedDate");
		String PreviousDate= twodaysReportList.get(0).get("PreviousDate");
        
        
        int i=0;
        for(Map<String, String> empReport: twodaysReportList){
        	
        	String SL = "";
         	String EmployeeId = "";
         	String EmployeeName="";
         	String Card = "";
         	String Designation = "";
         	String Department ="";
         	String PIn="";
         	String POut="";
         	String SIn="";

         	SL = ExcelUtils.filterDataAndFormatToString(empReport.get("SL"));  
         	EmployeeId = ExcelUtils.filterDataAndFormatToString(empReport.get("EmployeeId"));         	
         	EmployeeName = ExcelUtils.filterDataAndFormatToString(empReport.get("EmployeeName"));  
         	Card = ExcelUtils.filterDataAndFormatToString(empReport.get("Card"));
         	Designation = ExcelUtils.filterDataAndFormatToString(empReport.get("Designation"));
         	Department = ExcelUtils.filterDataAndFormatToString(empReport.get("Department"));
         	PIn = ExcelUtils.filterDataAndFormatToString(empReport.get("PIn"));
         	POut = ExcelUtils.filterDataAndFormatToString(empReport.get("POut"));
         	SIn = ExcelUtils.filterDataAndFormatToString(empReport.get("SIn"));
        	
         	reportDetailsMap = new HashMap<String,String>();
     	
        	reportDetailsMap.put("SL", SL);
         	reportDetailsMap.put("PIN", EmployeeId);
         	reportDetailsMap.put("EmployeeName", EmployeeName);
         	reportDetailsMap.put("Card", Card);
         	reportDetailsMap.put("Designation", Designation);
         	reportDetailsMap.put("Department", Department);
         	reportDetailsMap.put("InTime["+PreviousDate+"]", PIn);
         	reportDetailsMap.put("OutTime["+PreviousDate+"]", POut);
         	reportDetailsMap.put("InTime["+SelectedDate+"]", SIn);

         	reportList.add(i,reportDetailsMap);

			 
        	i++; 
		 }	
		
	return reportList;	
		
	}
	
	
	/**
	 * The method getTwoDaysinfo to get employee information
	 * @param delegator
	 * @param companyId
	 * @param processingMonth
	 * @param bonus
	 * @return
	 */
	
	@SuppressWarnings("null")
	public static List<Map<String, String>> getThreeDaysAttandanceReportInfo(Delegator delegator,List<Map<String, String>> threedaysReportList){
		List<Map<String, String>> reportList = new ArrayList<Map<String, String>>();
        Map<String,String> reportDetailsMap = null; 
        
        String SelectedDate= threedaysReportList.get(0).get("SelectedDate");
		String PreviousDate= threedaysReportList.get(0).get("PreviousDate");
		String TwoPreviousDate= threedaysReportList.get(0).get("TwoPreviousDate");
        
        
        int i=0;
        for(Map<String, String> empReport: threedaysReportList){
        	
        	String SL = "";
         	String EmployeeId = "";
         	String EmployeeName="";
         	String Card = "";
         	String Designation = "";
         	String Department ="";
         	String tPIn="";
         	String tPOut="";
         	String PIn="";
         	String POut="";        	
         	String SIn="";

         	SL = ExcelUtils.filterDataAndFormatToString(empReport.get("SL"));  
         	EmployeeId = ExcelUtils.filterDataAndFormatToString(empReport.get("EmployeeId"));         	
         	EmployeeName = ExcelUtils.filterDataAndFormatToString(empReport.get("EmployeeName"));  
         	Card = ExcelUtils.filterDataAndFormatToString(empReport.get("Card"));
         	Designation = ExcelUtils.filterDataAndFormatToString(empReport.get("Designation"));
         	Department = ExcelUtils.filterDataAndFormatToString(empReport.get("Department"));
         	tPIn = ExcelUtils.filterDataAndFormatToString(empReport.get("tPIn"));
         	tPOut = ExcelUtils.filterDataAndFormatToString(empReport.get("tPOut"));
         	PIn = ExcelUtils.filterDataAndFormatToString(empReport.get("PIn"));
         	POut = ExcelUtils.filterDataAndFormatToString(empReport.get("POut"));
         	SIn = ExcelUtils.filterDataAndFormatToString(empReport.get("SIn"));
        	
         	reportDetailsMap = new HashMap<String,String>();
     	
        	reportDetailsMap.put("SL", SL);
         	reportDetailsMap.put("PIN", EmployeeId);
         	reportDetailsMap.put("EmployeeName", EmployeeName);
         	reportDetailsMap.put("Card", Card);
         	reportDetailsMap.put("Designation", Designation);
         	reportDetailsMap.put("Department", Department);
         	reportDetailsMap.put("InTime["+TwoPreviousDate+"]", tPIn);
         	reportDetailsMap.put("OutTime["+TwoPreviousDate+"]", tPOut);
         	reportDetailsMap.put("InTime["+PreviousDate+"]", PIn);
         	reportDetailsMap.put("OutTime["+PreviousDate+"]", POut);
         	reportDetailsMap.put("InTime["+SelectedDate+"]", SIn);

         	reportList.add(i,reportDetailsMap);

			 
        	i++; 
		 }	
		
	return reportList;	
		
	}
	
	/**
	 * The method getTwodaysinfo to get employee information
	 * @param delegator
	 * @param companyId
	 * @param processingMonth
	 * @param bonus
	 * @return
	 */
	
	@SuppressWarnings("null")
	public static List<Map<String, String>> getSectionWiseReportInfo(Delegator delegator,List<Map<String, String>> sectionWiseStrenthList){
		List<Map<String, String>> reportList = new ArrayList<Map<String, String>>();
        Map<String,String> reportDetailsMap = null; 
        
       // String SelectedDate= sectionWiseStrenthList.get(0).get("SelectedDate");
		//String PreviousDate= sectionWiseStrenthList.get(0).get("PreviousDate");
        
        
        int i=0;
        for(Map<String, String> empReport: sectionWiseStrenthList){
        	
        	String SL = "";
        	String Division = "";
         	String Department = "";
         	String Section="";
         	String Line="";
         	String Required="";
         	String Existing="";
         	String Present = "";
         	String Absent = "";
         	String absentPersentance="";
         	String Remarks = "";

         	SL = ExcelUtils.filterDataAndFormatToString(empReport.get("SL")); 
         	Division = ExcelUtils.filterDataAndFormatToString(empReport.get("Division")); 
         	Department = ExcelUtils.filterDataAndFormatToString(empReport.get("Department"));
         	Section = ExcelUtils.filterDataAndFormatToString(empReport.get("Section"));
         	Line = ExcelUtils.filterDataAndFormatToString(empReport.get("Line"));
         	Required = ExcelUtils.filterDataAndFormatToString(empReport.get("Required"));
         	Existing = ExcelUtils.filterDataAndFormatToString(empReport.get("Existing"));  
         	Present = ExcelUtils.filterDataAndFormatToString(empReport.get("Present"));
         	Absent = ExcelUtils.filterDataAndFormatToString(empReport.get("Absent"));
         	absentPersentance = ExcelUtils.filterDataAndFormatToString(empReport.get("absentPersentance"));
         	Remarks = ExcelUtils.filterDataAndFormatToString(empReport.get("Remarks"));
         	
         	reportDetailsMap = new HashMap<String,String>();
     	
        	reportDetailsMap.put("SL", SL);
        	reportDetailsMap.put("Division", Division);
         	reportDetailsMap.put("Department", Department);
         	reportDetailsMap.put("Section", Section);
         	reportDetailsMap.put("Line", Line);
         	reportDetailsMap.put("Required", Required);
         	reportDetailsMap.put("Existing", Existing);
         	reportDetailsMap.put("Present", Present);
         	reportDetailsMap.put("Absent", Absent);
         	reportDetailsMap.put("Absent %", absentPersentance);
         	reportDetailsMap.put("Remarks", Remarks);
         	

         	reportList.add(i,reportDetailsMap);

			 
        	i++; 
		 }	
		
	return reportList;	
		
	}
	
	/**
	 * Sunipa
	 * The method InTime Probtion leave summery list info 
	 * @param delegator
	 * @return
	 */
	@SuppressWarnings("null")
	public static List<Map<String, String>> getprobitionLeaveReportInfo(Delegator delegator,List<Map<String, String>> probitionLeaveList){
		List<Map<String, String>> reportList = new ArrayList<Map<String, String>>();
        Map<String,String> reportDetailsMap = null; 
       
        int i=0;
        for(Map<String, String> empReport: probitionLeaveList){
        	
        	String SL = "";
        	String PIN = "";
        	String EmpName = "";
        	String Designation="";
         	String Department = "";
         	
         	String JoinDate="";
         	String LeaveEnjoyedDate="";
         	String LeaveType="";
         	String Remarks = "";
         	

         	SL = ExcelUtils.filterDataAndFormatToString(empReport.get("SL")); 
         	PIN = ExcelUtils.filterDataAndFormatToString(empReport.get("PIN")); 
         	EmpName = ExcelUtils.filterDataAndFormatToString(empReport.get("EmployeeName")); 
         	Department = ExcelUtils.filterDataAndFormatToString(empReport.get("Department"));
         	Designation = ExcelUtils.filterDataAndFormatToString(empReport.get("Designation"));
         	
         	JoinDate = ExcelUtils.filterDataAndFormatToString(empReport.get("JoinDate"));
         	LeaveEnjoyedDate = ExcelUtils.filterDataAndFormatToString(empReport.get("LeaveEnjoyedDate"));  
         	LeaveType = ExcelUtils.filterDataAndFormatToString(empReport.get("LeaveType"));
         	
         	Remarks = ExcelUtils.filterDataAndFormatToString(empReport.get("Remarks"));
         	
         	reportDetailsMap = new HashMap<String,String>();
     	
        	reportDetailsMap.put("SL", SL);
        	reportDetailsMap.put("PIN", PIN);
        	reportDetailsMap.put("EmployeeName", EmpName);
         	reportDetailsMap.put("Department", Department);
         	reportDetailsMap.put("Designation", Designation);
         	
         	reportDetailsMap.put("JoinDate", JoinDate);
         	reportDetailsMap.put("LeaveEnjoyedDate", LeaveEnjoyedDate);
         	reportDetailsMap.put("Remarks", Remarks);
         	reportDetailsMap.put("LeaveType", LeaveType);
         	reportList.add(i,reportDetailsMap);

			i++; 
		 }	
		
	return reportList;	
		
	}
	public static List<List<String>> prepareExcelDataForLeaveProbitionReport(List<Map<String, String>> partyList,List<String> columnHeaders){
		List<List<String>> allDataForExcelSheet = new ArrayList<List<String>>();
		List<String> oneRowData = new ArrayList<String>();
		
		setExcelHeader(columnHeaders);
	
		int i=0;
		for(Map<String,String> oneEmployeeInfo : partyList){
		
			oneRowData = getOneRowForEmployee(oneEmployeeInfo,columnHeaders);
			allDataForExcelSheet.add(i, oneRowData);			
			i++;	
		}
		
		return allDataForExcelSheet;
	}	
	/**
	 * Sunipa
	 * The method InTime Probtion leave summery list info 
	 * @param delegator
	 * @return
	 */
	@SuppressWarnings("null")
	public static List<Map<String, String>> getDesignationLeaveReportInfo(Delegator delegator,List<Map<String, String>> probitionLeaveList){
		List<Map<String, String>> reportList = new ArrayList<Map<String, String>>();
        Map<String,String> reportDetailsMap = null; 
       
        int i=0;
        for(Map<String, String> empReport: probitionLeaveList){
        	String Designation="";
        	String SL = "";
        	String PIN = "";
        	String EmpName = "";
        	
         	String Department = "";
         	String LeaveType="";
         	String MaxAvailableLeave="";
         	String Enjoyed="";
         	
         	String Balance = "";
         	
         	
         	Designation = ExcelUtils.filterDataAndFormatToString(empReport.get("Designation"));
         	SL = ExcelUtils.filterDataAndFormatToString(empReport.get("SL")); 
         	PIN = ExcelUtils.filterDataAndFormatToString(empReport.get("PIN")); 
         	EmpName = ExcelUtils.filterDataAndFormatToString(empReport.get("EmployeeName")); 
         	Department = ExcelUtils.filterDataAndFormatToString(empReport.get("Department"));
         	LeaveType = ExcelUtils.filterDataAndFormatToString(empReport.get("LeaveType"));
         	MaxAvailableLeave = ExcelUtils.filterDataAndFormatToString(empReport.get("MaxAvailableLeave"));
         	Enjoyed = ExcelUtils.filterDataAndFormatToString(empReport.get("Enjoyed"));  
         	Balance = ExcelUtils.filterDataAndFormatToString(empReport.get("Balance"));
         	
         	reportDetailsMap = new HashMap<String,String>();
         	
         	reportDetailsMap.put("Designation", Designation);
        	reportDetailsMap.put("SL", SL);
        	reportDetailsMap.put("PIN", PIN);
        	reportDetailsMap.put("EmployeeName", EmpName);
         	reportDetailsMap.put("Department", Department);
         	reportDetailsMap.put("LeaveType", LeaveType);
         	reportDetailsMap.put("MaxAvailableLeave", MaxAvailableLeave);
         	reportDetailsMap.put("Enjoyed", Enjoyed);
         	reportDetailsMap.put("Balance", Balance);
         	
         	reportList.add(i,reportDetailsMap);

			i++; 
		 }	
		
	return reportList;	
		
	}
	public static List<List<String>> prepareExcelDataForLeaveDesignationReport(List<Map<String, String>> partyList,List<String> columnHeaders){
		List<List<String>> allDataForExcelSheet = new ArrayList<List<String>>();
		List<String> oneRowData = new ArrayList<String>();
		
		setExcelHeader(columnHeaders);
	
		int i=0;
		for(Map<String,String> oneEmployeeInfo : partyList){
		
			oneRowData = getOneRowForEmployee(oneEmployeeInfo,columnHeaders);
			allDataForExcelSheet.add(i, oneRowData);			
			i++;	
		}
		
		return allDataForExcelSheet;
	}	
	/**
	 * The method InTime Attendance Missing list info 
	 * @param delegator
	 * @return
	 */
	
	@SuppressWarnings("null")
	public static List<Map<String, String>> getAttandanceMissingInfo(Delegator delegator,List<Map<String, String>> missingList){
		List<Map<String, String>> reportList = new ArrayList<Map<String, String>>();
        Map<String,String> reportDetailsMap = null; 
        
        String SelectedDate= missingList.get(0).get("SelectedDate");
		String PreviousDate= missingList.get(0).get("PreviousDate");
        
        
        int i=0;
        for(Map<String, String> empReport: missingList){
        	
        	String SL = "";
         	String EmployeeId = "";
         	String EmployeeName="";
         	String Card = "";
         	String Designation = "";
         	String Department ="";
         	String PIn="";
         	String POut="";
         	String SIn="";

         	SL = ExcelUtils.filterDataAndFormatToString(empReport.get("SL"));  
         	EmployeeId = ExcelUtils.filterDataAndFormatToString(empReport.get("EmployeeId"));         	
         	EmployeeName = ExcelUtils.filterDataAndFormatToString(empReport.get("EmployeeName"));  
         	Card = ExcelUtils.filterDataAndFormatToString(empReport.get("Card"));
         	Designation = ExcelUtils.filterDataAndFormatToString(empReport.get("Designation"));
         	Department = ExcelUtils.filterDataAndFormatToString(empReport.get("Department"));
         	PIn = ExcelUtils.filterDataAndFormatToString(empReport.get("PIn"));
         	POut = ExcelUtils.filterDataAndFormatToString(empReport.get("POut"));
         	SIn = ExcelUtils.filterDataAndFormatToString(empReport.get("SIn"));
        	
         	reportDetailsMap = new HashMap<String,String>();
     	
        	reportDetailsMap.put("SL", SL);
         	reportDetailsMap.put("PIN", EmployeeId);
         	reportDetailsMap.put("EmployeeName", EmployeeName);
         	reportDetailsMap.put("Card", Card);
         	reportDetailsMap.put("Designation", Designation);
         	reportDetailsMap.put("Department", Department);
         	reportDetailsMap.put("InTime["+PreviousDate+"]", PIn);
         	reportDetailsMap.put("OutTime["+PreviousDate+"]", POut);
         	reportDetailsMap.put("InTime["+SelectedDate+"]", SIn);

         	reportList.add(i,reportDetailsMap);

			 
        	i++; 
		 }	
		
	return reportList;	
		
	}
	
	/**
	 * The method getEmployeeListInfo to get employee information
	 * @param delegator
	 * @param companyId
	 * @param processingMonth
	 * @param bonus
	 * @return
	 */
	public static List<Map<String, String>> getEmployeesInfo(Delegator delegator,List<GenericValue> partyList,String orgPartyId) {
		
//get all the data without any filter
        
        List<GenericValue>  partyAll = FastList.newInstance();
        List<Map<String, String>> employeeDataListMap = new ArrayList<Map<String, String>>();
        Map<String,String> employeeDetailsMap = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
try {			
			
			/*partyAll = delegator.findByAnd("PartyAndPerson", UtilMisc.toMap("statusId", "EMPL_POS_ACTIVE"));*/
			for(GenericValue list : partyList){
				String partyId = (String) list.get("partyId");
				GenericValue pAndp = delegator.findByPrimaryKey("PartyAndPerson", UtilMisc.toMap("partyId", partyId.trim()));
	
				if(UtilValidate.isNotEmpty(pAndp)){
					
					partyAll.add(pAndp);
				}
				
			}
			
			
			
			
			int i=0;
         for (GenericValue map : partyAll) {

         	String partyId = ExcelUtils.filterDataAndFormatToString(map.get("partyId"));         	
         	String firstName = ExcelUtils.filterDataAndFormatToString(map.get("firstName"));
         	String middleName = ExcelUtils.filterDataAndFormatToString(map.get("middleName"));
         	String lastName = ExcelUtils.filterDataAndFormatToString(map.get("lastName"));
         	String FullName = firstName+""+middleName+""+lastName;
			 String fatherName = ExcelUtils.filterDataAndFormatToString(map.get("fatherName"));
			 String mothersMaidenName = ExcelUtils.filterDataAndFormatToString(map.get("mothersMaidenName")); 
			 String SpouseName = ExcelUtils.filterDataAndFormatToString(map.get("spouseName"));
			 String cardId = ExcelUtils.filterDataAndFormatToString(map.get("cardId")); 
         	 String religion= ExcelUtils.filterDataAndFormatToString(map.get("religion")); 
         	 String maritalStatus = ExcelUtils.filterDataAndFormatToString(map.get("maritalStatus"));
			 String gender = ExcelUtils.filterDataAndFormatToString(map.get("gender")); 
			
         	 String birthDate = ExcelUtils.filterDataAndFormatToString(map.get("birthDate"));
         	 String Age =  ProfileWorker.getAgeFromTheBirthDate((Timestamp) map.get("birthDate"));
			 String birthCertificateNo = ExcelUtils.filterDataAndFormatToString(map.get("birthCertificateNo")); 
			 String height= ExcelUtils.filterDataAndFormatToString(map.get("height"));
			 String nationalid = ExcelUtils.filterDataAndFormatToString(map.get("nationalid"));
			String bloodgroup = ExcelUtils.filterDataAndFormatToString(map.get("bloodgroup"));
			String mobileno = ExcelUtils.filterDataAndFormatToString(map.get("mobileno"));
			String statusId = ExcelUtils.filterDataAndFormatToString(map.get("statusId"));
			Timestamp joinDateInTimeStamp=null;

			
			//get present/permanent address from PartyAndPostalAddress entity
            List<GenericValue> list =delegator.findByAnd("PartyAndPostalAddress", UtilMisc.toMap("partyId", partyId));
         	GenericValue  partyAndPostalAddress=  	EntityUtil.getFirst(list);
         	String	presentAddress = "";
         	
         	 if(UtilValidate.isNotEmpty(partyAndPostalAddress)){
         		presentAddress = ExcelUtils.filterDataAndFormatToString(partyAndPostalAddress.get("address1")); 
             
         	 }
         	 
         	
         	//get Email from PartyAndContactMech entity
         	 List<GenericValue> Contact =delegator.findByAnd("PartyAndContactMech", UtilMisc.toMap("partyId", partyId,"contactMechTypeId", "EMAIL_ADDRESS"));
             GenericValue  EmailAddress=  	EntityUtil.getFirst(Contact); 
             String Email = "";
            // String joinDate = "";
             if(UtilValidate.isNotEmpty(EmailAddress)){
            	 Email  = ExcelUtils.filterDataAndFormatToString(EmailAddress.get("infoString")); 
            	// joinDate= ExcelUtils.filterDataAndFormatToString(EmailAddress.get("fromDate"));
             }
             
             
           //get Email from PartyAndContactMech entity
         	 List<GenericValue> employment =delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo", partyId));
             GenericValue employeejoinDate =  	EntityUtil.getFirst(employment); 
             
             String joinDate = "";
             if(UtilValidate.isNotEmpty(employeejoinDate)){ 
            	 joinDate=sdf.format(employeejoinDate.getTimestamp("fromDate"));
             }
             
             String SeparationDate = "";
             if(UtilValidate.isNotEmpty(employeejoinDate)){ 
            	 SeparationDate= ExcelUtils.filterDataAndFormatToString(employeejoinDate.get("thruDate"));
            	 joinDateInTimeStamp=(Timestamp)employeejoinDate.get("thruDate");
             }
             
           
             
           //get Designation & EmpType from EmplPositionFulfillment->EmplPositionAndEmplPositionType entity
         	 List<GenericValue> empPositions =delegator.findByAnd("EmplPositionFulfillment", UtilMisc.toMap("partyId", partyId));
             GenericValue  empPosition=  	EntityUtil.getFirst(empPositions); 
             String Designation = "";
             String emplType = "";
             String confirmationDate="";
             
             if(UtilValidate.isNotEmpty(empPosition)){
            	 String emplPositionId   = ExcelUtils.filterDataAndFormatToString(empPosition.get("emplPositionId")); 
            	 
            	 
            	 List<GenericValue> emplPositionTypes =delegator.findByAnd("EmplPositionAndEmplPositionType", UtilMisc.toMap("emplPositionId", emplPositionId));
                 GenericValue  emplPositionType=  	EntityUtil.getFirst(emplPositionTypes); 
                 if(UtilValidate.isNotEmpty(emplPositionType)){
                	 
                	 Designation=ExcelUtils.filterDataAndFormatToString(emplPositionType.get("description"));
                	 emplType=	ExcelUtils.filterDataAndFormatToString(emplPositionType.get("emplType"));	 
                	 
                 }               
                 
                 /* Confirmation Date*/
                 int numberOfMonth =AttendanceUtils.getNumberOfMonthToConfirmation(partyId, delegator, orgPartyId,employeejoinDate.getTimestamp("fromDate"));

                 Timestamp joindate;
                 Calendar joiningDateTime = Calendar.getInstance();
                 if(UtilValidate.isNotEmpty(employeejoinDate)){ 
                	 joindate=employeejoinDate.getTimestamp("fromDate");
                	 joiningDateTime.setTime(joindate);
                	 joiningDateTime.add(Calendar.MONTH, numberOfMonth);
                	 
                	 Date confirmDate = joiningDateTime.getTime();
                	 confirmationDate=sdf.format(confirmDate);                	 
                 }
                 
            	 
             }
           //get ParmanentAddress from PartyRelationshipAndContactMechDetail->PostalAddress entity
             List<GenericValue> pcontactMech =delegator.findByAnd("PartyRelationshipAndContactMechDetail", UtilMisc.toMap("partyId", partyId,"contactMechTypeId","PERMANENT_ADDRESS"));
             GenericValue  pcontactMechId =	EntityUtil.getFirst(pcontactMech);  
    
             String PermanentAddress = ""; 
             
             if(UtilValidate.isNotEmpty(pcontactMechId)){
            	 
             	String contactMechId= ExcelUtils.filterDataAndFormatToString(pcontactMechId.get("contactMechId")); 
             	List<GenericValue> Parmanent_Address =delegator.findByAnd("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId));
                 GenericValue  pmAddress=  	EntityUtil.getFirst(Parmanent_Address); 
                 if(UtilValidate.isNotEmpty(pmAddress)){
                	 

                	 PermanentAddress=   ExcelUtils.filterDataAndFormatToString(pmAddress.get("address1"));

                	 
                 }
              }
                         
           //get EmergncyAddress from PartyRelationshipAndContactMechDetail->PostalAddress entity
             List<GenericValue> contactMech =delegator.findByAnd("PartyRelationshipAndContactMechDetail", UtilMisc.toMap("partyId", partyId,"contactMechTypeId","EMERGENCY_ADDRESS"));
             GenericValue  gcontactMechId =	EntityUtil.getFirst(contactMech);  
             String emrgncyName= ""; 
             String emrgncyRelation= ""; 
             String emrgncyAddress = ""; 
             String emrgncyContactNo = "";
             String nationalId = "";
             String city = "";
             if(UtilValidate.isNotEmpty(gcontactMechId)){
            	 
            	String contactMechId= ExcelUtils.filterDataAndFormatToString(gcontactMechId.get("contactMechId")); 
            	List<GenericValue> Emergncy_Address =delegator.findByAnd("PostalAddress", UtilMisc.toMap("contactMechId", contactMechId));
                GenericValue  EmergncyAddress=  	EntityUtil.getFirst(Emergncy_Address); 
                if(UtilValidate.isNotEmpty(EmergncyAddress)){
               	 
                	emrgncyName=ExcelUtils.filterDataAndFormatToString(EmergncyAddress.get("attnName"));
                	emrgncyRelation =	ExcelUtils.filterDataAndFormatToString(EmergncyAddress.get("relation"));
                	emrgncyAddress=   ExcelUtils.filterDataAndFormatToString(EmergncyAddress.get("address1"));
                	emrgncyContactNo=  ExcelUtils.filterDataAndFormatToString(EmergncyAddress.get("contactNo"));                	
                	nationalId=   ExcelUtils.filterDataAndFormatToString(EmergncyAddress.get("nationalId"));
                	city=  ExcelUtils.filterDataAndFormatToString(EmergncyAddress.get("city"));
               	 
                }
             }
             
             //get contactNumber from PartyRelationshipAndContactMechDetail->TelecomNumber entity
             List<GenericValue> telecomContactMech =delegator.findByAnd("PartyRelationshipAndContactMechDetail", UtilMisc.toMap("partyId", partyId,"contactMechTypeId","TELECOM_NUMBER"));
             GenericValue  telecomContactMechId =	EntityUtil.getFirst(telecomContactMech);  
    
             String contactNumber = ""; 
             
             if(UtilValidate.isNotEmpty(telecomContactMechId)){
            	 
             	String contactMechId= ExcelUtils.filterDataAndFormatToString(telecomContactMechId.get("contactMechId")); 
             	List<GenericValue> contactNumbers =delegator.findByAnd("TelecomNumber", UtilMisc.toMap("contactMechId", contactMechId));
                 GenericValue  gcontactNumber=  	EntityUtil.getFirst(contactNumbers); 
                 if(UtilValidate.isNotEmpty(gcontactNumber)){
                	 

                	 contactNumber=   ExcelUtils.filterDataAndFormatToString(gcontactNumber.get("contactNumber"));

                	 
                 }
              }
         	   
            
             
           //get BGMEANo from PartyIdentification entity
             List<GenericValue> pIdentificationList =delegator.findByAnd("PartyIdentification", UtilMisc.toMap("partyId", partyId,"partyIdentificationTypeId", "BGMEAId"));
             List<GenericValue> servicebooknoList =delegator.findByAnd("PartyIdentification", UtilMisc.toMap("partyId", partyId,"partyIdentificationTypeId", "SERVICE_BOOK_NO"));
             
             GenericValue  partyIdentification =	EntityUtil.getFirst(pIdentificationList);  
             GenericValue  servicebookno =	EntityUtil.getFirst(servicebooknoList); 
             String bgmeaNo= ""; 
             String serviceBookNo= ""; 
             if(UtilValidate.isNotEmpty(partyIdentification)){
            	 
            	 bgmeaNo= ExcelUtils.filterDataAndFormatToString(partyIdentification.get("idValue")); 
             }
             	if(UtilValidate.isNotEmpty(servicebookno)){
            	 serviceBookNo= ExcelUtils.filterDataAndFormatToString(servicebookno.get("idValue")); 
             }
             
             //get Notes from PartyNoteView entity
             List<GenericValue> partyNoteView =delegator.findByAnd("PartyNoteView", UtilMisc.toMap("targetPartyId", partyId));
             GenericValue  partyNote =	EntityUtil.getFirst(partyNoteView);  
             String note= ""; 
             if(UtilValidate.isNotEmpty(partyNote)){
            	 
            	 note= ExcelUtils.filterDataAndFormatToString(partyNote.get("noteInfo")); 
             }          
             
             //check Picture (value Yes/No) from PartyContent
             
             List<GenericValue> partyContentList =delegator.findByAnd("PartyContent", UtilMisc.toMap("partyId", partyId,"partyContentTypeId", "INTERNAL"));
             GenericValue  partyContent =	EntityUtil.getFirst(partyContentList);  
             String hasPicture= ""; 
             if(UtilValidate.isNotEmpty(partyContent)){
            	 
            	 hasPicture= "Yes"; 
             }else{
            	 hasPicture= "No";
             }
             
           //get Financial Account Number ( Latest Account Number) from FinAccount
             List<GenericValue> finAccounts =delegator.findByAnd("FinAccount", UtilMisc.toMap("ownerPartyId", partyId));
             GenericValue  finAccount =	EntityUtil.getFirst(finAccounts);  
             String finAccountNo= ""; 
             if(UtilValidate.isNotEmpty(finAccount)){
            	 
            	 finAccountNo= ExcelUtils.filterDataAndFormatToString(finAccount.get("finAccountCode")); 
             }
             
             //get employee parents
             String organization="";
             String division="";
             String department="";
             String section="";
             String line="";
             int j=0;
 			List<String> parentList=AttendanceUtils.getPartyDepartment(delegator, partyId, new ArrayList<String>());
            if(UtilValidate.isNotEmpty(parentList)){
            	 
            	Collections.reverse(parentList);
    			for(String parent: parentList){
     				if(j==0){
     					organization=parent;
     				} 
     				else if(j==1){
     					division=parent;
     	 				
     				}
     				else if(j==2){
     					department=parent;
     	 			
     				}
     				else if(j==3){
     					section=parent;
     	 			
     				}
     				else if(j==4){
     					line=parent;	
     				}
     				j++;
     			}
             } 
 			         
           //get employeeId from EmploymentAndPersonAndUserLogin entity

             List<GenericValue> userList = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyId));
             GenericValue  user= EntityUtil.getFirst(userList);  

         	 employeeDetailsMap= new HashMap<String,String>();
         	 /*employeeDetailsMap.put("partyId", partyId);*/
         	
         	if(!UtilValidate.isEmpty(user)){ 
                String employeeId= ExcelUtils.filterDataAndFormatToString(user.get("userLoginId"));
             	employeeDetailsMap.put("EmployeeId", employeeId);	
         	}else{
         		employeeDetailsMap.put("EmployeeId", " ");
         	}
         	
         	employeeDetailsMap.put("EmployeeName", FullName);         
         	employeeDetailsMap.put("Father's Name", fatherName);
         	employeeDetailsMap.put("SpouseName", SpouseName);
         	employeeDetailsMap.put("Mother's Name", mothersMaidenName);        	
         	employeeDetailsMap.put("RFID No", cardId);     
         	employeeDetailsMap.put("Religion", religion);        	
         	employeeDetailsMap.put("Marital status", maritalStatus);
         	employeeDetailsMap.put("Gender", gender);
            employeeDetailsMap.put("Birth Date", birthDate);
         	employeeDetailsMap.put("Age", Age);	
         	employeeDetailsMap.put("Birth Certificate No", birthCertificateNo);
            employeeDetailsMap.put("Height", height);
         	employeeDetailsMap.put("National Id", nationalid); //1	
            employeeDetailsMap.put("Blood Group", bloodgroup);	
         	employeeDetailsMap.put("Mobile No", contactNumber);
         	employeeDetailsMap.put("Present Address", presentAddress);
            employeeDetailsMap.put("Permanent Address", PermanentAddress);
         	employeeDetailsMap.put("Email", Email);
         	employeeDetailsMap.put("BGMEA No", bgmeaNo);
         	employeeDetailsMap.put("SERVICE BOOK NO", serviceBookNo);
         	employeeDetailsMap.put("joindate", joinDate);
         	employeeDetailsMap.put("Separation date", SeparationDate);
         	employeeDetailsMap.put("Confirmation date", confirmationDate);
         	employeeDetailsMap.put("Designation", Designation);
         	
         	employeeDetailsMap.put("Organization", organization);
         	employeeDetailsMap.put("Division", division);
         	employeeDetailsMap.put("Department", department);
         	employeeDetailsMap.put("Section", section);
         	employeeDetailsMap.put("Line", line);
         	
         	employeeDetailsMap.put("EmployeeType", emplType);
         	if(statusId.intern()=="EMPL_POS_ACTIVE"){
         		 employeeDetailsMap.put("Status", "Active");
         	}else{
         		employeeDetailsMap.put("Status", "Separaration");
         	}      	
         	employeeDetailsMap.put("EmrgncyName", emrgncyName);
         	employeeDetailsMap.put("EmrgncyRelation", emrgncyRelation);
         	employeeDetailsMap.put("EmrgncyAddress", emrgncyAddress);
         	employeeDetailsMap.put("EmrgncyContactNo", emrgncyContactNo);
         	
         	employeeDetailsMap.put("Emergency National ID", nationalId);
         	employeeDetailsMap.put("Emergency City", city);
         	employeeDetailsMap.put("Note", note);
         	employeeDetailsMap.put("Picture", hasPicture);
         	employeeDetailsMap.put("Financial Account Number", finAccountNo);
         	        
         	
         	employeeDataListMap.add(i,employeeDetailsMap);
         	 	
         	

         	i++;   
         	
         }
   
         
		} catch (GenericEntityException e1) {
			
			e1.printStackTrace();
		}
		return employeeDataListMap;
	}
	
	
	/**
	 * The method getBonusnfo to get employee information
	 * @param delegator
	 * @param companyId
	 * @param processingMonth
	 * @param bonus
	 * @return
	 */
	
	@SuppressWarnings("null")
	public static List<Map<String, String>> getBonusInfo(Delegator delegator,List<Map<String, String>> employeeBonusList){
		List<Map<String, String>> bonusDataListMap = new ArrayList<Map<String, String>>();
        Map<String,String> bonusDetailsMap = null;    
        
        int i=0;
        for(Map<String, String> empBonus: employeeBonusList){
        	
        	String employeeId = "";
         	String employeeName = "";
         	String holidays="";
         	String absenceDates = "";
         	String lateDates = "";
         	String intimeDates="";
         	String monthlyBonus="";

        	employeeId = ExcelUtils.filterDataAndFormatToString(empBonus.get("employeeId"));  
        	employeeName = ExcelUtils.filterDataAndFormatToString(empBonus.get("employeeName"));         	
        	holidays = ExcelUtils.filterDataAndFormatToString(empBonus.get("holidays"));  
        	absenceDates = ExcelUtils.filterDataAndFormatToString(empBonus.get("absenceDates"));
        	lateDates = ExcelUtils.filterDataAndFormatToString(empBonus.get("lateDates"));
        	intimeDates = ExcelUtils.filterDataAndFormatToString(empBonus.get("intimeDates"));
         	monthlyBonus = ExcelUtils.filterDataAndFormatToString(empBonus.get("monthlyBonus"));
        	
        	bonusDetailsMap = new HashMap<String,String>();
     	
         	bonusDetailsMap.put("Employee Id", employeeId);
         	bonusDetailsMap.put("Employee Name", employeeName);
         	bonusDetailsMap.put("Holidays", holidays);
         	bonusDetailsMap.put("Absence Dates", absenceDates);
         	bonusDetailsMap.put("Late Dates", lateDates);
         	bonusDetailsMap.put("Intime Dates", intimeDates);
         	bonusDetailsMap.put("Bonus Amount", monthlyBonus);
       	
         	bonusDataListMap.add(i,bonusDetailsMap);

			 
        	i++; 
		 }	
		
	return bonusDataListMap;	
		
	}
	
	
	/**
	 * The method getOneRowDataForEmployee to get one row data with key 
	 * @param delegator
	 * @param dataEmployee
	 * @return
	 */
	
	public static Map<String,String> getOneRowDataForEmployeeDetails(Delegator delegator, Map<String,String> dataEmployee){
		Map<String,String> oneEmployeeRowData =new HashMap<String, String>();

    	oneEmployeeRowData = new HashMap<String, String>();
    	oneEmployeeRowData = composeARowForAnEmployeeWithHeader(dataEmployee);
		
		return oneEmployeeRowData;
	}
	
	
	/**
	 * The composeARowForAnEmployeeWithHeaderFor Employees details to compose one row
	 * 
	 * @return
	 */
	public static Map<String,String> composeARowForAnEmployeeWithHeader(
			Map<String, String> employee) {
		Map<String,String> dataLists = new HashMap<String, String>();
	
		dataLists.putAll(employee);

		return dataLists;
	}
	
	/**
	 * The method getOneRowDataForEmpBonusDetails to get one row data with key 
	 * @param delegator
	 * @param dataEmployee
	 * @return
	 */
	
	public static Map<String,String> getOneRowDataForEmpBonusDetails(Delegator delegator, Map<String,String> dataEmployeeBonus){
		Map<String,String> oneEmployeeBonusRowData =new HashMap<String, String>();

		oneEmployeeBonusRowData = new HashMap<String, String>();
		oneEmployeeBonusRowData = composeARowForAnEmployeeWithHeader(dataEmployeeBonus);
		
		return oneEmployeeBonusRowData;
	}
	
	/**
	 * The method getColumnHeadersFor Employees details to get column headers in excel  
	 * 
	 * @return
	 */
	public static List<String> getColumnHeadersForEmployeeDetails(GenericDelegator delegator,List<Map<String, String>> employeesInfo){
		List<String> columnHeaders = new ArrayList<String>();
		List<String> empinfoList = new ArrayList<String>();

		int prevSize = 0;
	    for(Map empInfo : employeesInfo){
	    	int empInfoSize = empInfo.size();
	    	if(empInfoSize < prevSize){
	    		empinfoList = new ArrayList<String>(empInfo.keySet());
	    	}else
	    	empinfoList = new ArrayList<String>(empInfo.keySet());
	    	prevSize = empInfoSize; 
	    }
	    
	    columnHeaders.add(0, "EmployeeId");
	    columnHeaders.add(1, "EmployeeName");	    
	    columnHeaders.add(2, "Status");
	    columnHeaders.add(3, "Designation");
	    columnHeaders.add(4, "Organization");
	    columnHeaders.add(5, "Division");
	    columnHeaders.add(6, "Department");
	    columnHeaders.add(7, "Section");
	    columnHeaders.add(8, "Line");	    
	    columnHeaders.add(9, "EmployeeType");
	    columnHeaders.add(10, "joindate");
	    columnHeaders.add(11, "Confirmation date");
	    columnHeaders.add(12, "Separation date");
	    columnHeaders.add(13, "RFID No");
	    columnHeaders.add(14, "BGMEA No");
	    columnHeaders.add(15, "SERVICE BOOK NO");
	    columnHeaders.add(16, "Gender");
	    columnHeaders.add(17, "Marital status");
	    columnHeaders.add(18, "Father's Name");	    
	    columnHeaders.add(19, "Mother's Name");
	    columnHeaders.add(20, "SpouseName");
	    columnHeaders.add(21, "Religion");
	    columnHeaders.add(22, "National Id");
	    columnHeaders.add(23, "Birth Date");
	    columnHeaders.add(24, "Age");	    
	    columnHeaders.add(25, "Birth Certificate No");
	    columnHeaders.add(26, "Height");
	    columnHeaders.add(27, "Blood Group");	    
	    columnHeaders.add(28, "Mobile No");
	    columnHeaders.add(29, "Email");
	    columnHeaders.add(30, "Present Address");
	    columnHeaders.add(31, "Permanent Address");	    
	    columnHeaders.add(32, "EmrgncyName");
	    columnHeaders.add(33, "EmrgncyRelation");
	    columnHeaders.add(34, "EmrgncyAddress");
	    columnHeaders.add(35, "EmrgncyContactNo");
	    columnHeaders.add(36, "Emergency National ID");
	    columnHeaders.add(37, "Emergency City");
	    columnHeaders.add(38, "Note");
	    columnHeaders.add(39, "Picture");
	    columnHeaders.add(40, "Financial Account Number");
	    
	    
	    
 
	    return columnHeaders;
	}
	
	/**
	 * The method getColumnHeadersFor BonusDetails to get column headers in excel  
	 * 
	 * @return
	 */
	public static List<String> getColumnHeadersForBonusDetails(GenericDelegator delegator,List<Map<String, String>> employeesBonusInfo){
		List<String> columnHeaders = new ArrayList<String>();
	/*	List<String> bonusInfoList = new ArrayList<String>();

		int prevSize = 0;
	    for(Map bonusInfo : employeesBonusInfo){
	    	int bonusInfoSize = bonusInfo.size();
	    	if(bonusInfoSize < prevSize){
	    		bonusInfoList = new ArrayList<String>(bonusInfo.keySet());
	    	}else
	    		bonusInfoList = new ArrayList<String>(bonusInfo.keySet());
	    	prevSize = bonusInfoSize; 
	    }*/
	    
	    columnHeaders.add(0, "Employee Id");
	    columnHeaders.add(1, "Employee Name");	    
	    columnHeaders.add(2, "Holidays");
	    columnHeaders.add(3, "Absence Dates");
	    columnHeaders.add(4, "Late Dates");	    
	    columnHeaders.add(5, "Intime Dates");
	    columnHeaders.add(6, "Bonus Amount");

	    return columnHeaders;
	}
	

	private static void setExcelHeader(List<String> headers){
		ExcelUtils.setColumnHeaders(headers);
	}
	
	
	public static List<String> getOneRowForEmployee(Map<String,String> oneEmployeeRowData,List<String> columnHeaders){
		
	    // value without headers
	    List<String> oneRowList = new ArrayList<String>();
	    int i=0;
	    for(String headersStr : columnHeaders){
	    	String keyValueData = "";
	    	for(Map.Entry<String,String> entry: oneEmployeeRowData.entrySet()){
	    		String keyHeader = entry.getKey();
		    	if(headersStr.equals(keyHeader)){
	    			keyValueData = entry.getValue();
	    			break;	
	    		}
		    }
	    	oneRowList.add(i, keyValueData);
	    	i++;
	    }
	    return oneRowList;
	}
	
	/**
	 * The method getOneRowFor Employees details to one row data without key
	 * 
	 * @return
	 */
	public static List<String> getOneRowForEmployee(GenericDelegator delegator, Map<String,String> oneEmployeeRowData,List<String> columnHeaders){
		
	    // value without headers
	    List<String> oneRowList = new ArrayList<String>();
	    int i=0;
	    for(String headersStr : columnHeaders){
	    	String keyValueData = "";
	    	for(Map.Entry<String,String> entry: oneEmployeeRowData.entrySet()){
	    		String keyHeader = entry.getKey();
		    	if(headersStr.equals(keyHeader)){
	    			keyValueData = entry.getValue();
	    			break;	
	    		}
		    }
	    	oneRowList.add(i, keyValueData);
	    	i++;
	    }
	    return oneRowList;
	}
	
	// start New Empl Status Report
	@SuppressWarnings("null")
	public static List<Map<String, String>> getNewEmplStatusReportInfo(Delegator delegator,List<Map<String, String>> missingList){
		List<Map<String, String>> reportList = new ArrayList<Map<String, String>>();
        Map<String,String> reportDetailsMap = null;     
        
        
        int i=0;
        for(Map<String, String> empReport: missingList){
        	
        	String SL = "";
         	String EmployeeId = "";
         	String EmployeeName="";
         	String Card = "";
         	String Designation = "";
         	String Department ="";
         	String fromDate="";
         	String remarks="";

         	SL = ExcelUtils.filterDataAndFormatToString(empReport.get("sl"));  
         	EmployeeId = ExcelUtils.filterDataAndFormatToString(empReport.get("emplId"));         	
         	EmployeeName = ExcelUtils.filterDataAndFormatToString(empReport.get("name"));  
         	Card = ExcelUtils.filterDataAndFormatToString(empReport.get("cardId"));
         	Designation = ExcelUtils.filterDataAndFormatToString(empReport.get("designation"));
         	Department = ExcelUtils.filterDataAndFormatToString(empReport.get("department"));
         	fromDate = ExcelUtils.filterDataAndFormatToString(empReport.get("fromDate"));
         	remarks = ExcelUtils.filterDataAndFormatToString(empReport.get("remarks"));
        	
         	reportDetailsMap = new HashMap<String,String>();
     	
        	reportDetailsMap.put("SL", SL);
         	reportDetailsMap.put("EmplId", EmployeeId);
         	reportDetailsMap.put("Name", EmployeeName);
         	reportDetailsMap.put("Department", Department);
         	reportDetailsMap.put("Designation", Designation);
         	reportDetailsMap.put("JoiningDate", fromDate);
         	reportDetailsMap.put("RFID", Card);
         	reportDetailsMap.put("Remarks", remarks);

         	reportList.add(i,reportDetailsMap);

			 
        	i++; 
		 }	
		
	return reportList;	
		
	}
	// End New Empl Status Report
	
	
	// start Resigned Employee Report
			@SuppressWarnings("null")
			public static List<Map<String, String>> getResignedEmployeeReportInfo(Delegator delegator,List<Map<String, String>> missingList){
				List<Map<String, String>> reportList = new ArrayList<Map<String, String>>();
		        Map<String,String> reportDetailsMap = null;     
		        
		        
		        int i=0;
		        for(Map<String, String> empReport: missingList){
		        	
		        	String SL = "";
		         	String EmployeeId = "";
		         	String EmployeeName="";
		         	String Card = "";
		         	String Designation = "";
		         	String Department ="";
		         	String fromDate="";
		         	String resignedDate="";
		         	String remarks="";

		         	SL = ExcelUtils.filterDataAndFormatToString(empReport.get("sl"));  
		         	EmployeeId = ExcelUtils.filterDataAndFormatToString(empReport.get("emplId"));         	
		         	EmployeeName = ExcelUtils.filterDataAndFormatToString(empReport.get("name"));  
		         	Card = ExcelUtils.filterDataAndFormatToString(empReport.get("cardId"));
		         	Designation = ExcelUtils.filterDataAndFormatToString(empReport.get("designation"));
		         	Department = ExcelUtils.filterDataAndFormatToString(empReport.get("department"));
		         	fromDate = ExcelUtils.filterDataAndFormatToString(empReport.get("fromDate"));
		         	resignedDate = ExcelUtils.filterDataAndFormatToString(empReport.get("thruDate"));
		         	remarks = ExcelUtils.filterDataAndFormatToString(empReport.get("remarks"));
		        	
		         	reportDetailsMap = new HashMap<String,String>();
		     	
		        	reportDetailsMap.put("SL", SL);
		         	reportDetailsMap.put("EmplId", EmployeeId);
		         	reportDetailsMap.put("Name", EmployeeName);
		         	reportDetailsMap.put("Department", Department);
		         	reportDetailsMap.put("Designation", Designation);
		         	reportDetailsMap.put("JoiningDate", fromDate);
		         	reportDetailsMap.put("ResignedDate", resignedDate);
		         	reportDetailsMap.put("RFID", Card);
		         	reportDetailsMap.put("Remarks", remarks);

		         	reportList.add(i,reportDetailsMap);

					 
		        	i++; 
				 }	
				
			return reportList;	
				
			}
			// End Resigned Employee Report
			
			/**
			 * The method getTwoDaysinfo to get employee information
			 * @param delegator
			 * @param companyId
			 * @param processingMonth
			 * @param bonus
			 * @return
			 */
			
			@SuppressWarnings("null")
			public static List<Map<String, String>> getRejectedApplicantsInfo(Delegator delegator,List<Map<String, String>> rejectedApplicants){
				List<Map<String, String>> reportList = new ArrayList<Map<String, String>>();
		        Map<String,String> reportDetailsMap = null; 

		        
		        
		        int i=0;
		        for(Map<String, String> aplicantReport: rejectedApplicants){
		        	
		        	String SL = "";
		         	String ApplicantId = "";
		         	String ApplicantName="";
		         	String Designation = "";
		         	String RejectedDate = "";
		         	String RejectReason ="";
		         	String Remarks="";		         	

		         	SL = ExcelUtils.filterDataAndFormatToString(aplicantReport.get("SL"));  
		         	ApplicantId = ExcelUtils.filterDataAndFormatToString(aplicantReport.get("ApplicantId"));         	
		         	ApplicantName = ExcelUtils.filterDataAndFormatToString(aplicantReport.get("ApplicantName"));  
		         	Designation = ExcelUtils.filterDataAndFormatToString(aplicantReport.get("Designation"));
		         	RejectedDate = ExcelUtils.filterDataAndFormatToString(aplicantReport.get("RejectedDate"));
		         	RejectReason = ExcelUtils.filterDataAndFormatToString(aplicantReport.get("RejectReason"));
		         	Remarks = ExcelUtils.filterDataAndFormatToString(aplicantReport.get("Remarks"));
		         	
		         	reportDetailsMap = new HashMap<String,String>();
		     	
		        	reportDetailsMap.put("SL", SL);
		         	reportDetailsMap.put("Applicant Id", ApplicantId);
		         	reportDetailsMap.put("Applicant Name", ApplicantName);
		         	reportDetailsMap.put("Designation", Designation);
		         	reportDetailsMap.put("Rejected Date", RejectedDate);
		         	reportDetailsMap.put("Rejected Reason", RejectReason);
		         	reportDetailsMap.put("Remarks", Remarks);

		         	reportList.add(i,reportDetailsMap);

					 
		        	i++; 
				 }	
				
			return reportList;	
				
			}
	
}
