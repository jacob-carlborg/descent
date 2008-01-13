package descent.tests.lookup;

public class LookupPhobos_Test extends AbstractLookupTest {
	
	public void testProblemMultipleDefined2() throws Exception {
		one("");
		two("import std.c.stdio;\r\n" + 
				"import std.stdio;\r\n" + 
				"\r\n" + 
				"int main(char[][] args)\r\n" + 
				"{\r\n" + 
				"    writefln(\"hola\");\r\n" + 
				"    printf(\"Success!\");\r\n" + 
				"    return 0;\r\n" + 
				"}");
		assertNoErrors();
	}
	
	public void testOpAndBitArray() throws Exception {
		one("");
		two("import std.bitarray;\r\n" + 
				"\r\n" + 
				"void foo() {\r\n" + 
				"	BitArray a;\r\n" + 
				"	BitArray b;\r\n" + 
				"	a &= b;\r\n" + 
				"}");
		assertNoErrors();
	}
	
	public void testOverrideCovariant() throws Exception {
		one("class Foo {\r\n" + 
				"	int compare(int* x) {\r\n" + 
				"		return 0;\r\n" + 
				"	}\r\n" + 
				"}");
		two("class Bar : Foo {\r\n" + 
				"	int compare(int* x) {\r\n" + 
				"		return 0;\r\n" + 
				"	}\r\n" + 
				"}");
		assertNoErrors();
	}
	
	public void testAnonymousEnumInInterface() throws Exception {
		one("interface IFoo {\r\n" + 
				"	enum { Eof = uint.max }\r\n" + 
				"}");
		two("class Foo : IFoo {\r\n" + 
				"	void foo() {\r\n" + 
				"		int i = 0;\r\n" + 
				"		if (i == Eof) {\r\n" + 
				"			\r\n" + 
				"		}\r\n" + 
				"	}\r\n" + 
				"}\r\n" + 
				"");
		assertNoErrors();
	}
	
	public void testProblemSignatureToType() throws Exception {
		one("interface IReader {\r\n" + 
				"	alias void delegate (IReader) Closure;\r\n" + 
				"}");
		two("class Reader : IReader {\r\n" + 
				"	final IReader get (IReader.Closure dg) \r\n" + 
				"	{\r\n" + 
				"	        dg (this);\r\n" + 
				"	        return this;\r\n" + 
				"	}\r\n" + 
				"}");
		assertNoErrors();
	}

}
