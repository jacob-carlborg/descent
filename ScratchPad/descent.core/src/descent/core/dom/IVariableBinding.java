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

import descent.core.IEvaluationResult;

/**
 * A variable binding represents either a field of a class or interface, or 
 * a local variable declaration (including formal parameters, local variables, 
 * and exception variables).
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 * @see ITypeBinding#getDeclaredFields()
 * @since 2.0
 */
public interface IVariableBinding extends IBinding {
	
	/**
	 * Returns whether this binding is for a variable.
	 * 
	 * @return <code>true</code> if this is the binding for a variable,
	 *    and <code>false</code> otherwise
	 */ 
	public boolean isVariable();
	
	/**
	 * Returns whether this binding is for an enum constant.
	 * Note that this method returns <code>false</code> for local variables
	 * and for fields other than enum constants.
	 * 
	 * @return <code>true</code> if this is the binding for an enum constant,
	 *    and <code>false</code> otherwise
	 * @since 3.1
	 */ 
	public boolean isEnumConstant();
	
	/**
	 * Returns whether this binding corresponds to a parameter. 
	 * 
	 * @return <code>true</code> if this is the binding for a parameter,
	 *    and <code>false</code> otherwise
	 * @since 3.2
	 */
	public boolean isParameter();
	
	/**
	 * Returns whether this binding corresponds to a local variable, alias
	 * or typedef. 
	 * 
	 * @return <code>true</code> if this is the binding for a local variable,
	 * alias or typedef, and <code>false</code> otherwise
	 * @since 3.2
	 */
	public boolean isLocal();

	/**
	 * Returns the name of the field or local variable declared in this binding.
	 * The name is always a simple identifier.
	 * 
	 * @return the name of this field or local variable
	 */
	public String getName();
	
	/**
	 * Returns the binding representing the symbol
	 * that declares this field.
	 * 
	 * @return the binding of the symbol that declares this field,
	 *   or <code>null</code> if none
	 */
	public IBinding getDeclaringSymbol();

	/**
	 * Returns the binding for the type of this field or local variable.
	 * 
	 * @return the binding for the type of this field or local variable
	 */
	public ITypeBinding getType();
	
	/**
	 * Returns a small integer variable id for this variable binding.
	 * <p>
	 * <b>Local variables inside methods:</b> Local variables (and parameters)
	 * declared within a single method are assigned ascending ids in normal
	 * code reading order; var1.getVariableId()&lt;var2.getVariableId() means that var1 is
	 * declared before var2.
	 * </p>
	 * <p>
	 * <b>Local variables outside methods:</b> Local variables declared in a
	 * type's static initializers (or initializer expressions of static fields)
	 * are assigned ascending ids in normal code reading order. Local variables
	 * declared in a type's instance initializers (or initializer expressions
	 * of non-static fields) are assigned ascending ids in normal code reading
	 * order. These ids are useful when checking definite assignment for
	 * static initializers (JLS 16.7) and instance initializers (JLS 16.8), 
	 * respectively.
	 * </p>
	 * <p>
	 * <b>Fields:</b> Fields declared as members of a type are assigned 
	 * ascending ids in normal code reading order; 
	 * field1.getVariableId()&lt;field2.getVariableId() means that field1 is declared before
	 * field2.
	 * </p>
	 * 
	 * @return a small non-negative variable id
	 */
	public int getVariableId();
	
	/**
	 * Returns this binding's constant value if it has one.
	 * Some variables may have a value computed at compile-time. If the type of
	 * the value is a primitive type, the result is the boxed equivalent (i.e.,
	 * int returned as an <code>Integer</code>). If the type of the value is
	 * <code>String</code>, the result is the string itself. If the variable has
	 * no compile-time computed value, the result is <code>null</code>.
	 * (Note: compile-time constant expressions cannot denote <code>null</code>;
	 * JLS2 15.28.). The result is always <code>null</code> for enum constants.
	 * 
	 * @return the constant value, or <code>null</code> if none
	 * @since 3.0
	 */
	public IEvaluationResult getConstantValue();
	
	/**
	 * Returns the binding for the variable declaration corresponding to this
	 * variable binding. For a binding for a field declaration in an instance
	 * of a generic type, this method returns the binding for the corresponding
	 * field declaration in the generic type. For other variable bindings,
	 * including all ones for local variables and parameters, this method
	 * returns the same binding.
	 *
	 * @return the variable binding for the originating declaration
	 * @since 3.1
	 */
	public IVariableBinding getVariableDeclaration();

}
