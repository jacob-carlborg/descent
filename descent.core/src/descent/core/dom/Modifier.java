package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.Flags;


/**
 * Modifier node.
 * <pre>
 * Modifier:
 *    <b>private</b>
 *    <b>package</b>
 *    <b>protected</b>
 *    <b>public</b>
 *    <b>export</b>
 *    <b>static</b>
 *    <b>final</b>
 *    <b>abstract</b>
 *    <b>override</b>
 *    <b>auto</b>
 *    <b>synchronized</b>
 *    <b>deprecated</b>
 *    <b>extern</b>
 *    <b>const</b>
 *    <b>scope</b>
 *    <b>invariant</b>
 *    <b>in</b>
 *    <b>out</b>
 *    <b>inout</b>
 *    <b>lazy</b>
 *    <b>ref</b>
 *    <b>enum</b>
 *    <b>pure</b>
 *    <b>nothrow</b>
 * </pre>
 */
public class Modifier extends ASTNode {
	
	/**
 	 * Modifier keywords.
	 */
	public static enum ModifierKeyword {
		
		PRIVATE_KEYWORD("private", PRIVATE),
		PACKAGE_KEYWORD("package", PACKAGE),
		PROTECTED_KEYWORD("protected", PROTECTED),
		PUBLIC_KEYWORD("public", PUBLIC),
		EXPORT_KEYWORD("export", EXPORT),
		STATIC_KEYWORD("static", STATIC),
		FINAL_KEYWORD("final", FINAL),
		ABSTRACT_KEYWORD("abstract", ABSTRACT),
		OVERRIDE_KEYWORD("override", OVERRIDE),
		AUTO_KEYWORD("auto", AUTO),
		SYNCHRONIZED_KEYWORD("synchronized", SYNCHRONIZED),
		DEPRECATED_KEYWORD("deprecated", DEPRECATED),
		EXTERN_KEYWORD("extern", EXTERN),
		CONST_KEYWORD("const", CONST),
		SCOPE_KEYWORD("scope", SCOPE),
		INVARIANT_KEYWORD("invariant", INVARIANT),
		IN_KEYWORD("in", IN),
		OUT_KEYWORD("out", OUT),
		INOUT_KEYWORD("inout", INOUT),
		LAZY_KEYWORD("lazy", LAZY),
		REF_KEYWORD("ref", REF),
		ENUM_KEYWORD("enum", ENUM),
		PURE_KEYWORD("pure", PURE),
		NOTHROW_KEYWORD("nothrow", NOTHROW),
		;
		
		private String keyword;
		private int flagValue;
		
		private ModifierKeyword(String keyword, int flagValue) {
			this.keyword = keyword;
			this.flagValue = flagValue;
		}
		
		/**
		 * Returns the modifier corresponding to the given single-bit flag value,
		 * or <code>null</code> if none or if more than one bit is set.
		 * <p>
		 * <code>fromFlagValue</code> is the converse of <code>toFlagValue</code>:
		 * that is, <code>ModifierKeyword.fromFlagValue(k.toFlagValue()) == k</code> for 
		 * all modifier keywords <code>k</code>.
		 * </p>
		 * 
		 * @param flagValue the single-bit flag value for the modifier
		 * @return the modifier keyword, or <code>null</code> if none
		 * @see #toFlagValue()
		 */
		public static ModifierKeyword fromFlagValue(int flagValue) {
			for(ModifierKeyword keyword : ModifierKeyword.values()) {
				if (keyword.toFlagValue() == flagValue) {
					return keyword;
				}
			}
			return null;
		}
		
		/**
		 * Returns the modifier corresponding to the given string,
		 * or <code>null</code> if none.
		 * <p>
		 * <code>toKeyword</code> is the converse of <code>toString</code>:
		 * that is, <code>ModifierKeyword.toKeyword(k.toString()) == k</code> for 
		 * all modifier keywords <code>k</code>.
		 * </p>
		 * 
		 * @param keyword the lowercase string name for the modifier
		 * @return the modifier keyword, or <code>null</code> if none
		 * @see #toString()
		 */
		public static ModifierKeyword toKeyword(String keyword) {
			for(ModifierKeyword mKeyword : ModifierKeyword.values()) {
				if (mKeyword.toString().equals(keyword)) {
					return mKeyword;
				}
			}
			return null;
		}
		
		/**
		 * Returns the modifier flag value corresponding to this modifier keyword.
		 * 
		 * @return one of the <code>Modifier</code> constants
		 * @see #fromFlagValue(int)
		 */ 
		public int toFlagValue() {
			return this.flagValue;
		}

		/**
		 * Returns the keyword for the modifier.
		 * 
		 * @return the keyword for the modifier
		 * @see #toKeyword(String)
		 */
		public String toString() {
			return this.keyword;
		}
		
	}
	
	/**
	 * Modifier constant (bit mask, value 0) indicating no modifiers.
	 * @since 2.0
	 */
	public static final int NONE = Flags.AccDefault;
	
	/**
	 * "private" modifier constant (bit mask).
	 */
	public static final int PRIVATE = Flags.AccPrivate;
	
	/**
	 * "package" modifier constant (bit mask).
	 */
	public static final int PACKAGE = Flags.AccPackage;
	
	/**
	 * "protected" modifier constant (bit mask).
	 */
	public static final int PROTECTED = Flags.AccProtected;
	
	/**
	 * "public" modifier constant (bit mask).
	 */
	public static final int PUBLIC = Flags.AccPublic;
	
	/**
	 * "export" modifier constant (bit mask).
	 */
	public static final int EXPORT = Flags.AccExport;
	
	/**
	 * "static" modifier constant (bit mask).
	 */
	public static final int STATIC = Flags.AccStatic;
	
	/**
	 * "final" modifier constant (bit mask).
	 */
	public static final int FINAL = Flags.AccFinal;
	
	/**
	 * "abstract" modifier constant (bit mask).
	 */
	public static final int ABSTRACT = Flags.AccAbstract;
	
	/**
	 * "override" modifier constant (bit mask).
	 */
	public static final int OVERRIDE = Flags.AccOverride;
	
	/**
	 * "auto" modifier constant (bit mask).
	 */
	public static final int AUTO = Flags.AccAuto;
	
	/**
	 * "synchronized" modifier constant (bit mask).
	 */
	public static final int SYNCHRONIZED = Flags.AccSynchronized;
	
	/**
	 * "deprecated" modifier constant (bit mask).
	 */
	public static final int DEPRECATED = Flags.AccDeprecated;
	
	/**
	 * "extern" modifier constant (bit mask).
	 */
	public static final int EXTERN = Flags.AccExtern;
	
	/**
	 * "const" modifier constant (bit mask).
	 */
	public static final int CONST = Flags.AccConst;
	
	/**
	 * "scope" modifier constant (bit mask).
	 */
	public static final int SCOPE = Flags.AccScope;
	
	/**
	 * "invariant" modifier constant (bit mask).
	 */
	public static final int INVARIANT = Flags.AccInvariant;
	
	/**
	 * "in" modifier constant (bit mask).
	 */
	public static final int IN = Flags.AccIn;
	
	/**
	 * "out" modifier constant (bit mask).
	 */
	public static final int OUT = Flags.AccOut;
	
	/**
	 * "inout" modifier constant (bit mask).
	 */
	public static final int INOUT = Flags.AccInout;
	
	/**
	 * "lazy" modifier constant (bit mask).
	 */
	public static final int LAZY = Flags.AccLazy;
	
	/**
	 * "ref" modifier constant (bit mask).
	 */
	public static final int REF = Flags.AccRef;
	
	/**
	 * "enum" modifier constant (bit mask).
	 */
	public static final int ENUM = Flags.AccEnum;
	
	/**
	 * "pure" modifier constant (bit mask).
	 */
	public static final int PURE = Flags.AccPure;
	
	/**
	 * "nothrow" modifier constant (bit mask).
	 */
	public static final int NOTHROW = Flags.AccNothrow;
	
	/**
	 * The "modifierKeyword" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor MODIFIER_KEYWORD_PROPERTY =
		new SimplePropertyDescriptor(Modifier.class, "modifierKeyword", ModifierKeyword.class, OPTIONAL); //$NON-NLS-1$

	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 */
	private static final List PROPERTY_DESCRIPTORS;

	static {
		List properyList = new ArrayList(1);
		createPropertyList(Modifier.class, properyList);
		addProperty(MODIFIER_KEYWORD_PROPERTY, properyList);
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
	 * The modifierKeyword.
	 */
	private ModifierKeyword modifierKeyword;


	/**
	 * Creates a new unparented modifier node owned by the given 
	 * AST.
	 * <p>
	 * N.B. This constructor is package-private.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	Modifier(AST ast) {
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
		if (property == MODIFIER_KEYWORD_PROPERTY) {
			if (get) {
				return getModifierKeyword();
			} else {
				setModifierKeyword((ModifierKeyword) value);
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
		return MODIFIER;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		Modifier result = new Modifier(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setModifierKeyword(getModifierKeyword());
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
	 * Returns the modifier keyword of this modifier.
	 * 
	 * @return the modifier keyword
	 */ 
	public ModifierKeyword getModifierKeyword() {
		if (this.modifierKeyword == null) {
			// lazy init must be thread-safe for readers
			synchronized (this) {
				if (this.modifierKeyword == null) {
					this.modifierKeyword = ModifierKeyword.PUBLIC_KEYWORD;
				}
			}
		}
		return this.modifierKeyword;
	}

	/**
	 * Sets the modifier keyword of this modifier.
	 * 
	 * @param modifierKeyword the modifier keyword
	 * @exception IllegalArgumentException if the argument is incorrect
	 */ 
	public void setModifierKeyword(ModifierKeyword modifierKeyword) {
		if (modifierKeyword == null) {
			throw new IllegalArgumentException();
		}
		preValueChange(MODIFIER_KEYWORD_PROPERTY);
		this.modifierKeyword = modifierKeyword;
		postValueChange(MODIFIER_KEYWORD_PROPERTY);
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
	
	/**
	 * Answer true if the receiver is the private modifier, false otherwise.
	 * 
	 * @return true if the receiver is the private modifier, false otherwise
	 */
	public boolean isPrivate() {
		return this.modifierKeyword == ModifierKeyword.PRIVATE_KEYWORD;
	}

	/**
	 * Returns whether the given flags includes the "private" modifier.
	 * 
	 * @param flags the modifier flags
	 * @return <code>true</code> if the <code>PRIVATE</code> bit is
	 *   set, and <code>false</code> otherwise
	 */
	public static boolean isPrivate(long flags) {
		return (flags & PRIVATE) != 0;
	}
	/**
	 * Answer true if the receiver is the package modifier, false otherwise.
	 * 
	 * @return true if the receiver is the package modifier, false otherwise
	 */
	public boolean isPackage() {
		return this.modifierKeyword == ModifierKeyword.PACKAGE_KEYWORD;
	}

	/**
	 * Returns whether the given flags includes the "package" modifier.
	 * 
	 * @param flags the modifier flags
	 * @return <code>true</code> if the <code>PACKAGE</code> bit is
	 *   set, and <code>false</code> otherwise
	 */
	public static boolean isPackage(long flags) {
		return (flags & PACKAGE) != 0;
	}
	/**
	 * Answer true if the receiver is the protected modifier, false otherwise.
	 * 
	 * @return true if the receiver is the protected modifier, false otherwise
	 */
	public boolean isProtected() {
		return this.modifierKeyword == ModifierKeyword.PROTECTED_KEYWORD;
	}

	/**
	 * Returns whether the given flags includes the "protected" modifier.
	 * 
	 * @param flags the modifier flags
	 * @return <code>true</code> if the <code>PROTECTED</code> bit is
	 *   set, and <code>false</code> otherwise
	 */
	public static boolean isProtected(long flags) {
		return (flags & PROTECTED) != 0;
	}
	/**
	 * Answer true if the receiver is the public modifier, false otherwise.
	 * 
	 * @return true if the receiver is the public modifier, false otherwise
	 */
	public boolean isPublic() {
		return this.modifierKeyword == ModifierKeyword.PUBLIC_KEYWORD;
	}

	/**
	 * Returns whether the given flags includes the "public" modifier.
	 * 
	 * @param flags the modifier flags
	 * @return <code>true</code> if the <code>PUBLIC</code> bit is
	 *   set, and <code>false</code> otherwise
	 */
	public static boolean isPublic(long flags) {
		return (flags & PUBLIC) != 0;
	}
	/**
	 * Answer true if the receiver is the export modifier, false otherwise.
	 * 
	 * @return true if the receiver is the export modifier, false otherwise
	 */
	public boolean isExport() {
		return this.modifierKeyword == ModifierKeyword.EXPORT_KEYWORD;
	}

	/**
	 * Returns whether the given flags includes the "export" modifier.
	 * 
	 * @param flags the modifier flags
	 * @return <code>true</code> if the <code>EXPORT</code> bit is
	 *   set, and <code>false</code> otherwise
	 */
	public static boolean isExport(long flags) {
		return (flags & EXPORT) != 0;
	}
	/**
	 * Answer true if the receiver is the static modifier, false otherwise.
	 * 
	 * @return true if the receiver is the static modifier, false otherwise
	 */
	public boolean isStatic() {
		return this.modifierKeyword == ModifierKeyword.STATIC_KEYWORD;
	}

	/**
	 * Returns whether the given flags includes the "static" modifier.
	 * 
	 * @param flags the modifier flags
	 * @return <code>true</code> if the <code>STATIC</code> bit is
	 *   set, and <code>false</code> otherwise
	 */
	public static boolean isStatic(long flags) {
		return (flags & STATIC) != 0;
	}
	/**
	 * Answer true if the receiver is the final modifier, false otherwise.
	 * 
	 * @return true if the receiver is the final modifier, false otherwise
	 */
	public boolean isFinal() {
		return this.modifierKeyword == ModifierKeyword.FINAL_KEYWORD;
	}

	/**
	 * Returns whether the given flags includes the "final" modifier.
	 * 
	 * @param flags the modifier flags
	 * @return <code>true</code> if the <code>FINAL</code> bit is
	 *   set, and <code>false</code> otherwise
	 */
	public static boolean isFinal(long flags) {
		return (flags & FINAL) != 0;
	}
	/**
	 * Answer true if the receiver is the abstract modifier, false otherwise.
	 * 
	 * @return true if the receiver is the abstract modifier, false otherwise
	 */
	public boolean isAbstract() {
		return this.modifierKeyword == ModifierKeyword.ABSTRACT_KEYWORD;
	}

	/**
	 * Returns whether the given flags includes the "abstract" modifier.
	 * 
	 * @param flags the modifier flags
	 * @return <code>true</code> if the <code>ABSTRACT</code> bit is
	 *   set, and <code>false</code> otherwise
	 */
	public static boolean isAbstract(long flags) {
		return (flags & ABSTRACT) != 0;
	}
	/**
	 * Answer true if the receiver is the override modifier, false otherwise.
	 * 
	 * @return true if the receiver is the override modifier, false otherwise
	 */
	public boolean isOverride() {
		return this.modifierKeyword == ModifierKeyword.OVERRIDE_KEYWORD;
	}

	/**
	 * Returns whether the given flags includes the "override" modifier.
	 * 
	 * @param flags the modifier flags
	 * @return <code>true</code> if the <code>OVERRIDE</code> bit is
	 *   set, and <code>false</code> otherwise
	 */
	public static boolean isOverride(long flags) {
		return (flags & OVERRIDE) != 0;
	}
	/**
	 * Answer true if the receiver is the auto modifier, false otherwise.
	 * 
	 * @return true if the receiver is the auto modifier, false otherwise
	 */
	public boolean isAuto() {
		return this.modifierKeyword == ModifierKeyword.AUTO_KEYWORD;
	}

	/**
	 * Returns whether the given flags includes the "auto" modifier.
	 * 
	 * @param flags the modifier flags
	 * @return <code>true</code> if the <code>AUTO</code> bit is
	 *   set, and <code>false</code> otherwise
	 */
	public static boolean isAuto(long flags) {
		return (flags & AUTO) != 0;
	}
	/**
	 * Answer true if the receiver is the synchronized modifier, false otherwise.
	 * 
	 * @return true if the receiver is the synchronized modifier, false otherwise
	 */
	public boolean isSynchronized() {
		return this.modifierKeyword == ModifierKeyword.SYNCHRONIZED_KEYWORD;
	}

	/**
	 * Returns whether the given flags includes the "synchronized" modifier.
	 * 
	 * @param flags the modifier flags
	 * @return <code>true</code> if the <code>SYNCHRONIZED</code> bit is
	 *   set, and <code>false</code> otherwise
	 */
	public static boolean isSynchronized(long flags) {
		return (flags & SYNCHRONIZED) != 0;
	}
	/**
	 * Answer true if the receiver is the deprecated modifier, false otherwise.
	 * 
	 * @return true if the receiver is the deprecated modifier, false otherwise
	 */
	public boolean isDeprecated() {
		return this.modifierKeyword == ModifierKeyword.DEPRECATED_KEYWORD;
	}

	/**
	 * Returns whether the given flags includes the "deprecated" modifier.
	 * 
	 * @param flags the modifier flags
	 * @return <code>true</code> if the <code>DEPRECATED</code> bit is
	 *   set, and <code>false</code> otherwise
	 */
	public static boolean isDeprecated(long flags) {
		return (flags & DEPRECATED) != 0;
	}
	/**
	 * Answer true if the receiver is the extern modifier, false otherwise.
	 * 
	 * @return true if the receiver is the extern modifier, false otherwise
	 */
	public boolean isExtern() {
		return this.modifierKeyword == ModifierKeyword.EXTERN_KEYWORD;
	}

	/**
	 * Returns whether the given flags includes the "extern" modifier.
	 * 
	 * @param flags the modifier flags
	 * @return <code>true</code> if the <code>EXTERN</code> bit is
	 *   set, and <code>false</code> otherwise
	 */
	public static boolean isExtern(long flags) {
		return (flags & EXTERN) != 0;
	}
	/**
	 * Answer true if the receiver is the const modifier, false otherwise.
	 * 
	 * @return true if the receiver is the const modifier, false otherwise
	 */
	public boolean isConst() {
		return this.modifierKeyword == ModifierKeyword.CONST_KEYWORD;
	}

	/**
	 * Returns whether the given flags includes the "const" modifier.
	 * 
	 * @param flags the modifier flags
	 * @return <code>true</code> if the <code>CONST</code> bit is
	 *   set, and <code>false</code> otherwise
	 */
	public static boolean isConst(long flags) {
		return (flags & CONST) != 0;
	}
	/**
	 * Answer true if the receiver is the scope modifier, false otherwise.
	 * 
	 * @return true if the receiver is the scope modifier, false otherwise
	 */
	public boolean isScope() {
		return this.modifierKeyword == ModifierKeyword.SCOPE_KEYWORD;
	}

	/**
	 * Returns whether the given flags includes the "scope" modifier.
	 * 
	 * @param flags the modifier flags
	 * @return <code>true</code> if the <code>SCOPE</code> bit is
	 *   set, and <code>false</code> otherwise
	 */
	public static boolean isScope(long flags) {
		return (flags & SCOPE) != 0;
	}
	
	/**
	 * Returns whether the given flags includes the "enum" modifier.
	 * 
	 * @param flags the modifier flags
	 * @return <code>true</code> if the <code>ENUM</code> bit is
	 *   set, and <code>false</code> otherwise
	 */
	public static boolean isEnum(long flags) {
		return (flags & ENUM) != 0;
	}

}
