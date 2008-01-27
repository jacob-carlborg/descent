package descent.internal.compiler.parser;

import java.util.List;

import org.eclipse.core.runtime.Assert;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class ScopeDsymbol extends Dsymbol implements IScopeDsymbol {

	public Dsymbols members, sourceMembers;
	public IDsymbolTable symtab;
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
	
	protected void acceptSynthetic(IASTVisitor visitor) {
		if (symtab != null) {
			for(char[] key : symtab.keys()) {
				if (key == null) continue;
				
				IDsymbol s = symtab.lookup(key);
				if (s.synthetic() && (members == null || !members.contains(s))) {
					s.accept(visitor);
				}
			}
		}
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
		SemanticMixin.importScope(this, s, protection);
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
		return SemanticMixin.search(this, loc, ident, flags, context);
	}

	@Override
	public IDsymbol syntaxCopy(IDsymbol s, SemanticContext context) {
		ScopeDsymbol sd;
		if (s != null) {
			sd = (ScopeDsymbol) s;
		} else {
			sd = new ScopeDsymbol(ident);
		}
		sd.members = arraySyntaxCopy(members, context);
		return sd;
	}
	
	public IDsymbolTable symtab() {
		return symtab;
	}
	
	public void symtab(IDsymbolTable symtab) {
		this.symtab = symtab;
	}
	
	public Dsymbols members() {
		return members;
	}
	
	public void members(Dsymbols members) {
		this.members = members;
	}
	
	public List<IScopeDsymbol> imports() {
		return imports;
	}
	
	public void imports(List<IScopeDsymbol> imports) {
		this.imports = imports;
	}
	
	public List<PROT> prots() {
		return prots;
	}
	
	public void prots(List<PROT> prots) {
		this.prots = prots;
	}

}
