package com.belladati.tutorial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.belladati.sdk.BellaDatiService;
import com.belladati.sdk.view.ViewType;

/**
 * Handles incoming page requests from the end user's browser. Connects to
 * BellaDati to fetch data and passes it on to the frontend for rendering.
 * 
 * @author Chris Hennigfeld
 */
@Controller
public class TutorialController {

	/** Hard-coded ID of the chart we want to load. */
	private static final String CHART_ID = "30751-PqMvlM9gdC";

	/**
	 * Provides access to a {@link BellaDatiService} instance, automatically
	 * injected via Spring.
	 */
	@Autowired
	private ServiceManager manager;

	/**
	 * Handles the root URL. Loads chart contents from BellaDati and injects
	 * them into the frontend view for rendering.
	 * 
	 * @return the frontend view and view options to send to the end user
	 */
	@RequestMapping("/")
	public ModelAndView initialUrl() {
		ModelAndView modelAndView = new ModelAndView("view");
		modelAndView.addObject("chart", manager.getService().loadViewContent(CHART_ID, ViewType.CHART));

		return modelAndView;
	}
}
