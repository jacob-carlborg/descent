package dtool.dom.declarations;

import java.util.ArrayList;
import java.util.List;

import util.tree.TreeVisitor;
import descent.internal.core.dom.Identifier;
import descent.internal.core.dom.ModuleDeclaration;
import descent.internal.core.dom.TOK;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.EntitySingle;
import dtool.model.IScope;

/**
 * D Module
 */
public class Module extends DefUnit implements IScope {

	public static class DeclarationModule extends ASTNeoNode {

		public EntitySingle.Identifier[] packages;
		public Symbol moduleName;

		public DeclarationModule(ModuleDeclaration md) {
			setSourceRange(md);
			this.moduleName = new DefUnit.Symbol(md.ident); 
		}

		public void accept0(IASTNeoVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, packages);
				TreeVisitor.acceptChild(visitor, moduleName);
			}
			visitor.endVisit(this);
		}
	}
	
	public DeclarationModule md;
	public ASTNode[] members; //FIXME
	

	public Module(descent.internal.core.dom.Module elem) {
		super(new Identifier(null, TOK.TOKidentifier));
		setSourceRange(elem);
		//newelem.name = (elem.ident != null) ? elem.ident.string : null; 
		if(elem.md != null){
			// If there is md there is this	elem.ident
			this.defname = new DefUnit.Symbol(elem.ident); 
			this.md = new DeclarationModule(elem.md);

			if(elem.md.packages != null) {
				this.md.packages = new EntitySingle.Identifier[elem.md.packages.size()];
				DescentASTConverter.convertMany(this.md.packages, elem.md.packages.toArray());
			} else {
				this.md.packages = new EntitySingle.Identifier[0];
			}
		}
		this.members = DescentASTConverter.convertMany(elem.getDeclarationDefinitions());
	}

	public EArcheType getArcheType() {
		return EArcheType.Module;
	}
	
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, md);
			TreeVisitor.acceptChildren(visitor, members);
		}
		visitor.endVisit(this);
	}

	public List<DefUnit> getDefUnits() {
		//TODO cache
		List<DefUnit> defunits = new ArrayList<DefUnit>();
		for(ASTNode elem: members) {
			if(elem instanceof DefUnit)
				defunits.add((DefUnit)elem);
		}
		return defunits;
	}

	@Override
	public IScope getScope() {
		return this;
	}

}
