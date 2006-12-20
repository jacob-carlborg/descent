package descent.internal.core.dom;

import java.util.List;

import descent.core.compiler.IProblem;
import descent.core.dom.ASTVisitor;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IDeclaration;
import descent.core.dom.IModuleDeclaration;

public class Module extends ASTNode implements ICompilationUnit {
	
	public ModuleDeclaration md;
	public List<IProblem> problems;
	public List<Declaration> members;
	public Identifier ident;
	
	public IModuleDeclaration getModuleDeclaration() {
		return md;
	}
	
	@SuppressWarnings("unchecked")
	public IDeclaration[] getDeclarationDefinitions() {
		if (members == null) return ASTNode.NO_DECLARATIONS;
		return (IDeclaration[]) members.toArray(new IDeclaration[members.size()]);
	}
	
	public IProblem[] getProblems() {
		if (problems == null) return new IProblem[0];
		return problems.toArray(new IProblem[0]);
	}
	
	public int getNodeType0() {
		return COMPILATION_UNIT;
	}
	
	@SuppressWarnings("unchecked")
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, md);
			acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}

}
