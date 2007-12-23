package descent.internal.compiler.lookup;

import java.util.List;

import descent.core.IType;
import descent.internal.compiler.parser.BaseClasses;
import descent.internal.compiler.parser.CtorDeclaration;
import descent.internal.compiler.parser.FuncDeclarations;
import descent.internal.compiler.parser.IClassDeclaration;
import descent.internal.compiler.parser.ICtorDeclaration;
import descent.internal.compiler.parser.SemanticContext;

public class RClassDeclaration extends RAggregateDeclaration implements
		IClassDeclaration {

	public RClassDeclaration(IType element) {
		super(element);
	}

	public IClassDeclaration baseClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public BaseClasses baseclasses() {
		// TODO Auto-generated method stub
		return null;
	}

	public ICtorDeclaration ctor() {
		// TODO Auto-generated method stub
		return null;
	}

	public void defaultCtor(CtorDeclaration defaultCtor) {
		// TODO Auto-generated method stub

	}

	public FuncDeclarations dtors() {
		// TODO Auto-generated method stub
		return null;
	}

	public void dtors(FuncDeclarations dtors) {
		// TODO Auto-generated method stub

	}

	public BaseClasses interfaces() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isBaseOf(IClassDeclaration cd, int[] poffset,
			SemanticContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isNested() {
		// TODO Auto-generated method stub
		return false;
	}

	public void isabstract(boolean isabstract) {
		// TODO Auto-generated method stub

	}

	public List vtbl() {
		// TODO Auto-generated method stub
		return null;
	}

	public List vtblFinal() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IClassDeclaration isClassDeclaration() {
		return this;
	}

}
