/*
 * 作成日： 2004/11/09 TODO この生成されたファイルのテンプレートを変更するには次を参照。 ウィンドウ ＞ 設定 ＞ Java ＞
 * コード・スタイル ＞ コード・テンプレート
 */
package gikolet;

import gikolet.base.Toolkit;
import gikolet.base.io.Scratchpad;
import gikolet.base.ui.CancelException;
import gikolet.base.ui.Color;
import gikolet.base.ui.Display;
import gikolet.base.ui.Font;
import gikolet.base.ui.Menu;
import gikolet.base.ui.MenuItem;
import gikolet.base.ui.OptionPane;
import gikolet.base.ui.Shell;
import gikolet.base.ui.TabControl;
import gikolet.base.ui.TabPage;
import gikolet.base.ui.events.ActionEvent;
import gikolet.base.ui.events.ActionListener;
import gikolet.base.ui.events.ApplicationEventListener;
import gikolet.base.ui.events.KeyEvent;
import gikolet.base.ui.events.KeyEventListener;
import gikolet.base.util.IllegalStateException;
import gikolet.imona.BBSData;
import gikolet.imona.Board;
import gikolet.imona.Range;
import gikolet.imona.Reader;
import gikolet.imona.Res;
import gikolet.imona.ThreadHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

/**
 * @author tetsutaro TODO この生成された型コメントのテンプレートを変更するには次を参照。 ウィンドウ ＞ 設定 ＞ Java ＞
 *         コード・スタイル ＞ コード・テンプレート
 */
public class Gikolet {
	private final static byte[]	_identifier	= new byte[] { 'G', 'i', 'k', 'o', 'l', 'e', 't' };
	private String[]			_defaultSevers;
	private GikoletExtensions	_gikoletExtensions;

	private Reader				_reader;
	private Shell				_shell;

	private TabControl			_rootTabCtrl;
	private TabPage				_rootTabPg1;
	private TabPage				_rootTabPg2;
	private TabPage				_rootTabPg3;

	private TabControl			_threadListTabCtrl;
	private TabControl			_threadTabCtrl;
	private TabPage				_emptyThreadTabPg;

	private BoardTreeControl	_boardTreeCtrl;
	private BookmarkListControl	_bookmarkListCtrl;
	private ConfigDialog		_configDialog;

	private MenuItem			_boardTreeShowMenuItem;
	private MenuItem			_threadHeaderListMoveMenuItem;
	private MenuItem			_resListTabMoveMenuItem;
	private MenuItem			_configShowMenuItem;
	private MenuItem			_exitMenuItem;
	private CommonListener		_commonListener;

	ActionListener				_menuAction	= new MenuActionListener();

	class MenuActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			menuAction(e);
		}
	};

	public Gikolet(String[] severs, GikoletExtensions gikoletExtensions) {
		_defaultSevers = severs;
		_gikoletExtensions = gikoletExtensions;

		try {
			_reader = new Reader(this);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e.getMessage());
		}

		_commonListener = new CommonListener();

		// オブジェクトの作成
		_shell = new Shell("Gikolet");

		_rootTabCtrl = new TabControl();
		_rootTabPg1 = new TabPage("板一覧");
		_boardTreeCtrl = new BoardTreeControl(this);

		_rootTabPg2 = new TabPage("ｽﾚｯﾄﾞ一覧");
		_threadListTabCtrl = new TabControl();
		_bookmarkListCtrl = new BookmarkListControl(this);

		_rootTabPg3 = new TabPage("ｽﾚｯﾄﾞ");
		_threadTabCtrl = new TabControl();
		_emptyThreadTabPg = new TabPage();

		EmptyControl emptyControl = new EmptyControl(true);

		_configDialog = new ConfigDialog(this, _shell, _defaultSevers);

		// メニュー
		_boardTreeShowMenuItem = new MenuItem("板一覧表示");
		_threadHeaderListMoveMenuItem = new Menu("ｽﾚｯﾄﾞ一覧表示");
		_resListTabMoveMenuItem = new MenuItem("ｽﾚｯﾄﾞ表示");
		_configShowMenuItem = new MenuItem("設定");
		_exitMenuItem = new MenuItem("終了");

		// オブジェクトの設定
		_rootTabCtrl.setFocusable(false);
		_threadListTabCtrl.setFocusable(false);
		_threadTabCtrl.setFocusable(false);
		_bookmarkListCtrl.setText("お気に入り");

		_rootTabPg1.add(_boardTreeCtrl);
		_rootTabPg2.add(_threadListTabCtrl);
		_rootTabPg3.add(_threadTabCtrl);
		_rootTabCtrl.addTabPage(_rootTabPg1);
		_rootTabCtrl.addTabPage(_rootTabPg2);
		_rootTabCtrl.addTabPage(_rootTabPg3);

		_threadListTabCtrl.add(_bookmarkListCtrl);
		_threadTabCtrl.addTabPage(_emptyThreadTabPg);

		_emptyThreadTabPg.add(emptyControl);
		emptyControl.addKeyEventListener(_commonListener);
		emptyControl.setBackColor(Color.WHITE);

		_shell.add(_rootTabCtrl);

		Toolkit.getDisplay().addApplicationEventListener(new ApplicationEventListener() {
			public void exitApplication() {
				try {
					save();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			public void startApplication() {
			}

			public void pauseApplication() {
			}
		});

		Menu menu = new Menu("ﾒﾆｭｰ");

		_boardTreeShowMenuItem.addActionListener(_menuAction);
		_threadHeaderListMoveMenuItem.addActionListener(_menuAction);
		_resListTabMoveMenuItem.addActionListener(_menuAction);
		_configShowMenuItem.addActionListener(_menuAction);
		_exitMenuItem.addActionListener(_menuAction);

		menu.add(_boardTreeShowMenuItem);
		menu.add(_threadHeaderListMoveMenuItem);
		menu.add(_resListTabMoveMenuItem);
		menu.add(_configShowMenuItem);
		menu.add(_exitMenuItem);

		_shell.setMenuItem(menu);

		Toolkit.getDisplay().setShell(_shell);
		try {
			load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public GikoletExtensions getGikoletExtensions() {
		return _gikoletExtensions;
	}

	public Config getConfig() {
		return _configDialog;
	}

	public CommonListener getCommonListener() {
		return _commonListener;
	}

	class CommonListener implements KeyEventListener {
		public void keyAction(KeyEvent e) {
			if (e.getKeyActionType() == KeyEvent.PRESSED) {
				if (e.getKeyCode() == KeyEvent.KEY_NUM3) {
					int i = _rootTabCtrl.getSelectedIndex() + 1;
					if (i > _rootTabCtrl.getTabPageSize() - 1) {
						i = 0;
					}
					_rootTabCtrl.setSelectedIndex(i, true);
					e.consume();
				} else if (e.getKeyCode() == KeyEvent.KEY_NUM1) {
					int i = _rootTabCtrl.getSelectedIndex() - 1;
					if (i < 0) {
						i = _rootTabCtrl.getTabPageSize() - 1;
					}
					_rootTabCtrl.setSelectedIndex(i, true);
					e.consume();
				} else if (e.getKeyCode() == KeyEvent.CLEAR || e.getKeyCode() == KeyEvent.KEY_NUM5) {
					_reader.onlineUpdateCancel();
					e.consume();
				}
			}
		}
	}

	public Reader getReader() {
		return _reader;
	}

	public void selected(Board board) {
		int i = OptionPane.showSelectionDialog(_shell, new String[] { "ｽﾚｯﾄﾞ一覧を読む", "検索する" });
		if (i == -1) {
			return;
		}
		selected(board, i == 1);
	}

	public void selected(Board board, boolean find) {
		String findStr = null;
		if (find) {
			try {
				findStr = Toolkit.getDisplay().imeOn(board.getName() + "を検索", "", Display.ANY);
			} catch (CancelException e) {
				return;
			}
		}
		ThreadHeaderListControl thlc = getThreadHeaderListControl(board, findStr, true);
		if (thlc.isReading()) {
			return;
		}

		_threadListTabCtrl.setSelected(thlc, true);
		_rootTabCtrl.setSelectedIndex(1, true);

		thlc.read(true);
	}

	public void threadHeaderListTabMove() {
		String[] strs = new String[_threadListTabCtrl.getTabPageSize()];
		strs[0] = "お気に入り";
		for (int i = 1; i < strs.length; i++) {
			Board brd = ((ThreadHeaderListControl) _threadListTabCtrl.getTabPageAt(i)).getBoard();
			strs[i] = brd.getName();
		}
		int index = OptionPane.showSelectionDialog(_shell, "ｽﾚｯﾄﾞ一覧表示", strs,
				_threadListTabCtrl.getSelectedIndex());
		if (index != -1) {
			_rootTabCtrl.setSelectedIndex(1, true);
			_threadListTabCtrl.setSelectedIndex(index, true);
		}
	}

	public void threadHeaderListTabClose(ThreadHeaderListControl threadHeaderListControl) {
		_threadListTabCtrl.removeTabPage(threadHeaderListControl);
	}

	private ThreadHeaderListControl getThreadHeaderListControl(Board board, String find,
			boolean create) {

		if (!_threadListTabCtrl.isContain(_emptyThreadTabPg)) {
			for (int i = 1; i < _threadListTabCtrl.getTabPageSize(); i++) {
				ThreadHeaderListControl thlc = (ThreadHeaderListControl) _threadListTabCtrl
						.getTabPageAt(i);
				Board brd = thlc.getBoard();
				String fnd = thlc.getFindString();
				if (board.equals(brd)) {
					if ((find == null && fnd == null) || (find != null && find.equals(fnd))
							|| (fnd != null && fnd.equals(find))) {
						return thlc;
					}
				}
			}
		}

		if (create) {
			if (_threadListTabCtrl.isContain(_emptyThreadTabPg)) {
				_threadListTabCtrl.removeTabPage(_emptyThreadTabPg);
			}
			ThreadHeaderListControl thlc = new ThreadHeaderListControl(this, board, find);

			String tabText = board.getName();
			if (find != null) {
				tabText += "(" + find + ")";
			}
			int tts = getConfig().getThreadTabTextLength();
			if (tts > 0 && tabText.length() > tts) {
				tabText = tabText.substring(0, tts);
			}
			thlc.setText(tabText);
			_threadListTabCtrl.addTabPage(thlc);
			return thlc;
		}
		return null;
	}

	public boolean transferThreadHeaderListFocusForward(TabPage tabPage) {
		int i = _threadListTabCtrl.indexOf(tabPage);
		if (i != -1) {
			i++;
			i = (i > _threadListTabCtrl.getTabPageSize() - 1) ? 0 : i;
			_threadListTabCtrl.setSelectedIndex(i, true);
			return true;
		}
		return false;
	}

	public boolean transferThreadHeaderListFocusBackward(TabPage tabPage) {
		int i = _threadListTabCtrl.indexOf(tabPage);
		if (i != -1) {
			i--;
			i = (i < 0) ? _threadListTabCtrl.getTabPageSize() - 1 : i;
			_threadListTabCtrl.setSelectedIndex(i, true);
		}
		return false;
	}

	public boolean transferResListFocusForward(TabPage tabPage) {
		int i = _threadTabCtrl.indexOf(tabPage);
		if (i != -1) {
			i++;
			i = (i > _threadTabCtrl.getTabPageSize() - 1) ? 0 : i;
			_threadTabCtrl.setSelectedIndex(i, true);
			return true;
		}
		return false;
	}

	public boolean transferResListFocusBackward(TabPage tabPage) {
		int i = _threadTabCtrl.indexOf(tabPage);
		if (i != -1) {
			i--;
			i = (i < 0) ? _threadTabCtrl.getTabPageSize() - 1 : i;
			_threadTabCtrl.setSelectedIndex(i, true);
		}
		return false;
	}

	public void showExceptionDialog(Exception e) {
		System.out.println(e);
		e.printStackTrace();
		OptionPane.showMessageDialog(_shell, e.getClass().getName(), e.getMessage());
	}

	public void selected(ThreadHeader threadHeader) {
		boolean writable = getGikoletExtensions().isResWritable();
		Bookmark bmk = (Bookmark) _bookmarkListCtrl.getBookmark(threadHeader);

		Vector strv = new Vector();
		if (bmk == null) {
			strv.addElement("最初から読む");
		} else {
			strv.addElement("栞>>" + ((bmk.getResNo() == 0) ? 1 : bmk.getResNo()) + "から読む");
			strv.addElement("未読>>" + (bmk.getReadMaxResIndex() + 1) + "から読む");
		}
		strv.addElement("最新" + getConfig().getNewResReadSize() + "件を読む");
		strv.addElement("ﾚｽ番号を指定して読む");
		strv.addElement(threadHeader.getBoard().getName() + "板を読む");
		strv.addElement("書き込み＆終了");

		if (writable) {
			strv.addElement("書き込み");
		}
		if (bmk == null) {
			strv.addElement("お気に入りに追加");
		}

		String[] strs = new String[strv.size()];
		strv.copyInto(strs);
		int i = OptionPane.showSelectionDialog(_shell, strs);
		if (i == -1) {
			return;
		} else if (i == 0) {
			ResListControl rlc = getResListControl(threadHeader, true);
			_threadTabCtrl.setSelected(rlc, true);
			_rootTabCtrl.setSelectedIndex(2, true);

			if (bmk == null) {
				rlc.read(1);
			} else {
				rlc.read((bmk.getResNo() == 0) ? 1 : bmk.getResNo());
			}
		} else {
			i += (bmk == null) ? 1 : 0;

			if (i == 1) {
				ResListControl rlc = getResListControl(threadHeader, true);
				_threadTabCtrl.setSelected(rlc, true);
				_rootTabCtrl.setSelectedIndex(2, true);
				rlc.read(bmk.getReadMaxResIndex() + 1);
			} else if (i == 2) {
				ResListControl rlc = getResListControl(threadHeader, true);
				_threadTabCtrl.setSelected(rlc, true);
				_rootTabCtrl.setSelectedIndex(2, true);
				rlc.readNew();
			} else if (i == 3) {
				try {
					int number = OptionPane.showInputDialog(_shell, "ﾚｽ番号指定", 1, 1000, 1);
					ResListControl rlc = getResListControl(threadHeader, true);
					_threadTabCtrl.setSelected(rlc, true);
					_rootTabCtrl.setSelectedIndex(2, true);
					rlc.read(number);
				} catch (CancelException e) {
				}
			} else if (i == 4) {
				selected(threadHeader.getBoard());
			} else if (i == 5) {
				resWrite(threadHeader, false);
			} else {
				i += writable ? 0 : 1;
				if (i == 6) {
					resWrite(threadHeader, true);
				} else if (i == 7) {
					addBookmark(threadHeader);
				}
			}
		}
	}

	public void resListTabMove() {
		String[] strs = new String[_threadTabCtrl.getTabPageSize()];
		for (int i = 0; i < strs.length; i++) {
			ThreadHeader th = ((ResListControl) _threadTabCtrl.getTabPageAt(i))
					.getViewThreadHeader();
			if (th != null) {
				strs[i] = th.getSubject();
			} else {
				strs[i] = "";
			}
		}
		int index = OptionPane.showSelectionDialog(_shell, "ｽﾚｯﾄﾞ表示", strs, _threadTabCtrl
				.getSelectedIndex());
		if (index != -1) {
			_rootTabCtrl.setSelectedIndex(2, true);
			_threadTabCtrl.setSelectedIndex(index, true);
		}
	}

	public void resListTabClose(ResListControl resListControl) {
		_threadTabCtrl.removeTabPage(resListControl);
		if (_threadTabCtrl.getTabPageSize() == 0) {
			_threadTabCtrl.addTabPage(_emptyThreadTabPg);
		}
	}

	private ResListControl getResListControl(ThreadHeader threadHeader, boolean create) {
		if (!_threadTabCtrl.isContain(_emptyThreadTabPg)) {
			for (int i = 0; i < _threadTabCtrl.getTabPageSize(); i++) {
				ResListControl rlc = (ResListControl) _threadTabCtrl.getTabPageAt(i);
				ThreadHeader th = rlc.getViewThreadHeader();
				if (th != null && threadHeader.equals(th)) {
					return rlc;
				}
			}
		}

		if (create) {
			if (_threadTabCtrl.isContain(_emptyThreadTabPg)) {
				_threadTabCtrl.removeTabPage(_emptyThreadTabPg);
			}
			ResListControl rlc;
			Bookmark b = (Bookmark) _bookmarkListCtrl.getBookmark(threadHeader);
			if (b != null) {
				rlc = new ResListControl(this, (Bookmark) b);
			} else {
				rlc = new ResListControl(this, threadHeader);
			}
			rlc.setScrollWeight(getConfig().getResScrollWeight());
			String tabText = threadHeader.getSubject();
			int tts = getConfig().getThreadTabTextLength();
			if (tts > 0 && tabText.length() > tts) {
				tabText = tabText.substring(0, tts);
			}
			rlc.setText(tabText);
			_threadTabCtrl.addTabPage(rlc);
			return rlc;
		}
		return null;
	}

	public void onlineUpdate(String uri) throws IOException {
		Range range = new Range();

		BBSData bbsData;

		try {
			bbsData = _reader.onlineUpdate(uri, getConfig().getThreadHeaderReadSize(), getConfig()
					.getResReadSize(), range);
		} catch (IOException ioe) {
			System.out.println(ioe);
			throw ioe;
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
			throw new IOException(e.getClass().getName() + ": " + e.getMessage());
		}
		int type = bbsData.getType();
		if (type == BBSData.BOARD) {
			Board board = (Board) bbsData;
			ThreadHeaderListControl thlc = getThreadHeaderListControl(board, null, true);
			_threadListTabCtrl.setSelected(thlc, true);
			_rootTabCtrl.setSelectedIndex(1, true);
			thlc.read(true);
		} else if (type == BBSData.THREAD_HEADER) {
			ThreadHeader threadHeader = (ThreadHeader) bbsData;
			ResListControl rlc = getResListControl(threadHeader, true);
			_threadTabCtrl.setSelected(rlc, true);
			_rootTabCtrl.setSelectedIndex(2, true);
			rlc.read(range.start);
		}
	}

	public void resWrite(ThreadHeader threadHeader, boolean direct) {
		if (direct) {
			try {
				getGikoletExtensions().resWrite(threadHeader.getBoard().getName(),
						threadHeader.getBoard().getNo(), threadHeader.getSubject(),
						threadHeader.getThreadNo());
			} catch (IOException ioe) {
				showExceptionDialog(ioe);
			}
		} else {
			String uri = _reader.getWriteURI(threadHeader);
			try {
				uri = Toolkit.getDisplay().imeOn("書込URIへ飛ぶ", uri, Display.ANY);
				Toolkit.getToolkit().openBrowser(uri);
			} catch (Exception ce) {
			}
		}
	}

	public void selectedRes(ThreadHeader threadHeader, int index) {
		_bookmarkListCtrl.readRes(threadHeader, index);
	}

	public void addBookmark(Board board) {
		_bookmarkListCtrl.addBookmark(board);
	}

	public void addBookmark(ThreadHeader threadHeader) {
		addBookmark(threadHeader, 0);
	}

	public void addBookmark(ThreadHeader threadHeader, int resNo) {
		addBookmark(threadHeader, resNo, resNo);
	}

	public void addBookmark(ThreadHeader threadHeader, int resNo, int readMaxResIndex) {
		ResListControl rlc = getResListControl(threadHeader, false);
		if (rlc != null) {
			readMaxResIndex = (rlc.getReadMaxIndex() != -1) ? rlc.getReadMaxIndex()
					: readMaxResIndex;

			Res res = rlc.getViewRes();
			if (res != null) {
				resNo = res.getNo();
			}
		}
		_bookmarkListCtrl.addBookmark(threadHeader, resNo, readMaxResIndex);
	}

	public void fontChanged(Font font) {
		_shell.setFont(font);
	}

	public void resScrollWeightChanged(int weight) {
		for (int i = 0; i < _threadTabCtrl.getTabPageSize(); i++) {
			TabPage tp = _threadTabCtrl.getTabPageAt(i);
			if (tp instanceof ResListControl) {
				((ResListControl) tp).setScrollWeight(weight);
			}
		}
	}

	public void threadTabTextLengthChanged(int threadTabTextLength) {
		for (int i = 0; i < _threadTabCtrl.getTabPageSize(); i++) {
			TabPage tp = _threadTabCtrl.getTabPageAt(i);
			if (tp instanceof ResListControl) {
				ResListControl rlc = (ResListControl) tp;
				ThreadHeader th = rlc.getViewThreadHeader();
				if (th != null) {
					String tabText = th.getSubject();
					if (threadTabTextLength > 0 && tabText.length() > threadTabTextLength) {
						tabText = tabText.substring(0, threadTabTextLength);
					}
					rlc.setText(tabText);
				}
			}
		}
	}

	public void topTabBarShowChanged(boolean show) {
		_rootTabCtrl.setTabBarShow(show);
	}

	public void setResShowMethod(int method) {
		for (int i = 0; i < _threadTabCtrl.getTabPageSize(); i++) {
			TabPage tp = _threadTabCtrl.getTabPageAt(i);
			if (tp instanceof ResListControl) {
				ResListControl rlc = (ResListControl) tp;
				rlc.setResShowMethod(method);
			}
		}
	}

	private void load() throws IOException {
		Scratchpad scratchpad = Toolkit.getToolkit().getScratchpad();

		InputStream in = null;
		try {
			in = scratchpad.openInputStream();
			byte[] identifier = new byte[_identifier.length];
			in.read(identifier);

			for (int i = 0; i < identifier.length; i++) {
				if (identifier[i] != _identifier[i]) {
					throw new IOException();
				}
			}

			_boardTreeCtrl.load(in);
			_bookmarkListCtrl.load(in);
			_configDialog.load(in);
		} finally {
			try {
				in.close();
			} catch (Exception e1) {
			}
		}
	}

	public void save() throws IOException {
		Scratchpad scratchpad = Toolkit.getToolkit().getScratchpad();

		OutputStream out = null;
		try {
			out = scratchpad.openOutputStream();
			out.write(_identifier);

			_boardTreeCtrl.save(out);
			_bookmarkListCtrl.save(out);
			_configDialog.save(out);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	private void menuAction(ActionEvent e) {
		Object source = e.getSource();
		if (source == _boardTreeShowMenuItem) {
			_rootTabCtrl.setSelectedIndex(0, true);
		} else if (source == _threadHeaderListMoveMenuItem) {
			threadHeaderListTabMove();
		} else if (source == _resListTabMoveMenuItem) {
			resListTabMove();
		} else if (source == _configShowMenuItem) {
			_configDialog.setVisible(true);
		} else if (source == _exitMenuItem) {
			Toolkit.getDisplay().destroy();
		}
	}
}
