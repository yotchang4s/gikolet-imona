/*
 * 作成日： 2004/09/20
 *
 * TODO この生成されたファイルのテンプレートを変更するには次を参照。
 * ウィンドウ ＞ 設定 ＞ Java ＞ コード・スタイル ＞ コード・テンプレート
 */
package gikolet.base.ui;

/**
 * @author tetsutaro TODO この生成された型コメントのテンプレートを変更するには次を参照。 ウィンドウ ＞ 設定 ＞ Java ＞ コード・スタイル ＞
 *         コード・テンプレート
 */
public class TabPage extends Panel {
	public TabPage() {
		this(null);
	}

	public TabPage(String text) {
		super();
		super.setVisibleCore(false);
		setText(text);
	}

	protected void setBoundsCore(int x, int y, int width, int height) {
		super.setBoundsCore(getX(), getY(), getWidth(), getHeight());
	}

	protected void setVisibleCore(boolean visible) {
		super.setVisibleCore(getVisible());
	}

	void setBoundsFromTabControl(int x, int y, int width, int height) {
		super.setBoundsCore(x, y, width, height);
	}

	void setVisibleFromTabControl(boolean visible) {
		super.setVisibleCore(visible);
	}

	protected void textChanged() {
		super.textChanged();

		revalidate();
		repaint();
	}
}