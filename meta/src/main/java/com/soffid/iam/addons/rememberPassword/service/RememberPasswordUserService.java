//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.rememberPassword.service;
import com.soffid.iam.addons.rememberPassword.common.MissconfiguredRecoverException;
import com.soffid.iam.service.MailService;
import com.soffid.iam.service.OTPValidationService;
import com.soffid.mda.annotation.*;

import es.caib.seycon.ng.exception.BadPasswordException;
import es.caib.seycon.ng.exception.UnknownUserException;
import es.caib.seycon.ng.model.DadaUsuariEntity;
import es.caib.seycon.ng.model.ServerEntity;
import es.caib.seycon.ng.servei.AccountService;
import es.caib.seycon.ng.servei.DadesAddicionalsService;
import es.caib.seycon.ng.servei.UsuariService;

import org.springframework.transaction.annotation.Transactional;

@Service ( translatedName="RememberPasswordUserService",
	 translatedPackage="com.soffid.iam.addons.rememberPassword.service",
	 grantees={com.soffid.iam.addons.rememberPassword.anonymous.class},
	 serverPath="/seycon/RememberPasswordUserService")
@Depends ({es.caib.seycon.ng.model.AuditoriaEntity.class,
	com.soffid.iam.addons.rememberPassword.service.RememberPasswordService.class,
	es.caib.seycon.ng.servei.InternalPasswordService.class,
	es.caib.seycon.ng.model.UsuariEntity.class,
	es.caib.seycon.ng.model.DispatcherEntity.class,
	com.soffid.iam.addons.rememberPassword.common.RememberPasswordChallenge.class,
	AccountService.class,
	UsuariService.class,
	DadesAddicionalsService.class,
	DadaUsuariEntity.class,
	OTPValidationService.class,
	ServerEntity.class,
	MailService.class})
public abstract class RememberPasswordUserService {

	/**
	 * 
	 * requestChallenge
	 * 
	 */
	@Transactional(rollbackFor={java.lang.Exception.class})
	@Description ("Request to recover an account password")
	public com.soffid.iam.addons.rememberPassword.common.RememberPasswordChallenge requestChallenge	(
		java.lang.String account, 
		@Nullable java.lang.String dispatcher)
		throws es.caib.seycon.ng.exception.InternalErrorException, UnknownUserException, MissconfiguredRecoverException {
	 return null;
	}
	
	// =====================

	@Description ("Request to recover a user password")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.rememberPassword.common.RememberPasswordChallenge requestChallenge(
		java.lang.String user)
		throws es.caib.seycon.ng.exception.InternalErrorException, UnknownUserException, MissconfiguredRecoverException {
	 return null;
	}

	@Transactional(rollbackFor={java.lang.Exception.class})
	@Description ("Second step. Answer user questions")
	public boolean responseChallenge(
		com.soffid.iam.addons.rememberPassword.common.RememberPasswordChallenge challenge)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}

	@Transactional(rollbackFor={java.lang.Exception.class})
	@Description ("Third step. Generate a new password")
	public void resetPassword(
		com.soffid.iam.addons.rememberPassword.common.RememberPasswordChallenge challenge)
		throws es.caib.seycon.ng.exception.InternalErrorException, BadPasswordException {
	}
	
	
}
