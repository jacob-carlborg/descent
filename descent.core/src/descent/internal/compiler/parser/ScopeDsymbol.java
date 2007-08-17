package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class ScopeDsymbol extends Dsymbol {

	public List<Dsymbol> members;
	public DsymbolTable symtab;
	public List<ScopeDsymbol> imports; // imported ScopeDsymbol's
	public List<PROT> prots; // PROT for each import

	public ScopeDsymbol(Loc loc) {
		super(loc);
		this.members = null;
		this.symtab = null;
		this.imports = null;
		this.prots = null;
	}

	public ScopeDsymbol(Loc loc, IdentifierExp id) {
		super(loc, id);
		this.members = null;
		this.symtab = null;
		this.imports = null;
		this.prots = null;
	}
	
	public void accept0(IASTVisitor visitor) {
		melnorme.miscutil.Assert.fail("Abstract Class accept0");
	}

	public void addMember(Dsymbol symbol) {
		members.add(symbol);
	}

	@Override
	public void defineRef(Dsymbol s) {
		ScopeDsymbol ss;

		ss = s.isScopeDsymbol();
		members = ss.members;
		ss.members = null;
	}

	@Override
	public int getNodeType() {
		return SCOPE_DSYMBOL;
	}

	public void importScope(ScopeDsymbol s, PROT protection) {
		if (s != this) {
			if (imports == null) {
				imports = new ArrayList<ScopeDsymbol>();
			} else {
				for (int i = 0; i < imports.size(); i++) {
					ScopeDsymbol ss;

					ss = imports.get(i);
					if (ss == s) {
						if (protection.ordinal() > prots.get(i).ordinal()) {
							prots.set(i, protection); // upgrade access
						}
						return;
					}
				}
			}
			imports.add(s);
			// TODO semantic check this translation
			// prots = (unsigned char *)mem.realloc(prots, imports.dim *
			// sizeof(prots[0]));
			// prots[imports.dim - 1] = protection;
			prots.set(imports.size() - 1, protection);
		}
	}

	@Override
	public boolean isforwardRef() {
		return (members == null);
	}

	@Override
	public ScopeDsymbol isScopeDsymbol() {
		return this;
	}

	@Override
	public String kind() {
		return "ScopeDsymbol";
	}

	public Dsymbol nameCollision(Dsymbol s, SemanticContext context) {
		Dsymbol sprev;

		// Look to see if we are defining a forward referenced symbol

		sprev = symtab.lookup(s.ident);

		Assert.isNotNull(sprev);
		if (s.equals(sprev)) // if the same symbol
		{
			if (s.isforwardRef()) {
				// reference
				return sprev;
			}
			if (sprev.isforwardRef()) {
				sprev.defineRef(s); // copy data from s into sprev
				return sprev;
			}
		}
		context.multiplyDefined(s, sprev);
		return sprev;
	}

	@Override
	public Dsymbol search(Loc loc, String ident, int flags, SemanticContext context) {
		Dsymbol s;
		int i;

		// Look in symbols declared in this module
		s = symtab != null ? symtab.lookup(ident) : null;
		if (s != null) {
		} else if (imports != null) {
			// Look in imported modules

			i = -1;
			for (ScopeDsymbol ss : imports) {
				i++;
				Dsymbol s2;

				// If private import, don't search it
				if ((flags & 1) != 0 && prots.get(i) == PROT.PROTprivate) {
					continue;
				}

				s2 = ss.search(loc, ident, ss.isModule() != null ? 1 : 0, context);
				if (s == null) {
					s = s2;
				} else if (s2 != null && s != s2) {
					if (s.toAlias(context) == s2.toAlias(context)) {
						if (s.isDeprecated()) {
							s = s2;
						}
					} else {
						/*
						 * Two imports of the same module should be regarded as
						 * the same.
						 */
						Import i1 = s.isImport();
						Import i2 = s2.isImport();
						if (!(i1 != null && i2 != null && (i1.mod == i2.mod || (i1.parent
								.isImport() == null
								&& i2.parent.isImport() == null && i1.ident
								.equals(i2.ident))))) {
							context.multiplyDefined(s, s2);
							break;
						}
					}
				}
			}
			if (s != null) {
				Declaration d = s.isDeclaration();
				if (d != null && d.protection == PROT.PROTprivate
						&& d.parent.isTemplateMixin() == null) {
					context.acceptProblem(Problem.newSemanticTypeError(
							d.ident.ident + " is private",
							IProblem.MemberIsPrivate, 0, start, length));
				}
			}
		}
		return s;
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		ScopeDsymbol sd;
		if (s != null) {
			sd = (ScopeDsymbol) s;
		} else {
			sd = new ScopeDsymbol(loc, ident);
		}
		sd.members = arraySyntaxCopy(members);
		return sd;
	}

}
