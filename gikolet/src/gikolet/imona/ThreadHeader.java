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
public class ThreadHeader extends BBSData {
	private Board	_board;
	private int		_threadNo;
	private String	_subject;
	private int		_resCount;
	
	private int _hashCodeCash = 0;

	protected ThreadHeader(int type, Board board, int threadNo, String subject, int resCount) {
		super(type);

		_board = board;
		_threadNo = threadNo;
		_subject = subject;
		_resCount = resCount;
	}

	public ThreadHeader(Board board, int threadNo, String subject, int resCount) {
		this(BBSData.THREAD_HEADER, board, threadNo, subject, resCount);
	}

	public Board getBoard() {
		return _board;
	}

	public int getThreadNo() {
		return _threadNo;
	}

	public int getResCount() {
		return _resCount;
	}

	public void setResCount(int resCount) {
		_resCount = resCount;
	}

	public String getSubject() {
		return _subject;
	}

	public int hashCode() {
		if(_hashCodeCash == 0){
			_hashCodeCash = ("" + _board.getNo() + _threadNo).hashCode();
		}
		return _hashCodeCash;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof ThreadHeader) {
			return equals((ThreadHeader) obj);
		}
		return false;
	}

	public synchronized boolean equals(ThreadHeader threadHeader) {
		if (threadHeader == null) {
			return false;
		}
		return (threadHeader.getBoard().equals(_board) && threadHeader.getThreadNo() == getThreadNo());
	}

	public String toString() {
		return _board.toString() + ":" + _threadNo + ":" + _subject + ":" + _resCount;
	}
}