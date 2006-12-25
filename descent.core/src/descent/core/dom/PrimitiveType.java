package descent.core.dom;

import java.util.ArrayList;
import java.util.List;


/**
 * Primitive type nodes.
 * <pre>
 * PrimitiveType:
 *    <b>void</b>
 *    <b>byte</b>
 *    <b>ubyte</b>
 *    <b>short</b>
 *    <b>ushort</b>
 *    <b>int</b>
 *    <b>uint</b>
 *    <b>long</b>
 *    <b>ulong</b>
 *    <b>float</b>
 *    <b>double</b>
 *    <b>real</b>
 *    <b>ifloat</b>
 *    <b>idouble</b>
 *    <b>ireal</b>
 *    <b>complex32</b> TODO
 *    <b>complex64</b> TODO
 *    <b>complex80</b> TODO
 *    <b>bit</b>
 *    <b>bool</b>
 *    <b>char</b>
 *    <b>wchar</b>
 *    <b>dchar</b>
 * </pre>
 * 
 * <p>
 * Note that due to the fact that AST nodes belong to a specific AST and
 * have a specific parent, there needs to multiple instances of these
 * nodes.
 * </p>
 */
public class PrimitiveType extends Type {
	
	/**
 	 * Primitive type codes.
	 * </pre>
	 */
	public static enum Code {
		VOID("void"),
		BYTE("byte"),
		UBYTE("ubyte"),
		SHORT("short"),
		USHORT("ushort"),
		INT("int"),
		UINT("uint"),
		LONG("long"),
		ULONG("ulong"),
		FLOAT("float"),
		DOUBLE("double"),
		REAL("real"),
		IFLOAT("ifloat"),
		IDOUBLE("idouble"),
		IREAL("ireal"),
		COMPLEX32("complex32"), // TODO
		COMPLEX64("complex64"), // TODO
		COMPLEX80("complex80"), // TODO
		BIT("bit"),
		BOOL("bool"),
		CHAR("char"),
		WCHAR("wchar"),
		DCHAR("dchar"),
		;
		
		/**
		 * The token for the code.
		 */
		private String token;
		
		/**
		 * Creates a new code with the given token.
		 * <p>
		 * Note: this constructor is private. The only instances
		 * ever created are the ones for the codes.
		 * </p>
		 * 
		 * @param token the character sequence for the operator
		 */
		private Code(String token) {
			this.token = token;
		}
		
		/**
		 * Returns the character sequence for the code.
		 * 
		 * @return the character sequence for the code
		 */
		public String toString() {
			return token;
		}
	}
	
	/**
	 * The "primitiveTypeCode" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor PRIMITIVETYPECODE_PROPERTY =
		new SimplePropertyDescriptor(PrimitiveType.class, "primitiveTypeCode", Code.class, OPTIONAL); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(1);
		createPropertyList(PrimitiveType.class, properyList);
		addProperty(PRIMITIVETYPECODE_PROPERTY, properyList);
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
	 * The primitiveTypeCode. By default, void.
	 */
	private Code primitiveTypeCode = Code.VOID;


	/**
	 * Creates a new unparented primitive type node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	PrimitiveType(AST ast) {
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
		if (property == PRIMITIVETYPECODE_PROPERTY) {
			if (get) {
				return getPrimitiveTypeCode();
			} else {
				setPrimitiveTypeCode((Code) value);
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
		return PRIMITIVE_TYPE;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		PrimitiveType result = new PrimitiveType(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setPrimitiveTypeCode(getPrimitiveTypeCode());
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
	 * Returns the primitiveTypeCode of this primitive type.
	 * 
	 * @return the primitiveTypeCode
	 */ 
	public Code getPrimitiveTypeCode() {
		return this.primitiveTypeCode;
	}

	/**
	 * Sets the primitiveTypeCode of this primitive type.
	 * 
	 * @param primitiveTypeCode the primitiveTypeCode
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setPrimitiveTypeCode(Code primitiveTypeCode) {
		if (primitiveTypeCode == null) {
			throw new IllegalArgumentException();
		}
		preValueChange(PRIMITIVETYPECODE_PROPERTY);
		this.primitiveTypeCode = primitiveTypeCode;
		postValueChange(PRIMITIVETYPECODE_PROPERTY);
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
	
	@Override
	public String toString() {
		return primitiveTypeCode.toString();
	}

}
