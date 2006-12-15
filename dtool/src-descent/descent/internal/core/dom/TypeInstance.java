package descent.internal.core.dom;

import descent.core.dom.IType;
import descent.core.domX.ASTVisitor;
import descent.core.domX.AbstractElement;

public class TypeInstance extends TypeQualified implements IType {
	
	public TemplateInstance tempinst;
	public QualifiedName qName;

	public TypeInstance(TemplateInstance tempinst) {
		super(TY.Tinstance);
		this.tempinst = tempinst;
	}
	
	public int getElementType() {
		return ElementTypes.TEMPLATE_INSTANCE_TYPE;
	}
	
	public AbstractElement[] getTemplateArguments() {
		return tempinst.getTemplateArguments();
	}
	
	public String getShortName() {
		return getName().toString();
	}
	
	@Override
	public QualifiedName getName() {
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
