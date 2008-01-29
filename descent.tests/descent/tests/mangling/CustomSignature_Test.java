package descent.tests.mangling;

import descent.core.dom.CompilationUnitResolver;
import descent.internal.compiler.env.ICompilationUnit;
import descent.internal.compiler.parser.AssignExp;
import descent.internal.compiler.parser.CompoundStatement;
import descent.internal.compiler.parser.ExpStatement;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.IClassDeclaration;
import descent.internal.compiler.parser.IEnumDeclaration;
import descent.internal.compiler.parser.IModule;
import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.IStructDeclaration;
import descent.internal.compiler.parser.ITemplateDeclaration;
import descent.internal.compiler.parser.IVarDeclaration;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.TemplateInstance;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeBasic;
import descent.internal.compiler.parser.TypeClass;
import descent.internal.compiler.parser.TypeEnum;
import descent.internal.compiler.parser.TypeStruct;
import descent.internal.compiler.parser.VarDeclaration;
import descent.internal.compiler.parser.VarExp;
import descent.tests.lookup.AbstractLookupTest;

public class CustomSignature_Test extends AbstractLookupTest implements ISignatureConstants {
	
	public void testClass() throws Exception {
		one("class Foo { }");
		two("Foo x;");
		
		Module module = CompilationUnitResolver.resolve(javaProject.getApiLevel(), 
				(ICompilationUnit) two, javaProject, null, null, true, null).module;
		VarDeclaration var = (VarDeclaration) module.members.get(2);
		TypeClass type = (TypeClass) var.type;
		IClassDeclaration cd = type.sym;
		assertEquals("Foo", new String(cd.ident().ident));
		
		assertEquals(one.getAllTypes()[0], cd.getJavaElement());
		assertEquals(one.getAllTypes()[0], type.getJavaElement());
		assertEquals(MODULE + "3one" + CLASS + "3Foo", type.getSignature());
	}
	
	public void testInterface() throws Exception {
		one("interface Foo { }");
		two("Foo x;");
		
		Module module = CompilationUnitResolver.resolve(javaProject.getApiLevel(), 
				(ICompilationUnit) two, javaProject, null, null, true, null).module;
		VarDeclaration var = (VarDeclaration) module.members.get(2);
		TypeClass type = (TypeClass) var.type;
		IClassDeclaration cd = type.sym;
		assertEquals("Foo", new String(cd.ident().ident));
		
		assertEquals(one.getAllTypes()[0], cd.getJavaElement());
		assertEquals(one.getAllTypes()[0], type.getJavaElement());
		assertEquals(MODULE + "3one" + INTERFACE + "3Foo", type.getSignature());
	}
	
	public void testStruct() throws Exception {
		one("struct Foo { }");
		two("Foo x;");
		
		Module module = CompilationUnitResolver.resolve(javaProject.getApiLevel(), 
				(ICompilationUnit) two, javaProject, null, null, true, null).module;
		VarDeclaration var = (VarDeclaration) module.members.get(2);
		TypeStruct type = (TypeStruct) var.type;
		IStructDeclaration cd = type.sym;
		assertEquals("Foo", new String(cd.ident().ident));
		
		assertEquals(one.getAllTypes()[0], cd.getJavaElement());
		assertEquals(one.getAllTypes()[0], type.getJavaElement());
		assertEquals(MODULE + "3one" + STRUCT + "3Foo", type.getSignature());
	}
	
	public void testUnion() throws Exception {
		one("union Foo { }");
		two("Foo x;");
		
		Module module = CompilationUnitResolver.resolve(javaProject.getApiLevel(), 
				(ICompilationUnit) two, javaProject, null, null, true, null).module;
		VarDeclaration var = (VarDeclaration) module.members.get(2);
		TypeStruct type = (TypeStruct) var.type;
		IStructDeclaration cd = type.sym;
		assertEquals("Foo", new String(cd.ident().ident));
		
		assertEquals(one.getAllTypes()[0], cd.getJavaElement());
		assertEquals(one.getAllTypes()[0], type.getJavaElement());
		assertEquals(MODULE + "3one" + UNION + "3Foo", type.getSignature());
	}
	
	public void testEnum() throws Exception {
		one("enum Foo { x }");
		two("Foo x;");
		
		Module module = CompilationUnitResolver.resolve(javaProject.getApiLevel(), 
				(ICompilationUnit) two, javaProject, null, null, true, null).module;
		VarDeclaration var = (VarDeclaration) module.members.get(2);
		TypeEnum type = (TypeEnum) var.type;
		IEnumDeclaration cd = type.sym;
		assertEquals("Foo", new String(cd.ident().ident));
		
		assertEquals(one.getAllTypes()[0], cd.getJavaElement());
		assertEquals(one.getAllTypes()[0], type.getJavaElement());
		assertEquals(MODULE + "3one" + ENUM + "3Foo", type.getSignature());
	}
	
	public void testTemplatedClass() throws Exception {
		one("class Foo() { }");
		two("Foo!() x;");
		
		Module module = CompilationUnitResolver.resolve(javaProject.getApiLevel(), 
				(ICompilationUnit) two, javaProject, null, null, true, null).module;
		VarDeclaration var = (VarDeclaration) module.members.get(2);
		Type type = (Type) var.type;
		assertEquals(MODULE + "3one" + TEMPLATED_CLASS + "3Foo" + TEMPLATE_PARAMETERS_BREAK + TEMPLATE_INSTANCE + TEMPLATE_PARAMETERS_BREAK, type.getSignature());
	}
	
	public void testTemplatedClass2() throws Exception {
		one("class Foo(T) { }");
		two("Foo!(int) x;");
		
		Module module = CompilationUnitResolver.resolve(javaProject.getApiLevel(), 
				(ICompilationUnit) two, javaProject, null, null, true, null).module;
		VarDeclaration var = (VarDeclaration) module.members.get(2);
		Type type = (Type) var.type;
		assertEquals(MODULE + "3one" + TEMPLATED_CLASS + "3Foo" + TEMPLATE_TYPE_PARAMETER + TEMPLATE_PARAMETERS_BREAK + TEMPLATE_INSTANCE + TEMPLATE_INSTANCE_TYPE + "i" + TEMPLATE_PARAMETERS_BREAK, type.getSignature());
	}
	
	public void testTemplatedClass3() throws Exception {
		one("class Foo(T) { T prop; } Foo!(int) x;");
		two("void foo() { x = null; }");
		
		Module module = CompilationUnitResolver.resolve(javaProject.getApiLevel(), 
				(ICompilationUnit) two, javaProject, null, null, true, null).module;
		FuncDeclaration func = (FuncDeclaration) module.members.get(2);
		CompoundStatement cs = (CompoundStatement) func.fbody;
		ExpStatement ex = (ExpStatement) cs.statements.get(0);
		AssignExp ae = (AssignExp) ex.exp;
		VarExp ve = (VarExp) ae.e1;
		IVarDeclaration var = (IVarDeclaration) ve.var;
		TypeClass type = (TypeClass) var.type();
		IClassDeclaration cd = type.sym;
		TemplateInstance ti = (TemplateInstance) cd.parent();
		assertEquals(1, ti.tiargs.size());
		assertSame(TypeBasic.tint32, ti.tiargs.get(0));
		ITemplateDeclaration tempdecl = ti.tempdecl;
		IModule imodule = (IModule) tempdecl.parent();
		assertNotNull(imodule);
	}
	
	public void testNestedTemplatedClass3() throws Exception {
		one("template Bar(U) { class Foo(T) { T prop; } Bar!(char[]).Foo!(int) x; }");
		two("void foo() { x = null; }");
		
		Module module = CompilationUnitResolver.resolve(javaProject.getApiLevel(), 
				(ICompilationUnit) two, javaProject, null, null, true, null).module;
		FuncDeclaration func = (FuncDeclaration) module.members.get(2);
		CompoundStatement cs = (CompoundStatement) func.fbody;
		ExpStatement ex = (ExpStatement) cs.statements.get(0);
		AssignExp ae = (AssignExp) ex.exp;
		VarExp ve = (VarExp) ae.e1;
		IVarDeclaration var = (IVarDeclaration) ve.var;
		TypeClass type = (TypeClass) var.type();
		IClassDeclaration cd = type.sym;
		TemplateInstance ti = (TemplateInstance) cd.parent();
		assertEquals(1, ti.tiargs.size());
		assertSame(TypeBasic.tint32, ti.tiargs.get(0));
		ITemplateDeclaration tempdecl = ti.tempdecl;
		IModule imodule = (IModule) tempdecl.parent();
		assertNotNull(imodule);
	}

}