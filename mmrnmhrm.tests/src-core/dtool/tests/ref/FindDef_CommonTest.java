package dtool.tests.ref;

import static melnorme.miscutil.Assert.assertFail;
import static melnorme.miscutil.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;

import mmrnmhrm.core.dltk.ParsingUtil;
import mmrnmhrm.core.model.CompilationUnit;
import mmrnmhrm.tests.BasePluginTest;
import mmrnmhrm.tests.SampleMainProject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.junit.Test;

import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.references.Reference;
import dtool.refmodel.NodeUtil;

//@RunWith(Parameterized.class)
public abstract class FindDef_CommonTest extends BasePluginTest {

	public static int counter = -666;
	
	public static final String TEST_SRCFOLDER = SampleMainProject.TEST_SRC_REFS;
	

	protected static void staticTestInit(String testfile) {
		counter = -1;
		System.out.println("======== "+ testfile +" ========");
	}
	
	CompilationUnit cunit;
	ISourceModule modUnit;
	Module module;

	int offset; 
    int targetOffset;
	
	public FindDef_CommonTest(int offset, int targetOffset, String testfile) throws IOException, CoreException {
		this.offset = offset;
		this.targetOffset = targetOffset;
		//cunit = CoreTestUtils.testParseCUnit(TestUtils.readTestDataFile(testfile));
		cunit = SampleMainProject.getCompilationUnit(TEST_SRCFOLDER +"/"+ testfile);
		modUnit = cunit.modUnit;
		//System.out.println("==== Source length: "+cunit.source.length()+" ====");
		module = ParsingUtil.getNeoASTModule(cunit.modUnit);	
	}
	
	@Test
	public void test() throws ModelException {
		assertFindReF(cunit, offset, cunit, targetOffset);
	}
	
	public static void assertFindReF(CompilationUnit cunit, int offset,
			CompilationUnit targetCunit, int targetOffset) throws ModelException {
		ISourceModule modUnit2 = cunit.modUnit;

		counter++;
		System.out.print("Find ref case #"+counter+": "+offset+": ");
		System.out.println(modUnit2.getBuffer().getContents().substring(offset).split("\\s")[0]);
		
		ASTNeoNode node = ASTNodeFinder.findElement(ParsingUtil.getNeoASTModule(modUnit2), offset);

		Reference ref = (Reference) node;
		
		Collection<DefUnit> defunits = ref.findTargetDefUnits(true);
		
		if(defunits == null || defunits.isEmpty()) {
			if(targetOffset == -1)
				return; // Ok, it matches the expected
			assertFail(" Find Ref got no DefUnit.");
		}
		DefUnit defunit = defunits.iterator().next();
		
		assertTrue(defunit != null, " defunit = null");

		Module obtainedModule = NodeUtil.getParentModule(defunit);
		assertTrue(obtainedModule.getModuleUnit().equals(targetCunit.modUnit), " Find Ref got wrong target module.");
		
		assertTrue(defunit.defname.getStartPos() == targetOffset, " Find Ref went to wrong offset: " + defunit.defname.getStartPos());
	}
}