/**
 * PhotoPart.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtekAgent_xsd;

public class PhotoPart  implements java.io.Serializable {
    private java.lang.Boolean hasPhoto;

    private byte[] photoData;

    public PhotoPart() {
    }

    public PhotoPart(
           java.lang.Boolean hasPhoto,
           byte[] photoData) {
           this.hasPhoto = hasPhoto;
           this.photoData = photoData;
    }


    /**
     * Gets the hasPhoto value for this PhotoPart.
     * 
     * @return hasPhoto
     */
    public java.lang.Boolean getHasPhoto() {
        return hasPhoto;
    }


    /**
     * Sets the hasPhoto value for this PhotoPart.
     * 
     * @param hasPhoto
     */
    public void setHasPhoto(java.lang.Boolean hasPhoto) {
        this.hasPhoto = hasPhoto;
    }


    /**
     * Gets the photoData value for this PhotoPart.
     * 
     * @return photoData
     */
    public byte[] getPhotoData() {
        return photoData;
    }


    /**
     * Sets the photoData value for this PhotoPart.
     * 
     * @param photoData
     */
    public void setPhotoData(byte[] photoData) {
        this.photoData = photoData;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PhotoPart)) return false;
        PhotoPart other = (PhotoPart) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.hasPhoto==null && other.getHasPhoto()==null) || 
             (this.hasPhoto!=null &&
              this.hasPhoto.equals(other.getHasPhoto()))) &&
            ((this.photoData==null && other.getPhotoData()==null) || 
             (this.photoData!=null &&
              java.util.Arrays.equals(this.photoData, other.getPhotoData())));
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
        if (getHasPhoto() != null) {
            _hashCode += getHasPhoto().hashCode();
        }
        if (getPhotoData() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPhotoData());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPhotoData(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PhotoPart.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtekAgent.xsd", "PhotoPart"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hasPhoto");
        elemField.setXmlName(new javax.xml.namespace.QName("", "hasPhoto"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("photoData");
        elemField.setXmlName(new javax.xml.namespace.QName("", "photoData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
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
