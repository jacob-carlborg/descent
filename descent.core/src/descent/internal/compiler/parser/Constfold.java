package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import static descent.internal.compiler.parser.ASTDmdNode.EXP_CANT_INTERPRET;
import static descent.internal.compiler.parser.TOK.TOKadd;
import static descent.internal.compiler.parser.TOK.TOKaddress;
import static descent.internal.compiler.parser.TOK.TOKarrayliteral;
import static descent.internal.compiler.parser.TOK.TOKassocarrayliteral;
import static descent.internal.compiler.parser.TOK.TOKequal;
import static descent.internal.compiler.parser.TOK.TOKidentity;
import static descent.internal.compiler.parser.TOK.TOKint64;
import static descent.internal.compiler.parser.TOK.TOKnotequal;
import static descent.internal.compiler.parser.TOK.TOKnotidentity;
import static descent.internal.compiler.parser.TOK.TOKstring;
import static descent.internal.compiler.parser.TOK.TOKstructliteral;
import static descent.internal.compiler.parser.TOK.TOKsymoff;

import static descent.internal.compiler.parser.TY.Tbool;
import static descent.internal.compiler.parser.TY.Tstruct;
import static descent.internal.compiler.parser.TY.Tvoid;

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
			Expression e;
			Loc loc = e1.loc;
			
			if(type.isreal())
			{
				e = new RealExp(loc,
						e1.toReal(context).add(e2.toReal(context)), type);
			}
			else if(type.isimaginary())
			{
				e = new RealExp(loc, e1.toImaginary(context).add(
						e2.toImaginary(context)), type);
			}
			else if(type.iscomplex())
			{
				/* TODO semantic
		    	// This rigamarole is necessary so that -0.0 doesn't get
		    	// converted to +0.0 by doing an extraneous add with +0.0
		    	complex_t c1;
		    	real_t r1;
		    	real_t i1;

		    	complex_t c2;
		    	real_t r2;
		    	real_t i2;

		    	complex_t v;
		    	int x;

		    	if (e1.type.isreal())
		    	{   r1 = e1.toReal();
		    	    x = 0;
		    	}
		    	else if (e1.type.isimaginary())
		    	{   i1 = e1.toImaginary();
		    	    x = 3;
		    	}
		    	else
		    	{   c1 = e1.toComplex();
		    	    x = 6;
		    	}

		    	if (e2.type.isreal())
		    	{   r2 = e2.toReal();
		    	}
		    	else if (e2.type.isimaginary())
		    	{   i2 = e2.toImaginary();
		    	    x += 1;
		    	}
		    	else
		    	{   c2 = e2.toComplex();
		    	    x += 2;
		    	}

		    	switch (x)
		    	{
		    	#if __DMC__
		    	    case 0+0:	v = (complex_t) (r1 + r2);	break;
		    	    case 0+1:	v = r1 + i2 * I;		break;
		    	    case 0+2:	v = r1 + c2;			break;
		    	    case 3+0:	v = i1 * I + r2;		break;
		    	    case 3+1:	v = (complex_t) ((i1 + i2) * I); break;
		    	    case 3+2:	v = i1 * I + c2;		break;
		    	    case 6+0:	v = c1 + r2;			break;
		    	    case 6+1:	v = c1 + i2 * I;		break;
		    	    case 6+2:	v = c1 + c2;			break;
		    	#else
		    	    case 0+0:	v = complex_t(r1 + r2, 0);	break;
		    	    case 0+1:	v = complex_t(r1, i2);		break;
		    	    case 0+2:	v = complex_t(r1 + creall(c2), cimagl(c2));	break;
		    	    case 3+0:	v = complex_t(r2, i1);		break;
		    	    case 3+1:	v = complex_t(0, i1 + i2);	break;
		    	    case 3+2:	v = complex_t(creall(c2), i1 + cimagl(c2));	break;
		    	    case 6+0:	v = complex_t(creall(c1) + r2, cimagl(c2));	break;
		    	    case 6+1:	v = complex_t(creall(c1), cimagl(c1) + i2);	break;
		    	    case 6+2:	v = c1 + c2;			break;
		    	#endif
		    	    default: assert(0);
		    	}
		    	e = new ComplexExp(loc, v, type);
		    	 */
				e = null;
			}
			else if(e1.op == TOKsymoff)
			{
				SymOffExp soe = (SymOffExp) e1;
				e = new SymOffExp(loc, soe.var, soe.offset.add(e2
						.toInteger(context)), context);
				e.type = type;
			}
			else if(e2.op == TOKsymoff)
			{
				SymOffExp soe = (SymOffExp) e2;
				e = new SymOffExp(loc, soe.var, soe.offset.add(e1
						.toInteger(context)), context);
				e.type = type;
			}
			else
			{
				e = new IntegerExp(loc, e1.toInteger(context).add(
						e2.toInteger(context)), type);
			}
			return e;
		}
	};
	
	public static final BinExp_fp Min = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			Expression e;
			Loc loc = e1.loc;
			
			if(type.isreal())
			{
				e = new RealExp(loc, e1.toReal(context).subtract(
						e2.toReal(context)), type);
			}
			else if(type.isimaginary())
			{
				e = new RealExp(loc, e1.toImaginary(context).subtract(
						e2.toImaginary(context)), type);
			}
			else if(type.iscomplex())
			{
				/* TODO semantic
				// This rigamarole is necessary so that -0.0 doesn't get
				// converted to +0.0 by doing an extraneous add with +0.0
				complex_t c1;
				real_t r1;
				real_t i1;
	
				complex_t c2;
				real_t r2;
				real_t i2;
	
				complex_t v;
				int x;
	
				if (e1.type.isreal())
				{   r1 = e1.toReal();
				    x = 0;
				}
				else if (e1.type.isimaginary())
				{   i1 = e1.toImaginary();
				    x = 3;
				}
				else
				{   c1 = e1.toComplex();
				    x = 6;
				}
	
				if (e2.type.isreal())
				{   r2 = e2.toReal();
				}
				else if (e2.type.isimaginary())
				{   i2 = e2.toImaginary();
				    x += 1;
				}
				else
				{   c2 = e2.toComplex();
				    x += 2;
				}
	
				switch (x)
				{
				#if __DMC__
				    case 0+0:	v = (complex_t) (r1 - r2);	break;
				    case 0+1:	v = r1 - i2  I;		break;
				    case 0+2:	v = r1 - c2;			break;
				    case 3+0:	v = i1  I - r2;		break;
				    case 3+1:	v = (complex_t) ((i1 - i2)  I); break;
				    case 3+2:	v = i1  I - c2;		break;
				    case 6+0:	v = c1 - r2;			break;
				    case 6+1:	v = c1 - i2  I;		break;
				    case 6+2:	v = c1 - c2;			break;
				#else
				    case 0+0:	v = complex_t(r1 - r2, 0);	break;
				    case 0+1:	v = complex_t(r1, -i2);		break;
				    case 0+2:	v = complex_t(r1 - creall(c2), -cimagl(c2));	break;
				    case 3+0:	v = complex_t(-r2, i1);		break;
				    case 3+1:	v = complex_t(0, i1 - i2);	break;
				    case 3+2:	v = complex_t(-creall(c2), i1 - cimagl(c2));	break;
				    case 6+0:	v = complex_t(creall(c1) - r2, cimagl(c1));	break;
				    case 6+1:	v = complex_t(creall(c1), cimagl(c1) - i2);	break;
				    case 6+2:	v = c1 - c2;			break;
				#endif
				    default: assert(0);
				}
				e = new ComplexExp(loc, v, type);
				*/
				e = null;
			}
			else if(e1.op == TOKsymoff)
			{
				SymOffExp soe = (SymOffExp) e1;
				e = new SymOffExp(loc, soe.var, soe.offset.subtract(e2
						.toInteger(context)), context);
				e.type = type;
			}
			else
			{
				e = new IntegerExp(loc, e1.toInteger(context).subtract(
						e2.toInteger(context)), type);
			}
			return e;
		}
	};
	
	public static final BinExp_fp Mul = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			Expression e;
			Loc loc = e1.loc;
			
			if(type.isfloating())
			{
				complex_t c;
				real_t r;
				
				if(e1.type.isreal())
				{
					r = e1.toReal(context);
					c = e2.toComplex(context);
					c = new complex_t(r.multiply(c.r), r.multiply(c.i));
				}
				else if(e1.type.isimaginary())
				{
					r = e1.toImaginary(context);
					c = e2.toComplex(context);
					c = new complex_t(r.negate().multiply(c.i), r.multiply(c.r));
				}
				else if(e2.type.isreal())
				{
					r = e2.toReal(context);
					c = e1.toComplex(context);
					c = new complex_t(r.multiply(c.r), r.multiply(c.i));
				}
				else if(e2.type.isimaginary())
				{
					r = e2.toImaginary(context);
					c = e1.toComplex(context);
					c = new complex_t(r.negate().multiply(c.i), r.multiply(c.r));
				}
				else
				{
					c = e1.toComplex(context).multiply(e2.toComplex(context));
				}
				
				if(type.isreal())
					e = new RealExp(loc, c.r, type);
				else if(type.isimaginary())
					e = new RealExp(loc, c.i, type);
				else if(type.iscomplex())
					e = new ComplexExp(loc, c, type);
				else
				{
					assert(false);
					e = null;
				}
			}
			else
			{
				e = new IntegerExp(loc, e1.toInteger(context).multiply
						(e2.toInteger(context)), type);
			}
			return e;
		}
	};
	
	public static final BinExp_fp Div = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			Expression e;
			Loc loc = e1.loc;
			
			if(type.isfloating())
			{
				complex_t c;
				real_t r;
				
				// e1.type.print();
				// e2.type.print();
				if(e2.type.isreal())
				{
					if(e1.type.isreal())
					{
						e = new RealExp(loc, e1.toReal(context).
								divide(e2.toReal(context)), type);
						return e;
					}
					r = e2.toReal(context);
					c = e1.toComplex(context);
					c = new complex_t(c.r.divide(r), c.i.divide(r));
				}
				else if(e2.type.isimaginary())
				{
					r = e2.toImaginary(context);
					c = e1.toComplex(context);
					c = new complex_t(c.i.divide(r), c.r.negate().divide(r));
				}
				else
				{
					c = e1.toComplex(context).divide(e2.toComplex(context));
				}
				
				if(type.isreal())
					e = new RealExp(loc, c.r, type);
				else if(type.isimaginary())
					e = new RealExp(loc, c.i, type);
				else if(type.iscomplex())
					e = new ComplexExp(loc, c, type);
				else
				{
					assert(false);
					e = null;
				}
			}
			else
			{
				integer_t n1;
				integer_t n2;
				integer_t n;
				
				n1 = e1.toInteger(context);
				n2 = e2.toInteger(context);
				if(n2.equals(0))
				{
					e2.error("divide by 0");
					e2 = new IntegerExp(Loc.ZERO, integer_t.ONE, e2.type);
					n2 = integer_t.ONE;
				}
				if(e1.type.isunsigned() || e2.type.isunsigned())
					n = /* TODO unsigned ((d_uns64) n1) / ((d_uns64) n2)*/ null;
				else
					n = n1.divide(n2);
				e = new IntegerExp(loc, n, type);
			}
			return e;
		}
	};
	
	public static final BinExp_fp Mod = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			Expression e;
			Loc loc = e1.loc;
			
			if(type.isfloating())
			{
				complex_t c;
				
				if(e2.type.isreal())
				{
					real_t r2 = e2.toReal(context);
					c = new complex_t(e1.toReal(context).remainder(r2),
							e1.toImaginary(context).remainder(r2));
				}
				else if(e2.type.isimaginary())
				{
					real_t i2 = e2.toImaginary(context);
					c = new complex_t(e1.toReal(context).remainder(i2),
							e1.toImaginary(context).remainder(i2));
				}
				else
				{
					assert (false);
					c = null;
				}
				
				if(type.isreal())
					e = new RealExp(loc, c.r, type);
				else if(type.isimaginary())
					e = new RealExp(loc, c.i, type);
				else if(type.iscomplex())
					e = new ComplexExp(loc, c, type);
				else
				{
					assert (false);
					e = null;
				}
			}
			else
			{
				integer_t n1;
				integer_t n2;
				integer_t n;
				
				n1 = e1.toInteger(context);
				n2 = e2.toInteger(context);
				if(n2.equals(0))
				{
					e2.error("divide by 0");
					e2 = new IntegerExp(Loc.ZERO, integer_t.ONE, e2.type);
					n2 = integer_t.ONE;
				}
				if(e1.type.isunsigned() || e2.type.isunsigned())
					n = null/* TODO unsigned ((d_uns64) n1) % ((d_uns64) n2) */;
				else
					n = n1.mod(n2);
				e = new IntegerExp(loc, n, type);
			}
			return e;
		}
	};
	
	public static final BinExp_fp Shl = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			Expression e;
		    Loc loc = e1.loc;

		    e = new IntegerExp(loc, e1.toInteger(context).
		    		shiftLeft(e2.toInteger(context)), type);
		    return e;
		}
	};
	
	public static final BinExp_fp Shr = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			Expression e;
		    Loc loc = e1.loc;
		    int count;
		    integer_t value;

		    value = e1.toInteger(context);
		    count = e2.toInteger(context).intValue();
		    switch (e1.type.toBasetype(context).ty)
		    {
			    /* TODO just calling bigInteger.shiftRight() method won't
			     * correctly truncate bits
				case Tint8:
					value = (d_int8)(value) >> count;
					break;
	
				case Tuns8:
					value = (d_uns8)(value) >> count;
					break;
	
				case Tint16:
					value = (d_int16)(value) >> count;
					break;
	
				case Tuns16:
					value = (d_uns16)(value) >> count;
					break;
	
				case Tint32:
					value = (d_int32)(value) >> count;
					break;
	
				case Tuns32:
					value = (d_uns32)(value) >> count;
					break;
	
				case Tint64:
					value = (d_int64)(value) >> count;
					break;
	
				case Tuns64:
					value = (d_uns64)(value) >> count;
					break;
				*/
				default:
					assert(false);
					value = null;
					break;
		    }
		    e = new IntegerExp(loc, value, type);
		    return e;
		}
	};
	
	public static final BinExp_fp Ushr = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			Expression e;
		    Loc loc = e1.loc;
		    int count;
		    integer_t value;

		    value = e1.toInteger(context);
		    count = e2.toInteger(context).intValue();
		    switch (e1.type.toBasetype(context).ty)
		    {
			    /* TODO just calling bigInteger.shiftRight() method won't
				 * correctly truncate bits
				case Tint8:
				case Tuns8:
					assert(0);		// no way to trigger this
					value = (value & 0xFF) >> count;
					break;
	
				case Tint16:
				case Tuns16:
					assert(0);		// no way to trigger this
					value = (value & 0xFFFF) >> count;
					break;
	
				case Tint32:
				case Tuns32:
					value = (value & 0xFFFFFFFF) >> count;
					break;
	
				case Tint64:
				case Tuns64:
					value = (d_uns64)(value) >> count;
					break;
				*/
				default:
					assert(false);
					value = null;
					break;
		    }
		    e = new IntegerExp(loc, value, type);
		    return e;
		}
	};
	
	public static final BinExp_fp And = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			Expression e;
		    Loc loc = e1.loc;

		    e = new IntegerExp(loc, e1.toInteger(context).
		    		and(e2.toInteger(context)), type);
		    return e;
		}
	};
	
	public static final BinExp_fp Or = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			Expression e;
		    Loc loc = e1.loc;

		    e = new IntegerExp(loc, e1.toInteger(context).
		    		or(e2.toInteger(context)), type);
		    return e;
		}
	};
	
	public static final BinExp_fp Xor = new BinExp_fp()
	{
		public Expression call(Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			Expression e;
		    Loc loc = e1.loc;

		    e = new IntegerExp(loc, e1.toInteger(context).
		    		xor(e2.toInteger(context)), type);
		    return e;
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
		public Expression call(TOK op, Type type, Expression e1, Expression e2,
				SemanticContext context);
	}
	
	public static final BinExp_fp2 Equal = new BinExp_fp2()
	{
		public Expression call(TOK op, Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			Expression e;
			Loc loc = e1.loc;
			int cmp;
			real_t r1;
			real_t r2;
			
			// printf("Equal(e1 = %s, e2 = %s)\n", e1.toChars(), e2.toChars());
			
			assert (op == TOKequal || op == TOKnotequal);
			
			if(e1.op == TOKstring && e2.op == TOKstring)
			{
				StringExp es1 = (StringExp) e1;
				StringExp es2 = (StringExp) e2;
				
				assert (es1.sz == es2.sz);
				if(es1.len == es2.len && es1.string.equals(es2.string))
					cmp = 1;
				else
					cmp = 0;
			}
			else if(e1.op == TOKarrayliteral && e2.op == TOKarrayliteral)
			{
				ArrayLiteralExp es1 = (ArrayLiteralExp) e1;
				ArrayLiteralExp es2 = (ArrayLiteralExp) e2;
				
				if((null == es1.elements || es1.elements.isEmpty()) &&
						(null == es2.elements || es2.elements.isEmpty()))
					cmp = 1; // both arrays are empty
				else if(null == es1.elements || es2.elements.isEmpty())
					cmp = 0;
				else if(es1.elements.size() != es2.elements.size())
					cmp = 0;
				else
				{
					cmp = 1;
					for(int i = 0; i < es1.elements.size(); i++)
					{
						Expression ee1 = (Expression) es1.elements.get(i);
						Expression ee2 = (Expression) es2.elements.get(i);
						
						Expression v = call(TOKequal, Type.tint32, ee1, ee2,
								context);
						if(v == EXP_CANT_INTERPRET)
							return EXP_CANT_INTERPRET;
						cmp = v.toInteger(context).intValue();
						if(cmp == 0)
							break;
					}
				}
			}
			else if(e1.op == TOKstructliteral && e2.op == TOKstructliteral)
			{
				StructLiteralExp es1 = (StructLiteralExp) e1;
				StructLiteralExp es2 = (StructLiteralExp) e2;
				
				if(es1.sd != es2.sd)
					cmp = 0;
				else if((null == es1.elements || es1.elements.isEmpty()) &&
						(null == es2.elements || es2.elements.isEmpty()))
					cmp = 1; // both arrays are empty
				else if(null == es1.elements || null == es2.elements)
					cmp = 0;
				else if(es1.elements.size() != es2.elements.size())
					cmp = 0;
				else
				{
					cmp = 1;
					for(int i = 0; i < es1.elements.size(); i++)
					{
						Expression ee1 = (Expression) es1.elements.get(i);
						Expression ee2 = (Expression) es2.elements.get(i);
						
						if(ee1 == ee2)
							continue;
						if(null == ee1 || null == ee2)
						{
							cmp = 0;
							break;
						}
						
						Expression v = call(TOKequal, Type.tint32, ee1, ee2,
								context);
						if(v == EXP_CANT_INTERPRET)
							return EXP_CANT_INTERPRET;
						cmp = v.toInteger(context).intValue();
						if(cmp == 0)
							break;
					}
				}
			}
			else if(!e1.isConst() || !e2.isConst())
			{
				return EXP_CANT_INTERPRET;
			}
			else if(e1.type.isreal() || e1.type.isimaginary())
			{
				if(e1.type.isreal())
				{
					r1 = e1.toReal(context);
					r2 = e2.toReal(context);
				}
				else
				{
					r1 = e1.toImaginary(context);
					r2 = e2.toImaginary(context);
				}
				
				if(r1.isnan() || r2.isnan()) // if unordered
				{
					cmp = 0;
				}
				else
				{
					cmp = r1.equals(r2) ? 1 : 0;
				}
			}
			else if(e1.type.iscomplex())
			{
				cmp = e1.toComplex(context).equals(e2.toComplex(context)) ? 1
						: 0;
			}
			else if(e1.type.isintegral())
			{
				cmp = e1.toInteger(context).equals(e2.toInteger(context)) ? 1
						: 0;
			}
			else
			{
				return EXP_CANT_INTERPRET;
			}
			if(op == TOKnotequal)
				cmp ^= 1;
			e = new IntegerExp(loc, cmp, type);
			return e;
		}
	};
	
	public static final BinExp_fp2 Cmp = new BinExp_fp2()
	{
		public Expression call(TOK op, Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			Expression e;
			Loc loc = e1.loc;
			int n = 0; // Just to keep Java from complaining, the default
			           // should never be used.
			real_t r1;
			real_t r2;
			
			if(e1.type.isreal() || e1.type.isimaginary())
			{
				if(e1.type.isreal())
				{
					r1 = e1.toReal(context);
					r2 = e2.toReal(context);
				}
				else
				{
					r1 = e1.toImaginary(context);
					r2 = e2.toImaginary(context);
				}
				// Don't rely on compiler, handle NAN arguments separately
				if(r1.isnan() || r2.isnan()) // if unordered
				{
					switch(op)
					{
						case TOKlt:
							n = 0;
							break;
						case TOKle:
							n = 0;
							break;
						case TOKgt:
							n = 0;
							break;
						case TOKge:
							n = 0;
							break;
						
						case TOKleg:
							n = 0;
							break;
						case TOKlg:
							n = 0;
							break;
						case TOKunord:
							n = 1;
							break;
						case TOKue:
							n = 1;
							break;
						case TOKug:
							n = 1;
							break;
						case TOKuge:
							n = 1;
							break;
						case TOKul:
							n = 1;
							break;
						case TOKule:
							n = 1;
							break;
						
						default:
							assert (false);
					}
				}
				else
				{
					switch(op)
					{
						case TOKlt:
							n = (r1.compareTo(r2) < 0) ? 1 : 0;
							break;
						case TOKle:
							n = (r1.compareTo(r2) <= 0) ? 1 : 0;
							break;
						case TOKgt:
							n = (r1.compareTo(r2) > 0) ? 1 : 0;
							break;
						case TOKge:
							n = (r1.compareTo(r2) >= 0) ? 1 : 0;
							break;
						
						case TOKleg:
							n = 1;
							break;
						case TOKlg:
							n = (r1.compareTo(r2) != 0) ? 1 : 0;
							break;
						case TOKunord:
							n = 0;
							break;
						case TOKue:
							n = (r1.compareTo(r2) == 0) ? 1 : 0;
							break;
						case TOKug:
							n = (r1.compareTo(r2) > 0) ? 1 : 0;
							break;
						case TOKuge:
							n = (r1.compareTo(r2) >= 0) ? 1 : 0;
							break;
						case TOKul:
							n = (r1.compareTo(r2) < 0) ? 1 : 0;
							break;
						case TOKule:
							n = (r1.compareTo(r2) <= 0) ? 1 : 0;
							break;
						
						default:
							assert (false);
					}
				}
			}
			else if(e1.type.iscomplex())
			{
				assert(false);
			}
			else
			{
				integer_t n1;
				integer_t n2;
				
				n1 = e1.toInteger(context);
				n2 = e2.toInteger(context);
				if(e1.type.isunsigned() || e2.type.isunsigned())
				{
					switch(op)
					{
						/* TODO unsigned comparison */
						default:
							assert(false);
					}
				}
				else
				{
					switch(op)
					{
						case TOKlt:
							n = (n1.compareTo(n2) < 0) ? 1 : 0;
							break;
						case TOKle:
							n = (n1.compareTo(n2) <= 0) ? 1 : 0;
							break;
						case TOKgt:
							n = (n1.compareTo(n2) > 0) ? 1 : 0;
							break;
						case TOKge:
							n = (n1.compareTo(n2) >= 0) ? 1 : 0;
							break;
						
						case TOKleg:
							n = 1;
							break;
						case TOKlg:
							n = (n1.compareTo(n2) != 0) ? 1 : 0;
							break;
						case TOKunord:
							n = 0;
							break;
						case TOKue:
							n = (n1.compareTo(n2) == 0) ? 1 : 0;
							break;
						case TOKug:
							n = (n1.compareTo(n2) > 0) ? 1 : 0;
							break;
						case TOKuge:
							n = (n1.compareTo(n2) >= 0) ? 1 : 0;
							break;
						case TOKul:
							n = (n1.compareTo(n2) < 0) ? 1 : 0;
							break;
						case TOKule:
							n = (n1.compareTo(n2) <= 0) ? 1 : 0;
							break;
						
						default:
							assert false;
					}
				}
			}
			e = new IntegerExp(loc, n, type);
			return e;
		}
	};
	
	public static final BinExp_fp2 Identity = new BinExp_fp2()
	{
		public Expression call(TOK op, Type type, Expression e1, Expression e2,
				SemanticContext context)
		{
			Loc loc = e1.loc;
			boolean cmp;
			
			if(e1.op == TOKsymoff && e2.op == TOKsymoff)
			{
				SymOffExp es1 = (SymOffExp) e1;
				SymOffExp es2 = (SymOffExp) e2;
				
				cmp = (es1.var == es2.var && es1.offset == es2.offset);
			}
			else if(e1.isConst() && e2.isConst())
			{
				return Equal.call((op == TOKidentity) ? TOKequal : TOKnotequal,
						type, e1, e2, context);
			}
			else
			{
				assert (false);
				cmp = false;
			}
			
			if(op == TOKnotidentity)
				cmp = !cmp;
			
			return new IntegerExp(loc, cmp ? 1 : 0, type);
		}
	};
	
	public static final UnaExp_fp Cast = new UnaExp_fp() {

		public Expression call(Type type, Expression e1, SemanticContext context) {
			return null;
		}
		
	};
	
	public final static Expression Cast(Type type, Type to, Expression e1,
			SemanticContext context) {
		Expression e = EXP_CANT_INTERPRET;
		Loc loc = e1.loc;

		if (type.equals(e1.type) && to.equals(type))
			return e1;

		if (!e1.isConst())
			return EXP_CANT_INTERPRET;

		Type tb = to.toBasetype(context);
		if (tb.ty == Tbool)
			e = new IntegerExp(loc, e1.toInteger(context).equals(0) ? 0 : 1,
					type);
		else if (type.isintegral()) {
			if (e1.type.isfloating()) {
				integer_t result;
				real_t r = e1.toReal(context);

				switch (type.toBasetype(context).ty) {
				case Tint8:
					result = NumberUtils.castToInt8(r);
					break;
				case Tchar:
				case Tuns8:
					result = NumberUtils.castToUns8(r);
					break;
				case Tint16:
					result = NumberUtils.castToInt16(r);
					break;
				case Twchar:
				case Tuns16:
					result = NumberUtils.castToUns16(r);
					break;
				case Tint32:
					result = NumberUtils.castToInt32(r);
					break;
				case Tdchar:
				case Tuns32:
					result = NumberUtils.castToUns32(r);
					break;
				case Tint64:
					result = NumberUtils.castToInt64(r);
					break;
				case Tuns64:
					result = NumberUtils.castToUns64(r);
					break;
				default:
					throw new IllegalStateException("assert(0);");
				}

				e = new IntegerExp(loc, result, type);
			} else if (type.isunsigned())
				e = new IntegerExp(loc, e1.toUInteger(context), type);
			else
				e = new IntegerExp(loc, e1.toInteger(context), type);
		} else if (tb.isreal()) {
			real_t value = e1.toReal(context);

			e = new RealExp(loc, value, type);
		} else if (tb.isimaginary()) {
			real_t value = e1.toImaginary(context);

			e = new RealExp(loc, value, type);
		} else if (tb.iscomplex()) {
			complex_t value = e1.toComplex(context);

			e = new ComplexExp(loc, value, type);
		} else if (tb.isscalar())
			e = new IntegerExp(loc, e1.toInteger(context), type);
		else if (tb.ty == Tvoid)
			e = EXP_CANT_INTERPRET;
		else if (tb.ty == Tstruct && e1.op == TOKint64) { // Struct = 0;
			StructDeclaration sd = tb.toDsymbol(null, context)
					.isStructDeclaration();
			if (sd == null) {
				throw new IllegalStateException("assert(sd);");
			}
			List<Expression> elements = new ArrayList<Expression>();
			for (int i = 0; i < sd.fields.size(); i++) {
				Dsymbol s = (Dsymbol) sd.fields.get(i);
				VarDeclaration v = s.isVarDeclaration();
				if (v == null) {
					throw new IllegalStateException("assert(v);");
				}

				Expression exp = new IntegerExp(0);
				exp = Cast(v.type, v.type, exp, context);
				if (exp == EXP_CANT_INTERPRET)
					return exp;
				elements.add(exp);
			}
			e = new StructLiteralExp(loc, sd, elements);
			e.type = type;
		} else {
			// TODO semantic uncomment below
			// error("cannot cast %s to %s", e1.type.toChars(context), type.toChars(context));
			e = new IntegerExp(loc, 0, type);
		}
		return e;
	}
	
	/*
	 * TODO semantic
	 * 
	 * *Slice(Type *type, Expression *e1, Expression *lwr, Expression *upr);
	 * 
	 * These functions may never be passed as a function pointer, in which case
	 * should they be interfaces to keep ortagonality or just functions?
	 */
	
}