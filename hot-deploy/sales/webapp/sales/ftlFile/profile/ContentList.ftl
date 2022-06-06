  <div id="partyContentList">
      <#if partyContent?has_content>
        <table class="basic-table" cellspacing="0">
          <#list partyContent as pContent>
          <#assign pcTypeInternal = pContent.getRelatedOne("PartyContentType").get("description", locale)>
            <#if "Internal Content" != pcTypeInternal>
            <#assign content = pContent.getRelatedOne("Content")>
            <#assign contentType = content.getRelatedOneCache("ContentType")>
            <#assign mimeType = content.getRelatedOneCache("MimeType")?if_exists>
            <#assign status = content.getRelatedOneCache("StatusItem")?if_exists>
            <#assign pcType = pContent.getRelatedOne("PartyContentType")>
            <tr>
             
              <!--<td class="button-col"><a href="<@ofbizUrl>EditPartyContents?contentId=${pContent.contentId}&amp;partyId=${pContent.partyId}&amp;partyContentTypeId=${pContent.partyContentTypeId}&amp;fromDate=${pContent.fromDate}</@ofbizUrl>">${content.contentId}</a></td>-->
              <td>${(pcType.get("description", locale))?if_exists}</td>
              <td>${content.contentName?if_exists}</td>
              <!--<td>${(contentType.get("description",locale))?if_exists}</td>-->
              <td>${(mimeType.description)?if_exists}</td>
              <!--<td>${(status.get("description",locale))?if_exists}</td>-->
              <td>${pContent.fromDate?if_exists}</td>
              <td class="button-col">
                <#if (content.contentName?has_content)>
                    <a target="_blank" href="<@ofbizUrl secure="${request.isSecure()?string}">img/${content.contentName}?imgId=${content.dataResourceId}</@ofbizUrl>">${uiLabelMap.CommonView}</a>
                </#if>
                <form name="removePartyContent_${pContent_index}" method="post" action="<@ofbizUrl>removeContessaContent</@ofbizUrl>">
                  <input type="hidden" name="contentId" value="${pContent.contentId}" />
                  <input type="hidden" name="partyId" value="${pContent.partyId}" />
                  <input type="hidden" name="partyContentTypeId" value="${pContent.partyContentTypeId}" />
                  <input type="hidden" name="fromDate" value="${pContent.fromDate}" />
                  <a href="javascript:document.removePartyContent_${pContent_index}.submit()">${uiLabelMap.CommonRemove}</a>
                </form>
              </td>
            </tr>
            
            </#if>
          </#list>
        </table>
      <#else>
        ${uiLabelMap.PartyNoContent}
      </#if>
  </div>