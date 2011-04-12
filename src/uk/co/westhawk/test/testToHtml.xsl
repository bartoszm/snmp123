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
<title>SNMP Test suite</title>
</head>
<body>
<a name="top"></a>
<xsl:call-template name="print_agents"/>
<xsl:call-template name="print_html_index"/>

<xsl:for-each select="/tests/test">
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
  <h3>List of tests</h3>
  <ul>
      <xsl:for-each select="/tests/test">
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
<a name="agents"></a><h1>Manager Test Suite - List of Test SNMP agents</h1>
<table width="95%" border="1">
<tr>
<th>Platform</th> <th>Machine</th> <th>SNMPv1</th> <th>SNMPv2c</th>
<th>SNMPv3</th> <th>IPv?</th> <th>note</th>
</tr>

<tr>
<td>MG-SOFT on NT</td>
<td>212.30.73.70</td>
<td>N </td>
<td>N </td>
<td>Y </td>
<td>IPv4 </td>
<td>See MG-SOFT SNMPv3 impl for Windows 
<a href="http://www.mg-soft.com/snmpv3.html">remote</a></td>
</tr>

<tr>
<td>Router Cisco 800 series</td>
<td>host50</td>
<td>Y </td>
<td>Y </td>
<td>Y </td>
<td>IPv4 </td>
</tr>

<tr>
<td>Windows XP</td>
<td>jessage</td>
<td>Y </td>
<td>Y </td>
<td>N </td>
<td>IPv4 </td>
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
<td>Solaris 2.8</td>
<td>roke</td>
<td>Y </td>
<td>N </td>
<td>N </td>
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
    <table width="95%" border="0">
    <tr>
    <td width="35%" valign="top">
      <table width="100%" border="1">
      <tr>
          <td><b>Version</b></td>
          <td><xsl:value-of select="version/@no"/></td>
      </tr>
      <tr>
          <td><b>Host</b></td>
          <td><xsl:value-of select="host"/></td>
      </tr>
      <tr>
          <td><b>Port</b></td>
          <td><xsl:value-of select="port"/></td>
      </tr>
      <tr>
          <td><b>Socket</b></td>
          <td><xsl:value-of select="socket_type/@type"/></td>
      </tr>
      <xsl:for-each select="community">
          <tr>
              <td><b>Community</b></td>
              <td><xsl:value-of select="."/></td>
          </tr>
      </xsl:for-each>
      <xsl:for-each select="usm">
          <tr>
              <td><b>Username</b></td>
              <td><xsl:value-of select="username"/></td>
          </tr>
          <tr>
              <td><b>Context Id</b></td>
              <td><xsl:value-of select="context/id"/></td>
          </tr>
          <tr>
              <td><b>Context Name</b></td>
              <td><xsl:value-of select="context/name"/></td>
          </tr>
          <tr>
              <td><b>Authetication ?</b></td>
              <td><xsl:value-of select="auth/@ado"/></td>
          </tr>
          <xsl:if test="auth/@ado='yes'">
              <tr>
                  <td><b>Auth Protocol</b></td>
                  <td><xsl:value-of select="auth/aproto"/></td>
              </tr>
              <tr>
                  <td><b>Auth Password</b></td>
                  <td><xsl:value-of select="auth/apassw"/></td>
              </tr>
              <tr>
                  <td><b>Privacy ?</b></td>
                  <td><xsl:value-of select="priv/@pdo"/></td>
              </tr>
              <xsl:if test="priv/@pdo='yes'">
                  <tr>
                      <td><b>Priv Password</b></td>
                      <td><xsl:value-of select="priv/ppassw"/></td>
                  </tr>
              </xsl:if>
          </xsl:if>
      </xsl:for-each>
      </table>
    </td>
    <td width="65%" valign="top">
      <table width="100%" border="1">
      <tr>
          <th>Request</th>
          <th>Oid</th>
          <th>Value</th>
          <th>Comment</th>
          <xsl:variable name="bulk_para">
              <xsl:value-of select="count(request/non_rep)"/>
          </xsl:variable>
          <xsl:if test="$bulk_para!=0">
              <th>Non Rep</th>
              <th>Max Rep</th>
          </xsl:if>
      </tr>
      <xsl:for-each select="request">
      <xsl:sort select="@type"/>
          <xsl:variable name="rowspan">
              <xsl:value-of select="count(oid)"/>
          </xsl:variable>
          <xsl:for-each select="oid">
              <xsl:choose>
                  <xsl:when test="position()=1">
                      <tr>
                          <td><xsl:attribute name="rowspan">
                                  <xsl:value-of select="$rowspan"/>
                              </xsl:attribute>
                              <xsl:value-of select="../@type"/>
                          </td>
                          <xsl:call-template name="oid"/>
                          <xsl:for-each select="../non_rep">
                              <td><xsl:attribute name="rowspan">
                                      <xsl:value-of select="$rowspan"/>
                                  </xsl:attribute>
                                  <xsl:value-of select="../non_rep"/>
                              </td>
                              <td><xsl:attribute name="rowspan">
                                      <xsl:value-of select="$rowspan"/>
                                  </xsl:attribute>
                                  <xsl:value-of select="../max_rep"/>
                              </td>
                          </xsl:for-each>
                      </tr>
                  </xsl:when>
                  <xsl:otherwise>
                      <tr> 
                          <xsl:call-template name="oid"/>
                      </tr>
                  </xsl:otherwise>
              </xsl:choose>
          </xsl:for-each>
      </xsl:for-each>
      </table>
    </td>
    </tr>
    </table>
</xsl:template>

<xsl:template name="oid">
    <td><xsl:value-of select="variable"/></td>
    <td><xsl:value-of select="value"/></td>
    <td><xsl:value-of select="comment"/></td>
</xsl:template>

</xsl:stylesheet>
