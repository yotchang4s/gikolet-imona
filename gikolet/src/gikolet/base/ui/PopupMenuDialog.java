/*
 * 作成日： 2004/11/26 TODO この生成されたファイルのテンプレートを変更するには次を参照。 ウィンドウ ＞ 設定 ＞ Java ＞
 * コード・スタイル ＞ コード・テンプレート
 */
package gikolet.base.ui;

import gikolet.base.Toolkit;
import gikolet.base.ui.events.ActionEvent;
import gikolet.base.ui.events.ActionListener;
import gikolet.base.ui.events.KeyEvent;
import gikolet.base.ui.events.KeyEventListener;

/**
 * @author tetsutaro TODO この生成された型コメントのテンプレートを変更するには次を参照。 ウィンドウ ＞ 設定 ＞ Java ＞
 *         コード・スタイル ＞ コード・テンプレート
 */
class PopupMenuDialog extends Dialog {
	private Menu			menu;
	private RootControl		_root;
	private List			list;

	private PopupMenuDialog	_menuDialog;
	private PopupMenuDialog	_parent;

	private MenuItem		_cancelMenuItem;

	PopupMenuDialog(RootControl root, Menu menu) {
		super(root, null, true);

		_root = root;

		this.list = new List();
		this.menu = menu;
		_cancelMenuItem = new MenuItem("取消");

		setMenuItem(_cancelMenuItem);
		_cancelMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		this.list.addKeyEventListener(new KeyEventListener() {
			public void keyAction(KeyEvent e) {
				if (e.getKeyActionType() == KeyEvent.PRESSED
						&& (e.getKeyCode() == KeyEvent.ENTER || e.getKeyCode() == KeyEvent.KEY_NUM5)) {
					clicked();
				}
			}
		});

		updateUI();

		// setLayoutManager(null);

		add(this.list);
	}

	public PopupMenuDialog getPopupMenuDialogParent() {
		return _parent;
	}

	// TODO PopupMenuDialogにキャンセルを実装したほうが良いけどね
	private void clicked() {
		int index = this.list.getSelectedIndex();
		MenuItem menuItem = this.menu.get(index);
		menuItem.fireAction();
		if (menuItem instanceof Menu) {
			Menu menu = (Menu) menuItem;
			if (menu.getMenuItemSize() > 0) {
				_menuDialog = new PopupMenuDialog(_root, menu);
				_menuDialog._parent = this;

				_menuDialog.setVisible(true);
				return;
			}
		}
		PopupMenuDialog parent = this;
		do {
			parent.setVisible(false);
			parent = parent._parent;
		} while (parent != null);
	}

	void updateUI() {
		if (_menuDialog != null) {
			_menuDialog.updateUI();
		}
		if (menu.getMenuItemSize() == 0) {
			setVisible(false);
		}
		this.list.clear();
		for (int i = 0; i < this.menu.getMenuItemSize(); i++) {
			MenuItem mi = this.menu.get(i);
			String str = mi.getLabel();
			/*
			 * if (mi instanceof Menu) { Menu m = (Menu) mi; }
			 */
			this.list.add(str);
		}
	}

	protected void layout() {
		this.list.setBounds(getClientArea());
	}

	public Dimension getPreferredSize(int hintWidth, int hintHeight) {
		Dimension pd = this.list.getPreferredSize(hintWidth, hintHeight);
		pd.setWidth(getLeftInset() + pd.getWidth() + getRightInset());
		pd.setHeight(getTopInset() + pd.getHeight() + getBottomInset());

		return pd;
	}

	public PopupMenuDialog getEndPopupMenuDialog() {
		if (_menuDialog != null) {
			return _menuDialog;
		}
		return this;
	}

	protected void setVisibleCore(boolean visible) {
		if (visible == getVisible()) {
			return;
		}
		if (visible) {
			/*
			 * if (this.menu.getMenuItemSize() == 0) { this.menu.fireAction();
			 * return; }
			 */
			Display display = Toolkit.getDisplay();
			int sWidth = display.getScreenWidth();
			int sHeight = display.getScreenHeight();
			Dimension d = getPreferredSize(DEFAULT, DEFAULT);

			int hintWidth = DEFAULT;
			int hintHeight = DEFAULT;
			if (d.getWidth() > sWidth) {
				hintWidth = sWidth;
			}
			if (d.getHeight() > sHeight) {
				hintHeight = sHeight;
			}
			if (hintWidth != DEFAULT || hintHeight != DEFAULT) {
				d = getPreferredSize(hintWidth, hintHeight);
			}

			d.setSize(Math.min(d.getWidth(), display.getScreenWidth()), Math
					.min(d.getHeight(), display.getScreenHeight()));

			int x = (display.getScreenWidth() - d.getWidth()) / 2;
			int y = (display.getScreenHeight() - d.getHeight()) / 2;

			setBounds(x, y, d.getWidth(), d.getHeight());

			// this.menu.addMenuListener(this.menuListener);
			menu._popup = this;
		} else {
			if (_menuDialog != null) {
				_menuDialog.setVisible(false);
			}
			if (_parent != null) {
				_parent._menuDialog = null;
			}
			menu._popup = null;
			// this.menu.removeMenuListener(this.menuListener);
		}
		super.setVisibleCore(visible);

	}

	/*
	 * private MenuListener menuListener = new MenuListener() { public void
	 * intervalAdded(Menu source, MenuItem child, int index) { updateUI(); }
	 * public void intervalRemoved(Menu source, MenuItem child, int index) {
	 * updateUI(); } };
	 */
}