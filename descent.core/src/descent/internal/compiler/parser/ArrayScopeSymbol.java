package descent.internal.compiler.parser;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.STC.STCconst;

import static descent.internal.compiler.parser.TOK.TOKarrayliteral;
import static descent.internal.compiler.parser.TOK.TOKindex;
import static descent.internal.compiler.parser.TOK.TOKslice;
import static descent.internal.compiler.parser.TOK.TOKstring;
import static descent.internal.compiler.parser.TOK.TOKtuple;
import static descent.internal.compiler.parser.TOK.TOKtype;

import static descent.internal.compiler.parser.TY.Ttuple;

// DMD 1.020
public class ArrayScopeSymbol extends ScopeDsymbol {

	public Expression exp;
	public TypeTuple type; // for tuple[length]
	public TupleDeclaration td; // for tuples of objects

	public ArrayScopeSymbol(Expression e) {
		Assert.isTrue(e.op == TOK.TOKindex || e.op == TOK.TOKslice);
		exp = e;
	}

	public ArrayScopeSymbol(TupleDeclaration s) {
		this.exp = null;
		this.type = null;
		this.td = s;
	}

	public ArrayScopeSymbol(TypeTuple t) {
		this.exp = null;
		this.type = t;
		this.td = null;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		melnorme.miscutil.Assert.fail("accept0 on a fake Node");
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
	public Dsymbol search(Loc loc, char[] ident, int flags,
			SemanticContext context) {
		if (CharOperation.equals(ident, Id.length)
				|| CharOperation.equals(ident, Id.dollar)) {
			Expression pvar;
			Expression ce;

			// L1:

			boolean loop = true;
			while (loop) {
				loop = false;

				if (td != null) {
					VarDeclaration v = new VarDeclaration(loc, Type.tsize_t,
							Id.dollar, null);
					Expression e = new IntegerExp(Loc.ZERO, td.objects.size(),
							Type.tsize_t);
					v.init = new ExpInitializer(Loc.ZERO, e);
					v.storage_class |= STCconst;
					return v;
				}

				if (type != null) {
					VarDeclaration v = new VarDeclaration(loc, Type.tsize_t,
							Id.dollar, null);
					Expression e = new IntegerExp(Loc.ZERO, type.arguments
							.size(), Type.tsize_t);
					v.init = new ExpInitializer(Loc.ZERO, e);
					v.storage_class |= STCconst;
					return v;
				}

				if (exp.op == TOKindex) {
					IndexExp ie = (IndexExp) exp;

					// TODO semantic I think this logic is ok
					// pvar = &ie.lengthVar;
					pvar = ie;
					ce = ie.e1;
				} else if (exp.op == TOKslice) {
					SliceExp se = (SliceExp) exp;

					// TODO semantic I think this logic is ok
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
						Expression e = new IntegerExp(Loc.ZERO,
								((StringExp) ce).len, Type.tsize_t);
						v.init = new ExpInitializer(Loc.ZERO, e);
						v.storage_class |= STCconst;
					} else if (ce.op == TOKarrayliteral) {
						/* It is for an array literal, so the
						 * length will be a const.
						 */
						Expression e = new IntegerExp(Loc.ZERO,
								((ArrayLiteralExp) ce).elements.size(),
								Type.tsize_t);
						v.init = new ExpInitializer(Loc.ZERO, e);
						v.storage_class |= STCconst;
					} else if (ce.op == TOKtuple) {
						Expression e = new IntegerExp(loc, ((TupleExp) ce).exps
								.size(), Type.tsize_t);
						v.init = new ExpInitializer(loc, e);
						v.storage_class |= STCconst;
					}

					// TODO semantic I think this logic is ok
					// *pvar = v;
					if (pvar instanceof IndexExp) {
						((IndexExp) pvar).lengthVar = v;
					} else {
						((SliceExp) pvar).lengthVar = v;
					}
				}
				// TODO semantic I think this logic is ok
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
