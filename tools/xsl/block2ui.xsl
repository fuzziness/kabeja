<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns:svg="http://www.w3.org/2000/svg"
                xmlns:kabeja="http://kabeja.org/processing/ui/1.0" 
                version="1.0">

  <xsl:output method="xml" indent="yes"/>
  <xsl:param name="fragment" /> 
  

  <xsl:template match="/">
        <xsl:apply-templates/>     
  </xsl:template>
  

  <xsl:template match="kabeja:components">  

      <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
        <xsl:apply-templates select="document($fragment)/kabeja:uiconfiguration/kabeja:components/kabeja:component"/>
      </xsl:copy>  
  </xsl:template>
  
   <xsl:template match="kabeja:filters">
  
      <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
        <xsl:apply-templates select="document($fragment)/kabeja:processing/kabeja:configuration/kabeja:filters/kabeja:filter"/>
      </xsl:copy>  
  </xsl:template>
  
  <xsl:template match="kabeja:serializers">
   
      <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
        <xsl:apply-templates select="document($fragment)/kabeja:processing/kabeja:configuration/kabeja:serializers/kabeja:serializer"/>
      </xsl:copy>  
  </xsl:template>

  <xsl:template match="kabeja:generators">
   
      <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
        <xsl:apply-templates select="document($fragment)/kabeja:processing/kabeja:configuration/kabeja:generators/kabeja:generator"/>
      </xsl:copy>  
  </xsl:template>
  
    <xsl:template match="kabeja:pipelines">
  
      <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
        <xsl:apply-templates select="document($fragment)/kabeja:processing/kabeja:pipelines/kabeja:pipeline"/>
      </xsl:copy>  
  </xsl:template>
   <!-- copy elements to the result tree -->
  <xsl:template match="@*|node()">
      <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
      </xsl:copy>
  </xsl:template>


</xsl:stylesheet>