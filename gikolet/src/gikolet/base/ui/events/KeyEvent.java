package gikolet.base.ui.events;

import gikolet.base.ui.Control;

/**
 * @author 鉄太郎
 */
public class KeyEvent extends InputEvent {
	public final static int	KEY_NON		= 1000;

	public final static int	KEY_NUM0	= 0;
	public final static int	KEY_NUM1	= 1;
	public final static int	KEY_NUM2	= 2;
	public final static int	KEY_NUM3	= 3;
	public final static int	KEY_NUM4	= 4;
	public final static int	KEY_NUM5	= 5;
	public final static int	KEY_NUM6	= 6;
	public final static int	KEY_NUM7	= 7;
	public final static int	KEY_NUM8	= 8;
	public final static int	KEY_NUM9	= 9;
	public final static int	KEY_POUND	= 10;
	public final static int	KEY_STAR	= 11;

	public final static int	UP			= 12;
	public final static int	DOWN		= 13;
	public final static int	LEFT		= 14;
	public final static int	RIGHT		= 15;

	public final static int	ENTER		= 16;

	public final static int	CLEAR		= 17;

	/*
	 * public final static int GAME_A = 17; public final static int GAME_B = 18;
	 * public final static int GAME_C = 19; public final static int GAME_D = 20;
	 */

	public final static int	PRESSED		= 100;
	public final static int	REPEATED	= 101;
	public final static int	RELEASED	= 102;

	private int				keyActionType;
	private int				keyCode;

	public KeyEvent(Control source, int keyActionType, int keyCode) {
		super(source);
		if (keyActionType < 100 || 102 < keyActionType) {
			throw new IllegalArgumentException("keyActionType");
		}
		if (keyCode < 0 || 17 < keyCode) {

		}
		this.keyActionType = keyActionType;
		this.keyCode = keyCode;
	}

	public int getKeyActionType() {
		return this.keyActionType;
	}

	public int getKeyCode() {
		return this.keyCode;
	}

	public static String getCodeName(int code) {
		switch (code) {
			case PRESSED:
				return "PRESSED";
			case REPEATED:
				return "REPEATED";
			case RELEASED:
				return "RELEASED";
			case UP:
				return "UP";
			case DOWN:
				return "DOWN";
			case LEFT:
				return "LEFT";
			case RIGHT:
				return "RIGHT";
			case ENTER:
				return "FIRE";
			case KEY_NUM0:
				return "KEY_NUM0";
			case KEY_NUM1:
				return "KEY_NUM1";
			case KEY_NUM2:
				return "KEY_NUM2";
			case KEY_NUM3:
				return "KEY_NUM3";
			case KEY_NUM4:
				return "KEY_NUM4";
			case KEY_NUM5:
				return "KEY_NUM5";
			case KEY_NUM6:
				return "KEY_NUM6";
			case KEY_NUM7:
				return "KEY_NUM7";
			case KEY_NUM8:
				return "KEY_NUM8";
			case KEY_NUM9:
				return "KEY_NUM9";
			case KEY_POUND:
				return "KEY_POUND";
			case KEY_STAR:
				return "KEY_STAR";
			case CLEAR:
				return "CLEAR";
			case KEY_NON:
				return "KEY_NON";
			default:
				return "not support";
		}
	}
}