package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class CatExp extends BinExp {

	public CatExp(Loc loc, Expression e1, Expression e2) {
		super(loc, TOK.TOKtilde, e1, e2);
	}
	
	@Override
	public int getNodeType() {
		return CAT_EXP;
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
		if(null != type)
			return this;
		
		Expression e;

		super.semanticp(sc, context);
		e = op_overload(sc);
		if(null != e)
		    return e;
		
		Type tb1 = e1.type.toBasetype(context);
		Type tb2 = e2.type.toBasetype(context);
	
		/* BUG: Should handle things like:
		 *	char c;
		 *	c ~ ' '
		 *	' ' ~ c;
		 */
		
		if ((tb1.ty == TY.Tsarray || tb1.ty == TY.Tarray) &&
		    e2.type.equals(tb1.next))
		{
		    type = tb1.next.arrayOf(context);
		    if(tb2.ty == TY.Tarray)
		    {
		    	// Make e2 into [e2]
		    	List<Expression> elements = new ArrayList<Expression>(1);
		    	elements.add(e2);
		    	e2 = new ArrayLiteralExp(e2.loc, elements);
		    	e2.type = type;
		    }
		    return this;
		}
		
		
		else if ((tb2.ty == TY.Tsarray || tb2.ty == TY.Tarray) &&
		    e1.type.equals(tb2.next))
		{
		    type = tb2.next.arrayOf(context);
		    if (tb1.ty == TY.Tarray)
		    {
		    	// Make e1 into [e1]
		    	List<Expression> elements = new ArrayList<Expression>(1);
		    	elements.add(e1);
				e1 = new ArrayLiteralExp(e1.loc, elements);
				e1.type = type;
		    }
		    return this;
		}
		
		typeCombine(sc, context);
	
		if (type.toBasetype(context).ty == TY.Tsarray)
		    type = type.toBasetype(context).next.arrayOf(context);
		
		if (e1.op == TOK.TOKstring && e2.op == TOK.TOKstring)
		{
		    e = optimize(WANTvalue);
		}
		else if (e1.type.equals(e2.type) &&
			(e1.type.toBasetype(context).ty == TY.Tarray ||
			 e1.type.toBasetype(context).ty == TY.Tsarray))
		{
		    e = this;
		}
		else
		{
		    //error("Can only concatenate arrays, not (%s ~ %s)",
		    //		e1.type.toChars(), e2.type.toChars());
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.CanOnlyConcatenateArrays,
					0,
					start,
					length,
					new String[] {
						e1.type.toChars(),
						e2.type.toChars(),
					}));
		    type = Type.tint32;
		    e = this;
		}
		e.type = e.type.semantic(loc, sc, context);
		return e;
	}
}
