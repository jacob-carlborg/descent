package dtool.ast.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.StructInitializer;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.references.RefIdentifier;
import dtool.descentadapter.DescentASTConverter;

public class InitializerStruct extends Initializer {

	public RefIdentifier[] indexes;
	public Initializer[] values;

	public InitializerStruct(StructInitializer elem) {
		convertNode(elem);
		this.indexes = new RefIdentifier[elem.field.size()];
		for(int i = 0; i < elem.field.size(); ++i) {
			IdentifierExp id = elem.field.get(i);
			ExpReference expref = (ExpReference) DescentASTConverter.convertElem(id);
			if(expref == null)
				this.indexes[i] = null;
			else
				this.indexes[i] = (RefIdentifier) expref.ref;
		}
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
