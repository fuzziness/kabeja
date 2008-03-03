/*
"THE SODA-WARE LICENSE" (Revision 1):

Lars Brandi Jensen <lbj@abj.dk> payed for this file made by Simon Mieth
<simon.mieth@gmx.de>. As long as you retain this notice you can do
whatever you want with this stuff. If we meet some day, and you think
this stuff is worth it, you can buy me a soda in return
Lars Brandi Jensen  or Simon Mieth.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED.  IN NO EVENT SHALL THE DXF2CALC PROJECT OR ITS
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.
*/
package dk.abj.svg.action;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.kabeja.processing.LayerFilter;
import org.kabeja.svg.SVGUtils;
import org.kabeja.svg.action.CanvasUpdateManager;
import org.kabeja.svg.action.CanvasUpdateRunnable;
import org.kabeja.svg.action.SVGDocumentAction;
import org.kabeja.ui.PropertiesEditor;
import org.kabeja.ui.PropertiesListener;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;

import de.miethxml.toolkit.ui.UIUtils;


public class LayerSelectionAction extends AbstractAction
    implements SVGDocumentAction, PropertiesEditor, CanvasUpdateRunnable {
    protected SVGDocument doc;
    protected Map properties = new HashMap();
    protected List listeners = new ArrayList();
    protected List layers = new ArrayList();
    protected Set disabledLayers = new HashSet();
    protected JCheckBox box;
    protected JDialog dialog;
    protected CanvasUpdateManager updateManager;

    public LayerSelectionAction() {
        super("Layers",
            new ImageIcon(UIUtils.resourceToBytes(LayerSelectionAction.class,
                    "/icons/layer_select.gif")));
        putValue(SHORT_DESCRIPTION,
            Messages.getString("editor.action.layer.selection"));
    }

    public void setDocument(SVGDocument doc) {
        Element el = doc.getElementById("draft");
        NodeList list = el.getElementsByTagName("g");

        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);

            if (node.hasAttributes() &&
                    (node.getAttributes().getNamedItem("id") != null)) {
                layers.add(node);
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        Thread t = new Thread(new Runnable() {
                    public void run() {
                        Frame[] frames = Frame.getFrames();

                        if (frames.length > 0) {
                            int i = 0;

                            while (!frames[i].isActive()) {
                                i++;
                            }

                            dialog = new JDialog(frames[i], true);
                        } else {
                            dialog = new JDialog();
                            dialog.setModal(true);
                        }

                        dialog.setTitle("Layer Tool");

                        JTable table = new JTable(new LayerTableModel());

                        JScrollPane sp = new JScrollPane(table);
                        dialog.getContentPane().add(sp, BorderLayout.CENTER);

                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
                        panel.setBorder(BorderFactory.createEmptyBorder(3, 10,
                                3, 5));
                        box = new JCheckBox();

                        box.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    properties.put(LayerFilter.PROPERTY_MERGE_LAYERS,
                                        Boolean.toString(box.isSelected()));
                                    firePropertiesUpdated();
                                }
                            });

                        if (properties.containsKey(
                                    LayerFilter.PROPERTY_MERGE_LAYERS)) {
                            box.setSelected(Boolean.valueOf(
                                    (String) properties.get(
                                        LayerFilter.PROPERTY_MERGE_LAYERS))
                                                   .booleanValue());
                        }

                        panel.add(box);
                        panel.add(new JLabel(Messages.getString(
                                    "editor.action.layer.merge")));
                        panel.add(Box.createHorizontalGlue());

                        JButton button = new JButton(Messages.getString(
                                    "button.close"));
                        button.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    dialog.setVisible(false);
                                    dialog.dispose();
                                }
                            });

                        panel.add(button);
                        dialog.getContentPane().add(panel, BorderLayout.SOUTH);
                        dialog.pack();

                        dialog.setVisible(true);
                    }
                });
        t.start();
    }

    public void addPropertiesListener(PropertiesListener listener) {
        this.listeners.add(listener);
    }

    public Map getProperties() {
        return this.properties;
    }

    public void removePropertiesListener(PropertiesListener listener) {
        this.listeners.remove(listener);
    }

    public void setProperties(Map props) {
        Iterator i = props.keySet().iterator();

        while (i.hasNext()) {
            String key = (String) i.next();

            this.properties.put(key, props.get(key));
        }
    }

    protected void firePropertiesUpdated() {
        Iterator i = this.listeners.iterator();

        while (i.hasNext()) {
            PropertiesListener listener = (PropertiesListener) i.next();
            listener.propertiesChanged(this.properties);
        }
    }

    public void setCanvasUpdateManager(CanvasUpdateManager manager) {
        this.updateManager = manager;
    }

    protected String getDisabledLayersStringList() {
        StringBuffer buf = new StringBuffer();
        Iterator i = disabledLayers.iterator();

        while (i.hasNext()) {
            String layerName = SVGUtils.reverseID(((Node) i.next()).getAttributes()
                                                   .getNamedItem("id")
                                                   .getNodeValue());
            buf.append(layerName);

            if (i.hasNext()) {
                buf.append("|");
            }
        }

        return buf.toString();
    }

    private class LayerTableModel extends AbstractTableModel {
        public String getColumnName(int column) {
            switch (column) {
            case 0:
                return Messages.getString("label.enabled");

            case 1:
                return "Layer";

            default:
                return "" + column;
            }
        }

        public int getColumnCount() {
            return 2;
        }

        public int getRowCount() {
            return layers.size();
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Node node = (Node) layers.get(rowIndex);

            Element el = (Element) node;

            switch (columnIndex) {
            case 0:

                if (el.hasAttributeNS(null, "visibility") &&
                        (el.getAttributeNS(null, "visibility") != null)) {
                    boolean b = Boolean.valueOf(el.getAttributeNS(null,
                                "visibility").equals("visible")).booleanValue();

                    return Boolean.valueOf(b);
                } else {
                    return Boolean.TRUE;
                }

            case 1:
                return SVGUtils.reverseID(node.getAttributes().getNamedItem("id")
                                              .getNodeValue());
            }

            return null;
        }

        public Class getColumnClass(int columnIndex) {
            switch (columnIndex) {
            case 0:
                return Boolean.class;

            default:
                return String.class;
            }
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex < 2) {
                return true;
            }

            return false;
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            switch (columnIndex) {
            case 0:

                Boolean b = (Boolean) aValue;

                try {
                    updateManager.invokeLater(new NodeUpdateRunnable(rowIndex,
                            b.booleanValue(), this));

                    if (b.booleanValue()) {
                        disabledLayers.remove(layers.get(rowIndex));
                    } else {
                        disabledLayers.add(layers.get(rowIndex));
                    }

                    // change the properties and fire change event
                    properties.put(LayerFilter.PROPERTY_REMOVE_LAYERS,
                        getDisabledLayersStringList());
                    firePropertiesUpdated();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                break;

            default:
                break;
            }
        }
    }

    /**
     * This is a simple helper class, witch will update a node in the update
     * thread of the of the UpdateManager
     *
     * @author simon
     *
     */
    private class NodeUpdateRunnable implements Runnable {
        private boolean visible = false;
        private int nodeIndex;
        private LayerTableModel model;

        public NodeUpdateRunnable(int nodeIndex, boolean visible,
            LayerTableModel model) {
            this.nodeIndex = nodeIndex;
            this.visible = visible;
            this.model = model;
        }

        public void run() {
            Node node = (Node) layers.get(nodeIndex);

            if (visible) {
                ((Element) node).setAttributeNS(null, "visibility", "visible");
            } else {
                ((Element) node).setAttributeNS(null, "visibility", "hidden");
            }

            model.fireTableCellUpdated(this.nodeIndex, 0);
        }
    }
}
