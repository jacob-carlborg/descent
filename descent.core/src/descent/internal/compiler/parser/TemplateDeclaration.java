package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

import static descent.internal.compiler.parser.STC.STCconst;

// DMD 1.020
public class TemplateDeclaration extends ScopeDsymbol {

	public static TemplateTupleParameter isVariadic(
			TemplateParameters parameters) {
		int dim = parameters.size();
		TemplateTupleParameter tp = null;

		if (dim != 0) {
			tp = (parameters.get(dim - 1)).isTemplateTupleParameter();
		}
		return tp;
	}
	// Wether this template declaration is just a wrapper for "class B(T) ..."
	public boolean wrapper;
	public TemplateParameters parameters;
	public Scope scope;
	public Dsymbol onemember;
											public TemplateDeclaration overnext; // next overloaded
	// TemplateDeclaration
	public TemplateDeclaration overroot; // first in overnext list

	List<TemplateInstance> instances = new ArrayList<TemplateInstance>();

	public TemplateDeclaration(Loc loc, IdentifierExp id,
			TemplateParameters parameters, Dsymbols decldefs) {
		super(loc, id);
		this.parameters = parameters;
		this.members = decldefs;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, parameters);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}

	public void declareParameter(Scope sc, TemplateParameter tp, ASTDmdNode o,
			SemanticContext context) {
		Type targ = isType(o);
		Expression ea = isExpression(o);
		Dsymbol sa = isDsymbol(o);
		Tuple va = isTuple(o);

		Dsymbol s;

		if (targ != null) {
			s = new AliasDeclaration(Loc.ZERO, tp.ident, targ);
		} else if (sa != null) {
			s = new AliasDeclaration(Loc.ZERO, tp.ident, sa);
		} else if (ea != null) {
			// tdtypes.data[i] always matches ea here
			Initializer init = new ExpInitializer(loc, ea);
			TemplateValueParameter tvp = tp.isTemplateValueParameter();
			if (tvp == null) {
				throw new IllegalStateException("assert(tvp);");
			}

			VarDeclaration v = new VarDeclaration(Loc.ZERO, tvp.valType,
					tp.ident, init);
			v.storage_class = STCconst;
			s = v;
		} else if (va != null) {
			s = new TupleDeclaration(loc, tp.ident, va.objects);
		} else {
			throw new IllegalStateException("assert(0);");
		}
		if (null == sc.insert(s)) {
			context.acceptProblem(Problem.newSyntaxError(IProblem.DeclarationIsAlreadyDefined, 0, s.start, s.length, new String[] { tp.ident.toChars(context) } ));
		}
		s.semantic(sc, context);
	}

	public FuncDeclaration deduce(Scope sc, Loc loc, Objects targsi,
			Expressions fargs, SemanticContext context) {

		MATCH m_best = MATCH.MATCHnomatch;
		TemplateDeclaration td_ambig = null;
		TemplateDeclaration td_best = null;
		Objects tdargs = new Objects();
		TemplateInstance ti;
		FuncDeclaration fd;

		for (TemplateDeclaration td = this; null != td; td = td.overnext) {
			if (null == td.scope) {
				error("forward reference to template %s", td.toChars(context));
				return Lerror(fargs, context);
			}
			if (null == td.onemember
					|| null == td.onemember.toAlias(context)
							.isFuncDeclaration()) {
				error("is not a function template");
				return Lerror(fargs, context);
			}

			MATCH m;

			Objects[] dedargs_ = new Objects[] { null }; // Pass a
															// one-element array
															// to get reference
															// semantics
			m = MATCH.MATCHnomatch; /* td.deduceMatch(targsi, fargs, dedargs_); */
			Objects dedargs = dedargs_[0];

			if (m == MATCH.MATCHnomatch) {
				continue;
			} else if (m.ordinal() < m_best.ordinal()) {
				// Ltd:
				td_ambig = null;
				continue;
			} else if (m.ordinal() > m_best.ordinal()) {
				// Ltd_best:
				td_ambig = null;
				td_best = td;
				m_best = m;
				tdargs.memcpy(dedargs);
				continue;
			}

			assert (m.ordinal() == m_best.ordinal());

			// Disambiguate by picking the most specialized TemplateDeclaration
			int c1 = 0;
			td.leastAsSpecialized(td_best, context);
			int c2 = 0;
			td_best.leastAsSpecialized(td, context);

			if (0 != c1 && 0 == c2) {
				// Ltd:
				td_ambig = null;
				continue;
			} else if (0 == c1 && 0 != c2) {
				// Ltd_best:
				td_ambig = null;
				td_best = td;
				m_best = m;
				tdargs = new Objects(dedargs);
				continue;
			} else {
				// Lambig:
				td_ambig = td;
				continue;
			}
		}

		if (null == td_best) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.SymbolDoesNotMatchAnyTemplateDeclaration, 0,
					start, length, new String[] { toChars(context) }));
			return Lerror(fargs, context);
		}
		if (null != td_ambig) {
			error(
					"%s matches more than one function template declaration, %s and %s",
					toChars(context), td_best.toChars(context), td_ambig
							.toChars(context));
		}

		/*
		 * The best match is td_best with arguments tdargs. Now instantiate the
		 * template.
		 */
		ti = new TemplateInstance(loc, td_best, tdargs);
		ti.semantic(sc, context);
		fd = ti.toAlias(context).isFuncDeclaration();
		if (null == fd) {
			return Lerror(fargs, context);
		}
		return fd;
	}

	public MATCH deduceMatch(Objects targsi, Expressions fargs,
			Objects dedargs, SemanticContext context) {
		throw new IllegalStateException("Not implemented!");
		// TODO semantic
		// return null;
	}

	@Override
	public int getNodeType() {
		return TEMPLATE_DECLARATION;
	}

	@Override
	public TemplateDeclaration isTemplateDeclaration() {
		return this;
	}

	public TemplateTupleParameter isVariadic() {
		return isVariadic(parameters);
	}

	@Override
	public String kind() {
		return (onemember != null && onemember.isAggregateDeclaration() != null) ? onemember
				.kind()
				: "template";
	}

	public int leastAsSpecialized(TemplateDeclaration td2,
			SemanticContext context) {
		/*
		 * This works by taking the template parameters to this template
		 * declaration and feeding them to td2 as if it were a template
		 * instance. If it works, then this template is at least as specialized
		 * as td2.
		 */

		TemplateInstance ti = new TemplateInstance(Loc.ZERO, ident); // create
																		// dummy
																		// template
																		// instance
		Objects dedtypes = new Objects();

		// Set type arguments to dummy template instance to be types
		// generated from the parameters to this template declaration
		if (ti.tiargs == null) {
			ti.tiargs = new Objects();
		}
		ti.tiargs.setDim(size(parameters));

		for (int i = 0; i < size(ti.tiargs); i++) {
			TemplateParameter tp = parameters.get(i);

			ASTDmdNode p = tp.dummyArg(context);
			if (p != null) {
				ti.tiargs.set(i, p);
			} else {
				ti.tiargs.setDim(i);
			}
		}

		// Temporary Array to hold deduced types
		dedtypes.setDim(size(td2.parameters));

		// Attempt a type deduction
		if (td2.matchWithInstance(ti, dedtypes, 1, context) != MATCHnomatch) {
			/*
			 * A non-variadic template is more specialized than a variadic one.
			 */
			if (isVariadic() != null && null == td2.isVariadic()) {
				// goto L1;
				return 0;
			}

			return 1;
		}
		// L1:
		return 0;
	}

	// Lerror:
	private FuncDeclaration Lerror(Expressions fargs, SemanticContext context) {
		OutBuffer buf = new OutBuffer();
		HdrGenState hgs = new HdrGenState();

		argExpTypesToCBuffer(buf, fargs, hgs, context);
		error("cannot deduce template function from argument types (%s)", buf
				.toChars());
		return null;
	}

	public MATCH matchWithInstance(TemplateInstance ti, Objects dedtypes,
			int flag, SemanticContext context) {
		MATCH m;
		int dedtypes_dim = size(dedtypes);

		dedtypes.zero();

		int parameters_dim = size(parameters);
		boolean variadic = isVariadic() != null;

		// If more arguments than parameters, no match
		if (size(ti.tiargs) > parameters_dim && !variadic) {
			return MATCHnomatch;
		}

		if (!(dedtypes_dim == parameters_dim)) {
			throw new IllegalStateException(
					"assert(dedtypes_dim == parameters_dim);");
		}

		if (!(dedtypes_dim >= size(ti.tiargs) || variadic)) {
			throw new IllegalStateException(
					"assert(dedtypes_dim >= size(ti.tiargs) || variadic);");
		}

		// Set up scope for parameters
		// assert((size_t)scope > 0x10000);
		ScopeDsymbol paramsym = new ScopeDsymbol();
		paramsym.parent = scope.parent;
		Scope paramscope = scope.push(paramsym);

		// Attempt type deduction
		m = MATCHexact;
		for (int i = 0; i < dedtypes_dim; i++) {
			MATCH m2;
			TemplateParameter tp = parameters.get(i);
			Declaration[] sparam = { null };

			m2 = tp.matchArg(paramscope, ti.tiargs, i, parameters, dedtypes,
					sparam, context);

			if (m2 == MATCHnomatch) {
				// goto Lnomatch;
				m = MATCHnomatch;
				paramscope.pop();
				return m;
			}

			if (m2.ordinal() < m.ordinal()) {
				m = m2;
			}

			if (0 == flag) {
				sparam[0].semantic(paramscope, context);
			}
			if (null == paramscope.insert(sparam[0])) {
				// goto Lnomatch;
				m = MATCHnomatch;
				paramscope.pop();
				return m;
			}
		}

		if (0 == flag) {
			// Any parameter left without a type gets the type of its
			// corresponding arg
			for (int i = 0; i < dedtypes_dim; i++) {
				if (null == dedtypes.get(i)) {
					if (!(i < size(ti.tiargs))) {
						throw new IllegalStateException(
								"assert(i < size(ti.tiargs));");
					}
					dedtypes.set(i, ti.tiargs.get(i));
				}
			}
		}

		// goto Lret;
		paramscope.pop();
		return m;
	}

	@Override
	public boolean overloadInsert(Dsymbol s, SemanticContext context) {
		TemplateDeclaration pf;
		TemplateDeclaration f;

		f = s.isTemplateDeclaration();
		if (null == f) {
			return false;
		}
		TemplateDeclaration pthis = this;
		TemplateDeclaration beforePf = null;
		for (pf = pthis; pf != null; pf = pf.overnext) {
			beforePf = pf;
		}

		f.overroot = this;

		if (beforePf == null) {
			throw new IllegalStateException("assert(beforeBf)");
		}

		beforePf.overnext = f;
		return true;
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (scope != null) {
			return; // semantic() already run
		}

		if (sc.func != null) {
			error("cannot declare template at function scope %s", sc.func
					.toChars(context));
		}

		if (context.global.params.useArrayBounds && sc.module != null) {
			// Generate this function as it may be used
			// when template is instantiated in other modules
			sc.module.toModuleArray();
		}

		if (context.global.params.useAssert && sc.module != null) {
			// Generate this function as it may be used
			// when template is instantiated in other modules
			sc.module.toModuleAssert();
		}

		/*
		 * Remember Scope for later instantiations, but make a copy since
		 * attributes can change.
		 */
		this.scope = new Scope(sc, context);
		this.scope.setNoFree();

		// Set up scope for parameters
		ScopeDsymbol paramsym = new ScopeDsymbol(loc);
		paramsym.parent = sc.parent;
		Scope paramscope = sc.push(paramsym);
		paramscope.parameterSpecialization = 1;

		for (int i = 0; i < parameters.size(); i++) {
			TemplateParameter tp = parameters.get(i);
			tp.declareParameter(paramscope, context);
		}

		for (TemplateParameter tp : parameters) {
			tp.semantic(paramscope, context);
		}

		paramscope.pop();

		if (members != null) {
			Dsymbol[] s = { null };
			if (Dsymbol.oneMembers(members, s, context)) {
				if (s[0] != null && s[0].ident != null
						&& s[0].ident.ident.equals(ident.ident)) {
					onemember = s[0];
					s[0].parent = this;
				}
			}
		}
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		TemplateDeclaration td;
		TemplateParameters p;
		Dsymbols d;

		p = null;
		if (parameters != null) {
			p = new TemplateParameters();
			p.setDim(parameters.size());
			for (int i = 0; i < p.size(); i++) {
				TemplateParameter tp = parameters.get(i);
				p.set(i, tp.syntaxCopy());
			}
		}
		d = Dsymbol.arraySyntaxCopy(members);
		td = new TemplateDeclaration(loc, ident, p, d);
		return td;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring(kind());
		buf.writeByte(' ');
		buf.writestring(ident.toChars());
		buf.writeByte('(');
		for (int i = 0; i < parameters.size(); i++) {
			TemplateParameter tp = parameters.get(i);
			if (i > 0) {
				buf.writeByte(',');
			}
			tp.toCBuffer(buf, hgs, context);
		}
		buf.writeByte(')');

		if (hgs.hdrgen) {
			hgs.tpltMember = true;
			buf.writenl();
			buf.writebyte('{');
			buf.writenl();
			for (int i = 0; i < members.size(); i++) {
				Dsymbol s = members.get(i);
				s.toCBuffer(buf, hgs, context);
			}
			buf.writebyte('}');
			buf.writenl();
			hgs.tpltMember = false;
		}
	}

	@Override
	public String toChars(SemanticContext context) {
		OutBuffer buf = new OutBuffer();
		HdrGenState hgs = new HdrGenState();

		buf.writestring(ident.toChars());
		buf.writeByte('(');
		for (int i = 0; i < parameters.size(); i++) {
			TemplateParameter tp = parameters.get(i);
			if (i != 0) {
				buf.writeByte(',');
			}
			tp.toCBuffer(buf, hgs, context);
		}
		buf.writeByte(')');
		// buf.writeByte(0);
		return buf.extractData();
	}

}
