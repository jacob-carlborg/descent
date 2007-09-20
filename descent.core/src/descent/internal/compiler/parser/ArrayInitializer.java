package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class ArrayInitializer extends Initializer {

	public Expressions index;
	public Initializers value;
	public int dim; // length of array being initialized
	public Type type; // type that array will be used to initialize
	public int sem; // !=0 if semantic() is run

	public ArrayInitializer(Loc loc) {
		super(loc);
	}

	@Override
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
		dim = 0;
		type = null;
	}

	@Override
	public int getNodeType() {
		return ARRAY_INITIALIZER;
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
				Initializer iz = value.get(0);
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
	public Initializer semantic(Scope sc, Type t, SemanticContext context) {
		int i;
		int length;

		if (sem != 0) {
			return this;
		}
		sem = 1;
		type = t;
		t = t.toBasetype(context);
		switch (t.ty) {
		case Tpointer:
		case Tsarray:
		case Tarray:
			break;

		default:
			error(loc, "cannot use array to initialize %s", type
					.toChars(context));
			return this;
		}

		length = 0;
		for (i = 0; i < index.size(); i++) {
			Expression idx;
			Initializer val;

			idx = index.get(i);
			if (idx != null) {
				idx = idx.semantic(sc, context);
				idx = idx.optimize(WANTvalue | WANTinterpret, context);
				index.set(i, idx);
				length = idx.toInteger(context).intValue();
			}

			val = value.get(i);
			val = val.semantic(sc, t.next, context);
			value.set(i, val);
			length++;
			if (length == 0) {
				error("array dimension overflow");
			}
			if (length > dim) {
				dim = length;
			}
		}
		// TODO semantic
		//	    unsigned long amax = 0x80000000;
		//	    if ((unsigned long) dim * t.next.size() >= amax)
		//		error(loc, "array dimension %u exceeds max of %ju", dim, amax / t.next.size());
		return this;
	}

	@Override
	public Initializer syntaxCopy() {
		ArrayInitializer ai = new ArrayInitializer(loc);

		if (!(index.size() == value.size())) {
			throw new IllegalStateException("assert(index.dim == value.dim);");
		}

		ai.index.ensureCapacity(index.size());
		ai.value.ensureCapacity(value.size());
		for (int i = 0; i < ai.value.size(); i++) {
			Expression e = index.get(i);
			if (e != null) {
				e = e.syntaxCopy();
			}
			ai.index.set(i, e);

			Initializer init = value.get(i);
			init = init.syntaxCopy();
			ai.value.set(i, init);
		}
		return ai;
	}

	public Initializer toAssocArrayInitializer(SemanticContext context) {
		Expressions keys;
		Expressions values;
		Expression e;

		keys = new Expressions();
		keys.ensureCapacity(value.size());
		values = new Expressions();
		values.ensureCapacity(value.size());

		for (int i = 0; i < value.size(); i++) {
			e = index.get(i);
			if (null == e) {
				// goto Lno;
				keys = null;
				values = null;
				error(loc, "not an associative array initializer");
				return this;
			}
			keys.set(i, e);

			Initializer iz = value.get(i);
			if (null == iz) {
				// goto Lno;
				keys = null;
				values = null;
				error(loc, "not an associative array initializer");
				return this;
			}
			e = iz.toExpression(context);
			if (null == e) {
				// goto Lno;
				keys = null;
				values = null;
				error(loc, "not an associative array initializer");
				return this;
			}
			values.set(i, e);
		}
		e = new AssocArrayLiteralExp(loc, keys, values);
		return new ExpInitializer(loc, e);
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writebyte('[');
		for (int i = 0; i < index.size(); i++) {
			if (i > 0) {
				buf.writebyte(',');
			}
			Expression ex = index.get(i);
			if (ex != null) {
				ex.toCBuffer(buf, hgs, context);
				buf.writebyte(':');
			}
			Initializer iz = value.get(i);
			if (iz != null) {
				iz.toCBuffer(buf, hgs, context);
			}
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
	
	@Override
	public ArrayInitializer isArrayInitializer() {
		return this;
	}
	
	@Override
	public void setBinding(ASTDmdNode binding) {
		super.setBinding(binding);
		
		if (value != null) {
			ExpInitializer expInit = (ExpInitializer) binding;
			AssignExp assignExp = (AssignExp) expInit.exp;
			ArrayLiteralExp arrayLiteralExp = (ArrayLiteralExp) assignExp.e2;
			for(int i = 0; i < value.size(); i++) {
				value.get(i).setBinding(arrayLiteralExp.elements.get(i).getBinding());
			}
		}		
	}

}
