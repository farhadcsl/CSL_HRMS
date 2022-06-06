
<#if results?has_content>
<#assign one = results?replace('&#91;', '[')>
<#assign two = one?replace('&#123;', '{')>
<#assign three = two?replace('&#58;', ':')>
<#assign four = three?replace('&#125;', '}')>
<#assign five = four?replace('&quot;', '\"')>
<#assign six = five?replace('&#93;', ']')>
<#assign seven = six?replace('&#40;', '(')>
<#assign eight = seven?replace('&#35;', '#')>
<#assign nine = eight?replace('&amp;', '&')>
<#assign final = nine?replace('&#41;', ')')>

    <script type="text/javascript" src="/flatgrey/js/primitives.latest.js"></script>
    <#-- <script type="text/javascript" src="/flatgrey/js/primitives.min.js"></script> -->
    
    <link href="/flatgrey/css/primitives.latest.css" media="screen" rel="stylesheet" type="text/css" />
    <link rel="stylesheet" href="/flatgrey/css/print.css" type="text/css" media="print" />
    <script type='text/javascript'>//<![CDATA[ 
        $(window).load(function () {

            var options = new primitives.orgdiagram.Config();
            
			var items =${final?if_exists};
	
            options.items = items;
            options.cursorItem = 0;
            options.minimalVisibility = ${selectionMode?if_exists};
            options.orientationType = 2;
            options.horizontalAlignment= 1;
            options.hasSelectorCheckbox = primitives.common.Enabled.False;

            jQuery("#basicdiagram").orgDiagram(options);
        });
		        function printDiv(divName) {
		     var printContents = document.getElementById(divName).innerHTML;
		     var originalContents = document.body.innerHTML;
		     document.body.innerHTML = printContents;
		
		     window.print();
		
		     document.body.innerHTML = originalContents;
		}
        //]]>  
    

    </script>
    
    <form method="post" name="changeSelectionMode" action="<@ofbizUrl>orgChart</@ofbizUrl>" class="basic-form">
       
        <table cellspacing="0">
                <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
                <input type="hidden" name="partyId" value="${partyUnit?if_exists}"/>
                <tr><td class="label">Selection Mode</td>
                    <td>
                    <input type="radio" name="selectionMode" value="1" onclick="javascript:changeModeSubmit();" <#if selectionMode == "1">checked="checked"</#if>/>Normal&nbsp;
                    <input type="radio" name="selectionMode" value="2" onclick="javascript:changeModeSubmit();" <#if selectionMode == "2">checked="checked"</#if>/>Minimize&nbsp;
                  </td>
               </tr>               
         </table>  
                 
    </form>

 <input type="button" onclick="printDiv('basicdiagram')"  value="Print Org chart" /> 
 
             
<div id="basicdiagram" style="width: 100%; height: 720px;" />

       
    
    </#if>