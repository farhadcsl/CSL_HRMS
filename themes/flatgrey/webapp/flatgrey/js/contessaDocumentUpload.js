//For Terminate Position PopUp Button
$(document).ready(function() {
	$("#terminateBtn").click(function() { //ImageBtn ID into the menu
		$("#terminateDisplay").dialog({ //Id where PopUp display
			autoOpen : true,
			title : "&nbsp;",
			height : 400,
			width : 600,
			modal : true,
			open : function() {
				$.ajax({
					url : "terminate?partyId="+partyId, //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#terminateDisplay").html(data);
					}
				});
			}

		});
	});
	
	$("#changeEmployeePositionBtn").click(function() { //ImageBtn ID into the menu
		var currentPosition=$("#emplPositionIdForTree").val();
		var employeeCurrentPositionInfo=$("#employeeCurrentPositionInfo").val();
		var employeeJoiningDate=$("#EmployeeJoiningDate").val();
		$("#changeEmployeePositionDisplay").dialog({ //Id where PopUp display
			autoOpen : true,
			title : "&nbsp;",
			height : 400,
			width : 600,
			modal : true,
			open : function() {
				$.ajax({
					url : "ChangeEmployeePosition?partyId="+partyId+'&currentPosition='+currentPosition+'&employeeCurrentPositionInfo='+employeeCurrentPositionInfo+'&employeeJoiningDate='+employeeJoiningDate, //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#changeEmployeePositionDisplay").html(data);
					}
				});
			}
		});
	});
});

//For Move Position PopUp Button
$(document).ready(function() {
	$("#movePositionBtn").click(function() {
		
		var unitMovePositionPopUp = document.getElementById('unitMovePositionPopUp');
		var movePositionPopUp = document.getElementById('movePositionPopUp');
		
		if (movePositionPopUp != null){ 
		
				$("#movePositionPopUp").dialog({
					autoOpen : true,
					title : "&nbsp;",
					height : 200,
					width : 480,
					modal : true,
					open : function() {
						$.ajax({
							url : "PositionMovePopUp", //This URL view request map
							type : "POST",
							data : getRequestData(),
							success : function(data) {
								$("#movePositionPopUp").html(data);
							}
						});
					}
		
				});
		}else if(unitMovePositionPopUp != null){
				$("#unitMovePositionPopUp").dialog({
					autoOpen : true,
					title : "&nbsp;",
					height : 200,
					width : 480,
					modal : true,
					open : function() {
						$.ajax({
							url : "UnitPositionMovePopUp", //This URL view request map
							type : "POST",
							data : getRequestData(),
							success : function(data) {
								$("#unitMovePositionPopUp").html(data);
							}
						});
					}
		
				});
	}
		
	});
});
//For Add Position PopUp Button
$(document).ready(function() {
	$("#addPositionBtn").click(function() {
	
		
		var unitAddPositionPopUp = document.getElementById('unitAddPositionPopUp');
		var addPositionPopUp = document.getElementById('addPositionPopUp');
		
		if (unitAddPositionPopUp != null){ 	
					$("#unitAddPositionPopUp").dialog({
						autoOpen : true,
						title : "&nbsp;",
						height : 275,
						width : 500,
						modal : true,
						open : function() {
							$.ajax({
								url : "UnitPositionAddPopUp", //This URL view request map
								type : "POST",
								data : getRequestData(),
								success : function(data) {
									$("#unitAddPositionPopUp").html(data);
								}
							});
						}
			
					});
			}else if(addPositionPopUp != null){
				
				$("#addPositionPopUp").dialog({
					autoOpen : true,
					title : "&nbsp;",
					height : 275,
					width : 500,
					modal : true,
					open : function() {
						$.ajax({
							url : "PositionAddPopUp", //This URL view request map
							type : "POST",
							data : getRequestData(),
							success : function(data) {
								$("#addPositionPopUp").html(data);
							}
						});
					}
		
				});
				
			}
	});
});

//For Add OrganizationHead PopUp Button
$(document).ready(function() {
	$("#addOrganizationHeadAssignBtn").click(function() {
	
		
		var addOrganizationHeadAssignPopUp = document.getElementById('addOrganizationHeadAssignPopUp');
		
		if(addOrganizationHeadAssignPopUp != null){
				
				$("#addOrganizationHeadAssignPopUp").dialog({
					autoOpen : true,
					title : "&nbsp;",
					height : 275,
					width : 500,
					modal : true,
					open : function() {
						$.ajax({
							url : "OrganizationHeadAssignAddPopUp", //This URL view request map
							type : "POST",
							data : getRequestData(),
							success : function(data) {
								$("#addOrganizationHeadAssignPopUp").html(data);
							}
						});
					}
		
				});
				
			}
	});
});

$(document).ready(function() {
	$("#addCompanyIcon").click(function() {
		$("#addCompanyPopUp").dialog({
			autoOpen : true,
			title : "Add Form",
			height : 245,
			width : 500,
			modal : true,
			open : function() {
				$.ajax({
					url : "CompanyAddPopUp",
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#addCompanyPopUp").html(data);
					}
				});
			}

		});
	});
});


$(document).ready(function() {
	$("#addPaygradeIcon").click(function() {
		$("#addPaygradePopUp").dialog({
			autoOpen : true,
			title : "Add PayGrade",
			height : 295,
			width : 500,
			modal : true,
			open : function() {
				//$('#ControlName').val(emplPositionId);
				$.ajax({
					url : "PaygradeAddPopUp?emplPositionId="+document.getElementById('PositionInfo_emplPositionId').value,
					type : "POST",
					data : getRequestData(),
					
					success : function(data) {
						$("#addPaygradePopUp").html(data);
					}
				});
			}

		});
	});
});
$(document).ready(function() {
	$("#addDocumentGroup").click(function() {
		$("#addDocumentPopUp").dialog({
			autoOpen : true,
			title : "Add Document",
			height : 250,
			width : 500,
			modal : true,
			open : function() {
				$.ajax({
					url : "uploadContent",
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#addDocumentPopUp").html(data);
					}
				});
			}

		});
	});
});


$(document).ready(function() {
	$("#addDocumentBudget").click(function() {
		$("#addDocumentBudgetPopUp").dialog({
			autoOpen : true,
			title : "Add Document",
			height : 250,
			width : 500,
			modal : true,
			open : function() {
				$.ajax({
					url : "uploadBudgetContent?emplPositionId="+document.getElementById('PositionInfo_emplPositionId').value,
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#addDocumentBudgetPopUp").html(data);
					}
				});
			}

		});
	});
});

$(document).ready(function() {
	$("#addDocumentRequisition").click(function() {
		$("#addDocumentRequisitionPopUp").dialog({
			autoOpen : true,
			title : "Add Document",
			height : 250,
			width : 500,
			modal : true,
			open : function() {
				$.ajax({
					url : "uploadRequisitionContent?jobRequisitionId="+document.getElementById('detailofRequisition_jobRequisitionId').value,
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#addDocumentRequisitionPopUp").html(data);
					}
				});
			}

		});
	});
});

$(document).ready(function() {
	$("#addDocumentcvRequisition").click(function() {
		$("#addDocumentcvRequisitionPopUp").dialog({
			autoOpen : true,
			title : "Add Document",
			height : 250,
			width : 500,
			modal : true,
			open : function() {
				$.ajax({
					url : "uploadcvRequisitionContent?cvBankId="+document.getElementById('ViewCvBank_cvBankId').value,
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#addDocumentcvRequisitionPopUp").html(data);
					}
				});
			}

		});
	});
});


$(document).ready(function() {
	$("#addDocumentLC").click(function() {
		$("#addLCDocumentPopUp").dialog({
			autoOpen : true,
			title : "Add LC Document",
			height : 250,
			width : 500,
			modal : true,
			open : function() {
				$.ajax({
					url : "uploadLcContent?lcId="+document.getElementById('viewLc_lcLcId').value,
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#addLCDocumentPopUp").html(data);
					}
				});
			}

		});
	});
});

$(document).ready(function() {
	$("#addDocumentUnit").click(function() {
		$("#addDocumentUnitPopUp").dialog({
			autoOpen : true,
			title : "Add Document",
			height : 250,
			width : 500,
			modal : true,
			open : function() {
				$.ajax({
					url : "uploadUnitContent",
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#addDocumentUnitPopUp").html(data);
					}
				});
			}

		});
	});
});

//added assign paygradeBenefit
$(document).ready(function() {
	$("#assignPaygradeBenfit").click(function() {
		$("#assignPaygradeBenfitPopUp").dialog({
			autoOpen : true,
			title : "Assign Paygrade Benefit",
			height : 300,
			width : 400,
			modal : true,
			open : function() {
				$.ajax({
					url : "AssignPaygradeBenfitAddPopUp?payGradeId="+document.getElementById('PaygradeInfo_payGradeId').value+"&emplPositionId="+document.getElementById('PaygradeInfo_emplPositionId').value, //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#assignPaygradeBenfitPopUp").html(data);
					}
				});
			}

		});
	});
});

//added assign pay grade deduction
$(document).ready(function() {
	$("#assignPaygradeDeduction").click(function() {
		$("#assignPaygradeDeductionPopUp").dialog({
			autoOpen : true,
			title : "Assign Paygrade Deduction",
			height : 280,
			width : 400,
			modal : true,
			open : function() {
				$.ajax({
					url : "AssignPaygradeDeductionAddPopUp?payGradeId="+document.getElementById('PaygradeInfo_payGradeId').value+"&emplPositionId="+document.getElementById('PaygradeInfo_emplPositionId').value, //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#assignPaygradeDeductionPopUp").html(data);
					}
				});
			}

		});
	});
});
//added assign pay grade increments
$(document).ready(function() {
	$("#addPaygradeIncrements").click(function() {
		$("#addPayGradeIncrementsPopUp").dialog({
			autoOpen : true,
			title : "Add Paygrade Increments",
			height : 210,
			width : 400,
			modal : true,
			open : function() {
				$.ajax({
					url : "AddPaygradeIncrementsPopUp?payGradeId="+document.getElementById('PaygradeInfo_payGradeId').value+"&emplPositionId="+document.getElementById('PaygradeInfo_emplPositionId').value, //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#addPayGradeIncrementsPopUp").html(data);
					}
				});
			}

		});
	});
});

//assign Benefit from increments
$(document).ready(function() {
	$("#assignIncrementBenfit").click(function() {
		$("#assignIncrementBenfitPopUp").dialog({
			autoOpen : true,
			title : "Assign Increment Benefit",
			height : 300,
			width : 400,
			modal : true,
			open : function() {
				$.ajax({
					url : "AssignIncrementBenfitAddPopUp?salaryStepSeqId="+document.getElementById('SalaryStepInfo_salaryStepSeqId').value+"&payGradeId="+document.getElementById('SalaryStepInfo_payGradeId').value+"&emplPositionId="+document.getElementById('SalaryStepInfo_emplPositionId').value, //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#assignIncrementBenfitPopUp").html(data);
					}
				});
			}

		});
	});
});
//assign increment deduction
$(document).ready(function() {
	$("#assignIncrementDeduction").click(function() {
		$("#assignIncrementDeductionPopUp").dialog({
			autoOpen : true,
			title : "Assign Increment Deduction",
			height : 300,
			width : 400,
			modal : true,
			open : function() {
				$.ajax({
					url : "AssignIncrementDeductionAddPopUp?salaryStepSeqId="+document.getElementById('SalaryStepInfo_salaryStepSeqId').value+"&payGradeId="+document.getElementById('SalaryStepInfo_payGradeId').value+"&emplPositionId="+document.getElementById('SalaryStepInfo_emplPositionId').value, //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#assignIncrementDeductionPopUp").html(data);
					}
				});
			}

		});
	});
});
//assign increment person
$(document).ready(function() {
	$("#assignIncrementPerson").click(function() {
		$("#assignIncrementPersonPopUp").dialog({
			autoOpen : true,
			title : "Assign Increment for Person",
			height : 150,
			width : 400,
			modal : true,
			open : function() {
				$.ajax({
					url : "AssignassignIncrementPersonAddPopUp?salaryStepSeqId="+document.getElementById('SalaryStepInfo_salaryStepSeqId').value+"&payGradeId="+document.getElementById('SalaryStepInfo_payGradeId').value+"&emplPositionId="+document.getElementById('SalaryStepInfo_emplPositionId').value, //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#assignIncrementPersonPopUp").html(data);
					}
				});
			}

		});
	});
});
//assign increment person
$(document).ready(function() {
	$("#assignFulfillmentPerson").click(function() {
		$("#assignFulfillmentPersonPopUp").dialog({
			autoOpen : true,
			title : "Assign Fulfillment for Person",
			height : 250,
			width : 500,
			modal : true,
			open : function() {
				$.ajax({
					url : "AssignFulfillmentPersonAddPopUp?emplPositionId="+document.getElementById('PositionInfo_emplPositionId').value, //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#assignFulfillmentPersonPopUp").html(data);
					}
				});
			}

		});
	});
});
//For Add reports to PopUp Button
$(document).ready(function() {
	$("#addReportingStructBtn").click(function() {
		$("#addReportingStruct").dialog({
			autoOpen : true,
			title : "&nbsp;",
			height : 210,
			width : 450,
			modal : true,
			open : function() {
				$.ajax({
					url : "ReportingStructAddPopUp?emplPositionId="+document.getElementById('PositionInfo_emplPositionId').value, //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#addReportingStruct").html(data);
					}
				});
			}

		});
	});
});
//For Add responsibilities PopUp Button
$(document).ready(function() {
	$("#addOrgPositionResponsibilityBtn").click(function() {
		$("#addOrgPositionResponsibility").dialog({
			autoOpen : true,
			title : "&nbsp;",
			height : 210,
			width : 450,
			modal : true,
			open : function() {
				$.ajax({
					url : "EmplPositionResponsibilityAddPopUp?emplPositionId="+document.getElementById('PositionInfo_emplPositionId').value, //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#addOrgPositionResponsibility").html(data);
					}
				});
			}

		});
	});
});
//create order from quotation
$(document).ready(function() {
	$("#createProttaySalesOrder").click(function() {
		$("#saleOrderPopup").dialog({
			autoOpen : true,
			title : "Add Quantity",
			height : 225,
			width : 500,
			modal : true,
			open : function() {
				$.ajax({
					url : "addQuantityPopUp?quoteId="+document.getElementById('ProfileQuote_quoteId').value,
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#saleOrderPopup").html(data);
					}
				});
			}

		});
	});
});

function getRequestData() {
	var data = {
		"test" : "data"
	};
	return data;
};




//For validate Employee Information PopUp Button
$(document).ready(function() {
	$("#validateEmployeeBtn").click(function() { //ImageBtn ID into the menu
		$("#validateEmployeeDisplay").dialog({ //Id where PopUp display
			autoOpen : true,
			title : "&nbsp;",
			height : 150,
			width : 350,
			modal : true,
			open : function() {
				$.ajax({
					url : "validateEmployee?partyId="+partyId, //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#validateEmployeeDisplay").html(data);
					}
				});
			}

		});
	});
});




//A function for get value from browser URL
function gup( name )
{
  name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
  var regexS = "[\\?&]"+name+"=([^&#]*)";
  var regex = new RegExp( regexS );
  var results = regex.exec( window.location.href );
  if( results == null )
    return "";
  else
    return results[1];
}
//Variables for URL 
var payGradeId = gup( 'payGradeId' );
var emplPositionId = gup( 'emplPositionId' );
var salaryStepSeqId = gup ( 'salaryStepSeqId' );
var partyId = gup ( 'partyId' );
var quoteId = gup ( 'partyId' );

