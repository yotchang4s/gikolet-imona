/*
 * Created on 2005/01/30 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet;

import gikolet.base.Toolkit;
import gikolet.base.ui.CancelException;
import gikolet.base.ui.Color;
import gikolet.base.ui.OptionPane;
import gikolet.base.ui.Dimension;
import gikolet.base.ui.Display;
import gikolet.base.ui.Label;
import gikolet.base.ui.Menu;
import gikolet.base.ui.MenuItem;
import gikolet.base.ui.TabPage;
import gikolet.base.ui.events.ActionEvent;
import gikolet.base.ui.events.ActionListener;
import gikolet.base.ui.events.KeyEvent;
import gikolet.base.ui.events.KeyEventListener;
import gikolet.imona.Res;
import gikolet.imona.ResList;
import gikolet.imona.ThreadHeader;

import java.io.IOException;
import java.util.Stack;

/**
 * @author tetsutaro TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ResListControl extends TabPage {
	private Gikolet			_gikolet;
	private Thread			_thread;
	private Thread			_newReadThread;
	private Stack			_innerLinkStack;

	private ResView			_view;
	private Label			_infoLabel;
	private Label			_prevCountInfoLabel;
	private Label			_nextCacheInfoLabel;
	private Label			_prevCacheInfoLabel;

	private ThreadHeader	_threadHeader;
	private ResList			_reses;

	// 現在表示している範囲
	private int				_readMaxIndex;
	private int				_readIndex;

	private boolean			_inOnlineReadJump;			// 通信完了時の次レスへジャンプの許可
	private boolean			_readJump;					// 通信中のレス移動の許可

	private Menu			_menu;
	private MenuItem		_prevMenuItem;
	private MenuItem		_aaMondeMenuItem;
	private MenuItem		_tabMoveMenuItem;
	private MenuItem		_tabCloseMenuItem;
	private MenuItem		_resJumpMenuItem;
	private MenuItem		_readMaxIndexMenuItem;
	private MenuItem		_textBoxShowMenuItem;
	private MenuItem		_resWriteMenuItem;
	private MenuItem		_resWriteAndExitMenuItem;
	private MenuItem		_addBookmarkMenuItem;

	private ActionListener	_menuAction;

	public ResListControl(Gikolet gikolet, Bookmark bookmark) {
		this(gikolet, (ThreadHeader) bookmark);

		if (bookmark != null) {
			_readMaxIndex = bookmark.getReadMaxResIndex();
		}
	}

	public ResListControl(Gikolet gikolet, ThreadHeader threadHeader) {
		super();

		_gikolet = gikolet;
		_threadHeader = threadHeader;
		_readIndex = -1;
		_readMaxIndex = -1;

		_reses = new ResList();
		_innerLinkStack = new Stack();

		_view = new ResView();
		_infoLabel = new Label();
		_nextCacheInfoLabel = new Label(">>");
		_prevCacheInfoLabel = new Label("<<");
		_prevCountInfoLabel = new Label();

		_menu = new Menu("ｽﾚﾒﾆｭｰ");
		_prevMenuItem = new MenuItem("戻る");
		_tabMoveMenuItem = new MenuItem("別のﾀﾌﾞに移動");
		_tabCloseMenuItem = new MenuItem("このﾀﾌﾞを閉じる");
		_readMaxIndexMenuItem = new Menu("");
		_resJumpMenuItem = new MenuItem("ﾚｽ番号指定");
		_resWriteMenuItem = new MenuItem("書込");
		_resWriteAndExitMenuItem = new MenuItem("書込＆終了");
		_textBoxShowMenuItem = new MenuItem("ﾃｷｽﾄﾎﾞｯｸｽ");
		_aaMondeMenuItem = new MenuItem("AAﾓｰﾄﾞ");
		_addBookmarkMenuItem = new MenuItem("お気に入りに登録");

		if (_gikolet.getConfig().getResShowMethod() == Config.RES_SHOW_METHOD_BODY_PRIORITY) {
			_view.setResShowMethod(ResView.RES_SHOW_METHOD_BODY_PRIORITY);
		}

		_view.addKeyEventListener(_gikolet.getCommonListener());
		_view.addKeyEventListener(new KeyEventListener() {
			public void keyAction(KeyEvent e) {
				view_keyAction(e);
			}
		});

		_menuAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menu_actionPerformed(e);
			}
		};

		_prevMenuItem.addActionListener(_menuAction);

		_menu.addActionListener(_menuAction);
		_tabMoveMenuItem.addActionListener(_menuAction);
		_tabCloseMenuItem.addActionListener(_menuAction);
		_aaMondeMenuItem.addActionListener(_menuAction);
		_resJumpMenuItem.addActionListener(_menuAction);
		_resWriteMenuItem.addActionListener(_menuAction);
		_resWriteAndExitMenuItem.addActionListener(_menuAction);
		_textBoxShowMenuItem.addActionListener(_menuAction);
		_addBookmarkMenuItem.addActionListener(_menuAction);

		_menu.add(_tabMoveMenuItem);
		_menu.add(_tabCloseMenuItem);
		_menu.add(_aaMondeMenuItem);
		_menu.add(_resJumpMenuItem);
		_menu.add(_resWriteAndExitMenuItem);
		_menu.add(_textBoxShowMenuItem);
		_menu.add(_addBookmarkMenuItem);

		if (_threadHeader != null) {
			_view.setPopupMenuItem(_menu);
		}
		setInnerLinkStackInfo();

		add(_view);
		add(_infoLabel);
		add(_prevCountInfoLabel);
		add(_prevCacheInfoLabel);
		add(_nextCacheInfoLabel);
	}

	protected void setResShowMethod(int method) {
		if (method == Config.RES_SHOW_METHOD_2CH) {
			method = ResView.RES_SHOW_METHOD_2CH;
		} else if (method == Config.RES_SHOW_METHOD_BODY_PRIORITY) {
			method = ResView.RES_SHOW_METHOD_BODY_PRIORITY;
		} else {
			System.out.println("予期しないレイアウトメソッド");
			method = ResView.RES_SHOW_METHOD_2CH;
		}
		_view.setResShowMethod(method);
	}

	protected void fontChanged() {
		super.fontChanged();

		for (int i = 0; i < _innerLinkStack.size(); i++) {
			int[] il = (int[]) _innerLinkStack.elementAt(i);
			il[1] = il[2] = 0;
			il[3] = -1;
			il[4] = (isAAMode()) ? 1 : 0;
		}
	}

	public void setScrollWeight(int weight) {
		// _scrollPane.setScrollWeight(weight);
		_view.setScrollWeight(weight);
	}

	private void menu_actionPerformed(ActionEvent e) {
		MenuItem ami = (MenuItem) e.getSource();
		if (ami == _menu) {
			int weight = 0;

			if (!_innerLinkStack.isEmpty() && !_menu.isContain(_prevMenuItem)) {
				int[] il = (int[]) _innerLinkStack.peek();
				_prevMenuItem = new MenuItem(">>" + il[0] + "へ戻る");
				_prevMenuItem.addActionListener(_menuAction);
				_menu.insert(_prevMenuItem, 0);

				weight++;
			}
			_menu.remove(_readMaxIndexMenuItem);
			_readMaxIndexMenuItem = new MenuItem("未読>>"
					+ ((_readMaxIndex == -1) ? 1 : _readMaxIndex + 1) + "から読む");
			_readMaxIndexMenuItem.addActionListener(_menuAction);
			_menu.insert(_readMaxIndexMenuItem, 4 + weight);

			// 0:0 nothing
			// 1:0 remove
			// 0:1 add
			// 1:1 nothing
			boolean b1 = _menu.isContain(_resWriteMenuItem);
			boolean b2 = _gikolet.getGikoletExtensions().isResWritable();
			if (b1 ^ b2) {
				if (b1) {
					_menu.remove(_resWriteMenuItem);
				} else if (b2) {
					_menu.insert(_resWriteMenuItem, 5 + (weight++));
				}
			} else if (b1) {
				weight++;
			}
		} else if (ami == _prevMenuItem) {
			int[] il = (int[]) _innerLinkStack.pop();
			setInnerLinkStackInfo();
			_menu.remove(_prevMenuItem);

			read(il[0], 1, true);
			_view.setViewContentPosition(il[1], il[2]);
			_view.setSelectedIndex(il[3]);
			// _scrollPane.setViewPosition(il[1], il[2]);
		} else if (ami == _aaMondeMenuItem) {
			setAAMode(!isAAMode());
		} else if (ami == _tabCloseMenuItem) {
			_gikolet.resListTabClose(this);
		} else if (ami == _readMaxIndexMenuItem) {
			int i = (_readMaxIndex == -1) ? 1 : _readMaxIndex + 1;
			_readJump = false;
			read(i, _gikolet.getConfig().getResReadSize(), true);
		} else if (ami == _resJumpMenuItem) {
			try {
				int num = OptionPane.showInputDialog(this, "ﾚｽ番号指定", 1, 1000,
						(_readIndex == -1) ? 1 : _readIndex);
				_readJump = false;
				read(num, 1, true);
			} catch (CancelException ce) {
			}
		} else if (ami == _resWriteMenuItem) {
			_gikolet.resWrite(_threadHeader, true);
		} else if (ami == _resWriteAndExitMenuItem) {
			_gikolet.resWrite(_threadHeader, false);
		} else if (ami == _textBoxShowMenuItem) {
			Display display = Toolkit.getDisplay();
			if (display != null) {
				Res res = _reses.get(_readIndex);
				if (res != null) {
					String resText = res.getNo() + "/" + res.getThreadHeader().getResCount() + ":"
							+ res.getName();
					if (res.getMailAddress() != null && !res.getMailAddress().equals("")) {
						resText += "[" + res.getMailAddress() + "]";
					}
					resText += '\r' + res.getDate() + " ID:" + res.getID() + '\r' + res.getBody();
					resText = resText.replace('\r', '\n');

					try {
						display.imeOn(resText, Display.ANY);
					} catch (CancelException ce) {
					}
				}
			}
		} else if (ami == _addBookmarkMenuItem) {
			_gikolet.addBookmark(_threadHeader, _readIndex, _readMaxIndex);
		} else if (ami == _tabMoveMenuItem) {
			_gikolet.resListTabMove();
		}
	}

	public boolean isAAMode() {
		return _view.isAAMode();
	}

	public void setAAMode(boolean aaMode) {
		if (aaMode == _view.isAAMode()) {
			return;
		}
		if (_view.isAAMode()) {
			_view.setAAMode(false);
			// _scrollPane
			// .setHorizontalScrollBarPolicy(ScrollPane.SCROLLBAR_NEVER);
		} else {
			_view.setAAMode(true);
			// _scrollPane
			// .setHorizontalScrollBarPolicy(ScrollPane.SCROLLBAR_AS_NEEDED);
		}
		int nowAAMode = (isAAMode()) ? 1 : 0;
		for (int i = 0; i < _innerLinkStack.size(); i++) {
			int[] il = (int[]) _innerLinkStack.elementAt(i);
			if (nowAAMode != il[4]) {
				il[1] = il[2] = 0;
				il[3] = -1;
				il[4] = nowAAMode;
			}
		}
	}

	public ThreadHeader getViewThreadHeader() {
		return _threadHeader;
	}

	public Res getViewRes() {
		if (_readIndex >= 1) {
			return _reses.get(_readIndex);
		}
		return null;
	}

	private void setNextAndPrevResInfo() {
		// Next
		Color fcolor1;
		Color bcolor1;
		if (_reses.containsIndex(_readIndex + 1)) {
			bcolor1 = null;
			fcolor1 = Color.BLACK;
		} else {
			bcolor1 = Color.RED;
			fcolor1 = Color.WHITE;
		}

		// Prev
		Color fcolor2;
		Color bcolor2;
		if (_readIndex == 1) {
			bcolor2 = null;
			fcolor2 = getBackColor();
		} else if (_reses.containsIndex(_readIndex - 1)) {
			bcolor2 = null;
			fcolor2 = Color.BLACK;
		} else {
			bcolor2 = Color.RED;
			fcolor2 = Color.WHITE;
		}

		_nextCacheInfoLabel.setBackColor(bcolor1);
		_nextCacheInfoLabel.setForeColor(fcolor1);

		_prevCacheInfoLabel.setBackColor(bcolor2);
		_prevCacheInfoLabel.setForeColor(fcolor2);
	}

	private void setInnerLinkStackInfo() {
		_prevCountInfoLabel.setText("L:" + _innerLinkStack.size());
	}

	private void setInfo(final String text) {
		if (Toolkit.getDisplay().isEventThread()) {
			_infoLabel.setText(text);
			_infoLabel.repaint();
		} else {
			Toolkit.getDisplay().invokeAndWait(new Runnable() {
				public void run() {
					_infoLabel.setText(text);
					_infoLabel.repaint();
				}
			});
		}
	}

	private synchronized void view_keyAction(KeyEvent e) {
		int type = e.getKeyActionType();
		int code = e.getKeyCode();

		if (type == KeyEvent.PRESSED) {
			if (code == KeyEvent.KEY_NUM0) {
				_gikolet.resListTabClose(this);
				e.consume();
				return;
			} else if (code == KeyEvent.KEY_NUM7) {
				_gikolet.transferResListFocusBackward(this);
				e.consume();
				return;
			} else if (code == KeyEvent.KEY_NUM9) {
				_gikolet.transferResListFocusForward(this);
				e.consume();
				return;
			}
		}

		if (!_readJump || _threadHeader == null || _readIndex == -1 || _view.getRes() == null) {
			return;
		}

		/*
		 * if (!_readJump) { switch (code) { case KeyEvent.KEY_NUM1: case
		 * KeyEvent.KEY_NUM3: case KeyEvent.KEY_NUM5: case KeyEvent.CLEAR:
		 * return; default: e.consume(); return; } }
		 */

		if (type != KeyEvent.RELEASED) {
			int readCount = _gikolet.getConfig().getResReadSize();
			if ((!_view.isAAMode() && code == KeyEvent.RIGHT) || code == KeyEvent.KEY_NUM6) {
				int readIndex = _readIndex + 1;
				if (readIndex >= 1) {
					read(readIndex, readCount, true);
					e.consume();
				}
			} else if ((!_view.isAAMode() && code == KeyEvent.LEFT) || code == KeyEvent.KEY_NUM4) {
				int readIndex = _readIndex - 1;
				if (readIndex >= 1) {
					read(readIndex, readCount, false);
					e.consume();
				}
			} else if (type == KeyEvent.PRESSED) {
				if (code == KeyEvent.UP || code == KeyEvent.KEY_NUM2) {
					if (_gikolet.getConfig().getResMoveMethod() == Config.RES_MOVE_METHOD_UP_DONW_LEFT_RIGHT
							&& _view.isVScrollEnd(false) && !_view.isNext(false)) {
						int readIndex = _readIndex - 1;
						if (readIndex >= 1) {
							read(readIndex, readCount, false, false, false);
							e.consume();
						}
					}
				} else if (code == KeyEvent.DOWN || code == KeyEvent.KEY_NUM8) {
					if (_gikolet.getConfig().getResMoveMethod() == Config.RES_MOVE_METHOD_UP_DONW_LEFT_RIGHT
							&& _view.isVScrollEnd(true) && !_view.isNext(true)) {
						int readIndex = _readIndex + 1;
						if (readIndex >= 1) {
							read(readIndex, readCount, true, true, false);
							e.consume();
						}
					}
				} else if (code == KeyEvent.KEY_STAR) {
					setAAMode(!isAAMode());
					e.consume();
				} else if (code == KeyEvent.ENTER || code == KeyEvent.KEY_NUM5) {
					if (!isNewReading()) {
						Res.Link link = _view.getViewSelected();
						if (link != null) {
							if (link.getType() == Res.URILink.URI_LINK) {
								String uri = ((Res.URILink) link).getURI();
								if (uri.startsWith("http://")
										&& (uri.indexOf(".2ch.net") != -1 || uri
												.indexOf(".bbspink.com") != -1)) {
									try {
										_gikolet.onlineUpdate(uri);
									} catch (IOException ioe) {
										setInfo(ioe.getMessage());
									}
								} else {
									try {
										uri = Toolkit.getDisplay()
												.imeOn("URIへ飛ぶ", uri, Display.ANY);
										Toolkit.getToolkit().openBrowser(uri);
									} catch (Exception ex) {
									}
								}
							} else if (link.getType() == Res.InnerLink.INNER_LINK) {
								Res.InnerLink iLink = (Res.InnerLink) link;
								int start = iLink.getStart();
								int count = iLink.getTo() - start + 1;

								read(start, count, true, true);
							}
							e.consume();
						}
					}
				}
			}
		}
	}

	protected void layout() {
		Dimension infoLabelSize = _infoLabel.getPreferredSize(DEFAULT, DEFAULT);
		Dimension prevCountLableSize = _prevCountInfoLabel.getPreferredSize(DEFAULT, DEFAULT);
		Dimension prevLableSize = _prevCacheInfoLabel.getPreferredSize(DEFAULT, DEFAULT);
		Dimension nextLabelSize = _nextCacheInfoLabel.getPreferredSize(DEFAULT, DEFAULT);

		int infoHeight = 0;
		infoHeight = Math.max(infoHeight, infoLabelSize.getHeight());
		infoHeight = Math.max(infoHeight, prevCountLableSize.getHeight());
		infoHeight = Math.max(infoHeight, prevLableSize.getHeight());
		infoHeight = Math.max(infoHeight, nextLabelSize.getHeight());

		int hh = getHeight() - infoHeight;
		int ww = getWidth();

		_view.setBounds(0, 0, ww, hh);

		ww -= nextLabelSize.getWidth();
		_nextCacheInfoLabel.setBounds(ww, hh, nextLabelSize.getWidth(), infoHeight);
		ww -= prevLableSize.getWidth() + 3;
		_prevCacheInfoLabel.setBounds(ww, hh, prevLableSize.getWidth(), infoHeight);
		ww -= prevCountLableSize.getWidth() + 3;
		_prevCountInfoLabel.setBounds(ww, hh, prevCountLableSize.getWidth(), infoHeight);

		_infoLabel.setBounds(0, hh, ww, infoHeight);
	}

	public boolean isReading() {
		return (_thread != null) ? _thread.isAlive() : false;
	}

	private boolean isNewReading() {
		return (_newReadThread != null) ? _newReadThread.isAlive() : false;
	}

	public synchronized void readNew() {
		final ThreadHeader threadHeader = _threadHeader;
		if (threadHeader == null) {
			return;
		}
		if (isReading()) {
			setInfo("別の通信中");
			return;
		}

		_readJump = false;

		_newReadThread = _thread = new Thread(new Runnable() {
			public void run() {
				int start;
				try {
					setInfo("通信開始");
					start = _gikolet.getReader().onlineUpdate(_reses, threadHeader,
							_gikolet.getConfig().getNewResReadSize()).start;
					setInfo("通信成功");
				} catch (Exception e) {
					if (e instanceof IOException) {
						String message = e.getMessage();
						if (message == null || message.equals("")) {
							message = "なんらかのｴﾗｰ";
						}
						setInfo(message);
					} else {
						_gikolet.showExceptionDialog(e);
					}

					synchronized (this) {
						_readJump = true;
					}
					return;
				}
				synchronized (this) {
					setInfo("表示処理開始");
					readLocal(start, true, false);
					_readJump = true;

					setNextAndPrevResInfo();
				}
				setInfo("完了");
			}
		});
		_thread.start();
	}

	private void readLocal(final int readIndex, final boolean def, final boolean jump) {
		if (Toolkit.getDisplay().isEventThread()) {
			readLocalCore(readIndex, def, jump);
		} else {
			Toolkit.getDisplay().invokeAndWait(new Runnable() {
				public void run() {
					readLocalCore(readIndex, def, jump);
				}
			});
		}
	}

	private void readLocalCore(int readIndex, boolean def, boolean jump) {
		Res res = _reses.get(readIndex);
		if (res != null) {
			if (jump) {
				_innerLinkStack.push(new int[] { _readIndex, _view.getViewContentX(),
						_view.getViewContentY(), _view.getSelectedIndex(), (isAAMode()) ? 1 : 0 });
				setInnerLinkStackInfo();
				jump = false;
			}
			_view.setRes(_threadHeader, res);
			if (def) {
				_view.setSelectedIndex(-1);
				_view.setViewContentPosition(0, 0);
			} else {
				int si = _view.getLinkSize();
				si = (si == 0) ? -1 : si;
				_view.setSelectedIndex(si);

				_view.setViewContentPosition(0, _view.getViewContentHeight()
						- _view.getViewAreaHeight());
			}
			_readIndex = readIndex;
			if (readIndex > _readMaxIndex) {
				_readMaxIndex = readIndex;
			}
			setNextAndPrevResInfo();

			_gikolet.selectedRes(_threadHeader, readIndex);
		}
	}

	public int getReadIndex() {
		return _readIndex;
	}

	public int getReadMaxIndex() {
		return _readMaxIndex;
	}

	public synchronized void read(int start) {
		if (_threadHeader == null) {
			throw new NullPointerException("threadHeader");
		}
		if (isReading()) {
			setInfo("別ﾚｽ読込中");
			return;
		}
		_readJump = false;

		read(start, _gikolet.getConfig().getResReadSize(), true);
	}

	private synchronized void read(int readIndex, int count, boolean next) {
		read(readIndex, count, next, true, false);
	}

	private synchronized void read(int readIndex, int count, boolean next, boolean jump) {
		read(readIndex, count, next, true, jump);
	}

	private synchronized void read(final int readIndex, final int count, final boolean next,
			final boolean def, final boolean jump) {
		final ThreadHeader threadHeader = _threadHeader;
		if (threadHeader == null) {
			return;
		}
		if (!_reses.containsIndex(readIndex)) {
			if (isReading()) {
				setInfo("別の通信中(ｷｬｯｼｭ無)");
				return;
			}
			_inOnlineReadJump = true;
			_thread = new Thread(new Runnable() {
				public void run() {
					int start;
					int to;

					if (next) {
						start = readIndex;
						to = start + count - 1;
					} else {
						to = readIndex;
						start = Math.max(1, to - count + 1);
					}
					try {
						setInfo("通信開始");
						_gikolet.getReader().onlineUpdate(_reses, threadHeader, start, to);
						setInfo("通信成功");
					} catch (Exception e) {
						if (e instanceof IOException) {
							String message = e.getMessage();
							if (message == null || message.equals("")) {
								message = "なんらかのｴﾗｰ";
							}
							setInfo(message);
						} else {
							_gikolet.showExceptionDialog(e);
						}

						synchronized (this) {
							_readJump = true;
						}
						return;
					}

					synchronized (this) {
						_readJump = true;

						if (_inOnlineReadJump) {
							setInfo("表示処理開始");
							readLocal(readIndex, def, jump);
							_inOnlineReadJump = false;
						}
						setNextAndPrevResInfo();
					}
					setInfo("完了");
				}
			});
			_thread.start();
		} else {
			_inOnlineReadJump = false;
			_readJump = true;
			readLocal(readIndex, def, jump);
		}
	}
}