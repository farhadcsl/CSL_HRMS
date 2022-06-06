 


function setStyleName(field){
	
	 var orderItemId = document.getElementById("StyleLayoutTemplate_orderId").value
	
	$.ajax({
		
		url : "geOrderItemForOrder?orderItemId="+orderItemId,
		
		type : "GET",
		data : getRequestData(),
		success : function(data) {
			
			//field = data.styleId;
			// alert("The Product :" + field);
			// alert("orderId: " + orderItemId);
			 //$("#productIdTo").html(field);
			
      
	
  			var x = document.getElementById("productId");
  			
  			
  			while (x.options.length > 0) {
  				x.remove(0);
  			}	  			

             for (var i = 0; i < data.styleId.length; i++) {
            	 
            	 var option = document.createElement("option");
            	 
            	 option.text = data.styleId[i].itemDescription;
     			option.value = data.styleId[i].productId;
     			
     			x.add(option);
     		
             }
			 
			 
             var y = document.getElementById("styleProductId");
   			
   			
   			while (y.options.length > 0) {
   				y.remove(0);
   			}	  			

              for (var i = 0; i < data.parentproducts.length; i++) {
             	 
             	 var option = document.createElement("option");
             	 
             	 option.text = data.parentproducts[i].productName;
      			option.value = data.parentproducts[i].productId;
      			
      			y.add(option);
      		/*alert(data.parentproducts[i].productName);*/
              }
		}
	
	});
}



function orderItemStyleName(field){
	
	 var productId = document.getElementById("productId").value
	
	$.ajax({
		
		url : "getStyleForOrderItem?productId="+productId,
		
		type : "GET",
		data : getRequestData(),
		success : function(data) {
			
			//field = data.styleId;
			// alert("The Product :" + field);
			// alert("orderId: " + orderItemId);
			 //$("#productIdTo").html(field);
			
      
	
  		
			 
             var y = document.getElementById("styleProductId");
   			
   			
   			while (y.options.length > 0) {
   				y.remove(0);
   			}	  			

              for (var i = 0; i < data.product.length; i++) {
             	 
             	 var option = document.createElement("option");
             	 
             	 option.text = data.product[i].productName;
      			option.value = data.product[i].productId;
      			
      			y.add(option);
      		/*alert(data.parentproducts[i].productName);*/
              }
		}
	
	});
}

function setStyleNameTest(param){
	 alert(param.value);
	$.ajax({
		url : "getStyleForOrderItem?orderItemId="+param.value,
		type : "GET",
		data : getRequestData(),
		success : function(data) {
			alert(url);
			field.value=data.styleId;
			 alert(field.value);
		}
	});
}

function myFunction(orderId) {
	alert(orderId);
    var x = document.getElementById("orderId");
  
}