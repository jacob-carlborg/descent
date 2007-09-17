package descent.tests.mars;

import java.util.List;

import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.ASTParser;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.AliasDeclaration;
import descent.core.dom.AliasDeclarationFragment;
import descent.core.dom.Argument;
import descent.core.dom.AsmBlock;
import descent.core.dom.AsmStatement;
import descent.core.dom.Block;
import descent.core.dom.BooleanLiteral;
import descent.core.dom.BreakStatement;
import descent.core.dom.CallExpression;
import descent.core.dom.CatchClause;
import descent.core.dom.CompilationUnit;
import descent.core.dom.ContinueStatement;
import descent.core.dom.DebugStatement;
import descent.core.dom.DeclarationStatement;
import descent.core.dom.DefaultStatement;
import descent.core.dom.DoStatement;
import descent.core.dom.DotIdentifierExpression;
import descent.core.dom.EnumDeclaration;
import descent.core.dom.ExpressionStatement;
import descent.core.dom.ForStatement;
import descent.core.dom.ForeachRangeStatement;
import descent.core.dom.ForeachStatement;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.GotoCaseStatement;
import descent.core.dom.GotoStatement;
import descent.core.dom.IfStatement;
import descent.core.dom.LabeledStatement;
import descent.core.dom.MixinDeclaration;
import descent.core.dom.Modifier;
import descent.core.dom.NumberLiteral;
import descent.core.dom.PragmaStatement;
import descent.core.dom.PrimitiveType;
import descent.core.dom.ReturnStatement;
import descent.core.dom.ScopeStatement;
import descent.core.dom.SimpleName;
import descent.core.dom.Statement;
import descent.core.dom.StaticAssert;
import descent.core.dom.StaticAssertStatement;
import descent.core.dom.StaticIfStatement;
import descent.core.dom.SwitchCase;
import descent.core.dom.SwitchStatement;
import descent.core.dom.SynchronizedStatement;
import descent.core.dom.TemplateMixinDeclaration;
import descent.core.dom.TemplateType;
import descent.core.dom.ThrowStatement;
import descent.core.dom.TryStatement;
import descent.core.dom.TypeExpression;
import descent.core.dom.TypedefDeclaration;
import descent.core.dom.TypedefDeclarationFragment;
import descent.core.dom.VariableDeclaration;
import descent.core.dom.VariableDeclarationFragment;
import descent.core.dom.VersionStatement;
import descent.core.dom.VolatileStatement;
import descent.core.dom.WhileStatement;
import descent.core.dom.WithStatement;
import descent.core.dom.Modifier.ModifierKeyword;

public class Statement_Test extends Parser_Test {
	
	public void testExpression() {
		String s = " 1;";
		ExpressionStatement stm = (ExpressionStatement) parseStatement(s);
		
		assertEquals(ASTNode.EXPRESSION_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, 2);
		
		assertEquals(ASTNode.NUMBER_LITERAL, stm.getExpression().getNodeType());
	}
	
	public void testAsmBlock() {
		String s = " asm { }";
		AsmBlock stm = (AsmBlock) parseStatement(s);
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testAsmBlockWithSemicolonStatement() {
		String s = " asm { ; }";
		AsmBlock stm = (AsmBlock) parseStatement(s);
		assertEquals(1, stm.statements().size());
		assertPosition(stm.statements().get(0), 7, 1);
	}
	
	public void testAsmBlockWithStatement() {
		String s = " asm { mov eax, 1; }";
		AsmBlock stm = (AsmBlock) parseStatement(s);
		assertEquals(1, stm.statements().size());
		
		AsmStatement asmStatement = (AsmStatement) stm.statements().get(0);
		assertPosition(asmStatement, 7, 11);
		
		assertEquals(4, asmStatement.tokens().size());
		assertEquals("mov", asmStatement.tokens().get(0).getToken());
		assertEquals("eax", asmStatement.tokens().get(1).getToken());
		assertEquals(",", asmStatement.tokens().get(2).getToken());
		assertEquals("1", asmStatement.tokens().get(3).getToken());
		
		assertPosition(asmStatement.tokens().get(0), 7, 3);
		assertPosition(asmStatement.tokens().get(1), 11, 3);
		assertPosition(asmStatement.tokens().get(2), 14, 1);
		assertPosition(asmStatement.tokens().get(3), 16, 1);
	}
	
	public void testAsmBlockWithLabel() {
		String s = " asm { label: nop; }";
		AsmBlock stm = (AsmBlock) parseStatement(s);
		assertEquals(1, stm.statements().size());
		
		LabeledStatement labeledStatement = (LabeledStatement) stm.statements().get(0);
		assertEquals("label", labeledStatement.getLabel().getIdentifier());
		assertPosition(labeledStatement, 7, 11);
		
		assertEquals(ASTNode.ASM_STATEMENT, labeledStatement.getBody().getNodeType());
	}
	
	public void testBreak() {
		String s = " break;";
		BreakStatement stm = (BreakStatement) parseStatement(s);
		
		assertEquals(ASTNode.BREAK_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, 6);
		
		assertNull(stm.getLabel());
	}
	
	public void testBreakLabel() {
		String s = " break label;";
		BreakStatement stm = (BreakStatement) parseStatement(s);
		
		assertEquals(ASTNode.BREAK_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, 12);
		
		assertEquals("label", stm.getLabel().getIdentifier());
		assertPosition(stm.getLabel(), 7, 5);
	}
	
	public void testContinue() {
		String s = " continue;";
		ContinueStatement stm = (ContinueStatement) parseStatement(s);
		
		assertEquals(ASTNode.CONTINUE_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, 9);
		
		assertNull(stm.getLabel());
	}
	
	public void testContinueLabel() {
		String s = " continue label;";
		ContinueStatement stm = (ContinueStatement) parseStatement(s);
		
		assertEquals(ASTNode.CONTINUE_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, 15);
		
		assertEquals("label", stm.getLabel().getIdentifier());
		assertPosition(stm.getLabel(), 10, 5);
	}
	
	public void testReturn() {
		String s = " return;";
		ReturnStatement stm = (ReturnStatement) parseStatement(s);
		
		assertEquals(ASTNode.RETURN_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, 7);
		
		assertNull(stm.getExpression());
	}
	
	public void testReturnValue() {
		String s = " return somex;";
		ReturnStatement stm = (ReturnStatement) parseStatement(s);
		
		assertEquals(ASTNode.RETURN_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, 13);
		
		assertEquals("somex", ((SimpleName) stm.getExpression()).getIdentifier());
		assertPosition(stm.getExpression(), 8, 5);
	}
	
	public void testWhile() {
		String s = " while(true) { }";
		WhileStatement stm = (WhileStatement) parseStatement(s);
		
		assertEquals(ASTNode.WHILE_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, 15);
		
		assertTrue(((BooleanLiteral) stm.getExpression()).booleanValue());
		
		assertPosition(stm.getBody(), 13, 3);
	}
	
	public void testDoWhile() {
		String s = " do { } while(true)";
		DoStatement stm = (DoStatement) parseStatement(s);
		
		assertEquals(ASTNode.DO_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, 18);
		
		assertTrue(((BooleanLiteral) stm.getExpression()).booleanValue());
		
		assertPosition(stm.getBody(), 4, 3);
	}
	
	public void testLabel() {
		String s = " label: break;";
		LabeledStatement stm = (LabeledStatement) parseStatement(s);
		
		assertEquals(ASTNode.LABELED_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, 13);
		
		assertEquals("label", stm.getLabel().getIdentifier());
		assertPosition(stm.getLabel(), 1, 5);
		
		assertPosition(stm.getBody(), 8, 6);
	}
	
	public void testStaticAssertStatement() {
		String s = " static assert(1, true);";
		StaticAssertStatement stm = (StaticAssertStatement) parseStatement(s);
		
		assertEquals(ASTNode.STATIC_ASSERT_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, 23);
		
		assertEquals("1", ((NumberLiteral) stm.getStaticAssert().getExpression()).getToken());
		assertTrue(((BooleanLiteral) stm.getStaticAssert().getMessage()).booleanValue());
	}
	
	public void testStaticAssertDeclaration() {
		String s = " static assert(1, true);";
		CompilationUnit unit = getCompilationUnit(s);
		
		assertEquals(1, unit.declarations().size());
		
		StaticAssert stm = (StaticAssert) unit.declarations().get(0);
		assertEquals(ASTNode.STATIC_ASSERT, stm.getNodeType());
		assertPosition(stm, 1, 23);
		
		assertEquals("1", ((NumberLiteral) stm.getExpression()).getToken());
		assertTrue(((BooleanLiteral) stm.getMessage()).booleanValue());
	}
	
	public void testWith() {
		String s = " with(true) { }";
		WithStatement stm = (WithStatement) parseStatement(s);
		
		assertEquals(ASTNode.WITH_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, 14);
		
		assertPosition(stm.getBody(), 12, 3);
		
		assertTrue(((BooleanLiteral) stm.getExpression()).booleanValue());
	}
	
	public void testFor() {
		String s = " for(int i = 0; i < 10; i++) { }";
		ForStatement stm = (ForStatement) parseStatement(s);
		
		assertEquals(ASTNode.FOR_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testForEmpty() {
		String s = " for(;;) { }";
		ForStatement stm = (ForStatement) parseStatement(s);
		
		assertEquals(ASTNode.FOR_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testForInitializerIsMultiVar() {
		String s = " for(int i, j; ; ) { }";
		ForStatement stm = (ForStatement) parseStatement(s);
		
		DeclarationStatement declStm = (DeclarationStatement) stm.getInitializer();
		assertPosition(declStm, 5, 9);
		
		VariableDeclaration var = (VariableDeclaration) declStm.getDeclaration();
		assertEquals(2, var.fragments().size());
		assertPosition(var, 5, 9);
		
		PrimitiveType type = (PrimitiveType) var.getType();
		assertEquals(PrimitiveType.Code.INT, type.getPrimitiveTypeCode());
		assertPosition(type, 5, 3);
		
		VariableDeclarationFragment f1 = var.fragments().get(0);
		assertEquals("i", f1.getName().getIdentifier());
		assertPosition(f1, 9, 1);
		
		VariableDeclarationFragment f2 = var.fragments().get(1);
		assertEquals("j", f2.getName().getIdentifier());
		assertPosition(f2, 12, 1);
		
		assertEquals(ASTNode.FOR_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testForInitializerIsMultiAlias() {
		String s = " for(alias int i, j; ; ) { }";
		ForStatement stm = (ForStatement) parseStatement(s);
		
		DeclarationStatement declStm = (DeclarationStatement) stm.getInitializer();
		assertPosition(declStm, 5, 15);
		
		AliasDeclaration var = (AliasDeclaration) declStm.getDeclaration();
		assertEquals(2, var.fragments().size());
		assertPosition(var, 5, 15);
		
		PrimitiveType type = (PrimitiveType) var.getType();
		assertEquals(PrimitiveType.Code.INT, type.getPrimitiveTypeCode());
		assertPosition(type, 11, 3);
		
		AliasDeclarationFragment f1 = var.fragments().get(0);
		assertEquals("i", f1.getName().getIdentifier());
		assertPosition(f1, 15, 1);
		
		AliasDeclarationFragment f2 = var.fragments().get(1);
		assertEquals("j", f2.getName().getIdentifier());
		assertPosition(f2, 18, 1);
		
		assertEquals(ASTNode.FOR_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testForInitializerIsMultiTypedef() {
		String s = " for(typedef int i, j; ; ) { }";
		ForStatement stm = (ForStatement) parseStatement(s);
		
		DeclarationStatement declStm = (DeclarationStatement) stm.getInitializer();
		assertPosition(declStm, 5, 17);
		
		TypedefDeclaration var = (TypedefDeclaration) declStm.getDeclaration();
		assertEquals(2, var.fragments().size());
		assertPosition(var, 5, 17);
		
		PrimitiveType type = (PrimitiveType) var.getType();
		assertEquals(PrimitiveType.Code.INT, type.getPrimitiveTypeCode());
		assertPosition(type, 13, 3);
		
		TypedefDeclarationFragment f1 = var.fragments().get(0);
		assertEquals("i", f1.getName().getIdentifier());
		assertPosition(f1, 17, 1);
		
		TypedefDeclarationFragment f2 = var.fragments().get(1);
		assertEquals("j", f2.getName().getIdentifier());
		assertPosition(f2, 20, 1);
		
		assertEquals(ASTNode.FOR_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testForeach() {
		String s = " foreach(inout a, b, c; x) { }";
		ForeachStatement stm = (ForeachStatement) parseStatement(s);
		
		assertEquals(ASTNode.FOREACH_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertFalse(stm.isReverse());
		
		List<Argument> args = stm.arguments();
		assertEquals(3, args.size());
		
		assertPosition(args.get(0), 9, 7);
		assertEquals("inout", args.get(0).modifiers().get(0).toString());
		assertEquals("a", args.get(0).getName().getIdentifier());
		assertPosition(args.get(0).getName(), 15, 1);
		
		assertPosition(args.get(1), 18, 1);
		assertEquals(0, args.get(1).modifiers().size());
		assertEquals("b", args.get(1).getName().getIdentifier());
		assertPosition(args.get(1).getName(), 18, 1);
		
		assertPosition(args.get(2), 21, 1);
		assertEquals(0, args.get(1).modifiers().size());
		assertEquals("c", args.get(2).getName().getIdentifier());
		assertPosition(args.get(2).getName(), 21, 1);
	}
	
	public void testForeachReverse() {
		String s = " foreach_reverse(a, b, c; x) { }";
		ForeachStatement stm = (ForeachStatement) parseStatement(s);
		
		assertEquals(ASTNode.FOREACH_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("x", ((SimpleName) stm.getExpression()).getIdentifier());
		
		assertTrue(stm.isReverse());
	}
	
	public void testForeachRange() {
		String s = " foreach(a; 1 .. 3) { }";
		ForeachRangeStatement stm = (ForeachRangeStatement) parseStatement(s, AST.D2);
		
		assertEquals(ASTNode.FOREACH_RANGE_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertFalse(stm.isReverse());
		
		assertEquals("a", stm.getArgument().getName().toString());
		assertEquals("1", stm.getFromExpression().toString());
		assertEquals("3", stm.getToExpression().toString());
	}
	
	public void testForeachWithDeclaration() {
		String s = " foreach(int x; y) { }";
		ForeachStatement stm = (ForeachStatement) parseStatement(s);
		
		assertEquals(ASTNode.FOREACH_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertFalse(stm.isReverse());
		
		List<Argument> args = stm.arguments();
		assertEquals(1, args.size());
		
		assertPosition(args.get(0), 9, 5);
		assertEquals(0, args.get(0).modifiers().size());
		assertEquals("x", args.get(0).getName().getIdentifier());
		assertEquals("int", args.get(0).getType().toString());
		assertPosition(args.get(0).getName(), 13, 1);
	}
	
	public void testVolatile() {
		String s = " volatile int x = 2;";
		VolatileStatement stm = (VolatileStatement) parseStatement(s);
		
		assertEquals(ASTNode.VOLATILE_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testSwitch() {
		String s = " switch(1) { case 1: default: }";
		SwitchStatement stm = (SwitchStatement) parseStatement(s);
		
		assertEquals(ASTNode.SWITCH_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("1", ((NumberLiteral) stm.getExpression()).getToken());
	}
	
	public void testTryFinally() {
		String s = " try { } finally { }";
		TryStatement stm = (TryStatement) parseStatement(s);
		
		assertEquals(ASTNode.TRY_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testTryCatch() {
		String s = " try { } catch(Bla b) { } catch { }";
		TryStatement stm = (TryStatement) parseStatement(s);
		
		assertEquals(ASTNode.TRY_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals(2, stm.catchClauses().size());
		assertPosition((CatchClause) stm.catchClauses().get(0), 9, 16);
	}
	
	public void testTryCatchFinally() {
		String s = " try { } catch(Bla b) { } catch { } finally { }";
		TryStatement stm = (TryStatement) parseStatement(s);
		
		assertEquals(ASTNode.TRY_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals(2, stm.catchClauses().size());
		assertPosition((CatchClause) stm.catchClauses().get(0), 9, 16);
	}
	
	public void testThrow() {
		String s = " throw 1;";
		ThrowStatement stm = (ThrowStatement) parseStatement(s);
		
		assertEquals(ASTNode.THROW_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("1", ((NumberLiteral) stm.getExpression()).getToken());
	}
	
	public void testSynchronized1() {
		String s = " synchronized x = 2;";
		SynchronizedStatement stm = (SynchronizedStatement) parseStatement(s);
		
		assertEquals(ASTNode.SYNCHRONIZED_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testSynchronized2() {
		String s = " synchronized(1) x = 2;";
		SynchronizedStatement stm = (SynchronizedStatement) parseStatement(s);
		
		assertEquals(ASTNode.SYNCHRONIZED_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testOnScope() {
		Object[][] objs = {
				{ "exit", ScopeStatement.Event.EXIT },
				{ "failure", ScopeStatement.Event.FAILURE },
				{ "success", ScopeStatement.Event.SUCCESS },
		};
		
		for(Object[] obj : objs) {
			String s = " scope(" + obj[0] + ") x = 2;";
			ScopeStatement stm = (ScopeStatement) parseStatement(s);
			
			assertEquals(ASTNode.SCOPE_STATEMENT, stm.getNodeType());
			assertPosition(stm, 1, s.length() - 1);
			
			assertEquals(obj[1], stm.getEvent());
		}
	}
	
	public void testOnScope2() {
		Object[][] objs = {
				{ "exit", ScopeStatement.Event.EXIT },
				{ "failure", ScopeStatement.Event.FAILURE },
				{ "success", ScopeStatement.Event.SUCCESS },
		};
		
		for(Object[] obj : objs) {
			String s = " on_scope_" + obj[0] + " { }";
			ScopeStatement stm = (ScopeStatement) parseStatement(s, AST.D0);
			
			assertEquals(ASTNode.SCOPE_STATEMENT, stm.getNodeType());
			assertPosition(stm, 1, s.length() - 1);
			
			assertEquals(obj[1], stm.getEvent());
		}
	}
	
	public void testGoto() {
		String s = " goto bla;";
		GotoStatement stm = (GotoStatement) parseStatement(s);
		
		assertEquals(ASTNode.GOTO_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertPosition(stm.getLabel(), 6, 3);
	}
	
	public void testGotoDefault() {
		String s = " goto default;";
		ASTNode stm = (ASTNode) parseStatement(s);
		
		assertEquals(ASTNode.GOTO_DEFAULT_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testGotoCase() {
		String s = " goto case bla;";
		GotoCaseStatement stm = (GotoCaseStatement) parseStatement(s);
		
		assertEquals(ASTNode.GOTO_CASE_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testPragma() {
		String s = " pragma(lib, 1);";
		PragmaStatement stm = (PragmaStatement) parseStatement(s);
		
		assertEquals(ASTNode.PRAGMA_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("lib", stm.getName().getIdentifier());
		assertPosition(stm.getName(), 8, 3);
		
		assertEquals(1, stm.arguments().size());
	}
	
	public void testIf() {
		String s = " if (1) { } else { }";
		IfStatement stm = (IfStatement) parseStatement(s);
		
		assertEquals(ASTNode.IF_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("1", ((NumberLiteral) stm.getExpression()).getToken());
		assertNull(stm.getArgument());
	}
	
	public void testIfAuto() {
		String s = " if (auto x = 1) { } else { }";
		IfStatement stm = (IfStatement) parseStatement(s);
		
		assertEquals(ASTNode.IF_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("1", ((NumberLiteral) stm.getExpression()).getToken());
		assertNotNull(stm.getArgument());
		assertEquals("x", stm.getArgument().getName().getIdentifier());
		assertPosition(stm.getArgument(), 5, 6);
	}
	
	public void testIfDeclaration() {
		String s = " if (int x = 1) { } else { }";
		IfStatement stm = (IfStatement) parseStatement(s);
		
		assertEquals(ASTNode.IF_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("1", ((NumberLiteral) stm.getExpression()).getToken());
		
		Argument argument = stm.getArgument();
		assertNotNull(argument);
		assertEquals("x", argument.getName().getIdentifier());
		assertEquals("int", argument.getType().toString());
		assertPosition(argument, 5, 5);
	}
	
	public void testIfDeprecated() {
		String s = " if (a; b) { } else { }";
		IfStatement stm = (IfStatement) parseStatement(s);
		
		assertEquals(ASTNode.IF_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("b", ((SimpleName) stm.getExpression()).getIdentifier());
		
		Argument argument = stm.getArgument();
		assertNotNull(argument);
		assertEquals("a", argument.getName().getIdentifier());
		assertPosition(argument, 5, 1);
	}
	
	public void testStaticIf() {
		String s = " static if (1) { } else { }";
		StaticIfStatement stm = (StaticIfStatement) parseStatement(s);
		
		assertEquals(ASTNode.STATIC_IF_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("1", ((NumberLiteral) stm.getExpression()).getToken());
	}
	
	public void testDebug1() {
		String s = " debug { } else { }";
		DebugStatement stm = (DebugStatement) parseStatement(s);
		
		assertEquals(ASTNode.DEBUG_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertNull(stm.getVersion());
	}
	
	public void testDebug2() {
		String s = " debug(1) { }";
		DebugStatement stm = (DebugStatement) parseStatement(s);
		
		assertEquals(ASTNode.DEBUG_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("1", stm.getVersion().getValue());
	}
	
	public void testVersion() {
		String s = " version(release) { } else { }";
		VersionStatement stm = (VersionStatement) parseStatement(s);
		
		assertEquals(ASTNode.VERSION_STATEMENT, stm.getNodeType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("release", stm.getVersion().getValue());
	}
	
	public void testTypeof() {
		String s = " typeof(2) x;";
		DeclarationStatement stm = (DeclarationStatement) parseStatement(s);
		
		VariableDeclaration var = (VariableDeclaration) stm.getDeclaration();
		assertEquals(ASTNode.TYPEOF_TYPE, var.getType().getNodeType());
	}
	
	public void testAggregate() {
		String s = " class X { };";
		DeclarationStatement stm = (DeclarationStatement) parseStatement(s);
		
		AggregateDeclaration ad = (AggregateDeclaration) stm.getDeclaration();
		assertEquals(AggregateDeclaration.Kind.CLASS, ad.getKind());
	}
	
	public void testEnum() {
		String s = " enum X { a };";
		DeclarationStatement stm = (DeclarationStatement) parseStatement(s);
		
		EnumDeclaration var = (EnumDeclaration) stm.getDeclaration();
		assertNotNull(var);
	}
	
	public void testTemplateMixin() {
		String s = " mixin X x;";
		DeclarationStatement stm = (DeclarationStatement) parseStatement(s);
		
		TemplateMixinDeclaration var = (TemplateMixinDeclaration) stm.getDeclaration();
		assertNotNull(var);
	}
	
	public void testMixin() {
		String s = " mixin(\"something\");";
		DeclarationStatement stm = (DeclarationStatement) parseStatement(s);
		
		MixinDeclaration var = (MixinDeclaration) stm.getDeclaration();
		assertNotNull(var);
	}
	
	public void testScope() {
		String s = " scope int x;";
		DeclarationStatement stm = (DeclarationStatement) parseStatement(s);
		assertPosition(stm, 1, s.length() - 1);
		
		VariableDeclaration var = (VariableDeclaration) stm.getDeclaration();
		assertPosition(var, 1, s.length() - 1);
		assertEquals(1, var.modifiers().size());
	}
	
	public void testScopeFinal() {
		String s = " scope final int x;";
		DeclarationStatement stm = (DeclarationStatement) parseStatement(s);
		
		VariableDeclaration var = (VariableDeclaration) stm.getDeclaration();
		assertNotNull(var);
		assertEquals(2, var.modifiers().size());
	}
	
	public void testVars() {
		String s = " int x, y, z;";
		DeclarationStatement stm = (DeclarationStatement) parseStatement(s);
		
		VariableDeclaration var = (VariableDeclaration) stm.getDeclaration();
		assertNotNull(var);
		assertEquals(3, var.fragments().size());
	}
	
	public void testAliases() {
		String s = " alias int x, y, z;";
		DeclarationStatement stm = (DeclarationStatement) parseStatement(s);
		
		AliasDeclaration var = (AliasDeclaration) stm.getDeclaration();
		assertNotNull(var);
		assertEquals(3, var.fragments().size());
	}
	
	public void testTypedefs() {
		String s = " typedef int x, y, z;";
		DeclarationStatement stm = (DeclarationStatement) parseStatement(s);
		
		TypedefDeclaration var = (TypedefDeclaration) stm.getDeclaration();
		assertNotNull(var);
		assertEquals(3, var.fragments().size());
	}
	
	public void testStaticVar() {
		String s = " static int x;";
		DeclarationStatement stm = (DeclarationStatement) parseStatement(s);
		
		VariableDeclaration var = (VariableDeclaration) stm.getDeclaration();
		assertNotNull(var);
		assertEquals(1, var.fragments().size());
		assertEquals(1, var.modifiers().size());
		assertEquals(Modifier.ModifierKeyword.STATIC_KEYWORD, var.modifiers().get(0).getModifierKeyword());
	}
	
	public void testModifiersWithVar() {
		Object[][] objs = {
			{ "const", Modifier.ModifierKeyword.CONST_KEYWORD },
			{ "final", Modifier.ModifierKeyword.FINAL_KEYWORD },
			{ "auto", Modifier.ModifierKeyword.AUTO_KEYWORD },
			{ "scope", Modifier.ModifierKeyword.SCOPE_KEYWORD },
			{ "override", Modifier.ModifierKeyword.OVERRIDE_KEYWORD },
			{ "abstract", Modifier.ModifierKeyword.ABSTRACT_KEYWORD },
			{ "synchronized", Modifier.ModifierKeyword.SYNCHRONIZED_KEYWORD },
			{ "deprecated", Modifier.ModifierKeyword.DEPRECATED_KEYWORD },
			{ "scope", Modifier.ModifierKeyword.SCOPE_KEYWORD },
		};
		
		for(Object[] pair : objs) {
			String s = " static " + pair[0] + "  x = 1;";
			DeclarationStatement stm = (DeclarationStatement) parseStatement(s);
			VariableDeclaration var = (VariableDeclaration) stm.getDeclaration();
			assertEquals(2, var.modifiers().size());
			assertEquals(ModifierKeyword.STATIC_KEYWORD, var.modifiers().get(0).getModifierKeyword());
			assertPosition(var.modifiers().get(0), 1, "static".length());
			assertEquals(pair[1], var.modifiers().get(1).getModifierKeyword());
			assertPosition(var.modifiers().get(1), 8, ((String) pair[0]).length());
		}
	}
	
	public void testScopeExp() throws Exception {
		String s = "void foo() { x!(y); }";
		CompilationUnit compilationUnit = getCompilationUnit(s, AST.D1);
		FunctionDeclaration decl = (FunctionDeclaration) compilationUnit.declarations().get(0);
		ExpressionStatement expStm = (ExpressionStatement) decl.getBody().statements().get(0);
		assertPosition(expStm, 13, 6);
		TypeExpression exp = (TypeExpression) expStm.getExpression();
		assertPosition(exp, 13, 5);
		TemplateType type = (TemplateType) exp.getType();
		assertPosition(type, 13, 5);
		assertPosition(type.getName(), 13, 1);
		assertPosition(type.arguments().get(0), 16, 1);
	}
	
	public void testScopeExp2() throws Exception {
		String s = "void foo() { x!(y).z(); }";
		CompilationUnit compilationUnit = getCompilationUnit(s, AST.D1);
		FunctionDeclaration decl = (FunctionDeclaration) compilationUnit.declarations().get(0);
		ExpressionStatement expStm = (ExpressionStatement) decl.getBody().statements().get(0);
		assertPosition(expStm, 13, 10);
		CallExpression callExp = (CallExpression) expStm.getExpression();
		assertPosition(callExp, 13, 9);
		DotIdentifierExpression dotIdExp = (DotIdentifierExpression) callExp.getExpression();;
		assertPosition(dotIdExp, 13, 7);
		TypeExpression exp = (TypeExpression) dotIdExp.getExpression();
		assertPosition(exp, 13, 5);
		TemplateType type = (TemplateType) exp.getType();
		assertPosition(type, 13, 5);
		assertPosition(type.getName(), 13, 1);
		assertPosition(type.arguments().get(0), 16, 1);
	}
	
	public void testParseStatements() {
		String s = "if(true) { } if (false) { } if (1) { }";
		
		ASTParser parser = ASTParser.newParser(AST.D1);
		parser.setKind(ASTParser.K_STATEMENTS);
		parser.setSource(s.toCharArray());
		Block block = (Block) parser.createAST(null);
		
		assertEquals(3, block.statements().size());
		
		Statement st = block.statements().get(0);
		assertPosition(st, 0, 12);
	}
	
	public void testCase() throws Exception {
		String s = " void foo() { switch(x) { case 1: break; } }";
		CompilationUnit unit = getCompilationUnit(s);
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		SwitchStatement switc = (SwitchStatement) func.getBody().statements().get(0);
		SwitchCase cas = (SwitchCase) ((Block) switc.getBody()).statements().get(0);
		assertEquals(1, cas.expressions().size());
		assertEquals("1", cas.expressions().get(0).toString());
		assertEquals(1, cas.statements().size());
		BreakStatement b = (BreakStatement) cas.statements().get(0);
		assertNotNull(b);
	}
	
	public void testCaseWithManyStatements() throws Exception {
		String s = " void foo() { switch(x) { case 1: foo(); bar(); break; } }";
		CompilationUnit unit = getCompilationUnit(s);
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		SwitchStatement switc = (SwitchStatement) func.getBody().statements().get(0);
		SwitchCase cas = (SwitchCase) ((Block) switc.getBody()).statements().get(0);
		assertEquals(1, cas.expressions().size());
		assertEquals("1", cas.expressions().get(0).toString());
		assertEquals(3, cas.statements().size());
	}
	
	public void testDefault() throws Exception {
		String s = " void foo() { switch(x) { default: break; } }";
		CompilationUnit unit = getCompilationUnit(s);
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		SwitchStatement switc = (SwitchStatement) func.getBody().statements().get(0);
		DefaultStatement cas = (DefaultStatement) ((Block) switc.getBody()).statements().get(0);
		assertEquals(1, cas.statements().size());
		BreakStatement b = (BreakStatement) cas.statements().get(0);
		assertNotNull(b);
	}
	
	public void testDefaultWithManyStatements() throws Exception {
		String s = " void foo() { switch(x) { default: foo(); bar(); break; } }";
		CompilationUnit unit = getCompilationUnit(s);
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		SwitchStatement switc = (SwitchStatement) func.getBody().statements().get(0);
		DefaultStatement cas = (DefaultStatement) ((Block) switc.getBody()).statements().get(0);
		assertEquals(3, cas.statements().size());
	}
	
	public void testMultiCase() throws Exception {
		String s = " void foo() { switch(x) { case 1, 2, 3: break; } }";
		CompilationUnit unit = getCompilationUnit(s);
		FunctionDeclaration func = (FunctionDeclaration) unit.declarations().get(0);
		SwitchStatement switc = (SwitchStatement) func.getBody().statements().get(0);
		SwitchCase cas = (SwitchCase) ((Block) switc.getBody()).statements().get(0);
		assertEquals(3, cas.expressions().size());
		assertEquals("1", cas.expressions().get(0).toString());
		assertEquals("2", cas.expressions().get(1).toString());
		assertEquals("3", cas.expressions().get(2).toString());
		assertEquals(1, cas.statements().size());
		BreakStatement b = (BreakStatement) cas.statements().get(0);
		assertNotNull(b);
	}

}
