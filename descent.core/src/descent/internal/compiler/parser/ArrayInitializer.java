package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class ArrayInitializer extends Initializer {

	public Expressions index;
	public Initializers value;
	public Type type; // type that array will be used to initialize

	public ArrayInitializer(Loc loc) {
		super(loc);
	}

	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, index);
			TreeVisitor.acceptChildren(visitor, value);
		}
		visitor.endVisit(this);
	}

	public void addInit(Expression index, Initializer value) {
		if (this.index == null) {
			this.index = new Expressions();
			this.value = new Initializers();
		}
		this.index.add(index);
		this.value.add(value);
	}

	@Override
	public Type inferType(Scope sc, SemanticContext context) {
		if (value != null) {
			for (int i = 0; i < value.size(); i++) {
				if (index.get(i) != null) {
					// goto Lno;
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.CannotInferType, 0, start, length));
					return Type.terror;
				}
			}
			if (value.size() > 0) {
				Initializer iz = (Initializer) value.get(0);
				if (iz != null) {
					Type t = iz.inferType(sc, context);
					t = new TypeSArray(t, new IntegerExp(iz.loc, value.size()));
					t = t.semantic(loc, sc, context);
					return t;
				}
			}
		}

		context.acceptProblem(Problem.newSemanticTypeError(
				IProblem.CannotInferType, 0, start, length));
		return Type.terror;
	}

	@Override
	public int getNodeType() {
		return ARRAY_INITIALIZER;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writebyte('[');
		for (int i = 0; i < index.size(); i++) {
			if (i > 0)
				buf.writebyte(',');
			Expression ex = index.get(i);
			if (ex != null) {
				ex.toCBuffer(buf, hgs, context);
				buf.writebyte(':');
			}
			Initializer iz = value.get(i);
			if (iz != null)
				iz.toCBuffer(buf, hgs, context);
		}
		buf.writebyte(']');
	}

	@Override
	public Expression toExpression(SemanticContext context) {
		Expressions elements;
		Expression e;

		elements = new Expressions();
		for (int i = 0; i < value.size(); i++) {
			if (index.get(i) != null) {
				// goto Lno;
				elements = null;
				error(loc, "array initializers as expressions are not allowed");
				return null;
			}
			Initializer iz = value.get(i);
			if (null == iz) {
				// goto Lno;
				elements = null;
				error(loc, "array initializers as expressions are not allowed");
				return null;
			}
			Expression ex = iz.toExpression(context);
			if (null == ex) {
				// goto Lno;
				elements = null;
				error(loc, "array initializers as expressions are not allowed");
				return null;
			}
			elements.add(ex);
		}
		e = new ArrayLiteralExp(loc, elements);
		e.type = type;
		return e;
	}

}
