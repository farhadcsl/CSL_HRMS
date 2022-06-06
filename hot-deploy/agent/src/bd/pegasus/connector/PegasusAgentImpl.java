package bd.pegasus.connector;

import java.net.Socket;

/**
 * 
 *
 *
 */
public class PegasusAgentImpl extends DefaultAgentImpl {

	public PegasusAgentImpl(Socket socketParam,String connectionName) {
		super(connectionName, socketParam);
	}

}
