<div id="partyContent" class="screenlet">
    <div class="screenlet-title-bar">
      <ul>
        <li class="h3">${uiLabelMap.ApplicantContent}</li>
      </ul>
      <br class="clear" />
    </div>
     
    <div class="screenlet-body">
          
      <form id="uploadPartyContent" method="post" enctype="multipart/form-data" action="<@ofbizUrl>uploadContessaApplicantContent?partyId=${parameters.partyId}</@ofbizUrl>">
        <input type="hidden" name="dataCategoryId" value="PERSONAL"/>
        <input type="hidden" name="contentTypeId" value="DOCUMENT"/>
        <input type="hidden" name="statusId" value="CTNT_PUBLISHED"/>
        <input type="hidden" name="partyId" value="${partyId}" id="contentPartyId"/>
        <input type="file" name="uploadedFile" class="required error" size="25" required="required"/>
        <div>
        <select name="partyContentTypeId" class="required error" required="required">
          <option value="">${uiLabelMap.PartySelectPurpose}</option>
          <#list partyContentTypes as partyContentType>
            <option value="${partyContentType.partyContentTypeId}">${partyContentType.get("description", locale)?default(partyContentType.partyContentTypeId)}</option>
          </#list>
        </select>
        </div>
        <input type="hidden" name="isPublic" value="N" />
     
          <input type="hidden" name="roleTypeId" value="CONTENT_ADMIN" />
      
        <input type="submit" value="${uiLabelMap.CommonUpload}" />
      </form>
     <hr />
      
    </div>
    	  	
    	${screens.render("component://humanres/widget/recruitment/ContessaRecruitmentScreens.xml#ApplicantContentList")}
      <hr />
  </div>

  <script type="text/javascript">
    jQuery("#uploadPartyContent").validate({
        submitHandler: function(form) {
            <#-- call upload scripts - functions defined in PartyProfileContent.js -->
            uploadPartyContent();
            getUploadProgressStatus();
            form.submit();
        }
    });
  </script>
