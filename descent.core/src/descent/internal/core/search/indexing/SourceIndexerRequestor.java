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

import java.util.Stack;

import descent.core.Flags;
import descent.core.Signature;
import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.internal.compiler.ISourceElementRequestor;
import descent.internal.core.search.processing.JobManager;

/**
 * This class is used by the JavaParserIndexer. When parsing the java file, the requestor
 * recognises the java elements (methods, fields, ...) and add them to an index.
 */
public class SourceIndexerRequestor implements ISourceElementRequestor, IIndexConstants {
	SourceIndexer indexer;

	char[] packageName = CharOperation.NO_CHAR;
	char[][] enclosingTypeNames = new char[5][];
	int depth = 0;
	int methodDepth = 0;
	
	Stack<Long> initializerModifiersStack = new Stack<Long>();
	
public SourceIndexerRequestor(SourceIndexer indexer) {
	this.indexer = indexer;
}
/**
 * @see ISourceElementRequestor#acceptConstructorReference(char[], int, int)
 */
public void acceptConstructorReference(char[] typeName, int argCount, int sourcePosition) {
	// TODO JDT signature
//	if (CharOperation.indexOf(Signature.C_GENERIC_START, typeName) > 0) {
//		typeName = Signature.toCharArray(Signature.getTypeErasure(Signature.createTypeSignature(typeName, false)).toCharArray());
//	}
	this.indexer.addConstructorReference(typeName, argCount);
	int lastDot = CharOperation.lastIndexOf('.', typeName);
	if (lastDot != -1) {
		char[][] qualification = CharOperation.splitOn('.', CharOperation.subarray(typeName, 0, lastDot));
		for (int i = 0, length = qualification.length; i < length; i++) {
			this.indexer.addNameReference(qualification[i]);
		}
	}
}
/**
 * @see ISourceElementRequestor#acceptFieldReference(char[], int)
 */
public void acceptFieldReference(char[] fieldName, int sourcePosition) {
	this.indexer.addFieldReference(fieldName);
}
public void acceptImport(int declarationStart, int declarationEnd, String name, String alias, String[] selectiveImportsNames,  String[] selectiveImportsAliases, long modifiers) {
//	 imports have already been reported while creating the ImportRef node (see SourceElementParser#comsume*ImportDeclarationName() methods)
}
/**
 * @see ISourceElementRequestor#acceptLineSeparatorPositions(int[])
 */
public void acceptLineSeparatorPositions(int[] positions) {
	// implements interface method
}
/**
 * @see ISourceElementRequestor#acceptMethodReference(char[], int, int)
 */
public void acceptMethodReference(char[] methodName, int argCount, int sourcePosition) {
	this.indexer.addMethodReference(methodName, argCount);
}
/**
 * @see ISourceElementRequestor#acceptPackage(int, int, char[])
 */
public void acceptPackage(int declarationStart, int declarationEnd, char[] name, boolean safe) {
	this.packageName = name;
}
/**
 * @see ISourceElementRequestor#acceptProblem(CategorizedProblem)
 */
public void acceptProblem(IProblem problem) {
	// implements interface method
}
/**
 * @see ISourceElementRequestor#acceptTypeReference(char[][], int, int)
 */
public void acceptTypeReference(char[][] typeName, int sourceStart, int sourceEnd) {
	int length = typeName.length;
	for (int i = 0; i < length - 1; i++)
		acceptUnknownReference(typeName[i], 0); // ?
	acceptTypeReference(typeName[length - 1], 0);
}
/**
 * @see ISourceElementRequestor#acceptTypeReference(char[], int)
 */
public void acceptTypeReference(char[] simpleTypeName, int sourcePosition) {
	this.indexer.addTypeReference(simpleTypeName);
}
/**
 * @see ISourceElementRequestor#acceptUnknownReference(char[][], int, int)
 */
public void acceptUnknownReference(char[][] name, int sourceStart, int sourceEnd) {
	for (int i = 0; i < name.length; i++) {
		acceptUnknownReference(name[i], 0);
	}
}
/**
 * @see ISourceElementRequestor#acceptUnknownReference(char[], int)
 */
public void acceptUnknownReference(char[] name, int sourcePosition) {
	this.indexer.addNameReference(name);
}
/*
 * Rebuild the proper qualification for the current source type:
 *
 * java.lang.Object ---> null
 * java.util.Hashtable$Entry --> [Hashtable]
 * x.y.A$B$C --> [A, B]
 */
public char[][] enclosingTypeNames(){

	if (depth == 0) return null;

	char[][] qualification = new char[this.depth][];
	System.arraycopy(this.enclosingTypeNames, 0, qualification, 0, this.depth);
	return qualification;
}
//private void enterAnnotationType(TypeInfo typeInfo) {
//	char[][] typeNames;
//	if (this.methodDepth > 0) {
//		typeNames = ONE_ZERO_CHAR;
//	} else {
//		typeNames = this.enclosingTypeNames();
//	}
//	this.indexer.addAnnotationTypeDeclaration(typeInfo.modifiers, packageName, typeInfo.name, typeNames, typeInfo.secondary);
//	this.pushTypeName(typeInfo.name);	
//}

private void enterClass(TypeInfo typeInfo) {

	// eliminate possible qualifications, given they need to be fully resolved again
//	if (typeInfo.superclass != null) {
//		typeInfo.superclass = getSimpleName(typeInfo.superclass);
//		
//		// add implicit constructor reference to default constructor
//		this.indexer.addConstructorReference(typeInfo.superclass, 0);
//	}
	if (typeInfo.superinterfaces != null){
		for (int i = 0, length = typeInfo.superinterfaces.length; i < length; i++) {
			typeInfo.superinterfaces[i] = getSimpleName(typeInfo.superinterfaces[i]);
		}
	}
	char[][] typeNames;
	if (this.methodDepth > 0) {
		typeNames = ONE_ZERO_CHAR;
	} else {
		typeNames = this.enclosingTypeNames();
	}
	char[][] typeParameterSignatures = null;
	if (typeInfo.typeParameters != null) {
		int typeParametersLength = typeInfo.typeParameters.length;
		typeParameterSignatures = new char[typeParametersLength][];
		for (int i = 0; i < typeParametersLength; i++) {
			ISourceElementRequestor.TypeParameterInfo typeParameterInfo = typeInfo.typeParameters[i];
//			typeParameterSignatures[i] = Signature.createTypeParameterSignature(typeParameterInfo.name, CharOperation.NO_CHAR_CHAR);
			typeParameterSignatures[i] = typeParameterInfo.signature;
		}
	}
	this.indexer.addClassDeclaration(typeInfo.modifiers, this.packageName, typeInfo.name, typeNames, typeInfo.superinterfaces, typeParameterSignatures, typeInfo.declarationStart);
	this.pushTypeName(typeInfo.name);
}
/**
 * @see ISourceElementRequestor#enterCompilationUnit()
 */
public void enterCompilationUnit() {
	// implements interface method
}
/**
 * @see ISourceElementRequestor#enterConstructor(MethodInfo)
 */
public void enterConstructor(MethodInfo methodInfo) {
	this.indexer.addConstructorDeclaration(methodInfo.name, methodInfo.parameterTypes);
	this.methodDepth++;
}
private void enterEnum(TypeInfo typeInfo) {
	// eliminate possible qualifications, given they need to be fully resolved again
	if (typeInfo.superinterfaces != null){
		for (int i = 0, length = typeInfo.superinterfaces.length; i < length; i++){
			typeInfo.superinterfaces[i] = getSimpleName(typeInfo.superinterfaces[i]);
		}
	}	
	char[][] typeNames;
	if (this.methodDepth > 0) {
		typeNames = ONE_ZERO_CHAR;
	} else {
		typeNames = this.enclosingTypeNames();
	}
	this.indexer.addEnumDeclaration(typeInfo.modifiers, packageName, typeInfo.name, typeNames, typeInfo.superinterfaces, typeInfo.declarationStart);
	this.pushTypeName(typeInfo.name);	
}
/**
 * @see ISourceElementRequestor#enterField(FieldInfo)
 */
public void enterField(FieldInfo fieldInfo) {
	char[][] typeNames;
	if (this.methodDepth > 0) {
		typeNames = ONE_ZERO_CHAR;
	} else {
		typeNames = this.enclosingTypeNames();
	}
	this.indexer.addFieldDeclaration(fieldInfo.modifiers, this.packageName, typeNames, fieldInfo.type, fieldInfo.name, fieldInfo.declarationStart);
	this.methodDepth++;
}
/**
 * @see ISourceElementRequestor#enterInitializer(int, int)
 */
public void enterInitializer(int declarationSourceStart, long modifiers, char[] displayString) {
	initializerModifiersStack.push(modifiers);
	
	if (!increasesMethodDepth(modifiers)) {
		return;
	}
	
	if ((modifiers & Flags.AccVersionAssignment) != 0) {
		this.indexer.addVersion(displayString);
	}
	
	this.methodDepth++;
}
private void enterInterface(TypeInfo typeInfo) {
	// eliminate possible qualifications, given they need to be fully resolved again
	if (typeInfo.superinterfaces != null){
		for (int i = 0, length = typeInfo.superinterfaces.length; i < length; i++){
			typeInfo.superinterfaces[i] = getSimpleName(typeInfo.superinterfaces[i]);
		}
	}	
	char[][] typeNames;
	if (this.methodDepth > 0) {
		typeNames = ONE_ZERO_CHAR;
	} else {
		typeNames = this.enclosingTypeNames();
	}
	char[][] typeParameterSignatures = null;
	if (typeInfo.typeParameters != null) {
		int typeParametersLength = typeInfo.typeParameters.length;
		typeParameterSignatures = new char[typeParametersLength][];
		for (int i = 0; i < typeParametersLength; i++) {
			ISourceElementRequestor.TypeParameterInfo typeParameterInfo = typeInfo.typeParameters[i];
//			typeParameterSignatures[i] = Signature.createTypeParameterSignature(typeParameterInfo.name, CharOperation.NO_CHAR_CHAR);
			typeParameterSignatures[i] = typeParameterInfo.signature;
		}
	}
	this.indexer.addInterfaceDeclaration(typeInfo.modifiers, packageName, typeInfo.name, typeNames, typeInfo.superinterfaces, typeParameterSignatures, typeInfo.declarationStart);
	this.pushTypeName(typeInfo.name);	
}
/**
 * @see ISourceElementRequestor#enterMethod(MethodInfo)
 */
public void enterMethod(MethodInfo methodInfo) {
	char[][] typeNames;
	if (this.methodDepth > 0) {
		typeNames = ONE_ZERO_CHAR;
	} else {
		typeNames = this.enclosingTypeNames();
	}
	
	char[][] typeParameterSignatures = null;
	if (methodInfo.typeParameters != null) {
		int typeParametersLength = methodInfo.typeParameters.length;
		typeParameterSignatures = new char[typeParametersLength][];
		for (int i = 0; i < typeParametersLength; i++) {
			ISourceElementRequestor.TypeParameterInfo typeParameterInfo =methodInfo.typeParameters[i];
//			typeParameterSignatures[i] = Signature.createTypeParameterSignature(typeParameterInfo.name, CharOperation.NO_CHAR_CHAR);
			typeParameterSignatures[i] = typeParameterInfo.signature;
		}
	}
	
	this.indexer.addMethodDeclaration(methodInfo.modifiers, packageName, methodInfo.name, typeNames, methodInfo.parameterTypes, typeParameterSignatures, methodInfo.signature, methodInfo.declarationStart);
	this.methodDepth++;
}
/**
 * @see ISourceElementRequestor#enterType(TypeInfo)
 */
public void enterType(TypeInfo typeInfo) {
	// TODO (jerome) might want to merge the 4 methods
	switch((int)(typeInfo.modifiers & (Flags.AccInterface | Flags.AccStruct | Flags.AccUnion | Flags.AccTemplate | Flags.AccEnum))) {
	case Flags.AccEnum:
		enterEnum(typeInfo);
		break;
	case Flags.AccInterface:
		enterInterface(typeInfo);
		break;
		/* TODO JDT search PRIORITY
	case Flags.AccStruct:
		enterStruct(typeInfo);
		break;
	case Flags.AccUnion:
		enterUnion(typeInfo);
		break;
	case Flags.AccTemplate:
		enterTemplate(typeInfo);
		break;
		*/
	default:
		enterClass(typeInfo);
		break;
	}
}

/**
 * @see ISourceElementRequestor#exitCompilationUnit(int)
 */
public void exitCompilationUnit(int declarationEnd) {
	// implements interface method
}
/**
 * @see ISourceElementRequestor#exitConstructor(int)
 */
public void exitConstructor(int declarationEnd) {
	this.methodDepth--;
}
/**
 * @see ISourceElementRequestor#exitField(int, int, int)
 */
public void exitField(int initializationStart, int declarationEnd, int declarationSourceEnd) {
	this.methodDepth--;
}
/**
 * @see ISourceElementRequestor#exitInitializer(int)
 */
public void exitInitializer(int declarationEnd) {
	long modifiers = initializerModifiersStack.pop();
	if (!increasesMethodDepth(modifiers)) {
		return;
	}
	
	this.methodDepth--;
}
/**
 * @see ISourceElementRequestor#exitMethod(int, int, int)
 */
public void exitMethod(int declarationEnd, int defaultValueStart, int defaultValueEnd) {
	this.methodDepth--;
}
/**
 * @see ISourceElementRequestor#exitType(int)
 */
public void exitType(int declarationEnd) {
	popTypeName();
}
/*
 * Returns the unqualified name without parameters from the given type name.
 */
private char[] getSimpleName(char[] typeName) {
	return Signature.toCharArray(typeName, false);
//	int lastDot = -1, lastGenericStart = -1;
//	int depthCount = 0;
//	int length = typeName.length;
//	lastDotLookup: for (int i = length -1; i >= 0; i--) {
//		switch (typeName[i]) {
//			case '.':
//				if (depthCount == 0) {
//					lastDot = i;
//					break lastDotLookup;
//				}
//				break;
//			case '<':
//				depthCount--;
//				if (depthCount == 0) lastGenericStart = i;
//				break;
//			case '>':
//				depthCount++;
//				break;
//		}
//	}
//	if (lastGenericStart < 0) {
//		if (lastDot < 0) {
//			return typeName;
//		}
//		return  CharOperation.subarray(typeName, lastDot + 1, length);
//	}
//	return  CharOperation.subarray(typeName, lastDot + 1, lastGenericStart);
}
public void popTypeName() {
	if (depth > 0) {
		enclosingTypeNames[--depth] = null;
	} else if (JobManager.VERBOSE) {
		// dump a trace so it can be tracked down
		try {
			enclosingTypeNames[-1] = null;
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}
}
public void pushTypeName(char[] typeName) {
	if (depth == enclosingTypeNames.length)
		System.arraycopy(enclosingTypeNames, 0, enclosingTypeNames = new char[depth*2][], 0, depth);
	enclosingTypeNames[depth++] = typeName;
}

// TODO JDT check if this must be implemented
public void enterConditional(int declarationStart, long modifiers, char[] displayString) {
	
}

public void enterConditionalThen(int declarationStart) {
	// TODO Auto-generated method stub
	
}

public void enterConditionalElse(int declarationStart) {
	// TODO Auto-generated method stub
	
}

public void exitConditional(int declarationEnd) {
	// TODO Auto-generated method stub
	
}

public void exitConditionalThen(int declarationEnd) {
	// TODO Auto-generated method stub
	
}

public void exitConditionalElse(int declarationEnd) {
	// TODO Auto-generated method stub
	
}

private boolean increasesMethodDepth(long modifiers) {
	if ((modifiers & 
			(Flags.AccAlign | Flags.AccExternDeclaration | Flags.AccPragma | Flags.AccThen | Flags.AccElse))
		!= 0) {
		return false;
	} else {
		return true;
	}
}

}
