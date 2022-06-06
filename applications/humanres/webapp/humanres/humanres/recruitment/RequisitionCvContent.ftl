
  <div id="partyContent" class="screenlet">
    <div class="screenlet-title-bar">
      <ul>
        <li class="h3">Documents of Requisition</li>
      </ul>
      <br class="clear" />
    </div>
     
    <div class="screenlet-body">
          
      <div class="label">${uploadTitle}</div>
      <form id="uploadPartyContent" method="post" enctype="multipart/form-data" action="<@ofbizUrl>uploadtotalCvRequisitionContent?cvBankId=${cvBankId}</@ofbizUrl>">
        <input type="hidden" name="dataCategoryId" value="PERSONAL"/>
        <input type="hidden" name="contentTypeId" value="DOCUMENT"/>
        <input type="hidden" name="statusId" value="CTNT_PUBLISHED"/>
         <input type="hidden" name="partyId" value="CSL" id="contentPartyId"/>
      	<input type="hidden" name="partyContentTypeId" value="${partyContentTypeId}"/>
        
        <input type="file" name="uploadedFile" class="required error" size="25" required="required"/>
         <input type="hidden" name="cvBankId" value="${cvBankId?if_exists}"/>
     
    	  <!-- <input type="hidden" name="partyContentTypeId" value="${partyContentTypeId}"/> 
  
       <div class="label">${uiLabelMap.PartyIsPublic}</div> -->
        <input type="hidden" name="isPublic" value="N" />
      <!--  <select name="isPublic">
            <option value="N">${uiLabelMap.CommonNo}</option>
            <option value="Y">${uiLabelMap.CommonYes}</option>
        </select> -->
          <input type="hidden" name="roleTypeId" value="CONTENT_ADMIN" />
      
        <input type="submit" value="${uiLabelMap.CommonUpload}" />
      </form>
      <!--<div id='progress_bar'><div></div></div>-->
       <hr />
      
    </div>
  </div>

  <script type="text/javascript">
    jQuery("#uploadPartyContent").validate({
        submitHandler: function(form) {
            <#-- call upload scripts - functions defined in PartyProfileContent.js    uploadPartyContent();
            getUploadProgressStatus();-->
         
            form.submit();
        }
    });
  </script>
