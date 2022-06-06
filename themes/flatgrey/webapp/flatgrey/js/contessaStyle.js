

$(document).ready(function(){
	var formId = $("#sendEmailWithAttachmentScreen > form").attr('id');
	var divId = $("#sendEmailWithAttachmentScreen > form > div").attr('id');
	//alert(divId);
	var attachmentList = "attachmentList";
	$('#attachmentList').hide();
	$('#includeAttachment').change(function(){
		  if($(this).prop("checked")) {
		    $('#attachmentList').show();
		  } else {
		    $('#attachmentList').hide();
		  }
	});
});


function showConsumptionHistoryPopup(productId, productIdTo){
	var url= "LookupConsumptionHistory";
	url += "?productId=" + productId;
	url += "&productIdTo=" + productIdTo;
	prottayPopup('Consumption History','#history-div',url,'500','350');
}
/*
function prottayPopup(title,containerId,url,height,width){
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
}*/