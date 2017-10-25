<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:zul="http://www.zkoss.org/2005/zul">

	<xsl:output method="xml" omit-xml-declaration="no" indent="yes" />

	<xsl:template match="/zul:vbox | /zul:box | /zul:zk" priority="3">
		<zul:vbox>
			<xsl:apply-templates select="@*" />
			<attribute name="onCreate">
				<![CDATA[
				try {
					import com.soffid.iam.addons.rememberPassword.service.ejb.RememberPasswordServiceHome;
					import com.soffid.iam.addons.rememberPassword.service.ejb.RememberPasswordService;
					
					javax.naming.Context context = new javax.naming.InitialContext();
					user = context.lookup (es.caib.seycon.ng.servei.ejb.UsuariServiceHome.JNDI_NAME)
						.create()
						.getCurrentUsuari();
					if (user != null)
					{
						Object rememberPassHome = context.lookup(RememberPasswordServiceHome.JNDI_NAME);
						RememberPasswordService service = rememberPassHome.create();
						
						if (!service.checkUserConfiguration(user.getCodi()))
						{
							questions_window.setVisible(true);
							questions_window.doHighlighted();
						}
					}
				} catch (java.lang.SecurityException e) {
				} catch (javax.ejb.AccessLocalException e) {}
				]]>
			</attribute>
			<xsl:apply-templates select="node()" />
		</zul:vbox>
	</xsl:template>

	<xsl:template match="zul:div[@id='appletFirmaDiv']" priority="3">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>

		<window id="questions_window" position="center, center" closable="true"
			visible="false" title="${{c:l('rememberPass.zul.Title')}}" width="800px"
			sclass="show-pass" onClose="self.setVisible(false); event.stopPropagation()">
			
			<datamodel id="model" rootNode="remember-password"
				src="addon/remember-password/descRememberPass.xml" />
			<zscript>

				<![CDATA[
					import es.caib.zkib.datasource.*;
					
					try
					{
						es.caib.zkib.zkiblaf.Application
							.setTitle(org.zkoss.util.resource.Labels
									.getLabel("rememberPass.zul.Title"));
					}
					
					catch (Exception ex){}
					
					String userCode = es.caib.seycon.ng.utils.Security.getCurrentUser();
					model.getVariables().declareVariable("userCode", userCode);
					
					void onNewRow(data)
					{
						row = data.getChildren().get(0).getChildren();
						
						if (row.get(0).getValue() == "")
						{
							row.get(0).setVisible(false);
							row.get(1).setVisible(true);
							row.get(1).focus();
						}
					}
				]]>
			</zscript>

			<vbox>
				<div>
					<label value="${{c:l('rememberPass.zul.Explanation1')}}" />
					<separator />

					<label onCreate="GetExplanation2Value()">
						<zscript>
							<![CDATA[
								void GetExplanation2Value()
								{
									self.setValue(String.format(org.zkoss.util.resource
										.Labels.getLabel("rememberPass.zul.Explanation2"),
											new Object [] {System.getProperty("addon.retrieve-password.query_number")}));
								}
							]]>
						</zscript>
					</label>
					<separator />

					<label onCreate="GetExplanation3Value()">
						<zscript>
							<![CDATA[
								void GetExplanation3Value()
								{
									self.setValue(String.format(org.zkoss.util.resource
										.Labels.getLabel("rememberPass.zul.Explanation3"),
											new Object [] {System.getProperty("addon.retrieve-password.right_number")}));
								}
							]]>
						</zscript>
					</label>
				</div>
				<separator />

				<grid id="grid_user_answers"
					dataPath="/questions_window/model:/user_answers"
					onNewRow="onNewRow(event.data)">
					<columns>
						<column label="${{c:l('rememberPass.zul.Question2')}}" />
						<column label="${{c:l('rememberPass.zul.Answer')}}" />
						<column width="2%">
							<imageclic src="~./img/list-add.gif"
								tooltiptext="${{c:l('rememberPass.zul.AddNew')}}">
								<attribute name="onClick">
									<![CDATA[
										Component cmp = self.getParent().getParent().getParent();
										ListModel modelo = cmp.getModel();
										DataSource datasource = cmp.getDataSource();
										Object position = modelo.newInstance();
										JXPContext ctx = datasource.getJXPathContext();
										String xpath = cmp.getXPath() + modelo.getBind(position);
										es.caib.zkib.jxpath.Pointer pointer = ctx.createPath(xpath);
										
										ctx.getRelativeContext(pointer).setValue("@user", userCode);
										
										pointer.invalidate();
									]]>
								</attribute>
							</imageclic>
						</column>
					</columns>

					<datarow>
						<div>
							<label bind="@question" width="99%"
								onClick='self.visible = false; self.parent.getChildren().get(1).visible=true;' />
							<textbox id="question" sclass="textbox" bind="@question"
								visible="false" width="99%" style="border: 0px;" />
						</div>

						<textbox id="answer" sclass="textbox" bind="@answer" width="99%" />

						<imageclic src="~./img/list-remove.gif"
							tooltiptext="${{c:l('rememberPass.zul.Delete')}}">
							<attribute name="onClick">
								<![CDATA[
									Component currentRow = self.getParent();
									ListModel modelo = currentRow.getGrid().getModel();
									int pos = currentRow.getParent().getChildren().indexOf(currentRow);
									Object modelPos = modelo.getElementAt(pos);
									String xpath = modelPos.getDataContext().getXPath();
									DataSource dataSource = grid_user_answers.getDataSource();
									JXPContext context = dataSource.getJXPathContext();
									Object data = context.getValue(xpath + "/@question");
									
									context.removePath(xpath);
								]]>
							</attribute>
						</imageclic>
					</datarow>
				</grid>
				
				<div align="center">
					<separator/>
					<button id="save_remember_password" label="${{c:l('rememberPass.zul.Close')}}">
						<attribute name="onClick">
							<![CDATA[
								model.commit();
								
								//questions_window.detach();
								questions_window.setVisible(false);
							]]>
						</attribute>
					</button>
				</div>
			</vbox>
		</window>
	</xsl:template>

	<xsl:template match="node()|@*" priority="2">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
