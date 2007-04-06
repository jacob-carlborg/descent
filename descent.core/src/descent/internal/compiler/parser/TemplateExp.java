package descent.internal.compiler.parser;

public class TemplateExp extends Expression {
	
	public TemplateDeclaration td;

    public TemplateExp(TemplateDeclaration td) {
    	super(TOK.TOKtemplate);
		this.td = td;    	
    }
    
    @Override
	public int getNodeType() {
		return TEMPLATE_EXP;
	}
    
    @Override
    public void rvalue(SemanticContext context) {
    	 error("template %s has no value", toChars());
    }

	@Override
    public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
    	 buf.writestring(td.toChars());
    }

}
