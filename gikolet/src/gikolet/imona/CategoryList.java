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
public class CategoryList {
	private Vector	_categorys;

	public CategoryList() {
		_categorys = new Vector();
	}

	public synchronized Category getCategory(int index) {
		return (Category) _categorys.elementAt(index);
	}

	public synchronized int indexOf(Category category) {
		return _categorys.indexOf(category);
	}

	public synchronized boolean contains(Category category) {
		return _categorys.contains(category);
	}

	public synchronized int indexOf(String categoryName) {
		for (int i = 0; i < getCount(); i++) {
			Category category = getCategory(i);

			if (category.getName().equals(categoryName)) {
				return i;
			}
		}
		return -1;
	}

	public synchronized void add(Category category) {
		_categorys.addElement(category);
	}

	public synchronized void addRange(CategoryList categorys) {
		synchronized (_categorys) {
			for (int i = 0; i < categorys.getCount(); i++) {
				add(categorys.getCategory(i));
			}
		}
	}

	public synchronized void insert(Category category, int index) {
		_categorys.insertElementAt(category, index);
	}

	public synchronized void remove(Category category) {
		_categorys.removeElement(category);
	}

	public synchronized void clear() {
		_categorys.removeAllElements();
	}

	public synchronized int getCount() {
		return _categorys.size();
	}
}