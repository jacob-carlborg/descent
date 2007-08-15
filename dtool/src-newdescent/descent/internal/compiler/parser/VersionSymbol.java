package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

// TODO this class is not exactly like in DMD
public class VersionSymbol extends Dsymbol {
	
	public long level;
	public Version version;

	public VersionSymbol(Loc loc, IdentifierExp ident, Version version) {
		super(loc, ident);
		this.version = version;		
	}
	
	public VersionSymbol(Loc loc, long level, Version version) {
		super(loc);
		this.level = level;
		this.version = version;		
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		
	}
	
	@Override
	public int getNodeType() {
		return VERSION_SYMBOL;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, version);
		}
		visitor.endVisit(this);
	}

}
