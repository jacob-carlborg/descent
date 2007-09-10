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
			Dsymbol[] ps, SemanticContext context) {
		Dsymbol s;
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
							//"Circular reference of typedef " + tt.sym.ident,
							IProblem.CircularDefinition, 0, tt.sym.ident.start,
							tt.sym.ident.length));
				}
			}
		} else {
			if (s[0] != null) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.UsedAsAType, 0, s[0].ident.start,
						s[0].ident.length, new String[] { new String(
								s[0].ident.ident) }));
			} else {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.UsedAsAType, 0, this.ident.start,
						this.ident.length, new String[] { new String(
								this.ident.ident) }));
			}
			// TODO see if this change is ok, I change it to error to get
			// just one error on things like "Clazz x;" where "Clazz is not defined".
			// (otherwise I get "voids have no value" also).
			t[0] = terror;
		}
		return t[0];
	}

	@Override
	public Type syntaxCopy() {
		TypeIdentifier t;

		t = new TypeIdentifier(loc, ident);
		t.syntaxCopyHelper(this);
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
	public Dsymbol toDsymbol(Scope sc, SemanticContext context) {
		Dsymbol s;
		Dsymbol[] scopesym = { null };

		if (sc == null) {
			return null;
		}
		s = sc.search(loc, ident, scopesym, context);
		if (s != null) {
			s = s.toAlias(context);
			if (idents != null) {
				for (IdentifierExp id : idents) {
					Dsymbol sm;
					if (id.dyncast() != DYNCAST.DYNCAST_IDENTIFIER) {
						// It's a template instance
						TemplateDeclaration td;
						TemplateInstance ti = ((TemplateInstanceWrapper) id).tempinst;
						id = ti.idents.get(0);
						sm = s.search(loc, id, 0, context);
						if (sm == null) {
							error(
									"template identifier %s is not a member of %s",
									id.toChars(), s.toChars(context));
							break;
						}
						sm = sm.toAlias(context);
						td = sm.isTemplateDeclaration();
						if (td == null) {
							error("%s is not a template", id.toChars());
							break;
						}
						ti.tempdecl = td;
						if (!ti.semanticdone) {
							ti.semantic(sc, context);
						}
						sm = ti.toAlias(context);
					} else {
						sm = s.search(loc, id, 0, context);
					}
					s = sm;

					if (s == null) // failed to find a symbol
					{
						break;
					}
					s = s.toAlias(context);
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
