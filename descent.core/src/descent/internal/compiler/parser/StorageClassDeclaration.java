package descent.internal.compiler.parser;

import java.util.List;

public class StorageClassDeclaration extends AttribDeclaration {

	public boolean single;
	public int stc;
	public Modifier modifier;
	public List<Modifier> modifiers;

	public StorageClassDeclaration(int stc, List<Dsymbol> decl, Modifier modifier, boolean single) {
		super(decl);
		this.stc = stc;
		this.single = single;
		this.modifier = modifier;
	}

	@Override
	public int kind() {
		return STORAGE_CLASS_DECLARATION;
	}

}
