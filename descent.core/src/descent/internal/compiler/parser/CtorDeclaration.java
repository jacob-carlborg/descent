package descent.internal.compiler.parser;

import java.util.List;

import descent.core.compiler.IProblem;

public class CtorDeclaration extends FuncDeclaration {
	
	public List<Argument> arguments;
	public int varargs;
	
	public CtorDeclaration(List<Argument> arguments, int varags) {
		super(new IdentifierExp(Id.ctor), STC.STCundefined, null);
		this.arguments = arguments;
		this.varargs = varags;
	}
	
	@Override
	public CtorDeclaration isCtorDeclaration() {
		return this;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		ClassDeclaration cd;
		Type tret;

		sc = sc.push();
		sc.stc &= ~STC.STCstatic; // not a static constructor

		parent = sc.parent;
		Dsymbol parent = toParent();
		cd = parent.isClassDeclaration();
		if (cd == null) {
			// TODO semantic point out the "this" token
			context.acceptProblem(Problem.newSemanticTypeError("Constructors only are for class definitions", IProblem.ConstructorsOnlyForClass, 0, start, "this".length()));
			tret = Type.tvoid;
		} else {
			tret = cd.type; // .referenceTo();
		}
		type = new TypeFunction(arguments, tret, varargs, LINK.LINKd);

		sc.flags |= Scope.SCOPEctor;
		type = type.semantic(sc, context);
		sc.flags &= ~Scope.SCOPEctor;

		// Append:
		// return this;
		// to the function body
		if (fbody != null) {
			Expression e;
			Statement s;

			// TODO semantic, mark as synthetic, make a test to verify it
			e = new ThisExp();
			s = new ReturnStatement(e);
			fbody = new CompoundStatement(fbody, s);
		}

		super.semantic(sc, context);

		sc.pop();
	}
	
	@Override
	public int getNodeType() {
		return CTOR_DECLARATION;
	}

}
