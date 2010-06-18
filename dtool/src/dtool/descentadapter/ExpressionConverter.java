package dtool.descentadapter;

import melnorme.miscutil.Assert;
import descent.internal.compiler.parser.AddAssignExp;
import descent.internal.compiler.parser.AddExp;
import descent.internal.compiler.parser.AddrExp;
import descent.internal.compiler.parser.AndAndExp;
import descent.internal.compiler.parser.AndAssignExp;
import descent.internal.compiler.parser.AndExp;
import descent.internal.compiler.parser.ArrayExp;
import descent.internal.compiler.parser.ArrayInitializer;
import descent.internal.compiler.parser.ArrayLiteralExp;
import descent.internal.compiler.parser.AssertExp;
import descent.internal.compiler.parser.AssignExp;
import descent.internal.compiler.parser.AssocArrayLiteralExp;
import descent.internal.compiler.parser.BinExp;
import descent.internal.compiler.parser.CallExp;
import descent.internal.compiler.parser.CastExp;
import descent.internal.compiler.parser.CatAssignExp;
import descent.internal.compiler.parser.CatExp;
import descent.internal.compiler.parser.CmpExp;
import descent.internal.compiler.parser.ComExp;
import descent.internal.compiler.parser.CommaExp;
import descent.internal.compiler.parser.CompileDeclaration;
import descent.internal.compiler.parser.CompileExp;
import descent.internal.compiler.parser.CompileStatement;
import descent.internal.compiler.parser.CondExp;
import descent.internal.compiler.parser.DecrementExp;
import descent.internal.compiler.parser.DeleteExp;
import descent.internal.compiler.parser.DivAssignExp;
import descent.internal.compiler.parser.DivExp;
import descent.internal.compiler.parser.DollarExp;
import descent.internal.compiler.parser.EqualExp;
import descent.internal.compiler.parser.ExpInitializer;
import descent.internal.compiler.parser.FileExp;
import descent.internal.compiler.parser.FuncExp;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.IdentityExp;
import descent.internal.compiler.parser.IftypeExp;
import descent.internal.compiler.parser.InExp;
import descent.internal.compiler.parser.IncrementExp;
import descent.internal.compiler.parser.IndexExp;
import descent.internal.compiler.parser.IntegerExp;
import descent.internal.compiler.parser.MinAssignExp;
import descent.internal.compiler.parser.MinExp;
import descent.internal.compiler.parser.ModAssignExp;
import descent.internal.compiler.parser.ModExp;
import descent.internal.compiler.parser.MulAssignExp;
import descent.internal.compiler.parser.MulExp;
import descent.internal.compiler.parser.MultiStringExp;
import descent.internal.compiler.parser.NegExp;
import descent.internal.compiler.parser.NewAnonClassExp;
import descent.internal.compiler.parser.NewExp;
import descent.internal.compiler.parser.NotExp;
import descent.internal.compiler.parser.NullExp;
import descent.internal.compiler.parser.OrAssignExp;
import descent.internal.compiler.parser.OrExp;
import descent.internal.compiler.parser.OrOrExp;
import descent.internal.compiler.parser.ParenExp;
import descent.internal.compiler.parser.PostExp;
import descent.internal.compiler.parser.PtrExp;
import descent.internal.compiler.parser.RealExp;
import descent.internal.compiler.parser.ScopeExp;
import descent.internal.compiler.parser.ShlAssignExp;
import descent.internal.compiler.parser.ShlExp;
import descent.internal.compiler.parser.ShrAssignExp;
import descent.internal.compiler.parser.ShrExp;
import descent.internal.compiler.parser.SliceExp;
import descent.internal.compiler.parser.StringExp;
import descent.internal.compiler.parser.StructInitializer;
import descent.internal.compiler.parser.SuperExp;
import descent.internal.compiler.parser.TOK;
import descent.internal.compiler.parser.ThisExp;
import descent.internal.compiler.parser.TraitsExp;
import descent.internal.compiler.parser.TypeDotIdExp;
import descent.internal.compiler.parser.TypeidExp;
import descent.internal.compiler.parser.UAddExp;
import descent.internal.compiler.parser.UnaExp;
import descent.internal.compiler.parser.UshrAssignExp;
import descent.internal.compiler.parser.UshrExp;
import descent.internal.compiler.parser.VoidInitializer;
import descent.internal.compiler.parser.XorAssignExp;
import descent.internal.compiler.parser.XorExp;
import dtool.ast.declarations.DeclarationStringMacro;
import dtool.ast.expressions.ExpArrayIndex;
import dtool.ast.expressions.ExpArrayLiteral;
import dtool.ast.expressions.ExpAssert;
import dtool.ast.expressions.ExpCall;
import dtool.ast.expressions.ExpCast;
import dtool.ast.expressions.ExpCond;
import dtool.ast.expressions.ExpDelete;
import dtool.ast.expressions.ExpDollar;
import dtool.ast.expressions.ExpIftype;
import dtool.ast.expressions.ExpLiteralFunc;
import dtool.ast.expressions.ExpLiteralImportedString;
import dtool.ast.expressions.ExpLiteralInteger;
import dtool.ast.expressions.ExpLiteralMapArray;
import dtool.ast.expressions.ExpLiteralNewAnonClass;
import dtool.ast.expressions.ExpLiteralNull;
import dtool.ast.expressions.ExpLiteralReal;
import dtool.ast.expressions.ExpLiteralString;
import dtool.ast.expressions.ExpNew;
import dtool.ast.expressions.ExpParenthesized;
import dtool.ast.expressions.ExpReference;
import dtool.ast.expressions.ExpSlice;
import dtool.ast.expressions.ExpStringMacro;
import dtool.ast.expressions.ExpSuper;
import dtool.ast.expressions.ExpThis;
import dtool.ast.expressions.ExpTraits;
import dtool.ast.expressions.ExpTypeid;
import dtool.ast.expressions.InfixExpression;
import dtool.ast.expressions.InitializerArray;
import dtool.ast.expressions.InitializerExp;
import dtool.ast.expressions.InitializerStruct;
import dtool.ast.expressions.InitializerVoid;
import dtool.ast.expressions.PostfixExpression;
import dtool.ast.expressions.PrefixExpression;
import dtool.ast.expressions.Resolvable;

abstract class ExpressionConverter extends DeclarationConverter {
	
	@Override
	public boolean visit(FileExp node) {
		return endAdapt(new ExpLiteralImportedString(node));
	}
	
	@Override
	public boolean visit(MultiStringExp node) {
		return endAdapt(new ExpLiteralString(node));		
	}

	@Override
	public boolean visit(TraitsExp node) {
		return endAdapt(new ExpTraits(node));
	}
	
	@Override
	public boolean visit(AssocArrayLiteralExp node) {
		return endAdapt(new ExpLiteralMapArray(node));
	}

	@Override
	public boolean visit(CompileDeclaration node) {
		return endAdapt(new DeclarationStringMacro(node));
	}

	@Override
	public boolean visit(CompileExp node) {
		return endAdapt(new ExpStringMacro(node));
	}

	@Override
	public boolean visit(CompileStatement node) {
		return endAdapt(new DeclarationStringMacro(node));
	}

	
	/* Initializers */
	
	@Override
	public boolean visit(ArrayInitializer element) {
		return endAdapt(new InitializerArray(element));
	}

	@Override
	public boolean visit(ExpInitializer element) {
		return endAdapt(new InitializerExp(element));
	}

	@Override
	public boolean visit(StructInitializer element) {
		return endAdapt(new InitializerStruct(element));
	}

	@Override
	public boolean visit(VoidInitializer element) {
		return endAdapt(new InitializerVoid(element));
	}

	
	/* ===================== Special ===================== */
	@Override
	public boolean visit(ArrayExp element) {
		return endAdapt(new ExpArrayIndex(element));
	}
	
	@Override
	public boolean visit(ArrayLiteralExp element) {
		return endAdapt(new ExpArrayLiteral(element));
	}
	
	@Override
	public boolean visit(AssertExp element) {
		return endAdapt(new ExpAssert(element));
	}
	
	@Override
	public boolean visit(CallExp element) {
		return endAdapt(new ExpCall(element));
	}
	
	@Override
	public boolean visit(CastExp element) {
		return endAdapt(new ExpCast(element));
	}
	
	@Override
	public boolean visit(CondExp element) {
		return endAdapt(new ExpCond(element));
	}
	
	@Override
	public boolean visit(DeleteExp element) {
		return endAdapt(new ExpDelete(element));
	}
	
	@Override
	public boolean visit(DollarExp element) {
		return endAdapt(new ExpDollar(element));
	}

	
	@Override
	public boolean visit(FuncExp element) {
		return endAdapt(new ExpLiteralFunc(element));
	}
	
	@Override
	public boolean visit(IdentifierExp element) {
		return endAdapt(new ExpReference(element));
	}
	
	@Override
	public boolean visit(IftypeExp element) {
		return endAdapt(new ExpIftype(element));
	}
	
	@Override
	public boolean visit(IntegerExp element) {
		return endAdapt(ExpLiteralInteger.convertIntegerExp(element));
	}
	
	@Override
	public boolean visit(NewAnonClassExp element) {
		return endAdapt(new ExpLiteralNewAnonClass(element));
	}
	
	@Override
	public boolean visit(NewExp element) {
		return endAdapt(new ExpNew(element));
	}
	
	@Override
	public boolean visit(NullExp element) {
		return endAdapt(new ExpLiteralNull(element));
	}
	
	final public boolean visit(ParenExp element) {
		return endAdapt(new ExpParenthesized(element));
	}
	
	@Override
	public boolean visit(RealExp element) {
		return endAdapt(new ExpLiteralReal(element));
	}
	
	@Override
	public boolean visit(ScopeExp element) {
		return endAdapt(new ExpReference(element));
	}
	
	@Override
	public boolean visit(SliceExp element) {
		return endAdapt(new ExpSlice(element));
	}
	
	@Override
	public boolean visit(StringExp element) {
		return endAdapt(new ExpLiteralString(element));
	}
	

	@Override
	public boolean visit(SuperExp element) {
		return endAdapt(new ExpSuper(element));
	}
	
	@Override
	public boolean visit(ThisExp element) {
		return endAdapt(new ExpThis(element));
	}
	
	@Override
	public boolean visit(TypeDotIdExp element) {
		return endAdapt(new ExpReference(element));
	}	
	
	@Override
	public boolean visit(TypeidExp element) {
		return endAdapt(new ExpTypeid(element));
	}	

	/* ===================== Unary ===================== */
	

	@Override
	public boolean visit(UnaExp element) {
		return assertFailABSTRACT_NODE();
	}
	
	@Override
	public boolean visit(AddrExp element) {
		return endAdapt(new PrefixExpression(element, PrefixExpression.Type.ADDRESS));
	}
	
	@Override
	public boolean visit(ComExp element) {
		return endAdapt(new PrefixExpression(element, PrefixExpression.Type.INVERT));
	}	
	
	@Override
	public boolean visit(NegExp element) {
		return endAdapt(new PrefixExpression(element, PrefixExpression.Type.NEGATIVE));
	}
	
	@Override
	public boolean visit(NotExp element) {
		return endAdapt(new PrefixExpression(element, PrefixExpression.Type.NOT));
	}	

	
	@Override
	public boolean visit(IndexExp node) {
		return assertFailFAKENODE();
	}

	@Override
	public boolean visit(PostExp node) {
		return endAdapt(new PostfixExpression(node));
	}

	@Override
	public boolean visit(PtrExp element) {
		return endAdapt(new PrefixExpression(element, PrefixExpression.Type.POINTER));
	}
	
	@Override
	public boolean visit(UAddExp element) {
		return endAdapt(new PrefixExpression(element, PrefixExpression.Type.POSITIVE));
	}	

	/* ===================== Binary ===================== */
	
	@Override
	public boolean visit(BinExp element) {
		Assert.fail("Error visited abstract class."); return false;
	}

	@Override
	public boolean visit(AddAssignExp element) {
		Resolvable newelem = new InfixExpression(element, InfixExpression.Type.ADD_ASSIGN);
		return endAdapt(newelem);
	}
	
	@Override
	public boolean visit(IncrementExp node) {
		Resolvable newelem = new PrefixExpression(node, PrefixExpression.Type.PRE_INCREMENT);
		return endAdapt(newelem);
	}

	

	@Override
	public boolean visit(MinAssignExp element) {
		Resolvable newelem = new InfixExpression(element, InfixExpression.Type.MIN_ASSIGN);
		return endAdapt(newelem);
	}
	
	
	@Override
	public boolean visit(DecrementExp node) {
		return endAdapt(new PrefixExpression(node, PrefixExpression.Type.PRE_DECREMENT));
	}
	
	@Override
	public boolean visit(AddExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.ADD));
	}
	
	@Override
	public boolean visit(AndAndExp element) {

		return endAdapt(new InfixExpression(element, InfixExpression.Type.AND_AND));
	}
	
	@Override
	public boolean visit(AndAssignExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.AND_ASSIGN));
	}
	
	@Override
	public boolean visit(AndExp element) {

		return endAdapt(new InfixExpression(element, InfixExpression.Type.AND));
	}
	
	@Override
	public boolean visit(AssignExp element) {

		return endAdapt(new InfixExpression(element, InfixExpression.Type.ASSIGN));
	}
	
	@Override
	public boolean visit(CatAssignExp element) {

		return endAdapt(new InfixExpression(element, InfixExpression.Type.CAT_ASSIGN));
	}
	
	@Override
	public boolean visit(CatExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.CAT));
	}
	
	@Override
	public boolean visit(CmpExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.CMP));
	}
	
	@Override
	public boolean visit(CommaExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.COMMA));
	}
	
	@Override
	public boolean visit(DivAssignExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.DIV_ASSIGN));
	}
	
	@Override
	public boolean visit(DivExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.DIV));
	}
	
	@Override
	public boolean visit(EqualExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.EQUAL));
	}
	
	@Override
	public boolean visit(IdentityExp element) {
		if(element.op == TOK.TOKis)
			return endAdapt(new InfixExpression(element, InfixExpression.Type.IDENTITY));
		else if(element.op == TOK.TOKnotis)
			return endAdapt(new InfixExpression(element, InfixExpression.Type.NOT_IDENTITY));
		
		Assert.fail(); return false;
	}
	
	@Override
	public boolean visit(InExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.IN));
	}
	
	@Override
	public boolean visit(MinExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.MIN));
	}
	
	@Override
	public boolean visit(ModAssignExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.MOD_ASSIGN));
	}
	
	@Override
	public boolean visit(ModExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.MOD));
	}
	
	@Override
	public boolean visit(MulAssignExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.MUL_ASSIGN));
	}
	
	@Override
	public boolean visit(MulExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.MUL));
	}
	
	@Override
	public boolean visit(OrAssignExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.OR_ASSIGN));
	}
	
	@Override
	public boolean visit(OrExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.OR));
	}
	
	@Override
	public boolean visit(OrOrExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.OR_OR));
	}
	
	@Override
	public boolean visit(ShlAssignExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.SHIFT_LEFT_ASSIGN));
	}
	
	@Override
	public boolean visit(ShlExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.SHIFT_LEFT));
	}
	
	@Override
	public boolean visit(ShrAssignExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.SHIFT_RIGHT_ASSIGN));
	}
	
	@Override
	public boolean visit(ShrExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.SHIFT_RIGHT));
	}
	
	@Override
	public boolean visit(UshrAssignExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.UNSIGNED_SHIFT_RIGHT_ASSIGN));
	}
	
	@Override
	public boolean visit(UshrExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.UNSIGNED_SHIFT_RIGHT));
	}
	
	@Override
	public boolean visit(XorAssignExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.XOR_ASSIGN));
	}
	
	@Override
	public boolean visit(XorExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.XOR));
	}
}
