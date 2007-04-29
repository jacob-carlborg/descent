package dtool.project;

import java.io.File;
import java.util.List;

import util.ExceptionAdapter;
import util.FileUtil;
import dtool.dom.declarations.DefUnit;
import dtool.model.BindingResolver;
import dtool.model.IScope;
import dtool.model.ModelException;

public class DToolProject implements IScope {

	public CompilationUnit testcu;
	private BindingResolver bresolver = new BindingResolver(this);
	
	public static DToolProject newTestProject() {
		DToolProject dproj = new DToolProject();
		try {
			File file = new File("testinput/test.d");
			String source = FileUtil.readStringFromFile(file);
			dproj.testcu = new CompilationUnit(source);
			dproj.testcu.file = file;
			
			System.out.println(">> read: " + dproj.testcu.file + " ");
			//System.out.println(testcu.source);
			
		} catch (Exception e) {
			throw ExceptionAdapter.unchecked(e);
		}
		return dproj;
	}

	public List<DefUnit> getDefUnits() {
		return testcu.getNeoModule().getDefUnits();
	}
	
	public DefUnit findEntity(String string) throws ModelException {
		return bresolver.findEntity(string);
	}

	/*public ASTNode findEntity(CompilationUnit cunit, int offset) {
		AssertIn.isTrue(offset < cunit.source.length());
		ASTNode elem = ASTElementFinder.findElement(cunit.getModule(), offset);
		return elem;
	}*/

}
