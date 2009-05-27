package descent.internal.core.ctfe;

import java.util.ArrayList;
import java.util.List;

import descent.core.IProblemRequestor;
import descent.core.compiler.IProblem;
import descent.core.ctfe.IDebugElementFactory;
import descent.core.ctfe.IDescentStackFrame;
import descent.core.ctfe.IDescentVariable;
import descent.core.dom.AST;
import descent.core.dom.CompilationUnitResolver.ParseResult;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.DsymbolTable;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.InterState;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.ScopeDsymbol;
import descent.internal.core.CompilationUnit;
import descent.internal.core.ctfe.dom.CompileTimeSemanticContext;

public class ExpressionEvaluator {
	
	private final ParseResult fParseResult;
	private final IDebugElementFactory fElementFactory;
	private final CompilationUnit fUnit;

	public ExpressionEvaluator(CompilationUnit unit, ParseResult parseResult, IDebugElementFactory elementFactory) {
		this.fUnit = unit;
		this.fParseResult = parseResult;
		this.fElementFactory = elementFactory;
	}
	
	public IDescentVariable evaluate(IDescentStackFrame stackFrame, String expression) {
		Scope scope = stackFrame.getScope();
		InterState is = stackFrame.getInterState();
		
		Parser parser = new Parser(AST.D1, expression.toCharArray(), 0, expression.length(), null, null, false, false, fUnit.getFullyQualifiedName().toCharArray(), fParseResult.encoder);
		parser.nextToken();
		
		Expression exp = parser.parseExpression();
		if (parser.problems != null && !parser.problems.isEmpty()) {
			return fElementFactory.newVariable(stackFrame.getNumber(), expression, parser.problems.toString());	
		} else {
			((CompileTimeSemanticContext) fParseResult.context).disableStepping();
			IProblemRequestor oldRequestor = fParseResult.context.problemRequestor;
			int oldMuteProblems = fParseResult.context.muteProblems;
			int oldGlobalErrors = fParseResult.context.global.errors; 
			
			fParseResult.context.muteProblems = 0;
			fParseResult.context.global.errors = 0;
			
			final List<IProblem> problems = new ArrayList<IProblem>();
			fParseResult.context.problemRequestor = new IProblemRequestor() {
				public void acceptProblem(IProblem problem) {
					problems.add(problem);
				}
				public void beginReporting() {
				}
				public void endReporting() {
				}
				public boolean isActive() {
					return true;
				}
			};
			
			try {
				Expression result;
				
				if (is == null) {
					scope.flags |= Scope.SCOPEstaticif;
					
					result = exp.semantic(scope, fParseResult.context);
					result = result.optimize(ASTDmdNode.WANTflags | ASTDmdNode.WANTvalue | ASTDmdNode.WANTinterpret, fParseResult.context);
				} else {
					// Need a synthetic scope with the function's local symbol table
					// and the variables being interpreted
					Scope sc = Scope.copy(scope);
					sc.scopesym = new ScopeDsymbol();
					sc.scopesym.symtab = new DsymbolTable();					
					
					for(char[] key : is.fd.localsymtab.keys()) {
						if (key == null)
							continue;
						sc.scopesym.symtab.insert(key, is.fd.localsymtab.lookup(key));
					}
					
					for(Dsymbol sym : is.vars) {
						sc.scopesym.symtab.insert(sym);
					}
					
					sc.flags |= Scope.SCOPEstaticif;
					
					result = exp.semantic(sc, fParseResult.context);
					result = result.interpret(is, fParseResult.context);
				}
				
				if (problems.size() > 0) {
					return fElementFactory.newVariable(stackFrame.getNumber(), expression, problems.toString());
				} else {
					return fElementFactory.newVariable(stackFrame.getNumber(), expression, result);
				}
			} finally {
				fParseResult.context.problemRequestor = oldRequestor;
				fParseResult.context.muteProblems = oldMuteProblems;
				fParseResult.context.global.errors = oldGlobalErrors;
				((CompileTimeSemanticContext) fParseResult.context).enableStepping();
			}
		}
	}

}
