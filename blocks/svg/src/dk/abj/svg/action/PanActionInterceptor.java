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

import java.awt.event.InputEvent;

import javax.swing.ImageIcon;

import org.apache.batik.swing.gvt.AbstractPanInteractor;
import org.kabeja.svg.action.ToggleInteractorActionAdapter;

import de.miethxml.toolkit.ui.UIUtils;


public class PanActionInterceptor extends ToggleInteractorActionAdapter {
    public PanActionInterceptor() {
        super(new AbstractPanInteractor() {
            }, InputEvent.SHIFT_MASK);
        super.putValue(super.SMALL_ICON,
            new ImageIcon(UIUtils.resourceToBytes(this.getClass(),
                    "/icons/zoom4.png")));
        super.putValue(SHORT_DESCRIPTION,
            Messages.getString("editor.action.panning"));
        super.putValue(super.NAME, "Pan");
    }
}
