package descent.internal.compiler.parser;

import descent.core.compiler.IProblem;

public class TemplateValueParameter extends TemplateParameter {
	
	public Type valType;
	public Expression specValue;
	public Expression defaultValue;
	
	public TemplateValueParameter(IdentifierExp ident, Type valType, Expression specValue, Expression defaultValue) {
		super(ident);
		this.valType = valType;
		this.specValue = specValue;
		this.defaultValue = defaultValue;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		VarDeclaration sparam = new VarDeclaration(valType, ident, null);
		sparam.storage_class = STC.STCtemplateparameter;
		if (sc.insert(sparam) == null) {
			context.acceptProblem(Problem.newSemanticTypeError("Duplicate parameter " + ident, IProblem.DuplicatedParameter, 0, ident.start, ident.length));
		}

		sparam.semantic(sc, context);
		valType = valType.semantic(sc, context);
		if (!(valType.isintegral() || valType.isfloating() || valType
				.isString())
				&& valType.ty != TY.Tident)
			error(
					"arithmetic/string type expected for value-parameter, not %s",
					valType.toChars());

		if (specValue != null) {
			Expression e = specValue;

			e = e.semantic(sc, context);
			e = e.implicitCastTo(sc, valType);
			e = e.optimize(Expression.WANTvalue | Expression.WANTinterpret);
			if (e.op == TOK.TOKint64 || e.op == TOK.TOKfloat64
					|| e.op == TOK.TOKcomplex80 || e.op == TOK.TOKnull
					|| e.op == TOK.TOKstring)
				specValue = e;
		}
	}
	
	@Override
	public int getNodeType() {
		return TEMPLATE_VALUE_PARAMETER;
	}

}
