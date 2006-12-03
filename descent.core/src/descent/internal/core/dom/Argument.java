package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IArgument;
import descent.core.dom.IExpression;
import descent.core.dom.ISimpleName;
import descent.core.dom.IType;

/**
 * Argument AST node type. An argument is the one passed to a function
 * or method.
 *
 * <pre>
 * Argument:
 *    [ | <b>in</b> | <b>out</b> | <b>inout</b> | <b>lazy</b> ] Type SimpleName [ <b>=</b> Expression ]
 * </pre>
 */
public class Argument extends ASTNode implements IArgument {
	
	/**
	 * The "passage mode" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor PASSAGE_MODE_PROPERTY = 
		new SimplePropertyDescriptor(Argument.class, "passageMode", int.class, MANDATORY); //$NON-NLS-1$
	
	/**
	 * The "type" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor TYPE_PROPERTY = 
		new ChildPropertyDescriptor(Argument.class, "type", Type.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$
	
	/**
	 * The "name" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor NAME_PROPERTY = 
		new ChildPropertyDescriptor(Argument.class, "name", SimpleName.class, MANDATORY, NO_CYCLE_RISK); //$NON-NLS-1$
	
	/**
	 * The "defaultValue" structural property of this node type.
	 */
	public static final ChildPropertyDescriptor DEFAULT_VALUE_PROPERTY = 
		new ChildPropertyDescriptor(Argument.class, "defaultValue", Expression.class, OPTIONAL, NO_CYCLE_RISK); //$NON-NLS-1$
	
	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;
	
	static {
		List propertyList = new ArrayList(3);
		createPropertyList(Argument.class, propertyList);
		addProperty(PASSAGE_MODE_PROPERTY, propertyList);
		addProperty(TYPE_PROPERTY, propertyList);
		addProperty(NAME_PROPERTY, propertyList);
		addProperty(DEFAULT_VALUE_PROPERTY, propertyList);
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
	
	// TODO comment better
	public PassageMode passageMode = PassageMode.IN;
	public Type type;
	public Identifier name;	
	public Expression defaultValue;
	
	/**
	 * Creates a new unparented argument node owned by the given 
	 * AST. By default, TODO.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	Argument(AST ast) {
		super(ast);
	}

	public Argument(PassageMode passageMode, Type type, Identifier name, Expression defaultValue) {
		this.name = name;
		this.type = type;
		this.passageMode = passageMode;
		this.defaultValue = defaultValue;
	}
	
	public ISimpleName getName() {
		return name;
	}
	
	public IType getType() {
		return type;
	}
	
	public IExpression getDefaultValue() {
		return defaultValue;
	}
	
	public int getKind() {
		switch(passageMode) {
		case IN: return IArgument.IN;
		case OUT: return IArgument.OUT;
		case INOUT: return IArgument.INOUT;
		default /* case Lazy */: return IArgument.LAZY;
		} 
	}

	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, type);
			acceptChild(visitor, name);
			acceptChild(visitor, defaultValue);
		}
		visitor.endVisit(this);
	}

	public int getElementType() {
		return ARGUMENT;
	}

}
