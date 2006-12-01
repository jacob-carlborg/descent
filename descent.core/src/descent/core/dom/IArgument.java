package descent.core.dom;

/**
 * An argument to a function or something else:
 * 
 * <pre>
 * [ | in | out | inout | lazy ] type name [ = defaultValue ]
 * </pre>
 */
public interface IArgument extends IElement {
	
	/** The argument is in */
	int IN = 1;
	/** The argument is out */
	int OUT = 2;
	/** The argument is inout */
	int INOUT = 3;
	/** The argument is lazy */
	int LAZY = 4;
	
	/**
	 * Returns the name of this argument.
	 */
	ISimpleName getName();
	
	/**
	 * Returns the type of this argument.
	 */
	IType getType();
	
	/**
	 * Returns the default value of this argument, if any,
	 * or <code>null</code>.
	 */
	IExpression getDefaultValue();
	
	/**
	 * Returns the kind of this argument: in, out, inout or lazy.
	 * Check this interface constants.
	 */
	int getKind();

}
