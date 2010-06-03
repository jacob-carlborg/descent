package descent.core;

import java.text.ParseException;
import java.util.BitSet;

public class Mangler {
	
	public static String demange(String name) throws ParseException {
		if(name.length() < 3 || name.charAt(0) != '_' || name.charAt(1) != 'D' || !Character.isDigit(name.charAt(2))) {
			throw new ParseException("", 0);
		}
		
		int[] ni = { 2 };
		StringBuilder result = parseQualifiedName(name, ni);
		result = parseType(name, ni, result);
		while(ni[0] < name.length()) {
			result.append(" . ").append(parseType(name, ni, parseQualifiedName(name, ni)));
		}

		if(ni[0] != name.length()) {
			throw new ParseException("", 0);
		}
		return result.toString();
	}

	private static StringBuilder parseQualifiedName(String name, int[] ni) throws ParseException {
		StringBuilder result = new StringBuilder();

		while(ni[0] < name.length() && Character.isDigit(name.charAt(ni[0]))) {
			if(result.length() != 0) {
				result.append(".");
			}
			result.append(parseSymbolName(name, ni));
		}
		return result;
	}

	private static StringBuilder parseSymbolName(String name, int[] ni) throws ParseException {
		int i = parseNumber(name, ni);
		if(ni[0] + i > name.length()) {
			throw new ParseException("", 0);
		}
		StringBuilder result = new StringBuilder();
		if(i >= 5 && name.charAt(ni[0]) == '_' && name.charAt(ni[0] + 1) == '_' && name.charAt(ni[0] + 2) == 'T') {
			int nisave = ni[0];
			boolean err = false;
			ni[0] += 3;
			try {
				result = fparseTemplateInstanceName(name, ni);
				if(ni[0] != nisave + i) {
					err = true;
				}
			} catch(ParseException me) {
				err = true;
			}
			ni[0] = nisave;
			if(err) {
				result.append(name.substring(ni[0], ni[0] + i));
				ni[0] += i;
			} else {
				ni[0] += i;
			}
			return result;
		}
		result.append(name.substring(ni[0], ni[0] + i));
		ni[0] += i;
		return result;
	}

	private static int parseNumber(String name, int[] ni) throws ParseException {
		int result = 0;

		while(ni[0] < name.length() && Character.isDigit(name.charAt(ni[0]))) {
			int i = name.charAt(ni[0]) - '0';
			if(result > (Integer.MAX_VALUE - i) / 10) {
				throw new ParseException("", 0);
			}
			result = result * 10 + i;
			ni[0]++;
		}
		return result;
	}
	
	private static StringBuilder parseType(String name, int[] ni) throws ParseException {
		return parseType(name, ni, new StringBuilder());
	}
	
	private static StringBuilder parseType(String name, int[] ni, StringBuilder identifier) throws ParseException {
		boolean isdelegate = false;
		boolean hasthisptr = false; /// For function/delegate types: expects a 'this' pointer as last argument
	Lagain: while(true) {
		if(ni[0] >= name.length()) {
			throw new ParseException("", 0);
		}
		StringBuilder p = new StringBuilder();
		switch(name.charAt(ni[0]++)) {
			case 'v':
				set(p, "void");
				return parseType_L1(p, isdelegate, identifier);				
			case 'b':
				set(p, "bool");
				return parseType_L1(p, isdelegate, identifier);	
			case 'g':
				set(p, "byte");
				return parseType_L1(p, isdelegate, identifier);	
			case 'h':
				set(p, "ubyte");
				return parseType_L1(p, isdelegate, identifier);	
			case 's':
				set(p, "short");
				return parseType_L1(p, isdelegate, identifier);	
			case 't':
				set(p, "ushort");
				return parseType_L1(p, isdelegate, identifier);	
			case 'i':
				set(p, "int");
				return parseType_L1(p, isdelegate, identifier);	
			case 'k':
				set(p, "uint");
				return parseType_L1(p, isdelegate, identifier);	
			case 'l':
				set(p, "long");
				return parseType_L1(p, isdelegate, identifier);	
			case 'm':
				set(p, "ulong");
				return parseType_L1(p, isdelegate, identifier);	
			case 'f':
				set(p, "float");
				return parseType_L1(p, isdelegate, identifier);	
			case 'd':
				set(p, "double");
				return parseType_L1(p, isdelegate, identifier);	
			case 'e':
				set(p, "real");
				return parseType_L1(p, isdelegate, identifier);	
			case 'o':
				set(p, "ifloat");
				return parseType_L1(p, isdelegate, identifier);	
			case 'p':
				set(p, "idouble");
				return parseType_L1(p, isdelegate, identifier);	
			case 'j':
				set(p, "ireal");
				return parseType_L1(p, isdelegate, identifier);	
			case 'q':
				set(p, "cfloat");
				return parseType_L1(p, isdelegate, identifier);	
			case 'r':
				p.append("cdouble");
				return parseType_L1(p, isdelegate, identifier);	
			case 'c':
				set(p, "creal");
				return parseType_L1(p, isdelegate, identifier);	
			case 'a':
				set(p, "char");
				return parseType_L1(p, isdelegate, identifier);	
			case 'u':
				set(p, "wchar");
				return parseType_L1(p, isdelegate, identifier);	
			case 'w':
				set(p, "dchar");
				return parseType_L1(p, isdelegate, identifier);	

			case 'A':
				// dynamic array
				set(p, parseType(name, ni)).append("[]");
				return parseType_L1(p, isdelegate, identifier);	

			case 'P':
				// pointer
				set(p, parseType(name, ni)).append("*");
				return parseType_L1(p, isdelegate, identifier);

			case 'G': // static array
			{
				int ns = ni[0];
				parseNumber(name, ni);
				int ne = ni[0];
				p.append(parseType(name, ni)).append("[").append(name.substring(ns, ne)).append("]");
				return parseType_L1(p, isdelegate, identifier);
			}

			case 'H':
				// associative array
				set(p, parseType(name, ni));
				set(p, parseType(name, ni).append("[").append(p).append("]"));
				return parseType_L1(p, isdelegate, identifier);

			case 'D':
				// delegate
				isdelegate = true;
				// goto Lagain;
				continue Lagain;

			case 'M':
				hasthisptr = true;
				// goto Lagain;
				continue Lagain;

			case 'F': // D function
			case 'U': // C function
			case 'W': // Windows function
			case 'V': // Pascal function
			case 'R': // C++ function
			{
				char mc = name.charAt(ni[0] - 1);
				StringBuilder args = new StringBuilder();

				while(true) {
					if(ni[0] >= name.length()) {
						throw new ParseException("", 0);
					}
					char c = name.charAt(ni[0]);
					if(c == 'Z') {
						break;
					}
					if(c == 'X') {
						if(args.length() == 0) {
							throw new ParseException("", 0);
						}
						args.append(" ...");
						break;
					}
					if(args.length() != 0) {
						args.append(", ");
					}
					switch(c) {
						case 'J':
							args.append("out ");
							ni[0]++;
							// goto default;
							args.append(parseType(name, ni));
							continue;

						case 'K':
							args.append("inout ");
							ni[0]++;
							// goto default;
							args.append(parseType(name, ni));
							continue;

						case 'L':
							args.append("lazy ");
							ni[0]++;
							// goto default;
							args.append(parseType(name, ni));
							continue;

						default:
							args.append(parseType(name, ni));
							continue;

						case 'Y':
							args.append("...");
							break;
					}
					break;
				}
				/* TODO mangling
				if(hasthisptr || isdelegate) {
					// add implicit 'this'/context pointer
					if(args.length() != 0) {
						args.append(", ");
					}
					args.append("void*");
				}
				*/
				ni[0]++;
				if(!isdelegate && identifier.length() != 0) {
					switch(mc) {
						case 'F':
							p.setLength(0);
						break; // D function
						case 'U':
							set(p, "extern (C) ");
						break; // C function
						case 'W':
							set(p, "extern (Windows) ");
						break; // Windows function
						case 'V':
							set(p, "extern (Pascal) ");
						break; // Pascal function
						default:
							throw new IllegalStateException();
					}
					set(p, parseType(name, ni)).append(" ").append(identifier).append("(").append(args).append(")");
					return p;
				}
				set(p, parseType(name, ni)).append((isdelegate ? " delegate(" : " function(")).append(args).append(")");
				isdelegate = false;
				return parseType_L1(p, isdelegate, identifier);
			}

			case 'C':
				set(p, "class ");
				return parseType_L2(name, ni, p, isdelegate, identifier);
			case 'S':
				set(p, "struct ");
				return parseType_L2(name, ni, p, isdelegate, identifier);
			case 'E':
				set(p, "enum ");
				return parseType_L2(name, ni, p, isdelegate, identifier);
			case 'T':
				set(p, "typedef ");
				return parseType_L2(name, ni, p, isdelegate, identifier);

			default:
				int i = ni[0] - 1;
				ni[0] = name.length();
				set(p, name.substring(i));
				return parseType_L1(p, isdelegate, identifier);
		}
		}
	}

	private static StringBuilder parseType_L1(StringBuilder p, boolean isdelegate, StringBuilder identifier) throws ParseException {
		if(isdelegate) {
			throw new ParseException("", 0); // 'D' must be followed by function
		}
		if(identifier.length() != 0) {
			p.append(" ").append(identifier);
		}
		return p;
	}
	
	private static StringBuilder parseType_L2(String name, int[] ni, StringBuilder p, boolean isdelegate, StringBuilder identifier) throws ParseException {
		p.append(parseQualifiedName(name, ni));
		return parseType_L1(p, isdelegate, identifier);
	}
	
	private static StringBuilder fparseTemplateInstanceName(String name, int[] ni) throws ParseException {
		StringBuilder result = parseSymbolName(name, ni).append("!(");
		int nargs = 0;

		while(true) {
			int[] i = { 0 };

			if(ni[0] >= name.length()) {
				throw new ParseException("", 0);
			}
			if(nargs != 0 && name.charAt(ni[0]) != 'Z') {
				result.append(", ");
			}
			nargs++;
			switch(name.charAt(ni[0]++)) {
				case 'T':
					result.append(parseType(name, ni));
					continue;

				case 'V':
					result.append(parseType(name, ni)).append(" ");
					if(ni[0] >= name.length()) {
						throw new ParseException("", 0);
					}
					switch(name.charAt(ni[0]++)) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							i[0] = ni[0] - 1;
							while(ni[0] < name.length() && Character.isDigit(name.charAt(ni[0]))) {
								ni[0]++;
							}
							result.append(name.substring(i[0], ni[0]));
							break;

						case 'N':
							i[0] = ni[0];
							while(ni[0] < name.length() && Character.isDigit(name.charAt(ni[0]))) {
								ni[0]++;
							}
							if(i[0] == ni[0]) {
								throw new ParseException("", 0);
							}
							result.append("-").append(name.substring(i[0], ni[0]));
							break;

						case 'n':
							result.append("null");
							break;

						case 'e':
							getReal(name, ni, i, result);
						break;

						case 'c':
							getReal(name, ni, i, result);
							result.append('+');
							getReal(name, ni, i, result);
							result.append('i');
						break;

						case 'a':
						case 'w':
						case 'd': {
							char m = name.charAt(ni[0] - 1);
							if(m == 'a')
								m = 'c';
							int n = parseNumber(name, ni);
							if(ni[0] >= name.length() || name.charAt(ni[0]++) != '_' || ni[0] + n * 2 > name.length()) {
								throw new ParseException("", 0);
							}
							result.append('"');
							for(i[0] = 0; i[0] < n; i[0]++) {
								char c;

								c = (char) ((ascii2hex(name.charAt((ni[0] + i[0] * 2)) << 4) + ascii2hex(name.charAt(ni[0] + i[0] * 2 + 1))));
								result.append(c);
							}
							ni[0] += n * 2;
							result.append('"');
							result.append(m);
							break;
						}

						default:
							throw new ParseException("", 0);
					}
					continue;

				case 'S':
					result.append(parseSymbolName(name, ni));
					continue;

				case 'Z':
				break;

				default:
					throw new ParseException("", 0);
			}
			break;
		}
		result.append(")");
		return result;
	}
	
	private static int ascii2hex(int c) throws ParseException {
		if (!isxdigit(c)) {
		    throw new ParseException("", 0);
		}
		return ( (c >= 'a') ? c - 'a' + 10 :
			(c >= 'A') ? c - 'A' + 10 :
				     c - '0'
		      );
	}

	private static boolean isxdigit(int c) {
		switch(c) {
		case '0': case '1': case '2': case '3': case '4': 
		case '5': case '6': case '7': case '8': case '9':
		case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
		case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
			return true;
		}
		return false;
	}

	// TODO: convert 80 bits to real??? wtf???
	private static void getReal(String name, int[] ni, int[] i, StringBuilder result) throws ParseException {
		BitSet p = new BitSet(80);

		if(ni[0] + 10 * 2 > name.length()) {
			throw new ParseException("", 0);
		}
		for(i[0] = 0; i[0] < 10; i[0]++) {
			int b;

			b = ((ascii2hex(name.charAt(ni[0] + i[0] * 2)) << 4) + ascii2hex(name.charAt(ni[0] + i[0] * 2 + 1)));
			p.set(i[0] * 8, (b & 0x00000001) != 0);
			p.set(i[0] * 8 + 1, (b & 0x00000010) != 0);
			p.set(i[0] * 8 + 2, (b & 0x00000100) != 0);
			p.set(i[0] * 8 + 3, (b & 0x00001000) != 0);
			p.set(i[0] * 8 + 4, (b & 0x00010000) != 0);
			p.set(i[0] * 8 + 5, (b & 0x00100000) != 0);
			p.set(i[0] * 8 + 6, (b & 0x01000000) != 0);
			p.set(i[0] * 8 + 7, (b & 0x10000000) != 0);
		}
		
		result.append(p);
		ni[0] += 10 * 2;
	}
	
	private static StringBuilder set(StringBuilder p, String contents) {
		p.setLength(0);
		p.append(contents);
		return p;
	}
	
	private static StringBuilder set(StringBuilder p, StringBuilder contents) {
		p.setLength(0);
		p.append(contents);
		return p;
	}

}
