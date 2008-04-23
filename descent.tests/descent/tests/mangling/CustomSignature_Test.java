package descent.tests.mangling;

import descent.core.dom.CompilationUnitResolver;
import descent.internal.compiler.env.ICompilationUnit;
import descent.internal.compiler.parser.AssignExp;
import descent.internal.compiler.parser.CallExp;
import descent.internal.compiler.parser.ClassDeclaration;
import descent.internal.compiler.parser.CompoundStatement;
import descent.internal.compiler.parser.DotVarExp;
import descent.internal.compiler.parser.EnumDeclaration;
import descent.internal.compiler.parser.ExpStatement;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.StructDeclaration;
import descent.internal.compiler.parser.TemplateDeclaration;
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
		ClassDeclaration cd = type.sym;
		assertEquals("Foo", new String(cd.ident.ident));
		
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
		ClassDeclaration cd = type.sym;
		assertEquals("Foo", new String(cd.ident.ident));
		
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
		StructDeclaration cd = type.sym;
		assertEquals("Foo", new String(cd.ident.ident));
		
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
		StructDeclaration cd = type.sym;
		assertEquals("Foo", new String(cd.ident.ident));
		
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
		EnumDeclaration cd = type.sym;
		assertEquals("Foo", new String(cd.ident.ident));
		
		assertEquals(one.getAllTypes()[0], cd.getJavaElement());
		assertEquals(one.getAllTypes()[0], type.getJavaElement());
		assertEquals(MODULE + "3one" + ENUM + "3Foo", type.getSignature());
	}
	
	public void testFunction() throws Exception {
		one("");
		two("void foo() { }");
		
		Module module = CompilationUnitResolver.resolve(javaProject.getApiLevel(), 
				(ICompilationUnit) two, javaProject, null, null, true, null).module;
		FuncDeclaration func = (FuncDeclaration) module.members.get(2);
		assertEquals(MODULE + "3two" + FUNCTION + "3fooFZv", func.getSignature());
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
		VarDeclaration var = (VarDeclaration) ve.var;
		TypeClass type = (TypeClass) var.type;
		ClassDeclaration cd = type.sym;
		TemplateInstance ti = (TemplateInstance) cd.parent;
		assertEquals(1, ti.tiargs.size());
		assertSame(TypeBasic.tint32, ti.tiargs.get(0));
		TemplateDeclaration tempdecl = ti.tempdecl;
		Module imodule = (Module) tempdecl.parent;
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
		VarDeclaration var = (VarDeclaration) ve.var;
		TypeClass type = (TypeClass) var.type;
		ClassDeclaration cd = type.sym;
		TemplateInstance ti = (TemplateInstance) cd.parent;
		assertEquals(1, ti.tiargs.size());
		assertSame(TypeBasic.tint32, ti.tiargs.get(0));
		TemplateDeclaration tempdecl = ti.tempdecl;
		Module imodule = (Module) tempdecl.parent;
		assertNotNull(imodule);
	}
	
	public void testMixedTemplateVariable() throws Exception {
		one("template Foo() { int someProperty; }");
		two("class Bar { mixin Foo!(); } void foo(Bar bar) { bar.someProperty = 3; }");
		
		Module module = CompilationUnitResolver.resolve(javaProject.getApiLevel(), 
				(ICompilationUnit) two, javaProject, null, null, true, null).module;
		FuncDeclaration func = (FuncDeclaration) module.members.get(3);
		CompoundStatement cs = (CompoundStatement) func.fbody;
		ExpStatement ex = (ExpStatement) cs.statements.get(0);
		AssignExp ae = (AssignExp) ex.exp;
		DotVarExp ve = (DotVarExp) ae.e1;
		VarDeclaration var = (VarDeclaration) ve.var;
		assertEquals(MODULE + "3one" + TEMPLATE + "3Foo" + TEMPLATE_PARAMETERS_BREAK + TEMPLATE_INSTANCE + TEMPLATE_PARAMETERS_BREAK + VARIABLE + "12someProperty", var.getSignature());
	}
	
	public void testMixedTemplateFunction() throws Exception {
		one("template Foo() { void someFunction() { } }");
		two("class Bar { mixin Foo!(); } void foo(Bar bar) { bar.someFunction(); }");
		
		Module module = CompilationUnitResolver.resolve(javaProject.getApiLevel(), 
				(ICompilationUnit) two, javaProject, null, null, true, null).module;
		FuncDeclaration func = (FuncDeclaration) module.members.get(3);
		CompoundStatement cs = (CompoundStatement) func.fbody;
		ExpStatement ex = (ExpStatement) cs.statements.get(0);
		CallExp call = (CallExp) ex.exp;
		DotVarExp ve = (DotVarExp) call.e1;
		FuncDeclaration var = (FuncDeclaration) ve.var;
		assertEquals(MODULE + "3one" + TEMPLATE + "3Foo" + TEMPLATE_PARAMETERS_BREAK + TEMPLATE_INSTANCE + TEMPLATE_PARAMETERS_BREAK + FUNCTION + "12someFunctionFZv", var.getSignature());
	}

}
