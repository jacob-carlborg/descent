package descent.internal.compiler.parser;

import java.util.ArrayList;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

import static descent.internal.compiler.parser.STC.STCabstract;
import static descent.internal.compiler.parser.STC.STCauto;
import static descent.internal.compiler.parser.STC.STCconst;
import static descent.internal.compiler.parser.STC.STCdeprecated;
import static descent.internal.compiler.parser.STC.STCextern;
import static descent.internal.compiler.parser.STC.STCfinal;
import static descent.internal.compiler.parser.STC.STCinvariant;
import static descent.internal.compiler.parser.STC.STCmanifest;
import static descent.internal.compiler.parser.STC.STCnothrow;
import static descent.internal.compiler.parser.STC.STCoverride;
import static descent.internal.compiler.parser.STC.STCpure;
import static descent.internal.compiler.parser.STC.STCscope;
import static descent.internal.compiler.parser.STC.STCstatic;
import static descent.internal.compiler.parser.STC.STCsynchronized;
import static descent.internal.compiler.parser.STC.STCtls;

import static descent.internal.compiler.parser.TOK.TOKabstract;
import static descent.internal.compiler.parser.TOK.TOKauto;
import static descent.internal.compiler.parser.TOK.TOKconst;
import static descent.internal.compiler.parser.TOK.TOKdeprecated;
import static descent.internal.compiler.parser.TOK.TOKextern;
import static descent.internal.compiler.parser.TOK.TOKfinal;
import static descent.internal.compiler.parser.TOK.TOKinvariant;
import static descent.internal.compiler.parser.TOK.TOKnothrow;
import static descent.internal.compiler.parser.TOK.TOKoverride;
import static descent.internal.compiler.parser.TOK.TOKpure;
import static descent.internal.compiler.parser.TOK.TOKscope;
import static descent.internal.compiler.parser.TOK.TOKstatic;
import static descent.internal.compiler.parser.TOK.TOKsynchronized;
import static descent.internal.compiler.parser.TOK.TOKtls;

public class StorageClassDeclaration extends AttribDeclaration {

	static class SCstring {
		int stc;
		TOK tok;

		public SCstring(int stc, TOK tok) {
			this.stc = stc;
			this.tok = tok;
		}
	}

	static final SCstring[] table1 = { 
			new SCstring(STCauto, TOKauto),
			new SCstring(STCscope, TOKscope),
			new SCstring(STCstatic, TOKstatic),
			new SCstring(STCextern, TOKextern),
			new SCstring(STCconst, TOKconst), 
			new SCstring(STCfinal, TOKfinal),
			new SCstring(STCabstract, TOKabstract),
			new SCstring(STCsynchronized, TOKsynchronized),
			new SCstring(STCdeprecated, TOKdeprecated),
			new SCstring(STCoverride, TOKoverride), };

	static final SCstring[] table2 = { 
			new SCstring(STCauto, TOKauto),
			new SCstring(STCscope, TOKscope),
			new SCstring(STCstatic, TOKstatic),
			new SCstring(STCextern, TOKextern),
			new SCstring(STCconst, TOKconst), 
			new SCstring(STCinvariant, TOKinvariant), 
			new SCstring(STCfinal, TOKfinal),
			new SCstring(STCabstract, TOKabstract),
			new SCstring(STCsynchronized, TOKsynchronized),
			new SCstring(STCdeprecated, TOKdeprecated),
			new SCstring(STCoverride, TOKoverride),
			new SCstring(STCnothrow, TOKnothrow),
			new SCstring(STCpure, TOKpure),
			new SCstring(STCtls, TOKtls),
			};

	public boolean single;
	public int stc;
	public Modifier modifier;
	public boolean colon;

	public StorageClassDeclaration(int stc, Dsymbols decl, Modifier modifier,
			boolean single, boolean colon) {
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

			if (context.isD2()) {
				/* These sets of storage classes are mutually exclusive,
				 * so choose the innermost or most recent one.
				 */
				if ((stc & (STCauto | STCscope | STCstatic | STCextern | STCmanifest)) != 0) {
					sc.stc &= ~(STCauto | STCscope | STCstatic | STCextern | STCmanifest);
				}
				if ((stc & (STCauto | STCscope | STCstatic | STCtls | STCmanifest)) != 0) {
					sc.stc &= ~(STCauto | STCscope | STCstatic | STCtls | STCmanifest);
				}
				if ((stc & (STCconst | STCinvariant | STCmanifest)) != 0) {
					sc.stc &= ~(STCconst | STCinvariant | STCmanifest);
				}
			} else {
				if ((stc & (STC.STCauto | STC.STCscope | STC.STCstatic | STC.STCextern)) != 0) {
					sc.stc &= ~(STC.STCauto | STC.STCscope | STC.STCstatic | STC.STCextern);
				}
			}

			sc.stc |= stc;

			for (Dsymbol s : decl) {
				// Send extra modifiers to out children, so that they can report better problems
				s.extraModifiers = modifiers;
				if (s.extraModifiers == null) {
					s.extraModifiers = new ArrayList<Modifier>();
				}
				if (extraModifiers != null) {
					s.extraModifiers.addAll(extraModifiers);
				}
				s.extraModifiers.add(modifier);
				s.semantic(sc, context);
				s.extraModifiers = null;
			}

			sc.stc = stc_save;
		} else {
			sc.stc = stc;
		}
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s, SemanticContext context) {
		StorageClassDeclaration scd;

		if (s != null) {
			throw new IllegalStateException("assert(!s);");
		}
		scd = new StorageClassDeclaration(stc, Dsymbol.arraySyntaxCopy(decl,
				context), modifier, single, colon);
		return scd;
	}
	
	public static void stcToCBuffer(OutBuffer buf, int stc,
			SemanticContext context) {
		SCstring[] theTable = context.isD2() ? table2 : table1;

		for (SCstring sc : theTable) {
			if ((stc & sc.stc) != 0) {
				buf.writestring(sc.tok.toString());
				buf.writeByte(' ');
			}
		}
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		stcToCBuffer(buf, stc, context);
		super.toCBuffer(buf, hgs, context);
	}

	@Override
	public String getSignature() {
		return parent.getSignature();
	}

	@Override
	public int getStorageClass() {
		return stc;
	}

}
