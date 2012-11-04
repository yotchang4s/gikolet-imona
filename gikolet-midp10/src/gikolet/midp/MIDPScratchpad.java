/*
 * Created on 2005/03/26
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gikolet.midp;

import gikolet.base.io.Scratchpad;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 * @author tetsutaro
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MIDPScratchpad extends Scratchpad {

	public InputStream openInputStream() throws IOException {
		RecordStore recordStore = null;
		try {
			recordStore = RecordStore.openRecordStore("Scratchpad", true);
			if (recordStore.getNumRecords() == 0) {
				return new ByteArrayInputStream(new byte[0]);
			}
			return new ByteArrayInputStream(recordStore.getRecord(1));
		} catch (RecordStoreException e) {
			throw new IOException();
		} finally {
			if (recordStore != null) {
				try {
					recordStore.closeRecordStore();
				} catch (RecordStoreException e1) {}
			}
		}
	}

	public OutputStream openOutputStream() {
		return new ByteArrayOutputStream() {
			public synchronized void close() throws IOException {
				RecordStore recordStore = null;
				try {
					recordStore = RecordStore.openRecordStore("Scratchpad", true);

					byte[] bytes = toByteArray();
					if (recordStore.getNumRecords() == 0) {
						recordStore.addRecord(bytes, 0, bytes.length);
					} else {
						recordStore.setRecord(1, bytes, 0, bytes.length);
					}
				} catch (RecordStoreException e) {
					throw new IOException();
				} finally {
					if (recordStore != null) {
						try {
							recordStore.closeRecordStore();
						} catch (RecordStoreException e1) {}
					}
					super.close();
				}
			}
		};
	}
}