package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class NewDeclaration extends FuncDeclaration {
	
	public Arguments arguments;
	public int varargs;
	
	public NewDeclaration(Loc loc, Arguments arguments, int varargs) {
		super(loc, new IdentifierExp(Loc.ZERO, Id.classNew), STC.STCstatic, null);
		this.arguments = arguments;
		this.varargs = varargs;
	}
	
	@Override
	public int getNodeType() {
		return NEW_DECLARATION;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifiers);
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, ident);
			// Template args?
			TreeVisitor.acceptChildren(visitor, arguments);
			TreeVisitor.acceptChildren(visitor, sourceFrequire);
			TreeVisitor.acceptChildren(visitor, sourceFbody);
			TreeVisitor.acceptChildren(visitor, outId);
			TreeVisitor.acceptChildren(visitor, sourceFensure);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public NewDeclaration isNewDeclaration() {
		return this;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		ClassDeclaration cd;
		Type tret;

		parent = sc.parent;
		Dsymbol parent = toParent();
		cd = parent.isClassDeclaration();
		if (cd == null && parent.isStructDeclaration() == null) {
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.NewAllocatorsOnlyForClassOrStruct, 0, start, "new".length()));
		}
		tret = Type.tvoid.pointerTo(context);
		type = new TypeFunction(arguments, tret, varargs, LINK.LINKd);

		type = type.semantic(loc, sc, context);
		assert (type.ty == TY.Tfunction);

		// Check that there is at least one argument of type uint
		TypeFunction tf = (TypeFunction) type;
		if (Argument.dim(tf.parameters, context) < 1) {
			// TODO semantic point out the "new" token
			context.acceptProblem(Problem.newSemanticTypeError(IProblem.AtLeastOneArgumentOfTypeExpected, 0, start, "new".length(), new String[] { "unit" }));
		} else {
			Argument a = Argument.getNth(tf.parameters, 0, context);
			if (!a.type.equals(Type.tuns32)) {
				context.acceptProblem(Problem.newSemanticTypeError(IProblem.FirstArgumentMustBeOfType, 0, a.type.start, a.type.length, new String[] { "uint" }));
			}
		}

		super.semantic(sc, context);
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
