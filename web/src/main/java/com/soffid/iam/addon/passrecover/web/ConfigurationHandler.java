package com.soffid.iam.addon.passrecover.web;

import org.zkoss.zk.ui.event.Event;

import com.soffid.iam.web.component.FrameHandler;

import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.zkib.component.DataTable;
import es.caib.zkib.datasource.CommitException;
import es.caib.zkib.datasource.XPathUtils;
import es.caib.zkib.zkiblaf.Application;
import es.caib.zkib.zkiblaf.Frame;

public class ConfigurationHandler extends FrameHandler {
	private static final long serialVersionUID = 1L;

	public ConfigurationHandler() throws InternalErrorException {
		super();
	}

	public void commit(Event event) throws CommitException {
		if (applyNoClose(event))
			Application.goBack();
	}

	public void addNew(Event event) {
		DataTable listbox = (DataTable) getFellow("listbox");
		listbox.addNew();
	}
	
	public void changeQuestion(Event event) throws CommitException {
		String value = ((String[]) event.getData())[0];
		XPathUtils.setValue(getFellow("listbox"), "question", value);
		applyNoClose(event);
	}

	public void removeQuestion(Event event) throws CommitException {
		DataTable listbox = (DataTable) getFellow("listbox");
		listbox.delete();
		applyNoClose(event);
	}
}
