package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.Constfold.ArrayLength;
import static descent.internal.compiler.parser.Constfold.Index;

import static descent.internal.compiler.parser.STC.STCconst;
import static descent.internal.compiler.parser.STC.STCforeach;
import static descent.internal.compiler.parser.STC.STCin;
import static descent.internal.compiler.parser.STC.STClazy;
import static descent.internal.compiler.parser.STC.STCout;
import static descent.internal.compiler.parser.STC.STCref;

import static descent.internal.compiler.parser.TOK.TOKdelegate;
import static descent.internal.compiler.parser.TOK.TOKforeach;
import static descent.internal.compiler.parser.TOK.TOKforeach_reverse;
import static descent.internal.compiler.parser.TOK.TOKstring;
import static descent.internal.compiler.parser.TOK.TOKtuple;
import static descent.internal.compiler.parser.TOK.TOKtype;
import static descent.internal.compiler.parser.TOK.TOKvar;

import static descent.internal.compiler.parser.TY.Taarray;
import static descent.internal.compiler.parser.TY.Tarray;
import static descent.internal.compiler.parser.TY.Tbit;
import static descent.internal.compiler.parser.TY.Tchar;
import static descent.internal.compiler.parser.TY.Tdchar;
import static descent.internal.compiler.parser.TY.Tdelegate;
import static descent.internal.compiler.parser.TY.Tfunction;
import static descent.internal.compiler.parser.TY.Tint32;
import static descent.internal.compiler.parser.TY.Tint64;
import static descent.internal.compiler.parser.TY.Tsarray;
import static descent.internal.compiler.parser.TY.Ttuple;
import static descent.internal.compiler.parser.TY.Tuns32;
import static descent.internal.compiler.parser.TY.Tuns64;
import static descent.internal.compiler.parser.TY.Twchar;

// DMD 1.020
public class ForeachStatement extends Statement {

	private final static char[] _aaApply = { '_', 'a', 'a', 'A', 'p', 'p', 'l',
			'y', };
	private final static char[] _aaApply2 = { '_', 'a', 'a', 'A', 'p', 'p',
			'l', 'y', '2' };

	public final static String[] fntab = { "cc", "cw", "cd", "wc", "cc", "wd",
			"dc", "dw", "dd" };
	public TOK op;
	public Arguments arguments;
	public Expression aggr;

	public Expression sourceAggr;

	public Statement body;
	public VarDeclaration key;

	public VarDeclaration value;

	public FuncDeclaration func; // function we're lexically in
	public List cases; // put breaks, continues, gotos and returns here

	public List gotos; // forward referenced goto's go here

	public ForeachStatement(Loc loc, TOK op, Arguments arguments,
			Expression aggr, Statement body) {
		super(loc);
		this.op = op;
		this.arguments = arguments;
		this.sourceAggr = aggr;
		this.aggr = aggr;
		this.body = body;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, arguments);
			TreeVisitor.acceptChildren(visitor, aggr);
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

	@Override
	public boolean comeFrom() {
		if (body != null) {
			return body.comeFrom();
		}
		return false;
	}

	@Override
	public boolean fallOffEnd(SemanticContext context) {
		if (body != null) {
			body.fallOffEnd(context);
		}
		return true;
	}

	@Override
	public int getNodeType() {
		return FOREACH_STATEMENT;
	}

	@Override
	public boolean hasBreak() {
		return true;
	}

	@Override
	public boolean hasContinue() {
		return true;
	}

	@Override
	public Statement inlineScan(InlineScanState iss, SemanticContext context) {
		aggr = aggr.inlineScan(iss, context);
		body = body.inlineScan(iss, context);
		return this;
	}

	@Override
	public Expression interpret(InterState istate, SemanticContext context) {
		if (istate.start == this) {
			istate.start = null;
		}
		if (istate.start != null) {
			return null;
		}

		Expression e = null;
		Expression eaggr;

		if (value.isOut() || value.isRef()) {
			return EXP_CANT_INTERPRET;
		}

		eaggr = aggr.interpret(istate, context);
		if (eaggr == EXP_CANT_INTERPRET) {
			return EXP_CANT_INTERPRET;
		}

		Expression dim = ArrayLength.call(Type.tsize_t, eaggr, context);
		if (dim == EXP_CANT_INTERPRET) {
			return EXP_CANT_INTERPRET;
		}

		Expression keysave = key != null ? key.value : null;
		Expression valuesave = value.value;

		integer_t d = dim.toUInteger(context);
		integer_t index;

		if (op == TOKforeach) {
			for (index = integer_t.ZERO; index.compareTo(d) < 0; index = index
					.add(1)) {
				Expression ekey = new IntegerExp(loc, index, Type.tsize_t);
				if (key != null) {
					key.value = ekey;
				}
				e = Index.call(value.type, eaggr, ekey, context);
				if (e == EXP_CANT_INTERPRET) {
					break;
				}
				value.value = e;

				e = body != null ? body.interpret(istate, context) : null;
				if (e == EXP_CANT_INTERPRET) {
					break;
				}
				if (e == EXP_BREAK_INTERPRET) {
					e = null;
					break;
				}
				if (e == EXP_CONTINUE_INTERPRET) {
					e = null;
				} else if (e != null) {
					break;
				}
			}
		} else // TOKforeach_reverse
		{
			for (index = d; !(index = index.subtract(1)).equals(0);) {
				Expression ekey = new IntegerExp(loc, index, Type.tsize_t);
				if (key != null) {
					key.value = ekey;
				}
				e = Index.call(value.type, eaggr, ekey, context);
				if (e == EXP_CANT_INTERPRET) {
					break;
				}
				value.value = e;

				e = body != null ? body.interpret(istate, context) : null;
				if (e == EXP_CANT_INTERPRET) {
					break;
				}
				if (e == EXP_BREAK_INTERPRET) {
					e = null;
					break;
				}
				if (e == EXP_CONTINUE_INTERPRET) {
					e = null;
				} else if (e != null) {
					break;
				}
			}
		}
		value.value = valuesave;
		if (key != null) {
			key.value = keysave;
		}
		return e;
	}

	@Override
	public Statement semantic(Scope sc, SemanticContext context) {
		ScopeDsymbol sym;
		Statement s = this;
		int dim = arguments.size();
		int i;
		TypeAArray taa = null;

		Type tn = null;
		Type tnv = null;

		func = sc.func;
		if (func.fes != null) {
			func = func.fes.func;
		}

		aggr = aggr.semantic(sc, context);
		aggr = resolveProperties(sc, aggr, context);
		if (aggr.type == null) {
			error("invalid foreach aggregate %s", aggr.toChars(context));
			return this;
		}

		inferApplyArgTypes(op, arguments, aggr, context);

		/*
		 * Check for inference errors
		 */
		if (dim != arguments.size()) {
			error("cannot uniquely infer foreach argument types");
			return this;
		}

		Type tab = aggr.type.toBasetype(context);

		if (tab.ty == Ttuple) // don't generate new scope for tuple loops
		{
			if (dim < 1 || dim > 2) {
				error("only one (value) or two (key,value) arguments for tuple foreach");
				return s;
			}

			TypeTuple tuple = (TypeTuple) tab;
			Statements statements = new Statements();
			int n = 0;
			TupleExp te = null;
			if (aggr.op == TOKtuple) {
				te = (TupleExp) aggr;
				n = te.exps.size();
			} else if (aggr.op == TOKtype) {
				n = Argument.dim(tuple.arguments, context);
			} else {
				Assert.isTrue(false);
			}
			for (int j = 0; j < n; j++) {
				int k = (op == TOKforeach) ? j : n - 1 - j;
				Expression e = null;
				Type t = null;
				if (te != null) {
					e = te.exps.get(k);
				} else {
					t = Argument.getNth(tuple.arguments, k, context).type;
				}
				Argument arg = arguments.get(0);
				Statements st = new Statements();

				if (dim == 2) { // Declare key
					if ((arg.storageClass & (STCout | STCref | STClazy)) != 0) {
						error("no storage class for %s", arg.ident.toChars());
					}
					TY keyty = arg.type.ty;
					if ((keyty != Tint32 && keyty != Tuns32)
							|| (context.global.params.isX86_64
									&& keyty != Tint64 && keyty != Tuns64)) {
						error("foreach: key type must be int or uint, not %s",
								arg.type.toChars(context));
					}
					Initializer ie = new ExpInitializer(loc, new IntegerExp(
							loc, k));
					VarDeclaration var = new VarDeclaration(loc, arg.type,
							arg.ident, ie);
					var.storage_class |= STCconst;
					DeclarationExp de = new DeclarationExp(loc, var);
					st.add(new ExpStatement(loc, de));
					arg = arguments.get(1); // value
				}
				// Declare value
				if ((arg.storageClass & (STCout | STCref | STClazy)) != 0) {
					error("no storage class for %s", arg.ident.toChars());
				}
				Dsymbol var;
				if (te != null) {
					if (e.type.toBasetype(context).ty == Tfunction
							&& e.op == TOKvar) {
						VarExp ve = (VarExp) e;
						var = new AliasDeclaration(loc, arg.ident, ve.var);
					} else {
						arg.type = e.type;
						Initializer ie = new ExpInitializer(Loc.ZERO, e);
						VarDeclaration v = new VarDeclaration(loc, arg.type,
								arg.ident, ie);
						if (e.isConst()) {
							v.storage_class |= STCconst;
						}
						//				#if V2
						//						    else
						//							v.storage_class |= STCfinal;
						//				#endif
						var = v;
					}
				} else {
					var = new AliasDeclaration(loc, arg.ident, t);
				}
				DeclarationExp de = new DeclarationExp(loc, var);
				st.add(new ExpStatement(loc, de));

				st.add(body.syntaxCopy());
				s = new CompoundStatement(loc, st);
				s = new ScopeStatement(loc, s);
				statements.add(s);
			}

			s = new UnrolledLoopStatement(loc, statements);
			s = s.semantic(sc, context);
			return s;
		}

		for (i = 0; i < dim; i++) {
			Argument arg = arguments.get(i);
			if (arg.type == null) {
				error("cannot infer type for %s", arg.ident.toChars());
				return this;
			}
		}

		sym = new ScopeDsymbol(loc);
		sym.parent = sc.scopesym;
		sc = sc.push(sym);

		sc.noctor++;

		switch (tab.ty) {
		case Tarray:
		case Tsarray:
			if (dim < 1 || dim > 2) {
				error("only one or two arguments for array foreach");
				break;
			}

			/*
			 * Look for special case of parsing char types out of char type
			 * array.
			 */
			tn = tab.next.toBasetype(context);
			if (tn.ty == Tchar || tn.ty == Twchar || tn.ty == Tdchar) {
				Argument arg;

				i = (dim == 1) ? 0 : 1; // index of value
				arg = arguments.get(i);
				arg.type = arg.type.semantic(loc, sc, context);
				tnv = arg.type.toBasetype(context);
				if (tnv.ty != tn.ty
						&& (tnv.ty == Tchar || tnv.ty == Twchar || tnv.ty == Tdchar)) {
					if ((arg.storageClass & STCref) != 0) {
						error("foreach: value of UTF conversion cannot be inout");
					}
					if (dim == 2) {
						arg = arguments.get(0);
						if ((arg.storageClass & STCref) != 0) {
							error("foreach: key cannot be inout");
						}
					}
					// goto Lapply;
					Statement[] ps = { null };
					semantic_Lapply(sc, context, dim, ps, tab, taa, tn, tnv, i);
					s = ps[0];
					break;
				}
			}

			for (i = 0; i < dim; i++) { // Declare args
				Argument arg = arguments.get(i);
				VarDeclaration var;

				var = new VarDeclaration(loc, arg.type, arg.ident, null);
				var.storage_class |= STCforeach;
				var.storage_class |= arg.storageClass
						& (STCin | STCout | STCref);
				DeclarationExp de = new DeclarationExp(loc, var);
				de.semantic(sc, context);
				if (dim == 2 && i == 0) {
					key = var;
				} else {
					value = var;
				}
			}

			sc.sbreak = this;
			sc.scontinue = this;
			body = body.semantic(sc, context);

			if (!value.type.equals(tab.next)) {
				if (aggr.op == TOKstring) {
					aggr = aggr.implicitCastTo(sc, value.type.arrayOf(context),
							context);
				} else {
					error("foreach: %s is not an array of %s", tab
							.toChars(context), value.type.toChars(context));
				}
			}

			if ((value.storage_class & STCout) != 0
					&& value.type.toBasetype(context).ty == Tbit) {
				error("foreach: value cannot be out and type bit");
			}

			if (key != null
					&& ((key.type.ty != Tint32 && key.type.ty != Tuns32) || (context.global.params.isX86_64
							&& key.type.ty != Tint64 && key.type.ty != Tuns64))) {
				error("foreach: key type must be int or uint, not %s", key.type
						.toChars(context));
			}

			if (key != null && (key.storage_class & STCout | STCref) != 0) {
				error("foreach: key cannot be out or ref");
			}
			break;

		case Taarray:
			taa = (TypeAArray) tab;
			if (dim < 1 || dim > 2) {
				error("only one or two arguments for associative array foreach");
				break;
			}
			if (op == TOKforeach_reverse) {
				error("no reverse iteration on associative arrays");
			}
			// goto Lapply
			Statement[] ps = { null };
			semantic_Lapply(sc, context, dim, ps, tab, taa, tn, tnv, i);
			s = ps[0];
			break;

		case Tclass:
		case Tstruct:
		case Tdelegate:
			// Lapply: 
		{
			Statement[] pointer_s = { null };
			semantic_Lapply(sc, context, dim, pointer_s, tab, taa, tn, tnv, i);
			s = pointer_s[0];
			break;
		}

		default:
			context.acceptProblem(Problem.newSemanticTypeError(
					IProblem.NotAnAggregateType, 0, sourceAggr.start,
					sourceAggr.length, new String[] { aggr.type.toString() }));
			break;
		}
		sc.noctor--;
		sc.pop();
		return s;
	}

	private void semantic_Lapply(Scope sc, SemanticContext context, int dim,
			Statement[] s, Type tab, TypeAArray taa, Type tn, Type tnv, int i) {
		FuncDeclaration fdapply;
		Arguments args;
		Expression ec;
		Expression e;
		FuncLiteralDeclaration fld;
		Argument a;
		Type t;
		Expression flde;
		IdentifierExp id;
		Type tret;

		tret = func.type.next;

		// Need a variable to hold value from any return statements in body.
		if (sc.func.vresult == null && tret != null && tret != Type.tvoid) {
			VarDeclaration v;

			v = new VarDeclaration(loc, tret, Id.result, null);
			v.noauto = true;
			v.semantic(sc, context);
			if (sc.insert(v) == null) {
				Assert.isTrue(false);
			}
			v.parent = sc.func;
			sc.func.vresult = v;
		}

		/*
		 * Turn body into the function literal: int delegate(ref T arg) {
		 * body }
		 */
		args = new Arguments();
		for (i = 0; i < dim; i++) {
			Argument arg = arguments.get(i);

			arg.type = arg.type.semantic(loc, sc, context);
			if ((arg.storageClass & STCref) != 0) {
				id = arg.ident;
			} else { // Make a copy of the inout argument so it isn't
				// a reference.
				VarDeclaration v;
				Initializer ie;
				id = new IdentifierExp(loc, ("__applyArg" + i).toCharArray());

				ie = new ExpInitializer(loc, id);
				v = new VarDeclaration(loc, arg.type, arg.ident, ie);
				s[0] = new DeclarationStatement(loc, v);
				body = new CompoundStatement(loc, s[0], body);
			}
			a = new Argument(STCref, arg.type, id, null);
			args.add(a);
		}
		t = new TypeFunction(args, Type.tint32, 0, LINK.LINKd);
		fld = new FuncLiteralDeclaration(loc, t, TOKdelegate, this);
		fld.fbody = body;
		flde = new FuncExp(loc, fld);
		flde = flde.semantic(sc, context);

		// Resolve any forward referenced goto's
		if (gotos != null) {
			for (int j = 0; j < gotos.size(); j++) {
				CompoundStatement cs = (CompoundStatement) gotos.get(j);
				GotoStatement gs = (GotoStatement) cs.statements.get(0);

				if (gs.label.statement == null) { // 'Promote' it to this scope, and replace with a return
					cases.add(gs);
					s[0] = new ReturnStatement(loc, new IntegerExp(loc, cases
							.size() + 1));
					cs.statements.set(0, s[0]);
				}
			}
		}

		if (tab.ty == Taarray) {
			// Check types
			Argument arg = arguments.get(0);
			if (dim == 2) {
				if ((arg.storageClass & STCref) != 0) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.ForeachIndexCannotBeRef, 0, arg.start,
							arg.length));
				}
				if (!arg.type.equals(taa.index)) {
					error("foreach: index must be type %s, not %s", taa.index
							.toChars(context), arg.type.toChars(context));
				}
				arg = arguments.get(1);
			}
			if (!arg.type.equals(taa.next)) {
				error("foreach: value must be type %s, not %s", taa.next
						.toChars(context), arg.type.toChars(context));
			}

			/*
			 * Call: _aaApply(aggr, keysize, flde)
			 */
			if (dim == 2) {
				fdapply = context.genCfunc(Type.tindex, _aaApply2);
			} else {
				fdapply = context.genCfunc(Type.tindex, _aaApply);
			}
			ec = new VarExp(loc, fdapply);
			Expressions exps = new Expressions();
			exps.add(aggr);
			int keysize = taa.key.size(loc, context);
			keysize = (keysize + 3) & ~3;
			exps.add(new IntegerExp(loc, keysize, Type.tint32));
			exps.add(flde);
			e = new CallExp(loc, ec, exps);
			e.type = Type.tindex; // don't run semantic() on e
		} else if (tab.ty == Tarray || tab.ty == Tsarray) {
			/*
			 * Call: _aApply(aggr, flde)
			 */
			int flag = 0;

			switch (tn.ty) {
			case Tchar:
				flag = 0;
				break;
			case Twchar:
				flag = 3;
				break;
			case Tdchar:
				flag = 6;
				break;
			default:
				Assert.isTrue(false);
			}
			switch (tnv.ty) {
			case Tchar:
				flag += 0;
				break;
			case Twchar:
				flag += 1;
				break;
			case Tdchar:
				flag += 2;
				break;
			default:
				Assert.isTrue(false);
			}

			String r = (op == TOKforeach_reverse) ? "R" : "";
			String fdname = "_aApply" + r + 2 + "." + fntab[flag] + dim;

			fdapply = context.genCfunc(Type.tindex, fdname.toCharArray());

			ec = new VarExp(loc, fdapply);
			Expressions exps = new Expressions();
			if (tab.ty == Tsarray) {
				aggr = aggr.castTo(sc, tn.arrayOf(context), context);
			}
			exps.add(aggr);
			exps.add(flde);
			e = new CallExp(loc, ec, exps);
			e.type = Type.tindex; // don't run semantic() on e
		} else if (tab.ty == Tdelegate) {
			/*
			 * Call: aggr(flde)
			 */
			Expressions exps = new Expressions();
			exps.add(flde);
			e = new CallExp(loc, aggr, exps);
			e = e.semantic(sc, context);
			if (e.type != Type.tint32) {
				error("opApply() function for %s must return an int", tab
						.toChars(context));
			}
		} else {
			/*
			 * Call: aggr.apply(flde)
			 */
			ec = new DotIdExp(loc, aggr, new IdentifierExp(loc,
					(op == TOKforeach_reverse) ? Id.applyReverse : Id.apply));
			Expressions exps = new Expressions();
			exps.add(flde);
			e = new CallExp(loc, ec, exps);
			e = e.semantic(sc, context);
			if (e.type != Type.tint32) {
				error("opApply() function for %s must return an int", tab
						.toChars(context));
			}
		}

		if (size(cases) == 0) {
			// Easy case, a clean exit from the loop
			s[0] = new ExpStatement(loc, e);
		} else { // Construct a switch statement around the return value
			// of the apply function.
			Statements a2 = new Statements();

			// default: break; takes care of cases 0 and 1
			s[0] = new BreakStatement(loc, null);
			s[0] = new DefaultStatement(loc, s[0]);
			a2.add(s[0]);

			// cases 2...
			for (int j = 0; j < cases.size(); j++) {
				s[0] = (Statement) cases.get(j);
				s[0] = new CaseStatement(loc, new IntegerExp(loc, i + 2), s[0]);
				a2.add(s[0]);
			}

			s[0] = new CompoundStatement(loc, a2);
			s[0] = new SwitchStatement(loc, e, s[0]);
			s[0] = s[0].semantic(sc, context);
		}
	}

	@Override
	public Statement syntaxCopy() {
		Arguments args = Argument.arraySyntaxCopy(arguments);
		Expression exp = aggr.syntaxCopy();
		ForeachStatement s = new ForeachStatement(loc, op, args, exp,
				body != null ? body.syntaxCopy() : null);
		return s;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		buf.writestring(op.toString());
		buf.writestring(" (");
		for (int i = 0; i < arguments.size(); i++) {
			Argument a = arguments.get(i);
			if (i != 0) {
				buf.writestring(", ");
			}
			if ((a.storageClass & STCref) != 0) {
				buf
						.writestring((context.global.params.Dversion == 1) ? "inout "
								: "ref ");
			}
			if (a.type != null) {
				a.type.toCBuffer(buf, a.ident, hgs, context);
			} else {
				buf.writestring(a.ident.toChars());
			}
		}
		buf.writestring("; ");
		aggr.toCBuffer(buf, hgs, context);
		buf.writebyte(')');
		buf.writenl();
		buf.writebyte('{');
		buf.writenl();
		if (body != null) {
			body.toCBuffer(buf, hgs, context);
		}
		buf.writebyte('}');
		buf.writenl();
	}

	@Override
	public boolean usesEH() {
		return body.usesEH();
	}

}
