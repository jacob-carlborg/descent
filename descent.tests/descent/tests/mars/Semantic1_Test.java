package descent.tests.mars;

import descent.core.compiler.IProblem;
import descent.core.dom.AST;

public class Semantic1_Test extends Parser_Test {

	public void testDuplicatedVar1() {
		String s = "int a; int a;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);

		assertError(p[0], IProblem.DuplicatedSymbol, 4, 1);
		assertError(p[1], IProblem.DuplicatedSymbol, 11, 1);
	}

	public void testDuplicatedVar2() {
		String s = "int a, a;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);

		assertError(p[0], IProblem.DuplicatedSymbol, 4, 1);
		assertError(p[1], IProblem.DuplicatedSymbol, 7, 1);
	}

	public void testDuplicatedSymbol() {
		String s = "int a; class a { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);

		assertError(p[0], IProblem.DuplicatedSymbol, 4, 1);
		assertError(p[1], IProblem.DuplicatedSymbol, 13, 1);
	}

	public void testDuplicatedSymbolWithProt() {
		String s = "int a; public class a { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);

		assertError(p[0], IProblem.DuplicatedSymbol, 4, 1);
		assertError(p[1], IProblem.DuplicatedSymbol, 20, 1);
	}

	public void testDuplicatedSymbolWithStorageClass() {
		String s = "int a; static class a { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);

		assertError(p[0], IProblem.DuplicatedSymbol, 4, 1);
		assertError(p[1], IProblem.DuplicatedSymbol, 20, 1);
	}

	public void testDuplicatedSymbolWithLink() {
		String s = "int a; extern { int a; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);

		assertError(p[0], IProblem.DuplicatedSymbol, 4, 1);
		assertError(p[1], IProblem.DuplicatedSymbol, 20, 1);
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

		assertError(p[0], IProblem.ForwardReference, 6, 1);
		assertError(p[1], IProblem.ForwardReference, 15, 1);
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

	// TODO should only report one problem (the first).
	// Dmd reports three in this case, we are trying
	// to make it cleaner to the user
	public void testUsedAsAType() {
		String s = " class X  { } Y y;";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);

		assertError(p[0], IProblem.UndefinedIdentifier, 14, 1);
		assertError(p[1], IProblem.UsedAsAType, 14, 1);
	}

	public void testUsedAsAType_Not() {
		assertNoSemanticErrors(" class X { } X x;");
	}

	public void testCannotInferTypeFromArrayInitializer() {
		String s = " auto x = [];";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.CannotInferType, 10, 2);
	}

	public void testCannotInferTypeFromArrayInitializer2() {
		String s = " auto x = [ 1 : 1 ];";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.CannotInferType, 10, 9);
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
		assertEquals(1, p.length);

		assertError(p[0], IProblem.UsedAsAType, 18, 1);
	}

	public void testInterfaceTypeWrong() {
		String s = " class B { } int x; class A : B, x { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.UsedAsAType, 33, 1);
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
		assertNoSemanticErrors(" class X { new(uint x) { } } ");
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

		assertError(p[0], IProblem.DestructorsOnlyForClass, 1, 5);
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
		String s = " int[3] bla() { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);

		assertError(p[0], IProblem.FunctionsCannotReturnStaticArrays, 1, 6);
		assertError(p[1], IProblem.FunctionMustReturnAResultOfType, 8, 3);
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
		assertEquals(1, p.length);

		assertError(p[0], IProblem.ConstructorsOnlyForClass, 12, 4);
	}
	
	public void testDestructorNotAllowedInStruct() {
		String s = " struct x { ~this() { } }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.DestructorsOnlyForClass, 12, 5);
	}
	
	public void testDuplicatedTypeTemplateParameter() {
		String s = " template T(X, X) { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.DuplicatedParameter, 15, 1);
	}
	
	public void testDuplicatedAliasTemplateParameter() {
		String s = " template T(X, alias X) { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.DuplicatedParameter, 21, 1);
	}
	
	public void testDuplicatedValueTemplateParameter() {
		String s = " template T(X, int X) { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.DuplicatedParameter, 19, 1);
	}
	
	public void testDuplicatedTupleTemplateParameter() {
		String s = " template T(X, X ...) { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.DuplicatedParameter, 15, 1);
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
		assertEquals("Type mismatch: cannot implicitly convert from char[3] to bool", p[0].getMessage());
	}
	
	public void testVoidFunctionsHaveNoResult() {
		String s = " void bla() out(id) { } body { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.VoidFunctionsHaveNoResult, 16, 2);
	}
	
	public void testReturnInPrecondition() {
		String s = " void bla() in { return 1; } body { }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(1, p.length);

		assertError(p[0], IProblem.ReturnStatementsCannotBeInContracts, 17, 9);
	}
	
	public void testThisOnlyAllowedInNonStaticMemberFunctions() {
		String s = " void bla() { this; }";
		IProblem[] p = getModuleProblems(s);
		assertEquals(2, p.length);

		assertError(p[0], IProblem.ThisOnlyAllowedInNonStaticMemberFunctions, 14, 4);
		assertError(p[1], IProblem.ExpressionHasNoEffect, 14, 4);
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

		assertError(p[0], IProblem.NotAnAggregateType, 32, 1);
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
		assertNoSemanticErrors(" bool a = __traits(hasMember);", AST.D2);
		assertNoSemanticErrors(" bool a = __traits(getMember);", AST.D2);
		assertNoSemanticErrors(" bool a = __traits(getVirtualFunctions);", AST.D2);
		assertNoSemanticErrors(" bool a = __traits(classInstanceSize);", AST.D2);
		assertNoSemanticErrors(" bool a = __traits(allMembers);", AST.D2);
		assertNoSemanticErrors(" bool a = __traits(derivedMembers);", AST.D2);
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

}
