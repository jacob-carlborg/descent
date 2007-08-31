package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;


public class TemplateExp extends Expression {
	
	public TemplateDeclaration td;

    public TemplateExp(Loc loc, TemplateDeclaration td) {
    	super(loc, TOK.TOKtemplate);
		this.td = td;    	
    }
    
    @Override
	public int getNodeType() {
		return TEMPLATE_EXP;
	}
    
	public void accept0(IASTVisitor visitor) {
		melnorme.miscutil.Assert.fail("accept0 on a fake Node");
	}

    
    @Override
    public void rvalue(SemanticContext context) {
    	 error("template %s has no value", toChars(context));
    }

	@Override
    public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
    	 buf.writestring(td.toChars(context));
    }

}
