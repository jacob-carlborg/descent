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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Miscellaneous file and stream utilities. 
 */
public final class FileUtil extends StreamUtil {
	
	private static final int EOF = -1;


	/** Read all bytes of the given file. */
	public static byte[] readBytesFromFile(File file) throws IOException, FileNotFoundException {
		long fileLength = file.length();
		/*
		 * You cannot create an array using a long type. It needs to be an
		 * int type. Before converting to an int type, check to ensure
		 * that file is not larger than Integer.MAX_VALUE.
		 */
		if (fileLength > Integer.MAX_VALUE) 
			throw new IOException("File is too large, size is bigger than " + Integer.MAX_VALUE);
		
		FileInputStream fis = new FileInputStream(file);
		return readBytesFromStream(fis, (int) fileLength);
	}
	
	
	/** Write the given array of bytes to given file */
	public static void writeBytesToFile(byte[] bytes, File file) throws FileNotFoundException,
			IOException {
		FileOutputStream fileOS = new FileOutputStream(file);
		BufferedOutputStream fileBOS = new BufferedOutputStream(fileOS);
		try {
			fileBOS.write(bytes);
		} finally {
			fileBOS.close();
		}
	}
	
	/* --- Stream util --- */
	
	
	/** Reads all bytes from given inputStream until an EOF is read. */
	public static byte[] readAllBytesFromStream(InputStream inputStream) throws IOException {
		try {
			final int BUFFER_SIZE = 1024;
			byte[] buffer = new byte[BUFFER_SIZE];
			byte[] bytes = new byte[0];
			int totalRead = 0;

			int read;
			while((read = inputStream.read(buffer)) != EOF) {
				bytes = ArrayUtil.concat(bytes, buffer, read);
				totalRead += read;
			}
			return bytes;
		} finally {
			inputStream.close();
		}
	}
	
	/** Read and returns all chars in the given Reader until the end of the stream.
	 * Closes reader. */
	public static char[] readAllCharsFromReader(Reader reader) throws IOException {
		try {
			final int BUFFER_SIZE = 1024;
			char[] buffer = new char[BUFFER_SIZE];
			char[] chars = new char[0];
			int totalRead = 0;

			int read;
			while((read = reader.read(buffer)) != EOF) {
				chars = ArrayUtil.concat(chars, buffer, read);
				totalRead += read;
			}
			return chars;
		} finally {
			reader.close();
		}
	}

	@Deprecated
	public static char[] readCharsFromReader(Reader reader) throws IOException {
		return readAllCharsFromReader(reader);
	}
	
	
	/** Reads count amount of bytes from the given InputStream, and returns them. 
	 *  Closes given inputStream. 
	 *  Throws an Exception if it fails to read given count amount of elements. */
	public static byte[] readBytesFromStream(InputStream inputStream, int count) throws IOException {
		try {
			byte[] bytes = new byte[count];
			int totalRead = 0;
			do {
				int read = inputStream.read(bytes, totalRead, count - totalRead);
				if (read == -1) {
					throw new IOException("Failed to read requested amount of characters. " +
							"Read: " + totalRead + " of total requested: " + count);
				}
				totalRead += read;
			} while (totalRead != count);
			return bytes;
		} finally {
			inputStream.close(); 
		}
	}
	
	/** Reads count amount of chars from the given Reader, and returns them. 
	 *  Closes given reader. 
	 *  Throws an Exception if it fails to read given count amount of elements. */
	public static char[] readCharsFromStream(Reader reader, int count) throws IOException {
		try {
			char[] chars = new char[count];
			int totalRead = 0;
			do {
				int read = reader.read(chars, totalRead, count - totalRead);
				if (read == -1) {
					throw new IOException("Failed to read requested amount of characters. " +
							"Read: " + totalRead + " of total requested: " + count);
				}
				totalRead += read;
			} while (totalRead != count);
			return chars;
		} finally {
			reader.close(); 
		}
	}
	
	/** Reads and returns all available bytes from the given InputStream, as specified by 
	 * {@link InputStream#available()}. Closes inputStream afterwards. */
	public static byte[] readAvailableBytesFromStream(InputStream inputStream) throws IOException {
		int availableToRead = inputStream.available();
		return readBytesFromStream(inputStream, availableToRead);
	}
	

	
	/** Performs an InputStream close, either ignoring IOExceptions, 
	 * or rethrowing them unchecked, according to rethrowAsUnchecked. */
	public static void uncheckedClose(InputStream inStream, boolean rethrowAsUnchecked) {
		try {
			inStream.close();
		} catch (IOException e) {
			if(rethrowAsUnchecked)
				throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
	}

	/** Performs an Reader close, either ignoring IOExceptions, 
	 * or rethrowing them unchecked, according to rethrowAsUnchecked. */
	public static void uncheckedClose(Reader reader, boolean rethrowAsUnchecked) {
		try {
			reader.close();
		} catch (IOException e) {
			if(rethrowAsUnchecked)
				throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
	}
}
