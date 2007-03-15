package descent.internal.compiler.parser;

public class Utf {
	
	public static String decodeChar(char[] input, int s, int len, int[] pidx, int[] presult) {
		int V;
	    int i = pidx[0];
	    char u = input[s + i];

	    assert(i >= 0 && i < len);

	    if ((u & 0x80) != 0)
	    {   int n;
			char u2;

		/* The following encodings are valid, except for the 5 and 6 byte
		 * combinations:
		 *	0xxxxxxx
		 *	110xxxxx 10xxxxxx
		 *	1110xxxx 10xxxxxx 10xxxxxx
		 *	11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
		 *	111110xx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
		 *	1111110x 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
		 */
		for (n = 1; ; n++)
		{
		    if (n > 4)
		    	return Lerr(input, s, i, pidx, presult); // only do the first 4 of 6 encodings
		    if (((u << n) & 0x80) == 0)
		    {
			if (n == 1)
				return Lerr(input, s, i, pidx, presult);
			break;
		    }
		}

		// Pick off (7 - n) significant bits of B from first byte of octet
		V = (u & ((1 << (7 - n)) - 1));

		if (i + (n - 1) >= len)
			return Lerr(input, s, i, pidx, presult); // off end of string

		/* The following combinations are overlong, and illegal:
		 *	1100000x (10xxxxxx)
		 *	11100000 100xxxxx (10xxxxxx)
		 *	11110000 1000xxxx (10xxxxxx 10xxxxxx)
		 *	11111000 10000xxx (10xxxxxx 10xxxxxx 10xxxxxx)
		 *	11111100 100000xx (10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx)
		 */
		u2 = input[s + i + 1];
		if ((u & 0xFE) == 0xC0 ||
		    (u == 0xE0 && (u2 & 0xE0) == 0x80) ||
		    (u == 0xF0 && (u2 & 0xF0) == 0x80) ||
		    (u == 0xF8 && (u2 & 0xF8) == 0x80) ||
		    (u == 0xFC && (u2 & 0xFC) == 0x80))
			return Lerr(input, s, i, pidx, presult); // overlong combination

		for (int j = 1; j != n; j++)
		{
		    u = input[s + i + j];
		    if ((u & 0xC0) != 0x80)
		    return Lerr(input, s, i, pidx, presult); // trailing bytes are 10xxxxxx
		    V = (V << 6) | (u & 0x3F);
		}
		if (!isValidDchar(V))
			return Lerr(input, s, i, pidx, presult);
		i += n;
	    }
	    else
	    {
		V = u;
		i++;
	    }

	    pidx[0] = i;
	    presult[0] = V;
	    return null;
	}
	
	public static boolean isValidDchar(long c) {
		 return c < 0xD800 ||
			(c > 0xDFFF && c <= 0x10FFFF && c != 0xFFFE && c != 0xFFFF);
	}
	
	private static String Lerr(char[] input, int s, int i, int[] pidx, int[] presult) {
		presult[0] = input[s + i];
	    pidx[0] = i + 1;
	    return "invalid UTF-8 sequence";
	}

}
