package com.soffid.iam.addons.rememberPassword.service;

import com.soffid.mda.annotation.Depends;
import com.soffid.mda.annotation.Service;

import es.caib.seycon.ng.servei.ApplicationBootService;

@Service(internal=true)
@Depends({RememberPasswordUserService.class})
public class BootService extends ApplicationBootService {

}
