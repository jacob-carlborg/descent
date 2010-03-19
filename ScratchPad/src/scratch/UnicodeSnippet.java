package scratch;

import static melnorme.miscutil.Assert.assertTrue;

import java.io.UnsupportedEncodingException;


public class UnicodeSnippet {

	public static void main(String[] args) throws UnsupportedEncodingException {
		
		String STR1 = "ç\u03a9";
		assertTrue(STR1.length() == 2);
		assertTrue(STR1.getBytes("UTF8").length == 4);
		assertTrue(STR1.getBytes("UTF16").length == 2+4);
		assertTrue(STR1.getBytes("UTF32").length == 8);
		System.out.println(STR1);
		
		String STR2 = "日本語";
		assertTrue(STR2.length() == 3);
		assertTrue(STR2.getBytes("UTF8").length == 9);
		assertTrue(STR2.getBytes("UTF16").length == 2+6);
		assertTrue(STR2.getBytes("UTF32").length == 12);
		System.out.println(STR2);
	}
}
