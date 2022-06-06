package bd.pegasus.connector;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 
 *
 * 
 * 
 * 
 */
public class ConnectionFactoryImpl implements ConnectionFactory {
	// please state what this member is for
	private static Logger logger=Logger.getLogger("ConnectionFactoryImpl");
	private static Map<String, AbstractAgent> connectionMap = new HashMap<String, AbstractAgent>();
	private static int PEGASUS_PORT = 4001;

	// private static Socket[] socket=new Socket[100];

	static int i = 0;
	static AbstractAgent agent;

/*	public static int connect(String server) {
		
		 * for(i=0;i<100;i++){ if(socket[i]==null) break; }
		 

		int retValue = 0;
		int port = 4001;
		agent = new DefaultAgentImpl(new Socket());
		Socket newConnection = agent.connect(server, port);
		// returns 1 if socket connection is established otherwise 0
		if (null != newConnection) {
			connectionMap.put(server, newConnection);
			retValue = 1;
			listen(server);
		}
		return retValue;
	}

	public static String close(String server) {
		String retVal = null;
		pegasusAgentImpl = new DefaultAgentImpl(connectionMap.get(server));
		retVal = pegasusAgentImpl.close();
		connectionMap.remove(server);
		return retVal;
	}

	public static void listen(String server) {
		pegasusAgentImpl = new DefaultAgentImpl(connectionMap.get(server));
		pegasusAgentImpl.startListenPort(pegasusAgentImpl);
	}
*/
/*	public static void main(String[] agrgs) {
		connect("192.168.170.58");
		listen("192.168.170.58");
	}
*/

	@Override
	public AbstractAgent checkConenction(String terminalIP) {
		if(connectionMap.containsKey(terminalIP)){
			return connectionMap.get(terminalIP);
		}
		return null;
	}

	@Override
	public AbstractAgent getConnection(String terminalIP) {
			try {
				Socket socket=new Socket(terminalIP,PEGASUS_PORT);
				agent = new DefaultAgentImpl(terminalIP, socket);
			} catch (UnknownHostException e) {
				agent=null;
				logger.debug("UnknownHostException------------------------->"+e);
				return agent;
			} catch (IOException e) {
				agent=null;
				logger.debug("IOException------------------------->"+e);
				return agent;
			}
			connectionMap.put(terminalIP, agent);			
			return agent;

	}

}