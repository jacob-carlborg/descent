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

import descent.core.compiler.CharOperation;
import descent.core.search.SearchPattern;
import descent.internal.core.index.EntryResult;
import descent.internal.core.index.Index;
import descent.internal.core.search.indexing.IIndexConstants;

public class MultiTypeDeclarationPattern extends JavaSearchPattern implements IIndexConstants {

public char[][] simpleNames;
public char[][] qualifications;

// set to CLASS_SUFFIX for only matching classes 
// set to INTERFACE_SUFFIX for only matching interfaces
// set to ENUM_SUFFIX for only matching enums
// set to ANNOTATION_TYPE_SUFFIX for only matching annotation types
// set to TYPE_SUFFIX for matching both classes and interfaces
public char typeSuffix;

protected static char[][] CATEGORIES = { TYPE_DECL };

public MultiTypeDeclarationPattern(
	char[][] qualifications,
	char[][] simpleNames,
	char typeSuffix,
	int matchRule) {

	this(matchRule);

	if (isCaseSensitive() || qualifications == null) {
		this.qualifications = qualifications;
	} else {
		int length = qualifications.length;
		this.qualifications = new char[length][];
		for (int i = 0; i < length; i++)
			this.qualifications[i] = CharOperation.toLowerCase(qualifications[i]);
	}
	if (simpleNames == null) {
		this.simpleNames = CharOperation.NO_CHAR_CHAR;
	} else if ((isCaseSensitive() || isCamelCase()) ) {
		this.simpleNames = simpleNames;
	} else {
		int length = simpleNames.length;
		this.simpleNames = new char[length][];
		for (int i = 0; i < length; i++)
			this.simpleNames[i] = CharOperation.toLowerCase(simpleNames[i]);
	}
	this.typeSuffix = typeSuffix;

	((InternalSearchPattern)this).mustResolve = typeSuffix != TYPE_SUFFIX; // only used to report type declarations, not their positions
}
MultiTypeDeclarationPattern(int matchRule) {
	super(TYPE_DECL_PATTERN, matchRule);
}
public SearchPattern getBlankPattern() {
	return new QualifiedTypeDeclarationPattern(R_EXACT_MATCH | R_CASE_SENSITIVE);
}
public char[][] getIndexCategories() {
	return CATEGORIES;
}
public boolean matchesDecodedKey(SearchPattern decodedPattern) {
	QualifiedTypeDeclarationPattern pattern = (QualifiedTypeDeclarationPattern) decodedPattern;
	switch(this.typeSuffix) {
		case CLASS_SUFFIX :
			switch (pattern.typeSuffix) {
				case CLASS_SUFFIX :
				case CLASS_AND_INTERFACE_SUFFIX :
				case CLASS_AND_ENUM_SUFFIX :
					break;
				default:
					return false;
			}
			break;
		case INTERFACE_SUFFIX :
			switch (pattern.typeSuffix) {
				case INTERFACE_SUFFIX :
				case CLASS_AND_INTERFACE_SUFFIX :
					break;
				default:
					return false;
			}
			break;
		case ENUM_SUFFIX :
			switch (pattern.typeSuffix) {
				case ENUM_SUFFIX :
				case CLASS_AND_ENUM_SUFFIX :
					break;
				default:
					return false;
			}
			break;
		case ANNOTATION_TYPE_SUFFIX :
			if (this.typeSuffix != pattern.typeSuffix) return false;
			break;
		case CLASS_AND_INTERFACE_SUFFIX :
			switch (pattern.typeSuffix) {
				case CLASS_SUFFIX :
				case INTERFACE_SUFFIX :
				case CLASS_AND_INTERFACE_SUFFIX :
					break;
				default:
					return false;
			}
			break;
		case CLASS_AND_ENUM_SUFFIX :
			switch (pattern.typeSuffix) {
				case CLASS_SUFFIX :
				case ENUM_SUFFIX :
				case CLASS_AND_ENUM_SUFFIX :
					break;
				default:
					return false;
			}
			break;
	}

	if (this.qualifications != null) {
		int count = 0;
		int max = this.qualifications.length;
		for (; count < max; count++)
			if (matchesName(this.qualifications[count], pattern.qualification))
				break;
		if (count == max) return false;
	}

	int count = 0;
	int max = this.simpleNames.length;
	for (; count < max; count++)
		if (matchesName(this.simpleNames[count], pattern.simpleName))
			break;
	return count < max;
}
EntryResult[] queryIn(Index index) throws IOException {
	int count = -1;
	int numOfNames = this.simpleNames.length;
	EntryResult[][] allResults = numOfNames > 1 ? new EntryResult[numOfNames][] : null;
	for (int i = 0; i < numOfNames; i++) {
		char[] key = this.simpleNames[i];
		int matchRule = getMatchRule();

		switch(getMatchMode()) {
			case R_PREFIX_MATCH :
				// do a prefix query with the simpleName
				break;
			case R_EXACT_MATCH :
				if (!this.isCamelCase) {
					// do a prefix query with the simpleName
					matchRule &= ~R_EXACT_MATCH;
					matchRule |= R_PREFIX_MATCH;
					key = CharOperation.append(key, SEPARATOR);
				}
				break;
			case R_PATTERN_MATCH :
				if (key[key.length - 1] != '*')
					key = CharOperation.concat(key, ONE_STAR, SEPARATOR);
				break;
			case R_REGEXP_MATCH :
				// TODO (frederic) implement regular expression match
				break;
		}

		EntryResult[] entries = index.query(getIndexCategories(), key, matchRule); // match rule is irrelevant when the key is null
		if (entries != null) {
			if (allResults == null) return entries;
			allResults[++count] = entries;
		}
	}

	if (count == -1) return null;
	int total = 0;
	for (int i = 0; i <= count; i++)
		total += allResults[i].length;
	EntryResult[] allEntries = new EntryResult[total];
	int next = 0;
	for (int i = 0; i <= count; i++) {
		EntryResult[] entries = allResults[i];
		System.arraycopy(entries, 0, allEntries, next, entries.length);
		next += entries.length;
	}
	return allEntries;
}
protected StringBuffer print(StringBuffer output) {
	switch (this.typeSuffix){
		case CLASS_SUFFIX :
			output.append("MultiClassDeclarationPattern: "); //$NON-NLS-1$
			break;
		case CLASS_AND_INTERFACE_SUFFIX :
			output.append("MultiClassAndInterfaceDeclarationPattern: "); //$NON-NLS-1$
			break;
		case CLASS_AND_ENUM_SUFFIX :
			output.append("MultiClassAndEnumDeclarationPattern: "); //$NON-NLS-1$
			break;
		case INTERFACE_SUFFIX :
			output.append("MultiInterfaceDeclarationPattern: "); //$NON-NLS-1$
			break;
		case ENUM_SUFFIX :
			output.append("MultiEnumDeclarationPattern: "); //$NON-NLS-1$
			break;
		case ANNOTATION_TYPE_SUFFIX :
			output.append("MultiAnnotationTypeDeclarationPattern: "); //$NON-NLS-1$
			break;
		default :
			output.append("MultiTypeDeclarationPattern: "); //$NON-NLS-1$
			break;
	}
	if (qualifications != null) {
		output.append("qualifications: <"); //$NON-NLS-1$
		for (int i = 0; i < qualifications.length; i++){
			output.append(qualifications[i]);
			if (i < qualifications.length - 1)
				output.append(", "); //$NON-NLS-1$
		}
		output.append("> "); //$NON-NLS-1$
	}
	if (simpleNames != null) {
		output.append("simpleNames: <"); //$NON-NLS-1$
		for (int i = 0; i < simpleNames.length; i++){
			output.append(simpleNames[i]);
			if (i < simpleNames.length - 1)
				output.append(", "); //$NON-NLS-1$
		}
		output.append(">"); //$NON-NLS-1$
	}
	return super.print(output);
}
}
