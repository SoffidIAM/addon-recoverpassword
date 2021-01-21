//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.rememberPassword.common;
import com.soffid.mda.annotation.*;

@ValueObject 
public class RememberPassConfig {

	public com.soffid.iam.addons.rememberPassword.common.RequireQuestion required;

	@Description("Number of questions to fill in")
	public java.lang.Integer number;

	@Description("Number of questions to correctly answer to recover password")
	public java.lang.Integer right;
	
	@Description("Number of questions to answer to recover passwords")
	public java.lang.Integer questions;
	
	public boolean allowQuestionRecovery;
	
	public boolean allowMailRecovery;
	
	public boolean allowPasswordReuse;
	
	public boolean allowOtpRecovery;
	
	public boolean allowSmsRecovery;
	
	public RecoverMethodEnum preferredMethod ;
	
	@Nullable
	public String smsAttribute;

	@Nullable
	public String smsUrl;
	
	@Nullable
	public String smsMethod;
	
	@Nullable
	public String smsBody;
	
	@Nullable
	public String smsHeaders;

	@Nullable
	public String smsResponseToCheck;
}
