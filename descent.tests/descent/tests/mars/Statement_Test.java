package descent.tests.mars;

import java.util.List;

import descent.core.dom.ASTNode;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.Argument;
import descent.core.dom.BooleanLiteral;
import descent.core.dom.BreakStatement;
import descent.core.dom.CatchClause;
import descent.core.dom.CompilationUnit;
import descent.core.dom.ContinueStatement;
import descent.core.dom.DebugStatement;
import descent.core.dom.DeclarationStatement;
import descent.core.dom.DoStatement;
import descent.core.dom.EnumDeclaration;
import descent.core.dom.ExpressionStatement;
import descent.core.dom.ForStatement;
import descent.core.dom.ForeachStatement;
import descent.core.dom.GotoCaseStatement;
import descent.core.dom.GotoStatement;
import descent.core.dom.IfStatement;
import descent.core.dom.LabelStatement;
import descent.core.dom.MixinDeclaration;
import descent.core.dom.NumberLiteral;
import descent.core.dom.PragmaStatement;
import descent.core.dom.ReturnStatement;
import descent.core.dom.ScopeStatement;
import descent.core.dom.SimpleName;
import descent.core.dom.StaticAssert;
import descent.core.dom.StaticAssertStatement;
import descent.core.dom.StaticIfStatement;
import descent.core.dom.SwitchStatement;
import descent.core.dom.SynchronizedStatement;
import descent.core.dom.ThrowStatement;
import descent.core.dom.TryStatement;
import descent.core.dom.VariableDeclaration;
import descent.core.dom.VersionStatement;
import descent.core.dom.VolatileStatement;
import descent.core.dom.WhileStatement;
import descent.core.dom.WithStatement;

public class Statement_Test extends Parser_Test {
	
	public void testExpression() {
		String s = " 1;";
		ExpressionStatement stm = (ExpressionStatement) parseStatement(s);
		
		assertEquals(ASTNode.EXPRESSION_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, 2);
		
		assertEquals(ASTNode.NUMBER_LITERAL, stm.getExpression().getNodeType0());
	}
	
	public void testBreak() {
		String s = " break;";
		BreakStatement stm = (BreakStatement) parseStatement(s);
		
		assertEquals(ASTNode.BREAK_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, 6);
		
		assertNull(stm.getLabel());
	}
	
	public void testBreakLabel() {
		String s = " break label;";
		BreakStatement stm = (BreakStatement) parseStatement(s);
		
		assertEquals(ASTNode.BREAK_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, 12);
		
		assertEquals("label", stm.getLabel().getIdentifier());
		assertPosition(stm.getLabel(), 7, 5);
	}
	
	public void testContinue() {
		String s = " continue;";
		ContinueStatement stm = (ContinueStatement) parseStatement(s);
		
		assertEquals(ASTNode.CONTINUE_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, 9);
		
		assertNull(stm.getLabel());
	}
	
	public void testContinueLabel() {
		String s = " continue label;";
		ContinueStatement stm = (ContinueStatement) parseStatement(s);
		
		assertEquals(ASTNode.CONTINUE_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, 15);
		
		assertEquals("label", stm.getLabel().getIdentifier());
		assertPosition(stm.getLabel(), 10, 5);
	}
	
	public void testReturn() {
		String s = " return;";
		ReturnStatement stm = (ReturnStatement) parseStatement(s);
		
		assertEquals(ASTNode.RETURN_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, 7);
		
		assertNull(stm.getExpression());
	}
	
	public void testReturnValue() {
		String s = " return somex;";
		ReturnStatement stm = (ReturnStatement) parseStatement(s);
		
		assertEquals(ASTNode.RETURN_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, 13);
		
		assertEquals("somex", ((SimpleName) stm.getExpression()).getIdentifier());
		assertPosition(stm.getExpression(), 8, 5);
	}
	
	public void testWhile() {
		String s = " while(true) { }";
		WhileStatement stm = (WhileStatement) parseStatement(s);
		
		assertEquals(ASTNode.WHILE_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, 15);
		
		assertTrue(((BooleanLiteral) stm.getExpression()).booleanValue());
		
		assertPosition(stm.getBody(), 13, 3);
	}
	
	public void testDoWhile() {
		String s = " do { } while(true)";
		DoStatement stm = (DoStatement) parseStatement(s);
		
		assertEquals(ASTNode.DO_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, 18);
		
		assertTrue(((BooleanLiteral) stm.getExpression()).booleanValue());
		
		assertPosition(stm.getBody(), 4, 3);
	}
	
	public void testLabel() {
		String s = " label: break;";
		LabelStatement stm = (LabelStatement) parseStatement(s);
		
		assertEquals(ASTNode.LABEL_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, 13);
		
		assertEquals("label", stm.getLabel().getIdentifier());
		assertPosition(stm.getLabel(), 1, 5);
		
		assertPosition(stm.getBody(), 8, 6);
	}
	
	public void testStaticAssertStatement() {
		String s = " static assert(1, true);";
		StaticAssertStatement stm = (StaticAssertStatement) parseStatement(s);
		
		assertEquals(ASTNode.STATIC_ASSERT_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, 23);
		
		assertEquals("1", ((NumberLiteral) stm.getStaticAssert().getExpression()).getToken());
		assertTrue(((BooleanLiteral) stm.getStaticAssert().getMessage()).booleanValue());
	}
	
	public void testStaticAssertDeclaration() {
		String s = " static assert(1, true);";
		CompilationUnit unit = getCompilationUnit(s);
		
		assertEquals(1, unit.declarations().size());
		
		StaticAssert stm = (StaticAssert) unit.declarations().get(0);
		assertEquals(ASTNode.STATIC_ASSERT, stm.getNodeType0());
		assertPosition(stm, 1, 23);
		
		assertEquals("1", ((NumberLiteral) stm.getExpression()).getToken());
		assertTrue(((BooleanLiteral) stm.getMessage()).booleanValue());
	}
	
	public void testWith() {
		String s = " with(true) { }";
		WithStatement stm = (WithStatement) parseStatement(s);
		
		assertEquals(ASTNode.WITH_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, 14);
		
		assertPosition(stm.getBody(), 12, 3);
		
		assertTrue(((BooleanLiteral) stm.getExpression()).booleanValue());
	}
	
	public void testFor() {
		String s = " for(int i = 0; i < 10; i++) { }";
		ForStatement stm = (ForStatement) parseStatement(s);
		
		assertEquals(ASTNode.FOR_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testForEmpty() {
		String s = " for(;;) { }";
		ForStatement stm = (ForStatement) parseStatement(s);
		
		assertEquals(ASTNode.FOR_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testForeach() {
		String s = " foreach(inout a, b, c; x) { }";
		ForeachStatement stm = (ForeachStatement) parseStatement(s);
		
		assertEquals(ASTNode.FOREACH_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, s.length() - 1);
		
		assertFalse(stm.isReverse());
		
		List<Argument> args = stm.arguments();
		assertEquals(3, args.size());
		
		assertPosition(args.get(0), 9, 7);
		assertEquals(Argument.PassageMode.INOUT, args.get(0).getPassageMode());
		assertEquals("a", args.get(0).getName().getIdentifier());
		assertPosition(args.get(0).getName(), 15, 1);
		
		assertPosition(args.get(1), 18, 1);
		assertEquals(Argument.PassageMode.IN, args.get(1).getPassageMode());
		assertEquals("b", args.get(1).getName().getIdentifier());
		assertPosition(args.get(1).getName(), 18, 1);
		
		assertPosition(args.get(2), 21, 1);
		assertEquals(Argument.PassageMode.IN, args.get(2).getPassageMode());
		assertEquals("c", args.get(2).getName().getIdentifier());
		assertPosition(args.get(2).getName(), 21, 1);
	}
	
	public void testForeachReverse() {
		String s = " foreach_reverse(a, b, c; x) { }";
		ForeachStatement stm = (ForeachStatement) parseStatement(s);
		
		assertEquals(ASTNode.FOREACH_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("x", ((SimpleName) stm.getExpression()).getIdentifier());
		
		assertTrue(stm.isReverse());
	}
	
	public void testForeachWithDeclaration() {
		String s = " foreach(int x; y) { }";
		ForeachStatement stm = (ForeachStatement) parseStatement(s);
		
		assertEquals(ASTNode.FOREACH_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, s.length() - 1);
		
		assertFalse(stm.isReverse());
		
		List<Argument> args = stm.arguments();
		assertEquals(1, args.size());
		
		assertPosition(args.get(0), 9, 5);
		assertEquals(Argument.PassageMode.IN, args.get(0).getPassageMode());
		assertEquals("x", args.get(0).getName().getIdentifier());
		assertEquals("int", args.get(0).getType().toString());
		assertPosition(args.get(0).getName(), 13, 1);
	}
	
	public void testVolatile() {
		String s = " volatile int x = 2;";
		VolatileStatement stm = (VolatileStatement) parseStatement(s);
		
		assertEquals(ASTNode.VOLATILE_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testSwitch() {
		String s = " switch(1) { case 1: default: }";
		SwitchStatement stm = (SwitchStatement) parseStatement(s);
		
		assertEquals(ASTNode.SWITCH_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("1", ((NumberLiteral) stm.getExpression()).getToken());
	}
	
	public void testTryFinally() {
		String s = " try { } finally { }";
		TryStatement stm = (TryStatement) parseStatement(s);
		
		assertEquals(ASTNode.TRY_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testTryCatchFinally() {
		String s = " try { } catch(Bla b) { } catch { } finally { }";
		TryStatement stm = (TryStatement) parseStatement(s);
		
		assertEquals(ASTNode.TRY_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals(2, stm.catchClauses().size());
		assertPosition((CatchClause) stm.catchClauses().get(0), 9, 16);
	}
	
	public void testThrow() {
		String s = " throw 1;";
		ThrowStatement stm = (ThrowStatement) parseStatement(s);
		
		assertEquals(ASTNode.THROW_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("1", ((NumberLiteral) stm.getExpression()).getToken());
	}
	
	public void testSynchronized1() {
		String s = " synchronized x = 2;";
		SynchronizedStatement stm = (SynchronizedStatement) parseStatement(s);
		
		assertEquals(ASTNode.SYNCHRONIZED_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testSynchronized2() {
		String s = " synchronized(1) x = 2;";
		SynchronizedStatement stm = (SynchronizedStatement) parseStatement(s);
		
		assertEquals(ASTNode.SYNCHRONIZED_STATEMENT, stm.getNodeType0());
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
			
			assertEquals(ASTNode.SCOPE_STATEMENT, stm.getNodeType0());
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
			ScopeStatement stm = (ScopeStatement) parseStatement(s);
			
			assertEquals(ASTNode.SCOPE_STATEMENT, stm.getNodeType0());
			assertPosition(stm, 1, s.length() - 1);
			
			assertEquals(obj[1], stm.getEvent());
		}
	}
	
	public void testGoto() {
		String s = " goto bla;";
		GotoStatement stm = (GotoStatement) parseStatement(s);
		
		assertEquals(ASTNode.GOTO_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, s.length() - 1);
		
		assertPosition(stm.getLabel(), 6, 3);
	}
	
	public void testGotoDefault() {
		String s = " goto default;";
		ASTNode stm = (ASTNode) parseStatement(s);
		
		assertEquals(ASTNode.GOTO_DEFAULT_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testGotoCase() {
		String s = " goto case bla;";
		GotoCaseStatement stm = (GotoCaseStatement) parseStatement(s);
		
		assertEquals(ASTNode.GOTO_CASE_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testPragma() {
		String s = " pragma(lib, 1);";
		PragmaStatement stm = (PragmaStatement) parseStatement(s);
		
		assertEquals(ASTNode.PRAGMA_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("lib", stm.getName().getIdentifier());
		assertPosition(stm.getName(), 8, 3);
		
		assertEquals(1, stm.arguments().size());
	}
	
	public void testIf() {
		String s = " if (1) { } else { }";
		IfStatement stm = (IfStatement) parseStatement(s);
		
		assertEquals(ASTNode.IF_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("1", ((NumberLiteral) stm.getExpression()).getToken());
		assertNull(stm.getArgument());
	}
	
	public void testIfAuto() {
		String s = " if (auto x = 1) { } else { }";
		IfStatement stm = (IfStatement) parseStatement(s);
		
		assertEquals(ASTNode.IF_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("1", ((NumberLiteral) stm.getExpression()).getToken());
		assertNotNull(stm.getArgument());
		assertEquals("x", stm.getArgument().getName().getIdentifier());
		assertPosition(stm.getArgument(), 5, 6);
	}
	
	public void testIfDeclaration() {
		String s = " if (int x = 1) { } else { }";
		IfStatement stm = (IfStatement) parseStatement(s);
		
		assertEquals(ASTNode.IF_STATEMENT, stm.getNodeType0());
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
		
		assertEquals(ASTNode.IF_STATEMENT, stm.getNodeType0());
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
		
		assertEquals(ASTNode.STATIC_IF_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("1", ((NumberLiteral) stm.getExpression()).getToken());
	}
	
	public void testDebug1() {
		String s = " debug { } else { }";
		DebugStatement stm = (DebugStatement) parseStatement(s);
		
		assertEquals(ASTNode.DEBUG_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, s.length() - 1);
		
		assertNull(stm.getVersion());
	}
	
	public void testDebug2() {
		String s = " debug(1) { }";
		DebugStatement stm = (DebugStatement) parseStatement(s);
		
		assertEquals(ASTNode.DEBUG_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("1", stm.getVersion().getValue());
	}
	
	public void testVersion() {
		String s = " version(release) { } else { }";
		VersionStatement stm = (VersionStatement) parseStatement(s);
		
		assertEquals(ASTNode.VERSION_STATEMENT, stm.getNodeType0());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("release", stm.getVersion().getValue());
	}
	
	/* TODO
	public void testStaticExtern() {
		String s = " static extern int x;";
		DeclarationStatement stm = (DeclarationStatement) parseStatement(s);
		
		VariableDeclaration var = (VariableDeclaration) stm.getDeclaration();
		assertTrue((var.getModifier() & IModifier.EXTERN) != 0);
	}
	*/
	
	public void testTypeof() {
		String s = " typeof(2) x;";
		DeclarationStatement stm = (DeclarationStatement) parseStatement(s);
		
		VariableDeclaration var = (VariableDeclaration) stm.getDeclaration();
		assertEquals(ASTNode.TYPEOF_TYPE, var.getType().getNodeType0());
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
	
	public void testMixin() {
		String s = " mixin X x;";
		DeclarationStatement stm = (DeclarationStatement) parseStatement(s);
		
		MixinDeclaration var = (MixinDeclaration) stm.getDeclaration();
		assertNotNull(var);
	}
	
	public void testScope() {
		String s = " scope int x;";
		DeclarationStatement stm = (DeclarationStatement) parseStatement(s);
		
		VariableDeclaration var = (VariableDeclaration) stm.getDeclaration();
		assertNotNull(var);
		// TODO assertEquals(1, var.modifiers().size());
	}

}
