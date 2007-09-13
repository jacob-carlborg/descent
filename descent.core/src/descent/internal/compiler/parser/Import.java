package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class Import extends Dsymbol {
	
	public Identifiers packages;
	public IdentifierExp id;
	public IdentifierExp aliasId;
	
	public Identifiers names;
	public Identifiers aliases;
	public Module mod;
	public Package pkg;
	
	public Import(Loc loc, Identifiers packages, IdentifierExp id, IdentifierExp aliasId) {
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
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, packages);
			TreeVisitor.acceptChildren(visitor, id);
			TreeVisitor.acceptChildren(visitor, aliasId);
			TreeVisitor.acceptChildren(visitor, names);
			TreeVisitor.acceptChildren(visitor, aliases);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public Dsymbol toAlias(SemanticContext context) {
		if (aliasId != null)
			return mod;
		return this;
	}

	public void addAlias(IdentifierExp name, IdentifierExp alias) {
		if (names == null) {
			names = new Identifiers();
			aliases = new Identifiers();
		}
		names.add(name);
		aliases.add(alias);
	}
	
	@Override
	public int getNodeType() {
		return IMPORT;
	}

}
