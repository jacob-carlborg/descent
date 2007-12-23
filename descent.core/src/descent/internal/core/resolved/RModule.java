package descent.internal.core.resolved;

import java.util.List;

import descent.core.ICompilationUnit;
import descent.internal.compiler.parser.IModule;

public class RModule extends RPackage implements IModule {

	private final ICompilationUnit unit;

	public RModule(ICompilationUnit unit) {
		this.unit = unit;
	}

	public List<char[]> debugids() {
		return null;
	}

	public void debugids(List<char[]> debugids) {
	}

	public List<char[]> debugidsNot() {
		return null;
	}

	public void debugidsNot(List<char[]> debugidsNot) {
	}

	public long debuglevel() {
		return 0;
	}

	public void debuglevel(long debuglevel) {
	}

	public IModule importedFrom() {
		return null;
	}

	public void importedFrom(IModule module) {
	}

	public boolean needmoduleinfo() {
		return false;
	}

	public void needmoduleinfo(boolean value) {
	}

	public int semanticdone() {
		return 0;
	}

	public List<char[]> versionids() {
		return null;
	}

	public void versionids(List<char[]> versionids) {
	}

	public List<char[]> versionidsNot() {
		return null;
	}

	public void versionidsNot(List<char[]> versionidsNot) {
	}

	public long versionlevel() {
		return 0;
	}

	public void versionlevel(long versionlevel) {
	}

}
