/*
 * Created on 2005/01/24 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.imona;

import gikolet.Gikolet;
import gikolet.base.Toolkit;
import gikolet.base.io.NetworkReader;
import gikolet.base.util.Utilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

/**
 * @author tetsutaro TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class Reader {
	private String		_baseURI;

	private BoardTable	_boardTableCaches;

	private Vector		_networkReaders;
	private Vector		_listeners;

	private Gikolet		_gikolet;

	public Reader(Gikolet gikolet) throws UnsupportedEncodingException {
		_gikolet = gikolet;
		_boardTableCaches = new BoardTable();
		_listeners = new Vector();
		_networkReaders = new Vector();
	}

	public void save(OutputStream out) throws IOException {
		_boardTableCaches.save(out);
	}

	public void load(InputStream in) throws IOException {
		_boardTableCaches.load(in);
	}

	public String getWriteURI(ThreadHeader threadHeader) {
		return _baseURI + "?v=D&m=w&b=" + threadHeader.getBoard().getNo()
				+ "&t=" + threadHeader.getThreadNo();
	}

	// http://は抜かしてある
	public String getBoardURI(int boardNo) throws IOException {
		byte[] bytes = getByteFromNetwork("?v=F&m=U&b=" + boardNo);
		errorChack(bytes[0]);

		return toString(bytes);
	}

	private String toString(byte[] bytes) throws UnsupportedEncodingException {
		return _gikolet.getGikoletExtensions().toString(bytes, 0, bytes.length);
	}

	private String toString(byte[] bytes, int start, int count)
			throws UnsupportedEncodingException {
		return _gikolet.getGikoletExtensions().toString(bytes, start, count);
	}

	private byte[] toSJIS(String str) throws UnsupportedEncodingException {
		return _gikolet.getGikoletExtensions().toSJIS(str);
	}

	public void onlineUpdateCancel() {
		NetworkReader[] networkReaders;
		synchronized (_networkReaders) {
			networkReaders = new NetworkReader[_networkReaders.size()];
			_networkReaders.copyInto(networkReaders);
			_networkReaders.removeAllElements();
		}
		for (int i = 0; i < networkReaders.length; i++) {
			NetworkReader networkReader = networkReaders[i];
			try {
				networkReader.close();
			} catch (IOException e) {
			}
		}
	}

	private byte[] getByteFromNetwork(String post) throws IOException {
		if (_baseURI == null) {
			throw new IOException("ｱｸｾｽする鯖がありません。");
		}

		post = "v=F&p=x&" + post;

		NetworkReader network = Toolkit.getToolkit().createNetworkReader(
				_baseURI);
		System.out.println(post);
		try {
			_networkReaders.addElement(network);
			network.open();

			network.write(toSJIS(post));

			if (!network.isOpen()) {
				throw new IOException("通信中断");
			}

			byte[] bytes;
			try {
				bytes = network.readToEnd();
			} catch (IOException ioe) {
				if (!network.isOpen()) {
					throw new IOException("通信中断");
				}
				throw ioe;
			}
			return bytes;
		} finally {
			boolean r = _networkReaders.removeElement(network);
			if (network.isOpen() && r) {
				network.close();
			}
		}
	}

	public synchronized void addBoardUpdateListener(BoardUpdateListener listener) {
		_listeners.addElement(listener);
	}

	public synchronized void removeBoardUpdateListener(
			BoardUpdateListener listener) {
		_listeners.removeElement(listener);
	}

	private synchronized void fireBoardChanged(String category, Board oldBoard,
			Board newBoard) {
		for (int i = 0; i < _listeners.size(); i++) {
			Object el = _listeners.elementAt(i);
			if (el instanceof BoardUpdateListener) {
				((BoardUpdateListener) el).boardChanged(category, oldBoard,
						newBoard);
			}
		}
	}

	private synchronized void fireBoardAdded(String category, Board newBoard) {
		for (int i = 0; i < _listeners.size(); i++) {
			Object el = _listeners.elementAt(i);
			if (el instanceof BoardUpdateListener) {
				((BoardUpdateListener) el).boardAdded(category, newBoard);
			}
		}
	}

	public void setBaseURI(String baseURI) {
		_baseURI = baseURI;
	}

	public String getBaseURI() {
		return _baseURI;
	}

	public void read(BoardTable boardTable) {
		boardTable.add(_boardTableCaches);
	}

	public void onlineUpdate(BoardTable boardTable) throws IOException {
		BoardTable newTable = new BoardTable();

		String data;
		byte[] bytes = getByteFromNetwork("m=b");
		errorChack(bytes[0]);

		int startOff = 0;
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] == 0x0A) {
				startOff = i + 1;
				break;
			}
		}
		data = toString(bytes, startOff, bytes.length - startOff);

		String[] strs = Utilities.tokenToStrings(data, "\n");

		data = null;
		bytes = null;
		System.gc();

		Vector categoryNames = new Vector();

		int i = 0;
		// カテゴリ名の取得
		while (i < strs.length && !strs[i].equals("")) {
			categoryNames.addElement(strs[i]);
			i++;
		}
		i++;

		// カテゴリと板名からBBSBoardを作る
		int categoryIndex = 0; // カテゴリのインデックス
		while (i < strs.length && !categoryNames.isEmpty()) {
			String categoryName = (String) categoryNames.firstElement();
			categoryNames.removeElement(categoryName);
			Category category = new Category(categoryName);
			newTable.getCategorys().add(category);
			String[] boardNames = Utilities.tokenToStrings(strs[i++], "\t");
			String[] boardNos = Utilities.tokenToStrings(strs[i++], "\t");
			for (int j = 0; j < boardNames.length; j++) {
				Board newBoard = new Board(Integer.parseInt(boardNos[j]),
						boardNames[j]);
				// 板の移転を検知
				// ただし板の名前が変わった場合は無理
				// カテゴリ違いの板番号同じ板が2つあった場合、両方検知される。
				Board oldBoard = _boardTableCaches.getBoardFromName(
						categoryName, newBoard.getName());
				if (oldBoard != null) {
					if (oldBoard.getNo() != newBoard.getNo()) {
						System.out.println(newBoard.getName() + ":"
								+ oldBoard.getNo() + "→" + newBoard);
						fireBoardChanged(categoryName, oldBoard, newBoard);
					}
				} else {
					fireBoardAdded(categoryName, newBoard);
				}
				category.getBoards().add(newBoard);
			}
			categoryIndex++;
		}

		/*
		 * int categoryIndex = 0; // カテゴリのインデックス while (i < strs.length &&
		 * !categoryNames.isEmpty()) { String categoryName = (String)
		 * categoryNames.firstElement();
		 * categoryNames.removeElement(categoryName); Category category = new
		 * Category(categoryName); newTable.getCategorys().add(category);
		 * String[] boardNames = Utilities.tokenToStrings(strs[i++], "\t"); for
		 * (int j = 0; j < boardNames.length; j++) { Board newBoard = new
		 * Board(categoryIndex * 100 + j, boardNames[j]); // 板の移転を検知 //
		 * ただし板の名前が変わった場合は無理 // カテゴリ違いの板番号同じ板が2つあった場合、両方検知される。 Board addedBoard =
		 * newTable.getBoardFromName(boardNames[j], 0); if (addedBoard == null) {
		 * Board oldBoard = _boardTableCaches.getBoardFromName( categoryName,
		 * newBoard.getName()); if (oldBoard != null) { if (oldBoard.getNo() !=
		 * newBoard.getNo()) { System.out.println(newBoard.getName() + ":" +
		 * oldBoard.getNo() + "→" + newBoard); fireBoardChanged(categoryName,
		 * oldBoard, newBoard); } } else { fireBoardAdded(categoryName,
		 * newBoard); } category.getBoards().add(newBoard); } } categoryIndex++; }
		 */

		if (newTable.getCategorys().getCount() > 0) {
			// 新しい板一覧を設定
			_boardTableCaches.clear();
			_boardTableCaches.add(newTable);
		} else {
			throw new IOException("板一覧の更新に失敗しました");
		}

		boardTable.add(newTable);
	}

	public BBSData onlineUpdate(String uri, int threadCount, int resCount,
			Range range) throws IOException {

		byte[] bytes = getByteFromNetwork("m=u&u=" + uri + "&c=b" + threadCount
				+ "r" + resCount);
		errorChack(bytes[0]);

		int startByte = 1;
		int endByte;
		if (bytes[0] == 0x14) {
			// スレ一覧
			endByte = indexOf(bytes, (byte) 0x09, startByte);
			int boardNo = toDecimal(getBytes(bytes, startByte, endByte));

			startByte = endByte + 1;
			endByte = indexOf(bytes, (byte) 0x0A, startByte);
			String boardName = toString(getBytes(bytes, startByte, endByte));

			Board board = new Board(boardNo, boardName);

			startByte = endByte + 1;
			range.start = 1;
			range.to = update(null, board, 1, bytes, startByte);

			return board;
		} else if (bytes[0] == 0x13) {
			// レス
			endByte = indexOf(bytes, (byte) 0x09, startByte);
			int boardNo = toDecimal(getBytes(bytes, startByte, endByte));

			startByte = endByte + 1;
			endByte = indexOf(bytes, (byte) 0x09, startByte);
			int threadNo = toDecimal(getBytes(bytes, startByte, endByte));

			startByte = endByte + 1;
			endByte = indexOf(bytes, (byte) 0x0A, startByte);
			String threadTitle = toString(getBytes(bytes, startByte, endByte));

			Board board = _boardTableCaches.getBoardFromNo(boardNo);
			if (board == null) {
				board = new Board(boardNo, "?");
			}
			ThreadHeader threadHeader = new ThreadHeader(board, threadNo,
					threadTitle, 0);

			startByte = endByte + 1;
			Range r = update(null, threadHeader, bytes, startByte);
			range.start = r.start;
			range.to = r.to;

			return threadHeader;
		} else {
			throw new IOException("何らかのｴﾗｰ");
		}
	}

	public int onlineUpdate(ThreadHeaderList threadHeaders, Board board,
			String find) throws IOException {
		return onlineUpdateCore(threadHeaders, board, -1, -1, find);
	}

	public int onlineUpdate(ThreadHeaderList threadHeaders, Board board,
			int start, int to) throws IOException {
		return onlineUpdateCore(threadHeaders, board, start, to, null);
	}

	private int update(ThreadHeaderList threadHeaders, Board board, int start,
			byte[] bytes, int startByte) throws IOException {
		int endByte;

		// 圧縮後の容量
		endByte = indexOf(bytes, (byte) 0x09, startByte);

		// 圧縮前の容量
		startByte = endByte + 1;
		endByte = indexOf(bytes, (byte) 0x0A, startByte);

		// スレッドの数
		startByte = endByte + 1;
		endByte = startByte + 1;
		int threadCount = toDecimal(getBytes(bytes, startByte, endByte));

		// body
		startByte = endByte;
		int i = 0;
		while (i < threadCount) {
			endByte = indexOf(bytes, (byte) 0x0A, startByte);
			if (endByte == -1) {
				endByte = bytes.length;
			}
			// スレッド番号
			int s = startByte;
			int e = indexOf(bytes, (byte) 0x09, startByte);
			int threadNo = toDecimal(getBytes(bytes, s, e));

			// スレッドタイトル
			s = e + 1;
			e = indexOf(bytes, (byte) 0x09, s);
			String threadSubject = toString(bytes, s, e - s);

			// レスの数
			s = e + 1;
			e = endByte;
			int resCount = toDecimal(getBytes(bytes, s, e));

			threadHeaders.set(new ThreadHeader(board, threadNo, threadSubject,
					resCount), start + i);

			startByte = endByte + 1;
			i++;
		}
		return threadCount;
	}

	private int onlineUpdateCore(ThreadHeaderList threadHeaders, Board board,
			int start, int to, String find) throws IOException {

		String post = "b=" + board.getNo();
		if (start != -1) {
			post += "&c=s" + start;
			if (to != -1) {
				post += "t" + to;
			}
		} else {
			start = 1;
		}
		if (find != null) {
			post += "&w=" + find + "&m=s";
		}

		byte[] bytes = getByteFromNetwork(post);
		errorChack(bytes[0]);

		return update(threadHeaders, board, start, bytes, 0);
	}

	private void errorChack(byte b) throws IOException {
		switch (b) {
			case 0x00:
				throw new IOException("なんらかのｴﾗｰ");
			case 0x01:
				// 読み込もうとしている範囲にレスがない(新しいレスがない)
				throw new IOException("新ﾚｽ無し");
			case 0x02:
				// iMonaサーバ<->2chの通信エラー
				throw new IOException("iMona鯖<->2chの通信ｴﾗｰ");
			case 0x03:
				// 検索で何もヒットしなかった
				throw new IOException("該当無し");
			case 0x04:
				// スレッドはDAT落ち
				throw new IOException("DAT落ち");
			case 0x05:
			case 0x06:
			case 0x07:
			case 0x08:
			case 0x09:
			case 0x0A:
			case 0x0B:
			case 0x0C:
			case 0x0D:
			case 0x0E:
			case 0x0F:
				// 未定義のエラー
				throw new IOException("未定義のｴﾗｰ");
		}
	}

	public Range onlineUpdate(ResList reses, ThreadHeader threadHeader,
			int newCount) throws IOException {
		return onlineUpdate(reses, threadHeader, newCount, -1);
	}

	public Range onlineUpdate(ResList reses, ThreadHeader threadHeader,
			int start, int to) throws IOException {
		String post = "b=" + threadHeader.getBoard().getNo() + "&t="
				+ threadHeader.getThreadNo();
		if (to == -1) {
			post += "&c=l" + start;
		} else {
			post += "&c=s" + start + "t" + to;
		}

		byte[] bytes = getByteFromNetwork(post);
		errorChack(bytes[0]);

		return update(reses, threadHeader, bytes, 0);
	}

	private Range update(ResList reses, ThreadHeader threadHeader,
			byte[] bytes, int startByte) throws IOException {
		int endByte;

		// 圧縮後の容量
		endByte = indexOf(bytes, (byte) 0x09, startByte);

		// 圧縮前の容量
		startByte = endByte + 1;
		endByte = indexOf(bytes, (byte) 0x09, startByte);

		// 開始レス番号
		startByte = endByte + 1;
		endByte = indexOf(bytes, (byte) 0x09, startByte);
		int sResNumber = toDecimal(getBytes(bytes, startByte, endByte));

		// 終了レス番号
		startByte = endByte + 1;
		endByte = indexOf(bytes, (byte) 0x09, startByte);
		int eResNumber = toDecimal(getBytes(bytes, startByte, endByte));

		// そのスレのレス数
		startByte = endByte + 1;
		endByte = indexOf(bytes, (byte) 0x0A, startByte);
		int threadResSize = toDecimal(getBytes(bytes, startByte, endByte));

		// あぼーんは考慮せず
		if (threadHeader.getResCount() < threadResSize) {
			threadHeader.setResCount(threadResSize);
		}

		// body
		int resCount = eResNumber - sResNumber + 1;
		startByte = endByte + 1;
		int i = 0;
		while (i < resCount) {
			endByte = indexOf(bytes, (byte) 0x0A, startByte);
			if (endByte == -1) {
				endByte = bytes.length;
			}
			Res res = getRes(threadHeader, bytes, startByte, endByte,
					sResNumber + i);
			if (reses != null) {
				reses.set(res);
			}

			startByte = endByte + 1;
			i++;
		}

		return new Range(sResNumber, eResNumber);
	}

	private Res getRes(ThreadHeader threadHeader, byte[] value, int start,
			int to, int resNum) throws IOException {
		int s = start;
		int t = to;
		// レスの内容
		int e = indexOf(value, (byte) 0x09, s, to);
		String body = (s >= e) ? "" : toString(value, s, e - s);

		// 名前
		s = e + 1;
		e = indexOf(value, (byte) 0x09, s, to);
		String name = toString(value, s, e - s);

		// メールアドレス
		s = e + 1;
		e = indexOf(value, (byte) 0x09, s, to);
		String mailAddress = (s >= e) ? "" : toString(value, s, e - s);

		// 時間
		s = e + 1;
		e = indexOf(value, (byte) 0x09, s, to);
		if (e == -1) {
			e = t;
		}
		String date = (s >= e) ? "" : toString(value, s, e - s);

		// ID
		s = e + 1;
		String id;
		if (s >= t) {
			id = "";
		} else {
			id = toString(value, s, t - s);
		}
		return new Res(threadHeader, resNum, name, mailAddress, date, id, body);
	}

	// よくわからんからiMonaのソースそのまま…
	private int toDecimal(byte[] num240) {
		if (num240 == null || num240.length == 0) {
			return 0;
		}

		int i = num240.length - 2;
		int j = 240;
		int ret = (num240[i + 1] & 0xFF) - 16;

		while (i >= 0) {
			ret += ((num240[i] & 0xFF) - 16) * j;
			i--;
			j = j * 240;
		}

		return ret;
	}

	private int indexOf(byte[] value, byte ch, int fromIndex) {
		return indexOf(value, ch, fromIndex, value.length);
	}

	private int indexOf(byte[] value, byte ch, int fromIndex, int toIndex) {
		if (value == null || value.length == 0) {
			return -1;
		}
		for (int i = fromIndex; i < value.length && i <= toIndex; i++) {
			if (value[i] == ch) {
				return i;
			}
		}
		return -1;
	}

	private byte[] getBytes(byte[] bytes, int start, int to) {
		// とりあえずエラー処理はしない
		byte[] w = new byte[to - start];
		System.arraycopy(bytes, start, w, 0, w.length);

		return w;
	}
}