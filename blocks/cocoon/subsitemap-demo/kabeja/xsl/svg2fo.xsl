<?xml version="1.0"?>


<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format">

  <xsl:template match="/">
   <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
   
    <fo:layout-master-set>
      <fo:simple-page-master master-name="page" page-height="29.7cm" page-width="21cm" margin-top="2cm" margin-bottom="2cm" margin-left="3cm" margin-right="3cm">
          <fo:region-body margin-top="2cm" margin-bottom="3cm"/>
          <fo:region-before extent="2.5cm"/>
          <fo:region-after extent="2cm"/>
      </fo:simple-page-master>
   
 
     <fo:page-sequence-master master-name="all">
       <fo:repeatable-page-master-alternatives>
	 <fo:conditional-page-master-reference master-reference="page" page-position="first"/>
       </fo:repeatable-page-master-alternatives>
     </fo:page-sequence-master>
    </fo:layout-master-set>

    <fo:page-sequence master-reference="all">

      <xsl:call-template name="header"/>
      <xsl:call-template name="footer"/>

      <fo:flow flow-name="xsl-region-body">
        <xsl:apply-templates/>
      </fo:flow>
    </fo:page-sequence>
   </fo:root>
  </xsl:template>

  <xsl:template match="svg">
    <fo:block font-size="24pt" space-before.optimum="24pt">Draft</fo:block>
    <fo:block width="100%" border="thin dashed black" padding="5pt">
      <fo:instream-foreign-object content-width="15cm" content-height="10cm" width="16cm" height="11cm" scaling="uniform" display-align="center">
        <svg xmlns="http://www.w3.org/2000/svg" width="15cm" height="10cm">
          
          <!-- this is a workaround otherwise the viewbox will not applied -->
          <svg viewBox="{@viewBox}">
              <xsl:copy-of select="child::*"/>
            </svg>
        </svg>
      </fo:instream-foreign-object>
    </fo:block>

    <fo:block font-size="24pt" space-before="1cm">Data</fo:block>
    <fo:block font-size="16pt" space-before="5mm" space-after="5mm">Layer</fo:block>
    <xsl:for-each select="g">
      <fo:block font-size="9pt" line-height="12pt">
        <xsl:value-of select="@id"/>
      </fo:block>

    </xsl:for-each>  
    <fo:block page-break-before="true">
    <fo:block font-size="16pt" space-before="5mm" space-after="5mm">Blocks</fo:block>
    <xsl:for-each select="defs/g">
      <fo:block font-size="9pt" line-height="12pt">
        <xsl:value-of select="@id"/>
      </fo:block>

    </xsl:for-each>  
  </fo:block>
   </xsl:template>



     <xsl:template name="header">
         <fo:static-content flow-name="xsl-region-before">
           <fo:block border-bottom="solid 1pt #cccccc">
             <fo:block line-height="20pt" text-align-last="justify" text-align="start" padding-top="2pt">
               <fo:inline
                 font-family="arial"
                 font-size="16pt"
                 font-weight="bold"
                 color="#6596c6"
                 text-align="start">
                 Kabeja DXF2SVG
             
                         <fo:leader leader-pattern="none" 
                           leader-pattern-width="8pt"
                           leader-alignment="reference-area"
                           />
  </fo:inline>

               <fo:inline color="black" text-align="right" font-size="8pt" font-style="italic">
                 Example
               </fo:inline>
             </fo:block>
           </fo:block>
         </fo:static-content>
       </xsl:template>

       <xsl:template name="footer">
         <fo:static-content flow-name="xsl-region-after">
           <fo:block text-align="center" font-size="12pt" border-top="solid 1pt #cccccc" padding-top="5px">
             Seite <fo:page-number/>
         </fo:block>
       </fo:static-content>
     </xsl:template>




</xsl:stylesheet>
