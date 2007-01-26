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
package descent.core.dom;

import java.util.List;

/**
 * Internal AST visitor for serializing an AST in a quick and dirty fashion.
 * For various reasons the resulting string is not necessarily legal
 * Java code; and even if it is legal Java code, it is not necessarily the string
 * that corresponds to the given AST. Although useless for most purposes, it's
 * fine for generating debug print strings.
 * <p>
 * Example usage:
 * <code>
 * <pre>
 *    NaiveASTFlattener p = new NaiveASTFlattener();
 *    node.accept(p);
 *    String result = p.getResult();
 * </pre>
 * </code>
 * Call the <code>reset</code> method to clear the previous result before reusing an
 * existing instance.
 * </p>
 */
class NaiveASTFlattener extends ASTVisitor {
	
	private boolean DONT_PRINT_LAST_ONE = false;
	private boolean PRINT_LAST_ONE = true;
	
	/**
	 * The string buffer into which the serialized representation of the AST is
	 * written.
	 */
	private StringBuffer buffer;
	
	private int indent = 0;
	
	/**
	 * Creates a new AST printer.
	 */
	NaiveASTFlattener() {
		this.buffer = new StringBuffer();
	}
	
	/**
	 * Returns the string accumulated in the visit.
	 *
	 * @return the serialized 
	 */
	public String getResult() {
		return this.buffer.toString();
	}
	
	/**
	 * Resets this printer so that it can be used again.
	 */
	public void reset() {
		this.buffer.setLength(0);
	}
	
	void printIndent() {
		for (int i = 0; i < this.indent; i++) 
			this.buffer.append("  "); //$NON-NLS-1$
	}
	
	/**
	 * Appends the text representation of the given modifier flags, followed by a single space.
	 * 
	 * @param ext the list of modifier and annotation nodes
	 * (element type: <code>IExtendedModifiers</code>)
	 */
	void printModifiers(List<Modifier> ext) {
		for(Modifier p : ext) {
			p.accept(this);
			this.buffer.append(" ");//$NON-NLS-1$
		}
	}
	
	void printPreDDocss(List<? extends ASTNode> ext) {
		for(ASTNode p : ext) {
			p.accept(this);
			this.buffer.append("\n");//$NON-NLS-1$
		}
	}
	
	void printList(List<? extends ASTNode> ext, String separator, boolean printLastOne) {
		int i = 0;
		int size = ext.size();
		for(ASTNode p : ext) {
			p.accept(this);
			if (i != size - 1 || printLastOne) {
				this.buffer.append(separator);
			}
			i++;
		}
	}
	
	@Override
	public boolean visit(AggregateDeclaration node) {
		printPreDDocss(node.preDDocs());
		printIndent();
		printModifiers(node.modifiers());
		this.buffer.append(node.getKind().getToken());
		this.buffer.append(" ");
		node.getName().accept(this);
		if (!node.templateParameters().isEmpty()) {
			this.buffer.append("(");
			printList(node.templateParameters(), ", ", DONT_PRINT_LAST_ONE);
			this.buffer.append(")");
		}
		this.buffer.append(" ");
		if (!node.baseClasses().isEmpty()) {
			this.buffer.append(" : ");
			printList(node.baseClasses(), ", ", DONT_PRINT_LAST_ONE);
		}
		this.buffer.append("{\n");
		this.indent++;
		printList(node.declarations(), "\n", PRINT_LAST_ONE);
		this.indent--;
		printIndent();
		this.buffer.append("}");
		if (node.getPostDDoc() != null) {
			node.getPostDDoc().accept(this);
		}
		return false;
	}
	
	@Override
	public boolean visit(AliasDeclaration node) {
		printPreDDocss(node.preDDocs());
		printIndent();
		printModifiers(node.modifiers());
		this.buffer.append("alias ");
		node.getType().accept(this);
		this.buffer.append(" ");
		printList(node.fragments(), ", ", DONT_PRINT_LAST_ONE);
		this.buffer.append(";");
		if (node.getPostDDoc() != null) {
			node.getPostDDoc().accept(this);
		}
		return false;
	}
	
	@Override
	public boolean visit(AliasDeclarationFragment node) {
		node.getName().accept(this);
		return false;
	}
	
	@Override
	public boolean visit(AliasTemplateParameter node) {
		this.buffer.append("alias ");
		node.getName().accept(this);
		if (node.getSpecificType() != null) {
			this.buffer.append(" : ");
			node.getSpecificType().accept(this);
		}
		if (node.getDefaultType() != null) {
			this.buffer.append(" = ");
			node.getDefaultType().accept(this);
		}
		return false;
	}
	
	@Override
	public boolean visit(AlignDeclaration node) {
		printPreDDocss(node.preDDocs());
		printIndent();
		printModifiers(node.modifiers());
		this.buffer.append("align(");
		this.buffer.append(node.getAlign());
		this.buffer.append(") {\n");
		this.indent++;
		printList(node.declarations(), "\n", PRINT_LAST_ONE);
		this.indent--;
		printIndent();
		this.buffer.append("}");
		if (node.getPostDDoc() != null) {
			node.getPostDDoc().accept(this);
		}
		return false;
	}
	
	@Override
	public boolean visit(Argument node) {
		switch(node.getPassageMode()) {
		case DEFAULT: break;
		case IN: this.buffer.append("in "); break;
		case INOUT: this.buffer.append("inout "); break;
		case LAZY: this.buffer.append("lazy "); break;
		case OUT: this.buffer.append("out "); break;
		}
		if (node.getType() != null) {
			node.getType().accept(this);
			this.buffer.append(" ");
		}
		node.getName().accept(this);
		if (node.getDefaultValue() != null) {
			this.buffer.append(" = ");
			node.getDefaultValue().accept(this);
		}
		return false;
	}
	
	@Override
	public boolean visit(ArrayAccess node) {
		node.getArray().accept(this);
		this.buffer.append("[");
		printList(node.indexes(), ", ", DONT_PRINT_LAST_ONE);
		this.buffer.append("]");
		return false;
	}
	
	@Override
	public boolean visit(ArrayInitializer node) {
		this.buffer.append("[");
		printList(node.fragments(), ", ", DONT_PRINT_LAST_ONE);
		this.buffer.append("]");
		return false;
	}
	
	@Override
	public boolean visit(ArrayInitializerFragment node) {
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
			this.buffer.append(": ");
		}
		node.getInitializer().accept(this);
		return false;
	}
	
	@Override
	public boolean visit(ArrayLiteral node) {
		this.buffer.append("[");
		printList(node.arguments(), ", ", DONT_PRINT_LAST_ONE);
		this.buffer.append("]");
		return false;
	}
	
	@Override
	public boolean visit(AsmStatement node) {
		this.buffer.append("asm {\n");
		this.indent++;
		// TODO complete when Asm statement is done
		this.indent--;
		this.buffer.append("}\n");
		return false;
	}
	
	@Override
	public boolean visit(AssertExpression node) {
		this.buffer.append("assert(");
		node.getExpression().accept(this);
		if (node.getMessage() != null) {
			this.buffer.append(", ");
			node.getMessage().accept(this);
		}
		this.buffer.append(")");
		return false;
	}
	
	@Override
	public boolean visit(Assignment node) {
		node.getLeftHandSide().accept(this);
		this.buffer.append(" ");
		this.buffer.append(node.getOperator().toString());
		this.buffer.append(" ");
		node.getRightHandSide().accept(this);
		return false;
	}
	
	@Override
	public boolean visit(AssociativeArrayType node) {
		node.getComponentType().accept(this);
		this.buffer.append("[");
		node.getKeyType().accept(this);
		this.buffer.append("]");
		return false;
	}
	
	@Override
	public boolean visit(BaseClass node) {
		if (node.getModifier() != null) {
			node.getModifier().accept(this);
			this.buffer.append(" ");
		}
		node.getType().accept(this);
		return false;
	}
	
	@Override
	public boolean visit(Block node) {
		printIndent();
		this.buffer.append("{\n");
		this.indent++;
		printList(node.statements(), "\n", PRINT_LAST_ONE);
		this.indent--;
		printIndent();
		this.buffer.append("}");
		return false;
	}
	
	@Override
	public boolean visit(BooleanLiteral node) {
		this.buffer.append(node.booleanValue());
		return false;
	}
	
	@Override
	public boolean visit(BreakStatement node) {
		printIndent();
		this.buffer.append("break");
		if (node.getLabel() != null) {
			this.buffer.append(" ");
			node.getLabel().accept(this);
		}
		this.buffer.append(";");
		return super.visit(node);
	}
	
	@Override
	public boolean visit(CallExpression node) {
		node.getExpression().accept(this);
		this.buffer.append("(");
		printList(node.arguments(), ", ", DONT_PRINT_LAST_ONE);
		this.buffer.append(")");
		return false;
	}
	
	@Override
	public boolean visit(CaseStatement node) {
		printIndent();
		this.buffer.append("case ");
		node.getExpression().accept(this);
		this.buffer.append(":\n");
		this.indent++;
		node.getBody().accept(this);
		this.buffer.append("\n");
		this.indent--;
		return false;
	}
	
	@Override
	public boolean visit(CastExpression node) {
		this.buffer.append("cast(");
		node.getType().accept(this);
		this.buffer.append(") ");
		node.getExpression().accept(this);
		return false;
	}
	
	@Override
	public boolean visit(CatchClause node) {
		printIndent();
		this.buffer.append("catch");
		if (node.getType() != null) {
			this.buffer.append("(");
			node.getType().accept(this);
			this.buffer.append(" ");
			node.getName().accept(this);
			this.buffer.append(")");
		}
		node.getBody().accept(this);
		this.buffer.append("\n");
		return false;
	}
	
	@Override
	public boolean visit(CharacterLiteral node) {
		this.buffer.append(node.getEscapedValue());
		return false;
	}
	
	@Override
	public boolean visit(Comment node) {
		switch(node.getKind()) {
		case BLOCK_COMMENT: this.buffer.append("/* */\n"); break;
		case DOC_BLOCK_COMMENT: this.buffer.append("/** */\n"); break;
		case DOC_LINE_COMMENT: this.buffer.append("///\n"); break;
		case DOC_PLUS_COMMENT: this.buffer.append("/++ +/\n"); break;
		case LINE_COMMENT: this.buffer.append("//\n"); break;
		case PLUS_COMMENT: this.buffer.append("/+ +/\n"); break;
		}
		return false;
	}
	
	@Override
	public boolean visit(CompilationUnit node) {
		if (node.getScriptLine() != null) {
			node.getScriptLine().accept(this);
			this.buffer.append("\n");
		}
		
		if (node.getModuleDeclaration() != null) {
			node.getModuleDeclaration().accept(this);
			this.buffer.append("\n");
		}
		
		printList(node.declarations(), "\n", PRINT_LAST_ONE);
		return false;
	}
	
	@Override
	public boolean visit(ConditionalExpression node) {
		node.getExpression().accept(this);
		this.buffer.append(" ? ");
		node.getThenExpression().accept(this);
		this.buffer.append(" : ");
		node.getElseExpression().accept(this);
		return false;
	}
	
	@Override
	public boolean visit(ContinueStatement node) {
		printIndent();
		this.buffer.append("continue");
		if (node.getLabel() != null) {
			this.buffer.append(" ");
			node.getLabel().accept(this);
		}
		this.buffer.append(";");
		return false;
	}
	
	@Override
	public boolean visit(DebugAssignment node) {
		printPreDDocss(node.preDDocs());
		printIndent();
		printModifiers(node.modifiers());
		this.buffer.append("debug = ");
		node.getVersion().accept(this);
		this.buffer.append(";");
		if (node.getPostDDoc() != null) {
			node.getPostDDoc().accept(this);
		}
		return false;
	}
	
	@Override
	public boolean visit(DebugDeclaration node) {
		printPreDDocss(node.preDDocs());
		printIndent();
		printModifiers(node.modifiers());
		this.buffer.append("debug");
		if (node.getVersion() != null) {
			this.buffer.append("(");
			node.getVersion().accept(this);
			this.buffer.append(")");
		}
		this.buffer.append(" {\n");
		this.indent++;
		printList(node.thenDeclarations(), "\n", PRINT_LAST_ONE);
		this.indent--;
		this.buffer.append("}");
		if (!node.elseDeclarations().isEmpty()) {
			this.buffer.append(" else {\n");
			this.indent++;
			printList(node.elseDeclarations(), "\n", PRINT_LAST_ONE);
			this.indent--;
			this.buffer.append("}");
		}
		if (node.getPostDDoc() != null) {
			node.getPostDDoc().accept(this);
		}
		return false;
	}
	
	@Override
	public boolean visit(DebugStatement node) {
		printIndent();
		this.buffer.append("debug");
		if (node.getVersion() != null) {
			this.buffer.append("(");
			node.getVersion().accept(this);
			this.buffer.append(")");
		}
		this.buffer.append(" {\n");
		this.indent++;
		node.getThenBody().accept(this);
		this.indent--;
		this.buffer.append("}");
		if (node.getElseBody() != null) {
			this.buffer.append(" else {\n");
			this.indent++;
			node.getElseBody().accept(this);
			this.indent--;
			this.buffer.append("}");
		}
		return false;
	}
	
	@Override
	public boolean visit(DeclarationStatement node) {
		printIndent();
		node.getDeclaration().accept(this);
		return false;
	}
	
	@Override
	public boolean visit(DefaultStatement node) {
		printIndent();
		this.buffer.append("default: \n");
		this.indent++;
		node.getBody().accept(this);
		this.buffer.append("\n");
		this.indent--;
		return false;
	}
	
	@Override
	public boolean visit(DelegateType node) {
		node.getReturnType().accept(this);
		this.buffer.append(" ");
		if (node.isFunctionPointer()) {
			this.buffer.append("function");
		} else {
			this.buffer.append("delegate");
		}
		this.buffer.append(" (");
		printList(node.arguments(), ", ", DONT_PRINT_LAST_ONE);
		if (node.isVariadic()) {
			this.buffer.append("...");
		}
		this.buffer.append(")");
		return false;
	}
	
	@Override
	public boolean visit(DeleteExpression node) {
		this.buffer.append("delete ");
		node.getExpression().accept(this);
		return false;
	}
	
	@Override
	public boolean visit(DollarLiteral node) {
		this.buffer.append("$");
		return false;
	}
	
	@Override
	public boolean visit(DoStatement node) {
		printIndent();
		this.buffer.append("do ");
		node.getBody().accept(this);
		this.buffer.append(" while(");
		node.getExpression().accept(this);
		this.buffer.append(");");
		return false;
	}
	
	@Override
	public boolean visit(DotIdentifierExpression node) {
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
		}
		this.buffer.append(".");
		node.getName().accept(this);
		return false;
	}
	
	@Override
	public boolean visit(DotTemplateTypeExpression node) {
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
		}
		this.buffer.append(".");
		node.getTemplateType().accept(this);
		return false;
	}
	
	@Override
	public boolean visit(DynamicArrayType node) {
		node.getComponentType().accept(this);
		this.buffer.append("[]");
		return false;
	}
	
	@Override
	public boolean visit(EnumDeclaration node) {
		printPreDDocss(node.preDDocs());
		printIndent();
		printModifiers(node.modifiers());
		this.buffer.append("enum");
		if (node.getName() != null) {
			this.buffer.append(" ");
			node.getName().accept(this);
		}
		if (node.getBaseType() != null) {
			this.buffer.append(" : ");
			node.getBaseType().accept(this);
		}
		this.buffer.append(" {\n");
		this.indent++;
		printList(node.enumMembers(), ",\n", PRINT_LAST_ONE);
		this.indent--;
		this.buffer.append("}");
		if (node.getPostDDoc() != null) {
			node.getPostDDoc().accept(this);
		}
		return false;
	}
	
	@Override
	public boolean visit(EnumMember node) {
		node.getName().accept(this);
		if (node.getValue() != null) {
			this.buffer.append(" = ");
			node.getValue().accept(this);
		}
		return false;
	}
	
	@Override
	public boolean visit(ExpressionInitializer node) {
		node.getExpression().accept(this);
		return false;
	}
	
	@Override
	public boolean visit(ExpressionStatement node) {
		printIndent();
		node.getExpression().accept(this);
		this.buffer.append(";");
		return false;
	}
	
	@Override
	public boolean visit(ExternDeclaration node) {
		printPreDDocss(node.preDDocs());
		printIndent();
		printModifiers(node.modifiers());
		this.buffer.append("extern");
		switch(node.getLinkage()) {
		case C: this.buffer.append("(C)"); break;
		case CPP: this.buffer.append("(C++)"); break;
		case D: this.buffer.append("(D)"); break;
		case DEFAULT: break;
		case PASCAL: this.buffer.append("(Pascal)"); break;
		case WINDOWS: this.buffer.append("(Windows)"); break;
		}
		this.buffer.append(" {\n");
		this.indent++;
		printList(node.declarations(), "\n", PRINT_LAST_ONE);
		this.indent--;
		this.buffer.append("}");
		if (node.getPostDDoc() != null) {
			node.getPostDDoc().accept(this);
		}
		return false;
	}
	
	@Override
	public boolean visit(ForeachStatement node) {
		printIndent();
		this.buffer.append("foreach");
		if (node.isReverse()) {
			this.buffer.append("_reverse");
		}
		this.buffer.append("(");
		printList(node.arguments(), ", ", DONT_PRINT_LAST_ONE);
		this.buffer.append("; ");
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
		}
		this.buffer.append(") ");
		node.getBody().accept(this);
		this.buffer.append("\n");
		return false;
	}
	
	@Override
	public boolean visit(ForStatement node) {
		printIndent();
		this.buffer.append("for(");
		if (node.getInitializer() != null) {
			node.getInitializer().accept(this);
		}
		this.buffer.append("; ");
		if (node.getCondition() != null) {
			node.getCondition().accept(this);
		}
		this.buffer.append("; ");
		if (node.getIncrement() != null) {
			node.getIncrement().accept(this);
		}
		this.buffer.append(") {\n");
		this.indent++;
		node.getBody().accept(this);
		this.indent--;
		this.buffer.append("}");
		return false;
	}
	
	@Override
	public boolean visit(FunctionDeclaration node) {
		printPreDDocss(node.preDDocs());
		printIndent();
		printModifiers(node.modifiers());
		switch(node.getKind()) {
		case CONSTRUCTOR:
			this.buffer.append("this");
			break;
		case DELETE:
			this.buffer.append("delete");
			break;
		case DESTRUCTOR:
			this.buffer.append("~this");
			break;
		case FUNCTION:
			node.getReturnType().accept(this);
			this.buffer.append(" ");
			node.getName().accept(this);
			break;
		case NEW:
			this.buffer.append("new");
			break;
		case STATIC_CONSTRUCTOR:
			this.buffer.append("static this");
			break;
		case STATIC_DESTRUCTOR:
			this.buffer.append("static ~this");
			break;
		}
		if (!node.templateParameters().isEmpty()) {
			this.buffer.append("(");
			printList(node.templateParameters(), ", ", DONT_PRINT_LAST_ONE);
			this.buffer.append(")");
		}
		this.buffer.append("(");
		printList(node.arguments(), ", ", DONT_PRINT_LAST_ONE);
		if (node.isVariadic()) {
			this.buffer.append("...");
		}
		this.buffer.append(")");
		if (node.getPrecondition() != null) {
			this.buffer.append("\n");
			printIndent();
			this.buffer.append(" in ");
			node.getPrecondition().accept(this);
			this.buffer.append("\n");
			printIndent();
		}
		if (node.getPostcondition() != null) {
			this.buffer.append("\n");
			printIndent();
			this.buffer.append(" out ");
			node.getPostcondition().accept(this);
			this.buffer.append("\n");
			printIndent();
		}
		if (node.getPrecondition() != null || node.getPostcondition() != null) {
			this.buffer.append(" body");
		}
		this.buffer.append(" ");
		node.getBody().accept(this);
		this.buffer.append("\n");
		if (node.getPostDDoc() != null) {
			node.getPostDDoc().accept(this);
		}
		return false;
	}
	
	@Override
	public boolean visit(FunctionLiteralDeclarationExpression node) {
		switch(node.getSyntax()) {
		case DELEGATE: this.buffer.append("delegate "); break;
		case EMPTY: break;
		case FUNCTION: this.buffer.append("function "); break;
		}
		this.buffer.append("(");
		printList(node.arguments(), ", ", DONT_PRINT_LAST_ONE);
		if (node.isVariadic()) {
			this.buffer.append("...");
		}
		this.buffer.append(")");
		if (node.getPrecondition() != null) {
			this.buffer.append("\n");
			printIndent();
			this.buffer.append(" in ");
			node.getPrecondition().accept(this);
			this.buffer.append("\n");
			printIndent();
		}
		if (node.getPostcondition() != null) {
			this.buffer.append("\n");
			printIndent();
			this.buffer.append(" out ");
			node.getPostcondition().accept(this);
			this.buffer.append("\n");
			printIndent();
		}
		if (node.getPrecondition() != null || node.getPostcondition() != null) {
			this.buffer.append(" body");
		}
		this.buffer.append(" ");
		node.getBody().accept(this);
		this.buffer.append("\n");
		return false;
	}
	
	@Override
	public boolean visit(GotoCaseStatement node) {
		printIndent();
		this.buffer.append("goto case ");
		node.getLabel().accept(this);
		this.buffer.append(";");
		return false;
	}
	
	@Override
	public boolean visit(GotoDefaultStatement node) {
		printIndent();
		this.buffer.append("goto default;");
		return false;
	}
	
	@Override
	public boolean visit(GotoStatement node) {
		printIndent();
		this.buffer.append("goto ");
		node.getLabel().accept(this);
		this.buffer.append(";");
		return false;
	}
	
	@Override
	public boolean visit(NullLiteral node) {
		this.buffer.append("null");
		return false;
	}
	
	@Override
	public boolean visit(SimpleName node) {
		this.buffer.append(node.getIdentifier());
		return false;
	}
	
	@Override
	public boolean visit(PrimitiveType node) {
		this.buffer.append(node.getPrimitiveTypeCode());
		return false;
	}
	
	@Override
	public boolean visit(QualifiedName node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

}
