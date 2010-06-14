<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2010 Simon Mieth Licensed under the Apache License, Version 
	2.0 (the "License"); you may not use this file except in compliance with 
	the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
	Unless required by applicable law or agreed to in writing, software distributed 
	under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES 
	OR CONDITIONS OF ANY KIND, either express or implied. See the License for 
	the specific language governing permissions and limitations under the License. -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:svg="http://www.w3.org/2000/svg" version="1.0">

	<xsl:output method="xml" />
	<xsl:param name="row" select="'2'" />
	<xsl:param name="column" select="'1'" />
	<xsl:param name="size" select="'6'" />

	<xsl:template match="/">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="svg">
		<svg:svg>

			<!-- generate variables -->

			<xsl:variable name="x">
				<xsl:call-template name="extractValue">
					<xsl:with-param name="value" select="'0'" />
					<xsl:with-param name="string" select="@viewBox" />
				</xsl:call-template>
			</xsl:variable>

			<xsl:variable name="y">
				<xsl:call-template name="extractValue">
					<xsl:with-param name="value" select="'1'" />
					<xsl:with-param name="string" select="@viewBox" />
				</xsl:call-template>
			</xsl:variable>

			<xsl:variable name="width">
				<xsl:call-template name="extractValue">
					<xsl:with-param name="value" select="'2'" />
					<xsl:with-param name="string" select="@viewBox" />
				</xsl:call-template>
			</xsl:variable>

			<xsl:variable name="height">
				<xsl:call-template name="extractValue">
					<xsl:with-param name="value" select="'3'" />
					<xsl:with-param name="string" select="@viewBox" />
				</xsl:call-template>
			</xsl:variable>

			<xsl:variable name="diff_h">
				<xsl:value-of select="$width div $size" />
			</xsl:variable>

			<xsl:variable name="diff_v">
				<xsl:value-of select="$height div $size" />
			</xsl:variable>


			<xsl:attribute name="viewBox">
        <xsl:value-of select="$x + $diff_h*($column - 1)" />
        <xsl:text> </xsl:text> 
        <xsl:value-of select="$y + $diff_v*($row - 1)" />
        <xsl:text> </xsl:text>
        <xsl:value-of select="$diff_h * $column" />
        <xsl:text> </xsl:text>
        <xsl:value-of select="$diff_v * $row" />
      </xsl:attribute>

			<xsl:copy-of select="child::*" />
		</svg:svg>


	</xsl:template>






	<xsl:template name="extractValue">
		<xsl:param name="value" select="'0'" />
		<xsl:param name="current" select="'0'" />
		<xsl:param name="string" select="''" />
		<xsl:choose>
			<xsl:when test="$value > $current">
				<xsl:call-template name="extractValue">
					<xsl:with-param name="value" select="$value" />
					<xsl:with-param name="current" select="$current + 1 " />
					<xsl:with-param name="string" select="substring-after($string,' ')" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>

					<xsl:when test="contains($string,' ')">
						<xsl:value-of select="substring-before($string,' ')" />
					</xsl:when>

					<xsl:otherwise>
						<xsl:value-of select="$string" />
					</xsl:otherwise>
				</xsl:choose>

			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

</xsl:stylesheet>
