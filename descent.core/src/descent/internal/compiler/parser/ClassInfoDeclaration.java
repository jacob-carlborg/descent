package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class ClassInfoDeclaration extends VarDeclaration {

	public IClassDeclaration cd;

	public ClassInfoDeclaration(IClassDeclaration cd, SemanticContext context) {
		this(Loc.ZERO, cd, context);
	}

	public ClassInfoDeclaration(Loc loc, IClassDeclaration cd,
			SemanticContext context) {
		super(loc, context.ClassDeclaration_classinfo.type(), cd.ident(), null);
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
	public IDsymbol syntaxCopy(IDsymbol s, SemanticContext context) {
		throw new IllegalStateException("assert(0);");
	}

}
