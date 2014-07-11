function loadViews(basePath) {
	// iterate over all wrappers and load their chart contents
	$(".wrapper").each(function() {
		loadViewContent($(this), basePath);
	});
}
function loadViewContent(wrapper, basePath) {
	var id = wrapper.data("view-id"); // ID of the chart
	var url = basePath + "/chart/" + id;
	
	// get the chart contents from our server
	$.get(url, function(response) { 
		var $container = wrapper.find(".chart");
	
		// clear any existing content from the chart container
		$container.empty();
		
		// create the chart and display it
		var chart = Charts.create("chart-" + id, response.content);
		chart.resize($container.width(), $container.height());
	});
};