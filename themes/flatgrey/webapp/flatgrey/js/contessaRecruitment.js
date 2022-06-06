$(document).ready(function() {
	$("#addCvBankNotesBtn").click(function() {
		var cvBankId=$("#ViewCvBank_cvBankId").val();
		$("#addCvBankNotesPopUp").dialog({
			autoOpen : true,
			title : "Note",
			height : 350,
			width : 550,
			modal : true,
			open : function() {
				$.ajax({
					//url : "RequisitionAddPopUp?emplPositionId="+emplPositionId, //This URL view request map
					
					url : "addCvBankNotes?cvBankId="+cvBankId,

					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#addCvBankNotesPopUp").html(data);
					}
				});
			}

		});
	});
	
	$("#addRequisitionBtn").click(function() {
		$("#addRequisitionPopUp").dialog({
			autoOpen : true,
			title : "Form",
			height : 600,
			width : 800,
			modal : true,
			open : function() {
				$.ajax({
					//url : "RequisitionAddPopUp?emplPositionId="+emplPositionId, //This URL view request map
					
					url : "RequisitionAddPopUp?emplPositionId="+document.getElementById('PositionInfo_emplPositionId').value,

					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#addRequisitionPopUp").html(data);
					}
				});
			}

		});
	});
	
	
	$("#addSuperiorSettingsBtn").click(function() {
		$("#addSuperiorSettings").dialog({
			autoOpen : true,
			title : "Form",
			height : 250,
			width : 300,
			modal : true,
			open : function() {
				$.ajax({
					//url : "RequisitionAddPopUp?emplPositionId="+emplPositionId, //This URL view request map
					
					url : "EmployeeSuperiorSettingspopup?partyId="+partyId,

					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#addSuperiorSettings").html(data);
					}
				});
			}

		});
	});
	$("#addApplicantIncrementBtn").click(function() {
		$("#addApplicantIncrementPopUp").dialog({
			autoOpen : true,
			title : "Form",
			height : 300,
			width : 400,
			modal : true,
			open : function() {
				$.ajax({
					url : "ApplicantIncAddPopUp?partyId="+document.getElementById('ApplicantSalaryNegotiation_partyId').value + "&salaryTemplateId="+document.getElementById('ApplicantSalaryNegotiation_applicantsalaryTemplateId').value,
					type : "POST",
					data : getRequestData(),
					success : function(data) {						
						$("#addApplicantIncrementPopUp").html(data);
					}
				});
			}
		});
	});
	$("#addApplicantSalaryBtn").click(function() {
		var partyId=document.getElementById('ApplicantSalaryNegotiationView_partyId').value;
		var payGradeId=document.getElementById('ApplicantSalaryNegotiationView_payGradeIdHide').value;
		var levelId=document.getElementById('ApplicantSalaryNegotiationView_levelIdHide').value;
		var salaryTemplateId=document.getElementById('ApplicantSalaryNegotiationView_salaryTemplateId').value;
		var basicSalaryStepId=document.getElementById('ApplicantSalaryNegotiationView_basicSalaryStepId').value;
		var dateAdded=document.getElementById('ApplicantSalaryNegotiationView_dataAddedTime').value;
		var actionType="Edit";
		if(dateAdded ==""){
			actionType="Add";
		}
		var url="ApplicantSalaryNegotiationModify?partyId="+partyId+"&payGradeId="+payGradeId+"&levelId="+levelId+"&salaryTemplateId="+salaryTemplateId+"&basicSalaryStepId="+basicSalaryStepId+"&dateAdded="+dateAdded+"&actionType="+actionType;
		$("#addApplicantSalaryPopUp").dialog({
			autoOpen : true,
			height : 500,
			width : 600,
			modal : true,
			open : function() {
				$.ajax({
					url : url,
					//url : "ApplicantSalaryNegotiationModify?partyId="+partyId,
					type : "POST",
					data : getRequestData(),
					success : function(data) {	
						$("#addApplicantSalaryPopUp").html(data);
					}
				});
			}
		});
	});
	//--------------------Payroll----------------------------//

	$("#addSalaryTemplateDetailsBtn").click(function() {
		$("#addSalaryTemplateDetailsPopUp").dialog({
			autoOpen : true,
			title : "Form",
			height : 500,
			width : 650,
			modal : true,
			open : function() {
				$.ajax({
					url : "EditSalaryTemplateDetails?salaryTemplateId="+document.getElementById('ViewSalaryTemplate_salaryTemplateId').value,
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						
						$("#addSalaryTemplateDetailsPopUp").html(data);
					}
				});
			}

		});
	});
	
	
	$("#addNewSalaryItemConte").click(function() {
		var partyId=document.getElementById('EmployeeInfo_partyId').value;
		var processId=document.getElementById('EmployeeInfo_processId').value;
		var monthNumber=document.getElementById('EmployeeInfo_monthNumber').value;
		var year=document.getElementById('EmployeeInfo_year').value;
		var urlPath="NewSalaryItemModify?partyId="+partyId+"&processId="+processId+"&monthNumber="+monthNumber+"&year="+year;
		
		$("#addSalaryItemPopUp").dialog({
			autoOpen : true,
			title : "Add New Benefit Item",
			height : 500,
			width : 650,
			modal : true,
			open : function() {
				$.ajax({
					url : urlPath,
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						
						$("#addSalaryItemPopUp").html(data);
					}
				});
			}

		});
	});
	
	
	

	$("#addNewSalaryDDItemConte").click(function() {
		var partyId=document.getElementById('EmployeeInfo_partyId').value;
		var processId=document.getElementById('EmployeeInfo_processId').value;
		var monthNumber=document.getElementById('EmployeeInfo_monthNumber').value;
		var year=document.getElementById('EmployeeInfo_year').value;
		var urlPath="NewSalaryDDItemModify?partyId="+partyId+"&processId="+processId+"&monthNumber="+monthNumber+"&year="+year;
		
		$("#addSalaryDDItemPopUp").dialog({
			autoOpen : true,
			title : "Add New Deduction Item",
			height : 500,
			width : 650,
			modal : true,
			open : function() {
				$.ajax({
					url : urlPath,
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						
						$("#addSalaryDDItemPopUp").html(data);
					}
				});
			}

		});
	});

	$("#screenlet_1_col").click(function() {
		$("#updateEmpTempSalaryItemPopUp").dialog({
			autoOpen : true,
			title : "Form",
			height : 500,
			width : 650,
			modal : true,
			open : function() {
				$.ajax({
					url : "EditEmpTempSalaryItem?partyId="+document.getElementById('ViewSalaryTemplate_salaryTemplateId').value,
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						
						$("#updateEmpTempSalaryItemPopUp").html(data);
					}
				});
			}

		});
	});
	
});


function approveRequisitionForm(form, value){
	
	if (value == ""){
		alert("Please select an action.");
	}else{
		var formName = form.name;
	    form.submit(); 
	}
}

function updateRequisition(jobRequisitionId){
	$("#addRequisitionPopUp").dialog({
		autoOpen : true,
		title : "Form",
		height : 800,
		width : 600,
		modal : true,
		open : function() {
			$.ajax({
				url : "RequisitionAddPopUp?jobRequisitionId="+jobRequisitionId,

				type : "POST",
				data : getRequestData(),
				success : function(data) {
					$("#addRequisitionPopUp").html(data);
				}
			});
		}

	});
}

function updateAttendanceComments(partyId){
	$("#addAttendanceCommentsPopUp").dialog({		
		autoOpen : true,
		title : "Form",
		height : 400,
		width : 600,
		modal : true,
		open : function() {
			$.ajax({
				url : "AttendanceCommentsPopUp?partyId="+partyId,

				type : "POST",
				data : getRequestData(),
				success : function(data) {
					$("#addAttendanceCommentsPopUp").html(data);
				}
			});
		}

	});
}



function showRequisitionDetail(jobRequisitionId){
	
	$("#addRequisitionPopUp").dialog({
		autoOpen : true,
		title : "Form",
		height : 300,
		width : 400,
		modal : true,
		open : function() {
			$.ajax({
				url : "RequisitionDetail?emplPositionId="+emplPositionId + "&jobRequisitionId="+jobRequisitionId, //This URL view request map
				type : "POST",
				data : getRequestData(),
				success : function(data) {
					$("#addRequisitionPopUp").html(data);
				}
			});
		}

	});
}
$(document).ready(function() {
	$("#discardBtn").click(function() { //ImageBtn ID into the menu
		$("#discardBtnPopUp").dialog({ //Id where PopUp display
			autoOpen : true,
			title : "&nbsp;",
			height : 150,
			width : 350,
			modal : true,
			open : function() {
				$.ajax({
					url : "discardApplicant?partyId="+partyId, //This URL view request map
					type : "POST",
					data : getRequestData(),
					success : function(data) {
						$("#discardBtnPopUp").html(data);
					}
				});
			}

		});
	});
});

function selectJobRequisition() {
	
	 var jobSelect = document.getElementById("EditJobPosting_jobRequisitionId");
	 var jobDescriptuion = jobSelect.options[jobSelect.selectedIndex].text;
	 var jobTitle = jobDescriptuion.split("[");
	 
	document.getElementById("EditJobPosting_jobTitle").value = jobTitle[0];

}

function ageValidationFromBirthDate(form, value) {
	 var now = new Date();
	    var birthdate = value.split("-");
	    var born = new Date(birthdate[0], birthdate[1]-1, birthdate[2]);
	    age=get_age(born,now);
	    console.log(birthdate[2]+" : "+birthdate[1]+" : "+birthdate[0]);
	    console.log(age);

	    if (age<=18)
	    {
	    	alert("Age should be greater then or equal to 18");
	    	var field= document.getElementById('EditProttayApplicant_birthDate_i18n');
	    	document.getElementById("EditProttayApplicant_birthDate_i18n").value = field.defaultValue;
	       
	        return false;
	    }

}

function applicantFromValidation() {
	
	 var now = new Date();
	 var  value= document.getElementById("EditConessaApplicant_birthDate_i18n").value;

	    var birthdate = value.split("/");
	    var born = new Date(birthdate[2], birthdate[1]-1, birthdate[0]);
	    age=get_age(born,now);
	    console.log(birthdate[2]+" : "+birthdate[1]+" : "+birthdate[0]);
	    console.log(age);

	    if (age<18)
	    {
	    	alert("Age should be greater then or equal to 18");
	    	var field= document.getElementById('EditContessaApplicant_birthDate_i18n');
	    	document.getElementById("EditContessaApplicant_birthDate_i18n").value = field.defaultValue;
	       
	        return false;
	    }else{
	    	 return true;
	    }

}

function get_age(born, now) {
    var birthday = new Date(now.getFullYear(), born.getMonth(), born.getDate());
    if (now >= birthday) 
      return (now.getFullYear()+1) - born.getFullYear();
    else
      return (now.getFullYear() + 1) - born.getFullYear();
  }

function medicalTestValidation(medicalTest , applicationStatus) {
	var test = medicalTest;
	if ((test==null || test=="") && (applicationStatus!="APPLICANT_CREATED")){
		alert("Medical Test Not Done Yet !!");
		return false;
	}else{
		return true;
	}

  }


function contessaRecruitmentPopup(title,containerId,url,height,width){
	
	
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

$(document).ready(function() {
	$("#addProcessGradeIcon").click(function() {
		contessaRecruitmentPopup('Add Process Grade','#editProcessGrade','AddProcessGradePopUp?&title=Add Process Grade','250','550');
		
	});
});

$(document).ready(function() {
	$("#addMultiProcessBenifitsPopUpIcon").click(function() {
		contessaRecruitmentPopup('Add Multi Process Benifits','#editProcessBenifits','MultiProcessBenifitsPopUp?&title=Add Multi Process Benifits','250','550');
		
	});
});

$(document).ready(function() {
	$("#addProcessSalaryPopUpIcon").click(function() {
		contessaRecruitmentPopup('Add PG and EG Based Process Salary','#editProcessSalary','AddProcessSalaryPopUp?&title=Add PG and EG Based Process Salary','400','500');
		
	});
});



