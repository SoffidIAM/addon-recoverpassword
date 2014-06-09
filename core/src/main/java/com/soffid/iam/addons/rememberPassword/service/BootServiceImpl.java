package com.soffid.iam.addons.rememberPassword.service;

import com.soffid.iam.addons.rememberPassword.servlet.RememberPasswordServlet;

import es.caib.seycon.ng.sync.SeyconApplication;
import es.caib.seycon.ng.sync.jetty.JettyServer;

public class BootServiceImpl extends BootServiceBase {

	@Override
	protected void handleConsoleBoot() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleSyncServerBoot() throws Exception {
		SeyconApplication.getJetty().bindAdministrationServlet("/rememberPasswordServlet", null, RememberPasswordServlet.class);
	}

}
