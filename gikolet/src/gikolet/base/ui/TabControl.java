/*
 * 作成日： 2004/09/20 TODO この生成されたファイルのテンプレートを変更するには次を参照。 ウィンドウ ＞ 設定 ＞ Java ＞
 * コード・スタイル ＞ コード・テンプレート
 */
package gikolet.base.ui;

import gikolet.base.ui.events.KeyEvent;

/**
 * @author tetsutaro TODO この生成された型コメントのテンプレートを変更するには次を参照。 ウィンドウ ＞ 設定 ＞ Java ＞
 *         コード・スタイル ＞ コード・テンプレート
 */
public class TabControl extends ContainerControl {
	private int		_selectedIndex;
	private int		_tabX;

	private boolean	_tabBarShow;

	public TabControl() {
		super(true, false);

		_tabBarShow = true;
		_selectedIndex = -1;
		_tabX = 8;
	}

	public void setTabBarShow(boolean show) {
		_tabBarShow = show;

		revalidate();
		repaint();
	}

	public void addTabPage(TabPage tabPage) {
		if (tabPage == null) {
			throw new NullPointerException("tabPage");
		}
		super.add(tabPage);
		if (getSelectedIndex() == -1) {
			setSelectedIndex(0, false);
		} else {
			tabPage.setVisibleFromTabControl(false);
		}
	}

	public void removeTabPage(TabPage tabPage) {
		if (tabPage == null) {
			throw new NullPointerException("tabPage");
		}
		int selectedIndex = this._selectedIndex;
		int index = indexOf(tabPage);

		super.remove(tabPage);

		if (getTabPageSize() > 0) {
			if (selectedIndex == index) {
				if (selectedIndex > 0) {
					selectedIndex--;
				}
				setSelectedIndex(selectedIndex, true);
			} else if (index < selectedIndex) {
				this._selectedIndex--;
			}
		} else if (selectedIndex == index) {
			tabPage.setVisibleFromTabControl(false);
			this._selectedIndex--;
		}
	}

	protected void layout() {
		Font font = getFont();
		if (font == null) {
			font = Font.getDefaultFont();
		}

		int x = 0;
		int y = 0;
		int width = getWidth();
		int height = getHeight();

		if (_tabBarShow) {
			x = 2;
			y = font.getHeight() + 9;
			width = getWidth() - x * 2;
			height = getHeight() - y - 2;
		} else {
			x = 0;
			y = 0;
			width = getWidth();
			height = getHeight();
		}

		for (int i = 0; i < getTabPageSize(); i++) {
			getTabPageAt(i).setBoundsFromTabControl(x, y, width, height);
		}

		if (getTabPageSize() > 0) {
			showSelectedTab();
		}
	}

	public Dimension getPreferredSize(int hintWidth, int hintHeight) {
		return new Dimension(200, 200); // 適当
	}

	protected void sizeChanged() {
		if (getTabPageSize() > 0) {
			showSelectedTab();
		}
		super.sizeChanged();
	}

	protected void fontChanged() {
		if (getTabPageSize() > 0) {
			showSelectedTab();
		}
		super.fontChanged();
	}

	public boolean setSelected(TabPage tabPage, boolean focus) {
		for (int i = 0; i < getTabPageSize(); i++) {
			if (getTabPageAt(i) == tabPage) {
				setSelectedIndex(i, focus);
				return true;
			}
		}
		return false;
	}

	public void setSelectedIndex(int index, boolean focus) {
		if (0 > index || index > getTabPageSize() - 1) {
			throw new IndexOutOfBoundsException("index is Illegal.");
		}
		TabPage tp = getTabPageAt(index);

		int selectedIndex = getSelectedIndex();
		if (0 <= selectedIndex && selectedIndex < getTabPageSize()) {
			TabPage old = getTabPageAt(selectedIndex);
			old.setVisibleFromTabControl(false);
		}
		this._selectedIndex = index;
		tp.setVisibleFromTabControl(true);

		if (focus) {
			tp.transferFocusForward();
		}
		showSelectedTab();
	}

	private void showSelectedTab() {
		int index = _selectedIndex;
		if (index < 0) {
			return;
		}
		Font font = getFont();
		TabPage tp = getTabPageAt(index);
		if (font != null) {
			int tx = 8;
			for (int i = 0; i < getTabPageSize(); i++) {
				TabPage tabPage = getTabPageAt(i);
				tx += font.stringWidth(tabPage.getText()) + 9;
			}
			// もし画面外だったら
			if (tx > 8 + getWidth() - 16 && _tabX + tx < 8 + getWidth() - 16) {
				_tabX = (tx - (8 + getWidth() - 16));
			}

			int tabX = _tabX;
			for (int i = 0; i < getTabPageSize(); i++) {
				TabPage tabPage = getTabPageAt(i);
				int tw = font.stringWidth(tabPage.getText());
				if (tabPage == tp) {
					int tpw = tw + 12;
					if (8 + getWidth() - 16 < tabX + tpw) {
						_tabX -= ((tabX + tpw) - (8 + getWidth() - 16));
						tabX = (8 + getWidth() - 16) - tpw;
					}
					if (tabX < 8) {
						_tabX += (8 - tabX);
					}
				}
				tabX += tw + 9;
			}

			/*
			 * int tx = 0; for (int i = 0; i < getTabPageSize(); i++) { TabPage
			 * tabPage = getTabPageAt(i); tx +=
			 * font.stringWidth(tabPage.getText()) + 9; } // もし画面にタブがおさまっていたら if
			 * (tx < 8 + getWidth() - 16) { _tabX = 8; } else { int tabX =
			 * _tabX; for (int i = 0; i < getTabPageSize(); i++) { TabPage
			 * tabPage = getTabPageAt(i); int tw =
			 * font.stringWidth(tabPage.getText()); if (tabPage == tp) { int tpw =
			 * tw + 12; if (8 + getWidth() - 16 < tabX + tpw) { _tabX -= ((tabX +
			 * tpw) - (8 + getWidth() - 16)); tabX = (8 + getWidth() - 16) -
			 * tpw; } if (tabX < 8) { _tabX += (8 - tabX); } } tabX += tw + 9; } }
			 */
		}
		repaint();
	} /*
		 * private void showSelectedTab() { int index = _selectedIndex; if
		 * (index < 0) { return; } Font font = getFont(); TabPage tp =
		 * getTabPageAt(index); if (font != null) { int tx = _tabX; for (int i =
		 * 0; i < getTabPageSize(); i++) { TabPage tabPage = getTabPageAt(i); tx +=
		 * font.stringWidth(tabPage.getText()) + 9; } // もしタブがおさまっていたら if (tx <
		 * 8 + getWidth() - 16) { _tabX = (8 + getWidth() - 16) - tx; _tabX = (8 <
		 * _tabX) ? 8 : _tabX; } int tabX = _tabX; for (int i = 0; i <
		 * getTabPageSize(); i++) { TabPage tabPage = getTabPageAt(i); int tw =
		 * font.stringWidth(tabPage.getText()); if (tabPage == tp) { int tpw =
		 * tw + 12; if (8 + getWidth() - 16 < tabX + tpw) { _tabX -= ((tabX +
		 * tpw) - (8 + getWidth() - 16)); tabX = (8 + getWidth() - 16) - tpw; }
		 * if (tabX < 8) { _tabX += (8 - tabX); } } tabX += tw + 9; } }
		 * repaint(); }
		 */

	public int getSelectedIndex() {
		return this._selectedIndex;
	}

	public TabPage getSelectedTabPage() {
		int index = getSelectedIndex();
		if (index != -1) {
			return getTabPageAt(index);
		}
		return null;
	}

	public TabPage getTabPageAt(int index) {
		return (TabPage) getControlAt(index);
	}

	public int getTabPageSize() {
		return getControlCount();
	}

	public void add(Control control) {
		if (control instanceof TabPage) {
			addTabPage((TabPage) control);
		} else {
			if (control == null) {
				throw new NullPointerException("control is null.");
			}
			throw new IllegalArgumentException("control is not TabPage");
		}
	}

	public void remove(Control control) {
		if (control instanceof TabPage) {
			removeTabPage((TabPage) control);
			return;
		} else {
			if (control == null) {
				throw new NullPointerException("control is null.");
			}
			throw new IllegalArgumentException("control is not TabPage");
		}
	}

	private Color	waku		= new Color(0x919B9C);
	private Color	wakuLight	= new Color(0x91A7B4);
	private Color	orange		= new Color(0xE68B2C);
	private Color	yellow		= new Color(0xFFC73C);

	protected void paintChildren(Graphics g) {
		TabPage tp = getSelectedTabPage();

		if (tp.getVisible() && g.getClipX() < tp.getX() + tp.getWidth()
				&& g.getClipY() < tp.getY() + tp.getHeight()
				&& tp.getX() < g.getClipX() + g.getClipWidth()
				&& tp.getY() < g.getClipY() + g.getClipHeight()) {

			Graphics cg = g.createGraphics(tp.getX(), tp.getY(), tp.getWidth(), tp.getHeight());
			cg.setBackColor(tp.getBackColor());
			cg.setColor(tp.getForeColor());
			cg.setFont(tp.getFont());
			tp.paint(cg);
		}
	}

	protected void paintControl(Graphics g) {
		if (!_tabBarShow) {
			return;
		}

		int width = getWidth();
		int height = getHeight();
		Font font = g.getFont();
		int tx = 2;
		int ty = font.getHeight() + 9;
		int twidth = width - tx * 2;
		int theight = height - ty - 2;

		if (tx <= g.getClipX() && ty <= g.getClipY()
				&& g.getClipX() + g.getClipWidth() <= tx + twidth
				&& g.getClipY() + g.getClipHeight() <= ty + theight) {
			return;
		}

		int x = _tabX;
		int y = 1;

		int selectedIndex = getSelectedIndex();

		// セレクトタブのwidthは6 + textWidth + 6
		for (int i = 0; i < getTabPageSize(); i++) {
			TabPage tp = getTabPageAt(i);
			String text = tp.getText();
			int textWidth = font.stringWidth(text);
			int textHeight = font.getHeight();

			if (i == selectedIndex) {
				g.setColor(Color.WHITE);
				g.fillRect(x + 1, y + 1, textWidth + 10, textHeight + 6);

				g.setColor(this.waku);
				g.drawLine(x, y + 2, x, y + 4 + textHeight + 1);
				g.drawLine(x + textWidth + 11, y + 2, x + textWidth + 11, y + 4 + textHeight + 1);

				g.setColor(this.yellow);
				g.drawLine(x + 2, y + 1, x + textWidth + 9, y + 1);
				g.drawLine(x + 1, y + 2, x + textWidth + 10, y + 2);
				g.setColor(this.orange);
				g.drawLine(x + 2, y, x + textWidth + 9, y);
				g.fillRect(x + 1, y + 1, 1, 1);
				g.fillRect(x + textWidth + 10, y + 1, 1, 1);

				g.setColor(this.waku);
				g.drawLine(0, y + textHeight + 6, x, y + textHeight + 6);
				g.drawLine(x + textWidth + 11, y + textHeight + 6, width - 1, y + textHeight + 6);
				g.drawLine(0, y + textHeight + 7, 0, height - 2);
				g.drawLine(width - 1, y + textHeight + 7, getWidth() - 1, height - 2);
				g.drawLine(0, height - 1, width - 1, height - 1);

				if (getFocused()) {
					g.setColor(Color.BLACK);
					g.setStrokeStyle(Graphics.DOTTED);
					g.drawRect(x + 5, y + 3, textWidth + 1, textHeight + 1);
					g.setStrokeStyle(Graphics.SOLID);
				}
				// g.setColor(Color.BLUE); g.fillRect(x + 6, y + 4, textWidth,
				// textHeight);

				g.setColor(Color.BLACK);
				g.drawString(text, x + 6, y + 4);
			} else {
				g.setStrokeStyle(Graphics.DOTTED);
				g.setColor(Color.WHITE);
				g.fillRect(x + 3, y + 4, textWidth + 6, textHeight + 2);

				g.setColor(this.wakuLight);
				if (i - 1 != selectedIndex) {
					g.drawLine(x + 2, y + 5, x + 2, y + 5 + textHeight);
				}
				g.fillRect(x + 3, y + 4, 1, 1);
				g.fillRect(x + textWidth + 8, y + 4, 1, 1);
				g.drawLine(x + 4, y + 3, x + textWidth + 7, y + 3);

				if (i + 1 != selectedIndex) {
					g.drawLine(x + textWidth + 9, y + 5, x + textWidth + 9, y + 5 + textHeight);
				}

				// g.setColor(Color.RED); g.fillRect(x + 6, y + 6, textWidth,
				// textHeight);

				g.setColor(Color.BLACK);
				g.drawString(text, x + 6, y + 6);

				g.setStrokeStyle(Graphics.SOLID);
			}

			x += textWidth + 9;
		}
	}

	protected void keyAction(KeyEvent event) {
		if (getSelectedIndex() == -1) {
			return;
		}
		int type = event.getKeyActionType();
		int code = event.getKeyCode();
		if (type == KeyEvent.PRESSED) {
			if (code == KeyEvent.DOWN || code == KeyEvent.RIGHT) {
				int selectedIndex = getSelectedIndex() + 1;
				if (selectedIndex > getTabPageSize() - 1) {
					selectedIndex = 0;
				}
				setSelectedIndex(selectedIndex, false);
			} else if (code == KeyEvent.UP || code == KeyEvent.LEFT) {
				int selectedIndex = getSelectedIndex() - 1;
				if (selectedIndex < 0) {
					selectedIndex = getTabPageSize() - 1;
				}
				setSelectedIndex(selectedIndex, false);
			} else if (code == KeyEvent.ENTER) {
				TabPage tp = getSelectedTabPage();
				tp.transferFocusForward();
			}
		}
		super.keyAction(event);
	}
}