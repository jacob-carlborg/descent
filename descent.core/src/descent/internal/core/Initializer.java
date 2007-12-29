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

import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.Flags;
import descent.core.IInitializer;
import descent.core.IJavaElement;
import descent.core.IJavaModelStatusConstants;
import descent.core.IMember;
import descent.core.ISourceManipulation;
import descent.core.ISourceRange;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.internal.core.util.Util;

/**
 * @see IInitializer
 */

/* package */ class Initializer extends Member implements IInitializer {
	
private String displayString;

protected Initializer(JavaElement parent, int count) {
	this(parent, count, "");
}

protected Initializer(JavaElement parent, int count, String displayString) {
	super(parent);
	// 0 is not valid: this first occurrence is occurrence 1.
	if (count <= 0)
		throw new IllegalArgumentException();
	this.displayString = displayString;
	this.occurrenceCount = count;
}
public boolean equals(Object o) {
	if (!(o instanceof Initializer)) return false;
	return super.equals(o);
}
/**
 * @see IJavaElement
 */
public int getElementType() {
	return INITIALIZER;
}
/**
 * @see JavaElement#getHandleMemento(StringBuffer)
 */
protected void getHandleMemento(StringBuffer buff) {
	((JavaElement)getParent()).getHandleMemento(buff);
	buff.append(getHandleMementoDelimiter());
	buff.append(this.occurrenceCount);
}
/**
 * @see JavaElement#getHandleMemento()
 */
protected char getHandleMementoDelimiter() {
	return JavaElement.JEM_INITIALIZER;
}
public int hashCode() {
	return Util.combineHashCodes(this.parent.hashCode(), this.occurrenceCount);
}
/**
 */
public String readableName() {

	return ((JavaElement)getDeclaringType()).readableName();
}
/**
 * @see ISourceManipulation
 */
public void rename(String newName, boolean force, IProgressMonitor monitor) throws JavaModelException {
	throw new JavaModelException(new JavaModelStatus(IJavaModelStatusConstants.INVALID_ELEMENT_TYPES, this));
}
/**
 * @see IMember
 */
public ISourceRange getNameRange() {
	return null;
}
@Override
public String getElementName() {
	return displayString;
}
/*
 * @see JavaElement#getPrimaryElement(boolean)
 */
public IJavaElement getPrimaryElement(boolean checkOwner) {
	if (checkOwner) {
		CompilationUnit cu = (CompilationUnit)getAncestor(COMPILATION_UNIT);
		if (cu == null || cu.isPrimary()) return this;
	}
	IJavaElement primaryParent = this.parent.getPrimaryElement(false);
	return ((IType)primaryParent).getInitializer(this.occurrenceCount);
}
public boolean isStaticConstructor() throws JavaModelException {
	long flags = getFlags();
	return !Flags.isStaticDestructor(flags)
		&& !Flags.isInvariant(flags)
		&& !Flags.isUnitTest(flags)
		&& !Flags.isStaticAssert(flags)
		&& !Flags.isDebugAssignment(flags)
		&& !Flags.isVersionAssignment(flags)
		&& !Flags.isAlign(flags)
		&& !Flags.isExternDeclaration(flags)
		&& !Flags.isPragma(flags)
		&& !Flags.isThen(flags)
		&& !Flags.isElse(flags)
		&& !Flags.isMixin(flags);
}
public boolean isStaticDestructor() throws JavaModelException {
	return Flags.isStaticDestructor(getFlags());
}
public boolean isInvariant() throws JavaModelException {
	return Flags.isInvariant(getFlags());
}
public boolean isUnitTest() throws JavaModelException {
	return Flags.isUnitTest(getFlags());
}
public boolean isStaticAssert() throws JavaModelException {
	return Flags.isStaticAssert(getFlags());
}
public boolean isVersionAssignment() throws JavaModelException {
	return Flags.isVersionAssignment(getFlags());
}
public boolean isDebugAssignment() throws JavaModelException {
	return Flags.isDebugAssignment(getFlags());
}
public boolean isAlign() throws JavaModelException {
	return Flags.isAlign(getFlags());
}
public boolean isExtern() throws JavaModelException {
	return Flags.isExternDeclaration(getFlags());
}
public boolean isPragma() throws JavaModelException {
	return Flags.isPragma(getFlags());
}
public boolean isThen() throws JavaModelException {
	return Flags.isThen(getFlags());
}
public boolean isElse() throws JavaModelException {
	return Flags.isElse(getFlags());
}
public boolean isMixin() throws JavaModelException {
	return Flags.isMixin(getFlags());
}
/**
 * @private Debugging purposes
 */
protected void toStringInfo(int tab, StringBuffer buffer, Object info, boolean showResolvedInfo) {
	buffer.append(this.tabString(tab));
	if (info == null) {
		buffer.append("<initializer #"); //$NON-NLS-1$
		buffer.append(this.occurrenceCount);
		buffer.append("> (not open)"); //$NON-NLS-1$
	} else if (info == NO_INFO) {
		buffer.append("<initializer #"); //$NON-NLS-1$
		buffer.append(this.occurrenceCount);
		buffer.append(">"); //$NON-NLS-1$
	} else {
		try {
			buffer.append("<"); //$NON-NLS-1$
			if (Flags.isStatic(this.getFlags())) {
				buffer.append("static "); //$NON-NLS-1$
			}
			buffer.append("initializer #"); //$NON-NLS-1$
			buffer.append(this.occurrenceCount);
			buffer.append(">"); //$NON-NLS-1$
		} catch (JavaModelException e) {
			buffer.append("<JavaModelException in toString of " + getElementName()); //$NON-NLS-1$
		}
	}
}
}
