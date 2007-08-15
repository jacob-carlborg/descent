package dtool.dom.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.StructInitializer;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.RefIdentifier;

public class InitializerStruct extends Initializer {

	public RefIdentifier[] indexes;
	public Initializer[] values;

	public InitializerStruct(StructInitializer elem) {
		convertNode(elem);
		//TODO
		this.indexes = DescentASTConverter.convertMany(elem.field.toArray(), new RefIdentifier[elem.field.size()]);
		this.values = Initializer.convertMany(elem.value);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, indexes);
			TreeVisitor.acceptChildren(visitor, values);
		}
		visitor.endVisit(this);
	}

}
