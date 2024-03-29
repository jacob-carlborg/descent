package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.MATCH.MATCHnomatch;
import static descent.internal.compiler.parser.STC.STCmanifest;
import static descent.internal.compiler.parser.TOK.TOKfunction;
import static descent.internal.compiler.parser.TOK.TOKtuple;
import static descent.internal.compiler.parser.TOK.TOKtype;
import static descent.internal.compiler.parser.TOK.TOKvar;
import static descent.internal.compiler.parser.TY.Ttuple;

import java.util.List;

import melnorme.utilbox.tree.TreeVisitor;
import descent.core.Signature;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;


public class TemplateInstance extends ScopeDsymbol {

	public Objects tiargs, sourceTiargs;
	public TemplateDeclaration tempdecl; // referenced by foo.bar.abc
	public TemplateInstance inst; // refer to existing instance
    public TemplateInstance tinst;		// enclosing template instance
	public AliasDeclaration aliasdecl; // != null if instance is an alias for its
	public int semanticdone; // has semantic() been done?
	public WithScopeSymbol withsym;
	public IdentifierExp name;
	public ScopeDsymbol argsym; // argument symbol table
	public Objects tdtypes = new Objects(3); // Array of Types/Expressions corresponding
	public int havetempdecl; // 1 if used second constructor
	public Dsymbol isnested; // if referencing local symbols, this is the context
	public boolean nest; // For recursion detection
	public int errors; // 1 if compiled with errors
	public boolean semantictiargsdone;
	
	// Descent: to improve performance, must be set by Parser or ModuleBuilder
	public ASTNodeEncoder encoder;
	 
	// to TemplateDeclaration.parameters
	// [int, char, 100]

	public TemplateInstance(char[] filename, int lineNumber, IdentifierExp id, ASTNodeEncoder encoder) {
		super(null);
		this.filename = filename;
		this.lineNumber = lineNumber;
		this.name = id;
		this.semantictiargsdone = false;
		this.encoder = encoder;
	}

	/*****************
	 * This constructor is only called when we figured out which function
	 * template to instantiate.
	 */
	public TemplateInstance(char[] filename, int lineNumber, TemplateDeclaration td, Objects tiargs, ASTNodeEncoder encoder) {
		super(null);
		this.filename = filename;
		this.lineNumber = lineNumber;
		this.name = td.ident;
		tiargs(tiargs);
		this.tempdecl = td;
		this.havetempdecl = 1;
		this.semantictiargsdone = true;
		this.encoder = encoder;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, name);
			TreeVisitor.acceptChildren(visitor, sourceTiargs);
		}
		visitor.endVisit(this);
	}

	public static Objects arraySyntaxCopy(Objects objs, SemanticContext context) {
		Objects a = null;
		if (objs != null) {
			a = new Objects(objs.size());
			a.setDim(objs.size());
			if (context.isD1()) {
				for (int i = 0; i < objs.size(); i++) {
					Type ta = isType(objs.get(i));
					if (ta != null) {
						a.set(i, ta.syntaxCopy(context));
					} else {
						Expression ea = isExpression(objs.get(i));
						if (ea == null) {
							throw new IllegalStateException("assert(ea);");
						}
						a.set(i, ea.syntaxCopy(context));
					}
				}
			} else {
				for (int i = 0; i < objs.size(); i++) {
				    a.set(i, objectSyntaxCopy(objs.get(i), context));
				}
			}
		}
		return a;
	}

	public void declareParameters(Scope scope, SemanticContext context) {
		for (int i = 0; i < size(tdtypes); i++) {
			TemplateParameter tp = tempdecl.parameters.get(i);
			ASTDmdNode o = tdtypes.get(i);

			tempdecl.declareParameter(scope, tp, o, context);
		}
	}

	public TemplateDeclaration findBestMatch(Scope sc, SemanticContext context) {
		/* Since there can be multiple TemplateDeclaration's with the same
		 * name, look for the best match.
		 */
		TemplateDeclaration td_ambig = null;
		TemplateDeclaration td_best = null;
		MATCH m_best = MATCHnomatch;
		Objects dedtypes = new Objects(3);

		for (TemplateDeclaration td = tempdecl; td != null; td = td.overnext) {
			MATCH m;

			// If more arguments than parameters,
			// then this is no match.
			if (size(td.parameters) < size(tiargs)) {
				if (null == td.isVariadic()) {
					continue;
				}
			}

			dedtypes.setDim(size(td.parameters));
			dedtypes.zero();
			
			if (null == td.scope) {
				if (context.acceptsErrors()) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.ForwardReferenceToTemplateDeclaration, this, new String[] { td.toChars(context) }));
				}
				return null;
			}
			m = td.matchWithInstance(this, dedtypes, 0, context);
			if (m == MATCHnomatch) {
				continue;
			}

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
				tdtypes.setDim(dedtypes.size());
				tdtypes.memcpy(dedtypes);
				continue;
			}
			{
				// Disambiguate by picking the most specialized TemplateDeclaration
				MATCH c1 = td.leastAsSpecialized(td_best, context);
				MATCH c2 = td_best.leastAsSpecialized(td, context);

				if (c1.ordinal() > c2.ordinal()) {
					// goto Ltd;
					td_ambig = null;
					td_best = td;
					m_best = m;
					if (tdtypes == null) {
						tdtypes = new Objects(dedtypes.size());
					}
					tdtypes.setDim(dedtypes.size());
					tdtypes.memcpy(dedtypes);
					continue;
				} else if (c1.ordinal() < c2.ordinal()) {
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
			if (context.acceptsErrors()) {
				if (tempdecl != null && null == tempdecl.overnext) {
				    // Only one template, so we can give better error message
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.SymbolDoesNotMatchTemplateDeclaration, this, toChars(context), tempdecl.toChars(context)));
				} else {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.SymbolDoesNotMatchAnyTemplateDeclaration, this, new String[] { toChars(context) }));
				}
			}
			return null;
		}
		if (td_ambig != null) {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.SymbolMatchesMoreThanOneTemplateDeclaration, this, new String[] { toChars(context), td_best.toChars(context), td_ambig
								.toChars(context) }));
			}
		}

		/* The best match is td_best
		 */
		tempdecl = td_best;
		return tempdecl;
	}

	public TemplateDeclaration findTemplateDeclaration(Scope sc,
			SemanticContext context) {
		if (null == tempdecl) {
			/* Given:
			 *    foo!( ... )
			 * figure out which TemplateDeclaration foo refers to.
			 */
			Dsymbol s;
			Dsymbol[] scopesym = new Dsymbol[] { null };
			IdentifierExp id;
			//int i;

			id = name;
			s = sc.search(filename, lineNumber, id, scopesym, context);
			if (null == s) {
				if (context.acceptsErrors()) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.UndefinedIdentifier, this, new String[] { id.toChars() }));
				}
				return null;
			}
			withsym = scopesym[0].isWithScopeSymbol();

			/* We might have found an alias within a template when
			 * we really want the template.
			 */
			TemplateInstance ti;
			if (null != s.parent
					&& null != (ti = s.parent.isTemplateInstance())) {
				if ((equals(ti.name, id) || equals(ti.toAlias(context).ident, id))
						&& null != ti.tempdecl) {
					/* This is so that one can refer to the enclosing
					 * template, even if it has the same name as a member
					 * of the template, if it has a !(arguments)
					 */
					tempdecl = ti.tempdecl;
					if (null != tempdecl.overroot) {
						tempdecl = tempdecl.overroot; // then get the start
					}
					s = tempdecl;
				}
			}

			s = s.toAlias(context);

			/* It should be a TemplateDeclaration, not some other symbol
			 */
			tempdecl = s.isTemplateDeclaration();
			if (null == tempdecl) {
				if (null == s.parent && context.global.errors > 0) {
					return null;
				}
				if (null == s.parent && null != s.getType(context)) {
					Dsymbol s2 = s.getType(context).toDsymbol(sc, context);
					if (null == s2) {
						if (context.acceptsErrors()) {
							context.acceptProblem(Problem.newSemanticTypeError(
									IProblem.SymbolNotATemplateItIs, id, new String[] { id.toChars(),
											s.kind() }));
						}
						return null;
					}
					s = s2;
				}
				//assert(s.parent);
				TemplateInstance $ti = null != s.parent ? s.parent
						.isTemplateInstance() : null;
				if (null != $ti
						&& (equals($ti.name, id) || equals($ti.toAlias(context).ident, id)) && null != $ti.tempdecl) {
					/* This is so that one can refer to the enclosing
					 * template, even if it has the same name as a member
					 * of the template, if it has a !(arguments)
					 */
					tempdecl = $ti.tempdecl;
					if (null != tempdecl.overroot) {
						tempdecl = tempdecl.overroot; // then get the start
					}
				} else {
					if (context.acceptsErrors()) {
						context
								.acceptProblem(Problem.newSemanticTypeError(
										IProblem.SymbolNotATemplateItIs, id, new String[] {
												id.toChars(), s.kind() }));
					}
					return null;
				}
			}
		} else {
			assert (null != tempdecl.isTemplateDeclaration());
		}
		return tempdecl;
	}

	public IdentifierExp genIdent(SemanticContext context) {
		OutBuffer buf = new OutBuffer();
		String id;
		Objects args;

		id = tempdecl.ident.toChars(context);
		buf.data.append("__T").append(id.length()).append(id);
		args = tiargs;
		for (int i = 0; i < size(args); i++) {
			ASTDmdNode o = args.get(i);
			Type ta = isType(o);
			Expression ea = isExpression(o);
			Dsymbol sa = isDsymbol(o);
			Tuple va = isTuple(o);
			
			boolean gotoLsa = false;
			if (ta != null) {
				buf.writeByte('T');
				if (ta.deco != null) {
					buf.writestring(ta.deco);
				} else {
//					if (context.global.errors == 0) {
//						throw new IllegalStateException(
//								"assert(context.global.errors);");
//					}
				}
			} else if (ea != null) {
				//				sinteger_t v;
				//				real_t r;
				//				char p;
				
				if (context.isD2()) {
				    ea = ea.optimize(WANTvalue | WANTinterpret, context);
				}

				if (ea.op == TOKvar) {
					sa = ((VarExp) ea).var;
					ea = null;
					// goto Lsa;
					gotoLsa = true;
					buf.writeByte('S');
					Declaration d = sa.isDeclaration();
					if (d != null && null == d.type.deco) {
						if (context.acceptsErrors()) {
							context.acceptProblem(Problem.newSemanticTypeError(
									IProblem.ForwardReferenceOfSymbol, this, new String[] { d.toChars(context) }));
						}
					} else {
						String p2 = sa.mangle(context);
						buf.data.append(p2.length()).append(p2);
					}
				}
				if (!gotoLsa) {
					if (ea.op == TOKfunction) {
						sa = ((FuncExp) ea).fd;
						ea = null;
						// goto Lsa;
						gotoLsa = true;
						buf.writeByte('S');
						Declaration d = sa.isDeclaration();
						if (d != null && null == d.type.deco) {
							if (context.acceptsErrors()) {
								context.acceptProblem(Problem.newSemanticTypeError(
										IProblem.ForwardReferenceOfSymbol, this, new String[] { d.toChars(context) }));
							}
							if (context.isD2()) {
								continue;
							}
						} else {
							String p2 = sa.mangle(context);
							buf.data.append(p2.length()).append(p2);
						}
					}
					if (!gotoLsa) {
						buf.writeByte('V');
						if (ea.op == TOKtuple) {
							if (context.acceptsErrors()) {
								context.acceptProblem(Problem.newSemanticTypeError(
										IProblem.TupleIsNotAValidTemplateValueArgument, this));
							}
							continue;
						}
						buf.writestring(ea.type.deco);
						ea.toMangleBuffer(buf, context);
					}
				}
			} else if (sa != null) {
				// Lsa: 
				buf.writeByte('S');
				Declaration d = sa.isDeclaration();
				if (d != null && (null == d.type || null == d.type.deco)) {
					if (context.acceptsErrors()) {
						context.acceptProblem(Problem.newSemanticTypeError(
								IProblem.ForwardReferenceOfSymbol, this, new String[] { d.toChars(context) }));
					}
					if (context.isD2()) {
						continue;
					}
				} else {
					String p = sa.mangle(context);
					buf.data.append(p.length()).append(p);
				}
			} else if (va != null) {
				assert (i + 1 == args.size()); // must be last one
				args = /* & */va.objects;
				i = -1;
			} else {
				throw new IllegalStateException("assert(0);");
			}
		}
		buf.writeByte('Z');
		id = buf.toChars();
		buf.data = null;
		return new IdentifierExp(id.toCharArray());
	}

	@Override
	public int getNodeType() {
		return TEMPLATE_INSTANCE;
	}

	@Override
	public AliasDeclaration isAliasDeclaration() {
		return aliasdecl;
	}

	public int hasNestedArgs(Objects args, SemanticContext context) {
		int nested = 0;

		/* A nested instance happens when an argument references a local
		 * symbol that is on the stack.
		 */
		for (int i = 0; i < args.size(); i++) {
			ASTDmdNode o = args.get(i);
			Expression ea = isExpression(o);
			Dsymbol sa = isDsymbol(o);
			Tuple va = isTuple(o);
			if (null != ea || null != sa) {
				// if(ea) was the first condition in DMD's code, so we check
				// ea first.
				boolean gotoLsa = null == ea;
				if (!gotoLsa) {
					if (null != ea) {
						if (ea.op == TOKvar) {
							sa = ((VarExp) ea).var;
							gotoLsa = true;
						}
						if (ea.op == TOKfunction) {
							sa = ((FuncExp) ea).fd;
							gotoLsa = true;
						}
					}
				}

				// else if(null != sa)
				if (gotoLsa) {
					Declaration d;
					boolean gotoL2 = false;
					
					if (context.isD1()) {
						d = sa.isDeclaration();
					} else {
						d = null;
						TemplateDeclaration td = sa.isTemplateDeclaration();
						if (td != null && td.literal) {
							// goto L2;
							gotoL2 = true;
						}
						if (!gotoL2) {
							d = sa.isDeclaration();
						}
					}
					
					boolean condition;
					
					if (gotoL2) {
						condition = true;
					} else {
						condition = null != d
								&& !d.isDataseg(context)
								&& (null == d.isFuncDeclaration() || d
										.isFuncDeclaration().isNested())
								&& null == isTemplateMixin();
						if (context.isD2()) {
							condition = condition && null != d && 0 == (d.storage_class & STCmanifest);
						}
					}
					
					if (condition) {
						// L2:
						// if module level template
						if (null != tempdecl.toParent().isModule()) {
							Dsymbol dparent = context.isD1() ? d.toParent() : sa.toParent();
							if (null == isnested)
								isnested = dparent;
							else if (isnested != dparent) {
								boolean gotoL1 = false;
								
								/* Select the more deeply nested of the two.
								 * Error if one is not nested inside the other.
								 */
								for (Dsymbol p = isnested; p != null; p = p.parent) {
									if (p == dparent) {
										// goto L1;	// isnested is most nested
										gotoL1 = true;
										break;
									}
								}
								
								if (!gotoL1) {
									for (Dsymbol p = dparent; true; p = p.parent) {
										if (p == isnested) {
											isnested = dparent;
											// goto L1;	// dparent is most nested
											break;
										}
									}
								}
								if (context.acceptsErrors()) {
									context.acceptProblem(Problem.newSemanticTypeErrorLoc(
											IProblem.SymbolIsNestedInBoth, 
											this, 
											new String[] { 
											this.toChars(context), 
											isnested.toChars(context), 
											dparent.toChars(context) }));
								}
							}
							// L1:
							nested |= 1;
						} else {
							if (context.acceptsErrors()) {
								context
										.acceptProblem(Problem
												.newSemanticTypeError(
														IProblem.CannotUseLocalAsTemplateParameter,
														this,
														new String[] { d
																.toChars(context) }));
							}
						}
					}
				}

			} else if (null != va) {
				nested |= hasNestedArgs(va.objects, context);
			}
		}
		return nested;
	}

	@Override
	public TemplateInstance isTemplateInstance() {
		return this;
	}

	@Override
	public String kind() {
		return "template instance";
	}

	@Override
	public String mangle(SemanticContext context) {
		OutBuffer buf = new OutBuffer();
		String id;

		id = ident != null ? ident.toChars() : toChars(context);
		if (tempdecl == null) {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(Problem.SymbolNotDefined, this, this.toChars(context)));
			}
		} else if (tempdecl.parent != null) {
			String p = tempdecl.parent.mangle(context);
			if (p.charAt(0) == '_' && p.charAt(1) == 'D') {
				p += 2;
			}
			buf.writestring(p);
		}
		buf.data.append(id.length()).append(id);
		id = buf.toChars();
		buf.data = null;
		return id;
	}

	@Override
	public boolean oneMember(Dsymbol[] ps, SemanticContext context) {
		ps[0] = null;
		return true;
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		// This is for the evaluation and selection engines
		if (this.name != null) {
			context.setTemplateInstance(this.name, this);
		}
		
		// Comment in Descent, we want template instances resolved when possible
		if (context.global.errors > 0) {
			if (0 == context.global.gag) {
				/* Trying to soldier on rarely generates useful messages
				 * at this point.
				 */
//				fatal(context);
			}
			return;
		}

		if (null != inst) // if semantic() was already run
		{
			return;
		}
		
	    // get the enclosing template instance from the scope tinst
	    tinst = sc.tinst;

		if (semanticdone != 0) {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.RecursiveTemplateExpansion, this));
			}
			//		inst = this;
			return;
		}
		semanticdone = 1;

		if (havetempdecl > 0) {
			// WTF assert((size_t)tempdecl.scope > 0x10000);
			// Deduce tdtypes
			tdtypes.setDim(tempdecl.parameters.size());
			
			int matchWithInstanceNum = context.isD2() ? 2 : 0;
			if (MATCHnomatch == tempdecl.matchWithInstance(this, tdtypes, matchWithInstanceNum,
					context)) {
				if (context.acceptsErrors()) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.IncompatibleArgumentsForTemplateInstantiation, this));
				}
				inst = this;
				return;
			}
		} else {
			/* Run semantic on each argument, place results in tiargs[]
			 * (if we havetempdecl, then tiargs is already evaluated)
			 */
			semanticTiargs(sc, context);

			tempdecl = findTemplateDeclaration(sc, context);
			if (null != tempdecl) {
				tempdecl = findBestMatch(sc, context);
			}
			
			name.setResolvedSymbol(tempdecl, context);
			
			// Comment in Descent, we want template instances resolved when possible
			if (null == tempdecl /* || context.global.errors > 0 */) {
				inst = this;
				return; // error recovery
			}
		}

		hasNestedArgs(tiargs, context);

		/* See if there is an existing TemplateInstantiation that already
		 * implements the typeargs. If so, just refer to that one instead.
		 */

		L1: for (int i = 0; i < tempdecl.instances.size(); i++) {
			TemplateInstance ti = tempdecl.instances.get(i);
			assert (tdtypes.size() == ti.tdtypes.size());

			// Nesting must match
			if (isnested != ti.isnested) {
				continue;
			}
			for (int j = 0; j < tdtypes.size(); j++) {
				ASTDmdNode o1 = tdtypes.get(j);
				ASTDmdNode o2 = ti.tdtypes.get(j);
				if (!match(o1, o2, tempdecl, sc, context)) {
					continue L1; // goto L1;
				}
			}

			// It's a match
			inst = ti;
			parent = ti.parent;
			return;

			//L1:
			//;
		}

		/* So, we need to implement 'this' instance.
		 */
		int errorsave = context.global.errors;
		inst = this;
		int tempdecl_instance_idx = tempdecl.instances.size();
		tempdecl.instances.add(this);
		parent = tempdecl.parent;

		ident = genIdent(context); // need an identifier for name mangling purposes.

		if (null != isnested) {
			parent = isnested;
		}

		// Add 'this' to the enclosing scope's members[] so the semantic routines
		// will get called on the instance members
		int dosemantic3 = 0;

		{
			List a;
			Scope scx = sc;
			
			if (scx != null && scx.scopesym != null &&
				    scx.scopesym.members != null && null == scx.scopesym.isTemplateMixin()
				    && (context.isD1() ? (0 == scx.module.selfImports(context)) : true)
				    ) {
			    a = scx.scopesym.members;
		    } else {
				Module m = sc.module.importedFrom;
				
				a = m.members;
				if (m.semanticdone >= 3) {
					dosemantic3 = 1;
				}
			}
			for (int i = 0; true; i++) {
				if (i == a.size()) {
					a.add(this);
					break;
				}
				if (this == a.get(i)) {
					break;
				}
			}
		}
		
		// Descent: added to avoid errors in Module::templateSemantic
//		if (context.templateSemanticStarted) {
//			computeAliasDecl(tempdecl.members, context);
//			return;
//		}

		// Copy the syntax trees from the TemplateDeclaration
		members = Dsymbol.arraySyntaxCopy(tempdecl.members, context);
		
		// Create our own scope for the template parameters
		Scope scope = tempdecl.scope;
		if (null == scope) {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.ForwardReferenceToTemplateDeclaration, this, new String[] { tempdecl.toChars(context) }));
			}
			return;
		}
		argsym = new ScopeDsymbol();
		argsym.parent = scope.parent;
		scope = scope.push(argsym);
			
		// Declare each template parameter as an alias for the argument type
	    Scope paramscope = scope.push();
	    paramscope.stc = 0;
	    declareParameters(paramscope, context);
	    paramscope.pop();		
		
		// Descent: temporary adjust error position so errors doesn't
		// appear inside templates, but always on the invocation site
		context.startTemplateEvaluation(this.tempdecl, scope);
		
		try {
			// Add members of template instance to template instance symbol table
			//	    parent = scope.scopesym;
			symtab = new DsymbolTable();
			int memnum = 0;
			for (int i = 0; i < members.size(); i++) {
				Dsymbol s = members.get(i);
				memnum |= s.addMember(scope, this, memnum, context);
			}
			
			computeAliasDecl(members, context);
	
			// Do semantic() analysis on template instance members
			Scope sc2;
			sc2 = scope.push(this);
			sc2.parent = /*isnested ? sc.parent :*/this;
		    sc2.tinst = this;
		    
		    if (++context.TemplateInstace_nest > 500)
		    {
				context.global.gag = 0;			// ensure error message gets printed
				if (context.acceptsErrors()) {
					context.acceptProblem(Problem.newSemanticTypeErrorLoc(IProblem.RecursiveTemplateExpansion, this));
				}
				context.fatalWasSignaled = true;
				return;
		    }
		    --context.TemplateInstace_nest;
	
			for (int i = 0; i < members.size(); i++) {
				Dsymbol s = members.get(i);
				s.semantic(sc2, context);
				sc2.module.runDeferredSemantic(context);
			}
	
			/* If any of the instantiation members didn't get semantic() run
			 * on them due to forward references, we cannot run semantic2()
			 * or semantic3() yet.
			 */
			boolean gotoLaftersemantic = false;
			for (int j = 0; j < size(context.Module_deferred); j++) {
				Dsymbol sd = (Dsymbol) context.Module_deferred.get(j);
	
				if (sd.parent == this) {
					gotoLaftersemantic = true;
				}
			}
	
			/* The problem is when to parse the initializer for a variable.
			 * Perhaps VarDeclaration.semantic() should do it like it does
			 * for initializers inside a function.
			 */
			//	    if (sc.parent.isFuncDeclaration())
			/* BUG 782: this has problems if the classes this depends on
			 * are forward referenced. Find a way to defer semantic()
			 * on this template.
			 */
			if (!gotoLaftersemantic) {
				semantic2(sc2, context);
	
				if (null != sc.func || dosemantic3 > 0) {
					semantic3(sc2, context);
				}
			}
	
			//Laftersemantic:
			sc2.pop();
	
			scope.pop();
	
			// Give additional context info if error occurred during instantiation
			if (context.global.errors != errorsave) {
				if (context.acceptsErrors()) {
					context.acceptProblem(Problem.newSemanticTypeError(IProblem.ErrorInstantiating, this));
				}
				errors = 1;
				if (context.global.gag > 0) {
					tempdecl.instances.remove(tempdecl_instance_idx);
				}
			}
		} finally {
			context.endTemplateEvaluation(this.tempdecl, scope);
		}
	}
	
	private void computeAliasDecl(Dsymbols members, SemanticContext context) {
		/* See if there is only one member of template instance, and that
		 * member has the same name as the template instance.
		 * If so, this template instance becomes an alias for that member.
		 */
		if (members.size() > 0) {
			Dsymbol[] s = new Dsymbol[] { null };
			if (Dsymbol.oneMembers(members, s, context) && null != s[0]) {
				if (null != s[0].ident && equals(s[0].ident, tempdecl.ident)) {
					aliasdecl = new AliasDeclaration(filename, lineNumber, s[0].ident, s[0]);
					
					// Descent
					aliasdecl.isTemplateParameter = true;
				}
			}
		}
	}

	@Override
	public void semantic2(Scope sc, SemanticContext context) {
		// Descent: for template semantic anlaysis to work
		if (context.templateSemanticStarted)
			return;
		
		int i;

		if (semanticdone >= 2) {
			return;
		}
		semanticdone = 2;

		if (errors == 0 && null != members) {
			sc = tempdecl.scope;
			assert (null != sc);
			sc = sc.push(argsym);
			sc = sc.push(this);
			sc.tinst = this;
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
		// Descent: for template semantic anlaysis to work
		if (context.templateSemanticStarted)
			return;
		
		int i;

		//if (toChars()[0] == 'D') *(char*)0=0;
		if (semanticdone >= 3) {
			return;
		}
		semanticdone = 3;
		if (0 == errors && null != members) {
			sc = tempdecl.scope;
			sc = sc.push(argsym);
			sc = sc.push(this);
			sc.tinst = this;
			for (i = 0; i < members.size(); i++) {
				Dsymbol s = members.get(i);
				s.semantic3(sc, context);
			}
			sc = sc.pop();
			sc.pop();
		}
	}

	public void semanticTiargs(Scope sc, SemanticContext context) {
		if (semantictiargsdone) {
			return;
		}
		semantictiargsdone = true;
		semanticTiargs(filename, lineNumber, sc, tiargs, 0, context);
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s, SemanticContext context) {
		TemplateInstance ti;

		if (s != null) {
			ti = (TemplateInstance) s;
		} else {
			ti = context.newTemplateInstance(filename, lineNumber, name);
		}

		ti.tiargs = arraySyntaxCopy(tiargs, context);
		super.syntaxCopy(ti, context);
		ti.copySourceRange(this);
		return ti;
	}

	@Override
	public Dsymbol toAlias(SemanticContext context) {
		if (inst == null) {
			if (context.acceptsErrors()) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.CannotResolveForwardReference, this));
			}
			return this;
		}

		if (inst != this) {
			return inst.toAlias(context);
		}

		if (aliasdecl != null) {
			return aliasdecl.toAlias(context);
		}

		return inst;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		int i;

		IdentifierExp id = name;
		buf.writestring(id.toChars());
		buf.writestring("!(");
		if (nest) {
			buf.writestring("...");
		} else {
			nest = true;
			Objects args = tiargs;
			for (i = 0; i < size(args); i++) {
				if (i > 0) {
					buf.writeByte(',');
				}
				ASTDmdNode oarg = args.get(i);
				ObjectToCBuffer(buf, hgs, oarg, context);
			}
			nest = false;
		}
		buf.writeByte(')');
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

	/**********************************
	 * Input:
	 *	flags	1: replace const variables with their initializers
	 */
	public static void semanticTiargs(char[] filename, int lineNumber, Scope sc, Objects tiargs,
			int flags, SemanticContext context) {
		// Run semantic on each argument, place results in tiargs[]
		if (null == tiargs) {
			return;
		}
		for (int j = 0; j < size(tiargs); j++) {
			ASTDmdNode o = tiargs.get(j);
			Type[] ta = { isType(o) };
			Expression[] ea = { isExpression(o) };
			Dsymbol[] sa = { isDsymbol(o) };

			if (ta[0] != null) {
				// It might really be an Expression or an Alias
				ta[0].resolve(filename, lineNumber, sc, ea, ta, sa, context);
				if (ea[0] != null) {
					ea[0] = ea[0].semantic(sc, context);
					if (context.isD1()) {
						ea[0] = ea[0].optimize(WANTvalue | WANTinterpret, context);
					} else {
						if (ea[0].op != TOKvar || (flags & 1) != 0)
						    ea[0] = ea[0].optimize(WANTvalue | WANTinterpret, context);
					}
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
				} else if (ta[0] != null) {
					if (ta[0].ty == Ttuple) { // Expand tuple
						TypeTuple tt = (TypeTuple) ta[0];
						int dim = tt.arguments.size();
						tiargs.remove(j);
						if (dim != 0) {
//							tiargs.setDim(dim);
							for (int i = 0; i < dim; i++) {
								Argument arg = tt.arguments.get(i);
								tiargs.add(j + i, arg.type);
							}
						}
						j--;
					} else {
						tiargs.set(j, ta[0]);
					}
				} else {
					if (context.global.errors == 0) {
						// TODO 
						// throw new IllegalStateException("assert(context.global.errors);");
					}
					tiargs.set(j, Type.terror);
				}
			} else if (ea[0] != null) {
				if (null == ea[0]) {
					if (context.global.errors == 0) {
						throw new IllegalStateException(
								"assert(context.global.errors);");
					}
					ea[0] = new IntegerExp(null, 0, 0);
				}
				if (ea[0] == null) {
					throw new IllegalStateException("assert(ea);");
				}
				ea[0] = ea[0].semantic(sc, context);
				if (context.isD1()) {
					ea[0] = ea[0].optimize(WANTvalue | WANTinterpret, context);
				} else {
				    if (ea[0].op != TOKvar || (flags & 1) != 0)
						ea[0] = ea[0].optimize(WANTvalue | WANTinterpret, context);
				}
				tiargs.set(j, ea[0]);
			    if (ea[0].op == TOKtype) {
					tiargs.set(j, ea[0].type);
			    }
			} else if (sa[0] != null) {
				if (context.isD2()) {
				    TemplateDeclaration td = sa[0].isTemplateDeclaration();
				    if (td != null && null == td.scope && td.literal) {
				    	td.semantic(sc, context);
				    }
				}
			} else {
				throw new IllegalStateException("assert (0);");
			}
		}
	}
	
	@Override
	public int getErrorStart() {
		return name.getErrorStart();
	}
	
	@Override
	public int getErrorLength() {
		return name.getErrorLength();
	}
	
	@Override
	public void appendSignature(StringBuilder sb, int options) {
		sb.append(name.ident.length);
		sb.append(name.ident);
		appendInstanceSignature(sb, options);
	}
	
	public void appendInstanceSignature(StringBuilder sb, int options) {
		sb.append(Signature.C_TEMPLATE_INSTANCE);
		for (int j = 0; j < size(tiargs); j++) {
			ASTDmdNode o = tiargs.get(j);
			Type ta = isType(o);
			Expression ea = isExpression(o);
			Dsymbol sa = isDsymbol(o);
			
			if (ta != null) {
				sb.append(Signature.C_TEMPLATE_INSTANCE_TYPE_PARAMETER);
				ta.appendSignature(sb, options);
			} else if (ea != null) {
				sb.append(Signature.C_TEMPLATE_INSTANCE_VALUE_PARAMETER);
				char[] exp = encoder.encodeExpression(ea);
				sb.append(exp.length);
				sb.append(Signature.C_TEMPLATE_INSTANCE_VALUE_PARAMETER);
				sb.append(exp);
			} else if (sa != null) {
				sb.append(Signature.C_TEMPLATE_INSTANCE_SYMBOL_PARAMETER);
				sa.appendSignature(sb, options);
			} else {
				// TODO Descent probably tuple
			}
			
		}
		sb.append(Signature.C_TEMPLATE_PARAMETERS_BREAK);
	}
	
	public void tiargs(Objects tiargs) {
		this.tiargs = tiargs;
		if (tiargs != null) {
			this.sourceTiargs = new Objects(tiargs);
		}
	}
	
	@Override
	public char getSignaturePrefix() {
		return Signature.C_TEMPLATE_INSTANCE;
	}
}