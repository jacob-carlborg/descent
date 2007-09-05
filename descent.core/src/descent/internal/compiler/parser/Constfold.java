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
	
	/*
	 * TODO semantic
	 * 
	 * Expression *Cast(Type *type, Type *to, Expression *e1); Expression
	 *    (implemented in ASTDmdNode, should it be moved?)
	 * *Slice(Type *type, Expression *e1, Expression *lwr, Expression *upr);
	 * 
	 * These functions may never be passed as a function pointer, in which case
	 * should they be interfaces to keep ortagonality or just functions?
	 */
	
}