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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Miscellaneous stream utilities. 
 */
public class StreamUtil {
	
	protected static final int EOF = -1;
	
	/** Reads and returns all bytes from given inputStream until an EOF is read. 
	 * Closes inputStream afterwards. */
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
	
	/** Reads and returns all chars from given reader until an EOF is read.
	 * Closes reader afterwards. */
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
	
	/** Reads all chars from given reader until an EOF is read, and returns them as a String.
	 * Closes reader afterwards. */
	public static String readStringFromReader(Reader reader) throws IOException {
		return new String(readAllCharsFromReader(reader));
	}
	
	@Deprecated
	public static char[] readCharsFromReader(Reader reader) throws IOException {
		return readAllCharsFromReader(reader);
	}
	
	
	/** Reads given count amount of bytes from given inputStream, and returns them. 
	 *  Closes inputStream afterwards. 
	 *  @throws IOException if it fails to read given count amount of elements. */
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
	
	/** Reads given count amount of chars from given reader, and returns them. 
	 *  Closes reader afterwards. 
	 *  @throws IOException if it fails to read given count amount of elements. */
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
	
	/** Reads and returns all available bytes from the given inputStream, as specified by 
	 * {@link InputStream#available()}. 
	 * Closes inputStream afterwards. */
	public static byte[] readAvailableBytesFromStream(InputStream inputStream) throws IOException {
		int availableToRead = inputStream.available();
		return readBytesFromStream(inputStream, availableToRead);
	}
	
	/** Writes given bytes array to given outpuStream. 
	 * Close outputStream afterwards. */
	public static void writeBytesToStream(byte[] bytes, OutputStream outputStream) throws IOException {
		// A BufferedOutputStream is likely not necessary since this is a one-time array write
		BufferedOutputStream bos = new BufferedOutputStream(outputStream);
		try {
			bos.write(bytes);
		} finally {
			bos.close();
		}
	}
	
	/** Writes given chars array to given writer. 
	 * Close writer afterwards. */
	public static void writeCharsToWriter(char[] chars, Writer writer) throws IOException {
		BufferedWriter bw = new BufferedWriter(writer);
		try {
			bw.write(chars);
		} finally {
			bw.close();
		}
	}
	
	/** Writes given string to given writer. 
	 * Close writer afterwards. */
	public static void writeStringToWriter(String string, Writer writer) throws IOException {
		BufferedWriter bw = new BufferedWriter(writer);
		try {
			bw.write(string);
		} finally {
			bw.close();
		}
	}
	
	/** Closes given inputStream, either ignoring IOExceptions, 
	 * or rethrowing them unchecked, according to given rethrowAsUnchecked. */
	public static void uncheckedClose(InputStream inStream, boolean rethrowAsUnchecked) {
		try {
			inStream.close();
		} catch (IOException e) {
			if(rethrowAsUnchecked)
				throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
	}
	
	/** Closes given reader, either ignoring IOExceptions, 
	 * or rethrowing them unchecked, according to given rethrowAsUnchecked. */
	public static void uncheckedClose(Reader reader, boolean rethrowAsUnchecked) {
		try {
			reader.close();
		} catch (IOException e) {
			if(rethrowAsUnchecked)
				throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
	}
	
}
