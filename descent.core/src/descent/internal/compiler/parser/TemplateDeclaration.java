package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.IJavaElement;
import descent.core.compiler.IProblem;
import descent.internal.compiler.lookup.SemanticRest;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.MATCH.MATCHconvert;
import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;

import static descent.internal.compiler.parser.STC.STCconst;

import static descent.internal.compiler.parser.TY.Tdelegate;
import static descent.internal.compiler.parser.TY.Tfunction;
import static descent.internal.compiler.parser.TY.Tident;
import static descent.internal.compiler.parser.TY.Tvoid;


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

	public List<TemplateInstance> instances = new ArrayList<TemplateInstance>();
	
	private IJavaElement javaElement;
	
	public SemanticRest rest;

	public TemplateDeclaration(Loc loc, IdentifierExp id,
			TemplateParameters parameters, Dsymbols decldefs) {
		super(id);
		this.loc = loc;
		this.parameters = parameters;
		this.members = decldefs;
		if (decldefs != null){
			this.sourceMembers = new Dsymbols(decldefs);
		}
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, parameters);
			TreeVisitor.acceptChildren(visitor, members);
			
			acceptSynthetic(visitor);
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
			
			// Descent
			((AliasDeclaration) s).isTemplateParameter = true;
		} else if (sa != null) {
			s = new AliasDeclaration(Loc.ZERO, tp.ident, sa);
			
			// Descent
			((AliasDeclaration) s).isTemplateParameter = true;
		} else if (ea != null) {
			// tdtypes.data[i] always matches ea here
			Initializer init = new ExpInitializer(loc, ea);
			TemplateValueParameter tvp = tp.isTemplateValueParameter();
			if (tvp == null) {
				throw new IllegalStateException("assert(tvp);");
			}

			VarDeclaration v = new VarDeclaration(loc, tvp.valType,
					tp.ident, init);
			v.storage_class = STCconst;
			s = v;
		} else if (va != null) {
			s = new TupleDeclaration(loc, tp.ident, va.objects);
		} else {
			throw new IllegalStateException("assert(0);");
		}
		if (null == sc.insert(s)) {
			if (context.acceptsProblems()) {
				context.acceptProblem(Problem.newSemanticTypeErrorLoc(
						IProblem.DeclarationIsAlreadyDefined, s,
						new String[] { tp.ident.toChars(context) }));
			}
		}
		s.semantic(sc, context);
	}

	public FuncDeclaration deduceFunctionTemplate(Scope sc, Loc loc, Objects targsi, Expressions fargs, SemanticContext context) {

		MATCH m_best = MATCH.MATCHnomatch;
		TemplateDeclaration td_ambig = null;
		TemplateDeclaration td_best = null;
		Objects tdargs = new Objects();
		TemplateInstance ti;
		FuncDeclaration fd;

		for (TemplateDeclaration td = this; null != td; td = td.overnext) {
			if (null == td.scope) {
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.ForwardReferenceToTemplate, this,
							new String[] { td.toChars(context) }));
				}
				return Lerror(fargs, context);
			}
			if (null == td.onemember
					|| null == td.onemember.toAlias(context)
							.isFuncDeclaration()) {
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.SymbolIsNotAFunctionTemplate, this,
							new String[] { toChars(context) }));
				}
				return Lerror(fargs, context);
			}

			MATCH m;

			Objects dedargs = new Objects();
			m = td.deduceFunctionTemplateMatch(targsi, fargs, dedargs, context);

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

			if (c1 > c2) {
				// Ltd:
				td_ambig = null;
				continue;
			} else if (c1 < c2) {
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
			if (context.acceptsProblems()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.SymbolDoesNotMatchAnyTemplateDeclaration, this,
						new String[] { toChars(context) }));
			}
			return Lerror(fargs, context);
		}
		if (null != td_ambig) {
			if (context.acceptsProblems()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.SymbolMatchesMoreThanOneTemplateDeclaration, this,
						new String[] { toChars(context), td_best.toChars(context),
								td_ambig.toChars(context) }));
			}
		}

		/*
		 * The best match is td_best with arguments tdargs. Now instantiate the
		 * template.
		 */
		ti = new TemplateInstance(loc, td_best, tdargs, context.encoder);
		ti.semantic(sc, context);
		fd = ti.toAlias(context).isFuncDeclaration();
		if (null == fd) {
			return Lerror(fargs, context);
		}
		return fd;
	}

	private static class GotoL1 extends Exception {
		private static final long serialVersionUID = 1L;
	}
	private static final GotoL1 GOTO_L1 = new GotoL1();

	private static class GotoL2 extends Exception {
		private static final long serialVersionUID = 1L;
	}
	private static final GotoL2 GOTO_L2 = new GotoL2();

	public MATCH deduceFunctionTemplateMatch(Objects targsi, Expressions fargs,
			Objects dedargs, SemanticContext context) {
		int i;
		int nfparams;
		int nfargs;
		int nargsi;
		int fptupindex = -1;
		int tuple_dim = 0;
		MATCH match = MATCHexact;
		FuncDeclaration fd = onemember.toAlias(context).isFuncDeclaration();
		TypeFunction fdtype;
		TemplateTupleParameter tp;
		Objects dedtypes = new Objects(); // for T:T*, the dedargs is the T*,
		// dedtypes is the T

		// PERHAPS assert((int)scope > 0x10000);

		dedargs.setDim(parameters.size());
		dedargs.zero();

		dedtypes.setDim(parameters.size());
		dedtypes.zero();

		// Set up scope for parameters
		ScopeDsymbol paramsym = new ScopeDsymbol();
		paramsym.parent = scope.parent;
		Scope paramscope = scope.push(paramsym);
		
		tp = isVariadic();

		nargsi = 0;
		if (null != targsi) { // Set initial template arguments

			nargsi = targsi.size();
			if (nargsi > parameters.size()) {
				if (null == tp) {
					return Lnomatch(paramscope); // goto Lnomatch;
				}
				dedargs.setDim(nargsi);
				dedargs.zero();
			}

			//memcpy(dedargs.data, targsi.data, nargsi * sizeof(dedargs.data));
			for (i = 0; i < nargsi; i++) {
				dedargs.set(i, targsi.get(i));
			}

			for (i = 0; i < nargsi; i++) {
				TemplateParameter $tp = (TemplateParameter) parameters.get(i);
				MATCH m;
				Declaration[] sparam = new Declaration[1];

				m = $tp.matchArg(paramscope, dedargs, i, parameters, dedtypes,
						sparam, context);
				if (m == MATCHnomatch)
					return Lnomatch(paramscope); // goto Lnomatch;
				if (m.ordinal() < match.ordinal())
					match = m;

				sparam[0].semantic(paramscope, context);
				if (null == paramscope.insert(sparam[0]))
					return Lnomatch(paramscope); // goto Lnomatch;
			}
		}
		
		// Descent: lazy initialization
		fd.consumeRestStructure();
		fd.consumeRest();

		assert (fd.type.ty == Tfunction);
		fdtype = (TypeFunction) fd.type;
		
		nfparams = Argument.dim(fdtype.parameters, context); // number of
		// function
		// parameters
		nfargs = fargs.size(); // number of function arguments

		/*
		 * Check for match of function arguments with variadic template
		 * parameter, such as:
		 * 
		 * template Foo(T, A...) { void Foo(T t, A a); } void main() {
		 * Foo(1,2,3); }
		 */
		tp = isVariadic();
		try {
			if (null != tp) {
				if (nfparams == 0) // if no function parameters
				{
					Tuple t = new Tuple();
					// printf("t = %p\n", t);
					dedargs.set(parameters.size() - 1, t);
					throw GOTO_L2;
				} else if (nfargs < nfparams - 1) {
					throw GOTO_L1;
				} else {
				    /* 
				     * Figure out which of the function parameters matches
				     * the tuple template parameter. Do this by matching
				     * type identifiers.
				     * Set the index of this function parameter to fptupindex.
				     */
				    for (fptupindex = 0; fptupindex < nfparams; fptupindex++) {
						Argument fparam = (Argument) fdtype.parameters
								.get(fptupindex);
						if (fparam.type.ty != Tident) {
							continue;
						}
						TypeIdentifier tid = (TypeIdentifier) fparam.type;
	
						if (!equals(tp.ident, tid.ident)
								|| (tid.idents != null && tid.idents.size() > 0)) {
							continue;
						}
						if (fdtype.varargs > 0) // variadic function doesn't
							return Lnomatch(paramscope); // goto Lnomatch; // go
						// with
						// variadic template
	
						/* 
						 * The types of the function arguments
						 * now form the tuple argument.
						 */
						Tuple t = new Tuple();
						dedargs.set(parameters.size() - 1, t);
	
						tuple_dim = nfargs - (nfparams - 1);
						t.objects.setDim(tuple_dim);
						for (i = 0; i < tuple_dim; i++) {
							Expression farg = (Expression) fargs.get(fptupindex + 1);
							t.objects.set(i, farg.type);
						}
						throw GOTO_L2;
					}
				    fptupindex--;
				}
			}
			throw GOTO_L1;
		}
		// L1:
		catch (GotoL1 $) {
			if (nfparams == nfargs)
				;
			else if (nfargs > nfparams) {
				if (fdtype.varargs == 0)
					return Lnomatch(paramscope); // goto Lnomatch; // too
				// many
				// args,
				// no match
				match = MATCHconvert; // match ... with a conversion
			}
		} catch (GotoL2 $) {
			// Fallthrough
		}
		// L2:
		// Loop through the function parameters
		for (i = 0; i < nfparams; i++) {
			
			/* Skip over function parameters which wound up
			 * as part of a template tuple parameter.
			 */
			if (i == fptupindex) {
				if (fptupindex == nfparams - 1)
					break;
				i += tuple_dim - 1;
				continue;
			}
			
			Argument fparam = Argument.getNth(fdtype.parameters, i, context);
			Expression farg;
			MATCH m;

			if (i >= nfargs) // if not enough arguments
			{
				if (null != fparam.defaultArg) {
					/*
					 * Default arguments do not participate in template argument
					 * deduction.
					 */
					return Lmatch(nargsi, dedargs, dedtypes, paramscope, match,
							context); // goto
					// Lmatch;
				}
			} else {
				farg = (Expression) fargs.get(i);

				m = farg.type.deduceType(scope, fparam.type, parameters,
						dedtypes, context);
				// printf("\tdeduceType m = %d\n", m);

				/*
				 * If no match, see if there's a conversion to a delegate
				 */
				if (MATCHnomatch == m
						&& fparam.type.toBasetype(context).ty == Tdelegate) {
					TypeDelegate td = (TypeDelegate) fparam.type
							.toBasetype(context);
					TypeFunction tf = (TypeFunction) td.nextOf();

					if (tf.varargs == 0
							&& Argument.dim(tf.parameters, context) == 0) {
						m = farg.type.deduceType(scope, tf.nextOf(),
								parameters, dedtypes, context);
						if (MATCHnomatch == m
								&& tf.nextOf().toBasetype(context).ty == Tvoid)
							m = MATCHconvert;
					}
					// printf("\tm2 = %d\n", m);
				}

				if (m != MATCHnomatch) {
					if (m.ordinal() < match.ordinal())
						match = m; // pick worst match
					continue;
				}
			}
			if (!(fdtype.varargs == 2 && i + 1 == nfparams))
				return Lnomatch(paramscope); // goto Lnomatch;

			/*
			 * Check for match with function parameter T...
			 */
			Type t = fparam.type;
			switch (t.ty) {
			// Perhaps we can do better with this, see
			// TypeFunction.callMatch()
			case Tsarray:
			case Tarray:
			case Tclass:
			case Tident:
				return Lmatch(nargsi, dedargs, dedtypes, paramscope, match,
						context); // goto
				// Lmatch;

			default:
				return Lnomatch(paramscope); // goto Lnomatch;
			}
		}

		return Lmatch(nargsi, dedargs, dedtypes, paramscope, match, context);
	}

	// return Lmatch(nargsi, dedargs, dedtypes, paramscope, match, context);
	// Lmatch:
	private MATCH Lmatch(int nargsi, Objects dedargs, Objects dedtypes,
			Scope paramscope, MATCH match, SemanticContext context) {
		/* Fill in any missing arguments with their defaults.
		 */
		for (int i = nargsi; i < dedargs.size(); i++) {
			TemplateParameter tp = (TemplateParameter) parameters.get(i);
			ASTDmdNode oarg = (ASTDmdNode) dedargs.get(i);
			ASTDmdNode o = (ASTDmdNode) dedtypes.get(i);
			// printf("1dedargs[%d] = %p, dedtypes[%d] = %p\n", i, oarg, i, o);
			if (null == oarg) {
				if (null != o) {
					if (null != tp.specialization()) {
						if (context.acceptsProblems()) {
							context
									.acceptProblem(Problem
											.newSemanticTypeError(
													IProblem.SpecializationNotAllowedForDeducedParameter,
													this, new String[] { tp.ident
															.toChars() }));
						}
					}
				} else {
					o = tp.defaultArg(paramscope, context);
					if (null == o) {
						// goto Lnomatch;
						paramscope.pop();
						// printf("\tnomatch\n");
						return MATCHnomatch;
					}
				}
				declareParameter(paramscope, tp, o, context);
				dedargs.set(i, o);
			}
		}

		paramscope.pop();
		return match;
	}

	// return Lnomatch(paramscope);
	// Lnomatch:
	private MATCH Lnomatch(Scope paramscope) {
		//Lnomatch:
		paramscope.pop();
		//printf("\tnomatch\n");
		return MATCHnomatch;
	}

	@Override
	public int getNodeType() {
		return TEMPLATE_DECLARATION;
	}

	@Override
	public TemplateDeclaration isTemplateDeclaration() {
		consumeRest();
		
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

	public int leastAsSpecialized(TemplateDeclaration td2, SemanticContext context) {
		/*
		 * This works by taking the template parameters to this template
		 * declaration and feeding them to td2 as if it were a template
		 * instance. If it works, then this template is at least as specialized
		 * as td2.
		 */

		TemplateInstance ti = new TemplateInstance(Loc.ZERO, ident, context.encoder); // create
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
		// TODO semantic the source range is bad
		if (context.acceptsProblems()) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.CannotDeduceTemplateFunctionFromArgumentTypes, this,
					new String[] { buf.toChars() }));
		}
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

			if (context.isD2()) {
				// TODO Semantic fix this
				// m2 = tp.matchArg(paramscope, ti.tiargs, i, parameters, dedtypes, sparam, (flag & 2) != 0 ? 1 : 0, context);
				m2 = tp.matchArg(paramscope, ti.tiargs, i, parameters, dedtypes, sparam, context);
			} else {
				m2 = tp.matchArg(paramscope, ti.tiargs, i, parameters, dedtypes, sparam, context);
			}

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
		f.overprevious = beforePf;
		return true;
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (rest != null && !rest.isConsumed()) {
			if (rest.getScope() == null) {
				rest.setSemanticContext(sc, context);
			}
			return;
		}
		
		if (scope != null) {
			return; // semantic() already run
		}

		if (sc.func != null) {
			if (context.acceptsProblems()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.CannotDeclareTemplateAtFunctionScope, this,
						new String[] { sc.func.toChars(context) }));
			}
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
		ScopeDsymbol paramsym = new ScopeDsymbol();
		paramsym.parent = sc.parent;
		Scope paramscope = sc.push(paramsym);
		paramscope.parameterSpecialization = 1;

		for (int i = 0; i < parameters.size(); i++) {
			TemplateParameter tp = parameters.get(i);
			tp.declareParameter(paramscope, context);
		}

		for (int i = 0; i < parameters.size(); i++) {
			TemplateParameter tp = parameters.get(i);
			tp.semantic(paramscope, context);
			
			if (i + 1 != parameters.size() && tp.isTemplateTupleParameter() != null) {
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeError(IProblem.TemplateTupleParameterMustBeLastOne, tp));
				}
			}
		}

		paramscope.pop();

		if (members != null) {
			Dsymbol[] s = { null };
			if (Dsymbol.oneMembers(members, s, context)) {
				if (s[0] != null && s[0].ident != null
						&& equals(s[0].ident.ident, ident.ident)) {
					onemember = s[0];
					s[0].parent = this;
				}
			}
		}
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s, SemanticContext context) {
		consumeRestStructure();
		
		TemplateDeclaration td;
		TemplateParameters p;
		Dsymbols d;

		p = null;
		if (parameters != null) {
			p = new TemplateParameters();
			p.setDim(parameters.size());
			for (int i = 0; i < p.size(); i++) {
				TemplateParameter tp = parameters.get(i);
				p.set(i, tp.syntaxCopy(context));
			}
		}
		d = Dsymbol.arraySyntaxCopy(members, context);
		td = new TemplateDeclaration(loc, ident, p, d);
		td.copySourceRange(this);
		td.javaElement = javaElement;
		td.wrapper = wrapper;
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
		if (parameters != null) {
			for (int i = 0; i < parameters.size(); i++) {
				TemplateParameter tp = parameters.get(i);
				if (i != 0) {
					buf.writeByte(',');
				}
				tp.toCBuffer(buf, hgs, context);
			}
		}
		buf.writeByte(')');
		// buf.writeByte(0);
		return buf.extractData();
	}
	
	@Override
	public char getSignaturePrefix() {
		if (wrapper) {
			Dsymbol member = (Dsymbol) members.get(0);
			return member.getSignaturePrefix();
		} else {
			return ISignatureConstants.TEMPLATE;
		}
	}
	
	/**
	 * We can overload templates.
	 */
	public boolean isOverloadable() {
		return true;
	}
	
	public void setJavaElement(IJavaElement javaElement) {
		this.javaElement = javaElement;
	}
	
	@Override
	public IJavaElement getJavaElement() {
		return javaElement;
	}
	
	public void consumeRestStructure() {
		if (rest != null && !rest.isStructureKnown()) {
			rest.buildStructure();
		}
		if (wrapper && members.get(0) != null) {
			members.get(0).consumeRestStructure();
		}
	}
	
	public void consumeRest() {
		if (rest != null && !rest.isConsumed()) {
			rest.consume(this);
		}
	}

}
