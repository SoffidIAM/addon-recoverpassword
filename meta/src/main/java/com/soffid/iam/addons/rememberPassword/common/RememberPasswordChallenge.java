//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.rememberPassword.common;
import com.soffid.mda.annotation.*;

import es.caib.seycon.ng.comu.Challenge;

@ValueObject 
public class RememberPasswordChallenge {
	@Nullable
	RecoverMethodEnum method;
	
	public java.lang.Long challengId;

	public java.util.Collection<com.soffid.iam.addons.rememberPassword.common.UserAnswer> questions;

	public boolean answered;

	public boolean finished;

	@Nullable
	public es.caib.seycon.ng.comu.Password password;

	public java.lang.String user;

	public java.util.Calendar challengeDate;

	public java.util.Calendar expirationDate;

	@Nullable
	public java.lang.String dispatcher;

	@Description ("PIN sent to the user by email")
	@Nullable
	public String emailPin;
	
	@Nullable
	@Description("OTP Challenge")
	Challenge otpChallenge;
}
