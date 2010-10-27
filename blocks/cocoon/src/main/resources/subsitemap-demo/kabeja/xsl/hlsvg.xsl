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
	<xsl:param name="highlight" select="'0'" />
	<xsl:param name="color" select="'red'" />


	<xsl:template match="/">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="svg:svg">
		<svg:svg viewBox="{@viewBox}" width="800px" height="600px">

			<xsl:apply-templates select="svg:defs" />
			<xsl:apply-templates select="svg:g[@id='draft']" />

		</svg:svg>
	</xsl:template>

	<xsl:template match="svg:defs">
		<svg:defs>
			<svg:filter id="dropShadow">
				<svg:feGaussianBlur in="SourceAlpha"
					stdDeviation="2" result="blur" />
				<svg:feOffset in="blur" dx="6" dy="6" result="offsetBlur" />
				<svg:feMerge>
					<feMergeNode in="offsetBlur" />
					<feMergeNode in="blur" />
					<feMergeNode in="SourceGraphic" />
				</svg:feMerge>
			</svg:filter>
			<xsl:apply-templates />
		</svg:defs>
	</xsl:template>

	<xsl:template match="svg:g[@id='draft']">
		<svg:g>
			<xsl:copy-of select="@*" />
			<xsl:for-each select="svg:g">

				<xsl:choose>
					<xsl:when test="position() = $highlight">
						<svg:g stroke="{$color}">
							<xsl:copy-of select="@*[not(name() = 'stroke')]" />
							<xsl:apply-templates select="svg:*" />
						</svg:g>
					</xsl:when>
					<xsl:when test="$highlight &gt; 0">
						<svg:g stroke="gray">
							<xsl:copy-of select="@*[not(name() = 'stroke')]" />
							<xsl:apply-templates />
						</svg:g>
					</xsl:when>
					<xsl:otherwise>

						<xsl:apply-templates select="." />

					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</svg:g>

	</xsl:template>

	<xsl:template match="svg:*">
		<xsl:element name="svg:{local-name()}">
			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
