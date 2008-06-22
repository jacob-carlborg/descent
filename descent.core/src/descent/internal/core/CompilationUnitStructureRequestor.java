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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.core.runtime.Assert;
import descent.core.*;
import descent.core.compiler.IProblem;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.ISourceElementRequestor;
import descent.internal.compiler.parser.HashtableOfCharArrayAndObject;
import descent.internal.compiler.util.HashtableOfObject;
import descent.internal.core.util.ReferenceInfoAdapter;

/**
 * A requestor for the fuzzy parser, used to compute the children of an ICompilationUnit.
 */
public class CompilationUnitStructureRequestor extends ReferenceInfoAdapter implements ISourceElementRequestor {
	
	/**
	 * The name of an annoymous enum, struct or union.
	 */
	private final static char[] ANNONYMOUS_NAME = { ' ' };

	/**
	 * The handle to the compilation unit being parsed
	 */
	protected ICompilationUnit unit;

	/**
	 * The info object for the compilation unit being parsed
	 */
	protected CompilationUnitElementInfo unitInfo;

	/**
	 * The import container info - null until created
	 */
	protected JavaElementInfo importContainerInfo = null;

	/**
	 * Hashtable of children elements of the compilation unit.
	 * Children are added to the table as they are found by
	 * the parser. Keys are handles, values are corresponding
	 * info objects.
	 */
	protected Map newElements;

	/**
	 * Stack of parent scope info objects. The info on the
	 * top of the stack is the parent of the next element found.
	 * For example, when we locate a method, the parent info object
	 * will be the type the method is contained in.
	 */
	protected Stack infoStack;
	
	/*
	 * Map from JavaElementInfo to of ArrayList of IJavaElement representing the children 
	 * of the given info.
	 */
	protected HashMap children;

	/**
	 * Stack of parent handles, corresponding to the info stack. We
	 * keep both, since info objects do not have back pointers to
	 * handles.
	 */
	protected Stack handleStack;

	/**
	 * The number of references reported thus far. Used to
	 * expand the arrays of reference kinds and names.
	 */
	protected int referenceCount= 0;

	/**
	 * Problem requestor which will get notified of discovered problems
	 */
	protected boolean hasSyntaxErrors = false;
	
	/*
	 * The source this requestor is using.
	 */
	protected char[] source;
	
	/**
	 * Empty collections used for efficient initialization
	 */
	protected static String[] NO_STRINGS = new String[0];
	protected static byte[] NO_BYTES= new byte[]{};

	protected HashtableOfObject fieldRefCache;
	protected HashtableOfObject messageRefCache;
	protected HashtableOfObject typeRefCache;
	protected HashtableOfObject unknownRefCache;

	private static Object dummy = new Object();
	protected Stack<Boolean> hasId = new Stack<Boolean>();
	protected int topLevelNesting = 0;
	protected HashtableOfCharArrayAndObject topLevelIdentifiers = new HashtableOfCharArrayAndObject();
	
	protected boolean stillAcceptsImportContainer = true;

protected CompilationUnitStructureRequestor(ICompilationUnit unit, CompilationUnitElementInfo unitInfo, Map newElements) {
	this.unit = unit;
	this.unitInfo = unitInfo;
	this.newElements = newElements;
} 

public void acceptImport(int declarationStart, int declarationEnd, String name, String alias, String[] selectiveImportsNames,  String[] selectiveImportsAliases, long modifiers) {
	JavaElement parentHandle= (JavaElement) this.handleStack.peek();
	if (stillAcceptsImportContainer && (modifiers == 0 || Flags.isStatic(modifiers)) && 
			parentHandle.getElementType() == IJavaElement.COMPILATION_UNIT) {
		ICompilationUnit parentCU= (ICompilationUnit)parentHandle;
		//create the import container and its info
		ImportContainer importContainer= (ImportContainer)parentCU.getImportContainer();
		if (this.importContainerInfo == null) {
			this.importContainerInfo = new JavaElementInfo();
			JavaElementInfo parentInfo = (JavaElementInfo) this.infoStack.peek();
			addToChildren(parentInfo, importContainer);
			this.newElements.put(importContainer, this.importContainerInfo);
		}
		
		String elementName = JavaModelManager.getJavaModelManager().intern(name);
		ImportDeclaration handle = new ImportDeclaration(importContainer, elementName, alias, selectiveImportsNames, selectiveImportsAliases);
		resolveDuplicates(handle);
		
		ImportDeclarationElementInfo info = new ImportDeclarationElementInfo();
		info.setSourceRangeStart(declarationStart);
		info.setSourceRangeEnd(declarationEnd);
		info.setFlags(modifiers);

		addToChildren(this.importContainerInfo, handle);
		this.newElements.put(handle, info);
	} else {
		JavaElementInfo parentInfo = (JavaElementInfo) this.infoStack.peek();
		
		String elementName = JavaModelManager.getJavaModelManager().intern(name);
		ImportDeclaration handle = new ImportDeclaration(parentHandle, elementName, alias, selectiveImportsNames, selectiveImportsAliases);
		resolveDuplicates(handle);
		
		ImportDeclarationElementInfo info = new ImportDeclarationElementInfo();
		info.setSourceRangeStart(declarationStart);
		info.setSourceRangeEnd(declarationEnd);
		info.setFlags(modifiers);

		addToChildren(parentInfo, handle);
		this.newElements.put(handle, info);
	}
}

/*
 * Table of line separator position. This table is passed once at the end
 * of the parse action, so as to allow computation of normalized ranges.
 *
 * A line separator might corresponds to several characters in the source,
 * 
 */
public void acceptLineSeparatorPositions(int[] positions) {
	// ignore line separator positions
}
/**
 * @see ISourceElementRequestor
 */
public void acceptPackage(int declarationStart, int declarationEnd, char[] name) {

		JavaElementInfo parentInfo = (JavaElementInfo) this.infoStack.peek();
		JavaElement parentHandle= (JavaElement) this.handleStack.peek();
		PackageDeclaration handle = null;
		
		if (parentHandle.getElementType() == IJavaElement.COMPILATION_UNIT || 
				parentHandle.getElementType() == IJavaElement.CLASS_FILE) {
			handle = new PackageDeclaration((CompilationUnit) parentHandle, new String(name));
		}
		else {
			Assert.isTrue(false); // Should not happen
		}
		resolveDuplicates(handle);
		
		SourceRefElementInfo info = new SourceRefElementInfo();
		info.setSourceRangeStart(declarationStart);
		info.setSourceRangeEnd(declarationEnd);

		addToChildren(parentInfo, handle);
		this.newElements.put(handle, info);

}
public void acceptProblem(IProblem problem) {
	if ((problem.getCategoryID() & IProblem.CAT_SYNTAX) != 0){
		this.hasSyntaxErrors = true;
	}
}
private void addToChildren(JavaElementInfo parentInfo, JavaElement handle) {
	ArrayList childrenList = (ArrayList) this.children.get(parentInfo);
	if (childrenList == null)
		this.children.put(parentInfo, childrenList = new ArrayList());
	childrenList.add(handle);
}
/**
 * Convert these type names to signatures.
 * @see Signature
 */
/* default */ static String[] convertTypeNamesToSigs(char[][] typeNames) {
	if (typeNames == null)
		return NO_STRINGS;
	int n = typeNames.length;
	if (n == 0)
		return NO_STRINGS;
	JavaModelManager manager = JavaModelManager.getJavaModelManager();
	String[] typeSigs = new String[n];
	for (int i = 0; i < n; ++i) {
		//typeSigs[i] = manager.intern(Signature.createTypeSignature(typeNames[i], false));
		typeSigs[i] = manager.intern(new String(typeNames[i]));
	}
	return typeSigs;
}
/**
 * @see ISourceElementRequestor
 */
public void enterCompilationUnit() {
	this.infoStack = new Stack();
	this.children = new HashMap();
	this.handleStack= new Stack();
	this.infoStack.push(this.unitInfo);
	this.handleStack.push(this.unit);
}
/**
 * @see ISourceElementRequestor
 */
public void enterConstructor(MethodInfo methodInfo) {
	enterMethod(methodInfo);
}

/**
 * @see ISourceElementRequestor
 */
public void enterField(FieldInfo fieldInfo) {
	addToTopLevel(fieldInfo.name);
	
	JavaElementInfo parentInfo = (JavaElementInfo) this.infoStack.peek();
	JavaElement parentHandle= (JavaElement) this.handleStack.peek();
	SourceField handle = null;
	//if (parentHandle.getElementType() == IJavaElement.TYPE) {
		String fieldName = JavaModelManager.getJavaModelManager().intern(new String(fieldInfo.name));
		handle = new SourceField(parentHandle, fieldName);
	//}
	//else {
	//	Assert.isTrue(false); // Should not happen
	//}
	resolveDuplicates(handle);
	
	SourceFieldElementInfo info = new SourceFieldElementInfo();
	info.setNameSourceStart(fieldInfo.nameSourceStart);
	info.setNameSourceEnd(fieldInfo.nameSourceEnd);
	info.setSourceRangeStart(fieldInfo.declarationStart);
	info.setFlags(fieldInfo.modifiers);
	info.setInitializationSource(fieldInfo.initializationSource);
	if (fieldInfo.type != null) {
		char[] typeName = JavaModelManager.getJavaModelManager().intern(fieldInfo.type);
		info.setTypeName(typeName);
	}
	
//	this.unitInfo.addAnnotationPositions(handle, fieldInfo.annotationPositions);

	addToChildren(parentInfo, handle);
	//parentInfo.addCategories(handle, fieldInfo.categories);
	this.newElements.put(handle, info);

	this.infoStack.push(info);
	this.handleStack.push(handle);
	
	stillAcceptsImportContainer = false;
}
/**
 * @see ISourceElementRequestor
 */
public void enterInitializer(
	int declarationSourceStart,
	long modifiers,
	char[] displayString) {
	// If there is a mixin, cancel the identifiers cache
	if (topLevelNesting == 0 && ((modifiers & Flags.AccMixin) != 0) || (modifiers & Flags.AccTemplateMixin) != 0) {
		topLevelIdentifiers = null;
	}

	JavaElementInfo parentInfo = (JavaElementInfo) this.infoStack.peek();
	JavaElement parentHandle= (JavaElement) this.handleStack.peek();
	Initializer handle = null;
	
	//if (parentHandle.getElementType() == IJavaElement.TYPE) {
	if (displayString.length == 0) {
		handle = new Initializer(parentHandle, 1);
	} else {
		String displayStringStr = JavaModelManager.getJavaModelManager().intern(new String(displayString));
		handle = new Initializer(parentHandle, 1, displayStringStr);
	}
	//}
	//else {
	//Assert.isTrue(false); // Should not happen
	//}
	resolveDuplicates(handle);
	
	InitializerElementInfo info = new InitializerElementInfo();
	info.setSourceRangeStart(declarationSourceStart);
	info.setFlags(modifiers);

	addToChildren(parentInfo, handle);
	this.newElements.put(handle, info);

	this.infoStack.push(info);
	this.handleStack.push(handle);
	
	stillAcceptsImportContainer = false;
}

public void enterConditional(int declarationSourceStart, long modifiers, char[] displayString) {
	JavaElementInfo parentInfo = (JavaElementInfo) this.infoStack.peek();
	JavaElement parentHandle= (JavaElement) this.handleStack.peek();
	Conditional handle = null;
	
	//if (parentHandle.getElementType() == IJavaElement.TYPE) {
	if (displayString.length == 0) {
		handle = new Conditional(parentHandle, 1);
	} else {
		String displayStringStr = JavaModelManager.getJavaModelManager().intern(new String(displayString));
		handle = new Conditional(parentHandle, 1, displayStringStr);
	}
	//}
	//else {
	//Assert.isTrue(false); // Should not happen
	//}
	resolveDuplicates(handle);
	
	ConditionalElementInfo info = new ConditionalElementInfo();
	info.setSourceRangeStart(declarationSourceStart);
	info.setFlags(modifiers);

	addToChildren(parentInfo, handle);
	this.newElements.put(handle, info);

	this.infoStack.push(info);
	this.handleStack.push(handle);
	
	stillAcceptsImportContainer = false;
}

public void enterConditionalThen(int declarationSourceStart) {
	JavaElementInfo parentInfo = (JavaElementInfo) this.infoStack.peek();
	JavaElement parentHandle= (JavaElement) this.handleStack.peek();
	Initializer handle = new Initializer(parentHandle, 1);
	resolveDuplicates(handle);
	
	InitializerElementInfo info = new InitializerElementInfo();
	info.setSourceRangeStart(declarationSourceStart);
	info.setFlags(Flags.AccThen);
	addToChildren(parentInfo, handle);
	this.newElements.put(handle, info);

	this.infoStack.push(info);
	this.handleStack.push(handle);
	
	stillAcceptsImportContainer = false;
}

public void enterConditionalElse(int declarationSourceStart) {
	JavaElementInfo parentInfo = (JavaElementInfo) this.infoStack.peek();
	JavaElement parentHandle= (JavaElement) this.handleStack.peek();
	Initializer handle = new Initializer(parentHandle, 1);
	resolveDuplicates(handle);
	
	InitializerElementInfo info = new InitializerElementInfo();
	info.setSourceRangeStart(declarationSourceStart);
	info.setFlags(Flags.AccElse);
	addToChildren(parentInfo, handle);
	this.newElements.put(handle, info);

	this.infoStack.push(info);
	this.handleStack.push(handle);
	
	stillAcceptsImportContainer = false;
}

/**
 * @see ISourceElementRequestor
 */
public void enterMethod(MethodInfo methodInfo) {
	addToTopLevel(methodInfo.name);
	
	JavaElementInfo parentInfo = (JavaElementInfo) this.infoStack.peek();
	JavaElement parentHandle= (JavaElement) this.handleStack.peek();
	SourceMethod handle = null;

	// translate nulls to empty arrays
	if (methodInfo.parameterTypes == null) {
		methodInfo.parameterTypes= CharOperation.NO_CHAR_CHAR;
	}
	if (methodInfo.parameterNames == null) {
		methodInfo.parameterNames= CharOperation.NO_CHAR_CHAR;
	}
//	if (methodInfo.exceptionTypes == null) {
//		methodInfo.exceptionTypes= CharOperation.NO_CHAR_CHAR;
//	}
	
	String[] parameterTypeSigs = convertTypeNamesToSigs(methodInfo.parameterTypes);
	
	//if (parentHandle.getElementType() == IJavaElement.TYPE) {
		String selector = JavaModelManager.getJavaModelManager().intern(new String(methodInfo.name));
		handle = new SourceMethod(parentHandle, selector, parameterTypeSigs);
	//}
	//else {
	//	Assert.isTrue(false); // Should not happen
	//}
	resolveDuplicates(handle);
	
	SourceMethodElementInfo info;
//	if (Flags.isConstructor(methodInfo.modifiers))
//		info = new SourceConstructorInfo();
	/*
	else if (methodInfo.isAnnotation)
		info = new SourceAnnotationMethodInfo();
	*/
//	else
		info = new SourceMethodInfo();
	info.setSourceRangeStart(methodInfo.declarationStart);
	long flags = methodInfo.modifiers;
	info.setNameSourceStart(methodInfo.nameSourceStart);
	info.setNameSourceEnd(methodInfo.nameSourceEnd);
	info.setFlags(flags);
	JavaModelManager manager = JavaModelManager.getJavaModelManager();
	char[][] parameterNames = methodInfo.parameterNames;
	for (int i = 0, length = parameterNames.length; i < length; i++)
		parameterNames[i] = manager.intern(parameterNames[i]);
	info.setArgumentNames(parameterNames);
	char[] returnType = methodInfo.returnType == null ? new char[]{'v'} : methodInfo.returnType;
	info.setReturnType(manager.intern(returnType));
	info.setParameterDefaultValues(methodInfo.parameterDefaultValues);
//	char[][] exceptionTypes = methodInfo.exceptionTypes;
//	info.setExceptionTypeNames(exceptionTypes);
//	for (int i = 0, length = exceptionTypes.length; i < length; i++)
//		exceptionTypes[i] = manager.intern(exceptionTypes[i]);
//	info.setDefaultValuesCount(methodInfo.defaultValuesCount);
//	this.unitInfo.addAnnotationPositions(handle, methodInfo.annotationPositions);
	addToChildren(parentInfo, handle);
	//parentInfo.addCategories(handle, methodInfo.categories);
	this.newElements.put(handle, info);
	this.infoStack.push(info);
	this.handleStack.push(handle);

	if (methodInfo.typeParameters != null) {
		for (int i = 0, length = methodInfo.typeParameters.length; i < length; i++) {
			TypeParameterInfo typeParameterInfo = methodInfo.typeParameters[i];
			enterTypeParameter(typeParameterInfo);
			exitMember(typeParameterInfo.declarationEnd);
		}
	}
	
	stillAcceptsImportContainer = false;
}
/**
 * @see ISourceElementRequestor
 */
public void enterType(TypeInfo typeInfo) {
	addToTopLevel(typeInfo.name);
	if (typeInfo.name != null && typeInfo.name.length > 0) {
		hasId.push(true);
		topLevelNesting++;
	} else {
		hasId.push(false);
	}
	
	JavaElementInfo parentInfo = (JavaElementInfo) this.infoStack.peek();
	JavaElement parentHandle= (JavaElement) this.handleStack.peek();
	// Changed to allow annonymous enums, structs and unions
	String nameString= new String(typeInfo.name == null ? ANNONYMOUS_NAME : typeInfo.name);
	SourceType handle = new SourceType(parentHandle, nameString); //NB: occurenceCount is computed in resolveDuplicates
	resolveDuplicates(handle);
	
	SourceTypeElementInfo info = new SourceTypeElementInfo();
	info.setHandle(handle);
	info.setSourceRangeStart(typeInfo.declarationStart);
	info.setFlags(typeInfo.modifiers);
	// Added to allow annonymous enums, structs and unions
	if (typeInfo.name != null) {
		info.setNameSourceStart(typeInfo.nameSourceStart);
		info.setNameSourceEnd(typeInfo.nameSourceEnd);
	}
//	// Added to allow storing default, min and max values of enums
//	info.setEnumValues(typeInfo.enumValues);
//	// Added to allow storing of sizeof and alignof
//	info.setAlignof(typeInfo.alignof);
//	info.setSizeof(typeInfo.sizeof);
	
	JavaModelManager manager = JavaModelManager.getJavaModelManager();
//	char[] superclass = typeInfo.superclass;
//	info.setSuperclassName(superclass == null ? null : manager.intern(superclass));
	char[][] superinterfaces = typeInfo.superinterfaces;
	for (int i = 0, length = superinterfaces == null ? 0 : superinterfaces.length; i < length; i++)
		superinterfaces[i] = manager.intern(superinterfaces[i]);
	info.setSuperInterfaceNames(superinterfaces);
//	info.addCategories(handle, typeInfo.categories);
//	if (parentHandle.getElementType() == IJavaElement.TYPE)
//		((SourceTypeElementInfo) parentInfo).addCategories(handle, typeInfo.categories);
	addToChildren(parentInfo, handle);
//	this.unitInfo.addAnnotationPositions(handle, typeInfo.annotationPositions);
	this.newElements.put(handle, info);
	this.infoStack.push(info);
	this.handleStack.push(handle);
	
	if (typeInfo.typeParameters != null) {
		for (int i = 0, length = typeInfo.typeParameters.length; i < length; i++) {
			TypeParameterInfo typeParameterInfo = typeInfo.typeParameters[i];
			enterTypeParameter(typeParameterInfo);
			exitMember(typeParameterInfo.declarationEnd);
		}
	}
	
	stillAcceptsImportContainer = false;
}
protected void enterTypeParameter(TypeParameterInfo typeParameterInfo) {
	JavaElementInfo parentInfo = (JavaElementInfo) this.infoStack.peek();
	JavaElement parentHandle = (JavaElement) this.handleStack.peek();
	String nameString = new String(typeParameterInfo.name);
	TypeParameter handle = new TypeParameter(parentHandle, nameString); //NB: occurenceCount is computed in resolveDuplicates
	resolveDuplicates(handle);
	
	TypeParameterElementInfo info = new TypeParameterElementInfo();
	info.setSourceRangeStart(typeParameterInfo.declarationStart);
	info.nameStart = typeParameterInfo.nameSourceStart;
	info.nameEnd = typeParameterInfo.nameSourceEnd;
//	info.bounds = typeParameterInfo.bounds;
	info.signature = typeParameterInfo.signature;
	info.defaultValue = typeParameterInfo.defaultValue;
	if (parentInfo instanceof SourceTypeElementInfo) {
		SourceTypeElementInfo elementInfo = (SourceTypeElementInfo) parentInfo;
		ITypeParameter[] typeParameters = elementInfo.typeParameters;
		int length = typeParameters.length;
		System.arraycopy(typeParameters, 0, typeParameters = new ITypeParameter[length+1], 0, length);
		typeParameters[length] = handle;
		elementInfo.typeParameters = typeParameters;
	} else {
		SourceMethodElementInfo elementInfo = (SourceMethodElementInfo) parentInfo;
		ITypeParameter[] typeParameters = elementInfo.typeParameters;
		int length = typeParameters.length;
		System.arraycopy(typeParameters, 0, typeParameters = new ITypeParameter[length+1], 0, length);
		typeParameters[length] = handle;
		elementInfo.typeParameters = typeParameters;
	}
//	this.unitInfo.addAnnotationPositions(handle, typeParameterInfo.annotationPositions);
	this.newElements.put(handle, info);
	this.infoStack.push(info);
	this.handleStack.push(handle);
	
	stillAcceptsImportContainer = false;
}
/**
 * @see ISourceElementRequestor
 */
public void exitCompilationUnit(int declarationEnd) {
	// set import container children
	if (this.importContainerInfo != null) {
		setChildren(this.importContainerInfo);
	}
	
	// set children
	setChildren(this.unitInfo);
	
	this.unitInfo.topLevelIdentifiers = topLevelIdentifiers;
	
	this.unitInfo.setSourceLength(declarationEnd + 1);

	// determine if there were any parsing errors
	this.unitInfo.setIsStructureKnown(!this.hasSyntaxErrors);
}
/**
 * @see ISourceElementRequestor
 */
public void exitConstructor(int declarationEnd) {
	exitMember(declarationEnd);
}
/**
 * @see ISourceElementRequestor
 */
public void exitField(int initializationStart, int declarationEnd, int declarationSourceEnd) {
	SourceFieldElementInfo info = (SourceFieldElementInfo) this.infoStack.pop();
	info.setSourceRangeEnd(declarationSourceEnd);
	setChildren(info);
	
	// remember initializer source if field is a constant
	/* TODO JDT initializers
	if (initializationStart != -1) {
		int flags = info.flags;
		Object typeInfo;
		if (Flags.isStatic(flags) && Flags.isFinal(flags)
				|| ((typeInfo = this.infoStack.peek()) instanceof SourceTypeElementInfo
					 && (Flags.isInterface(((SourceTypeElementInfo)typeInfo).flags)))) {
			int length = declarationEnd - initializationStart;
			if (length > 0) {
				char[] initializer = new char[length];
				System.arraycopy(this.parser.scanner.source, initializationStart, initializer, 0, length);
				info.initializationSource = initializer;
			}
		}
	}
	*/
	this.handleStack.pop();
}
/**
 * @see ISourceElementRequestor
 */
public void exitInitializer(int declarationEnd) {
	exitMember(declarationEnd);
}
/**
 * @see ISourceElementRequestor
 */
public void exitConditional(int declarationEnd) {
	exitMember(declarationEnd);
}
/**
 * @see ISourceElementRequestor
 */
public void exitConditionalThen(int declarationEnd) {
	exitMember(declarationEnd);
}
/**
 * @see ISourceElementRequestor
 */
public void exitConditionalElse(int declarationEnd) {
	exitMember(declarationEnd);
}
/**
 * common processing for classes and interfaces
 */
protected void exitMember(int declarationEnd) {
	SourceRefElementInfo info = (SourceRefElementInfo) this.infoStack.pop();
	info.setSourceRangeEnd(declarationEnd);
	setChildren(info);
	this.handleStack.pop();
}
/**
 * @see ISourceElementRequestor
 */
public void exitMethod(int declarationEnd, int defaultValueStart, int defaultValueEnd) {
	SourceMethodElementInfo info = (SourceMethodElementInfo) this.infoStack.pop();
	info.setSourceRangeEnd(declarationEnd);
	setChildren(info);
	
	// remember default value of annotation method
	/*
	if (info.isAnnotationMethod()) {
		SourceAnnotationMethodInfo annotationMethodInfo = (SourceAnnotationMethodInfo) info;
		annotationMethodInfo.defaultValueStart = defaultValueStart;
		annotationMethodInfo.defaultValueEnd = defaultValueEnd;
	}
	*/
	this.handleStack.pop();
}
/**
 * @see ISourceElementRequestor
 */
public void exitType(int declarationEnd) {
	boolean val = hasId.pop();
	if (val) {
		topLevelNesting--;
	}
	
	exitMember(declarationEnd);
}
/**
 * Resolves duplicate handles by incrementing the occurrence count
 * of the handle being created until there is no conflict.
 */
protected void resolveDuplicates(SourceRefElement handle) {
	while (this.newElements.containsKey(handle)) {
		handle.occurrenceCount++;
	}
}
private void setChildren(JavaElementInfo info) {
	ArrayList childrenList = (ArrayList) this.children.get(info);
	if (childrenList != null) {
		int length = childrenList.size();
		IJavaElement[] elements = new IJavaElement[length];
		childrenList.toArray(elements);
		info.children = elements;
	}
}

private void addToTopLevel(char[] name) {
	if (topLevelIdentifiers != null && topLevelNesting == 0 && name != null && name.length > 0) {
		topLevelIdentifiers.put(name, dummy);	
	}
}

}
