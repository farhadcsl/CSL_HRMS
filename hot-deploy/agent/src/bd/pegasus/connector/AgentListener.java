package bd.pegasus.connector;

public class AgentListener {
	
	public AgentListener(Runnable agent) {
		Thread th = new Thread(agent);
		th.start();
	}

}
