package dtool.tests.ast;

import java.io.IOException;

import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.tests.BaseTestClass;

import org.junit.BeforeClass;
import org.junit.Test;

import dtool.dom.ast.ASTElementFinder;
import dtool.dom.base.Entity;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;

public class GoToDefinition_Test extends BaseTestClass  {
	
	static CompilationUnit cunit;
	static Module module;
	
	int counter = 0;
	
	@BeforeClass
	public static void globalSetup() throws IOException {
		cunit = testCUparsing(readStringFromResource("gotodef.d", GoToDefinition_Test.class));
		System.out.println("==== Source length: "+cunit.source.length()+" ====");
		module = cunit.getNeoModule();
	}
	
	@Test
	public void test() {
		assertGoToReF(211,  50);
		assertGoToReF(253, 900);
		assertGoToReF(411,  50);
		assertGoToReF(457, 363);
		assertGoToReF(507, 204);
		assertGoToReF(568, 952);

	}

	private void assertGoToReF(int refOffset, int defOffset) {
		counter++;
		System.out.println("Searching: "+refOffset+":"+cunit.source.substring(refOffset, refOffset+20));
		Entity ent = (Entity) ASTElementFinder.findElement(module, refOffset);
		DefUnit defunit = ent.getTargetDefUnit();
		assertTrue(defunit.startPos == defOffset, 
				"Go To Ref didn't go to the right offset\n" +
				" Case #"+counter+"  "+refOffset+"->"+defOffset+
				" Got:"+defunit.startPos);
	}
}
