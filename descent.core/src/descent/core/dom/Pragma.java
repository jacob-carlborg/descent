package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

/**
 * Pragma AST node. Pragmas are parts of the source code that begin with 
 * the <b>#</b> character and last until the end of the line.
 * 
 * <pre>
 * Pragma:
 *    <b>#</b> <i>text</i>
 * </pre>
 */
public class Pragma extends ASTNode {
	
	/**
	 * The "text" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor TEXT_PROPERTY =
		new SimplePropertyDescriptor(Pragma.class, "text", String.class, OPTIONAL); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(1);
		createPropertyList(Pragma.class, properyList);
		addProperty(TEXT_PROPERTY, properyList);
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
	 * The text.
	 */
	private String text;


	/**
	 * Creates a new unparented pragma node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	Pragma(AST ast) {
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
		if (property == TEXT_PROPERTY) {
			if (get) {
				return getText();
			} else {
				setText((String) value);
				return null;
			}
		}
		// allow default implementation to flag the error
		return super.internalGetSetObjectProperty(property, get, value);
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final int getNodeType0() {
		return PRAGMA;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		Pragma result = new Pragma(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setText(getText());
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
		}
		visitor.endVisit(this);
	}

	/**
	 * Returns the text of this pragma.
	 * 
	 * @return the text
	 */ 
	public String getText() {
		return this.text;
	}

	/**
	 * Sets the text of this pragma.
	 * 
	 * @param text the text
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setText(String text) {
		if (text == null) {
			throw new IllegalArgumentException();
		}
		preValueChange(TEXT_PROPERTY);
		this.text = text;
		postValueChange(TEXT_PROPERTY);
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
	;
	}

}
