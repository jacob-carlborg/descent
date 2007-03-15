package descent.internal.compiler.parser;

import descent.core.compiler.IProblem;

public abstract class Dsymbol extends ASTNode {
	
	public Dsymbol parent;
	public IdentifierExp ident;	
	
	public Dsymbol() {
	}
	
	public Dsymbol(IdentifierExp ident) {
		this.ident = ident;
	}
	
	public boolean isAnonymous() {
		return ident == null;
	}
	
	public boolean isAggregateDeclaration() {
		int k = kind();
		return k == CLASS_DECLARATION || 
			k == STRUCT_DECLARATION ||
			k == INTERFACE_DECLARATION ||
			k == UNION_DECLARATION;		
	}
	
	public boolean isEnumDeclaration() {
		return kind() == ENUM_DECLARATION;
	}
	
	public boolean isFuncDeclaration() {
		return false;
	}
	
	public int addMember(Scope sc, ScopeDsymbol sd, int memnum, SemanticContext context) {
		parent = sd;
		if (!isAnonymous()) // no name, so can't add it to symbol table
		{
			if (sd.symtab.insert(this) == null) // if name is already defined
			{
				Dsymbol s2;

				s2 = sd.symtab.lookup(ident);
				if (!s2.overloadInsert(this)) {
					sd.multiplyDefined(this, s2, context.problemRequestor);
				}
			}
			if (sd.isAggregateDeclaration() || sd.isEnumDeclaration()) {
				if (ident.ident == Id.__sizeof 
						|| ident.ident == Id.alignof 
						|| ident.ident == Id.mangleof) {
					context.problemRequestor.acceptProblem(Problem.newSemanticMemberError("Property " + ident.ident.string + " cannot be redefined", IProblem.PropertyCanNotBeRedefined, 0, ident.start, ident.length));
				}
			}
			return 1;
		}
		return 0;
	}
	
	private boolean overloadInsert(Dsymbol dsymbol) {
		return false;
	}

	public void semantic(Scope sc, SemanticContext context) {
		
	}
	
	public void semantic2(Scope sc, SemanticContext context) {
		
	}
	
	public void semantic3(Scope sc, SemanticContext context) {
		
	}

}
