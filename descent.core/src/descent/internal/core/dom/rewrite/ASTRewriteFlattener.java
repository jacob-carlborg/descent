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
package descent.internal.core.dom.rewrite;

import java.util.List;

import descent.core.dom.*;
import descent.core.dom.Argument.PassageMode;
import descent.core.dom.ExternDeclaration.Linkage;
import descent.core.dom.FunctionLiteralDeclarationExpression.Syntax;
import descent.core.dom.IsTypeSpecializationExpression.TypeSpecialization;

public class ASTRewriteFlattener extends ASTVisitor {
	
	public static String asString(ASTNode node, RewriteEventStore store) {
		ASTRewriteFlattener flattener= new ASTRewriteFlattener(store);
		node.accept(flattener);
		return flattener.getResult();
	}
	
	private final String EMPTY= ""; //$NON-NLS-1$
	private final String LINE_END= "\n"; //$NON-NLS-1$
	protected StringBuffer result;
	private RewriteEventStore store;

	public ASTRewriteFlattener(RewriteEventStore store) {
		this.store= store;
		this.result= new StringBuffer();
	}
	
	/**
	 * Returns the string accumulated in the visit.
	 *
	 * @return the serialized 
	 */
	public String getResult() {
		// convert to a string, but lose any extra space in the string buffer by copying
		return new String(this.result.toString());
	}
	
	/**
	 * Resets this printer so that it can be used again.
	 */
	public void reset() {
		this.result.setLength(0);
	}
		
	protected List getChildList(ASTNode parent, StructuralPropertyDescriptor childProperty) {
		return (List) getAttribute(parent, childProperty);
	}
	
	protected ASTNode getChildNode(ASTNode parent, StructuralPropertyDescriptor childProperty) {
		return (ASTNode) getAttribute(parent, childProperty);
	}
	
	protected int getIntAttribute(ASTNode parent, StructuralPropertyDescriptor childProperty) {
		return ((Integer) getAttribute(parent, childProperty)).intValue();
	}
	
	protected boolean getBooleanAttribute(ASTNode parent, StructuralPropertyDescriptor childProperty) {
		return ((Boolean) getAttribute(parent, childProperty)).booleanValue();
	}
	
	protected Object getAttribute(ASTNode parent, StructuralPropertyDescriptor childProperty) {
		return this.store.getNewValue(parent, childProperty);
	}
	
	protected void visitChild(ASTNode parent, StructuralPropertyDescriptor childProperty) {
		visitChild(parent, childProperty, EMPTY, EMPTY);
	}
	
	protected void visitChild(ASTNode parent, StructuralPropertyDescriptor childProperty, String pre, String post) {
		ASTNode node = getChildNode(parent, childProperty);
		if (node == null) return;
		
		this.result.append(pre);
		node.accept(this);
		this.result.append(post);
	}
	
	protected void visitList(ASTNode parent, StructuralPropertyDescriptor childProperty, String separator) {
		List list= getChildList(parent, childProperty);
		for (int i= 0; i < list.size(); i++) {
			if (separator != null && i > 0) {
				this.result.append(separator);
			}
			((ASTNode) list.get(i)).accept(this);
		}
	}
	
	protected void visitList(ASTNode parent, StructuralPropertyDescriptor childProperty, String separator, String lead, String post) {
		List list= getChildList(parent, childProperty);
		if (!list.isEmpty()) {
			this.result.append(lead);
			for (int i= 0; i < list.size(); i++) {
				if (separator != null && i > 0) {
					this.result.append(separator);
				}
				((ASTNode) list.get(i)).accept(this);				
			}
			this.result.append(post);
		}
	}
	
	@Override
	public boolean visit(AggregateDeclaration node) {
		visitList(node, AggregateDeclaration.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, AggregateDeclaration.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		this.result.append(getAttribute(node, AggregateDeclaration.KIND_PROPERTY));
		this.result.append(" ");
		visitChild(node, AggregateDeclaration.NAME_PROPERTY);
		visitList(node, AggregateDeclaration.TEMPLATE_PARAMETERS_PROPERTY, ", ", "(", ")");
		visitList(node, AggregateDeclaration.BASE_CLASSES_PROPERTY, ", ", " : ", EMPTY);
		this.result.append(" {\n");
		visitList(node, AggregateDeclaration.DECLARATIONS_PROPERTY, LINE_END, EMPTY, LINE_END);
		this.result.append("}");
		visitChild(node, AggregateDeclaration.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(AliasDeclaration node) {
		visitList(node, AliasDeclaration.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, AliasDeclaration.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		this.result.append("alias ");
		visitChild(node, AliasDeclaration.TYPE_PROPERTY);
		this.result.append(" ");
		visitList(node, AliasDeclaration.FRAGMENTS_PROPERTY, ", ");
		this.result.append(";");
		visitChild(node, AliasDeclaration.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(AliasDeclarationFragment node) {
		visitChild(node, AliasDeclarationFragment.NAME_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(AliasTemplateParameter node) {
		this.result.append("alias ");
		visitChild(node, AliasTemplateParameter.NAME_PROPERTY);
		visitChild(node, AliasTemplateParameter.SPECIFIC_TYPE_PROPERTY, " : ", EMPTY);
		visitChild(node, AliasTemplateParameter.DEFAULT_TYPE_PROPERTY, " : ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(AlignDeclaration node) {
		visitList(node, AlignDeclaration.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, AlignDeclaration.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		this.result.append("align");
		int align = (Integer) getAttribute(node, AlignDeclaration.ALIGN_PROPERTY);
		if (node.getAlign() >= 2) {
			this.result.append("(");
			this.result.append(align);
			this.result.append(")");
		}
		this.result.append(" {\n");
		visitList(node, AlignDeclaration.DECLARATIONS_PROPERTY, LINE_END, EMPTY, LINE_END);
		this.result.append("}");
		visitChild(node, AlignDeclaration.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(Argument node) {
		boolean mustAppendSpace = true;
		
		PassageMode passageMode = (PassageMode) getAttribute(node, Argument.PASSAGE_MODE_PROPERTY);
		switch(passageMode) {
		case DEFAULT: mustAppendSpace = false; break;
		case IN: this.result.append("in"); break;
		case INOUT: this.result.append("inout"); break;
		case LAZY: this.result.append("lazy"); break;
		case OUT: this.result.append("out"); break;
		}
		
		ASTNode type = getChildNode(node, Argument.TYPE_PROPERTY);
		if (type != null) {
			if (mustAppendSpace) {
				this.result.append(" ");
			}
			type.accept(this);
			mustAppendSpace = true;
		}
		
		ASTNode name = getChildNode(node, Argument.NAME_PROPERTY);
		if (name != null) {
			if (mustAppendSpace) {
				this.result.append(" ");
			}
			name.accept(this);
		}
		visitChild(node, Argument.DEFAULT_VALUE_PROPERTY, " = ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(ArrayAccess node) {
		visitChild(node, ArrayAccess.ARRAY_PROPERTY);
		this.result.append("[");
		visitList(node, ArrayAccess.INDEXES_PROPERTY, ", ");
		this.result.append("]");
		return false;
	}
	
	@Override
	public boolean visit(ArrayInitializer node) {
		this.result.append("[");
		visitList(node, ArrayInitializer.FRAGMENTS_PROPERTY, ", ");
		this.result.append("]");
		return false;
	}
	
	@Override
	public boolean visit(ArrayInitializerFragment node) {
		visitChild(node, ArrayInitializerFragment.EXPRESSION_PROPERTY, EMPTY, ": ");
		visitChild(node, ArrayInitializerFragment.INITIALIZER_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(ArrayLiteral node) {
		this.result.append("[");
		visitList(node, ArrayLiteral.ARGUMENTS_PROPERTY, ", ");
		this.result.append("]");
		return false;
	}
	
	@Override
	public boolean visit(AsmBlock node) {
		this.result.append("asm {\n");
		visitList(node, AsmBlock.STATEMENTS_PROPERTY, LINE_END, EMPTY, LINE_END);
		this.result.append("}");
		return false;
	}
	
	@Override
	public boolean visit(AsmStatement node) {
		visitList(node, AsmStatement.TOKENS_PROPERTY, " ");
		this.result.append(";");
		return false;
	}
	
	@Override
	public boolean visit(AsmToken node) {
		this.result.append(node.getToken());
		return false;
	}
	
	@Override
	public boolean visit(AssertExpression node) {
		this.result.append("assert(");
		visitChild(node, AssertExpression.EXPRESSION_PROPERTY);
		visitChild(node, AssertExpression.MESSAGE_PROPERTY, ", ", EMPTY);
		this.result.append(")");
		return false;
	}
	
	@Override
	public boolean visit(Assignment node) {
		visitChild(node, Assignment.LEFT_HAND_SIDE_PROPERTY);
		this.result.append(" ");
		this.result.append(getAttribute(node, Assignment.OPERATOR_PROPERTY));
		this.result.append(" ");
		visitChild(node, Assignment.RIGHT_HAND_SIDE_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(AssociativeArrayType node) {
		visitChild(node, AssociativeArrayType.COMPONENT_TYPE_PROPERTY);
		this.result.append("[");
		visitChild(node, AssociativeArrayType.KEY_TYPE_PROPERTY);
		this.result.append("]");
		return false;
	}
	
	@Override
	public boolean visit(BaseClass node) {
		visitChild(node, BaseClass.MODIFIER_PROPERTY, EMPTY, " ");
		visitChild(node, BaseClass.TYPE_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(Block node) {
		this.result.append("{\n");
		visitList(node, Block.STATEMENTS_PROPERTY, LINE_END, EMPTY, LINE_END);
		this.result.append("}");
		return false;
	}
	
	@Override
	public boolean visit(BooleanLiteral node) {
		this.result.append(getBooleanAttribute(node, BooleanLiteral.BOOLEAN_VALUE_PROPERTY));
		return false;
	}
	
	@Override
	public boolean visit(BreakStatement node) {
		this.result.append("break");
		visitChild(node, BreakStatement.LABEL_PROPERTY, " ", EMPTY);
		this.result.append(";");
		return false;
	}
	
	@Override
	public boolean visit(CallExpression node) {
		visitChild(node, CallExpression.EXPRESSION_PROPERTY);
		this.result.append("(");
		visitList(node, CallExpression.ARGUMENTS_PROPERTY, ", ");
		this.result.append(")");
		return false;
	}
	
	@Override
	public boolean visit(SwitchCase node) {
		this.result.append("case ");
		visitChild(node, SwitchCase.EXPRESSION_PROPERTY);
		this.result.append(": ");
		visitChild(node, SwitchCase.BODY_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(CastExpression node) {
		this.result.append("cast(");
		visitChild(node, CastExpression.TYPE_PROPERTY);
		this.result.append(") ");
		visitChild(node, CastExpression.EXPRESSION_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(CatchClause node) {
		this.result.append("catch");
		
		ASTNode type = getChildNode(node, CatchClause.TYPE_PROPERTY);
		if (type != null) {
			this.result.append("(");
			type.accept(this);
			
			ASTNode name = getChildNode(node, CatchClause.NAME_PROPERTY);
			if (name != null) {
				this.result.append(" ");
				name.accept(this);
			}
			this.result.append(")");
		}
		this.result.append(" ");
		visitChild(node, CatchClause.BODY_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(CharacterLiteral node) {
		this.result.append(getAttribute(node, CharacterLiteral.ESCAPED_VALUE_PROPERTY));
		return false;
	}
	
	@Override
	public boolean visit(CodeComment node) {
		return false;
	}
	
	@Override
	public boolean visit(CompilationUnit node) {
		visitChild(node, CompilationUnit.MODULE_DECLARATION_PROPERTY, EMPTY, LINE_END);
		visitList(node, CompilationUnit.DECLARATIONS_PROPERTY, LINE_END, EMPTY, LINE_END);
		return false;
	}
	
	@Override
	public boolean visit(ConditionalExpression node) {
		visitChild(node, ConditionalExpression.EXPRESSION_PROPERTY);
		this.result.append(" ? ");
		visitChild(node, ConditionalExpression.THEN_EXPRESSION_PROPERTY);
		this.result.append(" : ");
		visitChild(node, ConditionalExpression.ELSE_EXPRESSION_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(ConstructorDeclaration node) {
		visitList(node, ConstructorDeclaration.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, ConstructorDeclaration.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		
		ConstructorDeclaration.Kind kind = (ConstructorDeclaration.Kind) getAttribute(node, ConstructorDeclaration.KIND_PROPERTY);
		switch(kind) {
		case CONSTRUCTOR:
			this.result.append("this");
			break;
		case DELETE:
			this.result.append("delete");
			break;
		case DESTRUCTOR:
			this.result.append("~this");
			break;
		case NEW:
			this.result.append("new");
			break;
		case STATIC_CONSTRUCTOR:
			this.result.append("static this");
			break;
		case STATIC_DESTRUCTOR:
			this.result.append("static ~this");
			break;
		}
		this.result.append("(");
		visitList(node, ConstructorDeclaration.ARGUMENTS_PROPERTY, ", ");
		
		boolean isVariadic = getBooleanAttribute(node, ConstructorDeclaration.VARIADIC_PROPERTY);
		if (isVariadic) {
			this.result.append("...");
		}
		this.result.append(")");
		
		ASTNode precondition = getChildNode(node, ConstructorDeclaration.PRECONDITION_PROPERTY);
		if (precondition != null) {
			this.result.append(LINE_END);
			this.result.append("in ");
			precondition.accept(this);
			this.result.append(LINE_END);
		}
		
		ASTNode postcondition = getChildNode(node, ConstructorDeclaration.POSTCONDITION_PROPERTY);
		if (node.getPostcondition() != null) {
			this.result.append(LINE_END);
			this.result.append("out ");
			postcondition.accept(this);
			this.result.append(LINE_END);
		}
		if (precondition != null || postcondition != null) {
			this.result.append("body");
		}
		this.result.append(" ");
		visitChild(node, ConstructorDeclaration.BODY_PROPERTY);
		visitChild(node, ConstructorDeclaration.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(ContinueStatement node) {
		this.result.append("continue");
		visitChild(node, ContinueStatement.LABEL_PROPERTY, " ", EMPTY);
		this.result.append(";");
		return false;
	}
	
	@Override
	public boolean visit(DDocComment node) {
		this.result.append(getAttribute(node, DDocComment.TEXT_PROPERTY));
		return false;
	}
	
	@Override
	public boolean visit(DebugAssignment node) {
		visitList(node, DebugAssignment.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, DebugAssignment.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		this.result.append("debug = ");
		visitChild(node, DebugAssignment.VERSION_PROPERTY);
		this.result.append(";");
		visitChild(node, DebugAssignment.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(DebugDeclaration node) {
		visitList(node, DebugDeclaration.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, DebugDeclaration.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		this.result.append("debug");
		visitChild(node, DebugDeclaration.VERSION_PROPERTY, "(", ")");
		this.result.append(" {\n");
		visitList(node, DebugDeclaration.THEN_DECLARATIONS_PROPERTY, LINE_END, EMPTY, LINE_END);
		this.result.append("}");
		visitList(node, DebugDeclaration.ELSE_DECLARATIONS_PROPERTY, LINE_END, " else {\n", "\n}");
		visitChild(node, DebugDeclaration.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(DebugStatement node) {
		this.result.append("debug");
		visitChild(node, DebugStatement.VERSION_PROPERTY, "(", ")");
		this.result.append(" ");
		visitChild(node, DebugStatement.THEN_BODY_PROPERTY);
		visitChild(node, DebugStatement.ELSE_BODY_PROPERTY, " else ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(DeclarationStatement node) {
		visitChild(node, DeclarationStatement.DECLARATION_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(DefaultStatement node) {
		this.result.append("default: ");
		visitChild(node, DefaultStatement.BODY_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(DelegateType node) {
		visitChild(node, DelegateType.RETURN_TYPE_PROPERTY);		this.result.append(" ");
		
		boolean isFunctionPointer = getBooleanAttribute(node, DelegateType.FUNCTION_POINTER_PROPERTY);
		if (isFunctionPointer) {
			this.result.append("function");
		} else {
			this.result.append("delegate");
		}
		this.result.append("(");
		visitList(node, DelegateType.ARGUMENTS_PROPERTY, ", ");
		
		boolean isVariadic = getBooleanAttribute(node, DelegateType.VARIADIC_PROPERTY);
		if (isVariadic) {
			this.result.append("...");
		}
		this.result.append(")");
		return false;
	}
	
	@Override
	public boolean visit(DeleteExpression node) {
		this.result.append("delete ");
		visitChild(node, DeleteExpression.EXPRESSION_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(DollarLiteral node) {
		this.result.append("$");
		return false;
	}
	
	@Override
	public boolean visit(DoStatement node) {
		this.result.append("do ");
		visitChild(node, DoStatement.BODY_PROPERTY);
		this.result.append(" while(");
		visitChild(node, DoStatement.EXPRESSION_PROPERTY);
		this.result.append(");");
		return false;
	}
	
	@Override
	public boolean visit(DotIdentifierExpression node) {
		visitChild(node, DotIdentifierExpression.EXPRESSION_PROPERTY);
		this.result.append(".");
		visitChild(node, DotIdentifierExpression.NAME_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(DotTemplateTypeExpression node) {
		visitChild(node, DotTemplateTypeExpression.EXPRESSION_PROPERTY);
		this.result.append(".");
		visitChild(node, DotTemplateTypeExpression.TEMPLATE_TYPE_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(DynamicArrayType node) {
		visitChild(node, DynamicArrayType.COMPONENT_TYPE_PROPERTY);
		this.result.append("[]");
		return false;
	}
	
	@Override
	public boolean visit(EnumDeclaration node) {
		visitList(node, EnumDeclaration.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, EnumDeclaration.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		this.result.append("enum");
		visitChild(node, EnumDeclaration.NAME_PROPERTY, " ", EMPTY);
		visitChild(node, EnumDeclaration.BASE_TYPE_PROPERTY, " : ", EMPTY);
		this.result.append(" {\n");
		visitList(node, EnumDeclaration.ENUM_MEMBERS_PROPERTY, ",\n", EMPTY, LINE_END);
		this.result.append("}");
		visitChild(node, EnumDeclaration.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(EnumMember node) {
		visitChild(node, EnumMember.NAME_PROPERTY);
		visitChild(node, EnumMember.VALUE_PROPERTY, " = ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(ExpressionInitializer node) {
		visitChild(node, ExpressionInitializer.EXPRESSION_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(ExpressionStatement node) {
		visitChild(node, ExpressionStatement.EXPRESSION_PROPERTY);
		this.result.append(";");
		return false;
	}
	
	@Override
	public boolean visit(ExternDeclaration node) {
		visitList(node, ExternDeclaration.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, ExternDeclaration.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		this.result.append("extern");
		
		ExternDeclaration.Linkage linkage = (Linkage) getAttribute(node, ExternDeclaration.LINKAGE_PROPERTY);
		switch(linkage) {
		case C: this.result.append("(C)"); break;
		case CPP: this.result.append("(C++)"); break;
		case D: this.result.append("(D)"); break;
		case DEFAULT: break;
		case PASCAL: this.result.append("(Pascal)"); break;
		case WINDOWS: this.result.append("(Windows)"); break;
		}
		this.result.append(" {\n");
		visitList(node, ExternDeclaration.DECLARATIONS_PROPERTY, LINE_END, EMPTY, LINE_END);
		this.result.append("}");
		visitChild(node, ExternDeclaration.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(ForeachStatement node) {
		this.result.append("foreach");
		
		boolean isReverse = getBooleanAttribute(node, ForeachStatement.REVERSE_PROPERTY);
		if (isReverse) {
			this.result.append("_reverse");
		}
		this.result.append("(");
		visitList(node, ForeachStatement.ARGUMENTS_PROPERTY, ", ");
		this.result.append("; ");
		visitChild(node, ForeachStatement.EXPRESSION_PROPERTY);
		this.result.append(") ");
		visitChild(node, ForeachStatement.BODY_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(ForStatement node) {
		this.result.append("for(");
		
		ASTNode init = getChildNode(node, ForStatement.INITIALIZER_PROPERTY);
		if (init == null) {
			this.result.append("; ");
		} else {
			visitChild(node, ForStatement.INITIALIZER_PROPERTY);
		}		
		visitChild(node, ForStatement.CONDITION_PROPERTY);
		this.result.append("; ");
		visitChild(node, ForStatement.INCREMENT_PROPERTY);
		this.result.append(") ");
		visitChild(node, ForStatement.BODY_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(FunctionDeclaration node) {
		visitList(node, FunctionDeclaration.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, FunctionDeclaration.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		visitChild(node, FunctionDeclaration.RETURN_TYPE_PROPERTY);
		this.result.append(" ");
		visitChild(node, FunctionDeclaration.NAME_PROPERTY);
		visitList(node, FunctionDeclaration.TEMPLATE_PARAMETERS_PROPERTY, ", ", "(", ")");
		this.result.append("(");
		visitList(node, FunctionDeclaration.ARGUMENTS_PROPERTY, ", ");
		
		boolean isVariadic = getBooleanAttribute(node, FunctionDeclaration.VARIADIC_PROPERTY);
		if (isVariadic) {
			this.result.append("...");
		}
		this.result.append(")");
		
		ASTNode precondition = getChildNode(node, FunctionDeclaration.PRECONDITION_PROPERTY);
		if (precondition != null) {
			this.result.append(LINE_END);
			this.result.append("in ");
			precondition.accept(this);
			this.result.append(LINE_END);
		}
		
		ASTNode postcondition = getChildNode(node, FunctionDeclaration.POSTCONDITION_PROPERTY);
		if (node.getPostcondition() != null) {
			this.result.append(LINE_END);
			this.result.append("out ");
			postcondition.accept(this);
			this.result.append(LINE_END);
		}
		if (precondition != null || postcondition != null) {
			this.result.append("body");
		}
		this.result.append(" ");
		visitChild(node, FunctionDeclaration.BODY_PROPERTY);
		visitChild(node, FunctionDeclaration.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(FunctionLiteralDeclarationExpression node) {
		FunctionLiteralDeclarationExpression.Syntax syntax = (Syntax) getAttribute(node, FunctionLiteralDeclarationExpression.SYNTAX_PROPERTY);
		switch(syntax) {
		case DELEGATE: this.result.append("delegate "); break;
		case EMPTY: break;
		case FUNCTION: this.result.append("function "); break;
		}
		this.result.append("(");
		visitList(node, FunctionLiteralDeclarationExpression.ARGUMENTS_PROPERTY, ", ");
		
		boolean isVariadic = getBooleanAttribute(node, FunctionLiteralDeclarationExpression.VARIADIC_PROPERTY);
		if (isVariadic) {
			this.result.append("...");
		}
		this.result.append(")");
		
		ASTNode precondition = getChildNode(node, FunctionLiteralDeclarationExpression.PRECONDITION_PROPERTY);
		if (precondition != null) {
			this.result.append(LINE_END);
			this.result.append("in ");
			precondition.accept(this);
			this.result.append(LINE_END);
		}
		
		ASTNode postcondition = getChildNode(node, FunctionLiteralDeclarationExpression.POSTCONDITION_PROPERTY);
		if (node.getPostcondition() != null) {
			this.result.append(LINE_END);
			this.result.append("out ");
			postcondition.accept(this);
			this.result.append(LINE_END);
		}
		if (precondition != null || postcondition != null) {
			this.result.append("body");
		}
		this.result.append(" ");		
		visitChild(node, FunctionDeclaration.BODY_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(GotoCaseStatement node) {
		this.result.append("goto case ");
		visitChild(node, GotoCaseStatement.LABEL_PROPERTY);
		this.result.append(";");
		return false;
	}
	
	@Override
	public boolean visit(GotoDefaultStatement node) {
		this.result.append("goto default;");
		return false;
	}
	
	@Override
	public boolean visit(GotoStatement node) {
		this.result.append("goto ");
		visitChild(node, GotoStatement.LABEL_PROPERTY);
		this.result.append(";");
		return false;
	}
	
	@Override
	public boolean visit(IfStatement node) {
		this.result.append("if(");
		visitChild(node, IfStatement.ARGUMENT_PROPERTY, EMPTY, " = ");
		visitChild(node, IfStatement.EXPRESSION_PROPERTY);
		this.result.append(") ");
		visitChild(node, IfStatement.THEN_BODY_PROPERTY);
		visitChild(node, IfStatement.ELSE_BODY_PROPERTY, " else ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(IftypeDeclaration node) {
		visitList(node, IftypeDeclaration.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, IftypeDeclaration.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		this.result.append("iftype(");
		visitChild(node, IftypeDeclaration.TEST_TYPE_PROPERTY);
		visitChild(node, IftypeDeclaration.NAME_PROPERTY, " ", EMPTY);
		
		ASTNode matchingType = getChildNode(node, IftypeDeclaration.MATCHING_TYPE_PROPERTY);
		if (matchingType != null) {
			IftypeDeclaration.Kind kind = (IftypeDeclaration.Kind) getAttribute(node, IftypeDeclaration.KIND_PROPERTY);
			switch(kind) {
			case EQUALS: this.result.append(" = "); break;
			case EXTENDS: this.result.append(" : "); break;
			case NONE: break;
			}
			matchingType.accept(this);
		}
		this.result.append(") {\n");
		visitList(node, IftypeDeclaration.THEN_DECLARATIONS_PROPERTY, LINE_END, EMPTY, LINE_END);
		this.result.append("}");
		visitList(node, IftypeDeclaration.ELSE_DECLARATIONS_PROPERTY, LINE_END, " else {\n", "\n}");
		visitChild(node, IftypeDeclaration.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(IftypeStatement node) {
		this.result.append("iftype(");
		visitChild(node, IftypeStatement.TEST_TYPE_PROPERTY);
		visitChild(node, IftypeStatement.NAME_PROPERTY, " ", EMPTY);
		
		ASTNode matchingType = getChildNode(node, IftypeStatement.MATCHING_TYPE_PROPERTY);
		if (matchingType != null) {
			IftypeDeclaration.Kind kind = (IftypeDeclaration.Kind) getAttribute(node, IftypeDeclaration.KIND_PROPERTY);
			switch(kind) {
			case EQUALS: this.result.append(" = "); break;
			case EXTENDS: this.result.append(" : "); break;
			case NONE: break;
			}
			matchingType.accept(this);
		}
		this.result.append(") ");
		visitChild(node, IftypeStatement.THEN_BODY_PROPERTY);
		visitChild(node, IftypeStatement.ELSE_BODY_PROPERTY, " else ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(Import node) {
		visitChild(node, Import.ALIAS_PROPERTY, EMPTY, " = ");
		visitChild(node, Import.NAME_PROPERTY);
		visitList(node, Import.SELECTIVE_IMPORTS_PROPERTY, ", ", " : ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(ImportDeclaration node) {
		visitList(node, ImportDeclaration.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, ImportDeclaration.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		boolean isStatic = getBooleanAttribute(node, ImportDeclaration.STATIC_PROPERTY);
		if (isStatic) {
			this.result.append("static ");
		}
		this.result.append("import ");
		visitList(node, ImportDeclaration.IMPORTS_PROPERTY, ", ");
		this.result.append(";");
		visitChild(node, ImportDeclaration.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(InfixExpression node) {
		visitChild(node, InfixExpression.LEFT_OPERAND_PROPERTY);
		this.result.append(" ");
		this.result.append(getAttribute(node, InfixExpression.OPERATOR_PROPERTY));
		this.result.append(" ");
		visitChild(node, InfixExpression.RIGHT_OPERAND_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(InvariantDeclaration node) {
		visitList(node, InvariantDeclaration.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, InvariantDeclaration.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		this.result.append("invariant ");
		visitChild(node, InvariantDeclaration.BODY_PROPERTY);
		visitChild(node, InvariantDeclaration.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(IsTypeExpression node) {
		this.result.append("is(");
		visitChild(node, IsTypeExpression.TYPE_PROPERTY);
		visitChild(node, IsTypeExpression.NAME_PROPERTY, " ", EMPTY);
		
		ASTNode specialization = getChildNode(node, IsTypeExpression.SPECIALIZATION_PROPERTY);
		if (specialization != null) {
			boolean isSameComparison = getBooleanAttribute(node, IsTypeExpression.SAME_COMPARISON_PROPERTY);
			if (isSameComparison) {
				this.result.append(" == ");
			} else {
				this.result.append(" : ");
			}
			specialization.accept(this);
		}
		this.result.append(")");
		return false;
	}
	
	@Override
	public boolean visit(IsTypeSpecializationExpression node) {
		this.result.append("is(");
		visitChild(node, IsTypeSpecializationExpression.TYPE_PROPERTY);
		visitChild(node, IsTypeSpecializationExpression.NAME_PROPERTY, " ", EMPTY);
		
		TypeSpecialization specialization = (TypeSpecialization) getAttribute(node, IsTypeSpecializationExpression.SPECIALIZATION_PROPERTY);
		if (specialization != null) {
			boolean isSameComparison = getBooleanAttribute(node, IsTypeSpecializationExpression.SAME_COMPARISON_PROPERTY);
			if (isSameComparison) {
				this.result.append(" == ");
			} else {
				this.result.append(" : ");
			}
			this.result.append(specialization.toString().toLowerCase());
		}
		this.result.append(")");
		return false;
	}
	
	@Override
	public boolean visit(LabeledStatement node) {
		visitChild(node, LabeledStatement.LABEL_PROPERTY);
		this.result.append(": ");
		visitChild(node, LabeledStatement.BODY_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(TemplateMixinDeclaration node) {
		visitList(node, TemplateMixinDeclaration.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, TemplateMixinDeclaration.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		this.result.append("mixin ");
		visitChild(node, TemplateMixinDeclaration.TYPE_PROPERTY);
		visitChild(node, TemplateMixinDeclaration.NAME_PROPERTY, " ", EMPTY);
		this.result.append(";");
		visitChild(node, TemplateMixinDeclaration.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(Modifier node) {
		this.result.append(getAttribute(node, Modifier.MODIFIER_KEYWORD_PROPERTY));
		return false;
	}
	
	@Override
	public boolean visit(ModifierDeclaration node) {
		visitList(node, ModifierDeclaration.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, ModifierDeclaration.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		visitChild(node, ModifierDeclaration.MODIFIER_PROPERTY);
		this.result.append(" {\n");
		visitList(node, ModifierDeclaration.DECLARATIONS_PROPERTY, LINE_END, EMPTY, LINE_END);
		this.result.append("}");
		visitChild(node, ModifierDeclaration.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(ModuleDeclaration node) {
		visitList(node, ModuleDeclaration.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		this.result.append("module ");
		visitChild(node, ModuleDeclaration.NAME_PROPERTY);
		this.result.append(";");
		visitChild(node, ModuleDeclaration.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(NewAnonymousClassExpression node) {
		visitChild(node, NewAnonymousClassExpression.EXPRESSION_PROPERTY, EMPTY, ".");
		this.result.append("new ");
		visitList(node, NewAnonymousClassExpression.NEW_ARGUMENTS_PROPERTY, ", ", "(", ") ");
		this.result.append("class ");
		visitList(node, NewAnonymousClassExpression.CONSTRUCTOR_ARGUMENTS_PROPERTY, ", ", "(", ") ");
		visitList(node, NewAnonymousClassExpression.BASE_CLASSES_PROPERTY, ", ");
		this.result.append(" {\n");
		visitList(node, NewAnonymousClassExpression.DECLARATIONS_PROPERTY, LINE_END, EMPTY, LINE_END);
		this.result.append("}");
		return false;
	}
	
	@Override
	public boolean visit(NewExpression node) {
		visitChild(node, NewExpression.EXPRESSION_PROPERTY, EMPTY, ".");
		this.result.append("new ");
		visitList(node, NewExpression.NEW_ARGUMENTS_PROPERTY, ", ", "(", ") ");
		node.getType().accept(this);
		visitList(node, NewExpression.CONSTRUCTOR_ARGUMENTS_PROPERTY, ", ", "(", ")");
		return false;
	}
	
	@Override
	public boolean visit(NullLiteral node) {
		this.result.append("null");
		return false;
	}
	
	@Override
	public boolean visit(NumberLiteral node) {
		this.result.append(getAttribute(node, NumberLiteral.TOKEN_PROPERTY));
		return false;
	}
	
	@Override
	public boolean visit(ParenthesizedExpression node) {
		this.result.append("(");
		visitChild(node, ParenthesizedExpression.EXPRESSION_PROPERTY);
		this.result.append(")");
		return false;
	}
	
	@Override
	public boolean visit(PointerType node) {
		visitChild(node, PointerType.COMPONENT_TYPE_PROPERTY);
		this.result.append("*");
		return false;
	}
	
	@Override
	public boolean visit(PostfixExpression node) {
		visitChild(node, PostfixExpression.OPERAND_PROPERTY);
		this.result.append(getAttribute(node, PostfixExpression.OPERATOR_PROPERTY));
		return false;
	}
	
	@Override
	public boolean visit(Pragma node) {
		return false;
	}
	
	@Override
	public boolean visit(PragmaDeclaration node) {
		visitList(node, PragmaDeclaration.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, PragmaDeclaration.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		this.result.append("pragma(");
		visitChild(node, PragmaDeclaration.NAME_PROPERTY);
		visitList(node, PragmaDeclaration.ARGUMENTS_PROPERTY, ", ", ", ", EMPTY);
		this.result.append(")");
		visitList(node, PragmaDeclaration.DECLARATIONS_PROPERTY, LINE_END, " {\n", "\n}");
		visitChild(node, PragmaDeclaration.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(PragmaStatement node) {
		this.result.append("pragma(");
		visitChild(node, PragmaStatement.NAME_PROPERTY);
		visitList(node, PragmaStatement.ARGUMENTS_PROPERTY, ", ", ", ", EMPTY);
		this.result.append(")");
		
		ASTNode body = getChildNode(node, PragmaStatement.BODY_PROPERTY);
		if (body != null) {
			this.result.append(" ");
			body.accept(this);
		} else {
			this.result.append(";");
		}
		return false;
	}
	
	@Override
	public boolean visit(PrefixExpression node) {
		this.result.append(getAttribute(node, PrefixExpression.OPERATOR_PROPERTY));
		visitChild(node, PrefixExpression.OPERAND_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(PrimitiveType node) {
		this.result.append(getAttribute(node, PrimitiveType.PRIMITIVE_TYPE_CODE_PROPERTY));
		return false;
	}
	
	@Override
	public boolean visit(QualifiedName node) {
		visitChild(node, QualifiedName.QUALIFIER_PROPERTY);
		this.result.append('.');
		visitChild(node, QualifiedName.NAME_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(QualifiedType node) {
		visitChild(node, QualifiedType.QUALIFIER_PROPERTY);
		this.result.append(".");
		visitChild(node, QualifiedType.TYPE_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(ReturnStatement node) {
		this.result.append("return");
		visitChild(node, ReturnStatement.EXPRESSION_PROPERTY, " ", EMPTY);
		this.result.append(";");
		return false;
	}
	
	@Override
	public boolean visit(ScopeStatement node) {
		this.result.append("scope(");
		this.result.append(getAttribute(node, ScopeStatement.EVENT_PROPERTY).toString().toLowerCase());
		this.result.append(") ");
		node.getBody().accept(this);
		return false;
	}
	
	@Override
	public boolean visit(SelectiveImport node) {
		visitChild(node, SelectiveImport.ALIAS_PROPERTY, EMPTY, " = ");
		visitChild(node, SelectiveImport.NAME_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(SimpleName node) {
		this.result.append(getAttribute(node, SimpleName.IDENTIFIER_PROPERTY));
		return false;
	}
	
	@Override
	public boolean visit(SimpleType node) {
		visitChild(node, SimpleType.NAME_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(SliceExpression node) {
		visitChild(node, SliceExpression.EXPRESSION_PROPERTY);
		this.result.append("[");
		ASTNode fromExpression = getChildNode(node, SliceExpression.FROM_EXPRESSION_PROPERTY);
		ASTNode toExpression = getChildNode(node, SliceExpression.TO_EXPRESSION_PROPERTY);
		if (fromExpression != null && toExpression != null) {
			fromExpression.accept(this);
			this.result.append(" .. ");
			toExpression.accept(this);
		}
		this.result.append("]");
		return false;
	}
	
	@Override
	public boolean visit(SliceType node) {
		visitChild(node, SliceType.COMPONENT_TYPE_PROPERTY);
		this.result.append("[");
		ASTNode fromExpression = getChildNode(node, SliceType.FROM_EXPRESSION_PROPERTY);
		ASTNode toExpression = getChildNode(node, SliceType.TO_EXPRESSION_PROPERTY);
		if (fromExpression != null && toExpression != null) {
			fromExpression.accept(this);
			this.result.append(" .. ");
			toExpression.accept(this);
		}
		this.result.append("]");
		return false;
	}
	
	@Override
	public boolean visit(StaticArrayType node) {
		visitChild(node, StaticArrayType.COMPONENT_TYPE_PROPERTY);
		this.result.append("[");
		visitChild(node, StaticArrayType.SIZE_PROPERTY);
		this.result.append("]");
		return false;
	}
	
	@Override
	public boolean visit(StaticAssert node) {
		visitList(node, StaticAssert.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, StaticAssert.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		this.result.append("static assert(");
		visitChild(node, StaticAssert.EXPRESSION_PROPERTY);
		visitChild(node, StaticAssert.MESSAGE_PROPERTY, ", ", EMPTY);
		this.result.append(")");
		visitChild(node, StaticAssert.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(StaticAssertStatement node) {
		visitChild(node, StaticAssertStatement.STATIC_ASSERT_PROPERTY);
		this.result.append(";");
		return false;
	}
	
	@Override
	public boolean visit(StaticIfDeclaration node) {
		visitList(node, StaticIfDeclaration.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, StaticIfDeclaration.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		this.result.append("static if(");
		visitChild(node, StaticIfDeclaration.EXPRESSION_PROPERTY);
		this.result.append(") {\n");
		visitList(node, StaticIfDeclaration.THEN_DECLARATIONS_PROPERTY, LINE_END, EMPTY, LINE_END);
		this.result.append("}");
		if (!node.elseDeclarations().isEmpty()) {
			this.result.append(" else {\n");
			visitList(node, StaticIfDeclaration.ELSE_DECLARATIONS_PROPERTY, LINE_END, " else {\n", "\n}");
			this.result.append("}");
		}
		visitChild(node, StaticIfDeclaration.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(StaticIfStatement node) {
		this.result.append("static if(");
		visitChild(node, StaticIfStatement.EXPRESSION_PROPERTY);
		this.result.append(") ");
		visitChild(node, StaticIfStatement.THEN_BODY_PROPERTY);
		visitChild(node, StaticIfStatement.ELSE_BODY_PROPERTY, " else ", EMPTY);
		return false;
	}

	@Override
	public boolean visit(StringLiteral node) {
		this.result.append(getAttribute(node, StringLiteral.ESCAPED_VALUE_PROPERTY));
		return false;
	}
	
	@Override
	public boolean visit(StringsExpression node) {
		visitList(node, StringsExpression.STRING_LITERALS_PROPERTY, " ");
		return false;
	}
	
	@Override
	public boolean visit(StructInitializer node) {
		this.result.append("{ ");
		visitList(node, StructInitializer.FRAGMENTS_PROPERTY, ", ");
		this.result.append("}");
		return false;
	}
	
	@Override
	public boolean visit(StructInitializerFragment node) {
		visitChild(node, StructInitializerFragment.NAME_PROPERTY, EMPTY, ": ");
		visitChild(node, StructInitializerFragment.INITIALIZER_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(SuperLiteral node) {
		this.result.append("super");
		return false;
	}
	
	@Override
	public boolean visit(SwitchStatement node) {
		this.result.append("switch(");
		visitChild(node, SwitchStatement.EXPRESSION_PROPERTY);
		this.result.append(") ");
		visitChild(node, SwitchStatement.BODY_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(SynchronizedStatement node) {
		this.result.append("synchronized");
		visitChild(node, SynchronizedStatement.EXPRESSION_PROPERTY, "(", ")");
		this.result.append(" ");
		visitChild(node, SynchronizedStatement.BODY_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(TemplateDeclaration node) {
		visitList(node, TemplateDeclaration.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, TemplateDeclaration.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		this.result.append("template ");
		node.getName().accept(this);
		visitList(node, TemplateDeclaration.TEMPLATE_PARAMETERS_PROPERTY, ", ", "(", ")");
		this.result.append(" {\n");
		visitList(node, TemplateDeclaration.DECLARATIONS_PROPERTY, LINE_END, EMPTY, LINE_END);
		this.result.append("}");
		visitChild(node, TemplateDeclaration.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(TemplateType node) {
		visitChild(node, TemplateType.NAME_PROPERTY);
		this.result.append("!");
		visitList(node, TemplateType.ARGUMENTS_PROPERTY, ", ", "(", ")");
		return false;
	}
	
	@Override
	public boolean visit(ThisLiteral node) {
		this.result.append("this");
		return false;
	}
	
	@Override
	public boolean visit(ThrowStatement node) {
		this.result.append("throw ");
		visitChild(node, ThrowStatement.EXPRESSION_PROPERTY);
		this.result.append(";");
		return false;
	}
	
	@Override
	public boolean visit(TryStatement node) {
		this.result.append("try ");
		visitChild(node, TryStatement.BODY_PROPERTY);
		this.result.append(LINE_END);
		visitList(node, TryStatement.CATCH_CLAUSES_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitChild(node, TryStatement.FINALLY_PROPERTY, " finally ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(TupleTemplateParameter node) {
		visitChild(node, TupleTemplateParameter.NAME_PROPERTY);
		this.result.append(" ...");
		return false;
	}
	
	@Override
	public boolean visit(TypedefDeclaration node) {
		visitList(node, TypedefDeclaration.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, TypedefDeclaration.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		this.result.append("typedef ");
		visitChild(node, TypedefDeclaration.TYPE_PROPERTY);
		this.result.append(" ");
		visitList(node, TypedefDeclaration.FRAGMENTS_PROPERTY, ", ");
		this.result.append(";");
		visitChild(node, TypedefDeclaration.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(TypedefDeclarationFragment node) {
		visitChild(node, TypedefDeclarationFragment.NAME_PROPERTY);
		visitChild(node, TypedefDeclarationFragment.INITIALIZER_PROPERTY, " = ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(TypeDotIdentifierExpression node) {
		visitChild(node, TypeDotIdentifierExpression.TYPE_PROPERTY);
		this.result.append(".");
		visitChild(node, TypeDotIdentifierExpression.NAME_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(TypeExpression node) {
		visitChild(node, TypeExpression.TYPE_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(TypeidExpression node) {
		this.result.append("typeid(");
		visitChild(node, TypeidExpression.TYPE_PROPERTY);
		this.result.append(")");
		return false;
	}
	
	@Override
	public boolean visit(TypeofType node) {
		this.result.append("typeof(");
		visitChild(node, TypeofType.EXPRESSION_PROPERTY);
		this.result.append(")");
		return false;
	}
	
	@Override
	public boolean visit(TypeTemplateParameter node) {
		visitChild(node, TypeTemplateParameter.NAME_PROPERTY);
		visitChild(node, TypeTemplateParameter.SPECIFIC_TYPE_PROPERTY, " : ", EMPTY);
		visitChild(node, TypeTemplateParameter.DEFAULT_TYPE_PROPERTY, " = ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(UnitTestDeclaration node) {
		visitList(node, UnitTestDeclaration.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, UnitTestDeclaration.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		this.result.append("unittest ");
		visitChild(node, UnitTestDeclaration.BODY_PROPERTY);
		visitChild(node, UnitTestDeclaration.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(ValueTemplateParameter node) {
		visitChild(node, ValueTemplateParameter.NAME_PROPERTY);
		visitChild(node, ValueTemplateParameter.SPECIFIC_VALUE_PROPERTY, " : ", EMPTY);
		visitChild(node, ValueTemplateParameter.DEFAULT_VALUE_PROPERTY, " = ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(VariableDeclaration node) {
		visitList(node, VariableDeclaration.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, VariableDeclaration.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		visitChild(node, VariableDeclaration.TYPE_PROPERTY, EMPTY, " ");
		visitList(node, VariableDeclaration.FRAGMENTS_PROPERTY, ", ");
		this.result.append(";");
		visitChild(node, VariableDeclaration.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(VariableDeclarationFragment node) {
		visitChild(node, VariableDeclarationFragment.NAME_PROPERTY);
		visitChild(node, VariableDeclarationFragment.INITIALIZER_PROPERTY, " = ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(Version node) {
		this.result.append(getAttribute(node, Version.VALUE_PROPERTY));
		return false;
	}
	
	@Override
	public boolean visit(VersionAssignment node) {
		visitList(node, VersionAssignment.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, VersionAssignment.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		this.result.append("version = ");
		visitChild(node, VersionAssignment.VERSION_PROPERTY);
		this.result.append(";");
		visitChild(node, VersionAssignment.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(VersionDeclaration node) {
		visitList(node, VersionDeclaration.PRE_D_DOCS_PROPERTY, LINE_END, EMPTY, LINE_END);
		visitList(node, VersionDeclaration.MODIFIERS_PROPERTY, " ", EMPTY, " ");
		this.result.append("version");
		visitChild(node, VersionDeclaration.VERSION_PROPERTY, "(", ")");
		this.result.append(" {\n");
		visitList(node, VersionDeclaration.THEN_DECLARATIONS_PROPERTY, LINE_END, EMPTY, LINE_END);
		this.result.append("}");
		visitList(node, VersionDeclaration.ELSE_DECLARATIONS_PROPERTY, LINE_END, " else {\n", "\n}");
		visitChild(node, VersionDeclaration.POST_D_DOC_PROPERTY, " ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(VersionStatement node) {
		this.result.append("version");
		visitChild(node, VersionStatement.VERSION_PROPERTY, "(", ")");
		this.result.append(" ");
		visitChild(node, VersionStatement.THEN_BODY_PROPERTY);
		visitChild(node, VersionStatement.ELSE_BODY_PROPERTY, " else ", EMPTY);
		return false;
	}
	
	@Override
	public boolean visit(VoidInitializer node) {
		this.result.append("void");
		return false;
	}
	
	@Override
	public boolean visit(VolatileStatement node) {
		this.result.append("volatile ");
		visitChild(node, VolatileStatement.BODY_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(WhileStatement node) {
		this.result.append("while(");
		visitChild(node, WhileStatement.EXPRESSION_PROPERTY);
		this.result.append(") ");
		visitChild(node, WhileStatement.BODY_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(WithStatement node) {
		this.result.append("with(");
		visitChild(node, WithStatement.EXPRESSION_PROPERTY);
		this.result.append(") ");
		visitChild(node, WithStatement.BODY_PROPERTY);
		return false;
	}
	
	@Override
	public boolean visit(EmptyStatement node) {
		this.result.append(";");
		return false;
	}
	
}
