package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class AndAndExp extends BinExp {

	public AndAndExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKandand, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return AND_AND_EXP;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, e1);
			TreeVisitor.acceptChildren(visitor, e2);
		}
		visitor.endVisit(this);
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
			e1 = e1.optimize(WANTflags, context);
			if(e1.isBool(false))
				return new IntegerExp(loc, 0, Type.tboolean);
		}
		
		e2 = e2.semantic(sc, context);
		sc.mergeCallSuper(cs1);
		e2 = resolveProperties(sc, e2, context);
		e2 = e2.checkToPointer(context);
		
		type = Type.tboolean;
		if(e1.type.ty == TY.Tvoid)
			type = Type.tvoid;
		if(e2.op == TOK.TOKtype || e2.op == TOK.TOKimport) {
			error(e2.toChars() + " is not an expression.");
		}
		
		return this;
	}

	@Override
	public Expression checkToBoolean(SemanticContext context)
	{
		e2 = e2.checkToBoolean(context);
		return this;
	}

	@Override
	public boolean isBit()
	{
		return true;
	}
	
	public int checkSideEffect(int flag, SemanticContext context)
	{
		if(flag == 2)
		{
			int temp = e1.checkSideEffect(2, context);
			return temp != 0 ? temp : e2.checkSideEffect(2, context);
		}
		else
		{
			e1.checkSideEffect(1, context);
			return e2.checkSideEffect(flag, context);
		}
	}
}
