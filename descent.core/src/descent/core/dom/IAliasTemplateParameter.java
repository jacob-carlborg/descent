package descent.core.dom;

import descent.internal.core.dom.Type;

/**
 * A template alias parameter:
 * 
 * <pre>
 * alias name : specificType = defaultType
 * </pre>
 */
public interface IAliasTemplateParameter extends ITemplateParameter {
	
	/**
	 * Returns the specific type, if any, or null.
	 */
	Type getSpecificType();
	
	/**
	 * Returns the default type, if any, or null.
	 */
	Type getDefaultType();

}
