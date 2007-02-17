package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * Constructor declaration AST node. A constructor declaration is the union of 
 * constructors, destructors, static constructors, static destructors, allocators (new)
 * and deallocators (delete).
 * 
 * <pre>
 * ConstructorDeclaration:
 *    Kind
 *       <b>(</b> [ Argument { <b>,</b> Argument } ] [ <b>...</b> ] <b>)</b>
 *       [ <b>in</b> Block ]
 *       [ <b>out</b> [ <b>(</b> SimpleName <b>)</b> ] Block ]
 *       [ <b>body</b> ] Block
 *       
 * Kind:
 *    <b>this</b> | <b>~this</b> | <b>static this</b> | <b>static ~this</b> | <b>new</b> | <b>delete</b> 
 * </pre>
 */
public class ConstructorDeclaration extends AbstractFunctionDeclaration {
	
	/**
	 * Kinds of "constructors".
	 */
	public enum Kind {
		/** Constructor kind */
		CONSTRUCTOR,
		/** Destructor kind */
		DESTRUCTOR,
		/** Static constructor kind */
		STATIC_CONSTRUCTOR,
		/** Static destructor kind */
		STATIC_DESTRUCTOR,
		/** New (allocator) kind */
		NEW,
		/** Delete (deallocator) kind */
		DELETE
	}
	
	/**
	 * The "preDDocs" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor PRE_D_DOCS_PROPERTY =
	internalPreDDocsPropertyFactory(ConstructorDeclaration.class); //$NON-NLS-1$

	/**
	 * The "modifiers" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor MODIFIERS_PROPERTY =
	internalModifiersPropertyFactory(ConstructorDeclaration.class); //$NON-NLS-1$

	/**
	 * The "kind" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor KIND_PROPERTY =
		new SimplePropertyDescriptor(ConstructorDeclaration.class, "kind", Kind.class, MANDATORY); //$NON-NLS-1$

	/**
	 * The "arguments" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor ARGUMENTS_PROPERTY =
	internalArgumentsPropertyFactory(ConstructorDeclaration.class); //$NON-NLS-1$

	/**
	 * The "variadic" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor VARIADIC_PROPERTY =
	internalVariadicPropertyFactory(ConstructorDeclaration.class); //$NON-NLS-1$

	/**
	 * The "precondition" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor PRECONDITION_PROPERTY =
	internalPreconditionPropertyFactory(ConstructorDeclaration.class); //$NON-NLS-1$

	/**
	 * The "postcondition" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor POSTCONDITION_PROPERTY =
	internalPostconditionPropertyFactory(ConstructorDeclaration.class); //$NON-NLS-1$

	/**
	 * The "postconditionVariableName" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor POSTCONDITION_VARIABLE_NAME_PROPERTY =
	internalPostconditionVariableNamePropertyFactory(ConstructorDeclaration.class); //$NON-NLS-1$

	/**
	 * The "body" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor BODY_PROPERTY =
	internalBodyPropertyFactory(ConstructorDeclaration.class); //$NON-NLS-1$

	/**
	 * The "postDDoc" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor POST_D_DOC_PROPERTY =
	internalPostDDocPropertyFactory(ConstructorDeclaration.class); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(10);
		createPropertyList(ConstructorDeclaration.class, properyList);
		addProperty(PRE_D_DOCS_PROPERTY, properyList);
		addProperty(MODIFIERS_PROPERTY, properyList);
		addProperty(KIND_PROPERTY, properyList);
		addProperty(ARGUMENTS_PROPERTY, properyList);
		addProperty(VARIADIC_PROPERTY, properyList);
		addProperty(PRECONDITION_PROPERTY, properyList);
		addProperty(POSTCONDITION_PROPERTY, properyList);
		addProperty(POSTCONDITION_VARIABLE_NAME_PROPERTY, properyList);
		addProperty(BODY_PROPERTY, properyList);
		addProperty(POST_D_DOC_PROPERTY, properyList);
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
	private Kind kind = Kind.STATIC_CONSTRUCTOR;


	/**
	 * Creates a new unparented constructor declaration node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	ConstructorDeclaration(AST ast) {
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
				setKind((Kind) value);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetObjectProperty(property, get, value);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final boolean internalGetSetBooleanProperty(SimplePropertyDescriptor property, boolean get, boolean value) {
		if (property == VARIADIC_PROPERTY) {
			if (get) {
				return isVariadic();
			} else {
				setVariadic(value);
				return false;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetBooleanProperty(property, get, value);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final ASTNode internalGetSetChildProperty(ChildPropertyDescriptor property, boolean get, ASTNode child) {
		if (property == PRECONDITION_PROPERTY) {
			if (get) {
				return getPrecondition();
			} else {
				setPrecondition((Statement) child);
				return null;
			}
		}
		if (property == POSTCONDITION_PROPERTY) {
			if (get) {
				return getPostcondition();
			} else {
				setPostcondition((Statement) child);
				return null;
			}
		}
		if (property == POSTCONDITION_VARIABLE_NAME_PROPERTY) {
			if (get) {
				return getPostconditionVariableName();
			} else {
				setPostconditionVariableName((SimpleName) child);
				return null;
			}
		}
		if (property == BODY_PROPERTY) {
			if (get) {
				return getBody();
			} else {
				setBody((Statement) child);
				return null;
			}
		}
		if (property == POST_D_DOC_PROPERTY) {
			if (get) {
				return getPostDDoc();
			} else {
				setPostDDoc((DDocComment) child);
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
		if (property == PRE_D_DOCS_PROPERTY) {
			return preDDocs();
		}
		if (property == MODIFIERS_PROPERTY) {
			return modifiers();
		}
		if (property == ARGUMENTS_PROPERTY) {
			return arguments();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

		@Override
		final ChildListPropertyDescriptor internalPreDDocsProperty() {
			return PRE_D_DOCS_PROPERTY;
		}
		
		@Override
		final ChildListPropertyDescriptor internalModifiersProperty() {
			return MODIFIERS_PROPERTY;
		}
		
		@Override
		final ChildListPropertyDescriptor internalArgumentsProperty() {
			return ARGUMENTS_PROPERTY;
		}
		
		@Override
		final SimplePropertyDescriptor internalVariadicProperty() {
			return VARIADIC_PROPERTY;
		}
		
		@Override
		final ChildPropertyDescriptor internalPreconditionProperty() {
			return PRECONDITION_PROPERTY;
		}
		
		@Override
		final ChildPropertyDescriptor internalPostconditionProperty() {
			return POSTCONDITION_PROPERTY;
		}
		
		@Override
		final ChildPropertyDescriptor internalPostconditionVariableNameProperty() {
			return POSTCONDITION_VARIABLE_NAME_PROPERTY;
		}
		
		@Override
		final ChildPropertyDescriptor internalBodyProperty() {
			return BODY_PROPERTY;
		}
		
		@Override
		final ChildPropertyDescriptor internalPostDDocProperty() {
			return POST_D_DOC_PROPERTY;
		}
		
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return CONSTRUCTOR_DECLARATION;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		ConstructorDeclaration result = new ConstructorDeclaration(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.preDDocs.addAll(ASTNode.copySubtrees(target, preDDocs()));
		result.modifiers.addAll(ASTNode.copySubtrees(target, modifiers()));
		result.setKind(getKind());
		result.arguments.addAll(ASTNode.copySubtrees(target, arguments()));
		result.setVariadic(isVariadic());
	result.setPrecondition((Statement) ASTNode.copySubtree(target, getPrecondition()));
	result.setPostcondition((Statement) ASTNode.copySubtree(target, getPostcondition()));
	result.setPostconditionVariableName((SimpleName) ASTNode.copySubtree(target, getPostconditionVariableName()));
		result.setBody((Statement) getBody().clone(target));
	result.setPostDDoc((DDocComment) ASTNode.copySubtree(target, getPostDDoc()));
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
			acceptChildren(visitor, this.preDDocs);
			acceptChildren(visitor, this.modifiers);
			acceptChildren(visitor, this.arguments);
			acceptChild(visitor, getPrecondition());
			acceptChild(visitor, getPostcondition());
			acceptChild(visitor, getPostconditionVariableName());
			acceptChild(visitor, getBody());
			acceptChild(visitor, getPostDDoc());
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the kind of this constructor declaration.
	 * 
	 * @return the kind
	 */ 
	public Kind getKind() {
		return this.kind;
	}

	/**
	 * Sets the kind of this constructor declaration.
	 * 
	 * @param kind the kind
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setKind(Kind kind) {
		if (kind == null) {
			throw new IllegalArgumentException();
		}
		preValueChange(KIND_PROPERTY);
		this.kind = kind;
		postValueChange(KIND_PROPERTY);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 10 * 4;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.preDDocs.listSize())
			+ (this.modifiers.listSize())
			+ (this.arguments.listSize())
			+ (this.precondition == null ? 0 : getPrecondition().treeSize())
			+ (this.postcondition == null ? 0 : getPostcondition().treeSize())
			+ (this.postconditionVariableName == null ? 0 : getPostconditionVariableName().treeSize())
			+ (this.body == null ? 0 : getBody().treeSize())
			+ (this.postDDoc == null ? 0 : getPostDDoc().treeSize())
	;
	}

}
