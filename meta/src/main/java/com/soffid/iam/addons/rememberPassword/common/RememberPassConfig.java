//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.rememberPassword.common;
import com.soffid.mda.annotation.*;

@ValueObject 
public abstract class RememberPassConfig {

	public com.soffid.iam.addons.rememberPassword.common.RequireQuestion required;

	public java.lang.Integer number;

	public java.lang.Integer right;

}
