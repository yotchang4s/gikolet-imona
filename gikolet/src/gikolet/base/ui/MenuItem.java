/*
 * 作成日： 2004/08/28
 *
 * TODO この生成されたファイルのテンプレートを変更するには次を参照。
 * ウィンドウ ＞ 設定 ＞ Java ＞ コード・スタイル ＞ コード・テンプレート
 */
package gikolet.base.ui;

import java.util.Vector;

import gikolet.base.ui.events.ActionEvent;
import gikolet.base.ui.events.ActionListener;

/**
 * @author tetsutaro TODO この生成された型コメントのテンプレートを変更するには次を参照。 ウィンドウ ＞ 設定 ＞ Java ＞
 *         コード・スタイル ＞ コード・テンプレート
 */
public class MenuItem {
	private String	label;
	private Menu	parent;

	private Vector	_listeners;

	public MenuItem(String label) {
		if (label == null) {
			label = "";
		}
		this.label = label;
	}

	public String getLabel() {
		return this.label;
	}

	public Menu getParent() {
		return this.parent;
	}

	void setParent(Menu parent) {
		this.parent = parent;
	}

	public void addActionListener(ActionListener listener) {
		if (_listeners == null) {
			_listeners = new Vector();
		}
		_listeners.addElement(listener);
	}

	public void removeActionListener(ActionListener listener) {
		_listeners.removeElement(listener);
	}

	protected void fireAction() {
		if (_listeners == null) {
			return;
		}

		ActionEvent e = null;
		for (int i = 0; i < _listeners.size(); i++) {
			if (e == null) {
				e = new ActionEvent(this, "MenuItemAction");
			}
			((ActionListener) _listeners.elementAt(i)).actionPerformed(e);
		}
	}

	public String toString() {
		return label;
	}
}