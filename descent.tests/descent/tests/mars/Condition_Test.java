package descent.tests.mars;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IConditionalDeclaration;
import descent.core.dom.IDebugDeclaration;
import descent.core.dom.IElement;
import descent.core.dom.IIftypeDeclaration;
import descent.core.dom.IStaticIfDeclaration;
import descent.core.dom.IConditionAssignment;
import descent.core.dom.IVersionDeclaration;
import descent.internal.core.dom.ParserFacade;

public class Condition_Test extends Parser_Test {
	
	public void testVersionString() {
		String s = " version(linux) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVersionDeclaration version = (IVersionDeclaration) declDefs[0];
		assertPosition(version, 1, 18);
		
		assertEquals("linux", version.getVersion().toString());
		assertPosition(version.getVersion(), 9, 5);
	}
	
	public void testVersionNumber() {
		String s = " version(1) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVersionDeclaration version = (IVersionDeclaration) declDefs[0];
		assertPosition(version, 1, 14);
		
		assertEquals("1", version.getVersion().toString());
		assertPosition(version.getVersion(), 9, 1);
	}
	
	public void testVersionElse() {
		String s = " version(Windows) { } else { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVersionDeclaration version = (IVersionDeclaration) declDefs[0];
		assertPosition(version, 1, s.length() - 1);
	}
	
	public void testDebug() {
		String s = " debug(bla) { int x; } else { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IDebugDeclaration d = (IDebugDeclaration) declDefs[0];
		assertEquals(IConditionalDeclaration.DEBUG_DECLARATION, d.getNodeType0());
		assertPosition(d, 1, s.length() - 1);
		
		assertEquals("bla", d.getDebug().toString());
		assertPosition(d.getDebug(), 7, 3);
	}
	
	public void testStaticIf() {
		String s = " static if(true) { int x; } else { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IStaticIfDeclaration d = (IStaticIfDeclaration) declDefs[0];
		assertEquals(IConditionalDeclaration.STATIC_IF_DECLARATION, d.getNodeType0());
		assertPosition(d, 1, s.length() - 1);
		
		assertEquals("true", d.getCondition().toString());
	}
	
	public void testVersionAssign() {
		String s = " version = 2;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IConditionAssignment v = (IConditionAssignment) declDefs[0];
		assertEquals(IElement.CONDITION_ASSIGNMENT, v.getNodeType0());
		assertEquals(IConditionAssignment.CONDITION_VERSION, v.getConditionAssignmentType());
		assertEquals("2", v.getValue().toString());
		assertPosition(v.getValue(), 11, 1);
		assertPosition(v, 1, s.length() - 1);
	}
	
	public void testVersionAssign2() {
		String s = " version = some;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IConditionAssignment v = (IConditionAssignment) declDefs[0];
		assertEquals(IElement.CONDITION_ASSIGNMENT, v.getNodeType0());
		assertEquals("some", v.getValue().toString());
		assertPosition(v.getValue(), 11, 4);
		assertPosition(v, 1, s.length() - 1);
	}
	
	public void testDebugAssign() {
		String s = " debug = 2;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IConditionAssignment v = (IConditionAssignment) declDefs[0];
		assertEquals(IElement.CONDITION_ASSIGNMENT, v.getNodeType0());
		assertEquals(IConditionAssignment.CONDITION_DEBUG, v.getConditionAssignmentType());
		assertEquals("2", v.getValue().toString());
		assertPosition(v.getValue(), 9, 1);
		assertPosition(v, 1, s.length() - 1);
	}
	
	public void testDebugAssign2() {
		String s = " debug = some;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IConditionAssignment v = (IConditionAssignment) declDefs[0];
		assertEquals(IElement.CONDITION_ASSIGNMENT, v.getNodeType0());
		assertEquals("some", v.getValue().toString());
		assertPosition(v.getValue(), 9, 4);
		assertPosition(v, 1, s.length() - 1);
	}
	
	public void testIftypeNone() {
		String s = " iftype(x) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IIftypeDeclaration d = (IIftypeDeclaration) declDefs[0];
		assertEquals(IConditionalDeclaration.IFTYPE_DECLARATION, d.getNodeType0());
		
		assertEquals(IIftypeDeclaration.IFTYPE_NONE, d.getIftypeCondition());
		assertEquals("x", d.getTestType().toString());
		assertNull(d.getIdentifier());
		assertNull(d.getMatchingType());
		
		assertPosition(d, 1, s.length() - 1);
	}
	
	public void testIftypeEquals() {
		String s = " iftype(x == y) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IIftypeDeclaration d = (IIftypeDeclaration) declDefs[0];
		assertEquals(IConditionalDeclaration.IFTYPE_DECLARATION, d.getNodeType0());
		
		assertEquals(IIftypeDeclaration.IFTYPE_EQUALS, d.getIftypeCondition());
		assertEquals("x", d.getTestType().toString());
		assertNull(d.getIdentifier());
		assertEquals("y", d.getMatchingType().toString());
		
		assertPosition(d, 1, s.length() - 1);
	}
	
	public void testIftypeExtends() {
		String s = " iftype(x : y) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IIftypeDeclaration d = (IIftypeDeclaration) declDefs[0];
		assertEquals(IConditionalDeclaration.IFTYPE_DECLARATION, d.getNodeType0());
		
		assertEquals(IIftypeDeclaration.IFTYPE_EXTENDS, d.getIftypeCondition());
		assertEquals("x", d.getTestType().toString());
		assertNull(d.getIdentifier());
		assertEquals("y", d.getMatchingType().toString());
		
		assertPosition(d, 1, s.length() - 1);
	}
	
	public void testIftypeWithIdentifier() {
		String s = " iftype(int x : y) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IIftypeDeclaration d = (IIftypeDeclaration) declDefs[0];
		assertEquals(IConditionalDeclaration.IFTYPE_DECLARATION, d.getNodeType0());
		
		assertEquals(IIftypeDeclaration.IFTYPE_EXTENDS, d.getIftypeCondition());
		assertEquals("int", d.getTestType().toString());
		assertEquals("x", d.getIdentifier().toString());
		assertPosition(d.getIdentifier(), 12, 1);
		assertEquals("y", d.getMatchingType().toString());
		
		assertPosition(d, 1, s.length() - 1);
	}
	
}
