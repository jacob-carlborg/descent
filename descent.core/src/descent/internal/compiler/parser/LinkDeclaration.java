package descent.internal.compiler.parser;

import java.util.List;

import org.eclipse.core.runtime.Assert;

public class LinkDeclaration extends AttribDeclaration {

	public LINK linkage;

	public LinkDeclaration(LINK linkage, List<Dsymbol> decl) {
		super(decl);
		this.linkage = linkage;
	}

	@Override
	public int getNodeType() {
		return LINK_DECLARATION;
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (decl != null && decl.size() > 0) {
			LINK linkage_save = sc.linkage;

			sc.linkage = linkage;
			for (Dsymbol s : decl) {
				s.semantic(sc, context);
			}
			sc.linkage = linkage_save;
		} else {
			sc.linkage = linkage;
		}
	}

	@Override
	public void semantic3(Scope sc, SemanticContext context) {
		if (decl != null && decl.size() > 0) {
			LINK linkage_save = sc.linkage;

			sc.linkage = linkage;
			for (Dsymbol s : decl) {
				s.semantic3(sc, context);
			}
			sc.linkage = linkage_save;
		} else {
			sc.linkage = linkage;
		}
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		LinkDeclaration ld;

		Assert.isNotNull(s);
		ld = new LinkDeclaration(linkage, Dsymbol.arraySyntaxCopy(decl));
		return ld;
	}

}
