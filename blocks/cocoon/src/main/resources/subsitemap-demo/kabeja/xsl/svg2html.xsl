<?xml version="1.0" encoding="iso-8859-1"?>
<!-- Copyright 2010 Simon Mieth Licensed under the Apache License, Version 
	2.0 (the "License"); you may not use this file except in compliance with 
	the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
	Unless required by applicable law or agreed to in writing, software distributed 
	under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES 
	OR CONDITIONS OF ANY KIND, either express or implied. See the License for 
	the specific language governing permissions and limitations under the License. -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:svg="http://www.w3.org/2000/svg" version="1.0">

	<xsl:output method="html" />
	<xsl:param name="file" select="''" />
	<xsl:param name="highlight" select="'0'" />
	<xsl:include href="header.xsl" />

	<xsl:template match="/">
		<html>
			<body style="margin:0px;background-color:white;">
				<xsl:call-template name="header" />
				<div style="margin-left:2%;margin-right:10%;margin-top:50px">
					<xsl:apply-templates />
				</div>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="svg:svg">
		<a style="display:block;text-align:center" href="../../../sample.html">Return to samples</a>
		<br />
		<table>
			<tr>
				<td valign="top">
					<img src="../../hl-{$highlight}/{$file}.jpg" />
				</td>
				<td valign="top">
					<table>
						<tr>
							<th style="valign:top">Draft Layers</th>
						</tr>
						<xsl:for-each select="svg:g[@id='draft']/svg:g">
							<xsl:choose>
								<xsl:when test="position() = $highlight">
									<tr style="background-color:lightgray;">
										<td>
											<i>
												<xsl:value-of select="@id" />
											</i>
										</td>
										<td>
											<span style="color:black;"> selected </span>
										</td>
									</tr>
								</xsl:when>
								<xsl:otherwise>
									<tr>
										<td>
											<xsl:value-of select="@id" />
										</td>
										<td>
											<a href="../{position()}/{$file}.html">Highlight</a>
										</td>
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
