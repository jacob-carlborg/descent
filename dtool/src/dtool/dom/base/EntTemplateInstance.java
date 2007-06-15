package dtool.dom.base;

import java.util.List;

import util.StringUtil;
import util.tree.TreeVisitor;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;

public class EntTemplateInstance extends EntitySingle {
	
	public List<ASTNeoNode> tiargs;
	
	public EntTemplateInstance(descent.internal.core.dom.TemplateInstance elem) {
		setSourceRange(elem);
		this.name = elem.string;
		this.tiargs = DescentASTConverter.convertManyL(elem.tiargs, tiargs);
	}

	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			//TreeVisitor.acceptChildren(visitor, name);
			TreeVisitor.acceptChildren(visitor, tiargs);
		}
		visitor.endVisit(this);
	}

	@Override
	public DefUnit getTargetDefUnit() {
		// TODO Try to figure which homonym
		return super.getTargetDefUnit();
	}
	
	public String toString() {
		return name + "!("+StringUtil.collToString(tiargs,",")+ ")";
	}
	
}
