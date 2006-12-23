package descent.tests.mars;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.IMixinDeclaration;
import descent.internal.core.dom.ParserFacade;
import descent.internal.core.dom.QualifiedType;
import descent.internal.core.dom.SimpleType;
import descent.internal.core.dom.TemplateType;
import descent.internal.core.dom.TypeofType;

public class Mixin_Test extends Parser_Test {
	
	public void testWithoutNot() {
		String s = " mixin Foo m;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IMixinDeclaration m = (IMixinDeclaration) declDefs[0];
		assertEquals(IElement.MIXIN_DECLARATION, m.getNodeType0());
		
		assertEquals("m", m.getName().getFullyQualifiedName());
		assertPosition(m.getName(), s.length() - 2, 1);
		
		assertPosition(m, 1, s.length() - 1);
		
		SimpleType type = (SimpleType) m.getType();
		assertEquals("Foo", type.getName().getFullyQualifiedName());
		assertPosition(type, 7, 3);
		assertPosition(type.getName(), 7, 3);
	}
	
	public void testExpressionParameter() {
		String s = " mixin Foo!(2) m;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IMixinDeclaration m = (IMixinDeclaration) declDefs[0];
		assertEquals(IElement.MIXIN_DECLARATION, m.getNodeType0());
		
		assertEquals("m", m.getName().getFullyQualifiedName());
		assertPosition(m.getName(), s.length() - 2, 1);
		
		assertPosition(m, 1, s.length() - 1);
		
		TemplateType type = (TemplateType) m.getType();
		assertEquals("Foo", type.getName().getFullyQualifiedName());
		assertEquals(1, type.arguments().size());
	}
	
	public void testDot() {
		String s = " mixin .Foo!(int) m;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IMixinDeclaration m = (IMixinDeclaration) declDefs[0];
		assertEquals(IElement.MIXIN_DECLARATION, m.getNodeType0());
		
		assertEquals("m", m.getName().getFullyQualifiedName());
		assertPosition(m.getName(), s.length() - 2, 1);
		
		assertPosition(m, 1, s.length() - 1);
		
		QualifiedType type = (QualifiedType) m.getType();
		
		assertNull(type.getQualifier());
		TemplateType type2 = (TemplateType) type.getType();
		assertEquals("Foo", type2.getName().getFullyQualifiedName());
		assertEquals(1, type2.arguments().size());
	}
	
	public void testTwo() {
		String s = " mixin a.Foo!(int) m;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IMixinDeclaration m = (IMixinDeclaration) declDefs[0];
		assertEquals(IElement.MIXIN_DECLARATION, m.getNodeType0());
		
		assertEquals("m", m.getName().getFullyQualifiedName());
		assertPosition(m.getName(), s.length() - 2, 1);
		
		assertPosition(m, 1, s.length() - 1);
		
		QualifiedType type = (QualifiedType) m.getType();
		
		TemplateType type2 = (TemplateType) type.getType();
		assertEquals("Foo", type2.getName().getFullyQualifiedName());
		assertEquals(1, type2.arguments().size());
		
		SimpleType type3 = (SimpleType) type.getQualifier();
		assertEquals("a", type3.getName().getFullyQualifiedName());
	}
	
	public void testThree() {
		String s = " mixin a.b.Foo!(int) m;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IMixinDeclaration m = (IMixinDeclaration) declDefs[0];
		assertEquals(IElement.MIXIN_DECLARATION, m.getNodeType0());
		
		assertEquals("m", m.getName().getFullyQualifiedName());
		assertPosition(m.getName(), s.length() - 2, 1);
		
		assertPosition(m, 1, s.length() - 1);
		
		QualifiedType type = (QualifiedType) m.getType();
		
		TemplateType type2 = (TemplateType) type.getType();
		assertEquals("Foo", type2.getName().getFullyQualifiedName());
		assertEquals(1, type2.arguments().size());
		
		QualifiedType type3 = (QualifiedType) type.getQualifier();
		
		SimpleType type4 = (SimpleType) type3.getType();
		assertEquals("b", type4.getName().getFullyQualifiedName());
		
		SimpleType type5 = (SimpleType) type3.getQualifier();
		assertEquals("a", type5.getName().getFullyQualifiedName());
	}
	
	public void testTypeof() {
		String s = " mixin typeof(2).Foo!(int) m;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IMixinDeclaration m = (IMixinDeclaration) declDefs[0];
		assertEquals(IElement.MIXIN_DECLARATION, m.getNodeType0());
		
		assertEquals("m", m.getName().getFullyQualifiedName());
		assertPosition(m.getName(), s.length() - 2, 1);
		
		assertPosition(m, 1, s.length() - 1);
		
		QualifiedType type = (QualifiedType) m.getType();
		
		TemplateType type2 = (TemplateType) type.getType();
		assertEquals("Foo", type2.getName().getFullyQualifiedName());
		assertEquals(1, type2.arguments().size());
		
		TypeofType type3 = (TypeofType) type.getQualifier();
		assertPosition(type3, 7, 9);
	}
	
	public void testDotAfterTemplate() {
		String s = " mixin Foo!(int).bar m;";
		ICompilationUnit unit = new ParserFacade().parseCompilationUnit(s);
		assertEquals(0, unit.getProblems().length);
		IElement[] declDefs = unit.getDeclarationDefinitions();
		assertEquals(1, declDefs.length);
		
		IMixinDeclaration m = (IMixinDeclaration) declDefs[0];
		assertEquals(IElement.MIXIN_DECLARATION, m.getNodeType0());
		
		assertEquals("m", m.getName().getFullyQualifiedName());
		assertPosition(m.getName(), s.length() - 2, 1);
		
		assertPosition(m, 1, s.length() - 1);
		
		QualifiedType type = (QualifiedType) m.getType();
		
		SimpleType type2 = (SimpleType) type.getType();
		assertEquals("bar", type2.getName().getFullyQualifiedName());
		
		TemplateType type3 = (TemplateType) type.getQualifier();
		assertPosition(type3, 7, 9);
		assertEquals("Foo", type3.getName().getFullyQualifiedName());
		assertEquals(1, type3.arguments().size());
	}

}
