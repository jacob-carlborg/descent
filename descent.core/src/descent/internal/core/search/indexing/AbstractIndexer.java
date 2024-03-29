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
package descent.internal.core.search.indexing;

import descent.core.compiler.CharOperation;
import descent.core.search.SearchDocument;
import descent.internal.compiler.lookup.TypeConstants;
import descent.internal.core.search.matching.ConstructorPattern;
import descent.internal.core.search.matching.FieldPattern;
import descent.internal.core.search.matching.MethodPattern;
import descent.internal.core.search.matching.SuperTypeReferencePattern;
import descent.internal.core.search.matching.TypeDeclarationPattern;

public abstract class AbstractIndexer implements IIndexConstants {

	SearchDocument document;

	public AbstractIndexer(SearchDocument document) {
		this.document = document;
	}
//	public void addAnnotationTypeDeclaration(long modifiers, char[] packageName, char[] name, char[][] enclosingTypeNames, boolean secondary) {
//		char[] indexKey = TypeDeclarationPattern.createIndexKey(modifiers, name, packageName, enclosingTypeNames, secondary);
//		addIndexEntry(TYPE_DECL, indexKey);
//		
//		addIndexEntry(
//			SUPER_REF, 
//			SuperTypeReferencePattern.createIndexKey(
//				modifiers, packageName, name, enclosingTypeNames, null, ANNOTATION_TYPE_SUFFIX, CharOperation.concatWith(TypeConstants.JAVA_LANG_ANNOTATION_ANNOTATION, '.'), ANNOTATION_TYPE_SUFFIX));
//	}	
	public void addClassDeclaration(
			long modifiers, 
			char[] packageName,
			char[] name, 
			char[][] enclosingTypeNames, 
			char[][] superinterfaces,
			char[][] typeParameterSignatures,
			int declarationStart) {
		char[] indexKey = TypeDeclarationPattern.createIndexKey(modifiers, name, packageName, CharOperation.concat(typeParameterSignatures), enclosingTypeNames, declarationStart);
		addIndexEntry(TYPE_DECL, indexKey);

//		if (superclass != null) {
//			superclass = erasure(superclass);
//			addTypeReference(superclass);
//		}
//		addIndexEntry(
//			SUPER_REF, 
//			SuperTypeReferencePattern.createIndexKey(
//				modifiers, packageName, name, enclosingTypeNames, typeParameterSignatures, CLASS_SUFFIX, superclass, CLASS_SUFFIX));
		if (superinterfaces != null) {
			for (int i = 0, max = superinterfaces.length; i < max; i++) {
				char[] superinterface = erasure(superinterfaces[i]);
				addTypeReference(superinterface);
				addIndexEntry(
					SUPER_REF,
					SuperTypeReferencePattern.createIndexKey(
						modifiers, packageName, name, enclosingTypeNames, typeParameterSignatures, CLASS_SUFFIX, superinterface, INTERFACE_SUFFIX));
			}
		}
	}
	private char[] erasure(char[] typeName) {
		// TODO JDT signature
//		int genericStart = CharOperation.indexOf(Signature.C_GENERIC_START, typeName);
//		if (genericStart > -1) 
//			typeName = CharOperation.subarray(typeName, 0, genericStart);
		return typeName;
	}
	public void addConstructorDeclaration(char[] typeName, char[][] parameterTypes) {
		int argCount = parameterTypes == null ? 0 : parameterTypes.length;
		addIndexEntry(CONSTRUCTOR_DECL, ConstructorPattern.createIndexKey(CharOperation.lastSegment(typeName,'.'), argCount));
	
		if (parameterTypes != null) {
			for (int i = 0; i < argCount; i++)
				addTypeReference(parameterTypes[i]);
		}
//		if (exceptionTypes != null)
//			for (int i = 0, max = exceptionTypes.length; i < max; i++)
//				addTypeReference(exceptionTypes[i]);
	}
	public void addConstructorReference(char[] typeName, int argCount) {
		char[] simpleTypeName = CharOperation.lastSegment(typeName,'.');
		addTypeReference(simpleTypeName);
		addIndexEntry(CONSTRUCTOR_REF, ConstructorPattern.createIndexKey(simpleTypeName, argCount));
		char[] innermostTypeName = CharOperation.lastSegment(simpleTypeName,'$');
		if (innermostTypeName != simpleTypeName)
			addIndexEntry(CONSTRUCTOR_REF, ConstructorPattern.createIndexKey(innermostTypeName, argCount));
	}
	public void addEnumDeclaration(long modifiers, char[] packageName, char[] name, char[][] enclosingTypeNames, char[][] superinterfaces, int declarationStart) {
		char[] indexKey = TypeDeclarationPattern.createIndexKey(modifiers, name, packageName, null, enclosingTypeNames, declarationStart);
		addIndexEntry(TYPE_DECL, indexKey);

		addIndexEntry(
			SUPER_REF, 
			SuperTypeReferencePattern.createIndexKey(
				modifiers, packageName, name, enclosingTypeNames, null, ENUM_SUFFIX, CharOperation.concatWith(TypeConstants.JAVA_LANG_ENUM, '.'), CLASS_SUFFIX));
		if (superinterfaces != null) {
			for (int i = 0, max = superinterfaces.length; i < max; i++) {
				char[] superinterface = erasure(superinterfaces[i]);
				addTypeReference(superinterface);
				addIndexEntry(
					SUPER_REF,
					SuperTypeReferencePattern.createIndexKey(
						modifiers, packageName, name, enclosingTypeNames, null, ENUM_SUFFIX, superinterface, INTERFACE_SUFFIX));
			}
		}
	}	
	public void addFieldDeclaration(long modifiers, char[] packageName, char[][] enclosingTypeNames, char[] typeName, char[] fieldName, int declarationStart) {
		addIndexEntry(FIELD_DECL, FieldPattern.createIndexKey(modifiers, packageName, enclosingTypeNames, fieldName, typeName, declarationStart));
		addTypeReference(typeName);
	}
	public void addFieldReference(char[] fieldName) {
		addNameReference(fieldName);
	}
	protected void addIndexEntry(char[] category, char[] key) {
		this.document.addIndexEntry(category, key);
	}
	public void addInterfaceDeclaration(long modifiers, char[] packageName, char[] name, char[][] enclosingTypeNames, char[][] superinterfaces, char[][] typeParameterSignatures, int declarationStart) {
		char[] indexKey = TypeDeclarationPattern.createIndexKey(modifiers, name, packageName, CharOperation.concat(typeParameterSignatures), enclosingTypeNames, declarationStart);
		addIndexEntry(TYPE_DECL, indexKey);

		if (superinterfaces != null) {
			for (int i = 0, max = superinterfaces.length; i < max; i++) {
				char[] superinterface = erasure(superinterfaces[i]);
				addTypeReference(superinterface);
				addIndexEntry(
					SUPER_REF,
					SuperTypeReferencePattern.createIndexKey(
						modifiers, packageName, name, enclosingTypeNames, typeParameterSignatures, INTERFACE_SUFFIX, superinterface, INTERFACE_SUFFIX));
			}
		}
	}
	public void addMethodDeclaration(long modifiers, char[] packageName, char[] methodName, char[][] enclosingTypeNames, char[][] parameterTypes, char[][] typeParameters, char[] signature, int declarationStart) {
		int argCount = parameterTypes == null ? 0 : parameterTypes.length;
		addIndexEntry(METHOD_DECL, MethodPattern.createIndexKey(modifiers, packageName, enclosingTypeNames, methodName, signature, CharOperation.concat(typeParameters), argCount, declarationStart));
	
		if (parameterTypes != null) {
			for (int i = 0; i < argCount; i++)
				addTypeReference(parameterTypes[i]);
		}
//		if (exceptionTypes != null)
//			for (int i = 0, max = exceptionTypes.length; i < max; i++)
//				addTypeReference(exceptionTypes[i]);
		if (signature != null)
			addTypeReference(signature);
	}
	public void addMethodReference(char[] methodName, int argCount) {
		addIndexEntry(METHOD_REF, MethodPattern.createIndexKey(0, null, null, methodName, null, null, argCount, 0));
	}
	public void addNameReference(char[] name) {
		addIndexEntry(REF, name);
	}
	public void addTypeReference(char[] typeName) {
		if (typeName == null) {
			return;
		}
		
		addNameReference(CharOperation.lastSegment(typeName, '.'));
	}
	public void addVersion(char[] displayString) {
		// TODO source indexer
	}
	public void addDebug(char[] displayString) {
		// TODO source indexer
	}
	public abstract void indexDocument();
}
