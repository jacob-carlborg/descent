package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class ProtDeclaration extends AttribDeclaration {

	public Modifier modifier;
	public boolean single;
	public PROT protection;
	public boolean colon;

	public ProtDeclaration(Loc loc, PROT p, List<Dsymbol> decl,
			Modifier modifier, boolean single, boolean colon) {
		super(loc, decl);
		this.protection = p;
		this.modifier = modifier;
		this.single = single;
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

		if (s != null) {
			throw new IllegalStateException("assert(s);");
		}
		pd = new ProtDeclaration(loc, protection,
				Dsymbol.arraySyntaxCopy(decl), modifier, single, colon);
		return pd;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
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
		super.toCBuffer(buf, hgs, context);
	}

}