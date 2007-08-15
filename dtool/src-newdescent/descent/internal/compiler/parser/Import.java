package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import descent.core.domX.IASTVisitor;

import melnorme.miscutil.tree.TreeVisitor;

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
