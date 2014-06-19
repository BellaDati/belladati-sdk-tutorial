package com.belladati.tutorial;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.belladati.sdk.BellaDati;
import com.belladati.sdk.BellaDatiConnection;
import com.belladati.sdk.BellaDatiService;
import com.belladati.sdk.auth.OAuthRequest;
import com.belladati.sdk.exception.auth.AuthorizationException;

/**
 * Provides access to the BellaDati service. This is a singleton component
 * retrieving the service object from the current session. Spring ensures that
 * the right session is injected thread-safely.
 * 
 * @author Chris Hennigfeld
 */
@Component
public class ServiceManager {

	/** Session attribute to store the BellaDati service. */
	private static final String SESSION_SERVICE_ATTRIBUTE = "BellaDatiService";

	/** Session attribute to store pending OAuth requests. */
	private static final String SESSION_OAUTH_ATTRIBUTE = "pendingOAuth";

	/** Current session to store the service object. */
	@Autowired
	private HttpSession session;

	/**
	 * Connection used to contact BellaDati cloud. Since this component is a
	 * singleton, there will be only one connection shared by all users.
	 */
	private final BellaDatiConnection connection = BellaDati.connect();

	/**
	 * Returns the service object used to access BellaDati.
	 * 
	 * @return the service object used to access BellaDati, or <tt>null</tt> if
	 *         the user is not logged in
	 */
	public BellaDatiService getService() {
		return (BellaDatiService) session.getAttribute(SESSION_SERVICE_ATTRIBUTE);
	}

	/**
	 * Returns <tt>true</tt> if the user is logged in.
	 * 
	 * @return <tt>true</tt> if the user is logged in
	 */
	public boolean isLoggedIn() {
		return getService() != null;
	}

	/**
	 * Initiates OAuth authentication to the BellaDati cloud server. Call
	 * {@link OAuthRequest#getAuthorizationUrl()} to point the user to the URL
	 * to authorize the request, then complete authorization by calling
	 * {@link #completeOAuth()}.
	 * 
	 * @param redirectUrl URL to redirect to after authorization
	 * @return the pending OAuth request
	 */
	public OAuthRequest initiateOAuth(String redirectUrl) {
		OAuthRequest request = connection.oAuth("techKey", "techSecret", redirectUrl);
		session.setAttribute(SESSION_OAUTH_ATTRIBUTE, request);
		return request;
	}

	/**
	 * Completes authorization of a pending OAuth request and returns the
	 * service object to access BellaDati. Does nothing if no OAuth request is
	 * pending for the current session.
	 * 
	 * @return the service object to access BellaDati, or <tt>null</tt> if no
	 *         OAuth request was pending
	 * @throws AuthorizationException if an error occurred during authorization
	 */
	public BellaDatiService completeOAuth() throws AuthorizationException {
		OAuthRequest request = (OAuthRequest) session.getAttribute(SESSION_OAUTH_ATTRIBUTE);
		if (request != null) {
			BellaDatiService service = request.requestAccess();
			storeService(service);
			return service;
		}
		return null;
	}

	/**
	 * Logs out.
	 */
	public void logout() {
		// since there's no session on the BD server,
		// we just need to discard the service object
		storeService(null);
	}

	/**
	 * Stores the given service object in the session. Call with <tt>null</tt>
	 * to clear the service.
	 * 
	 * @param service service object to store, <tt>null</tt> to clear
	 */
	private void storeService(BellaDatiService service) {
		session.setAttribute(SESSION_SERVICE_ATTRIBUTE, service);
	}
}
