package descent.internal.core;

import java.util.ArrayList;
import java.util.List;

import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.STC;
import descent.internal.compiler.parser.TypeBasic;

/**
 * A class for processing a signature.
 */
public class SignatureProcessor implements ISignatureConstants {
	
	/**
	 * The requestor for a signature processing. The processor notifies
	 * events to the given requestor.
	 */
	public static interface ISignatureRequestor {
		
		/**
		 * The processor has found a module.
		 * @param compoundName the fully qualified name of the module
		 * @signature the signature of the module
		 */
		void acceptModule(char[][] compoundName, String signature);
		
		/**
		 * The processos has found a symbol.
		 * @param type the type of the symbol
		 * @param name the name of the symbol
		 * @param startPosition if the symbol is local, this parameter holds the
		 * start position of it. Else, it holds the value -1.
		 * @signature the signature of the symbol
		 */
		void acceptSymbol(char type, char[] name, int startPosition, String signature);

		/**
		 * The processor has found a delegate.
		 * @signature the signature of the delegate
		 */
		void acceptDelegate(String signature);
		
		/**
		 * The processor has found a pointer.
		 * @param signature the signature from which the pointer originated
		 * @signature the signature of the pointer
		 */
		void acceptPointer(String signature);

		/**
		 * The processor has found a dynamic array.
		 * @param signature the signature from which the dynamic array originated
		 * @signature the signature of the dynamic array
		 */
		void acceptDynamicArray(String signature);

		/**
		 * The processor has found a static array.
		 * @param dimension the dimension of the static array
		 * @param signature the signature from which the static array originated
		 * @signature the signature of the static array
		 */
		void acceptStaticArray(int dimension, String signature);

		/**
		 * The processor has found an associative array.
		 * @param signature the signature from which the associative array originated
		 * @signature the signature of the associative array
		 */
		void acceptAssociativeArray(String signature);
		
		/**
		 * The processor is about to process a type function.
		 */
		void enterFunctionType();

		/**
		 * The processor has found a function type.
		 * @param link the linkage of the function
		 * @param signature the signature of the function type
		 */
		void exitFunctionType(LINK link, String signature);

		/**
		 * The processor has found a primitve type.
		 * @param type the primitive type
		 */
		void acceptPrimitive(TypeBasic type);

		/**
		 * The processor has found an argument break.
		 * @param c the argument break character
		 */
		void acceptArgumentBreak(char c);

		/**
		 * The requestor has found an argument modifier.
		 * @param stc the modifier
		 * @see STC#STCin
		 * @see STC#STCout
		 * @see STC#STClazy
		 */
		void acceptArgumentModifier(int stc);
				
	}
	
	/**
	 * Processes the given signature and notifies the requestor about the found
	 * elements.
	 * 
	 * <p>Type elements are notified in a bottom-up fashion. For example,
	 * if the signature is Pi (pointer to int), 
	 * {@link ISignatureRequestor#acceptPrimitive(TypeBasic)} will be called,
	 * followed by {@link ISignatureRequestor#acceptPointer()}.</p>
	 * 
	 * <p>Nested elements are notified in a top-down fashion. For example,
	 * if the signature is for a function A inside a function B, A will be
	 * notified before B.</p>
	 * 
	 * <p>Compound names for objects in object.d will be sent correctly, but
	 * the signature will remain the original. For example, if the signature
	 * is C6Object, the class { "object", "Object" } with signature C6Object
	 * will be notified.</p>
	 * 
	 * @param signature the signature to process
	 * @param requestor the signature processor requestor 
	 */
	public static void process(String signature, ISignatureRequestor requestor) {
		process0(signature, 0, requestor);
	}

	private static int process0(String signature, int i, ISignatureRequestor requestor) {
		if (signature == null || signature.length() == 0) {
			throw new IllegalArgumentException("Invalid signature: " + signature);
		}
		
		int localPosition = -1;		
		int start = i;
		
		while(i < signature.length()) {
			char first = signature.charAt(i);
			switch(first) {
			case MODULE: {
				int[] end = { 0 };
				char[][] compoundName = splitSignature(signature, i + 1, end);
				i = end[0];
				requestor.acceptModule(compoundName, signature.substring(start, i));				
				continue;
			}
			case CLASS:
			case STRUCT:
			case UNION:
			case INTERFACE:
			case ENUM:
			case ENUM_MEMBER:
			case VARIABLE:
			case ALIAS:
			case TYPEDEF:
			case FUNCTION:
				i++;
				char c = signature.charAt(i);
				if (!Character.isDigit(c)) {
					throw new IllegalStateException("Invalid signature: " + signature);
				}
				int n = 0;
				while(Character.isDigit(c)) {
					n = 10 * n + (c - '0');
					i++;
					c = signature.charAt(i);
				}
				char[] name = new char[n];
				signature.getChars(i, i + n, name, 0);
				i += n;
				
				if (first == FUNCTION) {
					i = process0(signature, i, requestor);
					requestor.acceptSymbol(first, name, localPosition, signature.substring(start, i));
				} else {
					requestor.acceptSymbol(first, name, localPosition, signature.substring(start, i));
				}
				
				localPosition = -1;				
				continue;
			case OTHER:
				i++;
				requestor.acceptSymbol(first, null, localPosition, signature.substring(start, i));
				localPosition = -1;
				continue;
			case 'D': { // delegate
				i = process0(signature, i + 1, requestor);
				requestor.acceptDelegate(signature.substring(start, i));
				return i;
			}
			case 'P': { // pointer
				i = process0(signature, i + 1, requestor);
				requestor.acceptPointer(signature.substring(start, i));
				return i;
			}
			case 'A': { // dynamic array
				i = process0(signature, i + 1, requestor);
				requestor.acceptDynamicArray(signature.substring(start, i));
				return i;
			}
			case 'G': { // static array
				int dimension = 0;
				i++;
				for(; i < signature.length(); i++) {
					c = signature.charAt(i);
					while(Character.isDigit(c)) {
						dimension = 10 * dimension + (c - '0');
						i++;
						c = signature.charAt(i);
					}
					break;
				}
				
				i = process0(signature, i, requestor);
				requestor.acceptStaticArray(dimension, signature.substring(start, i));
				return i;
			}
			case 'H': {// associative array
				i = process0(signature, i + 1, requestor);
				i = process0(signature, i, requestor);
				requestor.acceptAssociativeArray(signature.substring(start, i));
				return i;
			}
			case 'F': // Type function
			case 'U':
			case 'W':
			case 'V':
			case 'R': {
				requestor.enterFunctionType();
				
				LINK link;
				switch(first) {
				case 'F': link = LINK.LINKd; break;
				case 'U': link = LINK.LINKc; break;
				case 'W': link = LINK.LINKwindows; break;
				case 'V': link = LINK.LINKpascal; break;
				case 'R': link = LINK.LINKcpp; break;
				default: throw new IllegalStateException("Should not happen");
				}
				
				i++;
				while(signature.charAt(i) != 'X' && signature.charAt(i) != 'Y' && signature.charAt(i) != 'Z') {
					i = argumentModifier(signature, i, requestor);
					i = process0(signature, i, requestor);
				}
				
				requestor.acceptArgumentBreak(signature.charAt(i));
				
				i = argumentModifier(signature, i + 1, requestor);				
				i = process0(signature, i, requestor);
				// TODO varargs
				requestor.exitFunctionType(link, signature.substring(start, i));
				return i;
			}
			case 'X': // Argument break
			case 'Y':
			case 'Z':
				return i;
			case POSITION:
				n = 0;
				i++;
				c = signature.charAt(i);
				while(Character.isDigit(c)) {
					n = 10 * n + (c - '0');
					i++;
					c = signature.charAt(i);
				}
				localPosition = n;
				continue;
			default:
				// Try with type basic
				TypeBasic type = TypeBasic.fromSignature(first);
				if (type != null) {
					requestor.acceptPrimitive(type);
					return i + 1;
				} else {
					throw new IllegalArgumentException("Invalid signature: " + signature);
				}
			}
		}
		
		return i;
	}
	
	private static int argumentModifier(String signature, int i, ISignatureRequestor requestor) {
		char c = signature.charAt(i);
		switch(c) {
		case 'J': requestor.acceptArgumentModifier(STC.STCout); i++; break;
		case 'K': requestor.acceptArgumentModifier(STC.STCref); i++; break;
		case 'L': requestor.acceptArgumentModifier(STC.STClazy); i++; break;
		default: requestor.acceptArgumentModifier(STC.STCin); break;
		}
		return i;
	}
	
	/*
	 * Given a signature like 4test3foo it returns ["test", "foo"].
	 * After the function call, length[0] will return the consumed
	 * signature length.
	 */
	private static char[][] splitSignature(String signature, int start, int[] end) {
		List<char[]> piecesList = new ArrayList<char[]>();
		
		int i;
		for(i = start; i < signature.length(); i++) {
			char c = signature.charAt(i);
			if (!Character.isDigit(c)) {
				break;
			}
			int n = 0;
			while(Character.isDigit(c)) {
				n = 10 * n + (c - '0');
				i++;
				c = signature.charAt(i);
			}
			String name = signature.substring(i, i + n);
			piecesList.add(name.toCharArray());
			i += n - 1;
		}
		
		end[0] = i;
		
		return (char[][]) piecesList.toArray(new char[piecesList.size()][]);
	}

}
