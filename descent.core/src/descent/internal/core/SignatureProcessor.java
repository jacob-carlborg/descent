package descent.internal.core;

import java.util.ArrayList;
import java.util.List;

import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.STC;
import descent.internal.compiler.parser.TypeBasic;

/**
 * A class for processing a signature.
 * 
 * @see ISignatureRequestor
 */
public class SignatureProcessor implements ISignatureConstants {	
	
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
	 * @param signature the signature to process
	 * @param requestor the signature processor requestor 
	 */
	public static void process(String signature, ISignatureRequestor requestor) {
		process0(signature, 0, requestor);
	}

	public static int process0(String signature, int i, ISignatureRequestor requestor) {
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
			case TEMPLATE:
			case TEMPLATED_CLASS:
			case TEMPLATED_STRUCT:
			case TEMPLATED_UNION:
			case TEMPLATED_INTERFACE:
			case TEMPLATED_FUNCTION:
				i++;
				char c = signature.charAt(i);
				if (!Character.isDigit(c)) {
					throw new IllegalArgumentException("Invalid signature: " + signature);
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
				
				if (first == FUNCTION || first == TEMPLATED_FUNCTION) {
					i = process0(signature, i, requestor);
				}
				
				if (first == TEMPLATE || first == TEMPLATED_FUNCTION ||
						first == TEMPLATED_CLASS || first == TEMPLATED_STRUCT ||
						first == TEMPLATED_UNION || first == TEMPLATED_INTERFACE) {
					requestor.enterTemplateParameters();
					
					while(signature.charAt(i) != TEMPLATE_PARAMETERS_BREAK) {
						i = process0(signature, i, requestor);
					}
					i++;
					
					requestor.exitTemplateParameters();
				}
				
				requestor.acceptSymbol(first, name, localPosition, signature.substring(start, i));
				
				localPosition = -1;
				
				// If a symbol does not come next, don't process further
				if (i < signature.length() && !isSymbol(signature.charAt(i)) ) {
					return i;
				} else {
					continue;
				}
			case UNIT_TEST_INVARIANT_STATIC_CTOR_STATIC_DTOR:
				i++;
				requestor.acceptSymbol(first, null, localPosition, signature.substring(start, i));
				localPosition = -1;
				continue;
			case DELEGATE: {
				i = process0(signature, i + 1, requestor);
				requestor.acceptDelegate(signature.substring(start, i));
				return i;
			}
			case POINTER: { // pointer
				i = process0(signature, i + 1, requestor);
				requestor.acceptPointer(signature.substring(start, i));
				return i;
			}
			case DYNAMIC_ARRAY: { // dynamic array
				i = process0(signature, i + 1, requestor);
				requestor.acceptDynamicArray(signature.substring(start, i));
				return i;
			}
			case STATIC_ARRAY: { // static array
				i++;
				
				i = process0(signature, i, requestor);
				
				c = signature.charAt(i);
				
				n = 0;
				
				while(c != STATIC_ARRAY) {
					n = 10 * n + (c - '0');
					i++;
					c = signature.charAt(i);
				}
				i++;
				
				char[] dimension = signature.substring(i, i + n).toCharArray();
				
				i += n;
				
				requestor.acceptStaticArray(dimension, signature.substring(start, i));
				return i;
			}
			case ASSOCIATIVE_ARRAY: {// associative array
				i = process0(signature, i + 1, requestor);
				i = process0(signature, i, requestor);
				requestor.acceptAssociativeArray(signature.substring(start, i));
				return i;
			}
			case LINK_D: // Type function
			case LINK_C:
			case LINK_WINDOWS:
			case LINK_PASCAL:
			case LINK_CPP: {
				requestor.enterFunctionType();
				
				LINK link;
				switch(first) {
				case LINK_D: link = LINK.LINKd; break;
				case LINK_C: link = LINK.LINKc; break;
				case LINK_WINDOWS: link = LINK.LINKwindows; break;
				case LINK_PASCAL: link = LINK.LINKpascal; break;
				case LINK_CPP: link = LINK.LINKcpp; break;
				default: throw new IllegalStateException("Should not happen");
				}
				
				i++;
				while(signature.charAt(i) != FUNCTION_PARAMETERS_BREAK_VARIADIC2 && 
						signature.charAt(i) != FUNCTION_PARAMETERS_BREAK_VARIADIC && 
						signature.charAt(i) != FUNCTION_PARAMETERS_BREAK) {
					i = argumentModifier(signature, i, requestor);
					i = process0(signature, i, requestor);
				}
				
				requestor.acceptArgumentBreak(signature.charAt(i));
				i++;
				
				i = process0(signature, i, requestor);
				requestor.exitFunctionType(link, signature.substring(start, i));
				return i;
			}
			case TEMPLATE_TUPLE_PARAMETER:
				requestor.acceptTemplateTupleParameter();
				return i + 1;
			case TEMPLATE_ALIAS_PARAMETER:
				requestor.enterTemplateAliasParameter();
				
				i++;
				if (i < signature.length() && signature.charAt(i) == TEMPLATE_ALIAS_PARAMETER2) {
					i = process0(signature, i+1, requestor);
				}
				
				requestor.exitTemplateAliasParameter(signature.substring(start, i));
				return i;
			case TEMPLATE_TYPE_PARAMETER:
				requestor.enterTemplateTypeParameter();
				
				i++;
				if (i < signature.length() && signature.charAt(i) == TEMPLATE_TYPE_PARAMETER2) {
					i = process0(signature, i+1, requestor);
				}
				
				requestor.exitTemplateTypeParameter(signature.substring(start, i));
				return i;
			case TEMPLATE_VALUE_PARAMETER:
				requestor.enterTemplateValueParameter();
				
				i = process0(signature, i + 1, requestor);
				
				c = signature.charAt(i);
				if (i < signature.length() && Character.isDigit(c)) {
					n = 0;
					
					while(c != TEMPLATE_VALUE_PARAMETER) {
						n = 10 * n + (c - '0');
						i++;
						c = signature.charAt(i);
					}
					i++;
					
					requestor.acceptTemplateValueParameterSpecificValue(signature.substring(i, i + n).toCharArray());
					
					i += n;
				}
				
				requestor.exitTemplateValueParameter(signature.substring(start, i));
				return i;
			case TYPEOF:
				i++;
				c = signature.charAt(i);
				
				n = 0;
				
				while(c != TYPEOF) {
					n = 10 * n + (c - '0');
					i++;
					c = signature.charAt(i);
				}
				i++;
				
				requestor.acceptTypeof(signature.substring(i, i + n).toCharArray(),
								signature.substring(start, i + n));
				
				i += n;
				
				return i;
			case SLICE:
				i = process0(signature, i + 1, requestor);
				
				c = signature.charAt(i);
				n = 0;
				
				while(c != SLICE) {
					n = 10 * n + (c - '0');
					i++;
					c = signature.charAt(i);
				}
				i++;
				
				char[] lwr = signature.substring(i, i + n).toCharArray();
				
				i += n;
				
				c = signature.charAt(i);
				n = 0;
				
				while(c != SLICE) {
					n = 10 * n + (c - '0');
					i++;
					c = signature.charAt(i);
				}
				i++;
				
				char[] upr = signature.substring(i, i + n).toCharArray();
				
				requestor.acceptSlice(lwr, upr, signature.substring(start, i + n));
				
				i += n;
				
				return i;
			case MODIFIER_OUT:
			case MODIFIER_REF: 
			case MODIFIER_LAZY:
				i = argumentModifier(signature, i, requestor);
				continue;
			case TEMPLATE_PARAMETERS_BREAK: // Template parameters break
			case FUNCTION_PARAMETERS_BREAK_VARIADIC2: // Argument break
			case FUNCTION_PARAMETERS_BREAK_VARIADIC:
			case FUNCTION_PARAMETERS_BREAK:
				return i;
			case TEMPLATE_INSTANCE:
				requestor.enterTemplateInstance();

				i++;
				while(signature.charAt(i) != TEMPLATE_PARAMETERS_BREAK) {
					i = process0(signature, i, requestor);
				}
				i++;
				
				requestor.exitTemplateInstance(signature.substring(start, i));
				continue;
			case TEMPLATE_INSTANCE_TYPE:
				requestor.enterTemplateInstanceType();
				i++;
				i = process0(signature, i, requestor);
				requestor.exitTemplateInstanceTypeParameter(signature.substring(start, i));
				return i;
			case TEMPLATE_INSTANCE_VALUE:
				i++;
				c = signature.charAt(i);
				if (i < signature.length() && Character.isDigit(c)) {
					n = 0;
					
					while(c != TEMPLATE_INSTANCE_VALUE) {
						n = 10 * n + (c - '0');
						i++;
						c = signature.charAt(i);
					}
					i++;
					
					requestor.acceptTemplateInstanceValue(signature.substring(i, i + n).toCharArray(), signature.substring(start, i + n));
					
					i += n;
				}
				
				return i;
			case TEMPLATE_INSTANCE_SYMBOL:
				requestor.enterTemplateInstanceSymbol();
				i = process0(signature, i + 1, requestor);
				requestor.exitTemplateInstanceSymbol(signature.substring(start, i));
				return i;
			case IDENTIFIER:
				int[] end = { 0 };
				char[][] compoundName = splitSignature(signature, i + 1, end);
				i = end[0];
				requestor.acceptIdentifier(compoundName, signature.substring(start, i));
				
				// A template instance may follow an identifier
				if (i < signature.length() && signature.charAt(i) == TEMPLATE_INSTANCE) {
					continue;
				} else {
					return i;
				}
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
				requestor.acceptPosition(localPosition);
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
	
	private static boolean isSymbol(char c) {
		switch(c) {
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
		case TEMPLATE:
		case TEMPLATED_CLASS:
		case TEMPLATED_STRUCT:
		case TEMPLATED_UNION:
		case TEMPLATED_INTERFACE:
		case TEMPLATED_FUNCTION:
		case TEMPLATE_INSTANCE:
		case POSITION:
			return true;
		default:
			return false;
		}
	}
	
	private static int argumentModifier(String signature, int i, ISignatureRequestor requestor) {
		char c = signature.charAt(i);
		switch(c) {
		case MODIFIER_OUT: requestor.acceptArgumentModifier(STC.STCout); i++; break;
		case MODIFIER_REF: requestor.acceptArgumentModifier(STC.STCref); i++; break;
		case MODIFIER_LAZY: requestor.acceptArgumentModifier(STC.STClazy); i++; break;
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
