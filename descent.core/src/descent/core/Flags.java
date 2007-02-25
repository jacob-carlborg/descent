/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Corporation - added constant AccDefault
 *     IBM Corporation - added constants AccBridge and AccVarargs for J2SE 1.5 
 *******************************************************************************/
package descent.core;


/**
 * Utility class for decoding modifier flags in Java elements.
 * <p>
 * This class provides static methods only; it is not intended to be
 * instantiated or subclassed by clients.
 * </p>
 * <p>
 * Note that the numeric values of these flags match the ones for class files
 * as described in the Java Virtual Machine Specification. The AST class
 * <code>Modifier</code> provides the same functionality as this class, only in
 * the <code>descent.core.dom</code> package.
 * </p>
 *
 * @see IMember#getFlags()
 */
public final class Flags {

	/**
	 * Constant representing the absence of any flag
	 * @since 3.0
	 */
	public static final int AccDefault = 0x0000;
	/**
	 * Private access flag.
	 * @since 2.0
	 */
	public static final int AccPrivate = 0x0001;
	/**
	 * Package access flag.
	 * @since 2.0
	 */
	public static final int AccPackage = 0x0002;
	/**
	 * Protected access flag.
	 * @since 2.0
	 */
	public static final int AccProtected = 0x0004;
	/**
	 * Public access flag.
	 * @since 2.0
	 */
	public static final int AccPublic = 0x0008;
	/**
	 * Export access flag.
	 * @since 2.0
	 */
	public static final int AccExport = 0x0010;
	/**
	 * Static property flag.
	 * @since 2.0
	 */
	public static final int AccStatic = 0x0020;
	/**
	 * Final property flag.
	 * @since 2.0
	 */
	public static final int AccFinal = 0x0040;
	/**
	 * Abstract property flag.
	 * @since 2.0
	 */
	public static final int AccAbstract = 0x0080;
	/**
	 * Override property flag.
	 * @since 2.0
	 */
	public static final int AccOverride = 0x0100;
	/**
	 * Auto property flag.
	 * @since 2.0
	 */
	public static final int AccAuto = 0x0200;
	/**
	 * Synchronized property flag.
	 * @since 2.0
	 */
	public static final int AccSynchronized = 0x0400;
	/**
	 * Deprecated property flag.
	 * @since 2.0
	 */
	public static final int AccDeprecated = 0x0800;
	/**
	 * Extern property flag.
	 * @since 2.0
	 */
	public static final int AccExtern = 0x1000;
	/**
	 * Const property flag.
	 * @since 2.0
	 */
	public static final int AccConst = 0x2000;
	/**
	 * Scope property flag.
	 * @since 2.0
	 */
	public static final int AccScope = 0x4000;
	/**
	 * Enum property flag.
	 * @since 2.0
	 */
	public static final int AccEnum = 0x8000;
	
	// Extensions for types
	
	/**
	 * Interface property flag.
	 * @since 2.0
	 */
	public static final int AccInterface = 0x00010000;
	/**
	 * Struct property flag.
	 * @since 2.0
	 */
	public static final int AccStruct = 0x00020000;
	/**
	 * Union property flag.
	 * @since 2.0
	 */
	public static final int AccUnion = 0x00040000;
	/**
	 * Template property flag.
	 * @since 2.0
	 */
	public static final int AccTemplate = 0x00080000;
	
	// Extensions for methods (can reuse flags)
	
	/**
	 * Destructor property flag.
	 * @since 2.0
	 */
	public static final int AccConstructor = 0x00010000;
	
	/**
	 * Destructor property flag.
	 * @since 2.0
	 */
	public static final int AccDestructor = 0x00020000;
	
	/**
	 * New property flag.
	 * @since 2.0
	 */
	public static final int AccNew = 0x00040000;
	
	/**
	 * New property flag.
	 * @since 2.0
	 */
	public static final int AccDelete = 0x00080000;
	
	// Extensions for fields (can reuse flags)
	
	/**
	 * Alias property flag.
	 * @since 2.0
	 */
	public static final int AccAlias = 0x00010000;
	/**
	 * Typedef property flag.
	 * @since 2.0
	 */
	public static final int AccTypedef = 0x00020000;
	/**
	 * Mixin property flag.
	 * @since 2.0
	 */
	public static final int AccMixin = 0x00040000;
	
	// Extensions for initializers (can reuse flags)
	
	/**
	 * Mixin property flag.
	 * @since 2.0
	 */
	public static final int AccStaticDestructor = 0x00010000;
	
	/**
	 * Invariant property flag.
	 * @since 2.0
	 */
	public static final int AccInvariant = 0x00020000;
	
	/**
	 * UnitTest property flag.
	 * @since 2.0
	 */
	public static final int AccUnitTest = 0x00040000;
	
	/**
	 * Static assert property flag.
	 * @since 2.0
	 */
	public static final int AccStaticAssert = 0x00080000;
	
	/**
	 * Debug assignment property flag.
	 * @since 2.0
	 */
	public static final int AccDebugAssignment = 0x00100000;
	
	/**
	 * Version assignment property flag.
	 * @since 2.0
	 */
	public static final int AccVersionAssignment = 0x00200000;
	
	/**
	 * Align property flag.
	 * @since 2.0
	 */
	public static final int AccAlign = 0x00400000;
	
	/**
	 * Extern declaration property flag.
	 * @since 2.0
	 */
	public static final int AccExternDeclaration = 0x00800000;
	
	/**
	 * Pragma property flag.
	 * @since 2.0
	 */
	public static final int AccPragma = 0x01000000;

	/**
	 * Not instantiable.
	 */
	private Flags() {
		// Not instantiable
	}
	
	/**
	 * Returns whether the given integer includes the <code>private</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>private</code> modifier is included
	 */
	public static boolean isPrivate(int flags) {
		return (flags & AccPrivate) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>package</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>package</code> modifier is included
	 */
	public static boolean isPackage(int flags) {
		return (flags & AccPackage) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>protected</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>protected</code> modifier is included
	 */
	public static boolean isProtected(int flags) {
		return (flags & AccProtected) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>public</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>public</code> modifier is included
	 */
	public static boolean isPublic(int flags) {
		return (flags & AccPublic) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>export</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>export</code> modifier is included
	 */
	public static boolean isExport(int flags) {
		return (flags & AccExport) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>static</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>static</code> modifier is included
	 */
	public static boolean isStatic(int flags) {
		return (flags & AccStatic) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>final</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>final</code> modifier is included
	 */
	public static boolean isFinal(int flags) {
		return (flags & AccFinal) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>abstract</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>abstract</code> modifier is included
	 */
	public static boolean isAbstract(int flags) {
		return (flags & AccAbstract) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>override</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>override</code> modifier is included
	 */
	public static boolean isOverride(int flags) {
		return (flags & AccOverride) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>auto</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>auto</code> modifier is included
	 */
	public static boolean isAuto(int flags) {
		return (flags & AccAuto) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>synchronized</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>synchronized</code> modifier is included
	 */
	public static boolean isSynchronized(int flags) {
		return (flags & AccSynchronized) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>deprecated</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>deprecated</code> modifier is included
	 */
	public static boolean isDeprecated(int flags) {
		return (flags & AccDeprecated) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>extern</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>extern</code> modifier is included
	 */
	public static boolean isExtern(int flags) {
		return (flags & AccExtern) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>const</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>const</code> modifier is included
	 */
	public static boolean isConst(int flags) {
		return (flags & AccConst) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>scope</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>scope</code> modifier is included
	 */
	public static boolean isScope(int flags) {
		return (flags & AccScope) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>enum</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>enum</code> modifier is included
	 */
	public static boolean isEnum(int flags) {
		return (flags & AccEnum) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>interface</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>interface</code> modifier is included
	 */
	public static boolean isInterface(int flags) {
		return (flags & AccInterface) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>struct</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>struct</code> modifier is included
	 */
	public static boolean isStruct(int flags) {
		return (flags & AccStruct) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>union</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>union</code> modifier is included
	 */
	public static boolean isUnion(int flags) {
		return (flags & AccUnion) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>template</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>template</code> modifier is included
	 */
	public static boolean isTemplate(int flags) {
		return (flags & AccTemplate) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>alias</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>alias</code> modifier is included
	 */
	public static boolean isAlias(int flags) {
		return (flags & AccAlias) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>typedef</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>typedef</code> modifier is included
	 */
	public static boolean isTypedef(int flags) {
		return (flags & AccTypedef) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>mixin</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>mixin</code> modifier is included
	 */
	public static boolean isMixin(int flags) {
		return (flags & AccMixin) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>static destructor</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>static destructor</code> modifier is included
	 */
	public static boolean isStaticDestructor(int flags) {
		return (flags & AccStaticDestructor) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>constructor</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>constructor</code> modifier is included
	 */
	public static boolean isConstructor(int flags) {
		return (flags & AccConstructor) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>destructor</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>destructor</code> modifier is included
	 */
	public static boolean isDestructor(int flags) {
		return (flags & AccDestructor) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>new</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>new</code> modifier is included
	 */
	public static boolean isNew(int flags) {
		return (flags & AccNew) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>delete</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>delete</code> modifier is included
	 */
	public static boolean isDelete(int flags) {
		return (flags & AccDelete) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>invariant</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>invariant</code> modifier is included
	 */
	public static boolean isInvariant(int flags) {
		return (flags & AccInvariant) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>unittest</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>unittest</code> modifier is included
	 */
	public static boolean isUnitTest(int flags) {
		return (flags & AccUnitTest) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>static assert</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>static assert</code> modifier is included
	 */
	public static boolean isStaticAssert(int flags) {
		return (flags & AccStaticAssert) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>version assignment</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>version assignment</code> modifier is included
	 */
	public static boolean isVersionAssignment(int flags) {
		return (flags & AccVersionAssignment) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>debug assignment</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>debug assignment</code> modifier is included
	 */
	public static boolean isDebugAssignment(int flags) {
		return (flags & AccDebugAssignment) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>align</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>align</code> modifier is included
	 */
	public static boolean isAlign(int flags) {
		return (flags & AccAlign) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>extern declaration</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>extern declaration</code> modifier is included
	 */
	public static boolean isExternDeclaration(int flags) {
		return (flags & AccExternDeclaration) != 0;
	}
	
	/**
	 * Returns whether the given integer includes the <code>pragma</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>pragma</code> modifier is included
	 */
	public static boolean isPragma(int flags) {
		return (flags & AccPragma) != 0;
	}
	
	/**
	 * Returns a standard string describing the given modifier flags.
	 * @param flags the flags
	 * @return the standard string representation of the given flags
	 */
	public static String toString(int flags) {
		StringBuffer sb = new StringBuffer();

		if (isPrivate(flags))
			sb.append("private "); //$NON-NLS-1$
		if (isPackage(flags))
			sb.append("package "); //$NON-NLS-1$
		if (isProtected(flags))
			sb.append("protected "); //$NON-NLS-1$
		if (isPublic(flags))
			sb.append("public "); //$NON-NLS-1$
		if (isExport(flags))
			sb.append("export "); //$NON-NLS-1$
		if (isStatic(flags))
			sb.append("static "); //$NON-NLS-1$
		if (isFinal(flags))
			sb.append("final "); //$NON-NLS-1$
		if (isAbstract(flags))
			sb.append("abstract "); //$NON-NLS-1$
		if (isOverride(flags))
			sb.append("override "); //$NON-NLS-1
		if (isAuto(flags))
			sb.append("auto "); //$NON-NLS-1$
		if (isSynchronized(flags))
			sb.append("synchronized "); //$NON-NLS-1$
		if (isDeprecated(flags))
			sb.append("deprecated "); //$NON-NLS-1$
		if (isExtern(flags))
			sb.append("extern "); //$NON-NLS-1$
		if (isConst(flags))
			sb.append("const "); //$NON-NLS-1$
		if (isScope(flags))
			sb.append("scope "); //$NON-NLS-1$
		if (isEnum(flags))
			sb.append("enum "); //$NON-NLS-1$
		if (isInterface(flags))
			sb.append("interface "); //$NON-NLS-1$
		if (isStruct(flags))
			sb.append("struct "); //$NON-NLS-1$
		if (isUnion(flags))
			sb.append("union "); //$NON-NLS-1$
		if (isTemplate(flags))
			sb.append("template "); //$NON-NLS-1$
		if (isAlias(flags))
			sb.append("alias "); //$NON-NLS-1$
		if (isTypedef(flags))
			sb.append("typedef "); //$NON-NLS-1$
		if (isMixin(flags))
			sb.append("mixin "); //$NON-NLS-1$
		if (isStaticDestructor(flags))
			sb.append("~static this() "); //$NON-NLS-1$
		if (isConstructor(flags))
			sb.append("this() "); //$NON-NLS-1$
		if (isDestructor(flags))
			sb.append("~this() "); //$NON-NLS-1$
		if (isNew(flags))
			sb.append("new() "); //$NON-NLS-1$
		if (isDelete(flags))
			sb.append("delete() "); //$NON-NLS-1$
		if (isInvariant(flags))
			sb.append("invariant "); //$NON-NLS-1$
		if (isUnitTest(flags))
			sb.append("unittest "); //$NON-NLS-1$
		if (isStaticAssert(flags))
			sb.append("static assert "); //$NON-NLS-1$
		if (isVersionAssignment(flags))
			sb.append("version= "); //$NON-NLS-1$
		if (isDebugAssignment(flags))
			sb.append("debug= "); //$NON-NLS-1$
		if (isAlign(flags))
			sb.append("align "); //$NON-NLS-1$
		if (isExternDeclaration(flags))
			sb.append("extern declaration "); //$NON-NLS-1$
		if (isPragma(flags))
			sb.append("pragma "); //$NON-NLS-1$

		int len = sb.length();
		if (len == 0)
			return ""; //$NON-NLS-1$
		sb.setLength(len - 1);
		return sb.toString();
	}
}
