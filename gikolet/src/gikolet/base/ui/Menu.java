/*
 * 作成日： 2004/08/01 TODO この生成されたファイルのテンプレートを変更するには次を参照。 ウィンドウ ＞ 設定 ＞ Java ＞
 * コード・スタイル ＞ コード・テンプレート
 */
package gikolet.base.ui;

import java.util.Vector;

/**
 * @author tetsutaro TODO この生成された型コメントのテンプレートを変更するには次を参照。 ウィンドウ ＞ 設定 ＞ Java ＞
 *         コード・スタイル ＞ コード・テンプレート
 */
public class Menu extends MenuItem {
	private Vector	menus	= new Vector();
	// private Vector _listener;
	PopupMenuDialog	_popup;

	public Menu(String label) {
		super(label);
		this.menus = new Vector();
	}

	public boolean add(MenuItem menuItem) {
		if (menuItem == null || isContain(menuItem)) {
			return false;
		}
		if (menuItem.getParent() != null) {
			menuItem.getParent().remove(menuItem);
		}
		menuItem.setParent(this);
		this.menus.addElement(menuItem);

		// menuItemAdded(menuItem, menus.size() - 1);
		if (_popup != null) {
			_popup.updateUI();
		}
		return true;
	}

	public boolean insert(MenuItem menuItem, int index) {
		if (menuItem == null || isContain(menuItem)) {
			return false;
		}
		if (menuItem.getParent() != null) {
			menuItem.getParent().remove(menuItem);
		}
		menuItem.setParent(this);
		this.menus.insertElementAt(menuItem, index);

		// menuItemAdded(menuItem, index);
		if (_popup != null) {
			_popup.updateUI();
		}

		return true;
	}

	public boolean remove(MenuItem menuItem) {
		if (menuItem == null || !isContain(menuItem)) {
			return false;
		}
		menuItem.setParent(null);
		int index = menus.indexOf(menuItem);
		this.menus.removeElementAt(index);

		// menuItemRemoved(menuItem, index);
		if (_popup != null) {
			_popup.updateUI();
		}
		return true;
	}

	public int getChildIndex(MenuItem menuItem) {
		return this.menus.indexOf(menuItem);
	}

	public boolean isContain(MenuItem menuItem) {
		if (menuItem == null) {
			return false;
		}
		return this.menus.contains(menuItem);
	}

	public MenuItem get(int index) {
		if (index < 0 && index >= getMenuItemSize()) {
			throw new IndexOutOfBoundsException("index is illegal bounds.");
		}
		return (MenuItem) this.menus.elementAt(index);
	}

	public int getMenuItemSize() {
		return this.menus.size();
	}

	/*
	 * public void addMenuListener(MenuListener listener) { if (_listener ==
	 * null) { _listener = new Vector(); } _listener.addElement(listener); }
	 * public void removeMenuListener(MenuListener listener) { if (_listener !=
	 * null) { _listener.removeElement(listener); } } protected void
	 * menuItemAdded(MenuItem menuItem, int index) { if (_listener == null) {
	 * return; } for (int i = 0; i < _listener.size(); i++) { MenuListener
	 * listener = (MenuListener) _listener.elementAt(i);
	 * listener.intervalAdded(this, menuItem, index); } } protected void
	 * menuItemRemoved(MenuItem menuItem, int index) { if (_listener == null) {
	 * return; } for (int i = 0; i < _listener.size(); i++) { MenuListener
	 * listener = (MenuListener) _listener.elementAt(i);
	 * listener.intervalRemoved(this, menuItem, index); } }
	 */
}