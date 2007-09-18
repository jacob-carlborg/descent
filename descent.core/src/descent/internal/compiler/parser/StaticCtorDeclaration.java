package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class StaticCtorDeclaration extends FuncDeclaration {

	public StaticCtorDeclaration(Loc loc) {
		super(loc, new IdentifierExp(Loc.ZERO, Id.staticCtor), STC.STCstatic,
				null);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
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
	public Dsymbol syntaxCopy(Dsymbol s) {
		if (s != null) {
			throw new IllegalStateException("assert(!s);");
		}

		StaticCtorDeclaration scd = new StaticCtorDeclaration(loc);
		return super.syntaxCopy(scd);
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

}
