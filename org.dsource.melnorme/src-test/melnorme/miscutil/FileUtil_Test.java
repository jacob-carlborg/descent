/*******************************************************************************
 * Copyright (c) 2007 DSource.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial implementation
 *******************************************************************************/
package melnorme.miscutil;

import static melnorme.miscutil.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;

// A very basic test
public final class FileUtil_Test {
	
	private static final int WRITE_REPEAT_COUNT = 100000;
	private static final String WRITE_STRING = "0123456789";
	private int FILE_LENGTH = WRITE_REPEAT_COUNT * WRITE_STRING.length();
	
	private static File tempFile;
	
	static {
		try {
			tempFile = File.createTempFile("test-" + FileUtil_Test.class.getSimpleName(), ".tmp");
			tempFile.deleteOnExit();
			
			FileOutputStream fos = new FileOutputStream(tempFile);
			int count = WRITE_REPEAT_COUNT;
			while(count-- > 0) {
				fos.write(WRITE_STRING.getBytes("ASCII"));
			}
		} catch (IOException e) {
			throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
	}
	
	@Test
	public void test_read() throws FileNotFoundException, IOException {
		byte[] bytes;
		
		bytes = FileUtil.readBytesFromFile(tempFile);
		assertTrue(bytes.length == FILE_LENGTH);
		
		String string;
		
		bytes = FileUtil.readAvailableBytesFromStream(new FileInputStream(tempFile));
		assertTrue(bytes.length == FILE_LENGTH);
		string = new String(FileUtil.readAvailableBytesFromStream(new FileInputStream(tempFile)));
		assertTrue(string.length() == FILE_LENGTH);
		
		bytes = FileUtil.readBytesFromStream(new FileInputStream(tempFile), 1000);
		assertTrue(bytes.length == 1000);
		

		URL tempFileURL = tempFile.toURI().toURL();
		
		bytes = FileUtil.readAvailableBytesFromStream(tempFileURL.openStream());
		assertTrue(bytes.length == FILE_LENGTH);
		string = new String(FileUtil.readAvailableBytesFromStream(tempFileURL.openStream()));
		assertTrue(string.length() == FILE_LENGTH);
		
		bytes = FileUtil.readBytesFromStream(tempFileURL.openStream(), 1000);
		assertTrue(bytes.length == 1000);
	}
	
}
