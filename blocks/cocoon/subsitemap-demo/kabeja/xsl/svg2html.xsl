<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:svg="http://www.w3.org/2000/svg"
  version="1.0">

  <xsl:output method="html"/>
  <xsl:param name="file" select="''"/>
  <xsl:param name="highlight" select="'0'"/>
  <xsl:include href="header.xsl"/>
 
  <xsl:template match="/">
    <html>
       <body style="margin:0px;background-color:white;">
        <xsl:call-template name="header"/>
         <div style="margin-left:2%;margin-right:10%;margin-top:50px">
          <xsl:apply-templates/>
        </div>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="svg:svg">
    <a style="display:block;text-align:center" href="../../../sample.html">Return to samples</a>
    <br/>
    <table>
      <tr>
        <td valign="top"><img src="../../hl-{$highlight}/{$file}.jpg"/></td>
        <td valign="top">
          <table>
            <tr>
              <th  style="valign:top">Draft Layers</th>
            </tr>
            <xsl:for-each select="svg:g[@id='draft']/svg:g">
              <xsl:choose>
                <xsl:when test="position() = $highlight">
                  <tr style="background-color:lightgray;">
                    <td><i><xsl:value-of select="@id"/></i></td>
                    <td><span style="color:black;"> selected </span></td>
                  </tr>
                </xsl:when>
                <xsl:otherwise>
                  <tr>
                    <td><xsl:value-of select="@id"/></td>
                    <td><a href="../{position()}/{$file}.html">Highlight</a></td>
                  </tr>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:for-each>

          </table>
        </td>
      </tr>
    </table>
  </xsl:template>


</xsl:stylesheet>
