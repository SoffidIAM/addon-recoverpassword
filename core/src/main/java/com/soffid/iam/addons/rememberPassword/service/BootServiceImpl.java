package com.soffid.iam.addons.rememberPassword.service;

import com.soffid.iam.addons.rememberPassword.servlet.RememberPasswordServlet;
import com.soffid.iam.api.Tenant;

import es.caib.seycon.ng.sync.SeyconApplication;

public class BootServiceImpl extends BootServiceBase {

	@Override
	protected void handleConsoleBoot() throws Exception {
	}

	@Override
	protected void handleSyncServerBoot() throws Exception {
		SeyconApplication.getJetty().bindAdministrationServlet("/rememberPasswordServlet", null, RememberPasswordServlet.class);
		
		SeyconApplication.getJetty(). 
			publish(getRememberPasswordUserService(), RememberPasswordUserService.REMOTE_PATH, "agent");

	}

	@Override
	protected void handleTenantBoot(Tenant arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
