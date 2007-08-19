package dtool.dom.references;

import java.util.Collection;
import java.util.List;

import descent.internal.compiler.parser.IdentifierExp;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.definitions.DefUnit;
import dtool.dom.definitions.Module;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.EntityResolver;
import dtool.refmodel.NodeUtil;

/** 
 * A module reference (in import declarations only).
 */
public class RefModule extends Reference {
	
	//public String packageName;
	public String[] packages;
	public String module;

	public RefModule(List<IdentifierExp> packages, IdentifierExp id) {
		this.module = new String(id.ident);
		if(packages == null) {
			this.packages = new String[0];
			setSourceRange(id);
		} else {
			this.packages = new String[packages.size()];
			for (int i = 0; i < packages.size(); i++) {
				this.packages[i] = new String(packages.get(i).ident);
			}
			int startPos = packages.get(0).getStartPos();
			setSourceRange(startPos, id.getEndPos() - startPos);
		}
	}

	public Collection<DefUnit> findTargetDefUnits(boolean findOneOnly) {
		Module originMod = NodeUtil.getParentModule(this);
		Module targetMod = EntityResolver.findModule(originMod, packages, module);
		return DefUnitSearch.wrapResult(targetMod);
	}
	

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			//TreeVisitor.acceptChildren(visitor, root);
			//TreeVisitor.acceptChildren(visitor, subent);
		}
		visitor.endVisit(this);	
	}

}
