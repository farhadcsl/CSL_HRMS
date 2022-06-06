package com.hectrix.www.ACTAtek_service;

public class ACTAtekPortTypeProxy implements com.hectrix.www.ACTAtek_service.ACTAtekPortType {
  private String _endpoint = null;
  private com.hectrix.www.ACTAtek_service.ACTAtekPortType aCTAtekPortType = null;
  
  public ACTAtekPortTypeProxy() {
    _initACTAtekPortTypeProxy();
  }
  
  public ACTAtekPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initACTAtekPortTypeProxy();
  }
  
  private void _initACTAtekPortTypeProxy() {
    try {
      aCTAtekPortType = (new com.hectrix.www.ACTAtek_service.ACTAtekLocator()).getACTAtek();
      if (aCTAtekPortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)aCTAtekPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)aCTAtekPortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (aCTAtekPortType != null)
      ((javax.xml.rpc.Stub)aCTAtekPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.hectrix.www.ACTAtek_service.ACTAtekPortType getACTAtekPortType() {
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType;
  }
  
  public long login(java.lang.String username, java.lang.String password) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.login(username, password);
  }
  
  public java.lang.String logout(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.logout(sessionID);
  }
  
  public com.hectrix.www.ACTAtek_xsd.Agent[] getRegisteredAgents(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getRegisteredAgents(sessionID);
  }
  
  public com.hectrix.www.ACTAtek_xsd.Agent pollLog(long sessionID, com.hectrix.www.ACTAtek_xsd.Agent agent) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.pollLog(sessionID, agent);
  }
  
  public com.hectrix.www.ACTAtek_xsd.Log[] getLogs(long sessionID, com.hectrix.www.ACTAtek_xsd.GetLogsCriteria getLogsCriteria) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getLogs(sessionID, getLogsCriteria);
  }
  
  public java.lang.String addLog(long sessionID, java.util.Calendar timestamp, java.lang.String employeeID, com.hectrix.www.ACTAtek_xsd.EventType event, java.lang.String terminalSN) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.addLog(sessionID, timestamp, employeeID, event, terminalSN);
  }
  
  public java.lang.String addUser(long sessionID, com.hectrix.www.ACTAtek_xsd.User user) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.addUser(sessionID, user);
  }
  
  public com.hectrix.www.ACTAtek_xsd.User[] getUsers(long sessionID, com.hectrix.www.ACTAtek_xsd.GetUsersCriteria getUsersCriteria) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getUsers(sessionID, getUsersCriteria);
  }
  
  public java.lang.String updateUser(long sessionID, java.lang.String userID, com.hectrix.www.ACTAtek_xsd.User user) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.updateUser(sessionID, userID, user);
  }
  
  public java.lang.String deleteUser(long sessionID, java.lang.String userID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.deleteUser(sessionID, userID);
  }
  
  public com.hectrix.www.ACTAtek_xsd.User getMyself(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getMyself(sessionID);
  }
  
  public java.lang.String updateMyself(long sessionID, com.hectrix.www.ACTAtek_xsd.User user) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.updateMyself(sessionID, user);
  }
  
  public java.lang.String activateUser(long sessionID, java.lang.String userID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.activateUser(sessionID, userID);
  }
  
  public java.lang.String deactivateUser(long sessionID, java.lang.String userID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.deactivateUser(sessionID, userID);
  }
  
  public java.lang.String getUserMessage(long sessionID, java.lang.String userID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getUserMessage(sessionID, userID);
  }
  
  public java.lang.String updateUserMessage(long sessionID, java.lang.String userID, java.lang.String message) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.updateUserMessage(sessionID, userID, message);
  }
  
  public java.lang.String deleteUserMessage(long sessionID, java.lang.String userID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.deleteUserMessage(sessionID, userID);
  }
  
  public int addDepartment(long sessionID, com.hectrix.www.ACTAtek_xsd.Department department) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.addDepartment(sessionID, department);
  }
  
  public java.lang.String updateDepartment(long sessionID, int departmentID, com.hectrix.www.ACTAtek_xsd.Department department) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.updateDepartment(sessionID, departmentID, department);
  }
  
  public com.hectrix.www.ACTAtek_xsd.Department[] getDepartments(long sessionID, java.lang.Integer departmentID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getDepartments(sessionID, departmentID);
  }
  
  public java.lang.String deleteDepartment(long sessionID, int departmentID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.deleteDepartment(sessionID, departmentID);
  }
  
  public int addGroup(long sessionID, com.hectrix.www.ACTAtek_xsd.Group group) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.addGroup(sessionID, group);
  }
  
  public com.hectrix.www.ACTAtek_xsd.Group[] getGroups(long sessionID, com.hectrix.www.ACTAtek_xsd.GetGroupsCriteria getGroupsCriteria) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getGroups(sessionID, getGroupsCriteria);
  }
  
  public java.lang.String updateGroup(long sessionID, int groupID, com.hectrix.www.ACTAtek_xsd.Group group) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.updateGroup(sessionID, groupID, group);
  }
  
  public java.lang.String deleteGroup(long sessionID, int groupID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.deleteGroup(sessionID, groupID);
  }
  
  public com.hectrix.www.ACTAtek_xsd.Trigger[] getTriggers(long sessionID, com.hectrix.www.ACTAtek_xsd.GetTriggersCriteria getTriggersCriteria) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getTriggers(sessionID, getTriggersCriteria);
  }
  
  public java.lang.String updateTrigger(long sessionID, com.hectrix.www.ACTAtek_xsd.Trigger trigger) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.updateTrigger(sessionID, trigger);
  }
  
  public java.lang.String clearTrigger(long sessionID, com.hectrix.www.ACTAtek_xsd.TriggerType triggerType) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.clearTrigger(sessionID, triggerType);
  }
  
  public java.lang.String openDoor(long sessionID, int doorID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.openDoor(sessionID, doorID);
  }
  
  public java.lang.String openRelay(long sessionID, com.hectrix.www.ACTAtek_xsd.RelayID relayID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.openRelay(sessionID, relayID);
  }
  
  public java.lang.String closeRelay(long sessionID, com.hectrix.www.ACTAtek_xsd.RelayID relayID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.closeRelay(sessionID, relayID);
  }
  
  public java.lang.String registerAgent(long sessionID, java.lang.String ip, int port, java.lang.String agentVersion, java.lang.String magic, java.lang.String registrationID, boolean initSync) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.registerAgent(sessionID, ip, port, agentVersion, magic, registrationID, initSync);
  }
  
  public java.lang.String unregisterAgent(long sessionID, java.lang.String ip, int port) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.unregisterAgent(sessionID, ip, port);
  }
  
  public java.lang.String getAPIVersion() throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getAPIVersion();
  }
  
  public com.hectrix.www.ACTAtek_xsd.TerminalStatus getTerminalStatus(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getTerminalStatus(sessionID);
  }
  
  public com.hectrix.www.ACTAtek_xsd.Terminal[] getTerminalList(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getTerminalList(sessionID);
  }
  
  public java.lang.String nop(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.nop(sessionID);
  }
  
  public byte[] getLogPhoto(long sessionID, java.lang.String terminalSN, java.util.Calendar timestamp) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getLogPhoto(sessionID, terminalSN, timestamp);
  }
  
  public void registerFPUser(long sessionID, java.lang.String userID, com.hectrix.www.ACTAtek_xsd.holders.FingerprintHolder fingerprint, javax.xml.rpc.holders.StringHolder status) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    aCTAtekPortType.registerFPUser(sessionID, userID, fingerprint, status);
  }
  
  public com.hectrix.www.ACTAtek_xsd.LogDetail[] getFullLogs(long sessionID, com.hectrix.www.ACTAtek_xsd.GetLogsCriteria getLogsCriteria, java.lang.Integer limit) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getFullLogs(sessionID, getLogsCriteria, limit);
  }
  
  public java.lang.String resetLog(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.resetLog(sessionID);
  }
  
  public com.hectrix.www.ACTAtek_xsd.FingerprintSecurityLevel getFpSecurityLevel(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getFpSecurityLevel(sessionID);
  }
  
  public java.lang.String setFpSecurityLevel(long sessionID, com.hectrix.www.ACTAtek_xsd.FingerprintSecurityLevel fpSecurityLevel) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.setFpSecurityLevel(sessionID, fpSecurityLevel);
  }
  
  public int getRelayDelay(long sessionID, com.hectrix.www.ACTAtek_xsd.RelayID relayID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getRelayDelay(sessionID, relayID);
  }
  
  public java.lang.String setRelayDelay(long sessionID, com.hectrix.www.ACTAtek_xsd.RelayID relayID, int sec) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.setRelayDelay(sessionID, relayID, sec);
  }
  
  public com.hectrix.www.ACTAtek_xsd.AutoInOutOption getAutoInOut(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getAutoInOut(sessionID);
  }
  
  public java.lang.String setAutoInOut(long sessionID, com.hectrix.www.ACTAtek_xsd.AutoInOutOption autoInOutOption) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.setAutoInOut(sessionID, autoInOutOption);
  }
  
  public boolean getLogUnauthorizedEvent(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getLogUnauthorizedEvent(sessionID);
  }
  
  public java.lang.String setLogUnauthorizedEvent(long sessionID, boolean enable) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.setLogUnauthorizedEvent(sessionID, enable);
  }
  
  public java.lang.String setRelayOption(long sessionID, com.hectrix.www.ACTAtek_xsd.RelayID relayID, com.hectrix.www.ACTAtek_xsd.RelayOption relayOpt) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.setRelayOption(sessionID, relayID, relayOpt);
  }
  
  public com.hectrix.www.ACTAtek_xsd.RelayOption getRelayOption(long sessionID, com.hectrix.www.ACTAtek_xsd.RelayID relayID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getRelayOption(sessionID, relayID);
  }
  
  public java.lang.String setSNTP(long sessionID, boolean enable) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.setSNTP(sessionID, enable);
  }
  
  public boolean getSNTP(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getSNTP(sessionID);
  }
  
  public java.lang.String setSNTPServer(long sessionID, java.lang.String serveraddr) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.setSNTPServer(sessionID, serveraddr);
  }
  
  public java.lang.String getSNTPServer(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getSNTPServer(sessionID);
  }
  
  public java.util.Calendar getTerminalDateTime(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getTerminalDateTime(sessionID);
  }
  
  public java.lang.String setTerminalDateTime(long sessionID, java.util.Calendar terminalDate) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.setTerminalDateTime(sessionID, terminalDate);
  }
  
  public java.lang.String getTimeZone(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getTimeZone(sessionID);
  }
  
  public java.lang.String setTimeZone(long sessionID, java.lang.String timezone) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.setTimeZone(sessionID, timezone);
  }
  
  public byte[] getCaptureFingerprint(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getCaptureFingerprint(sessionID);
  }
  
  public void initCaptureFP(long sessionID, javax.xml.rpc.holders.ByteArrayHolder jpegPhoto, javax.xml.rpc.holders.StringHolder status) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    aCTAtekPortType.initCaptureFP(sessionID, jpegPhoto, status);
  }
  
  public java.lang.String lock(long sessionID, java.lang.String deviceNodeDbId) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.lock(sessionID, deviceNodeDbId);
  }
  
  public java.lang.String unlock(long sessionID, java.lang.String deviceNodeDbId) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.unlock(sessionID, deviceNodeDbId);
  }
  
  public byte[] systemBackup(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.systemBackup(sessionID);
  }
  
  public java.lang.String systemRestore(long sessionID, byte[] systemData) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.systemRestore(sessionID, systemData);
  }
  
  public java.lang.String getUsersDb(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getUsersDb(sessionID);
  }
  
  public java.lang.String deleteLogs(long sessionID, int daysToKeep) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.deleteLogs(sessionID, daysToKeep);
  }
  
  public java.lang.String remoteBackupStart(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.remoteBackupStart(sessionID);
  }
  
  public byte[] getChunk(long sessionID, java.lang.String filename, int chunkoffset, int chunksize) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getChunk(sessionID, filename, chunkoffset, chunksize);
  }
  
  public java.lang.String remoteBackupEnd(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.remoteBackupEnd(sessionID);
  }
  
  public com.hectrix.www.ACTAtek_xsd.TerminalStatus2 remoteRestoreCheck(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.remoteRestoreCheck(sessionID);
  }
  
  public java.lang.String remoteRestoreStart(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.remoteRestoreStart(sessionID);
  }
  
  public java.lang.String sendChunk(long sessionID, java.lang.String filename, int chunkoffset, byte[] systemData) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.sendChunk(sessionID, filename, chunkoffset, systemData);
  }
  
  public java.lang.String remoteRestoreEnd(long sessionID, long dbsize, long othersize) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.remoteRestoreEnd(sessionID, dbsize, othersize);
  }
  
  public java.lang.String cancelRestore(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.cancelRestore(sessionID);
  }
  
  public com.hectrix.www.ACTAtek_xsd.UserID[] addUsers(long sessionID, com.hectrix.www.ACTAtek_xsd.User[] users) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.addUsers(sessionID, users);
  }
  
  public java.lang.String updateUsers(long sessionID, com.hectrix.www.ACTAtek_xsd.UserID[] uids, com.hectrix.www.ACTAtek_xsd.User[] users) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.updateUsers(sessionID, uids, users);
  }
  
  public com.hectrix.www.ACTAtek_xsd.JobCodeSettings[] getJobCodeSettings(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getJobCodeSettings(sessionID);
  }
  
  public java.lang.String updateJobCodeSettings(long sessionID, com.hectrix.www.ACTAtek_xsd.JobCodeSettings[] jobcodesettings) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.updateJobCodeSettings(sessionID, jobcodesettings);
  }
  
  public java.lang.String addJobCodes(long sessionID, com.hectrix.www.ACTAtek_xsd.JobCode[] jobcodes) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.addJobCodes(sessionID, jobcodes);
  }
  
  public java.lang.String updateJobCodes(long sessionID, com.hectrix.www.ACTAtek_xsd.JobCode[] jobcodes) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.updateJobCodes(sessionID, jobcodes);
  }
  
  public java.lang.String deleteJobCodes(long sessionID, com.hectrix.www.ACTAtek_xsd.JobCode[] jobcodes) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.deleteJobCodes(sessionID, jobcodes);
  }
  
  public com.hectrix.www.ACTAtek_xsd.JobCode[] getJobCodes(long sessionID, com.hectrix.www.ACTAtek_xsd.GetJobCodeCriteria getJobCodeCriteria) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getJobCodes(sessionID, getJobCodeCriteria);
  }
  
  public int getUserCount(long sessionID) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getUserCount(sessionID);
  }
  
  public com.hectrix.www.ACTAtek_xsd.User[] getUserBatch(long sessionID, int offset, int limit) throws java.rmi.RemoteException{
    if (aCTAtekPortType == null)
      _initACTAtekPortTypeProxy();
    return aCTAtekPortType.getUserBatch(sessionID, offset, limit);
  }
  
  
}