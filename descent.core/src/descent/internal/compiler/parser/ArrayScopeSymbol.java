package descent.internal.compiler.parser;

import org.eclipse.core.runtime.Assert;
import static descent.internal.compiler.parser.TOK.*;
import static descent.internal.compiler.parser.STC.*;
import static descent.internal.compiler.parser.TY.*;

public class ArrayScopeSymbol extends ScopeDsymbol {

	public Expression exp;
	public TypeTuple type; // for tuple[length]
	public TupleDeclaration td; // for tuples of objects

	public ArrayScopeSymbol(Expression e) {
		super(Loc.ZERO);
		Assert.isTrue(e.op == TOK.TOKindex || e.op == TOK.TOKslice);
		this.exp = e;
		this.type = null;
		this.td = null;
	}

	public ArrayScopeSymbol(Loc loc, TupleDeclaration s) {
		super(loc);
		this.exp = null;
		this.type = null;
		this.td = s;
	}

	public ArrayScopeSymbol(Loc loc, TypeTuple t) {
		super(loc);
		this.exp = null;
		this.type = t;
		this.td = null;
	}

	@Override
	public int getNodeType() {
		return ARRAY_SCOPE_SYMBOL;
	}

	@Override
	public ArrayScopeSymbol isArrayScopeSymbol() {
		return this;
	}

	@Override
	public Dsymbol search(Loc loc, String ident, int flags, SemanticContext context) {
		if (ident.equals(Id.length.string) || ident.equals(Id.dollar.string)) {
			Expression pvar;
			Expression ce;

			// L1:

			boolean loop = true;
			while (loop) {
				loop = false;

				if (td != null) {
					VarDeclaration v = new VarDeclaration(loc, Type.tsize_t,
							Id.dollar, null);
					Expression e = new IntegerExp(loc, td.objects.size(),
							Type.tsize_t);
					v.init = new ExpInitializer(loc, e);
					v.storage_class |= STCconst;
					return v;
				}

				if (type != null) {
					VarDeclaration v = new VarDeclaration(loc, Type.tsize_t,
							Id.dollar, null);
					Expression e = new IntegerExp(loc, type.arguments.size(),
							Type.tsize_t);
					v.init = new ExpInitializer(loc, e);
					v.storage_class |= STCconst;
					return v;
				}

				if (exp.op == TOKindex) {
					IndexExp ie = (IndexExp) exp;

					// pvar = &ie.lengthVar;
					pvar = ie;
					ce = ie.e1;
				} else if (exp.op == TOKslice) {
					SliceExp se = (SliceExp) exp;

					// pvar = &se.lengthVar;
					pvar = se;
					ce = se.e1;
				} else {
					return null;
				}

				if (ce.op == TOKtype) {
					Type t = ((TypeExp) ce).type;
					if (t.ty == Ttuple) {
						type = (TypeTuple) t;
						// goto L1;
						loop = true;
						continue;
					}
				}

				boolean enterIf;
				if (pvar instanceof IndexExp) {
					enterIf = ((IndexExp) pvar).lengthVar == null;
				} else {
					enterIf = ((SliceExp) pvar).lengthVar == null;
				}
				if (enterIf) {
					VarDeclaration v = new VarDeclaration(loc, Type.tsize_t,
							Id.dollar, null);

					if (ce.op == TOKstring) {
						Expression e = new IntegerExp(loc, ((StringExp) ce).len,
								Type.tsize_t);
						v.init = new ExpInitializer(loc, e);
						v.storage_class |= STCconst;
					} else if (ce.op == TOKtuple) {
						Expression e = new IntegerExp(loc, ((TupleExp) ce).exps
								.size(), Type.tsize_t);
						v.init = new ExpInitializer(loc, e);
						v.storage_class |= STCconst;
					}

					// *pvar = v;
					if (pvar instanceof IndexExp) {
						((IndexExp) pvar).lengthVar = v;
					} else {
						((SliceExp) pvar).lengthVar = v;
					}
				}
				// return *pvar;
				if (pvar instanceof IndexExp) {
					return ((IndexExp) pvar).lengthVar;
				} else {
					return ((SliceExp) pvar).lengthVar;
				}
			}
		}
		return null;
	}

}
