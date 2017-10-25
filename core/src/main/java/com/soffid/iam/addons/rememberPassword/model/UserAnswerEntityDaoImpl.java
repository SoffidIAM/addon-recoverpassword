//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.rememberPassword.model;

import com.soffid.iam.addons.rememberPassword.common.UserAnswer;

/**
 * DAO UserAnswerEntity implementation
 */
public class UserAnswerEntityDaoImpl extends UserAnswerEntityDaoBase
{

	/* (non-Javadoc)
	 * @see com.soffid.iam.addons.rememberPassword.model.UserAnswerEntityDaoBase#toUserAnswer(com.soffid.iam.addons.rememberPassword.model.UserAnswerEntity, com.soffid.iam.addons.rememberPassword.common.UserAnswer)
	 */
	@Override
	public void toUserAnswer(UserAnswerEntity source, UserAnswer target) {
		super.toUserAnswer(source, target);
		target.setUser(source.getUser().getUserName());
	}

	/* (non-Javadoc)
	 * @see com.soffid.iam.addons.rememberPassword.model.UserAnswerEntityDaoBase#userAnswerToEntity(com.soffid.iam.addons.rememberPassword.common.UserAnswer, com.soffid.iam.addons.rememberPassword.model.UserAnswerEntity, boolean)
	 */
	@Override
	public void userAnswerToEntity(UserAnswer source, UserAnswerEntity target,
			boolean copyIfNull) {
		super.userAnswerToEntity(source, target, copyIfNull);
		target.setUser(getUserEntityDao().findByUserName(source.getUser()));
	}
}
