package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;


import descent.core.dom.IModifiersContainer;
import descent.core.dom.IName;
import descent.core.domX.IASTVisitor;

public class Import extends Dsymbol implements IModifiersContainer {
	
	public List<Identifier> packages;
	public List<SelectiveImport> selectiveImports;
	public QualifiedName qName;
	public Identifier alias;

	public Import(List<Identifier> packages, Identifier id, Identifier aliasid, boolean isstatic) {
		this.ident = id;
		this.packages = packages;
		this.qName = new QualifiedName(packages, id);
		this.alias = aliasid;
	}

	public void addAlias(Identifier name, Identifier alias) {
		if (selectiveImports == null) {
			selectiveImports = new ArrayList<SelectiveImport>();
		}
		selectiveImports.add(new SelectiveImport(name, alias));
	}

	public QualifiedName getQualifiedName() {
		return qName;
	}
	
	public IName getAlias() {
		return alias;
	}
	
	public SelectiveImport[] getSelectiveImports() {
		if (selectiveImports == null) return new SelectiveImport[0];
		return selectiveImports.toArray(new SelectiveImport[selectiveImports.size()]);
	}
	
	public int getElementType() {
		return ElementTypes.IMPORT;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, alias);
			TreeVisitor.acceptChild(visitor, qName);
			TreeVisitor.acceptChildren(visitor, selectiveImports);
		}
		visitor.endVisit(this);
	}

}
