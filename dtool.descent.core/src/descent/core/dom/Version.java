/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package descent.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.compiler.IScanner;
import descent.core.compiler.ITerminalSymbols;
import descent.core.compiler.InvalidInputException;


/**
 * AST node for a version. A value is an identifier other than
 * a keyword, boolean literal ("true", "false") or null literal ("null"), or an integer.
 * <pre>
 * Version:
 *     [ Identifier | IntegerExpression ]
 * </pre>
 */
public class Version extends ASTNode {

	/**
	 * The "value" structural property of this node type.
	 */
	public static final SimplePropertyDescriptor VALUE_PROPERTY = 
		new SimplePropertyDescriptor(Version.class, "value", String.class, MANDATORY); //$NON-NLS-1$
	
	/**
	 * A list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor}),
	 * or null if uninitialized.
	 * @since 3.0
	 */
	private static final List PROPERTY_DESCRIPTORS;
	
	static {
		List propertyList = new ArrayList(2);
		createPropertyList(Version.class, propertyList);
		addProperty(VALUE_PROPERTY, propertyList);
		PROPERTY_DESCRIPTORS = reapPropertyList(propertyList);
	}
	
	/**
	 * Returns a list of structural property descriptors for this node type.
	 * Clients must not modify the result.
	 * 
	 * @param apiLevel the API level; one of the AST.JLS* constants
	 * @return a list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor})
	 * @since 3.0
	 */
	public static List propertyDescriptors(int apiLevel) {
		return PROPERTY_DESCRIPTORS;
	}
	
	/**
	 * An unspecified (but externally observable) legal D identifier.
	 */
	private static final String MISSING_VALUE = "MISSING";//$NON-NLS-1$
	
	/**
	 * The value; defaults to a unspecified, legal D identifier.
	 */
	private String value = MISSING_VALUE;
	
	/**
	 * Creates a new AST node for a value owned by the given AST.
	 * The new node has an unspecified, legal D identifier.
	 * <p>
	 * N.B. This constructor is package-private; all subclasses must be 
	 * declared in the same package; clients are unable to declare 
	 * additional subclasses.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	Version(AST ast) {
		super(ast);
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 * @since 3.0
	 */
	final List internalStructuralPropertiesForType(int apiLevel) {
		return propertyDescriptors(apiLevel);
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	final Object internalGetSetObjectProperty(SimplePropertyDescriptor property, boolean get, Object value) {
		if (property == VALUE_PROPERTY) {
			if (get) {
				return getValue();
			} else {
				setValue((String) value);
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
		return SIMPLE_NAME;
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	ASTNode clone0(AST target) {
		Version result = new Version(target);
		result.setSourceRange(this.getStartPosition(), this.getLength());
		result.setValue(getValue());
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
		visitor.visit(this);
		visitor.endVisit(this);
	}

	/**
	 * Returns this node's value.
	 * 
	 * @return the value of this node
	 */ 
	public String getValue() {
		return this.value;
	}
	
	/**
	 * Sets the value of this node to the given value.
	 * 
	 * @param value the value of this node
	 * @exception IllegalArgumentException if the value is invalid
	 */ 
	public void setValue(String value) {
		// update internalSetValue if this is changed
		if (value == null) {
			throw new IllegalArgumentException();
		}
		IScanner scanner = this.ast.scanner;
		char[] source = value.toCharArray();
		scanner.setSource(source);
		final int length = source.length;
		scanner.resetTo(0, length);
		try {
			int tokenType = scanner.getNextToken();
			switch(tokenType) {
				case ITerminalSymbols.TokenNameIdentifier:
				case ITerminalSymbols.TokenNameIntegerLiteral:
					if (scanner.getCurrentTokenEndPosition() != length - 1) {
						// this is the case when there is only one value
						throw new IllegalArgumentException();
					}
					break;
				default:
					throw new IllegalArgumentException();
			}
		} catch(InvalidInputException e) {
			throw new IllegalArgumentException();
		}
		preValueChange(VALUE_PROPERTY);
		this.value = value;
		postValueChange(VALUE_PROPERTY);
	}

	/* (omit javadoc for this method)
	 * This method is a copy of setIdentifier(String) that doesn't do any validation.
	 */
	void internalSetValue(String ident) {
		preValueChange(VALUE_PROPERTY);
		this.value = ident;
		postValueChange(VALUE_PROPERTY);
	}
		
	/* (omit javadoc for this method)
	 * Method declared on Name.
	 */
	void appendName(StringBuffer buffer) {
		buffer.append(getValue());
	}

	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int memSize() {
		int size = Name.BASE_NAME_NODE_SIZE + 2 * 4;
		if (value != MISSING_VALUE) {
			// everything but our missing id costs
			size += stringSize(value);
		}
		return size;
	}
	
	/* (omit javadoc for this method)
	 * Method declared on ASTNode.
	 */
	int treeSize() {
		return memSize();
	}
	
}

