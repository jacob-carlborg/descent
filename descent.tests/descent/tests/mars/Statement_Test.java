package descent.tests.mars;

import descent.core.dom.IAggregateDeclaration;
import descent.core.dom.IArgument;
import descent.core.dom.IBreakStatement;
import descent.core.dom.ICompilationUnit;
import descent.core.dom.IConditionalStatement;
import descent.core.dom.IContinueStatement;
import descent.core.dom.IElement;
import descent.core.dom.IDebugStatement;
import descent.core.dom.IDeclarationStatement;
import descent.core.dom.IDoWhileStatement;
import descent.core.dom.IEnumDeclaration;
import descent.core.dom.IExpression;
import descent.core.dom.IExpressionStatement;
import descent.core.dom.IForStatement;
import descent.core.dom.IForeachStatement;
import descent.core.dom.IGotoCaseStatement;
import descent.core.dom.IGotoStatement;
import descent.core.dom.IIfStatement;
import descent.core.dom.ILabelStatement;
import descent.core.dom.IMixinDeclaration;
import descent.core.dom.IModifier;
import descent.core.dom.IOnScopeStatement;
import descent.core.dom.IPragmaStatement;
import descent.core.dom.IReturnStatement;
import descent.core.dom.IScopeStatement;
import descent.core.dom.IStatement;
import descent.core.dom.IStaticAssertDeclaration;
import descent.core.dom.IStaticAssertStatement;
import descent.core.dom.IStaticIfStatement;
import descent.core.dom.ISwitchStatement;
import descent.core.dom.ISynchronizedStatement;
import descent.core.dom.IThrowStatement;
import descent.core.dom.ITryStatement;
import descent.core.dom.IType;
import descent.core.dom.IVariableDeclaration;
import descent.core.dom.IVersionStatement;
import descent.core.dom.IVolatileStatement;
import descent.core.dom.IWhileStatement;
import descent.core.dom.IWithStatement;
import descent.internal.core.dom.ParserFacade;

public class Statement_Test extends Parser_Test {
	
	public void testExpression() {
		String s = " 1;";
		IExpressionStatement stm = (IExpressionStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.EXPRESSION_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, 2);
		
		assertEquals(IExpression.INTEGER_EXPRESSION, stm.getExpression().getElementType());
		
		assertVisitor(stm, 2);
	}
	
	public void testBreak() {
		String s = " break;";
		IBreakStatement stm = (IBreakStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.BREAK_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, 6);
		
		assertNull(stm.getLabel());
		
		assertVisitor(stm, 1);
	}
	
	public void testBreakLabel() {
		String s = " break label;";
		IBreakStatement stm = (IBreakStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.BREAK_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, 12);
		
		assertEquals("label", stm.getLabel().toString());
		assertPosition(stm.getLabel(), 7, 5);
		
		assertVisitor(stm, 2);
	}
	
	public void testContinue() {
		String s = " continue;";
		IContinueStatement stm = (IContinueStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.CONTINUE_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, 9);
		
		assertNull(stm.getLabel());
		
		assertVisitor(stm, 1);
	}
	
	public void testContinueLabel() {
		String s = " continue label;";
		IContinueStatement stm = (IContinueStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.CONTINUE_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, 15);
		
		assertEquals("label", stm.getLabel().toString());
		assertPosition(stm.getLabel(), 10, 5);
		
		assertVisitor(stm, 2);
	}
	
	public void testReturn() {
		String s = " return;";
		IReturnStatement stm = (IReturnStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.RETURN_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, 7);
		
		assertNull(stm.getReturnValue());
		
		assertVisitor(stm, 1);
	}
	
	public void testReturnValue() {
		String s = " return somex;";
		IReturnStatement stm = (IReturnStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.RETURN_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, 13);
		
		assertEquals("somex", stm.getReturnValue().toString());
		assertPosition(stm.getReturnValue(), 8, 5);
		
		assertVisitor(stm, 2);
	}
	
	public void testWhile() {
		String s = " while(true) { }";
		IWhileStatement stm = (IWhileStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.WHILE_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, 15);
		
		assertEquals("true", stm.getCondition().toString());
		
		assertPosition(stm.getBody(), 13, 3);
		
		assertVisitor(stm, 4);
	}
	
	public void testDoWhile() {
		String s = " do { } while(true)";
		IDoWhileStatement stm = (IDoWhileStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.DO_WHILE_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, 18);
		
		assertEquals("true", stm.getCondition().toString());
		
		assertPosition(stm.getBody(), 4, 3);
		
		assertVisitor(stm, 4);
	}
	
	public void testLabel() {
		String s = " label: break;";
		ILabelStatement stm = (ILabelStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.LABEL_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, 13);
		
		assertEquals("label", stm.getName().toString());
		assertPosition(stm.getName(), 1, 5);
		
		assertPosition(stm.getStatement(), 8, 6);
		
		assertVisitor(stm, 3);
	}
	
	public void testStaticAssertStatement() {
		String s = " static assert(1, true);";
		IStaticAssertStatement stm = (IStaticAssertStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.STATIC_ASSERT_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, 23);
		
		assertEquals("1", stm.getExpression().toString());
		assertEquals("true", stm.getMessage().toString());
		
		assertVisitor(stm, 3);
	}
	
	public void testStaticAssertDeclaration() {
		String s = " static assert(1, true);";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		
		assertEquals(1, unit.getDeclarationDefinitions().length);
		
		IStaticAssertDeclaration stm = (IStaticAssertDeclaration) unit.getDeclarationDefinitions()[0];
		assertEquals(IElement.STATIC_ASSERT, stm.getElementType());
		assertPosition(stm, 1, 23);
		
		assertEquals("1", stm.getExpression().toString());
		assertEquals("true", stm.getMessage().toString());
		
		assertVisitor(stm, 3);
	}
	
	public void testWith() {
		String s = " with(true) { }";
		IWithStatement stm = (IWithStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.WITH_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, 14);
		
		assertPosition(stm.getStatement(), 12, 3);
		
		assertEquals("true", stm.getExpression().toString());
		
		assertVisitor(stm, 4);
	}
	
	public void testFor() {
		String s = " for(int i = 0; i < 10; i++) { }";
		IScopeStatement scope = (IScopeStatement) new ParserFacade().parseStatement(s);
		IForStatement stm = (IForStatement) scope.getStatement();
		
		assertEquals(IStatement.FOR_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
		assertPosition(scope, 1, s.length() - 1);
	}
	
	public void testForEmpty() {
		String s = " for(;;) { }";
		IForStatement stm = (IForStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.FOR_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testForeach() {
		String s = " foreach(inout a, b, c; x) { }";
		IForeachStatement stm = (IForeachStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.FOREACH_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertFalse(stm.isReverse());
		
		IArgument[] args = stm.getArguments();
		assertEquals(3, args.length);
		
		assertPosition(args[0], 9, 7);
		assertEquals(IArgument.INOUT, args[0].getKind());
		assertEquals("a", args[0].getName().toString());
		assertPosition(args[0].getName(), 15, 1);
		
		assertPosition(args[1], 18, 1);
		assertEquals(IArgument.IN, args[1].getKind());
		assertEquals("b", args[1].getName().toString());
		assertPosition(args[1].getName(), 18, 1);
		
		assertPosition(args[2], 21, 1);
		assertEquals(IArgument.IN, args[2].getKind());
		assertEquals("c", args[2].getName().toString());
		assertPosition(args[2].getName(), 21, 1);
		
		assertVisitor(stm, 9);
	}
	
	public void testForeachReverse() {
		String s = " foreach_reverse(a, b, c; x) { }";
		IForeachStatement stm = (IForeachStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.FOREACH_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("x", stm.getIterable().toString());
		
		assertTrue(stm.isReverse());
	}
	
	public void testForeachWithDeclaration() {
		String s = " foreach(int x; y) { }";
		IForeachStatement stm = (IForeachStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.FOREACH_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertFalse(stm.isReverse());
		
		IArgument[] args = stm.getArguments();
		assertEquals(1, args.length);
		
		assertPosition(args[0], 9, 5);
		assertEquals(IArgument.IN, args[0].getKind());
		assertEquals("x", args[0].getName().toString());
		assertEquals("int", args[0].getType().toString());
		assertPosition(args[0].getName(), 13, 1);
	}
	
	public void testVolatile() {
		String s = " volatile int x = 2;";
		IVolatileStatement stm = (IVolatileStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.VOLATILE_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testSwitch() {
		String s = " switch(1) { case 1: default: }";
		ISwitchStatement stm = (ISwitchStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.SWITCH_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("1", stm.getExpression().toString());
	}
	
	public void testTryFinally() {
		String s = " try { } finally { }";
		ITryStatement stm = (ITryStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.TRY_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testTryCatchFinally() {
		String s = " try { } catch(Bla b) { } catch { } finally { }";
		ITryStatement stm = (ITryStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.TRY_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals(2, stm.getCatches().length);
		assertPosition(stm.getCatches()[0], 9, 16);
	}
	
	public void testThrow() {
		String s = " throw 1;";
		IThrowStatement stm = (IThrowStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.THROW_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("1", stm.getExpression().toString());
	}
	
	public void testSynchronized1() {
		String s = " synchronized x = 2;";
		ISynchronizedStatement stm = (ISynchronizedStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.SYNCHRONIZED_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testSynchronized2() {
		String s = " synchronized(1) x = 2;";
		ISynchronizedStatement stm = (ISynchronizedStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.SYNCHRONIZED_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testOnScope() {
		Object[][] objs = {
				{ "exit", IOnScopeStatement.ON_SCOPE_EXIT },
				{ "failure", IOnScopeStatement.ON_SCOPE_FAILURE },
				{ "success", IOnScopeStatement.ON_SCOPE_SUCCESS },
		};
		
		for(Object[] obj : objs) {
			String s = " scope(" + obj[0] + ") x = 2;";
			IOnScopeStatement stm = (IOnScopeStatement) new ParserFacade().parseStatement(s);
			
			assertEquals(IStatement.ON_SCOPE_STATEMENT, stm.getElementType());
			assertPosition(stm, 1, s.length() - 1);
			
			assertEquals(obj[1], stm.getOnScopeType());
		}
	}
	
	public void testOnScope2() {
		Object[][] objs = {
				{ "exit", IOnScopeStatement.ON_SCOPE_EXIT },
				{ "failure", IOnScopeStatement.ON_SCOPE_FAILURE },
				{ "success", IOnScopeStatement.ON_SCOPE_SUCCESS },
		};
		
		for(Object[] obj : objs) {
			String s = " on_scope_" + obj[0] + " { }";
			IOnScopeStatement stm = (IOnScopeStatement) new ParserFacade().parseStatement(s);
			
			assertEquals(IStatement.ON_SCOPE_STATEMENT, stm.getElementType());
			assertPosition(stm, 1, s.length() - 1);
			
			assertEquals(obj[1], stm.getOnScopeType());
		}
	}
	
	public void testGoto() {
		String s = " goto bla;";
		IGotoStatement stm = (IGotoStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.GOTO_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertPosition(stm.getLabel(), 6, 3);
	}
	
	public void testGotoDefault() {
		String s = " goto default;";
		IStatement stm = (IStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.GOTO_DEFAULT_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testGotoCase() {
		String s = " goto case bla;";
		IGotoCaseStatement stm = (IGotoCaseStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.GOTO_CASE_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
	}
	
	public void testPragma() {
		String s = " pragma(lib, 1);";
		IPragmaStatement stm = (IPragmaStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.PRAGMA_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("lib", stm.getIdentifier().toString());
		assertPosition(stm.getIdentifier(), 8, 3);
	}
	
	public void testIf() {
		String s = " if (1) { } else { }";
		IIfStatement stm = (IIfStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.IF_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("1", stm.getCondition().toString());
		assertNull(stm.getArgument());
	}
	
	public void testIfAuto() {
		String s = " if (auto x = 1) { } else { }";
		IIfStatement stm = (IIfStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.IF_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("1", stm.getCondition().toString());
		assertNotNull(stm.getArgument());
		assertEquals("x", stm.getArgument().getName().toString());
		assertPosition(stm.getArgument(), 5, 6);
	}
	
	public void testIfDeclaration() {
		String s = " if (int x = 1) { } else { }";
		IIfStatement stm = (IIfStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.IF_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("1", stm.getCondition().toString());
		
		IArgument argument = stm.getArgument();
		assertNotNull(argument);
		assertEquals("x", argument.getName().toString());
		assertEquals("int", argument.getType().toString());
		assertPosition(argument, 5, 5);
	}
	
	public void testIfDeprecated() {
		String s = " if (a; b) { } else { }";
		IIfStatement stm = (IIfStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IStatement.IF_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("b", stm.getCondition().toString());
		
		IArgument argument = stm.getArgument();
		assertNotNull(argument);
		assertEquals("a", argument.getName().toString());
		assertPosition(argument, 5, 1);
	}
	
	public void testStaticIf() {
		String s = " static if (1) { } else { }";
		IStaticIfStatement stm = (IStaticIfStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IConditionalStatement.STATIC_IF_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("1", stm.getCondition().toString());
	}
	
	public void testDebug1() {
		String s = " debug { } else { }";
		IDebugStatement stm = (IDebugStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IConditionalStatement.DEBUG_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertNull(stm.getDebug());
	}
	
	public void testDebug2() {
		String s = " debug(1) { }";
		IDebugStatement stm = (IDebugStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IConditionalStatement.DEBUG_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("1", stm.getDebug().toString());
	}
	
	public void testVersion() {
		String s = " version(release) { } else { }";
		IVersionStatement stm = (IVersionStatement) new ParserFacade().parseStatement(s);
		
		assertEquals(IConditionalStatement.VERSION_STATEMENT, stm.getElementType());
		assertPosition(stm, 1, s.length() - 1);
		
		assertEquals("release", stm.getVersion().toString());
	}
	
	public void testStaticExtern() {
		String s = " static extern int x;";
		IDeclarationStatement stm = (IDeclarationStatement) new ParserFacade().parseStatement(s);
		
		IVariableDeclaration var = (IVariableDeclaration) stm.getDeclaration();
		
		assertTrue((var.getModifiers() & IModifier.EXTERN) != 0);
	}
	
	public void testTypeof() {
		String s = " typeof(2) x;";
		IDeclarationStatement stm = (IDeclarationStatement) new ParserFacade().parseStatement(s);
		
		IVariableDeclaration var = (IVariableDeclaration) stm.getDeclaration();
		assertEquals(IType.TYPEOF_TYPE, var.getType().getElementType());
	}
	
	public void testAggregate() {
		String s = " class X { };";
		IDeclarationStatement stm = (IDeclarationStatement) new ParserFacade().parseStatement(s);
		
		IAggregateDeclaration var = (IAggregateDeclaration) stm.getDeclaration();
		assertEquals(IAggregateDeclaration.CLASS_DECLARATION, var.getAggregateDeclarationType());
	}
	
	public void testEnum() {
		String s = " enum X { a };";
		IDeclarationStatement stm = (IDeclarationStatement) new ParserFacade().parseStatement(s);
		
		IEnumDeclaration var = (IEnumDeclaration) stm.getDeclaration();
		assertNotNull(var);
	}
	
	public void testMixin() {
		String s = " mixin X x;";
		IDeclarationStatement stm = (IDeclarationStatement) new ParserFacade().parseStatement(s);
		
		IMixinDeclaration var = (IMixinDeclaration) stm.getDeclaration();
		assertNotNull(var);
	}
	
	public void testScope() {
		String s = " scope x;";
		IDeclarationStatement stm = (IDeclarationStatement) new ParserFacade().parseStatement(s);
		
		IVariableDeclaration var = (IVariableDeclaration) stm.getDeclaration();
		assertNotNull(var);
		assertTrue((var.getModifiers() & IModifier.SCOPE) != 0);
	}

}
