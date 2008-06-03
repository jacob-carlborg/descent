package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.TOK.TOKsymoff;
import static descent.internal.compiler.parser.TOK.TOKstructliteral;
import static descent.internal.compiler.parser.TOK.TOKint64;
import static descent.internal.compiler.parser.TOK.TOKaddress;
import static descent.internal.compiler.parser.TOK.TOKadd;
import static descent.internal.compiler.parser.Constfold.Ptr;


public class PtrExp extends UnaExp {

	public PtrExp(Loc loc, Expression e) {
		super(loc, TOK.TOKstar, e);
	}

	public PtrExp(Loc loc, Expression e, Type t) {
		super(loc, TOK.TOKstar, e);
		this.type = t;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceE1);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public int getNodeType() {
		return PTR_EXP;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context)
	{
		Expression e = EXP_CANT_INTERPRET;
		
		// Constant fold (&structliteral + offset)
		if(e1.op == TOKadd)
		{
			AddExp ae = (AddExp) e1;
			if(ae.e1.op == TOKaddress && ae.e2.op == TOKint64)
			{
				AddrExp ade = (AddrExp) ae.e1;
				Expression ex = ade.e1;
				ex = ex.interpret(istate, context);
				if(ex != EXP_CANT_INTERPRET)
				{
					if(ex.op == TOKstructliteral)
					{
						StructLiteralExp se = (StructLiteralExp) ex;
						int offset = ae.e2.toInteger(context).intValue();
						e = se.getField(type, offset, context);
						if(null == e)
							e = EXP_CANT_INTERPRET;
						return e;
					}
				}
			}
			e = Ptr.call(type, e1, context);
		}
		else if(e1.op == TOKsymoff)
		{
			SymOffExp soe = (SymOffExp) e1;
			VarDeclaration v = soe.var.isVarDeclaration();
			if(null != v)
			{
				Expression ev = getVarExp(loc, istate, v, context);
				if(ev != EXP_CANT_INTERPRET && ev.op == TOKstructliteral)
				{
					StructLiteralExp se = (StructLiteralExp) ev;
					e = se.getField(type, soe.offset.intValue(), context);
					if(null == e)
						e = EXP_CANT_INTERPRET;
				}
			}
		}
		return e;
	}
	
	@Override
	public Expression optimize(int result, SemanticContext context)
	{
		//printf("PtrExp.optimize(result = x%x) %s\n", result, toChars());
		e1 = e1.optimize(result, context);
		// Convert &ex to ex
		if(e1.op == TOKaddress)
		{
			Expression e;
			Expression ex;
			
			ex = ((AddrExp) e1).e1;
			if(type.equals(ex.type))
				e = ex;
			else
			{
				e = ex.copy();
				e.type = type;
			}
			return e;
		}
		// Constant fold (&structliteral + offset)
		if(e1.op == TOKadd)
		{
			Expression e;
			e = Ptr.call(type, e1, context);
			if(e != EXP_CANT_INTERPRET)
				return e;
		}
		
		if (context.isD2()) {
		    if (e1.op == TOKsymoff) {
				SymOffExp se = (SymOffExp) e1;
				VarDeclaration v = se.var.isVarDeclaration();
				Expression e = expandVar(result, v, context);
				if (e != null && e.op == TOKstructliteral) {
					StructLiteralExp sle = (StructLiteralExp) e;
					e = sle.getField(type, se.offset.intValue(), context);
					if (e != EXP_CANT_INTERPRET)
						return e;
				}
			}
		}
		
		return this;
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Type tb;

		super.semantic(sc, context);
		e1 = resolveProperties(sc, e1, context);
		if (type != null) {
			return this;
		}
		if (e1.type == null) {
			// printf("PtrExp.semantic('%s')\n", toChars());
		}
		tb = e1.type.toBasetype(context);
		switch (tb.ty) {
		case Tpointer:
			type = tb.next;
			if (type.isbit()) {
				Expression e;

				// Rewrite *p as p[0]
				e = new IndexExp(loc, e1, new IntegerExp(loc, 0));
				return e.semantic(sc, context);
			}
			break;

		case Tsarray:
		case Tarray:
			type = tb.next;
			e1 = e1.castTo(sc, type.pointerTo(context), context);
			break;

		default:
			if (context.acceptsProblems()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.CanOnlyDereferenceAPointer, this, new String[] { e1.type.toChars(context) }));
			}
			type = Type.tint32;
			break;
		}
		rvalue(context);
		return this;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writeByte('*');
	    expToCBuffer(buf, hgs, e1, op.precedence, context);
	}

	@Override
	public Expression toLvalue(Scope sc, Expression e, SemanticContext context) {
		return this;
	}

}
