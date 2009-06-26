package descent.internal.compiler.parser.ast;

import melnorme.miscutil.tree.ITreeVisitor;
import descent.internal.compiler.parser.*;
import descent.internal.compiler.parser.Package;

public interface IASTVisitor extends ITreeVisitor<ASTNode> {

	void preVisit(ASTNode node);
	void postVisit(ASTNode node);
	
	//boolean visit(IASTNode node);
	//void endVisit(IASTNode node);

	/** Visits the node element. 
	 * @return true if children element should be visited */
	boolean visit(ASTNode node);
	/** Does a post-visit to the node element. */ 
	void endVisit(ASTNode node);

	boolean visit(ASTDmdNode node);
	
	/* -----------  ----------- */
	
	boolean visit(AddAssignExp node);
	boolean visit(AddExp node);
	boolean visit(AddrExp node);
	boolean visit(AggregateDeclaration node);
	boolean visit(AliasDeclaration node);
	boolean visit(AliasThis node);
	boolean visit(AlignDeclaration node);
	boolean visit(AndAndExp node);
	boolean visit(AndAssignExp node);
	boolean visit(AndExp node);
	boolean visit(AnonDeclaration node);
	boolean visit(AnonymousAggregateDeclaration node);
	boolean visit(Argument node);
	boolean visit(ArrayExp node);
	boolean visit(ArrayInitializer node);
	boolean visit(ArrayLengthExp node);
	boolean visit(ArrayLiteralExp node);
	boolean visit(ArrayScopeSymbol node);
	boolean visit(AsmBlock node);
	boolean visit(AsmStatement node);
	boolean visit(AssertExp node);
	boolean visit(AssignExp node);
	boolean visit(AssocArrayLiteralExp node);
	//boolean visit(ASTNode node);
	boolean visit(AttribDeclaration node);
	boolean visit(BaseClass node);
	boolean visit(BinExp node);
	boolean visit(BoolExp node);
	boolean visit(BreakStatement node);
	boolean visit(CallExp node);
	boolean visit(CaseStatement node);
	boolean visit(CastExp node);
	boolean visit(CatAssignExp node);
	boolean visit(Catch node);
	boolean visit(CatExp node);
	boolean visit(ClassDeclaration node);
	boolean visit(ClassInfoDeclaration node);
	boolean visit(CmpExp node);
	boolean visit(ComExp node);
	boolean visit(CommaExp node);
	boolean visit(CompileDeclaration node);
	boolean visit(CompileExp node);
	boolean visit(CompileStatement node);
	boolean visit(ComplexExp node);
	boolean visit(CompoundStatement node);
	boolean visit(CondExp node);
	boolean visit(Condition node);
	boolean visit(ConditionalDeclaration node);
	boolean visit(ConditionalStatement node);
	boolean visit(ContinueStatement node);
	boolean visit(CtorDeclaration node);
	boolean visit(DebugCondition node);
	boolean visit(DebugSymbol node);
	boolean visit(Declaration node);
	boolean visit(DeclarationExp node);
	boolean visit(DeclarationStatement node);
	boolean visit(DefaultInitExp node);
	boolean visit(DefaultStatement node);
	boolean visit(DelegateExp node);
	boolean visit(DeleteDeclaration node);
	boolean visit(DeleteExp node);
	boolean visit(DivAssignExp node);
	boolean visit(DivExp node);
	boolean visit(DollarExp node);
	boolean visit(DoStatement node);
	boolean visit(DotExp node);
	boolean visit(DotIdExp node);
	boolean visit(DotTemplateExp node);
	boolean visit(DotTemplateInstanceExp node);
	boolean visit(DotTypeExp node);
	boolean visit(DotVarExp node);
	boolean visit(Dsymbol node);
	boolean visit(DsymbolExp node);
	boolean visit(DtorDeclaration node);
	boolean visit(EnumDeclaration node);
	boolean visit(EnumMember node);
	boolean visit(EqualExp node);
	boolean visit(ExpInitializer node);
	boolean visit(Expression node);
	boolean visit(ExpStatement node);
	boolean visit(FileExp node);
	boolean visit(FileInitExp node);
	boolean visit(ForeachRangeStatement node);
	boolean visit(ForeachStatement node);
	boolean visit(ForStatement node);
	boolean visit(FuncAliasDeclaration node);
	boolean visit(FuncDeclaration node);
	boolean visit(FuncExp node);
	boolean visit(FuncLiteralDeclaration node);
	boolean visit(GotoCaseStatement node);
	boolean visit(GotoDefaultStatement node);
	boolean visit(GotoStatement node);
	boolean visit(HaltExp node);
	boolean visit(IdentifierExp node);
	boolean visit(IdentityExp node);
	boolean visit(IfStatement node);
	boolean visit(IftypeCondition node);
	boolean visit(IsExp node);
	boolean visit(Import node);
	boolean visit(IndexExp node);
	boolean visit(InExp node);
	boolean visit(Initializer node);
	boolean visit(IntegerExp node);
	boolean visit(InterfaceDeclaration node);
	boolean visit(InvariantDeclaration node);
	boolean visit(LabelDsymbol node);
	boolean visit(LabelStatement node);
	boolean visit(LineInitExp node);
	boolean visit(LinkDeclaration node);
	boolean visit(MinAssignExp node);
	boolean visit(MinExp node);
	boolean visit(ModAssignExp node);
	boolean visit(ModExp node);
	boolean visit(Modifier node);
	boolean visit(Module node);
	boolean visit(ModuleDeclaration node);
	boolean visit(ModuleInfoDeclaration node);
	boolean visit(MulAssignExp node);
	boolean visit(MulExp node);
	boolean visit(NegExp node);
	boolean visit(NewAnonClassExp node);
	boolean visit(NewDeclaration node);
	boolean visit(NewExp node);
	boolean visit(NotExp node);
	boolean visit(NullExp node);
	boolean visit(OnScopeStatement node);
	boolean visit(OrAssignExp node);
	boolean visit(OrExp node);
	boolean visit(OrOrExp node);
	boolean visit(Package node);
	boolean visit(PostBlitDeclaration node);
	boolean visit(PostExp node);
	boolean visit(PragmaDeclaration node);
	boolean visit(PragmaStatement node);
	boolean visit(ProtDeclaration node);
	boolean visit(PtrExp node);
	boolean visit(RealExp node);
	boolean visit(RemoveExp node);
	boolean visit(ReturnStatement node);
	boolean visit(ScopeDsymbol node);
	boolean visit(ScopeExp node);
	boolean visit(ScopeStatement node);
	boolean visit(ShlAssignExp node);
	boolean visit(ShlExp node);
	boolean visit(ShrAssignExp node);
	boolean visit(ShrExp node);
	boolean visit(SliceExp node);
	boolean visit(Statement node);
	boolean visit(StaticAssert node);
	boolean visit(StaticAssertStatement node);
	boolean visit(StaticCtorDeclaration node);
	boolean visit(StaticDtorDeclaration node);
	boolean visit(StaticIfCondition node);
	boolean visit(StaticIfDeclaration node);
	boolean visit(StorageClassDeclaration node);
	boolean visit(StringExp node);
	boolean visit(StructDeclaration node);
	boolean visit(StructInitializer node);
	boolean visit(SuperExp node);
	boolean visit(SwitchStatement node);
	boolean visit(SymOffExp node);
	boolean visit(SynchronizedStatement node);
	boolean visit(TemplateAliasParameter node);
	boolean visit(TemplateDeclaration node);
	boolean visit(TemplateExp node);
	boolean visit(TemplateInstance node);
	boolean visit(TemplateInstanceWrapper node);
	boolean visit(TemplateMixin node);
	boolean visit(TemplateParameter node);
	boolean visit(TemplateThisParameter node);
	boolean visit(TemplateTupleParameter node);
	boolean visit(TemplateTypeParameter node);
	boolean visit(TemplateValueParameter node);
	boolean visit(ThisDeclaration node);
	boolean visit(ThisExp node);
	boolean visit(ThrowStatement node);
	boolean visit(TraitsExp node);
	boolean visit(TryCatchStatement node);
	boolean visit(TryFinallyStatement node);
	boolean visit(Tuple node);
	boolean visit(TupleDeclaration node);
	boolean visit(TupleExp node);
	boolean visit(Type node);
	boolean visit(TypeAArray node);
	boolean visit(TypeBasic node);
	boolean visit(TypeClass node);
	boolean visit(TypeDArray node);
	boolean visit(TypedefDeclaration node);
	boolean visit(TypeDelegate node);
	boolean visit(TypeEnum node);
	boolean visit(TypeExp node);
	boolean visit(TypeFunction node);
	boolean visit(TypeIdentifier node);
	boolean visit(TypeidExp node);
	boolean visit(TypeInfoArrayDeclaration node);
	boolean visit(TypeInfoAssociativeArrayDeclaration node);
	boolean visit(TypeInfoClassDeclaration node);
	boolean visit(TypeInfoDeclaration node);
	boolean visit(TypeInfoDelegateDeclaration node);
	boolean visit(TypeInfoEnumDeclaration node);
	boolean visit(TypeInfoFunctionDeclaration node);
	boolean visit(TypeInfoInterfaceDeclaration node);
	boolean visit(TypeInfoPointerDeclaration node);
	boolean visit(TypeInfoStaticArrayDeclaration node);
	boolean visit(TypeInfoStructDeclaration node);
	boolean visit(TypeInfoTypedefDeclaration node);
	boolean visit(TypeInstance node);
	boolean visit(TypePointer node);
	boolean visit(TypeReturn node);
	boolean visit(TypeQualified node);
	boolean visit(TypeSArray node);
	boolean visit(TypeSlice node);
	boolean visit(TypeStruct node);
	boolean visit(TypeTuple node);
	boolean visit(TypeTypedef node);
	boolean visit(TypeTypeof node);
	boolean visit(UAddExp node);
	boolean visit(UnaExp node);
	boolean visit(UnionDeclaration node);
	boolean visit(UnitTestDeclaration node);
	boolean visit(UnrolledLoopStatement node);
	boolean visit(UshrAssignExp node);
	boolean visit(UshrExp node);
	boolean visit(VarDeclaration node);
	boolean visit(VarExp node);
	boolean visit(Version node);
	boolean visit(VersionCondition node);
	boolean visit(VersionSymbol node);
	boolean visit(VoidInitializer node);
	boolean visit(VolatileStatement node);
	boolean visit(WhileStatement node);
	boolean visit(WithScopeSymbol node);
	boolean visit(WithStatement node);
	boolean visit(XorAssignExp node);
	boolean visit(XorExp node);
	
	void endVisit(AddAssignExp node);
	void endVisit(AddExp node);
	void endVisit(AddrExp node);
	void endVisit(AggregateDeclaration node);
	void endVisit(AliasDeclaration node);
	void endVisit(AliasThis node);
	void endVisit(AlignDeclaration node);
	void endVisit(AndAndExp node);
	void endVisit(AndAssignExp node);
	void endVisit(AndExp node);
	void endVisit(AnonDeclaration node);
	void endVisit(AnonymousAggregateDeclaration node);
	void endVisit(Argument node);
	void endVisit(ArrayExp node);
	void endVisit(ArrayInitializer node);
	void endVisit(ArrayLengthExp node);
	void endVisit(ArrayLiteralExp node);
	void endVisit(ArrayScopeSymbol node);
	void endVisit(AsmBlock node);
	void endVisit(AsmStatement node);
	void endVisit(AssertExp node);
	void endVisit(AssignExp node);
	void endVisit(AssocArrayLiteralExp node);
	//void endVisit(ASTNode node);
	void endVisit(AttribDeclaration node);
	void endVisit(BaseClass node);
	void endVisit(BinExp node);
	void endVisit(BoolExp node);
	void endVisit(BreakStatement node);
	void endVisit(CallExp node);
	void endVisit(CaseStatement node);
	void endVisit(CastExp node);
	void endVisit(CatAssignExp node);
	void endVisit(Catch node);
	void endVisit(CatExp node);
	void endVisit(ClassDeclaration node);
	void endVisit(ClassInfoDeclaration node);
	void endVisit(CmpExp node);
	void endVisit(ComExp node);
	void endVisit(CommaExp node);
	void endVisit(CompileDeclaration node);
	void endVisit(CompileExp node);
	void endVisit(CompileStatement node);
	void endVisit(ComplexExp node);
	void endVisit(CompoundStatement node);
	void endVisit(CondExp node);
	void endVisit(Condition node);
	void endVisit(ConditionalDeclaration node);
	void endVisit(ConditionalStatement node);
	void endVisit(ContinueStatement node);
	void endVisit(CtorDeclaration node);
	void endVisit(DebugCondition node);
	void endVisit(DebugSymbol node);
	void endVisit(Declaration node);
	void endVisit(DeclarationExp node);
	void endVisit(DeclarationStatement node);
	void endVisit(DefaultInitExp node);
	void endVisit(DefaultStatement node);
	void endVisit(DelegateExp node);
	void endVisit(DeleteDeclaration node);
	void endVisit(DeleteExp node);
	void endVisit(DivAssignExp node);
	void endVisit(DivExp node);
	void endVisit(DollarExp node);
	void endVisit(DoStatement node);
	void endVisit(DotExp node);
	void endVisit(DotIdExp node);
	void endVisit(DotTemplateExp node);
	void endVisit(DotTemplateInstanceExp node);
	void endVisit(DotTypeExp node);
	void endVisit(DotVarExp node);
	void endVisit(Dsymbol node);
	void endVisit(DsymbolExp node);
	void endVisit(DtorDeclaration node);
	void endVisit(EnumDeclaration node);
	void endVisit(EnumMember node);
	void endVisit(EqualExp node);
	void endVisit(ExpInitializer node);
	void endVisit(Expression node);
	void endVisit(ExpStatement node);
	void endVisit(FileExp node);
	void endVisit(FileInitExp node);
	void endVisit(ForeachRangeStatement node);
	void endVisit(ForeachStatement node);
	void endVisit(ForStatement node);
	void endVisit(FuncAliasDeclaration node);
	void endVisit(FuncDeclaration node);
	void endVisit(FuncExp node);
	void endVisit(FuncLiteralDeclaration node);
	void endVisit(GotoCaseStatement node);
	void endVisit(GotoDefaultStatement node);
	void endVisit(GotoStatement node);
	void endVisit(HaltExp node);
	void endVisit(IdentifierExp node);
	void endVisit(IdentityExp node);
	void endVisit(IfStatement node);
	void endVisit(IftypeCondition node);
	void endVisit(IsExp node);
	void endVisit(Import node);
	void endVisit(IndexExp node);
	void endVisit(InExp node);
	void endVisit(Initializer node);
	void endVisit(IntegerExp node);
	void endVisit(InterfaceDeclaration node);
	void endVisit(InvariantDeclaration node);
	void endVisit(LabelDsymbol node);
	void endVisit(LabelStatement node);
	void endVisit(LineInitExp node);
	void endVisit(LinkDeclaration node);
	void endVisit(MinAssignExp node);
	void endVisit(MinExp node);
	void endVisit(ModAssignExp node);
	void endVisit(ModExp node);
	void endVisit(Modifier node);
	void endVisit(Module node);
	void endVisit(ModuleDeclaration node);
	void endVisit(ModuleInfoDeclaration node);
	void endVisit(MulAssignExp node);
	void endVisit(MulExp node);
	void endVisit(NegExp node);
	void endVisit(NewAnonClassExp node);
	void endVisit(NewDeclaration node);
	void endVisit(NewExp node);
	void endVisit(NotExp node);
	void endVisit(NullExp node);
	void endVisit(OnScopeStatement node);
	void endVisit(OrAssignExp node);
	void endVisit(OrExp node);
	void endVisit(OrOrExp node);
	void endVisit(Package node);
	void endVisit(PostBlitDeclaration node);
	void endVisit(PostExp node);
	void endVisit(PragmaDeclaration node);
	void endVisit(PragmaStatement node);
	void endVisit(ProtDeclaration node);
	void endVisit(PtrExp node);
	void endVisit(RealExp node);
	void endVisit(RemoveExp node);
	void endVisit(ReturnStatement node);
	void endVisit(ScopeDsymbol node);
	void endVisit(ScopeExp node);
	void endVisit(ScopeStatement node);
	void endVisit(ShlAssignExp node);
	void endVisit(ShlExp node);
	void endVisit(ShrAssignExp node);
	void endVisit(ShrExp node);
	void endVisit(SliceExp node);
	void endVisit(Statement node);
	void endVisit(StaticAssert node);
	void endVisit(StaticAssertStatement node);
	void endVisit(StaticCtorDeclaration node);
	void endVisit(StaticDtorDeclaration node);
	void endVisit(StaticIfCondition node);
	void endVisit(StaticIfDeclaration node);
	void endVisit(StorageClassDeclaration node);
	void endVisit(StringExp node);
	void endVisit(StructDeclaration node);
	void endVisit(StructInitializer node);
	void endVisit(SuperExp node);
	void endVisit(SwitchStatement node);
	void endVisit(SymOffExp node);
	void endVisit(SynchronizedStatement node);
	void endVisit(TemplateAliasParameter node);
	void endVisit(TemplateDeclaration node);
	void endVisit(TemplateExp node);
	void endVisit(TemplateInstance node);
	void endVisit(TemplateInstanceWrapper node);
	void endVisit(TemplateMixin node);
	void endVisit(TemplateParameter node);
	void endVisit(TemplateThisParameter node);
	void endVisit(TemplateTupleParameter node);
	void endVisit(TemplateTypeParameter node);
	void endVisit(TemplateValueParameter node);
	void endVisit(ThisDeclaration node);
	void endVisit(ThisExp node);
	void endVisit(ThrowStatement node);
	void endVisit(TraitsExp node);
	void endVisit(TryCatchStatement node);
	void endVisit(TryFinallyStatement node);
	void endVisit(Tuple node);
	void endVisit(TupleDeclaration node);
	void endVisit(TupleExp node);
	void endVisit(Type node);
	void endVisit(TypeAArray node);
	void endVisit(TypeBasic node);
	void endVisit(TypeClass node);
	void endVisit(TypeDArray node);
	void endVisit(TypedefDeclaration node);
	void endVisit(TypeDelegate node);
	void endVisit(TypeEnum node);
	void endVisit(TypeExp node);
	void endVisit(TypeFunction node);
	void endVisit(TypeIdentifier node);
	void endVisit(TypeidExp node);
	void endVisit(TypeInfoArrayDeclaration node);
	void endVisit(TypeInfoAssociativeArrayDeclaration node);
	void endVisit(TypeInfoClassDeclaration node);
	void endVisit(TypeInfoDeclaration node);
	void endVisit(TypeInfoDelegateDeclaration node);
	void endVisit(TypeInfoEnumDeclaration node);
	void endVisit(TypeInfoFunctionDeclaration node);
	void endVisit(TypeInfoInterfaceDeclaration node);
	void endVisit(TypeInfoPointerDeclaration node);
	void endVisit(TypeInfoStaticArrayDeclaration node);
	void endVisit(TypeInfoStructDeclaration node);
	void endVisit(TypeInfoTypedefDeclaration node);
	void endVisit(TypeInstance node);
	void endVisit(TypePointer node);
	void endVisit(TypeQualified node);
	void endVisit(TypeReturn node);
	void endVisit(TypeSArray node);
	void endVisit(TypeSlice node);
	void endVisit(TypeStruct node);
	void endVisit(TypeTuple node);
	void endVisit(TypeTypedef node);
	void endVisit(TypeTypeof node);
	void endVisit(UAddExp node);
	void endVisit(UnaExp node);
	void endVisit(UnionDeclaration node);
	void endVisit(UnitTestDeclaration node);
	void endVisit(UnrolledLoopStatement node);
	void endVisit(UshrAssignExp node);
	void endVisit(UshrExp node);
	void endVisit(VarDeclaration node);
	void endVisit(VarExp node);
	void endVisit(Version node);
	void endVisit(VersionCondition node);
	void endVisit(VersionSymbol node);
	void endVisit(VoidInitializer node);
	void endVisit(VolatileStatement node);
	void endVisit(WhileStatement node);
	void endVisit(WithScopeSymbol node);
	void endVisit(WithStatement node);
	void endVisit(XorAssignExp node);
	void endVisit(XorExp node);

}