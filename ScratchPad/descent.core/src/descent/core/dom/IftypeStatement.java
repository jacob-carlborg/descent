package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.compiler.IftypeDeclarationKind;


/**
 * The <i>deprecated</i> iftype statement AST node type.
 *
 * <pre>
 * IftypeStatement:
 *    <b>iftype</b> <b>(</b> Type [ SimpleName ] [ [ <b>:</b> | <b>==</b> ] Type ] <b>)</b> Statement [ <b>else</b> Statement ]
 * </pre>
 */
public class IftypeStatement extends ConditionalStatement {
	
	/**
	 * The "kind" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor KIND_PROPERTY =
		new SimplePropertyDescriptor(IftypeStatement.class, "kind", IftypeDeclarationKind.class, OPTIONAL); //$NON-NLS-1$

	/**
	 * The "name" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor NAME_PROPERTY =
		new ChildPropertyDescriptor(IftypeStatement.class, "name", SimpleName.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "testType" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor TEST_TYPE_PROPERTY =
		new ChildPropertyDescriptor(IftypeStatement.class, "testType", Type.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "matchingType" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor MATCHING_TYPE_PROPERTY =
		new ChildPropertyDescriptor(IftypeStatement.class, "matchingType", Type.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$

	/**
	 * The "thenBody" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor THEN_BODY_PROPERTY =
		internalThenBodyPropertyFactory(IftypeStatement.class); //$NON-NLS-1$

	/**
	 * The "elseBody" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor ELSE_BODY_PROPERTY =
		internalThenBodyPropertyFactory(IftypeStatement.class); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(6);
		createPropertyList(IftypeStatement.class, properyList);
		addProperty(KIND_PROPERTY, properyList);
		addProperty(NAME_PROPERTY, properyList);
		addProperty(TEST_TYPE_PROPERTY, properyList);
		addProperty(MATCHING_TYPE_PROPERTY, properyList);
		addProperty(THEN_BODY_PROPERTY, properyList);
		addProperty(ELSE_BODY_PROPERTY, properyList);
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
	 * The kind.
	 */
	private IftypeDeclarationKind kind;

	/**
	 * The name.
	 */
	private SimpleName name;

	/**
	 * The testType.
	 */
	private Type testType;

	/**
	 * The matchingType.
	 */
	private Type matchingType;


	/**
	 * Creates a new unparented iftype statement node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	IftypeStatement(AST ast) {
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
	final Object internalGetSetObjectProperty(SimplePropertyDescriptor property, boolean get, Object value) {
		if (property == KIND_PROPERTY) {
			if (get) {
				return getKind();
			} else {
				setKind((IftypeDeclarationKind) value);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetObjectProperty(property, get, value);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final ASTNode internalGetSetChildProperty(ChildPropertyDescriptor property, boolean get, ASTNode child) {
		if (property == NAME_PROPERTY) {
			if (get) {
				return getName();
			} else {
				setName((SimpleName) child);
				return null;
			}
		}
		if (property == TEST_TYPE_PROPERTY) {
			if (get) {
				return getTestType();
			} else {
				setTestType((Type) child);
				return null;
			}
		}
		if (property == MATCHING_TYPE_PROPERTY) {
			if (get) {
				return getMatchingType();
			} else {
				setMatchingType((Type) child);
				return null;
			}
		}
		if (property == THEN_BODY_PROPERTY) {
			if (get) {
				return getThenBody();
			} else {
				setThenBody((Statement) child);
				return null;
			}
		}
		if (property == ELSE_BODY_PROPERTY) {
			if (get) {
				return getElseBody();
			} else {
				setElseBody((Statement) child);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetChildProperty(property, get, child);
	}
	
	@Override
	final ChildPropertyDescriptor internalThenBodyProperty() {
		return THEN_BODY_PROPERTY;
	}
	
	@Override
	final ChildPropertyDescriptor internalElseBodyProperty() {
		return ELSE_BODY_PROPERTY;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return IFTYPE_STATEMENT;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		IftypeStatement result = new IftypeStatement(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setKind(getKind());
	result.setName((SimpleName) ASTNode.copySubtree(target, getName()));
	result.setTestType((Type) ASTNode.copySubtree(target, getTestType()));
	result.setMatchingType((Type) ASTNode.copySubtree(target, getMatchingType()));
		result.setThenBody((Statement) getThenBody().clone(target));
	result.setElseBody((Statement) ASTNode.copySubtree(target, getElseBody()));
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
			acceptChild(visitor, getName());
			acceptChild(visitor, getTestType());
			acceptChild(visitor, getMatchingType());
			acceptChild(visitor, getThenBody());
			acceptChild(visitor, getElseBody());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the kind of this iftype statement.
	 * 
	 * @return the kind
	 */ 
	public IftypeDeclarationKind getKind() {
		return this.kind;
	}

	/**
	 * Sets the kind of this iftype statement.
	 * 
	 * @param kind the kind
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setKind(IftypeDeclarationKind kind) {
		if (kind == null) {
			throw new IllegalArgumentException();
		}
		preValueChange(KIND_PROPERTY);
		this.kind = kind;
		postValueChange(KIND_PROPERTY);
	}

	/**
	 * Returns the name of this iftype statement.
	 * 
	 * @return the name
	 */ 
	public SimpleName getName() {
		return this.name;
	}

	/**
	 * Sets the name of this iftype statement.
	 * 
	 * @param name the name
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setName(SimpleName name) {
		ASTNode oldChild = this.name;
		preReplaceChild(oldChild, name, NAME_PROPERTY);
		this.name = name;
		postReplaceChild(oldChild, name, NAME_PROPERTY);
	}

	/**
	 * Returns the test type of this iftype statement.
	 * 
	 * @return the test type
	 */ 
	public Type getTestType() {
		return this.testType;
	}

	/**
	 * Sets the test type of this iftype statement.
	 * 
	 * @param testType the test type
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setTestType(Type testType) {
		ASTNode oldChild = this.testType;
		preReplaceChild(oldChild, testType, TEST_TYPE_PROPERTY);
		this.testType = testType;
		postReplaceChild(oldChild, testType, TEST_TYPE_PROPERTY);
	}

	/**
	 * Returns the matching type of this iftype statement.
	 * 
	 * @return the matching type
	 */ 
	public Type getMatchingType() {
		return this.matchingType;
	}

	/**
	 * Sets the matching type of this iftype statement.
	 * 
	 * @param matchingType the matching type
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setMatchingType(Type matchingType) {
		ASTNode oldChild = this.matchingType;
		preReplaceChild(oldChild, matchingType, MATCHING_TYPE_PROPERTY);
		this.matchingType = matchingType;
		postReplaceChild(oldChild, matchingType, MATCHING_TYPE_PROPERTY);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 6 * 4;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.name == null ? 0 : getName().treeSize())
			+ (this.testType == null ? 0 : getTestType().treeSize())
			+ (this.matchingType == null ? 0 : getMatchingType().treeSize())
			+ (this.thenBody == null ? 0 : getThenBody().treeSize())
			+ (this.elseBody == null ? 0 : getElseBody().treeSize())
	;
	}

}
