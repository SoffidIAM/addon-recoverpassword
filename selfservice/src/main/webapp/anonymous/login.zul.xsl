<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:zul="http://www.zkoss.org/2005/zul">

	<xsl:output method="xml" omit-xml-declaration="no" indent="yes" />

	<xsl:template match="/zul:zk/zul:html/text()" priority="3">
		<![CDATA[
			<div class="login">
				<p>
					<img src="/anonymous/logo.png" alt="Soffid logo" />
				</p>
				
				<div class="loginbox">
					<p>${c:l('configure.header')}</p>
					
					<form method="post" action="/j_security_check" accept-charset="UTF-8">
						<input id="j_method_usu-pas" name="j_method" 
							type="hidden" value="U"/>
						
						<p>
							<div class="inputlogin">${c:l('login.lblUser')}:
								<input id="j_username" name="j_username" type="text"
									autocomplete="off" />
							</div>
						</p>
						
						<p>
							<div class="inputlogin">${c:l('login.lblPassword')}:
								<input id="j_password" name="j_password"
									type="password" autocomplete="off" />
							</div>
						</p>
						
						<p style="color:red;">${error}</p>
						<input name="formUCboton" type="submit" value="${c:l('configure.Login')}"/>
						
						<a href="anonymous/remember_pass_questions.zul">
							${c:l('login.zul.retrieve')}
						</a>
					</form>
				</div>
			</div>
		]]>
	</xsl:template>

	<xsl:template match="node()|@*" priority="2">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
