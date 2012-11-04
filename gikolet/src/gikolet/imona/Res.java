/*
 * Created on 2005/01/27 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */

package gikolet.imona;

import gikolet.base.util.Utilities;

import java.util.Vector;

/**
 * @author tetsutaro TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class Res extends BBSData {
	private ThreadHeader	_threadHeader;
	private int				_no;
	private String			_name;
	private String			_mailAddress;
	private String			_date;
	private String			_id;
	private String			_body;

	private Vector			_blocks;

	public Res(ThreadHeader threadHeader, int no, String name,
			String mailAddress, String date, String id, String body) {
		super(BBSData.RES);

		_threadHeader = threadHeader;
		_no = no;
		_name = name;
		_mailAddress = mailAddress;
		_date = date;
		_id = id;
		_body = body;
		kaiseki(body);
	}

	private void kaiseki(String str) {
		_blocks = new Vector();

		int curTextOffset = 0;
		for (int i = 0; i < str.length(); i++) {
			URILink uriLink = getURILink(str, i);
			if (uriLink != null) {
				if (curTextOffset < i) {
					_blocks.addElement(new TextBlock(str.substring(
							curTextOffset, i)));
				}
				_blocks.addElement(uriLink);

				curTextOffset = i + uriLink.getText().length();
				i = curTextOffset - 1;
			} else {
				InnerLink link = getInnerLink(str, i, str.length());
				if (link != null) {
					if (curTextOffset < i) {
						_blocks.addElement(new TextBlock(str.substring(
								curTextOffset, i)));
					}

					_blocks.addElement(link);

					curTextOffset = i + link.getText().length();
					i = curTextOffset - 1;
				}
			}
		}
		if (curTextOffset < str.length()) {
			_blocks.addElement(new TextBlock(str.substring(curTextOffset, str
					.length())));
		}
	}

	private InnerLink getInnerLink(String str, int s, int t) {
		int iw = s;
		char c = str.charAt(iw);
		if (c == '>' || c == '＞') {
			iw++;
			if (t <= iw) {
				return null;
			}
			c = str.charAt(iw);
			if (c == '>' || c == '＞') {
				iw++;
			}
		} else {
			return null;
		}

		if (t <= iw) {
			return null;
		}
		c = str.charAt(iw);
		if (!isDigit(c)) {
			return null;
		}

		int j = iw;

		while (true) {
			if (t <= j) {
				break;
			}
			c = str.charAt(j);
			if (isDigit(c)) {
				j++;
			} else {
				break;
			}
		}
		try {
			int so = parseInt(str.substring(iw, j));
			int to = so;

			if (t > j) {
				c = str.charAt(j);
				if (c == '-' || c == '−') {
					if (t > j + 1) {
						int jw = ++j;
						while (true) {
							if (t > j && isDigit(str.charAt(j))) {
								j++;
							} else {
								if (jw < j) {
									to = parseInt(str.substring(jw, j));
								}
								break;
							}
						}
					}
				}
			}

			return new InnerLink(str.substring(s, j), (so < to) ? so : to,
					(so < to) ? to : so);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private boolean isDigit(char c) {
		return ((0x30 <= c && c <= 0x39) || (0xFF10 <= c && c <= 0xFF19));
	}

	private int parseInt(String str) throws NumberFormatException {
		StringBuffer sb = new StringBuffer(str);
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (0xFF10 <= c && c <= 0xFF19) {
				sb.setCharAt(i, (char) (c - (char) 0xFEE0));
			}
		}
		return Integer.parseInt(sb.toString());
	}

	private URILink getURILink(String text, int index) {
		// ttp://
		if (index < 0 || text.length() < index + 6) {
			return null;
		}

		int nowIndex = index;
		char c = text.charAt(nowIndex);
		if (c == 'h' || c == 'ｈ') {
			if (text.length() < ++nowIndex + 6) {
				return null;
			}
		}

		// 冗長的な方が圧縮率が高いんではないだろうかぁぁぁ！！
		// …深夜3時に何をやってるんだろうかorz
		c = text.charAt(nowIndex++);
		if (c != 't' && c != 'ｔ') {
			return null;
		}
		c = text.charAt(nowIndex++);
		if (c != 't' && c != 'ｔ') {
			return null;
		}
		c = text.charAt(nowIndex++);
		if (c != 'p' && c != 'ｐ') {
			return null;
		}
		c = text.charAt(nowIndex++);
		if (c == 's' || c == 'ｓ') {
			if (text.length() < nowIndex + 3) {
				return null;
			}
			c = text.charAt(nowIndex++);
		}
		if (c != ':' && c != '：') {
			return null;
		}
		c = text.charAt(nowIndex++);
		if (c != '/' && c != '／') {
			return null;
		}
		c = text.charAt(nowIndex);
		if (c != '/' && c != '／') {
			return null;
		}

		// 英数字「;」「/」「?」「:」「@」「&」「=」「+」「$」「,」
		// 「-」「_」「.」「!」「~」「*」「'」「(」「)」「%」「#」
		int endIndex;
		while (true) {
			if (text.length() <= nowIndex) {
				endIndex = nowIndex;
				break;
			}
			c = text.charAt(nowIndex);
			if (Character.isDigit(c) || (0x61 <= c && c <= 0x7A)
					|| (0x41 <= c && c <= 0x5A) || c == ';' || c == '/'
					|| c == '?' || c == ':' || c == '@' || c == '&' || c == '='
					|| c == '+' || c == '$' || c == ',' || c == '-' || c == '_'
					|| c == '.' || c == '!' || c == '~' || c == '*'
					|| c == '\'' || c == '(' || c == ')' || c == '%'
					|| c == '#') {
				nowIndex++;
			} else {
				endIndex = nowIndex;
				break;
			}
		}
		String str = text.substring(index, endIndex);
		String uri = Utilities.toAlphaHalf(str);
		if (str.startsWith("ttp")) {
			uri = 'h' + str;
		}
		return new URILink(str, uri);
	}

	public ThreadHeader getThreadHeader() {
		return _threadHeader;
	}

	public int getNo() {
		return _no;
	}

	public String getName() {
		return _name;
	}

	public String getMailAddress() {
		return _mailAddress;
	}

	public String getDate() {
		return _date;
	}

	public String getID() {
		return _id;
	}

	public String getBody() {
		return _body;
	}

	public int getBlockSize() {
		return _blocks.size();
	}

	public TextBlock getBlock(int index) {
		return (TextBlock) _blocks.elementAt(index);
	}

	public String toString() {
		return "no:" + _no + " name:" + _name + " mailAddress:" + _mailAddress
				+ " date:" + _date + " id:" + _id + " body:" + _body;
	}

	public static class TextBlock {
		public final static int	TEXT	= 0;
		String					_text;
		int						_type	= TEXT;

		TextBlock(String text) {
			_text = text;
		}

		public String getText() {
			return _text;
		}

		public int getType() {
			return _type;
		}
	}

	public abstract static class Link extends TextBlock {
		public Link(String text) {
			super(text);
		}
	}

	public static class URILink extends Link {
		public final static int	URI_LINK	= 1;
		private String			_uri;

		URILink(String text, String uri) {
			super(text);
			_uri = uri;
			_type = URI_LINK;
		}

		public String getURI() {
			return _uri;
		}
	}

	public static class InnerLink extends Link {
		public final static int	INNER_LINK	= 2;
		int						_start;
		int						_to;

		InnerLink(String text, int start, int to) {
			super(text);
			_start = start;
			_to = to;
			_type = INNER_LINK;
		}

		public int getStart() {
			return _start;
		}

		public int getTo() {
			return _to;
		}
	}
}