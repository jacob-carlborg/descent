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

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaModelStatus;
import descent.core.IJavaModelStatusConstants;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.core.dom.ASTNode;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.SimpleName;
import descent.core.dom.rewrite.ASTRewrite;
import descent.internal.core.util.Messages;
import org.eclipse.jface.text.IDocument;

/**
 * <p>This operation creates a class or interface.
 *
 * <p>Required Attributes:<ul>
 *  <li>Parent element - must be a compilation unit, or type.
 *  <li>The source code for the type. No verification of the source is
 *      performed.
 * </ul>
 */
public class CreateTypeOperation extends CreateTypeMemberOperation {
/**
 * When executed, this operation will create a type unit
 * in the given parent element (a compilation unit, type)
 */
public CreateTypeOperation(IJavaElement parentElement, String source, boolean force) {
	super(parentElement, source, force);
}
protected ASTNode generateElementAST(ASTRewrite rewriter, IDocument document, ICompilationUnit cu) throws JavaModelException {
	ASTNode node = super.generateElementAST(rewriter, document, cu);
	if (!(node instanceof AggregateDeclaration))
		throw new JavaModelException(new JavaModelStatus(IJavaModelStatusConstants.INVALID_CONTENTS));
	return node;
}

/**
 * @see CreateElementInCUOperation#generateResultHandle()
 */
protected IJavaElement generateResultHandle() {
	IJavaElement parent= getParentElement();
	switch (parent.getElementType()) {
		case IJavaElement.COMPILATION_UNIT:
			return ((ICompilationUnit)parent).getType(getASTNodeName());
		case IJavaElement.TYPE:
			return ((IType)parent).getType(getASTNodeName());
		// Note: creating local/anonymous type is not supported 
	}
	return null;
}
/**
 * @see CreateElementInCUOperation#getMainTaskName()
 */
public String getMainTaskName(){
	return Messages.operation_createTypeProgress; 
}
/**
 * Returns the <code>IType</code> the member is to be created in.
 */
protected IType getType() {
	IJavaElement parent = getParentElement();
	if (parent.getElementType() == IJavaElement.TYPE) {
		return (IType) parent;
	}
	return null;
}
/**
 * @see CreateTypeMemberOperation#verifyNameCollision
 */
protected IJavaModelStatus verifyNameCollision() {
	IJavaElement parent = getParentElement();
	switch (parent.getElementType()) {
		case IJavaElement.COMPILATION_UNIT:
			String typeName = getASTNodeName();
			if (((ICompilationUnit) parent).getType(typeName).exists()) {
				return new JavaModelStatus(
					IJavaModelStatusConstants.NAME_COLLISION, 
					Messages.bind(Messages.status_nameCollision, typeName)); 
			}
			break;
		case IJavaElement.TYPE:
			typeName = getASTNodeName();
			if (((IType) parent).getType(typeName).exists()) {
				return new JavaModelStatus(
					IJavaModelStatusConstants.NAME_COLLISION, 
					Messages.bind(Messages.status_nameCollision, typeName)); 
			}
			break;
		// Note: creating local/anonymous type is not supported 
	}
	return JavaModelStatus.VERIFIED_OK;
}
private String getASTNodeName() {
	return ((AggregateDeclaration) this.createdNode).getName().getIdentifier();
}
protected SimpleName rename(ASTNode node, SimpleName newName) {
	AggregateDeclaration type = (AggregateDeclaration) node;
	SimpleName oldName = type.getName();
	type.setName(newName);
	return oldName;
}
}
