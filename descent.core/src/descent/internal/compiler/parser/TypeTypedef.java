package descent.internal.compiler.parser;

public class TypeTypedef extends Type {
	
	public TypedefDeclaration sym;

	public TypeTypedef(TypedefDeclaration sym) {
		super(TY.Ttypedef, null);
		this.sym = sym;
	}
	
	@Override
	public Type semantic(Scope sc, SemanticContext context) {
		 sym.semantic(sc, context);
		 return merge(context);
	}

	@Override
	public int kind() {
		return TYPE_TYPEDEF;
	}

}
