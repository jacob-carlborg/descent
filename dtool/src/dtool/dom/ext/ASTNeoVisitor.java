package dtool.dom.ext;

import descent.core.domX.ASTNode;
import descent.core.domX.ASTVisitor;
import dtool.dom.ASTElement;
import dtool.dom.Definition;
import dtool.dom.Module;
import dtool.dom.Symbol;
import dtool.dom.UnconvertedElement;


/**
 * A visitor for abstract syntax trees.
 * <p>
 * For each different concrete AST node type <i>T</i> there are
 * a pair of methods:
 * <ul>
 * <li><code>public boolean visit(<i>T</i> node)</code> - Visits
 * the given node to perform some arbitrary operation. If <code>true</code>
 * is returned, the given node's child nodes will be visited next; however,
 * if <code>false</code> is returned, the given node's child nodes will 
 * not be visited. The default implementation provided by this class does
 * nothing and returns <code>true</code>.
 * Subclasses may reimplement this method as needed.</li>
 * <li><code>public void endVisit(<i>T</i> node)</code> - Visits
 * the given node to perform some arbitrary operation. When used in the
 * conventional way, this method is called after all of the given node's
 * children have been visited (or immediately, if <code>visit</code> returned
 * <code>false</code>). The default implementation provided by this class does
 * nothing. Subclasses may reimplement this method as needed.</li>
 * </ul>
 * </p>
 * In addition, there are a pair of methods for visiting AST nodes in the 
 * abstract, regardless of node type:
 * <ul>
 * <li><code>public void preVisit(IElement node)</code> - Visits
 * the given node to perform some arbitrary operation. 
 * This method is invoked prior to the appropriate type-specific
 * <code>visit</code> method.
 * The default implementation of this method does nothing.
 * Subclasses may reimplement this method as needed.</li>
 * <li><code>public void postVisit(IElement node)</code> - Visits
 * the given node to perform some arbitrary operation. 
 * This method is invoked after the appropriate type-specific
 * <code>endVisit</code> method.
 * The default implementation of this method does nothing.
 * Subclasses may reimplement this method as needed.</li>
 * </ul>
 * 
 * @see descent.core.IElement#accept(ElementVisitor)
 */
public abstract class ASTNeoVisitor extends ASTVisitor {

	
	public void preVisit(ASTNode elem) {
		// Default implementation: do nothing
	}

	public void postVisit(ASTNode elem) {
		// Default implementation: do nothing
	}
	
	
	/**
	 * Visits the element.
	 * @param element the element to visit
	 * @return true if children element should be visited
	 */


	public boolean visit(ASTElement elem) {
		return visitAsSuperType(elem, ASTElement.class);
	}
	
	public boolean visit(Module elem) {
		return visitAsSuperType(elem, Module.class);
	}
	
	public boolean visit(UnconvertedElement elem) {
		return visitAsSuperType(elem, UnconvertedElement.class);
	}

	public boolean visit(Symbol elem) {
		return visitAsSuperType(elem, Symbol.class);
	}
	
	public boolean visit(Definition elem) {
		return visitAsSuperType(elem, Definition.class);
	}
	
	
	/* ---------------------------------- */


}
