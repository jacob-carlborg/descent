package descent.internal.compiler.parser;

import org.eclipse.core.runtime.Assert;

public class TypeInfoDeclaration extends VarDeclaration {
	
	public Type tinfo;
	
	public TypeInfoDeclaration(Type tinfo, int internal, SemanticContext context) {
		super(Loc.ZERO, context.typeinfo.type, tinfo.getTypeInfoIdent(internal), null);
		this.tinfo = tinfo;
		this.storage_class = STC.STCstatic;
		this.protection = PROT.PROTpublic;
		this.linkage = LINK.LINKc;
	}
	
	@Override
	public Dsymbol syntaxCopy(Dsymbol s) {
		Assert.isTrue(false);
		return null;
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		
	}

}
