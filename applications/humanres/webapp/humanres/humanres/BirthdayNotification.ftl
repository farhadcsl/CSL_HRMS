
<!----------------------------------- Birthday Alert Process ------------------------------->

<div id="BirthdayNotificationBoxCover"  onclick="LoadBirthdayNotification()">
    <div id="BirthdayCountBox"></div>
    <div id="ShowBirthdayNotificationBox"></div> 
</div>
<input type="hidden" id="BirthdayNotificationShowStatus" />

<script>
   $(document).ready(function () {   
   
   		var findBirthdayPersonURL	 = '/humanres/control/findBirthdayPerson/';
        $.ajax({
	        url: findBirthdayPersonURL,
	        async: false,
		    type: 'POST',
            success: function (data) {
            	var count=data.birthdayEmployeeList.length;
            	$("#BirthdayCountBox").html("<div >"+count+" Person Birthday Notification </div>");
            	$("#BirthdayNotificationBoxCover").css("display","block");
            	var htmlContent="";
            	
            	
	            for(var i=0;i<count;i++){
	            	var birthdayPartyId=data.birthdayEmployeeList[i].partyId;
	            	var fullName=data.birthdayEmployeeList[i].firstName+" "+data.birthdayEmployeeList[i].middleName+" "+data.birthdayEmployeeList[i].lastName;
	            	var imageSRC='src="'+data.birthdayEmployeeList[i].profileImageUrl+'"';
	            	htmlContent=htmlContent+'<a href="<@ofbizUrl>EmployeeProfile?partyId='+birthdayPartyId+'</@ofbizUrl>" >'
	            	htmlContent=htmlContent+'<div class="employeeBirthdayNotificationCover">';
	            	htmlContent=htmlContent+'<div class="birthdayPersonImageCover">';
	            	htmlContent=htmlContent+'<img '+imageSRC+' height="40px" width="50"/></div>';
	            	htmlContent=htmlContent+'<div class="birthdayPersonCover">';
	            	htmlContent=htmlContent+'<div >'+fullName+'</div>';
	            	htmlContent=htmlContent+'<div >Date of birth :'+data.birthdayEmployeeList[i].birthDate;
	            	htmlContent=htmlContent+'</div></div></a>';
				}
            	$("#ShowBirthdayNotificationBox").html(htmlContent);
            	$("#ShowBirthdayNotificationBox").show('slow');
              $("#BirthdayNotificationShowStatus").val('show');
            }
        });
        
    });    
    
	function LoadBirthdayNotification(){
	 	if ($("#BirthdayNotificationShowStatus").val() == 'show') {
	        $("#ShowBirthdayNotificationBox").hide('slow');
	        $("#BirthdayNotificationShowStatus").val('');
	    }
	    else {
            $("#ShowBirthdayNotificationBox").show('slow');
            $("#BirthdayNotificationShowStatus").val('show');
	    }
	}
    </script>

<style>
	
	
	#BirthdayCountBox{
		text-align:center;
	    color: #555;
	    font-family: Arial;
	    font-weight: bold;
	    font-size: 20px;
	    cursor:pointer;
	    padding-top: 10px;
	    margin-bottom: 5px;
	    position: relative;
	}
	
	#ShowBirthdayNotificationBox {
	    display: none;
	    position: relative;
	    margin: 10px;
	    font-family: Calibri;
	    font-weight: bold;
	    font-size: 20px;
	    color: #111;
		height:auto;
	    width: 400px;
	}
	#ShowBirthdayNotificationBox:hover{
		color:#003399;
	}
	.employeeBirthdayNotificationCover{	
	}
	.birthdayPersonImageCover{	
		float:left;		
		height:50px;
		width:60px;
	}
	.birthdayPersonCover{
	    font-family: Calibri;
	    font-size: 10px;
		width:330px;
		height:50px;
	}
	</style>
	
<!------------------------ End Of Birthday Alert Process ----------------------------->
	
	
	
	