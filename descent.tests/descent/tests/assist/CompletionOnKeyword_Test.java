package descent.tests.assist;

import descent.core.CompletionProposal;

public class CompletionOnKeyword_Test extends AbstractCompletionTest {
	
	public void testEmpty() throws Exception {
		assertCompletions(null, "test.d", "", 0,
				CompletionProposal.KEYWORD, "abstract", 0, 0,
				CompletionProposal.KEYWORD, "alias", 0, 0,
				CompletionProposal.KEYWORD, "align", 0, 0,
				CompletionProposal.KEYWORD, "auto", 0, 0,
				CompletionProposal.KEYWORD, "bool", 0, 0,
				CompletionProposal.KEYWORD, "byte", 0, 0,
				CompletionProposal.KEYWORD, "cdouble", 0, 0,
				CompletionProposal.KEYWORD, "cfloat", 0, 0,
				CompletionProposal.KEYWORD, "char", 0, 0,
				CompletionProposal.KEYWORD, "class", 0, 0,
				CompletionProposal.KEYWORD, "const", 0, 0,
				CompletionProposal.KEYWORD, "creal", 0, 0,
				CompletionProposal.KEYWORD, "dchar", 0, 0,
				CompletionProposal.KEYWORD, "debug", 0, 0,
				CompletionProposal.KEYWORD, "delete", 0, 0,
				CompletionProposal.KEYWORD, "deprecated", 0, 0,
				CompletionProposal.KEYWORD, "double", 0, 0,
				CompletionProposal.KEYWORD, "enum", 0, 0,
				CompletionProposal.KEYWORD, "export", 0, 0,
				CompletionProposal.KEYWORD, "extern", 0, 0,
				CompletionProposal.KEYWORD, "final", 0, 0,
				CompletionProposal.KEYWORD, "float", 0, 0,
				CompletionProposal.KEYWORD, "idouble", 0, 0,
				CompletionProposal.KEYWORD, "ifloat", 0, 0,
				CompletionProposal.KEYWORD, "import", 0, 0,
				CompletionProposal.KEYWORD, "int", 0, 0,
				CompletionProposal.KEYWORD, "interface", 0, 0,
				CompletionProposal.KEYWORD, "invariant", 0, 0,
				CompletionProposal.KEYWORD, "ireal", 0, 0,
				CompletionProposal.KEYWORD, "long", 0, 0,
				CompletionProposal.KEYWORD, "mixin", 0, 0,
				CompletionProposal.KEYWORD, "module", 0, 0,
				CompletionProposal.KEYWORD, "new", 0, 0,
				CompletionProposal.KEYWORD, "override", 0, 0,
				CompletionProposal.KEYWORD, "package", 0, 0,
				CompletionProposal.KEYWORD, "pragma", 0, 0,
				CompletionProposal.KEYWORD, "private", 0, 0,
				CompletionProposal.KEYWORD, "protected", 0, 0,
				CompletionProposal.KEYWORD, "public", 0, 0,
				CompletionProposal.KEYWORD, "real", 0, 0,
				CompletionProposal.KEYWORD, "scope", 0, 0,
				CompletionProposal.KEYWORD, "short", 0, 0,
				CompletionProposal.KEYWORD, "static", 0, 0,
				CompletionProposal.KEYWORD, "struct", 0, 0,
				CompletionProposal.KEYWORD, "synchronized", 0, 0,
				CompletionProposal.KEYWORD, "template", 0, 0,
				CompletionProposal.KEYWORD, "this", 0, 0,
				CompletionProposal.KEYWORD, "typedef", 0, 0,
				CompletionProposal.KEYWORD, "typeof", 0, 0,
				CompletionProposal.KEYWORD, "ubyte", 0, 0,
				CompletionProposal.KEYWORD, "uint", 0, 0,
				CompletionProposal.KEYWORD, "ulong", 0, 0,
				CompletionProposal.KEYWORD, "union", 0, 0,
				CompletionProposal.KEYWORD, "unittest", 0, 0,
				CompletionProposal.KEYWORD, "ushort", 0, 0,
				CompletionProposal.KEYWORD, "version", 0, 0,
				CompletionProposal.KEYWORD, "void", 0, 0,
				CompletionProposal.KEYWORD, "wchar", 0, 0
				);
	}
	
	public void testAlmostEmpty() throws Exception {
		assertCompletions(null, "test.d", "m", 1, 
				CompletionProposal.KEYWORD, "mixin", 0, 1,
				CompletionProposal.KEYWORD, "module", 0, 1
				);
	}
	
	public void testExtern() throws Exception {
		assertCompletions(null, "test.d", "extern()", 7, 
				CompletionProposal.KEYWORD, "C", 7, 7,
				CompletionProposal.KEYWORD, "C++", 7, 7,
				CompletionProposal.KEYWORD, "D", 7, 7,
				CompletionProposal.KEYWORD, "Pascal", 7, 7,
				CompletionProposal.KEYWORD, "System", 7, 7,
				CompletionProposal.KEYWORD, "Windows", 7, 7
				);
	}
	
	public void testExtern2() throws Exception {
		assertCompletions(null, "test.d", "extern(S)", 8, 
				CompletionProposal.KEYWORD, "System", 7, 8
				);
	}
	
	public void testFunctionBodyInOut() throws Exception {
		assertCompletions(null, "test.d", "void foo()  { }", 11, 
				CompletionProposal.KEYWORD, "body", 11, 11,
				CompletionProposal.KEYWORD, "in", 11, 11,
				CompletionProposal.KEYWORD, "out", 11, 11
				);
	}
	
	public void testFunctionBodyInOut2() throws Exception {
		assertCompletions(null, "test.d", "void foo() ", 11, 
				CompletionProposal.KEYWORD, "body", 11, 11,
				CompletionProposal.KEYWORD, "in", 11, 11,
				CompletionProposal.KEYWORD, "out", 11, 11
				);
	}

}
