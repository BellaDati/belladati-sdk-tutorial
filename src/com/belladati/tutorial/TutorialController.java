package com.belladati.tutorial;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.belladati.sdk.BellaDatiService;
import com.belladati.sdk.auth.OAuthRequest;
import com.belladati.sdk.dashboard.DashboardInfo;
import com.belladati.sdk.exception.auth.AuthorizationException;
import com.belladati.sdk.exception.interval.InvalidIntervalException;
import com.belladati.sdk.intervals.AbsoluteInterval;
import com.belladati.sdk.intervals.DateUnit;
import com.belladati.sdk.intervals.Interval;
import com.belladati.sdk.report.Report;
import com.belladati.sdk.report.ReportInfo;
import com.belladati.sdk.view.View;
import com.belladati.sdk.view.ViewType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handles incoming page requests from the end user's browser. Connects to
 * BellaDati to fetch data and passes it on to the frontend for rendering.
 * 
 * @author Chris Hennigfeld
 */
@Controller
public class TutorialController {

	/**
	 * Provides access to a {@link BellaDatiService} instance, automatically
	 * injected via Spring.
	 */
	@Autowired
	private ServiceManager manager;

	/**
	 * Handles the root URL. Redirects to the login page or the report page
	 * depending on whether the user is logged in.
	 */
	@RequestMapping("/")
	public ModelAndView initialUrl() throws InterruptedException, ExecutionException {
		if (manager.isLoggedIn()) {
			return showReportDashboardList();
		} else {
			return new ModelAndView("login");
		}
	}

	/**
	 * Loads the list of reports and dashboards from BellaDati and injects them
	 * into the frontend view to display.
	 */
	public ModelAndView showReportDashboardList() throws InterruptedException, ExecutionException {
		ModelAndView modelAndView = new ModelAndView("list");

		// start a service for parallel execution of requests
		ExecutorService service = Executors.newCachedThreadPool();

		// provide thread-independent access
		final BellaDatiService bdService = manager.getService();

		// submit requests for reports and dashboards
		Future<List<ReportInfo>> reportFuture = service.submit(new Callable<List<ReportInfo>>() {
			@Override
			public List<ReportInfo> call() throws Exception {
				return bdService.getReportInfo().load().toList();
			}
		});
		Future<List<DashboardInfo>> dashboardFuture = service.submit(new Callable<List<DashboardInfo>>() {
			@Override
			public List<DashboardInfo> call() throws Exception {
				return bdService.getDashboardInfo().load().toList();
			}
		});

		// stop the service once the requests are done
		service.shutdown();

		// then inject the responses into the view
		modelAndView.addObject("reports", reportFuture.get());
		modelAndView.addObject("dashboards", dashboardFuture.get());

		return modelAndView;
	}

	/**
	 * Loads report contents from BellaDati and injects them into the frontend
	 * view for rendering.
	 * 
	 * @param reportId ID of the report to load
	 */
	@RequestMapping("/report/{id}")
	public ModelAndView showReport(@PathVariable("id") String reportId) {
		if (!manager.isLoggedIn()) {
			return new ModelAndView("redirect:/");
		}
		Report report = manager.getService().loadReport(reportId);

		ModelAndView modelAndView = new ModelAndView("report");
		modelAndView.addObject("report", report);
		modelAndView.addObject("commonInterval", getCommonMonthInterval(report));

		return modelAndView;
	}

	/**
	 * Looks for a common month-based interval in the report's views. Views
	 * without an interval or with an interval that's not month-based are
	 * ignored.
	 * 
	 * @param report the report to examine
	 * @return the interval if all month-based views share the same interval,
	 *         <tt>null</tt> if there are different intervals or no views have
	 *         month-based intervals
	 */
	private Interval<DateUnit> getCommonMonthInterval(Report report) {
		Interval<DateUnit> commonInterval = null;
		for (View view : report.getViews()) {
			// check each view's interval
			Interval<DateUnit> dateInterval = view.getPredefinedDateInterval();
			if (dateInterval != null && dateInterval.getIntervalUnit() == DateUnit.MONTH) {
				// if the view has an interval that's month-based
				if (commonInterval == null) {
					// if we haven't seen a month-based interval yet, note it
					commonInterval = dateInterval;
				} else if (!commonInterval.equals(dateInterval)) {
					// if we've seen a different month interval before, return
					return null;
				}
			}
		}
		return commonInterval;
	}

	/**
	 * Loads dashboard contents from BellaDati and injects them into the
	 * frontend view for rendering.
	 * 
	 * @param dashboardId ID of the dashboard to load
	 */
	@RequestMapping("/dashboard/{id}")
	public ModelAndView showDashboard(@PathVariable("id") String dashboardId) {
		if (!manager.isLoggedIn()) {
			return new ModelAndView("redirect:/");
		}
		ModelAndView modelAndView = new ModelAndView("dashboard");
		modelAndView.addObject("dashboard", manager.getService().loadDashboard(dashboardId));

		return modelAndView;
	}

	/**
	 * Loads the thumbnail image for the dashboard with the given ID.
	 * 
	 * @param id ID of the dashboard
	 * @return the dashboard's thumbnail, or an empty array if no thumbnail is
	 *         found
	 */
	@RequestMapping(value = "/dashboard/{id}/thumbnail", produces = "image/png")
	@ResponseBody
	public byte[] getDashboardThumbnail(@PathVariable String id) {
		return doGetThumbnail(true, id);
	}

	/**
	 * Loads the thumbnail image for the report with the given ID.
	 * 
	 * @param id ID of the report
	 * @return the report's thumbnail, or an empty array if no thumbnail is
	 *         found
	 */
	@RequestMapping(value = "/report/{id}/thumbnail", produces = "image/png")
	@ResponseBody
	public byte[] getReportThumbnail(@PathVariable String id) {
		return doGetThumbnail(false, id);
	}

	/**
	 * Performs loading a thumbnail image for the given ID.
	 * 
	 * @param isDashboard <tt>true</tt> to load a dashboard image,
	 *            <tt>false</tt> for a report
	 * @param id ID of the dashboard or report
	 * @return the thumbnail, or an empty array if no thumbnail is found
	 */
	private byte[] doGetThumbnail(boolean isDashboard, String id) {
		try {
			final BufferedImage thumbnail;
			if (isDashboard) {
				thumbnail = (BufferedImage) manager.getService().loadDashboardThumbnail(id);
			} else {
				thumbnail = (BufferedImage) manager.getService().loadReportThumbnail(id);
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(thumbnail, "png", baos);
			baos.flush();
			byte[] bytes = baos.toByteArray();
			baos.close();
			return bytes;
		} catch (IOException e) {
			return new byte[0];
		}
	}

	/**
	 * Loads and returns the content of a chart from BellaDati.
	 * 
	 * @param chartId ID of the chart to load
	 * @return the JSON content of the chart
	 */
	@RequestMapping("/chart/{id}")
	@ResponseBody
	public JsonNode viewContent(@PathVariable("id") String chartId,
		@RequestParam(value = "interval", required = false) String intervalString) throws IOException {
		if (intervalString != null) {
			try {
				JsonNode interval = new ObjectMapper().readTree(intervalString);
				Calendar from = new GregorianCalendar(interval.get("from").get("year").asInt(), interval.get("from").get("month")
					.asInt() - 1, 1);
				Calendar to = new GregorianCalendar(interval.get("to").get("year").asInt(), interval.get("to").get("month")
					.asInt() - 1, 1);

				// if all is successful, use the interval to load the chart
				return (JsonNode) manager.getService().createViewLoader(chartId, ViewType.CHART)
					.setDateInterval(new AbsoluteInterval<DateUnit>(DateUnit.MONTH, from, to)).loadContent();
			} catch (IOException e) {} catch (InvalidIntervalException e) {}
		}

		// otherwise, load the chart without a specified interval
		return (JsonNode) manager.getService().loadViewContent(chartId, ViewType.CHART);
	}

	/**
	 * Redirects the user to BellaDati for OAuth authorization.
	 */
	@RequestMapping("/login")
	public ModelAndView redirectToAuth(HttpServletRequest request) {
		OAuthRequest oAuthRequest = manager.initiateOAuth(getDeploymentUrl(request) + "/authorize");
		return new ModelAndView("redirect:" + oAuthRequest.getAuthorizationUrl());
	}

	/**
	 * Landing page after OAuth authorization, reached by redirect from the
	 * BellaDati server. Completes OAuth.
	 */
	@RequestMapping("/authorize")
	public ModelAndView requestAccessToken(RedirectAttributes redirectAttributes) {
		try {
			manager.completeOAuth();
		} catch (AuthorizationException e) {
			/*
			 * show an error informing the user - use e.getReason() to show
			 * custom error messages depending on what happened
			 */
			redirectAttributes.addFlashAttribute("error", "Authentication failed: " + e.getMessage());
		}
		return new ModelAndView("redirect:/");
	}

	/**
	 * Logs out.
	 */
	@RequestMapping("/logout")
	public ModelAndView doLogout() {
		manager.logout();
		return new ModelAndView("redirect:/");
	}

	/**
	 * Finds the root URL of the current deployment based on the user's request.
	 * 
	 * @param request request from the user
	 * @return the deployment root, including scheme, server, port, and path
	 */
	private String getDeploymentUrl(HttpServletRequest request) {
		String requestUrl = request.getRequestURL().toString();
		String servletPath = request.getServletPath();
		return requestUrl.substring(0, requestUrl.length() - servletPath.length());
	}
}
