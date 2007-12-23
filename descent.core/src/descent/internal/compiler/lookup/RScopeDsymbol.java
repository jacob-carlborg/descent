package descent.internal.compiler.lookup;

import descent.core.IJavaElement;
import descent.core.IParent;
import descent.core.JavaModelException;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.IArrayScopeSymbol;
import descent.internal.compiler.parser.IDsymbolTable;
import descent.internal.compiler.parser.IScopeDsymbol;
import descent.internal.compiler.parser.PROT;
import descent.internal.core.util.Util;

public class RScopeDsymbol extends RDsymbol implements IScopeDsymbol {
	
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
						members.add(toDsymbol(child));
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
		return symtab;
	}

	public void symtab(IDsymbolTable symtab) {
		this.symtab = symtab;
	}
	
	@Override
	public IScopeDsymbol isScopeDsymbol() {
		return this;
	}

}
