package descent.internal.compiler.parser;

import java.util.List;

public abstract class AttribDeclaration extends Dsymbol {

	public List<Dsymbol> decl;

	public AttribDeclaration(Loc loc, List<Dsymbol> decl) {
		super(loc);
		this.decl = decl;
	}

	@Override
	public void addLocalClass(List<ClassDeclaration> aclasses, SemanticContext context) {
		List<Dsymbol> d = include(null, null, context);

		if (d != null) {
			for (Dsymbol s : d) {
				s.addLocalClass(aclasses, context);
			}
		}
	}
	
	@Override
	public AttribDeclaration isAttribDeclaration() {
		return this;
	}

	@Override
	public int addMember(Scope sc, ScopeDsymbol sd, int memnum,
			SemanticContext context) {
		int m = 0;
		List<Dsymbol> d = include(sc, sd, context);

		if (d != null) {
			for (Dsymbol s : d) {
				m |= s.addMember(sc, sd, m | memnum, context);
			}
		}
		return m;
	}

	@Override
	public void checkCtorConstInit(SemanticContext context) {
		List<Dsymbol> d = include(null, null, context);

		if (d != null) {
			for (Dsymbol s : d) {
				s.checkCtorConstInit(context);
			}
		}
	}

	@Override
	public boolean hasPointers(SemanticContext context) {
		List<Dsymbol> d = include(null, null, context);

		if (d != null) {
			for (Dsymbol s : d) {
				if (s.hasPointers(context)) {
					return true;
				}
			}
		}
		return false;
	}

	public List<Dsymbol> include(Scope sc, ScopeDsymbol sd, SemanticContext context) {
		// TODO semantic check the null,null references of this method 
		return decl;
	}

	@Override
	public String kind() {
		return "attribute";
	}

	@Override
	public boolean oneMember(Dsymbol[] ps, SemanticContext context) {
		List<Dsymbol> d = include(null, null, context);

		return Dsymbol.oneMembers(d, ps, context);
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		List<Dsymbol> d = include(sc, null, context);

		if (d != null && d.size() > 0) {
			for (Dsymbol s : d) {
				s.semantic(sc, context);
			}
		}
	}

	@Override
	public void semantic2(Scope sc, SemanticContext context) {
		List<Dsymbol> d = include(sc, null, context);

		if (d != null && d.size() > 0) {
			for (Dsymbol s : d) {
				s.semantic2(sc, context);
			}
		}
	}

	@Override
	public void semantic3(Scope sc, SemanticContext context) {
		List<Dsymbol> d = include(sc, null, context);

		if (d != null && d.size() > 0) {
			for (Dsymbol s : d) {
				s.semantic2(sc, context);
			}
		}
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
		if (decl != null) {
			buf.writenl();
			buf.writeByte('{');
			buf.writenl();
			for (Dsymbol s : decl) {
				buf.writestring("    ");
				s.toCBuffer(buf, hgs, context);
			}
			buf.writeByte('}');
		} else {
			buf.writeByte(';');
		}
		buf.writenl();
	}

}
