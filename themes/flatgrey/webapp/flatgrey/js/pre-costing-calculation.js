$(document).ready(function() {
	
	
	var ProductivityInfoForm = 'ProductivityInfo';
	var totalCostFormId = 'TotalCostInfo';

	var Budget_grandTotalCommercialCost = parseFloat($("#Budget_grandTotalCommercialCost").val());
	
	$("#" + totalCostFormId + "_othersExpenses").val(Budget_grandTotalCommercialCost.toFixed(2));
	
	setEfficiency(ProductivityInfoForm);
	setSalesCost(totalCostFormId);
	setNetCMRevenue(totalCostFormId);
	setFactoryOH(ProductivityInfoForm, totalCostFormId);
	setSellingOH(ProductivityInfoForm, totalCostFormId);
	setFOperPC(ProductivityInfoForm, totalCostFormId);
	setPreCostingCMperDzn(ProductivityInfoForm, totalCostFormId);
	setNetProfitLoss(ProductivityInfoForm, totalCostFormId);
	
	setAccessoriesPercentage(ProductivityInfoForm, totalCostFormId);
	setFabricsPercentage(ProductivityInfoForm, totalCostFormId);
	setEmbelishmentPercentage(ProductivityInfoForm, totalCostFormId);
	setOthersExpensesPercentage(ProductivityInfoForm, totalCostFormId);
	setNetCMRevenuePercentage(ProductivityInfoForm, totalCostFormId);
	
	setFactoryOHPercentage(ProductivityInfoForm, totalCostFormId);
	setSellingOHPercentage(ProductivityInfoForm, totalCostFormId);
	setDefferedInterestPercentage(ProductivityInfoForm, totalCostFormId);
	
	setSalesPercentage(totalCostFormId);
	
	// Number of Human Resource
	$("#" + ProductivityInfoForm + "_noOfHumanResource").change(function() {
		setEfficiency(ProductivityInfoForm);
		setFactoryOH(ProductivityInfoForm, totalCostFormId);
		setFOperPC(ProductivityInfoForm, totalCostFormId);
		setNetProfitLoss(ProductivityInfoForm, totalCostFormId);
	});
	
	// SAM
	$("#" + ProductivityInfoForm + "_sam").change(function() {
		setEfficiency(ProductivityInfoForm);
	});

	$("#" + ProductivityInfoForm + "_productivityPerHour").change(function() {
		setEfficiency(ProductivityInfoForm);
		setFactoryOH(ProductivityInfoForm, totalCostFormId);
		setFOperPC(ProductivityInfoForm, totalCostFormId)
		setNetProfitLoss(ProductivityInfoForm, totalCostFormId);

	});
	
	
	$("#OrderInformation_quantity").change(function() {
		setFactoryOH(ProductivityInfoForm, totalCostFormId);
		setFOperPC(ProductivityInfoForm, totalCostFormId);
		setPreCostingCMperDzn(ProductivityInfoForm, totalCostFormId);
		setNetProfitLoss(ProductivityInfoForm, totalCostFormId);

	});
	
	
	
	$("#" + ProductivityInfoForm + "_manHourPerPc").change(function() {
		setFactoryOH(ProductivityInfoForm, totalCostFormId);
		setFOperPC(ProductivityInfoForm, totalCostFormId)
		setNetProfitLoss(ProductivityInfoForm, totalCostFormId);

	});
	
	
	$("#" + ProductivityInfoForm + "_sellingOhPerPc").change(function() {
		setSellingOH(ProductivityInfoForm, totalCostFormId);
	});
	
	

	$("#" + totalCostFormId + "_factoryOH").change(function() {
		setFOperPC(ProductivityInfoForm, totalCostFormId);
		setNetProfitLoss(ProductivityInfoForm, totalCostFormId);
		//alert("factory oh");
	});
	
	$("#" + totalCostFormId + "_sellingOH").change(function() {
		setNetProfitLoss(ProductivityInfoForm, totalCostFormId);

	});
	
	
	///// total cost info
	
	$("#" + totalCostFormId + "_fabrics").change(function() {
		setNetCMRevenue(totalCostFormId);
		setNetProfitLoss(ProductivityInfoForm, totalCostFormId);

	});
	
	$("#" + totalCostFormId + "_accessories").change(function() {
		setNetCMRevenue(totalCostFormId);
		setNetProfitLoss(ProductivityInfoForm, totalCostFormId);

	});
	
	$("#" + totalCostFormId + "_embellishment").change(function() {
		setNetCMRevenue(totalCostFormId);
		setNetProfitLoss(ProductivityInfoForm, totalCostFormId);

	});
	
	$("#" + totalCostFormId + "_othersExpenses").change(function() {
		setNetCMRevenue(totalCostFormId);
		setNetProfitLoss(ProductivityInfoForm, totalCostFormId);
	});
	

	$("#" + totalCostFormId + "_netCMRevenue").change(function() {
		//alert("revenue change");
		setPreCostingCMperDzn(ProductivityInfoForm, totalCostFormId);
		setNetProfitLoss(ProductivityInfoForm, totalCostFormId);

	});
	
	////

	$("#OrderInformation_fobValue").change(function() {
		setSalesCost(totalCostFormId);
		setNetProfitLoss(ProductivityInfoForm, totalCostFormId);
	});


});



function setSalesPercentage(totalCostFormId) {

	var fabrics_percentage = $("#" + totalCostFormId + "_fabrics_percentage").val();
	var accessories_percentage = $("#" + totalCostFormId + "_accessories_percentage").val();
	var embellishment_percentage = $("#" + totalCostFormId + "_embellishment_percentage").val();
	var othersExpenses_percentage = $("#" + totalCostFormId + "_othersExpenses_percentage").val();
	
	var factoryOH_percentage = $("#" + totalCostFormId + "_factoryOH_percentage").val();
	var sellingOH_percentage = $("#" + totalCostFormId + "_sellingOH_percentage").val();
	var defferedInterest_percentage = $("#" + totalCostFormId + "_defferedInterest_percentage").val();
	var netProfitLoss_percentage = $("#" + totalCostFormId + "_netProfitLoss_percentage").val();

	var sales_percentage = parseFloat(fabrics_percentage) + parseFloat(accessories_percentage) + parseFloat(embellishment_percentage) + parseFloat(othersExpenses_percentage)
								+ parseFloat(factoryOH_percentage) + parseFloat(sellingOH_percentage) + parseFloat(defferedInterest_percentage) + parseFloat(netProfitLoss_percentage);
			
	//alert("sales_percentage:" + sales_percentage);
	$("#" + totalCostFormId + "_sales_percentage").val(sales_percentage.toFixed(2));
}


function setDefferedInterestPercentage(ProductivityInfoForm, totalCostFormId) {

	var accessoriesTotal = $("#" + totalCostFormId + "_defferedInterest").val();
	var salesTotal = $("#" + totalCostFormId + "_sales").val();
	
	var accessoriesPercentage = (accessoriesTotal/salesTotal)*100;
			
	$("#" + totalCostFormId + "_defferedInterest_percentage").val(accessoriesPercentage.toFixed(2));
}

function setSellingOHPercentage(ProductivityInfoForm, totalCostFormId) {

	var accessoriesTotal = $("#" + totalCostFormId + "_sellingOH").val();
	var salesTotal = $("#" + totalCostFormId + "_sales").val();
	
	var accessoriesPercentage = (accessoriesTotal/salesTotal)*100;
			
	$("#" + totalCostFormId + "_sellingOH_percentage").val(accessoriesPercentage.toFixed(2));
}

function setFactoryOHPercentage(ProductivityInfoForm, totalCostFormId) {

	var accessoriesTotal = $("#" + totalCostFormId + "_factoryOH").val();
	var salesTotal = $("#" + totalCostFormId + "_sales").val();
	
	var accessoriesPercentage = (accessoriesTotal/salesTotal)*100;
			
	$("#" + totalCostFormId + "_factoryOH_percentage").val(accessoriesPercentage.toFixed(2));
}

function setNetCMRevenuePercentage(ProductivityInfoForm, totalCostFormId) {

	var accessoriesTotal = $("#" + totalCostFormId + "_netCMRevenue").val();
	var salesTotal = $("#" + totalCostFormId + "_sales").val();
	
	var accessoriesPercentage = (accessoriesTotal/salesTotal)*100;
			
	$("#" + totalCostFormId + "_netCMRevenue_percentage").val(accessoriesPercentage.toFixed(2));
}

function setOthersExpensesPercentage(ProductivityInfoForm, totalCostFormId) {

	var accessoriesTotal = $("#" + totalCostFormId + "_othersExpenses").val();
	var salesTotal = $("#" + totalCostFormId + "_sales").val();
	
	var accessoriesPercentage = (accessoriesTotal/salesTotal)*100;
			
	$("#" + totalCostFormId + "_othersExpenses_percentage").val(accessoriesPercentage.toFixed(2));
}


function setEmbelishmentPercentage(ProductivityInfoForm, totalCostFormId) {

	var accessoriesTotal = $("#" + totalCostFormId + "_embellishment").val();
	var salesTotal = $("#" + totalCostFormId + "_sales").val();
	
	var accessoriesPercentage = (accessoriesTotal/salesTotal)*100;
			
	$("#" + totalCostFormId + "_embellishment_percentage").val(accessoriesPercentage.toFixed(2));
}


function setAccessoriesPercentage(ProductivityInfoForm, totalCostFormId) {

	
	var accessoriesTotal = $("#" + totalCostFormId + "_accessories").val();
	var salesTotal = $("#" + totalCostFormId + "_sales").val();
	
	var accessoriesPercentage = (accessoriesTotal/salesTotal)*100;
			
	$("#" + totalCostFormId + "_accessories_percentage").val(accessoriesPercentage.toFixed(2));
}


function setFabricsPercentage(ProductivityInfoForm, totalCostFormId) {

	
	var fabricsTotal = $("#" + totalCostFormId + "_fabrics").val();
	var salesTotal = $("#" + totalCostFormId + "_sales").val();
	
	var fabricsPercentage = (fabricsTotal/salesTotal)*100;
			
	$("#" + totalCostFormId + "_fabrics_percentage").val(fabricsPercentage.toFixed(2));
}


function setNetProfitLoss(ProductivityInfoForm, totalCostFormId) {

	
	var netCMRevenue = $("#" + totalCostFormId + "_netCMRevenue").val();
	var factoryOH = $("#" + totalCostFormId + "_factoryOH").val();
	var sellingOH = $("#" + totalCostFormId + "_sellingOH").val();
	var defferedInterest = $("#" + totalCostFormId + "_defferedInterest").val();
	
	//alert("netCMRevenue:" + netCMRevenue + " factoryOH:" + factoryOH + " sellingOH:" + sellingOH + " defferedInterest:" + defferedInterest);
	
	var netProfitLoss = parseFloat(netCMRevenue-factoryOH-sellingOH-defferedInterest);
			
	netProfitLoss = netProfitLoss.toFixed(2);
	
	/*if (netProfitLoss<0){
		netProfitLoss = "(" + netProfitLoss*-1 + ")";
	}
	*/
	$("#" + totalCostFormId + "_netProfitLoss").val(netProfitLoss);
	
	
	var salesTotal = $("#" + totalCostFormId + "_sales").val();
	var accessoriesPercentage = (netProfitLoss/salesTotal)*100;
	$("#" + totalCostFormId + "_netProfitLoss_percentage").val(accessoriesPercentage.toFixed(2));
	
}

function setPreCostingCMperDzn(ProductivityInfoForm, totalCostFormId) {

	
	var netCMRevenue = $("#" + totalCostFormId + "_netCMRevenue").val();
	var quantity = $("#OrderInformation_quantity").val();

	//alert("netCMRevenue:" + netCMRevenue + " quantity:" + quantity);

	
	var PreCostingCMperDzn = parseFloat(netCMRevenue/quantity*12);
	
			
	$("#" + ProductivityInfoForm + "_preCostingCMPerDzn").val(PreCostingCMperDzn.toFixed(2));
}


function setFOperPC(ProductivityInfoForm, totalCostFormId) {


	
	var factoryOH = $("#" + totalCostFormId + "_factoryOH").val();
	var quantity = $("#OrderInformation_quantity").val();

	//alert("factoryOH:" + factoryOH + "/ quantity:" + quantity);

	
	var FOperPC = parseFloat(factoryOH/quantity);
	
			
	$("#" + ProductivityInfoForm + "_foPcs").val(FOperPC.toFixed(2));
}



function setSellingOH(ProductivityInfoForm, totalCostFormId) {

	var productivityPerHour = $("#" + totalCostFormId + "_sales").val();
	var soFob = $("#" + ProductivityInfoForm + "_sellingOhPerPc").val();

	
	var factoryOH = parseFloat((productivityPerHour*soFob)/100);
			
	$("#" + totalCostFormId + "_sellingOH").val(factoryOH.toFixed(2));
}


function setFactoryOH(ProductivityInfoForm, totalCostFormId) {

	var noOfHumanResource = $("#" + ProductivityInfoForm + "_noOfHumanResource").val();
	var manHourPerPc = $("#" + ProductivityInfoForm + "_manHourPerPc").val();
	var productivityPerHour = $("#" + ProductivityInfoForm + "_productivityPerHour").val();
	var quantity = $("#OrderInformation_quantity").val();

	//alert("("+ noOfHumanResource + "*" + manHourPerPc + ")/"+ productivityPerHour + "*" + quantity);
	
	var factoryOH = parseFloat((noOfHumanResource * manHourPerPc)/productivityPerHour*quantity);
			
	//alert("factoryOH:" + factoryOH);
	
	//alert("factoryOH:"+factoryOH);
	$("#" + totalCostFormId + "_factoryOH").val(factoryOH.toFixed(2));
}

function setNetCMRevenue(totalCostFormId) {

	var totalSalesValue = 	$("#" + totalCostFormId + "_sales").val();
	
	var totalFabricsValue = 	$("#" + totalCostFormId + "_fabrics").val();
	var totalAccessoriesValue = 	$("#" + totalCostFormId + "_accessories").val();
	var totalEmbellishmentValue = 	$("#" + totalCostFormId + "_embellishment").val();
	var totalOthersExpensesValue = 	$("#" + totalCostFormId + "_othersExpenses").val();

	var netCMRevenue = parseFloat(totalSalesValue - parseFloat(totalFabricsValue) - parseFloat(totalAccessoriesValue) - parseFloat(totalEmbellishmentValue) - parseFloat(totalOthersExpensesValue));
	
//	alert("totalSalesValue:" + totalSalesValue + " totalFabricsValue:" + totalFabricsValue + " totalAccessoriesValue:" + parseInt(totalAccessoriesValue) + " totalEmbellishmentValue:" + totalEmbellishmentValue + " totalOthersExpensesValue:" + totalOthersExpensesValue);
	//alert(netCMRevenue);
	$("#" + totalCostFormId + "_netCMRevenue").val(netCMRevenue.toFixed(2));
}


function setSalesCost(totalCostFormId) {

	var fobValue = $("#OrderInformation_fobValue").val();
			
	$("#" + totalCostFormId + "_sales").val(fobValue);
}


function setEfficiency(formId) {

	var noOfHumanResource = $("#" + formId + "_noOfHumanResource").val();
	var SAM = $("#" + formId + "_sam").val();
	var productivityPerHour = $("#" + formId + "_productivityPerHour").val();


	var efficiency = parseInt(parseFloat((productivityPerHour * SAM)/(noOfHumanResource*60))*100);
	
	/*alert(efficiency);*/
			
	$("#" + formId + "_efficiency").val(efficiency);
}


