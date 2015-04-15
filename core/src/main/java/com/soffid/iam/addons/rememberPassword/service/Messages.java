/**
 * 
 */
package com.soffid.iam.addons.rememberPassword.service;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import es.caib.seycon.ng.comu.lang.MessageFactory;

/**
 * @author (C) Soffid 2014
 *
 */
public class Messages {
	private static final String BUNDLE_NAME = "com.soffid.iam.addons.rememberPassword.service.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		return MessageFactory.getString(BUNDLE_NAME, key);
	}
}
