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
package descent.internal.core.search.matching;

import java.io.IOException;

import descent.core.*;
import descent.core.compiler.CharOperation;
import descent.core.search.SearchPattern;
import descent.internal.core.index.*;
import descent.internal.core.search.indexing.IIndexConstants;
import descent.internal.core.util.Util;

public class MethodPattern extends FQNPattern implements IIndexConstants {

protected boolean findDeclarations;
protected boolean findReferences;

public char[] selector;

public char[] declaringQualification;
public char[] declaringSimpleName;

public char[] returnQualification;
public char[] returnSimpleName;

public char[][] parameterQualifications;
public char[][] parameterSimpleNames;
public int parameterCount;
public boolean varargs = false;

// extra reference info
protected IType declaringType;

// Signatures and arguments for generic search
char[][] returnTypeSignatures;
char[][][] returnTypeArguments;
char[][][] parametersTypeSignatures;
char[][][][] parametersTypeArguments;
boolean methodParameters = false;
char[][] methodArguments;

public char[] signature;
public int declarationStart;

protected static char[][] REF_CATEGORIES = { METHOD_REF };
protected static char[][] REF_AND_DECL_CATEGORIES = { METHOD_REF, METHOD_DECL };
protected static char[][] DECL_CATEGORIES = { METHOD_DECL };

/**
 * Method entries are encoded as selector '/' Arity:
 * e.g. 'foo/0'
 */
/*
 * selector / packageName / signature / declarationStart / enclosingTypeName / modifiers / argCount  
 */
public static char[] createIndexKey(long modifiers, char[] packageName, char[][] enclosingTypeNames, char[] selector, char[] signature, int argCount, int declarationStart) {
	char[] countChars = argCount < 10
		? COUNTS[argCount]
		: ("/" + String.valueOf(argCount)).toCharArray();
		
	int countCharsLength = countChars.length;
	
	char[] declarationStartChars = String.valueOf(declarationStart).toCharArray();
	int declarationStartLength = declarationStartChars.length;
	
	int selectorLength = selector == null ? 0 : selector.length;
	int packageLength = packageName == null ? 0 : packageName.length;
	int signatureLength = signature == null ? 0 : signature.length;
	int enclosingNamesLength = 0;
	if (enclosingTypeNames != null) {
		for (int i = 0, length = enclosingTypeNames.length; i < length;) {
			enclosingNamesLength += enclosingTypeNames[i].length;
			if (++i < length)
				enclosingNamesLength++; // for the '.' separator
		}
	}

	int resultLength = selectorLength + packageLength + signatureLength + enclosingNamesLength + declarationStartLength + countCharsLength + 7;
	char[] result = new char[resultLength];
	int pos = 0;
	if (selectorLength > 0) {
		System.arraycopy(selector, 0, result, pos, selectorLength);
		pos += selectorLength;
	}
	result[pos++] = SEPARATOR;
	if (packageLength > 0) {
		System.arraycopy(packageName, 0, result, pos, packageLength);
		pos += packageLength;
	}
	result[pos++] = SEPARATOR;
	if (signatureLength > 0) {
		System.arraycopy(signature, 0, result, pos, signatureLength);
		pos += signatureLength;
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
	result[pos++] = (char) (modifiers>>16);
	if (countCharsLength > 0) {
		System.arraycopy(countChars, 0, result, pos, countCharsLength);
	}
	return result;
}
// selector / packageName / signature / declarationStart / enclosingTypeName / modifiers / argCount
public void decodeIndexKey(char[] key) {
	int slash = CharOperation.indexOf(SEPARATOR, key, 0);
	this.selector = CharOperation.subarray(key, 0, slash);
	
	int start = ++slash;
	if (key[start] == SEPARATOR) {
		this.pkg = CharOperation.NO_CHAR;
	} else {
		slash = CharOperation.indexOf(SEPARATOR, key, start);
		this.pkg = TypeDeclarationPattern.internedPackageNames.add(CharOperation.subarray(key, start, slash));
	}
	
	start = ++slash;
	if (key[start] == SEPARATOR) {
		this.signature = CharOperation.NO_CHAR;
	} else {
		slash = CharOperation.indexOf(SEPARATOR, key, start);
		this.signature = CharOperation.subarray(key, start, slash);
	}
	
	start = ++slash;
	if (key[start] == SEPARATOR) {
		this.declarationStart = -1;
	} else {
		slash = CharOperation.indexOf(SEPARATOR, key, start);
		// TODO optimize this
		this.declarationStart = Integer.parseInt(new String(CharOperation.subarray(key, start, slash)));
	}
	
	// Continue key read by the end to decode arity
	int last = key.length - 1;
	this.parameterCount = 0;
	int power = 1;
	for (int i=last; i>=0; i--) {
		if (key[i] == SEPARATOR) {
			i--;
			last = i;
			break;
		}
		if (i == last) {
			this.parameterCount = key[i] - '0';
		} else {
			power *= 10;
			this.parameterCount += power * (key[i] - '0');
		}
	}

	// Continue key read by the end to decode modifiers
	// int last = key.length-1;
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

MethodPattern(int matchRule) {
	super(METHOD_PATTERN, matchRule);
}
public MethodPattern(
	boolean findDeclarations,
	boolean findReferences,
	char[] selector, 
	char[] declaringQualification,
	char[] declaringSimpleName,	
	char[] returnQualification, 
	char[] returnSimpleName,
	char[][] parameterQualifications, 
	char[][] parameterSimpleNames,
	IType declaringType,
	int matchRule) {

	this(matchRule);

	this.findDeclarations = findDeclarations;
	this.findReferences = findReferences;

	this.selector = (isCaseSensitive() || isCamelCase())  ? selector : CharOperation.toLowerCase(selector);
	this.declaringQualification = isCaseSensitive() ? declaringQualification : CharOperation.toLowerCase(declaringQualification);
	this.declaringSimpleName = isCaseSensitive() ? declaringSimpleName : CharOperation.toLowerCase(declaringSimpleName);
	this.returnQualification = isCaseSensitive() ? returnQualification : CharOperation.toLowerCase(returnQualification);
	this.returnSimpleName = isCaseSensitive() ? returnSimpleName : CharOperation.toLowerCase(returnSimpleName);
	if (parameterSimpleNames != null) {
		this.parameterCount = parameterSimpleNames.length;
		this.parameterQualifications = new char[this.parameterCount][];
		this.parameterSimpleNames = new char[this.parameterCount][];
		for (int i = 0; i < this.parameterCount; i++) {
			this.parameterQualifications[i] = isCaseSensitive() ? parameterQualifications[i] : CharOperation.toLowerCase(parameterQualifications[i]);
			this.parameterSimpleNames[i] = isCaseSensitive() ? parameterSimpleNames[i] : CharOperation.toLowerCase(parameterSimpleNames[i]);
		}
	} else {
		this.parameterCount = -1;
	}
	this.declaringType = declaringType;
	((InternalSearchPattern)this).mustResolve = mustResolve();
}
/*
 * Instanciate a method pattern with signatures for generics search
 */
public MethodPattern(
	boolean findDeclarations,
	boolean findReferences,
	char[] selector, 
	char[] declaringQualification,
	char[] declaringSimpleName,	
	char[] returnQualification, 
	char[] returnSimpleName,
	String returnSignature,
	char[][] parameterQualifications, 
	char[][] parameterSimpleNames,
	String[] parameterSignatures,
	IMethod method,
	int matchRule) {

	this(findDeclarations,
		findReferences,
		selector, 
		declaringQualification,
		declaringSimpleName,	
		returnQualification, 
		returnSimpleName,
		parameterQualifications, 
		parameterSimpleNames,
		method.getDeclaringType(),
		matchRule);
	
	// Set flags
	try {
		this.varargs = (method.getFlags() & Flags.AccVarargs1) != 0;
	} catch (JavaModelException e) {
		// do nothing
	}

	// Get unique key for parameterized constructors
	String genericDeclaringTypeSignature = null;
//	String genericSignature = null;
	String key;
	/* TODO JDT search/binding
	if (method.isResolved() && (new BindingKey(key = method.getKey())).isParameterizedType()) {
		genericDeclaringTypeSignature = Util.getDeclaringTypeSignature(key);
	} else {
	*/
		methodParameters = true;
	//}

	// Store type signature and arguments for declaring type
	if (genericDeclaringTypeSignature != null) {
		this.typeSignatures = Util.splitTypeLevelsSignature(genericDeclaringTypeSignature);
		setTypeArguments(Util.getAllTypeArguments(this.typeSignatures));
	} else {
		storeTypeSignaturesAndArguments(declaringType);
	}

	// Store type signatures and arguments for return type
	if (returnSignature != null) {
		returnTypeSignatures = Util.splitTypeLevelsSignature(returnSignature);
		returnTypeArguments = Util.getAllTypeArguments(returnTypeSignatures);
	}

	// Store type signatures and arguments for method parameters type
	if (parameterSignatures != null) {
		int length = parameterSignatures.length;
		if (length > 0) {
			parametersTypeSignatures = new char[length][][];
			parametersTypeArguments = new char[length][][][];
			for (int i=0; i<length; i++) {
				parametersTypeSignatures[i] = Util.splitTypeLevelsSignature(parameterSignatures[i]);
				parametersTypeArguments[i] = Util.getAllTypeArguments(parametersTypeSignatures[i]);
			}
		}
	}

	// Store type signatures and arguments for method
	methodArguments = extractMethodArguments(method);
	if (hasMethodArguments())  ((InternalSearchPattern)this).mustResolve = true;
}
/*
 * Instanciate a method pattern with signatures for generics search
 */
public MethodPattern(
	boolean findDeclarations,
	boolean findReferences,
	char[] selector, 
	char[] declaringQualification,
	char[] declaringSimpleName,	
	String declaringSignature,
	char[] returnQualification, 
	char[] returnSimpleName,
	String returnSignature,
	char[][] parameterQualifications, 
	char[][] parameterSimpleNames,
	String[] parameterSignatures,
	char[][] arguments,
	int matchRule) {

	this(findDeclarations,
		findReferences,
		selector, 
		declaringQualification,
		declaringSimpleName,	
		returnQualification, 
		returnSimpleName,
		parameterQualifications, 
		parameterSimpleNames,
		null,
		matchRule);

	// Store type signature and arguments for declaring type
	if (declaringSignature != null) {
		typeSignatures = Util.splitTypeLevelsSignature(declaringSignature);
		setTypeArguments(Util.getAllTypeArguments(typeSignatures));
	}

	// Store type signatures and arguments for return type
	if (returnSignature != null) {
		returnTypeSignatures = Util.splitTypeLevelsSignature(returnSignature);
		returnTypeArguments = Util.getAllTypeArguments(returnTypeSignatures);
	}

	// Store type signatures and arguments for method parameters type
	if (parameterSignatures != null) {
		int length = parameterSignatures.length;
		if (length > 0) {
			parametersTypeSignatures = new char[length][][];
			parametersTypeArguments = new char[length][][][];
			for (int i=0; i<length; i++) {
				parametersTypeSignatures[i] = Util.splitTypeLevelsSignature(parameterSignatures[i]);
				parametersTypeArguments[i] = Util.getAllTypeArguments(parametersTypeSignatures[i]);
			}
		}
	}

	// Store type signatures and arguments for method
	methodArguments = arguments;
	if (hasMethodArguments())  ((InternalSearchPattern)this).mustResolve = true;
}

public SearchPattern getBlankPattern() {
	return new MethodPattern(R_EXACT_MATCH | R_CASE_SENSITIVE);
}
public char[][] getIndexCategories() {
	if (this.findReferences)
		return this.findDeclarations ? REF_AND_DECL_CATEGORIES : REF_CATEGORIES;
	if (this.findDeclarations)
		return DECL_CATEGORIES;
	return CharOperation.NO_CHAR_CHAR;
}
boolean hasMethodArguments() {
	return methodArguments != null && methodArguments.length > 0;
}
boolean hasMethodParameters() {
	return methodParameters;
}
boolean isPolymorphicSearch() {
	return this.findReferences;
}
public boolean matchesDecodedKey(SearchPattern decodedPattern) {
	MethodPattern pattern = (MethodPattern) decodedPattern;

	return (this.parameterCount == pattern.parameterCount || this.parameterCount == -1 || this.varargs)
		&& matchesName(this.selector, pattern.selector);
}
/**
 * Returns whether a method declaration or message send must be resolved to 
 * find out if this method pattern matches it.
 */
protected boolean mustResolve() {
	// declaring type
	// If declaring type is specified - even with simple name - always resolves
	if (declaringSimpleName != null || declaringQualification != null) return true;

	// return type
	// If return type is specified - even with simple name - always resolves
	if (returnSimpleName != null || returnQualification != null) return true;

	// parameter types
	if (parameterSimpleNames != null)
		for (int i = 0, max = parameterSimpleNames.length; i < max; i++)
			if (parameterQualifications[i] != null) return true;
	return false;
}
EntryResult[] queryIn(Index index) throws IOException {
	char[] key = this.selector; // can be null
	int matchRule = getMatchRule();

	switch(getMatchMode()) {
		case R_EXACT_MATCH :
			if (this.isCamelCase) break;
			if (this.selector != null && this.parameterCount >= 0 && !this.varargs)
				// TODO JDT search method pattern
				// key = createIndexKey(this.selector, this.parameterCount);
				key = CharOperation.NO_CHAR;
			else { // do a prefix query with the selector
				matchRule &= ~R_EXACT_MATCH;
				matchRule |= R_PREFIX_MATCH;
			}
			break;
		case R_PREFIX_MATCH :
			// do a prefix query with the selector
			break;
		case R_PATTERN_MATCH :
			if (this.parameterCount >= 0 && !this.varargs)
				// TODO JDT search method pattern
				// key = createIndexKey(this.selector == null ? ONE_STAR : this.selector, this.parameterCount);
				key = CharOperation.NO_CHAR; 
			else if (this.selector != null && this.selector[this.selector.length - 1] != '*')
				key = CharOperation.concat(this.selector, ONE_STAR, SEPARATOR);
			// else do a pattern query with just the selector
			break;
		case R_REGEXP_MATCH :
			// TODO (frederic) implement regular expression match
			break;
	}

	return index.query(getIndexCategories(), key, matchRule); // match rule is irrelevant when the key is null
}
protected StringBuffer print(StringBuffer output) {
	if (this.findDeclarations) {
		output.append(this.findReferences
			? "MethodCombinedPattern: " //$NON-NLS-1$
			: "MethodDeclarationPattern: "); //$NON-NLS-1$
	} else {
		output.append("MethodReferencePattern: "); //$NON-NLS-1$
	}
	if (declaringQualification != null)
		output.append(declaringQualification).append('.');
	if (declaringSimpleName != null) 
		output.append(declaringSimpleName).append('.');
	else if (declaringQualification != null)
		output.append("*."); //$NON-NLS-1$

	if (selector != null)
		output.append(selector);
	else
		output.append("*"); //$NON-NLS-1$
	output.append('(');
	if (parameterSimpleNames == null) {
		output.append("..."); //$NON-NLS-1$
	} else {
		for (int i = 0, max = parameterSimpleNames.length; i < max; i++) {
			if (i > 0) output.append(", "); //$NON-NLS-1$
			if (parameterQualifications[i] != null) output.append(parameterQualifications[i]).append('.');
			if (parameterSimpleNames[i] == null) output.append('*'); else output.append(parameterSimpleNames[i]);
		}
	}
	output.append(')');
	if (returnQualification != null) 
		output.append(" --> ").append(returnQualification).append('.'); //$NON-NLS-1$
	else if (returnSimpleName != null)
		output.append(" --> "); //$NON-NLS-1$
	if (returnSimpleName != null) 
		output.append(returnSimpleName);
	else if (returnQualification != null)
		output.append("*"); //$NON-NLS-1$
	return super.print(output);
}
}
