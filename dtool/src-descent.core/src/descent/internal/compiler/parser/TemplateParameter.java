package descent.internal.compiler.parser;

import java.util.List;

import static descent.internal.compiler.parser.DYNCAST.DYNCAST_DSYMBOL;
import static descent.internal.compiler.parser.DYNCAST.DYNCAST_EXPRESSION;
import static descent.internal.compiler.parser.DYNCAST.DYNCAST_TUPLE;
import static descent.internal.compiler.parser.DYNCAST.DYNCAST_TYPE;
import static descent.internal.compiler.parser.TOK.TOKfunction;
import static descent.internal.compiler.parser.TOK.TOKvar;

// DMD 1.020
public abstract class TemplateParameter extends ASTDmdNode {

	public static Dsymbol getDsymbol(ASTDmdNode oarg, SemanticContext context) {
		Dsymbol sa;
		Expression ea = isExpression(oarg);
		if (ea != null) { // Try to convert Expression to symbol
			if (ea.op == TOKvar) {
				sa = ((VarExp) ea).var;
			} else if (ea.op == TOKfunction) {
				sa = ((FuncExp) ea).fd;
			} else {
				sa = null;
			}
		} else { // Try to convert Type to symbol
			Type ta = isType(oarg);
			if (ta != null) {
				sa = ta.toDsymbol(null, context);
			} else {
				sa = isDsymbol(oarg); // if already a symbol
			}
		}
		return sa;
	}

	public static Type getType(ASTDmdNode o) {
		Type t = isType(o);
		if (null == t) {
			Expression e = isExpression(o);
			if (e != null) {
				t = e.type;
			}
		}
		return t;
	}

	public static Dsymbol isDsymbol(ASTDmdNode o) {
		//return dynamic_cast<Dsymbol >(o);
		if (null == o || o.dyncast() != DYNCAST_DSYMBOL) {
			return null;
		}
		return (Dsymbol) o;
	}

	public static Expression isExpression(ASTDmdNode o) {
		//return dynamic_cast<Expression >(o);
		if (null == o || o.dyncast() != DYNCAST_EXPRESSION) {
			return null;
		}
		return (Expression) o;
	}

	public static Tuple isTuple(ASTDmdNode o) {
		//return dynamic_cast<Tuple >(o);
		if (null == o || o.dyncast() != DYNCAST_TUPLE) {
			return null;
		}
		return (Tuple) o;
	}

	public static Type isType(ASTDmdNode o) {
		//return dynamic_cast<Type >(o);
		if (null == o || o.dyncast() != DYNCAST_TYPE) {
			return null;
		}
		return (Type) o;
	}

	public Loc loc;
	public IdentifierExp ident;
	public Declaration sparam;

	public TemplateParameter(Loc loc, IdentifierExp ident) {
		this.loc = loc;
		this.ident = ident;
		this.sparam = null;
	}

	public abstract void declareParameter(Scope sc, SemanticContext context);

	public abstract ASTDmdNode defaultArg(Scope sc, SemanticContext context);

	/**
	 * Create dummy argument based on parameter.
	 */
	public abstract ASTDmdNode dummyArg(SemanticContext context);

	public TemplateAliasParameter isTemplateAliasParameter() {
		return null;
	}

	public TemplateTupleParameter isTemplateTupleParameter() {
		return null;
	}

	public TemplateTypeParameter isTemplateTypeParameter() {
		return null;
	}

	public TemplateValueParameter isTemplateValueParameter() {
		return null;
	}

	/**
	 * Match actual argument against parameter.
	 */
	public abstract MATCH matchArg(Scope sc, List<ASTDmdNode> tiargs, int i,
			List<TemplateParameter> parameters, List<ASTDmdNode> dedtypes,
			Declaration[] psparam, SemanticContext context);

	/**
	 * If TemplateParameter's match as far as overloading goes.
	 */
	public abstract int overloadMatch(TemplateParameter tp);

	public abstract void semantic(Scope sc, SemanticContext context);

	public abstract ASTDmdNode specialization();

	public abstract TemplateParameter syntaxCopy();

	public abstract void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context);

}
