<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- set if you want to generate html descriptions -->
<!--
<xsl:output indent="yes" method="html"/>
-->
  <xsl:output
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
    encoding="UTF-8" indent="yes" method="xml"
    media-type="text/html"
    omit-xml-declaration="yes"/>

<xsl:template match="/">

<xsl:variable name="tab" select="'&#9;'"/>
<xsl:variable name="newline" select="'&#10;'"/>
<xsl:variable name="slash" select="'&#47;'"/>


<html>
<head>
<title>SNMP Trap Test suite</title>
</head>
<body>
<a name="top"></a>
<xsl:call-template name="print_agents"/>
<xsl:call-template name="print_html_index"/>

<xsl:for-each select="/traps/test">
<xsl:sort select="host"/>
    <xsl:call-template name="print_test"/>
    <p> 
        <a href="#top">Back to Top</a>
    </p>
</xsl:for-each>
</body>
</html>
<xsl:value-of select="$newline"/>
</xsl:template>



<xsl:template name="print_html_index">
  <h3>List of hosts that send traps</h3>
  <ul>
      <xsl:for-each select="/traps/test">
      <xsl:sort select="host"/>
          <li>
              <xsl:variable name="name">
                  <xsl:call-template name="link"/>
              </xsl:variable>
              <a><xsl:attribute name="href">
                     <xsl:text>#h</xsl:text>
                     <xsl:value-of select="$name"/>
                 </xsl:attribute>
                 <xsl:value-of select="$name"/>
              </a>
          </li>
      </xsl:for-each>
  </ul>
</xsl:template>


<xsl:template name="link">
    <xsl:value-of select="host"/>
    <xsl:text>_</xsl:text>
    <xsl:value-of select="version/@no"/>
    <xsl:for-each select="usm">
            <xsl:text>_</xsl:text>
            <xsl:value-of select="auth/@ado"/>
            <xsl:if test="auth/@ado='yes'">
                    <xsl:text>_</xsl:text>
                    <xsl:value-of select="auth/aproto"/>
                    <xsl:text>_</xsl:text>
                    <xsl:value-of select="priv/@pdo"/>
                    <xsl:if test="priv/@pdo='yes'">
                        <xsl:text>_</xsl:text>
                        <xsl:value-of select="priv/pproto"/>
                    </xsl:if>
            </xsl:if>
    </xsl:for-each>
</xsl:template>


<xsl:template name="print_agents">
<a name="agents"></a><h1>Trap Test Suite - List of Test SNMP agents</h1>
<table width="95%" border="1">
<tr>
<th>Platform</th> <th>Machine</th> <th>SNMPv1</th> <th>SNMPv2c</th>
<th>SNMPv3</th> <th>IPv?</th>
</tr>

<tr>
<td>Windows 2000</td>
<td>selidor</td>
<td>Y </td>
<td>Y </td>
<td>N </td>
<td>IPv4 </td>
</tr>

<tr>
<td>NET-SNMP on Linux RedHat 7.2</td>
<td>way</td>
<td>Y </td>
<td>Y </td>
<td>Y </td>
<td>IPv4 </td>
</tr>

<tr>
<td>NET-SNMP on Linux Suse 10.0</td>
<td>apso</td>
<td>Y </td>
<td>Y </td>
<td>Y </td>
<td>IPv4 </td>
</tr>

<tr>
<td>NET-SNMP on Fedora core 4</td>
<td>hort</td>
<td>Y </td>
<td>Y </td>
<td>Y </td>
<td>IPv6 </td>
</tr>

<tr>
<td>Mac OS X 10.4</td>
<td>sleep</td>
<td>N </td>
<td>N </td>
<td>N </td>
<td>IPv6 </td>
</tr>

<tr>
<td>Solaris 2.8</td>
<td>roke</td>
<td>Y </td>
<td>N </td>
<td>N </td>
<td>IPv6 </td>
</tr>

</table>
</xsl:template>

<xsl:template name="print_test">
    <xsl:variable name="name">
        <xsl:call-template name="link"/>
    </xsl:variable>
    <a><xsl:attribute name="name">
           <xsl:text>h</xsl:text>
           <xsl:value-of select="$name"/>
       </xsl:attribute>
    </a>

    <h2><xsl:value-of select="$name"/></h2>
      <table width="95%" border="1">
      <tr>
          <td><b>Version</b></td>
          <td><b>Host</b></td>
          <td><b>Port</b></td>
          <td><b>Socket</b></td>
          <xsl:for-each select="community">
                  <td><b>Community</b></td>
          </xsl:for-each>
      </tr>
      <tr>
          <td><xsl:value-of select="version/@no"/></td>
          <td><xsl:value-of select="host"/></td>
          <td><xsl:value-of select="port"/></td>
          <td><xsl:value-of select="socket_type/@type"/></td>
          <xsl:for-each select="community">
                  <td><xsl:value-of select="."/></td>
          </xsl:for-each>
      </tr>
      <xsl:for-each select="usm">
          <tr><td colspan="4"> </td></tr>
          <tr>
              <td><b>Username</b></td>
              <td><b>Context Id</b></td>
              <td><b>Context Name</b></td>
          </tr>
          <tr>
              <td><xsl:value-of select="username"/></td>
              <td><xsl:value-of select="context/id"/></td>
              <td><xsl:value-of select="context/name"/></td>
          </tr>
          <tr><td colspan="4"> </td></tr>
          <tr>
              <td><b>Authetication ?</b></td>
              <xsl:if test="auth/@ado='yes'">
                      <td><b>Auth Protocol</b></td>
                      <td><b>Auth Password</b></td>
                      <td><b>Privacy ?</b></td>
                      <xsl:if test="priv/@pdo='yes'">
                              <td><b>Priv Password</b></td>
                      </xsl:if>
              </xsl:if>
          </tr>
          <tr>
              <td><xsl:value-of select="auth/@ado"/></td>
              <xsl:if test="auth/@ado='yes'">
                      <td><xsl:value-of select="auth/aproto"/></td>
                      <td><xsl:value-of select="auth/apassw"/></td>
                      <td><xsl:value-of select="priv/@pdo"/></td>
                      <xsl:if test="priv/@pdo='yes'">
                              <td><xsl:value-of select="priv/ppassw"/></td>
                      </xsl:if>
              </xsl:if>
          </tr>
      </xsl:for-each>
      </table>
</xsl:template>

<xsl:template name="oid">
    <td><xsl:value-of select="variable"/></td>
    <td><xsl:value-of select="value"/></td>
    <td><xsl:value-of select="comment"/></td>
</xsl:template>

</xsl:stylesheet>
