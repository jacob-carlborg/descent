package descent.tests.mars;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IConditionalDeclaration;
import descent.core.dom.IDebugDeclaration;
import descent.core.dom.IDElement;
import descent.core.dom.IStaticIfDeclaration;
import descent.core.dom.IConditionAssignment;
import descent.core.dom.IVersionDeclaration;
import descent.internal.core.dom.ParserFacade;

public class Condition_Test extends Parser_Test {
	
	public void testVersionString() {
		String s = " version(linux) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVersionDeclaration version = (IVersionDeclaration) declDefs[0];
		assertPosition(version, 1, 18);
		
		assertEquals("linux", version.getVersion().toString());
		assertPosition(version.getVersion(), 9, 5);
	}
	
	public void testVersionNumber() {
		String s = " version(1) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVersionDeclaration version = (IVersionDeclaration) declDefs[0];
		assertPosition(version, 1, 14);
		
		assertEquals("1", version.getVersion().toString());
		assertPosition(version.getVersion(), 9, 1);
	}
	
	public void testVersionElse() {
		String s = " version(Windows) { } else { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVersionDeclaration version = (IVersionDeclaration) declDefs[0];
		assertPosition(version, 1, s.length() - 1);
	}
	
	public void testDebug() {
		String s = " debug(bla) { int x; } else { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IDebugDeclaration d = (IDebugDeclaration) declDefs[0];
		assertEquals(IDElement.CONDITIONAL_DECLARATION, d.getElementType());
		assertEquals(IConditionalDeclaration.CONDITIONAL_DEBUG, d.getConditionalDeclarationType());
		assertPosition(d, 1, s.length() - 1);
		
		assertEquals("bla", d.getDebug().toString());
		assertPosition(d.getDebug(), 7, 3);
	}
	
	public void testStaticIf() {
		String s = " static if(true) { int x; } else { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IStaticIfDeclaration d = (IStaticIfDeclaration) declDefs[0];
		assertEquals(IDElement.CONDITIONAL_DECLARATION, d.getElementType());
		assertEquals(IConditionalDeclaration.CONDITIONAL_STATIC_IF, d.getConditionalDeclarationType());
		assertPosition(d, 1, s.length() - 1);
		
		assertEquals("true", d.getCondition().toString());
	}
	
	public void testVersionAssign() {
		String s = " version = 2;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IConditionAssignment v = (IConditionAssignment) declDefs[0];
		assertEquals(IDElement.CONDITION_ASSIGNMENT, v.getElementType());
		assertEquals(IConditionAssignment.CONDITION_VERSION, v.getConditionAssignmentType());
		assertEquals("2", v.getValue().toString());
		assertPosition(v.getValue(), 11, 1);
		assertPosition(v, 1, s.length() - 1);
	}
	
	public void testVersionAssign2() {
		String s = " version = some;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IConditionAssignment v = (IConditionAssignment) declDefs[0];
		assertEquals(IDElement.CONDITION_ASSIGNMENT, v.getElementType());
		assertEquals("some", v.getValue().toString());
		assertPosition(v.getValue(), 11, 4);
		assertPosition(v, 1, s.length() - 1);
	}
	
	public void testDebugAssign() {
		String s = " debug = 2;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IConditionAssignment v = (IConditionAssignment) declDefs[0];
		assertEquals(IDElement.CONDITION_ASSIGNMENT, v.getElementType());
		assertEquals(IConditionAssignment.CONDITION_DEBUG, v.getConditionAssignmentType());
		assertEquals("2", v.getValue().toString());
		assertPosition(v.getValue(), 9, 1);
		assertPosition(v, 1, s.length() - 1);
	}
	
	public void testDebugAssign2() {
		String s = " debug = some;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IDElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IConditionAssignment v = (IConditionAssignment) declDefs[0];
		assertEquals(IDElement.CONDITION_ASSIGNMENT, v.getElementType());
		assertEquals("some", v.getValue().toString());
		assertPosition(v.getValue(), 9, 4);
		assertPosition(v, 1, s.length() - 1);
	}
	
}
