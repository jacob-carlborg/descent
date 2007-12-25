package descent.internal.compiler.lookup;

import descent.core.IPackageDeclaration;
import descent.core.compiler.CharOperation;
import descent.internal.compiler.parser.IModuleDeclaration;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Identifiers;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.SemanticMixin;

public class RModuleDeclaration extends RNode implements IModuleDeclaration {
	
	private IdentifierExp id;
	private Identifiers packages;
	
	public RModuleDeclaration(IPackageDeclaration element, SemanticContext context) {
		super(element, context);
	}

	public IdentifierExp id() {
		calculateIdAndPackages();
		return id;
	}

	public Identifiers packages() {
		calculateIdAndPackages();
		return packages;
	}
	
	@Override
	public String toChars(SemanticContext context) {
		return SemanticMixin.toChars(this, context);
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
