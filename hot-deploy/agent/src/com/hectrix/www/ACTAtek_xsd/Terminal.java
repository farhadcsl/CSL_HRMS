/**
 * Terminal.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class Terminal  implements java.io.Serializable {
    private int terminalID;

    private java.lang.String terminalType;

    private java.lang.String terminalName;

    private java.lang.String terminalSN;

    private java.lang.String terminalIP;

    public Terminal() {
    }

    public Terminal(
           int terminalID,
           java.lang.String terminalType,
           java.lang.String terminalName,
           java.lang.String terminalSN,
           java.lang.String terminalIP) {
           this.terminalID = terminalID;
           this.terminalType = terminalType;
           this.terminalName = terminalName;
           this.terminalSN = terminalSN;
           this.terminalIP = terminalIP;
    }


    /**
     * Gets the terminalID value for this Terminal.
     * 
     * @return terminalID
     */
    public int getTerminalID() {
        return terminalID;
    }


    /**
     * Sets the terminalID value for this Terminal.
     * 
     * @param terminalID
     */
    public void setTerminalID(int terminalID) {
        this.terminalID = terminalID;
    }


    /**
     * Gets the terminalType value for this Terminal.
     * 
     * @return terminalType
     */
    public java.lang.String getTerminalType() {
        return terminalType;
    }


    /**
     * Sets the terminalType value for this Terminal.
     * 
     * @param terminalType
     */
    public void setTerminalType(java.lang.String terminalType) {
        this.terminalType = terminalType;
    }


    /**
     * Gets the terminalName value for this Terminal.
     * 
     * @return terminalName
     */
    public java.lang.String getTerminalName() {
        return terminalName;
    }


    /**
     * Sets the terminalName value for this Terminal.
     * 
     * @param terminalName
     */
    public void setTerminalName(java.lang.String terminalName) {
        this.terminalName = terminalName;
    }


    /**
     * Gets the terminalSN value for this Terminal.
     * 
     * @return terminalSN
     */
    public java.lang.String getTerminalSN() {
        return terminalSN;
    }


    /**
     * Sets the terminalSN value for this Terminal.
     * 
     * @param terminalSN
     */
    public void setTerminalSN(java.lang.String terminalSN) {
        this.terminalSN = terminalSN;
    }


    /**
     * Gets the terminalIP value for this Terminal.
     * 
     * @return terminalIP
     */
    public java.lang.String getTerminalIP() {
        return terminalIP;
    }


    /**
     * Sets the terminalIP value for this Terminal.
     * 
     * @param terminalIP
     */
    public void setTerminalIP(java.lang.String terminalIP) {
        this.terminalIP = terminalIP;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Terminal)) return false;
        Terminal other = (Terminal) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.terminalID == other.getTerminalID() &&
            ((this.terminalType==null && other.getTerminalType()==null) || 
             (this.terminalType!=null &&
              this.terminalType.equals(other.getTerminalType()))) &&
            ((this.terminalName==null && other.getTerminalName()==null) || 
             (this.terminalName!=null &&
              this.terminalName.equals(other.getTerminalName()))) &&
            ((this.terminalSN==null && other.getTerminalSN()==null) || 
             (this.terminalSN!=null &&
              this.terminalSN.equals(other.getTerminalSN()))) &&
            ((this.terminalIP==null && other.getTerminalIP()==null) || 
             (this.terminalIP!=null &&
              this.terminalIP.equals(other.getTerminalIP())));
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
        _hashCode += getTerminalID();
        if (getTerminalType() != null) {
            _hashCode += getTerminalType().hashCode();
        }
        if (getTerminalName() != null) {
            _hashCode += getTerminalName().hashCode();
        }
        if (getTerminalSN() != null) {
            _hashCode += getTerminalSN().hashCode();
        }
        if (getTerminalIP() != null) {
            _hashCode += getTerminalIP().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Terminal.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "Terminal"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalSN");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalSN"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalIP");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalIP"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
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
