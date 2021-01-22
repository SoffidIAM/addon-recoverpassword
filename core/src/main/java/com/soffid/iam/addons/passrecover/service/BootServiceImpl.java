package com.soffid.iam.addons.passrecover.service;

import com.soffid.iam.addons.passrecover.servlet.RecoverPasswordServlet;
import com.soffid.iam.api.Tenant;

import es.caib.seycon.ng.sync.SeyconApplication;

public class BootServiceImpl extends BootServiceBase {

	@Override
	protected void handleConsoleBoot() throws Exception {
	}

	@Override
	protected void handleSyncServerBoot() throws Exception {
		SeyconApplication.getJetty().bindAdministrationServlet("/rememberPasswordServlet", null, RecoverPasswordServlet.class);
		
		SeyconApplication.getJetty(). 
			publish(getRecoverPasswordUserService(), RecoverPasswordUserService.REMOTE_PATH, "agent");

	}

	@Override
	protected void handleTenantBoot(Tenant arg0) throws Exception {
	}

}
