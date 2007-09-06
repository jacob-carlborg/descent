package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.PROT.PROTexport;
import static descent.internal.compiler.parser.STC.STCauto;
import static descent.internal.compiler.parser.STC.STCconst;
import static descent.internal.compiler.parser.STC.STCfield;
import static descent.internal.compiler.parser.STC.STCscope;
import static descent.internal.compiler.parser.STC.STCstatic;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.CharOperation;
import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class VarDeclaration extends Declaration {

	public boolean first; // is this the first declaration in a multi
	public boolean last; // is this the last declaration in a multi
	public VarDeclaration next;
	
	// declaration?
	public Type sourceType;
	public Initializer init;
	public Initializer sourceInit;
	public Dsymbol aliassym; // if redone as alias to another symbol
	public Type htype;
	public Initializer hinit;;
	public int inuse;
	public int offset;
	public boolean noauto; // no auto semantics
	public int onstack; // 1: it has been allocated on the stack
	// 2: on stack, run destructor anyway
	public int nestedref;
	public boolean ctorinit;
	public Expression value; // when interpreting, this is the value

	// (NULL if value not determinable)

	public VarDeclaration(Loc loc, Type type, char[] ident, Initializer init) {
		this(loc, type, new IdentifierExp(Loc.ZERO, ident), init);
	}

	public VarDeclaration(Loc loc, Type type, IdentifierExp id, Initializer init) {
		super(loc, id);

		Assert.isTrue(type != null || init != null);

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
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, ident);
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
			error("missing initializer in static constructor for const variable");
		}
	}

	public void checkNestedReference(Scope sc, Loc loc, SemanticContext context) {
		if (!isDataseg(context) && parent != sc.parent && parent != null) {
			FuncDeclaration fdv = toParent().isFuncDeclaration();
			FuncDeclaration fdthis = sc.parent.isFuncDeclaration();

			if (fdv != null && fdthis != null) {
				/*
				 * TODO loc??? if (loc.filename) fdthis.getLevel(loc, fdv);
				 * nestedref = 1; fdv.nestedFrameRef = 1;
				 */
			}
		}
	}

	public ExpInitializer getExpInitializer(SemanticContext context) {
		ExpInitializer ei;

		if (init != null) {
			ei = init.isExpInitializer();
		} else {
			Expression e = type.defaultInit(context);
			if (e != null) {
				ei = new ExpInitializer(loc, e);
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
		if (parent == null
				&& (storage_class & (STC.STCstatic | STC.STCconst)) == 0) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.CannotResolveForwardReference, 0, start,
					length));
			type = Type.terror;
			return false;
		}
		return ((storage_class & (STC.STCstatic | STC.STCconst)) != 0
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
		return (storage_class & STC.STCin) != 0;
	}

	public boolean isInOut() {
		return (storage_class & (STC.STCin | STC.STCout)) == (STC.STCin | STC.STCout);
	}

	public boolean isOut() {
		return (storage_class & STC.STCout) != 0;
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
		storage_class |= sc.stc;
		if ((storage_class & STC.STCextern) != 0 && init != null) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.ExternSymbolsCannotHaveInitializers, 0,
					init.start, init.length));
		}

		/*
		 * If auto type inference, do the inference
		 */
		int inferred = 0;
		if (type == null) {
			inuse++;

			type = init.inferType(sc, context);
			type.synthetic = true;

			inuse--;
			inferred = 1;

			/*
			 * This is a kludge to support the existing syntax for RAII
			 * declarations.
			 */
			storage_class &= ~STC.STCauto;
		} else {
			type = type.semantic(loc, sc, context);
		}

		type.checkDeprecated(loc, sc, context);
		linkage = sc.linkage;
		this.parent = sc.parent;
		protection = sc.protection;

		Dsymbol parent = toParent();
		FuncDeclaration fd = parent.isFuncDeclaration();

		Type tb = type.toBasetype(context);
		if (tb.ty == TY.Tvoid && (storage_class & STC.STClazy) == 0) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.VoidsHaveNoValue, 0,
					sourceType.start, sourceType.length));
			type = Type.terror;
			tb = type;
		}
		if (tb.ty == TY.Tfunction) {
			error("cannot be declared to be a function");
			type = Type.terror;
			tb = type;
		}
		if (tb.ty == TY.Tstruct) {
			TypeStruct ts = (TypeStruct) tb;

			if (ts.sym.members == null) {
				context.acceptProblem(Problem.newSemanticTypeError(
						// "No definition of struct " + ts.sym.ident,
						IProblem.NoDefinition, 0, sourceType.start,
						sourceType.length, new String[] { new String(ts.sym.ident.ident) }));
			}
		}

		if (tb.ty == TY.Ttuple) {
			/*
			 * Instead, declare variables for each of the tuple elements and add
			 * those.
			 */
			TypeTuple tt = (TypeTuple) tb;
			int nelems = Argument.dim(tt.arguments, context);
			List exps = new ArrayList(nelems);

			for (int i = 0; i < nelems; i++) {
				Argument arg = Argument.getNth(tt.arguments, i, context);

				OutBuffer buf = new OutBuffer();
				buf.data.append("_").append(ident.ident).append(
						"_field_").append(i).append("u");
				String name = buf.extractData();
				IdentifierExp id = new IdentifierExp(loc, name.toCharArray());

				VarDeclaration v = new VarDeclaration(loc, arg.type, id, null);
				v.semantic(sc, context);

				if (sc.scopesym != null) {
					if (sc.scopesym.members != null) {
						sc.scopesym.members.add(v);
					}
				}

				Expression e = new DsymbolExp(loc, v);
				exps.add(e);
			}
			TupleDeclaration v2 = new TupleDeclaration(loc, ident, exps);
			v2.isexp = true;
			aliassym = v2;
			return;
		}

		if ((storage_class & STC.STCconst) != 0 && init == null && fd == null) {
			// Initialize by constructor only
			storage_class = (storage_class & ~STC.STCconst) | STC.STCctorinit;
		}

		if (isConst()) {
		} else if (isStatic()) {
		} else if (isSynchronized()) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.ModifierCannotBeAppliedToVariables, 0, ident.start, ident.length, new String[] { "synchronized" }));
		} else if (isOverride()) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.ModifierCannotBeAppliedToVariables, 0, ident.start, ident.length, new String[] { "override" }));
		} else if (isAbstract()) {
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.ModifierCannotBeAppliedToVariables, 0, ident.start, ident.length, new String[] { "abstract" }));
		} else if ((storage_class & STC.STCtemplateparameter) != 0) {
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
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.FieldsNotAllowedInInterfaces, 0, ident.start,
						ident.length));
			}

			TemplateInstance ti = parent.isTemplateInstance();
			if (ti != null) {
				// Take care of nested templates
				while (true) {
					TemplateInstance ti2 = ti.tempdecl.parent
							.isTemplateInstance();
					if (ti2 == null) {
						break;
					}
					ti = ti2;
				}

				// If it's a member template
				AggregateDeclaration ad = ti.tempdecl.isMember();
				if (ad != null && storage_class != STC.STCundefined) {
					error("cannot use template to add field to aggregate '%s'",
							ad.toChars(context));
				}
			}
		}

		if (type.isauto() && !noauto) {
			if ((storage_class & (STC.STCfield | STC.STCout | STC.STCstatic)) != 0
					|| fd == null) {
				error("globals, statics, fields, inout and out parameters cannot be auto");
			}

			if ((storage_class & (STC.STCauto | STC.STCscope)) == 0) {
				if ((storage_class & STC.STCparameter) == 0
						&& CharOperation.equals(ident.ident, Id.withSym)) {
					error("reference to auto class must be auto");
				}
			}
		}

		if (init == null
				&& !sc.inunion
				&& !isStatic()
				&& !isConst()
				&& fd != null
				&& (storage_class & (STC.STCfield | STC.STCin | STC.STCforeach)) == 0) {
			// Provide a default initializer
			if (type.ty == TY.Tstruct && ((TypeStruct) type).sym.zeroInit) {
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
						init = new ExpInitializer(ie.loc, ie.exp);
					}
				} else {
					init = getExpInitializer(context);
				}
			} else {
				init = getExpInitializer(context);
			}
		}

		if (init != null) {
			ExpInitializer ei = init.isExpInitializer();

			// See if we can allocate on the stack
			if (ei != null && isScope() && ei.exp.op == TOK.TOKnew) {
				NewExp ne = (NewExp) ei.exp;
				if (!(ne.newargs != null && ne.newargs.size() > 0)) {
					ne.onstack = true;
					onstack = 1;
					if (type.isBaseOf(ne.newtype.semantic(loc, sc, context), null, context)) {
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

					// printf("fd = '%s', var = '%s'\n", fd.toChars(),
					// toChars());
					if (ei == null) {
						Expression e = init.toExpression(context);
						if (e == null) {
							init = init.semantic(sc, type, context);
							e = init.toExpression(context);
							if (e == null) {
								error("is not a static and cannot have static initializer");
								return;
							}
						}
						ei = new ExpInitializer(init.loc, e);
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
							e1.type = new TypeSArray(t.next, new IntegerExp(loc, 
									Id.ZERO, dim, Type.tindex));
						}
						e1 = new SliceExp(loc, e1, null, null);
					} else if (t.ty == TY.Tstruct) {
						ei.exp = ei.exp.semantic(sc, context);
						if (ei.exp.implicitConvTo(type, context) == MATCH.MATCHnomatch) {
							ei.exp = new CastExp(loc, ei.exp, type);
						}
					}
					ei.exp = new AssignExp(loc, e1, ei.exp);
					ei.exp = ei.exp.semantic(sc, context);
					ei.exp.optimize(ASTDmdNode.WANTvalue, context);
				} else {
					init = init.semantic(sc, type, context);
					if (fd != null && isConst() && !isStatic()) { // Make it
						// static
						storage_class |= STC.STCstatic;
					}
				}
			} else if (isConst()) {
				/*
				 * Because we may need the results of a const declaration in a
				 * subsequent type, such as an array dimension, before
				 * semantic2() gets ordinarily run, try to run semantic2() now.
				 * Ignore failure.
				 */

				/*
				 * TODO semantic if (ei && !global.errors && !inferred) { int
				 * errors = global.errors; global.gag++; //printf("+gag\n");
				 * Expression e = ei.exp.syntaxCopy(); inuse++; e =
				 * e.semantic(sc, context); inuse--; e = e.implicitCastTo(sc,
				 * type); global.gag--; //printf("-gag\n"); if (errors !=
				 * global.errors) // if errors happened { if (global.gag == 0)
				 * global.errors = errors; // act as if nothing happened } else {
				 * e = e.optimize(Expression.WANTvalue |
				 * Expression.WANTinterpret); if (e.op == TOK.TOKint64 || e.op ==
				 * TOK.TOKstring) { ei.exp = e; // no errors, keep result } } }
				 */
			}
		}
	}

	@Override
	public void semantic2(Scope sc, SemanticContext context) {
		if (init != null && toParent().isFuncDeclaration() == null) {
			inuse++;
			init = init.semantic(sc, type, context);
			inuse--;
		}
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		VarDeclaration sv;
		if (s != null) {
			sv = (VarDeclaration) s;
		} else {
			Initializer init = null;
			if (this.init != null) {
				init = this.init.syntaxCopy();
				// init.isExpInitializer().exp.print();
				// init.isExpInitializer().exp.dump(0);
			}

			sv = new VarDeclaration(loc, type != null ? type.syntaxCopy() : null,
					ident, init);
			sv.storage_class = storage_class;
		}
		// Syntax copy for header file
		if (htype == null) // Don't overwrite original
		{
			if (type != null) // Make copy for both old and new instances
			{
				htype = type.syntaxCopy();
				sv.htype = type.syntaxCopy();
			}
		} else {
			// Make copy of original for new instance
			sv.htype = htype.syntaxCopy();
		}
		if (hinit == null) {
			if (init != null) {
				hinit = init.syntaxCopy();
				sv.hinit = init.syntaxCopy();
			}
		} else {
			sv.hinit = hinit.syntaxCopy();
		}
		return sv;
	}

	@Override
	public Dsymbol toAlias(SemanticContext context) {
		Assert.isTrue(this != aliassym);
		Dsymbol s = aliassym != null ? aliassym.toAlias(context) : this;
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
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

}
