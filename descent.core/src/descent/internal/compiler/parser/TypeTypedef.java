package descent.internal.compiler.parser;

public class TypeTypedef extends Type {
	
	public TypedefDeclaration sym;

	public TypeTypedef(TypedefDeclaration sym) {
		super(TY.Ttypedef, null);
		this.sym = sym;
		this.synthetic = true;
	}
	
	@Override
	public boolean isbit() {
		return sym.basetype.isbit();
	}
	
	@Override
	public Expression defaultInit(SemanticContext context) {
		Expression e;
		Type bt;

		if (sym.init != null) {
			return sym.init.toExpression(context);
		}
		bt = sym.basetype;
		e = bt.defaultInit(context);
		e.type = this;
		while (bt.ty == TY.Tsarray) {
			e.type = bt.next;
			bt = bt.next.toBasetype(context);
		}
		return e;
	}
	
	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
		 sym.semantic(sc, context);
		 return merge(context);
	}
	
	@Override
	public boolean isintegral() {
		return sym.basetype.isintegral();
	}
	
	@Override
	public boolean isunsigned() {
		return sym.basetype.isunsigned();
	}

	@Override
	public int getNodeType() {
		return TYPE_TYPEDEF;
	}

}
