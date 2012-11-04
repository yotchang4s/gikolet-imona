/*
 * Created on 2005/02/28 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.kddip30;

import java.util.Vector;

import gikolet.midp.GikoletMIDlet;
import gikolet.midp.MIDPGikoletExtensions;
import gikolet.midp.MIDPToolkit;

/**
 * @author tetsutaro TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class GikoletKDDIP30MIDlet extends GikoletMIDlet {
	private String[] _writableHosts;

	public GikoletKDDIP30MIDlet() {
		// TODO 修正予定
		// super("MIDlet-X-AllowURL-", "2.cgi");

		super(false);

		Vector vs = new Vector();
		Vector vws = new Vector();

		for (int i = 1; i <= 3; i++) {
			String server = getAppProperty("MIDlet-X-AllowURL-" + i);
			if (server != null) {
				if (server.indexOf("2ch.net") != -1
						|| server.indexOf("bbspink.com") != -1) {
					vws.addElement(server);
				} else {
					vs.addElement(server + "2.cgi");
				}
			}
		}
		_writableHosts = new String[vws.size()];
		vws.copyInto(_writableHosts);

		String[] severs = new String[vs.size()];
		vs.copyInto(severs);

		startGikolet(severs);
	}

	protected MIDPToolkit createMIDPToolkit() {
		return new KDDIP30Toolkit();
	}

	protected MIDPGikoletExtensions createMIDPGikoletExtensions() {
		return new KDDIP30GikoletExtensions(_writableHosts);
	}
}
