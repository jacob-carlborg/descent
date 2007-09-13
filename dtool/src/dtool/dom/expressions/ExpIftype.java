package dtool.dom.expressions;

import melnorme.miscutil.Assert;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.IftypeExp;
import descent.internal.compiler.parser.TOK;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.references.Reference;
import dtool.dom.references.ReferenceConverter;

public class ExpIftype extends Expression {

	public Reference arg;
	public TOK tok;
	public Reference specType;
	
	public ExpIftype(IftypeExp node) {
		convertNode(node);
		Assert.isNull(node.id);
		this.tok = node.tok;
		this.arg = ReferenceConverter.convertType(node.targ);
		this.specType = ReferenceConverter.convertType(node.tspec);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, arg);
			TreeVisitor.acceptChildren(visitor, specType);
		}
		visitor.endVisit(this);
	}

}
