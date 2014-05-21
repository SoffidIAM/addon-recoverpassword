//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.rememberPassword.service;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service ( translatedName="RememberPasswordService",
	 translatedPackage="com.soffid.iam.addons.rememberPassword.service")
@Depends ({es.caib.seycon.ng.model.AuditoriaEntity.class,
	com.soffid.iam.addons.rememberPassword.model.DefaultQuestionEntity.class,
	com.soffid.iam.addons.rememberPassword.model.UserAnswerEntity.class,
	es.caib.seycon.ng.servei.UsuariService.class,
	es.caib.seycon.ng.servei.ConfiguracioService.class})
public abstract class RememberPasswordService {

	@Operation ( grantees={com.soffid.iam.addons.rememberPassword.remember_password_create.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.rememberPassword.common.DefaultQuestion create(
		com.soffid.iam.addons.rememberPassword.common.DefaultQuestion question)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.rememberPassword.remember_password_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<com.soffid.iam.addons.rememberPassword.common.DefaultQuestion> findAllDefaultQuestions()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.rememberPassword.remember_password_delete.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		com.soffid.iam.addons.rememberPassword.common.DefaultQuestion question)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.rememberPassword.remember_password_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.rememberPassword.common.DefaultQuestion update(
		com.soffid.iam.addons.rememberPassword.common.DefaultQuestion question)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.rememberPassword.remember_password_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void update(
		com.soffid.iam.addons.rememberPassword.common.RememberPassConfig config)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.rememberPassword.remember_password_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.rememberPassword.common.RememberPassConfig getRememberPassConfiguration()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.rememberPassword.tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<com.soffid.iam.addons.rememberPassword.common.UserAnswer> findUserAnswersByUserName(
		java.lang.String userName)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.rememberPassword.tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.rememberPassword.common.UserAnswer update(
		com.soffid.iam.addons.rememberPassword.common.UserAnswer answer)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.rememberPassword.tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.rememberPassword.common.UserAnswer create(
		com.soffid.iam.addons.rememberPassword.common.UserAnswer answer)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.rememberPassword.tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void remove(
		com.soffid.iam.addons.rememberPassword.common.UserAnswer answer)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.rememberPassword.tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean checkUserConfiguration(
		java.lang.String user)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
}
