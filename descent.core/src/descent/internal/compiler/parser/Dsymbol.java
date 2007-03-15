package descent.internal.compiler.parser;

import descent.core.IProblemRequestor;
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
	
	public boolean addMember(Scope sc, ScopeDsymbol sd, int memnum, IProblemRequestor requestor) {
		parent = sd;
		if (!isAnonymous()) // no name, so can't add it to symbol table
		{
			if (sd.symtab.insert(this) == null) // if name is already defined
			{
				Dsymbol s2;

				s2 = sd.symtab.lookup(ident);
				if (!s2.overloadInsert(this)) {
					sd.multiplyDefined(this, s2, requestor);
				}
			}
			if (sd.isAggregateDeclaration() || sd.isEnumDeclaration()) {
				if (ident.ident == Id.__sizeof 
						|| ident.ident == Id.alignof 
						|| ident.ident == Id.mangleof) {
					requestor.acceptProblem(Problem.newSemanticMemberError("Property " + ident.ident.string + " cannot be redefined", IProblem.PropertyCanNotBeRedefined, 0, ident.start, ident.length));
				}
			}
			return true;
		}
		return false;
	}
	
	private boolean overloadInsert(Dsymbol dsymbol) {
		return false;
	}

	public void semantic(Scope scope, IProblemRequestor problemRequestor) {
		
	}

}
