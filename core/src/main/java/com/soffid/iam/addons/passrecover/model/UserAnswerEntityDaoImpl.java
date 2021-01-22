//
// (C) 2013 Soffid
//
//

package com.soffid.iam.addons.passrecover.model;

import com.soffid.iam.addons.passrecover.common.UserAnswer;
import com.soffid.iam.addons.passrecover.model.UserAnswerEntity;
import com.soffid.iam.addons.passrecover.model.UserAnswerEntityDaoBase;

/**
 * DAO UserAnswerEntity implementation
 */
public class UserAnswerEntityDaoImpl extends UserAnswerEntityDaoBase
{

	/* (non-Javadoc)
	 */
	@Override
	public void toUserAnswer(UserAnswerEntity source, UserAnswer target) {
		super.toUserAnswer(source, target);
		target.setUser(source.getUser().getUserName());
	}

	/* (non-Javadoc)
	 */
	@Override
	public void userAnswerToEntity(UserAnswer source, UserAnswerEntity target,
			boolean copyIfNull) {
		super.userAnswerToEntity(source, target, copyIfNull);
		target.setUser(getUserEntityDao().findByUserName(source.getUser()));
	}
}
