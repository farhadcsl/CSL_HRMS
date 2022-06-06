package bd.pegasus.connector;

import java.net.Socket;
import java.util.Map;

import org.ofbiz.entity.Delegator;

//This interface is used to manage for all kind of device related information 
//gather and change(like change time setting,listen port)
public interface AbstractAgent extends Runnable	 {
	/**
	 * 
	 * @param server
	 * @param port
	 * @return
	 */
public Socket connect(String server,int port);
/**
 * 
 * @return
 */
public String close();

/**
 * 
 * @param delegator
 * @param context
 * @return
 */
public int saveEmployeeLog(Delegator delegator,Map<String, ? extends Object> context);
/**
 * 
 * @param rfId
 * @param delegator
 * @return
 */
public String filterEmployee(String rfId,String terminalSN,Delegator delegator);
}