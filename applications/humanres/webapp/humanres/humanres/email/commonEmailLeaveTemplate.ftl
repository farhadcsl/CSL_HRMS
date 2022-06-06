<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>${title!}</title>
        <link rel="stylesheet" href="${StringUtil.wrapString(baseUrl!)}/images/maincss.css" type="text/css"/>
        <link rel="stylesheet" href="${StringUtil.wrapString(baseUrl!)}/bizznesstime/css/ContessaStyle.css" type="text/css"/>
        <style>
            .title_cutom{
                background: none repeat scroll 0 0 #0404B4;
                color: #FFFFFF;
                font-family: Calibri;
                font-weight: bold;
                padding: 3px 0;
                text-align: center;
            }
            body > hr{
                background: none repeat scroll 0 0 #00B2D9;
                border: medium none;
                padding: 2px;
            }
            label{
                color: #0099CC;
                font-weight: bold;
                font-family: Calibri;

            }
            td{
                padding:8px 20px;
                font-family: Calibri;
            }
            .footerNote{
	            font-family: Calibri;
	            color: #03a9f4;
	            font-size: 12px;
	            text-align: justify;
            }
        </style>
    </head>
    <body>

        <h3 class="title_cutom">CSL ERP Notification</h3>
        <br/>
		
		<#-- Create constructor object -->
		<#assign objectConstructor = "freemarker.template.utility.ObjectConstructor"?new()>
		
		<#-- Call calendar constructor -->
		<#assign clock = objectConstructor("java.util.GregorianCalendar")>
		
		<#-- Call formatter constructor -->
		<#assign ddmmyy = objectConstructor("java.text.SimpleDateFormat","dd/MM/yyyy hh:mm:ss")>
		
		<#-- Call getTime method to return the date in milliseconds-->
		<#assign date = clock.getTime()>
		
		<#-- Call format method to pretty print the date -->
		<#assign now = ddmmyy.format(date)>

	        <table border="0">
	         	<tr>
	                <td><label>Dear Concern,</label></td>
	                <td></td>
	            </tr>
	            <tr>
	                <td><label>Message:</label></td>
	                <td>${mailDescription!}</td>
	            </tr>
	            <tr>
		            <td>
			           <!-- <a href="https://localhost/humanres/control/EmplLeavesListForApproval"> 
			                Click here to view the request
				        </a>
				        <a href="${StringUtil.wrapString(baseUrl!)}/humanres/control/main/"> 
			                Click Here
				        </a> -->
					<a href="https://hrms.computerservicesltd.com/humanres/control/main"> 
			                Click Here
				        </a>
				        
			        </td>
	            </tr>
	        </table>	        
		
        <br/>
        <hr/>
        <table border="0">
	         	<tr>
	                <td>
	                <!--<img src="${StringUtil.wrapString(baseUrl!)}/bizznesstime/images/logo.png" alt="CSL"/>-->
	                <div id="logo"></div>
	                </td>
	            </tr>
	            <tr>
	                <td class="footerNote">
	        		NOTICE: This is a system generated email, if you think it was sent incorrectly please contact your System Administrator. 
	        		This communication may contain <b>CONFIDENTIAL AND PROPRIETARY INFORMATION.</b> If you are not the intended recipient, or believe you have received this communication in error, please do not print, copy, re-transmit, disseminate, or otherwise use the information. Thank you. 
	                </td>
	            </tr>	            
	        </table>
        <div style="display:none;">${footerMsg!}</div>
        <br /><br />

        </p>
    </body>
</html>
