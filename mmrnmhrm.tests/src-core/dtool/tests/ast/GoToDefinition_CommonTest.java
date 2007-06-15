package dtool.tests.ast;

import java.io.IOException;

import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.tests.BaseTestClass;
import dtool.dom.ast.ASTElementFinder;
import dtool.dom.base.Entity;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;

//@RunWith(Parameterized.class)
public class GoToDefinition_CommonTest extends BaseTestClass {

	static int counter = -666;

	CompilationUnit cunit;
	Module module;

	
	int defOffset; 
    int refOffset;
	
	public GoToDefinition_CommonTest(int defOffset, int refOffset, String testfile) throws IOException {
		this.defOffset = defOffset;
		this.refOffset = refOffset;
		
		cunit = testCUparsing(getTestDataFileString(testfile));
		System.out.println("==== Source length: "+cunit.source.length()+" ====");
		module = cunit.getNeoModule();	
	}
	
	

	protected void assertGoToReF(int refOffset, int defOffset) {
		counter++;
		System.out.print("Searching case #"+counter+": "+refOffset+" :");
		System.out.println(cunit.source.substring(refOffset).split("\\s")[0]);
		Entity ent = (Entity) ASTElementFinder.findElement(module, refOffset);
		DefUnit defunit = ent.getTargetDefUnit();
		assertTrue(defunit.defname.startPos == defOffset, 
				"Go To Ref didn't go to the right offset\n" +
				" Case #"+counter+"  "+refOffset+"->"+defOffset+
				" Got:"+defunit.startPos);
	}

}