package descent.internal.compiler.parser;

public class VarExp extends Expression {
	
	public Declaration var;

	public VarExp(Declaration var) {
		super(TOK.TOKvar);
		this.var = var;
		this.type = var.type;
	}

	@Override
	public int kind() {
		return VAR_EXP;
	}

}
