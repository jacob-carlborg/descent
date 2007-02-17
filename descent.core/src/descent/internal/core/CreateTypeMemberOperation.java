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

import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;

import descent.core.ICompilationUnit;
import descent.core.IJavaElement;
import descent.core.IJavaModelStatus;
import descent.core.IJavaModelStatusConstants;
import descent.core.IJavaProject;
import descent.core.IType;
import descent.core.JavaModelException;
import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.ASTParser;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.AlignDeclaration;
import descent.core.dom.CompilationUnit;
import descent.core.dom.DebugDeclaration;
import descent.core.dom.EnumDeclaration;
import descent.core.dom.ExternDeclaration;
import descent.core.dom.IftypeDeclaration;
import descent.core.dom.ModifierDeclaration;
import descent.core.dom.PragmaDeclaration;
import descent.core.dom.SimpleName;
import descent.core.dom.StaticIfDeclaration;
import descent.core.dom.StructuralPropertyDescriptor;
import descent.core.dom.TemplateDeclaration;
import descent.core.dom.VersionDeclaration;
import descent.core.dom.rewrite.ASTRewrite;
import descent.core.formatter.IndentManipulation;
import descent.internal.compiler.parser.ScannerHelper;

/**
 * Implements functionality common to
 * operations that create type members.
 */
public abstract class CreateTypeMemberOperation extends CreateElementInCUOperation {
	/**
	 * The source code for the new member.
	 */
	protected String source = null;
	/**
	 * The name of the <code>ASTNode</code> that may be used to
	 * create this new element.
	 * Used by the <code>CopyElementsOperation</code> for renaming
	 */
	protected String alteredName;
	/**
	 * The AST node representing the element that
	 * this operation created.
	 */
	 protected ASTNode createdNode;
/**
 * When executed, this operation will create a type member
 * in the given parent element with the specified source.
 */
public CreateTypeMemberOperation(IJavaElement parentElement, String source, boolean force) {
	super(parentElement);
	this.source = source;
	this.force = force;
}
protected StructuralPropertyDescriptor getChildPropertyDescriptor(ASTNode parent) {
	// TODO how to decide if "then" or "else" declarations property?
	switch (parent.getNodeType()) {
	case ASTNode.AGGREGATE_DECLARATION:
		return AggregateDeclaration.DECLARATIONS_PROPERTY;
	case ASTNode.ALIGN_DECLARATION:
		return AlignDeclaration.DECLARATIONS_PROPERTY;
	case ASTNode.DEBUG_DECLARATION:
		return DebugDeclaration.THEN_DECLARATIONS_PROPERTY;
	case ASTNode.IFTYPE_DECLARATION:
		return IftypeDeclaration.THEN_DECLARATIONS_PROPERTY;
	case ASTNode.STATIC_IF_DECLARATION:
		return StaticIfDeclaration.THEN_DECLARATIONS_PROPERTY;
	case ASTNode.VERSION_DECLARATION:
		return VersionDeclaration.THEN_DECLARATIONS_PROPERTY;
	case ASTNode.ENUM_DECLARATION:
		return EnumDeclaration.ENUM_MEMBERS_PROPERTY;
	case ASTNode.EXTERN_DECLARATION:
		return ExternDeclaration.DECLARATIONS_PROPERTY;
	case ASTNode.MODIFIER_DECLARATION:
		return ModifierDeclaration.DECLARATIONS_PROPERTY;
	case ASTNode.PRAGMA_DECLARATION:
		return PragmaDeclaration.DECLARATIONS_PROPERTY;
	case ASTNode.TEMPLATE_DECLARATION:
		return TemplateDeclaration.DECLARATIONS_PROPERTY;
	default:
		return CompilationUnit.DECLARATIONS_PROPERTY;
	}
}
protected ASTNode generateElementAST(ASTRewrite rewriter, IDocument document, ICompilationUnit cu) throws JavaModelException {
	if (this.createdNode == null) {
		this.source = removeIndentAndNewLines(this.source, document, cu);
		ASTParser parser = ASTParser.newParser(AST.D1);
		parser.setSource(this.source.toCharArray());
		parser.setProject(getCompilationUnit().getJavaProject());
		parser.setKind(ASTParser.K_DECLARATIONS);
		ASTNode node = parser.createAST(this.progressMonitor);
		String createdNodeSource;
		if (node.getNodeType() != ASTNode.COMPILATION_UNIT) {
			createdNodeSource = generateSyntaxIncorrectAST();
			if (this.createdNode == null)
				throw new JavaModelException(new JavaModelStatus(IJavaModelStatusConstants.INVALID_CONTENTS));
		} else {
			CompilationUnit typeDeclaration = (CompilationUnit) node;
			this.createdNode = typeDeclaration.declarations().iterator().next();
			createdNodeSource = this.source;
		}
		if (this.alteredName != null) {
			SimpleName newName = this.createdNode.getAST().newSimpleName(this.alteredName);
			SimpleName oldName = rename(this.createdNode, newName);
			int nameStart = oldName.getStartPosition();
			int nameEnd = nameStart + oldName.getLength();
			StringBuffer newSource = new StringBuffer();
			if (this.source.equals(createdNodeSource)) {
				newSource.append(createdNodeSource.substring(0, nameStart));
				newSource.append(this.alteredName);
				newSource.append(createdNodeSource.substring(nameEnd));
			} else {
				// syntactically incorrect source
				int createdNodeStart = this.createdNode.getStartPosition();
				int createdNodeEnd = createdNodeStart + this.createdNode.getLength();
				newSource.append(createdNodeSource.substring(createdNodeStart, nameStart));
				newSource.append(this.alteredName);
				newSource.append(createdNodeSource.substring(nameEnd, createdNodeEnd));
				
			}
			this.source = newSource.toString();
		}
	}
	if (rewriter == null) return this.createdNode;
	// return a string place holder (instead of the created node) so has to not lose comments and formatting
	return rewriter.createStringPlaceholder(this.source, this.createdNode.getNodeType());
}
private String removeIndentAndNewLines(String code, IDocument document, ICompilationUnit cu) {
	IJavaProject project = cu.getJavaProject();
	Map options = project.getOptions(true/*inherit JavaCore options*/);
	int tabWidth = IndentManipulation.getTabWidth(options);
	int indentWidth = IndentManipulation.getIndentWidth(options);
	int indent = IndentManipulation.measureIndentUnits(code, tabWidth, indentWidth);
	int firstNonWhiteSpace = -1;
	int length = code.length();
	while (firstNonWhiteSpace < length-1)
		if (!ScannerHelper.isWhitespace(code.charAt(++firstNonWhiteSpace)))
			break;
	int lastNonWhiteSpace = length;
	while (lastNonWhiteSpace > 0)
		if (!ScannerHelper.isWhitespace(code.charAt(--lastNonWhiteSpace)))
			break;
	String lineDelimiter = TextUtilities.getDefaultLineDelimiter(document);
	return IndentManipulation.changeIndent(code.substring(firstNonWhiteSpace, lastNonWhiteSpace+1), indent, tabWidth, indentWidth, "", lineDelimiter); //$NON-NLS-1$
}
/*
 * Renames the given node to the given name.
 * Returns the old name.
 */
protected abstract SimpleName rename(ASTNode node, SimpleName newName);
/**
 * Generates an <code>ASTNode</code> based on the source of this operation
 * when there is likely a syntax error in the source.
 * Returns the source used to generate this node.
 */
protected String generateSyntaxIncorrectAST() {
	//create some dummy source to generate an ast node
	StringBuffer buff = new StringBuffer();
	IType type = getType();
	String lineSeparator = descent.internal.core.util.Util.getLineSeparator(this.source, type == null ? null : type.getJavaProject());
	buff.append(lineSeparator + " public class A {" + lineSeparator); //$NON-NLS-1$
	buff.append(this.source);
	buff.append(lineSeparator).append('}');
	ASTParser parser = ASTParser.newParser(AST.D1);
	parser.setSource(buff.toString().toCharArray());
	CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
	if (compilationUnit.declarations().size() != 0)
	this.createdNode = compilationUnit.declarations().iterator().next();
	return buff.toString();
}
/**
 * Returns the IType the member is to be created in.
 */
protected IType getType() {
	return (IType)getParentElement();
}
/**
 * Sets the name of the <code>ASTNode</code> that will be used to
 * create this new element.
 * Used by the <code>CopyElementsOperation</code> for renaming
 */
protected void setAlteredName(String newName) {
	this.alteredName = newName;
}
/**
 * Possible failures: <ul>
 *  <li>NO_ELEMENTS_TO_PROCESS - the parent element supplied to the operation is
 * 		<code>null</code>.
 *	<li>INVALID_CONTENTS - The source is <code>null</code> or has serious syntax errors.
  *	<li>NAME_COLLISION - A name collision occurred in the destination
 * </ul>
 */
public IJavaModelStatus verify() {
	IJavaModelStatus status = super.verify();
	if (!status.isOK()) {
		return status;
	}
	if (this.source == null) {
		return new JavaModelStatus(IJavaModelStatusConstants.INVALID_CONTENTS);
	}
	if (!force) {
		//check for name collisions
		try {
			ICompilationUnit cu = getCompilationUnit();
			generateElementAST(null, getDocument(cu), cu);
		} catch (JavaModelException jme) {
			return jme.getJavaModelStatus();
		}
		return verifyNameCollision();
	}
	
	return JavaModelStatus.VERIFIED_OK;
}
/**
 * Verify for a name collision in the destination container.
 */
protected IJavaModelStatus verifyNameCollision() {
	return JavaModelStatus.VERIFIED_OK;
}
}
