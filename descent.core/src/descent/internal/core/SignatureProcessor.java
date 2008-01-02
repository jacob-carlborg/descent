package descent.internal.core;

import java.util.ArrayList;
import java.util.List;

import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.STC;
import descent.internal.compiler.parser.TypeBasic;

/**
 * A class for processing a signature.
 */
public class SignatureProcessor {
	
	/**
	 * The requestor for a signature processing. The processor notifies
	 * events to the given requestor.
	 */
	public static interface ISignatureRequestor {

		/**
		 * The processor has found a function.
		 * @param compoundName the fully qualified name of the function
		 * @param signature the signature from which the function originated
		 */
		void acceptFunction(char[][] compoundName, String signature);

		/**
		 * The processor has found an enum.
		 * @param compoundName the fully qualified name of the enum 
		 * @param signature the signature from which the enum originated
		 */
		void acceptEnum(char[][] compoundName, String signature);

		/**
		 * The processor has found a class.
		 * @param compoundName the fully qualified name of the class
		 * @param signature the signature from which the class originated
		 */
		void acceptClass(char[][] compoundName, String signature);

		/**
		 * The processor has found a struct.
		 * @param compoundName the fully qualified name of the struct
		 * @param signature the signature from which the struct originated
		 */
		void acceptStruct(char[][] compoundName, String signature);

		/**
		 * The processor has found a variable, alias or typedef.
		 * @param compoundName the fully qualified name of the variable,
		 * alias or typedef
		 * @param signature the signature from which the variable, alias
		 * or typedef originated
		 */
		void acceptVariableAliasOrTypedef(char[][] compoundName, String signature);

		/**
		 * The processor has found a delegate.
		 * @param signature the signature from which the delegate originated
		 */
		void acceptDelegate(String signature);
		
		/**
		 * The processor has found a pointer.
		 * @param signature the signature from which the pointer originated
		 */
		void acceptPointer(String signature);

		/**
		 * The processor has found a dynamic array.
		 * @param signature the signature from which the dynamic array originated
		 */
		void acceptDynamicArray(String signature);

		/**
		 * The processor has found a static array.
		 * @param dimension the dimension of the static array
		 * @param signature the signature from which the static array originated
		 */
		void acceptStaticArray(int dimension, String signature);

		/**
		 * The processor has found an associative array.
		 * @param signature the signature from which the associative array originated
		 */
		void acceptAssociativeArray(String signature);
		
		/**
		 * The processor is about to process a type function.
		 */
		void enterFunctionType();

		/**
		 * The processor has found a function type.
		 * @param link the linkage of the function
		 * @param signature the signature from which the function type originated
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
		process0(signature, requestor);
	}
	
	/*
	 * The returned value is the number of characters consumed of the signature.
	 */
	private static int process0(String signature, ISignatureRequestor requestor) {
		if (signature == null || signature.length() == 0) {
			throw new IllegalArgumentException("Invalid signature: " + signature);
		}
		
		String oldSignature = signature;
		signature = JavaElementFinder.correct(signature);
		
		int oldLength = oldSignature.length();		
		int newLength = signature.length();
		int diff = newLength - oldLength;
		int consumed = process1(signature, oldSignature, diff, requestor);
		return consumed - diff;
	}
	
	private static int process1(String signature, String originalSignature, int diff, ISignatureRequestor requestor) {
		if (signature == null || signature.length() == 0) {
			throw new IllegalArgumentException("Invalid signature: " + signature);
		} else {
			char first = signature.charAt(0);
			
			switch(first) {
			case 'E': // enum
			case 'C': // class
			case 'S': // struct
			case 'Q': // var, alias, typedef
			case 'O': // function
				char second = signature.charAt(1);
				if (Character.isDigit(second)) {
					int[] lengthTemp = { 0 };
					char[][] compoundName = splitSignature(signature, 1, lengthTemp);
					int i = lengthTemp[0];
					return finishFullyQualifiedName(signature, originalSignature, diff, first, compoundName, i, requestor);
				} else {
					int length1 = process0(signature.substring(1), requestor);
					int[] length2 = { 0 };
					char[][] compoundName = splitSignature(signature, length1 + 2, length2);
					int i = length2[0];
					return finishFullyQualifiedName(signature, originalSignature, diff, first, compoundName, i, requestor);
				}
			case 'D': { // delegate
				int length = process0(signature.substring(1), requestor);
				int consumed = length + 1;
				requestor.acceptDelegate(signature.substring(0, consumed));
				return consumed;
			}
			case 'P': { // pointer
				int length = process0(signature.substring(1), requestor);
				int consumed = length + 1;
				requestor.acceptPointer(signature.substring(0, consumed));
				return consumed;
			}
			case 'A': { // dynamic array
				int length = process0(signature.substring(1), requestor);
				int consumed = length + 1;
				requestor.acceptDynamicArray(signature.substring(0, consumed));
				return consumed;
			}
			case 'G': { // static array
				int dimension = 0;
				int i;
				for(i = 1; i < signature.length(); i++) {
					char c = signature.charAt(i);
					while(Character.isDigit(c)) {
						dimension = 10 * dimension + (c - '0');
						i++;
						c = signature.charAt(i);
					}
					break;
				}
				
				int length = process0(signature.substring(i), requestor);
				int consumed = length + i;
				requestor.acceptStaticArray(dimension, signature.substring(0, consumed));
				return consumed;
			}
			case 'H': {// associative array
				int length1 = process0(signature.substring(1), requestor);
				int length2 = process0(signature.substring(1 + length1), requestor);
				int consumed = length1 + length2 + 1;
				requestor.acceptAssociativeArray(signature.substring(0, consumed));
				return consumed;
			}
			case 'F': // Type function
			case 'U':
			case 'W':
			case 'V':
			case 'R':
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
				
				int i = 1;
				char c = signature.charAt(i);
				switch(c) {
				case 'J': requestor.acceptArgumentModifier(STC.STCout); i++; break;
				case 'K': requestor.acceptArgumentModifier(STC.STCin | STC.STCout); i++; break;
				case 'L': requestor.acceptArgumentModifier(STC.STClazy); i++; break;
				default: requestor.acceptArgumentModifier(STC.STCin); break;
				}
				
				int length = process0(signature.substring(i), requestor);
				while(length != 0) {
					i += length;
					
					c = signature.charAt(i);
					switch(c) {
					case 'J': requestor.acceptArgumentModifier(STC.STCout); i++; break;
					case 'K': requestor.acceptArgumentModifier(STC.STCin | STC.STCout); i++; break;
					case 'L': requestor.acceptArgumentModifier(STC.STClazy); i++; break;
					default: requestor.acceptArgumentModifier(STC.STCin); break;
					}
					
					length = process0(signature.substring(i), requestor);
				}
				
				i++;
				
				length = process0(signature.substring(i), requestor);
				
				int consumed = i + length;
				
				// TODO varargs
				requestor.exitFunctionType(link, signature.substring(0, consumed));
				return consumed;
			case 'X': // Argument break
			case 'Y':
			case 'Z':
				requestor.acceptArgumentBreak(first);
				return 0;
			default: // Try with type basic
				TypeBasic type = TypeBasic.fromSignature(first);
				if (type != null) {
					requestor.acceptPrimitive(type);
					return 1;
				} else {
					throw new IllegalArgumentException("Invalid signature: " + signature);
				}
			}
		}
	}
	
	/*
	 * Given a signature like 4test3foo it returns ["test", "foo"].
	 * After the function call, length[0] will return the consumed
	 * signature length.
	 */
	private static char[][] splitSignature(String signature, int start, int[] length) {
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
		
		length[0] = i;
		
		return (char[][]) piecesList.toArray(new char[piecesList.size()][]);
	}
	
	private static int finishFullyQualifiedName(String signature, String originalSignature, int dif, char first, char[][] compoundName, int i, ISignatureRequestor requestor) {
		// If it's a function, parse the parameters
		if (first == 'O') {
			int length = process0(signature.substring(i), requestor);
			int consumed = length + i;
			requestor.acceptFunction(compoundName, originalSignature.substring(0, consumed - dif));
			return consumed;
		} else {
			int consumed = i;
			switch(first) {
			case 'E': // enum
				requestor.acceptEnum(compoundName, originalSignature.substring(0, consumed - dif));
				break;
			case 'C': // class
				requestor.acceptClass(compoundName, originalSignature.substring(0, consumed - dif));
				break;
			case 'S': // struct
				requestor.acceptStruct(compoundName, originalSignature.substring(0, consumed - dif));
				break;
			case 'Q': // var, alias or typedef
				requestor.acceptVariableAliasOrTypedef(compoundName, originalSignature.substring(0, consumed - dif));
				break;
			}
			return consumed;
		}
	}

}
