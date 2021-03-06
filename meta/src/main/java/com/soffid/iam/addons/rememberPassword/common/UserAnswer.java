//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.rememberPassword.common;
import com.soffid.mda.annotation.*;

@ValueObject 
public class UserAnswer {

	@Nullable
	public java.lang.Long id;

	public java.lang.String question;

	public java.lang.String answer;

	public java.lang.String user;

}
