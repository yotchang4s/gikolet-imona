/*
 * Created on 2005/02/20
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.doja;

import gikolet.base.io.NetworkReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;

import com.nttdocomo.io.HttpConnection;

/**
 * @author tetsutaro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DoJaNetworkReader extends NetworkReader {
	private HttpConnection	_httpConnection;
	private boolean			post;

	protected DoJaNetworkReader(String uri) {
		super(uri);

		post = false;
	}

	public synchronized void open() throws IOException {
		_httpConnection = (HttpConnection) Connector.open(getURI(),
				Connector.READ_WRITE, true);
	}

	protected InputStream openInputStream() throws IOException {
		HttpConnection hc = _httpConnection;
		if (hc == null) {
			throw new IOException("no open");
		}
		if (!post) {
			hc.setRequestMethod(HttpConnection.GET);
		}
		hc.connect();

		int rc = hc.getResponseCode();
		if (rc != HttpConnection.HTTP_OK) {
			throw new IOException("Response Code is " + rc);
		}
		//à”ñ°Ç™ÇÌÇ©ÇÁÇÒ(ÅL•É÷•ÅM)
		if (!isOpen()) {
			throw new IOException("connect failed");
		}
		return hc.openInputStream();
	}

	protected OutputStream openOutputStream() throws IOException {
		HttpConnection hc = _httpConnection;
		if (hc == null) {
			throw new IOException("no open");
		}
		hc.setRequestMethod(HttpConnection.POST);
		post = true;
		return hc.openOutputStream();
	}

	public synchronized void close() throws IOException {
		if (_httpConnection == null) {
			throw new IOException("no open");
		}
		_httpConnection.close();
		_httpConnection = null;
	}

	public synchronized boolean isOpen() {
		return _httpConnection != null;
	}
}