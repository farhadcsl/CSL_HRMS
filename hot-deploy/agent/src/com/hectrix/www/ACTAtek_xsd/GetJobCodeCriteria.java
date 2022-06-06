/**
 * GetJobCodeCriteria.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class GetJobCodeCriteria  implements java.io.Serializable {
    private java.lang.Integer jobcodeID;

    private java.lang.String jobcodeDescription;

    private java.lang.Integer enable;

    private java.lang.Integer jobcode;

    public GetJobCodeCriteria() {
    }

    public GetJobCodeCriteria(
           java.lang.Integer jobcodeID,
           java.lang.String jobcodeDescription,
           java.lang.Integer enable,
           java.lang.Integer jobcode) {
           this.jobcodeID = jobcodeID;
           this.jobcodeDescription = jobcodeDescription;
           this.enable = enable;
           this.jobcode = jobcode;
    }


    /**
     * Gets the jobcodeID value for this GetJobCodeCriteria.
     * 
     * @return jobcodeID
     */
    public java.lang.Integer getJobcodeID() {
        return jobcodeID;
    }


    /**
     * Sets the jobcodeID value for this GetJobCodeCriteria.
     * 
     * @param jobcodeID
     */
    public void setJobcodeID(java.lang.Integer jobcodeID) {
        this.jobcodeID = jobcodeID;
    }


    /**
     * Gets the jobcodeDescription value for this GetJobCodeCriteria.
     * 
     * @return jobcodeDescription
     */
    public java.lang.String getJobcodeDescription() {
        return jobcodeDescription;
    }


    /**
     * Sets the jobcodeDescription value for this GetJobCodeCriteria.
     * 
     * @param jobcodeDescription
     */
    public void setJobcodeDescription(java.lang.String jobcodeDescription) {
        this.jobcodeDescription = jobcodeDescription;
    }


    /**
     * Gets the enable value for this GetJobCodeCriteria.
     * 
     * @return enable
     */
    public java.lang.Integer getEnable() {
        return enable;
    }


    /**
     * Sets the enable value for this GetJobCodeCriteria.
     * 
     * @param enable
     */
    public void setEnable(java.lang.Integer enable) {
        this.enable = enable;
    }


    /**
     * Gets the jobcode value for this GetJobCodeCriteria.
     * 
     * @return jobcode
     */
    public java.lang.Integer getJobcode() {
        return jobcode;
    }


    /**
     * Sets the jobcode value for this GetJobCodeCriteria.
     * 
     * @param jobcode
     */
    public void setJobcode(java.lang.Integer jobcode) {
        this.jobcode = jobcode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetJobCodeCriteria)) return false;
        GetJobCodeCriteria other = (GetJobCodeCriteria) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.jobcodeID==null && other.getJobcodeID()==null) || 
             (this.jobcodeID!=null &&
              this.jobcodeID.equals(other.getJobcodeID()))) &&
            ((this.jobcodeDescription==null && other.getJobcodeDescription()==null) || 
             (this.jobcodeDescription!=null &&
              this.jobcodeDescription.equals(other.getJobcodeDescription()))) &&
            ((this.enable==null && other.getEnable()==null) || 
             (this.enable!=null &&
              this.enable.equals(other.getEnable()))) &&
            ((this.jobcode==null && other.getJobcode()==null) || 
             (this.jobcode!=null &&
              this.jobcode.equals(other.getJobcode())));
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
        if (getJobcodeID() != null) {
            _hashCode += getJobcodeID().hashCode();
        }
        if (getJobcodeDescription() != null) {
            _hashCode += getJobcodeDescription().hashCode();
        }
        if (getEnable() != null) {
            _hashCode += getEnable().hashCode();
        }
        if (getJobcode() != null) {
            _hashCode += getJobcode().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetJobCodeCriteria.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "getJobCodeCriteria"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("jobcodeID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "jobcodeID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("jobcodeDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("", "jobcodeDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("enable");
        elemField.setXmlName(new javax.xml.namespace.QName("", "enable"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("jobcode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "jobcode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
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
