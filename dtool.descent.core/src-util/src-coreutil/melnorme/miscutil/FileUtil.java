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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
/**
 * Miscelleanous file utilities 
 */
public final class FileUtil {

	/** Read all bytes of the given file. */
	public static byte[] readBytesFromFile(File file) throws IOException, FileNotFoundException {
		long length = file.length();
	    /*
		 * You cannot create an array using a long type. It needs to be an
		 * int type. // Before converting to an int type, check to ensure
		 * that file is not larger than Integer.MAX_VALUE.
		 */
	    if (length > Integer.MAX_VALUE) 
	    	throw new IOException("File is too large");
	    
	    byte[] bytes;
	    
	    FileInputStream fis = new FileInputStream(file);
		try {
			// Create the char array to hold the data
			bytes = new byte[(int)length];

			// Read in the chars
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = fis.read(bytes, offset, bytes.length-offset)) >= 0) {
				offset += numRead;
			}

			// Ensure all the bytes have been read in
			if (offset < bytes.length) {
				throw new IOException("Could not completely read file "+file.getName());
			}
		} finally {
		    fis.close();
		}
	
	    return bytes;
	}

	
	/** Read all chars of the given file. */
	public static char[] readCharsFromFile(File file) throws IOException {

		long length = file.length();
	    /*
		 * You cannot create an array using a long type. It needs to be an
		 * int type. // Before converting to an int type, check to ensure
		 * that file is not larger than Integer.MAX_VALUE.
		 */
	    if (length > Integer.MAX_VALUE) 
	    	throw new IOException("File is too large");
	    
		char[] chars;

		FileReader fr = new java.io.FileReader(file);
		try {
			// Create the char array to hold the data
			chars = new char[(int)length];

			// Read in the chars
			int offset = 0;
			int numRead = 0;
			while (offset < chars.length
			       && (numRead = fr.read(chars, offset, chars.length-offset)) >= 0) {
			    offset += numRead;
			}

			// Ensure all the bytes have been read in
			if (offset < chars.length) {
			    throw new IOException("Could not completely read file "+file.getName());
			}
		} finally {
		    fr.close();
		}
	
	    return chars;
	}
	
	/** Read all chars available in the given File, returns a String */
	public static String readStringFromFile(File file) throws IOException {
		return new String(readCharsFromFile(file));
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
	
	
	private static char[] doReadCharsFromReader(Reader reader) throws IOException {
		char[] buffer = new char[1024];

	    // Read in the bytes
	    char[] chars = new char[0];
	    int offset = 0;

	    int numRead = 0;
	    while ((numRead = reader.read(buffer, 0, 1024)) >= 0) {
	    	chars = ArrayUtil.copyFrom(chars, offset + numRead);
	    	System.arraycopy(buffer, 0, chars, offset, numRead);
	    	offset += numRead;
	    }
	    return chars;
	}
	
	/** Read and returns all chars in the given Reader until the end of the stream.
	 * Closes reader. */
	public static char[] readCharsFromReader(Reader reader) throws IOException {
		try {
		    return doReadCharsFromReader(reader);
		} finally {
			reader.close();
		}
	}
	
	/** Read all chars available in the given Reader, returns a String. 
	 * Closes reader. */
	public static String readStringFromReader(Reader reader) throws IOException {
		return new String(readCharsFromReader(reader));
	}

	/** Read all chars available in the given InputStream, returns a String. 
	 *  Closes inputStream. */
	public static String readStringFromStream(InputStream inputStream) throws IOException {
		try {
			int availableToRead = inputStream.available();
			return doReadStringFromReader(inputStream, availableToRead);
		} finally {
			inputStream.close();
		}
	}

	private static String doReadStringFromReader(InputStream inputStream, int toRead) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		try {
			char[] chars = new char[toRead];
			int read = br.read(chars);
			if (read != toRead) {
				throw new IOException("Failed to read requested amount of characters. Read " + read
						+ " of " + toRead);
			}
			return new String(chars);
		} finally {
			//br.close();  Commented because it will cause inputStream to be closed 
		}
	}
	
	
	/** Performs an InputStream close, either ignoring IOExceptions, 
	 * or rethrowing them unchecked. */
	public static void uncheckedClose(InputStream inStream, boolean rethrowAsUnchecked) {
		try {
			inStream.close();
		} catch (IOException e) {
			if(rethrowAsUnchecked)
				throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
	}

	/** Performs an Reader close, either ignoring IOExceptions, 
	 * or rethrowing them unchecked. */
	public static void uncheckedClose(Reader reader, boolean rethrowAsUnchecked) {
		try {
			reader.close();
		} catch (IOException e) {
			if(rethrowAsUnchecked)
				throw melnorme.miscutil.ExceptionAdapter.unchecked(e);
		}
	}
}
