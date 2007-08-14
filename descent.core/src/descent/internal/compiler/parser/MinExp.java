package descent.internal.compiler.parser;

import java.math.BigInteger;

public class MinExp extends BinExp {

	public MinExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKmin, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return MIN_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context)
	{
		Expression e;
	    Type t1;
	    Type t2;

	    if(null != type)
	    	return this;

	    super.semanticp(sc, context);

	    e = op_overload(sc);
	    if(null != e)
	    	return e;

	    e = this;
	    t1 = e1.type.toBasetype(context);
	    t2 = e2.type.toBasetype(context);
	    if (t1.ty == TY.Tpointer)
	    {
	    	if (t2.ty == TY.Tpointer)
	    	{
	    		BigInteger stride;
	    		Expression e_;

	    		typeCombine(sc, context);		// make sure pointer types are compatible
	    		type = Type.tptrdiff_t;
	    		stride = BigInteger.valueOf(0); /* NEXTOF t2.nextOf().size(); */
	    		e_ = new DivExp(loc, this, new IntegerExp(Loc.ZERO, stride, Type.tptrdiff_t));
	    		e_.type = Type.tptrdiff_t;
	    		return e_;
	    	}
	    	else if (t2.isintegral())
	    		e = scaleFactor(sc, context);
	    	else
	    	{
	    		error("incompatible types for -");
		    	return new IntegerExp(loc, 0);
	    	}
	    }
	    else if (t2.ty == TY.Tpointer)
	    {
	    	type = e2.type;
	    	error("can't subtract pointer from %s", e1.type.toChars());
	    	return new IntegerExp(loc, 0);
	    }
	    else
	    {
	    	typeCombine(sc, context);
	    	t1 = e1.type.toBasetype(context);
	    	t2 = e2.type.toBasetype(context);
	    	if ((t1.isreal() && t2.isimaginary()) ||
	    		(t1.isimaginary() && t2.isreal()))
	    	{
			    switch (type.ty)
			    {
					case Tfloat32:
					case Timaginary32:
					    type = Type.tcomplex32;
					    break;
		
					case Tfloat64:
					case Timaginary64:
					    type = Type.tcomplex64;
					    break;
		
					case Tfloat80:
					case Timaginary80:
					    type = Type.tcomplex80;
					    break;
		
					default:
					    assert(false);
			    }
	    	}
	    }
	    
	    return e;
	}
	
}
