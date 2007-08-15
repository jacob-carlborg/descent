package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class StructInitializer extends Initializer {
	
	public List<IdentifierExp> field;
	public List<Initializer> value;
	
	public StructInitializer(Loc loc) {
		super(loc);
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, field);
			TreeVisitor.acceptChildren(visitor, value);
		}
		visitor.endVisit(this);
	}
	
	
	public void addInit(IdentifierExp field, Initializer value) {
		if (this.field == null) {
			this.field = new ArrayList<IdentifierExp>();
			this.value = new ArrayList<Initializer>();
		}
		this.field.add(field);
		this.value.add(value);
	}
	
	@Override
	public int getNodeType() {
		return STRUCT_INITIALIZER;
	}

}
