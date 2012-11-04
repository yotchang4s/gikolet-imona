/*
 * 作成日： 2004/11/25
 *
 * TODO この生成されたファイルのテンプレートを変更するには次を参照。
 * ウィンドウ ＞ 設定 ＞ Java ＞ コード・スタイル ＞ コード・テンプレート
 */
package gikolet.base.ui.events;

import gikolet.base.util.Event;

/**
 * @author tetsutaro
 *
 * TODO この生成された型コメントのテンプレートを変更するには次を参照。
 * ウィンドウ ＞ 設定 ＞ Java ＞ コード・スタイル ＞ コード・テンプレート
 */
public class ActionEvent extends Event {
	private String command;

	public ActionEvent(Object source, String command) {
		super(source);

		this.command = command;
	}

	public String getActionCommand() {
		return this.command;
	}
}