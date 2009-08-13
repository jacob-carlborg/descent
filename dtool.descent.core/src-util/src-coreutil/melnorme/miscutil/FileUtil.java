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

/**
 * Miscellaneous file utilities. 
 */
public final class FileUtil extends StreamUtil {
	

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
	
}
