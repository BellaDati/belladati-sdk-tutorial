package com.belladati.tutorial;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.belladati.sdk.view.ViewType;

@Controller
public class TutorialController {

	private static final String CHART_ID = "30751-PqMvlM9gdC";

	@Autowired
	private ServiceManager manager;

	@RequestMapping("/")
	public ModelAndView initialUrl() {
		ModelAndView modelAndView = new ModelAndView("view");
		modelAndView.addObject("chart", manager.getService().loadViewContent(CHART_ID, ViewType.CHART));

		return modelAndView;
	}
}
