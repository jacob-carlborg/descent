package descent.internal.core.ctfe;

import descent.core.IJavaProject;
import descent.core.IProblemRequestor;
import descent.internal.compiler.env.IModuleFinder;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.Condition;
import descent.internal.compiler.parser.ConditionalDeclaration;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.Global;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Initializer;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.StaticIfDeclaration;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.core.CompilerConfiguration;

public class CompileTimeSemanticContext extends SemanticContext {
	
	private final IDebugger debugger;

	public CompileTimeSemanticContext(IProblemRequestor problemRequestor,
			Module module, IJavaProject project, IModuleFinder moduleFinder,
			Global global, CompilerConfiguration config, ASTNodeEncoder encoder, IDebugger debugger) {
		super(problemRequestor, module, project, moduleFinder, global, config, encoder);
		this.debugger = debugger;
	}
	
	public void stepBegin(ASTDmdNode node, Scope sc) {
		debugger.stepBegin(node, sc);
	}
	
	public void stepEnd(ASTDmdNode node, Scope sc) {
		debugger.stepEnd(node, sc);
	}
	
	@Override
	public void startTemplateEvaluation(ASTDmdNode node) {
		super.startTemplateEvaluation(node);
		
		debugger.enterStackFrame();
	}
	
	@Override
	public void endTemplateEvaluation() {
		super.endTemplateEvaluation();
		
		debugger.exitStackFrame();
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

}
