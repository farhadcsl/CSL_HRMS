<div id="divCustomFieldCover">

</div>


<script type="text/javascript">
 
   $(document).ready(function () {
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
					var value=data.customFieldList[i].fieldValue;					
					
			  		htmlContent=htmlContent+'<tr><td class="label">'
			  		htmlContent=htmlContent+ '<span >'+fieldName+'</span></td>'
			  		htmlContent=htmlContent+ '<td>'+value+'</td></tr>'	
				});
			  	$("#basicInfo table").append(htmlContent);
            }
        });
    });
    
    </script>