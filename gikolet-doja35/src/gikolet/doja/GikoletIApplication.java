/*
 * Created on 2005/02/28 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.doja;

import gikolet.Gikolet;
import gikolet.GikoletExtensions;

import com.nttdocomo.ui.Dialog;
import com.nttdocomo.ui.IApplication;

/**
 * @author tetsutaro TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class GikoletIApplication extends IApplication {
	private DoJaToolkit			_dojaToolkit;
	private String				_startMessage;

	private static IApplication	_iApplication;

	public GikoletIApplication() {
		_startMessage = "IApplication起動";
		_iApplication = this;

		_startMessage = "Toolkit生成";
		_dojaToolkit = new DoJaToolkit();

		_startMessage = "Extensions生成";
		final GikoletExtensions ge = new GikoletExtensions();

		_startMessage = "Gikolet開始";
		Exception e = _dojaToolkit.getDoJaDisplay().invokeAndWait(
				new Runnable() {
					public void run() {
						_startMessage = "Gikolet生成";
						new Gikolet(new String[] { _iApplication.getSourceURL()
								+ "2.cgi" }, ge);
					}
				});
		if (e != null) {
			alert(e);
			terminate();
		}
		_startMessage = null;
	}

	protected void alert(Exception e) {
		Dialog dialog = new Dialog(Dialog.DIALOG_ERROR, "初期化失敗");
		dialog.setText(e.getClass().getName() + ": " + e.getMessage()
				+ "\nState: " + _startMessage);
		dialog.show();
	}

	public static IApplication getIApplication() {
		return _iApplication;
	}

	public void start() {
		_dojaToolkit.getDoJaDisplay().fireApplicationStart();
	}
}
