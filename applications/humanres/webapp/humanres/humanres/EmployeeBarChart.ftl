<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="utf-8">
	    <title>Contessa Employee Chart</title>
	    <link rel="stylesheet" href="/flatgrey/ChartJS/css/style.css" />
		<script type="text/javascript" src="/flatgrey/ChartJS/js/Chart.min.js"></script>
	    
		<link href='http://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800' rel='stylesheet' type='text/css'>
	</head> 
	<body>
		<select id="cbBusinessUnit" onchange="LoadBarChartForEmployeeCount()">
			<option></option>
			<option value="PARTY_DIVISION">Division</option>
			<option value="PARTY_DEPARTMENT">Department</option>
			<option value="PARTY_SECTION">Section</option>
			<option value="PARTY_UNIT">Unit</option>
		</select>
		<div class="container">
			<canvas id="canvas" height="200" width="700">
		</div>
		
	</body>
</html>

	

<script>	
	function LoadBarChartForEmployeeCount(){
		var businessUnit=$("#cbBusinessUnit").val();
		var url = '/humanres/control/ViewEmployeeBarChart';
    	var dataSet = {"businessUnit": businessUnit};
    	var headItemList=[];
    	var dataItemList=[];
        $.ajax({
            url: url,
	        async: false,
	        type: 'POST',
	        data: dataSet,
            beforeSend: function () {
            },
            success: function (data) {
				for(var i=0;i<data.businessUnitList.length;i++){
					headItemList.push(data.businessUnitList[i].groupName);
				}
				for(var i=0;i<data.businessUnitEmployeeCountList.length;i++){
					dataItemList.push(data.businessUnitEmployeeCountList[i]);
				}
				var BarChart = {
   					 labels: headItemList,
					 datasets: [
				        {
							fillColor : "rgba(252,147,65,0.5)",
							strokeColor : "rgba(255,255,255,1)",
				            data: dataItemList
				        },
				        {
							fillColor : "rgba(252,14,1,0.5)",
							strokeColor : "rgba(255,255,255,1)",
				            data: dataItemList
				        }
				    ]		
				}
				var myBarChart = new Chart(document.getElementById("canvas")
								.getContext("2d")).Bar(BarChart, {
								scaleFontSize : 10, 
								columnsGapPercent: 1,	
								scaleFontColor : "#ffa45e"}
								);
								var myPieChart1 = new Chart(ctx[0]).Pie(BarChart,options);
            },
            error: function (xhr, ajaxOption, thrownError) {
               alert(xhr.status);
            }
        });			
	}		
</script>
