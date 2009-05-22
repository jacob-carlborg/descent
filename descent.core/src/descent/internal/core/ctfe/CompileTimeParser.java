package descent.internal.core.ctfe;

import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.Condition;
import descent.internal.compiler.parser.ConditionalDeclaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.ExpStatement;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Expressions;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Initializer;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.Statement;
import descent.internal.compiler.parser.StaticIfCondition;
import descent.internal.compiler.parser.StaticIfDeclaration;
import descent.internal.compiler.parser.TemplateInstance;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeFunction;
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
	
	@Override
	protected Expression newCallExp(Loc loc, Expression e, Expressions expressions) {
		return new CompileTimeCallExp(loc, e, expressions);
	}
	
	@Override
	protected FuncDeclaration newFuncDeclaration(Loc loc, IdentifierExp ident, int storage_class, TypeFunction typeFunction) {
		return new CompileTimeFuncDeclaration(loc, ident, storage_class, typeFunction);
	}
	
	@Override
	protected Statement newIfStatement(Loc loc, Argument arg, Expression condition, Statement ifbody, Statement elsebody) {
		return new CompileTimeIfStatement(loc, arg, condition, ifbody, elsebody);
	}
	
	@Override
	protected Statement newReturnStatement(Loc loc, Expression exp) {
		return new CompileTimeReturnStatement(loc, exp);
	}
	
	@Override
	protected Statement newDeclarationStatement(Loc loc, Dsymbol d) {
		return new CompileTimeDeclarationStatement(loc, d);
	}
	
	@Override
	protected ExpStatement newExpStatement(Loc loc, Expression exp) {
		return new CompileTimeExpStatement(loc, exp);
	}

}
