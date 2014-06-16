package com.belladati.tutorial;

import org.springframework.stereotype.Component;

import com.belladati.sdk.BellaDati;
import com.belladati.sdk.BellaDatiService;

/**
 * Provides access to the BellaDati service. This is a singleton component
 * holding one lazily initialized service instance.
 * 
 * @author Chris Hennigfeld
 */
@Component
public class ServiceManager {

	/** Service object used to connect to BellaDati. */
	private BellaDatiService service;

	/**
	 * Returns the service object used to access BellaDati. Lazily initializes
	 * the service by connecting to BellaDati the first time this method is
	 * called.
	 * 
	 * @return the service object used to access BellaDati
	 */
	public BellaDatiService getService() {
		// first do a non-thread-safe check for better performance
		if (service != null) {
			// if we already have a service, use it
			return service;
		}
		// otherwise initialize, synchronized to be thread-safe
		synchronized (this) {
			// check again since the first check wasn't thread-safe
			if (service == null) {
				// connect to BellaDati and authenticate with fixed credentials
				service = BellaDati.connect().xAuth("techKey", "techSecret", "api-demo@belladati.com", "apiDemo1");
			}
			return service;
		}
	}
}
