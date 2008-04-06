package descent.tests.assist;

import descent.core.CompletionProposal;

public class KeywordProposal_Test extends AbstractCompletionTest {
	
	public void testEmpty() throws Exception {
		String s = "";
		
		assertCompletions(null, "test.d", s, 0, CompletionProposal.KEYWORD,
				"abstract", 0, 0,
				"alias", 0, 0,
				"align", 0, 0,
				"auto", 0, 0,
				"bool", 0, 0,
				"byte", 0, 0,
				"cdouble", 0, 0,
				"cfloat", 0, 0,
				"char", 0, 0,
				"class", 0, 0,
				"const", 0, 0,
				"creal", 0, 0,
				"dchar", 0, 0,
				"debug", 0, 0,
				"delete", 0, 0,
				"deprecated", 0, 0,
				"double", 0, 0,
				"enum", 0, 0,
				"export", 0, 0,
				"extern", 0, 0,
				"final", 0, 0,
				"float", 0, 0,
				"idouble", 0, 0,
				"ifloat", 0, 0,
				"import", 0, 0,
				"int", 0, 0,
				"interface", 0, 0,
				"invariant", 0, 0,
				"ireal", 0, 0,
				"long", 0, 0,
				"mixin", 0, 0,
				"module", 0, 0,
				"new", 0, 0,
				"override", 0, 0,
				"package", 0, 0,
				"pragma", 0, 0,
				"private", 0, 0,
				"protected", 0, 0,
				"public", 0, 0,
				"real", 0, 0,
				"scope", 0, 0,
				"short", 0, 0,
				"static", 0, 0,
				"struct", 0, 0,
				"synchronized", 0, 0,
				"template", 0, 0,
				"this", 0, 0,
				"typedef", 0, 0,
				"typeof", 0, 0,
				"ubyte", 0, 0,
				"uint", 0, 0,
				"ulong", 0, 0,
				"union", 0, 0,
				"unittest", 0, 0,
				"ushort", 0, 0,
				"version", 0, 0,
				"void", 0, 0,
				"wchar", 0, 0
				);
	}
	
	public void testAfterModule() throws Exception {
		String s = "module ";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.KEYWORD);
	}
	
	public void testAfterImport() throws Exception {
		String s = "import ";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.KEYWORD);
	}
	
	public void testAlmostEmpty() throws Exception {
		String s = "m";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.KEYWORD,
				"mixin", 0, s.length(),
				"module", 0, s.length()
				);
	}
	
	public void testExtern() throws Exception {
		String s = "extern()";
		
		assertCompletions(null, "test.d", s, 7, CompletionProposal.KEYWORD,
				"C", 7, 7,
				"C++", 7, 7,
				"D", 7, 7,
				"Pascal", 7, 7,
				"System", 7, 7,
				"Windows", 7, 7
				);
	}
	
	public void testExtern2() throws Exception {
		String s = "extern(S)";
		
		assertCompletions(null, "test.d", s, 8, CompletionProposal.KEYWORD,
				"System", 7, 8
				);
	}
	
	public void testBoolVar() throws Exception {
		String s = "bool x = ";
		
		assertCompletions(null, "test.d", s, s.length(), CompletionProposal.KEYWORD,
				"false", s.length(), s.length(),
				"true", s.length(), s.length()
				);
	}
	
	public void testFunctionBodyInOut() throws Exception {
		String s = "void foo()  { }";
		
		assertCompletions(null, "test.d", s, 11, CompletionProposal.KEYWORD,
				"body", 11, 11,
				"in", 11, 11,
				"out", 11, 11
				);
	}
	
	public void testFunctionBodyInOut2() throws Exception {
		String s = "void foo() ";
		
		assertCompletions(null, "test.d", s, 11, CompletionProposal.KEYWORD,
				"body", 11, 11,
				"in", 11, 11,
				"out", 11, 11
				);
	}

}
