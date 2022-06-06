function addGlAccountSetupPopup(glAccountId){ 
	var url= "";
	if(glAccountId!=null){
		url= "AddGlAccountSetup?glAccountId="+glAccountId;
	}else{
		url= "AddGlAccountSetup";
	}
	

		//alert(url);
	prottayGlAccountSetupPopup('Add GlAccountSetup Info','#suGlAccountSetup',url,'500','500');

	}
function prottayGlAccountSetupPopup(title,containerId,url,height,width){
	
	
	$(containerId).dialog({
		autoOpen : true,
		title : title,
		height : height,
		width : width,
		modal : true,
		open : function() {
			$.ajax({	
				url : url,
				type : "POST",
				data : getRequestData(),
				success : function(data) {
					$(containerId).html(data);
				}
			});
		}

	});
}