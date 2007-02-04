package descent.tests.rewrite;

import descent.core.dom.AsmBlock;
import descent.core.dom.AsmStatement;

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

}
