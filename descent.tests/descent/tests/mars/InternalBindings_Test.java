package descent.tests.mars;

import descent.core.dom.AST;
import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.ArrayInitializer;
import descent.internal.compiler.parser.BinExp;
import descent.internal.compiler.parser.CallExp;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.CompoundStatement;
import descent.internal.compiler.parser.CondExp;
import descent.internal.compiler.parser.DeclarationExp;
import descent.internal.compiler.parser.ExpInitializer;
import descent.internal.compiler.parser.ExpStatement;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.NewExp;
import descent.internal.compiler.parser.PostExp;
import descent.internal.compiler.parser.TypeIdentifier;
import descent.internal.compiler.parser.TypedefDeclaration;
import descent.internal.compiler.parser.VarDeclaration;

public class InternalBindings_Test extends Parser_Test {
	
	public void testClassDeclarationIdentifier() {
		Module m = getModuleSemanticNoProblems("class X { }", AST.D1);
		ClassDeclaration cd = (ClassDeclaration) m.members.get(0);
		assertSame(cd, cd.ident.getBinding());
	}
	
	public void testClassDeclarationBaseClass() {
		Module m = getModuleSemanticNoProblems("class X { } class Y : X { }", AST.D1);
		ClassDeclaration x = (ClassDeclaration) m.members.get(0);
		ClassDeclaration y = (ClassDeclaration) m.members.get(1);
		assertSame(x, y.sourceBaseclasses.get(0).getBinding());
		assertSame(x, y.sourceBaseclasses.get(0).sourceType.getBinding());
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
	
	public void testArrayInitializer() {
		Module m = getModuleSemanticNoProblems("int x; int[] y = [ x ];", AST.D1);
		VarDeclaration x = (VarDeclaration) m.members.get(0);
		VarDeclaration y = (VarDeclaration) m.members.get(1);
		ArrayInitializer init = (ArrayInitializer) y.sourceInit;
		assertEquals(x, init.value.get(0).getBinding());
	}
	
	public void testArrayInitializerInFunc() {
		Module m = getModuleSemanticNoProblems("int x; void foo() { int[] y = [ x ]; }", AST.D1);
		VarDeclaration x = (VarDeclaration) m.members.get(0);
		FuncDeclaration foo = (FuncDeclaration) m.members.get(1);
		CompoundStatement cs = (CompoundStatement) foo.sourceFbody;
		VarDeclaration y = (VarDeclaration) (((DeclarationExp) ((ExpStatement) cs.sourceStatements.get(0)).exp)).declaration;
		ArrayInitializer init = (ArrayInitializer) y.sourceInit;
		assertEquals(x, init.value.get(0).getBinding());
	}
	
	public void testAddAssignExp() {
		testBinExpScalar("+=");
	}
	
	public void testAddExp() {
		testBinExpScalar("+");
	}
	
	public void testAndAndExp() {
		testBinExpScalar("&&");
	}
	
	public void testAndAssignExp() {
		testBinExpScalar("&=");
	}
	
	public void testAndExp() {
		testBinExpScalar("&");
	}
	
	public void testAssignExp() {
		testBinExpScalar("=");
	}
	
	public void testCatAssignExp() {
		testBinExpString("~=");
	}
	
	public void testCatExp() {
		testBinExpString("~");
	}
	
	public void testCmpExp() {
		testBinExpScalar("<");
	}
	
//	public void testCommaExp() {
//		testBinExpScalar(",");
//	}
	
	public void testCondExp() {
		Module m = getModuleSemanticNoProblems("int x; int y; int z; int w = x ? y : z;", AST.D1);
		VarDeclaration x = (VarDeclaration) m.members.get(0);
		VarDeclaration y = (VarDeclaration) m.members.get(1);
		VarDeclaration z = (VarDeclaration) m.members.get(2);
		VarDeclaration w = (VarDeclaration) m.members.get(3);
		CondExp tri = (CondExp) ((ExpInitializer) w.sourceInit).sourceExp;
		assertSame(x, tri.econd.getBinding());
		assertSame(y, tri.sourceE1.getBinding());
		assertSame(z, tri.sourceE2.getBinding());
	}
	
	public void testDivAssignExp() {
		testBinExpScalar("/=");
	}
	
	public void testDivExp() {
		testBinExpScalar("/");
	}
	
	public void testEqualExp() {
		testBinExpScalar("==");
	}
	
	public void testIdentityExp() {
		testBinExpScalar("is");
	}
	
	public void testMinAssignExp() {
		testBinExpScalar("-=");
	}
	
	public void testMinExp() {
		testBinExpScalar("-");
	}
	
	public void testModAssignExp() {
		testBinExpScalar("%=");
	}
	
	public void testModExp() {
		testBinExpScalar("%");
	}
	
	public void testMulAssignExp() {
		testBinExpScalar("*=");
	}
	
	public void testMulExp() {
		testBinExpScalar("*");
	}
	
	public void testOrAssignExp() {
		testBinExpScalar("|=");
	}
	
	public void testOrExp() {
		testBinExpScalar("|");
	}
	
	public void testOrOrExp() {
		testBinExpScalar("||");
	}
	
	public void testPostExp() {
		Module m = getModuleSemanticNoProblems("int x; int y = x++;", AST.D1);
		VarDeclaration x = (VarDeclaration) m.members.get(0);
		VarDeclaration y = (VarDeclaration) m.members.get(1);
		PostExp bin = (PostExp) ((ExpInitializer) y.sourceInit).sourceExp;
		assertSame(x, bin.sourceE1.getBinding());
	}
	
	public void testShlAssignExp() {
		testBinExpScalar("<<=");
	}
	
	public void testShlExp() {
		testBinExpScalar("<<");
	}
	
	public void testShrAssignExp() {
		testBinExpScalar(">>=");
	}
	
	public void testShrExp() {
		testBinExpScalar(">>");
	}
	
	public void testUshrAssignExp() {
		testBinExpScalar(">>>=");
	}
	
	public void testUshrExp() {
		testBinExpScalar(">>>");
	}
	
	public void testXorAssignExp() {
		testBinExpScalar("^=");
	}
	
	public void testXorExp() {
		testBinExpScalar("^");
	}
	
	private void testBinExpScalar(String op) {
		testBinExp("int x; int y; int z = x ", op, " y;");
	}
	
	private void testBinExpString(String op) {
		testBinExp("char[] x; char[] y; char[] z = x ", op, " y;");
	}
	
	private void testBinExp(String pre, String op, String post) {
		Module m = getModuleSemanticNoProblems(pre + op + post, AST.D1);
		VarDeclaration x = (VarDeclaration) m.members.get(0);
		VarDeclaration y = (VarDeclaration) m.members.get(1);
		VarDeclaration z = (VarDeclaration) m.members.get(2);
		BinExp bin = (BinExp) ((ExpInitializer) z.sourceInit).sourceExp;
		assertSame(x, bin.sourceE1.getBinding());
		assertSame(y, bin.sourceE2.getBinding());
	}

}
