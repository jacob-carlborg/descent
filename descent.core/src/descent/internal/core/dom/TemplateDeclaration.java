package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IDElement;
import descent.core.dom.IDElementVisitor;
import descent.core.dom.IName;
import descent.core.dom.ITemplateDeclaration;
import descent.core.dom.ITemplateParameter;

public class TemplateDeclaration extends Dsymbol implements ITemplateDeclaration {
	
	private ITemplateParameter[] tpl;
	private IDElement[] declDefs;

	public TemplateDeclaration(Loc loc, Identifier id, List<TemplateParameter> tpl, List<IDElement> decldefs) {
		this.ident = id;
		this.tpl = tpl.toArray(new ITemplateParameter[tpl.size()]);
		if (decldefs != null) {
			this.declDefs = decldefs.toArray(new IDElement[decldefs.size()]);
		}
	}
	
	public IName getName() {
		return ident;
	}
	
	public ITemplateParameter[] getTemplateParameters() {
		return tpl;
	}
	
	public IDElement[] getDeclarationDefinitions() {
		if (declDefs == null) return AbstractElement.NO_ELEMENTS;
		return declDefs;
	}
	
	public int getElementType() {
		return TEMPLATE_DECLARATION;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, ident);
			acceptChildren(visitor, tpl);
			acceptChildren(visitor, declDefs);
		}
		visitor.endVisit(this);
	}

}
