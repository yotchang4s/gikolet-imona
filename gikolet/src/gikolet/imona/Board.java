/*
 * Created on 2005/01/21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.imona;

/**
 * @author tetsutaro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Board extends BBSData {
	private String	_name	= "";
	private int		_no;

	public Board(int no, String name) {
		super(BBSData.BOARD);
		_no = no;
		_name = name;
	}

	public void setNo(int boardNo) {
		_no = boardNo;
	}

	public String getName() {
		return _name;
	}

	public int getNo() {
		return _no;
	}

	public int hashCode() {
		return _no;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Board) {
			return equals((Board) obj);
		}
		return false;
	}

	public boolean equals(Board board) {
		if (board == null) {
			return false;
		}
		return (board.getNo() == getNo() && board.getName().equals(getName()));
	}

	public String toString() {
		return _name + ":" + _no;
	}
}