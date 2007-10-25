package descent.tests.mars;

import java.util.ArrayList;
import java.util.List;

import descent.core.compiler.IProblem;
import descent.core.dom.AST;

public class Semantic1_Test extends Parser_Test {

	public void testDuplicatedVar1() {
		String s = "int a; int a;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.SymbolConflictsWithSymbolAtLocation, 11, 1);
	}

	public void testDuplicatedVar2() {
		String s = "int a, a;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.SymbolConflictsWithSymbolAtLocation, 7, 1);
	}

	public void testDuplicatedSymbol() {
		String s = "int a; class a { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.SymbolConflictsWithSymbolAtLocation, 13, 1);
	}

	public void testDuplicatedSymbolWithProt() {
		String s = "int a; public class a { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.SymbolConflictsWithSymbolAtLocation, 20, 1);
	}

	public void testDuplicatedSymbolWithStorageClass() {
		String s = "int a; static class a { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.SymbolConflictsWithSymbolAtLocation, 20, 1);
	}

	public void testDuplicatedSymbolWithLink() {
		String s = "int a; extern { int a; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.SymbolConflictsWithSymbolAtLocation, 20, 1);
	}

	public void testPropertiesCannotBeRedefined() {
		String s = "class C { int sizeof; int alignof; int mangleof; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(3, p.length);

		assertError(p[0], IProblem.PropertyCanNotBeRedefined, 14, 6);
		assertError(p[1], IProblem.PropertyCanNotBeRedefined, 26, 7);
		assertError(p[2], IProblem.PropertyCanNotBeRedefined, 39, 8);
	}

	public void testTypedefCircularDefinition() {
		String s = "typedef one two; typedef two one;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.CircularDefinition, 29, 3);
	}

	public void testEnumBaseTypeMustBeOfIntegralType() {
		String s = "enum x : double { a }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.EnumBaseTypeMustBeOfIntegralType, 9, 6);
	}
	
	public void testEnumBaseTypeMustBeOfIntegralType2() {
		String s = "class p { } enum x : p { a }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.EnumBaseTypeMustBeOfIntegralType, 21, 1);
	}

	public void testEnumBaseTypeMustBeOfIntegralType_OK() {
		assertNoSemanticErrors("enum x : int { a }");
	}

	public void testEnumMustHaveAtLeastOneMember() {
		String s = "enum x { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.EnumMustHaveAtLeastOneMember, 5, 1);
	}

	public void testEnumMustHaveAtLeastOneMember_Not() {
		assertNoSemanticErrors("enum x;");
	}

	public void testEnumAnonMustHaveAtLeastOneMember2() {
		String s = " enum { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.EnumMustHaveAtLeastOneMember, 1, 4);
	}

	public void testEnumMustHaveAtLeastOneMember_OK() {
		assertNoSemanticErrors("enum x { a }");
	}

	public void testEnumOverflowWithBool() {
		String s = "enum x : bool { a, b, c }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.EnumValueOverflow, 22, 1);
	}

	public void testEnumOverflowWithBool2() {
		String s = "enum x : bool { a = 1, b }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.EnumValueOverflow, 23, 1);
	}
	
	public void testEnumOverflowWithBool_NOT() {
		assertNoSemanticErrors("enum x : bool { a = 0, b }");
	}

	public void testAliasCannotBeConst() {
		String s = "alias const int x;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.AliasCannotBeConst, 6, 5);
	}

	public void testAliasCircularDeclaration() {
		String s = "alias one two; alias two one;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.CircularDefinition, 25, 3);
	}

	public void testAliasCircularDeclaration2() {
		String s = "alias one two; alias two three; alias three one;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.CircularDefinition, 44, 3);
	}

	public void testAliasCircularDeclaration_Not() {
		assertNoSemanticErrors("int one = 2; alias one two; alias two three;");
	}

	public void testAliasForwardReference() {
		String s = "alias x y; int x = 2;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);

		assertError(p[0], IProblem.ForwardReferenceOfSymbol, 6, 1);
		assertError(p[1], IProblem.ForwardReferenceOfSymbol, 15, 1);
	}

	public void testAliasForwardReference_Not() {
		assertNoSemanticErrors("int x = 2; alias x y;");
	}

	public void testTypedefUsedAsAType() {
		String s = "typedef x y; int x = 2;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.UsedAsAType, 8, 1);
	}

	public void testExternSymbolsCannotHaveInitializers() {
		String s = "extern int x = 1;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.ExternSymbolsCannotHaveInitializers, 15, 1);
	}

	public void testVoidsHaveNoValue() {
		String s = " void x;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.VoidsHaveNoValue, 1, 4);
	}

	// Dmd reports three in this case, we are trying
	// to make it cleaner to the user
	public void testUsedAsAType() {
		String s = " class X  { } Y y;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(3, p.length);

		assertError(p[0], IProblem.UndefinedIdentifier, 14, 1);
		assertError(p[1], IProblem.UsedAsAType, 14, 1);
		assertError(p[2], IProblem.VoidsHaveNoValue, 14, 1);
	}

	public void testUsedAsAType_Not() {
		assertNoSemanticErrors(" class X { } X x;");
	}

	public void testCannotInferTypeFromArrayInitializer() {
		String s = " auto x = [];";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.CannotInferTypeFromThisArrayInitializer, 10, 2);
	}

	public void testCannotInferTypeFromArrayInitializer2() {
		String s = " auto x = [ 1 : 1 ];";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.CannotInferTypeFromThisArrayInitializer, 10, 9);
	}

	public void testVariableCannotBeSynchronized() {
		String s = " synchronized int x;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.ModifierCannotBeAppliedToVariables, 18, 1);
	}

	public void testVariableCannotBeOverride() {
		String s = " override int x;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.ModifierCannotBeAppliedToVariables, 14, 1);
	}

	public void testVariableCannotBeAbstract() {
		String s = " abstract int x;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.ModifierCannotBeAppliedToVariables, 14, 1);
	}

	public void testNoDefinitionOfStruct() {
		String s = " struct s; s x;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.NoDefinition, 11, 1);
	}

	public void testDuplicatedInterfaceInheritance() {
		String s = " interface A { } class B : A, A { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.DuplicatedInterfaceInheritance, 30, 1);
	}

	public void testCircularInterfaceInheritance() {
		String s = " class A : A { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.CircularDefinition, 11, 1);
	}

	public void testBaseTypeWrong() {
		String s = " int x; class A : x { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);

		assertError(p[0], IProblem.UsedAsAType, 18, 1);
		assertError(p[1], IProblem.BaseTypeMustBeClassOrInterface, 18, 1);
	}

	public void testInterfaceTypeWrong() {
		String s = " class B { } int x; class A : B, x { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);

		assertError(p[0], IProblem.UsedAsAType, 33, 1);
		assertError(p[1], IProblem.BaseTypeMustBeClassOrInterface, 33, 1);
	}

	public void testInterfaceCannotDeclareFields() {
		String s = " interface A { int x; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.FieldsNotAllowedInInterfaces, 19, 1);
	}

	public void testFunctionsCannotBeConst() {
		String s = " const void bla() { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.FunctionsCannotBeConstOrAuto, 12, 3);
	}

	public void testFunctionsCannotBeAuto() {
		String s = " auto void bla() { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.FunctionsCannotBeConstOrAuto, 11, 3);
	}

	public void testNonVirtualFunctionsCannotBeAbstract() {
		String s = " private abstract void bla() { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.NonVirtualFunctionsCannotBeAbstract, 23, 3);
	}

	public void testFunctionDoesNotOverrideAny() {
		String s = " class X { public override void bla() { } }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.FunctionDoesNotOverrideAny, 32, 3);
	}
	
	public void testFunctionDoesNotOverrideAny_Not() {
		assertNoSemanticErrors(" class X { public void bla() { } } class Y : X { public override void bla() { } }");
	}

	public void testCannotOverrideFinalFunctions() {
		String s = " class X { final void bla() { } } class Y : X { public override final void bla() { } }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.CannotOverrideFinalFunctions, 75, 3);
	}

	public void testNewAllocatorsOnlyForClassOrStruct() {
		String s = " new(uint x) { return null; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.NewAllocatorsOnlyForClassOrStruct, 1, 3);
	}
	
	public void testNewAllocatorsOnlyForClassOrStruct_Not() {
		assertNoSemanticErrors(" class X { new(uint x) { return null; } } ");
	}
	
	public void testDeleteDeallocatorsOnlyForClassOrStruct() {
		String s = " delete(void* x) { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.DeleteDeallocatorsOnlyForClassOrStruct, 1, 6);
	}
	
	public void testDeleteDeallocatorsOnlyForClassOrStruct_Not() {
		assertNoSemanticErrors(" class X { delete(void* x) { } } ");
	}
	
	public void testNewIllegalArguments1() {
		String s = " class X { new() { } }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.AtLeastOneArgumentOfTypeExpected, 11, 3);
	}
	
	public void testNewIllegalArguments2() {
		String s = " class X { new(int x) { } }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.FirstArgumentMustBeOfType, 15, 3);
	}
	
	public void testDeleteIllegalArguments1() {
		String s = " class X { delete() { } }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.OneArgumentOfTypeExpected, 11, 6);
	}
	
	public void testDeleteIllegalArguments2() {
		String s = " class X { delete(int x) { } }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.OneArgumentOfTypeExpected, 18, 3);
	}
	
	public void testConstructorsOnlyForClass() {
		String s = " this() { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.ConstructorsOnlyForClass, 1, 4);
	}
	
	public void testConstructorsOnlyForClass_Not() {
		assertNoSemanticErrors(" class X { this() { } } ");
	}
	
	public void testDestructorsOnlyForClass() {
		String s = " ~this() { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.DestructorsOnlyForClass, 2, 4);
	}
	
	public void testDestructorsOnlyForClass_Not() {
		assertNoSemanticErrors(" class X { ~this() { } } ");
	}
	
	public void testInvariantsOnlyForClassStructUnion() {
		String s = " invariant { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.InvariantsOnlyForClassStructUnion, 1, 9);
	}
	
	public void testInvariantsOnlyForClassStructUnion_Not() {
		assertNoSemanticErrors(" class X { invariant { } } ");
		assertNoSemanticErrors(" struct X { invariant { } } ");
		assertNoSemanticErrors(" union X { invariant { } } ");
	}
	
	public void testOverrideOnlyAppliesToClassMemberFunctions() {
		String s = " override void bla() { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.OverrideOnlyForClassMemberFunctions, 15, 3);
	}
	
	public void testIllegalMainReturnType() {
		String s = " long main() { return 0; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.MustReturnIntOrVoidFromMainFunction, 1, 4);
	}

	public void testIllegalMainArguments() {
		String s = " int main(int x) { return 0; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.IllegalMainParameters, 5, 4);
	}
	
	public void testFunctionsCannotReturnAStaticArray() {
		String s = " int[3] bla() { return null; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.FunctionsCannotReturnStaticArrays, 1, 6);
	}
	
	public void testCannotHaveOutOrInoutParameterOfStaticArray() {
		String s = " void bla(out int[3] x) { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.CannotHaveOutOrInoutParameterOfTypeStaticArray, 14, 6);
	}
	
	public void testCannotHaveVoidParameter() {
		String s = " void bla(void x) { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.CannotHaveParameterOfTypeVoid, 10, 4);
	}
	
	public void testMoreThanOneInvariant() {
		String s = " class X { invariant { } invariant { } }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.MoreThanOneInvariant, 25, 9);
	}
	
	public void testStructsCannotBeAbstract() {
		String s = " abstract struct x { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.StructsCannotBeAbstract, 17, 1);
	}
	
	public void testUnionsCannotBeAbstract() {
		String s = " abstract union x { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.UnionsCannotBeAbstract, 16, 1);
	}
	
	public void testConstructorNotAllowedInStruct() {
		String s = " struct x { this() { } }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);

		assertError(p[0], IProblem.ConstructorsOnlyForClass, 12, 4);
		assertError(p[1], IProblem.SpecialMemberFunctionsNotAllowedForSymbol, 12, 4);
	}
	
	public void testDestructorNotAllowedInStruct() {
		String s = " struct x { ~this() { } }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);

		assertError(p[0], IProblem.DestructorsOnlyForClass, 13, 4);
		assertError(p[1], IProblem.SpecialMemberFunctionsNotAllowedForSymbol, 13, 4);
	}
	
	public void testDuplicatedTypeTemplateParameter() {
		String s = " template T(X, X) { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.ParameterMultiplyDefined, 15, 1);
	}
	
	public void testDuplicatedAliasTemplateParameter() {
		String s = " template T(X, alias X) { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.ParameterMultiplyDefined, 21, 1);
	}
	
	public void testDuplicatedValueTemplateParameter() {
		String s = " template T(X, int X) { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.ParameterMultiplyDefined, 19, 1);
	}
	
	public void testDuplicatedTupleTemplateParameter() {
		String s = " template T(X, X ...) { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.ParameterMultiplyDefined, 15, 1);
	}
	
	public void testSpecAliasTemplateParameterSymbolNotFound() {
		String s = " template T(alias X : Y) { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.SymbolNotFound, 22, 1);
	}
	
	public void testSpecAliasTemplateParameterSymbolNotFound_Not() {
		assertNoSemanticErrors(" int Y; template T(alias X : Y) { }");
	}
	
	public void testStaticAssertNoMessage() {
		String s = " static assert(false);";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.AssertionFailedNoMessage, 15, 5);
	}
	
	public void testStaticAssert() {
		String s = " static assert(false, \"message\");";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.AssertionFailed, 15, 5);
	}
	
	public void testFunctionMustReturnLong() {
		String s = " long bla() { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.FunctionMustReturnAResultOfType, 6, 3);
	}
	
	public void testFunctionMustReturnLong_Not() {
		assertNoSemanticErrors(" long bla() { return 0; }");
	}
	
	public void testFunctionMustReturnInt_Not() {
		assertNoSemanticErrors(" int bla() { return 0; }");
	}
	
	public void testCanImplicitlyConvertFromCharToIntInReturn() {
		assertNoSemanticErrors(" int bla() { return 'a'; }");
	}
	
	public void testCannotImplicitlyConvertFromLongToIntInReturn() {
		String s = " int x() { return 234873294873294; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.CannotImplicitlyConvert, 18, 15);
	}
	
	public void testCannotImplicitlyConvertFromIntToBoolInVarInitializer() {
		String s = " void x() { bool y = 2; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.CannotImplicitlyConvert, 21, 1);
	}
	
	public void testCanImplicitlyConvertFromIntToBoolInVarInitializer() {
		assertNoSemanticErrors(" void x() { bool y = 1; }");
	}
	
	public void testCanImplicitlyConvertFromIntToBoolInVarInitializerWithCast() {
		assertNoSemanticErrors(" void x() { bool y = cast(bool) 2; }");
	}
	
	public void testCannotImplicitlyConvertFromIntToBoolInAssignment() {
		String s = " void x() { bool y; y = 2; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.CannotImplicitlyConvert, 24, 1);
	}
	
	public void testCanImplicitlyConvertFromIntToBoolInAssignment() {
		assertNoSemanticErrors(" void x() { bool y; y = 1; }");
	}
	
	public void testCanImplicitlyConvertFromIntToBoolInAssignmentWithCast() {
		assertNoSemanticErrors(" void x() { bool y; y = cast(bool) 2; }");
	}
	
	public void testCannotImplicitlyConvertFromStringToBoolInVarInitializer() {
		String s = " void x() { bool y = \"hey\"; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.CannotImplicitlyConvert, 21, 5);
		assertEquals("Cannot implicitly convert expression (\"hey\") of type char[3u] to bool", p[0].getMessage());
	}
	
	public void testCannotImplicitlyConvertInConstfold() {
		assertSemanticProblems(
				"const int[] v = [1, 2, 3]; \r\n" + 
				"const int x = v[(\"h\" ~ \"ola\")];", 
				"\"h\" ~ \"ola\"", IProblem.CannotImplicitlyConvert);
	}
	
	public void testCannotImplicitlyConvertInConstfold2() {
		assertSemanticProblems(
				"const int* x;\r\n" + 
				"const int* y;\r\n" + 
				"const int* z = x - y;", 
				"x - y", IProblem.CannotImplicitlyConvert);
	}
	
	public void testVoidFunctionsHaveNoResult() {
		String s = " void bla() out(id) { } body { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);

		assertError(p[0], IProblem.VoidFunctionsHaveNoResult, 16, 2);
		assertError(p[1], IProblem.VoidsHaveNoValue, 1, 4);
	}
	
	public void testReturnInPrecondition() {
		String s = " void bla() in { return 1; } body { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.ReturnStatementsCannotBeInContracts, 17, 9);
	}
	
	public void testThisOnlyAllowedInNonStaticMemberFunctions() {
		String s = " void bla() { this = 2; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.ThisOnlyAllowedInNonStaticMemberFunctions, 14, 4);
	}
	
	public void testSuperOnlyAllowedInNonStaticMemberFunctions() {
		String s = " void bla() { super = 2; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.SuperOnlyAllowedInNonStaticMemberFunctions, 14, 5);
	}
	
	public void testUndefinedIdentifier() {
		String s = " int bla() { return a; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.UndefinedIdentifier, 20, 1);
	}
	
	public void testUndefinedIdentifier_NOT() {
		assertNoSemanticErrors(" int bla() { int a; return a; }");
	}
	
	public void testNotAnAggregateType() {
		String s = " void bla() { int a; foreach(x; a) { } }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.CannotInferTypeForSymbol, 29, 1);
	}
	
	public void testNotAnAggregateType_OK() {
		assertNoSemanticErrors(" void bla() { int[] a; foreach(x; a) { } }");
	}
	
	public void testStringExpectedForPragmaMsg() {
		String s = " void bla() { pragma(msg, 1); }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.StringExpectedForPragmaMsg, 26, 1);
	}
	
	public void testStringExpectedForPragmaMsg_Not() {
		assertNoSemanticErrors(" void bla() { pragma(msg, \"somelib\"); }");
	}
	
	public void testStringExpectedForPragmaLib() {
		String s = " void bla() { pragma(lib, 1); }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.StringExpectedForPragmaLib, 26, 1);
	}
	
	public void testStringExpectedForPragmaLib_Not() {
		assertNoSemanticErrors(" void bla() { pragma(lib, \"somelib\"); }");
	}
	
	public void testPragmaIllegalArguments() {
		String s = " void bla() { pragma(lib); }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.LibPragmaMustRecieveASingleArgumentOfTypeString, 14, 6);
	}
	
	public void testUnrecognizedPragma() {
		String s = " void bla() { pragma(some); }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.UnrecognizedPragma, 21, 4);
	}
	
	public void testAnonUnionNotInAggregate() {
		String s = " union { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.AnonCanOnlyBePartOfAnAggregate, 1, 5);
	}
	
	public void testAnonUnionNotInAggregate_Not() {
		assertNoSemanticErrors(" class X { union { } }");
	}
	
	public void testAnonStructNotInAggregate() {
		String s = " struct { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.AnonCanOnlyBePartOfAnAggregate, 1, 6);
	}
	
	public void testAnonStructNotInAggregate_Not() {
		assertNoSemanticErrors(" class X { struct { } }");
	}
	
	public void testPragmaIsMissingClosingSemicolon() {
		String s = " pragma(lib, \"a\")";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.PragmaIsMissingClosingSemicolon, 1, 6);
	}
	
	public void testUnrecongnizedTrait() {
		String s = " bool a = __traits(hello);";
		IProblem[] p = getModuleProblems(s, AST.D2);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.UnrecongnizedTrait, 19, 5);
	}
	
	public void testUnrecongnizedTrait_Not() {
		assertNoSemanticErrors(" bool a = __traits(isAbstractClass);", AST.D2);
		assertNoSemanticErrors(" bool a = __traits(isArithmetic);", AST.D2);
		assertNoSemanticErrors(" bool a = __traits(isAssociativeArray);", AST.D2);
		assertNoSemanticErrors(" bool a = __traits(isFinalClass);", AST.D2);
		assertNoSemanticErrors(" bool a = __traits(isFloating);", AST.D2);
		assertNoSemanticErrors(" bool a = __traits(isIntegral);", AST.D2);
		assertNoSemanticErrors(" bool a = __traits(isScalar);", AST.D2);
		assertNoSemanticErrors(" bool a = __traits(isStaticArray);", AST.D2);
		assertNoSemanticErrors(" bool a = __traits(isUnsigned);", AST.D2);
		assertNoSemanticErrors(" bool a = __traits(isVirtualFunction);", AST.D2);
		assertNoSemanticErrors(" bool a = __traits(isAbstractFunction);", AST.D2);
		assertNoSemanticErrors(" bool a = __traits(isFinalFunction);", AST.D2);
		// TODO arguments must be passed in order to not fail
		// assertNoSemanticErrors(" bool a = __traits(hasMember);", AST.D2);
		// assertNoSemanticErrors(" bool a = __traits(getMember);", AST.D2);
		// assertNoSemanticErrors(" bool a = __traits(getVirtualFunctions);", AST.D2);
		// assertNoSemanticErrors(" bool a = __traits(classInstanceSize);", AST.D2);
		// assertNoSemanticErrors(" bool a = __traits(allMembers);", AST.D2);
		// assertNoSemanticErrors(" bool a = __traits(derivedMembers);", AST.D2);
	}
	
	public void testCanOnlyConcatenateArrays() {
		String s = "auto a = 2 ~ 4;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.CanOnlyConcatenateArrays, 9, 5);
	}
	
	public void testCanOnlyConcatenateArrays_Not() {
		// This suit is black... NOT!!!
		assertNoSemanticErrors("auto a = \"Hello \" ~ \"World!\";");
	}
	
	public void testIntInit() {
		assertNoSemanticErrors("int x = int.init;");
	}
	
	public void testIntSizeof() {
		assertNoSemanticErrors("int x = int.sizeof;");
	}
	
	public void testIntUndefinedProperty() {
		String s = "int x = int.something;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.UndefinedProperty, 12, 9);
	}
	
	public void testIntSizePropertyDeprecated() {
		String s = "int x = int.size;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.DeprecatedProperty, 12, 4);
	}
	
	public void testIntTypeinfoPropertyDeprecated() {
		String s = "TypeInfo x = int.typeinfo;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.DeprecatedProperty, 17, 8);
	}
	
	public void testBaseClassIsOuter() {
		assertNoSemanticErrors("class BaseClass { class SomeClass : BaseClass { } }");
	}
	
	public void testVersionConditionOk() {
		assertNoSemanticErrors("version = something;");
	}
	
	public void testVersionConditionPredefined() {
		String s = "version = DigitalMars;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.VersionIdentifierReserved, 10, 11);
	}
	
	public void testVersionConditionPredefined2() {
		String s = "version = D_something;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.VersionIdentifierReserved, 10, 11);
	}
	
	public void testAssignToBoolean() {
		String s = "void foo() { int x = 2; if (x = 3) { } }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.ExpressionDoesNotGiveABooleanResult, 28, 5);
	}
	
	public void testDeleteToBoolean() {
		String s = "void foo() { int* x; if (delete x) { } }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.ExpressionDoesNotGiveABooleanResult, 25, 8);
	}
	
	public void testBreakIsNotInsideALoopOrSwitch() {
		String s = "void foo() { break; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.BreakIsNotInsideALoopOrSwitch, 13, 6);
	}
	
	public void testBreakIsNotInsideALoopOrSwitch_NotInWhile() {
		assertNoSemanticErrors("void foo() { while(true) { break; } }");
	}
	
	public void testBreakIsNotInsideALoopOrSwitch_NotInSwitch() {
		assertNoSemanticErrors("void foo() { switch(true) { case true: break; default: } }");
	}
	
	public void testCaseIsNotInsideASwitchStatement() {
		String s = "void foo() { case 1:; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.CaseIsNotInSwitch, 13, 8);
	}
	
	public void testCaseIsNotInsideASwitchStatement_Not() {
		assertNoSemanticErrors("void foo() { switch(true) { case true: break; default: } }");
	}
	
	public void testVersionDeclarationMustBeAtModuleLevel() {
		String s = "class X { version = something; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.VersionDeclarationMustBeAtModuleLevel, 10, 20);
	}
	
	public void testVersionDeclarationMustBeAtModuleLevel_Not() {
		assertNoSemanticErrors("version = something;");
	}
	
	public void testDebugDeclarationMustBeAtModuleLevel() {
		String s = "class X { debug = something; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.DebugDeclarationMustBeAtModuleLevel, 10, 18);
	}
	
	public void testDebugDeclarationMustBeAtModuleLevel_Not() {
		assertNoSemanticErrors("debug = something;");
	}
	
	public void testGotoCaseNotInSwitch() {
		String s = "void foo() { goto case 1; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.GotoCaseNotInSwitch, 13, 12);
	}
	
	public void testGotoCaseNotInSwitch_Not() {
		assertNoSemanticErrors("void foo() { switch(true) { case true: break; case false: goto case true; default: } }");
	}
	
	public void testGotoDefaultNotInSwitch() {
		String s = "void foo() { goto default; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.GotoDefaultNotInSwitch, 13, 13);
	}
	
	public void testGotoDefaultNotInSwitch_Not() {
		assertNoSemanticErrors("void foo() { switch(true) { case false: goto default; default: } }");
	}
	
	public void testLazyVariablesCannotBeLvalues() {
		String s = "void foo(lazy int x) { x = 2; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.LazyVariablesCannotBeLvalues, 23, 1);
	}
	
	public void testStatementIsNotReachable() {
		String s = "void foo() { return; int x; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertWarning(p[0], IProblem.StatementIsNotReachable, 21, 6);
	}
	
	public void testDivisionByZeroWithDiv() {
		assertSemanticProblems("void foo() { int x = 2 / 0; }",
			"2 / 0", IProblem.DivisionByZero);
	}
	
	public void testDivisionByZeroWithMod() {
		assertSemanticProblems("void foo() { int x = 2 % 0; }", 
			"2 % 0", IProblem.DivisionByZero);
	}
	
	public void testDefaultNotInSwitch() {
		assertSemanticProblems("void foo() { default: }",
			"default:", IProblem.DefaultNotInSwitch);
	}
	
	public void testDefaultNotInSwitch_Not() {
		assertNoSemanticErrors("void foo() { switch(true) { default: } }");
	}
	
	public void testSwitchAlreadyHasDefault() {
		assertSemanticProblems("void foo() { switch(true) { default: break; default: } }",
			"default:", IProblem.SwitchAlreadyHasDefault, 44);
	}
	
	public void testContinueNotInLoop() {
		assertSemanticProblems("void foo() { continue; }", 
			"continue;", IProblem.ContinueNotInLoop);
	}
	
	public void testContinueNotInLoop_Not() {
		assertNoSemanticErrors("void foo() { while(true) { continue; } }");
	}
	
	public void testForeachIndexCannotBeRef() {
		assertSemanticProblems("void foo() { int[int] x; foreach(ref a, b; x) { } }",
			"ref a", IProblem.ForeachIndexCannotBeRef);
	}
	
	public void testFunctionArguments() {
		assertSemanticProblems("void foo(int x) {  } void bar() { foo(); }",
			"foo()", IProblem.ParametersDoesNotMatchParameterTypes,
			"foo()", IProblem.ExpectedNumberArguments);
	}
	
	public void testFunctionArguments_Not() {
		assertNoSemanticErrors("void foo(int x) { } void bar() { foo(1); }");
	}
	
	public void testIncompatibleTypes() {
		assertSemanticProblems("class X { } void foo() { X x = new X(); x = x + x; }",
			"x + x", IProblem.IncompatibleTypesForOperator);
	}

	public void testSymbolNotDefined() {
		assertSemanticProblems("mixin T!();",
			"T!()", IProblem.SymbolNotDefined);
	}

	public void testSymbolNotATemplate() {
		assertSemanticProblems("class T { } mixin T!();",
			"T!()", IProblem.SymbolNotATemplate);
	}
	
	public void testCannotDeleteType() {
		assertSemanticProblems("void foo() { delete 1; }",
			"1", IProblem.ConstantIsNotAnLValue,
			"delete 1", IProblem.CannotDeleteType);
	}
	
	public void testNotAnLvalue() {
		assertSemanticProblems("void foo() { delete new int; }",
				"new int", IProblem.NotAnLvalue);
	}
	
	public void testNoMatchForImplicitSuperConstructor1() {
		assertSemanticProblems(
				"class A { this(int x) { } }\r\n" + 
				"class B : A { }", 
				"B", IProblem.NoMatchForImplicitSuperCallInConstructor);
	}
	
	public void testNoMatchForImplicitSuperConstructor2() {
		String s = "class A { this(int x) { } } class B : A { this() { } }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.NoMatchForImplicitSuperCallInConstructor, 42, 4);
	}
	
	public void testClassConstructorCallMustBeInConstructor() {
		assertSemanticProblems(
				"void foo() { this(); }", 
				"this", IProblem.ClassConstructorCallMustBeInAConstructor);
	}
	
	public void testSuperClassConstructorCallMustBeInConstructor() {
		assertSemanticProblems(
				"void foo() { super(); }", 
				"super", IProblem.SuperClassConstructorCallMustBeInAConstructor);
	}
	
	public void testThisIsNotInAStructOrClassScope() {
		assertSemanticProblems(
				"void foo() { typeof(this) x; }", 
				"this", IProblem.ThisNotInClassOrStruct,
				"this", IProblem.ThisOnlyAllowedInNonStaticMemberFunctions);
	}
	
	public void testSuperIsNotInAClassScope() {
		assertSemanticProblems(
				"void foo() { typeof(super) x; }", 
				"super", IProblem.SuperNotInClass,
				"super", IProblem.SuperOnlyAllowedInNonStaticMemberFunctions);
	}
	
	public void testBaseTypeMustBeInterface() {
		assertSemanticProblems(
				"class B { } interface A : B { }", 
				"B", IProblem.BaseTypeMustBeInterface, 10);
	}
	
	public void testBaseTypeMustBeInterface2() {
		assertSemanticProblems(
				"class B { } interface A : private B { }", 
				"B", IProblem.BaseTypeMustBeInterface, 10);
	}
	
	public void testCannotInferTypeFromThisArrayInitializer() {
		assertSemanticProblems(
				"auto x = [1: 1, 2u];", 
				"[1: 1, 2u]", IProblem.CannotInferTypeFromThisArrayInitializer);
	}
	
	public void testOpApplyFunctionMustReturnAnInt() {
		assertSemanticProblems(
				"class X {\r\n" + 
				"	bool opApply(int delegate(ref int) dg) {\r\n" + 
				"		return 1;\r\n" + 
				"	}\r\n" + 
				"}\r\n" + 
				"\r\n" + 
				"void foo() {\r\n" + 
				"	foreach(x; new X()) {\r\n" + 
				"		\r\n" + 
				"	}\r\n" + 
				"}", 
				"new X()", IProblem.OpApplyFunctionMustReturnAnInt);
	}
	
	public void testArrayIndexOutOfBounds2() {
		assertSemanticProblems(
				"const int[] v = [1, 2, 3]; const int x = v[3];", 
				"3", IProblem.ArrayIndexOutOfBounds2, 30);
	}
	
	public void testArrayIndexOutOfBounds2WithAdd() {
		assertSemanticProblems(
				"const int[] v = [1, 2, 3]; const int x = v[1 + 2];", 
				"1 + 2", IProblem.ArrayIndexOutOfBounds2);
	}
	
	public void testArrayIndexOutOfBounds2WithMinus() {
		assertSemanticProblems(
				"const int[] v = [1, 2, 3]; const int x = v[5 - 2];", 
				"5 - 2", IProblem.ArrayIndexOutOfBounds2);
	}
	
	public void testArrayIndexOutOfBounds2WithMul() {
		assertSemanticProblems(
				"const int[] v = [1, 2, 3]; const int x = v[5 * 2];", 
				"5 * 2", IProblem.ArrayIndexOutOfBounds2);
	}
	
	public void testArrayIndexOutOfBounds2WithDiv() {
		assertSemanticProblems(
				"const int[] v = [1, 2, 3]; const int x = v[10 / 2];", 
				"10 / 2", IProblem.ArrayIndexOutOfBounds2);
	}
	
	public void testArrayIndexOutOfBounds2WithMod() {
		assertSemanticProblems(
				"const int[] v = [1, 2, 3]; const int x = v[9 % 10];", 
				"9 % 10", IProblem.ArrayIndexOutOfBounds2);
	}
	
	public void testArrayIndexOutOfBounds2WithLength() {
		assertSemanticProblems(
				"const int[] v = [1, 2, 3]; const int x = v[v.length];", 
				"v.length", IProblem.ArrayIndexOutOfBounds2);
	}
	
	public void testArrayIndexOutOfBounds2WithNeg() {
		assertSemanticProblems(
				"const int[] v = [1, 2, 3]; const int y = -3; const int x = v[-y];", 
				"-y", IProblem.ArrayIndexOutOfBounds2);
	}
	
	public void testArrayIndexOutOfBounds2WithIndex() {
		assertSemanticProblems(
				"const int[] v = [1, 2, 3]; const int x = v[v[2]];", 
				"v[2]", IProblem.ArrayIndexOutOfBounds2);
	}
	
	public void testArrayIndexOutOfBounds2WithCat() {
		assertSemanticProblems(
				"const int[] v = [1, 2, 3]; const int x = v[(\"a\" ~ \"bc\").length];", 
				"(\"a\" ~ \"bc\").length", IProblem.ArrayIndexOutOfBounds2);
	}
	
	public void testArrayIndexOutOfBounds2WithShl() {
		assertSemanticProblems(
				"const int[] v = [1, 2, 3]; const int x = v[1 << 2];", 
				"1 << 2", IProblem.ArrayIndexOutOfBounds2);
	}
	
	public void testArrayIndexOutOfBounds2WithShr() {
		assertSemanticProblems(
				"const int[] v = [1, 2, 3]; const int x = v[10 >> 1];", 
				"10 >> 1", IProblem.ArrayIndexOutOfBounds2);
	}
	
	public void testArrayIndexOutOfBounds2WithUshr() {
		assertSemanticProblems(
				"const int[] v = [1, 2, 3]; const int x = v[10 >>> 1];", 
				"10 >>> 1", IProblem.ArrayIndexOutOfBounds2);
	}
	
	public void testArrayIndexOutOfBounds2WithAnd() {
		assertSemanticProblems(
				"const int[] v = [1, 2, 3]; const int x = v[10 & 10];", 
				"10 & 10", IProblem.ArrayIndexOutOfBounds2);
	}
	
	public void testArrayIndexOutOfBounds2WithOr() {
		assertSemanticProblems(
				"const int[] v = [1, 2, 3]; const int x = v[10 | 10];", 
				"10 | 10", IProblem.ArrayIndexOutOfBounds2);
	}
	
	public void testArrayIndexOutOfBounds2WithXor() {
		assertSemanticProblems(
				"const int[] v = [1, 2, 3]; const int x = v[10 ^ 0];", 
				"10 ^ 0", IProblem.ArrayIndexOutOfBounds2);
	}
	
	public void testArrayIndexOutOfBounds2WithCall() {
		assertSemanticProblems(
				"const int[] v = [1, 2, 3]; int foo() { return 3; } const int x = v[foo( )];", 
				"foo( )", IProblem.ArrayIndexOutOfBounds2);
	}
	
	public void testIncompatibleTypesForMinus() {
		assertSemanticProblems(
				"const int* x;\r\n" + 
				"const int z = x - \"a\";", 
				"x - \"a\"", IProblem.IncompatibleTypesForMinus);
	}
	
	public void testExpressionHasNoEffect() {
		assertSemanticProblems(
				"void foo() { [1, 2, 3].length; }", 
				"[1, 2, 3].length;", IProblem.ExpressionHasNoEffect);
	}
	
	// TODO we'll need to implement the ::toDt methods for this
	public void testTooManyInitiailizersForArray() {
		assertSemanticProblems(
				"const int[1] a = [1, 2];", 
				"[1, 2]", IProblem.TooManyInitializers);
	}
	
	public void testTooManyInitiailizersForStruct() {
		assertSemanticProblems(
				"struct Struct {\r\n" + 
				"	int x;\r\n" + 
				"}\r\n" + 
				"Struct b = { 1, 2 };", 
				"{ 1, 2 }", IProblem.TooManyInitializers);
	}
	
	public void testCannotHaveArrayOfTypeInTypeDArray() {
		assertSemanticProblems(
				"int foo() {\r\n" + 
				"	return 2;\r\n" + 
				"}\r\n" + 
				"typeof(foo)[] x;", 
				"typeof(foo)[]", IProblem.CannotHaveArrayOfType);
	}
	
	public void testCannotHaveArrayOfTypeInTypeSArray() {
		assertSemanticProblems(
				"int foo() {\r\n" + 
				"	return 2;\r\n" + 
				"}\r\n" + 
				"typeof(foo)[3] x;", 
				"typeof(foo)[3]", IProblem.CannotHaveArrayOfType);
	}
	
	// TODO why dmd reports errors twice? grrr...
	public void testStringIndexOutOfBounds() {
		assertSemanticProblems(
				"const char c = \"hola\"[5];", 
				"5", IProblem.StringIndexOutOfBounds,
				"5", IProblem.StringIndexOutOfBounds);
	}
	
	public void testVersionDefinedAfterUse() {
		assertSemanticProblems(
				"version (foo) {\r\n" + 
				"	\r\n" + 
				"} else {\r\n" + 
				"	version = foo;\r\n" + 
				"}", 
				"version = foo;", IProblem.VersionDefinedAfterUse);
	}
	
	public void testDebugDefinedAfterUse() {
		assertSemanticProblems(
				"debug (foo) {\r\n" + 
				"	\r\n" + 
				"} else {\r\n" + 
				"	debug = foo;\r\n" + 
				"}", 
				"debug = foo;", IProblem.DebugDefinedAfterUse);
	}
	
	public void testWithExpressionsMustBeClassObjects() {
		assertSemanticProblems(
				"void foo() {\r\n" + 
				"	with(1) {\r\n" + 
				"		\r\n" + 
				"	}\r\n" + 
				"}", 
				"1", IProblem.WithExpressionsMustBeClassObject);
	}
	
	public void testCanOnlySynchronizeOnClassObjects() {
		assertSemanticProblems(
				"void foo() {\r\n" + 
				"	synchronized(1) {\r\n" + 
				"	\r\n" + 
				"	}\r\n" + 
				"}", 
				"1", IProblem.CanOnlySynchronizeOnClassObjects);
	}

	public void testParameterIsAlreadyDefined() {
		assertSemanticProblems(
				"void foo(int x, int x) {\r\n" + 
				"}", 
				"x", IProblem.ParameterIsAlreadyDefined, 15);
	}
	
	public void testNewCanOnlyCreateStructsDynamicArraysAndClassObjects() {
		assertSemanticProblems(
				"void foo() { void* x = new foo; }", 
				"foo", IProblem.UsedAsAType, 20,
				"new foo", IProblem.NewCanOnlyCreateStructsDynamicArraysAndClassObjects
				);
	}
	
	public void testTooManyInitializersForStructLiteral() {
		assertSemanticProblems(
				"struct str { int a, b; } str x = { 1, 2, 3 };", 
				"{ 1, 2, 3 }", IProblem.TooManyInitializers
				);
	}
	
	public void testMismatchedFunctionReturnTypeInference() {
		assertSemanticProblems(
				"auto x = { if (true) return 1; else return 1.0; };", 
				"1.0", IProblem.MismatchedFunctionReturnTypeInference
				);
	}
	
	public void testShiftLeftExceeds() {
		assertSemanticProblems(
				"void foo() {\r\n" + 
				"	byte x = 2;\r\n" + 
				"	int y = x << 33;\r\n" + 
				"}", 
				"x << 33", IProblem.ShiftLeftExceeds
				);
	}
	
	public void testSymbolCannotBeSliced() {
		assertSemanticProblems(
				"int y = 1[0 .. 3];", 
				"1[0 .. 3]", IProblem.SymbolCannotBeSlicedWithBrackets
				);
	}
	
	public void testSymbolIsNotAMemberOf() {
		assertSemanticProblems(
				"struct foo {\r\n" + 
				"	int x;\r\n" + 
				"}\r\n" + 
				"\r\n" + 
				"foo f = {y: 1};", 
				"y", IProblem.SymbolIsNotAMemberOf
				);
	}
	
	public void testCanOnlyDereferenceAPointer() {
		assertSemanticProblems("int x = *2;", 
				"*2", IProblem.CanOnlyDereferenceAPointer
				);
	}
	
	public void testSymbolMustBeAnArrayOfPointerType() {
		assertSemanticProblems("int y = y[0];", 
				"y[0]", IProblem.SymbolMustBeAnArrayOfPointerType
				);
	}
	
	public void testCircularInheritanceOfInterface() {
		assertSemanticProblems(
				"interface A : B {\r\n" + 
				"}\r\n" + 
				"interface B : A {\r\n" + 
				"}", 
				"A", IProblem.CircularInheritanceOfInterface, 20
				);
	}
	
	public void testCannotReturnExpressionFromConstructor() {
		assertSemanticProblems(
				"class Foo {\r\n" + 
				"	this() {\r\n" + 
				"		return 1;\r\n" + 
				"	}\r\n" + 
				"}", 
				"return 1", IProblem.CannotReturnExpressionFromConstructor
				);
	}
	
	public void testReturnInFinally() {
		assertSemanticProblems(
				"int foo() {\r\n" + 
				"	try {\r\n" + 
				"	} finally {\r\n" + 
				"		return 1;\r\n" + 
				"	}\r\n" + 
				"}", 
				"return 1;", IProblem.ReturnStatementsCannotBeInFinallyScopeExitOrScopeSuccessBodies,
				"foo", IProblem.NoReturnAtEndOfFunction
				);
	}
	
	/**
	 * Utility method for testing semantic problems. It is passed the source
	 * as the first argument, and then a variable list of expected problems.
	 * 
	 * The problems must be in the form of the string of the expected problem
	 * source followed by the problem ID, and optionally by the offset in the
	 * string to start searching at (passed to Java's String.indexOf()) if the
	 * source string contains multiple occurances of the problematic substring.
	 * 
	 * For example:
	 * ------------
	 * assertSemanticProblems("int a; int a;",
	 *     "a", IProblem.DuplicatedSymbol,
	 *     "a", IProblem.DuplicatedSymbol, 11);
	 * ------------
	 * 
	 * @param source
	 * @param problems
	 */
	private void assertSemanticProblems(String source, Object... problems)
	{
		class SemanticProblem
		{
			int problemId;
			int start;
			int length;
		}
		
		if(problems.length == 0)
		{
			assertNoSemanticErrors(source);
		}
		else
		{
			List<SemanticProblem> semanticProblems = 
				new ArrayList<SemanticProblem>();
			
			// Parse the arguments
			int argIndex = 0;
			while(argIndex < problems.length)
			{
				SemanticProblem problem = new SemanticProblem();
				
				assertTrue(problems[argIndex] instanceof String);
				String problemString = (String) problems[argIndex];
				argIndex++;
				
				assertTrue(problems[argIndex] instanceof Integer);
				problem.problemId = ((Integer) problems[argIndex]).intValue();
				assertTrue(problem.problemId > 0);
				argIndex++;
				
				int offset;
				if(argIndex < problems.length && problems[argIndex] instanceof Integer)
				{
					offset = ((Integer) problems[argIndex]).intValue();
					assertTrue(offset >= 0);
					argIndex++;
				}
				else
				{
					offset = 0;
				}
				
				problem.start = source.indexOf(problemString, offset);
				assertTrue(problem.start >= 0);
				problem.length = problemString.length();
				assertTrue(problem.length > 0);
				
				semanticProblems.add(problem);
			}
			
			// Check the module problems
			IProblem[] p = getModuleProblems(source);
			assertEquals(semanticProblems.size(), p.length);
			
			for(int i = 0; i < semanticProblems.size(); i++)
			{
				SemanticProblem sp = semanticProblems.get(i);
				assertError(p[i], sp.problemId, sp.start, sp.length);
			}
		}
	}
	
	/* TODO test for SemanticContext.IN_GCC = true
	public void testCannotPutCatchStatementInsideFinallyBlock() {
		String s = "void foo() { try { } finally { try { } catch { } } }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);
		
		assertError(p[0], IProblem.CannotPutCatchStatementInsideFinallyBlock, 39, 9);
	}
	*/

}
