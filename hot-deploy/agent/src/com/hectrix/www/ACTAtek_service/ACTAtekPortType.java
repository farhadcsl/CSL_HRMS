/**
 * ACTAtekPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_service;

public interface ACTAtekPortType extends java.rmi.Remote {

    /**
     * Return sessionID for subsequent calls
     */
    public long login(java.lang.String username, java.lang.String password) throws java.rmi.RemoteException;

    /**
     * Invalidate the given sessionID
     */
    public java.lang.String logout(long sessionID) throws java.rmi.RemoteException;

    /**
     * Get Agent List
     */
    public com.hectrix.www.ACTAtek_xsd.Agent[] getRegisteredAgents(long sessionID) throws java.rmi.RemoteException;

    /**
     * Actively Send Log to Agent, return agent status when done
     */
    public com.hectrix.www.ACTAtek_xsd.Agent pollLog(long sessionID, com.hectrix.www.ACTAtek_xsd.Agent agent) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getLogs
     */
    public com.hectrix.www.ACTAtek_xsd.Log[] getLogs(long sessionID, com.hectrix.www.ACTAtek_xsd.GetLogsCriteria getLogsCriteria) throws java.rmi.RemoteException;

    /**
     * Add a Log Entry
     */
    public java.lang.String addLog(long sessionID, java.util.Calendar timestamp, java.lang.String employeeID, com.hectrix.www.ACTAtek_xsd.EventType event, java.lang.String terminalSN) throws java.rmi.RemoteException;

    /**
     * Add a User
     */
    public java.lang.String addUser(long sessionID, com.hectrix.www.ACTAtek_xsd.User user) throws java.rmi.RemoteException;

    /**
     * Find Users
     */
    public com.hectrix.www.ACTAtek_xsd.User[] getUsers(long sessionID, com.hectrix.www.ACTAtek_xsd.GetUsersCriteria getUsersCriteria) throws java.rmi.RemoteException;

    /**
     * Change User Information
     */
    public java.lang.String updateUser(long sessionID, java.lang.String userID, com.hectrix.www.ACTAtek_xsd.User user) throws java.rmi.RemoteException;

    /**
     * Delete a given User
     */
    public java.lang.String deleteUser(long sessionID, java.lang.String userID) throws java.rmi.RemoteException;

    /**
     * Get current login User
     */
    public com.hectrix.www.ACTAtek_xsd.User getMyself(long sessionID) throws java.rmi.RemoteException;

    /**
     * Update own information
     */
    public java.lang.String updateMyself(long sessionID, com.hectrix.www.ACTAtek_xsd.User user) throws java.rmi.RemoteException;

    /**
     * Activate a User
     */
    public java.lang.String activateUser(long sessionID, java.lang.String userID) throws java.rmi.RemoteException;

    /**
     * Deactivate a User
     */
    public java.lang.String deactivateUser(long sessionID, java.lang.String userID) throws java.rmi.RemoteException;

    /**
     * Get User Message
     */
    public java.lang.String getUserMessage(long sessionID, java.lang.String userID) throws java.rmi.RemoteException;

    /**
     * Change User Message
     */
    public java.lang.String updateUserMessage(long sessionID, java.lang.String userID, java.lang.String message) throws java.rmi.RemoteException;

    /**
     * Delete User Message
     */
    public java.lang.String deleteUserMessage(long sessionID, java.lang.String userID) throws java.rmi.RemoteException;

    /**
     * Add a Department
     */
    public int addDepartment(long sessionID, com.hectrix.www.ACTAtek_xsd.Department department) throws java.rmi.RemoteException;

    /**
     * Change Department Information
     */
    public java.lang.String updateDepartment(long sessionID, int departmentID, com.hectrix.www.ACTAtek_xsd.Department department) throws java.rmi.RemoteException;

    /**
     * Get Departments by ID
     */
    public com.hectrix.www.ACTAtek_xsd.Department[] getDepartments(long sessionID, java.lang.Integer departmentID) throws java.rmi.RemoteException;

    /**
     * Delete Department
     */
    public java.lang.String deleteDepartment(long sessionID, int departmentID) throws java.rmi.RemoteException;

    /**
     * Add an Access Group
     */
    public int addGroup(long sessionID, com.hectrix.www.ACTAtek_xsd.Group group) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getGroups
     */
    public com.hectrix.www.ACTAtek_xsd.Group[] getGroups(long sessionID, com.hectrix.www.ACTAtek_xsd.GetGroupsCriteria getGroupsCriteria) throws java.rmi.RemoteException;

    /**
     * Change Group Information
     */
    public java.lang.String updateGroup(long sessionID, int groupID, com.hectrix.www.ACTAtek_xsd.Group group) throws java.rmi.RemoteException;

    /**
     * Delete a given Group
     */
    public java.lang.String deleteGroup(long sessionID, int groupID) throws java.rmi.RemoteException;

    /**
     * Get Trigger by Type
     */
    public com.hectrix.www.ACTAtek_xsd.Trigger[] getTriggers(long sessionID, com.hectrix.www.ACTAtek_xsd.GetTriggersCriteria getTriggersCriteria) throws java.rmi.RemoteException;

    /**
     * Change Trigger Details
     */
    public java.lang.String updateTrigger(long sessionID, com.hectrix.www.ACTAtek_xsd.Trigger trigger) throws java.rmi.RemoteException;

    /**
     * Clear All Trigger Time
     */
    public java.lang.String clearTrigger(long sessionID, com.hectrix.www.ACTAtek_xsd.TriggerType triggerType) throws java.rmi.RemoteException;

    /**
     * Open the Door Now
     */
    public java.lang.String openDoor(long sessionID, int doorID) throws java.rmi.RemoteException;

    /**
     * Open the Relay
     */
    public java.lang.String openRelay(long sessionID, com.hectrix.www.ACTAtek_xsd.RelayID relayID) throws java.rmi.RemoteException;

    /**
     * Close the Relay
     */
    public java.lang.String closeRelay(long sessionID, com.hectrix.www.ACTAtek_xsd.RelayID relayID) throws java.rmi.RemoteException;

    /**
     * Register an Agent (IP/PORT)
     */
    public java.lang.String registerAgent(long sessionID, java.lang.String ip, int port, java.lang.String agentVersion, java.lang.String magic, java.lang.String registrationID, boolean initSync) throws java.rmi.RemoteException;

    /**
     * Delete Agent (IP/PORT)
     */
    public java.lang.String unregisterAgent(long sessionID, java.lang.String ip, int port) throws java.rmi.RemoteException;

    /**
     * Get API Version Number
     */
    public java.lang.String getAPIVersion() throws java.rmi.RemoteException;

    /**
     * Get Terminal Status
     */
    public com.hectrix.www.ACTAtek_xsd.TerminalStatus getTerminalStatus(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getTerminalList
     */
    public com.hectrix.www.ACTAtek_xsd.Terminal[] getTerminalList(long sessionID) throws java.rmi.RemoteException;

    /**
     * Check if the session still valid and update timestamp of sessionID
     */
    public java.lang.String nop(long sessionID) throws java.rmi.RemoteException;

    /**
     * Get Photo of the related log parameter
     */
    public byte[] getLogPhoto(long sessionID, java.lang.String terminalSN, java.util.Calendar timestamp) throws java.rmi.RemoteException;

    /**
     * Immediately Register a Fingerprint User using ACTAtek
     */
    public void registerFPUser(long sessionID, java.lang.String userID, com.hectrix.www.ACTAtek_xsd.holders.FingerprintHolder fingerprint, javax.xml.rpc.holders.StringHolder status) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getFullLogs
     */
    public com.hectrix.www.ACTAtek_xsd.LogDetail[] getFullLogs(long sessionID, com.hectrix.www.ACTAtek_xsd.GetLogsCriteria getLogsCriteria, java.lang.Integer limit) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__resetLog
     */
    public java.lang.String resetLog(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getFpSecurityLevel
     */
    public com.hectrix.www.ACTAtek_xsd.FingerprintSecurityLevel getFpSecurityLevel(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__setFpSecurityLevel
     */
    public java.lang.String setFpSecurityLevel(long sessionID, com.hectrix.www.ACTAtek_xsd.FingerprintSecurityLevel fpSecurityLevel) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getRelayDelay
     */
    public int getRelayDelay(long sessionID, com.hectrix.www.ACTAtek_xsd.RelayID relayID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__setRelayDelay
     */
    public java.lang.String setRelayDelay(long sessionID, com.hectrix.www.ACTAtek_xsd.RelayID relayID, int sec) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getAutoInOut
     */
    public com.hectrix.www.ACTAtek_xsd.AutoInOutOption getAutoInOut(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__setAutoInOut
     */
    public java.lang.String setAutoInOut(long sessionID, com.hectrix.www.ACTAtek_xsd.AutoInOutOption autoInOutOption) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getLogUnauthorizedEvent
     */
    public boolean getLogUnauthorizedEvent(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__setLogUnauthorizedEvent
     */
    public java.lang.String setLogUnauthorizedEvent(long sessionID, boolean enable) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__setRelayOption
     */
    public java.lang.String setRelayOption(long sessionID, com.hectrix.www.ACTAtek_xsd.RelayID relayID, com.hectrix.www.ACTAtek_xsd.RelayOption relayOpt) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getRelayOption
     */
    public com.hectrix.www.ACTAtek_xsd.RelayOption getRelayOption(long sessionID, com.hectrix.www.ACTAtek_xsd.RelayID relayID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__setSNTP
     */
    public java.lang.String setSNTP(long sessionID, boolean enable) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getSNTP
     */
    public boolean getSNTP(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__setSNTPServer
     */
    public java.lang.String setSNTPServer(long sessionID, java.lang.String serveraddr) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getSNTPServer
     */
    public java.lang.String getSNTPServer(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getTerminalDateTime
     */
    public java.util.Calendar getTerminalDateTime(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__setTerminalDateTime
     */
    public java.lang.String setTerminalDateTime(long sessionID, java.util.Calendar terminalDate) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getTimeZone
     */
    public java.lang.String getTimeZone(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__setTimeZone
     */
    public java.lang.String setTimeZone(long sessionID, java.lang.String timezone) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getCaptureFingerprint
     */
    public byte[] getCaptureFingerprint(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__initCaptureFP
     */
    public void initCaptureFP(long sessionID, javax.xml.rpc.holders.ByteArrayHolder jpegPhoto, javax.xml.rpc.holders.StringHolder status) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__lock
     */
    public java.lang.String lock(long sessionID, java.lang.String deviceNodeDbId) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__unlock
     */
    public java.lang.String unlock(long sessionID, java.lang.String deviceNodeDbId) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__systemBackup
     */
    public byte[] systemBackup(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__systemRestore
     */
    public java.lang.String systemRestore(long sessionID, byte[] systemData) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getUsersDb
     */
    public java.lang.String getUsersDb(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__deleteLogs
     */
    public java.lang.String deleteLogs(long sessionID, int daysToKeep) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__RemoteBackupStart
     */
    public java.lang.String remoteBackupStart(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__GetChunk
     */
    public byte[] getChunk(long sessionID, java.lang.String filename, int chunkoffset, int chunksize) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__RemoteBackupEnd
     */
    public java.lang.String remoteBackupEnd(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__RemoteRestoreCheck
     */
    public com.hectrix.www.ACTAtek_xsd.TerminalStatus2 remoteRestoreCheck(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__RemoteRestoreStart
     */
    public java.lang.String remoteRestoreStart(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__SendChunk
     */
    public java.lang.String sendChunk(long sessionID, java.lang.String filename, int chunkoffset, byte[] systemData) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__RemoteRestoreEnd
     */
    public java.lang.String remoteRestoreEnd(long sessionID, long dbsize, long othersize) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__CancelRestore
     */
    public java.lang.String cancelRestore(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__addUsers
     */
    public com.hectrix.www.ACTAtek_xsd.UserID[] addUsers(long sessionID, com.hectrix.www.ACTAtek_xsd.User[] users) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__updateUsers
     */
    public java.lang.String updateUsers(long sessionID, com.hectrix.www.ACTAtek_xsd.UserID[] uids, com.hectrix.www.ACTAtek_xsd.User[] users) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getJobCodeSettings
     */
    public com.hectrix.www.ACTAtek_xsd.JobCodeSettings[] getJobCodeSettings(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__updateJobCodeSettings
     */
    public java.lang.String updateJobCodeSettings(long sessionID, com.hectrix.www.ACTAtek_xsd.JobCodeSettings[] jobcodesettings) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__addJobCodes
     */
    public java.lang.String addJobCodes(long sessionID, com.hectrix.www.ACTAtek_xsd.JobCode[] jobcodes) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__updateJobCodes
     */
    public java.lang.String updateJobCodes(long sessionID, com.hectrix.www.ACTAtek_xsd.JobCode[] jobcodes) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__deleteJobCodes
     */
    public java.lang.String deleteJobCodes(long sessionID, com.hectrix.www.ACTAtek_xsd.JobCode[] jobcodes) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getJobCodes
     */
    public com.hectrix.www.ACTAtek_xsd.JobCode[] getJobCodes(long sessionID, com.hectrix.www.ACTAtek_xsd.GetJobCodeCriteria getJobCodeCriteria) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getUserCount
     */
    public int getUserCount(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getUserBatch
     */
    public com.hectrix.www.ACTAtek_xsd.User[] getUserBatch(long sessionID, int offset, int limit) throws java.rmi.RemoteException;
}
