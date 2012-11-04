/*
 * 作成日: 2005/10/18
 */
package gikolet.midp;

import gikolet.GikoletExtensions;
import gikolet.base.util.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

/**
 * @author tetsutaro
 */
public class MIDPGikoletExtensions extends GikoletExtensions {
	private Vector	_cookies	= new Vector();

	public MIDPGikoletExtensions() {
		super();
	}

	public boolean isResWritable() {
		return true;
	}

	public void resWrite(String boardTitle, int boardNo, String threadTitle,
			int threadNo) {

		class TransfersValue {
			String			uri;
			String			host;

			int				boardNo;
			int				threadNo;

			String			name;
			String			mailAddress;
			String			body;

			long			time;
			String			cookieVefMessage;
			boolean			cookieVef;

			boolean			success;

			HttpConnection	httpConnection;
			Exception		exception;
		}

		final TransfersValue value = new TransfersValue();
		value.boardNo = boardNo;
		value.uri = null;

		final StateDialog sd = new StateDialog();

		new Thread() {
			public void run() {
				try {
					value.uri = GikoletMIDlet.getGikolet().getReader()
							.getBoardURI(value.boardNo);
					System.out.println("URI取得");
				} catch (Exception e) {
					value.exception = e;
				}
				sd.close();

			}
		}.start();

		if (!sd.open("通信中", "板番号からURIを取得しています。", true)) {
			GikoletMIDlet.getGikolet().getReader().onlineUpdateCancel();
			return;
		} else if (value.exception != null) {
			alert("書き込みﾎｽﾄ取得に失敗", value.exception.getMessage() + "\n\n"
					+ value.exception.getClass().getName());
			System.out.println(value.exception);
			return;
		}

		if (value.uri.charAt(value.uri.length() - 1) == '/') {
			value.uri = value.uri.substring(0, value.uri.length() - 1);
		}

		int i = value.uri.lastIndexOf('/', value.uri.length() - 1);
		final String boardName = value.uri.substring(i + 1);
		value.host = value.uri.substring(0, i);

		if (!isWritable(value.uri)) {
			alert("書き込み失敗", boardTitle + "板は書き込みできる板ではありません");
			return;
		}

		/* ************************************************************************* */
		/* ************************************************************************* */
		value.httpConnection = null;
		value.time = 0;
		value.threadNo = threadNo;

		new Thread() {
			public void run() {
				HttpConnection hc = null;
				try {
					System.out.println("time取得");
					value.httpConnection = hc = (HttpConnection) Connector
							.open("http://" + value.host + "/test/read.cgi/"
									+ boardName + "/" + value.threadNo + "/1",
									Connector.READ, true);
					hc.setRequestMethod(HttpConnection.HEAD);

					value.time = hc.getDate() / 1000;

				} catch (Exception ioe) {
					value.exception = ioe;
				} finally {
					try {
						hc.close();
					} catch (Exception e) {
					}
				}
				sd.close();
			}
		}.start();

		if (!sd.open("通信中", "フォームの生成時間取得中", true)) {
			try {
				value.httpConnection.close();
			} catch (Exception e) {
			}
			return;
		} else if (value.exception != null) {
			alert("フォームの生成時間取得失敗", value.exception.getMessage() + "\n\n"
					+ value.exception.getClass().getName());
			System.out.println(value.exception);
			return;
		}

		/* ************************************************************************* */
		/* ************************************************************************* */
		value.httpConnection = null;
		value.success = false;
		value.cookieVef = false;
		value.name = "";
		value.mailAddress = "sage";
		value.body = "";

		do {
			value.cookieVefMessage = null;
			value.exception = null;

			if (!value.cookieVef) {
				WriteDialog wd = new WriteDialog();
				if (!wd.open(boardTitle, threadTitle, value.name,
						value.mailAddress, value.body)) {
					return;
				}
				value.name = wd.getName();
				value.mailAddress = wd.getMailAddress();
				value.body = wd.getBody();
			}

			Thread thread = new Thread() {
				public void run() {
					HttpConnection hc = null;
					try {
						String encName = toURIEncodeSJIS(value.name);
						String encMail = toURIEncodeSJIS(value.mailAddress);
						String encBody = toURIEncodeSJIS(Utilities.unescape(
								value.body, false, true));

						String encSubmit = toURIEncodeSJIS((value.cookieVef) ? "上記全てを承諾して書き込む"
								: "書き込む");

						String bbsCGI = "http://" + value.host
								+ "/test/bbs.cgi";
						hc = (HttpConnection) Connector.open(bbsCGI,
								Connector.READ_WRITE, true);

						hc.setRequestMethod(HttpConnection.POST);

						hc.setRequestProperty("User-Agent",
								"Monazilla/1.00 Gikolet/0.0");
						hc.setRequestProperty("Accept",
								"text/html,text/plain,*/*");
						hc.setRequestProperty("Accept-Language", "ja");
						hc.setRequestProperty("Accept-Charset", "Shift_JIS");
						hc.setRequestProperty("Keep-Alive", "300");
						hc.setRequestProperty("Connection", "keep-alive");

						if (value.cookieVef) {
							hc.setRequestProperty("Referer", bbsCGI);
						} else {
							hc.setRequestProperty("Referer", "http://"
									+ value.uri + "/test/read.cgi/" + boardName
									+ "/" + value.threadNo + "/1");
						}

						StringBuffer sb = new StringBuffer();

						Enumeration e = _cookies.elements();
						while (e.hasMoreElements()) {
							sb.append((String) e.nextElement());

							if (e.hasMoreElements()) {
								sb.append("; ");
							}
						}

						String cookie = sb.toString();
						if (cookie.length() > 0) {
							System.out.println("Cookie: " + sb);

							hc.setRequestProperty("Cookie", sb.toString());
						}
						hc.setRequestProperty("Content-Type",
								"application/x-www-form-urlencoded");

						String post = "bbs=" + boardName + "&key="
								+ value.threadNo + "&time=" + value.time
								+ "&submit=" + encSubmit + "&FROM=" + encName
								+ "&mail=" + encMail + "&MESSAGE=" + encBody;

						System.out.println("書き込み");
						System.out.println(hc.getURL());
						System.out.println("Cookie: "
								+ hc.getRequestProperty("Cookie"));
						System.out.println(post);

						byte[] postBytes = toSJIS(post);
						hc.setRequestProperty("Content-Length", Integer
								.toString(postBytes.length));

						OutputStream os = null;
						try {
							os = hc.openOutputStream();

							// TODO ここは変える
							os.write(postBytes);
						} finally {
							try {
								os.close();
							} catch (Exception ee) {
							}
						}

						byte[] bytes;
						InputStream is = null;
						try {
							is = hc.openInputStream();

							ByteArrayOutputStream baos;
							baos = new ByteArrayOutputStream();

							byte[] buf = new byte[256];
							int t = 0;
							while ((t = is.read(buf)) != -1) {
								baos.write(buf, 0, t);
							}

							bytes = baos.toByteArray();
						} finally {
							try {
								is.close();
							} catch (Exception ee) {
							}
						}
						String str = MIDPGikoletExtensions.this.toString(bytes);

						// System.out.println(str);

						int rc = hc.getResponseCode();
						if (rc >= 300) {
							// かなりてけとー
							throw new IOException("ResponseCode is " + rc);
						}

						if (str.indexOf("ＥＲＲＯＲ") != -1) {
							/*
							 * int si; if ((si = str.indexOf("<b>ＥＲＲＯＲ：")) !=
							 * -1) { si += 9; int ti = str.indexOf("</b>", si);
							 * throw new IOException(str.substring(si, ti)); }
							 * throw new IOException("ＥＲＲＯＲ");
							 */
							throw new IOException(removeTag(str).trim());
						} else if (str.indexOf("お茶でも") != -1) {
							throw new IOException("鯖負荷が高いようです");
						} else if (str.indexOf("書き込み確認") != -1) {
							int i = 0;
							String key;
							_cookies.removeAllElements();
							do {
								key = hc.getHeaderFieldKey(i);
								if (key == null) {
									break;
								}
								// エミュレーターだと小文字に変換してしまうためそれに合わせる
								key = key.toLowerCase();
								if (key.equals("set-cookie")) {
									String value = hc.getHeaderField(i);

									int t;
									if ((t = value.indexOf(';')) == -1) {
										t = value.length();
									}
									System.out.println("Set-Cookie: " + value);
									_cookies.addElement(value.substring(0, t));
								}
								i++;
							} while (true);

							StringBuffer strsb = new StringBuffer();
							int s = 0;
							int t = 0;
							// auの糞がHTML書き換えくさるから…まじで死ね
							do {
								if ((s = str.indexOf("\n・", s)) != -1) {
									s++;
									if ((t = str.indexOf("<br", s)) != -1) {
										strsb
												.append(str.substring(s, t)
														+ "\n");
										s = t;
									}
								}
							} while (s != -1 && t != -1);
							value.cookieVefMessage = strsb.toString();

							value.cookieVef = true;
						} else if (str.indexOf("書きこみました") != -1) {
							value.success = true;
							value.cookieVef = false;
						} else {
							throw new IOException("原因不明");
						}
					} catch (Exception e) {
						value.exception = e;
					} finally {
						try {
							hc.close();
						} catch (Exception e) {
						}
						sd.close();
					}
				}
			};
			thread.start();

			sd.open("通信中", "書き込み中...", false);

			if (value.exception != null) {
				alert("書き込み失敗", value.exception.getMessage() + "\n\n"
						+ value.exception.getClass().getName());
				System.out.println(value.exception);
			} else if (value.cookieVef) {
				AlertDialog ynd = new AlertDialog(AlertDialog.YES_NO);
				if (!ynd.open("投稿確認", value.cookieVefMessage)) {
					return;
				}
			}
		} while (!value.success);
	}

	private String removeTag(String str) {
		StringBuffer sb = new StringBuffer();

		int s = 0;
		while (s < str.length()) {
			int t = str.indexOf('<', s);
			if (t == -1) {
				t = str.length();
			}
			if (t - s > 0) {
				sb.append(str.substring(s, t));
			}

			if (t + 1 < str.length()) {
				s = str.indexOf('>', t + 1);
				if (s == -1) {
					s = str.length();
				} else {
					s++;
				}
			} else {
				s = t + 1;
			}
		}
		return sb.toString();
	}

	private class Dialog {
		private boolean	_open;
		private boolean	_cancel;

		protected Dialog() {
			_open = false;
			_cancel = false;
		}

		protected synchronized boolean openCore(Displayable displayable) {
			if (_open) {
				return true;
			}
			_cancel = false;
			_open = true;

			GikoletMIDlet.getMIDPToolkit().getMIDPDisplay().setNativeCurrent(
					displayable);

			while (_open) {
				try {
					this.wait();
				} catch (InterruptedException e) {
				}
			}

			GikoletMIDlet.getMIDPToolkit().getMIDPDisplay().setNativeCurrent(
					null);
			return !_cancel;
		}

		boolean isCancel() {
			return _cancel;
		}

		public boolean isOpen() {
			return _open;
		}

		protected synchronized void closeCore(boolean cancel) {
			_open = false;
			_cancel = cancel;
			this.notify();
		}
	}

	private class AlertDialog extends Dialog {
		private Command			_okCmd	= new Command("OK", Command.OK, 0);
		private Command			_yesCmd	= new Command("はい", Command.SCREEN, 0);
		private Command			_noCmd	= new Command("いいえ", Command.SCREEN, 1);

		public final static int	OK		= 0;
		public final static int	YES_NO	= 1;

		private int				_type;

		public AlertDialog(int type) {
			super();
			if (type < 0 || 1 < type) {
				throw new IllegalArgumentException("type is illegal");
			}
			_type = type;
		}

		public boolean open(String title, String message) {
			Form form = new Form(title);
			form.append(message);

			if (_type == OK) {
				form.addCommand(_okCmd);
			} else if (_type == YES_NO) {
				form.addCommand(_yesCmd);
				form.addCommand(_noCmd);
			}
			form.setCommandListener(new CommandListener() {
				public void commandAction(Command c, Displayable d) {
					if (c == _okCmd) {
						closeCore(false);
					} else if (c == _yesCmd) {
						closeCore(false);
					} else if (c == _noCmd) {
						closeCore(true);
					}
				}
			});
			return openCore(form);
		}
	}

	private class WriteDialog extends Dialog {
		private Command	_okCmd;
		private Command	_cancelCmd;

		private String	_name;
		private String	_mailAddress;
		private String	_body;

		WriteDialog() {
			_okCmd = new Command("書込", Command.SCREEN, 0);
			_cancelCmd = new Command("ｷｬﾝｾﾙ", Command.SCREEN, 1);
		}

		public boolean open(String boardTitle, String threadTitle) {
			return open(boardTitle, threadTitle, "", "sage", "");
		}

		public boolean open(String boardTitle, String threadTitle, String name,
				String mail, String body) {
			synchronized (this) {
				_name = null;
				_mailAddress = null;
				_body = null;
			}
			Form form = new Form("書き込み");

			// final TextField nameTextField = new TextField("名前", "",
			// Integer.MAX_VALUE, TextField.ANY);
			// final TextField mailAddressTextField = new TextField("メル欄",
			// "sage",
			// Integer.MAX_VALUE, TextField.ANY);
			// final TextField bodyTextField = new TextField("内容", "",
			// Integer.MAX_VALUE, TextField.ANY);
			final TextField nameTextField = new TextField("名前", name, 255,
					TextField.ANY);
			final TextField mailAddressTextField = new TextField("メル欄", mail,
					255, TextField.ANY);
			final TextField bodyTextField = new TextField("内容", body, 4096,
					TextField.ANY);

			form.append(boardTitle + "板: \n");
			form.append(threadTitle);
			form.append(nameTextField);
			form.append(mailAddressTextField);
			form.append(bodyTextField);

			form.addCommand(_okCmd);
			form.addCommand(_cancelCmd);

			form.setCommandListener(new CommandListener() {
				public void commandAction(Command c, Displayable d) {
					if (c == _okCmd) {
						_name = nameTextField.getString();
						_mailAddress = mailAddressTextField.getString();
						_body = bodyTextField.getString();
						closeCore(false);
					} else if (c == _cancelCmd) {
						closeCore(true);
					}
				}
			});
			return openCore(form);
		}

		public String getName() {
			return _name;
		}

		public String getMailAddress() {
			return _mailAddress;
		}

		public String getBody() {
			return _body;
		}
	}

	private class StateDialog extends Dialog {
		private Command	_cancelCmd;

		public StateDialog() {
			_cancelCmd = new Command("ｷｬﾝｾﾙ", Command.SCREEN, 0);
		}

		// ｷｬﾝｾﾙしたらfalseを返す
		public boolean open(String title, String body, boolean cancel) {
			Form form = new Form(title);
			form.append(body);

			if (cancel) {
				form.addCommand(_cancelCmd);
				form.setCommandListener(new CommandListener() {
					public void commandAction(Command c, Displayable d) {
						closeCore(true);
					}
				});
			}

			return openCore(form);
		}

		public void close() {
			closeCore(false);
		}
	}

	protected boolean isWritable(String uri) {
		return true;
	}

	private void alert(String title, String msg) {
		AlertDialog ad = new AlertDialog(AlertDialog.OK);
		ad.open(title, msg);
	}

}
