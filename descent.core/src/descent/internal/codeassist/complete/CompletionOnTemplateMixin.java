package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Identifiers;
import descent.internal.compiler.parser.Objects;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.TemplateMixin;
import descent.internal.compiler.parser.Type;

public class CompletionOnTemplateMixin extends TemplateMixin {
	
	public Scope scope;

	public CompletionOnTemplateMixin(char[] filename, int lineNumber, IdentifierExp ident, Type tqual, Identifiers idents, Objects tiargs, ASTNodeEncoder encoder) {
		super(filename, lineNumber, ident, tqual, idents, tiargs, encoder);
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		super.semantic(sc, context);
		
		this.scope = ScopeCopy.copy(sc, context);
	}

}
