/*
 * Created on 2005/01/23 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.base.util;

import java.util.Vector;

/**
 * @author tetsutaro TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class Utilities {
	static final String	kana1	= "ｱｲｳｴｵｧｨｩｪｫｶｷｸｹｺｻｼｽｾｿﾀﾁﾂｯﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖｬｭｮﾗﾘﾙﾚﾛﾜｦﾝｰﾞﾟ､｡";
	static final String	kana2	= "アイウエオァィゥェォカキクケコサシスセソタチツッテトナニヌネノハヒフヘホマミムメモヤユヨャュョラリルレロワヲンー゛゜、。";

	static final String	alpha2	= "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ:/.";
	static final String	alpha1	= "ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ：／．";

	public static String[] tokenToStrings(String value, String delimiter) {
		Vector w = new Vector();
		int start = 0;
		int end = value.indexOf(delimiter);
		for (int h = 0; end >= 0; h++) {
			w.addElement(value.substring(start, end));
			start = end + delimiter.length();
			end = value.indexOf(delimiter, start);
		}
		w.addElement(value.substring(start));
		String[] strs = new String[w.size()];
		w.copyInto(strs);
		return strs;
	}

	public static String replace(String str, String before, String after) {
		StringBuffer sb = new StringBuffer();
		int start = 0;

		while (start < str.length()) {
			int to = str.indexOf(before, start);
			if (to == -1) {
				to = str.length();
				sb.append(str.substring(start, to));
			} else {
				sb.append(str.substring(start, to));
				sb.append(after);
			}
			start = to + before.length();
		}
		return sb.toString();
	}

	public static String escape(String str, boolean ht, boolean lf) {
		StringBuffer sb = new StringBuffer(str);

		for (int i = 0; i < sb.length(); i++) {
			char c = sb.charAt(i);

			if (c == '\t') {
				if (ht) {
					sb.deleteCharAt(i);
					sb.insert(i, "\\t");
					i++;
				}
			} else if (c == '\n') {
				if (lf) {
					sb.deleteCharAt(i);
					sb.insert(i, "\\n");
					i++;
				}
			} else if (c == '\\') {
				int bsCount = 1;
				while (i + bsCount < sb.length()
						&& sb.charAt(i + bsCount) == '\\') {
					bsCount++;
				}
				if (i + bsCount < sb.length()) {
					char c2 = sb.charAt(i + bsCount);
					if ((ht && (c2 == '\t' || c2 == 't'))
							|| (lf && (c2 == '\n' || c2 == 'n'))) {
						for (int j = 0; j < bsCount; j++) {
							sb.insert(i, '\\');
						}
						if ((ht && c2 == '\t') || (lf && c2 == '\n')) {
							i--;
						}
						bsCount *= 2;
					}
				}
				i += bsCount;
			}
		}
		return sb.toString();
	}

	public static String unescape(String str, boolean ht, boolean lf) {
		StringBuffer sb = new StringBuffer(str);

		for (int i = 0; i < sb.length(); i++) {
			char c = sb.charAt(i);

			if (c == '\\') {
				int bsCount = 1;
				while (i + bsCount < sb.length()
						&& sb.charAt(i + bsCount) == '\\') {
					bsCount++;
				}
				if (i + bsCount < sb.length()) {
					char c2 = sb.charAt(i + bsCount);
					if ((ht && c2 == 't') || (lf && c2 == 'n')) {
						if (bsCount % 2 == 0) {
							sb.delete(i, i + bsCount / 2);
						} else {
							sb.delete(i + bsCount / 2, i + bsCount + 1);
							if (c2 == 't') {
								sb.insert(i + bsCount / 2, '\t');
							} else if (c2 == 'n') {
								sb.insert(i + bsCount / 2, '\n');
							}
						}
						bsCount /=2;
					}
				}
				i = i + bsCount;
			}
		}
		return sb.toString();
	}

	public static String toAlphaHalf(String str) {
		StringBuffer str2;
		char kkv;
		str2 = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			kkv = toAlphaHalf(str.charAt(i));
			str2.append(kkv);
		}

		return str2.toString();
	}

	public static char toAlphaHalf(char kana) {
		int index;

		if ((index = alpha1.indexOf(kana)) >= 0) {
			kana = alpha2.charAt(index);
		}
		return kana;
	}

	public static String toKanaFull(String str) {
		StringBuffer str2;
		char kkv;
		str2 = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			kkv = toKanaFull(str.charAt(i));
			if (kkv == '゛') {
				kkv = str2.charAt(str2.length() - 1);
				kkv++;
				str2.deleteCharAt(str2.length() - 1);
			} else if (kkv == '゜') {
				kkv = str2.charAt(str2.length() - 1);
				kkv += 2;
				str2.deleteCharAt(str2.length() - 1);
			}
			str2.append(kkv);

		}

		return str2.toString();
	}

	public static char toKanaFull(char kana) {
		int index;

		if ((index = kana1.indexOf(kana)) >= 0) {
			kana = kana2.charAt(index);
		}
		return kana;
	}
}