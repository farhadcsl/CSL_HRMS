<div id="CompanyNoteViewCover">
	
</div>
<div id="CompanyNoteFullViewCover" >	
	<div id="CompanyNoteFullView">
		<div id="PopUpWindowCloseBtnCover"><div id="PopUpWindowCloseBtn" onclick="$('#CompanyNoteFullViewCover').hide('slow')">X</div></div>
		<div id="CompanyNoteFullTitle"></div>
		<div id="CompanyNoteFullDiscription"></div>
		<div id="CompanyNoteFullPublishDate"></div>
	</div>
</div>
<script>

   $(document).ready(function () {
   		var url = '/humanres/control/CompanyNoteView/';
        $.ajax({
	        url: url,
	        async: false,
	        type: 'POST',
            beforeSend: function () {
            },
            success: function (data) {
            	var count=data.companyNoteList.length;
	            for(var i=0;i<count;i++)
	            {	            	
	            	var dataRow=count-i-1;
            		var htmlContent='';
	            	var companyNoteId=data.companyNoteList[dataRow].companyNoteId;
	            	var title=data.companyNoteList[dataRow].title;
	            	var description=data.companyNoteList[dataRow].description;
	            	var shortDescription='';
	            	var descriptionLengthCount=description.length;
	            	if(descriptionLengthCount>100){
	            		shortDescription=description.substring(0,99) +' ...';   
	            	}
	            	else{
	            		shortDescription=description;
	            	}
	            	var dateAdded=data.companyNoteList[dataRow].dateAdded;
	            	htmlContent=htmlContent + '<div id="CompanyNoteView-'+companyNoteId+'" class="CompanyNoteView" onclick="showOrganizationNotes('+companyNoteId+')">';
	            	htmlContent=htmlContent + '<div id="CompanyNoteTitle-'+companyNoteId+'"  class="CompanyNoteTitle">'+title+'</div>';
	            	htmlContent=htmlContent + '<div id="CompanyNoteShortDescription-'+companyNoteId+'"  class="CompanyNoteShortDescription">'+shortDescription+'</div>';
	            	htmlContent=htmlContent + '<div id="CompanyNoteDiscription-'+companyNoteId+'"  class="CompanyNoteDiscription">'+description+'</div>';
	            	htmlContent=htmlContent + '<div id="CompanyNoteDiscription-'+companyNoteId+'"  class="CompanyNoteDiscription">'+description+'</div>';
	            	htmlContent=htmlContent + '<div id="CompanyNotePublishDate-'+companyNoteId+'"  class="CompanyNotePublishDate">Publish Date : '+dateAdded+'</div>';
	            	htmlContent=htmlContent + '</div>';	 
	            	$("#CompanyNoteViewCover").append(htmlContent)
		            if(i==4)
	            	{
	            		i=count;
	            		var viewAllHtmlCOntext='</br><div id="CompanyNoteViewAllLink" >';
	            		viewAllHtmlCOntext= viewAllHtmlCOntext+ ' <a href="<@ofbizUrl>CompanyNoteViewAll</@ofbizUrl>" >View All</a></div>';
	            		$("#CompanyNoteViewCover").append(viewAllHtmlCOntext)
	            	}
	            }

            }
        });
    });    
    function showOrganizationNotes(companyNoteId){
    	var title=$("#CompanyNoteTitle-"+companyNoteId).html();
    	var description=$("#CompanyNoteDiscription-"+companyNoteId).html();
    	var publishDate=$("#CompanyNotePublishDate-"+companyNoteId).html();
    	$("#CompanyNoteFullTitle").html(title);
    	$("#CompanyNoteFullDiscription").html(description);
    	$("#CompanyNoteFullPublishDate").html(publishDate);
    	$("#CompanyNoteFullViewCover").show('slow');
    }
 </script>
<style>
.CompanyNoteView{
    background-image: linear-gradient(#ffe, #fff);
    box-shadow: 0 0 3px 1px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    margin:10px;
    padding:5px;
}
.CompanyNoteView:hover{	
	background: #fefefe;
}
.CompanyNoteTitle {
    font-weight: bold;
    padding: 5px 0px;
    font-family: Calibri;
    font-size:12px;
    color:#0099cc;
}
.CompanyNoteShortDescription{    
    font-family: Calibri;
    font-size:11px;
    padding:2px;
}
.CompanyNoteDiscription{    
    font-family: Calibri;
    font-size:11px;
    padding:2px;
    display:none;
}
.CompanyNotePublishDate{
    text-align: right;
}



.CompanyNoteTitle {
    font-weight: bold;
    padding: 5px 0px;
    font-family: Calibri;
    font-size:12px;
    color:#0099cc;
}
.CompanyNoteDiscription{    
    font-family: Calibri;
    font-size:11px;
    padding:2px;
    display:none;
}
.CompanyNotePublishDate{
    text-align: right;
}



#CompanyNoteFullViewCover {
    background-color: #fefefe;
    margin:0% 10%;
    width: 80%;
    height:500px;
    display: none;
    position: fixed;
    top: 32px;
    right: 0px;
    border-radius: 5px;
    border: solid 1px;
    border-color: #ccc;
    z-index: 110;
    cursor:pointer;
}

#PopUpWindowCloseBtnCover {
    right: 0px;
    border-radius: 5px;
    padding: 5px;
    background:#ccc;
    background-image: linear-gradient(#fff, #ccc);
    box-shadow: 0 0 1px 1px rgba(0, 0, 0, 0.1);
    opacity: 1;
    z-index: 100;
    height:30px;
}
#PopUpWindowCloseBtn {
    float:right;
    font-size:20px;
    font-weight:bold;
    color:red;
    cursor:pointer;
}

#CompanyNoteFullView{
	margin:10px;	
}
#CompanyNoteFullTitle{
    font-weight: bold;
    padding: 10px;
    font-family: Calibri;
    font-size:16px;
    color:#0099cc;
    text-align:center;
    background:#eee;
    border-radius: 5px;

}
#CompanyNoteFullDiscription{  
    font-family: Calibri;
    font-size:16px;
    height:360px;
    padding:10px;   
    line-height:1.4em;
}
#CompanyNoteFullPublishDate{
    text-align: right;
    padding-right:10px;
}
#CompanyNoteViewAllLink{
	border-top:2px solid #eee;
	padding:2% 2%;
	cursor:pointer;
	width:96%;
	color:#0099cc;
    font-size:12px;
}
#CompanyNoteViewAllLink:hover{
	color:#0099cc;
}
</style>



