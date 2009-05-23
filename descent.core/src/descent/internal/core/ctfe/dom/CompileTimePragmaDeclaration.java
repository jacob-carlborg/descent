package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Expressions;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.PragmaDeclaration;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.ScopeDsymbol;
import descent.internal.compiler.parser.SemanticContext;

public class CompileTimePragmaDeclaration extends PragmaDeclaration {
	
	private Expression fMessage;

	public CompileTimePragmaDeclaration(Loc loc, IdentifierExp ident, Expressions args, Dsymbols decl) {
		super(loc, ident, args, decl);
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		try {
			((CompileTimeSemanticContext) context).stepBegin(this, sc);
			
			super.semantic(sc, context);
			
			if (fMessage != null) {
				((CompileTimeSemanticContext) context).message(fMessage.toString());
			}
		} finally {
			((CompileTimeSemanticContext) context).stepEnd(this, sc);
		}
	}
	
	@Override
	public int addMember(Scope sc, ScopeDsymbol sd, int memnum, SemanticContext context) {
		return 1;
	}
	
	@Override
	protected void message(Expression e) {
		fMessage = e;
	}

}
