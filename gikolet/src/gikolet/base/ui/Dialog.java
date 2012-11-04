/*
 * 作成日： 2004/11/12
 *
 * TODO この生成されたファイルのテンプレートを変更するには次を参照。
 * ウィンドウ ＞ 設定 ＞ Java ＞ コード・スタイル ＞ コード・テンプレート
 */
package gikolet.base.ui;

import gikolet.base.Toolkit;

/**
 * @author tetsutaro TODO この生成された型コメントのテンプレートを変更するには次を参照。 ウィンドウ ＞ 設定 ＞ Java ＞
 *         コード・スタイル ＞ コード・テンプレート
 */
public class Dialog extends RootControl {
	private String	title;
	private boolean	modal;

	public Dialog(boolean modal) {
		this((RootControl) null, modal);

		setVisible(false);
	}

	public Dialog(RootControl root, boolean modal) {
		this(root, null, modal, false);
	}

	public Dialog(String title, boolean modal) {
		this(null, title, modal, false);
	}

	public Dialog(RootControl root, String title, boolean modal) {
		this(root, title, modal, false);
	}
	
	public Dialog(RootControl root, String title, boolean modal, boolean focusable) {
		super(root, focusable);

		this.title = title;
		this.modal = modal;

		int it = 2;
		if (this.title != null && !this.title.equals("")) {
			it += getFont().getHeight() + 2;
		}
		setInsetsCore(it, 2, 2, 2);
	}

	public Dimension getPreferredSize(int hintWidth, int hintHeight) {
		Dimension ps = super.getPreferredSize(hintWidth, hintHeight);
		if (title != null) {
			int tw = getFont().stringWidth(title);
			tw = getLeftInset() + tw + getRightInset();

			ps.setWidth(Math.max(ps.getWidth(), tw));
		}
		return ps;
	}

	public boolean isModal() {
		return this.modal;
	}

	protected void paintControl(Graphics g) {		
		Font font = g.getFont();
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		if (this.title != null) {
			g.drawString(this.title, 2, 2);
			g.drawLine(1, 2 + font.getHeight(), getWidth() - 2, 2 + font
					.getHeight());
		}
	}

	private EventThread	eventThread	= null;

	protected void setVisibleCore(boolean visible) {
		if (visible == getVisible()) {
			return;
		}

		super.setVisibleCore(visible);

		Display display = Toolkit.getDisplay();
		if (visible) {
			display.addDialog(this);
			revalidate();
			repaint();
		} else {
			display.removeDialog(this);
			display.repaint(getX(), getY(), getWidth(), getHeight());
		}

		if (isModal()) {
			if (visible) {
				this.eventThread = new EventThread(display.getEventQueue());
				this.eventThread.start();
				while (this.eventThread.isAlive()) {
					try {
						this.eventThread.join();
					} catch (InterruptedException e) {
					}
				}
			} else {
				if (this.eventThread != null) {
					this.eventThread.dispose();
				}
			}
		}
	}

	public String getTitle() {
		return title;
	}

	protected void fontChanged() {
		int it = 2;
		if (this.title != null) {
			it += getFont().getHeight() + 2;
		}
		setInsets(it, 2, 2, 2);

		super.fontChanged();
	}
}