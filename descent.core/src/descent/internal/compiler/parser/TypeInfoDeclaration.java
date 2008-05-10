package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;


public class TypeInfoDeclaration extends VarDeclaration {
	
	public Type tinfo;
	
	public TypeInfoDeclaration(Type tinfo, int internal, SemanticContext context) {
		super(Loc.ZERO, context.Type_typeinfo.type, tinfo.getTypeInfoIdent(internal), null);
		this.tinfo = tinfo;
		this.storage_class = STC.STCstatic;
		this.protection = PROT.PROTpublic;
		this.linkage = LINK.LINKc;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public void semantic(Scope sc, SemanticContext context) {
		if (linkage != LINK.LINKc) {
			throw new IllegalStateException("assert(linkage == LINKc);");
		}
	}
	
	@Override
	public Dsymbol syntaxCopy(Dsymbol s, SemanticContext context) {
		throw new IllegalStateException("assert(0);"); // should never be produced by syntax
	}

}
