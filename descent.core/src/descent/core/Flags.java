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

import descent.core.dom.Modifier;

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
	public static final int AccPrivate = Modifier.PRIVATE;
	/**
	 * Package access flag.
	 * @since 2.0
	 */
	public static final int AccPackage = Modifier.PACKAGE;
	/**
	 * Protected access flag.
	 * @since 2.0
	 */
	public static final int AccProtected = Modifier.PROTECTED;
	/**
	 * Public access flag.
	 * @since 2.0
	 */
	public static final int AccPublic = Modifier.PUBLIC;
	/**
	 * Export access flag.
	 * @since 2.0
	 */
	public static final int AccExport = Modifier.EXPORT;
	/**
	 * Static property flag.
	 * @since 2.0
	 */
	public static final int AccStatic = Modifier.STATIC;
	/**
	 * Final property flag.
	 * @since 2.0
	 */
	public static final int AccFinal = Modifier.FINAL;
	/**
	 * Abstract property flag.
	 * @since 2.0
	 */
	public static final int AccAbstract = Modifier.ABSTRACT;
	/**
	 * Override property flag.
	 * @since 2.0
	 */
	public static final int AccOverride = Modifier.OVERRIDE;
	/**
	 * Auto property flag.
	 * @since 2.0
	 */
	public static final int AccAuto = Modifier.AUTO;
	/**
	 * Synchronized property flag.
	 * @since 2.0
	 */
	public static final int AccSynchronized = Modifier.SYNCHRONIZED;
	/**
	 * Deprecated property flag.
	 * @since 2.0
	 */
	public static final int AccDeprecated = Modifier.DEPRECATED;
	/**
	 * Extern property flag.
	 * @since 2.0
	 */
	public static final int AccExtern = Modifier.EXTERN;
	/**
	 * Const property flag.
	 * @since 2.0
	 */
	public static final int AccConst = Modifier.CONST;
	/**
	 * Scope property flag.
	 * @since 2.0
	 */
	public static final int AccScope = Modifier.SCOPE;

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
	
	// TODO JDT flags
	public static boolean isEnum(int flags) {
		return false;
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

		int len = sb.length();
		if (len == 0)
			return ""; //$NON-NLS-1$
		sb.setLength(len - 1);
		return sb.toString();
	}
}
