package descent.internal.compiler.parser;

import descent.core.compiler.IProblem;

public class TypedefDeclaration extends Declaration {
	
	public boolean last; // is this the last declaration in a multi declaration?
	public Type originalBasetype; // copy of basetype, because it will change
	public Type basetype;
	public Initializer init;
	int sem;			// 0: semantic() has not been run
						// 1: semantic() is in progress
						// 2: semantic() has been run
						// 3: semantic2() has been run
	
	public TypedefDeclaration(IdentifierExp id, Type basetype, Initializer init) {
		super(id);
		this.type = new TypeTypedef(this);
		this.basetype = basetype;
		this.originalBasetype = basetype;
		this.init = init;				
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (sem == 0) {
			sem = 1;
			basetype = basetype.semantic(sc, context);
			sem = 2;
			type = type.semantic(sc, context);
			if (sc.parent.isFuncDeclaration() != null && init != null) {
			    semantic2(sc, context);
			}
		} else if (sem == 1) {
			context.acceptProblem(Problem.newSemanticTypeError("Circular definition", IProblem.CircularDefinition, 0, ident.start, ident.length));
	    }
	}
	
	@Override
	public int kind() {
		return TYPEDEF_DECLARATION;
	}
	
	@Override
	public String toString() {
		return "typedef " + basetype + " " + ident + ";";
	}

}
