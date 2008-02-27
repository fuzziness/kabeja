/*
   Copyright 2005 Simon Mieth

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package org.kabeja.batik.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.batik.svggen.font.SVGFont;
import org.kabeja.svg.SVGUtils;


/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class FontImport {
    String source;
    String destination;
    String fontDescriptionFile;

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println(
                "The   source and/or destination directory and/or the fontdescription file are not set.");
            System.exit(0);
        }

        FontImport importer = new FontImport();
        importer.setSource(args[0]);
        importer.setDestination(args[1]);
        importer.setFontDescriptionFile(args[2]);
        importer.importFonts();
    }

    public void importFonts() {
        File in = new File(source);
        File dest = new File(destination);

        if (!dest.exists()) {
            dest.mkdirs();
        }

        File[] files = in.listFiles(new FileFilter() {
                    public boolean accept(File f) {
                        if (f.getName().toLowerCase().endsWith(".ttf")) {
                            return true;
                        }

                        return false;
                    }
                });

        if (files.length > 0) {
            try {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(fontDescriptionFile, true)));

                for (int i = 0; i < files.length; i++) {
                    String fontFile = files[i].getName().toLowerCase();
                    fontFile = fontFile.substring(0, fontFile.indexOf(".ttf"));

                    File svgFont = new File(dest.getAbsolutePath() +
                            File.separator + fontFile + ".svg");
                    importFont(files[i], svgFont, out);
                }

                out.flush();
                out.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * @return Returns the destination.
     */
    public String getDestination() {
        return destination;
    }

    /**
     * @param destination
     *            The destination to set.
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * @return Returns the source.
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source
     *            The source to set.
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return Returns the fontDescriptionFile.
     */
    public String getFontDescriptionFile() {
        return fontDescriptionFile;
    }

    /**
     * @param fontDescriptionFile
     *            The fontDescriptionFile to set.
     */
    public void setFontDescriptionFile(String fontDescriptionFile) {
        this.fontDescriptionFile = fontDescriptionFile;
    }

    private void importFont(File source, File dest, BufferedWriter out)
        throws IOException {
        String shx = source.getName().toLowerCase();
        shx = shx.substring(0, shx.indexOf(".ttf"));

        // remove unmeated chars from filename
        shx = fixFileName(shx);

        String[] args = new String[] {
                source.getAbsolutePath(), "-id", shx, "-o",
                dest.getAbsolutePath()
            };
        SVGFont.main(args);

        String uri = SVGUtils.fileToURI(dest);

        System.out.println("Imported:" + source.getName() + " to:" + uri);
        out.write(shx + " = " + uri + "\n");
    }

    private String fixFileName(String name) {
        StringBuffer buf = new StringBuffer();
        char[] c = name.toCharArray();

        for (int i = 0; i < c.length; i++) {
            if (c[i] != '_') {
                buf.append(c[i]);
            }
        }

        return buf.toString();
    }
}
