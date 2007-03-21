package descent.internal.compiler.parser;

import java.util.List;

public abstract class AttribDeclaration extends Dsymbol {

	public List<Dsymbol> decl;

	public AttribDeclaration(List<Dsymbol> decl) {
		this.decl = decl;
	}

	@Override
	public void addLocalClass(List<ClassDeclaration> aclasses) {
		List<Dsymbol> d = include(null, null);

		if (d != null) {
			for (Dsymbol s : d) {
				s.addLocalClass(aclasses);
			}
		}
	}

	@Override
	public int addMember(Scope sc, ScopeDsymbol sd, int memnum,
			SemanticContext context) {
		int m = 0;
		List<Dsymbol> d = include(sc, sd);

		if (d != null) {
			for (Dsymbol s : d) {
				m |= s.addMember(sc, sd, m | memnum, context);
			}
		}
		return m;
	}

	@Override
	public void checkCtorConstInit() {
		List<Dsymbol> d = include(null, null);

		if (d != null) {
			for (Dsymbol s : d) {
				s.checkCtorConstInit();
			}
		}
	}

	@Override
	public boolean hasPointers() {
		List<Dsymbol> d = include(null, null);

		if (d != null) {
			for (Dsymbol s : d) {
				if (s.hasPointers()) {
					return true;
				}
			}
		}
		return false;
	}

	public List<Dsymbol> include(Scope sc, ScopeDsymbol sd) {
		return decl;
	}

	@Override
	public String kind() {
		return "attribute";
	}

	@Override
	public boolean oneMember(Dsymbol[] ps) {
		List<Dsymbol> d = include(null, null);

		return Dsymbol.oneMembers(d, ps);
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		List<Dsymbol> d = include(sc, null);

		if (d != null && d.size() > 0) {
			for (Dsymbol s : d) {
				s.semantic(sc, context);
			}
		}
	}

	@Override
	public void semantic2(Scope sc, SemanticContext context) {
		List<Dsymbol> d = include(sc, null);

		if (d != null && d.size() > 0) {
			for (Dsymbol s : d) {
				s.semantic2(sc, context);
			}
		}
	}

	@Override
	public void semantic3(Scope sc, SemanticContext context) {
		List<Dsymbol> d = include(sc, null);

		if (d != null && d.size() > 0) {
			for (Dsymbol s : d) {
				s.semantic2(sc, context);
			}
		}
	}

}
