<form method="post" name="lookupparty" action="<@ofbizUrl>findEmployees</@ofbizUrl>" class="basic-form">
    <input type="hidden" name="lookupFlag" value="Y"/>
    <input type="hidden" name="hideFields" value="Y"/>
    <table cellspacing="0">
      <tr>
        </tr>
        <tr><td class="label">Employee ID</td>
           <!--  <td><input type="text" name="userLoginId" value="${parameters.userLoginId?if_exists}"/></td> -->
           <td>
            <@htmlTemplate.lookupField value='${requestParameters.userLoginId?if_exists}' formName="lookupparty" name="userLoginId" id="userLoginId" fieldFormName="LookupUserLoginAndPartyDetails"/>
           </td>
        </tr>                
        <tr>
        	<td class="label">Salary Template</td>
           <td>      
				<span class="ui-widget">             
					<select name="salaryTemplateId" id="EmployeeSalarySetupCreate_salaryTemplateId" size="1" class="selectTag" onchange="loadBasicSalaryStep()">
						<option>  </option>
					</select>
				</span>
           </td>
        </tr>                
        <tr><td class="label">Basic Salary Step</td>
           <td>      
				<span class="ui-widget">             
					<select name="basicSalaryStepId" id="EmployeeSalarySetupCreate_basicSalaryStepId" size="1" class="selectTag">
					 </select>
				</span>
           </td>
        </tr>
        <tr>
        
		    <td class="label"> </td>
		    <td colspan="4">
				<div class="divFormButton" id="submitBtn" onclick="saveEmployeeSalarySetup()">Create</div>				
		    </td>
    </td>
        <tr><td><input type="hidden" name="groupName" value="${parameters.groupName?if_exists}"/></td></tr>
        <tr><td><input type="hidden" name="roleTypeId" value="EMPLOYEE"/></td></tr>    
    </table>
</form>
<div id="divEmployeeSalarySetupBrackDownCover" ></div>
<script>
	    
    $(document).ready(function () {
    	var url = '/payroll/control/findSalaryTemplate';
    	var htmlContent='';
        $.ajax({
            url: url,
            cache: false,
            beforeSend: function () {
            },
            success: function (data) {
	            $.each(data.salaryTemplateList,function(i,j){
					htmlContent=htmlContent+'<option value="'+data.salaryTemplateList[i].salaryTemplateId+'" >'+data.salaryTemplateList[i].salaryTemplateName+'</option>';
				});
				$("#EmployeeSalarySetupCreate_salaryTemplateId").append(htmlContent);
            },
            error: function (xhr, ajaxOption, thrownError) {
               alert(xhr.status);
            }
        });
    });
	function loadBasicSalaryStep(){
    	var htmlContent='';
	  	var salaryTemplateId = $("#EmployeeSalarySetupCreate_salaryTemplateId").val();
    	var dataSet = {"salaryTemplateId": salaryTemplateId};
	    var url = '/payroll/control/findBasicSalaryStep';
        $.ajax({
            url: url,
	        async: false,
	        type: 'POST',
	        data: dataSet,
            beforeSend: function () {
            },
            success: function (data) {
	            $.each(data.basicSalaryStepList,function(i,j){
	            	htmlContent=htmlContent+'<option value="'+data.basicSalaryStepList[i].basicSalaryStepId+'" >'+data.basicSalaryStepList[i].basicSalaryStepName+'</option>';
				});
				$("#EmployeeSalarySetupCreate_basicSalaryStepId").html(htmlContent);
            },
            error: function (xhr, ajaxOption, thrownError) {
               alert('asdasdas');
            }
        });
	};
	
	function saveEmployeeSalarySetup(){
	var htmlContent='';
	  	var employeeId = $("#0_lookupId_userLoginId").val();
	  	var salaryTemplateId = $("#EmployeeSalarySetupCreate_salaryTemplateId").val();
	  	var basicSalaryStepId = $("#EmployeeSalarySetupCreate_basicSalaryStepId").val();
	  	if(employeeId==""){
	  		alert('Employee not selected');
	  	}
	  	else if(salaryTemplateId==""){
	  		alert('Salary Template not selected');
	  	}
	  	else if(basicSalaryStepId==""){
	  		alert('Basic Salary Step not Selected');
	  	}
	  	else{
	    	var dataSet = {"employeeId":employeeId ,"salaryTemplateId": salaryTemplateId,"basicSalaryStepId": basicSalaryStepId};
		    var url = 'saveEmployeeSalarySetup';
		    $.ajax({
	            url: url,
		        async: false,
		        type: 'POST',
		        data: dataSet,
	            beforeSend: function () {
	            },
	            success: function (result) {
	            	if(result._ERROR_MESSAGE_=="Already Has Salary Setup For This Employee"){			    	
		            	alert(result._ERROR_MESSAGE_);
		            	$("#EmployeeSalarySetupCreate_salaryTemplateId").val('');
		            	$("#EmployeeSalarySetupCreate_basicSalaryStepId").find('option').remove();
		            }
		            var partyId=result.partyId;
	            	var dataSet1 = {"employeeId":employeeId ,"partyId": partyId};
				    var url1 = 'ViewCurrentEmployeeSalarySetup';
		        	$.ajax({
			            url: url1,
				        async: false,
				        type: 'POST',
				        data: dataSet1,
			            beforeSend: function () {
			            },
			            success: function (result1) {
				            $("#divEmployeeSalarySetupBrackDownCover").html(result1);
			            },
			            error: function (xhr, ajaxOption, thrownError) {
			               alert(xhr.status);
			            }
		            });
	            },	            
	            error: function (xhr, ajaxOption, thrownError) {
	               alert(xhr.status);
	            }
	        });
		    
		}
	}
    

</script>