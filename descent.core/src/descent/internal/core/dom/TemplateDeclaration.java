package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IDeclaration;
import descent.core.dom.ISimpleName;
import descent.core.dom.ITemplateDeclaration;
import descent.core.dom.ITemplateParameter;

public class TemplateDeclaration extends Declaration implements ITemplateDeclaration {
	
	private ITemplateParameter[] tpl;
	private IDeclaration[] declDefs;

	public TemplateDeclaration(Identifier id, List<TemplateParameter> tpl, List<IDeclaration> decldefs) {
		super(id);
		this.tpl = tpl.toArray(new ITemplateParameter[tpl.size()]);
		if (decldefs != null) {
			this.declDefs = decldefs.toArray(new IDeclaration[decldefs.size()]);
		}
	}
	
	public ISimpleName getName() {
		return ident;
	}
	
	public ITemplateParameter[] getTemplateParameters() {
		return tpl;
	}
	
	public IDeclaration[] getDeclarationDefinitions() {
		if (declDefs == null) return ASTNode.NO_DECLARATIONS;
		return declDefs;
	}
	
	public int getElementType() {
		return TEMPLATE_DECLARATION;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
			acceptChildren(visitor, tpl);
			acceptChildren(visitor, declDefs);
		}
		visitor.endVisit(this);
	}

}
