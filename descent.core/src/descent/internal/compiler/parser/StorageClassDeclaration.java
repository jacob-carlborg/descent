package descent.internal.compiler.parser;

import java.util.List;

import org.eclipse.core.runtime.Assert;

public class StorageClassDeclaration extends AttribDeclaration {

	public boolean single;
	public int stc;
	public Modifier modifier;
	public List<Modifier> modifiers;

	public StorageClassDeclaration(int stc, List<Dsymbol> decl,
			Modifier modifier, boolean single) {
		super(decl);
		this.stc = stc;
		this.single = single;
		this.modifier = modifier;
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (decl != null && decl.size() > 0) {
			int stc_save = sc.stc;

			if ((stc & (STC.STCauto | STC.STCscope | STC.STCstatic | STC.STCextern)) != 0) {
				sc.stc &= ~(STC.STCauto | STC.STCscope | STC.STCstatic | STC.STCextern);
			}

			sc.stc |= stc;

			for (Dsymbol s : decl) {
				s.semantic(sc, context);
			}

			sc.stc = stc_save;
		} else {
			sc.stc = stc;
		}
	}

	@Override
	public int getNodeType() {
		return STORAGE_CLASS_DECLARATION;
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		StorageClassDeclaration scd;

		Assert.isNotNull(s);
		scd = new StorageClassDeclaration(stc, Dsymbol.arraySyntaxCopy(decl),
				modifier, single);
		return scd;
	}

}
