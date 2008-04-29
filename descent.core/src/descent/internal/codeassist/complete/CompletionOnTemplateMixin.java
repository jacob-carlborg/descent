package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Identifiers;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Objects;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.TemplateMixin;
import descent.internal.compiler.parser.Type;

public class CompletionOnTemplateMixin extends TemplateMixin {
	
	public Scope scope;

	public CompletionOnTemplateMixin(Loc loc, IdentifierExp ident, Type tqual, Identifiers idents, Objects tiargs) {
		super(loc, ident, tqual, idents, tiargs);
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		super.semantic(sc, context);
		
		this.scope = ScopeCopy.copy(sc, context);
	}

}
