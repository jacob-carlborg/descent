package descent.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.List;

import descent.core.Flags;
import descent.core.ICompilationUnit;
import descent.core.IImportDeclaration;
import descent.core.IJavaElement;
import descent.core.IPackageDeclaration;
import descent.core.IParent;
import descent.core.ISourceReference;
import descent.core.JavaModelException;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.IDsymbol;
import descent.internal.compiler.parser.IModule;
import descent.internal.compiler.parser.IModuleDeclaration;
import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Import;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.Module;
import descent.internal.compiler.parser.Parser;
import descent.internal.compiler.parser.ProtDeclaration;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.StorageClassDeclaration;
import descent.internal.core.util.Util;

public class RModule extends RPackage implements IModule {

	private IModule importedFrom;
	private IModuleDeclaration md;
	private boolean mdCalculated;
	private String signature;
	
	/*
	 * The list of modules publicly imported by this module.
	 */
	private List<IModule> publiclyImportedModules;

	public RModule(ICompilationUnit unit, SemanticContext context) {
		super(unit, context);
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
			ident = new IdentifierExp(((ICompilationUnit) element).getModuleName().toCharArray());
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
					md = new RModuleDeclaration(pkgs[0], context);
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
	
	@Override
	public IDsymbol search(Loc loc, char[] ident, int flags, SemanticContext context) {
		IDsymbol sym = super.search(loc, ident, flags, context);
		if (sym != null) {
			return sym;
		}
		
		if (publiclyImportedModules == null) {
			publiclyImportedModules = new ArrayList<IModule>();
			try {
				searchInPublicImports((IParent) element);
			} catch (JavaModelException e) {
				Util.log(e);
			}
		}
		
		for(IModule module : publiclyImportedModules) {
			sym = module.search(loc, ident, flags, context);
			if (sym != null) {
				return sym;
			}
		}
		
		return null;
	}
	
	private void searchInPublicImports(IParent parent) throws JavaModelException {
		for(IJavaElement child : parent.getChildren()) {
			if (child.getElementType() == IJavaElement.IMPORT_CONTAINER) {
				searchInPublicImports((IParent) child);
			} else if (child.getElementType() == IJavaElement.IMPORT_DECLARATION) {
				IImportDeclaration imp = (IImportDeclaration) child;
				if (Flags.isPublic(imp.getFlags())) {
					// Make sure the import is not selective, because that
					// currently doesn't work as public in DMD
					String name = imp.getElementName();
					if (name.indexOf(':') == -1 && name.indexOf('=') == -1) {
						String[] compoundNameS = name.split("\\.");
						char[][] compoundName = CharOperation.stringArrayToCharArray(compoundNameS);
						
						IModule mod;
						
						// This check is here to avoid loading twice the same module
						if (context.moduleFinder.isLoaded(compoundName)) {
							mod = context.moduleFinder.findModule(compoundName, context);
						} else {
							mod = context.load(compoundName);
						}
						if (mod != null) {
							publiclyImportedModules.add(mod);
						}
					}
				}
			}
		}
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
	
	/**
	 * Materializes an ISourceReference element.
	 */
	public Dsymbol materialize(ISourceReference r) {
		try {
			// We build the function from the source in order to interpret it
			// But we have to also include in the source:
			// - the original imports
			// - an import to this function's module, in order to not resolve
			//   again what is already resolved.
			
			int importCount = 1;
			
			// Build import statement to my module
			RModule rmodule = (RModule) getModule();
			ICompilationUnit unit = (ICompilationUnit) rmodule.element;				
			
			StringBuilder fullSource = new StringBuilder();
			fullSource.append("import ");
			fullSource.append(unit.getFullyQualifiedName());
			fullSource.append(";");
			
			// Now append this module's imports
			for(IDsymbol s : rmodule.members()) {
				Import imp = s.isImport();
				if (imp != null) {
					fullSource.append("import ");
					fullSource.append(imp.toString());
					fullSource.append(";");
					importCount++;
				}
			}
			
			fullSource.append(r.getSource());
			
			Parser parser = new Parser(Util.getApiLevel(element), fullSource.toString());
			parser.nextToken();
			Module m = parser.parseModuleObj();
			m.ident(getModule().ident());
			
			Dsymbol sym = (Dsymbol) m.members.get(importCount);
			
			m.semantic(context);
			
			while(sym instanceof StorageClassDeclaration) {
				sym = (Dsymbol) ((StorageClassDeclaration) sym).decl.get(0);
			}
			while(sym instanceof ProtDeclaration) {
				sym = (Dsymbol) ((ProtDeclaration) sym).decl.get(0);
			}
			
			return sym;
		} catch (JavaModelException e) {
			Util.log(e);
		}
		
		return null;
	}
	
	@Override
	public String getSignature() {
		if (signature == null) {
			StringBuilder sb = new StringBuilder();
			appendSignature(sb);
			signature = sb.toString();
		}
		return signature;
	}
	
	@Override
	public void appendSignature(StringBuilder sb) {
		sb.append(ISignatureConstants.MODULE);
		String[] pieces = ((ICompilationUnit) element).getFullyQualifiedName().split("\\.");
		for(String piece : pieces) {
			sb.append(piece.length());
			sb.append(piece);
		}
	}

}
