package descent.tests.binding;

import descent.core.dom.Argument;
import descent.core.dom.Block;
import descent.core.dom.CompilationUnit;
import descent.core.dom.DeclarationStatement;
import descent.core.dom.ForeachStatement;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.ITypeBinding;
import descent.core.dom.IVariableBinding;
import descent.core.dom.VariableDeclaration;
import descent.core.dom.VariableDeclarationFragment;

public class BindingLocalVar_Test extends AbstractBinding_Test {
	
	public void testLocalVarBinding() throws Exception {
		CompilationUnit unit = createCU("test.d", "void foo() { int x; }");
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		DeclarationStatement statement = (DeclarationStatement) func.getBody().statements().get(0);
		VariableDeclaration var = (VariableDeclaration) statement.getDeclaration();
		
		ITypeBinding typeBinding = var.resolveBinding();
		assertEquals("i", typeBinding.getKey());
		
		VariableDeclarationFragment fragment = var.fragments().get(0);
		IVariableBinding varBinding = fragment.resolveBinding();
		assertEquals("x", varBinding.getName());
		assertFalse(varBinding.isField());
		assertFalse(varBinding.isParameter());
		
		assertEquals("O4test3fooFZv#x", varBinding.getKey());
		
		assertSame(varBinding, fragment.getName().resolveBinding());
	}
	
	public void testLocalVarBinding2() throws Exception {
		CompilationUnit unit = createCU("test.d", "void foo() { { int x; } }");
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		Block block = (Block) func.getBody().statements().get(0);
		DeclarationStatement statement = (DeclarationStatement) block.statements().get(0);
		VariableDeclaration var = (VariableDeclaration) statement.getDeclaration();
		
		ITypeBinding typeBinding = var.resolveBinding();
		assertEquals("i", typeBinding.getKey());
		
		VariableDeclarationFragment fragment = var.fragments().get(0);
		IVariableBinding varBinding = fragment.resolveBinding();
		assertEquals("x", varBinding.getName());
		assertFalse(varBinding.isField());
		assertFalse(varBinding.isParameter());
		
		assertEquals("O4test3fooFZv#0#x", varBinding.getKey());
		
		assertSame(varBinding, fragment.getName().resolveBinding());
	}
	
	public void testLocalVarBinding3() throws Exception {
		CompilationUnit unit = createCU("test.d", 
			"void foo() { " +
				"{ } " +
				"{ int x; } " +
			"}");
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		Block block = (Block) func.getBody().statements().get(1);
		DeclarationStatement statement = (DeclarationStatement) block.statements().get(0);
		VariableDeclaration var = (VariableDeclaration) statement.getDeclaration();
		
		ITypeBinding typeBinding = var.resolveBinding();
		assertEquals("i", typeBinding.getKey());
		
		VariableDeclarationFragment fragment = var.fragments().get(0);
		IVariableBinding varBinding = fragment.resolveBinding();
		assertEquals("x", varBinding.getName());
		assertFalse(varBinding.isField());
		assertFalse(varBinding.isParameter());
		
		assertEquals("O4test3fooFZv#1#x", varBinding.getKey());
		
		assertSame(varBinding, fragment.getName().resolveBinding());
	}
	
	public void testLocalVarBinding4() throws Exception {
		CompilationUnit unit = createCU("test.d", 
			"void foo() { " +
				"{ } " +
				"{ " +
					"{} " +
					"{} " +
					"{ int x; } " +
				"} " +
			"}");
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		Block block1 = (Block) func.getBody().statements().get(1);
		Block block2 = (Block) block1.statements().get(2);
		DeclarationStatement statement = (DeclarationStatement) block2.statements().get(0);
		VariableDeclaration var = (VariableDeclaration) statement.getDeclaration();
		
		ITypeBinding typeBinding = var.resolveBinding();
		assertEquals("i", typeBinding.getKey());
		
		VariableDeclarationFragment fragment = var.fragments().get(0);
		IVariableBinding varBinding = fragment.resolveBinding();
		assertEquals("x", varBinding.getName());
		assertFalse(varBinding.isField());
		assertFalse(varBinding.isParameter());
		
		assertEquals("O4test3fooFZv#1#2#x", varBinding.getKey());
		
		assertSame(varBinding, fragment.getName().resolveBinding());
	}
	
	public void testLocalVarBindingInForeach() throws Exception {
		CompilationUnit unit = createCU("test.d", 
			"void foo() { " +
				"foreach(x; \"hey\") { } " +
			"}");
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		ForeachStatement statement = (ForeachStatement) func.getBody().statements().get(0);
		Argument argument = statement.arguments().get(0);

		IVariableBinding varBinding = argument.resolveBinding();;
		assertEquals("x", varBinding.getName());
		assertFalse(varBinding.isField());
		assertFalse(varBinding.isParameter());
		
		assertEquals("O4test3fooFZv#0#x", varBinding.getKey());
		
		assertSame(varBinding, argument.getName().resolveBinding());
	}
	
	public void testLocalVarBindingInForeach2() throws Exception {
		CompilationUnit unit = createCU("test.d", 
			"void foo() { " +
				"foreach(x; \"hey\") { } foreach(x; \"hey\") { }" +
			"}");
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		ForeachStatement statement = (ForeachStatement) func.getBody().statements().get(0);
		Argument argument = statement.arguments().get(0);

		IVariableBinding varBinding = argument.resolveBinding();;
		assertEquals("x", varBinding.getName());
		assertFalse(varBinding.isField());
		assertFalse(varBinding.isParameter());
		
		assertEquals("O4test3fooFZv#0#x", varBinding.getKey());
		
		assertSame(varBinding, argument.getName().resolveBinding());
		
		statement = (ForeachStatement) func.getBody().statements().get(1);
		argument = statement.arguments().get(0);

		varBinding = argument.resolveBinding();;
		assertEquals("x", varBinding.getName());
		assertFalse(varBinding.isField());
		assertFalse(varBinding.isParameter());
		
		assertEquals("O4test3fooFZv#1#x", varBinding.getKey());
		
		assertSame(varBinding, argument.getName().resolveBinding());
	}
	
	public void testLocalVarBindingInForeach3() throws Exception {
		CompilationUnit unit = createCU("test.d", 
			"void foo() { " +
				"foreach(x; \"hey\") { } foreach(x; \"hey\") { } int x;" +
			"}");
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		
		DeclarationStatement statement = (DeclarationStatement) func.getBody().statements().get(2);
		VariableDeclaration var = (VariableDeclaration) statement.getDeclaration();
		
		ITypeBinding typeBinding = var.resolveBinding();
		assertEquals("i", typeBinding.getKey());
		
		VariableDeclarationFragment fragment = var.fragments().get(0);
		IVariableBinding varBinding = fragment.resolveBinding();
		assertEquals("x", varBinding.getName());
		assertFalse(varBinding.isField());
		assertFalse(varBinding.isParameter());
		
		assertEquals("O4test3fooFZv#x", varBinding.getKey());
		
		assertSame(varBinding, fragment.getName().resolveBinding());
	}
	
	public void testParameter() throws Exception {
		CompilationUnit unit = createCU("test.d", "void foo(int x) { }");
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		Argument argument = func.arguments().get(0);
		
		IVariableBinding varBinding = argument.resolveBinding();
		assertEquals("x", varBinding.getName());
		assertEquals("i", varBinding.getType().getKey());
		assertFalse(varBinding.isField());
		assertTrue(varBinding.isParameter());
		
		assertEquals("O4test3fooFiZv#x", varBinding.getKey());
		
		assertSame(varBinding, argument.getName().resolveBinding());
	}

}
