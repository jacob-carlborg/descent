package descent.internal.core.ctfe;

import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.Condition;
import descent.internal.compiler.parser.ConditionalDeclaration;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Initializer;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.StaticIfCondition;
import descent.internal.compiler.parser.StaticIfDeclaration;
import descent.internal.compiler.parser.TemplateInstance;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.VarDeclaration;

public class CompileTimeParser extends Parser {

	public CompileTimeParser(int apiLevel, char[] source, int offset, int length,
			char[] filename, boolean recordLineSeparator) {
		super(apiLevel, source, offset, length, filename, recordLineSeparator);
	}
	
	@Override
	protected TemplateInstance newTemplateInstance(Loc loc, IdentifierExp id,
			ASTNodeEncoder encoder) {
		return new CompileTimeTemplateInstance(loc, id, encoder);
	}
	
	@Override
	protected VarDeclaration newVarDeclaration(Loc loc, Type type, IdentifierExp ident, Initializer init) {
		return new CompileTimeVarDeclaration(loc, type, ident, init);
	}
	
	@Override
	protected ConditionalDeclaration newConditionalDeclaration(Condition condition, Dsymbols a, Dsymbols aelse) {
		return new CompileTimeConditionalDeclaration(condition, a, aelse);
	}
	
	@Override
	protected StaticIfDeclaration newStaticIfDeclaration(StaticIfCondition condition, Dsymbols a, Dsymbols aelse) {
		return new CompileTimeStaticIfDeclaration(condition, a, aelse);
	}
	

}
