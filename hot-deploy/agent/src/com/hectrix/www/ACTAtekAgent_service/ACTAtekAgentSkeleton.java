/**
 * ACTAtekAgentSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtekAgent_service;

public class ACTAtekAgentSkeleton implements com.hectrix.www.ACTAtekAgent_service.ACTAtekAgentPortType, org.apache.axis.wsdl.Skeleton {
    private com.hectrix.www.ACTAtekAgent_service.ACTAtekAgentPortType impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "magic"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "unencryptedLog"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtekAgent.xsd", "Log"), com.hectrix.www.ACTAtekAgent_xsd.Log.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("log", _params, new javax.xml.namespace.QName("", "status"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtekAgent.xsd", "log"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("log") == null) {
            _myOperations.put("log", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("log")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "encryptedLog"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"), byte[].class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "photo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtekAgent.xsd", "PhotoPart"), com.hectrix.www.ACTAtekAgent_xsd.PhotoPart.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("encryptLog", _params, new javax.xml.namespace.QName("", "status"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtekAgent.xsd", "encryptLog"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("encryptLog") == null) {
            _myOperations.put("encryptLog", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("encryptLog")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "encryptedLogs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtekAgent.xsd", "eLogArray"), com.hectrix.www.ACTAtekAgent_xsd.ELog[].class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("encryptLogMultiple", _params, new javax.xml.namespace.QName("", "status"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtekAgent.xsd", "encryptLogMultiple"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("encryptLogMultiple") == null) {
            _myOperations.put("encryptLogMultiple", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("encryptLogMultiple")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "magic"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "registrationID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("sync", _params, new javax.xml.namespace.QName("", "status"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtekAgent.xsd", "sync"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("sync") == null) {
            _myOperations.put("sync", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("sync")).add(_oper);
    }

    public ACTAtekAgentSkeleton() {
        this.impl = new com.hectrix.www.ACTAtekAgent_service.ACTAtekAgentImpl();
    }

    public ACTAtekAgentSkeleton(com.hectrix.www.ACTAtekAgent_service.ACTAtekAgentPortType impl) {
        this.impl = impl;
    }
    public java.lang.String log(java.lang.String magic, com.hectrix.www.ACTAtekAgent_xsd.Log unencryptedLog) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.log(magic, unencryptedLog);
        return ret;
    }

    public java.lang.String encryptLog(byte[] encryptedLog, com.hectrix.www.ACTAtekAgent_xsd.PhotoPart photo) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.encryptLog(encryptedLog, photo);
        return ret;
    }

    public java.lang.String encryptLogMultiple(com.hectrix.www.ACTAtekAgent_xsd.ELog[] encryptedLogs) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.encryptLogMultiple(encryptedLogs);
        return ret;
    }

    public java.lang.String sync(java.lang.String magic, java.lang.String registrationID) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.sync(magic, registrationID);
        return ret;
    }

}
