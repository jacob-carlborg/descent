package descent.core.domX;

import util.tree.ITreeVisitor;
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

public interface IASTVisitor extends ITreeVisitor<ASTNode>{

	void preVisit(ASTNode elem);

	void postVisit(ASTNode elem);

	/**
	 * Visits the element.
	 * @param element the element to visit
	 * @return true if children element should be visited
	 */
	boolean visit(ASTNode elem);

	/* -----------  Abstract Classes  ----------- */
	boolean visit(AbstractElement elem);

	boolean visit(Dsymbol elem);

	boolean visit(Declaration elem);

	boolean visit(Initializer elem);

	boolean visit(TemplateParameter elem);

	boolean visit(AggregateDeclaration elem);

	boolean visit(Statement elem);

	boolean visit(Type elem);

	boolean visit(Expression elem);

	boolean visit(BinaryExpression elem);

	boolean visit(UnaryExpression elem);

	boolean visit(Module elem);

	boolean visit(ModuleDeclaration elem);

	boolean visit(ImportDeclaration elem);

	boolean visit(Import elem);

	boolean visit(FuncDeclaration elem);

	boolean visit(VarDeclaration elem);

	boolean visit(Identifier elem);

	boolean visit(AlignDeclaration elem);

	boolean visit(ConditionalDeclaration elem);

	boolean visit(DebugSymbol elem);

	boolean visit(AliasDeclaration elem);

	boolean visit(CtorDeclaration elem);

	boolean visit(DeleteDeclaration elem);

	boolean visit(DtorDeclaration elem);

	boolean visit(FuncLiteralDeclaration elem);

	boolean visit(NewDeclaration elem);

	boolean visit(StaticCtorDeclaration elem);

	boolean visit(StaticDtorDeclaration elem);

	boolean visit(TypedefDeclaration elem);

	boolean visit(EnumDeclaration elem);

	boolean visit(EnumMember elem);

	boolean visit(ArrayInitializer elem);

	boolean visit(ExpInitializer elem);

	boolean visit(StructInitializer elem);

	boolean visit(VoidInitializer elem);

	boolean visit(InvariantDeclaration elem);

	boolean visit(LinkDeclaration elem);

	boolean visit(PragmaDeclaration elem);

	boolean visit(ProtDeclaration elem);

	boolean visit(ScopeDsymbol elem);

	boolean visit(ClassDeclaration elem);

	boolean visit(InterfaceDeclaration elem);

	boolean visit(StructDeclaration elem);

	boolean visit(UnionDeclaration elem);

	boolean visit(StaticAssert elem);

	boolean visit(StaticIfDeclaration elem);

	boolean visit(StorageClassDeclaration elem);

	boolean visit(TemplateDeclaration elem);

	boolean visit(TemplateMixin elem);

	boolean visit(UnitTestDeclaration elem);

	boolean visit(VersionSymbol elem);

	boolean visit(TemplateInstance elem);

	boolean visit(QualifiedName elem);

	boolean visit(SelectiveImport elem);

	boolean visit(TypeSpecialization elem);

	boolean visit(TypeArray elem);

	boolean visit(TypeAArray elem);

	boolean visit(TypeDArray elem);

	boolean visit(TypeSArray elem);

	boolean visit(TypeBasic elem);

	boolean visit(TypeDelegate elem);

	boolean visit(TypeFunction elem);

	boolean visit(TypePointer elem);

	boolean visit(TypeQualified elem);

	boolean visit(TypeIdentifier elem);

	boolean visit(TypeInstance elem);

	boolean visit(TypeTypeof elem);

	boolean visit(TypeSlice elem);

	boolean visit(TypeStruct elem);

	boolean visit(TemplateAliasParameter elem);

	boolean visit(TemplateTupleParameter elem);

	boolean visit(TemplateTypeParameter elem);

	boolean visit(TemplateValueParameter elem);

	boolean visit(AsmStatement elem);

	boolean visit(BreakStatement elem);

	boolean visit(CaseStatement elem);

	boolean visit(CompoundStatement elem);

	boolean visit(ConditionalStatement elem);

	boolean visit(ContinueStatement elem);

	boolean visit(DeclarationStatement elem);

	boolean visit(DefaultStatement elem);

	boolean visit(DoStatement elem);

	boolean visit(ExpStatement elem);

	boolean visit(ForeachStatement elem);

	boolean visit(ForStatement elem);

	boolean visit(GotoCaseStatement elem);

	boolean visit(GotoDefaultStatement elem);

	boolean visit(GotoStatement elem);

	boolean visit(IfStatement elem);

	boolean visit(LabelStatement elem);

	boolean visit(OnScopeStatement elem);

	boolean visit(PragmaStatement elem);

	boolean visit(ReturnStatement elem);

	boolean visit(ScopeStatement elem);

	boolean visit(StaticAssertStatement elem);

	boolean visit(SwitchStatement elem);

	boolean visit(SynchronizedStatement elem);

	boolean visit(ThrowStatement elem);

	boolean visit(TryCatchStatement elem);

	boolean visit(TryFinallyStatement elem);

	boolean visit(VolatileStatement elem);

	boolean visit(WhileStatement elem);

	boolean visit(WithStatement elem);

	boolean visit(ArrayExp elem);

	boolean visit(ArrayLiteralExp elem);

	boolean visit(AssertExp elem);

	boolean visit(CallExp elem);

	boolean visit(CastExp elem);

	boolean visit(CondExp elem);

	boolean visit(DeleteExp elem);

	boolean visit(DollarExp elem);

	boolean visit(DotIdExp elem);

	boolean visit(DotTemplateInstanceExp elem);

	boolean visit(FuncExp elem);

	boolean visit(IdentifierExp elem);

	boolean visit(IftypeExp elem);

	boolean visit(IntegerExp elem);

	boolean visit(NewAnonClassExp elem);

	boolean visit(NewExp elem);

	boolean visit(NullExp elem);

	boolean visit(ParenthesizedExpression elem);

	boolean visit(RealExp elem);

	boolean visit(ScopeExp elem);

	boolean visit(SliceExp elem);

	boolean visit(StringExp elem);

	boolean visit(SuperExp elem);

	boolean visit(ThisExp elem);

	boolean visit(TypeDotIdExp elem);

	boolean visit(TypeExp elem);

	boolean visit(TypeidExp elem);

	boolean visit(AddAssignExp elem);

	boolean visit(AddExp elem);

	boolean visit(AndAndExp elem);

	boolean visit(AndAssignExp elem);

	boolean visit(AndExp elem);

	boolean visit(AssignExp elem);

	boolean visit(CatAssignExp elem);

	boolean visit(CatExp elem);

	boolean visit(CmpExp elem);

	boolean visit(CommaExp elem);

	boolean visit(DivAssignExp elem);

	boolean visit(DivExp elem);

	boolean visit(EqualExp elem);

	boolean visit(IdentityExp elem);

	boolean visit(InExp elem);

	boolean visit(MinAssignExp elem);

	boolean visit(MinExp elem);

	boolean visit(ModAssignExp elem);

	boolean visit(ModExp elem);

	boolean visit(MulAssignExp elem);

	boolean visit(MulExp elem);

	boolean visit(OrAssignExp elem);

	boolean visit(OrExp elem);

	boolean visit(OrOrExp elem);

	boolean visit(ShlAssignExp elem);

	boolean visit(ShlExp elem);

	boolean visit(ShrAssignExp elem);

	boolean visit(ShrExp elem);

	boolean visit(UshrAssignExp elem);

	boolean visit(UshrExp elem);

	boolean visit(XorAssignExp elem);

	boolean visit(XorExp elem);

	boolean visit(AddrExp elem);

	boolean visit(ComExp elem);

	boolean visit(NegExp elem);

	boolean visit(NotExp elem);

	boolean visit(PostDecExp elem);

	boolean visit(PostIncExp elem);

	boolean visit(PtrExp elem);

	boolean visit(UAddExp elem);

	/* -----------   ---------- */
	boolean visit(Argument elem);

	boolean visit(BaseClass elem);

	boolean visit(Catch elem);

	boolean visit(IDebugDeclaration elem);

	boolean visit(IVersionDeclaration elem);

	boolean visit(IIftypeDeclaration elem);

	boolean visit(IDebugStatement elem);

	boolean visit(IVersionStatement elem);

	boolean visit(IStaticIfStatement elem);

	boolean visit(ITrueExpression elem);

	boolean visit(IFalseExpression elem);

	boolean visit(IIntegerExpression elem);

	/* ===== End visit  ==== */
	void endVisit(ASTNode elem);

	void endVisit(IElement elem);

}