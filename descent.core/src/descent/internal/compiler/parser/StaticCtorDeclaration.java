package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.Signature;
import descent.internal.compiler.parser.ast.IASTVisitor;


public class StaticCtorDeclaration extends FuncDeclaration {
	
	public int thisStart; // where the "this" keyword starts

	public StaticCtorDeclaration(Loc loc) {
		super(loc, new IdentifierExp(Id.staticCtor), STC.STCstatic,
				null);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, sourceFrequire);
			TreeVisitor.acceptChildren(visitor, sourceFbody);
			TreeVisitor.acceptChildren(visitor, outId);
			TreeVisitor.acceptChildren(visitor, sourceFensure);
		}
		visitor.endVisit(this);
	}

	@Override
	public boolean addPostInvariant(SemanticContext context) {
		return false;
	}

	@Override
	public boolean addPreInvariant(SemanticContext context) {
		return false;
	}

	@Override
	public int getNodeType() {
		return STATIC_CTOR_DECLARATION;
	}

	@Override
	public boolean isStaticConstructor() {
		return true;
	}

	@Override
	public StaticCtorDeclaration isStaticCtorDeclaration() {
		return this;
	}

	@Override
	public AggregateDeclaration isThis() {
		return null;
	}

	@Override
	public boolean isVirtual(SemanticContext context) {
		return false;
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		type = new TypeFunction(null, Type.tvoid, 0, LINK.LINKd);
		
		/* If the static ctor appears within a template instantiation,
	     * it could get called multiple times by the module constructors
	     * for different modules. Thus, protect it with a gate.
	     */
	    if (inTemplateInstance() != null) {
			/*
			 * Add this prefix to the function: static int gate; if (++gate !=
			 * 1) return; Note that this is not thread safe; should not have
			 * threads during static construction.
			 */
			IdentifierExp id = context.uniqueId("__gate");
			VarDeclaration v = new VarDeclaration(Loc.ZERO, Type.tint32, id,
					null);
			v.storage_class = STC.STCstatic;
			Statements sa = new Statements();
			Statement s = new DeclarationStatement(Loc.ZERO, v);
			sa.add(s);
			Expression e = new IdentifierExp(Loc.ZERO, id);
			e = new AddAssignExp(Loc.ZERO, e, new IntegerExp(1));
			e = new EqualExp(Loc.ZERO, TOK.TOKnotequal, e, new IntegerExp(1));
			s = new IfStatement(Loc.ZERO, null, e, new ReturnStatement(
					Loc.ZERO, null), null);
			sa.add(s);
			if (fbody != null) {
				sa.add(fbody);
			}
			fbody = new CompoundStatement(Loc.ZERO, sa);
		}

		super.semantic(sc, context);

		// We're going to need ModuleInfo
		Module m = getModule();
		if (m == null) {
			m = sc.module;
		}
		if (m != null) {
			m.needmoduleinfo = true;
		}
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s, SemanticContext context) {
		if (s != null) {
			throw new IllegalStateException("assert(!s);");
		}

		StaticCtorDeclaration scd = context.newStaticCtorDeclaration(loc);
		scd.copySourceRange(this);
		return super.syntaxCopy(scd, context);
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		if (hgs.hdrgen) {
			buf.writestring("static this(){}\n");
			return;
		}
		buf.writestring("static this()");
		bodyToCBuffer(buf, hgs, context);
	}
	
	@Override
	public int getErrorStart() {
		return thisStart;
	}
	
	@Override
	public int getErrorLength() {
		return 4; // "this".length()
	}
	
	@Override
	public char getSignaturePrefix() {
		return Signature.C_SPECIAL_FUNCTION;
	}

}
