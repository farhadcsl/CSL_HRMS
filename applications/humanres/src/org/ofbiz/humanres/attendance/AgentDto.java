/**
 * 
 */
package org.ofbiz.humanres.attendance;

/**
 * 
 * 
 */
public class AgentDto {
	private String deviceURL;
	private String deviceUser;
	private String devicePass;
	private String endPointIP;
	private String agentVersion;
	public AgentDto() {
	}

	public AgentDto(String deviceURL, String deviceUser, String devicePass,String endPointIP,String agentVersion) {
		super();
		this.setDeviceURL(deviceURL);
		this.setDeviceUser(deviceUser);
		this.setDevicePass(devicePass);
		this.setEndPointIP(endPointIP);
		this.setAgentVersion(agentVersion);
	}


	@Override
	public String toString() {
		return "AgentUtils [deviceURL=" + deviceURL + ", deviceUser="
				+ deviceUser + ", devicePass=" + devicePass + ", endPointIP="
				+ endPointIP + ", agentVersion=" + agentVersion + "]";
	}

	public String getDeviceURL() {
		return deviceURL;
	}

	public void setDeviceURL(String deviceURL) {
		this.deviceURL = deviceURL;
	}

	public String getDeviceUser() {
		return deviceUser;
	}

	public void setDeviceUser(String deviceUser) {
		this.deviceUser = deviceUser;
	}

	public String getDevicePass() {
		return devicePass;
	}

	public void setDevicePass(String devicePass) {
		this.devicePass = devicePass;
	}

	public String getEndPointIP() {
		return endPointIP;
	}

	public void setEndPointIP(String endPointIP) {
		this.endPointIP = endPointIP;
	}

	public String getAgentVersion() {
		return agentVersion;
	}

	public void setAgentVersion(String agentVersion) {
		this.agentVersion = agentVersion;
	}

}
