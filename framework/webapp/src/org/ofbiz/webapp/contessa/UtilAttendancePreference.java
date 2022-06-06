package org.ofbiz.webapp.contessa;



import java.sql.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author zzz
 * @author zzz
 * 
 */
public class UtilAttendancePreference {
	public static final String module = UtilAttendancePreference.class.getName();
	private static Logger logger=Logger.getLogger("UtilAttendancePreference");

			
			/**
			 * Check from Date and thru Date overlap
			 * @author zzz
			 * @param delegator
			 * @param fromDate
			 * @param thrueDate
			 * @return
			 */
			public static boolean checkExistingDesignPrefByDate(Delegator delegator,Date fromDate,String emplPositionId,GenericValue designationPreference){
				
				EntityCondition codition=EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
		                EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate),
		                EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
		                EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS, emplPositionId)
		        ), EntityJoinOperator.AND);
			
				try {
					List<GenericValue> designAttenPrefs=delegator.findList("DesignPrefEmplposition", codition, null, null, null, false);
					
					if(designAttenPrefs!=null&&designAttenPrefs.size()>0){
						if(designationPreference!=null){
							designAttenPrefs.remove(designationPreference);
						}
						if(designAttenPrefs.size()>0){
								return true;
							}
					}
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return false;
			}
			
			/**
			 * @author zzz
			 * @param delegator
			 * @param employeeId
			 * @return
			 */
			public static int getMaxDesignPrefRevision(Delegator delegator,String emplPositionId){
				int designEmplposRevesion=0;
				try {
					List<GenericValue> emPrefes=delegator.findByAnd("DesignPrefEmplposition", UtilMisc.toMap("emplPositionId", emplPositionId));
					if(emPrefes!=null){
						for(GenericValue emPrefe:emPrefes){
							int revesion=Integer.valueOf(emPrefe.getString("designEmplposRevesion"));
							if(revesion>designEmplposRevesion){
								designEmplposRevesion=revesion;
							}
						}
					}
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return designEmplposRevesion;
			}
			
		
			/**
			 * Check from Date and thru Date overlap
			 * @author zzz
			 * @param delegator
			 * @param fromDate
			 * @param thrueDate
			 * @return
			 */
			public static boolean checkExistingOrgPrefByDate(Delegator delegator,Date fromDate,String orgPartyId,GenericValue orgAttendancePreference){
				
				EntityCondition codition=EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
		                EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate),
		                EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
		                EntityCondition.makeCondition("orgPartyId", EntityOperator.EQUALS, orgPartyId)
		        ), EntityJoinOperator.AND);
			
				try {
					List<GenericValue> orgAttenPrefs=delegator.findList("OrgAttendancePreference", codition, null, null, null, false);
					if(orgAttenPrefs!=null&&orgAttenPrefs.size()>0){
						if(orgAttendancePreference!=null){
							orgAttenPrefs.remove(orgAttendancePreference);
						}
						if(orgAttenPrefs.size()>0){
							return true;
						}
						
					}
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return false;
			}
			
			/**
			 * @author zzz
			 * @param delegator
			 * @param employeeId
			 * @return
			 */
			public static int getMaxOrgPrefRevision(Delegator delegator,String orgPartyId){
				int orgPrefRevision=0;
				try {
					List<GenericValue> orgPrefes=delegator.findByAnd("OrgAttendancePreference", UtilMisc.toMap("orgPartyId", orgPartyId));
					if(orgPrefes!=null){
						for(GenericValue emPrefe:orgPrefes){
							int revesion=Integer.valueOf(emPrefe.getString("orgPrefRevision"));
							if(revesion>orgPrefRevision){
								orgPrefRevision=revesion;
							}
						}
					}
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return orgPrefRevision;
			}

		
		/**
		 * Check from Date and thru Date overlap
		 * @author zzz
		 * @param delegator
		 * @param fromDate
		 * @param thrueDate
		 * @return
		 */
		public static boolean checkExistingEmPrefByDate(Delegator delegator,Date fromDate,String partyId,GenericValue employeeAttendancePreference){
			
			EntityCondition codition=EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
	                EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate),
	                EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
	                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)
	        ), EntityJoinOperator.AND);
		
			try {
				List<GenericValue> empAttenPrefs=delegator.findList("EmployeeAttendancePreference", codition, null, null, null, false);
				if(empAttenPrefs!=null&&empAttenPrefs.size()>0){
					if(employeeAttendancePreference!=null){
						empAttenPrefs.remove(employeeAttendancePreference);
					}
					if(empAttenPrefs.size()>0){
						return true;	
					}
					
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return false;
		}
		
		/**
		 * @author zzz
		 * @param delegator
		 * @param employeeId
		 * @return
		 */
		public static int getMaxEmpPrefRevision(Delegator delegator,String employeeId,String orgPartyId){
			int empPrefRevision=0;
			try {
				List<GenericValue> emPrefes=delegator.findByAnd("EmployeeAttendancePreference", UtilMisc.toMap("orgPartyId", orgPartyId, "employeeId", employeeId));
				if(emPrefes!=null){
					for(GenericValue emPrefe:emPrefes){
						int revesion=Integer.valueOf(emPrefe.getString("empPrefRevision"));
						if(revesion>empPrefRevision){
							empPrefRevision=revesion;
						}
					}
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return empPrefRevision;
		}
		

		/**
		 * @author zzz
		 * @param delegator
		 * @param orgPartyId
		 * @return
		 */
		public static int getMaxRosterPrefRevision(Delegator delegator,String orgPartyId,String rosterStartTime, String rosterEndTime){
			int rosterPrefRevision=0;
			try {
				List<GenericValue> rosterPrefes=delegator.findByAnd("AttendanceRosterPreference", UtilMisc.toMap("orgPartyId", orgPartyId,"rosterStartTime",rosterStartTime,"rosterEndTime",rosterEndTime));
				if(rosterPrefes!=null){
					for(GenericValue rosterPrefe:rosterPrefes){
						int revesion=Integer.valueOf(rosterPrefe.getString("rosterPrefRevision"));
						if(revesion>rosterPrefRevision){
							rosterPrefRevision=revesion;
						}
					}
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return rosterPrefRevision;
		}
		
		/**
		 * @author zzz
		 * @param delegator
		 * @param orgPartyId
		 * @param partyId
		 * @param rosterPreferenceId
		 * @return
		 */
		public static int getMaxRosterEmpRevision(Delegator delegator,String orgPartyId,String partyId, String rosterPreferenceId,String rosterPrefRevision){
			int rosterEmpRevesion=0;
			try {
				List<GenericValue> rosterEmps=delegator.findByAnd("RosterEmployee", UtilMisc.toMap("orgPartyId", orgPartyId,"partyId",partyId,"rosterPreferenceId",rosterPreferenceId,"rosterPrefRevision",rosterPrefRevision));
				if(rosterEmps!=null){
					for(GenericValue rosterPrefe:rosterEmps){
						int revesion=Integer.valueOf(rosterPrefe.getString("rosterEmpRevesion"));
						if(revesion>rosterEmpRevesion){
							rosterEmpRevesion=revesion;
						}
					}
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return rosterEmpRevesion;
		}
		
		
		/**
		 * Check from Date and thru Date overlap
		 * @author zzz
		 * @param delegator
		 * @return
		 */
		public static boolean checkExistingRosterPrefByTime(Delegator delegator,String orgPartyId,String rosterStartTime,String rosterEndTime,GenericValue rosterAttendancePreference){
			
			EntityCondition codition=EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
	                EntityCondition.makeCondition("rosterStartTime", EntityOperator.EQUALS, rosterStartTime),
	                EntityCondition.makeCondition("rosterEndTime", EntityOperator.EQUALS, rosterEndTime),
	                EntityCondition.makeCondition("orgPartyId", EntityOperator.EQUALS, orgPartyId)
	        ), EntityJoinOperator.AND);
		
			try {
				List<GenericValue> rosterAttenPrefs=delegator.findList("AttendanceRosterPreference", codition, null, null, null, false);
				if(rosterAttenPrefs!=null&&rosterAttenPrefs.size()>0){
					if(rosterAttendancePreference!=null){
						rosterAttenPrefs.remove(rosterAttendancePreference);
					}
					if(rosterAttenPrefs.size()>0){
						return true;
					}
					
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return false;
		}
		
		
		/**
		 * @author zzz
		 * @param delegator
		 * @param fromDate
		 * @param orgPartyId
		 * @param employeeId
		 * @param partyId
		 * @return
		 */
		public static boolean checkExistingRosterEmpByDate(Delegator delegator,Date fromDate,String orgPartyId,String employeeId,String partyId,GenericValue rosterEmp){
			
			EntityCondition codition=EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
	                EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate),
	                EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
	                EntityCondition.makeCondition("employeeId", EntityOperator.EQUALS, employeeId),
	                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
	                EntityCondition.makeCondition("orgPartyId", EntityOperator.EQUALS, orgPartyId)
	        ), EntityJoinOperator.AND);
		
			try {
				List<GenericValue> rosterEmployees=delegator.findList("RosterEmployee", codition, null, null, null, false);
				if(rosterEmployees!=null&&rosterEmployees.size()>0){
					
					if(rosterEmp!=null){
						rosterEmployees.remove(rosterEmp);
					}
					if(rosterEmployees.size()>0){
						return true;
					}
					
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return false;
		}
		
		/**
		 * @author zzz
		 * @param partyId
		 * @param delegator
		 * @param date
		 * @param orgPartyId
		 * @param selectedProperty
		 * @return value of selectedProperty
		 * @throws GenericEntityException 
		 */
		public static String getPreferencePropertyValue(String partyId,Delegator delegator,Date date,String orgPartyId,String selectedProperty) throws GenericEntityException{
			
			String value="";
			
			GenericValue dayPreference=getDayPreference(delegator, date, orgPartyId);
			GenericValue employeePreference =getEmployeePreference(partyId,delegator,date,orgPartyId);
			GenericValue designationPreference = getDesignationPreference(partyId,delegator,date,orgPartyId);
			GenericValue rosterPreference=getRosterPreference(partyId,delegator,date,orgPartyId);
			GenericValue OrganizationPreference = getOrganizationPreference(delegator,date,orgPartyId);
			
			if (dayPreference != null && !dayPreference.isEmpty() && dayPreference.containsKey(selectedProperty)) {
			    if (rosterPreference != null && !rosterPreference.isEmpty()) {
					value=rosterPreference.getString(selectedProperty);
				}else{
					value=dayPreference.getString(selectedProperty);	
				}
			}else if(UtilValidate.isNotEmpty(employeePreference) && employeePreference.containsKey(selectedProperty)){
				
				   value=employeePreference.getString(selectedProperty);
			}else if(UtilValidate.isNotEmpty(rosterPreference) && rosterPreference.containsKey(selectedProperty)){
				
				   value=rosterPreference.getString(selectedProperty);
			}else if(UtilValidate.isNotEmpty(designationPreference) && designationPreference.containsKey(selectedProperty)){
				
				   value=designationPreference.getString(selectedProperty);
			}else if(OrganizationPreference!=null){
				
				   value=OrganizationPreference.getString(selectedProperty);
			}
						
			return value;
		}
		
		/**
		 * @author zzz
		 * @param delegator
		 * @param date
		 * @param orgPartyId
		 * @param selectedProperty
		 * @return dayAttendancePreference
		 * @throws GenericEntityException
		 */
		public static GenericValue getDayPreference(Delegator delegator,Date date,String orgPartyId) throws GenericEntityException{

			GenericValue dayAttendancePreference=null;
			List<GenericValue> dayAttendancePreferences = delegator.findByAnd("DayAttendancePreference", UtilMisc.toMap("dateId",date,"orgPartyId",orgPartyId));
			 
			if (UtilValidate.isNotEmpty(dayAttendancePreferences)) {
				dayAttendancePreference = EntityUtil.getFirst(dayAttendancePreferences);
			}
			
			return dayAttendancePreference;
		}
		/**
		 * @author zzz
		 * @param delegator
		 * @param date
		 * @param orgPartyId
		 * @param selectedProperty
		 * @return employeePreference
		 * @throws GenericEntityException
		 */
		public static GenericValue getEmployeePreference(String partyId,Delegator delegator,Date date,String orgPartyId) throws GenericEntityException{
			GenericValue employeeAttendancePreference=null;
			EntityCondition empPrefCodition=EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
	                EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, date),
	                EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, date),
	                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
	                EntityCondition.makeCondition("orgPartyId", EntityOperator.EQUALS, orgPartyId)
	        ), EntityJoinOperator.AND);
						
			List<GenericValue> employeeAttendancePreferences=delegator.findList("EmployeeAttendancePreference", empPrefCodition, null, null, null, false);
			if (UtilValidate.isNotEmpty(employeeAttendancePreferences)) {
			    employeeAttendancePreference = EntityUtil.getFirst(employeeAttendancePreferences);
			}
			
			return employeeAttendancePreference;
		}
		/**
		 * @author zzz
		 * @param delegator
		 * @param date
		 * @param orgPartyId
		 * @param selectedProperty
		 * @return RosterPreference
		 * @throws GenericEntityException
		 */
		public static GenericValue getRosterPreference(String partyId,Delegator delegator,Date date,String orgPartyId) throws GenericEntityException{
			GenericValue rosterPref =null;
			EntityCondition reCodition=EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
	                EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, date),
	                EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, date),
	                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
	                EntityCondition.makeCondition("orgPartyId", EntityOperator.EQUALS, orgPartyId)
	        ), EntityJoinOperator.AND);
			
			List<GenericValue> rosterEmps=delegator.findList("RosterEmployee", reCodition, null, null, null, false);
			GenericValue rosterEmp = EntityUtil.getFirst(rosterEmps);
			
			if(UtilValidate.isNotEmpty(rosterEmp)){
				
				String rosterPreferenceId=rosterEmp.getString("rosterPreferenceId");
				String rosterPrefRevision=rosterEmp.getString("rosterPrefRevision");
				
				EntityCondition rpCodition=EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
		                EntityCondition.makeCondition("rosterPreferenceId", EntityOperator.EQUALS, rosterPreferenceId),
		                EntityCondition.makeCondition("rosterPrefRevision", EntityOperator.EQUALS, rosterPrefRevision)
		        ), EntityJoinOperator.AND);
				
				List<GenericValue> rosterPrefs=delegator.findList("AttendanceRosterPreference", rpCodition, null, null, null, false);
				
				if (UtilValidate.isNotEmpty(rosterPrefs)) {
					 rosterPref = EntityUtil.getFirst(rosterPrefs);
				}
			}			
			
			return rosterPref;
		}
		
		/**
		 * @author zzz
		 * @param delegator
		 * @param date
		 * @param orgPartyId
		 * @param selectedProperty
		 * @return designAttendancePreference
		 * @throws GenericEntityException
		 */
		
		public static GenericValue getDesignationPreference(String partyId,Delegator delegator,Date date,String orgPartyId) throws GenericEntityException{
			GenericValue designAttendancePreference=null;
			GenericValue designPrefEmplposition=null;
			String emplPositionId=Utils.getPositionIdByPatyId(delegator, partyId);
			
			EntityCondition designPrefCodition=EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
	                EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, date),
	                EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, date),
	                EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS, emplPositionId)
	               
	        ), EntityJoinOperator.AND);
			
			List<GenericValue> designPrefEmplpositions=delegator.findList("DesignPrefEmplposition", designPrefCodition, null, null, null, false);
			if(UtilValidate.isNotEmpty(designPrefEmplpositions)){
				 designPrefEmplposition = EntityUtil.getFirst(designPrefEmplpositions);	
			}
			
			if(UtilValidate.isNotEmpty(designPrefEmplposition)){
				
				String designationPreferenceId=designPrefEmplposition.getString("designationPreferenceId");				
				List<GenericValue> designationPreferences=delegator.findList("DesignationPreference",  EntityCondition.makeCondition("designationPreferenceId", EntityOperator.EQUALS, designationPreferenceId), null, null, null, false);
				if (UtilValidate.isNotEmpty(designationPreferences)) {
					designAttendancePreference = EntityUtil.getFirst(designationPreferences);
				}				
			}
						
			return designAttendancePreference;
		}
		/**
		 * @author zzz
		 * @param delegator
		 * @param date
		 * @param orgPartyId
		 * @param selectedProperty
		 * @return orgAttendancePreference
		 * @throws GenericEntityException
		 */
		public static GenericValue getOrganizationPreference(Delegator delegator,Date date,String orgPartyId) throws GenericEntityException{
			GenericValue orgAttendancePreference=null;
			EntityCondition orgPrefCodition=EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
	                EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, date),
	                EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, date),
	                EntityCondition.makeCondition("orgPartyId", EntityOperator.EQUALS, orgPartyId)
	        ), EntityJoinOperator.AND);
		
			List<GenericValue> orgAttendancePreferences=delegator.findList("OrgAttendancePreference", orgPrefCodition, null, null, null, false);
			if(UtilValidate.isNotEmpty(orgAttendancePreferences)){
				orgAttendancePreference = EntityUtil.getFirst(orgAttendancePreferences);	
			}			
			return orgAttendancePreference;
		}
		
}