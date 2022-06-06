<form method="post" action="/humanres/control/updateBasicInfo" id="basicInfo" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="basicInfo">
    <input name="partyId" id="basicInfo_partyId" type="hidden" value=${partyId}>
    
    <table cellspacing="0">
    <tbody>
    <tr>
	    <td class="label">
			<span id="basicInfo_partyId_title">Employee ID</span>    </td>
	    <td>${partyId}</td>
    </tr>
    
    <tr>
	    <td class="label">
			<span id="basicInfo_name_title">First Name</span>    </td>
	    <td>
		    <div class="basicInfoUpdateLabel" id="lblBasicInfo_firstName"> ${personInfo.firstName?if_exists}</div> 
		    <div class="basicInfoUpdateEditIcon" id="editIconBasicInfo_firstName" onclick="editFieldOpen('firstName','')"> <img src="/images/editIcon.png"> </div> 
		    <div> <input type="text"  id="txtBasicInfo_firstName"  class="basicInfoUpdateTxt" value="${personInfo.firstName?if_exists}"></div> 
		    <div class="basicInfoUpdateBtn" id="btnBasicInfo_firstName">
		    	<a class="buttontext" onclick="sendBasicInfoUpdateRequisition('firstName','PartyAndPerson','')">Update Requisition</a>
		    </div> 
	     </td>
    </tr>
    
    <tr>
	    <td class="label">
			<span id="basicInfo_name_title">Middle Name</span>    </td>
	    <td>
		    <div class="basicInfoUpdateLabel" id="lblBasicInfo_middleName"> ${personInfo.middleName?if_exists}</div> 
		    <div class="basicInfoUpdateEditIcon" id="editIconBasicInfo_middleName" onclick="editFieldOpen('middleName','')"> <img src="/images/editIcon.png"> </div> 
		    <div> <input type="text"  id="txtBasicInfo_middleName"  class="basicInfoUpdateTxt" value="${personInfo.middleName?if_exists}"></div> 
		    <div class="basicInfoUpdateBtn" id="btnBasicInfo_middleName">
		    	<a class="buttontext" onclick="sendBasicInfoUpdateRequisition('middleName','PartyAndPerson','')">Update Requisition</a>
		    </div> 
	     </td>
    </tr>
    
    <tr>
	    <td class="label">
			<span id="basicInfo_name_title">Last Name</span>    </td>
	    <td>
		    <div class="basicInfoUpdateLabel" id="lblBasicInfo_lastName">${personInfo.lastName?if_exists} </div> 
		    <div class="basicInfoUpdateEditIcon" id="editIconBasicInfo_lastName" onclick="editFieldOpen('lastName','')"> <img src="/images/editIcon.png"> </div> 
		    <div> <input type="text"  id="txtBasicInfo_lastName"  class="basicInfoUpdateTxt" value="${personInfo.lastName?if_exists}"></div> 
		    <div class="basicInfoUpdateBtn" id="btnBasicInfo_lastName">
		    	<a class="buttontext" onclick="sendBasicInfoUpdateRequisition('lastName','PartyAndPerson','')">Update Requisition</a>
		    </div> 
	     </td>
    </tr>
    <tr>
	    <td class="label">
			<span id="basicInfo_name_title">Father Name</span>    </td>
	    <td>
		    <div class="basicInfoUpdateLabel" id="lblBasicInfo_fatherName"> ${personInfo.fatherName?if_exists} </div> 
		    <div class="basicInfoUpdateEditIcon" id="editIconBasicInfo_fatherName" onclick="editFieldOpen('fatherName','')"> <img src="/images/editIcon.png"> </div> 
		    <div> <input type="text"  id="txtBasicInfo_fatherName"  class="basicInfoUpdateTxt" value="${personInfo.fatherName?if_exists}"></div> 
		    <div class="basicInfoUpdateBtn" id="btnBasicInfo_fatherName">
		    	<a class="buttontext" onclick="sendBasicInfoUpdateRequisition('fatherName','PartyAndPerson','')">Update Requisition</a>
		    </div> 
	     </td>
    </tr>
    <tr>
	    <td class="label">
			<span id="basicInfo_name_title">Mother Name</span>    </td>
	    <td>
		    <div class="basicInfoUpdateLabel" id="lblBasicInfo_mothersMaidenName"> ${personInfo.mothersMaidenName?if_exists} </div> 
		    <div class="basicInfoUpdateEditIcon" id="editIconBasicInfo_mothersMaidenName" onclick="editFieldOpen('mothersMaidenName','')"> <img src="/images/editIcon.png"> </div> 
		    <div> <input type="text"  id="txtBasicInfo_mothersMaidenName"  class="basicInfoUpdateTxt" value="${personInfo.mothersMaidenName?if_exists}"></div> 
		    <div class="basicInfoUpdateBtn" id="btnBasicInfo_mothersMaidenName">
		    	<a class="buttontext" onclick="sendBasicInfoUpdateRequisition('mothersMaidenName','PartyAndPerson','')">Update Requisition</a>
		    </div> 
	     </td>
    </tr>
    <tr>
	    <td class="label">
			<span id="basicInfo_name_title">Spouse Name</span>    </td>
	    <td>
	    	
		    <div class="basicInfoUpdateLabel" id="lblBasicInfo_spouseName"> ${personInfo.spouseName?if_exists} </div> 
		    <div class="basicInfoUpdateEditIcon" id="editIconBasicInfo_spouseName" onclick="editFieldOpen('spouseName','')"> <img src="/images/editIcon.png"> </div> 
		    <div> <input type="text"  id="txtBasicInfo_spouseName"  class="basicInfoUpdateTxt" value="${personInfo.spouseName?if_exists}"></div> 
		    <div class="basicInfoUpdateBtn" id="btnBasicInfo_spouseName">
		    	<a class="buttontext" onclick="sendBasicInfoUpdateRequisition('spouseName','PartyAndPerson','')">Update Requisition</a>
		    </div> 
	     </td>
    </tr>
    <tr>
	    <td class="label">
			<span id="basicInfo_name_title">Religion</span>    </td>
	    <td>
		    <div class="basicInfoUpdateLabel" id="lblBasicInfo_religion"> ${personInfo.religion?if_exists} </div> 
		    <div class="basicInfoUpdateEditIcon" id="editIconBasicInfo_religion" onclick="editFieldOpen('religion','')"> <img src="/images/editIcon.png"> </div> 
		    <div> <input type="text"  id="txtBasicInfo_religion"  class="basicInfoUpdateTxt" value="${personInfo.religion?if_exists}"></div> 
		    <div class="basicInfoUpdateBtn" id="btnBasicInfo_religion">
		    	<a class="buttontext" onclick="sendBasicInfoUpdateRequisition('religion','PartyAndPerson','')">Update Requisition</a>
		    </div> 
	     </td>	
    </tr>
    <tr>
	    <td class="label">
			<span id="basicInfo_name_title">Marital Status</span>    </td>
	    <td>
		    <div class="basicInfoUpdateLabel" id="lblBasicInfo_maritalStatus">${personInfo.maritalStatus?if_exists}</div> 
		    <div class="basicInfoUpdateEditIcon" id="editIconBasicInfo_maritalStatus" onclick="editFieldOpen('maritalStatus','')"> <img src="/images/editIcon.png"> </div> 
		    <div> 
			    <select  id="txtBasicInfo_maritalStatus"  class="basicInfoUpdateTxt">
			    	<option></option>
			    	<option value="S">Single</option>
			    	<option value="M">Married</option>
			    	<option value="P">Separated</option>
			    	<option value="D">Divorced</option>
			    	<option value="W">Widowed</option>
			    </select>
			</div> 
		    <div class="basicInfoUpdateBtn" id="btnBasicInfo_maritalStatus">
		    	<a class="buttontext" onclick="sendBasicInfoUpdateRequisition('maritalStatus','PartyAndPerson','')">Update Requisition</a>
		    </div> 
	     </td>	
    </tr>
    
    <tr>
	    <td class="label">
			<span id="basicInfo_name_title">Blood Group</span>    </td>
	    <td>
		    <div class="basicInfoUpdateLabel" id="lblBasicInfo_bloodgroup"> ${personInfo.bloodgroup?if_exists} </div> 
		    <div class="basicInfoUpdateEditIcon" id="editIconBasicInfo_bloodgroup" onclick="editFieldOpen('bloodgroup','')"> <img src="/images/editIcon.png"> </div> 
		    <div> <input type="text"  id="txtBasicInfo_bloodgroup"  class="basicInfoUpdateTxt" value="${personInfo.bloodgroup?if_exists}"></div> 
		    <div class="basicInfoUpdateBtn" id="btnBasicInfo_bloodgroup">
		    	<a class="buttontext" onclick="sendBasicInfoUpdateRequisition('bloodgroup','PartyAndPerson','')">Update Requisition</a>
		    </div> 
	     </td>	
    </tr>
    <tr>
	    <td class="label">
			<span id="basicInfo_name_title">National Id</span>    </td>
	    <td>
		   <div class="basicInfoUpdateLabel" id="lblBasicInfo_nationalid"> ${personInfo.nationalid?if_exists} </div> 
		    <div class="basicInfoUpdateEditIcon" id="editIconBasicInfo_nationalid" onclick="editFieldOpen('nationalid','')"> <img src="/images/editIcon.png"> </div> 
		    <div> <input type="text"  id="txtBasicInfo_nationalid"  class="basicInfoUpdateTxt" value="${personInfo.nationalid?if_exists}"></div> 
		    <div class="basicInfoUpdateBtn" id="btnBasicInfo_nationalid">
		    	<a class="buttontext" onclick="sendBasicInfoUpdateRequisition('nationalid','PartyAndPerson','')">Update Requisition</a>
		    </div> 
	     </td>	
    </tr>
    <tr>
	    <td class="label">
			<span id="basicInfo_name_title">Birth Certificate No</span>    </td>
	    <td>
		   <div class="basicInfoUpdateLabel" id="lblBasicInfo_birthCertificateNo"> ${personInfo.birthCertificateNo?if_exists} </div> 
		    <div class="basicInfoUpdateEditIcon" id="editIconBasicInfo_birthCertificateNo" onclick="editFieldOpen('birthCertificateNo','')"> <img src="/images/editIcon.png"> </div> 
		    <div> <input type="text"  id="txtBasicInfo_birthCertificateNo"  class="basicInfoUpdateTxt" value="${personInfo.birthCertificateNo?if_exists}"></div> 
		    <div class="basicInfoUpdateBtn" id="btnBasicInfo_birthCertificateNo">
		    	<a class="buttontext" onclick="sendBasicInfoUpdateRequisition('birthCertificateNo','PartyAndPerson','')">Update Requisition</a>
		    </div> 
	     </td>	
    </tr>
    <tr>
	    <td class="label">
			<span id="basicInfo_name_title">Passport No</span>    </td>
	    <td>
		   <div class="basicInfoUpdateLabel" id="lblBasicInfo_passportNumber"> ${personInfo.passportNumber?if_exists} </div> 
		    <div class="basicInfoUpdateEditIcon" id="editIconBasicInfo_passportNumber" onclick="editFieldOpen('passportNumber','')"> <img src="/images/editIcon.png"> </div> 
		    <div> <input type="text"  id="txtBasicInfo_passportNumber"  class="basicInfoUpdateTxt" value="${personInfo.passportNumber?if_exists}"></div> 
		    <div class="basicInfoUpdateBtn" id="btnBasicInfo_passportNumber">
		    	<a class="buttontext" onclick="sendBasicInfoUpdateRequisition('passportNumber','PartyAndPerson','')">Update Requisition</a>
		    </div> 
	     </td>	
    </tr>
    <tr>
	    <td class="label">
			<span id="basicInfo_name_title">Driving License No</span>    </td>
	    <td>
		   <div class="basicInfoUpdateLabel" id="lblBasicInfo_drivingLicenseNo"> ${personInfo.drivingLicenseNo?if_exists} </div> 
		    <div class="basicInfoUpdateEditIcon" id="editIconBasicInfo_drivingLicenseNo" onclick="editFieldOpen('drivingLicenseNo','')"> <img src="/images/editIcon.png"> </div> 
		    <div> <input type="text"  id="txtBasicInfo_drivingLicenseNo"  class="basicInfoUpdateTxt" value="${personInfo.drivingLicenseNo?if_exists}"></div> 
		    <div class="basicInfoUpdateBtn" id="btnBasicInfo_drivingLicenseNo">
		    	<a class="buttontext" onclick="sendBasicInfoUpdateRequisition('drivingLicenseNo','PartyAndPerson','')">Update Requisition</a>
		    </div> 
	     </td>	
    </tr>
    
    <tr>
	    <td class="label">
			<span id="basicInfo_name_title">Email</span>    </td>
	    <td>
		   <div class="basicInfoUpdateLabel" id="lblBasicInfo_EMAIL_ADDRESSinfoString"> ${emaillist[0].infoString?if_exists} </div> 
		    <div class="basicInfoUpdateEditIcon" id="editIconBasicInfo_EMAIL_ADDRESSinfoString" onclick="editFieldOpen('infoString','EMAIL_ADDRESS')"> <img src="/images/editIcon.png"> </div> 
		    <div> <input type="text"  id="txtBasicInfo_EMAIL_ADDRESSinfoString"  class="basicInfoUpdateTxt" value="${emaillist[0].infoString?if_exists}" /></div> 
		    <div class="basicInfoUpdateBtn" id="btnBasicInfo_EMAIL_ADDRESSinfoString">
		    	<a class="buttontext" onclick="sendBasicInfoUpdateRequisition('infoString','PartyAndContactMech','EMAIL_ADDRESS')">Update Requisition</a>
		    </div> 
	     </td>	
    </tr>
    
    <tr>
	    <td class="label">
			<span id="basicInfo_name_title">Mobile No</span>    </td>
	    <td>
		   <div class="basicInfoUpdateLabel" id="lblBasicInfo_TELECOM_NUMBERcontactNumber"> ${telecomnumberlist[0].contactNumber?if_exists} </div> 
		    <div class="basicInfoUpdateEditIcon" id="editIconBasicInfo_TELECOM_NUMBERcontactNumber" onclick="editFieldOpen('contactNumber','TELECOM_NUMBER')"> <img src="/images/editIcon.png"> </div> 
		    <div> <input type="text"  id="txtBasicInfo_TELECOM_NUMBERcontactNumber"  class="basicInfoUpdateTxt" value="${telecomnumberlist[0].contactNumber?if_exists}"></div> 
		    <div class="basicInfoUpdateBtn" id="btnBasicInfo_TELECOM_NUMBERcontactNumber">
		    	<a class="buttontext" onclick="sendBasicInfoUpdateRequisition('contactNumber','PartyAndTelecomNumber','TELECOM_NUMBER')">Update Requisition</a>
		    </div> 
	     </td>	
    </tr>
    
    
    
    
    <tr>
	    <td class="group-label">
			<span id="basicInfo_MAILING_ADDRESS_title">Mailing Address</span>    
		</td>
	    <td></td>
    </tr>
    
    <tr>
	    <td class="label">
			<span id="basicInfo_name_title">Address</span>    </td>
	    <td>
		   <div class="basicInfoUpdateLabel" id="lblBasicInfo_MAILING_ADDRESSaddress1"> ${mailingAddress[0].address1?if_exists} </div> 
		    <div class="basicInfoUpdateEditIcon" id="editIconBasicInfo_MAILING_ADDRESSaddress1" onclick="editFieldOpen('address1','MAILING_ADDRESS')"> <img src="/images/editIcon.png"> </div> 
		    <div> <input type="text"  id="txtBasicInfo_MAILING_ADDRESSaddress1"  class="basicInfoUpdateTxt" value="${mailingAddress[0].address1?if_exists}"></div> 
		    <div class="basicInfoUpdateBtn" id="btnBasicInfo_MAILING_ADDRESSaddress1">
		    	<a class="buttontext" onclick="sendBasicInfoUpdateRequisition('address1','PartyAndPostalAddress','MAILING_ADDRESS')">Update Requisition</a>
		    </div> 
	     </td>	
    </tr>
    <tr>
	    <td class="label">
			<span id="basicInfo_name_title">City</span>    </td>
	    <td>
		   <div class="basicInfoUpdateLabel" id="lblBasicInfo_MAILING_ADDRESScity"> ${mailingAddress[0].city?if_exists} </div> 
		    <div class="basicInfoUpdateEditIcon" id="editIconBasicInfo_MAILING_ADDRESScity" onclick="editFieldOpen('city','MAILING_ADDRESS')"> <img src="/images/editIcon.png"> </div> 
		    <div> <input type="text"  id="txtBasicInfo_MAILING_ADDRESScity"  class="basicInfoUpdateTxt" value="${mailingAddress[0].city?if_exists}"></div> 
		    <div class="basicInfoUpdateBtn" id="btnBasicInfo_MAILING_ADDRESScity">
		    	<a class="buttontext" onclick="sendBasicInfoUpdateRequisition('city','PartyAndPostalAddress','MAILING_ADDRESS')">Update Requisition</a>
		    </div> 
	     </td>	
    </tr>
    
    
    
    
    
    
    
    
    <tr>
	    <td class="group-label">
			<span id="basicInfo_MAILING_ADDRESS_title">Parmanent Address</span>    
		</td>
	    <td></td>
    </tr>
    
    <tr>
	    <td class="label">
			<span id="basicInfo_name_title">Address</span>    </td>
	    <td>
		   <div class="basicInfoUpdateLabel" id="lblBasicInfo_PERMANENT_ADDRESSaddress1"> ${permanentAddress[0].address1?if_exists} </div> 
		    <div class="basicInfoUpdateEditIcon" id="editIconBasicInfo_PERMANENT_ADDRESSaddress1" onclick="editFieldOpen('address1','PERMANENT_ADDRESS')"> <img src="/images/editIcon.png"> </div> 
		    <div> <input type="text"  id="txtBasicInfo_PERMANENT_ADDRESSaddress1"  class="basicInfoUpdateTxt" value="${permanentAddress[0].address1?if_exists}"></div> 
		    <div class="basicInfoUpdateBtn" id="btnBasicInfo_PERMANENT_ADDRESSaddress1">
		    	<a class="buttontext" onclick="sendBasicInfoUpdateRequisition('address1','PartyAndPostalAddress','PERMANENT_ADDRESS')">Update Requisition</a>
		    </div> 
	     </td>	
    </tr>
    <tr>
	    <td class="label">
			<span id="basicInfo_name_title">City</span>    </td>
	    <td>
		   <div class="basicInfoUpdateLabel" id="lblBasicInfo_PERMANENT_ADDRESScity"> ${permanentAddress[0].city?if_exists} </div> 
		    <div class="basicInfoUpdateEditIcon" id="editIconBasicInfo_PERMANENT_ADDRESScity" onclick="editFieldOpen('city','PERMANENT_ADDRESS')"> <img src="/images/editIcon.png"> </div> 
		    <div> <input type="text"  id="txtBasicInfo_PERMANENT_ADDRESScity"  class="basicInfoUpdateTxt" value="${permanentAddress[0].city?if_exists}"></div> 
		    <div class="basicInfoUpdateBtn" id="btnBasicInfo_PERMANENT_ADDRESScity">
		    	<a class="buttontext" onclick="sendBasicInfoUpdateRequisition('city','PartyAndPostalAddress','PERMANENT_ADDRESS')">Update Requisition</a>
		    </div> 
	     </td>	
    </tr>
 
 </table>
</form>
<div id="divCustomFieldUpdateCover">

</div>
<style>
	.basicInfoUpdateLabel{
		float:left;
	}
	.basicInfoUpdateEditIcon{
		float:left;
	}
	.basicInfoUpdateEditIcon img{
		height:10px;
		width:10px;
		margin-left:10px;cursor:pointer;
	}
	.basicInfoUpdateTxt{
		float:left;display:none;
	}
	.basicInfoUpdateBtn{
		float:left;display:none;margin-left:10px;cursor:pointer;
	}
</style>

<script>
   $(document).ready(function () {
	   var maritialStatus=$("#lblBasicInfo_maritalStatus").html();
	   if(maritialStatus=="M"){
	   		$("#lblBasicInfo_maritalStatus").html('Married');
	   }
	   else if(maritialStatus=="S"){
	   		$("#lblBasicInfo_maritalStatus").html('Single');
	   }
	   else if(maritialStatus=="D"){
	   		$("#lblBasicInfo_maritalStatus").html('Divorced');
	   }
	   else if(maritialStatus=="P"){
	   		$("#lblBasicInfo_maritalStatus").html('Separated');
	   }
	   else if(maritialStatus=="W"){
	   		$("#lblBasicInfo_maritalStatus").html('Widowed');
	   }
   
   
   
	  	var partyId = $("#basicInfo_partyId").val();
    	var dataSet = {"partyId": partyId};
    	var url = '/humanres/control/findProfileCustomField/';
        $.ajax({
	        url: url,
	        async: false,
	        type: 'POST',
	        data: dataSet,
            beforeSend: function () {
            },
            success: function (data) {
	  		var htmlContent=''
	            $.each(data.customFieldList,function(i,j){	            
	            	if(htmlContent==""){
		            	htmlContent='<tr><td class="group-label"><span id="basicInfo_AdditionalInformation_title">Additional information</span></td></tr>'
		            }
					var customFieldId=data.customFieldList[i].customFieldId;
					var partyId=data.customFieldList[i].partyId;
					var fieldName=data.customFieldList[i].fieldName;
					var repFieldName=fieldName.replace(" ","");
					var value=data.customFieldList[i].fieldValue;	
					if (typeof(value) == "undefined"){
						value='';
					}
			  		htmlContent=htmlContent+'<tr><td class="label"><span >'+fieldName+'</span></td><td>';
			  		htmlContent=htmlContent+ '<div class="basicInfoUpdateLabel" id="lblBasicInfo_'+customFieldId+repFieldName+'">'+value+'</div>';	
			  		var editFieldOpenValue="editFieldOpen('"+fieldName+"','"+customFieldId+"')";
			  		htmlContent=htmlContent+ '<div class="basicInfoUpdateEditIcon" id="editIconBasicInfo_'+customFieldId+repFieldName+'"  onclick="'+editFieldOpenValue+'"><img src="/images/editIcon.png"></div>';
			  		htmlContent=htmlContent+ '<div> <input type="text"  id="txtBasicInfo_'+customFieldId+repFieldName+'"  class="basicInfoUpdateTxt" value="'+value+'"></div>';
			  		var sendBasicInfoUpdateRequisitionValue="sendBasicInfoUpdateRequisition('"+fieldName+"','CustomField','"+customFieldId+"')";
			  		htmlContent=htmlContent+ '<div class="basicInfoUpdateBtn" id="btnBasicInfo_'+customFieldId+repFieldName+'"> <a class="buttontext" onclick="'+sendBasicInfoUpdateRequisitionValue+'">Update Requisition</a></div>';
			  		htmlContent=htmlContent+ '</td></tr>'	
				});
			  	$("#basicInfo table").append(htmlContent);
            }
        });
    });
	    
	function sendBasicInfoUpdateRequisition(fieldName,tableName,contactMechTypeId){
		
		var repFieldName=fieldName.replace(" ","");
		var partyId=$("#basicInfo_partyId").val();
		var fieldCurrentValue=$("#lblBasicInfo_"+contactMechTypeId+repFieldName).html();
		var fieldUpdatedValue=$("#txtBasicInfo_"+contactMechTypeId+repFieldName).val();
	    var dataSet = {"partyId": partyId,"fieldName": fieldName,"contactMechTypeId": contactMechTypeId,"tableName": tableName,"fieldCurrentValue": fieldCurrentValue,"fieldUpdatedValue": fieldUpdatedValue};
		var url = '/humanres/control/updateEmployeeBasicInfo/';
	        $.ajax({
		        url: url,
		        async: false,
		        type: 'POST',
		        data: dataSet,
	            beforeSend: function () {
	            },
	            success: function (data) {
	            		alert("Requiest Send");
	            }
	        });
		$("#lblBasicInfo_"+contactMechTypeId+repFieldName).css("display","block");
		$("#editIconBasicInfo_"+contactMechTypeId+repFieldName).css("display","block");
		$("#txtBasicInfo_"+contactMechTypeId+repFieldName).css("display","none");
		$("#btnBasicInfo_"+contactMechTypeId+repFieldName).css("display","none");
	}
	function editFieldOpen(fieldName,contactMechTypeId){
		fieldName=fieldName.replace(" ","");
		var partyId=$("#basicInfo_partyId").val();
		$("#lblBasicInfo_"+contactMechTypeId+fieldName).css("display","none");
		$("#editIconBasicInfo_"+contactMechTypeId+fieldName).css("display","none");
		$("#txtBasicInfo_"+contactMechTypeId+fieldName).css("display","block");
		$("#btnBasicInfo_"+contactMechTypeId+fieldName).css("display","block");
	}
</script>
