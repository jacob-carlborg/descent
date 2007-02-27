package util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
/**
 * Miscelleanous file utilities 
 */
public final class FileUtil {

	public static char[] readBytesFromFile(File file) throws IOException {
	    //InputStream is = new FileInputStream(file);
	    
		FileReader fr = new java.io.FileReader(file);
	
	    // Get the size of the file
	    long length = file.length();
	
	    // You cannot create an array using a long type.
	    // It needs to be an int type.
	    // Before converting to an int type, check
	    // to ensure that file is not larger than Integer.MAX_VALUE.
	    if (length > Integer.MAX_VALUE) {
	        // File is too large
	    }
	
	    // Create the byte array to hold the data
	    char[] bytes = new char[(int)length];
	
	    // Read in the bytes
	    int offset = 0;
	    int numRead = 0;
	    while (offset < bytes.length
	           && (numRead = fr.read(bytes, offset, bytes.length-offset)) >= 0) {
	        offset += numRead;
	    }
	
	    // Ensure all the bytes have been read in
	    if (offset < bytes.length) {
	        throw new IOException("Could not completely read file "+file.getName());
	    }
	
	    // Close the input stream and return bytes
	    fr.close();
	    return bytes;
	}

	public static String readStringFromFile(File file) throws IOException {
		return new String(readBytesFromFile(file));
	}

}
