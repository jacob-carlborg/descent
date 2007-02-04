package descent.tests.rewrite;

import descent.core.dom.DebugAssignment;
import descent.core.dom.Modifier;

public class RewriteDebugAssignmentTest extends AbstractRewriteTest {
	
	public void testAddPreDDoc() throws Exception {
		begin(" debug = 2;");
		
		DebugAssignment align = (DebugAssignment) unit.declarations().get(0);
		align.preDDocs().add(ast.newDDocComment("/** Some comment */\n"));
		
		assertEqualsTokenByToken("/** Some comment */ debug = 2;", end());
	}
	
	public void testAddModifiers() throws Exception {
		begin(" debug = 2;");
		
		DebugAssignment align = (DebugAssignment) unit.declarations().get(0);
		align.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		align.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD));
		
		assertEqualsTokenByToken("public abstract debug = 2;", end());
	}
	
	public void testRemoveModifier() throws Exception {
		begin(" public abstract debug = 2;");
		
		DebugAssignment align = (DebugAssignment) unit.declarations().get(0);
		align.modifiers().get(0).delete();
		
		assertEqualsTokenByToken("abstract debug = 2;", end());
	}
	
	public void testChangeVersion() throws Exception {
		begin(" debug = 2;");
		
		DebugAssignment align = (DebugAssignment) unit.declarations().get(0);
		align.setVersion(ast.newVersion("8"));
		
		assertEqualsTokenByToken("debug = 8;", end());
	}
	
	public void testAddPostDDoc() throws Exception {
		begin(" debug = 2;");
		
		DebugAssignment align = (DebugAssignment) unit.declarations().get(0);
		align.setPostDDoc(ast.newDDocComment("/// hello!"));
		
		assertEqualsTokenByToken("debug = 2; /// hello!", end());
	}
	
	public void testRemovePostDDoc() throws Exception {
		begin(" debug = 2; /// hello!");
		
		DebugAssignment align = (DebugAssignment) unit.declarations().get(0);
		align.getPostDDoc().delete();
		
		assertEqualsTokenByToken("debug = 2;", end());
	}

}
