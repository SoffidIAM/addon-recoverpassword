<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:zul="http://www.zkoss.org/2005/zul">

	<xsl:output method="xml" omit-xml-declaration="no" indent="yes" />
	
	<xsl:template match="div[@id='changePassword']/button[1]"
		priority="3">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
		<button label="${{c:l('recoverPass.Titol')}}" width="300px" 
			use="com.soffid.iam.addon.passrecover.web.ConfigureButton"
			style="margin-left:5px" onClick='es.caib.zkib.zkiblaf.Application.call("addon/passrecover/user_recoverpass.zul?custom=true")'/>
	</xsl:template>

	<xsl:template match="node()|@*" priority="2">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>