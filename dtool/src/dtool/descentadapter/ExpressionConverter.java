package dtool.descentadapter;

import util.Assert;
import descent.core.dom.IFalseExpression;
import descent.core.dom.IIntegerExpression;
import descent.core.dom.ITrueExpression;
import descent.internal.core.dom.AddAssignExp;
import descent.internal.core.dom.AddExp;
import descent.internal.core.dom.AddrExp;
import descent.internal.core.dom.AndAndExp;
import descent.internal.core.dom.AndAssignExp;
import descent.internal.core.dom.AndExp;
import descent.internal.core.dom.ArrayExp;
import descent.internal.core.dom.ArrayInitializer;
import descent.internal.core.dom.ArrayLiteralExp;
import descent.internal.core.dom.AssertExp;
import descent.internal.core.dom.AssignExp;
import descent.internal.core.dom.BinaryExpression;
import descent.internal.core.dom.CallExp;
import descent.internal.core.dom.CastExp;
import descent.internal.core.dom.CatAssignExp;
import descent.internal.core.dom.CatExp;
import descent.internal.core.dom.CmpExp;
import descent.internal.core.dom.ComExp;
import descent.internal.core.dom.CommaExp;
import descent.internal.core.dom.CondExp;
import descent.internal.core.dom.DeleteExp;
import descent.internal.core.dom.DivAssignExp;
import descent.internal.core.dom.DivExp;
import descent.internal.core.dom.DollarExp;
import descent.internal.core.dom.DotIdExp;
import descent.internal.core.dom.DotTemplateInstanceExp;
import descent.internal.core.dom.EqualExp;
import descent.internal.core.dom.ExpInitializer;
import descent.internal.core.dom.FuncExp;
import descent.internal.core.dom.IdentifierExp;
import descent.internal.core.dom.IdentityExp;
import descent.internal.core.dom.IftypeExp;
import descent.internal.core.dom.InExp;
import descent.internal.core.dom.IntegerExp;
import descent.internal.core.dom.MinAssignExp;
import descent.internal.core.dom.MinExp;
import descent.internal.core.dom.ModAssignExp;
import descent.internal.core.dom.ModExp;
import descent.internal.core.dom.MulAssignExp;
import descent.internal.core.dom.MulExp;
import descent.internal.core.dom.NegExp;
import descent.internal.core.dom.NewAnonClassExp;
import descent.internal.core.dom.NewExp;
import descent.internal.core.dom.NotExp;
import descent.internal.core.dom.NullExp;
import descent.internal.core.dom.OrAssignExp;
import descent.internal.core.dom.OrExp;
import descent.internal.core.dom.OrOrExp;
import descent.internal.core.dom.ParenthesizedExpression;
import descent.internal.core.dom.PostDecExp;
import descent.internal.core.dom.PostIncExp;
import descent.internal.core.dom.PtrExp;
import descent.internal.core.dom.RealExp;
import descent.internal.core.dom.ScopeExp;
import descent.internal.core.dom.ShlAssignExp;
import descent.internal.core.dom.ShlExp;
import descent.internal.core.dom.ShrAssignExp;
import descent.internal.core.dom.ShrExp;
import descent.internal.core.dom.SliceExp;
import descent.internal.core.dom.StringExp;
import descent.internal.core.dom.StructInitializer;
import descent.internal.core.dom.SuperExp;
import descent.internal.core.dom.ThisExp;
import descent.internal.core.dom.TypeDotIdExp;
import descent.internal.core.dom.TypeidExp;
import descent.internal.core.dom.UAddExp;
import descent.internal.core.dom.UnaryExpression;
import descent.internal.core.dom.UshrAssignExp;
import descent.internal.core.dom.UshrExp;
import descent.internal.core.dom.VoidInitializer;
import descent.internal.core.dom.XorAssignExp;
import descent.internal.core.dom.XorExp;
import descent.internal.core.dom.BinaryExpression.BinaryExpressionTypes;
import descent.internal.core.dom.UnaryExpression.IUnaryExpression2;
import dtool.dom.expressions.ExpArrayIndex;
import dtool.dom.expressions.ExpArrayLiteral;
import dtool.dom.expressions.ExpAssert;
import dtool.dom.expressions.ExpBoolLiteral;
import dtool.dom.expressions.ExpCall;
import dtool.dom.expressions.ExpCast;
import dtool.dom.expressions.ExpCond;
import dtool.dom.expressions.ExpDelete;
import dtool.dom.expressions.ExpDollar;
import dtool.dom.expressions.ExpDotEntityRef;
import dtool.dom.expressions.ExpDotTemplateInstance;
import dtool.dom.expressions.ExpEntityRef;
import dtool.dom.expressions.ExpIftype;
import dtool.dom.expressions.ExpLiteralFunc;
import dtool.dom.expressions.ExpLiteralInteger;
import dtool.dom.expressions.ExpLiteralNewAnonClass;
import dtool.dom.expressions.ExpLiteralNull;
import dtool.dom.expressions.ExpLiteralReal;
import dtool.dom.expressions.ExpLiteralString;
import dtool.dom.expressions.ExpNew;
import dtool.dom.expressions.ExpParenthesized;
import dtool.dom.expressions.ExpScope;
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
		// TODO Simplify
		return endAdapt(new ExpDotEntityRef(element));
	}
	
	public boolean visit(DotTemplateInstanceExp element) {
		return endAdapt(new ExpDotTemplateInstance(element));
	}
	
	public boolean visit(FuncExp element) {
		return endAdapt(new ExpLiteralFunc(element));
	}
	
	public boolean visit(IdentifierExp element) {
		return endAdapt(new ExpEntityRef(element));
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
	
	public boolean visit(ParenthesizedExpression element) {
		return endAdapt(new ExpParenthesized(element));
	}
	
	public boolean visit(RealExp element) {
		return endAdapt(new ExpLiteralReal(element));
	}
	
	public boolean visit(ScopeExp element) {
		return endAdapt(new ExpScope(element));
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
		return endAdapt(new ExpEntityRef(element));
	}	
	
	public boolean visit(TypeidExp element) {
		return endAdapt(new ExpTypeid(element));
	}	
	
	
	public boolean visit(ITrueExpression elem) {
		return endAdapt(new ExpBoolLiteral(elem));
	}

	public boolean visit(IFalseExpression elem) {
		return endAdapt(new ExpBoolLiteral(elem));
	}

	public boolean visit(IIntegerExpression elem) {
		return visit((IntegerExp) elem);
	}


	/* ===================== Unary ===================== */
	

	public boolean visit(UnaryExpression element) {
		switch(element.getUnaryExpressionType()) {
		
		case IUnaryExpression2.ADDRESS: return visit((AddrExp) element);
		case IUnaryExpression2.INVERT: return visit((ComExp) element);
		case IUnaryExpression2.NEGATIVE: return visit((NegExp) element);
		case IUnaryExpression2.NOT: return visit((NotExp) element);
		case IUnaryExpression2.POINTER: return visit((PtrExp) element);
		case IUnaryExpression2.POSITIVE: return visit((UAddExp) element);
		case IUnaryExpression2.POST_DECREMENT: return visit((PostDecExp) element);
		case IUnaryExpression2.POST_INCREMENT: return visit((PostIncExp) element);
		default:
			Assert.fail("Error visited abstract class."); return false;
		}
		
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
	
	public boolean visit(PostDecExp element) {
		return endAdapt(new PostfixExpression(element, PostfixExpression.Type.POST_DECREMENT));
	}
	
	public boolean visit(PostIncExp element) {
		return endAdapt(new PostfixExpression(element, PostfixExpression.Type.POST_INCREMENT));
	}	
	
	public boolean visit(PtrExp element) {
		return endAdapt(new PrefixExpression(element, PrefixExpression.Type.POINTER));
	}
	
	public boolean visit(UAddExp element) {
		return endAdapt(new PrefixExpression(element, PrefixExpression.Type.POSITIVE));
	}	

	/* ===================== Binary ===================== */
	
	public boolean visit(BinaryExpression element) {
		switch(element.getBinaryExpressionType()) {
		
		case BinaryExpressionTypes.ADD: return visit((AddExp) element);
		case BinaryExpressionTypes.ADD_ASSIGN: return visit((AddAssignExp) element);
		case BinaryExpressionTypes.AND: return visit((AndExp) element);
		case BinaryExpressionTypes.AND_AND: return visit((AndAndExp) element);
		case BinaryExpressionTypes.AND_ASSIGN: return visit((AndAssignExp) element);
		case BinaryExpressionTypes.ASSIGN: return visit((AssignExp) element);
		case BinaryExpressionTypes.CAT: return visit((CatExp) element);
		case BinaryExpressionTypes.CAT_ASSIGN: return visit((CatAssignExp) element);
		case BinaryExpressionTypes.CMP: return visit((CmpExp) element);
		case BinaryExpressionTypes.COMMA: return visit((CommaExp) element);
		case BinaryExpressionTypes.DIV: return visit((DivExp) element);
		case BinaryExpressionTypes.DIV_ASSIGN: return visit((DivAssignExp) element);
		case BinaryExpressionTypes.EQUAL: return visit((EqualExp) element);
		case BinaryExpressionTypes.IDENTITY: return visit((IdentityExp) element);
		case BinaryExpressionTypes.IN: return visit((InExp) element);
		case BinaryExpressionTypes.MIN: return visit((MinExp) element);
		case BinaryExpressionTypes.MIN_ASSIGN: return visit((MinAssignExp) element);
		case BinaryExpressionTypes.MOD: return visit((ModExp) element);
		case BinaryExpressionTypes.MOD_ASSIGN: return visit((ModAssignExp) element);
		case BinaryExpressionTypes.MUL: return visit((MulExp) element);
		case BinaryExpressionTypes.MUL_ASSIGN: return visit((MulAssignExp) element);
		case BinaryExpressionTypes.NOT_IDENTITY: return visit((IdentityExp) element);
		case BinaryExpressionTypes.OR: return visit((OrExp) element);
		case BinaryExpressionTypes.OR_ASSIGN: return visit((OrAssignExp) element);
		case BinaryExpressionTypes.OR_OR: return visit((OrOrExp) element);
		case BinaryExpressionTypes.SHIFT_LEFT: return visit((ShlExp) element);
		case BinaryExpressionTypes.SHIFT_LEFT_ASSIGN: return visit((ShlAssignExp) element);
		case BinaryExpressionTypes.SHIFT_RIGHT: return visit((ShrExp) element);
		case BinaryExpressionTypes.SHIFT_RIGHT_ASSIGN: return visit((ShrAssignExp) element);
		case BinaryExpressionTypes.UNSIGNED_SHIFT_RIGHT: return visit((UshrExp) element);
		case BinaryExpressionTypes.UNSIGNED_SHIFT_RIGHT_ASSIGN: return visit((UshrAssignExp) element);
		case BinaryExpressionTypes.XOR: return visit((XorExp) element);
		case BinaryExpressionTypes.XOR_ASSIGN: return visit((XorAssignExp) element);
		default:
			Assert.fail("Error visited abstract class."); return false;
		}
			
	}

	public boolean visit(AddAssignExp element) {
		Expression newelem;
		if(element.isUnary) {
			newelem = new PrefixExpression(element, PrefixExpression.Type.PRE_INCREMENT);
		} else {
			newelem = new InfixExpression(element, InfixExpression.Type.ADD_ASSIGN);
		}
		return endAdapt(newelem);
	}
	

	public boolean visit(MinAssignExp element) {
		Expression newelem;
		if(element.isUnary) {
			newelem = new PrefixExpression(element, PrefixExpression.Type.PRE_DECREMENT);
		} else {
			newelem = new InfixExpression(element, InfixExpression.Type.MIN_ASSIGN);
		}
		return endAdapt(newelem);
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
		if(element.getBinaryExpressionType() == BinaryExpressionTypes.IDENTITY)
			return endAdapt(new InfixExpression(element, InfixExpression.Type.IDENTITY));
		else if(element.getBinaryExpressionType() == BinaryExpressionTypes.NOT_IDENTITY)
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
