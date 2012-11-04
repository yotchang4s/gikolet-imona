/*
 * 作成日： 2004/11/28
 *
 * TODO この生成されたファイルのテンプレートを変更するには次を参照。
 * ウィンドウ ＞ 設定 ＞ Java ＞ コード・スタイル ＞ コード・テンプレート
 */
package gikolet.base.ui.events;

import gikolet.base.ui.Control;
import gikolet.base.util.Event;

/**
 * @author tetsutaro
 *
 * TODO この生成された型コメントのテンプレートを変更するには次を参照。
 * ウィンドウ ＞ 設定 ＞ Java ＞ コード・スタイル ＞ コード・テンプレート
 */
public class ControlBaseEvent extends Event {
	public ControlBaseEvent(Control source) {
		super(source);
	}

	public Control getSourceControl() {
		return (Control) getSource();
	}
}