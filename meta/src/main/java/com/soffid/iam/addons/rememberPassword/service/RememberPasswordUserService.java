//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.rememberPassword.service;
import com.soffid.mda.annotation.*;

import es.caib.seycon.ng.exception.BadPasswordException;
import es.caib.seycon.ng.exception.UnknownUserException;

import org.springframework.transaction.annotation.Transactional;

@Service ( translatedName="RememberPasswordUserService",
	 translatedPackage="com.soffid.iam.addons.rememberPassword.service",
	 grantees={com.soffid.iam.addons.rememberPassword.anonymous.class})
@Depends ({es.caib.seycon.ng.model.AuditoriaEntity.class,
	com.soffid.iam.addons.rememberPassword.service.RememberPasswordService.class,
	es.caib.seycon.ng.servei.InternalPasswordService.class,
	es.caib.seycon.ng.model.UsuariEntity.class,
	es.caib.seycon.ng.model.DispatcherEntity.class,
	com.soffid.iam.addons.rememberPassword.common.RememberPasswordChallenge.class})
public abstract class RememberPasswordUserService {

	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.rememberPassword.common.RememberPasswordChallenge requestChallenge(
		java.lang.String user, 
		@Nullable java.lang.String dispatcher)
		throws es.caib.seycon.ng.exception.InternalErrorException, UnknownUserException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean responseChallenge(
		com.soffid.iam.addons.rememberPassword.common.RememberPasswordChallenge challenge)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void resetPassword(
		com.soffid.iam.addons.rememberPassword.common.RememberPasswordChallenge challenge)
		throws es.caib.seycon.ng.exception.InternalErrorException, BadPasswordException {
	}
}
