/**
 * ACTAtekAgentPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtekAgent_service;

public interface ACTAtekAgentPortType extends java.rmi.Remote {

    /**
     * Log coming from ACTAtek
     */
    public java.lang.String log(java.lang.String magic, com.hectrix.www.ACTAtekAgent_xsd.Log unencryptedLog) throws java.rmi.RemoteException;

    /**
     * Encrypted Log coming from ACTAtek
     */
    public java.lang.String encryptLog(byte[] encryptedLog, com.hectrix.www.ACTAtekAgent_xsd.PhotoPart photo) throws java.rmi.RemoteException;

    /**
     * Multiple Encrypted Log coming from ACTAtek
     */
    public java.lang.String encryptLogMultiple(com.hectrix.www.ACTAtekAgent_xsd.ELog[] encryptedLogs) throws java.rmi.RemoteException;

    /**
     * Sync Request from ACTAtek
     */
    public java.lang.String sync(java.lang.String magic, java.lang.String registrationID) throws java.rmi.RemoteException;
}
