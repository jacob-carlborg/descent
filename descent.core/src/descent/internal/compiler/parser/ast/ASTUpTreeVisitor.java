package descent.internal.compiler.parser.ast;

import melnorme.miscutil.Assert;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.*;
import descent.internal.compiler.parser.Package;

/**
 * An abstract visitor class that that delegates each visit method, to the visit
 * method of the element's superclass 
 */
public class ASTUpTreeVisitor extends TreeVisitor implements IASTVisitor {

	
	public void preVisit(ASTNode elem) {
		// Default implementation: do nothing
	}

	public void postVisit(ASTNode elem) {
		// Default implementation: do nothing
	}
	
	public boolean visit(IASTNode node) {
		// Default implementation: do nothing
		return true;
	}
	
	public void endVisit(IASTNode node) {
		// Default implementation: do nothing
		return;
	}
	
	/* ====================================================== */


	

	public boolean visit(ASTNode node) {
		//return true;
		return visit((IASTNode) node);
	}
	
	public void endVisit(ASTNode node) {
		// Default implementation: do nothing
		//Assert.isTrue(ASTNode.class.getSuperclass().equals(ASTRangeLessNode.class));
		//endVisit((ASTRangeLessNode) node);
	}

	
	public boolean visit(ASTDmdNode elem) {
		Assert.isTrue(ASTDmdNode.class.getSuperclass().equals(ASTNode.class));
		return visit((ASTNode) elem);
	}
	
	public boolean visit(ASTRangeLessNode elem) {
		Assert.isTrue(ASTRangeLessNode.class.getSuperclass().equals(ASTDmdNode.class));
		return visit((ASTDmdNode) elem);
	}

/*	public boolean visit(AbstractElement elem) {
		//ensureVisitIsNotDirectVisit(elem);
		Assert.isTrue(AbstractElement.class.getSuperclass().equals(ASTNode.class));
		return visit((ASTNode) elem);
	}
*/

	public boolean visit(AddAssignExp node) {
		Assert.isTrue(AddAssignExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(AddExp node) {
		Assert.isTrue(AddExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(AddrExp node) {
		Assert.isTrue(AddrExp.class.getSuperclass().equals(UnaExp.class));
		return visit((UnaExp) node);
	}
	public boolean visit(AggregateDeclaration node) {
		Assert.isTrue(AggregateDeclaration.class.getSuperclass().equals(ScopeDsymbol.class));
		return visit((ScopeDsymbol) node);
	}
	public boolean visit(AliasDeclaration node) {
		Assert.isTrue(AliasDeclaration.class.getSuperclass().equals(Declaration.class));
		return visit((Declaration) node);
	}
	public boolean visit(AliasThis node) {
		Assert.isTrue(AliasThis.class.getSuperclass().equals(Dsymbol.class));
		return visit((Dsymbol) node);
	}
	public boolean visit(AlignDeclaration node) {
		Assert.isTrue(AlignDeclaration.class.getSuperclass().equals(AttribDeclaration.class));
		return visit((AttribDeclaration) node);
	}
	public boolean visit(AndAndExp node) {
		Assert.isTrue(AndAndExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(AndAssignExp node) {
		Assert.isTrue(AndAssignExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(AndExp node) {
		Assert.isTrue(AndExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(AnonDeclaration node) {
		Assert.isTrue(AnonDeclaration.class.getSuperclass().equals(AttribDeclaration.class));
		return visit((AttribDeclaration) node);
	}
	public boolean visit(AnonymousAggregateDeclaration node) {
		Assert.isTrue(AnonymousAggregateDeclaration.class.getSuperclass().equals(AggregateDeclaration.class));
		return visit((AggregateDeclaration) node);
	}
	public boolean visit(Argument node) {
		Assert.isTrue(Argument.class.getSuperclass().equals(ASTDmdNode.class));
		return visit((ASTDmdNode) node);
	}
	public boolean visit(ArrayExp node) {
		Assert.isTrue(ArrayExp.class.getSuperclass().equals(UnaExp.class));
		return visit((UnaExp) node);
	}
	public boolean visit(ArrayInitializer node) {
		Assert.isTrue(ArrayInitializer.class.getSuperclass().equals(Initializer.class));
		return visit((Initializer) node);
	}
	public boolean visit(ArrayLengthExp node) {
		Assert.isTrue(ArrayLengthExp.class.getSuperclass().equals(UnaExp.class));
		return visit((UnaExp) node);
	}
	public boolean visit(ArrayLiteralExp node) {
		Assert.isTrue(ArrayLiteralExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(ArrayScopeSymbol node) {
		Assert.isTrue(ArrayScopeSymbol.class.getSuperclass().equals(ScopeDsymbol.class));
		return visit((ScopeDsymbol) node);
	}
	public boolean visit(AsmBlock node) {
		Assert.isTrue(AsmBlock.class.getSuperclass().equals(CompoundStatement.class));
		return visit((CompoundStatement) node);
	}
	public boolean visit(AsmStatement node) {
		Assert.isTrue(AsmStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(AssertExp node) {
		Assert.isTrue(AssertExp.class.getSuperclass().equals(UnaExp.class));
		return visit((UnaExp) node);
	}
	public boolean visit(AssignExp node) {
		Assert.isTrue(AssignExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(AssocArrayLiteralExp node) {
		Assert.isTrue(AssocArrayLiteralExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}

	public boolean visit(AttribDeclaration node) {
		Assert.isTrue(AttribDeclaration.class.getSuperclass().equals(Dsymbol.class));
		return visit((Dsymbol) node);
	}
	public boolean visit(BaseClass node) {
		Assert.isTrue(BaseClass.class.getSuperclass().equals(ASTDmdNode.class));
		return visit((ASTDmdNode) node);
	}
	public boolean visit(BinExp node) {
		Assert.isTrue(BinExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(BoolExp node) {
		Assert.isTrue(BoolExp.class.getSuperclass().equals(UnaExp.class));
		return visit((UnaExp) node);
	}
	public boolean visit(BreakStatement node) {
		Assert.isTrue(BreakStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(CallExp node) {
		Assert.isTrue(CallExp.class.getSuperclass().equals(UnaExp.class));
		return visit((UnaExp) node);
	}
	public boolean visit(CaseStatement node) {
		Assert.isTrue(CaseStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(CaseRangeStatement node) {
		Assert.isTrue(CaseRangeStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(CastExp node) {
		Assert.isTrue(CastExp.class.getSuperclass().equals(UnaExp.class));
		return visit((UnaExp) node);
	}
	public boolean visit(CatAssignExp node) {
		Assert.isTrue(CatAssignExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(Catch node) {
		Assert.isTrue(Catch.class.getSuperclass().equals(ASTDmdNode.class));
		return visit((ASTDmdNode) node);
	}
	public boolean visit(CatExp node) {
		Assert.isTrue(CatExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(ClassDeclaration node) {
		Assert.isTrue(ClassDeclaration.class.getSuperclass().equals(AggregateDeclaration.class));
		return visit((AggregateDeclaration) node);
	}
	public boolean visit(ClassInfoDeclaration node) {
		Assert.isTrue(ClassInfoDeclaration.class.getSuperclass().equals(VarDeclaration.class));
		return visit((VarDeclaration) node);
	}
	public boolean visit(CmpExp node) {
		Assert.isTrue(CmpExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(ComExp node) {
		Assert.isTrue(ComExp.class.getSuperclass().equals(UnaExp.class));
		return visit((UnaExp) node);
	}
	public boolean visit(CommaExp node) {
		Assert.isTrue(CommaExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(CompileDeclaration node) {
		Assert.isTrue(CompileDeclaration.class.getSuperclass().equals(AttribDeclaration.class));
		return visit((AttribDeclaration) node);
	}
	public boolean visit(CompileExp node) {
		Assert.isTrue(CompileExp.class.getSuperclass().equals(UnaExp.class));
		return visit((UnaExp) node);
	}
	public boolean visit(CompileStatement node) {
		Assert.isTrue(CompileStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(ComplexExp node) {
		Assert.isTrue(ComplexExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(CompoundStatement node) {
		Assert.isTrue(CompoundStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(CondExp node) {
		Assert.isTrue(CondExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(Condition node) {
		Assert.isTrue(Condition.class.getSuperclass().equals(ASTRangeLessNode.class));
		return visit((ASTRangeLessNode) node);
	}
	public boolean visit(ConditionalDeclaration node) {
		Assert.isTrue(ConditionalDeclaration.class.getSuperclass().equals(AttribDeclaration.class));
		return visit((AttribDeclaration) node);
	}
	public boolean visit(ConditionalStatement node) {
		Assert.isTrue(ConditionalStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(ContinueStatement node) {
		Assert.isTrue(ContinueStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(CtorDeclaration node) {
		Assert.isTrue(CtorDeclaration.class.getSuperclass().equals(FuncDeclaration.class));
		return visit((FuncDeclaration) node);
	}
	public boolean visit(DebugCondition node) {
		Assert.isTrue(DebugCondition.class.getSuperclass().equals(DVCondition.class));
		return visit((DVCondition) node);
	}
	public boolean visit(DebugSymbol node) {
		Assert.isTrue(DebugSymbol.class.getSuperclass().equals(Dsymbol.class));
		return visit((Dsymbol) node);
	}
	public boolean visit(Declaration node) {
		Assert.isTrue(Declaration.class.getSuperclass().equals(Dsymbol.class));
		return visit((Dsymbol) node);
	}
	public boolean visit(DeclarationExp node) {
		Assert.isTrue(DeclarationExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(DeclarationStatement node) {
		Assert.isTrue(DeclarationStatement.class.getSuperclass().equals(ExpStatement.class));
		return visit((ExpStatement) node);
	}
	public boolean visit(DefaultStatement node) {
		Assert.isTrue(DefaultStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(DelegateExp node) {
		Assert.isTrue(DelegateExp.class.getSuperclass().equals(UnaExp.class));
		return visit((UnaExp) node);
	}
	public boolean visit(DeleteDeclaration node) {
		Assert.isTrue(DeleteDeclaration.class.getSuperclass().equals(FuncDeclaration.class));
		return visit((FuncDeclaration) node);
	}
	public boolean visit(DeleteExp node) {
		Assert.isTrue(DeleteExp.class.getSuperclass().equals(UnaExp.class));
		return visit((UnaExp) node);
	}
	public boolean visit(DivAssignExp node) {
		Assert.isTrue(DivAssignExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(DivExp node) {
		Assert.isTrue(DivExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(DollarExp node) {
		Assert.isTrue(DollarExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(DoStatement node) {
		Assert.isTrue(DoStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(DotExp node) {
		Assert.isTrue(DotExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(DotIdExp node) {
		Assert.isTrue(DotIdExp.class.getSuperclass().equals(UnaExp.class));
		return visit((UnaExp) node);
	}
	public boolean visit(DotTemplateExp node) {
		Assert.isTrue(DotTemplateExp.class.getSuperclass().equals(UnaExp.class));
		return visit((UnaExp) node);
	}
	public boolean visit(DotTemplateInstanceExp node) {
		Assert.isTrue(DotTemplateInstanceExp.class.getSuperclass().equals(UnaExp.class));
		return visit((UnaExp) node);
	}
	public boolean visit(DotTypeExp node) {
		Assert.isTrue(DotTypeExp.class.getSuperclass().equals(UnaExp.class));
		return visit((UnaExp) node);
	}
	public boolean visit(DotVarExp node) {
		Assert.isTrue(DotVarExp.class.getSuperclass().equals(UnaExp.class));
		return visit((UnaExp) node);
	}
	public boolean visit(Dsymbol node) {
		Assert.isTrue(Dsymbol.class.getSuperclass().equals(ASTDmdNode.class));
		return visit((ASTDmdNode) node);
	}
	public boolean visit(DsymbolExp node) {
		Assert.isTrue(DsymbolExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(DtorDeclaration node) {
		Assert.isTrue(DtorDeclaration.class.getSuperclass().equals(FuncDeclaration.class));
		return visit((FuncDeclaration) node);
	}
	public boolean visit(EnumDeclaration node) {
		Assert.isTrue(EnumDeclaration.class.getSuperclass().equals(ScopeDsymbol.class));
		return visit((ScopeDsymbol) node);
	}
	public boolean visit(EnumMember node) {
		Assert.isTrue(EnumMember.class.getSuperclass().equals(Dsymbol.class));
		return visit((Dsymbol) node);
	}
	public boolean visit(EqualExp node) {
		Assert.isTrue(EqualExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(ExpInitializer node) {
		Assert.isTrue(ExpInitializer.class.getSuperclass().equals(Initializer.class));
		return visit((Initializer) node);
	}
	public boolean visit(Expression node) {
		Assert.isTrue(Expression.class.getSuperclass().equals(ASTDmdNode.class));
		return visit((ASTDmdNode) node);
	}
	public boolean visit(ExpStatement node) {
		Assert.isTrue(ExpStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(FileExp node) {
		Assert.isTrue(FileExp.class.getSuperclass().equals(UnaExp.class));
		return visit((UnaExp) node);
	}
	public boolean visit(ForeachRangeStatement node) {
		Assert.isTrue(ForeachRangeStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(ForeachStatement node) {
		Assert.isTrue(ForeachStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(ForStatement node) {
		Assert.isTrue(ForStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(FuncAliasDeclaration node) {
		Assert.isTrue(FuncAliasDeclaration.class.getSuperclass().equals(FuncDeclaration.class));
		return visit((FuncDeclaration) node);
	}
	public boolean visit(FuncDeclaration node) {
		Assert.isTrue(FuncDeclaration.class.getSuperclass().equals(Declaration.class));
		return visit((Declaration) node);
	}
	public boolean visit(FuncExp node) {
		Assert.isTrue(FuncExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(FuncLiteralDeclaration node) {
		Assert.isTrue(FuncLiteralDeclaration.class.getSuperclass().equals(FuncDeclaration.class));
		return visit((FuncDeclaration) node);
	}
	public boolean visit(GotoCaseStatement node) {
		Assert.isTrue(GotoCaseStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(GotoDefaultStatement node) {
		Assert.isTrue(GotoDefaultStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(GotoStatement node) {
		Assert.isTrue(GotoStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(HaltExp node) {
		Assert.isTrue(HaltExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(IdentifierExp node) {
		Assert.isTrue(IdentifierExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(IdentityExp node) {
		Assert.isTrue(IdentityExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(IfStatement node) {
		Assert.isTrue(IfStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(IftypeCondition node) {
		Assert.isTrue(IftypeCondition.class.getSuperclass().equals(Condition.class));
		return visit((Condition) node);
	}
	public boolean visit(IsExp node) {
		Assert.isTrue(IsExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(Import node) {
		Assert.isTrue(Import.class.getSuperclass().equals(Dsymbol.class));
		return visit((Dsymbol) node);
	}
	public boolean visit(IndexExp node) {
		Assert.isTrue(IndexExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(InExp node) {
		Assert.isTrue(InExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(Initializer node) {
		Assert.isTrue(Initializer.class.getSuperclass().equals(ASTDmdNode.class));
		return visit((ASTDmdNode) node);
	}
	public boolean visit(IntegerExp node) {
		Assert.isTrue(IntegerExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(InterfaceDeclaration node) {
		Assert.isTrue(InterfaceDeclaration.class.getSuperclass().equals(ClassDeclaration.class));
		return visit((ClassDeclaration) node);
	}
	public boolean visit(InvariantDeclaration node) {
		Assert.isTrue(InvariantDeclaration.class.getSuperclass().equals(FuncDeclaration.class));
		return visit((FuncDeclaration) node);
	}
	public boolean visit(LabelDsymbol node) {
		Assert.isTrue(LabelDsymbol.class.getSuperclass().equals(Dsymbol.class));
		return visit((Dsymbol) node);
	}
	public boolean visit(LabelStatement node) {
		Assert.isTrue(LabelStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(LinkDeclaration node) {
		Assert.isTrue(LinkDeclaration.class.getSuperclass().equals(AttribDeclaration.class));
		return visit((AttribDeclaration) node);
	}
	public boolean visit(MinAssignExp node) {
		Assert.isTrue(MinAssignExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(MinExp node) {
		Assert.isTrue(MinExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(ModAssignExp node) {
		Assert.isTrue(ModAssignExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(ModExp node) {
		Assert.isTrue(ModExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(Modifier node) {
		Assert.isTrue(Modifier.class.getSuperclass().equals(ASTDmdNode.class));
		return visit((ASTDmdNode) node);
	}
	public boolean visit(Module node) {
		Assert.isTrue(Module.class.getSuperclass().equals(Package.class));
		return visit((Package) node);
	}
	public boolean visit(ModuleDeclaration node) {
		Assert.isTrue(ModuleDeclaration.class.getSuperclass().equals(ASTDmdNode.class));
		return visit((ASTDmdNode) node);
	}
	public boolean visit(ModuleInfoDeclaration node) {
		Assert.isTrue(ModuleInfoDeclaration.class.getSuperclass().equals(VarDeclaration.class));
		return visit((VarDeclaration) node);
	}
	public boolean visit(MulAssignExp node) {
		Assert.isTrue(MulAssignExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(MulExp node) {
		Assert.isTrue(MulExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(NegExp node) {
		Assert.isTrue(NegExp.class.getSuperclass().equals(UnaExp.class));
		return visit((UnaExp) node);
	}
	public boolean visit(NewAnonClassExp node) {
		Assert.isTrue(NewAnonClassExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(NewDeclaration node) {
		Assert.isTrue(NewDeclaration.class.getSuperclass().equals(FuncDeclaration.class));
		return visit((FuncDeclaration) node);
	}
	public boolean visit(NewExp node) {
		Assert.isTrue(NewExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(NotExp node) {
		Assert.isTrue(NotExp.class.getSuperclass().equals(UnaExp.class));
		return visit((UnaExp) node);
	}
	public boolean visit(NullExp node) {
		Assert.isTrue(NullExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(OnScopeStatement node) {
		Assert.isTrue(OnScopeStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(OrAssignExp node) {
		Assert.isTrue(OrAssignExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(OrExp node) {
		Assert.isTrue(OrExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(OrOrExp node) {
		Assert.isTrue(OrOrExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(Package node) {
		Assert.isTrue(Package.class.getSuperclass().equals(ScopeDsymbol.class));
		return visit((ScopeDsymbol) node);
	}
	public boolean visit(PostExp node) {
		Assert.isTrue(PostExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(PragmaDeclaration node) {
		Assert.isTrue(PragmaDeclaration.class.getSuperclass().equals(AttribDeclaration.class));
		return visit((AttribDeclaration) node);
	}
	public boolean visit(PragmaStatement node) {
		Assert.isTrue(PragmaStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(ProtDeclaration node) {
		Assert.isTrue(ProtDeclaration.class.getSuperclass().equals(AttribDeclaration.class));
		return visit((AttribDeclaration) node);
	}
	public boolean visit(PtrExp node) {
		Assert.isTrue(PtrExp.class.getSuperclass().equals(UnaExp.class));
		return visit((UnaExp) node);
	}
	public boolean visit(RealExp node) {
		Assert.isTrue(RealExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(RemoveExp node) {
		Assert.isTrue(RemoveExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(ReturnStatement node) {
		Assert.isTrue(ReturnStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(ScopeDsymbol node) {
		Assert.isTrue(ScopeDsymbol.class.getSuperclass().equals(Dsymbol.class));
		return visit((Dsymbol) node);
	}
	public boolean visit(ScopeExp node) {
		Assert.isTrue(ScopeExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(ScopeStatement node) {
		Assert.isTrue(ScopeStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(ShlAssignExp node) {
		Assert.isTrue(ShlAssignExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(ShlExp node) {
		Assert.isTrue(ShlExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(ShrAssignExp node) {
		Assert.isTrue(ShrAssignExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(ShrExp node) {
		Assert.isTrue(ShrExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(SliceExp node) {
		Assert.isTrue(SliceExp.class.getSuperclass().equals(UnaExp.class));
		return visit((UnaExp) node);
	}
	public boolean visit(Statement node) {
		Assert.isTrue(Statement.class.getSuperclass().equals(ASTDmdNode.class));
		return visit((ASTDmdNode) node);
	}
	public boolean visit(StaticAssert node) {
		Assert.isTrue(StaticAssert.class.getSuperclass().equals(Dsymbol.class));
		return visit((Dsymbol) node);
	}
	public boolean visit(StaticAssertStatement node) {
		Assert.isTrue(StaticAssertStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(StaticCtorDeclaration node) {
		Assert.isTrue(StaticCtorDeclaration.class.getSuperclass().equals(FuncDeclaration.class));
		return visit((FuncDeclaration) node);
	}
	public boolean visit(StaticDtorDeclaration node) {
		Assert.isTrue(StaticDtorDeclaration.class.getSuperclass().equals(FuncDeclaration.class));
		return visit((FuncDeclaration) node);
	}
	public boolean visit(StaticIfCondition node) {
		Assert.isTrue(StaticIfCondition.class.getSuperclass().equals(Condition.class));
		return visit((Condition) node);
	}
	public boolean visit(StaticIfDeclaration node) {
		Assert.isTrue(StaticIfDeclaration.class.getSuperclass().equals(ConditionalDeclaration.class));
		return visit((ConditionalDeclaration) node);
	}
	public boolean visit(StorageClassDeclaration node) {
		Assert.isTrue(StorageClassDeclaration.class.getSuperclass().equals(AttribDeclaration.class));
		return visit((AttribDeclaration) node);
	}
	public boolean visit(StringExp node) {
		Assert.isTrue(StringExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(StructDeclaration node) {
		Assert.isTrue(StructDeclaration.class.getSuperclass().equals(AggregateDeclaration.class));
		return visit((AggregateDeclaration) node);
	}
	public boolean visit(StructInitializer node) {
		Assert.isTrue(StructInitializer.class.getSuperclass().equals(Initializer.class));
		return visit((Initializer) node);
	}
	public boolean visit(SuperExp node) {
		Assert.isTrue(SuperExp.class.getSuperclass().equals(ThisExp.class));
		return visit((ThisExp) node);
	}
	public boolean visit(SwitchStatement node) {
		Assert.isTrue(SwitchStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(SymOffExp node) {
		Assert.isTrue(SymOffExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(SynchronizedStatement node) {
		Assert.isTrue(SynchronizedStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(TemplateAliasParameter node) {
		Assert.isTrue(TemplateAliasParameter.class.getSuperclass().equals(TemplateParameter.class));
		return visit((TemplateParameter) node);
	}
	public boolean visit(TemplateDeclaration node) {
		Assert.isTrue(TemplateDeclaration.class.getSuperclass().equals(ScopeDsymbol.class));
		return visit((ScopeDsymbol) node);
	}
	public boolean visit(TemplateExp node) {
		Assert.isTrue(TemplateExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(TemplateInstance node) {
		Assert.isTrue(TemplateInstance.class.getSuperclass().equals(ScopeDsymbol.class));
		return visit((ScopeDsymbol) node);
	}
	public boolean visit(TemplateInstanceWrapper node) {
		Assert.isTrue(TemplateInstanceWrapper.class.getSuperclass().equals(IdentifierExp.class));
		return visit((IdentifierExp) node);
	}
	public boolean visit(TemplateMixin node) {
		Assert.isTrue(TemplateMixin.class.getSuperclass().equals(TemplateInstance.class));
		return visit((TemplateInstance) node);
	}
	public boolean visit(TemplateParameter node) {
		Assert.isTrue(TemplateParameter.class.getSuperclass().equals(ASTDmdNode.class));
		return visit((ASTDmdNode) node);
	}
	public boolean visit(TemplateTupleParameter node) {
		Assert.isTrue(TemplateTupleParameter.class.getSuperclass().equals(TemplateParameter.class));
		return visit((TemplateParameter) node);
	}
	public boolean visit(TemplateTypeParameter node) {
		Assert.isTrue(TemplateTypeParameter.class.getSuperclass().equals(TemplateParameter.class));
		return visit((TemplateParameter) node);
	}
	public boolean visit(TemplateValueParameter node) {
		Assert.isTrue(TemplateValueParameter.class.getSuperclass().equals(TemplateParameter.class));
		return visit((TemplateParameter) node);
	}
	public boolean visit(ThisDeclaration node) {
		Assert.isTrue(ThisDeclaration.class.getSuperclass().equals(VarDeclaration.class));
		return visit((VarDeclaration) node);
	}
	public boolean visit(ThisExp node) {
		Assert.isTrue(ThisExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(ThrowStatement node) {
		Assert.isTrue(ThrowStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(TraitsExp node) {
		Assert.isTrue(TraitsExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(TryCatchStatement node) {
		Assert.isTrue(TryCatchStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(TryFinallyStatement node) {
		Assert.isTrue(TryFinallyStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(Tuple node) {
		Assert.isTrue(Tuple.class.getSuperclass().equals(ASTDmdNode.class));
		return visit((ASTDmdNode) node);
	}
	public boolean visit(TupleDeclaration node) {
		Assert.isTrue(TupleDeclaration.class.getSuperclass().equals(Declaration.class));
		return visit((Declaration) node);
	}
	public boolean visit(TupleExp node) {
		Assert.isTrue(TupleExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(Type node) {
		Assert.isTrue(Type.class.getSuperclass().equals(ASTDmdNode.class));
		return visit((ASTDmdNode) node);
	}
	public boolean visit(TypeAArray node) {
		Assert.isTrue(TypeAArray.class.getSuperclass().equals(Type.class));
		return visit((Type) node);
	}
	public boolean visit(TypeBasic node) {
		Assert.isTrue(TypeBasic.class.getSuperclass().equals(Type.class));
		return visit((Type) node);
	}
	public boolean visit(TypeClass node) {
		Assert.isTrue(TypeClass.class.getSuperclass().equals(Type.class));
		return visit((Type) node);
	}
	public boolean visit(TypeDArray node) {
		Assert.isTrue(TypeDArray.class.getSuperclass().equals(Type.class));
		return visit((Type) node);
	}
	public boolean visit(TypedefDeclaration node) {
		Assert.isTrue(TypedefDeclaration.class.getSuperclass().equals(Declaration.class));
		return visit((Declaration) node);
	}
	public boolean visit(TypeDelegate node) {
		Assert.isTrue(TypeDelegate.class.getSuperclass().equals(Type.class));
		return visit((Type) node);
	}
	public boolean visit(TypeEnum node) {
		Assert.isTrue(TypeEnum.class.getSuperclass().equals(Type.class));
		return visit((Type) node);
	}
	public boolean visit(TypeExp node) {
		Assert.isTrue(TypeExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(TypeFunction node) {
		Assert.isTrue(TypeFunction.class.getSuperclass().equals(Type.class));
		return visit((Type) node);
	}
	public boolean visit(TypeIdentifier node) {
		Assert.isTrue(TypeIdentifier.class.getSuperclass().equals(TypeQualified.class));
		return visit((TypeQualified) node);
	}
	public boolean visit(TypeidExp node) {
		Assert.isTrue(TypeidExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(TypeInfoArrayDeclaration node) {
		Assert.isTrue(TypeInfoArrayDeclaration.class.getSuperclass().equals(TypeInfoDeclaration.class));
		return visit((TypeInfoDeclaration) node);
	}
	public boolean visit(TypeInfoAssociativeArrayDeclaration node) {
		Assert.isTrue(TypeInfoAssociativeArrayDeclaration.class.getSuperclass().equals(TypeInfoDeclaration.class));
		return visit((TypeInfoDeclaration) node);
	}
	public boolean visit(TypeInfoClassDeclaration node) {
		Assert.isTrue(TypeInfoClassDeclaration.class.getSuperclass().equals(TypeInfoDeclaration.class));
		return visit((TypeInfoDeclaration) node);
	}
	public boolean visit(TypeInfoDeclaration node) {
		Assert.isTrue(TypeInfoDeclaration.class.getSuperclass().equals(VarDeclaration.class));
		return visit((VarDeclaration) node);
	}
	public boolean visit(TypeInfoDelegateDeclaration node) {
		Assert.isTrue(TypeInfoDelegateDeclaration.class.getSuperclass().equals(TypeInfoDeclaration.class));
		return visit((TypeInfoDeclaration) node);
	}
	public boolean visit(TypeInfoEnumDeclaration node) {
		Assert.isTrue(TypeInfoEnumDeclaration.class.getSuperclass().equals(TypeInfoDeclaration.class));
		return visit((TypeInfoDeclaration) node);
	}
	public boolean visit(TypeInfoFunctionDeclaration node) {
		Assert.isTrue(TypeInfoFunctionDeclaration.class.getSuperclass().equals(TypeInfoDeclaration.class));
		return visit((TypeInfoDeclaration) node);
	}
	public boolean visit(TypeInfoInterfaceDeclaration node) {
		Assert.isTrue(TypeInfoInterfaceDeclaration.class.getSuperclass().equals(TypeInfoDeclaration.class));
		return visit((TypeInfoDeclaration) node);
	}
	public boolean visit(TypeInfoPointerDeclaration node) {
		Assert.isTrue(TypeInfoPointerDeclaration.class.getSuperclass().equals(TypeInfoDeclaration.class));
		return visit((TypeInfoDeclaration) node);
	}
	public boolean visit(TypeInfoStaticArrayDeclaration node) {
		Assert.isTrue(TypeInfoStaticArrayDeclaration.class.getSuperclass().equals(TypeInfoDeclaration.class));
		return visit((TypeInfoDeclaration) node);
	}
	public boolean visit(TypeInfoStructDeclaration node) {
		Assert.isTrue(TypeInfoStructDeclaration.class.getSuperclass().equals(TypeInfoDeclaration.class));
		return visit((TypeInfoDeclaration) node);
	}
	public boolean visit(TypeInfoTypedefDeclaration node) {
		Assert.isTrue(TypeInfoTypedefDeclaration.class.getSuperclass().equals(TypeInfoDeclaration.class));
		return visit((TypeInfoDeclaration) node);
	}
	public boolean visit(TypeInstance node) {
		Assert.isTrue(TypeInstance.class.getSuperclass().equals(TypeQualified.class));
		return visit((TypeQualified) node);
	}
	public boolean visit(TypePointer node) {
		Assert.isTrue(TypePointer.class.getSuperclass().equals(Type.class));
		return visit((Type) node);
	}
	public boolean visit(TypeQualified node) {
		Assert.isTrue(TypeQualified.class.getSuperclass().equals(Type.class));
		return visit((Type) node);
	}
	public boolean visit(TypeSArray node) {
		Assert.isTrue(TypeSArray.class.getSuperclass().equals(Type.class));
		return visit((Type) node);
	}
	public boolean visit(TypeSlice node) {
		Assert.isTrue(TypeSlice.class.getSuperclass().equals(Type.class));
		return visit((Type) node);
	}
	public boolean visit(TypeStruct node) {
		Assert.isTrue(TypeStruct.class.getSuperclass().equals(Type.class));
		return visit((Type) node);
	}
	public boolean visit(TypeTuple node) {
		Assert.isTrue(TypeTuple.class.getSuperclass().equals(Type.class));
		return visit((Type) node);
	}
	public boolean visit(TypeTypedef node) {
		Assert.isTrue(TypeTypedef.class.getSuperclass().equals(Type.class));
		return visit((Type) node);
	}
	public boolean visit(TypeTypeof node) {
		Assert.isTrue(TypeTypeof.class.getSuperclass().equals(TypeQualified.class));
		return visit((TypeQualified) node);
	}
	public boolean visit(UAddExp node) {
		Assert.isTrue(UAddExp.class.getSuperclass().equals(UnaExp.class));
		return visit((UnaExp) node);
	}
	public boolean visit(UnaExp node) {
		Assert.isTrue(UnaExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(UnionDeclaration node) {
		Assert.isTrue(UnionDeclaration.class.getSuperclass().equals(StructDeclaration.class));
		return visit((StructDeclaration) node);
	}
	public boolean visit(UnitTestDeclaration node) {
		Assert.isTrue(UnitTestDeclaration.class.getSuperclass().equals(FuncDeclaration.class));
		return visit((FuncDeclaration) node);
	}
	public boolean visit(UnrolledLoopStatement node) {
		Assert.isTrue(UnrolledLoopStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(UshrAssignExp node) {
		Assert.isTrue(UshrAssignExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(UshrExp node) {
		Assert.isTrue(UshrExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(VarDeclaration node) {
		Assert.isTrue(VarDeclaration.class.getSuperclass().equals(Declaration.class));
		return visit((Declaration) node);
	}
	public boolean visit(VarExp node) {
		Assert.isTrue(VarExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	public boolean visit(Version node) {
		Assert.isTrue(Version.class.getSuperclass().equals(Dsymbol.class));
		return visit((Dsymbol) node);
	}
	public boolean visit(VersionCondition node) {
		Assert.isTrue(VersionCondition.class.getSuperclass().equals(DVCondition.class));
		return visit((DVCondition) node);
	}
	public boolean visit(VersionSymbol node) {
		Assert.isTrue(VersionSymbol.class.getSuperclass().equals(Dsymbol.class));
		return visit((Dsymbol) node);
	}
	public boolean visit(VoidInitializer node) {
		Assert.isTrue(VoidInitializer.class.getSuperclass().equals(Initializer.class));
		return visit((Initializer) node);
	}
	public boolean visit(VolatileStatement node) {
		Assert.isTrue(VolatileStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(WhileStatement node) {
		Assert.isTrue(WhileStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(WithScopeSymbol node) {
		Assert.isTrue(WithScopeSymbol.class.getSuperclass().equals(ScopeDsymbol.class));
		return visit((ScopeDsymbol) node);
	}
	public boolean visit(WithStatement node) {
		Assert.isTrue(WithStatement.class.getSuperclass().equals(Statement.class));
		return visit((Statement) node);
	}
	public boolean visit(XorAssignExp node) {
		Assert.isTrue(XorAssignExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}
	public boolean visit(XorExp node) {
		Assert.isTrue(XorExp.class.getSuperclass().equals(BinExp.class));
		return visit((BinExp) node);
	}

	
	/* ====================================================== */

	public void endVisit(ASTDmdNode elem) {
		Assert.isTrue(ASTDmdNode.class.getSuperclass().equals(ASTNode.class));
		endVisit((ASTNode) elem);
	}
	
	public void endVisit(ASTRangeLessNode elem) {
		Assert.isTrue(ASTRangeLessNode.class.getSuperclass().equals(ASTDmdNode.class));
		endVisit((ASTDmdNode) elem);
	}

/*	public void endVisit(AbstractElement elem) {
		//ensureVisitIsNotDirectVisit(elem);
		Assert.isTrue(AbstractElement.class.getSuperclass().equals(ASTNode.class));
		endVisit((ASTNode) elem);
	}
*/

	public void endVisit(AddAssignExp node) {
		Assert.isTrue(AddAssignExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(AddExp node) {
		Assert.isTrue(AddExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(AddrExp node) {
		Assert.isTrue(AddrExp.class.getSuperclass().equals(UnaExp.class));
		endVisit((UnaExp) node);
	}
	public void endVisit(AggregateDeclaration node) {
		Assert.isTrue(AggregateDeclaration.class.getSuperclass().equals(ScopeDsymbol.class));
		endVisit((ScopeDsymbol) node);
	}
	public void endVisit(AliasDeclaration node) {
		Assert.isTrue(AliasDeclaration.class.getSuperclass().equals(Declaration.class));
		endVisit((Declaration) node);
	}
	public void endVisit(AliasThis node) {
		Assert.isTrue(AliasThis.class.getSuperclass().equals(Dsymbol.class));
		endVisit((Dsymbol) node);
	}
	public void endVisit(AlignDeclaration node) {
		Assert.isTrue(AlignDeclaration.class.getSuperclass().equals(AttribDeclaration.class));
		endVisit((AttribDeclaration) node);
	}
	public void endVisit(AndAndExp node) {
		Assert.isTrue(AndAndExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(AndAssignExp node) {
		Assert.isTrue(AndAssignExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(AndExp node) {
		Assert.isTrue(AndExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(AnonDeclaration node) {
		Assert.isTrue(AnonDeclaration.class.getSuperclass().equals(AttribDeclaration.class));
		endVisit((AttribDeclaration) node);
	}
	public void endVisit(AnonymousAggregateDeclaration node) {
		Assert.isTrue(AnonymousAggregateDeclaration.class.getSuperclass().equals(AggregateDeclaration.class));
		endVisit((AggregateDeclaration) node);
	}
	public void endVisit(Argument node) {
		Assert.isTrue(Argument.class.getSuperclass().equals(ASTDmdNode.class));
		endVisit((ASTDmdNode) node);
	}
	public void endVisit(ArrayExp node) {
		Assert.isTrue(ArrayExp.class.getSuperclass().equals(UnaExp.class));
		endVisit((UnaExp) node);
	}
	public void endVisit(ArrayInitializer node) {
		Assert.isTrue(ArrayInitializer.class.getSuperclass().equals(Initializer.class));
		endVisit((Initializer) node);
	}
	public void endVisit(ArrayLengthExp node) {
		Assert.isTrue(ArrayLengthExp.class.getSuperclass().equals(UnaExp.class));
		endVisit((UnaExp) node);
	}
	public void endVisit(ArrayLiteralExp node) {
		Assert.isTrue(ArrayLiteralExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(ArrayScopeSymbol node) {
		Assert.isTrue(ArrayScopeSymbol.class.getSuperclass().equals(ScopeDsymbol.class));
		endVisit((ScopeDsymbol) node);
	}
	public void endVisit(AsmBlock node) {
		Assert.isTrue(AsmBlock.class.getSuperclass().equals(CompoundStatement.class));
		endVisit((CompoundStatement) node);
	}
	public void endVisit(AsmStatement node) {
		Assert.isTrue(AsmStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(AssertExp node) {
		Assert.isTrue(AssertExp.class.getSuperclass().equals(UnaExp.class));
		endVisit((UnaExp) node);
	}
	public void endVisit(AssignExp node) {
		Assert.isTrue(AssignExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(AssocArrayLiteralExp node) {
		Assert.isTrue(AssocArrayLiteralExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}

	public void endVisit(AttribDeclaration node) {
		Assert.isTrue(AttribDeclaration.class.getSuperclass().equals(Dsymbol.class));
		endVisit((Dsymbol) node);
	}
	public void endVisit(BaseClass node) {
		Assert.isTrue(BaseClass.class.getSuperclass().equals(ASTDmdNode.class));
		endVisit((ASTDmdNode) node);
	}
	public void endVisit(BinExp node) {
		Assert.isTrue(BinExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(BoolExp node) {
		Assert.isTrue(BoolExp.class.getSuperclass().equals(UnaExp.class));
		endVisit((UnaExp) node);
	}
	public void endVisit(BreakStatement node) {
		Assert.isTrue(BreakStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(CallExp node) {
		Assert.isTrue(CallExp.class.getSuperclass().equals(UnaExp.class));
		endVisit((UnaExp) node);
	}
	public void endVisit(CaseStatement node) {
		Assert.isTrue(CaseStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(CaseRangeStatement node) {
		Assert.isTrue(CaseRangeStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(CastExp node) {
		Assert.isTrue(CastExp.class.getSuperclass().equals(UnaExp.class));
		endVisit((UnaExp) node);
	}
	public void endVisit(CatAssignExp node) {
		Assert.isTrue(CatAssignExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(Catch node) {
		Assert.isTrue(Catch.class.getSuperclass().equals(ASTDmdNode.class));
		endVisit((ASTDmdNode) node);
	}
	public void endVisit(CatExp node) {
		Assert.isTrue(CatExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(ClassDeclaration node) {
		Assert.isTrue(ClassDeclaration.class.getSuperclass().equals(AggregateDeclaration.class));
		endVisit((AggregateDeclaration) node);
	}
	public void endVisit(ClassInfoDeclaration node) {
		Assert.isTrue(ClassInfoDeclaration.class.getSuperclass().equals(VarDeclaration.class));
		endVisit((VarDeclaration) node);
	}
	public void endVisit(CmpExp node) {
		Assert.isTrue(CmpExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(ComExp node) {
		Assert.isTrue(ComExp.class.getSuperclass().equals(UnaExp.class));
		endVisit((UnaExp) node);
	}
	public void endVisit(CommaExp node) {
		Assert.isTrue(CommaExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(CompileDeclaration node) {
		Assert.isTrue(CompileDeclaration.class.getSuperclass().equals(AttribDeclaration.class));
		endVisit((AttribDeclaration) node);
	}
	public void endVisit(CompileExp node) {
		Assert.isTrue(CompileExp.class.getSuperclass().equals(UnaExp.class));
		endVisit((UnaExp) node);
	}
	public void endVisit(CompileStatement node) {
		Assert.isTrue(CompileStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(ComplexExp node) {
		Assert.isTrue(ComplexExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(CompoundStatement node) {
		Assert.isTrue(CompoundStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(CondExp node) {
		Assert.isTrue(CondExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(Condition node) {
		Assert.isTrue(Condition.class.getSuperclass().equals(ASTRangeLessNode.class));
		endVisit((ASTRangeLessNode) node);
	}
	public void endVisit(ConditionalDeclaration node) {
		Assert.isTrue(ConditionalDeclaration.class.getSuperclass().equals(AttribDeclaration.class));
		endVisit((AttribDeclaration) node);
	}
	public void endVisit(ConditionalStatement node) {
		Assert.isTrue(ConditionalStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(ContinueStatement node) {
		Assert.isTrue(ContinueStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(CtorDeclaration node) {
		Assert.isTrue(CtorDeclaration.class.getSuperclass().equals(FuncDeclaration.class));
		endVisit((FuncDeclaration) node);
	}
	public void endVisit(DebugCondition node) {
		Assert.isTrue(DebugCondition.class.getSuperclass().equals(DVCondition.class));
		endVisit((DVCondition) node);
	}
	public void endVisit(DebugSymbol node) {
		Assert.isTrue(DebugSymbol.class.getSuperclass().equals(Dsymbol.class));
		endVisit((Dsymbol) node);
	}
	public void endVisit(Declaration node) {
		Assert.isTrue(Declaration.class.getSuperclass().equals(Dsymbol.class));
		endVisit((Dsymbol) node);
	}
	public void endVisit(DeclarationExp node) {
		Assert.isTrue(DeclarationExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(DeclarationStatement node) {
		Assert.isTrue(DeclarationStatement.class.getSuperclass().equals(ExpStatement.class));
		endVisit((ExpStatement) node);
	}
	public void endVisit(DefaultStatement node) {
		Assert.isTrue(DefaultStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(DelegateExp node) {
		Assert.isTrue(DelegateExp.class.getSuperclass().equals(UnaExp.class));
		endVisit((UnaExp) node);
	}
	public void endVisit(DeleteDeclaration node) {
		Assert.isTrue(DeleteDeclaration.class.getSuperclass().equals(FuncDeclaration.class));
		endVisit((FuncDeclaration) node);
	}
	public void endVisit(DeleteExp node) {
		Assert.isTrue(DeleteExp.class.getSuperclass().equals(UnaExp.class));
		endVisit((UnaExp) node);
	}
	public void endVisit(DivAssignExp node) {
		Assert.isTrue(DivAssignExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(DivExp node) {
		Assert.isTrue(DivExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(DollarExp node) {
		Assert.isTrue(DollarExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(DoStatement node) {
		Assert.isTrue(DoStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(DotExp node) {
		Assert.isTrue(DotExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(DotIdExp node) {
		Assert.isTrue(DotIdExp.class.getSuperclass().equals(UnaExp.class));
		endVisit((UnaExp) node);
	}
	public void endVisit(DotTemplateExp node) {
		Assert.isTrue(DotTemplateExp.class.getSuperclass().equals(UnaExp.class));
		endVisit((UnaExp) node);
	}
	public void endVisit(DotTemplateInstanceExp node) {
		Assert.isTrue(DotTemplateInstanceExp.class.getSuperclass().equals(UnaExp.class));
		endVisit((UnaExp) node);
	}
	public void endVisit(DotTypeExp node) {
		Assert.isTrue(DotTypeExp.class.getSuperclass().equals(UnaExp.class));
		endVisit((UnaExp) node);
	}
	public void endVisit(DotVarExp node) {
		Assert.isTrue(DotVarExp.class.getSuperclass().equals(UnaExp.class));
		endVisit((UnaExp) node);
	}
	public void endVisit(Dsymbol node) {
		Assert.isTrue(Dsymbol.class.getSuperclass().equals(ASTDmdNode.class));
		endVisit((ASTDmdNode) node);
	}
	public void endVisit(DsymbolExp node) {
		Assert.isTrue(DsymbolExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(DtorDeclaration node) {
		Assert.isTrue(DtorDeclaration.class.getSuperclass().equals(FuncDeclaration.class));
		endVisit((FuncDeclaration) node);
	}
	public void endVisit(EnumDeclaration node) {
		Assert.isTrue(EnumDeclaration.class.getSuperclass().equals(ScopeDsymbol.class));
		endVisit((ScopeDsymbol) node);
	}
	public void endVisit(EnumMember node) {
		Assert.isTrue(EnumMember.class.getSuperclass().equals(Dsymbol.class));
		endVisit((Dsymbol) node);
	}
	public void endVisit(EqualExp node) {
		Assert.isTrue(EqualExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(ExpInitializer node) {
		Assert.isTrue(ExpInitializer.class.getSuperclass().equals(Initializer.class));
		endVisit((Initializer) node);
	}
	public void endVisit(Expression node) {
		Assert.isTrue(Expression.class.getSuperclass().equals(ASTDmdNode.class));
		endVisit((ASTDmdNode) node);
	}
	public void endVisit(ExpStatement node) {
		Assert.isTrue(ExpStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(FileExp node) {
		Assert.isTrue(FileExp.class.getSuperclass().equals(UnaExp.class));
		endVisit((UnaExp) node);
	}
	public void endVisit(ForeachRangeStatement node) {
		Assert.isTrue(ForeachRangeStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(ForeachStatement node) {
		Assert.isTrue(ForeachStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(ForStatement node) {
		Assert.isTrue(ForStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(FuncAliasDeclaration node) {
		Assert.isTrue(FuncAliasDeclaration.class.getSuperclass().equals(FuncDeclaration.class));
		endVisit((FuncDeclaration) node);
	}
	public void endVisit(FuncDeclaration node) {
		Assert.isTrue(FuncDeclaration.class.getSuperclass().equals(Declaration.class));
		endVisit((Declaration) node);
	}
	public void endVisit(FuncExp node) {
		Assert.isTrue(FuncExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(FuncLiteralDeclaration node) {
		Assert.isTrue(FuncLiteralDeclaration.class.getSuperclass().equals(FuncDeclaration.class));
		endVisit((FuncDeclaration) node);
	}
	public void endVisit(GotoCaseStatement node) {
		Assert.isTrue(GotoCaseStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(GotoDefaultStatement node) {
		Assert.isTrue(GotoDefaultStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(GotoStatement node) {
		Assert.isTrue(GotoStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(HaltExp node) {
		Assert.isTrue(HaltExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(IdentifierExp node) {
		Assert.isTrue(IdentifierExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(IdentityExp node) {
		Assert.isTrue(IdentityExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(IfStatement node) {
		Assert.isTrue(IfStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(IftypeCondition node) {
		Assert.isTrue(IftypeCondition.class.getSuperclass().equals(Condition.class));
		endVisit((Condition) node);
	}
	public void endVisit(IsExp node) {
		Assert.isTrue(IsExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(Import node) {
		Assert.isTrue(Import.class.getSuperclass().equals(Dsymbol.class));
		endVisit((Dsymbol) node);
	}
	public void endVisit(IndexExp node) {
		Assert.isTrue(IndexExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(InExp node) {
		Assert.isTrue(InExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(Initializer node) {
		Assert.isTrue(Initializer.class.getSuperclass().equals(ASTDmdNode.class));
		endVisit((ASTDmdNode) node);
	}
	public void endVisit(IntegerExp node) {
		Assert.isTrue(IntegerExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(InterfaceDeclaration node) {
		Assert.isTrue(InterfaceDeclaration.class.getSuperclass().equals(ClassDeclaration.class));
		endVisit((ClassDeclaration) node);
	}
	public void endVisit(InvariantDeclaration node) {
		Assert.isTrue(InvariantDeclaration.class.getSuperclass().equals(FuncDeclaration.class));
		endVisit((FuncDeclaration) node);
	}
	public void endVisit(LabelDsymbol node) {
		Assert.isTrue(LabelDsymbol.class.getSuperclass().equals(Dsymbol.class));
		endVisit((Dsymbol) node);
	}
	public void endVisit(LabelStatement node) {
		Assert.isTrue(LabelStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(LinkDeclaration node) {
		Assert.isTrue(LinkDeclaration.class.getSuperclass().equals(AttribDeclaration.class));
		endVisit((AttribDeclaration) node);
	}
	public void endVisit(MinAssignExp node) {
		Assert.isTrue(MinAssignExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(MinExp node) {
		Assert.isTrue(MinExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(ModAssignExp node) {
		Assert.isTrue(ModAssignExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(ModExp node) {
		Assert.isTrue(ModExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(Modifier node) {
		Assert.isTrue(Modifier.class.getSuperclass().equals(ASTDmdNode.class));
		endVisit((ASTDmdNode) node);
	}
	public void endVisit(Module node) {
		Assert.isTrue(Module.class.getSuperclass().equals(Package.class));
		endVisit((Package) node);
	}
	public void endVisit(ModuleDeclaration node) {
		Assert.isTrue(ModuleDeclaration.class.getSuperclass().equals(ASTDmdNode.class));
		endVisit((ASTDmdNode) node);
	}
	public void endVisit(ModuleInfoDeclaration node) {
		Assert.isTrue(ModuleInfoDeclaration.class.getSuperclass().equals(VarDeclaration.class));
		endVisit((VarDeclaration) node);
	}
	public void endVisit(MulAssignExp node) {
		Assert.isTrue(MulAssignExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(MulExp node) {
		Assert.isTrue(MulExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(NegExp node) {
		Assert.isTrue(NegExp.class.getSuperclass().equals(UnaExp.class));
		endVisit((UnaExp) node);
	}
	public void endVisit(NewAnonClassExp node) {
		Assert.isTrue(NewAnonClassExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(NewDeclaration node) {
		Assert.isTrue(NewDeclaration.class.getSuperclass().equals(FuncDeclaration.class));
		endVisit((FuncDeclaration) node);
	}
	public void endVisit(NewExp node) {
		Assert.isTrue(NewExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(NotExp node) {
		Assert.isTrue(NotExp.class.getSuperclass().equals(UnaExp.class));
		endVisit((UnaExp) node);
	}
	public void endVisit(NullExp node) {
		Assert.isTrue(NullExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(OnScopeStatement node) {
		Assert.isTrue(OnScopeStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(OrAssignExp node) {
		Assert.isTrue(OrAssignExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(OrExp node) {
		Assert.isTrue(OrExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(OrOrExp node) {
		Assert.isTrue(OrOrExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(Package node) {
		Assert.isTrue(Package.class.getSuperclass().equals(ScopeDsymbol.class));
		endVisit((ScopeDsymbol) node);
	}
	public void endVisit(PostExp node) {
		Assert.isTrue(PostExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(PragmaDeclaration node) {
		Assert.isTrue(PragmaDeclaration.class.getSuperclass().equals(AttribDeclaration.class));
		endVisit((AttribDeclaration) node);
	}
	public void endVisit(PragmaStatement node) {
		Assert.isTrue(PragmaStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(ProtDeclaration node) {
		Assert.isTrue(ProtDeclaration.class.getSuperclass().equals(AttribDeclaration.class));
		endVisit((AttribDeclaration) node);
	}
	public void endVisit(PtrExp node) {
		Assert.isTrue(PtrExp.class.getSuperclass().equals(UnaExp.class));
		endVisit((UnaExp) node);
	}
	public void endVisit(RealExp node) {
		Assert.isTrue(RealExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(RemoveExp node) {
		Assert.isTrue(RemoveExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(ReturnStatement node) {
		Assert.isTrue(ReturnStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(ScopeDsymbol node) {
		Assert.isTrue(ScopeDsymbol.class.getSuperclass().equals(Dsymbol.class));
		endVisit((Dsymbol) node);
	}
	public void endVisit(ScopeExp node) {
		Assert.isTrue(ScopeExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(ScopeStatement node) {
		Assert.isTrue(ScopeStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(ShlAssignExp node) {
		Assert.isTrue(ShlAssignExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(ShlExp node) {
		Assert.isTrue(ShlExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(ShrAssignExp node) {
		Assert.isTrue(ShrAssignExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(ShrExp node) {
		Assert.isTrue(ShrExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(SliceExp node) {
		Assert.isTrue(SliceExp.class.getSuperclass().equals(UnaExp.class));
		endVisit((UnaExp) node);
	}
	public void endVisit(Statement node) {
		Assert.isTrue(Statement.class.getSuperclass().equals(ASTDmdNode.class));
		endVisit((ASTDmdNode) node);
	}
	public void endVisit(StaticAssert node) {
		Assert.isTrue(StaticAssert.class.getSuperclass().equals(Dsymbol.class));
		endVisit((Dsymbol) node);
	}
	public void endVisit(StaticAssertStatement node) {
		Assert.isTrue(StaticAssertStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(StaticCtorDeclaration node) {
		Assert.isTrue(StaticCtorDeclaration.class.getSuperclass().equals(FuncDeclaration.class));
		endVisit((FuncDeclaration) node);
	}
	public void endVisit(StaticDtorDeclaration node) {
		Assert.isTrue(StaticDtorDeclaration.class.getSuperclass().equals(FuncDeclaration.class));
		endVisit((FuncDeclaration) node);
	}
	public void endVisit(StaticIfCondition node) {
		Assert.isTrue(StaticIfCondition.class.getSuperclass().equals(Condition.class));
		endVisit((Condition) node);
	}
	public void endVisit(StaticIfDeclaration node) {
		Assert.isTrue(StaticIfDeclaration.class.getSuperclass().equals(ConditionalDeclaration.class));
		endVisit((ConditionalDeclaration) node);
	}
	public void endVisit(StorageClassDeclaration node) {
		Assert.isTrue(StorageClassDeclaration.class.getSuperclass().equals(AttribDeclaration.class));
		endVisit((AttribDeclaration) node);
	}
	public void endVisit(StringExp node) {
		Assert.isTrue(StringExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(StructDeclaration node) {
		Assert.isTrue(StructDeclaration.class.getSuperclass().equals(AggregateDeclaration.class));
		endVisit((AggregateDeclaration) node);
	}
	public void endVisit(StructInitializer node) {
		Assert.isTrue(StructInitializer.class.getSuperclass().equals(Initializer.class));
		endVisit((Initializer) node);
	}
	public void endVisit(SuperExp node) {
		Assert.isTrue(SuperExp.class.getSuperclass().equals(ThisExp.class));
		endVisit((ThisExp) node);
	}
	public void endVisit(SwitchStatement node) {
		Assert.isTrue(SwitchStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(SymOffExp node) {
		Assert.isTrue(SymOffExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(SynchronizedStatement node) {
		Assert.isTrue(SynchronizedStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(TemplateAliasParameter node) {
		Assert.isTrue(TemplateAliasParameter.class.getSuperclass().equals(TemplateParameter.class));
		endVisit((TemplateParameter) node);
	}
	public void endVisit(TemplateDeclaration node) {
		Assert.isTrue(TemplateDeclaration.class.getSuperclass().equals(ScopeDsymbol.class));
		endVisit((ScopeDsymbol) node);
	}
	public void endVisit(TemplateExp node) {
		Assert.isTrue(TemplateExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(TemplateInstance node) {
		Assert.isTrue(TemplateInstance.class.getSuperclass().equals(ScopeDsymbol.class));
		endVisit((ScopeDsymbol) node);
	}
	public void endVisit(TemplateInstanceWrapper node) {
		Assert.isTrue(TemplateInstanceWrapper.class.getSuperclass().equals(IdentifierExp.class));
		endVisit((IdentifierExp) node);
	}
	public void endVisit(TemplateMixin node) {
		Assert.isTrue(TemplateMixin.class.getSuperclass().equals(TemplateInstance.class));
		endVisit((TemplateInstance) node);
	}
	public void endVisit(TemplateParameter node) {
		Assert.isTrue(TemplateParameter.class.getSuperclass().equals(ASTDmdNode.class));
		endVisit((ASTDmdNode) node);
	}
	public void endVisit(TemplateTupleParameter node) {
		Assert.isTrue(TemplateTupleParameter.class.getSuperclass().equals(TemplateParameter.class));
		endVisit((TemplateParameter) node);
	}
	public void endVisit(TemplateTypeParameter node) {
		Assert.isTrue(TemplateTypeParameter.class.getSuperclass().equals(TemplateParameter.class));
		endVisit((TemplateParameter) node);
	}
	public void endVisit(TemplateValueParameter node) {
		Assert.isTrue(TemplateValueParameter.class.getSuperclass().equals(TemplateParameter.class));
		endVisit((TemplateParameter) node);
	}
	public void endVisit(ThisDeclaration node) {
		Assert.isTrue(ThisDeclaration.class.getSuperclass().equals(VarDeclaration.class));
		endVisit((VarDeclaration) node);
	}
	public void endVisit(ThisExp node) {
		Assert.isTrue(ThisExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(ThrowStatement node) {
		Assert.isTrue(ThrowStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(TraitsExp node) {
		Assert.isTrue(TraitsExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(TryCatchStatement node) {
		Assert.isTrue(TryCatchStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(TryFinallyStatement node) {
		Assert.isTrue(TryFinallyStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(Tuple node) {
		Assert.isTrue(Tuple.class.getSuperclass().equals(ASTDmdNode.class));
		endVisit((ASTDmdNode) node);
	}
	public void endVisit(TupleDeclaration node) {
		Assert.isTrue(TupleDeclaration.class.getSuperclass().equals(Declaration.class));
		endVisit((Declaration) node);
	}
	public void endVisit(TupleExp node) {
		Assert.isTrue(TupleExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(Type node) {
		Assert.isTrue(Type.class.getSuperclass().equals(ASTDmdNode.class));
		endVisit((ASTDmdNode) node);
	}
	public void endVisit(TypeAArray node) {
		Assert.isTrue(TypeAArray.class.getSuperclass().equals(Type.class));
		endVisit((Type) node);
	}
	public void endVisit(TypeBasic node) {
		Assert.isTrue(TypeBasic.class.getSuperclass().equals(Type.class));
		endVisit((Type) node);
	}
	public void endVisit(TypeClass node) {
		Assert.isTrue(TypeClass.class.getSuperclass().equals(Type.class));
		endVisit((Type) node);
	}
	public void endVisit(TypeDArray node) {
		Assert.isTrue(TypeDArray.class.getSuperclass().equals(Type.class));
		endVisit((Type) node);
	}
	public void endVisit(TypedefDeclaration node) {
		Assert.isTrue(TypedefDeclaration.class.getSuperclass().equals(Declaration.class));
		endVisit((Declaration) node);
	}
	public void endVisit(TypeDelegate node) {
		Assert.isTrue(TypeDelegate.class.getSuperclass().equals(Type.class));
		endVisit((Type) node);
	}
	public void endVisit(TypeEnum node) {
		Assert.isTrue(TypeEnum.class.getSuperclass().equals(Type.class));
		endVisit((Type) node);
	}
	public void endVisit(TypeExp node) {
		Assert.isTrue(TypeExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(TypeFunction node) {
		Assert.isTrue(TypeFunction.class.getSuperclass().equals(Type.class));
		endVisit((Type) node);
	}
	public void endVisit(TypeIdentifier node) {
		Assert.isTrue(TypeIdentifier.class.getSuperclass().equals(TypeQualified.class));
		endVisit((TypeQualified) node);
	}
	public void endVisit(TypeidExp node) {
		Assert.isTrue(TypeidExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(TypeInfoArrayDeclaration node) {
		Assert.isTrue(TypeInfoArrayDeclaration.class.getSuperclass().equals(TypeInfoDeclaration.class));
		endVisit((TypeInfoDeclaration) node);
	}
	public void endVisit(TypeInfoAssociativeArrayDeclaration node) {
		Assert.isTrue(TypeInfoAssociativeArrayDeclaration.class.getSuperclass().equals(TypeInfoDeclaration.class));
		endVisit((TypeInfoDeclaration) node);
	}
	public void endVisit(TypeInfoClassDeclaration node) {
		Assert.isTrue(TypeInfoClassDeclaration.class.getSuperclass().equals(TypeInfoDeclaration.class));
		endVisit((TypeInfoDeclaration) node);
	}
	public void endVisit(TypeInfoDeclaration node) {
		Assert.isTrue(TypeInfoDeclaration.class.getSuperclass().equals(VarDeclaration.class));
		endVisit((VarDeclaration) node);
	}
	public void endVisit(TypeInfoDelegateDeclaration node) {
		Assert.isTrue(TypeInfoDelegateDeclaration.class.getSuperclass().equals(TypeInfoDeclaration.class));
		endVisit((TypeInfoDeclaration) node);
	}
	public void endVisit(TypeInfoEnumDeclaration node) {
		Assert.isTrue(TypeInfoEnumDeclaration.class.getSuperclass().equals(TypeInfoDeclaration.class));
		endVisit((TypeInfoDeclaration) node);
	}
	public void endVisit(TypeInfoFunctionDeclaration node) {
		Assert.isTrue(TypeInfoFunctionDeclaration.class.getSuperclass().equals(TypeInfoDeclaration.class));
		endVisit((TypeInfoDeclaration) node);
	}
	public void endVisit(TypeInfoInterfaceDeclaration node) {
		Assert.isTrue(TypeInfoInterfaceDeclaration.class.getSuperclass().equals(TypeInfoDeclaration.class));
		endVisit((TypeInfoDeclaration) node);
	}
	public void endVisit(TypeInfoPointerDeclaration node) {
		Assert.isTrue(TypeInfoPointerDeclaration.class.getSuperclass().equals(TypeInfoDeclaration.class));
		endVisit((TypeInfoDeclaration) node);
	}
	public void endVisit(TypeInfoStaticArrayDeclaration node) {
		Assert.isTrue(TypeInfoStaticArrayDeclaration.class.getSuperclass().equals(TypeInfoDeclaration.class));
		endVisit((TypeInfoDeclaration) node);
	}
	public void endVisit(TypeInfoStructDeclaration node) {
		Assert.isTrue(TypeInfoStructDeclaration.class.getSuperclass().equals(TypeInfoDeclaration.class));
		endVisit((TypeInfoDeclaration) node);
	}
	public void endVisit(TypeInfoTypedefDeclaration node) {
		Assert.isTrue(TypeInfoTypedefDeclaration.class.getSuperclass().equals(TypeInfoDeclaration.class));
		endVisit((TypeInfoDeclaration) node);
	}
	public void endVisit(TypeInstance node) {
		Assert.isTrue(TypeInstance.class.getSuperclass().equals(TypeQualified.class));
		endVisit((TypeQualified) node);
	}
	public void endVisit(TypePointer node) {
		Assert.isTrue(TypePointer.class.getSuperclass().equals(Type.class));
		endVisit((Type) node);
	}
	public void endVisit(TypeQualified node) {
		Assert.isTrue(TypeQualified.class.getSuperclass().equals(Type.class));
		endVisit((Type) node);
	}
	public void endVisit(TypeSArray node) {
		Assert.isTrue(TypeSArray.class.getSuperclass().equals(Type.class));
		endVisit((Type) node);
	}
	public void endVisit(TypeSlice node) {
		Assert.isTrue(TypeSlice.class.getSuperclass().equals(Type.class));
		endVisit((Type) node);
	}
	public void endVisit(TypeStruct node) {
		Assert.isTrue(TypeStruct.class.getSuperclass().equals(Type.class));
		endVisit((Type) node);
	}
	public void endVisit(TypeTuple node) {
		Assert.isTrue(TypeTuple.class.getSuperclass().equals(Type.class));
		endVisit((Type) node);
	}
	public void endVisit(TypeTypedef node) {
		Assert.isTrue(TypeTypedef.class.getSuperclass().equals(Type.class));
		endVisit((Type) node);
	}
	public void endVisit(TypeTypeof node) {
		Assert.isTrue(TypeTypeof.class.getSuperclass().equals(TypeQualified.class));
		endVisit((TypeQualified) node);
	}
	public void endVisit(UAddExp node) {
		Assert.isTrue(UAddExp.class.getSuperclass().equals(UnaExp.class));
		endVisit((UnaExp) node);
	}
	public void endVisit(UnaExp node) {
		Assert.isTrue(UnaExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(UnionDeclaration node) {
		Assert.isTrue(UnionDeclaration.class.getSuperclass().equals(StructDeclaration.class));
		endVisit((StructDeclaration) node);
	}
	public void endVisit(UnitTestDeclaration node) {
		Assert.isTrue(UnitTestDeclaration.class.getSuperclass().equals(FuncDeclaration.class));
		endVisit((FuncDeclaration) node);
	}
	public void endVisit(UnrolledLoopStatement node) {
		Assert.isTrue(UnrolledLoopStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(UshrAssignExp node) {
		Assert.isTrue(UshrAssignExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(UshrExp node) {
		Assert.isTrue(UshrExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(VarDeclaration node) {
		Assert.isTrue(VarDeclaration.class.getSuperclass().equals(Declaration.class));
		endVisit((Declaration) node);
	}
	public void endVisit(VarExp node) {
		Assert.isTrue(VarExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}
	public void endVisit(Version node) {
		Assert.isTrue(Version.class.getSuperclass().equals(Dsymbol.class));
		endVisit((Dsymbol) node);
	}
	public void endVisit(VersionCondition node) {
		Assert.isTrue(VersionCondition.class.getSuperclass().equals(DVCondition.class));
		endVisit((DVCondition) node);
	}
	public void endVisit(VersionSymbol node) {
		Assert.isTrue(VersionSymbol.class.getSuperclass().equals(Dsymbol.class));
		endVisit((Dsymbol) node);
	}
	public void endVisit(VoidInitializer node) {
		Assert.isTrue(VoidInitializer.class.getSuperclass().equals(Initializer.class));
		endVisit((Initializer) node);
	}
	public void endVisit(VolatileStatement node) {
		Assert.isTrue(VolatileStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(WhileStatement node) {
		Assert.isTrue(WhileStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(WithScopeSymbol node) {
		Assert.isTrue(WithScopeSymbol.class.getSuperclass().equals(ScopeDsymbol.class));
		endVisit((ScopeDsymbol) node);
	}
	public void endVisit(WithStatement node) {
		Assert.isTrue(WithStatement.class.getSuperclass().equals(Statement.class));
		endVisit((Statement) node);
	}
	public void endVisit(XorAssignExp node) {
		Assert.isTrue(XorAssignExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}
	public void endVisit(XorExp node) {
		Assert.isTrue(XorExp.class.getSuperclass().equals(BinExp.class));
		endVisit((BinExp) node);
	}

	public void endVisit(FileInitExp node) {
		Assert.isTrue(FileInitExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}

	public void endVisit(LineInitExp node) {
		Assert.isTrue(LineInitExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}

	public void endVisit(PostBlitDeclaration node) {
		Assert.isTrue(PostBlitDeclaration.class.getSuperclass().equals(FuncDeclaration.class));
		endVisit((FuncDeclaration) node);
	}

	public void endVisit(TemplateThisParameter node) {
		Assert.isTrue(TemplateThisParameter.class.getSuperclass().equals(TemplateTypeParameter.class));
		endVisit((TemplateTypeParameter) node);
	}

	public void endVisit(TypeReturn node) {
		Assert.isTrue(TypeReturn.class.getSuperclass().equals(TypeQualified.class));
		endVisit((TypeQualified) node);
	}

	public boolean visit(FileInitExp node) {
		Assert.isTrue(FileInitExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}

	public boolean visit(LineInitExp node) {
		Assert.isTrue(LineInitExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}

	public boolean visit(PostBlitDeclaration node) {
		Assert.isTrue(PostBlitDeclaration.class.getSuperclass().equals(FuncDeclaration.class));
		return visit((FuncDeclaration) node);
	}

	public boolean visit(TemplateThisParameter node) {
		Assert.isTrue(TemplateThisParameter.class.getSuperclass().equals(TemplateTypeParameter.class));
		return visit((TemplateTypeParameter) node);
	}

	public boolean visit(TypeReturn node) {
		Assert.isTrue(TypeReturn.class.getSuperclass().equals(TypeQualified.class));
		return visit((TypeQualified) node);
	}
	
	public boolean visit(DefaultInitExp node) {
		Assert.isTrue(DefaultInitExp.class.getSuperclass().equals(Expression.class));
		return visit((Expression) node);
	}
	
	public void endVisit(DefaultInitExp node) {
		Assert.isTrue(DefaultInitExp.class.getSuperclass().equals(Expression.class));
		endVisit((Expression) node);
	}


}
