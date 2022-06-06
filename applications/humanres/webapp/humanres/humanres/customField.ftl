<div id="customFieldFullCover">

	<div id="btnCustomFieldAddCover">
		<div class="smallSubmit" onclick="addCustomField()">Add New Field</div>
	</div>
	<table class="viewDataTbl" id="tblCustomField">
		<tr class="tblHeaderRow">
			<td class="tblHeader">Field Name</td>
			<td class="tblHeader">Value</td>
			<td class="tblHeader">Action</td>
		</tr>
	</table>
	<input type="hidden" id="customFieldNumber" value="0"/>	
	<input type="hidden" id="partyId" value="${parameters.partyId}"/>	
	<div id="CustomFieldListFormCover"></div>
</div>

  <script type="text/javascript">
   $(document).ready(function () {
	  	var partyId = $("#partyId").val();
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
	  		var customFieldNumber=1;
	            $.each(data.customFieldList,function(i,j){
	            
					var customFieldId=data.customFieldList[i].customFieldId;
					var partyId=data.customFieldList[i].partyId;
					var fieldName=data.customFieldList[i].fieldName;
					var value=data.customFieldList[i].fieldValue;
					
					
			  		var tr='';
			  		tr=tr + '<tr class="tblRow" id="CFRow-'+customFieldNumber+'">'; 
			  		tr=tr + '<td><input type="hidden" id="customFieldId-'+customFieldNumber+'"  value="'+customFieldId+'"><input type="text" id="FieldName-'+customFieldNumber+'" style="display:none;"  value="'+fieldName+'"><div id="FieldNameText-'+customFieldNumber+'" >'+fieldName+'</div></td>';
			  		tr=tr + '<td><input type="text" id="Value-'+customFieldNumber+'" style="display:none;"  value="'+value+'"><div id="ValueText-'+customFieldNumber+'" >'+value+'</div></td>';
			  		tr=tr + '<td><div id="btnEditModecustomField-'+customFieldNumber+'" class="smallSubmit" onclick="ProfileCustomFieldEditMode('+customFieldNumber+')" >Edit</div>';
			  		tr=tr + '<div id="btnSavecustomField-'+customFieldNumber+'" style="display:none;" class="smallSubmit" onclick="saveProfileCustomField('+customFieldNumber+')" >Update</div></td>';
			  		tr=tr+'</tr>'					
			  		$("#tblCustomField").append(tr);
			  		customFieldNumber=customFieldNumber+1;
			  		$("#customFieldNumber").val(customFieldNumber)
				});
				
				
            }
        });
    });
    
    function ProfileCustomFieldEditMode(rowId){
    	$("#btnEditModecustomField-"+rowId).css('display','none');
    	$("#btnSavecustomField-"+rowId).css('display','block');
    	$("#FieldName-"+rowId).css('display','block');
    	$("#Value-"+rowId).css('display','block');
    	$("#FieldNameText-"+rowId).css('display','none');
    	$("#ValueText-"+rowId).css('display','none');
    }
	  function addCustomField(){
	  		var customFieldNumber=parseFloat($("#customFieldNumber").val())+1;
	  		var tr='';
	  		tr=tr + '<tr class="tblRow" id="CFRow-'+customFieldNumber+'">'; 
	  		tr=tr + '';
	  		tr=tr + '<td><input type="hidden" id="customFieldId-'+customFieldNumber+'" value=""><input type="text" id="FieldName-'+customFieldNumber+'"></td>';
	  		tr=tr + '<td><input type="text" id="Value-'+customFieldNumber+'"></td>';
	  		tr=tr + '<td><div id="btnSave-'+customFieldNumber+'" class="smallSubmit" onclick="saveProfileCustomField('+customFieldNumber+')" >Save</div></td>';
	  		tr=tr + '</tr>'			
	  		var tr='';
	  		tr=tr + '<tr class="tblRow" id="CFRow-'+customFieldNumber+'">'; 
	  		tr=tr + '<td><input type="hidden" id="customFieldId-'+customFieldNumber+'"  value=""><input type="text" id="FieldName-'+customFieldNumber+'" value=""><div id="FieldNameText-'+customFieldNumber+'"  style="display:none;" ></div></td>';
	  		tr=tr + '<td><input type="text" id="Value-'+customFieldNumber+'" value=""><div id="ValueText-'+customFieldNumber+'"  style="display:none;" ></div></td>';
	  		tr=tr + '<td><div id="btnEditModecustomField-'+customFieldNumber+'" style="display:none;" class="smallSubmit" onclick="ProfileCustomFieldEditMode('+customFieldNumber+')" >Edit</div>';
	  		tr=tr + '<div id="btnSavecustomField-'+customFieldNumber+'" class="smallSubmit" onclick="saveProfileCustomField('+customFieldNumber+')" >Update</div></td>';
	  		tr=tr+'</tr>'		
	  		$("#tblCustomField").append(tr);
	  		$("#customFieldNumber").val(customFieldNumber)
	  }
	  function saveProfileCustomField(rowId){
	  	var fieldName = $("#FieldName-" + rowId).val();
	  	var value = $("#Value-" + rowId).val();
	  	var partyId = $("#partyId").val();
	  	var customFieldId=$("#customFieldId-"+rowId).val();
    	var url = '/humanres/control/saveProfileCustomField';
    	var dataSet = {"fieldName": fieldName, "fieldValue": value, "partyId": partyId, "customFieldId": customFieldId};
	    $.ajax({
	        url: url,
	        async: false,
	        type: 'POST',
	        data: dataSet,
	        beforeSend: function () {
	            $("#btnSave-'+rowId+'").html('Saving...');
	        },
	        success: function (response) {
	        	$("#customFieldId-"+rowId).val(response.customFieldId);
	            $("#btnSave-'+rowId+'").html('Update');
	            $("#FieldNameText-"+rowId).html($("#FieldName-"+rowId).val());
	            $("#ValueText-"+rowId).html($("#Value-"+rowId).val());
		    	$("#btnEditModecustomField-"+rowId).css('display','block');
		    	$("#btnSavecustomField-"+rowId).css('display','none');		    	
		    	$("#FieldName-"+rowId).css('display','none');
		    	$("#Value-"+rowId).css('display','none');
		    	$("#FieldNameText-"+rowId).css('display','block');
		    	$("#ValueText-"+rowId).css('display','block');
	        },
	        error: function (xhr, ajaxOption, thrownError) {
	        	alert('Error No: '+xhr.status);
	            if (xhr.status == 404) {
	                $("#GoodReceiveProcessLoading").html('Some went wrong');
	            }
	        }
	    });
	  }
  </script>
<style>
#customFieldFullCover {
	width:100%;
	padding:0px 20px;
	margin-bottom:50px;
}

#tblCustomField{
    box-shadow: 0 0 4px 2px rgba(0, 0, 0, 0.1);
}
.viewDataTbl {
    background-color: #ffffff;
    font-size: 11px;
    font-family: Verdana;
    width: 95%;
    border-collapse: collapse;
}

    .viewDataTbl td {
        font-size: 11px;
        height: 30px;
    }
﻿ .tblHeaderRow {
	background:#777;
	border:5px;
}
﻿ .tblHeaderRow td {
    font-family: Calibri;
    font-size: 16px;
    font-weight: bold;
	background:#f00;
	border:5px;
}
.tblHeader{
	background:#1c417c;
	color:#fff;
}
.tblRow{
	border:1px solid #ddd;

}
#btnCustomFieldAddCover{
	padding:5px;
	height:30px;
	padding-right:30px;
}
.smallSubmit{
	cursor:pointer;
	width:100px;
	text-align:center;
	
}
</style>