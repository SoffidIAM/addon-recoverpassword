package com.soffid.iam.addon.passrecover.web;

import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;

import com.soffid.iam.utils.ConfigurationCache;

public class ConfigureButton extends Button implements AfterCompose {
	@Override
	public void afterCompose() {
		if ( !"true".equals(ConfigurationCache.getProperty("addon.retrieve.password.allow-question"))) {
			setVisible(false);
		}
	}

}
