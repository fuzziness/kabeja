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
package org.kabeja.svg.action;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.kabeja.processing.BoundsFilter;
import org.kabeja.svg.SVGConstants;
import org.kabeja.svg.SVGUtils;
import org.kabeja.ui.PropertiesEditor;
import org.kabeja.ui.PropertiesListener;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MouseEvent;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGMatrix;

import de.miethxml.toolkit.ui.UIUtils;


public class SelectionAction extends AbstractAction implements SVGDocumentAction,
    EventListener, PropertiesEditor, ItemListener, CanvasUpdateRunnable,
    GroupActionUnSelector {
    protected boolean selecting = false;
    protected boolean dragging = false;
    protected Element selectionRectangle;
    protected float startX = 0;
    protected float startY = 0;
    protected SVGMatrix matrix;
    protected Map properties = new HashMap();
    protected List listeners = new ArrayList();
    float lastX;
    float lastY;
    protected boolean process = false;
    protected boolean state = false;
    protected CanvasUpdateManager updateManager;

    public SelectionAction() {
        super("Selection",
            new ImageIcon(UIUtils.resourceToBytes(SelectionAction.class,
                    "/icons/cut_edit.gif")));
        putValue(SHORT_DESCRIPTION, Messages.getString("editor.action.cut.area"));
    }

    public void handleEvent(Event evt) {
        if (this.state) {
            MouseEvent event = (MouseEvent) evt;

            if (evt.getType().equals(SVGConstants.SVGEVENT_CLICK) && selecting) {
                selecting = false;

                removeSelectionRectangle();

                // evt.stopPropagation();
            } else if (evt.getType().equals(SVGConstants.SVGEVENT_MOUSEDOWN) &&
                    !selecting) {
                // setup the start and end positions
                this.matrix = ((SVGDocument) this.selectionRectangle
                               .getOwnerDocument()).getRootElement()
                               .getScreenCTM().inverse();
                this.startX = (matrix.getA() * event.getClientX()) +
                    (matrix.getC() * event.getClientY()) + matrix.getE();
                this.startY = (matrix.getB() * event.getClientX()) +
                    (matrix.getD() * event.getClientY()) + matrix.getF();

                this.changePosition(this.startX, this.startY,
                    event.getClientX(), event.getClientX());
                this.selectionRectangle.setAttributeNS(null, "visibility",
                    "visible");

                selecting = true;

                // evt.stopPropagation();
            } else if (evt.getType().equals(SVGConstants.SVGEVENT_MOUSEMOVE) &&
                    selecting) {
                // evt.getTarget()
                // System.out.println("moving with button");
                if ((Math.abs(lastX - event.getScreenX()) > 1) ||
                        (Math.abs(lastY - event.getScreenY()) > 1)) {
                    this.changePosition(this.startX, this.startY,
                        event.getClientX(), event.getClientY());
                    lastX = event.getScreenX();
                    lastY = event.getScreenY();
                }
            } else if (evt.getType().equals(SVGConstants.SVGEVENT_MOUSEUP) &&
                    selecting) {
                selecting = false;
                process = true;
                this.changePosition(this.startX, this.startY,
                    event.getClientX(), event.getClientY());
                this.firePropertiesUpdated();
            }
        }
    }

    protected void removeSelectionRectangle() {
        this.selectionRectangle.setAttributeNS(null, "visibility", "hidden");
        this.process = false;
        this.firePropertiesUpdated();
    }

    protected void changePosition(float x, float y, float endX, float endY) {
        this.selectionRectangle.setAttributeNS(null, "y", "" + y);

        float width = (float) (((matrix.getA() * endX) +
            (matrix.getC() * endY) + matrix.getE()) - x);
        float height = (float) (((matrix.getB() * endX) +
            (matrix.getD() * endY) + matrix.getF()) - y);

        if (width < 0) {
            this.selectionRectangle.setAttributeNS(null, "x",
                "" + SVGUtils.formatNumberAttribute(x + width));
            this.selectionRectangle.setAttributeNS(null, "width",
                "" + SVGUtils.formatNumberAttribute(Math.abs(width)));
        } else {
            this.selectionRectangle.setAttributeNS(null, "x",
                "" + SVGUtils.formatNumberAttribute(x));
            this.selectionRectangle.setAttributeNS(null, "width",
                "" + SVGUtils.formatNumberAttribute(width));
        }

        if (height < 0) {
            this.selectionRectangle.setAttributeNS(null, "y",
                "" + SVGUtils.formatNumberAttribute(y + height));
            this.selectionRectangle.setAttributeNS(null, "height",
                "" + SVGUtils.formatNumberAttribute(Math.abs(height)));
        } else {
            this.selectionRectangle.setAttributeNS(null, "y", "" + y);
            this.selectionRectangle.setAttributeNS(null, "height",
                "" + SVGUtils.formatNumberAttribute(height));
        }
    }

    public void setDocument(SVGDocument doc) {
        Element el = doc.getElementById("draft");
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;

        // we parse the viewBox
        String s = doc.getDocumentElement().getAttribute("viewBox");

        String[] data = s.split("\\s+");
        Element rectangle = doc.createElementNS(svgNS, "rect");

        // we create a invisible rectangle for the capturing the mouse events
        double x = Double.parseDouble(data[0]);

        double y = Double.parseDouble(data[1]);

        double width = Double.parseDouble(data[2]);

        double height = Double.parseDouble(data[3]);

        rectangle.setAttributeNS(null, "x",
            SVGUtils.formatNumberAttribute((x - (width / 2))));

        rectangle.setAttributeNS(null, "y",
            SVGUtils.formatNumberAttribute(y - (height / 2)));

        rectangle.setAttributeNS(null, "width",
            SVGUtils.formatNumberAttribute(width * 2));
        rectangle.setAttributeNS(null, "height",
            SVGUtils.formatNumberAttribute(height * 2));
        rectangle.setAttributeNS(null, "style", "fill:blue;opacity:0.0;");

        EventTarget t = (EventTarget) rectangle;
        t.addEventListener("click", this, false);
        t.addEventListener("mousedown", this, false);
        t.addEventListener("mouseup", this, false);
        t.addEventListener("mousemove", this, false);

        this.selectionRectangle = doc.createElementNS(svgNS, "rect");
        this.selectionRectangle.setAttributeNS(null, "x", data[0]);
        this.selectionRectangle.setAttributeNS(null, "y", data[1]);
        this.selectionRectangle.setAttributeNS(null, "width", data[2]);
        this.selectionRectangle.setAttributeNS(null, "height", data[3]);
        this.selectionRectangle.setAttributeNS(null, "style",
            "fill:blue;fill-opacity:0.10;stroke:black;stroke-width:0.10%;stroke-opacity:1.0");
        this.selectionRectangle.setAttributeNS(null, "visibility", "hidden");
        //doc.getDocumentElement().appendChild(this.selectionRectangle);
        //doc.getDocumentElement().appendChild(rectangle);
        doc.getDocumentElement()
           .insertBefore(rectangle, doc.getDocumentElement().getFirstChild());
        doc.getDocumentElement().insertBefore(this.selectionRectangle, rectangle);
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

    public void setProperties(Map properties) {
    }

    protected void firePropertiesUpdated() {
        // update the properties data
        double height = Double.parseDouble(this.selectionRectangle.getAttribute(
                    "height"));
        // TODO Auto-generated method stub
        this.properties.put(BoundsFilter.PROPERTY_X,
            this.selectionRectangle.getAttribute("x"));
        this.properties.put(BoundsFilter.PROPERTY_Y,
            "" +
            (((-1) * Double.parseDouble(this.selectionRectangle.getAttribute(
                    "y"))) - height));
        this.properties.put(BoundsFilter.PROPERTY_WIDTH,
            this.selectionRectangle.getAttribute("width"));
        this.properties.put(BoundsFilter.PROPERTY_HEIGHT, "" + height);
        this.properties.put(BoundsFilter.PROPERTY_PROCESS,
            Boolean.toString(process));

        Iterator i = this.listeners.iterator();

        while (i.hasNext()) {
            PropertiesListener listener = (PropertiesListener) i.next();
            listener.propertiesChanged(this.properties);
        }
    }

    public void actionPerformed(ActionEvent e) {
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            this.state = true;
        } else {
            this.state = false;
            // this should run in the UpdateManager RunnableQueue
            this.removeSelectionRectangle();
        }
    }

    public void setCanvasUpdateManager(CanvasUpdateManager manager) {
        this.updateManager = manager;
    }
}
