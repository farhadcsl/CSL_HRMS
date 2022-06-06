

<script type="text/javascript">
<#-- some labels are not unescaped in the JSON object so we have to do this manualy -->
function unescapeHtmlText(text) {
    return jQuery('<div />').html(text).text()
}
 
jQuery(window).load(createTree());
<#-- creating the JSON Data -->
var rawdata = [
        <#if (completedTree?has_content)>
            <@fillTree rootCat = completedTree/>
        </#if>
        
        <#macro fillTree rootCat>
            <#if (rootCat?has_content)>
                <#list rootCat as root>
                    {
                    "data": {"title" : unescapeHtmlText("<#if root.groupName?exists>${root.groupName?js_string} [${root.partyId}]<#else>${root.partyId?js_string}</#if>"), "attr": {"href" : "<@ofbizUrl>/viewprofile?partyId=${root.partyId}</@ofbizUrl>","onClick" : "callDocument('${root.partyId}');"}},
                    "attr": {"id" : "${root.partyId}", "rel" : "Y"}
                    <#if root.child?exists>
                    ,"state" : "closed"
                    </#if>
                    <#if root_has_next>
                        },
                    <#else>
                        }
                    </#if>
                </#list>
            </#if>
        </#macro>
     ];

 <#-- create Tree-->
  function createTree() {
    jQuery(function () {
			var emplId=$("#PersonalInformation_partyId").val();
    		var businessUnitList="";
			$('#reverse_all li input[name=userPrefValue]').each(function(i)
			{
				businessUnitList=businessUnitList+"BU-"+ $(this).val()+">";			
			});
			var positionIda=$("#emplPositionIdForTree").val();
			businessUnitList+=positionIda;
        $.cookie('jstree_select', null);
        $.cookie('jstree_open', null);
        jQuery("#tree").jstree({
        "core" : { "initially_open" : [ "${partyId}" ] },
        "plugins" : [ "themes", "json_data","ui" ,"cookies", "types", "crrm", "contextmenu"],
            "json_data" : {
                "data" : rawdata,
                          "ajax" : { "url" : "<@ofbizUrl>getHRChild</@ofbizUrl>", "type" : "POST",
                          "data" : function (n) {
                          var partyId=n.attr ? n.attr("id").replace("node_","") : 1 ;
                          var unitType=$("li #"+partyId).attr("rel");
                            return { 
                                "partyId" : n.attr ? n.attr("id").replace("node_","") : 1 ,
                                "additionParam" : "','category" ,
                                "hrefString" : "EmployeeProfile?partyId=" ,
                                "unitType" : unitType ,
                                "onclickFunction" : "callDocument"
                        }; 
                    }
                }
            },
            "types" : {
             "valid_children" : [ "root" ],
             "types" : {
                 "CATEGORY" : {
                     "icon" : { 
                         "image" : "/images/jquery/plugins/jsTree/themes/apple/d.png",
                         "position" : "10px40px"
                     }
                 }
             }
            },
            "contextmenu": {items: customMenu}
        });
        
		$("#tree").bind("open_node.jstree", function (e, data) 
		{
			var businessUnitArray=businessUnitList.split('>');
			for(var a=0;a<businessUnitArray.length; a++){			
	       		$(data.rslt.obj).find('#'+businessUnitArray[a]).each(function (i) 
				{
			       		data.inst.open_node($(this));
			       		if(businessUnitArray[a]==positionIda){
								//$("#tree #"+emplId + " a").addClass("jstree-clicked").delay(5000);
								$("#tree #"+emplId + " a").delay(4000).css('background','#ff0');
								//$("#tree #"+emplId + " a").doTimeout( 5000, 'css', 'color', 'blue' );
			       		}
			       		
				});
			}
			$("#tree #"+emplId + " a").addClass("jstree-clicked");
			
		})

		

    });
    
  }
  
  



  function callDocument(id,type) {
    var dataSet = {};
        URL = 'EmployeeProfile';
        dataSet = {"partyId" : id, "ajaxUpdateEvent" : "Y"};
        URL='EmployeeProfile?partyId='+id;
    jQuery.ajax({
        url: URL,
        type: 'POST',
        data: dataSet,
        error: function(msg) {
            alert("An error occured loading content! : " + msg);
        },
        success: function(msg) {
        var rel=$("#"+id).attr("rel");
			if(rel=="P"){	
    			window.location.replace('EmployeeProfile?partyId='+id);
            }
            
        }
    });
    
  }
  
  
  function customMenu(node) {
    if(node.attr('rel')=='Y'){ 
    var items = {
        EmpPosition: { 
            label: "Add Employee Position",
            action: function (NODE, TREE_OBJ) {
                var dataSet = {};
                dataSet = {"partyId" : NODE.attr("id")};
                jQuery.ajax({
                    type: "GET",
                    url: "EditEmplPosition",
                    data: dataSet,
                    error: function(msg) {
                        alert("An error occured loading content! : " + msg);
                    },
                    success: function(msg) {
                        jQuery('div.page-container').html(msg);
                    }
                });
    //jQuerry Ajax Request   
    
            }
        },
        AddIntOrg: { 
            label: "Add Internal Organization",
            action: function (NODE, TREE_OBJ) {
                var dataSet = {};
                dataSet = {"headpartyId" : NODE.attr("id")};
                jQuery.ajax({
                    type: "GET",
                    url: "EditInternalOrgFtl",
                    data: dataSet,
                    error: function(msg) {
                        alert("An error occured loading content! : " + msg);
                    },
                    success: function(msg) {
                        jQuery('#dialog').html(msg);
                    }
                });
            }
        },
        RemoveIntOrg: { 
            label: "Remove Internal Organization",
            action: function (NODE, TREE_OBJ) {
                var dataSet = {};
                dataSet = {"partyId" : NODE.attr("id"),"parentpartyId" : $.jstree._focused()._get_parent(node).attr("id")};
                jQuery.ajax({
                    type: "GET",
                    url: "RemoveInternalOrgFtl",
                    data: dataSet,
                    error: function(msg) {
                        alert("An error occured loading content! : " + msg);
                    },
                    success: function(msg) {
                        jQuery('#dialog').html(msg);
                    }
                });
            }
        }
    };}
    if(node.attr('rel')=='N'){ 
        var items = {
            AddPerson: { 
                label: "Add Person",
                action: function (NODE, TREE_OBJ) {
                    var dataSet = {};
                    dataSet = {"emplPositionId" : NODE.attr("id")};
                    jQuery.ajax({
                        type: "GET",
                        url: "EditEmplPositionFulfillments",
                        data: dataSet,
                        error: function(msg) {
                            alert("An error occured loading content! : " + msg);
                        },
                        success: function(msg) {
                            jQuery('div.page-container').html(msg);
                        }
                    });
                }
            }
        }
    }

    if ($(node).hasClass("folder")) {
        // Delete the "delete" menu item
        delete items.deleteItem;
    }
    return items;
}


</script>
<div id="dialog" title="Basic dialog">
</div>
<div id="tree"></div>