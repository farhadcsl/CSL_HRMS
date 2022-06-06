package org.ofbiz.humanres.report.profile;

import javolution.util.FastList;


import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SingleEmployeeProfileService {
	
	public static final String module = SingleEmployeeProfileService.class.getName();


    public static String getAnEmployeeName(Delegator delegator, String partyId) {
        List<GenericValue> partyName = null;
        try {
        	partyName = delegator.findByAnd("Person", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String firstName = "";
        String lastName= "";
        String middleName= "";
        if (UtilValidate.isNotEmpty(partyName)) {
            if (UtilValidate.isNotEmpty(partyName.get(0).get("firstName"))) {
            	firstName = partyName.get(0).get("firstName").toString();
            }
        }
        if (UtilValidate.isNotEmpty(partyName)) {
            if (UtilValidate.isNotEmpty(partyName.get(0).get("middleName"))) {
            	middleName = partyName.get(0).get("middleName").toString();
            }
        }
        if (UtilValidate.isNotEmpty(partyName)) {
            if (UtilValidate.isNotEmpty(partyName.get(0).get("lastName"))) {
            	lastName = partyName.get(0).get("lastName").toString();
            }
        }
        String Name = firstName +" " +middleName+" " +lastName;
        return Name;
    }
    
    public static String getAnEmployeeId(Delegator delegator, String partyId) {
        List<GenericValue> partyEmplId = null;
        try {
        	partyEmplId = delegator.findByAnd("PartyAndPersonAndFulfillment", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String userId = "";
       
        if (UtilValidate.isNotEmpty(partyEmplId)) {
            if (UtilValidate.isNotEmpty(partyEmplId.get(0).get("userLoginId"))) {
            	userId = partyEmplId.get(0).get("userLoginId").toString();
            }
        }
        
      
        return userId;
    }
    
    public static String getAnFatherName(Delegator delegator, String partyId) {
        List<GenericValue> partyFatherName = null;
        try {
        	partyFatherName = delegator.findByAnd("PartyAndPerson", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String FatherName = "";
       
        if (UtilValidate.isNotEmpty(partyFatherName)) {
            if (UtilValidate.isNotEmpty(partyFatherName.get(0).get("fatherName"))) {
            	FatherName = partyFatherName.get(0).get("fatherName").toString();
            }
        }
        
      
        return FatherName;
    }  
    
    
    public static String getAnDateofBirth(Delegator delegator, String partyId) {
        List<GenericValue> partyDateofBirth = null;
        try {
        	partyDateofBirth = delegator.findByAnd("EmploymentAndPerson", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String DateofBirth = "";
       
        if (UtilValidate.isNotEmpty(partyDateofBirth)) {
            if (UtilValidate.isNotEmpty(partyDateofBirth.get(0).get("birthDate"))) {
            	DateofBirth = partyDateofBirth.get(0).get("birthDate").toString();
            }
        }
        
      
        return DateofBirth;
    }
    
    public static String getAnEmployeeMobile(Delegator delegator, String partyId) {
        List<GenericValue> partyMobile = null;
        try {
        	partyMobile = delegator.findByAnd("PartyAndTelecomNumber", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String Mobile = "";
      
        if (UtilValidate.isNotEmpty(partyMobile)) {
            if (UtilValidate.isNotEmpty(partyMobile.get(0).get("contactNumber"))) {
            	Mobile = partyMobile.get(0).get("contactNumber").toString();
            }
        }
       return Mobile;
    }
    
    public static String getAnEmergencyContactName(Delegator delegator, String partyId) {
        List<GenericValue> partyemergencyContactName = null;
        try {
        	partyemergencyContactName = delegator.findByAnd("EmergencyAndNomineeInfo", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String emergencyContactName = "";
      
        if (UtilValidate.isNotEmpty(partyemergencyContactName)) {
            if (UtilValidate.isNotEmpty(partyemergencyContactName.get(0).get("emergencyContactName"))) {
            	emergencyContactName = partyemergencyContactName.get(0).get("emergencyContactName").toString();
            }
        }
       return emergencyContactName;
    }
    
    
    public static String getAnemergencyContactAddress(Delegator delegator, String partyId) {
        List<GenericValue> partyemergencyContactAddress = null;
        try {
        	partyemergencyContactAddress = delegator.findByAnd("EmergencyAndNomineeInfo", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String emergencyContactAddress = "";
      
        if (UtilValidate.isNotEmpty(partyemergencyContactAddress)) {
            if (UtilValidate.isNotEmpty(partyemergencyContactAddress.get(0).get("emergencyContactAddress"))) {
            	emergencyContactAddress = partyemergencyContactAddress.get(0).get("emergencyContactAddress").toString();
            }
        }
       return emergencyContactAddress;
    }
    
    
    public static String getAnemergencyContactNumber(Delegator delegator, String partyId) {
        List<GenericValue> partyemergencyContactNumber = null;
        try {
        	partyemergencyContactNumber = delegator.findByAnd("EmergencyAndNomineeInfo", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String emergencyContactNumber = "";
      
        if (UtilValidate.isNotEmpty(partyemergencyContactNumber)) {
            if (UtilValidate.isNotEmpty(partyemergencyContactNumber.get(0).get("emergencyContactNumber"))) {
            	emergencyContactNumber = partyemergencyContactNumber.get(0).get("emergencyContactNumber").toString();
            }
        }
       return emergencyContactNumber;
    }
    
    
    public static String getAnemergencyContactRelation(Delegator delegator, String partyId) {
        List<GenericValue> partyemergencyContactRelation = null;
        try {
        	partyemergencyContactRelation = delegator.findByAnd("EmergencyAndNomineeInfo", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String emergencyContactRelation = "";
      
        if (UtilValidate.isNotEmpty(partyemergencyContactRelation)) {
            if (UtilValidate.isNotEmpty(partyemergencyContactRelation.get(0).get("emergencyContactRelation"))) {
            	emergencyContactRelation = partyemergencyContactRelation.get(0).get("emergencyContactRelation").toString();
            }
        }
       return emergencyContactRelation;
    }
	
    
    
    public static String getAnEmailAdress(Delegator delegator, String partyId) {
        List<GenericValue> partyEmailAdress = null;
        try {
        	partyEmailAdress = delegator.findByAnd("PartyAndContactMech", UtilMisc.toMap("partyId", partyId,"contactMechTypeId","EMAIL_ADDRESS"));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String EmailAdress = "";
       
        if (UtilValidate.isNotEmpty(partyEmailAdress)) {
            if (UtilValidate.isNotEmpty(partyEmailAdress.get(0).get("infoString"))) {
            	EmailAdress = partyEmailAdress.get(0).get("infoString").toString();
            }
        }
        
      
        return EmailAdress;
    }  
    
    public static String getAnAdress(Delegator delegator, String partyId) {
        List<GenericValue> partyAdress = null;
        try {
        	partyAdress = delegator.findByAnd("PartyAndContactMech", UtilMisc.toMap("partyId", partyId,"contactMechTypeId","POSTAL_ADDRESS"));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String Adress = "";
       
        if (UtilValidate.isNotEmpty(partyAdress)) {
            if (UtilValidate.isNotEmpty(partyAdress.get(0).get("paAddress1"))) {
            	Adress = partyAdress.get(0).get("paAddress1").toString();
            }
        }
        
      
        return Adress;
    }  
    
    
    public static String getAnPicture(Delegator delegator, String partyId) {
        List<GenericValue> partyPicture = null;
        try {
        	partyPicture = delegator.findByAnd("PartyContentDetail", UtilMisc.toMap("partyId", partyId,"partyContentTypeId","INTERNAL","mimeTypeId","image/jpeg","contentTypeId","DOCUMENT"));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String pictureName = "";
        String dataResourceId="";
        String pictureUrl="";

    	int contentCount=partyPicture.size();
        if (UtilValidate.isNotEmpty(partyPicture)) {
            if (UtilValidate.isNotEmpty(partyPicture.get(contentCount-1).get("contentName"))) {
            	pictureName = partyPicture.get(contentCount-1).get("contentName").toString();
            	dataResourceId = partyPicture.get(contentCount-1).get("dataResourceId").toString();
            }
        }
        GenericValue photoResourceInfo = null;
  		try { 
  			photoResourceInfo = delegator.findByPrimaryKey("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId));
  			if (UtilValidate.isNotEmpty(photoResourceInfo)) {
  				pictureUrl=photoResourceInfo.get("objectInfo").toString();
  			}
  			else{
  			   String workingDir = System.getProperty("user.dir");
  				pictureUrl=workingDir+"/framework/images/webapp/images/noImage.jpeg";
  			}
  		} catch (GenericEntityException e) {
  			e.printStackTrace();
  			
  		}
      
        return pictureUrl;
    }
	public static String getAnEmployeeDesignation(Delegator delegator, String partyId) {
        List<GenericValue> partyDesignation = null;
        List<GenericValue> partyDesignation1 = null;
        List<GenericValue> partyDesignation2 = null;
        try {
        	partyDesignation = delegator.findByAnd("EmplPositionFulfillment", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String Designation = "";
      
        if (UtilValidate.isNotEmpty(partyDesignation)) {
            if (UtilValidate.isNotEmpty(partyDesignation.get(0).get("emplPositionId"))) {
            	Designation = partyDesignation.get(0).get("emplPositionId").toString();
            }
        }
        
        try {
        	partyDesignation1 = delegator.findByAnd("EmplPosition", UtilMisc.toMap("emplPositionId", Designation));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String Designation1 = "";
      
        if (UtilValidate.isNotEmpty(partyDesignation1)) {
            if (UtilValidate.isNotEmpty(partyDesignation1.get(0).get("emplPositionTypeId"))) {
            	Designation1 = partyDesignation1.get(0).get("emplPositionTypeId").toString();
            }
        }
        
        try {
        	partyDesignation2 = delegator.findByAnd("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", Designation1));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String Designation2 = "";
      
        if (UtilValidate.isNotEmpty(partyDesignation2)) {
            if (UtilValidate.isNotEmpty(partyDesignation2.get(0).get("description"))) {
            	Designation2 = partyDesignation2.get(0).get("description").toString();
            }
        }
        return Designation2;
    }
	

	public static String getAnBloodGroup(Delegator delegator, String partyId) {
        List<GenericValue> partyBloodGroup = null;
        try {
        	partyBloodGroup = delegator.findByAnd("PartyAndPerson", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String BloodGroup = "";
      
        if (UtilValidate.isNotEmpty(partyBloodGroup)) {
            if (UtilValidate.isNotEmpty(partyBloodGroup.get(0).get("bloodgroup"))) {
            	BloodGroup = partyBloodGroup.get(0).get("bloodgroup").toString();
            }
        }
       return BloodGroup;
    }
	
	public static String getAnDepartment(Delegator delegator,String partyId){
		String departmentName="";
		String sectionId="";
		String partyUnitId="";
		String partyTypeId="";
		String partyUnitName="";
		String roleTypeIdFrom="";
		 List<GenericValue> party = null;
			try {
				party = delegator.findByAnd("EmplPositionAndFulfillment", UtilMisc.toMap("employeePartyId", partyId));
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			GenericValue partys = EntityUtil.getFirst(party);
			if(UtilValidate.isNotEmpty(partys)){
				partyUnitId=partys.getString("partyId");
				
			}
			List<GenericValue> partyRel = null;
			try {
				partyRel = delegator.findByAnd("PartyRelationshipAndPartyDetail", UtilMisc.toMap("partyId", partyUnitId));
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*GenericValue partyRels = EntityUtil.getFirst(partyRel);
			if(UtilValidate.isNotEmpty(partyRels)){
				partyTypeId=partyRels.getString("partyTypeId");
				
			}
			if (partyTypeId.equalsIgnoreCase("PARTY_SECTION")){
				sectionName=partyRels.getString("toGroupName");
			}else {
				sectionName=partyRels.getString("fromGroupName");
			}*/
			
			
			GenericValue partyRels = EntityUtil.getFirst(partyRel);
			if(UtilValidate.isNotEmpty(partyRels)){
				partyTypeId=partyRels.getString("partyTypeId");
				
			}
			if (partyTypeId.equalsIgnoreCase("PARTY_DEPARTMENT")){
				departmentName=partyRels.getString("toGroupName");
			} else if (partyTypeId.equalsIgnoreCase("PARTY_SECTION")) {
				departmentName=partyRels.getString("fromGroupName");
			}
			else if (partyTypeId.equalsIgnoreCase("PARTY_UNIT")){
				sectionId=partyRels.getString("partyIdFrom");
				List<GenericValue> partyRell = null;
				try {
					partyRell = delegator.findByAnd("PartyRelationshipAndPartyDetail", UtilMisc.toMap("partyId", sectionId));
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				GenericValue partyRells = EntityUtil.getFirst(partyRell);
				if(UtilValidate.isNotEmpty(partyRells)){
					departmentName=partyRells.getString("fromGroupName");
					
				}
			}
		return departmentName;
	}
	  
    public static String getAnomineeName(Delegator delegator, String partyId) {
        List<GenericValue> partynomineeName = null;
        try {
        	partynomineeName = delegator.findByAnd("EmergencyAndNomineeInfo", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String nomineeName = "";
      
        if (UtilValidate.isNotEmpty(partynomineeName)) {
            if (UtilValidate.isNotEmpty(partynomineeName.get(0).get("nomineeInfoName"))) {
            	nomineeName = partynomineeName.get(0).get("nomineeInfoName").toString();
            }
        }
       return nomineeName;
    }
    
    public static String getAnomineeAddress(Delegator delegator, String partyId) {
        List<GenericValue> partynomineeInfoAddress = null;
        try {
        	partynomineeInfoAddress = delegator.findByAnd("EmergencyAndNomineeInfo", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String nomineeInfoAddress = "";
      
        if (UtilValidate.isNotEmpty(partynomineeInfoAddress)) {
            if (UtilValidate.isNotEmpty(partynomineeInfoAddress.get(0).get("nomineeInfoAddress"))) {
            	nomineeInfoAddress = partynomineeInfoAddress.get(0).get("nomineeInfoAddress").toString();
            }
        }
       return nomineeInfoAddress;
    }
    
    public static String getAnomineeInfoNumber(Delegator delegator, String partyId) {
        List<GenericValue> partynomineeInfoNumber = null;
        try {
        	partynomineeInfoNumber = delegator.findByAnd("EmergencyAndNomineeInfo", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String nomineeInfoNumber = "";
      
        if (UtilValidate.isNotEmpty(partynomineeInfoNumber)) {
            if (UtilValidate.isNotEmpty(partynomineeInfoNumber.get(0).get("nomineeInfoNumber"))) {
            	nomineeInfoNumber = partynomineeInfoNumber.get(0).get("nomineeInfoNumber").toString();
            }
        }
       return nomineeInfoNumber;
    }
    
    public static String getAnomineeRelation(Delegator delegator, String partyId) {
        List<GenericValue> partynomineeInfoRelation = null;
        try {
        	partynomineeInfoRelation = delegator.findByAnd("EmergencyAndNomineeInfo", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String nomineeInfoRelation = "";
      
        if (UtilValidate.isNotEmpty(partynomineeInfoRelation)) {
            if (UtilValidate.isNotEmpty(partynomineeInfoRelation.get(0).get("nomineeInfoRelation"))) {
            	nomineeInfoRelation = partynomineeInfoRelation.get(0).get("nomineeInfoRelation").toString();
            }
        }
       return nomineeInfoRelation;
    }
	
    public static String getAnexamTitle(Delegator delegator, String partyId) {
        List<GenericValue> partyexamTitle = null;
        try {
        	partyexamTitle = delegator.findByAnd("PartyQual", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String examTitle = "";
       
        if (UtilValidate.isNotEmpty(partyexamTitle)) {
            if (UtilValidate.isNotEmpty(partyexamTitle.get(0).get("partyQualTypeId"))) {
            	examTitle = partyexamTitle.get(0).get("partyQualTypeId").toString();
            }
        }
        
      
        return examTitle;
    }  
    public static String getAnpassingYear(Delegator delegator, String partyId) {
        List<GenericValue> partypassingYear = null;
        try {
        	partypassingYear = delegator.findByAnd("PartyQual", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String passingYear = "";
       
        if (UtilValidate.isNotEmpty(partypassingYear)) {
            if (UtilValidate.isNotEmpty(partypassingYear.get(0).get("passingYear"))) {
            	passingYear = partypassingYear.get(0).get("passingYear").toString();
            }
        }
        
      
        return passingYear;
    }  
    public static String getAnInstitute(Delegator delegator, String partyId) {
        List<GenericValue> partyInstitute = null;
        try {
        	partyInstitute = delegator.findByAnd("PartyQual", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String Institute = "";
       
        if (UtilValidate.isNotEmpty(partyInstitute)) {
            if (UtilValidate.isNotEmpty(partyInstitute.get(0).get("qualificationDesc"))) {
            	Institute = partyInstitute.get(0).get("qualificationDesc").toString();
            }
        }
        
      
        return Institute;
    }  
    
	
}