/*
 * Created on 2005/04/09
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.kddip30;

import gikolet.base.ui.events.KeyEvent;
import gikolet.midp.MIDPDisplay;

/**
 * @author tetsutaro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class KDDIP30Display extends MIDPDisplay {
	protected void fireApplicationDestroy() {
		super.fireApplicationDestroy();
	}

	protected int changeMIDPCanvasCodeToKeyEventCode(int keyCode) {
		if (keyCode == 0) {
			return KeyEvent.CLEAR;
		}
		return super.changeMIDPCanvasCodeToKeyEventCode(keyCode);
	}
}
