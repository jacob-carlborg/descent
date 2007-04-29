package descent.internal.core.dom;

import java.util.List;

import util.StringUtil;
import util.tree.TreeVisitor;
import descent.core.domX.AbstractElement;
import descent.core.domX.IASTVisitor;
import dtool.dom.ast.ASTNode;

public class TemplateInstance extends Identifier {

	public List<ASTNode> tiargs;

	public TemplateInstance(Identifier id) {
		super(id.string, id.value);
		this.startPos = id.startPos;
		this.length = id.length;
	}
	
	public AbstractElement[] getTemplateArguments() {
		if (tiargs == null) {
			return AbstractElement.NO_ELEMENTS;
		} else {
			return tiargs.toArray(new AbstractElement[tiargs.size()]);
		}
	}

	@Override
	public String toString() {
		return string + "!(" + StringUtil.collToString(tiargs, ",") + ")";
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, tiargs);
		}
		visitor.endVisit(this);
	}
	
}
