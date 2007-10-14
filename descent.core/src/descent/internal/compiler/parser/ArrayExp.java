package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class ArrayExp extends UnaExp {

	public Expressions arguments;

	public ArrayExp(Loc loc, Expression e, Expressions arguments) {
		super(loc, TOK.TOKarray, e);
		this.arguments = arguments;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, e1);
		}
		visitor.endVisit(this);
	}

	@Override
	public Expression doInline(InlineDoState ids) {
		ArrayExp ce;

		ce = (ArrayExp) copy();
		ce.e1 = e1.doInline(ids);
		ce.arguments = arrayExpressiondoInline(arguments, ids);
		return ce;
	}

	@Override
	public int getNodeType() {
		return ARRAY_EXP;
	}

	@Override
	public int inlineCost(InlineCostState ics, SemanticContext context) {
		return 1 + e1.inlineCost(ics, context)
				+ arrayInlineCost(ics, arguments, context);
	}

	@Override
	public Expression inlineScan(InlineScanState iss, SemanticContext context) {
		Expression e = this;

		e1 = e1.inlineScan(iss, context);
		arrayInlineScan(iss, arguments, context);

		return e;
	}

	@Override
	public char[] opId() {
		return Id.index;
	}

	@Override
	public void scanForNestedRef(Scope sc, SemanticContext context) {
		e1.scanForNestedRef(sc, context);
		arrayExpressionScanForNestedRef(sc, arguments, context);
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
			if (arguments.size() != 1) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.OnlyOneIndexAllowedToIndex, 0, start,
						length, new String[] { t1.toChars(context) }));
			}
			e = new IndexExp(loc, e1, arguments.get(0));
			return e.semantic(sc, context);
		}

		// Run semantic() on each argument
		for (int i = 0; i < arguments.size(); i++) {
			e = arguments.get(i);
			e = e.semantic(sc, context);
			if (null == e.type) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.SymbolHasNoValue, 0, e.start, e.length, new String[] { e.toChars(context) }));
			}
			arguments.set(i, e);
		}

		expandTuples(arguments, context);
		assert (arguments != null && arguments.size() > 0);

		e = op_overload(sc, context);
		if (null == e) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.NoOpIndexOperatorOverloadForType, 0, start,
					length, new String[] { e1.type.toChars(context) }));
			e = e1;
		}

		return e;
	}

	@Override
	public Expression syntaxCopy() {
		return new ArrayExp(loc, e1.syntaxCopy(), arraySyntaxCopy(arguments));
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
		if ((type != null) && (type.toBasetype(context).ty == TY.Tvoid)) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.VoidsHaveNoValue, 0, start,
					length));
		}
		return this;
	}

}
