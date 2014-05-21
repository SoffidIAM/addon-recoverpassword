//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.rememberPassword.model;
import com.soffid.mda.annotation.*;

@Entity (table="SC_RPANSW" )
@Depends ({com.soffid.iam.addons.rememberPassword.common.UserAnswer.class,
	com.soffid.iam.addons.rememberPassword.model.DefaultQuestionEntity.class,
	es.caib.seycon.ng.model.UsuariEntity.class})
public abstract class UserAnswerEntity {

	@Column (name="ANS_ANSWID")
	@Identifier
	public java.lang.Long id;

	@Column (name="ANS_QUESID")
	public java.lang.String question;

	@Column (name="ANS_ANSWER")
	@Nullable
	public java.lang.String answer;

	@ForeignKey (foreignColumn="DQU_ANSWID")
	public java.util.Collection<com.soffid.iam.addons.rememberPassword.model.DefaultQuestionEntity> questionToAnswer;

	@Column (name="ANS_USER")
	@Nullable
	public es.caib.seycon.ng.model.UsuariEntity user;

	@DaoFinder("select userAnswer from com.soffid.iam.addons.rememberPassword.model.UserAnswerEntity as userAnswer where userAnswer.user.codi = :userCode")
	public java.util.List<com.soffid.iam.addons.rememberPassword.model.UserAnswerEntity> findByUser(
		java.lang.String userCode) {
	 return null;
	}
}
