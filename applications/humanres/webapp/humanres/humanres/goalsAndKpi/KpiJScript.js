/**
 * mgrApprStatus_o_2 approved rejected
 * 
 * achievement_o_2
 */

$(document).ready(function() {
	var counter = 0;
	var mgrApproval;
	var achievement;
	var elements = document.getElementById("ListEmployeeGoalsAndKpi").elements;
	var obj = {};
	for (var i = 0; i < elements.length; i++) {
		var item = elements.item(i);
		obj[item.name] = item.value;

		if (obj[item.name] == "approved") {
			achievement = "ListEmployeeGoalsAndKpi_achievement_o_" + counter;
			// alert(achievement);
			document.getElementById(achievement).readOnly = true;
			counter++;
		}

		else if (obj[item.name] == "rejected") {
			// alert(counter);
			counter++;
		}

	}
	// alert(JSON.stringify(obj));

});


/**
 * mMgrApprStatus_o_0		approved	rejected    
 * 
 * mAchievement_o_0
 viewMonthlyKPIform_mMgrApprStatus_o_0
 viewMonthlyKPIform_mAchievement_o_0
 */

$(document).ready(function() {
	var counter=0;
		var mgrApproval;
		var mAchievement;
		 var elements = document.getElementById("viewMonthlyKPIform").elements;
		    var obj ={};
		    for(var i = 0 ; i < elements.length ; i++){    
		        var item = elements.item(i);
		        obj[item.name] = item.value;
		        
		        if(obj[item.name]=="Accepted"){	        
		        mAchievement = "viewMonthlyKPIform_mAchievement_o_"+counter;
		        //alert(achievement);
		        document.getElementById(mAchievement).readOnly = true;
		        counter++;
		        }
		        
		        else if(obj[item.name]=="Rejected"){	         
		         //alert(counter);
		         counter++;
		        }
		        
		    }
		//alert(JSON.stringify(obj));

	 });


$(document).ready(function() {
		var mgrApproval;
		var mAchievement;
		 var elements = document.getElementById("sendToManagerMail").elements;
		    var obj ={};
		    for(var i = 0 ; i < elements.length ; i++){    
		        var item = elements.item(i);
		        obj[item.name] = item.value;
		        
		        if(obj[item.name]=="Accepted"){	        
		        mAchievement = "viewMonthlyKPIform_mAchievement_o_"+counter;
		        //alert(achievement);
		        document.getElementById(mAchievement).readOnly = true;
		        counter++;
		        }
		        
		        else if(obj[item.name]=="Rejected"){	         
		         //alert(counter);
		         counter++;
		        }
		        
		    }
		//alert(JSON.stringify(obj));

	 });