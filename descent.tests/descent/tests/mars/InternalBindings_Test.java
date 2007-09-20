package descent.tests.mars;

import com.sun.org.apache.bcel.internal.generic.Type;

import descent.core.dom.AST;
import descent.internal.compiler.parser.AddExp;
import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.CallExp;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.CompoundStatement;
import descent.internal.compiler.parser.DeclarationExp;
import descent.internal.compiler.parser.ExpInitializer;
import descent.internal.compiler.parser.ExpStatement;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.NewExp;
import descent.internal.compiler.parser.TypeIdentifier;
import descent.internal.compiler.parser.TypedefDeclaration;
import descent.internal.compiler.parser.VarDeclaration;

public class InternalBindings_Test extends Parser_Test {
	
	public void testClassDeclarationIdentifier() {
		Module m = getModuleSemanticNoProblems("class X { }", AST.D1);
		ClassDeclaration cd = (ClassDeclaration) m.members.get(0);
		assertSame(cd, cd.ident.getBinding());
	}
	
	public void testAlias() {
		Module m = getModuleSemanticNoProblems("class X { } alias X x;", AST.D1);
		ClassDeclaration cd = (ClassDeclaration) m.members.get(0);
		AliasDeclaration alias = (AliasDeclaration) m.members.get(1);
		assertSame(cd, alias.getBinding());
		assertSame(cd, alias.sourceType.getBinding());
		assertSame(alias, alias.ident.getBinding());
	}
	
	public void testAliasWithNesting() {
		Module m = getModuleSemanticNoProblems("class X { class Y { void Z() { } } } alias X.Y.Z x;", AST.D1);
		ClassDeclaration x = (ClassDeclaration) m.members.get(0);
		ClassDeclaration y = (ClassDeclaration) x.members.get(0);
		FuncDeclaration z = (FuncDeclaration) y.members.get(0);
		AliasDeclaration alias = (AliasDeclaration) m.members.get(1);
		assertSame(z, alias.getBinding());
		assertSame(z, alias.sourceType.getBinding());
		
		TypeIdentifier type = (TypeIdentifier) alias.sourceType;
		assertSame(x, type.ident.getBinding());
		assertSame(y, type.idents.get(0).getBinding());
		assertSame(z, type.idents.get(1).getBinding());
		
		assertSame(alias, alias.ident.getBinding());
	}
	
	public void testTypedef() {
		Module m = getModuleSemanticNoProblems("class X { } typedef X x;", AST.D1);
		ClassDeclaration cd = (ClassDeclaration) m.members.get(0);
		TypedefDeclaration typedef = (TypedefDeclaration) m.members.get(1);
		assertSame(cd, typedef.getBinding());
		assertSame(cd, typedef.sourceBasetype.getBinding());
		assertSame(typedef, typedef.ident.getBinding());
	}
	
	public void testTypedefWithNestingDoesntWork() {
		getModuleSemantic("class X { class Y { void Z() { } }} typedef X.Y.Z x;", AST.D1);
	}
	
	public void testVarDeclaration() {
		Module m = getModuleSemanticNoProblems("class X { } X x;", AST.D1);
		ClassDeclaration cd = (ClassDeclaration) m.members.get(0);
		VarDeclaration var = (VarDeclaration) m.members.get(1);
		assertSame(cd, var.getBinding());
		assertSame(cd, var.sourceType.getBinding());
		assertSame(var, var.ident.getBinding());
	}
	
	public void testNewExp() {
		Module m = getModuleSemanticNoProblems("class X { } void foo() { X x = new X(); }", AST.D1);
		ClassDeclaration cd = (ClassDeclaration) m.members.get(0);
		FuncDeclaration f = (FuncDeclaration) m.members.get(1);
		CompoundStatement cs = (CompoundStatement) f.sourceFbody;
		ExpStatement es = (ExpStatement) cs.sourceStatements.get(0);
		VarDeclaration var = (VarDeclaration) ((DeclarationExp) es.exp).declaration;
		assertSame(cd, var.getBinding());
		assertSame(var, var.ident.getBinding());
		ExpInitializer ei = (ExpInitializer) var.sourceInit;
		NewExp ne = (NewExp) ei.sourceExp;
		assertSame(cd, ne.getBinding());
		assertSame(cd, ne.sourceNewtype.getBinding());
	}
	
	public void testCallExp() {
		Module m = getModuleSemanticNoProblems("void bar() { } void foo() { bar(); }", AST.D1);
		FuncDeclaration bar = (FuncDeclaration) m.members.get(0);
		FuncDeclaration foo = (FuncDeclaration) m.members.get(1);
		CompoundStatement cs = (CompoundStatement) foo.sourceFbody;
		ExpStatement es = (ExpStatement) cs.sourceStatements.get(0);
		CallExp call = (CallExp) es.sourceExp;
		assertSame(bar, call.getBinding());
	}
	
	public void testCallExpWithScalarArguments() {
		Module m = getModuleSemanticNoProblems("void bar(int x) { } void foo() { bar(1); }", AST.D1);
		FuncDeclaration bar = (FuncDeclaration) m.members.get(0);
		FuncDeclaration foo = (FuncDeclaration) m.members.get(1);
		CompoundStatement cs = (CompoundStatement) foo.sourceFbody;
		ExpStatement es = (ExpStatement) cs.sourceStatements.get(0);
		CallExp call = (CallExp) es.sourceExp;
		assertSame(bar, call.getBinding());
	}
	
	public void testCallExpWithClassArguments() {
		Module m = getModuleSemanticNoProblems("class X { } void bar(X x) { } void foo() { bar(new X); }", AST.D1);
		ClassDeclaration x = (ClassDeclaration) m.members.get(0);
		FuncDeclaration bar = (FuncDeclaration) m.members.get(1);
		FuncDeclaration foo = (FuncDeclaration) m.members.get(2);
		CompoundStatement cs = (CompoundStatement) foo.sourceFbody;
		ExpStatement es = (ExpStatement) cs.sourceStatements.get(0);
		CallExp call = (CallExp) es.sourceExp;
		assertSame(bar, call.getBinding());
		assertSame(x, call.arguments.get(0).getBinding());
	}
	
	public void testAssignExp() {
		Module m = getModuleSemanticNoProblems("int x; int y = x;", AST.D1);
		VarDeclaration x = (VarDeclaration) m.members.get(0);
		VarDeclaration y = (VarDeclaration) m.members.get(1);
		assertSame(x, y.sourceInit.getBinding());
		IdentifierExp exp = (IdentifierExp) ((ExpInitializer) y.sourceInit).sourceExp;
		assertSame(x, exp.getBinding());
	}
	
	public void testAddExp() {
		Module m = getModuleSemanticNoProblems("int x; int y = x + 2;", AST.D1);
		VarDeclaration x = (VarDeclaration) m.members.get(0);
		VarDeclaration y = (VarDeclaration) m.members.get(1);
		AddExp add = (AddExp) ((ExpInitializer) y.sourceInit).sourceExp;
		assertSame(x, add.sourceE1.getBinding());
	}

}
