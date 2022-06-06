package org.ofbiz.humanres.attendance;



import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;


/**
 * 
 * 
 */
public class AttendancePreferenceEvents {
	public static final String module = AttendancePreferenceEvents.class.getName();
	private static Logger logger=Logger.getLogger("AttendancePreferenceEvents");
	
	
	
	/**
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> addBatchPosition(DispatchContext dctx,
				Map<String, ? extends Object> context) {
			Map<String, Object> result = ServiceUtil.returnSuccess();

			String	emplPositionTypeId=(String)context.get("emplPositionTypeId");
			String	designationPreferenceId=(String)context.get("designationPreferenceId");
			Date fromDate=(Date)context.get("fromDate");
			Date thruDate=(Date)context.get("thruDate");
			
		
			
			if(emplPositionTypeId==null||emplPositionTypeId.isEmpty()){
				return	 ServiceUtil.returnError("Employee Position Type Id can not be empty");	
			}
			if(fromDate==null){
				return	 ServiceUtil.returnError("From date can not be empty");	
			}
			if(thruDate!=null){
				if(fromDate.after(thruDate)){
					return	 ServiceUtil.returnError("From date can not grater than thrue date");	
				}
				
			}
			
			Delegator delegator = dctx.getDelegator();
			
			try {
				
				List<GenericValue> emplPositions=delegator.findByAnd("EmplPosition", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId));
				
				for(GenericValue emplPosition:emplPositions){
					
					String emplPositionId=emplPosition.getString("emplPositionId");
					
					boolean fromDateCheck=	false;
					fromDateCheck=	UtilAttendancePreference.checkExistingDesignPrefByDate(delegator, fromDate,emplPositionId,null);
					
					boolean thrueDateCheck=false;
					if(thruDate!=null){
						thrueDateCheck=	UtilAttendancePreference.checkExistingDesignPrefByDate(delegator, thruDate,emplPositionId,null);
					}
					
					if(fromDateCheck||thrueDateCheck){
						continue;
						/*return	 ServiceUtil.returnError("Date range overlap with existing Designation Preference");*/
					}
					
					//Update Existing Preference Thrudate Update
					List<GenericValue> designAttenPrefs=delegator.findList("DesignPrefEmplposition",
							EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
							EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL, null),
							EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
			                EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS, emplPositionId)
			        ),EntityJoinOperator.AND), null, null, null, false);
					
					if(designAttenPrefs!=null&&designAttenPrefs.size()>0){
						continue;
							/*return	 ServiceUtil.returnError("Please set previous Designation preference thru date first");*/
					}
		
					
					int designEmplposRevesionNew=UtilAttendancePreference.getMaxDesignPrefRevision(delegator, emplPositionId);
					designEmplposRevesionNew=designEmplposRevesionNew+1;
					
					GenericValue newValue = delegator.makeValue("DesignPrefEmplposition");
					newValue.setNonPKFields(context);
					newValue.setPKFields(context);
					newValue.set("designEmplposRevesion", designEmplposRevesionNew+"");
					newValue.set("emplPositionId", emplPositionId);
					delegator.create(newValue);
				
				}
				
			} catch (GenericEntityException e) {
				
				result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
				result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
				return result;
			}
			
			result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
			result.put(ModelService.SUCCESS_MESSAGE, "Designation Preference Added Successfully");
			result.put("designationPreferenceId", designationPreferenceId);
			return result;
		}
	
	/**
	 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> addPosition(DispatchContext dctx,
				Map<String, ? extends Object> context) {
			Map<String, Object> result = ServiceUtil.returnSuccess();

			String	emplPositionId=(String)context.get("emplPositionId");
			String	designationPreferenceId=(String)context.get("designationPreferenceId");
			Date fromDate=(Date)context.get("fromDate");
			Date thruDate=(Date)context.get("thruDate");
			
		
			
			if(emplPositionId==null||emplPositionId.isEmpty()){
				return	 ServiceUtil.returnError("Employee Position Id can not be empty");	
			}
			if(fromDate==null){
				return	 ServiceUtil.returnError("From date can not be empty");	
			}
			Delegator delegator = dctx.getDelegator();
			
			try {
				
				boolean fromDateCheck=	false;
				fromDateCheck=	UtilAttendancePreference.checkExistingDesignPrefByDate(delegator, fromDate,emplPositionId,null);
				
				boolean thrueDateCheck=false;
				if(thruDate!=null){
					if(fromDate.after(thruDate)){
						return	 ServiceUtil.returnError("From date can not grater than thrue date");	
					}
					thrueDateCheck=	UtilAttendancePreference.checkExistingDesignPrefByDate(delegator, thruDate,emplPositionId,null);
				}
				
				if(fromDateCheck||thrueDateCheck){
					return	 ServiceUtil.returnError("Date range overlap with existing Designation Preference");
				}
				
				//Update Existing Preference Thrudate Update
				List<GenericValue> designAttenPrefs=delegator.findList("DesignPrefEmplposition",
						EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
						EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL, null),
						EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
		                EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS, emplPositionId)
		        ),EntityJoinOperator.AND), null, null, null, false);
				
				if(designAttenPrefs!=null&&designAttenPrefs.size()>0){
					
						return	 ServiceUtil.returnError("Please set previous Designation preference thru date first");
				}
	
				
				int designEmplposRevesionNew=UtilAttendancePreference.getMaxDesignPrefRevision(delegator, emplPositionId);
				designEmplposRevesionNew=designEmplposRevesionNew+1;
				
				GenericValue newValue = delegator.makeValue("DesignPrefEmplposition");
				newValue.setNonPKFields(context);
				newValue.setPKFields(context);
				newValue.set("designEmplposRevesion", designEmplposRevesionNew+"");
				delegator.create(newValue);
				
				
			} catch (GenericEntityException e) {
				
				result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
				result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
				return result;
			}
			
			result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
			result.put(ModelService.SUCCESS_MESSAGE, "Designation Preference Added Successfully");
			result.put("designationPreferenceId", designationPreferenceId);
			return result;
		}
	
	
	/**
	 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> updateEmpPosition(DispatchContext dctx,
				Map<String, ? extends Object> context) {
			Map<String, Object> result = ServiceUtil.returnSuccess();

			String	emplPositionId=(String)context.get("emplPositionId");
			String	designationPreferenceId=(String)context.get("designationPreferenceId");
			String designEmplposRevesion=(String)context.get("designEmplposRevesion");
			Date fromDate=(Date)context.get("fromDate");
			Date thruDate=(Date)context.get("thruDate");
			
		
			
			if(emplPositionId==null||emplPositionId.isEmpty()){
				return	 ServiceUtil.returnError("Employee Position Id can not be empty");	
			}
			if(fromDate==null){
				return	 ServiceUtil.returnError("From date can not be empty");	
			}
			Delegator delegator = dctx.getDelegator();
			
			try {
				
				GenericValue designEmplposition=delegator.findOne("DesignPrefEmplposition", false, UtilMisc.toMap("emplPositionId", emplPositionId, "designEmplposRevesion", designEmplposRevesion, "designationPreferenceId", designationPreferenceId));
				
				boolean fromDateCheck=	false;
				fromDateCheck=	UtilAttendancePreference.checkExistingDesignPrefByDate(delegator, fromDate,emplPositionId,designEmplposition);
				
				boolean thrueDateCheck=false;
				if(thruDate!=null){
					if(fromDate.after(thruDate)){
						return	 ServiceUtil.returnError("From date can not grater than thrue date");	
					}
					thrueDateCheck=	UtilAttendancePreference.checkExistingDesignPrefByDate(delegator, thruDate,emplPositionId,designEmplposition);
				}
				
				if(fromDateCheck||thrueDateCheck){
					return	 ServiceUtil.returnError("Date range overlap with existing Designation Preference");
				}
				
				//Update Existing Preference Thrudate Update
			/*	List<GenericValue> designAttenPrefs=delegator.findList("DesignPrefEmplposition",
						EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
						EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL, null),
						EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
		                EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS, emplPositionId)
		        ),EntityJoinOperator.AND), null, null, null, false);
				
				if(designAttenPrefs!=null&&designAttenPrefs.size()>0){
					
						return	 ServiceUtil.returnError("Please set previous Designation preference thru date first");
				}*/
	
				
			
				designEmplposition.setNonPKFields(context);
				designEmplposition.setPKFields(context);
				delegator.store(designEmplposition);
				
				
			} catch (GenericEntityException e) {
				
				result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
				result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
				return result;
			}
			
			result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
			result.put(ModelService.SUCCESS_MESSAGE, "Designation Preference Added Successfully");
			result.put("designationPreferenceId", designationPreferenceId);
			return result;
		}
	
		/**
		
		 * @param dctx
		 * @param context
		 * @return
		 */
		public static Map<String, Object> addDesignationPreference(DispatchContext dctx,
					Map<String, ? extends Object> context) {
				Map<String, Object> result = ServiceUtil.returnSuccess();

				//String	emplPositionId=(String)context.get("emplPositionId");
				
				///String	orgPartyId=(String)context.get("orgPartyId");
				
				/*Date fromDate=(Date)context.get("fromDate");
				Date thruDate=(Date)context.get("thruDate");*/
				
				/*if(emplPositionId==null||emplPositionId.isEmpty()){
					return	 ServiceUtil.returnError("Employee Position Id can not be empty");	
				}
				if(fromDate==null){
					return	 ServiceUtil.returnError("From date can not be empty");	
				}*/
				Delegator delegator = dctx.getDelegator();
				
				try {
					
					//boolean fromDateCheck=	false;
					//fromDateCheck=	UtilAttendancePreference.checkExistingDesignPrefByDate(delegator, fromDate,emplPositionId,null);
					
				/*	boolean thrueDateCheck=false;
					if(thruDate!=null){
						if(fromDate.after(thruDate)){
							return	 ServiceUtil.returnError("From date can not grater than thrue date");	
						}
						thrueDateCheck=	UtilAttendancePreference.checkExistingDesignPrefByDate(delegator, thruDate,emplPositionId,null);
					}
					
					if(fromDateCheck||thrueDateCheck){
						return	 ServiceUtil.returnError("Date range overlap with existing Designation Preference");
					}*/
					
					//Update Existing Preference Thrudate Update
				/*	List<GenericValue> designAttenPrefs=delegator.findList("DesignationPreference",
							EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
							EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL, null),
							EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
			                EntityCondition.makeCondition("emplPositionId", EntityOperator.EQUALS, emplPositionId)
			        ),EntityJoinOperator.AND), null, null, null, false);
					
					if(designAttenPrefs!=null&&designAttenPrefs.size()>0){
						
							return	 ServiceUtil.returnError("Please set previous Designation preference thru date first");
					}*/
		
					
					/*int designPrefRevisionNew=UtilAttendancePreference.getMaxDesignPrefRevision(delegator, emplPositionId, orgPartyId);
					designPrefRevisionNew=designPrefRevisionNew+1;*/
					
					GenericValue newValue = delegator.makeValue("DesignationPreference");
					String designationPreferenceId = delegator.getNextSeqId("DesignationPreference");
					newValue.setNonPKFields(context);
					newValue.setPKFields(context);
					newValue.set("designationPreferenceId", designationPreferenceId);
					//newValue.set("emplPositionId", emplPositionId);
					//newValue.set("designPrefRevision", designPrefRevisionNew+"");

					delegator.create(newValue);
					
					
				} catch (GenericEntityException e) {
					
					result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
					result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
					return result;
				}
				
				result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
				result.put(ModelService.SUCCESS_MESSAGE, "Designation Preference Added Successfully");
				return result;
			}
			
			/**
			 
			 * @param dctx
			 * @param context
			 * @return
			 */
			public static Map<String, Object> updateDesignationPreference(DispatchContext dctx,
					Map<String, ? extends Object> context) {
				Map<String, Object> result = ServiceUtil.returnSuccess();

				String	designationPreferenceId=(String)context.get("designationPreferenceId");
				//String designPrefRevision = (String) context.get("designPrefRevision");
				//String emplPositionId = (String) context.get("emplPositionId");
		
			/*	Date fromDate = (Date) context.get("fromDate");
				Date thruDate = (Date) context.get("thruDate");
				
				if(emplPositionId==null||emplPositionId.isEmpty()){
					return	 ServiceUtil.returnError("Employee Position Id can not be empty");	
				}
				if(fromDate==null){
					return	 ServiceUtil.returnError("From date can not be empty");	
				}*/
				Delegator delegator = dctx.getDelegator();
				
				try {
					
						GenericValue designationPreference = delegator.findByPrimaryKey(
							"DesignationPreference", UtilMisc.toMap("designationPreferenceId",designationPreferenceId));
		
				/*		boolean fromDateCheck=	false;
						fromDateCheck=	UtilAttendancePreference.checkExistingDesignPrefByDate(delegator, fromDate,emplPositionId,designationPreference);
						
						boolean thrueDateCheck=false;
						if(thruDate!=null){
							if(fromDate.after(thruDate)){
								return	 ServiceUtil.returnError("From date can not grater than thrue date");	
							}
							thrueDateCheck=	UtilAttendancePreference.checkExistingDesignPrefByDate(delegator, thruDate,emplPositionId,designationPreference);
						}
						if(fromDateCheck||thrueDateCheck){
							return	 ServiceUtil.returnError("Date range overlap with existing Employee Preference");
						}
						*/
						designationPreference.setNonPKFields(context);
						delegator.store(designationPreference);
					
					
					
				} catch (GenericEntityException e) {
					
					result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
					result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
					return result;
				}
				
				result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
				result.put(ModelService.SUCCESS_MESSAGE, "Designation Preference Updated Successfully");
				return result;
			}
			
			
			/**
			
			 * @param dctx
			 * @param context
			 * @return
			 */
			public static Map<String, Object> addOrgAttenPreference(DispatchContext dctx,
					Map<String, ? extends Object> context) {
				Map<String, Object> result = ServiceUtil.returnSuccess();

				String orgPartyId = (String) context.get("orgPartyId");	
				
				Date fromDate=(Date)context.get("fromDate");
				Date thruDate=(Date)context.get("thruDate");
				
				if(orgPartyId==null||orgPartyId.isEmpty()){
					return	 ServiceUtil.returnError("Organization Id can not be empty");	
				}
				if(fromDate==null){
					return	 ServiceUtil.returnError("From date can not be empty");	
				}
				Delegator delegator = dctx.getDelegator();
				
				try {
					
					boolean fromDateCheck=	false;
					fromDateCheck=	UtilAttendancePreference.checkExistingOrgPrefByDate(delegator, fromDate,orgPartyId,null);
					
					boolean thrueDateCheck=false;
					if(thruDate!=null){
						if(fromDate.after(thruDate)){
							return	 ServiceUtil.returnError("From date can not grater than thrue date");	
						}
						thrueDateCheck=	UtilAttendancePreference.checkExistingOrgPrefByDate(delegator, thruDate,orgPartyId,null);
					}
					
					if(fromDateCheck||thrueDateCheck){
						return	 ServiceUtil.returnError("Date range overlap with existing Organization Preference");
					}
					
					
					//Update Existing Preference Thrudate Update
					List<GenericValue> orgAttenPrefs=delegator.findList("OrgAttendancePreference",
							EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
							EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL, null),
							EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
			                EntityCondition.makeCondition("orgPartyId", EntityOperator.EQUALS, orgPartyId)
			        ),EntityJoinOperator.AND), null, null, null, false);
					
					if(orgAttenPrefs!=null&&orgAttenPrefs.size()>0){
						
						return	 ServiceUtil.returnError("Please set previous organization preference thru date first");
					}
					
					int orgPrefRevisionNew=UtilAttendancePreference.getMaxOrgPrefRevision(delegator,orgPartyId);
					orgPrefRevisionNew=orgPrefRevisionNew+1;
					
					GenericValue newValue = delegator.makeValue("OrgAttendancePreference");
					String organizationPreferencdId = delegator.getNextSeqId("OrgAttendancePreference");
					newValue.setNonPKFields(context);
					newValue.setPKFields(context);
					newValue.set("organizationPreferencdId", organizationPreferencdId);
					newValue.set("orgPartyId", orgPartyId);
					newValue.set("orgPrefRevision", orgPrefRevisionNew+"");
					delegator.create(newValue);
					
				} catch (GenericEntityException e1) {
					result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
					result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
					return result;
				}
				
				result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
				result.put(ModelService.SUCCESS_MESSAGE, "Organization Preference is Added Successfully");
				
				return result;
			}
			
			/**
			 
			 * @param dctx
			 * @param context
			 * @return
			 */
			public static Map<String, Object> updateOrgAttenPreference(DispatchContext dctx,
					Map<String, ? extends Object> context) {
				Map<String, Object> result = ServiceUtil.returnSuccess();
				
				String organizationPreferencdId = (String) context.get("organizationPreferencdId");
				String orgPrefRevision = (String) context.get("orgPrefRevision");
				String orgPartyId=(String)context.get("orgPartyId");

				Date fromDate = (Date) context.get("fromDate");
				Date thruDate = (Date) context.get("thruDate");
				
				if(orgPartyId==null||orgPartyId.isEmpty()){
					return	 ServiceUtil.returnError("Organization Id can not be empty");	
				}
				if(fromDate==null){
					return	 ServiceUtil.returnError("From date can not be empty");	
				}
				Delegator delegator = dctx.getDelegator();
			
				
				try {
					
						GenericValue orgAttendancePreference = delegator.findByPrimaryKey(
							"OrgAttendancePreference", UtilMisc.toMap("organizationPreferencdId",organizationPreferencdId,"orgPartyId",orgPartyId,"orgPrefRevision",orgPrefRevision));
				
		
						boolean fromDateCheck=	false;
						fromDateCheck=	UtilAttendancePreference.checkExistingOrgPrefByDate(delegator, fromDate,orgPartyId,orgAttendancePreference);
						
						boolean thrueDateCheck=false;
						if(thruDate!=null){
							if(fromDate.after(thruDate)){
								return	 ServiceUtil.returnError("From date can not grater than thrue date");	
							}
							thrueDateCheck=	UtilAttendancePreference.checkExistingOrgPrefByDate(delegator, thruDate,orgPartyId,orgAttendancePreference);
						}
						if(fromDateCheck||thrueDateCheck){
							return	 ServiceUtil.returnError("Date range overlap with existing Organization Preference");
						}
			
						orgAttendancePreference.setNonPKFields(context);
						delegator.store(orgAttendancePreference);
				
				
					
				} catch (GenericEntityException e) {
					result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
					result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
					return result;
				}
				result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
				result.put(ModelService.SUCCESS_MESSAGE, "Organization Preference Updated Successfully");
				return result;
			}
			
		/**
		
		 * @param dctx
		 * @param context
		 * @return
		 */
		public static Map<String, Object> addEmpAttenPreference(DispatchContext dctx,
				Map<String, ? extends Object> context) {
			Map<String, Object> result = ServiceUtil.returnSuccess();
			
			String employeeId = (String) context.get("employeeId");
			String	orgPartyId=(String)context.get("orgPartyId");
			
			Date fromDate=(Date)context.get("fromDate");
			Date thruDate=(Date)context.get("thruDate");
			
			if(employeeId==null||employeeId.isEmpty()){
				return	 ServiceUtil.returnError("Employee Id can not be empty");	
			}
			if(fromDate==null){
				return	 ServiceUtil.returnError("From date can not be empty");	
			}

			Delegator delegator = dctx.getDelegator();
			//Get PartyId from UserlonginId
			String partyId=AttendanceUtils.getPartyIDFromUserLoginID(delegator, employeeId);
			
			try {
					
					boolean fromDateCheck=	false;
					fromDateCheck=	UtilAttendancePreference.checkExistingEmPrefByDate(delegator, fromDate,partyId,null);
					
					boolean thrueDateCheck=false;
					if(thruDate!=null){
						if(fromDate.after(thruDate)){
							return	 ServiceUtil.returnError("From date can not grater than thrue date");	
						}
						thrueDateCheck=	UtilAttendancePreference.checkExistingEmPrefByDate(delegator, thruDate,partyId,null);
					}
					
					if(fromDateCheck||thrueDateCheck){
						return	 ServiceUtil.returnError("Date range overlap with existing Employee Preference");
					}
					
					//Update Existing Preference Thrudate Update
					List<GenericValue> empAttenPrefs=delegator.findList("EmployeeAttendancePreference",
							EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
							EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL, null),
							EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
			                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)
			        ),EntityJoinOperator.AND), null, null, null, false);
					
					if(empAttenPrefs!=null&&empAttenPrefs.size()>0){
						
						return	 ServiceUtil.returnError("Please set previous employee preference thru date first");
					}
		
					
					int empPrefRevisionNew=UtilAttendancePreference.getMaxEmpPrefRevision(delegator, employeeId, orgPartyId);
					empPrefRevisionNew=empPrefRevisionNew+1;
					
					GenericValue newValue = delegator.makeValue("EmployeeAttendancePreference");
					String employeePreferenceId = delegator.getNextSeqId("EmployeeAttendancePreference");
					newValue.setNonPKFields(context);
					newValue.setPKFields(context);
					newValue.set("employeePreferenceId", employeePreferenceId);
					newValue.set("employeeId", employeeId);
					newValue.set("partyId", partyId);
					newValue.set("empPrefRevision", empPrefRevisionNew+"");

					delegator.create(newValue);

			
				
				
			} catch (GenericEntityException e) {
				
				result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
				result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
				return result;
			}
			
			result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
			result.put(ModelService.SUCCESS_MESSAGE, "Employee Preference Added Successfully");
			return result;
		}
		

		/**
		
		 * @param dctx
		 * @param context
		 * @return
		 */
		public static Map<String, Object> updateEmpAttenPreference(DispatchContext dctx,
				Map<String, ? extends Object> context) {
			Map<String, Object> result = ServiceUtil.returnSuccess();
			
			String employeePreferenceId = (String) context.get("employeePreferenceId");
			String empPrefRevision = (String) context.get("empPrefRevision");
			String employeeId = (String) context.get("employeeId");
			String	orgPartyId=(String)context.get("orgPartyId");

			Date fromDate = (Date) context.get("fromDate");
			Date thruDate = (Date) context.get("thruDate");
			
			if(employeeId==null||employeeId.isEmpty()){
				return	 ServiceUtil.returnError("Employee Id can not be empty");	
			}
			if(fromDate==null){
				return	 ServiceUtil.returnError("From date can not be empty");	
			}
			
			Delegator delegator = dctx.getDelegator();
			String partyId=AttendanceUtils.getPartyIDFromUserLoginID(delegator, employeeId);
			
					try {
							GenericValue employeeAttendancePreference = delegator.findByPrimaryKey(
								"EmployeeAttendancePreference", UtilMisc.toMap("employeePreferenceId",employeePreferenceId.trim(),"empPrefRevision",empPrefRevision,"partyId",partyId));
						
			
							boolean fromDateCheck=	false;
							fromDateCheck=	UtilAttendancePreference.checkExistingEmPrefByDate(delegator, fromDate,partyId,employeeAttendancePreference);
							
							boolean thrueDateCheck=false;
							if(thruDate!=null){
								if(fromDate.after(thruDate)){
									return	 ServiceUtil.returnError("From date can not grater than thrue date");	
								}
								thrueDateCheck=	UtilAttendancePreference.checkExistingEmPrefByDate(delegator, thruDate,partyId,employeeAttendancePreference);
							}
							if(fromDateCheck||thrueDateCheck){
								return	 ServiceUtil.returnError("Date range overlap with existing Employee Preference");
							}
							
							employeeAttendancePreference.setNonPKFields(context);
							delegator.store(employeeAttendancePreference);
						
					
						
					} catch (GenericEntityException e) {
						result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
						result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
						return result;
					}
			
			result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
			result.put(ModelService.SUCCESS_MESSAGE, "Employee Preference Update Successfully");
			return result;
		}
		
		
		/**
		
		 * @param dctx
		 * @param context
		 * @return
		 */
		public static Map<String, Object> addRosterAttenPreference(DispatchContext dctx,
				Map<String, ? extends Object> context) {
			Map<String, Object> result = ServiceUtil.returnSuccess();
			
			String	orgPartyId=(String)context.get("orgPartyId");
			String rosterStartTime=(String)context.get("rosterStartTime");
			String rosterEndTime=(String)context.get("rosterEndTime");
			
			if(rosterStartTime==null||rosterStartTime.isEmpty()){
				return	 ServiceUtil.returnError("Roster Start Time can not be empty");	
			}
			
			if(rosterEndTime==null||rosterEndTime.isEmpty()){
				return	 ServiceUtil.returnError("Roster End Time can not be empty");	
			}
			
			Delegator delegator = dctx.getDelegator();
			
			try {
				
				boolean start_endTimeCheck=	false;
				start_endTimeCheck=	UtilAttendancePreference.checkExistingRosterPrefByTime(delegator,orgPartyId,rosterStartTime,rosterEndTime,null);
				
				if(start_endTimeCheck){
					return	 ServiceUtil.returnError("Time range conflict with existing Roster Preference");
				}
				
				int rosterPrefRevisionNew=UtilAttendancePreference.getMaxRosterPrefRevision(delegator,orgPartyId,rosterStartTime,rosterEndTime);
				
				GenericValue genValue = delegator.makeValue("AttendanceRosterPreference");
				String rosterPreferenceId = "";
				
				if(rosterPrefRevisionNew>0){
					List<GenericValue> rosterPrefes=delegator.findByAnd("AttendanceRosterPreference", UtilMisc.toMap("orgPartyId", orgPartyId,"rosterStartTime",rosterStartTime,"rosterEndTime",rosterEndTime));
					GenericValue rosterPrefe=EntityUtil.getFirst(rosterPrefes);
					rosterPreferenceId=rosterPrefe.getString("rosterPreferenceId");
				}else{
					rosterPreferenceId = delegator.getNextSeqId("AttendanceRosterPreference");
				}
				rosterPrefRevisionNew=rosterPrefRevisionNew+1;
				genValue.setNonPKFields(context);
				genValue.set("rosterPreferenceId", rosterPreferenceId);
				genValue.set("rosterPrefRevision", rosterPrefRevisionNew+"");
				delegator.create(genValue);
				
				
			} catch (GenericEntityException e) {
				result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
				result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
				return result;
			}
			result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
			result.put(ModelService.SUCCESS_MESSAGE, "Roster is Registered Successfully");
			
			return result;
		}
		
		/**
		
		 * @param dctx
		 * @param context
		 * @return
		 */
		public static Map<String, Object> updateRosterAttenPreference(DispatchContext dctx,
				Map<String, ? extends Object> context) {
			Map<String, Object> result = ServiceUtil.returnSuccess();
			
			String rosterPreferenceId = (String) context.get("rosterPreferenceId");
			String rosterPrefRevision = (String) context.get("rosterPrefRevision");
			String	orgPartyId=(String)context.get("orgPartyId");
			
			String rosterStartTime=(String)context.get("rosterStartTime");
			String rosterEndTime=(String)context.get("rosterEndTime");
			
			Delegator delegator = dctx.getDelegator();
			
			try {
							GenericValue rosterAttendancePreference = delegator.findByPrimaryKey(
																	"AttendanceRosterPreference", UtilMisc.toMap(
																	"rosterPreferenceId",rosterPreferenceId,
																	"rosterPrefRevision",rosterPrefRevision
																));
				
							
							boolean start_endTimeCheck=	false;
							start_endTimeCheck=	UtilAttendancePreference.checkExistingRosterPrefByTime(delegator,orgPartyId,rosterStartTime,rosterEndTime,rosterAttendancePreference);
							
							
							if(start_endTimeCheck){
								return	 ServiceUtil.returnError("Time range conflict with existing Roster Preference");
							}
							
							rosterAttendancePreference.setNonPKFields(context);
							rosterAttendancePreference.setPKFields(context);
							delegator.store(rosterAttendancePreference);
							
					
				
			} catch (GenericEntityException e) {
				result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
				result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
				return result;
			}
			result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
			result.put(ModelService.SUCCESS_MESSAGE, "Roster Preference Updated Successfully");
			return result;
		}
		


		
	/**
	
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> addDayAttenPreference(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		String dateId = (String)context.get("dateId");
		String	orgPartyId=(String)context.get("orgPartyId");
		Date date=Date.valueOf(dateId);

		Delegator delegator = dctx.getDelegator();
		
		
		try {
			List<GenericValue> dayAttendancePreferences = delegator.findByAnd(
					"DayAttendancePreference", UtilMisc.toMap("dateId",date,"orgPartyId",orgPartyId));
			GenericValue dayAttendancePreference = EntityUtil
					.getFirst(dayAttendancePreferences);
			if (!UtilValidate.isEmpty(dayAttendancePreference)) {
				result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
				result.put(ModelService.ERROR_MESSAGE, "Preference already Added For This Day");
				return result;
			}
			
			GenericValue newValue = delegator.makeValue("DayAttendancePreference");
			String dayPreferenceId = delegator.getNextSeqId("DayAttendancePreference");
			newValue.setNonPKFields(context);
			newValue.set("dateId", date);
			newValue.set("dayPreferenceId", dayPreferenceId.trim());
			delegator.create(newValue);
			
		} catch (GenericEntityException e) {
			result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
			result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
			return result;
		}
		

		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Day Preference Added Successfully");
		return result;
	}
	
	/**

	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> updateDayAttenPreference(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		String dateId = (String)context.get("dateId");
		Date date=Date.valueOf(dateId);
	
		String dayPreferenceId = (String) context.get("dayPreferenceId");
		Delegator delegator = dctx.getDelegator();

		
		try {
			GenericValue dayAttendancePreference = delegator.findByPrimaryKey(
					"DayAttendancePreference", UtilMisc.toMap("dayPreferenceId",dayPreferenceId));
			
			dayAttendancePreference.setNonPKFields(context);
			dayAttendancePreference.set("dayPreferenceId", dayPreferenceId.trim());
			dayAttendancePreference.set("dateId", date);
	
			delegator.store(dayAttendancePreference);
		} catch (GenericEntityException e) {
			result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
			result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
			return result;
		}
		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Day Preference Updated Successfully");
		return result;
	}


	/**

	 * @param dctx
	 * @param context
	 * @return
	 * @throws GenericEntityException
	 */
	public static Map<String, Object> addRosterEmployee(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		String rosterPreferenceId = (String) context.get("rosterPreferenceId");	
		String rosterPrefRevision = (String) context.get("rosterPrefRevision");	
		String rosterName= (String) context.get("rosterName");	
		String orgPartyId = AttendanceUtils.avoidNullPointerException((String) context.get("orgPartyId"));
		
		Date fromDate=(Date)context.get("fromDate");
		Date thruDate=(Date)context.get("thruDate");
		
		if(fromDate==null){
			return	 ServiceUtil.returnError("From date can not be empty");	
		}
		

		String employeeId =(String) context.get("employeeId");
		String employeeFullName=" ";

		Delegator delegator = dctx.getDelegator();
		//Get PartyId from UserloginId
		String partyId=AttendanceUtils.getPartyIDFromUserLoginID(delegator, employeeId);
		
		try {
			
			boolean fromDateCheck=	false;
			fromDateCheck=	UtilAttendancePreference.checkExistingRosterEmpByDate(delegator, fromDate, orgPartyId, employeeId, partyId,null);
			
			boolean thrueDateCheck=false;
			if(thruDate!=null){
				if(fromDate.after(thruDate)){
					return	 ServiceUtil.returnError("From date can not grater than thrue date");	
				}
				thrueDateCheck=	UtilAttendancePreference.checkExistingRosterEmpByDate(delegator, thruDate,orgPartyId, employeeId, partyId,null);
			}
			
			if(fromDateCheck||thrueDateCheck){
				return	 ServiceUtil.returnError("Date range overlap with existing Roster Employee");
			}
			
			//Update Existing Preference Thrudate Update
			List<GenericValue> rosterEmployees=delegator.findList("RosterEmployee",
					EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
					EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL, null),
					EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
	                EntityCondition.makeCondition("employeeId", EntityOperator.EQUALS, employeeId),
	                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
	                EntityCondition.makeCondition("orgPartyId", EntityOperator.EQUALS, orgPartyId)
	        ),EntityJoinOperator.AND), null, null, null, false);
			
			if(rosterEmployees!=null&&rosterEmployees.size()>0){
				return	 ServiceUtil.returnError("Please set previous roster employee assignment thru date first");
			}
			
			 GenericValue person = delegator.findOne("Person",
						UtilMisc.toMap("partyId", partyId),false);

			 if (!UtilValidate.isEmpty(person)){
					 employeeFullName=AttendanceUtils.avoidNullPointerException((String)person.get("firstName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("middleName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("lastName"));
					
			 }
			
			int rosterEmpRevisionNew=UtilAttendancePreference.getMaxRosterEmpRevision(delegator,orgPartyId,partyId,rosterPreferenceId,rosterPrefRevision);
			
			rosterEmpRevisionNew=rosterEmpRevisionNew+1;
			
			GenericValue genValue = delegator.makeValue("RosterEmployee");
			genValue.set("rosterPreferenceId", rosterPreferenceId);
			genValue.set("rosterPrefRevision", rosterPrefRevision);
			genValue.set("employeeId", employeeId);
			genValue.set("partyId", partyId);
			genValue.set("fromDate", fromDate);
			genValue.set("rosterEmpRevesion", rosterEmpRevisionNew+"");
			genValue.set("thruDate", thruDate);
			genValue.set("orgPartyId", orgPartyId);
			genValue.set("employeeFullName", employeeFullName);
			
			delegator.create(genValue);
			
		} catch (GenericEntityException e) {
			result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
			result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
			return result;
		}
		

		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Employee is Added Successfully");
		result.put("rosterPreferenceId", rosterPreferenceId);
		result.put("rosterName", rosterName);
		result.put("rosterPrefRevision", rosterPrefRevision);
		
		return result;
	}
	
	/**
	 * @author zzz	
	 * @param dctx
	 * @param context
	 * @return
	 * @throws GenericEntityException
	 */
	public static Map<String, Object> updateRosterEmployee(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		String rosterPreferenceId = (String) context.get("rosterPreferenceId");	
		String rosterEmpRevesion = (String) context.get("rosterEmpRevesion");	
		String rosterPrefRevision = (String) context.get("rosterPrefRevision");	
		String rosterName= (String) context.get("rosterName");
		String orgPartyId = AttendanceUtils.avoidNullPointerException((String) context.get("orgPartyId"));
		
		Date fromDate=(Date)context.get("fromDate");
		Date thruDate=(Date)context.get("thruDate");
		
		if(fromDate==null){
			return	 ServiceUtil.returnError("From date can not be empty");	
		}

		String employeeId =(String) context.get("employeeId");

		Delegator delegator = dctx.getDelegator();
		//Get PartyId from UserloginId
		String partyId=AttendanceUtils.getPartyIDFromUserLoginID(delegator, employeeId);
		
		try {
			
			GenericValue rosterEmp=delegator.findOne("RosterEmployee", false, UtilMisc.toMap("rosterEmpRevesion", rosterEmpRevesion, "partyId", partyId, "rosterPreferenceId", rosterPreferenceId));
			
			boolean fromDateCheck=	false;
			fromDateCheck=	UtilAttendancePreference.checkExistingRosterEmpByDate(delegator, fromDate, orgPartyId, employeeId, partyId,rosterEmp);
			
			boolean thrueDateCheck=false;
			if(thruDate!=null){
				if(fromDate.after(thruDate)){
					return	 ServiceUtil.returnError("From date can not grater than thrue date");	
				}
				thrueDateCheck=	UtilAttendancePreference.checkExistingRosterEmpByDate(delegator, thruDate,orgPartyId, employeeId, partyId,rosterEmp);
			}
			
			if(fromDateCheck||thrueDateCheck){
				return	 ServiceUtil.returnError("Date range overlap with existing Roster Employee");
			}
			
			rosterEmp.set("fromDate", fromDate);
			rosterEmp.set("thruDate", thruDate);
		
			delegator.store(rosterEmp);
			
		} catch (GenericEntityException e) {
			result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
			result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
			return result;
		}
		

		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Employee is Added Successfully");
		result.put("rosterPreferenceId", rosterPreferenceId);
		result.put("rosterName", rosterName);
		result.put("rosterPrefRevision", rosterPrefRevision);
		
		return result;
	}
	
	
	/**
	 * @author zzz
	 * @param dctx
	 * @param context
	 * @return
	 * @throws GenericEntityException
	 */
	public static Map<String, Object> deleteRosterEmployee(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		String rosterPreferenceId = (String) context.get("rosterPreferenceId");	
		String partyId =(String) context.get("partyId");
		String employeeId =(String) context.get("employeeId");
		String rosterPrefRevision =(String) context.get("rosterPrefRevision");
		String orgPartyId =(String) context.get("orgPartyId");
		String rosterEmpRevesion =(String) context.get("rosterEmpRevesion");
		String rosterName =(String) context.get("rosterName");
	
		Delegator delegator = dctx.getDelegator();
		
		try {
			
			List<EntityExpr> exprs = FastList.newInstance();
            exprs.add(EntityCondition.makeCondition("rosterPreferenceId", EntityOperator.EQUALS, rosterPreferenceId));
            exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
            exprs.add(EntityCondition.makeCondition("rosterEmpRevesion", EntityOperator.EQUALS, rosterEmpRevesion));
            exprs.add(EntityCondition.makeCondition("employeeId", EntityOperator.EQUALS, employeeId));
            exprs.add(EntityCondition.makeCondition("rosterPrefRevision", EntityOperator.EQUALS, rosterPrefRevision));
            exprs.add(EntityCondition.makeCondition("orgPartyId", EntityOperator.EQUALS, orgPartyId));

            delegator.removeByCondition("RosterEmployee", EntityCondition.makeCondition(exprs, EntityOperator.AND), true);
         
		} catch (GenericEntityException e) {
			result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
			result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
			return result;
		}
		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Employee is Deleted Successfully");
		result.put("rosterPreferenceId", rosterPreferenceId);
		result.put("rosterName", rosterName);
		result.put("rosterPrefRevision", rosterPrefRevision);
		
		return result;
	}


/**
 * @author zzz
 * @param dctx
 * @param context
 * @return
 * @throws GenericEntityException
 */
public static Map<String, Object> assignBatchEmployee(DispatchContext dctx,
		Map<String, ? extends Object> context) throws GenericEntityException {
	Map<String, Object> result = ServiceUtil.returnSuccess();

	Delegator delegator = dctx.getDelegator();
	String rosterPreferenceId = (String) context.get("rosterPreferenceId");	
	String rosterPrefRevision = (String) context.get("rosterPrefRevision");	
	String rosterName = (String) context.get("rosterName");	
	String orgPartyId1 = (String) context.get("orgPartyId");	
	String orgPartyId ="";
	String employeeFullName=" ";
	
	Date fromDate=(Date)context.get("fromDate");
	Date thruDate=(Date)context.get("thruDate");
	
	if(fromDate==null){
		return	 ServiceUtil.returnError("From date can not be empty");	
	}
	
	if(thruDate!=null){
		if(fromDate.after(thruDate)){
			return	 ServiceUtil.returnError("From date can not grater than thrue date");	
		}
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
    	
	List<GenericValue> partyList=FastList.newInstance();
	
	partyList=AttendanceUtils.getTotalEmployee(delegator,partyList,orgPartyId);
	
			for(GenericValue party:partyList){
				String partyId=party.getString("partyId");
				//Get UserloginId from PartyId
				String employeeId=AttendanceUtils.getUserLoginIDFromPartyID(delegator, partyId);
			try {
				
				boolean fromDateCheck=	false;
				fromDateCheck=	UtilAttendancePreference.checkExistingRosterEmpByDate(delegator, fromDate, orgPartyId, employeeId, partyId,null);
				
				boolean thrueDateCheck=false;
				if(thruDate!=null){
					thrueDateCheck=	UtilAttendancePreference.checkExistingRosterEmpByDate(delegator, thruDate,orgPartyId, employeeId, partyId,null);
				}
				
				if(fromDateCheck||thrueDateCheck){
					continue;
					/*return	 ServiceUtil.returnError("Date range overlap with existing Roster Employee");*/
				}
				
				//Update Existing Preference Thrudate Update
				List<GenericValue> rosterEmployees=delegator.findList("RosterEmployee",
						EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
						EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL, null),
						EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
		                EntityCondition.makeCondition("employeeId", EntityOperator.EQUALS, employeeId),
		                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
		                EntityCondition.makeCondition("orgPartyId", EntityOperator.EQUALS, orgPartyId)
		        ),EntityJoinOperator.AND), null, null, null, false);
				
				if(rosterEmployees!=null&&rosterEmployees.size()>0){
					continue;
					/*return	 ServiceUtil.returnError("Please set previous roster employee assignment thru date first");*/
				}
				
				 GenericValue person = delegator.findOne("Person",
							UtilMisc.toMap("partyId", partyId),false);

				 if (!UtilValidate.isEmpty(person)){
						 employeeFullName=AttendanceUtils.avoidNullPointerException((String)person.get("firstName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("middleName"))+" "+AttendanceUtils.avoidNullPointerException((String)person.get("lastName"));
				 }
				
				int rosterEmpRevisionNew=UtilAttendancePreference.getMaxRosterEmpRevision(delegator,orgPartyId,partyId,rosterPreferenceId,rosterPrefRevision);
				
				rosterEmpRevisionNew=rosterEmpRevisionNew+1;
				
				GenericValue genValue = delegator.makeValue("RosterEmployee");
				genValue.set("rosterPreferenceId", rosterPreferenceId);
				genValue.set("rosterPrefRevision", rosterPrefRevision);
				genValue.set("employeeId", employeeId);
				genValue.set("partyId", partyId);
				genValue.set("fromDate", fromDate);
				genValue.set("rosterEmpRevesion", rosterEmpRevisionNew+"");
				genValue.set("thruDate", thruDate);
				genValue.set("orgPartyId", orgPartyId1);
				genValue.set("employeeFullName", employeeFullName);
				
				delegator.create(genValue);
				
				} catch (GenericEntityException e) {
				result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
				result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
				return result;
				
			}
			}
	
				if(	UtilValidate.isEmpty(partyList)){
					result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
					result.put(ModelService.ERROR_MESSAGE, "No Employee for assigned");
					result.put("rosterPreferenceId", rosterPreferenceId);
					return result;
				}
		
	result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
	result.put(ModelService.SUCCESS_MESSAGE, "Employee is Added Successfully");
	result.put("rosterPreferenceId", rosterPreferenceId);
	result.put("rosterName", rosterName);
	result.put("rosterPrefRevision", rosterPrefRevision);
	
	return result;
}
	/**
	 * @author zzz
	 * @param delegator
	 * @param orgPartyId
	 * @param rosterPreferenceId
	 * @return
	 * @throws GenericEntityException
	 */
	public static List<Map<String,String>> getRosterList(Delegator delegator,String orgPartyId,String rosterPreferenceId,String rosterPrefRevision) throws GenericEntityException{
		List<Map<String,String>> rosterList=new ArrayList<Map<String,String>>();
	
		List<String> ids=new ArrayList<String>();
		List<GenericValue> rosters = delegator.findByAnd(
				"AttendanceRosterPreference", UtilMisc.toMap(
				"orgPartyId",orgPartyId
			));
		for(GenericValue roster:rosters){
			String id=roster.getString("rosterPreferenceId");
			String revision=roster.getString("rosterPrefRevision"); 
			if(id.equalsIgnoreCase(rosterPreferenceId)&&revision.equalsIgnoreCase(rosterPrefRevision)){
				continue;
			}else{
				id=id+","+revision;
				if(!ids.contains(id)){
					ids.add(id);
					Map<String,String> rosterinfo=FastMap.newInstance();
					rosterinfo.put("id",id);
					rosterinfo.put("name", roster.getString("rosterName"));
					rosterList.add(rosterinfo);	
				}
			}
		
		}
		return rosterList;
	}
	
	/**
	 * @author zzz
	 * @param dctx
	 * @param context
	 * @return
	 * @throws GenericEntityException
	 */
	public static Map<String, Object> swapRoster(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		String rosterPreferenceId = (String) context.get("rosterPreferenceId");	
		String rosterPrefRevision = (String) context.get("rosterPrefRevision");	
		String swapTo =(String) context.get("swapTo");
		String orgPartyId =(String) context.get("orgPartyId");
		
		Date fromDate =(Date) context.get("fromDate");
		Date thruDate =(Date) context.get("thruDate");
		
		if(fromDate==null){
			return	 ServiceUtil.returnError("From date can not be empty");	
		}
		
		if(thruDate!=null){
			if(fromDate.after(thruDate)){
				return	 ServiceUtil.returnError("From date can not grater than thrue date");	
			}
		}
		
		Delegator delegator = dctx.getDelegator();
		
		try {
			
			String[] swapToArray=swapTo.split(",");
			
			String rosterPreferenceIdTo=swapToArray[0].trim();
			String rosterPrefRevisionTo=swapToArray[1].trim();
			
			List<String> orderBy = FastList.newInstance();
			orderBy.add("partyId");
			
		    EntityFindOptions efo = new EntityFindOptions();
	        efo.setDistinct(true);
			
	        // default view settings
			DynamicViewEntity dynamicViewEntity=new DynamicViewEntity();
			dynamicViewEntity.addMemberEntity("REMP", "RosterEmployee");
			dynamicViewEntity.addAlias("REMP", "rosterPreferenceId", null, null, null,Boolean.TRUE, null);
            dynamicViewEntity.addAlias("REMP", "partyId", null, null, null,Boolean.TRUE, null); 
            dynamicViewEntity.addAlias("REMP", "rosterPrefRevision", null, null, null,Boolean.TRUE, null);
			dynamicViewEntity.addAlias("REMP", "orgPartyId");
			dynamicViewEntity.addAlias("REMP", "rosterEmpRevesion");
			dynamicViewEntity.addAlias("REMP", "employeeId");
			dynamicViewEntity.addAlias("REMP", "employeeFullName");
			dynamicViewEntity.addAlias("REMP", "fromDate");
			dynamicViewEntity.addAlias("REMP", "thruDate");
			
			List<String> rosterFromParties=new ArrayList<String>();
			List<String> rosterToParties=new ArrayList<String>();
			
			EntityListIterator eliRostersFrom =delegator.findListIteratorByCondition(dynamicViewEntity,EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
					EntityCondition.makeCondition("rosterPreferenceId", EntityOperator.EQUALS, rosterPreferenceId),
					EntityCondition.makeCondition("rosterPrefRevision", EntityOperator.EQUALS, rosterPrefRevision)
	        ),EntityJoinOperator.AND), null, null, orderBy, efo);
			
			
			List<GenericValue> rostersFrom=  eliRostersFrom.getCompleteList();
		
			for(GenericValue rosterFrom:rostersFrom){
				
				String partyId=rosterFrom.getString("partyId");
				if(!rosterFromParties.contains(partyId)){
					rosterFromParties.add(partyId);
					String employeeId=rosterFrom.getString("employeeId");
					
					boolean fromDateCheck=	false;
					fromDateCheck=	UtilAttendancePreference.checkExistingRosterEmpByDate(delegator, fromDate, orgPartyId, employeeId, partyId,null);
					
					boolean thrueDateCheck=false;
					if(thruDate!=null){
						thrueDateCheck=	UtilAttendancePreference.checkExistingRosterEmpByDate(delegator, thruDate,orgPartyId, employeeId, partyId,null);
					}
					
					if(fromDateCheck||thrueDateCheck){
						continue;
						/*return	 ServiceUtil.returnError("Date range overlap with existing Roster Employee");*/
					}
					
					int rosterEmpRevisionNew=UtilAttendancePreference.getMaxRosterEmpRevision(delegator,orgPartyId,partyId,rosterPreferenceIdTo,rosterPrefRevisionTo);
					
					rosterEmpRevisionNew=rosterEmpRevisionNew+1;
					
					GenericValue genValue = delegator.makeValue("RosterEmployee");
					genValue.set("rosterPreferenceId", rosterPreferenceIdTo);
					genValue.set("rosterPrefRevision", rosterPrefRevisionTo);
					genValue.set("fromDate", fromDate);
					genValue.set("thruDate", thruDate);
					genValue.set("employeeId", employeeId);
					genValue.set("partyId", partyId);
					genValue.set("orgPartyId",orgPartyId);
					genValue.set("employeeFullName",rosterFrom.getString("employeeFullName"));
					genValue.set("rosterEmpRevesion", rosterEmpRevisionNew+"");
					
					delegator.create(genValue);
					
				}
		
				
			}
			
			
			EntityListIterator eliRostersTo =delegator.findListIteratorByCondition(dynamicViewEntity,EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
					EntityCondition.makeCondition("rosterPreferenceId", EntityOperator.EQUALS, rosterPreferenceIdTo),
					EntityCondition.makeCondition("rosterPrefRevision", EntityOperator.EQUALS, rosterPrefRevisionTo)
	        ),EntityJoinOperator.AND), null, null, orderBy, efo);
			
			
			List<GenericValue> rostersTo=  eliRostersTo.getCompleteList();
			
			for(GenericValue rosterTo:rostersTo){
				String partyId=rosterTo.getString("partyId");
				if(!rosterToParties.contains(partyId)){
					rosterToParties.add(partyId);
					String employeeId=rosterTo.getString("employeeId");
					
					boolean fromDateCheck=	false;
					fromDateCheck=	UtilAttendancePreference.checkExistingRosterEmpByDate(delegator, fromDate, orgPartyId, employeeId, partyId,null);
					
					boolean thrueDateCheck=false;
					if(thruDate!=null){
						thrueDateCheck=	UtilAttendancePreference.checkExistingRosterEmpByDate(delegator, thruDate,orgPartyId, employeeId, partyId,null);
					}
					
					if(fromDateCheck||thrueDateCheck){
						continue;
						/*return	 ServiceUtil.returnError("Date range overlap with existing Roster Employee");*/
					}
					
					int rosterEmpRevisionNew=UtilAttendancePreference.getMaxRosterEmpRevision(delegator,orgPartyId,partyId,rosterPreferenceId,rosterPrefRevision);
					
					rosterEmpRevisionNew=rosterEmpRevisionNew+1;
					
					GenericValue genValue = delegator.makeValue("RosterEmployee");
					genValue.set("rosterPreferenceId", rosterPreferenceId);
					genValue.set("rosterPrefRevision", rosterPrefRevision);
					genValue.set("fromDate", fromDate);
					genValue.set("thruDate", thruDate);
					genValue.set("employeeId", employeeId);
					genValue.set("partyId", partyId);
					genValue.set("orgPartyId",orgPartyId);
					genValue.set("employeeFullName",rosterTo.getString("employeeFullName"));
					genValue.set("rosterEmpRevesion", rosterEmpRevisionNew+"");
					
					delegator.create(genValue);
				}
				
			}
	
         
		} catch (GenericEntityException e) {
			result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
			result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
			return ServiceUtil.returnError("GenericEntityException");
		}
		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Roster Swap Successfully");
	
		return result;
	}
	/**
	 * @author zzz
	 * @param dctx
	 * @param context
	 * @return
	 * @throws GenericEntityException
	 */
	public static Map<String, Object> swapSingleEmpBtnRoster(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		String rosterPreferenceId = (String) context.get("rosterPreferenceId");	
		String rosterPrefRevision=(String) context.get("rosterPrefRevision");	
		String rosterName=(String) context.get("rosterName");	
		String swapTo =(String) context.get("swapTo");
		String partyId =(String) context.get("partyId");
		String employeeId =(String) context.get("employeeId");
		String rosterEmpRevesion =(String) context.get("rosterEmpRevesion");
		String employeeFullName =(String) context.get("employeeFullName");
		Date fromDate =(Date) context.get("fromDate");
		Date thruDate =(Date) context.get("thruDate");
		String orgPartyId =(String) context.get("orgPartyId");
		
		if(fromDate==null){
			return	 ServiceUtil.returnError("From date can not be empty");	
		}
		
		if(thruDate!=null){
			if(fromDate.after(thruDate)){
				return	 ServiceUtil.returnError("From date can not grater than thrue date");	
			}
		}
		
		Delegator delegator = dctx.getDelegator();
		
		try {
			
			String[] id_revision=swapTo.split(",");
			
			String rosterPreferenceIdTo=id_revision[0].trim();
			String rosterPrefRevisionTo=id_revision[1].trim();
			
			boolean fromDateCheck=	false;
			fromDateCheck=	UtilAttendancePreference.checkExistingRosterEmpByDate(delegator, fromDate, orgPartyId, employeeId, partyId,null);
			
			boolean thrueDateCheck=false;
			if(thruDate!=null){
				thrueDateCheck=	UtilAttendancePreference.checkExistingRosterEmpByDate(delegator, thruDate,orgPartyId, employeeId, partyId,null);
			}
			
			if(fromDateCheck||thrueDateCheck){
			
				return	 ServiceUtil.returnError("Date range overlap with existing Roster Employee");
			}
	
			//Update Existing Preference Thrudate Update
			List<GenericValue> rosterEmployees=delegator.findList("RosterEmployee",
					EntityCondition.makeCondition(UtilMisc.<EntityCondition>toList(
					EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL, null),
					EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
	                EntityCondition.makeCondition("employeeId", EntityOperator.EQUALS, employeeId),
	                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
	                EntityCondition.makeCondition("orgPartyId", EntityOperator.EQUALS, orgPartyId)
	        ),EntityJoinOperator.AND), null, null, null, false);
			
			if(rosterEmployees!=null&&rosterEmployees.size()>0){
				return	 ServiceUtil.returnError("Please set previous roster employee assignment thru date first");
			}
			
			int rosterEmpRevisionNew=UtilAttendancePreference.getMaxRosterEmpRevision(delegator,orgPartyId,partyId,rosterPreferenceIdTo,rosterPrefRevisionTo);
			
			rosterEmpRevisionNew=rosterEmpRevisionNew+1;
			
			GenericValue genValue = delegator.makeValue("RosterEmployee");
			genValue.set("rosterPreferenceId", rosterPreferenceIdTo);
			genValue.set("rosterPrefRevision", rosterPrefRevisionTo);
			genValue.set("fromDate", fromDate);
			genValue.set("thruDate", thruDate);
			genValue.set("employeeId", employeeId);
			genValue.set("partyId", partyId);
			genValue.set("orgPartyId",orgPartyId);
			genValue.set("employeeFullName",employeeFullName);
			genValue.set("rosterEmpRevesion", rosterEmpRevisionNew+"");
			
			delegator.create(genValue);


    
         
		} catch (GenericEntityException e) {
			result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
			result.put(ModelService.ERROR_MESSAGE, "GenericEntityException");
			return result;
		}
		result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Roster Swap Successfully");
		result.put("rosterPreferenceId", rosterPreferenceId);
		result.put("rosterName", rosterName);
		result.put("rosterPrefRevision", rosterPrefRevision);
		return result;
	}

}