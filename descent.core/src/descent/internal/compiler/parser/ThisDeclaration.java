package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;


public class ThisDeclaration extends VarDeclaration {

	public ThisDeclaration(Loc loc, Type type) {
		super(loc, type, Id.This, null);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public Dsymbol syntaxCopy(Dsymbol s, SemanticContext context) {
		throw new IllegalStateException("assert(0);");
	}

}
