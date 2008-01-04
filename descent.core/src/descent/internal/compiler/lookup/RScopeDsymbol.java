package descent.internal.compiler.lookup;

import descent.core.ICompilationUnit;
import descent.core.IImportContainer;
import descent.core.IJavaElement;
import descent.core.IParent;
import descent.core.JavaModelException;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.HashtableOfCharArrayAndObject;
import descent.internal.compiler.parser.IDsymbol;
import descent.internal.compiler.parser.IDsymbolTable;
import descent.internal.compiler.parser.IScopeDsymbol;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.SemanticMixin;
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
			return search(null, ident, 0, null);
		}
		
	}
	
	private IDsymbolTable symtab;
	private Dsymbols members;

	public RScopeDsymbol(IJavaElement element, SemanticContext context) {
		super(element, context);
	}

	public void importScope(IScopeDsymbol s, PROT protection) {
		// TODO Auto-generated method stub

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
				
				if (!ov.containsKey(elemNameC)) {
					converted = (IDsymbol) hitCache.get(elemNameC);
				}
				
				if (converted == null) {
					converted = toDsymbol(child);
				}
				
				if (converted != null) {
					members.add(converted);
					hitCache.put(elemNameC, converted);
					ov.put(elemNameC, converted);
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

}
