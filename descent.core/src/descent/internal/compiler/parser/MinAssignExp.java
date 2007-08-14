package descent.internal.compiler.parser;

public class MinAssignExp extends BinExp {

	public MinAssignExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKminass, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return MIN_ASSIGN_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context)
	{
		Expression e;
		
		if(null != type)
			return this;

		super.semantic(sc, context);
		e2 = resolveProperties(sc, e2, context);

		e = op_overload(sc);
		if(null != e)
			return e;

		e1 = e1.modifiableLvalue(sc, null, context);
		e1.checkScalar(context);
		e1.checkNoBool(context);
		if (e1.type.ty == TY.Tpointer && e2.type.isintegral())
			e = scaleFactor(sc, context);
		else
		{
			type = e1.type;
			typeCombine(sc, context);
			e1.checkArithmetic(context);
			e2.checkArithmetic(context);
			if (type.isreal() || type.isimaginary())
			{
				assert(e2.type.isfloating());
				e2 = e2.castTo(sc, e1.type, context);
			}
			e = this;
		}
		return e;
	}

}
