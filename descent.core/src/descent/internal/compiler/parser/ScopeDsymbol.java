package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class ScopeDsymbol extends Dsymbol implements IScopeDsymbol {

	public Dsymbols members, sourceMembers;
	public DsymbolTable symtab;
	public List<IScopeDsymbol> imports; // imported ScopeDsymbol's
	public List<PROT> prots; // PROT for each import
	
	public ScopeDsymbol() {
		this.members = null;
		this.symtab = null;
		this.imports = null;
		this.prots = null;
	}
	
	public ScopeDsymbol(IdentifierExp id) {
		super(id);
		this.members = null;
		this.symtab = null;
		this.imports = null;
		this.prots = null;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	public void addMember(Dsymbol symbol) {
		members.add(symbol);
	}

	@Override
	public void defineRef(IDsymbol s) {
		IScopeDsymbol ss;

		ss = s.isScopeDsymbol();
		members = ss.members();
		ss.members(null);
	}

	@Override
	public int getNodeType() {
		return SCOPE_DSYMBOL;
	}

	public void importScope(IScopeDsymbol s, PROT protection) {
		if (s != this) {
			if (imports == null) {
				imports = new ArrayList<IScopeDsymbol>();
			} else {
				for (int i = 0; i < imports.size(); i++) {
					IScopeDsymbol ss;

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
			if (prots == null) {
				prots = new Array<PROT>();
			}
			prots.set(size(imports) - 1, protection);
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
	
	public static void multiplyDefined(Loc loc, IDsymbol s1, IDsymbol s2, SemanticContext context) {
		if (loc != null && loc.filename != null) {
			context.acceptProblem(Problem.newSemanticTypeErrorLoc(
					IProblem.SymbolAtLocationConflictsWithSymbolAtLocation, 
					s2, new String[] { s1.toPrettyChars(context), s1.locToChars(context), s2.toPrettyChars(context), s2.locToChars(context) }));
		} else {
			context.acceptProblem(Problem.newSemanticTypeErrorLoc(
					IProblem.SymbolConflictsWithSymbolAtLocation, 
					s1, new String[] { s1.toChars(context), s2.kind(),
						    s2.toPrettyChars(context),
						    s2.locToChars(context)}));
		}		
	}

	public IDsymbol nameCollision(Dsymbol s, SemanticContext context) {
		IDsymbol sprev;

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
		multiplyDefined(Loc.ZERO, s, sprev, context);
		return sprev;
	}

	@Override
	public IDsymbol search(Loc loc, char[] ident, int flags, SemanticContext context) {
		IDsymbol s;
		int i;

		// Look in symbols declared in this module
		s = symtab != null ? symtab.lookup(ident) : null;
		if (s != null) {
		} else if (imports != null) {
			// Look in imported modules

			i = -1;
			for (IScopeDsymbol ss : imports) {
				i++;
				IDsymbol s2;

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
						IImport i1 = s.isImport();
						IImport i2 = s2.isImport();
						if (!(i1 != null && i2 != null && (i1.mod() == i2.mod() || (i1.parent()
								.isImport() == null
								&& i2.parent().isImport() == null && equals(i1.ident(), i2.ident()))))) {
							multiplyDefined(loc, s, s2, context);
							break;
						}
					}
				}
			}
			if (s != null) {
				IDeclaration d = s.isDeclaration();
				if (d != null && d.protection() == PROT.PROTprivate
						&& d.parent().isTemplateMixin() == null) {
					context.acceptProblem(Problem.newSemanticTypeError(
							IProblem.MemberIsPrivate, this, new String[] { new String(d.ident().ident) }));
				}
			}
		}
		return s;
	}

	@Override
	public Dsymbol syntaxCopy(Dsymbol s, SemanticContext context) {
		ScopeDsymbol sd;
		if (s != null) {
			sd = (ScopeDsymbol) s;
		} else {
			sd = new ScopeDsymbol(ident);
		}
		sd.members = arraySyntaxCopy(members, context);
		return sd;
	}
	
	public DsymbolTable symtab() {
		return symtab;
	}
	
	public void symtab(DsymbolTable symtab) {
		this.symtab = symtab;
	}
	
	public Dsymbols members() {
		return members;
	}
	
	public void members(Dsymbols members) {
		this.members = members;
	}

}
