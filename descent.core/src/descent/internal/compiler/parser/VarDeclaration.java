package descent.internal.compiler.parser;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;

public class VarDeclaration extends Declaration {

	public boolean last;		// is this the last declaration in a multi declaration?
	public Type originalType;
	public Type type;
	public Initializer init;
	public Dsymbol aliassym;	// if redone as alias to another symbol
	public int inuse;
	public int offset;
	public boolean noauto;			// no auto semantics
	public int onstack;		// 1: it has been allocated on the stack
							// 2: on stack, run destructor anyway

	public VarDeclaration(Type type, IdentifierExp ident, Initializer init) {
		this.type = type;
		this.originalType = type;
		this.ident = ident;
		this.init = init;
	}
	
	@Override
	public Dsymbol toAlias(SemanticContext context) {
		Assert.isTrue(this != aliassym);
	    Dsymbol s = aliassym != null ? aliassym.toAlias(context) : this;
	    return s;
	}
	
	@Override
	public VarDeclaration isVarDeclaration() {
		return this;
	}
	
	@Override
	public boolean isDataseg(SemanticContext context) {
	    Dsymbol parent = this.toParent();
	    if (parent == null && (storage_class & (STC.STCstatic | STC.STCconst)) == 0)
	    {	
	    	context.acceptProblem(Problem.newSemanticTypeError("Forward referenced", IProblem.ForwardReference, 0, start, length));
	    	type = Type.terror;
	    	return false;
	    }
	    return ((storage_class & (STC.STCstatic | STC.STCconst)) != 0 ||
		   parent.isModule() != null ||
		   parent.isTemplateInstance() != null );
	}
	
	public void checkNestedReference(Scope sc, SemanticContext context) {
		if (!isDataseg(context) && parent != sc.parent && parent != null) {
			FuncDeclaration fdv = toParent().isFuncDeclaration();
			FuncDeclaration fdthis = sc.parent.isFuncDeclaration();

			if (fdv != null && fdthis != null) {
				/* TODO loc???
				if (loc.filename)
					fdthis.getLevel(loc, fdv);
				nestedref = 1;
				fdv.nestedFrameRef = 1;
				*/
			}
		}
	}
	
	public ExpInitializer getExpInitializer() {
		ExpInitializer ei;

		if (init != null) {
			ei = init.isExpInitializer();
		} else {
			Expression e = type.defaultInit();
			if (e != null) {
				ei = new ExpInitializer(e);
			} else {
				ei = null;
			}
		}
		return ei;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		storage_class |= sc.stc;
		if ((storage_class & STC.STCextern) != 0 && init != null) {
			context.acceptProblem(Problem.newSemanticTypeError("Extern symbols cannot have initializers", IProblem.ExternSymbolsCannotHaveInitializers, 0, init.start, init.length));
		}

		/* If auto type inference, do the inference
		 */
		int inferred = 0;
		if (type == null) {
			inuse++;
			
			type = init.inferType(sc, context);
			type.synthetic = true;
			
			inuse--;
			inferred = 1;

			/* This is a kludge to support the existing syntax for RAII
			 * declarations.
			 */
			storage_class &= ~STC.STCauto;
		} else {
			type = type.semantic(sc, context);
		}

		type.checkDeprecated(sc, context);
		linkage = sc.linkage;
		this.parent = sc.parent;
		protection = sc.protection;

		Dsymbol parent = toParent();
		FuncDeclaration fd = parent.isFuncDeclaration();

		Type tb = type.toBasetype(context);
		if (tb.ty == TY.Tvoid && (storage_class & STC.STClazy) == 0) {
			context.acceptProblem(Problem.newSemanticTypeError("Voids have no value", IProblem.VoidsHaveNoValue, 0, originalType.start, originalType.length));
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
				error("no definition of struct %s", ts.toChars());
			}
		}

		if (tb.ty == TY.Ttuple) { 
			/* Instead, declare variables for each of the tuple elements
			 * and add those.
			 */
			TypeTuple tt = (TypeTuple) tb;
			int nelems = Argument.dim(tt.arguments, context);
			List exps = new ArrayList(nelems);

			for (int i = 0; i < nelems; i++) {
				Argument arg = Argument.getNth(tt.arguments, i, context);

				OutBuffer buf = new OutBuffer();
				buf.data.append("_").append(ident.ident.string).append(
						"_field_").append(i).append("u");
				String name = buf.extractData();
				IdentifierExp id = new IdentifierExp(new Identifier(name,
						TOK.TOKidentifier));

				VarDeclaration v = new VarDeclaration(arg.type, id, null);
				v.semantic(sc, context);

				if (sc.scopesym != null) {
					if (sc.scopesym.members != null) {
						sc.scopesym.members.add(v);
					}
				}

				Expression e = new DsymbolExp(v);
				exps.add(e);
			}
			TupleDeclaration v2 = new TupleDeclaration(ident, exps);
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
			error("variable %s cannot be synchronized", toChars());
		} else if (isOverride()) {
			error("override cannot be applied to variable");
		} else if (isAbstract()) {
			error("abstract cannot be applied to variable");
		} else if ((storage_class & STC.STCtemplateparameter) != 0) {
		} else {
			AggregateDeclaration aad = sc.anonAgg;
			if (aad == null)
				aad = parent.isAggregateDeclaration();
			if (aad != null) {
				aad.addField(sc, this, context);
			}

			InterfaceDeclaration id = parent.isInterfaceDeclaration();
			if (id != null) {
				error("field not allowed in interface");
			}

			TemplateInstance ti = parent.isTemplateInstance();
			if (ti != null) {
				// Take care of nested templates
				while (true) {
					TemplateInstance ti2 = ti.tempdecl.parent
							.isTemplateInstance();
					if (ti2 == null)
						break;
					ti = ti2;
				}

				// If it's a member template
				AggregateDeclaration ad = ti.tempdecl.isMember();
				if (ad != null && storage_class != STC.STCundefined) {
					error("cannot use template to add field to aggregate '%s'",
							ad.toChars());
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
						&& ident.ident != Id.withSym)
					error("reference to auto class must be auto");
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
				Expression e = new IntegerExp("0", BigInteger.ZERO, Type.tint32);
				Expression e1;
				e1 = new VarExp(this);
				e = new AssignExp(e1, e);
				e.type = e1.type;
				init = new ExpInitializer(e/*.type.defaultInit()*/);
				return;
			} else if (type.ty == TY.Ttypedef) {
				TypeTypedef td = (TypeTypedef) type;
				if (td.sym.init != null) {
					init = td.sym.init;
					ExpInitializer ie = init.isExpInitializer();
					if (ie != null)
						// Make copy so we can modify it
						init = new ExpInitializer(ie.exp);
				} else
					init = getExpInitializer();
			} else {
				init = getExpInitializer();
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
					if (type.isBaseOf(ne.newtype.semantic(sc, context), null))
						onstack = 2;
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

					//printf("fd = '%s', var = '%s'\n", fd.toChars(), toChars());
					if (ei == null) {
						Expression e = init.toExpression();
						if (e == null) {
							init = init.semantic(sc, type, context);
							e = init.toExpression();
							if (e == null) {
								error("is not a static and cannot have static initializer");
								return;
							}
						}
						ei = new ExpInitializer(e);
						init = ei;
					}

					e1 = new VarExp(this);

					t = type.toBasetype(context);
					if (t.ty == TY.Tsarray) {
						dim = ((TypeSArray) t).dim.toInteger(context).intValue();
						// If multidimensional static array, treat as one large array
						while (true) {
							t = t.next.toBasetype(context);
							if (t.ty != TY.Tsarray)
								break;
							if (t.next.toBasetype(context).ty == TY.Tbit)
								// t.size() gives size in bytes, convert to bits
								dim *= t.size() * 8;
							else
								dim *= ((TypeSArray) t).dim.toInteger(context).intValue();
							e1.type = new TypeSArray(t.next, new IntegerExp("0",
									dim, Type.tindex));
						}
						e1 = new SliceExp(e1, null, null);
					} else if (t.ty == TY.Tstruct) {
						ei.exp = ei.exp.semantic(sc, context);
						if (!ei.exp.implicitConvTo(type, context))
							ei.exp = new CastExp(ei.exp, type);
					}
					ei.exp = new AssignExp(e1, ei.exp);
					ei.exp = ei.exp.semantic(sc, context);
					ei.exp.optimize(Expression.WANTvalue);
				} else {
					init = init.semantic(sc, type, context);
					if (fd != null && isConst() && !isStatic()) { // Make it static
						storage_class |= STC.STCstatic;
					}
				}
			} else if (isConst()) {
				/* Because we may need the results of a const declaration in a
				 * subsequent type, such as an array dimension, before semantic2()
				 * gets ordinarily run, try to run semantic2() now.
				 * Ignore failure.
				 */

				/* TODO semantic
				if (ei && !global.errors && !inferred) {
					int errors = global.errors;
					global.gag++;
					//printf("+gag\n");
					Expression e = ei.exp.syntaxCopy();
					inuse++;
					e = e.semantic(sc, context);
					inuse--;
					e = e.implicitCastTo(sc, type);
					global.gag--;
					//printf("-gag\n");
					if (errors != global.errors) // if errors happened
					{
						if (global.gag == 0)
							global.errors = errors; // act as if nothing happened
					} else {
						e = e.optimize(Expression.WANTvalue
								| Expression.WANTinterpret);
						if (e.op == TOK.TOKint64 || e.op == TOK.TOKstring) {
							ei.exp = e; // no errors, keep result
						}
					}
				}
				*/
			}
		}
	}
	
	@Override
	public int kind() {
		return VAR_DECLARATION;
	}
	
	@Override
	public String toString() {
		return type + " " + ident + ";";
	}

}
