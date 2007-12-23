package descent.internal.compiler.parser;

import java.util.List;

public interface IClassDeclaration extends IAggregateDeclaration {
	
	boolean isBaseOf(IClassDeclaration cd, int[] poffset, SemanticContext context);
	
	IClassDeclaration baseClass();
	
	BaseClasses baseclasses();
	
	BaseClasses interfaces();
	
	boolean isNested();
	
	void isabstract(boolean isabstract);
	
	List vtbl();
	
	List vtblFinal();
	
	ICtorDeclaration ctor();
	
	void defaultCtor(CtorDeclaration defaultCtor);
	
	FuncDeclarations dtors();
	
	void dtors(FuncDeclarations dtors);

}
