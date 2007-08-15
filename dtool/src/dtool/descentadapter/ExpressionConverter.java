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
import descent.internal.compiler.parser.BoolExp;
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
import descent.internal.compiler.parser.DotExp;
import descent.internal.compiler.parser.DotIdExp;
import descent.internal.compiler.parser.DotTemplateInstanceExp;
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
import descent.internal.compiler.parser.TupleExp;
import descent.internal.compiler.parser.TypeDotIdExp;
import descent.internal.compiler.parser.TypeidExp;
import descent.internal.compiler.parser.UAddExp;
import descent.internal.compiler.parser.UnaExp;
import descent.internal.compiler.parser.UshrAssignExp;
import descent.internal.compiler.parser.UshrExp;
import descent.internal.compiler.parser.VoidInitializer;
import descent.internal.compiler.parser.XorAssignExp;
import descent.internal.compiler.parser.XorExp;
import dtool.dom.expressions.ExpArrayIndex;
import dtool.dom.expressions.ExpArrayLiteral;
import dtool.dom.expressions.ExpAssert;
import dtool.dom.expressions.ExpCall;
import dtool.dom.expressions.ExpCast;
import dtool.dom.expressions.ExpCond;
import dtool.dom.expressions.ExpDelete;
import dtool.dom.expressions.ExpDollar;
import dtool.dom.expressions.ExpDotTemplateInstance;
import dtool.dom.expressions.ExpReference;
import dtool.dom.expressions.ExpIftype;
import dtool.dom.expressions.ExpLiteralBool;
import dtool.dom.expressions.ExpLiteralFunc;
import dtool.dom.expressions.ExpLiteralInteger;
import dtool.dom.expressions.ExpLiteralNewAnonClass;
import dtool.dom.expressions.ExpLiteralNull;
import dtool.dom.expressions.ExpLiteralReal;
import dtool.dom.expressions.ExpLiteralString;
import dtool.dom.expressions.ExpNew;
import dtool.dom.expressions.ExpParenthesized;
import dtool.dom.expressions.ExpSlice;
import dtool.dom.expressions.ExpSuper;
import dtool.dom.expressions.ExpThis;
import dtool.dom.expressions.ExpTypeid;
import dtool.dom.expressions.Expression;
import dtool.dom.expressions.InfixExpression;
import dtool.dom.expressions.InitializerArray;
import dtool.dom.expressions.InitializerExp;
import dtool.dom.expressions.InitializerStruct;
import dtool.dom.expressions.InitializerVoid;
import dtool.dom.expressions.PostfixExpression;
import dtool.dom.expressions.PrefixExpression;

abstract class ExpressionConverter extends DeclarationConverter {
	
	public boolean visit(DotExp node) {
		return assertFailFAKENODE();
	}

	public boolean visit(FileExp node) {
		Assert.failTODO();
		return false;
	}
	
	public boolean visit(MultiStringExp node) {
		Assert.failTODO();
		return false;
	}

	public boolean visit(TraitsExp node) {
		Assert.failTODO();
		return false;
	}
	
	public boolean visit(TupleExp node) {
		Assert.failTODO();
		return false;
	}
	
	public boolean visit(AssocArrayLiteralExp node) {
		Assert.failTODO();
		return false;
	}

	public boolean visit(CompileDeclaration node) {
		Assert.failTODO();
		return false;
	}

	public boolean visit(CompileExp node) {
		Assert.failTODO();
		return false;
	}

	public boolean visit(CompileStatement node) {
		Assert.failTODO();
		return false;
	}

	
	/* Initializers */
	
	public boolean visit(ArrayInitializer element) {
		return endAdapt(new InitializerArray(element));
	}

	public boolean visit(ExpInitializer element) {
		return endAdapt(new InitializerExp(element));
	}

	public boolean visit(StructInitializer element) {
		return endAdapt(new InitializerStruct(element));
	}

	public boolean visit(VoidInitializer element) {
		return endAdapt(new InitializerVoid(element));
	}

	
	/* ===================== Special ===================== */
	public boolean visit(ArrayExp element) {
		return endAdapt(new ExpArrayIndex(element));
	}
	
	public boolean visit(ArrayLiteralExp element) {
		return endAdapt(new ExpArrayLiteral(element));
	}
	
	public boolean visit(AssertExp element) {
		return endAdapt(new ExpAssert(element));
	}
	
	public boolean visit(CallExp element) {
		return endAdapt(new ExpCall(element));
	}
	
	public boolean visit(CastExp element) {
		return endAdapt(new ExpCast(element));
	}
	
	public boolean visit(CondExp element) {
		return endAdapt(new ExpCond(element));
	}
	
	public boolean visit(DeleteExp element) {
		return endAdapt(new ExpDelete(element));
	}
	
	public boolean visit(DollarExp element) {
		return endAdapt(new ExpDollar(element));
	}
	
	public boolean visit(DotIdExp element) {
		return endAdapt(new ExpReference(element));
	}
	
	public boolean visit(DotTemplateInstanceExp element) {
		return endAdapt(new ExpDotTemplateInstance(element));
	}
	
	public boolean visit(FuncExp element) {
		return endAdapt(new ExpLiteralFunc(element));
	}
	
	public boolean visit(IdentifierExp element) {
		return endAdapt(new ExpReference(element));
	}
	
	public boolean visit(IftypeExp element) {
		return endAdapt(new ExpIftype(element));
	}
	
	public boolean visit(IntegerExp element) {
		return endAdapt(new ExpLiteralInteger(element));
	}
	
	public boolean visit(NewAnonClassExp element) {
		return endAdapt(new ExpLiteralNewAnonClass(element));
	}
	
	public boolean visit(NewExp element) {
		return endAdapt(new ExpNew(element));
	}
	
	public boolean visit(NullExp element) {
		return endAdapt(new ExpLiteralNull(element));
	}
	
	final public boolean visit(ParenExp element) {
		return endAdapt(new ExpParenthesized(element));
	}
	
	public boolean visit(RealExp element) {
		return endAdapt(new ExpLiteralReal(element));
	}
	
	public boolean visit(ScopeExp element) {
		return endAdapt(new ExpReference(element));
	}
	
	public boolean visit(SliceExp element) {
		return endAdapt(new ExpSlice(element));
	}
	
	public boolean visit(StringExp element) {
		return endAdapt(new ExpLiteralString(element));
	}
	

	public boolean visit(SuperExp element) {
		return endAdapt(new ExpSuper(element));
	}
	
	public boolean visit(ThisExp element) {
		return endAdapt(new ExpThis(element));
	}
	
	public boolean visit(TypeDotIdExp element) {
		return endAdapt(new ExpReference(element));
	}	
	
	public boolean visit(TypeidExp element) {
		return endAdapt(new ExpTypeid(element));
	}	
	
	public boolean visit(BoolExp node) {
		return endAdapt(new ExpLiteralBool(node));
	}

	/* ===================== Unary ===================== */
	

	public boolean visit(UnaExp element) {
		return assertFailABSTRACT_NODE();
	}
	
	public boolean visit(AddrExp element) {
		return endAdapt(new PrefixExpression(element, PrefixExpression.Type.ADDRESS));
	}
	
	public boolean visit(ComExp element) {
		return endAdapt(new PrefixExpression(element, PrefixExpression.Type.INVERT));
	}	
	
	public boolean visit(NegExp element) {
		return endAdapt(new PrefixExpression(element, PrefixExpression.Type.NEGATIVE));
	}
	
	public boolean visit(NotExp element) {
		return endAdapt(new PrefixExpression(element, PrefixExpression.Type.NOT));
	}	

	
	public boolean visit(IndexExp node) {
		return assertFailFAKENODE();
	}

	public boolean visit(PostExp node) {
		return endAdapt(new PostfixExpression(node));
	}

	public boolean visit(PtrExp element) {
		return endAdapt(new PrefixExpression(element, PrefixExpression.Type.POINTER));
	}
	
	public boolean visit(UAddExp element) {
		return endAdapt(new PrefixExpression(element, PrefixExpression.Type.POSITIVE));
	}	

	/* ===================== Binary ===================== */
	
	public boolean visit(BinExp element) {
		Assert.fail("Error visited abstract class."); return false;
	}

	public boolean visit(AddAssignExp element) {
		Expression newelem = new InfixExpression(element, InfixExpression.Type.ADD_ASSIGN);
		return endAdapt(newelem);
	}
	
	public boolean visit(IncrementExp node) {
		Expression newelem = new PrefixExpression(node, PrefixExpression.Type.PRE_INCREMENT);
		return endAdapt(newelem);
	}

	

	public boolean visit(MinAssignExp element) {
		Expression newelem = new InfixExpression(element, InfixExpression.Type.MIN_ASSIGN);
		return endAdapt(newelem);
	}
	
	
	public boolean visit(DecrementExp node) {
		return endAdapt(new PrefixExpression(node, PrefixExpression.Type.PRE_DECREMENT));
	}
	
	public boolean visit(AddExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.ADD));
	}
	
	public boolean visit(AndAndExp element) {

		return endAdapt(new InfixExpression(element, InfixExpression.Type.AND_AND));
	}
	
	public boolean visit(AndAssignExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.AND_ASSIGN));
	}
	
	public boolean visit(AndExp element) {

		return endAdapt(new InfixExpression(element, InfixExpression.Type.AND));
	}
	
	public boolean visit(AssignExp element) {

		return endAdapt(new InfixExpression(element, InfixExpression.Type.ASSIGN));
	}
	
	public boolean visit(CatAssignExp element) {

		return endAdapt(new InfixExpression(element, InfixExpression.Type.CAT_ASSIGN));
	}
	
	public boolean visit(CatExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.CAT));
	}
	
	public boolean visit(CmpExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.CMP));
	}
	
	public boolean visit(CommaExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.COMMA));
	}
	
	public boolean visit(DivAssignExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.DIV_ASSIGN));
	}
	
	public boolean visit(DivExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.DIV));
	}
	
	public boolean visit(EqualExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.EQUAL));
	}
	
	public boolean visit(IdentityExp element) {
		if(element.op == TOK.TOKis)
			return endAdapt(new InfixExpression(element, InfixExpression.Type.IDENTITY));
		else if(element.op == TOK.TOKnotis)
			return endAdapt(new InfixExpression(element, InfixExpression.Type.NOT_IDENTITY));
		
		Assert.fail(); return false;
	}
	
	public boolean visit(InExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.IN));
	}
	
	public boolean visit(MinExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.MIN));
	}
	
	public boolean visit(ModAssignExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.MOD_ASSIGN));
	}
	
	public boolean visit(ModExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.MOD));
	}
	
	public boolean visit(MulAssignExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.MUL_ASSIGN));
	}
	
	public boolean visit(MulExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.MUL));
	}
	
	public boolean visit(OrAssignExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.OR_ASSIGN));
	}
	
	public boolean visit(OrExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.OR));
	}
	
	public boolean visit(OrOrExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.OR_OR));
	}
	
	public boolean visit(ShlAssignExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.SHIFT_LEFT_ASSIGN));
	}
	
	public boolean visit(ShlExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.SHIFT_LEFT));
	}
	
	public boolean visit(ShrAssignExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.SHIFT_RIGHT_ASSIGN));
	}
	
	public boolean visit(ShrExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.SHIFT_RIGHT));
	}
	
	public boolean visit(UshrAssignExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.UNSIGNED_SHIFT_RIGHT_ASSIGN));
	}
	
	public boolean visit(UshrExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.UNSIGNED_SHIFT_RIGHT));
	}
	
	public boolean visit(XorAssignExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.XOR_ASSIGN));
	}
	
	public boolean visit(XorExp element) {
		return endAdapt(new InfixExpression(element, InfixExpression.Type.XOR));
	}
}
