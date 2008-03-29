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
import java.util.List;

import descent.core.Flags;
import descent.core.IBuffer;
import descent.core.ICompilationUnit;
import descent.core.IField;
import descent.core.IJavaElement;
import descent.core.ISourceRange;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.core.Signature;
import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.internal.compiler.parser.Comment;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.EnumMember;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Parser;

/**
 * @see IField
 */

public class SourceField extends NamedMember implements IField {

/**
 * Constructs a handle to the field with the given name in the specified type. 
 */
protected SourceField(JavaElement parent, String name) {
	super(parent, name);
}
public boolean equals(Object o) {
	if (!(o instanceof SourceField)) return false;
	return super.equals(o);
}
public ASTNode findNode(descent.core.dom.CompilationUnit ast) {
	// For field declarations, a variable declaration fragment is returned
	// Return the FieldDeclaration instead
	ASTNode node = super.findNode(ast);
	if (node == null) return null;
	return node.getParent();
}
/**
 * @see IField
 */
public Object getConstant() throws JavaModelException {
	Object constant = null;	
	SourceFieldElementInfo info = (SourceFieldElementInfo) getElementInfo();
	final char[] constantSourceChars = info.initializationSource;
	if (constantSourceChars == null) {
		return null;
	}
			
	String constantSource = new String(constantSourceChars);
	String signature = info.getTypeSignature();
	try {
		if (signature.equals(Signature.SIG_INT)) {
			constant = new Integer(constantSource);
		} else if (signature.equals(Signature.SIG_SHORT)) {
			constant = new Short(constantSource);
		} else if (signature.equals(Signature.SIG_BYTE)) {
			constant = new Byte(constantSource);
		} else if (signature.equals(Signature.SIG_BOOL)) {
			constant = Boolean.valueOf(constantSource);
		} else if (signature.equals(Signature.SIG_CHAR)) {
			if (constantSourceChars.length != 3) {
				return null;
			}
			constant = new Character(constantSourceChars[1]);
		} else if (signature.equals(Signature.SIG_DOUBLE)) {
			constant = new Double(constantSource);
		} else if (signature.equals(Signature.SIG_FLOAT)) {
			constant = new Float(constantSource);
		} else if (signature.equals(Signature.SIG_LONG)) {
			if (constantSource.endsWith("L") || constantSource.endsWith("l")) { //$NON-NLS-1$ //$NON-NLS-2$
				int index = constantSource.lastIndexOf("L");//$NON-NLS-1$
				if (index != -1) {
					constant = new Long(constantSource.substring(0, index));
				} else {
					constant = new Long(constantSource.substring(0, constantSource.lastIndexOf("l")));//$NON-NLS-1$
				}
			} else {
				constant = new Long(constantSource);
			}
		} else if (signature.equals("QString;")) {//$NON-NLS-1$
			constant = constantSource;
		}
	} catch (NumberFormatException e) {
		// not a parsable constant
		return null;
	}
	return constant;
}
/*
 * (non-Javadoc)
 * @see descent.core.IField#getInitializationSource()
 */
public String getInitializerSource() throws JavaModelException {
	SourceFieldElementInfo info = (SourceFieldElementInfo) getElementInfo();
	char[] constantSourceChars = info.initializationSource;
	if (constantSourceChars == null) {
		return null;
	}
	return new String(constantSourceChars);
}
/**
 * @see IJavaElement
 */
public int getElementType() {
	return FIELD;
}
/* (non-Javadoc)
 * @see descent.core.IField#getKey()
 */
public String getKey() {
	try {
		return getKey(this, false/*don't open*/);
	} catch (JavaModelException e) {
		// happen only if force open is true
		return null;
	}
}
/**
 * @see JavaElement#getHandleMemento()
 */
protected char getHandleMementoDelimiter() {
	return JavaElement.JEM_FIELD;
}
/*
 * @see JavaElement#getPrimaryElement(boolean)
 */
public IJavaElement getPrimaryElement(boolean checkOwner) {
	if (checkOwner) {
		CompilationUnit cu = (CompilationUnit)getAncestor(COMPILATION_UNIT);
		if (cu.isPrimary()) return this;
	}
	IJavaElement primaryParent =this.parent.getPrimaryElement(false);
	return ((IType)primaryParent).getField(this.name);
}
/**
 * @see IField
 */
public String getTypeSignature() throws JavaModelException {
	// Descent: if I'm an enum constant, my type is my parent's type
	if (this.isEnumConstant()) {
		return getParent().getElementSignature();
	}
	
	// Else, return my signature
	SourceFieldElementInfo info = (SourceFieldElementInfo) getElementInfo();
	return info.getTypeSignature();
}
/**
 * @see IField
 */
public String getRawType() throws JavaModelException {
	SourceFieldElementInfo info = (SourceFieldElementInfo) getElementInfo();
	// TODO Descent remove this check when debug and version are used
	if (info.typeName == null) {
		return "i";
	}
	return new String(info.typeName);
}
/* (non-Javadoc)
 * @see descent.core.IField#isEnumConstant()
 */
public boolean isVariable() throws JavaModelException {
	long flags = getFlags();
	return !Flags.isEnum(flags) 
		&& !Flags.isAlias(flags) 
		&& !Flags.isTypedef(flags)
		&& !Flags.isTemplateMixin(flags);
}
/* (non-Javadoc)
 * @see descent.core.IField#isEnumConstant()
 */
public boolean isEnumConstant() throws JavaModelException {
	return Flags.isEnum(getFlags());
}
/* (non-Javadoc)
 * @see descent.core.IField#isAilas()
 */
public boolean isAlias() throws JavaModelException {
	return Flags.isAlias(getFlags());
}
/* (non-Javadoc)
 * @see descent.core.IField#isTypedef()
 */
public boolean isTypedef() throws JavaModelException {
	return Flags.isTypedef(getFlags());
}
/* (non-Javadoc)
 * @see descent.core.IField#isMixin()
 */
public boolean isTemplateMixin() throws JavaModelException {
	return Flags.isTemplateMixin(getFlags());
}
/* (non-Javadoc)
 * @see descent.core.IField#isResolved()
 */
public boolean isResolved() {
	return false;
}
/* TODO JDT binding
public JavaElement resolved(Binding binding) {
	SourceRefElement resolvedHandle = new ResolvedSourceField(this.parent, this.name, new String(binding.computeUniqueKey()));
	resolvedHandle.occurrenceCount = this.occurrenceCount;
	return resolvedHandle;
}
*/
/**
 * @private Debugging purposes
 */
protected void toStringInfo(int tab, StringBuffer buffer, Object info, boolean showResolvedInfo) {
	buffer.append(this.tabString(tab));
	if (info == null) {
		toStringName(buffer);
		buffer.append(" (not open)"); //$NON-NLS-1$
	} else if (info == NO_INFO) {
		toStringName(buffer);
	} else {
		try {
			buffer.append(Signature.toString(this.getTypeSignature()));
			//buffer.append(this.getTypeSignature());
			buffer.append(" "); //$NON-NLS-1$
			toStringName(buffer);
		} catch (JavaModelException e) {
			buffer.append("<JavaModelException in toString of " + getElementName()); //$NON-NLS-1$
		}
	}
}

@Override
public ISourceRange[] getJavadocRanges() throws JavaModelException {
	if (isEnumConstant()) {
		// Need to do something else for enum members, since alone they are not valid
		// declarations
		
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
		
		String text = "enum Foo{" + buf.getText(start, length) + "}";
		
		Parser parser = new Parser(AST.D2, text);
		Module module = parser.parseModuleObj();
		if (module.members == null || module.members.size() == 0) {
			return null;
		}
		EnumDeclaration declaration = (EnumDeclaration) module.members.get(0);
		EnumMember member = (EnumMember) declaration.members.get(0);
		
		// Build source ranges, but subtract 9 = "enumFoo{".length();
		List<ISourceRange> sourceRanges = new ArrayList<ISourceRange>(1);
		if (member.preComments != null) {
			for(int i = member.preComments.size() - 1; i >= 0; i--) {
				Comment ddoc = member.preComments.get(i);
				if (!ddoc.isDDocComment()) {
					break;
				}
				sourceRanges.add(0, new SourceRange(start + ddoc.start - 9, ddoc.length));
			}
		}
		
		if (member.postComment != null && member.postComment.isDDocComment()) {
			sourceRanges.add(new SourceRange(start + member.postComment.start - 9,
					member.postComment.length));
		}
		return sourceRanges.toArray(new ISourceRange[sourceRanges.size()]);
	} else {
		return super.getJavadocRanges();
	}
}
/*
 * (non-Javadoc)
 * @see descent.internal.core.JavaElement#appendElementSignature(java.lang.StringBuilder)
 */
@Override
protected void appendElementSignature(StringBuilder sb) throws JavaModelException {
	parent.appendElementSignature(sb);
	
	SourceTypeElementInfo info = (SourceTypeElementInfo) getElementInfo();
	long flags = info.getModifiers();
	if (Flags.isAlias(flags)) {
		sb.append(Signature.C_ALIAS);
	} else if (Flags.isTypedef(flags)) {
		sb.append(Signature.C_TYPEDEF);
	} else {
		sb.append(Signature.C_VARIABLE);
	}
	sb.append(name.length());
	sb.append(name);
}
}
