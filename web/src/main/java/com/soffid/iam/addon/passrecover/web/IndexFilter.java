package com.soffid.iam.addon.passrecover.web;

import org.zkoss.zk.ui.ext.AfterCompose;

import com.soffid.iam.addons.passrecover.service.ejb.RecoverPasswordService;
import com.soffid.iam.addons.passrecover.service.ejb.RecoverPasswordServiceHome;
import com.soffid.iam.utils.Security;

import es.caib.zkib.component.Div;

public class IndexFilter extends Div implements AfterCompose {

	@Override
	public void afterCompose() {
		try {
			
			javax.naming.Context context = new javax.naming.InitialContext();
			String user = Security.getCurrentUser();
			if (user != null)
			{
				RecoverPasswordService service = (RecoverPasswordService) context.lookup(RecoverPasswordServiceHome.JNDI_NAME);;
				
				if (!service.checkUserConfiguration(user))
				{
		            es.caib.zkib.zkiblaf.Application.call("/addon/passrecover/user_recoverpass.zul");
				}
			}
		} catch (Exception e) {
		}
	}

}
