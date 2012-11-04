/*
 * Created on 2005/02/28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.kddip30;

import gikolet.base.io.Scratchpad;
import gikolet.base.ui.Display;
import gikolet.midp.MIDPDisplay;
import gikolet.midp.MIDPToolkit;

import java.io.IOException;

import javax.microedition.io.Connector;

import com.kddi.io.BrowserConnection;

/**
 * @author tetsutaro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class KDDIP30Toolkit extends MIDPToolkit {
	private KDDIP30Scratchpad _kddip30Scratchpad;
	private KDDIP30Display _kddip30Display;
	
	public KDDIP30Toolkit() {
		_kddip30Scratchpad = new KDDIP30Scratchpad();
		_kddip30Display = new KDDIP30Display();
	}
	
	public MIDPDisplay getMIDPDisplay() {
		return _kddip30Display;
	}
	
	protected Display getDisplayCore() {
		return _kddip30Display;
	}

	public void openBrowser(String uri) throws IOException {
		String scheme;

		if (uri.startsWith("https://")) {
			uri = uri.substring(8, uri.length());
			scheme = "urls";
		} else if (uri.startsWith("http://")) {
			uri = uri.substring(7, uri.length());
			scheme = "url";
		} else {
			scheme = "url";
		}
		BrowserConnection bc = null;
		_kddip30Display.fireApplicationDestroy();
		try {
			bc = (BrowserConnection) Connector.open(scheme + "://" + uri);
		} finally {
			if (bc != null) {
				bc.close();
			}
		}
	}
	
	public Scratchpad getScratchpad() {
		return _kddip30Scratchpad;
	}
}