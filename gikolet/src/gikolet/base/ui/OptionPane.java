/*
 * 作成日： 2004/12/05 TODO この生成されたファイルのテンプレートを変更するには次を参照。 ウィンドウ ＞ 設定 ＞ Java ＞
 * コード・スタイル ＞ コード・テンプレート
 */
package gikolet.base.ui;

import gikolet.base.Toolkit;
import gikolet.base.ui.events.ActionEvent;
import gikolet.base.ui.events.ActionListener;
import gikolet.base.ui.events.KeyEvent;
import gikolet.base.ui.events.KeyEventListener;

/**
 * @author tetsutaro TODO この生成された型コメントのテンプレートを変更するには次を参照。 ウィンドウ ＞ 設定 ＞ Java ＞
 *         コード・スタイル ＞ コード・テンプレート
 */
public class OptionPane extends Dialog {
	private final static int	SELECTION		= 0;
	private final static int	NUMBER_INPUT	= 1;
	private final static int	MESSAGE			= 2;

	private int					type;

	private List				list;
	private int					_selectIndex;

	private NumberInputControl	_input;
	private int					_number;
	private boolean				_cancel;

	private KeyEventListener	_keyListener	= new OPKeyEventListener();

	class OPKeyEventListener implements KeyEventListener {
		public void keyAction(KeyEvent e) {
			OptionPane.this.keyAction(e);
		}
	};

	private OptionPane(RootControl root, String title, String[] strs, int selectIndex) {
		super(root, title, true);

		if (strs == null) {
			throw new NullPointerException("strs is null.");
		}
		if (selectIndex < 0 || strs.length - 1 < selectIndex) {
			throw new IndexOutOfBoundsException("selectIndex is illegal.");
		}

		this.list = new List();
		for (int i = 0; i < strs.length; i++) {
			if (strs[i] == null) {
				throw new NullPointerException("strs[" + i + "] is null.");
			}
			this.list.add(strs[i]);
		}
		this.list.addKeyEventListener(_keyListener);

		setCancelMenuItem();

		_selectIndex = selectIndex;
		list.setSelectedIndex(_selectIndex);

		add(list);

		this.type = SELECTION;
	}

	private OptionPane(RootControl root, String title, int minRange, int maxRange, int number) {
		super(root, title, true);

		_input = new NumberInputControl(minRange, maxRange, number);
		_input.setNumber(number);

		setCancelMenuItem();

		_input.addKeyEventListener(_keyListener);

		add(_input);

		this.type = NUMBER_INPUT;
	}

	private OptionPane(RootControl root, String title, String[] strs) {
		super(root, title, true, true);

		setDirection(VERTICAL);

		if (strs == null) {
			throw new NullPointerException("strs is null.");
		}
		addKeyEventListener(_keyListener);

		for (int i = 0; i < strs.length; i++) {
			if (strs[i] == null) {
				throw new NullPointerException("strs[" + i + "] is null.");
			}
			Label label = new Label(strs[i]);
			label.setBackColor(Color.WHITE);
			label.setForeColor(Color.BLACK);
			add(label);
		}
		this.type = MESSAGE;
	}

	protected void keyAction(KeyEvent e) {
		if (type == SELECTION) {
			if (e.getKeyActionType() == KeyEvent.PRESSED
					&& (e.getKeyCode() == KeyEvent.ENTER || e.getKeyCode() == KeyEvent.KEY_NUM5)) {
				_selectIndex = OptionPane.this.list.getSelectedIndex();
				setVisible(false);
				e.consume();
			}
		} else if (type == NUMBER_INPUT) {
			if (e.getKeyActionType() == KeyEvent.PRESSED
					&& (e.getKeyCode() == KeyEvent.ENTER || e.getKeyCode() == KeyEvent.KEY_NUM5)) {
				_number = _input.getNumber();
				_cancel = false;
				setVisible(false);
				e.consume();
			}
		} else if (type == MESSAGE) {
			setVisible(false);
			e.consume();
		}
	}

	private void cancel() {
		if (type == SELECTION) {
			_selectIndex = -1;
			setVisible(false);
		} else if (type == NUMBER_INPUT) {
			_cancel = true;
			setVisible(false);
		}
	}

	/*
	 * public Dimension getPreferredSize(int hintWidth, int hintHeight) {
	 * Dimension pd; if (_scrollPane != null) { pd =
	 * _scrollPane.getPreferredSize(hintWidth, hintHeight); } else { return
	 * super.getPreferredSize(hintWidth, hintHeight); } Insets insets =
	 * getInsets(); pd.setWidth(insets.getLeft() + pd.getWidth() +
	 * insets.getRight()); pd.setHeight(insets.getTop() + pd.getHeight() +
	 * insets.getBottom()); return pd; }
	 */

	private void setCancelMenuItem() {
		final MenuItem cancelMenuItem = new MenuItem("取消");
		cancelMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		setMenuItem(cancelMenuItem);
	}

	protected void layout() {
		Display display = Toolkit.getDisplay();
		Dimension pd = Utilities.getPreferredSize(this, display.getScreenWidth(), display
				.getScreenHeight());

		int x = (display.getScreenWidth() - pd.getWidth()) / 2;
		int y = (display.getScreenHeight() - pd.getHeight()) / 2;

		setBounds(x, y, pd.getWidth(), pd.getHeight());

		super.layout();
	}

	public static int showSelectionDialog(String[] strs) {
		return showSelectionDialog((Control) null, strs);
	}

	public static int showSelectionDialog(String title, String[] strs) {
		return showSelectionDialog(null, title, strs, 0);
	}

	public static int showSelectionDialog(Control conrol, String[] strs) {
		return showSelectionDialog(conrol, null, strs, 0);
	}

	public static int showSelectionDialog(Control conrol, String title, String[] strs) {
		return showSelectionDialog(conrol, title, strs, 0);
	}

	public static int showSelectionDialog(Control conrol, String title, String[] strs,
			int selectIndex) {
		RootControl root = null;
		if (conrol != null) {
			root = conrol.getRootControl();
		}
		final OptionPane optionPane = new OptionPane(root, title, strs, selectIndex);

		/*
		 * Display display = Toolkit.getDisplay(); Dimension d =
		 * Utilities.getPreferredSize(optionPane, display.getScreenWidth(),
		 * display .getScreenHeight()); int x = (display.getScreenWidth() -
		 * d.getWidth()) / 2; int y = (display.getScreenHeight() -
		 * d.getHeight()) / 2; optionPane.setBounds(x, y, d.getWidth(),
		 * d.getHeight());
		 */

		optionPane.setVisible(true);

		return optionPane._selectIndex;
	}

	public static int showInputDialog(Control control, String title, int minRange, int maxRange,
			int number) throws CancelException {
		RootControl root = null;
		if (control != null) {
			root = control.getRootControl();
		}
		OptionPane optionPane = new OptionPane(root, title, minRange, maxRange, number);

		optionPane.setVisible(true);

		if (optionPane._cancel) {
			throw new CancelException();
		}
		return optionPane._number;
	}

	public static void showMessageDialog(String message) {
		showMessageDialog(null, message);
	}

	public static void showMessageDialog(Control control, String message) {
		showMessageDialog(control, null, message);
	}

	public static void showMessageDialog(Control control, String title, String message) {
		showMessageDialog(control, title, new String[] { message });
	}

	public static void showMessageDialog(String[] message) {
		showMessageDialog(null, message);
	}

	public static void showMessageDialog(Control control, String[] message) {
		showMessageDialog(control, null, message);
	}

	public static void showMessageDialog(Control control, String title, String[] message) {
		RootControl root = null;
		if (control != null) {
			root = control.getRootControl();
		}
		new OptionPane(root, title, message).setVisible(true);
	}
}