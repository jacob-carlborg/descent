package descent.internal.compiler.parser;

import java.math.BigDecimal;

public class RealExp extends Expression {
	
	public String str;
	public BigDecimal value;
	
	public RealExp(String str, BigDecimal value, Type type) {
		super(TOK.TOKfloat64);
		this.str = str;
		this.value = value;
		this.type = type;
	}
	
	@Override
	public Expression semantic(Scope sc, SemanticContext context) {
		if (type == null)
			type = Type.tfloat64;
		else
			type = type.semantic(sc, context);
		return this;
	}
	
	@Override
	public int getNodeType() {
		return REAL_EXP;
	}

}
