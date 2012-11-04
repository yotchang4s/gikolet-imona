/*
 * Created on 2005/02/28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.midp;

import gikolet.base.Toolkit;
import gikolet.base.io.NetworkReader;
import gikolet.base.io.Scratchpad;
import gikolet.base.ui.Display;

import java.io.IOException;

/**
 * @author tetsutaro
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class MIDPToolkit extends Toolkit {
	private MIDPDisplay		_midpDisplay;
	private MIDPScratchpad	_midpScratchpad;

	protected MIDPToolkit() {
	}

	public MIDPDisplay getMIDPDisplay() {
		if (_midpDisplay == null) {
			_midpDisplay = new MIDPDisplay();
		}
		return _midpDisplay;
	}

	protected Display getDisplayCore() {
		return getMIDPDisplay();
	}

	public NetworkReader createNetworkReader(String uri) {
		return new MIDPNetworkReader(uri);
	}

	public void openBrowser(String uri) throws IOException {
		throw new IOException();
	}

	public Scratchpad getScratchpad() {
		if (_midpScratchpad == null) {
			_midpScratchpad = new MIDPScratchpad();
		}
		return _midpScratchpad;
	}
}