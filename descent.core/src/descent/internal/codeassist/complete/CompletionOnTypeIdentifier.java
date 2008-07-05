package descent.internal.codeassist.complete;

import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeIdentifier;

public class CompletionOnTypeIdentifier extends TypeIdentifier {
	
	public Scope scope;
	
	/*
	 * If it's type.|
	 * then dot is the cursor position. Else, it's -1.
	 */
	public int dot;

	public CompletionOnTypeIdentifier(Loc loc, IdentifierExp ident) {
		super(loc, ident);
		this.dot = -1;
	}
	
	public CompletionOnTypeIdentifier(Loc loc, IdentifierExp ident, int dot) {
		super(loc, ident);
		this.dot = dot;
	}
	
	@Override
	public Type semantic(Loc loc, Scope sc, SemanticContext context) {
		Type type = super.semantic(loc, sc, context);
		
		this.scope = ScopeCopy.copy(sc, context);
		
		return type;
	}
	
	@Override
	public void resolve(Loc loc, Scope sc, Expression[] pe, Type[] pt, Dsymbol[] ps, SemanticContext context) {
		super.resolve(loc, sc, pe, pt, ps, context);
		
		this.scope = ScopeCopy.copy(sc, context);
	}
	
	@Override
	public void addIdent(IdentifierExp ident) {
		
	}

}
