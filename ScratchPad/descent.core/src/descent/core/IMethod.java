/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Corporation - added J2SE 1.5 support
 *******************************************************************************/
package descent.core;

import descent.internal.compiler.IMethodVarArgsConstants;

/**
 * Represents a method (or constructor) declared in a type.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IMethod extends IMember, ITemplated, IMethodVarArgsConstants, IMethod__Marker {
	

/**
 * Returns the simple name of this method.
 * For a constructor, this returns the simple name of the declaring type.
 * Note: This holds whether the constructor appears in a source or binary type
 * (even though class files internally define constructor names to be <code>"&lt;init&gt;"</code>).
 * For the class initialization methods in binary types, this returns
 * the special name <code>"&lt;clinit&gt;"</code>.
 * This is a handle-only method.
 * @return the simple name of this method
 */
String getElementName();
/**
 * Returns the type signatures of the exceptions this method throws,
 * in the order declared in the source. Returns an empty array
 * if this method throws no exceptions.
 * <p>
 * For example, a source method declaring <code>"throws IOException"</code>,
 * would return the array <code>{"QIOException;"}</code>.
 * </p>
 * <p>
 * The type signatures may be either unresolved (for source types)
 * or resolved (for binary types), and either basic (for basic types)
 * or rich (for parameterized types). See {@link Signature} for details.
 * </p>
 *
 * @exception JavaModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource.
 * @return the type signatures of the exceptions this method throws,
 * in the order declared in the source, an empty array if this method throws no exceptions
 * @see Signature
 */
String[] getExceptionTypes() throws JavaModelException;
/**
 * Returns the number of parameters of this method.
 * This is a handle-only method.
 * 
 * @return the number of parameters of this method
 */
int getNumberOfParameters();
/**
 * Returns the binding key for this method. A binding key is a key that uniquely
 * identifies this method. It allows access to generic info for parameterized
 * methods.
 * 
 * @return the binding key for this method
 * @see descent.core.dom.IBinding#getKey()
 * @see BindingKey
 * @since 3.1
 */
String getKey();
/**
 * Returns the names of parameters in this method.
 * For binary types, associated source or attached Javadoc are used to retrieve the names.
 * If none can be retrieved, then these names are invented as "arg"+i, where i starts at 0.
 * Returns an empty array if this method has no parameters.
 *
 * <p>For example, a method declared as <code>public void foo(String text, int length)</code>
 * would return the array <code>{"text","length"}</code>.
 * </p>
 *
 * @exception JavaModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource.
 * @return the names of parameters in this method, an empty array if this method has no parameters
 */
String[] getParameterNames() throws JavaModelException;
/**
 * Returns the type signatures for the parameters of this method.
 * Returns an empty array if this method has no parameters.
 * This is a handle-only method.
 * <p>
 * For example, a source method declared as <code>public void foo(String text, int length)</code>
 * would return the array <code>{"QString;","I"}</code>.
 * </p>
 * <p>
 * The type signatures may be either unresolved (for source types)
 * or resolved (for binary types), and either basic (for basic types)
 * or rich (for parameterized types). See {@link Signature} for details.
 * </p>
 * 
 * @return the type signatures for the parameters of this method, an empty array if this method has no parameters
 * @see Signature
 */
String[] getParameterTypes();
/**
 * Returns the names of parameters in this method.
 * For binary types, these names are invented as "arg"+i, where i starts at 0 
 * (even if source is associated with the binary or if Javdoc is attached to the binary).
 * Returns an empty array if this method has no parameters.
 *
 * <p>For example, a method declared as <code>public void foo(String text, int length)</code>
 * would return the array <code>{"text","length"}</code>. For the same method in a
 * binary, this would return <code>{"arg0", "arg1"}</code>.
 * </p>
 *
 * @exception JavaModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource.
 * @return the names of parameters in this method, an empty array if this method has no parameters
 * @since 3.2
 */
String[] getRawParameterNames() throws JavaModelException;
/**
 * Returns the default values of the parameters. Each element in this
 * array is either <code>null</code>, if no default parameter is declared
 * for a specific parameter, or a String representing an expression.
 * <code>null</code> may be returned if there are no default values. 
 * @exception JavaModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource.
 * @return the default values of the parameters
 */
String[] getParameterDefaultValues() throws JavaModelException;
/**
 * Returns the type signature of the return value of this method.
 * For constructors, this returns the signature for void.
 * <p>
 * For example, a source method declared as <code>public String getName()</code>
 * would return <code>"QString;"</code>.
 * </p>
 * <p>
 * The type signature may be either unresolved (for source types)
 * or resolved (for binary types), and either basic (for basic types)
 * or rich (for parameterized types). See {@link Signature} for details.
 * </p>
 *
 * @exception JavaModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource.
 * @return the type signature of the return value of this method, void  for constructors
 * @see Signature
 */
String getReturnType() throws JavaModelException;
/**
 * Returns the signature of this method. This includes the signatures for the
 * parameter types and return type, but does not include the method name,
 * exception types, or type parameters.
 * <p>
 * For example, a source method declared as <code>public void foo(String text, int length)</code>
 * would return <code>"(QString;I)V"</code>.
 * </p>
 * <p>
 * The type signatures embedded in the method signature may be either unresolved
 * (for source types) or resolved (for binary types), and either basic (for
 * basic types) or rich (for parameterized types). See {@link Signature} for
 * details.
 * </p>
 *
 * @return the signature of this method
 * @exception JavaModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource.
 * @see Signature
 */
String getSignature() throws JavaModelException;
/**
 * Returns whether this method is a method.
 *
 * @exception JavaModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource.
 * 
 * @return true if this method is a method, false otherwise
 */
boolean isMethod() throws JavaModelException;
/**
 * Returns whether this method is a constructor.
 *
 * @exception JavaModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource.
 * 
 * @return true if this method is a constructor, false otherwise
 */
boolean isConstructor() throws JavaModelException;
/**
 * Returns whether this method is a destructor.
 *
 * @exception JavaModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource.
 * 
 * @return true if this method is a destructor, false otherwise
 */
boolean isDestructor() throws JavaModelException;
/**
 * Returns whether this method is a new (allocator).
 *
 * @exception JavaModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource.
 * 
 * @return true if this method is a new (allocator), false otherwise
 */
boolean isNew() throws JavaModelException;
/**
 * Returns whether this method is a delete (dellocator).
 *
 * @exception JavaModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource.
 * 
 * @return true if this method is a delete (dellocator), false otherwise
 */
boolean isDelete() throws JavaModelException;
/**
 * Returns whether this method is a postblit.
 *
 * @exception JavaModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource.
 * 
 * @return true if this method is a postblit, false otherwise
 */
boolean isPostBlit() throws JavaModelException;

/**
 * Returns whether this method is a main method.
 * It is a main method if:
 * <ul>
 * <li>its name is equal to <code>"main"</code></li>
 * <li>its return type is <code>void</code></li>
 * <li>it is <code>static</code> and <code>public</code></li>
 * <li>it defines one parameter whose type's simple name is <code>String[]</code></li>
 * </ul>
 * 
 * @exception JavaModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource.
 * @since 2.0
 * @return true if this method is a main method, false otherwise
 */
boolean isMainMethod() throws JavaModelException;
/**
 * Returns whether this method represents a resolved method.
 * If a method is resoved, its key contains resolved information.
 * 
 * @return whether this method represents a resolved method.
 * @since 3.1
 */
boolean isResolved();
/**
 * Returns whether this method is similar to the given method.
 * Two methods are similar if:
 * <ul>
 * <li>their element names are equal</li>
 * <li>they have the same number of parameters</li>
 * <li>the simple names of their parameter types are equal</li>
 * </ul>
 * This is a handle-only method.
 * 
 * @param method the given method
 * @return true if this method is similar to the given method.
 * @see Signature#getSimpleName(char[])
 * @since 2.0
 */
boolean isSimilar(IMethod method);
/**
 * Returns whether this method has variadic arguments, and how. This
 * is one of the <code>VARARGS_*</code> constants of this interface.
 *
 * @exception JavaModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource.
 * 
 * @return one of the <code>VARARGS_*</code> constants of this interface.
 */
int getVarargs() throws JavaModelException;
}
