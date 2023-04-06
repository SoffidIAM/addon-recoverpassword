package com.soffid.iam.addons.passrecover.model;

import java.util.Date;

import com.soffid.iam.addons.passrecover.common.RecoverMethodEnum;
import com.soffid.iam.model.TenantEntity;
import com.soffid.mda.annotation.Column;
import com.soffid.mda.annotation.DaoFinder;
import com.soffid.mda.annotation.DaoOperation;
import com.soffid.mda.annotation.Entity;
import com.soffid.mda.annotation.Identifier;
import com.soffid.mda.annotation.Nullable;

import es.caib.seycon.ng.model.UsuariEntity;

@Entity(table = "SC_RPONGO")
public class OngoingChallengeEntity {
	@Column(name = "RPO_ID")
	@Nullable @Identifier Long id;
	
	@Column(name = "RPO_EXPDAT")
	Date expirationDate;
	
	@Column(name = "RPO_DATE")
	Date date;
	
	@Column(name = "RPO_USU_ID")
	Long userId;
	
	@Nullable
	@Column(name = "RPO_SYSTEM")
	String system;
	
	@Column(name = "RPO_ANSWER")
	boolean answered;
	
	@Column(name = "RPO_TYPE")
	RecoverMethodEnum type;
	
	@Nullable @Column(name = "RPO_QUESTIONS", length = 1024)
	String questions;
	
	@Nullable @Column(name = "RPO_VALUE", length = 100)
	String value;
	
	@Column(name = "RPO_TEN_ID")
	TenantEntity tenant;
	
	@Nullable @Column(name = "RPO_OTPHAN")
	String otpHandler;
	
	@Nullable @Column(name = "RPO_OTPCAR")
	String otpCard;
	
	@Nullable @Column(name = "RPO_OTPCEL")
	String otpCell;
	
	@Nullable @Column(name = "RPO_OTPID")
	String otpChallengeId;
	@DaoOperation void deleteExpired() {};
	
}
