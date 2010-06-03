package descent.internal.codeassist;

import java.util.Map;

import descent.core.IEvaluationResult;
import descent.core.IJavaProject;
import descent.core.JavaModelException;
import descent.core.WorkingCopyOwner;
import descent.core.dom.CompilationUnitResolver;
import descent.core.dom.CompilationUnitResolver.ParseResult;
import descent.internal.compiler.env.ICompilationUnit;
import descent.internal.compiler.impl.CompilerOptions;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.CallExp;
import descent.internal.compiler.parser.CompileDeclaration;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.EnumMember;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.TemplateInstance;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.compiler.parser.ast.AstVisitorAdapter;
import descent.internal.core.Evaluator;
import descent.internal.core.util.Util;

/*
 * For now, don't take the JDT approach: let's parse, visit and see where
 * if the node falls between the given ranges.
 */
public class EvaluationEngine extends AstVisitorAdapter {
	
	IJavaProject javaProject;
	WorkingCopyOwner owner;
	Map settings;
	CompilerOptions compilerOptions;
	SemanticContext context;
	Evaluator evaluator;
	
	int offset;
	int length;
	IEvaluationResult result;

	public EvaluationEngine(
			Map settings,
			IJavaProject javaProject,
			WorkingCopyOwner owner) {
		this.javaProject = javaProject;
		this.owner = owner;
		this.settings = settings;
		this.compilerOptions = new CompilerOptions(settings);
	}
	
	public IEvaluationResult evaluate(ICompilationUnit sourceUnit, int offset) {
		this.offset = offset;
		
		try {
			ParseResult parseResult = CompilationUnitResolver.resolve(Util.getApiLevel(this.compilerOptions.getMap()), sourceUnit, javaProject, settings, owner, false, true, null);
			
			context = parseResult.context;
			evaluator = new Evaluator(javaProject, context);
			
			Module module = parseResult.module;
			module.accept(this);
			return result;
		} catch (JavaModelException e) {
			Util.log(e);
			return null;
		}
	}
	
	@Override
	public boolean visit(CompileDeclaration node) {
		// If between the mixin keyword
		if (node.start <= offset && offset + length <= node.start + 5) {
			result = evaluator.evalMembers(node.decl);
			return false;
		}
		return true;
	}
	
	@Override
	public boolean visit(VarDeclaration node) {
		if (result == null) {
			if (node.isConst() && isInRange(node.ident)) { 
				result = evaluator.evalInit(node.init);
			} else if (isInRange(node.sourceInit)) {
				node.sourceInit.accept(this);
			}
			return false;
		}
		return true;
	}
	
	@Override
	public boolean visit(EnumMember node) {
		if (result == null && isInRange(node.ident)) {
			result = evaluator.evalExp(node.value);
		}
		return false;
	}
	
	@Override
	public boolean visit(CallExp node) {
		if (result == null && isInRange(node.sourceE1)) {
			Expression exp = node.optimize(ASTDmdNode.WANTvalue | ASTDmdNode.WANTinterpret, context);
			result = evaluator.evalExp(exp);
		}
		return true;
	}
	
	@Override
	public boolean visit(IdentifierExp node) {
		Expression evaluatedExp;
		Expression resolvedExp;
		TemplateInstance tinst;
		if (result == null && isInRange(node)) {
			Dsymbol resolved;
			if ((resolved = context.getResolvedSymbol(node)) != null && resolved instanceof AliasDeclaration) {
				result = evaluator.evalAlias((AliasDeclaration) resolved);
			} else if ((evaluatedExp = context.getEvaluated(node)) != null) {
				result = evaluator.evalExp(evaluatedExp);
			} else if ((resolvedExp = context.getResolvedExp(node)) != null) {
				result = evaluator.evalExp(resolvedExp);
			} else if ((tinst = context.getTemplateInstance(node)) != null && tinst.members != null) {
				result = evaluator.evalMembers(tinst.members);
			}
		}
		
		return false;
	}
	
	private boolean isInRange(IdentifierExp ident) {
		if (ident == null) {
			return false;
		}
		if (ident.ident.length == 0) {
			return false;
		}
		
		return ident.start <= offset && offset + length <= ident.start + ident.length;
	}
	
	private boolean isInRange(ASTDmdNode node) {
		if (node == null) {
			return false;
		}
		return node.start <= offset && offset + length <= node.start + node.length;
	}

}
