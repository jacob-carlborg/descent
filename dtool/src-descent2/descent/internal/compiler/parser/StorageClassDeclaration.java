package descent.internal.compiler.parser;

import java.util.List;
import static descent.internal.compiler.parser.TOK.*;
import static descent.internal.compiler.parser.STC.*;

import org.eclipse.core.runtime.Assert;

public class StorageClassDeclaration extends AttribDeclaration {

	static class SCstring {
		int stc;
		TOK tok;

		public SCstring(int stc, TOK tok) {
			this.stc = stc;
			this.tok = tok;
		}
	}

	static SCstring[] table = { new SCstring(STCauto, TOKauto),
			new SCstring(STCscope, TOKscope),
			new SCstring(STCstatic, TOKstatic),
			new SCstring(STCextern, TOKextern),
			new SCstring(STCconst, TOKconst), new SCstring(STCfinal, TOKfinal),
			new SCstring(STCabstract, TOKabstract),
			new SCstring(STCsynchronized, TOKsynchronized),
			new SCstring(STCdeprecated, TOKdeprecated),
			new SCstring(STCoverride, TOKoverride), };

	public boolean single;
	public int stc;
	public Modifier modifier;
	public List<Modifier> modifiers;

	public StorageClassDeclaration(Loc loc, int stc, List<Dsymbol> decl,
			Modifier modifier, boolean single) {
		super(loc, decl);
		this.stc = stc;
		this.single = single;
		this.modifier = modifier;
	}

	@Override
	public int getNodeType() {
		return STORAGE_CLASS_DECLARATION;
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
	public Dsymbol syntaxCopy(Dsymbol s) {
		StorageClassDeclaration scd;

		Assert.isNotNull(s);
		scd = new StorageClassDeclaration(loc, stc, Dsymbol.arraySyntaxCopy(decl),
				modifier, single);
		return scd;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		boolean written = false;
		for (SCstring sc : table) {
			if ((stc & sc.stc) != 0) {
				if (written) {
					buf.writeByte(' ');
				}
				written = true;
				buf.writestring(sc.tok.toString());
			}
		}

		super.toCBuffer(buf, hgs, context);
	}

}
