package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.STC.STCabstract;
import static descent.internal.compiler.parser.STC.STCauto;
import static descent.internal.compiler.parser.STC.STCconst;
import static descent.internal.compiler.parser.STC.STCdeprecated;
import static descent.internal.compiler.parser.STC.STCextern;
import static descent.internal.compiler.parser.STC.STCfinal;
import static descent.internal.compiler.parser.STC.STCoverride;
import static descent.internal.compiler.parser.STC.STCscope;
import static descent.internal.compiler.parser.STC.STCstatic;
import static descent.internal.compiler.parser.STC.STCsynchronized;

import static descent.internal.compiler.parser.TOK.TOKabstract;
import static descent.internal.compiler.parser.TOK.TOKauto;
import static descent.internal.compiler.parser.TOK.TOKconst;
import static descent.internal.compiler.parser.TOK.TOKdeprecated;
import static descent.internal.compiler.parser.TOK.TOKextern;
import static descent.internal.compiler.parser.TOK.TOKfinal;
import static descent.internal.compiler.parser.TOK.TOKoverride;
import static descent.internal.compiler.parser.TOK.TOKscope;
import static descent.internal.compiler.parser.TOK.TOKstatic;
import static descent.internal.compiler.parser.TOK.TOKsynchronized;

// DMD 1.020
public class StorageClassDeclaration extends AttribDeclaration {

	static class SCstring {
		int stc;
		TOK tok;

		public SCstring(int stc, TOK tok) {
			this.stc = stc;
			this.tok = tok;
		}
	}

	static final SCstring[] table = { new SCstring(STCauto, TOKauto),
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
	public boolean colon;

	public StorageClassDeclaration(int stc, Dsymbols decl,
			Modifier modifier, boolean single, boolean colon) {
		super(decl);
		this.stc = stc;
		this.single = single;
		this.modifier = modifier;
		this.colon = colon;
	}

	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, modifier);
			TreeVisitor.acceptChildren(visitor, decl);
		}
		visitor.endVisit(this);
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

		if (s != null) {
			throw new IllegalStateException("assert(!s);");
		}
		scd = new StorageClassDeclaration(stc, Dsymbol
				.arraySyntaxCopy(decl), modifier, single, colon);
		return scd;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
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
