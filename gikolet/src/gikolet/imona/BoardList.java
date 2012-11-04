/*
 * Created on 2005/01/21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.imona;

import java.util.Vector;

/**
 * @author tetsutaro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BoardList {
	private Vector	_boards;

	public BoardList() {
		_boards = new Vector();
	}

	public synchronized Board getBoard(int index) {
		return (Board) _boards.elementAt(index);
	}

	public synchronized void add(Board board) {
		_boards.addElement(board);
	}

	public synchronized void addRange(BoardList boards) {
		for (int i = 0; i < boards.getCount(); i++) {
			add(boards.getBoard(i));
		}
	}

	public synchronized void insert(Board board, int index) {
		_boards.insertElementAt(board, index);
	}

	public synchronized void remove(Board board) {
		_boards.removeElement(board);
	}

	public synchronized void removeAt(int index) {
		this._boards.removeElementAt(index);
	}

	public synchronized int indexOf(String boardName) {
		for (int i = 0; i < getCount(); i++) {
			Board board = getBoard(i);

			if (board.getName().equals(boardName)) {
				return i;
			}
		}
		return -1;
	}

	public synchronized int indexOf(Board board) {
		return _boards.indexOf(board);
	}

	public synchronized int indexOf(int no) {
		for (int i = 0; i < getCount(); i++) {
			Board board = getBoard(i);

			if (board.getNo() == no)
				return i;
		}
		return -1;
	}

	public synchronized int getCount() {
		return this._boards.size();
	}

	public synchronized Board[] toArray() {
		Board[] w = new Board[_boards.size()];
		_boards.copyInto(w);
		return w;
	}

	public synchronized String toString() {
		StringBuffer sb;
		sb = new StringBuffer(16 * getCount());
		for (int i = 0; i < getCount(); i++) {
			sb.append(getBoard(i).getName());
			if (i + 1 < getCount()) {
				sb.append("<>");
			}
		}
		return sb.toString();
	}
}