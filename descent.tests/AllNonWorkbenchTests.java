import junit.framework.Test;
import junit.framework.TestSuite;
import descent.tests.ddoc.DdocParserTests;
import descent.tests.debugger.DdbgDebuggerTests;
import descent.tests.format.FormatAliasDeclaration_Test;
import descent.tests.format.FormatAlignDeclaration_Test;
import descent.tests.format.FormatCStyle_Test;
import descent.tests.format.FormatClassDeclaration_Test;
import descent.tests.format.FormatComment_Test;
import descent.tests.format.FormatConditionalDeclaration_Test;
import descent.tests.format.FormatDebugStatement_Test;
import descent.tests.format.FormatDoStatement_Test;
import descent.tests.format.FormatEnumDeclaration_Test;
import descent.tests.format.FormatForStatement_Test;
import descent.tests.format.FormatForeachStatement_Test;
import descent.tests.format.FormatFunctionDeclaration_Tests;
import descent.tests.format.FormatFunctionLiteralStatement_Test;
import descent.tests.format.FormatIfStatement_Test;
import descent.tests.format.FormatImportDeclaration_Tests;
import descent.tests.format.FormatInterfaceDeclaration_Test;
import descent.tests.format.FormatModuleDeclaration_Tests;
import descent.tests.format.FormatNewAnonymousClassStatement_Test;
import descent.tests.format.FormatPragmaDeclaration_Test;
import descent.tests.format.FormatScopeStatement_Test;
import descent.tests.format.FormatStress_Test;
import descent.tests.format.FormatStructDeclaration_Test;
import descent.tests.format.FormatSwitchStatement_Test;
import descent.tests.format.FormatSynchronizedStatement_Test;
import descent.tests.format.FormatTemplateDeclaration_Test;
import descent.tests.format.FormatTryStatement_Test;
import descent.tests.format.FormatTypedefDeclaration_Test;
import descent.tests.format.FormatUnionDeclaration_Test;
import descent.tests.format.FormatVariableDeclaration_Test;
import descent.tests.format.FormatVersionStatement_Test;
import descent.tests.format.FormatWhileStatement_Test;
import descent.tests.format.FormatWhitespaceDeclarations_Test;
import descent.tests.format.FormatWhitespaceStatements_Test;
import descent.tests.format.FormatWithStatement_Test;
import descent.tests.mangling.Demangler_Test;
import descent.tests.mangling.SignatureParameterCount_Test;
import descent.tests.mangling.SignatureParameterTypes_Test;
import descent.tests.mangling.SignatureProcessor_Test;
import descent.tests.mangling.SignatureReturnType_Test;
import descent.tests.mangling.SignatureTemplateParameterCount_Test;
import descent.tests.mangling.SignatureToCharArray_Test;
import descent.tests.mangling.SignatureToType_Test;
import descent.tests.mangling.Signature_Test;
import descent.tests.mars.ASTConvertion_Test;
import descent.tests.mars.Alias_Test;
import descent.tests.mars.Align_Test;
import descent.tests.mars.Bugs_Test;
import descent.tests.mars.Class_Test;
import descent.tests.mars.Comment_Test;
import descent.tests.mars.CompilationUnit_Test;
import descent.tests.mars.Condition_Test;
import descent.tests.mars.Enum_Test;
import descent.tests.mars.Expression_Test;
import descent.tests.mars.ExtendedSourceRange_Test;
import descent.tests.mars.Function_Test;
import descent.tests.mars.Import_Test;
import descent.tests.mars.Initializer_Test;
import descent.tests.mars.Interface_Test;
import descent.tests.mars.Invariant_Test;
import descent.tests.mars.LexerReplacements_Test;
import descent.tests.mars.Lexer_Test;
import descent.tests.mars.Link_Test;
import descent.tests.mars.MixinAndTemplateMiixn_Test;
import descent.tests.mars.Modifier_Test;
import descent.tests.mars.Pragma_Test;
import descent.tests.mars.Problems_Test;
import descent.tests.mars.Recovery_Tests;
import descent.tests.mars.ScannerTests;
import descent.tests.mars.SourceElementParserTest;
import descent.tests.mars.Statement_Test;
import descent.tests.mars.Struct_Test;
import descent.tests.mars.Template_Test;
import descent.tests.mars.Type_Test;
import descent.tests.mars.Typedef_Test;
import descent.tests.mars.Union_Test;
import descent.tests.mars.UnitTest_Test;
import descent.tests.mars.VariableDeclaration_Test;
import descent.tests.mars.integer_t_Test;
import descent.tests.rewrite.RewriteAggregateDeclarationTest;
import descent.tests.rewrite.RewriteAliasDeclarationTest;
import descent.tests.rewrite.RewriteAlignDeclarationTest;
import descent.tests.rewrite.RewriteArgumentTest;
import descent.tests.rewrite.RewriteBaseClassTest;
import descent.tests.rewrite.RewriteDDocCommentTest;
import descent.tests.rewrite.RewriteDebugAssignmentTest;
import descent.tests.rewrite.RewriteDebugDeclarationTest;
import descent.tests.rewrite.RewriteEnumDeclarationTest;
import descent.tests.rewrite.RewriteExpressionTest;
import descent.tests.rewrite.RewriteExternDeclarationTest;
import descent.tests.rewrite.RewriteFunctionDeclarationTest;
import descent.tests.rewrite.RewriteImportDeclarationTest;
import descent.tests.rewrite.RewriteInitializerTest;
import descent.tests.rewrite.RewriteMixinDeclarationTest;
import descent.tests.rewrite.RewriteModuleDeclarationTest;
import descent.tests.rewrite.RewriteStatementTest;
import descent.tests.rewrite.RewriteStaticIfDeclarationTest;
import descent.tests.rewrite.RewriteTemplateParameterTest;
import descent.tests.rewrite.RewriteTypeTest;
import descent.tests.trace.Trace_Test;

/*
 * Here are listed all tests that doesn't require a workbench and run relatively
 * fast.
 * 
 * Right click on this file and select: Run as... -> JUnit test
 */
public class AllNonWorkbenchTests {
	
	private final static int DDOC = 1;
	private final static int DEBUGGER = 2;
	private final static int FORMATTER = 4;
	private final static int LEXER_PARSER = 8;
	private final static int SIGNATURE = 16;
	private final static int REWRITE = 32;
	private final static int TRACE = 64;
	
	/*
	 * Comment a line to disable testing a particular feature.
	 */
	private final static int enabled = 0
//					| DDOC 
//					| DEBUGGER 
//					| FORMATTER 
					| LEXER_PARSER 
//					| SIGNATURE 
//					| REWRITE 
//					| TRACE
					;
	
	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test which doesn't require a workbench");
		
		if (isEnabled(DDOC)) {
			suite.addTestSuite(DdocParserTests.class);
		}
		
		if (isEnabled(DEBUGGER)) {
			suite.addTestSuite(DdbgDebuggerTests.class);
		}
		
		if (isEnabled(FORMATTER)) {
			suite.addTestSuite(FormatAliasDeclaration_Test.class);
			suite.addTestSuite(FormatAlignDeclaration_Test.class);
			suite.addTestSuite(FormatClassDeclaration_Test.class);
			suite.addTestSuite(FormatComment_Test.class);
			suite.addTestSuite(FormatConditionalDeclaration_Test.class);
			suite.addTestSuite(FormatCStyle_Test.class);
			suite.addTestSuite(FormatDebugStatement_Test.class);
			suite.addTestSuite(FormatDoStatement_Test.class);
			suite.addTestSuite(FormatEnumDeclaration_Test.class);
			suite.addTestSuite(FormatForeachStatement_Test.class);
			suite.addTestSuite(FormatForStatement_Test.class);
			suite.addTestSuite(FormatFunctionDeclaration_Tests.class);
			suite.addTestSuite(FormatFunctionLiteralStatement_Test.class);
			suite.addTestSuite(FormatIfStatement_Test.class);
			suite.addTestSuite(FormatImportDeclaration_Tests.class);
			suite.addTestSuite(FormatInterfaceDeclaration_Test.class);
//			suite.addTestSuite(FormatModifierDeclaration_Test.class);
			suite.addTestSuite(FormatModuleDeclaration_Tests.class);
			suite.addTestSuite(FormatNewAnonymousClassStatement_Test.class);
			suite.addTestSuite(FormatPragmaDeclaration_Test.class);
			suite.addTestSuite(FormatScopeStatement_Test.class);
			suite.addTestSuite(FormatStress_Test.class);
			suite.addTestSuite(FormatStructDeclaration_Test.class);
			suite.addTestSuite(FormatSwitchStatement_Test.class);
			suite.addTestSuite(FormatSynchronizedStatement_Test.class);
			suite.addTestSuite(FormatTemplateDeclaration_Test.class);
			suite.addTestSuite(FormatTryStatement_Test.class);
			suite.addTestSuite(FormatTypedefDeclaration_Test.class);
			suite.addTestSuite(FormatUnionDeclaration_Test.class);
			suite.addTestSuite(FormatVariableDeclaration_Test.class);
			suite.addTestSuite(FormatVersionStatement_Test.class);
			suite.addTestSuite(FormatWhileStatement_Test.class);
			suite.addTestSuite(FormatWhitespaceDeclarations_Test.class);
			suite.addTestSuite(FormatWhitespaceStatements_Test.class);
			suite.addTestSuite(FormatWithStatement_Test.class);
		}
		
		if (isEnabled(LEXER_PARSER)) {
			suite.addTestSuite(Alias_Test.class);
			suite.addTestSuite(Align_Test.class);
			suite.addTestSuite(ASTConvertion_Test.class);
			suite.addTestSuite(Bugs_Test.class);
			suite.addTestSuite(Class_Test.class);
			suite.addTestSuite(Comment_Test.class);
			suite.addTestSuite(CompilationUnit_Test.class);
			suite.addTestSuite(Condition_Test.class);
			suite.addTestSuite(Enum_Test.class);
			suite.addTestSuite(Expression_Test.class);
			suite.addTestSuite(ExtendedSourceRange_Test.class);
			suite.addTestSuite(Function_Test.class);
			suite.addTestSuite(Import_Test.class);
			suite.addTestSuite(Initializer_Test.class);
			suite.addTestSuite(integer_t_Test.class);
			suite.addTestSuite(Interface_Test.class);
			suite.addTestSuite(Invariant_Test.class);
			suite.addTestSuite(Lexer_Test.class);
			suite.addTestSuite(LexerReplacements_Test.class);
			suite.addTestSuite(Link_Test.class);
			suite.addTestSuite(MixinAndTemplateMiixn_Test.class);
			suite.addTestSuite(Modifier_Test.class);
			suite.addTestSuite(Pragma_Test.class);
			suite.addTestSuite(Problems_Test.class);
			suite.addTestSuite(Recovery_Tests.class);
			suite.addTestSuite(ScannerTests.class);
//			suite.addTestSuite(Semantic1_Test.class);
			suite.addTestSuite(SourceElementParserTest.class);
			suite.addTestSuite(Statement_Test.class);
			suite.addTestSuite(Struct_Test.class);
			suite.addTestSuite(Template_Test.class);
			suite.addTestSuite(Type_Test.class);
			suite.addTestSuite(Typedef_Test.class);
			suite.addTestSuite(Union_Test.class);
			suite.addTestSuite(UnitTest_Test.class);
			suite.addTestSuite(VariableDeclaration_Test.class);
		}
		
		if (isEnabled(SIGNATURE)) {
			suite.addTestSuite(Demangler_Test.class);
			suite.addTestSuite(Signature_Test.class);
			suite.addTestSuite(SignatureParameterCount_Test.class);
			suite.addTestSuite(SignatureTemplateParameterCount_Test.class);
			suite.addTestSuite(SignatureToCharArray_Test.class);
			suite.addTestSuite(SignatureReturnType_Test.class);
			suite.addTestSuite(SignatureParameterTypes_Test.class);
			suite.addTestSuite(SignatureProcessor_Test.class);
			suite.addTestSuite(SignatureToType_Test.class);
		}
		
		if (isEnabled(REWRITE)) {
			suite.addTestSuite(RewriteAggregateDeclarationTest.class);
			suite.addTestSuite(RewriteAliasDeclarationTest.class);
			suite.addTestSuite(RewriteAlignDeclarationTest.class);
			suite.addTestSuite(RewriteArgumentTest.class);
			suite.addTestSuite(RewriteBaseClassTest.class);
			suite.addTestSuite(RewriteDDocCommentTest.class);
			suite.addTestSuite(RewriteDebugAssignmentTest.class);		
			suite.addTestSuite(RewriteDebugDeclarationTest.class);
			suite.addTestSuite(RewriteEnumDeclarationTest.class);
			suite.addTestSuite(RewriteExpressionTest.class);
			suite.addTestSuite(RewriteExternDeclarationTest.class);
			suite.addTestSuite(RewriteFunctionDeclarationTest.class);
			suite.addTestSuite(RewriteImportDeclarationTest.class);
			suite.addTestSuite(RewriteInitializerTest.class);
			suite.addTestSuite(RewriteMixinDeclarationTest.class);
			suite.addTestSuite(RewriteModuleDeclarationTest.class);
			suite.addTestSuite(RewriteStatementTest.class);
			suite.addTestSuite(RewriteStaticIfDeclarationTest.class);		
			suite.addTestSuite(RewriteTemplateParameterTest.class);
			suite.addTestSuite(RewriteTypeTest.class);
		}
		
		if (isEnabled(TRACE)) {
			suite.addTestSuite(Trace_Test.class);
		}
		
		return suite;
	}

	private static boolean isEnabled(int num) {
		return (enabled & num) != 0;
	}

}
