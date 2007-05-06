package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

public class Import extends Dsymbol {
	
	public List<IdentifierExp> packages;
	public IdentifierExp id;
	public IdentifierExp aliasId;
	
	public List<IdentifierExp> names;
	public List<IdentifierExp> aliases;
	public Module mod;
	public Package pkg;
	
	public Import(Loc loc, List<IdentifierExp> packages, IdentifierExp id, IdentifierExp aliasId) {
		super(loc);
		this.packages = packages;
		this.id = id;
		this.aliasId = aliasId;
	}
	
	@Override
	public Import isImport() {
		return this;
	}
	
	@Override
	public Dsymbol toAlias(SemanticContext context) {
		if (aliasId != null)
			return mod;
		return this;
	}

	public void addAlias(IdentifierExp name, IdentifierExp alias) {
		if (names == null) {
			names = new ArrayList<IdentifierExp>();
			aliases = new ArrayList<IdentifierExp>();
		}
		names.add(name);
		aliases.add(alias);
	}
	
	@Override
	public int getNodeType() {
		return IMPORT;
	}

}