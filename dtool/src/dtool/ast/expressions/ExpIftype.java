package dtool.ast.expressions;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.IftypeExp;
import descent.internal.compiler.parser.TOK;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.references.Reference;
import dtool.ast.references.ReferenceConverter;

public class ExpIftype extends Expression {

	public Reference arg;
	public TOK tok;
	public Reference specType;
	
	public ExpIftype(IftypeExp node) {
		convertNode(node);
		//Assert.isNull(node.id); //Can occur in error in illegal D code
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
