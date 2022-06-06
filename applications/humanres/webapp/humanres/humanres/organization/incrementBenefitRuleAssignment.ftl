<#assign extInfo = parameters.extInfo?default("N")>
<#assign extInfo1 = parameters.extInfo1?default("B")>

<div id="incrementBenefitRuleAssignment" class="screenlet">
    <div class="screenlet-title-bar">
        <ul>
            <li class="h3">Increment Benefit Rule Assignment</li>
            
        </ul>
        <br class="clear"/>
    </div>
    <#if parameters.hideFields?default("N") != "Y">
    <div class="screenlet-body">
     
        <form method="post" id="myform"  name="LookupUserLoginAndPartyDetails"  action="<@ofbizUrl>incrementBenefitRuleAssignment</@ofbizUrl>" class="basic-form">
            <input type="hidden" name="lookupFlag" value="Y"/>
            <input type="hidden" name="hideFields" value="Y"/>
            <input type="hidden" name="defaultOrganizationPartyId" value="${defaultOrganizationPartyId?if_exists}" />
            <input type="hidden" name="emplPositionId" value="${emplPositionId?if_exists}" />
             <input type="hidden" name="payGradeId" value="${payGradeId?if_exists}" />
             <input type="hidden" name="salaryStepSeqId" value="${parameters.salaryStepSeqId?if_exists}" />
            <table cellspacing="0">
            	 	
              
               
                	<tr><td class="label">Payroll Item Id</td>
                    	<td><@htmlTemplate.lookupField value='${parameters.payrollItemId?if_exists}' formName="LookupUserLoginAndPartyDetails" name="payrollItemId" id="payrollItemId" fieldFormName="LookupPayrollItemAssignmentIdBenefit"/>
                   </td>
             		</tr>
             		<td></td>
             		
            	<tr><td class="label">Rule Id</td>
              			<td><@htmlTemplate.lookupField value='${parameters.ruleId?if_exists}' formName="LookupUserLoginAndPartyDetails" name="ruleId" id="ruleId" fieldFormName="LookupPayrollItemRule"/></td>
                   
                </tr>
                <tr></tr>
               <tr></tr>
                <tr></tr>
            <td></td>
            
       
                 <tr>
               
                    <td>&nbsp;</td>
                  
                     <td>
                        <input type="submit" value="Submit" id="textInput"/>
                    </td>
                    
                </tr>
            </table>
        </form>
    </div>
    </#if>
</div>
   
   
    <script>
    $('#myform').submit(function(){
	   var input = $('#textInput').val(); // Get the value here, do whatever you want to it
	   var name = $(this).attr('name'); // Get the name here, do whatever you want to it. 
	   $(this).attr('action', "<@ofbizUrl>incrementBenefitRuleAssignment</@ofbizUrl>"); // Set the action here. 
	}); 
    </script>

