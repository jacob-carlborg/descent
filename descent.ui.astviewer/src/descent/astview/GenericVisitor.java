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
package descent.astview;

import descent.core.dom.*;

public class GenericVisitor extends ASTVisitor {
	
	public GenericVisitor() {
		super();
	}

	/**
	 * @param visitJavadocTags <code>true</code> if doc comment tags are
	 * to be visited by default, and <code>false</code> otherwise
	 * @see Javadoc#tags()
	 * @see #visit(Javadoc)
	 * @since 3.0
	 */
	public GenericVisitor(boolean visitJavadocTags) {
		super(visitJavadocTags);
	}
	
	//---- Hooks for subclasses -------------------------------------------------

	protected boolean visitNode(ASTNode node) {
		return true;
	}
	
	protected void endVisitNode(ASTNode node) {
		// do nothing
	}

	@Override
	public void endVisit(AggregateDeclaration node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(AliasDeclaration node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(AliasDeclarationFragment node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(AliasTemplateParameter node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(AlignDeclaration node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(Argument node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(ArrayAccess node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(ArrayInitializer node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(ArrayInitializerFragment node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(ArrayLiteral node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(AsmStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(AssertExpression node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(Assignment node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(AssociativeArrayType node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(BaseClass node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(Block node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(BooleanLiteral node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(BreakStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(CallExpression node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(CaseStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(CastExpression node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(CatchClause node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(CharacterLiteral node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(Comment node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(CompilationUnit node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(ConditionalExpression node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(ContinueStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(DebugAssignment node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(DebugDeclaration node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(DebugStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(DeclarationStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(DefaultStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(DelegateType node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(DeleteExpression node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(DollarLiteral node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(DoStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(DotIdentifierExpression node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(DotTemplateTypeExpression node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(DynamicArrayType node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(EnumDeclaration node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(EnumMember node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(ExpressionInitializer node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(ExpressionStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(ExternDeclaration node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(ForeachStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(ForStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(FunctionDeclaration node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(FunctionLiteralDeclarationExpression node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(GotoCaseStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(GotoDefaultStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(GotoStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(IfStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(IftypeDeclaration node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(IftypeStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(Import node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(ImportDeclaration node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(InfixExpression node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(InvariantDeclaration node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(IsTypeExpression node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(IsTypeSpecializationExpression node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(LabelStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(MixinDeclaration node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(Modifier node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(ModifierDeclaration node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(ModuleDeclaration node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(NewAnonymousClassExpression node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(NewExpression node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(NullLiteral node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(NumberLiteral node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(ParenthesizedExpression node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(PointerType node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(PostfixExpression node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(PragmaDeclaration node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(PragmaStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(PrefixExpression node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(PrimitiveType node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(QualifiedName node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(QualifiedType node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(ReturnStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(ScopeStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(SelectiveImport node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(SimpleName node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(SimpleType node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(SliceExpression node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(SliceType node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(StaticArrayType node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(StaticAssert node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(StaticAssertStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(StaticIfDeclaration node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(StaticIfStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(StringLiteral node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(StringsExpression node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(StructInitializer node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(StructInitializerFragment node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(SuperLiteral node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(SwitchStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(SynchronizedStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(TemplateDeclaration node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(TemplateType node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(ThisLiteral node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(ThrowStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(TryStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(TupleTemplateParameter node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(TypedefDeclaration node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(TypedefDeclarationFragment node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(TypeDotIdentifierExpression node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(TypeExpression node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(TypeidExpression node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(TypeofType node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(TypeTemplateParameter node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(UnitTestDeclaration node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(ValueTemplateParameter node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(VariableDeclaration node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(VariableDeclarationFragment node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(Version node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(VersionAssignment node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(VersionDeclaration node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(VersionStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(VoidInitializer node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(VolatileStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(WhileStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public void endVisit(WithStatement node) {
		
		endVisitNode(node);
	}

	@Override
	public boolean visit(AggregateDeclaration node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(AliasDeclaration node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(AliasDeclarationFragment node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(AliasTemplateParameter node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(AlignDeclaration node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(Argument node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(ArrayAccess node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(ArrayInitializer node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(ArrayInitializerFragment node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(ArrayLiteral node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(AsmStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(AssertExpression node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(Assignment node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(AssociativeArrayType node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(BaseClass node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(Block node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(BooleanLiteral node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(BreakStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(CallExpression node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(CaseStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(CastExpression node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(CatchClause node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(CharacterLiteral node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(Comment node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(CompilationUnit node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(ConditionalExpression node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(ContinueStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(DebugAssignment node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(DebugDeclaration node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(DebugStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(DeclarationStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(DefaultStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(DelegateType node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(DeleteExpression node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(DollarLiteral node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(DoStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(DotIdentifierExpression node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(DotTemplateTypeExpression node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(DynamicArrayType node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(EnumDeclaration node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(EnumMember node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(ExpressionInitializer node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(ExpressionStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(ExternDeclaration node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(ForeachStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(ForStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(FunctionDeclaration node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(FunctionLiteralDeclarationExpression node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(GotoCaseStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(GotoDefaultStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(GotoStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(IfStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(IftypeDeclaration node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(IftypeStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(Import node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(ImportDeclaration node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(InfixExpression node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(InvariantDeclaration node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(IsTypeExpression node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(IsTypeSpecializationExpression node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(LabelStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(MixinDeclaration node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(Modifier node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(ModifierDeclaration node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(ModuleDeclaration node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(NewAnonymousClassExpression node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(NewExpression node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(NullLiteral node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(NumberLiteral node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(ParenthesizedExpression node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(PointerType node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(PostfixExpression node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(PragmaDeclaration node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(PragmaStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(PrefixExpression node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(PrimitiveType node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(QualifiedName node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(QualifiedType node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(ReturnStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(ScopeStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(SelectiveImport node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(SimpleName node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(SimpleType node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(SliceExpression node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(SliceType node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(StaticArrayType node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(StaticAssert node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(StaticAssertStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(StaticIfDeclaration node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(StaticIfStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(StringLiteral node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(StringsExpression node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(StructInitializer node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(StructInitializerFragment node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(SuperLiteral node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(SwitchStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(SynchronizedStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(TemplateDeclaration node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(TemplateType node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(ThisLiteral node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(ThrowStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(TryStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(TupleTemplateParameter node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(TypedefDeclaration node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(TypedefDeclarationFragment node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(TypeDotIdentifierExpression node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(TypeExpression node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(TypeidExpression node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(TypeofType node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(TypeTemplateParameter node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(UnitTestDeclaration node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(ValueTemplateParameter node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(VariableDeclaration node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(Version node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(VersionAssignment node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(VersionDeclaration node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(VersionStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(VoidInitializer node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(VolatileStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(WhileStatement node) {
		
		return visitNode(node);
	}

	@Override
	public boolean visit(WithStatement node) {
		
		return visitNode(node);
	}

}
