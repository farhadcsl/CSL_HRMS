/**
 * TerminalStatus2.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class TerminalStatus2  implements java.io.Serializable {
    private java.lang.String modelNumber;

    private java.lang.String serialNumber;

    private java.lang.String firmwareVersion;

    private java.lang.String FAMVersion;

    private java.lang.String description;

    private int registeredUsers;

    private int maximumUsers;

    private int automatchUsers;

    private int maximumAutomatchUsers;

    private java.lang.String currentStatus;

    private int actatekBoardID;

    private java.lang.String DBversion;

    private long flashleft;

    private long DBfilesize;

    public TerminalStatus2() {
    }

    public TerminalStatus2(
           java.lang.String modelNumber,
           java.lang.String serialNumber,
           java.lang.String firmwareVersion,
           java.lang.String FAMVersion,
           java.lang.String description,
           int registeredUsers,
           int maximumUsers,
           int automatchUsers,
           int maximumAutomatchUsers,
           java.lang.String currentStatus,
           int actatekBoardID,
           java.lang.String DBversion,
           long flashleft,
           long DBfilesize) {
           this.modelNumber = modelNumber;
           this.serialNumber = serialNumber;
           this.firmwareVersion = firmwareVersion;
           this.FAMVersion = FAMVersion;
           this.description = description;
           this.registeredUsers = registeredUsers;
           this.maximumUsers = maximumUsers;
           this.automatchUsers = automatchUsers;
           this.maximumAutomatchUsers = maximumAutomatchUsers;
           this.currentStatus = currentStatus;
           this.actatekBoardID = actatekBoardID;
           this.DBversion = DBversion;
           this.flashleft = flashleft;
           this.DBfilesize = DBfilesize;
    }


    /**
     * Gets the modelNumber value for this TerminalStatus2.
     * 
     * @return modelNumber
     */
    public java.lang.String getModelNumber() {
        return modelNumber;
    }


    /**
     * Sets the modelNumber value for this TerminalStatus2.
     * 
     * @param modelNumber
     */
    public void setModelNumber(java.lang.String modelNumber) {
        this.modelNumber = modelNumber;
    }


    /**
     * Gets the serialNumber value for this TerminalStatus2.
     * 
     * @return serialNumber
     */
    public java.lang.String getSerialNumber() {
        return serialNumber;
    }


    /**
     * Sets the serialNumber value for this TerminalStatus2.
     * 
     * @param serialNumber
     */
    public void setSerialNumber(java.lang.String serialNumber) {
        this.serialNumber = serialNumber;
    }


    /**
     * Gets the firmwareVersion value for this TerminalStatus2.
     * 
     * @return firmwareVersion
     */
    public java.lang.String getFirmwareVersion() {
        return firmwareVersion;
    }


    /**
     * Sets the firmwareVersion value for this TerminalStatus2.
     * 
     * @param firmwareVersion
     */
    public void setFirmwareVersion(java.lang.String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }


    /**
     * Gets the FAMVersion value for this TerminalStatus2.
     * 
     * @return FAMVersion
     */
    public java.lang.String getFAMVersion() {
        return FAMVersion;
    }


    /**
     * Sets the FAMVersion value for this TerminalStatus2.
     * 
     * @param FAMVersion
     */
    public void setFAMVersion(java.lang.String FAMVersion) {
        this.FAMVersion = FAMVersion;
    }


    /**
     * Gets the description value for this TerminalStatus2.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this TerminalStatus2.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the registeredUsers value for this TerminalStatus2.
     * 
     * @return registeredUsers
     */
    public int getRegisteredUsers() {
        return registeredUsers;
    }


    /**
     * Sets the registeredUsers value for this TerminalStatus2.
     * 
     * @param registeredUsers
     */
    public void setRegisteredUsers(int registeredUsers) {
        this.registeredUsers = registeredUsers;
    }


    /**
     * Gets the maximumUsers value for this TerminalStatus2.
     * 
     * @return maximumUsers
     */
    public int getMaximumUsers() {
        return maximumUsers;
    }


    /**
     * Sets the maximumUsers value for this TerminalStatus2.
     * 
     * @param maximumUsers
     */
    public void setMaximumUsers(int maximumUsers) {
        this.maximumUsers = maximumUsers;
    }


    /**
     * Gets the automatchUsers value for this TerminalStatus2.
     * 
     * @return automatchUsers
     */
    public int getAutomatchUsers() {
        return automatchUsers;
    }


    /**
     * Sets the automatchUsers value for this TerminalStatus2.
     * 
     * @param automatchUsers
     */
    public void setAutomatchUsers(int automatchUsers) {
        this.automatchUsers = automatchUsers;
    }


    /**
     * Gets the maximumAutomatchUsers value for this TerminalStatus2.
     * 
     * @return maximumAutomatchUsers
     */
    public int getMaximumAutomatchUsers() {
        return maximumAutomatchUsers;
    }


    /**
     * Sets the maximumAutomatchUsers value for this TerminalStatus2.
     * 
     * @param maximumAutomatchUsers
     */
    public void setMaximumAutomatchUsers(int maximumAutomatchUsers) {
        this.maximumAutomatchUsers = maximumAutomatchUsers;
    }


    /**
     * Gets the currentStatus value for this TerminalStatus2.
     * 
     * @return currentStatus
     */
    public java.lang.String getCurrentStatus() {
        return currentStatus;
    }


    /**
     * Sets the currentStatus value for this TerminalStatus2.
     * 
     * @param currentStatus
     */
    public void setCurrentStatus(java.lang.String currentStatus) {
        this.currentStatus = currentStatus;
    }


    /**
     * Gets the actatekBoardID value for this TerminalStatus2.
     * 
     * @return actatekBoardID
     */
    public int getActatekBoardID() {
        return actatekBoardID;
    }


    /**
     * Sets the actatekBoardID value for this TerminalStatus2.
     * 
     * @param actatekBoardID
     */
    public void setActatekBoardID(int actatekBoardID) {
        this.actatekBoardID = actatekBoardID;
    }


    /**
     * Gets the DBversion value for this TerminalStatus2.
     * 
     * @return DBversion
     */
    public java.lang.String getDBversion() {
        return DBversion;
    }


    /**
     * Sets the DBversion value for this TerminalStatus2.
     * 
     * @param DBversion
     */
    public void setDBversion(java.lang.String DBversion) {
        this.DBversion = DBversion;
    }


    /**
     * Gets the flashleft value for this TerminalStatus2.
     * 
     * @return flashleft
     */
    public long getFlashleft() {
        return flashleft;
    }


    /**
     * Sets the flashleft value for this TerminalStatus2.
     * 
     * @param flashleft
     */
    public void setFlashleft(long flashleft) {
        this.flashleft = flashleft;
    }


    /**
     * Gets the DBfilesize value for this TerminalStatus2.
     * 
     * @return DBfilesize
     */
    public long getDBfilesize() {
        return DBfilesize;
    }


    /**
     * Sets the DBfilesize value for this TerminalStatus2.
     * 
     * @param DBfilesize
     */
    public void setDBfilesize(long DBfilesize) {
        this.DBfilesize = DBfilesize;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TerminalStatus2)) return false;
        TerminalStatus2 other = (TerminalStatus2) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.modelNumber==null && other.getModelNumber()==null) || 
             (this.modelNumber!=null &&
              this.modelNumber.equals(other.getModelNumber()))) &&
            ((this.serialNumber==null && other.getSerialNumber()==null) || 
             (this.serialNumber!=null &&
              this.serialNumber.equals(other.getSerialNumber()))) &&
            ((this.firmwareVersion==null && other.getFirmwareVersion()==null) || 
             (this.firmwareVersion!=null &&
              this.firmwareVersion.equals(other.getFirmwareVersion()))) &&
            ((this.FAMVersion==null && other.getFAMVersion()==null) || 
             (this.FAMVersion!=null &&
              this.FAMVersion.equals(other.getFAMVersion()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            this.registeredUsers == other.getRegisteredUsers() &&
            this.maximumUsers == other.getMaximumUsers() &&
            this.automatchUsers == other.getAutomatchUsers() &&
            this.maximumAutomatchUsers == other.getMaximumAutomatchUsers() &&
            ((this.currentStatus==null && other.getCurrentStatus()==null) || 
             (this.currentStatus!=null &&
              this.currentStatus.equals(other.getCurrentStatus()))) &&
            this.actatekBoardID == other.getActatekBoardID() &&
            ((this.DBversion==null && other.getDBversion()==null) || 
             (this.DBversion!=null &&
              this.DBversion.equals(other.getDBversion()))) &&
            this.flashleft == other.getFlashleft() &&
            this.DBfilesize == other.getDBfilesize();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getModelNumber() != null) {
            _hashCode += getModelNumber().hashCode();
        }
        if (getSerialNumber() != null) {
            _hashCode += getSerialNumber().hashCode();
        }
        if (getFirmwareVersion() != null) {
            _hashCode += getFirmwareVersion().hashCode();
        }
        if (getFAMVersion() != null) {
            _hashCode += getFAMVersion().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        _hashCode += getRegisteredUsers();
        _hashCode += getMaximumUsers();
        _hashCode += getAutomatchUsers();
        _hashCode += getMaximumAutomatchUsers();
        if (getCurrentStatus() != null) {
            _hashCode += getCurrentStatus().hashCode();
        }
        _hashCode += getActatekBoardID();
        if (getDBversion() != null) {
            _hashCode += getDBversion().hashCode();
        }
        _hashCode += new Long(getFlashleft()).hashCode();
        _hashCode += new Long(getDBfilesize()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TerminalStatus2.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "TerminalStatus2"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("modelNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "modelNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serialNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "serialNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firmwareVersion");
        elemField.setXmlName(new javax.xml.namespace.QName("", "firmwareVersion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("FAMVersion");
        elemField.setXmlName(new javax.xml.namespace.QName("", "FAMVersion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("registeredUsers");
        elemField.setXmlName(new javax.xml.namespace.QName("", "registeredUsers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maximumUsers");
        elemField.setXmlName(new javax.xml.namespace.QName("", "maximumUsers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("automatchUsers");
        elemField.setXmlName(new javax.xml.namespace.QName("", "automatchUsers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maximumAutomatchUsers");
        elemField.setXmlName(new javax.xml.namespace.QName("", "maximumAutomatchUsers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "currentStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("actatekBoardID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ActatekBoardID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("DBversion");
        elemField.setXmlName(new javax.xml.namespace.QName("", "DBversion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("flashleft");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Flashleft"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("DBfilesize");
        elemField.setXmlName(new javax.xml.namespace.QName("", "DBfilesize"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
