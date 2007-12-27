package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;
import static descent.internal.compiler.parser.ILS.ILSno;
import static descent.internal.compiler.parser.ILS.ILSuninitialized;
import static descent.internal.compiler.parser.ILS.ILSyes;
import static descent.internal.compiler.parser.LINK.LINKc;
import static descent.internal.compiler.parser.LINK.LINKd;
import static descent.internal.compiler.parser.PROT.PROTexport;
import static descent.internal.compiler.parser.PROT.PROTprivate;

import static descent.internal.compiler.parser.STC.STCabstract;
import static descent.internal.compiler.parser.STC.STCauto;
import static descent.internal.compiler.parser.STC.STCdeprecated;
import static descent.internal.compiler.parser.STC.STCin;
import static descent.internal.compiler.parser.STC.STClazy;
import static descent.internal.compiler.parser.STC.STCout;
import static descent.internal.compiler.parser.STC.STCparameter;
import static descent.internal.compiler.parser.STC.STCref;
import static descent.internal.compiler.parser.STC.STCscope;
import static descent.internal.compiler.parser.STC.STCstatic;
import static descent.internal.compiler.parser.STC.STCvariadic;

import static descent.internal.compiler.parser.TOK.TOKvar;

import static descent.internal.compiler.parser.TY.Tarray;
import static descent.internal.compiler.parser.TY.Tchar;
import static descent.internal.compiler.parser.TY.Tclass;
import static descent.internal.compiler.parser.TY.Tfunction;
import static descent.internal.compiler.parser.TY.Tident;
import static descent.internal.compiler.parser.TY.Tinstance;
import static descent.internal.compiler.parser.TY.Tint32;
import static descent.internal.compiler.parser.TY.Tpointer;
import static descent.internal.compiler.parser.TY.Tsarray;
import static descent.internal.compiler.parser.TY.Ttuple;
import static descent.internal.compiler.parser.TY.Tvoid;

// DMD 1.020
public class FuncDeclaration extends Declaration implements IFuncDeclaration {

	private final static char[] missing_return_expression = { 'm', 'i', 's',
			's', 'i', 'n', 'g', ' ', 'r', 'e', 't', 'u', 'r', 'n', ' ', 'e',
			'x', 'p', 'r', 'e', 's', 's', 'i', 'o', 'n' };
	private final static char[] null_this = { 'n', 'u', 'l', 'l', ' ', 't',
			'h', 'i', 's' };

	public List fthrows; // Array of Type's of exceptions (not used)
	public Statement fensure;
	public Statement frequire;
	public Statement fbody;
	public Statement sourceFensure;
	public Statement sourceFrequire;
	public Statement sourceFbody;
	public IdentifierExp outId;
	public int vtblIndex; // for member functions, index into vtbl[]
	public boolean introducing; // !=0 if 'introducing' function
	public Type tintro; // if !=NULL, then this is the type
	// of the 'introducing' function
	// this one is overriding
	public Declaration overnext; // next in overload list
	public Scope scope; // !=NULL means context to use
	public int semanticRun; // !=0 if semantic3() had been run
	public IDsymbolTable localsymtab; // used to prevent symbols in different
	// scopes from having the same name
	public ForeachStatement fes; // if foreach body, this is the foreach
	public VarDeclaration vthis; // 'this' parameter (member and nested)
	public VarDeclaration v_arguments; // '_arguments' parameter
	public Dsymbols parameters; // Array of VarDeclaration's for parameters
	public IDsymbolTable labtab; // statement label symbol table
	public VarDeclaration vresult; // variable corresponding to outId
	public LabelDsymbol returnLabel; // where the return goes
	public boolean inferRetType;
	public boolean naked; // !=0 if naked
	public boolean inlineAsm; // !=0 if has inline assembler
	public ILS inlineStatus;
	public int inlineNest; // !=0 if nested inline
	public boolean cantInterpret;
	public boolean nestedFrameRef;
	public int hasReturnExp; // 1 if there's a return exp; statement
	// 2 if there's a throw statement
	// 4 if there's an assert(0)
	// 8 if there's inline asm

	// Support for NRVO (named return value optimization)
	public int nrvo_can; // !=0 means we can do it
	public IVarDeclaration nrvo_var; // variable to replace with shidden

	public FuncDeclaration(Loc loc, IdentifierExp ident, int storage_class,
			Type type) {
		super(ident);
		this.loc = loc;
		this.storage_class = storage_class;
		this.type = type;
		this.sourceType = type;
		this.loc = loc;
		this.vtblIndex = -1;
		this.inlineStatus = ILSuninitialized;
		this.inferRetType = (type != null && type.nextOf() == null);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, ident);
			// Template args?
			TreeVisitor.acceptChildren(visitor, parameters);
			TreeVisitor.acceptChildren(visitor, sourceFrequire);
			TreeVisitor.acceptChildren(visitor, sourceFbody);
			TreeVisitor.acceptChildren(visitor, outId);
			TreeVisitor.acceptChildren(visitor, sourceFensure);
		}
		visitor.endVisit(this);
	}

	public boolean addPostInvariant(SemanticContext context) {
		IAggregateDeclaration ad = isThis();
		return (ad != null
				&& ad.inv() != null
				&& context.global.params.useInvariants
				&& (protection == PROT.PROTpublic || protection == PROT.PROTexport) && !naked);
	}

	public boolean addPreInvariant(SemanticContext context) {
		IAggregateDeclaration ad = isThis();
		return (ad != null
				&& context.global.params.useInvariants
				&& (protection == PROT.PROTpublic || protection == PROT.PROTexport) && !naked);
	}

	public void appendExp(Expression e) {
		Statement s;

		s = new ExpStatement(Loc.ZERO, e);
		appendState(s);
	}

	public void appendState(Statement s) {
		CompoundStatement cs;

		if (null == fbody) {
			Statements a;

			a = new Statements();
			fbody = new CompoundStatement(Loc.ZERO, a);
		}
		cs = fbody.isCompoundStatement();
		cs.statements.add(s);
	}

	public void bodyToCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		if (fbody != null
				&& (!hgs.hdrgen || hgs.tpltMember || canInline(true, true,
						context))) {
			buf.writenl();

			// in{}
			if (frequire != null) {
				buf.writestring("in");
				buf.writenl();
				frequire.toCBuffer(buf, hgs, context);
			}

			// out{}
			if (fensure != null) {
				buf.writestring("out");
				if (outId != null) {
					buf.writebyte('(');
					buf.writestring(outId.toChars(context));
					buf.writebyte(')');
				}
				buf.writenl();
				fensure.toCBuffer(buf, hgs, context);
			}

			if (frequire != null || fensure != null) {
				buf.writestring("body");
				buf.writenl();
			}

			buf.writebyte('{');
			buf.writenl();
			fbody.toCBuffer(buf, hgs, context);
			buf.writebyte('}');
			buf.writenl();
		} else {
			buf.writeByte(';');
			buf.writenl();
		}
	}

	public boolean canInline(boolean hasthis, boolean hdrscan,
			SemanticContext context) {
		InlineCostState ics = new InlineCostState();
		int cost;

		if (needThis() && !hasthis) {
			return false;
		}

		if (inlineNest != 0 || (semanticRun == 0 && !hdrscan)) {
			return false;
		}

		switch (inlineStatus) {
		case ILSyes:
			return true;

		case ILSno:
			return false;

		case ILSuninitialized:
			break;

		default:
			throw new IllegalStateException("assert(0);");
		}

		if (type != null) {
			if (type.ty != Tfunction) {
				throw new IllegalStateException("assert(type.ty == Tfunction);");
			}
			TypeFunction tf = (TypeFunction) type;
			if (tf.varargs == 1) { // no variadic parameter lists
				// goto Lno;
				if (!hdrscan) {
					inlineStatus = ILSno;
				}
				return false;
			}

			/* Don't inline a function that returns non-void, but has
			 * no return expression.
			 */
			if (tf.next != null && tf.next.ty != Tvoid
					&& (hasReturnExp & 1) == 0 && !hdrscan) {
				// goto Lno
				if (!hdrscan) {
					inlineStatus = ILSno;
				}
				return false;
			}
		} else {
			CtorDeclaration ctor = isCtorDeclaration();

			if (ctor != null && ctor.varargs == 1) {
				// goto Lno
				if (!hdrscan) {
					inlineStatus = ILSno;
				}
				return false;
			}
		}

		if (fbody == null || !hdrscan
				&& (isSynchronized() || isImportedSymbol() || nestedFrameRef || // no nested references to this frame
				(isVirtual(context) && !isFinal()))) {
			// goto Lno;
			if (!hdrscan) {
				inlineStatus = ILSno;
			}
			return false;
		}

		/* If any parameters are Tsarray's (which are passed by reference)
		 * or out parameters (also passed by reference), don't do inlining.
		 */
		if (parameters != null) {
			for (int i = 0; i < parameters.size(); i++) {
				VarDeclaration v = (VarDeclaration) parameters.get(i);
				if (v.isOut() || v.isRef()
						|| v.type.toBasetype(context).ty == Tsarray) {
					// goto Lno;
					if (!hdrscan) {
						inlineStatus = ILSno;
					}
					return false;
				}
			}
		}

		// memset(&ics, 0, sizeof(ics));
		ics.nested = 0;
		ics.hasthis = hasthis;
		ics.fd = this;
		ics.hdrscan = hdrscan;
		cost = fbody.inlineCost(ics, context);
		if (cost >= COST_MAX) {
			// goto Lno;
			if (!hdrscan) {
				inlineStatus = ILSno;
			}
			return false;
		}

		if (!hdrscan) {
			inlineScan(context);
		}

		// Lyes:
		if (!hdrscan) {
			inlineStatus = ILSyes;
		}
		return true;
	}

	public boolean canInline(boolean hasthis, SemanticContext context) {
		return canInline(hasthis, false, context);
	}

	public Expression doInline(InlineScanState iss, Expression ethis,
			List arguments, SemanticContext context) {
		InlineDoState ids;
		DeclarationExp de;
		Expression e = null;

		// memset(&ids, 0, sizeof(ids));
		ids = new InlineDoState();

		ids.parent = iss.fd;

		// Set up vthis
		if (ethis != null) {
			VarDeclaration vthis;
			ExpInitializer ei;
			VarExp ve;

			if (ethis.type.ty != Tclass && ethis.type.ty != Tpointer) {
				ethis = ethis.addressOf(null, context);
			}

			ei = new ExpInitializer(ethis.loc, ethis);

			vthis = new VarDeclaration(ethis.loc, ethis.type, Id.This, ei);
			vthis.storage_class = STCin;
			vthis.linkage = LINKd;
			vthis.parent = iss.fd;

			ve = new VarExp(vthis.loc, vthis);
			ve.type = vthis.type;

			ei.exp = new AssignExp(vthis.loc, ve, ethis);
			ei.exp.type = ve.type;

			ids.vthis = vthis;
		}

		// Set up parameters
		if (ethis != null) {
			e = new DeclarationExp(Loc.ZERO, ids.vthis);
			e.type = Type.tvoid;
		}

		if (arguments != null && arguments.size() != 0) {
			if (parameters.size() != arguments.size()) {
				throw new IllegalStateException(
						"assert(parameters.size() == arguments.size());");
			}

			for (int i = 0; i < arguments.size(); i++) {
				VarDeclaration vfrom = (VarDeclaration) parameters.get(i);
				VarDeclaration vto;
				Expression arg = (Expression) arguments.get(i);
				ExpInitializer ei;
				VarExp ve;

				ei = new ExpInitializer(arg.loc, arg);

				vto = new VarDeclaration(vfrom.loc, vfrom.type, vfrom.ident, ei);
				vto.storage_class |= vfrom.storage_class
						& (STCin | STCout | STClazy | STCref);
				vto.linkage = vfrom.linkage;
				vto.parent = iss.fd;

				ve = new VarExp(vto.loc, vto);
				//ve.type = vto.type;
				ve.type = arg.type;

				ei.exp = new AssignExp(vto.loc, ve, arg);
				ei.exp.type = ve.type;

				ids.from.add(vfrom);
				ids.to.add(vto);

				de = new DeclarationExp(Loc.ZERO, vto);
				de.type = Type.tvoid;

				e = Expression.combine(e, de);
			}
		}

		inlineNest++;
		Expression eb = fbody.doInline(ids);
		inlineNest--;
		return Expression.combine(e, eb);
	}

	public int getLevel(Loc loc, IFuncDeclaration fd, SemanticContext context) {
		int level;
		IDsymbol s;
		IDsymbol fdparent;

		fdparent = fd.toParent2();
		if (fdparent == this) {
			return -1;
		}
		s = this;
		level = 0;
		while (fd != s && fdparent != s.toParent2()) {
			IFuncDeclaration thisfd = s.isFuncDeclaration();
			if (thisfd != null) {
				if (!thisfd.isNested() && null == thisfd.vthis()) {
					// goto Lerr;
					context.acceptProblem(Problem.newSemanticTypeErrorLoc(IProblem.CannotAccessFrameOfFunction, this, new String[] { fd.toChars(context) }));
					return 1;
				}
			} else {
				IClassDeclaration thiscd = s.isClassDeclaration();
				if (thiscd != null) {
					if (!thiscd.isNested()) {
						// goto Lerr;
						context.acceptProblem(Problem.newSemanticTypeErrorLoc(IProblem.CannotAccessFrameOfFunction, this, new String[] { fd.toChars(context) }));
						return 1;
					}
				} else {
					// goto Lerr;
					context.acceptProblem(Problem.newSemanticTypeErrorLoc(IProblem.CannotAccessFrameOfFunction, this, new String[] { fd.toChars(context) }));
					return 1;
				}
			}

			s = s.toParent2();
			if (s == null) {
				throw new IllegalStateException("assert(s);");
			}
			level++;
		}
		return level;
	}

	@Override
	public int getNodeType() {
		return FUNC_DECLARATION;
	}

	@Override
	public void inlineScan(SemanticContext context) {
		InlineScanState iss = new InlineScanState();
		iss.fd = this;
		if (fbody != null) {
			inlineNest++;
			fbody = fbody.inlineScan(iss, context);
			inlineNest--;
		}
	}

	public Expression interpret(InterState istate, Expressions arguments,
			SemanticContext context) {
		if (context.global.errors != 0) {
			return null;
		}
		if (equals(ident, Id.aaLen)) {
			return interpret_aaLen(istate, arguments, context);
		} else if (equals(ident, Id.aaKeys)) {
			return interpret_aaKeys(istate, arguments, context);
		} else if (equals(ident, Id.aaValues)) {
			return interpret_aaValues(istate, arguments, context);
		}

		if (cantInterpret || semanticRun == 1) {
			return null;
		}

		if (needThis() || isNested() || null == fbody) {
			cantInterpret = true;
			return null;
		}

		if (semanticRun == 0 && scope != null) {
			semantic3(scope, context);
		}
		if (semanticRun < 2) {
			return null;
		}

		Type tb = type.toBasetype(context);

		if (!(tb.ty == Tfunction)) {
			throw new IllegalStateException("assert(tb.ty == Tfunction);");
		}
		TypeFunction tf = (TypeFunction) tb;
		@SuppressWarnings("unused")
		Type tret = tf.next.toBasetype(context);
		if (tf.varargs != 0 /*|| tret.ty == Tvoid*/) {
			cantInterpret = true;
			return null;
		}

		if (tf.parameters != null) {
			int dim = Argument.dim(tf.parameters, context);
			for (int i = 0; i < dim; i++) {
				Argument arg = Argument.getNth(tf.parameters, i, context);
				if ((arg.storageClass & STClazy) != 0) {
					cantInterpret = true;
					return null;
				}
			}
		}

		InterState istatex = new InterState();
		istatex.caller = istate;
		istatex.fd = this;

		Expressions vsave = new Expressions();
		int dim = 0;
		if (arguments != null) {
			dim = arguments.size();

			if (!(0 == dim || parameters.size() == dim)) {
				throw new IllegalStateException(
						"assert(!dim || parameters.dim == dim);");
			}

			vsave.setDim(dim);

			for (int i = 0; i < dim; i++) {
				Expression earg = arguments.get(i);
				Argument arg = Argument.getNth(tf.parameters, i, context);
				VarDeclaration v = (VarDeclaration) parameters.get(i);
				vsave.set(i, v.value);
				if ((arg.storageClass & (STCout | STCref)) != 0) {
					/* Bind out or ref parameter to the corresponding
					 * variable v2
					 */
					if (null == istate || earg.op != TOKvar) {
						return null; // can't bind to non-interpreted vars
					}

					IVarDeclaration v2;
					while (true) {
						VarExp ve = (VarExp) earg;
						v2 = ve.var.isVarDeclaration();
						if (null == v2) {
							return null;
						}
						if (null == v2.value() || v2.value().op != TOKvar) {
							break;
						}
						earg = v2.value();
					}

					v.value = new VarExp(earg.loc, v2);

					/* Don't restore the value of v2 upon function return
					 */
					if (istate != null) {
						throw new IllegalStateException("assert(istate);");
					}
					for (int j = 0; j < istate.vars.size(); j++) {
						VarDeclaration v3 = (VarDeclaration) istate.vars.get(j);
						if (v3 == v2) {
							istate.vars.set(j, null);
							break;
						}
					}
				} else { /* Value parameters
				 */
					earg = earg.interpret(istatex, context);
					if (earg == EXP_CANT_INTERPRET) {
						return null;
					}
					v.value = earg;
				}
			}
		}

		/* Save the values of the local variables used
		 */
		Expressions valueSaves = new Expressions();
		if (istate != null) {
			valueSaves.setDim(size(istate.vars));
			for (int i = 0; i < size(istate.vars); i++) {
				VarDeclaration v = (VarDeclaration) istate.vars.get(i);
				if (v != null) {
					valueSaves.set(i, v.value);
					v.value = null;
				}
			}
		}

		Expression e = null;

		while (true) {
			try {
				e = fbody.interpret(istatex, context);
			} catch (StackOverflowError error) {
				istate.stackOverflow = true;
				e = EXP_CANT_INTERPRET;
			}
			if (e == EXP_CANT_INTERPRET) {
				e = null;
			}

			/* This is how we deal with a recursive statement AST
			 * that has arbitrary goto statements in it.
			 * Bubble up a 'result' which is the target of the goto
			 * statement, then go recursively down the AST looking
			 * for that statement, then execute starting there.
			 */
			if (e == EXP_GOTO_INTERPRET) {
				istatex.start = istatex.gotoTarget; // set starting statement
				istatex.gotoTarget = null;
			} else {
				break;
			}
		}

		/* Restore the parameter values
		 */
		for (int i = 0; i < dim; i++) {
			VarDeclaration v = (VarDeclaration) parameters.get(i);
			v.value = vsave.get(i);
		}

		if (istate != null) {
			/* Restore the variable values
			 */
			for (int i = 0; i < size(istate.vars); i++) {
				VarDeclaration v = (VarDeclaration) istate.vars.get(i);
				if (v != null) {
					v.value = valueSaves.get(i);
				}
			}
		}

		return e;
	}

	@Override
	public boolean isAbstract() {
		return (storage_class & STCabstract) != 0;
	}

	@Override
	public boolean isCodepseg() {
		return true; // functions are always in the code segment
	}

	public boolean isDllMain() {
		return equals(ident, Id.DllMain)
				&& linkage != LINKc && null == isMember();
	}

	@Override
	public boolean isExport() {
		return protection == PROTexport;
	}

	@Override
	public FuncDeclaration isFuncDeclaration() {
		return this;
	}

	@Override
	public boolean isImportedSymbol() {
		return (protection == PROTexport) && null == fbody;
	}

	public boolean isMain() {
		return ident != null && equals(ident, Id.main)
				&& linkage != LINK.LINKc && isMember() == null && !isNested();
	}

	public IAggregateDeclaration isMember2() {
		IAggregateDeclaration ad;

		ad = null;
		for (IDsymbol s = this; s != null; s = s.parent()) {
			ad = s.isMember();
			if (ad != null) {
				break;
			}
			if (s.parent() == null || s.parent().isTemplateInstance() == null) {
				break;
			}
		}
		return ad;
	}

	public boolean isNested() {
		return ((storage_class & STCstatic) == 0)
				&& (toParent2().isFuncDeclaration() != null);
	}

	@Override
	public IAggregateDeclaration isThis() {
		return SemanticMixin.isThis(this);
	}

	public boolean isVirtual(SemanticContext context) {
		return SemanticMixin.isVirtual(this, context);
	}

	public boolean isWinMain() {
		return equals(ident, Id.WinMain)
				&& linkage != LINKc && null == isMember();
	}

	@Override
	public String kind() {
		return "function";
	}

	@Override
	public String mangle(SemanticContext context) {
		if (isMain()) {
			return "_Dmain";
		}

		return super.mangle(context);
	}

	@Override
	public boolean needThis() {
		boolean i = isThis() != null;
		if (!i && isFuncAliasDeclaration() != null) {
			i = ((FuncAliasDeclaration) this).funcalias.needThis();
		}
		return i;
	}

	public IFuncDeclaration overloadExactMatch(Type t, SemanticContext context) {
		return SemanticMixin.overloadExactMatch(this, t, context);
	}

	@Override
	public boolean overloadInsert(Dsymbol s, SemanticContext context) {
		FuncDeclaration f;
		AliasDeclaration a;

		a = s.isAliasDeclaration();
		if (a != null) {
			if (overnext != null) {
				return overnext.overloadInsert(a, context);
			}
			if (a.aliassym == null && a.type.ty != Tident
					&& a.type.ty != Tinstance) {
				return false;
			}
			overnext = a;
			return true;
		}
		f = s.isFuncDeclaration();
		if (f == null) {
			return false;
		}

		if (type != null && f.type != null
				&& // can be NULL for overloaded constructors
				f.type.covariant(type, context) != 0
				&& isFuncAliasDeclaration() == null) {
			return false;
		}

		if (overnext != null) {
			return overnext.overloadInsert(f, context);
		}
		overnext = f;
		return true;
	}

	// Modified to add the caller's start and length, to signal a better error
	public IFuncDeclaration overloadResolve(Expressions arguments,
			SemanticContext context, ASTDmdNode caller) {
		return SemanticMixin.overloadResolve(this, arguments, context, caller);
	}

	public boolean overrides(IFuncDeclaration fd, SemanticContext context) {
		return SemanticMixin.overrides(this, fd, context);
	}

	public LabelDsymbol searchLabel(IdentifierExp ident) {
		IDsymbol s;

		if (null == labtab) {
			labtab = new DsymbolTable(); // guess we need one
		}

		s = labtab.lookup(ident);
		if (null == s) {
			s = new LabelDsymbol(ident);
			labtab.insert(s);
		}
		return (LabelDsymbol) s;
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		boolean gotoL1 = false;
		boolean gotoL2 = false;
		boolean gotoLmainerr = false;

		TypeFunction f;
		IStructDeclaration sd;
		IClassDeclaration cd;
		IInterfaceDeclaration id;

		if (type.nextOf() != null) {
			type = type.semantic(loc, sc, context);
		}

		if (type.ty != Tfunction) {
			context.acceptProblem(Problem.newSemanticTypeErrorLoc(
					IProblem.SymbolMustBeAFunction, this, new String[] { toChars(context) }));
			return;
		}
		f = (TypeFunction) (type);
		int nparams = Argument.dim(f.parameters, context);

		linkage = sc.linkage;
		// if (!parent)
		{
			// parent = sc.scopesym;
			parent = sc.parent;
		}
		protection = sc.protection;
		storage_class |= sc.stc;

		IDsymbol parent = toParent();

		if (isConst() || isAuto() || isScope()) {
			context.acceptProblem(Problem.newSemanticTypeErrorLoc(
					IProblem.FunctionsCannotBeConstOrAuto, this));
		}

		if (isAbstract() && !isVirtual(context)) {
			context.acceptProblem(Problem.newSemanticTypeErrorLoc(
					IProblem.NonVirtualFunctionsCannotBeAbstract, this));
		}

		sd = parent.isStructDeclaration();
		if (sd != null) {
			// Verify no constructors, destructors, etc.
			if (isCtorDeclaration() != null || isDtorDeclaration() != null
			//|| isInvariantDeclaration()
			//|| isUnitTestDeclaration()
			) {
				context.acceptProblem(Problem.newSemanticTypeErrorLoc(IProblem.SpecialMemberFunctionsNotAllowedForSymbol, this, new String[] { sd.kind() }));
			}
		}

		id = parent.isInterfaceDeclaration();
		if (id != null) {
			storage_class |= STCabstract;

			if (isCtorDeclaration() != null || isDtorDeclaration() != null
					|| isInvariantDeclaration() != null
					|| isUnitTestDeclaration() != null
					|| isNewDeclaration() != null || isDelete()) {
				context.acceptProblem(Problem.newSemanticTypeErrorLoc(IProblem.SpecialFunctionsNotAllowedInInterface, this, new String[] { id.toChars(context) }));
			}
			if (fbody != null) {
				context.acceptProblem(Problem.newSemanticTypeErrorLoc(IProblem.FunctionBodyIsNotAbstractInInterface, this, new String[] { id.toChars(context) }));
			}
		}

		cd = parent.isClassDeclaration();
		if (cd != null) {
			int vi;

			if (isCtorDeclaration() != null) {
				// ctor = (CtorDeclaration *)this;
				// if (!cd.ctor)
				// cd.ctor = ctor;
				return;
			}

			if ((storage_class & STCabstract) != 0) {
				cd.isabstract(true);
			}

			// if static function, do not put in vtbl[]
			if (!isVirtual(context)) {
				return;
			}

			// Find index of existing function in vtbl[] to override
			if (cd.baseClass() != null) {
				for (vi = 0; vi < cd.baseClass().vtbl().size() && !gotoL1; vi++) {
					IFuncDeclaration fdv = ((IDsymbol) cd.vtbl().get(vi))
							.isFuncDeclaration();

					// BUG: should give error if argument types match,
					// but return type does not?

					if (fdv != null && equals(fdv.ident(), ident)) {
						int cov = type.covariant(fdv.type(), context);

						if (cov == 2) {
							context.acceptProblem(Problem.newSemanticTypeErrorLoc(
									IProblem.FunctionOfTypeOverridesButIsNotCovariant, this, new String[] { toChars(context), type.toChars(context), fdv.toPrettyChars(context), fdv.type().toChars(context) }));
						}
						if (cov == 1) {
							if (fdv.isFinal()) {
								context.acceptProblem(Problem.newSemanticTypeErrorLoc(
										IProblem.CannotOverrideFinalFunction, this, new String[] { fdv.toPrettyChars(context) }));
							}
							if (fdv.toParent() == parent) {
								// If both are mixins, then error.
								// If either is not, the one that is not
								// overrides
								// the other.
								if (fdv.parent().isClassDeclaration() != null) {
									// goto L1;
									gotoL1 = true;
								}

								if (!gotoL1) {
									if (context.BREAKABI) {
										if (this.parent.isClassDeclaration() == null) {
											context.acceptProblem(Problem.newSemanticTypeErrorLoc(
													IProblem.MultipleOverridesOfSameFunction, this));
										}
									} else {
										if (this.parent.isClassDeclaration() == null
												&& isDtorDeclaration() == null) {
											context.acceptProblem(Problem.newSemanticTypeErrorLoc(
													IProblem.MultipleOverridesOfSameFunction, this));
										}
									}
									context.acceptProblem(Problem.newSemanticTypeErrorLoc(
											IProblem.MultipleOverridesOfSameFunction, this));
								}
							}

							if (!gotoL1) {
								cd.vtbl().set(vi, this);
								vtblIndex = vi;

								/*
								 * This works by whenever this function is called,
								 * it actually returns tintro, which gets
								 * dynamically cast to type. But we know that tintro
								 * is a base of type, so we could optimize it by not
								 * doing a dynamic cast, but just subtracting the
								 * isBaseOf() offset if the value is != null.
								 */

								if (fdv.tintro() != null) {
									tintro = fdv.tintro();
								} else if (!type.equals(fdv.type())) {
									/*
									 * Only need to have a tintro if the vptr
									 * offsets differ
									 */
									int[] offset = { 0 };
									if (fdv.type().nextOf().isBaseOf(
											type.nextOf(), offset, context)) {
										tintro = fdv.type();
									}
								}
							}

							// goto L1;
							gotoL1 = true;
						}

						if (!gotoL1) {
							if (cov == 3) {
								cd.sizeok(2); // can't finish due to forward
								// reference
								return;
							}
						}
					}
				}
			}

			// This is an 'introducing' function.
			if (!gotoL1) {

				// Verify this doesn't override previous final function
				if (cd.baseClass() != null) {
					IDsymbol s = cd.baseClass().search(loc, ident, 0, context);
					if (s != null) {
						IFuncDeclaration f2 = s.isFuncDeclaration();
						f2 = f2.overloadExactMatch(type, context);
						if (f2 != null && f2.isFinal()
								&& f2.prot() != PROTprivate) {
							context.acceptProblem(Problem.newSemanticTypeErrorLoc(
									IProblem.CannotOverrideFinalFunctions, this, new String[] {
											new String(ident.ident),
											new String(cd.ident().ident) }));
						}
					}
				}
				if (isFinal()) {
					cd.vtblFinal().add(this);
				} else {
					// Append to end of vtbl[]
					introducing = true;
					vi = cd.vtbl().size();
					cd.vtbl().add(this);
					vtblIndex = vi;
				}
			}

			// L1: ;

			/*
			 * Go through all the interface bases. If this function is covariant
			 * with any members of those interface functions, set the tintro.
			 */
			for (int i = 0; i < cd.interfaces().size() && !gotoL2; i++) {
				BaseClass b = cd.interfaces().get(i);
				for (vi = 0; vi < b.base.vtbl().size() && !gotoL2; vi++) {
					IDsymbol s = (Dsymbol) b.base.vtbl().get(vi);
					IFuncDeclaration fdv = s.isFuncDeclaration();
					if (fdv != null && equals(fdv.ident(), ident)) {
						int cov = type.covariant(fdv.type(), context);
						if (cov == 2) {
							context.acceptProblem(Problem.newSemanticTypeErrorLoc(
									IProblem.FunctionOfTypeOverridesButIsNotCovariant, this, new String[] { toChars(context), type.toChars(context), fdv.toPrettyChars(context), fdv.type().toChars(context) }));
						}
						if (cov == 1) {
							Type ti = null;

							if (fdv.tintro() != null) {
								ti = fdv.tintro();
							} else if (!type.equals(fdv.type())) {
								/*
								 * Only need to have a tintro if the vptr
								 * offsets differ
								 */
								int[] offset = { 0 };
								if (fdv.type().nextOf().isBaseOf(type.nextOf(),
										offset, context)) {
									ti = fdv.type();

								}
							}
							if (ti != null) {
								if (tintro != null && !tintro.equals(ti)) {
									context.acceptProblem(Problem.newSemanticTypeErrorLoc(
											IProblem.IncompatibleCovariantTypes, this, new String[] { tintro.toChars(context), ti.toChars(context) }));
								}
								tintro = ti;
							}
							// goto L2;
							gotoL2 = true;
						}
						if (!gotoL2) {
							if (cov == 3) {
								cd.sizeok(2); // can't finish due to forward
								// reference
								return;
							}
						}
					}
				}
			}

			if (!gotoL2) {
				if (introducing && isOverride()) {
					context.acceptProblem(Problem.newSemanticTypeErrorLoc(
							IProblem.FunctionDoesNotOverrideAny, this, new String[] {
									new String(ident.ident),
									new String(cd.ident().ident) }));
				}
			}

			// L2:	;
		} else if (isOverride() && parent.isTemplateInstance() == null) {
			errorOnModifier(IProblem.OverrideOnlyForClassMemberFunctions, TOK.TOKoverride, context);
		}

		/*
		 * Do not allow template instances to add virtual functions to a class.
		 */
		if (isVirtual(context)) {
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
				IClassDeclaration cd2 = ti.tempdecl.isClassMember();
				if (cd2 != null) {
					context.acceptProblem(Problem.newSemanticTypeErrorLoc(
							IProblem.CannotUseTemplateToAddVirtualFunctionToClass, this, new String[] { cd2.toChars(context) }));
				}
			}
		}

		if (isMain()) {
			// Check parameters to see if they are either () or (char[][] args)
			switch (nparams) {
			case 0:
				break;

			case 1: {
				Argument arg0 = Argument.getNth(f.parameters, 0, context);
				if (arg0.type.ty != Tarray
						|| arg0.type.next.ty != Tarray
						|| arg0.type.next.next.ty != Tchar
						|| ((arg0.storageClass & (STCout | STCref | STClazy)) != 0)) {
					// goto Lmainerr;
					gotoLmainerr = true;
				}
				break;
			}

			default:
				// goto Lmainerr;
				gotoLmainerr = true;
			}

			if (!gotoLmainerr) {
				if (f.nextOf().ty != Tint32 && f.nextOf().ty != Tvoid) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.MustReturnIntOrVoidFromMainFunction, type));
				}
			}
			if (f.varargs != 0 || gotoLmainerr) {
				// Lmainerr: 
				context.acceptProblem(Problem.newSemanticTypeErrorLoc(
						IProblem.IllegalMainParameters, this));
			}
		}

		if (equals(ident, Id.assign)
				&& (sd != null || cd != null)) { // Disallow
			// identity
			// assignment
			// operator.

			// opAssign(...)
			if (nparams == 0) {
				if (f.varargs != 0) {
					// goto Lassignerr;
					semantic_Lassignerr(context);
					return;
				}
			} else {
				Argument arg0 = Argument.getNth(f.parameters, 0, context);
				Type t0 = arg0.type.toBasetype(context);
				Type tb = sd != null ? sd.type() : cd.type();
				if (arg0.type.implicitConvTo(tb, context) != MATCH.MATCHnomatch
						|| (sd != null && t0.ty == Tpointer && t0.nextOf()
								.implicitConvTo(tb, context) != MATCH.MATCHnomatch)) {
					if (nparams == 1) {
						// goto Lassignerr;}
						semantic_Lassignerr(context);
						return;
					}
					Argument arg1 = Argument.getNth(f.parameters, 1, context);
					if (arg1.defaultArg != null) {
						// goto Lassignerr;
						semantic_Lassignerr(context);
						return;
					}
				}
			}
		}

		/*
		 * Save scope for possible later use (if we need the function internals)
		 */
		scope = new Scope(sc, context);
		scope.setNoFree();
		return;
	}

	private final void semantic_Lassignerr(SemanticContext context) {
		context.acceptProblem(Problem.newSemanticTypeErrorLoc(
				IProblem.IdentityAssignmentOperatorOverloadIsIllegal, this));
	}

	@Override
	public void semantic2(Scope sc, SemanticContext context) {
		// empty
	}

	@Override
	public void semantic3(Scope sc, SemanticContext context) {
		TypeFunction f;
		IAggregateDeclaration ad;
		VarDeclaration argptr = null;
		VarDeclaration _arguments = null;

		if (parent == null) {
			throw new IllegalStateException("assert(0);");
		}

		if (semanticRun != 0) {
			return;
		}
		semanticRun = 1;

		if (type == null || type.ty != Tfunction) {
			return;
		}
		f = (TypeFunction) (type);
		int nparams = Argument.dim(f.parameters, context);

		// Check the 'throws' clause
		/*
		 * throws not used right now if (fthrows) { int i;
		 * 
		 * for (i = 0; i < fthrows.dim; i++) { Type *t = (Type
		 * *)fthrows.data[i];
		 * 
		 * t = t.semantic(loc, sc); if (!t.isClassHandle()) error("can only
		 * throw classes, not %s", t.toChars()); } }
		 */

		if (fbody != null || frequire != null) {
			// Establish function scope
			ScopeDsymbol ss;
			Scope sc2;

			localsymtab = new DsymbolTable();

			ss = new ScopeDsymbol();
			ss.parent = sc.scopesym;
			sc2 = sc.push(ss);
			sc2.func = this;
			sc2.parent = this;
			sc2.callSuper = 0;
			sc2.sbreak = null;
			sc2.scontinue = null;
			sc2.sw = null;
			sc2.fes = fes;
			sc2.linkage = LINK.LINKd;
			sc2.stc &= ~(STCauto | STCscope | STCstatic | STCabstract | STCdeprecated);
			sc2.protection = PROT.PROTpublic;
			sc2.explicitProtection = 0;
			sc2.structalign = 8;
			sc2.incontract = 0;
			sc2.tf = null;
			sc2.noctor = 0;

			// Declare 'this'
			ad = isThis();
			if (ad != null) {
				VarDeclaration v;

				if (isFuncLiteralDeclaration() != null && isNested()) {
					context.acceptProblem(Problem.newSemanticTypeErrorLoc(
							IProblem.LiteralsCannotBeClassMembers, this));
					return;
				} else {
					Assert.isTrue(!isNested()); // can't be both member and
					// nested
					Assert.isNotNull(ad.handle());
					v = new ThisDeclaration(loc, ad.handle());
					v.storage_class |= STCparameter | STCin;
					v.semantic(sc2, context);
					if (sc2.insert(v) == null) {
						Assert.isTrue(false);
					}
					v.parent = this;
					vthis = v;
				}
			} else if (isNested()) {
				VarDeclaration v;

				v = new ThisDeclaration(loc, Type.tvoid.pointerTo(context));
				v.storage_class |= STCparameter | STCin;
				v.semantic(sc2, context);
				if (sc2.insert(v) == null) {
					Assert.isTrue(false);
				}
				v.parent = this;
				vthis = v;
			}

			// Declare hidden variable _arguments[] and _argptr
			if (f.varargs != 0) {
				Type t;

				if (f.linkage == LINK.LINKd) { // Declare _arguments[]
					if (context.BREAKABI) {
						v_arguments = new VarDeclaration(loc,
								context.Type_typeinfotypelist.type(),
								Id._arguments_typeinfo, null);
						v_arguments.storage_class = STCparameter | STCin;
						v_arguments.semantic(sc2, context);
						sc2.insert(v_arguments);
						v_arguments.parent = this;

						t = context.Type_typeinfo.type().arrayOf(context);
						_arguments = new VarDeclaration(loc, t, Id._arguments,
								null);
						_arguments.semantic(sc2, context);
						sc2.insert(_arguments);
						_arguments.parent = this;
					} else {
						t = context.Type_typeinfo.type().arrayOf(context);
						v_arguments = new VarDeclaration(loc, t, Id._arguments,
								null);
						v_arguments.storage_class = STCparameter | STCin;
						v_arguments.semantic(sc2, context);
						sc2.insert(v_arguments);
						v_arguments.parent = this;
					}
				}
				if (f.linkage == LINK.LINKd
						|| (parameters != null && parameters.size() > 0)) { // Declare
					// _argptr
					t = Type.tvoid.pointerTo(context);
					argptr = new VarDeclaration(loc, t, Id._argptr, null);
					argptr.semantic(sc2, context);
					sc2.insert(argptr);
					argptr.parent = this;
				}
			}

			// Propagate storage class from tuple arguments to their
			// sub-arguments.
			if (f.parameters != null) {
				for (int i = 0; i < f.parameters.size(); i++) {
					Argument arg = f.parameters.get(i);

					if (arg.type.ty == Ttuple) {
						TypeTuple t = (TypeTuple) arg.type;
						int dim = Argument.dim(t.arguments, context);
						for (int j = 0; j < dim; j++) {
							Argument narg = Argument.getNth(t.arguments, j,
									context);
							narg.storageClass = arg.storageClass;
						}
					}
				}
			}

			// Declare all the function parameters as variables
			if (nparams != 0) { // parameters[] has all the tuples removed, as
				// the back end
				// doesn't know about tuples
				parameters = new Dsymbols(nparams);
				for (int i = 0; i < nparams; i++) {
					Argument arg = Argument.getNth(f.parameters, i, context);
					IdentifierExp id = arg.ident;
					if (id == null) {
						id = new IdentifierExp(loc, ("_param_" + i + "u")
								.toCharArray());
						arg.ident = id;
					}
					VarDeclaration v = new VarDeclaration(loc, arg.type, id,
							null);
					v.storage_class |= STCparameter;
					if (f.varargs == 2 && i + 1 == nparams) {
						v.storage_class |= STCvariadic;
					}
					v.storage_class |= arg.storageClass
							& (STCin | STCout | STCref | STClazy);
					if ((v.storage_class & STClazy) != 0) {
						v.storage_class |= STCin;
					}
					v.semantic(sc2, context);
					if (sc2.insert(v) == null) {
						context.acceptProblem(Problem.newSemanticTypeError(IProblem.ParameterIsAlreadyDefined, arg.ident, new String[] { toChars(context), v.toChars(context) }));
					} else {
						parameters.add(v);
					}
					localsymtab.insert(v);
					v.parent = this;
				}
			}

			// Declare the tuple symbols and put them in the symbol table,
			// but not in parameters[].
			if (f.parameters != null) {
				for (int i = 0; i < f.parameters.size(); i++) {
					Argument arg = f.parameters.get(i);

					if (arg.ident == null) {
						continue; // never used, so ignore
					}
					if (arg.type.ty == Ttuple) {
						TypeTuple t = (TypeTuple) arg.type;
						int dim = Argument.dim(t.arguments, context);
						Objects exps = new Objects();
						exps.setDim(dim);
						for (int j = 0; j < dim; j++) {
							Argument narg = Argument.getNth(t.arguments, j,
									context);
							Assert.isNotNull(narg.ident);
							IVarDeclaration v = sc2.search(loc, narg.ident,
									null, context).isVarDeclaration();
							Assert.isNotNull(v);
							Expression e = new VarExp(loc, v);
							exps.set(j, e);
						}
						Assert.isNotNull(arg.ident);
						TupleDeclaration v = new TupleDeclaration(loc,
								arg.ident, exps);
						v.isexp = true;
						if (sc2.insert(v) == null) {
							context.acceptProblem(Problem.newSemanticTypeErrorLoc(IProblem.ParameterIsAlreadyDefined, v, new String[] { toChars(context), v.toChars(context) }));
						}
						localsymtab.insert(v);
						v.parent = this;
					}
				}
			}

			sc2.incontract++;

			if (frequire != null) {
				// BUG: need to error if accessing out parameters
				// BUG: need to treat parameters as const
				// BUG: need to disallow returns and throws
				// BUG: verify that all in and ref parameters are read
				frequire = frequire.semantic(sc2, context);
				labtab = null; // so body can't refer to labels
			}

			if (fensure != null || addPostInvariant(context)) {
				ScopeDsymbol sym;

				sym = new ScopeDsymbol();
				sym.parent = sc2.scopesym;
				sc2 = sc2.push(sym);

				Assert.isNotNull(type.nextOf());
				if (type.nextOf().ty == Tvoid) {
					if (outId != null) {
						context.acceptProblem(Problem.newSemanticTypeError(
								IProblem.VoidFunctionsHaveNoResult, outId));
					}
				} else {
					if (outId == null) {
						outId = new IdentifierExp(loc, Id.result); // provide a
						// default
					}
				}

				if (outId != null) { // Declare result variable
					VarDeclaration v;
					Loc loc = this.loc;

					if (fensure != null) {
						fensure.loc = loc;
					}

					v = new VarDeclaration(loc, type.nextOf(), outId, null);
					v.noauto = true;
					sc2.incontract--;
					v.semantic(sc2, context);
					sc2.incontract++;
					if (sc2.insert(v) == null) {
						context.acceptProblem(Problem.newSemanticTypeErrorLoc(
								IProblem.OutResultIsAlreadyDefined, this, new String[] { v.toChars(context) }));
					}
					v.parent = this;
					vresult = v;

					// vresult gets initialized with the function return value
					// in ReturnStatement::semantic()
				}

				// BUG: need to treat parameters as const
				// BUG: need to disallow returns and throws
				if (fensure != null) {
					fensure = fensure.semantic(sc2, context);
					labtab = null; // so body can't refer to labels
				}

				if (!context.global.params.useOut) {
					if (fensure != null) {
						fensure = null; // discard
					}
					if (vresult != null) {
						vresult = null;
					}
				}

				// Postcondition invariant
				if (addPostInvariant(context)) {
					Expression e = null;
					if (isCtorDeclaration() != null) {
						// Call invariant directly only if it exists
						IInvariantDeclaration inv = ad.inv();
						IClassDeclaration cd = ad.isClassDeclaration();

						while (inv == null && cd != null) {
							cd = cd.baseClass();
							if (cd == null) {
								break;
							}
							inv = cd.inv();
						}
						if (inv != null) {
							e = new DsymbolExp(loc, inv);
							e = new CallExp(loc, e);
							e = e.semantic(sc2, context);
						}
					} else { // Call invariant virtually
						ThisExp v = new ThisExp(loc);
						v.type = vthis.type;
						e = new AssertExp(loc, v);
					}
					if (e != null) {
						ExpStatement s = new ExpStatement(loc, e);
						if (fensure != null) {
							fensure = new CompoundStatement(loc, s, fensure);
						} else {
							fensure = s;
						}
					}
				}

				if (fensure != null) {
					returnLabel = new LabelDsymbol(Id.returnLabel);
					LabelStatement ls = new LabelStatement(loc,
							new IdentifierExp(loc, Id.returnLabel), fensure);
					ls.isReturnLabel = true;
					returnLabel.statement = ls;
				}
				sc2 = sc2.pop();
			}

			sc2.incontract--;

			if (fbody != null) {
				IClassDeclaration cd = isClassMember();

				if (isCtorDeclaration() != null && cd != null) {
					for (int i = 0; i < cd.fields().size(); i++) {
						IVarDeclaration v = cd.fields().get(i);
						v.ctorinit(false);
					}
				}

				if (inferRetType || f.retStyle() != RET.RETstack) {
					nrvo_can = 0;
				}

				fbody = fbody.semantic(sc2, context);

				if (inferRetType) { // If no return type inferred yet, then
					// infer a void
					if (type.nextOf() == null) {
						type.next = Type.tvoid;
						type = type.semantic(loc, sc, context);
					}
					f = (TypeFunction) type;
				}

				boolean offend = fbody != null ? fbody.fallOffEnd(context)
						: true;

				if (isStaticCtorDeclaration() != null) { /*
				 * It's a static
				 * constructor.
				 * Ensure that all
				 * ctor consts were
				 * initialized.
				 */

					IScopeDsymbol ad2 = toParent().isScopeDsymbol();
					Assert.isTrue(ad2 != null);
					for (int i = 0; i < ad2.members().size(); i++) {
						IDsymbol s = ad2.members().get(i);

						s.checkCtorConstInit(context);
					}
				}

				if (isCtorDeclaration() != null && cd != null) {

					// Verify that all the ctorinit fields got initialized
					if ((sc2.callSuper & Scope.CSXthis_ctor) == 0) {
						for (int i = 0; i < cd.fields().size(); i++) {
							IVarDeclaration v = cd.fields().get(i);

							if (!v.ctorinit() && v.isCtorinit()) {
								context.acceptProblem(Problem.newSemanticTypeErrorLoc(
										IProblem.MissingInitializerForConstField, v, new String[] { v.toChars(context) }));
							}
						}
					}

					if ((sc2.callSuper & Scope.CSXany_ctor) == 0
							&& cd.baseClass() != null
							&& cd.baseClass().ctor() != null) {
						sc2.callSuper = 0;

						// Insert implicit super() at start of fbody
						Expression e1 = new SuperExp(loc);
						Expression e = new CallExp(loc, e1);

						int errors = context.global.errors;
						context.global.gag++;
						e = e.semantic(sc2, context);
						context.global.gag--;
						if (errors != context.global.errors) {
							
							// I may be a synthetic node. In that case, mark
							// the error in the class' name
							if (this.synthetic) {
								context.acceptProblem(Problem.newSemanticTypeErrorLoc(
										IProblem.NoMatchForImplicitSuperCallInConstructor, parent));
							} else {
								context.acceptProblem(Problem.newSemanticTypeErrorLoc(
										IProblem.NoMatchForImplicitSuperCallInConstructor, this));
							}
						}

						Statement s = new ExpStatement(loc, e);
						fbody = new CompoundStatement(loc, s, fbody);
					}
				} else if (fes != null) { 
					// For foreach(){} body, append a return 0;
					Expression e = new IntegerExp(loc, 0);
					Statement s = new ReturnStatement(loc, e);
					fbody = new CompoundStatement(loc, fbody, s);
					Assert.isTrue(returnLabel == null);
				} else if (hasReturnExp == 0 && type.nextOf().ty != Tvoid) {
					context.acceptProblem(Problem.newSemanticTypeErrorLoc(
							IProblem.FunctionMustReturnAResultOfType, this, new String[] { type.nextOf()
									.toString() }));
				} else if (!inlineAsm) {
					if (type.nextOf().ty == Tvoid) {
						if (offend && isMain()) { // Add a return 0; statement
							Statement s = new ReturnStatement(loc,
									new IntegerExp(loc, 0));
							fbody = new CompoundStatement(loc, fbody, s);
						}
					} else {
						if (offend) {
							Expression e;

							if (context.global.params.warnings) {
								context.acceptProblem(Problem.newSemanticTypeWarning(
										IProblem.NoReturnAtEndOfFunction, getLineNumber(), getErrorStart(),
										getErrorLength(), new String[] { toChars(context) }));
							}

							if (context.global.params.useAssert
									&& !context.global.params.useInline) { /*
							 * Add
							 * an
							 * assert(0,
							 * msg);
							 * where
							 * the
							 * missing
							 * return
							 * should
							 * be.
							 */
								e = new AssertExp(loc, new IntegerExp(loc, 0),
										new StringExp(loc,
												missing_return_expression, missing_return_expression.length));
							} else {
								e = new HaltExp(loc);
							}
							e = new CommaExp(loc, e, type.nextOf()
									.defaultInit(context));
							e = e.semantic(sc2, context);
							Statement s = new ExpStatement(loc, e);
							fbody = new CompoundStatement(loc, fbody, s);
						}
					}
				}
			}

			{
				Statements a = new Statements();

				// Merge in initialization of 'out' parameters
				if (parameters != null) {
					for (int i = 0; i < parameters.size(); i++) {
						VarDeclaration v;

						v = (VarDeclaration) parameters.get(i);
						if ((v.storage_class & (STCout | STCin)) == STCout) {
							Assert.isNotNull(v.init);
							IExpInitializer ie = v.init.isExpInitializer();
							Assert.isNotNull(ie);
							ExpStatement es = new ExpStatement(loc, ie.exp());
							a.add(es);
						}
					}
				}

				if (argptr != null) { // Initialize _argptr to point past
					// non-variadic arg
					Expression e1;
					Expression e;
					Type t = argptr.type;
					VarDeclaration p;
					int offset;

					e1 = new VarExp(loc, argptr);
					if (parameters != null && parameters.size() > 0) {
						p = (VarDeclaration) parameters
								.get(parameters.size() - 1);
					} else {
						p = v_arguments; // last parameter is _arguments[]
					}
					offset = p.type.size(loc, context);
					offset = (offset + 3) & ~3; // assume stack aligns on 4
					e = new SymOffExp(loc, p, offset, context);
					e = new AssignExp(loc, e1, e);
					e.type = t;
					ExpStatement es = new ExpStatement(loc, e);
					a.add(es);
				}

				if (_arguments != null) {
					/*
					 * Advance to elements[] member of TypeInfo_Tuple with:
					 * _arguments = v_arguments.elements;
					 */
					Expression e = new VarExp(loc, v_arguments);
					e = new DotIdExp(loc, e,
							new IdentifierExp(loc, Id.elements));
					Expression e1 = new VarExp(loc, _arguments);
					e = new AssignExp(loc, e1, e);
					e = e.semantic(sc, context);
					ExpStatement es = new ExpStatement(loc, e);
					a.add(es);
				}

				// Merge contracts together with body into one compound
				// statement

				if (context._DH) {
					if (frequire != null && context.global.params.useIn) {
						frequire.incontract = true;
						a.add(frequire);
					}
				} else {
					if (frequire != null && context.global.params.useIn) {
						a.add(frequire);
					}
				}

				// Precondition invariant
				if (addPreInvariant(context)) {
					Expression e = null;
					if (isDtorDeclaration() != null) {
						// Call invariant directly only if it exists
						IInvariantDeclaration inv = ad.inv();
						IClassDeclaration cd = ad.isClassDeclaration();

						while (inv == null && cd != null) {
							cd = cd.baseClass();
							if (cd == null) {
								break;
							}
							inv = cd.inv();
						}
						if (inv != null) {
							e = new DsymbolExp(loc, inv);
							e = new CallExp(loc, e);
							e = e.semantic(sc2, context);
						}
					} else { // Call invariant virtually
						ThisExp v = new ThisExp(loc);
						v.type = vthis.type;
						Expression se = new StringExp(loc, null_this, null_this.length);
						se = se.semantic(sc, context);
						se.type = Type.tchar.arrayOf(context);
						e = new AssertExp(loc, v, se);
					}
					if (e != null) {
						ExpStatement s = new ExpStatement(loc, e);
						a.add(s);
					}
				}

				if (fbody != null) {
					a.add(fbody);
				}

				if (fensure != null) {
					a.add(returnLabel.statement);

					if (type.nextOf().ty != Tvoid) {
						// Create: return vresult;
						Assert.isNotNull(vresult);
						Expression e = new VarExp(loc, vresult);
						if (tintro != null) {
							e = e.implicitCastTo(sc, tintro.nextOf(), context);
							e = e.semantic(sc, context);
						}
						ReturnStatement s = new ReturnStatement(loc, e);
						a.add(s);
					}
				}

				fbody = new CompoundStatement(loc, a);
			}

			sc2.callSuper = 0;
			sc2.pop();
		}
		semanticRun = 2;
	}

	public void setFbody(Statement fbody) {
		this.fbody = fbody;
		sourceFbody = fbody;
	}

	public void setFensure(Statement fensure) {
		this.fensure = fensure;
		sourceFensure = fensure;
	}

	public void setFrequire(Statement frequire) {
		this.frequire = frequire;
		sourceFrequire = frequire;
	}

	@Override
	public IDsymbol syntaxCopy(IDsymbol s, SemanticContext context) {
		FuncDeclaration f;

		if (s != null) {
			f = (FuncDeclaration) s;
		} else {
			f = new FuncDeclaration(loc, ident, storage_class, type
					.syntaxCopy(context));
		}
		f.outId = outId;
		f.frequire = frequire != null ? frequire.syntaxCopy(context) : null;
		f.fensure = fensure != null ? fensure.syntaxCopy(context) : null;
		f.fbody = fbody != null ? fbody.syntaxCopy(context) : null;
		if (fthrows != null) {
			throw new IllegalStateException("assert(!fthrows);"); // deprecated
		}
		return f;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		type.toCBuffer(buf, ident, hgs, context);
		bodyToCBuffer(buf, hgs, context);
	}
	
	@Override
	public int getLineNumber() {
		return loc.linnum;
	}

	public String getSignature() {
		StringBuilder sb = new StringBuilder();
		sb.append("O");
		sb.append(parent.mangle((SemanticContext) null));
		sb.append(ident.length);
		sb.append(ident);
		sb.append(type.getSignature());
		return sb.toString();
	}
	
	public IDeclaration overnext() {
		return overnext;
	}
	
	public VarDeclaration vthis() {
		return vthis;
	}
	
	public Type tintro() {
		return tintro;
	}
	
	public boolean inferRetType() {
		return inferRetType;
	}
	
	public void nestedFrameRef(boolean nestedFrameRef) {
		this.nestedFrameRef = nestedFrameRef;
	}

}
