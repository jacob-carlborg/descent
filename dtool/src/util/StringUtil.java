package util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
/**
 * Miscelleanous String utilities 
 */
public final class StringUtil {

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

	/* ****************************************************** */
	
	public static String newSpaceFilledString(int indent) {
		return newFilledString(indent, ' ');
	}

	public static String newFilledString(int indent, char ch) {
		char str[] = new char[indent];
		Arrays.fill(str, ch);
		return new String(str);
	}

	public static String newFilledString(int indent, String str) { 
		StringBuffer sb = new StringBuffer(indent);
		for (int i = 0; i < indent; i++ )
			sb = sb.append(str);
		
		return sb.toString();
	}

	public static String trailString(String str, String strtrail) {
		if(str.length() > 0)
			return str + strtrail;
		else 
			return str;
	}	
	
	
	
	
	public static String collToString(Collection coll, String sep) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(Object item : coll){
			if(!first)
				sb.append(sep);
			else
				first = false;
			sb.append(item.toString());
		}
		return sb.toString();
	}

}
