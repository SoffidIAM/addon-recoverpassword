//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.passrecover.service;
import com.soffid.iam.addons.passrecover.common.MissconfiguredRecoverException;
import com.soffid.iam.service.MailService;
import com.soffid.mda.annotation.*;

import es.caib.seycon.ng.exception.BadPasswordException;
import es.caib.seycon.ng.exception.UnknownUserException;
import es.caib.seycon.ng.model.ServerEntity;
import es.caib.seycon.ng.servei.AccountService;

import org.springframework.transaction.annotation.Transactional;

@Service ( translatedName="RecoverPasswordUserService",
	 translatedPackage="com.soffid.iam.addons.passrecover.service",
	 grantees={com.soffid.iam.addons.passrecover.anonymous.class},
	 serverPath="/seycon/RecoverPasswordUserService")
@Depends ({es.caib.seycon.ng.model.AuditoriaEntity.class,
	com.soffid.iam.addons.passrecover.service.RecoverPasswordService.class,
	es.caib.seycon.ng.servei.InternalPasswordService.class,
	es.caib.seycon.ng.model.UsuariEntity.class,
	es.caib.seycon.ng.model.DispatcherEntity.class,
	com.soffid.iam.addons.passrecover.common.RecoverPasswordChallenge.class,
	AccountService.class,
	ServerEntity.class,
	MailService.class})
public abstract class RecoverPasswordUserService {

	/**
	 * 
	 * requestChallenge
	 * 
	 */
	@Transactional(rollbackFor={java.lang.Exception.class})
	@Description ("Request to recover an account password")
	public com.soffid.iam.addons.passrecover.common.RecoverPasswordChallenge requestChallenge	(
		java.lang.String account, 
		@Nullable java.lang.String dispatcher)
		throws es.caib.seycon.ng.exception.InternalErrorException, UnknownUserException, MissconfiguredRecoverException {
	 return null;
	}
	
	// =====================

	@Description ("Request to recover a user password")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.passrecover.common.RecoverPasswordChallenge requestChallenge(
		java.lang.String user)
		throws es.caib.seycon.ng.exception.InternalErrorException, UnknownUserException, MissconfiguredRecoverException {
	 return null;
	}

	@Transactional(rollbackFor={java.lang.Exception.class})
	@Description ("Second step. Answer user questions")
	public boolean responseChallenge(
		com.soffid.iam.addons.passrecover.common.RecoverPasswordChallenge challenge)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}

	@Transactional(rollbackFor={java.lang.Exception.class})
	@Description ("Third step. Generate a new password")
	public void resetPassword(
		com.soffid.iam.addons.passrecover.common.RecoverPasswordChallenge challenge)
		throws es.caib.seycon.ng.exception.InternalErrorException, BadPasswordException {
	}
	
	
}
