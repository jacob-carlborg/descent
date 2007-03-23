package descent.internal.compiler.parser;

import java.util.List;

import descent.core.compiler.IProblem;

public class NewDeclaration extends FuncDeclaration {
	
	public List<Argument> arguments;
	public int varargs;
	
	public NewDeclaration(List<Argument> arguments, int varargs) {
		super(new IdentifierExp(Id.classNew), STC.STCstatic, null);
		this.arguments = arguments;
		this.varargs = varargs;
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
			context.acceptProblem(Problem.newSemanticTypeError("New allocators only are for class or struct definitions", IProblem.NewAllocatorsOnlyForClassOrStruct, 0, start, "new".length()));
		}
		tret = Type.tvoid.pointerTo(context);
		type = new TypeFunction(arguments, tret, varargs, LINK.LINKd);

		type = type.semantic(sc, context);
		assert (type.ty == TY.Tfunction);

		// Check that there is at least one argument of type uint
		TypeFunction tf = (TypeFunction) type;
		if (Argument.dim(tf.parameters, context) < 1) {
			// TODO semantic point out the "new" token
			context.acceptProblem(Problem.newSemanticTypeError("At least one argument of type unit expected", IProblem.IllegalParameters, 0, start, "new".length()));
		} else {
			Argument a = Argument.getNth(tf.parameters, 0, context);
			if (!a.type.equals(Type.tuns32)) {
				context.acceptProblem(Problem.newSemanticTypeError("First argument must be of type uint", IProblem.IllegalParameters, 0, a.type.start, a.type.length));
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
	
	@Override
	public int getNodeType() {
		return NEW_DECLARATION;
	}

}
