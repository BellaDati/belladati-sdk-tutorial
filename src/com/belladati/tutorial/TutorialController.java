package com.belladati.tutorial;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.belladati.sdk.BellaDatiService;
import com.belladati.sdk.auth.OAuthRequest;
import com.belladati.sdk.exception.auth.AuthorizationException;
import com.belladati.sdk.view.ViewType;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Handles incoming page requests from the end user's browser. Connects to
 * BellaDati to fetch data and passes it on to the frontend for rendering.
 * 
 * @author Chris Hennigfeld
 */
@Controller
public class TutorialController {

	/** Hard-coded ID of the report we want to load. */
	private static final String REPORT_ID = "30751";

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
	public ModelAndView initialUrl() {
		if (manager.isLoggedIn()) {
			return showReport();
		} else {
			return new ModelAndView("login");
		}
	}

	/**
	 * Loads report contents from BellaDati and injects them into the frontend
	 * view for rendering.
	 */
	public ModelAndView showReport() {
		ModelAndView modelAndView = new ModelAndView("report");
		modelAndView.addObject("report", manager.getService().loadReport(REPORT_ID));

		return modelAndView;
	}

	/**
	 * Loads and returns the content of a chart from BellaDati.
	 * 
	 * @param chartId ID of the chart to load
	 * @return the JSON content of the chart
	 */
	@RequestMapping("/chart/{id}")
	@ResponseBody
	public JsonNode viewContent(@PathVariable("id") String chartId) {
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
