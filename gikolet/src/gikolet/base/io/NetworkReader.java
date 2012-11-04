/*
 * Created on 2005/01/22
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.base.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author tetsutaro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class NetworkReader {
	private String	_uri;

	protected abstract InputStream openInputStream() throws IOException;

	protected abstract OutputStream openOutputStream() throws IOException;

	public abstract void open() throws IOException;

	public abstract void close() throws IOException;

	public abstract boolean isOpen();

	protected NetworkReader(String uri) {
		if (uri == null) {
			throw new NullPointerException();
		}
		_uri = uri;
		System.out.println(getURI());
	}

	public void write(byte[] bytes) throws IOException {
		OutputStream outputStream = null;
		try {
			outputStream = openOutputStream();
			outputStream.write(bytes);
			outputStream.flush();
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	public byte[] readToEnd() throws IOException {
		System.gc();

		InputStream inputStream = null;
		try {
			inputStream = openInputStream();

			ByteArrayOutputStream baos;
			baos = new ByteArrayOutputStream();

			byte[] buf = new byte[256];
			int t = 0;
			while ((t = inputStream.read(buf)) != -1) {
				if (!isOpen()) {
					throw new IOException("connect failed");
				}
				baos.write(buf, 0, t);
			}

			return baos.toByteArray();
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	public String getURI() {
		return _uri;
	}
}