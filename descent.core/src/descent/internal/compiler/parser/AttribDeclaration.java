package descent.internal.compiler.parser;


// DMD 1.020
public abstract class AttribDeclaration extends Dsymbol {

	public Dsymbols decl;

	public AttribDeclaration(Dsymbols decl) {
		this.decl = decl;
	}

	@Override
	public void addLocalClass(ClassDeclarations aclasses,
			SemanticContext context) {
		Dsymbols d = include(null, null, context);

		if (d != null) {
			for (IDsymbol s : d) {
				s.addLocalClass(aclasses, context);
			}
		}
	}

	@Override
	public int addMember(Scope sc, IScopeDsymbol sd, int memnum,
			SemanticContext context) {
		int m = 0;
		Dsymbols d = include(sc, sd, context);

		if (d != null) {
			for (IDsymbol s : d) {
				m |= s.addMember(sc, sd, m | memnum, context);
			}
		}
		return m;
	}

	@Override
	public void checkCtorConstInit(SemanticContext context) {
		Dsymbols d = include(null, null, context);

		if (d != null) {
			for (IDsymbol s : d) {
				s.checkCtorConstInit(context);
			}
		}
	}

	@Override
	public boolean hasPointers(SemanticContext context) {
		Dsymbols d = include(null, null, context);

		if (d != null) {
			for (IDsymbol s : d) {
				if (s.hasPointers(context)) {
					return true;
				}
			}
		}
		return false;
	}

	public Dsymbols include(Scope sc, IScopeDsymbol sd, SemanticContext context) {
		return decl;
	}

	@Override
	public void inlineScan(SemanticContext context) {
		int i;
		Dsymbols d = include(null, null, context);

		if (d != null) {
			for (i = 0; i < d.size(); i++) {
				Dsymbol s;

				s = (Dsymbol) d.get(i);
				//printf("AttribDeclaration::inlineScan %s\n", s.toChars());
				s.inlineScan(context);
			}
		}
	}

	@Override
	public AttribDeclaration isAttribDeclaration() {
		return this;
	}

	@Override
	public String kind() {
		return "attribute";
	}

	@Override
	public boolean oneMember(Dsymbol[] ps, SemanticContext context) {
		Dsymbols d = include(null, null, context);

		return Dsymbol.oneMembers(d, ps, context);
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {
		Dsymbols d = include(sc, null, context);

		if (d != null && d.size() > 0) {
			for (IDsymbol s : d) {
				s.semantic(sc, context);
			}
		}
	}

	@Override
	public void semantic2(Scope sc, SemanticContext context) {
		Dsymbols d = include(sc, null, context);

		if (d != null && d.size() > 0) {
			for (IDsymbol s : d) {
				s.semantic2(sc, context);
			}
		}
	}

	@Override
	public void semantic3(Scope sc, SemanticContext context) {
		Dsymbols d = include(sc, null, context);

		if (d != null && d.size() > 0) {
			for (IDsymbol s : d) {
				s.semantic3(sc, context);
			}
		}
	}

	@Override
	public void toCBuffer(OutBuffer buf, HdrGenState hgs,
			SemanticContext context) {
		if (decl != null) {
			buf.writenl();
			buf.writeByte('{');
			buf.writenl();
			for (IDsymbol s : decl) {
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
