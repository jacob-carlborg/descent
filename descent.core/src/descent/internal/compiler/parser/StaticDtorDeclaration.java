package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class StaticDtorDeclaration extends FuncDeclaration {

	public StaticDtorDeclaration(Loc loc) {
		super(loc, new IdentifierExp(Loc.ZERO, Id.staticDtor), STC.STCstatic,
				null);
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, sourceFbody);
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
		return STATIC_DTOR_DECLARATION;
	}

	@Override
	public boolean isStaticDestructor() {
		return true;
	}

	@Override
	public AggregateDeclaration isThis() {
		return null;
	}

	@Override
	public boolean isVirtual() {
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

		StaticDtorDeclaration sdd = new StaticDtorDeclaration(loc);
		return super.syntaxCopy(sdd);
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		if (hgs.hdrgen) {
			return;
		}
		buf.writestring("static ~this()");
		bodyToCBuffer(buf, hgs, context);
	}

}
