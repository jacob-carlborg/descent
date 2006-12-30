package dtool;

import descent.core.domX.CommonTreeVisitor;
import dtool.dom.base.ASTNode;


public class GenericsTest<T> extends CommonTreeVisitor  {

	public boolean visit(T elem) {
		System.out.println("generic: T");
		return true;
	}
	
	public boolean visit(String elem) {
		System.out.println("generic: String");
		return true;
	}
	
	public static void func() {
		GenericsTest<ASTNode> tree = new GenericsTest<ASTNode>();
		//tree.visit(new Object());
		System.exit(0);
	}
}
