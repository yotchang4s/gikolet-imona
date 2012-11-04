/*
 * Created on 2005/02/04 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet;

import gikolet.base.Toolkit;
import gikolet.base.ui.CancelException;
import gikolet.base.ui.OptionPane;
import gikolet.base.ui.Display;
import gikolet.base.ui.List;
import gikolet.base.ui.Menu;
import gikolet.base.ui.MenuItem;
import gikolet.base.ui.Scrollable;
import gikolet.base.ui.TabPage;
import gikolet.base.ui.events.ActionEvent;
import gikolet.base.ui.events.ActionListener;
import gikolet.base.ui.events.KeyEvent;
import gikolet.base.ui.events.KeyEventListener;
import gikolet.base.ui.events.ListSelectionListener;
import gikolet.base.util.Utilities;
import gikolet.imona.BBSData;
import gikolet.imona.Board;
import gikolet.imona.BoardUpdateListener;
import gikolet.imona.ThreadHeader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author tetsutaro TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class BookmarkListControl extends TabPage {
	private Vector		_bookmarks;
	private Hashtable	_bookmarkTable;
	private Hashtable	_setQueue;

	private Gikolet		_gikolet;

	private List		_list;

	private Menu		menu;
	private MenuItem	moveMenuItem;
	private MenuItem	_addBoardMenuItem;
	private MenuItem	_addThreadMenuItem;
	private MenuItem	_editMenuItem;
	private MenuItem	_importMenuItem;
	private MenuItem	_exportMenuItem;
	private MenuItem	removeMenuItem;

	private int			moveBaseIndex	= -1;

	protected BookmarkListControl(Gikolet gikolet) {
		super();

		_gikolet = gikolet;
		_setQueue = new Hashtable();

		_bookmarks = new Vector();
		_bookmarkTable = new Hashtable();
		_list = new List(Scrollable.SCROLLBAR_AS_NEEDED,
				Scrollable.SCROLLBAR_NEVER);

		_gikolet.getReader().addBoardUpdateListener(new BoardUpdateListener() {
			public void boardAdded(String category, Board newBoard) {
			}

			public void boardChanged(String category, Board oldBoard,
					Board newBoard) {
				BookmarkListControl.this.boardChanged(category, oldBoard,
						newBoard);
			}
		});

		add(_list);

		menu = new Menu("編集");

		_addBoardMenuItem = new MenuItem("板の追加");
		_addThreadMenuItem = new MenuItem("ｽﾚｯﾄﾞの追加");
		moveMenuItem = new MenuItem("移動");
		_editMenuItem = new MenuItem("編集");
		removeMenuItem = new MenuItem("削除");
		_importMenuItem = new MenuItem("ｲﾝﾎﾟｰﾄ");
		_exportMenuItem = new MenuItem("ｴｸｽﾎﾟｰﾄ");

		menu.add(_addBoardMenuItem);
		menu.add(_addThreadMenuItem);
		menu.add(_importMenuItem);
		menu.add(_exportMenuItem);

		ActionListener menuActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAction((MenuItem) e.getSource());
			}
		};

		_list.setPopupMenuItem(menu);

		menu.addActionListener(menuActionListener);
		_addBoardMenuItem.addActionListener(menuActionListener);
		_addThreadMenuItem.addActionListener(menuActionListener);
		moveMenuItem.addActionListener(menuActionListener);
		_editMenuItem.addActionListener(menuActionListener);
		removeMenuItem.addActionListener(menuActionListener);
		_importMenuItem.addActionListener(menuActionListener);
		_exportMenuItem.addActionListener(menuActionListener);

		_list.addKeyEventListener(new KeyEventListener() {
			public void keyAction(KeyEvent e) {
				list_keyAction(e);
			}
		});
		_list.addKeyEventListener(_gikolet.getCommonListener());

		_list.addListSelectionListener(new ListSelectionListener() {
			public void selectedChange(int index) {
				list_selectedChange(index);
			}
		});
	}

	private void digestSetQueue() {
		if (!_setQueue.isEmpty()) {
			Enumeration e = _setQueue.elements();
			while (e.hasMoreElements()) {
				Integer oi = (Integer) e.nextElement();

				_list.set(getListStr((BBSData) _bookmarks.elementAt(oi
						.intValue())), oi.intValue());
			}
			_setQueue.clear();
		}
	}

	private void setSet(int index) {
		if (!isShowing()) {
			Integer oi = new Integer(index);
			_setQueue.put(oi, oi);
		} else {
			_list.set(getListStr((BBSData) _bookmarks.elementAt(index)), index);
		}
	}

	private void list_keyAction(KeyEvent e) {
		int type = e.getKeyActionType();
		int code = e.getKeyCode();

		if (type != KeyEvent.RELEASED) {
			if (code == KeyEvent.KEY_NUM7) {
				_gikolet.transferThreadHeaderListFocusBackward(this);
				e.consume();
			} else if (code == KeyEvent.KEY_NUM9) {
				_gikolet.transferThreadHeaderListFocusForward(this);
				e.consume();
			} else if (type == KeyEvent.PRESSED) {
				if (code == KeyEvent.ENTER || code == KeyEvent.KEY_NUM5) {
					if (_list.getSelectedIndex() != -1) {
						list_click();
					}
				}
			}
		}
	}

	public String getData() {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < _bookmarks.size(); i++) {
			BBSData bbsData = (BBSData) _bookmarks.elementAt(i);

			Board board;
			ThreadHeader threadHeader = null;
			Bookmark bookmark = null;
			if (bbsData.getType() == BBSData.BOARD) {
				board = (Board) bbsData;
			} else if (bbsData instanceof ThreadHeader) {
				threadHeader = (ThreadHeader) bbsData;
				board = threadHeader.getBoard();

				if (threadHeader instanceof Bookmark) {
					bookmark = (Bookmark) bbsData;
				}
			} else {
				continue;
			}
			sb.append(board.getNo());
			sb.append('\t');
			sb.append(board.getName());

			if (threadHeader != null) {
				sb.append('\t');
				sb.append(threadHeader.getThreadNo());
				sb.append('\t');
				sb.append(threadHeader.getSubject());
				sb.append('\t');
				sb.append(threadHeader.getResCount());

				if (bookmark != null) {
					sb.append('\t');
					sb.append(bookmark.getResNo());
					sb.append('\t');
					sb.append(bookmark.getReadMaxResIndex());
				}
			}
			if (i + 1 < _bookmarks.size()) {
				sb.append('\n');
			}
		}
		return sb.toString();
	}

	public void setData(String data) throws IOException {
		String[] lines = Utilities.tokenToStrings(data, "\n");

		for (int i = 0; i < lines.length; i++) {
			String[] values = Utilities.tokenToStrings(lines[i], "\t");
			if (values.length >= 2) {
				Board board = new Board(Integer.parseInt(values[0]), Utilities
						.unescape(values[1], true, true));
				if (values.length >= 5) {
					ThreadHeader threadHeader = new ThreadHeader(board, Integer
							.parseInt(values[2]), values[3], Integer
							.parseInt(values[4]));

					int resNo = 0;
					int readMaxResIndex = 0;
					if (values.length >= 7) {
						resNo = Integer.parseInt(values[5]);
						readMaxResIndex = Integer.parseInt(values[6]);
					}
					addBookmark(threadHeader, resNo, readMaxResIndex);
				} else {
					addBookmark(board);
				}
			}
		}
	}

	public void load(InputStream in) throws IOException {
		DataInputStream dis = new DataInputStream(in);

		setData(dis.readUTF());
	}

	public void save(OutputStream out) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);

		dos.writeUTF(getData());
		dos.flush();
	}

	/*
	 * public void save(OutputStream out) throws IOException { DataOutputStream
	 * dos = new DataOutputStream(out); dos.writeInt(_bookmarks.size()); for
	 * (int i = 0; i < _bookmarks.size(); i++) { Object obj =
	 * _bookmarks.elementAt(i); if (obj instanceof Bookmark) { Bookmark bookmark =
	 * (Bookmark) obj; dos.writeInt(bookmark.getBoard().getNo());
	 * dos.writeUTF(bookmark.getBoard().getName());
	 * dos.writeUTF(bookmark.getSubject());
	 * dos.writeInt(bookmark.getThreadNo());
	 * dos.writeInt(bookmark.getResCount()); dos.writeInt(bookmark.getResNo());
	 * dos.writeInt(bookmark.getReadMaxResIndex()); } else if (obj instanceof
	 * Board) { Board board = (Board) obj; dos.writeInt(board.getNo());
	 * dos.writeUTF(board.getName()); dos.writeUTF(""); dos.writeInt(-1);
	 * dos.writeInt(-1); dos.writeInt(-1); dos.writeInt(-1); } } } public void
	 * load(InputStream in) throws IOException { DataInputStream dis = new
	 * DataInputStream(in); Vector bv = new Vector(); int size = dis.readInt();
	 * for (int i = 0; i < size; i++) { Board board = new Board(dis.readInt(),
	 * dis.readUTF()); String subject = dis.readUTF(); int threadNo =
	 * dis.readInt(); int resCount = dis.readInt(); int resNo = dis.readInt();
	 * int readMaxResIndex = dis.readInt(); if (threadNo == -1) {
	 * bv.addElement(board); } else { bv.addElement(new Bookmark(board,
	 * threadNo, subject, resCount, resNo, readMaxResIndex)); } } for (int i =
	 * 0; i < bv.size(); i++) { addBookmark((BBSData) bv.elementAt(i)); } }
	 */

	protected void viewChanged(boolean view) {
		super.viewChanged(view);

		if (view) {
			digestSetQueue();
		}
	}

	private String getListStr(BBSData bbsData) {
		if (bbsData.getType() == BBSData.BOARD) {
			Board board = (Board) bbsData;
			return "[板] " + board.getName();
		} else if (bbsData.getType() == Bookmark.BOOKMARK_THREAD_THREADER) {
			Bookmark bmk = (Bookmark) bbsData;
			return bmk.getSubject() + " (" + bmk.getResNo() + "/"
					+ bmk.getReadMaxResIndex() + ")";
		}
		return bbsData.toString();
	}

	private void boardChanged(String category, Board oldBoard, Board newBoard) {
		for (int i = 0; i < _bookmarks.size(); i++) {
			Object obj = _bookmarks.elementAt(i);
			if (obj instanceof Bookmark) {
				Bookmark bookmark = (Bookmark) obj;

				if (bookmark.getBoard().equals(oldBoard)) {
					Bookmark newBookmark = new Bookmark(newBoard, bookmark
							.getThreadNo(), bookmark.getSubject(), bookmark
							.getResCount(), bookmark.getResNo(), bookmark
							.getReadMaxResIndex());

					setCore(newBookmark, i);
				}
			} else if (obj instanceof Board) {
				Board board = (Board) obj;

				if (board.equals(oldBoard)) {
					setCore(newBoard, i);
				}
			}
		}
	}

	public BBSData getBookmark(BBSData bbsData) {
		Integer oi = (Integer) _bookmarkTable.get(bbsData);
		if (oi != null) {
			return (BBSData) _bookmarks.elementAt(oi.intValue());
		}
		return null;
	}

	public boolean containsBookmark(BBSData bbsData) {
		return _bookmarkTable.get(bbsData) != null;
	}

	public int getBookmarkSize() {
		return _bookmarks.size();
	}

	public BBSData getBookmarkAt(int index) {
		return (BBSData) _bookmarks.elementAt(index);
	}

	public void addBookmark(ThreadHeader threadHeader, int resNo) {
		addBookmark(threadHeader, resNo, resNo);
	}

	public void addBookmark(ThreadHeader threadHeader, int resNo,
			int readMaxResIndex) {
		addBookmark(new Bookmark(threadHeader, resNo, readMaxResIndex));
	}

	public void addBookmark(Board board) {
		addBookmark((BBSData) board);
	}

	private void addBookmark(BBSData bbsData) {
		if (!containsBookmark(bbsData)) {
			_bookmarks.addElement(bbsData);
			_bookmarkTable.put(bbsData, new Integer(_bookmarks.size() - 1));
			_list.add(getListStr(bbsData));
		}
	}

	public void readRes(ThreadHeader threadHeader, int index) {
		Integer oi = (Integer) _bookmarkTable.get(threadHeader);
		if (oi != null) {
			Object bbsData = _bookmarks.elementAt(oi.intValue());
			if (bbsData instanceof Bookmark) {
				Bookmark bmk = (Bookmark) bbsData;
				if (bmk.getReadMaxResIndex() < index) {
					bmk.setReadMaxResIndex(index);
				}
				bmk.setResNo(index);

				setSet(oi.intValue());
			}
		}
		/*
		 * for (int i = 0; i < _bookmarks.size(); i++) { Object bookmark =
		 * _bookmarks.elementAt(i); if (bookmark.equals(threadHeader)) {
		 * Bookmark bmk = (Bookmark) bookmark; if (bmk.getReadMaxResIndex() <
		 * index) { bmk.setReadMaxResIndex(index); } bmk.setResNo(index);
		 * setSet(i); break; } }
		 */
	}

	private void insertCore(BBSData bbsData, int index) {
		_bookmarks.insertElementAt(bbsData, index);

		for (int i = index; i < _bookmarks.size(); i++) {
			Object e = _bookmarks.elementAt(i);
			_bookmarkTable.put(e, new Integer(i));
		}
	}

	private void setCore(BBSData bbsData, int index) {
		Object o = _bookmarks.elementAt(index);

		_bookmarkTable.remove(o);
		_bookmarks.setElementAt(bbsData, index);
		_bookmarkTable.put(bbsData, new Integer(index));
	}

	private void removeCore(int index) {
		Object o = _bookmarks.elementAt(index);

		_bookmarks.removeElementAt(index);
		_bookmarkTable.remove(o);

		for (int i = index; i < _bookmarks.size(); i++) {
			Object e = _bookmarks.elementAt(i);
			_bookmarkTable.put(e, new Integer(i));
		}
	}

	private void menuAction(MenuItem menuItem) {
		if (menuItem == menu) {
			if (_bookmarks.isEmpty()) {
				menu.remove(moveMenuItem);
				menu.remove(_editMenuItem);
				menu.remove(removeMenuItem);
			} else {
				menu.add(moveMenuItem);
				menu.add(_editMenuItem);
				menu.add(removeMenuItem);
			}
		} else if (menuItem == _addBoardMenuItem) {
			try {
				addBookmark(inputBookmarkBoard(null));
			} catch (CancelException e) {
			}
		} else if (menuItem == _addThreadMenuItem) {
			try {
				addBookmark(inputBookmarkThread(null));
			} catch (CancelException e) {
			}
		} else if (menuItem == moveMenuItem) {
			moveBaseIndex = _list.getSelectedIndex();
			_list.setSelectColor(_list.getSelectColor().getReversalColor());
		} else if (menuItem == _editMenuItem) {
			int index = _list.getSelectedIndex();
			Object obj = _bookmarks.elementAt(index);
			try {
				if (obj instanceof Board) {
					Board board = (Board) obj;
					board = inputBookmarkBoard(board);
					setCore(board, index);
					_list.set(getListStr(board), index);
				} else if (obj instanceof Bookmark) {
					Bookmark bookmark = (Bookmark) obj;
					bookmark = inputBookmarkThread(bookmark);
					setCore(bookmark, index);
					_list.set(getListStr(bookmark), index);
				}
				_bookmarkTable.put(obj, new Integer(index));
			} catch (CancelException e) {
			}
		} else if (menuItem == removeMenuItem) {
			digestSetQueue();

			int selectedIndex = _list.getSelectedIndex();

			removeCore(selectedIndex);
			_list.removeAt(selectedIndex);
		} else if (menuItem == _importMenuItem) {
			try {
				String str = Toolkit.getDisplay().imeOn("ｲﾝﾎﾟｰﾄ", "",
						Display.ANY);
				str = Utilities.unescape(str, true, true);
				setData(str);
			} catch (CancelException e) {
			} catch (IOException e) {
				System.out.println(e);
				e.printStackTrace();
				_gikolet.showExceptionDialog(new IOException("データが不正です。"));
			}

		} else if (menuItem == _exportMenuItem) {
			try {
				String str = getData();
				str = Utilities.escape(str, true, true);
				Toolkit.getDisplay().imeOn("ｴｸｽﾎﾟｰﾄ", str, Display.ANY);
			} catch (CancelException e) {
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
				_gikolet.showExceptionDialog(new IOException("データが不正です。"));
			}
		}
	}

	private Bookmark inputBookmarkThread(Bookmark bookmark)
			throws CancelException {
		Board board = null;
		int threadNo = 0;
		String threadSubject = "";
		// int threadResCount = 1;
		int threadResNo = 1;
		int threadRreadMaxResIndex = 1;

		if (bookmark != null) {
			board = bookmark.getBoard();
			threadNo = bookmark.getThreadNo();
			threadSubject = bookmark.getSubject();
			// threadResCount = bookmark.getResCount();
			threadResNo = bookmark.getResNo();
			threadRreadMaxResIndex = bookmark.getReadMaxResIndex();
		}
		board = inputBookmarkBoard(board);

		threadSubject = Toolkit.getDisplay().imeOn("ｽﾚｯﾄﾞﾀｲﾄﾙ", threadSubject,
				Display.ANY);
		/*
		 * if (threadSubject.equals("")) { throw new CancelException(); }
		 */
		threadNo = OptionPane.showInputDialog(this, "ｽﾚｯﾄﾞ番号", 0,
				Integer.MAX_VALUE, threadNo);
		// threadResCount = OptionPane.showInputDialog(this, "ｽﾚｯﾄﾞのﾚｽ数", 1,
		// 1000, threadResCount);
		threadResNo = OptionPane.showInputDialog(this, "栞のﾚｽ番号", 1, 10000,
				threadResNo);
		threadRreadMaxResIndex = OptionPane.showInputDialog(this, "既読の最大ﾚｽ番号",
				1, 10000, threadRreadMaxResIndex);

		return new Bookmark(board, threadNo, threadSubject, 0, threadResNo,
				threadRreadMaxResIndex);
	}

	private Board inputBookmarkBoard(Board board) throws CancelException {
		String boardName = "";
		int boardNo = 0;
		if (board != null) {
			boardName = board.getName();
			boardNo = board.getNo();
		}

		boardName = Toolkit.getDisplay().imeOn("板名", boardName, Display.ANY);
		if (boardName.equals("")) {
			throw new CancelException();
		}
		boardNo = OptionPane.showInputDialog(this, "板番号", 0, 9999999, boardNo);

		return new Board(boardNo, boardName);
	}

	private void list_selectedChange(int index) {
		if (moveBaseIndex != -1) {
			digestSetQueue();
			BBSData bookmark = (BBSData) _bookmarks.elementAt(moveBaseIndex);
			if (index < moveBaseIndex) {
				insertCore(bookmark, index);
				_list.insert(getListStr(bookmark), index);

				removeCore(moveBaseIndex + 1);
				_list.removeAt(moveBaseIndex + 1);
			} else {
				insertCore(bookmark, index + 1);
				_list.insert(getListStr(bookmark), index + 1);

				removeCore(moveBaseIndex);
				_list.removeAt(moveBaseIndex);
			}
			moveBaseIndex = index;
		}
	}

	private void list_click() {
		if (moveBaseIndex != -1) {
			_list.setSelectColor(null);
			moveBaseIndex = -1;
			return;
		}
		int index = _list.getSelectedIndex();
		if (index != -1) {
			Object bookmark = _bookmarks.elementAt(index);
			if (bookmark != null) {
				if (bookmark instanceof Board) {
					_gikolet.selected((Board) bookmark);
				} else if (bookmark instanceof Bookmark) {
					_gikolet.selected((Bookmark) bookmark);
				}
			}
		}
	}

	protected void layout() {
		_list.setBounds(0, 0, getWidth(), getHeight());
	}
}