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

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.svg.SVGUtils;
import org.kabeja.svg.action.DXFDocumentAction;
import org.kabeja.svg.action.SVGDocumentAction;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGDocument;

import de.miethxml.toolkit.ui.UIUtils;


public class HighlightAction extends AbstractAction implements SVGDocumentAction,
    EventListener, ItemListener, DXFDocumentAction {
    public final static String HIGHLIGHT_STYLE = "stroke:red;fill:blue;fill-opacity:0.25;stroke-width:0.15%";
    public final static String DEFAULT_STYLE = "stroke:black;stroke-width:0.15%";
    private Element elem;
    private Element oldElem;
    private Element labelID;
    private Node text;
    private String oldStyle;
    private DXFDocument dxfDocument;
    protected double STROKE_WIDTH_PRECENTS = 0.002;
    protected double strokeWidth = 0.5;
    protected double fontSize = 12;
    protected double highlightStrokeWidth = 1;
    protected boolean activated = false;

    public HighlightAction() {
        super("Highlight",
            new ImageIcon(UIUtils.resourceToBytes(HighlightAction.class,
                    ("/icons/highlight.png")),
                Messages.getString("editor.action.highlight")));
    }

    public HighlightAction(String b) {
        super(b);
    }

    public void handleEvent(Event evt) {
        if (this.activated) {
            oldElem = elem;
            elem = (Element) evt.getTarget();

            if (evt.getType().equals("mouseover")) {
                if (evt.getEventPhase() == Event.AT_TARGET) {
                    // unhighlight the last element before
                    if (oldElem != null) {
                        unHighlight(oldElem);
                    }

                    highlight(elem);
                    showHandle(evt, elem);
                }
            } else {
                if (evt.getEventPhase() == Event.AT_TARGET) {
                    unHighlight(elem);
                }
            }
        }

        // evt.stopPropagation();
    }

    protected void highlight(Element el) {
        this.oldStyle = el.getAttribute("style");
        el.setAttribute("style", HIGHLIGHT_STYLE);

        //this.labelID.removeChild(oldChild)
    }

    protected void unHighlight(Element el) {
        el.setAttribute("style", this.oldStyle);
        this.labelID.setAttributeNS(null, "visibility", "hidden");
    }

    public void setDocument(SVGDocument doc) {
        Element el = doc.getElementById("draft");
        prepare(el.getChildNodes());

        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;

        // we parse the viewBox
        String s = doc.getDocumentElement().getAttribute("viewBox");

        String[] data = s.split("\\s+");

        // we create a invisible rectangle for the capturing the mouse events
        double y = Double.parseDouble(data[1]);
        double width = Double.parseDouble(data[2]);
        fontSize = width / 5;
        this.labelID = doc.createElementNS(svgNS, "text");
        this.labelID.setAttributeNS(null, "x", data[0]);
        this.labelID.setAttributeNS(null, "y", "" + (y + fontSize));

        this.labelID.setAttributeNS(null, "style", "stroke-width:12pt");
        this.labelID.setAttributeNS(null, "visibility", "hidden");
        this.text = doc.createTextNode("Handle");
        this.labelID.appendChild(text);
        doc.getDocumentElement()
           .insertBefore(this.labelID, doc.getDocumentElement().getFirstChild());
    }

    public void prepare(NodeList nodes) {
        // Gets an element from the loaded document.
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            // if (node instanceof Element) {
            // ((Element) node).setAttribute("style",
            // DEFAULT_STYLE);
            // }
            if (node instanceof EventTarget && !node.getNodeName().equals("g")) {
                EventTarget t = (EventTarget) node;
                t.addEventListener("mouseover", this, false);
                t.addEventListener("mouseout", this, false);
            }

            if (node.hasChildNodes()) {
                prepare(node.getChildNodes());
            }
        }
    }

    protected void initStrokeWidth() {
        // this.strokeWidth = (STROKE_WIDTH_PRECENTS * (this.canvas.getWidth() +
        // this.canvas.getHeight())) / 2;
        // this.highlightStrokeWidth = this.strokeWidth;
    }

    public void actionPerformed(ActionEvent e) {
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            this.activated = true;
        } else {
            this.activated = false;
        }
    }

    protected void showHandle(Event e, Element el) {
        //		  MouseEvent mevt = (MouseEvent) e;
        //		  SVGDocument doc = ((SVGDocument)this.labelID
        //			.getOwnerDocument());
        //		    SVGMatrix screen = doc.getRootElement().getScreenCTM();
        //			SVGMatrix matrix =  doc.getRootElement().getScreenCTM()
        //					.inverse();
        //			 double startX = matrix.getA() * mevt.getClientX()
        //					+ matrix.getC() * mevt.getClientY() + matrix.getE();
        //			double startY = matrix.getB() * mevt.getClientX()
        //					+ matrix.getD() * mevt.getClientY() + matrix.getF();
        //			this.labelID.setAttributeNS(null, "x", ""+startX);
        //			this.labelID.setAttributeNS(null, "y", ""+startY);
        //			String s = doc.getDocumentElement().getAttribute("viewBox");
        //            System.out.println("s="+s);
        //			String[] data = s.split("\\s+");
        //			double w = Double.parseDouble(data[2]);
        //	
        //			float width = (float) (screen.getA() * w + screen.getC() * 0
        //					+ screen.getE());
        //			 
        String handle = SVGUtils.reverseID(el.getAttribute("id"));
        DXFEntity entity = this.dxfDocument.getDXFEntityByID(handle);
        System.out.println("Selected Entity=" + handle);

        if (entity != null) {
            System.out.println("Selected Entity-type=" + entity.getType());
        } else {
            System.out.println("No entity found for handle=" + handle);
        }

        //
        //			this.labelID.setAttributeNS(null, "visibility", "visible");
        //			this.labelID.setTextContent("Handle="+handle);
    }

    public void setDXFDocument(DXFDocument doc) {
        this.dxfDocument = doc;
    }
}
