/*
 * 作成日: 2005/10/18
 */
package gikolet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author tetsutaro
 */
public class GikoletExtensions {
	private String	_encoding;

	public GikoletExtensions() {
		_encoding = null;
	}

	public boolean isResWritable() {
		return false;
	}

	public void resWrite(String boardTitle, int boardNo, String threadTitle,
			int threadNo) throws IOException {
		throw new IOException("未対応");
	}

	public final String toString(byte[] sjis)
			throws UnsupportedEncodingException {
		return toString(sjis, 0, sjis.length);
	}

	public String toString(byte[] sjis, int offset, int count)
			throws UnsupportedEncodingException {
		return new String(sjis, offset, count, getSJISCharsetName());
	}

	public byte[] toSJIS(String str) throws UnsupportedEncodingException {
		return str.getBytes(getSJISCharsetName());
	}

	private String getSJISCharsetName() throws UnsupportedEncodingException {
		if (_encoding == null) {
			try {
				new String(new byte[0], "SJIS");
				_encoding = "SJIS";
			} catch (UnsupportedEncodingException uee1) {
				try {
					new String(new byte[0], "Shift_JIS");
					_encoding = "Shift_JIS";
				} catch (UnsupportedEncodingException uee2) {
					throw new UnsupportedEncodingException(
							"\"Shift_JIS\" and \"SJIS\" is unsupported Charactor Encoding");
				}
			}
		}
		return _encoding;
	}

	public String toURIEncodeSJIS(String in) {
		StringBuffer inBuf = new StringBuffer(in);
		;
		StringBuffer outBuf = new StringBuffer();
		for (int i = 0; i < inBuf.length(); i++) {
			char temp = inBuf.charAt(i);
			if (('a' <= temp && temp <= 'z') || ('A' <= temp && temp <= 'Z')
					|| ('0' <= temp && temp <= '9') || temp == '.'
					|| temp == '-' || temp == '*' || temp == '_') {
				outBuf.append(temp);
			} else if (temp == ' ') {
				outBuf.append('+');
			} else {
				byte[] bytes;
				try {
					bytes = toSJIS(new String(new char[] { temp }));
					for (int j = 0; j < bytes.length; j++) {
						int high = (bytes[j] >>> 4) & 0x0F;
						int low = (bytes[j] & 0x0F);
						outBuf.append('%');
						outBuf.append(Integer.toString(high, 16).toUpperCase());
						outBuf.append(Integer.toString(low, 16).toUpperCase());
					}
				} catch (Exception e) {
				}
			}
		}

		return outBuf.toString();
	}

	public String toURIDecodeSJIS(String s) throws UnsupportedEncodingException {
		String str = s.replace('+', ' ');

		int i;
		int start = 0;
		byte[] bytes = null;
		int length = str.length();
		StringBuffer result = new StringBuffer(length);
		while ((i = str.indexOf('%', start)) >= 0) {
			result.append(str.substring(start, i));
			start = i;

			while ((i + 2 < length) && (str.charAt(i) == '%')){
				i += 3;
			}

			if ((bytes == null) || (bytes.length < ((i - start) / 3))){
				bytes = new byte[((i - start) / 3)];
			}

			int index = 0;
			try {
				while (start < i) {
					String sub = str.substring(start + 1, start + 3);
					bytes[index] = (byte) Integer.parseInt(sub, 16);
					index++;
					start += 3;
				}
			} catch (NumberFormatException nfe) {
			}
			result.append(toString(bytes, 0, index));

			if (start < length && s.charAt(start) == '%') {
				result.append('%');
				start++;
			}
		}

		if (start < str.length()){
			result.append(str.substring(start));
		}

		return result.toString();
	}
}