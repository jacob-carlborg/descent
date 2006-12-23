package descent.tests.mars;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IConditionalDeclaration;
import descent.core.dom.IDebugDeclaration;
import descent.core.dom.IElement;
import descent.core.dom.IIftypeDeclaration;
import descent.core.dom.IStaticIfDeclaration;
import descent.core.dom.IVersionDeclaration;
import descent.internal.core.dom.BooleanLiteral;
import descent.internal.core.dom.DebugAssignment;
import descent.internal.core.dom.IftypeDeclaration;
import descent.internal.core.dom.ParserFacade;
import descent.internal.core.dom.SimpleType;
import descent.internal.core.dom.VersionAssignment;

public class Condition_Test extends Parser_Test {
	
	public void testVersionString() {
		String s = " version(linux) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVersionDeclaration version = (IVersionDeclaration) declDefs[0];
		assertPosition(version, 1, 18);
		
		assertEquals("linux", version.getVersion().getValue());
		assertPosition(version.getVersion(), 9, 5);
	}
	
	public void testVersionNumber() {
		String s = " version(1) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVersionDeclaration version = (IVersionDeclaration) declDefs[0];
		assertPosition(version, 1, 14);
		
		assertEquals("1", version.getVersion().getValue());
		assertPosition(version.getVersion(), 9, 1);
	}
	
	public void testVersionElse() {
		String s = " version(Windows) { } else { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IVersionDeclaration version = (IVersionDeclaration) declDefs[0];
		assertPosition(version, 1, s.length() - 1);
	}
	
	public void testDebug() {
		String s = " debug(bla) { int x; } else { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IDebugDeclaration d = (IDebugDeclaration) declDefs[0];
		assertEquals(IConditionalDeclaration.DEBUG_DECLARATION, d.getNodeType0());
		assertPosition(d, 1, s.length() - 1);
		
		assertEquals("bla", d.getVersion().getValue());
		assertPosition(d.getVersion(), 7, 3);
	}
	
	public void testStaticIf() {
		String s = " static if(true) { int x; } else { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IStaticIfDeclaration d = (IStaticIfDeclaration) declDefs[0];
		assertEquals(IConditionalDeclaration.STATIC_IF_DECLARATION, d.getNodeType0());
		assertPosition(d, 1, s.length() - 1);
		
		assertTrue(((BooleanLiteral) d.getExpression()).booleanValue());
	}
	
	public void testVersionAssign() {
		String s = " version = 2;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		VersionAssignment va = (VersionAssignment) declDefs[0];
		assertEquals("2", va.getVersion().getValue().toString());
		assertPosition(va.getVersion(), 11, 1);
		assertPosition(va, 1, s.length() - 1);
	}
	
	public void testVersionAssign2() {
		String s = " version = some;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		VersionAssignment va = (VersionAssignment) declDefs[0];
		assertEquals("some", va.getVersion().getValue().toString());
		assertPosition(va.getVersion(), 11, 4);
		assertPosition(va, 1, s.length() - 1);
	}
	
	public void testDebugAssign() {
		String s = " debug = 2;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		DebugAssignment da = (DebugAssignment) declDefs[0];
		assertEquals("2", da.getVersion().getValue().toString());
		assertPosition(da.getVersion(), 9, 1);
		assertPosition(da, 1, s.length() - 1);
	}
	
	public void testDebugAssign2() {
		String s = " debug = some;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		DebugAssignment da = (DebugAssignment) declDefs[0];
		assertEquals("some", da.getVersion().getValue().toString());
		assertPosition(da.getVersion(), 9, 4);
		assertPosition(da, 1, s.length() - 1);
	}
	
	public void testIftypeNone() {
		String s = " iftype(x) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(1, unit.getProblems().length); // iftype deprecated
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IIftypeDeclaration d = (IIftypeDeclaration) declDefs[0];
		assertEquals(IConditionalDeclaration.IFTYPE_DECLARATION, d.getNodeType0());
		
		assertEquals(IftypeDeclaration.Kind.NONE, d.getKind());
		assertEquals("x", ((SimpleType) d.getTestType()).getName().getFullyQualifiedName());
		assertNull(d.getName());
		assertNull(d.getMatchingType());
		
		assertPosition(d, 1, s.length() - 1);
	}
	
	public void testIftypeEquals() {
		String s = " iftype(x == y) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(1, unit.getProblems().length); // iftype deprecated
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IIftypeDeclaration d = (IIftypeDeclaration) declDefs[0];
		assertEquals(IConditionalDeclaration.IFTYPE_DECLARATION, d.getNodeType0());
		
		assertEquals(IftypeDeclaration.Kind.EQUALS, d.getKind());
		assertEquals("x", ((SimpleType) d.getTestType()).getName().getFullyQualifiedName());
		assertNull(d.getName());
		assertEquals("y", ((SimpleType) d.getMatchingType()).getName().getFullyQualifiedName());
		
		assertPosition(d, 1, s.length() - 1);
	}
	
	public void testIftypeExtends() {
		String s = " iftype(x : y) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(1, unit.getProblems().length); // iftype deprecated
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IIftypeDeclaration d = (IIftypeDeclaration) declDefs[0];
		assertEquals(IConditionalDeclaration.IFTYPE_DECLARATION, d.getNodeType0());
		
		assertEquals(IftypeDeclaration.Kind.EXTENDS, d.getKind());
		assertEquals("x", ((SimpleType) d.getTestType()).getName().getFullyQualifiedName());
		assertNull(d.getName());
		assertEquals("y", ((SimpleType) d.getMatchingType()).getName().getFullyQualifiedName());		
		assertPosition(d, 1, s.length() - 1);
	}
	
	public void testIftypeWithIdentifier() {
		String s = " iftype(int x : y) { }";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(1, unit.getProblems().length); // iftype deprecated
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IIftypeDeclaration d = (IIftypeDeclaration) declDefs[0];
		assertEquals(IConditionalDeclaration.IFTYPE_DECLARATION, d.getNodeType0());
		
		assertEquals(IftypeDeclaration.Kind.EXTENDS, d.getKind());
		assertEquals("int", d.getTestType().toString());
		assertEquals("x", d.getName().getIdentifier());
		assertPosition(d.getName(), 12, 1);
		assertEquals("y", ((SimpleType) d.getMatchingType()).getName().getFullyQualifiedName());		
		assertPosition(d, 1, s.length() - 1);
	}
	
}
