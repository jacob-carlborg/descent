package util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
/**
 * Miscelleanous file utilities 
 */
public final class FileUtil {

	/** Read all chars available in the given File. */
	public static char[] readCharsFromFile(File file) throws IOException {

		long length = file.length();
	    /*
		 * You cannot create an array using a long type. It needs to be an
		 * int type. // Before converting to an int type, check to ensure
		 * that file is not larger than Integer.MAX_VALUE.
		 */
	    if (length > Integer.MAX_VALUE) 
	    	throw new ExceptionAdapter(new IOException("File is too large"));
	    
		FileReader fr = new java.io.FileReader(file);
	
	    // Create the char array to hold the data
	    char[] chars = new char[(int)length];
	
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
	
	    fr.close();
	    return chars;
	}

	/** Read all chars available in the given File, returns a String */
	public static String readStringFromFile(File file) throws IOException {
		return new String(readCharsFromFile(file));
	}


	/** Read all chars available in the given Reader. */
	public static char[] readCharsFromReader(Reader reader) throws IOException {
	    char[] buffer = new char[1024];
	
	    // Read in the bytes
	    char[] chars = new char[0];
	    int offset = 0;

	    int numRead = 0;
	    while ( (numRead = reader.read(buffer, 0, 1024)) >= 0) {
	    	chars = Arrays.copyOf(chars, offset + numRead);
	    	System.arraycopy(buffer, 0, chars, offset, numRead);
	    	offset += numRead;
	    }
	    
	    return chars;
	}
	
	/** Read all chars available in the given Reader, returns a String. */
	public static String readStringFromReader(Reader reader) throws IOException {
		return new String(readCharsFromReader(reader));
	}
}
