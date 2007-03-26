package descent.internal.compiler.parser;

import java.util.List;

import org.eclipse.core.runtime.Assert;

public class ProtDeclaration extends AttribDeclaration {

	public Modifier modifier;
	public boolean single;
	public PROT protection;

	public ProtDeclaration(PROT p, List<Dsymbol> decl, Modifier modifier,
			boolean single) {
		super(decl);
		this.protection = p;
		this.modifier = modifier;
		this.single = single;
	}

	@Override
	public int getNodeType() {
		return PROT_DECLARATION;
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (decl != null && decl.size() > 0) {
			PROT protection_save = sc.protection;
			int explicitProtection_save = sc.explicitProtection;

			sc.protection = protection;
			sc.explicitProtection = 1;

			for (Dsymbol s : decl) {
				s.semantic(sc, context);
			}

			sc.protection = protection_save;
			sc.explicitProtection = explicitProtection_save;
		} else {
			sc.protection = protection;
			sc.explicitProtection = 1;
		}
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		ProtDeclaration pd;

		Assert.isTrue(s == null);
		pd = new ProtDeclaration(protection, Dsymbol.arraySyntaxCopy(decl),
				modifier, single);
		return pd;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs) {
		String p = null;

		switch (protection) {
		case PROTprivate:
			p = "private";
			break;
		case PROTpackage:
			p = "package";
			break;
		case PROTprotected:
			p = "protected";
			break;
		case PROTpublic:
			p = "public";
			break;
		case PROTexport:
			p = "export";
			break;
		default:
			Assert.isTrue(false);
			break;
		}
		buf.writestring(p);
		super.toCBuffer(buf, hgs);
	}

}
