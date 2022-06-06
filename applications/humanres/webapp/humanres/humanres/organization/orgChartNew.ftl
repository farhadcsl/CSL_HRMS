
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
	
	
	<link rel="stylesheet" href="/flatgrey/ChartDesign/js/jquery/ui-lightness/jquery-ui-1.10.2.custom.css" />
	<script type="text/javascript" src="/flatgrey/ChartDesign/js/jquery/jquery-1.9.1.js"></script>
	<script type="text/javascript" src="/flatgrey/ChartDesign/js/jquery/jquery-ui-1.10.2.custom.min.js"></script>
	<script  type="text/javascript" src="/flatgrey/ChartDesign/js/primitives.min.js?2023"></script>
	<link href="/flatgrey/ChartDesign/css/primitives.latest.css?2023" media="screen" rel="stylesheet" type="text/css" />
	
	<script type='text/javascript'>//<![CDATA[ 
	$(window).load(function () {
	
	    var options = new primitives.orgdiagram.Config();            
		var items =${final?if_exists};
	    options.items = items;
	    options.cursorItem = 0;
	    options.hasSelectorCheckbox = primitives.common.Enabled.True;
	    jQuery("#basicdiagram").orgDiagram(options);
	});//]]>  
	function printDiv(divName) {
	     var printContents = document.getElementById(divName).innerHTML;
	     var originalContents = document.body.innerHTML;
	     document.body.innerHTML = printContents;
	
	     window.print();
	
	     document.body.innerHTML = originalContents;
	}
	function orgChartExpend(){
		$("#basicdiagramCover").css("width","3000px")
		$("#basicdiagramCover").css("height","1200px")
	}
	</script>
	
	<form method="post" name="changeSelectionMode" action="<@ofbizUrl>orgChartNew</@ofbizUrl>" class="basic-form">
	   
	    <table cellspacing="0">
	    <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
	    <input type="hidden" name="partyId" value="${partyUnit?if_exists}"/>
	       
	   <tr>
	     <td>	        
	 		<input type="button" onclick="orgChartExpend()"  value="Expend" /> 	        
	     </td>
	     <td>
		 	<input type="button" onclick="printDiv('basicdiagram')"  value="Print Org chart" /> 
		 </td>
	   </tr>                 
	 </table>  
	     
	</form>
	
	 <div id="basicdiagramCover">
	<div id="basicdiagram" style="" />
	</div>
	
</#if>