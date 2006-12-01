package descent.core.domX;

import descent.core.dom.IBinaryExpression;
import descent.core.dom.IDebugDeclaration;
import descent.core.dom.IElement;
import descent.core.dom.IIftypeDeclaration;
import descent.core.dom.IUnaryExpression;
import descent.core.dom.IVersionDeclaration;
import descent.internal.core.dom.AggregateDeclaration;
import descent.internal.core.dom.BinaryExpression;
import descent.internal.core.dom.ConditionalDeclaration;
import descent.internal.core.dom.Declaration;
import descent.internal.core.dom.Dsymbol;
import descent.internal.core.dom.Expression;
import descent.internal.core.dom.FuncDeclaration;
import descent.internal.core.dom.Identifier;
import descent.internal.core.dom.Import;
import descent.internal.core.dom.ImportDeclaration;
import descent.internal.core.dom.Initializer;
import descent.internal.core.dom.Module;
import descent.internal.core.dom.ModuleDeclaration;
import descent.internal.core.dom.ScopeDsymbol;
import descent.internal.core.dom.Statement;
import descent.internal.core.dom.TemplateParameter;
import descent.internal.core.dom.Type;
import descent.internal.core.dom.UnaryExpression;
import descent.internal.core.dom.VarDeclaration;


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
public abstract class ASTVisitor extends CommonTreeVisitor {

	public void preVisit(ASTNode elem) {
		// Default implementation: do nothing
	}

	public void postVisit(ASTNode elem) {
		// Default implementation: do nothing
	}
	
	/* ====================================================== */

	/**
	 * Visits the element.
	 * @param element the element to visit
	 * @return true if children element should be visited
	 */
	public boolean visit(ASTNode elem) {
		return true;
	}
	
	/* -----------  Abstract Classes  ----------- */
	public boolean visit(AbstractElement element) {
		ensureVisitIsNotDirectVisit(element);
		return visitAsSuperType(element, AbstractElement.class);
	}


	public boolean visit(Dsymbol element) {
		ensureVisitIsNotDirectVisit(element);
		return visitAsSuperType(element, Dsymbol.class);
	}

	public boolean visit(Declaration element) {
		ensureVisitIsNotDirectVisit(element);
		return visitAsSuperType(element, Declaration.class);
	}

	public boolean visit(Initializer element) {
		ensureVisitIsNotDirectVisit(element);
		return visitAsSuperType(element, Initializer.class);
	}

	public boolean visit(TemplateParameter element) {
		ensureVisitIsNotDirectVisit(element);
		return visitAsSuperType(element, TemplateParameter.class);
	}

	public boolean visit(ScopeDsymbol element) {
		ensureVisitIsNotDirectVisit(element);
		return visitAsSuperType(element, ScopeDsymbol.class);
	}
	
	public boolean visit(AggregateDeclaration element) {
		ensureVisitIsNotDirectVisit(element);
		return visitAsSuperType(element, AggregateDeclaration.class);
	}

	public boolean visit(Statement element) {
		ensureVisitIsNotDirectVisit(element);
		return visitAsSuperType(element, Statement.class);
	}

	public boolean visit(Type element) {
		return visitAsSuperType(element, Type.class);
	}
	
	public boolean visit(Expression element) {
		ensureVisitIsNotDirectVisit(element);
		return visitAsSuperType(element, Expression.class);
	}

	public boolean visit(BinaryExpression element) {
		ensureVisitIsNotDirectVisit(element);
		return visitAsSuperType(element, BinaryExpression.class);
	}

	public boolean visit(UnaryExpression element) {
		ensureVisitIsNotDirectVisit(element);
		return visitAsSuperType(element, UnaryExpression.class);
	}

	/* -----------  Concrete Classes  ----------- */

	public boolean visit(Module element) {
		return visitAsSuperType(element, Module.class);
	}
	public boolean visit(ModuleDeclaration element) {
		return visitAsSuperType(element, ModuleDeclaration.class);
	}
	public boolean visit(ImportDeclaration element) {
		return visitAsSuperType(element, ImportDeclaration.class);
	}
	public boolean visit(Import element) {
		return visitAsSuperType(element, Import.class);
	}
	
	/* -----  Defs  ----- */
	
	public boolean visit(FuncDeclaration element) {
		return visitAsSuperType(element, FuncDeclaration.class);
	}
	public boolean visit(VarDeclaration element) {
		return visitAsSuperType(element, VarDeclaration.class);
	}
	public boolean visit(ConditionalDeclaration element) {
		return visitAsSuperType(element, ConditionalDeclaration.class);
	}

	/* -----  Other concretes  ----- */

	public boolean visit(Identifier element) {
		return visitAsSuperType(element, Identifier.class);
	}
	
	/* ------------- Ary Interfaces -------------------- */

	public boolean visit(IBinaryExpression element) {
		return visit((BinaryExpression)element);
	}
	
	public boolean visit(IUnaryExpression element) {
		return visit((UnaryExpression)element);
	}
	
	public boolean visit(IDebugDeclaration element) {
		return visit((ConditionalDeclaration)element);
	}
	
	public boolean visit(IIftypeDeclaration element) {
		return visit((ConditionalDeclaration)element);
	}
	
	public boolean visit(IVersionDeclaration element) {
		return visit((ConditionalDeclaration)element);
	}

	
	public boolean visit(IElement element) {
		return visit((AbstractElement)element);
	}

	/* ====================================================== */

	/**
	 * Ends the visit to the element.
	 * @param element the element to visit
	 */
	public void endVisit(ASTNode element) {
	}
	
	public void endVisit(IElement element) {
		endVisit((ASTNode) element);
	}
}
