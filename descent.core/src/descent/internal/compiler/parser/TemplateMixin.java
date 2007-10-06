package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.PROT.PROTpublic;

// DMD 1.020
public class TemplateMixin extends TemplateInstance {

	public Identifiers idents;
	public Type tqual;
	public Scope scope;
	
	public int typeStart;
	public int typeLength;

	public TemplateMixin(Loc loc, IdentifierExp ident, Type tqual,
			Identifiers idents, Objects tiargs) {
		super(loc, idents.get(idents.size() - 1));
		this.ident = ident;
		this.tqual = tqual;
		this.idents = idents;
		this.tiargs = tiargs != null ? tiargs : new Objects(0);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, idents);
			TreeVisitor.acceptChildren(visitor, tiargs);
			TreeVisitor.acceptChildren(visitor, ident);
		}
		visitor.endVisit(this);
	}

	@Override
	public int getNodeType() {
		return TEMPLATE_MIXIN;
	}

	@Override
	public boolean hasPointers(SemanticContext context) {
		for (int i = 0; i < members.size(); i++) {
			Dsymbol s = members.get(i);
			if (s.hasPointers(context)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void inlineScan(SemanticContext context) {
		super.inlineScan(context);
	}

	@Override
	public TemplateMixin isTemplateMixin() {
		return this;
	}

	@Override
	public String kind() {
		return "mixin";
	}

	@Override
	public boolean oneMember(Dsymbol[] ps, SemanticContext context) {
		return super.oneMember(ps, context);
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (semanticdone != 0 &&
		// This for when a class/struct contains mixin members, and
				// is done over because of forward references
				(null == parent || null == toParent().isAggregateDeclaration())) {
			return;
		}
		if (0 == semanticdone) {
			semanticdone = 1;
		}

		Scope scx = null;
		if (scope != null) {
			sc = scope;
			scx = scope; // save so we don't make redundant copies
			scope = null;
		}

		// Follow qualifications to find the TemplateDeclaration
		if (null == tempdecl) {
			Dsymbol s;
			int i;
			IdentifierExp id;

			if (tqual != null) {
				s = tqual.toDsymbol(sc, context);
				i = 0;
			} else {
				i = 1;
				id = idents.get(0);
				switch (id.dyncast()) {
				case DYNCAST_IDENTIFIER:
					s = sc.search(loc, id, null, context);
					break;

				case DYNCAST_DSYMBOL: {
					TemplateInstance ti = ((TemplateInstanceWrapper) id).tempinst;
					ti.semantic(sc, context);
					s = ti;
					break;
				}
				default:
					throw new IllegalStateException("assert(0);");
				}
			}

			for (; i < idents.size(); i++) {
				if (null == s) {
					break;
				}
				id = idents.get(i);
				s = s.searchX(loc, sc, id, context);
			}
			if (null == s) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.SymbolNotDefined, 0, typeStart, typeLength, new String[] { toChars(context) }));
				inst = this;
				return;
			}
			tempdecl = s.toAlias(context).isTemplateDeclaration();
			if (null == tempdecl) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.SymbolNotATemplate, 0, typeStart, typeLength, new String[] { s.toChars(context) }));
				inst = this;
				return;
			}
		}

		// Look for forward reference
		if (tempdecl == null) {
			throw new IllegalStateException("assert(tempdecl);");
		}
		for (TemplateDeclaration td = tempdecl; td != null; td = td.overnext) {
			if (null == td.scope) {
				/* Cannot handle forward references if mixin is a struct member,
				 * because addField must happen during struct's semantic, not
				 * during the mixin semantic.
				 * runDeferred will re-run mixin's semantic outside of the struct's
				 * semantic.
				 */
				semanticdone = 0;
				AggregateDeclaration ad = toParent().isAggregateDeclaration();
				if (ad != null) {
					ad.sizeok = 2;
				} else {
					// Forward reference
					scope = scx != null ? scx : new Scope(sc, context);
					scope.setNoFree();
					scope.module.addDeferredSemantic(this);
				}
				return;
			}
		}

		// Run semantic on each argument, place results in tiargs[]
		semanticTiargs(sc, context);

		tempdecl = findBestMatch(sc, context);
		if (null == tempdecl) {
			inst = this;
			return; // error recovery
		}

		if (null == ident) {
			ident = genIdent(context);
		}

		inst = this;
		parent = sc.parent;

		/* Detect recursive mixin instantiations.
		 */
		Lcontinue: for (Dsymbol s = parent; s != null; s = s.parent) {
			TemplateMixin tm = s.isTemplateMixin();
			if (null == tm || tempdecl != tm.tempdecl) {
				continue;
			}

			for (int i = 0; i < tiargs.size(); i++) {
				ASTDmdNode o = tiargs.get(i);
				Type ta = isType(o);
				Expression ea = isExpression(o);
				Dsymbol sa = isDsymbol(o);
				ASTDmdNode tmo = tm.tiargs.get(i);
				if (ta != null) {
					Type tmta = isType(tmo);
					if (null == tmta) {
						// goto Lcontinue;
						continue Lcontinue;
					}
					if (!ta.equals(tmta)) {
						// goto Lcontinue;
						continue Lcontinue;
					}
				} else if (ea != null) {
					Expression tme = isExpression(tmo);
					if (null == tme || !ea.equals(tme)) {
						// goto Lcontinue;
						continue Lcontinue;
					}
				} else if (sa != null) {
					Dsymbol tmsa = isDsymbol(tmo);
					if (sa != tmsa) {
						// goto Lcontinue;
						continue Lcontinue;
					}
				} else {
					throw new IllegalStateException("assert(0);");
				}
			}
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.RecursiveMixinInstantiation, 0, start, length));
			return;
		}

		// Copy the syntax trees from the TemplateDeclaration
		members = Dsymbol.arraySyntaxCopy(tempdecl.members);
		if (null == members) {
			return;
		}

		symtab = new DsymbolTable();

		for (Scope sce = sc; true; sce = sce.enclosing) {
			ScopeDsymbol sds = sce.scopesym;
			if (sds != null) {
				sds.importScope(this, PROTpublic);
				break;
			}
		}

		Scope scy = sc;
		scy = sc.push(this);
		scy.parent = this;

		argsym = new ScopeDsymbol();
		argsym.parent = scy.parent;
		Scope scope = scy.push(argsym);

		// Declare each template parameter as an alias for the argument type
		declareParameters(scope, context);

		// Add members to enclosing scope, as well as this scope
		for (int i = 0; i < members.size(); i++) {
			Dsymbol s;

			s = members.get(i);
			s.addMember(scope, this, i, context);
			//sc.insert(s);
			//printf("sc.parent = %p, sc.scopesym = %p\n", sc.parent, sc.scopesym);
			//printf("s.parent = %s\n", s.parent.toChars());
		}

		// Do semantic() analysis on template instance members
		Scope sc2;
		sc2 = scope.push(this);
		sc2.offset = sc.offset;
		for (int i = 0; i < members.size(); i++) {
			Dsymbol s = members.get(i);
			s.semantic(sc2, context);
		}
		sc.offset = sc2.offset;

		/* The problem is when to parse the initializer for a variable.
		 * Perhaps VarDeclaration::semantic() should do it like it does
		 * for initializers inside a function.
		 */
		//	    if (sc.parent.isFuncDeclaration())
		semantic2(sc2, context);

		if (sc.func != null) {
			semantic3(sc2, context);
		}

		sc2.pop();

		scope.pop();

		//	    if (!isAnonymous())
		{
			scy.pop();
		}
	}

	@Override
	public void semantic2(Scope sc, SemanticContext context) {
		int i;

		if (semanticdone >= 2) {
			return;
		}
		semanticdone = 2;
		if (members != null) {
			if (sc == null) {
				throw new IllegalStateException("assert (sc);");
			}
			sc = sc.push(argsym);
			sc = sc.push(this);
			for (i = 0; i < members.size(); i++) {
				Dsymbol s = members.get(i);
				s.semantic2(sc, context);
			}
			sc = sc.pop();
			sc.pop();
		}
	}

	@Override
	public void semantic3(Scope sc, SemanticContext context) {
		int i;

		if (semanticdone >= 3) {
			return;
		}
		semanticdone = 3;
		if (members != null) {
			sc = sc.push(argsym);
			sc = sc.push(this);
			for (i = 0; i < members.size(); i++) {
				Dsymbol s = members.get(i);
				s.semantic3(sc, context);
			}
			sc = sc.pop();
			sc.pop();
		}
	}

	public void setTypeSourceRange(int start, int length) {
		this.typeStart = start;
		this.typeLength = length;
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		TemplateMixin tm;

		Identifiers ids = new Identifiers();
		ids.setDim(idents.size());
		for (int i = 0; i < idents.size(); i++) { // Matches TypeQualified::syntaxCopyHelper()
			IdentifierExp id = idents.get(i);
			if (id.dyncast() == DYNCAST.DYNCAST_DSYMBOL) {
				TemplateInstance ti = ((TemplateInstanceWrapper) id).tempinst;

				ti = (TemplateInstance) ti.syntaxCopy(null);
				id = new TemplateInstanceWrapper(Loc.ZERO, ti);
			}
			ids.set(i, id);
		}

		tm = new TemplateMixin(loc, ident, (tqual != null ? tqual.syntaxCopy()
				: null), ids, tiargs);
		super.syntaxCopy(tm);
		return tm;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring("mixin ");
		int i;
		for (i = 0; i < idents.size(); i++) {
			IdentifierExp id = idents.get(i);

			if (i != 0) {
				buf.writeByte('.');
			}
			buf.writestring(id.toChars());
		}
		buf.writestring("!(");
		if (tiargs != null) {
			for (i = 0; i < tiargs.size(); i++) {
				if (i != 0) {
					buf.writebyte(',');
				}
				ASTDmdNode oarg = tiargs.get(i);
				Type t = isType(oarg);
				Expression e = isExpression(oarg);
				Dsymbol s = isDsymbol(oarg);
				if (t != null) {
					t.toCBuffer(buf, null, hgs, context);
				} else if (e != null) {
					e.toCBuffer(buf, hgs, context);
				} else if (s != null) {
					String p = s.ident != null ? s.ident.toChars() : s
							.toChars(context);
					buf.writestring(p);
				} else if (null == oarg) {
					buf.writestring("NULL");
				} else {
					throw new IllegalStateException("assert(0);");
				}
			}
		}
		buf.writebyte(')');
		buf.writebyte(';');
		buf.writenl();
	}

	@Override
	public String toChars(SemanticContext context) {
		OutBuffer buf = new OutBuffer();
		HdrGenState hgs = new HdrGenState();

		super.toCBuffer(buf, hgs, context);
		String s = buf.toChars();
		buf.data = null;
		return s;
	}

}
