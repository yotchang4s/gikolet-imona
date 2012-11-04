/*
 * Created on 2005/02/28 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.midp20;

import gikolet.midp.GikoletMIDlet;
import gikolet.midp.MIDPToolkit;
import gikolet.midp20.MIDP20Toolkit;

/**
 * @author tetsutaro TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class GikoletMIDP20MIDlet extends GikoletMIDlet {

	public GikoletMIDP20MIDlet() {
		super();
	}

	protected MIDPToolkit createMIDPToolkit() {
		return new MIDP20Toolkit();
	}
}