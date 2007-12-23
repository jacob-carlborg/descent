package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

import static descent.internal.compiler.parser.TY.Tident;

// DMD 1.020
public class TypeIdentifier extends TypeQualified {

	public IdentifierExp ident;

	public TypeIdentifier(Loc loc, char[] ident) {
		this(loc, new IdentifierExp(loc, ident));
	}

	public TypeIdentifier(Loc loc, IdentifierExp ident) {
		super(loc, TY.Tident);
		this.ident = ident;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
		}
		visitor.endVisit(this);
	}

	@Override
	public MATCH deduceType(Scope sc, Type tparam,
			TemplateParameters parameters, Objects dedtypes,
			SemanticContext context) {
		// Extra check
		if (tparam != null && tparam.ty == Tident) {
			TypeIdentifier tp = (TypeIdentifier) tparam;

			for (int i = 0; i < idents.size(); i++) {
				IdentifierExp id1 = idents.get(i);
				IdentifierExp id2 = tp.idents.get(i);

				if (!id1.equals(id2)) {
					return MATCHnomatch;
				}
			}
		}
		return super.deduceType(sc, tparam, parameters, dedtypes, context);
	}

	@Override
	public int getNodeType() {
		return TYPE_IDENTIFIER;
	}

	@Override
	public Type reliesOnTident() {
		return this;
	}

	@Override
	public void resolve(Loc loc, Scope sc, Expression[] pe, Type[] pt,
			IDsymbol[] ps, SemanticContext context) {
		IDsymbol s;
		Dsymbol[] scopesym = { null };

		s = sc.search(loc, ident, scopesym, context);
		resolveHelper(loc, sc, s, scopesym[0], pe, pt, ps, context);
	}

	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
		Type[] t = { null };
		Expression[] e = { null };
		Dsymbol[] s = { null };

		resolve(loc, sc, e, t, s, context);
		if (t[0] != null) {
			if (t[0].ty == TY.Ttypedef) {
				TypeTypedef tt = (TypeTypedef) t[0];

				if (tt.sym.sem == 1) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.CircularReferenceOfTypedef, tt.sym.ident, new String[] { tt.sym.ident.toString() }));
				}
			}
		} else {
			if (s[0] != null) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.UsedAsAType, this, new String[] { toChars(context) }));
			} else {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.UsedAsAType, this, new String[] { toChars(context) }));
			}
			t[0] = tvoid;
		}
		return t[0];
	}

	@Override
	public Type syntaxCopy(SemanticContext context) {
		TypeIdentifier t;

		t = new TypeIdentifier(loc, ident);
		t.syntaxCopyHelper(this, context);
		return t;
	}

	@Override
	public void toCBuffer2(OutBuffer buf, IdentifierExp ident, HdrGenState hgs,
			SemanticContext context) {
		OutBuffer tmp = new OutBuffer();

		tmp.writestring(this.ident.toChars());
		toCBuffer2Helper(tmp, null, hgs, context);
		buf.prependstring(tmp.toChars());
		if (ident != null) {
			buf.writeByte(' ');
			buf.writestring(ident.toChars());
		}
	}

	@Override
	public char[] toCharArray() {
		if (idents == null || idents.isEmpty()) {
			return ident.ident;
		} else {
			return super.toCharArray();
		}
	}

	@Override
	public void toDecoBuffer(OutBuffer buf, SemanticContext context) {
		String name = ident.toChars();
		int len = name.length();
		buf.writestring(ty.mangleChar);
		buf.writestring(len);
		buf.writestring(name);
	}

	@Override
	public IDsymbol toDsymbol(Scope sc, SemanticContext context) {
		if (null == sc) {
			return null;
		}

		Dsymbol[] scopesym = { null };
		IDsymbol s = sc.search(loc, ident, scopesym, context);
		if (s != null) {
			if (idents != null) {
				for (int i = 0; i < idents.size(); i++) {
					IdentifierExp id = idents.get(i);
					s = s.searchX(loc, sc, id, context);
					if (null == s) // failed to find a symbol
					{
						break;
					}
				}
			}
		}
		return s;
	}

	@Override
	public Expression toExpression() {
		Expression e = new IdentifierExp(loc, ident.ident);
		e.setSourceRange(ident.start, ident.length);
		if (idents != null) {
			for (IdentifierExp id : idents) {
				e = new DotIdExp(loc, e, id);
				e.setSourceRange(ident.start, id.start + id.length
						- ident.start);
			}
		}
		return e;
	}

}
