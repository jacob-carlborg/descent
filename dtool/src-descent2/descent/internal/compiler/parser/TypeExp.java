package descent.internal.compiler.parser;

public class TypeExp extends Expression {

	public TypeExp(Loc loc, Type type) {
		super(loc, TOK.TOKtype);
		this.type = type;
	}
	
	@Override
	public int getNodeType() {
		return TYPE_EXP;
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		type = type.semantic(loc, sc, context);
	    return this;
	}
	
	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		type.toCBuffer(buf, null, hgs);
	}

}