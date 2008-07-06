package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class VoidInitializer extends Initializer {

	public Type type;

	public VoidInitializer(Loc loc) {
		super(loc);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return VOID_INITIALIZER;
	}

	@Override
	public VoidInitializer isVoidInitializer() {
		return this;
	}

	@Override
	public Initializer semantic(Scope sc, Type t, SemanticContext context) {
		type = t;
		return this;
	}

	@Override
	public Initializer syntaxCopy() {
		return new VoidInitializer(loc);
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("void");
	}

	@Override
	public Expression toExpression(SemanticContext context) {
		error("void initializer has no value");
		return new IntegerExp(loc, 0, Type.tint32);
	}

}
