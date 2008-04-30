package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.core.IField;
import descent.core.compiler.IProblem;
import descent.internal.compiler.lookup.SemanticRest;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.PROT.PROTexport;

import static descent.internal.compiler.parser.STC.STCauto;
import static descent.internal.compiler.parser.STC.STCconst;
import static descent.internal.compiler.parser.STC.STCctorinit;
import static descent.internal.compiler.parser.STC.STCextern;
import static descent.internal.compiler.parser.STC.STCfield;
import static descent.internal.compiler.parser.STC.STCforeach;
import static descent.internal.compiler.parser.STC.STCin;
import static descent.internal.compiler.parser.STC.STClazy;
import static descent.internal.compiler.parser.STC.STCout;
import static descent.internal.compiler.parser.STC.STCparameter;
import static descent.internal.compiler.parser.STC.STCref;
import static descent.internal.compiler.parser.STC.STCscope;
import static descent.internal.compiler.parser.STC.STCstatic;
import static descent.internal.compiler.parser.STC.STCtemplateparameter;
import static descent.internal.compiler.parser.STC.STCundefined;

import static descent.internal.compiler.parser.TOK.TOKconstruct;
import static descent.internal.compiler.parser.TOK.TOKint64;
import static descent.internal.compiler.parser.TOK.TOKstring;

import static descent.internal.compiler.parser.TY.Taarray;

// DMD 1.020
public class VarDeclaration extends Declaration {

	public boolean first = true; // is this the first declaration in a multi
	public VarDeclaration next;

	// declaration?
	public Initializer init, sourceInit;
	public Dsymbol aliassym; // if redone as alias to another symbol
	public Type htype;
	public Initializer hinit;;
	public int inuse;
	public int offset;
	public boolean noauto; // no auto semantics
	public int onstack; // 1: it has been allocated on the stack
		// 2: on stack, run destructor anyway
	public int canassign;		// it can be assigned to
	public int nestedref;
	public boolean ctorinit;
	public Expression value; // when interpreting, this is the value
							// (NULL if value not determinable)
	public Object csym;
	public Object isym;
	
	private IField javaElement;
	
	public SemanticRest rest;

	public VarDeclaration(Loc loc, Type type, char[] ident, Initializer init) {
		this(loc, type, new IdentifierExp(Loc.ZERO, ident), init);
	}

	public VarDeclaration(Loc loc, Type type, IdentifierExp id, Initializer init) {
		super(id);

//		Assert.isTrue(type != null || init != null);

		this.loc = loc;
		this.type = type;
		this.sourceType = type;
		this.init = init;
		this.sourceInit = init;
		this.htype = null;
		this.hinit = null;
		this.offset = 0;
		this.noauto = false;
		this.nestedref = 0;
		this.inuse = 0;
		this.ctorinit = false;
		this.aliassym = null;
		this.onstack = 0;
		this.value = null;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, sourceType);
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, sourceInit);
		}
		visitor.endVisit(this);
	}
	
	public Expression callAutoDtor() {
		Expression e = null;

		if ((storage_class & (STCauto | STCscope)) != 0 && !noauto) {
			for (ClassDeclaration cd = type.isClassHandle(); cd != null; cd = cd.baseClass) {
				/*
				 * We can do better if there's a way with onstack classes to
				 * determine if there's no way the monitor could be set.
				 */
				if (true || onstack != 0 || cd.dtors.size() > 0) // if any
				// destructors
				{
					// delete this;
					Expression ec;

					ec = new VarExp(loc, this);
					e = new DeleteExp(loc, ec);
					e.type = Type.tvoid;
					break;
				}
			}
		}
		return e;
	}

	@Override
	public void checkCtorConstInit(SemanticContext context) {
		if (!ctorinit && isCtorinit() && (storage_class & STCfield) == 0) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.MissingInitializerInStaticConstructorForConstVariable, this));
		}
	}

	public void checkNestedReference(Scope sc, Loc loc, SemanticContext context) {
		if (parent != null && !this.isDataseg(context) && this.parent != sc.parent) {
			FuncDeclaration fdv = this.toParent().isFuncDeclaration();
			FuncDeclaration fdthis = (FuncDeclaration) sc.parent.isFuncDeclaration();

			if (fdv != null && fdthis != null) {
				if (loc != null && loc.filename != null)
					fdthis.getLevel(loc, fdv, context);
				this.nestedref(1);
				fdv.nestedFrameRef(true);
			}
		}
	}

	public ExpInitializer getExpInitializer(SemanticContext context) {
		ExpInitializer ei;

		if (this.init != null) {
			ei = this.init().isExpInitializer();
		} else {
			Expression e = this.type.defaultInit(context);
			if (e != null) {
				ei = new ExpInitializer(this.loc, e);
			} else {
				ei = null;
			}
		}
		return ei;
	}

	@Override
	public int getNodeType() {
		return VAR_DECLARATION;
	}

	@Override
	public boolean hasPointers(SemanticContext context) {
		return (!isDataseg(context) && type.hasPointers(context));
	}

	@Override
	public boolean isDataseg(SemanticContext context) {
		Dsymbol parent = this.toParent();
		if (parent == null && (this.storage_class & (STCstatic | STCconst)) == 0) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.CannotResolveForwardReference, this));
			this.type = Type.terror;
			return false;
		}
		return ((this.storage_class & (STCstatic | STCconst)) != 0
				|| parent.isModule() != null || parent.isTemplateInstance() != null);
	}

	@Override
	public boolean isImportedSymbol() {
		if (protection == PROTexport && init == null
				&& (isStatic() || isConst() || parent.isModule() == null)) {
			return true;
		}
		return false;
	}

	public boolean isIn() {
		return (storage_class & STCin) != 0;
	}

	public boolean isInOut() {
		return (storage_class & (STCin | STCout)) == (STCin | STCout);
	}

	@Override
	public boolean isOut() {
		return (storage_class & STCout) != 0;
	}

	@Override
	public VarDeclaration isVarDeclaration() {
		consumeRestStructure();
		consumeRest();
		
		return this;
	}

	@Override
	public String kind() {
		return "variable";
	}

	@Override
	public boolean needThis() {
		return (storage_class & STCfield) != 0;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (rest != null && !rest.isConsumed()) {
			if (rest.getScope() == null) {
				rest.setSemanticContext(sc, context);
			}
			return;
		}
		
		semantic0(sc, context);
		
		// Descent: for code evaluate
		if (sourceInit != null) {
			((Initializer) sourceInit).resolvedInitializer = (Initializer) init;
		}
	}
	
	private void semantic0(Scope sc, SemanticContext context) {
		storage_class |= sc.stc;
		if ((storage_class & STCextern) != 0 && init != null) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.ExternSymbolsCannotHaveInitializers, init));
		}

		/*
		 * If auto type inference, do the inference
		 */
		int inferred = 0;
		if (type == null) {
			inuse++;

			type = init.inferType(sc, context);

			inuse--;
			inferred = 1;

			/*
			 * This is a kludge to support the existing syntax for RAII
			 * declarations.
			 */
			storage_class &= ~STCauto;
			originalType = type;
		} else {
			if (null == originalType) {
				originalType = type;
			}
			type = type.semantic(loc, sc, context);
		}
		
		// Added for Descent: case "auto foo = new"
		if (type == null) {
			return;
		}

		type.checkDeprecated(loc, sc, context);
		linkage = sc.linkage;
		this.parent = sc.parent;
		protection = sc.protection;

		Dsymbol parent = toParent();
		FuncDeclaration fd = parent.isFuncDeclaration();

		Type tb = type.toBasetype(context);
		if (tb.ty == TY.Tvoid && (storage_class & STClazy) == 0) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.VoidsHaveNoValue, sourceType == null ? this : sourceType));
			type = Type.terror;
			tb = type;
		}
		if (tb.ty == TY.Tfunction) {
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.SymbolCannotBeDeclaredToBeAFunction, ident, new String[] { toChars(context) }));
			type = Type.terror;
			tb = type;
		}
		if (tb.ty == TY.Tstruct) {
			TypeStruct ts = (TypeStruct) tb;
			
			ts.sym.consumeRest();

			if (ts.sym.members == null) {
				context.acceptProblem(Problem.newSemanticTypeError(
						// "No definition of struct " + ts.sym.ident,
						IProblem.NoDefinition, sourceType, new String[] { new String(
								ts.sym.ident.ident) }));
			}
		}

		if (tb.ty == TY.Ttuple) {
			/*
			 * Instead, declare variables for each of the tuple elements and add
			 * those.
			 */
			TypeTuple tt = (TypeTuple) tb;
			int nelems = Argument.dim(tt.arguments, context);
			Objects exps = new Objects();
			exps.setDim(nelems);
			Expression ie = init != null ? init.toExpression(context) : null;

			for (int i = 0; i < nelems; i++) {
				Argument arg = Argument.getNth(tt.arguments, i, context);

				OutBuffer buf = new OutBuffer();
				buf.data.append("_").append(ident.ident).append("_field_")
						.append(i);
				String name = buf.extractData();
				IdentifierExp id = new IdentifierExp(loc, name.toCharArray());

			    Expression einit = ie;
				if (ie != null && ie.op == TOK.TOKtuple) {
					einit = (Expression) ((TupleExp) ie).exps.get(i);
				}
				Initializer ti = init;
				if (einit != null) {
					ti = new ExpInitializer(einit.loc, einit);
				}

				VarDeclaration v = new VarDeclaration(loc, arg.type, id, ti);
				v.semantic(sc, context);

				if (sc.scopesym != null) {
					if (sc.scopesym.members != null) {
						sc.scopesym.members.add(v);
					}
				}

				Expression e = new DsymbolExp(loc, v);
				exps.set(i, e);
			}
			TupleDeclaration v2 = new TupleDeclaration(loc, ident, exps);
			v2.isexp = true;
			aliassym = v2;
			return;
		}

		if ((storage_class & STCconst) != 0 && init == null && fd == null) {
			// Initialize by constructor only
			storage_class = (storage_class & ~STCconst) | STCctorinit;
		}

		if (isConst()) {
		} else if (isStatic()) {
		} else if (isSynchronized()) {
			if (ident != null) {
				context
						.acceptProblem(Problem.newSemanticTypeError(
								IProblem.ModifierCannotBeAppliedToVariables, ident,
								new String[] { "synchronized" }));
			}
		} else if (isOverride()) {
			if (ident != null) {
				context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.ModifierCannotBeAppliedToVariables, ident, new String[] { "override" }));
			}
		} else if (isAbstract()) {
			if (ident != null) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.ModifierCannotBeAppliedToVariables, ident, new String[] { "abstract" }));
			}
		} else if ((storage_class & STCtemplateparameter) != 0) {
		} else {
			AggregateDeclaration aad = sc.anonAgg;
			if (aad == null) {
				aad = parent.isAggregateDeclaration();
			}
			if (aad != null) {
				aad.addField(sc, this, context);
			}

			InterfaceDeclaration id = parent.isInterfaceDeclaration();
			if (id != null) {
				context.acceptProblem(Problem.newSemanticTypeErrorLoc(
						IProblem.FieldsNotAllowedInInterfaces, this));
			}

			TemplateInstance ti = parent.isTemplateInstance();
			if (ti != null) {
				// Take care of nested templates
				while (true) {
					TemplateInstance ti2 = ti.tempdecl.parent.isTemplateInstance();
					if (ti2 == null) {
						break;
					}
					ti = ti2;
				}

				// If it's a member template
				AggregateDeclaration ad = ti.tempdecl.isMember();
				if (ad != null && storage_class != STCundefined) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.CannotUseTemplateToAddFieldToAggregate, this, new String[] { ad.toChars(context) }));
				}
			}
		}

		if (type.isauto() && !noauto) {
			if ((storage_class & (STCfield | STCout | STCref | STCstatic)) != 0
					|| fd == null) {
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.GlobalsStaticsFieldsRefAndAutoParametersCannotBeAuto, this));
			}

			if ((storage_class & (STCauto | STCscope)) == 0) {
				if ((storage_class & STCparameter) == 0
						&& equals(ident, Id.withSym)) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.ReferenceToScopeClassMustBeScope, this, new String[] { toChars(context) }));
				}
			}
		}

		if (init == null && !sc.inunion && !isStatic() && !isConst()
				&& fd != null
				&& (storage_class & (STCfield | STCin | STCforeach)) == 0 &&
				type.size(context) != 0) {
			// Provide a default initializer
			if (type.ty == TY.Tstruct && ((TypeStruct) type).sym.zeroInit) {
				/* If a struct is all zeros, as a special case
			     * set it's initializer to the integer 0.
			     * In AssignExp::toElem(), we check for this and issue
			     * a memset() to initialize the struct.
			     * Must do same check in interpreter.
			     */				
				Expression e = new IntegerExp(loc, Id.ZERO, 0, Type.tint32);
				Expression e1;
				e1 = new VarExp(loc, this);
				e = new AssignExp(loc, e1, e);
				e.type = e1.type;
				init = new ExpInitializer(loc, e/* .type.defaultInit() */);
				return;
			} else if (type.ty == TY.Ttypedef) {
				TypeTypedef td = (TypeTypedef) type;
				if (td.sym.init != null) {
					init = td.sym.init;
					ExpInitializer ie = init.isExpInitializer();
					if (ie != null) {
						// Make copy so we can modify it
						init = new ExpInitializer(ie.loc(), ie.exp);
					}
				} else {
					init = getExpInitializer(context);
				}
			} else {
				init = getExpInitializer(context);
			}
		}

		if (init != null) {
			ArrayInitializer ai = init.isArrayInitializer();
			if (ai != null && tb.ty == Taarray) {
				init = ai.toAssocArrayInitializer(context);
			}

			StructInitializer si = init.isStructInitializer();
			ExpInitializer ei = init.isExpInitializer();

			// See if we can allocate on the stack
			if (ei != null && isScope() && ei.exp.op == TOK.TOKnew) {
				NewExp ne = (NewExp) ei.exp;
				if (!(ne.newargs != null && ne.newargs.size() > 0)) {
					ne.onstack = true;
					onstack = 1;
					if (type.isBaseOf(ne.newtype.semantic(loc, sc, context),
							null, context)) {
						onstack = 2;
					}
				}
			}

			// If inside function, there is no semantic3() call
			if (sc.func != null) {
				// If local variable, use AssignExp to handle all the various
				// possibilities.
				if (fd != null && !isStatic() && !isConst()
						&& init.isVoidInitializer() == null) {
					Expression e1;
					Type t;
					int dim;

					if (ei == null) {
						Expression e = init.toExpression(context);
						if (e == null) {
							init = init.semantic(sc, type, context);
							e = init.toExpression(context);
							if (e == null) {
								context.acceptProblem(Problem.newSemanticTypeError(IProblem.SymbolNotAStaticAndCannotHaveStaticInitializer, this, new String[] { toChars(context) }));
								return;
							}
						}
						ei = new ExpInitializer(init.loc(), e);
						init = ei;
					}

					e1 = new VarExp(loc, this);

					t = type.toBasetype(context);
					if (t.ty == TY.Tsarray) {
					    ei.exp = ei.exp.semantic(sc, context);
						if (null == ei.exp.implicitConvTo(type, context)) {
							dim = ((TypeSArray) t).dim.toInteger(context)
									.intValue();
							// If multidimensional static array, treat as one large
							// array
							while (true) {
								t = t.nextOf().toBasetype(context);
								if (t.ty != TY.Tsarray) {
									break;
								}
								dim *= ((TypeSArray) t).dim.toInteger(
										context).intValue();
								e1.type = new TypeSArray(t.nextOf(),
										new IntegerExp(loc, Id.ZERO, dim,
												Type.tindex), context.encoder);
							}
						}
						e1 = new SliceExp(loc, e1, null, null);
					} else if (t.ty == TY.Tstruct) {
						ei.exp = ei.exp.semantic(sc, context);
						if (ei.exp.implicitConvTo(type, context) == MATCH.MATCHnomatch) {
							ei.exp = new CastExp(loc, ei.exp, type);
						}
					}
					ei.exp = new AssignExp(loc, e1, ei.exp);
					ei.exp.op = TOKconstruct;
					canassign++;
					ei.exp = ei.exp.semantic(sc, context);
					canassign--;
					ei.exp.optimize(ASTDmdNode.WANTvalue, context);
				} else {
					init = init.semantic(sc, type, context);
					if (fd != null && isConst() && !isStatic()) { // Make it
						// static
						storage_class |= STCstatic;
					}
				}
			} else if (isConst() || isFinal()) {
				/* Because we may need the results of a const declaration in a
				 * subsequent type, such as an array dimension, before semantic2()
				 * gets ordinarily run, try to run semantic2() now.
				 * Ignore failure.
				 */

				if (0 == context.global.errors && 0 == inferred) {
					int errors = context.global.errors;
					context.global.gag++;
					Expression e = null;
					Initializer i2 = init;
					inuse++;
					if (ei != null) {
						e = ei.exp.syntaxCopy(context);
						e = e.semantic(sc, context);

						e = e.implicitCastTo(sc, type, context);
					} else if (si != null || ai != null) {
						i2 = init.syntaxCopy(context);
						i2 = i2.semantic(sc, type, context);
					}
					inuse--;
					context.global.gag--;
					if (errors != context.global.errors) // if errors happened
					{
						if (context.global.gag == 0) {
							context.global.errors = errors; // act as if nothing happened
						}
					} else if (ei != null) {
						e = e.optimize(WANTvalue | WANTinterpret, context);
						if (e.op == TOKint64 || e.op == TOKstring) {
							// TODO Descent: instead of copying the result, do semantic analysis again,
							// in order to get binding resolution
							ei.exp.semantic(sc, context);
							//							ei.exp = e;		// no errors, keep result
						}
					} else {
						init = i2; // no errors, keep result
					}
				}
			}
		}
	}
	
	@Override
	public void semantic2(Scope sc, SemanticContext context) {
		if (rest != null && !rest.isConsumed()) {
			if (rest.getScope() == null) {
				rest.setSemanticContext(sc, context);
			}
			return;
		}
		
		semantic20(sc, context);
		
		// Descent: for code evaluate
		if (sourceInit != null) {
			((Initializer) sourceInit).resolvedInitializer = (Initializer) init;
		}
	}

	private void semantic20(Scope sc, SemanticContext context) {
		Dsymbol top = toParent();
		if (init != null && top != null && top.isFuncDeclaration() == null) {
			inuse++;
			init = init.semantic(sc, type, context);
			inuse--;
		}
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s, SemanticContext context) {
		consumeRestStructure();
		
		VarDeclaration sv;
		if (s != null) {
			sv = (VarDeclaration) s;
		} else {
			Initializer init = null;
			if (this.init != null) {
				init = this.init.syntaxCopy(context);
				// init.isExpInitializer().exp.print();
				// init.isExpInitializer().exp.dump(0);
			}

			sv = new VarDeclaration(loc, type != null ? type.syntaxCopy(context)
					: null, ident, init);
			sv.storage_class = storage_class;
		}
		// Syntax copy for header file
		if (htype == null) // Don't overwrite original
		{
			if (type != null) // Make copy for both old and new instances
			{
				htype = type.syntaxCopy(context);
				sv.htype = type.syntaxCopy(context);
			}
		} else {
			// Make copy of original for new instance
			sv.htype = htype.syntaxCopy(context);
		}
		if (hinit == null) {
			if (init != null) {
				hinit = init.syntaxCopy(context);
				sv.hinit = init.syntaxCopy(context);
			}
		} else {
			sv.hinit = hinit.syntaxCopy(context);
		}
		sv.copySourceRange(this);
		sv.javaElement = javaElement;
		return sv;
	}

	@Override
	public Dsymbol toAlias(SemanticContext context) {
		Assert.isTrue(this != aliassym);
		Dsymbol s = aliassym != null ? aliassym.toAlias(context) : this;
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		if ((storage_class & STCconst) != 0) {
			buf.writestring("const ");
		}
		if ((storage_class & STCstatic) != 0) {
			buf.writestring("static ");
		}
		if (type != null) {
			type.toCBuffer(buf, ident, hgs, context);
		} else {
			buf.writestring(ident.toChars());
		}
		if (init != null) {
			buf.writestring(" = ");
			init.toCBuffer(buf, hgs, context);
		}
		buf.writeByte(';');
		buf.writenl();
	}
	
	@Override
	public int getLineNumber() {
		return loc.linnum;
	}
	
	public void setLineNumber(int lineNumber) {
		this.loc.linnum = lineNumber;
	}
	
	public int inuse() {
		return inuse;
	}
	
	public Initializer init() {
		return init;
	}
	
	public void init(Initializer init) {
		this.init = init;
	}
	
	public boolean ctorinit() {
		return ctorinit;
	}
	
	public void ctorinit(boolean c) {
		this.ctorinit = c;
	}
	
	public boolean noauto() {
		return noauto;
	}
	
	public Expression value() {
		return value;
	}
	
	public void value(Expression value) {
		this.value = value;
	}
	
	public int offset() {
		return offset;
	}
	
	public void offset(int offset) {
		this.offset = offset;
	}
	
	public int canassign() {
		return canassign;
	}
	
	public int nestedref() {
		return nestedref;
	}
	
	public void nestedref(int nestedref) {
		this.nestedref = nestedref;
	}
	
	public char getSignaturePrefix() {
		return ISignatureConstants.VARIABLE;
	}

	public void setJavaElement(IField field) {
		this.javaElement = field;
	}
	
	@Override
	public IField getJavaElement() {
		return javaElement;
	}
	
	@Override
	public void consumeRestStructure() {
		if (rest != null && !rest.isStructureKnown()) {
			rest.buildStructure();
		}
	}
	
	@Override
	public void consumeRest() {
		if (rest != null && !rest.isConsumed()) {
			rest.consume(this);
		}
	}

    // PERHAPS Symbol *toSymbol();

}
