<!-- 
 *
 * NAME
 *      $RCSfile: table-transform-post.xsl,v $
 * DESCRIPTION
 *      [given below]
 * DELTA
 *      $Revision: 1.1 $
 *      $Author: birgit $
 * CREATED
 *      $Date: 2002/10/15 13:37:02 $
 * COPYRIGHT
 *      Westhawk Ltd
 * TO DO
 *
 *
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- Whenever you match any node or any attribute -->
<xsl:template match="node()|@*">
    <!-- Copy the current node -->
    <xsl:copy>
      <!-- Including any attributes it has and any child nodes -->
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
</xsl:template>

<xsl:template match="request">
    <ROWSET>
        <xsl:apply-templates select="@*|node()"/>
    </ROWSET>
</xsl:template>

<xsl:template match="parameters">
    <ROW>
        <xsl:apply-templates select="@*|node()"/>
    </ROW>
</xsl:template>

<xsl:template match="xsql-error">
    <xsql-error>
        <xsl:apply-templates select="@*|node()"/>
    </xsql-error>
</xsl:template>

<xsl:template match="xsql-status">
    <xsql-status>
        <xsl:apply-templates select="@*|node()"/>
    </xsql-status>
</xsl:template>

<xsl:template match="command"/>
<xsl:template match="cookies"/>
<xsl:template match="session"/>

</xsl:stylesheet>
