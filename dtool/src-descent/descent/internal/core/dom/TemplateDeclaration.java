package descent.internal.core.dom;

import java.util.Arrays;
import java.util.List;

import descent.core.dom.IDeclaration;
import descent.core.dom.IName;
import descent.core.dom.ITemplateDeclaration;
import descent.core.domX.ASTVisitor;
import descent.core.domX.AbstractElement;

public class TemplateDeclaration extends Declaration implements ITemplateDeclaration {
	
	private TemplateParameter[] tpl;
	private IDeclaration[] declDefs;

	public TemplateDeclaration(Identifier id, List<TemplateParameter> tpl, List<IDeclaration> decldefs) {
		super(id);
		this.tpl = tpl.toArray(new TemplateParameter[tpl.size()]);
		if (decldefs != null) {
			this.declDefs = decldefs.toArray(new IDeclaration[decldefs.size()]);
		}
	}
	
	public IName getName() {
		return ident;
	}
	
	public TemplateParameter[] getTemplateParameters() {
		return tpl;
	}
	
	public IDeclaration[] getDeclarationDefinitions() {
		if (declDefs == null) return AbstractElement.NO_DECLARATIONS;
		return declDefs;
	}
	
	public int getElementType() {
		return ElementTypes.TEMPLATE_DECLARATION;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			
			acceptChild(visitor, ident);
			acceptChildren(visitor, tpl);
			acceptChildren(visitor, Arrays.asList(declDefs));
		}
		
		visitor.endVisit(this);
	}

}
