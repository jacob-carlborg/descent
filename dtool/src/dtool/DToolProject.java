package dtool;

import java.io.File;
import java.util.List;

import util.ExceptionAdapter;
import util.FileUtil;
import util.tree.IElement;
import dtool.dom.definitions.DefUnit;
import dtool.model.BindingResolver;
import dtool.model.DToolCompilationUnit;
import dtool.model.IScope;
import dtool.model.ModelException;

public class DToolProject implements IScope {

	public DToolCompilationUnit testcu;
	private BindingResolver bresolver = new BindingResolver();
	
	public static DToolProject newTestProject() {
		DToolProject dproj = new DToolProject();
		try {
			File file = new File("testinput/test.d");
			String source = FileUtil.readStringFromFile(file);
			dproj.testcu = new DToolCompilationUnit(source);
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

	public IElement[] getChildren() {
		return null;
	}

	public int getElementType() {
		return 0;
	}

	public IElement getParent() {
		return null;
	}

	public boolean hasChildren() {
		return false;
	}

}
