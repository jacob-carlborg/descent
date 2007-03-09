package descent.internal.core.dom;

import java.util.List;

import util.tree.TreeVisitor;

import descent.core.compiler.IProblem;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IDeclaration;
import descent.core.domX.ASTVisitor;
import descent.core.domX.AbstractElement;

public class Module extends ScopeDsymbol implements ICompilationUnit {
	
	public ModuleDeclaration md;
	public List<IProblem> problems;
	
	public ModuleDeclaration getModuleDeclaration() {
		return md;
	}
	
	@SuppressWarnings("unchecked")
	public IDeclaration[] getDeclarationDefinitions() {
		if (members == null) return AbstractElement.NO_DECLARATIONS;
		return (IDeclaration[]) members.toArray(new IDeclaration[members.size()]);
	}
	
	public IProblem[] getProblems() {
		if (problems == null) return new IProblem[0];
		return problems.toArray(new IProblem[0]);
	}
	
	public int getElementType() {
		return ElementTypes.COMPILATION_UNIT;
	}
	
	@SuppressWarnings("unchecked")
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, md);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}
	
	/*
	@Override
	public void semantic(Scope scope, IProblemCollector collector) {
		Scope sc = Scope.createGlobal(this, collector);

		// Add import of "object" if this module isn't "object"
		if (!ident.string.equals(Id.object)) {
			// TODO:
			// Import im = new Import(0, NULL, Id::object, NULL, 0);
			// members.shift(im);
		}

		// Add all symbols into module's symbol table
		symtab = new DsymbolTable();
		for (int i = 0; i < members.size(); i++) {
			Dsymbol s;

			s = (Dsymbol) members.get(i);
			s.addMember(null, sc.scopesym, 1, collector);
		}

		// Pass 1 semantic routines: do public side of the definition
		for (int i = 0; i < members.size(); i++) {
			Dsymbol s;

			s = (Dsymbol) members.get(i);
			s.semantic(sc, collector);
			// TODO: runDeferredSemantic();
		}

		sc = sc.pop();
		sc.pop();
	}
	*/

}
