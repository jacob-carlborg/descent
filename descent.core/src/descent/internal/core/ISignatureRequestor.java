package descent.internal.core;

import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.STC;
import descent.internal.compiler.parser.TypeBasic;

/**
 * The requestor for a signature processing. The processor notifies
 * events to the given requestor.
 * @see SignatureRequestorAdapter
 */
public interface ISignatureRequestor {

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
	void acceptSymbol(char type, char[] name, int startPosition,
			String signature);
	
	/**
	 * The processor has found an identifier.
	 * @param name the identifier name
	 * @param signature the signature of the identifier
	 */
	void acceptIdentifier(char[] name, String signature);

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
	 * The processor has found an argument modifier.
	 * @param stc the modifier
	 * @see STC#STCin
	 * @see STC#STCout
	 * @see STC#STClazy
	 */
	void acceptArgumentModifier(int stc);

	/**
	 * The processor will start signaling template parameters.
	 */
	void enterTemplateParameters();

	/**
	 * The processor stoped processing template parameters.
	 */
	void exitTemplateParameters();

	/**
	 * The processor has found a template tuple parameter.
	 */
	void acceptTemplateTupleParameter();

	/**
	 * The processor has found a template alias parameter and there
	 * is a change that it will report the specific type.
	 */
	void enterTemplateAliasParameter();

	/**
	 * The processor has finished processing a template alias parameter
	 */
	void exitTemplateAliasParameter(String signature);

	/**
	 * The processor has found a template type parameter and there
	 * is a change that it will report the specific type.
	 */
	void enterTemplateTypeParameter();

	/**
	 * The processor has finished processing a template type parameter
	 */
	void exitTemplateTypeParameter(String signature);

	/**
	 * The processor has found a template value parameter. Next it
	 * will report a type and there is a change that it will report 
	 * the specific value.
	 */
	void enterTemplateValueParameter();

	/**
	 * The processor is reporting the specific value of a
	 * template value parameter.
	 */
	void acceptTemplateValueParameterSpecificValue(Expression exp);

	/**
	 * The processor has finished processing a template value parameter
	 */
	void exitTemplateValueParameter(String signature);

}