/**
 * mgrApprStatus_o_2 approved rejected
 * 
 * achievement_o_2
 */
// alert("hhhhhhhhhh");
$(document)
		.ready(
				function() {
					var counter = 0;
					var achievement;
					var elements;
					var obj = {};
					try {
						elements = document
								.getElementById("ListEmployeeGoalsAndKpi").elements;
					} catch (e) {
						console.log(e)
					}
					if (elements != null) {
						for (var i = 0; i < elements.length; i++) {
							var item = elements.item(i);
							obj[item.name] = item.value;
							if (obj[item.name] == "Accepted") {
								achievement = "ListEmployeeGoalsAndKpi_achievement_o_"
										+ counter;
								// alert(achievement);
								document.getElementById(achievement).readOnly = true;
								counter++;
							} else if (obj[item.name] == "Rejected") {
								// alert(counter);
								counter++;
							}
						}
					}
					// alert(JSON.stringify(obj));
				});

// for monthly viewMonthlyKPIform
$(document).ready(function() {
	var counter = 0;
	var mAchievement;
	var elements;
	var obj = {};
	try {
		elements = document.getElementById("viewMonthlyKPIform").elements;
	} catch (e) {
		console.log(e)
	}
	if (elements != null) {
		for (var i = 0; i < elements.length; i++) {
			var item = elements.item(i);
			obj[item.name] = item.value;
			if (obj[item.name] == "Accepted") {
				mAchievement = "viewMonthlyKPIform_mAchievement_o_" + counter;
				// alert(achievement);
				document.getElementById(mAchievement).readOnly = true;
				counter++;
			} else if (obj[item.name] == "Rejected") {
				// alert(counter);
				counter++;
			}
		}
	}
	// alert(JSON.stringify(obj));
});

// for kpi counter
$(document).ready(function() {
	try {
		table = document.getElementsByClassName("sendCouter");
		var cell = table[0].rows[1].cells[2].innerHTML;
		if (cell == 0) {
			table[0].rows[1].cells[4].innerHTML = "";
		}

	} catch (e) {
		console.log(e)
	}

});
