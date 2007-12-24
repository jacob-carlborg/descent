package descent.internal.compiler.lookup;

import descent.core.IPackageDeclaration;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.IModuleDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Identifiers;

public class RModuleDeclaration extends RNode implements IModuleDeclaration {
	
	private IPackageDeclaration element;
	private IdentifierExp id;
	private Identifiers packages;
	
	public RModuleDeclaration(IPackageDeclaration element) {
		this.element = element;
	}

	public IdentifierExp id() {
		calculateIdAndPackages();
		return id;
	}

	public Identifiers packages() {
		calculateIdAndPackages();
		return packages;
	}
	
	private void calculateIdAndPackages() {
		if (id == null) {
			String name = element.getElementName();
			char[][] pieces = CharOperation.splitOn('.', name.toCharArray());
			if (pieces.length > 0) {
				packages = new Identifiers();
				for(int i = 0; i < pieces.length - 1; i++) {
					packages.add(new IdentifierExp(pieces[i]));
				}
			}
			id = new IdentifierExp(pieces[pieces.length - 1]);
		}
	}

}
