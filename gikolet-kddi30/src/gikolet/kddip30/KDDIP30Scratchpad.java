/*
 * Created on 2005/03/26
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.kddip30;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;

import gikolet.base.io.Scratchpad;

/**
 * @author tetsutaro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class KDDIP30Scratchpad extends Scratchpad {

	public InputStream openInputStream() throws IOException {
		return Connector.openInputStream("storage:");
	}

	public OutputStream openOutputStream() throws IOException {
		return Connector.openOutputStream("storage:");
	}
}
