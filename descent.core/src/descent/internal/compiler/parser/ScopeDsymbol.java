package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

public class ScopeDsymbol extends Dsymbol {
	
	public List<Dsymbol> members;
	public DsymbolTable symtab;
	
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
	public Dsymbol search(IdentifierExp ident, int flags) {
		return super.search(ident, flags);
	}
	
	@Override
	public int kind() {
		return -1;
	}

}
