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
package descent.internal.core;

import descent.core.Signature;

/**
 * Element info for IField elements.
 */

public class SourceFieldElementInfo extends MemberElementInfo /* implements ISourceField */ {
	
	/**
	 * The type name of this field.
	 */
	protected char[] typeName;
	
	/**
	 * The field's initializer string (if the field is a constant, or an enum member).
	 */
	protected char[] initializationSource;

/*
 * Returns the initialization source for this field.
 * Returns null if the field is not a constant or if it has no initialization.
 */
public char[] getInitializationSource() {
	return this.initializationSource;
}
/**
 * Returns the type name of the field.
 */
public char[] getTypeName() {
	return this.typeName;
}
/**
 * Returns the type signature of the field.
 *
 * @see Signature
 */
protected String getTypeSignature() {
	if (this.typeName != null) {
//		return Signature.createTypeSignature(this.typeName, false);
		return new String(this.typeName);
	}
	return null;
}

/**
 * Sets the type name of the field.
 */
protected void setTypeName(char[] typeName) {
	this.typeName = typeName;
}
public void setInitializationSource(char[] initializationSource) {
	this.initializationSource = initializationSource;
}
}
