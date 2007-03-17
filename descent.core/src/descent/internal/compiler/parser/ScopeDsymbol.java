package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import descent.core.compiler.IProblem;

public class ScopeDsymbol extends Dsymbol {
	
	public List<Dsymbol> members;
	public DsymbolTable symtab;
	public List<ScopeDsymbol> imports;		// imported ScopeDsymbol's
	public List<PROT> prots;	// PROT for each import
	
	public ScopeDsymbol() {
	}
	
	public ScopeDsymbol(IdentifierExp id) {
		this.ident = id;
	}
	
	public void addMember(Dsymbol symbol) {
		if (members == null) {
			members = new ArrayList<Dsymbol>();
		}
		members.add(symbol);
	}
	
	@Override
	public Dsymbol search(IdentifierExp ident, int flags, SemanticContext context) {
		Dsymbol s;
		int i;

		//printf("%s.ScopeDsymbol::search(ident='%s', flags=x%x)\n", toChars(), ident.toChars(), flags);
		// Look in symbols declared in this module
		s = symtab != null ? symtab.lookup(ident) : null;
		if (s != null) {
			//printf("\ts = '%s.%s'\n",toChars(),s.toChars());
		} else if (imports != null) {
			// Look in imported modules
			
			i = -1;
			for (ScopeDsymbol ss : imports) {
				i++;
				Dsymbol s2;

				// If private import, don't search it
				if ((flags & 1) != 0 && prots.get(i) == PROT.PROTprivate)
					continue;

				//printf("\tscanning import '%s', prots = %d, isModule = %p, isImport = %p\n", ss.toChars(), prots[i], ss.isModule(), ss.isImport());
				s2 = ss.search(ident, ss.isModule() != null ? 1 : 0, context);
				if (s == null)
					s = s2;
				else if (s2 != null && s != s2) {
					if (s.toAlias(context) == s2.toAlias(context)) {
						if (s.isDeprecated())
							s = s2;
					} else {
						/* Two imports of the same module should be regarded as
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
					context.acceptProblem(Problem.newSemanticTypeError(d.ident.ident.string + " is private", IProblem.MemberIsPrivate, 0, start, length));
				}
			}
		}
		return s;
	}
	
	@Override
	public int kind() {
		return -1;
	}

}
