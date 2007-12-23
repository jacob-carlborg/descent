package descent.internal.compiler.parser;

import descent.core.compiler.IProblem;

// DMD 1.020
public class Package extends ScopeDsymbol implements IPackage {

	public static DsymbolTable resolve(Identifiers packages, IDsymbol[] pparent,
			IPackage[] ppkg, SemanticContext context) {
		DsymbolTable dst = context.Module_modules;
		IDsymbol parent = null;

		if (ppkg != null) {
			ppkg[0] = null;
		}

		if (packages != null) {
			int i;

			for (i = 0; i < packages.size(); i++) {
				IdentifierExp pid = packages.get(i);
				IDsymbol p;

				p = dst.lookup(pid);
				if (null == p) {
					p = new Package(pid);
					dst.insert(p);
					p.parent(parent);
					((ScopeDsymbol) p).symtab = new DsymbolTable();
				} else {
					if (null == p.isPackage()) {
						throw new IllegalStateException(
								"assert(p.isPackage());");
					}
					if (null != p.isModule()) {
						context.acceptProblem(Problem.newSemanticTypeError(
								IProblem.PackageAndModuleHaveTheSameName, 0, 0,
								0));
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
