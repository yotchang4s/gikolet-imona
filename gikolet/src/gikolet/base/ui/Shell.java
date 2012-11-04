/*
 * Created on 2004/04/25
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gikolet.base.ui;

import gikolet.base.Toolkit;

/**
 * @author tetsutaro To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Shell extends RootControl {
	public Shell(String text) {
		super(null);

		Dimension size = Toolkit.getDisplay().getScreenSize();
		super.setBoundsCore(0, 0, size.getWidth(), size.getHeight());
		super.setVisibleCore(true);
	}

	protected void setVisibleCore(boolean visible) {
		setVisibleCore(true);
	}

	protected void setBoundsCore(int x, int y, int width, int height) {
		Dimension size = Toolkit.getDisplay().getScreenSize();
		super.setBoundsCore(0, 0, size.getWidth(), size.getHeight());
	}
}