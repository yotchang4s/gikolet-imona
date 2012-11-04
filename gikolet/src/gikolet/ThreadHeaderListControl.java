/*
 * Created on 2005/01/29 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet;

import gikolet.base.Toolkit;
import gikolet.base.ui.Dimension;
import gikolet.base.ui.Label;
import gikolet.base.ui.List;
import gikolet.base.ui.Menu;
import gikolet.base.ui.MenuItem;
import gikolet.base.ui.Scrollable;
import gikolet.base.ui.TabPage;
import gikolet.base.ui.events.ActionEvent;
import gikolet.base.ui.events.ActionListener;
import gikolet.base.ui.events.KeyEvent;
import gikolet.base.ui.events.KeyEventListener;
import gikolet.imona.Board;
import gikolet.imona.ThreadHeader;
import gikolet.imona.ThreadHeaderList;

import java.io.IOException;

/**
 * @author tetsutaro TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ThreadHeaderListControl extends TabPage {
	private Gikolet				_gikolet;

	private Board				_board;
	private ThreadHeaderList	_threadHeaders;

	private Thread				_thread;

	private List				_list;
	private Label				_infoLabel;

	private int					_start;
	private int					_to;
	private String				_find;

	private MenuItem			_updateMenuItem;
	private MenuItem			_tabMoveMenuItem;
	private MenuItem			_tabCloseMenuItem;

	protected ThreadHeaderListControl(Gikolet gikolet, Board board, String find) {
		super();

		_gikolet = gikolet;
		_board = board;
		_find = find;
		_start = -1;

		_threadHeaders = new ThreadHeaderList();
		_list = new List(Scrollable.SCROLLBAR_AS_NEEDED,
				Scrollable.SCROLLBAR_NEVER);
		_infoLabel = new Label();
		_list.setCirculate(false);

		add(_list);
		add(_infoLabel);

		_list.addKeyEventListener(_gikolet.getCommonListener());

		_list.addKeyEventListener(new KeyEventListener() {
			public void keyAction(KeyEvent e) {
				list_keyAction(e);
			}
		});
		_updateMenuItem = new MenuItem("更新");
		_tabMoveMenuItem = new MenuItem("別のﾀﾌﾞに移動");
		_tabCloseMenuItem = new MenuItem("このﾀﾌﾞを閉じる");

		Menu menu = new Menu("ｽﾚ一覧");

		ActionListener menuAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAction((MenuItem) e.getSource());
			}
		};
		_updateMenuItem.addActionListener(menuAction);
		_tabMoveMenuItem.addActionListener(menuAction);
		_tabCloseMenuItem.addActionListener(menuAction);

		menu.add(_updateMenuItem);
		menu.add(_tabMoveMenuItem);
		menu.add(_tabCloseMenuItem);

		_list.setPopupMenuItem(menu);
	}

	private void menuAction(MenuItem menuItem) {
		if (menuItem == _updateMenuItem) {
			read(false);
		} else if (menuItem == _tabMoveMenuItem) {
			_gikolet.threadHeaderListTabMove();
		} else if (menuItem == _tabCloseMenuItem) {
			_gikolet.threadHeaderListTabClose(this);
		}
	}

	public Board getBoard() {
		return _board;
	}

	private synchronized void list_keyAction(KeyEvent e) {
		int type = e.getKeyActionType();
		int code = e.getKeyCode();

		if (type == KeyEvent.PRESSED) {
			if (code == KeyEvent.KEY_NUM0) {
				_gikolet.threadHeaderListTabClose(this);
				e.consume();

				return;
			} else if (code == KeyEvent.KEY_NUM7) {
				_gikolet.transferThreadHeaderListFocusBackward(this);
				e.consume();
			} else if (code == KeyEvent.KEY_NUM9) {
				_gikolet.transferThreadHeaderListFocusForward(this);
				e.consume();
			}
		}
		if (_list.getListSize() == 0 || isReading()) {
			return;
		}

		if (type != KeyEvent.RELEASED) {
			if (code == KeyEvent.DOWN) {
				if (_list.getSelectedIndex() == _list.getListSize() - 1) {
					if (_to != -1 && _find == null) {
						read(_to + 1, _gikolet.getConfig()
								.getThreadHeaderReadSize(), true, true);
						e.consume();
					}
				}
			} else if (code == KeyEvent.UP) {
				if (_list.getSelectedIndex() == 0) {
					if (_start != -1) {
						read(_start - 1, _gikolet.getConfig()
								.getThreadHeaderReadSize(), false, true);
						e.consume();
					}
				}
			} else if (type == KeyEvent.PRESSED) {
				if (code == KeyEvent.ENTER || code == KeyEvent.KEY_NUM5) {
					if (!isReading()) {
						int index = _list.getSelectedIndex();
						if (index != -1) {
							index += _start;
							ThreadHeader th = _threadHeaders
									.getThreadHeader(index);
							if (th != null) {
								_gikolet.selected(th);
								e.consume();
							}
						}
					}
				}
			}
		}
	}

	protected void layout() {
		Dimension size = _infoLabel.getPreferredSize(DEFAULT, DEFAULT);
		_list.setBounds(0, 0, getWidth(), getHeight() - size.getHeight());
		_infoLabel
				.setBounds(0, _list.getHeight(), getWidth(), size.getHeight());
	}

	public String getFindString() {
		return _find;
	}

	public boolean isReading() {
		return (_thread != null) ? _thread.isAlive() : false;
	}

	public void read(boolean cache) {
		int start = _start;
		if (start == -1) {
			start = 1;
		}
		read(start, _gikolet.getConfig().getThreadHeaderReadSize(), true, cache);
	}

	private synchronized void readLocal(int start, int readSize, boolean next) {
		int to;

		if (next) {
			to = start + readSize - 1;
			to = Math.min(to, _threadHeaders.getEndIndex(start));
		} else {
			to = start;
			start = to - readSize + 1;
			start = (start < 1) ? 1 : start;
			start = Math.max(start, _threadHeaders.getStartIndex(to));
		}

		setInfo("表示処理開始");

		_list.clear();

		for (int i = start; i <= to; i++) {
			ThreadHeader th = _threadHeaders.getThreadHeader(i);
			_list
					.add(i + ":" + th.getSubject() + " (" + th.getResCount()
							+ ")");
		}
		_list.validateViewContent();
		if (next) {
			_list.setSelectedIndex(0);
			_list.ensureIndexIsVisible(0);
		} else {
			_list.setSelectedIndex(_list.getListSize() - 1);
			_list.ensureIndexIsVisible(_list.getListSize() - 1);
		}
		setInfo("完了");

		_start = start;
		_to = to;
	}

	private synchronized void read(int start, final int readSize,
			final boolean next, final boolean cache) {
		final String find = _find;
		final Board board = _board;
		if (_board == null || start < 1) {
			return;
		}

		final int willStart;
		final int willTo;

		if (next) {
			willStart = start;
			willTo = willStart + readSize - 1;
		} else {
			willTo = start;
			willStart = (willTo - readSize + 1 < 1) ? 1 : willTo - readSize + 1;
		}
		if (!cache || !_threadHeaders.containsIndex(start)) {
			if (isReading()) {
				setInfo("別の通信中(ｷｬｯｼｭ無)");
				return;
			}
			_thread = new Thread(new Runnable() {
				public void run() {
					try {
						setInfo("通信開始");
						final int onlineReadSize;
						if (find == null) {
							onlineReadSize = _gikolet.getReader().onlineUpdate(
									_threadHeaders, board, willStart, willTo);
						} else {
							onlineReadSize = _gikolet.getReader().onlineUpdate(
									_threadHeaders, board, _find);
						}
						setInfo("通信成功");

						Toolkit.getDisplay().invokeAndWait(new Runnable() {
							public void run() {
								readLocal(willStart, onlineReadSize, next);
							}
						});
					} catch (IOException ioe) {
						String message = ioe.getMessage();
						if (message == null || message.equals("")) {
							message = "なんらかのｴﾗｰ";
						}
						setInfo(message);
						return;
					} catch (Exception e) {
						_gikolet.showExceptionDialog(e);
						return;
					}
				}
			});
			_thread.start();
		} else {
			readLocal(start, readSize, next);
		}
	}

	private void setInfo(final String text) {
		if (Toolkit.getDisplay().isEventThread()) {
			_infoLabel.setText(text);
			_infoLabel.paintImmediately();
		} else {
			Toolkit.getDisplay().invokeAndWait(new Runnable() {
				public void run() {
					_infoLabel.setText(text);
					_infoLabel.paintImmediately();
				}
			});
		}
	}
}