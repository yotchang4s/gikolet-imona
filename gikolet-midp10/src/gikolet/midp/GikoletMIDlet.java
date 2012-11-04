/*
 * Created on 2005/02/28 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.midp;

import gikolet.Gikolet;
import gikolet.GikoletExtensions;

import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * @author tetsutaro TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class GikoletMIDlet extends MIDlet {
	private boolean				start	= false;
	private String				_startMessage;

	private static MIDPToolkit	_midpToolkit;
	private static Gikolet		_gikolet;
	private static MIDlet		_midlet;

	public GikoletMIDlet() {
		this(true);
	}

	protected void alert(Exception e) {
		Alert alert = new Alert("初期化失敗");
		alert.setString(e.getClass().getName() + ": " + e.getMessage()
				+ "\nState: " + _startMessage);
		alert.setTimeout(Alert.FOREVER);
		alert.setType(AlertType.ERROR);

		javax.microedition.lcdui.Display.getDisplay(this).setCurrent(alert);
	}

	protected GikoletMIDlet(boolean start) {
		_startMessage = "MIDlet起動";
		try {
			_midlet = this;

			if (start) {
				_startMessage = "鯖のURI取得";

				String severPropertyKey = "iMona-ServerURI-";

				Vector vs = new Vector();
				String sever = null;
				int i = 1;
				do {
					sever = getAppProperty(severPropertyKey + i++);
					if (sever != null) {
						vs.addElement(sever);
					}
				} while (sever != null);

				String[] severs = new String[vs.size()];
				vs.copyInto(severs);

				_startMessage = "Gikoletを開始";
				startGikolet(severs);
				_startMessage = null;
			}
		} catch (Exception e) {
			alert(e);
		}
	}

	protected void startGikolet(String[] severs) {
		if (start) {
			throw new IllegalStateException();
		}
		start = true;
		final String[] fsevers = new String[severs.length];
		System.arraycopy(severs, 0, fsevers, 0, severs.length);

		_startMessage = "Toolkit生成";
		_midpToolkit = createMIDPToolkit();

		_startMessage = "Extensions生成";
		final GikoletExtensions ge = createMIDPGikoletExtensions();

		_startMessage = "Gikolet開始";
		Exception e = _midpToolkit.getMIDPDisplay().invokeAndWait(
				new Runnable() {
					public void run() {
						_startMessage = "Gikolet生成";
						_gikolet = new Gikolet(fsevers, ge);
					}
				});
		if (e != null) {
			alert(e);
		}
	}

	protected MIDPToolkit createMIDPToolkit() {
		return new MIDPToolkit();
	}

	protected static MIDPToolkit getMIDPToolkit() {
		return _midpToolkit;
	}

	protected MIDPGikoletExtensions createMIDPGikoletExtensions() {
		return new MIDPGikoletExtensions();
	}

	public static Gikolet getGikolet() {
		return _gikolet;
	}

	public static MIDlet getMIDlet() {
		return _midlet;
	}

	protected void startApp() throws MIDletStateChangeException {
		if (_midpToolkit != null) {
			_midpToolkit.getMIDPDisplay().fireApplicationStart();
		}
	}

	protected void destroyApp(boolean b) throws MIDletStateChangeException {
		if (_midpToolkit != null) {
			_midpToolkit.getMIDPDisplay().fireApplicationDestroy();
		}
	}

	protected void pauseApp() {
		if (_midpToolkit != null) {
			_midpToolkit.getMIDPDisplay().fireApplicationPause();
		}
	}
}