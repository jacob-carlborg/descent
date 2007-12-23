package descent.internal.compiler.parser;

import descent.core.compiler.IProblem;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class TemplateExp extends Expression {
	
	public ITemplateDeclaration td;

    public TemplateExp(Loc loc, ITemplateDeclaration td) {
    	super(loc, TOK.TOKtemplate);
		this.td = td;    	
    }
    
    @Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
    
	@Override
	public int getNodeType() {
		return TEMPLATE_EXP;
	}
    
    @Override
    public void rvalue(SemanticContext context) {
    	context.acceptProblem(Problem.newSemanticTypeError(
				IProblem.TemplateHasNoValue, this, new String[] { toChars(context) }));
    }

	@Override
    public void toCBuffer(OutBuffer buf, HdrGenState hgs, SemanticContext context) {
    	 buf.writestring(td.toChars(context));
    }

}
