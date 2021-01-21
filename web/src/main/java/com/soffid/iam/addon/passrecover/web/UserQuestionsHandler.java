package com.soffid.iam.addon.passrecover.web;

import org.springframework.web.servlet.ModelAndView;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import es.caib.seycon.ng.utils.Security;
import es.caib.zkib.component.DataModel;
import es.caib.zkib.component.DataTable;
import es.caib.zkib.datasource.CommitException;
import es.caib.zkib.datasource.XPathUtils;
import es.caib.zkib.zkiblaf.Application;

public class UserQuestionsHandler extends Window implements AfterCompose {

	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		super.onPageAttached(newpage, oldpage);
		DataModel model = getModel();
		String userCode = es.caib.seycon.ng.utils.Security.getCurrentUser();
		model.getVariables().declareVariable("userCode", userCode);
	}

	@Override
	public void afterCompose() {
		Label exp2 = (Label) getFellow("exp2");
		exp2.setValue(String.format(org.zkoss.util.resource.Labels.getLabel("recoverPass.zul.Explanation2"),
					new Object [] { com.soffid.iam.utils.ConfigurationCache.getProperty("addon.retrieve-password.query_number")}));
		
		Label exp3 = (Label) getFellow("exp3");
		exp3.setValue(String.format(org.zkoss.util.resource.Labels.getLabel("recoverPass.zul.Explanation3"),
					new Object [] {com.soffid.iam.utils.ConfigurationCache.getProperty("addon.retrieve-password.right_number")}));
	}
	
	public void close(Event event) throws CommitException {
		DataModel model = getModel();
		model.commit();
		Application.goBack();
	}

	public void addNew(Event event) {
		DataTable listbox = (DataTable) getFellow("listbox");
		listbox.addNew();
		XPathUtils.setValue(listbox, "user", Security.getCurrentUser());
		
	}
	
	public void changeAnswer(Event event) throws CommitException {
		String value = ((String[]) event.getData())[0];
		Component listbox = getFellow("listbox");
		XPathUtils.setValue(listbox, "answer", value);
		String q = (String) XPathUtils.eval(listbox, "question");
		if (q != null && value != null && !q.trim().isEmpty() && ! value.trim().isEmpty()) {
			DataModel model = getModel();
			model.commit();
		}
	}

	public void changeQuestion(Event event) throws CommitException {
		String value = ((String[]) event.getData())[0];
		Component listbox = getFellow("listbox");
		XPathUtils.setValue(listbox, "question", value);
		String a = (String) XPathUtils.eval(listbox, "answer");
		if (a != null && value != null && !a.trim().isEmpty() && ! value.trim().isEmpty()) {
			DataModel model = getModel();
			model.commit();
		}
	}

	public DataModel getModel() {
		return (DataModel) getPage().getFellow("model");
	}

	public void removeAnswer(Event event) throws CommitException {
		DataTable listbox = (DataTable) getFellow("listbox");
		listbox.delete();
		getModel().commit();
	}
}
