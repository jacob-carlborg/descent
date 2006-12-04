package descent.core.dom;

import java.util.List;

import descent.internal.core.dom.AggregateDeclaration;
import descent.internal.core.dom.BaseClass;
import descent.internal.core.dom.Declaration;
import descent.internal.core.dom.SimpleName;
import descent.internal.core.dom.TemplateParameter;

/**
 * <p>Represents an aggregate declaration, such as a class, interface, struct or union.</p>
 * 
 * <p>Note that if the aggregate is templated (i.e. <code>class Foo(T) { }</code>) the parser
 * dosen't generate a template declaration: instead, it makes this class templated.</p>
 */
public interface IAggregateDeclaration extends IDeclaration, IModifiersContainer, ICommented {
	
	AggregateDeclaration.Kind getKind();
	
	/**
	 * Returns the name of this aggregate. May be <code>null</code> if this
	 * is annonymous.
	 */
	SimpleName getName();
	
	/**
	 * Returns the base classes of this aggregate. May be empty but
	 * never <code>null</code>.
	 */
	List<BaseClass> baseClasses();
	
	/**
	 * Returns the declaration definitions contained in this aggregate.
	 * May be empty but never <code>null</null>.
	 */
	List<Declaration> declarations();
	
	/**
	 * Returns the template parameters. Pre: isTemplate().
	 */
	List<TemplateParameter> templateParameters();

}
