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
public class Category {
	private BoardList	_boards;
	private String		_name;

	public Category() {
		this("");
	}

	public Category(String name) {
		if (name == null) {
			throw new NullPointerException("name");
		}
		_boards = new BoardList();
		_name = name;
	}

	public int getCount() {
		return _boards.getCount();
	}

	public void setName(String name) {
		if (name == null) {
			throw new NullPointerException("name");
		}
		_name = name;
	}

	public String getName() {
		return _name;
	}

	public BoardList getBoards() {
		return _boards;
	}

	public int hashCode() {
		return super.hashCode();
	}

	public boolean equals(Object obj) {
		if (obj instanceof Category) {
			return equals((Category) obj);
		}
		return false;
	}

	public boolean equals(Category category) {
		synchronized (category.getBoards()) {
			if (category != null && category.getCount() == getCount()) {
				for (int i = 0; i < getCount(); i++) {
					Board board1 = category.getBoards().getBoard(i);
					Board board2 = getBoards().getBoard(i);
					if (!board1.equals(board2)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	public String toString() {
		return getName();
	}
}