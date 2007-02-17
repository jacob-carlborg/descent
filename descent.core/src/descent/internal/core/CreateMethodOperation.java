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

import java.util.Iterator;
import java.util.List;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaModelStatus;
import descent.core.IJavaModelStatusConstants;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.core.Signature;
import descent.core.dom.ASTNode;
import descent.core.dom.Argument;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.SimpleName;
import descent.core.dom.rewrite.ASTRewrite;
import descent.internal.core.util.Messages;
import descent.internal.core.util.Util;
import org.eclipse.jface.text.IDocument;

/**
 * <p>This operation creates an instance method. 
 *
 * <p>Required Attributes:<ul>
 *  <li>Containing type
 *  <li>The source code for the method. No verification of the source is
 *      performed.
 * </ul>
 */
public class CreateMethodOperation extends CreateTypeMemberOperation {
	
	protected String[] parameterTypes;
	
/**
 * When executed, this operation will create a method
 * in the given type with the specified source.
 */
public CreateMethodOperation(IType parentElement, String source, boolean force) {
	super(parentElement, source, force);
}
/**
 * Returns the type signatures of the parameter types of the
 * current <code>MethodDeclaration</code>
 */
protected String[] convertASTMethodTypesToSignatures() {
	if (this.parameterTypes == null) {
		if (this.createdNode != null) {
			FunctionDeclaration methodDeclaration = (FunctionDeclaration) this.createdNode;
			List parameters = methodDeclaration.arguments();
			int size = parameters.size();
			this.parameterTypes = new String[size];
			Iterator iterator = parameters.iterator();
			// convert the AST types to signatures
			for (int i = 0; i < size; i++) {
				Argument parameter = (Argument) iterator.next();
				String typeSig = Util.getSignature(parameter.getType());
				int extraDimensions = 0; // parameter.getExtraDimensions();
				if (methodDeclaration.isVariadic() && i == size-1)
					extraDimensions++;
				this.parameterTypes[i] = Signature.createArraySignature(typeSig, extraDimensions);
			}
		}
	}
	return this.parameterTypes;
}
protected ASTNode generateElementAST(ASTRewrite rewriter, IDocument document, ICompilationUnit cu) throws JavaModelException {
	ASTNode node = super.generateElementAST(rewriter, document, cu);
	if (node.getNodeType() != ASTNode.FUNCTION_DECLARATION)
		throw new JavaModelException(new JavaModelStatus(IJavaModelStatusConstants.INVALID_CONTENTS));
	return node;
}
/**
 * @see CreateElementInCUOperation#generateResultHandle
 */
protected IJavaElement generateResultHandle() {
	String[] types = convertASTMethodTypesToSignatures();
	String name = getASTNodeName();
	return getType().getMethod(name, types);
}
private String getASTNodeName() {
	return ((FunctionDeclaration) this.createdNode).getName().getIdentifier();
}
/**
 * @see CreateElementInCUOperation#getMainTaskName()
 */
public String getMainTaskName(){
	return Messages.operation_createMethodProgress; 
}
protected SimpleName rename(ASTNode node, SimpleName newName) {
	FunctionDeclaration method = (FunctionDeclaration) node;
	SimpleName oldName = method.getName();
	method.setName(newName);
	return oldName;
}
/**
 * @see CreateTypeMemberOperation#verifyNameCollision
 */
protected IJavaModelStatus verifyNameCollision() {
	if (this.createdNode != null) {
		IType type = getType();
		String name;
		// if (((FunctionDeclaration) this.createdNode).isConstructor())
		// name = type.getElementName();
		// else
			name = getASTNodeName();
		String[] types = convertASTMethodTypesToSignatures();
		if (type.getMethod(name, types).exists()) {
			return new JavaModelStatus(
				IJavaModelStatusConstants.NAME_COLLISION, 
				Messages.bind(Messages.status_nameCollision, name)); 
		}
	}
	return JavaModelStatus.VERIFIED_OK;
}
}
