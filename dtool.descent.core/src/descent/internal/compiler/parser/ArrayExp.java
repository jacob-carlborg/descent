package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class ArrayExp extends UnaExp {

	public List<Expression> arguments;

	public ArrayExp(Loc loc, Expression e, List<Expression> arguments) {
		super(loc, TOK.TOKarray, e);
		this.arguments = arguments;
	}

	@Override
	public int getNodeType() {
		return ARRAY_EXP;
	}

	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, e1);
		}
		visitor.endVisit(this);
	}

	@Override
	public Expression syntaxCopy() {
		return new ArrayExp(loc, e1.syntaxCopy(), arraySyntaxCopy(arguments));
	}

	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		Expression e;
		Type t1;

		super.semantic(sc, context);
		e1 = resolveProperties(sc, e1, context);

		t1 = e1.type.toBasetype(context);
		if (t1.ty != TY.Tclass && t1.ty != TY.Tstruct) {
			// Convert to IndexExp
			if (arguments.size() != 1)
				error("only one index allowed to index " + t1.toChars(context));
			e = new IndexExp(loc, e1, arguments.get(0));
			return e.semantic(sc, context);
		}

		// Run semantic() on each argument
		for (int i = 0; i < arguments.size(); i++) {
			e = arguments.get(i);
			e = e.semantic(sc, context);
			if (null == e.type)
				error(e.toChars(context) + " has no value");
			arguments.set(i, e);
		}

		expandTuples(arguments);
		assert (arguments != null && arguments.size() > 0);

		e = op_overload(sc, context);
		if (null == e) {
			error("no [] operator overload for type "
					+ e1.type.toChars(context));
			e = e1;
		}

		return e;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		expToCBuffer(buf, hgs, e1, PREC.PREC_primary, context);
		buf.writeByte('[');
		argsToCBuffer(buf, arguments, hgs, context);
		buf.writeByte(']');
	}

	@Override
	public Expression toLvalue(Scope sc, Expression e, SemanticContext context) {
		if ((type != null) && (type.toBasetype(context).ty == TY.Tvoid))
			error("voids have no value");
		return this;
	}

}