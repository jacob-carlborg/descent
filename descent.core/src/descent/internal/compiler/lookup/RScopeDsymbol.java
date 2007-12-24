package descent.internal.compiler.lookup;

import descent.core.IJavaElement;
import descent.core.IParent;
import descent.core.JavaModelException;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.IArrayScopeSymbol;
import descent.internal.compiler.parser.IDsymbol;
import descent.internal.compiler.parser.IDsymbolTable;
import descent.internal.compiler.parser.IScopeDsymbol;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.PROT;
import descent.internal.core.util.Util;

public class RScopeDsymbol extends RDsymbol implements IScopeDsymbol {
	
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
			return childrenCache.keys();
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

	public RScopeDsymbol(IJavaElement element) {
		super(element);
	}

	public void importScope(IScopeDsymbol s, PROT protection) {
		// TODO Auto-generated method stub

	}

	public IArrayScopeSymbol isArrayScopeSymbol() {
		// TODO Auto-generated method stub
		return null;
	}

	public Dsymbols members() {
		if (members == null) {
			members = new Dsymbols();
			
			if (element instanceof IParent) {
				IParent parent = (IParent) element;
				try {
					for(IJavaElement child : parent.getChildren()) {
						IDsymbol converted = toDsymbol(child);
						if (converted != null) {
							members.add(converted);
						}
					}
				} catch (JavaModelException e) {
					Util.log(e);
				}
			}
		}
		return members;
	}

	public void members(Dsymbols members) {
		// TODO Auto-generated method stub

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
