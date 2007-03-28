package descent.internal.compiler.parser;

import org.eclipse.core.runtime.Assert;

// TODO this class is not exactly like in DMD
public class DebugSymbol extends Dsymbol {
	
	public long level;
	public Version version;

	public DebugSymbol(IdentifierExp ident, Version version) {
		super(ident);
		this.version = version;		
	}
	
	public DebugSymbol(long level, Version version) {
		this.level = level;
		this.version = version;		
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		
	}
	
	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		Assert.isTrue(s == null);
	    DebugSymbol ds = new DebugSymbol(ident, version);
	    ds.level = level;
	    return ds;
	}
	
	@Override
	public int getNodeType() {
		return DEBUG_SYMBOL;
	}

}
