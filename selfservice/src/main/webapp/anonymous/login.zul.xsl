<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:zul="http://www.zkoss.org/2005/zul">

	<xsl:output method="xml" omit-xml-declaration="no" indent="yes" />

	<xsl:template match="/zul:zk/zul:html[@id='htmlData']/text()" priority="3">
		<![CDATA[
		<div class="login">
        		<p><img src="/anonymous/logo.png" alt="Soffid logo" /> </p> 
			<div class="loginbox" id="loginbox"> 
				<p>${c:l('configure.header')}</p>
				<form method="post" action="j_security_check" id='loginForm' accept-charset="UTF-8">
				<input name="j_method"  id="j_method_usu-pas" type="hidden" value="U"/>
				<input name="j_username" id="j_username" type="hidden" value="${user }">
				<input name="j_tenant" id="j_tenant" type="hidden" autocomplete="off"  onChange="updateUsername()" value="${tenant}"/>
				<p>
				
					<div class="inputlogin">${c:l('login.lblUser')} :
					<input name="j_account" id="j_account" type="text" autocomplete="off" autofocus onChange="updateUsername ()" /></div>
				</p>
				<p>
					<div class="inputlogin">${c:l('login.lblPassword')} :
					<input name="j_password" id="j_password" type="password" autocomplete="off" value="${password }"/></div>
				</p>
				<p style="color:red;">${error}</p>
				<input name="formUCboton" type="submit" value="${c:l('configure.Login')}">
				<a href="anonymous/remember_pass_questions.zul">
					${c:l('login.zul.retrieve')}
				</a>
		]]>
	</xsl:template>

	<xsl:template match="node()|@*" priority="2">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
