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
package descent.internal.core.search.matching;

import descent.core.compiler.CharOperation;
import descent.core.search.SearchPattern;
import descent.internal.core.search.indexing.IIndexConstants;
import descent.internal.core.util.Util;

public class FieldPattern extends VariablePattern implements IIndexConstants {

// declaring type
protected char[] declaringQualification;
protected char[] declaringSimpleName;

// type
protected char[] typeQualification;
protected char[] typeSimpleName;

public char[] typeName;

public int declarationStart;

protected static char[][] REF_CATEGORIES = { REF };
protected static char[][] REF_AND_DECL_CATEGORIES = { REF, FIELD_DECL };
protected static char[][] DECL_CATEGORIES = { FIELD_DECL };

/*
 * Create index key for field declaration pattern:
 *		key = fieldName / typeName / packageName / declarationStart / enclosingTypeName / modifiers
 */
public static char[] createIndexKey(long modifiers, char[] packageName, char[][] enclosingTypeNames, char[] fieldName, char[] typeName, int declarationStart) {
	char[] declarationStartChars = String.valueOf(declarationStart).toCharArray();
	int declarationStartLength = declarationStartChars.length;
	
	int fieldNameLength = fieldName == null ? 0 : fieldName.length;
	int typeNameLength = typeName == null ? 0 : typeName.length;
	int packageLength = packageName == null ? 0 : packageName.length;
	int enclosingNamesLength = 0;
	if (enclosingTypeNames != null) {
		for (int i = 0, length = enclosingTypeNames.length; i < length;) {
			enclosingNamesLength += enclosingTypeNames[i].length;
			if (++i < length)
				enclosingNamesLength++; // for the '.' separator
		}
	}

	int resultLength = fieldNameLength + typeNameLength + packageLength + declarationStartLength + enclosingNamesLength + 7;
	char[] result = new char[resultLength];
	int pos = 0;
	if (fieldNameLength > 0) {
		System.arraycopy(fieldName, 0, result, pos, fieldNameLength);
		pos += fieldNameLength;
	}
	result[pos++] = SEPARATOR;
	if (typeNameLength > 0) {
		System.arraycopy(typeName, 0, result, pos, typeNameLength);
		pos += typeNameLength;
	}
	result[pos++] = SEPARATOR;
	if (packageLength > 0) {
		System.arraycopy(packageName, 0, result, pos, packageLength);
		pos += packageLength;
	}
	result[pos++] = SEPARATOR;
	System.arraycopy(declarationStartChars, 0, result, pos, declarationStartLength);
	pos += declarationStartLength;
	result[pos++] = SEPARATOR;
	if (enclosingTypeNames != null && enclosingNamesLength > 0) {
		for (int i = 0, length = enclosingTypeNames.length; i < length;) {
			char[] enclosingName = enclosingTypeNames[i];
			int itsLength = enclosingName.length;
			System.arraycopy(enclosingName, 0, result, pos, itsLength);
			pos += itsLength;
			if (++i < length)
				result[pos++] = '.';
		}
	}
	result[pos++] = SEPARATOR;
	result[pos++] = (char) modifiers;
	result[pos] = (char) (modifiers>>16);
	return result;
}

public FieldPattern(
	boolean findDeclarations,
	boolean readAccess,
	boolean writeAccess,
	char[] name, 
	char[] declaringQualification,
	char[] declaringSimpleName,	
	char[] typeQualification, 
	char[] typeSimpleName,
	int matchRule) {

	super(FIELD_PATTERN, findDeclarations, readAccess, writeAccess, name, matchRule);

	this.declaringQualification = isCaseSensitive() ? declaringQualification : CharOperation.toLowerCase(declaringQualification);
	this.declaringSimpleName = isCaseSensitive() ? declaringSimpleName : CharOperation.toLowerCase(declaringSimpleName);
	this.typeQualification = isCaseSensitive() ? typeQualification : CharOperation.toLowerCase(typeQualification);
	this.typeSimpleName = (isCaseSensitive() || isCamelCase())  ? typeSimpleName : CharOperation.toLowerCase(typeSimpleName);

	((InternalSearchPattern)this).mustResolve = mustResolve();
}
/*
 * Instanciate a field pattern with additional information for generics search
 */
public FieldPattern(
	boolean findDeclarations,
	boolean readAccess,
	boolean writeAccess,
	char[] name, 
	char[] declaringQualification,
	char[] declaringSimpleName,	
	char[] typeQualification, 
	char[] typeSimpleName,
	String typeSignature,
	int matchRule) {

	this(findDeclarations, readAccess, writeAccess, name, declaringQualification, declaringSimpleName, typeQualification, typeSimpleName, matchRule);

	// store type signatures and arguments
	if (typeSignature != null) {
		this.typeSignatures = Util.splitTypeLevelsSignature(typeSignature);
		setTypeArguments(Util.getAllTypeArguments(this.typeSignatures));
	}
}
/*
 * Type entries are encoded as:
 * 	simpleTypeName / packageName / declarationStart, enclosingTypeName / modifiers
 *			e.g. Object/java.lang//0
 * 		e.g. Cloneable/java.lang//512
 * 		e.g. LazyValue/javax.swing/UIDefaults/0
 * or for secondary types as:
 * 	simpleTypeName / packageName / enclosingTypeName / modifiers / S
 */
public void decodeIndexKey(char[] key) {
	int slash = CharOperation.indexOf(SEPARATOR, key, 0);
	this.simpleName = CharOperation.subarray(key, 0, slash);
	
	int start = ++slash;
	if (key[start] == SEPARATOR) {
		this.typeName = CharOperation.NO_CHAR;
	} else {
		slash = CharOperation.indexOf(SEPARATOR, key, start);
		this.typeName = CharOperation.subarray(key, start, slash);
	}

	start = ++slash;
	if (key[start] == SEPARATOR) {
		this.pkg = CharOperation.NO_CHAR;
	} else {
		slash = CharOperation.indexOf(SEPARATOR, key, start);
		this.pkg = TypeDeclarationPattern.internedPackageNames.add(CharOperation.subarray(key, start, slash));
	}
	
	start = ++slash;
	if (key[start] == SEPARATOR) {
		this.declarationStart = -1;
	} else {
		this.declarationStart = 0;
		for (; key[start] != SEPARATOR; start++) {
			declarationStart = 10 * declarationStart + (key[start] - '0');
		}
		slash = start;
	}

	// Continue key read by the end to decode modifiers
	int last = key.length-1;
	this.modifiers = key[last-1] + (key[last]<<16);
	decodeModifiers();

	// Retrieve enclosing type names
	start = slash + 1;
	last -= 2; // position of ending slash
	if (start == last) {
		this.enclosingTypeNames = CharOperation.NO_CHAR_CHAR;
	} else {
		if (last == (start+1) && key[start] == ZERO_CHAR) {
			this.enclosingTypeNames = ONE_ZERO_CHAR;
		} else {
			this.enclosingTypeNames = CharOperation.splitOn('.', key, start, last);
		}
	}
}
private void decodeModifiers() {
	// TODO Auto-generated method stub
}

public SearchPattern getBlankPattern() {
	return new FieldPattern(false, false, false, null, null, null, null, null, R_EXACT_MATCH | R_CASE_SENSITIVE);
}
public char[] getIndexKey() {
	return this.simpleName;
}
public char[][] getIndexCategories() {
	if (this.findReferences)
		return this.findDeclarations || this.writeAccess ? REF_AND_DECL_CATEGORIES : REF_CATEGORIES;
	if (this.findDeclarations)
		return DECL_CATEGORIES;
	return CharOperation.NO_CHAR_CHAR;
}
public boolean matchesDecodedKey(SearchPattern decodedPattern) {
	return true; // index key is not encoded so query results all match
}
protected boolean mustResolve() {
	if (this.declaringSimpleName != null || this.declaringQualification != null) return true;
	if (this.typeSimpleName != null || this.typeQualification != null) return true;

	return super.mustResolve();
}
protected StringBuffer print(StringBuffer output) {
	if (this.findDeclarations) {
		output.append(this.findReferences
			? "FieldCombinedPattern: " //$NON-NLS-1$
			: "FieldDeclarationPattern: "); //$NON-NLS-1$
	} else {
		output.append("FieldReferencePattern: "); //$NON-NLS-1$
	}
	if (declaringQualification != null) output.append(declaringQualification).append('.');
	if (declaringSimpleName != null) 
		output.append(declaringSimpleName).append('.');
	else if (declaringQualification != null) output.append("*."); //$NON-NLS-1$
	if (simpleName == null) {
		output.append("*"); //$NON-NLS-1$
	} else {
		output.append(simpleName);
	}
	if (typeQualification != null) 
		output.append(" --> ").append(typeQualification).append('.'); //$NON-NLS-1$
	else if (typeSimpleName != null) output.append(" --> "); //$NON-NLS-1$
	if (typeSimpleName != null) 
		output.append(typeSimpleName);
	else if (typeQualification != null) output.append("*"); //$NON-NLS-1$
	return super.print(output);
}
}
