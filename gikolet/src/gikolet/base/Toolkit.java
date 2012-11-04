/*
 * Created on 2005/02/28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.base;

import gikolet.base.io.NetworkReader;
import gikolet.base.io.Scratchpad;
import gikolet.base.ui.Display;
import gikolet.base.util.IllegalStateException;

import java.io.IOException;

/**
 * @author tetsutaro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class Toolkit {
	private static Toolkit	_toolkit;

	protected Toolkit() {
		super();
		if (_toolkit != null) {
			throw new IllegalStateException();
		}
		_toolkit = this;
	}

	public static Toolkit getToolkit() {
		return _toolkit;
	}

	public static Display getDisplay() {
		return getToolkit().getDisplayCore();
	}

	public abstract void openBrowser(String uri) throws IOException;

	protected abstract Display getDisplayCore();

	public abstract NetworkReader createNetworkReader(String uri);

	public abstract Scratchpad getScratchpad();
}