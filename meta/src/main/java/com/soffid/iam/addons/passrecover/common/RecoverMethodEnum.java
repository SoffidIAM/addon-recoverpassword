package com.soffid.iam.addons.passrecover.common;

import com.soffid.mda.annotation.Description;
import com.soffid.mda.annotation.Enumeration;

@Enumeration
@Description("Contains the implemented recovery methods")
public class RecoverMethodEnum {
	public String RECOVER_BY_MAIL = "mail";
	public String RECOVER_BY_QUESTIONS = "question";
}
