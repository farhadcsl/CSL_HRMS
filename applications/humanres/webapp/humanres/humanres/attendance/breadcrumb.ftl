<!-- Order list for the breadcrumb --> 
<ol id="reverse_all" class="breadcrumb flat">
	<!-- breadcrumbList is a list comes from groovy -->
	<li>
		<form id="breadcrumb" class="sort-order-asc" name="breadcrumb" method="post" enctype="multipart/form-data" action="<@ofbizUrl>setUserPreferenceForatten</@ofbizUrl>">
			
				<input type="hidden" name="userPrefTypeId" value="ATTENDANCE_LOCATION" />
				<input type="hidden" name="userPrefGroupTypeId" value="GLOBAL_PREFERENCES" />
				<input type="hidden" name="userPrefValue" value="company" />
				<input type="hidden" name="selection" value="Company"/>	
				<a href="javascript:void()" onclick="submitHandler()">${companyName}</a>	
	
		</form>
		  <script type="text/javascript">
		
				function submitHandler() {
					$("#breadcrumb").submit();
				}
				
		  </script>
	</li>
	<#list attenbreadcrumbList as breadcrumb>
		<!-- li is under the loop of list. Form id is unique for javaScript ID --> 
		<li>
		 	<form id="breadcrumb${breadcrumb.partyId}" class="sort-order-asc" name="breadcrumb${breadcrumb.partyId}" method="post" enctype="multipart/form-data" action="<@ofbizUrl>setUserPreferenceForatten</@ofbizUrl>">
			
				<input type="hidden" name="userPrefTypeId" value="ATTENDANCE_LOCATION" />
				<input type="hidden" name="userPrefGroupTypeId" value="GLOBAL_PREFERENCES" />
				<input type="hidden" name="userPrefValue" value="${breadcrumb.partyId}" />
				<a href="javascript:void()" onclick="submitHandler${breadcrumb.partyId}()">${breadcrumb.groupName}</a>	
			</form>
		
		  <script type="text/javascript">
		
				function submitHandler${breadcrumb.partyId}() {
					$("#breadcrumb${breadcrumb.partyId}").submit();
				}
				
		  </script>
		  
		</li>
	</#list>
</ol>
<!-- END list -->



<!-- CSS Style Sheet for this breadcrumb, This CSS is here for development issue. this css will go to a css file. -->
<style>
	li > a{
		display: block;
		padding: 5.5px 15px;
		text-decoration: none;
	}
	#reverse_all, #reverse_allChild {
		/*position: absolute;
		box-shadow: 0 0 15px 1px rgba(0, 0, 0, 0.35);*/
		overflow: hidden;
		border-radius: 5px;
		display: inline-block;
		border: 2px solid #aaa;
    background-image: linear-gradient(#fff, #ccc);
    box-shadow: 0 0 2px 1px rgba(0, 0, 0, 0.4);
    	list-style-type: none;
	}
	#reverse_all li, #reverse_allChild li {
		float: left;
		text-align:center;
	}
	#reverse_all > li > form > a:after, #reverse_all > li > a:after, #reverse_allChild > li > a:after {
		content: '';
		position: absolute;
		top: 0; 
		right: -13px;
		
		width: 27px; 
		height: 27px;
		<!--
		-webkit-transform: scale(0.707) rotate(45deg);
		transform: scale(0.707) rotate(45deg);
		z-index: 1;

		box-shadow: 
			2px -2px 0 2px rgba(0, 0, 0, 0.4), 
			3px -3px 0 2px rgba(255, 255, 255, 0.1);
		border-radius: 0 5px 0 50px;
		-->
	}

.breadcrumb > li > form > a, .breadcrumbChild > li > a {
	text-decoration: none;
	outline: none;
	display: block;
	float: left;
	font-size: 14px;
	line-height:27px;
	color: white;
	padding: 0 10px 0 15px;
	position: relative;
    background-image: linear-gradient(#fff, #ccc);
}

.breadcrumb > li > form > a:first-child, .breadcrumbChild > li > a:first-child {

}

.breadcrumb > li > form > a.active, .breadcrumb > li > form > a:hover, .breadcrumbChild > li > a.active, .breadcrumbChild > li > a:hover{
	
}
.breadcrumb > li > form > a.active:after, .breadcrumb > li > form > a:hover:after, .breadcrumbChild > li > a.active:after, .breadcrumbChild > li > a:hover:after {
	
}

.flat > li > form > a, .flat > li > form > a:after, .flat > li > a, .flat > li > a:after {
	color: #111;
	transition: all 0.5s;
}
.flat > li > form > a:hover, .flat > li > form > a.active, .flat > li > form > a:hover:after, .flat > li > form > a.active:after{
	background: #1d407e;
	color:#fff;
}

.flat > li > a:hover, .flat > li > a.active, .flat > li > a:hover:after, .flat > li > a.active:after{
	background: #1d407e;
	color:#fff;
}
.breadcrumb li a{
	text-decoration: none;
	outline: none;
	display: block;
	float: left;
	font-size: 14px;
	line-height:27px;
	padding: 0 10px 0 15px;
	position: relative;

}
</style>

<!-- Prefixfree 
<script src="http://thecodeplayer.com/uploads/js/prefixfree-1.0.7.js" type="text/javascript"></script>-->