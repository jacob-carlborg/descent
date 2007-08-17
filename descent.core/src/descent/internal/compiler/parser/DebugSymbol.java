package descent.internal.compiler.parser;

import melnorme.miscutil.tree.TreeVisitor;

import org.eclipse.core.runtime.Assert;

import descent.internal.compiler.parser.ast.IASTVisitor;

// TODO this class is not exactly like in DMD
public class DebugSymbol extends Dsymbol {
	
	public long level;
	public Version version;

	public DebugSymbol(Loc loc, IdentifierExp ident, Version version) {
		super(loc, ident);
		this.version = version;		
	}
	
	public DebugSymbol(Loc loc, long level, Version version) {
		super(loc);
		this.level = level;
		this.version = version;		
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, version);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		
	}
	
	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		Assert.isTrue(s == null);
	    DebugSymbol ds = new DebugSymbol(loc, ident, version);
	    ds.level = level;
	    return ds;
	}
	
	@Override
	public int getNodeType() {
		return DEBUG_SYMBOL;
	}

}
