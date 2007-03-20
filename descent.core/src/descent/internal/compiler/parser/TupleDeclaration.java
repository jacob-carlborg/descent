package descent.internal.compiler.parser;

import java.util.List;

public class TupleDeclaration extends Declaration {
	
	public List<ASTNode> objects;
	public boolean isexp;					// true: expression tuple
	public TypeTuple tupletype;	// !=NULL if this is a type tuple

	public TupleDeclaration(IdentifierExp ident, List<ASTNode> objects) {
		super(ident);
		this.type = null;
		this.objects = objects;
		this.isexp = false;
		this.tupletype = null;
	}
	
	@Override
	public TupleDeclaration isTupleDeclaration() {
		return this;
	}

	@Override
	public int getNodeType() {
		return TUPLE_DECLARATION;
	}

}
