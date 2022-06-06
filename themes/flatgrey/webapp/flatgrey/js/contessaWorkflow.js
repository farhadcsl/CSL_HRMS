$(document).ready(function() {
	
	
	$("#addProcessStepBtn").click(function() {
		var processStrId=document.getElementById('DetailsLeaveProcessStructureSetup_processStrId').value;
		var processName=document.getElementById('DetailsLeaveProcessStructureSetup_processName').value;
		$("#LinkForAddProcessStepPopUp").dialog({
			autoOpen : true,
			title : "New Process Step For [ "+processName+" ]",
			height : 450,
			width : 600,
			modal : true,
			open : function() {
				$.ajax({
					url : "NewLeaveProcessStepSetup?processStrId="+processStrId,
					type : "POST",
					data : getRequestData(),
					success : function(data) {						
						$("#LinkForAddProcessStepPopUp").html(data);
					}
				});
			}
		});
	});

	$("#addPerfApprProcessStepBtn").click(function() {
		var processStrId=document.getElementById('DetailsPerfApprProcessStructureSetup_processStrId').value;
		var processName=document.getElementById('DetailsPerfApprProcessStructureSetup_processName').value;
		$("#LinkForAddPerfApprProcessStepPopUp").dialog({
			autoOpen : true,
			title : "New Process Step For [ "+processName+" ]",
			height : 450,
			width : 600,
			modal : true,
			open : function() {
				$.ajax({
					url : "NewPerfApprProcessStepSetup?processStrId="+processStrId,
					type : "POST",
					data : getRequestData(),
					success : function(data) {						
						$("#LinkForAddPerfApprProcessStepPopUp").html(data);
					}
				});
			}
		});
	});
});