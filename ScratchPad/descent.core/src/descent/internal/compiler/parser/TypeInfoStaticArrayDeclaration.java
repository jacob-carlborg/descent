package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;


public class TypeInfoStaticArrayDeclaration extends TypeInfoDeclaration {

	public TypeInfoStaticArrayDeclaration(Type tinfo, SemanticContext context) {
		super(tinfo, 0, context);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
