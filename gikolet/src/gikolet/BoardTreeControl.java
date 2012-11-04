/*
 * Created on 2005/01/23
 */
package gikolet;

import gikolet.base.ui.ContainerControl;
import gikolet.base.ui.OptionPane;
import gikolet.base.ui.List;
import gikolet.base.ui.Menu;
import gikolet.base.ui.MenuItem;
import gikolet.base.ui.events.ActionEvent;
import gikolet.base.ui.events.ActionListener;
import gikolet.base.ui.events.KeyEvent;
import gikolet.base.ui.events.KeyEventListener;
import gikolet.imona.Board;
import gikolet.imona.BoardTable;
import gikolet.imona.Category;
import gikolet.imona.CategoryList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author tetsutaro
 */
public class BoardTreeControl extends ContainerControl {
	private Category	_extendedCategory;
	private List		_list;
	private BoardTable	_iMonaBoardTable;
	private Gikolet		_gikolet;

	private Menu		_menu;
	private MenuItem	_threadHeadersFindMenu;
	private MenuItem	_addBookmarkMenuItem;
	private MenuItem	_showBoardNoMenuItem;
	private MenuItem	_boardUpdateMenu;

	protected BoardTreeControl(Gikolet gikolet) {
		super(false, false);

		_gikolet = gikolet;

		_iMonaBoardTable = new BoardTable();

		_list = new List();

		add(_list);

		_menu = new Menu("板ﾒﾆｭｰ");
		_threadHeadersFindMenu = new MenuItem("ｽﾚｯﾄﾞ検索");
		_addBookmarkMenuItem = new MenuItem("お気に入りへ登録");
		_showBoardNoMenuItem = new MenuItem("板番号の表示");
		_boardUpdateMenu = new MenuItem("板一覧更新");

		_menu.add(_boardUpdateMenu);
		_list.setPopupMenuItem(_menu);

		ActionListener menuActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAction((MenuItem) e.getSource());
			}
		};
		_menu.addActionListener(menuActionListener);
		_threadHeadersFindMenu.addActionListener(menuActionListener);
		_addBookmarkMenuItem.addActionListener(menuActionListener);
		_showBoardNoMenuItem.addActionListener(menuActionListener);
		_boardUpdateMenu.addActionListener(menuActionListener);

		_list.addKeyEventListener(new KeyEventListener() {
			public void keyAction(KeyEvent e) {
				list_keyAction(e);
			}
		});
		_list.addKeyEventListener(_gikolet.getCommonListener());
	}

	private Board	_selectedBoard;

	private void menuAction(MenuItem menuItem) {
		if (menuItem == _menu) {
			Object so = getSelected();
			if (so != null) {
				if (so instanceof Board) {
					_selectedBoard = (Board) so;
					_menu.insert(_threadHeadersFindMenu, 0);
					_menu.insert(_addBookmarkMenuItem, 1);
					_menu.insert(_showBoardNoMenuItem, 2);
				} else if (so instanceof Category) {
					_menu.remove(_threadHeadersFindMenu);
					_menu.remove(_addBookmarkMenuItem);
					_menu.remove(_showBoardNoMenuItem);
				}
			}
		} else if (menuItem == _boardUpdateMenu) {
			try {
				BoardTable boardTable = new BoardTable();
				_gikolet.getReader().onlineUpdate(boardTable);
				_iMonaBoardTable.clear();
				_iMonaBoardTable.add(boardTable);

				setBoardTable();
			} catch (Exception e) {
				_gikolet.showExceptionDialog(e);
			}
		} else if (menuItem == _threadHeadersFindMenu) {
			_gikolet.selected(_selectedBoard, true);
		} else if (menuItem == _addBookmarkMenuItem) {
			_gikolet.addBookmark(_selectedBoard);
		} else if (menuItem == _showBoardNoMenuItem) {
			OptionPane.showMessageDialog(this, _selectedBoard.getName() + "の板番号", Integer
					.toString(_selectedBoard.getNo()));
		}
	}

	private void list_keyAction(KeyEvent e) {
		if (e.getKeyActionType() == KeyEvent.PRESSED) {
			if (e.getKeyCode() == KeyEvent.KEY_NUM0) {
				if (_extendedCategory != null) {
					expand(_extendedCategory);
				}
			}
		} else if (e.getKeyCode() == KeyEvent.ENTER) {
			if (_list.getSelectedIndex() != -1) {
				selected();
			}
		}
	}

	private void selected() {
		Object os = getSelected();
		if (os instanceof Category) {
			expand((Category) os);
		} else if (os instanceof Board) {
			_gikolet.selected((Board) os, false);
		}
	}

	private void expand(Category category) {
		CategoryList categories = _iMonaBoardTable.getCategorys();
		if (!categories.contains(category)) {
			return;
		}
		int categoryIndex = categories.indexOf(category);

		if (category == _extendedCategory) {
			_list.set(" + " + category.getName(), categoryIndex);
			for (int j = 0; j < category.getBoards().getCount(); j++) {
				_list.removeAt(categoryIndex + 1);
			}
			_list.validateViewContent();
			_list.setSelectedIndex(categoryIndex);
			_list.ensureIndexIsVisible(_list.getSelectedIndex());

			_extendedCategory = null;
		} else {
			if (_extendedCategory != null) {
				expand(_extendedCategory);
			}
			_list.set(" - " + category.getName(), categoryIndex);
			for (int j = 0; j < category.getBoards().getCount(); j++) {
				Board board = category.getBoards().getBoard(j);
				_list.insert("      " + board.getName(), categoryIndex + j + 1);
			}
			_list.validateViewContent();
			_list.setSelectedIndex(categoryIndex);
			_list.ensureIndexIsVisible(categoryIndex + category.getBoards().getCount());
			_list.ensureIndexIsVisible(_list.getSelectedIndex());

			_extendedCategory = category;
		}
	}

	public void save(OutputStream out) throws IOException {
		_gikolet.getReader().save(out);
	}

	public void load(InputStream in) throws IOException {
		_gikolet.getReader().load(in);
		_gikolet.getReader().read(_iMonaBoardTable);
		setBoardTable();
	}

	public Category getSelectedCategory() {
		Object obj = getSelected();
		if (obj instanceof Category) {
			return (Category) obj;
		}
		return null;
	}

	public Board getSelectedBoard() {
		Object obj = getSelected();
		if (obj instanceof Board) {
			return (Board) obj;
		}
		return null;
	}

	private Object getSelected() {
		CategoryList categorys = _iMonaBoardTable.getCategorys();
		int selectedIndex = _list.getSelectedIndex();
		if (selectedIndex == -1 || categorys.getCount() == 0) {
			return null;
		}
		int extendedCategoryIndex = categorys.indexOf(_extendedCategory);
		if (extendedCategoryIndex == -1 || selectedIndex <= extendedCategoryIndex) {
			return categorys.getCategory(selectedIndex);

		} else if (extendedCategoryIndex + _extendedCategory.getCount() < selectedIndex) {
			return categorys.getCategory(selectedIndex - _extendedCategory.getCount());

		} else {
			return _extendedCategory.getBoards()
					.getBoard(selectedIndex - extendedCategoryIndex - 1);

		}
	}

	private void setBoardTable() {
		_list.clear();
		_list.validate();
		_extendedCategory = null;
		CategoryList categorys = _iMonaBoardTable.getCategorys();
		for (int i = 0; i < categorys.getCount(); i++) {
			Category category = categorys.getCategory(i);
			_list.add(((category.getCount() == 0) ? "   " : " + ") + category.getName());
		}
	}

	protected void layout() {
		_list.setBounds(0, 0, getWidth(), getHeight());
	}
}