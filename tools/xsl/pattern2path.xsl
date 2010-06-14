<?xml version="1.0" encoding="utf-8"?>
<!--
  Copyright 2010 simon
  
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
    http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns:svg="http://www.w3.org/2000/svg"

                version="1.0">

  <xsl:output method="xml"/>

  <xsl:template match="/">
  <xsl:variable name="width">
       <xsl:value-of select="/svg:svg/svg:defs/svg:pattern[@width &gt; /svg:svg/svg:defs/svg:pattern/@width]/@width"/>
  </xsl:variable>
  
  <xsl:variable name="height">
      <xsl:value-of select="sum(/svg:svg/svg:defs/svg:pattern/@height) + count(/svg:svg/svg:defs/svg:pattern)"/>
  </xsl:variable>
  <svg viewBox="0 0 {$width} {$height}" overflow="visible" stroke-width="0.025">
        <xsl:apply-templates/>
  </svg>
  </xsl:template>

  <xsl:template match="svg:pattern">
    <xsl:variable name="y">
        <xsl:value-of select="sum(preceding-sibling::svg:pattern/@height) + count(preceding-sibling::svg:pattern)"/>
    </xsl:variable>
  
      <g id="{@id}" >
      <xsl:attribute name="viewBox">
         <xsl:value-of select="@x"/><xsl:text> </xsl:text><xsl:value-of select="@y"/><xsl:text> </xsl:text><xsl:value-of select="@width"/><xsl:text> </xsl:text><xsl:value-of select="@height"/> 
      </xsl:attribute>
       <xsl:attribute name="transform">
         <xsl:text>translate(</xsl:text><xsl:value-of select="-1 * @x"/><xsl:text>  </xsl:text><xsl:value-of select="$y - @y"/><xsl:text>)</xsl:text>
      </xsl:attribute>
       <xsl:copy-of select="svg:path"/>
       <rect x="{@x}"  y="{@y}" width="{@width}" height="{@height}" stroke="blue" fill="blue" opacity="0.3" />
      </g>
  </xsl:template>
  <xsl:template match="@*|node()">
        <xsl:apply-templates select="child::*"/>
  </xsl:template>

  

</xsl:stylesheet>
