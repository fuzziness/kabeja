/*
   Copyright 2008 Simon Mieth

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
package org.kabeja.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Ant;


public class BlockTask extends Ant {
    protected final static String SEPARATOR = ",";
    protected List blocks = new ArrayList();
    protected String blockDirectory;
    protected String target = "dist";

    public void setBlockDir(String blockdir) {
        this.blockDirectory = blockdir;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void execute() throws BuildException {
        File f = new File(this.blockDirectory);

        if (!f.isAbsolute()) {
            f = new File(this.getProject().getBaseDir(), this.blockDirectory);
        }

        File[] dirs = f.listFiles();

        for (int i = 0; i < dirs.length; i++) {
            if (dirs[i].isDirectory()) {
                setupBlock(dirs[i]);
            }
        }

        // setup the properties
        getProject()
            .setUserProperty("kabeja.home",
            getProject().getBaseDir().getAbsolutePath());

        buildBlocks();

        // log(this.getProject().getBaseDir().getAbsolutePath());
    }

    public String getTaskName() {
        return super.getTaskName();
    }

    protected void setupBlock(File dir) throws BuildException {
        Hashtable props = getProject().getProperties();
        File buildFile = new File(dir, "build.xml");

        if (buildFile.exists()) {
            log("Setup block:" + dir.getName());

            Block b = new Block(dir.getName(), buildFile.getAbsolutePath());

            if (props.containsKey(("block." + dir.getName() + ".dependency"))) {
                String dep = (String) props.get("block." + dir.getName() +
                        ".dependency");
                handleDependency(dep, b);
            }

            if (props.containsKey("block." + dir.getName())) {
                b.setEnabled(Boolean.valueOf(
                        (((String) props.get("block." + dir.getName())).trim()))
                                    .booleanValue());
                addBlock(b);
            } else {
                log("Block:" + dir.getName() + " not configured.");
            }
        }
    }

    protected void handleDependency(String dependency, Block block) {
        if (dependency.length() > 0) {
            log("\tBlock " + block.getName() + " depence on:" + dependency);

            StringTokenizer st = new StringTokenizer(dependency, SEPARATOR);

            while (st.hasMoreTokens()) {
                String dep = st.nextToken();
                block.addDependency(dep);
            }
        }
    }

    protected void buildBlocks() {
        Iterator i = this.blocks.iterator();

        while (i.hasNext()) {
            Block b = (Block) i.next();

            if (b.isEnabled()) {
                File f = new File(b.getBuildFile());
                setAntfile(b.getBuildFile());
                setDir(f.getParentFile());
                super.setTarget(this.target);
                log("  Process block:" + b.getName() + " Target:" +
                    this.target);
                super.execute();
            } else {
                log("  Omit block:" + b.getName());
            }
        }
    }

    protected void addBlock(Block block) throws BuildException {
        if (!block.hasDependency()) {
            this.blocks.add(0, block);
        } else {
            for (int i = blocks.size() - 1; i >= 0; i++) {
                Block b = (Block) this.blocks.get(i);

                if (b.dependsOn(block.getName())) {
                    // check circle dependency
                    if (block.dependsOn(b.getName())) {
                        throw new BuildException(
                            "Cannot handle circle refernce of the blocks " +
                            b.getName() + "<-> " + block.getName());
                    }
                } else if (block.dependsOn(b.getName())) {
                    // insert after this block
                    this.blocks.add(i + 1, block);
                    // setup the enable state
                    block.setEnabled(b.isEnabled());

                    return;
                } else {
                    // add block here
                    this.blocks.add(i, block);

                    return;
                }
            }

            this.blocks.add(block);
        }
    }

    private class Block {
        String name;
        String buildFile;
        Set dependency = new HashSet();
        boolean build;

        public Block(String name, String buildfile) {
            this.name = name;
            this.buildFile = buildfile;
        }

        public void addDependency(String dep) {
            this.dependency.add(dep);
        }

        public boolean dependsOn(String name) {
            return this.dependency.contains(name);
        }

        public boolean hasDependency() {
            return this.dependency.size() > 0;
        }

        public String getName() {
            return name;
        }

        public String getBuildFile() {
            return buildFile;
        }

        public boolean isEnabled() {
            return this.build;
        }

        public void setEnabled(boolean b) {
            this.build = b;
        }
    }
}
