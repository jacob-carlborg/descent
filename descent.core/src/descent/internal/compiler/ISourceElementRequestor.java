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
package descent.internal.compiler;

import descent.core.compiler.IProblem;

/*
 * Part of the source element parser responsible for building the output. It
 * gets notified of structural information as they are detected, relying on the
 * requestor to assemble them together, based on the notifications it got.
 * 
 * The structural investigation includes: - package statement - import
 * statements - top-level types: package member, member types (member types of
 * member types...) - fields - methods
 * 
 * If reference information is requested, then all source constructs are
 * investigated and type, field & method references are provided as well.
 * 
 * Any (parsing) problem encountered is also provided.
 * 
 * All positions are relative to the exact source fed to the parser.
 * 
 * Elements which are complex are notified in two steps: - enter <Element> :
 * once the element header has been identified - exit <Element> : once the
 * element has been fully consumed
 * 
 * other simpler elements (package, import) are read all at once: - accept
 * <Element>
 */

public interface ISourceElementRequestor {
	
	public static class TypeInfo {
		public int declarationStart;
		public long modifiers;
		public char[] name;
		public int nameSourceStart;
		public int nameSourceEnd;
		public char[][] superinterfaces;
		public TypeParameterInfo[] typeParameters;
		public boolean isForwardDeclaration;
	}
	
	public static class TypeParameterInfo {
		public int declarationStart;
		public int declarationEnd;
		public char[] name;
		public char[] signature;
		public char[] defaultValue;
		public int nameSourceStart;
		public int nameSourceEnd;
	}
	
	public static class MethodInfo {
		public int declarationStart;
		public long modifiers;
		public char[] returnType;
		public char[] name;
		public int nameSourceStart;
		public int nameSourceEnd;
		public char[][] parameterTypes;
		public char[][] parameterDefaultValues;
		public char[][] parameterNames;
		public TypeParameterInfo[] typeParameters;
		public char[] signature;
	}
	
	public static class FieldInfo {
		public int declarationStart;
		public long modifiers;
		public char[] type;
		public char[] name;
		public int nameSourceStart; 
		public int nameSourceEnd;
		public char[] initializationSource;
	}
	
	void acceptConstructorReference(char[] typeName, int argCount, int sourcePosition);
	
	void acceptFieldReference(char[] fieldName, int sourcePosition);
	/**
	 * @param declarationStart
	 *                   This is the position of the first character of the import
	 *                   keyword.
	 * @param declarationEnd
	 *                   This is the position of the ';' ending the import statement or
	 *                   the end of the comment following the import.
	 * @param modifiers
	 *                   can be set to static from 1.5 on.
	 */
	void acceptImport(int declarationStart, int declarationEnd, String name, String alias, String[] selectiveImportsNames,  String[] selectiveImportsAliases, long modifiers);

	/*
	 * Table of line separator position. This table is passed once at the end of
	 * the parse action, so as to allow computation of normalized ranges.
	 * 
	 * A line separator might corresponds to several characters in the source,
	 *  
	 */
	void acceptLineSeparatorPositions(int[] positions);

	void acceptMethodReference(char[] methodName, int argCount, int sourcePosition);
	
	void acceptPackage(int declarationStart, int declarationEnd, char[] name, boolean safe);

	void acceptProblem(IProblem problem);

	void acceptTypeReference(char[][] typeName, int sourceStart, int sourceEnd);

	void acceptTypeReference(char[] typeName, int sourcePosition);

	void acceptUnknownReference(char[][] name, int sourceStart, int sourceEnd);

	void acceptUnknownReference(char[] name, int sourcePosition);

	void enterCompilationUnit();
	
	void enterConditional(int declarationStart, long modifiers, char[] displayString);
	
	void enterConditionalThen(int declarationStart);
	
	void enterConditionalElse(int declarationStart);

	void enterConstructor(MethodInfo methodInfo);

	void enterField(FieldInfo fieldInfo);
	
	void enterInitializer(int declarationStart, long modifiers, char[] displayString);
	
	void enterMethod(MethodInfo methodInfo);
	
	void enterType(TypeInfo typeInfo);
	
	void exitCompilationUnit(int declarationEnd);
	
	void exitConstructor(int declarationEnd);

	/*
	 * initializationStart denotes the source start of the expression used for
	 * initializing the field if any (-1 if no initialization).
	 */
	void exitField(int initializationStart, int declarationEnd, int declarationSourceEnd);
	
	void exitInitializer(int declarationEnd);
	
	void exitConditional(int declarationEnd);
	
	void exitConditionalThen(int declarationEnd);
	
	void exitConditionalElse(int declarationEnd);
	
	void exitMethod(int declarationEnd, int defaultValueStart, int defaultValueEnd);
	
	void exitType(int declarationEnd);
}
