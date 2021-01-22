<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:zul="http://www.zkoss.org/2005/zul">

	<xsl:output method="xml" omit-xml-declaration="no" indent="yes" />

	<xsl:template match="/zul:zk/zul:html[3]/text()" priority="3">
		<![CDATA[
				<p style="padding-top: 15px">
					<a href="${execution.contextPath}/anonymous/recover_pass_questions.zul">
						${c:l('login.zul.retrieve')}
					</a>
				</p>
		]]>
		<xsl:value-of select="."/>
	</xsl:template>

	<xsl:template match="node()|@*" priority="2">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
