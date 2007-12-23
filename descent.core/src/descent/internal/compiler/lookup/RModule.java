package descent.internal.compiler.lookup;

import java.util.List;

import descent.core.ICompilationUnit;
import descent.internal.compiler.parser.IModule;

public class RModule extends RPackage implements IModule {

	private IModule importedFrom;

	public RModule(ICompilationUnit unit) {
		super(unit);
	}

	public List<char[]> debugids() {
		// TODO Auto-generated method stub
		return null;
	}

	public void debugids(List<char[]> debugids) {
		// TODO Auto-generated method stub
		
	}

	public List<char[]> debugidsNot() {
		// TODO Auto-generated method stub
		return null;
	}

	public void debugidsNot(List<char[]> debugidsNot) {
		// TODO Auto-generated method stub
		
	}

	public long debuglevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void debuglevel(long debuglevel) {
		// TODO Auto-generated method stub
		
	}

	public IModule importedFrom() {
		return importedFrom;
	}

	public void importedFrom(IModule module) {
		this.importedFrom = module;
	}

	public boolean needmoduleinfo() {
		// TODO Auto-generated method stub
		return false;
	}

	public void needmoduleinfo(boolean value) {
		// TODO Auto-generated method stub
		
	}

	public int semanticdone() {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<char[]> versionids() {
		// TODO Auto-generated method stub
		return null;
	}

	public void versionids(List<char[]> versionids) {
		// TODO Auto-generated method stub
		
	}

	public List<char[]> versionidsNot() {
		// TODO Auto-generated method stub
		return null;
	}

	public void versionidsNot(List<char[]> versionidsNot) {
		// TODO Auto-generated method stub
		
	}

	public long versionlevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void versionlevel(long versionlevel) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public IModule isModule() {
		return this;
	}

}
