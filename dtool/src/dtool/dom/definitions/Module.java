package dtool.dom.definitions;

import java.util.ArrayList;
import java.util.List;

import util.StringUtil;
import util.tree.TreeVisitor;
import descent.internal.core.dom.Identifier;
import descent.internal.core.dom.ModuleDeclaration;
import descent.internal.core.dom.TOK;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.base.EntIdentifier;
import dtool.dom.declarations.Declaration;
import dtool.model.IDeeCompilationUnit;
import dtool.model.IScope;

/**
 * D Module
 */
public class Module extends DefUnit implements IScope {

	public static class DeclarationModule extends ASTNeoNode {

		public EntIdentifier[] packages;
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
		
		@Override
		public String toString() {
			return StringUtil.collToString(packages, ".") + "." + moduleName;
		}
	}

	public IDeeCompilationUnit cunit;

	public DeclarationModule md;
	public ASTNode[] members;
	

	public Module(descent.internal.core.dom.Module elem) {
		convertNode(elem); // elem not a full formed Dsymbol
		setSourceRange(elem);
		convertIdentifier(new Identifier(null, TOK.TOKidentifier));
		//newelem.name = (elem.ident != null) ? elem.ident.string : null; 
		if(elem.md != null){
			// If there is md there is this	elem.ident
			this.defname = new DefUnit.Symbol(elem.ident); 
			this.md = new DeclarationModule(elem.md);

			if(elem.md.packages != null) {
				this.md.packages = new EntIdentifier[elem.md.packages.size()];
				DescentASTConverter.convertMany(elem.md.packages.toArray(),this.md.packages);
			} else {
				this.md.packages = new EntIdentifier[0];
			}
		}
		this.members = Declaration.convertMany(elem.getDeclarationDefinitions());
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
	public IScope getBindingScope() {
		return this;
	}
	
	public IScope getSuperScope() {
		return null;
	}

}
