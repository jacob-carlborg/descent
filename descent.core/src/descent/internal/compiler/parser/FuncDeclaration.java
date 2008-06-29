package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.BE.BEfallthru;
import static descent.internal.compiler.parser.BUILTIN.BUILTINcos;
import static descent.internal.compiler.parser.BUILTIN.BUILTINfabs;
import static descent.internal.compiler.parser.BUILTIN.BUILTINnot;
import static descent.internal.compiler.parser.BUILTIN.BUILTINsin;
import static descent.internal.compiler.parser.BUILTIN.BUILTINsqrt;
import static descent.internal.compiler.parser.BUILTIN.BUILTINtan;
import static descent.internal.compiler.parser.BUILTIN.BUILTINunknown;
import static descent.internal.compiler.parser.LINK.LINKc;
import static descent.internal.compiler.parser.MATCH.MATCHconst;
import static descent.internal.compiler.parser.MATCH.MATCHexact;
import static descent.internal.compiler.parser.MATCH.MATCHnomatch;
import static descent.internal.compiler.parser.PROT.PROTexport;
import static descent.internal.compiler.parser.PROT.PROTprivate;
import static descent.internal.compiler.parser.STC.STCabstract;
import static descent.internal.compiler.parser.STC.STCauto;
import static descent.internal.compiler.parser.STC.STCconst;
import static descent.internal.compiler.parser.STC.STCdeprecated;
import static descent.internal.compiler.parser.STC.STCfinal;
import static descent.internal.compiler.parser.STC.STCin;
import static descent.internal.compiler.parser.STC.STCinvariant;
import static descent.internal.compiler.parser.STC.STClazy;
import static descent.internal.compiler.parser.STC.STCnodtor;
import static descent.internal.compiler.parser.STC.STCout;
import static descent.internal.compiler.parser.STC.STCparameter;
import static descent.internal.compiler.parser.STC.STCref;
import static descent.internal.compiler.parser.STC.STCscope;
import static descent.internal.compiler.parser.STC.STCstatic;
import static descent.internal.compiler.parser.STC.STCtls;
import static descent.internal.compiler.parser.STC.STCvariadic;
import static descent.internal.compiler.parser.TOK.TOKaddress;
import static descent.internal.compiler.parser.TOK.TOKconstruct;
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

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.core.IMethod;
import descent.core.JavaModelException;
import descent.core.Signature;
import descent.core.compiler.IProblem;
import descent.internal.compiler.lookup.SemanticRest;
import descent.internal.compiler.parser.ast.IASTVisitor;
import descent.internal.core.util.Util;

public class FuncDeclaration extends Declaration {

	private final static char[] missing_return_expression = { 'm', 'i', 's',
			's', 'i', 'n', 'g', ' ', 'r', 'e', 't', 'u', 'r', 'n', ' ', 'e',
			'x', 'p', 'r', 'e', 's', 's', 'i', 'o', 'n' };
	private final static char[] null_this = { 'n', 'u', 'l', 'l', ' ', 't',
			'h', 'i', 's' };
	private static String FeZe = "FeZe"; // real function(real)

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
	public DsymbolTable localsymtab; // used to prevent symbols in different
	// scopes from having the same name
	public ForeachStatement fes; // if foreach body, this is the foreach
	public VarDeclaration vthis; // 'this' parameter (member and nested)
	public VarDeclaration v_arguments; // '_arguments' parameter
	public Dsymbols parameters; // Array of VarDeclaration's for parameters
	public DsymbolTable labtab; // statement label symbol table
	public VarDeclaration vresult; // variable corresponding to outId
	public LabelDsymbol returnLabel; // where the return goes
	public boolean inferRetType;
	public boolean naked; // !=0 if naked
	public boolean inlineAsm; // !=0 if has inline assembler
	public int inlineNest; // !=0 if nested inline
	public boolean cantInterpret;
	public boolean nestedFrameRef;
	public int hasReturnExp; // 1 if there's a return exp; statement
	// 2 if there's a throw statement
	// 4 if there's an assert(0)
	// 8 if there's inline asm

	// Support for NRVO (named return value optimization)
	public int nrvo_can; // !=0 means we can do it
	public VarDeclaration nrvo_var; // variable to replace with shidden

	BUILTIN builtin; // set if this is a known, builtin
	// function we can evaluate at compile
	// time

	int tookAddressOf; // set if someone took the address of
	// this function
	Dsymbols closureVars; // local variables in this function
	// which are referenced by nested
	// functions

	// Wether this function is actually a templated function
	public boolean templated;

	protected IMethod javaElement;
	private FuncDeclaration materialized; // in case the body is yet unknown

	public SemanticRest rest;

	public FuncDeclaration(Loc loc, IdentifierExp ident, int storage_class,
			Type type) {
		super(ident);
		this.loc = loc;
		this.storage_class = storage_class;
		this.type = type;
		this.sourceType = type;
		this.loc = loc;
		this.vtblIndex = -1;
		this.inferRetType = (type != null && type.nextOf() == null);
		this.builtin = BUILTINunknown;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, sourceType);
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, sourceFrequire);
			TreeVisitor.acceptChildren(visitor, sourceFbody);
			TreeVisitor.acceptChildren(visitor, outId);
			TreeVisitor.acceptChildren(visitor, sourceFensure);
		}
		visitor.endVisit(this);
	}

	public boolean addPostInvariant(SemanticContext context) {
		AggregateDeclaration ad = isThis();
		
		if (context.isD2()) {
			return (ad != null
					&& ad.inv != null
					&& context.global.params.useInvariants
					&& (protection == PROT.PROTpublic || protection == PROT.PROTexport) && !naked
					&& !equals(ident, Id.cpctor));
		} else {
			return (ad != null
					&& ad.inv != null
					&& context.global.params.useInvariants
					&& (protection == PROT.PROTpublic || protection == PROT.PROTexport) && !naked);
		}
	}

	public boolean addPreInvariant(SemanticContext context) {
		AggregateDeclaration ad = isThis();
		
		if (context.isD2()) {
			return (ad != null
					&& context.global.params.useInvariants
					&& (protection == PROT.PROTpublic || protection == PROT.PROTexport) && !naked
					&& !equals(ident, Id.cpctor));
		} else {
			return (ad != null
					&& context.global.params.useInvariants
					&& (protection == PROT.PROTpublic || protection == PROT.PROTexport) && !naked);
		}
	}
	
	public boolean needsClosure()
	{
	    /* Need a closure for all the closureVars[] if any of the
	     * closureVars[] are accessed by a
	     * function that escapes the scope of this function.
	     * We take the conservative approach and decide that any function that:
	     * 1) is a virtual function
	     * 2) has its address taken
	     * 3) has a parent that escapes
	     *
	     * Note that since a non-virtual function can be called by
	     * a virtual one, if that non-virtual function accesses a closure
	     * var, the closure still has to be taken. Hence, we check for isThis()
	     * instead of isVirtual(). (thanks to David Friedman)
	     */

	    for (int i = 0; i < size(closureVars); i++) {
			VarDeclaration v = (VarDeclaration) closureVars.get(i);

			for (int j = 0; j < size(v.nestedrefs); j++) {
				FuncDeclaration f = (FuncDeclaration) v.nestedrefs.get(j);

				// printf("\t\tf = %s, %d, %d\n", f.toChars(), f.isVirtual(),
				// f.tookAddressOf);
				if (f.isThis() != null || f.tookAddressOf != 0) {
					// goto Lyes; // assume f escapes this function's scope
					return true;
				}

				// Look to see if any parents of f that are below this escape
				for (Dsymbol s = f.parent; s != this; s = s.parent) {
					f = s.isFuncDeclaration();
					if (f != null
							&& (f.isThis() != null || f.tookAddressOf != 0)) {
						// goto Lyes;
						return true;
					}
				}
			}
		}
		return false;

//	Lyes:
//	    return true;
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
		if (fbody != null && (!hgs.hdrgen || hgs.tpltMember /* || canInline(true, true,
		 context) */)) {
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

	public int getLevel(Loc loc, FuncDeclaration fd, SemanticContext context) {
		int level;
		Dsymbol s;
		Dsymbol fdparent;

		fdparent = fd.toParent2();
		if (fdparent == this) {
			return -1;
		}
		s = this;
		level = 0;
		while (fd != s && fdparent != s.toParent2()) {
			FuncDeclaration thisfd = s.isFuncDeclaration();
			if (thisfd != null) {
				if (!thisfd.isNested() && null == thisfd.vthis()) {
					// goto Lerr;
					if (context.acceptsProblems()) {
						context.acceptProblem(Problem.newSemanticTypeErrorLoc(
								IProblem.CannotAccessFrameOfFunction, this,
								new String[] { fd.toChars(context) }));
					}
					return 1;
				}
			} else {
				ClassDeclaration thiscd = s.isClassDeclaration();
				if (thiscd != null) {
					if (!thiscd.isNested()) {
						// goto Lerr;
						if (context.acceptsProblems()) {
							context
									.acceptProblem(Problem
											.newSemanticTypeErrorLoc(
													IProblem.CannotAccessFrameOfFunction,
													this, new String[] { fd
															.toChars(context) }));
						}
						return 1;
					}
				} else {
					// goto Lerr;
					if (context.acceptsProblems()) {
						context.acceptProblem(Problem.newSemanticTypeErrorLoc(
								IProblem.CannotAccessFrameOfFunction, this,
								new String[] { fd.toChars(context) }));
					}
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

	public Expression interpret(InterState istate, Expressions arguments,
			SemanticContext context) {
		if (context.global.errors != 0) {
			return null;
		}

		// If no body is avaiable (this FuncDeclaration was created from
		// an IMethod) materialize the body and interpret
		if (javaElement != null) {
			if (scope == null) {
				return null;
			}
			if (materialized == null) {
				Dsymbol sym = internalMaterialize();
				sym.semantic(scope.enclosing, context);
				sym.semantic2(scope.enclosing, context);
				sym.semantic3(scope.enclosing, context);
				materialized = extractFunction(sym);
			}
			return materialized.interpret(istate, arguments, context);
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

					VarDeclaration v2;
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
				} else {
					/* 
					 * Value parameters
					 */
					Type ta = arg.type.toBasetype(context);
					if (ta.ty == Tsarray && earg.op == TOKaddress) {
						/* Static arrays are passed by a simple pointer.
						 * Skip past this to get at the actual arg.
						 */
						earg = ((AddrExp) earg).e1;
					}
					earg = earg.interpret(istate != null ? istate : istatex,
							context);

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
	
	public FuncDeclaration materialize() {
		Dsymbol sym = internalMaterialize();
		if (sym == null) {
			return null;
		}
		
		sym = extractFunction(sym);

		return (FuncDeclaration) sym;
	}
	
	private Dsymbol internalMaterialize() {
		try {
			String source = javaElement.getSource();
			// TODO api level
			Parser parser = new Parser(source.toCharArray(), 0, source
					.length(), false, false, false, false, Lexer.D1,
					null, null, false, null);
			parser.nextToken();
	
			Module module = parser.parseModuleObj();
	
			return module.members.get(0);
		} catch (JavaModelException e1) {
			Util.log(e1);
			return null;
		}
	}
	
	private FuncDeclaration extractFunction(Dsymbol sym) {
		while (sym instanceof StorageClassDeclaration
				|| sym instanceof ProtDeclaration) {
			if (sym instanceof StorageClassDeclaration) {
				sym = ((StorageClassDeclaration) sym).decl.get(0);
			} else {
				sym = ((ProtDeclaration) sym).decl.get(0);
			}
		}

		if (sym instanceof TemplateDeclaration) {
			sym = ((TemplateDeclaration) sym).members.get(0);
		}
		
		return (FuncDeclaration) sym;
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
		return equals(ident, Id.DllMain) && linkage != LINKc
				&& null == isMember();
	}

	@Override
	public boolean isExport() {
		return protection == PROTexport;
	}

	@Override
	public FuncDeclaration isFuncDeclaration() {
		consumeRest();

		return this;
	}

	@Override
	public boolean isImportedSymbol() {
		return (protection == PROTexport) && null == fbody;
	}

	public boolean isMain() {
		return ident != null && equals(ident, Id.main) && linkage != LINK.LINKc
				&& isMember() == null && !isNested();
	}

	public AggregateDeclaration isMember2() {
		AggregateDeclaration ad;

		ad = null;
		for (Dsymbol s = this; s != null; s = s.parent) {
			ad = s.isMember();
			if (ad != null) {
				break;
			}
			if (s.parent == null || s.parent.isTemplateInstance() == null) {
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
	public AggregateDeclaration isThis() {
		AggregateDeclaration ad;

		ad = null;
		if ((this.storage_class & STCstatic) == 0) {
			ad = this.isMember2();
		}
		return ad;
	}
	
	@Override
	public boolean isFinal() {
		ClassDeclaration cd;
		return isMember() != null
				&& (super.isFinal() || ((cd = toParent().isClassDeclaration()) != null && (cd.storage_class & STCfinal) != 0));
	}
	
	@Override
	public boolean isOverloadable() {
		return true;
	}

	public boolean isVirtual(SemanticContext context) {
		return this.isMember() != null
				&& !(this.isStatic() || this.protection == PROT.PROTprivate || this.protection == PROT.PROTpackage)
				&& this.toParent().isClassDeclaration() != null;
	}

	public boolean isWinMain() {
		return equals(ident, Id.WinMain) && linkage != LINKc
				&& null == isMember();
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

		if (isWinMain() || isDllMain()) {
			return ident.toChars();
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

	public FuncDeclaration overloadExactMatch(Type t, SemanticContext context) {
		FuncDeclaration f;
		Declaration d;
		Declaration next;

		for (d = this; d != null; d = next) {
			FuncAliasDeclaration fa = d.isFuncAliasDeclaration();

			if (fa != null) {
				FuncDeclaration f2 = fa.funcalias
						.overloadExactMatch(t, context);
				if (f2 != null) {
					return f2;
				}
				next = fa.overnext;
			} else {
				AliasDeclaration a = d.isAliasDeclaration();

				if (a != null) {
					Dsymbol s = a.toAlias(context);
					next = s.isDeclaration();
					if (next == a) {
						break;
					}
				} else {
					f = d.isFuncDeclaration();
					if (f == null) {
						break; // BUG: should print error message?
					}
					if (t.equals(d.type)) {
						return f;
					}
					next = f.overnext;
				}
			}
		}
		return null;
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
			a.overprevious = this;
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
		f.overprevious = this;
		return true;
	}
	
	/*************************************
	 * Determine partial specialization order of 'this' vs g.
	 * This is very similar to TemplateDeclaration::leastAsSpecialized().
	 * Returns:
	 *	match	'this' is at least as specialized as g
	 *	0	g is more specialized than 'this'
	 */
	public MATCH leastAsSpecialized(FuncDeclaration g, SemanticContext context)
	{
	    /* This works by calling g() with f()'s parameters, and
	     * if that is possible, then f() is at least as specialized
	     * as g() is.
	     */

	    TypeFunction tf = (TypeFunction) type;
	    TypeFunction tg = (TypeFunction) g.type;
	    int nfparams = Argument.dim(tf.parameters, context);
	    int ngparams = Argument.dim(tg.parameters, context);
	    MATCH match = MATCHexact;

	    /* If both functions have a 'this' pointer, and the mods are not
	     * the same and g's is not const, then this is less specialized.
	     */
	    if (needThis() && g.needThis())
	    {
		if (tf.mod != tg.mod)
		{
		    if (tg.mod == Type.MODconst)
			match = MATCHconst;
		    else
			return MATCHnomatch;
		}
	    }

	    /* Create a dummy array of arguments out of the parameters to f()
	     */
	    Expressions args = new Expressions(nfparams);
	    args.setDim(nfparams);
	    for (int u = 0; u < nfparams; u++)
	    {
		Argument p = Argument.getNth(tf.parameters, u, context);
		Expression e = p.type.defaultInit(context);
		args.set(u, e);
	    }

	    MATCH m = (MATCH) tg.callMatch(null, args, context);
	    if (m != MATCHnomatch)
	    {
	        /* A variadic template is less specialized than a
	         * non-variadic one.
	         */
	        if (tf.varargs != 0 && 0 == tg.varargs) {
	            // goto L1;	// less specialized
	        	return MATCHnomatch;
	        }

	        return m;
	    }
//	  L1:
	    return MATCHnomatch;
	}
	
	public FuncDeclaration overloadResolve(Loc loc, Expression ethis, Expressions arguments,
			SemanticContext context, ASTDmdNode caller) {
		return overloadResolve(loc, ethis, arguments, 0, context, caller);
	}

	// Modified to add the caller's start and length, to signal a better error
	public FuncDeclaration overloadResolve(Loc loc, Expression ethis, Expressions arguments,
			int flags, SemanticContext context, ASTDmdNode caller) {
		TypeFunction tf;
		Match m = new Match();
		m.last = MATCHnomatch;
		ASTDmdNode.overloadResolveX(m, this, ethis, arguments, context);

		if (m.count == 1) // exactly one match
		{
			return m.lastf;
		} else {
			OutBuffer buf = new OutBuffer();

			if (arguments != null) {
				HdrGenState hgs = new HdrGenState();

				ASTDmdNode.argExpTypesToCBuffer(buf, arguments, hgs, context);
			}

			if (m.last == MATCHnomatch) {
				if (context.isD2()) {
					if ((flags & 1) != 0) {// if do not print error messages
						return null; // no match
					}
				}
				
				tf = (TypeFunction) this.type;

				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.ParametersDoesNotMatchParameterTypes,
							caller, new String[] {
									this.kindForError(context)
											+ Argument.argsTypesToChars(
													tf.parameters, tf.varargs,
													context), buf.toChars() }));
				}
				return m.anyf; // as long as it's not a FuncAliasDeclaration
			} else {
				TypeFunction t1 = (TypeFunction) m.lastf.type;
				TypeFunction t2 = (TypeFunction) m.nextf.type;

				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.CalledWithArgumentTypesMatchesBoth,
							caller, new String[] {
									buf.toChars(),
									m.lastf.toPrettyChars(context),
									Argument.argsTypesToChars(t1.parameters,
											t1.varargs, context),
									m.nextf.toPrettyChars(context),
									Argument.argsTypesToChars(t2.parameters,
											t2.varargs, context) }));
				}
				return m.lastf;
			}
		}
	}

	public boolean overrides(FuncDeclaration fd, SemanticContext context) {
		boolean result = false;

		if (ASTDmdNode.equals(fd.ident, this.ident)) {
			int cov = this.type.covariant(fd.type, context);
			if (cov != 0) {
				ClassDeclaration cd1 = this.toParent().isClassDeclaration();
				ClassDeclaration cd2 = fd.toParent().isClassDeclaration();

				if (cd1 != null && cd2 != null
						&& cd2.isBaseOf(cd1, null, context)) {
					result = true;
				}
			}
		}
		return result;
	}

	public LabelDsymbol searchLabel(IdentifierExp ident) {
		Dsymbol s;

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
		if (rest != null && !rest.isConsumed()) {
			if (rest.getScope() == null) {
				rest.setSemanticContext(sc, context);
			}
			return;
		}

		boolean gotoL1 = false;
		boolean gotoL2 = false;
		boolean gotoLmainerr = false;

		TypeFunction f = null;
		StructDeclaration sd = null;
		ClassDeclaration cd = null;
		InterfaceDeclaration id = null;
		int nparams = 0;

		if (!context.isD2()) {
			if (type.nextOf() != null) {
				type = type.semantic(loc, sc, context);
			}
	
			if (type.ty != Tfunction) {
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeErrorLoc(
							IProblem.SymbolMustBeAFunction, this,
							new String[] { toChars(context) }));
				}
				return;
			}
			f = (TypeFunction) (type);
			nparams = Argument.dim(f.parameters, context);
	
			linkage = sc.linkage;
			// if (!parent)
			{
				// parent = sc.scopesym;
				parent = sc.parent;
			}
			protection = sc.protection;
		}
		
		storage_class |= sc.stc;
		
		if (context.isD2()) {
			if (null == originalType)
				originalType = type;
			if (null == type.deco && type.nextOf() != null) {
				/*
				 * Apply const and invariant storage class to the function type
				 */
				type = type.semantic(loc, sc, context);
				if ((storage_class & STCinvariant) != 0) { // Don't use
															// toInvariant(), as
															// that will do a
															// merge()
					type = type.makeInvariant(0, 0);
					type.deco = type.merge(context).deco;
				} else if ((storage_class & STCconst) != 0) {
					if (!type.isInvariant()) { // Don't use toConst(), as that
												// will do a merge()
						type = type.makeConst(0, 0);
						type.deco = type.merge(context).deco;
					}
				}
			}
			if (type.ty != Tfunction) {
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeError(IProblem.SymbolMustBeAFunction, this, new String[] { toChars(context) }));
				}
				return;
			}
			f = (TypeFunction) (type);

			nparams = Argument.dim(f.parameters, context);

			linkage = sc.linkage;
			// if (!parent)
			{
				// parent = sc.scopesym;
				parent = sc.parent;
			}
			protection = sc.protection;
		}

		Dsymbol parent = toParent();

		if (context.isD2()) {
			if (isAuto() || isScope()) {
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeErrorLoc(
							IProblem.FunctionsCannotBeScopeOrAuto, this));
				}
			}
		} else {
			if (isConst() || isAuto() || isScope()) {
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeErrorLoc(
							IProblem.FunctionsCannotBeConstOrAuto, this));
				}
			}
		}

		if (isAbstract() && !isVirtual(context)) {
			if (context.acceptsProblems()) {
				context.acceptProblem(Problem.newSemanticTypeErrorLoc(
						IProblem.NonVirtualFunctionsCannotBeAbstract, this));
			}
		}
		
		if (context.isD2()) {
			if ((f.isConst() || f.isInvariant()) && null == isThis()) {
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeErrorLoc(
							IProblem.WithoutThisCannotBeConstInvariant, this));
				}
			}
		}

		if (isAbstract() && isFinal()) {
			if (context.acceptsProblems()) {
				context.acceptProblem(Problem.newSemanticTypeErrorLoc(
						IProblem.CannotBeBothAbstractAndFinal, this));
			}
		}

		sd = parent.isStructDeclaration();
		if (sd != null) {
			boolean condition;
			if (context.isD2()) {
				condition = isCtorDeclaration() != null;
			} else {
				condition = isCtorDeclaration() != null 
				  || isDtorDeclaration() != null;
			}
			
			// Verify no constructors, destructors, etc.
			if (condition) {
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeErrorLoc(
							IProblem.SpecialMemberFunctionsNotAllowedForSymbol,
							this, new String[] { sd.kind() }));
				}
			}
		}

		id = parent.isInterfaceDeclaration();
		if (id != null) {
			storage_class |= STCabstract;
			
			boolean condition;
			if (context.isD2()) {
				condition = isCtorDeclaration() != null
					|| isPostBlitDeclaration() != null
					|| isDtorDeclaration() != null
					|| isInvariantDeclaration() != null
					|| isUnitTestDeclaration() != null
					|| isNewDeclaration() != null || isDelete();
			} else {
				condition = isCtorDeclaration() != null 
					|| isDtorDeclaration() != null
					|| isInvariantDeclaration() != null
					|| isUnitTestDeclaration() != null
					|| isNewDeclaration() != null || isDelete();
			}

			if (condition) {
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeErrorLoc(
							IProblem.SpecialFunctionsNotAllowedInInterface,
							this, new String[] { id.toChars(context) }));
				}
			}
			if (fbody != null) {
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeErrorLoc(
							IProblem.FunctionBodyIsNotAbstractInInterface,
							this, new String[] { id.toChars(context) }));
				}
			}
		}

		cd = parent.isClassDeclaration();
		if (cd != null) {
			// Descent: lazy initialization
			cd.consumeRest();

			int vi;

			if (isCtorDeclaration() != null) {
				// ctor = (CtorDeclaration *)this;
				// if (!cd.ctor)
				// cd.ctor = ctor;
				return;
			}

			if ((storage_class & STCabstract) != 0) {
				cd.isabstract = true;
			}

			// if static function, do not put in vtbl[]
			if (!isVirtual(context)) {
				// goto Ldone

				/* Save scope for possible later use (if we need the
				 * function internals)
				 */
				scope = new Scope(sc, context);
				scope.setNoFree();
				return;
			}

			// Find index of existing function in vtbl[] to override
			if (cd.baseClass != null) {
				for (vi = 0; vi < cd.baseClass.vtbl.size() && !gotoL1; vi++) {
					// Descent workarround
					// TODO fix this
					if (vi >= cd.vtbl.size()) {
						break;
					}

					FuncDeclaration fdv = ((Dsymbol) cd.vtbl.get(vi))
							.isFuncDeclaration();

					// BUG: should give error if argument types match,
					// but return type does not?

					if (fdv != null && equals(fdv.ident, ident)) {
						int cov = type.covariant(fdv.type, context);

						if (cov == 2) {
							if (context.acceptsProblems()) {
								context
										.acceptProblem(Problem
												.newSemanticTypeErrorLoc(
														IProblem.FunctionOfTypeOverridesButIsNotCovariant,
														this,
														new String[] {
																toChars(context),
																type
																		.toChars(context),
																fdv
																		.toPrettyChars(context),
																fdv.type
																		.toChars(context) }));
							}
						}
						if (cov == 1) {
							if (fdv.isFinal()) {
								if (context.acceptsProblems()) {
									context
											.acceptProblem(Problem
													.newSemanticTypeErrorLoc(
															IProblem.CannotOverrideFinalFunction,
															this,
															new String[] { fdv
																	.toPrettyChars(context) }));
								}
							}
							if (fdv.toParent() == parent) {
								// If both are mixins, then error.
								// If either is not, the one that is not
								// overrides
								// the other.
								if (fdv.parent.isClassDeclaration() != null) {
									// goto L1;
									gotoL1 = true;
								}

								if (!gotoL1) {
									if (context.BREAKABI) {
										if (this.parent.isClassDeclaration() == null) {
											if (context.acceptsProblems()) {
												context
														.acceptProblem(Problem
																.newSemanticTypeErrorLoc(
																		IProblem.MultipleOverridesOfSameFunction,
																		this));
											}
										}
									} else {
										if (this.parent.isClassDeclaration() == null
												&& isDtorDeclaration() == null) {
											if (context.acceptsProblems()) {
												context
														.acceptProblem(Problem
																.newSemanticTypeErrorLoc(
																		IProblem.MultipleOverridesOfSameFunction,
																		this));
											}
										}
									}
									if (context.acceptsProblems()) {
										context
												.acceptProblem(Problem
														.newSemanticTypeErrorLoc(
																IProblem.MultipleOverridesOfSameFunction,
																this));
									}
								}
							}

							if (!gotoL1) {
								cd.vtbl.set(vi, this);
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
								} else if (!type.equals(fdv.type)) {
									/*
									 * Only need to have a tintro if the vptr
									 * offsets differ
									 */
									int[] offset = { 0 };
									if (fdv.type.nextOf().isBaseOf(
											type.nextOf(), offset, context)) {
										tintro = fdv.type;
									}
								}
							}

							// goto L1;
							gotoL1 = true;
						}

						if (!gotoL1) {
							if (cov == 3) {
								cd.sizeok = 2; // can't finish due to forward
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
				if (cd.baseClass != null) {
					Dsymbol s = cd.baseClass.search(loc, ident, 0, context);
					if (s != null) {
						FuncDeclaration f2;
						
						// TODO added for Descent, see if it's correct
						FuncAliasDeclaration fad = s.isFuncAliasDeclaration();
						if (fad != null) {
							f2 = fad.funcalias;
						} else {
							f2 = s.isFuncDeclaration();
						}
						if (f2 == null) {
							System.out.println(123456);
						}
						f2 = f2.overloadExactMatch(type, context);
						if (f2 != null && f2.isFinal()
								&& f2.prot() != PROTprivate) {
							if (context.acceptsProblems()) {
								context
										.acceptProblem(Problem
												.newSemanticTypeErrorLoc(
														IProblem.CannotOverrideFinalFunctions,
														this,
														new String[] {
																new String(
																		ident.ident),
																new String(
																		cd.ident.ident) }));
							}
						}
					}
				}
				if (isFinal()) {
					cd.vtblFinal.add(this);
				} else {
					// Append to end of vtbl[]
					introducing = true;
					vi = cd.vtbl.size();
					cd.vtbl.add(this);
					vtblIndex = vi;
				}
			}

			// L1: ;

			/*
			 * Go through all the interface bases. If this function is covariant
			 * with any members of those interface functions, set the tintro.
			 */
			for (int i = 0; i < cd.interfaces.size() && !gotoL2; i++) {
				BaseClass b = cd.interfaces.get(i);
				for (vi = 0; vi < b.base.vtbl.size() && !gotoL2; vi++) {
					Dsymbol s = (Dsymbol) b.base.vtbl.get(vi);
					FuncDeclaration fdv = s.isFuncDeclaration();
					if (fdv != null && equals(fdv.ident, ident)) {
						int cov = type.covariant(fdv.type, context);
						if (cov == 2) {
							if (context.acceptsProblems()) {
								context
										.acceptProblem(Problem
												.newSemanticTypeErrorLoc(
														IProblem.FunctionOfTypeOverridesButIsNotCovariant,
														this,
														new String[] {
																toChars(context),
																type
																		.toChars(context),
																fdv
																		.toPrettyChars(context),
																fdv.type
																		.toChars(context) }));
							}
						}
						if (cov == 1) {
							Type ti = null;

							if (fdv.tintro() != null) {
								ti = fdv.tintro();
							} else if (!type.equals(fdv.type)) {
								/*
								 * Only need to have a tintro if the vptr
								 * offsets differ
								 */
								int[] offset = { 0 };
								if (fdv.type.nextOf().isBaseOf(type.nextOf(),
										offset, context)) {
									ti = fdv.type;

								}
							}
							if (ti != null) {
								if (tintro != null && !tintro.equals(ti)) {
									if (context.acceptsProblems()) {
										context
												.acceptProblem(Problem
														.newSemanticTypeErrorLoc(
																IProblem.IncompatibleCovariantTypes,
																this,
																new String[] {
																		tintro
																				.toChars(context),
																		ti
																				.toChars(context) }));
									}
								}
								tintro = ti;
							}
							// goto L2;
							gotoL2 = true;
						}
						if (!gotoL2) {
							if (cov == 3) {
								cd.sizeok = 2; // can't finish due to forward
								// reference
								return;
							}
						}
					}
				}
			}

			if (!gotoL2) {
				if (introducing && isOverride()) {
					if (context.acceptsProblems()) {
						context.acceptProblem(Problem.newSemanticTypeErrorLoc(
								IProblem.FunctionDoesNotOverrideAny, this,
								new String[] { new String(ident.ident),
										new String(cd.ident.ident) }));
					}
				}
			}

			// L2:	;
		} else if (isOverride() && parent.isTemplateInstance() == null) {
			errorOnModifier(IProblem.OverrideOnlyForClassMemberFunctions,
					TOK.TOKoverride, context);
		}

		/*
		 * Do not allow template instances to add virtual functions to a class.
		 */
		if (isVirtual(context)) {
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
				ClassDeclaration cd2 = ti.tempdecl.isClassMember();
				if (cd2 != null) {
					if (context.acceptsProblems()) {
						context
								.acceptProblem(Problem
										.newSemanticTypeErrorLoc(
												IProblem.CannotUseTemplateToAddVirtualFunctionToClass,
												this, new String[] { cd2
														.toChars(context) }));
					}
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
						|| arg0.type.nextOf().ty != Tarray
						|| arg0.type.nextOf().nextOf().ty != Tchar
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
					if (context.acceptsProblems()) {
						context.acceptProblem(Problem.newSemanticTypeError(
								IProblem.MustReturnIntOrVoidFromMainFunction,
								type));
					}
				}
			}
			if (f.varargs != 0 || gotoLmainerr) {
				// Lmainerr: 
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeErrorLoc(
							IProblem.IllegalMainParameters, this));
				}
			}
		}

		if (equals(ident, Id.assign) && (sd != null || cd != null)) { // Disallow
			// identity
			// assignment
			// operator.

			// opAssign(...)
			if (nparams == 0) {
				if (f.varargs != 0) {
					// goto Lassignerr;
					semantic_Lassignerr(sc, sd, context);
					return;
				}
			} else {
				Argument arg0 = Argument.getNth(f.parameters, 0, context);
				Type t0 = arg0.type.toBasetype(context);
				Type tb = sd != null ? sd.type : cd.type;
				if (arg0.type.implicitConvTo(tb, context) != MATCH.MATCHnomatch
						|| (sd != null && t0.ty == Tpointer && t0.nextOf()
								.implicitConvTo(tb, context) != MATCH.MATCHnomatch)) {
					if (nparams == 1) {
						// goto Lassignerr;}
						semantic_Lassignerr(sc, sd, context);
						return;
					}
					Argument arg1 = Argument.getNth(f.parameters, 1, context);
					if (arg1.defaultArg != null) {
						// goto Lassignerr;
						semantic_Lassignerr(sc, sd, context);
						return;
					}
				}
			}
		}

		// Ldone:
		/*
		 * Save scope for possible later use (if we need the function internals)
		 */
		scope = new Scope(sc, context);
		scope.setNoFree();
		return;
	}

	private final void semantic_Lassignerr(Scope sc, StructDeclaration sd, SemanticContext context) {
		if (context.isD2()) {
			if (sd != null) {
				sd.hasIdentityAssign = 1;	// don't need to generate it
				// goto Ldone;
				/*
				 * Save scope for possible later use (if we need the function internals)
				 */
				scope = new Scope(sc, context);
				scope.setNoFree();
				return;
		    }
		}
		
		if (context.acceptsProblems()) {
			context
					.acceptProblem(Problem
							.newSemanticTypeErrorLoc(
									IProblem.IdentityAssignmentOperatorOverloadIsIllegal,
									this));
		}
	}

	@Override
	public void semantic2(Scope sc, SemanticContext context) {
		// empty
	}

	@Override
	public void semantic3(Scope sc, SemanticContext context) {
		if (rest != null && !rest.isConsumed()) {
			if (rest.getScope() == null) {
				rest.setSemanticContext(sc, context);
			}
			return;
		}

		TypeFunction f;
		AggregateDeclaration ad;
		VarDeclaration argptr = null;
		VarDeclaration _arguments = null;

		if (parent == null) {
			if (context.global.errors != 0) {
				return;
			}
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
		
		int nparams = 0;
		if (!context.isD2()) {
			nparams = Argument.dim(f.parameters, context);
		} 

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

		// Descent: changed to always get semantic done on arguments
		//		if (fbody != null || frequire != null) {
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
		if (context.isD2()) {
			sc2.stc &= ~(STCauto | STCscope | STCstatic | STCabstract | STCdeprecated | STCconst | STCfinal | STCinvariant | STCtls);
		} else {
			sc2.stc &= ~(STCauto | STCscope | STCstatic | STCabstract
					| STCdeprecated | STCfinal);
		}
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
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeErrorLoc(
							IProblem.LiteralsCannotBeClassMembers, this));
				}
				return;
			} else {
				Assert.isTrue(!isNested()); // can't be both member and
				// nested
				Assert.isNotNull(ad.handle);
				
				if (context.isD2()) {
					Type thandle = ad.handle;
					if ((storage_class & STCconst) != 0 || type.isConst())
					{
					    if (thandle.ty == Tclass)
						thandle = thandle.constOf(context);
					    else
					    {	assert(thandle.ty == Tpointer);
						thandle = thandle.nextOf().constOf(context).pointerTo(context);
					    }
					}
					else if ((storage_class & STCinvariant) != 0 || type.isInvariant())
					{
					    if (thandle.ty == Tclass)
						thandle = thandle.invariantOf(context);
					    else
					    {	assert(thandle.ty == Tpointer);
						thandle = thandle.nextOf().invariantOf(context).pointerTo(context);
					    }
					}
					v = new ThisDeclaration(loc, thandle);
					v.storage_class |= STCparameter;
				} else {
					v = new ThisDeclaration(loc, ad.handle);
					v.storage_class |= STCparameter | STCin;
				}
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
			if (context.isD2()) {
				v.storage_class |= STCparameter;
			} else {
				v.storage_class |= STCparameter | STCin;
			}
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
							context.Type_typeinfotypelist.type,
							Id._arguments_typeinfo, null);
					if (context.isD2()) {
						v_arguments.storage_class = STCparameter;
					} else {
						v_arguments.storage_class = STCparameter | STCin;
					}
					v_arguments.semantic(sc2, context);
					sc2.insert(v_arguments);
					v_arguments.parent = this;

					t = context.Type_typeinfo.type.arrayOf(context);
					_arguments = new VarDeclaration(loc, t, Id._arguments, null);
					_arguments.semantic(sc2, context);
					sc2.insert(_arguments);
					_arguments.parent = this;
				} else {
					t = context.Type_typeinfo.type.arrayOf(context);
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
						Argument narg = Argument
								.getNth(t.arguments, j, context);
						narg.storageClass = arg.storageClass;
					}
				}
			}
		}

		// Declare all the function parameters as variables
		if (context.isD2()) {
			nparams = Argument.dim(f.parameters, context);
		}
		
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
				VarDeclaration v = new VarDeclaration(loc, arg.type, id, null);
				v.copySourceRange(arg);

				// Descent: for binding resolution
				arg.var = v;
				if (type instanceof TypeFunction) {
					TypeFunction tf = (TypeFunction) type;
					if (tf.parameters != null && i < tf.parameters.size()) {
						tf.parameters.get(i).var = v;
					}
				}
				if (sourceType instanceof TypeFunction) {
					TypeFunction tf = (TypeFunction) sourceType;
					if (tf.parameters != null && i < tf.parameters.size()) {
						tf.parameters.get(i).var = v;
					}
				}

				v.storage_class |= STCparameter;
				if (f.varargs == 2 && i + 1 == nparams) {
					v.storage_class |= STCvariadic;
				}
				
				if (context.isD2()) {
					v.storage_class |= arg.storageClass & (STCin | STCout | STCref | STClazy | STCfinal | STCconst | STCinvariant | STCnodtor);
				} else {
					v.storage_class |= arg.storageClass
							& (STCin | STCout | STCref | STClazy);
					if ((v.storage_class & STClazy) != 0) {
						v.storage_class |= STCin;
					}
				}
				v.semantic(sc2, context);
				if (sc2.insert(v) == null) {
					if (context.acceptsProblems()) {
						context.acceptProblem(Problem.newSemanticTypeError(
								IProblem.ParameterIsAlreadyDefined, arg.ident,
								new String[] { toChars(context),
										v.toChars(context) }));
					}
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
						Argument narg = Argument
								.getNth(t.arguments, j, context);
						Assert.isNotNull(narg.ident);
						VarDeclaration v = sc2.search(loc, narg.ident, null,
								context).isVarDeclaration();
						Assert.isNotNull(v);
						Expression e = new VarExp(context.isD2() ? v.loc : Loc.ZERO, v);
						exps.set(j, e);
					}
					Assert.isNotNull(arg.ident);
					TupleDeclaration v = new TupleDeclaration(context.isD2() ? loc : Loc.ZERO, arg.ident,
							exps);
					v.isexp = true;
					if (sc2.insert(v) == null) {
						if (context.acceptsProblems()) {
							context.acceptProblem(Problem
									.newSemanticTypeErrorLoc(
											IProblem.ParameterIsAlreadyDefined,
											v, new String[] { toChars(context),
													v.toChars(context) }));
						}
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
					if (context.acceptsProblems()) {
						context.acceptProblem(Problem.newSemanticTypeError(
								IProblem.VoidFunctionsHaveNoResult, outId));
					}
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
					if (context.acceptsProblems()) {
						context.acceptProblem(Problem.newSemanticTypeErrorLoc(
								IProblem.OutResultIsAlreadyDefined, this,
								new String[] { v.toChars(context) }));
					}
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
					InvariantDeclaration inv = ad.inv;
					ClassDeclaration cd = ad.isClassDeclaration();

					while (inv == null && cd != null) {
						cd = cd.baseClass;
						if (cd == null) {
							break;
						}
						inv = cd.inv;
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
				LabelStatement ls = new LabelStatement(loc, new IdentifierExp(
						loc, Id.returnLabel), fensure);
				ls.isReturnLabel = true;
				returnLabel.statement = ls;
			}
			sc2 = sc2.pop();
		}

		sc2.incontract--;

		if (fbody != null) {
			ClassDeclaration cd = isClassMember();

			if (isCtorDeclaration() != null && cd != null) {
				for (int i = 0; i < cd.fields.size(); i++) {
					VarDeclaration v = cd.fields.get(i);
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

			boolean offend = fbody != null ? fbody.fallOffEnd(context) : true;

			if (isStaticCtorDeclaration() != null) {
				/*
				 * It's a static constructor.
				 * Ensure that all ctor consts were initialized.
				 */

				Dsymbol p = toParent();
				ScopeDsymbol ad2 = p.isScopeDsymbol();
				if (null == ad2) {
					if (context.acceptsProblems()) {
						context
								.acceptProblem(Problem
										.newSemanticTypeErrorLoc(
												IProblem.StaticConstructorCanOnlyBePartOfStructClassModule,
												this));
					}
				} else {
					for (int i = 0; i < ad2.members.size(); i++) {
						Dsymbol s = ad2.members.get(i);

						s.checkCtorConstInit(context);
					}
				}
			}

			if (isCtorDeclaration() != null && cd != null) {

				// Verify that all the ctorinit fields got initialized
				if ((sc2.callSuper & Scope.CSXthis_ctor) == 0) {
					for (int i = 0; i < cd.fields.size(); i++) {
						VarDeclaration v = cd.fields.get(i);

						if (!v.ctorinit() && v.isCtorinit()) {
							if (context.acceptsProblems()) {
								context
										.acceptProblem(Problem
												.newSemanticTypeErrorLoc(
														context.isD2() ? 
																IProblem.MissingInitializerForFinalField : 
																IProblem.MissingInitializerForConstField,
														v,
														new String[] { v
																.toChars(context) }));
							}
						}
					}
				}

				if ((sc2.callSuper & Scope.CSXany_ctor) == 0
						&& cd.baseClass != null && cd.baseClass.ctor != null) {
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
							if (context.acceptsProblems()) {
								context
										.acceptProblem(Problem
												.newSemanticTypeErrorLoc(
														IProblem.NoMatchForImplicitSuperCallInConstructor,
														parent));
							}
						} else {
							if (context.acceptsProblems()) {
								context
										.acceptProblem(Problem
												.newSemanticTypeErrorLoc(
														IProblem.NoMatchForImplicitSuperCallInConstructor,
														this));
							}
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
				if (context.acceptsProblems()) {
					context.acceptProblem(Problem.newSemanticTypeErrorLoc(
							IProblem.FunctionMustReturnAResultOfType, this,
							new String[] { type.nextOf().toString() }));
				}
			} else if (!inlineAsm) {
				offend = fbody != null ? (fbody.blockExit(context) & BEfallthru) != 0 : true;
				
				if (type.nextOf().ty == Tvoid) {
					if (offend && isMain()) { // Add a return 0; statement
						Statement s = new ReturnStatement(loc, new IntegerExp(
								loc, 0));
						fbody = new CompoundStatement(loc, fbody, s);
					}
				} else {
					if (offend) {
						Expression e;

						if (context.global.params.warnings) {
							if (context.acceptsProblems()) {
								context
										.acceptProblem(Problem
												.newSemanticTypeWarning(
														IProblem.NoReturnAtEndOfFunction,
														getLineNumber(),
														getErrorStart(),
														getErrorLength(),
														new String[] { toChars(context) }));
							}
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
											missing_return_expression,
											missing_return_expression.length));
						} else {
							e = new HaltExp(loc);
						}
						e = new CommaExp(loc, e, type.nextOf().defaultInit(
								context));
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
						ExpInitializer ie = v.init.isExpInitializer();
						Assert.isNotNull(ie);
						ExpStatement es = new ExpStatement(loc, ie.exp);
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
					p = (VarDeclaration) parameters.get(parameters.size() - 1);
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
				e = new DotIdExp(loc, e, new IdentifierExp(loc, Id.elements));
				Expression e1 = new VarExp(loc, _arguments);
				e = new AssignExp(loc, e1, e);
				if (context.isD2()) {
					e.op = TOKconstruct;
				}
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
					InvariantDeclaration inv = ad.inv;
					ClassDeclaration cd = ad.isClassDeclaration();

					while (inv == null && cd != null) {
						cd = cd.baseClass;
						if (cd == null) {
							break;
						}
						inv = cd.inv;
					}
					if (inv != null) {
						e = new DsymbolExp(loc, inv);
						e = new CallExp(loc, e);
						e = e.semantic(sc2, context);
					}
				} else { // Call invariant virtually
					ThisExp v = new ThisExp(loc);
					v.type = vthis.type;
					Expression se = new StringExp(loc, null_this,
							null_this.length);
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
			
			if (context.isD2()) {
				/* Append destructor calls for parameters as finally blocks.
			     */
			    if (parameters != null)
			    {	for (int i = 0; i < size(parameters); i++)
				{
				    VarDeclaration v = (VarDeclaration) parameters.get(i);

				    if ((v.storage_class & (STCref | STCout)) != 0) {
				    	continue;
				    }

				    /* Don't do this for static arrays, since static
				     * arrays are called by reference. Remove this
				     * when we change them to call by value.
				     */
				    if (v.type.toBasetype(context).ty == Tsarray) {
				    	continue;
				    }

				    Expression e = v.callAutoDtor(sc);
				    if (e != null)
				    {	Statement s = new ExpStatement(Loc.ZERO, e);
					s = s.semantic(sc, context);
					if (fbody.blockExit(context) == BEfallthru)
					    fbody = new CompoundStatement(Loc.ZERO, fbody, s);
					else
					    fbody = new TryFinallyStatement(Loc.ZERO, fbody, s);
				    }
				}
			    }
			}
		}

		sc2.callSuper = 0;
		sc2.pop();
		//		}
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
	public Dsymbol syntaxCopy(Dsymbol s, SemanticContext context) {
		consumeRestStructure();

		FuncDeclaration f;

		if (s != null) {
			f = (FuncDeclaration) s;
		} else {
			f = new FuncDeclaration(loc, ident, storage_class, type
					.syntaxCopy(context));
		}

		// Descent
		f.templated = templated;

		f.outId = outId;
		f.frequire = frequire != null ? frequire.syntaxCopy(context) : null;
		f.sourceFrequire = f.frequire;
		
		f.fensure = fensure != null ? fensure.syntaxCopy(context) : null;
		f.sourceFensure = f.fensure;
		
		f.fbody = fbody != null ? fbody.syntaxCopy(context) : null;
		f.sourceFbody = f.fbody;
		
		if (fthrows != null) {
			throw new IllegalStateException("assert(!fthrows);"); // deprecated
		}
		f.copySourceRange(this);
		f.javaElement = javaElement;
		f.templated = templated;
		return f;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		type.toCBuffer(buf, ident, hgs, context);
		bodyToCBuffer(buf, hgs, context);
	}

	public BUILTIN isBuiltin() {
		if (builtin == BUILTINunknown) {
			builtin = BUILTINnot;
			if (parent != null && parent.isModule() != null) {
				if (equals(parent.ident, Id.math) && parent.parent != null
						&& equals(parent.parent.ident, Id.std)
						&& null == parent.parent.parent) {
					if (FeZe.equals(type.deco)) {
						if (equals(ident, Id.sin)) {
							builtin = BUILTINsin;
						} else if (equals(ident, Id.cos)) {
							builtin = BUILTINcos;
						} else if (equals(ident, Id.tan)) {
							builtin = BUILTINtan;
						} else if (equals(ident, Id._sqrt)) {
							builtin = BUILTINsqrt;
						} else if (equals(ident, Id.fabs)) {
							builtin = BUILTINfabs;
						}
					}
				}
			}
		}
		return builtin;
	}
	
	/********************************************
	 * If there are no overloads of function f, return that function,
	 * otherwise return NULL.
	 */
	public final static OverloadApply_fp fpunique = new OverloadApply_fp() {
		public int call(Object param, FuncDeclaration f, SemanticContext context) {
			FuncDeclaration[] pf = (FuncDeclaration[]) param;
			if (pf[0] != null) {
				pf[0] = null;
				return 1; // ambiguous, done
			} else {
				pf[0] = f;
				return 0;
			}
		};
	};

	public FuncDeclaration isUnique(SemanticContext context)
	{   FuncDeclaration result = null;

	    overloadApply(this, fpunique, result, context);
	    return result;
	}

	@Override
	public int getLineNumber() {
		return loc.linnum;
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

	@Override
	public char getSignaturePrefix() {
		if (templated) {
			return Signature.C_TEMPLATED_FUNCTION;
		} else {
			return Signature.C_FUNCTION;
		}
	}

	public String getFunctionSignature() {
		StringBuilder sb = new StringBuilder();
		SemanticMixin.appendNameSignature(this, sb);
		return sb.toString();
	}

	@Override
	public boolean templated() {
		return templated;
	}

	public void setJavaElement(IMethod javaElement) {
		this.javaElement = javaElement;
	}

	@Override
	public IMethod getJavaElement() {
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

}
