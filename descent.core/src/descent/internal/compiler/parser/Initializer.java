package descent.internal.compiler.parser;

// DMD 1.020
public abstract class Initializer extends ASTDmdNode {
	
	public Loc loc;

	public Initializer(Loc loc) {
		this.loc = loc;
	}

	public Initializers arraySyntaxCopy(Initializers ai) {
		Initializers a = null;

		if (ai != null) {
			a = new Initializers();
			a.setDim(ai.size());
			for (int i = 0; i < a.size(); i++) {
				Initializer e = ai.get(i);
				e = e.syntaxCopy();
				a.set(i, e);
			}
		}
		return a;
	}

	public Type inferType(Scope sc, SemanticContext context) {
		error("cannot infer type from initializer");
		return Type.terror;
	}
	
	public ArrayInitializer isArrayInitializer() {
		return null;
	}

	public ExpInitializer isExpInitializer() {
		return null;
	}

	public VoidInitializer isVoidInitializer() {
		return null;
	}

	public Initializer semantic(Scope sc, Type t, SemanticContext context) {
		return this;
	}

	public Initializer syntaxCopy() {
		return this;
	}

	public abstract void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context);

	@Override
	public String toChars(SemanticContext context) {
		OutBuffer buf = new OutBuffer();
		HdrGenState hgs = new HdrGenState();
		toCBuffer(buf, hgs, context);
		return buf.toChars();
	}

	public abstract Expression toExpression(SemanticContext context);

}
