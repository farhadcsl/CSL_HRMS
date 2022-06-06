$(document).ready(function() {	
    $('input[type=submit]').click(function (){
    	startLoader();
    });
    $('#loadingContainer').click(function (){
    	$("#loadingContainer").fadeOut("slow");
    });
	$("#addPartyTrainingRequestDetailsBtn").click(function() {
		var requestId=document.getElementById('ViewPartyTrainingRequest_requestId').value;
		var trainingOfferId=document.getElementById('ViewPartyTrainingRequest_trainingOfferId').value;
		var partyId=document.getElementById('ViewPartyTrainingRequest_partyId').value;
		$("#addPartyTrainingRequestDetailsPopUp").dialog({
			autoOpen : true,
			//title : "Party Training Request Details Form [ "+requestId+" ]",
			height : 500,
			width : 800,
			modal : true,
			open : function() {
				$.ajax({
					url : "NewPartyTrainingRequestDetails?requestId="+requestId+'&trainingOfferId='+trainingOfferId+'&partyId='+partyId,
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#addPartyTrainingRequestDetailsPopUp").html(data);
					}
				});
			}
		});
	});	
});

function startLoader(){
	var textMSG="Loading .";
	var dotText=1;
	$("#loadingMsg").html(textMSG);
	$("#loadingContainer").fadeIn("slow");
	var unfiniteLoop=true;
	while(unfiniteLoop==true){
		unfiniteLoop=false;
		var dot="";
		for(i=0;i<=dotText;i++){
			dot=dot+".";
			if(i==dotText){
				if(dotText==3){
					dotText=1;
				}
				else{
					dotText=dotText+1;
				}
				setTimeout(function(){
					$("#loadingMsg").html(textMSG+dot);
					unfiniteLoop=true;
				},500);
			}
		}
	}
}