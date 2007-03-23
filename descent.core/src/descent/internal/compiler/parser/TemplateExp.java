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

}
