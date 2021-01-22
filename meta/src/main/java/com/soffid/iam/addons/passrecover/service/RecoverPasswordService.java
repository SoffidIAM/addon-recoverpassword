//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.passrecover.service;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service ( translatedName="RecoverPasswordService",
	 translatedPackage="com.soffid.iam.addons.passrecover.service")
@Depends ({es.caib.seycon.ng.model.AuditoriaEntity.class,
	com.soffid.iam.addons.passrecover.model.DefaultQuestionEntity.class,
	com.soffid.iam.addons.passrecover.model.UserAnswerEntity.class,
	es.caib.seycon.ng.servei.UsuariService.class,
	es.caib.seycon.ng.servei.ConfiguracioService.class})
public abstract class RecoverPasswordService {

	@Operation ( grantees={com.soffid.iam.addons.passrecover.recover_password_create.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.passrecover.common.DefaultQuestion create(
		com.soffid.iam.addons.passrecover.common.DefaultQuestion question)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.passrecover.recover_password_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<com.soffid.iam.addons.passrecover.common.DefaultQuestion> findAllDefaultQuestions()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.passrecover.recover_password_delete.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		com.soffid.iam.addons.passrecover.common.DefaultQuestion question)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.passrecover.recover_password_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.passrecover.common.DefaultQuestion update(
		com.soffid.iam.addons.passrecover.common.DefaultQuestion question)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.passrecover.recover_password_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void update(
		com.soffid.iam.addons.passrecover.common.RecoverPassConfig config)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.passrecover.recover_password_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.passrecover.common.RecoverPassConfig getRecoverPassConfiguration()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.passrecover.tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<com.soffid.iam.addons.passrecover.common.UserAnswer> findUserAnswersByUserName(
		java.lang.String userName)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.passrecover.tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.passrecover.common.UserAnswer update(
		com.soffid.iam.addons.passrecover.common.UserAnswer answer)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.passrecover.tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.addons.passrecover.common.UserAnswer create(
		com.soffid.iam.addons.passrecover.common.UserAnswer answer)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={com.soffid.iam.addons.passrecover.tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void remove(
		com.soffid.iam.addons.passrecover.common.UserAnswer answer)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={com.soffid.iam.addons.passrecover.tothom.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean checkUserConfiguration(
		java.lang.String user)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
}
