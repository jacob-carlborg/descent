package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.Declaration;
import descent.internal.core.dom.TemplateParameter;

/**
 * A template delcaration:
 */
public interface ITemplateDeclaration extends IDeclaration {
	
	/**
	 * Returns the name of the template.
	 */
	ISimpleName getName();
	
	/**
	 * Returns the template parameters.
	 */
	List<TemplateParameter> templateParameters();
	
	/**
	 * Returns the declaration definitions contained in
	 * this template.
	 */
	List<Declaration> declarations();

}
