/**
 * ELog.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtekAgent_xsd;

public class ELog  implements java.io.Serializable {
    private byte[] encryptedLog;

    private com.hectrix.www.ACTAtekAgent_xsd.PhotoPart photo;

    public ELog() {
    }

    public ELog(
           byte[] encryptedLog,
           com.hectrix.www.ACTAtekAgent_xsd.PhotoPart photo) {
           this.encryptedLog = encryptedLog;
           this.photo = photo;
    }


    /**
     * Gets the encryptedLog value for this ELog.
     * 
     * @return encryptedLog
     */
    public byte[] getEncryptedLog() {
        return encryptedLog;
    }


    /**
     * Sets the encryptedLog value for this ELog.
     * 
     * @param encryptedLog
     */
    public void setEncryptedLog(byte[] encryptedLog) {
        this.encryptedLog = encryptedLog;
    }


    /**
     * Gets the photo value for this ELog.
     * 
     * @return photo
     */
    public com.hectrix.www.ACTAtekAgent_xsd.PhotoPart getPhoto() {
        return photo;
    }


    /**
     * Sets the photo value for this ELog.
     * 
     * @param photo
     */
    public void setPhoto(com.hectrix.www.ACTAtekAgent_xsd.PhotoPart photo) {
        this.photo = photo;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ELog)) return false;
        ELog other = (ELog) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.encryptedLog==null && other.getEncryptedLog()==null) || 
             (this.encryptedLog!=null &&
              java.util.Arrays.equals(this.encryptedLog, other.getEncryptedLog()))) &&
            ((this.photo==null && other.getPhoto()==null) || 
             (this.photo!=null &&
              this.photo.equals(other.getPhoto())));
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
        if (getEncryptedLog() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEncryptedLog());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEncryptedLog(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getPhoto() != null) {
            _hashCode += getPhoto().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ELog.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtekAgent.xsd", "eLog"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("encryptedLog");
        elemField.setXmlName(new javax.xml.namespace.QName("", "encryptedLog"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("photo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "photo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtekAgent.xsd", "PhotoPart"));
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
