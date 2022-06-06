package bd.pegasus.connector;

/**
 * 
 *
 *
 */
public interface ConnectionFactory {
	public AbstractAgent checkConenction(String terminalIP);
	public AbstractAgent getConnection(String terminalIP);
}
