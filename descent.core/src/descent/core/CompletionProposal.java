/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.core;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.compiler.CharOperation;
import descent.internal.codeassist.InternalCompletionProposal;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.TemplateDeclaration;
import descent.internal.compiler.parser.TypeFunction;
import descent.internal.core.util.Util;

/**
 * Completion proposal.
 * <p>
 * In typical usage, the user working in a Java code editor issues
 * a code assist command. This command results in a call to
 * <code>ICodeAssist.codeComplete(position, completionRequestor)</code>
 * passing the current position in the source code. The code assist
 * engine analyzes the code in the buffer, determines what kind of
 * Java language construct is at that position, and proposes ways
 * to complete that construct. These proposals are instances of
 * the class <code>CompletionProposal</code>. These proposals,
 * perhaps after sorting and filtering, are presented to the user
 * to make a choice.
 * </p>
 * <p>
 * The proposal is as follows: insert
 * the {@linkplain #getCompletion() completion string} into the
 * source file buffer, replacing the characters between 
 * {@linkplain #getReplaceStart() the start}
 * and {@linkplain #getReplaceEnd() end}. The string
 * can be arbitrary; for example, it might include not only the 
 * name of a method but a set of parentheses. Moreover, the source
 * range may include source positions before or after the source
 * position where <code>ICodeAssist.codeComplete</code> was invoked.
 * The rest of the information associated with the proposal is
 * to provide context that may help a user to choose from among
 * competing proposals.
 * </p>
 * <p>
 * The completion engine creates instances of this class; it is not intended 
 * to be instantiated or subclassed by clients.
 * </p>
 * 
 * @see ICodeAssist#codeComplete(int, CompletionRequestor)
 * @since 3.0
 */
public final class CompletionProposal extends InternalCompletionProposal {
	private boolean updateCompletion = false;

	/**
	 * Completion is a declaration of an anonymous class.
	 * This kind of completion might occur in a context like
	 * <code>"new List^;"</code> and complete it to
	 * <code>"new List() {}"</code>.
	 * <p>
	 * The following additional context information is available
	 * for this kind of completion proposal at little extra cost:
	 * <ul>
	 * <li>{@link #getDeclarationSignature()} -
	 * the type signature of the type being implemented or subclassed
	 * </li>
	 * <li>{@link #getDeclarationKey()} -
	 * the type unique key of the type being implemented or subclassed
	 * </li>
	 * <li>{@link #getSignature()} -
	 * the method signature of the constructor that is referenced
	 * </li>
	 * <li>{@link #getKey()} -
	 * the method unique key of the constructor that is referenced
	 * if the declaring type is not an interface
	 * </li>
	 * <li>{@link #getFlags()} -
	 * the modifiers flags of the constructor that is referenced
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @see #getKind()
	 */
	public static final int ANONYMOUS_CLASS_DECLARATION = 1;

	/**
	 * Completion is a reference to a field.
	 * This kind of completion might occur in a context like
	 * <code>"this.ref^ = 0;"</code> and complete it to
	 * <code>"this.refcount = 0;"</code>.
	 * <p>
	 * The following additional context information is available
	 * for this kind of completion proposal at little extra cost:
	 * <ul>
	 * <li>{@link #getDeclarationSignature()} -
	 * the type signature of the type that declares the field that is referenced
	 * </li>
	 * <li>{@link #getFlags()} -
	 * the modifiers flags (including ACC_ENUM) of the field that is referenced
	 * </li>
	 * <li>{@link #getName()} -
	 * the simple name of the field that is referenced
	 * </li>
	 * <li>{@link #getSignature()} -
	 * the type signature of the field's type (as opposed to the
	 * signature of the type in which the referenced field
	 * is declared)
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @see #getKind()
	 */
	public static final int FIELD_REF = 2;

	/**
	 * Completion is a keyword.
	 * This kind of completion might occur in a context like
	 * <code>"public cl^ Foo {}"</code> and complete it to
	 * <code>"public class Foo {}"</code>.
	 * <p>
	 * The following additional context information is available
	 * for this kind of completion proposal at little extra cost:
	 * <ul>
	 * <li>{@link #getName()} -
	 * the keyword token
	 * </li>
	 * <li>{@link #getFlags()} -
	 * the corresponding modifier flags if the keyword is a modifier
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @see #getKind()
	 */
	public static final int KEYWORD = 3;

	/**
	 * Completion is a reference to a label.
	 * This kind of completion might occur in a context like
	 * <code>"break lo^;"</code> and complete it to
	 * <code>"break loop;"</code>.
	 * <p>
	 * The following additional context information is available
	 * for this kind of completion proposal at little extra cost:
	 * <ul>
	 * <li>{@link #getName()} -
	 * the simple name of the label that is referenced
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @see #getKind()
	 */
	public static final int LABEL_REF = 4;

	/**
	 * Completion is a reference to a local variable.
	 * This kind of completion might occur in a context like
	 * <code>"ke^ = 4;"</code> and complete it to
	 * <code>"keys = 4;"</code>.
	 * <p>
	 * The following additional context information is available
	 * for this kind of completion proposal at little extra cost:
	 * <ul>
	 * <li>{@link #getFlags()} -
	 * the modifiers flags of the local variable that is referenced
	 * </li>
	 * <li>{@link #getName()} -
	 * the simple name of the local variable that is referenced
	 * </li>
	 * <li>{@link #getSignature()} -
	 * the type signature of the local variable's type
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @see #getKind()
	 */
	public static final int LOCAL_VARIABLE_REF = 5;

	/**
	 * Completion is a reference to a method.
	 * This kind of completion might occur in a context like
	 * <code>"System.out.pr^();"</code> and complete it to
	 * <code>""System.out.println();"</code>.
	 * <p>
	 * The following additional context information is available
	 * for this kind of completion proposal at little extra cost:
	 * <ul>
	 * <li>{@link #getDeclarationSignature()} -
	 * the type signature of the type that declares the method that is referenced
	 * </li>
	 * <li>{@link #getFlags()} -
	 * the modifiers flags of the method that is referenced
	 * </li>
	 * <li>{@link #getName()} -
	 * the simple name of the method that is referenced
	 * </li>
	 * <li>{@link #getSignature()} -
	 * the method signature of the method that is referenced
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @see #getKind()
	 */
	public static final int METHOD_REF = 6;

	/**
	 * Completion is a declaration of a method.
	 * This kind of completion might occur in a context like
	 * <code>"new List() {si^};"</code> and complete it to
	 * <code>"new List() {public int size() {} };"</code>.
	 * <p>
	 * The following additional context information is available
	 * for this kind of completion proposal at little extra cost:
	 * <ul>
	 * <li>{@link #getDeclarationSignature()} -
	 * the type signature of the type that declares the
	 * method that is being overridden or implemented
	 * </li>
	 * <li>{@link #getDeclarationKey()} -
	 * the unique of the type that declares the
	 * method that is being overridden or implemented
	 * </li>
	 * <li>{@link #getName()} -
	 * the simple name of the method that is being overridden
	 * or implemented
	 * </li>
	 * <li>{@link #getSignature()} -
	 * the method signature of the method that is being
	 * overridden or implemented
	 * </li>
	 * <li>{@link #getKey()} -
	 * the method unique key of the method that is being
	 * overridden or implemented
	 * </li>
	 * <li>{@link #getFlags()} -
	 * the modifiers flags of the method that is being
	 * overridden or implemented
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @see #getKind()
	 */
	public static final int METHOD_DECLARATION = 7;

	/**
	 * Completion is a reference to a package.
	 * This kind of completion might occur in a context like
	 * <code>"import java.u^.*;"</code> and complete it to
	 * <code>"import java.util.*;"</code>.
	 * <p>
	 * The following additional context information is available
	 * for this kind of completion proposal at little extra cost:
	 * <ul>
	 * <li>{@link #getDeclarationSignature()} -
	 * the dot-based package name of the package that is referenced
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @see #getKind()
	 */
	public static final int COMPILATION_UNIT_REF = 8;

	/**
	 * Completion is a reference to a type. Any kind of type
	 * is allowed, including primitive types, reference types,
	 * array types, parameterized types, and type variables.
	 * This kind of completion might occur in a context like
	 * <code>"public static Str^ key;"</code> and complete it to
	 * <code>"public static String key;"</code>.
	 * <p>
	 * The following additional context information is available
	 * for this kind of completion proposal at little extra cost:
	 * <ul>
	 * <li>{@link #getDeclarationSignature()} -
	 * the dot-based package name of the package that contains
	 * the type that is referenced
	 * </li>
	 * <li>{@link #getSignature()} -
	 * the type signature of the type that is referenced
	 * </li>
	 * <li>{@link #getFlags()} -
	 * the modifiers flags (including Flags.AccInterface, AccEnum,
	 * and AccAnnotation) of the type that is referenced
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @see #getKind()
	 */
	public static final int TYPE_REF = 9;

	/**
	 * Completion is a declaration of a variable (locals, parameters,
	 * fields, etc.).
	 * <p>
	 * The following additional context information is available
	 * for this kind of completion proposal at little extra cost:
	 * <ul>
	 * <li>{@link #getName()} -
	 * the simple name of the variable being declared
	 * </li>
	 * <li>{@link #getSignature()} -
	 * the type signature of the type of the variable
	 * being declared
	 * </li>
	 * <li>{@link #getFlags()} -
	 * the modifiers flags of the variable being declared
	 * </li>
	 * </ul>
	 * </p>
	 * @see #getKind()
	 */
	public static final int VARIABLE_DECLARATION = 10;

	/**
	 * Completion is a declaration of a new potential method.
	 * This kind of completion might occur in a context like
	 * <code>"new List() {si^};"</code> and complete it to
	 * <code>"new List() {public int si() {} };"</code>.
	 * <p>
	 * The following additional context information is available
	 * for this kind of completion proposal at little extra cost:
	 * <ul>
	 * <li>{@link #getDeclarationSignature()} -
	 * the type signature of the type that declares the
	 * method that is being created
	 * </li>
	 * <li>{@link #getName()} -
	 * the simple name of the method that is being created
	 * </li>
	 * <li>{@link #getSignature()} -
	 * the method signature of the method that is being
	 * created
	 * </li>
	 * <li>{@link #getFlags()} -
	 * the modifiers flags of the method that is being
	 * created
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @see #getKind()
     * @since 3.1
	 */
	public static final int POTENTIAL_METHOD_DECLARATION = 11;
	
	/**
	 * Completion is a reference to a method name.
	 * This kind of completion might occur in a context like
	 * <code>"import p.X.fo^"</code> and complete it to
	 * <code>"import p.X.foo;"</code>.
	 * <p>
	 * The following additional context information is available
	 * for this kind of completion proposal at little extra cost:
	 * <ul>
	 * <li>{@link #getDeclarationSignature()} -
	 * the type signature of the type that declares the method that is referenced
	 * </li>
	 * <li>{@link #getFlags()} -
	 * the modifiers flags of the method that is referenced
	 * </li>
	 * <li>{@link #getName()} -
	 * the simple name of the method that is referenced
	 * </li>
	 * <li>{@link #getSignature()} -
	 * the method signature of the method that is referenced
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @see #getKind()
     * @since 3.1
	 */
	public static final int METHOD_NAME_REFERENCE = 12;
	
	/**
	 * Completion is a reference to annotation's attribute.
	 * This kind of completion might occur in a context like
	 * <code>"@Annot(attr^=value)"</code> and complete it to
	 * <code>"@Annot(attribute^=value)"</code>.
	 * <p>
	 * The following additional context information is available
	 * for this kind of completion proposal at little extra cost:
	 * <ul>
	 * <li>{@link #getDeclarationSignature()} -
	 * the type signature of the annotation that declares the attribute that is referenced
	 * </li>
	 * <li>{@link #getFlags()} -
	 * the modifiers flags of the attribute that is referenced
	 * </li>
	 * <li>{@link #getName()} -
	 * the simple name of the attribute that is referenced
	 * </li>
	 * <li>{@link #getSignature()} -
	 * the type signature of the attribute's type (as opposed to the
	 * signature of the type in which the referenced attribute
	 * is declared)
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @see #getKind()
	 * @since 3.1
	 */
	public static final int ANNOTATION_ATTRIBUTE_REF = 13;

	/**
	 * Completion is a link reference to a field in a javadoc text.
	 * This kind of completion might occur in a context like
	 * <code>"	* blabla System.o^ blabla"</code> and complete it to
	 * <code>"	* blabla {&#64;link System#out } blabla"</code>.
	 * <p>
	 * The following additional context information is available
	 * for this kind of completion proposal at little extra cost:
	 * <ul>
	 * <li>{@link #getDeclarationSignature()} -
	 * the type signature of the type that declares the field that is referenced
	 * </li>
	 * <li>{@link #getFlags()} -
	 * the modifiers flags (including ACC_ENUM) of the field that is referenced
	 * </li>
	 * <li>{@link #getName()} -
	 * the simple name of the field that is referenced
	 * </li>
	 * <li>{@link #getSignature()} -
	 * the type signature of the field's type (as opposed to the
	 * signature of the type in which the referenced field
	 * is declared)
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @see #getKind()
	 * @since 3.2
	 */
	public static final int JAVADOC_FIELD_REF = 14;

	/**
	 * Completion is a link reference to a method in a javadoc text.
	 * This kind of completion might occur in a context like
	 * <code>"	* blabla Runtime.get^ blabla"</code> and complete it to
	 * <code>"	* blabla {&#64;link Runtime#getRuntime() }"</code>.
	 * <p>
	 * The following additional context information is available
	 * for this kind of completion proposal at little extra cost:
	 * <ul>
	 * <li>{@link #getDeclarationSignature()} -
	 * the type signature of the type that declares the method that is referenced
	 * </li>
	 * <li>{@link #getFlags()} -
	 * the modifiers flags of the method that is referenced
	 * </li>
	 * <li>{@link #getName()} -
	 * the simple name of the method that is referenced
	 * </li>
	 * <li>{@link #getSignature()} -
	 * the method signature of the method that is referenced
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @see #getKind()
	 * @since 3.2
	 */
	public static final int JAVADOC_METHOD_REF = 15;

	/**
	 * Completion is a link reference to a type in a javadoc text.
	 * Any kind of type is allowed, including primitive types, reference types,
	 * array types, parameterized types, and type variables.
	 * This kind of completion might occur in a context like
	 * <code>"	* blabla Str^ blabla"</code> and complete it to
	 * <code>"	* blabla {&#64;link String } blabla"</code>.
	 * <p>
	 * The following additional context information is available
	 * for this kind of completion proposal at little extra cost:
	 * <ul>
	 * <li>{@link #getDeclarationSignature()} -
	 * the dot-based package name of the package that contains
	 * the type that is referenced
	 * </li>
	 * <li>{@link #getSignature()} -
	 * the type signature of the type that is referenced
	 * </li>
	 * <li>{@link #getFlags()} -
	 * the modifiers flags (including Flags.AccInterface, AccEnum,
	 * and AccAnnotation) of the type that is referenced
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @see #getKind()
	 * @since 3.2
	 */
	public static final int JAVADOC_TYPE_REF = 16;

	/**
	 * Completion is a value reference to a static field in a javadoc text.
	 * This kind of completion might occur in a context like
	 * <code>"	* blabla System.o^ blabla"</code> and complete it to
	 * <code>"	* blabla {&#64;value System#out } blabla"</code>.
	 * <p>
	 * The following additional context information is available
	 * for this kind of completion proposal at little extra cost:
	 * <ul>
	 * <li>{@link #getDeclarationSignature()} -
	 * the type signature of the type that declares the field that is referenced
	 * </li>
	 * <li>{@link #getFlags()} -
	 * the modifiers flags (including ACC_ENUM) of the field that is referenced
	 * </li>
	 * <li>{@link #getName()} -
	 * the simple name of the field that is referenced
	 * </li>
	 * <li>{@link #getSignature()} -
	 * the type signature of the field's type (as opposed to the
	 * signature of the type in which the referenced field
	 * is declared)
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @see #getKind()
	 * @since 3.2
	 */
	public static final int JAVADOC_VALUE_REF = 17;

	/**
	 * Completion is a method argument or a class/method type parameter
	 * in javadoc param tag.
	 * This kind of completion might occur in a context like
	 * <code>"	* @param arg^ blabla"</code> and complete it to
	 * <code>"	* @param argument blabla"</code>.
	 * or
	 * <code>"	* @param &lt;T^ blabla"</code> and complete it to
	 * <code>"	* @param &lt;TT&gt; blabla"</code>.
	 * <p>
	 * The following additional context information is available
	 * for this kind of completion proposal at little extra cost:
	 * <ul>
	 * <li>{@link #getDeclarationSignature()} -
	 * the type signature of the type that declares the field that is referenced
	 * </li>
	 * <li>{@link #getFlags()} -
	 * the modifiers flags (including ACC_ENUM) of the field that is referenced
	 * </li>
	 * <li>{@link #getName()} -
	 * the simple name of the field that is referenced
	 * </li>
	 * <li>{@link #getSignature()} -
	 * the type signature of the field's type (as opposed to the
	 * signature of the type in which the referenced field
	 * is declared)
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @see #getKind()
	 * @since 3.2
	 */
	public static final int JAVADOC_PARAM_REF = 18;

	/**
	 * Completion is a javadoc block tag.
	 * This kind of completion might occur in a context like
	 * <code>"	* @s^ blabla"</code> and complete it to
	 * <code>"	* @see blabla"</code>.
	 * <p>
	 * The following additional context information is available
	 * for this kind of completion proposal at little extra cost:
	 * <ul>
	 * <li>{@link #getDeclarationSignature()} -
	 * the type signature of the type that declares the field that is referenced
	 * </li>
	 * <li>{@link #getFlags()} -
	 * the modifiers flags (including ACC_ENUM) of the field that is referenced
	 * </li>
	 * <li>{@link #getName()} -
	 * the simple name of the field that is referenced
	 * </li>
	 * <li>{@link #getSignature()} -
	 * the type signature of the field's type (as opposed to the
	 * signature of the type in which the referenced field
	 * is declared)
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @see #getKind()
	 * @since 3.2
	 */
	public static final int JAVADOC_BLOCK_TAG = 19;

	/**
	 * Completion is a javadoc inline tag.
	 * This kind of completion might occur in a context like
	 * <code>"	* Insert @l^ Object"</code> and complete it to
	 * <code>"	* Insert {&#64;link Object }"</code>.
	 * <p>
	 * The following additional context information is available
	 * for this kind of completion proposal at little extra cost:
	 * <ul>
	 * <li>{@link #getDeclarationSignature()} -
	 * the type signature of the type that declares the field that is referenced
	 * </li>
	 * <li>{@link #getFlags()} -
	 * the modifiers flags (including ACC_ENUM) of the field that is referenced
	 * </li>
	 * <li>{@link #getName()} -
	 * the simple name of the field that is referenced
	 * </li>
	 * <li>{@link #getSignature()} -
	 * the type signature of the field's type (as opposed to the
	 * signature of the type in which the referenced field
	 * is declared)
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @see #getKind()
	 * @since 3.2
	 */
	public static final int JAVADOC_INLINE_TAG = 20;
	
	/**
	 * Completion is a version identifier.
	 * 
	 * @see #getKind()
	 * @since 3.2
	 */
	public static final int VERSION_REF = 21;
	
	/**
	 * Completion is a ddoc macro.
	 * 
	 * @see #getKind()
	 * @since 3.2
	 */
	public static final int DDOC_MACRO = 22;
	
	/**
	 * Completion is a template.
	 * 
	 * @see #getKind()
	 * @since 3.2
	 */
	public static final int TEMPLATE_REF = 23;
	
	/**
	 * Completion is a templated aggregate.
	 * 
	 * @see #getKind()
	 * @since 3.2
	 */
	public static final int TEMPLATED_AGGREGATE_REF = 24;
	
	/**
	 * Completion is a templated function.
	 * 
	 * @see #getKind()
	 * @since 3.2
	 */
	public static final int TEMPLATED_FUNCTION_REF = 25;
	
	/**
	 * Completion is a debug identifier.
	 * 
	 * @see #getKind()
	 * @since 3.2
	 */
	public static final int DEBUG_REF = 26;
	
	/**
	 * Completion is a debug identifier.
	 * 
	 * @see #getKind()
	 * @since 3.2
	 */
	public static final int FUNCTION_CALL = 27;
	
	/**
	 * Completion is a debug identifier.
	 * 
	 * @see #getKind()
	 * @since 3.2
	 */
	public static final int OP_CALL = 28;
	
	/**
	 * Completion is an enum member.
	 * 
	 * @see #getKind()
	 * @since 3.2
	 */
	public static final int ENUM_MEMBER = 29;
	
	public static final int EXTENSION_METHOD = 30;

	/**
	 * First valid completion kind.
	 * 
	 * @since 3.1
	 */
	protected static final int FIRST_KIND = ANONYMOUS_CLASS_DECLARATION;
	
	/**
	 * Last valid completion kind.
	 * 
	 * @since 3.1
	 */
	protected static final int LAST_KIND = EXTENSION_METHOD;
	
	/**
	 * Kind of completion request.
	 */
	private final int completionKind;
	
	/**
	 * Offset in original buffer where ICodeAssist.codeComplete() was
	 * requested.
	 */
	private final int completionLocation;
	
	/**
	 * Start position (inclusive) of source range in original buffer 
	 * containing the relevant token
	 * defaults to empty subrange at [0,0).
	 */
	private int tokenStart = 0;
	
	/**
	 * End position (exclusive) of source range in original buffer 
	 * containing the relevant token;
	 * defaults to empty subrange at [0,0).
	 */
	private int tokenEnd = 0;
	
	/**
	 * Completion string; defaults to empty string.
	 */
	private char[] completion = CharOperation.NO_CHAR;
	
	/**
	 * Start position (inclusive) of source range in original buffer 
	 * to be replaced by completion string; 
	 * defaults to empty subrange at [0,0).
	 */
	private int replaceStart = 0;
	
	/**
	 * End position (exclusive) of source range in original buffer 
	 * to be replaced by completion string;
	 * defaults to empty subrange at [0,0).
	 */
	private int replaceEnd = 0;
	
	/**
	 * Relevance rating; positive; higher means better;
	 * defaults to minimum rating.
	 */
	private int relevance = 1;
	
	/**
	 * Signature of the relevant package or type declaration
	 * in the context, or <code>null</code> if none.
	 * Defaults to null.
	 */
	private char[] declarationSignature = null;
	
	/**
	 * Unique key of the relevant package or type declaration
	 * in the context, or <code>null</code> if none.
	 * Defaults to null.
	 */
	private char[] declarationKey = null;
	
	/**
	 * Simple name of the method, field,
	 * member, or variable relevant in the context, or
	 * <code>null</code> if none.
	 * Defaults to null.
	 */
	private char[] name = null;
	
	/**
	 * Signature of the method, field type, member type,
	 * relevant in the context, or <code>null</code> if none.
	 * Defaults to null.
	 */
	private char[] signature = null;
	
	/**
	 * Unique of the method, field type, member type,
	 * relevant in the context, or <code>null</code> if none.
	 * Defaults to null.
	 */
	private char[] key = null;
	
	/**
	 * Modifier flags relevant in the context, or
	 * <code>Flags.AccDefault</code> if none.
	 * Defaults to <code>Flags.AccDefault</code>.
	 */
	private long flags = Flags.AccDefault;
	
	/**
	 * Parameter names (for method completions), or
	 * <code>null</code> if none. Lazily computed.
	 * Defaults to <code>null</code>.
	 */
	private char[][] parameterNames = null;
	
	/**
	 * Indicates whether parameter names have been computed.
	 */
	private boolean parameterNamesComputed = false;
	
	/**
	 * Parameter default values (for method completions), or
	 * <code>null</code> if none. Lazily computed.
	 * Defaults to <code>null</code>.
	 */
	private char[][] parameterDefaultValues = null;
	
	/**
	 * Indicates whether parameter default values have been computed.
	 */
	private boolean parameterDefaultValuesComputed = false;
	
	/**
	 * Template parameter names (for template completions), or
	 * <code>null</code> if none. Lazily computed.
	 * Defaults to <code>null</code>.
	 */
	private char[][] templateParameterNames = null;
	
	/**
	 * Indicates whether template parameter names have been computed.
	 */
	private boolean templateParameterNamesComputed = false;
	
	/**
	 * Template parameter default values (for template completions), or
	 * <code>null</code> if none. Lazily computed.
	 * Defaults to <code>null</code>.
	 */
	private char[][] templateParameterDefaultValues = null;
	
	/**
	 * Indicates whether template parameter default values have been computed.
	 */
	private boolean templateParameterDefaultValuesComputed = false;
	
	/**
	 * Creates a basic completion proposal. All instance
	 * field have plausible default values unless otherwise noted.
	 * <p>
	 * Note that the constructors for this class are internal to the
	 * Java model implementation. Clients cannot directly create
	 * CompletionProposal objects.
	 * </p>
	 * 
	 * @param kind one of the kind constants declared on this class
	 * @param completionOffset original offset of code completion request
	 * @return a new completion proposal
	 */
	public static CompletionProposal create(int kind, int completionOffset) {
		return new CompletionProposal(kind, completionOffset);
	}
	
	/**
	 * Creates a basic completion proposal. All instance
	 * field have plausible default values unless otherwise noted.
	 * <p>
	 * Note that the constructors for this class are internal to the
	 * Java model implementation. Clients cannot directly create
	 * CompletionProposal objects.
	 * </p>
	 * 
	 * @param kind one of the kind constants declared on this class
	 * @param completionLocation original offset of code completion request
	 */
	CompletionProposal(int kind, int completionLocation) {
		if ((kind < CompletionProposal.FIRST_KIND)
				|| (kind > CompletionProposal.LAST_KIND)) {
			throw new IllegalArgumentException();
		}
		if (this.completion == null || completionLocation < 0) {
			// Work around for bug 132558 (https://bugs.eclipse.org/bugs/show_bug.cgi?id=132558).
			// completionLocation can be -1 if the completion occur at the start of a file or
			// the start of a code snippet but this API isn't design to support negative position.
			if(this.completion == null || completionLocation != -1) {
				throw new IllegalArgumentException();
			}
			completionLocation = 0;
		}
		this.completionKind = kind;
		this.completionLocation = completionLocation;
	}
	
	/**
	 * Returns the kind of completion being proposed.
	 * <p>
	 * The set of different kinds of completion proposals is
	 * expected to change over time. It is strongly recommended
	 * that clients do <b>not</b> assume that the kind is one of the
	 * ones they know about, and code defensively for the
	 * possibility of unexpected future growth.
	 * </p>
	 * 
	 * @return the kind; one of the kind constants
	 * declared on this class, or possibly a kind unknown
	 * to the caller
	 */
	public int getKind() {
		return this.completionKind;
	}
	
	/**
	 * Returns the character index in the source file buffer
	 * where source completion was requested (the 
	 * <code>offset</code> parameter to
	 * <code>ICodeAssist.codeComplete</code> minus one).
	 * 
	 * @return character index in source file buffer
	 * @see ICodeAssist#codeComplete(int,CompletionRequestor)
	 */
	// TODO (david) https://bugs.eclipse.org/bugs/show_bug.cgi?id=132558
	public int getCompletionLocation() {
		return this.completionLocation;
	}
	
	/**
	 * Returns the character index of the start of the
	 * subrange in the source file buffer containing the
	 * relevant token being completed. This
	 * token is either the identifier or Java language keyword
	 * under, or immediately preceding, the original request 
	 * offset. If the original request offset is not within
	 * or immediately after an identifier or keyword, then the
	 * position returned is original request offset and the
	 * token range is empty.
	 * 
	 * @return character index of token start position (inclusive)
	 */
	public int getTokenStart() {
		return this.tokenStart;
	}
	
	/**
	 * Returns the character index of the end (exclusive) of the subrange
	 * in the source file buffer containing the
	 * relevant token. When there is no relevant token, the
	 * range is empty
	 * (<code>getEndToken() == getStartToken()</code>).
	 * 
	 * @return character index of token end position (exclusive)
	 */
	public int getTokenEnd() {
		return this.tokenEnd;
	}
	
	/**
	 * Sets the character indices of the subrange in the
	 * source file buffer containing the relevant token being
	 * completed. This token is either the identifier or
	 * Java language keyword under, or immediately preceding,
	 * the original request offset. If the original request
	 * offset is not within or immediately after an identifier
	 * or keyword, then the source range begins at original
	 * request offset and is empty.
	 * <p>
	 * If not set, defaults to empty subrange at [0,0).
	 * </p>
	 * 
	 * @param startIndex character index of token start position (inclusive)
	 * @param endIndex character index of token end position (exclusive)
	 */
	public void setTokenRange(int startIndex, int endIndex) {
		if (startIndex < 0 || endIndex < startIndex) {
			throw new IllegalArgumentException();
		}
		this.tokenStart = startIndex;
		this.tokenEnd = endIndex;
	}
	
	/**
	 * Returns the proposed sequence of characters to insert into the
	 * source file buffer, replacing the characters at the specified
	 * source range. The string can be arbitrary; for example, it might
	 * include not only the name of a method but a set of parentheses.
	 * <p>
	 * The client must not modify the array returned.
	 * </p>
	 * 
	 * @return the completion string
	 */
	public char[] getCompletion() {
		if(this.completionKind == METHOD_DECLARATION) {
			this.findParameterNames(null);
			if(this.updateCompletion) {
				this.updateCompletion = false;
				
				if(this.parameterNames != null) {
					int length = this.parameterNames.length;
					StringBuffer completionBuffer = new StringBuffer(this.completion.length);
						
					int start = 0;
					int end = CharOperation.indexOf('%', this.completion);
	
					completionBuffer.append(this.completion, start, end - start);
					
					for(int i = 0 ; i < length ; i++){
						completionBuffer.append(this.parameterNames[i]);
						start = end + 1;
						end = CharOperation.indexOf('%', this.completion, start);
						if(end > -1){
							completionBuffer.append(this.completion, start, end - start);
						} else {
							completionBuffer.append(this.completion, start, this.completion.length - start);
						}
					}
					int nameLength = completionBuffer.length();
					this.completion = new char[nameLength];
					completionBuffer.getChars(0, nameLength, this.completion, 0);
				}
			}
		}
		return this.completion;
	}
	
	/**
	 * Sets the proposed sequence of characters to insert into the
	 * source file buffer, replacing the characters at the specified
	 * source range. The string can be arbitrary; for example, it might
	 * include not only the name of a method but a set of parentheses.
	 * <p>
	 * If not set, defaults to an empty character array.
	 * </p>
	 * <p>
	 * The completion engine creates instances of this class and sets
	 * its properties; this method is not intended to be used by other clients.
	 * </p>
	 * 
	 * @param completion the completion string
	 */
	public void setCompletion(char[] completion) {
		this.completion = completion;
	}
	
	/**
	 * Returns the character index of the start of the
	 * subrange in the source file buffer to be replaced
	 * by the completion string. If the subrange is empty
	 * (<code>getReplaceEnd() == getReplaceStart()</code>),
	 * the completion string is to be inserted at this
	 * index.
	 * <p>
	 * Note that while the token subrange is precisely 
	 * specified, the replacement range is loosely
	 * constrained and may not bear any direct relation
	 * to the original request offset. For example,
	 * it would be possible for a type completion to 
	 * propose inserting an import declaration at the
	 * top of the compilation unit; or the completion
	 * might include trailing parentheses and
	 * punctuation for a method completion.
	 * </p>
	 * 
	 * @return replacement start position (inclusive)
	 */
	public int getReplaceStart() {
		return this.replaceStart;
	}
	
	/**
	 * Returns the character index of the end of the
	 * subrange in the source file buffer to be replaced
	 * by the completion string. If the subrange is empty
	 * (<code>getReplaceEnd() == getReplaceStart()</code>),
	 * the completion string is to be inserted at this
	 * index.
	 * 
	 * @return replacement end position (exclusive)
	 */
	public int getReplaceEnd() {
		return this.replaceEnd;
	}
	
	/**
	 * Sets the character indices of the subrange in the
	 * source file buffer to be replaced by the completion
	 * string. If the subrange is empty
	 * (<code>startIndex == endIndex</code>),
	 * the completion string is to be inserted at this
	 * index.
	 * <p>
	 * If not set, defaults to empty subrange at [0,0).
	 * </p>
	 * <p>
	 * The completion engine creates instances of this class and sets
	 * its properties; this method is not intended to be used by other clients.
	 * </p>
	 * 
	 * @param startIndex character index of replacement start position (inclusive)
	 * @param endIndex character index of replacement end position (exclusive)
	 */
	public void setReplaceRange(int startIndex, int endIndex) {
		if (startIndex < 0 || endIndex < startIndex) {
			throw new IllegalArgumentException();
		}
		this.replaceStart = startIndex;
		this.replaceEnd = endIndex;
	}
	
	/**
	 * Returns the relative relevance rating of this proposal.
	 * 
	 * @return relevance rating of this proposal; ratings are positive; higher means better
	 */
	public int getRelevance() {
		return this.relevance;
	}
	
	/**
	 * Sets the relative relevance rating of this proposal.
	 * <p>
	 * If not set, defaults to the lowest possible rating (1).
	 * </p>
	 * <p>
	 * The completion engine creates instances of this class and sets
	 * its properties; this method is not intended to be used by other clients.
	 * </p>
	 * 
	 * @param rating relevance rating of this proposal; ratings are positive; higher means better
	 */
	public void setRelevance(int rating) {
		if (rating <= 0) {
			throw new IllegalArgumentException();
		}
		this.relevance = rating;
	}
	
	/**
	 * Returns the type signature or package name of the relevant
	 * declaration in the context, or <code>null</code> if none.
	 * <p>
	 * This field is available for the following kinds of
	 * completion proposals:
	 * <ul>
	 *  <li><code>ANNOTATION_ATTRIBUT_REF</code> - type signature
	 * of the annotation that declares the attribute that is referenced</li>
	 * <li><code>ANONYMOUS_CLASS_DECLARATION</code> - type signature
	 * of the type that is being subclassed or implemented</li>
	 * 	<li><code>FIELD_REF</code> - type signature
	 * of the type that declares the field that is referenced</li>
	 * 	<li><code>METHOD_REF</code> - type signature
	 * of the type that declares the method that is referenced</li>
	 * 	<li><code>METHOD_DECLARATION</code> - type signature
	 * of the type that declares the method that is being
	 * implemented or overridden</li>
	 * 	<li><code>PACKAGE_REF</code> - dot-based package 
	 * name of the package that is referenced</li>
	 * 	<li><code>TYPE_REF</code> - dot-based package 
	 * name of the package containing the type that is referenced</li>
	 *  <li><code>POTENTIAL_METHOD_DECLARATION</code> - type signature
	 * of the type that declares the method that is being created</li>
	 * </ul>
	 * For kinds of completion proposals, this method returns
	 * <code>null</code>. Clients must not modify the array
	 * returned.
	 * </p>
	 * 
	 * @return a type signature or a package name (depending
	 * on the kind of completion), or <code>null</code> if none
	 * @see Signature
	 */
	public char[] getDeclarationSignature() {
		return this.declarationSignature;
	}
	
	/**
	 * Returns the key of the relevant
	 * declaration in the context, or <code>null</code> if none.
	 * <p>
	 * This field is available for the following kinds of
	 * completion proposals:
	 * <ul>
	 * <li><code>ANONYMOUS_CLASS_DECLARATION</code> - key
	 * of the type that is being subclassed or implemented</li>
	 * 	<li><code>METHOD_DECLARATION</code> - key
	 * of the type that declares the method that is being
	 * implemented or overridden</li>
	 * </ul>
	 * For kinds of completion proposals, this method returns
	 * <code>null</code>. Clients must not modify the array
	 * returned.
	 * </p>
	 * 
	 * @return a key, or <code>null</code> if none
	 * @see descent.core.dom.ASTParser#createASTs(ICompilationUnit[], String[], descent.core.dom.ASTRequestor, IProgressMonitor)
     * @since 3.1
	 */
	public char[] getDeclarationKey() {
		return this.declarationKey;
	}
	
	/**
	 * Sets the type or package signature of the relevant
	 * declaration in the context, or <code>null</code> if none.
	 * <p>
	 * If not set, defaults to none.
	 * </p>
	 * <p>
	 * The completion engine creates instances of this class and sets
	 * its properties; this method is not intended to be used by other clients.
	 * </p>
	 * 
	 * @param signature the type or package signature, or
	 * <code>null</code> if none
	 */
	public void setDeclarationSignature(char[] signature) {
		this.declarationSignature = signature;
	}
	
	/**
	 * Sets the type or package key of the relevant
	 * declaration in the context, or <code>null</code> if none.
	 * <p>
	 * If not set, defaults to none.
	 * </p>
	 * <p>
	 * The completion engine creates instances of this class and sets
	 * its properties; this method is not intended to be used by other clients.
	 * </p>
	 * 
	 * @param key the type or package key, or
	 * <code>null</code> if none
     * @since 3.1
	 */
	public void setDeclarationKey(char[] key) {
		this.declarationKey = key;
	}
	
	/**
	 * Returns the simple name of the method, field,
	 * member, or variable relevant in the context, or
	 * <code>null</code> if none.
	 * <p>
	 * This field is available for the following kinds of
	 * completion proposals:
	 * <ul>
	 *  <li><code>ANNOTATION_ATTRIBUT_REF</code> - the name of the attribute</li>
	 * 	<li><code>FIELD_REF</code> - the name of the field</li>
	 * 	<li><code>KEYWORD</code> - the keyword</li>
	 * 	<li><code>LABEL_REF</code> - the name of the label</li>
	 * 	<li><code>LOCAL_VARIABLE_REF</code> - the name of the local variable</li>
	 * 	<li><code>METHOD_REF</code> - the name of the method (the type simple name for constructor)</li>
	 * 	<li><code>METHOD_DECLARATION</code> - the name of the method (the type simple name for constructor)</li>
	 * 	<li><code>VARIABLE_DECLARATION</code> - the name of the variable</li>
	 *  <li><code>POTENTIAL_METHOD_DECLARATION</code> - the name of the method</li>
	 * </ul>
	 * For kinds of completion proposals, this method returns
	 * <code>null</code>. Clients must not modify the array
	 * returned.
	 * </p>
	 * 
	 * @return the keyword, field, method, local variable, or member
	 * name, or <code>null</code> if none
	 */
	public char[] getName() {
		return this.name;
	}
	
	
	/**
	 * Sets the simple name of the method (type simple name for constructor), field,
	 * member, or variable relevant in the context, or
	 * <code>null</code> if none.
	 * <p>
	 * If not set, defaults to none.
	 * </p>
	 * <p>
	 * The completion engine creates instances of this class and sets
	 * its properties; this method is not intended to be used by other clients.
	 * </p>
	 * 
	 * @param name the keyword, field, method, local variable,
	 * or member name, or <code>null</code> if none
	 */
	public void setName(char[] name) {
		this.name = name;
	}
	
	/**
	 * Returns the signature of the method or type
	 * relevant in the context, or <code>null</code> if none.
	 * <p>
	 * This field is available for the following kinds of
	 * completion proposals:
	 * <ul>
	 * <li><code>ANNOTATION_ATTRIBUT_REF</code> - the type signature
	 * of the referenced attribute's type</li>
	 * <li><code>ANONYMOUS_CLASS_DECLARATION</code> - method signature
	 * of the constructor that is being invoked</li>
	 * 	<li><code>FIELD_REF</code> - the type signature
	 * of the referenced field's type</li>
	 * 	<li><code>LOCAL_VARIABLE_REF</code> - the type signature
	 * of the referenced local variable's type</li>
	 * 	<li><code>METHOD_REF</code> - method signature
	 * of the method that is referenced</li>
	 * 	<li><code>METHOD_DECLARATION</code> - method signature
	 * of the method that is being implemented or overridden</li>
	 * 	<li><code>TYPE_REF</code> - type signature
	 * of the type that is referenced</li>
	 * 	<li><code>VARIABLE_DECLARATION</code> - the type signature
	 * of the type of the variable being declared</li>
	 *  <li><code>POTENTIAL_METHOD_DECLARATION</code> - method signature
	 * of the method that is being created</li>
	 * </ul>
	 * For kinds of completion proposals, this method returns
	 * <code>null</code>. Clients must not modify the array
	 * returned.
	 * </p>
	 * 
	 * @return the signature, or <code>null</code> if none
	 * @see Signature
	 */
	public char[] getSignature() {
		return this.signature;
	}
	
	@Override
	public char[] getTypeSignature() {
		return this.typeSignature;
	}
	
	/**
	 * Returns the key relevant in the context,
	 * or <code>null</code> if none.
	 * <p>
	 * This field is available for the following kinds of
	 * completion proposals:
	 * <ul>
	 * <li><code>ANONYMOUS_CLASS_DECLARATION</code> - method key
	 * of the constructor that is being invoked, or <code>null</code> if
	 * the declaring type is an interface</li>
	 * 	<li><code>METHOD_DECLARATION</code> - method key
	 * of the method that is being implemented or overridden</li>
	 * </ul>
	 * For kinds of completion proposals, this method returns
	 * <code>null</code>. Clients must not modify the array
	 * returned.
	 * </p>
	 * 
	 * @return the key, or <code>null</code> if none
	 * @see descent.core.dom.ASTParser#createASTs(ICompilationUnit[], String[], descent.core.dom.ASTRequestor, IProgressMonitor)
     * @since 3.1
	 */
	public char[] getKey() {
		return this.key;
	}
	
//	/**
//	 * Returns the package name of the relevant
//	 * declaration in the context, or <code>null</code> if none.
//	 * <p>
//	 * This field is available for the following kinds of
//	 * completion proposals:
//	 * <ul>
//	 * <li><code>ANONYMOUS_CLASS_DECLARATION</code> - the dot-based package name
//	 * of the type that is being subclassed or implemented</li>
//	 * 	<li><code>FIELD_REF</code> - the dot-based package name
//	 * of the type that declares the field that is referenced</li>
//	 * 	<li><code>METHOD_REF</code> - the dot-based package name
//	 * of the type that declares the method that is referenced</li>
//	 * 	<li><code>METHOD_DECLARATION</code> - the dot-based package name
//	 * of the type that declares the method that is being
//	 * implemented or overridden</li>
//	 * </ul>
//	 * For kinds of completion proposals, this method returns
//	 * <code>null</code>. Clients must not modify the array
//	 * returned.
//	 * </p>
//	 * 
//	 * @return the dot-based package name, or
//	 * <code>null</code> if none
//	 * @see #getDeclarationSignature()
//	 * @see #getSignature()
//	 * 
//	 * @since 3.1
//	 */
//	public char[] getDeclarationPackageName() {
//		return this.declarationPackageName;
//	}
//	
//	/**
//	 * Returns the type name of the relevant
//	 * declaration in the context without the package fragment,
//	 * or <code>null</code> if none. 
//	 * <p>
//	 * This field is available for the following kinds of
//	 * completion proposals:
//	 * <ul>
//	 * <li><code>ANONYMOUS_CLASS_DECLARATION</code> - the dot-based type name
//	 * of the type that is being subclassed or implemented</li>
//	 * 	<li><code>FIELD_REF</code> - the dot-based type name
//	 * of the type that declares the field that is referenced
//	 * or an anonymous type instanciation ("new X(){}") if it is an anonymous type</li>
//	 * 	<li><code>METHOD_REF</code> - the dot-based type name
//	 * of the type that declares the method that is referenced
//	 * or an anonymous type instanciation ("new X(){}") if it is an anonymous type</li>
//	 * 	<li><code>METHOD_DECLARATION</code> - the dot-based type name
//	 * of the type that declares the method that is being
//	 * implemented or overridden</li>
//	 * </ul>
//	 * For kinds of completion proposals, this method returns
//	 * <code>null</code>. Clients must not modify the array
//	 * returned.
//	 * </p>
//	 * 
//	 * @return the dot-based package name, or
//	 * <code>null</code> if none
//	 * @see #getDeclarationSignature()
//	 * @see #getSignature()
//	 * 
//	 * @since 3.1
//	 */
//	public char[] getDeclarationTypeName() {
//		return this.declarationTypeName;
//	}
//	
//	/**
//	 * Returns the package name of the method or type
//	 * relevant in the context, or <code>null</code> if none.
//	 * <p>
//	 * This field is available for the following kinds of
//	 * completion proposals:
//	 * <ul>
//	 * 	<li><code>FIELD_REF</code> - the dot-based package name
//	 * of the referenced field's type</li>
//	 * 	<li><code>LOCAL_VARIABLE_REF</code> - the dot-based package name
//	 * of the referenced local variable's type</li>
//	 * 	<li><code>METHOD_REF</code> -  the dot-based package name
//	 * of the return type of the method that is referenced</li>
//	 * 	<li><code>METHOD_DECLARATION</code> - the dot-based package name
//	 * of the return type of the method that is being implemented
//	 * or overridden</li>
//	 * 	<li><code>PACKAGE_REF</code> - the dot-based package name
//	 * of the package that is referenced</li>
//	 * 	<li><code>TYPE_REF</code> - the dot-based package name
//	 * of the type that is referenced</li>
//	 * 	<li><code>VARIABLE_DECLARATION</code> - the dot-based package name
//	 * of the type of the variable being declared</li>
//	 * </ul>
//	 * For kinds of completion proposals, this method returns
//	 * <code>null</code>. Clients must not modify the array
//	 * returned.
//	 * </p>
//	 * 
//	 * @return the package name, or <code>null</code> if none
//	 * 
//	 * @see #getDeclarationSignature()
//	 * @see #getSignature()
//	 * 
//	 * @since 3.1
//	 */
//	public char[] getPackageName() {
//		return this.packageName;
//	}
//	
//	/**
//	 * Returns the type name without the package fragment of the method or type
//	 * relevant in the context, or <code>null</code> if none.
//	 * <p>
//	 * This field is available for the following kinds of
//	 * completion proposals:
//	 * <ul>
//	 * 	<li><code>FIELD_REF</code> - the dot-based type name
//	 * of the referenced field's type</li>
//	 * 	<li><code>LOCAL_VARIABLE_REF</code> - the dot-based type name
//	 * of the referenced local variable's type</li>
//	 * 	<li><code>METHOD_REF</code> -  the dot-based type name
//	 * of the return type of the method that is referenced</li>
//	 * 	<li><code>METHOD_DECLARATION</code> - the dot-based type name
//	 * of the return type of the method that is being implemented
//	 * or overridden</li>
//	 * 	<li><code>TYPE_REF</code> - the dot-based type name
//	 * of the type that is referenced</li>
//	 * 	<li><code>VARIABLE_DECLARATION</code> - the dot-based package name
//	 * of the type of the variable being declared</li>
//	 * </ul>
//	 * For kinds of completion proposals, this method returns
//	 * <code>null</code>. Clients must not modify the array
//	 * returned.
//	 * </p>
//	 * 
//	 * @return the package name, or <code>null</code> if none
//	 * 
//	 * @see #getDeclarationSignature()
//	 * @see #getSignature()
//	 * 
//	 * @since 3.1
//	 */
//	public char[] getTypeName() {
//		return this.typeName;
//	}
//	
//	/**
//	 * Returns the parameter package names of the method 
//	 * relevant in the context, or <code>null</code> if none.
//	 * <p>
//	 * This field is available for the following kinds of
//	 * completion proposals:
//	 * <ul>
//	 * 	<li><code>ANONYMOUS_CLASS_DECLARATION</code> - parameter package names
//	 * of the constructor that is being invoked</li>
//	 * 	<li><code>METHOD_REF</code> - parameter package names
//	 * of the method that is referenced</li>
//	 * 	<li><code>METHOD_DECLARATION</code> - parameter package names
//	 * of the method that is being implemented or overridden</li>
//	 * </ul>
//	 * For kinds of completion proposals, this method returns
//	 * <code>null</code>. Clients must not modify the array
//	 * returned.
//	 * </p>
//	 * 
//	 * @return the package name, or <code>null</code> if none
//	 * 
//	 * @see #getDeclarationSignature()
//	 * @see #getSignature()
//	 * 
//	 * @since 3.1
//	 */
//	public char[][] getParameterPackageNames() {
//		return this.parameterPackageNames;
//	}
//	
//	/**
//	 * Returns the parameter type names without teh package fragment of
//	 * the method relevant in the context, or <code>null</code> if none.
//	 * <p>
//	 * This field is available for the following kinds of
//	 * completion proposals:
//	 * <ul>
//	 * 	<li><code>ANONYMOUS_CLASS_DECLARATION</code> - parameter type names
//	 * of the constructor that is being invoked</li>
//	 * 	<li><code>METHOD_REF</code> - parameter type names
//	 * of the method that is referenced</li>
//	 * 	<li><code>METHOD_DECLARATION</code> - parameter type names
//	 * of the method that is being implemented or overridden</li>
//	 * </ul>
//	 * For kinds of completion proposals, this method returns
//	 * <code>null</code>. Clients must not modify the array
//	 * returned.
//	 * </p>
//	 * 
//	 * @return the package name, or <code>null</code> if none
//	 * 
//	 * @see #getDeclarationSignature()
//	 * @see #getSignature()
//	 * 
//	 * @since 3.1
//	 */
//	public char[][] getParameterTypeNames() {
//		return this.parameterTypeNames;
//	}
	
	/**
	 * Sets the signature of the method, field type, member type,
	 * relevant in the context, or <code>null</code> if none.
	 * <p>
	 * If not set, defaults to none.
	 * </p>
	 * <p>
	 * The completion engine creates instances of this class and sets
	 * its properties; this method is not intended to be used by other clients.
	 * </p>
	 * 
	 * @param signature the signature, or <code>null</code> if none
	 */
	public void setSignature(char[] signature) {
		this.signature = signature;
	}
	
	/**
	 * Sets the key of the method, field type, member type,
	 * relevant in the context, or <code>null</code> if none.
	 * <p>
	 * If not set, defaults to none.
	 * </p>
	 * <p>
	 * The completion engine creates instances of this class and sets
	 * its properties; this method is not intended to be used by other clients.
	 * </p>
	 * 
	 * @param key the key, or <code>null</code> if none
     * @since 3.1
	 */
	public void setKey(char[] key) {
		this.key = key;
	}
	
	/**
	 * Returns the modifier flags relevant in the context, or
	 * <code>Flags.AccDefault</code> if none.
	 * <p>
	 * This field is available for the following kinds of
	 * completion proposals:
	 * <ul>
	 * <li><code>ANNOTATION_ATTRIBUT_REF</code> - modifier flags
	 * of the attribute that is referenced; 
	 * <li><code>ANONYMOUS_CLASS_DECLARATION</code> - modifier flags
	 * of the constructor that is referenced</li>
	 * 	<li><code>FIELD_REF</code> - modifier flags
	 * of the field that is referenced; 
	 * <code>Flags.AccEnum</code> can be used to recognize
	 * references to enum constants
	 * </li>
	 * 	<li><code>KEYWORD</code> - modifier flag
	 * corrresponding to the modifier keyword</li>
	 * 	<li><code>LOCAL_VARIABLE_REF</code> - modifier flags
	 * of the local variable that is referenced</li>
	 * 	<li><code>METHOD_REF</code> - modifier flags
	 * of the method that is referenced;
	 * <code>Flags.AccAnnotation</code> can be used to recognize
	 * references to annotation type members
	 * </li>
	 * 	<li><code>METHOD_DECLARATION</code> - modifier flags
	 * for the method that is being implemented or overridden</li>
	 * 	<li><code>TYPE_REF</code> - modifier flags
	 * of the type that is referenced; <code>Flags.AccInterface</code>
	 * can be used to recognize references to interfaces, 
	 * <code>Flags.AccEnum</code> enum types,
	 * and <code>Flags.AccAnnotation</code> annotation types
	 * </li>
	 * 	<li><code>VARIABLE_DECLARATION</code> - modifier flags
	 * for the variable being declared</li>
	 * 	<li><code>POTENTIAL_METHOD_DECLARATION</code> - modifier flags
	 * for the method that is being created</li>
	 * </ul>
	 * For other kinds of completion proposals, this method returns
	 * <code>Flags.AccDefault</code>.
	 * </p>
	 * 
	 * @return the modifier flags, or
	 * <code>Flags.AccDefault</code> if none
	 * @see Flags
	 */
	public long getFlags() {
		return this.flags;
	}
	
	/**
	 * Sets the modifier flags relevant in the context.
	 * <p>
	 * If not set, defaults to none.
	 * </p>
	 * <p>
	 * The completion engine creates instances of this class and sets
	 * its properties; this method is not intended to be used by other clients.
	 * </p>
	 * 
	 * @param flags the modifier flags, or
	 * <code>Flags.AccDefault</code> if none
	 */
	public void setFlags(long flags) {
		this.flags = flags;
	}
	
	/**
	 * Finds the method parameter names.
	 * This information is relevant to method reference (and
	 * method declaration proposals). Returns <code>null</code>
	 * if not available or not relevant.
	 * <p>
	 * The client must not modify the array returned.
	 * </p>
	 * <p>
	 * <b>Note that this is an expensive thing to compute, which may require
	 * parsing Java source files, etc. Use sparingly.</b>
	 * </p>
	 * 
	 * @param monitor the progress monitor, or <code>null</code> if none
	 * @return the parameter names, or <code>null</code> if none
	 * or not available or not relevant
	 */
	public char[][] findParameterNames(IProgressMonitor monitor) {
		if (!this.parameterNamesComputed) {
			this.parameterNamesComputed = true;
			
			if (node != null) {
				FuncDeclaration func;
				if (node instanceof FuncDeclaration) {
					func = (FuncDeclaration) node;
				} else if (node instanceof TemplateDeclaration) {
					func = (FuncDeclaration) ((TemplateDeclaration) node).members.get(0);
				} else {
					return this.parameterNames;
				}
				
				TypeFunction tf = (TypeFunction) func.type;
				final int size = ASTDmdNode.size(tf.parameters);
				this.parameterNames = new char[size][];
				for (int i = 0; i < size; i++) {
					IdentifierExp ident = tf.parameters.get(i).ident;
					this.parameterNames[i] = ident == null || ident.ident == null ? ("arg" + (i + 1)).toCharArray() : ident.ident;
				}
			} else if (declarationStart != -1 && packageName != null) {
				IJavaElement element = getJavaElement();
				if (element instanceof IMethod) {
					IMethod method = (IMethod) element;
					String[] paramNames;
					try {
						paramNames = method.getParameterNames();

						this.parameterNames = new char[paramNames.length][];
						for (int i = 0; i < paramNames.length; i++) {
							if (paramNames[i].length() == 0) {
								this.parameterNames[i] = ("arg" + i).toCharArray();
							} else {
								this.parameterNames[i] = paramNames[i]
										.toCharArray();
							}
						}
					} catch (JavaModelException e) {
						this.parameterNames = CharOperation.NO_CHAR_CHAR;
					}
				} else {
					getJavaElement();
					this.parameterNames = CharOperation.NO_CHAR_CHAR;
				}
			} else {
				this.parameterNames = CharOperation.NO_CHAR_CHAR;
			}
		}
		return this.parameterNames;
	}
	
	/**
	 * Finds the method default values names.
	 * This information is relevant to method reference (and
	 * method declaration proposals). Returns <code>null</code>
	 * if not available or not relevant.
	 * <p>
	 * The client must not modify the array returned.
	 * </p>
	 * <p>
	 * <b>Note that this is an expensive thing to compute, which may require
	 * parsing Java source files, etc. Use sparingly.</b>
	 * </p>
	 * 
	 * @param monitor the progress monitor, or <code>null</code> if none
	 * @return the default values, or <code>null</code> if none
	 * or not available or not relevant
	 */
	public char[][] findParameterDefaultValues(IProgressMonitor monitor) {
		if (!this.parameterDefaultValuesComputed) {
			this.parameterDefaultValuesComputed = true;
			
			IJavaElement element = getJavaElement();
			if (element instanceof IMethod) {
				IMethod method = (IMethod) element;
				String[] paramDefaultValues;
				try {
					paramDefaultValues = method.getParameterDefaultValues();

					if (paramDefaultValues == null) {
						this.parameterDefaultValues = CharOperation.NO_CHAR_CHAR;
					} else {
						this.parameterDefaultValues = new char[paramDefaultValues.length][];
						for (int i = 0; i < paramDefaultValues.length; i++) {
							if (paramDefaultValues[i] != null) {
								this.parameterDefaultValues[i] = paramDefaultValues[i]
										.toCharArray();
							}
						}
					}
				} catch (JavaModelException e) {
					this.parameterDefaultValues = CharOperation.NO_CHAR_CHAR;
				}
			} else {
				getJavaElement();
				this.parameterDefaultValues = CharOperation.NO_CHAR_CHAR;
			}
		}
		return this.parameterDefaultValues;
	}
	
	public IJavaElement getJavaElement() {
		if (node instanceof Dsymbol) {
			Dsymbol sym = (Dsymbol) node;
			if (sym.getJavaElement() != null) {
				return (IJavaElement) sym.getJavaElement();
			} else {
				Module mod = sym.getModule();
				ICompilationUnit unit = completionEngine.internalSignature.getCompilationUnit(mod.moduleName);
				if (unit != null) {
					try {
						return completionEngine.internalSignature.binarySearch(unit, node.start, node.start + node.length);
					} catch (JavaModelException e) {
						Util.log(e);
					}
				}
			}
		} else if (packageName != null && declarationStart != -1) {
			ICompilationUnit cu = completionEngine.internalSignature.getCompilationUnit(new String(packageName));
			if (cu != null) {
				try {
					return completionEngine.internalSignature.binarySearch(cu, declarationStart, declarationStart);
				} catch (JavaModelException e) {
					Util.log(e);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Finds the template parameter names.
	 * This information is relevant to template reference (and
	 * template declaration proposals). Returns <code>null</code>
	 * if not available or not relevant.
	 * <p>
	 * The client must not modify the array returned.
	 * </p>
	 * <p>
	 * <b>Note that this is an expensive thing to compute, which may require
	 * parsing Java source files, etc. Use sparingly.</b>
	 * </p>
	 * 
	 * @param monitor the progress monitor, or <code>null</code> if none
	 * @return the parameter names, or <code>null</code> if none
	 * or not available or not relevant
	 */
	public char[][] findTemplateParameterNames(IProgressMonitor monitor) {
		if (!this.templateParameterNamesComputed) {
			this.templateParameterNamesComputed = true;
			
			if (node != null && node instanceof TemplateDeclaration) {
				TemplateDeclaration temp = (TemplateDeclaration) node;
				
				this.templateParameterNames = new char[temp.parameters.size()][];
				for (int i = 0; i < temp.parameters.size(); i++) {
					IdentifierExp ident = temp.parameters.get(i).ident;
					this.templateParameterNames[i] = ident == null || ident.ident == null ? ("arg" + (i + 1)).toCharArray() : ident.ident;
				}
			} else {
				IJavaElement elem = getJavaElement();
				if (elem != null && elem instanceof ITemplated) {
					ITemplated templated = (ITemplated) elem;
					try {
						ITypeParameter[] params = templated.getTypeParameters();
						templateParameterNames = new char[params.length][];
						for (int i = 0; i < templateParameterNames.length; i++) {
							templateParameterNames[i] = params[i].getElementName().toCharArray();
						}
					} catch (JavaModelException e) {
						this.templateParameterNames = CharOperation.NO_CHAR_CHAR;	
					}
				} else {
					this.templateParameterNames = CharOperation.NO_CHAR_CHAR;
				}}
			
		}
		return this.templateParameterNames;
	}
	
	/**
	 * Finds the template parameter names.
	 * This information is relevant to template reference (and
	 * template declaration proposals). Returns <code>null</code>
	 * if not available or not relevant.
	 * <p>
	 * The client must not modify the array returned.
	 * </p>
	 * <p>
	 * <b>Note that this is an expensive thing to compute, which may require
	 * parsing Java source files, etc. Use sparingly.</b>
	 * </p>
	 * 
	 * @param monitor the progress monitor, or <code>null</code> if none
	 * @return the parameter names, or <code>null</code> if none
	 * or not available or not relevant
	 */
	public char[][] findTemplateParameterDefaultValues(IProgressMonitor monitor) {
		if (!this.templateParameterDefaultValuesComputed) {
			this.templateParameterDefaultValuesComputed = true;
			
			IJavaElement elem = getJavaElement();
			if (elem != null && elem instanceof ITemplated) {
				ITemplated templated = (ITemplated) elem;
				try {
					ITypeParameter[] params = templated.getTypeParameters();
					templateParameterDefaultValues = new char[params.length][];
					for (int i = 0; i < templateParameterDefaultValues.length; i++) {
						if (params[i].getDefaultValue() != null) {
							templateParameterDefaultValues[i] = params[i].getDefaultValue().toCharArray();
						}
					}
				} catch (JavaModelException e) {
					this.templateParameterDefaultValues = CharOperation.NO_CHAR_CHAR;	
				}
			} else {
				this.templateParameterDefaultValues = CharOperation.NO_CHAR_CHAR;
			}
		}
		return this.templateParameterDefaultValues;
	}

	/**
	 * Sets the method parameter names.
	 * This information is relevant to method reference (and
	 * method declaration proposals).
	 * <p>
	 * The completion engine creates instances of this class and sets
	 * its properties; this method is not intended to be used by other clients.
	 * </p>
	 * 
	 * @param parameterNames the parameter names, or <code>null</code> if none
	 */
	public void setParameterNames(char[][] parameterNames) {
		this.parameterNames = parameterNames;
		this.parameterNamesComputed = true;
	}
	
	/**
	 * Returns the accessibility of the proposal.
	 * <p>
	 * This field is available for the following kinds of
	 * completion proposals:
	 * <ul>
	 * 	<li><code>TYPE_REF</code> - accessibility of the type</li>
	 * </ul>
	 * For these kinds of completion proposals, this method returns
	 * {@link IAccessRule#K_ACCESSIBLE} or {@link IAccessRule#K_DISCOURAGED}
	 * or {@link IAccessRule#K_NON_ACCESSIBLE}.
	 * By default this method return {@link IAccessRule#K_ACCESSIBLE}.
	 * </p>
	 * 
	 * @see IAccessRule
	 * 
	 * @return the accessibility of the proposal
	 * 
	 * @since 3.1
	 */
	public int getAccessibility() {
		return this.accessibility;
	}
	
	/**
	 * Returns whether this proposal is a constructor.
	 * <p>
	 * This field is available for the following kinds of
	 * completion proposals:
	 * <ul>
	 * <li><code>METHOD_REF</code> - return <code>true</code>
	 * if the referenced method is a constructor</li>
	 * 	<li><code>METHOD_DECLARATION</code> - return <code>true</code>
	 * if the declared method is a constructor</li>
	 * </ul>
	 * For kinds of completion proposals, this method returns
	 * <code>false</code>.
	 * </p>
	 * 
	 * @return <code>true</code> if the proposal is a constructor.
	 * @since 3.1
	 */
	public boolean isConstructor() {
		return this.isConstructor;
	}
	
	public boolean isAlias() {
		return isAlias;
	}
	
	/**
	 * Returns whether the proposal is intended to be written with
	 * arguments or just the name of the proposal.
	 */
	@Override
	public boolean wantArguments() {
		return super.wantArguments();
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append('[');
		switch(this.completionKind) {
			case CompletionProposal.ANONYMOUS_CLASS_DECLARATION :
				buffer.append("ANONYMOUS_CLASS_DECLARATION"); //$NON-NLS-1$
				break;
			case CompletionProposal.FIELD_REF :
				buffer.append("FIELD_REF"); //$NON-NLS-1$
				break;
			case CompletionProposal.KEYWORD :
				buffer.append("KEYWORD"); //$NON-NLS-1$
				break;
			case CompletionProposal.LABEL_REF :
				buffer.append("LABEL_REF"); //$NON-NLS-1$
				break;
			case CompletionProposal.LOCAL_VARIABLE_REF :
				buffer.append("LOCAL_VARIABLE_REF"); //$NON-NLS-1$
				break;
			case CompletionProposal.METHOD_DECLARATION :
				buffer.append("METHOD_DECLARATION"); //$NON-NLS-1$
				if(this.isConstructor) {
					buffer.append("<CONSTRUCTOR>"); //$NON-NLS-1$
				}
				break;
			case CompletionProposal.METHOD_REF :
				buffer.append("METHOD_REF"); //$NON-NLS-1$
				if(this.isConstructor) {
					buffer.append("<CONSTRUCTOR>"); //$NON-NLS-1$
				}
				break;
			case CompletionProposal.COMPILATION_UNIT_REF :
				buffer.append("PACKAGE_REF"); //$NON-NLS-1$
				break;
			case CompletionProposal.TYPE_REF :
				buffer.append("TYPE_REF"); //$NON-NLS-1$
				break;
			case CompletionProposal.VARIABLE_DECLARATION :
				buffer.append("VARIABLE_DECLARATION"); //$NON-NLS-1$
				break;
			case CompletionProposal.POTENTIAL_METHOD_DECLARATION :
				buffer.append("POTENTIAL_METHOD_DECLARATION"); //$NON-NLS-1$
				break;
			case CompletionProposal.METHOD_NAME_REFERENCE :
				buffer.append("METHOD_IMPORT"); //$NON-NLS-1$
				break;
			case CompletionProposal.ANNOTATION_ATTRIBUTE_REF :
				buffer.append("ANNOTATION_ATTRIBUTE_REF"); //$NON-NLS-1$
				break;
			case CompletionProposal.JAVADOC_BLOCK_TAG :
				buffer.append("JAVADOC_BLOCK_TAG"); //$NON-NLS-1$
				break;
			case CompletionProposal.JAVADOC_INLINE_TAG :
				buffer.append("JAVADOC_INLINE_TAG"); //$NON-NLS-1$
				break;
			case CompletionProposal.JAVADOC_FIELD_REF:
				buffer.append("JAVADOC_FIELD_REF"); //$NON-NLS-1$
				break;
			case CompletionProposal.JAVADOC_METHOD_REF :
				buffer.append("JAVADOC_METHOD_REF"); //$NON-NLS-1$
				break;
			case CompletionProposal.JAVADOC_TYPE_REF :
				buffer.append("JAVADOC_TYPE_REF"); //$NON-NLS-1$
				break;
			case CompletionProposal.JAVADOC_PARAM_REF :
				buffer.append("JAVADOC_PARAM_REF"); //$NON-NLS-1$
				break;
			case CompletionProposal.JAVADOC_VALUE_REF :
				buffer.append("JAVADOC_VALUE_REF"); //$NON-NLS-1$
				break;
			case CompletionProposal.VERSION_REF:
				buffer.append("VERSION_REF"); //$NON-NLS-1$
				break;
			case CompletionProposal.DDOC_MACRO:
				buffer.append("DDOC_MACRO"); //$NON-NLS-1$
				break;
			default :
				buffer.append("PROPOSAL"); //$NON-NLS-1$
				break;
				
		}
		buffer.append("]{completion:"); //$NON-NLS-1$
		if (this.completion != null) buffer.append(this.completion);
		buffer.append(", declSign:"); //$NON-NLS-1$
		if (this.declarationSignature != null) buffer.append(this.declarationSignature);  
		buffer.append(", sign:"); //$NON-NLS-1$
		if (this.signature != null) buffer.append(this.signature);
		buffer.append(", declKey:"); //$NON-NLS-1$
		if (this.declarationKey != null) buffer.append(this.declarationKey);
		buffer.append(", key:"); //$NON-NLS-1$
		if (this.key != null) buffer.append(key);
		buffer.append(", name:"); //$NON-NLS-1$
		if (this.name != null) buffer.append(this.name);
		buffer.append(", ["); //$NON-NLS-1$
		buffer.append(this.replaceStart);
		buffer.append(',');
		buffer.append(this.replaceEnd);
		buffer.append("], relevance="); //$NON-NLS-1$
		buffer.append(this.relevance);
		buffer.append('}');
		return buffer.toString();
	}
}