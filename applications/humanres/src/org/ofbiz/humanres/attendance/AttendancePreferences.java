package org.ofbiz.humanres.attendance;



import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.apache.log4j.Logger;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanres.excelexport.EmployeeExcelExportService;
import org.ofbiz.humanres.excelexport.ExcelUtils;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.opentaps.common.util.UtilCommon;


/**
 * @author zzz
 * @author zzz
 * 
 */
public class AttendancePreferences {
	public static final String module = AttendancePreferences.class.getName();
	private static Logger logger=Logger.getLogger("AttendancePreferences");

	public static Map<String, Object> findDailyAbsenceReport(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		//DateFormat df = new SimpleDateFormat("dd/MM/yyyy"); 
		
		Delegator delegator = dctx.getDelegator();
		
		String fromDate = (String) context.get("fromDate");
		String orgPartyId = (String) context.get("orgPartyId"); 
		String companyId=orgPartyId;// Here orgPartyId change because of getting id of division/dept/sec/line,so we need to store the same value in different variable.
		TimeZone timeZone=(TimeZone) context.get("timeZone"); 
		Locale locale=(Locale) context.get("locale");
		DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
		
		List<Map<String, String>> absenceList=FastList.newInstance();
		java.sql.Date fromTime=null;
		
	  	// filter on division
	    String division = (String) context.get("division");
		if (UtilValidate.isNotEmpty(division)) {
			  orgPartyId=division;
	        }
		
		// filter on department
		String department = (String) context.get("department");
		      
	    if (UtilValidate.isNotEmpty(department)) {
		   orgPartyId=department;
		   
	        }
	    
	    // filter on section
	    String section = (String) context.get("section");
	      
        if (UtilValidate.isNotEmpty(section)) {
        	  orgPartyId=section; 
        }
	    // filter on line
        String line = (String) context.get("line");
        
        if (UtilValidate.isNotEmpty(line)) {
        	  orgPartyId=line;  
        }
		List<GenericValue> partyList=FastList.newInstance();
		
		if(fromDate!=null&&!fromDate.isEmpty()){
				
			
			if(fromDate!=null && !fromDate.isEmpty()){
				try{
					fromTime=Date.valueOf(fromDate);
				}catch(Exception e){
					fromTime=new Date(System.currentTimeMillis());
				}
			}else{
				fromTime=new Date(System.currentTimeMillis());
			}
			java.sql.Date fromTime1=new Date(System.currentTimeMillis());
			Timestamp dayStart=AttendanceUtils.getModifiedDayStart(new Timestamp(fromTime.getTime()));
			Timestamp dayEnd=AttendanceUtils.getModifiedDayEnd(new Timestamp(fromTime.getTime()));
			
			List<Timestamp> holidays=AttendanceUtils.getHolidays(orgPartyId, dayStart, dayEnd, delegator);
			if(holidays==null||holidays.size()<=0){
				partyList=getTotalEmployee(delegator,partyList,orgPartyId);
				partyList=AttendanceUtils.filterValidEmployeeListByDate(delegator, partyList, dayStart);
				int sl=1;
				for(GenericValue party:partyList){
					
					String partyId=party.getString("partyId");
					String employeeId=AttendanceUtils.getUserLoginIDFromPartyID(delegator,partyId);
					if (employeeId==null||employeeId==""){
						continue;
					}
					List<GenericValue> employeeLeaves=new ArrayList<GenericValue>();
				    List<EntityExpr> exprsList = FastList.newInstance();
				    exprsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				    exprsList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
				    try{
					employeeLeaves = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsList, EntityOperator.AND), null, null, null, false);
				    }catch(Exception e){
				    	logger.debug("Exception to get leave in findDailyAbsenceReport function---->"+e);
				    }
					boolean checkLeaveStatus=AttendanceUtils.checkEmpLeave(employeeLeaves,fromTime.getTime());
					if(!checkLeaveStatus){
					if(AttendanceUtils.checkCardExistanceByPartyId(delegator,partyId))
					{
					
						List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
						List<EntityExpr> exprs = FastList.newInstance();
						List<String> orderBy = FastList.newInstance();
						orderBy.add("logtimeStamp ASC");
						exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
						exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
						exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId ));
						
						try {
							//delegator.findList(entityName, entityCondition, fieldsToSelect, orderBy, findOptions, useCache)
							employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);
						
							if(employeeLogs.size()<=0){
								int totalAbsence=getThisMonthTotalAbsencByPartyId(delegator,partyId,dayEnd,timeZone,locale,companyId);
								int totalcontinuesAbsence=getContinuesAbesenceDays(delegator,partyId,dayEnd,timeZone,locale,companyId);
							
								Map<String, String> employee=	FastMap.newInstance();
								
								employee.put("SL", String.valueOf(sl));
								employee.put("partyId", partyId);
								employee.put("EmployeeId", employeeId);
								
								String cardId=	AttendanceUtils.getCardIdFromPartyId(delegator,partyId);
								
								String designation=	AttendanceUtils.getPartyDesignation(delegator,partyId);
								
								List<String> departmentNameList=FastList.newInstance();
								departmentNameList=AttendanceUtils.getPartyDepartment(delegator,partyId,departmentNameList);
								
								employee.put("Card", cardId);
								employee.put("Designation", designation);
								
								
								employee.put("Department", AttendanceUtils.getDepartmentName(departmentNameList));
								
								employee.put("TotalAbsence", String.valueOf(totalAbsence));
								employee.put("ContinuesAbsence", String.valueOf(totalcontinuesAbsence));
	
		
								GenericValue person = delegator.findByPrimaryKey(
										"Person", UtilMisc.toMap("partyId",partyId.trim()));
		
								if (!UtilValidate.isEmpty(person)){
									String name=AttendanceUtils.avoidNullPointerException((String)person.get("firstName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("middleName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("lastName"));
									employee.put("EmployeeName", name);
									//employee.put("Date", df.format(fromTime));
								}
								absenceList.add(employee);	
								sl++;
							}
						
						} catch (GenericEntityException e) {
							e.printStackTrace();
						}
				 }
				}
				}
			}
			
		}
		ArrayList<Map> sortedAbsentList = AttendanceUtils.getSortedList(absenceList);
	    int serial =1;
	    for(int i=0;i<sortedAbsentList.size();i++){
	    	Map<String,String> sortedMap = sortedAbsentList.get(i);
	    	sortedMap.put("SL", String.valueOf(serial++));
	    	sortedAbsentList.set(i, sortedMap);
	    }
	    result.put("absenceList", sortedAbsentList);
	    if(UtilValidate.isNotEmpty(fromTime)){
			result.put("selectedDate", fmt.format(fromTime));
	
	    }else{
			result.put("selectedDate", fromDate);
	
	    }
		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Employee Absence List");
		return result;
	}

	public static Map<String, Object> findDailyAbsence(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy"); 
		
		Delegator delegator = dctx.getDelegator();
		
		String fromDate = (String) context.get("fromDate");
		String toDate = (String) context.get("toDate");
		String orgPartyId = (String) context.get("orgPartyId"); 
		String entryTime="";
		String maximumStayTime="";
		
		GenericValue orgPreference=	AttendanceUtils.getOrgPreference(delegator,orgPartyId);
		if(orgPreference!=null){
			
			if(orgPreference.get("entryTime")!=null){
				 entryTime=(String)orgPreference.get("entryTime");
			}
			if(orgPreference.get("maximumStayTime")!=null){
				 maximumStayTime=(String)orgPreference.get("maximumStayTime");
			}
			 
		}
		
		java.sql.Date fromTime=null;
		java.sql.Date toTime=null;
		
	
		List<GenericValue> partyRelationships=FastList.newInstance();
		List<Timestamp> holidays=new ArrayList<Timestamp>();
	
		List<Map<String, String>> absenceList=FastList.newInstance();
		try {
	
			if(fromDate!=null&&!fromDate.isEmpty()&&toDate!=null&&!toDate.isEmpty()){
				fromTime=Date.valueOf(fromDate);
				toTime=Date.valueOf(toDate);
				holidays=AttendanceUtils.getHolidays(orgPartyId, new Timestamp(fromTime.getTime()), new Timestamp(toTime.getTime()), delegator);
				partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom",orgPartyId.trim()));
			}
			
			for(GenericValue partyRelationship:partyRelationships){
				
				List<GenericValue> employeeLeaves=new ArrayList<GenericValue>();
				
				String employeeId=(String)partyRelationship.get("partyIdTo");
				
				
		        List<EntityExpr> exprsList = FastList.newInstance();
		        exprsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
		        exprsList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
				
				employeeLeaves = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsList, EntityOperator.AND), null, null, null, false);
				
				GenericValue empOrRostPreference=	AttendanceUtils.getEmpORRosterPreference(delegator,orgPartyId, employeeId);
				
				if(empOrRostPreference!=null){
					if(empOrRostPreference.get("entryTime")!=null){
						 entryTime=(String)empOrRostPreference.get("entryTime");
					}
					if(empOrRostPreference.get("maximumStayTime")!=null){
						 maximumStayTime=(String)empOrRostPreference.get("maximumStayTime");
					}
					
				}
				
				long stime=AttendanceUtils.getStartTime(fromTime);
				long etime=AttendanceUtils.getEndTime(toTime);
			
				
				while(stime<=etime){
					
					List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
					
					if(!AttendanceUtils.checkHoliday(holidays, new Timestamp(stime))&&!AttendanceUtils.checkEmpLeave(employeeLeaves,stime)){
						
						GenericValue dayPreference=	AttendanceUtils.getDayPreference(delegator, new Date(stime), orgPartyId);
						
						if(dayPreference!=null){
							if(dayPreference.get("entryTime")!=null){
								 entryTime=(String)dayPreference.get("entryTime");
							}
							if(dayPreference.get("maximumStayTime")!=null){
								 maximumStayTime=(String)dayPreference.get("maximumStayTime");
							}
							
						}
						
						long entryTimeInDate=AttendanceUtils.getLateEntryTimeInDate(entryTime,new Date(stime));
						long maximumStayTimeInDate=AttendanceUtils.getLateEntryTimeInDate(maximumStayTime,new Date(stime));
						long startTime=AttendanceUtils.getStartTime(new Date(stime));
						
						List<EntityExpr> exprs = FastList.newInstance();
						List<String> orderBy = FastList.newInstance();
						orderBy.add("logtimeStamp ASC");
						exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(startTime)));
						exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(maximumStayTimeInDate)));
						exprs.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, employeeId));
						
						try {
							//delegator.findList(entityName, entityCondition, fieldsToSelect, orderBy, findOptions, useCache)
							employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);
						} catch (GenericEntityException e) {
							e.printStackTrace();
						}
							
							if(employeeLogs.size()<=0){
								Map<String, String> employee=	FastMap.newInstance();
								employee.put("EmployeeId", employeeId);
								
								GenericValue person = delegator.findByPrimaryKey(
										"Person", UtilMisc.toMap("partyId",employeeId.trim()));

								if (!UtilValidate.isEmpty(person)){
									String name=AttendanceUtils.avoidNullPointerException((String)person.get("firstName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("middleName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("lastName"));
									employee.put("EmployeeName", name);
									java.util.Date currenTimeDate= new Date(stime);
									
									employee.put("Date", df.format(currenTimeDate));
								}
								absenceList.add(employee);		
							}
						
					}
				
					stime=stime+86400000;
				}
		
			}

		} catch (GenericEntityException e) {
			result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
			result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
			return result;
		}
		
	    result.put("absenceList", absenceList);
		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Employee Absence List");
		return result;
	}	
	
	
	public static Map<String, Object> findDailyLate1(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy"); 
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
		Delegator delegator = dctx.getDelegator();
		
		String fromDate = (String) context.get("fromDate");
		String toDate = (String) context.get("toDate");
		String fromTime = (String) context.get("fromTime");
		String toTime = (String) context.get("toTime");
		String employeeId = (String) context.get("employeeId");
		
		String orgPartyId = (String) context.get("orgPartyId"); 
		String unitPartyId=orgPartyId;
		int sl = 1;
		
		// filter on division
	    String division = (String) context.get("division");
		if (UtilValidate.isNotEmpty(division)) {
				unitPartyId=division;
	        }
		
		// filter on department
		String department = (String) context.get("department");
		      
	    if (UtilValidate.isNotEmpty(department)) {
	    		unitPartyId=department;   
	        }
	    
	    // filter on section
	    String section = (String) context.get("section");
	      
        if (UtilValidate.isNotEmpty(section)) {
        	unitPartyId=section; 
        }
	    // filter on line
        String line = (String) context.get("line");
        
        if (UtilValidate.isNotEmpty(line)) {
        	unitPartyId=line;  
        }
        	//logger.debug("orgPartyId-------->"+orgPartyId);
		
		java.sql.Date fromDateToSqlDate = null;
		java.sql.Date toDateToSqlDate = null;
		
		List<Map<String, String>> lateList=FastList.newInstance();
		List<GenericValue> partyList=FastList.newInstance();
		try {
			long stime=0;
			long etime=0;
		
			if(fromDate!=null&&!fromDate.isEmpty()){
				fromDateToSqlDate=Date.valueOf(fromDate);
				if(toDate!=null && toDate.length()>0){
					toDateToSqlDate=Date.valueOf(toDate);
				}else{
					toDateToSqlDate=Date.valueOf(fromDate);
				}
				stime=AttendanceUtils.getStartTime(fromDateToSqlDate);
				etime=AttendanceUtils.getEndTime(toDateToSqlDate);
				if(employeeId.length()<=0 || employeeId==null){
				//	partyList=AttendanceUtils.getTotalEmployee(delegator,partyList,unitPartyId);
					partyList=AttendanceUtils.getTotalActiveEmployee(delegator, partyList, unitPartyId);
				}
			}
			logger.debug("partyList.size()------->"+partyList.size());
			while(stime<=etime){
				Timestamp 	startTimestamp=null;
				Timestamp	endTimestamp=null;
				if(fromTime!=null && fromTime.length()>0){
					startTimestamp=AttendanceUtils.getModifiedDayStartWithTime(new Timestamp(stime), fromTime);
				}else{
					startTimestamp=AttendanceUtils.getModifiedDayStart(new Timestamp(stime));
				}
			    if(toTime!=null && toTime.length()>0){
			    	endTimestamp= AttendanceUtils.getModifiedDayEndWithTime(new Timestamp(stime),toTime);
			    }else{
			    	endTimestamp=AttendanceUtils.getModifiedDayEnd(new Timestamp(stime));
			    }
				List<GenericValue> employeeLeaves=new ArrayList<GenericValue>();
				if(employeeId!=null && employeeId.length()>0){
					String partyId=AttendanceUtils.getPartyIDFromUserLoginID(delegator, employeeId);
				    List<EntityExpr> exprsList = FastList.newInstance();
				    exprsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				    exprsList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
						
				    try {
							employeeLeaves = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsList, EntityOperator.AND), null, null, null, false);
						} catch (GenericEntityException e) {
							e.printStackTrace();
						}
						
						
						List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
						
//						Map<String,Object> preference=UtilAttendancePreference.getPreference(partyId, delegator, new Date(stime), orgPartyId);
						String entryTime=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, new Date(stime), orgPartyId, UtilPreferenceProperties.ENTRYTIME);
						String lateEntryTime=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, new Date(stime), orgPartyId, UtilPreferenceProperties.LATEENTRYTIME);
						
						long entryTimeInDate=AttendanceUtils.getLateEntryTimeInDate(entryTime,new Date(stime));
						
						List<EntityExpr> exprs = FastList.newInstance();
						List<String> orderBy = FastList.newInstance();
						orderBy.add("logtimeStamp ASC");
						
						exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO, startTimestamp));
						exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.LESS_THAN_EQUAL_TO, endTimestamp));
						exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
						logger.debug("Condition for late entry------>"+exprs);
						//delegator.findList(entityName, entityCondition, fieldsToSelect, orderBy, findOptions, useCache)
						employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);	
					
						if(employeeLogs.size()>0){
							
							long lateEntryTimeInDate=AttendanceUtils.getLateEntryTimeInDate(lateEntryTime,new Date(stime));
							
							GenericValue employeeLog=employeeLogs.get(0);
							Timestamp	time=(Timestamp)employeeLog.get("logtimeStamp");
							if(AttendanceUtils.compareLateLogtime(lateEntryTimeInDate,time.getTime())){
							
								GenericValue person = delegator.findByPrimaryKey(
											"Person", UtilMisc.toMap("partyId",partyId.trim()));
								
								String cardId =	AttendanceUtils.getCardIdFromPartyId(delegator,partyId);
								String designation=	AttendanceUtils.getPartyDesignation(delegator,partyId);
								List<String> departmentNameList=FastList.newInstance();
								departmentNameList=AttendanceUtils.getPartyDepartment(delegator,partyId,departmentNameList);
								String realEmployeeId=AttendanceUtils.getUserLoginIDFromPartyID(delegator, partyId);
								if (!UtilValidate.isEmpty(person)){
									Map<String, String> employee=FastMap.newInstance();
									employee.put("date", df.format(new Date(stime)));
									employee.put("EmployeeId", realEmployeeId);
									employee.put("Card", cardId);
									employee.put("SL", String.valueOf(sl++));
									employee.put("Designation", designation);
									employee.put("Department", AttendanceUtils.getDepartmentName(departmentNameList));
									String name=AttendanceUtils.avoidNullPointerException((String)person.get("firstName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("middleName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("lastName"));
									employee.put("EmployeeName", name);									
									employee.put("Date", df.format(new Date(stime)));
									employee.put("EntryTime", simpleDateFormat.format(new Date(time.getTime())));
									List<GenericValue> empSkillList = delegator.findByAnd("PartySkillTest", UtilMisc.toMap("partyId",partyId.trim()));
									if (!UtilValidate.isEmpty(empSkillList)) {
										StringBuffer processList = new StringBuffer();
										 int j=0;
									for(GenericValue empProcess: empSkillList){
									    String workEffortId = 	empProcess.getString("workEffortId");
									    List<GenericValue> workEffortList = delegator.findByAnd("WorkEffort", UtilMisc.toMap("workEffortId",workEffortId.trim()));
									 
									 if (!UtilValidate.isEmpty(workEffortList)) {
										 GenericValue workEffort = EntityUtil.getFirst(workEffortList);
										 processList.append(workEffort.getString("workEffortName"));
										 if(empSkillList.size()>1 && empSkillList.size()-1!=j){
											 processList.append(", ");
										 }

										 employee.put("Process", processList.toString()); 
									   }

									    j++;
									}							
								   }
									lateList.add(employee);
								}
								
							}
							
						}
				}else{
					for(GenericValue party:partyList){
						String partyId=party.getString("partyId");
						
					    List<EntityExpr> exprsList = FastList.newInstance();
					    exprsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
					    exprsList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
							
					    try {
								employeeLeaves = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsList, EntityOperator.AND), null, null, null, false);
							} catch (GenericEntityException e) {
								e.printStackTrace();
							}
						
							
							List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
							
//							Map<String,Object> preference=UtilAttendancePreference.getPreference(partyId, delegator, new Date(stime), orgPartyId);
							String entryTime=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, new Date(stime), orgPartyId, UtilPreferenceProperties.ENTRYTIME);
							String lateEntryTime=UtilAttendancePreference.getPreferencePropertyValue(partyId, delegator, new Date(stime), orgPartyId, UtilPreferenceProperties.LATEENTRYTIME);
							
							long entryTimeInDate=AttendanceUtils.getLateEntryTimeInDate(entryTime,new Date(stime));
							
							List<EntityExpr> exprs = FastList.newInstance();
							List<String> orderBy = FastList.newInstance();
							orderBy.add("logtimeStamp ASC");
							
							exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO, startTimestamp));
							exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.LESS_THAN_EQUAL_TO, endTimestamp));
							exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
							//delegator.findList(entityName, entityCondition, fieldsToSelect, orderBy, findOptions, useCache)
							employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);	
						
							if(!UtilValidate.isEmpty(employeeLogs)){
								
								long lateEntryTimeInDate=AttendanceUtils.getLateEntryTimeInDate(lateEntryTime,new Date(stime));
								
								GenericValue employeeLog=employeeLogs.get(0);
								Timestamp	time=(Timestamp)employeeLog.get("logtimeStamp");
								if(AttendanceUtils.compareLateLogtime(lateEntryTimeInDate,time.getTime())){
								
									GenericValue person = delegator.findByPrimaryKey(
												"Person", UtilMisc.toMap("partyId",partyId.trim()));
									
									String cardId =	AttendanceUtils.getCardIdFromPartyId(delegator,partyId);
									String designation=	AttendanceUtils.getPartyDesignation(delegator,partyId);
									List<String> departmentNameList=FastList.newInstance();
									departmentNameList=AttendanceUtils.getPartyDepartment(delegator,partyId,departmentNameList);
									String realEmployeeId=AttendanceUtils.getUserLoginIDFromPartyID(delegator, partyId);
									if (!UtilValidate.isEmpty(person)){
										Map<String, String> employee=FastMap.newInstance();
										employee.put("date", df.format(new Date(stime)));
										employee.put("EmployeeId", realEmployeeId);
										employee.put("Card", cardId);
										employee.put("SL", String.valueOf(sl++));
										employee.put("Designation", designation);
										employee.put("Department", AttendanceUtils.getDepartmentName(departmentNameList));
										String name=AttendanceUtils.avoidNullPointerException((String)person.get("firstName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("middleName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("lastName"));
										employee.put("EmployeeName", name);									
										employee.put("Date", df.format(new Date(stime)));
										employee.put("EntryTime", simpleDateFormat.format(new Date(time.getTime())));
										List<GenericValue> empSkillList = delegator.findByAnd("PartySkillTest", UtilMisc.toMap("partyId",partyId.trim()));
										if (!UtilValidate.isEmpty(empSkillList)) {
											StringBuffer processList = new StringBuffer();
											 int j=0;
										for(GenericValue empProcess: empSkillList){
										    String workEffortId = 	empProcess.getString("workEffortId");
										    List<GenericValue> workEffortList = delegator.findByAnd("WorkEffort", UtilMisc.toMap("workEffortId",workEffortId.trim()));
										 
										 if (!UtilValidate.isEmpty(workEffortList)) {
											 GenericValue workEffort = EntityUtil.getFirst(workEffortList);
											 
											 String workEffortName = workEffort.getString("workEffortName");
											 if(UtilValidate.isEmpty(workEffortName)){
												 workEffortName="";
											 }
											 
											 processList.append(workEffortName);
											 if(empSkillList.size()>1 && empSkillList.size()-1!=j){
												 processList.append(", ");
											 }

											 employee.put("Process", processList.toString()); 
										   }

										    j++;
										}							
									   }
										
										lateList.add(employee);
										/*logger.debug("employeeDetailsss "+partyId+" "+cardId+" "+sl+" "+designation+" ");*/
									}
									
								}
								
							}
							
						}
				}
				stime=stime+86400000;
				}

		} catch (GenericEntityException e) {
			result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
			result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
			return result;
		}
		
		ArrayList<Map> sortedLateList = AttendanceUtils.getSortedList(lateList);
	    int serial =1;
	    for(int i=0;i<sortedLateList.size();i++){
	    	Map<String,String> sortedMap = sortedLateList.get(i);
	    	sortedMap.put("SL", String.valueOf(serial++));
	    	sortedLateList.set(i, sortedMap);
	    }
	    result.put("lateList", sortedLateList);
	    if(UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(toDate)){
	    	result.put("selectedDate", df.format(fromDateToSqlDate));
			result.put("selectToDate", df.format(toDateToSqlDate));	
	    }else{
	    	result.put("selectedDate", fromDate);
			result.put("selectToDate", toDate);
	    }
	    
		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Employee Late List");
		return result;
	}


	public static Map<String, Object> findOverTimes(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy"); 
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
		Delegator delegator = dctx.getDelegator();
		
		String fromDate = (String) context.get("fromDate");
		String toDate = (String) context.get("toDate");
		String orgPartyId = (String) context.get("orgPartyId"); 
		String entryTime="";
		String maximumStayTime="";
		String oTallowances="";
		//String extraOTallowances="";
		//String extraOTStartTime="";
		//String extraOTEndTime="";
		String oTStartTime="";
		String oTEndTime="";
		
		GenericValue orgPreference=	AttendanceUtils.getOrgPreference(delegator,orgPartyId);
		if(orgPreference!=null){
			
			if(orgPreference.get("entryTime")!=null){
				 entryTime=(String)orgPreference.get("entryTime");
			}
			if(orgPreference.get("maximumStayTime")!=null){
				 maximumStayTime=(String)orgPreference.get("maximumStayTime");
			}
			if(orgPreference.get("oTallowances")!=null){
				oTallowances=(String)orgPreference.get("oTallowances");
			}
		/*	if(orgPreference.get("extraOTallowances")!=null){
				extraOTallowances=(String)orgPreference.get("extraOTallowances");
			}*/
		/*	if(orgPreference.get("extraOTStartTime")!=null){
				extraOTStartTime=(String)orgPreference.get("extraOTStartTime");
			}
			if(orgPreference.get("extraOTEndTime")!=null){
				extraOTEndTime=(String)orgPreference.get("extraOTEndTime");
			}*/
			if(orgPreference.get("oTStartTime")!=null){
				oTStartTime=(String)orgPreference.get("oTStartTime");
			}
			if(orgPreference.get("oTEndTime")!=null){
				oTEndTime=(String)orgPreference.get("oTEndTime");
			}
			 
		}
		
		java.sql.Date fromTime=null;
		java.sql.Date toTime=null;
		
		logger.debug("fromDate in Attendance preferance--->"+fromDate);
		logger.debug("toDate in Attendance preferance--->"+toDate);
		List<GenericValue> partyRelationships=FastList.newInstance();
		List<Map<String, String>> overtimeList=FastList.newInstance();
		List<Timestamp> holidays=new ArrayList<Timestamp>();
		try {
			if(fromDate!=null&&!fromDate.isEmpty()&&toDate!=null&&!toDate.isEmpty()){
			fromTime=Date.valueOf(fromDate);
			toTime=Date.valueOf(toDate);
				partyRelationships = delegator.findByAnd(
					"PartyRelationship", UtilMisc.toMap("partyIdFrom",orgPartyId.trim()));
				holidays=AttendanceUtils.getHolidays(orgPartyId, new Timestamp(fromTime.getTime()), new Timestamp(toTime.getTime()), delegator);
			}
			
			
		
			for(GenericValue partyRelationship:partyRelationships){
				
				List<GenericValue> employeeLeaves=new ArrayList<GenericValue>();
				
				String employeeId=(String)partyRelationship.get("partyIdTo");
				
				GenericValue empOrRostPreference=	AttendanceUtils.getEmpORRosterPreference(delegator,orgPartyId, employeeId);
				
				if(empOrRostPreference!=null){
					if(empOrRostPreference.get("entryTime")!=null){
						 entryTime=(String)empOrRostPreference.get("entryTime");
					}
					if(empOrRostPreference.get("maximumStayTime")!=null){
						 maximumStayTime=(String)empOrRostPreference.get("maximumStayTime");
					}
					if(empOrRostPreference.get("oTallowances")!=null){
						oTallowances=(String)empOrRostPreference.get("oTallowances");
					}
			/*		if(empOrRostPreference.get("extraOTallowances")!=null){
						extraOTallowances=(String)empOrRostPreference.get("extraOTallowances");
					}*/
				/*	if(empOrRostPreference.get("extraOTStartTime")!=null){
						extraOTStartTime=(String)empOrRostPreference.get("extraOTStartTime");
					}
					if(empOrRostPreference.get("extraOTEndTime")!=null){
						extraOTEndTime=(String)empOrRostPreference.get("extraOTEndTime");
					}*/
					if(empOrRostPreference.get("oTStartTime")!=null){
						oTStartTime=(String)empOrRostPreference.get("oTStartTime");
					}
					if(empOrRostPreference.get("oTEndTime")!=null){
						oTEndTime=(String)empOrRostPreference.get("oTEndTime");
					}
					
				}
				
			    List<EntityExpr> exprsList = FastList.newInstance();
			    exprsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
			    exprsList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
					
			    try {
						employeeLeaves = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsList, EntityOperator.AND), null, null, null, false);
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
				
				long stime=AttendanceUtils.getStartTime(fromTime);
				long etime=AttendanceUtils.getEndTime(toTime);

				
				while(stime<=etime){
					
							long otHours=0;
							long otMinutes=0;
							//long eotHours=0;
							//long eotMinutes=0;
							
							List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
							Map<String, String> employee=FastMap.newInstance();
							employee.put("EmployeeId", employeeId);
						
							 GenericValue person = delegator.findByPrimaryKey(
										"Person", UtilMisc.toMap("partyId",employeeId.trim()));

						
							if (!UtilValidate.isEmpty(person)){
								String name=AttendanceUtils.avoidNullPointerException((String)person.get("firstName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("middleName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("lastName"));
								employee.put("EmployeeName", name);
								employee.put("Date", df.format(new Date(stime)));
							}
							if(!AttendanceUtils.checkHoliday(holidays, new Timestamp(stime))&&!AttendanceUtils.checkEmpLeave(employeeLeaves,stime)){
								
								GenericValue dayPreference=	AttendanceUtils.getDayPreference(delegator, new Date(stime), orgPartyId);
								
								if(dayPreference!=null){
									if(dayPreference.get("entryTime")!=null){
										 entryTime=(String)dayPreference.get("entryTime");
									}
									if(dayPreference.get("maximumStayTime")!=null){
										 maximumStayTime=(String)dayPreference.get("maximumStayTime");
									}
									if(dayPreference.get("oTallowances")!=null){
										oTallowances=(String)dayPreference.get("oTallowances");
									}
							/*		if(dayPreference.get("extraOTallowances")!=null){
										extraOTallowances=(String)dayPreference.get("extraOTallowances");
									}	
									if(dayPreference.get("extraOTStartTime")!=null){
										extraOTStartTime=(String)dayPreference.get("extraOTStartTime");
									}
									if(dayPreference.get("extraOTEndTime")!=null){
										extraOTEndTime=(String)dayPreference.get("extraOTEndTime");
									}*/
									if(dayPreference.get("oTStartTime")!=null){
										oTStartTime=(String)dayPreference.get("oTStartTime");
									}
									if(dayPreference.get("oTEndTime")!=null){
										oTEndTime=(String)dayPreference.get("oTEndTime");
									}
									
								}
								
								if(oTallowances.equalsIgnoreCase("Y")){
									
										long entryTimeInDate=AttendanceUtils.getLateEntryTimeInDate(entryTime,new Date(stime));
										long maximumStayTimeInDate=AttendanceUtils.getLateEntryTimeInDate(maximumStayTime,new Date(stime));
										long startTime=AttendanceUtils.getStartTime(new Date(stime));
										
										List<EntityExpr> exprs = FastList.newInstance();
										List<String> orderBy = FastList.newInstance();
										orderBy.add("logtimeStamp ASC");
										exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(startTime)));
										exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(maximumStayTimeInDate)));
										exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
										
										
										try {
											//delegator.findList(entityName, entityCondition, fieldsToSelect, orderBy, findOptions, useCache)
											employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);
										} catch (GenericEntityException e) {
											e.printStackTrace();
										}
										
										if(employeeLogs.size()>1){
											
											GenericValue employeeLog=employeeLogs.get(employeeLogs.size()-1);
											
											Timestamp	time=(Timestamp)employeeLog.get("logtimeStamp");
											employee.put("ExitTime", simpleDateFormat.format(new Date(time.getTime())));
											
											GenericValue employeeLogEntrry=employeeLogs.get(0);
											Timestamp	entrytime=(Timestamp)employeeLogEntrry.get("logtimeStamp");
											employee.put("EntryTime", simpleDateFormat.format(new Date(entrytime.getTime())));
											
									/*		if(extraOTallowances.equalsIgnoreCase("Y")){
												
												long extraOTStartTimeInDate=AttendanceUtils.getLateEntryTimeInDate(extraOTStartTime,new Date(stime));
												long extraOTEndTimeInDate=AttendanceUtils.getLateEntryTimeInDate(extraOTEndTime,new Date(stime));
												if(AttendanceUtils.compareOTtime(extraOTStartTimeInDate,extraOTEndTimeInDate,time.getTime())){
													eotHours=(time.getTime()-extraOTStartTimeInDate)/3600000;
													eotMinutes=((time.getTime()-extraOTStartTimeInDate)%3600000)/60000;
													
												}else if(time.getTime()>extraOTEndTimeInDate){
													eotHours=(extraOTEndTimeInDate-extraOTStartTimeInDate)/3600000;
													eotMinutes=((extraOTEndTimeInDate-extraOTStartTimeInDate)%3600000)/60000;
												}
												
											}*/
										
											long oTStartTimeInDate=AttendanceUtils.getLateEntryTimeInDate(oTStartTime,new Date(stime));
											long oTEndTimeInDate=AttendanceUtils.getLateEntryTimeInDate(oTEndTime,new Date(stime));
											
											if(AttendanceUtils.compareOTtime(oTStartTimeInDate,oTEndTimeInDate,time.getTime())){
													otHours=(time.getTime()-oTStartTimeInDate)/3600000;
													otMinutes=((time.getTime()-oTStartTimeInDate)%3600000)/60000;
												}else if(time.getTime()>oTEndTimeInDate){
													otHours=(oTEndTimeInDate-oTStartTimeInDate)/3600000;
													otMinutes=((oTEndTimeInDate-oTStartTimeInDate)%3600000)/60000;
												}
												
									}
								}
								
							
							
							}
							if(otMinutes>=50)
								otHours=otHours+1;
							employee.put("OTHours", String.valueOf(otHours));
							//employee.put("eotHours", String.valueOf(eotHours)+":"+String.valueOf(eotMinutes));
						
							overtimeList.add(employee);	
							stime=stime+86400000;
				}
			
			
			}

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		result.put("overtimeList", overtimeList);
		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Employee Late List");
		return result;
	}
	
	public static Map<String, Object> findYearlylyAttendanceBonusReport(DispatchContext dctx,
			Map<String, ? extends Object> context) throws ParseException {
		
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy"); 
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
		
			Map<String, Object> result = ServiceUtil.returnSuccess();
			List<Map<String, String>> yearlyBonusList=FastList.newInstance();
		
			Delegator delegator = dctx.getDelegator();
			String years = (String) context.get("years");
			String orgPartyId = (String) context.get("orgPartyId");
			String entryTime="";
			String maximumStayTime="";
			String lateEntryTime="";
			
			GenericValue orgPreference=	AttendanceUtils.getOrgPreference(delegator,orgPartyId);
			if(orgPreference!=null){
				
				if(orgPreference.get("entryTime")!=null){
					 entryTime=(String)orgPreference.get("entryTime");
				}
				if(orgPreference.get("maximumStayTime")!=null){
					 maximumStayTime=(String)orgPreference.get("maximumStayTime");
				}
				if(orgPreference.get("lateEntryTime")!=null){
					lateEntryTime=(String)orgPreference.get("lateEntryTime");
				}
				 
			}
		
			if(years!=null&&!years.trim().isEmpty()){
				Map<String,Long> startAndEndOfYear=AttendanceUtils.getStartAndEndOfYear(years);
				
				List<GenericValue> partyRelationships=FastList.newInstance();
				List<Timestamp> holidays=new ArrayList<Timestamp>();
				holidays=AttendanceUtils.getHolidays(orgPartyId, new Timestamp(startAndEndOfYear.get("startyear")), new Timestamp(startAndEndOfYear.get("endyear")), delegator);
				try {
					partyRelationships = delegator.findByAnd(
							"PartyRelationship", UtilMisc.toMap("partyIdFrom",orgPartyId.trim()));
					
					for(GenericValue partyRelationship:partyRelationships){
						
						List<GenericValue> employeeLeaves=new ArrayList<GenericValue>();
						
						String employeeId=(String)partyRelationship.get("partyIdTo");
						
					    List<EntityExpr> exprsList = FastList.newInstance();
					    exprsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
					    exprsList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
							
					    try {
								employeeLeaves = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsList, EntityOperator.AND), null, null, null, false);
							} catch (GenericEntityException e) {
								e.printStackTrace();
							}
						
						long stime=startAndEndOfYear.get("startyear");
						long etime=startAndEndOfYear.get("endyear");
					
						int lateDates=0;
						int absenceDates=0;
						int intimeDates=0;
						int holiday=0;

						Map<String, String> employee=FastMap.newInstance();
						employee.put("employeeId", employeeId);
				
						 GenericValue person = delegator.findByPrimaryKey(
									"Person", UtilMisc.toMap("partyId",employeeId.trim()));

						if (!UtilValidate.isEmpty(person)){
							String name=AttendanceUtils.avoidNullPointerException((String)person.get("firstName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("middleName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("lastName"));
							employee.put("employeeName", name);
						}
						
					
				
						GenericValue empOrRostPreference=	AttendanceUtils.getEmpORRosterPreference(delegator,orgPartyId, employeeId);
						
						if(empOrRostPreference!=null){
							if(empOrRostPreference.get("entryTime")!=null){
								 entryTime=(String)empOrRostPreference.get("entryTime");
							}
							if(empOrRostPreference.get("maximumStayTime")!=null){
								 maximumStayTime=(String)empOrRostPreference.get("maximumStayTime");
							}
							if(empOrRostPreference.get("lateEntryTime")!=null){
								lateEntryTime=(String)empOrRostPreference.get("lateEntryTime");
							}
							
						}
						
					
						while(stime<=etime){
							
							List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
							
							if(!AttendanceUtils.checkHoliday(holidays, new Timestamp(stime))&&!AttendanceUtils.checkEmpLeave(employeeLeaves,stime)){
								
								GenericValue dayPreference=	AttendanceUtils.getDayPreference(delegator, new Date(stime), orgPartyId);
								
								if(dayPreference!=null){
									if(dayPreference.get("entryTime")!=null){
										 entryTime=(String)dayPreference.get("entryTime");
									}
									if(dayPreference.get("maximumStayTime")!=null){
										 maximumStayTime=(String)dayPreference.get("maximumStayTime");
									}
									if(dayPreference.get("lateEntryTime")!=null){
										lateEntryTime=(String)dayPreference.get("lateEntryTime");
									}
									
								}
								
								
								long entryTimeInDate=AttendanceUtils.getLateEntryTimeInDate(entryTime,new Date(stime));
								long startTime=AttendanceUtils.getStartTime(new Date(stime));
								long maximumStayTimeInDate=AttendanceUtils.getLateEntryTimeInDate(maximumStayTime,new Date(stime));
								
								List<EntityExpr> exprs = FastList.newInstance();
								List<String> orderBy = FastList.newInstance();
								orderBy.add("logtimeStamp ASC");
								exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(startTime)));
								exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(maximumStayTimeInDate)));
								exprs.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, employeeId));
								
								try {
									//delegator.findList(entityName, entityCondition, fieldsToSelect, orderBy, findOptions, useCache)
									employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);
								} catch (GenericEntityException e) {
									e.printStackTrace();
								}
							
								
								if(employeeLogs.size()>0){
									
									long lateEntryTimeInDate=AttendanceUtils.getLateEntryTimeInDate(lateEntryTime,new Date(stime));
									GenericValue employeeLog=employeeLogs.get(0);
									Timestamp	time=(Timestamp)employeeLog.get("logtimeStamp");
									if(!AttendanceUtils.compareLateLogtime(lateEntryTimeInDate,time.getTime())){
							
										lateDates++;
									
									}else{
										intimeDates++;
									}
									
								}else{
									absenceDates++;
								}
						
							}else{
								holiday++;
							}
			
							stime=stime+86400000;
						}
						
						String bonus="0";
						String attendanceBonusAllowances=AttendanceUtils.getAttendanceBonusAllowances(employeeId,delegator,orgPartyId);
						
					if(attendanceBonusAllowances.equals("Y")){						
				           String lateEntryDaysForAttenBonusDisable=AttendanceUtils.getyearlyLateEntryDaysForAttenBonusDisable(employeeId,delegator,orgPartyId);
					       String lateEntryDaysForAttenBonusDeduction=AttendanceUtils.getyearlylateEntryDaysForAttenBonusDeduction(employeeId,delegator,orgPartyId);
					  	   String yearlyBonusAmount=AttendanceUtils.getyearlyBonusAmount(employeeId,delegator,orgPartyId);
					  	   String bonusDeductionAmount=AttendanceUtils.getbonusDeductionAmount(employeeId,delegator,orgPartyId);
					  	
					  	try{
					  		int daysForBonusDisable  = Integer.parseInt(lateEntryDaysForAttenBonusDisable);
					  	  	int daysForBonusDeduction = Integer.parseInt(lateEntryDaysForAttenBonusDeduction);
					  	  	int bonusAmount = Integer.parseInt(yearlyBonusAmount);
					  	  	int deductionAmount = Integer.parseInt(bonusDeductionAmount);
					  	  	  		  
					  		  if((lateDates+absenceDates)<(daysForBonusDeduction)){
					  	    	  bonus=yearlyBonusAmount; 
					  	      }
					  	      else if((lateDates+absenceDates)<(daysForBonusDisable)){
					  	    	  bonus=Integer.toString(bonusAmount-deductionAmount);
					  	      }else{
					  	    	  bonus="0";  
					  	      }
					  	}catch(Exception e){

							result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
							result.put(ModelService.ERROR_MESSAGE, "At least One Preference Settings needed");
					  	}
						
					}else{
							  bonus="0";  
						  }
						
						employee.put("lateDates", String.valueOf(lateDates));
						employee.put("absenceDates",  String.valueOf(absenceDates));
						employee.put("intimeDates",  String.valueOf(intimeDates));
						employee.put("holiday",  String.valueOf(holiday));
						employee.put("yearlyBonus", bonus);
						yearlyBonusList.add(employee);	
					}
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
							
				
			}
			
			result.put("yearlyBonusList", yearlyBonusList);
			result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
			result.put(ModelService.SUCCESS_MESSAGE, "Employee Yearly Attendance Bonus List");
			return result;
	}
	
	
	public static String exportDailyAbsenceToExcel(HttpServletRequest request ,
			HttpServletResponse response) {
	
      	List<Map<String, String>> absenceList = (List<Map<String, String>>) request.getSession().getAttribute("absenceList");
 
		if(absenceList!=null&&!absenceList.isEmpty()){
				long currentTime=System.currentTimeMillis();
		      
		        String fileName = "DailyAbsenceReport_"
						+ String.valueOf(currentTime) + ".xls";
				
				// create path for excel file
				String excelFilePath = UtilCommon.getAbsoluteFilePath(request, fileName);
			  
				// prepare for excel
				List<String> columnHeaders = new ArrayList<String>();
				
			    columnHeaders.add(0, "EmployeeId");
			    columnHeaders.add(1, "EmployeeName");	    
			    columnHeaders.add(2, "Date");
		        List<List<String>> data = EmployeeExcelExportService.prepareExcelDataForEmployeeAbsenceReport(absenceList,columnHeaders);

				try {
					ExcelUtils.saveToExcel(excelFilePath, "employeeAbsenceList", data);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return ExcelUtils.downloadExcel(fileName, request, response);
		}else{
			
			return "Data Empty";
		}
	}
	
	public static String exportDailyAbsenceToExcel1(HttpServletRequest request ,
			HttpServletResponse response) {
	
      	List<Map<String, String>> absenceList = (List<Map<String, String>>) request.getSession().getAttribute("absenceList");
 
		if(absenceList!=null&&!absenceList.isEmpty()){
				long currentTime=System.currentTimeMillis();
		      
		        String fileName = "DailyAbsenceReport_"
						+ String.valueOf(currentTime) + ".xls";
				
				// create path for excel file
				String excelFilePath = UtilCommon.getAbsoluteFilePath(request, fileName);
			  
				// prepare for excel
				List<String> columnHeaders = new ArrayList<String>();
			
			    columnHeaders.add(0, "SL");
			    columnHeaders.add(1, "EmployeeId");
			    columnHeaders.add(2, "EmployeeName");	    
			    columnHeaders.add(3, "Card");
			    columnHeaders.add(4, "Designation");
			    columnHeaders.add(5, "Department");
			    columnHeaders.add(6, "TotalAbsence");
			    columnHeaders.add(7, "ContinuesAbsence");
			    
			    
		        List<List<String>> data = EmployeeExcelExportService.prepareExcelDataForEmployeeAbsenceReport(absenceList,columnHeaders);

				try {
					ExcelUtils.saveToExcel(excelFilePath, "employeeAbsenceList", data);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return ExcelUtils.downloadExcel(fileName, request, response);
		}else{
			
			return "Data Empty";
		}
	}
	
	
	public static String exportDailyLateToExcel(HttpServletRequest request ,
			HttpServletResponse response) {
	
      	List<Map<String, String>> lateList = (List<Map<String, String>>) request.getSession().getAttribute("lateList");
 
		if(lateList!=null&&!lateList.isEmpty()){
				long currentTime=System.currentTimeMillis();
		        String fileName = "DailyLateReport_"
						+ String.valueOf(currentTime) + ".xls";
				
				// create path for excel file
				String excelFilePath = UtilCommon.getAbsoluteFilePath(request, fileName);
			  
				// prepare for excel
				List<String> columnHeaders = new ArrayList<String>();
				
				columnHeaders.add(0, "SL");
			    columnHeaders.add(1, "EmployeeId");	    
			    columnHeaders.add(2, "Card");
			    columnHeaders.add(3, "EmployeeName"); 
			    columnHeaders.add(4, "Designation");
			    columnHeaders.add(5, "Department");
			    columnHeaders.add(6, "Process");
			    columnHeaders.add(7, "EntryTime");
			
		        List<List<String>> data = EmployeeExcelExportService.prepareExcelDataForEmployeeAbsenceReport(lateList,columnHeaders);
		   
				try {
					
					ExcelUtils.saveToExcel(excelFilePath, "employeeLateList", data);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return ExcelUtils.downloadExcel(fileName, request, response);
		}else{
			
			return "Data Empty";
		}
	}
	

	public static String exportOTReportToExcel(HttpServletRequest request ,HttpServletResponse response) {
	
      	List<Map<String, String>> overtimeList = (List<Map<String, String>>) request.getSession().getAttribute("overtimeList");
 
		if(overtimeList!=null&&!overtimeList.isEmpty()){
				long currentTime=System.currentTimeMillis();
		        String fileName = "EmployeeOTReport_"
						+ String.valueOf(currentTime) + ".xls";
				
				// create path for excel file
				String excelFilePath = UtilCommon.getAbsoluteFilePath(request, fileName);
			  
				// prepare for excel
				List<String> columnHeaders = new ArrayList<String>();
				
			    columnHeaders.add(0, "EmployeeId");
			    columnHeaders.add(1, "EmployeeName");	    
			    columnHeaders.add(2, "Date");
			    columnHeaders.add(3, "EntryTime");
			    columnHeaders.add(4, "ExitTime");
			    columnHeaders.add(5, "OTHours");
			    //columnHeaders.add(6, "eotHours");
			 
		        List<List<String>> data = EmployeeExcelExportService.prepareExcelDataForEmployeeAbsenceReport(overtimeList,columnHeaders);
				
				try {
					ExcelUtils.saveToExcel(excelFilePath, "employeeOTList", data);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return ExcelUtils.downloadExcel(fileName, request, response);
		}else{
			
			return "Data Empty";
		}
	}


	public static String exportYABReportToExcel(HttpServletRequest request ,HttpServletResponse response) {
		
      	List<Map<String, String>> yearlyBonusList = (List<Map<String, String>>) request.getSession().getAttribute("yearlyBonusList");
 
		if(yearlyBonusList!=null&&!yearlyBonusList.isEmpty()){
				long currentTime=System.currentTimeMillis();
		        String fileName = "EmployeeYABReport_"
						+ String.valueOf(currentTime) + ".xls";
				
				// create path for excel file
				String excelFilePath = UtilCommon.getAbsoluteFilePath(request, fileName);
			  
				// prepare for excel
				List<String> columnHeaders = new ArrayList<String>();
				
			    columnHeaders.add(0, "employeeId");
			    columnHeaders.add(1, "employeeName");	    
			    columnHeaders.add(2, "lateDates");
			    columnHeaders.add(3, "absenceDates");
			    columnHeaders.add(4, "intimeDates");
			    columnHeaders.add(5, "holiday");
			    columnHeaders.add(6, "yearlyBonus");
			 
			 
		        List<List<String>> data = EmployeeExcelExportService.prepareExcelDataForEmployeeAbsenceReport(yearlyBonusList,columnHeaders);
				
				try {
					ExcelUtils.saveToExcel(excelFilePath, "employeeYABList", data);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return ExcelUtils.downloadExcel(fileName, request, response);
		}else{
			
			return "Data Empty";
		}
	}
	
	public static Map<String, Object>	getYearList(DispatchContext dctx,
			Map<String, ? extends Object> context){
		List<Map<String,String>> yearList=new ArrayList<Map<String,String>>();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		java.util.Date currentTime=new java.util.Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		String currentyear=formatter.format(currentTime);
		
		int year=Integer.parseInt(currentyear);
		int syear=year-20;
		int eyear=year+20;
		
		while(syear<=eyear){
			Map<String,String> yearvalue=new HashMap<String,String>();
			yearvalue.put("year", String.valueOf(syear));
			yearList.add(yearvalue);
			syear++;
		}
		
			result.put("yearList", yearList);
			result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
			result.put(ModelService.SUCCESS_MESSAGE, "Employee Attendance Bonus List");
			return result;
	}

	
	public static String exportMonthlyBonusToExcel(HttpServletRequest request ,
			HttpServletResponse response) {
	
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
      	
		List<Map<String, String>> employeeBonusList = (List<Map<String, String>>) request.getSession().getAttribute("employeeBonusList");
		
		if(employeeBonusList!=null&&!employeeBonusList.isEmpty()){
  
				List<List<String>> data = EmployeeExcelExportService.prepareExcelDataForMonthlyBonus(delegator,employeeBonusList);
				// create file Name with random number
				String fileName = "MonthlyBonusReport_"
						+ String.valueOf((int) (Math.random() * 100000)) + ".xls";
				
				// create path for excel file
				
				String excelFilePath = UtilCommon
						.getAbsoluteFilePath(request, fileName);
				
				try {
					
					ExcelUtils.saveToExcel(excelFilePath, "employeeBonusList", data);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return ExcelUtils.downloadExcel(fileName, request, response);

		}else{
			
			
			return "Data Empty";
		}
}
	
	
	public static Map<String, Object> findMonthlyAttendanceBonusReport(DispatchContext dctx,
			Map<String, ? extends Object> context) throws ParseException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<Map<String, String>> employeeBonusList=FastList.newInstance();

		String selectMonthYear = (String) context.get("selectMonthYear");
		String orgPartyId = (String) context.get("orgPartyId");
		
		java.sql.Date monthYearInDate=null;		
		Delegator delegator = dctx.getDelegator();
		List<GenericValue> partyRelationships=FastList.newInstance();
		
		try {
			
			if(selectMonthYear!=null&&!selectMonthYear.isEmpty()){
				
				//First date of month
				monthYearInDate=AttendanceUtils.getsqltDate(selectMonthYear);
				/*partyRelationships = delegator.findByAnd(
					"PartyRelationship", UtilMisc.toMap("partyIdFrom",orgPartyId.trim()));*/
				partyRelationships = AttendanceUtils.getTotalEmployee(delegator, partyRelationships, orgPartyId);
				
				//Find last date of month from calendar	
				Calendar cal = Calendar.getInstance();
	            cal.setTime(monthYearInDate);
	            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));        
	            java.sql.Date monthLastDate=  new java.sql.Date(cal.getTime().getTime());
	            
	           //Get holidays of selected month
	        	List<Timestamp> holidays=new ArrayList<Timestamp>();
				holidays=	AttendanceUtils.getHolidays(orgPartyId, new Timestamp(monthYearInDate.getTime()), new Timestamp(monthLastDate.getTime()), delegator);
				
				//Get employeeId from partyRelationship & find 1month logs for each employee from EmployLog table
			   for(GenericValue partyRelationship:partyRelationships){
					
				 String employeeId=(String)partyRelationship.get("partyId");
				 String employeeFullName=" ";
				 				 
				 GenericValue employeeName = delegator.findByPrimaryKey(
							"Person", UtilMisc.toMap("partyId",employeeId.trim()));
					 if(employeeName!=null){
					  employeeFullName=AttendanceUtils.avoidNullPointerException((String)employeeName.get("firstName"))+AttendanceUtils.avoidNullPointerException((String)employeeName.get("middleName"))+AttendanceUtils.avoidNullPointerException((String)employeeName.get("lastName"));
					 }	
					 
				 Map<String, String> employee=	FastMap.newInstance();
				 int totalAbsence =0;
				 int totalholidays=0;

				 List<Long>monthlogs=FastList.newInstance();				
				 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					
		         Calendar start = Calendar.getInstance();
		         start.setTime(monthYearInDate);
		         Calendar end = Calendar.getInstance();
		         end.setTime(monthLastDate);

					// find log from month 1st date to last date
				for (java.util.Date date =  start.getTime(); !start.after(end); start.add(Calendar.DATE, 1), date =  start.getTime()) 
			      {
			           Calendar cal1 = Calendar.getInstance();
			           cal1.setTime(date);
			           if(holidays.contains(cal1.getTime())){
			        	   totalholidays++;
			           continue;
			       }	          

				   List<EntityCondition> exprsAnd = FastList.newInstance();
				   Set<String> fieldsToSelect = UtilMisc.toSet("logtimeStamp");
				   List<String> orderBy = FastList.newInstance();
				   orderBy.add("logtimeStamp ASC");
				   String searchDate=dateFormat.format(cal1.getTime());
				    
		           exprsAnd.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
		           exprsAnd.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("logtimeStamp"), EntityOperator.LIKE, EntityFunction.UPPER(searchDate + "%")));
		            
		           List<GenericValue> empLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprsAnd, EntityOperator.AND), fieldsToSelect,orderBy , null, true);
		           if(!empLogs.isEmpty())
		           {
		            	monthlogs.add(empLogs.get(0).getTimestamp("logtimeStamp").getTime());      	
		           }else{
		            	totalAbsence++;
		            }   
		            
				}
				//Get selected month employee bonus from total absence and late entry 	 					  
				 List<String> employeeBonus= AttendanceUtils.getEmployeeBonus(monthlogs,totalAbsence,employeeId,orgPartyId,delegator,result);
				 employee.put("employeeId", employeeId);
				 employee.put("employeeName", employeeFullName);
				 employee.put("monthlyBonus", employeeBonus.get(0));
				 employee.put("lateDates", employeeBonus.get(1));
				 employee.put("intimeDates", employeeBonus.get(2));
				 employee.put("absenceDates", Integer.toString(totalAbsence));
				 employee.put("holidays", Integer.toString(totalholidays));
                 employeeBonusList.add(employee);
			}

			}
			} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    result.put("employeeBonusList", employeeBonusList);
		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Employee Attendance Bonus List");

		return result;
	}
	
	public static double getOverTimeAmount(DispatchContext dctx,String fromDate,String toDate,String orgPartyId,String employeeId,double basicSalary){
		double retValue=0.0;
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy"); 
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
		Delegator delegator = dctx.getDelegator();
		String entryTime="";
		String maximumStayTime="";
		String oTallowances="";
		String extraOTallowances="";
		String extraOTStartTime="";
		String extraOTEndTime="";
		String oTStartTime="";
		String oTEndTime="";
		
		GenericValue orgPreference=	AttendanceUtils.getOrgPreference(delegator,orgPartyId);
		if(orgPreference!=null){
			
			if(orgPreference.get("entryTime")!=null){
				 entryTime=(String)orgPreference.get("entryTime");
			}
			if(orgPreference.get("maximumStayTime")!=null){
				 maximumStayTime=(String)orgPreference.get("maximumStayTime");
			}
			if(orgPreference.get("oTallowances")!=null){
				oTallowances=(String)orgPreference.get("oTallowances");
			}
			if(orgPreference.get("extraOTallowances")!=null){
				extraOTallowances=(String)orgPreference.get("extraOTallowances");
			}
			if(orgPreference.get("extraOTStartTime")!=null){
				extraOTStartTime=(String)orgPreference.get("extraOTStartTime");
			}
			if(orgPreference.get("extraOTEndTime")!=null){
				extraOTEndTime=(String)orgPreference.get("extraOTEndTime");
			}
			if(orgPreference.get("oTStartTime")!=null){
				oTStartTime=(String)orgPreference.get("oTStartTime");
			}
			if(orgPreference.get("oTEndTime")!=null){
				oTEndTime=(String)orgPreference.get("oTEndTime");
			}
			 
		}
		java.sql.Date fromTime=null;
		java.sql.Date toTime=null;
		
	
		//List<GenericValue> partyRelationships=FastList.newInstance();
		List<Map<String, String>> overtimeList=FastList.newInstance();
		List<Timestamp> holidays=new ArrayList<Timestamp>();
		try {
			if(fromDate!=null&&!fromDate.isEmpty()&&toDate!=null&&!toDate.isEmpty()){
			fromTime=Date.valueOf(fromDate);
			toTime=Date.valueOf(toDate);
/*				partyRelationships = delegator.findByAnd(
					"PartyRelationship", UtilMisc.toMap("partyIdFrom",orgPartyId.trim()));*/
				holidays=AttendanceUtils.getHolidays(orgPartyId, new Timestamp(fromTime.getTime()), new Timestamp(toTime.getTime()), delegator);
			}
						
		
			//for(GenericValue partyRelationship:partyRelationships){
				
				List<GenericValue> employeeLeaves=new ArrayList<GenericValue>();
				
				
				GenericValue empOrRostPreference=	AttendanceUtils.getEmpORRosterPreference(delegator,orgPartyId, employeeId);
				
				if(empOrRostPreference!=null){
					if(empOrRostPreference.get("entryTime")!=null){
						 entryTime=(String)empOrRostPreference.get("entryTime");
					}
					if(empOrRostPreference.get("maximumStayTime")!=null){
						 maximumStayTime=(String)empOrRostPreference.get("maximumStayTime");
					}
					if(empOrRostPreference.get("oTallowances")!=null){
						oTallowances=(String)empOrRostPreference.get("oTallowances");
					}
					if(empOrRostPreference.get("extraOTallowances")!=null){
						extraOTallowances=(String)empOrRostPreference.get("extraOTallowances");
					}
					if(empOrRostPreference.get("extraOTStartTime")!=null){
						extraOTStartTime=(String)empOrRostPreference.get("extraOTStartTime");
					}
					if(empOrRostPreference.get("extraOTEndTime")!=null){
						extraOTEndTime=(String)empOrRostPreference.get("extraOTEndTime");
					}
					if(empOrRostPreference.get("oTStartTime")!=null){
						oTStartTime=(String)empOrRostPreference.get("oTStartTime");
					}
					if(empOrRostPreference.get("oTEndTime")!=null){
						oTEndTime=(String)empOrRostPreference.get("oTEndTime");
					}
					
				}
				
			    List<EntityExpr> exprsList = FastList.newInstance();
			    exprsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
			    exprsList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
					
			    try {
						employeeLeaves = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsList, EntityOperator.AND), null, null, null, false);
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
				
				long stime=AttendanceUtils.getStartTime(fromTime);
				long etime=AttendanceUtils.getEndTime(toTime);

				
				while(stime<=etime){
					
							long otHours=0;
							long otMinutes=0;
							long eotHours=0;
							long eotMinutes=0;
							
							List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
							//Map<String, String> employee=FastMap.newInstance();
							//employee.put("employeeId", employeeId);
							List<GenericValue> PartyAndPersonAndUserLoginAndEmployments = delegator.findByAnd("PartyAndPersonAndUserLoginAndEmployment",
									UtilMisc.toMap("userLoginId", employeeId));

/*							GenericValue partyAndPersonAndUserLoginAndEmployment = EntityUtil
									.getFirst(PartyAndPersonAndUserLoginAndEmployments);
							if (!UtilValidate.isEmpty(partyAndPersonAndUserLoginAndEmployment)){
								String name=AttendanceUtils.avoidNullPointerException((String)partyAndPersonAndUserLoginAndEmployment.get("firstName"))+" "+AttendanceUtils.avoidNullPointerException((String)partyAndPersonAndUserLoginAndEmployment.get("middleName"))+" "+AttendanceUtils.avoidNullPointerException((String)partyAndPersonAndUserLoginAndEmployment.get("lastName"));
								employee.put("employeeName", name);
								employee.put("day", df.format(new Date(stime)));
							}*/
							if(!AttendanceUtils.checkHoliday(holidays, new Timestamp(stime))&&!AttendanceUtils.checkEmpLeave(employeeLeaves,stime)){
								
								GenericValue dayPreference=	AttendanceUtils.getDayPreference(delegator, new Date(stime), orgPartyId);
								
								if(dayPreference!=null){
									if(dayPreference.get("entryTime")!=null){
										 entryTime=(String)dayPreference.get("entryTime");
									}
									if(dayPreference.get("maximumStayTime")!=null){
										 maximumStayTime=(String)dayPreference.get("maximumStayTime");
									}
									if(dayPreference.get("oTallowances")!=null){
										oTallowances=(String)dayPreference.get("oTallowances");
									}
									if(dayPreference.get("extraOTallowances")!=null){
										extraOTallowances=(String)dayPreference.get("extraOTallowances");
									}	
									if(dayPreference.get("extraOTStartTime")!=null){
										extraOTStartTime=(String)dayPreference.get("extraOTStartTime");
									}
									if(dayPreference.get("extraOTEndTime")!=null){
										extraOTEndTime=(String)dayPreference.get("extraOTEndTime");
									}
									if(dayPreference.get("oTStartTime")!=null){
										oTStartTime=(String)dayPreference.get("oTStartTime");
									}
									if(dayPreference.get("oTEndTime")!=null){
										oTEndTime=(String)dayPreference.get("oTEndTime");
									}
									
								}
								
								if(oTallowances.equalsIgnoreCase("Y")){
									
									long entryTimeInDate=AttendanceUtils.getLateEntryTimeInDate(entryTime,new Date(stime));
									long maximumStayTimeInDate=AttendanceUtils.getLateEntryTimeInDate(maximumStayTime,new Date(stime));
										
										
										List<EntityExpr> exprs = FastList.newInstance();
										List<String> orderBy = FastList.newInstance();
										orderBy.add("logtimeStamp ASC");
										exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(entryTimeInDate)));
										exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(maximumStayTimeInDate)));
										exprs.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, employeeId));
										
										
										try {
											//delegator.findList(entityName, entityCondition, fieldsToSelect, orderBy, findOptions, useCache)
											employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);
										} catch (GenericEntityException e) {
											e.printStackTrace();
										}
										if(employeeLogs.size()>1){
											
											GenericValue employeeLog=employeeLogs.get(employeeLogs.size()-1);
											
											Timestamp	time=(Timestamp)employeeLog.get("logtimeStamp");
											//employee.put("exitTime", simpleDateFormat.format(new Date(time.getTime())));
											
											GenericValue employeeLogEntrry=employeeLogs.get(0);
											Timestamp	entrytime=(Timestamp)employeeLogEntrry.get("logtimeStamp");
											//employee.put("entryTime", simpleDateFormat.format(new Date(entrytime.getTime())));
											
											if(extraOTallowances.equalsIgnoreCase("Y")){
												
												long extraOTStartTimeInDate=AttendanceUtils.getLateEntryTimeInDate(extraOTStartTime,new Date(stime));
												long extraOTEndTimeInDate=AttendanceUtils.getLateEntryTimeInDate(extraOTEndTime,new Date(stime));
												if(AttendanceUtils.compareOTtime(extraOTStartTimeInDate,extraOTEndTimeInDate,time.getTime())){
													eotHours=(time.getTime()-extraOTStartTimeInDate)/3600000;
													eotMinutes=((time.getTime()-extraOTStartTimeInDate)%3600000)/60000;
													
												}else if(time.getTime()>extraOTEndTimeInDate){
													eotHours=(extraOTEndTimeInDate-extraOTStartTimeInDate)/3600000;
													eotMinutes=((extraOTEndTimeInDate-extraOTStartTimeInDate)%3600000)/60000;
												}
												
											}
										
											long oTStartTimeInDate=AttendanceUtils.getLateEntryTimeInDate(oTStartTime,new Date(stime));
											long oTEndTimeInDate=AttendanceUtils.getLateEntryTimeInDate(oTEndTime,new Date(stime));
											
											if(AttendanceUtils.compareOTtime(oTStartTimeInDate,oTEndTimeInDate,time.getTime())){
													otHours=(time.getTime()-oTStartTimeInDate)/3600000;
													otMinutes=((time.getTime()-oTStartTimeInDate)%3600000)/60000;
												}else if(time.getTime()>oTEndTimeInDate){
													otHours=(oTEndTimeInDate-oTStartTimeInDate)/3600000;
													otMinutes=((oTEndTimeInDate-oTStartTimeInDate)%3600000)/60000;
												}
												
									}
								}
								
							
							
							}
							//employee.put("otHours", String.valueOf(otHours)+":"+String.valueOf(otMinutes));
							float otHour=otHours+(otMinutes/60);
							//employee.put("eotHours", String.valueOf(eotHours)+":"+String.valueOf(eotMinutes));
							float eotHour=eotHours+(eotMinutes/60);
							retValue=retValue+((otHour+eotHour)*(basicSalary/104));
							//overtimeList.add(employee);	
							stime=stime+86400000;
				}
			
			
			//}

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retValue;
	}
	
	public static List<GenericValue> getTotalEmployee(Delegator delegator,List<GenericValue> totalEmployee,String partyId){
		List<GenericValue> children=getChildren(delegator, partyId);
		
		List<GenericValue> positions=getPositions(delegator, partyId);
		
		for(GenericValue child:children){
	
			totalEmployee=getTotalEmployee(delegator,totalEmployee,child.getString("partyIdTo"));
		}
		
		if(positions!=null){
			for(GenericValue position:positions){
				totalEmployee.addAll(getTotalEmployeeByPosition(delegator, position.getString("emplPositionId")));
			}
		}
		return totalEmployee;
		
	}
	

	public static 	List<GenericValue> getChildren(Delegator delegator, String partyId){
		
		List<GenericValue> children=FastList.newInstance();
		
		Set<String> fieldsToSelect = FastSet.newInstance();
		fieldsToSelect.add("partyIdTo");
		   	
		try {
				List<EntityExpr> exprs = FastList.newInstance();
		        exprs.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
		        exprs.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP"));
		        children = delegator.findList("PartyRelationship", EntityCondition.makeCondition(exprs, EntityOperator.AND), fieldsToSelect, null, null, false);
		   
		
			}catch (GenericEntityException e) {
				return children;
			}
		
		return children;
	}
	public static 	List<GenericValue> getPositions(Delegator delegator, String partyId){
		
		List<GenericValue> positions=FastList.newInstance();
		Set<String> fieldsToSelect = FastSet.newInstance();
		fieldsToSelect.add("emplPositionId");
		
		try {
			positions= delegator.findList("EmplPositionAndEmplPositionType", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),fieldsToSelect,null,null,false);
		}catch (GenericEntityException e) {
			return positions;
		}
		return positions;
	}
	public static 	List<GenericValue> getTotalEmployeeByPosition(Delegator delegator, String positionId){
		List<GenericValue> emplPositionFulfillmentList=FastList.newInstance();
		Set<String> fieldsToSelect = FastSet.newInstance();
		fieldsToSelect.add("partyId");
		
		try {
			 emplPositionFulfillmentList = delegator.findList("EmplPositionFulfillment", EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS, positionId), fieldsToSelect, null, null, false);			
			return emplPositionFulfillmentList;

		} catch (GenericEntityException e) {
			return null;
		}
    }
	
public static int getThisMonthTotalAbsencByPartyId(Delegator delegator,String partyId,Timestamp dayEnd,TimeZone timeZone,Locale locale,String orgPartyId) throws GenericEntityException{
		
		Timestamp thisMonthStart=AttendanceUtils.getModifiedMonthStart(dayEnd,0);
		List<Map<String,Object>> holidays=FastList.newInstance();
		holidays=AttendanceUtils.getHolidaysWithType(orgPartyId, new Timestamp(thisMonthStart.getTime()), new Timestamp(dayEnd.getTime()), delegator);		
		List<GenericValue> employeeLeaves=new ArrayList<GenericValue>();
		
		//getting join date...
		List<GenericValue> employment = delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo",partyId));
		java.sql.Timestamp joinTime = null; 
		long joinTimeInLong = 0;
		if(UtilValidate.isNotEmpty(employment)){
			joinTime = employment.get(0).getTimestamp("fromDate");
			joinTimeInLong = joinTime.getTime();
		}
		
		int totalAbsence=0;
	    while(thisMonthStart.getTime()<=dayEnd.getTime()){
	    	Timestamp dayStart=AttendanceUtils.getModifiedDayStart(thisMonthStart);
	    	Timestamp dayEnd1=AttendanceUtils.getModifiedDayEnd(thisMonthStart);
	    	long dayEndTimeInLong = dayEnd1.getTime();
	    	
	    	// compare with joining date because (don't show absent if is not employee on that day)
			if(dayEndTimeInLong>=joinTimeInLong){
		    	Object checkHoliday=AttendanceUtils.checkHolidayFromHolidayMap(holidays,dayStart);	
				if(checkHoliday==null){
				    List<EntityExpr> exprsList = FastList.newInstance();
				    exprsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				    exprsList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
				    try{
				    	employeeLeaves = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsList, EntityOperator.AND), null, null, null, false);
				    }catch(Exception ex){
				    	logger.debug("Exception to et leave in getThisMonthTotalAbsencByPartyId function--->"+ex);
				    }
					boolean checkLeaveStatus=AttendanceUtils.checkEmpLeave(employeeLeaves,dayStart.getTime());	
					if(!checkLeaveStatus){	
						List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
						List<EntityExpr> exprs = FastList.newInstance();
						List<String> orderBy = FastList.newInstance();
						orderBy.add("logtimeStamp ASC");
						exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
						exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd1));
						exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId ));
						
						try {
							//delegator.findList(entityName, entityCondition, fieldsToSelect, orderBy, findOptions, useCache)
							employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);
						
							if(employeeLogs.size()<=0){
								totalAbsence++;
							}
							
						} catch (GenericEntityException e) {
							e.printStackTrace();
						}			
					}
				}
	    	}
			thisMonthStart=UtilDateTime.addDaysToTimestamp(thisMonthStart, 1);
	    }
		return totalAbsence;
	}
	
	
	public static int getContinuesAbesenceDays(Delegator delegator,String partyId,Timestamp dayEnd,TimeZone timeZone,Locale locale,String orgPartyId){

		
		Timestamp thisMonthStart=AttendanceUtils.getModifiedMonthStart(dayEnd,0);
		List<Map<String,Object>> holidays=FastList.newInstance();
		holidays=AttendanceUtils.getHolidaysWithType(orgPartyId, new Timestamp(thisMonthStart.getTime()), new Timestamp(dayEnd.getTime()), delegator);		
		List<GenericValue> employeeLeaves=new ArrayList<GenericValue>();
		
		
		int totalAbsence=0;
		
		boolean flag=true;
		
	    while(flag && dayEnd.getTime()>=thisMonthStart.getTime()){
	    	Timestamp dayStart=AttendanceUtils.getModifiedDayStart(dayEnd);
			Timestamp todayEnd=AttendanceUtils.getModifiedDayEnd(dayEnd);
			
			Object checkHoliday=AttendanceUtils.checkHolidayFromHolidayMap(holidays,dayStart);	
			if(checkHoliday==null){
			    List<EntityExpr> exprsList = FastList.newInstance();
			    exprsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			    exprsList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
			    try{
			    	employeeLeaves = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsList, EntityOperator.AND), null, null, null, false);
			    }catch(Exception ex){
			    	logger.debug("Exception to et leave in getThisMonthTotalAbsencByPartyId function--->"+ex);
			    }
			boolean checkLeaveStatus=AttendanceUtils.checkEmpLeave(employeeLeaves,dayStart.getTime());	
			
			if(!checkLeaveStatus){
			List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
			List<EntityExpr> exprs = FastList.newInstance();
			List<String> orderBy = FastList.newInstance();
			orderBy.add("logtimeStamp ASC");
			exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO, dayStart));
			exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
			exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId ));
			
			try {
				//delegator.findList(entityName, entityCondition, fieldsToSelect, orderBy, findOptions, useCache)
				employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);
			
				if(employeeLogs.size()<=0){
					totalAbsence++;
				}else{
					flag=false;
				}
				
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			
			
			}else{
				flag=false;
			}
	    }
			dayEnd=UtilDateTime.addDaysToTimestamp(dayEnd, -1);	
	    }
		return totalAbsence;
	
	}


	//Job Card
	public static Map<String, Object> findJobCard(DispatchContext dctx,
			Map<String, ? extends Object> context){
		double summeryWorkDay=0;
		double summeryAbsentDay=0;
		double summeryLeaveDay=0;
		int summeryHoliDay=0;
		int summeryWeeklyDay=0;
		int summeryFestivalDay=0;
		int summeryOtherHoliDay=0;
		int summeryTotalHalfDay=0;
		int summeryTotalOtInHour=0;
		Map<String, Object> result = ServiceUtil.returnSuccess();
		/*String selectMonthYear = (String) context.get("selectMonthYear");*/
		String fromDate = (String) context.get("fromDate");
		String toDate = (String) context.get("toDate");
		String orgPartyId = (String) context.get("orgPartyId");
		String employeeId = (String) context.get("employeeId"); 
		java.sql.Date fromTime=null;
		java.sql.Date toTime=null;
	/*	java.sql.Date monthYearInDate=null;*/
		List<Map> employeeLogList=FastList.newInstance();
		List<Map> summeryReport=FastList.newInstance();
		Map<String,Object> employmentInfo =FastMap.newInstance();
		Map<String, String> summeryReportMap=	FastMap.newInstance();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy"); 
		DateFormat dfWithDash = new SimpleDateFormat("yyyy-MM-dd"); 
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm ");
		Delegator delegator = dctx.getDelegator();
		if(fromDate!=null&&!fromDate.isEmpty()&&toDate!=null&&!toDate.isEmpty()){
		fromTime=Date.valueOf(fromDate);
		toTime=Date.valueOf(toDate);	
		String clintPartyId=null;
		String clintStartTime = null;
		String clintEndTime = null;
		String beforeTimeDuration = null;
		String afterTimeDuration = null;
		List<GenericValue> reportFlags=FastList.newInstance();
		try {
			reportFlags=delegator.findByAnd("ReportFlags", "orgPartyId",orgPartyId);
			if(reportFlags.size()>0){
				 clintPartyId = reportFlags.get(0).getString("clintPartyId");
				if(clintPartyId.equals("0")){
					
				}else{
				 GenericValue clintTimeSetting = delegator.findByPrimaryKey("ClintTimeSetting", UtilMisc.toMap("clintPartyId", clintPartyId));
				 if(UtilValidate.isNotEmpty(clintTimeSetting.get("clintPartyId"))){
				 clintStartTime = clintTimeSetting.getString("startTime");
				 clintEndTime = clintTimeSetting.getString("endTime");
				 beforeTimeDuration = clintTimeSetting.getString("beforeTimeDuration");
				 afterTimeDuration = clintTimeSetting.getString("afterTimeDuration");					 
				 }
				}
			}
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<GenericValue> employeeLeaves=new ArrayList<GenericValue>();
		try{	
		String partyId=AttendanceUtils.getPartyIDFromUserLoginID(delegator,employeeId);	
		List<GenericValue> employmentList=FastList.newInstance();
		java.sql.Timestamp joiningDate=null;
		java.sql.Timestamp resignationDate=null;
		try{
			employmentList=delegator.findByAnd("Employment", "partyIdTo",partyId);
			GenericValue employment=EntityUtil.getFirst(employmentList);
			joiningDate=(Timestamp)employment.get("fromDate");
			resignationDate=(Timestamp)employment.get("thruDate");
		}catch(Exception e){
			logger.debug("Exception--------------->"+e);
		}
		
		List<Map<String,Object>> holidays=FastList.newInstance();
		holidays=AttendanceUtils.getHolidaysWithType(orgPartyId, new Timestamp(fromTime.getTime()), new Timestamp(toTime.getTime()), delegator);
		long stime=AttendanceUtils.getStartTime(fromTime);
		long etime=AttendanceUtils.getEndTime(toTime);
		while(stime<=etime){
				Map<String, String> employeeLog=	FastMap.newInstance();
				List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();	

				Timestamp dayStart=new Timestamp(stime);
				boolean resignationFlag=true;
				if(resignationDate!=null){
					if(stime<resignationDate.getTime()){
						resignationFlag=true;
					}else{
						resignationFlag=false;
					}
				}
				if(stime>=joiningDate.getTime() && resignationFlag){
				long dayEndInLong=AttendanceUtils.getEndTime(new java.sql.Date(stime));
				Timestamp dayEnd=new Timestamp(dayEndInLong);		
				employeeLog.put("date", df.format(dayStart));
			    List<EntityExpr> exprsList = FastList.newInstance();
			    exprsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			    exprsList.add(EntityCondition.makeCondition("leaveStatus", EntityOperator.EQUALS, "LEAVE_APPROVED"));
/*				employeeLeaves = delegator.findList("EmplLeave", EntityCondition.makeCondition(exprsList, EntityOperator.AND), null, null, null, false);
				boolean checkLeaveStatus=AttendanceUtils.checkEmpLeave(employeeLeaves,stime);*/			    
			    double numberOfLeaveDays=AttendanceReports.getTotalFullDayLeave(delegator, orgPartyId, partyId,  dfWithDash.format(dayStart), dfWithDash.format(dayStart));
			    int totalHafldayCount = AttendanceReports.getTotalHalfDayLeave(delegator, orgPartyId, partyId, dayStart, dayEnd);
			    //numberOfLeaveDays = (double) totalHafldayCount/2; 
			    Object checkHoliday=AttendanceUtils.checkHolidayFromHolidayMap(holidays,dayStart);	
				if(checkHoliday!=null){
					summeryHoliDay++;
				}
				if(checkHoliday==null){
				if(numberOfLeaveDays<1){
				List<EntityExpr> exprs = FastList.newInstance();
				List<String> orderBy = FastList.newInstance();
				orderBy.add("logtimeStamp ASC");
				exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO,dayStart));
				exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.LESS_THAN_EQUAL_TO, dayEnd));
				exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));	
				try {
					employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);
				
				} catch (GenericEntityException e) {
					logger.debug("Got Exception To get Employee Log In findJobCard Function---->"+e);
					e.printStackTrace();
				}		
				String exitTime=AttendanceUtils.getExitTime(partyId, delegator, new Date(stime), orgPartyId);		

				//if(!checkHoliday){
				if(exitTime!=null){
					long exitTimeInLong=AttendanceUtils.getStrngTimeInDate(exitTime, new Date(stime));
					Timestamp officeExitTimeInTimeStamp=new Timestamp(exitTimeInLong);
					employeeLog.put("endOfficeTime", simpleDateFormat.format(officeExitTimeInTimeStamp));
				}else{
					employeeLog.put("endOfficeTime", "Not Set");	
				}
	/*			}else{
					if(exitTime!=null){
						employeeLog.put("endOfficeTime", "0");
					}else{
						employeeLog.put("endOfficeTime", "Not Set");	
					}
				}*/
				if(employeeLogs.size()>=1){
					Timestamp	outtime=null;
					Timestamp	entrytime=null;
					if(employeeLogs.size()==1){
						String entryTime=AttendanceUtils.getEntryTime(partyId, delegator, new Date(stime), orgPartyId);
						long entryTimeInLong=AttendanceUtils.getStrngTimeInDate(entryTime, new Date(stime));
						Timestamp officeEntryTimeInTimeStamp=new Timestamp(entryTimeInLong);
						GenericValue employeeLogEntrry=employeeLogs.get(0);
						entrytime=(Timestamp)employeeLogEntrry.get("logtimeStamp");	
						float officeStayTime=UtilDateTime.getHourInterval(officeEntryTimeInTimeStamp, entrytime);
						if(officeStayTime<0){
							officeStayTime=officeStayTime*-1;
						}
						if(officeStayTime>4){
							entrytime=null;
							outtime=(Timestamp)employeeLogEntrry.get("logtimeStamp");	
						}
					}else if(employeeLogs.size()>1){					
					GenericValue outemployeeLog=employeeLogs.get(employeeLogs.size()-1);
					outtime=(Timestamp)outemployeeLog.get("logtimeStamp");
					GenericValue employeeLogEntrry=employeeLogs.get(0);
					entrytime=(Timestamp)employeeLogEntrry.get("logtimeStamp");	
					}							
					float officeStayTime=6F;		// If entry time or out time is missing that employee is count as present,so we initialize this as 6h
					if(entrytime!=null && outtime!=null){
						officeStayTime=UtilDateTime.getHourInterval(entrytime, outtime);
					}
					String workDayType="Present";

					if(entrytime!=null){
						//employeeLog.put("timeIn", simpleDateFormat.format(entrytime));
						//							
						if(clintPartyId!=null && !clintPartyId.equals("0")){
							Date inDate = new Date(entrytime.getTime());
					    	long  clintEntryTimeInDate= AttendanceUtils.getStrngTimeInDate(clintStartTime, inDate);
					    	Calendar logcalendar = Calendar.getInstance();
					    	logcalendar.setTime(inDate);
					    	logcalendar.set(Calendar.SECOND, 0);
					    	logcalendar.set(Calendar.MILLISECOND, 0);

					    	 if(logcalendar.getTimeInMillis()<clintEntryTimeInDate){
					    	  int randomNumber = AttendanceUtils.randInt(1, Integer.parseInt(beforeTimeDuration));
							  Date clintDate = new Date(clintEntryTimeInDate-randomNumber*60000);
					    	  Calendar inTime = Calendar.getInstance();
					    	  inTime.setTime(clintDate);
					    	  inTime.set(Calendar.SECOND, 0);
					    	  inTime.set(Calendar.MILLISECOND, 0);
							  employeeLog.put("timeIn", simpleDateFormat.format(inTime.getTime()));					     		
					     	 }else{
							  employeeLog.put("timeIn", simpleDateFormat.format(entrytime));
					     	 }					    	 					    	
							}else{
							  employeeLog.put("timeIn", simpleDateFormat.format(entrytime));
							}
						
						//
						
					}else{
						employeeLog.put("timeIn", "0");
					}
					if(outtime!=null){
						//employeeLog.put("timeOut", simpleDateFormat.format(outtime));
						//
						Date outDate = new Date(outtime.getTime());	
						if(clintPartyId!=null && !clintPartyId.equals("0")){
					    	long  clintExitTimeInDate= AttendanceUtils.getStrngTimeInDate(clintEndTime, outDate);
					    	
					    	Calendar logcalendar1 = Calendar.getInstance();
					    	logcalendar1.setTime(outDate);
					    	logcalendar1.set(Calendar.SECOND, 0);
					    	logcalendar1.set(Calendar.MILLISECOND, 0);
					    	 
					      if(logcalendar1.getTimeInMillis()>clintExitTimeInDate){
					    	int randomNumber = AttendanceUtils.randInt(1, Integer.parseInt(afterTimeDuration));
							Date clintDate = new Date(clintExitTimeInDate+randomNumber*60000);
					    	Calendar outTime = Calendar.getInstance();
					    	outTime.setTime(clintDate);
					    	outTime.set(Calendar.SECOND, 0);
					    	outTime.set(Calendar.MILLISECOND, 0);
							employeeLog.put("timeOut", simpleDateFormat.format(outTime.getTime()));					     		
					       }else{
							  employeeLog.put("timeOut", simpleDateFormat.format(outDate));
					     	 }
							}else{
							  employeeLog.put("timeOut", simpleDateFormat.format(outDate));
							}
						//
						
					}else{
						employeeLog.put("timeOut", "0");
					}
					if(officeStayTime>=6){
					employeeLog.put("days", "1");
					employeeLog.put("entryStatus", workDayType);
					summeryWorkDay++;
					Integer otHour=0;
					if(outtime!=null){
						otHour=AttendanceUtils.getEmployeeOt(delegator, orgPartyId, partyId, new Date(stime), outtime);
					}
					if(otHour>=0){
					employeeLog.put("overTime", String.valueOf(otHour));
					summeryTotalOtInHour=summeryTotalOtInHour+otHour;
					}
					}else if(officeStayTime>=4 && employeeLogs.size()>1){
						summeryTotalHalfDay++;
						summeryWorkDay=summeryWorkDay+0.5;
						if(numberOfLeaveDays==0.5){
							summeryLeaveDay=summeryLeaveDay+.5;
							employeeLog.put("days", ".5");	
							employeeLog.put("entryStatus", "Present");
							String leaveDescription=AttendanceUtils.getLeaveReasonDesc(employeeLeaves, stime, delegator);
							employeeLog.put("leaveDescription", leaveDescription);
						}else{
							summeryAbsentDay=summeryAbsentDay+.5;
							employeeLog.put("days", ".5");	
							employeeLog.put("entryStatus", "Present");
						}
						Integer otHour=0;
						if(outtime!=null){
							otHour=AttendanceUtils.getEmployeeOt(delegator, orgPartyId, partyId, new Date(stime), outtime);
						}
						if(otHour>=0){
						summeryTotalOtInHour=summeryTotalOtInHour+otHour;
						employeeLog.put("overTime", String.valueOf(otHour));
						}
					}else if(employeeLogs.size()==1){
						employeeLog.put("days", "1");
						employeeLog.put("entryStatus", workDayType);
						Integer otHour=0;
						if(outtime!=null){
							otHour=AttendanceUtils.getEmployeeOt(delegator, orgPartyId, partyId, new Date(stime), outtime);
						}
						if(otHour>=0){
						summeryTotalOtInHour=summeryTotalOtInHour+otHour;
						employeeLog.put("overTime", String.valueOf(otHour));
						}
					}else{
					summeryAbsentDay++;	
					employeeLog.put("days", "0");	
					employeeLog.put("entryStatus", "Absent");
					}
				}else if(numberOfLeaveDays==0.5){
					summeryAbsentDay=summeryAbsentDay+.5;
					summeryLeaveDay=summeryLeaveDay+.5;
					employeeLog.put("days", ".5");	
					employeeLog.put("entryStatus", "Leave");
				}else{
					summeryAbsentDay++;	
					employeeLog.put("entryStatus", "Absent");
					employeeLog.put("timeIn", "0");
					employeeLog.put("timeOut", "0");
					employeeLog.put("days", "0");
				}	
				}else{
					summeryLeaveDay++;
					employeeLog.put("entryStatus", "Leave");
					String leaveDescription=AttendanceUtils.getLeaveReasonDesc(employeeLeaves, stime, delegator);
					employeeLog.put("leaveDescription", leaveDescription);
				}		
		}else{
			
			employeeLog.put("timeIn", "0");
			employeeLog.put("timeOut", "0");
			employeeLog.put("days", "0");
			String holidayType=(String)checkHoliday;
			if(holidayType.equals("OFFICE_HOLIDAY")){
				employeeLog.put("entryStatus", "Holiday(WL)");
				summeryWeeklyDay++;
			}else if(holidayType.equals("PUBLIC_HOLIDAY")){
				employeeLog.put("entryStatus", "Holiday(FL)");
				summeryFestivalDay++;
			}
		}
				
				if(employeeLog!=null && employeeLog.size()>0){
					employeeLogList.add(employeeLog);
				}
		}
				stime=stime+86400000;				
		}	
		
		
		 employmentInfo=AttendanceEvents.getEmploymentInfo((GenericDelegator) delegator,employeeId,orgPartyId);
		
		
		}catch(Exception e){
			logger.debug("Exception in findJobCard--->"+e);
		}
		}
		summeryReportMap.put("summeryWorkDay", String.valueOf(summeryWorkDay));
		summeryReportMap.put("summeryAbsentDay", String.valueOf(summeryAbsentDay));
		summeryReportMap.put("summeryLeaveDay", String.valueOf(summeryLeaveDay));
		summeryReportMap.put("summeryHoliDay", String.valueOf(summeryHoliDay));
		summeryReportMap.put("summeryWeeklyDay", String.valueOf(summeryWeeklyDay));
		summeryReportMap.put("summeryFestivalDay", String.valueOf(summeryFestivalDay));
		summeryReportMap.put("summeryTotalHalfDay", String.valueOf(summeryTotalHalfDay));
		summeryReportMap.put("summeryTotalOtInHour", String.valueOf(summeryTotalOtInHour));
		summeryReport.add(summeryReportMap);
		
		result.put("employeeLogList", employeeLogList);
		result.put("summeryReport", summeryReport);
		result.put("employmentInfo", employmentInfo);
		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Employee Late List");
		if(fromTime!=null){
			result.put("formatedFromDate", df.format(new Timestamp(AttendanceUtils.getStartTime(fromTime))));
		}
		if(toTime!=null){
			result.put("formatedToDate", df.format(new Timestamp(AttendanceUtils.getStartTime(toTime))));
		}
		return result;
	}

public static String ExportJobCardReportToExcel(HttpServletRequest request ,
		HttpServletResponse response) {

	List<Map<String, String>> employeeLogList = (List<Map<String, String>>) request.getSession().getAttribute("employeeLogList");

	if(employeeLogList!=null&&!employeeLogList.isEmpty()){
			long currentTime=System.currentTimeMillis();
	      
	        String fileName = "EmployeeJobCardReport_"
					+ String.valueOf(currentTime) + ".xls";
			
			// create path for excel file
			String excelFilePath = UtilCommon.getAbsoluteFilePath(request, fileName);
		  
			// prepare for excel
			List<String> columnHeaders = new ArrayList<String>();
		
		    columnHeaders.add(0, "date");
		    columnHeaders.add(1, "entryStatus");
		    columnHeaders.add(2, "EmployeeName");	    
		    columnHeaders.add(3, "timeIn");
		    columnHeaders.add(4, "timeOut");
		    columnHeaders.add(5, "days");
		    columnHeaders.add(6, "endOfficeTime");
		    
		    
	        List<List<String>> data = EmployeeExcelExportService.prepareExcelDataForEmployeeAbsenceReport(employeeLogList,columnHeaders);

			try {
				ExcelUtils.saveToExcel(excelFilePath, "employeeLogList", data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return ExcelUtils.downloadExcel(fileName, request, response);
	}else{
		
		return "Data Empty";
	}
}


public static int getRemarksOperatorEmployee(Delegator delegator){
	List<GenericValue> sectionList=FastList.newInstance();
	List<GenericValue> lineList=FastList.newInstance();
	List<GenericValue> positionList=FastList.newInstance();
	List<GenericValue> employeeList=FastList.newInstance();
	List<GenericValue> allemployee=FastList.newInstance();
	int operator=0;
	java.sql.Date fromTime=null;
	
			try {
	        	List<EntityExpr> exprs = FastList.newInstance();
		        exprs.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "10047"));
		        exprs.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP"));
				sectionList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
			
	        if(sectionList!=null&&sectionList.size()>0){
	        	for(GenericValue child:sectionList){
	        		String sectionId=child.getString("partyIdTo");
	        		lineList = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdFrom" ,EntityOperator.EQUALS,sectionId), null, null, null, false);
	        		for(GenericValue value:lineList){
	        			String lineId=value.getString("partyIdTo");
	        			List<EntityExpr> exprs1 = FastList.newInstance();
	        			exprs1.add(EntityCondition.makeCondition("description", EntityOperator.LIKE, "%OPERATOR%"));
	        			exprs1.add(EntityCondition.makeCondition("description", EntityOperator.NOT_EQUAL,"ASSISTANT OPERATOR"));
	        			exprs1.add(EntityCondition.makeCondition("partyId" ,EntityOperator.EQUALS,lineId));
	        			positionList = delegator.findList("EmplPositionAndEmplPositionType", EntityCondition.makeCondition(exprs1 ,EntityOperator.AND), null, null, null, false);
	        			
	        			for(GenericValue value1:positionList){
	        			String description=value1.getString("description");	
	        			String positionId=value1.getString("emplPositionId");
	        			employeeList = delegator.findList("EmplPositionFulfillment",EntityCondition.makeCondition("emplPositionId" ,EntityOperator.EQUALS,positionId), null, null, null, false);
	        			
	        			for(GenericValue value2:employeeList){
	        			String employeeId=value2.getString("partyId");
	        			/*allemployee.add(value2);
	        			temp=allemployee.size();*/
	        			List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
						List<EntityExpr> exprs3 = FastList.newInstance();
						List<String> orderBy = FastList.newInstance();
						orderBy.add("logtimeStamp ASC");
						fromTime=new Date(System.currentTimeMillis());
						exprs3.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("logtimeStamp"), EntityOperator.LIKE, EntityFunction.UPPER(fromTime + "%")));
						exprs3.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,employeeId ));
						employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs3, EntityOperator.AND), null, orderBy, null, true);					
						operator=employeeLogs.size();

	        			}
	        			}
	        		}
	        	}
	        
	        }
	        } catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	return operator;
}

public static int getRemarksHelperEmployee(Delegator delegator){
	List<GenericValue> sectionList=FastList.newInstance();
	List<GenericValue> lineList=FastList.newInstance();
	List<GenericValue> positionList=FastList.newInstance();
	List<GenericValue> employeeList=FastList.newInstance();
	List<GenericValue> allemployee=FastList.newInstance();
	int helper=0;
	java.sql.Date fromTime=null;
	
			try {
	        	List<EntityExpr> exprs = FastList.newInstance();
		        exprs.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "10047"));
		        exprs.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP"));
				sectionList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
			
	        if(sectionList!=null&&sectionList.size()>0){
	        	for(GenericValue child:sectionList){
	        		String sectionId=child.getString("partyIdTo");
	        		lineList = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdFrom" ,EntityOperator.EQUALS,sectionId), null, null, null, false);
	        		for(GenericValue value:lineList){
	        			String lineId=value.getString("partyIdTo");
	        			List<EntityExpr> exprs1 = FastList.newInstance();
	        			exprs1.add(EntityCondition.makeCondition("description", EntityOperator.EQUALS,"ASSISTANT OPERATOR"));
	        			exprs1.add(EntityCondition.makeCondition("partyId" ,EntityOperator.EQUALS,lineId));
	        			positionList = delegator.findList("EmplPositionAndEmplPositionType", EntityCondition.makeCondition(exprs1 ,EntityOperator.AND), null, null, null, false);
	        			
	        			for(GenericValue value1:positionList){
	        			String description=value1.getString("description");	
	        			String positionId=value1.getString("emplPositionId");
	        			employeeList = delegator.findList("EmplPositionFulfillment",EntityCondition.makeCondition("emplPositionId" ,EntityOperator.EQUALS,positionId), null, null, null, false);
	        			
	        			for(GenericValue value2:employeeList){
	        			String employeeId=value2.getString("partyId");
	        			/*allemployee.add(value2);
	        			temp=allemployee.size();*/
	        			List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
						List<EntityExpr> exprs3 = FastList.newInstance();
						List<String> orderBy = FastList.newInstance();
						orderBy.add("logtimeStamp ASC");
						fromTime=new Date(System.currentTimeMillis());
						exprs3.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("logtimeStamp"), EntityOperator.LIKE, EntityFunction.UPPER(fromTime + "%")));
						exprs3.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,employeeId ));
						employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs3, EntityOperator.AND), null, orderBy, null, true);					
						helper=employeeLogs.size();

	        			}
	        			}
	        		}
	        	}
	        	
	        }
	        } catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	return helper;
}

public static int getRemarksIronmanEmployee(Delegator delegator){
	List<GenericValue> sectionList=FastList.newInstance();
	List<GenericValue> lineList=FastList.newInstance();
	List<GenericValue> positionList=FastList.newInstance();
	List<GenericValue> employeeList=FastList.newInstance();
	List<GenericValue> allemployee=FastList.newInstance();
	int ironman=0;
	java.sql.Date fromTime=null;
	
			try {
	        	List<EntityExpr> exprs = FastList.newInstance();
		        exprs.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "10047"));
		        exprs.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP"));
				sectionList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
			
	        if(sectionList!=null&&sectionList.size()>0){
	        	for(GenericValue child:sectionList){
	        		String sectionId=child.getString("partyIdTo");
	        		lineList = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdFrom" ,EntityOperator.EQUALS,sectionId), null, null, null, false);
	        		for(GenericValue value:lineList){
	        			String lineId=value.getString("partyIdTo");
	        			List<EntityExpr> exprs1 = FastList.newInstance();
	        			exprs1.add(EntityCondition.makeCondition("description", EntityOperator.EQUALS,"%IRON MAN%"));
	        			exprs1.add(EntityCondition.makeCondition("partyId" ,EntityOperator.EQUALS,lineId));
	        			positionList = delegator.findList("EmplPositionAndEmplPositionType", EntityCondition.makeCondition(exprs1 ,EntityOperator.AND), null, null, null, false);
	        			
	        			for(GenericValue value1:positionList){
	        			String description=value1.getString("description");	
	        			String positionId=value1.getString("emplPositionId");
	        			employeeList = delegator.findList("EmplPositionFulfillment",EntityCondition.makeCondition("emplPositionId" ,EntityOperator.EQUALS,positionId), null, null, null, false);
	        			
	        			for(GenericValue value2:employeeList){
	        			String employeeId=value2.getString("partyId");
	        			/*allemployee.add(value2);
	        			temp=allemployee.size();*/
	        			List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
						List<EntityExpr> exprs3 = FastList.newInstance();
						List<String> orderBy = FastList.newInstance();
						orderBy.add("logtimeStamp ASC");
						fromTime=new Date(System.currentTimeMillis());
						exprs3.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("logtimeStamp"), EntityOperator.LIKE, EntityFunction.UPPER(fromTime + "%")));
						exprs3.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,employeeId ));
						employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs3, EntityOperator.AND), null, orderBy, null, true);					
						ironman=employeeLogs.size();

	        			}
	        			}
	        		}
	        	}
	        	
	        
	        }
	        } catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	return ironman;
}
public static int getallEmployee(Delegator delegator){
	List<GenericValue> sectionList=FastList.newInstance();
	List<GenericValue> lineList=FastList.newInstance();
	List<GenericValue> positionList=FastList.newInstance();
	List<GenericValue> employeeList=FastList.newInstance();
	List<GenericValue> allemployee=FastList.newInstance();
	int allEmployee=0;
	java.sql.Date fromTime=null;
	
			try {
	        	List<EntityExpr> exprs = FastList.newInstance();
		        exprs.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "10047"));
		        exprs.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP"));
				sectionList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
			
	        if(sectionList!=null&&sectionList.size()>0){
	        	for(GenericValue child:sectionList){
	        		String sectionId=child.getString("partyIdTo");
	        		lineList = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdFrom" ,EntityOperator.EQUALS,sectionId), null, null, null, false);
	        		for(GenericValue value:lineList){
	        			String lineId=value.getString("partyIdTo");
	        			List<EntityExpr> exprs1 = FastList.newInstance();
	        			exprs1.add(EntityCondition.makeCondition("partyId" ,EntityOperator.EQUALS,lineId));
	        			positionList = delegator.findList("EmplPositionAndEmplPositionType", EntityCondition.makeCondition(exprs1 ,EntityOperator.AND), null, null, null, false);
	        			
	        			for(GenericValue value1:positionList){
	        			String description=value1.getString("description");	
	        			String positionId=value1.getString("emplPositionId");
	        			employeeList = delegator.findList("EmplPositionFulfillment",EntityCondition.makeCondition("emplPositionId" ,EntityOperator.EQUALS,positionId), null, null, null, false);
	        			
	        			for(GenericValue value2:employeeList){
	        			String employeeId=value2.getString("partyId");
	        			/*allemployee.add(value2);
	        			temp=allemployee.size();*/
	        			List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
						List<EntityExpr> exprs3 = FastList.newInstance();
						List<String> orderBy = FastList.newInstance();
						orderBy.add("logtimeStamp ASC");
						fromTime=new Date(System.currentTimeMillis());
						exprs3.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("logtimeStamp"), EntityOperator.LIKE, EntityFunction.UPPER(fromTime + "%")));
						exprs3.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,employeeId ));
						employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs3, EntityOperator.AND), null, orderBy, null, true);					
						allEmployee=employeeLogs.size();

	        			}
	        			}
	        		}
	        	}
	        	
	        }
	        } catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	return allEmployee;
}

public static Map<String, Object> findSectionWiseStrenth(DispatchContext dctx,Map<String, ? extends Object> context) {
	
	Map<String, Object> result = ServiceUtil.returnSuccess();
	//DateFormat df = new SimpleDateFormat("dd/MM/yyyy"); 
	Delegator delegator = dctx.getDelegator();
	String fromDate = (String) context.get("fromDate");
	SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm a");
	String orgPartyId = (String) context.get("orgPartyId"); 
	TimeZone timeZone=(TimeZone) context.get("timeZone"); 
	Locale locale=(Locale) context.get("locale"); 
	
	List<Map<String, String>> sectionWiseStrenthList=FastList.newInstance();
	List<Map<String, String>> sectionWiseStrenthList1=FastList.newInstance();
			//get Position 
	List<Map<String,String>> positions=AttendanceUtils.getPositions(delegator, orgPartyId);
	
  	// filter on division
    String division = (String) context.get("division");
	if (UtilValidate.isNotEmpty(division)) {
		  orgPartyId=division;
        }
	
	// filter on department
	String department = (String) context.get("department");
	if (UtilValidate.isNotEmpty(department)) {
	   orgPartyId=department;
	   
        }
    
    // filter on section
    String section = (String) context.get("section");
    if (UtilValidate.isNotEmpty(section)) {
    	  orgPartyId=section; 
    }
    // filter on line
    String line = (String) context.get("line");
     if (UtilValidate.isNotEmpty(line)) {
    	  orgPartyId=line;  
    }
    	logger.debug("orgPartyId-------->"+orgPartyId);
	List<GenericValue> partyList=FastList.newInstance();
	if(fromDate!=null&&!fromDate.isEmpty()){
			
		java.sql.Date fromTime=null;
		if(fromDate!=null && !fromDate.isEmpty()){
			try{
				fromTime=Date.valueOf(fromDate);
				
			}catch(Exception e){
				fromTime=new Date(System.currentTimeMillis());
			}
		}else{
			fromTime=new Date(System.currentTimeMillis());
		}
	
		int operator=0;
		int helper=0;
		int ironMan=0;
		int allemployee=0;
		int others=0;
		
	
		operator=getRemarksOperatorEmployee(delegator);
		helper=getRemarksHelperEmployee(delegator);
		ironMan=getRemarksIronmanEmployee(delegator);
		allemployee=getallEmployee(delegator);
		others=allemployee -(operator+helper+ironMan);
	
	
		java.sql.Date fromDateTime=Date.valueOf(fromDate);
		long TimeInMillisecond=AttendanceUtils.getStartTime(fromDateTime);
		TimeInMillisecond=TimeInMillisecond-86400000;

		partyList=getTotalEmployee(delegator,partyList,orgPartyId);
		logger.debug("Total Employee-------->"+partyList.size());
		
		List<GenericValue> divitionlist=FastList.newInstance();
		List<GenericValue> Departmentlist=FastList.newInstance();
		List<GenericValue> Sectionlist=FastList.newInstance();
		List<GenericValue> linelist=FastList.newInstance();
		List<GenericValue> positionList=FastList.newInstance();
		
		Map<String, String> otherMap=	FastMap.newInstance();
		try {
			divitionlist=delegator.findList("PartyGroup", EntityCondition.makeCondition("partyId" ,EntityOperator.EQUALS,"50002"), null, null, null, false);/*10044*/
			for(GenericValue devesion:divitionlist){
			
				Map<String, String> divisionMap=	FastMap.newInstance();
				String devesionName=devesion.getString("groupName");
				String partyId=devesion.getString("partyId");
				divisionMap.put("Division", devesionName);
				sectionWiseStrenthList.add(divisionMap);
				
				Departmentlist= delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId),null,null,null,false);
				
				int sl=1;
			for(GenericValue departments:Departmentlist){
				
				Map<String, String> departmentMap=	FastMap.newInstance();
				String departmentId=departments.getString("partyIdTo");
        		String departmentName= AttendanceUtils.getGroupName(delegator,departmentId);
        		departmentMap.put("Department", departmentName);
        		sectionWiseStrenthList.add(departmentMap);
				Sectionlist=delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, departmentId),null,null,null,false);	
					
				
			for(GenericValue sections:Sectionlist){
				
				Map<String, String> sectionMap=	FastMap.newInstance();
				String sectionId=sections.getString("partyIdTo");
				String sectionName= AttendanceUtils.getGroupName(delegator,sectionId);
				sectionMap.put("Section", sectionName);
				sectionWiseStrenthList.add(sectionMap);
				linelist=delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, sectionId),null,null,null,false);	
			
			for(GenericValue lines:linelist){
				//Map<String, String> lineMap=	FastMap.newInstance();
				String lineId=lines.getString("partyIdTo");
				String lineName= AttendanceUtils.getGroupName(delegator,lineId);
				sectionMap.put("Line", lineName);
				//sectionWiseStrenthList.add(sectionMap);
				
				
				int required = 0;
				
				
				
				
				
				positionList = delegator.findList("EmplPosition", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, lineId), null, null, null, false);
			/*	for(GenericValue position:positionList){
					String positionid=position.getString("emplPositionId");
					//String budgetid=position.getString("budgetId");
					
						GenericValue empPosition = delegator.findByPrimaryKey(
								"EmplPosition", UtilMisc.toMap("emplPositionId",positionid.trim()));
						String budgetid=empPosition.getString("budgetId");
						if (UtilValidate.isNotEmpty(budgetid)) {
					
					List<GenericValue>  budgetItemList = null;
				
					budgetItemList = delegator.findByAnd("BudgetItem", UtilMisc.toMap("budgetId", budgetid,"budgetItemTypeId", "REQUIREMENT_BUDGET"));
						if(budgetItemList.size()>0){
						GenericValue budgetItem = EntityUtil.getFirst(budgetItemList);
						
						String amount =  budgetItem.getString("amount");
						float r= Float.valueOf(amount.trim());
						  required = (int) r;
						  }
						otherMap.put("Required", String.valueOf(required));
						
					}	
						
						if(operator>0 ||helper>0||ironMan>0)	{	
					String remarks="Operator = " +String.valueOf(operator) +" " 
							+"Helper = " +String.valueOf(helper) +" "
							+"Ironman = " +String.valueOf(ironMan)+" "
							+"Others = " +String.valueOf(others) ;
					
					otherMap.put("Remarks", remarks);
					sectionWiseStrenthList.add(otherMap);
					
						
						
				}
					
				}
				*/
				
				
				
			}
			
			sectionMap.put("SL", String.valueOf(sl));
			sl++;
			}
			
				
			}	
		
			
			   }
			
			sectionWiseStrenthList1.addAll(sectionWiseStrenthList)	;	
			
		} catch (GenericEntityException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
    	
	}
	else{
		
	}
	

result.put("sectionWiseStrenthList", sectionWiseStrenthList1);
result.put("selectedDate", fromDate);
result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
result.put(ModelService.SUCCESS_MESSAGE, "Section Wise Strength List");
return result;
}



public static String exportSectionWiseReportToExcel(HttpServletRequest request ,
	HttpServletResponse response) {
GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
	List<Map<String, String>> sectionWiseStrenthList = (List<Map<String, String>>) request.getSession().getAttribute("sectionWiseStrenthList");
	Map<String, String> total = (Map<String, String>) request.getSession().getAttribute("total");
if(sectionWiseStrenthList!=null&&!sectionWiseStrenthList.isEmpty()){
		long currentTime=System.currentTimeMillis();
		//String SelectedDate= sectionWiseStrenthList.get(0).get("SelectedDate");
		//String PreviousDate= sectionWiseStrenthList.get(0).get("PreviousDate");
      
        String fileName = "Report_SectionWise"
				+ String.valueOf(currentTime) + ".xls";
		
		// create path for excel file
		String excelFilePath = UtilCommon.getAbsoluteFilePath(request, fileName);
	  
		// prepare for excel
		List<String> columnHeaders = new ArrayList<String>();
	
	    columnHeaders.add(0, "SL");
	    columnHeaders.add(1, "Division");
	    columnHeaders.add(2, "Department");
	    columnHeaders.add(3, "Section");
	    columnHeaders.add(4, "Line");
	    columnHeaders.add(5, "Required");
	    columnHeaders.add(6, "Existing");	    
	    columnHeaders.add(7, "Present");
	    columnHeaders.add(8, "Absent");
	    columnHeaders.add(9, "Absent %");
	    columnHeaders.add(9, "Remarks");
	   // columnHeaders.add(6, "OutTime["+PreviousDate+"]");
	   // columnHeaders.add(7, "InTime["+SelectedDate+"]");
	   
	    
	    List<Map<String, String>> reportList=EmployeeExcelExportService.getSectionWiseReportInfo(delegator, sectionWiseStrenthList);
        List<List<String>> data = EmployeeExcelExportService.prepareExcelDataForSectionWiseReport(reportList,columnHeaders);

		try {
			ExcelUtils.saveToExcel2(excelFilePath, "employeeSectionWiseReportList", data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ExcelUtils.downloadExcel(fileName, request, response);
}else{
	
	return "Data Empty";
}
}

//*Start of Parvez code*
public static Map<String, Object> findDailyMonthlyAbsenteeism(DispatchContext dctx,
		Map<String, ? extends Object> context) {
	Map<String, Object> result = ServiceUtil.returnSuccess();
	
	//DateFormat df = new SimpleDateFormat("dd/MM/yyyy"); 
	
	Delegator delegator = dctx.getDelegator();
	
	String fromDate = (String) context.get("fromDate");
	String toDate = (String) context.get("toDate");
	java.sql.Date fromTime=null;
	java.sql.Date toTime=null;
	String orgPartyId = (String) context.get("orgPartyId");
	String orgId = (String) context.get("orgPartyId");
	TimeZone timeZone=(TimeZone) context.get("timeZone"); 
	Locale locale=(Locale) context.get("locale"); 
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

	

	
	List<Map<String, String>> absenteeismList=FastList.newInstance();
    Map<String, String> additionalInfo=	FastMap.newInstance();
	
  	// filter on division
    String division = (String) context.get("division");
	if (UtilValidate.isNotEmpty(division)) {
		  orgPartyId=division;
        }
	
	// filter on department
	String department = (String) context.get("department");
	      
    if (UtilValidate.isNotEmpty(department)) {
	   orgPartyId=department;
	   
        }
    
    // filter on section
    String section = (String) context.get("section");
      
    if (UtilValidate.isNotEmpty(section)) {
    	  orgPartyId=section; 
    }
    // filter on line
    String line = (String) context.get("line");
    
    if (UtilValidate.isNotEmpty(line)) {
    	  orgPartyId=line;  
    }
    	logger.debug("orgPartyId-------->"+orgPartyId);
	List<GenericValue> partyList=FastList.newInstance();
	List<GenericValue> partyRelationships=FastList.newInstance();
	List<Timestamp> holidays=new ArrayList<Timestamp>();
	
	try {
	if(fromDate!=null&&!fromDate.isEmpty()&&toDate!=null&&!toDate.isEmpty()){
		int days=0;
		float avgParcentage=0;
		fromTime=Date.valueOf(fromDate);
		toTime=Date.valueOf(toDate);
		holidays=AttendanceUtils.getHolidays(orgId, new Timestamp(fromTime.getTime()), new Timestamp(toTime.getTime()), delegator);
		partyRelationships = AttendanceUtils.getTotalEmployee(delegator, partyRelationships, orgPartyId);
	
	  if(partyRelationships.size()>0){	
		
		Calendar start = Calendar.getInstance();
        start.setTime(fromTime);
        Calendar end = Calendar.getInstance();
        end.setTime(toTime);
              
        for (java.util.Date date =  start.getTime(); !start.after(end); start.add(Calendar.DATE, 1), date =  start.getTime()) 
	      {
        	Map<String, String> absenteeism = FastMap.newInstance();
        	 if(holidays.contains(date)){
        		absenteeism.put("Date", String.valueOf(fmt.format(date)));
     	        absenteeism.put("AbsentPersent", "Holiday");
     	        absenteeismList.add(absenteeism);   
	           continue;
	       }	
        	int totalAbsence =0;
        	int totalPresent =0;
        	days++;
        	String dateToPartyList=String.valueOf(dateFormat.format(date));
    		Timestamp dayStart=AttendanceUtils.getModifiedDayStart(new Timestamp(Date.valueOf(dateToPartyList).getTime()));
    		partyRelationships=AttendanceUtils.filterValidEmployeeListByDate(delegator, partyRelationships, dayStart);
  
           for(GenericValue partyRelationship:partyRelationships){
        	   String employeeId=partyRelationship.getString("partyId"); 
        	   
        	   List<EntityCondition> exprsAnd = FastList.newInstance();
			   Set<String> fieldsToSelect = UtilMisc.toSet("logtimeStamp");
			   List<String> orderBy = FastList.newInstance();
			   orderBy.add("logtimeStamp ASC");
			   String searchDate=dateFormat.format(date);
			   
			   exprsAnd.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeId));
	           exprsAnd.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("logtimeStamp"), EntityOperator.LIKE, EntityFunction.UPPER(searchDate + "%")));
	            
	           List<GenericValue> empLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprsAnd, EntityOperator.AND), fieldsToSelect,orderBy , null, true);

	           if(empLogs.size()<=0)
	          	           {
	          	        	   totalAbsence++;
	          	        	   
	          	           }else{
	          	        	   totalPresent++;
	          	            }  
        	   
           }
           
           
           float parcentage =  (float)((totalAbsence * 100)/partyRelationships.size());
           avgParcentage=avgParcentage+parcentage;
			
			String absentPersentance = String.format("%.2f", parcentage)+"%";
			absenteeism.put("Date", String.valueOf(fmt.format(date)));
	        absenteeism.put("AbsentPersent", String.valueOf(absentPersentance));
	        absenteeismList.add(absenteeism);
			          
		}
        avgParcentage=avgParcentage/days;
        String avgParcent = String.format("%.2f", avgParcentage)+"%";
		
        additionalInfo.put("duration", String.valueOf(fmt.format(fromTime)+" To : "+fmt.format(toTime)));
        additionalInfo.put("unit", String.valueOf(orgPartyId));
        additionalInfo.put("avgAbsent", avgParcent);

	  } 
	}
	} catch (GenericEntityException e) {
		result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
		result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
		return result;
	}
	
    result.put("absenteeismList", absenteeismList);
    result.put("additionalInfo", additionalInfo);
	result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
	result.put(ModelService.SUCCESS_MESSAGE, "Daily Monthly Absenteeism List");
	return result;
}

public static String exportDailyMonthlyAbsenteeismToExcel(HttpServletRequest request ,
		HttpServletResponse response) {

  	List<Map<String, String>> absenteeismList = (List<Map<String, String>>) request.getSession().getAttribute("absenteeismList");

	if(absenteeismList!=null&&!absenteeismList.isEmpty()){
			long currentTime=System.currentTimeMillis();
	      
	        String fileName = "DailyAbsenceReport_"
					+ String.valueOf(currentTime) + ".xls";
			
			// create path for excel file
			String excelFilePath = UtilCommon.getAbsoluteFilePath(request, fileName);
		  
			// prepare for excel
			List<String> columnHeaders = new ArrayList<String>();
		
		    columnHeaders.add(0, "SL");
		    columnHeaders.add(1, "EmployeeId");
		    columnHeaders.add(2, "EmployeeName");	    
		    columnHeaders.add(3, "Card");
		    columnHeaders.add(4, "Designation");
		    columnHeaders.add(5, "Department");
		    columnHeaders.add(6, "TotalAbsence");
		    columnHeaders.add(7, "ContinuesAbsence");
		    
		    
	        List<List<String>> data = EmployeeExcelExportService.prepareExcelDataForEmployeeAbsenceReport(absenteeismList,columnHeaders);

			try {
				ExcelUtils.saveToExcel(excelFilePath, "employeeAbsenceList", data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return ExcelUtils.downloadExcel(fileName, request, response);
	}else{
		
		return "Data Empty";
	}
}

public static Map<String, Object> findTowDaysAttendanceReport(DispatchContext dctx,
		Map<String, ? extends Object> context) {
	Map<String, Object> result = ServiceUtil.returnSuccess();
	
	Delegator delegator = dctx.getDelegator();
	
	String fromDate = (String) context.get("fromDate");
	String orgPartyId = (String) context.get("orgPartyId"); 
	String orgId = (String) context.get("orgPartyId");
	TimeZone timeZone=(TimeZone) context.get("timeZone"); 
	Locale locale=(Locale) context.get("locale"); 
	
	List<Map<String, String>> twodaysReportList=FastList.newInstance();
	 String clintPartyId=null;
	 String clintStartTime = null;
	 String clintEndTime = null;
	 String beforeTimeDuration = null;
	 String afterTimeDuration = null;
	
	SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
	DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
	
  	// filter on division
    String division = (String) context.get("division");
	if (UtilValidate.isNotEmpty(division)) {
		  orgPartyId=division;
        }
	
	// filter on department
	String department = (String) context.get("department");
	      
    if (UtilValidate.isNotEmpty(department)) {
	   orgPartyId=department;
	   
        }
    
    // filter on section
    String section = (String) context.get("section");
      
    if (UtilValidate.isNotEmpty(section)) {
    	  orgPartyId=section; 
    }
    // filter on line
    String line = (String) context.get("line");
    
    if (UtilValidate.isNotEmpty(line)) {
    	  orgPartyId=line;  
    }
    	logger.debug("orgPartyId-------->"+orgPartyId);
	List<GenericValue> partyList=FastList.newInstance();
	
	if(fromDate!=null&&!fromDate.isEmpty()){
			
		java.sql.Date fromTime=null;
		
		if(fromDate!=null && !fromDate.isEmpty()){
			try{
				fromTime=Date.valueOf(fromDate);
				
			}catch(Exception e){
				fromTime=new Date(System.currentTimeMillis());
			}
		}else{
			fromTime=new Date(System.currentTimeMillis());
		}
		Timestamp dayStart=AttendanceUtils.getModifiedDayStart(new Timestamp(fromTime.getTime()));
	
		java.sql.Date fromDateTime=Date.valueOf(fromDate);
		long TimeInMillisecond=AttendanceUtils.getStartTime(fromDateTime);
		TimeInMillisecond=TimeInMillisecond-86400000;
		Date previousDate = new Date(TimeInMillisecond);
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
		List<GenericValue> reportFlags=FastList.newInstance();
		try {
			reportFlags=delegator.findByAnd("ReportFlags", "orgPartyId",orgId);
			if(reportFlags.size()>0){
				 clintPartyId = reportFlags.get(0).getString("clintPartyId");
				if(clintPartyId.equals("0")){
					
				}else{
				 GenericValue clintTimeSetting = delegator.findByPrimaryKey("ClintTimeSetting", UtilMisc.toMap("clintPartyId", clintPartyId));
				 if(UtilValidate.isNotEmpty(clintTimeSetting.get("clintPartyId"))){
					  clintStartTime = clintTimeSetting.getString("startTime");
					  clintEndTime = clintTimeSetting.getString("endTime");
					  beforeTimeDuration = clintTimeSetting.getString("beforeTimeDuration");
					  afterTimeDuration = clintTimeSetting.getString("afterTimeDuration");					 
				 }
				}
			}
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

			partyList=getTotalEmployee(delegator,partyList,orgPartyId);
			partyList=AttendanceUtils.filterValidEmployeeListByDate(delegator, partyList, dayStart);
			logger.debug("Total Employee-------->"+partyList.size());
			int sl=1;
			for(GenericValue party:partyList){
				
				String partyId=party.getString("partyId");
				//String employeeId = AttendanceUtils.getUserLoginIDFromPartyID(delegator, partyId);
				
				if(AttendanceUtils.checkCardExistanceByPartyId(delegator,partyId))
				{
					List<GenericValue> employeePreviousDayLogs  = new ArrayList<GenericValue>();
					List<EntityExpr> pexprs = FastList.newInstance();
				
					List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
					List<EntityExpr> exprs = FastList.newInstance();
					List<String> orderBy = FastList.newInstance();
					orderBy.add("logtimeStamp ASC");

					exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("logtimeStamp"), EntityOperator.LIKE, EntityFunction.UPPER(fromTime + "%")));
					exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId ));
					
					pexprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("logtimeStamp"), EntityOperator.LIKE, EntityFunction.UPPER(formatter.format(previousDate) + "%")));
					pexprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId ));
										
					try {
						Map<String, String> employee=	FastMap.newInstance();
						employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);					
						employeePreviousDayLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(pexprs, EntityOperator.AND), null, orderBy, null, true);

						if(employeeLogs.size()>0){

							Date sDate = new Date(employeeLogs.get(0).getTimestamp("logtimeStamp").getTime());
							//
							if(clintPartyId!=null && !clintPartyId.equals("0")){
					    	long  clintEntryTimeInDate= AttendanceUtils.getStrngTimeInDate(clintStartTime, sDate);
					    	Calendar logcalendar = Calendar.getInstance();
					    	logcalendar.setTime(sDate);
					    	logcalendar.set(Calendar.SECOND, 0);
					    	logcalendar.set(Calendar.MILLISECOND, 0);
							
					    	 if(logcalendar.getTimeInMillis()<clintEntryTimeInDate){
					    		 int randomNumber = AttendanceUtils.randInt(1, Integer.parseInt(beforeTimeDuration));
								 Date clintDate = new Date(clintEntryTimeInDate-randomNumber*60000);
					    		 Calendar inTime = Calendar.getInstance();
					    		 inTime.setTime(clintDate);
					    		 inTime.set(Calendar.SECOND, 0);
					    		 inTime.set(Calendar.MILLISECOND, 0);
								 employee.put("SIn", String.valueOf(hourFormat.format(inTime.getTime())));				 
					     		
					     	 }else{
									employee.put("SIn", String.valueOf(hourFormat.format(sDate)));				 
					     	 }
							}else{
								employee.put("SIn", String.valueOf(hourFormat.format(sDate)));				 

							}
							//
							//employee.put("SIn", String.valueOf(hourFormat.format(sDate)));				 
						
						}else{
								employee.put("SIn", "0");
						}
						if(employeePreviousDayLogs.size()>0){
							
							Date inDate = new Date(employeePreviousDayLogs.get(0).getTimestamp("logtimeStamp").getTime());
							Date outDate = new Date(employeePreviousDayLogs.get(employeePreviousDayLogs.size()-1).getTimestamp("logtimeStamp").getTime());
						//	employee.put("PIn", String.valueOf(hourFormat.format(inDate)));
						//	employee.put("POut", String.valueOf(hourFormat.format(outDate)));
						//	
							if(clintPartyId!=null && !clintPartyId.equals("0")){
						    	long  clintEntryTimeInDate= AttendanceUtils.getStrngTimeInDate(clintStartTime, inDate);
						    	long  clintExitTimeInDate= AttendanceUtils.getStrngTimeInDate(clintEndTime, outDate);

						    	Calendar logcalendar = Calendar.getInstance();
						    	logcalendar.setTime(inDate);
						    	logcalendar.set(Calendar.SECOND, 0);
						    	logcalendar.set(Calendar.MILLISECOND, 0);
						    	
						    	Calendar logcalendar1 = Calendar.getInstance();
						    	logcalendar1.setTime(outDate);
						    	logcalendar1.set(Calendar.SECOND, 0);
						    	logcalendar1.set(Calendar.MILLISECOND, 0);
								
						    	 if(logcalendar.getTimeInMillis()<clintEntryTimeInDate){
						    		 int randomNumber = AttendanceUtils.randInt(1, Integer.parseInt(beforeTimeDuration));
									 Date clintDate = new Date(clintEntryTimeInDate-randomNumber*60000);
						    		 Calendar inTime = Calendar.getInstance();
						    		 inTime.setTime(clintDate);
						    		 inTime.set(Calendar.SECOND, 0);
						    		 inTime.set(Calendar.MILLISECOND, 0);
									 employee.put("PIn", String.valueOf(hourFormat.format(inTime.getTime())));				 
						     		
						     	 }else{
										employee.put("PIn", String.valueOf(hourFormat.format(inDate)));				 
						     	 }
						    	 
						    	 if(logcalendar1.getTimeInMillis()>clintExitTimeInDate){
						    		 int randomNumber = AttendanceUtils.randInt(1, Integer.parseInt(afterTimeDuration));
									 Date clintDate = new Date(clintExitTimeInDate+randomNumber*60000);
						    		 Calendar outTime = Calendar.getInstance();
						    		 outTime.setTime(clintDate);
						    		 outTime.set(Calendar.SECOND, 0);
						    		 outTime.set(Calendar.MILLISECOND, 0);
									 employee.put("POut", String.valueOf(hourFormat.format(outTime.getTime())));				 
						     		
						     	 }else{
									employee.put("POut", String.valueOf(hourFormat.format(outDate)));				 
						     	 }
								}else{
									employee.put("PIn", String.valueOf(hourFormat.format(inDate)));	
									employee.put("POut", String.valueOf(hourFormat.format(outDate)));				 

								}
							
						}else{
							    employee.put("PIn", "0");
								employee.put("POut", "0");
						}
							
							employee.put("SL", String.valueOf(sl));
							employee.put("EmployeeId", AttendanceUtils.getUserLoginIDFromPartyID(delegator,partyId));							
							String cardId=	AttendanceUtils.getCardIdFromPartyId(delegator,partyId);							
							String designation=	AttendanceUtils.getPartyDesignation(delegator,partyId);
							
							List<String> departmentNameList=FastList.newInstance();
							departmentNameList=AttendanceUtils.getPartyDepartment(delegator,partyId,departmentNameList);
							
							employee.put("Card", cardId);
							employee.put("Designation", designation);
							employee.put("Department", AttendanceUtils.getDepartmentName(departmentNameList));


							GenericValue person = delegator.findByPrimaryKey(
									"Person", UtilMisc.toMap("partyId",partyId.trim()));
	
							if (!UtilValidate.isEmpty(person)){
								String name=AttendanceUtils.avoidNullPointerException((String)person.get("firstName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("middleName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("lastName"));
								employee.put("EmployeeName", name);
							}
							
							employee.put("SelectedDate", fmt.format(Date.valueOf(fromDate)));
							employee.put("PreviousDate", fmt.format(previousDate));
							
							
							List<GenericValue> empSkillList = delegator.findByAnd("PartySkillTest", UtilMisc.toMap("partyId",partyId.trim()));
							if (!UtilValidate.isEmpty(empSkillList)) {
								StringBuffer processList = new StringBuffer();
								 int j=0;
							for(GenericValue empProcess: empSkillList){
							    String workEffortId = 	empProcess.getString("workEffortId");
							    List<GenericValue> workEffortList = delegator.findByAnd("WorkEffort", UtilMisc.toMap("workEffortId",workEffortId.trim()));
							 
							 if (!UtilValidate.isEmpty(workEffortList)) {
								 GenericValue workEffort = EntityUtil.getFirst(workEffortList);
								 processList.append(workEffort.getString("workEffortName"));
								 if(empSkillList.size()>1 && empSkillList.size()-1!=j){
									 processList.append(", ");
								 }

								 employee.put("Process", processList.toString()); 
							   }

							    j++;
							}							
						   }
							
							twodaysReportList.add(employee);	
							sl++;
					
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
			}
			}
		
	}
	
	ArrayList<Map> sortedTwodaysReportList = AttendanceUtils.getSortedList(twodaysReportList);
    int serial =1;
    for(int i=0;i<sortedTwodaysReportList.size();i++){
    	Map<String,String> sortedMap = sortedTwodaysReportList.get(i);
    	sortedMap.put("SL", String.valueOf(serial++));
    	sortedTwodaysReportList.set(i, sortedMap);
    }
    result.put("twodaysReportList", sortedTwodaysReportList);
    if(!UtilValidate.isEmpty(fromDate)){
    	result.put("selectedDate", fmt.format(Date.valueOf(fromDate)));
    }else{
    	result.put("selectedDate", fromDate);
    }
	
	result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
	result.put(ModelService.SUCCESS_MESSAGE, "Employee 2days attendance List");
	return result;
}

public static String exportTwoDaysReportToExcel(HttpServletRequest request ,
		HttpServletResponse response) {
	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
  	List<Map<String, String>> twodaysReportList = (List<Map<String, String>>) request.getSession().getAttribute("twodaysReportList");

	if(twodaysReportList!=null&&!twodaysReportList.isEmpty()){
			long currentTime=System.currentTimeMillis();
			String SelectedDate= twodaysReportList.get(0).get("SelectedDate");
			String PreviousDate= twodaysReportList.get(0).get("PreviousDate");
	      
	        String fileName = "Report_"+SelectedDate+"_And_"+PreviousDate+"_"
					+ String.valueOf(currentTime) + ".xls";
			
			// create path for excel file
			String excelFilePath = UtilCommon.getAbsoluteFilePath(request, fileName);
		  
			// prepare for excel
			List<String> columnHeaders = new ArrayList<String>();
		
		    columnHeaders.add(0, "SL");
		    columnHeaders.add(1, "PIN");
		    columnHeaders.add(2, "EmployeeName");	    
		    columnHeaders.add(3, "Card");
		    columnHeaders.add(4, "Designation");
		    columnHeaders.add(4, "Department");
		    columnHeaders.add(5, "InTime["+PreviousDate+"]");
		    columnHeaders.add(6, "OutTime["+PreviousDate+"]");
		    columnHeaders.add(7, "InTime["+SelectedDate+"]");
		   
		    
		    List<Map<String, String>> reportList=EmployeeExcelExportService.getAttandanceReportInfo(delegator, twodaysReportList);
	        List<List<String>> data = EmployeeExcelExportService.prepareExcelDataFor2DaysReport(reportList,columnHeaders);

			try {
				ExcelUtils.saveToExcel2(excelFilePath, "employeeTwoDaysReportList", data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return ExcelUtils.downloadExcel(fileName, request, response);
	}else{
		
		return "Data Empty";
	}
}

public static Map<String, Object> findThreeDaysAttendanceReport(DispatchContext dctx,
		Map<String, ? extends Object> context) {
	Map<String, Object> result = ServiceUtil.returnSuccess();
	
	Delegator delegator = dctx.getDelegator();
	
	String fromDate = (String) context.get("fromDate");
	String orgPartyId = (String) context.get("orgPartyId"); 
	String orgId = (String) context.get("orgPartyId");
	TimeZone timeZone=(TimeZone) context.get("timeZone"); 
	Locale locale=(Locale) context.get("locale"); 
	DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
	
	List<Map<String, String>> threedaysReportList=FastList.newInstance();
	String clintPartyId=null;
	String clintStartTime = null;
	String clintEndTime = null;
	String beforeTimeDuration = null;
	String afterTimeDuration = null;
	
	SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
	java.sql.Date fromTime=null;
	
  	// filter on division
    String division = (String) context.get("division");
	if (UtilValidate.isNotEmpty(division)) {
		  orgPartyId=division;
        }
	
	// filter on department
	String department = (String) context.get("department");
	      
    if (UtilValidate.isNotEmpty(department)) {
	   orgPartyId=department;
	   
        }
    
    // filter on section
    String section = (String) context.get("section");
      
    if (UtilValidate.isNotEmpty(section)) {
    	  orgPartyId=section; 
    }
    // filter on line
    String line = (String) context.get("line");
    
    if (UtilValidate.isNotEmpty(line)) {
    	  orgPartyId=line;  
    }
    	logger.debug("orgPartyId-------->"+orgPartyId);
	List<GenericValue> partyList=FastList.newInstance();
	
	if(fromDate!=null&&!fromDate.isEmpty()){

		if(fromDate!=null && !fromDate.isEmpty()){
			try{
				fromTime=Date.valueOf(fromDate);
				
			}catch(Exception e){
				fromTime=new Date(System.currentTimeMillis());
			}
		}else{
			fromTime=new Date(System.currentTimeMillis());
		}
		java.sql.Date fromDateTime=Date.valueOf(fromDate);
		long TimeInMillisecond=AttendanceUtils.getStartTime(fromDateTime);
		TimeInMillisecond=TimeInMillisecond-86400000;
		long towPreviousDateInTime=TimeInMillisecond-86400000;
		Date previousDate = new Date(TimeInMillisecond);
		Date twoPreviousDate = new Date(towPreviousDateInTime);
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    Timestamp dayStart=AttendanceUtils.getModifiedDayStart(new Timestamp(fromTime.getTime()));	

		//
		List<GenericValue> reportFlags=FastList.newInstance();
		try {
			reportFlags=delegator.findByAnd("ReportFlags", "orgPartyId",orgId);
			if(reportFlags.size()>0){
				 clintPartyId = reportFlags.get(0).getString("clintPartyId");
				if(clintPartyId.equals("0")){
					
				}else{
				 GenericValue clintTimeSetting = delegator.findByPrimaryKey("ClintTimeSetting", UtilMisc.toMap("clintPartyId", clintPartyId));
				 if(UtilValidate.isNotEmpty(clintTimeSetting.get("clintPartyId"))){
				    clintStartTime = clintTimeSetting.getString("startTime");
					clintEndTime = clintTimeSetting.getString("endTime");
					beforeTimeDuration = clintTimeSetting.getString("beforeTimeDuration");
					afterTimeDuration = clintTimeSetting.getString("afterTimeDuration");					 
				 }
				}
			}
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    //
			partyList=getTotalEmployee(delegator,partyList,orgPartyId);
			partyList=AttendanceUtils.filterValidEmployeeListByDate(delegator, partyList, dayStart);
			logger.debug("Total Employee-------->"+partyList.size());
			int sl=1;
			for(GenericValue party:partyList){
				
				String partyId=party.getString("partyId");
				
				if(AttendanceUtils.checkCardExistanceByPartyId(delegator,partyId))
				{
					List<GenericValue> employeetwoPreviousDayLogs  = new ArrayList<GenericValue>();
					List<EntityExpr> twopexprs = FastList.newInstance();
					
					List<GenericValue> employeePreviousDayLogs  = new ArrayList<GenericValue>();
					List<EntityExpr> pexprs = FastList.newInstance();
				
					List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
					List<EntityExpr> exprs = FastList.newInstance();
					List<String> orderBy = FastList.newInstance();
					orderBy.add("logtimeStamp ASC");

					exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("logtimeStamp"), EntityOperator.LIKE, EntityFunction.UPPER(fromTime + "%")));
					exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId ));
					
					pexprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("logtimeStamp"), EntityOperator.LIKE, EntityFunction.UPPER(formatter.format(previousDate) + "%")));
					pexprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId ));
					
					twopexprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("logtimeStamp"), EntityOperator.LIKE, EntityFunction.UPPER(formatter.format(twoPreviousDate) + "%")));
					twopexprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId ));
					
					try {
						Map<String, String> employee=	FastMap.newInstance();
						employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);					
						employeePreviousDayLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(pexprs, EntityOperator.AND), null, orderBy, null, true);
						employeetwoPreviousDayLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(twopexprs, EntityOperator.AND), null, orderBy, null, true);

						if(employeeLogs.size()>0){

						Date sDate = new Date(employeeLogs.get(0).getTimestamp("logtimeStamp").getTime());	
							//employee.put("SIn", String.valueOf(hourFormat.format(sDate)));
						if(clintPartyId!=null && !clintPartyId.equals("0")){
						    long  clintEntryTimeInDate= AttendanceUtils.getStrngTimeInDate(clintStartTime, sDate);
						    Calendar logcalendar = Calendar.getInstance();
						    logcalendar.setTime(sDate);
						    logcalendar.set(Calendar.SECOND, 0);
						    logcalendar.set(Calendar.MILLISECOND, 0);
								
						    if(logcalendar.getTimeInMillis()<clintEntryTimeInDate){
						    int randomNumber = AttendanceUtils.randInt(1, Integer.parseInt(beforeTimeDuration));
							Date clintDate = new Date(clintEntryTimeInDate-randomNumber*60000);
						    Calendar inTime = Calendar.getInstance();
						    inTime.setTime(clintDate);
						    inTime.set(Calendar.SECOND, 0);
						    inTime.set(Calendar.MILLISECOND, 0);
							employee.put("SIn", String.valueOf(hourFormat.format(inTime.getTime())));				 
						     		
						    }else{
								employee.put("SIn", String.valueOf(hourFormat.format(sDate)));				 
						     	 }
						 }else{
								employee.put("SIn", String.valueOf(hourFormat.format(sDate)));				 
								}                             						
					  }else{
							employee.put("SIn", "0");
						}
						if(employeePreviousDayLogs.size()>0){
							
							Date inDate = new Date(employeePreviousDayLogs.get(0).getTimestamp("logtimeStamp").getTime());
							Date outDate = new Date(employeePreviousDayLogs.get(employeePreviousDayLogs.size()-1).getTimestamp("logtimeStamp").getTime());
					    	//employee.put("PIn", String.valueOf(hourFormat.format(inDate)));
					    	//employee.put("POut", String.valueOf(hourFormat.format(outDate)));
							if(clintPartyId!=null && !clintPartyId.equals("0")){
						    	long  clintEntryTimeInDate= AttendanceUtils.getStrngTimeInDate(clintStartTime, inDate);
						    	long  clintExitTimeInDate= AttendanceUtils.getStrngTimeInDate(clintEndTime, outDate);

						    	Calendar logcalendar = Calendar.getInstance();
						    	logcalendar.setTime(inDate);
						    	logcalendar.set(Calendar.SECOND, 0);
						    	logcalendar.set(Calendar.MILLISECOND, 0);
						    	
						    	Calendar logcalendar1 = Calendar.getInstance();
						    	logcalendar1.setTime(outDate);
						    	logcalendar1.set(Calendar.SECOND, 0);
						    	logcalendar1.set(Calendar.MILLISECOND, 0);
								
						    	 if(logcalendar.getTimeInMillis()<clintEntryTimeInDate){
						    		 int randomNumber = AttendanceUtils.randInt(1, Integer.parseInt(beforeTimeDuration));
									 Date clintDate = new Date(clintEntryTimeInDate-randomNumber*60000);
						    		 Calendar inTime = Calendar.getInstance();
						    		 inTime.setTime(clintDate);
						    		 inTime.set(Calendar.SECOND, 0);
						    		 inTime.set(Calendar.MILLISECOND, 0);
									 employee.put("PIn", String.valueOf(hourFormat.format(inTime.getTime())));				 
						     		
						     	 }else{
										employee.put("PIn", String.valueOf(hourFormat.format(inDate)));				 
						     	 }
						    	 
						    	 if(logcalendar1.getTimeInMillis()>clintExitTimeInDate){
						    		 int randomNumber = AttendanceUtils.randInt(1, Integer.parseInt(afterTimeDuration));
									 Date clintDate = new Date(clintExitTimeInDate+randomNumber*60000);
						    		 Calendar outTime = Calendar.getInstance();
						    		 outTime.setTime(clintDate);
						    		 outTime.set(Calendar.SECOND, 0);
						    		 outTime.set(Calendar.MILLISECOND, 0);
									 employee.put("POut", String.valueOf(hourFormat.format(outTime.getTime())));				 
						     		
						     	 }else{
									employee.put("POut", String.valueOf(hourFormat.format(outDate)));				 
						     	 }
								}else{
									employee.put("PIn", String.valueOf(hourFormat.format(inDate)));	
									employee.put("POut", String.valueOf(hourFormat.format(outDate)));				 

								}

						}else{
							 employee.put("PIn", "0");
							 employee.put("POut", "0");
						}
						
						if(employeetwoPreviousDayLogs.size()>0){
							
							Date tinDate = new Date(employeetwoPreviousDayLogs.get(0).getTimestamp("logtimeStamp").getTime());
							Date toutDate = new Date(employeetwoPreviousDayLogs.get(employeetwoPreviousDayLogs.size()-1).getTimestamp("logtimeStamp").getTime());
                            
							//employee.put("tPIn", String.valueOf(hourFormat.format(tinDate))); 
							//employee.put("tPOut", String.valueOf(hourFormat.format(toutDate)));
							if(clintPartyId!=null && !clintPartyId.equals("0")){
						    	long  clintEntryTimeInDate= AttendanceUtils.getStrngTimeInDate(clintStartTime, tinDate);
						    	long  clintExitTimeInDate= AttendanceUtils.getStrngTimeInDate(clintEndTime, toutDate);

						    	Calendar logcalendar = Calendar.getInstance();
						    	logcalendar.setTime(tinDate);
						    	logcalendar.set(Calendar.SECOND, 0);
						    	logcalendar.set(Calendar.MILLISECOND, 0);
						    	
						    	Calendar logcalendar1 = Calendar.getInstance();
						    	logcalendar1.setTime(toutDate);
						    	logcalendar1.set(Calendar.SECOND, 0);
						    	logcalendar1.set(Calendar.MILLISECOND, 0);
								
						    	 if(logcalendar.getTimeInMillis()<clintEntryTimeInDate){
						    		 int randomNumber = AttendanceUtils.randInt(1, Integer.parseInt(beforeTimeDuration));
									 Date clintDate = new Date(clintEntryTimeInDate-randomNumber*60000);
						    		 Calendar inTime = Calendar.getInstance();
						    		 inTime.setTime(clintDate);
						    		 inTime.set(Calendar.SECOND, 0);
						    		 inTime.set(Calendar.MILLISECOND, 0);
									 employee.put("tPIn", String.valueOf(hourFormat.format(inTime.getTime())));				 
						     		
						     	 }else{
										employee.put("tPIn", String.valueOf(hourFormat.format(tinDate)));				 
						     	 }
						    	 
						    	 if(logcalendar1.getTimeInMillis()>clintExitTimeInDate){
						    		 int randomNumber = AttendanceUtils.randInt(1, Integer.parseInt(afterTimeDuration));
									 Date clintDate = new Date(clintExitTimeInDate+randomNumber*60000);
						    		 Calendar outTime = Calendar.getInstance();
						    		 outTime.setTime(clintDate);
						    		 outTime.set(Calendar.SECOND, 0);
						    		 outTime.set(Calendar.MILLISECOND, 0);
									 employee.put("tPOut", String.valueOf(hourFormat.format(outTime.getTime())));				 
						     		
						     	 }else{
									employee.put("tPOut", String.valueOf(hourFormat.format(toutDate)));				 
						     	 }
								}else{
									employee.put("tPIn", String.valueOf(hourFormat.format(tinDate)));	
									employee.put("tPOut", String.valueOf(hourFormat.format(toutDate)));				 
								}

						}else{
							    employee.put("tPIn", "0");
								employee.put("tPOut", "0");
						}
							
							employee.put("SL", String.valueOf(sl));
							employee.put("EmployeeId", AttendanceUtils.getUserLoginIDFromPartyID(delegator,partyId));							
							String cardId=	AttendanceUtils.getCardIdFromPartyId(delegator,partyId);							
							String designation=	AttendanceUtils.getPartyDesignation(delegator,partyId);
							
							List<String> departmentNameList=FastList.newInstance();
							departmentNameList=AttendanceUtils.getPartyDepartment(delegator,partyId,departmentNameList);
							
							employee.put("Card", cardId);
							employee.put("Designation", designation);
							employee.put("Department", AttendanceUtils.getDepartmentName(departmentNameList));


							GenericValue person = delegator.findByPrimaryKey(
									"Person", UtilMisc.toMap("partyId",partyId.trim()));
	
							if (!UtilValidate.isEmpty(person)){
								String name=AttendanceUtils.avoidNullPointerException((String)person.get("firstName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("middleName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("lastName"));
								employee.put("EmployeeName", name);
							}
							
							employee.put("SelectedDate", fmt.format(fromTime));
							employee.put("PreviousDate", fmt.format(previousDate));
							employee.put("TwoPreviousDate", fmt.format(twoPreviousDate));
							
							List<GenericValue> empSkillList = delegator.findByAnd("PartySkillTest", UtilMisc.toMap("partyId",partyId.trim()));
							if (!UtilValidate.isEmpty(empSkillList)) {
								StringBuffer processList = new StringBuffer();
								 int j=0;
							for(GenericValue empProcess: empSkillList){
							    String workEffortId = 	empProcess.getString("workEffortId");
							    List<GenericValue> workEffortList = delegator.findByAnd("WorkEffort", UtilMisc.toMap("workEffortId",workEffortId.trim()));
							 
							 if (!UtilValidate.isEmpty(workEffortList)) {
								 GenericValue workEffort = EntityUtil.getFirst(workEffortList);
								 processList.append(workEffort.getString("workEffortName"));
								 if(empSkillList.size()>1 && empSkillList.size()-1!=j){
									 processList.append(", ");
								 }

								 employee.put("Process", processList.toString()); 
							   }

							    j++;
							}							
						   }
							threedaysReportList.add(employee);	
							sl++;					
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
			}
			}		
	}
	ArrayList<Map> sortedThreedaysReportList = AttendanceUtils.getSortedList(threedaysReportList);
    int serial =1;
    for(int i=0;i<sortedThreedaysReportList.size();i++){
    	Map<String,String> sortedMap = sortedThreedaysReportList.get(i);
    	sortedMap.put("SL", String.valueOf(serial++));
    	sortedThreedaysReportList.set(i, sortedMap);
    }
    
    result.put("threedaysReportList", sortedThreedaysReportList);
    if(UtilValidate.isNotEmpty(fromTime)){
    	result.put("selectedDate", fmt.format(fromTime));
    }else{
    	result.put("selectedDate", fromDate);

    }
	result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
	result.put(ModelService.SUCCESS_MESSAGE, "Employee 3days attendance List");
	return result;
}

public static String exportThreeDaysReportToExcel(HttpServletRequest request ,
		HttpServletResponse response) {
	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
  	List<Map<String, String>> threedaysReportList = (List<Map<String, String>>) request.getSession().getAttribute("threedaysReportList");

	if(threedaysReportList!=null&&!threedaysReportList.isEmpty()){
			long currentTime=System.currentTimeMillis();
			String SelectedDate= threedaysReportList.get(0).get("SelectedDate");
			String PreviousDate= threedaysReportList.get(0).get("PreviousDate");
			String TwoPreviousDate= threedaysReportList.get(0).get("TwoPreviousDate");
	      
	        String fileName = "Report_"+SelectedDate+"_And_"+PreviousDate+"_"
					+ String.valueOf(currentTime) + ".xls";
			
			// create path for excel file
			String excelFilePath = UtilCommon.getAbsoluteFilePath(request, fileName);
		  
			// prepare for excel
			List<String> columnHeaders = new ArrayList<String>();
		
		    columnHeaders.add(0, "SL");
		    columnHeaders.add(1, "PIN");
		    columnHeaders.add(2, "EmployeeName");	    
		    columnHeaders.add(3, "Card");
		    columnHeaders.add(4, "Designation");
		    columnHeaders.add(5, "Department");
		    columnHeaders.add(6, "InTime["+TwoPreviousDate+"]");
		    columnHeaders.add(7, "OutTime["+TwoPreviousDate+"]");
		    columnHeaders.add(8, "InTime["+PreviousDate+"]");
		    columnHeaders.add(9, "OutTime["+PreviousDate+"]");
		    columnHeaders.add(10, "InTime["+SelectedDate+"]");
		   
		    
		    List<Map<String, String>> reportList=EmployeeExcelExportService.getThreeDaysAttandanceReportInfo(delegator, threedaysReportList);
	        List<List<String>> data = EmployeeExcelExportService.prepareExcelDataForReport(reportList,columnHeaders);

			try {
				ExcelUtils.saveToExcel2(excelFilePath, "employeeThreeDaysReportList", data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return ExcelUtils.downloadExcel(fileName, request, response);
	}else{
		
		return "Data Empty";
	}
}

public static Map<String, Object> findInAttenMissingReport(DispatchContext dctx,
		Map<String, ? extends Object> context) {
	Map<String, Object> result = ServiceUtil.returnSuccess();	
	Delegator delegator = dctx.getDelegator();
	
	String fromDate = (String) context.get("fromDate");
	String orgPartyId = (String) context.get("orgPartyId");
	String orgIdForPreference = (String) context.get("orgPartyId"); 
	TimeZone timeZone=(TimeZone) context.get("timeZone"); 
	Locale locale=(Locale) context.get("locale"); 
	
	List<Map<String, String>> inMissingList=FastList.newInstance();
	DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
	java.sql.Date fromTime=null;
	
	String clintPartyId=null;
	String clintStartTime = null;
	String clintEndTime = null;
	String beforeTimeDuration = null;
	String afterTimeDuration = null;
	List<GenericValue> reportFlags=FastList.newInstance();
	try {
		reportFlags=delegator.findByAnd("ReportFlags", "orgPartyId",orgPartyId);
		if(reportFlags.size()>0){
			 clintPartyId = reportFlags.get(0).getString("clintPartyId");
			if(clintPartyId.equals("0")){
				
			}else{
			 GenericValue clintTimeSetting = delegator.findByPrimaryKey("ClintTimeSetting", UtilMisc.toMap("clintPartyId", clintPartyId));
			 if(UtilValidate.isNotEmpty(clintTimeSetting.get("clintPartyId"))){
			  clintStartTime = clintTimeSetting.getString("startTime");
		      clintEndTime = clintTimeSetting.getString("endTime");
			  beforeTimeDuration = clintTimeSetting.getString("beforeTimeDuration");
			  afterTimeDuration = clintTimeSetting.getString("afterTimeDuration");					 
			 }
			}
		}
	} catch (GenericEntityException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
  	// filter on division
    String division = (String) context.get("division");
	if (UtilValidate.isNotEmpty(division)) {
		  orgPartyId=division;
        }	
	// filter on department
	String department = (String) context.get("department");
	      
    if (UtilValidate.isNotEmpty(department)) {
	   orgPartyId=department;
	   
        }    
    // filter on section
    String section = (String) context.get("section");
      
    if (UtilValidate.isNotEmpty(section)) {
    	  orgPartyId=section; 
    }
    // filter on line
    String line = (String) context.get("line");
    
    if (UtilValidate.isNotEmpty(line)) {
    	  orgPartyId=line;  
    }
    	logger.debug("orgPartyId-------->"+orgPartyId);
	List<GenericValue> partyList=FastList.newInstance();
	
	if(fromDate!=null&&!fromDate.isEmpty()){
					
		if(fromDate!=null && !fromDate.isEmpty()){
			try{
				fromTime=Date.valueOf(fromDate);				
			}catch(Exception e){
				fromTime=new Date(System.currentTimeMillis());
			}
		}else{
			fromTime=new Date(System.currentTimeMillis());
		}			
		java.sql.Date fromDateTime=Date.valueOf(fromDate);
		long TimeInMillisecond=AttendanceUtils.getStartTime(fromDateTime);
		TimeInMillisecond=TimeInMillisecond-86400000;
		Date previousDate = new Date(TimeInMillisecond);
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Timestamp dayStart=AttendanceUtils.getModifiedDayStart(new Timestamp(fromTime.getTime()));
		
			partyList=getTotalEmployee(delegator,partyList,orgPartyId);
			partyList=AttendanceUtils.filterValidEmployeeListByDate(delegator, partyList, dayStart);
			logger.debug("Total Employee-------->"+partyList.size());
			int sl=1;
			for(GenericValue party:partyList){
				
				String partyId=party.getString("partyId");
				
				if(AttendanceUtils.checkCardExistanceByPartyId(delegator,partyId))
				{
					List<GenericValue> employeePreviousDayLogs  = new ArrayList<GenericValue>();
					List<EntityExpr> pexprs = FastList.newInstance();
				
					List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
					List<EntityExpr> exprs = FastList.newInstance();
					List<String> orderBy = FastList.newInstance();
					Set<String> fieldsToSelect = UtilMisc.toSet("logtimeStamp");
					orderBy.add("logtimeStamp ASC");

					exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("logtimeStamp"), EntityOperator.LIKE, EntityFunction.UPPER(fromTime + "%")));
					exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId ));
					
					pexprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("logtimeStamp"), EntityOperator.LIKE, EntityFunction.UPPER(formatter.format(previousDate) + "%")));
					pexprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId ));
										
					try {
						Map<String, String> employee=	FastMap.newInstance();
						employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), fieldsToSelect, orderBy, null, true);					
						employeePreviousDayLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(pexprs, EntityOperator.AND), fieldsToSelect, orderBy, null, true);

						
						if(employeePreviousDayLogs.size()>0){
							
							Date inDate = new Date(employeePreviousDayLogs.get(0).getTimestamp("logtimeStamp").getTime());
							Date outDate = new Date(employeePreviousDayLogs.get(employeePreviousDayLogs.size()-1).getTimestamp("logtimeStamp").getTime());

							String entryTime = AttendanceUtils.getEntryTime(partyId, delegator, inDate, orgIdForPreference);
					    	String exitTime = AttendanceUtils.getExitTime(partyId, delegator, outDate, orgIdForPreference);							 	
					      	
					    	Calendar entryCalendar = Calendar.getInstance();
					    	entryCalendar.setTime(inDate);
					    	entryCalendar.set(Calendar.SECOND, 0);
					    	entryCalendar.set(Calendar.MILLISECOND, 0);
					    	
					    	Calendar exitCalendar = Calendar.getInstance();
					    	exitCalendar.setTime(outDate);
					    	exitCalendar.set(Calendar.SECOND, 0);
					    	exitCalendar.set(Calendar.MILLISECOND, 0);
					      					    	
					    	
					    	long  entryTimeInDate= AttendanceUtils.convertStringTimeToLong(entryTime, inDate);
					    	long  exitTimeInDate= AttendanceUtils.convertStringTimeToLong(exitTime, outDate);					    	
					    	long  interval = exitTimeInDate-entryTimeInDate;
					    	long  middleTimeInOffieHoure = entryTimeInDate + (interval/2);
					    	
					    	 if(entryCalendar.getTimeInMillis()>middleTimeInOffieHoure){
					    		 
					    		 if(employeeLogs.size()>0){

									Date sDate = new Date(employeeLogs.get(0).getTimestamp("logtimeStamp").getTime());
									//employee.put("SIn", String.valueOf(hourFormat.format(sDate)));
									if(clintPartyId!=null && !clintPartyId.equals("0")){
									    long  clintEntryTimeInDate= AttendanceUtils.getStrngTimeInDate(clintStartTime, sDate);
									    Calendar logcalendar = Calendar.getInstance();
									    logcalendar.setTime(sDate);
									    logcalendar.set(Calendar.SECOND, 0);
									    logcalendar.set(Calendar.MILLISECOND, 0);
											
									    if(logcalendar.getTimeInMillis()<clintEntryTimeInDate){
									    int randomNumber = AttendanceUtils.randInt(1, Integer.parseInt(beforeTimeDuration));
										Date clintDate = new Date(clintEntryTimeInDate-randomNumber*60000);
									    Calendar inTime = Calendar.getInstance();
									    inTime.setTime(clintDate);
									    inTime.set(Calendar.SECOND, 0);
									    inTime.set(Calendar.MILLISECOND, 0);
										employee.put("SIn", String.valueOf(hourFormat.format(inTime.getTime())));				 
									     		
									    }else{
											employee.put("SIn", String.valueOf(hourFormat.format(sDate)));				 
									     	 }
									 }else{
											employee.put("SIn", String.valueOf(hourFormat.format(sDate)));				 
											}															 
									}else{
									employee.put("SIn", "0");
									}
					    		 
					    		 Date outDateNew = outDate;
					    		 employee.put("PIn", "0");
								// employee.put("POut", String.valueOf(hourFormat.format(outDateNew)));					    		 
					    		 
					    			if(clintPartyId!=null && !clintPartyId.equals("0")){
								    	long  clintExitTimeInDate= AttendanceUtils.getStrngTimeInDate(clintEndTime, outDateNew);
								    	
								    	Calendar logcalendar1 = Calendar.getInstance();
								    	logcalendar1.setTime(outDateNew);
								    	logcalendar1.set(Calendar.SECOND, 0);
								    	logcalendar1.set(Calendar.MILLISECOND, 0);
								    	 
								    	 if(logcalendar1.getTimeInMillis()>clintExitTimeInDate){
								    		 int randomNumber = AttendanceUtils.randInt(1, Integer.parseInt(afterTimeDuration));
											 Date clintDate = new Date(clintExitTimeInDate+randomNumber*60000);
								    		 Calendar outTime = Calendar.getInstance();
								    		 outTime.setTime(clintDate);
								    		 outTime.set(Calendar.SECOND, 0);
								    		 outTime.set(Calendar.MILLISECOND, 0);
											 employee.put("POut", String.valueOf(hourFormat.format(outTime.getTime())));				 
								     		
								     	 }else{
											employee.put("POut", String.valueOf(hourFormat.format(outDateNew)));				 
								     	 }
										}else{
											 employee.put("POut", String.valueOf(hourFormat.format(outDateNew)));				 
										}					    		 
								 
								 employee.put("SL", String.valueOf(sl));
								 employee.put("EmployeeId", AttendanceUtils.getUserLoginIDFromPartyID(delegator,partyId));							
								 String cardId=	AttendanceUtils.getCardIdFromPartyId(delegator,partyId);							
								 String designation=	AttendanceUtils.getPartyDesignation(delegator,partyId);
								 
								 List<String> departmentNameList=FastList.newInstance();
								 departmentNameList=AttendanceUtils.getPartyDepartment(delegator,partyId,departmentNameList);
									
								 employee.put("Card", cardId);
								 employee.put("Designation", designation);
								 employee.put("Department", AttendanceUtils.getDepartmentName(departmentNameList));
								 
								 GenericValue person = delegator.findByPrimaryKey(
											"Person", UtilMisc.toMap("partyId",partyId.trim()));
			
								if (!UtilValidate.isEmpty(person)){
								String name=AttendanceUtils.avoidNullPointerException((String)person.get("firstName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("middleName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("lastName"));
								employee.put("EmployeeName", name);
								}
								
								employee.put("SelectedDate", fmt.format(fromTime));
								employee.put("PreviousDate", fmt.format(previousDate));
								
								
								List<GenericValue> empSkillList = delegator.findByAnd("PartySkillTest", UtilMisc.toMap("partyId",partyId.trim()));
								if (!UtilValidate.isEmpty(empSkillList)) {
									StringBuffer processList = new StringBuffer();
									 int j=0;
								for(GenericValue empProcess: empSkillList){
								    String workEffortId = 	empProcess.getString("workEffortId");
								    List<GenericValue> workEffortList = delegator.findByAnd("WorkEffort", UtilMisc.toMap("workEffortId",workEffortId.trim()));
								 
								 if (!UtilValidate.isEmpty(workEffortList)) {
									 GenericValue workEffort = EntityUtil.getFirst(workEffortList);
									 processList.append(workEffort.getString("workEffortName"));
									 if(empSkillList.size()>1 && empSkillList.size()-1!=j){
										 processList.append(", ");
									 }

									 employee.put("Process", processList.toString()); 
								   }

								    j++;
								}							
							   }
									
								inMissingList.add(employee);	
								sl++;					     						     		
					     	 }

						}						
					
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
			}
			}
		
	}
	
	ArrayList<Map> sortedInMissingList = AttendanceUtils.getSortedList(inMissingList);
    int serial =1;
    for(int i=0;i<sortedInMissingList.size();i++){
    	Map<String,String> sortedMap = sortedInMissingList.get(i);
    	sortedMap.put("SL", String.valueOf(serial++));
    	sortedInMissingList.set(i, sortedMap);
    }
    
    result.put("inMissingList", sortedInMissingList);
    if(UtilValidate.isNotEmpty(fromTime)){
    	result.put("selectedDate", fmt.format(fromTime));
    }else{
    	result.put("selectedDate", fromDate);

    }
	result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
	result.put(ModelService.SUCCESS_MESSAGE, "Employee Attendance Missing List");
	return result;
}

public static String exportInMissingListReportToExcel(HttpServletRequest request ,
		HttpServletResponse response) {
	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
  	List<Map<String, String>> inMissingList = (List<Map<String, String>>) request.getSession().getAttribute("inMissingList");

	if(inMissingList!=null&&!inMissingList.isEmpty()){
			long currentTime=System.currentTimeMillis();
			String SelectedDate= inMissingList.get(0).get("SelectedDate");
			String PreviousDate= inMissingList.get(0).get("PreviousDate");
	      
	        String fileName = "Report_"+SelectedDate+"_And_"+PreviousDate+"_"
					+ String.valueOf(currentTime) + ".xls";
			
			// create path for excel file
			String excelFilePath = UtilCommon.getAbsoluteFilePath(request, fileName);
		  
			// prepare for excel
			List<String> columnHeaders = new ArrayList<String>();
		
		    columnHeaders.add(0, "SL");
		    columnHeaders.add(1, "PIN");
		    columnHeaders.add(2, "EmployeeName");	    
		    columnHeaders.add(3, "Card");
		    columnHeaders.add(4, "Designation");
		    columnHeaders.add(4, "Department");
		    columnHeaders.add(5, "InTime["+PreviousDate+"]");
		    columnHeaders.add(6, "OutTime["+PreviousDate+"]");
		    columnHeaders.add(7, "InTime["+SelectedDate+"]");
		   
		    
		    List<Map<String, String>> reportList=EmployeeExcelExportService.getAttandanceMissingInfo(delegator, inMissingList);
	        List<List<String>> data = EmployeeExcelExportService.prepareExcelDataForReport(reportList,columnHeaders);

			try {
				ExcelUtils.saveToExcel2(excelFilePath, "employeeInTimeAttenMissingList", data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return ExcelUtils.downloadExcel(fileName, request, response);
	}else{
		
		return "Data Empty";
	}
}



public static Map<String, Object> findOutAttenMissingReport(DispatchContext dctx,
		Map<String, ? extends Object> context) {
	Map<String, Object> result = ServiceUtil.returnSuccess();
	
	Delegator delegator = dctx.getDelegator();	
	String fromDate = (String) context.get("fromDate");
	String orgPartyId = (String) context.get("orgPartyId");
	String orgIdForPreference = (String) context.get("orgPartyId"); 
	TimeZone timeZone=(TimeZone) context.get("timeZone"); 
	Locale locale=(Locale) context.get("locale"); 
	
	List<Map<String, String>> outMissingList=FastList.newInstance();	
	SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
	DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
	java.sql.Date fromTime=null;

	String clintPartyId=null;
	String clintStartTime = null;
	String clintEndTime = null;
	String beforeTimeDuration = null;
	String afterTimeDuration = null;
	List<GenericValue> reportFlags=FastList.newInstance();
	try {
		reportFlags=delegator.findByAnd("ReportFlags", "orgPartyId",orgPartyId);
		if(reportFlags.size()>0){
			 clintPartyId = reportFlags.get(0).getString("clintPartyId");
			if(clintPartyId.equals("0")){
				
			}else{
			 GenericValue clintTimeSetting = delegator.findByPrimaryKey("ClintTimeSetting", UtilMisc.toMap("clintPartyId", clintPartyId));
			 if(UtilValidate.isNotEmpty(clintTimeSetting.get("clintPartyId"))){
			  clintStartTime = clintTimeSetting.getString("startTime");
		      clintEndTime = clintTimeSetting.getString("endTime");
			  beforeTimeDuration = clintTimeSetting.getString("beforeTimeDuration");
			  afterTimeDuration = clintTimeSetting.getString("afterTimeDuration");					 
			 }
			}
		}
	} catch (GenericEntityException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
  	// filter on division
    String division = (String) context.get("division");
	if (UtilValidate.isNotEmpty(division)) {
		  orgPartyId=division;
        }
	
	// filter on department
	String department = (String) context.get("department");
	      
    if (UtilValidate.isNotEmpty(department)) {
	   orgPartyId=department;
	   
        }   
    // filter on section
    String section = (String) context.get("section");
      
    if (UtilValidate.isNotEmpty(section)) {
    	  orgPartyId=section; 
    }
    // filter on line
    String line = (String) context.get("line");
    
    if (UtilValidate.isNotEmpty(line)) {
    	  orgPartyId=line;  
    }
    	logger.debug("orgPartyId-------->"+orgPartyId);
	List<GenericValue> partyList=FastList.newInstance();
	
	if(fromDate!=null&&!fromDate.isEmpty()){
			
		if(fromDate!=null && !fromDate.isEmpty()){
			try{
				fromTime=Date.valueOf(fromDate);
				
			}catch(Exception e){
				fromTime=new Date(System.currentTimeMillis());
			}
		}else{
			fromTime=new Date(System.currentTimeMillis());
		}
			
		java.sql.Date fromDateTime=Date.valueOf(fromDate);
		long TimeInMillisecond=AttendanceUtils.getStartTime(fromDateTime);
		TimeInMillisecond=TimeInMillisecond-86400000;
		Date previousDate = new Date(TimeInMillisecond);
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Timestamp dayStart=AttendanceUtils.getModifiedDayStart(new Timestamp(fromTime.getTime()));
		
			partyList=getTotalEmployee(delegator,partyList,orgPartyId);
			partyList=AttendanceUtils.filterValidEmployeeListByDate(delegator, partyList, dayStart);
			logger.debug("Total Employee-------->"+partyList.size());
			int sl=1;
			for(GenericValue party:partyList){
				
				String partyId=party.getString("partyId");
				
				if(AttendanceUtils.checkCardExistanceByPartyId(delegator,partyId))
				{
					List<GenericValue> employeePreviousDayLogs  = new ArrayList<GenericValue>();
					List<EntityExpr> pexprs = FastList.newInstance();
				
					List<GenericValue> employeeLogs  = new ArrayList<GenericValue>();
					List<EntityExpr> exprs = FastList.newInstance();
					List<String> orderBy = FastList.newInstance();
					orderBy.add("logtimeStamp ASC");

					exprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("logtimeStamp"), EntityOperator.LIKE, EntityFunction.UPPER(fromTime + "%")));
					exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId ));
					
					pexprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("logtimeStamp"), EntityOperator.LIKE, EntityFunction.UPPER(formatter.format(previousDate) + "%")));
					pexprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId ));
										
					try {
						Map<String, String> employee=	FastMap.newInstance();
						employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, true);					
						employeePreviousDayLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(pexprs, EntityOperator.AND), null, orderBy, null, true);

						
						if(employeePreviousDayLogs.size()>0){
							
							Date inDate = new Date(employeePreviousDayLogs.get(0).getTimestamp("logtimeStamp").getTime());
							Date outDate = new Date(employeePreviousDayLogs.get(employeePreviousDayLogs.size()-1).getTimestamp("logtimeStamp").getTime());

							String entryTime = AttendanceUtils.getEntryTime(partyId, delegator, inDate, orgIdForPreference);
					    	String exitTime = AttendanceUtils.getExitTime(partyId, delegator, outDate, orgIdForPreference);							 	
					      	
					    	Calendar entryCalendar = Calendar.getInstance();
					    	entryCalendar.setTime(inDate);
					    	entryCalendar.set(Calendar.SECOND, 0);
					    	entryCalendar.set(Calendar.MILLISECOND, 0);
					    	
					    	Calendar exitCalendar = Calendar.getInstance();
					    	exitCalendar.setTime(outDate);
					    	exitCalendar.set(Calendar.SECOND, 0);
					    	exitCalendar.set(Calendar.MILLISECOND, 0);
					      					    	
					    	
					    	long  entryTimeInDate= AttendanceUtils.convertStringTimeToLong(entryTime, inDate);
					    	long  exitTimeInDate= AttendanceUtils.convertStringTimeToLong(exitTime, outDate);					    	
					    	long  interval = exitTimeInDate-entryTimeInDate;
					    	long  middleTimeInOffieHoure = entryTimeInDate + (interval/2);
					    	
					    	 if(exitCalendar.getTimeInMillis()<middleTimeInOffieHoure){
					    		 
					    		 if(employeeLogs.size()>0){

									Date sDate = new Date(employeeLogs.get(0).getTimestamp("logtimeStamp").getTime());
									//employee.put("SIn", String.valueOf(hourFormat.format(sDate)));
									if(clintPartyId!=null && !clintPartyId.equals("0")){
									    long  clintEntryTimeInDate= AttendanceUtils.getStrngTimeInDate(clintStartTime, sDate);
									    Calendar logcalendar = Calendar.getInstance();
									    logcalendar.setTime(sDate);
									    logcalendar.set(Calendar.SECOND, 0);
									    logcalendar.set(Calendar.MILLISECOND, 0);
											
									    if(logcalendar.getTimeInMillis()<clintEntryTimeInDate){
									    int randomNumber = AttendanceUtils.randInt(1, Integer.parseInt(beforeTimeDuration));
										Date clintDate = new Date(clintEntryTimeInDate-randomNumber*60000);
									    Calendar inTime = Calendar.getInstance();
									    inTime.setTime(clintDate);
									    inTime.set(Calendar.SECOND, 0);
									    inTime.set(Calendar.MILLISECOND, 0);
										employee.put("SIn", String.valueOf(hourFormat.format(inTime.getTime())));				 
									     		
									    }else{
											employee.put("SIn", String.valueOf(hourFormat.format(sDate)));				 
									     	 }
									 }else{
											employee.put("SIn", String.valueOf(hourFormat.format(sDate)));				 
											}
															 
									}else{
									employee.put("SIn", "0");
									}
					    		 
					    		 Date inDateNew = inDate;
					    		// employee.put("PIn", String.valueOf(hourFormat.format(inDateNew)));
								 employee.put("POut", "0");
								 if(clintPartyId!=null && !clintPartyId.equals("0")){
								    	long  clintExitTimeInDate= AttendanceUtils.getStrngTimeInDate(clintEndTime, inDateNew);
								    	
								    	Calendar logcalendar1 = Calendar.getInstance();
								    	logcalendar1.setTime(inDateNew);
								    	logcalendar1.set(Calendar.SECOND, 0);
								    	logcalendar1.set(Calendar.MILLISECOND, 0);
								    	 
								    	 if(logcalendar1.getTimeInMillis()>clintExitTimeInDate){
								    		 int randomNumber = AttendanceUtils.randInt(1, Integer.parseInt(beforeTimeDuration));
											 Date clintDate = new Date(clintExitTimeInDate+randomNumber*60000);
								    		 Calendar inTime = Calendar.getInstance();
								    		 inTime.setTime(clintDate);
								    		 inTime.set(Calendar.SECOND, 0);
								    		 inTime.set(Calendar.MILLISECOND, 0);
											 employee.put("PIn", String.valueOf(hourFormat.format(inTime.getTime())));				 
								     		
								     	 }else{
											employee.put("PIn", String.valueOf(hourFormat.format(inDateNew)));				 
								     	 }
										}else{
											 employee.put("PIn", String.valueOf(hourFormat.format(inDateNew)));				 
										}
								 
								 employee.put("SL", String.valueOf(sl));
								 employee.put("EmployeeId", AttendanceUtils.getUserLoginIDFromPartyID(delegator,partyId));							
								 String cardId=	AttendanceUtils.getCardIdFromPartyId(delegator,partyId);							
								 String designation=	AttendanceUtils.getPartyDesignation(delegator,partyId);
								 
								 List<String> departmentNameList=FastList.newInstance();
								 departmentNameList=AttendanceUtils.getPartyDepartment(delegator,partyId,departmentNameList);
									
								 employee.put("Card", cardId);
								 employee.put("Designation", designation);
								 employee.put("Department", AttendanceUtils.getDepartmentName(departmentNameList));
								 
								 GenericValue person = delegator.findByPrimaryKey(
											"Person", UtilMisc.toMap("partyId",partyId.trim()));
			
								if (!UtilValidate.isEmpty(person)){
								String name=AttendanceUtils.avoidNullPointerException((String)person.get("firstName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("middleName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("lastName"));
								employee.put("EmployeeName", name);
								}
									
								employee.put("SelectedDate", fmt.format(fromTime));
								employee.put("PreviousDate", fmt.format(previousDate));
								
								List<GenericValue> empSkillList = delegator.findByAnd("PartySkillTest", UtilMisc.toMap("partyId",partyId.trim()));
								if (!UtilValidate.isEmpty(empSkillList)) {
									StringBuffer processList = new StringBuffer();
									 int j=0;
								for(GenericValue empProcess: empSkillList){
								    String workEffortId = 	empProcess.getString("workEffortId");
								    List<GenericValue> workEffortList = delegator.findByAnd("WorkEffort", UtilMisc.toMap("workEffortId",workEffortId.trim()));
								 
								 if (!UtilValidate.isEmpty(workEffortList)) {
									 GenericValue workEffort = EntityUtil.getFirst(workEffortList);
									 processList.append(workEffort.getString("workEffortName"));
									 if(empSkillList.size()>1 && empSkillList.size()-1!=j){
										 processList.append(", ");
									 }

									 employee.put("Process", processList.toString()); 
								   }

								    j++;
								}							
							   }
									
								outMissingList.add(employee);	
								sl++;					     						     		
					     	 }

						}						
					
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
			}
			}
		
	}
	ArrayList<Map> sortedOutMissingList = AttendanceUtils.getSortedList(outMissingList);
    int serial =1;
    for(int i=0;i<sortedOutMissingList.size();i++){
    	Map<String,String> sortedMap = sortedOutMissingList.get(i);
    	sortedMap.put("SL", String.valueOf(serial++));
    	sortedOutMissingList.set(i, sortedMap);
    }
    
    result.put("outMissingList", sortedOutMissingList);
    if(UtilValidate.isNotEmpty(fromTime)){
    	result.put("selectedDate", fmt.format(fromTime));
    }else{
    	result.put("selectedDate", fromDate);

    }	result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
	result.put(ModelService.SUCCESS_MESSAGE, "Employee Attendance Missing List");
	return result;
}

public static String exportOutMissingListReportToExcel(HttpServletRequest request ,
		HttpServletResponse response) {
	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
  	List<Map<String, String>> outMissingList = (List<Map<String, String>>) request.getSession().getAttribute("outMissingList");

	if(outMissingList!=null&&!outMissingList.isEmpty()){
			long currentTime=System.currentTimeMillis();
			String SelectedDate= outMissingList.get(0).get("SelectedDate");
			String PreviousDate= outMissingList.get(0).get("PreviousDate");
	      
	        String fileName = "OutMissing("+PreviousDate+")"
					+ String.valueOf(currentTime) + ".xls";
			
			// create path for excel file
			String excelFilePath = UtilCommon.getAbsoluteFilePath(request, fileName);
		  
			// prepare for excel
			List<String> columnHeaders = new ArrayList<String>();
		
		    columnHeaders.add(0, "SL");
		    columnHeaders.add(1, "PIN");
		    columnHeaders.add(2, "EmployeeName");	    
		    columnHeaders.add(3, "Card");
		    columnHeaders.add(4, "Designation");
		    columnHeaders.add(4, "Department");
		    columnHeaders.add(5, "InTime["+PreviousDate+"]");
		    columnHeaders.add(6, "OutTime["+PreviousDate+"]");
		    columnHeaders.add(7, "InTime["+SelectedDate+"]");
		   
		    
		    List<Map<String, String>> reportList=EmployeeExcelExportService.getAttandanceMissingInfo(delegator, outMissingList);
	        List<List<String>> data = EmployeeExcelExportService.prepareExcelDataForReport(reportList,columnHeaders);

			try {
				ExcelUtils.saveToExcel2(excelFilePath, "employeeOutTimeAttenMissingList", data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return ExcelUtils.downloadExcel(fileName, request, response);
	}else{
		
		return "Data Empty";
	}
}

//*End of Parvez code*

// *Tomal Start code*
/**
* This method is use to get Total Employee Position
* @param orgPartyId String, emplPositionId String, emplPositionTypeId String 
* @return Map<String,Long> result of total employee position. 
* @author tomal mahdi
* */	
public static Map<String, Object> getTotalEmplPosition(DispatchContext dctx,
		Map<String, ? extends Object> context) {
	Map<String, Object> result = ServiceUtil.returnSuccess();
	
	Delegator delegator = dctx.getDelegator();
	
	String orgPartyId = (String) context.get("orgPartyId");
	String emplPositionId = (String) context.get("emplPositionId");
	String emplPositionTypeId = (String) context.get("emplPositionTypeId"); 
	
	List<GenericValue> listIt = FastList.newInstance();
	/*logger.debug("EmplPosition: "+emplPositionId+" "+emplPositionTypeId+" org: "+orgPartyId);*/
	

	List<GenericValue> empPosition = new ArrayList<GenericValue>();
	
	
	int count = 0;
	empPosition = AttendanceUtils.getTotalPositions(delegator,empPosition,orgPartyId);
	
	for(GenericValue empposition : empPosition){
		count++;
		String emplPositionType = empposition.getString("emplPositionTypeId");
		String empPositionId = empposition.getString("emplPositionId");
		
		if(emplPositionTypeId==null ||emplPositionTypeId.equals("")){
			listIt.add(empposition);
		}else{
			if(emplPositionTypeId.equals(emplPositionType)){
				listIt.add(empposition);
			}
		}
	}
	/*logger.debug("Counter : "+count);*/
	result.put("listIt", listIt);

return result; 
}
//*Tomal End code*

/** 
 * @param 
 * @return 
 * @author zzz
 * @throws GenericEntityException 
 * 
**/
public static List<GenericValue> getTotalEmployeeByPositionAndEmpType(Delegator delegator,List<GenericValue> totalEmployee,String partyId,String employmentType){
	List<GenericValue> children=getChildren(delegator, partyId);
	
	List<GenericValue> positions=getPositions(delegator, partyId);
	
	for(GenericValue child:children){

		totalEmployee=getTotalEmployeeByPositionAndEmpType(delegator,totalEmployee,child.getString("partyIdTo"),employmentType);
	}
	
	if(positions!=null){
		for(GenericValue position:positions){
			totalEmployee.addAll(AttendanceUtils.getEmployeeByPositionAndEmploymentType(delegator, position.getString("emplPositionId"),employmentType));
		}
	}
	return totalEmployee;
	
}


/**
 * @see This method find employee by position & employeementType
 * @author zzz
 * @param delegator
 * @param positionId,employmentType
 * @return employee list
 */
public static List<GenericValue> getTotalEmployeeByEmploymentType(Delegator delegator,List<GenericValue> totalEmployee,String partyId,String employmentType){
	List<GenericValue> children=getChildren(delegator, partyId);
	
	List<GenericValue> positions=getPositions(delegator, partyId);
	
	for(GenericValue child:children){

		totalEmployee=getTotalEmployeeByEmploymentType(delegator,totalEmployee,child.getString("partyIdTo"),employmentType);
	}
	
	if(positions!=null){
		for(GenericValue position:positions){
			totalEmployee.addAll(AttendanceUtils.getEmployeeByPositionAndEmploymentType(delegator, position.getString("emplPositionId"),employmentType));
		}
	}
	return totalEmployee;
	
}

/** 
 * @param Delegator delegator, List<GenericValue> totalEmployee, String partyId  
 * @return 
 * @author TomalMahdi
 * @throws GenericEntityException 
 * 
**/
public static List<GenericValue> getTotalManagerByPositionAndEmpType(Delegator delegator,List<GenericValue> totalEmployee,String partyId){
	List<GenericValue> children=getChildren(delegator, partyId);
	List<GenericValue> positions=getPositions(delegator, partyId);
	for(GenericValue child:children){
		totalEmployee=getTotalManagerByPositionAndEmpType(delegator,totalEmployee,child.getString("partyIdTo"));
	}
	if(positions!=null){
		for(GenericValue position:positions){
			totalEmployee.addAll(AttendanceUtils.getManagersByPosition(delegator, position.getString("emplPositionId")));
		}
	}
	return totalEmployee;
	
}

}
