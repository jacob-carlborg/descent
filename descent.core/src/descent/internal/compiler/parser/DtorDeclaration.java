package descent.internal.compiler.parser;

import java.util.ArrayList;

import descent.core.compiler.IProblem;

public class DtorDeclaration extends FuncDeclaration {
	
	public DtorDeclaration(Loc loc) {
		super(loc, new IdentifierExp(Loc.ZERO, Id.dtor), STC.STCundefined, null);
	}

	@Override
	public DtorDeclaration isDtorDeclaration() {
		return this;
	}
	
	@Override
	public boolean overloadInsert(Dsymbol s, SemanticContext context) {
		return false;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		ClassDeclaration cd;

		parent = sc.parent;
		Dsymbol parent = toParent();
		cd = parent.isClassDeclaration();
		if (cd == null) {
			// TODO semantic point out the "this" token
			context.acceptProblem(Problem.newSemanticTypeError("Destructors only are for class definitions", IProblem.DestructorsOnlyForClass, 0, start, "~this".length()));
		} else {
			if (cd.dtors == null) {
				cd.dtors = new ArrayList<FuncDeclaration>();
			}
			cd.dtors.add(this);
		}
		type = new TypeFunction(null, Type.tvoid, 0, LINK.LINKd);

		sc = sc.push();
		sc.stc &= ~STC.STCstatic; // not a static destructor
		sc.linkage = LINK.LINKd;

		super.semantic(sc, context);

		sc.pop();
	}
	
	@Override
	public boolean addPreInvariant(SemanticContext context) {
		return (vthis != null && context.global.params.useInvariants);
	}
	
	@Override
	public boolean addPostInvariant(SemanticContext context) {
		return false;
	}
	
	@Override
	public int getNodeType() {
		return DTOR_DECLARATION;
	}

}
