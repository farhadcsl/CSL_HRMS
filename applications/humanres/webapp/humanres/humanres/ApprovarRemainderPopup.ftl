<!----------------------------------- Leave Notification ------------------------------->
<div id="NotificationBoxCover">
    <div id="CountBox" onclick="LoadNotification()"></div>
    <a href="<@ofbizUrl>EmplLeavesListForApproval</@ofbizUrl>" >
    	<div id="ShowNotificationBox"></div> 
    </a>
</div>
<input type="hidden" id="NotificationShowStatus" />


<script>
   $(document).ready(function () {
   
   	var partyId=$("#userLoginIdForRemainder").val();
   		if(partyId!=undefined){
	   		var dataSet = {"partyId": partyId};
	    	var url = '/humanres/control/ApprovalNotificationRemainder/';
	        $.ajax({
		        url: url,
		        async: false,
		        type: 'POST',
		        data: dataSet,
	            beforeSend: function () {
	            },
	            success: function (data) {
	            	var count=data.remainderList.length;
	            	if(count>0){
		            	$("#NotificationBoxCover").css("display","block");
		            	var htmlContent="<div > "+ count+" Leave requiest is pending for approval </div>"
		            	$("#ShowNotificationBox").html(htmlContent) 
		            	$("#CountBox").html(count);
	            	}
	            }
	        });
   		}
        
    });    
	function LoadNotification() {
	    if ($("#NotificationShowStatus").val() == 'show') {
	        $("NotificationBoxCover").css('background', 'none');
	        $("#ShowNotificationBox").hide('slow');
	        $("#NotificationShowStatus").val('');
	    }
	    else {
            $("#ShowNotificationBox").show('slow');
            $("#NotificationShowStatus").val('show');
	    }
	}
	function HideNotification() {
	    $("#NotificationBoxCover").css('background', 'none');
	    $("#ShowNotificationBox").hide('slow');
	    $("#NotificationShowStatus").val('');
	}
	
	
</script>

<style>
	#NotificationBoxCover{
	 	top: 118px;
	    right: 0px;
	    background: #fefefe;
	    position: fixed;
	    opacity: 0.7;
	    /*z-index: 100;*/
	    display: none;
	    border-top-left-radius: 10px;
	    border-bottom-left-radius: 10px;
	    border:2px solid #111;
	}
	#CountBox{
		height: 30px;
	    width: 40px;
	    background: #003399;
	    color: #;
	    font-family: Arial;
	    font-weight: bold;
	    font-size: 20px;
	    text-align: center;
	    padding-top: 15px;
	    position: relative;
	    float: right;
	    border-top-left-radius: 8px;
	    border-bottom-left-radius: 8px;
	    cursor: pointer;
	}	
	#ShowNotificationBox {
	    display: none;
	    margin: 5px;
	    width: 600px;
	    position: relative;
	    margin: 10px;
	    font-family: Calibri;
	    font-weight: bold;
	    font-size: 20px;
	    color: #111;
	}
	#ShowNotificationBox:hover{
		color:#003399;
	}	
</style>
<!------------------------- End of Leave Notification ------------------------------->