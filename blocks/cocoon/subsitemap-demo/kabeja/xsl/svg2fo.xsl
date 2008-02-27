<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:svg="http://www.w3.org/2000/svg">

	<xsl:template match="/">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

			<fo:layout-master-set>
				<fo:simple-page-master master-name="page"
					page-height="29.7cm" page-width="21cm" margin-top="2cm"
					margin-bottom="2cm" margin-left="3cm" margin-right="3cm">
					<fo:region-body margin-top="2cm"
						margin-bottom="3cm" />
					<fo:region-before extent="2.5cm" />
					<fo:region-after extent="2cm" />
				</fo:simple-page-master>


				<fo:page-sequence-master master-name="all">
					<fo:repeatable-page-master-alternatives>
						<fo:conditional-page-master-reference
							master-reference="page" page-position="first" />
					</fo:repeatable-page-master-alternatives>
				</fo:page-sequence-master>
			</fo:layout-master-set>

			<fo:page-sequence master-reference="all">

				<xsl:call-template name="header" />
				<xsl:call-template name="footer" />

				<fo:flow flow-name="xsl-region-body">
					<xsl:apply-templates />
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

	<xsl:template match="svg:svg">
		<fo:block font-size="18pt" space-before.optimum="18pt">
			DXF Draft
		</fo:block>
		<fo:block width="100%" border="0.1px solid gray" background-color="rgb(242,242,242)"
			padding="5pt">
			<fo:instream-foreign-object content-width="15cm"
				width="16cm"  scaling="uniform"
				display-align="center" content-height="scale-to-fit" >
				<svg:svg width="15cm" height="10cm">

					<!-- this is a workaround otherwise the viewbox will not applied -->
					<svg:svg viewBox="{@viewBox}">
						<xsl:apply-templates />
					</svg:svg>
				</svg:svg>
			</fo:instream-foreign-object>
		</fo:block>

		<fo:block font-size="18pt" space-before="1cm">
			Draft data
		</fo:block>
		<fo:block font-size="11pt">
			This section lists some data of the draft file.
		</fo:block>
		<fo:block font-size="18pt" space-before="5mm"
			space-after="5mm">
			List of layers
		</fo:block>
		<fo:block>
			<xsl:for-each select="svg:g[@id='draft']/svg:g">
				<fo:inline font-size="11pt" line-height="12pt">
					<xsl:value-of select="@id" />
					<xsl:if test="not(position() = last())">
						<xsl:text>, </xsl:text>
					</xsl:if>
				</fo:inline>

			</xsl:for-each>
		</fo:block>
		<fo:block page-break-before="true">
			<fo:block font-size="18pt" space-before="5mm"
				space-after="5mm">
				List of blocks
			</fo:block>
			<fo:block>
				<xsl:for-each select="svg:defs/svg:g">
					<fo:inline font-size="9pt" line-height="12pt">
						<xsl:value-of select="@id" />
						<xsl:if test="not(position() =last())">
							<xsl:text>, </xsl:text>
						</xsl:if>
					</fo:inline>

				</xsl:for-each>
			</fo:block>
		</fo:block>
	</xsl:template>



	<xsl:template name="header">
		<fo:static-content flow-name="xsl-region-before">
			<fo:block border-bottom="solid 0.1px #cccccc">
				<fo:block line-height="20pt" text-align-last="justify"
					text-align="start" padding-top="2pt">
					<fo:inline font-family="arial" font-size="16pt"
						font-weight="bold" color="#6596c6" text-align="start">
						Kabeja DXF2SVG

						<fo:leader leader-pattern="none"
							leader-pattern-width="8pt" leader-alignment="reference-area" />
					</fo:inline>

					<fo:inline color="black" text-align="right"
						font-size="8pt" font-style="italic">
						Example
					</fo:inline>
				</fo:block>
			</fo:block>
		</fo:static-content>
	</xsl:template>

	<xsl:template name="footer">
		<fo:static-content flow-name="xsl-region-after">
			<fo:block text-align="center" font-size="12pt"
				border-top="solid 0.1px #cccccc" padding-top="5px">
				Site
				<fo:page-number />
			</fo:block>
		</fo:static-content>
	</xsl:template>

	<xsl:template match="svg:*">
		<xsl:element name="svg:{local-name()}">
			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>


</xsl:stylesheet>
