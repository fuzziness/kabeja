/*******************************************************************************
 * Copyright 2010 Simon Mieth
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.kabeja.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Ant;

public class BlockTask extends Ant {
    protected final static String SEPARATOR = ",";

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

        Map blocks = new HashMap();
        for (int i = 0; i < dirs.length; i++) {
            if (dirs[i].isDirectory()) {
                File buildFile = new File(dirs[i], "build.xml");

                if (buildFile.exists()) {
                    log("Setup block:" + dirs[i].getName());

                    Block b = setupBlock(dirs[i], buildFile);
                    blocks.put(b.getName(), b);
                } else {
                    log("Omit module dir:" + dirs[i].getName()
                            + " not build.xml found");
                }

            }
        }

        // setup the properties
        getProject().setUserProperty("kabeja.home",
                getProject().getBaseDir().getAbsolutePath());

        List buildOrder = resolveDependency(blocks);
        log("Build " + buildOrder.size() + " blocks in the following order");
        Iterator i = buildOrder.iterator();
        while (i.hasNext()) {
            Block b = (Block) i.next();
            log("Block:" + b);
        }

        buildBlocks(buildOrder);

        // log(this.getProject().getBaseDir().getAbsolutePath());
    }

    public String getTaskName() {
        return super.getTaskName();
    }

    protected Block setupBlock(File dir, File buildFile) throws BuildException {

        Hashtable props = getProject().getProperties();

        Block b = new Block(dir.getName(), buildFile.getAbsolutePath());

        if (props.containsKey(("block." + dir.getName() + ".dependency"))) {
            String dep = (String) props.get("block." + dir.getName()
                    + ".dependency");
            handleDependency(dep, b);
        }

        if (props.containsKey("block." + dir.getName())) {
            b.setEnabled(Boolean.valueOf(
                    (((String) props.get("block." + dir.getName())).trim()))
                    .booleanValue());

        } else {
            log("Block:" + dir.getName() + " not configured.");
        }

        return b;

    }

    protected void handleDependency(String dependency, Block block) {
        if (dependency.length() > 0) {
            StringTokenizer st = new StringTokenizer(dependency, SEPARATOR);

            while (st.hasMoreTokens()) {
                String dep = st.nextToken();
                block.addDependency(dep.trim());
            }
        }
    }

    protected List resolveDependency(Map blocks) {
        List buildOrder = new ArrayList();
        Set inserted = new HashSet();
        Iterator i = blocks.values().iterator();
        while (i.hasNext()) {
            Block b = (Block) i.next();
            if (b.isEnabled()) {
                Iterator di = resolveDependency(b, blocks).iterator();
                while (di.hasNext()) {
                    String block = (String) di.next();
                    if (inserted.contains(block)) {
                        log("omit Block:" + block + " inserted berfore");
                    } else {
                        buildOrder.add(blocks.get(block));
                        inserted.add(block);
                        log("Add block:" + block + " for dependency of "
                                + b.getName());
                    }
                }
                if (!inserted.contains(b.getName())) {
                    buildOrder.add(b);
                    inserted.add(b.getName());
                }
            }

        }
        return buildOrder;
    }

    protected List resolveDependency(Block b, Map blocks) {
        List list = new ArrayList();
        log("Resolve dependency for:" + b.getName());
        Iterator i = b.getDependencies().iterator();
        while (i.hasNext()) {
            String dependency = ((String) i.next()).trim();

            Block dep = (Block) blocks.get(dependency.trim());
            List blocksBefore = resolveDependency(dep, blocks);
            Iterator di = blocksBefore.iterator();
            while (di.hasNext()) {
                String name = (String) di.next();
                if (name.equals(b.getName())) {
                    throw new BuildException("Unresolved Dependency Block:"
                            + b.getName() + " depends on:" + dependency
                            + " which depends on:" + b.getName()
                            + ", fix the dependency");
                } else {
                    list.add(name);
                }

            }
            list.add(dependency);
        }

        return list;
    }

    protected void buildBlocks(List blocks) {

        Iterator i = blocks.iterator();

        while (i.hasNext()) {
            Block b = (Block) i.next();

            if (b.isEnabled()) {
                File f = new File(b.getBuildFile());
                setAntfile(b.getBuildFile());
                setDir(f.getParentFile());
                super.setTarget(this.target);
                log("Process block:" + b.getName() + " Target:" + this.target);
                super.execute();
            } else {
                log("Omit block:" + b.getName());
            }
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

        public Set getDependencies() {
            return this.dependency;
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append(this.name);
            if (this.dependency.size() > 0) {
                buf.append("[depends on:");
                Iterator i = dependency.iterator();
                while (i.hasNext()) {
                    buf.append(i.next());
                    if (i.hasNext()) {
                        buf.append(",");
                    }
                }
                buf.append("]");
            }
            return buf.toString();
        }
    }
}
