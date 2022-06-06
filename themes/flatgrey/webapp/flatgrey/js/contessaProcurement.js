

$(document).ready(function() {
	$("#addProcRequirementRole").click(function() {
		$("#ajaxRequirementRoleAdd").dialog({
			autoOpen : true,
			title : "Add Requirement Role",
			height : 250,
			width : 400,
			modal : true,
			open : function() {
				$.ajax({
					url : "AjaxRequirementRole?requirementId="+document.getElementById('DisplayRequirement_requirementId').value, //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#ajaxRequirementRoleAdd").html(data);
					}
				});
			}

		});
	});
});

/*
$(document).ready(function() {
	$("#addProcRequirementRole").click(function() {
		
		var myElem = document.getElementById('DisplayRequirement_requirementId').value;
		var url = "AjaxRequirementRole?requirementId="+myElem;//This URL view request map
		
		prottayPopup('Add Requirment Role','#ajaxRequirementRoleAdd',url,'500','500');
	});
});
*/


$(document).ready(function() {
	$("#addProcRequirement").click(function() {
		$("#addProcRequirementUpdate").dialog({
			autoOpen : true,
			title : "&nbsp;",
			height : 410,
			width : 450,
			modal : true,
			open : function() {
				$.ajax({
					url : "AjaxRequirementRole?requirementId="+document.getElementById('EditRequirementRole_requirementId').value, //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#addProcRequirementUpdate").html(data);
					}
				});
			}

		});
	});
});


function getRequestData() {
	var data = {
		"test" : "data"
	};
	return data;
};





function updateProductPricePopup(productId, partyId){

var url= "UpdateProductSuppliers?productId="+productId+"&partyId="+partyId;
//alert(url);
//"&currencyUomId="+currencyUomId+"&minimumOrderQuantity="+minimumOrderQuantity+"&availableFromDate="+availableFromDate;
	
contessaPopup('Update product price','#suProduct',url,'210','450');

}



function showUpdateOrderItemHistory(orderId, productId){

var url= "UpdateHistoryForOrderItem?orderId="+orderId+"&productId="+productId;
//alert(url);
//"&currencyUomId="+currencyUomId+"&minimumOrderQuantity="+minimumOrderQuantity+"&availableFromDate="+availableFromDate;
	
contessaPopup('Order Item Updated History for'+' '+productId,'#orderItemHistory',url,'210','450');

}

function updatePreCostingProductQuantity(productId, orderId, productIdTo, quantity ,currencyUomId){	

var url= "UpdatePreCostQuantity?productId="+productId+"&orderId="+orderId+"&productIdTo="+productIdTo+"&quantity="+quantity+"&currencyUomId="+currencyUomId;

contessaPopup('Update Quantity','#updateQuantity',url,'210','450');

}


function updatePreCostingProductPrice(productId,orderId, productIdTo, lastprice, currencyUomId){
	
var url= "UpdatePreCostPrice?productId="+productId+"&orderId="+orderId+"&productIdTo="+productIdTo+"&lastprice="+lastprice+"&currencyUomId="+currencyUomId;

contessaPopup('Update Price','#updateQuantityPrice',url,'210','450');

}
/*

function prottayPopup(title,containerId,containerId1,url,height,width){
	
	
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
