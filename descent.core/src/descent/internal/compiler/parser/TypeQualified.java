package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

public abstract class TypeQualified extends Type {
	
	public List<IdentifierExp> idents;
	
	public TypeQualified(TY ty) {
		super(ty, null);
	}

	public void addIdent(IdentifierExp ident) {
		if (idents == null) {
			idents = new ArrayList<IdentifierExp>();
		}
		idents.add(ident);
	}

}
