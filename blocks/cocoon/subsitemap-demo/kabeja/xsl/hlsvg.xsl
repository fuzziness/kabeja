<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  xmlns:svg="http://www.w3.org/2000/svg"
  version="1.0">

  <xsl:output method="xml"/>
  <xsl:param name="highlight" select="'0'"/>
  <xsl:param name="color" select="'red'"/>


  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="svg">
    <svg viewBox="{@viewBox}" width="800px" height="600px">
     
      <defs>
           <xsl:copy-of select="defs/*"/>
            <filter id="dropShadow">
            <feGaussianBlur in="SourceAlpha" stdDeviation="2" 
              result="blur"/>
            <feOffset in="blur" dx="6" dy="6"
              result="offsetBlur"/>
            <feMerge>
              <feMergeNode in="offsetBlur"/> 
              <feMergeNode in="blur"/>
              <feMergeNode in="SourceGraphic"/>
            </feMerge>
          </filter>
	  </defs>
      <xsl:apply-templates select="g"/>
    </svg>
  </xsl:template>

  <xsl:template match="g">
     <g>
      <xsl:copy-of select="@*"/>
      <xsl:for-each select="g">
        <xsl:choose>
          <xsl:when test="position() = $highlight">
            <g stroke="{$color}" id="{@id}" stroke-width=".3%">
              <xsl:copy-of select="*"/>
             </g>
          </xsl:when>
          <xsl:when test="$highlight &gt; 0">
            <g stroke="gray" >
	        <xsl:copy-of select="@*[not(name() = 'stroke')]"/>
		<xsl:copy-of select="child::*"/> 
	    </g>
          </xsl:when>
	  <xsl:otherwise>
	    <xsl:copy-of select="."/>
	  </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
     </g>
  
  </xsl:template>

</xsl:stylesheet>
