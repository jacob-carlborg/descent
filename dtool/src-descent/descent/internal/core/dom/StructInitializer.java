package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import util.tree.TreeVisitor;

import descent.core.dom.IInitializer;
import descent.core.dom.IName;
import descent.core.domX.ASTVisitor;

public class StructInitializer extends Initializer {
	
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
	
	public IName[] getNames() {
		return ids.toArray(new IName[ids.size()]);
	}
	
	public IInitializer[] getValues() {
		return values.toArray(new IInitializer[values.size()]);
	}
	
	public int getElementType() {
		return ElementTypes.STRUCT_INITIALIZER;
	}
	
	public void accept0(ASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) { 
			TreeVisitor.acceptChildren(visitor, ids);
			TreeVisitor.acceptChildren(visitor, values);
		}
		visitor.endVisit(this);
	}

}
