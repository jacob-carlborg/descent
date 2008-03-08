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
package descent.internal.core;

import java.util.ArrayList;
import java.util.List;

import descent.core.Flags;
import descent.core.IBuffer;
import descent.core.IClassFile;
import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IMember;
import descent.core.IMethod;
import descent.core.ISourceRange;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.core.Signature;
import descent.core.WorkingCopyOwner;
import descent.core.dom.AST;
import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.Comment;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.TypedefDeclaration;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.core.util.MementoTokenizer;

/**
 * @see IMember
 */

public abstract class Member extends SourceRefElement implements IMember {
	
protected Member(JavaElement parent) {
	super(parent);
}
protected static boolean areSimilarMethods(
	String name1, String[] params1, 
	String name2, String[] params2,
	String[] simpleNames1) {
		
	if (name1.equals(name2)) {
		int params1Length = params1.length;
		if (params1Length == params2.length) {
			for (int i = 0; i < params1Length; i++) {
				// TODO JDT signature
//				String simpleName1 = 
//					simpleNames1 == null ? 
//						Signature.getSimpleName(Signature.toString(Signature.getTypeErasure(params1[i]))) :
//						simpleNames1[i];
//				String simpleName2 = Signature.getSimpleName(Signature.toString(Signature.getTypeErasure(params2[i])));
//				if (!simpleName1.equals(simpleName2)) {
//					return false;
//				}
			}
			return true;
		}
	}
	return false;
}
/**
 * Converts a field constant from the compiler's representation
 * to the Java Model constant representation (Number or String).
 */
/* TODO JDT compiler
protected static Object convertConstant(Constant constant) {
	if (constant == null)
		return null;
	if (constant == Constant.NotAConstant) {
		return null;
	}
	switch (constant.typeID()) {
		case TypeIds.T_boolean :
			return constant.booleanValue() ? Boolean.TRUE : Boolean.FALSE;
		case TypeIds.T_byte :
			return new Byte(constant.byteValue());
		case TypeIds.T_char :
			return new Character(constant.charValue());
		case TypeIds.T_double :
			return new Double(constant.doubleValue());
		case TypeIds.T_float :
			return new Float(constant.floatValue());
		case TypeIds.T_int :
			return new Integer(constant.intValue());
		case TypeIds.T_long :
			return new Long(constant.longValue());
		case TypeIds.T_short :
			return new Short(constant.shortValue());
		case TypeIds.T_JavaLangString :
			return constant.stringValue();
		default :
			return null;
	}
}
*/
/*
 * Helper method for SourceType.findMethods and BinaryType.findMethods
 */
public static IMethod[] findMethods(IMethod method, IMethod[] methods) {
	String elementName = method.getElementName();
	String[] parameters = method.getParameterTypes();
	int paramLength = parameters.length;
	String[] simpleNames = new String[paramLength];
	for (int i = 0; i < paramLength; i++) {
		// TODO JDT signature
//		String erasure = Signature.getTypeErasure(parameters[i]);
//		simpleNames[i] = Signature.getSimpleName(Signature.toString(erasure));
	}
	ArrayList list = new ArrayList();
	for (int i = 0, length = methods.length; i < length; i++) {
		IMethod existingMethod = methods[i];
		if (areSimilarMethods(
				elementName,
				parameters,
				existingMethod.getElementName(),
				existingMethod.getParameterTypes(),
				simpleNames)) {
			list.add(existingMethod);
		}
	}
	int size = list.size();
	if (size == 0) {
		return null;
	} else {
		IMethod[] result = new IMethod[size];
		list.toArray(result);
		return result;
	}
}
/* TODO JDT categories
public String[] getCategories() throws JavaModelException {
	IType type = (IType) getAncestor(IJavaElement.TYPE);
	if (type == null) return CharOperation.NO_STRINGS;
	if (type.isBinary()) {
		return CharOperation.NO_STRINGS;
	} else {
		SourceTypeElementInfo info = (SourceTypeElementInfo) ((SourceType) type).getElementInfo();
		HashMap map = info.getCategories();
		if (map == null) return CharOperation.NO_STRINGS;
		String[] categories = (String[]) map.get(this);
		if (categories == null) return CharOperation.NO_STRINGS;
		return categories;
	}
}
*/
/**
 * @see IMember
 */
public IClassFile getClassFile() {
	return ((JavaElement)getParent()).getClassFile();
}
/**
 * @see IMember
 */
public IType getDeclaringType() {
	JavaElement parentElement = (JavaElement)getParent();
	if (parentElement.getElementType() == TYPE) {
		return (IType) parentElement;
	}
	return null;
}
/**
 * @see IMember
 */
public long getFlags() throws JavaModelException {
	MemberElementInfo info = (MemberElementInfo) getElementInfo();
	return info.getModifiers();
}
/*
 * @see JavaElement
 */
public IJavaElement getHandleFromMemento(String token, MementoTokenizer memento, WorkingCopyOwner workingCopyOwner) {
	switch (token.charAt(0)) {
		case JEM_COUNT:
			return getHandleUpdatingCountFromMemento(memento, workingCopyOwner);
		case JEM_TYPE:
			String typeName;
			if (memento.hasMoreTokens()) {
				typeName = memento.nextToken();
				char firstChar = typeName.charAt(0);
				if (firstChar == JEM_FIELD || firstChar == JEM_INITIALIZER || firstChar == JEM_METHOD || firstChar == JEM_TYPE || firstChar == JEM_COUNT || firstChar == JEM_CONDITIONAL) {
					token = typeName;
					typeName = ""; //$NON-NLS-1$
				} else {
					token = null;
				}
			} else {
				typeName = ""; //$NON-NLS-1$
				token = null;
			}
			JavaElement type = (JavaElement)getType(typeName, 1);
			if (token == null) {
				return type.getHandleFromMemento(memento, workingCopyOwner);
			} else {
				return type.getHandleFromMemento(token, memento, workingCopyOwner);
			}
		case JEM_LOCALVARIABLE:
			if (!memento.hasMoreTokens()) return this;
			String varName = memento.nextToken();
			if (!memento.hasMoreTokens()) return this;
			memento.nextToken(); // JEM_COUNT
			if (!memento.hasMoreTokens()) return this;
			int declarationStart = Integer.parseInt(memento.nextToken());
			if (!memento.hasMoreTokens()) return this;
			memento.nextToken(); // JEM_COUNT
			if (!memento.hasMoreTokens()) return this;
			int declarationEnd = Integer.parseInt(memento.nextToken());
			if (!memento.hasMoreTokens()) return this;
			memento.nextToken(); // JEM_COUNT
			if (!memento.hasMoreTokens()) return this;
			int nameStart = Integer.parseInt(memento.nextToken());
			if (!memento.hasMoreTokens()) return this;
			memento.nextToken(); // JEM_COUNT
			if (!memento.hasMoreTokens()) return this;
			int nameEnd = Integer.parseInt(memento.nextToken());
			if (!memento.hasMoreTokens()) return this;
			memento.nextToken(); // JEM_COUNT
			if (!memento.hasMoreTokens()) return this;
			String typeSignature = memento.nextToken();
			// TODO Descent alias, typedef or var?
			return new LocalVariable(this, varName, declarationStart, declarationEnd, nameStart, nameEnd, typeSignature, 0);
		case JEM_TYPE_PARAMETER:
			if (!memento.hasMoreTokens()) return this;
			String typeParameterName = memento.nextToken();
			JavaElement typeParameter = new TypeParameter(this, typeParameterName);
			return typeParameter.getHandleFromMemento(memento, workingCopyOwner);
	}
	return null;
}
/**
 * @see JavaElement#getHandleMemento()
 */
protected char getHandleMementoDelimiter() {
	return JavaElement.JEM_TYPE;
}
/*
 * Returns the outermost context defining a local element. Per construction, it can only be a
 * method/field/initializarer member; thus, returns null if this member is already a top-level type or member type.
 * e.g for X.java/X/Y/foo()/Z/bar()/T, it will return X.java/X/Y/foo()
 */
public Member getOuterMostLocalContext() {
	IJavaElement current = this;
	Member lastLocalContext = null;
	parentLoop: while (true) {
		switch (current.getElementType()) {
			case CLASS_FILE:
			case COMPILATION_UNIT:
				break parentLoop; // done recursing
			case TYPE:
				// cannot be a local context
				break;
			case INITIALIZER:
			case FIELD:
			case METHOD:
				 // these elements can define local members
				lastLocalContext = (Member) current;
				break;
		}		
		current = current.getParent();
	} 
	return lastLocalContext;
}
public ISourceRange[] getJavadocRanges() throws JavaModelException {
	ISourceRange range= this.getSourceRange();
	if (range == null) return null;
	IBuffer buf= null;
	if (this.isBinary()) {
		buf = this.getClassFile().getBuffer();
	} else {
		ICompilationUnit compilationUnit = this.getCompilationUnit();
		if (!compilationUnit.isConsistent()) {
			return null;
		}
		buf = compilationUnit.getBuffer();
	}
	final int start= range.getOffset();
	final int length= range.getLength();
	
	Parser parser = new Parser(AST.D2, buf.getText(start, length));
	Module module = parser.parseModuleObj();
	if (module.members == null || module.members.size() == 0) {
		return null;
	}
	Dsymbol declaration = (Dsymbol) module.members.get(0);
	
	List<ISourceRange> sourceRanges = new ArrayList<ISourceRange>(1);
	if (declaration instanceof VarDeclaration) {
		VarDeclaration var = (VarDeclaration) declaration;
		while(var.next != null) {
			var = var.next;
			declaration = var;
		}
	} else if (declaration instanceof AliasDeclaration) {
		AliasDeclaration var = (AliasDeclaration) declaration;
		while(var.next != null) {
			var = var.next;
			declaration = var;
		}
	} else if (declaration instanceof TypedefDeclaration) {
		TypedefDeclaration var = (TypedefDeclaration) declaration;
		while(var.next != null) {
			var = var.next;
			declaration = var;
		}
	}
	
	if (declaration.preComments != null) {
		for(int i = declaration.preComments.size() - 1; i >= 0; i--) {
			Comment ddoc = declaration.preComments.get(i);
			if (!ddoc.isDDocComment()) {
				// Work even if there are non-ddoc comments in between
				// break;
				continue;
			}
			sourceRanges.add(0, new SourceRange(start + ddoc.start, ddoc.length));
		}
	}
	
	if (declaration.postComment != null && declaration.postComment.isDDocComment()) {
		sourceRanges.add(new SourceRange(start + declaration.postComment.start,
				declaration.postComment.length));
	}

	return sourceRanges.toArray(new ISourceRange[sourceRanges.size()]);
}
/**
 * @see IMember
 */
public ISourceRange getNameRange() throws JavaModelException {
	MemberElementInfo info= (MemberElementInfo)getElementInfo();
	return new SourceRange(info.getNameSourceStart(), info.getNameSourceEnd() - info.getNameSourceStart() + 1);
}
/**
 * @see IMember
 */
public IType getType(String typeName, int count) {
	if (isBinary()) {
		throw new IllegalArgumentException("Not a source member " + toStringWithAncestors()); //$NON-NLS-1$
	} else {
		SourceType type = new SourceType(this, typeName);
		type.occurrenceCount = count;
		return type;
	}
}
/**
 * @see IMember
 */
public boolean isBinary() {
	return false;
}
protected boolean isMainMethod(IMethod method) throws JavaModelException {
	/* TODO JDT Java -> D */
	if ("main".equals(method.getElementName()) && Signature.SIG_VOID.equals(method.getReturnType())) { //$NON-NLS-1$
		long flags= method.getFlags();
		if (Flags.isStatic(flags) && Flags.isPublic(flags)) {
			String[] paramTypes= method.getParameterTypes();
			if (paramTypes.length == 1) {
				// TODO JDT signature
//				String typeSignature=  Signature.toString(paramTypes[0]);
//				return "String[]".equals(Signature.getSimpleName(typeSignature)); //$NON-NLS-1$
			}
		}
		return true;
	}
	return false;
}
/**
 * @see IJavaElement
 */
public boolean isReadOnly() {
	return getClassFile() != null;
}
/**
 */
public String readableName() {

	IJavaElement declaringType = getDeclaringType();
	if (declaringType != null) {
		String declaringName = ((JavaElement) getDeclaringType()).readableName();
		StringBuffer buffer = new StringBuffer(declaringName);
		buffer.append('.');
		buffer.append(this.getElementName());
		return buffer.toString();
	} else {
		return super.readableName();
	}
}
/**
 * Updates the name range for this element.
 */
protected void updateNameRange(int nameStart, int nameEnd) {
	try {
		MemberElementInfo info = (MemberElementInfo) getElementInfo();
		info.setNameSourceStart(nameStart);
		info.setNameSourceEnd(nameEnd);
	} catch (JavaModelException npe) {
		return;
	}
}
/*
 * (non-Javadoc)
 * @see descent.core.IJavaElement#isCompileTimeGenerated()
 */
public boolean isCompileTimeGenerated() throws JavaModelException {
	return (getFlags() & Flags.AccCompileTimeGenerated) != 0;
}
}
