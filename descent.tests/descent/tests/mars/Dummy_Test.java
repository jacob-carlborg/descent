package descent.tests.mars;

import descent.core.dom.AST;
import descent.internal.compiler.parser.Module;

public class Dummy_Test extends Parser_Test {
	
	public void testDummy() throws Exception {
		//Module mod = getModuleSemantic("class X { int y; } void foo() { X x new X(); x.y = 2; }", AST.D1);
		Module mod = getModuleSemantic("class X { int y; } void foo() { X x new X(); int z = x.y + 2; }", AST.D1);
		System.out.println(mod.toString());
	}

}
