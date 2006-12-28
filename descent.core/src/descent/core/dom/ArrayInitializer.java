package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * Array initializer AST node.
 * 
 * <pre>
 * ArrayInitializer:
 *    <b>[</b> [ ArrayInitializerFragment { <b>,</b> ArrayInitializerFragment } ] <b>]</b> 
 * </pre>
 */
public class ArrayInitializer extends Initializer {
	
	/**
	 * The "fragments" structural property of this node type.
	 */
	public static final ChildListPropertyDescriptor FRAGMENTS_PROPERTY =
		new ChildListPropertyDescriptor(ArrayInitializer.class, "fragments", ArrayInitializerFragment.class, CYCLE_RISK); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(1);
		createPropertyList(ArrayInitializer.class, properyList);
		addProperty(FRAGMENTS_PROPERTY, properyList);
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
	 * The fragments
	 * (element type: <code>ArrayInitializerFragment</code>).
	 * Defaults to an empty list.
	 */
	private ASTNode.NodeList fragments =
		new ASTNode.NodeList(FRAGMENTS_PROPERTY);

	/**
	 * Creates a new unparented array initializer node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	ArrayInitializer(AST ast) {
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
	final List internalGetChildListProperty(ChildListPropertyDescriptor property) {
		if (property == FRAGMENTS_PROPERTY) {
			return fragments();
		}
		// allow default implementation to flag the error
		return super.internalGetChildListProperty(property);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return ARRAY_INITIALIZER;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		ArrayInitializer result = new ArrayInitializer(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.fragments.addAll(ASTNode.copySubtrees(target, fragments()));
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
			acceptChildren(visitor, fragments);
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the live ordered list of fragments for this
	 * array initializer.
	 * 
	 * @return the live list of array initializer
	 *    (element type: <code>ArrayInitializerFragment</code>)
	 */ 
	public List<ArrayInitializerFragment> fragments() {
		return this.fragments;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE + 1 * 4;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return
			memSize()
			+ (this.fragments.listSize())
	;
	}

}
