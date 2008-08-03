package descent.internal.compiler.parser;

public abstract class SymbolExp extends Expression {
	
	public Declaration var;
	public boolean hasOverloads;

	public SymbolExp(Loc loc, TOK op, Declaration var, boolean hasOverloads) {
		super(loc, op);
		
		this.var = var;
		this.hasOverloads = hasOverloads;
	}

}
