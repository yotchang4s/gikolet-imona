/*
 * Created on 2005/02/28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.doja;

import gikolet.base.Toolkit;
import gikolet.base.io.NetworkReader;
import gikolet.base.io.Scratchpad;
import gikolet.base.ui.Display;

import com.nttdocomo.ui.IApplication;

/**
 * @author tetsutaro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DoJaToolkit extends Toolkit {
	private DoJaDisplay		_dojaDisplay;
	private DoJaScratchpad	_dojaScratchpad;

	public DoJaToolkit() {
		super();

		_dojaScratchpad = new DoJaScratchpad();
		_dojaDisplay = new DoJaDisplay();
	}

	public DoJaDisplay getDoJaDisplay() {
		return _dojaDisplay;
	}

	protected Display getDisplayCore() {
		return getDoJaDisplay();
	}

	public NetworkReader createNetworkReader(String uri) {
		return new DoJaNetworkReader(uri);
	}

	public void openBrowser(String uri) {
		System.out.println(uri + "�֐ڑ�");
		_dojaDisplay.fireApplicationDestroy();
		GikoletIApplication.getIApplication().launch(IApplication.LAUNCH_BROWSER,
				new String[] { uri });
	}

	public Scratchpad getScratchpad() {
		return _dojaScratchpad;
	}
}