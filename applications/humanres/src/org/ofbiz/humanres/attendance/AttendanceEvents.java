package org.ofbiz.humanres.attendance;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import javolution.util.FastList;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import bd.pegasus.connector.AbstractAgent;
import bd.pegasus.connector.AgentListener;
import bd.pegasus.connector.ConnectionFactory;
import bd.pegasus.connector.ConnectionFactoryImpl;

import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;
import com.hectrix.www.ACTAtek_service.ACTAtekLocator;
import com.hectrix.www.ACTAtek_service.ACTAtekPortType;
import com.hectrix.www.ACTAtek_xsd.Terminal;



/**
 *
 * 
 */
public class AttendanceEvents {
	public static final String module = AttendanceEvents.class.getName();
	private static Logger logger=Logger.getLogger("AttendanceEvents");
	private static final String magic=UtilProperties.getPropertyValue("contessa.properties", "magic");
	private static final String agentPort=UtilProperties.getPropertyValue("contessa.properties", "agent.port");
	private static final ACTAtekLocator locator = new ACTAtekLocator();
	private static final ConnectionFactory connectionFactory = new ConnectionFactoryImpl();
	
	
	
	public static String uploadEmployeeLog(HttpServletRequest request,HttpServletResponse response)
    {
		
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		ServletFileUpload fu = new ServletFileUpload(new DiskFileItemFactory());         
        java.util.List lst = null;
        StringBuilder errorMassage=new StringBuilder();
        errorMassage.append("File Upload Fail for Duplicate Entry ");
        StringBuilder successMassage=new StringBuilder();
        successMassage.append("File Uploading......Successfully ");
        int entityCoun=0;
        String file_name="";
        StringBuilder warningMassage=new StringBuilder();
        warningMassage.append("These Duplicate Entry can not be store ");
        boolean flag=false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+6"));
        try 
        {
            lst = fu.parseRequest(request);
        }
        catch (FileUploadException fup_ex) 
        {
          
            fup_ex.printStackTrace();
         
            request.setAttribute("_ERROR_MESSAGE_", "File upload stopped by exception");                    
            return("error");
        }

        if(lst.size()==0)        //There is no item in lst
        {
           
            request.setAttribute("_ERROR_MESSAGE_", "Please Select a File.");                    
            return("error");
        }


        FileItem file_item = null;
        FileItem selected_file_item=null;

        //Checking for form fields - Start
            for (int i=0; i < lst.size(); i++) 
            {
                file_item=(FileItem)lst.get(i);
                String fieldName = file_item.getFieldName();

                //Check for the attributes for user selected file - Start
                    if(fieldName.equals("filename"))
                    {
                        selected_file_item=file_item;
                    
                        file_name=file_item.getName();             //Getting the file name
                      
                        break;
                    }
              
            }
      
            if(selected_file_item==null)                    //If selected file item is null
            {
               
                request.setAttribute("_ERROR_MESSAGE_", "Please Select a File");                    
                return("error");
            }

            byte[] file_bytes=selected_file_item.get();
            byte[] extract_bytes=new byte[file_bytes.length];

            for(int l=0;l<file_bytes.length;l++)
            {
            	extract_bytes[l]=file_bytes[l];	
            }

            try
                {
                    FileOutputStream fout=new FileOutputStream(file_name);
                   
                    fout.flush();
                    fout.write(extract_bytes);
                    fout.flush();
                    fout.close();
                	DataInputStream in = new DataInputStream(new ByteArrayInputStream(extract_bytes));
        			
        			Thread fileUpload = new FileUploadThread(delegator,in, file_name);
        			fileUpload.start();
                    
                }
                catch(IOException ioe_ex)
                {
                    ioe_ex.printStackTrace();
           
                    request.setAttribute("_ERROR_MESSAGE_", "File upload stopped by exception");                    
                    return("error");
                }
            
            if(flag){
            	 request.setAttribute("_ERROR_MESSAGE_", warningMassage.toString());                    
                 return("error");
            }
            //successMassage.append(entityCoun+" Data Store in Database");
            request.setAttribute("_EVENT_MESSAGE_", successMassage.toString());            
            return("success");
      
    }
	
	
	
	public static Map<String, Object> registerEmployee(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();

		Delegator delegator = dctx.getDelegator();

		String partyId = (String) context.get("partyId");
		String currentLocationId = (String) context.get("currentLocationId");
		String terminalSN = (String) context.get("terminalSN");
		String groupName = (String) context.get("groupName");
		String orgId = (String) context.get("orgId");
		String description = (String) context.get("description");
		String firstName = "";
		String lastName = "";

		try {

				/* Get PartyId from UserLoginId */
				partyId=AttendanceUtils.getPartyIDFromUserLoginID(delegator, partyId);
				/* Start Parent Device Status Validation */
				List<GenericValue> devices = delegator.findByAnd("Location",UtilMisc.toMap(
							"locationId", currentLocationId, 
							"locationTypeId","Device",
							"parentLocationTypeId", "Gate"
							));

				GenericValue device = EntityUtil.getFirst(devices);
				
				if (device.get("statusId").equals("DEVICE_UNREGISTERED")) {
						String errMsg = "Fail to Register, Please First Register Device for this Employee";
						Debug.logInfo(errMsg, module);
						Map<String, Object> results = ServiceUtil.returnSuccess();

						results.put(ModelService.ERROR_MESSAGE, errMsg);

						return results;
				}
				/* End Parent Device Status Validation */

				/* Check Selected Employee is Already Registered on this Device */

				List<GenericValue> deviceRegistrations = delegator.findByAnd("DeviceRegistration",UtilMisc.toMap(
						"locationId", device.get("locationId")
						));

				GenericValue deviceRegistration = EntityUtil
						.getFirst(deviceRegistrations);

				List<GenericValue> employeeRegistrations = delegator.findByAnd("EmployeeRegistration", UtilMisc.toMap(
						"userLoginId",partyId,
						"deivceId",deviceRegistration.get("terminalSN")
						));

				GenericValue employeeRegistration = EntityUtil
						.getFirst(employeeRegistrations);
				if (!UtilValidate.isEmpty(employeeRegistration)) {
						String errMsg = "Fail to Register, Selected Employee is Already Register on this Device";
						Debug.logInfo(errMsg, module);
						Map<String, Object> results = ServiceUtil.returnSuccess();

						results.put(ModelService.ERROR_MESSAGE, errMsg);

						return results;
				}

				/* End Check Selected Employee is Already Registered on this Device */
			

				/* Check null Employee Selection */
			
				List<GenericValue> persons = delegator.findByAnd("Person",
							UtilMisc.toMap("partyId", partyId));

				GenericValue person = EntityUtil
							.getFirst(persons);
				if (UtilValidate.isEmpty(person)) {
							String errMsg = "Fail to Register, Employee Missing";
							Debug.logInfo(errMsg, module);
							Map<String, Object> results = ServiceUtil.returnSuccess();

							results.put(ModelService.ERROR_MESSAGE, errMsg);

							return results;
				}

				/* End Check null Employee Selection */

				if (person.get("firstName") != null) {
					
							firstName = (String) person.get("firstName");
				}
				
				if (person.get("lastName") != null) {
					
							lastName = (String) person.get("lastName");
				}
				
				groupName = firstName + " " + lastName;
			
				/*Start Insertion Location Related Data In Location Table*/
				GenericValue newValue = delegator.makeValue("Location");

				String locationId = delegator.getNextSeqId("Location");
				newValue.set("locationId", locationId.trim());
				newValue.set("locationTypeId", "Employee");
				newValue.set("partyId", orgId);
				newValue.set("parentLocationId", currentLocationId.trim());
				newValue.set("locationName", groupName.trim());
				newValue.set("parentLocationTypeId", "Device");
				newValue.set("statusId", "EMPL_REGISTERED");
				newValue.set("description", description);
				newValue.set("currentInOutStatus", "");

				delegator.create(newValue);
			
				/*End Insertion Location Related Data In Location Table*/

				/*Start Insertion EmployeeRegistration Related Data In EmployeeRegistration Table*/
				GenericValue newValue2 = delegator
						.makeValue("EmployeeRegistration");

				newValue2.set("userLoginId", partyId.trim());
				newValue2.set("deivceId", terminalSN.trim());
				newValue2.set("locationId", locationId.trim());
				newValue2.set("employeeName", groupName.trim());

				delegator.create(newValue2);
				/*End Insertion EmployeeRegistration Related Data In EmployeeRegistration Table*/
				
				
				result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
				result.put(ModelService.SUCCESS_MESSAGE, "Employee is Registered Successfully");

		} catch (GenericEntityException e) {
			String errMsg = "GenericEntityException";
			Debug.logInfo(errMsg, module);
			Map<String, Object> results = ServiceUtil.returnSuccess();

			results.put(ModelService.ERROR_MESSAGE, errMsg);

			return results;
		}

		return result;
	}

	public static Map<String, Object> registerDevices(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		String deviceType= (String) context.get("deviceType");
		String partyId = (String) context.get("partyId");
		String terminalIP = (String) context.get("terminalIP");
		String pegasusAddress=(String) context.get("pegasusAddress");
		String terminalType = (String) context.get("terminalType");
		String terminalSN = (String) context.get("terminalSN");
		String currentLocationId = (String) context.get("currentLocationId");
		String groupName = (String) context.get("groupName");
		String orgId = (String) context.get("orgId");
		String description = (String) context.get("description");
		
		String firmewareVersion= (String) context.get("firmewareVersion");
		String fliVersion= (String) context.get("fliVersion");
		String userName = (String) context.get("userName");
		String userPass = (String) context.get("userPass");
		String endPointIP = (String) context.get("endPointIP");
		String agentVersion = (String) context.get("agentVersion");
		String type = (String) context.get("type");
		//String firsTimeRegister = (String) context.get("firsTimeRegister");
		
		try {
			
					/*Start Check Physical Existence of this Device*/
			if(deviceType.equals("Actatek")){
					ACTAtekPortType api = locator.getACTAtek(new java.net.URL(getAgentUri(terminalIP)));
					long sessionID = api.login(userName, userPass);
		
			
					Terminal[] terminalList =	api.getTerminalList(sessionID);
					boolean isValid=true;
					
					for(Terminal terminal:terminalList ){
								if((terminalSN.equalsIgnoreCase(terminal.getTerminalSN()))&&(terminalIP.equalsIgnoreCase(terminal.getTerminalIP()))){
									isValid=false;
								}
					}
			
					if(isValid){
								String errMsg = "Fail to Register,  This Device Physicaly Invalid";
								Debug.logInfo(errMsg, module);
								Map<String, Object> results = ServiceUtil.returnSuccess();

								results.put(ModelService.ERROR_MESSAGE, errMsg);
					
								/*Must be Logout*/	
								api.logout(sessionID);
								return results;
					}
					
					/*End Check Physical Existence of this Device*/	
					else{
			
								/*Start Check Duplicate Device Registration */
								List<GenericValue> deviceRegistrations = delegator.findByAnd("DeviceRegistration", UtilMisc.toMap(
										"terminalIP", terminalIP, 
										"terminalSN", terminalSN
					
										));

								GenericValue deviceRegistration = EntityUtil.getFirst(deviceRegistrations);
								if (deviceRegistration!=null) {
												String errMsg = "Sorry, this Device already exists.";
												Debug.logInfo(errMsg, module);
												Map<String, Object> results = ServiceUtil.returnSuccess();

												results.put(ModelService.ERROR_MESSAGE, errMsg);
												/*Must be Logout*/	
												api.logout(sessionID);
												return results;
								}
			
								/*End Check Duplicate Device Registration */
			
				
								/*Start Parent Gate Status of This Device */
								List<GenericValue> gates = delegator.findByAnd("Location", UtilMisc.toMap(
											"locationId", currentLocationId, 
											"locationTypeId", "Gate",
											"parentLocationTypeId", "Floor"
										));

								GenericValue gate = EntityUtil.getFirst(gates);
								if (gate.get("statusId").equals("GATE_DEACTIVATED")) {
											String errMsg = "Fail to Register, Please First Registere Gate for this Device";
											Debug.logInfo(errMsg, module);
											Map<String, Object> results = ServiceUtil.returnSuccess();

											results.put(ModelService.ERROR_MESSAGE, errMsg);
											/*Must be Logout*/	
											api.logout(sessionID);
											return results;
								}
								
								/*End Parent Gate Status of This Device */
						
								/*Start Register Agent for This Device */
								try{
											long registrationID=System.currentTimeMillis();
											api.registerAgent(sessionID, getEndPointURL(endPointIP), Integer.valueOf(agentPort),agentVersion, magic, String.valueOf(registrationID), true);	
											
											/*Must be Logout*/	
											api.logout(sessionID);
								}
								catch(Exception e){
											String errMsg = "Fail to Register Agent";
											Debug.logInfo(errMsg, module);
											Map<String, Object> results = ServiceUtil.returnSuccess();

											results.put(ModelService.ERROR_MESSAGE, errMsg);
					
											/*Must be Logout*/	
											api.logout(sessionID);
											return results;	
								}
								/*End Register Agent for This Device */
			
								/*Start Insertion Location Related Data in Location Table */
			
								GenericValue newValue = delegator.makeValue("Location");
								String locationId = delegator.getNextSeqId("Location");
								newValue.set("locationId", locationId.trim());
								newValue.set("locationTypeId", "Device");
								newValue.set("statusId", "DEVICE_REGISTERED");
								newValue.set("partyId", orgId);
								newValue.set("parentLocationId", currentLocationId.trim());
								newValue.set("locationName", groupName.trim());
								newValue.set("parentLocationTypeId", "Gate");
								newValue.set("description", description);
								

								delegator.create(newValue);
			
								/*End Insertion Location Related Data in Location Table */
			
								/*Start Insertion DeviceRegistration Related Data in DeviceRegistration Table */

								GenericValue newValue2 = delegator.makeValue("DeviceRegistration");
								newValue2.set("partyId", orgId);
								newValue2.set("deviceType", deviceType.trim());
								newValue2.set("terminalId", partyId.trim());
								newValue2.set("locationId", locationId.trim());
								newValue2.set("terminalIP", terminalIP);
								newValue2.set("terminalName", groupName.trim());
								newValue2.set("terminalType", terminalType);
								newValue2.set("terminalSN", terminalSN);
								newValue2.set("firmewareVersion", firmewareVersion);
								newValue2.set("fliVersion", fliVersion);
								newValue2.set("userName", userName);
								newValue2.set("userPass", userPass);
								newValue2.set("endPointIP", endPointIP);
								newValue2.set("agentVersion", agentVersion);
								newValue2.set("type", type);
								newValue2.set("firsTimeRegister", "N");
								newValue2.set("lastUpdateRow", "0");
			
								delegator.create(newValue2);
			
								/*End Insertion DeviceRegistration Related Data in DeviceRegistration Table */

								result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
								result.put(ModelService.SUCCESS_MESSAGE, "Deivce is Registered Successfully");

					}
			}else if(deviceType.equals("Pegasus")){
				
				/*Start Parent Gate Status of This Device */
				List<GenericValue> gates = delegator.findByAnd("Location", UtilMisc.toMap(
							"locationId", currentLocationId, 
							"locationTypeId", "Gate",
							"parentLocationTypeId", "Floor"
						));

				GenericValue gate = EntityUtil.getFirst(gates);
				if (gate.get("statusId").equals("GATE_DEACTIVATED")) {
							String errMsg = "Fail to Register, Please First Registere Gate for this Device";
							Debug.logInfo(errMsg, module);
							Map<String, Object> results = ServiceUtil.returnSuccess();

							results.put(ModelService.ERROR_MESSAGE, errMsg);
							return results;
				}
				
				/*End Parent Gate Status of This Device */
				/*Start Check Duplicate Device Registration */
				List<GenericValue> deviceRegistrations = delegator.findByAnd("DeviceRegistration", UtilMisc.toMap(
						"terminalSN", terminalSN));
				List<GenericValue> isSameIPandAddresses = delegator.findByAnd("DeviceRegistration", UtilMisc.toMap(
						"terminalIP", terminalIP,"pegasusAddress",pegasusAddress));

				GenericValue deviceRegistration = EntityUtil.getFirst(deviceRegistrations);
				GenericValue isSameIPandAddress = EntityUtil.getFirst(isSameIPandAddresses);
				
				if (deviceRegistration!=null || isSameIPandAddress!=null) {
								String errMsg = "Fail to Register, This Device already Register";
								Debug.logInfo(errMsg, module);
								Map<String, Object> results = ServiceUtil.returnSuccess();

								results.put(ModelService.ERROR_MESSAGE, errMsg);	
								return results;
				}
				//AbstractAgent agent=connectionFactory.getConnection(terminalIP);
				AbstractAgent agent=connectionFactory.checkConenction(terminalIP);
				if(agent==null){
					agent=connectionFactory.getConnection(terminalIP);
					if(agent!=null){
						new AgentListener(agent);
						}else{
							String errMsg = "Fail to Register.";
							Map<String, Object> results = ServiceUtil.returnSuccess();
							results.put(ModelService.ERROR_MESSAGE, errMsg);	
							return results;	
						}
				}
				GenericValue newValue = delegator.makeValue("Location");
				String locationId = delegator.getNextSeqId("Location");
				newValue.set("locationId", locationId.trim());
				newValue.set("locationTypeId", "Device");
				newValue.set("statusId", "DEVICE_REGISTERED");
				newValue.set("partyId", orgId);
				newValue.set("parentLocationId", currentLocationId.trim());
				newValue.set("locationName", groupName.trim());
				newValue.set("parentLocationTypeId", "Gate");
				newValue.set("description", description);

				delegator.create(newValue);
				
				GenericValue newValue2 = delegator.makeValue("DeviceRegistration");
				newValue2.set("deviceType", deviceType.trim());
				newValue2.set("terminalId", partyId.trim());
				newValue2.set("locationId", locationId.trim());
				newValue2.set("terminalIP", terminalIP);
				newValue2.set("pegasusAddress", pegasusAddress);
				newValue2.set("terminalSN", terminalSN);
				newValue2.set("type", type);
				newValue2.set("lastUpdateRow", "0");

				delegator.create(newValue2);

				/*End Insertion DeviceRegistration Related Data in DeviceRegistration Table */

				result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
				result.put(ModelService.SUCCESS_MESSAGE, "Deivce is Registered Successfully");
			}
					
					
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			String errMsg = "GenericEntityException";
			Debug.logInfo(errMsg, module);
			Map<String, Object> results = ServiceUtil.returnSuccess();

			results.put(ModelService.ERROR_MESSAGE, errMsg);

			return results;
		}
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			String errMsg = "MalformedURLException";
			Debug.logInfo(errMsg, module);
			Map<String, Object> results = ServiceUtil.returnSuccess();

			results.put(ModelService.ERROR_MESSAGE, errMsg);

			return results;
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			String errMsg = "RemoteException,Fail to Connect Terminal IP";
			Debug.logInfo(errMsg, module);
			Map<String, Object> results = ServiceUtil.returnSuccess();

			results.put(ModelService.ERROR_MESSAGE, errMsg);

			return results;
		}
		return result;
	}
	

	public static Map<String, Object> createGates(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();

		String partyId = null;
		String currentLocationId = (String) context.get("currentLocationId");
		String groupName = (String) context.get("groupName");
		String orgId = (String) context.get("orgId");
		String description = (String) context.get("description");

		try {

					GenericValue newValue = delegator.makeValue("Location");
					partyId = delegator.getNextSeqId("Location");
					newValue.set("locationId", partyId.trim());
					newValue.set("locationTypeId", "Gate");
					newValue.set("statusId", "GATE_ACTIVATED");
					newValue.set("partyId", orgId);
					newValue.set("parentLocationId", currentLocationId.trim());
					newValue.set("locationName", groupName.trim());
					newValue.set("parentLocationTypeId", "Floor");
					newValue.set("description", description);

					delegator.create(newValue);

					result.put(ModelService.RESPONSE_MESSAGE,
					ModelService.RESPOND_SUCCESS);
					result.put(ModelService.SUCCESS_MESSAGE, "Gate Added Successfully");

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}
	

	public static Map<String, Object> createFloors(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();

		String partyId = null;
		String currentLocationId = (String) context.get("currentLocationId");
		String groupName = (String) context.get("groupName");
		String orgId = (String) context.get("orgId");
		String description = (String) context.get("description");

		try {
						GenericValue newValue = delegator.makeValue("Location");
						partyId = delegator.getNextSeqId("Location");
						newValue.set("locationId", partyId.trim());
						newValue.set("locationTypeId", "Floor");
						newValue.set("partyId", orgId);
						newValue.set("parentLocationId", currentLocationId.trim());
						newValue.set("locationName", groupName.trim());
						newValue.set("parentLocationTypeId", "Building");
						newValue.set("description", description);

						delegator.create(newValue);

						result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
						result.put(ModelService.SUCCESS_MESSAGE, "Floor Added Successfully");

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

	public static Map<String, Object> createBuilding(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();

		String partyId = null;
		String orgId = (String) context.get("orgId");
		String groupName = (String) context.get("groupName");
		String description = (String) context.get("description");

		try {
						GenericValue newValue = delegator.makeValue("Location");
						partyId = delegator.getNextSeqId("Location");
						newValue.set("locationId", partyId.trim());
						newValue.set("locationTypeId", "Building");
						newValue.set("partyId", orgId);
						newValue.set("locationName", groupName.trim());
						newValue.set("parentLocationTypeId", "Building");
						newValue.set("description", description);
			
						delegator.create(newValue);

						result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
						result.put(ModelService.SUCCESS_MESSAGE, "Building Added Successfully");

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

	public static Map<String, Object> unRegisterGate(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();

		String partyId = (String) context.get("userPrefValue");
		String currentLocationId = (String) context.get("currentLocationId");
		String statusId = (String) context.get("statusId");
		String sucess = "";

		try {

						List<GenericValue> locations = delegator.findByAnd("Location",
														UtilMisc.toMap("locationId", partyId,
																		"parentLocationId",currentLocationId, 
																		"locationTypeId", "Gate",
																		"parentLocationTypeId", "Floor"));

						GenericValue location = EntityUtil.getFirst(locations);
						if (UtilValidate.isEmpty(location)) {
							
										sucess.concat("Fail to Update, Location missing");
										Map<String, Object> results = ServiceUtil.returnSuccess();

										results.put(ModelService.ERROR_MESSAGE, sucess);
										return results;
						}
						/* Check Gate Current Status*/
						
						if (statusId.equalsIgnoreCase("GATE_ACTIVATED")) {
									
				
										/* Get all Devices Under this Gate*/
										List<GenericValue> devices = delegator.findByAnd("Location",
												UtilMisc.toMap("locationTypeId", "Device",
														"parentLocationId", partyId,
														"parentLocationTypeId", "Gate"));
									
											
										for (GenericValue device : devices) {
															
																/* Get user name and password for this device registered agent*/
																AgentDto agentutil=getAgentUtil(String.valueOf(device.get("locationId")),delegator);
						
																if(agentutil!=null){	
							
																		try {
																					ACTAtekPortType api = locator.getACTAtek(new java.net.URL(agentutil.getDeviceURL()));
							
																					long sessionID = api.login(agentutil.getDeviceUser(), agentutil.getDevicePass());
																					
																					/* Unregistered agent for this device*/
																					api.unregisterAgent(sessionID, agentutil.getEndPointIP(), Integer.valueOf(agentPort));
						
																					/* Get all Employee Under this Device*/
																					List<GenericValue> employees = delegator.findByAnd(
																							"Location", UtilMisc.toMap("parentLocationId",device.get("locationId"),
																														"locationTypeId", "Employee",
																														"parentLocationTypeId", "Device"));
																				
						
																					for (GenericValue employee : employees) {
																								
																									/* Unregister this Employee*/
																									employee.set("statusId", "EMPL_UNREGISTERED");
																									delegator.store(employee);
																									
																									/* Deactivate this Employee from atatek device*/
																									String userId=	getUserId(String.valueOf(employee.get("locationId")),delegator);
																									deactivate(api,userId,sessionID);
										
																					}		
							
																					api.logout(sessionID);
						
																		} catch (RemoteException e) {
																			sucess.concat("RemoteException");
																			Map<String, Object> results = ServiceUtil.returnSuccess();

																			results.put(ModelService.ERROR_MESSAGE, sucess);
																			return results;
																		} catch (MalformedURLException e) {
																			sucess.concat("MalformedURLException");
																			Map<String, Object> results = ServiceUtil.returnSuccess();

																			results.put(ModelService.ERROR_MESSAGE, sucess);
																			return results;
																		} catch (ServiceException e) {
																			sucess.concat("ServiceException");
																			Map<String, Object> results = ServiceUtil.returnSuccess();

																			results.put(ModelService.ERROR_MESSAGE, sucess);
																			return results;
																		}
																
																		/* Unregister this Device*/
																		device.set("statusId", "DEVICE_UNREGISTERED");	
																		delegator.store(device);
																}
															
										}
								
										/* Deactivate this Gate*/
										location.set("statusId", "GATE_DEACTIVATED");
										delegator.store(location);

										result.put(ModelService.RESPONSE_MESSAGE,
												ModelService.RESPOND_SUCCESS);
										result.put(ModelService.SUCCESS_MESSAGE, "Gate successfully deactivated");
						} else {
								/* Activate this Gate*/
								location.set("statusId", "GATE_ACTIVATED");
								delegator.store(location);

								result.put(ModelService.RESPONSE_MESSAGE,
										ModelService.RESPOND_SUCCESS);
								result.put(ModelService.SUCCESS_MESSAGE, " Gate successfully activated");
						}

			


		} catch (GenericEntityException e) {
			
			sucess.concat("GenericEntityException");
			Map<String, Object> results = ServiceUtil.returnSuccess();

			results.put(ModelService.ERROR_MESSAGE, sucess);
			return results;
		}

		return result;

	}
	
	public static Map<String, Object> updateDeviceStaus(DispatchContext dctx,
			Map<String, ? extends Object> context) {
				Map<String, Object> result = ServiceUtil.returnSuccess();
				Delegator delegator = dctx.getDelegator();
		
				String locationId = (String) context.get("userPrefValue");
				
				String firsTimeRegister = (String) context.get("firsTimeRegister");
				
				List<GenericValue> deviceRegistrations=null;
			
				try {
					deviceRegistrations = delegator.findByAnd("DeviceRegistration",UtilMisc.toMap("locationId", locationId));
					GenericValue	deviceRegistration=	EntityUtil.getFirst(deviceRegistrations);
					if (!UtilValidate.isEmpty(deviceRegistration)){
					  if(firsTimeRegister.equalsIgnoreCase("Y")){
						  deviceRegistration.set("firsTimeRegister", "N");
					  }else if(firsTimeRegister.equalsIgnoreCase("N")){
						  deviceRegistration.set("firsTimeRegister", "Y");
					  }
					}
					delegator.store(deviceRegistration);
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
				
			return result;
		}

	public static Map<String, Object> unRegisterDevice(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();

		String partyId = (String) context.get("userPrefValue");
		List<GenericValue> deviceRegistrations = null;
		try {
			deviceRegistrations = delegator.findByAnd("DeviceRegistration",
					UtilMisc.toMap("locationId", partyId));
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
		}

		GenericValue deviceRegistration = EntityUtil.getFirst(deviceRegistrations);
		String deviceType=(String) deviceRegistration.get("deviceType");
		String terminalIP=(String) deviceRegistration.get("terminalIP");
		String terminalSN=(String) deviceRegistration.get("terminalSN");
		String currentLocationId = (String) context.get("currentLocationId");
		String statusId = (String) context.get("statusId");
		String sucess = "";

		try {

			List<GenericValue> locations = delegator.findByAnd("Location",
					UtilMisc.toMap("locationId", partyId, "parentLocationId",
							currentLocationId, "locationTypeId", "Device",
							"parentLocationTypeId", "Gate"));

			GenericValue location = EntityUtil.getFirst(locations);
			if (UtilValidate.isEmpty(location)) {
				sucess.concat("Fail to Update, Location missing");
				Map<String, Object> results = ServiceUtil.returnSuccess();

				results.put(ModelService.ERROR_MESSAGE, sucess);
				return results;
			}
			try {
				
				/* Get user name and password for this device registered agent*/
				AgentDto agentutil=getAgentUtil(String.valueOf(location.get("locationId")),delegator);
				if(deviceType.equals("Actatek")){
				ACTAtekPortType api = locator.getACTAtek(new java.net.URL(
						agentutil.getDeviceURL()));
			
				long sessionID = api.login(agentutil.getDeviceUser(), agentutil.getDevicePass());
			
				/* Check Device Current Status*/
				if (statusId.equalsIgnoreCase("DEVICE_REGISTERED")) {
						
				
						if(agentutil!=null){	

							/* Get all Employee Under this Device*/
							List<GenericValue> employees = delegator.findByAnd("Location",
									UtilMisc.toMap("locationTypeId", "Employee",
													"parentLocationId", partyId,
													"parentLocationTypeId", "Device"));
							
			
							for (GenericValue employee : employees) {
									
									/* Unregister this Employee*/
									employee.set("statusId", "EMPL_UNREGISTERED");
									delegator.store(employee);
									
									/* Deactivate this Employee from atatek device*/
									String userId=	getUserId(String.valueOf(employee.get("locationId")),delegator);
									deactivate(api,userId,sessionID);
								
							}
			
							/* Unregistered agent for this device*/
							api.unregisterAgent(sessionID, agentutil.getEndPointIP(), Integer.valueOf(agentPort));
						
					
				/* Unregister this Device*/			
				location.set("statusId", "DEVICE_UNREGISTERED");			
				delegator.store(location);
			 }			
						/*Must be Logout*/	
						api.logout(sessionID);
						
						result.put(ModelService.RESPONSE_MESSAGE,
								ModelService.RESPOND_SUCCESS);
						result.put(ModelService.SUCCESS_MESSAGE, "Device sucessfully unregistered");
			} else {

				
				/* Start Check Parent Gate Status of This Device */
				List<GenericValue> gates = delegator.findByAnd("Location",
						UtilMisc.toMap("locationId", currentLocationId,
								"locationTypeId", "Gate",
								"parentLocationTypeId", "Floor"));

				GenericValue gate = EntityUtil.getFirst(gates);
				if (gate.get("statusId").equals("GATE_DEACTIVATED")) {
					String errMsg = "Fail to Register, Please First Register Gate for this Device";
					Debug.logInfo(errMsg, module);
					Map<String, Object> results = ServiceUtil.returnSuccess();

					results.put(ModelService.ERROR_MESSAGE, errMsg);
					/*Must be Logout*/	
					api.logout(sessionID);	
					return results;
				}
				/* End Check Parent Gate Status of This Device */
				
				
				
				try{
					/* Register agent for this device*/
					long registrationID=System.currentTimeMillis();
					 api.registerAgent(sessionID, agentutil.getEndPointIP(), Integer.valueOf(agentPort),agentutil.getAgentVersion(), magic, String.valueOf(registrationID), true);	
					/*Must be Logout*/	
					
					api.logout(sessionID);
				}
				catch(Exception e){
						String errMsg = "Fail to Register Agent";
						Debug.logInfo(errMsg, module);
						Map<String, Object> results = ServiceUtil.returnSuccess();

						results.put(ModelService.ERROR_MESSAGE, errMsg);
		
						/*Must be Logout*/	
						api.logout(sessionID);
						return results;	
				}
				
				/*Register This Device */
				location.set("statusId", "DEVICE_REGISTERED");
				delegator.store(location);
				
				result.put(ModelService.RESPONSE_MESSAGE,
						ModelService.RESPOND_SUCCESS);
				result.put(ModelService.SUCCESS_MESSAGE, "Device sucessfully registered");
					}
				}else if(deviceType.equals("Pegasus")){
					if (statusId.equalsIgnoreCase("DEVICE_REGISTERED")) {
						List<GenericValue> employees = delegator.findByAnd("Location",
								UtilMisc.toMap("locationTypeId", "Employee",
												"parentLocationId", partyId,
												"parentLocationTypeId", "Device"));
						
		
						for (GenericValue employee : employees) {
								
								/* Unregister this Employee*/
								employee.set("statusId", "EMPL_UNREGISTERED");
								delegator.store(employee);						
						}
						AbstractAgent agent=connectionFactory.checkConenction(terminalSN);
						String closeStatus="success";
						if(agent!=null){
							closeStatus=agent.close();
						}
						if(closeStatus.equals("success")){
						location.set("statusId", "DEVICE_UNREGISTERED");	
						result.put(ModelService.SUCCESS_MESSAGE, "Device sucessfully unregistered");
						delegator.store(location);
						}else {
							Map<String, Object> results = ServiceUtil.returnSuccess();

							results.put(ModelService.ERROR_MESSAGE, "Failed to unregister device");
							return results;	
						}
					}else if (statusId.equalsIgnoreCase("DEVICE_UNREGISTERED")){
						/* Start Check Parent Gate Status of This Device */
						List<GenericValue> gates = delegator.findByAnd("Location",
								UtilMisc.toMap("locationId", currentLocationId,
										"locationTypeId", "Gate",
										"parentLocationTypeId", "Floor"));

						GenericValue gate = EntityUtil.getFirst(gates);
						if (gate.get("statusId").equals("GATE_DEACTIVATED")) {
							String errMsg = "Fail to Register, Please First Register Gate for this Device";
							Debug.logInfo(errMsg, module);
							Map<String, Object> results = ServiceUtil.returnSuccess();

							results.put(ModelService.ERROR_MESSAGE, errMsg);
							return results;
						}
						/* End Check Parent Gate Status of This Device */
						AbstractAgent agent=connectionFactory.checkConenction(terminalIP);
						//new AgentListener(agent);
						if(agent==null){
							agent=connectionFactory.getConnection(terminalIP);
							if(agent!=null){
								new AgentListener(agent);
								}else{
									String errMsg = "Fail to Register.";
									Map<String, Object> results = ServiceUtil.returnSuccess();
									results.put(ModelService.ERROR_MESSAGE, errMsg);	
									return results;	
								}
						}
						location.set("statusId", "DEVICE_REGISTERED");
						delegator.store(location);
						result.put(ModelService.SUCCESS_MESSAGE, "Device sucessfully registered");
					}
					
				}
			
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				String errMsg = "RemoteException";
				Debug.logInfo(errMsg, module);
				Map<String, Object> results = ServiceUtil.returnSuccess();

				results.put(ModelService.ERROR_MESSAGE, errMsg);

				return results;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				String errMsg = "MalformedURLException";
				Debug.logInfo(errMsg, module);
				Map<String, Object> results = ServiceUtil.returnSuccess();

				results.put(ModelService.ERROR_MESSAGE, errMsg);

				return results;
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				String errMsg = "ServiceException";
				Debug.logInfo(errMsg, module);
				Map<String, Object> results = ServiceUtil.returnSuccess();

				results.put(ModelService.ERROR_MESSAGE, errMsg);

				return results;
			}

		

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			String errMsg = "GenericEntityException";
			Debug.logInfo(errMsg, module);
			Map<String, Object> results = ServiceUtil.returnSuccess();

			results.put(ModelService.ERROR_MESSAGE, errMsg);

			return results;
		}

		return result;

	}

	public static Map<String, Object> unRegisterEmployee(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();

		String partyId = (String) context.get("userPrefValue");
		String currentLocationId = (String) context.get("currentLocationId");
		String statusId = (String) context.get("statusId");
		List<GenericValue> deviceRegistrations = null;
		try {
			deviceRegistrations = delegator.findByAnd("DeviceRegistration",
					UtilMisc.toMap("locationId", currentLocationId));
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		GenericValue deviceRegistration = EntityUtil.getFirst(deviceRegistrations);
		String deviceType=(String) deviceRegistration.get("deviceType");
		String sucess = "";

		try {
			
			
							List<GenericValue> locations = delegator.findByAnd("Location",UtilMisc.toMap("locationId", partyId, 
																										    "parentLocationId",currentLocationId, 
																											"locationTypeId", "Employee",
																											"parentLocationTypeId", "Device"));

							GenericValue location = EntityUtil.getFirst(locations);
							if (UtilValidate.isEmpty(location)) {
										sucess.concat("Fail to Register, Location Missing");
										result.put(ModelService.RESPONSE_MESSAGE,ModelService.ERROR_MESSAGE);
										result.put(ModelService.ERROR_MESSAGE, sucess);
										return result;
							}
			
							List<GenericValue> devices = delegator.findByAnd("Location",
														UtilMisc.toMap("locationId", currentLocationId,
																		"locationTypeId", "Device",
																		"parentLocationTypeId", "Gate"));

							GenericValue device = EntityUtil.getFirst(devices);
			
							/* Get user name and password for this device registered agent*/
							AgentDto agentutil=getAgentUtil(String.valueOf(device.get("locationId")),delegator);
			
							if(agentutil!=null){	
	
										String userId=	getUserId(String.valueOf(location.get("locationId")),delegator);
										/*Check Employee Current Status	*/
										if (statusId.equalsIgnoreCase("EMPL_UNREGISTERED")) {
				
											if (device.get("statusId").equals("DEVICE_UNREGISTERED")) {
														String errMsg = "Fail to Register, Please First Register Device for this Employee";
														Debug.logInfo(errMsg, module);
														Map<String, Object> results = ServiceUtil.returnSuccess();

														results.put(ModelService.ERROR_MESSAGE, errMsg);

														return results;
											}
							
										
											if(deviceType.equals("Actatek")){
											try {
				
														ACTAtekPortType api = locator.getACTAtek(new java.net.URL(agentutil.getDeviceURL()));
					
														long sessionID = api.login(agentutil.getDeviceUser(), agentutil.getDevicePass());
														/*Activate this user on actatek Device*/
														activate(api,userId,sessionID);
														api.logout(sessionID);
						
											} catch (RemoteException e) {
						
												String errMsg = "RemoteException";
												Debug.logInfo(errMsg, module);
												Map<String, Object> results = ServiceUtil.returnSuccess();

												results.put(ModelService.ERROR_MESSAGE, errMsg);

												return results;
											} catch (MalformedURLException e) {
						
												String errMsg = "MalformedURLException";
												Debug.logInfo(errMsg, module);
												Map<String, Object> results = ServiceUtil.returnSuccess();

												results.put(ModelService.ERROR_MESSAGE, errMsg);

												return results;
											} catch (ServiceException e) {
						
												String errMsg = "ServiceException";
												Debug.logInfo(errMsg, module);
												Map<String, Object> results = ServiceUtil.returnSuccess();

												results.put(ModelService.ERROR_MESSAGE, errMsg);

												return results;
											}
						
											}
							/*Register Employee*/
							location.set("statusId", "EMPL_REGISTERED");
							delegator.store(location);

							result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
							result.put(ModelService.SUCCESS_MESSAGE, "Selected user is successfully registered");
							
						} else {
							
							if(deviceType.equals("Actatek")){
								try {
				
									ACTAtekPortType api = locator.getACTAtek(new java.net.URL(agentutil.getDeviceURL()));
				
									long sessionID = api.login(agentutil.getDeviceUser(), agentutil.getDevicePass());
									
									/*Deactivate this user on actatek Device*/
									deactivate(api,userId,sessionID);
									api.logout(sessionID);
						
								} catch (RemoteException e) {
						
									String errMsg = "RemoteException";
									Debug.logInfo(errMsg, module);
									Map<String, Object> results = ServiceUtil.returnSuccess();

									results.put(ModelService.ERROR_MESSAGE, errMsg);

									return results;
								} catch (MalformedURLException e) {
									String errMsg = "MalformedURLException";
									Debug.logInfo(errMsg, module);
									Map<String, Object> results = ServiceUtil.returnSuccess();

									results.put(ModelService.ERROR_MESSAGE, errMsg);

									return results;
								} catch (ServiceException e) {
									String errMsg = "ServiceException";
									Debug.logInfo(errMsg, module);
									Map<String, Object> results = ServiceUtil.returnSuccess();

									results.put(ModelService.ERROR_MESSAGE, errMsg);

									return results;
								}
				
							}		
				/*Register Employee*/		
				location.set("statusId", "EMPL_UNREGISTERED");
				delegator.store(location);
								
				result.put(ModelService.RESPONSE_MESSAGE,
						ModelService.RESPOND_SUCCESS);
				result.put(ModelService.SUCCESS_MESSAGE, "Selected user is successfully unregistered");
			}

		
		}

		} catch (GenericEntityException e) {
			String errMsg = "GenericEntityException";
			Debug.logInfo(errMsg, module);
			Map<String, Object> results = ServiceUtil.returnSuccess();

			results.put(ModelService.ERROR_MESSAGE, errMsg);

			return results;
		}

		return result;

	}
	public static Map<String, Object> sinkDevice(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		logger.debug("sinkDevice started---->");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		String partyId = (String) context.get("userPrefValue");
		String currentLocationId = (String) context.get("currentLocationId");
		Integer lastUpdateRowAfterRead=0;
		String sucess = "";
		List<GenericValue> devices=null;
		try {
			devices = delegator.findByAnd("DeviceRegistration",UtilMisc.toMap("locationId", partyId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GenericValue device = EntityUtil.getFirst(devices);
		String lastUpdateRow=(String)device.get("lastUpdateRow");
		String terminalSN=(String)device.get("terminalSN");
		List<GenericValue> employeeLogs = null;
		List<EntityExpr> exprs = FastList.newInstance();
		if(lastUpdateRow!=null){
		if(Integer.parseInt(lastUpdateRow)==0){
			exprs.add(EntityCondition.makeCondition("logId", EntityOperator.GREATER_THAN_EQUAL_TO,"0"));
			exprs.add(EntityCondition.makeCondition("terminalSN", EntityOperator.EQUALS, terminalSN));
		}else{
			exprs.add(EntityCondition.makeCondition("logId", EntityOperator.GREATER_THAN_EQUAL_TO,lastUpdateRow));	
			exprs.add(EntityCondition.makeCondition("terminalSN", EntityOperator.EQUALS, terminalSN));
		}
		try {
			employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs,EntityOperator.AND), null, null, null, true);
		} catch (Exception e) {
			logger.debug("Exception to get deviceRegistrations in sinkDevice---->"+e);
		}
		}else{
			logger.debug("Field lastUpdateRow in Table DeviceRegistration  is null");
			String errMsg = "Field lastUpdateRow in Table DeviceRegistration  is null";
			Debug.logInfo(errMsg, module);
			Map<String, Object> results = ServiceUtil.returnSuccess();

			results.put(ModelService.ERROR_MESSAGE, errMsg);
			return results;
		}
		if(employeeLogs.size()>0){
			int logIdToBeStored=0;
			try{				
				logger.debug("reached here 111"+employeeLogs.size());
				//String database="/home/rajib/Projects/Docs/Pga.mdb";
				Table table = DatabaseBuilder.open(new File("/home/rajib/data/Pga.mdb")).getTable("Access");
				logger.debug("reached here 222");
				for(GenericValue employeeLog:employeeLogs){
					String logId=(String)employeeLog.get("logId");
					String userLoginId=(String)employeeLog.get("userLoginId");
					List<GenericValue> employees=null;
					String employeeRFID=null;
					String employeeName=null;
					try {
						employees = delegator.findByAnd("Person",UtilMisc.toMap("partyId", userLoginId));
						GenericValue employee=EntityUtil.getFirst(employees);
						employeeRFID=(String)employee.get("cardId");
						employeeName=(String)employee.get("firstName");
					} catch (GenericEntityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Timestamp logtimeStamp=(Timestamp)employeeLog.get("logtimeStamp");
					String eventType=(String)employeeLog.get("eventType");
					String logTerminalSN=(String)employeeLog.get("terminalSN");
					String senderTerminal=(String)employeeLog.get("senderTerminal");	
					String year=null;
					String month=null;
					String date=null;
					String hour=null;
					String min=null;
					year=String.valueOf(1900+logtimeStamp.getYear());
					if(logtimeStamp.getMonth()<10){
						month="0"+logtimeStamp.getMonth();
					}else{
						month=String.valueOf(logtimeStamp.getMonth());
					}
					if(logtimeStamp.getDate()<10){
						date="0"+logtimeStamp.getDate();
					}else{
						date=String.valueOf(logtimeStamp.getDate());
					}
					if(logtimeStamp.getHours()<10){
						hour="0"+logtimeStamp.getHours();
					}else{
						hour=String.valueOf(logtimeStamp.getHours());
					}
					if(logtimeStamp.getMinutes()<10){
						min="0"+logtimeStamp.getMinutes();
					}else{
						min=String.valueOf(logtimeStamp.getMinutes());
					}
					String showDate=month+date+year;
					String showTime=hour+min;
					table.addRow(0,employeeRFID,userLoginId,employeeName,"DoorNo",logTerminalSN,logtimeStamp,"Legal Access","","",0,showDate,showTime,eventType);
					if(Integer.parseInt(logId)>logIdToBeStored){
						logIdToBeStored=Integer.parseInt(logId);
					}
				}
				
			}catch(IOException e){
				logger.debug("Failed to Write in .mdb file---->"+e);
				String errMsg = "GenericEntityException";
				Debug.logInfo(errMsg, module);
				Map<String, Object> results = ServiceUtil.returnSuccess();

				results.put(ModelService.ERROR_MESSAGE, errMsg);
				return results;
			}catch (Exception e) {
				logger.debug("Failed to Write in .mdb file---->"+e);
			}
			try{
				
			List<GenericValue> deviceRegistrations = delegator.findByAnd("DeviceRegistration",UtilMisc.toMap("locationId", partyId));

			GenericValue deviceUpdate = EntityUtil.getFirst(deviceRegistrations);
			deviceUpdate.set("lastUpdateRow", String.valueOf(logIdToBeStored));
			
			delegator.store(deviceUpdate);
			//deviceUpdate.store();
			//delegator.storeAll(deviceRegistrations);
			logger.debug("locationId3---->"+partyId);
			}catch(Exception e){
				logger.debug("Failed to update DeviceRegistration---->"+e);
				String errMsg = "GenericEntityException";
				Debug.logInfo(errMsg, module);
				Map<String, Object> results = ServiceUtil.returnSuccess();
				results.put(ModelService.ERROR_MESSAGE, errMsg);

				return results;
			}
		}
		result.put(ModelService.RESPONSE_MESSAGE,
		ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Device Sink Successful.");
		logger.debug("reached here 1sahcfgcs");
		return result;

	}
	private static String getUserId(String locationId,Delegator delegator){
		try {
			List<GenericValue> employeeRegistrations = delegator.findByAnd("EmployeeRegistration",
					UtilMisc.toMap("locationId", locationId));
			GenericValue employeeRegistration = EntityUtil.getFirst(employeeRegistrations);
			if (!UtilValidate.isEmpty(employeeRegistration)) {
				return String.valueOf(employeeRegistration.get("userLoginId"));
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private static String deactivate(ACTAtekPortType api,String userId,long sessionID){
		
			
			try {
				
				if(userId!=null){
					api.deactivateUser(sessionID, userId);
					
					return "Success";	
				}else{
				
					return "Fail";	
				}
			
				
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		
		return "Fail";			
	}
	
	private static String activate(ACTAtekPortType api,String userId,long sessionID){
		try {
	
				if(userId!=null){
					api.activateUser(sessionID, userId);
					
					return "Success";	
				}else{
				
					return "Fail";	
				}
			
				
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		return "Fail";	
	}
	
	private static String getAgentUri(String terminalIp){
		return "http://"+terminalIp.trim()+"/cgi-bin/rpcrouter".trim();
	}
	
	private static String getEndPointURL(String endPointIP){
		return "http://"+endPointIP.trim()+":8080/agent/services/ACTAtekAgent".trim();	
	}
	
	private static AgentDto getAgentUtil(String locationid,Delegator delegator){
		
		AgentDto agentUtil=new AgentDto();
		try {
						List<GenericValue> deviceRegistrations = delegator.findByAnd("DeviceRegistration",UtilMisc.toMap("locationId", locationid));
						GenericValue deviceRegistration = EntityUtil.getFirst(deviceRegistrations);
						
						if (UtilValidate.isEmpty(deviceRegistration)){
								return null;
						}
						else{
							
								agentUtil.setDevicePass(String.valueOf(deviceRegistration.get("userPass")));
								agentUtil.setDeviceURL(getAgentUri(String.valueOf(deviceRegistration.get("terminalIP"))));
								agentUtil.setDeviceUser(String.valueOf(deviceRegistration.get("userName")));
								agentUtil.setEndPointIP(getEndPointURL(String.valueOf(deviceRegistration.get("endPointIP"))));
								agentUtil.setAgentVersion(String.valueOf(deviceRegistration.get("agentVersion")));
						}
			
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			return null;
		}
		return agentUtil;
	}
	
	public static String getInOutStatus(Delegator delegator, String locationId){

		List<GenericValue> locations = FastList.newInstance();
		try {
			 locations = delegator.findByAnd("Location",UtilMisc.toMap("locationId", locationId));
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return "";	
		}
		GenericValue location = EntityUtil.getFirst(locations);
		if (!UtilValidate.isEmpty(location)){
			
			Timestamp time=(Timestamp)location.get("lastUpdatedStamp");
			if(compareLogtime(time)){
				String inOutStatus=String.valueOf(location.get("currentInOutStatus"));
				if(inOutStatus!=null){
					return inOutStatus;
				}else{
					return "";	
				}
				 
			}else{
				return "";	
			}
		
		}
		

	return "";	

	}
	
	
	public static List<String> getCurrentEmpStatus(Delegator delegator, String locationId,String partyId){
		
		List<String> currentEmpStatus=new ArrayList<String>();
		GenericValue location=null;
		try {
			location = delegator.findByPrimaryKey("Location",UtilMisc.toMap("locationId", locationId));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
	
		if (location != null){
			
			String	locationTypeId=String.valueOf(location.get("locationTypeId"));
			String currentLocationId=String.valueOf(location.get("locationId"));
			
			if(locationTypeId.equalsIgnoreCase("Device")){

			List<GenericValue>	locations=getchild(delegator,currentLocationId);

				currentEmpStatus=getList(delegator,locations,partyId);

			}
			else{
				List<GenericValue> emloyeelist=new ArrayList<GenericValue>();

				List<GenericValue>	locations1=getchild(delegator,currentLocationId);

				Iterator<GenericValue> iterator1 = locations1.iterator();
				while (iterator1.hasNext()) {

					GenericValue	location1=(GenericValue) iterator1.next();

					//for Gate
					if(String.valueOf(location1.get("locationTypeId")).equalsIgnoreCase("Device")){
						List<GenericValue>	employees1=getchild(delegator,String.valueOf(location1.get("locationId")));
						for(GenericValue emp1:employees1){
							emloyeelist.add(emp1);
						}
					}
					else{
						List<GenericValue>	employees1=getchild(delegator,String.valueOf(location1.get("locationId")));
						for(GenericValue emp1:employees1){
							//for Floor
							if(String.valueOf(emp1.get("locationTypeId")).equalsIgnoreCase("Device")){
								List<GenericValue> employees2=getchild(delegator,String.valueOf(emp1.get("locationId")));
								for(GenericValue emp2:employees2){
									emloyeelist.add(emp2);
								}
							}else{

								List<GenericValue>	employees2=getchild(delegator,String.valueOf(emp1.get("locationId")));
								for(GenericValue emp2:employees2){
									//for Building
									if(String.valueOf(emp2.get("locationTypeId")).equalsIgnoreCase("Device")){
										List<GenericValue> employees3=getchild(delegator,String.valueOf(emp2.get("locationId")));
										for(GenericValue emp3:employees3){
											emloyeelist.add(emp3);
										}
									}
								}
							}
						}
					}
				}
				
				currentEmpStatus=getList(delegator,emloyeelist,partyId);

			
				
			}
			
	
		
		}
		

	return currentEmpStatus;	

	}
	
	
	public static boolean compareLogtime(Date time){

		Date date= new Date();

		Calendar calendarStart = Calendar.getInstance();
		calendarStart.setTime(date);
		calendarStart.set(Calendar.HOUR_OF_DAY, 0);
		calendarStart.set(Calendar.MINUTE, 0);
		calendarStart.set(Calendar.SECOND, 0);
		calendarStart.set(Calendar.MILLISECOND, 0);


		Calendar calendarEnd = Calendar.getInstance();
		calendarEnd.setTime(date);
		calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
		calendarEnd.set(Calendar.MINUTE, 59);
		calendarEnd.set(Calendar.SECOND, 59);
		calendarEnd.set(Calendar.MILLISECOND, 999);


		return (time.before((calendarEnd.getTime())) && time.after((calendarStart.getTime())));
	}
	
	public static List<GenericValue>  getchild(Delegator delegator,String locationId){
		List<GenericValue> locations = null;
		try {
			locations = delegator.findByAnd("Location",UtilMisc.toMap("parentLocationId", locationId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return locations;
	}
	
	public static List<String>  getList(Delegator delegator,List<GenericValue>	locations,String partyId){

		List<String> currentEmpStatus=new ArrayList<String>();

		int registeredEmployee=0;
		int inEmployee=0;
		int outEmployee=0;
		int atPresents=0;
		int inAbsent=0;

		Iterator<GenericValue> iterator = locations.iterator();
		
		while (iterator.hasNext()) {

			GenericValue	location=(GenericValue) iterator.next();


			if(String.valueOf(location.get("statusId")).equalsIgnoreCase("EMPL_REGISTERED") && String.valueOf(location.get("partyId")).equalsIgnoreCase(partyId)){

				registeredEmployee++;

				

			String	userid=getUserId(delegator,String.valueOf(location.get("locationId")));

			List<GenericValue>	emlogs=getEmpLogs(delegator,userid);


				for(GenericValue emlog:emlogs){
					
					Timestamp	time=(Timestamp)emlog.get("logtimeStamp");
					
					if(String.valueOf(emlog.get("eventType")).equalsIgnoreCase("IN")){

						if(compareLogtime(time)){
							inEmployee++;
						}
					}
					else if(String.valueOf(emlog.get("eventType")).equalsIgnoreCase("OUT")){
						if(compareLogtime(time)){
							outEmployee++;
						}
					}

				}


			}
		}
		
		atPresents=inEmployee-outEmployee;
		inAbsent=registeredEmployee-inEmployee;
		
		currentEmpStatus.add(String.valueOf(registeredEmployee));
		currentEmpStatus.add(String.valueOf(inEmployee));
		currentEmpStatus.add(String.valueOf(outEmployee));
		currentEmpStatus.add(String.valueOf(atPresents));
		currentEmpStatus.add(String.valueOf(inAbsent));

		return currentEmpStatus;
	}
	public static String getDeviceType(Delegator delegator, String locationId){
		String retVal=null;
		List<GenericValue> devices=null;
		try {
			devices = delegator.findByAnd("DeviceRegistration",UtilMisc.toMap("locationId", locationId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GenericValue device = EntityUtil.getFirst(devices);
		retVal=(String)device.get("deviceType");
		return retVal;
	}
	public static String getUserId(Delegator delegator,String locationId){
		
	
	
		List<GenericValue> employeeRegistrations = null;
		try {
			employeeRegistrations = delegator.findByAnd("EmployeeRegistration",UtilMisc.toMap("locationId", locationId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GenericValue	employeeRegistration=	EntityUtil.getFirst(employeeRegistrations);
			if (!UtilValidate.isEmpty(employeeRegistration)){
				return String.valueOf(employeeRegistration.get("userLoginId"));
			}
			return null;
		}
 public static List<GenericValue> getEmpLogs(Delegator delegator,String userid){
	 
	
	 try {
		List<GenericValue> employeeLogs= delegator.findByAnd("EmployeeLog",UtilMisc.toMap("userLoginId", userid));
		return employeeLogs;
		
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
 }
 
	public String filterEmployee(String rfId,String terminalSN, Delegator delegator) {
		String retVal = null;
		try {
			List<GenericValue> employee = delegator.findByAnd(
					"Person",
					UtilMisc.toMap("cardId",String.valueOf(Integer.parseInt(rfId))));
			GenericValue employeeGenericValue = EntityUtil.getFirst(employee);
			System.out.println("rfId--->" + rfId);

			if (UtilValidate.isEmpty(employeeGenericValue)) {
				System.out.println("================================Employee not found=========================================");
				return null;
			} else {
				String partyId = String.valueOf(employeeGenericValue
						.get("partyId"));
				List<GenericValue> employeeIdList = delegator.findByAnd(
						"Party", UtilMisc.toMap("partyId", partyId));
				GenericValue employeeIdGenericValue = EntityUtil
						.getFirst(employeeIdList);

				if (UtilValidate.isEmpty(employeeIdGenericValue)) {
					return null;
				} else {
					String employeeId = String.valueOf(employeeGenericValue
							.get("partyId"));
					List<GenericValue> employeeRegistrations = delegator.findByAnd("EmployeeRegistration", UtilMisc.toMap(
							"userLoginId",employeeId,
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
						retVal = employeeId;	
						}
					}
					}
				}

			}

		} catch (GenericEntityException e) {
			System.out.println("Exception in filterEmployee--->" + e);
			return null;
		}
		
		return retVal;
	}
	
	public static boolean saveEmployeeLog(Delegator delegator,
			Map<String, ? extends Object> context) {
		boolean flag=false;
		String logId = null;
		String userID = (String) context.get("userID");
		String partyId = (String) context.get("partyId");
		String timestamp = (String) context.get("timestamp");
		String trigger = (String) context.get("trigger");
		String terminalSN = (String) context.get("terminalSN");
		String sender = (String) context.get("sender");

		try {
			
			GenericValue emplog=	delegator.findByPrimaryKey("EmployeeLog", 
					UtilMisc.toMap("partyId", partyId,
								   "logtimeStamp", new java.sql.Timestamp(Long.valueOf(timestamp))
								  ));
			
			if(UtilValidate.isEmpty(emplog)){
				
				GenericValue newValue = delegator.makeValue("EmployeeLog");
				logId = delegator.getNextSeqId("EmployeeLog");
				newValue.set("logId", logId);
				newValue.set("userLoginId", userID);
				newValue.set("partyId", partyId);
				newValue.set("logtimeStamp",
				new java.sql.Timestamp(Long.valueOf(timestamp)));
				newValue.set("eventType", trigger);
				newValue.set("terminalSN", terminalSN);
				newValue.set("senderTerminal", sender);
				delegator.create(newValue);
				
				flag=true;
			}
		
			
		} catch (GenericEntityException e) {
			System.out.println("Exception in saveEmployeeLog--->" + e);
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}
	public static GenericValue checkEmpExistence(GenericDelegator delegator,String cardID,Timestamp currentDate){
		
		try {
			List<GenericValue>	persons = delegator.findByAnd("Person",UtilMisc.toMap("cardId", cardID));
			for(GenericValue person:persons){
				String partyId=(String)person.get("partyId");
				boolean validateEmployee=AttendanceUtils.filterValidEmployeeByDate(delegator, partyId, currentDate);
				if(validateEmployee && !UtilValidate.isEmpty(person)){
						return person;
				}
			}
		} catch (GenericEntityException e) {
			return null;
		}
		
		return null;
	}
	public static Map<String,Object> getEmploymentInfo(GenericDelegator delegator,String employeeId,String orgPartyId){
		String partyId=AttendanceUtils.getPartyIDFromUserLoginID(delegator,employeeId);
		logger.debug("partyId------>"+partyId);
			SimpleDateFormat sdf = new SimpleDateFormat(
					"dd-MM-yyyy");
		Map<String, Object> returnMap=new HashMap<String, Object>();
		returnMap.put("employeeId", employeeId);
		
		try {
			List<GenericValue>	employmentInformationList = delegator.findByAnd("EmploymentAndPerson",UtilMisc.toMap("partyIdTo", partyId,"partyIdFrom",orgPartyId));
			logger.debug("employmentInformationList size---->"+employmentInformationList.size());
			GenericValue	employmentInfo=	EntityUtil.getFirst(employmentInformationList);
			if (!UtilValidate.isEmpty(employmentInfo)){
				String name="";
				if((String)employmentInfo.get("firstName")!=null){
					name=(String)employmentInfo.get("firstName");
				}
				if((String)employmentInfo.get("middleName")!=null){
					name=name+" "+(String)employmentInfo.get("middleName");
				}
				if((String)employmentInfo.get("lastName")!=null){
					name=name+" "+(String)employmentInfo.get("lastName");
				}				
				logger.debug("employmentInfo.get(lastName)---->"+employmentInfo.get("lastName"));
				returnMap.put("name", name);
				returnMap.put("joiningDate",sdf.format((Timestamp)employmentInfo.get("fromDate") ));
				returnMap.put("cardId", (String)employmentInfo.get("cardId"));
			}
		} catch (Exception e) {
			logger.debug("Exception in getting employmentInformationList---->"+e);
			return null;
		}
		try {
			List<GenericValue>	employeePositions = delegator.findByAnd("EmplPositionFulfillment", UtilMisc.toMap("partyId", partyId));		
			GenericValue employeePosition=EntityUtil.getFirst(employeePositions);
			if (!UtilValidate.isEmpty(employeePosition)){
				String employeePositionId=(String)employeePosition.get("emplPositionId");
				if(employeePositionId!=null && employeePositionId.length()>0){
					List<GenericValue>	employeePositionDetails = delegator.findByAnd("EmplPosition", UtilMisc.toMap("emplPositionId", employeePositionId));
					GenericValue employeePositionDetail=EntityUtil.getFirst(employeePositionDetails);
					String payGradeId=(String)employeePositionDetail.get("payGradeId");
					String emplPsitionTypeId=(String)employeePositionDetail.get("emplPositionTypeId");
					if(emplPsitionTypeId!=null && emplPsitionTypeId.length()>0){
					List<GenericValue>	employeePositionTypeDetails = delegator.findByAnd("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPsitionTypeId));
					GenericValue employeePositionTypeDetail=EntityUtil.getFirst(employeePositionTypeDetails);
					returnMap.put("designation", (String)employeePositionTypeDetail.get("description"));
					}
					if(payGradeId!=null && payGradeId.length()>0){
					GenericValue	payGradeName = delegator.findByPrimaryKey("PayGrade", UtilMisc.toMap("payGradeId", payGradeId));
					returnMap.put("payGradeName", (String)payGradeName.get("payGradeName"));
					}
				}
			}
		} catch (Exception e) {
			logger.debug("Exception in getting designation---->"+e);
			return null;
		}
		try{
			List<GenericValue>	partyGroups = delegator.findByAnd("PartyGroup",UtilMisc.toMap("partyId", orgPartyId));
			GenericValue	partyGroup=	EntityUtil.getFirst(partyGroups);
			returnMap.put("companyName", (String)partyGroup.get("groupName"));
			List<String> departmentNameList=FastList.newInstance();
			departmentNameList=AttendanceUtils.getPartyDepartment(delegator,partyId,departmentNameList);
			String deptName=AttendanceUtils.makeDepartmentName(departmentNameList);
			returnMap.put("deptName", deptName);
		}catch(Exception e){
			logger.debug("Exception in getting company---->"+e);
		}
		return returnMap;
	}
	
	/**
	 * Method added for edit Building,Floor,Gate
	 */

public static Map<String, Object> updateLocation(DispatchContext dctx,
			Map<String, ? extends Object> context) {
				Map<String, Object> result = ServiceUtil.returnSuccess();
				Delegator delegator = dctx.getDelegator();
				
				String locationId = (String) context.get("locationId");
				
				String locationName = (String) context.get("groupName");
				
				List<GenericValue> locations=null;
			
				try {
					locations = delegator.findByAnd("Location",UtilMisc.toMap("locationId", locationId));
					GenericValue location=	EntityUtil.getFirst(locations);
					if (!UtilValidate.isEmpty(location)){
					 location.set("locationName",locationName);
					  delegator.store(location);
					}
					
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
				result.put("locationId",locationId);	
				result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
				result.put(ModelService.SUCCESS_MESSAGE, "Location Info Update Successfully");	
				return result;
		}
/**
 * Method added for edit Device
 */

public static Map<String, Object> updateDevice(DispatchContext dctx,
		Map<String, ? extends Object> context) {
			Map<String, Object> result = ServiceUtil.returnSuccess();
			Delegator delegator = dctx.getDelegator();
			
			String locationId = (String) context.get("currentLocationId");
			
			String deviceName = (String) context.get("employeeName");
			
			List<GenericValue> locations=null;
		
			try {
				locations = delegator.findByAnd("Location",UtilMisc.toMap("locationId", locationId));
				GenericValue location=	EntityUtil.getFirst(locations);
				if (!UtilValidate.isEmpty(location)){
				 location.set("locationName",deviceName);
				  delegator.store(location);
				}
				
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			result.put("currentLocationId",locationId);	
			result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
			result.put(ModelService.SUCCESS_MESSAGE, "Device Name Update Successfully");	
			return result;
	}


/**
 * Method added for add employee log manually
 * @throws GenericEntityException 
 */
public static Map<String, Object> addEmployeeLog(DispatchContext dctx,Map<String, ? extends Object> context) throws GenericEntityException {
			Map<String, Object> result = ServiceUtil.returnSuccess();
		
			Delegator delegator = dctx.getDelegator();
		
			String logId = "";
			String userLoginId = (String) context.get("userLoginId");
			Timestamp logtimeStamp = (Timestamp) context.get("logDate");
			String inTime = (String) context.get("inTime");
			String outTime = (String) context.get("outTime");
			String terminalSN = (String) context.get("terminalSN");
			String senderTerminal = (String) context.get("senderTerminal");
			
			String partyId=AttendanceUtils.getPartyIDFromUserLoginID(delegator, userLoginId);
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(logtimeStamp);
			
			inTime = inTime.toLowerCase();
			Scanner in = new Scanner(inTime).useDelimiter("[^0-9]+");
			int hour = in.nextInt();
			int minute = in.nextInt();
			String intValue = inTime.replaceAll("[^a-z]", "");
			
			if(intValue.equals("pm")){
				if(hour<12){
					hour+=12;
				}
			}
			if(intValue.equals("am") && hour==12){
				hour=0;
			}
			cal.add(Calendar.HOUR_OF_DAY, hour);
			cal.add(Calendar.MINUTE, minute);
			
			java.sql.Timestamp logtimeStampIn = new java.sql.Timestamp(cal.getTimeInMillis());
			
			outTime = outTime.toLowerCase();
			in = new Scanner(outTime).useDelimiter("[^0-9]+");
			hour = in.nextInt();
			minute = in.nextInt();
			intValue = outTime.replaceAll("[^a-z]", "");
			
			if(intValue.equals("pm")){
				if(hour<12){
					hour+=12;
				}
			}
			if(intValue.equals("am") && hour==12){
				hour=0;
			}
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
			
			java.sql.Timestamp logtimeStampOut = new java.sql.Timestamp(cal.getTimeInMillis());
			
			java.sql.Timestamp queryStartDate = logtimeStamp, queryEndDate=null;
			
			cal.setTime(logtimeStamp);
			cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)+1);
			queryEndDate = new java.sql.Timestamp(cal.getTimeInMillis());
			
			List<EntityExpr> exprs = FastList.newInstance();
			List<GenericValue> employeeLogs = FastList.newInstance();
			
			exprs.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS,userLoginId));
			exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO,queryStartDate));
			exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.GREATER_THAN_EQUAL_TO,queryStartDate));
			exprs.add(EntityCondition.makeCondition("logtimeStamp", EntityOperator.LESS_THAN,queryEndDate));
			
			employeeLogs = delegator.findList("EmployeeLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, true);
			for(GenericValue emplLog:employeeLogs){
				delegator.removeValue(emplLog);
				
			}
			
				
		try {
			
			GenericValue employeeLogIn = delegator.makeValue("EmployeeLog");
			logId = delegator.getNextSeqId("EmployeeLog");
			employeeLogIn.set("logId", logId);
			employeeLogIn.set("userLoginId", userLoginId);
			employeeLogIn.set("partyId", partyId);
			employeeLogIn.set("logtimeStamp",logtimeStampIn);
			employeeLogIn.set("terminalSN", terminalSN);
			employeeLogIn.set("senderTerminal", senderTerminal);
			delegator.create(employeeLogIn);
			
			logId = delegator.getNextSeqId("EmployeeLog");
			employeeLogIn.set("logId", logId);
			employeeLogIn.set("logtimeStamp",logtimeStampOut);
			delegator.create(employeeLogIn);
			
			
			
		}catch (GenericEntityException e) {
				e.printStackTrace();
			}
		
			result.put("logId",logId);	
			result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_SUCCESS);
			result.put(ModelService.SUCCESS_MESSAGE, "Employee Log Added Successfully");	
			return result;

			
	}
}