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

import org.eclipse.core.runtime.IStatus;
import descent.core.ICompilationUnit;
import descent.core.IImportDeclaration;
import descent.core.IJavaElement;
import descent.core.IJavaModelStatus;
import descent.core.IJavaModelStatusConstants;
import descent.core.IType;
import descent.core.JavaConventions;
import descent.core.JavaModelException;
import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.CompilationUnit;
import descent.core.dom.Name;
import descent.core.dom.ModuleDeclaration;
import descent.core.dom.StructuralPropertyDescriptor;
import descent.core.dom.rewrite.ASTRewrite;
import descent.internal.core.util.Messages;
import org.eclipse.jface.text.IDocument;

/**
 * <p>This operation adds/replaces a package declaration in an existing compilation unit.
 * If the compilation unit already includes the specified package declaration,
 * it is not generated (it does not generate duplicates).
 *
 * <p>Required Attributes:<ul>
 *  <li>Compilation unit element
 *  <li>Package name
 * </ul>
 */
public class CreatePackageDeclarationOperation extends CreateElementInCUOperation {
	/**
	 * The name of the package declaration being created
	 */
	protected String name = null;
/**
 * When executed, this operation will add a package declaration to the given compilation unit.
 */
public CreatePackageDeclarationOperation(String name, ICompilationUnit parentElement) {
	super(parentElement);
	this.name= name;
}
protected StructuralPropertyDescriptor getChildPropertyDescriptor(ASTNode parent) {
	return CompilationUnit.MODULE_DECLARATION_PROPERTY;
}
protected ASTNode generateElementAST(ASTRewrite rewriter, IDocument document, ICompilationUnit cu) throws JavaModelException {
	//look for an existing package declaration
	IJavaElement[] children = getCompilationUnit().getChildren();
	for (int i = 0; i < children.length; i++) {
		if (children[i].getElementType() ==  IJavaElement.PACKAGE_DECLARATION && this.name.equals(children[i].getElementName())) {
			//equivalent package declaration already exists
			this.creationOccurred = false;
			return null;
		}
	}
	AST ast = this.cuAST.getAST();
	ModuleDeclaration pkgDeclaration = ast.newModuleDeclaration();
	Name astName = ast.newName(this.name);
	pkgDeclaration.setName(astName);
	return pkgDeclaration;
}
/**
 * Creates and returns the handle for the element this operation created.
 */
protected IJavaElement generateResultHandle() {
	return getCompilationUnit().getPackageDeclaration(this.name);
}
/**
 * @see CreateElementInCUOperation#getMainTaskName()
 */
public String getMainTaskName(){
	return Messages.operation_createPackageProgress; 
}
/**
 * Sets the correct position for new package declaration:<ul>
 * <li> before the first import
 * <li> if no imports, before the first type
 * <li> if no type - first thing in the CU
 * <li> 
 */
protected void initializeDefaultPosition() {
	try {
		ICompilationUnit cu = getCompilationUnit();
		IImportDeclaration[] imports = cu.getImports();
		if (imports.length > 0) {
			createBefore(imports[0]);
			return;
		}
		IType[] types = cu.getTypes();
		if (types.length > 0) {
			createBefore(types[0]);
			return;
		}
	} catch (JavaModelException e) {
		// cu doesn't exist: ignore
	}
}
/**
 * Possible failures: <ul>
 *  <li>NO_ELEMENTS_TO_PROCESS - no compilation unit was supplied to the operation 
 *  <li>INVALID_NAME - a name supplied to the operation was not a valid
 * 		package declaration name.
 * </ul>
 * @see IJavaModelStatus
 * @see JavaConventions
 */
public IJavaModelStatus verify() {
	IJavaModelStatus status = super.verify();
	if (!status.isOK()) {
		return status;
	}
	if (JavaConventions.validatePackageName(this.name).getSeverity() == IStatus.ERROR) {
		return new JavaModelStatus(IJavaModelStatusConstants.INVALID_NAME, this.name);
	}
	return JavaModelStatus.VERIFIED_OK;
}
}
