package descent.internal.compiler.parser;

// DMD 1.020
public class Package extends ScopeDsymbol {

	public static DsymbolTable resolve(Identifiers packages, Dsymbol[] pparent,
			Package[] ppkg, SemanticContext context) {
		DsymbolTable dst = context.Module_modules;
		Dsymbol parent = null;

		if (ppkg != null) {
			ppkg = null;
		}

		if (packages != null) {
			int i;

			for (i = 0; i < packages.size(); i++) {
				IdentifierExp pid = packages.get(i);
				Dsymbol p;

				p = dst.lookup(pid);
				if (null == p) {
					p = new Package(pid);
					dst.insert(p);
					p.parent = parent;
					((ScopeDsymbol) p).symtab = new DsymbolTable();
				} else {
					if (null == p.isPackage()) {
						throw new IllegalStateException(
								"assert(p.isPackage());");
					}
					if (null == p.isModule()) {
						ASTDmdNode.error("module and package have the same name");
						// TODO semantic
						// fatal();
						break;
					}
				}
				parent = p;
				dst = ((Package) p).symtab;
				if (ppkg != null && null == ppkg[0]) {
					ppkg[0] = (Package) p;
				}
			}
			if (pparent != null) {
				pparent[0] = parent;
			}
		}
		return dst;
	}

	public Package(IdentifierExp ident) {
		super(ident);
	}

	@Override
	public Package isPackage() {
		return this;
	}

	@Override
	public String kind() {
		return "package";
	}
}
