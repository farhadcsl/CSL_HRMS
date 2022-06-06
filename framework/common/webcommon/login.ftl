<#if requestAttributes.uiLabelMap?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#assign useMultitenant = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "multitenant")>

<#assign username = requestParameters.USERNAME?default((sessionAttributes.autoUserLogin.userLoginId)?default(""))>
<#if username != "">
  <#assign focusName = false>
<#else>
  <#assign focusName = true>
</#if>
<div id="Headersection" class="headersection" >
		<div id="contessa-logo" class="contessa-logo">
		<#if layoutSettings.contessaLogo?has_content>
				<#assign contessaLogo = layoutSettings.contessaLogo/>
	    		<img alt="${layoutSettings.contessaLogo}" src="<@ofbizContentUrl>${StringUtil.wrapString(contessaLogo)}</@ofbizContentUrl>">
	    	</#if>
		</div>
		<div id="contessa-title" class="contessa-title">
				Computer Services Limited
		</div> 

</div>
<center>
  <div class="screenlet login-screenlet">
    <div class="screenlet-title-bar">
    	<div class="contessa-header">
			<!-- <div class="contessa-logo">
				<#if layoutSettings.contessaLogo?has_content>
					<#assign contessaLogo = layoutSettings.contessaLogo/>
	    			<img alt="${layoutSettings.contessaLogo}" src="<@ofbizContentUrl>${StringUtil.wrapString(contessaLogo)}</@ofbizContentUrl>">
	    		</#if>
			</div>
		</div> -->
		<!--  <div id="help_btn">
			<a href="#" title="Login: Insert your login user name, Insert your password, Press enter to your keyboard or click to login button, Your dashboard will appear according to your access permission.

Recover lost password: Click on â€œForgot your passwordâ€�, Forgot your password page will appear,Insert user name, Click on â€œGet password hintâ€� to know password hints or Click on â€œEmail passwordâ€� (System will send mail to your email which already defined to your profile on the system)">Help</a>      		
		</div> 
      <div id ="auth" >Central Authentication</div> -->
    </div>
    
    <div class="screenlet-body">
    <div id="contessaLogin">
    	
      <form method="post" action="<@ofbizUrl>login</@ofbizUrl>" name="loginform">
        <table class="basic-table" cellspacing="0">
          <tr>
        <!--    <td class="label">${uiLabelMap.CommonUsername}</td> -->
            <td class="label">USER ID</td> <!-- EDITED BY FARHAD to change User Name to ID in login page -->
            <td><input type="text" name="USERNAME" value="${username}" size="20"/></td>
          </tr>
          <tr>
            <td class="label">${uiLabelMap.CommonPassword}</td>
            <td><input type="password" name="PASSWORD" value="" size="20"/></td>
          </tr>
          <#if ("Y" == useMultitenant) >
              <#if !requestAttributes.tenantId?exists>
                  <tr>
                      <td class="label">${uiLabelMap.CommonTenantId}</td>
                      <td><input type="text" name="tenantId" value="${parameters.tenantId?if_exists}" size="20"/></td>
                  </tr>
              <#else>
                  <input type="hidden" name="tenantId" value="${requestAttributes.tenantId?if_exists}"/>
              </#if>
          </#if>
          <tr>
            <td colspan="2" align="center">
              <input type="submit" value="${uiLabelMap.CommonLogin}"/>
            </td>
          </tr>
        </table>
        <input type="hidden" name="JavaScriptEnabled" value="N"/>
        <br />
        
      </form>
      </div>
    </div>
  </div>
  <div class="copyright">Copyright@2022 Computer Services Limited</div> 
</center>

<script language="JavaScript" type="text/javascript">
  document.loginform.JavaScriptEnabled.value = "Y";
  <#if focusName>
    document.loginform.USERNAME.focus();
  <#else>
    document.loginform.PASSWORD.focus();
  </#if>
</script>
