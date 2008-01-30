package descent.internal.compiler.lookup;

import java.util.List;

import descent.core.ICompilationUnit;
import descent.core.IImportContainer;
import descent.core.IImportDeclaration;
import descent.core.IJavaElement;
import descent.core.IParent;
import descent.core.JavaModelException;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.HashtableOfCharArrayAndObject;
import descent.internal.compiler.parser.IDsymbol;
import descent.internal.compiler.parser.IDsymbolTable;
import descent.internal.compiler.parser.IScopeDsymbol;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.Import;
import descent.internal.compiler.parser.Loc;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.SemanticMixin;
import descent.internal.core.JavaElementFinder;
import descent.internal.core.util.Util;

public abstract class RScopeDsymbol extends RDsymbol implements IScopeDsymbol {
	
	private class RDsymbolTable implements IDsymbolTable {

		public IDsymbol insert(IDsymbol dsymbol) {
			return insert(dsymbol.ident(), dsymbol);
		}

		public IDsymbol insert(IdentifierExp ident, IDsymbol dsymbol) {
			return insert(ident.ident, dsymbol);
		}

		public IDsymbol insert(char[] ident, IDsymbol dsymbol) {
			throw new IllegalStateException("Should not be called");
		}

		public char[][] keys() {
			members();
			return hitCache.keys();
		}

		public IDsymbol lookup(IdentifierExp ident) {
			return lookup(ident.ident);
		}

		public IDsymbol lookup(char[] ident) {
			return RScopeDsymbol.this.lookup(ident);
		}
		
	}
	
	private IDsymbolTable symtab;
	private Dsymbols members;
	
	public List<IScopeDsymbol> imports; // imported ScopeDsymbol's
	public List<PROT> prots; // PROT for each import
	
	// This hashtables is here to:
	// - speed up searches
	// - avoid creating duplicated classes
	protected HashtableOfCharArrayAndObject hitCache; 
	protected HashtableOfCharArrayAndObject missCache;

	public RScopeDsymbol(IJavaElement element, SemanticContext context) {
		super(element, context);
	}

	public void importScope(IScopeDsymbol s, PROT protection) {
		SemanticMixin.importScope(this, s, protection);
	}	
	
	@Override
	public String mangle(SemanticContext context) {
		return SemanticMixin.Dsymbol_mangle(this, context);
	}

	public Dsymbols members() {
		if (members == null) {
			members = new Dsymbols();
			
			// This is to avoid putting in the cache overloaded symbols
			HashtableOfCharArrayAndObject ov = new HashtableOfCharArrayAndObject();
			
			if (element instanceof IParent) {
				IParent parent = (IParent) element;
				listMembers(members, ov, parent);
			}
		}
		return members;
	}
	
	@Override
	public IDsymbol search(Loc loc, char[] ident, int flags, SemanticContext context) {
		IDsymbol sym = SemanticMixin.search(this, loc, ident, flags, context);
		
		// This is here to get the "import bug", with which you can
		// access members of modules privately improted, if you use fqn
		if (sym instanceof Import) {
			Import imp = (Import) sym;
			if (imp.pkg == null) {
				imp.load(((RModule) getModule()).getScope(), context);
			}
		}
		return sym;
	}
	
	private IDsymbol lookup(char[] ident) {
		if (!(element instanceof IParent)) {
			return null;
		}
		
		if (missCache == null) {
			missCache = new HashtableOfCharArrayAndObject();
		} else {
			if (missCache.containsKey(ident)) {
				return null;
			}
		}
		
		if (hitCache == null) {
			hitCache = new HashtableOfCharArrayAndObject();
		} else {
			Object result = hitCache.get(ident);
			if (result != null) {
				return (IDsymbol) result;
			}
		}
		
		String sident = new String(ident);		
		IParent parent = (IParent) element;
		IDsymbol result = searchInChildren(parent, ident, sident);
		
		if (result == null) {
			missCache.put(ident, this);
			return null;
		} else {
			return result;	
		}
	}
	
	private IDsymbol searchInChildren(IParent parent, char[] ident, String sident) {
		try {
			IJavaElement[] children = parent.getChildren();
			for(IJavaElement child : children) {
				IParent searchInChildren = JavaElementFinder.mustSearchInChildren(child);
				if (searchInChildren != null) {
					IDsymbol result = searchInChildren(searchInChildren, ident, sident);
					if (result != null) {
						return result;
					}
					continue;
				}
				
				if (!JavaElementFinder.isReturnTarget(child)) {
					continue;
				}
				
				// Imports are also returned, but only if the first
				// piece of the name matches
				String elementName = child.getElementName();
				if (elementName.equals(sident) ||
						(child instanceof IImportDeclaration &&
							elementName.startsWith(sident + "."))) {
					IDsymbol result = toDsymbol(child);
					hitCache.put(ident, result);
					return result;
				}
			}
		} catch (JavaModelException e) {
			Util.log(e, "Exception retrieveing children");
		}
		return null;
	}
	
	private void listMembers(Dsymbols members, HashtableOfCharArrayAndObject ov, IParent parent) {
		try {
			for(IJavaElement child : parent.getChildren()) {
				if (child.getElementType() == IJavaElement.IMPORT_CONTAINER) {
					listMembers(members, ov, ((IImportContainer) child)); 
					continue;
				}
				
				String elemName = child.getElementName();
				if (child instanceof ICompilationUnit) {
					elemName = elemName.substring(0, elemName.indexOf('.'));
				}
				char[] elemNameC = elemName.toCharArray();
				
				if (hitCache == null) {
					hitCache = new HashtableOfCharArrayAndObject();
				}
				
				IDsymbol converted = null;
				
				boolean isOverloaded = ov.containsKey(elemNameC);
				if (!isOverloaded) {
					converted = (IDsymbol) hitCache.get(elemNameC);
				}
				
				if (converted == null) {
					converted = toDsymbol(child);
				}
				
				if (converted != null) {
					members.add(converted);
					if (!isOverloaded) {
						hitCache.put(elemNameC, converted);
						ov.put(elemNameC, converted);
					}
				}
			}
		} catch (JavaModelException e) {
			Util.log(e);
		}
	}

	public void members(Dsymbols members) {
		throw new IllegalStateException("Should not be called");
	}

	public IDsymbolTable symtab() {
		if (symtab == null) {
			symtab = new RDsymbolTable();
		}
		return symtab;
	}

	public void symtab(IDsymbolTable symtab) {
		throw new IllegalStateException("Should not be called");
	}
	
	@Override
	public IScopeDsymbol isScopeDsymbol() {
		return this;
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
