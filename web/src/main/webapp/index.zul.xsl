<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:zul="http://www.zkoss.org/2005/zul">
e
	<xsl:output method="xml" omit-xml-declaration="no" indent="yes" />

	<xsl:template match="/zul:div" priority="3">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />

			<div use="com.soffid.iam.addon.passrecover.web.IndexFilter"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="node()|@*" priority="2">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
