package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class TypeInfoArrayDeclaration extends TypeInfoDeclaration {

	public TypeInfoArrayDeclaration(Type tinfo, SemanticContext context) {
		super(tinfo, 0, context);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
