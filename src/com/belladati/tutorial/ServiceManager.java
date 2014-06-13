package com.belladati.tutorial;

import org.springframework.stereotype.Component;

import com.belladati.sdk.BellaDati;
import com.belladati.sdk.BellaDatiService;

/**
 * Provides access to the BellaDati service stored in the current session.
 * 
 * @author Chris Hennigfeld
 */
@Component
public class ServiceManager {

	private BellaDatiService service;

	public BellaDatiService getService() {
		if (service != null) {
			return service;
		}
		synchronized (this) {
			if (service == null) {
				service = BellaDati.connect().xAuth("techKey", "techSecret", "api-demo@belladati.com", "apiDemo1");
			}
			return service;
		}
	}
}
