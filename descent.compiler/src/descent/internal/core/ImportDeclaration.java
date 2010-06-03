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

import org.eclipse.core.runtime.Assert;

import descent.core.IImportDeclaration;
import descent.core.IJavaElement;
import descent.core.JavaModelException;
import descent.core.compiler.CharOperation;

/**
 * Handle for an import declaration. Info object is a ImportDeclarationElementInfo.
 * @see IImportDeclaration
 */

public class ImportDeclaration extends SourceRefElement implements IImportDeclaration {

	protected String name;
	protected String alias;
	protected String[] selectiveImportsNames;
	protected String[] selectiveImportsAliases;
	
/**
 * Constructs an ImportDeclaration in the given import container
 * with the given name.
 */
protected ImportDeclaration(JavaElement parent, String name, String alias, String[] selectiveImportsNames, String[] selectiveImportsAliases) {
	super(parent);
	this.name = name;
	this.alias = alias;
	this.selectiveImportsNames = selectiveImportsNames;
	this.selectiveImportsAliases = selectiveImportsAliases;
}
public boolean equals(Object o) {
	if (!(o instanceof ImportDeclaration)) return false;
	return super.equals(o);
}
public String getElementName() {
	return this.name;
}
public String getAlias() {
	return alias;
}
public String[] getSelectiveImportsAliases() {
	if (selectiveImportsAliases == null) {
		return CharOperation.NO_STRINGS;
	}
	return selectiveImportsAliases;
}
public String[] getSelectiveImportsNames() {
	if (selectiveImportsNames == null) {
		return CharOperation.NO_STRINGS;
	}
	return selectiveImportsNames;
}
/**
 * @see IJavaElement
 */
public int getElementType() {
	return IMPORT_DECLARATION;
}
/**
 * @see descent.core.IImportDeclaration#getFlags()
 */
public long getFlags() throws JavaModelException {
	ImportDeclarationElementInfo info = (ImportDeclarationElementInfo)getElementInfo();
	return info.getModifiers();
}
/**
 * @see JavaElement#getHandleMemento(StringBuffer)
 * For import declarations, the handle delimiter is associated to the import container already
 */
protected void getHandleMemento(StringBuffer buff) {
	((JavaElement)getParent()).getHandleMemento(buff);
	escapeMementoName(buff, getElementName());
	if (this.occurrenceCount > 1) {
		buff.append(JEM_COUNT);
		buff.append(this.occurrenceCount);
	}
}
/**
 * @see JavaElement#getHandleMemento()
 */
protected char getHandleMementoDelimiter() {
	// For import declarations, the handle delimiter is associated to the import container already
	Assert.isTrue(false, "Should not be called"); //$NON-NLS-1$
	return 0;
}
/*
 * @see JavaElement#getPrimaryElement(boolean)
 */
public IJavaElement getPrimaryElement(boolean checkOwner) {
	CompilationUnit cu = (CompilationUnit)this.parent.getParent();
	if (checkOwner && cu.isPrimary()) return this;
	return cu.getImport(getElementName());
}
/**
 */
public String readableName() {

	return null;
}
/**
 * @private Debugging purposes
 */
protected void toStringInfo(int tab, StringBuffer buffer, Object info, boolean showResolvedInfo) {
	buffer.append(this.tabString(tab));
	buffer.append("import "); //$NON-NLS-1$
	toStringName(buffer);
	if (info == null) {
		buffer.append(" (not open)"); //$NON-NLS-1$
	}
}
/*
 * (non-Javadoc)
 * @see descent.internal.core.JavaElement#appendElementSignature(java.lang.StringBuilder)
 */
@Override
protected void appendElementSignature(StringBuilder sb) throws JavaModelException {
	// Nothing
}
}
