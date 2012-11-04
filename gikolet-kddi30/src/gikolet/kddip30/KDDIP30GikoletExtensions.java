/*
 * 作成日: 2005/10/27
 */
package gikolet.kddip30;

import gikolet.midp.MIDPGikoletExtensions;

/**
 * @author tetsutaro
 */
public class KDDIP30GikoletExtensions extends MIDPGikoletExtensions {
	private String[]	_severs;

	public KDDIP30GikoletExtensions(String[] severs) {
		super();

		// パッケージ内のことだから全信用するｗｗ
		_severs = severs;
	}

	protected boolean isWritable(String uri) {
		if (uri != null) {
			String _uri = "http://" + uri;
			for (int i = 0; i < _severs.length; i++) {
				if (_uri.indexOf(_severs[i]) != -1) {
					return true;
				}
			}
		}
		return false;
	}
}
