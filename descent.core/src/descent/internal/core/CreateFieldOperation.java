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

import java.util.Iterator;

import org.eclipse.jface.text.IDocument;

import descent.core.ICompilationUnit;
import descent.core.IField;
import descent.core.IJavaElement;
import descent.core.IJavaModelStatus;
import descent.core.IJavaModelStatusConstants;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.core.dom.ASTNode;
import descent.core.dom.SimpleName;
import descent.core.dom.VariableDeclaration;
import descent.core.dom.VariableDeclarationFragment;
import descent.core.dom.rewrite.ASTRewrite;
import descent.internal.core.util.Messages;

/**
 * <p>This operation creates a field declaration in a type.
 *
 * <p>Required Attributes:<ul>
 *  <li>Containing Type
 *  <li>The source code for the declaration. No verification of the source is
 *      performed.
 * </ul>
 */
public class CreateFieldOperation extends CreateTypeMemberOperation {
/**
 * When executed, this operation will create a field with the given name
 * in the given type with the specified source.
 *
 * <p>By default the new field is positioned after the last existing field
 * declaration, or as the first member in the type if there are no
 * field declarations.
 */
public CreateFieldOperation(IType parentElement, String source, boolean force) {
	super(parentElement, source, force);
}
protected ASTNode generateElementAST(ASTRewrite rewriter, IDocument document, ICompilationUnit cu) throws JavaModelException {
	ASTNode node = super.generateElementAST(rewriter, document, cu);
	if (node.getNodeType() != ASTNode.VARIABLE_DECLARATION)
		throw new JavaModelException(new JavaModelStatus(IJavaModelStatusConstants.INVALID_CONTENTS));
	return node;
}
/**
 * @see CreateElementInCUOperation#generateResultHandle
 */
protected IJavaElement generateResultHandle() {
	return getType().getField(getASTNodeName());
}
/**
 * @see CreateElementInCUOperation#getMainTaskName()
 */
public String getMainTaskName(){
	return Messages.operation_createFieldProgress; 
}
private VariableDeclarationFragment getFragment(ASTNode node) {
	Iterator fragments =  ((VariableDeclaration) node).fragments().iterator();
	if (this.anchorElement != null) {
		VariableDeclarationFragment fragment = null;
		String fragmentName = this.anchorElement.getElementName();
		while (fragments.hasNext()) {
			fragment = (VariableDeclarationFragment) fragments.next();
			if (fragment.getName().getIdentifier().equals(fragmentName)) {
				return fragment;
			}
		}
		return fragment;
	} else {
		return (VariableDeclarationFragment) fragments.next();
	}
}
/**
 * By default the new field is positioned after the last existing field
 * declaration, or as the first member in the type if there are no
 * field declarations.
 */
protected void initializeDefaultPosition() {
	IType parentElement = getType();
	try {
		IField[] fields = parentElement.getFields();
		if (fields != null && fields.length > 0) {
			final IField lastField = fields[fields.length - 1];
			if (parentElement.isEnum()) {
				IField field = lastField;
				if (!field.isEnumConstant()) {
					createAfter(lastField);
				}
			} else {
				createAfter(lastField);
			}
		} else {
			IJavaElement[] elements = parentElement.getChildren();
			if (elements != null && elements.length > 0) {
				createBefore(elements[0]);
			}
		}
	} catch (JavaModelException e) {
		// type doesn't exist: ignore
	}
}
/**
 * @see CreateTypeMemberOperation#verifyNameCollision
 */
protected IJavaModelStatus verifyNameCollision() {
	if (this.createdNode != null) {
		IType type= getType();
		String fieldName = getASTNodeName();
		if (type.getField(fieldName).exists()) {
			return new JavaModelStatus(
				IJavaModelStatusConstants.NAME_COLLISION, 
				Messages.bind(Messages.status_nameCollision, fieldName)); 
		}
	}
	return JavaModelStatus.VERIFIED_OK;
}
private String getASTNodeName() {
	if (this.alteredName != null) return this.alteredName;
	return getFragment(this.createdNode).getName().getIdentifier();
}
protected SimpleName rename(ASTNode node, SimpleName newName) {
	VariableDeclarationFragment fragment = getFragment(node);
	SimpleName oldName = fragment.getName();
	fragment.setName(newName);
	return oldName;
}
}
