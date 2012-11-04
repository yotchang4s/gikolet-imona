/*
 * Created on 2005/01/21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.imona;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

/**
 * @author tetsutaro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BoardTable {
	private CategoryList	_categorys;

	public CategoryList getCategorys() {
		return _categorys;
	}

	public BoardTable() {
		_categorys = new CategoryList();
	}

	public synchronized void add(BoardTable table) {
		if (table == null) {
			throw new NullPointerException("table");
		}

		synchronized (table.getCategorys()) {
			for (int i = 0; i < table.getCategorys().getCount(); i++) {
				Category category = table.getCategorys().getCategory(i);
				getCategorys().add(category);
			}
		}
	}

	public synchronized void clear() {
		getCategorys().clear();
	}

	public synchronized void save(OutputStream out) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);

		CategoryList categorys = getCategorys();

		synchronized (categorys) {
			dos.writeInt(categorys.getCount());

			for (int i = 0; i < categorys.getCount(); i++) {
				Category category = categorys.getCategory(i);

				dos.writeUTF(category.getName());

				BoardList boards = category.getBoards();
				dos.writeInt(boards.getCount());

				synchronized (boards) {
					for (int j = 0; j < boards.getCount(); j++) {
						Board board = boards.getBoard(j);
						dos.writeInt(board.getNo());
						dos.writeUTF(board.getName());
					}
				}
			}
		}
		dos.flush();
	}

	public synchronized void load(InputStream in) throws IOException {
		BoardTable newTable = new BoardTable();

		DataInputStream dis = new DataInputStream(in);

		int categoryCount = dis.readInt();
		for (int i = 0; i < categoryCount; i++) {
			Category category = new Category(dis.readUTF());
			newTable.getCategorys().add(category);

			int boardCount = dis.readInt();
			for (int j = 0; j < boardCount; j++) {
				Board board = new Board(dis.readInt(), dis.readUTF());
				category.getBoards().add(board);
			}
		}

		getCategorys().clear();
		getCategorys().addRange(newTable.getCategorys());
	}

	public synchronized boolean contains(Board board) {
		if (board == null) {
			throw new NullPointerException("board");
		}

		CategoryList categorys = getCategorys();

		synchronized (categorys) {
			for (int i = 0; i < categorys.getCount(); i++) {
				Category category = categorys.getCategory(i);

				if (category.getBoards().indexOf(board) >= 0) {
					return true;
				}
			}
		}
		return false;
	}

	public synchronized Board getBoardFromNo(int no) {
		CategoryList categorys = getCategorys();
		synchronized (categorys) {
			for (int i = 0; i < categorys.getCount(); i++) {
				Category category = categorys.getCategory(i);

				synchronized (category.getBoards()) {
					int index = category.getBoards().indexOf(no);
					if (index >= 0) {
						return category.getBoards().getBoard(index);
					}
				}
			}
		}
		return null;
	}

	public synchronized Board getBoardFromName(String boardName, int index) {
		CategoryList categorys = getCategorys();

		synchronized (categorys) {
			int count = 0;
			for (int i = 0; i < categorys.getCount(); i++) {
				Category category = categorys.getCategory(i);

				synchronized (category.getBoards()) {
					int iw = category.getBoards().indexOf(boardName);
					if (iw != -1) {
						if (index == count) {
							return category.getBoards().getBoard(index);
						}
						count++;
					}
				}
			}
		}
		return null;
	}

	public synchronized Board getBoardFromName(String categoryName, String boardName) {
		CategoryList categorys = getCategorys();

		synchronized (categorys) {
			int index = categorys.indexOf(categoryName);
			if (index != -1) {
				Category category = categorys.getCategory(index);

				synchronized (category.getBoards()) {
					index = category.getBoards().indexOf(boardName);
					if (index != -1) {
						return category.getBoards().getBoard(index);
					}
				}
			}
		}
		return null;
	}

	public synchronized Board[] toArray() {
		Vector vw = new Vector();

		CategoryList categorys = getCategorys();

		synchronized (categorys) {
			for (int i = 0; i < categorys.getCount(); i++) {
				Category category = categorys.getCategory(i);

				BoardList boards = category.getBoards();

				synchronized (boards) {
					for (int j = 0; j < boards.getCount(); j++) {
						Board board = boards.getBoard(i);
						vw.addElement(board);
					}
				}
			}
		}

		Board[] baw = new Board[vw.size()];
		vw.copyInto(baw);

		return baw;
	}
}