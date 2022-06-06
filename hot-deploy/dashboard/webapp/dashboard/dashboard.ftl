<#if (requestAttributes.externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>
<#if (externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>

<head>
  <title>Dashboard Contessa</title>

<!--<script src="js/jquery-1.2.6.pack.js"type="text/javascript"></script>
<script src="js/jquery.flow.1.1.min.js"type="text/javascript"></script>
<script src="jquery-1.10.2.min.js"></script>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js">
<script src="http://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.10.2.min.js">-->

</head>
<!------------------------------------------------------------------------------------------------------------------->
<!-------------------------------------------------MAIN-------------------------------------------------------------->
<!------------------------------------------------------------------------------------------------------------------->
<div id="Maindash">
   <!--  no need   <div id="mainpic">-->        
   <!-- <br><img class="mainpic" src="images/business_trend_further.jpg" alt="business_trend_further"/></br>  <!--main picture-->
     
    <div id="dashboardHeader"></div>
    <div id="dashboardHeader2">
    	<div id="dashboardLogoCover">
    		<img src="/bizznesstime/images/logo.png" alt="Contessa Solution Limited" >
    	</div>
    </div>
    <div id="dashboardHeader3">
    	<div id="moduleTitleViewCover">
    		<div id="moduleTitleView1"></div>
    		<div id="moduleTitleView2"></div>
    	</div>
    </div>
    
    <div id="dashboardContainer">
        <div id="leftDashboardContainer">
            <div id="leftDashboardContainerCover">
                <div id="formDashboardHeader">                    
				<span>
		            <#if person?has_content>
		              ${uiLabelMap.CommonWelcome},  ${person.firstName?if_exists} ${person.lastName?if_exists} ( ${userLogin.userLoginId} )
		            <#elseif partyGroup?has_content>
		              ${uiLabelMap.CommonWelcome},  ${partyGroup.groupName?if_exists} ( ${userLogin.userLoginId} )
		            <#else>
		              ${uiLabelMap.CommonWelcome}
		            </#if>
		        </span>
                </div>
                <div id="dashboardLogoutBtn" >
                	<a href="<@ofbizUrl>logout</@ofbizUrl>">${uiLabelMap.CommonLogout}</a>
                </div>
                <div id="dashboardNewsBoardCover" >
                </div>
            </div>
        </div>
        
        <div id="centerDashboardContainer">
            <div id="dashboardModuleFullCover">
			     <a  class="moduleCover" title="HR" href="/humanres/control/main${externalKeyParam}"
			      onmouseover="SelecteModule('HR','HR')">
			     	<div class="moduleImageCover">
	                    <img src="/bizznesstime/images/Module/HR & Payroll.png" alt="HR & Payroll" class="image">
	                </div>		
                    <div class="moduleCovername">HR</div>	     	
			     </a>
			     <a  class="moduleCover" title="Payroll" href="/payroll/control/main${externalKeyParam}"
			      onmouseover="SelecteModule('Payroll','Payroll')">
			     	<div class="moduleImageCover">
	                    <img src="/bizznesstime/images/Module/accounting.png" alt="Payroll" class="image">
	                </div>		
                    <div class="moduleCovername">Payroll</div>	     	
			     </a>
			     <a  class="moduleCover" title="Workflow" href="/workflow/control/main${externalKeyParam}"
			      onmouseover="SelecteModule('Workflow','Workflow')">
			     	<div class="moduleImageCover">
	                    <img src="/bizznesstime/images/Module/settings.png" alt="Workflow" class="image">
	                </div>		
                    <div class="moduleCovername">Workflow</div>	     	
			     </a> 
			     <!--<a  class="moduleCover" title="Accounting" href="/accounting/control/main${externalKeyParam}"
			      onmouseover="SelecteModule('Accounting','Accounting')">
			     	<div class="moduleImageCover">
	                    <img src="/bizznesstime/images/Module/accounting.png" alt="Accounting" class="image">
	                </div>		
                    <div class="moduleCovername">Accounting</div>	     	
			     </a>
			     <a  class="moduleCover" title="Catalog" href="/catalog/control/main${externalKeyParam}"
			      onmouseover="SelecteModule('Catalog','Catalog')">
			     	<div class="moduleImageCover">
	                    <img src="/bizznesstime/images/Module/catalogHover.png" alt="Catalog" class="image">
	                </div>		
                    <div class="moduleCovername">Catalog</div>	     	
			     </a>
			     <a  class="moduleCover" title="Warehouse" href="/facility/control/main${externalKeyParam}"
			      onmouseover="SelecteModule('Warehouse','Warehouse')">
			     	<div class="moduleImageCover">
	                    <img src="/bizznesstime/images/Module/warehouse.png" alt="Warehouse" class="image">
	                </div>		
                    <div class="moduleCovername">Warehouse</div>	     	
			     </a>
			     <a  class="moduleCover" title="Sales & CRM" href="/marketing/control/main${externalKeyParam}"
			      onmouseover="SelecteModule('Sales & CRM','Sales & CRM')">
			     	<div class="moduleImageCover">
	                    <img src="/bizznesstime/images/Module/Sales&CRM.png" alt="Sales & CRM" class="image">
	                </div>		
                    <div class="moduleCovername">Sales & CRM</div>	     	
			     </a>
			     <a  class="moduleCover" title="Order Management" href="/ordermgr/control/main${externalKeyParam}"
			      onmouseover="SelecteModule('Order Management','Order Management')">
			     	<div class="moduleImageCover">
	                    <img src="/bizznesstime/images/Module/OrderManagement.png" alt="Order Management" class="image">
	                </div>		
                    <div class="moduleCovername">Order Management</div>	     	
			     </a>
			     <a  class="moduleCover" title="Asset Management" href="/assetmaint/control/main${externalKeyParam}"
			      onmouseover="SelecteModule('Asset Management','Asset Management')">
			     	<div class="moduleImageCover">
	                    <img src="/bizznesstime/images/Module/HR & Payroll.png" alt="Asset Management" class="image">
	                </div>		
                    <div class="moduleCovername">Asset Management</div>	     	
			     </a>
			     <a  class="moduleCover" title="Settings" href="/webtools/control/main${externalKeyParam}"
			      onmouseover="SelecteModule('Settings','Settings')">
			     	<div class="moduleImageCover">
	                    <img src="/bizznesstime/images/Module/settings.png" alt="Settings" class="image">
	                </div>		
                    <div class="moduleCovername">Settings</div>	     	
			     </a> -->
            </div>
        </div>
        <div id="rightDashboardContainer"></div>
    </div>        
    <div id="dashboardFooter">
        Copy right by : Contessa Solution Limited
    </div>
</div>

<script>

   $(document).ready(function (){
	   	$("#wrap").css("display","none");
	   	$("#footer").css("display","none");
	   	$("#humanMsg").css("display","none");
	   	var Maindash=$("#Maindash").html();
	   	$("#Maindash").html('');
	   	$("body").css("background","none")
	   	$("body").append('<div id="CopyDashBoard"></div>');
	   $("#CopyDashBoard").html(Maindash);
   });
	function SelecteModule(t1,t2){
		document.getElementById("moduleTitleView1").innerHTML=t1;
		document.getElementById("moduleTitleView2").innerHTML=t2;
	}
</script>
<style>
    body {
        margin:0px;
        padding:0px;
    }
    #dashboardHeader {
        background: none repeat scroll 0 0 #094460;
        height: 30px;
    }

    #dashboardHeader2 {
        background-color: #fff;
    }
    #dashboardLogoCover{
    	margin:10px 20px;
    }
	#dashboardLogoCover img{
	height:50px;
	weight:500px;
}
    #dashboardHeader3 {
        background-color: #CCCCCC;
        float: left;
        padding:5px 0px;
        width: 100%;
        height:30px;
    }

    #moduleTitleViewCover{
    	padding-left:300px;
    	margin:5px;
    }
    #moduleTitleView1{
    	font-size:20px;
    	font-family:Calibri;
    	color:#FF6633;
    	float:left;
    	padding-right:5px;
    }
    #moduleTitleView2{
    	font-size:20px;
    	font-family:Calibri;
    	color:#999;    
    }
    #dashboardContainer {
        float: left;
        width: 100%;
    }

    #leftDashboardContainer {
        width: 23%;
        float: left;
    }

    #leftDashboardContainerCover {
    	weight:97%;
        border-right: 2px dotted #333333;
        min-height:600px;
    }

    #centerDashboardContainer {
        width: 54%;
        float: left;
    }
    #dashboardModuleFullCover {
        margin:10px;
    }
    #rightDashboardContainer {
        width: 23%;
        float: left;
    }

    #formDashboardHeader {
        background-color: #FF6633;
        color: #FFFFFF;
        font-size: 1em;
        padding: 6px 0 6px 6px;
		margin:10px 25px;
		padding:10px;
    }
    #dashboardLogoutBtn{
		margin:20px 25px;
    	
    }
	#dashboardLogoutBtn a{
        text-decoration: none;
        color: #FF6633;
        padding:2px 10px;
        height: 25px;
        weight:150px;
        text-align: center;
        border: 2px solid #666666;
        font-family:Arial;
	
	}
    .moduleCovername {
        height: 30px;
        text-align: center;
        padding-top:5px;
    }
     .moduleCovername:hover {
            text-decoration: underline;
            text-align: center;
            cursor: pointer;
        }

    .moduleCover {
        margin-right: 10px;
        margin-bottom: 10px;
        float: left;
        margin-top: 30px;
        height: 130px;
        width: 150px;
        cursor: pointer;
        font-decoration:none;
        color:#000;
        text-decoration: none;
    }

    .moduleImageCover {
        height: 100px;
        width: 150px;
        border: 1px solid #ddd;
    }
    .moduleImageCover img {
        height: 100px;
        width: 150px;
    }

        .moduleImageCover img:hover {
            z-index: 100;
            box-shadow: 0 0 3px 2px rgba(0,0,0,0.6);
            border-radius:1px;
        }
    #dashboardFooter {
        clear:both;
        background: none repeat scroll 0 0 #094460;
        width:100%;
        text-align:center;
        font-family:Calibri;
        font-size:16px;
        padding:20px 0px;
    }
    
</style>
