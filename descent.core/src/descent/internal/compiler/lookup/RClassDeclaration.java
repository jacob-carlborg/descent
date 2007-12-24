package descent.internal.compiler.lookup;

import java.util.List;

import descent.core.IType;
import descent.internal.compiler.parser.BaseClasses;
import descent.internal.compiler.parser.ClassInfoDeclaration;
import descent.internal.compiler.parser.CtorDeclaration;
import descent.internal.compiler.parser.FuncDeclarations;
import descent.internal.compiler.parser.IClassDeclaration;
import descent.internal.compiler.parser.ICtorDeclaration;
import descent.internal.compiler.parser.IDsymbol;
import descent.internal.compiler.parser.INewDeclaration;
import descent.internal.compiler.parser.IVarDeclaration;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeClass;

public class RClassDeclaration extends RAggregateDeclaration implements
		IClassDeclaration {
	
	private Type type;

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
	
	@Override
	public Type type() {
		if (type == null) {
			type = new TypeClass(this);
		}
		return type;
	}
	
	@Override
	public Type getType() {
		return type();
	}
	
	public IVarDeclaration vthis() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ClassInfoDeclaration vclassinfo() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void vclassinfo(ClassInfoDeclaration vclassinfo) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isCOMclass() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isauto() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isAbstract() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public PROT getAccess(IDsymbol smember) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int vtblOffset() {
		return 1;
	}

}
