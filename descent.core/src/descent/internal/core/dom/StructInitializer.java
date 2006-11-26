package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IInitializer;
import descent.core.dom.IName;
import descent.core.dom.IStructInitializer;

public class StructInitializer extends Initializer implements IStructInitializer {
	
	private List<Identifier> ids;
	private List<Initializer> values;

	public StructInitializer(Loc loc) {
		this.ids = new ArrayList<Identifier>();
		this.values = new ArrayList<Initializer>();
	}

	public void addInit(Identifier id, Initializer value) {
		this.ids.add(id);
		this.values.add(value);
	}
	
	public IName[] getNames() {
		return ids.toArray(new IName[ids.size()]);
	}
	
	public IInitializer[] getValues() {
		return values.toArray(new IInitializer[values.size()]);
	}
	
	public int getElementType() {
		return STRUCT_INITIALIZER;
	}
	
	public void accept(IDElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) { 
			acceptChildren(visitor, ids);
			acceptChildren(visitor, values);
		}
		visitor.endVisit(this);
	}

}
