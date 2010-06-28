package descent.tests.mars;

import descent.core.compiler.IftypeDeclarationKind;
import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.BooleanLiteral;
import descent.core.dom.DebugAssignment;
import descent.core.dom.DebugDeclaration;
import descent.core.dom.IftypeDeclaration;
import descent.core.dom.SimpleType;
import descent.core.dom.StaticIfDeclaration;
import descent.core.dom.VersionAssignment;
import descent.core.dom.VersionDeclaration;

public class Condition_Test extends Parser_Test {
	
	public void testVersionString() {
		String s = " version(linux) { }";
		VersionDeclaration version = (VersionDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(version, 1, 18);
		
		assertEquals("linux", version.getVersion().getValue());
		assertPosition(version.getVersion(), 9, 5);
	}
	
	public void testVersionNumber() {
		String s = " version(1) { }";
		VersionDeclaration version = (VersionDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(version, 1, 14);
		
		assertEquals("1", version.getVersion().getValue());
		assertPosition(version.getVersion(), 9, 1);
	}
	
	public void testVersionElse() {
		String s = " version(Windows) { } else { }";
		VersionDeclaration version = (VersionDeclaration) getSingleDeclarationNoProblems(s);
		assertPosition(version, 1, s.length() - 1);
	}
	
	public void testDebug() {
		String s = " debug(bla) { int x; } else { }";
		DebugDeclaration d = (DebugDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.DEBUG_DECLARATION, d.getNodeType());
		assertPosition(d, 1, s.length() - 1);
		
		assertEquals("bla", d.getVersion().getValue());
		assertPosition(d.getVersion(), 7, 3);
	}
	
	public void testStaticIf() {
		String s = " static if(true) { int x; } else { }";
		StaticIfDeclaration d = (StaticIfDeclaration) getSingleDeclarationNoProblems(s);
		assertEquals(ASTNode.STATIC_IF_DECLARATION, d.getNodeType());
		assertPosition(d, 1, s.length() - 1);
		
		assertTrue(((BooleanLiteral) d.getExpression()).booleanValue());
	}
	
	public void testVersionAssign() {
		String s = " version = 2;";
		VersionAssignment va = (VersionAssignment) getSingleDeclarationNoProblems(s);
		assertEquals("2", va.getVersion().getValue().toString());
		assertPosition(va.getVersion(), 11, 1);
		assertPosition(va, 1, s.length() - 1);
	}
	
	public void testVersionAssign2() {
		String s = " version = some;";
		VersionAssignment va = (VersionAssignment) getSingleDeclarationNoProblems(s);
		assertEquals("some", va.getVersion().getValue().toString());
		assertPosition(va.getVersion(), 11, 4);
		assertPosition(va, 1, s.length() - 1);
	}
	
	public void testDebugAssign() {
		String s = " debug = 2;";
		DebugAssignment da = (DebugAssignment) getSingleDeclarationNoProblems(s);
		assertEquals("2", da.getVersion().getValue().toString());
		assertPosition(da.getVersion(), 9, 1);
		assertPosition(da, 1, s.length() - 1);
	}
	
	public void testDebugAssign2() {
		String s = " debug = some;";
		DebugAssignment da = (DebugAssignment) getSingleDeclarationNoProblems(s);
		assertEquals("some", da.getVersion().getValue().toString());
		assertPosition(da.getVersion(), 9, 4);
		assertPosition(da, 1, s.length() - 1);
	}
	
	public void testIftypeNone() {
		String s = " iftype(x) { }";
		IftypeDeclaration d = (IftypeDeclaration) getSingleDeclarationWithProblems(s, 1, AST.D0); // iftype deprecated
		assertEquals(ASTNode.IFTYPE_DECLARATION, d.getNodeType());
		
		assertEquals(IftypeDeclarationKind.NONE, d.getKind());
		assertEquals("x", ((SimpleType) d.getTestType()).getName().getFullyQualifiedName());
		assertNull(d.getName());
		assertNull(d.getMatchingType());
		
		assertPosition(d, 1, s.length() - 1);
	}
	
	public void testIftypeEquals() {
		String s = " iftype(x == y) { }";
		IftypeDeclaration d = (IftypeDeclaration) getSingleDeclarationWithProblems(s, 1, AST.D0); // iftype deprecated
		assertEquals(ASTNode.IFTYPE_DECLARATION, d.getNodeType());
		
		assertEquals(IftypeDeclarationKind.EQUALS, d.getKind());
		assertEquals("x", ((SimpleType) d.getTestType()).getName().getFullyQualifiedName());
		assertNull(d.getName());
		assertEquals("y", ((SimpleType) d.getMatchingType()).getName().getFullyQualifiedName());
		
		assertPosition(d, 1, s.length() - 1);
	}
	
	public void testIftypeExtends() {
		String s = " iftype(x : y) { }";
		IftypeDeclaration d = (IftypeDeclaration) getSingleDeclarationWithProblems(s, 1, AST.D0); // iftype deprecated
		assertEquals(ASTNode.IFTYPE_DECLARATION, d.getNodeType());
		
		assertEquals(IftypeDeclarationKind.EXTENDS, d.getKind());
		assertEquals("x", ((SimpleType) d.getTestType()).getName().getFullyQualifiedName());
		assertNull(d.getName());
		assertEquals("y", ((SimpleType) d.getMatchingType()).getName().getFullyQualifiedName());		
		assertPosition(d, 1, s.length() - 1);
	}
	
	public void testIftypeWithIdentifier() {
		String s = " iftype(int x : y) { }";
		IftypeDeclaration d = (IftypeDeclaration) getSingleDeclarationWithProblems(s, 1, AST.D0); // iftype deprecated
		assertEquals(ASTNode.IFTYPE_DECLARATION, d.getNodeType());
		
		assertEquals(IftypeDeclarationKind.EXTENDS, d.getKind());
		assertEquals("int", d.getTestType().toString());
		assertEquals("x", d.getName().getIdentifier());
		assertPosition(d.getName(), 12, 1);
		assertEquals("y", ((SimpleType) d.getMatchingType()).getName().getFullyQualifiedName());		
		assertPosition(d, 1, s.length() - 1);
	}
	
}
