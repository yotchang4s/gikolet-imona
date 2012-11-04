/*
 * 作成日： 2004/11/28
 *
 * TODO この生成されたファイルのテンプレートを変更するには次を参照。
 * ウィンドウ ＞ 設定 ＞ Java ＞ コード・スタイル ＞ コード・テンプレート
 */
package gikolet.base.ui.events;

import gikolet.base.ui.Control;

/**
 * @author tetsutaro
 *
 * TODO この生成された型コメントのテンプレートを変更するには次を参照。
 * ウィンドウ ＞ 設定 ＞ Java ＞ コード・スタイル ＞ コード・テンプレート
 */
public class InputEvent extends ControlBaseEvent {
	private boolean consume;

	public InputEvent(Control source) {
		super(source);

		this.consume = false;
	}

	public void consume() {
		this.consume = true;
	}

	public boolean isConsumed() {
		return this.consume;
	}
}