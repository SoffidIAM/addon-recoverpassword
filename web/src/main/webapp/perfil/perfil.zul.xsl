<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:zul="http://www.zkoss.org/2005/zul">

	<xsl:output method="xml" omit-xml-declaration="no" indent="yes" />
	
	<xsl:template match="/zk/frame/tabbox/tabpanels/tabpanel[1]/button[1]"
		priority="3">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
		<button label="${{c:l('rememberPass.Titol')}}" width="200px"
			style="margin-left:5px" onClick="show_remember_password_page()"/>
			<zscript>
				<![CDATA[
					void show_remember_password_page()
					{
						es.caib.zkib.zkiblaf.Application
								.call("addon/remember-password/user_rememberpass.zul?custom=true");
					}
				]]>
			</zscript>
	</xsl:template>

	<xsl:template match="node()|@*" priority="2">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>