package descent.internal.core.dom;

import descent.core.dom.IDElement;
import descent.core.dom.IQualifiedName;
import descent.core.dom.ITemplateInstanceType;

public class TypeInstance extends TypeQualified implements ITemplateInstanceType {
	
	public TemplateInstance tempinst;
	public QualifiedName qName;

	public TypeInstance(Loc loc, TemplateInstance tempinst) {
		super(TY.Tinstance, loc);
		this.tempinst = tempinst;
	}
	
	@Override
	public int getTypeType() {
		return TYPE_TEMPLATE_INSTANCE;
	}
	
	public IDElement[] getTemplateArguments() {
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

}
