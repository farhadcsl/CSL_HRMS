<#assign contessaOrg = defaultOrganizationPartyId?if_exists>
<#assign positionOrg = employmentData.emplPosition.partyId?if_exists>

<div id="incrementBenefitRuleAssignment" class="screenlet">
    <!--<div class="screenlet-title-bar">
        <ul>
            <li class="h3">Current Employment Information</li>
        </ul>
        <br class="clear"/>
    </div>-->
    <#if parameters.hideFields?default("N") != "Y">
  <!--   <div class="screenlet-body"> -->
     	<form method="post" action='<@ofbizUrl>changeJoingDate?partyId=${partyIdTo}</@ofbizUrl>' name="changeJoingDate">
	     	<table>
	     				<tr><!--<td class="label">Company Name</td>-->
	     				<td colspan="2">
	                    	<!--<td>${groupName?if_exists} [${partyIdFrom?if_exists}]</td>-->
	                    	<ol id="reverse_all" class="breadcrumb flat">
	                    	
	                    	<#list breadcrumbList as breadcrumb>
	                    	
	                    	<li>
	                    	<form id="breadcrumb${breadcrumb.partyId}" class="sort-order-asc" name="breadcrumb${breadcrumb.partyId}" method="post" enctype="multipart/form-data" action="<@ofbizUrl>setUserPreference</@ofbizUrl>">
				
								<input type="hidden" name="userPrefTypeId" value="ORGANIZATION_PARTY" />
								<input type="hidden" name="userPrefGroupTypeId" value="GLOBAL_PREFERENCES" />
								<input type="hidden" name="userPrefValue" value="${breadcrumb.partyId}" />
								<a href="javascript:void()" onclick="submitHandler${breadcrumb.partyId}()">${breadcrumb.groupName?if_exists}</a>
					
							</form>
							 <script type="text/javascript">
								
										function submitHandler${breadcrumb.partyId}() {
											$("#breadcrumb${breadcrumb.partyId}").submit();
										}
										
			  					</script>
							
								</li>	
								</#list>
								
								
									<#list unitbreadcrumbList as breadcrumbunit>
									<!-- li is under the loop of list. Form id is unique for javaScript ID --> 
									<li>
									 	<form id="breadcrumbunit${breadcrumbunit.partyId}" class="sort-order-asc" name="breadcrumbunit${breadcrumbunit.partyId}" method="post" enctype="multipart/form-data" action="<@ofbizUrl>setUserPreference</@ofbizUrl>">
										
											<input type="hidden" name="userPrefTypeId" value="ORGANIZATION_UNIT" />
											<input type="hidden" name="userPrefGroupTypeId" value="GLOBAL_PREFERENCES" />
											<input type="hidden" name="userPrefValue" value="${breadcrumbunit.partyId}" />
											<a href="javascript:void()" onclick="submitHandler${breadcrumbunit.partyId}()">${breadcrumbunit.groupName}</a>
											
										</form>
									
									  <script type="text/javascript">
									
											function submitHandler${breadcrumbunit.partyId}() {
												$("#breadcrumbunit${breadcrumbunit.partyId}").submit();
											}
											
									  </script>
									  
									</li>
								</#list>
								
								
						</ol>
						</td>
	         			</tr>
	         			<#assign extInfo = employmentData.emplPosition?default("notExist")>
	             <#if contessaOrg == positionOrg>
	             		<tr><td class="label">Designation</td>
	             		<td>${employmentData.emplPositionType.description?if_exists} [${employmentData.emplPosition.emplPositionId?if_exists}]
	             		<input type="hidden" id="emplPositionIdForTree" value="${employmentData.emplPosition.emplPositionId?if_exists}" />
	             		<input type="hidden" id="employeeCurrentPositionInfo" value="${employmentData.emplPositionType.description?if_exists} [${employmentData.emplPosition.emplPositionId?if_exists}]" />
	              			</td>
	             		<#if extInfo=="notExist">
	             		<td><a href="<@ofbizUrl>PorttayPositionView?emplPositionId=${employmentData.emplPosition.emplPositionId?if_exists}</@ofbizUrl>">${employmentData.emplPositionType.description?if_exists} [${employmentData.emplPosition.emplPositionId?if_exists}]</a>
	              			</td>
	              			</#if>
	                	</tr>
	              <#else>
	              		<tr><td class="label">Designation</td>
	             		<td  >${employmentData.emplPositionType.description?if_exists} [${employmentData.emplPosition.emplPositionId?if_exists}]
	             		<input type="hidden" id="emplPositionIdForTree" value="${employmentData.emplPosition.emplPositionId?if_exists}" />
	             		<input type="hidden" id="employeeCurrentPositionInfo" value="${employmentData.emplPositionType.description?if_exists} [${employmentData.emplPosition.emplPositionId?if_exists}]" />
	              			</td>
	                	</tr>
	              </#if> 	
	                	
						<tr><td class="label">Employment Type</td>
	              			<td>${employmentType?if_exists}</td>
	              			
	              			
	                	</tr>
	                	<tr><td class="label">Joining Date</td>
	              			
	              			<td>${dateofjoin?if_exists}
	              				<input type="hidden" id="EmployeeJoiningDate" value="${dateofjoin?if_exists}" />
	              			</td>

               				<td><input type="hidden" name="partyIdTo" value="${partyIdTo?if_exists}"/></td>
	                	</tr>
	                	<!-- <tr> <td class="label">Change Join Date</td>
	                		<td>
								 <@htmlTemplate.renderDateTimeField name="changeDate" event="" action="" dateType="Timestamp" value="" className="" alert="" title="Change Joinging Date" size="10" 
								 	maxlength="15" id="changeDate"  shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" 
								 	timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" 
								 	pmSelected="" compositeType="" formName=""/>
							
							<span id="requiredDateSpan"></span> <input type="submit" id="changeDateButton" value="${uiLabelMap.CommonUpdate}" class="buttontext"/></td>
	                	</tr> -->
	                	<!-- <tr><td class="label">Confirmation Date</td>
	              			
	              			<td>${confimDate?if_exists}</td>
	                	</tr> -->
	                	<tr><td class="label">Separation Date</td>
	              			
	              			<td>
	              				<#if thruDate?has_content>
	              					${terminationDate}
	              				<#else> Current Employee
	              				</#if>
	              			
	              			</td>
	                	</tr>
	                	
	                	<tr><td class="label">Status</td>
	              			<td>${statDescription?if_exists}</td>
	                	</tr>
	                	<!-- <tr><td class="label">Remarks</td>
	              			<td></td>
	                	</tr> -->
	                	
	                	<!-- <tr><td class="label">OT Allowence</td>
	              			<td>${oTAllowence?if_exists}</td>
	                	</tr> -->
	                	
	     	</table>
        </form>
    </div>
    </#if>
<!-- </div> -->

<!-- CSS Style Sheet for this breadcrumb, This CSS is here for development issue. this css will go to a css file. -->
<style>
	#reverse_all, #reverse_allChild {
		/*position: absolute;
		box-shadow: 0 0 15px 1px rgba(0, 0, 0, 0.35);*/
		overflow: hidden;
		border-radius: 5px;
		display: inline-block;
		border: 1px solid #aaa;
	}
	#reverse_all li, #reverse_allChild li {
		position: relative;
		float: left;
	}
	#reverse_all > li > form > a:after,#reverse_allChild > li > a:after {
		content: '';
		position: absolute;
		top: 0; 
		right: -9px;
		
		width: 18px; 
		height: 18px;
		-webkit-transform: scale(0.707) rotate(45deg);
		transform: scale(0.707) rotate(45deg);

		z-index: 1;

		box-shadow: 
			1px -1px 0 1px rgba(0, 0, 0, 0.4), 
			3px -3px 0 2px rgba(255, 255, 255, 0.1);
		border-radius: 0 5px 0 50px;
	}

.breadcrumb > li > form > a, .breadcrumbChild > li > a {
	text-decoration: none;
	outline: none;
	display: block;
	float: left;
	font-size: 8px;
	line-height:18px;
	color: white;
	padding: 0 7px 0 16px;
	background: #666;
	background: linear-gradient(#fff, #ccc);
	position: relative;
}

.breadcrumb > li > form > a:first-child, .breadcrumbChild > li > a:first-child {
	/*padding-left: 46px;*/
	border-radius: 5px 0 0 5px;
}

.breadcrumb > li > form > a.active, .breadcrumb > li > form > a:hover, .breadcrumbChild > li > a.active, .breadcrumbChild > li > a:hover{
	background: #333;
	background: linear-gradient(#333, #000);
}
.breadcrumb > li > form > a.active:after, .breadcrumb > li > form > a:hover:after, .breadcrumbChild > li > a.active:after, .breadcrumbChild > li > a:hover:after {
	background: #333;
	background: linear-gradient(135deg, #333, #000);
}

.flat > li > form > a, .flat > li > form > a:after, .flat > li > a, .flat > li > a:after {
	background: white;
	color: #111;
	transition: all 0.5s;
}
.flat > li > form > a:hover, .flat > li > form > a.active, .flat > li > form > a:hover:after, .flat > li > form > a.active:after{
	background: #1d407e;
}

.flat > li > a:hover, .flat > li > a.active, .flat > li > a:hover:after, .flat > li > a.active:after{
	background: #1d407e;
}
</style>

<script language="javascript" type="text/javascript">
	
	$(document).ready(function() {
		
	    $('#changeDateButton').click(function(e) {
	        
			changeDateValue = document.getElementById('changeDate_i18n').value;
			if(changeDateValue==''){
				$('#requiredDateSpan').html('required');
				$('#changeDate_i18n').focus();
				e.preventDefault();
			}
		});
	});
	
	
	
	$(document).ready(function() {
		
	    $('#changeEmploymentType').click(function(e) {
	        
			changeEmploymentValue = document.getElementById('employmentType').value;
			if(changeEmploymentValue==''){
				$('#employmentType').focus();
				e.preventDefault();
			}
		});
	});
</script>
