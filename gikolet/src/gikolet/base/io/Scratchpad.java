/*
 * Created on 2005/03/26
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.base.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author tetsutaro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class Scratchpad {
	public abstract InputStream openInputStream() throws IOException;

	public abstract OutputStream openOutputStream() throws IOException;
}