package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

import static descent.internal.compiler.parser.TOK.TOKfunction;
import static descent.internal.compiler.parser.TOK.TOKtuple;
import static descent.internal.compiler.parser.TOK.TOKvar;

import static descent.internal.compiler.parser.TY.Ttuple;

// DMD 1.020
public class TemplateInstance extends ScopeDsymbol {

	public Objects tiargs;
	public TemplateDeclaration tempdecl; // referenced by foo.bar.abc
	public TemplateInstance inst; // refer to existing instance
	public AliasDeclaration aliasdecl; // != null if instance is an alias for its
	public int semanticdone; // has semantic() been done?
	public WithScopeSymbol withsym;
	public IdentifierExp name;
	public ScopeDsymbol argsym; // argument symbol table
	public Objects tdtypes; // Array of Types/Expressions corresponding
	public int havetempdecl; // 1 if used second constructor

	// to TemplateDeclaration.parameters
	// [int, char, 100]

	public TemplateInstance(Loc loc, IdentifierExp id) {
		super(loc);
		this.name = id;
	}

	public TemplateInstance(Loc loc, TemplateDeclaration td, Objects tiargs) {
		super(null);
		this.loc = loc;
		this.name = td.ident;
		this.tiargs = tiargs;
		this.tempdecl = td;
		this.havetempdecl = 1;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, tiargs);
		}
		visitor.endVisit(this);
	}

	@Override
	public Dsymbol toAlias(SemanticContext context) {
		if (inst == null) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.CannotResolveForwardReference, 0, start, length));
			return this;
		}

		if (inst != this)
			return inst.toAlias(context);

		if (aliasdecl != null)
			return aliasdecl.toAlias(context);

		return inst;
	}

	@Override
	public TemplateInstance isTemplateInstance() {
		return this;
	}

	@Override
	public int getNodeType() {
		return TEMPLATE_INSTANCE;
	}

	@Override
	public String toChars(SemanticContext context) {
		OutBuffer buf = new OutBuffer();
		HdrGenState hgs = new HdrGenState();
		toCBuffer(buf, hgs, context);
		String s = buf.toChars();
		buf.data = null;
		return s;
	}

	@Override
	public String mangle(SemanticContext context) {
		OutBuffer buf = new OutBuffer();
		String id;

		id = ident != null ? ident.toChars() : toChars(context);
		if (tempdecl.parent != null) {
			String p = tempdecl.parent.mangle(context);
			if (p.charAt(0) == '_' && p.charAt(1) == 'D')
				p += 2;
			buf.writestring(p);
		}
		// TODO semantic this was %zu . what's that?
		buf.writestring(id.length());
		buf.writestring(id);
		id = buf.toChars();
		buf.data = null;
		return id;
	}

	public void semanticTiargs(Scope sc, SemanticContext context) {
		semanticTiargs(loc, sc, tiargs, context);
	}

	public void semanticTiargs(Loc loc, Scope sc, Objects tiargs,
			SemanticContext context) {
		// Run semantic on each argument, place results in tiargs[]
		if (null == tiargs)
			return;
		for (int j = 0; j < tiargs.size(); j++) {
			ASTDmdNode o = (ASTDmdNode) tiargs.get(j);
			Type[] ta = { isType(o) };
			Expression[] ea = { isExpression(o) };
			Dsymbol[] sa = { isDsymbol(o) };

			if (ta != null) {
				// It might really be an Expression or an Alias
				ta[0].resolve(loc, sc, ea, ta, sa, context);
				if (ea != null) {
					ea[0] = ea[0].semantic(sc, context);
					ea[0] = ea[0].optimize(WANTvalue | WANTinterpret, context);
					tiargs.set(j, ea[0]);
				} else if (sa[0] != null) {
					tiargs.set(j, sa[0]);
					TupleDeclaration d = sa[0].toAlias(context)
							.isTupleDeclaration();
					if (d != null) {
						// int dim = d.objects.size();
						tiargs.remove(j);
						tiargs.addAll(j, d.objects);
						j--;
					}
				} else if (ta != null) {
					if (ta[0].ty == Ttuple) { // Expand tuple
						TypeTuple tt = (TypeTuple) ta[0];
						int dim = tt.arguments.size();
						tiargs.remove(j);
						if (dim != 0) {
							tiargs.ensureCapacity(dim);
							for (int i = 0; i < dim; i++) {
								Argument arg = (Argument) tt.arguments.get(i);
								tiargs.add(j + i, arg.type);
							}
						}
						j--;
					} else
						tiargs.add(j, ta[0]);
				} else {
					if (context.global.errors == 0) {
						throw new IllegalStateException(
								"assert(context.global.errors);");
					}
					tiargs.set(j, Type.terror);
				}
			} else if (ea[0] != null) {
				if (null == ea[0]) {
					if (context.global.errors == 0) {
						throw new IllegalStateException(
								"assert(context.global.errors);");
					}
					ea[0] = new IntegerExp(Loc.ZERO, 0);
				}
				if (ea[0] == null) {
					throw new IllegalStateException("assert(ea);");
				}
				ea[0] = ea[0].semantic(sc, context);
				ea[0] = ea[0].optimize(WANTvalue | WANTinterpret, context);
				tiargs.set(j, ea[0]);
			} else if (sa[0] != null) {
			} else {
				throw new IllegalStateException("assert (0);");
			}
		}
	}

	public TemplateDeclaration findBestMatch(Scope sc, SemanticContext context) {
		/* Since there can be multiple TemplateDeclaration's with the same
		 * name, look for the best match.
		 */
		TemplateDeclaration td_ambig = null;
		TemplateDeclaration td_best = null;
		MATCH m_best = MATCHnomatch;
		Objects dedtypes = new Objects();

		for (TemplateDeclaration td = tempdecl; td != null; td = td.overnext) {
			MATCH m;

			//	if (tiargs.dim) printf("2: tiargs.dim = %d, data[0] = %p\n", tiargs.dim, tiargs.data[0]);

			// If more arguments than parameters,
			// then this is no match.
			if (td.parameters.size() < tiargs.size()) {
				if (null == td.isVariadic())
					continue;
			}

			dedtypes.ensureCapacity(td.parameters.size());
			if (null == td.scope) {
				error("forward reference to template declaration %s", td
						.toChars(context));
				return null;
			}
			m = td.matchWithInstance(this, dedtypes, 0, context);
			if (null == m) // no match at all
				continue;

			if (m.ordinal() < m_best.ordinal()) {
				// goto Ltd_best;
				td_ambig = null;
				continue;
			}
			if (m.ordinal() > m_best.ordinal()) {
				// goto Ltd;
				td_ambig = null;
				td_best = td;
				m_best = m;
				tdtypes.ensureCapacity(dedtypes.size());
				for (ASTDmdNode a : dedtypes) {
					tdtypes.add(a);
				}
				continue;
			}
			{
				// Disambiguate by picking the most specialized TemplateDeclaration
				int c1 = td.leastAsSpecialized(td_best, context);
				int c2 = td_best.leastAsSpecialized(td, context);

				if (c1 != 0 && 0 == c2) {
					// goto Ltd;
					td_ambig = null;
					td_best = td;
					m_best = m;
					tdtypes.ensureCapacity(dedtypes.size());
					for (ASTDmdNode a : dedtypes) {
						tdtypes.add(a);
					}
					continue;
				} else if (0 == c1 && c2 != 0) {
					// goto Ltd_best;
					td_ambig = null;
					continue;
				} else {
					// goto Lambig;
					td_ambig = td;
					continue;
				}
			}
		}

		if (null == td_best) {
			error("%s does not match any template declaration",
					toChars(context));
			return null;
		}
		if (td_ambig != null) {
			error("%s matches more than one template declaration, %s and %s",
					toChars(context), td_best.toChars(context), td_ambig
							.toChars(context));
		}

		/* The best match is td_best
		 */
		tempdecl = td_best;
		return tempdecl;
	}

	public void declareParameters(Scope scope, SemanticContext context) {
		for (int i = 0; i < tdtypes.size(); i++) {
			TemplateParameter tp = (TemplateParameter) tempdecl.parameters
					.get(i);
			//Object o = (Object )tiargs.data[i];
			ASTDmdNode o = (ASTDmdNode) tdtypes.get(i);

			tempdecl.declareParameter(scope, tp, o, context);
		}
	}

	public IdentifierExp genIdent(SemanticContext context) {
		OutBuffer buf = new OutBuffer();
		String id;
		Objects args;

		id = tempdecl.ident.toChars(context);
		// TODO semantic
		// buf.printf("__T%zu%s", id.length(), id);
		args = tiargs;
		for (int i = 0; i < args.size(); i++) {
			ASTDmdNode o = (ASTDmdNode) args.get(i);
			Type ta = isType(o);
			Expression ea = isExpression(o);
			Dsymbol sa = isDsymbol(o);
			Tuple va = isTuple(o);
			if (ta != null) {
				buf.writeByte('T');
				if (ta.deco != null)
					buf.writestring(ta.deco);
				else {
					if (context.global.errors == 0) {
						throw new IllegalStateException(
								"assert(context.global.errors);");
					}
				}
			} else if (ea != null) {
				sinteger_t v;
				real_t r;
				char p;

				if (ea.op == TOKvar) {
					sa = ((VarExp) ea).var;
					ea = null;
					// goto Lsa;
					buf.writeByte('S');
					Declaration d = sa.isDeclaration();
					if (d != null && null == d.type.deco)
						error("forward reference of %s", d.toChars(context));
					else {
						String p2 = sa.mangle(context);
						// TODO semantic
						// buf.printf("%zu%s", strlen(p2), p2);
					}
				}
				if (ea.op == TOKfunction) {
					sa = ((FuncExp) ea).fd;
					ea = null;
					// goto Lsa;
					buf.writeByte('S');
					Declaration d = sa.isDeclaration();
					if (d != null && null == d.type.deco)
						error("forward reference of %s", d.toChars(context));
					else {
						String p2 = sa.mangle(context);
						// TODO semantic
						// buf.printf("%zu%s", strlen(p2), p2);
					}
				}
				buf.writeByte('V');
				if (ea.op == TOKtuple) {
					ea.error("tuple is not a valid template value argument");
					continue;
				}
				buf.writestring(ea.type.deco);
				ea.toMangleBuffer(buf, context);
			} else if (sa != null) {
				// Lsa: 
				buf.writeByte('S');
				Declaration d = sa.isDeclaration();
				if (d != null && null == d.type.deco)
					error("forward reference of %s", d.toChars(context));
				else {
					String p = sa.mangle(context);
					// TODO semantic
					// buf.printf("%zu%s", strlen(p), p);
				}
			} else if (va != null) {
				assert (i + 1 == args.size()); // must be last one
				args = /* & */va.objects;
				i = -1;
			} else
				throw new IllegalStateException("assert(0);");
		}
		buf.writeByte('Z');
		id = buf.toChars();
		buf.data = null;
		return new IdentifierExp(id.toCharArray());
	}

	public Objects arraySyntaxCopy(Objects objs) {
		Objects a = null;
		if (objs != null) {
			a = new Objects();
			a.ensureCapacity(objs.size());
			for (int i = 0; i < objs.size(); i++) {
				Type ta = isType((ASTDmdNode) objs.get(i));
				if (ta != null)
					a.set(i, ta.syntaxCopy());
				else {
					Expression ea = isExpression((ASTDmdNode) objs.get(i));
					if (ea == null) {
						throw new IllegalStateException("assert(ea);");
					}
					a.set(i, ea.syntaxCopy());
				}
			}
		}
		return a;
	}

	public Dsymbol syntaxCopy(Dsymbol s) {
		TemplateInstance ti;
		// int i;

		if (s != null)
			ti = (TemplateInstance) s;
		else
			ti = new TemplateInstance(loc, name);

		ti.tiargs = arraySyntaxCopy(tiargs);

		super.syntaxCopy(ti);
		return ti;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		super.semantic(sc, context);
	}
	
}