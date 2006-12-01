package descent.internal.core.dom;

import java.lang.reflect.Method;

import descent.core.domX.AbstractElement;




/**
 * Visitor design pattern.
 * 
 */
public abstract class DElementVisitor {
	
	protected boolean checkASTtypes = false;
	protected boolean visitingAsSuper = false;
	
	public void preVisit(AbstractElement elem) {
		// Default implementation: do nothing
	}

	public void postVisit(AbstractElement elem) {
		// Default implementation: do nothing
	}


	public boolean visitAsSuperType(Object element, Class elemclass)  {
		Class elemsuper = elemclass.getSuperclass();
		Method method;
		try {
			method = this.getClass().getMethod("visit", new Class[]{elemsuper});
			visitingAsSuper = true;
			boolean result = (Boolean) method.invoke(this, element);
			visitingAsSuper = false;
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e); 
		} 
	}
	
	public static class UnknownASTElementException extends Exception {
		public UnknownASTElementException(AbstractElement element) {
			super("ASTVisitor: Unknown ASTElement type:"+element);
		}
	}
	
	/**
	 * Visits the element.
	 * @param element the element to visit
	 * @return true if children element should be visited
	 */

	/* -----------  Abstract Classes  ----------- */
	
	public boolean visit(AbstractElement element) {
		if(checkASTtypes && visitingAsSuper != true)
			throw new RuntimeException(new UnknownASTElementException(element));
		return true;
	}

	public boolean visit(Dsymbol element) {
		if(checkASTtypes && visitingAsSuper != true)
			throw new RuntimeException(new UnknownASTElementException(element));
		return visitAsSuperType(element, Dsymbol.class);
	}

	public boolean visit(Declaration element) {
		if(checkASTtypes && visitingAsSuper != true)
			throw new RuntimeException(new UnknownASTElementException(element));
		return visitAsSuperType(element, Declaration.class);
	}

	public boolean visit(Initializer element) {
		if(checkASTtypes && visitingAsSuper != true)
			throw new RuntimeException(new UnknownASTElementException(element));
		return visitAsSuperType(element, Initializer.class);
	}

	public boolean visit(TemplateParameter element) {
		if(checkASTtypes && visitingAsSuper != true)
			throw new RuntimeException(new UnknownASTElementException(element));
		return visitAsSuperType(element, TemplateParameter.class);
	}
	
	public boolean visit(AggregateDeclaration element) {
		if(checkASTtypes && visitingAsSuper != true)
			throw new RuntimeException(new UnknownASTElementException(element));
		return visitAsSuperType(element, AggregateDeclaration.class);
	}

	public boolean visit(Statement element) {
		if(checkASTtypes && visitingAsSuper != true)
			throw new RuntimeException(new UnknownASTElementException(element));
		return visitAsSuperType(element, Statement.class);
	}

	public boolean visit(Expression element) {
		if(checkASTtypes && visitingAsSuper != true)
			throw new RuntimeException(new UnknownASTElementException(element));
		return visitAsSuperType(element, Expression.class);
	}

	public boolean visit(BinaryExpression element) {
		if(checkASTtypes && visitingAsSuper != true)
			throw new RuntimeException(new UnknownASTElementException(element));
		return visitAsSuperType(element, BinaryExpression.class);
	}

	public boolean visit(UnaryExpression element) {
		if(checkASTtypes && visitingAsSuper != true)
			throw new RuntimeException(new UnknownASTElementException(element));
		return visitAsSuperType(element, UnaryExpression.class);
	}

	/* -----------  Concrete Classes  ----------- */

	public boolean visit(AlignDeclaration element) {
		return visitAsSuperType(element, AlignDeclaration.class);
	}

	public boolean visit(ConditionalDeclaration element) {
		return visitAsSuperType(element, ConditionalDeclaration.class);
	}

	public boolean visit(DebugSymbol element) {
		return visitAsSuperType(element, DebugSymbol.class);
	}

	public boolean visit(AliasDeclaration element) {
		return visitAsSuperType(element, AliasDeclaration.class);
	}

	public boolean visit(FuncDeclaration element) {
		return visitAsSuperType(element, FuncDeclaration.class);
	}

	public boolean visit(CtorDeclaration element) {
		return visitAsSuperType(element, CtorDeclaration.class);
	}

	public boolean visit(DeleteDeclaration element) {
		return visitAsSuperType(element, DeleteDeclaration.class);
	}

	public boolean visit(DtorDeclaration element) {
		return visitAsSuperType(element, DtorDeclaration.class);
	}

	public boolean visit(FuncLiteralDeclaration element) {
		return visitAsSuperType(element, FuncLiteralDeclaration.class);
	}

	public boolean visit(NewDeclaration element) {
		return visitAsSuperType(element, NewDeclaration.class);
	}

	public boolean visit(StaticCtorDeclaration element) {
		return visitAsSuperType(element, StaticCtorDeclaration.class);
	}

	public boolean visit(StaticDtorDeclaration element) {
		return visitAsSuperType(element, StaticDtorDeclaration.class);
	}

	public boolean visit(TypedefDeclaration element) {
		return visitAsSuperType(element, TypedefDeclaration.class);
	}

	public boolean visit(VarDeclaration element) {
		return visitAsSuperType(element, VarDeclaration.class);
	}

	public boolean visit(EnumDeclaration element) {
		return visitAsSuperType(element, EnumDeclaration.class);
	}

	public boolean visit(EnumMember element) {
		return visitAsSuperType(element, EnumMember.class);
	}

	public boolean visit(Import element) {
		return visitAsSuperType(element, Import.class);
	}

	public boolean visit(ArrayInitializer element) {
		return visitAsSuperType(element, ArrayInitializer.class);
	}

	public boolean visit(ExpInitializer element) {
		return visitAsSuperType(element, ExpInitializer.class);
	}

	public boolean visit(StructInitializer element) {
		return visitAsSuperType(element, StructInitializer.class);
	}

	public boolean visit(VoidInitializer element) {
		return visitAsSuperType(element, VoidInitializer.class);
	}

	public boolean visit(InvariantDeclaration element) {
		return visitAsSuperType(element, InvariantDeclaration.class);
	}

	public boolean visit(LinkDeclaration element) {
		return visitAsSuperType(element, LinkDeclaration.class);
	}

	public boolean visit(PragmaDeclaration element) {
		return visitAsSuperType(element, PragmaDeclaration.class);
	}

	public boolean visit(ProtDeclaration element) {
		return visitAsSuperType(element, ProtDeclaration.class);
	}

	public boolean visit(ScopeDsymbol element) {
		return visitAsSuperType(element, ScopeDsymbol.class);
	}

	public boolean visit(ClassDeclaration element) {
		return visitAsSuperType(element, ClassDeclaration.class);
	}

	public boolean visit(InterfaceDeclaration element) {
		return visitAsSuperType(element, InterfaceDeclaration.class);
	}

	public boolean visit(StructDeclaration element) {
		return visitAsSuperType(element, StructDeclaration.class);
	}

	public boolean visit(UnionDeclaration element) {
		return visitAsSuperType(element, UnionDeclaration.class);
	}

	public boolean visit(Module element) {
		return visitAsSuperType(element, Module.class);
	}

	public boolean visit(StaticAssert element) {
		return visitAsSuperType(element, StaticAssert.class);
	}

	public boolean visit(StaticIfDeclaration element) {
		return visitAsSuperType(element, StaticIfDeclaration.class);
	}

	public boolean visit(StorageClassDeclaration element) {
		return visitAsSuperType(element, StorageClassDeclaration.class);
	}

	public boolean visit(TemplateDeclaration element) {
		return visitAsSuperType(element, TemplateDeclaration.class);
	}

	public boolean visit(TemplateMixin element) {
		return visitAsSuperType(element, TemplateMixin.class);
	}

	public boolean visit(UnitTestDeclaration element) {
		return visitAsSuperType(element, UnitTestDeclaration.class);
	}

	public boolean visit(VersionSymbol element) {
		return visitAsSuperType(element, VersionSymbol.class);
	}

	public boolean visit(Identifier element) {
		return visitAsSuperType(element, Identifier.class);
	}

	public boolean visit(TemplateInstance element) {
		return visitAsSuperType(element,TemplateInstance .class);
	}

	public boolean visit(ImportDeclaration element) {
		return visitAsSuperType(element, ImportDeclaration.class);
	}

	public boolean visit(ModuleDeclaration element) {
		return visitAsSuperType(element, ModuleDeclaration.class);
	}

	public boolean visit(QualifiedName element) {
		return visitAsSuperType(element, QualifiedName.class);
	}

	public boolean visit(SelectiveImport element) {
		return visitAsSuperType(element, SelectiveImport.class);
	}

	public boolean visit(TypeSpecialization element) {
		return visitAsSuperType(element, TypeSpecialization.class);
	}

	public boolean visit(Type element) {
		return visitAsSuperType(element, Type.class);
	}

	public boolean visit(TypeArray element) {
		return visitAsSuperType(element, TypeArray.class);
	}

	public boolean visit(TypeAArray element) {
		return visitAsSuperType(element, TypeAArray.class);
	}

	public boolean visit(TypeDArray element) {
		return visitAsSuperType(element, TypeDArray.class);
	}

	public boolean visit(TypeSArray element) {
		return visitAsSuperType(element, TypeSArray.class);
	}

	public boolean visit(TypeBasic element) {
		return visitAsSuperType(element, TypeBasic.class);
	}

	public boolean visit(TypeDelegate element) {
		return visitAsSuperType(element, TypeDelegate.class);
	}

	public boolean visit(TypeFunction element) {
		return visitAsSuperType(element, TypeFunction.class);
	}

	public boolean visit(TypePointer element) {
		return visitAsSuperType(element, TypePointer.class);
	}

	public boolean visit(TypeQualified element) {
		return visitAsSuperType(element, TypeQualified.class);
	}

	public boolean visit(TypeIdentifier element) {
		return visitAsSuperType(element, TypeIdentifier.class);
	}

	public boolean visit(TypeInstance element) {
		return visitAsSuperType(element, TypeInstance.class);
	}

	public boolean visit(TypeTypeof element) {
		return visitAsSuperType(element, TypeTypeof.class);
	}

	public boolean visit(TypeSlice element) {
		return visitAsSuperType(element, TypeSlice.class);
	}

	public boolean visit(TypeStruct element) {
		return visitAsSuperType(element, TypeStruct.class);
	}

	public boolean visit(TemplateAliasParameter element) {
		return visitAsSuperType(element, TemplateAliasParameter.class);
	}

	public boolean visit(TemplateTupleParameter element) {
		return visitAsSuperType(element, TemplateTupleParameter.class);
	}

	public boolean visit(TemplateTypeParameter element) {
		return visitAsSuperType(element, TemplateTypeParameter.class);
	}

	public boolean visit(TemplateValueParameter element) {
		return visitAsSuperType(element, TemplateValueParameter.class);
	}

	public boolean visit(AsmStatement element) {
		return visitAsSuperType(element, AsmStatement.class);
	}

	public boolean visit(BreakStatement element) {
		return visitAsSuperType(element, BreakStatement.class);
	}

	public boolean visit(CaseStatement element) {
		return visitAsSuperType(element, CaseStatement.class);
	}

	public boolean visit(CompoundStatement element) {
		return visitAsSuperType(element, CompoundStatement.class);
	}

	public boolean visit(ConditionalStatement element) {
		return visitAsSuperType(element, ConditionalStatement.class);
	}

	public boolean visit(ContinueStatement element) {
		return visitAsSuperType(element, ContinueStatement.class);
	}

	public boolean visit(DeclarationStatement element) {
		return visitAsSuperType(element, DeclarationStatement.class);
	}

	public boolean visit(DefaultStatement element) {
		return visitAsSuperType(element, DefaultStatement.class);
	}

	public boolean visit(DoStatement element) {
		return visitAsSuperType(element, DoStatement.class);
	}

	public boolean visit(ExpStatement element) {
		return visitAsSuperType(element, ExpStatement.class);
	}

	public boolean visit(ForeachStatement element) {
		return visitAsSuperType(element, ForeachStatement.class);
	}

	public boolean visit(ForStatement element) {
		return visitAsSuperType(element, ForStatement.class);
	}

	public boolean visit(GotoCaseStatement element) {
		return visitAsSuperType(element, GotoCaseStatement.class);
	}

	public boolean visit(GotoDefaultStatement element) {
		return visitAsSuperType(element, GotoDefaultStatement.class);
	}

	public boolean visit(GotoStatement element) {
		return visitAsSuperType(element, GotoStatement.class);
	}

	public boolean visit(IfStatement element) {
		return visitAsSuperType(element, IfStatement.class);
	}

	public boolean visit(LabelStatement element) {
		return visitAsSuperType(element, LabelStatement.class);
	}

	public boolean visit(OnScopeStatement element) {
		return visitAsSuperType(element, OnScopeStatement.class);
	}

	public boolean visit(PragmaStatement element) {
		return visitAsSuperType(element, PragmaStatement.class);
	}

	public boolean visit(ReturnStatement element) {
		return visitAsSuperType(element, ReturnStatement.class);
	}

	public boolean visit(ScopeStatement element) {
		return visitAsSuperType(element, ScopeStatement.class);
	}

	public boolean visit(StaticAssertStatement element) {
		return visitAsSuperType(element, StaticAssertStatement.class);
	}

	public boolean visit(SwitchStatement element) {
		return visitAsSuperType(element, SwitchStatement.class);
	}

	public boolean visit(SynchronizedStatement element) {
		return visitAsSuperType(element, SynchronizedStatement.class);
	}

	public boolean visit(ThrowStatement element) {
		return visitAsSuperType(element, ThrowStatement.class);
	}

	public boolean visit(TryCatchStatement element) {
		return visitAsSuperType(element, TryCatchStatement.class);
	}

	public boolean visit(TryFinallyStatement element) {
		return visitAsSuperType(element, TryFinallyStatement.class);
	}

	public boolean visit(VolatileStatement element) {
		return visitAsSuperType(element, VolatileStatement.class);
	}

	public boolean visit(WhileStatement element) {
		return visitAsSuperType(element, WhileStatement.class);
	}

	public boolean visit(WithStatement element) {
		return visitAsSuperType(element, WithStatement.class);
	}

	public boolean visit(ArrayExp element) {
		return visitAsSuperType(element, ArrayExp.class);
	}

	public boolean visit(ArrayLiteralExp element) {
		return visitAsSuperType(element, ArrayLiteralExp.class);
	}

	public boolean visit(AssertExp element) {
		return visitAsSuperType(element, AssertExp.class);
	}

	public boolean visit(CallExp element) {
		return visitAsSuperType(element, CallExp.class);
	}

	public boolean visit(CastExp element) {
		return visitAsSuperType(element, CastExp.class);
	}

	public boolean visit(CondExp element) {
		return visitAsSuperType(element, CondExp.class);
	}

	public boolean visit(DeleteExp element) {
		return visitAsSuperType(element, DeleteExp.class);
	}

	public boolean visit(DollarExp element) {
		return visitAsSuperType(element, DollarExp.class);
	}

	public boolean visit(DotIdExp element) {
		return visitAsSuperType(element, DotIdExp.class);
	}

	public boolean visit(DotTemplateInstanceExp element) {
		return visitAsSuperType(element, DotTemplateInstanceExp.class);
	}

	public boolean visit(FuncExp element) {
		return visitAsSuperType(element, FuncExp.class);
	}

	public boolean visit(IdentifierExp element) {
		return visitAsSuperType(element, IdentifierExp.class);
	}

	public boolean visit(IftypeExp element) {
		return visitAsSuperType(element, IftypeExp.class);
	}

	public boolean visit(IntegerExp element) {
		return visitAsSuperType(element, IntegerExp.class);
	}

	public boolean visit(NewAnonClassExp element) {
		return visitAsSuperType(element, NewAnonClassExp.class);
	}

	public boolean visit(NewExp element) {
		return visitAsSuperType(element, NewExp.class);
	}

	public boolean visit(NullExp element) {
		return visitAsSuperType(element, NullExp.class);
	}

	public boolean visit(ParenthesizedExpression element) {
		return visitAsSuperType(element, ParenthesizedExpression.class);
	}

	public boolean visit(RealExp element) {
		return visitAsSuperType(element, RealExp.class);
	}

	public boolean visit(ScopeExp element) {
		return visitAsSuperType(element, ScopeExp.class);
	}

	public boolean visit(SliceExp element) {
		return visitAsSuperType(element, SliceExp.class);
	}

	public boolean visit(StringExp element) {
		return visitAsSuperType(element, StringExp.class);
	}

	public boolean visit(SuperExp element) {
		return visitAsSuperType(element, SuperExp.class);
	}

	public boolean visit(ThisExp element) {
		return visitAsSuperType(element, ThisExp.class);
	}

	public boolean visit(TypeDotIdExp element) {
		return visitAsSuperType(element, TypeDotIdExp.class);
	}

	public boolean visit(TypeExp element) {
		return visitAsSuperType(element, TypeExp.class);
	}

	public boolean visit(TypeidExp element) {
		return visitAsSuperType(element, TypeidExp.class);
	}

	public boolean visit(AddAssignExp element) {
		return visitAsSuperType(element, AddAssignExp.class);
	}

	public boolean visit(AddExp element) {
		return visitAsSuperType(element, AddExp.class);
	}

	public boolean visit(AndAndExp element) {
		return visitAsSuperType(element, AndAndExp.class);
	}

	public boolean visit(AndAssignExp element) {
		return visitAsSuperType(element, AndAssignExp.class);
	}

	public boolean visit(AndExp element) {
		return visitAsSuperType(element, AndExp.class);
	}

	public boolean visit(AssignExp element) {
		return visitAsSuperType(element, AssignExp.class);
	}

	public boolean visit(CatAssignExp element) {
		return visitAsSuperType(element, CatAssignExp.class);
	}

	public boolean visit(CatExp element) {
		return visitAsSuperType(element, CatExp.class);
	}

	public boolean visit(CmpExp element) {
		return visitAsSuperType(element, CmpExp.class);
	}

	public boolean visit(CommaExp element) {
		return visitAsSuperType(element, CommaExp.class);
	}

	public boolean visit(DivAssignExp element) {
		return visitAsSuperType(element, DivAssignExp.class);
	}

	public boolean visit(DivExp element) {
		return visitAsSuperType(element, DivExp.class);
	}

	public boolean visit(EqualExp element) {
		return visitAsSuperType(element, EqualExp.class);
	}

	public boolean visit(IdentityExp element) {
		return visitAsSuperType(element, IdentityExp.class);
	}

	public boolean visit(InExp element) {
		return visitAsSuperType(element, InExp.class);
	}

	public boolean visit(MinAssignExp element) {
		return visitAsSuperType(element, MinAssignExp.class);
	}

	public boolean visit(MinExp element) {
		return visitAsSuperType(element, MinExp.class);
	}

	public boolean visit(ModAssignExp element) {
		return visitAsSuperType(element, ModAssignExp.class);
	}

	public boolean visit(ModExp element) {
		return visitAsSuperType(element, ModExp.class);
	}

	public boolean visit(MulAssignExp element) {
		return visitAsSuperType(element, MulAssignExp.class);
	}

	public boolean visit(MulExp element) {
		return visitAsSuperType(element, MulExp.class);
	}

	public boolean visit(OrAssignExp element) {
		return visitAsSuperType(element, OrAssignExp.class);
	}

	public boolean visit(OrExp element) {
		return visitAsSuperType(element, OrExp.class);
	}

	public boolean visit(OrOrExp element) {
		return visitAsSuperType(element, OrOrExp.class);
	}

	public boolean visit(ShlAssignExp element) {
		return visitAsSuperType(element, ShlAssignExp.class);
	}

	public boolean visit(ShlExp element) {
		return visitAsSuperType(element, ShlExp.class);
	}

	public boolean visit(ShrAssignExp element) {
		return visitAsSuperType(element, ShrAssignExp.class);
	}

	public boolean visit(ShrExp element) {
		return visitAsSuperType(element, ShrExp.class);
	}

	public boolean visit(UshrAssignExp element) {
		return visitAsSuperType(element, UshrAssignExp.class);
	}

	public boolean visit(UshrExp element) {
		return visitAsSuperType(element, UshrExp.class);
	}

	public boolean visit(XorAssignExp element) {
		return visitAsSuperType(element, XorAssignExp.class);
	}

	public boolean visit(XorExp element) {
		return visitAsSuperType(element, XorExp.class);
	}

	public boolean visit(AddrExp element) {
		return visitAsSuperType(element, AddrExp.class);
	}

	public boolean visit(ComExp element) {
		return visitAsSuperType(element, ComExp.class);
	}

	public boolean visit(NegExp element) {
		return visitAsSuperType(element, NegExp.class);
	}

	public boolean visit(NotExp element) {
		return visitAsSuperType(element, NotExp.class);
	}

	public boolean visit(PostDecExp element) {
		return visitAsSuperType(element, PostDecExp.class);
	}

	public boolean visit(PostIncExp element) {
		return visitAsSuperType(element, PostIncExp.class);
	}

	public boolean visit(PtrExp element) {
		return visitAsSuperType(element, PtrExp.class);
	}

	public boolean visit(UAddExp element) {
		return visitAsSuperType(element, UAddExp.class);
	}

	
	/**
	 * Ends the visit to the element.
	 * @param element the element to visit
	 */
	public abstract void endVisit(AbstractElement element);

}
