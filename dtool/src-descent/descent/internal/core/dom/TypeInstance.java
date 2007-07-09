package descent.internal.core.dom;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.dom.IType;
import descent.core.domX.IASTVisitor;
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
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, qName);
			TreeVisitor.acceptChildren(visitor, tempinst.getTemplateArguments());
		}
		visitor.endVisit(this);
	}

}
