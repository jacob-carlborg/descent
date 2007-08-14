package descent.internal.compiler.parser;

public class OrOrExp extends BinExp {

	public OrOrExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKoror, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return OR_OR_EXP;
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context)
	{
		e1 = e1.semantic(sc, context);
		e1 = resolveProperties(sc, e1, context);
		e1 = e1.checkToPointer(context);
		e1 = e1.checkToBoolean(context);
		int cs1 = sc.callSuper;
		
		if((sc.flags & Scope.SCOPEstaticif) > 0)
		{
			//If in static if, don't evaluate e2 if we don't have to.
			e1 = e1.optimize(WANTflags);
			if(e1.isBool(true))
				return new IntegerExp(loc, 1, Type.tboolean);
		}
		
		e2 = e2.semantic(sc, context);
		sc.mergeCallSuper(cs1);
		e2 = resolveProperties(sc, e2, context);
		e2 = e2.checkToPointer(context);
		
		type = Type.tboolean;
		if(e1.type.ty == TY.Tvoid)
			type = Type.tvoid;
		if(e2.op == TOK.TOKtype || e2.op == TOK.TOKimport)
			error(e2.toChars() + " is not an expression.");
		
		return this;
	}

}
