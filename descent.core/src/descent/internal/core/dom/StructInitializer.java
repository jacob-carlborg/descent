package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IInitializer;
import descent.core.dom.ISimpleName;
import descent.core.dom.IStructInitializer;

public class StructInitializer extends Initializer implements IStructInitializer {
	
	private List<Identifier> ids;
	private List<Initializer> values;

	public StructInitializer() {
		this.ids = new ArrayList<Identifier>();
		this.values = new ArrayList<Initializer>();
	}

	public void addInit(Identifier id, Initializer value) {
		this.ids.add(id);
		this.values.add(value);
	}
	
	public ISimpleName[] getNames() {
		return ids.toArray(new ISimpleName[ids.size()]);
	}
	
	public IInitializer[] getValues() {
		return values.toArray(new IInitializer[values.size()]);
	}
	
	public int getElementType() {
		return STRUCT_INITIALIZER;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) { 
			acceptChildren(visitor, ids);
			acceptChildren(visitor, values);
		}
		visitor.endVisit(this);
	}

}
