package descent.internal.compiler.lookup;

import java.util.List;

import descent.core.ICompilationUnit;
import descent.core.IPackageDeclaration;
import descent.core.JavaModelException;
import descent.internal.compiler.parser.IModule;
import descent.internal.compiler.parser.IModuleDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.core.util.Util;

public class RModule extends RPackage implements IModule {

	private IModule importedFrom;
	private IModuleDeclaration md;
	private boolean mdCalculated;

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
	
	@Override
	public IdentifierExp ident() {
		if (ident == null) {
			// Remove extension
			String name = element.getElementName();
			ident = new IdentifierExp(name.substring(0, name.lastIndexOf('.')).toCharArray());
		}
		return ident;
	}

	public IModule importedFrom() {
		return importedFrom;
	}

	public void importedFrom(IModule module) {
		this.importedFrom = module;
	}
	
	public IModuleDeclaration md() {
		if (!mdCalculated) {
			ICompilationUnit unit = (ICompilationUnit) element;
			IPackageDeclaration[] pkgs;
			try {
				pkgs = unit.getPackageDeclarations();
				if (pkgs.length > 0) {
					md = new RModuleDeclaration(pkgs[0]);	
				}
			} catch (JavaModelException e) {
				Util.log(e);
			}
			
			mdCalculated = true;
		}
		return md;
	}

	public boolean needmoduleinfo() {
		// TODO Auto-generated method stub
		return false;
	}

	public void needmoduleinfo(boolean value) {
		// TODO Auto-generated method stub
		
	}

	public int semanticdone() {
		return 3;
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
