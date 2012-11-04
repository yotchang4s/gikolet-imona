/*
 * Created on 2005/02/06 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet;

import gikolet.base.Toolkit;
import gikolet.base.ui.CancelException;
import gikolet.base.ui.OptionPane;
import gikolet.base.ui.Dialog;
import gikolet.base.ui.Dimension;
import gikolet.base.ui.Display;
import gikolet.base.ui.Font;
import gikolet.base.ui.List;
import gikolet.base.ui.MenuItem;
import gikolet.base.ui.Shell;
import gikolet.base.ui.events.ActionEvent;
import gikolet.base.ui.events.ActionListener;
import gikolet.base.ui.events.KeyEvent;
import gikolet.base.ui.events.KeyEventListener;
import gikolet.base.util.Utilities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 * @author tetsutaro TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ConfigDialog extends Dialog implements Config {
	private Gikolet				_gikolet;
	private List				_list;

	private final static String	FONT_FACE			= "FontFace";
	private final static String	FONT_SIZE			= "FontSize";
	private final static String	FONT_STYLE			= "FontStyle";

	private final static String	THREAD_READ_SIZE	= "ThreadReadSize";
	private final static String	RES_READ_SIZE		= "ResReadSize";
	private final static String	RES_NEW_READ_SIZE	= "ResNewReadSize";
	private final static String	RES_SCROLL_WEIGHT	= "ResScrollWeight";
	private final static String	RES_MOVE_METHOD		= "ResMoveMethod";
	private final static String	RES_SHOW_METHOD		= "ResShowMethod";

	private final static String	TAB_TITLE_LENGTH	= "TabTitleLength";
	private final static String	ROOT_TABBAR_SHOW	= "RootTabBarShow";

	private final static String	SERVER				= "Server";
	private final static String	USER_DEFINE_SERVER	= "UserDefineServer";

	private String[]			_defaultSevers;
	private Font				_font				= Font.getDefaultFont();
	private int					_readThreadCount;
	private int					_readResCount;
	private int					_newResReadCount;
	private int					_resScrollWeight;
	private int					_resMoveMethod;
	private int					_tabThreadTitleSize;
	private boolean				_topTabBarShow;
	private int					_resArrangement;
	private String				_sever;
	private String				_userSever;

	public ConfigDialog(Gikolet gikolet, Shell shell, String[] defaultSevers) {
		super(shell, "設定", false);

		_gikolet = gikolet;
		_defaultSevers = defaultSevers;
		init();

		_list = new List();
		_list.add("ﾌｫﾝﾄ");
		_list.add("1回に読むｽﾚ数");
		_list.add("1回に読むﾚｽ数");
		_list.add("最新で読むﾚｽ数");
		_list.add("ﾚｽのｽｸﾛｰﾙ量");
		_list.add("ﾚｽの移動方法");
		_list.add("ﾚｽの表示方法");
		_list.add("ｽﾚﾀﾌﾞの文字数");
		_list.add("ﾙｰﾄﾀﾌﾞﾊﾞｰの表示");
		_list.add("ｱｸｾｽする鯖");
		_list.add("設定を今すぐ保存");
		_list.add("初期化");

		add(_list);

		// setLayoutManager(null);

		_list.addKeyEventListener(new KeyEventListener() {
			public void keyAction(KeyEvent e) {
				if (e.getKeyActionType() == KeyEvent.PRESSED
						&& (e.getKeyCode() == KeyEvent.ENTER || e.getKeyCode() == KeyEvent.KEY_NUM5)) {
					listClick();
				}
			}
		});

		final MenuItem closeMenuItem = new MenuItem("閉じる");
		closeMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		setMenuItem(closeMenuItem);
	}

	private void init() {
		_font = Font.getDefaultFont();
		_tabThreadTitleSize = 0;
		_readThreadCount = 15;
		_readResCount = 30;
		_newResReadCount = 10;
		_resScrollWeight = 30;
		_resMoveMethod = RES_MOVE_METHOD_UP_DONW_LEFT_RIGHT;
		_topTabBarShow = true;
		_resArrangement = 0;
		if (_defaultSevers.length > 0) {
			_sever = _defaultSevers[0];
		}
		_userSever = "";

		_gikolet.fontChanged(_font);
		_gikolet.resScrollWeightChanged(_resScrollWeight);
		_gikolet.getReader().setBaseURI(_sever);
		_gikolet.threadTabTextLengthChanged(_tabThreadTitleSize);
		_gikolet.topTabBarShowChanged(_topTabBarShow);
		_gikolet.setResShowMethod(_resArrangement);
	}

	public int getResMoveMethod() {
		return _resMoveMethod;
	}

	public int getResShowMethod() {
		return _resArrangement;
	}

	public int getThreadTabTextLength() {
		return _tabThreadTitleSize;
	}

	public int getThreadHeaderReadSize() {
		return _readThreadCount;
	}

	public int getNewResReadSize() {
		return _newResReadCount;
	}

	public int getResScrollWeight() {
		return _resScrollWeight;
	}

	public int getResReadSize() {
		return _readResCount;
	}

	public boolean isTopTabBarShow() {
		return _topTabBarShow;
	}

	public String getServer() {
		return _sever;
	}

	public void load(InputStream in) throws IOException {
		DataInputStream dis = new DataInputStream(in);

		setData(dis.readUTF());
	}

	public void save(OutputStream out) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);

		dos.writeUTF(getData());
		dos.flush();
	}

	public void setData(String text) {
		String[] lines = Utilities.tokenToStrings(text, "\n");

		Hashtable configTable = new Hashtable();
		for (int i = 0; i < lines.length; i++) {
			String[] values = Utilities.tokenToStrings(lines[i], "\t");
			if (values.length >= 2) {
				String key = values[0];
				String value = values[1];
				for (int j = 2; j < values.length; j++) {
					value += ('\t' + values[j]);
				}
				configTable.put(key, value);
			}
		}

		int fontFace = getConfigTableInt(configTable, FONT_FACE, _font.getFace());
		int fontSize = getConfigTableInt(configTable, FONT_SIZE, _font.getSize());
		int fontStyle = getConfigTableInt(configTable, FONT_STYLE, _font.getStyle());
		_font = Font.createFont(fontFace, fontStyle, fontSize);

		_readThreadCount = getConfigTableInt(configTable, THREAD_READ_SIZE, _readThreadCount);
		_readResCount = getConfigTableInt(configTable, RES_READ_SIZE, _readResCount);
		_newResReadCount = getConfigTableInt(configTable, RES_NEW_READ_SIZE, _newResReadCount);

		_resScrollWeight = getConfigTableInt(configTable, RES_SCROLL_WEIGHT, _resScrollWeight);
		_resMoveMethod = getConfigTableInt(configTable, RES_MOVE_METHOD, _resMoveMethod);
		_resArrangement = getConfigTableInt(configTable, RES_SHOW_METHOD, _resArrangement);

		_tabThreadTitleSize = getConfigTableInt(configTable, TAB_TITLE_LENGTH, _tabThreadTitleSize);
		_topTabBarShow = getConfigTableBoolean(configTable, ROOT_TABBAR_SHOW, _topTabBarShow);

		_sever = getConfigTable(configTable, SERVER, _sever);
		_userSever = getConfigTable(configTable, USER_DEFINE_SERVER, _userSever);

		boolean defsv = false;
		for (int i = 0; i < _defaultSevers.length; i++) {
			if (_defaultSevers[i].equals(_sever)) {
				defsv = true;
				break;
			}
		}
		if (!defsv) {
			if (!_userSever.equals(_sever)) {
				_userSever = _sever;
			}
		}

		_gikolet.fontChanged(_font);
		_gikolet.resScrollWeightChanged(_resScrollWeight);
		_gikolet.threadTabTextLengthChanged(_tabThreadTitleSize);
		_gikolet.getReader().setBaseURI(_sever);
		_gikolet.topTabBarShowChanged(_topTabBarShow);
		_gikolet.setResShowMethod(_resArrangement);
	}

	public String getData() {
		StringBuffer sb = new StringBuffer();

		sb.append(FONT_FACE).append('\t').append(_font.getFace()).append('\n');
		sb.append(FONT_SIZE).append('\t').append(_font.getSize()).append('\n');
		sb.append(FONT_STYLE).append('\t').append(_font.getStyle()).append('\n');

		sb.append(THREAD_READ_SIZE).append('\t').append(_readThreadCount).append('\n');
		sb.append(RES_READ_SIZE).append('\t').append(_readResCount).append('\n');
		sb.append(RES_NEW_READ_SIZE).append('\t').append(_newResReadCount).append('\n');

		sb.append(RES_SCROLL_WEIGHT).append('\t').append(_resScrollWeight).append('\n');
		sb.append(RES_SHOW_METHOD).append('\t').append(_resArrangement).append('\n');
		sb.append(RES_MOVE_METHOD).append('\t').append(_resMoveMethod).append('\n');

		sb.append(TAB_TITLE_LENGTH).append('\t').append(_tabThreadTitleSize).append('\n');
		sb.append(ROOT_TABBAR_SHOW).append('\t').append(_topTabBarShow).append('\n');

		sb.append(SERVER).append('\t').append(Utilities.escape(_sever, false, true)).append('\n');
		sb.append(USER_DEFINE_SERVER).append('\t')
				.append(Utilities.escape(_userSever, false, true));

		return sb.toString();
	}

	private boolean getConfigTableBoolean(Hashtable table, String key, boolean defalut) {
		String value = getConfigTable(table, key, String.valueOf(defalut));
		if (value.equals("true")) {
			return true;
		} else if (value.equals("false")) {
			return false;
		}
		throw new IllegalArgumentException("value is Boolean.");
	}

	private int getConfigTableInt(Hashtable table, String key, int defalut) {
		String value = getConfigTable(table, key, Integer.toString(defalut));
		return Integer.parseInt(value);
	}

	private String getConfigTable(Hashtable table, String key, String defalut) {
		String value = (String) table.get(key);
		if (value == null) {
			value = defalut;
		} else {
			value = Utilities.unescape(value, false, true);
		}
		return value;
	}

	protected void layout() {
		Display display = Toolkit.getDisplay();
		Dimension pd = gikolet.base.ui.Utilities.getPreferredSize(this, display.getScreenWidth(),
				display.getScreenHeight());

		int x = (display.getScreenWidth() - pd.getWidth()) / 2;
		int y = (display.getScreenHeight() - pd.getHeight()) / 2;

		setBounds(x, y, pd.getWidth(), pd.getHeight());

		super.layout();
	}

	private void listClick() {
		int index = _list.getSelectedIndex();
		if (index == 0) {
			int si = OptionPane.showSelectionDialog(this, "ﾌｫﾝﾄ", new String[] { "ｻｲｽﾞ", "ﾌｪｲｽ",
					"ﾎﾞｰﾙﾄﾞ", "ｲﾀﾘｯｸ" });
			int size = _font.getSize();
			int style = _font.getStyle();
			int face = _font.getFace();
			if (si == 0) {
				si = 0;
				if (size == Font.SIZE_LARGE) {
					si = 0;
				} else if (size == Font.SIZE_MEDIUM) {
					si = 1;
				} else if (size == Font.SIZE_SMALL) {
					si = 2;
				} else if (size == Font.SIZE_TINY) {
					si = 3;
				}
				si = OptionPane.showSelectionDialog(this, "ｻｲｽﾞ", new String[] { "大", "中", "小",
						"極小" }, si);
				if (si == 0) {
					size = Font.SIZE_LARGE;
				} else if (si == 1) {
					size = Font.SIZE_MEDIUM;
				} else if (si == 2) {
					size = Font.SIZE_SMALL;
				} else if (si == 3) {
					size = Font.SIZE_TINY;
				}
			} else if (si == 1) {
				si = 0;
				if (face == Font.FACE_SYSTEM) {
					si = 0;
				} else if (face == Font.FACE_MONOSPACE) {
					si = 1;
				} else if (face == Font.FACE_PROPORTIONAL) {
					si = 2;
				}
				si = OptionPane.showSelectionDialog(this, "ﾌｪｲｽ", new String[] { "ｼｽﾃﾑ", "ﾓﾉｽﾍﾟｰｽ",
						"ﾌﾟﾛﾎﾟｰｼｮﾅﾙ", }, si);
				if (si == 0) {
					face = Font.FACE_SYSTEM;
				} else if (si == 1) {
					face = Font.FACE_MONOSPACE;
				} else if (si == 2) {
					face = Font.FACE_PROPORTIONAL;
				}
			} else if (si == 2) {
				si = (_font.isBold()) ? 0 : 1;
				si = OptionPane.showSelectionDialog(this, "ﾎﾞｰﾙﾄﾞ", new String[] { "適用する", "適用しない" },
						si);
				if (si == 0) {
					style |= Font.STYLE_BOLD;
					style &= ~Font.STYLE_PLAIN;
				} else if (si == 1) {
					style &= ~Font.STYLE_BOLD;
					if (!_font.isItalic()) {
						style &= ~Font.STYLE_PLAIN;
					}
				}
			} else if (si == 3) {
				si = (_font.isItalic()) ? 0 : 1;
				si = OptionPane
						.showSelectionDialog(this, "ｲﾀﾘｯｸ", new String[] { "適用する", "適用しない" }, si);
				if (si == 0) {
					style |= Font.STYLE_ITALIC;
					style &= ~Font.STYLE_PLAIN;
				} else if (si == 1 && _font.isItalic()) {
					style &= ~Font.STYLE_ITALIC;
					if (!_font.isBold()) {
						style &= ~Font.STYLE_PLAIN;
					}
				}
			}
			_font = Font.createFont(face, style, size);

			_gikolet.fontChanged(_font);
		} else {
			try {
				if (index == 1) {
					_readThreadCount = OptionPane.showInputDialog(this, "1回に読むｽﾚ数", 1, 1000,
							_readThreadCount);
				} else if (index == 2) {
					_readResCount = OptionPane.showInputDialog(this, "1回に読むﾚｽ数", 1, 1000,
							_readResCount);
				} else if (index == 3) {
					_newResReadCount = OptionPane.showInputDialog(this, "最新で読むﾚｽ数", 1, 1000,
							_newResReadCount);
				} else if (index == 4) {
					_resScrollWeight = OptionPane.showInputDialog(this, "ﾚｽのｽｸﾛｰﾙ量", 1, 100,
							_resScrollWeight);
					_gikolet.resScrollWeightChanged(_resScrollWeight);
				} else if (index == 5) {
					int si = 0;
					if (_resMoveMethod == RES_MOVE_METHOD_UP_DONW_LEFT_RIGHT) {
						si = 0;
					} else if (_resMoveMethod == RES_MOVE_METHOD_LEFT_RIGHT) {
						si = 1;
					}
					si = OptionPane.showSelectionDialog(this, "ﾚｽの移動方法", new String[] { "上下左右",
							"左右" }, si);
					if (si == 0) {
						_resMoveMethod = RES_MOVE_METHOD_UP_DONW_LEFT_RIGHT;
					} else if (si == 1) {
						_resMoveMethod = RES_MOVE_METHOD_LEFT_RIGHT;
					}
				} else if (index == 6) {
					int si = 0;
					if (_resArrangement == RES_SHOW_METHOD_2CH) {
						si = 0;
					} else if (_resArrangement == RES_SHOW_METHOD_BODY_PRIORITY) {
						si = 1;
					}
					si = OptionPane.showSelectionDialog(this, "ﾚｽの表示方法", new String[] { "デフォルト",
							"本文優先" }, si);
					if (si == 0) {
						_resArrangement = RES_SHOW_METHOD_2CH;
					} else if (si == 1) {
						_resArrangement = RES_SHOW_METHOD_BODY_PRIORITY;
					}
					_gikolet.setResShowMethod(_resArrangement);
				} else if (index == 7) {
					_tabThreadTitleSize = OptionPane.showInputDialog(this, "ｽﾚﾀﾌﾞの文字数", 0, 99,
							_tabThreadTitleSize);
					_gikolet.threadTabTextLengthChanged(_tabThreadTitleSize);
				} else if (index == 8) {
					int si = (_topTabBarShow) ? 0 : 1;
					si = OptionPane.showSelectionDialog(this, "ﾙｰﾄﾀﾌﾞﾊﾞｰの表示", new String[] {
							"表示する", "表示しない" }, si);

					_topTabBarShow = (si == 0) ? true : false;
					_gikolet.topTabBarShowChanged(_topTabBarShow);
				} else if (index == 9) {
					String[] defaultSever = _defaultSevers;
					String[] str = new String[defaultSever.length + 1];
					System.arraycopy(defaultSever, 0, str, 0, defaultSever.length);
					str[str.length - 1] = "ﾕｰｻﾞｰ定義鯖";

					int si = 0;
					if (_sever != null) {
						si = str.length - 1;
						for (int i = 0; i < defaultSever.length; i++) {
							if (defaultSever[i].equals(_sever)) {
								si = i;
								break;
							}
						}
					}

					int i = OptionPane.showSelectionDialog(this, "鯖選択", str, si);
					if (i != -1) {
						if (i == str.length - 1) {
							_sever = Toolkit.getDisplay().imeOn("鯖を入力", _userSever, Display.ANY);
							_userSever = _sever;
						} else {
							_sever = str[i];
						}
						_gikolet.getReader().setBaseURI(_sever);
					}
				} else if (index == 10) {
					String message;
					try {
						_gikolet.save();
						message = "保存成功";
					} catch (Exception e) {
						e.printStackTrace();
						message = "保存失敗";
					}
					OptionPane.showMessageDialog(this, new String[] { message });
				} else if (index == 11) {
					init();
				}
			} catch (CancelException e) {
			}
		}
	}
}