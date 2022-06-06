$(document).ready(function() {
	$("#addPerfApprBusinessKPIBtn").click(function() {
		var partyId=$("#PerfApprHead_partyId").val();
		var processId=$("#PerfApprHead_processId").val();
		$("#addPerfApprBusinessKPIPopUp").dialog({
			autoOpen : true,
			title : "Form",
			height : 600,
			width : 1000,
			modal : true,
			open : function() {
				$.ajax({
					url : "CreatePerfApprBusinessKPI?partyId="+partyId+"&processId="+processId,
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#addPerfApprBusinessKPIPopUp").html(data);
					}
				});
			}

		});
	});
	$("#addPerfApprBehavioralKPIBtn").click(function() {
		var partyId=$("#PerfApprHead_partyId").val();
		var processId=$("#PerfApprHead_processId").val();
		$("#addPerfApprBehavioralKPIAddPopUp").dialog({
			autoOpen : true,
			title : "Form",
			height : 600,
			width : 1000,
			modal : true,
			open : function() {
				$.ajax({
					url : "NewBehaviourKPI?partyId="+partyId+"&processId="+processId,
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#addPerfApprBehavioralKPIAddPopUp").html(data);
					}
				});
			}

		});
	});
	$("#addPerfApprDevelopmentPlanBtn").click(function() {
		var partyId=$("#PerfApprHead_partyId").val();
		var processId=$("#PerfApprHead_processId").val();
		$("#addPerfApprDevelopmentPlanPopUp").dialog({
			autoOpen : true,
			title : "Form",
			height : 600,
			width : 1000,
			modal : true,
			open : function() {
				$.ajax({
					url : "NewDevelopmentPlan?partyId="+partyId+"&processId="+processId,
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#addPerfApprDevelopmentPlanPopUp").html(data);
					}
				});
			}

		});
	});
	
});