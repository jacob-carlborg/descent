package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;


public class ClassInfoDeclaration extends VarDeclaration {

	public ClassDeclaration cd;

	public ClassInfoDeclaration(ClassDeclaration cd, SemanticContext context) {
		this(null, 0, cd, context);
	}

	public ClassInfoDeclaration(char[] filename, int lineNumber, ClassDeclaration cd,
			SemanticContext context) {
		super(filename, lineNumber, context.ClassDeclaration_classinfo.type, cd.ident, null);
		this.cd = cd;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		// empty
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s, SemanticContext context) {
		throw new IllegalStateException("assert(0);");
	}

}
