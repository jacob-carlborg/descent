package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.core.domX.IASTVisitor;

public class TemplateValueParameter extends TemplateParameter {
	
	public Type valType;
	public Expression specValue;
	public Expression defaultValue;
	
	public TemplateValueParameter(Loc loc, IdentifierExp ident, Type valType, Expression specValue, Expression defaultValue) {
		super(loc, ident);
		this.valType = valType;
		this.specValue = specValue;
		this.defaultValue = defaultValue;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, valType);
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, specValue);
			TreeVisitor.acceptChildren(visitor, defaultValue);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		VarDeclaration sparam = new VarDeclaration(loc, valType, ident, null);
		sparam.storage_class = STC.STCtemplateparameter;
		if (sc.insert(sparam) == null) {
			context.acceptProblem(Problem.newSemanticTypeError("Duplicate parameter " + ident, IProblem.DuplicatedParameter, 0, ident.start, ident.length));
		}

		sparam.semantic(sc, context);
		valType = valType.semantic(loc, sc, context);
		if (!(valType.isintegral() || valType.isfloating() || valType
				.isString())
				&& valType.ty != TY.Tident)
			error(
					"arithmetic/string type expected for value-parameter, not %s",
					valType.toChars());

		if (specValue != null) {
			Expression e = specValue;

			e = e.semantic(sc, context);
			e = e.implicitCastTo(sc, valType, context);
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
