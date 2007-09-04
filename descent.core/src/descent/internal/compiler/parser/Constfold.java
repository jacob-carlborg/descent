package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.TOK.*;
import static descent.internal.compiler.parser.ASTDmdNode.EXP_CANT_INTERPRET;

/**
 * A class to hold constant-folding functions used by the interpreter. The
 * functions that use these are in UnaExp and BinExp. In DMD, they are in the
 * file constfold.c. Arguably, these should be moved into their respective
 * classes so the code looks more like DMD's (or even inlined as anonymous
 * classes), but since they're all in one file in DMD, I put them all in one
 * file here.
 * 
 * @author Walter Bright, port by Robert Fraser & Ary Manzana
 */

// DMD 1.020
public class Constfold
{
	
	public static interface UnaExp_fp
	{
		public Expression call(Type type, Expression e1,
				SemanticContext context);
	}
	
	public static final UnaExp_fp Neg = new UnaExp_fp()
	{
		public Expression call(Type type, Expression e1, SemanticContext context)
		{
			Expression e;
			Loc loc = e1.loc;
			
			if(e1.type.isreal())
			{
				e = new RealExp(loc, e1.toReal(context).negate(), type);
			}
			else if(e1.type.isimaginary())
			{
				e = new RealExp(loc, e1.toImaginary(context).negate(), type);
			}
			else if(e1.type.iscomplex())
			{
				e = new ComplexExp(loc, e1.toComplex(context).negate(), type);
			}
			else
			{
				e = new IntegerExp(loc, e1.toInteger(context).negate(), type);
			}
			
			return e;
		}
	};
	
	public static final UnaExp_fp Com = new UnaExp_fp()
	{
		public Expression call(Type type, Expression e1,
				SemanticContext context)
		{
			Expression e;
		    Loc loc = e1.loc;

		    e = new IntegerExp(loc, e1.toInteger(context).complement(), type);
		    return e;
		}
	};
	
	public static final UnaExp_fp Not = new UnaExp_fp()
	{
		public Expression call(Type type, Expression e1, 
				SemanticContext context)
		{
			Expression e;
		    Loc loc = e1.loc;
		    
		    // And, for the crowd's amusement, we now have a triple-negative!
		    e = new IntegerExp(loc, e1.isBool(false) ? 1 : 0, type);
		    return e;
		}
	};
	
	public static final UnaExp_fp Ptr = new UnaExp_fp()
	{
		public Expression call(Type type, Expression e1, SemanticContext context)
		{
			// printf("Ptr(e1 = %s)\n", e1.toChars());
			if(e1.op == TOKadd)
			{
				AddExp ae = (AddExp) e1;
				if(ae.e1.op == TOKaddress && ae.e2.op == TOKint64)
				{
					AddrExp ade = (AddrExp) ae.e1;
					if(ade.e1.op == TOKstructliteral)
					{
						StructLiteralExp se = (StructLiteralExp) ade.e1;
						int offset = ae.e2.toInteger(context).intValue();
						Expression e = null /* TODO semantic se.getField(type, offset) */;
						if(null == e)
							e = EXP_CANT_INTERPRET;
						return e;
					}
				}
			}
			return EXP_CANT_INTERPRET;
		}
	};
	
	public static final UnaExp_fp Bool = new UnaExp_fp()
	{
		public Expression call(Type type, Expression e1, 
				SemanticContext context)
		{
			Expression e;
		    Loc loc = e1.loc;

		    e = new IntegerExp(loc, e1.isBool(true) ? 1 : 0, type);
		    return e;
		}
	};
	
	public static final UnaExp_fp ArrayLength = new UnaExp_fp()
	{
		public Expression call(Type type, Expression e1, SemanticContext context)
		{
			Expression e;
			Loc loc = e1.loc;
			
			if(e1.op == TOKstring)
			{
				StringExp es1 = (StringExp) e1;
				
				e = new IntegerExp(loc, es1.len, type);
			}
			else if(e1.op == TOKarrayliteral)
			{
				ArrayLiteralExp ale = (ArrayLiteralExp) e1;
				int dim;
				
				dim = null != ale.elements ? ale.elements.size() : 0;
				e = new IntegerExp(loc, dim, type);
			}
			else if(e1.op == TOKassocarrayliteral)
			{
				AssocArrayLiteralExp ale = (AssocArrayLiteralExp) e1;
				int dim = ale.keys.size();
				
				e = new IntegerExp(loc, dim, type);
			}
			else
				e = EXP_CANT_INTERPRET;
			return e;
		}
	};
	
	public static final UnaExp_fp expType = new UnaExp_fp()
	{
		public Expression call(Type type, Expression e, SemanticContext context)
		{
			if(!type.equals(e.type))
			{
				e = e.copy();
				e.type = type;
			}
			return e;
		}
	};
	
	//--------------------------------------------------------------------------
	public static interface BinExp_fp
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context);
	}
	
	public static final BinExp_fp Add = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			// TODO semantic
			return null;
		}
	};
	
	public static final BinExp_fp Min = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			// TODO semantic
			return null;
		}
	};
	
	public static final BinExp_fp Mul = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			// TODO semantic
			return null;
		}
	};
	
	public static final BinExp_fp Div = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			// TODO semantic
			return null;
		}
	};
	
	public static final BinExp_fp Mod = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			// TODO semantic
			return null;
		}
	};
	
	public static final BinExp_fp Shl = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			// TODO semantic
			return null;
		}
	};
	
	public static final BinExp_fp Shr = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			// TODO semantic
			return null;
		}
	};
	
	public static final BinExp_fp Ushr = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			// TODO semantic
			return null;
		}
	};
	
	public static final BinExp_fp And = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			// TODO semantic
			return null;
		}
	};
	
	public static final BinExp_fp Or = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			// TODO semantic
			return null;
		}
	};
	
	public static final BinExp_fp Xor = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			// TODO semantic
			return null;
		}
	};
	
	public static final BinExp_fp Index = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			// TODO semantic
			return null;
		}
	};
	
	public static final BinExp_fp Cat = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			// TODO semantic
			return null;
		}
	};
	
	//--------------------------------------------------------------------------
	public static interface BinExp_fp2
	{
		public Expression call(TOK tok, Type type, Expression e1, Expression e2,
				SemanticContext context);
	}
	
	public static final BinExp_fp2 Equal = new BinExp_fp2()
	{
		public Expression call(TOK tok, Type type, Expression e1,
				Expression e2, SemanticContext context)
		{
			// TODO semantic
			return null;
		}
	};
	
	public static final BinExp_fp2 Cmp = new BinExp_fp2()
	{
		public Expression call(TOK tok, Type type, Expression e1,
				Expression e2, SemanticContext context)
		{
			// TODO semantic
			return null;
		}
	};
	
	public static final BinExp_fp2 Identity = new BinExp_fp2()
	{
		public Expression call(TOK tok, Type type, Expression e1,
				Expression e2, SemanticContext context)
		{
			// TODO semantic
			return null;
		}
	};
	
	/*
	TODO semantic
	
	Expression *Cast(Type *type, Type *to, Expression *e1);
	Expression *Slice(Type *type, Expression *e1, Expression *lwr, Expression *upr);
	
	 * These functions may never be passed as a function pointer, in which case
	 * should they be interfaces to keep ortagonality or just functions?
	 */
	
}