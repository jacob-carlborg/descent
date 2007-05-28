package descent.core.domX;

import util.tree.TreeVisitor;
import descent.core.dom.IDebugDeclaration;
import descent.core.dom.IDebugStatement;
import descent.core.dom.IElement;
import descent.core.dom.IFalseExpression;
import descent.core.dom.IIftypeDeclaration;
import descent.core.dom.IIntegerExpression;
import descent.core.dom.IStaticIfStatement;
import descent.core.dom.ITrueExpression;
import descent.core.dom.IVersionDeclaration;
import descent.core.dom.IVersionStatement;
import descent.internal.core.dom.*;
import dtool.dom.ast.ASTNode;

/**
 * An abstract visitor class that that delegates each visit method, to the visit
 * method of the element's superclass 
 */
public abstract class ASTUpTreeVisitor extends TreeVisitor<ASTNode> implements IASTVisitor {

	/* ====================================================== */

	/**
	 * Visits the element.
	 * @param element the element to visit
	 * @return true if children element should be visited
	 */
	public boolean visit(ASTNode elem) {
		return true;
	}

	/* -----------  Abstract Classes  ----------- */
	public boolean visit(AbstractElement elem) {
		//ensureVisitIsNotDirectVisit(elem);
		return visitAsSuperType(elem, AbstractElement.class);
	}

	public boolean visit(Dsymbol elem) {
		return visitAsSuperType(elem, Dsymbol.class);
	}

	public boolean visit(Declaration elem) {
		return visitAsSuperType(elem, Declaration.class);
	}

	public boolean visit(Initializer elem) {
		return visitAsSuperType(elem, Initializer.class);
	}

	public boolean visit(TemplateParameter elem) {
		return visitAsSuperType(elem, TemplateParameter.class);
	}

	public boolean visit(AggregateDeclaration elem) {
		return visitAsSuperType(elem, AggregateDeclaration.class);
	}

	public boolean visit(Statement elem) {
		return visitAsSuperType(elem, Statement.class);
	}

	public boolean visit(Type elem) {
		return visitAsSuperType(elem, Type.class);
	}

	public boolean visit(Expression elem) {
		return visitAsSuperType(elem, Expression.class);
	}

	public boolean visit(BinaryExpression elem) {
		return visitAsSuperType(elem, BinaryExpression.class);
	}

	public boolean visit(UnaryExpression elem) {
		return visitAsSuperType(elem, UnaryExpression.class);
	}

	/* -----------  Concrete Classes  ----------- */

	public boolean visit(Module elem) {
		return visitAsSuperType(elem, Module.class);
	}

	public boolean visit(ModuleDeclaration elem) {
		return visitAsSuperType(elem, ModuleDeclaration.class);
	}

	public boolean visit(ImportDeclaration elem) {
		return visitAsSuperType(elem, ImportDeclaration.class);
	}

	public boolean visit(Import elem) {
		return visitAsSuperType(elem, Import.class);
	}

	/* -----  Defs  ----- */

	public boolean visit(FuncDeclaration elem) {
		return visitAsSuperType(elem, FuncDeclaration.class);
	}

	public boolean visit(VarDeclaration elem) {
		return visitAsSuperType(elem, VarDeclaration.class);
	}

	/* -----  Other concretes  ----- */

	public boolean visit(Identifier elem) {
		return visitAsSuperType(elem, Identifier.class);
	}

	public boolean visit(AlignDeclaration elem) {
		return visitAsSuperType(elem, AlignDeclaration.class);
	}

	public boolean visit(ConditionalDeclaration elem) {
		return visitAsSuperType(elem, ConditionalDeclaration.class);
	}

	public boolean visit(DebugSymbol elem) {
		return visitAsSuperType(elem, DebugSymbol.class);
	}

	public boolean visit(AliasDeclaration elem) {
		return visitAsSuperType(elem, AliasDeclaration.class);
	}

	public boolean visit(CtorDeclaration elem) {
		return visitAsSuperType(elem, CtorDeclaration.class);
	}

	public boolean visit(DeleteDeclaration elem) {
		return visitAsSuperType(elem, DeleteDeclaration.class);
	}

	public boolean visit(DtorDeclaration elem) {
		return visitAsSuperType(elem, DtorDeclaration.class);
	}

	public boolean visit(FuncLiteralDeclaration elem) {
		return visitAsSuperType(elem, FuncLiteralDeclaration.class);
	}

	public boolean visit(NewDeclaration elem) {
		return visitAsSuperType(elem, NewDeclaration.class);
	}

	public boolean visit(StaticCtorDeclaration elem) {
		return visitAsSuperType(elem, StaticCtorDeclaration.class);
	}

	public boolean visit(StaticDtorDeclaration elem) {
		return visitAsSuperType(elem, StaticDtorDeclaration.class);
	}

	public boolean visit(TypedefDeclaration elem) {
		return visitAsSuperType(elem, TypedefDeclaration.class);
	}

	public boolean visit(EnumDeclaration elem) {
		return visitAsSuperType(elem, EnumDeclaration.class);
	}

	public boolean visit(EnumMember elem) {
		return visitAsSuperType(elem, EnumMember.class);
	}

	public boolean visit(ArrayInitializer elem) {
		return visitAsSuperType(elem, ArrayInitializer.class);
	}

	public boolean visit(ExpInitializer elem) {
		return visitAsSuperType(elem, ExpInitializer.class);
	}

	public boolean visit(StructInitializer elem) {
		return visitAsSuperType(elem, StructInitializer.class);
	}

	public boolean visit(VoidInitializer elem) {
		return visitAsSuperType(elem, VoidInitializer.class);
	}

	public boolean visit(InvariantDeclaration elem) {
		return visitAsSuperType(elem, InvariantDeclaration.class);
	}

	public boolean visit(LinkDeclaration elem) {
		return visitAsSuperType(elem, LinkDeclaration.class);
	}

	public boolean visit(PragmaDeclaration elem) {
		return visitAsSuperType(elem, PragmaDeclaration.class);
	}

	public boolean visit(ProtDeclaration elem) {
		return visitAsSuperType(elem, ProtDeclaration.class);
	}

	public boolean visit(ScopeDsymbol elem) {
		return visitAsSuperType(elem, ScopeDsymbol.class);
	}

	public boolean visit(ClassDeclaration elem) {
		return visitAsSuperType(elem, ClassDeclaration.class);
	}

	public boolean visit(InterfaceDeclaration elem) {
		return visitAsSuperType(elem, InterfaceDeclaration.class);
	}

	public boolean visit(StructDeclaration elem) {
		return visitAsSuperType(elem, StructDeclaration.class);
	}

	public boolean visit(UnionDeclaration elem) {
		return visitAsSuperType(elem, UnionDeclaration.class);
	}

	public boolean visit(StaticAssert elem) {
		return visitAsSuperType(elem, StaticAssert.class);
	}

	public boolean visit(StaticIfDeclaration elem) {
		return visitAsSuperType(elem, StaticIfDeclaration.class);
	}

	public boolean visit(StorageClassDeclaration elem) {
		return visitAsSuperType(elem, StorageClassDeclaration.class);
	}

	public boolean visit(TemplateDeclaration elem) {
		return visitAsSuperType(elem, TemplateDeclaration.class);
	}

	public boolean visit(TemplateMixin elem) {
		return visitAsSuperType(elem, TemplateMixin.class);
	}

	public boolean visit(UnitTestDeclaration elem) {
		return visitAsSuperType(elem, UnitTestDeclaration.class);
	}

	public boolean visit(VersionSymbol elem) {
		return visitAsSuperType(elem, VersionSymbol.class);
	}

	public boolean visit(TemplateInstance elem) {
		return visitAsSuperType(elem, TemplateInstance.class);
	}

	public boolean visit(QualifiedName elem) {
		return visitAsSuperType(elem, QualifiedName.class);
	}

	public boolean visit(SelectiveImport elem) {
		return visitAsSuperType(elem, SelectiveImport.class);
	}

	public boolean visit(TypeSpecialization elem) {
		return visitAsSuperType(elem, TypeSpecialization.class);
	}

	public boolean visit(TypeArray elem) {
		return visitAsSuperType(elem, TypeArray.class);
	}

	public boolean visit(TypeAArray elem) {
		return visitAsSuperType(elem, TypeAArray.class);
	}

	public boolean visit(TypeDArray elem) {
		return visitAsSuperType(elem, TypeDArray.class);
	}

	public boolean visit(TypeSArray elem) {
		return visitAsSuperType(elem, TypeSArray.class);
	}

	public boolean visit(TypeBasic elem) {
		return visitAsSuperType(elem, TypeBasic.class);
	}

	public boolean visit(TypeDelegate elem) {
		return visitAsSuperType(elem, TypeDelegate.class);
	}

	public boolean visit(TypeFunction elem) {
		return visitAsSuperType(elem, TypeFunction.class);
	}

	public boolean visit(TypePointer elem) {
		return visitAsSuperType(elem, TypePointer.class);
	}

	public boolean visit(TypeQualified elem) {
		return visitAsSuperType(elem, TypeQualified.class);
	}

	public boolean visit(TypeIdentifier elem) {
		return visitAsSuperType(elem, TypeIdentifier.class);
	}

	public boolean visit(TypeInstance elem) {
		return visitAsSuperType(elem, TypeInstance.class);
	}

	public boolean visit(TypeTypeof elem) {
		return visitAsSuperType(elem, TypeTypeof.class);
	}

	public boolean visit(TypeSlice elem) {
		return visitAsSuperType(elem, TypeSlice.class);
	}

	public boolean visit(TypeStruct elem) {
		return visitAsSuperType(elem, TypeStruct.class);
	}

	public boolean visit(TemplateAliasParameter elem) {
		return visitAsSuperType(elem, TemplateAliasParameter.class);
	}

	public boolean visit(TemplateTupleParameter elem) {
		return visitAsSuperType(elem, TemplateTupleParameter.class);
	}

	public boolean visit(TemplateTypeParameter elem) {
		return visitAsSuperType(elem, TemplateTypeParameter.class);
	}

	public boolean visit(TemplateValueParameter elem) {
		return visitAsSuperType(elem, TemplateValueParameter.class);
	}

	public boolean visit(AsmStatement elem) {
		return visitAsSuperType(elem, AsmStatement.class);
	}

	public boolean visit(BreakStatement elem) {
		return visitAsSuperType(elem, BreakStatement.class);
	}

	public boolean visit(CaseStatement elem) {
		return visitAsSuperType(elem, CaseStatement.class);
	}

	public boolean visit(CompoundStatement elem) {
		return visitAsSuperType(elem, CompoundStatement.class);
	}

	public boolean visit(ConditionalStatement elem) {
		return visitAsSuperType(elem, ConditionalStatement.class);
	}

	public boolean visit(ContinueStatement elem) {
		return visitAsSuperType(elem, ContinueStatement.class);
	}

	public boolean visit(DeclarationStatement elem) {
		return visitAsSuperType(elem, DeclarationStatement.class);
	}

	public boolean visit(DefaultStatement elem) {
		return visitAsSuperType(elem, DefaultStatement.class);
	}

	public boolean visit(DoStatement elem) {
		return visitAsSuperType(elem, DoStatement.class);
	}

	public boolean visit(ExpStatement elem) {
		return visitAsSuperType(elem, ExpStatement.class);
	}

	public boolean visit(ForeachStatement elem) {
		return visitAsSuperType(elem, ForeachStatement.class);
	}

	public boolean visit(ForStatement elem) {
		return visitAsSuperType(elem, ForStatement.class);
	}

	public boolean visit(GotoCaseStatement elem) {
		return visitAsSuperType(elem, GotoCaseStatement.class);
	}

	public boolean visit(GotoDefaultStatement elem) {
		return visitAsSuperType(elem, GotoDefaultStatement.class);
	}

	public boolean visit(GotoStatement elem) {
		return visitAsSuperType(elem, GotoStatement.class);
	}

	public boolean visit(IfStatement elem) {
		return visitAsSuperType(elem, IfStatement.class);
	}

	public boolean visit(LabelStatement elem) {
		return visitAsSuperType(elem, LabelStatement.class);
	}

	public boolean visit(OnScopeStatement elem) {
		return visitAsSuperType(elem, OnScopeStatement.class);
	}

	public boolean visit(PragmaStatement elem) {
		return visitAsSuperType(elem, PragmaStatement.class);
	}

	public boolean visit(ReturnStatement elem) {
		return visitAsSuperType(elem, ReturnStatement.class);
	}

	public boolean visit(ScopeStatement elem) {
		return visitAsSuperType(elem, ScopeStatement.class);
	}

	public boolean visit(StaticAssertStatement elem) {
		return visitAsSuperType(elem, StaticAssertStatement.class);
	}

	public boolean visit(SwitchStatement elem) {
		return visitAsSuperType(elem, SwitchStatement.class);
	}

	public boolean visit(SynchronizedStatement elem) {
		return visitAsSuperType(elem, SynchronizedStatement.class);
	}

	public boolean visit(ThrowStatement elem) {
		return visitAsSuperType(elem, ThrowStatement.class);
	}

	public boolean visit(TryCatchStatement elem) {
		return visitAsSuperType(elem, TryCatchStatement.class);
	}

	public boolean visit(TryFinallyStatement elem) {
		return visitAsSuperType(elem, TryFinallyStatement.class);
	}

	public boolean visit(VolatileStatement elem) {
		return visitAsSuperType(elem, VolatileStatement.class);
	}

	public boolean visit(WhileStatement elem) {
		return visitAsSuperType(elem, WhileStatement.class);
	}

	public boolean visit(WithStatement elem) {
		return visitAsSuperType(elem, WithStatement.class);
	}

	public boolean visit(ArrayExp elem) {
		return visitAsSuperType(elem, ArrayExp.class);
	}

	public boolean visit(ArrayLiteralExp elem) {
		return visitAsSuperType(elem, ArrayLiteralExp.class);
	}

	public boolean visit(AssertExp elem) {
		return visitAsSuperType(elem, AssertExp.class);
	}

	public boolean visit(CallExp elem) {
		return visitAsSuperType(elem, CallExp.class);
	}

	public boolean visit(CastExp elem) {
		return visitAsSuperType(elem, CastExp.class);
	}

	public boolean visit(CondExp elem) {
		return visitAsSuperType(elem, CondExp.class);
	}

	public boolean visit(DeleteExp elem) {
		return visitAsSuperType(elem, DeleteExp.class);
	}

	public boolean visit(DollarExp elem) {
		return visitAsSuperType(elem, DollarExp.class);
	}

	public boolean visit(DotIdExp elem) {
		return visitAsSuperType(elem, DotIdExp.class);
	}

	public boolean visit(DotTemplateInstanceExp elem) {
		return visitAsSuperType(elem, DotTemplateInstanceExp.class);
	}

	public boolean visit(FuncExp elem) {
		return visitAsSuperType(elem, FuncExp.class);
	}

	public boolean visit(IdentifierExp elem) {
		return visitAsSuperType(elem, IdentifierExp.class);
	}

	public boolean visit(IftypeExp elem) {
		return visitAsSuperType(elem, IftypeExp.class);
	}

	public boolean visit(IntegerExp elem) {
		return visitAsSuperType(elem, IntegerExp.class);
	}

	public boolean visit(NewAnonClassExp elem) {
		return visitAsSuperType(elem, NewAnonClassExp.class);
	}

	public boolean visit(NewExp elem) {
		return visitAsSuperType(elem, NewExp.class);
	}

	public boolean visit(NullExp elem) {
		return visitAsSuperType(elem, NullExp.class);
	}

	public boolean visit(ParenthesizedExpression elem) {
		return visitAsSuperType(elem, ParenthesizedExpression.class);
	}

	public boolean visit(RealExp elem) {
		return visitAsSuperType(elem, RealExp.class);
	}

	public boolean visit(ScopeExp elem) {
		return visitAsSuperType(elem, ScopeExp.class);
	}

	public boolean visit(SliceExp elem) {
		return visitAsSuperType(elem, SliceExp.class);
	}

	public boolean visit(StringExp elem) {
		return visitAsSuperType(elem, StringExp.class);
	}

	public boolean visit(SuperExp elem) {
		return visitAsSuperType(elem, SuperExp.class);
	}

	public boolean visit(ThisExp elem) {
		return visitAsSuperType(elem, ThisExp.class);
	}

	public boolean visit(TypeDotIdExp elem) {
		return visitAsSuperType(elem, TypeDotIdExp.class);
	}

	public boolean visit(TypeExp elem) {
		return visitAsSuperType(elem, TypeExp.class);
	}

	public boolean visit(TypeidExp elem) {
		return visitAsSuperType(elem, TypeidExp.class);
	}

	public boolean visit(AddAssignExp elem) {
		return visitAsSuperType(elem, AddAssignExp.class);
	}

	public boolean visit(AddExp elem) {
		return visitAsSuperType(elem, AddExp.class);
	}

	public boolean visit(AndAndExp elem) {
		return visitAsSuperType(elem, AndAndExp.class);
	}

	public boolean visit(AndAssignExp elem) {
		return visitAsSuperType(elem, AndAssignExp.class);
	}

	public boolean visit(AndExp elem) {
		return visitAsSuperType(elem, AndExp.class);
	}

	public boolean visit(AssignExp elem) {
		return visitAsSuperType(elem, AssignExp.class);
	}

	public boolean visit(CatAssignExp elem) {
		return visitAsSuperType(elem, CatAssignExp.class);
	}

	public boolean visit(CatExp elem) {
		return visitAsSuperType(elem, CatExp.class);
	}

	public boolean visit(CmpExp elem) {
		return visitAsSuperType(elem, CmpExp.class);
	}

	public boolean visit(CommaExp elem) {
		return visitAsSuperType(elem, CommaExp.class);
	}

	public boolean visit(DivAssignExp elem) {
		return visitAsSuperType(elem, DivAssignExp.class);
	}

	public boolean visit(DivExp elem) {
		return visitAsSuperType(elem, DivExp.class);
	}

	public boolean visit(EqualExp elem) {
		return visitAsSuperType(elem, EqualExp.class);
	}

	public boolean visit(IdentityExp elem) {
		return visitAsSuperType(elem, IdentityExp.class);
	}

	public boolean visit(InExp elem) {
		return visitAsSuperType(elem, InExp.class);
	}

	public boolean visit(MinAssignExp elem) {
		return visitAsSuperType(elem, MinAssignExp.class);
	}

	public boolean visit(MinExp elem) {
		return visitAsSuperType(elem, MinExp.class);
	}

	public boolean visit(ModAssignExp elem) {
		return visitAsSuperType(elem, ModAssignExp.class);
	}

	public boolean visit(ModExp elem) {
		return visitAsSuperType(elem, ModExp.class);
	}

	public boolean visit(MulAssignExp elem) {
		return visitAsSuperType(elem, MulAssignExp.class);
	}

	public boolean visit(MulExp elem) {
		return visitAsSuperType(elem, MulExp.class);
	}

	public boolean visit(OrAssignExp elem) {
		return visitAsSuperType(elem, OrAssignExp.class);
	}

	public boolean visit(OrExp elem) {
		return visitAsSuperType(elem, OrExp.class);
	}

	public boolean visit(OrOrExp elem) {
		return visitAsSuperType(elem, OrOrExp.class);
	}

	public boolean visit(ShlAssignExp elem) {
		return visitAsSuperType(elem, ShlAssignExp.class);
	}

	public boolean visit(ShlExp elem) {
		return visitAsSuperType(elem, ShlExp.class);
	}

	public boolean visit(ShrAssignExp elem) {
		return visitAsSuperType(elem, ShrAssignExp.class);
	}

	public boolean visit(ShrExp elem) {
		return visitAsSuperType(elem, ShrExp.class);
	}

	public boolean visit(UshrAssignExp elem) {
		return visitAsSuperType(elem, UshrAssignExp.class);
	}

	public boolean visit(UshrExp elem) {
		return visitAsSuperType(elem, UshrExp.class);
	}

	public boolean visit(XorAssignExp elem) {
		return visitAsSuperType(elem, XorAssignExp.class);
	}

	public boolean visit(XorExp elem) {
		return visitAsSuperType(elem, XorExp.class);
	}

	public boolean visit(AddrExp elem) {
		return visitAsSuperType(elem, AddrExp.class);
	}

	public boolean visit(ComExp elem) {
		return visitAsSuperType(elem, ComExp.class);
	}

	public boolean visit(NegExp elem) {
		return visitAsSuperType(elem, NegExp.class);
	}

	public boolean visit(NotExp elem) {
		return visitAsSuperType(elem, NotExp.class);
	}

	public boolean visit(PostDecExp elem) {
		return visitAsSuperType(elem, PostDecExp.class);
	}

	public boolean visit(PostIncExp elem) {
		return visitAsSuperType(elem, PostIncExp.class);
	}

	public boolean visit(PtrExp elem) {
		return visitAsSuperType(elem, PtrExp.class);
	}

	public boolean visit(UAddExp elem) {
		return visitAsSuperType(elem, UAddExp.class);
	}

	/* -----------   ---------- */
	public boolean visit(Argument elem) {
		return visitAsSuperType(elem, Argument.class);
	}

	public boolean visit(BaseClass elem) {
		return visitAsSuperType(elem, BaseClass.class);
	}

	public boolean visit(Catch elem) {
		return visitAsSuperType(elem, Catch.class);
	}

	/* ------------- Ary Interfaces -------------------- */

	public boolean visit(IDebugDeclaration elem) {
		return visit((ConditionalDeclaration) elem);
	}

	public boolean visit(IVersionDeclaration elem) {
		return visit((ConditionalDeclaration) elem);
	}

	public boolean visit(IIftypeDeclaration elem) {
		return visit((ConditionalDeclaration) elem);
	}

	public boolean visit(IDebugStatement elem) {
		return visit((ConditionalStatement) elem);
	}

	public boolean visit(IVersionStatement elem) {
		return visit((ConditionalStatement) elem);
	}

	public boolean visit(IStaticIfStatement elem) {
		return visit((ConditionalStatement) elem);
	}

	public boolean visit(ITrueExpression elem) {
		return visit((IntegerExp) elem);
	}

	public boolean visit(IFalseExpression elem) {
		return visit((IntegerExp) elem);
	}

	public boolean visit(IIntegerExpression elem) {
		return visit((IntegerExp) elem);
	}

	/* ====================================================== */

	/**
	 * Ends the visit to the element.
	 * @param element the element to visit
	 */
	public void endVisit(ASTNode elem) {
	}

	public void endVisit(IElement elem) {
		endVisit((ASTNode) elem);
	}

}
