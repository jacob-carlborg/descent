package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.ISuperExpression;

/**
 * Super literal node.
 */
public class SuperLiteral extends Expression implements ISuperExpression {
	
	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;
	
	static {
		List propertyList = new ArrayList(1);
		createPropertyList(NullLiteral.class, propertyList);
		PROPERTY_DESCRIPTORS = reapPropertyList(propertyList);
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
	 * Creates a new unparented super literal node owned by the given AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	SuperLiteral(AST ast) {
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
	 * TODO make it package
	 */
	public final int getNodeType0() {
		return SUPER_LITERAL;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		NullLiteral result = new NullLiteral(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
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
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		return BASE_NODE_SIZE;
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return memSize();
	}

	// TODO Descent remove
	public SuperLiteral() {
	}
	
	@Override
	public String toString() {
		return "super";
	}

}
