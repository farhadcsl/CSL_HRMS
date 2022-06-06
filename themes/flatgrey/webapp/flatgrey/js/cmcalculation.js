$(document).ready(function() {
	// screenlet_1_col ProductionCostMakingFormA
	// screenlet_4_col ProductionCostMakingForm
	//alert("Test");
	var formId = '';
	var innerFormId = $("#screenlet_1_col > form").attr('id');
	var innerFormId2 = $("#costingAndMaking_col > form").attr('id');
	var innerFormId3 = $("#costingAndMaking_col > form").attr('id');
	if (innerFormId2 == 'ProductionCostMakingForm') {
		formId = innerFormId2;
	}
	if(innerFormId3 == 'StyleProductionCostMakingForm'){
		formId = innerFormId3;
	}
	else
		formId = innerFormId;

	setCMPrice(formId);

	// Number of Human Resource
	$("#" + formId + "_noOfHumanResource").change(function() {
		setCMPrice(formId);
	});

	// Productivity Per Hour
	$("#" + formId + "_productivityPerHour").change(function() {
		setCMPrice(formId);
	});
	// Production Hour Per Day
	$("#" + formId + "_productionHourPerday").change(function() {
		setCMPrice(formId);
	});

	// Man Hour Per Pices
	$("#" + formId + "_manHourPerPc").change(function() {
		setCMPrice(formId);
	});

	$("#" + formId + "_manhourPerDayCost").change(function() {
		setCMPrice(formId);
	});

	$("#" + formId + "_sellingOhPerPc").change(function() {
		setCMPrice(formId);

	});

	$("#" + formId + "_profitMarginOnCM").change(function() {
		setCMPrice(formId);
	});

});

function setCMPrice(formId) {
	getDailyProduction(formId);
	getSellingOverHeadPerDay(formId);
	getManhourCost(formId);
	getManhourPerDayCost(formId);
	getTotalCost(formId);
	getNetCostMakingPerPc(formId);
	getNetBEPCostMakingPerdz(formId);
	getNetCostMakingPerdzWPerFivePercent(formId);
}

function getDailyProduction(formId) {
	var productivityPerHour = $("#" + formId + "_productivityPerHour").val();
	var productionHourPerday = $("#" + formId + "_productionHourPerday").val();
	$("#" + formId + "_dailyProduction").val(
			productivityPerHour * productionHourPerday);
}

function getManhourCost(formId) {
	var noOfHumanResource = $("#" + formId + "_noOfHumanResource").val();
	var manHourPerPc = $("#" + formId + "_manHourPerPc").val();
	$("#" + formId + "_manhourCost").val(noOfHumanResource * manHourPerPc);
}

/**
 * The getManhourPerDayCost method return ( productionPerday*getManhourCost() )
 * 
 * @param productionPerday
 * @return
 */
function getManhourPerDayCost(formId) {
	var noOfHumanResource = $("#" + formId + "_noOfHumanResource").val();
	var manHourPerPc = $("#" + formId + "_manHourPerPc").val();
	var productionPerday = $("#" + formId + "_productionHourPerday").val();

	var manhourPerDayCost = noOfHumanResource * productionPerday * manHourPerPc;
	$("#" + formId + "_manhourPerDayCost").val(manhourPerDayCost);
}

/**
 * The getSellingOverHeadPerDay method return ( getDailyProduction() *
 * sellingOhPerPc )
 * 
 * @param sellingOhPerPc
 * @return
 */
function getSellingOverHeadPerDay(formId) {
	var dailyProduction = $("#" + formId + "_dailyProduction").val();
	var sellingOhPerPc = $("#" + formId + "_sellingOhPerPc").val();
	var sellingOverHeadPerDay = dailyProduction * sellingOhPerPc;
	$("#" + formId + "_sellingOverHeadPerday").val(sellingOverHeadPerDay);
}

/**
 * The getTotalCost method return ( getManhourPerDayCost() *
 * getSellingOverHeadPerDay() )
 * 
 * @return
 */
function getTotalCost(formId) {

	var productivityPerHour = $("#" + formId + "_productivityPerHour").val();
	var productionHourPerday = $("#" + formId + "_productionHourPerday").val();
	var sellingOhPerPc = $("#" + formId + "_sellingOhPerPc").val();

	var noOfHumanResource = $("#" + formId + "_noOfHumanResource").val();
	var manHourPerPc = $("#" + formId + "_manHourPerPc").val();

	var manhourPerDayCost = parseInt(productionHourPerday * noOfHumanResource
			* manHourPerPc);
	var sellingOverHeadPerDay = parseInt(productivityPerHour
			* productionHourPerday * sellingOhPerPc);

	var totalCost = manhourPerDayCost + sellingOverHeadPerDay;
	$("#" + formId + "_totalCost").val(totalCost.toFixed(2));

}

function getNetCostMakingPerPc(formId) {

	var productivityPerHour = $("#" + formId + "_productivityPerHour").val();
	var productionHourPerday = $("#" + formId + "_productionHourPerday").val();

	var sellingOhPerPc = $("#" + formId + "_sellingOhPerPc").val();
	var manhourCost = $("#" + formId + "_manhourCost").val();
	var noOfHumanResource = $("#" + formId + "_noOfHumanResource").val();
	var manHourPerPc = $("#" + formId + "_manHourPerPc").val();

	var manhourPerDayCost = parseInt(productionHourPerday * noOfHumanResource
			* manHourPerPc);
	var sellingOverHeadPerDay = parseInt(productivityPerHour
			* productionHourPerday * sellingOhPerPc);

	var totalCost = manhourPerDayCost + sellingOverHeadPerDay;
	var netCostMakingPerPc = totalCost
			/ (productivityPerHour * productionHourPerday);

	if (!isNaN(netCostMakingPerPc)) {
		$("#" + formId + "_netCostMakingPerPc").val(
				netCostMakingPerPc.toFixed(2));
		$("#" + formId + "_netCostMakingPerPices").val(
				netCostMakingPerPc.toFixed(2));

	} else {
		$("#" + formId + "_netCostMakingPerPc").val('0.00');
		$("#" + formId + "_netCostMakingPerPices").val('0.00');
	}
}

function getNetBEPCostMakingPerdz(formId) {

	var productivityPerHour = $("#" + formId + "_productivityPerHour").val();
	var productionHourPerday = $("#" + formId + "_productionHourPerday").val();

	var sellingOhPerPc = $("#" + formId + "_sellingOhPerPc").val();
	var manhourCost = $("#" + formId + "_manhourCost").val();
	var noOfHumanResource = $("#" + formId + "_noOfHumanResource").val();
	var manHourPerPc = $("#" + formId + "_manHourPerPc").val();

	var manhourPerDayCost = parseInt(productionHourPerday * noOfHumanResource
			* manHourPerPc);
	var sellingOverHeadPerDay = parseInt(productivityPerHour
			* productionHourPerday * sellingOhPerPc);

	var totalCost = manhourPerDayCost + sellingOverHeadPerDay;
	var netCostMakingPerPc = (totalCost / (productivityPerHour * productionHourPerday)) * 12;
	if (!isNaN(netCostMakingPerPc)) {
		$("#" + formId + "_netBEPCostMakingPerdz").val(
				netCostMakingPerPc.toFixed(2));
	} else {
		$("#" + formId + "_netBEPCostMakingPerdz").val('0.00');
	}

}
/**
 * The method return ( getDailyProduction() * getTotalCost() )
 * 
 * @return
 */

function getNetCostMakingPerdzWPerFivePercent(formId) {

	var productivityPerHour = $("#" + formId + "_productivityPerHour").val();
	var productionHourPerday = $("#" + formId + "_productionHourPerday").val();
	var sellingOhPerPc = $("#" + formId + "_sellingOhPerPc").val();
	var manhourCost = $("#" + formId + "_manhourCost").val();
	var noOfHumanResource = $("#" + formId + "_noOfHumanResource").val();
	var manHourPerPc = $("#" + formId + "_manHourPerPc").val();
	var profitMarginOnCM = $("#" + formId + "_profitMarginOnCM").val();

	var manhourPerDayCost = parseInt(productionHourPerday * noOfHumanResource
			* manHourPerPc);
	var sellingOverHeadPerDay = parseInt(productivityPerHour
			* productionHourPerday * sellingOhPerPc);

	var totalCost = manhourPerDayCost + sellingOverHeadPerDay;
	var netCostMakingPerPc = totalCost
			/ (productivityPerHour * productionHourPerday);

	var percentValue = profitMarginOnCM / 100;
	var totalProfitMarginCost = (netCostMakingPerPc * 12) / (1 - percentValue);

	if (!isNaN(totalProfitMarginCost)) {
		$("#" + formId + "_netCostMakingPerdozenWPerFivePercent").val(
				totalProfitMarginCost.toFixed(2));
	} else {
		$("#" + formId + "_netCostMakingPerdozenWPerFivePercent").val('0.00');
	}

}
