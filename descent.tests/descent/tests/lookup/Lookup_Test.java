package descent.tests.lookup;

import java.util.Hashtable;

import descent.core.JavaCore;

/*
 * Tests to check that the implementation of the resolved part of the
 * semantic analysis is working as expected.
 */
public class Lookup_Test extends AbstractLookupTest {
	
	@Override
	protected void setUp() throws Exception {
		Hashtable opts = JavaCore.getOptions();
		opts.put(JavaCore.COMPILER_SHOW_SEMANTIC_ERRORS, "2");
		JavaCore.setOptions(opts);
		
		super.setUp();
	}
	
	public void testExtern() throws Exception {
		one("extern { class Bar; }");
		two("Bar bar;");
		assertNoErrors();
	}
	
	public void testExternCpp() throws Exception {
		one("extern(C++) { class Bar; }");
		two("Bar bar;");
		assertNoErrors();
	}
	
	public void testFieldDefinedNotOk() throws Exception {
		one("");
		two("void foo() { field = 2; }");
		assertErrors();
	}
	
	public void testFieldDefinedOk() throws Exception {
		one("int field;");
		two("void foo() { field = 2; }");
		assertNoErrors();
	}
	
	public void testAliasDefinedOk() throws Exception {
		one("int field; alias field someAlias;");
		two("void foo() { someAlias = 2; }");
		assertNoErrors();
	}
	
	public void testTypedefDefinedOk() throws Exception {
		one("typedef int someTypedef;");
		two("void foo(someTypedef foo) { foo = 2; }");
		assertNoErrors();
	}
	
	public void testClassDefinedNotOk() throws Exception {
		one("");
		two("Bar b;");
		assertErrors();
	}
	
	public void testClassDefinedOk() throws Exception {
		one("class Bar { }");
		two("Bar b;");
		assertNoErrors();
	}
	
	public void testClassMangle() throws Exception {
		one("class Bar { }");
		two("static assert(Bar.mangleof == \"C3one3Bar\");");
		assertNoErrors();
	}
	
	public void testInterfaceDefinedOk() throws Exception {
		one("interface Bar { a }");
		two("Bar b;");
		assertNoErrors();
	}
	
	public void testInterfaceMangle() throws Exception {
		one("interface Bar { }");
		two("static assert(Bar.mangleof == \"C3one3Bar\");");
		assertNoErrors();
	}
	
	public void testStructDefinedOk() throws Exception {
		one("struct Bar { }");
		two("Bar b;");
		assertNoErrors();
	}
	
	public void testStructMangle() throws Exception {
		one("struct Bar { }");
		two("static assert(Bar.mangleof == \"S3one3Bar\");");
		assertNoErrors();
	}
	
	public void testUnionDefinedOk() throws Exception {
		one("union Bar { int a; }");
		two("Bar b;");
		assertNoErrors();
	}
	
	public void testUnionMangle() throws Exception {
		one("union Bar { }");
		two("static assert(Bar.mangleof == \"S3one3Bar\");");
		assertNoErrors();
	}
	
	public void testEnumDefinedOk() throws Exception {
		one("enum Bar { a }");
		two("Bar b;");
		assertNoErrors();
	}
	
	public void testEnumMangle() throws Exception {
		one("enum Bar { a }");
		two("static assert(Bar.mangleof == \"i\");");
		assertNoErrors();
	}
	
	public void testEnumMangle2() throws Exception {
		one("enum Bar : char { a }");
		two("static assert(Bar.mangleof == \"a\");");
		assertNoErrors();
	}
	
	public void testClassInheritanceNotOk() throws Exception {
		one("class Bar { } class Foo { }");
		two("void x(Foo f) { Bar b = f; }");
		assertErrors();
	}
	
	public void testClassInheritanceOk() throws Exception {
		one("class Bar { } class Foo : Bar { }");
		two("void x(Foo f) { Bar b = f; }");
		assertNoErrors();
	}
	
	public void testInterfaceInheritanceOk() throws Exception {
		one("interface Bar { } class Foo : Bar { }");
		two("void x(Foo f) { Bar b = f; }");
		assertNoErrors();
	}
	
	public void testInterfaceInheritance2Ok() throws Exception {
		one("interface Bar { } interface Foo : Bar { }");
		two("void x(Foo f) { Bar b = f; }");
		assertNoErrors();
	}
	
	public void testEnumValueNotOk() throws Exception {
		one("enum Bar { a, b, c }");
		two("void x(Bar f) { f = Bar.d; }");
		assertErrors();
	}
	
	public void testEnumValueOk() throws Exception {
		one("enum Bar { a, b, c }");
		two("void x(Bar f) { f = Bar.a; }");
		assertNoErrors();
	}
	
	public void testEnumValueCT() throws Exception {
		one("enum Bar { a, b = 3, c }");
		two("static assert(Bar.b == 3);");
		assertNoErrors();
	}
	
	public void testEnumValue2() throws Exception {
		one("enum Bar { a, b, c }");
		two("static assert(Bar.b == 1);");
		assertNoErrors();
	}
	
	public void testFuncNotFound() throws Exception {
		one("");
		two("void foo() { bar(); }");
		assertErrors();
	}
	
	public void testFuncMatchEmpty() throws Exception {
		one("void bar() { }");
		two("void foo() { bar(); }");
		assertNoErrors();
	}
	
	public void testFuncMatchBasic() throws Exception {
		one("void bar(int x) { }");
		two("void foo() { bar(1); }");
		assertNoErrors();
	}
	
	public void testFuncMatchDynamicArray() throws Exception {
		one("void bar(int[] x) { }");
		two("void foo() { int[] x; bar(x); }");
		assertNoErrors();
	}
	
	public void testFuncMatchPointer() throws Exception {
		one("void bar(int* x) { }");
		two("void foo() { int* x; bar(x); }");
		assertNoErrors();
	}
	
	public void testFuncMatchFunction() throws Exception {
		one("void bar(int function(int) x) { }");
		two("void foo() { int function(int) x; bar(x); }");
		assertNoErrors();
	}
	
	public void testFuncOverloadFound2() throws Exception {
		one("void bar(int x) { } void bar(char[] x) { }");
		two("void foo() { bar(\"hey\"); }");
		assertNoErrors();
	}
	
	public void testClassMemberNotAccessible() throws Exception {
		one("class Bar { private int x; }");
		two("void foo(Bar b) { b.x = 2; }");
		assertErrors();
	}
	
	public void testStructMemberNotAccessible() throws Exception {
		one("struct Bar { private int x; }");
		two("void foo(Bar b) { b.x = 2; }");
		assertErrors();
	}
	
	public void testDeprecatedClass() throws Exception {
		one("deprecated class Bar { }");
		two("Bar b;");
		assertErrors();
	}
	
	public void testConstructor() throws Exception {
		one("class Bar { }");
		two("void foo() { Bar b = new Bar(); }");
		assertNoErrors();
	}
	
	public void testConstructor2() throws Exception {
		one("class Bar { this() { } }");
		two("void foo() { Bar b = new Bar(); }");
		assertNoErrors();
	}
	
	public void testConstructor3() throws Exception {
		one("class Bar { this(int x) { } }");
		two("void foo() { Bar b = new Bar(1); }");
		assertNoErrors();
	}
	
	public void testConstructorOverload() throws Exception {
		one("class Bar { this() { } this(int x) { } }");
		two("void foo() { Bar b = new Bar(1); }");
		assertNoErrors();
	}
	
	public void testAlias() throws Exception {
		one("alias int myInt;");
		two("myInt i;");
		assertNoErrors();
	}
	
	public void testAlias2() throws Exception {
		one("alias int myInt; void foo(myInt m) { }");
		two("void bar() { myInt i; foo(i); }");
		assertNoErrors();
	}
	
	public void testConstVar() throws Exception {
		one("const int i = 1;");
		two("static assert(i == 1);");
		assertNoErrors();
	}
	
	public void testSizeof1() throws Exception {
		one("struct s { }");
		two("static assert(s.sizeof == 1);");
		assertNoErrors();
	}
	
	public void testSizeof2() throws Exception {
		one("struct s { int x; }");
		two("static assert(s.sizeof == 4);");
		assertNoErrors();
	}
	
	public void testAlignof1() throws Exception {
		one("struct s { }");
		two("static assert(s.alignof == 1);");
		assertNoErrors();
	}
	
	public void testAlignof2() throws Exception {
		one("struct s { int x; }");
		two("static assert(s.alignof == 4);");
		assertNoErrors();
	}
	
	public void testAlignof3() throws Exception {
		one("align(2) { struct s { int x; } }");
		two("static assert(s.alignof == 2);");
		assertNoErrors();
	}
	
	public void testDstress_alias_42_B() throws Exception {
		one("struct S { int i; }" + 
			"" + 
			"template Alias(alias A){" + 
			"	alias A Alias;" + 
			"}" + 
			"" + 
			"Alias!(S) x;");
		two("static assert(is(typeof(x) == S));");
		assertNoErrors();
	}
	
	public void testCTFE() throws Exception {
		one("int fact(int x) { if (x == 0) return 1; else return x * fact(x - 1); }");
		two("static assert(fact(4) == 24);");
		assertNoErrors();
	}
	
	public void testCTFE2() throws Exception {
		one("alias int myInt; myInt fact(myInt x) { if (x == 0) return 1; else return x * fact(x - 1); }");
		two("static assert(fact(4) == 24);");
		assertNoErrors();
	}
	
	public void testCTFE3() throws Exception {
		three("alias int myInt;");
		one("import three; myInt fact(myInt x) { if (x == 0) return 1; else return x * fact(x - 1); }");
		two("static assert(fact(4) == 24);");
		assertNoErrors();
	}
	
	public void testClassOverloadMethodsInSuperMustUseAlias1() throws Exception {
		one("class Base { void foo(int x); } class Child : Base { void foo(); }");
		two("void foo(Child c) { c.foo(1); }");
		assertErrors();
	}
	
	public void testClassOverloadMethodsInSuperMustUseAlias2() throws Exception {
		one("class Base { void foo(int x); } class Child : Base { void foo(); }");
		two("void foo(Child c) { alias Base.foo foo; c.foo(1); }");
		assertErrors();
	}
	
	public void testHangBug() throws Exception {
		one("");
		two("string[] foo() { return [\"one\", \"two\"]; } const string[] x = foo();");
		assertNoErrors();
	}
	
	public void testHangBug2() throws Exception {
		one("");
		two("void foo() { return [\"one\"; }");
		assertErrors();
	}
	
	public void testNestedPackagesProblem1() throws Exception {
		one("");
		createCompilationUnit("first", "file.d", "");
		createCompilationUnit("first.second", "file.d", "class Foo { int x; } alias Foo FOO;");
		two("import first.second.file; void foo(FOO f) { f.x = 2; }");
		assertNoErrors();
	}
	
	public void testStaticIfHides() throws Exception {
		one("static if(false) { class Foo { } }");
		two("Foo foo;");
		assertErrors();
	}
	
	public void testStaticIfShows() throws Exception {
		one("static if(true) { class Foo { } }");
		two("Foo foo;");
		assertNoErrors();
	}
	
	public void testStaticIfElseHides() throws Exception {
		one("static if(true) { } else { class Foo { } }");
		two("Foo foo;");
		assertErrors();
	}
	
	public void testStaticIfElseShows() throws Exception {
		one("static if(false) { } else { class Foo { } }");
		two("Foo foo;");
		assertNoErrors();
	}
	
	public void testVersionHides() throws Exception {
		one("version(Linux) { class Foo { } }");
		two("Foo foo;");
		assertErrors();
	}
	
	public void testVersionShows() throws Exception {
		one("version(Windows) { class Foo { } }");
		two("Foo foo;");
		assertNoErrors();
	}
	
	public void testVersionElseHides() throws Exception {
		one("version(Windows) { } else { class Foo { } }");
		two("Foo foo;");
		assertErrors();
	}
	
	public void testVersionElseShows() throws Exception {
		one("version(Linux) { } else { class Foo { } }");
		two("Foo foo;");
		assertNoErrors();
	}
	
	public void testDebugHides() throws Exception {
		one("debug { class Foo { } }");
		two("Foo foo;");
		assertErrors();
	}
	
	public void testDebugElseShows() throws Exception {
		one("debug { } else { class Foo { } }");
		two("Foo foo;");
		assertNoErrors();
	}
	
	public void testTemplate() throws Exception {
		one("template Temp() { const int Temp = 3; }");
		two("mixin Temp!();");
		assertNoErrors();
	}
	
	public void testMixin() throws Exception {
		one("mixin(\"class Bar { }\")");
		two("Bar bar;");
		assertNoErrors();
	}
	
	public void testTemplateOverload() throws Exception {
		one("template Temp(T) { const int Temp = 3; } template Temp() { const int Temp = 3; }");
		two("mixin Temp!();");
		assertNoErrors();
	}
	
	public void testTemplatedClass() throws Exception {
		one("class Bar(T) { }");
		two("Bar!(int) x;");
		assertNoErrors();
	}

	public void testTemplatedClass2() throws Exception {
		one("class Bar(int T) { } class Bar(float T) { }");
		two("Bar!(1) x; Bar!(1.3) y;");
		assertNoErrors();
	}
	
	public void testTemplatedFunction() throws Exception {
		one("void foo(int T)() { }");
		two("void main() { foo!(1); }");
		assertNoErrors();
	}
	
	public void testTemplatedFunction2() throws Exception {
		one("void foo(int T)() { } void foo(float T)() { }");
		two("void main() { foo!(1); foo!(1.3); }");
		assertNoErrors();
	}
	
	public void testProblemMultipleDefined() throws Exception {
		one("");
		two("import std.stdio; import std.c.stdio;");
		assertNoErrors();
	}
	
	public void testPublicImport() throws Exception {
		createCompilationUnit("another.d", "int x;");
		one("public import another;");
		two("void foo() { x = 3; }");
		assertNoErrors();
	}
	
	public void testPublicImport2() throws Exception {
		createCompilationUnit("another.d", "int x;");
		one("public { import another; }");
		two("void foo() { x = 3; }");
		assertNoErrors();
	}
	
	public void testPrivateImportBug() throws Exception {
		createCompilationUnit("another.d", "int x;");
		one("import another;");
		two("void foo() { another.x = 3; }");
		assertNoErrors();
	}
	
	public void testAsmMovEaxVoidsNoReturnError() throws Exception {
		one("");
		two("int foo() { asm { mov EAX, 1; } }");
		assertNoErrors();
	}
	
	public void testFunctionDefaultValue() throws Exception {
		one("void foo(int x, int y = 1) { }");
		two("void bar() { foo(1); }");
		assertNoErrors();
	}
	
	public void testForeachBug() throws Exception {
		one("");
		two("bool inPattern(dchar c, char[] pattern) {\r\n" + 
				"	foreach(dchar p; pattern) {\r\n" + 
				"		if(true) {\r\n" + 
				"			return true;\r\n" + 
				"		} else if(false) {\r\n" + 
				"			return false;\r\n" + 
				"		}\r\n" + 
				"	}\r\n" + 
				"	return false;\r\n" + 
				"}");
		assertNoErrors();
	}
	
	public void testEnumTypeBug() throws Exception {
		one("");
		two("import std.system;\r\n" + 
				"\r\n" + 
				"void foo() {\r\n" + 
				"	Endian e = std.system.endian;\r\n" + 
				"}");
		assertNoErrors();
	}
	
	public void testAmbigousDefinitionOfMallocBug() throws Exception {
		one("");
		two("import std.stdio;\r\n" + 
			"import std.ctype;\r\n" + 
			"import std.string;\r\n" + 
			"import std.c.string;\r\n" + 
			"import std.c.stdlib;\r\n" + 
			"\r\n" + 
			"static void *trace_malloc(size_t nbytes)\r\n" + 
			"{   void *p;\r\n" + 
			"\r\n" + 
			"    p = malloc(nbytes);\r\n" + 
			"    if (!p)\r\n" + 
			"	exit(EXIT_FAILURE);\r\n" + 
			"    return p;\r\n" + 
			"}");
		assertNoErrors();
	}
	
	public void testForwardingVariadicArguments() throws Exception {
		one("");
		two("void foo(int[] arguments ...) {\r\n" + 
			"	\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"void bar(int[] arguments ...){\r\n" + 
			"	foo(arguments);\r\n" + 
			"}");
		assertNoErrors();
	}

}
