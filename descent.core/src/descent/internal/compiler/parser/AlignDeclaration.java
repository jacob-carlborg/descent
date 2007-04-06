package descent.internal.compiler.parser;

import java.util.List;

import org.eclipse.core.runtime.Assert;

public class AlignDeclaration extends AttribDeclaration {

	public int salign;

	public AlignDeclaration(int sa, List<Dsymbol> decl) {
		super(decl);
		this.salign = sa;
	}

	@Override
	public int getNodeType() {
		return ALIGN_DECLARATION;
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (decl != null) {
			int salign_save = sc.structalign;

			sc.structalign = salign;
			for (Dsymbol s : decl) {
				s.semantic(sc, context);
			}
			sc.structalign = salign_save;
		} else
			sc.structalign = salign;
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		AlignDeclaration ad;

		Assert.isTrue(s == null);
		ad = new AlignDeclaration(salign, Dsymbol.arraySyntaxCopy(decl));
		return ad;
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		buf.data.append("align (" + salign + ")");
		super.toCBuffer(buf, hgs, context);
	}

}
