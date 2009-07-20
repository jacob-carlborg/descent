package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;


public class ModuleInfoDeclaration extends VarDeclaration {

	public Module mod;

	public ModuleInfoDeclaration(char[] filename, int lineNumber, Module mod, SemanticContext context) {
		super(filename, lineNumber, context.Module_moduleinfo.type, mod.ident, null);
		this.mod = mod;
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
