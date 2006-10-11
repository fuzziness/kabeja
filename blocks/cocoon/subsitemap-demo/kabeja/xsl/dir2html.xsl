<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dir="http://apache.org/cocoon/directory/2.0"
  version="1.0">

 <xsl:output method="html"/>
 <xsl:param name="kabeja-samples" select="''"/>
 <xsl:include href="header.xsl"/>
  <xsl:template match="/">
    <html>
       <body style="margin:0px;background-color:#ffffff;">
        <xsl:call-template name="header"/>
	 <div style="margin-left:20%;margin-right:10%;margin-top:50px">
	    <h2 style="border-bottom:thin solid #cccccc">Explore DXF-Files</h2>
          <table style="border:thin solid #aaaaaa">
	  <tr>
	     <th>File</th>
	     <th></th>
	     <th></th>
	     <th></th> 
	     <th></th>
	     <th></th>
	  </tr>
            <xsl:apply-templates/>
          </table>
	  
	 <h2 style="border-bottom:thin solid #cccccc">Adding your files</h2>
	  <p> You can simple copy your files to: <b><xsl:value-of select="$kabeja-samples"/></b>.</p>	  
        </div>
	
	
	
      </body>
    </html>
  </xsl:template>

  <xsl:template match="dir:file[contains(@name,'.dxf')]">
    <xsl:variable name="file" select="substring-before(@name,'.dxf')"/>
    <tr >
      <td style="padding:4px"><xsl:value-of select="@name"/></td>
      <td style="padding:4px"><a href="demo/{$file}.jpg">Image</a></td>
      <td style="padding:4px"><a href="demo/{$file}.xml">XML</a></td>
      <td style="padding:4px"><a href="demo/{$file}.svg">SVG</a></td>
      <td style="padding:4px"><a href="demo/{$file}.pdf">PDF</a></td>
      <td style="padding:4px"><a href="demo/info/0/{$file}.html">Details</a></td>
    </tr>
  </xsl:template>

</xsl:stylesheet>
