/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

function lookupParty(url) {
    partyIdValue = document.lookupparty.partyId.value;
    userLoginIdValue = document.lookupparty.userLoginId.value;
    if (partyIdValue.length > 0 || userLoginIdValue.length > 0) {
        document.lookupparty.action = url;
    }
    return true;
}

function refreshInfo() {
    document.lookupparty.lookupFlag.value = "N";
    document.lookupparty.hideFields.value = "N";
    document.lookupparty.submit();
}

function ContessaRefreshInfo() {
    document.LookupUserLoginAndPartyDetails.lookupFlag.value = "N";
    document.LookupUserLoginAndPartyDetails.hideFields.value = "N";
    document.LookupUserLoginAndPartyDetails.submit();
}
function changeModeSubmit() {
    document.changeSelectionMode.submit();
}


function differenceTwoDate(date1, date2) {
    // Get 1 day in milliseconds
    var one_day = 1000 * 60 * 60 * 24;
    // Convert both dates to milliseconds
    var date1_ms = new Date(date1).getTime();
    var date2_ms = new Date(date2).getTime();

    // Calculate the difference in milliseconds
    var difference_ms = date2_ms - date1_ms;

    // Convert back to days and return
    return Math.round(difference_ms / one_day) + 1;
}

jQuery(document).ready(function () {
    jQuery('#emplLeaveFromDate').change(function () {
        CalculateLeaveSubtractHoliday();
    });

    jQuery('#emplLeaveThruDate').change(function () {
        CalculateLeaveSubtractHoliday();
    });

    jQuery('#emplLeaveLeaveTypeId').change(function () {
        CalculateLeaveSubtractHoliday();
    });
});

function approveRequisitionForm(form, value) {
    if (value == "") {
        alert("Please select an action.");
    } else {
        var formName = form.name;
        form.submit();
    }
}
function CalculateLeaveSubtractHoliday() {
    var URL = 'calculateLeaveWithoutHoliday';
    var fromDate = jQuery('#emplLeaveFromDate').val();
    var thruDate = jQuery('#emplLeaveThruDate').val();
    var companyId = jQuery('#companyId').val();
    var leaveTypeId = jQuery('#emplLeaveLeaveTypeId').val();
    //alert(fromDate+'---'+thruDate+'---'+companyId+'----'+leaveTypeId);
    var dataSet = {"companyId": companyId, "fromDate": fromDate, "thruDate": thruDate};
    jQuery.ajax({
        url: URL,
        async: false,
        type: 'POST',
        data: dataSet,
        dataType: "json",
        error: function (data) {
            alert("An error occured loading content! : " + data);
        },
        success: function (data) {
            var numberOfDays = 0;
            var numberOfHolidays = 0;
            if (fromDate.length > 0 && thruDate.length > 0) {
               numberOfDays = differenceTwoDate(fromDate, thruDate);
               //numberOfDays = numberOfDays - data.holidays.length;               
               //$('#emplLeaveNoOfLeave').val(numberOfDays);
               var GMT_HOURS_IN_MILL = 1000 * 60 * 60 * 6;
               var date1_ms = new Date(fromDate).getTime() - GMT_HOURS_IN_MILL;
               var date2_ms = new Date(thruDate).getTime() - GMT_HOURS_IN_MILL;
               for (var holiday = 0; holiday < data.holidays.length; holiday++) {
                   if (data.holidays[holiday].estimatedStartDate.time >= date1_ms && data.holidays[holiday].estimatedStartDate.time <= date2_ms) {
                       numberOfHolidays++;

                       //alert(data.holidays[holiday].estimatedStartDate.date + "/" + (data.holidays[holiday].estimatedStartDate.month+1) + "/" + (data.holidays[holiday].estimatedStartDate.year+1900));
                   }
               }
                if (leaveTypeId == "1001") numberOfHolidays = 0; 
                numberOfDays = numberOfDays - numberOfHolidays;
            }
            if (numberOfDays < 0) {numberOfDays = 0; alert("YOU HAVE INSERTED AN INVALID DATE. PLEASE INSERT AGAIN")}
            jQuery('#emplLeaveNoOfLeave').val(numberOfDays);
        }
    });
}