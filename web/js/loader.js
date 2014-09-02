function loadViews(basePath) {
	// iterate over all wrappers and load their chart contents
	$(".wrapper").each(function() {
		loadViewContent($(this), basePath);
	});
}

function loadViewContent(wrapper, basePath, interval) {
	var id = wrapper.data("view-id"); // ID of the chart
	var url = basePath + "/chart/" + id;
	var $container = wrapper.find(".chart");
	
	// clear any existing content from the chart container
	$container.empty();
	
	// get the chart contents from our server
	$.get(url, { interval: JSON.stringify(interval) }, function(response) {
		
		// create the chart and display it
		var chart = Charts.create("chart-" + id, response.content);
		chart.resize($container.width(), $container.height());
	});
}

function initInterval($selectContainer, initialInterval, basePath) {
	// helper function to set month and year values
	var setValues = function($innerContainer, date) {
		$innerContainer.find("[name=month]").val(date.month);
		$innerContainer.find("[name=year]").val(date.year);
	};
	
	var resetValues = function() {
		// set from and to dates in the respective select boxes
		setValues($selectContainer.find(".date-from"), initialInterval.dateInterval.interval.from);
		setValues($selectContainer.find(".date-to"), initialInterval.dateInterval.interval.to);
	};
	
	resetValues();
	
	var updateContents = function() {
		// build an interval object containing the selected from and to date
		var interval = { from: {}, to: {} };
		interval.from.year = $selectContainer.find(".date-from [name=year]").val();
		interval.from.month = $selectContainer.find(".date-from [name=month]").val();
		interval.to.year = $selectContainer.find(".date-to [name=year]").val();
		interval.to.month = $selectContainer.find(".date-to [name=month]").val();
		
		$(".wrapper[data-use-date-interval=true]").each(function() {
			// reload all views that use the interval
			loadViewContent($(this), basePath, interval);
		});
	};
	
	$selectContainer.find(".date-update").click(updateContents);
	$selectContainer.find(".date-reset").click(function() {
		resetValues();
		updateContents();
	});
}