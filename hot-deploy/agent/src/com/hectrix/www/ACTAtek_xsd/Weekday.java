/**
 * Weekday.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class Weekday implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected Weekday(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _SUNDAY = "SUNDAY";
    public static final java.lang.String _MONDAY = "MONDAY";
    public static final java.lang.String _TUESDAY = "TUESDAY";
    public static final java.lang.String _WEDNESDAY = "WEDNESDAY";
    public static final java.lang.String _THURSDAY = "THURSDAY";
    public static final java.lang.String _FRIDAY = "FRIDAY";
    public static final java.lang.String _SATURDAY = "SATURDAY";
    public static final java.lang.String _HOLIDAY = "HOLIDAY";
    public static final Weekday SUNDAY = new Weekday(_SUNDAY);
    public static final Weekday MONDAY = new Weekday(_MONDAY);
    public static final Weekday TUESDAY = new Weekday(_TUESDAY);
    public static final Weekday WEDNESDAY = new Weekday(_WEDNESDAY);
    public static final Weekday THURSDAY = new Weekday(_THURSDAY);
    public static final Weekday FRIDAY = new Weekday(_FRIDAY);
    public static final Weekday SATURDAY = new Weekday(_SATURDAY);
    public static final Weekday HOLIDAY = new Weekday(_HOLIDAY);
    public java.lang.String getValue() { return _value_;}
    public static Weekday fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        Weekday enumeration = (Weekday)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static Weekday fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Weekday.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "Weekday"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
