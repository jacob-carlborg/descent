package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class UnitTestDeclaration extends FuncDeclaration {
	
	public UnitTestDeclaration(Loc loc) {
		super(loc, new IdentifierExp(Loc.ZERO, unitTestId()), STC.STCundefined, null);
	}
	
	@Override
	public int getNodeType() {
		return UNIT_TEST_DECLARATION;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, sourceFbody);
		}
		visitor.endVisit(this);
	}
	
	private static int unitTestId;
	private static char[] unitTestId() {
		return ("__unittest" + ++unitTestId).toCharArray();
	}
	
	@Override
	public UnitTestDeclaration isUnitTestDeclaration() {
		return this;
	}
	
	@Override
	public AggregateDeclaration isThis() {
		return null;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (context.global.params.useUnitTests) {
			// Type tret;

			type = new TypeFunction(null, Type.tvoid, 0, LINK.LINKd);
			super.semantic(sc, context);
		}

		// We're going to need ModuleInfo even if the unit tests are not
		// compiled in, because other modules may import this module and refer
		// to this ModuleInfo.
		Module m = getModule();
		if (m == null) {
			m = sc.module;
		}
		if (m != null) {
			m.needmoduleinfo = true;
		}
	}
	
	@Override
	public boolean addPreInvariant(SemanticContext context) {
		return false;
	}
	
	@Override
	public boolean addPostInvariant(SemanticContext context) {
		return false;
	}
	
}
