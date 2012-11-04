/*
 * Created on 2004/04/25 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gikolet.base.ui;

import java.util.Vector;

/**
 * @author tetsutaro To change the template for this generated type comment go
 *         to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class ContainerControl extends Control {
	private Vector			controls;
	private int				_topInset;
	private int				_bottomInset;
	private int				_leftInset;
	private int				_rightInset;

	private final boolean	validateRoot;

	protected ContainerControl(boolean focusable, boolean validateRoot) {
		super(focusable);

		this.validateRoot = validateRoot;

		_topInset = 0;
		_bottomInset = 0;
		_leftInset = 0;
		_rightInset = 0;
	}

	void setDisplay(Display display) {
		super.setDisplay(display);

		if (controls != null) {
			for (int i = 0; i < controls.size(); i++) {
				((Control) controls.elementAt(i)).setDisplay(display);
			}
		}
	}

	/*
	 * protected final void setInsets(Insets insets) {
	 * setInsets(insets.getTop(), insets.getBottom(), insets.getLeft(),
	 * insets.getRight()); }
	 */

	protected void setInsets(int top, int bottom, int left, int right) {
		setInsetsCore(top, bottom, left, right);

		revalidate();
		repaint();
	}

	protected void setInsetsCore(int top, int bottom, int left, int right) {
		_topInset = top;
		_bottomInset = bottom;
		_leftInset = left;
		_rightInset = right;
	}

	/*
	 * public final Insets getInsets() { return this.insets; }
	 */

	public int getClientAreaX() {
		return _leftInset;
	}

	public int getClientAreaY() {
		return _topInset;
	}

	public int getClientAreaWidth() {
		return getWidth() - _leftInset - _rightInset;
	}

	public int getClientAreaHeight() {
		return getHeight() - _topInset - _bottomInset;
	}

	public final Rectangle getClientArea() {
		return new Rectangle(getClientAreaX(), getClientAreaY(),
				getClientAreaWidth(), getClientAreaHeight());
	}

	public void setFont(Font font) {
		Font setFont = getSetFont();
		if (setFont != font) {
			super.setFont(font);
			childFireFontChanged(this);
		}
	}

	void childFireFontChanged(Control c) {
		if (c instanceof ContainerControl) {
			ContainerControl parent = (ContainerControl) c;
			for (int i = 0; i < parent.getControlCount(); i++) {
				Control control = parent.getControlAt(i);
				if (!control.isSetFont()) {
					control.fontChanged();
					childFireFontChanged(control);
					if (parent.getValidateRoot()) {
						parent.revalidate();
					}
				}
			}
		}
	}

	public void setBackColor(Color color) {
		Color setBackColor = getSetBackColor();
		if (setBackColor != color) {
			super.setBackColor(color);
			childFireBackColortChanged(this);
		}
	}

	void childFireBackColortChanged(Control c) {
		if (c instanceof ContainerControl) {
			ContainerControl parent = (ContainerControl) c;
			for (int i = 0; i < parent.getControlCount(); i++) {
				Control control = parent.getControlAt(i);
				if (!control.isSetBackColor()) {
					control.backColorChanged();
					childFireBackColortChanged(control);
				}
			}
		}
	}

	public void setForeColor(Color color) {
		Color setForeColor = getSetForeColor();
		if (setForeColor != color) {
			super.setForeColor(color);
			childFireForeColortChanged(this);
		}
	}

	void childFireForeColortChanged(Control c) {
		if (c instanceof ContainerControl) {
			ContainerControl parent = (ContainerControl) c;
			for (int i = 0; i < parent.getControlCount(); i++) {
				Control control = parent.getControlAt(i);
				if (!control.isSetForeColor()) {
					control.foreColorChanged();
					childFireForeColortChanged(control);
				}
			}
		}
	}

	public int indexOf(Control control) {
		return controls.indexOf(control);
	}

	public void add(Control control) {
		if (control == null) {
			throw new NullPointerException("control is null.");
		}
		if (isContain(control)) {
			throw new IllegalArgumentException("control is contains");
		}
		if (control instanceof RootControl) {
			throw new IllegalArgumentException("control is RootControl");
		}

		for (Control c = this; c != null; c = c.getParent()) {
			if (c == control) {
				throw new IllegalArgumentException("control is parent");
			}
		}

		if (this.controls == null) {
			this.controls = new Vector();
		}

		ContainerControl parent = control.getParent();
		if (parent != null) {
			parent.remove(control);
		}

		this.controls.addElement(control);
		control.setDisplay(getDisplay());
		control.setParent(this);
		controlAdded(control);

		if (getFont() != null && !control.isSetFont()) {
			control.fontChanged();
			childFireFontChanged(control);
		}
		if (getBackColor() != null && !control.isSetBackColor()) {
			control.backColorChanged();
			childFireBackColortChanged(control);
		}
		if (getForeColor() != null && !control.isSetForeColor()) {
			control.foreColorChanged();
			childFireForeColortChanged(control);
		}
		RootControl root = getRootControl();
		if (root != null) {
			if (root.getFocus() == null) {
				root.transferFocusForward();
			}
		}

		revalidate();
		repaint();
	}

	public void remove(Control control) {
		if (control == null) {
			throw new NullPointerException("control is null.");
		}
		if (!isContain(control)) {
			throw new IllegalArgumentException("control is not contains.");
		}
		this.controls.removeElement(control);
		if (getDisplay() != null) {
			control.setDisplay(null);
		}
		focusInvalidate();

		control.setParent(null);

		if (getFont() != null && !control.isSetFont()) {
			control.fontChanged();
			childFireFontChanged(control);
		}
		if (getBackColor() != null && !control.isSetBackColor()) {
			control.backColorChanged();
			childFireBackColortChanged(control);
		}
		if (getForeColor() != null && !control.isSetForeColor()) {
			control.foreColorChanged();
			childFireForeColortChanged(control);
		}

		revalidate();
		repaint();

		controlRemoved(control);

	}

	public void removeAll() {
		for (int i = 0; i < getControlCount(); i++) {
			remove(getControlAt(0));
		}
	}

	public boolean isContain(Control control) {
		if (this.controls == null) {
			return false;
		}
		return this.controls.contains(control);
	}

	public Control getControlAt(int index) {
		if (index < 0 || this.controls == null || getControlCount() <= index) {
			throw new IndexOutOfBoundsException("index is illegal.");
		}
		return (Control) this.controls.elementAt(index);
	}

	public int getControlCount() {
		return (this.controls == null) ? 0 : this.controls.size();
	}

	public Dimension getPreferredSize(int hintWidth, int hintHeight) {
		Dimension ps = super.getPreferredSize(hintWidth, hintHeight);

		ps.setWidth(ps.getWidth() + getLeftInset() + getRightInset());
		ps.setHeight(ps.getHeight() + getTopInset() + getBottomInset());

		return ps;
	}

	public int getBottomInset() {
		return _bottomInset;
	}

	public int getRightInset() {
		return _rightInset;
	}

	public int getTopInset() {
		return _topInset;
	}

	public int getLeftInset() {
		return _leftInset;
	}

	public final boolean getValidateRoot() {
		return this.validateRoot;
	}

	protected void paint(Graphics g) {
		super.paint(g);
		if (getControlCount() > 0) {
			if (getWidth() - getLeftInset() - getRightInset() >= 0
					&& getHeight() - getTopInset() - getBottomInset() >= 0) {
				int cx = g.getClipX();
				int cy = g.getClipY();
				int cw = g.getClipWidth();
				int ch = g.getClipHeight();

				g.clipRect(getClientAreaX(), getClientAreaY(),
						getClientAreaWidth(), getClientAreaHeight());
				paintChildren(g.createGraphics());

				g.setClip(cx, cy, cw, ch);
			}
		}
	}

	protected void paintChildren(Graphics g) {
		for (int i = 0; i < getControlCount(); i++) {
			Control c = getControlAt(i);
			if (c.getVisible() && g.getClipX() < c.getX() + c.getWidth()
					&& g.getClipY() < c.getY() + c.getHeight()
					&& c.getX() < g.getClipX() + g.getClipWidth()
					&& c.getY() < g.getClipY() + g.getClipHeight()) {

				Graphics cg = g.createGraphics(c.getX(), c.getY(),
						c.getWidth(), c.getHeight());
				cg.setBackColor(c.getBackColor());
				cg.setColor(c.getForeColor());
				cg.setFont(c.getFont());
				c.paint(cg);
			}
		}
	}

	protected void controlAdded(Control control) {
	}

	protected void controlRemoved(Control control) {
	}

	protected void setVisibleCore(boolean visible) {
		if (visible != getVisible()) {
			boolean showing = isShowing();

			super.setVisibleCore(visible);

			if (showing != isShowing()) {
				viewChangedCore(visible);
			}
		}
	}

	void layoutCore() {
		layout();
		for (int i = 0; i < getControlCount(); i++) {
			Control c = getControlAt(i);
			if (c instanceof ContainerControl) {
				ContainerControl cc = (ContainerControl) c;

				cc.layoutCore();
			}
		}
	}

	protected void layout() {
	}

	void viewChangedCore(boolean view) {
		if (getVisible()) {
			viewChanged(view);
			for (int i = 0; i < getControlCount(); i++) {
				Control child = getControlAt(i);

				if (child instanceof ContainerControl) {
					((ContainerControl) child).viewChangedCore(view);
				} else {
					child.viewChanged(view);
				}
			}
		}
	}
}