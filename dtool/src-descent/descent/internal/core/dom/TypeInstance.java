package descent.internal.core.dom;

import descent.core.dom.IElement;
import descent.core.dom.IQualifiedName;
import descent.core.dom.ITemplateInstanceType;
import descent.core.domX.ASTVisitor;

public class TypeInstance extends TypeQualified implements ITemplateInstanceType {
	
	public TemplateInstance tempinst;
	public QualifiedName qName;

	public TypeInstance(TemplateInstance tempinst) {
		super(TY.Tinstance);
		this.tempinst = tempinst;
	}
	
	public int getElementType() {
		return TEMPLATE_INSTANCE_TYPE;
	}
	
	public IElement[] getTemplateArguments() {
		return tempinst.getTemplateArguments();
	}
	
	public String getShortName() {
		return getName().toString();
	}
	
	@Override
	public IQualifiedName getName() {
		if (qName == null) {
			qName = new QualifiedName(tempinst);
		}
		return qName;
	}
	
	@Override
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, qName);
			acceptChildren(visitor, tempinst.getTemplateArguments());
		}
		visitor.endVisit(this);
	}

}
