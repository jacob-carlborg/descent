package descent.internal.compiler.parser;

import java.util.List;
import static descent.internal.compiler.parser.MATCH.*;
import static descent.internal.compiler.parser.TY.*;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// TODO semantic
public class AssocArrayLiteralExp extends Expression {

	public List<Expression> keys;
	public List<Expression> values;

	public AssocArrayLiteralExp(Loc loc, List<Expression> keys,
			List<Expression> values) {
		super(loc, TOK.TOKassocarrayliteral);
		this.keys = keys;
		this.values = values;
	}

	@Override
	public int getNodeType() {
		return ASSOC_ARRAY_LITERAL_EXP;
	}

	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, keys);
			TreeVisitor.acceptChildren(visitor, values);
		}
		visitor.endVisit(this);
	}

	@Override
	public MATCH implicitConvTo(Type t, SemanticContext context) {
		MATCH result = MATCHexact;

		Type typeb = type.toBasetype(context);
		Type tb = t.toBasetype(context);
		if (tb.ty == Taarray && typeb.ty == Taarray) {
			for (int i = 0; i < keys.size(); i++) {
				Expression e = (Expression) keys.get(i);
				MATCH m = (MATCH) e.implicitConvTo(((TypeAArray) tb).key,
						context);
				if (m.ordinal() < result.ordinal())
					result = m; // remember worst match
				if (result == MATCHnomatch)
					break; // no need to check for worse
				e = (Expression) values.get(i);
				m = (MATCH) e.implicitConvTo(tb.next, context);
				if (m.ordinal() < result.ordinal())
					result = m; // remember worst match
				if (result == MATCHnomatch)
					break; // no need to check for worse
			}
			return result;
		} else
			return super.implicitConvTo(t, context);
	}

	@Override
	public Expression castTo(Scope sc, Type t, SemanticContext context) {
		Type typeb = type.toBasetype(context);
		Type tb = t.toBasetype(context);
		if (tb.ty == Taarray && typeb.ty == Taarray
				&& tb.next.toBasetype(context).ty != Tvoid) {
			assert (keys.size() == values.size());
			for (int i = 0; i < keys.size(); i++) {
				Expression e = (Expression) values.get(i);
				e = e.castTo(sc, tb.next, context);
				values.set(i, e);

				e = (Expression) keys.get(i);
				e = e.castTo(sc, ((TypeAArray) tb).key, context);
				keys.set(i, e);
			}
			type = t;
			return this;
		}
		// L1:
		return super.castTo(sc, t, context);
	}

}
