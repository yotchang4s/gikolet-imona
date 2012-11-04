/*
 * Created on 2005/02/28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.midp20;

import gikolet.midp.GikoletMIDlet;
import gikolet.midp.MIDPToolkit;

import java.io.IOException;

/**
 * @author tetsutaro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MIDP20Toolkit extends MIDPToolkit {
	public MIDP20Toolkit() {
	}

	public void openBrowser(String uri) throws IOException {
		GikoletMIDlet.getMIDlet().platformRequest(uri);
	}
}