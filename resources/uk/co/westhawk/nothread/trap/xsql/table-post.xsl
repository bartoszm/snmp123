<!-- 
 *
 * NAME
 *      $RCSfile: table-post.xsl,v $
 * DESCRIPTION
 *      [given below]
 * DELTA
 *      $Revision: 1.2 $
 *      $Author: birgit $
 * CREATED
 *      $Date: 2005/03/02 18:07:25 $
 * COPYRIGHT
 *      Westhawk Ltd
 * TO DO
 *
 *
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" >

  <xsl:output
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
    encoding="UTF-8" indent="yes" method="xml"
    media-type="text/html"
    omit-xml-declaration="yes"/>

<xsl:variable name="error_msg" select="//xsql-error/message"/>
<xsl:variable name="status" select="//xsql-status"/>

<xsl:template match="/">
<html>
  <head>
  <title>Result of last insert in Table Trap</title>
  </head>
  <body>
  <h1>Result of last insert in Table Trap</h1>

  <xsl:call-template name="print_error_message"/>

  <xsl:call-template name="print_status">
      <xsl:with-param name="verb" select="'Insert'"/>
  </xsl:call-template>

  <table border="1" width="90%">
      <xsl:for-each select="/SUPER/TRAPS/TRAP/*">
          <xsl:call-template name="print_row"/>
      </xsl:for-each>
  </table>

  </body>
</html>
</xsl:template> 



<xsl:template name="print_status"> 
  <xsl:param name="verb"/>

  <xsl:if test="$status">
      <p style="color: red; white-space: pre;">
          <xsl:for-each select="$status">
              <xsl:value-of select="@action"/>
              <xsl:text>: </xsl:text>
              <xsl:value-of select="$verb"/>
              <xsl:text> </xsl:text>
              <xsl:value-of select="@rows"/>
              <xsl:choose>
                  <xsl:when test="@rows = 1">
                      <xsl:text> row.</xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                      <xsl:text> rows.</xsl:text>
                  </xsl:otherwise>
              </xsl:choose>
          </xsl:for-each>
      </p>
  </xsl:if>
</xsl:template>


<xsl:template name="print_error_message"> 
  <xsl:if test="$error_msg">
      <p style="color: red; white-space: pre;">
          <xsl:for-each select="$error_msg">
              <xsl:variable name="message" select="."/>
              <xsl:choose>
                  <xsl:when test="contains($message, 'href')"/>
                  <xsl:otherwise>
                      <xsl:value-of select="$message"/><br/>
                </xsl:otherwise>
              </xsl:choose>
          </xsl:for-each>
      </p>
  </xsl:if>
</xsl:template>


<xsl:template name="print_row">
    <tr>
        <td>
          <xsl:value-of select="local-name()"/>
        </td>
        <td>
          <xsl:value-of select="node()"/>
        </td>
    </tr>
</xsl:template>

</xsl:stylesheet>

