/*
 * Created on 2004/04/24
 */
package gikolet.base.ui;

import gikolet.base.ui.events.KeyEvent;
import gikolet.base.ui.events.KeyEventListener;

import java.util.Vector;

/**
 * @author 鉄太郎
 */
public class Control {
	public final static int		DEFAULT	= -1;

	private Rectangle			bounds;

	private ContainerControl	parent;
	private Display				_display;

	private boolean				visible;
	private boolean				focusable;
	private boolean				defaultFocusable;

	private Font				font;
	private Color				foreColor;
	private Color				backColor;

	private String				name;
	private String				text;

	private Vector				_keyEventListeners;

	private MenuItem			_popupMenuItem;

	protected Control(boolean focusable) {
		this.bounds = new Rectangle();
		this.text = "";

		this.defaultFocusable = focusable;
		this.focusable = this.defaultFocusable;

		this.visible = true;
	}

	Display getDisplay() {
		return _display;
	}

	void setDisplay(Display display) {
		_display = display;
	}

	public void setPopupMenuItem(MenuItem menuItem) {
		_popupMenuItem = menuItem;

		Display display = getDisplay();
		if (getDisplay() != null) {
			if (getFocused() && getRootControl().isCurrentRootControl()) {
				display.setPopupMenuItemNavi(menuItem);
			}
		}
	}

	public MenuItem getPopupMenuItem() {
		return _popupMenuItem;
	}

	public final void addKeyEventListener(KeyEventListener listener) {
		if (listener != null) {
			if (_keyEventListeners == null) {
				_keyEventListeners = new Vector();
			}
			_keyEventListeners.addElement(listener);
		}
	}

	public final void removeKeyEventListener(KeyEventListener listener) {
		if (listener != null && _keyEventListeners != null) {
			_keyEventListeners.removeElement(listener);
		}
	}

	public Rectangle getViewRect() {
		ContainerControl c = getParent();
		Rectangle r = new Rectangle(getX(), getY(), getWidth(), getHeight());
		if (c == null) {
			return r;
		}
		int x = r.x;
		int y = r.y;

		while (c != null && !(c instanceof RootControl)) {
			x += c.getX();
			y += c.getY();
			r.setLocation(r.getX() + c.getX(), r.getY() + c.getY());

			int minX = (r.x >= c.getX() + c.getClientAreaX()) ? r.x : c.getX()
					+ c.getClientAreaX();
			int minY = (r.y >= c.getY() + c.getClientAreaY()) ? r.y : c.getY()
					+ c.getClientAreaY();
			int maxX = (r.x + r.width <= c.getX() + c.getClientAreaX()
					+ c.getClientAreaWidth()) ? r.x + r.width : c.getX()
					+ c.getClientAreaX() + c.getClientAreaWidth();
			int maxY = (r.y + r.height <= c.getY() + c.getClientAreaY()
					+ c.getClientAreaHeight()) ? r.y + r.height : c.getY()
					+ c.getClientAreaY() + c.getClientAreaHeight();

			r.setBounds(minX, minY, maxX - minX, maxY - minY);

			c = c.getParent();
		}
		r.setLocation(r.getX() - x, r.getY() - y);
		return r;
	}

	/**
	 * このコンポーネントの現在の左上隅のx座標を返します。
	 * 
	 * @return 親の座標空間でのこのコンポーネントの現在の左上隅のx座標。
	 */
	public final int getX() {
		return this.bounds.getX();
	}

	/**
	 * このコンポーネントの現在の左上隅のy座標を返します。
	 * 
	 * @return 親の座標空間でのこのコンポーネントの現在の左上隅のy座標。
	 */
	public final int getY() {
		return this.bounds.getY();
	}

	/**
	 * このコンポーネントの現在の幅を返します。
	 * 
	 * @return このコンポーネントの現在の幅。
	 */
	public final int getWidth() {
		return this.bounds.getWidth();
	}

	/**
	 * このコンポーネントの現在の高さを返します。
	 * 
	 * @return このコンポーネントの現在の高さ。
	 */
	public final int getHeight() {
		return this.bounds.getHeight();
	}

	/**
	 * このコンポーネントの新しい座標をxとyに変更します。
	 * 
	 * @param x
	 *            親の座標空間でのこのコンポーネントの新しい左上隅のx座標。
	 * @param y
	 *            親の座標空間でのこのコンポーネントの新しい左上隅のy座標。
	 */
	public final void setLocation(int x, int y) {
		setBounds(x, y, getWidth(), getHeight());
	}

	/**
	 * このコンポーネントの現在の左上隅の座標を返します。
	 * 
	 * @return 親の座標空間でのこのコンポーネントの現在の左上隅の座標。
	 */
	public final Point getLocation() {
		return new Point(getX(), getY());
	}

	public final void setSize(Dimension d) {
		setBounds(getX(), getY(), d.getWidth(), d.getHeight());
	}

	/**
	 * このコンポーネントの現在のサイズをwidthとheightに変更します。
	 * 
	 * @param width
	 *            このコンポーネントの新しい幅。
	 * @param height
	 *            このコンポーネントの新しい高さ。
	 */
	public final void setSize(int width, int height) {
		setBounds(getX(), getY(), width, height);
	}

	/**
	 * このコンポーネントの現在のサイズを返します。
	 * 
	 * @return このコンポーネントの現在のサイズ。
	 */
	public final Dimension getSize() {
		return new Dimension(getWidth(), getHeight());
	}

	/**
	 * このコンポーネントの現在の境界を返します。
	 * 
	 * @return 親の座標空間でのこのコンポーネントの境界。
	 */
	public final Rectangle getBounds() {
		return new Rectangle(getX(), getY(), getWidth(), getHeight());
	}

	public void setBounds(Rectangle bounds) {
		setBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds
				.getHeight());
	}

	/**
	 * このコンポーネントの現在の境界を返します。
	 * 
	 * @param x
	 *            親の座標空間でのこのコンポーネントの新しい左上隅のx座標。
	 * @param y
	 *            親の座標空間でのこのコンポーネントの新しい左上隅のy座標。
	 * @param width
	 *            このコンポーネントの新しい幅。
	 * @param height
	 *            このコンポーネントの新しい高さ。
	 */
	public final void setBounds(int x, int y, int width, int height) {
		int oldX = getX();
		int oldY = getY();
		int oldW = getWidth();
		int oldH = getHeight();

		// boolean moved = (oldX != x) || (oldY != y);
		boolean resized = (oldW != width) || (oldH != height);

		setBoundsCore(x, y, width, height);

		if (resized) {
			revalidate();
		}

		Control parent = getParent();
		if (parent != null) {
			parent.repaint(oldX, oldY, oldW, oldH);
		}
		repaint();
	}

	public final boolean isShowing() {
		Control parent = this;
		while (parent != null) {
			if (!parent.getVisible()) {
				return false;
			}
			if (parent instanceof RootControl) {
				return true;
			}

			parent = parent.getParent();
		}
		return false;
	}

	public final RootControl getRootControl() {
		Control parent = this;
		while (parent != null) {
			if (parent instanceof RootControl) {
				return (RootControl) parent;
			}
			parent = parent.getParent();
		}
		return null;
	}

	public final boolean containsFocus() {
		RootControl root = getRootControl();
		if (root == null) {
			return false;
		}
		Control parent = root.getFocus();
		while (parent != null) {
			if (parent == this) {
				return true;
			}
			parent = parent.getParent();
		}
		return false;
	}

	public final boolean canFocus() {
		if (!getFocusable()) {
			return false;
		}
		Control parent = this;
		while (true) {
			if (parent == null) {
				return false;
			} else if (!parent.getVisible()) {
				return false;
			} else if (parent instanceof RootControl) {
				return true;
			}
			parent = parent.getParent();
		}
	}

	public final boolean setFocus() {
		if (canFocus()) {
			RootControl root = getRootControl();
			Control old = root.getFocus();
			root.setFocus(this);
			if (old != null) {
				old.focusLost();
				old.repaint();
			}
			focusGained();
			repaint();

			return true;
		}
		return false;
	}

	public final boolean getFocused() {
		RootControl root = getRootControl();
		if (root == null) {
			return false;
		}
		return root.getFocus() == this;
	}

	public boolean isSetBackColor() {
		return backColor != null;
	}

	// このメソッドを継承する際は必ずこのメソッドを呼ぶこと
	public void setBackColor(Color color) {
		if ((backColor == null && color == null)
				|| (backColor != null && backColor.equals(color))
				|| (color != null && color.equals(backColor))) {
			return;
		}
		this.backColor = color;
		backColorChanged();
		repaint();
	}

	public final Color getBackColor() {
		Control c = this;
		do {
			if (c.backColor != null) {
				return c.backColor;
			}
			c = c.getParent();
		} while (c != null);

		return null;
	}

	public final Color getParentBackColor() {
		ContainerControl parent = getParent();
		if (parent != null) {
			return parent.getBackColor();
		}
		return null;
	}

	public final Color getSetBackColor() {
		return backColor;
	}

	public boolean isSetForeColor() {
		return foreColor != null;
	}

	// このメソッドを継承する際は必ずこのメソッドを呼ぶこと
	public void setForeColor(Color color) {
		if ((foreColor == null && color == null)
				|| (foreColor != null && foreColor.equals(color))
				|| (color != null && color.equals(foreColor))) {
			return;
		}
		this.foreColor = color;
		foreColorChanged();
		repaint();
	}

	public final Color getForeColor() {
		Control c = this;
		do {
			if (c.foreColor != null) {
				return c.foreColor;
			}
			c = c.getParent();
		} while (c != null);

		return null;
	}

	public final Color getParentForeColor() {
		ContainerControl parent = getParent();
		if (parent != null) {
			return parent.getForeColor();
		}
		return null;
	}

	public final Color getSetForeColor() {
		return foreColor;
	}

	public boolean isSetFont() {
		return this.font != null;
	}

	// このメソッドを継承する際は必ずこのメソッドを呼ぶこと
	public void setFont(Font font) {
		if (font == this.font) {
			return;
		}
		this.font = font;

		fontChanged();

		revalidate();
		repaint();
	}

	public final Font getFont() {
		Control c = this;
		do {
			if (c.font != null) {
				return c.font;
			}
			c = c.getParent();
		} while (c != null);

		return null;
	}

	public final Font getParentFont() {
		ContainerControl parent = getParent();
		if (parent != null) {
			return parent.getFont();
		}
		return null;
	}

	public final Font getSetFont() {
		return font;
	}

	// 表示をするかしないか。これはこのコントロールとその子のみに影響。
	public final void setVisible(boolean visible) {
		if (visible == getVisible()) {
			return;
		}
		if (!visible) {
			repaint();
		}
		setVisibleCore(visible);
		if (visible) {
			repaint();
		}
		visibleChanged();
	}

	public final boolean getVisible() {
		return this.visible;
	}

	final void setParent(ContainerControl parent) {
		this.parent = parent;

		parentChanged();
	}

	public ContainerControl getParentValidRoot() {
		if (this instanceof RootControl) {
			return null;
		}
		ContainerControl cc = getParent();

		while (cc != null) {
			if (cc.getValidateRoot()) {
				return cc;
			}
			cc = cc.getParent();
		}
		return null;
	}

	public final ContainerControl getParent() {
		return this.parent;
	}

	public final void setName(String name) {
		if (name == null) {
			name = "";
		}
		this.name = name;
		nameChanged();
	}

	public final String getName() {
		return this.name;
	}

	public final void setText(String text) {
		if (text == null) {
			text = "";
		}
		this.text = text;

		textChanged();
	}

	public final String getText() {
		return this.text;
	}

	// このコントロールがフォーカス所有者であるかのように、フォーカスを前のコンポーネントに転送します。
	public final boolean transferFocusBackward() {
		Control prev = getPrevControl(/* !getFocusable() */);
		while (prev != null) {
			if (prev.setFocus()) {
				return true;
			}
			prev = prev.getPrevControl(/* !next.getFocusable() */);
		}
		return false;
	}

	// このコントロールがフォーカス所有者であるかのように、フォーカスを次のコンポーネントに転送します。
	public final boolean transferFocusForward() {
		Control next = getNextControl(true /* !getFocusable() */);
		while (next != null) {
			if (next.setFocus()) {
				return true;
			}
			next = next.getNextControl(true /* !next.getFocusable() */);
		}
		return false;
	}

	final Control getPrevControl() {
		if (this instanceof RootControl) {
			return null;
		}
		ContainerControl p = this.getParent();
		if (p == null) {
			return null;
		}
		boolean prev = false;
		for (int i = p.getControlCount() - 1; i >= 0; i--) {
			Control c = p.getControlAt(i);
			if (prev == true) {
				return getTreeEndControl(c);
			} else if (c == this) {
				prev = true;
			}
		}
		return p.getPrevControl();
	}

	private Control getTreeEndControl(Control control) {
		if (control instanceof ContainerControl) {
			ContainerControl container = (ContainerControl) control;
			for (int i = container.getControlCount() - 1; i >= 0; i--) {
				return getTreeEndControl(container.getControlAt(i));
			}
		}
		return control;

	}

	private final Control getNextControl(boolean children) {
		if (children && this instanceof ContainerControl) {
			ContainerControl cc = (ContainerControl) this;
			if (cc.getControlCount() > 0) {
				return cc.getControlAt(0);
			}
		}

		ContainerControl parent = this.getParent();
		Control child = this;
		while (parent != null && !(child instanceof RootControl)) {
			boolean next = false;
			for (int i = 0; i < parent.getControlCount(); i++) {
				Control c = parent.getControlAt(i);
				if (next == true) {
					return c;
				} else if (c == child) {
					next = true;
				}
			}
			child = parent;
			parent = child.getParent();
		}
		return null;
	}

	public void setFocusable(boolean focusable) {
		if (this.defaultFocusable) {
			this.focusable = focusable;
		}
	}

	public boolean getDefaultFocusable() {
		return this.defaultFocusable;
	}

	public final boolean getFocusable() {
		return this.focusable;
	}

	public Dimension getPreferredSize(int hintWidth, int hintHeight) {
		Dimension size = getSize();
		if (hintWidth != DEFAULT) {
			size.setWidth(hintWidth);
		}
		if (hintHeight != DEFAULT) {
			size.setHeight(hintHeight);
		}
		return size;
	}

	public final void paintImmediately() {
		paintImmediately(0, 0, getWidth(), getHeight());
	}

	public final void paintImmediately(int x, int y, int width, int height) {
		if (!getVisible() || getDisplay() == null) {
			return;
		}
		if (x < 0) {
			width += x;
			x = 0;
		}
		if (y < 0) {
			height += y;
			y = 0;
		}
		if (x + width >= getWidth()) {
			width = getWidth() - x;
		}

		if (height + y >= getHeight()) {
			height = getHeight() - y;
		}

		x += getX();
		y += getY();
		if (this instanceof RootControl) {
			getDisplay().paint(x, y, width, height, true);
		} else {
			ContainerControl parent = getParent();
			if (parent != null) {
				parent.paintImmediately(x, y, width, height);
			}
		}
	}

	public final void repaint() {
		repaint(0, 0, getWidth(), getHeight());
	}

	public final void repaint(int x, int y, int width, int height) {
		if (!getVisible() || getDisplay() == null || width <= 0 || height <= 0) {
			return;
		}
		if (x < 0) {
			width += x;
			x = 0;
		}
		if (y < 0) {
			height += y;
			y = 0;
		}
		if (x + width >= getWidth()) {
			width = getWidth() - x;
		}

		if (height + y >= getHeight()) {
			height = getHeight() - y;
		}
		if (width <= 0 || height <= 0) {
			return;
		}
		x += getX();
		y += getY();
		if (this instanceof RootControl) {
			getDisplay().repaint(x, y, width, height);
		} else {
			ContainerControl parent = getParent();
			if (parent != null) {
				parent.repaint(x, y, width, height);
			}
		}
	}

	protected void scrollRectToVisible(int x, int y, int width, int height) {
		ContainerControl parent = getParent();
		int dx = getX(), dy = getY();

		if (parent != null) {
			x += dx;
			y += dy;

			parent.scrollRectToVisible(x, y, width, height);
		}
	}

	public final void revalidate() {
		if (getDisplay() == null) {
			return;
		}
		if (this instanceof ContainerControl) {
			ContainerControl cc = (ContainerControl) this;

			if (!cc.getValidateRoot()) {
				if (getParent() != null) {
					getParent().revalidate();
				}
			} else {
				getDisplay().validate(cc, false);
			}
		} else {
			if (getParent() != null) {
				getParent().revalidate();
			}
		}
	}

	public final void validate() {
		if (getDisplay() == null) {
			return;
		}
		if (this instanceof ContainerControl) {
			ContainerControl cc = (ContainerControl) this;

			if (!cc.getValidateRoot()) {
				if (getParent() != null) {
					getParent().validate();
				}
			} else {
				getDisplay().validate(cc, true);
			}
		} else {
			if (getParent() != null) {
				getParent().validate();
			}
		}
	}

	protected boolean processKeyPreview(KeyEvent e) {
		return false;
	}

	void processKeyMessage(int keyActionType, int keyCode) {
		KeyEvent e = new KeyEvent(this, keyActionType, keyCode);
		processTabKey(e);

		if (!e.isConsumed()) {
			// リスナーに通知
			if (_keyEventListeners != null) {
				for (int i = 0; i < _keyEventListeners.size(); i++) {
					((KeyEventListener) _keyEventListeners.elementAt(i))
							.keyAction(e);
				}
			}
		}
		if (!e.isConsumed()) {
			keyAction(e);
		}

		if (!e.isConsumed()) {
			Control parent = getParent();
			while (parent != null && !(parent instanceof RootControl)) {
				parent.processKeyPreview(e);
				if (e.isConsumed()) {
					return;
				}
				parent = parent.getParent();
			}
		}

	}

	protected void processTabKey(KeyEvent e) {
		int type = e.getKeyActionType();
		int code = e.getKeyCode();

		// KEY_NUM1を押したらフォーカスを前へ転送
		// KEY_NUM2を押したらフォーカスを上へ転送
		// KEY_NUM3を押したらフォーカスを次へ転送
		if (type == KeyEvent.PRESSED || type == KeyEvent.REPEATED) {
			switch (code) {
				case KeyEvent.KEY_NUM1:
					if (transferFocusBackward()) {
						e.consume();
					}
					break;
				/*
				 * case KeyEvent.KEY_NUM2 : Control parent = getParent(); while
				 * (parent != null) { if (parent.setFocus()) { return true; }
				 * parent = getParent(); } return false;
				 */
				case KeyEvent.KEY_NUM3:
					if (transferFocusForward()) {
						e.consume();
					}
					break;
			}
		}
	}

	protected void setBoundsCore(int x, int y, int width, int height) {
		if (getX() == x && getY() == y && getWidth() == width
				&& getHeight() == height) {
			return;
		}

		boolean resized = (getWidth() != width) || (getHeight() != height);
		boolean moved = (getX() != x) || (getY() != y);

		this.bounds.setBounds(x, y, width, height);

		if (moved) {
			locationChanged();
		}
		if (resized) {
			sizeChanged();
		}
	}

	protected void setVisibleCore(boolean visible) {
		if (visible == getVisible()) {
			return;
		}
		this.visible = visible;
		if (!visible && visible == getVisible()) {
			focusInvalidate();
		}
		visibleChanged();
	}

	// プロパティチェンジイベント
	protected void foreColorChanged() {
	}

	protected void backColorChanged() {
	}

	protected void fontChanged() {
	}

	protected void locationChanged() {
	}

	protected void sizeChanged() {
	}

	protected void parentChanged() {
	}

	protected void visibleChanged() {
	}

	protected void nameChanged() {
	}

	protected void textChanged() {
	}

	// とりあえずToolkit内のpaint系メソッドは戻るときgが渡された時の状態に戻すのを保証する。
	protected void paint(Graphics g) {
		Color backColor = g.getBackColor();
		Color color = g.getColor();
		int strokeStyle = g.getStrokeStyle();
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();
		int translateX = g.getTranslateX();
		int translateY = g.getTranslateY();

		paintBackground(g);
		g.setBackColor(backColor);
		g.translate(-(g.getTranslateX() - translateX),
				-(g.getTranslateY() - translateY));
		g.setColor(color);
		g.setStrokeStyle(strokeStyle);
		g.setClip(clipX, clipY, clipWidth, clipHeight);

		paintControl(g);

		g.setBackColor(backColor);
		g.setColor(color);
		g.setStrokeStyle(strokeStyle);
		g.setClip(clipX, clipY, clipWidth, clipHeight);
		g.translate(-translateX, -translateY);
	}

	protected void paintControl(Graphics g) {
	}

	// このメソッドでgを操作するとき呼び出されたときと同じパラメータに戻すこと。
	// ﾏﾝﾄﾞｸｻｲならg.createGraphics()を使う。
	protected void paintBackground(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());
	}

	// TABに設定されているキーはここへはこない。
	protected void keyAction(KeyEvent event) {
		if (event == null) {
			throw new NullPointerException("event");
		}
	}

	protected void focusGained() {
	}

	protected void focusLost() {
	}

	protected void viewChanged(boolean view) {
	}

	// もし子（階層）にフォーカスがあるのならば移動させる。
	void focusInvalidate() {
		if (containsFocus()) {
			RootControl root = getRootControl();
			Control focus = root.getFocus();
			// System.out.println("a:" + focus);
			if (!focus.transferFocusBackward() && !focus.transferFocusForward()) {
				root.setFocus(null);
			}
		}
	}
}