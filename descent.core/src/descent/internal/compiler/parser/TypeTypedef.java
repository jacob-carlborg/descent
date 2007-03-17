package descent.internal.compiler.parser;

public class TypeTypedef extends Type {
	
	public TypedefDeclaration sym;

	public TypeTypedef(TypedefDeclaration sym) {
		super(TY.Ttypedef, null);
		this.sym = sym;
		this.synthetic = true;
	}
	
	@Override
	public Type semantic(Scope sc, SemanticContext context) {
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
	public int kind() {
		return TYPE_TYPEDEF;
	}

}
