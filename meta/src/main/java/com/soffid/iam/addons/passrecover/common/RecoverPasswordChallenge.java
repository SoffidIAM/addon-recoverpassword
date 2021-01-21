//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.passrecover.common;
import com.soffid.mda.annotation.*;

@ValueObject 
public class RecoverPasswordChallenge {
	@Nullable
	RecoverMethodEnum method;
	
	public java.lang.Long challengId;

	public java.util.Collection<com.soffid.iam.addons.passrecover.common.UserAnswer> questions;

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
}
