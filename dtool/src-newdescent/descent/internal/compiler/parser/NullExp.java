package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.MATCH.MATCHconvert;
import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.TY.Taarray;
import static descent.internal.compiler.parser.TY.Tarray;
import static descent.internal.compiler.parser.TY.Tclass;
import static descent.internal.compiler.parser.TY.Tdelegate;
import static descent.internal.compiler.parser.TY.Tpointer;
import static descent.internal.compiler.parser.TY.Ttypedef;
import static descent.internal.compiler.parser.TY.Tvoid;
import descent.core.domX.IASTVisitor;

public class NullExp extends Expression {
	
	public boolean committed;	// !=0 if type is committed
	
	public NullExp(Loc loc) {
		super(loc, TOK.TOKnull);
		this.committed = false;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		visitor.endVisit(this);
	}

	
	@Override
	public Expression castTo(Scope sc, Type t, SemanticContext context) {
		Expression e;
		Type tb;

		committed = true;
		e = this;
		tb = t.toBasetype(context);
		type = type.toBasetype(context);
		if (tb != type) {
			// NULL implicitly converts to any pointer type or dynamic array
			if (type.ty == Tpointer
					&& type.next.ty == Tvoid
					&& (tb.ty == Tpointer || tb.ty == Tarray
							|| tb.ty == Taarray || tb.ty == Tdelegate)) {
			} else {
				return super.castTo(sc, t, context);
			}
		}
		e.type = t;
		return e;
	}
	
	@Override
	public int getNodeType() {
		return NULL_EXP;
	}
	
	@Override
	public MATCH implicitConvTo(Type t, SemanticContext context) {
		if (this.type.equals(t)) {
			return MATCHexact;
		}
		// NULL implicitly converts to any pointer type or dynamic array
		if (type.ty == Tpointer && type.next.ty == Tvoid) {
			if (t.ty == Ttypedef) {
				t = ((TypeTypedef) t).sym.basetype;
			}
			if (t.ty == Tpointer || t.ty == Tarray || t.ty == Taarray
					|| t.ty == Tclass || t.ty == Tdelegate) {
				return committed ? MATCHconvert : MATCHexact;
			}
		}
		return super.implicitConvTo(t, context);
	}
	
	@Override
	public boolean isBool(boolean result) {
		return result ? false : true;
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		 // NULL is the same as (void *)0
	    if (type == null) {
			type = Type.tvoid.pointerTo(context);
		}
	    return this;
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.writestring("null");
	}
	
	@Override
	public void toMangleBuffer(OutBuffer buf, SemanticContext context) {
		buf.writeByte('n');
	}

}
