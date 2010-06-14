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

package org.kabeja.entities;

import org.kabeja.common.Type;
import org.kabeja.entities.util.TextDocument;
import org.kabeja.entities.util.TextParser;
import org.kabeja.entities.util.Utils;
import org.kabeja.math.Bounds;


/**
 * @author <a href="mailto:simon.mieth@gmx.de>Simon Mieth</a>
 *
 */
public class MText extends Text {
    public static final int ATTACHMENT_TOP_LEFT = 1;
    public static final int ATTACHMENT_TOP_CENTER = 2;
    public static final int ATTACHMENT_TOP_RIGHT = 3;
    public static final int ATTACHMENT_MIDDLE_LEFT = 4;
    public static final int ATTACHMENT_MIDDLE_CENTER = 5;
    public static final int ATTACHMENT_MIDDLE_RIGHT = 6;
    public static final int ATTACHMENT_BOTTOM_LEFT = 7;
    public static final int ATTACHMENT_BOTTOM_CENTER = 8;
    public static final int ATTACHMENT_BOTTOM_RIGHT = 9;
    private int attachmentpointLocation = 1;
    private double refwidth = 0.0;
    private double refheight = 0.0;

    
    public void setAttachmentPoint(int value) {
        this.attachmentpointLocation = value;
    }

    public void setReferenceWidth(double width) {
        this.refwidth = width;
    }

    public double getReferenceWidth() {
        return this.refwidth;
    }

    public void setReferenceHeight(double height) {
        this.refheight = height;
    }

    public double getReferenceHeight() {
        return this.refheight;
    }

    public Type<MText> getType() {
        return Type.TYPE_MTEXT;
    }

    public double getRotation() {
        if (rotation != 0.0) {
            return rotation;
        } else if ((alignmentPoint.getX() != 0.0) || (alignmentPoint.getY() != 0.0) ||
                (alignmentPoint.getZ() != 0.0)) {
            // the align point as direction vector here
            // calculate the angle between the x-axis and the direction-vector
            double[] x = { alignmentPoint.getX(), alignmentPoint.getY(), alignmentPoint.getZ() };
            double v = alignmentPoint.getX() / Utils.vectorValue(x);
            v = Math.toDegrees(Math.acos(v));

            return v;
        }

        // same as 0.0
        return rotation;
    }

    public TextDocument getTextDocument() {
        return this.textDoc;
    }

    public void setText(String text) {
        this.text = text;

        this.textDoc = TextParser.parseMText(this);
    }

    public int getAlignment() {
        return attachmentpointLocation;
    }

    public boolean isOmitLineType() {
        return true;
    }


    public Bounds getBounds() {
        Bounds bounds = new Bounds();
        int l = this.textDoc.getMaximumLineLength();

        if (l > 0) {
            double h = getHeight();

            if (h == 0.0) {
                h = getReferenceHeight();
            }

            double w = l * 0.7 * h;
            h *= this.textDoc.getLineCount();

            switch (this.attachmentpointLocation) {
            case ATTACHMENT_BOTTOM_CENTER:
                bounds.addToBounds(this.p.getX() + (w / 2), this.p.getY() + h,
                    p.getZ());
                bounds.addToBounds(this.p.getX() - (w / 2), this.p.getY(),
                    p.getZ());

                break;

            case ATTACHMENT_BOTTOM_LEFT:
                bounds.addToBounds(this.p.getX() + w, this.p.getY() + h,
                    p.getZ());
                bounds.addToBounds(this.p.getX(), this.p.getY(), p.getZ());

                break;

            case ATTACHMENT_BOTTOM_RIGHT:
                bounds.addToBounds(this.p.getX() - w, this.p.getY() + h,
                    p.getZ());
                bounds.addToBounds(this.p.getX(), this.p.getY(), p.getZ());

                break;

            case ATTACHMENT_MIDDLE_CENTER:
                bounds.addToBounds(this.p.getX() + (w / 2),
                    this.p.getY() + (h / 2), p.getZ());
                bounds.addToBounds(this.p.getX() - (w / 2),
                    this.p.getY() - (h / 2), p.getZ());

                break;

            case ATTACHMENT_MIDDLE_LEFT:
                bounds.addToBounds(this.p.getX(), this.p.getY() + (h / 2),
                    p.getZ());
                bounds.addToBounds(this.p.getX() + w, this.p.getY() - (h / 2),
                    p.getZ());

                break;

            case ATTACHMENT_MIDDLE_RIGHT:
                bounds.addToBounds(this.p.getX(), this.p.getY() + (h / 2),
                    p.getZ());
                bounds.addToBounds(this.p.getX() - w, this.p.getY() - (h / 2),
                    p.getZ());

                break;

            case ATTACHMENT_TOP_LEFT:
                bounds.addToBounds(this.p.getX(), this.p.getY(), p.getZ());
                bounds.addToBounds(this.p.getX() + w, this.p.getY() - h,
                    p.getZ());

                break;

            case ATTACHMENT_TOP_CENTER:
                bounds.addToBounds(this.p.getX() + (w / 2), this.p.getY(),
                    p.getZ());
                bounds.addToBounds(this.p.getX() - (w / 2), this.p.getY() - h,
                    p.getZ());

                break;

            case ATTACHMENT_TOP_RIGHT:
                bounds.addToBounds(this.p.getX(), this.p.getY(), p.getZ());
                bounds.addToBounds(this.p.getX() - w, this.p.getY() - h,
                    p.getZ());

                break;
            }
        } else {
            bounds.setValid(false);
        }

        return bounds;
    }
}
