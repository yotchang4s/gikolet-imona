/*
 * Created on 2005/02/03
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet;

import gikolet.imona.Board;
import gikolet.imona.ThreadHeader;

/**
 * @author tetsutaro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Bookmark extends ThreadHeader {
	public final static int	BOOKMARK_THREAD_THREADER	= 4;
	private int				_resNo						= 0;
	private int				_readMaxResIndex			= 0;

	public Bookmark(ThreadHeader threadHeader) {
		this(threadHeader, 0, 0);
	}

	public Bookmark(ThreadHeader threadHeader, int resNo, int readMaxResIndex) {
		this(threadHeader.getBoard(), threadHeader.getThreadNo(), threadHeader
				.getSubject(), threadHeader.getResCount(), resNo,
				readMaxResIndex);
	}

	public Bookmark(Board board, int threadNo, String subject, int resCount,
			int resNo, int readMaxResIndex) {
		super(BOOKMARK_THREAD_THREADER, board, threadNo, subject, resCount);
		setReadMaxResIndex(readMaxResIndex);
		setResNo(resNo);
	}

	public void setReadMaxResIndex(int readMaxResIndex) {
		if (readMaxResIndex < _resNo || readMaxResIndex < 0) {
			throw new IllegalArgumentException(readMaxResIndex + " is illegal.");
		}
		_readMaxResIndex = readMaxResIndex;
	}

	public void setResNo(int resNo) {
		if (resNo < 0) {
			throw new IllegalArgumentException();
		}
		_resNo = resNo;
		if (_resNo > _readMaxResIndex) {
			_readMaxResIndex = _resNo;
		}
	}

	public int getResNo() {
		return _resNo;
	}

	public int getReadMaxResIndex() {
		return _readMaxResIndex;
	}
}