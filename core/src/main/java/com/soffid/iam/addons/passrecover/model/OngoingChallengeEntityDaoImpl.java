package com.soffid.iam.addons.passrecover.model;

import java.util.Date;

import com.soffid.iam.utils.Security;

public class OngoingChallengeEntityDaoImpl extends OngoingChallengeEntityDaoBase {

	@Override
	protected void handleDeleteExpired() throws Exception {
		getSession().createQuery("delete from com.soffid.iam.addons.passrecover.model.OngoingChallengeEntityImpl "
				+ "where tenant.id=:tenantId and expirationDate < :now")
			.setLong("tenantId", Security.getCurrentTenantId())
			.setDate("now", new Date())
			.executeUpdate();
	}

}
