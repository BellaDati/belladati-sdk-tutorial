function loadViews(basePath) {
	// iterate over all wrappers and load their chart contents
	$(".wrapper").each(function() {
		loadViewContent($(this), basePath);
	});
}

function loadViewContent(wrapper, basePath, interval, filterValues) {
	var id = wrapper.data("view-id"); // ID of the chart
	var url = basePath + "/chart/" + id;
	var $container = wrapper.find(".chart");
	
	// clear any existing content from the chart container
	$container.empty();
	
	// get the chart contents from our server
	$.get(url, { interval: JSON.stringify(interval), filterValues: JSON.stringify(filterValues) }, function(response) {
		
		// create the chart and display it
		var chart = Charts.create("chart-" + id, response.content);
		chart.resize($container.width(), $container.height());
	});
}

function initInteractions($selectContainer, initialInterval, $filterContainer, basePath) {
	// helper function to set month and year values
	var setValues = function($innerContainer, date) {
		$innerContainer.find("[name=month]").val(date.month);
		$innerContainer.find("[name=year]").val(date.year);
	};
	
	var resetValues = function() {
		if($selectContainer.length > 0) {
			// set from and to dates in the respective select boxes
			setValues($selectContainer.find(".date-from"), initialInterval.dateInterval.interval.from);
			setValues($selectContainer.find(".date-to"), initialInterval.dateInterval.interval.to);
		}
		if($filterContainer.length > 0) {
			$filterContainer.find(".filter-value").prop("checked", false);
		}
	};
	
	resetValues();
	
	var updateContents = function() {
		var interval = null;
		var filterValues = [];

		if($selectContainer.length > 0) {
			// build an interval object containing the selected from and to date
			interval = { from: {}, to: {} };
			interval.from.year = $selectContainer.find(".date-from [name=year]").val();
			interval.from.month = $selectContainer.find(".date-from [name=month]").val();
			interval.to.year = $selectContainer.find(".date-to [name=year]").val();
			interval.to.month = $selectContainer.find(".date-to [name=month]").val();
		}
		
		$filterContainer.find(".filter-value").each(function() {
			// get all selected filter values
			if($(this).is(":checked")) {
				filterValues.push(this.id);
			}
		});
		
		$(".wrapper").each(function() {
			if($(this).is("[data-use-date-interval=true]")) {
				// reload all views that use the interval
				loadViewContent($(this), basePath, interval, filterValues);
			} else {
				// reload all other views
				loadViewContent($(this), basePath, undefined, filterValues);
			}
		});
	};
	
	$(".content-update .update").click(updateContents);
	$(".content-update .reset").click(function() {
		resetValues();
		updateContents();
	});
}