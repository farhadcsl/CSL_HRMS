/**
 * User.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class User  implements java.io.Serializable {
    private java.lang.String userID;

    private java.lang.String lastName;

    private java.lang.String firstName;

    private java.lang.String otherName;

    private com.hectrix.www.ACTAtek_xsd.AdminLevel adminLevel;

    private boolean usePassword;

    private java.lang.String password;

    private int[] groupID;

    private int[] departmentID;

    private com.hectrix.www.ACTAtek_xsd.FingerprintSecurityLevel fingerprintSecurityLevel;

    private com.hectrix.www.ACTAtek_xsd.Status status;

    private com.hectrix.www.ACTAtek_xsd.Fingerprint[] fingerprints;

    private java.lang.String cardsn;

    private int fpgroupid;

    public User() {
    }

    public User(
           java.lang.String userID,
           java.lang.String lastName,
           java.lang.String firstName,
           java.lang.String otherName,
           com.hectrix.www.ACTAtek_xsd.AdminLevel adminLevel,
           boolean usePassword,
           java.lang.String password,
           int[] groupID,
           int[] departmentID,
           com.hectrix.www.ACTAtek_xsd.FingerprintSecurityLevel fingerprintSecurityLevel,
           com.hectrix.www.ACTAtek_xsd.Status status,
           com.hectrix.www.ACTAtek_xsd.Fingerprint[] fingerprints,
           java.lang.String cardsn,
           int fpgroupid) {
           this.userID = userID;
           this.lastName = lastName;
           this.firstName = firstName;
           this.otherName = otherName;
           this.adminLevel = adminLevel;
           this.usePassword = usePassword;
           this.password = password;
           this.groupID = groupID;
           this.departmentID = departmentID;
           this.fingerprintSecurityLevel = fingerprintSecurityLevel;
           this.status = status;
           this.fingerprints = fingerprints;
           this.cardsn = cardsn;
           this.fpgroupid = fpgroupid;
    }


    /**
     * Gets the userID value for this User.
     * 
     * @return userID
     */
    public java.lang.String getUserID() {
        return userID;
    }


    /**
     * Sets the userID value for this User.
     * 
     * @param userID
     */
    public void setUserID(java.lang.String userID) {
        this.userID = userID;
    }


    /**
     * Gets the lastName value for this User.
     * 
     * @return lastName
     */
    public java.lang.String getLastName() {
        return lastName;
    }


    /**
     * Sets the lastName value for this User.
     * 
     * @param lastName
     */
    public void setLastName(java.lang.String lastName) {
        this.lastName = lastName;
    }


    /**
     * Gets the firstName value for this User.
     * 
     * @return firstName
     */
    public java.lang.String getFirstName() {
        return firstName;
    }


    /**
     * Sets the firstName value for this User.
     * 
     * @param firstName
     */
    public void setFirstName(java.lang.String firstName) {
        this.firstName = firstName;
    }


    /**
     * Gets the otherName value for this User.
     * 
     * @return otherName
     */
    public java.lang.String getOtherName() {
        return otherName;
    }


    /**
     * Sets the otherName value for this User.
     * 
     * @param otherName
     */
    public void setOtherName(java.lang.String otherName) {
        this.otherName = otherName;
    }


    /**
     * Gets the adminLevel value for this User.
     * 
     * @return adminLevel
     */
    public com.hectrix.www.ACTAtek_xsd.AdminLevel getAdminLevel() {
        return adminLevel;
    }


    /**
     * Sets the adminLevel value for this User.
     * 
     * @param adminLevel
     */
    public void setAdminLevel(com.hectrix.www.ACTAtek_xsd.AdminLevel adminLevel) {
        this.adminLevel = adminLevel;
    }


    /**
     * Gets the usePassword value for this User.
     * 
     * @return usePassword
     */
    public boolean isUsePassword() {
        return usePassword;
    }


    /**
     * Sets the usePassword value for this User.
     * 
     * @param usePassword
     */
    public void setUsePassword(boolean usePassword) {
        this.usePassword = usePassword;
    }


    /**
     * Gets the password value for this User.
     * 
     * @return password
     */
    public java.lang.String getPassword() {
        return password;
    }


    /**
     * Sets the password value for this User.
     * 
     * @param password
     */
    public void setPassword(java.lang.String password) {
        this.password = password;
    }


    /**
     * Gets the groupID value for this User.
     * 
     * @return groupID
     */
    public int[] getGroupID() {
        return groupID;
    }


    /**
     * Sets the groupID value for this User.
     * 
     * @param groupID
     */
    public void setGroupID(int[] groupID) {
        this.groupID = groupID;
    }

    public int getGroupID(int i) {
        return this.groupID[i];
    }

    public void setGroupID(int i, int _value) {
        this.groupID[i] = _value;
    }


    /**
     * Gets the departmentID value for this User.
     * 
     * @return departmentID
     */
    public int[] getDepartmentID() {
        return departmentID;
    }


    /**
     * Sets the departmentID value for this User.
     * 
     * @param departmentID
     */
    public void setDepartmentID(int[] departmentID) {
        this.departmentID = departmentID;
    }

    public int getDepartmentID(int i) {
        return this.departmentID[i];
    }

    public void setDepartmentID(int i, int _value) {
        this.departmentID[i] = _value;
    }


    /**
     * Gets the fingerprintSecurityLevel value for this User.
     * 
     * @return fingerprintSecurityLevel
     */
    public com.hectrix.www.ACTAtek_xsd.FingerprintSecurityLevel getFingerprintSecurityLevel() {
        return fingerprintSecurityLevel;
    }


    /**
     * Sets the fingerprintSecurityLevel value for this User.
     * 
     * @param fingerprintSecurityLevel
     */
    public void setFingerprintSecurityLevel(com.hectrix.www.ACTAtek_xsd.FingerprintSecurityLevel fingerprintSecurityLevel) {
        this.fingerprintSecurityLevel = fingerprintSecurityLevel;
    }


    /**
     * Gets the status value for this User.
     * 
     * @return status
     */
    public com.hectrix.www.ACTAtek_xsd.Status getStatus() {
        return status;
    }


    /**
     * Sets the status value for this User.
     * 
     * @param status
     */
    public void setStatus(com.hectrix.www.ACTAtek_xsd.Status status) {
        this.status = status;
    }


    /**
     * Gets the fingerprints value for this User.
     * 
     * @return fingerprints
     */
    public com.hectrix.www.ACTAtek_xsd.Fingerprint[] getFingerprints() {
        return fingerprints;
    }


    /**
     * Sets the fingerprints value for this User.
     * 
     * @param fingerprints
     */
    public void setFingerprints(com.hectrix.www.ACTAtek_xsd.Fingerprint[] fingerprints) {
        this.fingerprints = fingerprints;
    }


    /**
     * Gets the cardsn value for this User.
     * 
     * @return cardsn
     */
    public java.lang.String getCardsn() {
        return cardsn;
    }


    /**
     * Sets the cardsn value for this User.
     * 
     * @param cardsn
     */
    public void setCardsn(java.lang.String cardsn) {
        this.cardsn = cardsn;
    }


    /**
     * Gets the fpgroupid value for this User.
     * 
     * @return fpgroupid
     */
    public int getFpgroupid() {
        return fpgroupid;
    }


    /**
     * Sets the fpgroupid value for this User.
     * 
     * @param fpgroupid
     */
    public void setFpgroupid(int fpgroupid) {
        this.fpgroupid = fpgroupid;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof User)) return false;
        User other = (User) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.userID==null && other.getUserID()==null) || 
             (this.userID!=null &&
              this.userID.equals(other.getUserID()))) &&
            ((this.lastName==null && other.getLastName()==null) || 
             (this.lastName!=null &&
              this.lastName.equals(other.getLastName()))) &&
            ((this.firstName==null && other.getFirstName()==null) || 
             (this.firstName!=null &&
              this.firstName.equals(other.getFirstName()))) &&
            ((this.otherName==null && other.getOtherName()==null) || 
             (this.otherName!=null &&
              this.otherName.equals(other.getOtherName()))) &&
            ((this.adminLevel==null && other.getAdminLevel()==null) || 
             (this.adminLevel!=null &&
              this.adminLevel.equals(other.getAdminLevel()))) &&
            this.usePassword == other.isUsePassword() &&
            ((this.password==null && other.getPassword()==null) || 
             (this.password!=null &&
              this.password.equals(other.getPassword()))) &&
            ((this.groupID==null && other.getGroupID()==null) || 
             (this.groupID!=null &&
              java.util.Arrays.equals(this.groupID, other.getGroupID()))) &&
            ((this.departmentID==null && other.getDepartmentID()==null) || 
             (this.departmentID!=null &&
              java.util.Arrays.equals(this.departmentID, other.getDepartmentID()))) &&
            ((this.fingerprintSecurityLevel==null && other.getFingerprintSecurityLevel()==null) || 
             (this.fingerprintSecurityLevel!=null &&
              this.fingerprintSecurityLevel.equals(other.getFingerprintSecurityLevel()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.fingerprints==null && other.getFingerprints()==null) || 
             (this.fingerprints!=null &&
              java.util.Arrays.equals(this.fingerprints, other.getFingerprints()))) &&
            ((this.cardsn==null && other.getCardsn()==null) || 
             (this.cardsn!=null &&
              this.cardsn.equals(other.getCardsn()))) &&
            this.fpgroupid == other.getFpgroupid();
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
        if (getUserID() != null) {
            _hashCode += getUserID().hashCode();
        }
        if (getLastName() != null) {
            _hashCode += getLastName().hashCode();
        }
        if (getFirstName() != null) {
            _hashCode += getFirstName().hashCode();
        }
        if (getOtherName() != null) {
            _hashCode += getOtherName().hashCode();
        }
        if (getAdminLevel() != null) {
            _hashCode += getAdminLevel().hashCode();
        }
        _hashCode += (isUsePassword() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getPassword() != null) {
            _hashCode += getPassword().hashCode();
        }
        if (getGroupID() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGroupID());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGroupID(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDepartmentID() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDepartmentID());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDepartmentID(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getFingerprintSecurityLevel() != null) {
            _hashCode += getFingerprintSecurityLevel().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getFingerprints() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFingerprints());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFingerprints(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getCardsn() != null) {
            _hashCode += getCardsn().hashCode();
        }
        _hashCode += getFpgroupid();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(User.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "User"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lastName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "firstName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("otherName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "otherName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("adminLevel");
        elemField.setXmlName(new javax.xml.namespace.QName("", "adminLevel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "AdminLevel"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("usePassword");
        elemField.setXmlName(new javax.xml.namespace.QName("", "usePassword"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("password");
        elemField.setXmlName(new javax.xml.namespace.QName("", "password"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("groupID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "groupID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("departmentID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "departmentID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fingerprintSecurityLevel");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fingerprintSecurityLevel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "FingerprintSecurityLevel"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "Status"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fingerprints");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fingerprints"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "Fingerprint"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("", "fingerprint"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardsn");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cardsn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fpgroupid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fpgroupid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
