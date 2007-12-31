/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package descent.core.dom;

import descent.internal.compiler.parser.ASTDmdNode;

/**
 * A binding resolver is an internal mechanism for figuring out the binding
 * for a major declaration, type, or name reference. This also handles
 * the creation and mapping between annotations and the ast nodes that define them.
 * <p>
 * The default implementation serves as the default binding resolver
 * that does no resolving whatsoever. Internal subclasses do all the real work.
 * </p>
 * 
 * @see AST#getBindingResolver
 */
class BindingResolver {
	
	/**
	 * Creates a binding resolver.
	 */
	BindingResolver() {
		// default implementation: do nothing
	}

	/**
	 * Finds the corresponding AST node from which the given binding originated.
	 * Returns <code>null</code> if the binding does not correspond to any node
	 * in the compilation unit.
	 * <p>
	 * The following table indicates the expected node type for the various
	 * different kinds of bindings:
	 * <ul>
	 * <li></li>
	 * <li>package - a <code>PackageDeclaration</code></li>
	 * <li>class or interface - a <code>TypeDeclaration</code> or a
	 *    <code>ClassInstanceCreation</code> (for anonymous classes) </li>
	 * <li>primitive type - none</li>
	 * <li>array type - none</li>
	 * <li>field - a <code>VariableDeclarationFragment</code> in a 
	 *    <code>FieldDeclaration</code> </li>
	 * <li>local variable - a <code>SingleVariableDeclaration</code>, or
	 *    a <code>VariableDeclarationFragment</code> in a 
	 *    <code>VariableDeclarationStatement</code> or 
	 *    <code>VariableDeclarationExpression</code></li>
	 * <li>method - a <code>MethodDeclaration</code> </li>
	 * <li>constructor - a <code>MethodDeclaration</code> </li>
	 * <li>annotation type - an <code>AnnotationTypeDeclaration</code>
	 * <li>annotation type member - an <code>AnnotationTypeMemberDeclaration</code>
	 * </ul>
	 * </p>
	 * <p>
	 * The implementation of <code>CompilationUnit.findDeclaringNode</code>
	 * forwards to this method.
	 * </p>
	 * <p>
	 * The default implementation of this method returns <code>null</code>.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param binding the binding
	 * @return the corresponding node where the bindings is declared, 
	 *    or <code>null</code> if none
	 */
	ASTNode findDeclaringNode(IBinding binding) {
		return null;
	}

	/**
	 * Finds the corresponding AST node from which the given binding key originated.
	 * 
	 * The default implementation of this method returns <code>null</code>.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param bindingKey the binding key
	 * @return the corresponding node where the bindings is declared, 
	 *    or <code>null</code> if none
	 */
	ASTNode findDeclaringNode(String bindingKey) {
		return null;
	}

	/**
	 * Resolves and returns the binding for the constructor being invoked.
	 * <p>
	 * The implementation of
	 * <code>ClassInstanceCreation.resolveConstructor</code>
	 * forwards to this method. Which constructor is invoked is often a function
	 * of the context in which the expression node is embedded as well as
	 * the expression subtree itself.
	 * </p>
	 * <p>
	 * The default implementation of this method returns <code>null</code>.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param expression the expression of interest
	 * @return the binding for the constructor being invoked, or 
	 *    <code>null</code> if no binding is available
	 */
	IMethodBinding resolveConstructor(ConstructorDeclaration expression) {
		return null;
	}
	
	/**
	 * Resolves the type of the given expression and returns the type binding
	 * for it. 
	 * <p>
	 * The implementation of <code>Expression.resolveTypeBinding</code>
	 * forwards to this method. The result is often a function of the context
	 * in which the expression node is embedded as well as the expression 
	 * subtree itself.
	 * </p>
	 * <p>
	 * The default implementation of this method returns <code>null</code>.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param expression the expression whose type is of interest
	 * @return the binding for the type of the given expression, or 
	 *    <code>null</code> if no binding is available
	 */
	ITypeBinding resolveExpressionType(Expression expression) {
		return null;
	}
	
	/**
	 * Resolves the given import declaration and returns the binding for it.
	 * <p>
	 * The implementation of <code>ImportDeclaration.resolveBinding</code>
	 * forwards to this method.
	 * </p>
	 * <p>
	 * The default implementation of this method returns <code>null</code>.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param importDeclaration the import declaration of interest
	 * @return the binding for the given package declaration, or 
	 *         the package binding (for on-demand imports) or type binding
	 *         (for single-type imports), or <code>null</code> if no binding is
	 *         available
	 */
	IPackageBinding resolveImport(Import imp) {
		return null;
	}
	
	/**
	 * Resolves the given import declaration and returns the binding for it.
	 * <p>
	 * The implementation of <code>ImportDeclaration.resolveBinding</code>
	 * forwards to this method.
	 * </p>
	 * <p>
	 * The default implementation of this method returns <code>null</code>.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param importDeclaration the import declaration of interest
	 * @return the binding for the given package declaration, or 
	 *         the package binding (for on-demand imports) or type binding
	 *         (for single-type imports), or <code>null</code> if no binding is
	 *         available
	 */
	IBinding resolveSelectiveImport(SelectiveImport imp) {
		return null;
	}
		
	/**
	 * Resolves the given method declaration and returns the binding for it.
	 * <p>
	 * The implementation of <code>MethodDeclaration.resolveBinding</code>
	 * forwards to this method. How the method resolves is often a function of
	 * the context in which the method declaration node is embedded as well as
	 * the method declaration subtree itself.
	 * </p>
	 * <p>
	 * The default implementation of this method returns <code>null</code>.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param method the method or constructor declaration of interest
	 * @return the binding for the given method declaration, or 
	 *    <code>null</code> if no binding is available
	 */
	IMethodBinding resolveMethod(FunctionDeclaration method) {
		return null;
	}
	
	/**
	 * Resolves the given name and returns the type binding for it.
	 * <p>
	 * The implementation of <code>Name.resolveBinding</code> forwards to
	 * this method. How the name resolves is often a function of the context
	 * in which the name node is embedded as well as the name itself.
	 * </p>
	 * <p>
	 * The default implementation of this method returns <code>null</code>.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param name the name of interest
	 * @return the binding for the name, or <code>null</code> if no binding is
	 *    available
	 */
	IBinding resolveName(Name name) {
		return null;
	}
	
	/**
	 * Resolves the given package declaration and returns the binding for it.
	 * <p>
	 * The implementation of <code>PackageDeclaration.resolveBinding</code>
	 * forwards to this method.
	 * </p>
	 * <p>
	 * The default implementation of this method returns <code>null</code>.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param pkg the package declaration of interest
	 * @return the binding for the given package declaration, or 
	 *    <code>null</code> if no binding is available
	 */
	IPackageBinding resolvePackage(ModuleDeclaration pkg) {
		return null;
	}

	/**
	 * Resolves the given aggregate declaration and returns the binding
	 * for it.
	 * <p>
	 * The implementation of <code>AggregateDeclaration.resolveBinding</code> 
	 * forwards to this method. How the declaration resolves is often a 
	 * function of the context in which the declaration node is embedded as well
	 * as the declaration subtree itself.
	 * </p>
	 * <p>
	 * The default implementation of this method returns <code>null</code>.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param type the anonymous class declaration of interest
	 * @return the binding for the given class declaration, or <code>null</code>
	 *    if no binding is available
	 */
	ITypeBinding resolveAggregate(AggregateDeclaration type) {
		return null;
	}

	/**
	 * Resolves the given enum declaration and returns the binding
	 * for it.
	 * <p>
	 * The implementation of <code>EnumDeclaration.resolveBinding</code> 
	 * forwards to this method. How the enum declaration resolves is often
	 * a function of the context in which the declaration node is embedded
	 * as well as the enum declaration subtree itself.
	 * </p>
	 * <p>
	 * The default implementation of this method returns <code>null</code>.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param type the enum declaration of interest
	 * @return the binding for the given enum declaration, or <code>null</code>
	 *    if no binding is available
	 * @since 3.0
	 */
	ITypeBinding resolveEnum(EnumDeclaration type) {
		return null;
	}
	
	/**
	 * Resolves the given enum member declaration and returns the binding for it.
	 * <p>
	 * The implementation of <code>EnumMember.resolveBinding</code>
	 * forwards to this method.
	 * </p>
	 * <p>
	 * The default implementation of this method returns <code>null</code>.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param member the enum member declaration of interest
	 * @return the binding for the given enum memeber declaration, or 
	 *    <code>null</code> if no binding is available
	 */
	IVariableBinding resolveEnumMember(EnumMember member) {
		return null;
	}

	/**
	 * Resolves the given type and returns the type binding for it.
	 * <p>
	 * The implementation of <code>Type.resolveBinding</code>
	 * forwards to this method. How the type resolves is often a function
	 * of the context in which the type node is embedded as well as the type
	 * subtree itself.
	 * </p>
	 * <p>
	 * The default implementation of this method returns <code>null</code>.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param type the type of interest
	 * @return the binding for the given type, or <code>null</code>
	 *    if no binding is available 
	 */
	ITypeBinding resolveType(Type type) {
		return null;
	}
	
	/**
	 * Resolves the given variable declaration and returns the binding for it.
	 * <p>
	 * The implementation of <code>VariableDeclarationFragment.resolveBinding</code>
	 * forwards to this method. How the variable declaration resolves is often
	 * a function of the context in which the variable declaration node is 
	 * embedded as well as the variable declaration subtree itself. VariableDeclaration 
	 * declarations used as local variable, formal parameter and exception 
	 * variables resolve to local variable bindings; variable declarations
	 * used to declare fields resolve to field bindings.
	 * </p>
	 * <p>
	 * The default implementation of this method returns <code>null</code>.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param variable the variable declaration of interest
	 * @return the binding for the given variable declaration, or 
	 *    <code>null</code> if no binding is available
	 */
	IVariableBinding resolveVariableFragment(VariableDeclarationFragment variable) {
		return null;
	}
	
	/**
	 * Resolves the given variable declaration and returns the binding for it.
	 * <p>
	 * The implementation of <code>VariableDeclaration.resolveBinding</code>
	 * forwards to this method. How the variable declaration resolves is often
	 * a function of the context in which the variable declaration node is 
	 * embedded as well as the variable declaration subtree itself. VariableDeclaration 
	 * declarations used as local variable, formal parameter and exception 
	 * variables resolve to local variable bindings; variable declarations
	 * used to declare fields resolve to field bindings.
	 * </p>
	 * <p>
	 * The default implementation of this method returns <code>null</code>.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param variable the variable declaration of interest
	 * @return the binding for the given variable declaration, or 
	 *    <code>null</code> if no binding is available
	 */
	ITypeBinding resolveVariable(VariableDeclaration variable) {
		return null;
	}
	
	/**
	 * Resolves the given argument and returns the binding for it.
	 * <p>
	 * The implementation of <code>Argument.resolveBinding</code>
	 * forwards to this method.
	 * </p>
	 * <p>
	 * The default implementation of this method returns <code>null</code>.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param argument the argument of interest
	 * @return the binding for the given argument, or 
	 *    <code>null</code> if no binding is available
	 */
	public IVariableBinding resolveArgument(Argument argument) {
		return null;
	} 
	
	/**
	 * Resolves the given well known type by name and returns the type binding
	 * for it.
	 * <p>
	 * The implementation of <code>AST.resolveWellKnownType</code>
	 * forwards to this method.
	 * </p>
	 * <p>
	 * The default implementation of this method returns <code>null</code>.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param name the name of a well known type
	 * @return the corresponding type binding, or <code>null<code> if the 
	 *   named type is not considered well known or if no binding can be found
	 *   for it
	 */
	ITypeBinding resolveWellKnownType(String name) {
		return null;
	}
	
	/**
	 * Allows the user to store information about the given old/new pair of
	 * AST nodes.
	 * <p>
	 * The default implementation of this method does nothing.
	 * Subclasses may reimplement.
	 * </p>
	 * 
	 * @param newNode the new AST node
	 * @param oldASTNode the old AST node
	 */
	void store(ASTNode newNode, ASTDmdNode oldASTNode) {
		// default implementation: do nothing
	}
	
}
