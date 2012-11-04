/*
 * 作成日： 2004/11/18
 *
 * TODO この生成されたファイルのテンプレートを変更するには次を参照。
 * ウィンドウ ＞ 設定 ＞ Java ＞ コード・スタイル ＞ コード・テンプレート
 */
package gikolet.base.ui;

import java.util.Vector;

/**
 * @author tetsutaro
 *
 * TODO この生成された型コメントのテンプレートを変更するには次を参照。
 * ウィンドウ ＞ 設定 ＞ Java ＞ コード・スタイル ＞ コード・テンプレート
 */
public class Utilities {
	//サイズを調整する
	//位置よりもサイズ優先
	public static Dimension getPreferredSize(Control control, int width, int height) {
		Dimension d = control.getPreferredSize(Control.DEFAULT, Control.DEFAULT);

		int hintWidth = Control.DEFAULT;
		int hintHeight = Control.DEFAULT;
		if (d.getWidth() > width) {
			hintWidth = width;
		}
		if (d.getHeight() > height) {
			hintHeight = height;
		}
		if (hintWidth != Control.DEFAULT || hintHeight != Control.DEFAULT) {
			d = control.getPreferredSize(hintWidth, hintHeight);
		}

		d.setSize(Math.min(d.getWidth(), width), Math.min(d.getHeight(), height));
		return d;
	}

	public static String[] split(Font font, String str, int width) {
		if (str.length() == 0) {
			return new String[] { "" };
		}
		Vector vw = new Vector();
		StringBuffer sb = new StringBuffer();
		int x = 0;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			int cw = font.charWidth(c);

			/*if (x + cw >= width) {
			 if (sb.length() == 0) {
			 vw.addElement(String.valueOf(c));
			 continue;
			 }
			 vw.addElement(sb.toString());
			 sb.delete(0, sb.length());
			 x = 0;
			 }*/
			if (x == 0 && cw > width) {
				vw.addElement(String.valueOf(c));
				continue;
			}
			if (x + cw > width) {
				vw.addElement(sb.toString());
				sb.delete(0, sb.length());
				x = 0;
			}

			sb.append(c);
			x += cw;
		}
		if (sb.length() > 0) {
			vw.addElement(sb.toString());
		}
		String[] lines = new String[vw.size()];
		vw.copyInto(lines);

		return lines;

		/*Vector vw = new Vector();
		 StringBuffer sb = new StringBuffer();
		 int x = ax;
		 for (int i = 0; i < str.length(); i++) {
		 char c = str.charAt(i);
		 int cw = font.charWidth(c);

		 if (x + cw >= width) {
		 if (sb.length() == 0) {
		 vw.addElement(String.valueOf(c));
		 continue;
		 }
		 vw.addElement(sb.toString());
		 sb.delete(0, sb.length());
		 x = 0;
		 }
		 sb.append(c);
		 x += cw;
		 }
		 if (sb.length() > 0) {
		 vw.addElement(sb.toString());
		 }
		 String[] lines = new String[vw.size()];
		 vw.copyInto(lines);

		 return lines;*/
	}

	public static int getLineBreak(Font font, String str, int off, int len, int width) {
		int l = 1;
		if (str.length() == 0) {
			return off;
		}
		int w = font.charWidth(str.charAt(off));
		if (w >= width) {
			return off + 1;
		}
		while (off + l < str.length() && l <= len) {
			int ww = font.substringWidth(str, off, l);
			if (ww > width) {
				l--;
				break;
			}
			l++;
		}
		return off + l;
	}

	public static String[] getLines(Font font, String text, int width) {
		Vector w = new Vector();
		String[] lines;
		width++;

		int s = 0;
		while (s < text.length()) {
			int t = Utilities.getLineBreak(font, text, s, text.length() - s, width);
			w.addElement(text.substring(s, t));
			s = t;
		}
		lines = new String[w.size()];
		w.copyInto(lines);
		return lines;
	}

	public static String[] getLinesInNewLine(Font font, String text, int width) {
		Vector w = new Vector();
		String[] lines;
		width++;

		int s = 0;
		while (s < text.length()) {
			int t = Utilities.getLineBreak(font, text, s, text.length() - s, width);
			System.out.println(t);
			w.addElement(text.substring(s, t));
			s = t;
		}
		lines = new String[w.size()];
		w.copyInto(lines);
		return lines;
	}
}