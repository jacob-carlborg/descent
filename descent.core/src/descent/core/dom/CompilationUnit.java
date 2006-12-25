package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.compiler.IProblem;

/**
 * Compilation unit AST node.
 * 
 * <pre>
 * CompilationUnit:
 *    [ ModuleDeclaration ]
 *    { Declaration }
 * </pre>
 */
public class CompilationUnit extends ASTNode {
	
	/**
	 * The "moduleDeclaration" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor MODULE_DECLARATION_PROPERTY =
		new ChildPropertyDescriptor(CompilationUnit.class, "moduleDeclaration", ModuleDeclaration.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "declarations" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor DECLARATIONS_PROPERTY =
		new ChildListPropertyDescriptor(CompilationUnit.class, "declarations", Declaration.class, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(2);
		createPropertyList(CompilationUnit.class, properyList);
		addProperty(MODULE_DECLARATION_PROPERTY, properyList);
		addProperty(DECLARATIONS_PROPERTY, properyList);
		PROPERTY_DESCRIPTORS = reapPropertyList(properyList);
	}

	/**
	 * Returns a list of structural property descriptors for this node type.
	 * Clients must not modify the result.
	 * 
	 * @param apiLevel the API level; one of the
	 * <code>AST.JLS*</code> constants

	 * @return a list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor})
	 * @since 3.0
	 */
	public static List propertyDescriptors(int apiLevel) {
		return PROPERTY_DESCRIPTORS;
	}

	/**
	 * The moduleDeclaration.
	 */
	private ModuleDeclaration moduleDeclaration;

	/**
	 * The declarations
	 * (element type: <code>Declaration</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList declarations =
		new ASTNode.NodeList(DECLARATIONS_PROPERTY);

	/**
	 * Creates a new unparented compilation unit node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	CompilationUnit(AST ast) {
		super(ast);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final List internalStructuralPropertiesForType(int apiLevel) {
		return propertyDescriptors(apiLevel);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final ASTNode internalGetSetChildProperty(ChildPropertyDescriptor property, boolean get, ASTNode child) {
		if (property == MODULE_DECLARATION_PROPERTY) {
			if (get) {
				return getModuleDeclaration();
			} else {
				setModuleDeclaration((ModuleDeclaration) child);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetChildProperty(property, get, child);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final List internalGetChildListProperty(ChildListPropertyDescriptor property) {
		if (property == DECLARATIONS_PROPERTY) {
			return declarations();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return COMPILATION_UNIT;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		CompilationUnit result = new CompilationUnit(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
	result.setModuleDeclaration((ModuleDeclaration) ASTNode.copySubtree(target, getModuleDeclaration()));
		result.declarations.addAll(ASTNode.copySubtrees(target, declarations()));
		return result;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final boolean subtreeMatch0(ASTMatcher matcher, Object other) {
		// dispatch to correct overloaded match method
		return matcher.match(this, other);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	void accept0(ASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if (visitChildren) {
			// visit children in normal left to right reading order
			acceptChild(visitor, getModuleDeclaration());
			acceptChildren(visitor, declarations());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the module declaration of this compilation unit.
	 * 
	 * @return the module declaration
	 */ 
	public ModuleDeclaration getModuleDeclaration() {
		return this.moduleDeclaration;
	}

	/**
	 * Sets the module declaration of this compilation unit.
	 * 
	 * @param moduleDeclaration the module declaration
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setModuleDeclaration(ModuleDeclaration moduleDeclaration) {
		ASTNode oldChild = this.moduleDeclaration;
		preReplaceChild(oldChild, moduleDeclaration, MODULE_DECLARATION_PROPERTY);
		this.moduleDeclaration = moduleDeclaration;
		postReplaceChild(oldChild, moduleDeclaration, MODULE_DECLARATION_PROPERTY);
	}

	/**
	 * Returns the live ordered list of declarations for this
	 * compilation unit.
	 * 
	 * @return the live list of compilation unit
	 *    (element type: <code>Declaration</code>)
	 */ 
	public List<Declaration> declarations() {
		return this.declarations;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 2 * 4;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.moduleDeclaration == null ? 0 : getModuleDeclaration().treeSize())
			+ (this.declarations.listSize())
	;
	}
	
	public List<IProblem> problems;
	
	public IProblem[] getProblems() {
		if (problems == null) return new IProblem[0];
		return problems.toArray(new IProblem[0]);
	}

}
