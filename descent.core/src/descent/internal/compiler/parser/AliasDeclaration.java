package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.TOK.TOKfunction;
import static descent.internal.compiler.parser.TOK.TOKvar;

// DMD 1.020
public class AliasDeclaration extends Declaration {

	public boolean first = true; // is this the first declaration in a multi
	public AliasDeclaration next;

	public Type htype;
	public Dsymbol haliassym;
	public Dsymbol aliassym;
	public Dsymbol overnext; // next in overload list
	public int inSemantic;

	public AliasDeclaration(Loc loc, IdentifierExp id, Dsymbol s) {
		super(id);

		Assert.isTrue(s != this);

		this.loc = loc;
		this.type = null;
		this.aliassym = s;
		this.htype = null;
		this.haliassym = null;
		this.overnext = null;
		this.inSemantic = 0;

		Assert.isNotNull(s);
	}

	public AliasDeclaration(Loc loc, IdentifierExp id, Type type) {
		super(id);
		this.loc = loc;
		this.type = type;
		this.sourceType = type;
		this.aliassym = null;
		this.htype = null;
		this.haliassym = null;
		this.overnext = null;
		this.inSemantic = 0;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, ident);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return ALIAS_DECLARATION;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public AliasDeclaration isAliasDeclaration() {
		return this;
	}

	@Override
	public String kind() {
		return "alias";
	}

	@Override
	public boolean overloadInsert(Dsymbol s, SemanticContext context) {
		/*
		 * Don't know yet what the aliased symbol is, so assume it can be
		 * overloaded and check later for correctness.
		 */

		if (overnext == null) {
			overnext = s;
			return true;
		} else {
			return overnext.overloadInsert(s, context);
		}
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (aliassym != null) {
			if (aliassym.isTemplateInstance() != null) {
				aliassym.semantic(sc, context);
			}
			return;
		}
		this.inSemantic = 1;

		if ((storage_class & STC.STCconst) != 0) {
			// Signal better the error using the modifiers (HACK)
			if (modifiers != null) {
				for (Modifier modifier : modifiers) {
					if (modifier.tok == TOK.TOKconst) {
						context.acceptProblem(Problem.newSemanticTypeError(
								IProblem.AliasCannotBeConst, 0, modifier.start,
								modifier.length));
					}
				}
			}
		}

		storage_class |= sc.stc & STC.STCdeprecated;

		// Given:
		// alias foo.bar.abc def;
		// it is not knowable from the syntax whether this is an alias
		// for a type or an alias for a symbol. It is up to the semantic()
		// pass to distinguish.
		// If it is a type, then type is set and getType() will return that
		// type. If it is a symbol, then aliassym is set and type is NULL -
		// toAlias() will return aliasssym.

		Dsymbol[] s = { null };
		Type[] t = { null };
		Expression[] e = { null };

		/* This section is needed because resolve() will:
		 *   const x = 3;
		 *   alias x y;
		 * try to alias y to 3.
		 */
		s[0] = type.toDsymbol(sc, context);
		if (s[0] != null) {
			// goto L2;
			semantic_L2(sc, context, s[0]); // it's a symbolic alias
			return;
		}

		type.resolve(loc, sc, e, t, s, context);
		if (s[0] != null) {
			// goto L2;
			semantic_L2(sc, context, s[0]); // it's a symbolic alias
			return;
		} else if (e[0] != null) {
			// Try to convert Expression to Dsymbol
			if (e[0].op == TOKvar) {
				s[0] = ((VarExp) e[0]).var;
				// goto L2;
				semantic_L2(sc, context, s[0]); // it's a symbolic alias
				return;
			} else if (e[0].op == TOKfunction) {
				s[0] = ((FuncExp) e[0]).fd;
				// goto L2;
				semantic_L2(sc, context, s[0]); // it's a symbolic alias
				return;
			} else {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.CannotAliasAnExpression, 0, sourceType.start, sourceType.length, new String[] { e[0].toChars(context) }));
				t[0] = e[0].type;
			}
		} else if (t[0] != null) {
			type = t[0];
		}
		if (overnext != null) {
			ScopeDsymbol.multiplyDefined(Loc.ZERO, this, overnext, context);
		}
		this.inSemantic = 0;
		return;
	}

	public void semantic_L1(Scope sc, SemanticContext context) {
		if (overnext != null) {
			ScopeDsymbol.multiplyDefined(Loc.ZERO, this, overnext, context);
		}
		type = type.semantic(loc, sc, context);
		this.inSemantic = 0;
		return;
	}

	public void semantic_L2(Scope sc, SemanticContext context, Dsymbol s) {
		Type tempType = type;
		type = null;
		VarDeclaration v = s.isVarDeclaration();
		if (v != null && v.linkage == LINK.LINKdefault) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.ForwardReferenceOfSymbol, 0, tempType.start,
					tempType.length, new String[] { tempType.toString() }));
			context
					.acceptProblem(Problem.newSemanticTypeError(
							IProblem.ForwardReferenceOfSymbol, 0, v.ident.start,
							v.ident.length, new String[] { new String(
									v.ident.ident) }));
			s = null;
		} else {
			FuncDeclaration f = s.toAlias(context).isFuncDeclaration();
			if (f != null) {
				if (overnext != null) {
					FuncAliasDeclaration fa = new FuncAliasDeclaration(loc, f);
					if (!fa.overloadInsert(overnext, context)) {
						ScopeDsymbol.multiplyDefined(Loc.ZERO, f, overnext, context);
					}
					overnext = null;
					s = fa;
					s.parent = sc.parent;
				}
			}
			if (overnext != null) {
				ScopeDsymbol.multiplyDefined(Loc.ZERO, s, overnext, context);
			}
			if (s == this) {
				s = null;
			}
		}
		aliassym = s;
		this.inSemantic = 0;
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		Assert.isTrue(s == null);
		AliasDeclaration sa;
		if (type != null) {
			sa = new AliasDeclaration(loc, ident, type.syntaxCopy());
		} else {
			sa = new AliasDeclaration(loc, ident, aliassym.syntaxCopy(null));
		}
		// Syntax copy for header file
		if (htype == null) // Don't overwrite original
		{
			if (type != null) // Make copy for both old and new instances
			{
				htype = type.syntaxCopy();
				sa.htype = type.syntaxCopy();
			}
		} else {
			// Make copy of original for new instance
			sa.htype = htype.syntaxCopy();
		}
		if (haliassym == null) {
			if (aliassym != null) {
				haliassym = aliassym.syntaxCopy(s);
				sa.haliassym = aliassym.syntaxCopy(s);
			}
		} else {
			sa.haliassym = haliassym.syntaxCopy(s);
		}
		return sa;
	}

	@Override
	public Dsymbol toAlias(SemanticContext context) {
		Assert.isTrue(this != aliassym);
		if (inSemantic != 0) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.CircularDefinition, 0, ident.start, ident.length, new String[] { toChars(context) }));
		}
		Dsymbol s = aliassym != null ? aliassym.toAlias(context) : this;
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("alias ");
		if (aliassym != null) {
			aliassym.toCBuffer(buf, hgs, context);
			buf.writeByte(' ');
			buf.writestring(ident.toChars());
		} else {
			type.toCBuffer(buf, ident, hgs, context);
		}
		buf.writeByte(';');
		buf.writenl();
	}
	
	// Specific for Descent
	public ASTDmdNode getBinding() {
		return aliassym;
	}

}
