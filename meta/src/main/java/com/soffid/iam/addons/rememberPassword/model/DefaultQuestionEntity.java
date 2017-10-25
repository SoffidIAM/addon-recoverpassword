//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.rememberPassword.model;
import com.soffid.iam.model.TenantEntity;
import com.soffid.mda.annotation.*;

@Entity (table="SC_RPQUES" )
@Depends ({com.soffid.iam.addons.rememberPassword.common.DefaultQuestion.class,
	com.soffid.iam.addons.rememberPassword.model.UserAnswerEntity.class})
public abstract class DefaultQuestionEntity {

	@Column (name="DQU_QUESTID")
	@Identifier
	public java.lang.Long id;

	@Column (name="DQU_QUESTION")
	public java.lang.String question;

	@Column (name="DQU_ANSWID")
	@Nullable
	public com.soffid.iam.addons.rememberPassword.model.UserAnswerEntity answerToQuestion;

	@Column (name="DQU_TEN_ID")
	@Nullable
	TenantEntity tenant;
}
