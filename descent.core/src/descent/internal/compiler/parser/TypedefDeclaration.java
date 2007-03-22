package descent.internal.compiler.parser;

import descent.core.compiler.IProblem;

public class TypedefDeclaration extends Declaration {
	
	public boolean last; // is this the last declaration in a multi declaration?
	public Type sourceBasetype; // copy of basetype, because it will change
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
		this.sourceBasetype = basetype;
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
	public void semantic2(Scope sc, SemanticContext context) {
	    if (sem == 2) {
			sem = 3;
			if (init != null) {
				init = init.semantic(sc, basetype, context);

				ExpInitializer ie = init.isExpInitializer();
				if (ie != null) {
					if (ie.exp.type == basetype)
						ie.exp.type = type;
				}
			}
		}
	}
	
	@Override
	public int getNodeType() {
		return TYPEDEF_DECLARATION;
	}
	
	@Override
	public String toString() {
		return "typedef " + basetype + " " + ident + ";";
	}

}
