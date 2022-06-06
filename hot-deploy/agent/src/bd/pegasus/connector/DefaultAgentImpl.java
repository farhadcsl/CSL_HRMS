package bd.pegasus.connector;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.apache.log4j.Logger;
/**
 * 
 * 
 *
 */
public class DefaultAgentImpl implements AbstractAgent{
	static Socket socket = null;
	private static Logger logger=Logger.getLogger("DefaultAgentImpl");

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	String connectionName = "";

/*	public DefaultAgentImpl(String connectionName, Socket socketParam) {
		socket = socketParam;
		this.connectionName = connectionName;
	}*/
	public DefaultAgentImpl(String connectionName, Socket socketParam) {
		socket = socketParam;
		this.connectionName = connectionName;
	}
	@Override
	public Socket connect(String server, int port) {
		// int ERROR = 1;
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(server, port));
			logger.debug("Connected with server "
					+ socket.getInetAddress() + ":" + socket.getPort());
			System.out.println("Connected with server "
					+ socket.getInetAddress() + ":" + socket.getPort());
		} catch (UnknownHostException e) {
			System.out.println(e);

		} catch (IOException e) {
			System.out.println(e);
		}
		return socket;
	}

	public String close() {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory
				.getDelegator("default");
		int numberOfRegisterDevice=0;
			String serverAddress = socket
					.getRemoteSocketAddress().toString()
					.split(":")[0].replace("/", "");
			try{
				List<GenericValue> deviceListFromDb = delegator.findByAnd(
						"DeviceRegistration", UtilMisc.toMap("terminalIP", serverAddress));
				for(GenericValue device:deviceListFromDb){
					List<GenericValue> registeredDevices = delegator.findByAnd(
							"Location", UtilMisc.toMap("locationId", (String)device.get("locationId")));
					GenericValue registerDevice=EntityUtil.getFirst(registeredDevices);
					if(((String)registerDevice.get("statusId")).equals("DEVICE_REGISTERED")){
						numberOfRegisterDevice++;
					}
					
				}
					if(numberOfRegisterDevice<=1){
						socket.close();
					}				
				}catch(Exception ex){
					System.out.println("Exception to find out terminalsn");
				}			
		return "success";
	}
	
	public Socket reConnect(String serverAddress,String portAddress) {
		int ERROR = 1;	
		System.out.println("Port Address--->"+portAddress);
		try {
			Socket socket1 = new Socket();
			socket1.connect(new InetSocketAddress(serverAddress, Integer.parseInt(portAddress)));
		    System.out.println("ReConnected with server " +socket1.getInetAddress() +":" + socket1.getPort());
		    return socket1;
		}
		catch (UnknownHostException e) {
		    System.out.println(e);
		    return null;
		}catch (IOException e) {
		    System.out.println(e);
		    return null;
		}
	}

	public int listenPort() {
		logger.debug("Listener is started");
		int retVal = 1;
		BufferedReader input=null;
		DataOutputStream output=null;
		byte[] sendData = new byte[50];
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory
				.getDelegator("default");
		while (true) {

			try {
				if(!socket.isClosed()){
					Map<String, String> deviceList = new HashMap<String, String>();
					ArrayList<String> dataToBeSent=new ArrayList<String>();
					String serverAddress = socket
							.getRemoteSocketAddress().toString()
							.split(":")[0].replace("/", "");
					String employeeId=null;
					//GenericValue device=null;
					String data = "05444F3030";

					try{
					List<GenericValue> deviceListFromDb = delegator.findByAnd(
							"DeviceRegistration", UtilMisc.toMap("terminalIP", serverAddress));
					for(GenericValue device:deviceListFromDb){
						List<GenericValue> registeredDevices = delegator.findByAnd(
								"Location", UtilMisc.toMap("locationId", (String)device.get("locationId")));
						GenericValue registerDevice=EntityUtil.getFirst(registeredDevices);
						if(((String)registerDevice.get("statusId")).equals("DEVICE_REGISTERED")){
						deviceList.put((String)device.get("pegasusAddress"), (String)device.get("terminalSN"));
						dataToBeSent.add("0"+Long.toHexString(Long.parseLong(data,16)+Long.parseLong((String)device.get("pegasusAddress"))));
						
						}
						
					}
					}catch(Exception ex){
						System.out.println("Exception to find out terminalsn");
					}	
				for(String command:dataToBeSent){
				try{	
				output = new DataOutputStream(socket.getOutputStream());
				sendData = AttendanceConnectorUtil.hexStringToByteArray(command);
				output.write(sendData);
				output.flush();
				}catch(Exception e){
					logger.debug("Exception to write---->"+e);
				}
				long startTime=System.currentTimeMillis();
				long finishTime=0L;
				while(true){
				input = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				try {
					String message = null;	
					
					if (input.ready()) {
						message = input.readLine();
						//String hexValue = AttendanceConnectorUtil.bytesToHex(message.getBytes("ISO-8859-1"));						
						if (message.length() == 46) {
							//logger.debug("Message is--->"+message);
							//System.out.println("Message is--->"+message);
							int result = 0;
							String pegasusAddress=message.substring(1,3);
							String cardNumber = message.substring(13, 21);
							String dateTime = "20" + message.substring(25, 27)
									+ "-" + message.substring(27, 29) + "-"
									+ message.substring(29, 31) + " "
									+ message.substring(33, 35) + ":"
									+ message.substring(35, 37);

							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm");
							sdf.setTimeZone(TimeZone.getTimeZone("GMT+6"));
							Date date = new Date();
							try {
								date = sdf.parse(dateTime);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							employeeId = filterEmployee(cardNumber,deviceList.get(pegasusAddress),
									delegator);
							if (employeeId != null) {
								Map<String, String> paramMap = UtilMisc.toMap(
										"logID", String.valueOf(System
												.currentTimeMillis()), "logID",
										employeeId, "userID", String
												.valueOf(Integer
														.parseInt(cardNumber)),
										"timestamp", String.valueOf(date
												.getTime()), "trigger", "In",
										"terminalSN", deviceList.get(pegasusAddress), "sender",
										serverAddress);								
								result = saveEmployeeLog(delegator, paramMap);
								retVal = result;
							}
						}
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						break;
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}		
					try{
					output = new DataOutputStream(socket.getOutputStream());
					sendData = new byte[50];
					sendData = AttendanceConnectorUtil.hexStringToByteArray(command);
					output.write(sendData);
					output.flush();
					}catch(Exception e){
						logger.debug("Exception for writing------>"+e);
					}
					finishTime=System.currentTimeMillis();
					System.out.println("Sent Command------->"+command);
					if(finishTime-startTime>10000L){					
/*						String serverAddress = socket
								.getRemoteSocketAddress().toString()
								.split(":")[0].replace("/", "");*/
						String portAddress=socket
								.getRemoteSocketAddress().toString()
								.split(":")[1];	
					socket.close();	
						while(true){		
						socket=reConnect(serverAddress,portAddress);
						if(socket!=null){
							startTime=System.currentTimeMillis();
							if(output!=null)
							{
								output.flush();
								output.close();
							}
							if(input!=null)
							{
								input.close();
							}	
							break;
						}
						}
					}
				} catch (InterruptedIOException e) {
					logger.debug("Exception in DefaultAgentImpl(listenPort(InterruptedIOException))------->"+e);
				}
				}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				
				//}
				}
				}else{
					System.out.println("=================Socket is closed From Listener part==============");
						if(output!=null)
						{
							output.flush();
							output.close();
						}
						if(input!=null)
						{
							input.close();
						}	
					return retVal;
				}
				
			} catch (IOException e) {
				logger.debug("Exception in DefaultAgentImpl(listenPort(IOException))------->"+e);
				try {
					socket.close();
				} catch (IOException ex) {
					System.out.println(ex);
				}
				retVal = 0;
				return retVal;
			}
		}
	}

	// 1 means save successfully,0 means save unsuccessful
	@Override
	public int saveEmployeeLog(Delegator delegator,
			Map<String, ? extends Object> context) {
		int retVal = 1;
		String partyId = null;
		String userID = (String) context.get("logID");
		String timestamp = (String) context.get("timestamp");
		String trigger = (String) context.get("trigger");
		String terminalSN = (String) context.get("terminalSN");
		String sender = (String) context.get("sender");

		try {
			GenericValue newValue = delegator.makeValue("EmployeeLog");
			partyId = delegator.getNextSeqId("EmployeeLog");
			newValue.set("logId", partyId.trim());
			newValue.set("userLoginId", userID);
			newValue.set("logtimeStamp",
					new java.sql.Timestamp(Long.valueOf(timestamp)));
			newValue.set("eventType", trigger);
			newValue.set("terminalSN", terminalSN);
			newValue.set("senderTerminal", sender);
			delegator.create(newValue);
		} catch (GenericEntityException e) {
			System.out.println("Exception in saveEmployeeLog--->" + e);
			retVal = 0;
			e.printStackTrace();
		}
		return retVal;
	}

	// If Employee exist then send the employee id otherwise send null value
	@Override
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

	@Override
	public void run() {
		listenPort();
	}

}