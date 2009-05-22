package descent.internal.core.ctfe;

import descent.core.IJavaProject;
import descent.core.IProblemRequestor;
import descent.internal.compiler.env.IModuleFinder;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.Argument;
import descent.internal.compiler.parser.Condition;
import descent.internal.compiler.parser.ConditionalDeclaration;
import descent.internal.compiler.parser.DeclarationStatement;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.ExpStatement;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.Expressions;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.Global;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.IfStatement;
import descent.internal.compiler.parser.Initializer;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.ReturnStatement;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Statement;
import descent.internal.compiler.parser.StaticIfDeclaration;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.core.CompilerConfiguration;

public class CompileTimeSemanticContext extends SemanticContext {
	
	private final IDebugger debugger;
	private int fDisabledStepping;

	public CompileTimeSemanticContext(IProblemRequestor problemRequestor,
			Module module, IJavaProject project, IModuleFinder moduleFinder,
			Global global, CompilerConfiguration config, ASTNodeEncoder encoder, IDebugger debugger) {
		super(problemRequestor, module, project, moduleFinder, global, config, encoder);
		this.debugger = debugger;
	}
	
	public void stepBegin(ASTDmdNode node, Scope sc) {
		if (fDisabledStepping > 0)
			return;
		
		debugger.stepBegin(node, sc);
	}
	
	public void stepEnd(ASTDmdNode node, Scope sc) {
		if (fDisabledStepping > 0)
			return;
		
		debugger.stepEnd(node, sc);
	}
	
	public void stepBegin(ASTDmdNode node, InterState is) {
		if (fDisabledStepping > 0)
			return;
		
		debugger.stepBegin(node, is);
	}
	
	public void stepEnd(ASTDmdNode node, InterState is) {
		if (fDisabledStepping > 0)
			return;
		
		debugger.stepEnd(node, is);
	}
	
	@Override
	public void startTemplateEvaluation(ASTDmdNode node) {
		if (fDisabledStepping > 0)
			return;
		
		super.startTemplateEvaluation(node);
		
		debugger.enterStackFrame();
	}
	
	@Override
	public void endTemplateEvaluation() {
		if (fDisabledStepping > 0)
			return;
		
		super.endTemplateEvaluation();
		
		debugger.exitStackFrame();
	}
	
	public void enterFunctionInterpret() {
		if (fDisabledStepping > 0)
			return;
		
		debugger.enterStackFrame();
	}
	
	public void exitFunctionInterpret() {
		if (fDisabledStepping > 0)
			return;
		
		debugger.exitStackFrame();
	}
	
	public void disableStepping() {
		fDisabledStepping++;
	}

	public void enableStepping() {
		fDisabledStepping--;
	}
	
	@Override
	protected VarDeclaration newVarDeclaration(Loc loc, Type type, IdentifierExp exp, Initializer init) {
		return new CompileTimeVarDeclaration(loc, type, exp, init);
	}
	
	@Override
	public ConditionalDeclaration newConditionalDeclaration(Condition condition, Dsymbols a, Dsymbols elseDecl) {
		return new CompileTimeConditionalDeclaration(condition, a, elseDecl);
	}
	
	@Override
	protected StaticIfDeclaration newStaticIfDeclaration(Condition condition, Dsymbols a, Dsymbols aelse) {
		return new CompileTimeStaticIfDeclaration(condition, a, aelse);
	}

	@Override
	public Expression newCallExp(Loc loc, Expression e, Expressions args) {
		return new CompileTimeCallExp(loc, e, args);
	}
	
	@Override
	protected FuncDeclaration newFuncDeclaration(Loc loc, IdentifierExp ident, int storage_class, Type syntaxCopy) {
		return new CompileTimeFuncDeclaration(loc, ident, storage_class, syntaxCopy);
	}
	
	@Override
	protected IfStatement newIfStatement(Loc loc, Argument a, Expression condition, Statement ifbody, Statement elsebody) {
		return new CompileTimeIfStatement(loc, a, condition, ifbody, elsebody);
	}
	
	@Override
	protected ReturnStatement newReturnStatement(Loc loc, Expression e) {
		return new CompileTimeReturnStatement(loc, e);
	}
	
	@Override
	protected DeclarationStatement newDeclarationStatement(Loc loc, Expression e) {
		return new CompileTimeDeclarationStatement(loc, e);
	}
	
	@Override
	protected ExpStatement newExpStatement(Loc loc, Expression e) {
		return new CompileTimeExpStatement(loc, e);
	}

}
