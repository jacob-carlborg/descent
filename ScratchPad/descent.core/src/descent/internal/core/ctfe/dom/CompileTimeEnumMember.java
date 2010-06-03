package descent.internal.core.ctfe.dom;

import descent.internal.compiler.parser.EnumMember;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Type;

public class CompileTimeEnumMember extends EnumMember {

	public CompileTimeEnumMember(char[] filename, int lineNumber, IdentifierExp id, Expression value, Type type) {
		super(filename, lineNumber, id, value, type);
	}

	public CompileTimeEnumMember(char[] filename, int lineNumber, IdentifierExp id, Expression value) {
		super(filename, lineNumber, id, value);
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		try {
			((CompileTimeSemanticContext) context).stepBegin(this, sc);
			
			super.semantic(sc, context);
		} finally {
			((CompileTimeSemanticContext) context).stepEnd(this, sc);
		}
	}

}
