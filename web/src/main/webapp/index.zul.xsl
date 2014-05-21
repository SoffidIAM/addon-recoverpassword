<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:zul="http://www.zkoss.org/2005/zul">

	<xsl:output method="xml" omit-xml-declaration="no" indent="yes" />

	<xsl:template match="zul:div" priority="3">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>

		<zscript>
			<![CDATA[
			try {
				import com.soffid.iam.addons.rememberPassword.service.ejb.RememberPasswordServiceHome;
				import com.soffid.iam.addons.rememberPassword.service.ejb.RememberPasswordService;
				
				String userCode = Executions.getCurrent().getUserPrincipal().getName();
				
				javax.naming.Context context = new javax.naming.InitialContext();
				Object rememberPassHome = context.lookup(RememberPasswordServiceHome.JNDI_NAME);
				RememberPasswordService service = rememberPassHome.create();
				
				if (!service.checkUserConfiguration(userCode))
				{
					Executions.getCurrent().getDesktop()
						.getSession().setAttribute("paginaActual",
							"addon/remember-password/user_rememberpass.zul");
				}
			} catch (java.lang.SecurityException e) {
			} catch (javax.ejb.AccessLocalException e) {}
			]]>
		</zscript>
	</xsl:template>

	<xsl:template match="node()|@*" priority="2">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
