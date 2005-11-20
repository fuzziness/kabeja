<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns:svg="http://www.w3.org/2000/svg"

                version="1.0">

  <xsl:output method="xml"/>

  <xsl:template match="/">
        <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="svg:font-face">
    <xsl:variable name="file">
      <xsl:value-of select="svg:font-face-src/svg:font-face-uri/@xlink:href"/>
    </xsl:variable>
    <xsl:variable name="uri">
     <xsl:value-of select="substring-before($file,'#')"/>   
   </xsl:variable>
   <xsl:variable name="fontID">
      <xsl:value-of select="substring-after($file,'#')"/>   
   </xsl:variable>
     <xsl:apply-templates select="document($uri)/svg/defs/font[@id = $fontID]"/>
  </xsl:template>

 <!-- fix the font-family name and namespace -->

 <xsl:template match="font">
   <font xmlns="http://www.w3.org/2000/svg">
    <xsl:copy-of select="@*"/>
     <font-face font-family="{@id}" xmlns="http://www.w3.org/2000/svg">
         <xsl:copy-of select="font-face/@*[name() != 'font-family']"/>
     </font-face> 
    <xsl:apply-templates select="*[name() != 'font-face']"/>
   </font>
 </xsl:template>  

 <!-- we need to fix the namespace from the embeding font -->
 <xsl:template match="*[namespace-uri() = '']">
      <xsl:element name="{name()}" namespace="http://www.w3.org/2000/svg">
             <xsl:copy-of select="@*[name() != 'xmlns']"/>
	     <xsl:apply-templates/>
      </xsl:element>
 </xsl:template>
 


  <!-- copy all svg:elements to the result tree -->
  <xsl:template match="@*|node()">
      <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
      </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
