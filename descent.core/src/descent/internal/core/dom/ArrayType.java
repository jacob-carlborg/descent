package descent.internal.core.dom;

import descent.core.dom.IArrayType;

/**
 * Abstract subclass for array types.
 * <pre>
 * ArrayType:
 *    AssociativeArrayType
 *    DynamicArrayType
 *    StaticArrayType
 * AssociativeArrayType:
 *    Type <b>[</b> Type <b>]</b>
 * DynamicArrayType:
 *    Type <b>[ ]</b>
 * StaticArrayType:
 *    Type <b>[</b> Expression <b>]</b>
 * </pre>
 */
public abstract class ArrayType extends DmdType implements IArrayType {
	
	/**
	 * The componentType.
	 */
	Type componentType;
	
	/**
	 * Returns structural property descriptor for the "componentType" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	abstract ChildPropertyDescriptor internalComponentTypeProperty();
	
	/**
	 * Returns structural property descriptor for the "componentType" property
	 * of this node.
	 * 
	 * @return the property descriptor
	 */
	public final ChildPropertyDescriptor getComponentTypeProperty() {
		return internalComponentTypeProperty();
	}
	
	/**
	 * Creates and returns a structural property descriptor for the
	 * "name" property declared on the given concrete node type.
	 * 
	 * @return the property descriptor
	 */
	static final ChildPropertyDescriptor internalComponentTypePropertyFactory(Class nodeClass) {
		return new ChildPropertyDescriptor(nodeClass, "componentType", Name.class, MANDATORY, CYCLE_RISK); //$NON-NLS-1$
	}
	
	// TODO descent remove
	public ArrayType(TY ty, DmdType next) {
		super(ty, next);
	}
	
	/**
	 * Creates a new AST node for an abstract array type.
	 * <p>
	 * N.B. This constructor is package-private; all subclasses must be 
	 * declared in the same package; clients are unable to declare 
	 * additional subclasses.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	public ArrayType(AST ast) {
		super(ast);
	}
	
	/**
	 * Returns the component type of this dynamic array type.
	 * 
	 * @return the component type
	 */ 
	public Type getComponentType() {
		return this.componentType;
	}

	/**
	 * Sets the component type of this dynamic array type.
	 * 
	 * @param componentType the component type
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	public void setComponentType(Type componentType) {
		ASTNode oldChild = this.componentType;
		
		ChildPropertyDescriptor p = internalComponentTypeProperty();
		
		preReplaceChild(oldChild, componentType, p);
		this.componentType = componentType;
		postReplaceChild(oldChild, componentType, p);
		
		// TODO descent remove
		this.next = (DmdType) componentType;
	}

}
