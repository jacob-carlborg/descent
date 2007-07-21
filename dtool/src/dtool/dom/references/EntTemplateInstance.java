package dtool.dom.references;

import java.util.List;

import melnorme.miscutil.StringUtil;
import melnorme.miscutil.tree.TreeVisitor;

import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;

public class EntTemplateInstance extends EntitySingle {
	
	public List<ASTNeoNode> tiargs;
	
	public EntTemplateInstance() {
	}
	
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
	public DefUnit findTargetDefUnit() {
		// TODO Try to figure which homonym
		return super.findTargetDefUnit();
	}
	
	public String toString() {
		return name + "!("+StringUtil.collToString(tiargs,",")+ ")";
	}
	
}
