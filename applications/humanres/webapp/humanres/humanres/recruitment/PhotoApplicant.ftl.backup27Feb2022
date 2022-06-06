 <div id="partyContent" class="screenlet">
    <div class="screenlet-title-bar"><ul><li class="h3">Profile Picture</li></ul><br class="clear"></div>
    <!-- x is used for only one photo check as a counter-->
    
     <#list partyContent as pContent>
     
     	<#assign contentId = pContent.get("contentId")>
     
        <#assign content = pContent.getRelatedOne("Content")>
        <#assign contentType = content.getRelatedOneCache("ContentType")>
        <#assign mimeType = content.getRelatedOneCache("MimeType")?if_exists>
        <#assign status = content.getRelatedOneCache("StatusItem")?if_exists>
        <#assign pcType = pContent.getRelatedOne("PartyContentType")>

        <#if !pContent_has_next && pcType.partyContentTypeId=="INTERNAL">
			<div id='profile-photo'><img height="200px" width="175px" src="<@ofbizUrl secure="${request.isSecure()?string}">img/${content.contentName}?imgId=${content.dataResourceId}</@ofbizUrl>"/></div>
		</#if>
		
	</#list>
	<div class="ContentPhoto">
	 <#if security.hasPermission("PARTYMGR_ADMIN", session)>
      <form id="uploadPartyContent" method="post" enctype="multipart/form-data" action="<@ofbizUrl>uploadPartyContent</@ofbizUrl>">
        <input type="hidden" name="dataCategoryId" value="PERSONAL"/>
        <input type="hidden" name="contentTypeId" value="DOCUMENT"/>
        <input type="hidden" name="statusId" value="CTNT_PUBLISHED"/>
        <input type="hidden" name="partyId" value="${partyId}" id="contentPartyId"/>
        <input type="file" name="uploadedFile" class="required error" size="15" required="required"/>
        <br/>
		<select name="partyContentTypeId" class="required error" style="display:none;">
            <option selected value="INTERNAL">Internal Content</option>
            <option value="USERDEF">User Defined Content</option>
        </select>
        <select name="isPublic" class="valid" style="display:none;">
            <option value="N">No</option>
            <option selected value="Y">Yes</option>
        </select>
        <select name="roleTypeId" class="valid" style="display:none;">
          <option selected value="EMPLOYEE">Employee</option>
        </select>
        <input type="submit" value="${uiLabelMap.CommonUpload}" />
      </form>
      </div>
      <#else>
      		<hr/>
      		
      </#if>
      
      <!--<div id='progress_bar'><div></div>-->
		
  </div>

  <script type="text/javascript">
    jQuery("#uploadPartyContent").validate({
        submitHandler: function(form) {
            <#-- call upload scripts - functions defined in PartyProfileContent.js -->
            uploadPartyContent();
            getUploadProgressStatus();
            form.submit();
            var url = window.location.href;    
			if (url.indexOf('?') > -1){
			   url += '&param=1'
			}else{
			   url += '?param=1'
			}
			window.location.href = url;
        }
    });
  </script>
