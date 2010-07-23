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

import static melnorme.miscutil.Assert.assertFail;
import static melnorme.miscutil.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.junit.Test;

// A very basic test
public final class StreamUtil_Test {
	
	protected static final int WRITE_REPEAT_COUNT = 10000;
	protected static final String WRITE_STRING = "0123456789";
	protected int FILE_LENGTH = WRITE_REPEAT_COUNT * WRITE_STRING.length();
	
	protected static File tempFile;
	protected static File tempFile2;
	
	static {
		try {
			tempFile = File.createTempFile("test-" + StreamUtil_Test.class.getSimpleName(), ".tmp");
			tempFile2 = File.createTempFile("test-" + StreamUtil_Test.class.getSimpleName(), ".tmp");
			tempFile.deleteOnExit();
			tempFile2.deleteOnExit();
			
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
	public void test_readBytes() throws FileNotFoundException, IOException {
		byte[] bytes;
		
		bytes = StreamUtil.readAllBytesFromStream(new FileInputStream(tempFile));
		assertTrue(bytes.length == FILE_LENGTH);
		
		String string;
		
		bytes = StreamUtil.readAvailableBytesFromStream(new FileInputStream(tempFile));
		assertTrue(bytes.length == FILE_LENGTH);
		string = new String(StreamUtil.readAvailableBytesFromStream(new FileInputStream(tempFile)));
		assertTrue(string.length() == FILE_LENGTH);
		
		bytes = StreamUtil.readBytesFromStream(new FileInputStream(tempFile), 1000);
		assertTrue(bytes.length == 1000);
		
		
		URL tempFileURL = tempFile.toURI().toURL();
		
		bytes = StreamUtil.readAvailableBytesFromStream(tempFileURL.openStream());
		assertTrue(bytes.length == FILE_LENGTH);
		string = new String(StreamUtil.readAvailableBytesFromStream(tempFileURL.openStream()));
		assertTrue(string.length() == FILE_LENGTH);
		
		bytes = StreamUtil.readBytesFromStream(tempFileURL.openStream(), 1000);
		assertTrue(bytes.length == 1000);
		
	}
	
	@Test
	public void test_readChars() throws FileNotFoundException, IOException {
		URL tempFileURL = tempFile.toURI().toURL();
		
		String str;
		str = StreamUtil.readStringFromReader(getReader(tempFile));
		assertTrue(str.length() == FILE_LENGTH);
		str = new String(StreamUtil.readAllCharsFromReader(getReader(tempFile)));
		assertTrue(str.length() == FILE_LENGTH);
		
		str = new String(StreamUtil.readCharsFromReader(new InputStreamReader(tempFileURL.openStream(), "ASCII"), 1000));
		assertTrue(str.length() == 1000);
	}
	
	@Test
	public void test_readCharsX() throws IOException {
		try {
			new String(StreamUtil.readCharsFromReader(getReader(tempFile2), 1000));
			assertFail();
		} catch (Exception e) {
			assertTrue(e.getMessage().startsWith("Failed to read requested"));
		}
	}
	
	private Reader getReader(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		return new InputStreamReader(is, "ASCII");
	}
	
	@Test
	public void testCopyStream() throws FileNotFoundException, IOException {
		byte[] bytes;
		StreamUtil.copyStream(new FileInputStream(tempFile), new FileOutputStream(tempFile2));
		bytes = StreamUtil.readAllBytesFromStream(new FileInputStream(tempFile2));
		assertTrue(bytes.length == FILE_LENGTH);
		
		
		StreamUtil.copyStream(new ByteArrayInputStream("asdf".getBytes("ASCII")), new FileOutputStream(tempFile2));
		bytes = StreamUtil.readAllBytesFromStream(new FileInputStream(tempFile2));
		assertTrue(bytes.length == "asdf".length());

		String testString2 = "abdcefghij";
		StreamUtil.copyStream(new ByteArrayInputStream(testString2.getBytes("ASCII")),
				new BufferedOutputStream(new FileOutputStream(tempFile2)));
		bytes = StreamUtil.readAllBytesFromStream(new FileInputStream(tempFile2));
		assertTrue(bytes.length == testString2.length());
		
		
		StreamUtil.copyBytesToStream(new ByteArrayInputStream(testString2.getBytes("ASCII")), new FileOutputStream(tempFile2), 4);
		bytes = StreamUtil.readAllBytesFromStream(new FileInputStream(tempFile2));
		assertTrue(bytes.length == "abcd".length());
		
		
	}
	
}
