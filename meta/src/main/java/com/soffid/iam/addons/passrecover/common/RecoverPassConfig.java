//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.passrecover.common;
import com.soffid.mda.annotation.*;

@ValueObject 
public class RecoverPassConfig {

	public com.soffid.iam.addons.passrecover.common.RequireQuestion required;

	@Description("Number of questions to fill in")
	public java.lang.Integer number;

	@Description("Number of questions to correctly answer to recover password")
	public java.lang.Integer right;
	
	@Description("Number of questions to answer to recover passwords")
	public java.lang.Integer questions;
	
	public boolean allowQuestionRecovery;
	
	public boolean allowMailRecovery;
	
	public boolean allowPasswordReuse;
	
	public RecoverMethodEnum preferredMethod ;

}
