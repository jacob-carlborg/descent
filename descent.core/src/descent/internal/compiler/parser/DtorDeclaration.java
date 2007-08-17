package descent.internal.compiler.parser;

import java.util.ArrayList;

import melnorme.miscutil.tree.TreeVisitor;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class DtorDeclaration extends FuncDeclaration {
	
	public DtorDeclaration(Loc loc) {
		super(loc, new IdentifierExp(Loc.ZERO, Id.dtor), STC.STCundefined, null);
	}
	
	
	@Override
	public int getNodeType() {
		return DTOR_DECLARATION;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, ident);
			// Template args?
			TreeVisitor.acceptChildren(visitor, sourceFrequire);
			TreeVisitor.acceptChildren(visitor, sourceFbody);
			TreeVisitor.acceptChildren(visitor, outId);
			TreeVisitor.acceptChildren(visitor, sourceFensure);
		}
		visitor.endVisit(this);
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

}
