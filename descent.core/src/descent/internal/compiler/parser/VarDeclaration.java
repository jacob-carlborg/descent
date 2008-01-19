package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
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
public class VarDeclaration extends Declaration implements IVarDeclaration {

	public boolean first = true; // is this the first declaration in a multi
	public VarDeclaration next;

	// declaration?
	public IInitializer init, sourceInit;
	public Dsymbol aliassym; // if redone as alias to another symbol
	public Type htype;
	public IInitializer hinit;;
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

	public VarDeclaration(Loc loc, Type type, char[] ident, IInitializer init) {
		this(loc, type, new IdentifierExp(Loc.ZERO, ident), init);
	}

	public VarDeclaration(Loc loc, Type type, IdentifierExp id, IInitializer init) {
		super(id);

		Assert.isTrue(type != null || init != null);

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
			for (IClassDeclaration cd = type.isClassHandle(); cd != null; cd = cd.baseClass()) {
				/*
				 * We can do better if there's a way with onstack classes to
				 * determine if there's no way the monitor could be set.
				 */
				if (true || onstack != 0 || cd.dtors().size() > 0) // if any
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
		SemanticMixin.checkNestedReference(this, sc, loc, context);
	}

	public IExpInitializer getExpInitializer(SemanticContext context) {
		return SemanticMixin.getExpInitializer(this, context);
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
		return SemanticMixin.isDataseg(this, context);
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
		} else {
			type = type.semantic(loc, sc, context);
		}

		type.checkDeprecated(loc, sc, context);
		linkage = sc.linkage;
		this.parent = sc.parent;
		protection = sc.protection;

		IDsymbol parent = toParent();
		IFuncDeclaration fd = parent.isFuncDeclaration();

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

			if (ts.sym.members() == null) {
				context.acceptProblem(Problem.newSemanticTypeError(
						// "No definition of struct " + ts.sym.ident,
						IProblem.NoDefinition, sourceType, new String[] { new String(
								ts.sym.ident().ident) }));
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

			for (int i = 0; i < nelems; i++) {
				Argument arg = Argument.getNth(tt.arguments, i, context);

				OutBuffer buf = new OutBuffer();
				buf.data.append("_").append(ident.ident).append("_field_")
						.append(i);
				String name = buf.extractData();
				IdentifierExp id = new IdentifierExp(loc, name.toCharArray());

				VarDeclaration v = new VarDeclaration(loc, arg.type, id, null);
				v.semantic(sc, context);

				if (sc.scopesym != null) {
					if (sc.scopesym.members() != null) {
						sc.scopesym.members().add(v);
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
			context
					.acceptProblem(Problem.newSemanticTypeError(
							IProblem.ModifierCannotBeAppliedToVariables, ident,
							new String[] { "synchronized" }));
		} else if (isOverride()) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.ModifierCannotBeAppliedToVariables, ident, new String[] { "override" }));
		} else if (isAbstract()) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.ModifierCannotBeAppliedToVariables, ident, new String[] { "abstract" }));
		} else if ((storage_class & STCtemplateparameter) != 0) {
		} else {
			IAggregateDeclaration aad = sc.anonAgg;
			if (aad == null) {
				aad = parent.isAggregateDeclaration();
			}
			if (aad != null) {
				aad.addField(sc, this, context);
			}

			IInterfaceDeclaration id = parent.isInterfaceDeclaration();
			if (id != null) {
				context.acceptProblem(Problem.newSemanticTypeErrorLoc(
						IProblem.FieldsNotAllowedInInterfaces, this));
			}

			TemplateInstance ti = parent.isTemplateInstance();
			if (ti != null) {
				// Take care of nested templates
				while (true) {
					TemplateInstance ti2 = ti.tempdecl.parent().isTemplateInstance();
					if (ti2 == null) {
						break;
					}
					ti = ti2;
				}

				// If it's a member template
				IAggregateDeclaration ad = ti.tempdecl.isMember();
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
				&& (storage_class & (STCfield | STCin | STCforeach)) == 0) {
			// Provide a default initializer
			if (type.ty == TY.Tstruct && ((TypeStruct) type).sym.zeroInit()) {
				Expression e = new IntegerExp(loc, Id.ZERO, 0, Type.tint32);
				Expression e1;
				e1 = new VarExp(loc, this);
				e = new AssignExp(loc, e1, e);
				e.type = e1.type;
				init = new ExpInitializer(loc, e/* .type.defaultInit() */);
				return;
			} else if (type.ty == TY.Ttypedef) {
				TypeTypedef td = (TypeTypedef) type;
				if (td.sym.init() != null) {
					init = td.sym.init();
					IExpInitializer ie = init.isExpInitializer();
					if (ie != null) {
						// Make copy so we can modify it
						init = new ExpInitializer(ie.loc(), ie.exp());
					}
				} else {
					init = getExpInitializer(context);
				}
			} else {
				init = getExpInitializer(context);
			}
		}

		if (init != null) {
			IArrayInitializer ai = init.isArrayInitializer();
			if (ai != null && type.toBasetype(context).ty == Taarray) {
				init = ai.toAssocArrayInitializer(context);
			}

			IExpInitializer ei = init.isExpInitializer();

			// See if we can allocate on the stack
			if (ei != null && isScope() && ei.exp().op == TOK.TOKnew) {
				NewExp ne = (NewExp) ei.exp();
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
						dim = ((TypeSArray) t).dim.toInteger(context)
								.intValue();
						// If multidimensional static array, treat as one large
						// array
						while (true) {
							t = t.next.toBasetype(context);
							if (t.ty != TY.Tsarray) {
								break;
							}
							if (t.next.toBasetype(context).ty == TY.Tbit) {
								// t.size() gives size in bytes, convert to bits
								dim *= t.size(loc, context) * 8;
							} else {
								dim *= ((TypeSArray) t).dim.toInteger(context)
										.intValue();
							}
							e1.type = new TypeSArray(t.next, new IntegerExp(
									loc, Id.ZERO, dim, Type.tindex));
						}
						e1 = new SliceExp(loc, e1, null, null);
					} else if (t.ty == TY.Tstruct) {
						ei.exp(ei.exp().semantic(sc, context));
						if (ei.exp().implicitConvTo(type, context) == MATCH.MATCHnomatch) {
							ei.exp(new CastExp(loc, ei.exp(), type));
						}
					}
					ei.exp(new AssignExp(loc, e1, ei.exp()));
					ei.exp().op = TOKconstruct;
					canassign++;
					ei.exp(ei.exp().semantic(sc, context));
					canassign--;
					ei.exp().optimize(ASTDmdNode.WANTvalue, context);
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

				if (ei != null && 0 == context.global.errors && 0 == inferred) {
					int errors = context.global.errors;
					context.global.gag++;
					Expression e = ei.exp().syntaxCopy(context);
					inuse++;
					e = e.semantic(sc, context);
					
					// Descent: for binding resolution
					if (sourceInit instanceof ExpInitializer) {
						((ExpInitializer) sourceInit).sourceExp.setResolvedExpression(e);
					}
					
					inuse--;
					e = e.implicitCastTo(sc, type, context);
					context.global.gag--;
					if (errors != context.global.errors) // if errors happened
					{
						if (context.global.gag == 0)
							context.global.errors = errors; // act as if nothing happened
					} else {
						e = e.optimize(WANTvalue | WANTinterpret, context);
						
						// Descent: for binding resolution
						if (sourceInit instanceof ExpInitializer) {
							((ExpInitializer) sourceInit).sourceExp.setEvaluatedExpression(e);
						}
						
						if (e.op == TOKint64 || e.op == TOKstring) {
							ei.exp(e); // no errors, keep result
						}
					}
				}
			}
		}
	}
	
	@Override
	public void semantic2(Scope sc, SemanticContext context) {
		semantic20(sc, context);
		
		// Descent: for code evaluate
		if (sourceInit != null) {
			((Initializer) sourceInit).resolvedInitializer = (Initializer) init;
		}
	}

	private void semantic20(Scope sc, SemanticContext context) {
		if (init != null && toParent().isFuncDeclaration() == null) {
			inuse++;
			init = init.semantic(sc, type, context);
			inuse--;
		}
	}

	@Override
	public IDsymbol syntaxCopy(IDsymbol s, SemanticContext context) {
		VarDeclaration sv;
		if (s != null) {
			sv = (VarDeclaration) s;
		} else {
			IInitializer init = null;
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
		return sv;
	}

	@Override
	public IDsymbol toAlias(SemanticContext context) {
		Assert.isTrue(this != aliassym);
		IDsymbol s = aliassym != null ? aliassym.toAlias(context) : this;
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
	
	public IInitializer init() {
		return init;
	}
	
	public void init(IInitializer init) {
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

    // PERHAPS Symbol *toSymbol();

}
