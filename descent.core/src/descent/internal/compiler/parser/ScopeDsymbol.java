package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class ScopeDsymbol extends Dsymbol {

	public Dsymbols members, sourceMembers;
	public DsymbolTable symtab;
	public List<ScopeDsymbol> imports; // imported ScopeDsymbol's
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
	
	protected void acceptSynthetic(IASTVisitor visitor) {
		if (symtab != null) {
			for(char[] key : symtab.keys()) {
				if (key == null) continue;
				
				Dsymbol s = symtab.lookup(key);
				if (s.synthetic && (members == null || !members.contains(s))) {
					s.accept(visitor);
				}
			}
		}
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
			if (this.imports == null) {
				this.imports = new ArrayList<ScopeDsymbol>();
			} else {
				for (int i = 0; i < this.imports.size(); i++) {
					ScopeDsymbol ss;

					ss = this.imports.get(i);
					if (ss == s) {
						if (protection.ordinal() > this.prots.get(i).ordinal()) {
							this.prots.set(i, protection); // upgrade access
						}
						return;
					}
				}
			}
			this.imports.add(s);
			// TODO semantic check this translation
			// prots = (unsigned char *)mem.realloc(prots, imports.dim *
			// sizeof(prots[0]));
			// prots[imports.dim - 1] = protection;
			if (this.prots == null) {
				this.prots = new Array<PROT>();
			}
			this.prots.set(ASTDmdNode.size(this.imports) - 1, protection);
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
	
	public static void multiplyDefined(Loc loc, Dsymbol s1, Dsymbol s2, SemanticContext context) {
		if (loc != null && loc.filename != null) {
			if (context.acceptsProblems()) {
				context.acceptProblem(Problem.newSemanticTypeErrorLoc(
						IProblem.SymbolAtLocationConflictsWithSymbolAtLocation, 
						s2, new String[] { s1.toPrettyChars(context), s1.locToChars(context), s2.toPrettyChars(context), s2.locToChars(context) }));
			}
		} else {
			if (context.acceptsProblems()) {
				context.acceptProblem(Problem.newSemanticTypeErrorLoc(
						IProblem.SymbolConflictsWithSymbolAtLocation, 
						s1, new String[] { s1.toChars(context), s2.kind(),
							    s2.toPrettyChars(context),
							    s2.locToChars(context)}));
			}
		}		
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
		multiplyDefined(Loc.ZERO, s, sprev, context);
		return sprev;
	}

	@Override
	public Dsymbol search(Loc loc, char[] ident, int flags, SemanticContext context) {
		Dsymbol s;
		int i;

		// Look in symbols declared in this module
		s = symtab != null ? symtab.lookup(ident) : null;
		if (s != null) {
		} else if (imports != null && !imports.isEmpty()) {
			// Look in imported modules
			i = -1;
			for (ScopeDsymbol ss : imports) {
				i++;
				Dsymbol s2;

				// If private import, don't search it
				if ((flags & 1) != 0 && this.prots.get(i) == PROT.PROTprivate) {
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
								&& i2.parent.isImport() == null && ASTDmdNode.equals(i1.ident, i2.ident))))) {
							ScopeDsymbol.multiplyDefined(loc, s, s2, context);
							break;
						}
					}
				}
			}
			if (s != null) {
				Declaration d = s.isDeclaration();
				if (d != null && d.protection == PROT.PROTprivate
						&& d.parent.isTemplateMixin() == null) {
					if (context.acceptsProblems()) {
						context.acceptProblem(Problem.newSemanticTypeError(
								IProblem.MemberIsPrivate, d, new String[] { new String(d.ident.ident) }));
					}
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
	
	/*******************************************
	 * Look for member of the form:
	 *	const(MemberInfo)[] getMembers(string);
	 * Returns NULL if not found
	 */
	FuncDeclaration findGetMembers(SemanticContext context) {
		Dsymbol s = search_function(this, Id.getmembers, context);
		FuncDeclaration fdx = s != null ? s.isFuncDeclaration() : null;

		if (fdx != null && fdx.isVirtual(context))
			fdx = null;

		return fdx;
	}

}
