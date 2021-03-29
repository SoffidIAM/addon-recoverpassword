package com.soffid.iam.addon.passrecover.web;

import java.util.Iterator;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;

import com.soffid.iam.addons.passrecover.common.UserAnswer;
import com.soffid.iam.addons.passrecover.service.ejb.RecoverPasswordUserService;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.zkib.component.Div;
import es.caib.zkib.zkiblaf.Missatgebox;

public class RecoverPasswordHandler extends Div implements AfterCompose {
	com.soffid.iam.addons.passrecover.service.ejb.RecoverPasswordUserService ejb ;
	com.soffid.iam.addons.passrecover.common.RecoverPasswordChallenge challenge = null;
	private Textbox txt_user;
	private Html policy;
	private Grid grid_user_answers;
	private Div user_code_req;
	private Div user_questions;
	private Div resetPassword;
	private Label userName;
	private Textbox passnueva1;
	private Textbox passnueva2;
	
	public void afterCompose() {
		try
		{
			ejb = (RecoverPasswordUserService) new javax.naming.InitialContext().
					lookup(com.soffid.iam.addons.passrecover.service.ejb.RecoverPasswordUserServiceHome.JNDI_NAME);
			
			txt_user = (Textbox) getFellow("txt_user");
			policy = (Html) getFellow("policy");
			grid_user_answers = (Grid) getFellow("grid_user_answers");
			user_code_req = (Div) getFellow("user_code_req");
			user_questions = (Div) getFellow("user_questions");
			resetPassword = (Div) getFellow("resetPassword");
			userName = (Label) getFellow("userName");
			passnueva1 = (Textbox) getFellow("passnueva1");
			passnueva2 = (Textbox) getFellow("passnueva2");
		}
		catch (Exception ex){
			throw new UiException(ex);
		}
	}
	

	public void showPolicy () throws InternalErrorException
	{
		es.caib.seycon.ng.servei.PasswordService ps = es.caib.seycon.ng.ServiceLocator.instance().getPasswordService();
		String dispatcher = ps.getDefaultDispatcher();
		
		String []pds = ps.getPolicyDescription( txt_user.getValue(), dispatcher).split("\n");
		StringBuffer policyDescription = new StringBuffer();
		for (String pd: pds)
		{
			if (pd.startsWith("- "))
				policyDescription.append("    ").append(pd).append("\n");
			else
				policyDescription.append("<LI>").append(pd).append("</LI>");
		}
		policy.setContent("<pre style=\"white-space: pre-line; font-size: small\">"+policyDescription.toString()+"</pre>");

	}
	
	public void show_user_questions()
	{
		String userCode = txt_user.getValue();
		try {
			challenge = ejb.requestChallenge(userCode, null);
			
			Rows rows = grid_user_answers.getRows();
			Iterator it = rows.getChildren().iterator();
			while (it.hasNext()) 
			{
				it.next();
				it.remove ();
			}
			
			it = challenge.getQuestions().iterator();
			while (it.hasNext())
			{
				UserAnswer ua = (UserAnswer) it.next();
				Row r = new Row();
				Label l  = new Label (ua.getQuestion());
				l.setParent(r);
				Textbox tb = new Textbox();
				tb.setParent(r);
				r.setParent(rows);
			}
		} catch (es.caib.seycon.ng.exception.InternalErrorException e) {
			es.caib.zkib.zkiblaf.Missatgebox.info(org.zkoss.util.resource
					.Labels.getLabel("recoverPass.zul.ObtainQuestionsError") + e.getMessage());
			return;
		} catch (es.caib.seycon.ng.exception.UnknownUserException e) {
			es.caib.zkib.zkiblaf.Missatgebox.info(org.zkoss.util.resource
					.Labels.getLabel("recoverPass.zul.ObtainQuestionsError") + e.getMessage());
			return;
		} catch (com.soffid.iam.addons.passrecover.common.MissconfiguredRecoverException e) {
			es.caib.zkib.zkiblaf.Missatgebox.info(org.zkoss.util.resource
					.Labels.getLabel("recoverPass.zul.ObtainQuestionsError") + e.getMessage());
			return;
		}
		
		user_code_req.setVisible(false);
		user_questions.setVisible(true);
	}
	
	public void answerQuestions ()
	{
		Rows rows = grid_user_answers.getRows();
		Iterator it = challenge.getQuestions().iterator();
		Iterator it2 = rows.getChildren().iterator();
		while (it.hasNext())
		{
			Row r = (Row) it2.next();
			UserAnswer ua = (UserAnswer) it.next();
			Textbox tb = (Textbox) r.getChildren().get(1);
			ua.setAnswer(tb.getText());
		}
		try {
			if (ejb.responseChallenge(challenge))
			{
				resetPassword.setVisible(true);
				userName.setValue( txt_user.getValue());
				user_questions.setVisible (false);
				showPolicy ();
			} else {
				es.caib.zkib.zkiblaf.Missatgebox.info(org.zkoss.util.resource
						.Labels.getLabel("recoverPass.zul.IncorrectQuestions"));
			}
		} catch (es.caib.seycon.ng.exception.InternalErrorException e) {
			es.caib.zkib.zkiblaf.Missatgebox.info(org.zkoss.util.resource
					.Labels.getLabel("recoverPass.zul.ObtainQuestionsError") + e.getMessage());
		}
	}
	
	public void goBack()
	{
		es.caib.zkib.zkiblaf.Application.goBack();
	}
		
	public void changepass()
	{
		// Obtenim els camps actuals
		String contraNova1 = passnueva1.getValue();
		String contraNova2 = passnueva2.getValue();
		
		// Check void passwords
		if (contraNova1 == null || contraNova1.trim().isEmpty() ||
				contraNova2 == null || contraNova2.trim().isEmpty())
		{
			Missatgebox.error(org.zkoss.util.resource
					.Labels.getLabel("changepass.Omplir"),
				org.zkoss.util.resource.Labels.getLabel("changepass.CanviPWD"));
			return;
		}
		
		// Check new passwords
		if (!contraNova1.equals(contraNova2))
		{
			Missatgebox.error(org.zkoss.util.resource
					.Labels.getLabel("changepass.Coincidir"),
				org.zkoss.util.resource.Labels.getLabel("changepass.CanviPWD"));
			return;
		}

		challenge.setPassword(new es.caib.seycon.ng.comu.Password(contraNova1));
		try
		{
			ejb.resetPassword(challenge);
			getDesktop().getSession().setAttribute("samlLoginToken", new String [] {
					com.soffid.iam.utils.Security.getCurrentTenantName()+"\\"+txt_user.getValue(), 
					challenge.getPassword().getPassword()
				});
			Missatgebox.confirmaOK(org.zkoss.util.resource.Labels.getLabel("changepass.CanviOK"),
						org.zkoss.util.resource.Labels.getLabel("changepass.CanviPWD"),
					(ev) -> {
						Executions.sendRedirect("/");
					});
			{
			}
		}
		
		catch (es.caib.seycon.ng.exception.BadPasswordException e)
		{
			Missatgebox.error(String.format(org.zkoss.util.resource
						.Labels.getLabel("changepass.PWDAntic4"),
					new Object[] { e.getMessage() }),
				org.zkoss.util.resource.Labels.getLabel("changepass.PWDInvalid"));
		}
		catch (Throwable e)
		{
			System.out.println ("ERROR");
			String msg = e.getMessage();
			msg = (msg != null) ? ": " + msg : "";
			Missatgebox.error(String.format(org.zkoss.util.resource
					.Labels.getLabel("changepass.Error"),
				new Object [] {msg}));
		}
	}

	public void cancel(Event event) {
		Executions.sendRedirect("/");
	}
	
	public void gopassnueva2 (Event event) {
		passnueva2.setFocus(true);
	}
}
