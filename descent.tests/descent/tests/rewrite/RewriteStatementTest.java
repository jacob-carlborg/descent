package descent.tests.rewrite;

import descent.core.dom.Argument;
import descent.core.dom.AsmBlock;
import descent.core.dom.AsmStatement;
import descent.core.dom.Block;
import descent.core.dom.CatchClause;
import descent.core.dom.DebugStatement;
import descent.core.dom.ExpressionStatement;
import descent.core.dom.ForStatement;
import descent.core.dom.ForeachStatement;
import descent.core.dom.TryStatement;

public class RewriteStatementTest extends AbstractRewriteTest {
	
	public void testAsmBlockAddStatement() throws Exception {
		AsmBlock block = (AsmBlock) beginStatement("asm { }");
		
		AsmStatement stm = ast.newAsmStatement();
		stm.tokens().add(ast.newAsmToken("mov"));
		stm.tokens().add(ast.newAsmToken("eax"));
		stm.tokens().add(ast.newAsmToken(","));
		stm.tokens().add(ast.newAsmToken("ebx"));
		
		block.statements().add(stm);
		
		assertStatementEqualsTokenByToken("asm { mov eax, ebx; }", end());
	}
	
	public void testAsmBlockRemoveStatement() throws Exception {
		AsmBlock block = (AsmBlock) beginStatement("asm { mov eax, ebx; }");
		block.statements().clear();
		assertStatementEqualsTokenByToken("asm { }", end());
	}
	
	public void testAsmBlockAddStatementToken() throws Exception {
		AsmBlock block = (AsmBlock) beginStatement("asm { mov eax, ; }");
		
		AsmStatement stm = (AsmStatement) block.statements().get(0);
		stm.tokens().add(ast.newAsmToken("ebx"));
		
		assertStatementEqualsTokenByToken("asm { mov eax, ebx; }", end());
	}
	
	public void testAsmBlockChangeStatementTokenToken() throws Exception {
		AsmBlock block = (AsmBlock) beginStatement("asm { mov eax, ebx; }");

		AsmStatement stm = (AsmStatement) block.statements().get(0);
		stm.tokens().get(0).setToken("lea");
		
		assertStatementEqualsTokenByToken("asm { lea eax, ebx; }", end());
	}
	
	public void testCatchClauseAddNameAndType() throws Exception {
		TryStatement tryStatement = (TryStatement) beginStatement("try { } catch { }");
		CatchClause clause = tryStatement.catchClauses().get(0);
		
		clause.setType(ast.newSimpleType(ast.newSimpleName("Exception")));
		clause.setName(ast.newSimpleName("e"));
		
		assertStatementEqualsTokenByToken("try { } catch (Exception e) { }", end());
	}
	
	public void testCatchClauseRemoveNameAndType() throws Exception {
		TryStatement tryStatement = (TryStatement) beginStatement("try { } catch(Exception e) { }");
		CatchClause clause = tryStatement.catchClauses().get(0);
		clause.getType().delete();
		clause.getName().delete();
		
		assertStatementEqualsTokenByToken("try { } catch { }", end());
	}
	
	public void testCatchClauseRemoveNameAndType2() throws Exception {
		TryStatement tryStatement = (TryStatement) beginStatement("try { } catch(int function() e) { }");
		CatchClause clause = tryStatement.catchClauses().get(0);
		clause.getType().delete();
		clause.getName().delete();
		
		assertStatementEqualsTokenByToken("try { } catch { }", end());
	}
	
	public void testCatchClauseChangeName() throws Exception {
		TryStatement tryStatement = (TryStatement) beginStatement("try { } catch(Exception e) { }");
		CatchClause clause = tryStatement.catchClauses().get(0);
		clause.setName(ast.newSimpleName("ex"));
		
		assertStatementEqualsTokenByToken("try { } catch(Exception ex) { }", end());
	}
	
	public void testCatchClauseChangeType() throws Exception {
		TryStatement tryStatement = (TryStatement) beginStatement("try { } catch(Exception e) { }");
		CatchClause clause = tryStatement.catchClauses().get(0);
		clause.setType(ast.newSimpleType(ast.newSimpleName("Throwable")));
		
		assertStatementEqualsTokenByToken("try { } catch(Throwable e) { }", end());
	}
	
	public void testCatchClauseChangeBody() throws Exception {
		TryStatement tryStatement = (TryStatement) beginStatement("try { } catch(Exception e) { }");
		CatchClause clause = tryStatement.catchClauses().get(0);
		
		Block block = ast.newBlock();		
		ExpressionStatement stm = ast.newExpressionStatement(ast.newSimpleName("doIt"));
		block.statements().add(stm);
		
		clause.setBody(block);
		
		assertStatementEqualsTokenByToken("try { } catch(Exception e) { doIt; }", end());
	}
	
	public void testDebugStatementAddVersion() throws Exception {
		DebugStatement stm = (DebugStatement) beginStatement("debug { }");
		stm.setVersion(ast.newVersion("v1"));
		
		assertStatementEqualsTokenByToken("debug(v1) { }", end()); 
	}
	
	public void testDebugStatementChangeVersion() throws Exception {
		DebugStatement stm = (DebugStatement) beginStatement("debug (v1) { }");
		stm.setVersion(ast.newVersion("xx22"));
		
		assertStatementEqualsTokenByToken("debug(xx22) { }", end()); 
	}
	
	public void testDebugStatementRemoveVersion() throws Exception {
		DebugStatement stm = (DebugStatement) beginStatement("debug (v1) { }");
		stm.getVersion().delete();
		
		assertStatementEqualsTokenByToken("debug { }", end()); 
	}
	
	public void testDebugStatementChangeThenBody() throws Exception {
		DebugStatement stm = (DebugStatement) beginStatement("debug { }");
		
		Block block = ast.newBlock();		
		ExpressionStatement stm2 = ast.newExpressionStatement(ast.newSimpleName("doIt"));
		block.statements().add(stm2);
		
		stm.setThenBody(block);
		
		assertStatementEqualsTokenByToken("debug { doIt; }", end()); 
	}
	
	public void testDebugStatementChangeThenBody2() throws Exception {
		DebugStatement stm = (DebugStatement) beginStatement("debug (v1) { }");
		
		Block block = ast.newBlock();		
		ExpressionStatement stm2 = ast.newExpressionStatement(ast.newSimpleName("doIt"));
		block.statements().add(stm2);
		
		stm.setThenBody(block);
		
		assertStatementEqualsTokenByToken("debug (v1) { doIt; }", end()); 
	}
	
	public void testDebugStatementAddElseBody() throws Exception {
		DebugStatement stm = (DebugStatement) beginStatement("debug { }");
		
		Block block = ast.newBlock();		
		ExpressionStatement stm2 = ast.newExpressionStatement(ast.newSimpleName("doIt"));
		block.statements().add(stm2);
		
		stm.setElseBody(block);
		
		assertStatementEqualsTokenByToken("debug { } else { doIt; }", end()); 
	}
	
	public void testDebugStatementAddElseBody2() throws Exception {
		DebugStatement stm = (DebugStatement) beginStatement("debug (v1) { }");
		
		Block block = ast.newBlock();		
		ExpressionStatement stm2 = ast.newExpressionStatement(ast.newSimpleName("doIt"));
		block.statements().add(stm2);
		
		stm.setElseBody(block);
		
		assertStatementEqualsTokenByToken("debug (v1) { } else { doIt; }", end()); 
	}
	
	public void testDebugStatementRemoveElseBody() throws Exception {
		DebugStatement stm = (DebugStatement) beginStatement("debug { } else { }");
		stm.getElseBody().delete();
		
		assertStatementEqualsTokenByToken("debug { }", end()); 
	}
	
	public void testDebugStatementRemoveElseBody2() throws Exception {
		DebugStatement stm = (DebugStatement) beginStatement("debug (v1) { } else { }");
		stm.getElseBody().delete();
		
		assertStatementEqualsTokenByToken("debug (v1) { }", end()); 
	}
	
	public void testDebugStatementChangeVersionValueAndElse() throws Exception {
		DebugStatement stm = (DebugStatement) beginStatement("debug (v1) { } else { }");
		stm.getVersion().setValue("v2");
		stm.getElseBody().delete();
		
		assertStatementEqualsTokenByToken("debug (v2) { }", end()); 
	}
	
	public void testForeachStatementChangeReverse() throws Exception {
		ForeachStatement stm = (ForeachStatement) beginStatement("foreach(x; y) { }");
		stm.setReverse(true);
		
		assertStatementEqualsTokenByToken("foreach_reverse(x; y) { }", end()); 
	}
	
	public void testForeachStatementChangeReverse2() throws Exception {
		ForeachStatement stm = (ForeachStatement) beginStatement("foreach_reverse(x; y) { }");
		stm.setReverse(false);
		
		assertStatementEqualsTokenByToken("foreach(x; y) { }", end()); 
	}
	
	public void testForeachAddArguments() throws Exception {
		ForeachStatement stm = (ForeachStatement) beginStatement("foreach(x; y) { }");
		
		Argument arg1 = ast.newArgument();
		arg1.setName(ast.newSimpleName("w"));
		
		stm.arguments().add(arg1);
		
		assertStatementEqualsTokenByToken("foreach(x, w; y) { }", end()); 
	}
	
	public void testForeachRemoveArguments() throws Exception {
		ForeachStatement stm = (ForeachStatement) beginStatement("foreach(x, w; y) { }");
		stm.arguments().get(1).delete();
		
		assertStatementEqualsTokenByToken("foreach(x; y) { }", end()); 
	}
	
	public void testForAddInitializer() throws Exception {
		ForStatement stm = (ForStatement) beginStatement("for(;;) { }");
		stm.setInitializer(ast.newExpressionStatement(ast.newSimpleName("a")));
		
		assertStatementEqualsTokenByToken("for(a;;) { }", end());
	}
	
	public void testForRemoveInitializer() throws Exception {
		ForStatement stm = (ForStatement) beginStatement("for(a;;) { }");
		stm.getInitializer().delete();
		
		assertStatementEqualsTokenByToken("for(;;) { }", end());
	}
	
	public void testForAddCondition() throws Exception {
		ForStatement stm = (ForStatement) beginStatement("for(;;) { }");
		stm.setCondition(ast.newSimpleName("a"));
		
		assertStatementEqualsTokenByToken("for(;a;) { }", end());
	}
	
	public void testForRemoveCondition() throws Exception {
		ForStatement stm = (ForStatement) beginStatement("for(;a;) { }");
		stm.getCondition().delete();
		
		assertStatementEqualsTokenByToken("for(;;) { }", end());
	}
	
	public void testForAddCondition2() throws Exception {
		ForStatement stm = (ForStatement) beginStatement("for(x;;) { }");
		stm.setCondition(ast.newSimpleName("a"));
		
		assertStatementEqualsTokenByToken("for(x;a;) { }", end());
	}
	
	public void testForRemoveCondition2() throws Exception {
		ForStatement stm = (ForStatement) beginStatement("for(x;a;) { }");
		stm.getCondition().delete();
		
		assertStatementEqualsTokenByToken("for(x;;) { }", end());
	}
	
	public void testForAddIncrement() throws Exception {
		ForStatement stm = (ForStatement) beginStatement("for(;;) { }");
		stm.setIncrement(ast.newSimpleName("a"));
		
		assertStatementEqualsTokenByToken("for(;;a) { }", end());
	}
	
	public void testForRemoveIncrement() throws Exception {
		ForStatement stm = (ForStatement) beginStatement("for(;;a) { }");
		stm.getIncrement().delete();
		
		assertStatementEqualsTokenByToken("for(;;) { }", end());
	}

}
