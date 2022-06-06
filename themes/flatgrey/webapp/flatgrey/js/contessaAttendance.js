
	var timer = setInterval(function refreshCurrentEmployeeInfo() {
		var myElem = document.getElementById('golobalCurrentEmpInfodiv');
		if (myElem != null){ 
				
				$.ajax({
					url : "GolobalCurrentEmpInfo", //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#golobalCurrentEmpInfodiv").html(data);
					}
				});
			
		}
			
	
 }, 2000);
	
	
	var timer = setInterval(function refreshCurrentEmployeeLog() {
		var myElem = document.getElementById('deviceCurrentEmpStatusdiv');
		if (myElem != null){ 
				
				$.ajax({
					url : "DeviceCurrentEmpStatus", //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#deviceCurrentEmpStatusdiv").html(data);
					}
				});
			
		}
			
	
 }, 2000);



	var timer = setInterval(function refreshEmployeeLog() {
		var myElem = document.getElementById('employeelogdiv');
		if (myElem != null){ 
				
				$.ajax({
					url : "AttendanceList", //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#employeelogdiv").html(data);
					}
				});
			
		}
			
	
 }, 10000);
	
	
	var timer = setInterval(function refreshGateList() {
		var myElem = document.getElementById('gateListdiv');
		if (myElem != null){ 
				
				$.ajax({
					url : "GateList", //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#gateListdiv").html(data);
					}
				});
			
		}
			
	
 }, 10000);
	
	
	
	var timer = setInterval(function refreshEmployeesCurrentStatus() {
		var myElem = document.getElementById('empCurrentStatusdiv');
		if (myElem != null){ 
				
				$.ajax({
					url : "EmpCurrentStatus", //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#empCurrentStatusdiv").html(data);
					}
				});
			
		}
			
	
 }, 10000);
	
	var timer = setInterval(function refreshEmpCurrentStatus() {
		var myElem = document.getElementById('employeesCurrentStatusdiv');
		if (myElem != null){ 
				
				$.ajax({
					url : "EmployeesCurrentStatus", //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#employeesCurrentStatusdiv").html(data);
					}
				});
			
		}
			
	
 }, 10000);
	
	
	
	var timer = setInterval(function refreshFloorList() {
		var myElem = document.getElementById('floorListdiv');
		if (myElem != null){ 
				
				$.ajax({
					url : "FloorList", //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#floorListdiv").html(data);
					}
				});
			
		}
			
	
 }, 10000);
	
	var timer = setInterval(function refreshBuildingList() {
		var myElem = document.getElementById('buildingListdiv');
		if (myElem != null){ 
				
				$.ajax({
					url : "BuildingList", //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#buildingListdiv").html(data);
					}
				});
			
		}
			
	
 }, 10000);
	
	var timer = setInterval(function refreshDeviceList() {
		var myElem = document.getElementById('deviceListdiv');
		if (myElem != null){ 
				
				$.ajax({
					url : "DeviceList", //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#deviceListdiv").html(data);
					}
				});
			
		}
			
	
 }, 10000);
	
	var timer = setInterval(function refreshEmployeeList() {

		
		var myElem = document.getElementById('employeesdiv');
		if (myElem != null){ 
			
			$.ajax({
				url : "EmployeeList", //This URL view request map
				type : "POST",
				data : getRequestData(),
				success : function(data) {
					$("#employeesdiv").html(data);
				}
			});
		}
		
		
	
 }, 10000);

 
 function getRequestData() {
	var data = {
		"test" : "data"
		
			
	};
	return data;
};

$(document).ready(function() {
	$("#addLocationIcon").click(function() {
		$("#addLocationPopUp").dialog({
			autoOpen : true,
			title : "Add Form",
			height : 225,
			width : 500,
			modal : true,
			open : function() {
				$.ajax({
					url : "AddLocationPopup",
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#addLocationPopUp").html(data);
					}
				});
			}

		});
	});
	$("#addDeviceIcon").click(function() {
		$("#addDevicePopUp").dialog({
			autoOpen : true,
			title : "Add Form",
			height : 500,
			width : 500,
			modal : true,
			open : function() {
				$.ajax({
					url : "AddLocationPopup",
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#addDevicePopUp").html(data);
					}
				});
			}

		});
	});
});


$(document).ready(function() {
	var div1 = document.getElementById('div1');
	var div2 = document.getElementById('div2');
	var employeesCurrentStatusdiv = document
			.getElementById('employeesCurrentStatusdiv');

	$("#deviceCurrentEmpStatusdivMax").click(function() {
		$("#deviceCurrentEmpStatusdiv").animate({
			/*width : 1010*/
		}, 600);

		div1.style.display = 'none';
		div2.style.display = 'none';
		$('.profile-right').css({
			float : 'left',
			height : 'auto',
			width : '100%'
		});

		employeesCurrentStatusdiv.style.display = 'none';
		
		$( "#deviceCurrentEmpStatusdiv" ).addClass( "deviceCurrentEmpStatusCenter" );
		
		$('#deviceCurrentEmpStatusdivMax').hide();
	    $('#deviceCurrentEmpStatusdivMin').show();

	});
});

$(document).ready(function() {
	var div1 = document.getElementById('div1');
	var div2 = document.getElementById('div2');
	var employeesCurrentStatusdiv = document.getElementById('employeesCurrentStatusdiv');
	
	$('#deviceCurrentEmpStatusdivMin').css({
		display : 'none',
	});
	
	$('.screenlet-title-bar ul li').css({
		border : '0',
	});
	
	$("#deviceCurrentEmpStatusdivMin").click(function() {
		$("#deviceCurrentEmpStatusdiv").animate({

			color : "#000",
		}, 100);

		div1.style.display = 'block';
		div2.style.display = 'block';
		employeesCurrentStatusdiv.style.display = 'block';
		$('.profile-right').css({
			float : 'right',
			width : '47%'
		});
		$('#deviceCurrentEmpStatusdivMax').show();
	    $('#deviceCurrentEmpStatusdivMin').hide();
	    $( ".deviceCurrentEmpStatusCenter" ).switchClass( "deviceCurrentEmpStatusCenter", "temp", 10 )
	});
});


function contessaPopup(title,containerId,url,height,width){
	
	
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


function contessaPopupAttenEdit(title,containerId,url,height,width){
	
	
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
				
						 /*For Employee Attendance Preference*/
						 $('#EmployeeAttendancePreferenceForm_entryTime').timepicker();
						 $('#EmployeeAttendancePreferenceForm_exitTime').timepicker();
						 $('#EmployeeAttendancePreferenceForm_lunchTime').timepicker();
						 $('#EmployeeAttendancePreferenceForm_maximumStayTime').timepicker();
						 $('#EmployeeAttendancePreferenceForm_lateEntryTime').timepicker();
						 $('#EmployeeAttendancePreferenceForm_oTStartTime').timepicker();
						 $('#EmployeeAttendancePreferenceForm_oTEndTime').timepicker();
						 $('#EmployeeAttendancePreferenceForm_extraOTStartTime').timepicker();
						 $('#EmployeeAttendancePreferenceForm_extraOTEndTime').timepicker();
						 
						 $('#EmployeeAttendancePreferenceForm_firstTiffinStartTime').timepicker();
						 $('#EmployeeAttendancePreferenceForm_firstTiffinEndTime').timepicker();
						 $('#EmployeeAttendancePreferenceForm_secondTiffinStartTime').timepicker();
						 $('#EmployeeAttendancePreferenceForm_secondTiffinEndTime').timepicker();
						 $('#EmployeeAttendancePreferenceForm_nightStartTime').timepicker();
						 $('#EmployeeAttendancePreferenceForm_nightEndTime').timepicker();
		
						 
						 /*For Employee Attendance Day Preference*/
						 $('#DayAttendancePreferenceForm_entryTime').timepicker();
						 $('#DayAttendancePreferenceForm_exitTime').timepicker();
						 $('#DayAttendancePreferenceForm_lunchTime').timepicker();
						 $('#DayAttendancePreferenceForm_maximumStayTime').timepicker();
						 $('#DayAttendancePreferenceForm_lateEntryTime').timepicker();
						 $('#DayAttendancePreferenceForm_oTStartTime').timepicker();
						 $('#DayAttendancePreferenceForm_oTEndTime').timepicker();
						 $('#DayAttendancePreferenceForm_extraOTStartTime').timepicker();
						 $('#DayAttendancePreferenceForm_extraOTEndTime').timepicker();
						 
						 $('#DayAttendancePreferenceForm_firstTiffinStartTime').timepicker();
						 $('#DayAttendancePreferenceForm_firstTiffinEndTime').timepicker();
						 $('#DayAttendancePreferenceForm_secondTiffinStartTime').timepicker();
						 $('#DayAttendancePreferenceForm_secondTiffinEndTime').timepicker();
						 $('#DayAttendancePreferenceForm_nightStartTime').timepicker();
						 $('#DayAttendancePreferenceForm_nightEndTime').timepicker();
						
						 /*For Employee Attendance Organization Preference*/
						 $('#OrganizationPreferenceForm_entryTime').timepicker();
						 $('#OrganizationPreferenceForm_exitTime').timepicker();
						 $('#OrganizationPreferenceForm_lunchTime').timepicker();
						 $('#OrganizationPreferenceForm_maximumStayTime').timepicker();
						 $('#OrganizationPreferenceForm_lateEntryTime').timepicker();
						 $('#OrganizationPreferenceForm_oTStartTime').timepicker();
						 $('#OrganizationPreferenceForm_oTEndTime').timepicker();
						 $('#OrganizationPreferenceForm_extraOTStartTime').timepicker();
						 $('#OrganizationPreferenceForm_extraOTEndTime').timepicker();
						 
						 $('#OrganizationPreferenceForm_firstTiffinStartTime').timepicker();
						 $('#OrganizationPreferenceForm_firstTiffinEndTime').timepicker();
						 $('#OrganizationPreferenceForm_secondTiffinStartTime').timepicker();
						 $('#OrganizationPreferenceForm_secondTiffinEndTime').timepicker();
						 $('#OrganizationPreferenceForm_nightStartTime').timepicker();
						 $('#OrganizationPreferenceForm_nightEndTime').timepicker();
						 $('#half_Day_Morning_Start').timepicker();
						 $('#half_Day_Morning_End').timepicker();
						 $('#half_Day_Evening_Start').timepicker();
						 $('#half_Day_Evening_End').timepicker();
						 
						 /*For Roster Preference*/
						 $('#RosterPreferenceForm_rosterStartTime').timepicker();
						 $('#RosterPreferenceForm_rosterEndTime').timepicker();
						 $('#RosterPreferenceForm_entryTime').timepicker();
						 $('#RosterPreferenceForm_exitTime').timepicker();
						 $('#RosterPreferenceForm_lunchTime').timepicker();
						 $('#RosterPreferenceForm_maximumStayTime').timepicker();
						 $('#RosterPreferenceForm_lateEntryTime').timepicker();
						 $('#RosterPreferenceForm_oTStartTime').timepicker();
						 $('#RosterPreferenceForm_oTEndTime').timepicker();
						 $('#RosterPreferenceForm_extraOTStartTime').timepicker();
						 $('#RosterPreferenceForm_extraOTEndTime').timepicker();
						 
						 $('#RosterPreferenceForm_firstTiffinStartTime').timepicker();
						 $('#RosterPreferenceForm_firstTiffinEndTime').timepicker();
						 $('#RosterPreferenceForm_secondTiffinStartTime').timepicker();
						 $('#RosterPreferenceForm_secondTiffinEndTime').timepicker();
						 $('#RosterPreferenceForm_nightStartTime').timepicker();
						 $('#RosterPreferenceForm_nightEndTime').timepicker();
						 	
							 
						 $('#entryTime').timepicker();
						 $('#exitTime').timepicker();
						 $('#lunchTime').timepicker();
						 $('#maximumStayTime').timepicker();
						 $('#lateEntryTime').timepicker();
						 $('#oTStartTime').timepicker();
						 $('#oTEndTime').timepicker();
						 $('#extraOTStartTime').timepicker();
						 $('#extraOTEndTime').timepicker();
						 $('#firstTiffinStartTime').timepicker();
						 $('#firstTiffinEndTime').timepicker();
						 $('#secondTiffinStartTime').timepicker();
						 $('#secondTiffinEndTime').timepicker();
						 $('#nightStartTime').timepicker();
						 $('#nightEndTime').timepicker();
		 
						 $('#lunchStartTime').timepicker();
						 $('#lunchEndTime').timepicker();
	
	
	
						
						/*For Attendance Employee Preference */
						NumericOnly("EmployeeAttendancePreferenceForm_oTCalculationOperand");
						NumericOnly("EmployeeAttendancePreferenceForm_bonusDeductionAmount");
						NumericOnly("EmployeeAttendancePreferenceForm_yearlyBonusAmount");
						NumericOnly("EmployeeAttendancePreferenceForm_montlyBonusAmount");
						NumericOnly("EmployeeAttendancePreferenceForm_lateEntryDaysForAttenBonusDisable");
						NumericOnly("EmployeeAttendancePreferenceForm_lateEntryDaysForAttenBonusDeduction");
						NumericOnly("EmployeeAttendancePreferenceForm_yearlyLateEntryDaysForAttenBonusDisable");
						NumericOnly("EmployeeAttendancePreferenceForm_yearlylateEntryDaysForAttenBonusDeduction");
						NumericOnly("EmployeeAttendancePreferenceForm_lunchDuration");
						
						/*For Attendance Day Preference */
						NumericOnly("DayAttendancePreferenceForm_oTCalculationOperand");
						NumericOnly("DayAttendancePreferenceForm_bonusDeductionAmount");
						NumericOnly("DayAttendancePreferenceForm_yearlyBonusAmount");
						NumericOnly("DayAttendancePreferenceForm_montlyBonusAmount");
						NumericOnly("DayAttendancePreferenceForm_lateEntryDaysForAttenBonusDisable");
						NumericOnly("DayAttendancePreferenceForm_lateEntryDaysForAttenBonusDeduction");
						NumericOnly("DayAttendancePreferenceForm_lunchDuration");
						
						/*For Attendance Organization Preference */
						NumericOnly("OrganizationPreferenceForm_montlyBonusAmount");
						NumericOnly("OrganizationPreferenceForm_YearlyBonusAmount");
						NumericOnly("OrganizationPreferenceForm_bonusDeductionAmount");
						NumericOnly("OrganizationPreferenceForm_oTCalculationOperand");
						NumericOnly("OrganizationPreferenceForm_lateEntryDaysForAttenBonusDisable");
						NumericOnly("OrganizationPreferenceForm_lateEntryDaysForAttenBonusDeduction");
						NumericOnly("OrganizationPreferenceForm_yearlyLateEntryDaysForAttenBonusDisable");
						NumericOnly("OrganizationPreferenceForm_yearlylateEntryDaysForAttenBonusDeduction");
						NumericOnly("OrganizationPreferenceForm_yearlyBonusAmount");
						NumericOnly("OrganizationPreferenceForm_lunchDuration");
						
						
						/*For Roster Preference */
						NumericOnly("RosterPreferenceForm_montlyBonusAmount");
						NumericOnly("RosterPreferenceForm_YearlyBonusAmount");
						NumericOnly("RosterPreferenceForm_bonusDeductionAmount");
						NumericOnly("RosterPreferenceForm_oTCalculationOperand");
						NumericOnly("RosterPreferenceForm_lateEntryForAttenBonusDisable");
						NumericOnly("RosterPreferenceForm_lateEntryForAttenBonusDeduction");
						NumericOnly("RosterPreferenceForm_lateEntryDaysForAttenBonusDisable");
						NumericOnly("RosterPreferenceForm_lateEntryDaysForAttenBonusDeduction");
						NumericOnly("RosterPreferenceForm_yearlyLateEntryDaysForAttenBonusDisable");
						NumericOnly("RosterPreferenceForm_yearlylateEntryDaysForAttenBonusDeduction");
						NumericOnly("RosterPreferenceForm_yearlyBonusAmount");
						
							
						NumericOnly("RosterPreferenceForm_lunchDuration");
						
						NumericOnly("firstTiffinRate");
						NumericOnly("secondTiffinRate");
						NumericOnly("nightRate");
						NumericOnly("attBonDeductAmnt");
						NumericOnly("confirmationAfter");
						NumericOnly("lateEntryDaysForAttenBonusDisable");
						NumericOnly("lateEntryDaysForAttenBonusDeduction");
						NumericOnly("yearlyLateEntryDaysForAttenBonusDisable");
						NumericOnly("yearlylateEntryDaysForAttenBonusDeduction");
						NumericOnly("montlyBonusAmount");
						NumericOnly("yearlyBonusAmount");
						NumericOnly("bonusDeductionAmount");
						NumericOnly("oTCalculationOperand");
						
						NumericOnly("leOrAbsForattbnDis");
						NumericOnly("halfLesForattbnDed");
						NumericOnly("lunchDuration");
			
					//});
				}
			});
		}
	
	});
	

}

$(document).ready(function() {
	$("#addEmpPreferenceIcon").click(function() {
		contessaPopupAttenEdit('Add Employee Preference','#editEmployeePreference','EditEmpPreference?&title=Add Employee Preference','650','650');
	});
});

$(document).ready(function() {
	/*	$("#FindDailyLateReport1_fromTime").click(function() {*/
			$('#FindDailyLateReport1_fromTime').timepicker({ 'timeFormat': 'H:i' });
			$('#FindDailyLateReport1_toTime').timepicker({ 'timeFormat': 'H:i' });
	/*	});*/
	});

$(document).ready(function() {
	$("#addDayPreferenceIcon").click(function() {
		contessaPopupAttenEdit('Add Day Preference','#editDayPreference','EditDayPreference?&title=Add Day Preference','540','540');
		
	});
});

$(document).ready(function() {
	$("#addOrgPreferenceIcon").click(function() {
		contessaPopupAttenEdit('Add Organization Preference','#editOrgPreference','EditOrgPreference?&title=Add Organization Preference','650','650');
		
	});
});

$(document).ready(function() {
	$("#addRosPreferenceIcon").click(function() {
		contessaPopupAttenEdit('Add Roster Preference','#editRosterPreference','EditRosterPreference?&title=Add Roster Preference','650','650');
		
	});
});

$(document).ready(function() {
	$("#addDesignationPreferenceIcon").click(function() {
		contessaPopupAttenEdit('Add Designation Preference','#editDesignationPreference','AddDesignationPreference','650','650');
		
	});
});

$(document).ready(function() {
	$("#addEmployeeIcon").click(function() {
		var rosterPreferenceId = document.getElementById('RosterPreIdForm_rosterPreferenceId').value;
		var rosterPrefRevision = document.getElementById('RosterPreIdForm_rosterPrefRevision').value;
		var rosterName = document.getElementById('RosterPreIdForm_rosterName').value;
		var url= "AddEmployeeIntoRoster?rosterPreferenceId="+rosterPreferenceId+"&rosterPrefRevision="+rosterPrefRevision+"&rosterName="+rosterName;
	
		contessaPopup('Add Employee Into Roster','#addRosterEmpContainer',url,'310','450');
		
	});
});


$(document).ready(function() {
	$("#addPositionIcon").click(function() {
		var designationPreferenceId = document.getElementById('DesignationPreIdForm_designationPreferenceId').value;
		var url= "AddPositionIntoDesignationPreference?designationPreferenceId="+designationPreferenceId;
		contessaPopup('Add Postion Into Designation Preference','#addPosition',url,'310','450');
		
	});
});

$(document).ready(function() {
	$("#addBatchPositionIcon").click(function() {
		var designationPreferenceId = document.getElementById('DesignationPreIdForm_designationPreferenceId').value;
		var url= "AddBatchPositionIntoDesignationPreference?designationPreferenceId="+designationPreferenceId;
		contessaPopup('Add Bacth Position Into Designation Preference','#addBatchPosition',url,'310','450');
		
	});
});


$(document).ready(function() {
	$('#a').datepicker( {
	    changeMonth: true,
	    changeYear: true,
	    showButtonPanel: true,
	    
	    dateFormat: 'MM yy',
	    onClose: function(dateText, inst) { 
	        var month = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
	        var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
	        $(this).datepicker('setDate', new Date(year, month, 1));
	    }
	
	
	});

	$('#a').datepicker('widget').addClass( "ui-month-only" );
	

});
$(document).ready(function() {
	$('input[name="deviceName"]').change(function(e) {
	    alert( $(this).val() ); 
	    alert( $('input[name="deviceName"]').val() ); 
	});


});

$(document).ready(function() {
});
function getFieldsForAddDevice(deviceNameParam){
	//alert(document.getElementById('pegasusAddress'));	
	if(deviceNameParam=='Pegasus'){
		$('#pegasusAddress').attr('disabled', false);
		$('#terminalType').attr('disabled', true);
		$('#firmewareVersion').attr('disabled', true);
		$('#fliVersion').attr('disabled', true);
		$('#userName').attr('disabled', true);
		$('#userPass').attr('disabled', true);
		$('#endPointIP').attr('disabled', true);
		$('#agentVersion').attr('disabled', true);
		$('#type').attr('disabled', true);
	}
	if(deviceNameParam=='Actatek'){
		$('#pegasusAddress').attr('disabled', true);
		$('#terminalType').attr('disabled', false);
		$('#firmewareVersion').attr('disabled', false);
		$('#fliVersion').attr('disabled', false);
		$('#userName').attr('disabled', false);
		$('#userPass').attr('disabled', false);
		$('#endPointIP').attr('disabled', false);
		$('#agentVersion').attr('disabled', false);
		$('#type').attr('disabled', false);
	}
}

$(document).ready(function() {
	$("#timelinePopup").click(function() {
		//var custRequestId = document.getElementById('FilterCalendarEvents_custRequestId').value;
		var orderId = document.getElementById('FilterCalendarEvents_orderId').value;
	
		$("#timelineContainer").dialog({
			autoOpen : true,
			title : "Time Line View",
			height : 700,
			width : 1000,
			modal : true,
			open : function() {
				$.ajax({
					//url : "TimelinePopup?custRequestId="+custRequestId+"&orderId="+orderId,
					url : "TimelinePopup?orderId="+orderId,
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#timelineContainer").html(data);
					}
				});
			
			     $('#eventTimeline').b1njTimeline({
			                'height' : 550
			      });
	       		
			}

		});
	});
});


$(document).ready(function() {
	
	NumericOnly("chargeAmount");
	
});


function EmployeeAttendanceCommentsPopUp(partyId,dateOfAttendance,employeeComments){
	$("#addAttendanceCommentsPopUp").dialog({		
		autoOpen : true,
		title : "Form",
		height : 400,
		width : 600,
		modal : true,
		open : function() {
			$.ajax({
				url : "EmployeeAttendanceCommentsPopUp?partyId="+partyId+'&dateOfAttendance='+dateOfAttendance+'&employeeComments='+employeeComments,

				type : "POST",
				data : getRequestData(),
				success : function(data) {
					$("#addAttendanceCommentsPopUp").html(data);
				}
			});
		}

	});
}



function SupervisorAttendanceCommentsPopUp(partyId,employeeId,dateOfAttendance,firstInTime,lastOutTime,attendanceStatus,attendanceResult,employeeComments,supervisorComments){
	$("#addSupervosorCommentsPopUp").dialog({		
		autoOpen : true,
		title : "Form",
		height : 600,
		width : 700,
		modal : true,
		open : function() {
			$.ajax({
				url : "SupervisorAttendanceCommentsPopUp?partyId="+partyId+'&employeeId='+employeeId+'&dateOfAttendance='+dateOfAttendance+'&firstInTime='+firstInTime+'&lastOutTime='+lastOutTime+'&attendanceStatus='+attendanceStatus+'&attendanceResult='+attendanceResult+'&employeeComments='+employeeComments+'&supervisorComments='+supervisorComments,

				type : "POST",
				data : getRequestData(),
				success : function(data) {
					$("#addSupervosorCommentsPopUp").html(data);
				}
			});
		}

	});
}


