/**
 * ACTAtekAgentImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtekAgent_service;

import java.net.MalformedURLException;
import java.security.AlgorithmParameters;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.rpc.ServiceException;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.hectrix.www.ACTAtekAgent_xsd.ELog;
import com.hectrix.www.ACTAtekAgent_xsd.Log;
import com.hectrix.www.ACTAtekAgent_xsd.PhotoPart;
import com.hectrix.www.ACTAtek_service.ACTAtekLocator;
import com.hectrix.www.ACTAtek_service.ACTAtekPortType;
import com.hectrix.www.ACTAtek_xsd.AdminLevel;
import com.hectrix.www.ACTAtek_xsd.FingerprintSecurityLevel;
import com.hectrix.www.ACTAtek_xsd.User;


public class ACTAtekAgentImpl implements
		com.hectrix.www.ACTAtekAgent_service.ACTAtekAgentPortType {
	
	private static final String magic=UtilProperties.getPropertyValue("contessa.properties", "magic");
	private static final ACTAtekLocator locator = new ACTAtekLocator();
	public static final String module = ACTAtekAgentImpl.class.getName();
	public java.lang.String log(java.lang.String magic,
			com.hectrix.www.ACTAtekAgent_xsd.Log unencryptedLog)
			throws java.rmi.RemoteException {
		@SuppressWarnings("unused")
		String magic1 = magic;
		@SuppressWarnings("unused")
		Log log = unencryptedLog;
		return null;
	}

	public java.lang.String encryptLog(byte[] encryptedLog,
			com.hectrix.www.ACTAtekAgent_xsd.PhotoPart photo)
			throws java.rmi.RemoteException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String logdata = null;
		try {
		
			logdata = decrypt(encryptedLog,magic);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<String,Object> logMap=parseLog(delegator,logdata, photo);
		Log log =(Log) logMap.get("log");
		Map<String,String> agentDto= (Map<String,String>) logMap.get("agentDto");
	
		Map<String, Object> userAndDeviceInfo=getUserAndDeviceInfo(log.getTerminalSN(), delegator);
		
		//Check device type 
		String trigger=String.valueOf(userAndDeviceInfo.get("type"));
		if(trigger.equalsIgnoreCase("Auto")){
			trigger=log.getTrigger().getValue();
		}
		
		if (isValidLog(log,delegator,userAndDeviceInfo)) {
			
			
			
			if(agentDto.get("firsTimeRegister").equalsIgnoreCase("Y")&&log.getUserID().equalsIgnoreCase("Unknown User")){
				String	rfid=String.valueOf(getDecimalFromHexa(agentDto.get("rfid")));
				rfid=convertToLastEightDigit(rfid);
				GenericValue person=checkEmpExistence(delegator,rfid);
				
				
				if(person!=null){
					String terminalIP= agentDto.get("terminalIP");
					String userName=agentDto.get("userName");
					String userPass=agentDto.get("userPass");
					String cardID=agentDto.get("cardID");
					ACTAtekPortType api;
					try {
						api = locator.getACTAtek(new java.net.URL(getAgentUri(terminalIP)));
				
						long sessionID = api.login(userName, userPass);
						User user=new User();
						String firstName="";
						String lastName="";
						if(person.get("firstName")!=null){
							firstName=String.valueOf(person.get("firstName"));
						}
						if(person.get("lastName")!=null){
							lastName=String.valueOf(person.get("lastName"));
						}
						
						user.setUserID(String.valueOf(person.get("partyId")));
						user.setFirstName(firstName);
						user.setLastName(lastName);
						user.setAdminLevel(AdminLevel.PERSONALUSER);
						user.setCardsn(cardID);
						com.hectrix.www.ACTAtek_xsd.Status status=new com.hectrix.www.ACTAtek_xsd.Status();
						status.setActive(true);
						status.setSmartCard(true);
						
						status.setFingerprint(false);
						user.setStatus(status);
						user.setFingerprintSecurityLevel(FingerprintSecurityLevel.LOWEST);
						
					
						
						if(registerEmployee(delegator,log.getTerminalSN(),String.valueOf(person.get("partyId")),firstName+" "+lastName)){
							api.addUser(sessionID, user);
						}
					
						api.logout(sessionID);
					
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ServiceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			
			
			Map<String, String> paramMap = UtilMisc.toMap( 
	    			"logID", String.valueOf(log.getLogID()), 
	    			"userID", String.valueOf(log.getUserID()),
	    			"timestamp",String.valueOf(log.getTimestamp().getTimeInMillis()),
	    			"trigger", trigger.toUpperCase(),
	    			"terminalSN", log.getTerminalSN(),
	    			"sender", log.getSender()
	    		);
			
			
			Map<String, Object> result = FastMap.newInstance();
			
				try {
					
					if(isActive(String.valueOf(log.getUserID()),log.getTerminalSN(),delegator)){
					result = saveEmployeeLog(delegator, paramMap);
				
					result.get(ModelService.RESPONSE_MESSAGE);
					}
				} catch (Exception e) {
					
					e.printStackTrace();
				}		
			
		}


	
		return magic;
	}

	public String getUserName(String id) {
		List<User> list = ACTAtekAgentConstants.terminalUsers;
		for (User user : list) {
			if (user.getUserID().equalsIgnoreCase(id)) {
				return user.getFirstName();
			}
		}
		return id;
	}

	public boolean isValidLog(Log log,GenericDelegator delegator,Map<String, Object> userAndDeviceInfo) {
		return (isSystemLog(log,delegator,userAndDeviceInfo) | isSameLog(log)) ? false : true;
	}

	public boolean isSystemLog(Log log,GenericDelegator delegator,Map<String, Object> userAndDeviceInfo) {
		
		
		if (userAndDeviceInfo!=null&&log.getUserID().equalsIgnoreCase((String)userAndDeviceInfo.get("userName"))) {
			return true;
		}
		return false;
	}

	public boolean isSameLog(Log log) {
		if (null == ACTAtekAgentConstants.prevLog) {
			ACTAtekAgentConstants.prevLog = log;
			return false;
		} else {
			Log prvlog = ACTAtekAgentConstants.prevLog;
			if (log.getTimestamp().equals(prvlog.getTimestamp())
					&& log.getUserID().equals(prvlog.getUserID())
					&& log.getTrigger().equals(prvlog.getTrigger())
					&& log.getTerminalSN().equals(prvlog.getTerminalSN())) {
				return true;
			}

			return false;
		}
	}

	public java.lang.String encryptLogMultiple(
			com.hectrix.www.ACTAtekAgent_xsd.ELog[] encryptedLogs)
			throws java.rmi.RemoteException {
		@SuppressWarnings("unused")
		ELog[] l = encryptedLogs;
		return null;
	}

	public java.lang.String sync(java.lang.String magic,
			java.lang.String registrationID) throws java.rmi.RemoteException {

		@SuppressWarnings("unused")
		String id = registrationID;
		return null;
	}

	private String decrypt(byte[] data, String magic) throws Exception {

		Cipher c = Cipher.getInstance("Blowfish/CFB/NoPadding");
		AlgorithmParameters parms = AlgorithmParameters.getInstance("Blowfish");

		parms.init(new IvParameterSpec(new byte[8]));

		SecretKeySpec keyspec = new SecretKeySpec(magic.getBytes(), "Blowfish");

		c.init(Cipher.DECRYPT_MODE, keyspec, parms);

		return new String(c.doFinal(data));

	}

	private Map<String,Object> parseLog(GenericDelegator delegator,String logdata, PhotoPart photo) {
		
		Map<String,Object> logMap=new HashMap<String,Object>();
		
		StringTokenizer st = new StringTokenizer(logdata, "\t");
		int i = 0;
		boolean success = false;
		boolean breaking = false;
		long timestamp = 0;
		String userID = null;
		String eventID = null;
		String terminalSN = null;
		String sender = null;
		String cardID=null;
		String rfid=null;
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			switch (i) {
			case 0:
				timestamp = Long.parseLong(token);
				break;
			case 1:
				userID = token;
				break;
			case 2:
				eventID = token;
				break;
			case 3:
				terminalSN = token;
				break;
			case 4:
				sender = token;
				success = true;
				break;
			case 5:
				cardID=token.substring(8, token.length()-3);
				rfid=cardID.substring(5);
				break;	
			default:
				break;
			}
			if (breaking)
				break;
			i++;
		}
		if (success) { // well-formatted log string
			// Reconstruct the log
			com.hectrix.www.ACTAtekAgent_xsd.Log log = new com.hectrix.www.ACTAtekAgent_xsd.Log();
			Calendar ts = Calendar.getInstance();
			ts.setTimeInMillis(timestamp * 1000);
			com.hectrix.www.ACTAtekAgent_xsd.EventType evt = com.hectrix.www.ACTAtekAgent_xsd.EventType
					.fromString(eventID);
			log.setLogID(0); // don't care
			log.setTerminalSN(terminalSN);
			log.setTimestamp(ts);
			log.setTrigger(evt);
			log.setUserID(userID);
			log.setSender(sender);
			log.setPhotoPart(photo);
			logMap.put("log", log);
			Map<String,String> agentDto=getDeviceDto(delegator,terminalSN);
			agentDto.put("rfid", rfid);
			agentDto.put("cardID", cardID);
			logMap.put("agentDto", agentDto);
		
			return logMap;
		}
		return null;
	}
	private static Map<String, Object> saveEmployeeLog(Delegator delegator,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		


		String partyId = null;
	
		String userID = (String) context.get("userID");
		String timestamp = (String) context.get("timestamp");
		String trigger = (String) context.get("trigger");
		String terminalSN = (String) context.get("terminalSN");
		String sender = (String) context.get("sender");

		try {
			
			
			GenericValue newValue = delegator.makeValue("EmployeeLog");
			partyId = delegator.getNextSeqId("EmployeeLog");
			
			
			newValue.set("logId", partyId.trim());
			newValue.set("userLoginId", userID);
			newValue.set("logtimeStamp", new java.sql.Timestamp(Long.valueOf(timestamp)));
			newValue.set("eventType", trigger);
			newValue.set("terminalSN", terminalSN);
			newValue.set("senderTerminal", sender);
		

			delegator.create(newValue);
			
			String locationid=getLoctaionId(delegator,userID);
			
			
			List<GenericValue> locations = delegator.findByAnd("Location",UtilMisc.toMap(
					"locationId", locationid
					));
			GenericValue location = EntityUtil.getFirst(locations);
			
			if (!UtilValidate.isEmpty(location)){
				location.set("currentInOutStatus", trigger);
				delegator.store(location);
			}
			

			result.put(ModelService.RESPONSE_MESSAGE,
					ModelService.RESPOND_SUCCESS);
			result.put(ModelService.SUCCESS_MESSAGE, "Gate Added Successfully");

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}
	private static boolean isActive(String userId,String terminalSN,GenericDelegator delegator){
		
		try {
			List<GenericValue> employeeRegistrations = delegator.findByAnd("EmployeeRegistration", UtilMisc.toMap(
					"userLoginId",userId,
					"deivceId",terminalSN
					));
			GenericValue employeeRegistration = EntityUtil
					.getFirst(employeeRegistrations);
			if (!UtilValidate.isEmpty(employeeRegistration)) {
			List<GenericValue> locations = delegator.findByAnd("Location", UtilMisc.toMap(
					"locationId",employeeRegistration.get("locationId")
					));
			
			GenericValue location = EntityUtil
					.getFirst(locations);
			if (!UtilValidate.isEmpty(location)) {
			if(location.get("statusId").equals("EMPL_REGISTERED")){
				return true;	
				}
			}
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	private static Map<String, Object> getUserAndDeviceInfo(String terminalSN,Delegator delegator){
		
		Map<String, Object> userAndDeviceInfo = new HashMap<String, Object>();
	
		try {
						List<GenericValue> deviceRegistrations = delegator.findByAnd("DeviceRegistration",UtilMisc.toMap("terminalSN", terminalSN));
						GenericValue deviceRegistration = EntityUtil.getFirst(deviceRegistrations);
						
						if (UtilValidate.isEmpty(deviceRegistration)){
								return null;
						}
						else{	
								
							String	userName=String.valueOf(deviceRegistration.get("userName"));
							String type=String.valueOf(deviceRegistration.get("type"));
							userAndDeviceInfo.put("userName", userName);
							userAndDeviceInfo.put("type", type);
						
						}
			
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			return null;
		}
		return userAndDeviceInfo;
	}
	
	private static String getLoctaionId(Delegator delegator,String userLonginId){
		
		List<GenericValue> employeeRegistrations=null;
		try {
			employeeRegistrations = delegator.findByAnd("EmployeeRegistration",UtilMisc.toMap(
					"userLoginId", userLonginId
					));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GenericValue employeeRegistration = EntityUtil.getFirst(employeeRegistrations);
		
		if (!UtilValidate.isEmpty(employeeRegistration)){
			return String.valueOf(employeeRegistration.get("locationId"));
		}
		return null;
		
	}
	private static Map<String,String> getDeviceDto(GenericDelegator delegator,String terminalSN){
		List<GenericValue> deviceRegistrations=null;
		Map<String,String> agentDto=new HashMap<String,String>();
		String action="";
		try {
			deviceRegistrations = delegator.findByAnd("DeviceRegistration",UtilMisc.toMap("terminalSN", terminalSN));
			GenericValue	deviceRegistration=	EntityUtil.getFirst(deviceRegistrations);
			if (!UtilValidate.isEmpty(deviceRegistration)){
				action= String.valueOf(deviceRegistration.get("firsTimeRegister"));
				agentDto.put("firsTimeRegister", action);
				agentDto.put("terminalIP", String.valueOf(deviceRegistration.get("terminalIP")));
				agentDto.put("userName", String.valueOf(deviceRegistration.get("userName")));
				agentDto.put("userPass", String.valueOf(deviceRegistration.get("userPass")));
	
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return agentDto;
	}
	private static GenericValue checkEmpExistence(GenericDelegator delegator,String cardID){
	
		try {
			List<GenericValue>	persons = delegator.findByAnd("Person",UtilMisc.toMap("cardId", cardID));
			GenericValue	person=	EntityUtil.getFirst(persons);
			if (!UtilValidate.isEmpty(person)){
				return person;
			}
		} catch (GenericEntityException e) {
			return null;
		}
		
		return null;
	}
	private static int getDecimalFromHexa(String rfid){
		return Integer.parseInt(rfid, 16);
	}
	private static String getAgentUri(String terminalIp){
		return "http://"+terminalIp.trim()+"/cgi-bin/rpcrouter".trim();
	}
	private static boolean registerEmployee(Delegator delegator,String terminalSN,String userLoginID,String employeeName){
		List<GenericValue> partyRelationships=FastList.newInstance();
		List<GenericValue> deviceRegistrations=FastList.newInstance();
		String orgId=null;
		String parentLocationId=null;
		String description="First Time Employee Registration";

		
		
		try {
			
			
			List<GenericValue> employeeRegistrations = delegator.findByAnd("EmployeeRegistration", UtilMisc.toMap(
					"userLoginId",userLoginID,
					"deivceId",terminalSN.trim()
					));

			GenericValue employeeRegistration = EntityUtil
					.getFirst(employeeRegistrations);
			if (!UtilValidate.isEmpty(employeeRegistration)) {
					String errMsg = "Fail to Register, Selected Employee is Already Register on this Device";
					Debug.logInfo(errMsg, module);
					return false;
			}
			
			partyRelationships = delegator.findByAnd(
					"PartyRelationship", UtilMisc.toMap("partyIdTo",userLoginID.trim()));
			GenericValue partyRelationship = EntityUtil
					.getFirst(partyRelationships);
			if (!UtilValidate.isEmpty(partyRelationship)){
				orgId=String.valueOf(partyRelationship.get("partyIdFrom"));
			}
			
			deviceRegistrations=delegator.findByAnd(
					"DeviceRegistration", UtilMisc.toMap("terminalSN",terminalSN.trim(),"partyId",orgId));
			GenericValue deviceRegistration = EntityUtil
					.getFirst(deviceRegistrations);
			if (!UtilValidate.isEmpty(deviceRegistration)){
				parentLocationId=String.valueOf(deviceRegistration.get("locationId"));
			}else{
				return false;
			}
			
			if(parentLocationId!=null){
				/* Start Parent Device Status Validation */
				List<GenericValue> devices = delegator.findByAnd("Location",UtilMisc.toMap(
							"locationId", parentLocationId, 
							"locationTypeId","Device",
							"parentLocationTypeId", "Gate"
							));

				GenericValue device = EntityUtil.getFirst(devices);
				
				if (device.get("statusId").equals("DEVICE_UNREGISTERED")) {
						String errMsg = "Fail to Register, Please First Register Device for this Employee";
						Debug.logInfo(errMsg, module);

						return false;
				}
				/* End Parent Device Status Validation */
			}

			/*Start Insertion Location Related Data In Location Table*/
			GenericValue newValue = delegator.makeValue("Location");

			String locationId = delegator.getNextSeqId("Location");
			newValue.set("locationId", locationId.trim());
			newValue.set("locationTypeId", "Employee");
			newValue.set("partyId", orgId);
			newValue.set("parentLocationId", parentLocationId.trim());
			newValue.set("locationName", employeeName.trim());
			newValue.set("parentLocationTypeId", "Device");
			newValue.set("statusId", "EMPL_REGISTERED");
			newValue.set("description", description);
			newValue.set("currentInOutStatus", "");

			delegator.create(newValue);
		
			/*End Insertion Location Related Data In Location Table*/
			
			/*Start Insertion EmployeeRegistration Related Data In EmployeeRegistration Table*/
			GenericValue newValue2 = delegator
					.makeValue("EmployeeRegistration");

			newValue2.set("userLoginId", userLoginID.trim());
			newValue2.set("deivceId", terminalSN.trim());
			newValue2.set("locationId", locationId.trim());
			newValue2.set("employeeName", employeeName.trim());

			delegator.create(newValue2);
			/*End Insertion EmployeeRegistration Related Data In EmployeeRegistration Table*/
			
			
		} catch (GenericEntityException e) {
			
			return false;
		}
		return true;
	}

	private static String getPartyIDFromUserLoginID(Delegator delegator,String userLoginId){
		List<GenericValue> userLogins=FastList.newInstance();
		try {
			userLogins = delegator.findByAnd("UserLogin",UtilMisc.toMap("userLoginId", userLoginId));
			GenericValue	userLogin=	EntityUtil.getFirst(userLogins);
			if (!UtilValidate.isEmpty(userLogin)){
				return String.valueOf(userLogin.get("partyId"));
			}
		} catch (GenericEntityException e) {
			return null;
		}
		return null;
	}

	private static String getUserLoginIDFromPartyID(Delegator delegator,String partyId){
		List<GenericValue> userLogins=FastList.newInstance();
		try {
			userLogins = delegator.findByAnd("UserLogin",UtilMisc.toMap("partyId", partyId));
			GenericValue	userLogin=	EntityUtil.getFirst(userLogins);
			if (!UtilValidate.isEmpty(userLogin)){
				return String.valueOf(userLogin.get("userLoginId"));
			}
		} catch (GenericEntityException e) {
			return null;
		}
		return null;
	}
	public static String convertToLastEightDigit(String firstDigit){
			String retVal=null;
			long rfId=Long.parseLong(firstDigit);
			String binaryString=Long.toBinaryString(rfId);
			int binaryStringLeangth=binaryString.length();
			String subString1=binaryString.substring(binaryStringLeangth-16, binaryStringLeangth);
			String subString2=binaryString.substring(0,binaryStringLeangth-16);
			int s1=Integer.parseInt(subString1, 2);
			int s2=Integer.parseInt(subString2, 2);
		
			retVal=String.valueOf(s2).concat(String.valueOf(s1));
		
		return retVal;
	}
}