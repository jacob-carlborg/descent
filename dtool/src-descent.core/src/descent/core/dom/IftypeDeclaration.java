package descent.core.dom;



/**
 * The <i>deprecated</i> iftype declaration AST node type.
 *
 * <pre>
 * IftypeDeclaration:
 *    { Modifier } <b>iftype</b> <b>(</b> Type [ SimpleName ] [ [ <b>:</b> | <b>==</b> ] Type ] <b>)</b> { Declaration }  [ <b>else</b> { Declaration } ]
 * </pre>
 */
public class IftypeDeclaration {
	
	/**
	 * The kind of comparison.
	 */
	public static enum Kind {
		/** No comparison */
		NONE,
		/** Comparison made with <b>=</b> */
		EQUALS,
		/** Comparison made with <b>:</b> */
		EXTENDS
	}
	

}
