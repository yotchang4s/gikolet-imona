/*
 * Created on 2004/04/26 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gikolet.base.ui;

import java.util.Vector;

/**
 * @author tetsutaro To change the template for this generated type comment go
 *         to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class RootControl extends Panel {
	private Control		focus;
	private MenuItem	_menuItem;
	private Vector		children;

	RootControl(RootControl root) {
		this(root, false);
	}

	RootControl(RootControl root, boolean focusable) {
		super(focusable, true);

		if (root == null) {
			setBackColor(new Color(0xECE4D8));
			setForeColor(Color.BLACK);
			setFont(Font.getDefaultFont());
		} else {
			setParent(root);
		}

		super.setVisibleCore(false);
	}

	public final void setMenuItem(MenuItem menuItem) {
		_menuItem = menuItem;
		Display display = getDisplay();
		if (display != null) {
			if (display.getCurrentWindow() == this) {
				display.setMenuItemNavi(menuItem);
			}
		}
	}

	public final MenuItem getMenuItem() {
		return _menuItem;
	}

	public void pack() {
		setSize(getPreferredSize(DEFAULT, DEFAULT));
	}

	public void setBackColor(Color color) {
		if (color == null) {
			if (getParent() == null) {
				color = new Color(0xECE4D8);
			}
		}
		Color setBackColor = getSetBackColor();
		if (setBackColor != color) {
			super.setBackColor(color);
			if (children != null) {
				for (int i = 0; i < children.size(); i++) {
					Control c = (Control) children.elementAt(i);
					c.backColorChanged();
					childFireBackColortChanged(c);
				}
			}
		}
	}

	public void setFont(Font font) {
		if (font == null) {
			if (getParent() == null) {
				font = Font.getDefaultFont();
			}
		}
		Font setFont = getSetFont();
		if (setFont != font) {
			super.setFont(font);
			if (children != null) {
				for (int i = 0; i < children.size(); i++) {
					Control c = (Control) children.elementAt(i);
					c.fontChanged();
					childFireFontChanged(c);
				}
			}
		}
	}

	public void setForeColor(Color color) {
		if (color == null) {
			if (getParent() == null) {
				color = Color.BLACK;
			}
		}
		Color setForeColor = getSetForeColor();
		if (setForeColor != color) {
			super.setForeColor(color);
			if (children != null) {
				for (int i = 0; i < children.size(); i++) {
					Control c = (Control) children.elementAt(i);
					c.foreColorChanged();
					childFireForeColortChanged(c);
				}
			}
		}
	}

	public boolean isCurrentRootControl() {
		Display display = getDisplay();
		return (display != null && display.getCurrentWindow() == this);
	}

	void setFocus(Control control) {
		setFocusCore(control);

		Display display = getDisplay();
		if (display != null) {
			if (control != null && isCurrentRootControl()) {
				display.setPopupMenuItemNavi(control.getPopupMenuItem());
			} else {
				display.setPopupMenuItemNavi(null);
			}
		}
	}

	void setFocusCore(Control control) {
		this.focus = control;
	}

	Control getFocus() {
		return this.focus;
	}

	private void addChildRootControl(RootControl root) {
		if (children == null) {
			children = new Vector();
		}
		if (!children.contains(root)) {
			if (getFont() != null && !root.isSetFont()) {
				root.fontChanged();
				childFireFontChanged(root);
			}
			if (getBackColor() != null && !root.isSetBackColor()) {
				root.backColorChanged();
				childFireBackColortChanged(root);
			}
			if (getForeColor() != null && !root.isSetForeColor()) {
				root.foreColorChanged();
				childFireForeColortChanged(root);
			}
			children.addElement(root);
		}
	}

	private void removeChildRootControl(RootControl root) {
		if (children != null && children.contains(root)) {
			if (getFont() != null && !root.isSetFont()) {
				root.fontChanged();
				childFireFontChanged(root);
			}
			if (getBackColor() != null && !root.isSetBackColor()) {
				root.backColorChanged();
				childFireBackColortChanged(root);
			}
			if (getForeColor() != null && !root.isSetForeColor()) {
				root.foreColorChanged();
				childFireForeColortChanged(root);
			}
			children.removeElement(root);
		}
	}

	protected void setVisibleCore(boolean visible) {
		if (visible == getVisible()) {
			return;
		}

		super.setVisibleCore(visible);

		ContainerControl parent = getParent();
		if (parent != null) {
			RootControl rootParent = (RootControl) getParent();
			if (visible) {
				rootParent.addChildRootControl(this);
			} else {
				rootParent.removeChildRootControl(this);
			}
		}
		/*
		 * if (visible) { if (getFocus() == null) { transferFocusForward(); } }
		 */
	}
}