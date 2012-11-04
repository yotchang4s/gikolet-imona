package gikolet;

import gikolet.base.ui.Font;
import gikolet.base.ui.Graphics;

/**
 * @author tetsutaro TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class AAModeTable {
	public static void drawString(Graphics g, String str, int x, int y) {
		Font font = g.getFont();
		if (y + font.getHeight() <= g.getClipY()
				|| g.getClipY() + g.getClipHeight() <= y) {
			return;
		}

		// int emWidth = font.charWidth(' ');
		int emWidth = font.getHeight();

		int xx = 0;
		int cx = x;
		for (int i = 0; i < str.length()
				&& cx < g.getClipX() + g.getClipWidth(); i++) {
			char c = str.charAt(i);

			int grif = getGrif(c);
			if (grif == -1) {
				xx += font.charWidth(c) * 1000;
			} else {
				xx += (grif * emWidth * 1000) / 1024;
			}
			int w = x + xx / 1000;
			if (g.getClipX() <= w) {
				g.drawChar(getDrawChar(c), cx, y);
			}
			cx = w;
		}
	}

	private static char getDrawChar(char c) {
		switch (c) {
			case '（':
				return '(';
			case '）':
				return ')';
			case '・':
				return '･';
			case '‐':
				return '-';
			case '´':
				return '′';
			case '｀':
				return '`';
			case '—':
			case '―':
			case '−':
			case '－':
			case '─':
				return '―';
			case '［':
				return '[';
			case '］':
				return ']';
			case '｝':
				return '}';
			case '｛':
				return '{';
			case '「':
				return '｢';
			case '」':
				return '｣';
			case '；':
				return ';';
			case '：':
				return ':';
			case 'w':
				return 'ｗ';
			case 'W':
				return 'Ｗ';
			case 0x201D: // '”'
				return '"';
			// case 0x2018: //'‘':
			// return '‘';
			// 半角で代わりが無い！
			default:
				return c;
		}
	}

	public static int stringWidth(Font font, String str) {
		return stringWidthx1000(font, str) / 1000;
	}

	public static int stringWidthx1000(Font font, String str) {
		int width = 0;
		// int emWidth = font.charWidth('あ');
		int emWidth = font.getHeight();

		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);

			int grif = getGrif(c);
			if (grif == -1) {
				width += font.charWidth(c) * 1000;
			} else {
				width += (grif * emWidth * 1000) / 1024;
			}
		}
		return width;
	}

	public static int charWidth(Font font, char c) {
		return charWidthx1000(font, c) / 1000;
	}

	public static int charWidthx1000(Font font, char c) {
		int grif = getGrif(c);
		if (grif == -1) {
			return font.charWidth(c) * 1000;
		}
		// int emWidth = font.charWidth('あ');
		int emWidth = font.getHeight();

		return (grif * emWidth * 1000) / 1024;
	}

	// 変換表が違う時のための指定も多分してある。。
	// 本当にウニ野郎はファッキンだ！
	private static int getGrif(char c) {
		switch (c) {
			case 0x20: // ' '
				return 320;
			case 0x21: // '!'
				return 256;
			/*
			 * case 0x22: //'"' case 0x23: //'#' case 0x24: //'$' case 0x25:
			 * //'%' return 512;
			 */
			case 0x26: // '&':
				return 640;
			case 0x27: // '\'':
				return 192;
			case 0x28: // '('
			case 0x29: // ')'
				return 320;
			/*
			 * case 0x2A: //'*' case 0x2B: //'+' return 512;
			 */
			case 0x2C: // ','
				return 192;
			case 0x2D: // '-'
				return 512;
			case 0x2E: // '.'
				return 192;
			/*
			 * case 0x2F: //'/' case 0x30: //'0' case 0x31: //'1' case 0x32:
			 * //'2' case 0x33: //'3' case 0x34: //'4' case 0x35: //'5' case
			 * 0x36: //'6' case 0x37: //'7' case 0x38: //'8' case 0x39: //'9'
			 * return 512;
			 */
			case 0x3A: // ':'
			case 0x3B: // ';'
				return 192;
			/*
			 * case 0x3C: //'<' case 0x3D: //'=' case 0x3E: //'>' case 0x3F:
			 * //'?' return 512;
			 */
			case 0x40: // '@'
				return 704;
			case 0x41: // 'A'
			case 0x42: // 'B'
				return 640;
			case 0x43: // 'C'
				return 704;
			case 0x44: // 'D'
				return 640;
			case 0x45: // 'E'
			case 0x46: // 'F'
				return 576;
			case 0x47: // 'G'
				return 704;
			case 0x48: // 'H'
				return 640;
			case 0x49: // 'I'
				return 256;
			case 0x4A: // 'J'
				return 576;
			case 0x4B: // 'K'
				return 640;
			case 0x4C: // 'L'
				return 576;
			case 0x4D: // 'M'
				return 768;
			case 0x4E: // 'N'
				return 640;
			case 0x4F: // 'O'
				return 704;
			case 0x50: // 'P'
				return 640;
			case 0x51: // 'Q'
				return 704;
			case 0x52: // 'R'
			case 0x53: // 'S'
				return 640;
			case 0x54: // 'T'
				return 576;
			case 0x55: // 'U'
			case 0x56: // 'V'
				return 640;
			case 0x57: // 'W'
				// モナーフォントだと704
				// でも実際はこれが正しいらしい…
				return 768;
			case 0x58: // 'X'
				return 640;
			case 0x59: // 'Y'
			case 0x5A: // 'Z'
				return 576;
			case 0x5B: // '['
				return 320;
			/*
			 * case 0x5C: //'\\' return 512;
			 */
			case 0x5D: // ']':
				return 320;
			case 0x5E: // '^':
				return 448;
			case 0x5F: // '_':
				return 320;
			case 0x60: // '`'
				return 448;
			/*
			 * case 0x61: //'a' case 0x62: //'b' case 0x63: //'c' case 0x64:
			 * //'d' case 0x65: //'e' return 512;
			 */
			case 0x66: // 'f'
				return 320;
			case 0x67: // 'g'
				return 448;
			/*
			 * case 0x68: //'h' return 512;
			 */
			case 0x69: // 'i'
				return 192;
			case 0x6A: // 'j'
				return 256;
			case 0x6B: // 'k'
				return 448;
			case 0x6C: // 'l'
				return 192;
			case 0x6D: // 'm'
				return 768;
			/*
			 * case 0x6E: //'n' case 0x6F: //'o' case 0x70: //'p' case 0x71:
			 * //'q' return 512;
			 */
			case 0x72: // 'r'
				return 384;
			case 0x73: // 's'
				return 448;
			case 0x74: // 't'
				return 384;
			/*
			 * case 0x75: //'u' case 0x76: //'v' return 512;
			 */
			case 0x77: // 'w'
				// モナーフォントは640
				// これもズレてるような…
				// 704:768=640:x
				return 698;
			case 0x78: // 'x'
				return 448;
			case 0x79: // 'y'
				return 576;
			case 0x7A: // 'z'
				return 448;
			case 0x7B: // '{'
			case 0x7C: // '|'
			case 0x7D: // '}'
				return 256;
			case 0x7E: // '~'
				return 448;
			case 0xA8: // '¨'
				return 512;
			case 0xB4: // '´'
				return 512;
			case 0x2009: // 細いスペースらしい…
				return 128;
			case 0x2010: // '‐'
				return 512;
			case 0x2014: // '—':
			case 0x2015: // '―'
				return 1024;
			case 0x2018: // '‘'
			case 0x2019: // '’'
			case 0x201D: // '”'
				return 512;
			case 0x2122: // '™'
				return 448;
			case 0x2212: // '−'
			case 0x2500: // '─':
				return 1024;
			case 0x3000: // ' '
			case 0x3001: // '、'
			case 0x3002: // '。'
				return 704;
			/*
			 * case 0x3003: //'〃': return 1024;
			 */
			case 0x3008: // '〈'
			case 0x3009: // '〉'
			case 0x300A: // '《'
			case 0x300B: // '》'
			case 0x300C: // '「'
			case 0x300D: // '」'
			case 0x300E: // '『'
			case 0x300F: // '』'
			case 0x3010: // '【'
			case 0x3011: // '】'
			case 0x3014: // '〔'
			case 0x3015: // '〕'
				return 512;

			case 0x3041: // 'ぁ'
				return 768;
			case 0x3042: // 'あ'
				return 960;
			case 0x3043: // 'ぃ'
				return 832;
			case 0x3044: // 'い'
				return 960;
			case 0x3045: // 'ぅ'
				return 640;
			case 0x3046: // 'う'
				return 704;
			case 0x3047: // 'ぇ'
				return 768;
			case 0x3048: // 'え'
				return 896;
			case 0x3049: // 'ぉ'
				return 832;
			case 0x304A: // 'お'
				return 960;
			/*
			 * case 0x304B: //'か' return 1024;
			 */
			/*
			 * case 0x304C: //'が' return 1024;
			 */
			case 0x304D: // 'き'
				return 896;
			case 0x304E: // 'ぎ'
				return 896;
			case 0x304F: // 'く'
				return 576;
			case 0x3050: // 'ぐ'
				return 832;
			case 0x3051: // 'け'
				return 960;
			/*
			 * case 0x3052: //'げ' return 1024;
			 */
			case 0x3053: // 'こ'
				return 832;
			case 0x3054: // 'ご'
				return 896;
			case 0x3055: // 'さ'
				return 768;
			case 0x3056: // 'ざ'
				return 896;
			case 0x3057: // 'し'
				return 768;
			case 0x3058: // 'じ'
				return 768;
			case 0x3059: // 'す'
				return 960;
			/*
			 * case 0x305A: //'ず' case 0x305B: //'せ' case 0x305C: //'ぜ' return
			 * 1024;
			 */
			case 0x305D: // 'そ'
			case 0x305E: // 'ぞ'
			case 0x305F: // 'た'
			case 0x3060: // 'だ'
				return 960;
			case 0x3061: // 'ち'
			case 0x3062: // 'ぢ'
				return 896;
			case 0x3063: // 'っ'
				return 832;
			case 0x3064: // 'つ'
				return 960;
			case 0x3065: // 'づ'
				return 960;
			case 0x3066: // 'て'
			case 0x3067: // 'で'
				return 896;
			case 0x3068: // 'と'
				return 768;
			case 0x3069: // 'ど'
				return 896;
			case 0x306A: // 'な'
				return 896;
			case 0x306B: // 'に'
				return 960;
			/*
			 * case 0x306C: //'ぬ' case 0x306D: //'ね' case 0x306E: //'の' case
			 * 0x306F: //'は' case 0x3070: //'ば' case 0x3071: //'ぱ' return 1024;
			 */
			case 0x3072: // 'ひ'
			case 0x3073: // 'び'
			case 0x3074: // 'ぴ'
				return 960;
			/*
			 * case 0x3075: //'ふ' case 0x3076: //'ぶ' case 0x3077: //'ぷ' case
			 * 0x3078: //'へ' case 0x3079: //'べ' case 0x307A: //'ぺ' case 0x307B:
			 * //'ほ' case 0x307C: //'ぼ' case 0x307D: //'ぽ' return 1024;
			 */
			case 0x307E: // 'ま'
				return 896;
			/*
			 * case 0x307F: //'み' case 0x3080: //'む' case 0x3081: //'め' return
			 * 1024;
			 */
			case 0x3082: // 'も'
				return 832;
			case 0x3083: // 'ゃ'
				return 896;
			/*
			 * case 0x3084: //'や' return 1024;
			 */
			case 0x3085: // 'ゅ'
				return 896;
			/*
			 * case 0x3086: //'ゆ' return 1024;
			 */
			case 0x3087: // 'ょ'
				return 768;
			case 0x3088: // 'よ'
				return 896;
			case 0x3089: // 'ら'
				return 832;
			case 0x308A: // 'り'
				return 768;
			case 0x308B: // 'る'
				return 896;
			/*
			 * case 0x308C: //'れ' return 1024;
			 */
			case 0x308D: // 'ろ'
			case 0x308E: // 'ゎ'
				return 896;
			/*
			 * case 0x308F: //'わ' case 0x3090: //'ゐ' case 0x3091: //'ゑ' return
			 * 1024;
			 */
			case 0x3092: // 'を'
				return 896;
			case 0x3093: // 'ん'
				return 960;

			case 0x309B: // '゛'
			case 0x309C: // '゜'
				return 512;
			case 0x309D: // 'ゝ'
				return 768;
			case 0x309E: // 'ゞ'
				return 704;

			case 0x30A1: // 'ァ'
				return 768;
			case 0x30A2: // 'ア'
				return 869;
			case 0x30A3: // 'ィ'
				return 640;
			case 0x30A4: // 'イ'
				return 832;
			case 0x30A5: // 'ゥ'
				return 768;
			case 0x30A6: // 'ウ'
				return 960;
			case 0x30A7: // 'ェ'
				return 768;
			case 0x30A8: // 'エ'
				return 896;
			case 0x30A9: // 'ォ'
				return 832;
			case 0x30AA: // 'オ'
				return 960;
			case 0x30AB: // 'カ'
				return 832;
			case 0x30AC: // 'ガ'
			case 0x30AD: // 'キ'
			case 0x30AE: // 'ギ'
				return 960;
			case 0x30AF: // 'ク'
				return 832;
			case 0x30B0: // 'グ'
			case 0x30B1: // 'ケ'
			case 0x30B2: // 'ゲ'
				return 960;
			case 0x30B3: // 'コ'
				return 832;
			case 0x30B4: // 'ゴ'
				return 896;
			/*
			 * case 0x30B5: //'サ' case 0x30B6: //'ザ' return 1024;
			 */
			case 0x30B7: // 'シ'
			case 0x30B8: // 'ジ'
			case 0x30B9: // 'ス'
				return 896;
			case 0x30BA: // 'ズ'
			case 0x30BB: // 'セ'
				return 960;
			/*
			 * case 0x30BC: //'ゼ' return 1024;
			 */
			case 0x30BD: // 'ソ'
				return 832;
			case 0x30BE: // 'ゾ'
				return 896;
			case 0x30BF: // 'タ'
				return 768;
			case 0x30C0: // 'ダ'
			case 0x30C1: // 'チ'
			case 0x30C2: // 'ヂ'
				return 960;
			case 0x30C3: // 'ッ'
				return 768;
			case 0x30C4: // 'ツ'
				return 896;
			case 0x30C5: // 'ヅ'
				return 960;
			case 0x30C6: // 'テ'
				return 896;
			case 0x30C7: // 'デ'
				return 960;
			case 0x30C8: // 'ト'
				return 640;
			case 0x30C9: // 'ド'
				return 704;
			case 0x30CA: // 'ナ'
			case 0x30CB: // 'ニ'
				return 960;
			case 0x30CC: // 'ヌ'
				return 832;
			case 0x30CD: // 'ネ'
				return 960;
			case 0x30CE: // 'ノ'
				return 704;
			/*
			 * case 0x30CF: //'ハ' case 0x30D0: //'バ' case 0x30D1: //'パ' return
			 * 1024;
			 */
			case 0x30D2: // 'ヒ'
				return 768;
			case 0x30D3: // 'ビ'
			case 0x30D4: // 'ピ'
				return 896;
			case 0x30D5: // 'フ'
				return 832;
			case 0x30D6: // 'ブ'
			case 0x30D7: // 'プ'
				return 896;
			case 0x30D8: // 'ヘ'
			case 0x30D9: // 'ベ'
			case 0x30DA: // 'ペ'
			case 0x30DB: // 'ホ'
			case 0x30DC: // 'ボ'
			case 0x30DD: // 'ポ'
				return 960;
			case 0x30DE: // 'マ'
				return 896;
			case 0x30DF: // 'ミ'
				return 704;
			/*
			 * case 0x30E0: //'ム' return 1024;
			 */
			case 0x30E1: // 'メ'
				return 768;
			case 0x30E2: // 'モ'
				return 896;
			case 0x30E3: // 'ャ'
				return 832;
			/*
			 * case 0x30E4: //'ヤ' return 1024;
			 */
			case 0x30E5: // 'ュ'
				return 832;
			case 0x30E6: // 'ユ'
				return 960;
			case 0x30E7: // 'ョ':
				return 640;
			case 0x30E8: // 'ヨ'
				return 768;
			case 0x30E9: // 'ラ'
				return 832;
			case 0x30EA: // 'リ'
				return 768;
			/*
			 * case 0x30EB: //'ル' return 1024;
			 */
			case 0x30EC: // 'レ'
			case 0x30ED: // 'ロ'
				return 896;
			case 0x30EE: // 'ヮ'
				return 768;
			case 0x30EF: // 'ワ'
				return 960;
			/*
			 * case 0x30F0: //'ヰ' case 0x30F1: //'ヱ' return 1024;
			 */
			case 0x30F2: // 'ヲ'
				return 832;
			case 0x30F3: // 'ン'
				return 896;
			case 0x30F4: // 'ヴ'
				return 960;
			case 0x30F5: // 'ヵ'
			case 0x30F6: // 'ヶ'
				return 760;

			case 0x30FB: // '・'
				return 512;
			case 0x30FC: // 'ー'
				return 960;
			case 0x30FD: // 'ヽ'
			case 0x30FE: // 'ヾ'
				return 768;
		}
		switch (c) {
			case 0xFF01: // '！'
				return 1024;
			case 0xFF08: // '（'
			case 0xFF09: // '）'
				return 512;
			case 0xFF0C: // '，'
				return 704;
			case 0xFF0D: // '－'
				return 1024;
			case 0xFF0E: // '．'
			case 0xFF10: // '０'
			case 0xFF11: // '１'
			case 0xFF12: // '２'
			case 0xFF13: // '３'
			case 0xFF14: // '４'
			case 0xFF15: // '５'
			case 0xFF16: // '６'
			case 0xFF17: // '７'
			case 0xFF18: // '８'
			case 0xFF19: // '９'
				return 704;
			case 0xFF1A: // '：'
			case 0xFF1B: // '；'
				return 512;
			case 0xFF1F: // '？'
				return 1024;
			case 0xFF21: // 'Ａ'
				return 704;
			case 0xFF22: // 'Ｂ'
				return 832;
			case 0xFF23: // 'Ｃ'
			case 0xFF24: // 'Ｄ'
				return 768;
			case 0xFF25: // 'Ｅ'
				return 704;
			case 0xFF26: // 'Ｆ'
				return 640;
			case 0xFF27: // 'Ｇ'
			case 0xFF28: // 'Ｈ'
				return 768;
			case 0xFF29: // 'Ｉ'
				return 256;
			case 0xFF2A: // 'Ｊ'
				return 640;
			case 0xFF2B: // 'Ｋ'
				return 768;
			case 0xFF2C: // 'Ｌ'
				return 704;
			case 0xFF2D: // 'Ｍ'
				return 960;
			case 0xFF2E: // 'Ｎ'
				return 768;
			case 0xFF2F: // 'Ｏ'
				return 832;
			case 0xFF30: // 'Ｐ'
				return 704;
			case 0xFF31: // 'Ｑ'
				return 832;
			case 0xFF32: // 'Ｒ'
			case 0xFF33: // 'Ｓ'
				return 768;
			case 0xFF34: // 'Ｔ'
				return 640;
			case 0xFF35: // 'Ｕ'
				return 768;
			case 0xFF36: // 'Ｖ'
				return 704;
			/*
			 * case 0xFF37: //'Ｗ' return 1024;
			 */
			case 0xFF38: // 'Ｘ'
			case 0xFF39: // 'Ｙ'
			case 0xFF3A: // 'Ｚ'
				return 640;
			case 0xFF3B: // '［'
			// case 0xFF3C: //'＼'
			case 0xFF3D: // '］'
			case 0xFF3E: // '＾'
				return 512;
			case 0xFF3F: // '＿'
				return 1024;
			case 0xFF40: // '｀'
				return 512;
			case 0xFF41: // 'ａ'
				return 576;
			case 0xFF42: // 'ｂ'
				return 640;
			case 0xFF43: // 'ｃ'
				return 576;
			case 0xFF44: // 'ｄ'
				return 640;
			case 0xFF45: // 'ｅ'
				return 576;
			case 0xFF46: // 'ｆ'
				return 320;
			case 0xFF47: // 'ｇ'
				return 576;
			case 0xFF48: // 'ｈ'
				return 640;
			case 0xFF49: // 'ｉ'
			case 0xFF4A: // 'ｊ'
				return 256;
			case 0xFF4B: // 'ｋ'
				return 640;
			case 0xFF4C: // 'ｌ'
				return 256;
			case 0xFF4D: // 'ｍ'
				return 960;
			case 0xFF4E: // 'ｎ'
			case 0xFF4F: // 'ｏ'
			case 0xFF50: // 'ｐ'
			case 0xFF51: // 'ｑ'
				return 640;
			case 0xFF52: // 'ｒ'
				return 384;
			case 0xFF53: // 'ｓ'
				return 576;
			case 0xFF54: // 'ｔ'
				return 320;
			case 0xFF55: // 'ｕ'
				return 640;
			case 0xFF56: // 'ｖ'
				return 512;
			case 0xFF57: // 'ｗ'
				return 768;
			case 0xFF58: // 'ｘ'
				return 511;
			case 0xFF59: // 'ｙ'
			case 0xFF5A: // 'ｚ'
				return 512;
			case 0xFF5B: // '｛'
			case 0xFF5D: // '｝'
				return 512;
			case 0xFF61: // '｡'
			case 0xFF62: // '｢'
			case 0xFF63: // '｣'
			case 0xFF64: // '､'
			case 0xFF65: // '･'
				return 448;
			case 0xFF66: // 'ｦ'
				return 576;
			/*
			 * case 0xFF67: //'ｧ' case 0xFF68: //'ｨ' case 0xFF69: //'ｩ' case
			 * 0xFF6A: //'ｪ' case 0xFF6B: //'ｫ' case 0xFF6C: //'ｬ' case 0xFF6D:
			 * //'ｭ' case 0xFF6E: //'ｮ' case 0xFF6F: //'ｯ' case 0xFF70: //'ｰ'
			 * return 512;
			 */
			case 0xFF71: // 'ｱ'
				return 640;
			case 0xFF72: // 'ｲ'
				return 576;
			case 0xFF73: // 'ｳ'
			case 0xFF74: // 'ｴ'
			case 0xFF75: // 'ｵ'
				return 640;
			case 0xFF76: // 'ｶ'
				return 576;
			case 0xFF77: // 'ｷ'
				return 640;
			case 0xFF78: // 'ｸ'
				return 576;
			case 0xFF79: // 'ｹ'
				return 640;
			case 0xFF7A: // 'ｺ'
				return 576;
			case 0xFF7B: // 'ｻ'
				return 704;
			case 0xFF7C: // 'ｼ'
				return 576;
			case 0xFF7D: // 'ｽ'
			case 0xFF7E: // 'ｾ'
				return 640;
			case 0xFF7F: // 'ｿ'
			case 0xFF80: // 'ﾀ'
				return 576;
			case 0xFF81: // 'ﾁ'
				return 640;
			case 0xFF82: // 'ﾂ'
				return 576;
			case 0xFF83: // 'ﾃ'
				return 640;
			case 0xFF84: // 'ﾄ'
				return 448;
			case 0xFF85: // 'ﾅ'
			case 0xFF86: // 'ﾆ'
				return 640;
			case 0xFF87: // 'ﾇ'
				return 576;
			case 0xFF88: // 'ﾈ'
				return 640;
			/*
			 * case 0xFF89: //'ﾉ' return 512;
			 */
			case 0xFF8A: // 'ﾊ'
				return 640;
			/*
			 * case 0xFF8B: //'ﾋ' return 512;
			 */
			case 0xFF8C: // 'ﾌ'
				return 576;
			case 0xFF8D: // 'ﾍ'
			case 0xFF8E: // 'ﾎ'
			case 0xFF8F: // 'ﾏ'
				return 640;
			case 0xFF90: // 'ﾐ'
				return 448;
			case 0xFF91: // 'ﾑ'
				return 704;
			/*
			 * case 0xFF91: //'ﾒ' return 512;
			 */
			case 0xFF92: // 'ﾓ'
				return 640;
			case 0xFF93: // 'ﾔ'
				return 704;
			case 0xFF94: // 'ﾕ'
				return 640;
			/*
			 * case 0xFF95: //'ﾖ' return 512;
			 */
			case 0xFF96: // 'ﾗ'
				return 576;
			/*
			 * case 0xFF97: //'ﾘ' return 512;
			 */
			case 0xFF98: // 'ﾙ'
				return 704;
			case 0xFF99: // 'ﾚ'
			case 0xFF9A: // 'ﾛ'
			case 0xFF9B: // 'ﾜ'
			case 0xFF9C: // 'ﾝ'
				return 576;
			case 0xFF9E: // 'ﾞ'
			case 0xFF9F: // 'ﾟ'
				return 256;
			case 0xFFE3: // '￣':
				return 1024;
		}
		return -1;
	}
}